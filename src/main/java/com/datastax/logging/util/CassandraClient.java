package com.datastax.logging.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.security.auth.login.LoginException;

import org.apache.cassandra.thrift.*;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.*;


/**
 * This wraps the underlying Cassandra thrift client and attempts to handle
 * disconnect, unavailable, timeout errors gracefully.
 *
 * On disconnect, if it cannot reconnect to the same host then it will use a
 * different host from the ring. After a successful connecting, the ring will be
 * refreshed.
 *
 * This incorporates the CircuitBreaker pattern so not to overwhelm the network
 * with reconnect attempts.
 *
 */
public class CassandraClient implements java.lang.reflect.InvocationHandler
{

    private static final Logger logger  = Logger.getLogger(CassandraClient.class);
    
    /**
     * The rpc port of the cassandra servers
     */
    private int                 port;

    /**
     * The last successfully connected server.
     */
    private String				lastUsedHost;
    /**
     * Last time the ring was checked.
     */
    private long                lastPoolCheck;
    /**
     * A list holds all servers from the ring.
     */
    private List<TokenRange>    ring;

    /**
     * Cassandra thrift client.
     */
    private Cassandra.Client    client;

    /**
     * ITransportFactory used to obtain new thrift connections
     */
    private ITransportFactory transportFactory;

    /**
     * Current transport in use, underlying the thrift client.
     */
    private TTransport transport;
    
    /**
     * The key space to get the ring information from.
     */
    private String              ringKs;
    private CircuitBreaker      breaker = new CircuitBreaker(1, 1);

    /**
     * Maximum number of attempts when connection is lost.
     */
    private final int maxAttempts = 10;

    /**
     * Construct a (somewhat) robust client which will automatically attempt to
     * reconnect to an alternative node if its current connection becomes 
     * unavailable.
     *
     * @param host cassandra host
     * @param port cassandra port
     * @param transportFactory used to open new thrift connections to the cassandra cluster
     * @return a Cassandra Client Interface
     * @throws IOException
     */
    public static Cassandra.Iface openConnection(String host, int port, ITransportFactory transportFactory)
            throws IOException
    {
        return (Cassandra.Iface) java.lang.reflect.Proxy.newProxyInstance(Cassandra.Client.class.getClassLoader(),
                                                                          Cassandra.Client.class.getInterfaces(), 
                                                                          new CassandraClient(host, port, transportFactory));
    }

    private CassandraClient(String host, int port, ITransportFactory transportFactory)
    throws IOException
    {
        this.lastUsedHost = host;
        this.port = port;
        this.lastPoolCheck = 0;
        this.transportFactory = transportFactory;
        initialize();
    }

