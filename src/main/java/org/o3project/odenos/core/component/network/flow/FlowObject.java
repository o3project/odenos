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

import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlIn;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlOut;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionGroupAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;

/**
 * Classes in FlowObject represent flows defined in network. As same as
 * TopologyObject, each flow is represented in two ways: one for inner structure
 * and one for communication.
 *
 */
public class FlowObject {

  // *** Classes below represent Message used in communication ***

  /**
   * Flow status.
   *
   * <p>
   * status means:
   * </p>
   * <ul>
   * <li>none: Definition of Flow is, the state is not enabled.</li>
   * <li>establishing:Set during the flow of the physical layer by the driver.</li>
   * <li>established: Flow enabled.</li>
   * <li>teardown: The removal process during the flow of the physical layer by the driver.</li>
   * <li>failed: failure to flow.</li>
   * </ul>
   */
  public static enum FlowStatus {
    NONE("none"),
    ESTABLISHING("establishing"),
    ESTABLISHED("established"),
    TEARDOWN("teardown"),
    FAILED("failed");

    private String name;

    private FlowStatus(String name) {
      this.name = name;
    }

    /**
     * Returns the FlowStatus representation of the string argument.
     * @param name string of status.
     * @return FlowStatus representation of the string argument.
     */
    public static FlowStatus messageValueOf(String name) {
      FlowStatus[] values = values();
      if (name == null) {
        return null;
      }
      for (FlowStatus s : values) {
        if (name.equals(s.toString())) {
          return s;
        }
      }
      return null;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * Type of Flow.
   *
   * <ul>
   * <li>BasicFlow</li>
   * <li>OFPFlow</li>
   * </ul>
   */
  public static enum FlowType {
    BASIC_FLOW("BasicFlow"),
    OFP_FLOW("OFPFlow");

    private String name;

    private FlowType(String name) {
      this.name = name;
    }

    /**
     * Returns the FlowType representation of the string argument.
     * @param name string of status.
     * @return FlowType representation of the string argument.
     */
    public static FlowType messageValueOf(String name) {
      FlowType[] values = values();
      for (FlowType s : values) {
        if (name.equals(s.toString())) {
          return s;
        }
      }
      return null;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * Read a flow message.
   * @param value flow message
   * @return flow instance.
   */
  public static Flow readFlowMessageFrom(Value value) {
    Flow flow;

    try {
      if (value == null || value.isNilValue()
          || value.asMapValue().get(ValueFactory.createRawValue("type")) == null) {
        return null;
      }
    } catch (Exception e) {
      //e.printStackTrace();
      return null;
    }

    String type = value.asMapValue().get(ValueFactory.createRawValue("type"))
        .asRawValue().getString();
    switch (type) {
      case "BasicFlow":
        flow = new BasicFlow();
        flow.readValue(value);
        break;
      case "OFPFlow":
        flow = new OFPFlow();
        flow.readValue(value);
        break;
      default:
        flow = null;
        break;
    }

    return flow;
  }

  /**
   * Read a flow match.
   * @param value flow match
   * @return flow match instance.
   */
  public static BasicFlowMatch readFlowMatchFrom(Value value) {

    MapValue map;
    try {
      map = value.asMapValue();
    } catch (Exception e) {
      //e.printStackTrace();
      return null;
    }

    BasicFlowMatch match;
    if (map.get(ValueFactory.createRawValue("type")) == null) {
      return null;
    }
    String type = map.get(ValueFactory.createRawValue("type")).asRawValue().getString();
    switch (type) {
      case "BasicFlowMatch":
        match = new BasicFlowMatch();
        match.readValue(value);
        break;
      case "OFPFlowMatch":
        match = new OFPFlowMatch();
        match.readValue(value);
        break;
      default:
        match = null;
        break;
    }

    return match;
  }

  // *** Classes below represent action of BasicFlow ***

  /**
   * Read a basic flow action.
   * @param value basic flow action
   * @return flow action instance.
   */
  public static FlowAction readBasicFlowActionFrom(Value value) {

    MapValue map;
    try {
      map = value.asMapValue();
    } catch (Exception e) {
      //e.printStackTrace();
      return null;
    }

    FlowAction action;
    if (map.get(ValueFactory.createRawValue("type")) == null) {
      return null;
    }
    String type = map.get(ValueFactory.createRawValue("type")).asRawValue().getString();
    switch (type) {
      case "FlowActionOutput":
        action = new FlowActionOutput();
        action.readValue(value);
        break;
      default:
        action = null;
        break;
    }

    return action;
  }

  // *** Classes below represent actions of OFPFlow ***

  /**
   * Read a OFP flow action.
   * @param value OFP flow action
   * @return OFP flow action instance.
   */
  public static FlowAction readOFPFlowActionFrom(Value value) {

    MapValue map;
    try {
      map = value.asMapValue();
    } catch (Exception e) {
      //e.printStackTrace();
      return null;
    }

    FlowAction action;

    if (map.get(ValueFactory.createRawValue("type")) == null) {
      return null;
    }
    String type = map.get(ValueFactory.createRawValue("type"))
        .asRawValue().getString();
    switch (type) {
      case "OFPFlowActionCopyTtlIn":
        action = new OFPFlowActionCopyTtlIn();
        break;
      case "OFPFlowActionCopyTtlOut":
        action = new OFPFlowActionCopyTtlOut();
        break;
      case "OFPFlowActionDecIpTtl":
        action = new OFPFlowActionDecIpTtl();
        break;
      case "OFPFlowActionDecMplsTtl":
        action = new OFPFlowActionDecMplsTtl();
        break;
      case "OFPFlowActionExperimenter":
        action = new OFPFlowActionExperimenter();
        break;
      case "OFPFlowActionGroupAction":
        action = new OFPFlowActionGroupAction();
        break;
      case "OFPFlowActionPopMpls":
        action = new OFPFlowActionPopMpls();
        break;
      case "OFPFlowActionPopPbb":
        action = new OFPFlowActionPopPbb();
        break;
      case "OFPFlowActionPopVlan":
        action = new OFPFlowActionPopVlan();
        break;
      case "OFPFlowActionPushMpls":
        action = new OFPFlowActionPushMpls();
        break;
      case "OFPFlowActionPushPbb":
        action = new OFPFlowActionPushPbb();
        break;
      case "OFPFlowActionPushVlan":
        action = new OFPFlowActionPushVlan();
        break;
      case "OFPFlowActionSetField":
        action = new OFPFlowActionSetField();
        break;
      case "OFPFlowActionSetIpTtl":
        action = new OFPFlowActionSetIpTtl();
        break;
      case "OFPFlowActionSetMplsTtl":
        action = new OFPFlowActionSetMplsTtl();
        break;
      case "OFPFlowActionSetQueue":
        action = new OFPFlowActionSetQueue();
        break;
      default:
        return null;
    }
    action.readValue(value);
    return action;
  }

}
