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

package org.o3project.odenos.core.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowChanged;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.LinkChanged;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.NodeChanged;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.PortChanged;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.message.Event;
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
 * Test class for Logic.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Logic.class, ConversionTable.class, NetworkInterface.class })
public class LogicTest {

  private Logic target;
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

    // dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    dispatcher = Mockito.mock(MessageDispatcher.class);

    String objectId = "objectId";
    target = Mockito.spy(new Logic(objectId, dispatcher) {

      @Override
      protected String getSuperType() {
        return "SuperType";
      }

      @Override
      protected String getDescription() {
        return "Description";
      }
    });

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    dispatcher = null;

  }

  private Logic createPowerSpy() throws Exception {

    String objectId = "objectId";
    Logic logic = PowerMockito.spy(new Logic(objectId, dispatcher) {

      @Override
      protected String getSuperType() {
        return "SuperType";
      }

      @Override
      protected String getDescription() {
        return "Description";
      }

    });

    target = logic;
    return logic;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#Logic(java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testLogic() throws Exception {

    /*
     * test
     */
    target = new Logic(null, null) {

      @Override
      protected String getSuperType() {
        return "SuperType";
      }

      @Override
      protected String getDescription() {
        return "Description";
      }
    };

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversionTable()}.
   */
  @Test
  public void testConversionTable() {

    /*
     * test
     */
    ConversionTable result = target.conversionTable();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#systemMngInterface()}.
   */
  @Test
  public void testSystemMngInterface() {

    /*
     * test
     */
    SystemManagerInterface result = target.systemMngInterface();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#networkInterfaces()}.
   */
  @Test
  public void testNetworkInterfaces() {

    /*
     * test
     */
    HashMap<String, NetworkInterface> result = target.networkInterfaces();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnEvent() throws Exception {

    /*
     * setting
     */
    String publisherId = "publisherId";
    String eventType = NodeChanged.TYPE;
    Object body = new Object();
    doNothing().when(target).onNodeAdded(eq(publisherId),
        (Node) anyObject());

    Event event = Mockito.spy(new Event(publisherId, eventType, body));
    doReturn(null).when(event).getBody(NodeChanged.class);

    /*
     * test
     */
    target.onEvent(event);

    /*
     * check
     */
    verify(target, times(1)).onNodeChanged(eq(publisherId),
        (NodeChanged) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPre() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    boolean result = target
        .onConnectionChangedAddedPre(componentConnectionChanged);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedUpdatePre() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    boolean result = target
        .onConnectionChangedUpdatePre(componentConnectionChanged);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedDeletePre() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    boolean result = target
        .onConnectionChangedDeletePre(componentConnectionChanged);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAdded() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    target.onConnectionChangedAdded(componentConnectionChanged);

    /*
     * check
     */
    verify(target).onConnectionChangedAdded(componentConnectionChanged);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedUpdate(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedUpdate() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    target.onConnectionChangedUpdate(componentConnectionChanged);

    /*
     * check
     */
    verify(target).onConnectionChangedUpdate(componentConnectionChanged);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedDelete() {

    /*
     * setting
     */
    ComponentConnectionChanged componentConnectionChanged =
        Mockito.mock(ComponentConnectionChanged.class);

    /*
     * test
     */
    target.onConnectionChangedDelete(componentConnectionChanged);

    /*
     * check
     */
    verify(target).onConnectionChangedDelete(componentConnectionChanged);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#addEntryEventSubscription(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryEventSubscription() {

    /*
     * setting
     */
    EventSubscription eventSubscription = Mockito
        .spy(new EventSubscription("objectId"));
    Whitebox.setInternalState(target, "eventSubscription",
        eventSubscription);

    HashMap<String, ArrayList<String>> subscriptionTable =
        Mockito.spy(new HashMap<String, ArrayList<String>>());
    Whitebox.setInternalState(target, "subscriptionTable",
        subscriptionTable);

    /*
     * test
     */
    target.addEntryEventSubscription("NODE_CHANGED", "nwcId");
    target.addEntryEventSubscription("PORT_CHANGED", "nwcId");
    target.addEntryEventSubscription("LINK_CHANGED", "nwcId");
    target.addEntryEventSubscription("FLOW_CHANGED", "nwcId");
    target.addEntryEventSubscription("IN_PACKET_ADDED", "nwcId");
    target.addEntryEventSubscription("OUT_PACKET_ADDED", "nwcId");

    /*
     * check
     */
    InOrder eventSubscriptionInOrder = Mockito.inOrder(eventSubscription);
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "NodeChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "PortChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "LinkChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "FlowChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "InPacketAdded");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "OutPacketAdded");

    InOrder subscriptionTableInOrder = Mockito.inOrder(subscriptionTable);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "NODE_CHANGED::nwcId", null);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "PORT_CHANGED::nwcId", null);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "LINK_CHANGED::nwcId", null);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "FLOW_CHANGED::nwcId", null);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "IN_PACKET_ADDED::nwcId", null);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "OUT_PACKET_ADDED::nwcId", null);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#addEntryEventSubscription(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryEventSubscription_null() {

    /*
     * test
     */
    target.addEntryEventSubscription(null, null);

    /*
     * check
     */
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#removeEntryEventSubscription(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testRemoveEntryEventSubscription() {

    /*
     * setting
     */
    EventSubscription eventSubscription = Mockito
        .spy(new EventSubscription("objectId"));
    Whitebox.setInternalState(target, "eventSubscription",
        eventSubscription);

    HashMap<String, ArrayList<String>> subscriptionTable =
        Mockito.spy(new HashMap<String, ArrayList<String>>());
    Whitebox.setInternalState(target, "subscriptionTable",
        subscriptionTable);

    /*
     * test
     */
    target.removeEntryEventSubscription("NODE_CHANGED", "nwcId");
    target.removeEntryEventSubscription("PORT_CHANGED", "nwcId");
    target.removeEntryEventSubscription("LINK_CHANGED", "nwcId");
    target.removeEntryEventSubscription("FLOW_CHANGED", "nwcId");
    target.removeEntryEventSubscription("IN_PACKET_ADDED", "nwcId");
    target.removeEntryEventSubscription("OUT_PACKET_ADDED", "nwcId");

    /*
     * check
     */
    InOrder eventSubscriptionInOrder = Mockito.inOrder(eventSubscription);
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "NodeChanged");
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "PortChanged");
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "LinkChanged");
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "FlowChanged");
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "InPacketAdded");
    eventSubscriptionInOrder.verify(eventSubscription).removeFilter(
        "nwcId", "OutPacketAdded");

    InOrder subscriptionTableInOrder = Mockito.inOrder(subscriptionTable);
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "NODE_CHANGED::nwcId");
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "PORT_CHANGED::nwcId");
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "LINK_CHANGED::nwcId");
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "FLOW_CHANGED::nwcId");
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "IN_PACKET_ADDED::nwcId");
    subscriptionTableInOrder.verify(subscriptionTable).remove(
        "OUT_PACKET_ADDED::nwcId");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#removeEntryEventSubscription(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testRemoveEntryEventSubscription_null() {

    /*
     * test
     */
    target.removeEntryEventSubscription(null, null);

    /*
     * check
     */
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#updateEntryEventSubscription(java.lang.String, java.lang.String, java.util.ArrayList)}
   * .
   */
  @Test
  public void testUpdateEntryEventSubscription() {

    /*
     * setting
     */
    EventSubscription eventSubscription = Mockito
        .spy(new EventSubscription("objectId"));
    Whitebox.setInternalState(target, "eventSubscription",
        eventSubscription);

    HashMap<String, ArrayList<String>> subscriptionTable =
        Mockito.spy(new HashMap<String, ArrayList<String>>());
    Whitebox.setInternalState(target, "subscriptionTable",
        subscriptionTable);

    /*
     * test
     */
    ArrayList<String> attributes = new ArrayList<String>();
    target.updateEntryEventSubscription("NODE_CHANGED", "nwcId", attributes);
    target.updateEntryEventSubscription("PORT_CHANGED", "nwcId", attributes);
    target.updateEntryEventSubscription("LINK_CHANGED", "nwcId", attributes);
    target.updateEntryEventSubscription("FLOW_CHANGED", "nwcId", attributes);

    /*
     * check
     */
    InOrder eventSubscriptionInOrder = Mockito.inOrder(eventSubscription);
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "NodeChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "PortChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "LinkChanged");
    eventSubscriptionInOrder.verify(eventSubscription).addFilter("nwcId",
        "FlowChanged");

    InOrder subscriptionTableInOrder = Mockito.inOrder(subscriptionTable);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "NODE_CHANGED::UPDATE::nwcId",
        attributes);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "PORT_CHANGED::UPDATE::nwcId",
        attributes);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "LINK_CHANGED::UPDATE::nwcId",
        attributes);
    subscriptionTableInOrder.verify(subscriptionTable).put(
        "FLOW_CHANGED::UPDATE::nwcId",
        attributes);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeChanged(java.lang.String, org.o3project.odenos.core.component.network.topology.NodeChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnNodeChanged() throws Exception {

    /*
     * setting
     */
    NodeChanged nodeChanged = new NodeChanged();
    nodeChanged.action = "add";

    Node node = new Node();
    nodeChanged.curr = node;

    target.addEntryEventSubscription("NODE_CHANGED", "networkId");

    /*
     * test
     */
    target.onNodeChanged("networkId", nodeChanged);

    /*
     * check
     */
    verify(target, times(1)).onNodeAdded(eq("networkId"),
        (Node) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortChanged(java.lang.String, org.o3project.odenos.core.component.network.topology.PortChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnPortChanged() throws Exception {

    /*
     * setting
     */
    PortChanged portChanged = new PortChanged();
    portChanged.action = "add";

    Port port = new Port();
    portChanged.curr = port;

    target.addEntryEventSubscription("PORT_CHANGED", "networkId");

    /*
     * test
     */
    target.onPortChanged("networkId", portChanged);

    /*
     * check
     */
    verify(target, times(1)).onPortAdded(eq("networkId"),
        (Port) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkChanged(java.lang.String, org.o3project.odenos.core.component.network.topology.LinkChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnLinkChanged() throws Exception {

    /*
     * setting
     */
    LinkChanged linkChanged = new LinkChanged();
    linkChanged.action = "add";

    Link link = new Link();
    linkChanged.curr = link;

    target.addEntryEventSubscription("LINK_CHANGED", "networkId");

    /*
     * test
     */
    target.onLinkChanged("networkId", linkChanged);

    /*
     * check
     */
    verify(target, times(1)).onLinkAdded(eq("networkId"),
        (Link) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowChanged(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowChanged() throws Exception {

    /*
     * setting
     */
    FlowChanged flowChanged = new FlowChanged();
    flowChanged.action = "add";

    Flow flow = new Flow() {

      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return null;
      }
    };
    flowChanged.curr = flow;

    target.addEntryEventSubscription("FLOW_CHANGED", "networkId");

    /*
     * test
     */
    target.onFlowChanged("networkId", flowChanged);

    /*
     * check
     */
    verify(target, times(1)).onFlowAdded(eq("networkId"),
        (Flow) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onInPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnInPacketAdded() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    InPacketAdded msg = Mockito.mock(InPacketAdded.class);

    /*
     * test
     */
    target.onInPacketAdded("networkId", msg);

    /*
     * check
     */
    verify(target, times(1)).conversion(eq("networkId"),
        (InPacketAdded) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   */
  @Test
  public void testOnInPacketAddedPre() {

    /*
     * setting
     */
    InPacketAdded msg = Mockito.mock(InPacketAdded.class);

    /*
     * test
     */
    boolean result = target.onInPacketAddedPre("networkId", msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onInPacketAddedPost(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnInPacketAddedPost() {

    /*
     * setting
     */
    InPacketAdded msg = Mockito.mock(InPacketAdded.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onInPacketAddedPost("networkId", msg, respList);

    /*
     * check
     */
    verify(target).onInPacketAddedPost("networkId", msg, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnOutPacketAdded() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    OutPacketAdded msg = Mockito.mock(OutPacketAdded.class);

    /*
     * test
     */
    target.onOutPacketAdded("networkId", msg);

    /*
     * check
     */
    verify(target, times(1)).conversion(eq("networkId"),
        (OutPacketAdded) anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onOutPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   */
  @Test
  public void testOnOutPacketAddedPre() {

    /*
     * setting
     */
    OutPacketAdded msg = Mockito.mock(OutPacketAdded.class);

    /*
     * test
     */
    boolean result = target.onOutPacketAddedPre("networkId", msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onOutPacketAddedPost(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnOutPacketAddedPost() {

    /*
     * setting
     */
    OutPacketAdded msg = Mockito.mock(OutPacketAdded.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onOutPacketAddedPost("networkId", msg, respList);

    /*
     * check
     */
    verify(target).onOutPacketAddedPost("networkId", msg, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
   * .
   */
  @Test
  public void testOnNodeAdded() {

    /*
     * setting
     */
    Node node = Mockito.mock(Node.class);

    /*
     * test
     */
    target.onNodeAdded("networkId", node);

    /*
     * check
     */
    verify(target, times(1)).onNodeAddedPost(
        eq("networkId"),
        (Node) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeAddedPre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
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
    boolean result = target.onNodeAddedPre("networkId", node);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeAddedPost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnNodeAddedPost() {

    /*
     * setting
     */
    Node node = Mockito.mock(Node.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onNodeAddedPost("networkId", node, respList);

    /*
     * check
     */
    verify(target).onNodeAddedPost("networkId", node, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
   * .
   */
  @Test
  public void testOnPortAdded() {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);

    /*
     * test
     */
    target.onPortAdded("networkId", port);

    /*
     * check
     */
    verify(target, times(1)).onPortAddedPost(
        eq("networkId"),
        (Port) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortAddedPre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
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
    boolean result = target.onPortAddedPre("networkId", port);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortAddedPost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnPortAddedPost() {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onPortAddedPost("networkId", port, respList);

    /*
     * check
     */
    verify(target).onPortAddedPost("networkId", port, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public void testOnLinkAdded() {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);

    /*
     * test
     */
    target.onLinkAdded("networkId", link);

    /*
     * check
     */
    verify(target, times(1)).onLinkAddedPost(
        eq("networkId"),
        (Link) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkAddedPre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public void testOnLinkAddedPre() {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);

    /*
     * test
     */
    boolean result = target.onLinkAddedPre("networkId", link);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkAddedPost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnLinkAddedPost() {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onLinkAddedPost("networkId", link, respList);

    /*
     * check
     */
    verify(target).onLinkAddedPost("networkId", link, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
   * .
   */
  @Test
  public void testOnFlowAdded() {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);

    /*
     * test
     */
    target.onFlowAdded("networkId", flow);

    /*
     * check
     */
    verify(target, times(1)).onFlowAddedPost(
        eq("networkId"),
        (Flow) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
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
    boolean result = target.onFlowAddedPre("networkId", flow);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowAddedPost(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnFlowAddedPost() {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onFlowAddedPost("networkId", flow, respList);

    /*
     * check
     */
    verify(target).onFlowAddedPost("networkId", flow, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnNodeUpdate() throws Exception {

    /*
     * setting
     */
    Node prev = Mockito.mock(Node.class);
    Node curr = Mockito.mock(Node.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    target = createPowerSpy();

    HashMap<String, Response> mockResult = new HashMap<String, Response>();
    doReturn(mockResult).when(target).conversion(eq("networkId"),
        (Node) anyObject(),
        (Node) anyObject(), (ArrayList<String>) anyObject());

    /*
     * test
     */
    target.onNodeUpdate("networkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(target, atLeastOnce()).onNodeUpdatePost(
        eq("networkId"),
        eq(prev),
        eq(curr),
        (ArrayList<String>) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.ArrayList)}
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
    boolean result = target.onNodeUpdatePre("networkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeUpdatePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.ArrayList, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnNodeUpdatePost() {

    /*
     * setting
     */
    Node prev = Mockito.mock(Node.class);
    Node curr = Mockito.mock(Node.class);
    ArrayList<String> attributesList = new ArrayList<String>();
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onNodeUpdatePost("networkId", prev, curr, attributesList,
        respList);

    /*
     * check
     */
    verify(target).onNodeUpdatePost("networkId", prev, curr,
        attributesList, respList);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnPortUpdate() throws Exception {

    /*
     * setting
     */
    Port prev = Mockito.mock(Port.class);
    Port curr = Mockito.mock(Port.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    target = createPowerSpy();

    HashMap<String, Response> mockResult = new HashMap<String, Response>();
    // doReturn(mockResult).when(target).conversion(eq("networkId"), (Node)
    // anyObject(),
    // (Node) anyObject(), (ArrayList<String>) anyList());
    doReturn(mockResult).when(target).conversion(eq("networkId"),
        (Node) anyObject(),
        (Node) anyObject(), (ArrayList<String>) anyObject());

    /*
     * test
     */
    target.onPortUpdate("networkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(target, atLeastOnce()).onPortUpdatePost(eq("networkId"),
        eq(prev), eq(curr),
        (ArrayList<String>) anyObject(),
        (HashMap<String, Response>) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.ArrayList)}
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
    boolean result = target.onPortUpdatePre("networkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortUpdatePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.ArrayList, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnPortUpdatePost() {

    /*
     * setting
     */
    Port prev = Mockito.mock(Port.class);
    Port curr = Mockito.mock(Port.class);
    ArrayList<String> attributesList = new ArrayList<String>();
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onPortUpdatePost("networkId", prev, curr, attributesList,
        respList);

    /*
     * chcek
     */
    verify(target).onPortUpdatePost("networkId", prev, curr,
        attributesList, respList);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnLinkUpdate() throws Exception {

    /*
     * setting
     */
    Link prev = Mockito.mock(Link.class);
    Link curr = Mockito.mock(Link.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    target = createPowerSpy();

    HashMap<String, Response> mockResult = new HashMap<String, Response>();
    doReturn(mockResult).when(target).conversion(eq("networkId"),
        (Node) anyObject(),
        (Node) anyObject(), (ArrayList<String>) anyObject());

    /*
     * test
     */
    target.onLinkUpdate("networkId", prev, curr, attributesList);

    /*
     * check
     */
    verify(target, atLeastOnce()).onLinkUpdatePost(
        eq("networkId"),
        eq(prev),
        eq(curr),
        (ArrayList<String>) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.ArrayList)}
   * .
   */
  @Test
  public void testOnLinkUpdatePre() {

    /*
     * setting
     */
    Link prev = Mockito.mock(Link.class);
    Link curr = Mockito.mock(Link.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    boolean result = target.onLinkUpdatePre("networkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkUpdatePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.ArrayList, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnLinkUpdatePost() {

    /*
     * setting
     */
    Link prev = Mockito.mock(Link.class);
    Link curr = Mockito.mock(Link.class);
    ArrayList<String> attributesList = new ArrayList<String>();
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onLinkUpdatePost("networkId", prev, curr, attributesList,
        respList);

    /*
     * check
     */
    verify(target).onLinkUpdatePost("networkId", prev, curr,
        attributesList, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.ArrayList)}
   * .
   *
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnFlowUpdate() throws Exception {

    /*
     * setting
     */
    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    target = createPowerSpy();

    HashMap<String, Response> mockResult = new HashMap<String, Response>();
    doReturn(mockResult).when(target).conversion(eq("networkId"),
        (Node) anyObject(),
        (Node) anyObject(), (ArrayList<String>) anyObject());

    /*
     * test
     */
    target.onFlowUpdate("networkId", prev, curr, attributesList);

    verify(target, atLeastOnce()).onFlowUpdatePost(
        eq("networkId"),
        eq(prev),
        eq(curr),
        (ArrayList<String>) anyObject(),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.ArrayList)}
   * .
   */
  @Test
  public void testOnFlowUpdatePre() {

    /*
     * setting
     */
    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("networkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowUpdatePost(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.ArrayList, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnFlowUpdatePost() {

    /*
     * setting
     */
    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onFlowUpdatePost("networkId", prev, curr, attributesList,
        respList);

    /*
     * check
     */
    verify(target).onFlowUpdatePost("networkId", prev, curr,
        attributesList, respList);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnNodeDelete() throws Exception {

    /*
     * setting
     */
    Node node = Mockito.spy(new Node());

    target = createPowerSpy();

    HashMap<String, Response> mockRespResult = new HashMap<String, Response>();
    doReturn(mockRespResult).when(target).deleteConversion("networkId",
        node);

    /*
     * test
     */
    target.onNodeDelete("networkId", node);

    /*
     * check
     */
    verify(target, atLeastOnce()).onNodeDeletePost(
        eq("networkId"),
        eq(node),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
   * .
   */
  @Test
  public void testOnNodeDeletePre() {

    /*
     * setting
     */
    Node node = Mockito.mock(Node.class);

    /*
     * test
     */
    boolean result = target.onNodeDeletePre("networkId", node);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onNodeDeletePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnNodeDeletePost() {

    /*
     * setting
     */
    Node node = Mockito.mock(Node.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onNodeDeletePost("networkId", node, respList);

    /*
     * check
     */
    verify(target).onNodeDeletePost("networkId", node, respList);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnPortDelete() throws Exception {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);

    target = createPowerSpy();

    HashMap<String, Response> mockRespResult = new HashMap<String, Response>();
    doReturn(mockRespResult).when(target).deleteConversion("networkId",
        port);

    /*
     * test
     */
    target.onPortDelete("networkId", port);

    /*
     * check
     */
    verify(target, atLeastOnce()).onPortDeletePost(
        eq("networkId"),
        eq(port),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
   * .
   */
  @Test
  public void testOnPortDeletePre() {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);

    /*
     * test
     */
    boolean result = target.onPortDeletePre("networkId", port);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onPortDeletePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnPortDeletePost() {

    /*
     * setting
     */
    Port port = Mockito.mock(Port.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onPortDeletePost("networkId", port, respList);

    /*
     * check
     */
    verify(target).onPortDeletePost("networkId", port, respList);
    verifyNoMoreInteractions(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnLinkDelete() throws Exception {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);

    target = createPowerSpy();

    HashMap<String, Response> mockRespResult = new HashMap<String, Response>();
    doReturn(mockRespResult).when(target).deleteConversion("networkId",
        link);

    /*
     * test
     */
    target.onLinkDelete("networkId", link);

    /*
     * check
     */
    verify(target, atLeastOnce()).onLinkDeletePost(
        eq("networkId"),
        eq(link),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public void testOnLinkDeletePre() {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);

    /*
     * test
     */
    boolean result = target.onLinkDeletePre("networkId", link);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onLinkDeletePost(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnLinkDeletePost() {

    /*
     * setting
     */
    Link link = Mockito.mock(Link.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onLinkDeletePost("networkId", link, respList);

    /*
     * check
     */
    verify(target).onLinkDeletePost("networkId", link, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowDelete(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnFlowDelete() throws Exception {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);

    target = createPowerSpy();

    HashMap<String, Response> mockRespResult = new HashMap<String, Response>();
    doReturn(mockRespResult).when(target).deleteConversion("networkId",
        flow);

    /*
     * test
     */
    target.onFlowDelete("networkId", flow);

    /*
     * check
     */
    verify(target, atLeastOnce()).onFlowDeletePost(
        eq("networkId"),
        eq(flow),
        (HashMap<String, Response>) anyMapOf(String.class,
            Response.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowDeletePre(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
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
    boolean result = target.onFlowDeletePre("networkId", flow);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#onFlowDeletePost(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.HashMap)}
   * .
   */
  @Test
  public void testOnFlowDeletePost() {

    /*
     * setting
     */
    Flow flow = Mockito.mock(Flow.class);
    HashMap<String, Response> respList = new HashMap<String, Response>();

    /*
     * test
     */
    target.onFlowDeletePost("networkId", flow, respList);

    /*
     * check
     */
    verify(target).onFlowDeletePost("networkId", flow, respList);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testConversionStringNode() throws Exception {

    /*
     * setting
     */
    target = createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Node node = new Node();

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1", node);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2"), is(true));
    assertThat(result.containsKey("nwcId3"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testConversionStringPort() throws Exception {

    /*
     * setting
     */
    target = createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Port port = new Port("1", "PortId", "nwcId1");

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1", port);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2"), is(true));
    assertThat(result.containsKey("nwcId3"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public void testConversionStringLink() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Link link = new Link("LinkId", "nwcId1", "PortId1", "nwcId2", "PortId2");

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1", link);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2"), is(true));
    assertThat(result.containsKey("nwcId3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testConversionStringFlow() throws Exception {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Flow flow = new Flow("1", "FlowId1", "Owner", true, "Priority", "none",
        new HashMap<String, String>()) {

      @Override
      public String getType() {
        return "Type";
      }

      @Override
      public boolean validate() {
        return false;
      }

    };

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1", flow);

    /*
     * check
     */
    assertThat(result.size(), is(0));
    assertThat(result.containsKey("nwcId2"), is(false));
    assertThat(result.containsKey("nwcId3"), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   */
  @Test
  public void testConversionStringInPacketAdded() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryNode("nwcId1", "NodeId1", "nwcId2", "NodeId2");
    conversionTable.addEntryNode("nwcId2", "NodeId2", "nwcId3", "NodeId3");
    conversionTable.addEntryNode("nwcId3", "NodeId3", "nwcId1", "NodeId1");

    conversionTable.addEntryPort("nwcId1", "NodeId1", "PortId1", "nwcId2",
        "NodeId2", "PortId2");
    conversionTable.addEntryPort("nwcId1", "NodeId1", "PortId1", "nwcId2",
        "NodeId2", "PortId2");
    conversionTable.addEntryPort("nwcId2", "NodeId2", "PortId2", "nwcId3",
        "NodeId3", "PortId3");
    conversionTable.addEntryPort("nwcId3", "NodeId3", "PortId3", "nwcId1",
        "NodeId1", "PortId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface1 = Mockito.spy(new NetworkInterface(
        dispatcher, "nwcId1"));
    NetworkInterface networkInterface2 = Mockito.spy(new NetworkInterface(
        dispatcher, "nwcId2"));
    networkInterfaceMap.put("nwcId1", networkInterface1);
    networkInterfaceMap.put("nwcId2", networkInterface2);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Packet packet = new InPacket("PacketId", "NodeId1", "PortId1",
        "data".getBytes(),
        new HashMap<String, String>(), new BasicFlowMatch("NodeId1",
            "PortId1"));
    InPacketAdded inPacketAdded = new InPacketAdded(packet);

    doReturn(packet).when(target)
        .delInPacket(networkInterface1, "PacketId");

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1",
        inPacketAdded);

    /*
     * check
     */
    assertThat(result.size(), is(1));
    assertThat(result.containsKey("nwcId2"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   */
  @Test
  public void testConversionStringOutPacketAdded() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryNode("nwcId1", "NodeId1", "nwcId2", "NodeId2");
    conversionTable.addEntryNode("nwcId2", "NodeId2", "nwcId3", "NodeId3");
    conversionTable.addEntryNode("nwcId3", "NodeId3", "nwcId1", "NodeId1");

    conversionTable.addEntryPort("nwcId1", "NodeId1", "PortId1", "nwcId2",
        "NodeId2", "PortId2");
    conversionTable.addEntryPort("nwcId2", "NodeId2", "PortId2", "nwcId3",
        "NodeId3", "PortId3");
    conversionTable.addEntryPort("nwcId3", "NodeId3", "PortId3", "nwcId1",
        "NodeId1", "PortId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface1 = Mockito.spy(new NetworkInterface(
        dispatcher, "nwcId1"));
    NetworkInterface networkInterface2 = Mockito.spy(new NetworkInterface(
        dispatcher, "nwcId2"));
    networkInterfaceMap.put("nwcId1", networkInterface1);
    networkInterfaceMap.put("nwcId2", networkInterface2);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    List<String> ports = new ArrayList<String>(Arrays.asList("PortId1"));
    Packet packet = new OutPacket("PacketId", "NodeId1", ports,
        new ArrayList<String>(), "data".getBytes(),
        new HashMap<String, String>(), new BasicFlowMatch("NodeId1",
            "PortId1"));
    OutPacketAdded outPacketAdded = new OutPacketAdded(packet);

    doReturn(packet).when(target).delOutPacket(networkInterface1,
        "PacketId");

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1",
        outPacketAdded);

    /*
     * check
     */
    assertThat(result.size(), is(1));
    assertThat(result.containsKey("nwcId2"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node, org.o3project.odenos.component.network.topology.TopologyObject.Node, java.util.ArrayList)}
   * .
   */
  @Test
  public void testConversionStringNodeNodeArrayListOfString() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryNode("nwcId1", "NodeId1", "nwcId2", "NodeId2");
    conversionTable.addEntryNode("nwcId2", "NodeId2", "nwcId3", "NodeId3");
    conversionTable.addEntryNode("nwcId3", "NodeId3", "nwcId1", "NodeId1");

    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId2"));
    NetworkInterface networkInterface3 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId3"));
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    PowerMockito.doReturn(new Node("NodeId2")).when(networkInterface2)
        .getNode("NodeId2");
    PowerMockito.doReturn(new Node("NodeId3")).when(networkInterface3)
        .getNode("NodeId3");

    Map<String, String> attributesPrev = new HashMap<>();
    attributesPrev.put("Key1", "Value1");
    Node prev = new Node("1", "NodeId1", new HashMap<String, Port>(),
        attributesPrev);
    Map<String, String> attributesCurr = new HashMap<>();
    attributesCurr.put("Key1", "Value11");
    Node curr = new Node("1", "NodeId1", new HashMap<String, Port>(),
        attributesCurr);
    ArrayList<String> attributesList = new ArrayList<>();

    /*
     * test
     */
    HashMap<String, Response> result = target.conversion("nwcId1", prev,
        curr, attributesList);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::NodeId2"), is(true));
    assertThat(result.containsKey("nwcId3::NodeId3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port, org.o3project.odenos.component.network.topology.TopologyObject.Port, java.util.ArrayList)}
   * .
   */
  @Test
  public void testConversionStringPortPortArrayListOfString() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryPort("nwcId1", "NodeId1", "PortId1", "nwcId2",
        "NodeId2", "PortId2");
    conversionTable.addEntryPort("nwcId2", "NodeId2", "PortId2", "nwcId3",
        "NodeId3", "PortId3");
    conversionTable.addEntryPort("nwcId3", "NodeId3", "PortId3", "nwcId1",
        "NodeId1", "PortId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId2"));
    NetworkInterface networkInterface3 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId3"));
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    PowerMockito.doReturn(new Port("PortId2")).when(networkInterface2)
        .getPort("NodeId2", "PortId2");
    PowerMockito.doReturn(new Port("PortId3")).when(networkInterface3)
        .getPort("NodeId3", "PortId3");

    Map<String, String> attributesPrev = new HashMap<>();
    attributesPrev.put("Key1", "Value1");
    Port prev = new Port("1", "PortId1", "NodeId1", "OutLink", "InLink",
        attributesPrev);
    Map<String, String> attributesCurr = new HashMap<>();
    attributesCurr.put("Key1", "Value11");
    Port curr = new Port("1", "PortId1", "NodeId1", "OutLink", "InLink",
        attributesCurr);

    /*
     * test
     */
    ArrayList<String> attributesList = new ArrayList<>();
    HashMap<String, Response> result = target.conversion("nwcId1", prev,
        curr, attributesList);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::NodeId2::PortId2"), is(true));
    assertThat(result.containsKey("nwcId3::NodeId3::PortId3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link, org.o3project.odenos.component.network.topology.TopologyObject.Link, java.util.ArrayList)}
   * .
   */
  @Test
  public void testConversionStringLinkLinkArrayListOfString() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryLink("nwcId1", "LinkId1", "nwcId2", "LinkId2");
    conversionTable.addEntryLink("nwcId2", "LinkId2", "nwcId3", "LinkId3");
    conversionTable.addEntryLink("nwcId3", "LinkId3", "nwcId1", "LinkId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId2"));
    NetworkInterface networkInterface3 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId3"));
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    PowerMockito.doReturn(new Link("LinkId2")).when(networkInterface2)
        .getLink("LinkId2");
    PowerMockito.doReturn(new Link("LinkId3")).when(networkInterface3)
        .getLink("LinkId3");

    Map<String, String> attributesPrev = new HashMap<>();
    attributesPrev.put("Key1", "Value1");
    Link prev = new Link("1", "LinkId1", "NodeId1", "PortId1", "NodeId2",
        "PortId2", attributesPrev);
    Map<String, String> attributesCurr = new HashMap<>();
    attributesCurr.put("Key1", "Value11");
    Link curr = new Link("1", "LinkId1", "NodeId1", "PortId1", "NodeId2",
        "PortId2", attributesCurr);

    /*
     * test
     */
    ArrayList<String> attributesList = new ArrayList<>();
    HashMap<String, Response> result = target.conversion("nwcId1", prev,
        curr, attributesList);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::LinkId2"), is(true));
    assertThat(result.containsKey("nwcId3::LinkId3"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#conversion(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, org.o3project.odenos.core.component.network.flow.FlowObject.Flow, java.util.ArrayList)}
   * .
   */
  @Test
  public void testConversionStringFlowFlowArrayListOfString() {

    /*
     * setting
     */
    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryFlow("nwcId1", "FlowId1", "nwcId2", "FlowId2");
    conversionTable.addEntryFlow("nwcId2", "FlowId2", "nwcId3", "FlowId3");
    conversionTable.addEntryFlow("nwcId3", "FlowId3", "nwcId1", "FlowId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId2"));
    NetworkInterface networkInterface3 = PowerMockito
        .spy(new NetworkInterface(dispatcher, "nwcId3"));
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    PowerMockito.doReturn(new BasicFlow("FlowId2")).when(networkInterface2)
        .getFlow("FlowId2");
    PowerMockito.doReturn(new BasicFlow("FlowId3")).when(networkInterface3)
        .getFlow("FlowId3");

    Map<String, String> attributesPrev = new HashMap<>();
    attributesPrev.put("Key1", "Value1");
    Flow prev = new Flow("1", "FlowId1", "Owner", true, "Priority", "none",
        attributesPrev);
    Map<String, String> attributesCurr = new HashMap<>();
    attributesCurr.put("Key1", "Value11");
    Flow curr = new Flow("1", "FlowId1", "Owner", true, "Priority", "none",
        attributesCurr);

    /*
     * test
     */
    ArrayList<String> attributesList = new ArrayList<>();
    HashMap<String, Response> result = target.conversion("nwcId1", prev,
        curr, attributesList);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::FlowId2"), is(true));
    assertThat(result.containsKey("nwcId3::FlowId3"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#deleteConversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Node)}
   * .
   */
  @Test
  public void testDeleteConversionStringNode() {

    /*
     * setting
     */
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryNode("nwcId1", "NodeId1", "nwcId2", "NodeId2");
    conversionTable.addEntryNode("nwcId2", "NodeId2", "nwcId3", "NodeId3");
    conversionTable.addEntryNode("nwcId3", "NodeId3", "nwcId1", "NodeId1");

    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Node node = new Node("NodeId1");

    /*
     * test
     */
    HashMap<String, Response> result = target.deleteConversion("nwcId1",
        node);

    /*
     * check
     */
    verify(conversionTable).delEntryNode("nwcId1", "NodeId1");

    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::NodeId2"), is(true));
    assertThat(result.containsKey("nwcId3::NodeId3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#deleteConversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Port)}
   * .
   */
  @Test
  public void testDeleteConversionStringPort() {

    /*
     * setting
     */
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryPort("nwcId1", "NodeId1", "PortId1", "nwcId2",
        "NodeId2", "PortId2");
    conversionTable.addEntryPort("nwcId2", "NodeId2", "PortId2", "nwcId3",
        "NodeId3", "PortId3");
    conversionTable.addEntryPort("nwcId3", "NodeId3", "PortId3", "nwcId1",
        "NodeId1", "PortId1");

    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Port port = new Port("PortId1", "NodeId1");

    /*
     * test
     */
    HashMap<String, Response> result = target.deleteConversion("nwcId1",
        port);

    /*
     * check
     */
    verify(conversionTable).delEntryPort("nwcId1", "NodeId1", "PortId1");

    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::NodeId2::PortId2"), is(true));
    assertThat(result.containsKey("nwcId3::NodeId3::PortId3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#deleteConversion(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public void testDeleteConversionStringLink() {

    /*
     * setting
     */
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryLink("nwcId1", "LinkId1", "nwcId2", "LinkId2");
    conversionTable.addEntryLink("nwcId2", "LinkId2", "nwcId3", "LinkId3");
    conversionTable.addEntryLink("nwcId3", "LinkId3", "nwcId1", "LinkId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Link link = new Link("LinkId1", "nwcId1", "PortId1", "nwcId2",
        "PortId2");

    /*
     * test
     */
    HashMap<String, Response> result = target.deleteConversion("nwcId1",
        link);

    /*
     * check
     */
    verify(conversionTable).delEntryLink("nwcId1", "LinkId1");

    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::LinkId2"), is(true));
    assertThat(result.containsKey("nwcId3::LinkId3"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Logic#deleteConversion(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.Flow)}
   * .
   */
  @Test
  public void testDeleteConversionStringFlow() {

    /*
     * setting
     */
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryNetwork("nwcId1", "nwcId2");
    conversionTable.addEntryNetwork("nwcId2", "nwcId3");
    conversionTable.addEntryNetwork("nwcId3", "nwcId1");

    conversionTable.addEntryFlow("nwcId1", "FlowId1", "nwcId2", "FlowId2");
    conversionTable.addEntryFlow("nwcId2", "FlowId2", "nwcId3", "FlowId3");
    conversionTable.addEntryFlow("nwcId3", "FlowId3", "nwcId1", "FlowId1");
    Whitebox.setInternalState(target, "conversionTable", conversionTable);

    Map<String, NetworkInterface> networkInterfaceMap = new HashMap<>();
    NetworkInterface networkInterface1 = new NetworkInterface(dispatcher,
        "nwcId1");
    NetworkInterface networkInterface2 = new NetworkInterface(dispatcher,
        "nwcId2");
    NetworkInterface networkInterface3 = new NetworkInterface(dispatcher,
        "nwcId3");
    networkInterfaceMap.put("nwcId1", networkInterface1);
    networkInterfaceMap.put("nwcId2", networkInterface2);
    networkInterfaceMap.put("nwcId3", networkInterface3);
    Whitebox.setInternalState(target, "networkInterfaces",
        networkInterfaceMap);

    Flow flow = new Flow("1", "FlowId1", "Owner", true, "Priority", "none",
        new HashMap<String, String>()) {

      @Override
      public String getType() {
        return "Type";
      }

      @Override
      public boolean validate() {
        return false;
      }

    };

    /*
     * test
     */
    HashMap<String, Response> result = target.deleteConversion("nwcId1",
        flow);

    /*
     * check
     */
    verify(conversionTable).delEntryFlow("nwcId1", "FlowId1");

    assertThat(result.size(), is(2));
    assertThat(result.containsKey("nwcId2::FlowId2"), is(true));
    assertThat(result.containsKey("nwcId3::FlowId3"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Logic#onEventComponentConnection(ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnEventComponentConnection_Add() throws Exception {

    /*
     * setting
     */
    ComponentConnection prev = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    ComponentConnection curr = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    curr.setProperty("network_id", "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        Logic.CONN_ADD, prev, curr);

    /*
     * test
     */
    Whitebox.invokeMethod(target, "onEventComponentConnection", message);

    /*
     * check
     */
    verify(target).onConnectionChangedAdded(message);

    Map<String, NetworkInterface> resultNetworks = Whitebox
        .getInternalState(target, "networkInterfaces");
    assertThat(resultNetworks.containsKey("NetworkId"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Logic#onEventComponentConnection(ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnEventComponentConnection_Update() throws Exception {

    /*
     * setting
     */
    ComponentConnection prev = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    ComponentConnection curr = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    curr.setProperty("network_id", "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        Logic.CONN_UPDATE, prev, curr);

    /*
     * test
     */
    Whitebox.invokeMethod(target, "onEventComponentConnection", message);

    /*
     * check
     */
    verify(target).onConnectionChangedUpdate(message);
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Logic#onEventComponentConnection(ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnEventComponentConnection_Delete() throws Exception {

    /*
     * setting
     */
    ComponentConnection prev = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    ComponentConnection curr = new ComponentConnection("ObjectId",
        "connectionType", "connectionState");
    curr.setProperty("network_id", "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        Logic.CONN_DELETE, prev, curr);

    /*
     * test
     */
    Whitebox.invokeMethod(target, "onEventComponentConnection", message);

    /*
     * check
     */
    verify(target).onConnectionChangedDelete(message);

    Map<String, NetworkInterface> resultNetworks = Whitebox
        .getInternalState(target, "networkInterfaces");
    assertThat(resultNetworks.containsKey("NetworkId"), is(false));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.Logic#getIgnoreKeys(ArrayList<String>, ArrayList<String>)}.
   * @throws Exception
   */
  @Test
  public void testGetIgnoreKeys() throws Exception {

    /*
     * setting
     */
    ArrayList<String> allKeys = new ArrayList<String>(Arrays.asList(
        "type", "version", "flow_id", "owner", "enabled", "priority",
        "status"
        ));

    ArrayList<String> updateKeys = new ArrayList<String>(Arrays.asList(
        "flow_id", "owner", "enabled", "priority", "status"
        ));

    /*
     * test
     */
    ArrayList<String> result = Whitebox.invokeMethod(target,
        "getIgnoreKeys", allKeys, updateKeys);

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.contains("type"), is(true));
    assertThat(result.contains("version"), is(true));

  }
}
