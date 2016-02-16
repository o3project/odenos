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

package org.o3project.odenos.core.component.network.packet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * Test class for PacketQueueSet.
 *
 * 
 *
 */
public class PacketQueueSetTest {

  private PacketQueueSet target;

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() throws Exception {
    target = spy(new PacketQueueSet());
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueueSet#PacketQueueSet()}
   * .
   */
  @Test
  public final void testPacketQueueSet() {
    PacketQueueSet target = new PacketQueueSet();

    InPacketQueue inQueue = WhiteboxImpl.getInternalState(target, "inQueue");
    OutPacketQueue outQueue = WhiteboxImpl.getInternalState(target, "outQueue");

    assertThat(inQueue, not(nullValue()));
    assertThat(outQueue, not(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueueSet#getInQueue()}
   * .
   */
  @Test
  public final void testGetInQueue() {
    PacketQueue result = target.getInQueue();

    assertThat(result, is(InPacketQueue.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueueSet#getOutQueue()}
   * .
   */
  @Test
  public final void testGetOutQueue() {
    PacketQueue result = target.getOutQueue();

    assertThat(result, is(OutPacketQueue.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueueSet#getPacketStatus()}
   * .
   */
  @Test
  public final void testGetPacketStatus() {
    /*
     * set
     */
    PacketQueueSet target = new PacketQueueSet();

    /*
     * test
     */
    PacketStatus result = target.getPacketStatus();

    /*
     * check
     */
    assertThat(result.inStatus.packetCount, is(0L));
    assertThat(result.inStatus.packetBytes, is(0L));
    assertThat(result.inStatus.packetQueueCount, is(0L));
    assertThat(result.inStatus.packets.size(), is(0));
    assertThat(result.outStatus.packetCount, is(0L));
    assertThat(result.outStatus.packetBytes, is(0L));
    assertThat(result.outStatus.packetQueueCount, is(0L));
    assertThat(result.outStatus.packets.size(), is(0));
  }

}
