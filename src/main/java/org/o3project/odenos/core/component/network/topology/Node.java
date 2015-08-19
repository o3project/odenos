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

package org.o3project.odenos.core.component.network.topology;

import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.packer.Packer;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Switch Node data class.
 *
 */
public class Node extends BaseObject implements Cloneable {
  private static final int MSG_NUM_MIN = 1;
  private static final int MSG_NUM_MAX = 5;
  private String nodeId = null;
  private Map<String, Port> ports = new HashMap<String, Port>();
  private Map<String, String> attributes = new HashMap<String, String>();

  /* NetworkElements */
  public static final String TYPE = "type";
  public static final String VERSION = "version";
  public static final String NODE_ID = "node_id";
  public static final String PORTS = "ports";
  public static final String ATTRIBUTES = "attributes";

  /* AttrbuteElements */
  public static final String OPER_STATUS = "oper_status";
  public static final String PHYSICAL_ID = "physical_id";
  public static final String VENDOR = "vendor";


  /**
   * Constructor.
   */
  public Node() {
    initElements(this.INITIAL_VERSION, null, this.ports, null);
  }

  /**
   * Constructor.
   * @param nodeId node ID.
   */
  public Node(String nodeId) {
    initElements(this.INITIAL_VERSION, nodeId, this.ports, null);
  }

  /**
   * Constructor.
   * @param version string of version.
   * @param nodeId node ID.
   */
  public Node(String version, String nodeId) {
    initElements(version, nodeId, this.ports, null);
  }

  /**
   * Constructor.
   * @param version string of version.
   * @param nodeId node id that is unique in the Network.
   * @param ports Set of ports in this Node.
   * @param attributes map of attributes.
   */
  public Node(String version, String nodeId,
      Map<String, Port> ports,
      Map<String, String> attributes) {

    initElements(version, nodeId, ports, attributes);
  }

  protected void initElements(
      String version, String nodeId,
      Map<String, Port> ports,
      Map<String, String> attributes) {

    this.setType("Node");
    this.setVersion(version);
    this.setId(nodeId);
    this.ports = ports;

    if(attributes != null) {
      this.putAttributes(attributes);
    }
    if(!this.isAttribute(OPER_STATUS)) {
      this.putAttribute(OPER_STATUS, STATUS_UP);
    }
    if(!this.isAttribute(PHYSICAL_ID)) {
      this.putAttribute(PHYSICAL_ID, nodeId);
    }
    if(!this.isAttribute(VENDOR)) {
      this.putAttribute(VENDOR, "unknown");
    }
  }

  /**
   * Constructor.
   * @param msg node message.
   */
  public Node(Node msg) {
    this(msg.getId());
    this.setVersion(msg.getVersion());
    this.putAttributes(new HashMap<String, String>(msg.getAttributes()));
    Map<String, Port> ports = new HashMap<String, Port>();
    if (msg.ports != null) {
      for (Entry<String, Port> entry : msg.ports.entrySet()) {
        ports.put(entry.getKey(), new Port(entry.getValue()));
      }
    }
    this.ports = ports;
  }

  /**
   * Confirm the parameter.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    if (this.getId() == null
        || this.getType() == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a node ID.
   * @return node ID.
   */
  public String getId() {
    return nodeId;
  }

  /**
   * Sets a node ID.
   * @param nodeId node ID.
   */
  public void setId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Get ports.
   * @return map of ports.
   */
  public Map<String, Port> getPortMap() {
    if (ports == null) {
      return new HashMap<String, Port>();
    }
    return ports;
  }

  private String getUniquePortId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (ports.get(id) != null);

