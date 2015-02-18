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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

/**
 * Test class for PacketStatus.
 *
 */
public class PacketStatusTest {

  private static final int MSG_NUM = 9;
  private PacketStatus target;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    target = Mockito.spy(new PacketStatus());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#PacketStatus()}
   * .
   */
  @Test
  public final void testPacketStatus() {

    /*
     * test
     */
    PacketStatus target = new PacketStatus();

    /*
     * check
     */
    assertThat(target.inStatus.packetCount, is(0L));
    assertThat(target.inStatus.packetBytes, is(0L));
    assertThat(target.inStatus.packetQueueCount, is(0L));
    assertThat(target.inStatus.packets, is(nullValue()));
    assertThat(target.outStatus.packetCount, is(0L));
    assertThat(target.outStatus.packetBytes, is(0L));
    assertThat(target.outStatus.packetQueueCount, is(0L));
    assertThat(target.outStatus.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#getInStatus()}
   * .
   */
  @Test
  public final void testGetInStatus() {

    /*
     * set
     */
    PacketStatus target = new PacketStatus();

    /*
     * test
     */
    PacketStatusSub status = target.getInStatus();

    /*
     * check
     */
    assertThat(status.packetCount, is(0L));
    assertThat(status.packetBytes, is(0L));
    assertThat(status.packetQueueCount, is(0L));
    assertThat(status.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#getOutStatus()}
   * .
   */
  @Test
  public final void testGetOutStatus() {

    /*
     * set
     */
    PacketStatus target = new PacketStatus();

    /*
     * test
     */
    PacketStatusSub status = target.getOutStatus();

    /*
     * check
     */
    assertThat(status.packetCount, is(0L));
    assertThat(status.packetBytes, is(0L));
    assertThat(status.packetQueueCount, is(0L));
    assertThat(status.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReadFrom() throws Exception {

    /*
     * set
     */

    Unpacker unpacker = Mockito.mock(Unpacker.class);

    when(unpacker.readMapBegin()).thenReturn(9);
    when(unpacker.readString()).thenReturn("type", "TYPE")
        .thenReturn("in_packet_count")
        .thenReturn("in_packet_bytes")
        .thenReturn("in_packet_queue_count")
        .thenReturn("in_packets", "5")
        .thenReturn("out_packet_count")
        .thenReturn("out_packet_bytes")
        .thenReturn("out_packet_queue_count")
        .thenReturn("out_packets", "5")
        .thenThrow(new IOException());
    when(unpacker.readLong()).thenReturn(123L)
        .thenReturn(456L)
        .thenReturn(789L)
        .thenReturn(258L)
        .thenReturn(369L)
        .thenReturn(159L)
        .thenThrow(new IOException());
    when(unpacker.readArrayBegin()).thenReturn(1)
        .thenReturn(1)
        .thenThrow(new IOException());

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readMapBegin();
    verify(unpacker, times(1)).readMapEnd();

    assertThat(target.inStatus.packetCount, is(123L));
    assertThat(target.inStatus.packetBytes, is(456L));
    assertThat(target.inStatus.packetQueueCount, is(789L));
    assertThat(target.inStatus.packets.size(), is(1));
    assertThat(target.outStatus.packetCount, is(258L));
    assertThat(target.outStatus.packetBytes, is(369L));
    assertThat(target.outStatus.packetQueueCount, is(159L));
    assertThat(target.outStatus.packets.size(), is(1));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReadFromSkipNil() throws Exception {

    /*
     * set
     */

    Unpacker unpacker = Mockito.mock(Unpacker.class);

    when(unpacker.readMapBegin()).thenReturn(9);
    when(unpacker.readString()).thenReturn("type", "TYPE")
        .thenReturn("in_packet_count")
        .thenReturn("in_packet_bytes")
        .thenReturn("in_packet_queue_count")
        .thenReturn("in_packets", "5")
        .thenReturn("out_packet_count")
        .thenReturn("out_packet_bytes")
        .thenReturn("out_packet_queue_count")
        .thenReturn("out_packets", "5")
        .thenThrow(new IOException());
    when(unpacker.readLong()).thenReturn(123L)
        .thenReturn(456L)
        .thenReturn(789L)
        .thenReturn(258L)
        .thenReturn(369L)
        .thenReturn(159L)
        .thenThrow(new IOException());
    when(unpacker.readArrayBegin()).thenReturn(1)
        .thenReturn(1)
        .thenThrow(new IOException());

    when(unpacker.trySkipNil()).thenReturn(true);

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readMapBegin();
    verify(unpacker, times(1)).readMapEnd();

    assertThat(target.inStatus.packetCount, is(123L));
    assertThat(target.inStatus.packetBytes, is(456L));
    assertThat(target.inStatus.packetQueueCount, is(789L));
    assertThat(target.inStatus.packets.size(), is(1));
    assertThat(target.outStatus.packetCount, is(258L));
    assertThat(target.outStatus.packetBytes, is(369L));
    assertThat(target.outStatus.packetQueueCount, is(159L));
    assertThat(target.outStatus.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReadFromDefault() throws Exception {

    /*
     * set
     */

    Unpacker unpacker = Mockito.mock(Unpacker.class);

    when(unpacker.readMapBegin()).thenReturn(9);
    when(unpacker.readString()).thenReturn("type_false", "TYPE")
        .thenReturn("in_packet_count_false")
        .thenReturn("in_packet_bytes_false")
        .thenReturn("in_packet_queue_count_false")
        .thenReturn("in_packets_false", "5")
        .thenReturn("out_packet_count_false")
        .thenReturn("out_packet_bytes_false")
        .thenReturn("out_packet_queue_count_false")
        .thenReturn("out_packets_false", "5")
        .thenThrow(new IOException());
    when(unpacker.readLong()).thenReturn(123L)
        .thenReturn(456L)
        .thenReturn(789L)
        .thenReturn(258L)
        .thenReturn(369L)
        .thenReturn(159L)
        .thenThrow(new IOException());
    when(unpacker.readArrayBegin()).thenReturn(1)
        .thenReturn(1)
        .thenThrow(new IOException());

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readMapBegin();
    verify(unpacker, times(1)).readMapEnd();

    assertThat(target.inStatus.packetCount, is(0L));
    assertThat(target.inStatus.packetBytes, is(0L));
    assertThat(target.inStatus.packetQueueCount, is(0L));
    assertThat(target.inStatus.packets, is(nullValue()));
    assertThat(target.outStatus.packetCount, is(0L));
    assertThat(target.outStatus.packetBytes, is(0L));
    assertThat(target.outStatus.packetQueueCount, is(0L));
    assertThat(target.outStatus.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketStatus#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testWriteTo() throws Exception {

    /*
     * set
     */
    Packer pk = Mockito.mock(Packer.class);
    doReturn(pk).when(pk).write(anyString());

    PacketStatus target = new PacketStatus();

    /*
     * test
     */
    target.writeTo(pk);

    /*
     * check
     */
    verify(pk, times(1)).writeMapBegin(MSG_NUM);
    verify(pk, times(1)).writeMapEnd();
  }

}
