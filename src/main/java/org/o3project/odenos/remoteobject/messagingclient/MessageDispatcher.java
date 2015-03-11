/*
 * Copyright 2015 NEC Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.o3project.odenos.remoteobject.messagingclient;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.actor.Actor;
import org.o3project.odenos.remoteobject.actor.Mail;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.Config.MODE;
import org.o3project.odenos.remoteobject.messagingclient.redis.MonitorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>PubSub messaging client for OdenOS</h1>
 *
 * <p>
 * This class provides the following services to remote objects:
 * <ul>
 * <li>asynchronous channel subscription (SUBSCRIBE/UNSUBSCRIBE)
 * <li>asynchronous event publication (PUBLISH)
 * <li>synchronous request/response (remote transactions)
 * <li>event dispatch to local objects
 * </ul>
 *
 * <p>
 * Some of parameters in this class are configurable.
 * See {@link ConfigBuilder}.
 *
 * <p>
 * The user of this class needs to take the following steps:
 * <ol>
 * <li> Instantiate this class.
 * <li> Call "start()" method to start PublisherClient and SubscriberClient.
 * <li> (optional) Call "join()" to block the thread that instantiated this class
 * (e.g., Odenos.java's main()).
 * <li> Call "close()" to terminate the instance of this class, or try-with-resources.
 * </ol>
 *
 * <p>
 * If you want to monitor all the messages being sent/received, enable the logger's
 * DEBUG flag (log4j). The logger dumps messages in a YAML-like format.
 *
 * <p>
 * You may extend this class (override some methods) to enhance the features
 * or add additional capabilities.
 *
 * @see IPubSubDriver
 * @see IMessageListener
 * @see Config
 * @see ConfigBuilder
 * @see RemoteTransactions
 * @see SubscribersMap
 */
public class MessageDispatcher implements Closeable, IMessageListener {

  private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

  protected static final byte TYPE_REQUEST = 0;
  protected static final byte TYPE_RESPONSE = 1;
  protected static final byte TYPE_EVENT = 2;

  protected static final byte[] MONITOR_CHANNEL = "_monitor".getBytes();
  protected static final String REQUEST = "REQUEST";
  protected static final String RESPONSE = "RESPONSE";
  protected static final String EVENT = "EVENT";

  protected BlockingQueue<Runnable> subscriberQueue;

  protected SynchronousQueue<Object> dispatcherJoin =
      new SynchronousQueue<>();

  protected ConcurrentHashMap<String, RemoteObject> localObjectsMap =
      new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, Boolean> remoteObjectsMap =
      new ConcurrentHashMap<>();

  protected final String sourceDispatcherId;
  protected final String eventManagerId;
  protected final String systemManagerId;

  protected final EnumSet<MODE> mode;
  protected boolean pubSubDriverSuspended = false;

  protected ArrayBlockingQueue<Request> eventManagerQueue =
      new ArrayBlockingQueue<>(1000);
  protected Thread subscriptionFeeder = null;

  protected MessagePack msgpack = new MessagePack();

  protected IPubSubDriver driverImpl;

  protected SubscribersMap subscribersMap = new SubscribersMap();

  protected RemoteTransactions remoteTransactions = null;

  protected final Actor actor;
  protected int serial = 0;

  protected AtomicInteger loopbackSequenceNumber = new AtomicInteger(0);

  protected boolean localRequestsToPubSubServer = false;
  protected boolean includeSourceObjectId = false;
  protected boolean reflectMessageToMonitor = false;

  // For message monitoring
  protected MonitorClient monitor = null;

  protected static final String channelString(final String publisherId, final String eventId) {
    return publisherId + ":" + eventId;
  }

  /**
   * Constructor with no arguments.
   */
  public MessageDispatcher() {
    this(new ConfigBuilder().build());
  }

  /**
   * Constructor.
   *
   * @param systemManagerId System Manager ID
   */
  public MessageDispatcher(final String systemManagerId) {
    this(new ConfigBuilder()
        .setSystemManagerId(systemManagerId)
        .build());
  }

