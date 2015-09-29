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

package org.o3project.odenos.remoteobject.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RESTTranslator.class })
@PowerMockIgnore({"javax.management.*"})
public class RESTTranslatorTest {

  private RESTTranslator target;

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
    target = Mockito.spy(new RESTTranslator("ObjectId2", dispatcher));

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
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#RESTTranslator(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public void testRESTTranslatorStringStringMessageDispatcher() {

    /*
     * test
     */
    target = new RESTTranslator("ObjectId", dispatcher);

    /*
     * check
     */
    ObjectProperty objectProperty = Whitebox.getInternalState(target,
        "objectProperty");
    assertThat(objectProperty.getObjectId(), is("ObjectId"));

    assertThat(target.getMessageDispatcher(), is(dispatcher));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#RESTTranslator(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher, java.lang.String)}
   * .
   */
  @Test
  public void testRESTTranslatorStringStringMessageDispatcherString() {

    /*
     * test
     */
    target = new RESTTranslator("ObjectId", dispatcher,
        "PropertiesFilePath");

    /*
     * check
     */
    ObjectProperty objectProperty = Whitebox.getInternalState(target,
        "objectProperty");
    assertThat(objectProperty.getObjectId(), is("ObjectId"));

    assertThat(target.getMessageDispatcher(), is(dispatcher));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#RESTTranslator(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher, java.lang.String, java.lang.Integer)}
   * .
   */
  @Test
  public void testRESTTranslatorStringStringMessageDispatcherStringInteger() {

    /*
     * test
     */
    target = new RESTTranslator("ObjectId", dispatcher,
        "PropertiesFilePath", 18080);

    /*
     * check
     */
    ObjectProperty objectProperty = Whitebox.getInternalState(target,
        "objectProperty");
    assertThat(objectProperty.getObjectId(), is("ObjectId"));

    assertThat(target.getMessageDispatcher(), is(dispatcher));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#setAsyncContext(java.lang.String, javax.servlet.AsyncContext)}
   * .
   */
  @Test
  public void testSetAsyncContext() {

    /*
     * setting
     */
    Map<String, AsyncContext> asyncContextMap =
        Mockito.spy(new HashMap<String, AsyncContext>());
    Whitebox.setInternalState(target, "asyncContextMap", asyncContextMap);

    /*
     * test
     */
    AsyncContext context = Mockito.mock(AsyncContext.class);
    target.setAsyncContext("SubscriptionId", context);

    /*
     * check
     */
    verify(asyncContextMap, times(1)).put("SubscriptionId", context);

    Map<String, AsyncContext> resultMap = Whitebox.getInternalState(target,
        "asyncContextMap");

    assertThat(resultMap.get("SubscriptionId"), is(context));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#removeAsyncContext(java.lang.String)}
   * .
   */
  @Test
  public void testRemoveAsyncContext() {

    /*
     * setting
     */
    Map<String, AsyncContext> asyncContextMap =
        Mockito.spy(new HashMap<String, AsyncContext>());

    AsyncContext context = Mockito.mock(AsyncContext.class);
    asyncContextMap.put("SubscriptionId", context);

    Whitebox.setInternalState(target, "asyncContextMap", asyncContextMap);

    /*
     * test
     */
    AsyncContext result = target.removeAsyncContext("SubscriptionId");

    /*
     * check
     */
    assertThat(result, is(context));

    Map<String, AsyncContext> resultContextMap =
        Whitebox.getInternalState(target, "asyncContextMap");
    assertThat(resultContextMap.containsKey("SubscriptionId"), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#modifyDistributionSetting(java.lang.String, java.util.Map, java.util.Map)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testModifyDistributionSetting() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new RESTTranslator("ObjectId", dispatcher));

    Map<String, List<String>> added = new HashMap<>();
    added.put(
        "ObjectId1",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType1", "EventType2" })));
    added.put(
        "ObjectId2",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType3", "EventType4" })));
    added.put(
        "ObjectId3",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType5", "EventType6" })));

    Map<String, List<String>> removed = new HashMap<>();
    removed.put("ObjectId2",
        new ArrayList<String>(Arrays.asList(new String[] { "value3" })));

    /*
     * test
     */
    target.modifyDistributionSetting("SubscriptionId", added, removed);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

    Map<List<String>, Set<String>> distributionMap =
        Whitebox.getInternalState(target, "distributionTable");
    assertThat(distributionMap.size(), is(6));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType1")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType2")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId2",
        "EventType3")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType5")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType6")), is(true));

    EventSubscription eventSubscription =
        Whitebox.getInternalState(target, "eventSubscription");
    Map<String, Set<String>> eventFilters = eventSubscription.getFilters();
    assertThat(eventFilters.size(), is(3));
    assertThat(eventFilters.get("ObjectId1")
        .containsAll(createList("EventType1", "EventType2")), is(true));
    assertThat(
        eventFilters.get("ObjectId2").containsAll(
            createList("EventType3")), is(true));
    assertThat(eventFilters.get("ObjectId3")
        .containsAll(createList("EventType5", "EventType6")), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#modifyDistributionSetting(java.lang.String, java.util.Map, java.util.Map)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testModifyDistributionSetting_NoRemoved() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new RESTTranslator("ObjectId", dispatcher));

    Map<String, List<String>> added = new HashMap<>();
    added.put(
        "ObjectId1",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType1", "EventType2" })));
    added.put(
        "ObjectId2",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType3", "EventType4" })));
    added.put(
        "ObjectId3",
        new ArrayList<String>(Arrays.asList(new String[] {
            "EventType5", "EventType6" })));

    Map<String, List<String>> removed = null;

    /*
     * test
     */
    target.modifyDistributionSetting("SubscriptionId", added, removed);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

    Map<List<String>, Set<String>> distributionMap =
        Whitebox.getInternalState(target, "distributionTable");
    assertThat(distributionMap.size(), is(6));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType1")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType2")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId2",
        "EventType3")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId2",
        "EventType4")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType5")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType6")), is(true));

