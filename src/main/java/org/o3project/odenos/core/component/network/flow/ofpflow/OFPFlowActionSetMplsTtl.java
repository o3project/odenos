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
 * An action that replaces an existing MPLS TTL. This action applies to
 * packets with an existing MPLS shim header.
 */
public class OFPFlowActionSetMplsTtl extends FlowAction {

  private Integer mplsTtl;

  /**
   * Constructor.
   */
  public OFPFlowActionSetMplsTtl() {
  }

  /**
   * Constructor.
   *
   * @param mplsTtl the MPLS TTL to set to.
   */
  public OFPFlowActionSetMplsTtl(Integer mplsTtl) {
    this.mplsTtl = mplsTtl;
  }

  @Override
  public boolean validate() {
    if (mplsTtl == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a groupId.
   *
   * @return mplsTtl the MPLS TTL to set to.
   */
  public Integer getMplsTtl() {
    return mplsTtl;
  }

  /**
   * Sets a mplsTtl.
   *
   * @param mplsTtl the MPLS TTL to set to.
   */
  public void setMplsTtl(Integer mplsTtl) {
    this.mplsTtl = mplsTtl;
  }

  @Override
  public String getType() {
    return OFPFlowActionSetMplsTtl.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("mpls_ttl"));
    if (!targetValue.isNilValue()) {
      mplsTtl = targetValue.asIntegerValue().getInt();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (mplsTtl == null) {
      values.put("mpls_ttl", ValueFactory.createIntegerValue(0));
    } else {
      values.put("mpls_ttl", ValueFactory.createIntegerValue(mplsTtl));
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

    if (!(obj instanceof OFPFlowActionSetMplsTtl)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionSetMplsTtl obj2 = (OFPFlowActionSetMplsTtl) obj;

    try {
      if (!obj2.getMplsTtl().equals(this.mplsTtl)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionSetMplsTtl clone() {
    return new OFPFlowActionSetMplsTtl(mplsTtl);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("mplsTtl", mplsTtl);

    return sb.toString();
  }

}