  /**
   * Constructor.
   *
   * @param systemManagerId System Manager ID
   * @param host pubsub server host name or IP addrss
   * @param port pubsub server port number
   */
  public MessageDispatcher(final String systemManagerId,
      final String host, final int port) {
    this(new ConfigBuilder()
        .setSystemManagerId(systemManagerId)
        .setHost(host)
        .setPort(port)
        .build());
  }

  /**
   *
   * @param config {@link MessageDispatcher}'s config
   * @see org.o3project.odenos.remoteobject.messagingclient.redis.PubSubDriverImpl
   */
  public MessageDispatcher(Config config) {

    // Config
    systemManagerId = config.getSystemManagerId();
    eventManagerId = config.getEventManagerId();
    sourceDispatcherId = config.getSourceDispatcherId();
    mode = config.getMode();

    localRequestsToPubSubServer = mode.contains(MODE.LOCAL_REQUEST_TO_PUBSUB);
    includeSourceObjectId = mode.contains(MODE.INCLUDE_SOURCE_OBJECT_ID);
    reflectMessageToMonitor = mode.contains(MODE.REFLECT_MESSAGE_TO_MONITOR);

    // Actor system instantiation.
    // The number of woker threads: the max number of remote transactions.
    actor = Actor.getInstance(config.getRemoteTransactionsMax());

    // Instantiates IPubSubDriver impl. class
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    try {
      Class<?> clazz = classLoader.loadClass(config.getPubSubDriverImpl());
      Constructor<?> constructor = clazz.getDeclaredConstructor(
          Config.class, IMessageListener.class);
      driverImpl = (IPubSubDriver) constructor.newInstance(config, this);
    } catch (Exception e) {
      log.error("class load error", e);
    }

    // Remote Transactions pool
    remoteTransactions = new RemoteTransactions(this, config);

    // Monitoring
    if (reflectMessageToMonitor) {
      String monitorName = "monitor@" + sourceDispatcherId;
      monitor = new MonitorClient(config.getHost(), config.getPort()); // Keep-alive
      monitor.setClientName(monitorName.getBytes());
    }
  }

