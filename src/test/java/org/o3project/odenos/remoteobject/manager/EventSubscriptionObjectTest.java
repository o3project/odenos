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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EventSubscriptionMap.class, EventSubscription.class })
@PowerMockIgnore({"javax.management.*"})
public class EventSubscriptionObjectTest {
  private EventSubscriptionMap target1 = null;
  private EventSubscription target2 = null;

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
    target1 = new EventSubscriptionMap();
    target2 = new EventSubscription();
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target1 = null;
    target2 = null;
  }

  public final Value createEventSubscription1() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id1");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory
        .createRawValue("ComponentConnectionChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });
    return eventSubscription;
  }

  public final Value createEventSubscription2() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id2");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_python");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory
        .createRawValue("ComponentConnectionChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });
    return eventSubscription;
  }

  public final void setSubscriptions() {
    Value subscriberId1Key = ValueFactory.createRawValue("subscriber_id1");
    Value subscriberId2Key = ValueFactory.createRawValue("subscriber_id2");
    Value subscriptionMap = ValueFactory.createMapValue(new Value[] {
        subscriberId1Key, this.createEventSubscription1(),
        subscriberId2Key, this.createEventSubscription2()
    });
    target1.readValue(subscriptionMap);
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#readValue(org.msgpack.type.Value)}.
   */
  @Test
  public final void testEventSubscriptionMapReadValue() {
    Value subscriberId1Key = ValueFactory.createRawValue("subscriber_id1");
    Value subscriberId2Key = ValueFactory.createRawValue("subscriber_id2");
    Value subscriptionMap = ValueFactory.createMapValue(new Value[] {
        subscriberId1Key, this.createEventSubscription1(),
        subscriberId2Key, this.createEventSubscription2()
    });

    boolean result = target1.readValue(subscriptionMap);

    assertThat(result, is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#writeValueSub(java.util.Map)}.
   */
  @Test
  public final void testEventSubscriptionMapWriteValueSub() {
    this.setSubscriptions();

    Map<String, Value> values = new HashMap<String, Value>();
    boolean result = target1.writeValueSub(values);

    assertThat(result, is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#getSubscriptions()}.
   */
  @Test
  public final void testGetSubscriptions() {
    this.setSubscriptions();

    Map<String, EventSubscription> subscriptions = target1
        .getSubscriptions();

    assertThat(subscriptions.containsKey("subscriber_id1"), is(true));
    assertThat(subscriptions.containsKey("subscriber_id2"), is(true));
    assertThat(subscriptions.get("subscriber_id1"),
        is(instanceOf(EventSubscription.class)));
    assertThat(subscriptions.get("subscriber_id2"),
        is(instanceOf(EventSubscription.class)));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#getSubscription(java.lang.String)}.
   */
  @Test
  public final void testGetSubscription() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);

    EventSubscription retSubscription = target1
        .getSubscription("subscriber_id1");

    assertThat(retSubscription, is(notNullValue()));
    assertThat(retSubscription.getSubscriberId(), is("subscriber_id1"));
    assertThat(retSubscription.getFilters().get("publisher_id").size(),
        is(2));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#setSubscription(java.lang.String, EventSubscription)}.
   */
  @Test
  public final void testSetSubscription() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    EventSubscription first = target1.setSubscription(subscriberId,
        subscription);

    assertThat(first, is(nullValue()));

    subscription.addFilter(publisherId, eventId2);
    EventSubscription second = target1.setSubscription(subscriberId,
        subscription);

    assertThat(second, is(notNullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#removeSubscription(java.lang.String)}.
   */
  @Test
  public final void testRemoveSubscription() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);
    subscriberId = "subscriber_id2";
    publisherId = "publisher_id";
    eventId1 = "event_1";
    eventId2 = "event_2";
    subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);

    assertThat(target1.subscriptions.size(), is(2));
    assertThat(target1.subscriptionMap.size(), is(1));

    EventSubscription old = target1.removeSubscription("subscriber_id1");

    assertThat(old.getSubscriberId(), is("subscriber_id1"));
    assertThat(target1.subscriptions.size(), is(1));
    assertThat(target1.subscriptionMap.size(), is(1));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#getSubscribers(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testGetSubscribersNothingEvent() {
    List<EventSubscription> ret = target1.getSubscribers("publisher",
        "eventType");

    assertThat(ret, is(nullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#getSubscribers(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testGetSubscribersNothingEventType() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);
    subscriberId = "subscriber_id2";
    publisherId = "publisher_id";
    eventId1 = "event_1";
    eventId2 = "event_3";
    subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);

    List<EventSubscription> ret = target1.getSubscribers("publisher_id",
        "event_4");

    assertThat(ret.isEmpty(), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#getSubscribers(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testGetSubscribers() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);
    subscriberId = "subscriber_id2";
    publisherId = "publisher_id";
    eventId1 = "event_1";
    eventId2 = "event_3";
    subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);

    List<EventSubscription> ret = target1.getSubscribers("publisher_id",
        "event_1");

    assertThat(ret.size(), is(2));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap#clear()}.
   */
  @Test
  public final void testClear() {
    String subscriberId = "subscriber_id1";
    String publisherId = "publisher_id";
    String eventId1 = "event_1";
    String eventId2 = "event_2";
    EventSubscription subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);
    subscriberId = "subscriber_id2";
    publisherId = "publisher_id";
    eventId1 = "event_1";
    eventId2 = "event_3";
    subscription = new EventSubscription(subscriberId);
    subscription.addFilter(publisherId, eventId1);
    subscription.addFilter(publisherId, eventId2);
    target1.setSubscription(subscriberId, subscription);

    target1.clear();

    assertThat(target1.subscriptionMap.isEmpty(), is(true));
    assertThat(target1.subscriptions.isEmpty(), is(true));
  }

  /**
   * Test method for {@link EventSubscription#readValue(org.msgpack.type.Value)}.
   */
  @Test
  public final void testEventSubscriptionReadValue() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });

    boolean result = target2.readValue(eventSubscription);

    assertThat(result, is(true));
  }

  /**
   * Test method for {@link EventSubscription#readValue(org.msgpack.type.Value)}.
   */
  @Test
  public final void testReadValueNothingSubscriberId() {
    //        Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    //        Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        //                subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });

    boolean result = target2.readValue(eventSubscription);

    assertThat(result, is(false));
  }

  /**
   * Test method for {@link EventSubscription#readValue(org.msgpack.type.Value)}.
   */
  @Test
  public final void testReadValueIncludeMapKeyNilValue() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    //        Value objectId = ValueFactory.createRawValue("systemmanager");
    Value objectId = ValueFactory.createNilValue();
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });

    boolean result = target2.readValue(eventSubscription);

    assertThat(result, is(false));
  }

  /**
   * Test method for {@link EventSubscription#readValue(org.msgpack.type.Value)}.
   */
  @Test
  public final void testReadValueIncludeMapValueNilValue() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    //        Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    //        Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    //        Value eventList = ValueFactory.createArrayValue(new Value[] {
    //                eventId1, eventId2
    //        });
    Value eventList = ValueFactory.createNilValue();
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });

    boolean result = target2.readValue(eventSubscription);

    assertThat(result, is(false));
  }

  /**
   * Test method for {@link EventSubscription#writeValueSub(java.util.Map)}.
   */
  @Test
  public final void testEventSubscriptionWriteValueSub() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });
    target2.readValue(eventSubscription);

    Map<String, Value> values = new HashMap<String, Value>();
    boolean result = target2.writeValueSub(values);

    assertThat(result, is(true));
    assertThat(values.get("subscriber_id"), is(notNullValue()));
    assertThat(values.get("event_filters"), is(notNullValue()));
  }

  /**
   * Test method for {@link EventSubscription#EventSubscription()}.
   */
  @Test
  public final void testEventSubscription() {
    target2 = new EventSubscription();

    assertThat(target2.getSubscriberId(), is(nullValue()));
  }

  /**
   * Test method for {@link EventSubscription#EventSubscription(java.lang.String)}.
   */
  @Test
  public final void testEventSubscriptionString() {
    target2 = new EventSubscription("subscriber_id");

    assertThat(target2.getSubscriberId(), is("subscriber_id"));
  }

  /**
   * Test method for {@link EventSubscription#getSubscriberId()}.
   */
  @Test
  public final void testGetSubscriberId() {
    target2 = new EventSubscription("subscriber_id");

    assertThat(target2.getSubscriberId(), is("subscriber_id"));
  }

  /**
   * Test method for {@link EventSubscription#setSubscriberId(java.lang.String)}.
   */
  @Test
  public final void testSetSubscriberId() {

    /*
     * test
     */
    target2.setSubscriberId("NewSubscriberId");

    /*
     * check
     */
    String resultSubscriberId = Whitebox.getInternalState(target2, "subscriberId");
    assertThat(resultSubscriberId, is("NewSubscriberId"));

    assertThat(target2.getSubscriberId(), is("NewSubscriberId"));

  }

  /**
   * Test method for {@link EventSubscription#getFilters()}.
   */
  @Test
  public final void testGetFilters() {
    Value subscriberIdKey = ValueFactory.createRawValue("subscriber_id");
    Value subscriberIdValue = ValueFactory.createRawValue("compmgr_java");
    Value eventFiltersKey = ValueFactory.createRawValue("event_filters");
    Value objectId = ValueFactory.createRawValue("systemmanager");
    Value eventId1 = ValueFactory.createRawValue("ComponentManagerChanged");
    Value eventId2 = ValueFactory.createRawValue("ComponentChanged");
    Value eventList = ValueFactory.createArrayValue(new Value[] {
        eventId1, eventId2
    });
    Value eventFilters = ValueFactory.createMapValue(new Value[] {
        objectId, eventList
    });
    Value eventSubscription = ValueFactory.createMapValue(new Value[] {
        subscriberIdKey, subscriberIdValue,
        eventFiltersKey, eventFilters
    });
    target2.readValue(eventSubscription);

    Map<String, Set<String>> resultFilters = target2.getFilters();

    assertThat(resultFilters.containsKey("systemmanager"), is(true));
    assertThat(resultFilters.get("systemmanager").size(), is(2));
  }

  /**
   * Test method for {@link EventSubscription#addFilter(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testAddFilterNotContainKey() {
    target2.addFilter("publisherId", "eventId1");

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.containsKey("publisherId"), is(true));
    assertThat(resultFilters.get("publisherId").size(), is(1));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.event.EventSubscription#addFilter(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testAddFilterContainKey() {
    target2.addFilter("publisherId", "eventId1");
    target2.addFilter("publisherId", "eventId2");

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.containsKey("publisherId"), is(true));
    assertThat(resultFilters.get("publisherId").size(), is(2));
  }

  /**
   * Test method for {@link EventSubscription#removeFilter(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testRemoveFilterContainKey() {
    target2.addFilter("publisherId", "eventId1");
    target2.removeFilter("publisherId", "eventId1");

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.get("publisherId").size(), is(0));
  }

  /**
   * Test method for {@link EventSubscription#removeFilter(java.lang.String, java.lang.String)}.
   */
  @Test
  public final void testRemoveFilterNotContainKey() {
    target2.addFilter("publisherId", "eventId1");
    target2.removeFilter("publisherId1", "eventId1");

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.get("publisherId").size(), is(1));
  }

  /**
   * Test method for {@link EventSubscription#clearFilter()}.
   */
  @Test
  public final void testClearFilter() {
    target2.addFilter("publisherId", "eventId1");
    target2.clearFilter();

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.isEmpty(), is(true));
  }

  /**
   * Test method for {@link EventSubscription#removePublisherId(java.lang.String)}.
   */
  @Test
  public final void testRemovePublisherId() {
    target2.addFilter("publisherId1", "eventId1");
    target2.addFilter("publisherId2", "eventId1");
    target2.removePublisherId("publisherId1");

    Map<String, Set<String>> resultFilters = target2.getFilters();
    assertThat(resultFilters.containsKey("publisherId1"), is(false));
    assertThat(resultFilters.containsKey("publisherId2"), is(true));
  }

  /**
   * Test method for {@link EventSubscription#equals(java.lang.Object)}.
   */
  @Test
  public final void testEqualsSameObject() {
    boolean result = target2.equals(target2);

    assertThat(result, is(true));
  }

  /**
   * Test method for {@link EventSubscription#equals(java.lang.Object)}.
   */
  @Test
  public final void testEqualsObjectSameSubscriberId() {
    target2 = new EventSubscription("subscriberId");
    EventSubscription comp = new EventSubscription("subscriberId");
    boolean result = target2.equals(comp);

    assertThat(result, is(true));
  }

  /**
   * Test method for {@link EventSubscription#equals(java.lang.Object)}.
   */
  @Test
  public final void testEqualsObjectDiffSubscriberId() {
    target2 = new EventSubscription("subscriberId1");
    EventSubscription comp = new EventSubscription("subscriberId2");
    boolean result = target2.equals(comp);

    assertThat(result, is(false));
  }

  /**
   * Test method for {@link EventSubscription#equals(java.lang.Object)}.
   */
  @Test
  public final void testEqualsNotEventSubscriptionObject() {
    Value comp = ValueFactory.createRawValue("subscriberId");
    boolean result = target2.equals(comp);

    assertThat(result, is(false));
  }

}
