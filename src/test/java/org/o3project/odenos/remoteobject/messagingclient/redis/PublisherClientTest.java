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

package org.o3project.odenos.remoteobject.messagingclient.redis;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.messagingclient.redis.PublisherClient;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PowerMockIgnore("org.apache.log4j.*")
@RunWith(PowerMockRunner.class)
public class PublisherClientTest {

  private PublisherClient target = null;
  private static final String HOST = "localhost";
  private static int PORT = 6379;

  private static boolean skip = true;
  
  private RedisServerAddress redisServerAddress = 
      new RedisServerAddress(HOST, PORT, null, 0);

  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() {
    if (!skip) {
      PubSubDriverImpl mockListener = Mockito.mock(PubSubDriverImpl.class);
      target = new PublisherClient(redisServerAddress, 20, mockListener,
          false, null);
      target.start();
    }
  }

  @After
  public void tearDown() {
    if (!skip) {
      target.close();
    }
  }

  @Test
  public final void testPublisherClientPublisher() {
    if (!skip) {
      PubSubDriverImpl mockListener = Mockito.mock(PubSubDriverImpl.class);
      PublisherClient target = new PublisherClient(redisServerAddress, 200,
          mockListener, false, null);
      target.start();
      constructorCommonTest(target);
      target.close();
    }
  }

  private void constructorCommonTest(PublisherClient target) {
    ArrayBlockingQueue<Runnable> queue = Whitebox.getInternalState(target, "publisherQueue");
    assertThat(queue, instanceOf(ArrayBlockingQueue.class));
    assertThat(queue.remainingCapacity(), is(200));
    RedisServerAddress redisServerAddress =
        Whitebox.getInternalState(target, "redisServerAddress");
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
  }

  @Test
  public final void testStart() {
    if (!skip) {
      target.start();
      Thread sendThread = Whitebox.getInternalState(target, "sendThread");
      Thread receiveThread = Whitebox.getInternalState(target, "receiveThread");
      assertTrue(target.isStarted());
      assertTrue(sendThread.isAlive());
      assertTrue(receiveThread.isAlive());
    }
  }

}
