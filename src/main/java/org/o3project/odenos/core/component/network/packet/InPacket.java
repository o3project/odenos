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

package org.o3project.odenos.core.component.network.packet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.Arrays;
import java.util.Map;

/**
 * Packet IN data.
 *
 */
public class InPacket extends Packet {
  private String nodeId;
  private String portId;
  private BasicFlowMatch header;
  public byte[] data;

  /**
   * Constructor.
   */
  public InPacket() {
    super();
  }

  /**
   * Constructor.
   * @param packetId Packet ID..
   * @param nodeId node id to input the packet.
   * @param portId port id to input the packet.
   * @param data payload.
   * @param attributes map of attributes.
   */
  public InPacket(String packetId, String nodeId, String portId,
      byte[] data, Map<String, String> attributes) {
    super(packetId, attributes);
    this.nodeId = nodeId;
    this.portId = portId;
    this.data = data;
  }

  /**
   * Constructor.
   * @param packetId Packet ID.
   * @param nodeId node id to input the packet.
   * @param portId port id to input the packet.
   * @param data payload.
   * @param attributes attributes.
   * @param header header information.
   */
  public InPacket(String packetId, String nodeId, String portId,
      byte[] data, Map<String, String> attributes,
      BasicFlowMatch header) {
    this(packetId, nodeId, portId, data, attributes);
    this.header = header;
    if (header == null) {
      this.header = new BasicFlowMatch();
    }
  }

  /**
   * Constructor.
   * @param msg message of Packet IN.
   */
  public InPacket(InPacket msg) {
    this(msg.getPacketId(), msg.getNodeId(), msg.getPortId(),
        msg.getData(), msg.getAttributes(), msg.getHeader());
  }

  @Override
  public String getType() {
    return "InPacket";
  }

  /**
   * Returns a header info.
   * @return header info.
   */
  public BasicFlowMatch getHeader() {
    return this.header;
  }

  /**
   * Sets a header info.
   * @param header header info.
   */
  public void setHeader(BasicFlowMatch header) {
    this.header = header;
  }

  /**
   * Returns a node ID.
   * @return node ID.
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets a node ID.
   * @param nodeId node ID.
   */
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Returns a port ID.
   * @return port ID.
   */
  public String getPortId() {
    return portId;
  }

  /**
   * Sets a port ID.
   * @param portId port ID.
   */
  public void setPortId(String portId) {
    this.portId = portId;
  }

  /**
   * Return payload.
   * @return payload.
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Set payload.
   * @param data payload.
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof InPacket)) {
      return false;
    }
    if (!(super.equals(obj))) {
      return false;
    }

    final InPacket obj2 = (InPacket) obj;

    if (!obj2.nodeId.equals(this.nodeId)) {
      return false;
    }
    if (!obj2.portId.equals(this.portId)) {
      return false;
    }
    if (!Arrays.equals(obj2.data, this.data)) {
      return false;
    }

    return false;
  }

  @Override
  public boolean readValue(Value value) {
    if (!super.readValue(value)) {
      return false;
    }

    MapValue map = value.asMapValue();

    Value nodeIdValue = map.get(ValueFactory.createRawValue("node"));
    if (nodeIdValue != null) {
      nodeId = nodeIdValue.asRawValue().getString();
    }

    Value portIdValue = map.get(ValueFactory.createRawValue("port"));
    if (portIdValue != null) {
      portId = portIdValue.asRawValue().getString();
    }

    Value dataValue = map.get(ValueFactory.createRawValue("data"));
    if (dataValue != null) {
      data = dataValue.asRawValue().getByteArray();
    }

    Value headerValue = map.get(ValueFactory.createRawValue("header"));
    if (headerValue != null) {
      header = FlowObject.readFlowMatchFrom(headerValue);
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }
    values.put("node", ValueFactory.createRawValue(this.nodeId));
    values.put("port", ValueFactory.createRawValue(this.portId));
    values.put("data", ValueFactory.createRawValue(this.data));
    if (header != null) {
      values.put("header", header.writeValue());
    }
    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.packet.Packet#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("packetId", packetId);
    sb.append("nodeId", nodeId);
    sb.append("portId", portId);
    sb.append("data", data);
    sb.append("attributes", getAttributes());
    sb.append("header", header);

    return sb.toString();
  }

}
