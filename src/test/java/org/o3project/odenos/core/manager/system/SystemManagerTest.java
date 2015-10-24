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

package org.o3project.odenos.core.manager.system;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
import org.msgpack.type.Value;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.manager.component.ComponentType;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
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
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemManager.class })
@PowerMockIgnore({"javax.management.*"})
public class SystemManagerTest {
  private MessageDispatcher mockDispatcher = PowerMockito
      .mock(MessageDispatcher.class);
  private ObjectProperty mockEventManagerProp = PowerMockito
      .mock(ObjectProperty.class);

  private static final String SYSTEM_MGR_ID = "systemmanager";
  private static final String EVENT_MGR_ID = "eventmanager";
  private static final String EVT_BASE_URI = "odenos://127.0.0.1:12345/"
      + EVENT_MGR_ID;

  private static final String COMP_MGR_ID1 = "compmgr_java1";
  private static final String[] COMP_TYPES1 = { "Aggregator", "Federator",
      "Network" };
  private static final String AGGREGATOR_ID = "aggregator";
  private static final String NETWORK_ID1 = "network1";

  private static final String COMP_MGR_ID2 = "compmgr_java2";
  private static final String[] COMP_TYPES2 = { "Slicer", "LinkLayerizer",
      "Network" };
  private static final String SLICER_ID = "slicer";
  private static final String NETWORK_ID2 = "network2";

  private static final String CONN_ID1 = "connId1";
  private static final String CONN_ID2 = "connId2";

  HashSet<String> componentMgrsSet = new HashSet<String>();
  HashMap<String, String> componentStateList = new HashMap<String, String>();
  ObjectPropertiesHash componetsObjectProperties = new ObjectPropertiesHash();
  HashMap<String, HashSet<String>> allComponentTypes = new HashMap<String, HashSet<String>>();
  HashMap<String, String> mapCompAndCompMgr = new HashMap<String, String>();
  HashMap<String, ComponentConnection> connectionTable =
      new HashMap<String, ComponentConnection>();

  SystemManager target = null;

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
    // eventManagerProp mock
    Mockito.when(mockEventManagerProp.getObjectId()).thenReturn(
        EVENT_MGR_ID);
    Mockito.when(mockEventManagerProp.getBaseUri())
        .thenReturn(EVT_BASE_URI);

