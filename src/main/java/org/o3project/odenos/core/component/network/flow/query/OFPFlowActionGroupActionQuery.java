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
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionGroupAction;

import java.util.Map;

/**
 * Prepares a query for OFPFlowActionGroupAction class.
 */
public class OFPFlowActionGroupActionQuery extends FlowActionQuery {

  private Integer groupId;

  /**
   * Constructor.
   * @param params action conditions.
   */
  public OFPFlowActionGroupActionQuery(Map<String, String> params) {
    super(params);
  }

  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    if (!BasicQuery.checkMapExactly(this.actions,
        new String[] { "group_id" })) {
      return false;
    }
    try {
      this.groupId = Integer.parseInt(this.actions.get("group_id"));
    } catch (NumberFormatException ex) {
      return false;
    }
    return true;
  }

  @Override
  public boolean matchExactly(FlowAction action) {
    if (action == null || !action.getType().equals(
        OFPFlowActionGroupAction.class.getSimpleName())) {
      return false;
    }
    OFPFlowActionGroupAction target = (OFPFlowActionGroupAction) action;
    if (this.groupId == null) {
      return true;
    }
    if (target.getGroupId() == null) {
      return false;
    }
    return this.groupId.equals(target.getGroupId());
  }

}
