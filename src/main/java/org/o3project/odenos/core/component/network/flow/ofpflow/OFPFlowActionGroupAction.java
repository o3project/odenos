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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;

import java.util.Map;

/**
 * An action that sets the group id that uniquely identifies a group table entry
 * instance. A group table entry contains the group type, counters and action
 * buckets that modify the flow pipeline processing.
 */
public class OFPFlowActionGroupAction extends FlowAction {

  private Integer groupId;

  /**
   * Constructor.
   */
  public OFPFlowActionGroupAction() {
  }

  /**
   * Constructor.
   *
   * @param groupId
   *          the group id to set to.
   */
  public OFPFlowActionGroupAction(Integer groupId) {
    this.groupId = groupId;
  }

  @Override
  public boolean validate() {
    if (groupId == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a groupId.
   *
   * @return groupId the group id to set to.
   */
  public Integer getGroupId() {
    return groupId;
  }

  /**
   * Sets a groupId.
   *
   * @param groupId
   *          the group id to set to.
   */
  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getType() {
    return OFPFlowActionGroupAction.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("group_id"));
    if (!targetValue.isNilValue()) {
      groupId = targetValue.asIntegerValue().getInt();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (groupId == null) {
      values.put("group_id", ValueFactory.createIntegerValue(0));
    } else {
      values.put("group_id", ValueFactory.createIntegerValue(groupId));
    }

    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof OFPFlowActionGroupAction)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionGroupAction obj2 = (OFPFlowActionGroupAction) obj;

    try {
      if (!obj2.getGroupId().equals(this.groupId)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionGroupAction clone() {
    return new OFPFlowActionGroupAction(groupId);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("groupId", groupId);

    return sb.toString();
  }

}
