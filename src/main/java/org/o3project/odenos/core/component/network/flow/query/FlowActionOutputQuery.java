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

package org.o3project.odenos.core.component.network.flow.query;

import org.o3project.odenos.core.component.network.BasicQuery;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;

import java.util.Map;

/**
 * Prepares a query for FlowActionOutput class.
 *
 */
public class FlowActionOutputQuery
    extends FlowActionQuery {

  String output = null;

  /**
   * Constructor.
   * @param params action conditions.
   */
  public FlowActionOutputQuery(Map<String, String> params) {
    super(params);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#parse()
   */
  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    if (!BasicQuery
        .checkMapExactly(this.actions, new String[] { "output" })) {
      return false;
    }
    this.output = this.actions.get("output");
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)
   */
  @Override
  public boolean matchExactly(FlowAction target) {
    if (target == null || !target.getType().equals(
        FlowActionOutput.class.getSimpleName())) {
      return false;
    }
    FlowActionOutput action = (FlowActionOutput) target;
    if (this.output != null && !this.output.equals(action.output)) {
      return false;
    }
    return true;
  }
}
