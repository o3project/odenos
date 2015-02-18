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

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.Event;

import java.io.IOException;

/**
 * Base message Class of event message PacketAdded.
 */
public abstract class BasePacketAdded extends Event {

  private static final int MSG_NUM = 1;

  private String packetId;

  /**
   * Constructor.
   */
  public BasePacketAdded() {
  }

  /**
   * Constructor.
   * @param inpacket packet of packet IN.
   */
  public BasePacketAdded(final Packet inpacket) {
    setId(inpacket.getPacketId());
  }

  /**
   * Returns a packet ID.
   * @return packet ID.
   */
  public final String getId() {
    return packetId;
  }

  /**
   * Sets a packet ID.
   * @param packetId packet ID.
   */
  public final void setId(final String packetId) {
    this.packetId = packetId;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();
    if (size != MSG_NUM) {
      throw new IOException();
    }

    if (!upk.readString().equals("id")) {
      throw new IOException();
    }
    this.packetId = upk.readString();

    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeMapBegin(MSG_NUM);

    pk.write("id");
    pk.write(this.packetId);

    pk.writeMapEnd();
  }
}