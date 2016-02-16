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
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Basic Flow class.
 *
 */
public class BasicFlow extends Flow implements Cloneable {
  public List<BasicFlowMatch> matches;
  public List<String> path;
  public Map<String, List<FlowAction>> edgeActions;

  /**
   * Constructor.
   */
  public BasicFlow() {
    super();

    matches = new ArrayList<BasicFlowMatch>();
    path = new ArrayList<String>();
    edgeActions = new HashMap<String, List<FlowAction>>();
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   */
  public BasicFlow(String flowId) {
    super(flowId);

    matches = new ArrayList<BasicFlowMatch>();
    path = new ArrayList<String>();
    edgeActions = new HashMap<String, List<FlowAction>>();
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   * @param owner Author of flow.
   * @param enabled The value the owner is usually set.
   *                "True":flow is activation. "False":flow is invalidation.
   * @param priority string of priority (0 - 65535: max)
   */
  public BasicFlow(String flowId, String owner, boolean enabled,
      String priority) {
    super(flowId, owner, enabled, priority);

    matches = new ArrayList<BasicFlowMatch>();
    path = new ArrayList<String>();
    edgeActions = new HashMap<String, List<FlowAction>>();
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
   * @param path list of link through flow.
   * @param edgeActions map of actions.
   * @param attributes map of attributes.
   */
  public BasicFlow(String version, String flowId, String owner,
      boolean enabled, String priority, String status,
      List<BasicFlowMatch> matches, List<String> path,
      Map<String, List<FlowAction>> edgeActions,
      Map<String, String> attributes) {
    super(version, flowId, owner, enabled, priority, status, attributes);

    if (matches == null) {
      this.matches = new ArrayList<BasicFlowMatch>();
    } else {
      this.matches = matches;
    }

    if (path == null) {
      this.path = new ArrayList<String>();
    } else {
      this.path = path;
    }

    if (edgeActions == null) {
      this.edgeActions = new HashMap<String, List<FlowAction>>();
    } else {
      this.edgeActions = edgeActions;
    }
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
    return BasicFlow.class.getSimpleName();
  }

  /**
   * Returns a list of match conditions.
   * @return list of  match conditions.
   */
  public List<BasicFlowMatch> getMatches() {
    return matches;
  }

  /**
   * Appends a match condition.
   * @param match match condition.
   * @return true.
   */
  public boolean addMatch(BasicFlowMatch match) {
    boolean ret = matches.add(match);
    return ret;
  }

  /**
   * Append all match condition of lists.
   * @param matches list of match condition.
   */
  public void putMatches(List<BasicFlowMatch> matches) {
    this.matches = matches;
  }

  /**
   * Remove a match condition.
   * @param match match condition.
   * @return removed match condition.
   */
  public boolean removeMatch(BasicFlowMatch match) {
    boolean ret = matches.remove(match);
    return ret;
  }

  /**
   * Returns a list of path.
   * @return list of path.
   */
  public final List<String> getPath() {
    return path;
  }

  /**
   * Append all path of lists.
   * @param path list of path.
   */
  public void putPath(List<String> path) {
    this.path = path;
  }

  /**
   * Append a link.
   * @param link path of link.
   * @return true.
   */
  public boolean addPath(String link) {
    boolean ret = path.add(link);
    return ret;
  }

  /**
   * Remove a path.
   * @param link path of link.
   * @return true if list contained path of link.
   */
  public boolean removePath(String link) {
    boolean ret = path.remove(link);
    return ret;
  }

  /**
   * Returns map of actions.
   * @return map of actions.
   */
  public final Map<String, List<FlowAction>> getEdgeActions() {
    return edgeActions;
  }

  /**
   * Returns a list of actions which associated the node ID.
   * @param nodeId node ID.
   * @return list of actions which associated the node ID.
   */
  public final List<FlowAction> getEdgeActions(String nodeId) {
    return edgeActions.get(nodeId);
  }

  /**
   * Sets a map of actions.
   * @param edgeActions map of actions.
   */
  public void putEdgeActions(Map<String, List<FlowAction>> edgeActions) {
    this.edgeActions = edgeActions;
  }

  /**
   * Add an action.
   * @param nodeId Added ID for node.
   * @param action Added action
   * @return true if an action is added.
   */
  public boolean addEdgeAction(String nodeId, FlowAction action) {
    List<FlowAction> actions = edgeActions.get(nodeId);

    if (actions == null) {
      actions = new ArrayList<FlowAction>();
      edgeActions.put(nodeId, actions);
    }

    boolean ret = actions.add(action);
    return ret;
  }

  /**
   * Delete an action related to specific node.
   *
   * @param nodeId Deleted ID for node.
   * @param action Deleted action.
   * @return true if an action could be deleted.
   */
  public boolean removeEdgeAction(String nodeId, FlowAction action) {
    List<FlowAction> actions = edgeActions.get(nodeId);

    if (actions == null) {
      return false;
    }
    if (!actions.remove(action)) {
      return false;
    }

    if (actions.isEmpty()) {
      edgeActions.remove(nodeId);
    }

    return true;
  }

  /**
   * Delete a match condition.
   * @param match match condition.
   * @return true if list contains match condition.
   */
  public boolean deleteMatch(BasicFlowMatch match) {
    boolean ret = matches.remove(match);
    return ret;
  }

  /**
   * Delete a path.
   * @param pathToDelete path.
   * @return true if list contains path.
   */
  public boolean deletePath(String pathToDelete) {
    boolean ret = path.remove(pathToDelete);
    return ret;
  }

  /**
   * Delete all actions related to the node.
   *
   * @param nodeId Deleted ID for node.
   * @return removed value of node with node ID.
   */
  public boolean deleteActions(String nodeId) {
    edgeActions.remove(nodeId);
    return true;
  }

  @Override
  public boolean readValue(Value value) {
    if (!super.readValue(value)) {
      return false;
    }

    MapValue map = value.asMapValue();

    Value matchesValue = map.get(ValueFactory.createRawValue("matches"));
    if (matchesValue != null && !matchesValue.isNilValue()) {
      ArrayValue matchesArray = matchesValue.asArrayValue();
      for (Value matchValue : matchesArray) {
        matches.add(FlowObject.readFlowMatchFrom(matchValue));
      }
    }

    Value pathValue = map.get(ValueFactory.createRawValue("path"));
    if (pathValue != null && !pathValue.isNilValue()) {
      ArrayValue pathArray = pathValue.asArrayValue();
      for (Value linkValue : pathArray.getElementArray()) {
        if (!linkValue.isNilValue()) {
          path.add(linkValue.asRawValue().getString());
        }
      }
    }

    Value edgeValue = map.get(ValueFactory.createRawValue("edge_actions"));
    if (edgeValue != null && !edgeValue.isNilValue()) {
      MapValue edgeMap = edgeValue.asMapValue();
      for (Entry<Value, Value> entry : edgeMap.entrySet()) {
        String nodeId = entry.getKey().asRawValue().getString();
        for (Value actionValue : entry.getValue().asArrayValue()) {
          FlowAction action = FlowObject
              .readBasicFlowActionFrom(actionValue);
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

    Value[] matchesArray = new Value[getMatches().size()];
    int num1 = 0;
    for (BasicFlowMatch match : getMatches()) {
      matchesArray[num1] = match.writeValue();
      ++num1;
    }
    values.put("matches", ValueFactory.createArrayValue(matchesArray));

    Value[] pathArray = new Value[getPath().size()];
    num1 = 0;
    for (String link : getPath()) {
      pathArray[num1] = ValueFactory.createRawValue(link);
      ++num1;
    }
    values.put("path", ValueFactory.createArrayValue(pathArray));

    Value[] edgeArray = new Value[getEdgeActions().size() * 2];
    num1 = 0;
    for (Entry<String, List<FlowAction>> entry : getEdgeActions()
        .entrySet()) {
      edgeArray[num1 * 2] = ValueFactory.createRawValue(entry.getKey());

      List<FlowAction> actions = entry.getValue();
      Value[] actionsArray = new Value[actions.size()];

      int num2 = 0;
      for (FlowAction action : actions) {
        actionsArray[num2] = action.writeValue();
        ++num2;
      }

      edgeArray[num1 * 2 + 1] = ValueFactory
          .createArrayValue(actionsArray);
      ++num1;
    }
    values.put("edge_actions", ValueFactory.createMapValue(edgeArray));

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

    if (!(obj instanceof BasicFlow)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final BasicFlow obj2 = (BasicFlow) obj;

    if (!obj2.getMatches().equals(this.matches)
        || !obj2.getPath().equals(this.path)
        || !obj2.getEdgeActions().equals(this.edgeActions)) {
      return false;
    }

    return true;
  }

  @Override
  public BasicFlow clone() {
    BasicFlow flow = new BasicFlow(
        getVersion(), getFlowId(), getOwner(),
        getEnabled(), getPriority(), getStatus(),
        new ArrayList<BasicFlowMatch>(),
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

  /* (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.flow.Flow#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("version", getVersion());
    sb.append("flowId", getFlowId());
    sb.append("owner", getOwner());
    sb.append("enabled", getEnabled());
    sb.append("priority", getPriority());
    sb.append("status", getStatus());
    sb.append("matches", matches);
    sb.append("path", path);
    sb.append("edgeActions", edgeActions);
    sb.append("attributes", getAttributes());

    return sb.toString();
  }

}
