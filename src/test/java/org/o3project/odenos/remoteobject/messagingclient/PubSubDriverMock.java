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

import java.net.ProtocolException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PubSubDriverMock implements IPubSubDriver, IMessageListener {

  private boolean isStarted = false;
  private Set<String> channels = new HashSet<>();

  public String systemManagerId;
  public String host;
  public int port;
  public IMessageListener listener;
  public HashMap<String, byte[]> publishedMessages = new HashMap<>();

  public PubSubDriverMock(Config config, IMessageListener listener) {
    this.systemManagerId = config.getSystemManagerId();
    this.host = config.getHost();
    this.port = config.getPort();
    this.listener = listener;
  }

  public String getSystemManagerId() {
    return systemManagerId;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public IMessageListener getListener() {
    return listener;
  }

  @Override
  public void start() {
    this.isStarted = true;
  }

  @Override
  public void close() {
    this.isStarted = false;
  }

  @Override
  public boolean isStarted() {
    return this.isStarted;
  }

  @Override
  public void subscribeChannels(Set<String> channels) {
    for (String channel : channels) {
      this.channels.add(channel);
    }
  }

  @Override
  public void subscribeChannel(String channel) {
    this.channels.add(channel);
  }

  @Override
  public void unsubscribeChannels(Set<String> channels) {
    for (String channel : channels) {
      this.channels.remove(channel);
    }
  }

  @Override
  public void unsubscribeChannel(String channel) {
    this.channels.remove(channel);
  }

  @Override
  public void unsubscribeAll() {
    this.channels.clear();
  }

  @Override
  public void publish(String channel, byte[] data) {
    publishedMessages.put(channel, data);
  }

  @Override
  public boolean channelExist(String channel) throws ProtocolException {
    return this.channels.contains(channel);
  }

  @Override
  public void systemManagerAttached() {
    // NOP
  }

  @Override
  public <K, V> IMultiMap<K, V> getMultiMap(String name) {
    return null;
  }

  @Override
  public void onMessage(String channel, byte[] message) {
    // NOP
  }

  @Override
  public void onPmessage(String patter, String channel, byte[] message) {
    // NOP
  }

  @Override
  public void onReconnected() {
    // TODO Auto-generated method stub
  }

  @Override
  public void onDisconnected() {
    // TODO Auto-generated method stub
  }

  @Override
  public void psubscribeChannels(Set<String> patterns) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void psubscribeChannel(String pattern) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void punsubscribeChannels(Set<String> patterns) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void punsubscribeChannel(String pattern) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void punsubscribeAll() {
    // TODO Auto-generated method stub
    
  }

}
