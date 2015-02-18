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

package org.o3project.odenos.remoteobject.manager;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.RequestParser.ParsedRequest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EventManager.class })
public class EventManagerTest {
  public static final String OBJECT_ID = "object_id";
  MessageDispatcher mockDispatcher = PowerMockito
      .mock(MessageDispatcher.class);
  EventManager target = null;

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
    target = PowerMockito.spy(new EventManager(OBJECT_ID, mockDispatcher));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#EventManager(java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}.
   */
  @Test
  public final void testEventManager() {
    assertThat(target, is(instanceOf(EventManager.class)));
    ObjectProperty obj = WhiteboxImpl.getInternalState(target,
        "objectProperty");
    assertThat(obj.getObjectState(), is(ObjectProperty.State.RUNNING));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#OnFinalize()}.
   */
  @Test
  public final void testOnFinalize() {
    target = Mockito.spy(new EventManager(OBJECT_ID, mockDispatcher));

    target.onFinalize();

    EventSubscriptionMap subscriptionMap = WhiteboxImpl.getInternalState(
        target, "subscriptionMap");
    assertThat(subscriptionMap.subscriptions.isEmpty(), is(true));
    assertThat(subscriptionMap.subscriptionMap.isEmpty(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.manager.system.EventManager#createParser()}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testCreateParser() throws Exception {
    RequestParser<String> result =
        (RequestParser<String>) Whitebox.invokeMethod(target,
            "createParser");

    Request req = new Request(OBJECT_ID,
        Request.Method.PUT,
        "settings/event_subscriptions/<subscriber_id>",
        null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(OBJECT_ID,
        Request.Method.GET,
        "settings/event_subscriptions",
        null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(OBJECT_ID,
        Request.Method.GET,
        "settings/event_subscriptions/<subscriber_id>",
        null);
    assertThat(result.parse(req), is(notNullValue()));
    req = new Request(OBJECT_ID,
        Request.Method.GET,
        "settings/event_subscription",
        null);
    assertThat(result.parse(req), is(nullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   * @throws Exception
   */
  @Test
  public final void testOnRequestWithSuccess() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Request req = new Request(OBJECT_ID,
        Request.Method.PUT,
        "settings/event_subscriptions/subscriber_id=SubscriberId",
        evtSubscription);
    Response dummyResp = new Response(Response.OK,
        evtSubscription);
    PowerMockito.doReturn(dummyResp).when(target,
        "putSubscription", "SubscriberId", evtSubscription);

    Response resp = target.onRequest(req);

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(WhiteboxImpl.getInternalState(resp, "body"),
        is(instanceOf(EventSubscription.class)));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   * @throws Exception
   */
  @Test
  public final void testOnRequestParsedNullNoMatch() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Request req = new Request(OBJECT_ID,
        Request.Method.PUT,
        "settings/event_subscription/subscriber_id=SubscriberId",
        evtSubscription);

    Response resp = target.onRequest(req);

    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   * @throws Exception
   */
  @SuppressWarnings("rawtypes")
  @Test
  public final void testOnRequestParseResultNull() throws Exception {
    Object parserMock =
        PowerMockito.mock((WhiteboxImpl.getInternalState(target,
            "parser").getClass()));

    ParsedRequest parsedMock = PowerMockito.mock(ParsedRequest.class);
    PowerMockito.doReturn(parsedMock).when(parserMock, "parse",
        (Request) anyObject());
    WhiteboxImpl.setInternalState(target, "parser", parserMock);

    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Request req = new Request(OBJECT_ID,
        Request.Method.PUT,
        "settings/event_subscriptions/subscriber_id=SubscriberId",
        evtSubscription);

    Response resp = target.onRequest(req);

    assertThat(resp.statusCode, is(Response.BAD_REQUEST));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   * @throws Exception
   */
  @Test
  public final void testOnRequestWithException() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Request req = new Request(OBJECT_ID,
        Request.Method.GET,
        "settings/event_subscriptions/subscriber_id",
        evtSubscription);
    PowerMockito.doThrow(new Exception()).when(target,
        "getSubscription",
        "subscriber_id");

    Response resp = target.onRequest(req);

    assertThat(resp.statusCode, is(Response.NOT_FOUND));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#putSubscription(java.lang.String, org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscription)}.
   * @throws Exception
   */
  @Test
  public final void testPutSubscription() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");

    Response resp = Whitebox.invokeMethod(target, "putSubscription",
        "SubscriberId", evtSubscription);

    EventSubscriptionMap subscriptionMap = WhiteboxImpl.getInternalState(
        target, "subscriptionMap");
    assertThat(subscriptionMap.getSubscription("SubscriberId"),
        is(evtSubscription));
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(EventSubscription.class),
        is(evtSubscription));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#getSubscriptions()}.
   * @throws Exception
   */
  @Test
  public final void testGetSubscriptions() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Whitebox.invokeMethod(target, "putSubscription", "SubscriberId", evtSubscription);

    Response resp = Whitebox.invokeMethod(target, "getSubscriptions");

    EventSubscriptionMap subscriptionMap = WhiteboxImpl.getInternalState(
        target, "subscriptionMap");
    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(EventSubscriptionMap.class),
        is(subscriptionMap));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#getSubscription(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public final void testGetSubscriptionSuccess() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId");
    evtSubscription.addFilter("publisherId", "eventId");
    Whitebox.invokeMethod(target, "putSubscription", "subscriberId", evtSubscription);

    Response resp = Whitebox.invokeMethod(target, "getSubscription",
        "subscriberId");

    assertThat(resp.statusCode, is(Response.OK));
    assertThat(resp.getBody(EventSubscription.class),
        is(evtSubscription));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventManager#getSubscription(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public final void testGetSubscriptionNotFound() throws Exception {
    EventSubscription evtSubscription = new EventSubscription(
        "subscriberId1");
    evtSubscription.addFilter("publisherId", "eventId");
    Whitebox.invokeMethod(target, "putSubscription", "SubscriberId", evtSubscription);

    Response resp = Whitebox.invokeMethod(target, "getSubscription",
        "subscriberId2");

    assertThat(resp.statusCode, is(Response.NOT_FOUND));
  }

}
