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
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Flow class.
 *
 */
public class Flow extends OdenosMessage implements Cloneable {
  public static String DEFAULT_PRIORITY = "32768";
  String flowId;
  private String owner;
  private boolean enabled;
  private String priority;
  private String status;

  /* NetworkElements */
  public static final String TYPE = "type";
  public static final String VERSION = "version";
  public static final String FLOW_ID = "flow_id";
  public static final String OWNER = "owner";
  public static final String ENABLED = "enabled";
  public static final String PRIORITY = "priority";
  public static final String STATUS = "status";
  public static final String ATTRIBUTES = "attributes";

  /* AttrbuteElements */
  public static final String REQ_BANDWIDTH = "req_bandwidth";
  public static final String REQ_LATENCY = "req_latency";
  public static final String BANDWIDTH = "bandwidth";
  public static final String LATENCY = "latency";


  /**
   * Constructor.
   */
  public Flow() {
    this.initElements(this.INITIAL_VERSION,
       null, "unknown", true, this.DEFAULT_PRIORITY, FlowStatus.NONE.toString(), null);
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   */
  public Flow(String flowId) {
    this.initElements(this.INITIAL_VERSION,
       flowId, "unknown", true, this.DEFAULT_PRIORITY, FlowStatus.NONE.toString(), null);
  }

  /**
   * Constructor.
   * @param flowId ID for flow.
   * @param owner Author of flow.
   * @param enabled The value the owner is usually set.
   *                "True":flow is activation. "False":flow is invalidation.
   * @param priority value:0-65535(max priority).
   */
  public Flow(String flowId, String owner, boolean enabled, String priority) {
    this.initElements(this.INITIAL_VERSION, 
      flowId, owner, enabled, priority, FlowStatus.NONE.toString(), null);
  }

  /**
   * Constructor.
   * @param version version of Object.
   * @param flowId ID for flow.
   * @param owner Author of flow.
   * @param enabled The value the owner is usually set.
   *                "True":flow is activation. "False":flow is invalidation.
   * @param priority value:0-65535(max priority).
   * @param status Flow status.
   * @param attributes Attributes.
   */
  public Flow(String version, String flowId, String owner,
      boolean enabled, String priority, String status,
      Map<String, String> attributes) {

    this.initElements(version, flowId, owner, enabled, priority, status, attributes);
  }

  protected void initElements(
      String version, String flowId, String owner,
      boolean enabled, String priority, String status,
      Map<String, String> attributes) {

    this.setType("Flow");
    this.setVersion(version);
    this.setFlowId(flowId);
    this.setOwner(owner);
    this.setEnabled(enabled);
    this.setPriority(priority);
    this.setStatus(status);

    if(attributes != null) {
      this.putAttributes(attributes);
    }
  }

  /**
   * Returns a type of flow.
   * @return type of flow.
   */
  public String getType() {
    return Flow.class.getSimpleName();
  }

  /**
   * Returns true if parameter is valid.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    return true;
  }

  /**
   * Returns a flow ID.
   * @return flow ID.
   */
  public String getFlowId() {
    return flowId;
  }

  /**
   * Sets a flow ID.
   * @param flowId flow ID.
   */
  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }

  /**
   * Returns a author of flow.
   * @return author of flow.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Sets a author of flow.
   * @param owner author of flow.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Returns true if flow is activation.
   * @return true if flow is activation.
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * Sets a activation of flow.
   * @param enabled activation of flow.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns a priority.
   * @return priority.
   */
  public String getPriority() {
    return priority;
  }

  /**
   * Sets a priority.
   * @param priority priority.
   */
  public void setPriority(String priority) {
    this.priority = priority;
  }

  /**
   * Returns a status of flow.
   * @return status of flow.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets a status of flow.
   * @param status of flow.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Returns true if flow is activation.
   * @return true if flow is activation.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns a value of status.
   * @return value of status.
   */
  public FlowStatus getStatusValue() {
    return FlowStatus.messageValueOf(status);
  }

