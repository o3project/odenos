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

import static org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus.messageValueOf;

import java.util.List;
import java.util.Map;

import org.o3project.odenos.core.component.network.BasicQuery;
import org.o3project.odenos.core.component.network.flow.FlowActionQueryFactory;
import org.o3project.odenos.core.component.network.flow.FlowMatchQueryFactory;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.remoteobject.message.BaseObject;

/**
 * Prepares a query for OFPFlow class.
 *
 */
public class OFPFlowQuery extends FlowQuery {

  protected String type = null;
  protected Boolean enabled = null;
  protected FlowStatus status = null;
  protected BasicFlowMatchQuery match = null;
  protected Map<String, String> path = null;
  protected FlowActionQuery actions = null;

  private Long idleTimeout = null;
  private Long hardTimeout = null;

  /**
   * Constructor.
   * @param queriesString query string.
   */
  public OFPFlowQuery(String queriesString) {
    super(queriesString);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.component.network.BaseObjectQuery#parse()
   */
  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }

    this.type = this.queries.remove("type");

    if (this.queries.containsKey("enabled")) {
      String enable = this.queries.get("enabled");
      if (!"true".equals(enable.toLowerCase())
          && !"false".equals(enable.toLowerCase())) {
        return false;
      }
      this.enabled = Boolean.parseBoolean(this.queries.remove("enabled"));
    }
    if (this.queries.containsKey("status")) {
      this.status = FlowStatus.messageValueOf(this.queries
          .remove("status"));
      if (this.status == null) {
        return false;
      }
    }
    if (this.queries.containsKey("match")) {
      Map<String, String> tmp =
          BasicQuery.convertToMap(
              this.queries.remove("match").split(","), "=");
      if (tmp == null) {
        return false;
      }
      if (!tmp.containsKey("type")) {
        return false;
      }
      this.match = FlowMatchQueryFactory.create(tmp);
      if (this.match == null) {
        return false;
      }
      if (!this.match.parse()) {
        return false;
      }
    }
    if (this.queries.containsKey("path")) {
      Map<String, String> tmp =
          BasicQuery.convertToMap(
              this.queries.remove("path").split(","), "=");
      if (tmp == null) {
        return false;
      }
      if (tmp.containsKey("link_id")
          && tmp.size() == 1) {
        // do nothing
      } else if (tmp.containsKey("node_id")
          && tmp.containsKey("port_id")
          && tmp.size() == 2) {
        // do nothing
      } else if (tmp.containsKey("node_id")
          && !tmp.containsKey("port_id")
          && tmp.size() == 1) {
        // do nothing
      } else {
        return false;
      }
      this.path = tmp;
    }
    if (this.queries.containsKey("actions")) {
      Map<String, String> tmp =
          BasicQuery.convertToMap(this.queries.remove("actions")
              .split(","), "=");
      if (tmp == null) {
        return false;
      }
      if (!tmp.containsKey("type")) {
        return false;
      }
      this.actions = FlowActionQueryFactory.create(tmp);
      if (this.actions == null) {
        return false;
      }
      if (!this.actions.parse()) {
        return false;
      }
    }
    this.idleTimeout = BasicQuery.cretateLong(this.queries, "idle_timeout");
    this.hardTimeout = BasicQuery.cretateLong(this.queries, "hard_timeout");

    return true;
  }

  @Override
  public boolean matchExactly(BaseObject target) {

    if (!target.getClass().equals(OFPFlow.class)) {
      return false;
    }

    if (!super.matchExactly(target)) {
      return false;
    }

    OFPFlow flow = (OFPFlow) target;

    if (this.enabled != null && !this.enabled.equals(flow.isEnabled())) {
      return false;
    }

    if (this.status != null && !this.status.equals(messageValueOf(flow.getStatus()))) {
      return false;
    }

    if (this.match != null) {
      for (BasicFlowMatch match : flow.getMatches()) {
        if (!this.match.matchExactly(match)) {
          return false;
        }
      }
    }

    if (this.path == null) {
      // do nothing
    } else if (this.path.containsKey("link_id")) {
      // link
      if (!flow.getPath().contains(this.path.get("link_id"))) {
        return false;
      }
    } else if (this.path.containsKey("node_id")
        && this.path.containsKey("port_id")) {
      // node + port
      boolean found = false;
      String nodeId = this.path.get("node_id");
      String portId = this.path.get("port_id");

      for (String linkId : flow.getPath()) {
        Link link = this.topology.getLink(linkId);
        if (link == null) {
          continue;
        }
        if ((link.getDstNode().equals(nodeId)
            && link.getDstPort().equals(portId))
            || (link.getSrcNode().equals(nodeId)
            && link.getSrcPort().equals(portId))) {
          found = true;
          break;
        }
      }

      if (!found) {
        return false;
      }
    } else if (this.path.containsKey("node_id")) {
      // node
      boolean found = false;
      String nodeId = this.path.get("node_id");
      for (String linkId : flow.getPath()) {
        Link link = this.topology.getLink(linkId);
        if (link == null) {
          continue;
        }
        if (link.getDstNode().equals(nodeId)
            || link.getSrcNode().equals(nodeId)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }

    if (this.actions != null) {
      String edgeNodeId = this.actions.getEdgeNodeId();
      if (edgeNodeId != null) {
        if (!flow.getEdgeActions().containsKey(edgeNodeId)) {
          return false;
        }
        for (FlowAction action : flow.getEdgeActions().get(edgeNodeId)) {
          if (!this.actions.getClass().getSimpleName().matches(
              action.getType() + ".*")) {
            continue;
          }
          if (!this.actions.matchExactly(action)) {
            return false;
          }
        }
      } else {
        for (List<FlowAction> actions : flow.getEdgeActions().values()) {
          for (FlowAction action : actions) {
            if (!this.actions.getClass().getSimpleName().matches(
                action.getClass().getSimpleName() + ".*")) {
              continue;
            }
            if (!this.actions.matchExactly(action)) {
              return false;
            }
          }

        }
      }
    }

    if (this.idleTimeout != null
        && !this.idleTimeout.equals(flow.getIdleTimeout())) {
      return false;
    }

    if (this.hardTimeout != null
        && !this.hardTimeout.equals(flow.getHardTimeout())) {
      return false;
    }

    return true;
  }
}
