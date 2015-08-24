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

package org.o3project.odenos.remoteobject.messagingclient.redis;

import org.o3project.odenos.remoteobject.messagingclient.IMessageListener;
import org.o3project.odenos.remoteobject.messagingclient.IMultiMap;
import org.o3project.odenos.remoteobject.messagingclient.IPubSubDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Set;

import org.o3project.odenos.remoteobject.messagingclient.Config;
import org.o3project.odenos.remoteobject.messagingclient.Config.MODE;

import static redis.clients.jedis.Protocol.DEFAULT_PORT;

/**
 * {@link org.o3project.odenos.remoteobject.messagingclient.IPubSubDriver}
 *  implementation for Redis server.
 *
 * <p>
 * Note: Redis server does not allow us to share same IP socket
 * for both event publication and subscription. That's why this class
 * instantiates two Redis clients, one for publication and the other
 * for subscription: PublischerClient and SusbscriberClient.
 * Additionally, this class instantiates ChannelCheckerClient.
 *
 * <p>
 * Redis server can be SPOF and this implementation supports
 * a very limited change-over capability to cope with Redis
 * server crash or network connectivity loss. However, you may develop
 * your own {@link org.o3project.odenos.remoteobject.messagingclient.IPubSubDriver}
 * implementation class supporting full-fledged high-availability
 * (clustering, fail-over etc) pubsub.
 *
 * <p>
 * Note: {@link PublisherClient} supports another publish command "evalsha"
 * (a lua script) to mulitcast a message locally and also to send a message
 * to a remote Redis server via some sort of bridge: B2B (back-to-back)
 * Redis client, although this package does not include such bridge.
 *
 * <pre>
 * 1. Two independent Redis servers
 *
 * Serever crashed!
 * [Redis server]                         [Redis server]
 *     |   |
 *     |   |
 *     |   +----------------------------------+
 *     |                                      |
 *  Redis client                          Redis client
 *
 *                        |
 *                        V
 *
 * [Redis server]                         [Redis server]
 *                                            |   |
 *                                            |   |
 *     +--------------------------------------+   |
 *     |                                          |
 *  Redis client                          Redis client
 *
 *
 * 2. Redis server clustering (master-master mode)
 *
 *  lua script                             lua script
 * [Redis server] ---- [bridge(B2B)] ---- [Redis server]
 *     |   |
 *     |   |       Connectivity lost
 *     |   +--------------X-------------------+
 *     |                                      |
 *  Redis client                          Redis client
 *
 *                        |
 *                        V
 *  lua script                             lua script
 * [Redis server] ---- [bridge(B2B)] ---- [Redis server]
 *     |                                      |
 *     |                                      |
 *     |                                      |
 *     |                                      |
 *  Redis client                          Redis client
 *
 * </pre>
 *
 * @see org.o3project.odenos.remoteobject.messagingclient.IPubSubDriver
 */
public class PubSubDriverImpl implements IPubSubDriver, Closeable {

  private static final Logger log = LoggerFactory.getLogger(PubSubDriverImpl.class);

  private PublisherClient publisherClient;
  private SubscriberClient subscriberClient;
  private ChannelCheckerClient channelCheckerClient;

  private boolean connected = false;

  private IMessageListener listener;

  private boolean bridged = false;

  private static final String HOST = "localhost";
  private final RedisServerAddress redisServerAddress;

  /**
   * Constructor.
   *
   * @param config {@link org.o3project.odenos.remoteobject.messagingclient.Config}
   * @param listener {@link org.o3project.odenos.remoteobject.messagingclient.IMessageListener}
   * or null in case of a monitoring-only client
   */
  public PubSubDriverImpl(Config config, IMessageListener listener) {

    this.listener = listener;

    bridged = config.getMode().contains(MODE.PUBSUB_BRIDGED);

    // Sets Redis server addresses
    String host = (config.getHost() == null) ? HOST : config.getHost();
    int port = (config.getPort() <= 0) ? DEFAULT_PORT : config.getPort();
    String hostB = (config.getHostB() == null) ? HOST : config.getHostB();
    int portB = (config.getPortB() <= 0) ? DEFAULT_PORT : config.getPortB();
    redisServerAddress = new RedisServerAddress(host, port, hostB, portB);

    // PublisherQueueSize (default: 1000)
    int publisherQueueSize = (config.getPublisherQueueSize() == 0)
        ? 1000 : config.getPublisherQueueSize();

    // Creates a set of pubsub clients
    if (log.isDebugEnabled()) {
      log.debug("[Redis server] host: {}, port: {}, bridged: {}",
          redisServerAddress.getHost(), redisServerAddress.getPort(), bridged);
    }

    this.publisherClient =
        new PublisherClient(redisServerAddress, publisherQueueSize, this, bridged,
            config.getPublishScript());
    this.subscriberClient = new SubscriberClient(redisServerAddress, this);
    this.channelCheckerClient = new ChannelCheckerClient(redisServerAddress);
  }

