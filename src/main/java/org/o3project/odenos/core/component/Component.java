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

import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

/**
 * Component.
 *
 */
public abstract class Component extends RemoteObject {

  public static final String CONN_ADD = "add";
  public static final String CONN_UPDATE = "update";
  public static final String CONN_DELETE = "delete";
  public static final String NODE_CHANGED = "NODE_CHANGED";
  public static final String PORT_CHANGED = "PORT_CHANGED";
  public static final String LINK_CHANGED = "LINK_CHANGED";
  public static final String FLOW_CHANGED = "FLOW_CHANGED";
  public static final String IN_PACKET_ADDED = "IN_PACKET_ADDED";
  public static final String OUT_PACKET_ADDED = "OUT_PACKET_ADDED";
  public static final String STATUS_UP = "UP";
  public static final String STATUS_DOWN = "DOWN";


  /**
   * NetworkElements class.
   *
   */
  public class NetworkElements {
    public static final String TYPE = "type";
    public static final String VERSION = "version";
    public static final String NODE_ID = "node_id";
    public static final String PORT_ID = "port_id";
    public static final String LINK_ID = "link_id";
    public static final String FLOW_ID = "flow_id";
    public static final String IN_LINK = "in_link";
    public static final String OUT_LINK = "out_link";
    public static final String SRC_NODE = "src_node";
    public static final String SRC_PORT = "src_port";
    public static final String DST_NODE = "dst_node";
    public static final String DST_PORT = "dst_port";
    public static final String OWNER = "owner";
    public static final String ENABLED = "enabled";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";
  }


  /**
   * AttrElements class.
   *
   */
  public class AttrElements {
    public static final String ATTRIBUTES = "attributes";
    public static final String ADMIN_STATUS = "admin_status";
    public static final String OPER_STATUS = "oper_status";
    public static final String PHYSICAL_ID = "physical_id";
    public static final String VENDOR = "vendor";
    public static final String MAX_BANDWIDTH = "max_bandwidth";
    public static final String UNRESERVED_BANDWIDTH = "unreserved_bandwidth";
    public static final String IS_BOUNDARY = "is_boundary";
    public static final String COST = "cost";
    public static final String LATENCY = "latency";
    public static final String BANDWIDTH = "bandwidth";
    public static final String REQ_LATENCY = "req_latency";
    public static final String REQ_BANDWIDTH = "req_bandwidth";
    public static final String ESTABLISHMENT_STATUS = "establishment_status";
  }

  /**
   * Component Constructor.
   * @param objectId ID for Object
   * @param dispatcher MessageDispatcher
   */
  public Component(final String objectId, final MessageDispatcher dispatcher) {
    super(objectId, dispatcher);
    this.setSuperType();
    this.setDescription();
    this.setConnectionTypes();
    resetEventSubscription();
  }

  /**
   * Get Super Type of Component. Need to Implement at Inheritance Logic Component.
   *
   * @return Super Type of Component
   */
  protected abstract String getSuperType();

  /**
   * Get Description of Component. Need to Implement at Inheritance Logic Component.
   *
   * @return Description of Component
   */
  protected abstract String getDescription();

  /**
   * Get Connection Type which can be connected to Component.
   *
   * @return Connection Types of Component.
   */
  protected String getConnectionTypes() {
    return "";
  }

  /**
   * Set SuperType to Self ObjectProperty.
   */
  protected final void setSuperType() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.OBJECT_SUPER_TYPE,
        this.getSuperType());
  }

  /**
   * Set Description to Self ObjectProperty.
   */
  protected final void setDescription() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.DESCRIPTION,
        this.getDescription());
  }

  /**
   * Set Connection Types to Self ObjectProperty.
   */
  protected final void setConnectionTypes() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.CONNECTION_TYPES,
        this.getConnectionTypes());
  }

  protected void resetEventSubscription() {
    if (this.messageDispatcher == null) {
      return;
    }

    eventSubscription.clearFilter();
    eventSubscription.addFilter(
        this.messageDispatcher.getSystemManagerId(),
        ComponentConnectionChanged.TYPE);
    try {
      this.applyEventSubscription();
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }
}
