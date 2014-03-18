package com.datastax.logging.appender;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import com.google.common.base.Joiner;

/**
 * Main class that uses Cassandra to store log entries into.
 * 
 */
public class CassandraAppender extends AppenderSkeleton
{
    //Cassandra configuration
    private String hosts = "localhost";
    private int port = 9042; //for the binary protocol, 9160 is default for thrift
    private String username = "";
    private String password = "";
    private static final String ip = getIP();
    private static final String hostname = getHostName();

    //Keyspace/ColumnFamily information
    private String keyspaceName = "Logging";
	private String columnFamily = "log_entries";
	private String appName = "default";
    private String replication = "{ 'class' : 'SimpleStrategy', 'replication_factor' : 1 }";
    private ConsistencyLevel consistencyLevelWrite = ConsistencyLevel.ONE;

    // CF column names
    public static final String ID = "key";
    public static final String HOST_IP = "host_ip";
    public static final String HOST_NAME = "host_name";
    public static final String APP_NAME = "app_name";
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

    //session state
    private PreparedStatement statement;
    private volatile boolean initialized = false;
    private volatile boolean initializationFailed = false;
    private Cluster cluster;
    private Session session;

    public CassandraAppender()
    {
		LogLog.debug("Creating CassandraAppender");
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(LoggingEvent event)
    {
        // We have to defer initialization of the client because TTransportFactory
        // references some Hadoop classes which can't safely be used until the logging
        // infrastructure is fully set up. If we attempt to initialize the client
        // earlier, it causes NPE's from the constructor of org.apache.hadoop.conf.Configuration.
        if(!initialized)
            initClient();
        if(!initializationFailed)
            createAndExecuteQuery(event);
    }

    //Connect to cassandra, then setup the schema and preprocessed statement
    private synchronized void initClient()
    {
        //We should be able to go without an Atomic variable here.  There are two potential problems:
        // 1. Multiple threads read intialized=false and call init client.  However, the method is
        //    synchronized so only one will get the lock first, and the others will drop out here.
        // 2. One thread reads initialized=true before initClient finishes.  This also should not
        //    happen as the lock should include a memory barrier.
        if(initialized || initializationFailed)
            return;

		// Just while we initialise the client, we must temporarily
		// disable all logging or else we get into an infinite loop
		Level globalThreshold = LogManager.getLoggerRepository().getThreshold();
		LogManager.getLoggerRepository().setThreshold(Level.OFF);

		try
        {
            String[] contactPoints = hosts.split(",\\s*");

            cluster = Cluster.builder()
						  .addContactPoints(contactPoints)
						  .withPort(port)
						  .withCredentials(username, password)
                          .withLoadBalancingPolicy(new RoundRobinPolicy())
                          .build();

		    session = cluster.connect();
            setupSchema();
            setupStatement();
		}
        catch (Exception e)
        {
		    LogLog.error("Error ", e);
			errorHandler.error("Error setting up cassandra logging schema: " + e);

            //If the user misconfigures the port or something, don't keep failing.
            initializationFailed = true;
		}
        finally
        {
            //Always reenable logging
            LogManager.getLoggerRepository().setThreshold(globalThreshold);
            initialized = true;
		}
	}


    /**
     * Create Keyspace and CF if they do not exist.
     */
    private void setupSchema() throws IOException
    {
        //Create keyspace if necessary
        String ksQuery = String.format("CREATE KEYSPACE IF NOT EXISTS \"%s\" WITH REPLICATION = %s;",
                                       keyspaceName, replication);
        session.execute(ksQuery);

        //Create table if necessary
        String cfQuery =  String.format("CREATE TABLE IF NOT EXISTS \"%s\".\"%s\" (%s UUID PRIMARY KEY, " +
                                        "%s text, %s bigint, %s text, %s text, %s text, %s text, %s text," +
                                        "%s text, %s text, %s bigint, %s text, %s text, %s text, %s text," +
                                        "%s text);",
                                        keyspaceName, columnFamily, ID, APP_NAME, APP_START_TIME, CLASS_NAME,
                                        FILE_NAME, HOST_IP, HOST_NAME, LEVEL, LINE_NUMBER, METHOD_NAME,
                                        TIMESTAMP, LOGGER_NAME, MESSAGE, NDC, THREAD_NAME, THROWABLE_STR);
        session.execute(cfQuery);
    }

    /**
     * Setup and preprocess our insert query, so that we can just bind values and send them over the binary protocol
     */
    private void setupStatement()
    {
        //Preprocess our append statement
        String insertQuery = String.format("INSERT INTO \"%s\".\"%s\" " +
                                           "(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ",
                                           keyspaceName, columnFamily, ID, APP_NAME, HOST_IP, HOST_NAME, LOGGER_NAME,
                                           LEVEL, CLASS_NAME, FILE_NAME, LINE_NUMBER, METHOD_NAME, MESSAGE, NDC,
                                           APP_START_TIME, THREAD_NAME, THROWABLE_STR, TIMESTAMP);

        statement = session.prepare(insertQuery);
        statement.setConsistencyLevel(ConsistencyLevel.valueOf(consistencyLevelWrite.toString()));
    }

    /**
     * Send one logging event to Cassandra.  We just bind the new values into the preprocessed query
     * built by setupStatement
     */
    private void createAndExecuteQuery(LoggingEvent event)
    {
		BoundStatement bound = new BoundStatement(statement);

        // A primary key combination of timestamp/hostname/threadname should be unique as long as the thread names
        // are set, but would not be backwards compatible.  Do we care?
        bound.setUUID(0, UUID.randomUUID());

        bound.setString(1, appName);
        bound.setString(2, ip);
        bound.setString(3, hostname);
        bound.setString(4, event.getLoggerName());
        bound.setString(5, event.getLevel().toString());

        LocationInfo locInfo = event.getLocationInformation();
        if (locInfo != null) {
            bound.setString(6, locInfo.getClassName());
            bound.setString(7, locInfo.getFileName());
            bound.setString(8, locInfo.getLineNumber());
            bound.setString(9, locInfo.getMethodName());
        }

        bound.setString(10, event.getRenderedMessage());
        bound.setString(11, event.getNDC());
        bound.setLong(12, new Long(LoggingEvent.getStartTime()));
        bound.setString(13, event.getThreadName());

        String[] throwableStrs = event.getThrowableStrRep();
        if (throwableStrs != null)
            bound.setString(14, Joiner.on(", ").join(throwableStrs));

        bound.setLong(15, new Long(event.getTimeStamp()));
        session.execute(bound);
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        session.shutdown();  //closeAsync for 2.0.0-rc3 driver
        cluster.shutdown();
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
     * Called once all the options have been set. Starts listening for clients
     * on the specified socket.
     */
    public void activateOptions()
    {
        // reset();
    }

    //
    //Boilerplate from here on out
    //

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

	public String getUsername()
    {
		return username;
	}

	public void setUsername(String username)
    {
		this.username = username;
	}

	public String getPassword()
    {
		return password;
	}

	public void setPassword(String password)
    {
		this.password = password;
	}

	public String getColumnFamily()
    {
		return columnFamily;
	}

	public void setColumnFamily(String columnFamily)
    {
		this.columnFamily = columnFamily;
	}

	public String getReplication()
    {
		return replication;
	}

	public void setReplication(String strategy)
    {
		replication = unescape(strategy);
	}

	public String getConsistencyLevelWrite()
    {
		return consistencyLevelWrite.toString();
	}

	public void setConsistencyLevelWrite(String consistencyLevelWrite)
    {
		try {
			this.consistencyLevelWrite = ConsistencyLevel.valueOf(unescape(consistencyLevelWrite));
		}
        catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Consistency level " + consistencyLevelWrite
					+ " wasn't found. Available levels: " + Joiner.on(", ").join(ConsistencyLevel.values()));
		}
	}


    public String getAppName()
    {
		return appName;
	}

	public void setAppName(String appName)
    {
		this.appName = appName;
	}

	private static String getHostName()
    {
		String hostname = "unknown";

		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (Throwable t) {

		}
		return hostname;
	}

	private static String getIP()
    {
		String ip = "unknown";

		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (Throwable t) {

		}
		return ip;
	}

	/**
	 * Strips leading and trailing '"' characters
	 * 
	 * @param b
	 *            - string to unescape
	 * @return String - unexspaced string
	 */
	private static String unescape(String b)
    {
		if (b.charAt(0) == '\"' && b.charAt(b.length() - 1) == '\"')
			b = b.substring(1, b.length() - 1);
		return b;
	}
}