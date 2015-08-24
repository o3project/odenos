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

import org.msgpack.type.Value;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.List;
import java.util.Map;

/**
 * Packet OUT for OpenFlow.
 *
 */
public class OFPOutPacket extends OutPacket {

  /**
   * Constructor.
   */
  public OFPOutPacket() {
    super();
  }

  /**
   * Constructor.
   * @param packetId Packet ID.
   * @param nodeId node id to output the packet.
   * @param portIds list of port id.
   * @param portExceptIds list of excluded port ids.
   * @param header header information.
   * @param data payload.
   * @param attributes map of attributes.
   */
  public OFPOutPacket(String packetId, String nodeId,
      List<String> portIds,
      List<String> portExceptIds,
      BasicFlowMatch header,
      byte[] data, Map<String, String> attributes) {
    super(packetId, nodeId, portIds,
        portExceptIds, data, attributes);
    this.setHeader(header);
  }

  /**
   * Constructor.
   * @param msg packet message.
   */
  public OFPOutPacket(OFPOutPacket msg) {
    this(msg.getPacketId(), msg.getNodeId(), msg.getPorts(), msg
        .getExceptPorts(),
        msg.getHeader(), msg.getData(), msg.getAttributes());
  }

  @Override
  public String getType() {
    return "OFPOutPacket";
  }

  @Override
  public boolean readValue(Value value) {
    if (!super.readValue(value)) {
      return false;
    }
    value.asMapValue();
    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (this.getHeader() != null) {
      values.put("header", this.getHeader().writeValue());
    }

    return true;
  }
}