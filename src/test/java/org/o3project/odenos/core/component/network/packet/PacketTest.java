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
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for Packet.
 *
 * 
 *
 */
public class PacketTest {

  private Packet target;

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
    target = Mockito.spy(new Packet() {

      @Override
      public String getType() {
        return null;
      }
    });
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
   * {@link org.o3project.odenos.core.component.network.packet.Packet#Packet()}.
   */
  @Test
  public final void testPacket() {

    /*
     * test
     */
    Packet result = new Packet() {

      @Override
      public String getType() {
        return null;
      }
    };

    /*
     * check
     */
    assertThat(result.type, is(nullValue()));
    assertThat(result.getPacketId(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#Packet(java.lang.String, java.util.Map)}
   * .
   */
  @Test
  public final void testPacketStringMapOfStringString() {

    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key0", "value0");

    /*
     * test
     */
    Packet target = new Packet("packet_id", packetAttributes) {

      @Override
      public String getType() {
        return null;
      }
    };

    /*
     * check
     */
    Map<String, String> map = WhiteboxImpl.getInternalState(target, "attributes");
    assertThat(target.getPacketId(), is("packet_id"));
    assertThat(map.get("key0"), is("value0"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#Packet(java.lang.String, java.util.Map)}
   * .
   */
  @Test
  public final void testPacketStringMapOfStringStringAttributeNull() {

    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key0", "value0");

    /*
     * test
     */
    Packet target = new Packet("packet_id", null) {

      @Override
      public String getType() {
        return null;
      }
    };

    /*
     * check
     */
    Map<String, String> map = WhiteboxImpl.getInternalState(target, "attributes");
    assertThat(target.getPacketId(), is("packet_id"));
    assertThat(map.get("key0"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#Packet(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public final void testPacketPacket() {

    /*
     * set
     */
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("key0", "value0");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");

    Packet inPacket =
        new InPacket("packet_id", "node_id", "port_id", data, packetAttributes, header);

    /*
     * test
     */
    Packet target = new Packet(inPacket) {

      @Override
      public String getType() {
        return null;
      }
    };

    /*
     * check
     */
    assertThat(target.getPacketId(), is("packet_id"));
    assertThat(target.getType(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#getType()}.
   */
  @Test
  public final void testGetType() {

    /*
     * test
     */
    String result = target.getType();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#getPacketId()}
   * .
   */
  @Test
  public final void testGetPacketId() {

    /*
     * test
     */
    String result = target.getPacketId();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#setPacketId(java.lang.String)}
   * .
   */
  @Test
  public final void testSetPacketId() {

    /*
     * test
     */
    target.setPacketId("packet_id");

    /*
     * check
     */
    assertThat(target.getPacketId(), is("packet_id"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObject() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Packet target = new Packet("packet_id02", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

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
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Packet target = new Packet("packet_id02", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

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
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotPacket() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Packet target = new Packet("packet_id02", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

    /*
     * test
     */
    boolean result = target.equals("String");

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotequlseType() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Packet packet = new Packet("packet_id", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };
    Packet target = new Packet("packet_id02", packetAttributes) {

      @Override
      public String getType() {
        return "Packet02";
      }
    };

    /*
     * test
     */
    boolean result = packet.equals(target);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotequlsePacket() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Packet packet = new Packet("packet_id", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };
    Packet target = new Packet("packet_id02", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

    /*
     * test
     */
    boolean result = packet.equals(target);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotequlseAttributes() {
    /*
     * set
     */
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    Map<String, String> targetAttributes = new HashMap<String, String>();
    targetAttributes.put("packet_id2", "123456789");
    Packet packet = new Packet("packet_id", packetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

    Packet target = new Packet("packet_id", targetAttributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

    /*
     * test
     */
    boolean result = packet.equals(target);

    /*
     * check
     */
    assertNotSame(target.getAttributes(), packet.getAttributes());
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {

    /*
     * set
     */
    Value value = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    MapValue mapToAttrMap = Mockito.mock(MapValue.class);
    MapValue attrMap = Mockito.mock(MapValue.class);

    when(value.asMapValue()).thenReturn(map);
    when(map.get(ValueFactory.createRawValue("attributes"))).thenReturn(mapToAttrMap);
    when(mapToAttrMap.asMapValue()).thenReturn(attrMap);

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    verify(value, times(1)).asMapValue();
    verify(mapToAttrMap, times(1)).asMapValue();
    assertNotSame(map.get(ValueFactory.createRawValue("type")), is(nullValue()));
    assertNotSame(map.get(ValueFactory.createRawValue("packet_id")), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValueNull() {

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
    doReturn(null).when(map).get(ValueFactory.createRawValue("type"));
    doReturn(null).when(map).get(ValueFactory.createRawValue("packet_id"));

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    verify(value, times(1)).asMapValue();
    verify(mapToAttrMap, times(1)).asMapValue();
    assertThat(map.get(ValueFactory.createRawValue("type")), is(nullValue()));
    assertThat(map.get(ValueFactory.createRawValue("packet_id")), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.Packet#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {

    /*
     * set
     */
    List<String> portIds =
        new ArrayList<String>(Arrays.asList("Id1", "Id2", "Id3", "Id4", "Id5", "Id6"));
    byte[] data = { 1 };
    Map<String, String> packetAttributes = new HashMap<String, String>();
    packetAttributes.put("packet_id", "123456");
    BasicFlowMatch header = new BasicFlowMatch("node_id", "port_id");
    Packet target =
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
   * {@link org.o3project.odenos.core.component.network.packet.Packet#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<>();
    target = new Packet("PacketId", attributes) {

      @Override
      public String getType() {
        return "Packet";
      }
    };

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[packetId=PacketId,attributes={}]"), is(true));

  }

}
