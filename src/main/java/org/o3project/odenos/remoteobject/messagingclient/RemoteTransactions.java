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

import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>Remote transactions.</h1>
 * 
 * <p>
 * This class works under requestSync() of MessageDispatcher.
 * 
 * <pre>
 * sendRequest()
 *       | put & poll (3sec timeout then 27sec timeout)
 *       V
 * [[rendezvous(sno)][rendezvous(sno)]...] responseMap 
 *       ^
 *       | offer
 * signalResponse()
 * </pre>
 *
 * @see MessageDispatcher
 */
class RemoteTransactions {

  private static final Logger log = LoggerFactory.getLogger(RemoteTransactions.class);

  private AtomicInteger seqno = new AtomicInteger(0);
  private ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap =
      new ConcurrentHashMap<Integer, SynchronousQueue<Response>>();
  private ArrayBlockingQueue<SynchronousQueue<Response>> rendezvousPool;
  private final int secondTimeout;
  private final int initialTimeout;

  private MessageDispatcher dispatcher = null;

  /**
   * Constructor.
   * 
   * @param objectId object ID.
   * @param dispatcher MessageDispatcher object.
   */
  RemoteTransactions(MessageDispatcher dispatcher, Config config) {
    this.dispatcher = dispatcher;
    initialTimeout = config.getRemoteTransactionsInitialTimeout();
    secondTimeout =
        config.getRemoteTransactionsFinalTimeout() - initialTimeout;
    rendezvousPool = new ArrayBlockingQueue
        <SynchronousQueue<Response>>(config.getRemoteTransactionsMax());
    for (int i = 0; i < config.getRemoteTransactionsMax(); i++) {
      try {
        rendezvousPool.put(new SynchronousQueue<Response>());
      } catch (InterruptedException e) {
        log.error("cannot return SynchronousQueue to rendezvous pool");
      }
    }
  }

  void onFinalize() {
    rendezvousPool.clear();
  }

  /**
   * Sends Request to RemoteObject. 
   * 
   * @param sno sequential number.
   * @param response a response.
   * @throws TimeoutException 
   * @throws IOException 
   * @throws InterruptedException 
   * @throws IllegalArgumentException 
   * @throws Exception if an exception occurs.
   */
  Response sendRequest(Request request) 
      throws IllegalArgumentException, InterruptedException,
      IOException, TimeoutException {
    return sendRequest(request, null);
  }

  Response sendRequest(Request request, String sourceObjectId)
      throws InterruptedException, IOException,
      TimeoutException, IllegalArgumentException {

    // Increments Sequence No.
    int sno = seqno.getAndIncrement();
    // Rendezvous point for Request/Response
    SynchronousQueue<Response> rendezvous = null;

    // Takes a rendezvous point from the pool
    try {
      rendezvous = rendezvousPool.take();
    } catch (InterruptedException e) {
      throw e;
    }

    // Response waiting queue 
    responseMap.put(sno, rendezvous);

    try {
      // Sends the request to RemoteObject
      dispatcher.publishRequestAsync(sno, request, sourceObjectId);
    } catch (IOException e) {
      responseMap.remove(sno);
      rendezvousPool.put(rendezvous);
      throw e;
    }

    // Waits for the response from RemoteObjet
    Response response = rendezvous.poll(initialTimeout, TimeUnit.SECONDS);

    if (response == null) { // INITIAL_TIMEOUT expired
      if (log.isDebugEnabled()) {
        log.debug("request timeout (initial)");
      }
      // Checks if the channel (i.e., object ID) exists)
      if (dispatcher.getChannelChecker().channelExist(request.objectId)) {
        // Waits for the response
        response = rendezvous.poll(secondTimeout, TimeUnit.SECONDS);
        // Returns the rendezvous point object to the pool
        rendezvousPool.put(rendezvous);
        if (response == null) { // remoteRequestTimeout expired
          if (log.isDebugEnabled()) {
            log.debug("request timeout (final)");
          }
          responseMap.remove(sno);
          throw new TimeoutException("no reply from " + request.objectId);
        }
      } else { // The channel does not exist
        rendezvousPool.put(rendezvous);
        responseMap.remove(sno);
        if (log.isDebugEnabled()) {
          log.debug("non-existent channel");
        }
        throw new IllegalArgumentException("request to non-existent component: "
            + request.objectId);
      }
    } else {
      rendezvousPool.put(rendezvous);
    }
    return response;
  }

  /**
   * Signals Response to rendezvous point.
   * 
   * @param sno sequential number.
   * @param response a response.
   * @throws Exception if an exception occurs.
   */
  void signalResponse(int sno, Response response) throws Exception {
    if (responseMap.containsKey(sno)) {
      SynchronousQueue<Response> rendezvous = responseMap.remove(sno);
      if (!rendezvous.offer(response, secondTimeout, TimeUnit.SECONDS)) {
        throw new TimeoutException(String.format(
            "cannnot respond to rendezvous point: response = %s",
            response.getBodyValue()));
      }
    }
  }
}
