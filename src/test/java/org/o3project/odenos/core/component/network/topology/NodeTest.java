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
import static org.junit.Assert.assertNull;
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
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Node.
 */
public class NodeTest {

  private Node target;

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

    Map<String, String> nodeAttributes = new HashMap<String, String>();
    Map<String, String> portAttributes = new HashMap<String, String>();
    Map<String, Port> ports = new HashMap<String, Port>();
    nodeAttributes.put("node_att123", "node_val123");
    portAttributes.put("port_att123", "port_val123");
    Port port =
        Mockito.spy(new Port("456", "port_id123", "node_id123",
            "out_link123",
            "in_link123", portAttributes));
    ports.put("port_id123", port);
    target = Mockito.spy(new Node("123", "node_id123", ports,
        nodeAttributes));

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
   * {@link org.o3project.odenos.core.component.network.topology.Node#Node()}.
   */
  @Test
  public void testNode() {

    Node result = new Node();

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#Node(java.lang.String)}
   * .
   */
  @Test
  public void testNodeWithNodeId() {

    Node result = new Node("node_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#Node(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testNodeWithVersion() {

    Node result = new Node("123", "node_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node_id123"));
    assertThat(result.getVersion(), is("123"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.core.component.network.topology.Node#Node(java.lang.String,
   * java.lang.String, java.util.Map, java.util.Map)}.
   */
  @Test
  public void testNodeWithPorts() {

    Map<String, String> nodeAttributes = new HashMap<String, String>();
    Map<String, String> portAttributes = new HashMap<String, String>();
    Map<String, Port> ports = new HashMap<String, Port>();
    nodeAttributes.put("node_att123", "node_val123");
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    ports.put("port_id123", port);
    Node result = new Node("123", "node_id123", ports, nodeAttributes);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node_id123"));
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getPortMap().get("port_id123").getType(), is("Port"));
    assertThat(result.getPortMap().get("port_id123").getVersion(),
        is("456"));
    assertThat(result.getPortMap().get("port_id123").getId(),
        is("port_id123"));
    assertThat(result.getPortMap().get("port_id123").getNode(),
        is("node_id123"));
    assertThat(result.getPortMap().get("port_id123").getOutLink(),
        is("out_link123"));
    assertThat(result.getPortMap().get("port_id123").getInLink(),
        is("in_link123"));
    assertThat(
        result.getPortMap().get("port_id123")
            .getAttribute("port_att123"),
        is("port_val123"));
    assertThat(result.getAttribute("node_att123"), is("node_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#Node(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testNodeWithNode() {

    Map<String, String> nodeAttributes = new HashMap<String, String>();
    Map<String, String> portAttributes = new HashMap<String, String>();
    Map<String, Port> ports = new HashMap<String, Port>();
    nodeAttributes.put("node_att123", "node_val123");
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    ports.put("port_id123", port);
    Node param = new Node("123", "node_id123", ports, nodeAttributes);
    Node result = new Node(param);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node_id123"));
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getPortMap().get("port_id123").getType(), is("Port"));
    assertThat(result.getPortMap().get("port_id123").getVersion(),
        is("456"));
    assertThat(result.getPortMap().get("port_id123").getId(),
        is("port_id123"));
    assertThat(result.getPortMap().get("port_id123").getNode(),
        is("node_id123"));
    assertThat(result.getPortMap().get("port_id123").getOutLink(),
        is("out_link123"));
    assertThat(result.getPortMap().get("port_id123").getInLink(),
        is("in_link123"));
    assertThat(
        result.getPortMap().get("port_id123")
            .getAttribute("port_att123"),
        is("port_val123"));
    assertThat(result.getAttribute("node_att123"), is("node_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getType()}.
   */
  @Test
  public void testGetType() {
    assertThat(target.getType(), is("Node"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getId()}.
   */
  @Test
  public void testGetId() {
    assertThat(target.getId(), is("node_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#setId(java.lang.String)}
   * .
   */
  @Test
  public void testSetId() {
    target.setId("abc");
    assertThat(target.getId(), is("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPortMap()}
   * .
   */
  @Test
  public void testGetPortMap() {
    Map<String, Port> ports = target.getPortMap();
    Port port = ports.get("port_id123");
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("456"));
    assertThat(port.getId(), is("port_id123"));
    assertThat(port.getNode(), is("node_id123"));
    assertThat(port.getOutLink(), is("out_link123"));
    assertThat(port.getInLink(), is("in_link123"));
    assertThat(port.getAttribute("port_att123"), is("port_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPortMap()}
   * .
   */
  @Test
  public void testGetPortMapWhenPortsIsNull() {
    Whitebox.setInternalState(target, "ports", (Map<String, Port>) null);
    Map<String, Port> ports = target.getPortMap();
    assertThat(ports, is(notNullValue()));
    assertThat(ports.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#clearPorts()}
   * .
   */
  @Test
  public void testClearPorts() {
    target.clearPorts();
    Map<String, Port> ports = target.getPortMap();
    assertThat(ports, is(notNullValue()));
    assertThat(ports.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#createPort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testCreatePort() {
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att456", "port_val456");
    Port port =
        new Port("123", "port_id456", "node_id456", "out_link456",
            "in_link456",
            portAttributes);
    Port result = target.createPort(port);

    assertThat(result, is(notNullValue()));
    assertTrue(result.getVersion().equals("1"));
    assertTrue(result.getId().equals("port_id456"));
    assertTrue(result.getNode().equals("node_id123"));
    assertThat(target.getPort(result.getId()), is(notNullValue()));
    assertThat(result.getAttribute("port_att456"), is("port_val456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPort(java.lang.String)}
   * .
   */
  @Test
  public void testGetPort() {
    Port result = target.getPort("port_id123");

    assertThat(result, is(notNullValue()));
    assertTrue(result.getId().equals("port_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPort(java.lang.String)}
   * .
   */
  @Test
  public void testGetPortWithNotExistingId() {
    Port result = target.getPort("port_id456");

    assertNull(result);
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.core.component.network.topology.Node#setPorts(java.util.Map)}.
   */
  @Test
  public void testSetPorts() {
    Map<String, Port> ports = new HashMap<String, Port>();
    Port port = new Port("port_id456");
    ports.put("port_id456", port);

    target.setPorts(ports);

    assertNull(target.getPort("port_id123"));
    assertThat(target.getPort("port_id456"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePortWithExistingPortId() {
    target.getPort("port_id123").setOutLink(null);
    target.getPort("port_id123").setInLink(null);
    Port result = target.deletePort("port_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertNull(target.getPort("port_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePortWithExistingPortIdHavingLinks() {
    Port result = target.deletePort("port_id123");

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePortWithNotExistingPortId() {
    Port result = target.deletePort("port_id456");

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePortWithNullId() {
    Port result = target.deletePort((String) null);

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithExistingPort() {
    Port port = new Port("port_id123");
    Port result = target.deletePort(port);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port_id123"));
    assertNull(target.getPort("port_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithExistingPortHavingOutLink() {
    Port port = new Port("port_id123");
    port.setOutLink("out_link123");
    Port result = target.deletePort(port);

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithExistingPortHavingInLink() {
    Port port = new Port("port_id123");
    port.setInLink("in_link123");
    Port result = target.deletePort(port);

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithNotExistingPort() {
    Port port = new Port("port_id456");
    Port result = target.deletePort(port);

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithNullPort() {
    Port result = target.deletePort((Port) null);

    assertNull(result);
    assertThat(target.getPort("port_id123"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPortMessages(org.o3project.odenos.core.component.network.topology.PortQuery)}
   * .
   */
  @Test
  public void testGetPortMessages() {
    PortQuery query = new PortQuery("attributes=port_att123=port_val123");
    query.parse();
    Map<String, Port> result = target.getPortMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get("port_id123"), is(notNullValue()));

    Port port = result.get("port_id123");

    assertThat(port.getAttribute("port_att123"), is("port_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getPortMessages(org.o3project.odenos.core.component.network.topology.PortQuery)}
   * .
   */
  @Test
  public void testGetPortMessagesWithBadQuery() {
    PortQuery query = new PortQuery("attributes=port_att123=port_val124");
    query.parse();
    Map<String, Port> result = target.getPortMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("NODE_ATT456", "NODE_VAL456");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("PORT_ATT456", "PORT_VAL456");
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PORT_ID456", new Port("246", "PORT_ID456", "NODE_ID456",
        "OUT_LINK456", "IN_LINK456", portAttributes));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Node");
      pk.write("node_id");
      pk.write("NODE_ID456");

      pk.write("ports");
      pk.write(ports);

      pk.write("attributes");
      pk.write(nodeAttributes);
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
    assertThat(target, is(notNullValue()));
    assertThat(target.getId(), is("NODE_ID456"));
    assertThat(target.getVersion(), is("123"));
    Port port = target.getPortMap().get("PORT_ID456");
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("246"));
    assertThat(port.getId(), is("PORT_ID456"));
    assertThat(port.getNode(), is("NODE_ID456"));
    assertThat(port.getOutLink(), is("OUT_LINK456"));
    assertThat(port.getInLink(), is("IN_LINK456"));
    assertThat(port.getAttribute("PORT_ATT456"), is("PORT_VAL456"));
    assertThat(target.getAttribute("NODE_ATT456"), is("NODE_VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Port#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("NODE_ATT456", "NODE_VAL456");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("PORT_ATT456", "PORT_VAL456");
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PORT_ID456", new Port("246", "PORT_ID456", "NODE_ID456",
        "OUT_LINK456", "IN_LINK456", portAttributes));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("type");
      pk.write("Node");
      pk.write("version");
      pk.write("789");
      pk.write("node_id");
      pk.write("NODE_ID456");

      pk.write("ports");
      pk.write(ports);

      pk.write("attributes");
      pk.write(nodeAttributes);
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
    assertThat(target, is(notNullValue()));
    assertThat(target.getId(), is("NODE_ID456"));
    assertThat(target.getVersion(), is("789"));
    Port port = target.getPortMap().get("PORT_ID456");
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("246"));
    assertThat(port.getId(), is("PORT_ID456"));
    assertThat(port.getNode(), is("NODE_ID456"));
    assertThat(port.getOutLink(), is("OUT_LINK456"));
    assertThat(port.getInLink(), is("IN_LINK456"));
    assertThat(port.getAttribute("PORT_ATT456"), is("PORT_VAL456"));
    assertThat(target.getAttribute("NODE_ATT456"), is("NODE_VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum0() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("NODE_ATT456", "NODE_VAL456");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("PORT_ATT456", "PORT_VAL456");
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PORT_ID456", new Port("246", "PORT_ID456", "NODE_ID456",
        "OUT_LINK456", "IN_LINK456", portAttributes));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(0);
      pk.write("type");
      pk.write("Node");
      pk.write("version");
      pk.write("789");
      pk.write("node_id");
      pk.write("NODE_ID456");

      pk.write("ports");
      pk.write(ports);

      pk.write("attributes");
      pk.write(nodeAttributes);
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
   * {@link org.o3project.odenos.core.component.network.topology.Node#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum6() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("NODE_ATT456", "NODE_VAL456");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("PORT_ATT456", "PORT_VAL456");
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("PORT_ID456", new Port("246", "PORT_ID456", "NODE_ID456",
        "OUT_LINK456", "IN_LINK456", portAttributes));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(6);
      pk.write("type");
      pk.write("Node");
      pk.write("version");
      pk.write("789");
      pk.write("node_id");
      pk.write("NODE_ID456");

      pk.write("ports");
      pk.write(ports);

      pk.write("attributes");
      pk.write(nodeAttributes);
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
   * {@link org.o3project.odenos.core.component.network.topology.Node#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithEmptyPorts() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("NODE_ATT456", "NODE_VAL456");
    Map<String, Port> ports = new HashMap<String, Port>();
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Node");
      pk.write("node_id");
      pk.write("NODE_ID456");

      pk.write("ports");
      pk.write(ports);

      pk.write("attributes");
      pk.write(nodeAttributes);
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
    assertThat(target, is(notNullValue()));
    assertThat(target.getId(), is("NODE_ID456"));
    assertThat(target.getVersion(), is("123"));
    Map<String, Port> portmap = target.getPortMap();
    assertThat(portmap.size(), is(0));
    assertThat(target.getAttribute("NODE_ATT456"), is("NODE_VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteTo() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = null;
    upk = msg.createUnpacker(in);
    Node node = new Node();

    try {
      node.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(node.getId(), is("node_id123"));
    assertThat(node.getVersion(), is("123"));
    assertThat(node.getPortMap().get("port_id123").getType(), is("Port"));
    assertThat(node.getPortMap().get("port_id123").getVersion(), is("456"));
    assertThat(node.getPortMap().get("port_id123").getId(),
        is("port_id123"));
    assertThat(node.getPortMap().get("port_id123").getNode(),
        is("node_id123"));
    assertThat(node.getPortMap().get("port_id123").getOutLink(),
        is("out_link123"));
    assertThat(node.getPortMap().get("port_id123").getInLink(),
        is("in_link123"));
    assertThat(
        node.getPortMap().get("port_id123").getAttribute("port_att123"),
        is("port_val123"));
    assertThat(node.getAttribute("node_att123"), is("node_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithoutVersion() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    Mockito.when(target.getVersion()).thenReturn(null);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in;
    in = new ByteArrayInputStream(bytes);
    Unpacker upk = null;
    upk = msg.createUnpacker(in);
    Node node = new Node();

    try {
      node.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(node.getId(), is("node_id123"));
    assertThat(node.getVersion(), is("0"));
    assertThat(node.getPortMap().get("port_id123").getType(), is("Port"));
    assertThat(node.getPortMap().get("port_id123").getVersion(), is("456"));
    assertThat(node.getPortMap().get("port_id123").getId(),
        is("port_id123"));
    assertThat(node.getPortMap().get("port_id123").getNode(),
        is("node_id123"));
    assertThat(node.getPortMap().get("port_id123").getOutLink(),
        is("out_link123"));
    assertThat(node.getPortMap().get("port_id123").getInLink(),
        is("in_link123"));
    assertThat(
        node.getPortMap().get("port_id123").getAttribute("port_att123"),
        is("port_val123"));
    assertThat(node.getAttribute("node_att123"), is("node_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("node_att123", "node_val123");
    Map<String, String> nodeAttributes2 = new HashMap<String, String>();
    nodeAttributes2.put("node_att123", "node_val123");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att123", "port_val123");
    Map<String, String> portAttributes2 = new HashMap<String, String>();
    portAttributes2.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    Port port2 =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes2);
    Map<String, Port> ports = new HashMap<String, Port>();
    Map<String, Port> ports2 = new HashMap<String, Port>();
    ports.put("port_id123", port);
    ports2.put("port_id123", port2);
    Node node = new Node("123", "node_id123", ports, nodeAttributes);
    Node node2 = new Node("123", "node_id123", ports2, nodeAttributes2);

    assertTrue(node.equals(node2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithNull() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("node_att123", "node_val123");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("port_id123", port);
    Node node = new Node("123", "node_id123", ports, nodeAttributes);

    assertFalse(node.equals(null));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithSameInstance() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("node_att123", "node_val123");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("port_id123", port);
    Node node = new Node("123", "node_id123", ports, nodeAttributes);

    assertTrue(node.equals(node));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentClass() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("node_att123", "node_val123");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    Map<String, Port> ports = new HashMap<String, Port>();
    ports.put("port_id123", port);
    Node node = new Node("123", "node_id123", ports, nodeAttributes);

    assertFalse(node.equals("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentValue() {
    Map<String, String> nodeAttributes = new HashMap<String, String>();
    nodeAttributes.put("node_att123", "node_val123");
    Map<String, String> nodeAttributes2 = new HashMap<String, String>();
    nodeAttributes2.put("node_att456", "node_val456");
    Map<String, String> portAttributes = new HashMap<String, String>();
    portAttributes.put("port_att123", "port_val123");
    Map<String, String> portAttributes2 = new HashMap<String, String>();
    portAttributes2.put("port_att456", "port_val456");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    Port port2 =
        new Port("246", "port_id456", "node_id456", "out_link456",
            "in_link456",
            portAttributes2);
    Map<String, Port> ports = new HashMap<String, Port>();
    Map<String, Port> ports2 = new HashMap<String, Port>();
    ports.put("port_id123", port);
    ports2.put("port_id456", port2);
    Node node = new Node("123", "node_id123", ports, nodeAttributes);
    Node node2 = new Node("789", "node_id456", ports2, nodeAttributes2);

    assertFalse(node.equals(node2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#clone()}.
   */
  @Test
  public void testClone() {

    Map<String, String> nodeAttributes = new HashMap<String, String>();
    Map<String, String> portAttributes = new HashMap<String, String>();
    Map<String, Port> ports = new HashMap<String, Port>();
    nodeAttributes.put("node_att123", "node_val123");
    portAttributes.put("port_att123", "port_val123");
    Port port =
        new Port("456", "port_id123", "node_id123", "out_link123",
            "in_link123",
            portAttributes);
    ports.put("port_id123", port);
    Node param = new Node("123", "node_id123", ports, nodeAttributes);
    Node result = param.clone();

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node_id123"));
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getPortMap().get("port_id123").getType(), is("Port"));
    assertThat(result.getPortMap().get("port_id123").getVersion(),
        is("456"));
    assertThat(result.getPortMap().get("port_id123").getId(),
        is("port_id123"));
    assertThat(result.getPortMap().get("port_id123").getNode(),
        is("node_id123"));
    assertThat(result.getPortMap().get("port_id123").getOutLink(),
        is("out_link123"));
    assertThat(result.getPortMap().get("port_id123").getInLink(),
        is("in_link123"));
    assertThat(
        result.getPortMap().get("port_id123")
            .getAttribute("port_att123"),
        is("port_val123"));
    assertThat(result.getAttribute("node_att123"), is("node_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#getUniquePortId()}.
   */
  @Test
  public void testGetUniquePortId() throws Exception {
    String result1 = Whitebox.invokeMethod(target, "getUniquePortId");
    String result2 = Whitebox.invokeMethod(target, "getUniquePortId");
    assertFalse(result1.equals(result2));
    assertNull(target.getPortMap().get(result1));
    assertNull(target.getPortMap().get(result2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#toString()}.
   */
  @Test
  public void testToString() throws Exception {

    /*
     * setting
     */
    Map<String, Port> ports = new HashMap<>();
    Map<String, String> portAttributes = new HashMap<>();
    portAttributes.put("att456", "val456");
    Port port = new Port("456", "PortId", "NodeId", "OutLink", "InLink",
        portAttributes);
    ports.put("PortId", port);

    Map<String, String> attributes = new HashMap<>();
    attributes.put("att123", "val123");
    Node target = new Node("123", "NodeId", ports, attributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.contains("[version=123,node_id=NodeId"), is(true));
    // TODO
    // Test disabled
    // JAVA8 is the order of the string becomes a change.
    //assertThat(result.contains("attributes={physical_id=NodeId, vendor=unknown, oper_status=UP, att123=val123}]"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Node#toString()}.
   */
  @Test
  public void testToString_NoPort() throws Exception {

    /*
     * setting
     */
    Map<String, Port> ports = new HashMap<>();

    Map<String, String> attributes = new HashMap<>();
    attributes.put("att123", "val123");
    Node target = new Node("123", "NodeId", ports, attributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[version=123",
        "node_id=NodeId",
        "ports={}",
        "attributes={physical_id=NodeId, vendor=unknown, oper_status=UP, att123=val123}]"
    }, ",");

    // TODO
    // Test disabled
    // JAVA8 is the order of the string becomes a change.
    //assertThat(result.endsWith(expectedString), is(true));

  }

}
