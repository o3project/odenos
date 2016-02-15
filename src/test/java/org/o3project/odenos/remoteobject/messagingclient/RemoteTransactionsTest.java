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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.messagingclient.RemoteTransactions;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class RemoteTransactionsTest {

  static final String ObjectId = "objectId";

  RemoteTransactions target = null;

  private static boolean skip = true;

  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }

  @Before
  public void setUp() throws Exception {
    // RemoteTransactions mock
    Config config = new ConfigBuilder()
        .setPubSubDriverImpl(
            org.o3project.odenos.remoteobject.messagingclient.PubSubDriverMock.class.getName()
        )
        .setRemoteTransactionsMax(8888)
        .setRemoteTransactionsInitialTimeout(1)
        .setRemoteTransactionsFinalTimeout(10)
        .build();
    MessageDispatcher disp = new MessageDispatcher(config);
    target = new RemoteTransactions(disp, config);
  }

  @After
  public void tearDown() throws Exception {
    target = null;
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testRemoteTransactions() {
    int initialTimeout = Whitebox.getInternalState(target, "initialTimeout");
    int secondTimeout = Whitebox.getInternalState(target, "secondTimeout");
    ArrayBlockingQueue<SynchronousQueue<Response>> rendezvousPool =
        (ArrayBlockingQueue<SynchronousQueue<Response>>) WhiteboxImpl
            .getInternalState(target, "rendezvousPool");
    assertThat(initialTimeout, is(1));
    assertThat(secondTimeout, is(10 - 1));
    assertThat(rendezvousPool.size(), is(8888));
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testOnFinalize() {
    ArrayBlockingQueue<SynchronousQueue<Response>> rendezvousPool =
        (ArrayBlockingQueue<SynchronousQueue<Response>>) WhiteboxImpl
            .getInternalState(target, "rendezvousPool");
    assertThat(rendezvousPool.size(), is(not(0)));
    target.onFinalize();
    assertThat(rendezvousPool.size(), is(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testSendRequestMessageWithSuccess() throws Exception {

    class ResponseThread extends Thread {
      private ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap;
      public Response response;

      public ResponseThread(
          ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap,
          Response response) {
        this.responseMap = responseMap;
        this.response = response;
      }

      public void run() {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        SynchronousQueue<Response> rendezvous = this.responseMap.remove(0);
        try {
          rendezvous.offer(this.response, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    Response mockResponse = Mockito.mock(Response.class);
    ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap =
        (ConcurrentHashMap<Integer, SynchronousQueue<Response>>)
        Whitebox.getInternalState(target, "responseMap");
    ResponseThread responseThread =
        new ResponseThread(responseMap, mockResponse);
    Request mockRequest = Mockito.mock(Request.class);

    responseThread.start();
    Response resp = target.sendRequest(mockRequest);

    assertThat(resp, is(mockResponse));
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testSignalResponse() throws Exception {
    if (!skip) {
      Request request = new Request("object1", Request.Method.GET, "/test", "txid", null);
      Response mockResponse = Mockito.mock(Response.class);
      ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap =
          (ConcurrentHashMap<Integer, SynchronousQueue<Response>>) Whitebox
              .getInternalState(target, "responseMap");
      MessageDispatcher disp = Whitebox.getInternalState(target, "dispatcher");
      IPubSubDriver driver = Whitebox.getInternalState(disp, "driverImpl");
      driver.subscribeChannel("object1");
      System.out.println(responseMap);
      target.sendRequest(request);
      System.out.println(responseMap);
      assertTrue(responseMap.containsKey(1));
      target.signalResponse(1, mockResponse);
      try {
        Thread.sleep(100);
      } catch (Exception e) {
        e.printStackTrace();
      }
      assertThat(responseMap.size(), is(0));
    }
  }

  @Test
  public final void testSignalResponseWithInitialTimeoutException() throws Exception {
    if (!skip) {
      Request request = new Request("object1", Request.Method.GET, "/test", "txid", null);
      Response mockResponse = Mockito.mock(Response.class);
      @SuppressWarnings("unchecked")
      ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap =
          (ConcurrentHashMap<Integer, SynchronousQueue<Response>>) Whitebox
              .getInternalState(target, "responseMap");
      MessageDispatcher disp = Mockito.mock(MessageDispatcher.class);
      IPubSubDriver checker = Mockito.mock(IPubSubDriver.class);
      Mockito.when(checker.channelExist(anyString())).thenReturn(true);
      Mockito.when(disp.getChannelChecker()).thenReturn(checker);
      Whitebox.setInternalState(target, "dispatcher", disp);
      Whitebox.setInternalState(target, "initialTimeout", 1);
      Whitebox.setInternalState(target, "secondTimeout", 2);
      target.sendRequest(request);
      try {
        Thread.sleep(1100);
      } catch (Exception e) {
        e.printStackTrace();
      }
      target.signalResponse(1, mockResponse);
      try {
        Thread.sleep(100);
      } catch (Exception e) {
        e.printStackTrace();
      }
      assertThat(responseMap.size(), is(0));
    }
  }

  //@Test(expected = TimeoutException.class)
  public final void testSignalResponseWithSecondTimeoutException() throws Exception {
    if (!skip) {
      Request request = new Request("object1", Request.Method.GET, "/test", "txid", null);
      MessageDispatcher disp = Mockito.mock(MessageDispatcher.class);
      IPubSubDriver checker = Mockito.mock(IPubSubDriver.class);
      Mockito.when(checker.channelExist(anyString())).thenReturn(true);
      Mockito.when(disp.getChannelChecker()).thenReturn(checker);
      Whitebox.setInternalState(target, "dispatcher", disp);
      Whitebox.setInternalState(target, "initialTimeout", 1);
      Whitebox.setInternalState(target, "secondTimeout", 2);
      target.sendRequest(request);
    }
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.messagingclient.RemoteTransactions#signalResponse(int, org.o3project.odenos.remoteobject.message.Response)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testSignalResponseWithNonExsistentSno() {
    if (!skip) {
      SynchronousQueue<Response> rendezvous = new SynchronousQueue<Response>();
      Response mockResponse = Mockito.mock(Response.class);
      ConcurrentHashMap<Integer, SynchronousQueue<Response>> responseMap =
          (ConcurrentHashMap<Integer, SynchronousQueue<Response>>) WhiteboxImpl
              .getInternalState(target, "responseMap");
      responseMap.put(1, rendezvous);

      try {
        target.signalResponse(2, mockResponse);
      } catch (Exception e) {
        e.printStackTrace();
      }

      assertThat(responseMap.size(), is(1));
    }
  }
}
