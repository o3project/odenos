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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for OFPFlow.
 *
 * 
 *
 */
public class OFPFlowTest {

  private OFPFlow target;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    target = spy(new OFPFlow());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#OFPFlow()}
   * .
   */
  @Test
  public final void testOFPFlow() {
    /*
     * test
     */
    OFPFlow target = new OFPFlow();

    /*
     * check
     */
    assertThat(target.getIdleTimeout(), is(0L));
    assertThat(target.getHardTimeout(), is(0L));
    assertThat(target.matches.size(), is(0));
    assertThat(target.path.size(), is(0));
    assertThat(target.edgeActions.size(), is(0));

    String flowId = WhiteboxImpl.getInternalState(target, "flowId");
    assertThat(flowId, is(nullValue()));

    String owner = WhiteboxImpl.getInternalState(target, "owner");
    assertThat(owner, is(nullValue()));

    boolean enabled = WhiteboxImpl.getInternalState(target, "enabled");
    assertThat(enabled, is(false));

    String priority = WhiteboxImpl.getInternalState(target, "priority");
    assertThat(priority, is(nullValue()));

    String status = WhiteboxImpl.getInternalState(target, "status");
    assertThat(status, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#OFPFlow(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowString() {
    /*
     * test
     */
    OFPFlow target = new OFPFlow("FlowId");

    /*
     * check
     */
    assertThat(target.getIdleTimeout(), is(0L));
    assertThat(target.getHardTimeout(), is(0L));
    assertThat(target.matches.size(), is(0));
    assertThat(target.path.size(), is(0));
    assertThat(target.edgeActions.size(), is(0));

    String flowId = WhiteboxImpl.getInternalState(target, "flowId");
    assertThat(flowId, is("FlowId"));

    String owner = WhiteboxImpl.getInternalState(target, "owner");
    assertThat(owner, is(nullValue()));

    boolean enabled = WhiteboxImpl.getInternalState(target, "enabled");
    assertThat(enabled, is(false));

    String priority = WhiteboxImpl.getInternalState(target, "priority");
    assertThat(priority, is(nullValue()));

    String status = WhiteboxImpl.getInternalState(target, "status");
    assertThat(status, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#OFPFlow(java.lang.String, java.lang.String, boolean, java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowStringStringBooleanString() {
    /*
     * test
     */
    OFPFlow target = new OFPFlow("FlowId", "Owner", true, "Priority");

    /*
     * check
     */
    assertThat(target.getIdleTimeout(), is(0L));
    assertThat(target.getHardTimeout(), is(0L));
    assertThat(target.matches.size(), is(0));
    assertThat(target.path.size(), is(0));
    assertThat(target.edgeActions.size(), is(0));

    String flowId = WhiteboxImpl.getInternalState(target, "flowId");
    assertThat(flowId, is("FlowId"));

    String owner = WhiteboxImpl.getInternalState(target, "owner");
    assertThat(owner, is("Owner"));

    boolean enabled = WhiteboxImpl.getInternalState(target, "enabled");
    assertThat(enabled, is(true));

    String priority = WhiteboxImpl.getInternalState(target, "priority");
    assertThat(priority, is("Priority"));

    String status = WhiteboxImpl.getInternalState(target, "status");
    assertThat(status, is("none"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#OFPFlow(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.List, java.lang.Long, java.lang.Long, java.util.List, java.util.Map, java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowStringMapOfStringString() {

    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("node_id", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    edgeActions.put("node01", actions);

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    /*
     * test
     */
    target = new OFPFlow("123", "FlowId", "Owner", true, "Priority",
        "Status", matches, 10L, 10L, path, edgeActions, attributes);

    /*
     * check
     */
    assertThat(target.getIdleTimeout(), is(10L));
    assertThat(target.getHardTimeout(), is(10L));
    assertThat(target.matches.size(), is(1));
    assertThat(target.path.size(), is(1));
    assertThat(target.edgeActions.size(), is(1));

    String flowId = WhiteboxImpl.getInternalState(target, "flowId");
    assertThat(flowId, is("FlowId"));

    String owner = WhiteboxImpl.getInternalState(target, "owner");
    assertThat(owner, is("Owner"));

    boolean enabled = WhiteboxImpl.getInternalState(target, "enabled");
    assertThat(enabled, is(true));

    String priority = WhiteboxImpl.getInternalState(target, "priority");
    assertThat(priority, is("Priority"));

    String status = WhiteboxImpl.getInternalState(target, "status");
    assertThat(status, is("Status"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#validate()}
   * .
   */
  @Test
  public final void testValidate() {
    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("node_id", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    Map<String, List<FlowAction>> edgeActions = null;

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    OFPFlow target = new OFPFlow("123", "FlowId", "Owner", true,
        "Priority",
        "Status", matches, 10L, 10L, path, edgeActions, attributes);

    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));
    target.addEdgeAction("NodeId01", actions.get(0));

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */

    assertThat(target.matches.size(), is(1));
    assertThat(target.edgeActions.size(), is(1));
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#validate()}
   * .
   */
  @Test
  public final void testValidateFalseBasicFlowMatch() {
    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    Map<String, List<FlowAction>> edgeActions = null;

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    OFPFlow target = new OFPFlow("123", "FlowId", "Owner", true,
        "Priority",
        "Status", matches, 10L, 10L, path, edgeActions, attributes);

    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));
    target.addEdgeAction("NodeId01", actions.get(0));

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */

    assertThat(target.matches.size(), is(1));
    assertThat(target.edgeActions.size(), is(1));
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    String result = target.getType();

    assertThat(result, is("OFPFlow"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#getIdleTimeout()}
   * .
   */
  @Test
  public final void testGetIdleTimeout() {
    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("node_id", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    Map<String, List<FlowAction>> edgeActions = null;

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    OFPFlow target = new OFPFlow("123", "FlowId", "Owner", true,
        "Priority",
        "Status", matches, 10L, 15L, path, edgeActions, attributes);

    /*
     * test
     */
    Long result = target.getIdleTimeout();

    /*
     * check
     */
    assertThat(result, is(10L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#getHardTimeout()}
   * .
   */
  @Test
  public final void testGetHardTimeout() {
    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("node_id", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    Map<String, List<FlowAction>> edgeActions = null;

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    OFPFlow target = new OFPFlow("123", "FlowId", "Owner", true,
        "Priority",
        "Status", matches, 10L, 15L, path, edgeActions, attributes);

    /*
     * test
     */
    Long result = target.getHardTimeout();

    /*
     * check
     */
    assertThat(result, is(15L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#addMatch(org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch)}
   * .
   */
  @Test
  public final void testAddMatchOFPFlowMatch() {
    OFPFlow target = new OFPFlow();
    BasicFlowMatch match = new BasicFlowMatch("node_id", "port_id");

    target.addMatch(match);

    assertThat(target.matches.size(), is(1));
    assertThat(target.matches.get(0), is(match));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#removeMatch(org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch)}
   * .
   */
  @Test
  public final void testRemoveMatchOFPFlowMatch() {
    /*
     * set
     */
    OFPFlow target = new OFPFlow();
    BasicFlowMatch match01 = new BasicFlowMatch("node_id01", "port_id01");
    BasicFlowMatch match02 = new BasicFlowMatch("node_id02", "port_id02");
    target.addMatch(match01);
    target.addMatch(match02);

    /*
     * test
     */
    target.removeMatch(match01);

    /*
     * check
     */
    assertThat(target.matches.size(), is(1));
    assertThat(target.matches.get(0), is(match02));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#setIdleTimeout(long)}
   * .
   */
  @Test
  public final void testSetIdleTimeout() {
    OFPFlow target = new OFPFlow();

    target.setIdleTimeout(20L);

    assertThat(target.getIdleTimeout(), is(20L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#setHardTimeout(long)}
   * .
   */
  @Test
  public final void testSetHardTimeout() {
    OFPFlow target = new OFPFlow();

    target.setHardTimeout(15L);

    assertThat(target.getHardTimeout(), is(15L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#addPath(java.lang.String)}
   * .
   */
  @Test
  public final void testAddPath() {
    OFPFlow target = new OFPFlow();

    target.addPath("Link");

    assertThat(target.path.get(0), is("Link"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#putPath(java.util.List)}
   * .
   */
  @Test
  public final void testPutPath() {
    OFPFlow target = new OFPFlow();
    List<String> path = new ArrayList<String>(Arrays.asList("Link01",
        "Link02", "Link03"));

    target.putPath(path);

    assertThat(target.path.get(1), is("Link02"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#removePath(java.lang.String)}
   * .
   */
  @Test
  public final void testRemovePath() {
    /*
     * set
     */
    OFPFlow target = new OFPFlow();
    List<String> path = new ArrayList<String>(Arrays.asList("Link01",
        "Link02", "Link03"));
    target.putPath(path);

    /*
     * test
     */
    boolean result = target.removePath("Link02");

    /*
     * check
     */
    assertThat(result, is(true));
    assertThat(target.path.get(1), is("Link03"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#addEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @Test
  public final void testAddEdgeAction() {
    /*
     * set
     */
    OFPFlow target = new OFPFlow();
    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));

    /*
     * test
     */
    boolean result01 = target.addEdgeAction("NodeId01", actions.get(0));
    boolean result02 = target.addEdgeAction("NodeId02", actions.get(1));

    /*
     * check
     */
    assertThat(result01, is(true));
    assertThat(result02, is(true));
    assertThat(target.edgeActions.size(), is(2));
    assertThat(target.edgeActions.get("NodeId01").get(0),
        is(actions.get(0)));
    assertThat(target.edgeActions.get("NodeId02").get(0),
        is(actions.get(1)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#deleteActions(java.lang.String)}
   * .
   */
  @Test
  public final void testDeleteActions() {
    /*
     * set
     */
    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));
    actions.add(new FlowActionOutput("port03"));

    OFPFlow target = new OFPFlow();
    target.addEdgeAction("NodeId01", actions.get(0));
    target.addEdgeAction("NodeId01", actions.get(1));
    target.addEdgeAction("NodeId02", actions.get(2));

    /*
     * test
     */
    boolean result = target.deleteActions("NodeId01");

    /*
     * check
     */
    assertThat(result, is(true));
    assertThat(target.edgeActions.size(), is(1));
    assertThat(target.edgeActions.get("NodeId01"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */

    Value[] actionsMap = new Value[4];
    actionsMap[0] = ValueFactory.createRawValue("type");
    actionsMap[1] = ValueFactory.createRawValue("FlowActionOutput");
    actionsMap[2] = ValueFactory.createRawValue("output");
    actionsMap[3] = ValueFactory.createRawValue("output_value");

    Value[] edgeActionsArray = new Value[1];
    edgeActionsArray[0] = ValueFactory.createMapValue(actionsMap);

    Value[] edgeActionsMap = new Value[2];
    edgeActionsMap[0] = ValueFactory.createRawValue("edge_actions_array");
    edgeActionsMap[1] = ValueFactory.createArrayValue(edgeActionsArray);

    Value[] matchesMap = new Value[4];
    matchesMap[0] = ValueFactory.createRawValue("type");
    matchesMap[1] = ValueFactory.createRawValue("BasicFlowMatch");
    matchesMap[2] = ValueFactory.createRawValue("key01");
    matchesMap[3] = ValueFactory.createRawValue("value01");

    Value[] matchesArray = new Value[1];
    matchesArray[0] = ValueFactory.createMapValue(matchesMap);

    Value[] pathArray = new Value[2];
    pathArray[0] = ValueFactory.createRawValue("path01");
    pathArray[1] = ValueFactory.createRawValue("path02");

    Value[] attributesArray = new Value[2];
    attributesArray[0] = ValueFactory.createRawValue("key01");
    attributesArray[1] = ValueFactory.createRawValue("value01");

    Value[] valueArray = new Value[24];
    valueArray[0] = ValueFactory.createRawValue("idle_timeout");
    valueArray[1] = ValueFactory.createIntegerValue(10L);
    valueArray[2] = ValueFactory.createRawValue("hard_timeout");
    valueArray[3] = ValueFactory.createIntegerValue(15L);
    valueArray[4] = ValueFactory.createRawValue("edge_actions");
    valueArray[5] = ValueFactory.createMapValue(edgeActionsMap);
    valueArray[6] = ValueFactory.createRawValue("matches");
    valueArray[7] = ValueFactory.createArrayValue(matchesArray);
    valueArray[8] = ValueFactory.createRawValue("path");
    valueArray[9] = ValueFactory.createArrayValue(pathArray);
    valueArray[10] = ValueFactory.createRawValue("version");
    valueArray[11] = ValueFactory.createRawValue("123");
    valueArray[12] = ValueFactory.createRawValue("flow_id");
    valueArray[13] = ValueFactory.createRawValue("FlowId");
    valueArray[14] = ValueFactory.createRawValue("owner");
    valueArray[15] = ValueFactory.createRawValue("Owner");
    valueArray[16] = ValueFactory.createRawValue("enabled");
    valueArray[17] = ValueFactory.createBooleanValue(true);
    valueArray[18] = ValueFactory.createRawValue("priority");
    valueArray[19] = ValueFactory.createRawValue("Priority");
    valueArray[20] = ValueFactory.createRawValue("status");
    valueArray[21] = ValueFactory.createRawValue("Status");
    valueArray[22] = ValueFactory.createRawValue("attributes");
    valueArray[23] = ValueFactory.createMapValue(attributesArray);

    Value value = mock(Value.class);
    MapValue map = mock(MapValue.class);
    map = ValueFactory.createMapValue(valueArray);

    doReturn(map).when(value).asMapValue();

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(target.getIdleTimeout(), is(10L));
    assertThat(target.getHardTimeout(), is(15L));
    assertThat(target.matches.size(), is(1));
    assertThat(target.path.size(), is(2));
    assertThat(target.edgeActions.size(), is(1));

    String flowId = WhiteboxImpl.getInternalState(target, "flowId");
    assertThat(flowId, is("FlowId"));

    String owner = WhiteboxImpl.getInternalState(target, "owner");
    assertThat(owner, is("Owner"));

    boolean enabled = WhiteboxImpl.getInternalState(target, "enabled");
    assertThat(enabled, is(true));

    String priority = WhiteboxImpl.getInternalState(target, "priority");
    assertThat(priority, is("Priority"));

    String status = WhiteboxImpl.getInternalState(target, "status");
    assertThat(status, is("Status"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {
    /*
     * set
     */
    OFPFlow target = new OFPFlow();
    Map<String, Value> values = new HashMap<String, Value>();

    target.setIdleTimeout(15L);
    target.setHardTimeout(20L);

    /*
     * test
     */
    boolean result = target.writeValueSub(values);

    /*
     * check
     */
    assertThat(result, is(true));
    assertThat(values.get("idle_timeout").asIntegerValue().getLong(),
        is(15L));
    assertThat(values.get("hard_timeout").asIntegerValue().getLong(),
        is(20L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObject() {
    OFPFlow obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseInstanceof() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseSuper() {
    OFPFlow obj = new OFPFlow();
    doReturn("type").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotEqualsIdleTimeout() {
    OFPFlow obj = new OFPFlow();
    obj.setIdleTimeout(15L);

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotEqualsHardTimeout() {
    OFPFlow obj = new OFPFlow();
    obj.setHardTimeout(20L);

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow#clone()}
   * .
   */
  @Test
  public final void testClone() {
    /*
     * set
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch("node_id", "port_id");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(basicFlowMatch);

    List<String> path = new ArrayList<String>();
    path.add("Path");

    List<FlowAction> actions = new ArrayList<FlowAction>();
    actions.add(new FlowActionOutput("port01"));
    actions.add(new FlowActionOutput("port02"));
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    edgeActions.put("node01", actions);

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("attributes_key", "attributes_value");

    OFPFlow obj = new OFPFlow("123", "FlowId", "Owner", true, "Priority",
        "Status", matches, 10L, 15L, path, edgeActions, attributes);

    /*
     * test
     */
    OFPFlow resurlt = obj.clone();

    /*
     * check
     */
    assertThat(resurlt.getIdleTimeout(), is(10L));
    assertThat(resurlt.getHardTimeout(), is(15L));
    assertThat(resurlt.matches.size(), is(1));
    assertThat(resurlt.path.size(), is(1));
    assertThat(resurlt.edgeActions.size(), is(1));

    String flowId = WhiteboxImpl.getInternalState(resurlt, "flowId");
    assertThat(flowId, is("FlowId"));

    String owner = WhiteboxImpl.getInternalState(resurlt, "owner");
    assertThat(owner, is("Owner"));

    boolean enabled = WhiteboxImpl.getInternalState(resurlt, "enabled");
    assertThat(enabled, is(true));

    String priority = WhiteboxImpl.getInternalState(resurlt, "priority");
    assertThat(priority, is("Priority"));

    String status = WhiteboxImpl.getInternalState(resurlt, "status");
    assertThat(status, is("Status"));
  }

}
