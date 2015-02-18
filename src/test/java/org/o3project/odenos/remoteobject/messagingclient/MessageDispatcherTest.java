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

package org.o3project.odenos.remoteobject.messagingclient;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.messagingclient.redis.ChannelCheckerClient;
import org.o3project.odenos.remoteobject.messagingclient.redis.PublisherClient;
import org.o3project.odenos.remoteobject.messagingclient.redis.SubscriberClient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.net.ProtocolException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * This test cases use {@link PubSubDriverMock}.
 * 
 * Part of tests are performed with Redis server, if -DtestWithRedis
 * is set as a system property of run config.
 */
public class MessageDispatcherTest {

  static final String SYSTEM_MANAGER_ID = "systemmanager";
  static final String EVENT_MANAGER_ID = "eventmanager";
  static final String HOST = "localhost";
  static final int PORT = 6379;

  private MessageDispatcher target = null;

  private static boolean skip = true;

  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }

  @Before
  public void setUp() {
    String driverClassName =
        org.o3project.odenos.remoteobject.messagingclient.PubSubDriverMock.class.getName();
    Config config = new ConfigBuilder()
        .setSystemManagerId(SYSTEM_MANAGER_ID)
        .setEventManagerId(EVENT_MANAGER_ID)
        .setPubSubDriverImpl(driverClassName)
        .build();
    target = PowerMockito.spy(new MessageDispatcher(config));
  }

  @After
  public void tearDown() {
    target = null;
  }

  @Test
  public final void testMessageDispatcher() {
    if (!skip) {
      target = new MessageDispatcher();
      constructorCommonTest();
      checkHostAndPort("localhost", 6379);
    }
  }

  @Test
  public final void testMessageDispatcherSystemManagerId() {
    if (!skip) {
      target = new MessageDispatcher(SYSTEM_MANAGER_ID);
      constructorCommonTest();
      checkHostAndPort("localhost", 6379);
    }
  }

  @Test
  public final void testMessageDispatcherSystemManagerIdHostPort() {
    if (!skip) {
      target = new MessageDispatcher(SYSTEM_MANAGER_ID, HOST, PORT);
      constructorCommonTest();
      checkHostAndPort(HOST, PORT);
    }
  }

  @Test
  public final void testMessageDispatcherConfig() {
    Config config = new ConfigBuilder()
        .setSystemManagerId("Germany")
        .setEventManagerId("Berlin")
        .setHost("U9")
        .setPort(10623)
        .setPubSubDriverImpl(
            org.o3project.odenos.remoteobject.messagingclient.PubSubDriverMock.class.getName())
        .setRemoteTransactionsInitialTimeout(10)
        .setRemoteTransactionsFinalTimeout(110)
        .setRemoteTransactionsMax(10000)
        .setSourceDispatcherId("Tokyo")
        .build();

    target = new MessageDispatcher(config);

    assertThat(target.getSystemManagerId(), is("Germany"));
    assertThat(target.getSourceDispatcherId(), is("Tokyo"));
    assertThat(target.getEventManagerId(), is("Berlin"));
    PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
    assertThat(driverImpl.getClass().getName(),
        is(org.o3project.odenos.remoteobject.messagingclient.PubSubDriverMock.class.getName()));
    assertThat(driverImpl.getSystemManagerId(), is("Germany"));
    assertThat(driverImpl.getHost(), is("U9"));
    assertThat(driverImpl.getPort(), is(10623));
    assertThat(driverImpl.getListener(), instanceOf(IMessageListener.class));
    RemoteTransactions remoteTransactions =
        Whitebox.getInternalState(target, "remoteTransactions");
    assertThat((int) Whitebox.getInternalState(remoteTransactions, "initialTimeout"), is(10));
    assertThat((int) Whitebox.getInternalState(remoteTransactions, "secondTimeout"), is(110 - 10));
    ArrayBlockingQueue<SynchronousQueue<Response>> queue =
        Whitebox.getInternalState(remoteTransactions, "rendezvousPool");
    assertThat(queue.size(), is(10000));
  }

  private void constructorCommonTest() {
    assertThat(target.getSystemManagerId(), is(SYSTEM_MANAGER_ID));
    assertThat(
        (MessagePack) Whitebox.getInternalState(target, "msgpack"),
        instanceOf(MessagePack.class));
    assertThat(Whitebox.getInternalState(target, "remoteTransactions"),
        instanceOf(RemoteTransactions.class));
    assertThat(Whitebox.getInternalState(target, "driverImpl"),
        instanceOf(IPubSubDriver.class));
  }

  private void checkHostAndPort(String host, int port) {
    IPubSubDriver driverImpl =
        Whitebox.getInternalState(target, "driverImpl");
    SubscriberClient subscriberClient =
        Whitebox.getInternalState(driverImpl, "subscriberClient");
    PublisherClient publisherClient =
        Whitebox.getInternalState(driverImpl, "publisherClient");
    ChannelCheckerClient channelCheckerClient =
        Whitebox.getInternalState(driverImpl, "channelCheckerClient");
    assertThat((String) Whitebox.getInternalState(subscriberClient, "host"),
        is(host));
    assertThat((int) Whitebox.getInternalState(subscriberClient, "port"),
        is(port));
    assertThat((String) Whitebox.getInternalState(publisherClient, "host"),
        is(host));
    assertThat((int) Whitebox.getInternalState(publisherClient, "port"),
        is(port));
    assertThat((String) Whitebox.getInternalState(channelCheckerClient, "host"),
        is(host));
    assertThat((int) Whitebox.getInternalState(channelCheckerClient, "port"),
        is(port));
  }

  @Test
  public final void testStart() {
    PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
    assertFalse(driverImpl.isStarted());

    target.start();

    assertTrue(driverImpl.isStarted());
  }

  @Test
  public final void testClose() {
    target.start();
    target.close();

    PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(!driverImpl.isStarted());
  }

  @Test
  public final void testAddLocalObject() {
      RemoteObject mockLocalObject = Mockito.mock(RemoteObject.class);
      Mockito.when(mockLocalObject.getObjectId()).thenReturn("object1");
      ConcurrentHashMap<String, RemoteObject> localObjectsMap =
          Whitebox.getInternalState(target, "localObjectsMap");
      target.addLocalObject(mockLocalObject);
      assertThat(localObjectsMap.get("object1"), is(mockLocalObject));
      
      PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
      try {
        assertTrue(driverImpl.channelExist(mockLocalObject.getObjectId()));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
  }

  @Test
  public final void testRemoveLocalObject() {
      RemoteObject mockLocalObject = Mockito.mock(RemoteObject.class);
      Mockito.when(mockLocalObject.getObjectId()).thenReturn("object1");
      ConcurrentHashMap<String, RemoteObject> localObjectsMap =
          Whitebox.getInternalState(target, "localObjectsMap");
      localObjectsMap.put("object1", mockLocalObject);
      target.removeLocalObject(mockLocalObject);
      assertNull(localObjectsMap.get("object1"));
      PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
      try {
        assertFalse(driverImpl.channelExist(mockLocalObject.getObjectId()));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
  }

  @Test
  public final void testContainObjectId() {
      RemoteObject mockLocalObject = Mockito.mock(RemoteObject.class);
      Mockito.when(mockLocalObject.getObjectId()).thenReturn("object1");
      ConcurrentHashMap<String, RemoteObject> localObjectsMap =
          Whitebox.getInternalState(target, "localObjectsMap");
      localObjectsMap.put("object1", mockLocalObject);
      assertTrue(target.containObjectId("object1"));
  }

  @Test
  public final void testPublishEventAsyncSuccess() throws Exception {
      Event mockEvent = Mockito.mock(Event.class);
      mockEvent.publisherId = "publisher";
      mockEvent.eventType = "event";
      String channel = "publisher:event";
      target.publishEventAsync(mockEvent);
      PubSubDriverMock driverImpl = Whitebox.getInternalState(target, "driverImpl");
      assertTrue(driverImpl.publishedMessages.containsKey(channel));
  }

  @Test
  public final void testSubscribeEventSuccess() throws Exception {
      // EventSubscription mock setup
      EventSubscription mockEventSubscription =
          Mockito.mock(EventSubscription.class);
      Mockito.doReturn("alice").when(mockEventSubscription).getSubscriberId();
      Mockito.doReturn(true).when(mockEventSubscription).hasChannelsToBeSubscribed();
      Mockito.doNothing().when(mockEventSubscription).clearChannelsToBeSubscribed();
      Set<String> eventIds = new HashSet<>();
      eventIds.add("event1");
      eventIds.add("event2");
      ConcurrentHashMap<String, Set<String>> channelsToBeSubscribed =
          new ConcurrentHashMap<>();
      channelsToBeSubscribed.put("bob", eventIds);
      Mockito.doReturn(channelsToBeSubscribed).
          when(mockEventSubscription).getChannelsToBeSubscribed();

      // Test execution
      target.subscribeEvent(mockEventSubscription);
      SubscribersMap subscribersMap
        = Whitebox.getInternalState(target, "subscribersMap");
      assertTrue(subscribersMap.getSubscribers("bob:event1").contains("alice"));
      assertTrue(subscribersMap.getSubscribers("bob:event2").contains("alice"));
    }

  @Test
  public final void testUnsubscribeEventSuccess() throws Exception {
      // subscribersMap setup
      SubscribersMap subscribersMap = new SubscribersMap();
      subscribersMap.setSubscription("bob:event1", "alice");
      subscribersMap.setSubscription("bob:event2", "alice");
      Whitebox.setInternalState(target, "subscribersMap", subscribersMap);

      // EventSubscription mock setup
      EventSubscription mockEventSubscription =
          Mockito.mock(EventSubscription.class);
      Mockito.doReturn("alice").when(mockEventSubscription).getSubscriberId();
      Mockito.doReturn(true).when(mockEventSubscription).hasChannelsToBeUnsubscribed();
      Mockito.doNothing().when(mockEventSubscription).clearChannelsToBeUnsubscribed();
      Set<String> eventIds = new HashSet<>();
      eventIds.add("event1");
      eventIds.add("event2");
      ConcurrentHashMap<String, Set<String>> channelsToBeUnsubscribed =
          new ConcurrentHashMap<>();
      channelsToBeUnsubscribed.put("bob", eventIds);
      Mockito.doReturn(channelsToBeUnsubscribed).
          when(mockEventSubscription).getChannelsToBeUnsubscribed();

      // Test execution
      target.subscribeEvent(mockEventSubscription);
      subscribersMap
        = Whitebox.getInternalState(target, "subscribersMap");
      assertNull(subscribersMap.getSubscribers("bob:event1"));
      assertNull(subscribersMap.getSubscribers("bob:event2"));
    }

  @Test
  public final void testGetSystemManagerId() {
      assertThat(target.getSystemManagerId(), is(SYSTEM_MANAGER_ID));
  }

  @Test
  public final void testGetEventManagerId() {
    assertThat(target.getEventManagerId(), is(EVENT_MANAGER_ID));
  }
}
