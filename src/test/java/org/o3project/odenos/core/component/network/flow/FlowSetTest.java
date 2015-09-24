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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowType;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.query.FlowQuery;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test class for FlowSet.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FlowSet.class, UUID.class })
@PowerMockIgnore({"javax.management.*"})
public class FlowSetTest {

  private FlowSet target;

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

    Map<String, List<String>> priority = new HashMap<String, List<String>>();
    Map<String, Flow> flows = new HashMap<String, Flow>();

    target = Mockito.spy(new FlowSet("1", priority, flows));

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
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue() {

    /*
     * setting
     */
    Map<String, List<String>> priority = Mockito
        .spy(new HashMap<String, List<String>>());
    Map<String, Flow> flows = Mockito.spy(new HashMap<String, Flow>());
    target = Mockito.spy(new FlowSet("1", priority, flows));

    /* version setting */
    Value versionKey = ValueFactory.createRawValue("version");
    Value versionValue = ValueFactory.createRawValue("1");

    /* priority setting */
    Value priorityKey = ValueFactory.createRawValue("priority");

    Value prioritySubKey1 = ValueFactory.createRawValue("FlowId1");
    Value prioritySubValue1 = ValueFactory.createArrayValue(new Value[] {
        ValueFactory.createRawValue("FlowId1"),
        ValueFactory.createRawValue("FlowId2")
    });
    Value prioritySubKey2 = ValueFactory.createRawValue("FlowId2");
    Value prioritySubValue2 = ValueFactory.createArrayValue(new Value[] {
        ValueFactory.createRawValue("FlowId1"),
        ValueFactory.createRawValue("FlowId2")
    });

    Value priorityValue = ValueFactory.createMapValue(new Value[] {
        prioritySubKey1, prioritySubValue1,
        prioritySubKey2, prioritySubValue2,
    });

    /* flows setting */
    Value flowsKey = ValueFactory.createRawValue("flows");

    Value flowsSubKey1 = ValueFactory.createRawValue("FlowId1");
    Value flowsSubValue1 = new BasicFlow().writeValue();
    Value flowsSubKey2 = ValueFactory.createRawValue("FlowId2");
    Value flowsSubValue2 = new OFPFlow().writeValue();

    Value flowsValue = ValueFactory.createMapValue(new Value[] {
        flowsSubKey1, flowsSubValue1,
        flowsSubKey2, flowsSubValue2,
    });

    Value values = ValueFactory.createMapValue(new Value[] {
        versionKey, versionValue,
        priorityKey, priorityValue,
        flowsKey, flowsValue
    });

    /*
     * test
     */
    boolean result = target.readValue(values);

    /*
     * check
     */
    verify(priority, times(1)).clear();
    verify(flows, times(1)).clear();

    assertThat(result, is(true));

    assertThat(priority.size(), is(2));
    assertThat(priority.containsKey("FlowId1"), is(true));
    assertThat(priority.containsKey("FlowId2"), is(true));

    assertThat(flows.size(), is(2));
    assertThat(flows.containsKey("FlowId1"), is(true));
    assertThat(flows.containsKey("FlowId2"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSub() {

    /*
     * setting
     */
    Map<String, Value> values = new HashMap<String, Value>();

    /*
     * test
     */
    boolean result = target.writeValueSub(values);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(values.size(), is(4));
    assertThat(values.get("type").toString(), is("\"FlowSet\""));
    assertThat(values.get("version").toString(), is("\"1\""));
    assertThat(values.get("priority").isMapValue(), is(true));
    assertThat(values.get("flows").isMapValue(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#FlowSet()}.
   */
  @Test
  public void testFlowSet() {

    /*
     * test
     */
    target = new FlowSet();

    /*
     * check
     */
    assertThat(target.getVersion(), is("0"));

    assertThat(target.priority, is(notNullValue()));
    assertThat(target.priority.isEmpty(), is(true));
    assertThat(target.flows, is(notNullValue()));
    assertThat(target.flows.isEmpty(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#FlowSet(java.lang.String, java.util.Map, java.util.Map)}
   * .
   */
  @Test
  public void testFlowSetStringMapOfStringListOfStringMapOfStringFlow() {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<String, List<String>>();
    priority.put(
        "Priority1",
        new ArrayList<String>(Arrays.asList(new String[] { "FlowId1",
            "FlowId2" })));
    priority.put(
        "Priority2",
        new ArrayList<String>(Arrays.asList(new String[] { "FlowId3",
            "FlowId4" })));
    Map<String, Flow> flows = new HashMap<String, Flow>();
    flows.put("flows1", new Flow("FlowId1"));
    flows.put("flows2", new Flow("FlowId2"));
    flows.put("flows3", new Flow("FlowId3"));
    flows.put("flows4", new Flow("FlowId4"));

    /*
     * test
     */
    target = new FlowSet("123", priority, flows);

    /*
     * check
     */
    assertThat(target.getVersion(), is("123"));

    assertThat(target.priority, is(notNullValue()));
    assertThat(target.priority.size(), is(2));
    assertThat(target.flows, is(notNullValue()));
    assertThat(target.flows.size(), is(4));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#FlowSet()}.
   */
  @Test
  public void testFlowSet_Null() {

    /*
     * test
     */
    target = new FlowSet(null, null, null);

    /*
     * check
     */
    assertThat(target.getVersion(), is("0"));

    assertThat(target.priority, is(notNullValue()));
    assertThat(target.priority.isEmpty(), is(true));
    assertThat(target.flows, is(notNullValue()));
    assertThat(target.flows.isEmpty(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeString_BasicFlow() {

    /*
     * setting
     */
    FlowType basicType = FlowType.BASIC_FLOW;

    /*
     * test
     */
    Flow resultFlow = target.createFlow(basicType, "Priority");

    /*
     * check
     */
    assertThat(resultFlow.getType(), is("BasicFlow"));
    assertThat(resultFlow.getFlowId(), is(notNullValue()));
    assertThat(resultFlow.getVersion(), is("1"));

    String priorityFlowId = target.priority.get("Priority").get(0);
    assertThat(priorityFlowId, is(resultFlow.getFlowId()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeString_OfpFlow() {

    /*
     * setting
     */
    FlowType ofpType = FlowType.OFP_FLOW;

    /*
     * test
     */
    Flow resultFlow = target.createFlow(ofpType, "Priority");

    /*
     * check
     */
    assertThat(resultFlow.getType(), is("OFPFlow"));
    assertThat(resultFlow.getFlowId(), is(notNullValue()));
    assertThat(resultFlow.getVersion(), is("1"));

    String priorityFlowId = target.priority.get("Priority").get(0);
    assertThat(priorityFlowId, is(resultFlow.getFlowId()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeString_Null() {

    /*
     * test
     */
    Flow resultFlow = target.createFlow(null, null);

    /*
     * check
     */
    assertNull(resultFlow);
    assertThat(target.flows.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeStringString_BasicFlow() {

    /*
     * setting
     */
    FlowType basicType = FlowType.BASIC_FLOW;

    /*
     * test
     */
    Flow resultFlow = target.createFlow(basicType, "Priority", "FlowId");

    /*
     * check
     */
    assertThat(resultFlow.getType(), is("BasicFlow"));
    assertThat(resultFlow.getFlowId(), is("FlowId"));
    assertThat(resultFlow.getVersion(), is("1"));

    String priorityFlowId = target.priority.get("Priority").get(0);
    assertThat(priorityFlowId, is(resultFlow.getFlowId()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeStringString_OfpFlow() {

    /*
     * setting
     */
    FlowType ofpType = FlowType.OFP_FLOW;

    /*
     * test
     */
    Flow resultFlow = target.createFlow(ofpType, "Priority", "FlowId");

    /*
     * check
     */
    assertThat(resultFlow.getType(), is("OFPFlow"));
    assertThat(resultFlow.getFlowId(), is("FlowId"));
    assertThat(resultFlow.getVersion(), is("1"));

    String priorityFlowId = target.priority.get("Priority").get(0);
    assertThat(priorityFlowId, is(resultFlow.getFlowId()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowType, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowFlowTypeStringString_Null() {

    /*
     * test
     */
    Flow resultFlow = target.createFlow(null, (String) null, (String) null);

    /*
     * check
     */
    assertNull(resultFlow);
    assertThat(target.flows.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testCreateFlowFlow_BasicFlow() {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    Map<String, String> attributes = new HashMap<String, String>();
    BasicFlow flow =
        new BasicFlow("1", "FlowId", "Owner", true, "Priority", "none",
            matches, path,
            edgeActions,
            attributes);
    PowerMockito.mockStatic(UUID.class);
    PowerMockito.when(UUID.randomUUID()).thenReturn(new UUID(1, 1));

    /*
     * test
     */
    Flow resultFlow = target.createFlow(flow);

    /*
     * check
     */
    assertThat(resultFlow.getType(), is(flow.getType()));
    assertThat(resultFlow.getFlowId(),
        is("00000000-0000-0001-0000-000000000001"));
    assertThat(resultFlow.getVersion(), is("1"));
    assertThat(resultFlow.getOwner(), is(flow.getOwner()));
    assertThat(resultFlow.getEnabled(), is(flow.getEnabled()));
    assertThat(resultFlow.getPriority(), is(flow.getPriority()));
    assertThat(resultFlow.getStatus(), is(flow.getStatus()));

    assertThat(resultFlow, is(BasicFlow.class));
    BasicFlow resultBasicFlow = (BasicFlow) resultFlow;
    assertThat(resultBasicFlow.getMatches(), is(flow.getMatches()));
    assertThat(resultBasicFlow.getPath(), is(flow.getPath()));
    assertThat(resultBasicFlow.getEdgeActions(), is(flow.getEdgeActions()));
    assertThat(resultBasicFlow.getAttributes(), is(flow.getAttributes()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testCreateFlowFlow_OfpFlow() {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    Map<String, String> attributes = new HashMap<String, String>();
    OFPFlow flow =
        new OFPFlow("1", "FlowId", "Owner", true, "Priority", "none",
            matches, 0L, 0L,
            path,
            edgeActions, attributes);
    PowerMockito.mockStatic(UUID.class);
    PowerMockito.when(UUID.randomUUID()).thenReturn(new UUID(1, 1));

    /*
     * test
     */
    Flow resultFlow = target.createFlow(flow);

    /*
     * check
     */
    assertThat(resultFlow.getType(), is(flow.getType()));
    assertThat(resultFlow.getFlowId(),
        is("00000000-0000-0001-0000-000000000001"));
    assertThat(resultFlow.getVersion(), is("1"));
    assertThat(resultFlow.getOwner(), is(flow.getOwner()));
    assertThat(resultFlow.getEnabled(), is(flow.getEnabled()));
    assertThat(resultFlow.getPriority(), is(flow.getPriority()));
    assertThat(resultFlow.getStatus(), is(flow.getStatus()));

    assertThat(resultFlow, is(OFPFlow.class));
    OFPFlow resultOfpFlow = (OFPFlow) resultFlow;
    assertThat(resultOfpFlow.getMatches(), is(flow.getMatches()));
    assertThat(resultOfpFlow.getIdleTimeout(), is(flow.getIdleTimeout()));
    assertThat(resultOfpFlow.getHardTimeout(), is(flow.getHardTimeout()));
    assertThat(resultOfpFlow.getPath(), is(flow.getPath()));
    assertThat(resultOfpFlow.getEdgeActions(), is(flow.getEdgeActions()));
    assertThat(resultOfpFlow.getAttributes(), is(flow.getAttributes()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testCreateFlowFlow_Flow() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    Flow flow = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    /*
     * test
     */
    Flow resultFlow = target.createFlow(flow);

    /*
     * check
     */
    assertNull(resultFlow);
    assertThat(target.flows.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testCreateFlowFlow_null() {

    /*
     * test
     */
    Flow resultFlow = target.createFlow(null);

    /*
     * check
     */
    assertThat(resultFlow, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowStringFlowString_BasicFlow() {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    Map<String, String> attributes = new HashMap<String, String>();
    BasicFlow msg =
        new BasicFlow("1", "FlowId", "Owner", true, "Priority", "none",
            matches, path,
            edgeActions,
            attributes);

    /*
     * test
     */
    Flow resultFlow = target.createFlow("FlowId", msg, "123");

    /*
     * check
     */
    assertThat(resultFlow.getFlowId(), is("FlowId"));
    assertThat(resultFlow.getVersion(), is("124"));

    assertThat(resultFlow.getType(), is(msg.getType()));
    assertThat(resultFlow.getOwner(), is(msg.getOwner()));
    assertThat(resultFlow.getEnabled(), is(msg.getEnabled()));
    assertThat(resultFlow.getPriority(), is(msg.getPriority()));
    assertThat(resultFlow.getStatus(), is(msg.getStatus()));

    assertThat(resultFlow, is(BasicFlow.class));
    BasicFlow resultBasicFlow = (BasicFlow) resultFlow;
    assertThat(resultBasicFlow.getMatches(), is(msg.getMatches()));
    assertThat(resultBasicFlow.getPath(), is(msg.getPath()));
    assertThat(resultBasicFlow.getEdgeActions(), is(msg.getEdgeActions()));
    assertThat(resultBasicFlow.getAttributes(), is(msg.getAttributes()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowStringFlowString_OfpFlow() {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    Map<String, String> attributes = new HashMap<String, String>();
    OFPFlow msg =
        new OFPFlow("1", "FlowId", "Owner", true, "Priority", "none",
            matches, 0L, 0L,
            path,
            edgeActions, attributes);

    /*
     * test
     */
    Flow resultFlow = target.createFlow("FlowId", msg, "123");

    /*
     * check
     */
    assertThat(resultFlow.getFlowId(), is("FlowId"));
    assertThat(resultFlow.getVersion(), is("124"));

    assertThat(resultFlow.getType(), is(msg.getType()));
    assertThat(resultFlow.getOwner(), is(msg.getOwner()));
    assertThat(resultFlow.getEnabled(), is(msg.getEnabled()));
    assertThat(resultFlow.getPriority(), is(msg.getPriority()));
    assertThat(resultFlow.getStatus(), is(msg.getStatus()));

    assertThat(resultFlow, is(OFPFlow.class));
    OFPFlow resultOfpFlow = (OFPFlow) resultFlow;
    assertThat(resultOfpFlow.getMatches(), is(msg.getMatches()));
    assertThat(resultOfpFlow.getIdleTimeout(), is(msg.getIdleTimeout()));
    assertThat(resultOfpFlow.getHardTimeout(), is(msg.getHardTimeout()));
    assertThat(resultOfpFlow.getPath(), is(msg.getPath()));
    assertThat(resultOfpFlow.getEdgeActions(), is(msg.getEdgeActions()));
    assertThat(resultOfpFlow.getAttributes(), is(msg.getAttributes()));

    assertThat(target.flows.size(), is(1));
    Flow flowInMap = target.flows.get(resultFlow.getFlowId());
    assertThat(flowInMap, is(resultFlow));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowStringFlowString_Flow() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    Flow msg = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    /*
     * test
     */
    Flow resultFlow = target.createFlow("FlowId", msg, "123");

    /*
     * check
     */
    assertNull(resultFlow);
    assertThat(target.flows.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#createFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, java.lang.String)}
   * .
   */
  @Test
  public void testCreateFlowStringFlowString_Null() {

    /*
     * test
     */
    Flow resultFlow = target.createFlow(null, (Flow) null, null);

    /*
     * check
     */
    assertThat(resultFlow, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#getFlow(java.lang.String)}
   * .
   */
  @Test
  public void testGetFlow() {

    Map<String, Flow> flows = new HashMap<String, Flow>();
    Flow settingFlow = new Flow("FlowId");
    flows.put("FlowId", settingFlow);

    target.flows = flows;

    /*
     * test
     */
    Flow resultFlow = target.getFlow("FlowId");

    /*
     * check
     */
    assertThat(resultFlow, is(settingFlow));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#getFlows()}.
   */
  @Test
  public void testGetFlows_Empty() {

    /*
     * test
     */
    Map<String, Flow> resultMap = target.getFlows();

    /*
     * check
     */
    assertThat(resultMap, is(notNullValue()));
    assertThat(resultMap.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#deleteFlow(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteFlowString() {

    /*
     * setting
     */

    /*
     * test
     */
    Flow result = target.deleteFlow((String) null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#deleteFlow(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteFlowString_Null() {

    /*
     * test
     */
    Flow result = target.deleteFlow((String) null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#deleteFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testDeleteFlowFlow() {

    /*
     * setting
     */

    /*
     * test
     */
    Flow result = target.deleteFlow((Flow) null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#deleteFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testDeleteFlowFlow_Null() {

    /*
     * test
     */
    Flow result = target.deleteFlow((Flow) null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#getUniqueFlowId()}
   * .
   */
  @Test
  public void testGetUniqueFlowId() {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<String, List<String>>();
    Map<String, Flow> flows = new HashMap<String, Flow>();
    Flow settingFlow = new Flow();
    flows.put("00000000-0000-0001-0000-000000000001", settingFlow);

    target = PowerMockito.spy(new FlowSet("1", priority, flows));

    PowerMockito.mockStatic(UUID.class);

    PowerMockito.when(UUID.randomUUID())
        .thenReturn(new UUID(1, 1)) /*
                                     * return
                                     * 00000000-0000-0001-0000-000000000001
                                     */
        .thenReturn(new UUID(1, 1)) /*
                                     * return
                                     * 00000000-0000-0001-0000-000000000001
                                     */
        .thenCallRealMethod();

    /*
     * test
     */
    String result = target.getUniqueFlowId();

    /*
     * chcek
     */
    PowerMockito.verifyStatic(times(3));
    UUID.randomUUID(); /* verify target for verifyStatic(times(3)) */

    assertThat(result, is(notNullValue()));
    assertThat(result, is(not("00000000-0000-0001-0000-000000000001")));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#getFlowMessages(org.o3project.odenos.component.network.flow.FlowQuery)}
   * .
   */
  @Test
  public void testGetFlowMessages() {

    /*
     * setting
     */
    Flow basicFlow = new BasicFlow("BasicFlowId");
    OFPFlow ofpFlow = new OFPFlow("OFPFlowId");
    ofpFlow.setIdleTimeout(0L);
    ofpFlow.setHardTimeout(0L);

    target.createFlow(basicFlow);
    target.createFlow(ofpFlow);

    FlowQuery query = FlowQueryFactory.create("type=BasicFlow");

    /*
     * test
     */
    FlowSet result = target.getFlowMessages(query);

    /*
     * check
     */
    Map<String, Flow> resultFlowMap = result.getFlows();

    assertThat(resultFlowMap.size(), is(1));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowSet#getFlowMessages(org.o3project.odenos.component.network.flow.FlowQuery)}
   * .
   */
  @Test
  public void testGetFlowMessages_Null() {

    /*
     * test
     */
    FlowSet result = target.getFlowMessages(null);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.flow.FlowSet#getPriority(String)}.
   * @throws Exception
   *
   */
  @Test
  public void testGetPriority() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<>();
    priority.put("Flow1", new ArrayList<>(Arrays.asList("FlowId1")));
    priority.put("Flow2", new ArrayList<>(Arrays.asList("FlowId2")));
    priority.put("Flow3", new ArrayList<>(Arrays.asList("FlowId3")));

    Whitebox.setInternalState(target, "priority", priority);

    /*
     * test
     */
    String result = Whitebox.invokeMethod(target, "getPriority", "FlowId2");

    /*
     * check
     */
    assertThat(result, is("Flow2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.flow.FlowSet#toString()}.
   * @throws Exception
   *
   */
  @Test
  public void testToString() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<String, List<String>>();
    Map<String, Flow> flows = new HashMap<String, Flow>();

    target = new FlowSet("1", priority, flows);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("version=1,priority={},flows={}]"), is(true));
  }

}