  /**
   * Set Flow status.
   * @param status Flow status.
   */
  public void setStatusValue(FlowStatus status) {
    if (status == null) {
      return;
    }
    this.status = status.toString();
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();

    Value versionValue = map.get(ValueFactory.createRawValue(VERSION));
    if (versionValue != null && !versionValue.isNilValue()) {
      setVersion(versionValue.asRawValue().getString());
    }
    Value flowIdValue = map.get(ValueFactory.createRawValue(FLOW_ID));
    if (flowIdValue != null && !flowIdValue.isNilValue()) {
      flowId = flowIdValue.asRawValue().getString();
    }
    Value ownerValue = map.get(ValueFactory.createRawValue(OWNER));
    if (ownerValue != null && !ownerValue.isNilValue()) {
      owner = ownerValue.asRawValue().getString();
    }
    if (map.get(ValueFactory.createRawValue(ENABLED)) != null) {
      enabled = map.get(ValueFactory.createRawValue(ENABLED))
          .asBooleanValue().getBoolean();
    }
    Value priorityValue = map.get(ValueFactory.createRawValue(PRIORITY));
    if (priorityValue == null || priorityValue.isNilValue()) {
      priority = DEFAULT_PRIORITY;
    } else {
      priority = map.get(ValueFactory.createRawValue(PRIORITY))
          .asRawValue().getString();
    }
    Value statusValue = ValueFactory.createRawValue(STATUS);
    if (statusValue != null && map.get(statusValue) != null
        && !map.get(statusValue).isNilValue()) {
      status = map.get(statusValue).asRawValue().getString();
    }

    if (map.get(ValueFactory.createRawValue(ATTRIBUTES)) != null
        && !map.get(ValueFactory.createRawValue(ATTRIBUTES))
            .isNilValue()) {
      MapValue attrMap = map.get(
          ValueFactory.createRawValue(ATTRIBUTES)).asMapValue();
      for (Entry<Value, Value> entry : attrMap.entrySet()) {
        String mapKey = entry.getKey().asRawValue().getString();
        String mapValue = null;
        if (!entry.getValue().isNilValue()) {
          mapValue = entry.getValue().asRawValue().getString();
        }
        putAttribute(mapKey, mapValue);
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put(TYPE, ValueFactory.createRawValue(getType()));
    values.put(VERSION, ValueFactory.createRawValue(getVersion()));
    values.put(FLOW_ID, ValueFactory.createRawValue(getFlowId()));
    values.put(OWNER, ValueFactory.createRawValue(getOwner()));
    values.put(ENABLED, ValueFactory.createBooleanValue(getEnabled()));
    values.put(PRIORITY, ValueFactory.createRawValue(getPriority()));
    if (getStatus() != null) {
      values.put(STATUS, ValueFactory.createRawValue(getStatus()));
    }

    Value[] attributesArray = new Value[getAttributes().size() * 2];
    int num = 0;
    for (Entry<String, String> entry : getAttributes().entrySet()) {
      attributesArray[num * 2] =
          ValueFactory.createRawValue(entry.getKey());
      attributesArray[num * 2 + 1] =
          ValueFactory.createRawValue(entry.getValue());
      ++num;
    }
    values.put(ATTRIBUTES, ValueFactory.createMapValue(attributesArray));

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

    if (!(obj instanceof Flow)) {
      return false;
    }

    final Flow obj2 = (Flow) obj;

    if (obj2.getFlowId() == null && this.getFlowId() != null
        || obj2.getFlowId() != null && this.getFlowId() == null
        || obj2.getOwner() == null && this.getOwner() != null
        || obj2.getOwner() != null && this.getOwner() == null
        || obj2.getPriority() == null && this.getPriority() != null
        || obj2.getPriority() != null && this.getPriority() == null
        || obj2.getStatus() == null && this.getStatus() != null
        || obj2.getStatus() != null && this.getStatus() == null) {
      return false;
    }

    if (obj2.getType().equals(this.getType())
        && obj2.getVersion().equals(this.getVersion())
        && (obj2.getFlowId() == null && this.getFlowId() == null
        || obj2.getFlowId().equals(this.getFlowId()))
        && (obj2.getOwner() == null && this.getOwner() == null
        || obj2.getOwner().equals(this.getOwner()))
        && obj2.getEnabled() == this.getEnabled()
        && (obj2.getPriority() == null && this.getPriority() == null
        || obj2.getPriority().equals(this.getPriority()))
        && (obj2.getStatus() == null && this.getStatus() == null
        || obj2.getStatus().equals(this.getStatus()))
        && obj2.getAttributes().equals(this.getAttributes())) {
      return true;
    }
    return false;
  }

  @Override
  public Flow clone() {
    Flow flow = new Flow();

    flow.setEnabled(getEnabled());
    flow.setFlowId(getFlowId());
    flow.setOwner(getOwner());
    flow.setPriority(getPriority());
    flow.setStatus(getStatus());
    flow.setVersion(getVersion());
    flow.putAttributes(new HashMap<String, String>(getAttributes()));

    return flow;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append(VERSION, getVersion());
    sb.append(FLOW_ID, flowId);
    sb.append(OWNER, owner);
    sb.append(ENABLED, enabled);
    sb.append(PRIORITY, priority);
    sb.append(STATUS, status);
    sb.append(ATTRIBUTES, getAttributes());

    return sb.toString();

  }

}
