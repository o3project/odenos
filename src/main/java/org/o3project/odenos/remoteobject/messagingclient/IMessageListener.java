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

public interface IMessageListener {

  /**
   * A message has just been received from pubsub server. 
   * 
   * @param channel channel (aka "topic")
   * @param message message in the form of byte array
   */
  public void onMessage(String channel, byte[] message);

  /**
   * A message has just been received from pubsub server. 
   * 
   * @param pattern pattern
   * @param channel channel (aka "topic")
   * @param message message in the form of byte array
   */
  public void onPmessage(String pattern, String channel, byte[] message);

  /**
   * Connectivity to pubsub server has just been resumed. 
   */
  public void onReconnected();

  /**
   * Connectivity to pubsub server has just been lost.
   */
  public void onDisconnected();

}
