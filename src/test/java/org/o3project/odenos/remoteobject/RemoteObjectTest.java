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

package org.o3project.odenos.remoteobject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.event.ObjectPropertyChanged;
import org.o3project.odenos.remoteobject.event.ObjectSettingsChanged;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.RequestParser.ParsedRequest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.io.IOException;

/**
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemoteObject.class })
@PowerMockIgnore({"javax.management.*"})
public class RemoteObjectTest {
  String objectId = "objectId";
  String baseUri = "baseUri";
  private RemoteObject target;
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
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));
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
   * {@link org.o3project.odenos.remoteobject.RemoteObject#RemoteObject(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public final void testRemoteObject() {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    assertThat(target.getProperty().getObjectId(), is(objectId));
    assertThat(target.getProperty().getObjectType(),
        is(RemoteObject.class.getSimpleName()));
    assertThat(target.getProperty().getObjectState(),
        is(ObjectProperty.State.INITIALIZING));
    assertThat(target.eventSubscription,
        is(new EventSubscription(objectId)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onInitialize(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public final void testOnInitialize() {
    ObjectProperty prop = new ObjectProperty("objectType", "objectId");
    boolean result = target.onInitialize(prop);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onFinalize()}.
   */
  @Test
  public final void testOnFinalizeStateChanegeNotFinalizingToFinalizing() {
    dispatcher = Mockito.mock(MessageDispatcher.class);

    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    target.onFinalize();

    assertThat(target.getProperty().getObjectState(),
        is(ObjectProperty.State.FINALIZING));

    verify(target, times(1)).onPropertyChanged(
        ObjectPropertyChanged.Action.delete.name(),
        (ObjectProperty) anyObject(),
        null);
    verify(dispatcher, times(1)).removeLocalObject(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onFinalize()}.
   */
  @Test
  public final void testOnFinalizeStateChanegeFinalizingToFinalizing() {
    dispatcher = Mockito.mock(MessageDispatcher.class);

    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));
    target.getProperty().setObjectState(ObjectProperty.State.FINALIZING);

    target.onFinalize();

    assertThat(target.getProperty().getObjectState(),
        is(ObjectProperty.State.FINALIZING));

    verify(target, times(0)).onPropertyChanged(
        ObjectPropertyChanged.Action.delete.name(),
        (ObjectProperty) anyObject(),
        null);
    verify(dispatcher, times(1)).removeLocalObject(target);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getObjectId()}.
   */
  @Test
  public final void testGetObjectId() {
    String result = target.getObjectId();

    assertThat(result, is(objectId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getProperty()}.
   */
  @Test
  public final void testGetProperty() {
    ObjectProperty result = target.getProperty();

    assertThat(result, is(target.objectProperty));
    assertThat(result.getObjectId(), is(objectId));
    assertThat(result.getObjectType(),
        is(RemoteObject.class.getSimpleName()));
    assertThat(result.getObjectState(),
        is(ObjectProperty.State.INITIALIZING));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getSettings()}.
   */
  @Test
  public final void testGetSettings() {
    ObjectSettings result = target.getSettings();

    assertThat(result, is(target.objectSettings));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getMessageDispatcher()}.
   */
  @Test
  public final void testGetMessageDispatcher() {
    MessageDispatcher result = target.getMessageDispatcher();

    assertThat(result, is(dispatcher));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#setState(java.lang.String)}
   * .
   */
  @Test
  public final void testSetStateChangeToNextDifferentState() {
    target.setState(ObjectProperty.State.FINALIZING);

    verify(target, times(1)).onPropertyChanged(
        ObjectPropertyChanged.Action.update.name(),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#setState(java.lang.String)}
   * .
   */
  @Test
  public final void testSetStateChangeToNextSameState() {
    target.setState(ObjectProperty.State.INITIALIZING);

    verify(target, times(0)).onPropertyChanged(
        ObjectPropertyChanged.Action.update.name(),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#setMessageDispatcher(org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public final void testSetMessageDispatcherWithNotNull() {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    verify(dispatcher, times(1)).addLocalObject((RemoteObject) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#setMessageDispatcher(org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public final void testSetMessageDispatcherWithNull() {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));
    target.setMessageDispatcher(null);

    verify(dispatcher, times(1)).addLocalObject((RemoteObject) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getSystemManagerId()}.
   */
  @Test
  public final void testGetSystemManagerId() {
    String systemMgrId = "systemManagerId";
    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(systemMgrId).when(dispatcher).getSystemManagerId();
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    String result = target.getSystemManagerId();

    assertThat(result, is(systemMgrId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#getEventManagerId()}.
   */
  @Test
  public final void testGetEventManagerId() {
    String eventMgrId = "eventManagerId";
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doReturn(eventMgrId).when(dispatcher).getEventManagerId();
    String result = target.getEventManagerId();

    assertThat(result, is(eventMgrId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#addRemoteObject(java.lang.String, java.net.InetSocketAddress)}
   * .
   *
   * @throws IOException
   */
  @Test
  public final void testAddRemoteObject() throws IOException {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    target.addRemoteObject(objectId);

    verify(dispatcher, times(1)).addRemoteObject(eq(objectId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#addRemoteObject(java.lang.String, java.net.InetSocketAddress)}
   * .
   *
   * @throws IOException
   */
  //@Test(expected = IOException.class)
  @Test(expected = Exception.class)
  public final void testAddRemoteObjectWithIoException() throws IOException {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doThrow(new IOException()).when(dispatcher).addRemoteObject(
        eq(objectId));
    target.addRemoteObject(objectId);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#removeRemoteObject(java.lang.String)}
   * .
   */
  @Test
  public final void testRemoveRemoteObject() {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    try {
      target.removeRemoteObject(objectId);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
    }

    verify(dispatcher, times(1)).removeRemoteObject(objectId);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#requestSync(java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRequestSync() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    Response dummyResp = new Response(Response.OK, "aaa");

    doReturn(dummyResp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    Response result = target.requestSync(objectId, Request.Method.GET, "/",
        "body");

    assertThat(result, is((Response) dummyResp));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#requestSync(java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testrequestSyncWithException() throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doThrow(new Exception()).when(dispatcher).requestSync(
        (Request) anyObject());

    target.requestSync(objectId, Request.Method.GET, "/", "body");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#request(java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testRequest() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    Response dummyResp = new Response(Response.OK, "aaa");

    doReturn(dummyResp).when(dispatcher).requestSync((Request) anyObject(), anyString());

    Response result = target.request(objectId, Request.Method.GET, "/",
        "body");

    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(String.class), is("aaa"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#request(java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testRequestWithException() throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doThrow(new Exception()).when(dispatcher).requestSync(
        (Request) anyObject());

    target.request(objectId, Request.Method.GET, "/", "body");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#publishEvent(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testPublishEvent() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    target.publishEvent("eventType", "body");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#publishEvent(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testPublishEventWithException() throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doThrow(new Exception()).when(dispatcher).publishEventAsync(
        (Event) anyObject());

    target.publishEvent("eventType", "body");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#applyEventSubscription()}.
   *
   * @throws Exception
   */
  @Test
  public final void testApplyEventSubscription() throws Exception {
    dispatcher = Mockito.mock(MessageDispatcher.class);
    String objectId = "objectId";

    Response dummyResp = new Response(Response.ACCEPTED, "aaa");
    doReturn(dummyResp).when(dispatcher).subscribeEvent(
        (EventSubscription) anyObject());

    target = Mockito.spy(new RemoteObject(objectId, dispatcher));
    Response result = target.applyEventSubscription();

    assertThat(result, is((Response) dummyResp));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#applyEventSubscription()}.
   *
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public final void testApplyEventSubscriptionWithException()
      throws Exception {
    dispatcher = Mockito.spy(new MessageDispatcher("systemManagerId"));
    String objectId = "objectId";
    target = Mockito.spy(new RemoteObject(objectId, dispatcher));

    doThrow(new Exception()).when(dispatcher).requestSync(
        (Request) anyObject());

    target.applyEventSubscription();
    
    throw new Exception();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#createParser()}.
   *
   * @throws Exception
   */
  @Test
  public final void testCreateParser() throws Exception {
    target = PowerMockito.spy(new RemoteObject(objectId, dispatcher));

    @SuppressWarnings("unchecked")
    RequestParser<String> result =
        (RequestParser<String>) Whitebox.invokeMethod(target,
            "createParser");

    Request req = new Request(objectId, Request.Method.GET, "property",
        null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.PUT, "property", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.GET, "settings", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.PUT, "settings", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.POST, "event", null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(objectId, Request.Method.GET, "event", null);
    assertThat(result.parse(req), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDispatchRequestSuccess() throws ParseBodyException {
    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = "property";
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDispatchRequestWithRequestPathIsNull()
      throws ParseBodyException {
    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = null;
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), nullValue());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDispatchRequestWithRequestPathEmpty()
      throws ParseBodyException {
    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = "";
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), nullValue());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDispatchRequestWithParseError()
      throws ParseBodyException {
    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = "aaa";
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), nullValue());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testDispatchRequestWithParseResultNull() throws Exception {
    target = PowerMockito.spy(new RemoteObject(objectId, dispatcher));

    Object parserMock =
        PowerMockito.mock((WhiteboxImpl.getInternalState(target,
            "parser").getClass()));
    @SuppressWarnings("rawtypes")
    ParsedRequest parsedMock = PowerMockito.mock(ParsedRequest.class);
    PowerMockito.doReturn(parsedMock).when(parserMock, "parse",
        (Request) anyObject());
    WhiteboxImpl.setInternalState(target, "parser", parserMock);

    String destId = "componentXXX";
    Request.Method method = Request.Method.GET;
    String path = "property";
    Object body = null;
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDispatchRequestWithException()
      throws ParseBodyException {
    String destId = "componentXXX";
    Request.Method method = Request.Method.PUT;
    String path = "property";
    String body = "body";
    Request request = new Request(destId,
        method,
        path,
        body);

    Response result = target.dispatchRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBodyValue(), nullValue());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   */
  @Test
  public final void testDispatchEvent() {
    Event event = Mockito.mock(Event.class);
    event.publisherId = "publisher1";
    event.eventType = "eventType1";
    EventSubscription eventSubscription = Mockito.mock(EventSubscription.class);
    Mockito.when(eventSubscription.contains("publisher1", "eventType1"))
      .thenReturn(true);
    Whitebox.setInternalState(target, "eventSubscription", eventSubscription);

    target.dispatchEvent(event);

    verify(target, times(1)).doPostEvent(event);
    verify(target, times(1)).onEvent(event);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#dispatchEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   */
  @Test
  public final void testDispatchEventFromUnsubscribedChannel() {
    Event event = Mockito.mock(Event.class);
    event.publisherId = "publisher1";
    event.eventType = "eventType1";
    EventSubscription eventSubscription = Mockito.mock(EventSubscription.class);
    Mockito.when(eventSubscription.contains("publisher1", "eventType1"))
      .thenReturn(false);
    Whitebox.setInternalState(target, "eventSubscription", eventSubscription);

    target.dispatchEvent(event);

    verify(target, times(0)).doPostEvent(event);
    verify(target, times(0)).onEvent(event);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doGetProperty()}.
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoGetProperty() throws ParseBodyException {
    Response result = target.doGetProperty();

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutPropertyWithAdd() throws ParseBodyException {
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    prop.setProperty("add_prop", "test");
    Response result = target.doPutProperty(prop);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
    ObjectProperty updateProp = (ObjectProperty) result
        .getBody(ObjectProperty.class);
    assertThat(updateProp.getProperty("add_prop"), is("test"));

    verify(target, times(1)).onPropertyChanged(
        eq(ObjectPropertyChanged.Action.update.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutPropertyWithDelete() throws ParseBodyException {
    target.getProperty().setProperty("del_prop", "test");
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    prop.deleteProperty("del_prop");
    Response result = target.doPutProperty(prop);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
    ObjectProperty updateProp = (ObjectProperty) result
        .getBody(ObjectProperty.class);
    assertThat(updateProp.getProperty("del_prop"), nullValue());

    verify(target, times(1)).onPropertyChanged(
        eq(ObjectPropertyChanged.Action.update.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutPropertyWithUpdate() throws ParseBodyException {
    target.getProperty().setProperty("update_prop", "test1");
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    prop.setProperty("update_prop", "test2");
    Response result = target.doPutProperty(prop);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
    ObjectProperty updateProp = (ObjectProperty) result
        .getBody(ObjectProperty.class);
    assertThat(updateProp.getProperty("update_prop"), is("test2"));

    verify(target, times(1)).onPropertyChanged(
        eq(ObjectPropertyChanged.Action.update.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutPropertyWithNothingUpdate()
      throws ParseBodyException {
    target.getProperty().setProperty("update_prop", "test1");
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    Response result = target.doPutProperty(prop);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectProperty) result.getBody(ObjectProperty.class),
        is(target.objectProperty));
    ObjectProperty updateProp = (ObjectProperty) result
        .getBody(ObjectProperty.class);
    assertThat(updateProp.getProperty("update_prop"), is("test1"));

    verify(target, times(0)).onPropertyChanged(
        eq(ObjectPropertyChanged.Action.update.name()),
        (ObjectProperty) anyObject(),
        (ObjectProperty) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutPropertyWithStateChangeFinalizing()
      throws ParseBodyException {
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    prop.setObjectState(ObjectProperty.State.FINALIZING);
    Response result = target.doPutProperty(prop);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), nullValue());

    verify(target, times(1)).onFinalize();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doGetSettings()}.
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoGetSettings() throws ParseBodyException {
    target.getSettings().setSetting("settings_test", "test");

    Response result = target.doGetSettings();

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectSettings) result.getBody(ObjectSettings.class),
        is(target.getSettings()));
    ObjectSettings setting = (ObjectSettings) result
        .getBody(ObjectSettings.class);
    assertThat(setting.getSetting("settings_test"), is("test"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutSettingsWithAdd() throws ParseBodyException {
    ObjectSettings settings = (ObjectSettings) target.getSettings().clone();
    settings.setSetting("add_setting", "test");
    Response result = target.doPutSettings(settings);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectSettings) result.getBody(ObjectSettings.class),
        is(target.objectSettings));
    ObjectSettings respSettings = (ObjectSettings) result
        .getBody(ObjectSettings.class);
    assertThat(respSettings.getSetting("add_setting"), is("test"));

    verify(target, times(1)).onSettingsChanged(
        eq(ObjectSettingsChanged.Action.update.name()),
        (ObjectSettings) anyObject(),
        (ObjectSettings) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutSettingsWithDelete() throws ParseBodyException {
    target.getSettings().setSetting("delete_settings", "test");

    ObjectSettings settings = (ObjectSettings) target.getSettings().clone();
    settings.deleteSetting("delete_settings");
    Response result = target.doPutSettings(settings);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectSettings) result.getBody(ObjectSettings.class),
        is(target.objectSettings));
    ObjectSettings respSettings = (ObjectSettings) result
        .getBody(ObjectSettings.class);
    assertThat(respSettings.getSetting("delete_settings"), nullValue());

    verify(target, times(1)).onSettingsChanged(
        eq(ObjectSettingsChanged.Action.update.name()),
        (ObjectSettings) anyObject(),
        (ObjectSettings) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutSettingsWithUpdate() throws ParseBodyException {
    target.getSettings().setSetting("update_settings", "test1");
    ObjectSettings settings = (ObjectSettings) target.getSettings().clone();
    settings.setSetting("update_settings", "test2");

    Response result = target.doPutSettings(settings);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectSettings) result.getBody(ObjectSettings.class),
        is(target.objectSettings));
    ObjectSettings respSettings = (ObjectSettings) result
        .getBody(ObjectSettings.class);
    assertThat(respSettings.getSetting("update_settings"), is("test2"));

    verify(target, times(1)).onSettingsChanged(
        eq(ObjectSettingsChanged.Action.update.name()),
        (ObjectSettings) anyObject(),
        (ObjectSettings) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPutSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws ParseBodyException
   */
  @Test
  public final void testDoPutSettingsWithNothingUpdate()
      throws ParseBodyException {
    target.getSettings().setSetting("update_settings", "test1");

    ObjectSettings settings = (ObjectSettings) target.getSettings().clone();

    Response result = target.doPutSettings(settings);

    assertThat(result.statusCode, is(Response.OK));
    assertThat((ObjectSettings) result.getBody(ObjectSettings.class),
        is(target.objectSettings));

    verify(target, times(0)).onSettingsChanged(
        eq(ObjectSettingsChanged.Action.update.name()),
        (ObjectSettings) anyObject(),
        (ObjectSettings) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#doPostEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   */
  @Test
  public final void testDoPostEvent() {
    Event event = Mockito.mock(Event.class);
    Response result = target.doPostEvent(event);

    assertThat(result.statusCode, is(Response.ACCEPTED));
    assertThat(result.getBodyValue(), nullValue());

    verify(target, times(1)).onEvent((Event) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onStateChanged(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testOnStateChangedStateChangeFinalizing() {
    target.onStateChanged(ObjectProperty.State.RUNNING,
        ObjectProperty.State.FINALIZING);

    verify(target, times(1)).onFinalize();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onStateChanged(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testOnStateChangedStateChangeNotFinalizing() {
    target.onStateChanged(ObjectProperty.State.RUNNING,
        ObjectProperty.State.ERROR);

    verify(target, times(0)).onFinalize();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onStateChanged(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testOnStateChangedStateChangeAlreadyFinalizing() {
    target.onStateChanged(ObjectProperty.State.FINALIZING,
        ObjectProperty.State.FINALIZING);

    verify(target, times(0)).onFinalize();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onPropertyChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty, org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnPropertyChanged() throws Exception {
    String action = ObjectPropertyChanged.Action.update.name();
    ObjectProperty prev = Mockito.mock(ObjectProperty.class);
    ObjectProperty curr = Mockito.mock(ObjectProperty.class);

    target.onPropertyChanged(action,
        prev,
        curr);

    verify(target, times(1)).publishEvent(
        eq(ObjectPropertyChanged.TYPE),
        (ObjectPropertyChanged) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onPropertyChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectProperty, org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnPropertyChangedWithException() throws Exception {
    String action = ObjectPropertyChanged.Action.update.name();
    ObjectProperty prev = Mockito.mock(ObjectProperty.class);
    ObjectProperty curr = Mockito.mock(ObjectProperty.class);

    doThrow(new Exception()).when(target).publishEvent(
        eq(ObjectPropertyChanged.TYPE),
        (ObjectPropertyChanged) anyObject());

    target.onPropertyChanged(action,
        prev,
        curr);

    verify(target, times(1)).publishEvent(
        eq(ObjectPropertyChanged.TYPE),
        (ObjectPropertyChanged) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onSettingsChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectSettings, org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnSettingsChanged() throws Exception {
    String action = ObjectSettingsChanged.Action.update.name();
    ObjectSettings prev = Mockito.mock(ObjectSettings.class);
    ObjectSettings curr = Mockito.mock(ObjectSettings.class);

    target.onSettingsChanged(action,
        prev,
        curr);

    verify(target, times(1)).publishEvent(
        eq(ObjectSettingsChanged.TYPE),
        (ObjectSettingsChanged) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onSettingsChanged(java.lang.String, org.o3project.odenos.remoteobject.ObjectSettings, org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnSettingsChangedWithException() throws Exception {
    String action = ObjectSettingsChanged.Action.update.name();
    ObjectSettings prev = Mockito.mock(ObjectSettings.class);
    ObjectSettings curr = Mockito.mock(ObjectSettings.class);

    doThrow(new Exception()).when(target).publishEvent(
        eq(ObjectSettingsChanged.TYPE),
        (ObjectSettingsChanged) anyObject());

    target.onSettingsChanged(action,
        prev,
        curr);

    verify(target, times(1)).publishEvent(
        eq(ObjectSettingsChanged.TYPE),
        (ObjectSettingsChanged) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public final void testOnRequest() {
    Request request = Mockito.mock(Request.class);
    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   */
  @Test
  public final void testOnEvent() {
    Event event = Mockito.mock(Event.class);
    target.onEvent(event);

    // Do Nothing
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#isFinalize()}.
   *
   * @throws Exception
   */
  @Test
  public final void testIsFinalizeStateFinalizing() throws Exception {
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();
    prop.setObjectState(ObjectProperty.State.FINALIZING);

    boolean result = Whitebox.invokeMethod(target, "isFinalize", prop);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RemoteObject#isFinalize()}.
   *
   * @throws Exception
   */
  @Test
  public final void testIsFinalizeStateNotFinalizing() throws Exception {
    ObjectProperty prop = (ObjectProperty) target.getProperty().clone();

    boolean result = Whitebox.invokeMethod(target, "isFinalize", prop);

    assertThat(result, is(false));
  }
}
