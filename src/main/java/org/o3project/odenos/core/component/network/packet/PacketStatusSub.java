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

import java.util.List;

/**
 * Stats of some packet information.
 *
 */
public class PacketStatusSub {
  public long packetCount;
  public long packetBytes;
  public long packetQueueCount;
  public List<String> packets;

  /**
   * Returns a count of packet.
   * @return count of packet.
   */
  public long getPacketCount() {
    return packetCount;
  }

  /**
   * Returns bytes of packet.
   * @return bytes of packet.
   */
  public long getPacketBytes() {
    return packetBytes;
  }

  /**
   * Returns a count of packet queue.
   * @return count of packet queue.
   */
  public long getPacketQueueCount() {
    return packetQueueCount;
  }

  /**
   * Returns a list of packets.
   * @return list of packets.
   */
  public List<String> getPackets() {
    return packets;
  }

}