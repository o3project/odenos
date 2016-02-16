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

package org.o3project.odenos.component.linklayerizer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.Component.AttrElements;
import org.o3project.odenos.core.component.Component.NetworkElements;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
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

/**
 * Test class for LinkLayerizerOnFlow.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ LinkLayerizerOnFlow.class, ConversionTable.class,
    NetworkInterface.class })
@PowerMockIgnore({"javax.management.*"})
public class LinkLayerizerOnFlowTest {

  private ConversionTable conversionTable;

  private Map<String, NetworkInterface> networkInterfaces;

  private LinkLayerizerBoundaryTable boundaryTable;

  private LinkLayerizerOnFlow target;

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() throws Exception {

    conversionTable = PowerMockito.spy(new ConversionTable());

    networkInterfaces = new HashMap<>();

    boundaryTable = Mockito.spy(new LinkLayerizerBoundaryTable());

    target = Mockito.spy(new LinkLayerizerOnFlow(conversionTable,
        networkInterfaces, boundaryTable));

  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    conversionTable = null;

    networkInterfaces = null;

    boundaryTable = null;

  }

  private LinkLayerizerOnFlow createPowerSpy() throws Exception {

    target = PowerMockito.spy(new LinkLayerizerOnFlow(conversionTable,
        networkInterfaces, boundaryTable));

    return target;

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#LinkLayerizerOnFlow(org.o3project.odenos.core.component.ConversionTable, java.util.Map, org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable)}.
   */
  @Test
  public void testLinkLayerizerOnFlow() {

    /*
     * setting
     */
    ConversionTable conversionTable = Mockito.mock(ConversionTable.class);
    Map<String, NetworkInterface> networkInterfaces = new HashMap<>();
    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    /*
     * test
     */
    LinkLayerizerOnFlow result = new LinkLayerizerOnFlow(conversionTable,
        networkInterfaces, boundaryTable);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    ConversionTable resultConversionTable = Whitebox.getInternalState(
        result, "conversionTable");
    assertThat(resultConversionTable, is(conversionTable));

    Map<String, NetworkInterface> resultNetworkInterfaces = Whitebox
        .getInternalState(result, "networkInterfaces");
    assertThat(resultNetworkInterfaces, is(networkInterfaces));

    LinkLayerizerBoundaryTable resultBoundaryTable = Whitebox
        .getInternalState(result, "boundaryTable");
    assertThat(resultBoundaryTable, is(boundaryTable));

    Map<String, List<String>> lowerFlows = Whitebox.getInternalState(
        result, "lowerFlows");
    assertThat(lowerFlows.size(), is(0));

    Map<String, String> layerizedLinks = Whitebox.getInternalState(result,
        "layerizedLinks");
    assertThat(layerizedLinks.size(), is(0));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getLayerizedLinks()}.
   */
  @Test
  public void testGetLayerizedLinks() {

    /*
     * test
     */
    Map<String, String> result = target.getLayerizedLinks();

    /*
     * check
     */
    assertThat(result.size(), is(0));

    Map<String, String> settingLinks = Whitebox.getInternalState(target,
        "layerizedLinks");
    assertThat(result, is(settingLinks));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getLowerFlows()}.
   */
  @Test
  public void testGetLowerFlows() {

    /*
     * test
     */
    Map<String, List<String>> result = target.getLowerFlows();

    /*
     * check
     */
    assertThat(result.size(), is(0));

    Map<String, List<String>> settingFlows = Whitebox.getInternalState(
        target, "lowerFlows");
    assertThat(result, is(settingFlows));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#flowAddedLayerizedNwExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testFlowAddedLayerizedNwExistPath() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    /* NetworkInterfaces */

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNetIf);
    networkInterfaces.put("LayerizedNetworkId", layerizedNetIf);

    /* LinkLayerizerOnFlow */

    PowerMockito.doNothing().when(target, "registerUpperFlows", anyObject());

    /* Links */

    Link link1_1 = new Link("LinkId1_1",
        "SrcNode1", "SrcPort1", "DstNode1", "DstPort1");
    Link link2 = new Link("LinkId2",
        "SrcNode2", "SrcPort2", "DstNode2", "DstPort2");
    Link link3_1 = new Link("LinkId3_1",
        "SrcNode3", "SrcPort3", "DstNode3", "DstPort3");
    Map<String, Link> links = new HashMap<>();
    links.put(link1_1.getId(), link1_1);
    links.put(link2.getId(), link2);
    links.put(link3_1.getId(), link3_1);
    PowerMockito.doReturn(links).when(upperNetIf).getLinks();

    Link link1 = new Link("LinkId1", 
        "SrcNode1", "SrcPort1", "DstNode1", "DstPort1");
    Link link3 = new Link("LinkId3", 
        "SrcNode3", "SrcPort3", "DstNode3", "DstPort3");
    PowerMockito.doReturn(link1).when(layerizedNetIf).getLink("LinkId1");
    PowerMockito.doReturn(link2).when(layerizedNetIf).getLink("LinkId2");
    PowerMockito.doReturn(link3).when(layerizedNetIf).getLink("LinkId3");

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match = new BasicFlowMatch("InNode", "InPort");
    matches.add(match);

    List<String> path = new ArrayList<>(Arrays.asList("LinkId1", "LinkId2", "LinkId3"));
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, flowAttributes);

    /*
     * test
     */
    target.flowAddedLayerizedNwExistPath("LayerizedNetworkId", flow);

    /*
     * check
     */

    /* expected flow1 */

    List<BasicFlowMatch> expectedMatches1 = new ArrayList<>();
    BasicFlowMatch expectedMatch1 = new BasicFlowMatch("InNode", "InPort");
    expectedMatches1.add(expectedMatch1);

    List<String> expectedPath1 = new ArrayList<>(Arrays.asList("LinkId1_1", "LinkId2", "LinkId3_1"));

    Map<String, List<FlowAction>> expectedEdgeActions1 = new HashMap<>();

    Map<String, String> expectedAttributes1 = new HashMap<>();

    BasicFlow expectedFlow1 = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none", expectedMatches1, expectedPath1,
        expectedEdgeActions1, expectedAttributes1);

    PowerMockito.verifyPrivate(target).invoke("registerUpperFlows",
        expectedFlow1);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#flowAddedLowerNw(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testFlowAddedLowerNw() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIfLayerized = PowerMockito
        .spy(new NetworkInterface(dispatcher,
            "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", netIfLayerized);

    doReturn(null).when(netIfLayerized).putLink((Link) anyObject());

    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId1", "Type",
        "LowerNetworkId", "LowerNodeId1", "LowerPortId1",
        "UpperNetworkId", "UpperNodeId1", "UpperPortId1");
    boundaryTable.addEntry("BoundaryId1", boundary1);
    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId2", "Type",
        "LowerNetworkId", "LowerNodeId2", "LowerPortId2",
        "UpperNetworkId", "UpperNodeId2", "UpperPortId2");
    boundaryTable.addEntry("BoundaryId2", boundary2);

    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match = new BasicFlowMatch("LowerNodeId1",
        "LowerPortId1");
    matches.add(match);

    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    FlowAction action = new FlowActionOutput("LowerPortId2");
    List<FlowAction> actions = new ArrayList<FlowAction>(
        Arrays.asList(action));
    edgeActions.put("LowerNodeId2", actions);

    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "establishing", matches, path, edgeActions, attributes);

    Link expectedLink = new Link("BoundaryId1_BoundaryId2",
        "UpperNodeId1", "UpperPortId1", "UpperNodeId2", "UpperPortId2");
    Response resp = new Response(Response.OK, expectedLink);
    doReturn(resp).when(netIfLayerized).putLink((Link)anyObject());

    /*
     * test
     */
    target.flowAddedLowerNw("LowerNetworkId", flow);

    /*
     * check
     */
    expectedLink.putAttribute(AttrElements.ESTABLISHMENT_STATUS,
        FlowStatus.ESTABLISHING.toString());
    expectedLink.putAttribute(AttrElements.OPER_STATUS,
        LinkLayerizerOnFlow.STATUS_DOWN);
    expectedLink.putAttribute(AttrElements.COST, "1");

    Map<String, String> resultLayerizedLinks = target.getLayerizedLinks();
    assertThat(resultLayerizedLinks.size(), is(1));
    assertThat(resultLayerizedLinks.get("FlowId"),
        is("BoundaryId1_BoundaryId2"));

    Map<String, List<String>> resultLowerFlows = target.getLowerFlows();
    assertThat(resultLowerFlows.size(), is(1));
    List<String> resultFlows = resultLowerFlows
        .get("BoundaryId1_BoundaryId2");
    assertThat(resultFlows.size(), is(1));
    assertThat(resultFlows.get(0), is("FlowId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#flowUpdateUpperNwExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.util.List)}.
   * @throws Exception throws Exception in targets
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testFlowUpdateUpperNwExistPath() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    conversionTable.addEntryFlow("UpperNetworkId", "FlowId",
        "LayerizedNetworkId", "UpperNetworkId_FlowId");
    conversionTable.addEntryFlow("UpperNetworkId2", "FlowId2",
        "LayerizedNetworkId", "UpperNetworkId_FlowId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, flowAttributes);

    List<String> attributes = new ArrayList<>();

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface upperNwIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "UpperNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNwIf);
    NetworkInterface upperNwIf02 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "UpperNetworkId2"));
    networkInterfaces.put("UpperNetworkId2", upperNwIf02);
    NetworkInterface layerizedNwIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", layerizedNwIf);

    /*
     * test
     */
    target.flowUpdateUpperNwExistPath("UpperNetworkId", flow, attributes);

    /*
     * check
     */
    verify(target, atLeastOnce()).flowUpdateUpperNwExistPath(
        "UpperNetworkId", flow, attributes);
    PowerMockito.verifyPrivate(target).invoke("checkParam",
        "UpperNetworkId", flow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#flowUpdateLowerNw(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.util.List)}.
   */
  @Test
  public void testFlowUpdateLowerNw() {

    /*
     * setting
     */

    /* ConversionTable */

    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    /* NetworkInterfaces */

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIfLayerized = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", netIfLayerized);

    /* LinkLayerizerOnFlow */

    Map<String, String> layerizedLinks = new HashMap<>();
    layerizedLinks.put("FlowId", "LinkId");
    Whitebox.setInternalState(target, "layerizedLinks", layerizedLinks);

    /* Link */

    Link link = new Link("LinkId");
    PowerMockito.doReturn(link).when(netIfLayerized).getLink("LinkId");
    PowerMockito.doReturn(null).when(netIfLayerized)
        .putLink((Link) anyObject());

    /* FlowSet */

    Map<String, List<String>> priorities = new HashMap<>();

    Map<String, Flow> flows = new HashMap<>();

    List<BasicFlowMatch> matches1 = new ArrayList<>();
    List<String> path1 = new ArrayList<>(Arrays.asList("LinkId"));
    Map<String, List<FlowAction>> edgeActions1 = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    BasicFlow flow1 = new BasicFlow("0", "FlowId1", "Owner", true, "0",
        "none", matches1, path1, edgeActions1, attributes1);
    flows.put("FlowId1", flow1);

    List<BasicFlowMatch> matches2 = new ArrayList<>();
    List<String> path2 = new ArrayList<>(Arrays.asList("LinkId"));
    Map<String, List<FlowAction>> edgeActions2 = new HashMap<>();
    Map<String, String> attributes2 = new HashMap<>();
    BasicFlow flow2 = new BasicFlow("0", "FlowId2", "Owner", true, "0",
        "none", matches2, path2, edgeActions2, attributes2);
    flows.put("FlowId2", flow2);

    FlowSet flowSet = new FlowSet("0", priorities, flows);

    PowerMockito.doReturn(flowSet).when(netIfLayerized).getFlowSet();

    PowerMockito.doReturn(null).when(netIfLayerized)
        .putFlow((Flow) anyObject());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "failed", matches, path, edgeActions, flowAttributes);

    List<String> attributes = new ArrayList<>();

    /*
     * test
     */
    target.flowUpdateLowerNw("LowerNetworkId", flow, attributes);

    /*
     * check
     */
    verify(netIfLayerized).putFlow(flow1);
    verify(netIfLayerized).putFlow(flow2);

    assertThat(flow1.getStatus(), is("failed"));
    assertThat(flow2.getStatus(), is("failed"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#flowDeleteLowerNw(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testFlowDeleteLowerNw() {

    /*
     * setting
     */

    /* ConversionTable */

    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    /* NetworkInterfaces */

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIfLayerized = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LowerNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", netIfLayerized);

    PowerMockito.doReturn(null).when(netIfLayerized).delLink((anyString()));

    /* LinkLayerizerOnFlow */

    Map<String, String> layerizedLinks = new HashMap<>();
    layerizedLinks.put("FlowId", "LinkId");
    Whitebox.setInternalState(target, "layerizedLinks", layerizedLinks);

    Map<String, List<String>> lowerFlows = new HashMap<>();
    lowerFlows
        .put("LinkId", new ArrayList<String>(Arrays.asList("FlowId")));
    Whitebox.setInternalState(target, "lowerFlows", lowerFlows);

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "failed", matches, path, edgeActions, flowAttributes);

    /*
     * test
     */
    target.flowDeleteLowerNw("LowerNetworkId", flow);

    /*
     * check
     */
    assertThat(layerizedLinks.containsKey("FlowId"), is(false));
    assertThat(lowerFlows.isEmpty(), is(true));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#updateFlow(java.lang.String, java.lang.String, BasicFlow, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUpdateFlow_Enabled() throws Exception {

    /*
     * setting
     */
    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", netIf);

    doReturn(null).when(netIf).putFlow((Flow) anyObject());

    Map<String, String> dstAttributes = new HashMap<>();
    Flow dstFlow = new Flow("0", "FlowId", "Owner", true, "0", "none",
        dstAttributes);
    dstAttributes.put(NetworkElements.ENABLED, "");
    doReturn(dstFlow).when(netIf).getFlow("FlowId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    attributes.put(NetworkElements.ENABLED, "false");
    BasicFlow srcFlow = new BasicFlow("0", "FlowId", "Owner", false, "0",
        "none", matches, path, edgeActions, attributes);
    List<String> attributesList = new ArrayList<>(
        Arrays.asList(NetworkElements.ENABLED));

    /*
     * test
     */
    target.updateFlow("NetworkId", "FlowId", srcFlow, attributesList);

    /*
     * check
     */
    verify(netIf).putFlow(dstFlow);

    assertThat(dstFlow.getEnabled(), is(false));
    assertThat(dstFlow.getAttribute(NetworkElements.ENABLED), is("false"));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#updateFlow(java.lang.String, java.lang.String, BasicFlow, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUpdateFlow_Priority() throws Exception {

    /*
     * setting
     */
    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", netIf);

    doReturn(null).when(netIf).putFlow((Flow) anyObject());

    Map<String, String> dstAttributes = new HashMap<>();
    Flow dstFlow = new Flow("0", "FlowId", "Owner", true, "0", "none",
        dstAttributes);
    dstAttributes.put(NetworkElements.PRIORITY, "0");
    doReturn(dstFlow).when(netIf).getFlow("FlowId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    attributes.put(NetworkElements.PRIORITY, "1");
    BasicFlow srcFlow = new BasicFlow("0", "FlowId", "Owner", true, "1",
        "none", matches, path, edgeActions, attributes);
    List<String> attributesList = new ArrayList<>(
        Arrays.asList(NetworkElements.PRIORITY));

    /*
     * test
     */
    target.updateFlow("NetworkId", "FlowId", srcFlow, attributesList);

    /*
     * check
     */
    verify(netIf).putFlow(dstFlow);

    assertThat(dstFlow.getPriority(), is("1"));
    assertThat(dstFlow.getAttribute(NetworkElements.PRIORITY), is("1"));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#updateFlow(java.lang.String, java.lang.String, BasicFlow, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUpdateFlow_Status() throws Exception {

    /*
     * setting
     */
    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", netIf);

    doReturn(null).when(netIf).putFlow((Flow) anyObject());

    Map<String, String> dstAttributes = new HashMap<>();
    dstAttributes.put(NetworkElements.STATUS, "none");
    Flow dstFlow = new Flow("0", "FlowId", "Owner", true, "0", "none",
        dstAttributes);
    doReturn(dstFlow).when(netIf).getFlow("FlowId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    attributes.put(NetworkElements.STATUS, "established");
    BasicFlow srcFlow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "established", matches, path, edgeActions, attributes);
    List<String> attributesList = new ArrayList<>(
        Arrays.asList(NetworkElements.STATUS));

    /*
     * test
     */
    target.updateFlow("NetworkId", "FlowId", srcFlow, attributesList);

    /*
     * check
     */
    verify(netIf).putFlow(dstFlow);

    assertThat(dstFlow.getStatus(), is("established"));
    assertThat(dstFlow.getAttribute(NetworkElements.STATUS),
        is("established"));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#updateFlow(java.lang.String, java.lang.String, BasicFlow, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUpdateFlow_Nothing() throws Exception {

    /*
     * setting
     */
    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", netIf);

    doReturn(null).when(netIf).putFlow((Flow) anyObject());

    Map<String, String> dstAttributes = new HashMap<>();
    Flow dstFlow = new Flow("0", "FlowId", "Owner", true, "0", "none",
        dstAttributes);
    doReturn(dstFlow).when(netIf).getFlow("FlowId");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow srcFlow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, attributes);
    List<String> attributesList = new ArrayList<>();

    /*
     * test
     */
    target.updateFlow("NetworkId", "FlowId", srcFlow, attributesList);

    /*
     * check
     */
    verify(netIf, never()).putFlow(dstFlow);

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#registerUpperFlows(List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testRegisterUpperFlows_OneFlow() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIfUpper = PowerMockito.spy(new NetworkInterface(
        dispatcher,
        "UpperNetworkId"));
    NetworkInterface netIfLayerized = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    networkInterfaces.put("UpperNetworkId", netIfUpper);
    networkInterfaces.put("LayerizedNetworkId", netIfLayerized);

    PowerMockito.doReturn(null).when(netIfUpper)
        .putFlow((Flow) anyObject());

    BasicFlow upperFlow = new BasicFlow("UpperFlowId");

    /*
     * test
     */
    target.registerUpperFlows(upperFlow);

    /*
     * check
     */
    verify(netIfUpper).putFlow(upperFlow);
    verify(conversionTable).addEntryFlow("LayerizedNetworkId",
        "UpperFlowId", "UpperNetworkId", "UpperFlowId");

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#registerUpperFlows(List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testRegisterUpperFlows_ManyFlows() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    MessageDispatcher dispatcher = Mockito.mock(MessageDispatcher.class);
    NetworkInterface netIfUpper = PowerMockito.spy(new NetworkInterface(
        dispatcher,
        "UpperNetworkId"));
    NetworkInterface netIfLayerized = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    networkInterfaces.put("UpperNetworkId", netIfUpper);
    networkInterfaces.put("LayerizedNetworkId", netIfLayerized);

    doReturn(null).when(netIfUpper).putFlow((Flow) anyObject());

    List<BasicFlowMatch> matches1 = new ArrayList<>();
    List<String> path1 = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions1 = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    BasicFlow upperFlow1 = new BasicFlow("0", "UpperFlowId", "Owner",
        true, "0", "none", matches1, path1, edgeActions1, attributes1);

    /*
     * test
     */
    target.registerUpperFlows(upperFlow1);

    /*
     * check
     */
    verify(netIfUpper).putFlow(upperFlow1);
    verify(conversionTable).addEntryFlow("LayerizedNetworkId",
        "UpperFlowId", "UpperNetworkId", "UpperFlowId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#setLayerizedLinkStatus(Link, Flow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSetLayerizedLinkStatus_Establishing() throws Exception {

    /*
     * setting
     */
    Link link = new Link("LinkId");
    Map<String, String> attributes = new HashMap<>();
    Flow flow = new Flow("0", "FlowId", "Owner", true, "0",
        FlowStatus.ESTABLISHING.toString(), attributes);

    /*
     * test
     */
    boolean result = target.setLayerizedLinkStatus(link, flow);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(link.getAttribute(AttrElements.ESTABLISHMENT_STATUS),
        is(FlowStatus.ESTABLISHING.toString()));
    assertThat(link.getAttribute(AttrElements.OPER_STATUS),
        is(LinkLayerizerOnFlow.STATUS_DOWN));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#setLayerizedLinkStatus(Link, Flow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSetLayerizedLinkStatus_Failed() throws Exception {

    /*
     * setting
     */
    Link link = new Link("LinkId");
    Map<String, String> attributes = new HashMap<>();
    Flow flow = new Flow("0", "FlowId", "Owner", true, "0",
        FlowStatus.FAILED.toString(), attributes);

    /*
     * test
     */
    boolean result = target.setLayerizedLinkStatus(link, flow);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(link.getAttribute(AttrElements.ESTABLISHMENT_STATUS),
        is(FlowStatus.FAILED.toString()));
    assertThat(link.getAttribute(AttrElements.OPER_STATUS),
        is(LinkLayerizerOnFlow.STATUS_DOWN));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#setLayerizedLinkStatus(Link, Flow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSetLayerizedLinkStatus_Established() throws Exception {

    /*
     * setting
     */
    Link link = new Link("LinkId");
    Map<String, String> attributes = new HashMap<>();
    Flow flow = new Flow("0", "FlowId", "Owner", true, "0",
        FlowStatus.ESTABLISHED.toString(), attributes);

    /*
     * test
     */
    boolean result = target.setLayerizedLinkStatus(link, flow);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(link.getAttribute(AttrElements.ESTABLISHMENT_STATUS),
        is(FlowStatus.ESTABLISHED.toString()));
    assertThat(link.getAttribute(AttrElements.OPER_STATUS),
        is(LinkLayerizerOnFlow.STATUS_UP));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getBoundaryByMatches(String, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetBoundaryByMatches() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId1", "Type1",
        "LowerNetworkId1", "LowerNodeId1", "LowerPortId1",
        "UpperNetworkId1", "UpperNodeId1", "UpperPortId1");
    boundaryTable.addEntry(boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId2", "Type2",
        "LowerNetworkId2", "LowerNodeId2", "LowerPortId2",
        "UpperNetworkId2", "UpperNodeId2", "UpperPortId2");
    boundaryTable.addEntry(boundary2);

    LinkLayerizerBoundary boundary3 = new LinkLayerizerBoundary(
        "BoundaryId3", "Type3",
        "LowerNetworkId3", "LowerNodeId3", "LowerPortId3",
        "UpperNetworkId3", "UpperNodeId3", "UpperPortId3");
    boundaryTable.addEntry(boundary3);

    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match = new BasicFlowMatch("LowerNodeId2",
        "LowerPortId2");
    matches.add(match);

    /*
     * test
     */
    LinkLayerizerBoundary result = target.getBoundaryByMatches(
        "LowerNetworkId2", matches);

    /*
     * check
     */
    assertThat(result, is(boundary2));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getBoundaryByActions(String, Map)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetBoundaryByActions() throws Exception {

    /*
     * setting
     */

    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId1", "Type1", "LowerNetworkId1", "LowerNodeId1",
        "LowerPortId1", "UpperNetwork1", "UpperNodeId1", "UpperPortId1");
    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId2", "Type2", "LowerNetworkId2", "LowerNodeId2",
        "LowerPortId2", "UpperNetwork2", "UpperNodeId2", "UpperPortId2");
    boundaryTable.addEntry(boundary1);
    boundaryTable.addEntry(boundary2);

    Map<String, List<FlowAction>> actionMap = new HashMap<>();

    FlowAction action1 = new FlowActionOutput("LowerPortId1");
    List<FlowAction> actions1 = Arrays.asList(action1);

    FlowAction action2 = new FlowActionOutput("LowerPortId2");
    List<FlowAction> actions2 = Arrays.asList(action2);

    actionMap.put("LowerNodeId1", actions1);
    actionMap.put("LowerNodeId2", actions2);

    /*
     * test
     */
    LinkLayerizerBoundary result = target.getBoundaryByActions(
        "LowerNetworkId2", actionMap);

    /*
     * check
     */
    assertThat(result, is(boundary2));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getNetworkIdByType(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNetworkIdByType() throws Exception {

    /*
     * setting
     */
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");

    /*
     * test
     */
    String resultLower = target.getNetworkIdByType("lower");
    String resultUpper = target.getNetworkIdByType("upper");
    String resultLayerized = target.getNetworkIdByType("layerized");

    /*
     * check
     */
    assertThat(resultLower, is("LowerNetworkId"));
    assertThat(resultUpper, is("UpperNetworkId"));
    assertThat(resultLayerized, is("LayerizedNetworkId"));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#getIgnoreKeys(List, List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetIgnoreKeys() throws Exception {

    /*
     * setting
     */
    List<String> allKeys = Arrays.asList("attribute::aaa",
        "attribute::bbb", "attribute::ccc", "aaa", "bbb:bbb");
    List<String> updateKeys = Arrays.asList("attribute::bbb",
        "attribute::ccc");

    /*
     * test
     */
    List<String> result = target.getIgnoreKeys(allKeys, updateKeys);

    /*
     * check
     */
    List<String> expectList = Arrays.asList("attribute::aaa", "aaa",
        "bbb:bbb");
    assertThat(result, is(expectList));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#checkParam(java.lang.String, BasicFlow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCheckParam() throws Exception {

    /*
     * setting
     */
    BasicFlow flow = new BasicFlow();

    /*
     * test
     */
    boolean result = target.checkParam("NetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerOnFlow#createLinkId(java.lang.String, java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCreateLinkId() throws Exception {

    /*
     * test
     */
    String result = target.createLinkId("SrcBoundaryId", "DstBoundaryId");

    /*
     * check
     */
    assertThat(result, is("SrcBoundaryId_DstBoundaryId"));

  }

}
