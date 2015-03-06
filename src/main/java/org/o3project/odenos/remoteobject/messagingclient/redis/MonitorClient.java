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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class MonitorClient extends RedisClient {

  private static final Logger log = LoggerFactory.getLogger(MonitorClient.class);

  private Thread receiveThread = null;

  public MonitorClient(final String host, final int port) {
    super(true); // TCP keep-alive is set.
    connect(host, port);
  }
  
  public synchronized void publish(byte[] channel, byte[] message) {
    super.publish(channel, message);
  }

  protected synchronized void setClientName(String name) {
    setClientName(name.getBytes());
  }

  protected void receive() {
    receiveThread = new Thread(new ReceiveThread(), "MonitorClient-receive");
    receiveThread.start();
  }

  private class ReceiveThread implements Runnable {
    @Override
    public void run() {
      Object object;
      while (true) {
        try {
          object = read();
          if (log.isDebugEnabled()) {
            log.debug("reply from Redis server: {}", object.toString());
          }
        } catch (JedisConnectionException e) {
          log.error("connection error", e);
          break;
        }
      }
    }
  }

}