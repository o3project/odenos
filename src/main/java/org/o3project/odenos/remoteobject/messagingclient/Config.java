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

import java.util.Collection;
import java.util.EnumSet;

/**
 * {@link MessageDispatcher} configuration.
 * 
 * @see ConfigBuilder
 */
public interface Config {

  /**
   * {@link MessageDispatcher} running mode.
   */
  public static enum MODE {
    // Sends local requests and local events to pubsub server.
    // This is mainly for a debugging purpose to monitor all messages.
    LOOPBACK_DISABLED,
    // This is mainly for a debugging purpose to include
    // source ObjectId for Request/Response and Event
    INCLUDE_SOURCE_OBJECT_ID,
    // Reflects a received request, response or event to pubsub server
    // for a message monitoring purpose.
    REFLECT_MESSAGE_TO_MONITOR,
    // Re-SUBSCRIBEs onReconnected()
    RESEND_SUBSCRIBE_ON_RECONNECTED,
    // Bridged pubsub client 
    PUBSUB_BRIDGED,
    // Outputs a received request, response or event to logger
    OUTPUT_MESSAGE_TO_LOGGER
  };

  public String getSystemManagerId();

  public String getEventManagerId();

  public String getHost();

  public int getPort();

  public String getHostB();

  public int getPortB();

  public String getSourceDispatcherId();

  public int getPublisherQueueSize();
  
  public String getPubSubDriverImpl();

  public int getRemoteTransactionsMax();

  public int getRemoteTransactionsInitialTimeout();

  public int getRemoteTransactionsFinalTimeout();
  
  public boolean getSystemManagerStatusCheck();
  
  public EnumSet<MODE> getMode();
  
  public String getPublishScript();
  
  public Collection<String> getObjectIds();
}
