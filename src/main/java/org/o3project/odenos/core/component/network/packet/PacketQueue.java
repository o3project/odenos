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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Queue packet.
 *
 */
public class PacketQueue {
  private LinkedList<Packet> packets = new LinkedList<Packet>();
  private Map<String, Packet> packetDict = new HashMap<String, Packet>();
  private long packetCount = 0;
  private long packetBytes = 0;

  /**
   * Constructor.
   */
  public PacketQueue() {
  }

  /**
   * Enqueue a packet.
   * @param packet target a packet.
   * @return processed the packet.
   */
  public Packet enqueuePacket(Packet packet) {
    if (packet == null) {
      return null;
    }

    packet.packetId = getUniquePacketId();
    Class<?> clazz = packet.getClass();
    Field field;
    byte[] data;
    try {
      field = clazz.getField("data");
      data = (byte[]) field.get(packet);
    } catch (Exception e) {
      return null;
    }

    synchronized (packets) {
      packets.add(packet);
      packetDict.put(packet.getPacketId(), packet);
      ++packetCount;

      if (data != null) {
        packetBytes += data.length;
      }
    }

    return packet;
  }

  /**
   * Dequeue a packet.
   * @return processed the packet.
   */
  public Packet dequeuePacket() {
    Packet packet;
    synchronized (packets) {
      packet = packets.poll();
      if (packet != null) {
        packetDict.remove(packet.getPacketId());
      }
    }
    return packet;
  }

  /**
   * Returns a packet.
   * @return packet that in the head of list.
   */
  public Packet peekPacket() {
    return packets.peek();
  }

  /**
   * Returns a packet.
   * @param packetId packet ID.
   * @return packet associated to packet ID.
   */
  public Packet getPacket(String packetId) {
    return packetDict.get(packetId);
  }

  /**
   * Pick a packet.
   * @param packetId Packet id.
   * @return Packet object.
   */
  public Packet pickPacket(String packetId) {
    Packet packet = getPacket(packetId);

    synchronized (packets) {
      if (packet == null || !packets.remove(packet)) {
        return null;
      }
      packetDict.remove(packetId);
    }

    return packet;
  }

  /**
   * Clear queue of packets.
   */
  public void clearPackets() {
    synchronized (packets) {
      packets.clear();
      packetDict.clear();
    }
  }

  /**
   * Get a status of packet.
   * @return status of packet.
   */
  public PacketStatus getPacketStatus() {
    PacketStatus status = new PacketStatus();
    if (this instanceof InPacketQueue) {
      calcPacketStatus(status.getInStatus());
    } else if (this instanceof OutPacketQueue) {
      calcPacketStatus(status.getOutStatus());
    }
    return status;
  }

  /**
   * Get a message of packet.
   * @param query Queries string.
   * @return got messages.
   */
  public Map<String, Packet> getPacketMessages(PacketQuery<?> query) {
    Map<String, Packet> packets = new HashMap<String, Packet>();
    for (Packet packet : this.packets) {
      if (query.matchExactly(packet)) {
        packets.put(packet.getPacketId(), packet);
      }

    }
    return packets;
  }

  /**
   * Calculate the statistics of the packets.
   * @param status sub statistics of the packets.
   */
  public void calcPacketStatus(PacketStatusSub status) {
    List<String> packetsList = new ArrayList<String>();

    synchronized (packets) {
      for (Packet packet : packets) {
        packetsList.add(packet.getPacketId());
      }
    }
    status.packetCount = packetCount;
    status.packetBytes = packetBytes;
    status.packetQueueCount = packetsList.size();
    status.packets = packetsList;
  }

  private int packetIdCounter = 0;

  private String getUniquePacketId() {
    return String.format("%010d", packetIdCounter++);
  }
}