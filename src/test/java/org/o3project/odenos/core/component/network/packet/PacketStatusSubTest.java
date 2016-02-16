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
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for PacketStatusSub.
 *
 * 
 *
 */
public class PacketStatusSubTest {

  public PacketStatusSub target;
  public final long packetCount = 10000;
  public final long packetBytes = 20000;
  public final long packetQueueCount = 30000;
  @SuppressWarnings("serial")
  public List<String> packets = new ArrayList<String>() {
    {
      add("packet01");
      add("packet02");
    }
  };

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
    target = new PacketStatusSub();
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
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatusSub#getPacketCount()}
   * .
   */
  @Test
  public final void testGetPacketCount() {

    target.packetCount = packetCount;
    assertThat(target.getPacketCount(), is(packetCount));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatusSub#getPacketBytes()}
   * .
   */
  @Test
  public final void testGetPacketBytes() {
    target.packetBytes = packetBytes;
    assertThat(target.getPacketBytes(), is(packetBytes));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatusSub#getPacketQueueCount()}
   * .
   */
  @Test
  public final void testGetPacketQueueCount() {
    target.packetQueueCount = packetQueueCount;
    assertThat(target.getPacketQueueCount(), is(packetQueueCount));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatusSub#getPackets()}
   * .
   */
  @Test
  public final void testGetPackets() {
    target.packets = packets;
    assertThat(target.getPackets(), is(packets));
  }

}
