package org.o3project.odenos.core.util.zookeeper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.PurgeTxnLog;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.o3project.odenos.remoteobject.RemoteObjectManager;
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
 * ZooKeeper service for coordinating distributed objects on ODENOS.
 */
public final class ZooKeeperService {

  private static final Logger log =
      LoggerFactory.getLogger(ZooKeeperService.class);

  private static ServerConfig zkServerConfig;
  private static Thread zkServerThread;
  private static String zk_host = "localhost";
  private static int zk_port = 2181;
  private static String zk_dir = "./var/zookeeper";
  private static final String TICK_TIME = "2000";
  private static final int DEFAULT_TIMEOUT = 4000;

  private static ZooKeeperServerMain zkServer = null;

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
   * Cleans up ZooKeeper transaction logs.
   * 
   * Note: this method is to be removed in the future, since ZooKeeper
   * servers should run in other processes for stability.
   */
  public static void cleanUp() {
    try {
      PurgeTxnLog.purge(new File(zk_dir), new File(zk_dir), 3);
    } catch (IOException e) {
      log.error("Unable to clea up ZooKeeper transction logs");
    }
  }

  /**
   * Starts ZooKeeper server. 
   * 
   * Note: this method is to be removed in the future, since ZooKeeper
   * servers should run in other processes for stability.
   */
  public static void startZkServer() {

    if (zkServer == null) {
      zkServer = new ZooKeeperServerMain();
      zkServerConfig = new ServerConfig();
      Properties startupProperties = new Properties();
      // Start ZooKeeper server in this JVM.
      // ZooKeeper server listen port number for clients.
      startupProperties.setProperty("clientPort", new Integer(zk_port).toString());
      // ZooKeeper server log dir. 
      startupProperties.setProperty("dataDir", zk_dir);
      startupProperties.setProperty("tickTime", TICK_TIME);

      QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
      try {
        quorumConfiguration.parseProperties(startupProperties);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      zkServerConfig.readFrom(quorumConfiguration);

      zkServerThread = new Thread() {
        public void run() {
          try {
            // Start the server.
            zkServer.runFromConfig(zkServerConfig);
          } catch (IOException e) {
            log.warn("Retrying to start ZooKeeper server...");
          }
        }
      };
      zkServerThread.setDaemon(true);
      zkServerThread.start();

    } else {
      log.warn("ZooKeeper server already started");
    }

    waitForServerToBeUp();
  }

  /**
   * Stops ZooKeeper server. 
   * 
   * Note: this method is to be removed in the future, since ZooKeeper
   * servers should run in other processes for stability.
   */
  public static void stopZkServer() {
    // Removes ephemeral znodes
    ZooKeeper zk = zooKeeper();

    final Function<String, Void> clearChildren = 
      (path) -> {
        try {
          {
            zk.getChildren(path, false).forEach(new Consumer<String>() {
              public void accept(final String child) {
                try {
                  zk.delete(path + "/" + child, -1);
                } catch (InterruptedException | KeeperException e) {
                  log.error("unable to delete children", e);
                }
              }
            });
          }
         } catch (Exception e) {
           log.error("unable to delete children", e);
         }
        return null;
    };
    
    clearChildren.apply(RemoteObjectManager.ZK_CMPMGR_PATH);
    clearChildren.apply(RemoteObjectManager.ZK_CMP_PATH);
    clearChildren.apply("/system_manager");
    
    Method shutdown = null;
    try {
      shutdown = ZooKeeperServerMain.class.getDeclaredMethod("shutdown");
    } catch (Exception e) {
      log.error("shutdown error", e);
    }
    shutdown.setAccessible(true);
    try {
      shutdown.invoke(zkServer);
    } catch (Exception e) {
      log.error("shutdown error", e);
    }
    try {
      zkServerThread.join(5000);
      zkServerThread = null;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("interrupted");
      zkServerThread = null;
    }
  }

  /**
   * Blocks until ZooKeeper server startup completion.
   * 
   * Note: this method is to be removed in the future, since ZooKeeper
   * servers should run in other processes for stability.
   */
  public static void waitForServerToBeUp() {
    ZooKeeper zk = null;
    while (true) {
      try {
        log.debug("ZooKeeper server is starting...");
        Thread.sleep(2000);
        if (zk == null) {
          zk = zooKeeper(60000, null);
        }
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
   * Returns an instance of ZooKeeper client with default
   * session timeout and watcher.
   *  
   * @return ZooKeeper client instance for the session
   */
  public static ZooKeeper zooKeeper() {
    return zooKeeper(DEFAULT_TIMEOUT, null);
  }

  /**
   * Returns an instance of ZooKeeper client.
   *  
   * @param timeout ZooKeeper session timeout in msec 
   * @param watcher Watcher instance or null for default Watcher
   * @return ZooKeeper client instance for the session
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