  /**
   * onMessage implementation.
   *
   * <p>
   * This method has two roles:
   * <ul>
   * <li> Works like a mail transfer agent to transfer
   * a received request or event to {@link org.o3project.odenos.remoteobject.RemoteObject}.
   * <li> Supports both synchronous and asynchronous messaging.
   * </ul>
   */
  @Override
  public void onMessage(final String channel, byte[] message) {

    serial++; // Serial number for incoming messages.

    try {
      BufferUnpacker upk = msgpack.createBufferUnpacker(message);
      // read delivery header.
      byte type = upk.readByte();
      final int sno = upk.readInt(); // Sequence number for outgoing request messages.
      final String sourceObjectId = upk.readString();

      RemoteObject localObject = null;
      Queue<Mail> mailbox = null;
      Mail mail = null;

      switch (type) {

        case TYPE_REQUEST: // Transaction(request): synchronous operation
          /*
           *  --- request --> dispatchRequest() -------> [RemoteObject]
           *                                                  |
           *  <-- response --- publishResponseAsync() <-------+
           */
          final Request request = upk.read(Request.class);

          // Monitoring
          if (reflectMessageToMonitor) {
            BufferPacker pk = msgpack.createBufferPacker();
            pk.write(REQUEST);
            pk.write(channel);
            pk.write(sourceObjectId);
            pk.write(sno);
            pk.write(request.method.name());
            pk.write("/" + channel + "/" + request.path);
            pk.write(request.getBodyValue());
            byte[] data = pk.toByteArray();
            monitor.publish(MONITOR_CHANNEL, data);
          }

          // Wraps the request with Mail and deliver it to a mailbox.
          String to = request.objectId;
          mail = new Mail(serial, sno, to, sourceObjectId, this, request, null);
          localObject = localObjectsMap.get(to);
          if (localObject != null) {
            mailbox = localObject.getMailbox();
            synchronized (mailbox) {
              mailbox.add(mail);
              if (!localObject.isRunning()) {
                localObject.setRunning(true);
                // Assigns a thread to read a mail in the mailbox.
                actor.read(localObject);
              }
            }
          }
          break;

        case TYPE_RESPONSE: // Transaction(response): synchronous operation
          /*
           *                                 publishRequestAsync()
           * requestSync() -> [RemoteTransaction] ---- request -----> [RemoteObject]
           *              <--            ^                                  |
           *                             |                     publishResponseAsync()
           *                             |                                  |
           *                             +- signalResponse() <-- response --+
           */
          Response response = upk.read(Response.class);

          // Monitoring
          if (reflectMessageToMonitor) {
            BufferPacker pk = msgpack.createBufferPacker();
            pk.write(RESPONSE);
            pk.write(channel);
            pk.write(sourceObjectId);
            pk.write(sno);
            pk.write(response.statusCode);
            pk.write(response.getBodyValue());
            byte[] data = pk.toByteArray();
            monitor.publish(MONITOR_CHANNEL, data);
          }

          remoteTransactions.signalResponse(sno, response);
          break;

        case TYPE_EVENT: // Asynchronous
          /*
           * publishEventAsync() -- event --> dispatchEvent() --> [RemoteObject]
           *                                         :
           *                              [EventSubscriptionMap]
           */
          final Event event = upk.read(Event.class);

          // All the subscribers of the channel
          final Collection<String> subscribers =
              subscribersMap.getSubscribers(channel);
          if (subscribers == null) { // No subscribers found on the channel
            if (log.isDebugEnabled()) {
              log.debug("no subscribers subscribing the channel: {}", channel);
            }
            return; // Silently discards the event
          }

          // Wraps the event with Mail and deliver it to a mailbox.
          for (String subscriber : subscribers) {
            localObject = localObjectsMap.get(subscriber);
            if (localObject != null) {

              // Monitoring
              if (reflectMessageToMonitor) {
                BufferPacker pk = msgpack.createBufferPacker();
                pk.write(EVENT);
                pk.write(subscriber);
                pk.write(event.publisherId);
                pk.write(event.publisherId + ":" + event.getEventType());
                pk.write(event.getBodyValue());
                byte[] data = pk.toByteArray();
                monitor.publish(MONITOR_CHANNEL, data);
              }

              mail = new Mail(serial, sno, subscriber, channel, this, null, event);
              mailbox = localObject.getMailbox();
              synchronized (mailbox) {
                mailbox.add(mail);
                if (!localObject.isRunning()) {
                  localObject.setRunning(true);
                  // Assigns a thread to read a mail in the mailbox.
                  actor.read(localObject);
                }
              }
            }
          }
          break;

        default:
          break;
      }
    } catch (Exception e) {
      log.error("onMessage failed", e);
    }
  }

  // Receives a Pattern message
  @Override
  public void onPmessage(String pattern, String channel, byte[] message) {
    if (log.isDebugEnabled()) {
      log.debug("message received,"
          + " pattern: {}, channel: {}, message: {}",
          pattern, channel, new String(message));
    }
  }

  /**
   * Starts the services.
   *
   * <p>
   * This method must be called after instantiating this class to start
   * a {@link IPubSubDriver} implementation class.
   * </p>
   */
  public void start() {

    // This method blocks until the connectivity with pubsub server
    // has become ready.
    driverImpl.start();

    // To receive Request from "remote" RemoteObject,
    // MessageDispatcher needs to register itself w/ pubsub server.
    driverImpl.subscribeChannel(sourceDispatcherId);

    // This thread feeds subscription info to EventManager
    // in an eventually-consistent manner.
    subscriptionFeeder = new Thread(new Runnable() {
      @Override
      public void run() {
        do {
          Request request = null;
          try {
            request = eventManagerQueue.take();
            Response response = requestSync(request, getSourceDispatcherId());
            if (response == null || !response.statusCode.equals(Response.OK)) {
              log.warn("Unsuccessful transaction to EventManager: " + response.statusCode);
            }
          } catch (InterruptedException e) {
            log.warn("Unsuccessful transaction to EventManager due to some internal error");
          } catch (Exception e) {
            log.warn("EventManager may be inactive");
          }
        } while (true); // TODO: graceful thread termination
      }
    });
    subscriptionFeeder.start();

    log.info("started");
  }