    EventSubscription eventSubscription =
        Whitebox.getInternalState(target, "eventSubscription");
    Map<String, Set<String>> eventFilters = eventSubscription.getFilters();
    assertThat(eventFilters.size(), is(3));
    assertThat(eventFilters.get("ObjectId1")
        .containsAll(createList("EventType1", "EventType2")), is(true));
    assertThat(eventFilters.get("ObjectId2")
        .containsAll(createList("EventType3", "EventType4")), is(true));
    assertThat(eventFilters.get("ObjectId3")
        .containsAll(createList("EventType5", "EventType6")), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#modifyDistributionSetting(java.lang.String, java.util.Map, java.util.Map)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testModifyDistributionSetting_NoAdd() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new RESTTranslator("ObjectId", dispatcher));

    Map<List<String>, Set<String>> settingDistMap = new HashMap<>();
    settingDistMap.put(createList("ObjectId1", "EventType1"),
        new HashSet<String>());
    settingDistMap.put(createList("ObjectId1", "EventType2"),
        new HashSet<String>());
    settingDistMap.put(createList("ObjectId2", "EventType3"),
        new HashSet<String>());
    settingDistMap.put(createList("ObjectId2", "EventType4"),
        new HashSet<String>());
    settingDistMap.put(createList("ObjectId3", "EventType5"),
        new HashSet<String>());
    settingDistMap.put(createList("ObjectId3", "EventType6"),
        new HashSet<String>());
    Whitebox.setInternalState(target, "distributionTable", settingDistMap);

    EventSubscription settingEventSubscription = new EventSubscription();
    settingEventSubscription.addFilter("ObjectId1", "EventType1");
    settingEventSubscription.addFilter("ObjectId1", "EventType2");
    settingEventSubscription.addFilter("ObjectId2", "EventType3");
    settingEventSubscription.addFilter("ObjectId2", "EventType4");
    settingEventSubscription.addFilter("ObjectId3", "EventType5");
    settingEventSubscription.addFilter("ObjectId3", "EventType6");
    Whitebox.setInternalState(target, "eventSubscription",
        settingEventSubscription);

    Map<String, List<String>> added = null;

    Map<String, List<String>> removed = new HashMap<>();
    removed.put("ObjectId2",
        new ArrayList<String>(Arrays.asList(new String[] { "value3" })));

    /*
     * test
     */
    target.modifyDistributionSetting("SubscriptionId", added, removed);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

    Map<List<String>, Set<String>> distributionMap =
        Whitebox.getInternalState(target, "distributionTable");
    assertThat(distributionMap.size(), is(6));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType1")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId1",
        "EventType2")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId2",
        "EventType3")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType5")), is(true));
    assertThat(distributionMap.containsKey(createList("ObjectId3",
        "EventType6")), is(true));

    EventSubscription eventSubscription =
        Whitebox.getInternalState(target, "eventSubscription");
    Map<String, Set<String>> eventFilters = eventSubscription.getFilters();
    assertThat(eventFilters.size(), is(3));
    assertThat(eventFilters.get("ObjectId1")
        .containsAll(createList("EventType1", "EventType2")), is(true));
    assertThat(
        eventFilters.get("ObjectId2").containsAll(
            createList("EventType3")), is(true));
    assertThat(eventFilters.get("ObjectId3")
        .containsAll(createList("EventType5", "EventType6")), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#modifyDistributionSetting(java.lang.String, java.util.Map, java.util.Map)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testModifyDistributionSetting_NoAddNoRemoved() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new RESTTranslator("ObjectId", dispatcher));

    /*
     * test
     */
    target.modifyDistributionSetting("SubscriptionId", null, null);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnEvent() throws Exception {

    /*
     * setting
     */
    Map<List<String>, Set<String>> distributionTable =
        Mockito.spy(new HashMap<List<String>, Set<String>>());
    Set<String> subscriptionIds = new HashSet<>();
    subscriptionIds.add("SubscriptionId1");
    subscriptionIds.add("SubscriptionId2");
    distributionTable.put(createList("PublisherId", "EventType"),
        subscriptionIds);
    Whitebox.setInternalState(target, "distributionTable",
        distributionTable);

    Map<String, AsyncContext> asyncContextMap = new HashMap<String, AsyncContext>();
    Whitebox.setInternalState(target, "asyncContextMap", asyncContextMap);

    /*
     * test
     */
    Event event =
        new Event("PublisherId", "EventType", "txid", 
            ValueFactory.createRawValue("aEventBody"));
    target.onEvent(event);

    /*
     * check
     */
    verify(target, times(1)).removeAsyncContext("SubscriptionId1");
    verify(target, times(1)).removeAsyncContext("SubscriptionId2");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#onEvent(org.o3project.odenos.remoteobject.message.Event)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testOnEvent_NotFoundSubscriptionId() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new RESTTranslator("ObjectId", dispatcher));

    Map<List<String>, Set<String>> distributionTable =
        Mockito.spy(new HashMap<List<String>, Set<String>>());
    EventSubscription eventSubscription = Mockito
        .spy(new EventSubscription());

    Whitebox.setInternalState(target, "distributionTable",
        distributionTable);
    Whitebox.setInternalState(target, "eventSubscription",
        eventSubscription);

    Event event = new Event("PublisherId", "EventType", "txid", new Object());

    /*
     * test
     */
    target.onEvent(event);

    /*
     * check
     */
    verify(distributionTable, times(1)).remove(
        createList("PublisherId", "EventType"));
    verify(eventSubscription, times(1)).removeFilter("PublisherId",
        "EventType");

    PowerMockito.verifyPrivate(target, atLeastOnce()).invoke(
        "applyEventSubscription");

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.rest.RESTTranslator#startServer(String, Integer)}.
   *
   * @throws Exception
   */
  @Test
  public void testStartServer() throws Exception {

    Whitebox.invokeMethod(target, "stopServer");

    /*
     * test
     */
    Whitebox.invokeMethod(target, "startServer", "127.0.0.9", 48080, "");

    /*
     * check
     */
    Server server = null;
    for (int i = 0; i < 10; ++i) {
      Thread.sleep(1000);
      server = Whitebox.invokeMethod(target, "server");
      if (server != null) {
        break;
      }
    }
    assertThat(server, is(notNullValue()));

  }

  private List<String> createList(String... strs) {

    List<String> list = new ArrayList<>();

    if (strs == null) {
      return list;
    }

    for (String str : strs) {
      list.add(str);
    }

    return list;

  }

}
