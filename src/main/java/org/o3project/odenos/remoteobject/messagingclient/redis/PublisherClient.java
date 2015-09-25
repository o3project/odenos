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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis publisher client.
 * 
 * <p>
 * This class has two threads running in parallel:
 * a send thread and a receive thread.
 * 
 * <p>
 * The send thread takes a task out of publisherQueue and sends
 * it to Redis server.
 * 
 * <p>
 * The receive thread runs in background just to get a reply
 * out of output stream.
 * 
 * 
 * @see RedisClient
 * @see PubSubDriverImpl
 * @see org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher
 */
public class PublisherClient extends RedisClient {

  private static final Logger log = LogManager.getLogger(PublisherClient.class);

  private Thread sendThread = null;
  private Thread receiveThread = null;

  private final PubSubDriverImpl listener;
  private boolean waitingReconnect = false;

  private final RedisServerAddress redisServerAddress;

  private ArrayBlockingQueue<PublishData> publisherQueue = null;

  // Lua script to publish an event to both local and remote Redis servers.
  private static final byte[] ONE = "1".getBytes();
  private byte[] sha1 = null;
  private byte[] publishScript = null;
  private final boolean bridged;

  private final AtomicInteger sessionId;

  // Java data object representing a task in publisher queue
  private class PublishData {
    protected byte[] channel;
    protected byte[] data;

    public PublishData(byte[] channel, byte[] data) {
      this.channel = channel;
      this.data = data;
    }
  }

  /**
   * Constructor.
   * 
   * @param redisServerAddress Redis server addresses
   * @param publisherQueueSize publisher queue size
   * @param listener instance of {@link PubSubDriverImpl}
   * @param bridgeClient true if this client is bridged 
   * @param publishScript lua script
   */
  protected PublisherClient(RedisServerAddress redisServerAddress, int publisherQueueSize,
      PubSubDriverImpl listener,
      boolean bridgeClient, String publishScript) {
    super(true); // TCP keep-alive is set.
    this.redisServerAddress = redisServerAddress;
    this.sessionId = new AtomicInteger(redisServerAddress.getSessionId());
    this.listener = listener;
    this.bridged = bridgeClient;
    if (publishScript != null) {
      this.publishScript = publishScript.getBytes();
      try {
        this.sha1 = calcSha1(this.publishScript);
      } catch (NoSuchAlgorithmException e) {
        log.error(LogMessage.buildLogMessage(50004, LogMessage.getTxid(), "SHA-1 algorithm unavailable")); // This will never happen anyway.
      }
    }
    publisherQueue = new ArrayBlockingQueue<>(publisherQueueSize);
    send();
  }

  /**
   * Starts the Publisher client.
   */
  protected void start() {
    sessionId.set(redisServerAddress.getSessionId());
    receive();
  }

  /**
   * Checks if Publisher Client is started.
   * 
   * @return true if started, false if not started 
   */
  protected boolean isStarted() {
    return sendThread != null && receiveThread != null
        && sendThread.isAlive() && receiveThread.isAlive()
        && isConnected();
  }

  /**
   * 
   * @param channel channel.
   * @param data request data.
   */
  protected void publish(String channel, byte[] data) {
    try {
      publisherQueue.put(new PublishData(SafeEncoder.encode(channel), data));
    } catch (InterruptedException e) {
      log.error(LogMessage.buildLogMessage(50005, LogMessage.getTxid(), "cannot put a request in publish queue"));
    }
  }

  protected synchronized void setClientName(String name) {
    setClientName(name.getBytes());
  }

  protected void send() {
    sendThread = new Thread(new SendThread(), "PublisherClient-send");
    sendThread.setPriority(Thread.MAX_PRIORITY - 2);
    sendThread.setDaemon(true);
    sendThread.start();
  }

  protected void receive() {
    receiveThread = new Thread(new ReceiveThread(), "PublisherClient-receive");
    receiveThread.start();
  }

  protected class SendThread implements Runnable {
    @Override
    public void run() {
      while (true) {
        int count = publisherQueue.size();
        if (count <= 1) {
          PublishData publishData;
          try {
            publishData = publisherQueue.take(); // blocking here
            if (bridged) {
              evalsha(sha1, ONE, publishData.channel, publishData.data);
            } else {
              publish(publishData.channel, publishData.data);
            }
          } catch (InterruptedException e) {
            log.debug(e.getMessage());
          }
        } else { // TODO: Redis multi, publish and exec
          while (count > 0) {
            PublishData publishData = publisherQueue.poll();
            if (bridged) {
              evalsha(sha1, ONE, publishData.channel, publishData.data);
            } else {
              publish(publishData.channel, publishData.data);
            }
            count--;
          }
        }
      }
    }
  }

  private class ReceiveThread implements Runnable {
    @Override
    public void run() {
      Object object;
      while (true) {
        try {
          connect(redisServerAddress.getHost(), redisServerAddress.getPort());
        } catch (JedisConnectionException e) {
          // NOP
        }
        if (isConnected()) {
          try {
            if (bridged) {
              if (sha1 == null || publishScript == null) {
                log.error(LogMessage.buildLogMessage(50006, LogMessage.getTxid(), "publish script is not set"));
              } else {
                scriptExists(sha1);
                List<Object> list = readObjectListFromInputStream();
                Long exists = (Long) list.get(0);
                if (exists == 0) { // Checks if the lua script exists on the server.
                  scriptLoad(publishScript); // Loads the lua script to the server.
                }
              }
            }
            if (waitingReconnect) {
              listener.onReconnected(sessionId.get());
              waitingReconnect = false;
              //if (log.isDebugEnabled()) {
              //  log.debug("onReconnected({})", sessionId.get());
              //}
            }
            break;
          } catch (Exception e) {
            log.error(LogMessage.buildLogMessage(50007, LogMessage.getTxid(), "internal error"), e);
          }
        } else {
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            log.error(LogMessage.buildLogMessage(50003, LogMessage.getTxid(), "thread error"), e);
          }
        }
      }
      while (true) {
        try {
          object = read();
          if (log.isDebugEnabled()) {
            log.debug("reply from Redis server: {}", object.toString());
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

  // Calculates SHA1 digest
  private static byte[] calcSha1(byte[] script) throws NoSuchAlgorithmException {
    StringBuffer stringBuffer = new StringBuffer();
    MessageDigest messageDigest;
    messageDigest = MessageDigest.getInstance("SHA-1");
    byte[] result = messageDigest.digest(script);
    for (int i = 0; i < result.length; i++) {
      stringBuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
    }
    return stringBuffer.toString().getBytes();
  }
}
