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

import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Stats of network's packet information.
 *
 */
public class PacketStatus implements MessagePackable {
  private static final int MSG_NUM = 9;
  PacketStatusSub inStatus;
  PacketStatusSub outStatus;

  /**
   * Constructor.
   */
  public PacketStatus() {
    inStatus = new PacketStatusSub();
    outStatus = new PacketStatusSub();
  }

  /**
   * Returns a stats of some packet IN information.
   * @return stats of some packet IN information.
   */
  public PacketStatusSub getInStatus() {
    return inStatus;
  }

  /**
   * Returns a stats of some packet OUT information.
   * @return stats of some packet OUT information.
   */
  public PacketStatusSub getOutStatus() {
    return outStatus;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();
    if (size != MSG_NUM) {
      throw new IOException();
    }

    int listsize;
    while (size-- > 0) {
      switch (upk.readString()) {
        case "type":
          upk.readString();
          break;
        case "in_packet_count":
          inStatus.packetCount = upk.readLong();
          break;
        case "in_packet_bytes":
          inStatus.packetBytes = upk.readLong();
          break;
        case "in_packet_queue_count":
          inStatus.packetQueueCount = upk.readLong();
          break;
        case "in_packets":
          listsize = upk.readArrayBegin();
          inStatus.packets = new ArrayList<String>(listsize);
          while (listsize-- > 0) {
            inStatus.packets.add(upk.readString());
          }
          upk.readArrayEnd();
          break;
        case "out_packet_count":
          outStatus.packetCount = upk.readLong();
          break;
        case "out_packet_bytes":
          outStatus.packetBytes = upk.readLong();
          break;
        case "out_packet_queue_count":
          outStatus.packetQueueCount = upk.readLong();
          break;
        case "out_packets":
          if (!upk.trySkipNil()) {
            listsize = upk.readArrayBegin();
            outStatus.packets = new ArrayList<String>(listsize);
            while (listsize-- > 0) {
              outStatus.packets.add(upk.readString());
            }
            upk.readArrayEnd();
          }
          break;
        default:
          break;
      }
    }
    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeMapBegin(MSG_NUM);

    pk.write("type").write("PacketStatus");
    pk.write("in_packet_count").write(inStatus.packetCount);
    pk.write("in_packet_bytes").write(inStatus.packetBytes);
    pk.write("in_packet_queue_count").write(inStatus.packetQueueCount);
    pk.write("in_packets").write(inStatus.packets);
    pk.write("out_packet_count").write(outStatus.packetCount);
    pk.write("out_packet_bytes").write(outStatus.packetBytes);
    pk.write("out_packet_queue_count").write(outStatus.packetQueueCount);
    pk.write("out_packets").write(outStatus.packets);

    pk.writeMapEnd();
  }
}