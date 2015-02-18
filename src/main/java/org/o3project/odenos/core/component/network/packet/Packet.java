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
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Packet data.
 *
 */
public abstract class Packet extends OdenosMessage {
  public String type;
  public String packetId;

  /**
   * Constructor.
   */
  public Packet() {
  }

  /**
   * Constructor.
   * @param packetId Packet id.
   * @param attributes map of attributes.
   */
  public Packet(String packetId, Map<String, String> attributes) {
    this.packetId = packetId;
    if (attributes == null) {
      this.putAttributes(new HashMap<String, String>());
    } else {
      this.putAttributes(attributes);
    }
  }

  /**
   * Constructor.
   * @param msg packet message.
   */
  public Packet(Packet msg) {
    this(msg.getPacketId(), msg.getAttributes());
  }

  /**
   * Returns a type of packet.
   * @return type of packet.
   */
  public abstract String getType();

  /**
   * Returns a packet ID.
   * @return packet ID.
   */
  public String getPacketId() {
    return packetId;
  }

  /**
   * Sets a packet ID.
   * @param packetId packet ID.
   */
  public void setPacketId(String packetId) {
    this.packetId = packetId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Packet)) {
      return false;
    }

    final Packet obj2 = (Packet) obj;

    if (!obj2.getType().equals(this.getType())) {
      return false;
    }
    if (!obj2.getPacketId().equals(this.packetId)) {
      return false;
    }
    if (!obj2.getAttributes().equals(this.getAttributes())) {
      return true;
    }

    return false;
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();

    Value typeValue = map.get(ValueFactory.createRawValue("type"));
    if (typeValue != null) {
      type = typeValue.asRawValue().getString();
    }

    Value packetIdValue = map.get(ValueFactory.createRawValue("packet_id"));
    if (packetIdValue != null && !packetIdValue.isNilValue()) {
      packetId = packetIdValue.asRawValue().getString();
    }

    Value attrValue = map.get(ValueFactory.createRawValue("attributes"));
    if (attrValue != null && !attrValue.isNilValue()) {
      MapValue attrMap = attrValue.asMapValue();
      for (Entry<Value, Value> entry : attrMap.entrySet()) {
        putAttribute(entry.getKey().asRawValue().getString(),
            entry.getValue().asRawValue().getString());
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put("type", ValueFactory.createRawValue(getType()));

    values.put("packet_id", ValueFactory.createRawValue(getPacketId()));

    Value[] attributesArray = new Value[getAttributes().size() * 2];
    int num = 0;
    for (Entry<String, String> entry : getAttributes().entrySet()) {
      attributesArray[num * 2] = ValueFactory.createRawValue(entry
          .getKey());
      attributesArray[num * 2 + 1] = ValueFactory.createRawValue(
          entry.getValue());
      ++num;
    }
    values.put("attributes", ValueFactory.createMapValue(attributesArray));

    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("packetId", packetId);
    sb.append("attributes", getAttributes());

    return sb.toString();
  }

}