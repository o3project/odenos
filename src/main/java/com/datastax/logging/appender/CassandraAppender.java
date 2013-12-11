package com.datastax.logging.appender;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.datastax.logging.util.CassandraClient;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.ITransportFactory;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TFramedTransportFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Main class that uses Cassandra to store log entries into.
 * 
 */
public class CassandraAppender extends AppenderSkeleton
{
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    // CF column names
    public static final String HOST_IP = "host_ip";
    public static final String HOST_NAME = "host_name";
    public static final String APP_NAME  = "app_name";
    public static final String LOGGER_NAME = "logger_name";
    public static final String LEVEL = "level";
    public static final String CLASS_NAME = "class_name";
    public static final String FILE_NAME = "file_name";
    public static final String LINE_NUMBER = "line_number";
    public static final String METHOD_NAME = "method_name";
    public static final String MESSAGE = "message";
    public static final String NDC = "ndc";
    public static final String APP_START_TIME = "app_start_time";
    public static final String THREAD_NAME = "thread_name";
    public static final String THROWABLE_STR = "throwable_str_rep";
    public static final String TIMESTAMP = "log_timestamp";


    /**
     * Thrift transport options. The map of transport options may be specified
     * via the transportOptions appender configuration, using JSON notation.
     * TRANSPORT_FACTORY_CLASS_KEY is a special property which, if present
     * in that JSON map specifies the classname of ITransportFactory
     * implementation to use. Any other entries in transportOptions will be
     * passed onto the implementation as options if it declares that it
     * supports them. If no transportOptions value is present in log4j config,
     * or if the JSON map does not contain the TRANSPORT_FACTORY_CLASS_KEY
     * entry, a standard framed transport factory is used.
     */
    public static final String TRANSPORT_FACTORY_CLASS_KEY = "thrift.transport.factory";
    private static Map<String, String> transportOptions = new HashMap<String, String>();
    private static ITransportFactory transportFactory;

    /**
     * Keyspace name. Default: "Logging".
     */
    private String keyspaceName = "Logging";
    private String columnFamily = "log_entries";
    private String appName = "default";
    
    private String placementStrategy = "org.apache.cassandra.locator.SimpleStrategy";
    private Map<String, String> strategyOptions = new HashMap<String, String>();
    // required by SimpleStrategy
    private int replicationFactor = 1;

    private ConsistencyLevel consistencyLevelWrite = ConsistencyLevel.ONE;

    private int maxBufferedRows = 1; // buffering is turned off by default

    private Map<ByteBuffer, Map<String, List<Mutation>>> rowBuffer;

    private AtomicBoolean clientInitialized = new AtomicBoolean(false);
    private Cassandra.Iface client;
    
    private static final String ip = getIP();
    private static final String hostname = getHostName();

    /**
     * Cassandra comma separated hosts.
     */
    private String hosts = "localhost";

    /**
     * Cassandra port.
     */
    private int port = 9160;

