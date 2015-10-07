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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test class for LinkLayerizer.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ LinkLayerizer.class, ConversionTable.class,
    NetworkInterface.class, SystemManagerInterface.class,
    ComponentConnection.class })
@PowerMockIgnore({"javax.management.*"})
public class LinkLayerizerTest {

  private MessageDispatcher dispatcher;

  private LinkLayerizer target;

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

    target = Mockito.spy(new LinkLayerizer("ObjectId", dispatcher));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    dispatcher = null;

  }

  private LinkLayerizer createPowerSpy() throws Exception {

    target = PowerMockito.spy(new LinkLayerizer("ObjectId", dispatcher));

    return target;

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#LinkLayerizer(java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}.
   * @throws Exception
   */
  @Test
  public void testLinkLayerizer() throws Exception {

    /*
     * test
     */
    LinkLayerizer result = new LinkLayerizer("NewObjectId", dispatcher);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    LinkLayerizerBoundaryTable resultTable = Whitebox.getInternalState(
        result, "linkLayerizerBoundaryTable");
    assertThat(resultTable, is(notNullValue()));

    LinkLayerizerOnFlow resultLinklayerOnFlow = Whitebox
        .getInternalState(result, "linkLayerizerOnFlow");
    assertThat(resultLinklayerOnFlow, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getSuperType()}.
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
    assertThat(result, is("LinkLayerizer"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getDescription()}.
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
    assertThat(result, is("LinkLayerizer Component"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getConnectionTypes()}.
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
    assertThat(result, is("layerized:1,upper:1,lower:1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isUpperLinkisync()}.
   */
  @Test
  public void testIsUpperLinkisync() {

    /*
     * test
     */
    boolean result = target.isUpperLinkisync();

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#setUpperLinkisync(boolean)}.
   */
  @Test
  public void testSetUpperLinkisync() {

    /*
     * test
     */
    target.setUpperLinkisync(false);

    /*
     * check
     */
    assertThat(target.isUpperLinkisync(), is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAddedPre_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "lower", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAddedPre_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "upper", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAddedPre_layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "layerized", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedAddedPre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedUpdatePre() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "lower", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedUpdatePre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDeletePre_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("NetworkId", "lower");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "lower", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedDeletePre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDeletePre_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("NetworkId", "upper");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "upper", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedDeletePre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDeletePre_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("NetworkId", "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "layerized", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    boolean result = target.onConnectionChangedDeletePre(message);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded_lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("NetworkId", "lower");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");

    PowerMockito.doReturn(null).when(systemIf)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "lower", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedAdded(message);

    verify(systemIf).putConnection(curr);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("NetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");

    PowerMockito.doReturn(null).when(systemIf)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "upper", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    PowerMockito.doNothing().when(target,
        "doOnConnectionChangedAddedUpper", "NetworkId");

    /*
     * test
     */
    target.onConnectionChangedAdded(message);

    /*
     * check
     */
    //verify(target).subscribeUpper("NetworkId");
    verify(conversionTable).addEntryNetwork("LayerizedNetworkId",
        "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "doOnConnectionChangedAddedUpper", "NetworkId");

    verify(systemIf).putConnection(curr);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedAdded_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("NetworkId", "layerized");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");

    PowerMockito.doReturn(null).when(systemIf)
        .putConnection((ComponentConnection) anyObject());

    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "layerized", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    PowerMockito.doNothing().when(target,
        "doOnConnectionChangedAddedLayerized", "NetworkId");

    /*
     * test
     */
    target.onConnectionChangedAdded(message);

    /*
     * check
     */
    //verify(target).subscribeLayerized("NetworkId");
    verify(conversionTable).addEntryNetwork("UpperNetworkId", "NetworkId");
    PowerMockito.verifyPrivate(target).invoke(
        "doOnConnectionChangedAddedLayerized", "NetworkId");

    verify(systemIf).putConnection(curr);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedUpdate(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   */
  @Test
  public void testOnConnectionChangedUpdate() {

    /*
     * setting
     */
    ComponentConnection curr = new ComponentConnectionLogicAndNetwork(
        "ObjectId", "lower", "ConnectionState", "ObjectId",
        "NetworkId");
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    /*
     * test
     */
    target.onConnectionChangedUpdate(message);

    /*
     * check
     */
    verify(target).onConnectionChangedUpdate(message);
    verifyNoMoreInteractions(target);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");
    doReturn(null).when(systemIf).putConnection(
        (ComponentConnection) anyObject());

    ComponentConnection curr = PowerMockito
        .spy(new ComponentConnectionLogicAndNetwork(
            "ObjectId", "lower", "ConnectionState", "ObjectId",
            "NetworkId"));
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    PowerMockito.doNothing().when(target,
        "doOnConnectionChangedDeleteLower", "NetworkId");

    /*
     * test
     */
    target.onConnectionChangedDelete(message);

    /*
     * check
     */
    verify(target, atLeastOnce()).onConnectionChangedDelete(message);
    PowerMockito.verifyPrivate(target).invoke(
        "doOnConnectionChangedDeleteLower", "NetworkId");

    assertThat(curr.getObjectState(), is(ComponentConnection.State.NONE));

    verify(conversionTable).delEntryConnectionType("NetworkId");
    verify(conversionTable).delEntryNetwork("NetworkId");

    InOrder inOrder = Mockito.inOrder(curr, systemIf);
    inOrder.verify(curr).setConnectionState(
        ComponentConnection.State.FINALIZING);
    inOrder.verify(systemIf).putConnection(curr);
    inOrder.verify(curr)
        .setConnectionState(ComponentConnection.State.NONE);
    inOrder.verify(systemIf).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");
    doReturn(null).when(systemIf).putConnection(
        (ComponentConnection) anyObject());

    ComponentConnection curr = PowerMockito
        .spy(new ComponentConnectionLogicAndNetwork(
            "ObjectId", "upper", "ConnectionState", "ObjectId",
            "NetworkId"));
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    PowerMockito.doNothing().when(target,
        "doOnConnectionChangedDeleteUpper", "NetworkId");

    /*
     * test
     */
    target.onConnectionChangedDelete(message);

    /*
     * check
     */
    verify(target, atLeastOnce()).onConnectionChangedDelete(message);
    PowerMockito.verifyPrivate(target).invoke(
        "doOnConnectionChangedDeleteUpper", "NetworkId");

    assertThat(curr.getObjectState(), is(ComponentConnection.State.NONE));

    verify(conversionTable).delEntryConnectionType("NetworkId");
    verify(conversionTable).delEntryNetwork("NetworkId");

    InOrder inOrder = Mockito.inOrder(curr, systemIf);
    inOrder.verify(curr).setConnectionState(
        ComponentConnection.State.FINALIZING);
    inOrder.verify(systemIf).putConnection(curr);
    inOrder.verify(curr)
        .setConnectionState(ComponentConnection.State.NONE);
    inOrder.verify(systemIf).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}.
   * @throws Exception
   */
  @Test
  public void testOnConnectionChangedDelete_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    SystemManagerInterface systemIf = PowerMockito
        .mock(SystemManagerInterface.class);
    PowerMockito.doReturn(systemIf).when(target, "systemMngInterface");
    doReturn(null).when(systemIf).putConnection(
        (ComponentConnection) anyObject());

    ComponentConnection curr = PowerMockito
        .spy(new ComponentConnectionLogicAndNetwork(
            "ObjectId", "layerized", "ConnectionState", "ObjectId",
            "NetworkId"));
    ComponentConnectionChanged message = new ComponentConnectionChanged(
        "Action", null, curr);

    PowerMockito.doNothing().when(target,
        "doOnConnectionChangedDeleteLayerized", "NetworkId");

    /*
     * test
     */
    target.onConnectionChangedDelete(message);

    /*
     * check
     */
    verify(target, atLeastOnce()).onConnectionChangedDelete(message);
    PowerMockito.verifyPrivate(target).invoke(
        "doOnConnectionChangedDeleteLayerized", "NetworkId");

    assertThat(curr.getObjectState(), is(ComponentConnection.State.NONE));

    verify(conversionTable).delEntryConnectionType("NetworkId");
    verify(conversionTable).delEntryNetwork("NetworkId");

    InOrder inOrder = Mockito.inOrder(curr, systemIf);
    inOrder.verify(curr).setConnectionState(
        ComponentConnection.State.FINALIZING);
    inOrder.verify(systemIf).putConnection(curr);
    inOrder.verify(curr)
        .setConnectionState(ComponentConnection.State.NONE);
    inOrder.verify(systemIf).putConnection(curr);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#subscribeLower(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSubscribeLower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.subscribeLower("LowerId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "FLOW_CHANGED", "LowerId");

    ArrayList<String> attributes = new ArrayList<String>();
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "updateEntryEventSubscription", "FLOW_CHANGED", "LowerId",
        attributes);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#subscribeUpper(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSubscribeUpper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.subscribeUpper("UpperId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "NODE_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "PORT_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "LINK_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "FLOW_CHANGED", "UpperId");

    ArrayList<String> nodeAttributes = new ArrayList<String>(
        Arrays.asList(
            "attributes::oper_status",
            "attributes::physical_id",
            "attributes::vendor")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "NODE_CHANGED", "UpperId",
        nodeAttributes);

    ArrayList<String> portAttributes = new ArrayList<String>(
        Arrays.asList(
            "attributes::oper_status",
            "attributes::max_bandwidth",
            "attributes::physical_id",
            "attributes::vendor")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "PORT_CHANGED", "UpperId",
        portAttributes);

    ArrayList<String> linkAttributes = new ArrayList<String>(
        Arrays.asList(
            "attributes::oper_status",
            "attributes::latency",
            "attributes::max_bandwidth")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "LINK_CHANGED", "UpperId",
        linkAttributes);
    ArrayList<String> flowAttributes = new ArrayList<String>(
        Arrays.asList(
            "status",
            "attributes::req_bandwidth",
            "attributes::req_latency")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "FLOW_CHANGED", "UpperId",
        flowAttributes);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#subscribeLayerized(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSubscribeLayerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.subscribeLayerized("LayerizedId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "LINK_CHANGED", "LayerizedId");
    PowerMockito.verifyPrivate(target).invoke("addEntryEventSubscription",
        "FLOW_CHANGED", "LayerizedId");

    ArrayList<String> portAttributes = new ArrayList<String>(
        Arrays.asList(
            "attributes::unreserved_bandwidth",
            "attributes::is_boundary")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "PORT_CHANGED", "LayerizedId",
        portAttributes);

    ArrayList<String> linkAttributes = new ArrayList<String>(
        Arrays.asList(
            "attributes::cost",
            "attributes::req_latency",
            "attributes::unreserved_bandwidth",
            "attributes::req_bandwidth")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "LINK_CHANGED", "LayerizedId",
        linkAttributes);
    ArrayList<String> flowAttributes = new ArrayList<String>(
        Arrays.asList(
            "owner",
            "enabled",
            "priority",
            "attributes::bandwidth",
            "attributes::latency")
        );
    PowerMockito.verifyPrivate(target).invoke(
        "updateEntryEventSubscription", "FLOW_CHANGED", "LayerizedId",
        flowAttributes);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#unsubscribeLower(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsubscribeLower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.unsubscribeLower("LowerId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "LowerId");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#unsubscribeUpper(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsubscribeUpper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.unsubscribeUpper("UpperId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "NODE_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "PORT_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "LINK_CHANGED", "UpperId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "UpperId");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#unsubscribeLayerized(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsubscribeLayerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /*
     * test
     */
    target.unsubscribeLayerized("LayerizedId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "NODE_CHANGED", "LayerizedId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "PORT_CHANGED", "LayerizedId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "LINK_CHANGED", "LayerizedId");
    PowerMockito.verifyPrivate(target).invoke(
        "removeEntryEventSubscription", "FLOW_CHANGED", "LayerizedId");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   */
  @Test
  public void testOnRequest() {

    /*
     * setting
     */
    Request request = new Request("ObjectId", Method.GET,
        "settings/boundaries", "txid",
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
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onLinkAddedPre(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}.
   * @throws Exception
   */
  @Test
  public void testOnLinkAddedPre() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.setUpperLinkisync(false); // upperLinkSync
    
    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher, "LowerNetworkId");
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    Link link = new Link("LinkId");

    /*
     * test
     */
    boolean resultLower = target.onLinkAddedPre("LowerNetworkId", link);
    boolean resultUpper = target.onLinkAddedPre("UpperNetworkId", link);
    boolean resultLayerized = target.onLinkAddedPre("LayerizedNetworkId",
        link);

    /*
     * check
     */
    assertThat(resultLower, is(true));
    assertThat(resultUpper, is(true));
    assertThat(resultLayerized, is(false)); // upperLinkSync

  }

  
  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onLinkAddedPre(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}.
   * @throws Exception
   */
  @Test
  public void testOnLinkAddedPreSyncFlow() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito.spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId", "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.setUpperLinkisync(false); // upperLinkSync

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher, "LowerNetworkId");
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");
    
    /* Links */
    Link link = new Link("LinkId", "SrcNode", "SrcPort", "DstNode", "DstPort");

    Link link1_1 = new Link("LinkId1_1",
        "SrcNode", "SrcPort", "DstNode", "DstPort");
    Link link2 = new Link("LinkId2",
        "SrcNode2", "SrcPort2", "DstNode2", "DstPort2");
    Map<String, Link> links = new HashMap<>();
    links.put(link1_1.getId(), link1_1);
    links.put(link2.getId(), link2);
    PowerMockito.doReturn(links).when(layerizedNetIf).getLinks();


    FlowSet layerizedFlows = new FlowSet();
    /* set flow */
    List<BasicFlowMatch> matches = new ArrayList<>();
    BasicFlowMatch match = new BasicFlowMatch("InNode", "InPort");
    matches.add(match);
    List<String> path = new ArrayList<>(Arrays.asList("LinkId1_1", "LinkId2"));
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none", matches, path, edgeActions, flowAttributes);
    layerizedFlows.getFlows().put(flow.getFlowId(), flow);

    PowerMockito.doReturn(layerizedFlows).when(layerizedNetIf).getFlowSet();

    /* LinkLayerizerOnFlow */
    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    PowerMockito.doReturn(new HashMap<>()).when(upperNetIf).getLinks();

    /*
     * test
     */
    boolean resultUpper = target.onLinkAddedPre("UpperNetworkId", link);


    /*
     * check
     */
    assertThat(resultUpper, is(false));

  }
  
  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onLinkUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Link, org.o3project.odenos.core.component.network.topology.Link, java.util.ArrayList)}.
   * @throws Exception
   */
  @Test
  public void testOnLinkUpdatePre() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.setUpperLinkisync(false); // upperLinkSync

    Link prev = new Link("prev");
    Link curr = new Link("curr");
    ArrayList<String> attributes = new ArrayList<>();

    /*
     * test
     */
    boolean resultLower = target.onLinkUpdatePre("LowerNetworkId",
        prev, curr, attributes);
    boolean resultUpper = target.onLinkUpdatePre("UpperNetworkId",
        prev, curr, attributes);
    boolean resultLayerized = target.onLinkUpdatePre("LayerizedNetworkId",
        prev, curr, attributes);

    /*
     * check
     */
    assertThat(resultLower, is(true));
    assertThat(resultUpper, is(true));
    assertThat(resultLayerized, is(false)); // upperLinkSync

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onLinkDeletePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Link)}.
   * @throws Exception
   */
  @Test
  public void testOnLinkDeletePre() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.setUpperLinkisync(false); // upperLinkSync

    Link link = new Link("LinkId");

    /*
     * test
     */
    boolean resultLower = target.onLinkDeletePre("LowerNetworkId", link);
    boolean resultUpper = target.onLinkDeletePre("UpperNetworkId", link);
    boolean resultLayerized = target.onLinkDeletePre("LayerizedNetworkId",
        link);

    /*
     * check
     */
    assertThat(resultLower, is(true));
    assertThat(resultUpper, is(true));
    assertThat(resultLayerized, is(false)); // upperLinkSync

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowAddedPre_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface upperNetIf = new NetworkInterface(dispatcher,
        "UpperNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>();
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("LowerNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

    verify(onFlow).flowAddedLowerNw("LowerNetworkId", settingFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowAddedPre_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface upperNetIf = new NetworkInterface(dispatcher,
        "UpperNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>(Arrays.asList("path"));
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("UpperNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

    verifyZeroInteractions(onFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowAddedPre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowAddedPre_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>(Arrays.asList("path"));
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowAddedPre("LayerizedNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

    verify(onFlow).flowAddedLayerizedNwExistPath("LayerizedNetworkId",
        settingFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnFlowUpdatePre_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>(Arrays.asList("path"));
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowUpdateLowerNw(anyString(),
        (BasicFlow) anyObject(), (List<String>) anyList());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    Flow prev = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    Flow curr = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    ArrayList<String> attributesList = new ArrayList<>();

    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("LowerNetworkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(false));

    verify(onFlow).flowUpdateLowerNw("LowerNetworkId", settingFlow,
        attributesList);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnFlowUpdatePre_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface upperNetIf = new NetworkInterface(dispatcher,
        "UpperNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>(Arrays.asList("path"));
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowUpdateLowerNw(anyString(),
        (BasicFlow) anyObject(), (List<String>) anyList());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    Flow prev = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    Flow curr = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    ArrayList<String> attributesList = new ArrayList<>();

    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("UpperNetworkId", prev, curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(false));

    verify(onFlow).flowUpdateUpperNwExistPath("UpperNetworkId",
        settingFlow, attributesList);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow, org.o3project.odenos.core.component.network.flow.Flow, java.util.ArrayList)}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnFlowUpdatePre_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Flows */

    List<BasicFlowMatch> settingMatches = new ArrayList<>();
    List<String> settingPath = new ArrayList<>(Arrays.asList("path"));
    Map<String, List<FlowAction>> settingEdgeActions = new HashMap<>();
    Map<String, String> settingAttribubtes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none",
        settingMatches, settingPath, settingEdgeActions,
        settingAttribubtes);
    doReturn(settingFlow).when(target).getFlow(anyString(),
        (Flow) anyObject());

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowUpdateLowerNw(anyString(),
        (BasicFlow) anyObject(), (List<String>) anyList());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    Flow prev = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    Flow curr = new BasicFlow("0", "FlowId", "Owner", true, "0", "none",
        matches, path, edgeActions, attributes);
    ArrayList<String> attributesList = new ArrayList<>();

    /*
     * test
     */
    boolean result = target.onFlowUpdatePre("LayerizedNetworkId", prev,
        curr,
        attributesList);

    /*
     * check
     */
    assertThat(result, is(true));

    verifyZeroInteractions(onFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowDeletePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowDeletePre_Lower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowDeleteLowerNw(anyString(),
        (BasicFlow) anyObject());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowDeletePre("LowerNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

    verify(onFlow).flowDeleteLowerNw("LowerNetworkId", flow);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowDeletePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowDeletePre_Upper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowDeleteLowerNw(anyString(),
        (BasicFlow) anyObject());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowDeletePre("UpperNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(false));

    verifyNoMoreInteractions(onFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onFlowDeletePre(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testOnFlowDeletePre_Layerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterfaces */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = new NetworkInterface(dispatcher,
        "LowerNetworkId");
    NetworkInterface layerizedNetIf = new NetworkInterface(dispatcher,
        "LayerizedNetworkId");
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* LinkLayerizerOnFlow */

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);

    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable,
        netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowDeleteLowerNw(anyString(),
        (BasicFlow) anyObject());

    /* Parameters */

    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();

    BasicFlow flow = new BasicFlow("0", "FlowId", "Owner", true, "0",
        "none",
        matches, path, edgeActions, attributes);

    /*
     * test
     */
    boolean result = target.onFlowDeletePre("LayerizedNetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(true));

    verifyNoMoreInteractions(onFlow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}.
   * @throws Exception
   */
  @Test
  public void testOnInPacketAddedPre() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    netIfs.put("UpperNetworkId", upperNetIf);
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    Map<String, String> settingInAttributes = new HashMap<>();
    InPacket settingInPacket = new InPacket("PacketId", "NodeId",
        "PortId", "data".getBytes(), settingInAttributes);
    PowerMockito.doReturn(settingInPacket).when(target, "getInPacket",
        (NetworkInterface) anyObject(), anyString());

    Map<String, String> attributes = new HashMap<>();
    Packet inPacket = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), attributes);
    InPacketAdded msg = new InPacketAdded(inPacket);

    Map<String, Link> layerizedLinks = new HashMap<>();
    Link link1 = new Link("LinkId1", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    layerizedLinks.put("LinkId1", link1);
    doReturn(layerizedLinks).when(layerizedNetIf).getLinks();

    /*
     * test
     */
    boolean result = target.onInPacketAddedPre("UpperNetworkId", msg);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.Flow)}.
   * @throws Exception
   */
  @Test
  public void testGetFlowStringFlow() throws Exception {

    /*
     * settnig
     */
    createPowerSpy();

    HashMap<String, NetworkInterface> networkInterfaces = new HashMap<>();
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));
    networkInterfaces.put("NetworkId", netIf);
    PowerMockito.doReturn(networkInterfaces).when(target,
        "networkInterfaces");

    BasicFlowMatch match = new BasicFlowMatch();
    List<BasicFlowMatch> matches = new ArrayList<>(Arrays.asList(match));
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none", matches, path, edgeActions, attributes);
    doReturn(settingFlow).when(netIf).getFlow("FlowId");

    Flow flow = new Flow("FlowId");

    /*
     * test
     */
    BasicFlow result = target.getFlow("NetworkId", flow);

    /*
     * check
     */
    assertThat(result, is(settingFlow));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getFlow(org.o3project.odenos.core.component.NetworkInterface, java.lang.String)}.
   */
  @Test
  public void testGetFlowNetworkInterfaceString() {

    /*
     * setting
     */
    NetworkInterface netIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "NetworkId"));

    BasicFlowMatch match = new BasicFlowMatch();
    List<BasicFlowMatch> matches = new ArrayList<>(Arrays.asList(match));
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeActions = new HashMap<>();
    Map<String, String> attributes = new HashMap<>();
    BasicFlow settingFlow = new BasicFlow("0", "FlowId", "Owner", true,
        "0", "none", matches, path, edgeActions, attributes);
    doReturn(settingFlow).when(netIf).getFlow("FlowId");

    /*
     * test
     */
    BasicFlow result = target.getFlow(netIf, "FlowId");

    /*
     * check
     */
    assertThat(result, is(settingFlow));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedAddedLower(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedAddedLower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    FlowSet flowSet = new FlowSet();
    Map<String, Flow> flows = flowSet.getFlows();
    BasicFlow basicFlow1 = new BasicFlow("FlowId1");
    BasicFlow basicFlow2 = new BasicFlow("FlowId2");
    flows.put("FlowId1", basicFlow1);
    flows.put("FlowId2", basicFlow2);
    doReturn(flowSet).when(upperNetIf).getFlowSet();

    doReturn(null).when(layerizedNetIf).delLink(anyString());

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable));

    Map<String, List<String>> lowerFlows = new ConcurrentHashMap<>();
    lowerFlows.put("LinkId1", new ArrayList<String>());
    lowerFlows.put("LinkId2", new ArrayList<String>());
    Whitebox.setInternalState(onFlow, "lowerFlows", lowerFlows);

    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    doNothing().when(onFlow).flowAddedLowerNw(anyString(),
        (BasicFlow) anyObject());

    /*
     * test
     */
    Whitebox.invokeMethod(target, "doOnConnectionChangedAddedLower",
        "LowerNetworkId");

    /*
     * check
     */
    assertThat(lowerFlows.size(), is(0));
    target.setBoundaryPortAttr();

    verify(onFlow).flowAddedLowerNw("LowerNetworkId", basicFlow1);
    verify(onFlow).flowAddedLowerNw("LowerNetworkId", basicFlow2);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedAddedUpper(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedAddedUpper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    doReturn(null).when(layerizedNetIf).deleteTopology();

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    FlowSet flowSet = new FlowSet();
    Map<String, Flow> flows = flowSet.getFlows();
    BasicFlow basicFlow1 = new BasicFlow("FlowId1");
    BasicFlow basicFlow2 = new BasicFlow("FlowId2");
    flows.put("FlowId1", basicFlow1);
    flows.put("FlowId2", basicFlow2);
    doReturn(flowSet).when(lowerNetIf).getFlowSet();

    /*
     * test
     */
    Whitebox.invokeMethod(target, "doOnConnectionChangedAddedUpper",
        "UpperNetworkId");

    /*
     * check
     */
    verify(layerizedNetIf).deleteTopology();
    target.setBoundaryPortAttr();

    verify(onFlow).flowAddedLowerNw("LowerNetworkId", basicFlow1);
    verify(onFlow).flowAddedLowerNw("LowerNetworkId", basicFlow2);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedAddedLayerized(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedAddedLayerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterface */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* LinkLayerizerOnFlow */
    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = Mockito.spy(new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable));
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* for upper */

    doReturn(null).when(upperNetIf).deleteAllFlow();

    Map<String, Port> upperPorts1 = new HashMap<>();
    Port upperPort11 = new Port("PortId11", "NodeId1");
    Port upperPort12 = new Port("PortId12", "NodeId1");
    upperPorts1.put("PortId11", upperPort11);
    upperPorts1.put("PortId12", upperPort12);

    Map<String, Port> upperPorts2 = new HashMap<>();
    Port upperPort21 = new Port("PortId21", "NodeId2");
    Port upperPort22 = new Port("PortId22", "NodeId2");
    upperPorts2.put("PortId21", upperPort21);
    upperPorts2.put("PortId22", upperPort22);

    Map<String, String> upperAttributes1 = new HashMap<>();
    Map<String, String> upperAttributes2 = new HashMap<>();

    Node upperNode1 = new Node("0", "NodeId1", upperPorts1,
        upperAttributes1);
    Node upperNode2 = new Node("0", "NodeId2", upperPorts2,
        upperAttributes2);

    Map<String, Node> upperNodes = new HashMap<>();
    upperNodes.put("NodeId1", upperNode1);
    upperNodes.put("NodeId2", upperNode2);

    Map<String, Link> upperLinks = new HashMap<>();
    Link upperLink1 = new Link("LinkId1");
    Link upperLink2 = new Link("LinkId2");
    upperLinks.put("LinkId1", upperLink1);
    upperLinks.put("LinkId2", upperLink2);

    doReturn(upperLinks).when(upperNetIf).getLinks();

    Topology upperTopology = new Topology(upperNodes, upperLinks);
    doReturn(upperTopology).when(upperNetIf).getTopology();

    /* for layerized */

    doReturn(null).when(layerizedNetIf).putNode((Node) anyObject());
    doReturn(null).when(layerizedNetIf).putPort((Port) anyObject());
    doReturn(null).when(layerizedNetIf).putLink((Link) anyObject());

    /* for lower */

    Map<String, List<String>> lowerPriority = new HashMap<>();
    Map<String, Flow> lowerFlows = new HashMap<>();
    BasicFlow lowerFlow1 = new BasicFlow("FlowId1");
    BasicFlow lowerFlow2 = new BasicFlow("FlowId2");
    lowerFlows.put("FlowId1", lowerFlow1);
    lowerFlows.put("FlowId2", lowerFlow2);
    FlowSet flowSet = new FlowSet("0", lowerPriority, lowerFlows);

    doReturn(flowSet).when(lowerNetIf).getFlowSet();

    /*
     * test
     */
    Whitebox.invokeMethod(target, "doOnConnectionChangedAddedLayerized",
        "LayerizedNetworkId");

    /*
     * check
     */

    verify(layerizedNetIf).putNode(upperNode1);
    verify(layerizedNetIf).putNode(upperNode2);
    verify(layerizedNetIf).putPort(upperPort11);
    verify(layerizedNetIf).putPort(upperPort12);
    verify(layerizedNetIf).putPort(upperPort21);
    verify(layerizedNetIf).putPort(upperPort22);
    verify(layerizedNetIf).putLink(upperLink1);
    verify(layerizedNetIf).putLink(upperLink2);

    verify(onFlow).flowAddedLowerNw("LowerNetworkId", lowerFlow1);
    verify(onFlow).flowAddedLowerNw("LowerNetworkId", lowerFlow2);

    assertThat(onFlow.getLowerFlows().size(), is(0));
    assertThat(onFlow.getLayerizedLinks().size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedDeleteLower(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedDeleteLower() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterface */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* LinkLayerizerOnFlow */

    Map<String, List<String>> lowerFlows = new HashMap<>();

    lowerFlows.put("LowerLinkId1", new ArrayList<String>());
    lowerFlows.put("LowerLinkId2", new ArrayList<String>());
    lowerFlows.put("LowerLinkId3", new ArrayList<String>());

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable);
    Whitebox.setInternalState(onFlow, "lowerFlows", lowerFlows);
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* Links */

    doReturn(null).when(layerizedNetIf).delLink(anyString());

    /*
     * test
     */
    Whitebox.invokeMethod(target, "doOnConnectionChangedDeleteLower",
        "LowerNetworkId");

    /*
     * check
     */
    assertThat(onFlow.getLayerizedLinks().size(), is(0));
    assertThat(onFlow.getLowerFlows().size(), is(0));

    //verify(target).unsubscribeLower("LowerNetworkId");

    verify(layerizedNetIf).delLink("LowerLinkId1");
    verify(layerizedNetIf).delLink("LowerLinkId2");
    verify(layerizedNetIf).delLink("LowerLinkId3");
    verifyNoMoreInteractions(layerizedNetIf);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedDeleteUpper(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedDeleteUpper() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterface */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Ports */

    Map<String, Port> layerizedPorts1 = new HashMap<>();

    Port port11 = new Port("0", "LayerizedPortId11", "LayerizedNodeId1");
    layerizedPorts1.put("LayerizedPortId11", port11);
    Port port12 = new Port("0", "LayerizedPortId12", "LayerizedNodeId1");
    layerizedPorts1.put("LayerizedPortId12", port12);

    Map<String, Port> layerizedPorts2 = new HashMap<>();

    Port port21 = new Port("0", "LayerizedPortId21", "LayerizedNodeId2");
    layerizedPorts2.put("LayerizedPortId21", port21);
    Port port22 = new Port("0", "LayerizedPortId22", "LayerizedNodeId2");
    layerizedPorts2.put("LayerizedPortId22", port22);

    /* Nodes */

    Map<String, Node> layerizedNodes = new HashMap<>();

    Map<String, String> attributes1 = new HashMap<>();
    Node layerizedNode1 = new Node("0", "LayerizedNodeId1",
        layerizedPorts1, attributes1);
    layerizedNodes.put("LayerizedNodeId1", layerizedNode1);

    Map<String, String> attributes2 = new HashMap<>();
    Node layerizedNode2 = new Node("0", "LayerizedNodeId2",
        layerizedPorts2, attributes2);
    layerizedNodes.put("LayerizedNodeId2", layerizedNode2);

    doReturn(layerizedNodes).when(layerizedNetIf).getNodes();

    /* Links */

    Map<String, Link> layerizedLinks = new HashMap<>();
    layerizedLinks.put("LayerizedLinkId1", new Link("LayerizedLinkId1"));
    layerizedLinks.put("LayerizedLinkId2", new Link("LayerizedLinkId2"));
    layerizedLinks.put("LayerizedLinkId3", new Link("LayerizedLinkId3"));
    doReturn(layerizedLinks).when(layerizedNetIf).getLinks();

    /* LinkLayerizerOnFlow */

    Map<String, List<String>> lowerFlows = new HashMap<>();

    lowerFlows.put("LowerLinkId1", new ArrayList<String>());
    lowerFlows.put("LowerLinkId2", new ArrayList<String>());
    lowerFlows.put("LowerLinkId3", new ArrayList<String>());

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable);
    Whitebox.setInternalState(onFlow, "lowerFlows", lowerFlows);
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* delete method */

    doReturn(null).when(upperNetIf).delLink(anyString());
    doReturn(null).when(layerizedNetIf).delNode(anyString());
    doReturn(null).when(layerizedNetIf).delPort(anyString(), anyString());
    doReturn(null).when(layerizedNetIf).delLink(anyString());

    /*
     * test
     */
    target.doOnConnectionChangedDeleteUpper("UpperNetworkId");

    /*
     * check
     */
    assertThat(onFlow.getLayerizedLinks().size(), is(0));
    assertThat(onFlow.getLowerFlows().size(), is(0));

    assertThat(conversionTable.getLink().size(), is(0));
    assertThat(conversionTable.getPort().size(), is(0));
    assertThat(conversionTable.getNode().size(), is(0));
    assertThat(conversionTable.getFlow().size(), is(0));

    verify(upperNetIf).delLink("LowerLinkId1");
    verify(upperNetIf).delLink("LowerLinkId2");
    verify(upperNetIf).delLink("LowerLinkId3");
    verify(upperNetIf).deleteAllFlow();

    verify(layerizedNetIf).putStatusFaildAllFlow();
    verify(layerizedNetIf).delNode("LayerizedNodeId1");
    verify(layerizedNetIf).delNode("LayerizedNodeId2");
    verify(layerizedNetIf).delPort("LayerizedNodeId1",
        "LayerizedPortId11");
    verify(layerizedNetIf).delPort("LayerizedNodeId1",
        "LayerizedPortId12");
    verify(layerizedNetIf).delPort("LayerizedNodeId2",
        "LayerizedPortId21");
    verify(layerizedNetIf).delPort("LayerizedNodeId2",
        "LayerizedPortId22");
    verify(layerizedNetIf).getLinks();
    verify(layerizedNetIf).delLink("LayerizedLinkId1");
    verify(layerizedNetIf).delLink("LayerizedLinkId2");
    verify(layerizedNetIf).delLink("LayerizedLinkId3");

    //verify(target).unsubscribeUpper("UpperNetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#doOnConnectionChangedDeleteLayerized(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDoOnConnectionChangedDeleteLayerized() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    /* ConversionTable */

    ConversionTable conversionTable = new ConversionTable();
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /* NetworkInterface */

    Map<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(dispatcher, "LayerizedNetworkId"));
    netIfs.put("LowerNetworkId", lowerNetIf);
    netIfs.put("UpperNetworkId", upperNetIf);
    netIfs.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    /* Ports */

    Map<String, Port> layerizedPorts1 = new HashMap<>();

    Port port11 = new Port("0", "LayerizedPortId11", "LayerizedNodeId1");
    layerizedPorts1.put("LayerizedPortId11", port11);
    Port port12 = new Port("0", "LayerizedPortId12", "LayerizedNodeId1");
    layerizedPorts1.put("LayerizedPortId12", port12);

    Map<String, Port> layerizedPorts2 = new HashMap<>();

    Port port21 = new Port("0", "LayerizedPortId21", "LayerizedNodeId2");
    layerizedPorts2.put("LayerizedPortId21", port21);
    Port port22 = new Port("0", "LayerizedPortId22", "LayerizedNodeId2");
    layerizedPorts2.put("LayerizedPortId22", port22);

    /* Nodes */

    Map<String, Node> layerizedNodes = new HashMap<>();

    Map<String, String> attributes1 = new HashMap<>();
    Node layerizedNode1 = new Node("0", "LayerizedNodeId1",
        layerizedPorts1, attributes1);
    layerizedNodes.put("LayerizedNodeId1", layerizedNode1);

    Map<String, String> attributes2 = new HashMap<>();
    Node layerizedNode2 = new Node("0", "LayerizedNodeId2",
        layerizedPorts2, attributes2);
    layerizedNodes.put("LayerizedNodeId2", layerizedNode2);

    doReturn(layerizedNodes).when(layerizedNetIf).getNodes();

    /* Links */

    Map<String, Link> upperLinks = new HashMap<>();
    upperLinks.put("UpperLinkId1", new Link("UpperLinkId1"));
    upperLinks.put("UpperLinkId2", new Link("UpperLinkId2"));
    upperLinks.put("UpperLinkId3", new Link("UpperLinkId3"));
    doReturn(upperLinks).when(upperNetIf).getLinks();

    Map<String, Link> layerizedLinks = new HashMap<>();
    layerizedLinks.put("LayerizedLinkId1", new Link("LayerizedLinkId1"));
    layerizedLinks.put("LayerizedLinkId2", new Link("LayerizedLinkId2"));
    layerizedLinks.put("LayerizedLinkId3", new Link("LayerizedLinkId3"));
    doReturn(layerizedLinks).when(layerizedNetIf).getLinks();

    /* Flows */

    doReturn(null).when(upperNetIf).deleteAllFlow();

    /* LinkLayerizerOnFlow */

    Map<String, List<String>> lowerFlows = new HashMap<>();

    lowerFlows.put("LowerLinkId1", new ArrayList<String>());
    lowerFlows.put("LowerLinkId2", new ArrayList<String>());
    lowerFlows.put("LowerLinkId3", new ArrayList<String>());

    LinkLayerizerBoundaryTable boundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    LinkLayerizerOnFlow onFlow = new LinkLayerizerOnFlow(
        conversionTable, netIfs, boundaryTable);
    Whitebox.setInternalState(onFlow, "lowerFlows", lowerFlows);
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /* delete method */

    doReturn(null).when(upperNetIf).delLink(anyString());
    doReturn(null).when(layerizedNetIf).delNode(anyString());
    doReturn(null).when(layerizedNetIf).delPort(anyString(), anyString());
    doReturn(null).when(layerizedNetIf).delLink(anyString());

    /*
     * test
     */
    target.doOnConnectionChangedDeleteLayerized("LayerizedNetworkId");

    /*
     * check
     */
    assertThat(onFlow.getLayerizedLinks().size(), is(0));
    assertThat(onFlow.getLowerFlows().size(), is(0));

    assertThat(conversionTable.getLink().size(), is(0));
    assertThat(conversionTable.getPort().size(), is(0));
    assertThat(conversionTable.getNode().size(), is(0));
    assertThat(conversionTable.getFlow().size(), is(0));

    verify(upperNetIf).deleteAllFlow();
    verify(upperNetIf).delLink("LowerLinkId1");
    verify(upperNetIf).delLink("LowerLinkId2");
    verify(upperNetIf).delLink("LowerLinkId3");
    verifyNoMoreInteractions(upperNetIf);

    verify(layerizedNetIf).getNodes();
    verify(layerizedNetIf).delNode("LayerizedNodeId1");
    verify(layerizedNetIf).delNode("LayerizedNodeId2");
    verify(layerizedNetIf).delPort("LayerizedNodeId1",
        "LayerizedPortId11");
    verify(layerizedNetIf).delPort("LayerizedNodeId1",
        "LayerizedPortId12");
    verify(layerizedNetIf).delPort("LayerizedNodeId2",
        "LayerizedPortId21");
    verify(layerizedNetIf).delPort("LayerizedNodeId2",
        "LayerizedPortId22");
    verify(layerizedNetIf).getLinks();
    verify(layerizedNetIf).delLink("LayerizedLinkId1");
    verify(layerizedNetIf).delLink("LayerizedLinkId2");
    verify(layerizedNetIf).delLink("LayerizedLinkId3");
    verify(layerizedNetIf).putStatusFaildAllFlow();

    //verify(target).unsubscribeLayerized("LayerizedNetworkId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#createParser()}.
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
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#putUpperLinkSync(Boolean)}.
   * @throws Exception
   */
  @Test
  public void testPutUpperLinkSync() throws Exception {

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "putUpperLinkSync",
        false);

    /*
     * check
     */
    verify(target).setUpperLinkisync(false);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(String.class).endsWith("false"), is(true));

    assertThat(target.isUpperLinkisync(), is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#postBoundary(LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testPostBoundary() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = Mockito
        .spy(new LinkLayerizerBoundaryTable());
    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
        "BoundaryId", "Type", "LowerNwId", "LowerNwNodeId",
        "LowerNwPortId", "UpperNwId", "UpperNwNodeId", "UpperNwPortId");

    PowerMockito.doNothing().when(target, "setBoundaryPortAttr");

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "postBoundary",
        boundary);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(LinkLayerizerBoundary.class), is(boundary));

    verify(linkLayerizerBoundaryTable).addEntry(boundary);

    String boundaryId = boundary.getId();
    assertThat(boundaryId, is(notNullValue()));
    assertThat(boundaryId, is(not("BoundaryId")));

    assertThat(linkLayerizerBoundaryTable.getEntry(boundaryId), is(boundary));

    PowerMockito.verifyPrivate(target).invoke("setBoundaryPortAttr");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getBoundaries()}.
   * @throws Exception
   */
  @Test
  public void testGetBoundaries() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = new LinkLayerizerBoundaryTable();
    Map<String, LinkLayerizerBoundary> boundaries = new HashMap<>();
    linkLayerizerBoundaryTable.setBoundaries(boundaries);
    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getBoundaries");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsMap(LinkLayerizerBoundary.class),
        is(boundaries));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getBoundary(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetBoundary() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = new LinkLayerizerBoundaryTable();
    LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
        "BoundaryId", "Type", "LowerNwId", "LowerNwNodeId",
        "LowerNwPortId", "UpperNwId", "UpperNwNodeId", "UpperNwPortId");
    linkLayerizerBoundaryTable.addEntry("BoundaryId", boundary);
    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getBoundary",
        "BoundaryId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(LinkLayerizerBoundary.class), is(boundary));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#putBoundary(java.lang.String, LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testPutBoundary_Add() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
        "BoundaryId", "Type", "LowerNwId", "LowerNwNodeId",
        "LowerNwPortId", "UpperNwId", "UpperNwNodeId", "UpperNwPortId");

    PowerMockito.doNothing().when(target, "setBoundaryPortAttr");

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "putBoundary",
        "BoundaryId", boundary);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(LinkLayerizerBoundary.class), is(boundary));

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = Whitebox
        .getInternalState(target, "linkLayerizerBoundaryTable");
    assertThat(linkLayerizerBoundaryTable.getEntry("BoundaryId"),
        is(boundary));

    PowerMockito.verifyPrivate(target).invoke("setBoundaryPortAttr");
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#putBoundary(java.lang.String, LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testPutBoundary_Update() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId", "Type",
        "LowerNwId1", "LowerNwNodeId1", "LowerNwPortId1",
        "UpperNwId2", "UpperNwNodeId1", "UpperNwPortId1");
    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId", "Type",
        "LowerNwId2", "LowerNwNodeId2", "LowerNwPortId2",
        "UpperNwId2", "UpperNwNodeId2", "UpperNwPortId2");

    PowerMockito.doNothing().when(target, "setBoundaryPortAttr");

    /*
     * test
     */
    Response result1 = Whitebox.invokeMethod(target, "putBoundary",
        "BoundaryId", boundary1);
    Response result2 = Whitebox.invokeMethod(target, "putBoundary",
        "BoundaryId", boundary2);

    /*
     * check
     */
    assertThat(result1.statusCode, is(Response.OK));
    assertThat(result1.getBody(LinkLayerizerBoundary.class), is(boundary1));
    assertThat(result2.statusCode, is(Response.OK));
    assertThat(result2.getBody(LinkLayerizerBoundary.class), is(boundary2));

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = Whitebox
        .getInternalState(target, "linkLayerizerBoundaryTable");
    assertThat(linkLayerizerBoundaryTable.getEntry("BoundaryId"),
        is(boundary2));

    PowerMockito.verifyPrivate(target, times(2)).invoke(
        "setBoundaryPortAttr");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#deleteBoundary(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDeleteBoundary() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = new LinkLayerizerBoundaryTable();
    LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
        "BoundaryId", "Type", "LowerNwId", "LowerNwNodeId",
        "LowerNwPortId", "UpperNwId", "UpperNwNodeId", "UpperNwPortId");
    linkLayerizerBoundaryTable.addEntry(boundary);
    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    PowerMockito.doNothing().when(target, "unsetBoundaryPortAttr",
        anyString());
    ;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deleteBoundary",
        "BoundaryId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    assertThat(linkLayerizerBoundaryTable.getEntry("BoundaryId"),
        is(nullValue()));

    PowerMockito.verifyPrivate(target).invoke("unsetBoundaryPortAttr",
        "BoundaryId");

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLowerFlows()}.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetLowerFlows() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> lowerFlows = new HashMap<>();
    List<String> flows = new ArrayList<>(
        Arrays.asList("LinkId1", "LinkId2"));
    lowerFlows.put("FlowId", flows);

    LinkLayerizerOnFlow onFlow = Mockito.mock(LinkLayerizerOnFlow.class);
    doReturn(lowerFlows).when(onFlow).getLowerFlows();

    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getLowerFlows");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    Map<String, ?> resultMap = result.getBodyAsMap(List.class);
    assertThat((Map<String, List<String>>) resultMap, is(lowerFlows));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLowerFlows(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetLowerFlowsString() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> lowerFlows = new HashMap<>();
    List<String> flows = new ArrayList<>(
        Arrays.asList("LinkId1", "LinkId2"));
    lowerFlows.put("FlowId", flows);

    LinkLayerizerOnFlow onFlow = Mockito.mock(LinkLayerizerOnFlow.class);
    doReturn(lowerFlows).when(onFlow).getLowerFlows();

    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getLowerFlows",
        "FlowId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyAsList(String.class), is(flows));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLayerizedlinks()}.
   * @throws Exception
   */
  @Test
  public void testGetLayerizedlinks() throws Exception {

    /*
     * setting
     */
    LinkLayerizerOnFlow onFlow = Mockito.mock(LinkLayerizerOnFlow.class);
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);
    Map<String, String> settingLinks = new HashMap<>();

    doReturn(settingLinks).when(onFlow).getLayerizedLinks();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getLayerizedlinks");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    Map<String, String> resultLinks = result.getBodyAsMap(String.class);

    assertThat(resultLinks, is(settingLinks));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLayerizedLink(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetLayerizedLink() throws Exception {

    /*
     * setting
     */
    LinkLayerizerOnFlow onFlow = Mockito.mock(LinkLayerizerOnFlow.class);
    Whitebox.setInternalState(target, "linkLayerizerOnFlow", onFlow);
    Map<String, String> settingLinks = new HashMap<>();
    settingLinks.put("FlowId", "LinkId");

    doReturn(settingLinks).when(onFlow).getLayerizedLinks();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getLayerizedLink",
        "FlowId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    String resultLink = result.getBody(String.class);

    assertThat(resultLink, is("LinkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#setBoundaryPortAttr()}.
   * @throws Exception
   */
  @Test
  public void testSetBoundaryPortAttr() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    Map<String, LinkLayerizerBoundary> boundaries = new HashMap<>();

    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId1", "Type1",
        "LowerNwId1", "LowerNwNodeId1", "LowerNwPortId1",
        "UpperNwId1", "UpperNwNodeId1", "UpperNwPortId1");
    boundaries.put("BoundaryId1", boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId2", "Type2",
        "LowerNwId2", "LowerNwNodeId2", "LowerNwPortId2",
        "UpperNwId2", "UpperNwNodeId2", "UpperNwPortId2");
    boundaries.put("BoundaryId2", boundary2);

    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    doReturn(boundaries).when(linkLayerizerBoundaryTable).getBoundaries();

    HashMap<String, NetworkInterface> netIfs = new HashMap<>();
    NetworkInterface lowerNetIf1 = PowerMockito
        .mock(NetworkInterface.class);
    NetworkInterface lowerNetIf2 = PowerMockito
        .mock(NetworkInterface.class);
    NetworkInterface upperNetIf1 = PowerMockito
        .mock(NetworkInterface.class);
    NetworkInterface upperNetIf2 = PowerMockito
        .mock(NetworkInterface.class);
    netIfs.put("LowerNwId1", lowerNetIf1);
    netIfs.put("LowerNwId2", lowerNetIf2);
    netIfs.put("UpperNwId1", upperNetIf1);
    netIfs.put("UpperNwId2", upperNetIf2);
    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    doReturn(null).when(lowerNetIf1).putPort((Port) anyObject());
    doReturn(null).when(lowerNetIf2).putPort((Port) anyObject());
    doReturn(null).when(upperNetIf1).putPort((Port) anyObject());
    doReturn(null).when(upperNetIf2).putPort((Port) anyObject());

    Port lowerPort1 = new Port("LowerNwPortId1", "LowerNwNodeId1");
    Port lowerPort2 = new Port("LowerNwPortId2", "LowerNwNodeId2");
    Port upperPort1 = new Port("UpperNwPortId1", "UpperNwNodeId1");
    Port upperPort2 = new Port("UpperNwPortId2", "UpperNwNodeId2");

    doReturn(lowerPort1).when(lowerNetIf1).getPort("LowerNwNodeId1",
        "LowerNwPortId1");
    doReturn(lowerPort2).when(lowerNetIf2).getPort("LowerNwNodeId2",
        "LowerNwPortId2");
    doReturn(upperPort1).when(upperNetIf1).getPort("UpperNwNodeId1",
        "UpperNwPortId1");
    doReturn(upperPort2).when(upperNetIf2).getPort("UpperNwNodeId2",
        "UpperNwPortId2");

    /*
     * test
     */
    Whitebox.invokeMethod(target, "setBoundaryPortAttr");

    /*
     * check
     */
    verify(lowerNetIf1).putPort(lowerPort1);
    verify(lowerNetIf2).putPort(lowerPort2);
    verify(upperNetIf1).putPort(upperPort1);
    verify(upperNetIf2).putPort(upperPort2);

    assertThat(lowerPort1.getAttribute("is_boundary"), is("true"));
    assertThat(lowerPort2.getAttribute("is_boundary"), is("true"));
    assertThat(upperPort1.getAttribute("is_boundary"), is("true"));
    assertThat(upperPort2.getAttribute("is_boundary"), is("true"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#unsetBoundaryPortAttr(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testUnsetBoundaryPortAttr() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    Map<String, NetworkInterface> netIfs = new HashMap<>();

    NetworkInterface lowerNetIf1 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId1"));
    netIfs.put("LowerNetworkId1", lowerNetIf1);
    NetworkInterface lowerNetIf2 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId2"));
    netIfs.put("LowerNetworkId2", lowerNetIf2);
    NetworkInterface lowerNetIf3 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId3"));
    netIfs.put("LowerNetworkId3", lowerNetIf3);

    NetworkInterface upperNetIf1 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId1"));
    netIfs.put("UpperNetworkId1", upperNetIf1);
    NetworkInterface upperNetIf2 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId2"));
    netIfs.put("UpperNetworkId2", upperNetIf2);
    NetworkInterface upperNetIf3 = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId3"));
    netIfs.put("UpperNetworkId3", upperNetIf3);

    PowerMockito.doReturn(netIfs).when(target, "networkInterfaces");

    Port lowerPort2 = Mockito
        .spy(new Port("LowerPortId2", "LowerNodeId2"));
    Port upperPort2 = Mockito
        .spy(new Port("UpperPortId2", "UpperNodeId2"));

    doReturn(lowerPort2).when(lowerNetIf2).getPort("LowerNodeId2",
        "LowerPortId2");
    doReturn(upperPort2).when(upperNetIf2).getPort("UpperNodeId2",
        "UpperPortId2");

    doReturn(null).when(lowerPort2).deleteAttribute(anyString());
    doReturn(null).when(upperPort2).deleteAttribute(anyString());

    doReturn(null).when(lowerNetIf2).putPort((Port) anyObject());
    doReturn(null).when(upperNetIf2).putPort((Port) anyObject());

    Map<String, LinkLayerizerBoundary> boundaries = new HashMap<>();

    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "BoundaryId1", "Type1",
        "LowerNetworkId1", "LowerNodeId1", "LowerPortId1",
        "UpperNetworkId1", "UpperNodeId1", "UpperPortId1");
    boundaries.put("BoundaryId1", boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "BoundaryId2", "Type2",
        "LowerNetworkId2", "LowerNodeId2", "LowerPortId2",
        "UpperNetworkId2", "UpperNodeId2", "UpperPortId2");
    boundaries.put("BoundaryId2", boundary2);

    LinkLayerizerBoundary boundary3 = new LinkLayerizerBoundary(
        "BoundaryId3", "Type3",
        "LowerNetworkId3", "LowerNodeId3", "LowerPortId3",
        "UpperNetworkId3", "UpperNodeId3", "UpperPortId3");
    boundaries.put("BoundaryId3", boundary3);

    LinkLayerizerBoundaryTable linkLayerizerBoundaryTable = Mockito
        .mock(LinkLayerizerBoundaryTable.class);
    Whitebox.setInternalState(target, "linkLayerizerBoundaryTable",
        linkLayerizerBoundaryTable);

    doReturn(boundaries).when(linkLayerizerBoundaryTable).getBoundaries();

    /*
     * test
     */
    Whitebox.invokeMethod(target, "unsetBoundaryPortAttr", "BoundaryId2");

    /*
     * check
     */

    verify(lowerNetIf2).getPort("LowerNodeId2", "LowerPortId2");
    verify(lowerPort2).deleteAttribute("is_boundary");
    verify(lowerNetIf2).putPort(lowerPort2);

    verifyZeroInteractions(lowerNetIf1);
    verifyZeroInteractions(lowerNetIf3);
    verifyZeroInteractions(upperNetIf1);
    verifyZeroInteractions(upperNetIf3);

    verify(upperNetIf2).getPort("UpperNodeId2", "UpperPortId2");
    verify(upperPort2).deleteAttribute("is_boundary");
    verify(upperNetIf2).putPort(upperPort2);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLowerNetworkIds()}.
   * @throws Exception
   */
  @Test
  public void testGetLowerNetworkIds() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    List<String> result = Whitebox.invokeMethod(target,
        "getLowerNetworkIds");

    /*
     * check
     */
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is("LowerNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getUpperNetworkIds()}.
   * @throws Exception
   */
  @Test
  public void testGetUpperNetworkIds() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    List<String> result = Whitebox.invokeMethod(target,
        "getUpperNetworkIds");

    /*
     * check
     */
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is("UpperNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLayerizedNetworkIds()}.
   * @throws Exception
   */
  @Test
  public void testGetLayerizedNetworkIds() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    List<String> result = Whitebox.invokeMethod(target,
        "getLayerizedNetworkIds");

    /*
     * check
     */
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is("LayerizedNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getNetworkIds(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIds() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    List<String> resultLower = Whitebox.invokeMethod(target,
        "getNetworkIds", "lower");
    List<String> resultUpper = Whitebox.invokeMethod(target,
        "getNetworkIds", "upper");
    List<String> resultLayerized = Whitebox.invokeMethod(target,
        "getNetworkIds", "layerized");

    /*
     * check
     */
    assertThat(resultLower.size(), is(1));
    assertThat(resultUpper.size(), is(1));
    assertThat(resultLayerized.size(), is(1));

    assertThat(resultLower.get(0), is("LowerNetworkId"));
    assertThat(resultUpper.get(0), is("UpperNetworkId"));
    assertThat(resultLayerized.get(0), is("LayerizedNetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getUpperNetworkIf()}.
   * @throws Exception
   */
  @Test
  public void testGetUpperNetworkIf() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> networkInterfaces = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    networkInterfaces.put("LowerNetworkId", lowerNetIf);
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNetIf);
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(
            dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(networkInterfaces).when(target,
        "networkInterfaces");

    /*
     * test
     */
    NetworkInterface result = Whitebox.invokeMethod(target,
        "getUpperNetworkIf");

    /*
     * check
     */
    assertThat(result, is(upperNetIf));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getLayerizedNetworkIf()}.
   * @throws Exception
   */
  @Test
  public void testGetLayerizedNetworkIf() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> networkInterfaces = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    networkInterfaces.put("LowerNetworkId", lowerNetIf);
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNetIf);
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(
            dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(networkInterfaces).when(target,
        "networkInterfaces");

    /*
     * test
     */
    NetworkInterface result = Whitebox.invokeMethod(target,
        "getLayerizedNetworkIf");

    /*
     * check
     */
    assertThat(result, is(layerizedNetIf));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getNetworkIf(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIfString() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    HashMap<String, NetworkInterface> networkInterfaces = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    networkInterfaces.put("LowerNetworkId", lowerNetIf);
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNetIf);
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(
            dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(networkInterfaces).when(target,
        "networkInterfaces");

    /*
     * test
     */
    NetworkInterface resultLower = Whitebox.invokeMethod(target,
        "getNetworkIf", "lower");
    NetworkInterface resultUpper = Whitebox.invokeMethod(target,
        "getNetworkIf", "upper");
    NetworkInterface resultLayerized = Whitebox.invokeMethod(target,
        "getNetworkIf", "layerized");

    /*
     * check
     */
    assertThat(resultLower, is(lowerNetIf));
    assertThat(resultUpper, is(upperNetIf));
    assertThat(resultLayerized, is(layerizedNetIf));

  }

  /**
   * Test method for {@literal org.o3project.odenos.component.linklayerizer.LinkLayerizer#getNetworkIf(List<java.lang.String>)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIfListString() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    HashMap<String, NetworkInterface> networkInterfaces = new HashMap<>();
    NetworkInterface lowerNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "LowerNetworkId"));
    networkInterfaces.put("LowerNetworkId", lowerNetIf);
    NetworkInterface upperNetIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "UpperNetworkId"));
    networkInterfaces.put("UpperNetworkId", upperNetIf);
    NetworkInterface layerizedNetIf = PowerMockito
        .spy(new NetworkInterface(
            dispatcher, "LayerizedNetworkId"));
    networkInterfaces.put("LayerizedNetworkId", layerizedNetIf);
    PowerMockito.doReturn(networkInterfaces).when(target,
        "networkInterfaces");

    List<String> lowerIds = Arrays.asList("LowerNetworkId");
    List<String> upperIds = Arrays.asList("UpperNetworkId");
    List<String> layerizedIds = Arrays.asList("LayerizedNetworkId");

    /*
     * test
     */
    NetworkInterface resultLower = Whitebox.invokeMethod(target,
        "getNetworkIf", lowerIds);
    NetworkInterface resultUpper = Whitebox.invokeMethod(target,
        "getNetworkIf", upperIds);
    NetworkInterface resultLayerized = Whitebox.invokeMethod(target,
        "getNetworkIf", layerizedIds);

    /*
     * check
     */
    assertThat(resultLower, is(lowerNetIf));
    assertThat(resultUpper, is(upperNetIf));
    assertThat(resultLayerized, is(layerizedNetIf));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isConnectionType(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsConnectionType() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    boolean resultLower = Whitebox.invokeMethod(target,
        "isConnectionType", "lower");
    boolean resultUpper = Whitebox.invokeMethod(target,
        "isConnectionType", "upper");
    boolean resultLayerized = Whitebox.invokeMethod(target,
        "isConnectionType", "layerized");

    /*
     * check
     */
    assertThat(resultLower, is(true));
    assertThat(resultUpper, is(true));
    assertThat(resultLayerized, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isConnectionType(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsConnectionType_NoConnection() throws Exception {

    /*
     * test
     */
    boolean resultLower = Whitebox.invokeMethod(target,
        "isConnectionType", "lower");
    boolean resultUpper = Whitebox.invokeMethod(target,
        "isConnectionType", "upper");
    boolean resultLayerized = Whitebox.invokeMethod(target,
        "isConnectionType", "layerized");

    /*
     * check
     */
    assertThat(resultLower, is(false));
    assertThat(resultUpper, is(false));
    assertThat(resultLayerized, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isLayerizedNetwork(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsLayerizedNetwork() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    boolean resultLower = Whitebox.invokeMethod(target,
        "isLayerizedNetwork", "LowerNetworkId");
    boolean resultUpper = Whitebox.invokeMethod(target,
        "isLayerizedNetwork", "UpperNetworkId");
    boolean resultLayerized = Whitebox.invokeMethod(target,
        "isLayerizedNetwork", "LayerizedNetworkId");

    /*
     * check
     */
    assertThat(resultLower, is(false));
    assertThat(resultUpper, is(false));
    assertThat(resultLayerized, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isLowerNetwork(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsLowerNetwork() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    boolean resultLower = Whitebox.invokeMethod(target, "isLowerNetwork",
        "LowerNetworkId");
    boolean resultUpper = Whitebox.invokeMethod(target, "isLowerNetwork",
        "UpperNetworkId");
    boolean resultLayerized = Whitebox.invokeMethod(target,
        "isLowerNetwork", "LayerizedNetworkId");

    /*
     * check
     */
    assertThat(resultLower, is(true));
    assertThat(resultUpper, is(false));
    assertThat(resultLayerized, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#isUpperNetwork(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsUpperNetwork() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    boolean resultLower = Whitebox.invokeMethod(target, "isUpperNetwork",
        "LowerNetworkId");
    boolean resultUpper = Whitebox.invokeMethod(target, "isUpperNetwork",
        "UpperNetworkId");
    boolean resultLayerized = Whitebox.invokeMethod(target,
        "isUpperNetwork", "LayerizedNetworkId");

    /*
     * check
     */
    assertThat(resultLower, is(false));
    assertThat(resultUpper, is(true));
    assertThat(resultLayerized, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizer#getConnectionType(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConnectionType() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    conversionTable.addEntryConnectionType("LowerNetworkId", "lower");
    conversionTable.addEntryConnectionType("UpperNetworkId", "upper");
    conversionTable.addEntryConnectionType("LayerizedNetworkId",
        "layerized");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    /*
     * test
     */
    String resultLower = Whitebox.invokeMethod(target,
        "getConnectionType", "LowerNetworkId");
    String resultUpper = Whitebox.invokeMethod(target,
        "getConnectionType", "UpperNetworkId");
    String resultLayerized = Whitebox.invokeMethod(target,
        "getConnectionType", "LayerizedNetworkId");

    /*
     * check
     */
    assertThat(resultLower, is("lower"));
    assertThat(resultUpper, is("upper"));
    assertThat(resultLayerized, is("layerized"));

  }

}
