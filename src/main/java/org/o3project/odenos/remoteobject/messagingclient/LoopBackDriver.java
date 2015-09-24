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
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

/**
 * {@link IPubSubDriver} implementation as a loopback interface for events.
 */
public class LoopBackDriver implements IPubSubDriver {

  private static final Logger log = LogManager.getLogger(LoopBackDriver.class);

  private IMessageListener listener;
  
  public LoopBackDriver(IMessageListener listener) {
    this.listener = listener;
  }

  @Override
  public void start() {
    log.warn("unsupported");
  }

  @Override
  public void close() {
    log.warn("unsupported");
  }

  @Override
  public boolean isStarted() {
    log.warn("unsupported");
    return true;
  }

  @Override
  public void subscribeChannels(Set<String> channels) {
    log.warn("unsupported");
  }

  @Override
  public void subscribeChannel(String channel) {
    log.warn("unsupported");
  }

  @Override
  public void unsubscribeChannels(Set<String> channels) {
    log.warn("unsupported");
  }

  @Override
  public void unsubscribeChannel(String channel) {
    log.warn("unsupported");
  }

  @Override
  public void unsubscribeAll() {
    log.warn("unsupported");
  }

  @Override
  public void psubscribeChannels(Set<String> patterns) {
    log.warn("unsupported");
  }

  @Override
  public void psubscribeChannel(String pattern) {
    log.warn("unsupported");
  }

  @Override
  public void punsubscribeChannels(Set<String> patterns) {
    log.warn("unsupported");
  }

  @Override
  public void punsubscribeChannel(String pattern) {
    log.warn("unsupported");
  }

  @Override
  public void punsubscribeAll() {
    log.warn("unsupported");
  }

  @Override
  public void publish(String channel, byte[] data) {
    listener.onMessage(channel, data);
  }

  @Override
  public boolean channelExist(String channel) throws ProtocolException {
    log.warn("unsupported");
    return false;
  }

  @Override
  public <K, V> IMultiMap<K, V> getMultiMap(String name) {
    return null;
  }
}
