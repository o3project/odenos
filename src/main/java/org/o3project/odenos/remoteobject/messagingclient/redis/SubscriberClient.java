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

import static redis.clients.jedis.Protocol.Keyword.MESSAGE;
import static redis.clients.jedis.Protocol.Keyword.PMESSAGE;
import static redis.clients.jedis.Protocol.Keyword.PSUBSCRIBE;
import static redis.clients.jedis.Protocol.Keyword.PUNSUBSCRIBE;
import static redis.clients.jedis.Protocol.Keyword.SUBSCRIBE;
import static redis.clients.jedis.Protocol.Keyword.UNSUBSCRIBE;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis subscriber client. 
 *
 * @see RedisClient
 * @see PubSubDriverImpl
 * @see org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher
 */
public class SubscriberClient extends RedisClient {

  private static final Logger log = LogManager.getLogger(SubscriberClient.class);
  private static String txid = null;

  private PubSubDriverImpl listener;

  private final RedisServerAddress redisServerAddress;

  private int channelCount = 0;
  Thread thread = null;

  private boolean waitingReconnect = false;

  private final AtomicInteger sessionId;

  /**
   * Constructor.
   * 
   * @param redisServerAddress Redis server addresses
   * @param listener instance of {@link PubSubDriverImpl}
   */
  protected SubscriberClient(
      RedisServerAddress redisServerAddress, PubSubDriverImpl listener) {
    super(true);
    this.redisServerAddress = redisServerAddress;
    this.sessionId = new AtomicInteger(redisServerAddress.getSessionId());
    this.listener = listener;
  }

  /**
   * Starts the Subscriber client.
   */
  protected void start() {
    sessionId.set(redisServerAddress.getSessionId());
    receive();
  }

  protected boolean isStarted() {
    return thread != null && thread.isAlive() && isConnected();
  }

  protected void onMessage(String channel, byte[] message) {
    listener.onMessage(channel, message);
  }

  protected void onPmessage(
      String pattern, String channel, byte[] message) {
    listener.onPmessage(pattern, channel, message);
  }

  protected void onSubscribe(byte[] channel, int channelCount) {
    // TODO: ACK confirmation 
  }

  protected void onUnsubscribe(byte[] channel, int channelCount) {
    // TODO: ACK confirmation 
  }

  protected void onPsubscribe(byte[] channel, int channelCount) {
    // TODO: ACK confirmation 
  }

  protected void onPunsubscribe(byte[] channel, int channelCount) {
    // TODO: ACK confirmation 
  }

  /**
   * Subscribe channels.
   * 
   * @param channels subscribe channels.
   */
  protected void subscribeChannels(Set<String> channels) {
    List<byte[]> safeEncodedChannels = new ArrayList<>();
    for (String channel : channels) {
      safeEncodedChannels.add(SafeEncoder.encode(channel));
    }
    subscribe(safeEncodedChannels.toArray(new byte[channels.size()][]));
  }

  /**
   * Subscribe channel.
   * 
   * @param channel subscribe channel.
   */
  protected void subscribeChannel(String channel) {
    List<String> channels = new ArrayList<>();
    channels.add(channel);
    subscribeChannels(new HashSet<String>(channels));
  }

  /**
   * Unsubscribe channels.
   * 
   * @param channels unsubscribe channels.
   */
  protected void unsubscribeChannels(Set<String> channels) {
    List<byte[]> safeEncodedChannels = new ArrayList<>();
    for (String channel : channels) {
      safeEncodedChannels.add(SafeEncoder.encode(channel));
    }
    unsubscribe(safeEncodedChannels.toArray(new byte[channels.size()][]));
  }

  /**
   * Unsubscribe channel.
   * 
   * @param channel unsubscribe channel.
   */
  protected void unsubscribeChannel(String channel) {
    ArrayList<String> channels = new ArrayList<>();
    channels.add(channel);
    unsubscribeChannels(new HashSet<String>(channels));
  }

  protected void psubscribeChannels(Set<String> patterns) {
    List<byte[]> safeEncodedChannels = new ArrayList<>();
    for (String pattern : patterns) {
      safeEncodedChannels.add(SafeEncoder.encode(pattern));
    }
    psubscribe(safeEncodedChannels.toArray(new byte[patterns.size()][]));
  }

