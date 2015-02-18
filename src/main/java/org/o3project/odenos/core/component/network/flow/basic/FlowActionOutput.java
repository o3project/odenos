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

package org.o3project.odenos.core.component.network.flow.basic;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

import java.util.Map;

/**
 * FlowAction class which indicates packet output processing in an edge node of BasicFlow.
 *
 */
public class FlowActionOutput extends FlowAction {
  public String output;

  /**
   * Constructor.
   */
  public FlowActionOutput() {
  }

  /**
   * Constructor.
   * @param output Output port ID.
   */
  public FlowActionOutput(String output) {
    this.output = output;
  }

  @Override
  public boolean validate() {
    if (output == null || output.equals("")) {
      return false;
    }
    return true;
  }

  @Override
  public String getType() {
    return "FlowActionOutput";
  }

  /**
   * Returns a output port ID.
   * @return output port ID.
   */
  public String getOutput() {
    return output;
  }

  /**
   * Sets a output port ID.
   * @param output output port ID.
   */
  public void setOutput(String output) {
    this.output = output;
  }

  @Override
  public boolean readValue(Value value) {
    output = value.asMapValue().get(ValueFactory.createRawValue("output"))
        .asRawValue().getString();
    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    values.put("output", ValueFactory.createRawValue(output));
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

    if (!(obj instanceof FlowActionOutput)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final FlowActionOutput obj2 = (FlowActionOutput) obj;

    if (!obj2.getOutput().equals(this.output)) {
      return false;
    }

    return true;
  }

  @Override
  public FlowAction clone() {
    return new FlowActionOutput(output);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("output", output);

    return sb.toString();
  }

}