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

package org.o3project.odenos.core.component.network.flow.ofpflow;

import org.msgpack.type.Value;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;

import java.util.Map;

/**
 * Copy TTL inwards. Creates an action that copies the TTL from outermost to
 * next-to-outermost header with TTL. The copy applies to IP-to-IP,
 * MPLS-to-MPLS, and IP-to-MPLS packets.
 */
public class OFPFlowActionCopyTtlIn extends FlowAction {

  @Override
  public boolean validate() {
    return true;
  }

  @Override
  public String getType() {
    return OFPFlowActionCopyTtlIn.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    return super.writeValueSub(values);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof OFPFlowActionCopyTtlIn)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    return true;
  }

  @Override
  public FlowAction clone() {
    return new OFPFlowActionCopyTtlIn();
  }

  @Override
  public String toString() {
    return getType();
  }

}