  /**
   * After starting this class (start()), this method MAY be called to block the
   * thread that created the instance of this class. Otherwise, the thread will
   * finish and the instance of this class will be terminated.
   *
   * @throws InterruptedException unsuccessful join
   */
  public void join() throws InterruptedException {
    // TODO:
    // initiate a graceful termination procedure (i.e., close()).
    log.info("joining");
    @SuppressWarnings("unused")
    Object object = dispatcherJoin.take();
  }

  @Deprecated
  public void stop() {
    close();
    log.info("stop");
  }

  /**
   * Closes the services.
   *
   * <p>
   * You can also do "try-with-resources" to automatically close this class.
   */
  @Override
  public void close() {
    // TODO: Graceful termination of all the components and the transport
    // TODO: subscriptionFeeder termination
    driverImpl.close();
    remoteTransactions.onFinalize();
    subscribersMap.clear();
    log.info("terminated");
  }

  /**
   * Adds a local object as a listener of messages.
   *
   * <p>
   * The user (i.e., RemoteObject) of this class calls this method
   * to register the RemoteObject as "local RemoteObject" with
   * this class.
   *
   * <pre>
   * {@literal
   *   [RemoteObject]    [RemoteObject]    [RemoteObject]
   *   dispatchEvent()  dispatchRequest() dispatchRequest()
   *         ^                 ^                 ^
   *         |                 |                 |
   *         +---------------+ + +---------------+
   *                         | | |
   *                  [MessageDispatcher]<>-----[SubscribersMap]
   *                           |
   *                        message
   *                           |
   *                    [pubsub server]
   * }
   * </pre>
   *
   * <p>
   * This method also sends "SUBSCRIBE own-object-ID-as-channel"
   * to pubsub server to receive PUBLISH destined to the remote object,
   * since requestSync() method sends PUBLISH as "request" to the remote
   * object.
   *
   * @param localObject a remote object
   * @see org.o3project.odenos.remoteobject.RemoteObject#dispatchEvent
   * @see org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest
   */
  public void addLocalObject(RemoteObject localObject) {
    String objectId = localObject.getObjectId();
    if (localObjectsMap.putIfAbsent(objectId, localObject) == null) {
      driverImpl.subscribeChannel(objectId);
    }
    if (objectId.equals(systemManagerId)) {
      driverImpl.systemManagerAttached();
    }
  }

  /**
   * Removes a local object.
   *
   * <p>
   * This method also sends "UNSUBSCRIBE own-object-ID-as-channel"
   * to pubsub server to stop receiving PUBLISH destined to the
   * remote object.
   *
   * @param localObject a remote object
   */
  public void removeLocalObject(RemoteObject localObject) {
    String objectId = localObject.getObjectId();
    if (localObjectsMap.remove(objectId) != null) {
      // Unsubscribes objectId as a channel to stop receiving Request
      // from remote objects via PubSub.
      driverImpl.unsubscribeChannel(objectId);
    }
  }

  /**
   * Returns Object ID registered as "local RemoteObject".
   *
   * @param objectId Object ID of a remote object
   * @return true if the remote object has already been registered as "local RemoteObject"
   */
  public boolean containObjectId(String objectId) {
    return localObjectsMap.containsKey(objectId);
  }

  /**
   * Appends a remote object.
   *
   * <p>
   * TODO: Should this method be deprecated or not?
   * @param objectId object ID.
   */
  public void addRemoteObject(String objectId) {
    remoteObjectsMap.putIfAbsent(objectId, Boolean.valueOf(true));
  }

  /**
   * Removes a remote object.
   *
   * <p>
   * TODO: Should this method be deprecated or not?
   *
   * @param objectId Object ID
   */
  public void removeRemoteObject(String objectId) {
    remoteObjectsMap.remove(objectId);
  }

