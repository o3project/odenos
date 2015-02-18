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

package org.o3project.odenos.core.component.network.flow;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowType;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.query.FlowQuery;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Set of Flow class.
 *
 */
public class FlowSet extends OdenosMessage {
  public String type = "FlowSet";
  public Map<String, List<String>> priority;
  public Map<String, Flow> flows;

  /**
   * Constructor.
   */
  public FlowSet() {
    priority = new HashMap<String, List<String>>();
    flows = new HashMap<String, Flow>();
  }

  /**
   * Constructor.
   * @param version version of Object.
   * @param priority map of priorities.
   * @param flows map of flows.
   */
  public FlowSet(String version, Map<String, List<String>> priority,
      Map<String, Flow> flows) {
    if (version != null) {
      this.setVersion(version);
    }
    if (priority == null) {
      this.priority = new HashMap<String, List<String>>();
    } else {
      this.priority = priority;
    }
    if (flows == null) {
      this.flows = new HashMap<String, Flow>();
    } else {
      this.flows = flows;
    }
  }

  /**
   * Create a flow.
   * @param type type of flow.
   * @param pri priority.
   * @return flow object..
   */
  public Flow createFlow(FlowType type, String pri) {
    String flowId = getUniqueFlowId();
    return createFlow(type, pri, flowId);
  }

  /**
   * Create a flow instance.
   * @param type string of FlowType.
   * @param pri string of priority (0 - 65535: max)
   * @param flowId ID for flow.
   * @return flow object.
   */
  public Flow createFlow(FlowType type, String pri, String flowId) {
    Flow flow;
    if (type == null) {
      return null;
    }
    switch (type) {
      case BASIC_FLOW:
        flow = new BasicFlow(flowId);
        break;
      case OFP_FLOW:
        flow = new OFPFlow(flowId);
        break;
      default:
        return null;
    }
    flow.setPriority(pri);
    flow.updateVersion();
    flows.put(flowId, flow);
    if (priority.get(pri) == null) {
      priority.put(pri, new ArrayList<String>());
    }
    priority.get(pri).add(flowId);
    updateVersion();
    return flow;
  }

  /**
   * Create a flow.
   * @param msg message of flow.
   * @return flow object.
   */
  public Flow createFlow(Flow msg) {
    String flowId = getUniqueFlowId();
    return createFlow(flowId, msg, Flow.INITIAL_VERSION);
  }

  /**
   * Create a flow instance.
   * @param flowId ID for flow.
   * @param msg message of flow.
   * @param version version of Object.
   * @return flow instance.
   */
  public Flow createFlow(String flowId, Flow msg, String version) {
    if (flowId == null) {
      return null;
    }
    Flow flow = getFlow(flowId);
    if (msg == null || msg.getType() == null
        || FlowType.messageValueOf(msg.getType()) == null) {
      return null;
    }
    switch (FlowType.messageValueOf(msg.getType())) {
      case BASIC_FLOW:
        BasicFlow bflow = (BasicFlow) flow;
        if (bflow == null) {
          bflow = new BasicFlow();
        }
        BasicFlow bmsg = (BasicFlow) msg;
        bflow.setVersion(version);
        bflow.setFlowId(flowId);
        bflow.setOwner(bmsg.getOwner());
        bflow.setEnabled(bmsg.getEnabled());
        bflow.setPriority(bmsg.getPriority());
        bflow.setStatus(bmsg.getStatus());
        bflow.putMatches(bmsg.getMatches());
        bflow.putPath(bmsg.getPath());
        bflow.putEdgeActions(bmsg.getEdgeActions());
        bflow.putAttributes(bmsg.getAttributes());
        flow = bflow;
        break;
      case OFP_FLOW:
        OFPFlow oflow = (OFPFlow) flow;
        if (oflow == null) {
          oflow = new OFPFlow();
        }
        OFPFlow omsg = (OFPFlow) msg;
        oflow.setVersion(version);
        oflow.setFlowId(flowId);
        oflow.setOwner(omsg.getOwner());
        oflow.setEnabled(omsg.getEnabled());
        oflow.setPriority(omsg.getPriority());
        oflow.setStatus(omsg.getStatus());
        oflow.putMatches(omsg.getMatches());
        oflow.setIdleTimeout(omsg.getIdleTimeout());
        oflow.setHardTimeout(omsg.getHardTimeout());
        oflow.putPath(omsg.getPath());
        oflow.putEdgeActions(omsg.getEdgeActions());
        oflow.putAttributes(omsg.getAttributes());
        flow = oflow;
        break;
      default:
        return null;
    }
    flow.updateVersion();
    flows.put(flowId, flow);
    if (priority.get(msg.getPriority()) == null) {
      priority.put(msg.getPriority(), new ArrayList<String>());
    }
    if (!priority.get(msg.getPriority()).contains(flowId)) {
      priority.get(msg.getPriority()).add(flowId);
    }
    updateVersion();
    return flow;
  }

