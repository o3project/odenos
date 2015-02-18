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

package org.o3project.odenos.remoteobject.actor;

import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

/**
 * A wrapper class of {@link org.o3project.odenos.remoteobject.message.OdenosMessage}. 
 * 
 * <p>
 * This class contains {@link org.o3project.odenos.remoteobject.message.Request} or
 * {@link org.o3project.odenos.remoteobject.message.Event} in the body.
 */
public class Mail {

  public final int serial;  // Serial number for incoming messages.
  public final int sno;     // Serial number for outgoing request messages.
  public final String to;
  public final String from;
  public final MessageDispatcher via;
  public final Request request; // Note: null if this mail contains an event.
  public final Event event;     // Note: null if this mail contains a request.
  
  /**
   * Constructor.
   * 
   * @param serial serial number for incoming messages.
   * @param sno sequence number for outgoing request messages.
   * @param to remote object to send request.
   * @param from remote object to receive response.
   * @param via message dispatcher.
   * @param request request.
   * @param event event.
   */
  public Mail(int serial, int sno, String to, String from,
      MessageDispatcher via, Request request, Event event) {
    this.serial = serial;
    this.sno = sno;
    this.to = to;
    this.from = from;
    this.via = via;
    this.request = request;
    this.event = event;
  }

}
