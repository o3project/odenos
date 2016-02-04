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

package org.o3project.odenos.component.slicer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
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
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
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
 * Test class for Slicer.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Slicer.class, SliceConditionTable.class,
    ConversionTable.class, NetworkInterface.class })
@PowerMockIgnore({"javax.management.*"})
public class SlicerTest {

  private Slicer target;
  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());
    target = Mockito.spy(new Slicer("ObjectId", "BaseUri", dispatcher));
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    dispatcher = null;
  }

  Slicer createPowerTarget() throws Exception {

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());
    target = PowerMockito
        .spy(new Slicer("ObjectId", "BaseUri", dispatcher));

    return target;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   */
  @Test
  public void testOnRequest() {

    /*
     * setting
     */
    Request request = new Request("ObjectId", Method.GET,
        "settings/slice_condition_table", "txid",
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
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getSuperType()}.
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
    assertThat(result, is("Slicer"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getDescription()}.
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
    assertThat(result, is("Slicer Component"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getConnectionTypes()}.
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
    assertThat(result, is("sliver:*,original:1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedAddedPre_Original() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "ConnectionState", "ObjectId",
        "NetworkId");
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
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedAddedPre_Sliver() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "sliver", "ConnectionState", "ObjectId",
        "NetworkId");
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
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdatePre() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "ConnectionType", "ConnectionState", "LogicId",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedUpdatePre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdatePre_SameLogicId() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "ConnectionType", "ConnectionState", "ObjectId",
        "NetworkId");
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
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdatePre_Null() {

    /*
     * setting
     */
    ComponentConnection curr = null;
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedUpdatePre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedDeletePre() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "ConnectionType", "ConnectionState", "LogicId",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedDeletePre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedAdded_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "ConnectionState", "LogicId",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.when(target, "conversionTable")
        .thenReturn(conversionTable);

    ArrayList<String> connectios = new ArrayList<String>(Arrays.asList(
        "abc", "def", "ghi"));
    PowerMockito.doReturn(connectios).when(conversionTable)
        .getConnectionList("sliver");

    /*
     * test
     */
    target.onConnectionChangedAdded(msg);

    /*
     * check
     */
    verify(conversionTable, times(3)).addEntryNetwork(eq("NetworkId"),
        anyString());
    verify(conversionTable, times(1)).addEntryNetwork("NetworkId", "abc");
    verify(conversionTable, times(1)).addEntryNetwork("NetworkId", "def");
    verify(conversionTable, times(1)).addEntryNetwork("NetworkId", "ghi");

    PowerMockito.verifyPrivate(target, times(3)).invoke(
        "subscribeOriginal",
        "NetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedAdded_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "sliver", "ConnectionState", "LogicId",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.when(target, "conversionTable")
        .thenReturn(conversionTable);

    ArrayList<String> connectios = new ArrayList<String>(Arrays.asList(
        "abc", "def", "ghi"));
    PowerMockito.doReturn(connectios).when(conversionTable)
        .getConnectionList("original");

    /*
     * test
     */
    target.onConnectionChangedAdded(msg);

    /*
     * check
     */
    verify(conversionTable, times(1)).addEntryNetwork(anyString(),
        eq("NetworkId"));
    verify(conversionTable).addEntryNetwork("abc", "NetworkId");

    PowerMockito.verifyPrivate(target, times(3)).invoke("subscribeSliver",
        "NetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedUpdate(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdate() {

    ComponentConnectionChanged msg = Mockito
        .mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    target.onConnectionChangedUpdate(msg);

    /*
     * check
     */
    verifyNoMoreInteractions(msg);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedDelete_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "original", "ConnectionState", "Slicer",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    doNothing().when(conversionTable).delEntryConnectionType("NetworkId");

    /*
     * test
     */
    target.onConnectionChangedDelete(msg);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(3)).invoke(
        "unsubscribeOriginal",
        "NetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedDelete_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "sliver", "ConnectionState", "LogicId",
        "NetworkId");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", null, curr);

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.when(target, "conversionTable")
        .thenReturn(conversionTable);

    /*
     * test
     */
    target.onConnectionChangedDelete(msg);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(3)).invoke(
        "unsubscribeSliver",
        "NetworkId");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onInPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnInPacketAdded_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("OriginalNetwork", "SliverNetwork");
    conversionTable.addEntryConnectionType("OriginalNetwork", "original");
    conversionTable.addEntryConnectionType("SliverNetwork", "sliver");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> networkIfMap = new HashMap<>();
    NetworkInterface originalNetIf = PowerMockito
        .mock(NetworkInterface.class);
    networkIfMap.put("OriginalNetwork", originalNetIf);
    NetworkInterface sliverNetIf = PowerMockito
        .mock(NetworkInterface.class);
    networkIfMap.put("SliverNetwork", sliverNetIf);
    PowerMockito.doReturn(networkIfMap).when(target, "networkInterfaces");

    Map<String, String> attributes = new HashMap<>();
    BasicFlowMatch header = new BasicFlowMatch("InNode", "InPort");
    InPacket inPacket = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), attributes, header);

    PowerMockito.doReturn(inPacket).when(target, "delInPacket",
        originalNetIf, "PacketId");

    PowerMockito.doReturn("ConnectionId").when(target,
        "matchPriorityTable", (BasicFlowMatch) anyObject());

    Map<String, String> connectionToNwc = new HashMap<>();
    connectionToNwc.put("ConnectionId", "SliverNetwork");
    Whitebox.setInternalState(target, "connectionToNwc", connectionToNwc);

    InPacketAdded msg = new InPacketAdded(inPacket);

    /*
     * test
     */
    target.onInPacketAdded("OriginalNetwork", msg);

    /*
     * check
     */
    verify(sliverNetIf).postInPacket((InPacket) anyObject());

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onInPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnInPacketAdded_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn("sliver").when(conversionTable)
        .getConnectionType("SliverNetworkId");

    HashMap<String, NetworkInterface> networkIfMap = new HashMap<>();
    PowerMockito.doReturn(networkIfMap).when(target, "networkInterfaces");

    InPacketAdded msg = new InPacketAdded();

    /*
     * test
     */
    target.onInPacketAdded("SliverNetworkId", msg);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, never()).invoke("networkInterfaces");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnOutPacketAdded_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = PowerMockito
        .mock(ConversionTable.class);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn("original").when(conversionTable)
        .getConnectionType("NetworkId");

    HashMap<String, NetworkInterface> networkIfMap = new HashMap<>();
    PowerMockito.doReturn(networkIfMap).when(target, "networkInterfaces");

    OutPacketAdded msg = new OutPacketAdded();

    /*
     * test
     */
    target.onOutPacketAdded("NetworkId", msg);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, never()).invoke("networkInterfaces");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnOutPacketAdded_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("OriginalNetworkId", "SliverNetworkId");
    conversionTable.addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("SliverNetworkId", "sliver");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> networkIfMap = new HashMap<>();
    NetworkInterface originalNetIf = PowerMockito
        .mock(NetworkInterface.class);
    networkIfMap.put("OriginalNetworkId", originalNetIf);
    NetworkInterface sliverNetIf = PowerMockito
        .mock(NetworkInterface.class);
    networkIfMap.put("SliverNetworkId", sliverNetIf);
    PowerMockito.doReturn(networkIfMap).when(target, "networkInterfaces");

    PowerMockito.doReturn("ConnectionId").when(target,
        "matchPriorityTable", (BasicFlowMatch) anyObject());

    Map<String, String> connectionToNwc = new HashMap<>();
    connectionToNwc.put("ConnectionId", "SliverNetwork");
    Whitebox.setInternalState(target, "connectionToNwc", connectionToNwc);

    List<String> portIds = Arrays.asList("PortId");
    List<String> portExceptIds = Arrays.asList("NoPortId");
    Map<String, String> attributes = new HashMap<>();
    BasicFlowMatch header = new BasicFlowMatch("InNode", "InPort");

    OutPacket outPacket = new OutPacket("PacketId", "NodeId", portIds,
        portExceptIds, "data".getBytes(), attributes, header);
    PowerMockito.doReturn(outPacket).when(target, "delOutPacket",
        sliverNetIf, "PacketId");

    OutPacketAdded msg = new OutPacketAdded(outPacket);

    /*
     * test
     */
    target.onOutPacketAdded("SliverNetworkId", msg);

    /*
     * check
     */
    verify(originalNetIf).postOutPacket((OutPacket) anyObject());

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnFlowAddedPre_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("OriginalNetworkId", "SliverNetworkId");
    conversionTable.addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("SliverNetworkId", "sliver");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    Flow flow = new BasicFlow("1", "FlowId", "Owner", true, "1", "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("OriginalNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnFlowAddedPre_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("OriginalNetworkId", "SliverNetworkId");
    conversionTable.addEntryConnectionType("OriginalNetworkId", "original");
    conversionTable.addEntryConnectionType("SliverNetworkId", "sliver");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition = new BasicSliceCondition("ConditionId",
        "Type", "ConnectionId", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", condition);
    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    Map<String, String> nwcToConnection = new HashMap<>();
    nwcToConnection.put("SliverNetworkId", "ConnectionId");
    Whitebox.setInternalState(target, "nwcToConnection", nwcToConnection);

    BasicFlowMatch flowMatch = new BasicFlowMatch("InNode", "InPort");
    List<BasicFlowMatch> matches = Arrays.asList(flowMatch);
    List<String> path = Arrays.asList();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    Flow flow = new BasicFlow("1", "FlowId", "Owner", true, "1", "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("SliverNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getConditionTable()}.
   */
  @Test
  public void testGetConditionTable() {

    /*
     * setting
     */
    SliceConditionTable conditionTable = new SliceConditionTable();
    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    SliceConditionTable result = target.getConditionTable();

    /*
     * check
     */
    assertThat(result, is(conditionTable));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#Slicer(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSlicer() throws Exception {

    /*
     * test
     */
    target = new Slicer("ObjectId", "BaseUri", dispatcher);

    /*
     * check
     */
    String objectId = target.getObjectId();
    assertThat(objectId, is("ObjectId"));

    MessageDispatcher messageDispatcher = target.getMessageDispatcher();
    assertThat(messageDispatcher, is(dispatcher));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#subscribeOriginal(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSubscribeOriginal() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.subscribeOriginal("NwcId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "NODE_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "PORT_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "LINK_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "IN_PACKET_ADDED", "NwcId");

    ArrayList<String> portAttr = new ArrayList<String>(Arrays.asList(
        "attributes::unreserved_bandwidth", "attributes::is_boundary"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "PORT_CHANGED", "NwcId",
        portAttr);
    ArrayList<String> linkAttr = new ArrayList<String>(Arrays.asList(
        "attributes::cost", "attributes::req_latency",
        "attributes::unreserved_bandwidth"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "LINK_CHANGED", "NwcId",
        linkAttr);
    ArrayList<String> flowAttr = new ArrayList<String>(Arrays.asList(
        "status", "attributes::bandwidth", "attributes::latency"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "FLOW_CHANGED", "NwcId",
        flowAttr);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#unsubscribeOriginal(java.lang.String)}.
   * @throws Exception throws Exception in targets
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
    target.unsubscribeOriginal("NwcId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "NODE_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "PORT_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "LINK_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "IN_PACKET_ADDED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "NwcId");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#subscribeSliver(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSubscribeSliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.subscribeSliver("NwcId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "FLOW_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "OUT_PACKET_ADDED", "NwcId");

    ArrayList<String> nodeAttr = new ArrayList<String>(Arrays.asList(
        "attributes::oper_status", "attributes::physical_id",
        "attributes::vendor"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "NODE_CHANGED", "NwcId",
        nodeAttr);
    ArrayList<String> portAttr = new ArrayList<String>(Arrays.asList(
        "attributes::oper_status", "attributes::max_bandwidth",
        "attributes::physical_id", "attributes::vendor"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "PORT_CHANGED", "NwcId",
        portAttr);
    ArrayList<String> linkAttr = new ArrayList<String>(Arrays.asList(
        "attributes::oper_status", "attributes::latency",
        "attributes::max_bandwidth", "attributes::req_bandwidth"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "LINK_CHANGED", "NwcId",
        linkAttr);
    ArrayList<String> flowAttr = new ArrayList<String>(Arrays.asList(
        "enabled", "priority", "attributes::req_bandwidth",
        "attributes::req_latency"));
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "FLOW_CHANGED", "NwcId",
        flowAttr);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#unsubscribeSliver(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUnsubscribeSliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    /*
     * test
     */
    target.unsubscribeSliver("NwcId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "OUT_PACKET_ADDED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "NODE_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "PORT_CHANGED", "NwcId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "LINK_CHANGED", "NwcId");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#putSliceConditionTablePriority(java.lang.Integer, java.util.List)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSliceConditionTablePriority() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    PowerMockito.doReturn(sliceConditionTable).when(target)
        .getConditionTable();
    PowerMockito
        .doNothing()
        .when(sliceConditionTable)
        .addEntryToSliceCondition(anyString(),
            (SliceCondition) anyObject());

    List<BasicSliceCondition> body = new ArrayList<>();
    BasicSliceCondition basicSliceCondition = new BasicSliceCondition("Id",
        "BasicSliceCondition", "Connection", "InNode", "InPort");
    body.add(basicSliceCondition);

    /*
     * test
     */
    Response result = target.putSliceConditionTablePriority("1", body);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsList(BasicSliceCondition.class), is(body));

    verify(sliceConditionTable).addEntryToSliceCondition("1",
        basicSliceCondition);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#postSliceConditionTableConditionId(java.lang.String, org.o3project.odenos.component.slicer.BasicSliceCondition)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostSliceConditionTableConditionId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    PowerMockito.doReturn(sliceConditionTable).when(target)
        .getConditionTable();
    PowerMockito
        .doNothing()
        .when(sliceConditionTable)
        .addEntryToSliceCondition(anyString(), anyString(),
            (SliceCondition) anyObject());

    BasicSliceCondition body = new BasicSliceCondition("ConditionId",
        "BasicSliceCondition", "Connection", "InNode", "InPort");

    /*
     * test
     */
    Response result = target.postSliceConditionTableConditionId("ConditionId", body);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(BasicSliceCondition.class), is(body));

    verify(sliceConditionTable).addEntryToSliceCondition("ConditionId", body);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#putSliceConditionTableConditionId(java.lang.Integer, org.o3project.odenos.component.slicer.BasicSliceCondition)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSliceConditionTableConditionId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    PowerMockito.doReturn(sliceConditionTable).when(target)
        .getConditionTable();
    PowerMockito
        .doNothing()
        .when(sliceConditionTable)
        .addEntryToSliceCondition(anyString(), anyString(),
            (SliceCondition) anyObject());

    BasicSliceCondition body = new BasicSliceCondition("ConditionId",
        "BasicSliceCondition", "Connection", "InNode", "InPort");

    /*
     * test
     */
    Response result = target.putSliceConditionTableConditionId("1", "ConditionId", body);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(BasicSliceCondition.class), is(body));

    verify(sliceConditionTable).addEntryToSliceCondition("1", "ConditionId", body);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#deleteSliceConditionTable(java.lang.Integer)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteSliceConditionTable() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    PowerMockito.doReturn(sliceConditionTable).when(target)
        .getConditionTable();
    PowerMockito
        .doNothing()
        .when(sliceConditionTable)
        .addEntryToSliceCondition(anyString(),
            (SliceCondition) anyObject());

    /*
     * test
     */
    Response result = target.deleteSliceConditionTable("1");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    verify(sliceConditionTable).deleteSliceConditionTable("1");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#deleteSliceConditionTableConditionId(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteSliceConditionTableConditionId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    Whitebox.setInternalState(target, "conditionTable", sliceConditionTable);

    /*
     * test
     */
    Response result = target
        .deleteSliceConditionTableConditionId("ConditionId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    verify(sliceConditionTable).deleteSliceCondition("ConditionId");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getSliceConditionTablePriority(java.lang.Integer)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetSliceConditionTablePriority() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable sliceConditionTable = PowerMockito
        .mock(SliceConditionTable.class);
    Whitebox.setInternalState(target, "conditionTable", sliceConditionTable);

    /*
     * test
     */
    Response result = target.getSliceConditionTablePriority("1");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    verify(sliceConditionTable).getConditionIdList("1");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getSliceConditionTableConditionId(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetSliceConditionTableConditionId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition = new BasicSliceCondition(
        "ConditionId", "Type", "ConnectionId", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", "ConditionId", condition);

    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    Response result = target
        .getSliceConditionTableConditionId("ConditionId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(SliceCondition.class), is(condition));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getSliceConditionTableConnectionId(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetSliceConditionTableConnectionId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition = new BasicSliceCondition(
        "ConditionId", "Type", "ConnectionId", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", "ConditionId", condition);

    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    Response result = target
        .getSliceConditionTableConnectionId("ConnectionId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    List<String> expectIds = Arrays.asList("ConditionId");
    assertThat(result.getBodyAsList(String.class), is(expectIds));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#createParser()}.
   * @throws Exception throws Exception in targets
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
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#doOnConnectionDeleteSliver(org.o3project.odenos.component.networkinterface)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public final void testDoOnConnectionDeleteSliver() throws Exception {

    // setting
    NetworkInterface slivIf = Mockito.spy(new NetworkInterface(dispatcher,
        "sliver_nw"));

    target.doOnConnectionDeleteSliver(slivIf);
  }
  
  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#doOnConnectionDeleteOriginal(org.o3project.odenos.component.networkinterface)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public final void testdoOnConnectionDeleteOriginal() throws Exception {

    // setting
    createPowerTarget();

    NetworkInterface orgIf = Mockito.spy(new NetworkInterface(dispatcher,
        "original_nw"));

    target.doOnConnectionDeleteOriginal(orgIf);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#reflecteToSliverFromOriginal(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public final void testReflecteToSliverFromOriginal() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    NetworkInterface orgIf = new NetworkInterface(dispatcher, "original_nw");
    NetworkInterface aggIf = Mockito.spy(new NetworkInterface(dispatcher,
        "sliver_nw"));

    Whitebox.invokeMethod(target, "reflecteToSliverFromOriginal", orgIf,
        aggIf);

    verify(aggIf).getTopology();

  }

  /**
  * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getConvNetworkId(java.lang.String)}.
  * @throws Exception throws Exception in targets
  */
  @Test
  public final void testGetConvNetworkId() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    String nwId = "network01";

    ArrayList<String> nwList = new ArrayList<String>(Arrays.asList(
        "network01", "network02"));

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(nwList).when(conversionTable, "getNetwork", nwId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat((String) Whitebox.invokeMethod(target, "getConvNetworkId",
        nwId), is("network01"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getFlowMapping(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetFlowMapping_Original() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("OrgNwcId", "original");
    conversionTable.addEntryConnectionType("RepNwcId", "sliver");

    conversionTable.addEntryNetwork("OrgNwcId", "RepNwcId");
    conversionTable.addEntryFlow("OrgNwcId", "OrgFlowId", "RepNwcId",
        "RepFlowId");

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getFlowMapping",
        "original");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, String> bodyMap = result.getBodyAsMap(String.class);
    assertThat(bodyMap.size(), is(1));
    assertThat(bodyMap.get("OrgFlowId"), is("RepNwcId::RepFlowId"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#getFlowMapping(java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetFlowMapping_Sliver() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("ConnectionId", "original");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getFlowMapping",
        "sliver");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, String> bodyMap = result.getBodyAsMap(String.class);
    assertThat(bodyMap.size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#isMatchConditonTable(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testIsMatchConditonTable() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition = new BasicSliceCondition(
        "ConditionId", "Type", "ConnectionId", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", condition);

    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    BasicFlowMatch match = new BasicFlowMatch("InNode", "InPort");
    boolean result =
        Whitebox.invokeMethod(target, "isMatchConditonTable",
            "ConnectionId", match);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#matchPriorityTable(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testMatchPriorityTable() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition1 = new BasicSliceCondition(
        "ConditionId1", "Type", "ConnectionId1", "InNode", "InPort");
    SliceCondition condition2 = new BasicSliceCondition(
        "ConditionId2", "Type", "ConnectionId2", "InNode", "InPort");
    SliceCondition condition3 = new BasicSliceCondition(
        "ConditionId3", "Type", "ConnectionId3", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", condition1);
    conditionTable.addEntryToSliceCondition("65535", condition2);
    conditionTable.addEntryToSliceCondition("1234", condition3);

    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    BasicFlowMatch match = new BasicFlowMatch("InNode", "InPort");
    String result =
        Whitebox.invokeMethod(target, "matchPriorityTable", match);

    /*
     * check
     */
    assertThat(result, is("ConnectionId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.Slicer#isMatchSliceConditon(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testIsMatchSliceConditon() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    SliceConditionTable conditionTable = new SliceConditionTable();
    SliceCondition condition = new BasicSliceCondition(
        "ConditionId", "Type", "ConnectionId", "InNode", "InPort");
    conditionTable.addEntryToSliceCondition("1", "ConditionId", condition);

    Whitebox.setInternalState(target, "conditionTable", conditionTable);

    /*
     * test
     */
    BasicFlowMatch match = new BasicFlowMatch("InNode", "InPort");
    boolean result =
        Whitebox.invokeMethod(target, "isMatchSliceConditon",
            "ConditionId", match);

    /*
     * check
     */
    assertThat(result, is(true));

  }

}
