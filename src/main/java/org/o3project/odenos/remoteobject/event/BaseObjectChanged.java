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

package org.o3project.odenos.remoteobject.event;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.Event;

import java.io.IOException;

/**
 * The event to notify of change.
 *
 * @param <T> object class.
 */
public abstract class BaseObjectChanged<T> extends Event {

  private static final int MSG_NUM = 3;

  private Class<T> msgClass;
  private String action;
  private T prev;
  private T curr;

  public BaseObjectChanged(final Class<T> clazz) {
    this.msgClass = clazz;
  }

  /**
   * Constructor.
   * @param action string of action. "add", "delete", or "update"
   * @param prev previous object.
   * @param curr current object.
   */
  public BaseObjectChanged(
      final String action,
      final T prev,
      final T curr) {
    this.action = action;
    this.prev = prev;
    this.curr = curr;
  }

  public final String action() {
    return this.action;
  }

  public final T prev() {
    return this.prev;
  }

  public final T curr() {
    return this.curr;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();
    if (size != MSG_NUM) {
      throw new IOException();
    }

    while (size-- > 0) {
      String field = upk.readString();
      switch (field) {
        case "action":
          action = upk.readString();
          break;
        case "prev":
          if (!upk.trySkipNil()) {
            prev = upk.read(this.msgClass);
          }
          break;
        case "curr":
          if (!upk.trySkipNil()) {
            curr = upk.read(this.msgClass);
          }
          break;
        default:
          throw new IOException();
      }
    }
    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeMapBegin(MSG_NUM);
    pk.write("action").write(action);
    pk.write("prev").write(prev);
    pk.write("curr").write(curr);
    pk.writeMapEnd();
  }
}