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

import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EventSubscription represents subscription issued by RemoteObjects and managed
 * by EventManager.
 *
 */

public class EventSubscriptionObject {

  public static class EventSubscriptionMap extends OdenosMessage {
    /**
     * List of all subscriptions.
     */
    Map<String, EventSubscription> subscriptions = new HashMap<String, EventSubscription>();

    /**
     * Nested dict of subscriptions, indexed by "Publisher ID"(first key)
     * and "EventType"(second key).
     */
    protected Map<String, Map<String, List<EventSubscription>>> subscriptionMap =
        new ConcurrentHashMap<String, Map<String, List<EventSubscription>>>();

    public Map<String, EventSubscription> getSubscriptions() {
      return subscriptions;
    }

    public EventSubscription getSubscription(String subscriber) {
      return subscriptions.get(subscriber);
    }

    /**
     * Set a subscription.
     * @param subscriber a subscriber.
     * @param subscription a subscription.
     * @return previous subscription.
     */
    public EventSubscription setSubscription(String subscriber,
        EventSubscription subscription) {
      EventSubscription old = subscriptions.put(subscriber, subscription);
      if (old != null) {
        for (Entry<String, Set<String>> map : old.getFilters()
            .entrySet()) {
          String publisherId = map.getKey();
          for (String event : map.getValue()) {
            if (event.equals("ComponentConnectionChanged")) {
              continue;
            }
            // workaround for NPE
            if (subscriptionMap.get(publisherId) == null) {
                continue;
            }
            List<EventSubscription> eventSubs =
                subscriptionMap.get(publisherId).get(event);
            if (eventSubs != null) {
              int index = eventSubs.indexOf(subscription);
              if (index != -1) {
                eventSubs.remove(index);
              }
              if (eventSubs.isEmpty()) {
                subscriptionMap.get(publisherId).remove(event);
              }
            }
            if (subscriptionMap.get(publisherId).isEmpty()) {
              subscriptionMap.remove(publisherId);
            }
          }
        }
      }

      for (Entry<String, Set<String>> entry : subscription.getFilters().entrySet()) {
        String publisher = entry.getKey();
        Map<String, List<EventSubscription>> eventList =
            subscriptionMap.get(publisher);
        if (eventList == null
            || (!subscriptionMap.containsKey(publisher))) {
          eventList = new HashMap<String, List<EventSubscription>>();
          subscriptionMap.put(publisher, eventList);
        }

        for (String event : entry.getValue()) {
          List<EventSubscription> eventSubs = eventList.get(event);
          if (eventSubs == null) {
            eventSubs = new ArrayList<EventSubscription>();
            eventList.put(event, eventSubs);
            eventSubs.add(subscription);
          } else {
            int index = eventSubs.indexOf(subscription);
            if (index != -1) {
              eventSubs.set(index, subscription);
            } else {
              eventSubs.add(subscription);
            }
          }
        }
      }

      return old;
    }

    /**
     * Remove a subscription.
     * @param subscriber a subscriber.
     * @return previous subscription.
     */
    public EventSubscription removeSubscription(String subscriber) {
      EventSubscription old = subscriptions.remove(subscriber);
      if (old == null) {
        return null;
      }

      for (Entry<String, Set<String>> entry : old.getFilters().entrySet()) {
        String publisherId = entry.getKey();
        if (entry.getValue() == null) {
          return null;
        }

        for (String eventId : entry.getValue()) {
          subscriptionMap.get(publisherId).get(eventId).remove(old);
          if (subscriptionMap.get(publisherId).get(eventId) == null) {
            subscriptionMap.get(publisherId).remove(eventId);
            if (subscriptionMap.get(publisherId) == null) {
              subscriptionMap.remove(publisherId);
            }
          }
        }
      }

      return old;
    }

    /**
     * Returns a list of subscriptions.
     * @param publisher a publisher.
     * @param eventType type of event.
     * @return list of subscriptions.
     */
    public List<EventSubscription> getSubscribers(String publisher,
        String eventType) {
      Map<String, List<EventSubscription>> eventMap = subscriptionMap
          .get(publisher);
      if (eventMap == null) {
        return null;
      }
      List<EventSubscription> orig = eventMap.get(eventType);
      if (orig == null) {
        return new ArrayList<EventSubscription>();
      } else {
        List<EventSubscription> copy = new ArrayList<EventSubscription>(
            orig);
        return copy;
      }
    }

    public void clear() {
      subscriptions.clear();
      subscriptionMap.clear();
    }

    @Override
    public boolean readValue(Value value) {
      MapValue map = value.asMapValue();

      for (Entry<Value, Value> entry : map.entrySet()) {
        String subscriber = entry.getKey().asRawValue().getString();
        EventSubscription subscription = new EventSubscription();
        subscription.readValue(entry.getValue());

        setSubscription(subscriber, subscription);
      }

      return true;
    }

    @Override
    public boolean writeValueSub(Map<String, Value> values) {
      for (Entry<String, EventSubscription> entry : subscriptions
          .entrySet()) {
        values.put(entry.getKey(), entry.getValue().writeValue());
      }
      return true;
    }
  }
}
