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
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.event.BaseObjectChanged;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test class for DummyDriver.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DummyDriver.class, BaseObjectChanged.class, ComponentConnectionChanged.class,
    ComponentConnection.class, OutPacketAdded.class })
@PowerMockIgnore({"javax.management.*"})
public class DummyDriverTest {

  private DummyDriver target;
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

    target = PowerMockito.spy(new DummyDriver("objectId", dispatcher));
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    dispatcher = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#DummyDriver(java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDummyDriver_null() throws Exception {

    /*
     * test
     */
    target = PowerMockito.spy(new DummyDriver(null, null));

    /*
     * check
     */
    assertThat(target, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#getDescription()}.
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
    assertThat(result, containsString("dummy driver"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#getSuperType()}.
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
    assertThat(result, containsString("Driver"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public void testOnConnectionChangedAddedPre() {

    /*
     * setting
     */
    ComponentConnection prev = Mockito.mock(ComponentConnection.class);
    ComponentConnection curr =
        Mockito.spy(new ComponentConnection("objectId", "LogicAndNetwork",
            "connectionType", "connectionState"));
    curr.setProperty("logic_id", "objectId");

    ComponentConnectionChanged ccc =
        PowerMockito.spy(new ComponentConnectionChanged("action", prev, curr));

    /*
     * test & check
     */
    assertThat(target.onConnectionChangedAddedPre(ccc), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedAdded() throws Exception {

    /*
     * setting
     */
    PowerMockito.doNothing().when(target, "subscribeNetworkComponent");

    ComponentConnection prev = Mockito.mock(ComponentConnection.class);
    ComponentConnection curr = Mockito
        .spy(new ComponentConnection("objectId", "connectionType", "connectionState"));
    ComponentConnectionChanged ccc = new ComponentConnectionChanged("action", prev, curr);

    /*
     * test
     */
    target.onConnectionChangedAdded(ccc);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("subscribeNetworkComponent");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnConnectionChangedDelete() throws Exception {

    /*
     * setting
     */
    PowerMockito.doNothing().when(target, "unsubscribeNetworkComponent");

    /*
     * test
     */
    ComponentConnectionChanged ccc = new ComponentConnectionChanged(
        "dummy_action",
        new ComponentConnectionLogicAndNetwork("a", "b", "c", "d", "e"),
        new ComponentConnectionLogicAndNetwork("a", "b", "c", "d", "e"));
    target.onConnectionChangedDelete(ccc);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("unsubscribeNetworkComponent");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public void testOnFlowAdded() {

    /*
     * setting
     */
    Flow flow = Mockito.spy(new Flow("NetworkId"));

    HashMap<String, NetworkInterface> networkIfs = new HashMap<String, NetworkInterface>();
    networkIfs.put("NetworkId", new NetworkInterface(dispatcher, "NetworkId"));

    Whitebox.setInternalState(target, "networkInterfaces", networkIfs);

    doReturn("establishing").when(flow).getStatus();

    /*
     * test
     */
    target.onFlowAdded("networkId", flow);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public void testOnFlowUpdate() {

    /*
     * setting
     */
    Flow prev = Mockito.mock(Flow.class);
    Flow curr = Mockito.mock(Flow.class);
    ArrayList<String> attributesList = new ArrayList<String>();

    doNothing().when(target).onFlowAdded("networkId", curr);

    /*
     * test
     */
    target.onFlowUpdate("networkId", prev, curr, attributesList);

    /*
     * check
     */
    // #TODO
    //verify(target, times(1)).onFlowAdded("networkId", curr);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnOutPacketAdded() throws Exception {

    /*
     * setting
     */
    OutPacketAdded outPacketAdded = PowerMockito.spy(new OutPacketAdded());
    PowerMockito.doReturn("packetId").when(outPacketAdded).getId();

    NetworkInterface netif = PowerMockito.spy(new NetworkInterface(dispatcher, "networkId"));
    Response response = new Response(Response.OK, new Object());
    PowerMockito.doReturn(response).when(netif).delOutPacket("packetId");

    HashMap<String, NetworkInterface> netMap = new HashMap<String, NetworkInterface>();
    netMap.put("networkId", new NetworkInterface(dispatcher, "networkId"));

    PowerMockito.doReturn(netMap).when(target).networkInterfaces();

    /*
     * test
     */
    target.onOutPacketAdded("networkId", outPacketAdded);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.DummyDriver#onOutPacketAdded(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnOutPacketAdded_networkinterfaceEmpty() throws Exception {

    /*
     * setting
     */
    OutPacketAdded outPacketAdded = PowerMockito.spy(new OutPacketAdded());

    HashMap<String, NetworkInterface> netif = new HashMap<String, NetworkInterface>();
    doReturn(netif).when(target).networkInterfaces();

    /*
     * test
     */
    target.onOutPacketAdded("networkId", outPacketAdded);

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.DummyDriver#subscribeNetworkComponent()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSubscribeNetworkComponent() throws Exception {

    /*
     * test
     */
    Whitebox.invokeMethod(target, "subscribeNetworkComponent");

    /*
     * check
     */
    verify(target).addEntryEventSubscription(eq(Logic.FLOW_CHANGED), anyString());
    verify(target).addEntryEventSubscription(eq(Logic.OUT_PACKET_ADDED), anyString());

    ArrayList<String> attr = null;
    verify(target).updateEntryEventSubscription(eq(Logic.FLOW_CHANGED), anyString(), eq(attr));

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke("applyEventSubscription");
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.DummyDriver#unsubscribeNetworkComponent()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testUnsubscribeNetworkComponent() throws Exception {

    /*
     * test
     */
    Whitebox.invokeMethod(target, "unsubscribeNetworkComponent");

    /*
     * check
     */
    verify(target).removeEntryEventSubscription(eq(Logic.FLOW_CHANGED), anyString());
    verify(target).removeEntryEventSubscription(eq(Logic.OUT_PACKET_ADDED), anyString());

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke("applyEventSubscription");
  }

}
