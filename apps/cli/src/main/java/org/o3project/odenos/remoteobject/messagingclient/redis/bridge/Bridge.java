package org.o3project.odenos.remoteobject.messagingclient.redis.bridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.o3project.odenos.core.component.Component;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.Config;
import org.o3project.odenos.remoteobject.messagingclient.ConfigBuilder;
import org.o3project.odenos.remoteobject.messagingclient.IMessageListener;
import org.o3project.odenos.remoteobject.messagingclient.IPubSubDriver;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.messagingclient.redis.PubSubDriverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pubsub bridge.
 * 
 * <pre>
 *       -- *@bridge ->       -- * -->
 *             <-- * --       <- *@bridge --
 *         +-----------[Bridge]-----------+
 *         |                              |
 * [pubsub server A]              [pubsub server B]
 * </pre>
 *
 * <p>
 * To enable this bridge, MODE.PUBSUB_BRIDGED must be set by 
 * {@link org.o3project.odenos.remoteobject.messagingclient.ConfigBuilder}.
 *
 * <p>
 * TODO: systemmanager is not visible from a component manager on the
 * opposite side.
 */
public class Bridge extends Component {

  private static final Logger log = LoggerFactory.getLogger(Bridge.class);

  // Management-plane
  MessageDispatcher dispatcher;

  // Two Redis servers
  // TODO: parameter config.
  private static final int port = 6379;

  // Redis drivers
  private Map<String, IPubSubDriver> drivers = new HashMap<>();

  // PSUBSCRIBE patterns
  private Map<String, HashSet<String>> patterns = new ConcurrentHashMap<>();

  // Request parser
  protected final RequestParser<IActionCallback> parser;

  /**
   * Constructor.
   * 
   * @param objectId Redis bridge ID
   * @param dispatcher MessageDispatcher for management
   * @param hostA Redis server A's host
   * @param portA Redis server A's port
   * @param hostB Redis server B's host
   * @param portB Redis server B's port
   * @throws Exception 
   */
  public Bridge(String objectId, MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    this.dispatcher = dispatcher;
    parser = createParser();
  }

  private static final String description = "Redis bridge";

  @SuppressWarnings("static-access")
  @Override
  protected final String getDescription() {
    return this.description;
  }

  private void addDriver(String host, int port) {
    Config config;
    patterns.put(host, new HashSet<String>());
    if (patterns.size() == 2) {
      Listener listenerA = null;
      IPubSubDriver driverA = null;
      IPubSubDriver driverB = null;
      for (String driverHost : patterns.keySet()) {
        if (listenerA == null) {
          listenerA = new Listener();
          config = new ConfigBuilder()
                   .setSystemManagerId(null)
                   .setSystemManagerStatusCheck(false)
                   .setHost(driverHost)
                   .setPort(port)
                   .build();
          driverA = new PubSubDriverImpl(config, listenerA);
          listenerA.setSelf(driverA);
          drivers.put(driverHost, driverA);
        } else {
          Listener listenerB = new Listener();
          listenerB.setPeer(driverA);
          config = new ConfigBuilder()
                   .setSystemManagerId(null)
                   .setSystemManagerStatusCheck(false)
                   .setHost(driverHost)
                   .setPort(port)
                   .build();
          driverB = new PubSubDriverImpl(config, listenerB);
          listenerB.setSelf(driverB);
          listenerA.setPeer(driverB);
          drivers.put(driverHost, driverB);
        }
      }
      driverA.start();
      driverB.start();
      addPattern("*@bridge");
    }
  }

  public Map<String, Integer> getHosts() {
    Map<String, Integer> hosts = new HashMap<>();
    for (String host : drivers.keySet()) {
      hosts.put(host, port);
    }
    return hosts;
  }

  /**
   * Redis PSUBSCRIBE command matching pattern for both 
   * Redis server A and B.
   * 
   * @param pattern Redis PSUBSCRIBE pattern.
   * @return true if the pattern is successfully set.
   */
  public boolean addPattern(String pattern) {
    boolean newlySet = false;
    for (String host : patterns.keySet()) {
      HashSet<String> set = patterns.get(host);
      if (set != null) {
        boolean setThis = set.add(pattern);
        newlySet = (newlySet || setThis) ? true : false;
        if (setThis) {
          drivers.get(host).psubscribeChannel(pattern);
        }
      }
    }
    return newlySet;
  }

