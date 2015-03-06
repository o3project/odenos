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

package org.o3project.odenos.remoteobject.actor;

import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Simple actor system implementation.
 *
 * This singleton class implements Actor-Model-like request/event
 * processing.
 *
 * <p>
 * Each {@link org.o3project.odenos.remoteobject.RemoteObject}
 * as an actor has a mailbox. This class assigns a Java thread to
 * RemoteObject when its mailbox has received a mail from another
 * RemoteObject via {@link
 * org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher}.
 *
 * {@literal
 *            RemoteObject                      MessageDispatcher
 *           +------------+                     +---------------------+
 * Actor  (  |  [mailbox]<-----mail, mail, ... --- onMessage() <----------
 * thread  )-|  [state]   |                     |                     |
 *        (  |  [logic]  ----- event ------------> publishEventAsync() -->
 *         ) |           ----- request ----------> requestSync() -------->
 *           |           <---- response ----------               <--------
 *           +------------+                     +---------------------+
 * }
 *
 * <p>
 * MessageDispatcher provides asynchronous and synchronous messaging
 * services: publishEventAsync() and requestSync() methods. RemoteObject
 * as Actor uses these methods to communicate with other RemoteObject.
 *
 * <p>
 * RemoteObject may create another RemoteObject by using SystemManagerIF.
 *
 * <p>
 * Note that synchronous request/response (requestSync()) to another
 * "local" RemoteObject attached to a same "local" MessageDispatcher is
 * performed by using a caller's thread: local-loopback.
 * In that sense, this implementation is NOT a pure Actor Model implementation. Or, you may force
 * {@link org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher}
 * to send "local" requests to pubsub server as well.
 * Refer to {@link org.o3project.odenos.remoteobject.messagingclient.Config}.
 *
 * <p>
 * TODO: study lightweight threads to realize a true Actor Model and
 * increase its performance.
 * TODO: study what the best Queue implementation for the mailbox is.
 *
 * @see org.o3project.odenos.remoteobject.RemoteObject
 * @see org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher
 * @see org.o3project.odenos.remoteobject.messagingclient.Config
 */
public class Actor implements Closeable {

  private static final Logger log = LoggerFactory.getLogger(Actor.class);

  private static ThreadPoolExecutor threadPoolExecutor = null;

  private static volatile Actor actor = null;

  /**
   * Adjust these parameters to gain the best performance for your
   * environment.
   *
   * @param workers the number of worker threads
   * @see org.o3project.odenos.remoteobject.messagingclient.Config
   * @see org.o3project.odenos.remoteobject.messagingclient.ConfigBuilder
   */
  private Actor(int workers) {
    ThreadFactory threadFactory = new ActorThreadFactory("Actor-thread");
    // Single threadPoolExecutor per JVM.
    threadPoolExecutor = new ThreadPoolExecutor(
        workers, // corePoolSize
        workers, // maximumPoolSize
        20, // keepAliveTime
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(10000),
        threadFactory,
        new AbortPolicy()
        );
  }

  /**
   * Returns an instance of {@link Actor}.
   *
   * @param workers the number of worker threads
   * @return singleton of {@link Actor}
   */
  public static final Actor getInstance(int workers) {
    if (actor == null) {
      actor = new Actor(workers);
    }
    return actor;
  }

  /**
   * This method assigns a thread to RemoteObject, to read
   * a mail in a mailbox and do further processing following
   * the mail content.
   *
   * <p>
   * You can dump all the incoming messages by enabling the log
   * threshold to DEBUG. If you also want to dump messaging
   * between RemoteObject instances on same JVM, set
   * MODE.LOCAL_REQUESTS_TO_PUBSUB.
   *
   * @param localObject local object
   * @see org.o3project.odenos.remoteobject.messagingclient.Config
   */
  public void read(final RemoteObject localObject) {

    if (log.isDebugEnabled()) {
      Queue<Mail> mailbox = localObject.getMailbox();
      Mail head = mailbox.peek();
      String type = null;
      String path = "-";
      if (head.request != null) {
        type = head.request.method.name();
        path = "/" + head.to + "/" + head.request.path;
      } else if (head.event != null) {
        type = "NOTIFY";
      } else {
        type = "unidentifed message type!!!";
      }
      // Include MODE.LOCAL_REQUESTS_TO_PUBSUB as MessageDispatcher config
      // to dump all incoming messages including local-loopback messages.
      log.debug("{} mails: #{} \"{}\" <={}= \"{}\" via {}, {}",
              mailbox.size(), head.serial, head.to, type, head.from,
              head.via.getSourceDispatcherId(), path);
    }

    // Picks up a worker thread and assigns it to RemoteObject.
    threadPoolExecutor.execute(new Runnable() {
      @Override
      public void run() {
        Queue<Mail> mailbox = localObject.getMailbox();
        Mail mail = mailbox.poll();
        if (mail != null) {
          // synchronized with MessageDispatcher#requestSync().
          synchronized (localObject) {
            if (mail.request != null) {
              Response response = localObject.dispatchRequest(mail.request);
              try {
                mail.via.publishResponseAsync(mail.sno, mail.from, mail.request, response);
              } catch (IOException e) {
                log.error("unable to send response", e);
              }
            } else if (mail.event != null) {
              localObject.dispatchEvent(mail.event);
            }
          }
        }
        // synchronized with MessageDispatcher#onMessage()
        synchronized (mailbox) {
          if (!mailbox.isEmpty()) {
            read(localObject);
          } else {
            localObject.setRunning(false);
          }
        }
      }
    });
  }

  @Override
  public void close() throws IOException {
    threadPoolExecutor.shutdown();
  }

  private class ActorThreadFactory implements ThreadFactory {
    String name;
    int count = 0;

    ActorThreadFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      return new Thread(runnable, String.format("%s(%d)", name, count++));
    }
  }
}
