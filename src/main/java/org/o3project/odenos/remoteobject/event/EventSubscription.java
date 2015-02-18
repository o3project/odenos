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

package org.o3project.odenos.remoteobject.event;

import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * EventSubscription represents subscription issued by RemoteObjects and managed
 * by EventManager.
 *
 */
public class EventSubscription extends OdenosMessage {
  private String subscriberId = null;
  private Map<String, Set<String>> eventFilters = new HashMap<String, Set<String>>();

  // Diff are registered
  private Map<String, Set<String>> channelsToBeSubscribed = new HashMap<String, Set<String>>();
  // Diff are registered
  private Map<String, Set<String>> channelsToBeUnsubscribed = new HashMap<String, Set<String>>();

  /**
   * Constructor.
   */
  public EventSubscription() {
  }

  /**
   * Constructor.
   * @param subscriberId subscriber ID.
   */
  public EventSubscription(final String subscriberId) {
    this.subscriberId = subscriberId;
  }

  /**
   * Returns a subscriber ID.
   * @return subscriber ID.
   */
  public String getSubscriberId() {
    return this.subscriberId;
  }

  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  public Map<String, Set<String>> getFilters() {
    return eventFilters;
  }

  /**
   * Appends a filter.
   * @param publisherId publisher ID.
   * @param eventId event ID.
   */
  public void addFilter(String publisherId, String eventId) {
    if (!eventFilters.containsKey(publisherId)) {
      eventFilters.put(publisherId, new HashSet<String>());
    }
    // Diff 
    if (!channelsToBeSubscribed.containsKey(publisherId)) {
      channelsToBeSubscribed.put(publisherId, new HashSet<String>());
    }
    eventFilters.get(publisherId).add(eventId);
    // Diff
    channelsToBeSubscribed.get(publisherId).add(eventId);
  }

  /**
   * Removes a filter.
   * @param publisherId publisher ID.
   * @param eventId event ID.
   */
  public void removeFilter(String publisherId, String eventId) {
    if (eventFilters.containsKey(publisherId)) {
      eventFilters.get(publisherId).remove(eventId);
    }
    // Diff
    if (!channelsToBeUnsubscribed.containsKey(publisherId)) {
      channelsToBeUnsubscribed.put(publisherId, new HashSet<String>());
    }
    // Diff
    channelsToBeUnsubscribed.get(publisherId).add(eventId);
  }

  public void clearFilter() {
    eventFilters.clear();
  }

  /**
   * Checks if the channel has been subscribed.
   * 
   * @param publisherId publisher id
   * @param eventId event id
   * @return true -- the channel has been subscribed.
   */
  public boolean contains(String publisherId, String eventId) {
    boolean subscribing = false;
    if (eventFilters.containsKey(publisherId)) {
      if (eventFilters.get(publisherId).contains(eventId)) {
        subscribing = true;
      }
    }
    return subscribing;
  }

  public boolean hasChannelsToBeSubscribed() {
    return (!channelsToBeSubscribed.isEmpty());
  }

  public boolean hasChannelsToBeUnsubscribed() {
    return (!channelsToBeUnsubscribed.isEmpty());
  }

  public Map<String, Set<String>> getChannelsToBeSubscribed() {
    return channelsToBeSubscribed;
  }

  public Map<String, Set<String>> getChannelsToBeUnsubscribed() {
    return channelsToBeUnsubscribed;
  }

  public void clearChannelsToBeSubscribed() {
    channelsToBeSubscribed.clear();
  }

  public void clearChannelsToBeUnsubscribed() {
    channelsToBeUnsubscribed.clear();
  }

  public void removePublisherId(String publisherId) {
    eventFilters.remove(publisherId);
  }

  @Override
  public boolean readValue(Value value) {
    MapValue map = value.asMapValue();

    Value subscriberValue = map.get(ValueFactory
        .createRawValue("subscriber_id"));
    if (subscriberValue != null) {
      subscriberId = subscriberValue.asRawValue().getString();
    } else {
      return false;
    }

    Value filtersValue = map.get(ValueFactory
        .createRawValue("event_filters"));
    if (filtersValue != null) {
      MapValue filtersMap = filtersValue.asMapValue();
      for (Entry<Value, Value> entry : filtersMap.entrySet()) {
        if (entry.getKey().isNilValue()
            || entry.getValue().isNilValue()) {
          return false;
        }
        String publisherId = entry.getKey().asRawValue()
            .getString();
        ArrayValue eventsArray = entry.getValue().asArrayValue();

        for (Value eventValue : eventsArray) {
          String event = eventValue.asRawValue().getString();
          addFilter(publisherId, event);
        }
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put("subscriber_id",
        ValueFactory.createRawValue(subscriberId));

    Value[] filtersMap = new Value[eventFilters.size() * 2];
    int num1 = 0;
    for (Entry<String, Set<String>> entry : eventFilters.entrySet()) {
      Value[] eventsArray = new Value[entry.getValue().size()];

      int num2 = 0;
      for (String eventId : entry.getValue()) {
        eventsArray[num2++] = ValueFactory.createRawValue(eventId);
      }

      filtersMap[num1 * 2] = ValueFactory.createRawValue(entry
          .getKey());
      filtersMap[num1 * 2 + 1] = ValueFactory
          .createArrayValue(eventsArray);
      ++num1;
    }

    values.put("event_filters", ValueFactory.createMapValue(filtersMap));

    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof EventSubscription)) {
      return false;
    }

    EventSubscription eventSubscription = (EventSubscription) obj;

    return this.subscriberId
        .equals(eventSubscription.getSubscriberId());
  }
}
