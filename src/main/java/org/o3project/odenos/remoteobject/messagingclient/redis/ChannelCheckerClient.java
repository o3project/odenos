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

import java.net.ProtocolException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Redis channel checker client.
 * 
 * <p>
 * This class provides a method to check if a channel really
 * exists on Redis server. 
 * 
 * @see RedisClient
 * @see PubSubDriverImpl
 * @see org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher
 */
public class ChannelCheckerClient extends RedisClient {

  private static final Logger log = LogManager.getLogger(ChannelCheckerClient.class);

  private final RedisServerAddress redisServerAddress;
  
  /**
   * Constructor.
   * 
   * @param redisServerAddress redisServerAddress
   */
  protected ChannelCheckerClient(RedisServerAddress redisServerAddress) {
    super(false);  // TCP keep-alive is not set.
    this.redisServerAddress = redisServerAddress;
  }

  /**
   * Checks if channel exist.
   * 
   * @param channel channel.
   * @return true if exist, false if not exist.
   */
  protected synchronized boolean channelExist(String channel) {
    connect(redisServerAddress.getHost(), redisServerAddress.getPort()); // Calls the method, since TCP keep alive is disabled. 
    pubsubNumsub(channel.getBytes());
    boolean exist = false;
    try {
      exist = (readPubsubNumsubReply(channel) > 0) ? true : false;
    } catch (ProtocolException e) {
      // TODO: exception handling
      log.error("Redis protocol error", e);
    }
    return exist;
  }

  /**
   * Checks if SystemManager exist.
   * 
   * @param systemManagerId system manager id.
   * @return true if exist, false if not exist.
   */
  protected synchronized boolean systemManagerExist(String systemManagerId) {
    connect(redisServerAddress.getHost(), redisServerAddress.getPort());
    getClientList();
    List<String> clients = readGetClientListReply();
    for (String client : clients) {
      StringTokenizer tokenizer = new StringTokenizer(client.trim(), " ");
      while (tokenizer.hasMoreTokens()) {
        String keyValue = tokenizer.nextToken();
        if (keyValue.startsWith("name=")) {
          if (keyValue.substring(5).equals(systemManagerId)) {
            return true;
          } else {
            break;
          }
        }
      }
    }
    return false;
  }
}
