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

package org.o3project.odenos.component.learningswitch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for LearningSwitch.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ LearningSwitch.class, NetworkInterface.class })
public class LearningSwitchTest {

  private LearningSwitch target;

  private MessageDispatcher dispatcher;

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

    target = Mockito
        .spy(new LearningSwitch("LearningObjectId", dispatcher));

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  private LearningSwitch createPowerTarget() throws Exception {

    LearningSwitch learningSwitch =
        PowerMockito.spy(new LearningSwitch("LearningObjectId",
            dispatcher));

    target = learningSwitch;

    return learningSwitch;

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#LearningSwitch(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public void testLearningSwitch() {

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getSuperType()}.
   */
  @Test
  public void testGetSuperType() {

    /*
     * test & check
     */
    assertThat(target.getSuperType(), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getDescription()}.
   */
  @Test
  public void testGetDescription() {

    /*
     * test & check
     */
    assertThat(target.getDescription(), is("Learning Switch"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPre() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("ObjectId",
            "ConnectionType",
            "ConnectionState", "LearningObjectId", "networkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

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
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPreWithNetwotk() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("ObjectId",
            "ConnectionType",
            "ConnectionState", "LearningObjectId", "networkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    Whitebox.setInternalState(target, "network", "NetworkId");

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPre_CurrNull() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr = null;

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPre_CurrParameterNull() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr = new ComponentConnection(null, null, null,
        null);

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(msg);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedUpdatePre() {

    /*
     * test & check
     */
    assertThat(target.onConnectionChangedUpdatePre(null), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedDeletePre() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("ObjectId",
            "ConnectionType",
            "ConnectionState", "LearningObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    Whitebox.setInternalState(target, "network", "NetworkId");

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
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("ObjectId",
            "ConnectionType",
            "ConnectionState", "LearningObjectId", "NetworkId");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    /*
     * test
     */
    target.onConnectionChangedAdded(msg);

    /*
     * check
     */
    verify(target, times(3)).subscribeNetwork();

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

    String network = Whitebox.getInternalState(target, "network");
    assertThat(network, is("NetworkId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    HashMap<String, String> fdb = Mockito
        .spy(new HashMap<String, String>());

    Whitebox.setInternalState(target, "network", "NetworkId");
    Whitebox.setInternalState(target, "fdb", fdb);

    ComponentConnection prev = Mockito
        .mock(ComponentConnectionLogicAndNetwork.class);
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("ObjectId",
            "ConnectionType",
            "ConnectionState", "LearningObjectId", "NetworkId");
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put("NetworkId", new NetworkInterface(dispatcher, "NetworkId"));
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "Action", prev, curr);

    /*
     * test
     */
    target.onConnectionChangedDelete(msg);

    /*
     * check
     */
    verify(target, atLeastOnce()).unsubscribeNetwork();
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

    verify(fdb, times(1)).clear();

    String network = Whitebox.getInternalState(target, "network");
    assertThat(network, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#subscriptionNetwork()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSubscribeNetworkSuccess() throws Exception {

    /*
     * setting
     */
    createPowerTarget();
    Whitebox.setInternalState(target, "network", "NetworkId");

    /*
     * test
     */
    target.subscribeNetwork();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "addEntryEventSubscription", "NODE_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "addEntryEventSubscription", "PORT_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "addEntryEventSubscription", "LINK_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "addEntryEventSubscription", "FLOW_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "addEntryEventSubscription", "IN_PACKET_ADDED", "NetworkId");

    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "updateEntryEventSubscription",
        "NODE_CHANGED", "NetworkId", null);
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "updateEntryEventSubscription",
        "PORT_CHANGED", "NetworkId", null);
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "updateEntryEventSubscription",
        "LINK_CHANGED", "NetworkId", null);
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "updateEntryEventSubscription",
        "FLOW_CHANGED", "NetworkId", null);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#subscriptionNetwork()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSubscribeNetworkThrowException() throws Exception {

    /*
     * setting
     */
    Exception ex = PowerMockito.spy(new Exception());
    LearningSwitch target = PowerMockito.spy(new LearningSwitch(
        "LearningObjectId", dispatcher));
    createPowerTarget();
    Whitebox.setInternalState(target, "network", "NetworkId");
    PowerMockito.doThrow(ex).when(target, "applyEventSubscription");

    /*
     * test
     */
    target.subscribeNetwork();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "applyEventSubscription");
    //PowerMockito.verifyPrivate(ex, times(1)).invoke("printStackTrace");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#unsubscriptionNetwork()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testUnsubscribeNetworkSuccess() throws Exception {

    /*
     * setting
     */
    createPowerTarget();
    Whitebox.setInternalState(target, "network", "NetworkId");

    /*
     * test
     */
    target.unsubscribeNetwork();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeEntryEventSubscription", "NODE_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeEntryEventSubscription", "PORT_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeEntryEventSubscription", "LINK_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "NetworkId");
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeEntryEventSubscription", "IN_PACKET_ADDED", "NetworkId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#subscriptionNetwork()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testUnsubscribeNetworkThrowException() throws Exception {

    /*
     * setting
     */
    Exception ex = PowerMockito.spy(new Exception());
    LearningSwitch target = PowerMockito.spy(new LearningSwitch(
        "LearningObjectId", dispatcher));
    createPowerTarget();
    Whitebox.setInternalState(target, "network", "NetworkId");
    PowerMockito.doThrow(ex).when(target, "applyEventSubscription");

    /*
     * test
     */
    target.unsubscribeNetwork();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "applyEventSubscription");
    //PowerMockito.verifyPrivate(ex, times(1)).invoke("printStackTrace");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnRequest() throws Exception {

    /*
     * setting
     */
    Method method = Request.Method.GET;
    Object body = new Object();
    Request request = new Request("ObjectId", method,
        "settings/default_idle_timer", body);

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
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnRequest_RequestNull() throws Exception {

    /*
     * test
     */
    Response result = target.onRequest(null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBody(String.class), containsString("Error"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onNodeAddedPre(java.lang.String, org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testOnNodeAddedPre() {

    /*
     * setting
     */
    Node node = Mockito.mock(Node.class);

    /*
     * test
     */
    boolean result = target.onNodeAddedPre("NetworkId", node);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onNodeUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.core.component.network.topology.Node, java.util.ArrayList)}
   * .
   */
  @Test
  public void testOnNodeUpdatePre() {

    /*
     * setting
     */
    Node prev = Mockito.mock(Node.class);
    Node curr = Mockito.mock(Node.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    boolean result = target.onNodeUpdatePre("NetworkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onNodeDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnNodeDelete() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PortId", new Port("PortId", "NodeId"));

    Node node = new Node("1", "NodeId", ports, null);

    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("fdb1", "NodeId::PortId");

    Whitebox.setInternalState(target, "fdb", fdb);

    /*
     * test
     */
    target.onNodeDelete("NetworkId", node);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onNodeDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnNodeDelete_NodeNull() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PortId", new Port("PortId", "NodeId"));

    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("fdb1", "NodeId::PortId");

    Whitebox.setInternalState(target, "fdb", fdb);

    /*
     * test
     */
    target.onNodeDelete("NetworkId", null);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, never()).invoke("deleteFlowByNode",
        eq("NodeId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onPortAddedPre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testOnPortAddedPre() {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);

    /*
     * test
     */
    boolean result = target.onPortAddedPre("NetworkId", port);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onPortUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, java.util.ArrayList)}
   * .
   */
  @Test
  public void testOnPortUpdatePre() {

    /*
     * setting
     */
    Port prev = Mockito.mock(Port.class);
    Port curr = Mockito.mock(Port.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    boolean result = target.onPortUpdatePre("NetworkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onPortDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnPortDelete() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PortId", new Port("PortId", "NodeId"));

    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("fdb1", "NodeId::PortId");

    Whitebox.setInternalState(target, "fdb", fdb);

    Port port = new Port("PortId", "NodeId");

    /*
     * test
     */
    target.onPortDelete("NetworkId", port);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onLinkAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testOnLinkAdded() {

    /*
     * setting
     */
    Link link = new Link("LinkId");

    /*
     * test
     */
    target.onLinkAdded("NetworkId", link);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onLinkDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnLinkDelete() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");

    /*
     * test
     */
    target.onLinkDelete("NetworkId", link);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onLinkDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnLinkDelete_FailToValifate() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Link link = new Link("LinkId");

    /*
     * test
     */
    target.onLinkDelete("NetworkId", link);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, never()).invoke("deleteFlowByLink",
        "LinkId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onLinkDelete(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnLinkDelete_LinkNull() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Link link = null;

    /*
     * test
     */
    target.onLinkDelete("NetworkId", link);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, never()).invoke("deleteFlowByLink",
        "LinkId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testOnFlowAddedPre() {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("NetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow curr = Mockito.spy(new OFPFlow("FlowId"));
    curr.setStatus("");
    ArrayList<String> attributesList = new ArrayList<String>();

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    Flow prev = Mockito.mock(Flow.class);

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(curr, never()).setEnabled(false);

    verify(networkInterface, never()).putFlow(curr);
    verify(networkInterface, never()).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate_StatusTeardown() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow curr = Mockito.spy(new OFPFlow("FlowId"));
    curr.setStatus("teardown");

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    BasicFlow settingBasicFlow =
        Mockito.spy(new BasicFlow("1", "FlowId", "Owner", true,
            "Priority", "teardown",
            new ArrayList<BasicFlowMatch>(),
            new ArrayList<String>(),
            new HashMap<String, List<FlowAction>>(),
            new HashMap<String, String>()));
    PowerMockito.doReturn(settingBasicFlow).when(target, "getFlow",
        networkInterface, "FlowId");

    Flow prev = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(settingBasicFlow, times(1)).setEnabled(false);

    verify(networkInterface, times(1)).putFlow(settingBasicFlow);
    verify(networkInterface, never()).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate_StatusFailed() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow curr = Mockito.spy(new OFPFlow("FlowId"));
    curr.setStatus("failed");

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    BasicFlow settingBasicFlow =
        Mockito.spy(new BasicFlow("1", "FlowId", "Owner", true,
            "Priority", "failed",
            new ArrayList<BasicFlowMatch>(),
            new ArrayList<String>(),
            new HashMap<String, List<FlowAction>>(),
            new HashMap<String, String>()));
    PowerMockito.doReturn(settingBasicFlow).when(target, "getFlow",
        networkInterface, "FlowId");

    Flow prev = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(curr, never()).setEnabled(false);

    verify(networkInterface, never()).putFlow(curr);
    verify(networkInterface, times(1)).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate_BasicFlow() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.spy(new BasicFlow("FlowId"));
    ArrayList<String> attributesList = new ArrayList<String>();

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(curr, never()).setEnabled(false);
    verify(networkInterface, never()).putFlow(curr);
    verify(networkInterface, never()).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate_NoStatus() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.spy(new OFPFlow("FlowId"));
    ArrayList<String> attributesList = new ArrayList<String>();

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(curr, never()).setEnabled(false);

    verify(networkInterface, never()).putFlow(curr);
    verify(networkInterface, never()).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowUpdate_NullFlow() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    Flow prev = Mockito.mock(Flow.class);
    Flow curr = null;
    ArrayList<String> attributesList = new ArrayList<String>();

    HashMap<String, NetworkInterface> networkInterfaceMap =
        new HashMap<String, NetworkInterface>();
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));
    networkInterfaceMap.put("NetworkId", networkInterface);

    PowerMockito.doReturn(networkInterfaceMap).when(target,
        "networkInterfaces");

    /*
     * test
     */
    target.onFlowUpdate("NetworkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(networkInterface, never()).putFlow(curr);
    verify(networkInterface, never()).delFlow("FlowId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onFlowDeletePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   */
  @Test
  public void testOnFlowDeletePre() {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);

    /*
     * test
     */
    boolean result = target.onFlowDeletePre("NetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#onInPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnInPacketAdded() throws Exception {

    /*
     * setting
     */
    createPowerTarget();

    OFPFlowMatch header = new OFPFlowMatch("InNode", "InPort");
    header.setEthSrc("DlSrc");
    header.setEthDst("DlDst");
    header.setIpv4Src("NwSrc");
    header.setIpv4Dst("NwDst");

    Packet inPacket = new OFPInPacket("PacketId", "NodeId", "PortId", 0,
        header, null, null);

    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "PacketId"));
    HashMap<String, NetworkInterface> networkMap = new HashMap<String, NetworkInterface>();
    networkMap.put("PacketId", networkInterface);

    doReturn(inPacket).when(networkInterface).getInPacket("PacketId");

    PowerMockito.doReturn(networkMap).when(target, "networkInterfaces");

    Response response = new Response(Response.OK, inPacket);
    PowerMockito.doReturn(response).when(networkInterface,
        "getObjectToNetwork", anyString(),
        eq("packets/in/PacketId"));

    InPacketAdded msg = Mockito.spy(new InPacketAdded(inPacket));

    /*
     * test
     */
    target.onInPacketAdded("PacketId", msg);

    /*
     * check
     */
    verify(networkInterface, times(1)).postOutPacket(
        (OutPacket) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getInPacket(org.o3project.odenos.core.component.NetworkInterface, java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetInPacket() throws Exception {

    /*
     * setting
     */
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));

    InPacket inPacket =
        new OFPInPacket("PacketId", "NodeId", "PortId", 0,
            new OFPFlowMatch(), null, null);
    Response response = new Response(Response.OK, inPacket);

    PowerMockito.doReturn(response).when(networkInterface,
        "getObjectToNetwork", anyString(),
        eq("packets/in/PacketId"));

    doReturn(inPacket).when(networkInterface).getInPacket("PacketId");

    /*
     * test
     */
    InPacket result = target.getInPacket(networkInterface, "PacketId");

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    BasicFlow resultFlow = result.getBody(BasicFlow.class);
    assertThat(resultFlow, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getInPacket(org.o3project.odenos.core.component.NetworkInterface, java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetInPacket_OfpFlowMatch() throws Exception {

    /*
     * setting
     */
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));

    InPacket inPacket =
        new OFPInPacket("PacketId", "NodeId", "PortId", 0,
            new OFPFlowMatch(), null, null);
    Response response = new Response(Response.OK, inPacket);

    PowerMockito.doReturn(response).when(networkInterface,
        "getObjectToNetwork", anyString(),
        eq("packets/in/PacketId"));

    doReturn(inPacket).when(networkInterface).getInPacket("PacketId");

    /*
     * test
     */
    InPacket result = target.getInPacket(networkInterface, "PacketId");

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    OFPInPacket resultFlow = result.getBody(OFPInPacket.class);
    assertThat(resultFlow, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getInPacket(org.o3project.odenos.core.component.NetworkInterface, java.lang.String)}
   * .
   */
  @Test
  public void testGetInPacket_NoInPacket() {

    /*
     * setting
     */
    NetworkInterface networkInterface = new NetworkInterface(dispatcher,
        "NetworkId");

    networkInterface.putFlow(new OFPFlow());

    /*
     * test
     */
    InPacket result = target.getInPacket(networkInterface, "PacketId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getInPacket(org.o3project.odenos.core.component.NetworkInterface, java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetInPacket_NullInPacket() throws Exception {

    /*
     * setting
     */
    NetworkInterface networkInterface =
        PowerMockito.spy(new NetworkInterface(dispatcher, "NetworkId"));

    PowerMockito.doReturn(null)
        .when(networkInterface, "getObjectToNetwork", anyString(),
            eq("packets/in/PacketId"));

    /*
     * test
     */
    InPacket result = target.getInPacket(networkInterface, "PacketId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#createParser()}.
   *
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
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getFdb()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetFdb() throws Exception {

    /*
     * setting
     */
    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("00:11:22:33:44:55", "NodeId1::PortId1");
    fdb.put("66:77:88:99:aa:bb", "NodeId2::PortId2");
    fdb.put("cc:dd:ee:ff:00:11", "NodeId3::PortId3");
    Whitebox.setInternalState(target, "fdb", fdb);

    /*
     * test
     */
    Response result = target.getFdb();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, String> fdbMap = result.getBodyAsMap(String.class);
    assertThat(fdbMap.size(), is(3));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getFdb()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetFdb_EmptyFdb() throws Exception {

    /*
     * test
     */
    Response result = target.getFdb();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBodyAsMap(String.class).size(), is(0));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getIdleTimer()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetIdleTimer() throws Exception {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "idleTimeout", 123);

    /*
     * test
     */
    Response result = target.getIdleTimer();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Integer.class), is(123));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getHardTimer()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetHardTimer() throws Exception {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "hardTimeout", 123);

    /*
     * test
     */
    Response result = target.getHardTimer();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Integer.class), is(123));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#putHardTimer(Integer)}.
   *
   * @throws Exception
   */
  @Test
  public void testPutHardTimer() throws Exception {

    /*
     * test
     */
    Response result = target.putHardTimer(123);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Integer.class), is(123));

    assertThat((Integer) Whitebox.getInternalState(target, "hardTimeout"),
        is(123));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#putIdleTimer(Integer)}.
   *
   * @throws Exception
   */
  @Test
  public void testPutIdleTimer() throws Exception {

    /*
     * test
     */
    Response result = target.putIdleTimer(123);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Integer.class), is(123));

    assertThat((Integer) Whitebox.getInternalState(target, "idleTimeout"),
        is(123));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFdb()}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFdb() throws Exception {

    /*
     * setting
     */
    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("00:11:22:33:44:55", "Value1");
    fdb.put("66:77:88:99:aa:bb", "Value2");
    fdb.put("cc:dd:ee:ff:00:11", "Value3");
    Whitebox.setInternalState(target, "fdb", fdb);

    /*
     * test
     */
    Response result = target.deleteFdb();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(fdb.isEmpty(), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFdb(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFdbString() throws Exception {

    /*
     * setting
     */
    HashMap<String, String> fdb = new HashMap<String, String>();
    fdb.put("00:11:22:33:44:55", "Value1");
    fdb.put("66:77:88:99:aa:bb", "Value2");
    fdb.put("cc:dd:ee:ff:00:11", "Value3");
    Whitebox.setInternalState(target, "fdb", fdb);

    /*
     * test
     */
    Response result = target.deleteFdb("66778899aabb");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(fdb.size(), is(2));
    assertThat(fdb.containsKey("00:11:22:33:44:55"), is(true));
    assertThat(fdb.containsKey("cc:dd:ee:ff:00:11"), is(true));
  }

  /**
   * Test method for {@literal org.o3project.odenos.component.LearningSwitch#createOFPFlow(InPacket, String, String, List<String>)}.
   *
   * @throws Exception
   */
  @Test
  public void testCreateOFPFlow() throws Exception {

    /*
     * setting
     */
    InPacket inPacket = new InPacket();
    inPacket.setVersion("123");
    inPacket.setHeader(new OFPFlowMatch());
    List<String> path = new ArrayList<>(Arrays.asList("path"));

    /*
     * test
     */
    OFPFlow result = (OFPFlow) target.createOFPFlow(inPacket, "NodeId", "PortId", path);

    /*
     * check
     */
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getFlowId(), is("LearningObjectId_0"));
    assertThat(result.getOwner(), is("LearningObjectId"));
    assertThat(result.getEnabled(), is(true));
    assertThat(result.getPriority(), is("0"));
    assertThat(result.getStatus(), is("none"));
    assertThat(result.getMatches().size(), is(1));
    assertThat(result.getMatches().get(0), is(OFPFlowMatch.class));
    assertThat(result.getIdleTimeout(), is(60L));
    assertThat(result.getHardTimeout(), is(300L));
    assertThat(result.getPath().size(), is(1));
    assertThat(result.getPath().contains("path"), is(true));
    assertThat(result.getEdgeActions().size(), is(1));
    assertThat(result.getEdgeActions().get("NodeId").size(), is(1));
    assertThat(result.getEdgeActions().get("NodeId").get(0),
        is(FlowAction.class));
    assertThat(result.getAttributes().size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFlowByLink(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFlowByLink() throws Exception {

    /*
     * setting
     */
    HashMap<String, NetworkInterface> networks = new HashMap<>();
    NetworkInterface networkInterface = PowerMockito
        .spy(new NetworkInterface(dispatcher, "NetworkId"));
    networks.put("NetworkId", networkInterface);
    Whitebox.setInternalState(target, "networkInterfaces", networks);

    Whitebox.setInternalState(target, "network", "NetworkId");

    HashMap<String, Flow> flows = new HashMap<>();
    OFPFlow flow1 = new OFPFlow("FlowId1");
    flow1.putPath(new ArrayList<String>(Arrays.asList("LinkId1")));
    flows.put("FlowId1", flow1);
    OFPFlow flow2 = new OFPFlow("FlowId2");
    flow2.putPath(new ArrayList<String>(Arrays.asList("LinkId2")));
    flows.put("FlowId2", flow2);
    OFPFlow flow3 = new OFPFlow("FlowId3");
    flow3.putPath(new ArrayList<String>(Arrays.asList("LinkId3")));
    flows.put("FlowId3", flow3);
    Whitebox.setInternalState(target, "flows", flows);

    doReturn(new Response(Response.OK, "Body")).when(networkInterface)
        .delFlow("FlowId2");

    /*
     * test
     */
    target.deleteFlowByLink("LinkId2");

    /*
     * check
     */
    verify(networkInterface).delFlow("FlowId2");
    verifyNoMoreInteractions(networkInterface);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFlowByNode(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFlowByNode() throws Exception {

    /*
     * setting
     */
    HashMap<String, NetworkInterface> networks = new HashMap<>();
    NetworkInterface networkInterface = PowerMockito
        .spy(new NetworkInterface(dispatcher, "NetworkId"));
    networks.put("NetworkId", networkInterface);
    Whitebox.setInternalState(target, "networkInterfaces", networks);

    Whitebox.setInternalState(target, "network", "NetworkId");

    HashMap<String, Flow> flows = new HashMap<>();
    OFPFlow flow1 = new OFPFlow("FlowId1");
    flow1.putPath(new ArrayList<String>(Arrays.asList("LinkId1")));
    flows.put("FlowId1", flow1);
    OFPFlow flow2 = new OFPFlow("FlowId2");
    flow2.putPath(new ArrayList<String>(Arrays.asList("LinkId2")));
    flows.put("FlowId2", flow2);
    OFPFlow flow3 = new OFPFlow("FlowId3");
    flow3.putPath(new ArrayList<String>(Arrays.asList("LinkId3")));
    flows.put("FlowId3", flow3);
    Whitebox.setInternalState(target, "flows", flows);

    Link link1 = new Link("LinkId1", "NodeId1", "PortId1", "NodeId2",
        "PortId2");
    Link link2 = new Link("LinkId2", "NodeId2", "PortId2", "NodeId3",
        "PortId3");
    Link link3 = new Link("LinkId3", "NodeId3", "PortId3", "NodeId1",
        "PortId1");

    doReturn(link1).when(networkInterface).getLink("LinkId1");
    doReturn(link2).when(networkInterface).getLink("LinkId2");
    doReturn(link3).when(networkInterface).getLink("LinkId3");

    doReturn(new Response(Response.OK, "Body")).when(networkInterface)
        .delLink("LinkId2");

    /*
     * test
     */
    target.deleteFlowByNode("NodeId2");

    /*
     * check
     */
    verify(networkInterface).delFlow("FlowId1");
    verify(networkInterface).delFlow("FlowId2");
    verify(networkInterface, never()).delFlow("FlowId3");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFlowByPort(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFlowByPort() throws Exception {

    /*
     * setting
     */
    HashMap<String, NetworkInterface> networks = new HashMap<>();
    NetworkInterface networkInterface = PowerMockito
        .spy(new NetworkInterface(dispatcher, "NetworkId"));
    networks.put("NetworkId", networkInterface);
    Whitebox.setInternalState(target, "networkInterfaces", networks);

    Whitebox.setInternalState(target, "network", "NetworkId");

    HashMap<String, Flow> flows = new HashMap<>();
    OFPFlow flow1 = new OFPFlow("FlowId1");
    flow1.putPath(new ArrayList<String>(Arrays.asList("LinkId1")));
    flows.put("FlowId1", flow1);
    OFPFlow flow2 = new OFPFlow("FlowId2");
    flow2.putPath(new ArrayList<String>(Arrays.asList("LinkId2")));
    flows.put("FlowId2", flow2);
    OFPFlow flow3 = new OFPFlow("FlowId3");
    flow3.putPath(new ArrayList<String>(Arrays.asList("LinkId3")));
    flows.put("FlowId3", flow3);
    Whitebox.setInternalState(target, "flows", flows);

    Link link1 = new Link("LinkId1", "NodeId1", "PortId1", "NodeId2",
        "PortId2");
    Link link2 = new Link("LinkId2", "NodeId2", "PortId2", "NodeId3",
        "PortId3");
    Link link3 = new Link("LinkId3", "NodeId3", "PortId3", "NodeId1",
        "PortId1");

    doReturn(link1).when(networkInterface).getLink("LinkId1");
    doReturn(link2).when(networkInterface).getLink("LinkId2");
    doReturn(link3).when(networkInterface).getLink("LinkId3");

    doReturn(new Response(Response.OK, "Body")).when(networkInterface)
        .delLink("LinkId2");

    /*
     * test
     */
    target.deleteFlowByPort("PortId2");

    /*
     * check
     */
    verify(networkInterface).delFlow("FlowId1");
    verify(networkInterface).delFlow("FlowId2");
    verify(networkInterface, never()).delFlow("FlowId3");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#deleteFlowByEthAddr(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteFlowByEthAddr() throws Exception {

    /*
     * setting
     */
    HashMap<String, NetworkInterface> networks = new HashMap<>();
    NetworkInterface networkInterface = PowerMockito
        .spy(new NetworkInterface(dispatcher, "NetworkId"));
    networks.put("NetworkId", networkInterface);
    Whitebox.setInternalState(target, "networkInterfaces", networks);

    Whitebox.setInternalState(target, "network", "NetworkId");

    HashMap<String, Flow> flows = new HashMap<>();

    OFPFlowMatch match1 = new OFPFlowMatch("InNodeId1", "InPortId1");
    match1.setEthSrc("00:11:22:33:44:55");
    match1.setEthDst("66:77:88:99:aa:bb");
    match1.setIpv4Src("192.168.1.0/24");
    match1.setIpv4Dst("192.168.2.0/24");

    List<BasicFlowMatch> matches1 = new ArrayList<BasicFlowMatch>();
    matches1.add(match1);
    List<String> path1 = new ArrayList<>(Arrays.asList("LinkId1"));
    Map<String, List<FlowAction>> edgeActions1 = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    OFPFlow flow1 = new OFPFlow("1", "FlowId1", "Owner", true, "Priority",
        "none", matches1, 30L, 60L, path1,
        edgeActions1, attributes1);
    flows.put("FlowId1", flow1);

    OFPFlowMatch match2 = new OFPFlowMatch("NodeId2", "PortId2");
    match1.setEthSrc("66:77:88:99:aa:bb");
    match1.setEthDst("cc:dd:ee:ff:00:11");
    match1.setIpv4Src("192.168.2.0/24");
    match1.setIpv4Dst("192.168.3.0/24");

    List<BasicFlowMatch> matches2 = new ArrayList<BasicFlowMatch>();
    matches2.add(match2);
    List<String> path2 = new ArrayList<>(Arrays.asList("LinkId2"));
    Map<String, List<FlowAction>> edgeActions2 = new HashMap<>();
    Map<String, String> attributes2 = new HashMap<>();
    OFPFlow flow2 = new OFPFlow("1", "FlowId2", "Owner", true, "Priority",
        "none", matches2, 30L, 60L, path2,
        edgeActions2, attributes2);
    flows.put("FlowId2", flow2);

    OFPFlowMatch match3 = new OFPFlowMatch("NodeId3", "PortId3");
    match1.setEthSrc("cc:dd:ee:ff:00:11");
    match1.setEthDst("00:11:22:33:44:55");
    match1.setIpv4Src("192.168.3.0/24");
    match1.setIpv4Dst("192.168.1.0/24");

    List<BasicFlowMatch> matches3 = new ArrayList<BasicFlowMatch>();
    matches3.add(match3);
    List<String> path3 = new ArrayList<>(Arrays.asList("LinkId3"));
    Map<String, List<FlowAction>> edgeActions3 = new HashMap<>();
    Map<String, String> attributes3 = new HashMap<>();
    OFPFlow flow3 = new OFPFlow("1", "FlowId3", "Owner", true, "Priority",
        "none", matches3, 30L, 60L, path3,
        edgeActions3, attributes3);
    flows.put("FlowId3", flow3);
    Whitebox.setInternalState(target, "flows", flows);

    /*
     * test
     */
    target.deleteFlowByEthAddr("66:77:88:99:aa:bb");

    /*
     * check
     */
    assertThat(flows.size(), is(3));
    assertThat(flows.containsKey("FlowId3"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getInNode(OFPFlow)}.
   *
   * @throws Exception
   */
  @Test
  public void testGetInNode() throws Exception {

    /*
     * setting
     */
    OFPFlowMatch match = new OFPFlowMatch("NodeId", "PortId");
    match.setEthSrc("00:11:22:33:44:55");
    match.setEthDst("66:77:88:99:aa:bb");
    match.setIpv4Src("192.168.1.0/24");
    match.setIpv4Dst("192.168.2.0/24");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(match);
    List<String> path = new ArrayList<>(Arrays.asList("LinkId"));
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes3 = new HashMap<>();
    OFPFlow flow = new OFPFlow("1", "FlowId", "Owner", true, "Priority",
        "none", matches, 30L, 60L, path,
        edgeActions, attributes3);

    /*
     * test
     */
    String result = target.getInNode(flow);

    /*
     * check
     */
    assertThat(result, is("NodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.learningswitch.LearningSwitch#getOutNode(OFPFlow)}.
   *
   * @throws Exception
   */
  @Test
  public void testGetOutNode() throws Exception {

    /*
     * setting
     */
    OFPFlowMatch match = new OFPFlowMatch("NodeId", "PortId");
    match.setEthSrc("00:11:22:33:44:55");
    match.setEthDst("66:77:88:99:aa:bb");
    match.setIpv4Src("192.168.1.0/24");
    match.setIpv4Dst("192.168.2.0/24");

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(match);
    List<String> path = new ArrayList<>(Arrays.asList("LinkId"));

    FlowAction flowAction = new OFPFlowActionSetField(match);

    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    edgeActions.put("NodeId",
        new ArrayList<FlowAction>(Arrays.asList(flowAction)));
    Map<String, String> attributes3 = new HashMap<>();
    OFPFlow flow = new OFPFlow("1", "FlowId", "Owner", true, "Priority",
        "none", matches, 30L, 60L, path,
        edgeActions, attributes3);

    /*
     * test
     */
    String result = target.getOutNode(flow);

    /*
     * check
     */
    assertThat(result, is("NodeId"));
  }

}
