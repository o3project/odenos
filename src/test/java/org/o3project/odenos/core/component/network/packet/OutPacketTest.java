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
import static org.junit.Assert.assertNotSame;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for OutPacket.
 *
 */
public class OutPacketTest {
  private OutPacket target;

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

    target = Mockito.spy(new OutPacket());

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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket()}
   * .
   */
  @Test
  public void testOutPacket() {

    /*
     * test
     */
    OutPacket result = new OutPacket();

    /*
     * check
     */
    assertThat(result.type, is(nullValue()));
    assertThat(result.getPacketId(), is(nullValue()));
    assertThat(result.getNodeId(), is(nullValue()));
    assertThat(result.getPorts().size(), is(0));
    assertThat(result.getExceptPorts().size(), is(0));
    assertThat(result.getData(), is(nullValue()));
    assertThat(result.getHeader(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(java.lang.String, java.lang.String, java.util.List, java.util.List, byte[], java.util.Map)}
   * .
   */
  @Test
  public void testOutPacketStrStrListOfStrListOfStrByteArrayMapOfStrStrExceptPortsNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");

    /*
     * test
     */
    OutPacket result =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(6));
    assertThat(result.getExceptPorts().size(), is(0));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.data, is(data));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(java.lang.String, java.lang.String, java.util.List, java.util.List, byte[], java.util.Map)}
   * .
   */
  @Test
  public void testOutPacketStrStrListOfStrListOfStrByteArrayMapOfStrStrPortsNull() {

    /*
     * set
     */
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");

    /*
     * test
     */
    OutPacket result =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(0));
    assertThat(result.getExceptPorts().size(), is(6));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(java.lang.String, java.lang.String, java.util.List, java.util.List, byte[], java.util.Map, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public void testOutPacketStringBasicFlowMatchMorePortsNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    /*
     * test
     */
    OutPacket result =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(6));
    assertThat(result.getExceptPorts().size(), is(0));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is("node_id"));
    assertThat(result.getHeader().getInPort(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(java.lang.String, java.lang.String, java.util.List, java.util.List, byte[], java.util.Map, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public void
      testOutPacketStringMoreNullExceptPorts() {

    /*
     * set
     */
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");

    /*
     * test
     */
    OutPacket result =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes, null);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(0));
    assertThat(result.getExceptPorts().size(), is(6));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is(nullValue()));
    assertThat(result.getHeader().getInPort(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(org.o3project.odenos.core.component.network.packet.OutPacket)}
   * .
   */
  @Test
  public void testOutPacketOutPacketExceptPortsNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket value =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);

    /*
     * test
     */

    OutPacket result = new OutPacket(value);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(6));
    assertThat(result.getExceptPorts().size(), is(0));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is("node_id"));
    assertThat(result.getHeader().getInPort(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#OutPacket(org.o3project.odenos.core.component.network.packet.OutPacket)}
   * .
   */
  @Test
  public void testOutPacketOutPacketPortsNull() {

    /*
     * set
     */
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket value =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes, header);

    /*
     * test
     */

    OutPacket result = new OutPacket(value);

    /*
     * check
     */
    assertThat(result.getPacketId(), is("packet_id"));
    assertThat(result.getNodeId(), is("node_id"));
    assertThat(result.getPorts().size(), is(0));
    assertThat(result.getExceptPorts().size(), is(6));
    assertThat(result.getAttribute("packet_id"), is("123456"));
    assertThat(result.getData(), is(data));
    assertThat(result.getHeader().getInNode(), is("node_id"));
    assertThat(result.getHeader().getInPort(), is("port_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getType()}
   * .
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
    assertThat(result, is("OutPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getHeader()}
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#setHeader(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getNodeId()}
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#setNodeId(java.lang.String)}
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getPorts()}
   * .
   */
  @Test
  public void testGetPorts() {

    /*
     * test
     */

    List<String> result = target.getPorts();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#setPorts(java.util.List)}
   * .
   */
  @Test
  public void testSetPorts() {
    /*
     * set
     */
    List<String> value =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));

    /*
     * test
     */

    target.setPorts(value);

    /*
     * check
     */
    assertThat(target.getPorts().size(), is(6));
    assertThat(target.getExceptPorts().size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getExceptPorts()}
   * .
   */
  @Test
  public void testGetExceptPorts() {

    /*
     * test
     */

    List<String> result = target.getExceptPorts();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#setExceptPorts(java.util.List)}
   * .
   */
  @Test
  public void testSetExceptPorts() {
    /*
     * set
     */
    List<String> value =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));

    /*
     * test
     */

    target.setExceptPorts(value);

    /*
     * check
     */
    assertThat(target.getPorts().size(), is(0));
    assertThat(target.getExceptPorts().size(), is(6));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#getData()}
   * .
   */
  @Test
  public void testGetData() {

    /*
     * test
     */

    byte[] result = target.getData();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#setData(byte[])}
   * .
   */
  @Test
  public void testSetData() {
    /*
     * set
     */
    byte[] value = { 1, 2 };

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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue() {

    /*
     * set
     */
    Value[] headerArray = new Value[2];
    headerArray[0] = ValueFactory.createRawValue("type");
    headerArray[1] = ValueFactory.createRawValue("BasicFlowMatch");

    Value value = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    MapValue mapToAttrMap = Mockito.mock(MapValue.class);
    MapValue attrMap = Mockito.mock(MapValue.class);

    doReturn(map).when(value).asMapValue();
    when(map.get(ValueFactory.createRawValue("attributes"))).thenReturn(mapToAttrMap);
    doReturn(attrMap).when(mapToAttrMap).asMapValue();
    when(map.get(ValueFactory.createRawValue("data"))).thenReturn(
        ValueFactory.createRawValue("data"));
    when(map.get(ValueFactory.createRawValue("header"))).thenReturn(
        ValueFactory.createMapValue(headerArray));

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    verify(value, times(2)).asMapValue();
    verify(mapToAttrMap, times(1)).asMapValue();
    assertNotSame(map.get(ValueFactory.createRawValue("type")), is(nullValue()));
    assertNotSame(map.get(ValueFactory.createRawValue("packet_id")), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValueNull() {
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
    when(map.get(ValueFactory.createRawValue("data"))).thenReturn(null);
    when(map.get(ValueFactory.createRawValue("header"))).thenReturn(null);

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    verify(value, times(2)).asMapValue();
    verify(mapToAttrMap, times(1)).asMapValue();
    assertThat(map.get(ValueFactory.createRawValue("type")), is(nullValue()));
    assertThat(map.get(ValueFactory.createRawValue("packet_id")), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSubExceptPortsNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSubPortsNull() {

    /*
     * set
     */
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes, header);
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaPortsNull() {

    /*
     * set
     */
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes, header);

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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaExceptPortsNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);

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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaObjectNull() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);

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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaNotOutPacket() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);

    /*
     * test
     */
    boolean result = target.equals(portIds);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaSuperFalte() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket outPacket =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);
    OutPacket value =
        new OutPacket("packet_id2", "node_id", portIds, null, data, packetAttributes,
            header);

    /*
     * test
     */
    boolean result = outPacket.equals(value);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaNotEqualsNodeId() {

    /*
     * set
     */

    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);
    OutPacket obj =
        new OutPacket("packet_id", "node_id2", portIds, null, data, packetAttributes,
            header);

    /*
     * test
     */
    boolean result = target.equals(obj);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaNotEqualsPorts() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);
    OutPacket value =
        new OutPacket("packet_id", "node_id", portExceptIds, null, data,
            packetAttributes, header);

    /*
     * test
     */
    boolean result = target.equals(value);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaNotEqualsExceptPorts() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    List<String> portExceptIds =
        new ArrayList<String>(Arrays.asList("ExId1", "ExId2", "ExId3", "IExd4", "ExId5",
            "ExId6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", null, portExceptIds, data,
            packetAttributes, header);
    OutPacket value =
        new OutPacket("packet_id", "node_id", null, portIds, data, packetAttributes,
            header);

    /*
     * test
     */
    boolean result = target.equals(value);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObjectaNotEqualsData() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    OutPacket target =
        new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes,
            header);
    OutPacket value =
        new OutPacket("packet_id", "node_id", portIds, null, null, packetAttributes,
            header);

    /*
     * test
     */
    boolean result = target.equals(value);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacket#toString()}
   * .
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    List<String> portIds = new ArrayList<String>();
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    target = new OutPacket("packet_id", "node_id", portIds, null, data, packetAttributes, header);

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
        "portIds=[]",
        "portExceptIds=[]",
        "data={1}",
        "attributes={}",
        "header=org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch"
    }, ",");

    assertThat(result.contains(expectedString), is(true));
  }

}
