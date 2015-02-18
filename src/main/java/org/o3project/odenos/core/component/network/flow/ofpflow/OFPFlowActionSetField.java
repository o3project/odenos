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
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;

import java.util.Map;

/**
 * Action set field - an array of one or more OXM. A set field action can be any
 * OXM field except OXM_IN_PORT, OXM_IN_PHY_PORT, OXM_METADATA
 */
public class OFPFlowActionSetField extends FlowAction {

  private OFPFlowMatch match;

  /**
   * Constructor.
   */
  public OFPFlowActionSetField() {
  }

  /**
   * Constructor.
   *
   * @param matchField OFPFlowMatch
   */
  public OFPFlowActionSetField(OFPFlowMatch matchField) {
    this.match = matchField;
  }

  @Override
  public boolean validate() {
    if (match == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a matchField.
   *
   * @return matchField OFPFlowMatch
   */
  public OFPFlowMatch getMatch() {
    return match;
  }

  /**
   * Sets a matchField.
   *
   * @param matchField OFPFlowMatch
   */
  public void setMatch(OFPFlowMatch matchField) {
    this.match = matchField;
  }

  @Override
  public String getType() {
    return OFPFlowActionSetField.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("match"));
    try {
      if (!targetValue.isNilValue()) {
        match = (OFPFlowMatch) FlowObject.readFlowMatchFrom(targetValue);
      }
    } catch (ClassCastException ex) {
      return false;
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (match == null) {
      values.put("match", null);
    } else {
      values.put("match", match.writeValue());
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

    if (!(obj instanceof OFPFlowActionSetField)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionSetField obj2 = (OFPFlowActionSetField) obj;

    try {
      if (!obj2.getMatch().equals(this.match)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionSetField clone() {
    return new OFPFlowActionSetField(match.clone());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("match", match.toString());

    return sb.toString();
  }

}