  /**
   * Starts SubscriberClient and PublisherClient.
   *
   * <p>
   * Note: this method blocks the calling thread until
   * the Redis client becomes ready.
   */
  @Override
  public void start() {

    subscriberClient.start();
    publisherClient.start();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      log.error("thread error", e);
    }

    boolean logOutput = false;
    while (true) {
      if (subscriberClient.isStarted() && publisherClient.isStarted()) {
        connected = true;
        break;
      } else {
        if (!logOutput) {
          log.warn("unable to get access to Redis server (host: {}, port: {})",
              redisServerAddress.getHost(), redisServerAddress.getPort());
          logOutput = true;
        }
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          log.error("thread error", e);
        }
      }
    }
  }

  /**
   * Closes SubscriberClient and PublisherClient.
   */
  @Override
  public void close() {
    channelCheckerClient.close();
    publisherClient.close();
    subscriberClient.close();
  }

  @Override
  public boolean isStarted() {
    return (publisherClient.isStarted() && subscriberClient.isStarted());
  }

  @Override
  public void subscribeChannels(Set<String> channels) {
    subscriberClient.subscribeChannels(channels);
  }

  @Override
  public void subscribeChannel(String channel) {
    subscriberClient.subscribeChannel(channel);
  }

  @Override
  public void unsubscribeChannels(Set<String> channels) {
    subscriberClient.unsubscribeChannels(channels);
  }

  @Override
  public void unsubscribeChannel(String channel) {
    subscriberClient.unsubscribeChannel(channel);
  }

  @Override
  public void unsubscribeAll() {
    publisherClient.unsubscribe();
  }

  @Override
  public void psubscribeChannels(Set<String> patterns) {
    subscriberClient.psubscribeChannels(patterns);
  }

  @Override
  public void psubscribeChannel(String pattern) {
    subscriberClient.psubscribeChannel(pattern);
  }

  @Override
  public void punsubscribeChannels(Set<String> patterns) {
    subscriberClient.punsubscribeChannels(patterns);
  }

  @Override
  public void punsubscribeChannel(String patterns) {
    subscriberClient.punsubscribeChannel(patterns);
  }

  @Override
  public void punsubscribeAll() {
    subscriberClient.punsubscribeAll();
  }

  @Override
  public void publish(String channel, byte[] message) {
    publisherClient.publish(channel, message);
  }

  @Override
  public boolean channelExist(String channel) {
    return channelCheckerClient.channelExist(channel);
  }

  @Override
  public <K, V> IMultiMap<K, V> getMultiMap(String name) {
    return null;
  }

  public void onMessage(String channel, byte[] message) {
    if (listener != null) {  // checks if this is monitoring-only client or not.
      listener.onMessage(channel, message);
    }
  }

  public void onPmessage(String pattern, String channel, byte[] message) {
    if (listener != null) {  // checks if this is monitoring-only client or not.
      listener.onPmessage(pattern, channel, message);
    }
  }

  private int acceptedOnReconnected = -1;
  private int acceptedOnDisconnected = -1;

  /**
   * Called by {@link PublisherClient} and {@link SubscriberClient} to
   * inform that a connection to Redis server has just been resumed.
   *
   * @param sessionId ID that identifies a pair of {@link PublisherClient}
   * and {@link SubscriberClient}
   */
  public synchronized void onReconnected(int sessionId) {
    if (publisherClient.isConnected() && subscriberClient.isConnected()
        && acceptedOnReconnected < sessionId) {
      if (log.isDebugEnabled()) {  // checks if this is monitoring-only client or not.
        log.debug("sessionId: {}, acceptedOnReconnected: {}", sessionId, acceptedOnReconnected);
      }
      acceptedOnReconnected = sessionId;
      if (listener != null) {  // checks if this is monitoring-only client or not.
        listener.onReconnected();
      }
      connected = true;
    }
  }

  /**
   * Called by {@link PublisherClient} and {@link SubscriberClient} to
   * inform that a connection to Redis server has just been lost.
   *
   * @param sessionId ID that identifies a pair of {@link PublisherClient}
   * and {@link SubscriberClient}
   */
  public synchronized void onDisconnected(int sessionId) {
    if (connected && acceptedOnDisconnected < sessionId) {
      if (log.isDebugEnabled()) {
        log.debug("sessionId: {}, acceptedOnDisconnected: {}", sessionId, acceptedOnDisconnected);
      }
      acceptedOnDisconnected = sessionId;
      close();
      if (listener != null) {  // checks if this is monitoring-only client or not.
        listener.onDisconnected();
      }
      redisServerAddress.next();
      start();
    }
  }
}