  /**
   * Returns MessageDispatcher's ID.
   *
   * @return Source Dispatcher ID
   */
  public String getSourceDispatcherId() {
    return sourceDispatcherId;
  }

  /**
   * Sets a remote system manager.
   *
   * <p>
   * TODO: Should this method be deprecated or not?
   */
  public void setRemoteSystemManager() {
    remoteObjectsMap.put(systemManagerId, Boolean.valueOf(true));
  }

  /**
   * Sets a remote system manager.
   * @param host name of host.
   * @param port port number.
   * @throws IOException if an I/O error occurs.
   * @deprecated @see #setRemoteSystemManager()
   */
  @Deprecated
  public void setRemoteSystemManager(String host, int port)
      throws IOException {
    setRemoteSystemManager();
  }

  /**
   * Sets a remote system manager.
   *
   * @param systemManagerId system manager ID.
   * @deprecated @see #setRemoteSystemManager()
   */
  @Deprecated
  public void setRemoteSystemManager(String systemManagerId) {
    remoteObjectsMap.put(systemManagerId, Boolean.valueOf(true));
  }

  /**
   * Synchronous request/response service (remote transactions)
   *
   * <p>
   * This method operates in two modes:
   * <ul>
   * <li> if the request is to "local RemoteObject", then
   * this method works like a local loopback interface
   * to dispatch the request to "local RemoteObject".
   * <li> else if the request is to "remote RemoteObject", then
   * this method uses RemoteTransactions class to send the
   * request to "remote RemoteObject" via pubsub server.
   * </ul>
   *
   * <pre>
   *                 (local)
   * [RemoteObject]  [RemoteObject]
   *  requestSync()        ^
   *       |               |
   *       |               |
   *       +---------------+
   *           loopback
   *
   *                               (remote)
   * [RemoteObject]                [RemoteObject]
   *  requsetSync()                      ^
   *       |                             |
   *       |                             |
   *       +-------[pubsub server]-------+
   * </pre>
   *
   * @param request Request to be sent
   * @return Response response to the request
   * @throws Exception exception
   */
  public Response requestSync(Request request) throws Exception {
    return requestSync(request, getSourceDispatcherId());
  }

  public Response requestSync(Request request, String sourceObjectId)
      throws Exception {
    String objectId = request.objectId;
    Response response;
    RemoteObject localObject = localObjectsMap.get(objectId);
    int sno = loopbackSequenceNumber.getAndIncrement();
    if (localObject != null && !localRequestsToPubSubServer) {

      // Monitoring
      if (reflectMessageToMonitor) {
        BufferPacker pk = msgpack.createBufferPacker();
        pk.write(REQUEST);
        pk.write(objectId);
        pk.write(sourceObjectId);
        pk.write(sno);
        pk.write(request.method.name());
        pk.write("/" + request.objectId + "/" + request.path);
        pk.write(request.getBodyValue());
        byte[] data = pk.toByteArray();
        monitor.publish(MONITOR_CHANNEL, data);
      }

      // Loopback of request/response
      // synchronized with Actor#read()
      synchronized (localObject) {
        Request requested = deepCopy(request);
        Response responsed = localObject.dispatchRequest(requested);
        response = deepCopy(responsed);
      }

      // Monitoring
      if (reflectMessageToMonitor) {
        BufferPacker pk = msgpack.createBufferPacker();
        pk.write(RESPONSE);
        pk.write(sourceObjectId);
        pk.write(objectId);
        pk.write(sno);
        pk.write(response.statusCode);
        pk.write(request.getBodyValue());
        byte[] data = pk.toByteArray();
        monitor.publish(MONITOR_CHANNEL, data);
      }

      // TODO: remoteObjectsMap is not unused.
    } else if (remoteObjectsMap.containsKey(objectId)) {
      response = remoteTransactions.sendRequest(request, sourceObjectId);
    } else {
      // request to an unregistered remote object
      remoteObjectsMap.put(objectId, Boolean.valueOf(false));
      response = remoteTransactions.sendRequest(request, sourceObjectId);
    }
    return response;
  }

