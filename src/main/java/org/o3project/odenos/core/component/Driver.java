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

package org.o3project.odenos.core.component;

import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

/**
 * Driver class.
 *
 */
public abstract class Driver extends Logic {
  private static final Logger log = LogManager.getLogger(Driver.class);

  public static final String ORIGINAL = "original";

  /**
   * Constructor.
   * @param objectId object ID.
   * @param baseUri base URI.
   * @param dispatcher Message Dispatcher object.
   * @throws Exception if an error occurs.
   * @deprecated @see #Driver(String, MessageDispatcher)
   */
  @Deprecated
  public Driver(final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, baseUri, dispatcher);
    log.info(LogMessage.buildLogMessage(10057, LogMessage.getTxid(), "created."));
  }

  /**
   * Constructor.
   * @param objectId object ID.
   * @param dispatcher Message Dispatcher object.
   * @throws Exception if an error occurs.
   */
  public Driver(final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    log.info(LogMessage.buildLogMessage(10057, LogMessage.getTxid(), "created."));
  }

  /**
   * Returns a type of component.
   *
   * @return type of component.
   */
  @Override
  protected final String getSuperType() {
    return Driver.class.getSimpleName();
  }

  /**
   * Get Connection Type which can be connected to Component.
   *
   * @return Connection Types of Component.
   */
  @Override
  protected String getConnectionTypes() {
    // <connection type>:<connection number>,...
    return String.format("%s:1", ORIGINAL);
  }

}
