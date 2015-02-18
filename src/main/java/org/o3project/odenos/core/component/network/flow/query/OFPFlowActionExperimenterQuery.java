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
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter;

import java.util.Arrays;
import java.util.Map;

/**
 * Prepares a query for OFPFlowActionExperimenter class.
 *
 */
public class OFPFlowActionExperimenterQuery extends FlowActionQuery {

  private Integer experimenterId = null;
  private byte[] body = null;

  /**
   * Constructor.
   * @param actions action conditions.
   */
  public OFPFlowActionExperimenterQuery(Map<String, String> actions) {
    super(actions);
  }

  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    if (!BasicQuery.checkMapExactly(this.actions, new String[] {
        "experimenter_id", "body" })) {
      return false;
    }

    if (this.actions.containsKey("experimenter_id")) {
      this.experimenterId = BasicQuery
          .cretateInteger(this.actions, "experimenter_id");
      if (this.experimenterId == null) {
        return false;
      }
    }
    String body = this.actions.get("body");
    if (body != null) {
      this.body = body.getBytes();
    }

    return true;
  }

  @Override
  public boolean matchExactly(FlowAction action) {
    if (action == null || !action.getType().equals(
        OFPFlowActionExperimenter.class.getSimpleName())) {
      return false;
    }
    OFPFlowActionExperimenter target = (OFPFlowActionExperimenter) action;
    if (this.experimenterId != null) {
      if (target.getExperimenterId() == null) {
        return false;
      }
      if (!this.experimenterId.equals(target.getExperimenterId())) {
        return false;
      }
    }

    if (this.body == null) {
      if (target.getBody() != null) {
        return false;
      }
      return true;
    }
    if (target.getBody() == null) {
      return false;
    }
    return Arrays.equals(this.body, target.getBody());
  }
}