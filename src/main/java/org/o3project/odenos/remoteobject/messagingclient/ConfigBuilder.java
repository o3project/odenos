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

import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

import org.o3project.odenos.remoteobject.messagingclient.Config.MODE;

/**
 * {@link MessageDispatcher} configuration builder.
 * 
 * <p>
 * This builder builds an immutable config for {@link MessageDispatcher}.
 * 
 * @see Config
 * @see MessageDispatcher
 * @see RemoteTransactions
 * @see IPubSubDriver
 * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, RejectedExecutionHandler)
 */
public class ConfigBuilder {

  /**
   *  Default {@link IPubSubDriver} implementation class
   */
  private static final String DEFAULT_PUBSUB_DRIVER_IMPL_CLASS =
      org.o3project.odenos.remoteobject.messagingclient.redis.PubSubDriverImpl.class.getName();
  private String pubSubDriverImpl = DEFAULT_PUBSUB_DRIVER_IMPL_CLASS;

  // MessageDispatcher basic properties
  // "0" means that an IPubSubDriver impl class should set its default.
  private String systemManagerId = "systemmanager";
  private String eventManagerId = "eventmanager";
  private String host = "localhost";  // Master pubsub server host
  private int port = 0;               // Master pubsub server port
  private String hostB = null;        // Changes over to this host
  private int portB = 0;              // Changes over to this port
  private String sourceDispatcherId = UUID.randomUUID().toString();

  // Remote transactions tuning
  private int remoteTransactionsMax = 20;
  private int remoteTransactionsInitialTimeout = 3;
  private int remoteTransactionsFinalTimeout = 30;
 
  // Publisher queue size (Note: this is optional and implementation-specific)
  // "0" means that an IPubSubDriver impl class should set its default.
  private int publisherQueueSize = 0; 
  
  // System Manager status check.
  // Component Managers wait until System Manager becomes active. 
  private boolean systemManagerStatusCheck = true;
  
  // MessageDispatcher high-availability mode
  private EnumSet<MODE> mode = EnumSet.of(
          MODE.RESEND_SUBSCRIBE_ON_RECONNECTED
          );

  // This script is IPubSubDriver-implementation-class-specific.
  // Pubsub server such as Redis support scripting to extend its capabilities.
  private String publishScript =
      "redis.call('publish', KEYS[1]..'@bridge', ARGV[1]) ; redis.call('publish', KEYS[1], ARGV[1])";

  public ConfigBuilder setSystemManagerId(final String systemManagerId) {
    this.systemManagerId = systemManagerId;
    return this;
  }

  public String getSystemManagerId() {
    return systemManagerId;
  }

  public ConfigBuilder setEventManagerId(final String eventManagerId) {
    this.eventManagerId = eventManagerId;
    return this;
  }

  public String getEventManagerId() {
    return eventManagerId;
  }

  /**
   * Master pubsub server host name or IP address.
   * 
   * @param host master pubsub server host name or IP address
   * @return
   */
  public ConfigBuilder setHost(final String host) {
    this.host = host;
    return this;
  }

  public String getHost() {
    return host;
  }

  /**
   * Master pubsub server port number.
   * 
   * @param port master pubsub server port number
   * @return
   */
  public ConfigBuilder setPort(final int port) {
    this.port = port;
    return this;
  }

  public int getPort() {
    return port;
  }

  /**
   * Slave pubsub server host name or IP address.
   * 
   * <p>
   * This parameter is optional.
   * 
   * @param hostB slave pubsub server host name or IP address
   * @return
   */
  public ConfigBuilder setHostB(final String hostB) {
    this.hostB = hostB;
    return this;
  }

  public String getHostB() {
    return hostB;
  }

  /**
   * Slave pubsub server host port number.
   * 
   * <p>
   * This parameter is optional.
   * 
   * @param portB slave pubsub server port number
   * @return
   */
  public ConfigBuilder setPortB(final int portB) {
    this.portB = portB;
    return this;
  }

  public int getPortB() {
    return portB;
  }

  public ConfigBuilder setSourceDispatcherId(final String sourceDispatcherId) {
    this.sourceDispatcherId = sourceDispatcherId;
    return this;
  }

  public String getSourceDispatcherId() {
    return sourceDispatcherId;
  }

  public ConfigBuilder setPubSubDriverImpl(String name) {
    this.pubSubDriverImpl = name;
    return this;
  }

  public String getPubSubDriverImpl() {
    return pubSubDriverImpl; 
  }

  public ConfigBuilder setRemoteTransactionsMax(int remoteTransactionsMax) {
    this.remoteTransactionsMax = remoteTransactionsMax;
    return this;
  }

