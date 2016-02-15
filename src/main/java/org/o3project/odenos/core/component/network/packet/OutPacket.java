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
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Packet OUT data.
 *
 */
public class OutPacket extends Packet {
  private String nodeId;
  private List<String> portIds = new ArrayList<String>();
  private List<String> portExceptIds = new ArrayList<String>();
  private BasicFlowMatch header;
  public byte[] data;

  /**
   * Constructor.
   */
  public OutPacket() {
    super();
  }

  /**
   * Constructor.
   * @param packetId Packet id.
   * @param nodeId node id to output the packet.
   * @param portIds list of port id.
   * @param portExceptIds list of excluded port ids.
   * @param data payload.
   * @param attributes map of attributes.
   */
  public OutPacket(String packetId, String nodeId,
      List<String> portIds, List<String> portExceptIds,
      byte[] data, Map<String, String> attributes) {
    super(packetId, attributes);
    this.nodeId = nodeId;
    if (portIds != null) {
      this.portIds = portIds;
    }
    if (portExceptIds != null) {
      this.portExceptIds = portExceptIds;
    }
    this.data = data;
  }

  /**
   * Constructor.
   * @param packetId Packet id.
   * @param nodeId node id to output the packet.
   * @param portIds list of port id.
   * @param portExceptIds list of excluded port ids.
   * @param data payload.
   * @param attributes map of attributes.
   * @param header header information.
   */
  public OutPacket(String packetId, String nodeId,
      List<String> portIds, List<String> portExceptIds,
      byte[] data, Map<String, String> attributes,
      BasicFlowMatch header) {
    super(packetId, attributes);
    this.nodeId = nodeId;
    if (portIds != null) {
      this.portIds = portIds;
    }
    if (portExceptIds != null) {
      this.portExceptIds = portExceptIds;
    }
    this.data = data;
    this.header = header;
    if (header == null) {
      this.header = new BasicFlowMatch();
    }
  }

  /**
   * Constructor.
   * @param msg packet message of Packet OUT.
   */
  public OutPacket(OutPacket msg) {
    this(msg.getPacketId(), msg.getNodeId(), msg.getPorts(), msg
        .getExceptPorts(),
        msg.getData(), msg.getAttributes(), msg.getHeader());
  }

  @Override
  public String getType() {
    return "OutPacket";
  }

  /**
   * Return a header info.
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
   * Returns a list of port IDs.
   * @return list of port IDs.
   */
  public List<String> getPorts() {
    return this.portIds;
  }

  /**
   * Set all port IDs of lists.
   * @param portIds list of port IDs.
   */
  public void setPorts(List<String> portIds) {
    this.portExceptIds = new ArrayList<String>();
    this.portIds = portIds;
  }

  /**
   * return a list of excepted port ID.
   * @return list of excepted port ID.
   */
  public List<String> getExceptPorts() {
    return portExceptIds;
  }

  /**
   * Set a list of excepted port ID.
   * @param portExceptIds list of excepted port ID.
   */
  public void setExceptPorts(List<String> portExceptIds) {
    this.portIds = new ArrayList<String>();
    this.portExceptIds = portExceptIds;
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
    if (!(obj instanceof OutPacket)) {
      return false;
    }
    if (!(super.equals(obj))) {
      return false;
    }

    final OutPacket obj2 = (OutPacket) obj;

    if (!obj2.nodeId.equals(this.nodeId)) {
      return false;
    }
    if (!obj2.portIds.equals(this.portIds)) {
      return false;
    }
    if (!obj2.portExceptIds.equals(this.portExceptIds)) {
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

    Value portIdsValue = map.get(ValueFactory.createRawValue("ports"));
    if (portIdsValue != null) {
      ArrayValue valueArray = portIdsValue.asArrayValue();
      for (Value val : valueArray.getElementArray()) {
        this.portIds.add(val.asRawValue().getString());
      }
    }

    Value portExceptIdsValue = map.get(
        ValueFactory.createRawValue("ports-except"));
    if (portExceptIdsValue != null) {
      ArrayValue valueArray = portExceptIdsValue.asArrayValue();
      for (Value val : valueArray.getElementArray()) {
        this.portExceptIds.add(val.asRawValue().getString());
      }
    }

    Value dataValue = map.get(ValueFactory.createRawValue("data"));
    if (dataValue != null) {
      this.data = dataValue.asRawValue().getByteArray();
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

    if (portIds == null) {
      values.put("ports", ValueFactory.createNilValue());
    } else {
      Value[] portIdsValues = new Value[portIds.size()];
      int num = 0;
      for (String portId : portIds) {
        portIdsValues[num] = ValueFactory.createRawValue(portId);
        ++num;
      }
      values.put("ports",
          ValueFactory.createArrayValue(portIdsValues));
    }

    if (portExceptIds == null) {
      values.put("ports-except", ValueFactory.createNilValue());
    } else {
      Value[] portExceptIdsValues = new Value[portExceptIds.size()];
      int num = 0;
      for (String portId : portExceptIds) {
        portExceptIdsValues[num] = ValueFactory.createRawValue(portId);
        ++num;
      }
      values.put("ports-except",
          ValueFactory.createArrayValue(portExceptIdsValues));
    }

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
    sb.append("portIds", portIds);
    sb.append("portExceptIds", portExceptIds);
    sb.append("data", data);
    sb.append("attributes", getAttributes());
    sb.append("header", header);

    return sb.toString();
  }

}
