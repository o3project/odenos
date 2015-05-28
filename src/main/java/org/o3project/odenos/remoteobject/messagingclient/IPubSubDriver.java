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

import java.io.Closeable;
import java.net.ProtocolException;
import java.util.Set;

/**
 * Pubsub driver interface. 
 */
public interface IPubSubDriver extends Closeable {

  /**
   * Starts services. 
   */
  void start();

  /**
   * Closes.
   */
  void close();

  /**
   * Checks if the implementation class is started. 
   *
   * @return boolean isStarted
   */
  boolean isStarted();

  /**
   * Subscribes channels. 
   *
   * @param channels Set String
   */
  void subscribeChannels(Set<String> channels);

  /**
   * Subscribe a channel.
   *
   * @param channel String
   */
  void subscribeChannel(String channel);

  /**
   * Unsubscribes channels.
   *
   * @param channels Set String
   */
  void unsubscribeChannels(Set<String> channels);

  /**
   * Unsubscribe a channel. 
   *
   * @param channel String
   */
  void unsubscribeChannel(String channel);

  /**
   * Unsubscribe all the channels.
   */
  void unsubscribeAll();

  /**
   * Subscribes channels.
   *
   * @param patterns Set String
   */
  void psubscribeChannels(Set<String> patterns);

  /**
   * Subscribe a channel.
   *
   * @param pattern String
   */
  void psubscribeChannel(String pattern);

  /**
   * Unsubscribes channels.
   *
   * @param patterns Set String
   */
  void punsubscribeChannels(Set<String> patterns);

  /**
   * Unsubscribe a channel. 
   *
   * @param pattern String
   */
  void punsubscribeChannel(String pattern);

  /**
   * Unsubscribe all the channels.
   */
  void punsubscribeAll();

  /**
   * Publishes a message to listeners.
   *
   * @param channel String
   * @param data  byte[]
   */
  void publish(String channel, byte[] data);

  /**
   * Checks if the channel exists.
   * 
   * @param channel Channel to be checked
   * @return true if the channel exists
   * @throws ProtocolException operation failed
   */
  boolean channelExist(String channel) throws ProtocolException;
  
  /**
   * This method provides extended features for future enhancements.
   *
   * @param name String
   * @param <K> K
   * @param <V> V
   * @return K, V K,V
   */
  <K, V> IMultiMap<K, V> getMultiMap(String name);

}
