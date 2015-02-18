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
 * An action that sets the queue id for the packet.
 */
public class OFPFlowActionSetQueue extends FlowAction {

  private Integer queueId;

  /**
   * Constructor.
   */
  public OFPFlowActionSetQueue() {
  }

  /**
   * Constructor.
   *
   * @param queueId the queue id to set to.
   */
  public OFPFlowActionSetQueue(Integer queueId) {
    this.queueId = queueId;
  }

  @Override
  public boolean validate() {
    if (queueId == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a queueId.
   *
   * @return queueId the queue id to set to.
   */
  public Integer getQueueId() {
    return queueId;
  }

  /**
   * Sets a queueId.
   *
   * @param queueId
   *          the queue id to set to.
   */
  public void setQueueId(Integer queueId) {
    this.queueId = queueId;
  }

  @Override
  public String getType() {
    return OFPFlowActionSetQueue.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("queue_id"));
    if (!targetValue.isNilValue()) {
      queueId = targetValue.asIntegerValue().getInt();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (queueId == null) {
      values.put("queue_id", ValueFactory.createIntegerValue(0));
    } else {
      values.put("queue_id", ValueFactory.createIntegerValue(queueId));
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

    if (!(obj instanceof OFPFlowActionSetQueue)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionSetQueue obj2 = (OFPFlowActionSetQueue) obj;

    try {
      if (!obj2.getQueueId().equals(this.queueId)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionSetQueue clone() {
    return new OFPFlowActionSetQueue(queueId);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("queueId", queueId);

    return sb.toString();
  }

}
