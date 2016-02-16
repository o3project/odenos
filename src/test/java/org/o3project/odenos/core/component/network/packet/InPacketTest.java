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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for InPacket.
 *
 */
public class InPacketTest {

  private InPacket target;

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
    target = new InPacket();
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
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#InPacket()}
   * .
   */
  @Test
  public void testInPacket() {

    /*
     * test
     */
    InPacket result = new InPacket();

    /*
     * check
     */
    assertThat(result.getNodeId(), is(nullValue()));
    assertThat(result.getPortId(), is(nullValue()));
    assertThat(result.getData(), is(nullValue()));
    assertThat(result.getHeader(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#InPacket(java.lang.String, java.lang.String, java.lang.String, byte[], java.util.Map)}
   * .
   */
  @Test
  public void testInPacketStringStringStringByteArrayMapOfStringString() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");

    /*
     * test
     */
    InPacket result = new InPacket("packet_id", "node_id", "port_id", data, packetAttributes);

    /*
     * check
     */

    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPortId(), is("port_id"));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#InPacket(java.lang.String, java.lang.String, java.lang.String, byte[], java.util.Map, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public void testInPacketStringStringStringByteArrayMapOfStringStringBasicFlowMatch() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    /*
     * test
     */

    InPacket result =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);

    /*
     * check
     */
    assertThat(result.packetId, is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPortId(), is("port_id"));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is("node_id"));
    assertThat(result.getHeader().getInPort(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#InPacket(java.lang.String, java.lang.String, java.lang.String, byte[], java.util.Map, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public void testInPacketStringStringStringByteArrayMapOfStringStringBasicFlowMatchHeaderNull() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");

    /*
     * test
     */

    InPacket result =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, null);

    /*
     * check
     */
    assertThat(result.packetId, is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPortId(), is("port_id"));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is(nullValue()));
    assertThat(result.getHeader().getInPort(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#InPacket(org.o3project.odenos.core.component.network.packet.InPacket)}
   * .
   */
  @Test
  public void testInPacketInPacket() {

    /*
     * set
     */
    InPacket packet = new InPacket();

    /*
     * test
     */
    InPacket result = new InPacket(packet);

    /*
     * check
     */
    assertThat(result.getNodeId(), is(nullValue()));
    assertThat(result.getPortId(), is(nullValue()));
    assertThat(result.getData(), is(nullValue()));
    assertThat(result.getHeader().getInNode(), is(nullValue()));
    assertThat(result.getHeader().getInPort(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#getType()}.
   */
  @Test
  public void testGetType() {

    /*
     * test
     */

    String result = target.getType();

    /*
     * check
     */
    assertThat(result, is("InPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#getHeader()}
   * .
   */
  @Test
  public void testGetHeader() {

    /*
     * test
     */

    BasicFlowMatch result = target.getHeader();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#setHeader(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public void testSetHeader() {

    /*
     * set
     */
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    /*
     * test
     */

    target.setHeader(header);

    /*
     * check
     */
    assertThat(target.getHeader().getInNode(), is("node_id"));
    assertThat(target.getHeader().getInPort(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#getNodeId()}
   * .
   */
  @Test
  public void testGetNodeId() {

    /*
     * test
     */

    String result = target.getNodeId();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#setNodeId(java.lang.String)}
   * .
   */
  @Test
  public void testSetNodeId() {

    /*
     * set
     */
    String value = "node_id";

    /*
     * test
     */

    target.setNodeId(value);

    /*
     * check
     */
    assertThat(target.getNodeId(), is("node_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#getPortId()}
   * .
   */
  @Test
  public void testGetPortId() {

    /*
     * test
     */

    String result = target.getNodeId();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#setPortId(java.lang.String)}
   * .
   */
  @Test
  public void testSetPortId() {

    /*
     * set
     */
    String value = "port_id";

    /*
     * test
     */

    target.setPortId(value);

    /*
     * x check
     */
    assertThat(target.getPortId(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#getData()}.
   */
  @Test
  public void testGetData() {

    /*
     * test
     */

    String result = target.getNodeId();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#setData(byte[])}
   * .
   */
  @Test
  public void testSetData() {

    /*
     * set
     */
    byte[] value = { 1 };

    /*
     * test
     */

    target.setData(value);

    /*
     * check
     */
    assertThat(target.getData(), is(value));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(target);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectNull() {

    /*
     * test
     */
    boolean result = target.equals(null);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectNotInPacket() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(header);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectNotEqualsNode() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    InPacket inpacket =
        new InPacket("packet_id", "node_id2", "port_id", data, packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(inpacket);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectNotEqualsPort() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    InPacket inpacket =
        new InPacket("packet_id", "node_id", "port_id2", data, packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(inpacket);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectNotEqualsData() {

    /*
     * set
     */
    byte[] data = { 1 };
    byte[] data2 = { 10 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    InPacket inpacket =
        new InPacket("packet_id", "node_id", "port_id2", data2, packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(inpacket);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue() {

    /*
     * set
     */
    Value value = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    MapValue mapToAttrMap = Mockito.mock(MapValue.class);
    MapValue attrMap = Mockito.mock(MapValue.class);

    doReturn(map).when(value).asMapValue();
    when(map.get(ValueFactory.createRawValue("attributes"))).thenReturn(mapToAttrMap);
    doReturn(attrMap).when(mapToAttrMap).asMapValue();

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    verify(value, times(2)).asMapValue();
    verify(mapToAttrMap, times(1)).asMapValue();
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSub() {
    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    InPacket target =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);
    Map<String, Value> map = new HashMap<String, Value>();

    /*
     * test
     */
    boolean result = target.writeValueSub(map);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacket#toString()}
   * .
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    target = new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[packetId=packet_id",
        "nodeId=node_id",
        "portId=port_id",
        "data={1}",
        "attributes={packet_id=123456}",
        "header=org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch"
    }, ",");

    assertThat(result.contains(expectedString), is(true));
  }

}