    /**
     * Initialize the cassandra connection.
     *
     * @throws IOException
     */
    private void initialize() throws IOException
    {
        int attempt = 0;
        while (attempt++ < maxAttempts)
        {
            attemptReconnect();
            if (client != null) {
                break;
            } else {
                // sleep and try again
                try
                {
                    Thread.sleep(1050);
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        }

        if(client == null)
            throw new IOException("Error connecting to node " + lastUsedHost);

        //Find the first keyspace that's not system and assign it to the lastly used keyspace.
        try
        {
            List<KsDef> allKs = client.describe_keyspaces();

            if (allKs.isEmpty() || (allKs.size() == 1 && allKs.get(0).name.equalsIgnoreCase("system"))) {
                allKs.add(createTmpKs());
            }

            for(KsDef ks : allKs)
            {
                if(!ks.name.equalsIgnoreCase("system")) {
                    ringKs = ks.name;
                    break;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }

        checkRing();
    }
    
    /**
     * Create connection to a given host.
     *
     * @param host cassandra host
     * @return cassandra thrift client
     * @throws IOException error
     */
    private Cassandra.Client createConnection(String host) throws IOException, LoginException
    {

        Cassandra.Client client = getConnection(host, port);
        //connect to last known keyspace
        if(ringKs != null)
        {
            try
            {
                client.set_keyspace(ringKs);
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }
        }

        return client;
    }
    
    private Cassandra.Client getConnection(String host, int rpcport) throws IOException, LoginException
    {
        try
        {
            transport =  transportFactory.openTransport(host, port);
            return new Cassandra.Client(new TBinaryProtocol(transport));
        }
        catch (Exception e)
        {
            throw new IOException("Unable to connect to server: " + host + ":" + rpcport, e);
        }
    }


    

    /**
     * Create a temporary keyspace. This will only be called when there is no keyspace except system defined on (new cluster).
     * However we need a keyspace to call describe_ring to get all servers from the ring.
     *
     * @return the temporary keyspace
     * @throws InvalidRequestException error
     * @throws TException error
     * @throws InterruptedException error
     */
    private KsDef createTmpKs() throws InvalidRequestException, TException, InterruptedException, SchemaDisagreementException
    {
        KsDef tmpKs = new KsDef("proxy_client_ks", "org.apache.cassandra.locator.SimpleStrategy", Collections.<CfDef>emptyList());
        tmpKs.putToStrategy_options("replication_factor", "1");

        client.system_add_keyspace(tmpKs);

        return tmpKs;
    }

    /**
     * Refresh the list of servers in the ring.
     *
     * @throws IOException
     */
    private void checkRing() throws IOException
    {
        if (client == null) {
            breaker.failure();
            return;
        }

        long now = System.currentTimeMillis();

        if ((now - lastPoolCheck) > 60 * 1000) {
            try {
                if (breaker.allow()) {
                    ring = client.describe_ring(ringKs);
                    lastPoolCheck = now;
                    breaker.success();
                }
            } catch (InvalidRequestException e) {
                throw new IOException(e);
            } catch (TException e) {
                breaker.failure();
                attemptReconnect();
            }
        }
    }

    /**
     * Choose next server that is different from the last used host
     * to try to connect.
     *
     * @param host the last server tried
     */
    private String getNextServer(String host) {
        
        // grab all the endpoint addresses 
        SortedSet<String> allEndpoints = new TreeSet<String>();
        for (TokenRange range : ring)
        {
            allEndpoints.addAll(range.getEndpoints());
        }
        List<String> endpoints = new ArrayList(allEndpoints);
        Iterator<String> endpointIter = endpoints.iterator();
        
        // if the last used host is still in the ring, 
        // position the iterator at it
        if (endpoints.contains(host))
        {
            while (true)
            {
                if (endpointIter.next().equals(host))
                {
                    break;
                }
            }
        }
        // at this point either the last used host is
        // no longer part of the ring in which case we
        // can just pick the first endpoint, or we positioned
        // the iterator at its position in the list. 
        // So now we just return the next endpoint in the 
        // list, or we've reached the last in the list, 
        // in which case we wrap around and return the first
        // enpoint.
        if (endpointIter.hasNext())
        {
            return endpointIter.next();
        }
        else
        {
            return endpoints.get(0);
        }
    }
    
    /**
     * Attempt to connect to the next available server.
     */
    private void attemptReconnect()
    {
        // first try to connect to the same host as before
        if (ring == null || ring.size() == 0)
        {
            try
            {
                client = createConnection(lastUsedHost);
                breaker.success();
                if(logger.isDebugEnabled())
                    logger.debug("Connected to cassandra at " + lastUsedHost + ":" + port);
                return;
            }
            catch (IOException e)
            {
                logger.warn("Connection failed to Cassandra node: " + lastUsedHost + ":" + port + " " + e.getMessage());
            }
            catch (LoginException e)
            {
                logger.warn("Authentication failure connecting to Cassandra node: " + lastUsedHost + ":" + port + " " + e.getMessage());
            }
        }

    	//If the ring does't contain any server, fails the attempt.
        if (ring == null || ring.size() == 0)
        {
            logger.warn("No cassandra ring information found, no other nodes to connect to");
            client = null;
            return;
        }

        // only one node (myself)
        if (ring.size() == 1)
        {
            logger.warn("No other cassandra nodes in this ring to connect to");
            client = null;
            return;
        }

        String endpoint = getNextServer(lastUsedHost);

        try
        {
            client = createConnection(endpoint);
            lastUsedHost = endpoint; //Assign the last successfully connected server.
            breaker.success();
            checkRing(); //Refresh the servers in the ring.
            logger.info("Connected to cassandra at " + endpoint + ":" + port);
        }
        catch (IOException e)
        {
            logger.warn("Failed connecting to a different cassandra node in this ring: " + endpoint + ":" + port);
            client = null;
        }
        catch (LoginException e)
        {
            logger.warn("Authentication failure connecting to a different cassandra node in this ring: " + endpoint + ":" + port);
        }
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        Object result = null;

        int tries = 0;

        // incase this is the first time
        if (ring == null)
            checkRing();

        while (result == null && tries++ < maxAttempts)
        {
            if (client == null)
            {
                // don't even try if client isn't connected
                breaker.failure();
            }

            try
            {
                if (breaker.allow())
                {
                    result = m.invoke(client, args);

                    if(m.getName().equalsIgnoreCase("set_keyspace") && args.length == 1)
                    {
                    	//Keep last known keyspace when set_keyspace is successfully invoked.
                        ringKs = (String)args[0];
                    }

                    breaker.success();
                    return result;
                }
                else
                {
                    while (!breaker.allow())
                    {
                        Thread.sleep(1050); // sleep and try again
                    }
                    attemptReconnect();

                    if(client != null)
                    {
                    	//If able to connect to a server, decrease tries to try more times.
                        tries--;
                    }
                }
            }
            catch (InvocationTargetException e)
            {

                if (e.getTargetException() instanceof UnavailableException ||
                        e.getTargetException() instanceof TimedOutException ||
                        e.getTargetException() instanceof TTransportException)
                {

                    breaker.failure();

                    // rethrow on last try
                    if (tries >= maxAttempts)
                        throw e.getCause();
                }
                else
                {
                    throw e.getCause();
                }
            }
            catch (Exception e)
            {
                logger.error("Error invoking a method via proxy: ", e);
                throw new RuntimeException(e);
            }

        }

        throw new UnavailableException();
    }

}