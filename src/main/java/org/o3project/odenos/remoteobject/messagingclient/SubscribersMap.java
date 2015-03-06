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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Hash map to keep info on which subscriber subscribes which channel.
 * 
 * <p>
 * You may extend this class (override some methods) to enhance the features 
 * or add additional capabilities. 
 */
public class SubscribersMap {

  protected Map<String, Collection<String>> subscribersMap =
      new ConcurrentHashMap<>();

  private static final Logger log = LoggerFactory.getLogger(SubscribersMap.class);

  MessageDispatcher disp;

  public SubscribersMap() {
  }

  protected Set<String> getSubscribedChannels() {
    return subscribersMap.keySet();
  }

  /**
   * Sets subscription.
   * 
   * @param channel channel to be subscribed
   * @param subscriberId subscriber's object ID
   * @return true when new channel is added
   */
  protected boolean setSubscription(String channel, String subscriberId) {
    synchronized (subscribersMap) { // atomic operation
      Collection<String> subscribers = subscribersMap.get(channel);
      if (subscribers == null) {
        // TODO: MessageDispatcher#dispatchEvent() could cause 
        // ConcurrentModificationError when MessageDispatcher#subscribeChannels()
        // or #unsubscribeChannels() are modifying it. Thus, CopyOnWrite
        // version is used here, since the operation expected here is read-intensive.
        // If subscribeChannels()/unsubscribeChannels()'s performance significantly
        // degrades, another measure will be taken... 
        subscribers = new CopyOnWriteArraySet<>();
        subscribers.add(subscriberId);
        subscribersMap.put(channel, subscribers);
        return true;
      } else {
        subscribers.add(subscriberId);
        return false;
      }
    }
  }

  /**
   * Removes subscription.
   * 
   * @param subscriberId subscriber's object ID
   * @param channel channel to be unsubscribed
   * @return true when the existing channel is removed
   */
  protected boolean removeSubscription(String channel, String subscriberId) {
    synchronized (subscribersMap) {
      Collection<String> subscribersList = subscribersMap.get(channel);
      if (subscribersList != null) {
        subscribersList.remove(subscriberId);
        if (subscribersList.size() == 0) {
          subscribersMap.remove(channel);
          return true;
        } else {
          return false;
        }
      } else {
        log.warn("channel already unregistered");
        return false;
      }
    }
  }

  /**
   * Removes subscriber.
   * 
   * @param subscriberId subscriber to be removed
   */
  protected void removeSubscriber(String subscriberId) {
    Collection<String> channelsToBeRemoved = new ArrayList<>();
    synchronized (subscribersMap) {
      for (String channel : subscribersMap.keySet()) {
        Collection<String> subscribersList = subscribersMap.get(channel);
        subscribersList.remove(subscriberId);
        if (subscribersList.size() == 0) {
          channelsToBeRemoved.add(channel);
        }
      }
      for (String channel : channelsToBeRemoved) {
        subscribersMap.remove(channel);
      }
    }
  }

  /**
   * Returns a list of subscribers of the channel.
   * 
   * @param channel channel
   * @return a list of subscribers of the channel
   */
  protected Collection<String> getSubscribers(String channel) {
    return subscribersMap.get(channel);
  }

  protected void clear() {
    subscribersMap.clear();
  }
}
