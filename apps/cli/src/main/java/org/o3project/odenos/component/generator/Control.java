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
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class is to control {@link Generator} via REST APIs.
 */
public class Control extends BaseObject implements Cloneable {

  /*** Control model ********************************************/
  private int frequency = 1000; // 1000msec
  private boolean generating = false; // Generating InPacket
  private String payload = "Hello World!";
  private int maxSeqno = 0xFFFFFF; // Max sequence number

  /**************************************************************/

  public Control() {
  }

  public Control(boolean generating) {
    this.generating = generating;
  }

  public Control(int frequency, boolean generating) {
    this.frequency = frequency;
    this.generating = generating;
  }

  public Control(int frequency, boolean generating, String payload, int maxSeqno, int mode) {
    this.frequency = frequency;
    this.generating = generating;
    this.payload = payload;
    this.maxSeqno = maxSeqno;
  }

  public Control(Control msg) {
    this.setVersion(msg.getVersion());
    this.frequency = msg.getFrequency();
    this.generating = msg.getGenerating();
    this.payload = msg.getPayload();
    this.maxSeqno = msg.getMaxSeqno();
    this.putAttributes(new HashMap<String, String>(msg.getAttributes()));
  }

  protected int getFrequency() {
    return frequency;
  }

  protected boolean getGenerating() {
    return generating;
  }

  protected String getPayload() {
    return payload;
  }

  protected int getMaxSeqno() {
    return maxSeqno;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {

    int size = upk.readMapBegin();

    while (size-- > 0) {
      switch (upk.readString()) {
      case "frequency":
        frequency = upk.readInt();
        break;
      case "generating":
        generating = upk.readBoolean();
        break;
      case "payload":
        payload = upk.readString();
        break;
      case "maxSeqno":
        maxSeqno = upk.readInt();
        break;
      default:
        break;
      }
    }

    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {

    pk.writeMapBegin(4);

    pk.write("frequency");
    pk.write(frequency);

    pk.write("generating");
    pk.write(generating);

    pk.write("payload");
    pk.write(payload);

    pk.write("maxSeqno");
    pk.write(maxSeqno);

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

    if (!(obj instanceof Control)) {
      return false;
    }

    Control control = (Control) obj;

    try {
      if (control.getVersion().equals(this.getVersion())
          && control.getFrequency() == this.frequency && control.getGenerating() == this.generating
          && control.getPayload().equals(this.payload) && control.getMaxSeqno() == this.maxSeqno
          && control.getAttributes().equals(this.getAttributes())) {
        return true;
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Control clone() {
    return new Control(this);
  }
}