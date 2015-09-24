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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
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
 * Test class for Federator.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Federator.class, ConversionTable.class,
    NetworkInterface.class, SystemManagerInterface.class })
@PowerMockIgnore({"javax.management.*"})
public class FederatorTest {

  private MessageDispatcher dispatcher;

  private Federator target;

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

    target = Mockito.spy(new Federator("ObjectId", dispatcher));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    dispatcher = null;

  }

  private Federator createPowerTarget() throws Exception {
    target = PowerMockito.spy(new Federator("ObjectId", dispatcher));

    return target;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#Federator(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}.
   * @throws Exception
   */
  @Test
  public void testFederator() throws Exception {

    /*
     * test
     */
    Federator result = new Federator("ObjectId", dispatcher);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    assertThat(result.getObjectId(), is("ObjectId"));
    assertThat(result.getMessageDispatcher(), is(dispatcher));

    assertThat(Whitebox.getInternalState(target, "federatorBoundaryTable"),
        is(notNullValue()));
    assertThat(Whitebox.getInternalState(target, "federatorOnFlow"),
        is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   */
  @Test
  public void testOnRequest() {

    /*
     * setting
     */
    Request request = new Request("ObjectId", Method.GET,
        "settings/boundaries",
        new Object());

    /*
     * test
     */
    Response result = target.onRequest(request);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getSuperType()}.
   */
  @Test
  public void testGetSuperType() {

    /*
     * test
     */
    String result = target.getSuperType();

    /*
     * check
     */
    assertThat(result, is("Federator"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getDescription()}.
   */
  @Test
  public void testGetDescription() {

    /*
     * test
     */
    String result = target.getDescription();

    /*
     * check
     */
    assertThat(result, is("Federator Component"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getConnectionTypes()}.
   */
  @Test
  public void testGetConnectionTypes() {

    /*
     * test
     */
    String result = target.getConnectionTypes();

    /*
     * check
     */
    assertThat(result, is("federated:1,original:*"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedAddedPre_Original() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAddedPre_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("NetworkId",
        Federator.ORIGINAL_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "federated", "none", "ObjectId", "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdatePre() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedUpdatePre(msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedDeletePre() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedDeletePre(msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemMngInterface = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemMngInterface).when(target,
        "systemMngInterface");
    PowerMockito.doReturn(null).when(systemMngInterface)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedAdded(msg);

    /*
     * check
     */
    verify(target, atLeastOnce()).subscribeOriginal("NetworkId");

    verify(systemMngInterface).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemMngInterface = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemMngInterface).when(target,
        "systemMngInterface");
    PowerMockito.doReturn(null).when(systemMngInterface)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "federated", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedAdded(msg);

    /*
     * check
     */
    verify(target, atLeastOnce()).subscribeFederated("NetworkId");

    verify(systemMngInterface).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedUpdate(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdate() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedUpdate(msg);

    /*
     * check
     */
    verify(target).onConnectionChangedUpdate(msg);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SystemManagerInterface systemMngInterface = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemMngInterface).when(target,
        "systemMngInterface");
    PowerMockito.doReturn(null).when(systemMngInterface)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "none", "ObjectId", "NetworkId");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "NetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("NetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedDelete(msg);

    /*
     * check
     */
    verify(target, atLeastOnce()).unsubscribeOriginal("NetworkId");
    verify(systemMngInterface, times(2)).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SystemManagerInterface systemMngInterface = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemMngInterface).when(target,
        "systemMngInterface");
    PowerMockito.doReturn(null).when(systemMngInterface)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "federated", "none", "ObjectId", "NetworkId");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "NetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("NetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedDelete(msg);

    /*
     * check
     */
    verify(target, atLeastOnce()).unsubscribeFederated("NetworkId");

    verify(systemMngInterface, times(2)).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onNodeAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Node)}.
   * @throws Exception
   */
  @Test
  public void testOnNodeAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("FederatedNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    /*
     * test
     */
    Node node = new Node("NodeId");
    target.onNodeAdded("OriginalNetworkId", node);

    /*
     * check
     */
    verify(conversionTable).addEntryNode("OriginalNetworkId", "NodeId",
        "FederatedNetworkId", "OriginalNetworkId_NodeId");
    Node expectedNode = new Node("FederatedNetworkId_NodeId");
    verify(netIf).putNode(expectedNode);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onNodeAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Node)}.
   * @throws Exception
   */
  @Test
  public void testOnNodeAdded_Fedetated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("FederatedNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    /*
     * test
     */
    Node node = new Node("NodeId");
    target.onNodeAdded("FederatedNetworkId", node);

    /*
     * check
     */
    verify(conversionTable, never()).addEntryNode(anyString(),
        anyString(), anyString(), anyString());
    PowerMockito.verifyPrivate(target, never()).invoke("networkInterfaces");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onPortAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}.
   * @throws Exception
   */
  @Test
  public void testOnPortAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("FederatedNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    /*
     * test
     */
    Port port = new Port("PortId", "NodeId");
    target.onPortAdded("OriginalNetworkId", port);

    /*
     * check
     */
    verify(conversionTable).addEntryPort("OriginalNetworkId", "NodeId",
        "PortId", "FederatedNetworkId", "OriginalNetworkId_NodeId",
        "OriginalNetworkId_NodeId_PortId");

    Port expectedPort = new Port("FederatedNetworkId_NodeId_PortId",
        "FederatedNetworkId_NodeId");
    verify(netIf).putPort(expectedPort);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onPortAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}.
   * @throws Exception
   */
  @Test
  public void testOnPortAdded_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    Port port = new Port("PortId", "NodeId");
    target.onPortAdded("FederatedNetworkId", port);

    /*
     * check
     */
    verify(conversionTable, never())
        .addEntryPort(anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    PowerMockito.verifyPrivate(target, never()).invoke("networkInterfaces");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onLinkAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}.
   * @throws Exception
   */
  @Test
  public void testOnLinkAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    conversionTable.addEntryPort("OriginalNetworkId", "SrcNodeId",
        "SrcPortId", "FederatedNetworkId",
        "OriginalNetworkId_SrcNodeId",
        "OriginalNetworkId_NodeId_SrcPortId");
    ;
    conversionTable.addEntryPort("OriginalNetworkId", "DstNodeId",
        "DstPortId", "FederatedNetworkId",
        "OriginalNetworkId_DstNodeId",
        "OriginalNetworkId_NodeId_DstPortId");
    ;
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("FederatedNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    target.onLinkAdded("OriginalNetworkId", link);

    /*
     * check
     */
    verify(conversionTable).addEntryLink("OriginalNetworkId", "LinkId",
        "FederatedNetworkId", "OriginalNetworkId_LinkId");

    Link expectedLink = new Link("LinkId", "SrcNodeId", "SrcPortId",
        "DstNodeId", "DstPortId");
    verify(netIf).putLink(expectedLink);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}.
   * @throws Exception
   */
  @Test
  public void testOnInPacketAddedPre() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    Map<String, String> attributes = new HashMap<>();
    InPacket inPacket = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), attributes);
    PowerMockito.doReturn(inPacket).when(target, "getInPacket", netIf,
        "PacketId");

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("NetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    Map<String, String> msgAttributes = new HashMap<>();
    Packet packet = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), msgAttributes);
    InPacketAdded msg = new InPacketAdded(packet);

    /*
     * test
     */
    boolean result = target.onInPacketAddedPre("NetworkId", msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}.
   * @throws Exception
   */
  @Test
  public void testOnOutPacketAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface netIf = Mockito.spy(new NetworkInterface(dispatcher,
        "OriginalNetworkId"));
    Map<String, String> attributes = new HashMap<>();
    List<String> portIds = new ArrayList<>();
    portIds.add("PortId");
    OutPacket outPacket = new OutPacket(
        "PacketId", "NodeId", portIds, new ArrayList<String>(),
        "data".getBytes(), attributes, new BasicFlowMatch("InNodeId", "InPortId"));
    PowerMockito.doReturn(outPacket).when(target, "delOutPacket", netIf,
        "PacketId");

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("NetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    Packet packet = new OutPacket(
        "PacketId", "NodeId", portIds, new ArrayList<String>(),
        "data".getBytes(), attributes, new BasicFlowMatch("InNodeId", "InPortId"));
    OutPacketAdded msg = new OutPacketAdded(packet);

    /*
     * test
     */
    target.onOutPacketAdded("NetworkId", msg);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   */
  @Test
  public void testOnFlowAdded() {

    /*
     * setting
     */
    BasicFlow flow = new BasicFlow();
    flow.path = new ArrayList<String>(Arrays.asList("path"));

    FederatorOnFlow onFlow = Mockito.mock(FederatorOnFlow.class);
    Whitebox.setInternalState(target, "federatorOnFlow", onFlow);

    /*
     * test
     */
    target.onFlowAdded("NetworkId", flow);

    /*
     * check
     */
    verify(onFlow).createOriginalFlow(flow);
    verifyNoMoreInteractions(onFlow);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   */
  @Test
  public void testOnFlowAdded_NoPath() {

    /*
     * setting
     */
    BasicFlow flow = new BasicFlow();
    flow.path = new ArrayList<String>();

    FederatorOnFlow onFlow = Mockito.mock(FederatorOnFlow.class);
    Whitebox.setInternalState(target, "federatorOnFlow", onFlow);

    /*
     * test
     */
    target.onFlowAdded("NetworkId", flow);

    /*
     * check
     */
    verify(onFlow).createOriginalFlow(flow);
    verifyNoMoreInteractions(onFlow);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws FederatorException
   */
  @Test
  public void testOnFlowUpdatePre_Established() throws Exception {
    /*
     *  setting
     */
    createPowerTarget();
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn("original").when(conversionTable, "getConnectionType", anyObject());


    BasicFlow prevFlow = new BasicFlow();
    prevFlow.path = new ArrayList<String>(Arrays.asList("path"));
    BasicFlow currFlow = new BasicFlow();
    currFlow.path = new ArrayList<String>(Arrays.asList("path"));
    currFlow.setStatus(FlowObject.FlowStatus.ESTABLISHED.toString());
    ArrayList<String> arraylist = new ArrayList<String>();

    FederatorOnFlow onFlow = Mockito.mock(FederatorOnFlow.class);
    Whitebox.setInternalState(target, "federatorOnFlow", onFlow);


    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("NetworkId", prevFlow,
        currFlow, arraylist);

    /*
     * check
     */
    assertThat(result, is(true));
    verifyNoMoreInteractions(onFlow);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws FederatorException
   */
  @Test
  public void testOnFlowUpdatePre_Failed() throws Exception {

    /*
    * setting
    */
    createPowerTarget();
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn("federated").when(conversionTable, "getConnectionType", anyObject());

    BasicFlow prevFlow = new BasicFlow();
    prevFlow.path = new ArrayList<String>(Arrays.asList("path"));
    BasicFlow currFlow = new BasicFlow();
    currFlow.path = new ArrayList<String>(Arrays.asList("path"));
    currFlow.setStatus(FlowObject.FlowStatus.FAILED.toString());
    ArrayList<String> arraylist = new ArrayList<String>();

    FederatorOnFlow onFlow = Mockito.mock(FederatorOnFlow.class);
    Whitebox.setInternalState(target, "federatorOnFlow", onFlow);


    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("NetworkId", prevFlow,
        currFlow, arraylist);
    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#postBoundaries(org.o3project.odenos.component.federator.FederatorBoundary)}.
   * @throws Exception
   */
  @Test
  public void testPostBoundaries() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    FederatorBoundaryTable table = Mockito
        .spy(new FederatorBoundaryTable());
    Whitebox.setInternalState(target, "federatorBoundaryTable", table);

    FederatorBoundary boundary = new FederatorBoundary("Id", "Type",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");
    PowerMockito.doReturn(true).when(target, "addBoundaryLink", boundary);

    /*
     * test
     */
    Response result = target.postBoundaries(boundary);

    /*
     * check
     */
    verify(table).addEntry(boundary);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(FederatorBoundary.class), is(boundary));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#putBoundaries(java.lang.String, org.o3project.odenos.component.federator.FederatorBoundary)}.
   * @throws Exception
   */
  @Test
  public void testPutBoundaries() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    FederatorBoundaryTable table = Mockito
        .spy(new FederatorBoundaryTable());
    Whitebox.setInternalState(target, "federatorBoundaryTable", table);

    FederatorBoundary boundary = new FederatorBoundary("boundaryId", "Type",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");
    PowerMockito.doReturn(true).when(target, "addBoundaryLink", boundary);

    /*
     * test
     */
    Response result = target.putBoundaries("boundaryId", boundary);

    /*
     * check
     */
    verify(table).addEntry("boundaryId", boundary);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(FederatorBoundary.class), is(boundary));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#deleteBoundaryLink(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDelBoundaries_Netwerk_Node_Port() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    FederatorBoundaryTable table = Mockito
        .spy(new FederatorBoundaryTable());
    Whitebox.setInternalState(target, "federatorBoundaryTable", table);

    PowerMockito.doNothing().when(target, "doDelAttributeBoundaryPort", 
        anyObject());
    PowerMockito.doReturn(true).when(target, "deleteBoundaryLink", "Id",
        "node", "port");

    /*
     * test
     */
    Response result = target.delBoundaries("BoundaryId");

    /*
     * check
     */
    verify(table).deleteEntry("BoundaryId");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#delBoundaries(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDelBoundaries() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    FederatorBoundaryTable table = Mockito
        .spy(new FederatorBoundaryTable());
    Whitebox.setInternalState(target, "federatorBoundaryTable", table);

    PowerMockito.doNothing().when(target, "doDelAttributeBoundaryPort", 
        anyObject());
    PowerMockito.doReturn(true).when(target, "deleteBoundaryLink", "Id");

    /*
     * test
     */
    Response result = target.delBoundaries("BoundaryId");

    /*
     * check
     */
    verify(table).deleteEntry("BoundaryId");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getBoundaries()}.
   */
  @Test
  public void testGetBoundaries() {

    /*
     * test
     */
    Response result = target.getBoundaries();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    FederatorBoundaryTable table =
        Whitebox.getInternalState(target, "federatorBoundaryTable");
    Map<String, FederatorBoundary> expectedBoundaries =
        Whitebox.getInternalState(table, "boundaries");

    assertThat(result.getBodyAsMap(FederatorBoundary.class),
        is(expectedBoundaries));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwNode(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwNode_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> nodes = Mockito.mock(Map.class);
    PowerMockito.doReturn(nodes).when(target, "getNwNodesFed");

    /*
     * test
     */
    Response result = target.getNwNode("federated");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(nodes));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwNode(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwNode_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> nodes = Mockito.mock(Map.class);
    PowerMockito.doReturn(nodes).when(target, "getNwNodesOrigin");

    /*
     * test
     */
    Response result = target.getNwNode("original");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(nodes));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwPort(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwPort_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> ports = Mockito.mock(Map.class);
    PowerMockito.doReturn(ports).when(target, "getNwPortsFed");

    /*
     * test
     */
    Response result = target.getNwPort("federated");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(ports));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwPort(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwPort_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> ports = Mockito.mock(Map.class);
    PowerMockito.doReturn(ports).when(target, "getNwPortsOrigin");

    /*
     * test
     */
    Response result = target.getNwPort("original");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(ports));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwLink(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwLink_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> links = Mockito.mock(Map.class);
    PowerMockito.doReturn(links).when(target, "getNwLinksFed");

    /*
     * test
     */
    Response result = target.getNwLink("federated");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(links));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwLink(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwLink_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> links = Mockito.mock(Map.class);
    PowerMockito.doReturn(links).when(target, "getNwLinksOrigin");

    /*
     * test
     */
    Response result = target.getNwLink("original");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(links));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwFlow(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwFlow_Federated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> flows = Mockito.mock(Map.class);
    PowerMockito.doReturn(flows).when(target, "getNwFlowsFed");

    /*
     * test
     */
    Response result = target.getNwFlow("federated");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(flows));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNwFlow(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNwFlow_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    @SuppressWarnings("unchecked")
    Map<String, String> flows = Mockito.mock(Map.class);
    PowerMockito.doReturn(flows).when(target, "getNwFlowsOrigin");

    /*
     * test
     */
    Response result = target.getNwFlow("original");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(String.class), is(flows));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#subscribeOriginal(java.lang.String)}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeOriginal() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.subscribeOriginal("NetworkId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(4)).invoke(
        "addEntryEventSubscription", anyString(), anyString());
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.IN_PACKET_ADDED, "NetworkId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.NODE_CHANGED, "NetworkId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.PORT_CHANGED, "NetworkId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.LINK_CHANGED, "NetworkId");

    PowerMockito.verifyPrivate(target, times(4)).invoke(
        "updateEntryEventSubscription", anyString(), anyString(),
        (ArrayList<String>) anyObject());
    ArrayList<String> expectedNodeAttributes = new ArrayList<String>(
        Arrays.asList("attributes::oper_status",
            "attributes::physical_id", "attributes::vendor"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.NODE_CHANGED,
        "NetworkId", expectedNodeAttributes);

    ArrayList<String> expectedPortAttributes = new ArrayList<String>(
        Arrays.asList("attributes::oper_status",
            "attributes::max_bandwidth", "attributes::physical_id",
            "attributes::vendor"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.PORT_CHANGED,
        "NetworkId", expectedPortAttributes);

    ArrayList<String> expectedLinkAttributes = new ArrayList<String>(
        Arrays.asList("attributes::oper_status", "attributes::latency",
            "attributes::max_bandwidth"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.LINK_CHANGED,
        "NetworkId", expectedLinkAttributes);

    ArrayList<String> expectedFlowAttributes = new ArrayList<String>(
        Arrays.asList("status", "attributes::req_bandwidth",
            "attributes::req_latency"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.FLOW_CHANGED,
        "NetworkId", expectedFlowAttributes);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#unsubscribeOriginal(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsubscribeOriginal() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.unsubscribeOriginal("NetworkId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(5)).invoke(
        "removeEntryEventSubscription", anyString(), anyString());
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.NODE_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.PORT_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.LINK_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.IN_PACKET_ADDED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.FLOW_CHANGED,
        "NetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#subscribeFederated(java.lang.String)}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeFederated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.subscribeFederated("NetworkId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(2)).invoke(
        "addEntryEventSubscription", anyString(), anyString());
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.OUT_PACKET_ADDED, "NetworkId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        Federator.FLOW_CHANGED, "NetworkId");

    ArrayList<String> expectedPortAttributes = new ArrayList<String>(
        Arrays.asList("attributes::unreserved_bandwidth",
            "attributes::vendor"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.PORT_CHANGED,
        "NetworkId", expectedPortAttributes);

    ArrayList<String> expectedLinkAttributes = new ArrayList<String>(
        Arrays.asList("attributes::cost", "attributes::req_latency",
            "attributes::unreserved_bandwidth",
            "attributes::req_bandwidth"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.LINK_CHANGED,
        "NetworkId", expectedLinkAttributes);

    ArrayList<String> expectedFlowAttributes = new ArrayList<String>(
        Arrays.asList("owner", "enabled", "attributes::bandwidth",
            "attributes::latency"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", Federator.FLOW_CHANGED,
        "NetworkId", expectedFlowAttributes);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#unsubscribeFederated(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsubscribeFederated() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.unsubscribeFederated("NetworkId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(5)).invoke(
        "removeEntryEventSubscription", anyString(), anyString());
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.FLOW_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.OUT_PACKET_ADDED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.NODE_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.PORT_CHANGED,
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", Federator.LINK_CHANGED,
        "NetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#createParser()}.
   * @throws Exception
   */
  @Test
  public void testCreateParser() throws Exception {

    /*
     * test
     */
    RequestParser<?> result = Whitebox.invokeMethod(target, "createParser");

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    Object state = Whitebox.getInternalState(result, "headState");
    assertThat(state, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyFlow()}.
   * @throws Exception
   */
  @Test
  public void testVerifyFlow() throws Exception {

    /*
     * setting
     */
    Flow basicFlow = new BasicFlow();

    /*
     * test & check
     */
    target.verifyFlow(basicFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyFlow()}.
   * @throws Exception
   */
  @Test(expected = FederatorException.class)
  public void testVerifyFlow_Invalid() throws Exception {

    /*
     * setting
     */
    Flow flow = new Flow();

    /*
     * test & check
     */
    target.verifyFlow(flow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNetworkIdByType(String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIdByType() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("OriginalNetworkId",
        Federator.ORIGINAL_NETWORK);
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    String resultOrg = target.getNetworkIdByType(Federator.ORIGINAL_NETWORK);
    String resultFed = target.getNetworkIdByType(Federator.FEDERATED_NETWORK);

    /*
     * check
     */
    assertThat(resultOrg, is("OriginalNetworkId"));
    assertThat(resultFed, is("FederatedNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNetworkIdByType(String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIdByType_Nothing() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    String resultOrg = target.getNetworkIdByType(Federator.ORIGINAL_NETWORK);
    String resultFed = target.getNetworkIdByType(Federator.FEDERATED_NETWORK);

    /*
     * check
     */
    assertThat(resultOrg, is(nullValue()));
    assertThat(resultFed, is(nullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#reflectToFederatedFromOriginal()}.
   * @throws Exception
   */
  @Test
  public void testReflectToFederatedFromOriginal() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "OriginalNetworkId"));
    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));

    Map<String, NetworkInterface> networkInterfaes = new HashMap<>();
    networkInterfaes.put("OriginalNetworkId", orgNetIf);
    networkInterfaes.put("FederatedNetworkId", fedNetIf);
    PowerMockito.doReturn(networkInterfaes).when(target,
        "networkInterfaces");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable
        .addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        "federated");

    conversionTable.addEntryPort("OriginalNetworkId", "NodeId1",
        "PortId1", "FederatedNetworkId", "OriginalNetworkId_NodeId1",
        "OriginalNetworkId_PortId1");
    conversionTable.addEntryPort("OriginalNetworkId", "NodeId2",
        "PortId2", "FederatedNetworkId", "OriginalNetworkId_NodeId2",
        "OriginalNetworkId_PortId2");
    conversionTable.addEntryPort("OriginalNetworkId",
        "OriginalNetworkId_NodeId1", "OriginalNetworkId_PortId1",
        "FederatedNetworkId", "NodeId1", "PortId1");
    conversionTable.addEntryPort("OriginalNetworkId",
        "OriginalNetworkId_NodeId2", "OriginalNetworkId_PortId2",
        "FederatedNetworkId", "NodeId2", "PortId2");

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, Node> nodes = new HashMap<>();
    Node node1 = new Node("NodeId1");
    Node node2 = new Node("NodeId2");
    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);

    Map<String, Link> links = new HashMap<>();
    Link link1 = new Link("LinkId1", "NodeId1", "PortId1",
        "OriginalNetworkId_NodeId1", "OriginalNetworkId_PortId1");
    Link link2 = new Link("LinkId2", "NodeId2", "PortId2",
        "OriginalNetworkId_NodeId2", "OriginalNetworkId_PortId2");
    links.put("LinkId1", link1);
    links.put("LinkId2", link2);

    Topology topology = new Topology(nodes, links);
    PowerMockito.doReturn(topology).when(orgNetIf).getTopology();

    /*
     * test
     */
    target.reflectToFederatedFromOriginal(orgNetIf, fedNetIf);

    /*
     * check
     */
    verify(target, atLeastOnce()).onNodeAdded("OriginalNetworkId", node1);
    verify(conversionTable).addEntryNode("OriginalNetworkId", "NodeId1",
        "FederatedNetworkId", "OriginalNetworkId_NodeId1");
    fedNetIf.putNode(node1);

    verify(target, atLeastOnce()).onNodeAdded("OriginalNetworkId", node2);
    verify(conversionTable).addEntryNode("OriginalNetworkId", "NodeId2",
        "FederatedNetworkId", "OriginalNetworkId_NodeId2");
    fedNetIf.putNode(node2);

    verify(target, atLeastOnce()).onLinkAdded("OriginalNetworkId", link1);
    verify(conversionTable).addEntryLink("OriginalNetworkId", "LinkId1",
        "FederatedNetworkId", "OriginalNetworkId_LinkId1");
    fedNetIf.putLink(link1);

    verify(target, atLeastOnce()).onLinkAdded("OriginalNetworkId", link2);
    verify(conversionTable).addEntryLink("OriginalNetworkId", "LinkId2",
        "FederatedNetworkId", "OriginalNetworkId_LinkId2");
    fedNetIf.putLink(link2);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#finalizingOriginalNetwork()}.
   * @throws Exception
   */
  @Test
  public void testFinalizingOriginalNetwork() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "OriginalNetworkId"));
    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put(orgNetIf.getNetworkId(), orgNetIf);
    netMap.put(fedNetIf.getNetworkId(), fedNetIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, List<String>> priority = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    Flow flow1 = new Flow("FlowId1");
    Flow flow2 = new Flow("FlowId2");
    flows.put("FlowId1", flow1);
    flows.put("FlowId2", flow2);
    FlowSet flowSet = new FlowSet("0", priority, flows);
    // doReturn
    doReturn(flowSet).when(fedNetIf).getFlowSet();

    Map<String, Port> ports1 = new HashMap<>();
    Port port11 = new Port("PortId1", "NodeId1");
    Port port12 = new Port("PortId2", "NodeId");
    ports1.put("PortId1", port11);
    ports1.put("PortId2", port12);
    Map<String, String> attributes1 = new HashMap<>();
    Node node1 = new Node("0", "NodeId1", ports1, attributes1);

    Map<String, Port> ports2 = new HashMap<>();
    Port port21 = new Port("PortId1", "NodeId2");
    Port port22 = new Port("PortId2", "NodeId2");
    ports2.put("PortId1", port21);
    ports2.put("PortId2", port22);
    Map<String, String> attributes2 = new HashMap<>();
    Node node2 = new Node("0", "NodeId2", ports2, attributes2);

    Map<String, Node> nodes = new HashMap<>();

    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);
    // doReturn
    doReturn(nodes).when(fedNetIf).getNodes();

    Map<String, Link> links = new HashMap<>();
    Link link1 = new Link("LinkId1", "NodeId1", "PortId1",
        "OriginalNetworkId_NodeId1", "OriginalNetworkId_PortId1");
    Link link2 = new Link("LinkId2", "NodeId2", "PortId2",
        "OriginalNetworkId_NodeId2", "OriginalNetworkId_PortId2");
    links.put("LinkId1", link1);
    links.put("LinkId2", link2);
    // doReturn
    doReturn(links).when(fedNetIf).getLinks();

    /*
     * test
     */
    target.finalizingOriginalNetwork(
        orgNetIf.getNetworkId(), fedNetIf.getNetworkId());
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#finalizingFederatedNetwork()}.
   * @throws Exception
   */
  @Test
  public void testFinalizingFederatedNetwork() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface orgNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "OriginalNetworkId"));
    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put(orgNetIf.getNetworkId(), orgNetIf);
    netMap.put(fedNetIf.getNetworkId(), fedNetIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, List<String>> priority = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    Flow flow1 = new Flow("FlowId1");
    Flow flow2 = new Flow("FlowId2");
    flows.put("FlowId1", flow1);
    flows.put("FlowId2", flow2);
    FlowSet flowSet = new FlowSet("0", priority, flows);
    // doReturn
    doReturn(flowSet).when(fedNetIf).getFlowSet();

    Map<String, Port> ports1 = new HashMap<>();
    Port port11 = new Port("PortId1", "NodeId1");
    Port port12 = new Port("PortId2", "NodeId");
    ports1.put("PortId1", port11);
    ports1.put("PortId2", port12);
    Map<String, String> attributes1 = new HashMap<>();
    Node node1 = new Node("0", "NodeId1", ports1, attributes1);

    Map<String, Port> ports2 = new HashMap<>();
    Port port21 = new Port("PortId1", "NodeId2");
    Port port22 = new Port("PortId2", "NodeId2");
    ports2.put("PortId1", port21);
    ports2.put("PortId2", port22);
    Map<String, String> attributes2 = new HashMap<>();
    Node node2 = new Node("0", "NodeId2", ports2, attributes2);

    Map<String, Node> nodes = new HashMap<>();

    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);
    // doReturn
    doReturn(nodes).when(fedNetIf).getNodes();

    Map<String, Link> links = new HashMap<>();
    Link link1 = new Link("LinkId1", "NodeId1", "PortId1",
        "OriginalNetworkId_NodeId1", "OriginalNetworkId_PortId1");
    Link link2 = new Link("LinkId2", "NodeId2", "PortId2",
        "OriginalNetworkId_NodeId2", "OriginalNetworkId_PortId2");
    links.put("LinkId1", link1);
    links.put("LinkId2", link2);
    // doReturn
    doReturn(links).when(fedNetIf).getLinks();

    List<String> list = new ArrayList<>();
    list.add(orgNetIf.getNetworkId());
    /*
     * test
     */
    target.finalizingFederatedNetwork(list, fedNetIf.getNetworkId());
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#postOutPacketNoBroadcast(String, OutPacket)}.
   * @throws Exception
   */
  @Test
  public void testPostOutPacketNoBroadcast() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    conversionTable.addEntryNetwork("OriginalNetworkId",
        "FederatedNetworkId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    OutPacket outPacket = new OutPacket();

    /*
     * test
     */
    target.postOutPacketNoBroadcast("OriginalNetworkId", outPacket);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#postOutPacketBroadcast(String, String, OutPacket)}.
   * @throws Exception
   */
  @Test
  public void testPostOutPacketBroadcast() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    conversionTable.addEntryNetwork("OriginalNetworkId",
        "FederatedNetworkId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    OutPacket outPacket = new OutPacket();

    /*
     * test
     */
    target.postOutPacketBroadcast(
        "OriginalNetworkId", "OriginalNetworkId2", outPacket);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getConvNetworkId()}.
   * @throws Exception
   */
  @Test
  public void testGetConvNetworkId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    conversionTable.addEntryNetwork("OriginalNetworkId",
        "FederatedNetworkId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    String result = target.getConvNetworkId("OriginalNetworkId");

    /*
     * check
     */
    assertThat(result, is("FederatedNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#addBoundaryLink()}.
   * @throws Exception
   */
  @Test
  public void testAddBoundaryLink() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    conversionTable.addEntryPort("NetworkId1", "NodeId1", "PortId1",
        "FederatedNetworkId1", "NetworkId1_NodeId1",
        "NetworkId1_NodeId1_PortId1");
    conversionTable.addEntryPort("NetworkId2", "NodeId2", "PortId2",
        "FederatedNetworkId2", "NetworkId2_NodeId2",
        "NetworkId2_NodeId2_PortId2");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf1 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId1"));
    Port port1 = new Port("PortId1", "NodeId1");

    doReturn(port1).when(netIf1).getPort("NodeId1", "PortId1");
    netIf1.putPort(port1);

    NetworkInterface netIf2 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId2"));
    Port port2 = new Port("PortId2", "NodeId2");
    doReturn(port2).when(netIf2).getPort("NodeId2", "PortId2");

    NetworkInterface fedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("NetworkId1", netIf1);
    netMap.put("NetworkId2", netIf2);
    netMap.put("FederatedNetworkId", fedNetIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    PowerMockito.doReturn(true).when(target, "doPutAttributeBoundaryPort", 
        anyObject(), anyObject());
    PowerMockito.doReturn(true).when(target, "doPutAttributeBoundaryPort", 
        anyObject(), anyObject());

    FederatorBoundary boundary = new FederatorBoundary("Id", "Type",
        "NetworkId1", "NodeId1", "PortId1", "NetworkId2", "NodeId2",
        "PortId2");

    
    /*
     * test
     */
    //boolean result = target.addBoundaryLink(boundary);

    /*
     * check
     */
    //assertThat(result, is(true));

    //Link expectedLink1 = new Link("Id_link01",
    //    "NetworkId1_NodeId1", "NetworkId1_NodeId1_PortId1",
    //    "NetworkId2_NodeId2", "NetworkId2_NodeId2_PortId2");
    //verify(fedNetIf).putLink(expectedLink1);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#deleteBoundaryLink()}.
   * @throws Exception
   */
  @Test
  public void testDeleteBoundaryLink() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        Federator.FEDERATED_NETWORK);
    conversionTable.addEntryPort("NetworkId1",
        "NetworkId1_NodeId1", "NetworkId1_NodeId1_PortId1",
        "FederatedNetworkId",
        "NetworkId1_NodeId1", "NetworkId1_NodeId1_PortId1");
    conversionTable.addEntryPort("NetworkId2",
        "NetworkId2_NodeId2", "NetworkId2_NodeId2_PortId2",
        "FederatedNetworkId",
        "NetworkId2_NodeId2", "NetworkId2_NodeId2_PortId2");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "FederatedNetworkId"));
    Link link1 = new Link("Id_link01",
        "NetworkId1_NodeId1", "NetworkId1_NodeId1_PortId1",
        "NetworkId2_NodeId2", "NetworkId2_NodeId2_PortId2");
    Link link2 = new Link("Id_link02",
        "NetworkId2_NodeId2", "NetworkId2_NodeId2_PortId2",
        "NetworkId1_NodeId1", "NetworkId1_NodeId1_PortId1");
    Map<String, Link> links = new HashMap<>();
    links.put("Id_link01", link1);
    links.put("Id_link02", link2);
    doReturn(links).when(netIf).getLinks();

    FederatorBoundaryTable table = new FederatorBoundaryTable();
    FederatorBoundary boundary = new FederatorBoundary("BoundaryId",
        "Type", "NetworkId1", "NetworkId1_NodeId1",
        "NetworkId1_NodeId1_PortId1",
        "NetworkId2", "NetworkId2_NodeId2",
        "NetworkId2_NodeId2_PortId2");
    table.addEntry("BoundaryId", boundary);
    Whitebox.setInternalState(target, "federatorBoundaryTable", table);

    HashMap<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("FederatedNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    /*
     * test
     */
    boolean result = target.deleteBoundaryLink("BoundaryId");

    /*
     * check
     */
    assertThat(result, is(true));

    verify(netIf).delLink("Id_link01");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getNodes()}.
   * @throws Exception
   */
  @Test
  public void testGetNodes() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = Mockito.spy(new ConversionTable());
    conversionTable
        .addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        "federated");

    conversionTable.addEntryNode("OriginalNetworkId", "NodeId1",
        "FederatedNetworkId", "OriginalNetworkId_NodeId1");
    conversionTable.addEntryNode("OriginalNetworkId", "NodeId2",
        "FederatedNetworkId", "OriginalNetworkId_NodeId2");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));

    Map<String, Node> nodes = new HashMap<>();
    Node node1 = new Node("NodeId1");
    Node node2 = new Node("NodeId2");
    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);

    Map<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("OriginalNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    doReturn(nodes).when(netIf).getNodes();

    /*
     * test
     */
    Map<String, String> result = target.getNodes("original");

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.get("OriginalNetworkId::NodeId1"),
        is("FederatedNetworkId::OriginalNetworkId_NodeId1"));
    assertThat(result.get("OriginalNetworkId::NodeId2"),
        is("FederatedNetworkId::OriginalNetworkId_NodeId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getPorts()}.
   * @throws Exception
   */
  @Test
  public void testGetPorts() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = Mockito.spy(new ConversionTable());
    conversionTable
        .addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        "federated");

    conversionTable.addEntryNode("OriginalNetworkId", "NodeId1",
        "FederatedNetworkId", "OriginalNetworkId_NodeId1");
    conversionTable.addEntryNode("OriginalNetworkId", "NodeId2",
        "FederatedNetworkId", "OriginalNetworkId_NodeId2");

    conversionTable.addEntryPort("OriginalNetworkId",
        "NodeId1", "PortId1",
        "FederatedNetworkId", "OriginalNetworkId_NodeId1",
        "OriginalNetworkId_NodeId1_PortId1");
    conversionTable.addEntryPort("OriginalNetworkId",
        "NodeId2", "PortId2",
        "FederatedNetworkId", "OriginalNetworkId_NodeId2",
        "OriginalNetworkId_NodeId2_PortId2");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));

    Map<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("OriginalNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    Map<String, Node> nodes = new HashMap<>();
    Node node1 = new Node("NodeId1");
    Node node2 = new Node("NodeId2");
    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);
    doReturn(nodes).when(netIf).getNodes();

    Map<String, Port> ports1 = new HashMap<>();
    Port port1 = new Port("PortId1", "NodeId1");
    ports1.put("PortId1", port1);
    doReturn(ports1).when(netIf).getPorts("NodeId1");

    Map<String, Port> ports2 = new HashMap<>();
    Port port2 = new Port("PortId2", "NodeId2");
    ports2.put("PortId2", port2);
    doReturn(ports2).when(netIf).getPorts("NodeId2");

    /*
     * test
     */
    Map<String, String> result = target.getPorts("original");

    /*
     * check
     */
    assertThat(result.size(), is(2));

    StringBuilder sbNode1 = new StringBuilder();
    sbNode1.append("FederatedNetworkId");
    sbNode1.append("::");
    sbNode1.append("OriginalNetworkId_NodeId1");
    sbNode1.append("::");
    sbNode1.append("FederatedNetworkId");
    sbNode1.append("::");
    sbNode1.append("OriginalNetworkId_NodeId1");
    sbNode1.append("::");
    sbNode1.append("OriginalNetworkId_NodeId1_PortId1");
    String expectedNode1 = sbNode1.toString();
    assertThat(result.get("OriginalNetworkId::NodeId1::PortId1"),
        is(expectedNode1));

    StringBuilder sbNode2 = new StringBuilder();
    sbNode2.append("FederatedNetworkId");
    sbNode2.append("::");
    sbNode2.append("OriginalNetworkId_NodeId2");
    sbNode2.append("::");
    sbNode2.append("FederatedNetworkId");
    sbNode2.append("::");
    sbNode2.append("OriginalNetworkId_NodeId2");
    sbNode2.append("::");
    sbNode2.append("OriginalNetworkId_NodeId2_PortId2");
    String expectedNode2 = sbNode2.toString();
    assertThat(result.get("OriginalNetworkId::NodeId2::PortId2"),
        is(expectedNode2));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getLinks()}.
   * @throws Exception
   */
  @Test
  public void testGetLinks() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = Mockito.spy(new ConversionTable());
    conversionTable
        .addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        "federated");

    conversionTable.addEntryLink("OriginalNetworkId", "LinkId1",
        "FederatedNetworkId", "OriginalNetworkId_LinkId1");
    conversionTable.addEntryLink("OriginalNetworkId", "LinkId2",
        "FederatedNetworkId", "OriginalNetworkId_LinkId2");

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));

    Map<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("OriginalNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    Map<String, Link> links = new HashMap<>();
    Link link1 = new Link("LinkId1", "SrcNode1", "SrcPort1", "DstNode1",
        "DstPort1");
    Link link2 = new Link("LinkId2", "SrcNode2", "SrcPort2", "DstNode2",
        "DstPort2");
    links.put("LinkId1", link1);
    links.put("LinkId2", link2);
    doReturn(links).when(netIf).getLinks();

    /*
     * test
     */
    Map<String, String> result = target.getLinks("original");

    /*
     * check
     */
    assertThat(result.size(), is(2));

    assertThat(result.get("OriginalNetworkId::LinkId1"),
        is("FederatedNetworkId::OriginalNetworkId_LinkId1"));
    assertThat(result.get("OriginalNetworkId::LinkId2"),
        is("FederatedNetworkId::OriginalNetworkId_LinkId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#getFlows()}.
   * @throws Exception
   */
  @Test
  public void testGetFlows() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = Mockito.spy(new ConversionTable());
    conversionTable
        .addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("FederatedNetworkId",
        "federated");

    conversionTable.addEntryFlow("OriginalNetworkId", "FlowId1",
        "FederatedNetworkId", "OriginalNetworkId_FlowId1");
    conversionTable.addEntryFlow("OriginalNetworkId", "FlowId2",
        "FederatedNetworkId", "OriginalNetworkId_FlowId2");

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));

    Map<String, NetworkInterface> netMap = new HashMap<>();
    netMap.put("OriginalNetworkId", netIf);
    PowerMockito.doReturn(netMap).when(target, "networkInterfaces");

    Map<String, List<String>> priority = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    Flow flow1 = new Flow("FlowId1", "Owner", true, "0");
    flows.put("FlowId1", flow1);
    Flow flow2 = new Flow("FlowId2", "Owner", true, "0");
    flows.put("FlowId2", flow2);
    FlowSet flowSet = new FlowSet("0", priority, flows);

    doReturn(flowSet).when(netIf).getFlowSet();

    /*
     * test
     */
    Map<String, String> result = target.getFlows("original");

    /*
     * check
     */
    assertThat(result.size(), is(2));

    assertThat(result.get("OriginalNetworkId::FlowId1"),
        is("FederatedNetworkId::OriginalNetworkId_FlowId1"));
    assertThat(result.get("OriginalNetworkId::FlowId2"),
        is("FederatedNetworkId::OriginalNetworkId_FlowId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyType()}.
   * @throws Exception
   */
  @Test
  public void testVerifyType() throws Exception {

    /*
     * test
     */
    target.verifyType("LogicAndNetwork");

    /*
     * check
     * expected: No Exception
     */
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyType()}.
   * @throws Exception
   */
  @Test(expected = FederatorException.class)
  public void testVerifyType_Invalid() throws Exception {

    /*
     * test & check
     */
    target.verifyType("Logic");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyId()}.
   * @throws Exception
   */
  @Test
  public void testVerifyId() throws Exception {

    /*
     * setting
     */
    ComponentConnection cc = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "ConnectionType", "ConnectionState", "ObjectId",
        "NetworkId");

    /*
     * test
     */
    target.verifyId(cc);

    /*
     * check
     * expected: No Exception
     */
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.Federator#verifyId()}.
   * @throws Exception
   */
  @Test(expected = FederatorException.class)
  public void testVerifyId_Invalid() throws Exception {

    /*
     * setting
     */
    ComponentConnection cc = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "ConnectionType", "ConnectionState", "LogicId",
        "NetworkId");

    /*
     * test & check
     */
    target.verifyId(cc);

  }

}
