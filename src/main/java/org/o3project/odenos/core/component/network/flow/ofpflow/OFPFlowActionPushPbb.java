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
 * An action that pushes a new PBB(Provider BackBone Bridging) header onto
 * the packet. A PBB frame format consists of a supplementary MAC layer that
 * encapsulates another ethernet frame.
 */
public class OFPFlowActionPushPbb extends FlowAction {

  // The PPB ethertype is 0x88e7.
  public static final Integer DEFAULT_ETHER_TYPE = 0x88e7;

  private Integer ethType;

  /**
   * Constructor.
   */
  public OFPFlowActionPushPbb() {
    this(DEFAULT_ETHER_TYPE);
  }

  /**
   * Constructor.
   *
   * @param ethType the ethertype to set to.
   */
  public OFPFlowActionPushPbb(Integer ethType) {
    this.ethType = ethType;
  }

  @Override
  public boolean validate() {
    if (ethType == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a ethType.
   *
   * @return ethType the ethertype to set to.
   */
  public Integer getEthType() {
    return ethType;
  }

  /**
   * Sets a ethType.
   *
   * @param ethType the ethertype to set to.
   */
  public void setEthType(Integer ethType) {
    this.ethType = ethType;
  }

  @Override
  public String getType() {
    return OFPFlowActionPushPbb.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("eth_type"));
    if (!targetValue.isNilValue()) {
      ethType = targetValue.asIntegerValue().getInt();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (ethType == null) {
      values.put("eth_type", ValueFactory.createIntegerValue(0));
    } else {
      values.put("eth_type", ValueFactory.createIntegerValue(ethType));
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

    if (!(obj instanceof OFPFlowActionPushPbb)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionPushPbb obj2 = (OFPFlowActionPushPbb) obj;

    try {
      if (!obj2.getEthType().equals(this.ethType)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionPushPbb clone() {
    return new OFPFlowActionPushPbb(ethType);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("ethType", ethType);

    return sb.toString();
  }

}
