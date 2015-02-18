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
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.messagingclient.Config;
import org.o3project.odenos.remoteobject.messagingclient.ConfigBuilder;
import org.o3project.odenos.remoteobject.messagingclient.IMessageListener;
import org.powermock.reflect.Whitebox;

public class PubSubDriverImplTest {

  private static final Config config = new ConfigBuilder()
                                        .setSystemManagerId("systemmanager")
                                        .setHost("localhost")
                                        .setHost("localhost")
                                        .setPort(6379)
                                        .build();
  private IMessageListener listener = Mockito.mock(IMessageListener.class);

  private static boolean skip = true;
  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }
  
  @Before
  public void setUp() {
  }

  @Test
  public void testDriverImplTest() {
    if (!skip) {
      try (PubSubDriverImpl target = new PubSubDriverImpl(config, listener)) {
        assertThat(Whitebox.getInternalState(target, "publisherClient"),
            instanceOf(PublisherClient.class));
        assertThat(Whitebox.getInternalState(target, "subscriberClient"),
            instanceOf(SubscriberClient.class));
        assertThat(Whitebox.getInternalState(target, "channelCheckerClient"),
            instanceOf(ChannelCheckerClient.class));
      }
    }
  }

  @Test
  public void testStart() {
    if (!skip) {
      try (PubSubDriverImpl target = new PubSubDriverImpl(
          config, listener)) {
        target.start();
        assertTrue(((PublisherClient) Whitebox.getInternalState(target, "publisherClient"))
            .isStarted());
        assertTrue(((SubscriberClient) Whitebox.getInternalState(target, "subscriberClient"))
            .isStarted());
      }
    }
  }

  @Test
  public void isStareted() {
    if (!skip) {
      try (PubSubDriverImpl target = new PubSubDriverImpl(
          config, listener)) {
        target.start();
        assertTrue(target.isStarted());
      }
    }
  }

}
