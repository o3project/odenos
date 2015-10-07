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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

/**
 * Redis server addresses holder. 
 */
public class RedisServerAddress {

  private static final Logger log = LogManager.getLogger(RedisServerAddress.class);

  private List<SimpleImmutableEntry<String, Integer>> pubsubAddresses =
      new ArrayList<>();

  private boolean pubsubAddressA = false;

  private String host = null;
  private int port = 0;
  
  private int sessionId = 0;

  /**
   * Constructor.
   * 
   * @param host Redis server host name or port number
   * @param port Redis server port number
   * @param hostB Redis server host name or port number (backup)
   * @param portB Redis server port number (backup)
   */
  public RedisServerAddress(String host, int port, String hostB, int portB) {
    this.host = host;
    this.port = port;
    SimpleImmutableEntry<String, Integer> addressA =
        new SimpleImmutableEntry<>(host, port);
    SimpleImmutableEntry<String, Integer> addressB = addressA;
    if (hostB != null && portB > 0) {
      addressB = new SimpleImmutableEntry<>(hostB, portB);
    }
    pubsubAddresses.add(addressA);
    pubsubAddresses.add(addressB);
  }

  /**
   * Next sessionId, address (host or IP address) and port number.
   */
  protected synchronized void next() {
    SimpleImmutableEntry<String, Integer> entry;
    if (pubsubAddressA) {
      if (pubsubAddresses.get(1).getKey() != null) {
        pubsubAddressA = false;
      }
      entry = pubsubAddresses.get(0);
      host = entry.getKey();
      port = entry.getValue();
    } else {
      entry = pubsubAddresses.get(1);
      host = entry.getKey();
      port = entry.getValue();
      pubsubAddressA = true;
    }
    sessionId++;
    if (log.isDebugEnabled()) {
      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "sessionId: {}, host: {}, port: {}", sessionId, host, port));
    }
  }
  
  /**
   * Returns session ID that is used for identifying a pair of
   * {@link PublisherClient} and {@link SubscriberClient}.
   * 
   * 
   * @return sessionId
   */
  protected synchronized int getSessionId() {
    return sessionId;
  }

  /**
   * Returns Redis server host name or IP address.
   * 
   * @return host name or IP address
   */
  protected synchronized String getHost() {
    return host;
  }

  /**
   * Returns Redis server port number.
   * 
   * @return Redis server port number.
   */
  protected synchronized int getPort() {
    return port;
  }
}
