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

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class SubscribersMapTest {
  
  
  SubscribersMap target = null;
  private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> subscribersMap;
  
  @Before
  public void setUp() {
    target = new SubscribersMap();
  }
  
  @After
  public void tearDown() {
    target = null;
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSetSubscription() {
    target.setSubscription("tokyo", "subscriber1");
    subscribersMap =
        (ConcurrentHashMap<String, CopyOnWriteArraySet<String>>)
        Whitebox.getInternalState(target, "subscribersMap");
    CopyOnWriteArraySet<String> subscribers = subscribersMap.get("tokyo");
    assertTrue(subscribers.contains("subscriber1"));
  }
  
  @Test
  public void testRemoveSubscription() {
    subscribersMap = new ConcurrentHashMap<>();
    CopyOnWriteArraySet<String> subscribers = new CopyOnWriteArraySet<>();
    subscribers.add("subscriber1");
    subscribers.add("subscriber2");
    subscribersMap.put("tokyo", subscribers);
    Whitebox.setInternalState(target, "subscribersMap", subscribersMap);
    target.removeSubscription("tokyo", "subscriber1");
    assertFalse(subscribersMap.isEmpty());
    target.removeSubscription("tokyo", "subscriber2");
    assertTrue(subscribersMap.isEmpty());
  }

  @Test
  public void testRemoveSubscriber() {
    subscribersMap = new ConcurrentHashMap<>();
    CopyOnWriteArraySet<String> subscribers = new CopyOnWriteArraySet<>();
    subscribers.add("subscriber1");
    subscribers.add("subscriber2");
    subscribersMap.put("tokyo", subscribers);
    Whitebox.setInternalState(target, "subscribersMap", subscribersMap);
    target.removeSubscriber("subscriber1");
    assertFalse(subscribersMap.isEmpty());
    target.removeSubscriber("subscriber2");
    assertTrue(subscribersMap.isEmpty());
  }

}
