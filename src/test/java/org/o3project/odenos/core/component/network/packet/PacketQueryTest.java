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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for PacketQuery.
 *
 * 
 *
 */
public class PacketQueryTest {

  private PacketQuery<Object> target;

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
    String queriseString = "attributes=\"key1=value1,key2=value2\"";
    target = spy(new PacketQuery<Object>(queriseString));
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
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#PacketQuery(java.lang.String)}
   * .
   */
  @Test
  public final void testPacketQuery() {

    /*
     * test
     */
    PacketQuery<Object> target = new PacketQuery<Object>("123\"456\"789");

    /*
     * check
     */
    String result = (String) WhiteboxImpl.getInternalState(target, "queriesString");
    assertThat(result, is("123456789"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#parse()}
   * .
   */
  @Test
  public final void testParse() {
    /*
     * set
     */
    String queriesString = "attributes=\"key1=value1,key2=value2\"";
    PacketQuery<Object> target = new PacketQuery<Object>(queriesString);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertNotNull(attributes);
    assertSame(queries.size(), 0);
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#parse()}
   * .
   */
  @Test
  public final void testParseNull() {
    /*
     * set
     */
    PacketQuery<Object> target = new PacketQuery<Object>(null);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertNull(attributes);
    assertSame(queries.size(), 0);
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#getAttributeValue(java.lang.String)}
   * .
   */
  @Test
  public final void testGetAttributeValue() {
    /*
     * set
     */
    boolean result = target.parse();

    /*
     * test
     */
    assertThat(result, is(true));
    assertThat(target.getAttributeValue("key1"), is("value1"));
    assertThat(target.getAttributeValue("key2"), is("value2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#matchExactly(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testMatchExactly() {
    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key1", "value1");
    packetAttributes.put("key2", "value2");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    Packet packet =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    boolean parseResult = target.parse();

    /*
     * test
     */
    boolean matchExactlyResult = target.matchExactly(packet);

    /*
     * check
     */
    assertThat(parseResult, is(true));
    assertThat(matchExactlyResult, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#matchExactly(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testMatchExactlyNotContainsKey() {
    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key", "value1");
    packetAttributes.put("key2", "value2");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    Packet packet =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    boolean parseResult = target.parse();

    /*
     * test
     */
    boolean matchExactlyResult = target.matchExactly(packet);

    /*
     * check
     */
    assertThat(parseResult, is(true));
    assertThat(matchExactlyResult, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketQuery#matchExactly(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testMatchExactlyNotEquals() {
    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key1", "value1");
    packetAttributes.put("key2", "value");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    Packet packet =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    boolean parseResult = target.parse();

    /*
     * test
     */
    boolean matchExactlyResult = target.matchExactly(packet);

    /*
     * check
     */
    assertThat(parseResult, is(true));
    assertThat(matchExactlyResult, is(false));
  }

}