    public CassandraAppender()
    {
        LogLog.debug("Creating CassandraAppender");
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        flush();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout()
    {
        return false;
    }
    
    /**
     * Called once all the options have been set. Starts listening for clients on the specified socket.
     */
    public void activateOptions()
    {
        reset();
    }
    
    private synchronized void initClient()
    {
        
        // another thread has already initialized the client, nothing to do
        if (clientInitialized.get())
        {
            return;
        }

        // Just while we initialise the client, we must temporarily
        // disable all logging or else we get into an infinite loop 
        Level globalThreshold = LogManager.getLoggerRepository().getThreshold();
        LogManager.getLoggerRepository().setThreshold(Level.OFF);
        
        try
        {
            try
            {
                initTransportFactory();
            }
            catch (Exception e)
            {
                LogLog.error("Can't initialize Thrift transport factory", e);
                errorHandler.error("Can't initialize Thrift transport factory: " + e);
            }

            try
            {
                client = CassandraClient.openConnection(hosts, port, transportFactory);
            }
            catch (Exception e)
            {
                LogLog.error("Can't initialize cassandra connections", e);
                errorHandler.error("Can't initialize cassandra connections: " + e);
            }
    
            try
            {
                setupSchema();
            }
            catch (Exception e)
            {
                LogLog.error("Error setting up cassandra logging schema", e);
                errorHandler.error("Error setting up cassandra logging schema: " + e);
            }
    
            try
            {
                client.set_keyspace(keyspaceName);
            }
            catch (Exception e)
            {
                LogLog.error("Error setting keyspace", e);
                errorHandler.error("Error setting keyspace: " + e);
            }
        }
        finally
        {
            // make sure we re-enable logging, even if we errored during client setup
            LogManager.getLoggerRepository().setThreshold(globalThreshold);
        }        
        
        clientInitialized.set(true);
    }

    private void initTransportFactory()
    throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        LogLog.debug("Initializing thrift transport factory");
        if (transportOptions.containsKey(TRANSPORT_FACTORY_CLASS_KEY))
        {
            LogLog.debug("Custom transport factory specified");
            Class clazz = Class.forName(transportOptions.get(TRANSPORT_FACTORY_CLASS_KEY));
            if (ITransportFactory.class.isAssignableFrom(clazz))
            {
                transportFactory = (ITransportFactory)clazz.newInstance();
                Map<String, String> supportedOptions = new HashMap<String, String>();
                for (Map.Entry<String, String> option : transportOptions.entrySet())
                {
                    LogLog.debug(option.getKey());
                    if (transportFactory.supportedOptions().contains(option.getKey()))
                    {
                        LogLog.debug("is a supported option");
                        supportedOptions.put(option.getKey(), option.getValue());
                    }
                }
                transportFactory.setOptions(supportedOptions);
            }
        }
        else
        {
            LogLog.debug("No custom transport factory specified, defaulting to TFramedTransportFactory");
            transportFactory = new TFramedTransportFactory();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(LoggingEvent event)
    {
        // We have to defer initialization of the client because ITransportFactory
        // references some Hadoop classes which can't safely be
        // used until the logging infrastructure is fully set up. If we attempt to
        // initialize the client earlier, it causes NPE's from the constructor of
        // org.apache.hadoop.conf.Configuration 
        // The initClient method is synchronized and includes a double check of 
        // the client status, so we only do this once.
        if (! clientInitialized.get())
        {
            initClient();
        }
        
        ByteBuffer rowId = toByteBuffer(UUID.randomUUID());
        Map<String, List<Mutation>> mutMap = new HashMap<String, List<Mutation>>();
        mutMap.put(columnFamily, createMutationList(event));
        rowBuffer.put(rowId, mutMap);

        flushIfNecessary();
    }

    private void flushIfNecessary()
    {
        if (rowBuffer.size() >= maxBufferedRows)
            flush();
    }

    private void flush()
    {
        if (rowBuffer.size() > 0)
        {
            try
            {
                client.batch_mutate(rowBuffer, consistencyLevelWrite);
            }
            catch (Exception e)
            {
                errorHandler.error("Failed to persist in Cassandra", e, ErrorCode.FLUSH_FAILURE);
            }
            reset();
        }
    }

    private void reset()
    {
        rowBuffer = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
    }

    private List<Mutation> createMutationList(LoggingEvent event)
    {
        List<Mutation> mutList = new ArrayList<Mutation>();

        long colTs = System.currentTimeMillis() * 1000; // don't use log entry timestamp for column timestamp to avoid
                                                        // clock skew issues
        createMutation(mutList, APP_NAME, appName, colTs);
        createMutation(mutList, HOST_IP, ip, colTs);
        createMutation(mutList, HOST_NAME, hostname, colTs);
        createMutation(mutList, LOGGER_NAME, event.getLoggerName(), colTs);
        createMutation(mutList, LEVEL, event.getLevel().toString(), colTs);
        LocationInfo locInfo = event.getLocationInformation();
        if (locInfo != null)
        {
            createMutation(mutList, CLASS_NAME, locInfo.getClassName(), colTs);
            createMutation(mutList, FILE_NAME, locInfo.getFileName(), colTs);
            createMutation(mutList, LINE_NUMBER, locInfo.getLineNumber(), colTs);
            createMutation(mutList, METHOD_NAME, locInfo.getMethodName(), colTs);
        }
        createMutation(mutList, MESSAGE, event.getRenderedMessage(), colTs);
        createMutation(mutList, NDC, event.getNDC(), colTs);
        createMutation(mutList, APP_START_TIME, LoggingEvent.getStartTime(), colTs);
        createMutation(mutList, THREAD_NAME, event.getThreadName(), colTs);
        String[] throwableStrs = event.getThrowableStrRep();
        if (throwableStrs != null)
        {
            StringBuilder builder = new StringBuilder();
            for (String throwableStr : throwableStrs)
            {
                builder.append(throwableStr);
            }
            createMutation(mutList, THROWABLE_STR, builder.toString(), colTs);
        }
        createMutation(mutList, TIMESTAMP, event.getTimeStamp(), colTs);

        return mutList;
    }

    private void createMutation(List<Mutation> mutList, String column, long value, long ts)
    {
        createMutation(mutList, column, toByteBuffer(value), ts);
    }

    private void createMutation(List<Mutation> mutList, String column, String value, long ts)
    {
        if (value != null)
        {
            createMutation(mutList, column, toByteBuffer(value), ts);
        }
    }

    private void createMutation(List<Mutation> mutList, String column, ByteBuffer value, long ts)
    {
        Mutation mutation = new Mutation();
        Column col = new Column(toByteBuffer(column));
        col.setValue(value);
        col.setTimestamp(ts);
        ColumnOrSuperColumn cosc = new ColumnOrSuperColumn().setColumn(col);
        mutation.setColumn_or_supercolumn(cosc);
        mutList.add(mutation);
    }

    /**
     * Create Keyspace and CF if they do not exist.
     */
    private void setupSchema() throws IOException
    {

        KsDef ksDef = verifyKeyspace();
        if (ksDef == null)
        {
            // create both
            createKeyspaceAndColFam();
        }
        else
        {
            // keyspace exists, does the cf?

            if (!checkForCF(ksDef))
            {
                // create cf
                createColumnFamily();
            }
        }
    }

    public String getKeyspaceName()
    {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName)
    {
        this.keyspaceName = keyspaceName;
    }

    public String getHosts()
    {
        return hosts;
    }

    public void setHosts(String hosts)
    {
        this.hosts = hosts;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getColumnFamily()
    {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily)
    {
        this.columnFamily = columnFamily;
    }

    public String getPlacementStrategy()
    {
        return placementStrategy;
    }

    public void setPlacementStrategy(String strategy)
    {
        if (strategy == null)
            throw new IllegalArgumentException("placementStrategy can't be null");

        placementStrategy = unescape(strategy);
    }

    public String getStrategyOptions()
    {
        return strategyOptions.toString();
    }

    public void setStrategyOptions(String newOptions)
    {
        if (newOptions == null)
            throw new IllegalArgumentException("strategyOptions can't be null.");

        try
        {
            strategyOptions = jsonMapper.readValue(unescape(newOptions), strategyOptions.getClass());
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid JSON map: " + newOptions + ", error: " + e.getMessage());
        }
    }

    public int getReplicationFactor()
    {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor)
    {
        this.replicationFactor = replicationFactor;
    }

    public String getConsistencyLevelWrite()
    {
        return consistencyLevelWrite.toString();
    }

    public void setConsistencyLevelWrite(String consistencyLevelWrite)
    {
        try
        {
            this.consistencyLevelWrite = ConsistencyLevel.valueOf(unescape(consistencyLevelWrite));
        }
        catch (IllegalArgumentException e)
        {
            StringBuilder availableCLs = new StringBuilder();
            boolean first = true;
            for (ConsistencyLevel cl : ConsistencyLevel.values())
            {
                if (first)
                    first = false;
                else
                    availableCLs.append(", ");

                availableCLs.append(cl);
            }
            throw new IllegalArgumentException("Consistency level " + consistencyLevelWrite
                                               + " wasn't found. Available levels: "
                                               + availableCLs.toString() + ".");
        }
    }

    public int getMaxBufferedRows()
    {
        return maxBufferedRows;
    }

    public void setMaxBufferedRows(int maxBufferedRows)
    {
        this.maxBufferedRows = maxBufferedRows;
    }

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getTransportOptions()
    {
        return transportOptions.toString();
    }

    public void setTransportOptions(String newOptions)
    {
        if (newOptions == null)
            throw new IllegalArgumentException("transportOptions can't be null.");

        try
        {
            transportOptions = jsonMapper.readValue(unescape(newOptions), transportOptions.getClass());
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid JSON map: " + newOptions + ", error: " + e.getMessage());
        }
    }
        
    /*
     * Verify that the keyspace exists. Returns the KsDef if it does, else null.
     */
    private KsDef verifyKeyspace()
            throws IOException
    {

        KsDef ksDef = null;

        try
        {
            ksDef = client.describe_keyspace(keyspaceName);
            // it exists, fall through to return positive
        }
        catch (NotFoundException e)
        {
            // doesn't exist
        }
        catch (Exception e)
        {
            throw new IOException("Exception caught while trying to verify keyspace existance.", e);
        }

        return ksDef;
    }

    /*
     * Check for CF in the given KS definition.
     */
    private boolean checkForCF(KsDef ksDef)
    {
        boolean exists = false;

        for (CfDef cfDef : ksDef.getCf_defs())
        {
            if (cfDef.getName().equals(columnFamily))
            {
                exists = true;
                break;
            }
        }

        return exists;
    }

    private void createKeyspaceAndColFam()
            throws IOException
    {

        List<CfDef> cfDefList = new ArrayList<CfDef>();
        cfDefList.add(createCfDef());

        try
        {
            KsDef ksDef = new KsDef(keyspaceName, placementStrategy, cfDefList);

            if (placementStrategy.equals("org.apache.cassandra.locator.SimpleStrategy"))
            {
                if (!strategyOptions.containsKey("replication_factor"))
                    strategyOptions.put("replication_factor", Integer.toString(replicationFactor));
            }

            ksDef.setStrategy_options(strategyOptions);

            client.system_add_keyspace(ksDef);
            int magnitude = client.describe_ring(keyspaceName).size();
            Thread.sleep(1000 * magnitude);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    private void createColumnFamily()
            throws IOException
    {

        CfDef cfDef = createCfDef();
        try
        {
            // keyspace should have already been verified...
            client.set_keyspace(keyspaceName);
            client.system_add_column_family(cfDef);
            int magnitude = client.describe_ring(keyspaceName).size();
            Thread.sleep(1000 * magnitude);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    private CfDef createCfDef()
    {
        CfDef cfDef = new CfDef(keyspaceName, columnFamily);
        cfDef.setKey_validation_class("UUIDType");
        cfDef.setComparator_type("UTF8Type");
        cfDef.setDefault_validation_class("UTF8Type");

        addColumn(cfDef, APP_NAME, "UTF8Type");
        addColumn(cfDef, HOST_IP, "UTF8Type");
        addColumn(cfDef, HOST_NAME, "UTF8Type");
        addColumn(cfDef, LOGGER_NAME, "UTF8Type");
        addColumn(cfDef, LEVEL, "UTF8Type");
        addColumn(cfDef, CLASS_NAME, "UTF8Type");
        addColumn(cfDef, FILE_NAME, "UTF8Type");
        addColumn(cfDef, LINE_NUMBER, "UTF8Type");
        addColumn(cfDef, METHOD_NAME, "UTF8Type");
        addColumn(cfDef, MESSAGE, "UTF8Type");
        addColumn(cfDef, NDC, "UTF8Type");
        addColumn(cfDef, APP_START_TIME, "LongType");
        addColumn(cfDef, THREAD_NAME, "UTF8Type");
        addColumn(cfDef, THROWABLE_STR, "UTF8Type");
        addColumn(cfDef, TIMESTAMP, "LongType");

        return cfDef;
    }

    private CfDef addColumn(CfDef cfDef, String columnName, String validator)
    {
        ColumnDef colDef = new ColumnDef();
        colDef.setName(toByteBuffer(columnName));
        colDef.setValidation_class(validator);
        cfDef.addToColumn_metadata(colDef);

        return cfDef;
    }

    private static final Charset charset = Charset.forName("UTF-8");

    // Serialize a string
    public static ByteBuffer toByteBuffer(String s)
    {
        if (s == null)
        {
            return null;
        }

        return ByteBuffer.wrap(s.getBytes(charset));
    }

    // serialize a UUID
    public ByteBuffer toByteBuffer(UUID uuid)
    {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++)
        {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++)
        {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return ByteBuffer.wrap(buffer);
    }

    // serialize a long
    public ByteBuffer toByteBuffer(long longVal)
    {
        return ByteBuffer.allocate(8).putLong(0, longVal);
    }
    
    private static String getHostName()
    {
        String hostname = "unknown";
        
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch(Throwable t)
        {
            
        }
        return hostname;
    }
    
    private static String getIP()
    {
        String ip = "unknown";
        
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        }
        catch(Throwable t)
        {
            
        }
        return ip;
    }

    /**
     * Strips leading and trailing '"' characters
     * @param b - string to unescape
     * @return String - unexspaced string
     */
    private static String unescape(String b)
    {
        if (b.charAt(0) == '\"' && b.charAt(b.length() - 1) == '\"')
            b = b.substring(1, b.length() - 1);
        return b;
    }
}
