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

package org.o3project.odenos.core.component.network;

import org.msgpack.annotation.Optional;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.Event;

import java.io.IOException;

/**
 * Base message Class of event message NetworkChanged.
 *
 * @param <T> message class.
 *
 */
public class BaseNetworkChanged<T> extends Event {

  protected static final int MSG_NUM_MIN = 4;
  protected static final int MSG_NUM_MAX = 5;

  private Class<T> msgClass;

  public String id;
  public String action;
  @Optional
  public String version;
  public T prev;
  public T curr;

  /**
   * Constructor.
   * @param clazz message class.
   */
  public BaseNetworkChanged(final Class<T> clazz) {
    this.msgClass = clazz;
  }

  /**
   * Constructor.
   * @param action String of action.
   * @param prev Previous parameter.
   * @param curr Current parameter.
   */
  public BaseNetworkChanged(
      final String action,
      final T prev,
      final T curr) {
    this.action = action;
    this.prev = prev;
    this.curr = curr;
  }

  protected final void setId(final String id) {
    this.id = id;
  }

  protected final void setVersion(final String version) {
    this.version = version;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();

    if (size < MSG_NUM_MIN || MSG_NUM_MAX < size) {
      throw new IOException();
    }

    while (size-- > 0) {
      switch (upk.readString()) {
        case "id":
          id = upk.readString();
          break;
        case "version":
          version = upk.readString();
          break;
        case "action":
          action = upk.readString();
          break;
        case "prev":
          if (!upk.trySkipNil()) {
            prev = upk.read(msgClass);
          }
          break;
        case "curr":
          if (!upk.trySkipNil()) {
            curr = upk.read(msgClass);
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
    int msgnum = MSG_NUM_MIN;
    if (version != null) {
      msgnum = MSG_NUM_MAX;
    }
    pk.writeMapBegin(msgnum);

    pk.write("id").write(id);

    if (version != null) {
      pk.write("version").write(version);
    }

    pk.write("action").write(action);
    pk.write("prev").write(prev);
    pk.write("curr").write(curr);

    pk.writeMapEnd();
  }
}