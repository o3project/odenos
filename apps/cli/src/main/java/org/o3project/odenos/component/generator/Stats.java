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

package org.o3project.odenos.component.generator;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * This class collects statistics during or after InPacket generation. 
 */
public class Stats extends BaseObject implements Cloneable {

  /*** Stats model ********************************************/
  private int inPackets = 0; // The number of InPackets sent
  private int outPackets = 0; // The number of OutPackets received
  private int meanFrequency = 0; // Mean InPackets frequency in msec
  private int meanTat = 0; // Mean TAT in msec
  private int onFlowAdded = 0; // The number of onFlowAdded
  private int onFlowUpdate = 0; // The number of onFlowUpdate
  private int onFlowDelete = 0; // The number of onFlowDelete
  /**************************************************************/

  private int tatAccumulated = 0;
  private int startTime = 0;
  private int count = 0;

  public Stats() {
  }

  public Stats(Stats msg) {
    this.setVersion(msg.getVersion());
    this.inPackets = msg.getInPackets();
    this.outPackets = msg.getOutPackets();
    this.meanFrequency = msg.getMeanFrequency();
    this.meanTat = msg.getMeanTat();
    this.onFlowAdded = msg.getOnFlowAdded();
    this.onFlowAdded = msg.getOnFlowDelete();
    this.putAttributes(new HashMap<String, String>(msg.getAttributes()));
  }

  protected void postInPacket() {
    if (count == 0) {
      this.inPackets++;
      startTime = PostInPacketGenerator.currentTimeMills();
    } else {
      this.inPackets++;
      this.meanFrequency = (PostInPacketGenerator.currentTimeMills() - startTime) / inPackets;
    }
  }

  protected void onFlowAdded() {
    this.onFlowAdded++;
  }

  protected void onFlowUpdate() {
    this.onFlowUpdate++;
  }

  protected void onFlowDelete() {
    this.onFlowDelete++;
  }

  protected void clear() {
    inPackets = 0;
    outPackets = 0;
    meanTat = 0;
    onFlowAdded = 0;
    onFlowUpdate = 0;
    onFlowDelete = 0;
    tatAccumulated = 0;
    count = 0;
  }

  protected void onOutPacketAdded_(OutPacket outPacket) {
    ByteBuffer payload = ByteBuffer.wrap(outPacket.getData());
    @SuppressWarnings("unused")
    byte goback = payload.get();
    int timestamp1 = payload.getInt();
    tatAccumulated += PostInPacketGenerator.currentTimeMills() - timestamp1;
    count++;
    meanTat = tatAccumulated / count; // in msec
    this.outPackets++;
  }

  protected int getInPackets() {
    return inPackets;
  }

  protected int getOutPackets() {
    return outPackets;
  }

  protected int getMeanFrequency() {
    return meanFrequency;
  }

  protected int getMeanTat() {
    return meanTat;
  }

  protected int getOnFlowAdded() {
    return onFlowAdded;
  }

  protected int getOnFlowUpdate() {
    return onFlowUpdate;
  }

  protected int getOnFlowDelete() {
    return onFlowDelete;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {

    int size = upk.readMapBegin();

    while (size-- > 0) {
      switch (upk.readString()) {
        case "inPackets":
          inPackets = upk.readInt();
          break;
        case "outPackets":
          outPackets = upk.readInt();
          break;
        case "meanFrequency":
          meanFrequency = upk.readInt();
          break;
        case "meanTat":
          meanTat = upk.readInt();
          break;
        case "onFlowAdded":
          onFlowAdded = upk.readInt();
          break;
        case "onFlowUpdate":
          onFlowUpdate = upk.readInt();
          break;
        case "onFlowDeleted":
          onFlowDelete = upk.readInt();
          break;
        default:
          break;
      }
    }

    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {

    pk.writeMapBegin(7);

    pk.write("inPackets");
    pk.write(inPackets);

    pk.write("outPackets");
    pk.write(outPackets);

    pk.write("meanFrequency");
    pk.write(meanFrequency);

    pk.write("meanTat");
    pk.write(meanTat);

    pk.write("onFlowAdded");
    pk.write(onFlowAdded);

    pk.write("onFlowUpdate");
    pk.write(onFlowUpdate);

    pk.write("onFlowDelete");
    pk.write(onFlowDelete);

    pk.writeMapEnd();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Stats)) {
      return false;
    }

    Stats env = (Stats) obj;

    try {
      if (env.getVersion().equals(this.getVersion()) && env.getInPackets() == this.inPackets
          && env.getOutPackets() == this.outPackets && env.getMeanFrequency() == this.meanFrequency
          && env.getMeanTat() == this.meanTat && env.getOnFlowAdded() == this.onFlowAdded
          && env.getOnFlowUpdate() == this.onFlowUpdate
          && env.getOnFlowDelete() == this.onFlowDelete
          && env.getAttributes().equals(this.getAttributes())) {
        return true;
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Stats clone() {
    return new Stats(this);
  }
}