  /**
   * Returns a flow with the flow ID.
   * @param flowId flow ID.
   * @return value of flow.
   */
  public Flow getFlow(String flowId) {
    return flows.get(flowId);
  }

  /**
   * Returns map of flows.
   * @return map of flows.
   */
  public Map<String, Flow> getFlows() {
    if (flows == null) {
      return new HashMap<String, Flow>();
    }
    return flows;
  }

  /**
   * Remove the mapping for flow ID from map.
   * @param flowId flow ID.
   * @return previous value flow.
   */
  public Flow deleteFlow(String flowId) {
    Flow flow = flows.get(flowId);
    return deleteFlow(flow);
  }

  /**
   * Delete a flow.
   * @param flow deleted flow.
   * @return deleted flow.
   */
  public Flow deleteFlow(Flow flow) {
    updateVersion();
    if (flow == null) {
      return null;
    }
    String pri = flow.getPriority();
    priority.get(pri).remove(flow.flowId);
    return flows.remove(flow.flowId);
  }

  protected String getUniqueFlowId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (flows.containsKey(id));

    return id;
  }

  private String getPriority(String flowId) {
    for (Map.Entry<String, List<String>> e : priority.entrySet()) {
      if (e.getValue().contains(flowId)) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Return flow message.
   * @param query queries string.
   * @return flow set instance.
   */
  public FlowSet getFlowMessages(FlowQuery query) {
    Map<String, Flow> flows = new HashMap<String, Flow>();
    Map<String, List<String>> priorities = new HashMap<String, List<String>>();
    for (Flow flow : this.flows.values()) {
      if (query.matchExactly(flow)) {
        String flowId = flow.getFlowId();
        String pri = getPriority(flowId);
        flows.put(flowId, flow);
        if (!priorities.containsKey(pri)) {
          priorities.put(pri, new ArrayList<String>());
        }
        priorities.get(pri).add(flowId);
      }
    }
    FlowSet msg = new FlowSet(getVersion(), priorities, flows);
    return msg;
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();

    Value versionValue = map.get(ValueFactory.createRawValue("version"));
    if (versionValue != null && !versionValue.isNilValue()) {
      setVersion(versionValue.asRawValue().getString());
    }

    priority.clear();
    Value priorityValue = map.get(ValueFactory.createRawValue("priority"))
        .asMapValue();
    if (priorityValue != null && !priorityValue.isNilValue()) {
      MapValue priorityMap = priorityValue.asMapValue();
      for (Entry<Value, Value> entry : priorityMap.entrySet()) {
        String key = entry.getKey().asRawValue().getString();

        ArrayValue priorityVarray = entry.getValue().asArrayValue();
        List<String> priorityArray = new ArrayList<String>();
        for (Value p : priorityVarray) {
          priorityArray.add(p.asRawValue().getString());
        }

        priority.put(key, priorityArray);
      }
    }

    flows.clear();
    Value flowsValue = map.get(ValueFactory.createRawValue("flows"));
    if (flowsValue != null && !flowsValue.isNilValue()) {
      MapValue flowsMap = flowsValue.asMapValue();
      for (Entry<Value, Value> entry : flowsMap.entrySet()) {
        String key = entry.getKey().asRawValue().getString();
        Flow msg = FlowObject.readFlowMessageFrom(entry.getValue());

        flows.put(key, msg);
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put("type", ValueFactory.createRawValue("FlowSet"));
    values.put("version", ValueFactory.createRawValue(getVersion()));

    Value[] priorityValues = new Value[priority.size() * 2];
    int num1 = 0;
    for (Entry<String, List<String>> entry : priority.entrySet()) {
      priorityValues[num1 * 2] = ValueFactory.createRawValue(entry
          .getKey());
      if (entry.getValue() != null) {

        Value[] priorityInner = new Value[entry.getValue().size()];
        int num2 = 0;
        for (String p : entry.getValue()) {
          priorityInner[num2] = ValueFactory.createRawValue(p);
          ++num2;
        }
        priorityValues[num1 * 2 + 1] = ValueFactory
            .createArrayValue(priorityInner);
      } else {
        priorityValues[num1 * 2 + 1] = ValueFactory.createNilValue();
      }
      ++num1;
    }

    MapValue priorityMap = ValueFactory.createMapValue(priorityValues);
    values.put("priority", priorityMap);

    Value[] flowsValues = new Value[flows.size() * 2];
    num1 = 0;
    for (Entry<String, Flow> entry : flows.entrySet()) {
      flowsValues[num1 * 2] = ValueFactory.createRawValue(entry.getKey());
      flowsValues[num1 * 2 + 1] = entry.getValue().writeValue();
      ++num1;
    }

    MapValue flowsMap = ValueFactory.createMapValue(flowsValues);
    values.put("flows", flowsMap);

    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("version", getVersion());
    sb.append("priority", priority);
    sb.append("flows", flows);

    return sb.toString();
  }

}
