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

package org.o3project.odenos.core.component.network.flow;

import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.core.component.network.BaseNetworkChanged;

import java.io.IOException;

/**
 * FlowChanged represents a body of event message delivered when FlowChanged
 * event occurs.
 *
 */
public class FlowChanged extends BaseNetworkChanged<Flow> {

  /**
   * Actions.
   *
   * <ul>
   * <li>add</li>
   * <li>update</li>
   * <li>delete</li>
   * </ul>
   */
  public static enum Action {
    add, delete, update;
  }

  public static final String TYPE = "FlowChanged";

  /**
   * Constructors.
   */
  public FlowChanged() {
    super(Flow.class);
  }

  /**
   * Constructors.
   * @param prev Previous flow.
   * @param curr Current flow.
   * @param action Action string.
   */
  public FlowChanged(final Flow prev,
      final Flow curr,
      final Action action) {
    super(null, prev, curr);
    if (action != null) {
      this.action = action.name();
    }

    String id;
    String version = null;

    if (curr != null) {
      id = curr.getFlowId();
      version = curr.getVersion();
    } else if (prev == null) {
      id = null;
    } else {
      id = prev.getFlowId();
    }

    if (action == Action.delete) {
      version = null;
    }

    this.setId(id);
    this.setVersion(version);
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
            prev = FlowObject.readFlowMessageFrom(upk.readValue());
          }
          break;
        case "curr":
          if (!upk.trySkipNil()) {
            curr = FlowObject.readFlowMessageFrom(upk.readValue());
          }
          break;
        default:
          throw new IOException();
      }
    }
    upk.readMapEnd();
  }

}