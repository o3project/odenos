package org.o3project.odenos.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * ZooKeeper server for coordinating distributed objects on ODENOS.
 * 
 * Note: this class is to be removed in the future, since ZooKeeper
 * servers should run in other processes for stability.
 */
public final class ZooKeeperService {

  private static final Logger log =
      LoggerFactory.getLogger(ZooKeeperService.class);

  private static ZooKeeperServerMain server;
  private static ServerConfig zkServerConfig;
  private static Thread thread;
  private static String zk_host = "localhost";
  private static int zk_port = 2181;
  private static String zk_dir = "./var/zookeeper";

  public static void setZkHost(String host) {
    zk_host = host;
  }

  public static void setZkPort(int port) {
    zk_port = port;
  }

  public static void setZkDir(String dir) {
    zk_dir = dir;
  }

  /**
   * Constructor. 
   * 
   * TODO: the server should be run as a stand alone process supporting HA.
   */
  public static void startZkServer() {

    if (server == null) {
      server = new ZooKeeperServerMain();
      zkServerConfig = new ServerConfig();
      Properties startupProperties = new Properties();
      startupProperties.setProperty("clientPort", new Integer(zk_port).toString());
      startupProperties.setProperty("dataDir", zk_dir);

      QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
      try {
        quorumConfiguration.parseProperties(startupProperties);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      zkServerConfig.readFrom(quorumConfiguration);

      thread = new Thread() {
        public void run() {
          try {
            server.runFromConfig(zkServerConfig);
          } catch (IOException e) {
            log.warn("Retrying to start ZooKeeper server...");
          }
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            log.error("Thread sleep failed", e);
          }
        }
      };
      thread.setDaemon(true);
      thread.start();

    } else {
      log.warn("ZooKeeper server already started");
    }

    waitForServerToBeUp();
  }

  public static void waitForServerToBeUp() {
    ZooKeeper zk = zooKeeper(60000, null);
    while (true) {
      try {
        Thread.sleep(1000);
        log.debug("ZooKeeper server is starting...");
      } catch (InterruptedException e) {
        log.error("ZooKeeperSerivce startup failed");
      }
      ZooKeeper.States st = zk.getState();
      if (st == ZooKeeper.States.CONNECTED) {
        break;
      }
    }
  }

  /**
   * Returns an instance of ZooKeeper client.
   *  
   * @param zk_host ZooKeeper server host name or IP address
   * @param zk_port ZooKeeper server port number
   * @param watcher Watcher instance or null for default Watcher
   * @return ZooKeeper instance
   */
  public static ZooKeeper zooKeeper(int timeout, Watcher watcher) {
    // Default watcher
    String hostport = zk_host + ":" + new Integer(zk_port).toString();
    log.debug("hostport: {}", hostport);
    if (watcher == null) {
      watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
          log.debug("ZooKeeper server event: {}", event.toString());
        }
      };
    }
    ZooKeeper zk = null;
    try {
      zk = new ZooKeeper(hostport, timeout, watcher);
    } catch (IOException e) {
      log.error("Cannot connect to ZooKeeper server", e);
    }
    return zk;
  }

}