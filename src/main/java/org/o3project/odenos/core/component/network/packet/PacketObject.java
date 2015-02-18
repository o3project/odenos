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
import org.msgpack.type.ValueFactory;

/**
 * Prepares a packet.
 *
 */
public class PacketObject {

  /**
   * Read a Packet.
   * @param value value data.
   * @return Packet instance.
   */
  public static Packet readPacketMessageFrom(Value value) {
    Packet msg;
    String type = value.asMapValue().get(ValueFactory.createRawValue("type"))
        .asRawValue().getString();
    switch (type) {
      case "InPacket":
        msg = new InPacket();
        break;
      case "OutPacket":
        msg = new OutPacket();
        break;
      case "OFPInPacket":
        msg = new OFPInPacket();
        break;
      case "OFPOutPacket":
        msg = new OFPOutPacket();
        break;

      default:
        return null;
    }
    msg.readValue(value);
    return msg;
  }

  /**
   * Create a Packet.
   * @param msg target packet.
   * @return Packet instance.
   */
  public static Packet createPacket(Packet msg) {
    if (msg instanceof InPacket) {
      return createInPacket(msg);
    }
    if (msg instanceof OutPacket) {
      return createOutPacket(msg);
    }
    return null;
  }

  /**
   * Read a Packet IN.
   * @param value packet.
   * @return Packet instance.
   */
  public static InPacket readInPacketFrom(Value value) {
    InPacket inpacket;

    if (value == null || value.isNilValue()) {
      return null;
    }

    String type = value.asMapValue().get(
        ValueFactory.createRawValue("type")).asRawValue().getString();

    switch (type) {
      case "InPacket":
        inpacket = new InPacket();
        inpacket.readValue(value);
        break;
      case "OFPInPacket":
        inpacket = new OFPInPacket();
        inpacket.readValue(value);
        break;
      default:
        inpacket = null;
        break;
    }

    return inpacket;
  }

  /**
   * Read a Packet OUT.
   * @param value packet.
   * @return Packet instance.
   */
  public static OutPacket readOutPacketFrom(Value value) {
    OutPacket outpacket;

    if (value == null || value.isNilValue()) {
      return null;
    }
    String type = value.asMapValue().get(
        ValueFactory.createRawValue("type")).asRawValue().getString();

    switch (type) {
      case "OutPacket":
        outpacket = new OutPacket();
        outpacket.readValue(value);
        break;
      case "OFPOutPacket":
        outpacket = new OFPOutPacket();
        outpacket.readValue(value);
        break;
      default:
        outpacket = null;
        break;
    }

    return outpacket;
  }

  private static InPacket createInPacket(Packet msg) {
    InPacket packet;
    switch (msg.getType()) {
      case "InPacket":
        packet = new InPacket((InPacket) msg);
        break;
      case "OFPInPacket":
        packet = new OFPInPacket((OFPInPacket) msg);
        break;
      default:
        return null;
    }
    return packet;
  }

  private static OutPacket createOutPacket(Packet msg) {
    OutPacket packet;
    switch (msg.getType()) {
      case "OutPacket":
        packet = new OutPacket((OutPacket) msg);
        break;
      case "OFPOutPacket":
        packet = new OFPOutPacket((OFPOutPacket) msg);
        break;
      default:
        return null;
    }
    return packet;
  }
}
