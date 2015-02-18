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

/**
 * Set of packet queue.
 *
 */
public class PacketQueueSet {
  private InPacketQueue inQueue;
  private OutPacketQueue outQueue;

  /**
   * Constructor.
   */
  public PacketQueueSet() {
    inQueue = new InPacketQueue();
    outQueue = new OutPacketQueue();
  }

  /**
   * Returns a packet queue IN.
   * @return packet queue IN.
   */
  public PacketQueue getInQueue() {
    return inQueue;
  }

  /**
   * Returns a packet queue IN.
   * @return packet queue OUT.
   */
  public PacketQueue getOutQueue() {
    return outQueue;
  }

  /**
   * Returns packet status.
   * @return packet status.
   */
  public PacketStatus getPacketStatus() {
    PacketStatus status = new PacketStatus();
    inQueue.calcPacketStatus(status.inStatus);
    outQueue.calcPacketStatus(status.outStatus);

    return status;
  }
}