  /**
   * Redis PSUBSCRIBE command matching pattern for either 
   * Redis server A or B.
   * 
   * @param host Redis host
   * @param pattern Redis PSUBSCRIBE pattern.
   * @return true if the pattern is successfully set.
   */
  public boolean addPattern(String host, String pattern) {
    HashSet<String> set = patterns.get(host);
    if (set != null) {
      boolean newlySet = set.add(pattern);
      if (newlySet) {
        drivers.get(host).psubscribeChannel(pattern);
      }
      return newlySet;
    } else {
      return false;
    }
  }

  public boolean removePattern(String pattern) {
    boolean newlyUnset = false;
    for (String host : patterns.keySet()) {
      HashSet<String> set = patterns.get(host);
      if (set != null) {
        boolean unsetThis = set.remove(pattern);
        newlyUnset = (newlyUnset || unsetThis) ? true : false;
        if (unsetThis) {
          drivers.get(host).punsubscribeChannel(pattern);
        }
      }
    }
    return newlyUnset;
  }

  public boolean removePattern(String host, String pattern) {
    HashSet<String> set = patterns.get(host);
    if (set != null) {
      boolean newlyUnset = set.remove(pattern);
      if (newlyUnset) {
        drivers.get(host).punsubscribeChannel(pattern);
      }
      return newlyUnset;
    } else {
      return false;
    }
  }

  public void clearPatterns() {
    for (String host : drivers.keySet()) {
      drivers.get(host).punsubscribeAll();
    }
    for (String host : patterns.keySet()) {
      patterns.get(host).clear();
    }
  }

  public void clearPatterns(String host) {
    patterns.get(host).clear();
    drivers.get(host).punsubscribeAll();
  }

  public class Listener implements IMessageListener {

    String host;
    IPubSubDriver self;
    IPubSubDriver peer;

    public void setSelf(IPubSubDriver self) {
      this.self = self;
    }

    public void setPeer(IPubSubDriver peer) {
      this.peer = peer;
    }

    @Override
    public void onMessage(String channel, byte[] message) {
    }

    @Override
    public void onPmessage(String pattern, String channel, byte[] message) {
      if (log.isDebugEnabled()) {
        log.debug("pattern: {}, channel: {}, message{}", pattern, channel, new String(message));
        peer.publish(channel.replace("@bridge", ""), message);
      }
    }

    @Override
    public void onReconnected() {
      self.psubscribeChannels(patterns.get(host));
    }

    @Override
    public void onDisconnected() {
    }
  }

  private RequestParser<IActionCallback> createParser() {

    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.POST,
            "<host>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return requestPostDriver(
                parsed.getParam("host"));
              }
            });
        addRule(Method.POST,
            "<host>/<pattern>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return requestPostPattern(
                    parsed.getParam("host"),
                    parsed.getParam("pattern"));
              }
            });
        addRule(Method.DELETE,
            "<host>/<pattern>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return requestDeletePattern(
                    parsed.getParam("host"),
                    parsed.getParam("pattern"));
              }
            });
      }
    };
  }

  protected Response requestPostPattern(String host, String pattern) {
    addPattern(host, pattern);
    return new Response(Response.OK, null);
  }

  protected Response requestDeletePattern(String host, String pattern) {
    removePattern(host, pattern);
    return new Response(Response.OK, null);
  }

  protected Response requestPostDriver(String host) {
    addDriver(host, port);
    return new Response(Response.OK, null);
  }

  @Override
  protected Response onRequest(
      final Request request) {
    RequestParser<IActionCallback>.ParsedRequest parsed = parser
        .parse(request);
    if (parsed == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
    IActionCallback callback = parsed.getResult();
    if (callback == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
    try {
      return callback.process(parsed);
    } catch (Exception e) {
      log.error("request error", e);
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
  }

  @Override
  protected String getSuperType() {
    // TODO Auto-generated method stub
    return null;
  }
}
