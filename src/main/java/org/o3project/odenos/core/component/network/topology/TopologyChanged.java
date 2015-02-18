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

package org.o3project.odenos.core.component.network.topology;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.Event;

import java.io.IOException;

/**
 * TopologyChanged represents a body of event message delivered when
 * TopologyChanged event occurs.
 *
 */
public class TopologyChanged extends Event {

  /**
   * Actions.
   *
   * <ul>
   * <li>add</li>
   * <li>delete</li>
   * <li>update</li>
   * </ul>
   */
  public static enum Action {
    add, delete, update;
  }

  public static final String TYPE = "TopologyChanged";
  private static final int MSG_NUM_MIN = 3;
  private static final int MSG_NUM_MAX = 4;

  public String version;
  public String action;
  public Topology prev;
  public Topology curr;

  /**
   * Constructor.
   */
  public TopologyChanged() {
  }

  /**
   * Constructor.
   * @param prev previous topology.
   * @param curr current topology.
   * @param action string of action. "add", "delete", or "update"
   */
  public TopologyChanged(final Topology prev,
      final Topology curr,
      final Action action) {
    if (curr != null) {
      this.version = curr.getVersion();
      this.curr = curr;
    } else {
      this.version = null;
      this.curr = null;
    }

    if (prev != null) {
      this.prev = prev;
    } else {
      this.prev = null;
    }

    this.action = action.toString();
    if (action == Action.delete) {
      this.version = null;
    }
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();

    if (size < MSG_NUM_MIN || MSG_NUM_MAX < size) {
      throw new IOException();
    }

    while (size-- > 0) {
      switch (upk.readString()) {
        case "version":
          version = upk.readString();
          break;
        case "action":
          action = upk.readString();
          break;
        case "prev":
          if (!upk.trySkipNil()) {
            prev = upk.read(Topology.class);
          }
          break;
        case "curr":
          if (!upk.trySkipNil()) {
            curr = upk.read(Topology.class);
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

    if (version != null) {
      pk.write("version").write(version);
    }
    pk.write("action").write(action);
    pk.write("prev").write(prev);
    pk.write("curr").write(curr);

    pk.writeMapEnd();
  }
}