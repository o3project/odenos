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
 *  An action that replaces an existing IP TTL value. The packet checksum
 *  must be re-calculated.
 */
public class OFPFlowActionSetIpTtl extends FlowAction {

  private Integer ipTtl;

  /**
   * Constructor.
   */
  public OFPFlowActionSetIpTtl() {
  }

  /**
   * Constructor.
   *
   * @param ipTtl the ip_ttl value to set to.
   */
  public OFPFlowActionSetIpTtl(Integer ipTtl) {
    this.ipTtl = ipTtl;
  }

  @Override
  public boolean validate() {
    if (ipTtl == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a ipTtl.
   *
   * @return ipTtl the ip_ttl value to set to.
   */
  public Integer getIpTtl() {
    return ipTtl;
  }

  /**
   * Sets a ipTtl.
   *
   * @param ipTtl the ip_ttl value to set to.
   */
  public void setIpTtl(Integer ipTtl) {
    this.ipTtl = ipTtl;
  }

  @Override
  public String getType() {
    return OFPFlowActionSetIpTtl.class.getSimpleName();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();
    Value targetValue;

    targetValue = map.get(ValueFactory.createRawValue("ip_ttl"));
    if (!targetValue.isNilValue()) {
      ipTtl = targetValue.asIntegerValue().getInt();
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (ipTtl == null) {
      values.put("ip_ttl", ValueFactory.createIntegerValue(0));
    } else {
      values.put("ip_ttl", ValueFactory.createIntegerValue(ipTtl));
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

    if (!(obj instanceof OFPFlowActionSetIpTtl)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowActionSetIpTtl obj2 = (OFPFlowActionSetIpTtl) obj;

    try {
      if (!obj2.getIpTtl().equals(this.ipTtl)) {
        return false;
      }
    } catch (NullPointerException ex) {
      return false;
    }
    return true;
  }

  @Override
  public OFPFlowActionSetIpTtl clone() {
    return new OFPFlowActionSetIpTtl(ipTtl);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("ipTtl", ipTtl);

    return sb.toString();
  }

}
