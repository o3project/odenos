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

package org.o3project.odenos.remoteobject.manager.component;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.Component;
import org.o3project.odenos.core.component.Driver;
import org.o3project.odenos.core.component.network.Network;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.manager.component.event.ComponentChanged;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ComponentManager.class })
@PowerMockIgnore({"javax.management.*"})
public class ComponentManagerTest {
  String objectId = "objectId";
  String baseUri = "baseUri";
  private ComponentManager target;
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
    target = Mockito.spy(new ComponentManager(objectId, dispatcher));
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
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public final void testOnRequestSuccess() {
    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = "component_types";
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public final void testOnRequestWithException() {
    String destId = "componentXXX";
    Request.Method method = Request.Method.PUT;
    String path = "components/aaa";
    Object body = "aaa";
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), nullValue());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnEventComponentManagerChangedAdd() throws Exception {
    ObjectProperty prev = null;
    ObjectProperty curr = new ObjectProperty(objectId, "compMgr");
    ComponentManagerChanged msg =
        new ComponentManagerChanged(
            ComponentManagerChanged.Action.add.name(),
            prev,
            curr);
    String type = ComponentManagerChanged.TYPE;
    Event event = new Event(objectId, type, msg);
    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    target.onEvent(event);

    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "registerOtherComponentManager",
        ((ObjectProperty) anyObject()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnEventComponentManagerChangedDel() throws Exception {
    ObjectProperty prev = new ObjectProperty(objectId, "compMgr");
    ObjectProperty curr = null;
    ComponentManagerChanged msg =
        new ComponentManagerChanged(
            ComponentManagerChanged.Action.delete.name(),
            prev,
            curr);
    String type = ComponentManagerChanged.TYPE;
    Event event = new Event(objectId, type, msg);
    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    target.onEvent(event);

    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "unregisterComponentManager",
        "compMgr");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnEventUnexpectEvent() throws Exception {
    ObjectProperty prev = new ObjectProperty(objectId, "compMgr");
    ObjectProperty curr = null;
    ComponentChanged msg = new ComponentChanged(
        ComponentManagerChanged.Action.delete.name(),
        prev,
        curr);
    String type = ComponentChanged.TYPE;
    Event event = new Event(objectId, type, msg);

    target.onEvent(event);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnEventComponentManagerChangedWithException()
      throws Exception {
    ObjectProperty prev = null;
    ObjectProperty curr = new ObjectProperty(objectId, "compMgr");
    ComponentManagerChanged msg =
        new ComponentManagerChanged(
            ComponentManagerChanged.Action.add.name(),
            prev,
            curr);
    String type = ComponentManagerChanged.TYPE;
    Event event = new Event(objectId, type, msg);
    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doThrow(new Exception()).when(target,
        "registerOtherComponentManager",
        (ObjectProperty) anyObject());

    target.onEvent(event);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#ComponentManager(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public final void testComponentManager() {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new ComponentManager(objectId, dispatcher));

    assertThat(target.getProperty().getObjectId(), is(objectId));
    assertThat(target.getProperty().getObjectType(),
        is(ComponentManager.class.getSimpleName()));
    assertThat(target.getProperty().getObjectState(),
        is(ObjectProperty.State.RUNNING));
    assertThat(
        (HashMap<String, Class<? extends Component>>) target.componentClasses,
        is(new HashMap<String, Class<? extends Component>>()));
    assertThat((HashMap<String, Component>) target.components,
        is(new HashMap<String, Component>()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerToSystemManager()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRegisterToSystemManagerSuccess() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    Response dummyResp = new Response(Response.OK, null);

    doReturn(dummyResp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doNothing().when(target, "registerEventManager");
    PowerMockito.doNothing().when(target, "subscribeEvents");
    PowerMockito.doNothing().when(target, "registerComponentManagers");

    target.registerToSystemManager();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerToSystemManager()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRegisterToSystemManagerFailure() throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    Response mockResp = Mockito.mock(Response.class,
        Mockito.CALLS_REAL_METHODS);

    doReturn(mockResp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    target.registerToSystemManager();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerEventManager()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRegisterEventManagerSuccess() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    ObjectProperty prop = new ObjectProperty("eventmanager",
        "EventManager");
    Response resp = new Response(Response.OK, prop);

    doReturn(resp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));
    PowerMockito.doReturn("eventmanager").when(target, "getEventManagerId");

    Whitebox.invokeMethod(target, "registerEventManager");

    PowerMockito.verifyPrivate(target, times(1)).invoke("addRemoteObject",
        eq("eventmanager"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerEventManager()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRegisterEventManagerFailWithGetEventManager()
      throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    Response mockResponse = Mockito.mock(Response.class,
        Mockito.CALLS_REAL_METHODS);

    doReturn(mockResponse).when(dispatcher).requestSync(
        (Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "registerEventManager");

    PowerMockito.verifyPrivate(target, never()).invoke("addRemoteObject",
        eq("eventmanager"),
        (InetSocketAddress) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerEventManager()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRegisterEventManagerFailWithAddRemoteObject()
      throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    Response mockResponse = Mockito.mock(Response.class,
        Mockito.CALLS_REAL_METHODS);

    doReturn(mockResponse).when(dispatcher).requestSync(
        (Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doThrow(new IOException()).when(target,
        "addRemoteObject",
        anyString(),
        (InetSocketAddress) anyObject());

    Whitebox.invokeMethod(target, "registerEventManager");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#subscribeEvents()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testSubscribeEventsSuccess() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    Response resp = new Response(Response.OK, null);

    doReturn(resp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doReturn(resp).when(target, "applyEventSubscription");

    Whitebox.invokeMethod(target, "subscribeEvents");

    // Do Nothing
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#subscribeEvents()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testSubscribeEventsFailure() throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    Response mockResponse = Mockito.mock(Response.class,
        Mockito.CALLS_REAL_METHODS);

    doReturn(mockResponse).when(dispatcher).requestSync(
        (Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "subscribeEvents");

    throw new Exception();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerComponentManagers()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRegisterComponentManagersSuccess() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    ObjectPropertyList propList = new ObjectPropertyList();
    propList.add(new ObjectProperty("Driver", "driver1"));
    propList.add(new ObjectProperty("Logic", "logic1"));
    propList.add(new ObjectProperty("Network", "network1"));
    Response resp = new Response(Response.OK, propList);

    doReturn(resp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doNothing().when(target, "registerOtherComponentManager",
        (ObjectProperty) anyObject());

    Whitebox.invokeMethod(target, "registerComponentManagers");

    PowerMockito.verifyPrivate(target, times(3)).invoke(
        "registerOtherComponentManager",
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerComponentManagers()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRegisterComponentManagersFailWithGetComponentManagers()
      throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    Response mockResponse = Mockito.mock(Response.class,
        Mockito.CALLS_REAL_METHODS);

    doReturn(mockResponse).when(dispatcher).requestSync(
        (Request) anyObject(), anyString());

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "registerComponentManagers");

    PowerMockito.verifyPrivate(target, never()).invoke(
        "registerOtherComponentManager",
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerOtherComponentManager()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRegisterOtherComponentManagerSuccess()
      throws Exception {
    ObjectProperty prop = new ObjectProperty("ComponentManager",
        "compmgr_java");

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "registerOtherComponentManager", prop);

    PowerMockito.verifyPrivate(target, times(1)).invoke("addRemoteObject",
        eq("compmgr_java"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerOtherComponentManager()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRegisterOtherComponentManagerSelfProperty()
      throws Exception {
    ObjectProperty prop = new ObjectProperty("ComponentManager",
        objectId);

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "registerOtherComponentManager", prop);

    PowerMockito.verifyPrivate(target, never()).invoke("addRemoteObject",
        eq(objectId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerOtherComponentManager()}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRegisterOtherComponentManagerFailWithAddRemoteObject()
      throws Exception {
    ObjectProperty prop = new ObjectProperty("ComponentManager",
        "compmgr_java");

    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    PowerMockito.doThrow(new IOException()).when(target,
        "addRemoteObject",
        anyString(),
        (InetSocketAddress) anyObject());

    Whitebox.invokeMethod(target, "registerOtherComponentManager", prop);

    PowerMockito.verifyPrivate(target, times(1)).invoke("addRemoteObject",
        eq("compmgr_java"),
        (InetSocketAddress) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#unregisterComponentManager()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testUnregisterComponentManager() throws Exception {
    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    Whitebox.invokeMethod(target, "unregisterComponentManager", objectId);

    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "removeRemoteObject",
        eq(objectId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#createParser()}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testCreateParser() throws Exception {
    target = PowerMockito.spy(new ComponentManager(objectId, dispatcher));

    @SuppressWarnings("unchecked")
    RequestParser<String> result =
        (RequestParser<String>) Whitebox.invokeMethod(target,
            "createParser");

    Request req = new Request(objectId, Request.Method.GET,
        "component_types", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.GET, "components", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.GET, "components/<comp_id>",
        null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.PUT, "components/<comp_id>", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.DELETE,
        "components/<comp_id>", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.PUT, "components/<comp_id>",
        null);
    assertThat(result.parse(req), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerComponentType(java.lang.Class)}
   * .
   */
  @Test
  public final void testRegisterComponentType() {
    target.registerComponentType(Network.class);

    assertThat(
        target.getProperty().getProperty(
            ObjectProperty.PropertyNames.COMPONENT_TYPES),
        is(Network.class.getSimpleName()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#registerComponentType(java.lang.Class)}
   * .
   */
  @Test
  public final void testRegisterComponentTypeSecondAdd() {
    target.registerComponentType(Driver.class);
    target.registerComponentType(Network.class);

    assertThat(
        target.getProperty().getProperty(
            ObjectProperty.PropertyNames.COMPONENT_TYPES),
        is(Driver.class.getSimpleName() + ","
            + Network.class.getSimpleName()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponentTypes()}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testGetComponentTypes() throws ParseBodyException {
    target.registerComponentType(Network.class);
    Response result = target.getComponentTypes();

    assertThat(result.statusCode, is(Response.OK));
    Map<String, ComponentType> getTypes = (HashMap<String, ComponentType>) result
        .getBodyAsMap(ComponentType.class);
    for (String types : getTypes.keySet()) {
      assertThat(types, is(Network.class.getSimpleName()));
    }
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponentTypes()}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testGetComponentTypesWithEmpty()
      throws ParseBodyException {
    Response result = target.getComponentTypes();

    assertThat(result.statusCode, is(Response.OK));
    Map<String, ComponentType> getTypes = (HashMap<String, ComponentType>) result
        .getBodyAsMap(ComponentType.class);
    assertThat(getTypes.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponents()}
   * .
   */
  @Test
  public final void testGetComponents() {
    target.registerComponentType(Network.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    target.putComponentId(prop.getObjectId(), prop);

    Response result = target.getComponents();

    assertThat(result.statusCode, is(Response.OK));
    Map<String, ObjectProperty> propMap =
        (Map<String, ObjectProperty>) result
            .getBodyAsMap(ObjectProperty.class);
    for (String objId : propMap.keySet()) {
      assertThat(propMap.get(objId),
          is(instanceOf(ObjectProperty.class)));
    }
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponents()}
   * .
   */
  @Test
  public final void testGetComponentsWithEmpty() {
    target.registerComponentType(Network.class);
    Response result = target.getComponents();

    assertThat(result.statusCode, is(Response.OK));
    Map<String, ObjectProperty> propMap =
        (Map<String, ObjectProperty>) result
            .getBodyAsMap(ObjectProperty.class);
    assertThat(propMap.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponentId(java.lang.String)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testGetComponentId() throws ParseBodyException {
    target.registerComponentType(Network.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    target.putComponentId(prop.getObjectId(), prop);

    Response result = target.getComponentId("network1");
    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(instanceOf(ObjectProperty.class)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#getComponentId(java.lang.String)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testGetComponentIdWithNotFound()
      throws ParseBodyException {
    Response result = target.getComponentId("network1");
    assertThat(result.statusCode, is(Response.NOT_FOUND));
    assertThat(result.getBodyValue(),
        is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#putComponentId(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testPutComponentIdWithSuccess()
      throws ParseBodyException {
    target.registerComponentType(Network.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    Response result = target.putComponentId(prop.getObjectId(), prop);

    assertThat(result.statusCode, is(Response.CREATED));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(instanceOf(ObjectProperty.class)));

    verify(target, times(1)).componentChanged(
        eq(ComponentChanged.Action.add.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#putComponentId(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testPutComponentIdWithBodyIsNull()
      throws ParseBodyException {
    target.registerComponentType(Network.class);
    Response result = target.putComponentId(null, null);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat((String) result.getBody(String.class),
        is("Bad format: ObjectProperty is expected"));

    verify(target, times(0)).componentChanged(
        eq(ComponentChanged.Action.add.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#putComponentId(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testPutComponentIdWithNotRegisteredComponentType()
      throws ParseBodyException {
    // target.registerComponentType(Aggregator.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    Response result = target.putComponentId(prop.getObjectId(), prop);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat((String) result.getBody(String.class),
        is("Error unknown type "));

    verify(target, times(0)).componentChanged(
        eq(ComponentChanged.Action.add.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#putComponentId(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testPutComponentIdWithConflict()
      throws ParseBodyException {
    target.registerComponentType(Network.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    Response result = target.putComponentId(prop.getObjectId(), prop);
    result = target.putComponentId(prop.getObjectId(), prop);

    assertThat(result.statusCode, is(Response.CONFLICT));
    assertThat((String) result.getBody(String.class),
        is("Component is already created"));

    verify(target, times(2)).componentChanged(
        eq(ComponentChanged.Action.add.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#deleteComponentId(java.lang.String)}
   * .
   */
  @Test
  public final void testDeleteComponentId() {
    target.registerComponentType(Network.class);
    ObjectProperty prop = new ObjectProperty(
        Network.class.getSimpleName(),
        "network1");
    target.putComponentId(prop.getObjectId(), prop);

    Response result = target.deleteComponentId("network1");

    assertThat(result.statusCode, is(Response.OK));
    verify(target, times(1)).componentChanged(
        eq(ComponentChanged.Action.delete.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#deleteComponentId(java.lang.String)}
   * .
   */
  @Test
  public final void testDeleteComponentIdWithNotRegistered() {
    Response result = target.deleteComponentId("network1");

    assertThat(result.statusCode, is(Response.OK));
    verify(target, times(0)).componentChanged(
        eq(ComponentChanged.Action.delete.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.component.ComponentManager#componentChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty, org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public final void testComponentChanged() {
    String action = ComponentChanged.Action.update.name();
    ObjectProperty prev = Mockito.mock(ObjectProperty.class);
    ObjectProperty curr = Mockito.mock(ObjectProperty.class);

    target.componentChanged(action,
        prev,
        curr);
  }
}