    return id;
  }

  /**
   * Remove all ports attached to this node.
   */
  public void clearPorts() {
    ports.clear();
  }

  /**
   * Create a port.
   * @param msg port message.
   * @return port instance.
   */
  public Port createPort(Port msg) {
    String portId;
    if (msg == null || msg.getId() == null) {
      // automatic numbering if node_id is undefined
      portId = getUniquePortId();
    } else {
      portId = msg.getId();
    }
    Port port = getPort(portId);
    if (port == null) {
      port = new Port(portId);
      port.setVersion(INITIAL_VERSION);
      ports.put(port.getId(), port);
    }
    port.setNode(this.getId());
    if (msg != null) {
      port.putAttributes(msg.getAttributes());
    }
    port.updateVersion();
    this.updateVersion();

    return port;
  }

  /**
   * Returns a port.
   * @param portId port ID.
   * @return port associated to the port ID.
   */
  public Port getPort(String portId) {
    return ports.get(portId);
  }

  /**
   * Set ports.
   * @param ports map of ports.
   */
  public void setPorts(Map<String, Port> ports) {
    clearPorts();
    Map<String, Port> newPorts = new HashMap<String, Port>();
    if (ports != null) {
      for (Entry<String, Port> entry : ports.entrySet()) {
        newPorts.put(entry.getKey(), new Port(entry.getValue()));
      }
    }
    this.ports = newPorts;
  }

  /**
   * Delete a port.
   * @param portId port ID.
   * @return removed port.
   */
  public Port deletePort(String portId) {
    return deletePort(getPort(portId));
  }

  /**
   * Delete a port.
   * @param port deleted port.
   * @return deleted the port.
   */
  public Port deletePort(Port port) {
    if (port == null) {
      return null;
    }

    // Still has link(s)
    if (port.getInLink() != null || port.getOutLink() != null) {
      return null;
    }

    Port ret = ports.remove(port.getId());
    return ret;
  }

  /**
   * Get message of ports.
   * @param query queries string.
   * @return map of ports.
   */
  public Map<String, Port> getPortMessages(PortQuery query) {
    Map<String, Port> ports = new HashMap<String, Port>();
    for (Port port : this.ports.values()) {
      if (query.matchExactly(port)) {
        ports.put(port.getId(), port);
      }
    }
    return ports;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();

    if (size < MSG_NUM_MIN || MSG_NUM_MAX < size) {
      throw new IOException();
    }

    while (size-- > 0) {
      switch (upk.readString()) {
        case TYPE:
          this.setType(upk.readString());
          break;
        case VERSION:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            setVersion("0");
          } else {
            setVersion(upk.readString());
          }
          break;
        case NODE_ID:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            nodeId = null;
          } else {
            nodeId = upk.readString();
          }
          break;
        case PORTS:
          ports.clear();
          // ports.addAll(upk.read(tList(TString)));
          int portsSize = upk.readMapBegin();
          while (portsSize-- > 0) {
            String portId = upk.readString();
            Port port = upk.read(Port.class);
            ports.put(portId, port);
          }
          upk.readMapEnd();
          break;
        case ATTRIBUTES:
          putAttributes(upk.read(tMap(TString, TString)));
          break;
        default:
          break;
      }
    }
    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeMapBegin(MSG_NUM_MAX);

    pk.write(TYPE);
    pk.write(getType());

    pk.write(VERSION);
    pk.write(getVersion());

    pk.write(NODE_ID);
    pk.write(nodeId);

    pk.write(PORTS);
    pk.write(ports);

    pk.write(ATTRIBUTES);
    pk.write(getAttributes());

    pk.writeMapEnd();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Node)) {
      return false;
    }

    Node nodeMessage = (Node) obj;

    if (nodeMessage.getType().equals(this.getType())
        && nodeMessage.getVersion().equals(this.getVersion())
        && nodeMessage.getId().equals(this.nodeId)
        && nodeMessage.getPortMap().equals(this.ports)
        && nodeMessage.getAttributes().equals(this.getAttributes())) {
      return true;
    }

    return false;
  }

  @Override
  public Node clone() {
    return new Node(this);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append(VERSION, getVersion());
    sb.append(NODE_ID, nodeId);
    sb.append(PORTS, ports);
    sb.append(ATTRIBUTES, getAttributes());

    return sb.toString();

  }
}
