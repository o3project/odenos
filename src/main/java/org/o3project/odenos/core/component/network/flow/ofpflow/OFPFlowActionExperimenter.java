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

import java.util.Arrays;
import java.util.Map;

/**
 * An action to set an experimenter action. Creates an action to set an
 * experimenter action.
 */
public class OFPFlowActionExperimenter extends FlowAction {

  private Integer experimenterId;
  private byte[] body;

  /**
   * Constructor.
   */
  public OFPFlowActionExperimenter() {
  }

  /**
   * Constructor.
   *
   * @param experimenterId
   *          the experimenter identifier.
   * @param body
   *          experimenter-defined arbitrary additional data.
   */
  public OFPFlowActionExperimenter(Integer experimenterId, byte[] body) {
    this.experimenterId = experimenterId;
    this.body = body;
  }

  @Override
  public boolean validate() {
    if (experimenterId == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a experimenterId.
   *
   * @return the experimenter identifier.
   */
  public Integer getExperimenterId() {
    return experimenterId;
  }

  /**
   * Sets a experimenterId.
   *
   * @param experimenterId
   *          the experimenter identifier.
   */
  public void setExperimenterId(Integer experimenterId) {
    this.experimenterId = experimenterId;
  }

  /**
   * Return bytes of body.
   *
   * @return bytes of body.
   */
  public byte[] getBody() {
    return body;
  }

  /**
   * Set bytes of body.
   *
   * @param body
   *          bytes of body.
   */
  public void setBody(byte[] body) {
    this.body = body;
  }

  @Override
  public String getType() {
    return OFPFlowActionExperimenter.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("experimenter_id"));
    if (!targetValue.isNilValue()) {
      experimenterId = targetValue.asIntegerValue().getInt();
    }

    targetValue = map.get(ValueFactory.createRawValue("body"));
    if (!targetValue.isNilValue()) {
      body = targetValue.asRawValue().getByteArray();
    }
    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (experimenterId == null) {
      return false;
    } else {
      values.put("experimenter_id", ValueFactory.createIntegerValue(experimenterId));
    }

    if (body != null) {
      values.put("body", ValueFactory.createRawValue(body));
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

    if (!(obj instanceof OFPFlowActionExperimenter)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionExperimenter obj2 = (OFPFlowActionExperimenter) obj;

    try {
      if (!obj2.getExperimenterId().equals(this.experimenterId)) {
        return false;
      }
      if (!Arrays.equals(obj2.getBody(), this.body)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionExperimenter clone() {
    return new OFPFlowActionExperimenter(experimenterId, body.clone());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("experimenterId", experimenterId);
    sb.append("body", body);

    return sb.toString();
  }

}