  /**
   * Deep copy.
   *
   * <p>
   * This is mainly to avoid {@link java.util.ConcurrentModificationException}.
   *
   * @param object original data.
   * @return copy data.
   * @throws IOException exception.
   */
  @SuppressWarnings("unchecked")
  public <T extends MessageBodyUnpacker> T deepCopy(T object) throws IOException {
    Class<?> clazz = object.getClass();
    byte[] raw = msgpack.write(object);
    return (T) msgpack.read(raw, clazz);
  }

  /**
   * Asynchronous event publication service
   *
   * <p>
   * Remote objects use this method to publish an event
   * asynchronously.
   */
  public void publishEventAsync(final Event event) throws IOException {
    final String channel = channelString(event.publisherId, event.eventType);
    publishEventAsync(channel, event, null);
  }

  private void
      publishEventAsync(final String channel, final Event event, final String subscriberId)
          throws IOException {
    BufferPacker pk = msgpack.createBufferPacker();
    // write delivery header.
    byte[] message = null;
    pk.write(TYPE_EVENT);
    pk.write(0);
    pk.write("event");
    // write delivery body.
    pk.write(event);
    message = pk.toByteArray();
    // PUBLISH
    driverImpl.publish(channel, message);
  }

  /**
   * Asynchronous event publication service for requestSync().
   *
   * <p>
   * requestSync() turns into this method via RemoteMessageTransport.
   *
   * <p>
   * Remote objects uses this method to publish a request as event asynchronously.
   *
   * <p>
   * Although this method is asynchronous, {@link RemoteTransactions} provides
   * a synchronous method to wait for a response from another remote object.
   */
  protected void publishRequestAsync(final int sno, final Request request,
      final String sourceObjectId)
      throws IOException {
    BufferPacker pk = msgpack.createBufferPacker();
    // write delivery header.
    pk.write(TYPE_REQUEST);
    pk.write(sno);
    if (sourceObjectId != null && includeSourceObjectId) {
      pk.write(sourceObjectId);
    } else {
      pk.write(getSourceDispatcherId());
    }
    // write delivery body.
    pk.write(request);
    byte[] message = pk.toByteArray();
    String channel = request.objectId;
    // PUBLISH
    driverImpl.publish(channel, message);
  }

  /**
   * Asynchronous event publication service for requestSync().
   *
   * <p>
   * The response eventually reaches its originating method (requestSync()) via
   * RemoteMessageTransport.
   */
  public void publishResponseAsync(final int sno, final String channel,
      final Request request, final Response response)
      throws IOException {
    BufferPacker pk = msgpack.createBufferPacker();
    // write delivery header.
    pk.write(TYPE_RESPONSE);
    pk.write(sno);
    pk.write(request.objectId);
    // write delivery body.
    pk.write(response);
    byte[] message = pk.toByteArray();
    // PUBLISH
    driverImpl.publish(channel, message);
  }

  /**
   * Event subscription service.
   *
   * @param eventSubscription pubsub channel to be subscribed
   */
  public Response subscribeEvent(final EventSubscription eventSubscription) {
    subscribeChannelsWithDiff(eventSubscription);
    Response response = new Response(Response.OK, null);
    return response;
  }

  protected void subscribeChannelsWithDiff(final EventSubscription eventSubscription) {
    {
      String subscriberId = eventSubscription.getSubscriberId();

      if (eventSubscription.hasChannelsToBeSubscribed()) {
        Map<String, Set<String>> channelsToBeSubscribed =
            eventSubscription.getChannelsToBeSubscribed();
        subscribeChannels(subscriberId, channelsToBeSubscribed);
        eventSubscription.clearChannelsToBeSubscribed();

      } else if (eventSubscription.hasChannelsToBeUnsubscribed()) {
        Map<String, Set<String>> channelsToBeUnsubscribed =
            eventSubscription.getChannelsToBeUnsubscribed();
        unsubscribeChannels(subscriberId, channelsToBeUnsubscribed);
        eventSubscription.clearChannelsToBeUnsubscribed();

      } else {
        if (log.isDebugEnabled()) {
          log.debug("calling this method with empty subscription must be avoided");
        }
      }

      requestToEventManager(subscriberId, eventSubscription);
    }
  }

