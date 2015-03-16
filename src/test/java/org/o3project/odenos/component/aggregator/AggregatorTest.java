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

package org.o3project.odenos.component.aggregator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.core.util.PathCalculator;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test class for Aggregator.
 *
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Aggregator.class, ConversionTable.class,
    PathCalculator.class,
    ComponentConnection.class, NetworkInterface.class })
public class AggregatorTest {

  private Aggregator target;
  private MessageDispatcher dispatcher;

  private static final String AGGREGATOR_ID = "aggregator";
  private static final String ORIGINAL_NW_ID = "original_network";
  private static final String AGGREGATED_NW_ID = "aggregated_network";

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
    target = PowerMockito.spy(new Aggregator("objectId", dispatcher) {
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

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#Aggregator(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testAggregator() throws Exception {

    String objId = AGGREGATOR_ID;

    target = PowerMockito.spy(new Aggregator(objId, dispatcher));
    assertThat(target.getObjectId(), is(objId));

    assertThat(target, is(instanceOf(Aggregator.class)));
    assertThat(WhiteboxImpl.getInternalState(target, "pathCalculator"),
        instanceOf(PathCalculator.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getSuperType()}
   * .
   */
  @Test
  public final void testGetSuperType() {
    assertThat(target.getSuperType(), is("SuperType"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getDescription()}
   * .
   */
  @Test
  public final void testGetDescription() {
    assertThat(target.getDescription(), is("Description"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConnectionTypes()}
   * .
   */
  @Test
  public final void testGetConnectionTypes() {
    assertThat(target.getConnectionTypes(), is("aggregated:1,original:1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedAddedPreSuccessOriginal() {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "running", "logic_id",
            "network1");

    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    when(target.getObjectId()).thenReturn(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);

    assertThat(target.onConnectionChangedAddedPre(msg), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedAddedPreSuccessAggregator() {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "aggregated",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("obj_id", "aggregated",
            "running",
            "logic_id", "network1");

    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    when(target.getObjectId()).thenReturn(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);

    assertThat(target.onConnectionChangedAddedPre(msg), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedAddedPreWithObjectTypeError()
      throws Exception {
    ComponentConnection prev =
        new ComponentConnection("logic_id", "LogicAndNetwork",
            "initializing");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnection("logic_id",
            "Original", "running"));

    when(curr.getObjectType()).thenReturn("isConnectionType");

    ComponentConnectionChanged msg =
        PowerMockito.spy(new ComponentConnectionChanged("add", prev,
            curr));

    assertThat(target.onConnectionChangedAddedPre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedPreWithLogicIdError()
      throws Exception {
    ComponentConnection prev =
        new ComponentConnection("logic_id", "LogicAndNetwork",
            "initializing");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnection("logic_id",
            "Original", "running"));

    when(curr.getObjectType()).thenReturn(
        "ComponentConnectionLogicAndNetwork.TYPE");
    when(target.getObjectId()).thenReturn("isConnectionType");

    ComponentConnectionChanged msg =
        PowerMockito.spy(new ComponentConnectionChanged("add", prev,
            curr));

    assertThat(target.onConnectionChangedAddedPre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedPreWithOrgTypeError()
      throws Exception {
    target = PowerMockito.spy(new Aggregator(AGGREGATOR_ID, dispatcher));

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "object",
            "running", "logic_id", "network1"));

    when(curr.getObjectType()).thenReturn(
        "ComponentConnectionLogicAndNetwork.TYPE");
    when(curr.getConnectionType()).thenReturn("ORIGINAL");

    when(target.getObjectId()).thenReturn(
        "ComponentConnectionLogicAndNetwork.LOGIC_ID");

    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(true).when(conversionTable, "isConnectionType",
        "ORIGINAL");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onConnectionChangedAddedPre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedPreWithAggTypeError()
      throws Exception {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "object",
            "running", "logic_id", "network1"));

    when(curr.getObjectType()).thenReturn(
        "ComponentConnectionLogicAndNetwork.TYPE");
    when(curr.getConnectionType()).thenReturn("AGGREGATED");

    when(target.getObjectId()).thenReturn(
        "ComponentConnectionLogicAndNetwork.LOGIC_ID");

    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(true).when(conversionTable, "isConnectionType",
        "AGGREGATED");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onConnectionChangedAddedPre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAddedPre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedAddedPreWithCncTypeError()
      throws Exception {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "object",
            "running", "logic_id", "network1"));

    when(curr.getObjectType()).thenReturn(
        "ComponentConnectionLogicAndNetwork.TYPE");
    when(curr.getConnectionType()).thenReturn("OTHER");

    when(target.getObjectId()).thenReturn(
        "ComponentConnectionLogicAndNetwork.LOGIC_ID");

    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(true).when(conversionTable, "isConnectionType",
        "AGGREGATED");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onConnectionChangedAddedPre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedUpdatePreWithObjectTypeErr() {
    ComponentConnection prev =
        new ComponentConnection("logic_id", "LogicAndNetwork",
            "initializing");
    ComponentConnection curr = new ComponentConnection("logic_id",
        "Original", "running");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "update", prev, curr);

    assertThat(target.onConnectionChangedUpdatePre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedUpdatePreWithLogicIdError()
      throws Exception {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "original",
            "running", "logic_id", "network1"));

    when(curr.getObjectType()).thenReturn(
        "ComponentConnectionLogicAndNetwork.TYPE");
    when(target.getObjectId()).thenReturn("isConnectionType");

    ComponentConnectionChanged msg =
        PowerMockito.spy(new ComponentConnectionChanged("update", prev,
            curr));

    assertThat(target.onConnectionChangedUpdatePre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedUpdatePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedUpdatePreSuccess() {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "running", "logic_id",
            "network1");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "update", prev, curr);

    when(target.getObjectId()).thenReturn(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    assertThat(target.onConnectionChangedUpdatePre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedDeletePreObjectTypeErr() {
    ComponentConnection prev =
        new ComponentConnection("logic_id", "LogicAndNetwork",
            "initializing");
    ComponentConnection curr = new ComponentConnection("logic_id",
        "Original", "running");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "delete", prev, curr);

    assertThat(target.onConnectionChangedDeletePre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedDeletePreLogicIdErr() {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "running", "logic_id",
            "network1");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "delete", prev, curr);

    when(target.getObjectId()).thenReturn(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);

    assertThat(target.onConnectionChangedDeletePre(msg), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedDeletePre(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedDeletePreSuccess() {
    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "running", "logic_id",
            "network1");

    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "delete", prev, curr);

    when(target.getObjectId()).thenReturn(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    assertThat(target.onConnectionChangedDeletePre(msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedOriginalSuccess()
      throws Exception {

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put("network01", new NetworkInterface(dispatcher, "network01"));
    nwifs.put("network02", new NetworkInterface(dispatcher, "network02"));

    @SuppressWarnings("serial")
    ArrayList<String> array = new ArrayList<String>() {
      {
        add("network01");
        add("network02");
      }
    };
    PowerMockito.doReturn(array).when(conversionTable, "getConnectionList",
        "aggregated");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network01");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "original",
            "running", "logic_id", "network01"));
    when(curr.getConnectionType()).thenReturn("original");
    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    target.onConnectionChangedAdded(msg);

    PowerMockito.verifyPrivate(conversionTable, times(1)).invoke(
        "addEntryNetwork",
        anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedOriginalSize0()
      throws Exception {
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put("network01", new NetworkInterface(dispatcher, "network01"));
    nwifs.put("network02", new NetworkInterface(dispatcher, "network02"));

    @SuppressWarnings("serial")
    ArrayList<String> array = new ArrayList<String>() {
      {
        add("network01");
        add("network02");
      }
    };

    PowerMockito.doReturn(array).when(conversionTable, "getConnectionList",
        "aggregated");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network01");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "original",
            "running", "logic_id", "network01"));
    when(curr.getConnectionType()).thenReturn("original");
    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    target.onConnectionChangedAdded(msg);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedAddedAggregatedSuccess()
      throws Exception {

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put("network01", new NetworkInterface(dispatcher, "network01"));
    nwifs.put("network02", new NetworkInterface(dispatcher, "network02"));

    @SuppressWarnings("serial")
    ArrayList<String> array = new ArrayList<String>() {
      {
        add("network01");
        add("network02");
      }
    };
    PowerMockito.doReturn(array).when(conversionTable, "getConnectionList",
        "original");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "aggregated",
            "initializing",
            "logic_id", "network01");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "aggregated",
            "running", "logic_id", "network01"));
    when(curr.getConnectionType()).thenReturn("aggregated");
    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    target.onConnectionChangedAdded(msg);

    PowerMockito.verifyPrivate(conversionTable, times(1)).invoke(
        "addEntryNetwork",
        anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedAdded(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedAddedAggregatedSize0()
      throws Exception {

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put("network01", new NetworkInterface(dispatcher, "network01"));
    nwifs.put("network02", new NetworkInterface(dispatcher, "network02"));

    PowerMockito.doReturn(new ArrayList<String>()).when(conversionTable,
        "getConnectionList", "original");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "aggregated",
            "initializing",
            "logic_id", "network01");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "aggregated",
            "running", "logic_id", "network01"));
    when(curr.getConnectionType()).thenReturn("aggregated");
    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    target.onConnectionChangedAdded(msg);

    PowerMockito.verifyPrivate(conversionTable, never()).invoke(
        "addEntryNetwork", anyString(),
        anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedUpdate(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedUpdate() {
    // Do Nothing.

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnConnectionChangedDeleteSuccess() throws Exception {
    target = PowerMockito.spy(new Aggregator("objectId", dispatcher));

    Flow flow = new Flow("flowId");
    NetworkInterface netif = new NetworkInterface(dispatcher, "network1");
    netif.putFlow(flow);
    Map<String, NetworkInterface> netifs = new HashMap<String, NetworkInterface>();
    netifs.put("network1", netif);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn("network1").when(target, "getConvNetworkId",
        "network1");
    PowerMockito.doReturn(netifs).when(target, "networkInterfaces");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "original",
            "running", "logic_id", "network1"));
    when(curr.getConnectionType()).thenReturn("original");
    ComponentConnectionChanged msg = new ComponentConnectionChanged(
        "delete", prev, curr);

    target.onConnectionChangedDelete(msg);

    PowerMockito.verifyPrivate(conversionTable, times(1)).invoke(
        "delEntryConnectionType",
        "network1");
    PowerMockito.verifyPrivate(conversionTable, times(1)).invoke(
        "delEntryNetwork", "network1");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onConnectionChangedDelete(org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged)}
   * .
   */
  @Test
  public final void testOnConnectionChangedDeleteNwIdErr() throws Exception {
    target = PowerMockito.spy(new Aggregator("objectId", dispatcher));

    PowerMockito.doReturn(null)
        .when(target, "getConvNetworkId", "network1");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    @SuppressWarnings("serial")
    ArrayList<String> array = new ArrayList<String>() {
      {
        add("network01");
        add("network02");
      }
    };
    PowerMockito.doReturn(array).when(conversionTable, "getConnectionList",
        "aggregated");

    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    ComponentConnection prev =
        new ComponentConnectionLogicAndNetwork("obj_id", "original",
            "initializing",
            "logic_id", "network1");
    ComponentConnection curr =
        PowerMockito.spy(new ComponentConnectionLogicAndNetwork(
            "obj_id", "original",
            "running", "logic_id", "network1"));
    when(curr.getConnectionType()).thenReturn("original");
    ComponentConnectionChanged msg = new ComponentConnectionChanged("add",
        prev, curr);

    target.onConnectionChangedDelete(msg);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeOriginal(java.lang.String)}
   * .
   */
  @Test
  public final void testSubscribeOriginal() throws Exception {
    String networkId = ORIGINAL_NW_ID;

    target.subscribeOriginal(networkId);

    PowerMockito.verifyPrivate(target, times(5)).invoke(
        "addEntryEventSubscription",
        anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeOriginal(java.lang.String)}
   * .
   */
  @Test
  public final void testSubscribeOriginalWithException() throws Exception {
    String networkId = ORIGINAL_NW_ID;

    PowerMockito.doThrow(new Exception()).when(target,
        "applyEventSubscription");

    target.subscribeOriginal(networkId);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeOriginal(java.lang.String)}
   * .
   */
  @Test
  public final void testUnSubscribeOriginal() throws Exception {
    String networkId = ORIGINAL_NW_ID;

    target.unsubscribeOriginal(networkId);

    PowerMockito.verifyPrivate(target, times(5)).invoke(
        "removeEntryEventSubscription", anyString(),
        anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeOriginal(java.lang.String)}
   * .
   */
  @Test
  public final void testUnSubscribeOriginalWithException() throws Exception {
    String networkId = ORIGINAL_NW_ID;

    PowerMockito.doThrow(new Exception()).when(target,
        "applyEventSubscription");

    target.unsubscribeOriginal(networkId);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeAggregated(java.lang.String)}
   * .
   */
  @Test
  public final void testSubscribeAggregated() throws Exception {
    String networkId = AGGREGATED_NW_ID;

    target.subscribeAggregated(networkId);

    PowerMockito.verifyPrivate(target, times(2)).invoke(
        "addEntryEventSubscription",
        anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeAggregated(java.lang.String)}
   * .
   */
  @Test
  public final void testSubscribeAggregatedWithException() throws Exception {
    String networkId = AGGREGATED_NW_ID;

    PowerMockito.doThrow(new Exception()).when(target,
        "applyEventSubscription");

    target.subscribeAggregated(networkId);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeAggregated(java.lang.String)}
   * .
   */
  @Test
  public final void testUnSubscribeAggregated() throws Exception {
    String networkId = AGGREGATED_NW_ID;

    target.unsubscribeAggregated(networkId);

    PowerMockito.verifyPrivate(target, times(4)).invoke(
        "removeEntryEventSubscription", anyString(),
        anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#subscribeAggregated(java.lang.String)}
   * .
   */
  @Test
  public final void testUnSubscribeAggregatedWithException() throws Exception {
    String networkId = AGGREGATED_NW_ID;

    PowerMockito.doThrow(new Exception()).when(target,
        "applyEventSubscription");

    target.unsubscribeAggregated(networkId);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#createParser()}
   * .
   * @throws Exception
   */
  @Test
  public final void testCreateParser() throws Exception {
    @SuppressWarnings("unchecked")
    RequestParser<String> result = (RequestParser<String>) Whitebox
        .invokeMethod(target, "createParser");

    Request req = new Request(AGGREGATOR_ID, Request.Method.GET,
        "aggregated_nw_port", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(AGGREGATOR_ID, Request.Method.GET,
        "original_nw_port", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(AGGREGATOR_ID, Request.Method.GET,
        "aggregated_nw_flow", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(AGGREGATOR_ID, Request.Method.GET,
        "original_nw_flow", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(AGGREGATOR_ID, Request.Method.PUT,
        "aggregated_nw_port", null);
    assertThat(result.parse(req), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public final void testOnRequestSuccess() {
    Request.Method method = Request.Method.GET;
    ObjectProperty body = null;
    Request request = new Request("Aggregator", method,
        "aggregated_nw_port", body);

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public final void testOnRequestParseErr() {
    Request.Method method = Request.Method.GET;
    Object body = new Object();
    Request request = new Request("ObjectId", method,
        "settings/default_idle_timer", body);

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat((String) WhiteboxImpl.getInternalState(result, "body"),
        is("Error unknown request "));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getNwPort(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testGetNwPort() throws Exception {
    String type = "type";

    assertThat(Whitebox.invokeMethod(target, "getNwPort", type),
        is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getNwFlow(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testGetNwFlow() throws Exception {
    String type = "type";

    assertThat(Whitebox.invokeMethod(target, "getNwFlow", type),
        is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeAddedSuccess() throws Exception {
    String networkId = ORIGINAL_NW_ID;
    Map<String, Port> ports1 = new HashMap<String, Port>();
    Node nodeMessage = new Node("0", "ORIGINAL_NW_ID", ports1,
        new HashMap<String, String>());
    nodeMessage.setId("ORIGINAL_NW_ID");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn("network2").when(target, "getNetworkIdByType",
        "aggregated");

    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    NetworkInterface originalNwInterface =
        new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
    nwifs.put("network2", originalNwInterface);

    PowerMockito.doReturn("node2").when(target, "getConvNodeId", ORIGINAL_NW_ID, "node_id");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    target.onNodeAdded(networkId, nodeMessage);

    //PowerMockito.verifyPrivate(target).invoke("getConvNodeId", networkId,
     //   nodeMessage.getId());
    PowerMockito.verifyPrivate(conversionTable).invoke("addEntryNode",
        anyString(),
        anyString(), anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeAddedAggregated() throws Exception {
    String networkId = ORIGINAL_NW_ID;
    Node nodeMessage = new Node();
    nodeMessage.setId("node_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onNodeAdded(networkId, nodeMessage);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeAddedDiffNetworkId() throws Exception {
    String networkId = ORIGINAL_NW_ID;
    Node nodeMessage = new Node();
    nodeMessage.setId("node_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn("network2").when(target, "getNetworkIdByType",
        "original");
    target.onNodeAdded(networkId, nodeMessage);

    PowerMockito.verifyPrivate(target, never()).invoke("getConvNodeId",
        networkId, "node_id");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeAddedNodeIdNotNUll() throws Exception {
    String networkId = ORIGINAL_NW_ID;
    Map<String, Port> ports1 = new HashMap<>();
    Node nodeMessage = new Node("0", "ORIGINAL_NW_ID", ports1, new HashMap<String, String>());
    nodeMessage.setId("ORIGINAL_NW_ID");

    ConversionTable conversionTable = PowerMockito.spy(new ConversionTable());
    conversionTable = PowerMockito.spy(new ConversionTable());

    //PowerMockito.doReturn("original").when(conversionTable, "getConnectionType", networkId);
    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target, "getNetworkIdByType", "aggregated");
    
    Map<String, NetworkInterface> nwifs = new HashMap<>();
    NetworkInterface nwIf = new NetworkInterface(
        dispatcher, AGGREGATED_NW_ID, AggregatorTest.class.getSimpleName());
    nwifs.put(AGGREGATED_NW_ID, nwIf);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(
        target, "getConvNodeId", AGGREGATED_NW_ID, "objectId");

    target.onNodeAdded(networkId, nodeMessage);

    PowerMockito.verifyPrivate(target).invoke("getConvNodeId", AGGREGATED_NW_ID, "objectId");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnNodeUpdatePreSuccess() throws Exception {
    ArrayList<String> attrList = new ArrayList<String>();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, Port> ports1 = new HashMap<String, Port>();
    Map<String, Port> ports2 = new HashMap<String, Port>();

    Node prev = new Node(
        "0", "node1", ports1, new HashMap<String, String>());
    Node curr = new Node(
        "0", "node2", ports2, new HashMap<String, String>());

    assertThat(
        target.onNodeUpdatePre(ORIGINAL_NW_ID, prev, curr, attrList),
        is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnNodeUpdatePreConnTypeError() throws Exception {
    ArrayList<String> attrList = new ArrayList<String>();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Map<String, Port> ports1 = new HashMap<String, Port>();
    Map<String, Port> ports2 = new HashMap<String, Port>();

    Node prev = new Node(
        "0", "node1", ports1, new HashMap<String, String>());
    Node curr = new Node(
        "0", "node2", ports2, new HashMap<String, String>());

    assertThat(
        target.onNodeUpdatePre(ORIGINAL_NW_ID, prev, curr, attrList),
        is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeDeletePreSuccess() throws Exception {
    @SuppressWarnings("serial")
    ArrayList<String> array = new ArrayList<String>() {
      {
        add("network01");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(array).when(conversionTable, "getNode",
        anyString(), anyString());
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");

    Map<String, Port> ports1 = new HashMap<String, Port>();
    Node node1 = new Node(
        "0", "node1", ports1, new HashMap<String, String>());

    assertThat(target.onNodeDeletePre(ORIGINAL_NW_ID, node1), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeDeletePreConnTypeErr() throws Exception {
    Map<String, Port> ports1 = new HashMap<String, Port>();
    Node node1 = new Node(
        "0", "node1", ports1, new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onNodeDeletePre(ORIGINAL_NW_ID, node1), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeDeletePreNetworkIdErr() throws Exception {
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "original");

    Map<String, Port> ports1 = new HashMap<String, Port>();
    Node node1 = new Node(
        "0", "node1", ports1, new HashMap<String, String>());

    assertThat(target.onNodeDeletePre(ORIGINAL_NW_ID, node1), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onNodeDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   */
  @Test
  public final void testOnNodeDeletePreSizeErr() throws Exception {
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");
    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");

    Map<String, Port> ports1 = new HashMap<String, Port>();
    Node node1 = new Node(
        "0", "node1", ports1, new HashMap<String, String>());

    assertThat(target.onNodeDeletePre(ORIGINAL_NW_ID, node1), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public final void testOnPortAddedSuccess() throws Exception {
    Port portN1P1 = Mockito.spy(new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>()));

    NetworkInterface netif = new NetworkInterface(dispatcher, "network1");
    netif.putPort(portN1P1);
    Map<String, NetworkInterface> netifs = new HashMap<String, NetworkInterface>();
    netifs.put(AGGREGATED_NW_ID, netif);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(netifs).when(target, "networkInterfaces");

    target.onPortAdded(ORIGINAL_NW_ID, portN1P1);

    verify(portN1P1).getInLink();
    verify(portN1P1).getAttributes();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public final void testOnPortAddedAggregated() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onPortAdded(ORIGINAL_NW_ID, portN1P1);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortAdded(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public final void testOnPortAddedNetIdNull() throws Exception {
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "aggregated");

    Port portN1P1 = Mockito.spy(new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>()));

    target.onPortAdded(ORIGINAL_NW_ID, portN1P1);

    verify(portN1P1, never()).getNode();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnPortUpdatePreSuccess() throws Exception {
    ArrayList<String> attrList = new ArrayList<String>();

    Port prev = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port curr = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(
        target.onPortUpdatePre(ORIGINAL_NW_ID, prev, curr, attrList),
        is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnPortUpdatePreConnTypeError() throws Exception {
    ArrayList<String> attrList = new ArrayList<String>();

    Port prev = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port curr = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(
        target.onPortUpdatePre(ORIGINAL_NW_ID, prev, curr, attrList),
        is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortDeletePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public final void testOnPortDeletePreSuccess() throws Exception {
    Port port = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onPortDeletePre(ORIGINAL_NW_ID, port), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onPortDeletePre(java.lang.String, org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public final void testOnPortDeletePreConnTypeError() throws Exception {
    Port port = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onPortDeletePre(ORIGINAL_NW_ID, port), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkAddedSuccess() throws Exception {
    Link linkMessage = Mockito.spy(new Link());
    linkMessage.setId("link_id");
    when(linkMessage.validate()).thenReturn(true);

    String networkId = ORIGINAL_NW_ID;

    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    NetworkInterface originalNwInterface =
        new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
    nwifs.put("network2", originalNwInterface);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn("network2").when(target, "getNetworkIdByType",
        "aggregated");
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    target.onLinkAdded(networkId, linkMessage);

    assertThat(Whitebox.invokeMethod(target, "getConvPortId",
        anyString(), anyString(), anyString()), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkAddedAggregated() throws Exception {

    String networkId = ORIGINAL_NW_ID;
    Link linkMessage = new Link();
    linkMessage.setId("link_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onLinkAdded(networkId, linkMessage);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkAddedValidateErr() throws Exception {

    String networkId = ORIGINAL_NW_ID;
    Link linkMessage = new Link();
    linkMessage.setId("link_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onLinkAdded(networkId, linkMessage);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkAddedDiffNetworkId() throws Exception {
    Link linkMessage = Mockito.spy(new Link());
    linkMessage.setId("link_id");
    when(linkMessage.validate()).thenReturn(true);

    String networkId = ORIGINAL_NW_ID;

    Map<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    NetworkInterface originalNwInterface =
        new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
    nwifs.put("network2", originalNwInterface);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "aggregated");

    nwifs.put("network2", originalNwInterface);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    target.onLinkAdded(networkId, linkMessage);

    PowerMockito.verifyPrivate(target, never()).invoke("getConvPortId",
        anyString(),
        anyString(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, java.util.ArrayList)}
   * .
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public final void testOnLinkUpdateSuccess() throws Exception {
    Map<String, List<String>> prio = new HashMap<String, List<String>>();
    Map<String, Flow> flows = Mockito.spy(new HashMap<String, Flow>());
    Set<String> keyset = new HashSet<String>();
    when(flows.keySet()).thenReturn(keyset);
    FlowSet flowset = Mockito.spy(new FlowSet("1", prio, flows));

    NetworkInterface aggnif = PowerMockito.spy(new NetworkInterface(
        dispatcher, AGGREGATED_NW_ID));
    NetworkInterface orgif = PowerMockito.spy(new NetworkInterface(
        dispatcher, ORIGINAL_NW_ID));
    doReturn(flowset).when(orgif).getFlowSet();
    doReturn(flowset).when(aggnif).getFlowSet();

    Response resp = new Response(Response.OK, new Object());

    PowerMockito.doReturn(resp).when(orgif, "getObjectToNetwork",
        ORIGINAL_NW_ID, "flows");
    PowerMockito.doReturn(resp).when(aggnif, "getObjectToNetwork",
        AGGREGATED_NW_ID, "flows");

    Map<String, NetworkInterface> netifs = Mockito
        .spy(new HashMap<String, NetworkInterface>());
    when(netifs.get(AGGREGATED_NW_ID)).thenReturn(aggnif);
    when(netifs.get(ORIGINAL_NW_ID)).thenReturn(orgif);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(netifs).when(target, "networkInterfaces");
    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");

    Map attr = new HashMap<String, String>();
    attr.put("oper_status", "UP");

    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN1P2 = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P1 = new Port("0", "port1", "node2",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link curr = Mockito.spy(new Link("0", "link1",
        portN1P2.getNode(), portN1P2.getId(), //src
        portN2P1.getNode(), portN2P1.getId(), //dst
        null));
    Link prev = new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>());

    curr.putAttributes(attr);
    when(curr.validate()).thenReturn(true);

    ArrayList<String> attrList = new ArrayList<String>();

    target.onLinkUpdate(ORIGINAL_NW_ID, prev, curr, attrList);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnLinkUpdateConnTypeErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN1P2 = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P1 = new Port("0", "port1", "node2",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link prev = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));
    Link curr = Mockito.spy(new Link("0", "link1",
        portN1P2.getNode(), portN1P2.getId(), //src
        portN2P1.getNode(), portN2P1.getId(), //dst
        new HashMap<String, String>()));
    ArrayList<String> attrList = new ArrayList<String>();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onLinkUpdate(ORIGINAL_NW_ID, prev, curr, attrList);
    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnLinkUpdateValidateErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN1P2 = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P1 = new Port("0", "port1", "node2",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Link prev = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));
    Link curr = Mockito.spy(new Link("0", "link1",
        portN1P2.getNode(), portN1P2.getId(), //src
        portN2P1.getNode(), portN2P1.getId(), //dst
        new HashMap<String, String>()));
    ArrayList<String> attrList = new ArrayList<String>();

    when(curr.validate()).thenReturn(false);

    target.onLinkUpdate(ORIGINAL_NW_ID, prev, curr, attrList);
    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnLinkUpdateNetworkIdErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN1P2 = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P1 = new Port("0", "port1", "node2",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "aggregated");

    Link prev = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));
    Link curr = Mockito.spy(new Link("0", "link1",
        portN1P2.getNode(), portN1P2.getId(), //src
        portN2P1.getNode(), portN2P1.getId(), //dst
        new HashMap<String, String>()));

    when(curr.validate()).thenReturn(true);

    ArrayList<String> attrList = new ArrayList<String>();
    target.onLinkUpdate(ORIGINAL_NW_ID, prev, curr, attrList);
    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkUpdate(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnLinkUpdateOperStatusErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN1P2 = new Port("0", "port2", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P1 = new Port("0", "port1", "node2",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    Link prev = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));
    Link curr = Mockito.spy(new Link("0", "link1",
        portN1P2.getNode(), portN1P2.getId(), //src
        portN2P1.getNode(), portN2P1.getId(), //dst
        null));

    when(curr.validate()).thenReturn(true);

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");

    ArrayList<String> attrList = new ArrayList<String>();
    target.onLinkUpdate(ORIGINAL_NW_ID, prev, curr, attrList);

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkDeleteSuccess() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link link = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));

    Map<String, List<String>> prio = new HashMap<String, List<String>>();
    Map<String, Flow> flows = Mockito.spy(new HashMap<String, Flow>());
    Set<String> keyset = new HashSet<String>();
    when(flows.keySet()).thenReturn(keyset);
    FlowSet flowset = Mockito.spy(new FlowSet("1", prio, flows));

    when(link.validate()).thenReturn(true);

    NetworkInterface aggnif = PowerMockito.spy(new NetworkInterface(
        dispatcher, AGGREGATED_NW_ID));
    NetworkInterface orgif = PowerMockito.spy(new NetworkInterface(
        dispatcher, ORIGINAL_NW_ID));

    doReturn(flowset).when(orgif).getFlowSet();

    Response resp = new Response(Response.OK, new Object());
    PowerMockito.doReturn(resp).when(orgif, "getObjectToNetwork",
        ORIGINAL_NW_ID, "flows");
    PowerMockito.doReturn(resp).when(aggnif, "getObjectToNetwork",
        AGGREGATED_NW_ID, "flows");

    Map<String, NetworkInterface> netifs = Mockito
        .spy(new HashMap<String, NetworkInterface>());

    when(netifs.get(AGGREGATED_NW_ID)).thenReturn(aggnif);
    when(netifs.get(ORIGINAL_NW_ID)).thenReturn(orgif);

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");
    PowerMockito.doReturn(netifs).when(target, "networkInterfaces");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");

    target.onLinkDelete(ORIGINAL_NW_ID, link);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkDeleteConnTypeErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link link = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", "netId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onLinkDelete("netId", link);
    verify(link, never()).validate();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkDeleteValidateErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link link = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", "netId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    when(link.validate()).thenReturn(false);

    target.onLinkDelete("netId", link);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onLinkDelete(java.lang.String, org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testOnLinkDeleteNetworkIdErr() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link link = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", "netId");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    when(link.validate()).thenReturn(true);
    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "aggregated");

    target.onLinkDelete("netId", link);

    PowerMockito.verifyPrivate(target, never()).invoke("updateOperStatus",
        anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnFlowAdded() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");

    NetworkInterface mockAggNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface mockOrigNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, mockAggNwIf);
    nwifs.put(ORIGINAL_NW_ID, mockOrigNwIf);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow basicFlow = Mockito.spy(new BasicFlow("basicFlowId"));

    PowerMockito.doReturn(basicFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    PowerMockito.doReturn(true).when(target, "updateFlow",
        anyObject(), anyObject(), anyObject(), anyObject());

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(conversionTable, times(1)).invoke(
        "addEntryFlow",
        eq(ORIGINAL_NW_ID), eq("basicFlowId"), eq(AGGREGATED_NW_ID),
        eq("basicFlowId"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public final void testOnFlowAddedOriginal() throws Exception {
    String networkId = ORIGINAL_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(target, never()).invoke(
        "getNetworkIdByType", "aggregated");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public final void testOnFlowAddedOrgNetworkIfErr() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "original");

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), eq("flow_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public final void testOnFlowAddedAggNetworkIfErr() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(null).when(target, "getNetworkIdByType",
        "aggregated");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), eq("flow_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public final void testOnFlowAddedNotEqualAggregatorNwId() throws Exception {
    String networkId = AGGREGATED_NW_ID + "_diff";
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), eq("flow_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   */
  @Test
  public final void testOnFlowAddedBasicFlowIsNull() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");

    NetworkInterface mockAggNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface mockOrigNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, mockAggNwIf);
    nwifs.put(ORIGINAL_NW_ID, mockOrigNwIf);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow basicFlow = null;

    PowerMockito.doReturn(basicFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(target, never()).invoke("updateFlow",
        anyObject(), anyObject(), anyObject(), anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowAdded(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   * @throws Exception
   */
  @Test
  public final void testOnFlowAddedAggNoUpdateFlowPath() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow flow = new Flow();
    flow.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    PowerMockito.doReturn(AGGREGATED_NW_ID).when(target,
        "getNetworkIdByType", "aggregated");
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target,
        "getNetworkIdByType", "original");

    NetworkInterface mockAggNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface mockOrigNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, mockAggNwIf);
    nwifs.put(ORIGINAL_NW_ID, mockOrigNwIf);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow basicFlow = Mockito.spy(new BasicFlow("basicFlowId"));

    PowerMockito.doReturn(basicFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    PowerMockito.doReturn(false).when(target, "updateFlow",
        anyObject(), anyObject(), anyObject(), anyObject());

    target.onFlowAdded(networkId, flow);

    PowerMockito.verifyPrivate(conversionTable, never()).invoke(
        "addEntryFlow",
        eq(ORIGINAL_NW_ID), eq("basicFlowId"), eq(AGGREGATED_NW_ID),
        eq("basicFlowId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   * @throws Exception
   */
  @Test
  public final void testOnFlowUpdatePreConnTypeAggregated() throws Exception {
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");
    String networkId = AGGREGATED_NW_ID;

    BasicFlow srcFlow = Mockito.spy(new BasicFlow("1", "flow_id1", "owner",
        true, "priority", "status", new ArrayList<BasicFlowMatch>(),
        new ArrayList<String>(),
        new HashMap<String, List<FlowAction>>(),
        new HashMap<String, String>()));
    BasicFlow dstFlow = PowerMockito.spy(new BasicFlow("flow_id2"));

    PowerMockito.doReturn(srcFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    PowerMockito.doReturn(dstFlow).when(target, "getFlow", anyObject(),
        eq("flow_id2"));

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface srcNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface dstNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, srcNwIf);
    nwifs.put(ORIGINAL_NW_ID, dstNwIf);
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target, "getConvNetworkId",
        eq(AGGREGATED_NW_ID));
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    PowerMockito.doReturn("flow_id1::flow_id2").when(target,
        "getConvFlowId",
        eq(AGGREGATED_NW_ID), eq("flow_id1"));

    ArrayList<String> attributesList = new ArrayList<String>();
    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(false));

    PowerMockito.verifyPrivate(dstFlow, times(1)).invoke("setEnabled",
        eq(true));
    PowerMockito.verifyPrivate(dstFlow, times(1)).invoke("setPriority",
        eq("priority"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdatePre(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnFlowUpdatePreConnTypeOriginal() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");
    ArrayList<String> attributesList = new ArrayList<String>();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(true));

    PowerMockito.verifyPrivate(target, never()).invoke("networkInterfaces");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   */
  @Test
  public final void testOnFlowUpdatePreDstNetIfNull() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface srcNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, srcNwIf);
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    ArrayList<String> attributesList = new ArrayList<String>();
    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(false));

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), eq("flow_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   * @throws Exception
   */
  @Test
  public final void testOnFlowUpdatePreSrcFlowErr() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface srcNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface dstNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, srcNwIf);
    nwifs.put(ORIGINAL_NW_ID, dstNwIf);
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target, "getConvNetworkId",
        eq(AGGREGATED_NW_ID));
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow srcFlow = null;

    PowerMockito.doReturn(srcFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    ArrayList<String> attributesList = new ArrayList<String>();
    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(false));

    PowerMockito.verifyPrivate(target, never()).invoke("getConvFlowId",
        anyString(), anyString());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   * @throws Exception
   */
  @Test
  public final void testOnFlowUpdatePreDstFlowIdErr() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface srcNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface dstNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, srcNwIf);
    nwifs.put(ORIGINAL_NW_ID, dstNwIf);
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target, "getConvNetworkId",
        eq(AGGREGATED_NW_ID));
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow srcFlow = Mockito.spy(new BasicFlow("flow_id"));

    PowerMockito.doReturn(srcFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    PowerMockito.doReturn(null).when(target, "getConvFlowId",
        eq(AGGREGATED_NW_ID), eq("flow_id"));

    ArrayList<String> attributesList = new ArrayList<String>();
    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(false));

    PowerMockito.verifyPrivate(target, never()).invoke("getFlow",
        anyObject(), eq("flow_id2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onFlowUpdate(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, java.util.ArrayList)}
   * .
   * @throws Exception
   */
  @Test
  public final void testOnFlowUpdatePreDstFlowNullErr() throws Exception {
    String networkId = AGGREGATED_NW_ID;
    Flow prev = new Flow();
    prev.setFlowId("flow_id");
    Flow curr = new Flow();
    curr.setFlowId("flow_id");

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType", networkId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    NetworkInterface srcNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            AGGREGATED_NW_ID));
    NetworkInterface dstNwIf =
        PowerMockito.spy(new NetworkInterface(dispatcher,
            ORIGINAL_NW_ID));
    HashMap<String, NetworkInterface> nwifs = new HashMap<String, NetworkInterface>();
    nwifs.put(AGGREGATED_NW_ID, srcNwIf);
    nwifs.put(ORIGINAL_NW_ID, dstNwIf);
    PowerMockito.doReturn(ORIGINAL_NW_ID).when(target, "getConvNetworkId",
        eq(AGGREGATED_NW_ID));
    PowerMockito.doReturn(nwifs).when(target, "networkInterfaces");

    BasicFlow srcFlow = Mockito.spy(new BasicFlow("flow_id"));
    BasicFlow dstFlow = null;

    PowerMockito.doReturn(srcFlow).when(target, "getFlow", anyObject(),
        eq("flow_id"));

    PowerMockito.doReturn("flow_id1::flow_id2").when(target,
        "getConvFlowId",
        eq(AGGREGATED_NW_ID), eq("flow_id"));

    PowerMockito.doReturn(dstFlow).when(target, "getFlow", anyObject(),
        eq("flow_id2"));

    ArrayList<String> attributesList = new ArrayList<String>();
    boolean result =
        target.onFlowUpdatePre(networkId, prev, curr, attributesList);

    assertThat(result, is(false));

    PowerMockito.verifyPrivate(target, never()).invoke("updateFlow",
        anyObject(), anyObject(), anyObject(), anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   */
  @Test
  public final void testOnInPacketAddedPreSuccess() throws Exception {
    InPacketAdded msg = new InPacketAdded();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onInPacketAddedPre(ORIGINAL_NW_ID, msg), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.InPacketAdded)}
   * .
   */
  @Test
  public final void testOnInPacketAddedPreErr() throws Exception {
    InPacketAdded msg = new InPacketAdded();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onInPacketAddedPre(ORIGINAL_NW_ID, msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   */
  @Test
  public final void testOnOutPacketAddedPreSuccess() throws Exception {
    OutPacketAdded msg = new OutPacketAdded();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("aggregated").when(conversionTable,
        "getConnectionType",
        ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onOutPacketAddedPre(ORIGINAL_NW_ID, msg), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#onInPacketAddedPre(java.lang.String, org.o3project.odenos.core.component.network.packet.OutPacketAdded)}
   * .
   */
  @Test
  public final void testOnOutPacketAddedPreErr() throws Exception {
    OutPacketAdded msg = new OutPacketAdded();

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn("original")
        .when(conversionTable, "getConnectionType", ORIGINAL_NW_ID);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.onOutPacketAddedPre(ORIGINAL_NW_ID, msg), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getFlow(org.o3project.odenos.component.networkinterface, java.lang.String)}
   * .
   */
  @Test
  public final void testGetFlowSuccess() {
    NetworkInterface nwIf = PowerMockito.spy(new NetworkInterface(
        dispatcher, "nwcId"));

    BasicFlow flow = Mockito.spy(new BasicFlow("flowId"));

    doReturn(flow).when(nwIf).getFlow("flowId");

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    assertThat(target.getFlow(nwIf, "flowId"), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getFlow(org.o3project.odenos.component.networkinterface, java.lang.String)}
   * .
   */
  @Test
  public final void testGetFlowIfNull() {
    NetworkInterface nwIf = null;

    assertThat(target.getFlow(nwIf, "flowId"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getFlow(org.o3project.odenos.component.networkinterface, java.lang.String)}
   * .
   */
  @Test
  public final void testGetFlowIdNull() {
    NetworkInterface nwIf = new NetworkInterface(dispatcher, "nwcId");

    assertThat(target.getFlow(nwIf, null), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getFlow(org.o3project.odenos.component.networkinterface, java.lang.String)}
   * .
   */
  @Test
  public final void testGetFlowFlowNull() {
    NetworkInterface nwIf = new NetworkInterface(dispatcher, "nwcId");

    when(nwIf.getFlow("flowId")).thenReturn(null);

    assertThat(target.getFlow(nwIf, "flowId"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#finalizingNetwork(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface)}
   *
   * @throws Exception
   */
  @Test
  public final void testFinalizingNetwork() throws Exception {

    Map<String, List<String>> map = new HashMap<String, List<String>>();
    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(map).when(conversionTable, "getFlow");
    PowerMockito.doReturn(map).when(conversionTable, "getLink");
    PowerMockito.doReturn(map).when(conversionTable, "getNode");
    PowerMockito.doReturn(map).when(conversionTable, "getPort");

    NetworkInterface orgIf = new NetworkInterface(dispatcher,
        ORIGINAL_NW_ID);
    NetworkInterface aggIf = Mockito.spy(new NetworkInterface(dispatcher,
        AGGREGATED_NW_ID));

    target.finalizingNetwork(orgIf, aggIf);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#reflecteToAggregatedFromOriginal(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface)}
   * .
   * @throws Exception
   */
  @Test
  public final void testReflecteToAggregatedFromOriginal() throws Exception {
    NetworkInterface orgIf = new NetworkInterface(dispatcher,
        ORIGINAL_NW_ID);
    NetworkInterface aggIf = Mockito.spy(new NetworkInterface(dispatcher,
        AGGREGATED_NW_ID));

    target.reflecteToAggregatedFromOriginal(orgIf, aggIf);

    verify(aggIf).getTopology();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#updateFlow(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface, org.o3project.odenos.core.component.network.flow.basic.BasicFlow, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}
   * .
   * @throws Exception
   */
  @Test
  public final void testUpdateFlow() throws Exception {
    NetworkInterface orgIf = new NetworkInterface(dispatcher,
        ORIGINAL_NW_ID);
    NetworkInterface aggIf = new NetworkInterface(dispatcher,
        AGGREGATED_NW_ID);
    BasicFlow orgFlow = new BasicFlow();
    BasicFlow aggFlow = PowerMockito.spy(new BasicFlow());

    List<String> path = new ArrayList<String>();
    PowerMockito.doReturn(path).when(target, "createOriginalFlowPath",
        anyString(), anyList());
    PowerMockito.doReturn(true).when(target, "setMatch", anyList(),
        anyString());

    assertThat(target.updateFlow(orgIf, aggIf, orgFlow, aggFlow), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#updateFlow(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface, org.o3project.odenos.core.component.network.flow.basic.BasicFlow, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}
   * .
   * @throws Exception
   */
  @Test
  public final void testUpdateFlowWithFalse() throws Exception {
    NetworkInterface orgIf = null;
    NetworkInterface aggIf = null;
    BasicFlow orgFlow = new BasicFlow();
    BasicFlow aggFlow = new BasicFlow();

    assertThat((boolean) Whitebox.invokeMethod(target, "updateFlow", orgIf,
        aggIf, orgFlow, aggFlow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#updateOperStatus(org.o3project.odenos.component.networkinterface)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testUpdateOperStatus() throws Exception {
    NetworkInterface nwIf = Mockito.spy(new NetworkInterface(dispatcher,
        ORIGINAL_NW_ID));

    PathCalculator mockCal = Mockito.mock(PathCalculator.class);
    Whitebox.setInternalState(target, "pathCalculator", mockCal);

    target.updateOperStatus(nwIf);

    verify(mockCal).checkConnectivity();
    verify(nwIf).putAttributeOfNode(anyMap());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#addUnconnectedPortToAggregated(org.o3project.odenos.component.networkinterface, org.o3project.odenos.component.networkinterface, org.o3project.odenos.core.component.network.topology.Link)}
   * .
   * @throws Exception
   */
  @Test
  public final void testAddUnconnectedPortToAggregated() throws Exception {
    Port portN1P1 = new Port("0", "port1", "node1",
        "", "", new HashMap<String, String>());
    Port portN2P2 = new Port("0", "port2", "node2",
        "", "", new HashMap<String, String>());

    Link link = Mockito.spy(new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), //src
        portN2P2.getNode(), portN2P2.getId(), //dst
        new HashMap<String, String>()));

    NetworkInterface orgIf = Mockito.spy(new NetworkInterface(dispatcher,
        ORIGINAL_NW_ID));
    NetworkInterface aggIf = null;

    PathCalculator mockCal = Mockito.mock(PathCalculator.class);
    Whitebox.setInternalState(target, "pathCalculator", mockCal);

    target.addUnconnectedPortToAggregated(orgIf, aggIf, link);

    verify(mockCal).delLink(link);
    //verify(orgIf, times(2)).getPort(anyString(), anyString());

    verify(link, times(1)).getSrcNode();
    verify(link, times(1)).getSrcPort();
    verify(link, times(1)).getDstNode();
    verify(link, times(1)).getDstPort();

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#createOriginalFlowPath(java.lang.String, java.util.List<String>)}
   * .
   * @throws Exception
   */
  @Test
  public final void testCreateOriginalFlowPath() throws Exception {
    String srcNode = "node01";
    List<String> dstNodes = new ArrayList<String>();

    assertThat(Whitebox.invokeMethod(target, "createOriginalFlowPath",
        srcNode, dstNodes), is(notNullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#createOriginalFlowPath(java.lang.String, java.util.List<String>)}
   * .
   * @throws Exception
   */
  @Test
  public final void testCreateOriginalFlowPathSrtNodeNull() throws Exception {
    String srcNode = null;
    List<String> dstNodes = new ArrayList<String>();

    assertThat(Whitebox.invokeMethod(target, "createOriginalFlowPath",
        srcNode, dstNodes), is(nullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#createOriginalFlowPath(java.lang.String, java.util.List<String>)}
   * .
   * @throws Exception
   */
  @Test
  public final void testCreateOriginalFlowPathDstNodesNull() throws Exception {
    String srcNode = "node01";
    List<String> dstNodes = null;

    assertThat(Whitebox.invokeMethod(target, "createOriginalFlowPath",
        srcNode, dstNodes), is(nullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#setMatch(java.util.List<BasicFlowMatch>, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testSetMatch() throws Exception {

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(match1);
    matches.add(match2);
    matches.add(match3);
    matches.add(match4);

    String srcPort = "node_id::node1::port1";
    //FIXME If srcPort have NG format, may occar 'ArrayIndexOutOfBoundsException'

    assertThat((boolean) Whitebox.invokeMethod(target, "setMatch", matches,
        srcPort), is(true));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#setMatch(java.util.List<BasicFlowMatch>, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testSetMatchSrcPortNull() throws Exception {
    String srcPort = null;
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();

    assertThat((boolean) Whitebox.invokeMethod(target, "setMatch", matches,
        srcPort), is(false));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#setMatch(java.util.List<BasicFlowMatch>, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testSetMatchMatchNull() throws Exception {
    String srcPort = "node_id::node1::port1";
    List<BasicFlowMatch> matches = null;

    assertThat((boolean) Whitebox.invokeMethod(target, "setMatch", matches,
        srcPort), is(false));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#setMatch(java.util.List<BasicFlowMatch>, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testSetMatchMatchSize0() throws Exception {
    String srcPort = "node_id::node1::port1";
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();

    assertThat((boolean) Whitebox.invokeMethod(target, "setMatch", matches,
        srcPort), is(false));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#setActions(java.lang.String, java.util.Map<String, List<FlowAction>>, java.util.Map<String, List<FlowAction>>)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testSetActions() throws Exception {
    String nwId = "srcNetworkId";
    List<FlowAction> srcAction = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    List<FlowAction> dstAction = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    Map<String, List<FlowAction>> srcActions = new HashMap<String, List<FlowAction>>();
    Map<String, List<FlowAction>> dstActions = Mockito
        .spy(new HashMap<String, List<FlowAction>>());
    srcActions.put("node1", srcAction);
    dstActions.put("node2", dstAction);

    Whitebox.invokeMethod(target, "setActions", nwId, srcActions,
        dstActions);

    verify(dstActions).clear();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getNetworkIdByType(java.lang.String)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetNetworkIdByType() throws Exception {
    String connType = "connectionType";

    ArrayList<String> connList = new ArrayList<String>() {
      {
        add("conn01");
        add("conn02");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(connList).when(conversionTable,
        "getConnectionList", connType);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.getNetworkIdByType(connType), is("conn01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getNetworkIdByType(java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetNetworkIdByTypeSize0() throws Exception {
    String connType = "connectionType";

    assertThat(
        Whitebox.invokeMethod(target, "getNetworkIdByType", connType),
        is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getNetworkIdByType(java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetNetworkIdByTypeNull() throws Exception {
    String connType = null;

    assertThat(
        Whitebox.invokeMethod(target, "getNetworkIdByType", connType),
        is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNetworkId(java.lang.String)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetConvNetworkId() throws Exception {
    String nwId = "network01";

    ArrayList<String> nwList = new ArrayList<String>() {
      {
        add("network01");
        add("network02");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(nwList).when(conversionTable, "getNetwork", nwId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.getConvNetworkId(nwId), is("network01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNetworkId(java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvNetworkIdSize0() throws Exception {
    String nwId = "network01";

    assertThat(target.getConvNetworkId(nwId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNetworkId(java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvNetworkIdNull() throws Exception {
    String nwId = null;

    assertThat(target.getConvNetworkId(nwId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNodeId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetConvNodeId() throws Exception {
    String nwId = "network01";
    String nodeId = "node01";

    ArrayList<String> nodeList = new ArrayList<String>() {
      {
        add("node01");
        add("node02");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(nodeList).when(conversionTable, "getNode", nwId,
        nodeId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.getConvNodeId(nwId, nodeId), is("node01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNodeId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvNodeIdSize0() throws Exception {
    String nwId = "network01";
    String nodeId = "node01";

    assertThat(target.getConvNodeId(nwId, nodeId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNodeId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvNodeIdNull() throws Exception {
    String nwId = "network01";
    String nodeId = null;

    assertThat(target.getConvNodeId(nwId, nodeId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvNodeId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvNodeIdNetworkNull() throws Exception {
    String nwId = null;
    String nodeId = "node01";

    assertThat(target.getConvNodeId(nwId, nodeId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetConvPortId() throws Exception {
    String nwId = "network01";
    String nodeId = "node01";
    String portId = "port01";

    ArrayList<String> portList = new ArrayList<String>() {
      {
        add("port01");
        add("port02");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(portList).when(conversionTable, "getPort", nwId,
        nodeId, portId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.getConvPortId(nwId, nodeId, portId), is("port01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdNetworkNull() throws Exception {
    String nwId = null;
    String nodeId = "node01";
    String portId = "port01";

    assertThat(target.getConvPortId(nwId, nodeId, portId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdNodeNull() throws Exception {
    String nwId = "network01";
    String nodeId = null;
    String portId = "port01";

    assertThat(target.getConvPortId(nwId, nodeId, portId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdPortNull() throws Exception {
    String nwId = "network01";
    String nodeId = "node01";
    String portId = null;

    assertThat(target.getConvPortId(nwId, nodeId, portId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdSize0() throws Exception {
    String nwId = "network01";
    String nodeId = "node01";
    String portId = "port01";

    assertThat(target.getConvPortId(nwId, nodeId, portId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvFlowId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetConvFlowId() throws Exception {
    String nwId = "network01";
    String flowId = "flow01";

    ArrayList<String> flowList = new ArrayList<String>() {
      {
        add("flow01");
        add("flow02");
      }
    };

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());
    PowerMockito.doReturn(flowList).when(conversionTable, "getFlow", nwId,
        flowId);
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    assertThat(target.getConvFlowId(nwId, flowId), is("flow01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvFlowId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvFlowIdNetworkNull() throws Exception {
    String nwId = null;
    String flowId = "flow01";

    assertThat(target.getConvFlowId(nwId, flowId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvFlowId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvFlowIdFlowNull() throws Exception {
    String nwId = "network01";
    String flowId = null;

    assertThat(target.getConvFlowId(nwId, flowId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.component.aggregator.Aggregator#getConvFlowId(java.lang.String, java.lang.String)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvFlowIdSize0() throws Exception {
    String nwId = "network01";
    String flowId = "flow01";

    assertThat(target.getConvFlowId(nwId, flowId), is(nullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#getConvPortIdByMatches(java.lang.String, java.util.List<BasicFlowMatch>)}
   * .
   * @throws Exception
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetConvPortIdByMatches() throws Exception {
    String nwId = "network01";

    ConversionTable conversionTable = PowerMockito
        .spy(new ConversionTable());

    ArrayList<String> portList = new ArrayList<String>() {
      {
        add("port01");
        add("port02");
      }
    };

    PowerMockito.doReturn(portList).when(conversionTable, "getPort", nwId,
        "node1", "port1");
    PowerMockito.doReturn(conversionTable).when(target, "conversionTable");

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(match1);
    matches.add(match2);
    matches.add(match3);
    matches.add(match4);

    assertThat(Whitebox.invokeMethod(target, "getConvPortIdByMatches",
        nwId, matches), is(notNullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#getConvPortIdByMatches(java.lang.String, java.util.List<BasicFlowMatch>)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdByMatchesSize0() throws Exception {
    String nwId = "network01";
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();

    assertThat(Whitebox.invokeMethod(target, "getConvPortIdByMatches",
        nwId, matches), is(nullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#getConvPortIdByActions(java.lang.String, java.util.Map<String, List<FlowAction>>)}
   * .
   * @throws Exception
   */
  @SuppressWarnings({ "serial" })
  @Test
  public final void testGetConvPortIdByActions() throws Exception {
    String nwId = "network01";

    List<FlowAction> action1 = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    List<FlowAction> action2 = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    Map<String, List<FlowAction>> actions = new HashMap<String, List<FlowAction>>();
    actions.put("node1", action1);
    actions.put("node2", action2);

    assertThat(Whitebox.invokeMethod(target, "getConvPortIdByActions",
        nwId, actions), is(notNullValue()));

  }

  /**
   * Test method for
   * {@literal org.o3project.odenos.component.aggregator.Aggregator#getConvPortIdByActions(java.lang.String, java.util.Map<String, List<FlowAction>>)}
   * .
   * @throws Exception
   */
  @Test
  public final void testGetConvPortIdByActionsNoList() throws Exception {
    String nwId = "network01";
    Map<String, List<FlowAction>> actions = new HashMap<String, List<FlowAction>>();

    List<String> dstPorts = Whitebox.invokeMethod(target,
        "getConvPortIdByActions", nwId, actions);
    assertThat(dstPorts.size(), is(0));

  }

}
