package org.o3project.odenos.core.util.zookeeper;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildrenWatcher {

  ZooKeeper zk = null;
  boolean watcherRunning = false;

  public ChildrenWatcher(ZooKeeper zk) {
    this.zk = zk;
  }

  private static final Logger log = LoggerFactory.getLogger(ChildrenWatcher.class);

  public void watchChildren(final String path) {
    if (watcherRunning == true) {
      log.warn("Watch already set");
      return;
    } else {
    }
    Stat stat = null;
    try {
      stat = zk.exists(path, childrenWatcher);
    } catch (KeeperException | InterruptedException e) {
      log.error("operation failed");
    }
    if (stat == null) {
      log.warn("Watch on non-existent path: " + path);
    }
  }

  private void logChildren(final String path) {
    List<String> children = null;
    try {
      children = zk.getChildren(path, false);
    } catch (KeeperException | InterruptedException e) {
      log.error("Operation error");
    }
    if (children == null) {
      log.error("Unable to read children");
    }
  }

  Watcher childrenWatcher=new Watcher(){@Override public void process(WatchedEvent event){switch(event.getType()){case NodeChildrenChanged:String path=event.getPath();log.warn("Node children changed: "+path);logChildren(path);watchChildren(path);break;default:break;}}};
}
