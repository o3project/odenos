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

package org.o3project.odenos.core.manager.system;

import org.o3project.odenos.core.component.LogicIF;
import org.o3project.odenos.core.component.NetworkIF;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObjectIF;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.util.Map;

public class SystemManagerIF extends RemoteObjectIF {
  private static final String T_NETWORK = "Network";

  public static final String PATH_OBJECTS = "objects/";
  public static final String PATH_COMP_MGRS = "component_managers/";
  public static final String PATH_EVENT_MGR = "event_manager/";
  public static final String PATH_COMP_TYPES = "component_types/";
  public static final String PATH_COMPS = "components/";
  public static final String PATH_CONNECTIONS = "connections/";

  @Deprecated
  public SystemManagerIF(final MessageDispatcher dispatcher) {
    super(dispatcher, dispatcher.getSystemManagerId());
  }

  public SystemManagerIF(final String sourceDispatcherId, final MessageDispatcher dispatcher) {
    super(sourceDispatcherId, dispatcher);
  }

  public final NetworkIF createNetwork(final String id) {
    this.createComponent(T_NETWORK, id);
    //return new NetworkIF(this.dispatcher(), id);
    return new NetworkIF(getSourceObjectId(), this.dispatcher());
  }

  public final LogicIF createLogic(final String type, final String id) {
    this.createComponent(type, id);
    //return new LogicIF(this.dispatcher(), id);
    return new LogicIF(getSourceObjectId(), this.dispatcher());
  }

  /**
   * connect to component connection.
   * @param network Network Interface.
   * @param logic Remote Object Interface.
   * @param type connection type.
   * @return component connection.
   */
  public final ComponentConnection connect(NetworkIF network, RemoteObjectIF logic, String type) {
    ComponentConnection conn = new ComponentConnectionLogicAndNetwork(
        null, type, ComponentConnection.State.INITIALIZING, logic.id(), network.id());
    return this.createConnection(conn);
  }

  /* API for Component Managers */

  public final ObjectProperty addComponentManager(final ObjectProperty body) {
    return this.put(PATH_COMP_MGRS + body.getObjectId(), body).getBody2(ObjectProperty.class);
  }

  public final ObjectPropertyList getComponentManagers() {
    return this.get(PATH_COMP_MGRS).getBody2(ObjectPropertyList.class);
  }

  public final ObjectProperty getComponentManager(final String id) {
    return this.get(PATH_COMP_MGRS + id).getBody2(ObjectProperty.class);
  }

  public final ObjectProperty deleteComponentManager(final String id) {
    return this.delete(PATH_COMP_MGRS + id).getBody2(ObjectProperty.class);
  }

  /* API for Event Managers */

  public final ObjectProperty getEventManager() {
    return this.get(PATH_EVENT_MGR).getBody2(ObjectProperty.class);
  }

  /* API for ComponentType */

  public final ComponentTypesHash getComponentTypes() {
    return this.get(PATH_COMP_TYPES).getBody2(ComponentTypesHash.class);
  }

  public final ObjectPropertyList getComponentType(final String type) {
    return this.get(PATH_COMP_TYPES + type).getBody2(ObjectPropertyList.class);
  }

  /* API for Component */

  public final ObjectProperty createComponent(final ObjectProperty body) {
    return this.post(PATH_COMPS, body).getBody2(ObjectProperty.class);
  }

  public final ObjectProperty createComponent(final String type, final String id) {
    return post(PATH_COMPS, new ObjectProperty(type, id)).getBody2(ObjectProperty.class);
  }

  public final ObjectPropertiesHash getComponents() {
    return this.get(PATH_COMPS).getBody2(ObjectPropertiesHash.class);
  }

  public final ObjectProperty getComponent(final String id) {
    return this.get(PATH_COMPS + id).getBody2(ObjectProperty.class);
  }

  public final ObjectProperty deleteComponent(final String id) {
    return this.delete(PATH_COMPS + id).getBody2(ObjectProperty.class);
  }

  /* API for ComponentConnection */

  public final ComponentConnection createConnection(final ComponentConnection body) {
    return this.post(PATH_CONNECTIONS, body).getBody2(ComponentConnection.class);
  }

  public final Map<String, ComponentConnection> getConnections() {
    return this.get(PATH_CONNECTIONS).getBodyAsMap(ComponentConnection.class);
  }

  public final ComponentConnection getConnection(final String id) {
    return this.get(PATH_CONNECTIONS + id).getBody2(ComponentConnection.class);
  }

  public final ComponentConnection updateConnection(final ComponentConnection body) {
    return this.put(PATH_CONNECTIONS + body.getObjectId(), body)
        .getBody2(ComponentConnection.class);
  }

  public final ComponentConnection deleteConnection(final String id) {
    return this.delete(PATH_CONNECTIONS + id).getBody2(ComponentConnection.class);
  }
}