    target = new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp);

    // initialize private field of SystemManager
    // set ComponentManagers
    componentMgrsSet.add(COMP_MGR_ID1);
    componentMgrsSet.add(COMP_MGR_ID2);
    WhiteboxImpl.setInternalState(target,
        "componentMgrsSet",
        componentMgrsSet);

    // set allComponentTypes
    for (String type : COMP_TYPES1) {
      if (!allComponentTypes.containsKey(type)) {
        HashSet<String> compMgrSet = new HashSet<String>();
        compMgrSet.add(COMP_MGR_ID1);
        allComponentTypes.put(type, compMgrSet);
      } else {
        allComponentTypes.get(type).add(COMP_MGR_ID1);
      }
    }
    for (String type : COMP_TYPES2) {
      if (!allComponentTypes.containsKey(type)) {
        HashSet<String> compMgrSet = new HashSet<String>();
        compMgrSet.add(COMP_MGR_ID2);
        allComponentTypes.put(type, compMgrSet);
      } else {
        allComponentTypes.get(type).add(COMP_MGR_ID2);
      }
    }

    // set componentStateList
    componentStateList.put(AGGREGATOR_ID, ObjectProperty.State.RUNNING);
    componentStateList.put(NETWORK_ID1, ObjectProperty.State.RUNNING);
    componentStateList.put(SLICER_ID, ObjectProperty.State.RUNNING);
    componentStateList.put(NETWORK_ID2, ObjectProperty.State.RUNNING);

    // set componetsObjectProperties
    componetsObjectProperties.put(AGGREGATOR_ID, new ObjectProperty("Aggregator", AGGREGATOR_ID));

    // set mapCompAndCompMgr
    mapCompAndCompMgr.put(AGGREGATOR_ID, COMP_MGR_ID1);
    mapCompAndCompMgr.put(NETWORK_ID1, COMP_MGR_ID1);
    mapCompAndCompMgr.put(SLICER_ID, COMP_MGR_ID2);
    mapCompAndCompMgr.put(NETWORK_ID2, COMP_MGR_ID2);

    // set connectionTable
    ComponentConnection connection1 = new ComponentConnectionLogicAndNetwork(
        CONN_ID1,
        ComponentConnectionLogicAndNetwork.TYPE,
        ComponentConnection.State.RUNNING,
        AGGREGATOR_ID,
        NETWORK_ID1);
    ComponentConnection connection2 = new ComponentConnectionLogicAndNetwork(
        CONN_ID2,
        ComponentConnectionLogicAndNetwork.TYPE,
        ComponentConnection.State.RUNNING,
        SLICER_ID,
        NETWORK_ID2);
    connectionTable.put(CONN_ID1, connection1);
    connectionTable.put(CONN_ID2, connection2);
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
   * {@link org.o3project.odenos.core.manager.system.SystemManager#SystemManager(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher, org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public final void testSystemManager() {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    assertThat(target, is(instanceOf(SystemManager.class)));
    assertThat((ObjectProperty) WhiteboxImpl.getInternalState(target,
        "eventManagerProperty"),
        is(mockEventManagerProp));
    ObjectProperty obj = WhiteboxImpl.getInternalState(target,
        "objectProperty");
    assertThat(obj.getObjectState(), is(ObjectProperty.State.RUNNING));
    assertThat(WhiteboxImpl.getInternalState(target, "parser"),
        is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnRequestWithSuccess() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    // create Request Data
    ObjectProperty prop =
        new ObjectProperty("ComponentManager",
            COMP_MGR_ID1);
    String compTypes = "Network,Aggregator,Slicer";
    prop.setProperty(ObjectProperty.PropertyNames.COMPONENT_TYPES,
        compTypes);

    Request req = new Request(SYSTEM_MGR_ID,
        Request.Method.PUT,
        "component_managers/" + prop.getObjectId(),
        "txid",
        prop);

    Response dummyResp = new Response(Response.OK,
        prop);

    PowerMockito.doReturn(dummyResp).when(target, "putComponentManagers",
        prop.getObjectId(), prop);

    // Call test target method
    Response resp = target.onRequest(req);

    // assertion
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(ObjectProperty.class)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnRequestParsedNullNoMatch() throws Exception {
    // create Request Data
    ObjectProperty prop =
        new ObjectProperty("ComponentManager",
            COMP_MGR_ID1);
    String compTypes = "Network,Aggregator,Slicer";
    prop.setProperty(ObjectProperty.PropertyNames.COMPONENT_TYPES,
        compTypes);

    Request req = new Request(SYSTEM_MGR_ID,
        Request.Method.POST,
        "component?managers?test",
        "txid",
        prop);

    // Call test target method
    Response resp = target.onRequest(req);

    // assertion
    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnRequestParsedNullCompileMatch() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    // create Request Data
    ObjectProperty prop =
        new ObjectProperty("ComponentManager",
            COMP_MGR_ID1);
    Request req = new Request(SYSTEM_MGR_ID,
        Request.Method.POST,
        "components/network1/topology/nodes",
        "txid",
        prop);

    Response dummyResp = new Response(Response.OK, null);
    PowerMockito.doReturn(dummyResp).when(target, "transferComponent",
        anyString(), anyString(), eq(Request.Method.POST), eq("txid"),
        (Value) anyObject());

    // Call test target method
    Response resp = target.onRequest(req);

    // assertion
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBodyValue(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnRequestResponseNull() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    // create Request Data
    ObjectProperty prop =
        new ObjectProperty("ComponentManager",
            COMP_MGR_ID1);

    Request req = new Request(SYSTEM_MGR_ID,
        Request.Method.PUT,
        "component_managers/" + prop.getObjectId(),
        "txid",
        prop);

    PowerMockito.doReturn(null).when(target, "putComponentManagers",
        prop.getObjectId(), prop);

    // Call test target method
    Response resp = target.onRequest(req);

    // assertion
    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
    assertThat(resp.getBodyValue(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#createParser()}.
   */
  @Test
  public final void testCreateParser() throws Exception {
    @SuppressWarnings("unchecked")
    RequestParser<String> result = (RequestParser<String>) Whitebox
        .invokeMethod(target, "createParser");

    Request req = new Request(SYSTEM_MGR_ID, Request.Method.PUT,
        "component_managers/<compmgr_id>", "txid" ,null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "component_managers", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "component_managers/<compmgr_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.DELETE,
        "component_managers/<compmgr_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET, "event_manager",
        "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET, "component_types",
        "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "component_types/<type>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET, "components", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "components/<comp_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.POST, "components",
        "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.DELETE,
        "components/<comp_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.POST, "connections",
        "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET, "connections",
        "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "connections/<conn_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.PUT,
        "connections/<conn_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.DELETE,
        "connections/<conn_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "objects/<object_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.GET,
        "base_uri/<object_id>", "txid", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(SYSTEM_MGR_ID, Request.Method.PUT,
        "objects/<object_id>", "txid", null);
    assertThat(result.parse(req), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#onFinalize()}.
   */
  @Test
  public final void testOnFinalize() {
    target =
        Mockito.spy(new SystemManager("sysmgrId", mockDispatcher,
            mockEventManagerProp));

    target.onFinalize();

    verify(target, times(1)).onFinalize();

    Map<String, String> map = new HashMap<String, String>();
    map = WhiteboxImpl.getInternalState(target, "componentStateList");
    assertThat(map.isEmpty(), is(true));
    map = WhiteboxImpl.getInternalState(target, "mapCompAndCompMgr");
    assertThat(map.isEmpty(), is(true));

    Set<String> set = new HashSet<String>();
    set = WhiteboxImpl.getInternalState(target, "componentMgrsSet");
    assertThat(set.isEmpty(), is(true));

    Map<String, HashSet<String>> type = new HashMap<String, HashSet<String>>();
    type = WhiteboxImpl.getInternalState(target, "allComponentTypes");
    assertThat(type.isEmpty(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getDestinationCompId(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetDestinationCompId() throws Exception {
    String path = "components/network1/topology/nodes";

    assertThat((String) Whitebox.invokeMethod(target,
        "getDestinationCompId", path), is("network1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getDestinationCompId(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetDestinationCompIdPathNull() throws Exception {
    String path = null;

    assertThat(Whitebox.invokeMethod(target, "getDestinationCompId", path),
        is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getDestinationPath(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetDestinationPath() throws Exception {
    String path = "components/network1/topology/nodes";

    assertThat((String) Whitebox.invokeMethod(target, "getDestinationPath",
        path), is("topology/nodes"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getDestinationPath(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetDestinationPathNull() throws Exception {
    String path = null;

    assertThat(Whitebox.invokeMethod(target, "getDestinationPath", path),
        is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#monitorAliveOfComponents(long)}.
   *
   * @throws Exception
   */
  @Test
  public final void testMonitorAliveOfComponents() throws Exception {
    long period = TimeUnit.MINUTES.toMinutes(30);

    target = Mockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    Whitebox.invokeMethod(target, "monitorAliveOfComponents", period);

    verify(target).getProperty();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#CheckComponentsTask(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testCheckComponentsTask() throws Exception {
    String testUri = "test/componentmanager/test";

    SystemManager.CheckComponentsTask task = Mockito
        .spy(target.new CheckComponentsTask(testUri));

    assertThat((String) WhiteboxImpl.getInternalState(task, "baseUri"),
        is(testUri));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#run()}.
   *
   * @throws Exception
   */
  @Test
  public final void testRun() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("key1", "value1");
    mockList.put("key2", "value2");
    mockList.put("key3", "value3");

    String testUri = "test/componentmanager/test";

    Whitebox.setInternalState(target, "componentStateList", mockList);
    Response mockres = PowerMockito.spy(new Response(Response.BAD_REQUEST,
        null));
    PowerMockito.doReturn(mockres).when(target)
        .request("key2", Request.Method.GET, testUri, "txid", null);
    SystemManager.CheckComponentsTask task = PowerMockito
        .spy(target.new CheckComponentsTask(testUri));

    task.run();

    HashMap<String, String> resultList = Whitebox.getInternalState(target,
        "componentStateList");
    assertThat(resultList.get("key1"), is(not(ObjectProperty.State.ERROR)));
    assertThat(resultList.get("key2"), is(ObjectProperty.State.ERROR));
    assertThat(resultList.get("key3"), is(not(ObjectProperty.State.ERROR)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#run()}.
   *
   * @throws Exception
   */
  @Test
  public final void testRunWithException() throws Exception {
    target = Mockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("key1", "value1");
    mockList.put("key2", "value2");
    mockList.put("key3", "value3");
    Whitebox.setInternalState(target, "componentStateList", mockList);

    String testUri = "test/componentmanager/test";

    SystemManager.CheckComponentsTask task = Mockito
        .spy(target.new CheckComponentsTask(testUri));
    Mockito.doThrow(new Exception())
        .when(target)
        .request(anyString(), eq(Request.Method.GET), anyString(), eq("txid"),
            eq(null));

    task.run();

    HashMap<String, String> resultList = Whitebox.getInternalState(target,
        "componentStateList");
    assertThat(resultList, is(mockList));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteAllComponentManagers()}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteAllComponentManagers() throws Exception {
    HashSet<String> mockSet = Mockito.spy(new HashSet<String>());
    mockSet.add("compMgr01");
    mockSet.add("compMgr02");
    mockSet.add("compMgr03");
    mockSet.add("compMgr04");

    WhiteboxImpl.setInternalState(target, "componentMgrsSet", mockSet);

    Whitebox.invokeMethod(target, "deleteAllComponentManagers");

    HashSet<String> resultSet = Whitebox.getInternalState(target,
        "componentMgrsSet");
    assertThat(resultSet.isEmpty(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getObjectById()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetObjectById() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("key01", "value01");
    mockList.put("key02", "value02");
    mockList.put("key03", "value03");
    mockList.put("key04", "value04");
    ObjectProperty mockobj = new ObjectProperty("objtype", "comp01");

    String id = "key02";

    PowerMockito.doReturn(mockobj).when(target,
        "getPropertyFromOtherComponent", id);
    Whitebox.setInternalState(target, "componentStateList", mockList);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getObjectById", id);

    assertThat(ret.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getObjectById()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetObjectByIdNotFound() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String id = "objId";
    Response ret = null;
    PowerMockito.doReturn(new ObjectProperty("objtype", "objid")).when(
        target, "getPropertyFromOtherComponent", id);

    ret = (Response) Whitebox.invokeMethod(target, "getObjectById", id);

    assertThat(ret.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponentManagers(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentManagers() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    ObjectProperty obj = Mockito
        .spy(new ObjectProperty("objType", "objId"));
    when(obj.getProperty(ObjectProperty.PropertyNames.COMPONENT_TYPES))
        .thenReturn("onRequest: GET, systemmanager");
    String id = "objId";
    Response ret = null;
    PowerMockito.doReturn(null).when(target,
        "getPropertyFromOtherComponent", id);

    ret = (Response) Whitebox.invokeMethod(target, "putComponentManagers",
        obj.getObjectId(), obj);

    assertThat(ret.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponentManagers(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentManagersConflict() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashSet<String> mockSet = new HashSet<String>();
    mockSet.add("comp01");
    mockSet.add("comp02");

    ObjectProperty obj = new ObjectProperty("objtype", "comp01");
    String id = "objId";
    Response ret = null;
    PowerMockito.doReturn(null).when(target,
        "getPropertyFromOtherComponent", id);

    Whitebox.setInternalState(target, "componentMgrsSet", mockSet);

    ret = (Response) Whitebox.invokeMethod(target, "putComponentManagers",
        obj.getObjectId(), obj);

    assertThat(ret.statusCode, is(Response.CONFLICT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponentManagers(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentManagersCompTypeNull() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    ObjectProperty obj = new ObjectProperty("objtype", "comp01");

    String id = "objId";
    Response ret = null;
    PowerMockito.doReturn(null).when(target,
        "getPropertyFromOtherComponent", id);

    ret = (Response) Whitebox.invokeMethod(target, "putComponentManagers",
        obj.getObjectId(), obj);

    assertThat(ret.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.manager.system.SystemManager#updateCreatableComponentType(java.lang.String, java.util.List<String>)}.
   *
   * @throws Exception
   */
  @Test
  public final void testUpdateCreatableComponentType() throws Exception {
    target = Mockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");

    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    mockTypes.put("type01", testTypes);

    String compMgrId = "compMgr";

    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);

    Whitebox.invokeMethod(target, "updateCreatableComponentType",
        compMgrId, typeList);

    HashSet<String> retSet = mockTypes.get("type01");
    assertThat(retSet.add("compMgr"), is(false));
    retSet = mockTypes.get("type02");
    assertThat(retSet.add("compMgr"), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentManagers()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentManagers() throws Exception {
    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentManagers");

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentManager(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentManager() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashSet<String> mockSet = Mockito.spy(new HashSet<String>());
    mockSet.add("compMgr01");
    mockSet.add("compMgr02");
    mockSet.add("compMgr03");
    mockSet.add("compMgr04");
    WhiteboxImpl.setInternalState(target, "componentMgrsSet", mockSet);

    String compMgrId = "compMgr01";

    ObjectProperty op = new ObjectProperty("objType", "objId");
    PowerMockito.doReturn(op).when(target, "getPropertyFromOtherComponent",
        compMgrId);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentManager", compMgrId);

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentManager(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentManagerNotFound() throws Exception {
    String compMgrId = "mgrId";

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentManager", compMgrId);

    assertThat(ret.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponentManager(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentManager() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    HashSet<String> mockSet = Mockito.spy(new HashSet<String>());
    mockSet.add("compMgr01");
    mockSet.add("compMgr02");
    mockSet.add("compMgr03");
    mockSet.add("compMgr04");
    WhiteboxImpl.setInternalState(target, "componentMgrsSet", mockSet);

    String compMgrId = "compMgr01";

    Response ret = (Response) Whitebox.invokeMethod(target,
        "deleteComponentManager", compMgrId);
    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));
    PowerMockito.verifyPrivate(target).invoke(
        "getPropertyFromOtherComponent", compMgrId);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponentManager(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentManagerWithNoTarget() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compMgrId = "mgrId";

    Response ret = (Response) Whitebox.invokeMethod(target,
        "deleteComponentManager", compMgrId);

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));
    PowerMockito.verifyPrivate(target, never()).invoke(
        "getPropertyFromOtherComponent", compMgrId);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteFromCreatableComponentType(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteFromCreatableComponentType() throws Exception {
    String compMgrId = "mgrId";
    HashMap<String, HashSet<String>> resultTypes = new HashMap<String, HashSet<String>>();

    Whitebox.invokeMethod(target, "deleteFromCreatableComponentType",
        compMgrId);

    resultTypes = Whitebox.getInternalState(target, "allComponentTypes");
    assertThat(resultTypes.isEmpty(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getEventManager()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetEventManager() throws Exception {
    Response ret = (Response) Whitebox.invokeMethod(target,
        "getEventManager");

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(instanceOf(ObjectProperty.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentTypes(String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentTypesOfComponentMng() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    PowerMockito.doReturn(new Response(Response.OK, null)).when(target)
        .request("compId", Request.Method.GET, "component_types", "txid", null);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentTypes", "compId");


    assertThat(ret.statusCode, is(Response.OK));
  }
  
  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentTypes()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentTypes() throws Exception {
    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentTypes");

    assertThat(ret.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentManagersByType(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentManagersByType() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");
    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    testTypes.add("testType");
    mockTypes.put("type01", testTypes);
    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);
    ObjectProperty mockObj = new ObjectProperty("objType", "objId");
    PowerMockito.doReturn(mockObj).when(target,
        "getPropertyFromOtherComponent", "testType");

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentManagersByType", "type01");

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(instanceOf(ObjectPropertyList.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponentManagersByType(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentManagersByTypeNotFound() throws Exception {
    target = Mockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    HashMap<String, HashSet<String>> mocktypes = new HashMap<String, HashSet<String>>();

    for (String type : COMP_TYPES1) {
      if (!mocktypes.containsKey(type)) {
        HashSet<String> compMgrSet = new HashSet<String>();
        compMgrSet.add(COMP_MGR_ID1);
        mocktypes.put(type, compMgrSet);
      } else {
        mocktypes.get(type).add(COMP_MGR_ID1);
      }
    }
    for (String type : COMP_TYPES2) {
      if (!mocktypes.containsKey(type)) {
        HashSet<String> compMgrSet = new HashSet<String>();
        compMgrSet.add(COMP_MGR_ID2);
        mocktypes.put(type, compMgrSet);
      } else {
        mocktypes.get(type).add(COMP_MGR_ID2);
      }
    }

    WhiteboxImpl.setInternalState(target, "allComponentTypes", mocktypes);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "getComponentManagersByType", "Aggregator");

    assertThat(ret.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponents()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponents() throws Exception {
    Response ret = (Response) Whitebox
        .invokeMethod(target, "getComponents");

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(instanceOf(ObjectPropertiesHash.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponent() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compId = "compId";

    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("compId", "value01");
    Whitebox.setInternalState(target, "componentStateList", mockList);
    ObjectProperty mockObj = new ObjectProperty("objType", "objId");
    PowerMockito.doReturn(mockObj).when(target,
        "getPropertyFromOtherComponent", compId);

    Response ret = (Response) Whitebox.invokeMethod(target, "getComponent",
        compId);

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(instanceOf(ObjectProperty.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetComponentNotFound() throws Exception {
    String compId = "compId";

    Response ret = (Response) Whitebox.invokeMethod(target, "getComponent",
        compId);

    assertThat(ret.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponent() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");
    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    testTypes.add("testType");
    mockTypes.put("type01", testTypes);
    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);

    ObjectProperty obj = new ObjectProperty("type01", "testType");

    PowerMockito.doReturn(new Response(Response.CREATED, obj)).when(target)
        .request("testType", Request.Method.PUT, "components/" + obj.getObjectId(), "txid", obj);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "postComponent", obj);

    assertThat(ret.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#putComponent(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPutComponent() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");
    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    testTypes.add("testType");
    mockTypes.put("type01", testTypes);
    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);

    ObjectProperty obj = new ObjectProperty("type01", "testType");

    PowerMockito.doReturn(new Response(Response.CREATED, obj)).when(target)
        .request("testType", Request.Method.PUT, "components/" + obj.getObjectId(), "txid", obj);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "putComponent", obj.getObjectId(), obj);

    assertThat(ret.statusCode, is(Response.CREATED));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"),
        is(instanceOf(ObjectProperty.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentWithBadRequest() throws Exception {
    ObjectProperty obj = new ObjectProperty("type01", "testType");

    Response ret = (Response) Whitebox.invokeMethod(target,
        "postComponent", obj);

    assertThat(ret.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentWithException() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");
    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    testTypes.add("testType");
    mockTypes.put("type01", testTypes);

    ObjectProperty obj = new ObjectProperty("type01", "testType");

    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);
    PowerMockito.doThrow(new Exception()).when(target)
        .request("testType", Request.Method.POST, "components", "txid", obj);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "postComponent", obj);

    assertThat(ret.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostComponentWithObjPropNull() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    List<String> typeList = new ArrayList<String>();
    typeList.add("type01");
    typeList.add("type02");
    typeList.add("type03");
    HashMap<String, HashSet<String>> mockTypes = new HashMap<String, HashSet<String>>();
    HashSet<String> testTypes = new HashSet<String>();
    testTypes.add("testType");
    mockTypes.put("type01", testTypes);
    Whitebox.setInternalState(target, "allComponentTypes", mockTypes);

    ObjectProperty obj = new ObjectProperty("type01", "testType");

    PowerMockito.doReturn(new Response(Response.OK, null)).when(target)
        .request("testType", Request.Method.POST, "components", "txid", obj);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "postComponent", obj);

    assertThat(ret.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponent() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compId = "compId";
    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put(compId, "value01");
    Whitebox.setInternalState(target, "componentStateList", mockList);
    PowerMockito.doReturn(new Response(Response.OK, null))
        .when(target, "deleteComponentFromComponentManager",
            anyString(), anyString());

    Response ret = (Response) Whitebox.invokeMethod(target,
        "deleteComponent", compId);

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentWithForbidden() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compId = "compId";
    PowerMockito.doReturn(true).when(target, "hasConnection", compId);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "deleteComponent", compId);

    assertThat(ret.statusCode, is(Response.FORBIDDEN));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentWithNotFound() throws Exception {
    String compId = "compId";

    Response ret = (Response) Whitebox.invokeMethod(target,
        "deleteComponent", compId);

    assertThat(ret.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#transferComponent(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, org.msgpack.type.Value)}.
   *
   * @throws Exception
   */
  @Test
  public final void testTransferComponent() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String id = "id";
    String path = "path";
    Request.Method method = Request.Method.GET;
    String txid = "txid";
    Value value = Mockito.mock(Value.class);
    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("id", "value01");
    Whitebox.setInternalState(target, "componentStateList", mockList);
    PowerMockito.doReturn(new Response(Response.OK, null)).when(target)
        .request(id, method, path, "txid", value);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "transferComponent", id, path, method, txid, value);

    assertThat(ret.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#transferComponent(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, org.msgpack.type.Value)}.
   *
   * @throws Exception
   */
  @Test
  public final void testTransferComponentWithException() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String id = "id";
    String path = "path";
    Request.Method method = Request.Method.GET;
    String txid = "txid";
    Value value = Mockito.mock(Value.class);
    HashMap<String, String> mockList = new HashMap<String, String>();
    mockList.put("id", "value01");
    Whitebox.setInternalState(target, "componentStateList", mockList);
    PowerMockito.doThrow(new Exception()).when(target)
        .request(id, method, path, "txid", value);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "transferComponent", id, path, method, txid, value);

    assertThat(ret.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#transferComponent(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, org.msgpack.type.Value)}.
   *
   * @throws Exception
   */
  @Test
  public final void testTransferComponentWithBadRequest() throws Exception {
    String id = "id";
    String path = "path";
    Request.Method method = Request.Method.GET;
    String txid = "txid";
    Value value = Mockito.mock(Value.class);

    Response ret = (Response) Whitebox.invokeMethod(target,
        "transferComponent", id, path, method, txid, value);

    assertThat(ret.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(ret, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getPropertyFromOtherComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetPropertyFromOtherComponentFailed()
      throws Exception {
    String compId = "compId";

    assertThat(Whitebox.invokeMethod(target,
        "getPropertyFromOtherComponent", compId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getPropertyFromOtherComponent(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetPropertyFromOtherComponentWithException()
      throws Exception {
    String compId = "compId";

    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    PowerMockito.doThrow(new Exception()).when(target)
        .request(compId, Request.Method.GET, "property", "txid", null);

    assertThat(Whitebox.invokeMethod(target,
        "getPropertyFromOtherComponent", compId), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponentFromComponentManager(java.lang.String, java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentFromComponentManager()
      throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compMgrId = "compMgrId";
    String compId = "compId";
    PowerMockito
        .doReturn(new Response(Response.OK, null))
        .when(target)
        .request(compMgrId, Request.Method.DELETE, "components/compId",
            "txid", null);

    Response resp = Whitebox.invokeMethod(target,
        "deleteComponentFromComponentManager", compMgrId, compId);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponentFromComponentManager(java.lang.String, java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentFromComponentManagerWithError()
      throws Exception {
    String compMgrId = "compMgrId";
    String compId = "compId";

    Response resp = Whitebox.invokeMethod(target,
        "deleteComponentFromComponentManager", compMgrId, compId);

    assertThat(resp.statusCode, is(not(Response.OK)));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteComponentFromComponentManager(java.lang.String, java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteComponentFromComponentManagerWithException()
      throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String compMgrId = "compMgrId";
    String compId = "compId";

    PowerMockito.doThrow(new Exception()).when(target)
        .request(compMgrId, Request.Method.DELETE, "components/", "txid", null);

    Response resp = Whitebox.invokeMethod(target,
        "deleteComponentFromComponentManager", compMgrId, compId);

    assertThat(resp.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#componentManagerChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty, org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testComponentManagerChanged() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String action = "action";
    ObjectProperty prev = new ObjectProperty("objtype", "objid");
    ObjectProperty curr = new ObjectProperty("objtype", "objid");

    Whitebox.invokeMethod(target, "componentManagerChanged", action, prev,
        curr);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "publishEvent", eq(ComponentManagerChanged.TYPE), anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#componentManagerChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty, org.o3project.odenos.remoteobject.ObjectProperty)}.
   *
   * @throws Exception
   */
  @Test
  public final void testComponentManagerChangedWithException()
      throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String action = "action";
    ObjectProperty prev = new ObjectProperty("objtype", "objid");
    ObjectProperty curr = new ObjectProperty("objtype", "objid");

    PowerMockito.doThrow(new Exception()).when(target, "publishEvent",
        anyString(), anyObject());

    Whitebox.invokeMethod(target, "componentManagerChanged", action, prev,
        curr);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postConnections(org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostConnections() throws Exception {
    Whitebox.setInternalState(target, "mapCompAndCompMgr",
        mapCompAndCompMgr);
    ComponentConnectionLogicAndNetwork body = new ComponentConnectionLogicAndNetwork(
        AGGREGATOR_ID, ComponentConnectionLogicAndNetwork.TYPE,
        "state", SLICER_ID, NETWORK_ID2);

    Response resp = Whitebox.invokeMethod(target, "postConnections", body);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(ComponentConnectionLogicAndNetwork.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postConnections(org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostConnectionsWithBadRequest() throws Exception {
    ComponentConnection body = new ComponentConnection("id", "type",
        "state");

    Response resp = Whitebox.invokeMethod(target, "postConnections", body);

    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(String.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#postConnections(org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPostConnectionsWithConflict() throws Exception {
    Whitebox.setInternalState(target, "mapCompAndCompMgr",
        mapCompAndCompMgr);
    Whitebox.setInternalState(target, "connectionTable", connectionTable);
    ComponentConnectionLogicAndNetwork body = new ComponentConnectionLogicAndNetwork(
        CONN_ID1, ComponentConnectionLogicAndNetwork.TYPE, "state",
        SLICER_ID, NETWORK_ID2);

    Response resp = Whitebox.invokeMethod(target, "postConnections", body);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(ComponentConnectionLogicAndNetwork.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#putConnections(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPutConnections() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);

    ComponentConnectionLogicAndNetwork body = new ComponentConnectionLogicAndNetwork(
        "id",
        ComponentConnectionLogicAndNetwork.TYPE,
        "state",
        ComponentConnectionLogicAndNetwork.LOGIC_ID,
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    Response resp = Whitebox.invokeMethod(target, "putConnections",
        CONN_ID1, body);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(ComponentConnectionLogicAndNetwork.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#putConnections(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPutConnectionsWithNotFound() throws Exception {
    String connId = "connId";
    ComponentConnection body = new ComponentConnection("id", "type",
        "state");

    Response resp = Whitebox.invokeMethod(target, "putConnections", connId,
        body);

    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#putConnections(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPutConnectionsWithBadRequest() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);
    ComponentConnection body = new ComponentConnection("id", "type",
        "state");

    Response resp = Whitebox.invokeMethod(target, "putConnections",
        CONN_ID1, body);

    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(String.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getConnections()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetConnections() throws Exception {
    Response resp = Whitebox.invokeMethod(target, "getConnections");

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(TreeMap.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getConnection(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetConnection() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);

    Response resp = Whitebox
        .invokeMethod(target, "getConnection", CONN_ID1);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(ComponentConnection.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getConnection(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetConnectionWithNotFound() throws Exception {
    String connId = "connId";

    Response resp = Whitebox.invokeMethod(target, "getConnection", connId);

    assertThat(resp.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteConnections(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteConnections() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);

    assertThat(((Response) (Whitebox.invokeMethod(target, "getConnection",
        CONN_ID1))).statusCode, is(Response.OK));

    Response resp = Whitebox.invokeMethod(target, "deleteConnections",
        CONN_ID1);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(nullValue()));
    assertThat(((Response) (Whitebox.invokeMethod(target, "getConnection",
        CONN_ID1))).statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteConnections(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteConnectionsNotExist() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);
    String connId = "connId";

    assertThat(((Response) (Whitebox.invokeMethod(target, "getConnection",
        connId))).statusCode, is(Response.NOT_FOUND));

    Response resp = Whitebox.invokeMethod(target, "deleteConnections",
        connId);

    assertThat(resp.statusCode, is(Response.NOT_FOUND));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"), is(notNullValue()));
    assertThat(((Response) (Whitebox.invokeMethod(target, "getConnection",
        connId))).statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#hasConnection(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testHasConnectionWithLogicId() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);

    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        AGGREGATOR_ID), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#hasConnection(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testHasConnectionWithNetworkId() throws Exception {
    Whitebox.setInternalState(target, "connectionTable", connectionTable);

    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        NETWORK_ID2), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#hasConnection(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testHasConnectionFalse() throws Exception {
    String id = "id";

    assertThat(
        (boolean) Whitebox.invokeMethod(target, "hasConnection", id),
        is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#componentConnectionChanged(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection, org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testComponentConnectionChanged() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    String action = "action";
    ComponentConnection prev = new ComponentConnection("id", "type",
        "state");
    ComponentConnection curr = new ComponentConnection("id", "type",
        "state");

    Whitebox.invokeMethod(target, "componentConnectionChanged", action,
        prev, curr);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "publishEvent", eq(ComponentConnectionChanged.TYPE),
        anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#componentConnectionChanged(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection, org.o3project.odenos.core.manager.system.ComponentConnection)}.
   *
   * @throws Exception
   */
  @Test
  public final void testComponentConnectionChangedWithException()
      throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    String action = "action";
    ComponentConnection prev = new ComponentConnection("id", "type",
        "state");
    ComponentConnection curr = new ComponentConnection("id", "type",
        "state");

    PowerMockito.doThrow(new Exception()).when(target, "publishEvent",
        anyString(), anyObject());

    Whitebox.invokeMethod(target, "componentConnectionChanged", action,
        prev, curr);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteAllComponentConnection()}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteAllComponentConnection() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));

    Whitebox.setInternalState(target, "mapCompAndCompMgr",
        mapCompAndCompMgr);

    Whitebox.invokeMethod(target, "deleteAllComponentConnection");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "deleteConnectionWithCompId", anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteConnectionWithCompId(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteConnectionWithCompIdLogic() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    Whitebox.setInternalState(target, "connectionTable", connectionTable);
    String compId = SLICER_ID;

    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        compId), is(true));

    Whitebox.invokeMethod(target, "deleteConnectionWithCompId", compId);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "deleteConnections", anyString());
    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        compId), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#deleteConnectionWithCompId(java.lang.String)}.
   *
   * @throws Exception
   */
  @Test
  public final void testDeleteConnectionWithCompIdNetwork() throws Exception {
    target = PowerMockito.spy(new SystemManager(SYSTEM_MGR_ID,
        mockDispatcher,
        mockEventManagerProp));
    Whitebox.setInternalState(target, "connectionTable", connectionTable);
    String compId = NETWORK_ID1;

    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        compId), is(true));

    Whitebox.invokeMethod(target, "deleteConnectionWithCompId", compId);

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "deleteConnections", anyString());
    assertThat((boolean) Whitebox.invokeMethod(target, "hasConnection",
        compId), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#putSequence(java.lang.String, java.util.Map<java.lnag.String. java.lnag.Long>)}.
   *
   * @throws Exception
   */
  @Test
  public final void testPutSequence() throws Exception {
    String seqId = "offset";
    Map<String, String> spec = new HashMap<String, String>();
    spec.put("start", String.valueOf(1000L));
    spec.put("end", String.valueOf(9000L));
    spec.put("step", String.valueOf(10L));
    Response resp = Whitebox.invokeMethod(target, "putSequence", seqId, spec);

    Map<String, String> body = WhiteboxImpl.getInternalState(resp, "body");
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(body.get("seq_id"), is(seqId));
    assertThat(body.get("start"), is(spec.get("start")));
    assertThat(body.get("end"), is(spec.get("end")));
    assertThat(body.get("step"), is(spec.get("step")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getSequence(java.lang.String, java.util.Map<java.lnag.String. java.lnag.Long>)}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetSequence() throws Exception {
    String seqId = "offset";
    Map<String, String> spec = new HashMap<String, String>();
    spec.put("start", String.valueOf(1000L));
    spec.put("end", String.valueOf(9000L));
    spec.put("step", String.valueOf(10L));
    Whitebox.invokeMethod(target, "putSequence", seqId, spec);

    Response resp;
    resp = Whitebox.invokeMethod(target, "getSequence", seqId, true);
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(String.class), is(String.valueOf(1000L)));
    resp = Whitebox.invokeMethod(target, "getSequence", seqId, true);
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(String.class), is(String.valueOf(1010L)));
    resp = Whitebox.invokeMethod(target, "getSequence", seqId, false);
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(String.class), is(String.valueOf(1010L)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.SystemManager#getUniqueID()}.
   *
   * @throws Exception
   */
  @Test
  public final void testGetUniqueID() throws Exception {
    assertThat(Whitebox.invokeMethod(target, "getUniqueID"),
        is(notNullValue()));

  }

}
