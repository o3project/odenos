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

package org.o3project.odenos.core.component.network.topology;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Port.
 */
public class PortTest {

  private Port target;

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

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    target = Mockito.spy(new Port("123", "port_id123", "node_id123",
        "out_link123", "in_link123", attributes));

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
   * {@link org.o3project.odenos.core.component.network.topology.Port#Port()}.
   */
  @Test
  public void testPort() {

    Port result = new Port();

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#Port(java.lang.String)}
   * .
   */
  @Test
  public void testPortWithPortId() {

    Port result = new Port("port_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#Port(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testPortWithPortIdAndNodeId() {

    Port result = new Port("port_id123", "node_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertThat(result.getNode(), is("node_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#Port(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testPortWithVersion() {

    Port result = new Port("456", "port_id123", "node_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertThat(result.getNode(), is("node_id123"));
    assertThat(result.getVersion(), is("456"));
  }

  /**
   * Test method for {@link
   *  org.o3project.odenos.core.component.network.topology.Port#Port(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String,
   * java.lang.String, java.util.Map)}.
   */
  @Test
  public void testPortWithParameters() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port result = new Port("789", "port_id123", "node_id123",
        "out_link123", "in_link123", attributes);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertThat(result.getNode(), is("node_id123"));
    assertThat(result.getVersion(), is("789"));
    assertThat(result.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#Port(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testPortWithPort() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port param = new Port("987", "port_id123", "node_id123",
        "out_link123", "in_link123", attributes);
    Port result = new Port(param);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertThat(result.getNode(), is("node_id123"));
    assertThat(result.getVersion(), is("987"));
    assertThat(result.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#getType()}.
   */
  @Test
  public void testGetType() {
    assertThat(target.getType(), is("Port"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#getId()}.
   */
  @Test
  public void testGetId() {
    assertThat(target.getId(), is("port_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#setId(java.lang.String)}
   * .
   */
  @Test
  public void testSetId() {
    target.setId("abc");
    assertThat(target.getId(), is("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#getNode()}.
   */
  @Test
  public void testGetNode() {
    assertThat(target.getNode(), is("node_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#setNode(java.lang.String)}
   * .
   */
  @Test
  public void testSetNode() {
    target.setNode("def");
    assertThat(target.getNode(), is("def"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#getOutLink()}
   * .
   */
  @Test
  public void testGetOutLink() {
    assertThat(target.getOutLink(), is("out_link123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#setOutLink(java.lang.String)}
   * .
   */
  @Test
  public void testSetOutLink() {
    target.setOutLink("ghi");
    assertThat(target.getOutLink(), is("ghi"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#getInLink()}.
   */
  @Test
  public void testGetInLink() {
    assertThat(target.getInLink(), is("in_link123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#setInLink(java.lang.String)}
   * .
   */
  @Test
  public void testSetInLink() {
    target.setInLink("jkl");
    assertThat(target.getInLink(), is("jkl"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(6);
      pk.write("type");
      pk.write("Port");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Port"));
    assertThat(target.getVersion(), is("123"));
    assertThat(target.getId(), is("PORT_ID456"));
    assertThat(target.getNode(), is("NODE_ID456"));
    assertThat(target.getOutLink(), is("OUT_LINK456"));
    assertThat(target.getInLink(), is("IN_LINK456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Port"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is("PORT_ID456"));
    assertThat(target.getNode(), is("NODE_ID456"));
    assertThat(target.getOutLink(), is("OUT_LINK456"));
    assertThat(target.getInLink(), is("IN_LINK456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum0() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(0);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum8() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(8);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithEmptyPortId() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Port"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is(""));
    assertThat(target.getNode(), is("NODE_ID456"));
    assertThat(target.getOutLink(), is("OUT_LINK456"));
    assertThat(target.getInLink(), is("IN_LINK456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithEmptyOutLink() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("");
      pk.write("in_link");
      pk.write("IN_LINK456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Port"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is("PORT_ID456"));
    assertThat(target.getNode(), is("NODE_ID456"));
    assertThat(target.getOutLink(), is(""));
    assertThat(target.getInLink(), is("IN_LINK456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithEmptyInLink() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("type");
      pk.write("Port");
      pk.write("version");
      pk.write("456");
      pk.write("port_id");
      pk.write("PORT_ID456");
      pk.write("node_id");
      pk.write("NODE_ID456");
      pk.write("out_link");
      pk.write("OUT_LINK456");
      pk.write("in_link");
      pk.write("");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Port"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is("PORT_ID456"));
    assertThat(target.getNode(), is("NODE_ID456"));
    assertThat(target.getOutLink(), is("OUT_LINK456"));
    assertThat(target.getInLink(), is(""));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteTo() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    Port port = new Port();

    try {
      port.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("123"));
    assertThat(port.getId(), is("port_id123"));
    assertThat(port.getNode(), is("node_id123"));
    assertThat(port.getOutLink(), is("out_link123"));
    assertThat(port.getInLink(), is("in_link123"));
    assertThat(port.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithEmptyOutLink() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    target.setOutLink("");
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    Port port = new Port();

    try {
      port.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("123"));
    assertThat(port.getId(), is("port_id123"));
    assertThat(port.getNode(), is("node_id123"));
    assertThat(port.getOutLink(), is(""));
    assertThat(port.getInLink(), is("in_link123"));
    assertThat(port.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithEmptyInLink() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    target.setInLink("");
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    Port port = new Port();

    try {
      port.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("123"));
    assertThat(port.getId(), is("port_id123"));
    assertThat(port.getNode(), is("node_id123"));
    assertThat(port.getOutLink(), is("out_link123"));
    assertThat(port.getInLink(), is(""));
    assertThat(port.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att123", "val123");
    Port port =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123", attributes);
    Port port2 =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123",
            attributes2);

    assertTrue(port.equals(port2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithNull() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port port =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123", attributes);

    assertFalse(port.equals(null));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithSameInstance() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port port =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123", attributes);

    assertTrue(port.equals(port));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentClass() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port port =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123", attributes);

    assertFalse(port.equals("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithNullOutLink() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att123", "val123");
    Port port = new Port("123", "port_id123", "node_id123", null,
        "in_link123", attributes);
    Port port2 = new Port("123", "port_id123", "node_id123", null,
        "in_link123", attributes2);

    assertTrue(port.equals(port2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithNullInLink() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att123", "val123");
    Port port = new Port("123", "port_id123", "node_id123", "out_link123",
        null, attributes);
    Port port2 = new Port("123", "port_id123", "node_id123", "out_link123",
        null, attributes2);

    assertTrue(port.equals(port2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentValue() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att456", "val456");
    Port port =
        new Port("123", "port_id123", "node_id123", "out_link123",
            "in_link123", attributes);
    Port port2 =
        new Port("456", "port_id456", "node_id456", "out_link456",
            "in_link456",
            attributes2);

    assertFalse(port.equals(port2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#clone()}.
   */
  @Test
  public void testClone() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port param = new Port("987", "port_id123", "node_id123",
        "out_link123", "in_link123", attributes);
    Port result = param.clone();

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertThat(result.getNode(), is("node_id123"));
    assertThat(result.getVersion(), is("987"));
    assertThat(result.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Port target = new Port("123", "PortId", "NodeId", "OutLink", "InLink",
        attributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[version=123",
        "port_id=PortId",
        "node_id=NodeId",
        "out_link=OutLink",
        "in_link=InLink",
        "attributes={physical_id=PortId@NodeId, vendor=unknown, is_boundary=false, oper_status=UP, att123=val123}]"
    }, ",");

    System.out.println(result);
    System.out.println(expectedString);
    // TODO
    // Test disabled
    // JAVA8 is the order of the string becomes a change.
    //assertThat(result.endsWith(expectedString), is(true));

  }

}
