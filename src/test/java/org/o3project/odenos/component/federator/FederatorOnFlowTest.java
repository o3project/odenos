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

package org.o3project.odenos.component.federator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for FederatorOnFlow.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FederatorOnFlow.class, ConversionTable.class,
    NetworkInterface.class })
public class FederatorOnFlowTest {

  private MessageDispatcher dispatcher;

  private ConversionTable conversionTable;

  private Map<String, NetworkInterface> networkInterfaces;

  private NetworkInterface networkInterface;

  private FederatorBoundaryTable federatorBoundaryTable;

  private FederatorOnFlow target;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());

    conversionTable = PowerMockito.spy(new ConversionTable());

    networkInterface = new NetworkInterface(dispatcher, "NetworkId");

    networkInterfaces = new HashMap<>();
    networkInterfaces.put("NetworkId", networkInterface);

    federatorBoundaryTable = new FederatorBoundaryTable();

    target = Mockito.spy(new FederatorOnFlow(conversionTable,
        networkInterfaces, federatorBoundaryTable));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    dispatcher = null;
    conversionTable = null;
    networkInterfaces = null;
    networkInterface = null;
    federatorBoundaryTable = null;

  }

  private FederatorOnFlow createPowerTarget() throws Exception {

    target = PowerMockito.spy(new FederatorOnFlow(conversionTable,
        networkInterfaces, federatorBoundaryTable));

    return target;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#FederatorOnFlow(org.o3project.odenos.core.component.ConversionTable, java.util.Map, org.o3project.odenos.component.federator.FederatorBoundaryTable)}.
   */
  @Test
  public void testFederatorOnFlow() {

    /*
     * test
     */
    FederatorOnFlow result = new FederatorOnFlow(conversionTable,
        networkInterfaces, federatorBoundaryTable);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowAddedExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testFlowAddedExistPath() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    doNothing().when(target).doPathSetter(anyString(),
        (BasicFlow) anyObject());
    doNothing().when(target).doFlowAddedSelect((BasicFlow) anyObject());
    doNothing().when(target).doFlowAddedSetFlowRegister();

    BasicFlow flow = Mockito.mock(BasicFlow.class);

    /*
     * test
     */
    target.flowAddedExistPath("NetworkId", flow);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("doPathSetter", "NetworkId",
        flow);
    PowerMockito.verifyPrivate(target).invoke("doFlowAddedSelect", flow);
    PowerMockito.verifyPrivate(target).invoke("doFlowAddedSetFlowRegister");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowAddedNotExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testFlowAddedNotExistPath() {

    /*
     * setting
     */
    conversionTable.addEntryNode("NetworkId", "NodeId",
        "FederatedNetworkId", "NetworkId_NodeId");

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", orgNetIf);

    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match = new BasicFlowMatch("NetworkId_NodeId",
        "NetworkId_NodeId_PortId");
    matches.add(match);

    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    FlowAction action = new FlowActionOutput("output");
    List<FlowAction> actions = new ArrayList<FlowAction>(
        Arrays.asList(action));
    edgeActions.put("NetworkId_NodeId", actions);

    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, attributes);

    /*
     * test
     */
    target.flowAddedNotExistPath("FederatedNetworkId", flow);

    /*
     * check
     */
    verify(conversionTable).addEntryFlow("FederatedNetworkId",
        "FlowId", "NetworkId", "FlowId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowUpdatePreStatusFailed(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testFlowUpdatePreStatusFailed() {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("NetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));
    networkInterfaces.put("NetworkId", orgNetIf);
    networkInterfaces.put("FederatedNetworkId", fedNetIf);

    BasicFlow fedFlow = Mockito.spy(new BasicFlow("FlowId"));
    doReturn(fedFlow).when(fedNetIf).getFlow("FlowId");

    BasicFlow flow = new BasicFlow("FlowId");

    /*
     * test
     */
    target.flowUpdatePreStatusFailed("NetworkId", flow);

    /*
     * check
     */
    verify(fedFlow).setStatus("failed");

    verify(conversionTable).delEntryFlow("NetworkId", "FlowId");

    verify(orgNetIf).delFlow("FlowId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowUpdatePreStatusEstablished(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testFlowUpdatePreStatusEstablished() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("NetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("NetworkId2",
        Federator.ORIGINAL_NETWORK);

    conversionTable.addEntryFlow("NetworkId", "FlowId", "NetworkId2", "FlowId");

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", orgNetIf);

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "established", matches, path, edgeActions, attributes);
    doReturn(settingFlow).when(orgNetIf).getFlow("FlowId");

    /*
     * test
     */
    BasicFlow flow = new BasicFlow("FlowId");
    target.flowUpdatePreStatusEstablished("NetworkId2", flow);

    /*
     * check
     */
    verify(orgNetIf).getFlow("FlowId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#doPathSetter(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testDoPathSetter() {

    /*
     * setting
     */
    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));
    networkInterfaces.put("FederatedNetworkId", fedNetIf);

    conversionTable.addEntryPort("NetworkId", "NodeId", "PortId",
        "FederatedNetworkId", "NetworkId_NodeId",
        "NetworkId_NodeId_PortId");
    conversionTable.addEntryPort("NetworkId", "NetworkId_NodeId",
        "NetworkId_NodeId_PortId", "FederatedNetworkId", "NodeId",
        "PortId");
    conversionTable.addEntryLink("NetworkId", "LinkId",
        "FederatedNetworkId", "NetworkId_LinkId");

    Link link = new Link("LinkId", "NodeId", "PortId", "NetworkId_NodeId",
        "NetworkId_NodeId_PortId");
    PowerMockito.doReturn(link).when(fedNetIf)
        .getLink("NetworkId_LinkId");

    /* key: <networkId>, value: <paths> */
    Map<String, List<String>> nwPaths = Whitebox.getInternalState(target,
        "nwPaths");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = Arrays.asList("NetworkId_LinkId");
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, attributes);

    /*
     * test
     */
    target.doPathSetter("FederatedNetworkId", flow);

    /*
     * check
     */
    assertThat(nwPaths.size(), is(1));

    List<String> resultLinks = nwPaths.get("NetworkId");
    assertThat(resultLinks.size(), is(1));
    assertThat(resultLinks.get(0), is("LinkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#doFlowAddedSelect(org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testDoFlowAddedSelect() {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);

    Map<String, List<String>> nwPaths = Whitebox.getInternalState(target,
        "nwPaths");
    nwPaths.put("NetworkId", Arrays.asList("/Path"));

    Map<String, BoundaryPort> nwSrcBoundaryPorts = Whitebox
        .getInternalState(target, "nwSrcBoundaryPorts");
    BoundaryPort boundaryPort = new BoundaryPort("NetworkId", "NodeId",
        "PortId");
    nwSrcBoundaryPorts.put("NetworkId", boundaryPort);

    BasicFlow flow = new BasicFlow("FlowId");

    /*
     * test
     */
    target.doFlowAddedSelect(flow);

    /*
     * check
     */
    Map<String, BasicFlow> orgFlowList = Whitebox.getInternalState(target,
        "orgFlowList");

    assertThat(orgFlowList.size(), is(1));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#doFlowAddedSetFlowRegister()}.
   */
  @Test
  public void testDoFlowAddedSetFlowRegister() {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", orgNetIf);

    Map<String, BasicFlow> orgFlowList = new HashMap<>();
    BasicFlow flow1 = new BasicFlow("FlowId");
    orgFlowList.put("NetworkId", flow1);
    Whitebox.setInternalState(target, "orgFlowList", orgFlowList);

    /*
     * test
     */
    target.doFlowAddedSetFlowRegister();

    /*
     * check
     */
    verify(conversionTable).addEntryFlow("NetworkId", "FlowId",
        "FederatedNetworkId", "FlowId"); // non convert fed_flow_id;

    verify(orgNetIf).putFlow(flow1);

  }

  /*
   * ======================================================================
   * Private Methods
   * ======================================================================
   */

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#initialize()}.
   * @throws Exception
   */
  @Test
  public void testInitialize() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> nwPaths = Whitebox.getInternalState(target,
        "nwPaths");
    nwPaths = Mockito.spy(nwPaths);
    Whitebox.setInternalState(target, "nwPaths", nwPaths);

    Map<String, BoundaryPort> nwSrcBoundaryPorts = Whitebox
        .getInternalState(target, "nwSrcBoundaryPorts");
    nwSrcBoundaryPorts = Mockito.spy(nwSrcBoundaryPorts);
    Whitebox.setInternalState(target, "nwSrcBoundaryPorts",
        nwSrcBoundaryPorts);

    Map<String, BoundaryPort> nwDstBoundaryPorts = Whitebox
        .getInternalState(target, "nwDstBoundaryPorts");
    nwDstBoundaryPorts = Mockito.spy(nwDstBoundaryPorts);
    Whitebox.setInternalState(target, "nwDstBoundaryPorts",
        nwDstBoundaryPorts);

    Map<String, BasicFlow> orgFlowList = Whitebox.getInternalState(target,
        "orgFlowList");
    orgFlowList = Mockito.spy(orgFlowList);
    Whitebox.setInternalState(target, "orgFlowList", orgFlowList);

    /*
     * test
     */
    target.initialize();

    /*
     * check
     */
    verify(nwPaths).clear();
    verify(nwSrcBoundaryPorts).clear();
    verify(nwDstBoundaryPorts).clear();
    verify(orgFlowList).clear();

    assertThat(nwPaths.size(), is(0));
    assertThat(nwSrcBoundaryPorts.size(), is(0));
    assertThat(nwDstBoundaryPorts.size(), is(0));
    assertThat(orgFlowList.size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#setFlowMatch(org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSetFlowMatch() throws Exception {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match1 = Mockito.spy(new BasicFlowMatch("", ""));
    BasicFlowMatch match2 = Mockito.spy(new BasicFlowMatch("", ""));
    matches.add(match1);
    matches.add(match2);

    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "Status", matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.setFlowMatch(flow, "NodeId", "PortId");

    /*
     * check
     */
    assertThat(result, is(true));

    List<BasicFlowMatch> resultMatches = flow.getMatches();
    assertThat(resultMatches.size(), is(2));

    BasicFlowMatch resultMatch1 = resultMatches.get(0);
    verify(resultMatch1).setInNode("NodeId");
    assertThat(resultMatch1.getInNode(), is("NodeId"));
    verify(resultMatch1).setInPort("PortId");
    assertThat(resultMatch1.getInPort(), is("PortId"));

    BasicFlowMatch resultMatch2 = resultMatches.get(1);
    verify(resultMatch2).setInNode("NodeId");
    assertThat(resultMatch2.getInNode(), is("NodeId"));
    verify(resultMatch2).setInPort("PortId");
    assertThat(resultMatch2.getInPort(), is("PortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#convertMatch(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testConvertMatch() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryPort("NetworkId", "NodeId1", "PortId1",
        "FederatedNetworkId", "NetworkId_NodeId1",
        "NetworkId_NodeId1_PortId1");
    conversionTable.addEntryPort("NetworkId", "NodeId2", "PortId2",
        "FederatedNetworkId", "NetworkId_NodeId2",
        "NetworkId_NodeId2_PortId2");

    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match1 = Mockito.spy(new BasicFlowMatch("NodeId1",
        "PortId1"));
    BasicFlowMatch match2 = Mockito.spy(new BasicFlowMatch("NodeId2",
        "PortId2"));
    matches.add(match1);
    matches.add(match2);

    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "Status", matches, path, edgeActions, attributes);

    /*
     * test
     */
    target.convertMatch("NetworkId", flow);

    /*
     * check
     */
    List<BasicFlowMatch> resultMatches = flow.getMatches();
    assertThat(resultMatches.size(), is(2));

    BasicFlowMatch result1 = resultMatches.get(0);

    verify(match1).setInNode("NetworkId_NodeId1");
    assertThat(result1.getInNode(), is("NetworkId_NodeId1"));
    verify(match1).setInPort("NetworkId_NodeId1_PortId1");
    assertThat(result1.getInPort(), is("NetworkId_NodeId1_PortId1"));

    BasicFlowMatch result2 = resultMatches.get(1);

    verify(match2).setInNode("NetworkId_NodeId2");
    assertThat(result2.getInNode(), is("NetworkId_NodeId2"));
    verify(match2).setInPort("NetworkId_NodeId2_PortId2");
    assertThat(result2.getInPort(), is("NetworkId_NodeId2_PortId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#setFlowAction(org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSetFlowAction() throws Exception {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();

    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    FlowAction action1 = new FlowActionOutput();
    FlowAction action2 = new FlowActionOutput();
    edgeActions.put("action01",
        new ArrayList<FlowAction>(Arrays.asList(action1, action2)));
    edgeActions.put("action02",
        new ArrayList<FlowAction>(Arrays.asList(action1, action2)));

    Map<String, String> attributes = new HashMap<>();

    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.setFlowAction(flow, "NodeId", "PortId");

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, List<FlowAction>> resultActionMap = flow.getEdgeActions();
    assertThat(resultActionMap.size(), is(1));

    List<FlowAction> resultActions = resultActionMap.get("NodeId");
    assertThat(resultActions.size(), is(1));

    FlowAction resultAction = resultActions.get(0);
    assertThat((resultAction instanceof FlowActionOutput), is(true));

    FlowActionOutput resultActionOutput = (FlowActionOutput) resultAction;
    assertThat(resultActionOutput.getOutput(), is("PortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#convertAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testConvertAction() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryPort("NetworkId", "NodeId", "PortId",
        "FederatedNetworkId", "NetworkId_NodeId",
        "NetworkId_NodeId_PortId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();

    List<FlowAction> actions = new ArrayList<>();
    FlowAction action = new FlowActionOutput("NetworkId_NodeId_PortId");
    actions.add(action);
    edgeActions.put("NetworkId_NodeId", actions);

    Map<String, String> attributes = new HashMap<>();

    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, attributes);

    /*
     * test
     */
    target.convertAction("FederatedNetworkId", flow);

    /*
     * check
     */
    Map<String, List<FlowAction>> resultActionMap = flow.getEdgeActions();

    assertThat(resultActionMap.size(), is(1));

    List<FlowAction> resultActions = resultActionMap.get("NodeId");
    assertThat(CollectionUtils.isNotEmpty(resultActions), is(true));

    FlowAction resultAction = resultActions.get(0);
    assertThat((resultAction instanceof FlowActionOutput), is(true));
    FlowActionOutput resultOutput = (FlowActionOutput) resultAction;
    assertThat(resultOutput.getOutput(), is("PortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getNetworkIdByType(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIdByType() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("NetworkId", "original");

    /*
     * test
     */
    String result = target.getNetworkIdByType("original");

    /*
     * check
     */
    assertThat(result, is("NetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvNodeId(java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvNodeId() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryNode("NetworkId", "NodeId",
        "FederatedNetworkId", "NetworkId_NodeId");

    /*
     * test
     */
    String result = target.getConvNodeId("NetworkId", "NodeId");

    /*
     * check
     */
    assertThat(result, is("FederatedNetworkId::NetworkId_NodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvPortId() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryPort("NetworkId", "NodeId", "PortId",
        "FederatedNetworkId", "NetworkId_NodeId",
        "NetworkId_NodeId_PortId");

    /*
     * test
     */
    String result = target.getConvPortId("NetworkId", "NodeId", "PortId");

    /*
     * check
     */
    assertThat(
        result,
        is("FederatedNetworkId::NetworkId_NodeId::NetworkId_NodeId_PortId"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvLinkId(java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvLinkId() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryLink("NetworkId", "LinkId",
        "FederatedNetworkId", "NetworkId_LinkId");

    /*
     * test
     */
    String result = target.getConvLinkId("NetworkId", "LinkId");

    /*
     * check
     */
    assertThat(result, is("FederatedNetworkId::NetworkId_LinkId"));

  }

}
