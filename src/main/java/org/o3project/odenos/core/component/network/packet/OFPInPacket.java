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

import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.Map;

/**
 * Packet IN for OpenFlow.
 *
 */
public class OFPInPacket extends InPacket {
  private float time;

  /**
   * Constructor.
   */
  public OFPInPacket() {
    super();
  }

  /**
   * Constructor.
   * @param packetId Packet ID.
   * @param nodeId node id to input the packet
   * @param portId port_id to input the packet
   * @param time system time.
   * @param header header information.
   * @param data payload.
   * @param attributes map of attributes.
   */
  public OFPInPacket(String packetId, String nodeId, String portId,
      float time, BasicFlowMatch header, byte[] data,
      Map<String, String> attributes) {
    super(packetId, nodeId, portId, data, attributes, header);
    this.time = time;
    this.packetId = packetId;
  }

  /**
   * Constructor.
   * @param msg packet message.
   */
  public OFPInPacket(OFPInPacket msg) {
    this(msg.getPacketId(), msg.getNodeId(), msg.getPortId(),
        msg.getTime(),
        msg.getHeader(), msg.getData(), msg.getAttributes());
  }

  @Override
  public String getType() {
    return "OFPInPacket";
  }

  /**
   * Return the system time.
   * @return system time.
   */
  public float getTime() {
    return time;
  }

  /**
   * Set the system time.
   * @param time system time.
   */
  public void setTime(float time) {
    this.time = time;
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

    final OFPInPacket obj2 = (OFPInPacket) obj;

    if (obj2.time != this.time) {
      return false;
    }
    if (!obj2.getHeader().equals(this.getHeader())) {
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

    Value timeValue = map.get(ValueFactory.createRawValue("time"));
    if (timeValue != null) {
      time = timeValue.asFloatValue().getFloat();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }
    values.put("time", ValueFactory.createFloatValue(this.time));

    return true;
  }
}