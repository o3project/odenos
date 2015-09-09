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

package org.o3project.odenos.core.util.zookeeper;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZooKeeper client with a callback handler.
 */
public class KeepAliveClient {

  private static final Logger log = LoggerFactory.getLogger(KeepAliveClient.class);

  ZooKeeper zk = null;
  Set<String> paths = new ConcurrentSkipListSet<>();

  public KeepAliveClient() {
    connect();
  }

  private void connect() {
    zk = ZooKeeperService.zooKeeper(5000, new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        switch (event.getState()) {
        case Expired:
          log.warn("ZooKeeper session exipired");
          connect();
          createPaths(paths, CreateMode.EPHEMERAL);
          break;
        default:
          break;
        }
      }
    });
  }

  /**
   * Creates a znode on ZooKeeper server.
   * 
   * @param path
   * @param mode
   */
  public synchronized void createPath(final String path, CreateMode mode) {
    if (mode == CreateMode.PERSISTENT) {
      try {
        if (zk.exists(path, false) == null) {
          zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
      } catch (KeeperException | InterruptedException e) {
        log.error("Unable to create a path: " + path, e);
      }
    } else if (mode == CreateMode.EPHEMERAL) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, mode, createPathCallback, new byte[0]);
    } else {
      log.warn("Unsupported mode: " + mode.toString());
    }
  }

  /**
   * Deletes a znode on ZooKeeper server.
   * 
   * @param path
   */
  public synchronized void deletePath(final String path) {
    try {
      zk.delete(path, -1);
    } catch (InterruptedException | KeeperException e) {
      log.error("Unable to delete a path: " + path);
    }
  }

  /**
   * Callback handler for asynchronous ZooKeeper.create() method.
   */
  StringCallback createPathCallback = new StringCallback() {
    public void processResult(int rc, String path, Object ctx, String name) {
      Code code = Code.get(rc);
      switch (code) {
      case CONNECTIONLOSS:
        paths.add(path);
        createPaths(paths, CreateMode.EPHEMERAL);
        break;
      case OK:
        paths.add(path);
        break;
      case NODEEXISTS:
        log.warn("node exists: " + path);
        break;
      default:
        log.error("process result: " + code.toString());
        break;
      }
    }
  };

  private void createPaths(final Set<String> paths, final CreateMode mode) {
    Iterator<String> iterator = paths.iterator();
    while (iterator.hasNext()) {
      String path = iterator.next();
      createPath(path, mode);
    }
  }

  Set<String> watchedPaths = new ConcurrentSkipListSet<>();

  /**
   * Sets a watcher on a znode path to detect its disappearance.
   * 
   * @param path
   */
  public void watchPath(final String path, final String message) {
    try {
      zk.exists(path, new Watcher() {
        @Override
        public void process(WatchedEvent event) {
          String path = event.getPath();
          switch (event.getType()) {
          case NodeCreated:
            log.info("znode created: " + path);
            watchPath(path, message);
            break;
          case NodeDeleted:
            if (message == null) {
              log.warn("znode deleted: " + path);
            } else {
              log.warn("{}: {}", message, path);
            }
            break;
          default:
            log.error("Unidentified watch event: " + event.toString());
            break;
          }
        }
      });
    } catch (KeeperException | InterruptedException e) {
      log.error("ZooKeeper operation error");
    }
  }
  
  /**
   * Sets a watcher on a znode path. 
   * 
   * @param path
   * @param watcher
   */
  public void watchPath(final String path, Watcher watcher) {
    try {
      zk.exists(path, watcher);
    } catch (KeeperException | InterruptedException e) {
      log.error("ZooKeeper operation error");
    }
  }
}