  protected void psubscribeChannel(String pattern) {
    List<String> patterns = new ArrayList<>();
    patterns.add(pattern);
    psubscribeChannels(new HashSet<String>(patterns));
  }

  protected void punsubscribeChannels(Set<String> patterns) {
    List<byte[]> safeEncodedChannels = new ArrayList<>();
    for (String pattern : patterns) {
      safeEncodedChannels.add(SafeEncoder.encode(pattern));
    }
    punsubscribe(safeEncodedChannels.toArray(new byte[patterns.size()][]));
  }

  protected void punsubscribeChannel(String pattern) {
    ArrayList<String> patterns = new ArrayList<>();
    patterns.add(pattern);
    punsubscribeChannels(new HashSet<String>(patterns));
  }

  protected synchronized void unsubscribeAll() {
    unsubscribe();
  }

  protected synchronized void punsubscribeAll() {
    punsubscribe();
  }

  private void receive() {
    txid = LogMessage.getSavedTxid();
    thread = new Thread(new RecieveThread(), "SubscriberClient-receive");
    thread.setPriority(Thread.MAX_PRIORITY);
    thread.setDaemon(true);
    thread.start();
  }

  private class RecieveThread implements Runnable {
    public void run() {
      receiveLoop();
    }
  }

  private void receiveLoop() {
    while (true) {
      try {
        connect(redisServerAddress.getHost(), redisServerAddress.getPort());
      } catch (JedisConnectionException e) {
        // NOP
      }
      if (isConnected()) {
        if (waitingReconnect) {
          listener.onReconnected(sessionId.get());
          waitingReconnect = false;
          //if (log.isDebugEnabled()) {
          //  log.debug("onReconnected({})", sessionId.get());
          //}
        }
        break;
      } else {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "thread error"), e);
        }
      }
    }
    while (true) {
      try {
        List<Object> reply = readObjectListFromInputStream(); // blocking here
        final Object firstObj = reply.get(0);
        if (!(firstObj instanceof byte[])) {
          log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Unknown message type: {}", firstObj));
        }
        final byte[] response = (byte[]) firstObj;
        if (Arrays.equals(SUBSCRIBE.raw, response)) {
          channelCount = ((Long) reply.get(2)).intValue();
          final byte[] channel = (byte[]) reply.get(1);
          onSubscribe(channel, channelCount);
        } else if (Arrays.equals(UNSUBSCRIBE.raw, response)) {
          channelCount = ((Long) reply.get(2)).intValue();
          final byte[] channel = (byte[]) reply.get(1);
          onUnsubscribe(channel, channelCount);
        } else if (Arrays.equals(MESSAGE.raw, response)) {
          final byte[] channel = (byte[]) reply.get(1);
          final byte[] message = (byte[]) reply.get(2);
          onMessage(new String(channel), message);
        } else if (Arrays.equals(PMESSAGE.raw, response)) {
          final byte[] pattern = (byte[]) reply.get(1);
          final byte[] channel = (byte[]) reply.get(2);
          final byte[] message = (byte[]) reply.get(3);
          onPmessage(new String(pattern), new String(channel), message);
        } else if (Arrays.equals(PSUBSCRIBE.raw, response)) {
          channelCount = ((Long) reply.get(2)).intValue();
          final byte[] pattern = (byte[]) reply.get(1);
          onPsubscribe(pattern, channelCount);
        } else if (Arrays.equals(PUNSUBSCRIBE.raw, response)) {
          channelCount = ((Long) reply.get(2)).intValue();
          final byte[] pattern = (byte[]) reply.get(1);
          onPunsubscribe(pattern, channelCount);
        } else {
          log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Unsupported message type: {}", firstObj));
        }
      } catch (JedisConnectionException e) {
        waitingReconnect = true;
        listener.onDisconnected(sessionId.get());
        //if (log.isDebugEnabled()) {
        //  log.debug("onDisconnected({})", sessionId.get());
        //}
        break;
      }
    }
  }
}
