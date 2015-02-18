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

import org.o3project.odenos.core.component.network.flow.basic.FlowAction;

import java.util.Map;

/**
 * Prepares a query for FlowAction class.
 *
 */
public abstract class FlowActionQuery {
  protected Map<String, String> actions = null;
  protected String type = null;
  protected String edgeNodeId = null;

  /**
   * Constructor.
   * @param params action conditions.
   */
  public FlowActionQuery(Map<String, String> params) {
    this.actions = params;
  }

  /**
   * Returns a edge node ID.
   * @return edge node ID.
   */
  public String getEdgeNodeId() {
    return edgeNodeId;
  }

  /**
   * Parse queries.
   * @return true if queries could be parsed.
   */
  public boolean parse() {
    if (this.actions == null) {
      return false;
    }
    if (!this.actions.containsKey("type")) {
      return false;
    }
    this.type = this.actions.remove("type");
    if (this.actions.containsKey("edge_node")) {
      this.edgeNodeId = this.actions.remove("edge_node");
    }
    return true;
  }

  /**
   * Returns true if a action match exactly.
   * @param target action.
   * @return if action is matched exactly.
   */
  public boolean matchExactly(FlowAction target) {
    return false;
  }
}