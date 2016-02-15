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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Test class for PacketQueue.
 *
 */
public class PacketQueueTest {

  private PacketQueue target;

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
    target = spy(new PacketQueue());
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
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#PacketQueue()}
   * .
   */
  @Test
  public final void testPacketQueue() {
    /*
     * test
     */
    PacketQueue target = new PacketQueue();

    /*
     * check
     */
    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    Map<String, Packet> packetDict = WhiteboxImpl.getInternalState(target, "packetDict");

    assertThat(packets.size(), is(0));
    assertThat(packetDict.size(), is(0));

    long packetCount = WhiteboxImpl.getInternalState(target, "packetCount");
    assertThat(packetCount, is(0L));

    long packetBytes = WhiteboxImpl.getInternalState(target, "packetBytes");
    assertThat(packetBytes, is(0L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#enqueuePacket(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testEnqueuePacket() {
    /*
     * set
     */
    Packet packet = new InPacket();
    Packet packet1 = new InPacket();
    Packet packet2 = new InPacket();

    /*
     * test
     */
    packet = target.enqueuePacket(packet);
    packet1 = target.enqueuePacket(packet1);
    packet2 = target.enqueuePacket(packet2);

    /*
     * check
     */
    assertThat(packet.getPacketId(), is("0000000000"));
    assertThat(packet1.getPacketId(), is("0000000001"));
    assertThat(packet2.getPacketId(), is("0000000002"));

    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    assertThat(packets.size(), is(3));

    Map<String, Packet> packetDict = WhiteboxImpl.getInternalState(target, "packetDict");
    assertThat(packetDict.size(), is(3));

    long packetCount = WhiteboxImpl.getInternalState(target, "packetCount");
    assertThat(packetCount, is(3L));

    long packetBytes = WhiteboxImpl.getInternalState(target, "packetBytes");
    assertThat(packetBytes, is(0L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#enqueuePacket(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testEnqueuePacketNull() {
    /*
     * test
     */
    Packet packet = target.enqueuePacket(null);

    /*
     * check
     */
    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    Map<String, Packet> packetDict = WhiteboxImpl.getInternalState(target, "packetDict");

    assertThat(packet, is(nullValue()));
    assertThat(packets.size(), is(0));
    assertThat(packetDict.size(), is(0));

    long packetCount = WhiteboxImpl.getInternalState(target, "packetCount");
    assertThat(packetCount, is(0L));

    long packetBytes = WhiteboxImpl.getInternalState(target, "packetBytes");
    assertThat(packetBytes, is(0L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#dequeuePacket()}
   * .
   */
  @Test
  public final void testDequeuePacket() {

    /*
     * set
     */
    Packet packet = new InPacket();
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Packet result = target.dequeuePacket();

    /*
     * check
     */
    assertThat(result, is(packet));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#dequeuePacket()}
   * .
   */
  @Test
  public final void testDequeuePacketNull() {
    /*
     * test
     */
    Packet result = target.dequeuePacket();

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#peekPacket()}
   * .
   */
  @Test
  public final void testPeekPacket() {

    /*
     * set
     */
    Packet packet = new InPacket();
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Packet result = target.peekPacket();

    /*
     * check
     */
    assertThat(result, is(packet));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getPacket(java.lang.String)}
   * .
   */
  @Test
  public final void testGetPacket() {

    /*
     * set
     */
    Packet packet = new InPacket();
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Packet result = target.getPacket(packet.getPacketId());

    /*
     * check
     */
    assertThat(result, is(packet));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#pickPacket(java.lang.String)}
   * .
   */
  @Test
  public final void testPickPacket() {

    /*
     * set
     */
    Packet packet = new InPacket();
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Packet result = target.pickPacket(packet.getPacketId());

    /*
     * check
     */
    assertThat(result, is(packet));
    assertThat(target.getPacket(packet.getPacketId()), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#pickPacket(java.lang.String)}
   * .
   */
  @Test
  public final void testPickPacketPacketNull() {

    /*
     * set
     */
    Packet packet = new InPacket();
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Packet result = target.pickPacket("0000000003");

    /*
     * check
     */
    assertThat(result, is(nullValue()));
    assertThat(target.getPacket(packet.getPacketId()), is(packet));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#pickPacket(java.lang.String)}
   * .
   */
  @Test
  public final void testPickPacketPacketNotRemove() {

    /*
     * set
     */
    Packet packet = new InPacket();

    when(target.getPacket("Packet")).thenReturn(packet);

    /*
     * test
     */
    Packet result = target.pickPacket("Packet");

    /*
     * check
     */
    assertThat(result, is(nullValue()));
    assertThat(target.getPacket(packet.getPacketId()), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#clearPackets()}
   * .
   */
  @Test
  public final void testClearPackets() {
    /*
     * set
     */
    Packet packet = new InPacket();
    Packet packet1 = new InPacket();
    Packet packet2 = new InPacket();
    packet = target.enqueuePacket(packet);
    packet1 = target.enqueuePacket(packet1);
    packet2 = target.enqueuePacket(packet2);

    /*
     * test
     */
    target.clearPackets();

    /*
     * check
     */
    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    Map<String, Packet> packetDict = WhiteboxImpl.getInternalState(target, "packetDict");

    assertThat(packets.size(), is(0));
    assertThat(packetDict.size(), is(0));

    long packetCount = WhiteboxImpl.getInternalState(target, "packetCount");
    assertThat(packetCount, is(3L));

    long packetBytes = WhiteboxImpl.getInternalState(target, "packetBytes");
    assertThat(packetBytes, is(0L));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getPacketStatus()}
   * .
   */
  @Test
  public final void testGetPacketStatusInPacketQueue() {

    /*
     * set
     */
    PacketQueue target = new InPacketQueue();

    Packet packet = new InPacket();
    Packet packet1 = new InPacket();
    Packet packet2 = new InPacket();
    packet = target.enqueuePacket(packet);
    packet1 = target.enqueuePacket(packet1);
    packet2 = target.enqueuePacket(packet2);

    /*
     * test
     */
    PacketStatus result = target.getPacketStatus();

    /*
     * check
     */
    assertThat(result.inStatus.packetCount, is(3L));
    assertThat(result.inStatus.packetBytes, is(0L));
    assertThat(result.inStatus.packetQueueCount, is(3L));
    assertThat(result.inStatus.packets.size(), is(3));
    assertThat(result.outStatus.packetCount, is(0L));
    assertThat(result.outStatus.packetBytes, is(0L));
    assertThat(result.outStatus.packetQueueCount, is(0L));
    assertThat(result.outStatus.packets, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getPacketStatus()}
   * .
   */
  @Test
  public final void testGetPacketStatusOutPacketQueue() {

    /*
     * set
     */
    PacketQueue target = new OutPacketQueue();

    Packet packet = new InPacket();
    Packet packet1 = new InPacket();
    Packet packet2 = new InPacket();
    packet = target.enqueuePacket(packet);
    packet1 = target.enqueuePacket(packet1);
    packet2 = target.enqueuePacket(packet2);

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
    assertThat(result.inStatus.packets, is(nullValue()));
    assertThat(result.outStatus.packetCount, is(3L));
    assertThat(result.outStatus.packetBytes, is(0L));
    assertThat(result.outStatus.packetQueueCount, is(3L));
    assertThat(result.outStatus.packets.size(), is(3));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getPacketMessages(org.o3project.odenos.core.component.network.packet.PacketQuery)}
   * .
   */
  @Test
  public final void testGetPacketMessages() {

    /*
     * set
     */
    String queriesString = "attributes=\"key0=value0\"";
    PacketQuery<String> query = spy(new PacketQuery<String>(queriesString));
    query.parse();

    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key0", "value0");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    Packet packet =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Map<String, Packet> result = target.getPacketMessages(query);

    /*
     * check
     */
    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    assertThat(query.matchExactly(packet), is(true));
    assertThat(packets.size(), is(1));
    assertThat(result.size(), is(1));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getPacketMessages(org.o3project.odenos.core.component.network.packet.PacketQuery)}
   * .
   */
  @Test
  public final void testGetPacketMessagesNotMatchExactly() {

    /*
     * set
     */
    String queriesString = "attributes=\"key0=value0\"";
    PacketQuery<String> query = spy(new PacketQuery<String>(queriesString));
    query.parse();

    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "0000000000");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    Packet packet =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    packet = target.enqueuePacket(packet);

    /*
     * test
     */
    Map<String, Packet> result = target.getPacketMessages(query);

    /*
     * check
     */
    LinkedList<Packet> packets = WhiteboxImpl.getInternalState(target, "packets");
    assertThat(query.matchExactly(packet), is(false));
    assertThat(packets.size(), is(1));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#calcPacketStatus(org.o3project.odenos.core.component.network.packet.PacketStatusSub)}
   * .
   */
  @Test
  public final void testCalcPacketStatus() {

    /*
     * set
     */
    Packet packet = new InPacket();
    Packet packet1 = new InPacket();
    Packet packet2 = new InPacket();
    packet = target.enqueuePacket(packet);
    packet1 = target.enqueuePacket(packet1);
    packet2 = target.enqueuePacket(packet2);

    PacketStatusSub status = spy(new PacketStatusSub());

    /*
     * test
     */
    target.calcPacketStatus(status);

    /*
     * check
     */
    assertThat(status.packetCount, is(3L));
    assertThat(status.packetBytes, is(0L));
    assertThat(status.packetQueueCount, is(3L));
    assertThat(status.packets.size(), is(3));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQueue#getUniquePacketId()}
   * .
   */
  @Test
  public final void testGetUniquePacketId() throws Exception {

    /*
     * set
     */
    PacketQueue target = spy(new PacketQueue());

    /*
     * test
     */
    String result00 = Whitebox.invokeMethod(target, "getUniquePacketId");
    String result01 = Whitebox.invokeMethod(target, "getUniquePacketId");
    String result02 = Whitebox.invokeMethod(target, "getUniquePacketId");

    /*
     * check
     */
    assertThat(result00, is("0000000000"));
    assertThat(result01, is("0000000001"));
    assertThat(result02, is("0000000002"));
  }

}