  public int getRemoteTransactionsMax() {
    return remoteTransactionsMax;
  }

  public ConfigBuilder setRemoteTransactionsInitialTimeout(
      int remoteTransactionsInitialTimeout) {
    this.remoteTransactionsInitialTimeout = remoteTransactionsInitialTimeout;
    return this;
  }

  public int getRemoteTransactionsInitialTimeout() {
    return remoteTransactionsInitialTimeout;
  }

  public ConfigBuilder setRemoteTransactionsFinalTimeout(
      int remoteTransactionsFinalTimeout) {
    this.remoteTransactionsFinalTimeout = remoteTransactionsFinalTimeout;
    return this;
  }

  public int getRemoteTransactionsFinalTimeout() {
    return remoteTransactionsFinalTimeout;
  }
  
  public ConfigBuilder setPublisherQueueSize(int publisherQueueSize) {
    this.publisherQueueSize = publisherQueueSize;
    return this;
  }

  public int getPublisherQueueSize() {
    return publisherQueueSize;
  }
  
  public ConfigBuilder setSystemManagerStatusCheck(boolean check) {
    this.systemManagerStatusCheck = check;
    return this;
  }

  public boolean getSystemManagerStatusCheck() {
    return systemManagerStatusCheck;
  }
  
  public ConfigBuilder setMode(EnumSet<MODE> mode) {
    this.mode = mode;
    return this;
  }
  
  public EnumSet<MODE> getMode() {
    return mode;
  }
  
  public ConfigBuilder setPublishScript(String publishScript) {
    this.publishScript = publishScript;
    return this;
  }
  
  public String getPublishScript() {
    return publishScript;
  }
 
  /**
   * Returns an instance of immutable config. 
   */
  public Config build() {
    return new ConfigImpl(this);
  }
  
  private final class ConfigImpl implements Config {
    
    private final String systemManagerId;
    private final String eventManagerId;
    private final String host;
    private final int port;
    private final String hostB;
    private final int portB;
    private final String sourceDispatcherId;
    private final String pubSubDriverImpl;
    private final int remoteTransactionsMax;
    private final int remoteTransactionsInitialTimeout;
    private final int remoteTransactionsFinalTimeout;
    private final boolean systemManagerStatusCheck;
    private final EnumSet<MODE> mode;
    private final String publishScript;

    private ConfigImpl(ConfigBuilder builder) {
      this.systemManagerId = builder.getSystemManagerId();
      this.eventManagerId = builder.getEventManagerId();
      this.host = builder.getHost();
      this.port = builder.getPort();
      this.hostB = builder.getHostB();
      this.portB = builder.getPortB();
      this.sourceDispatcherId = builder.getSourceDispatcherId();
      this.pubSubDriverImpl = builder.getPubSubDriverImpl();
      this.remoteTransactionsMax = builder.getRemoteTransactionsMax();
      this.remoteTransactionsInitialTimeout = builder.getRemoteTransactionsInitialTimeout();
      this.remoteTransactionsFinalTimeout = builder.getRemoteTransactionsFinalTimeout();
      this.systemManagerStatusCheck = builder.getSystemManagerStatusCheck();
      this.mode = builder.getMode();
      this.publishScript = builder.getPublishScript();
    }

    @Override
    public String getSystemManagerId() {
      return systemManagerId;
    }

    @Override
    public String getEventManagerId() {
      return eventManagerId;
    }

    @Override
    public String getHost() {
      return host;
    }

    @Override
    public int getPort() {
      return port;
    }

    @Override
    public String getHostB() {
      return hostB;
    }

    @Override
    public int getPortB() {
      return portB;
    }

    @Override
    public String getSourceDispatcherId() {
      return sourceDispatcherId;
    }

    @Override
    public String getPubSubDriverImpl() {
      return pubSubDriverImpl; 
    }

    @Override
    public int getRemoteTransactionsMax() {
      return remoteTransactionsMax;
    }

    @Override
    public int getRemoteTransactionsInitialTimeout() {
      return remoteTransactionsInitialTimeout;
    }

    @Override
    public int getRemoteTransactionsFinalTimeout() {
      return remoteTransactionsFinalTimeout;
    }
    
    @Override
    public int getPublisherQueueSize() {
      return publisherQueueSize;
    }

    @Override
    public boolean getSystemManagerStatusCheck() {
      return systemManagerStatusCheck;
    }

    @Override
    public EnumSet<MODE> getMode() {
      return mode;
    }
    
    @Override
    public String getPublishScript() {
      return publishScript;
    }
  }
}
