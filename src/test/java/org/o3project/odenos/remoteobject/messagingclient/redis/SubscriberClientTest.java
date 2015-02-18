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

package org.o3project.odenos.remoteobject.messagingclient.redis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.messagingclient.redis.RedisClient;
import org.o3project.odenos.remoteobject.messagingclient.redis.SubscriberClient;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.reflect.Whitebox;

@PowerMockIgnore("org.apache.log4j.*")
public class SubscriberClientTest {

  private SubscriberClient target = null;
  private RedisClient redisClient = null;
  private static final String HOST = "localhost";
  private static int PORT = 6379;

  PubSubDriverImpl listener;
  
  private RedisServerAddress redisServerAddress =
      new RedisServerAddress(HOST, PORT, null, 0);

  private static boolean skip = true;
  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }

  @Before
  public void setUp() throws Exception {
    if (!skip) {
      listener = Mockito.mock(PubSubDriverImpl.class);
      doNothing().when(listener).onMessage(anyString(), any(byte[].class));
      target = new SubscriberClient(redisServerAddress, listener);
      target.start();
      redisClient = new RedisClient();
      redisClient.connect(HOST, PORT);
    }
  }

  @After
  public void tearDown() throws Exception {
    if (!skip) {
      target.close();
      redisClient.close();
    }
  }

  @Test
  public final void testSubscriberClient() {
    if (!skip) {
      SubscriberClient target = null;
      target = new SubscriberClient(redisServerAddress, listener);
      target.start();
      constructorCommonTest(target);
      target.close();
    }
  }

  private void constructorCommonTest(SubscriberClient target) {
    if (!skip) {
      assertThat((String) Whitebox.getInternalState(target, "host"), is(HOST));
      assertThat((int) Whitebox.getInternalState(target, "port"), is(PORT));
    }
  }

  @Test
  public final void testStart() {
    if (!skip) {
      SubscriberClient target = null;
      target = new SubscriberClient(redisServerAddress, listener);
      target.start();
      assertTrue(target.isStarted());
      target.close();
    }
  }

  @Test
  public final void testClose() {
    if (!skip) {
      SubscriberClient target = null;
      target = new SubscriberClient(redisServerAddress, listener);
      target.start();
      target.close();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      assertFalse(target.isStarted());
    }
  }

  @Test
  public final void testSubscribeChannel() {
    if (!skip) {
      target.subscribeChannel("tokyo");
      try {
        redisClient.pubsubNumsub("tokyo".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(1L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void testUnsubscribeChannel() {
    if (!skip) {
      target.subscribeChannel("tokyo");
      target.unsubscribeChannel("tokyo");
      try {
        redisClient.pubsubNumsub("tokyo".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(0L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void testSubscribeChannels() {
    if (!skip) {
      Set<String> channels = new HashSet<>();
      channels.add("tokyo");
      channels.add("kyoto");
      target.subscribeChannels(channels);
      try {
        redisClient.pubsubNumsub("tokyo".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(1L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
      try {
        redisClient.pubsubNumsub("kyoto".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(1L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void testUnsubscribeChannels() {
    if (!skip) {
      Set<String> channels = new HashSet<>();
      channels.add("tokyo");
      channels.add("kyoto");
      target.subscribeChannels(channels);
      target.unsubscribeChannels(channels);
      try {
        redisClient.pubsubNumsub("tokyo".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(0L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
      try {
        redisClient.pubsubNumsub("kyoto".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(0L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void testUnsubscribeAll() {
    if (!skip) {
      ArrayList<String> channels = new ArrayList<>();
      channels.add("tokyo");
      channels.add("kyoto");
      target.unsubscribeAll();
      try {
        redisClient.pubsubNumsub("tokyo".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(0L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
      try {
        redisClient.pubsubNumsub("kyoto".getBytes());
        long num = redisClient.readPubsubNumsubReply("tokyo");
        assertThat(num, is(0L));
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
    }
  }
}