  protected void requestToEventManager(
      String subscriberId, EventSubscription eventSubscription) {
    Request request = new Request(getEventManagerId(),
        Request.Method.PUT,
        "settings/event_subscriptions/" + subscriberId,
        eventSubscription);
    if (!eventManagerQueue.offer(request)) {
      log.warn("EventManager request queue is full");
    }
  }

  /**
   * Channel subscription service.
   *
   * @param subscriberId Subscriber's object ID
   * @param channelsToBeSubscribed channels to be subscribed
   */
  public void subscribeChannels(final String subscriberId,
      final Map<String, Set<String>> channelsToBeSubscribed) {
    Set<String> channels = new HashSet<>();
    for (String publisherId : channelsToBeSubscribed.keySet()) {
      Set<String> eventIds = channelsToBeSubscribed.get(publisherId);
      for (String eventId : eventIds) {
        String channel = channelString(publisherId, eventId);
        if (subscribersMap.setSubscription(channel, subscriberId)) {
          channels.add(channel);
        }
      }
    }
    if (!channels.isEmpty() && !pubSubDriverSuspended) {
      driverImpl.subscribeChannels(channels);
    }
  }

  /**
   * Channel unsubscription service.
   *
   * @param subscriberId subscriber's object ID
   * @param channelsToBeUnsubscribed channels to be unsubscribed
   */
  public void unsubscribeChannels(final String subscriberId,
      final Map<String, Set<String>> channelsToBeUnsubscribed) {
    Set<String> channels = new HashSet<>();
    for (String publisherId : channelsToBeUnsubscribed.keySet()) {
      Set<String> eventIds = channelsToBeUnsubscribed.get(publisherId);
      for (String eventId : eventIds) {
        String channel = channelString(publisherId, eventId);
        if (subscribersMap.removeSubscription(channel, subscriberId)) {
          channels.add(channel);
        }
      }
    }
    if (!channels.isEmpty() && !pubSubDriverSuspended) {
      driverImpl.unsubscribeChannels(channels);
    }
  }

  /**
   * Returns an system manager ID.
   *
   * <p>
   * @return system manager ID
   */
  public String getSystemManagerId() {
    return systemManagerId;
  }

  /**
   * Returns an event manager ID.
   *
   * <p>
   * @return event manager ID.
   */
  public String getEventManagerId() {
    return eventManagerId;
  }

  /**
   * Returns a ChannelChecker instance.
   *
   * <p>
   * @return an instance of Channel Checker
   */
  protected IPubSubDriver getChannelChecker() {
    return driverImpl;
  }

  @Override
  public void onReconnected() {
    log.info("reconnected");
    if (mode.contains(MODE.RESEND_SUBSCRIBE_ON_RECONNECTED)) {

      Set<String> channels;

      // channel as sourceDispatcherId
      channels = new HashSet<String>();
      channels.add(getSourceDispatcherId());
      driverImpl.subscribeChannels(channels);

      // channels as object_IDs registered with localObjectsMap
      channels = localObjectsMap.keySet();
      if (!channels.isEmpty()) {
        driverImpl.subscribeChannels(channels);
      }

      // all channels registered with subscribersMap
      channels = subscribersMap.getSubscribedChannels();
      if (!channels.isEmpty()) {
        driverImpl.subscribeChannels(channels);
      }

      // re-SUBSCRIBE completed with all the registered channels.
      pubSubDriverSuspended = false; // Resumed
    } else {
      // You may add some code here to inform other objects that
      // the network connectivity has resumed or the pubsub server
      // has become available.
    }
  }

  @Override
  public void onDisconnected() {
    log.warn("disconnected");
    if (mode.contains(MODE.RESEND_SUBSCRIBE_ON_RECONNECTED)) {
      pubSubDriverSuspended = true; // Suspended
    } else {
      // You may add some code here to inform other objects that
      // the network connectivity has lost or the pubsub server
      // has become unavailable.
    }
  }

}
