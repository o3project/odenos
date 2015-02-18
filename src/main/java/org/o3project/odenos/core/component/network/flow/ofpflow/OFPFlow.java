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

import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * OFP Flow class.
 *
 */
public class OFPFlow extends BasicFlow {
  private long idleTimeout;
  private long hardTimeout;

  /**
   * Constructor.
   */
  public OFPFlow() {
    super();
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   */
  public OFPFlow(String flowId) {
    super(flowId);
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   * @param owner Author of flow.
   * @param enabled The value the owner is usually set.
   *                "True":flow is activation. "False":flow is invalidation.
   * @param priority string of priority (0 - 65535: max)
   */
  public OFPFlow(String flowId, String owner, boolean enabled,
      String priority) {
    super(flowId, owner, enabled, priority);
  }

  /**
   * Constructor.
   * @param version version of Object.
   * @param flowId ID for flow.
   * @param owner Author of flow.
   * @param enabled The value the owner is usually set.
   *                "True":flow is activation. "False":flow is invalidation.
   * @param priority string of priority (0 - 65535: max)
   * @param status string of flow status.
   * @param matches match conditions.
   * @param idleTimeout The number of seconds until the time-out.
   * @param hardTimeout The number of seconds until the time-out.
   * @param path list of link through flow.
   * @param edgeActions map of actions.
   * @param attributes map of attributes.
   */
  public OFPFlow(String version, String flowId, String owner,
      boolean enabled, String priority,
      String status, List<BasicFlowMatch> matches,
      Long idleTimeout, Long hardTimeout,
      List<String> path, Map<String, List<FlowAction>> edgeActions,
      Map<String, String> attributes) {
    super(version, flowId, owner, enabled,
        priority, status, matches, path, edgeActions, attributes);

    this.idleTimeout = idleTimeout;
    this.hardTimeout = hardTimeout;
  }

  @Override
  public boolean validate() {
    for (BasicFlowMatch m : matches) {
      if (!m.validate()) {
        return false;
      }
    }
    for (List<FlowAction> l : edgeActions.values()) {
      for (FlowAction a : l) {
        if (!a.validate()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public String getType() {
    return OFPFlow.class.getSimpleName();
  }

  /**
   * Return the number of seconds of the idle time-out.
   * @return the number of seconds of the idle time-out.
   */
  public Long getIdleTimeout() {
    return idleTimeout;
  }

  /**
   * Return the number of seconds of the hard time-out.
   * @return the number of seconds of the hard time-out.
   */
  public Long getHardTimeout() {
    return hardTimeout;
  }

  /**
   * Appends a match condition.
   * @param match match condition.
   */
  public void addMatch(OFPFlowMatch match) {
    matches.add(match);
  }

  /**
   * Removes a match condition.
   * @param match match condition.
   * @return removed match condition.
   */
  public boolean removeMatch(OFPFlowMatch match) {
    boolean ret = matches.remove(match);
    return ret;
  }

  /**
   * Set a number of seconds of the idle time-out.
   * @param idleTimeout number of seconds of the idle time-out.
   */
  public void setIdleTimeout(long idleTimeout) {
    this.idleTimeout = idleTimeout;
  }

  /**
   * Set a number of seconds of the hard time-out.
   * @param hardTimeout number of seconds of the hard time-out.
   */
  public void setHardTimeout(long hardTimeout) {
    this.hardTimeout = hardTimeout;
  }

  @Override
  public boolean readValue(Value value) {
    if (!super.readValue(value)) {
      return false;
    }

    MapValue map = value.asMapValue();

    Value idleTimeoutValue = map.get(ValueFactory
        .createRawValue("idle_timeout"));
    if (idleTimeoutValue != null && !idleTimeoutValue.isNilValue()) {
      idleTimeout = idleTimeoutValue.asIntegerValue().getLong();
    }

    Value hardTimeoutValue = map.get(ValueFactory
        .createRawValue("hard_timeout"));
    if (hardTimeoutValue != null && !hardTimeoutValue.isNilValue()) {
      hardTimeout = hardTimeoutValue.asIntegerValue().getLong();
    }

    Value edgeValue = map.get(ValueFactory.createRawValue("edge_actions"));
    if (edgeActions != null && !edgeValue.isNilValue()) {
      MapValue edgeMap = edgeValue.asMapValue();
      for (Entry<Value, Value> entry : edgeMap.entrySet()) {
        String nodeId = entry.getKey().asRawValue().getString();
        for (Value actionValue : entry.getValue().asArrayValue()) {
          FlowAction action = FlowObject
              .readOFPFlowActionFrom(actionValue);
          if (action != null) {
            addEdgeAction(nodeId, action);
          }
        }
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }
    values.put("idle_timeout", ValueFactory.createIntegerValue(idleTimeout));
    values.put("hard_timeout", ValueFactory.createIntegerValue(hardTimeout));
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

    if (!(obj instanceof OFPFlow)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlow obj2 = (OFPFlow) obj;

    if (!obj2.getIdleTimeout().equals(this.idleTimeout)) {
      return false;
    }

    if (!obj2.getHardTimeout().equals(this.hardTimeout)) {
      return false;
    }

    return true;
  }

  @Override
  public OFPFlow clone() {
    OFPFlow flow = new OFPFlow(
        getVersion(), getFlowId(), getOwner(),
        getEnabled(), getPriority(), getStatus(),
        new ArrayList<BasicFlowMatch>(),
        getIdleTimeout(), getHardTimeout(),
        new ArrayList<String>(getPath()),
        new HashMap<String, List<FlowAction>>(),
        new HashMap<String, String>(getAttributes())
        );
    for (BasicFlowMatch m : getMatches()) {
      flow.getMatches().add(m.clone());
    }
    for (Entry<String, List<FlowAction>> entry : getEdgeActions()
        .entrySet()) {
      List<FlowAction> list = new ArrayList<FlowAction>();
      for (FlowAction act : entry.getValue()) {
        list.add(act.clone());
      }
      flow.getEdgeActions().put(entry.getKey(), list);
    }
    return flow;
  }
}
