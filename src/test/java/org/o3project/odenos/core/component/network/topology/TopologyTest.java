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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Topology.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Topology.class)
@PowerMockIgnore({"javax.management.*"})
public class TopologyTest {

  private Topology target;

  private Map<String, String> node1Attributes;
  private Map<String, String> node2Attributes;
  private Map<String, String> port1Attributes;
  private Map<String, String> port2Attributes;
  private Map<String, String> port3Attributes;
  private Map<String, String> port4Attributes;
  private Map<String, String> link1Attributes;
  private Map<String, String> link2Attributes;
  private Map<String, Port> ports1;
  private Map<String, Port> ports2;
  private Map<String, Node> nodes;
  private Map<String, Link> links;
  private Port port1;
  private Port port2;
  private Port port3;
  private Port port4;
  private Node node1;
  private Node node2;
  private Link link1;
  private Link link2;

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

    node1Attributes = new HashMap<String, String>();
    node2Attributes = new HashMap<String, String>();
    port1Attributes = new HashMap<String, String>();
    port2Attributes = new HashMap<String, String>();
    port3Attributes = new HashMap<String, String>();
    port4Attributes = new HashMap<String, String>();
    link1Attributes = new HashMap<String, String>();
    link2Attributes = new HashMap<String, String>();
    ports1 = new HashMap<String, Port>();
    ports2 = new HashMap<String, Port>();
    nodes = new HashMap<String, Node>();
    links = new HashMap<String, Link>();
    node1Attributes.put("node1_att123", "node1_val123");
    node2Attributes.put("node2_att123", "node2_val123");
    port1Attributes.put("port1_att123", "port1_val123");
    port2Attributes.put("port2_att123", "port2_val123");
    port3Attributes.put("port3_att123", "port3_val123");
    port4Attributes.put("port4_att123", "port4_val123");
    link1Attributes.put("link1_att123", "link1_val123");
    link2Attributes.put("link2_att123", "link2_val123");
    port1 =
        Mockito.spy(new Port("401", "port1_id123", "node1_id123",
            "link1_id123", "",
            port1Attributes));
    port2 =
        Mockito.spy(new Port("402", "port2_id123", "node1_id123", "",
            "link2_id123",
            port2Attributes));
    port3 =
        Mockito.spy(new Port("403", "port3_id123", "node2_id123", "",
            "link1_id123",
            port3Attributes));
    port4 =
        Mockito.spy(new Port("404", "port4_id123", "node2_id123",
            "link2_id123", "",
            port4Attributes));
    ports1.put("port1_id123", port1);
    ports1.put("port2_id123", port2);
    ports2.put("port3_id123", port3);
    ports2.put("port4_id123", port4);
    node1 = Mockito.spy(new Node("301", "node1_id123", ports1,
        node1Attributes));
    node2 = Mockito.spy(new Node("302", "node2_id123", ports2,
        node2Attributes));
    nodes.put("node1_id123", node1);
    nodes.put("node2_id123", node2);
    link1 =
        Mockito.spy(new Link("201", "link1_id123", "node1_id123",
            "port1_id123",
            "node2_id123", "port3_id123", link1Attributes));
    link2 =
        Mockito.spy(new Link("202", "link2_id123", "node2_id123",
            "port4_id123",
            "node1_id123", "port2_id123", link2Attributes));
    links.put("link1_id123", link1);
    links.put("link2_id123", link2);
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
   * {@link org.o3project.odenos.core.component.network.topology.Topology#Topology()}
   * .
   */
  @Test
  public void testTopology() {

    target = new Topology();

    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.component.network.topology.Topology#Topology(java.
   * util.Map<org.o3project.odenos.component.network.topology.Node>,
   * java.util.Map)}.
   */
  @Test
  public void testTopologyWithNodesAndLinks() {

    target = Mockito.spy(new Topology(nodes, links));

    assertThat(target, is(notNullValue()));
    assertThat(target.getVersion(), is("0"));
    assertThat(target.nodes, is(notNullValue()));
    assertThat(target.links, is(notNullValue()));
    assertThat(target.nodes.size(), is(2));
    assertThat(target.links.size(), is(2));
    assertThat(target.nodes.get("node1_id123"), is(notNullValue()));
    assertThat(target.nodes.get("node2_id123"), is(notNullValue()));
    assertThat(target.links.get("link1_id123"), is(notNullValue()));
    assertThat(target.links.get("link2_id123"), is(notNullValue()));
    assertThat(target.nodes.get("node1_id123").getId(), is("node1_id123"));
    assertThat(target.nodes.get("node2_id123").getId(), is("node2_id123"));
    assertThat(target.links.get("link1_id123").getId(), is("link1_id123"));
    assertThat(target.links.get("link2_id123").getId(), is("link2_id123"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.component.network.topology.Topology#Topology(java.
   * util.Map<java.lang.String,
   * org.o3project.odenos.component.network.topology.Node>,
   * java.util.Map)}.
   */
  @Test
  public void testTopologyWithParams() {

    target = Mockito.spy(new Topology("101", nodes, links));

    assertThat(target, is(notNullValue()));
    assertThat(target.getVersion(), is("101"));
    assertThat(target.nodes, is(notNullValue()));
    assertThat(target.links, is(notNullValue()));
    assertThat(target.nodes.size(), is(2));
    assertThat(target.links.size(), is(2));
    assertThat(target.nodes.get("node1_id123"), is(notNullValue()));
    assertThat(target.nodes.get("node2_id123"), is(notNullValue()));
    assertThat(target.links.get("link1_id123"), is(notNullValue()));
    assertThat(target.links.get("link2_id123"), is(notNullValue()));
    assertThat(target.nodes.get("node1_id123").getId(), is("node1_id123"));
    assertThat(target.nodes.get("node2_id123").getId(), is("node2_id123"));
    assertThat(target.links.get("link1_id123").getId(), is("link1_id123"));
    assertThat(target.links.get("link2_id123").getId(), is("link2_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#validate()}
   * .
   */
  @Test
  public void testValidate() {

    target = Mockito.spy(new Topology("101", nodes, links));

    assertTrue(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#validate()}
   * .
   */
  @Test
  public void testValidateWithNullSrcNode() {

    nodes.remove("node1_id123");
    target = Mockito.spy(new Topology("101", nodes, links));

    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#validate()}
   * .
   */
  @Test
  public void testValidateWithNullSrcPort() {

    Link link = links.get("link1_id123");
    Mockito.when(nodes.get(link.getSrcNode()).getPort(link.getSrcPort()))
        .thenReturn(null);
    target = Mockito.spy(new Topology("101", nodes, links));

    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#validate()}
   * .
   */
  @Test
  public void testValidateWithNullDstNode() {

    nodes.remove("node2_id123");
    target = Mockito.spy(new Topology("101", nodes, links));

    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#validate()}
   * .
   */
  @Test
  public void testValidateWithNullDstPort() {

    Link link = links.get("link1_id123");
    Mockito.when(nodes.get(link.getDstNode()).getPort(link.getDstPort()))
        .thenReturn(null);
    target = Mockito.spy(new Topology("101", nodes, links));

    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNodeMap()}
   * .
   */
  @Test
  public void testGetNodeMap() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Map<String, Node> map = target.getNodeMap();

    assertThat(map, is(notNullValue()));
    assertThat(map.size(), is(2));
    assertThat(map.get("node1_id123"), is(notNullValue()));
    assertThat(map.get("node1_id123").getId(), is("node1_id123"));
    assertThat(map.get("node2_id123"), is(notNullValue()));
    assertThat(map.get("node2_id123").getId(), is("node2_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNodeMap()}
   * .
   */
  @Test
  public void testGetNodeMapWhenNodesIsNull() {

    target = Mockito.spy(new Topology("101", null, links));
    Map<String, Node> map = target.getNodeMap();

    assertThat(map, is(notNullValue()));
    assertThat(map.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLinkMap()}
   * .
   */
  @Test
  public void testGetLinkMap() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Map<String, Link> map = target.getLinkMap();

    assertThat(map, is(notNullValue()));
    assertThat(map.size(), is(2));
    assertThat(map.get("link1_id123"), is(notNullValue()));
    assertThat(map.get("link1_id123").getId(), is("link1_id123"));
    assertThat(map.get("link2_id123"), is(notNullValue()));
    assertThat(map.get("link2_id123").getId(), is("link2_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLinkMap()}
   * .
   */
  @Test
  public void testGetLinkMapWhenLinksIsNull() {

    target = Mockito.spy(new Topology("101", nodes, null));
    Map<String, Link> map = target.getLinkMap();

    assertThat(map, is(notNullValue()));
    assertThat(map.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#createNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testCreateNode() {

    target = Mockito.spy(new Topology("101", null, null));
    target.createNode(node1);

    assertThat(target.getVersion(), is("102"));
    assertThat(target.getNodeMap().size(), is(1));
    for (Node value : target.getNodeMap().values()) {
      assertThat(value, is(notNullValue()));
      assertThat(value.getPort("port1_id123"), is(notNullValue()));
      assertThat(value.getAttribute("node1_att123"), is("node1_val123"));
    }
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#createNode(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testCreateLink() {

    target = Mockito.spy(new Topology("101", nodes, null));
    target.createLink(link1);

    assertThat(target.getVersion(), is("102"));
    assertThat(target.getLinkMap().size(), is(1));
    for (Link value : target.getLinkMap().values()) {
      assertThat(value, is(notNullValue()));
      assertThat(value.getSrcNode(), is("node1_id123"));
      assertThat(value.getSrcPort(), is("port1_id123"));
      assertThat(value.getDstNode(), is("node2_id123"));
      assertThat(value.getDstPort(), is("port3_id123"));
      assertThat(value.getAttribute("link1_att123"), is("link1_val123"));
      assertThat(value.getVersion(), is("1"));

      Port port;
      port = target.getNode(value.getSrcNode()).getPort(
          value.getSrcPort());
      assertThat(port.getVersion(), is("402"));
      port = target.getNode(value.getDstNode()).getPort(
          value.getDstPort());
      assertThat(port.getVersion(), is("404"));
    }
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testDeleteNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Node node = target.getNode("node1_id123");
    node.getPort("port1_id123").setOutLink(null);
    node.getPort("port1_id123").setInLink(null);
    node.getPort("port2_id123").setOutLink(null);
    node.getPort("port2_id123").setInLink(null);
    boolean result = target.deleteNode(node);

    assertTrue(result);
    assertThat(target.getNodeMap().size(), is(1));
    assertNull(target.getNode("node1_id123"));
    assertThat(target.getVersion(), is("102"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testDeleteNodeWithNull() {

    target = Mockito.spy(new Topology("101", nodes, links));
    boolean result = target.deleteNode(null);

    assertFalse(result);
    assertThat(target.getNodeMap().size(), is(2));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testDeleteNodeWithNodeWhichHasLinks() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Node node = target.getNode("node1_id123");
    boolean result = target.deleteNode(node);

    assertFalse(result);
    assertThat(target.getNodeMap().size(), is(2));
    assertThat(target.getNode("node1_id123"), is(notNullValue()));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   */
  @Test
  public void testDeleteNodeWithNotExistingNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Node node = new Node("node3_id123");
    boolean result = target.deleteNode(node);

    assertFalse(result);
    assertThat(target.getNodeMap().size(), is(2));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDeletePort() {

    target = Mockito.spy(new Topology("101", nodes, links));
    target.getPort("node1_id123", "port1_id123").setOutLink(null);
    target.getPort("node1_id123", "port1_id123").setInLink(null);
    boolean result = target.deletePort("node1_id123", "port1_id123");

    assertTrue(result);
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(1));
    assertThat(target.getNode("node1_id123").getPortMap(),
        is(notNullValue()));
    assertThat(target.getNode("node1_id123").getVersion(), is("302"));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDeletePortWithNotExistingPortId() {

    target = Mockito.spy(new Topology("101", nodes, links));
    target.getPort("node1_id123", "port1_id123").setOutLink(null);
    target.getPort("node1_id123", "port1_id123").setInLink(null);
    boolean result = target.deletePort("node1_id123", "port4_id123");

    assertFalse(result);
    assertThat(target.getNodeMap().size(), is(2));
    assertThat(target.getVersion(), is("101"));
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteLink(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteLink() {

    target = Mockito.spy(new Topology("101", nodes, links));
    boolean result = target.deleteLink("link1_id123");

    assertTrue(result);
    assertThat(target.getLinkMap().size(), is(1));
    assertNull(target.getLink("link1_id123"));
    assertThat(target.getVersion(), is("102"));
    assertNull(target.getNode("node1_id123").getPort("port1_id123")
        .getOutLink());
    assertNull(target.getNode("node2_id123").getPort("port3_id123")
        .getInLink());
    assertThat(target.getNode("node1_id123").getPort("port1_id123")
        .getVersion(), is("402"));
    assertThat(target.getNode("node2_id123").getPort("port3_id123")
        .getVersion(), is("404"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteLink(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testDeleteLinkWithLink() {

    target = Mockito.spy(new Topology("101", nodes, links));
    boolean result = target.deleteLink(target.getLink("link1_id123"));

    assertTrue(result);
    assertThat(target.getLinkMap().size(), is(1));
    assertNull(target.getLink("link1_id123"));
    assertThat(target.getVersion(), is("102"));
    assertNull(target.getNode("node1_id123").getPort("port1_id123")
        .getOutLink());
    assertNull(target.getNode("node2_id123").getPort("port3_id123")
        .getInLink());
    assertThat(target.getNode("node1_id123").getPort("port1_id123")
        .getVersion(), is("402"));
    assertThat(target.getNode("node2_id123").getPort("port3_id123")
        .getVersion(), is("404"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteLink(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testDeleteLinkWithNull() {

    target = Mockito.spy(new Topology("101", nodes, links));
    boolean result = target.deleteLink((Link) null);

    assertFalse(result);
    assertThat(target.getLinkMap().size(), is(2));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deleteLink(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testDeleteLinkWithNotExistingLink() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Link link = new Link("link3_id123");
    boolean result = target.deleteLink(link);

    assertFalse(result);
    assertThat(target.getLinkMap().size(), is(2));
    assertThat(target.getVersion(), is("101"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNode(java.lang.String)}
   * .
   */
  @Test
  public void testGetNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Node result = target.getNode("node1_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("node1_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNode(java.lang.String)}
   * .
   */
  @Test
  public void testGetNodeGettingNotExistingNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Node result = target.getNode("node3_id123");

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPort() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Port result = target.getPort("node1_id123", "port1_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("port1_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortGettingNotExistingNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Port result = target.getPort("node3_id123", "port1_id123");

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortGettingNotExistingPort() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Port result = target.getPort("node1_id123", "port3_id123");

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLink(java.lang.String)}
   * .
   */
  @Test
  public void testGetLink() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Link result = target.getLink("link1_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("link1_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLink(java.lang.String)}
   * .
   */
  @Test
  public void testGetLinkGettingNotExistingLink() {

    target = Mockito.spy(new Topology("101", nodes, links));
    Link result = target.getLink("link3_id123");

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNodeMesages(org.o3project.odenos.core.component.network.topology.NodeQuery)}
   * .
   */
  @Test
  public void testGetNodeMessages() {

    target = Mockito.spy(new Topology("101", nodes, links));
    NodeQuery query = new NodeQuery("attributes=node1_att123=node1_val123");
    query.parse();
    Map<String, Node> result = target.getNodeMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get("node1_id123"), is(notNullValue()));
    assertThat(result.get("node1_id123").getAttribute("node1_att123"),
        is("node1_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getNodeMesages(org.o3project.odenos.core.component.network.topology.NodeQuery)}
   * .
   */
  @Test
  public void testGetNodeMessagesWithBadQuery() {

    target = Mockito.spy(new Topology("101", nodes, links));
    NodeQuery query = new NodeQuery("attributes=node1_att123=node1_val124");
    query.parse();
    Map<String, Node> result = target.getNodeMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPortMesages(org.o3project.odenos.core.component.network.topology.PortQuery, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortMessages() {

    target = Mockito.spy(new Topology("101", nodes, links));
    PortQuery query = new PortQuery("attributes=port1_att123=port1_val123");
    query.parse();
    Map<String, Port> result = target.getPortMessages(query, "node1_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get("port1_id123"), is(notNullValue()));
    assertThat(result.get("port1_id123").getAttribute("port1_att123"),
        is("port1_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPortMesages(org.o3project.odenos.core.component.network.topology.PortQuery, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortMessagesWithBadQuery() {

    target = Mockito.spy(new Topology("101", nodes, links));
    PortQuery query = new PortQuery("attributes=port1_att123=port1_val124");
    query.parse();
    Map<String, Port> result = target.getPortMessages(query, "node1_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getPortMesages(org.o3project.odenos.core.component.network.topology.PortQuery, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortMessagesWithNotExistingNode() {

    target = Mockito.spy(new Topology("101", nodes, links));
    PortQuery query = new PortQuery("attributes=port1_att123=port1_val123");
    query.parse();
    Map<String, Port> result = target.getPortMessages(query, "node3_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLinkMesages(org.o3project.odenos.core.component.network.topology.LinkQuery)}
   * .
   */
  @Test
  public void testGetLinkMessages() {

    target = Mockito.spy(new Topology("101", nodes, links));
    LinkQuery query = new LinkQuery("attributes=link1_att123=link1_val123");
    query.parse();
    Map<String, Link> result = target.getLinkMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get("link1_id123"), is(notNullValue()));
    assertThat(result.get("link1_id123").getAttribute("link1_att123"),
        is("link1_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getLinkMesages(org.o3project.odenos.core.component.network.topology.LinkQuery)}
   * .
   */
  @Test
  public void testGetLinkMessagesWithBadQuery() {

    target = Mockito.spy(new Topology("101", nodes, links));
    LinkQuery query = new LinkQuery("attributes=link1_att123=link1_val124");
    query.parse();
    Map<String, Link> result = target.getLinkMessages(query);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  private void checkNodes(Map<String, Node> nodes) {

    assertThat(nodes, is(notNullValue()));
    assertThat(nodes.size(), is(2));

    Node node = nodes.get("node1_id123");
    assertThat(node, is(notNullValue()));
    assertThat(node.getType(), is("Node"));
    assertThat(node.getVersion(), is("301"));
    assertThat(node.getId(), is("node1_id123"));
    assertThat(node.getAttribute("node1_att123"), is("node1_val123"));

    Map<String, Port> ports = node.getPortMap();
    assertThat(ports, is(notNullValue()));
    assertThat(ports.size(), is(2));

    Port port = node.getPort("port1_id123");
    assertThat(port, is(notNullValue()));
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("401"));
    assertThat(port.getId(), is("port1_id123"));
    assertThat(port.getNode(), is("node1_id123"));
    assertThat(port.getOutLink(), is("link1_id123"));
    assertThat(port.getInLink(), is(""));
    assertThat(port.getAttribute("port1_att123"), is("port1_val123"));

    port = node.getPort("port2_id123");
    assertThat(port, is(notNullValue()));
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("402"));
    assertThat(port.getNode(), is("node1_id123"));
    assertThat(port.getOutLink(), is(""));
    assertThat(port.getInLink(), is("link2_id123"));
    assertThat(port.getAttribute("port2_att123"), is("port2_val123"));

    node = nodes.get("node2_id123");
    assertThat(node, is(notNullValue()));
    assertThat(node.getType(), is("Node"));
    assertThat(node.getVersion(), is("302"));
    assertThat(port.getId(), is("port2_id123"));
    assertThat(node.getId(), is("node2_id123"));
    assertThat(node.getAttribute("node2_att123"), is("node2_val123"));

    ports = node.getPortMap();
    assertThat(ports, is(notNullValue()));
    assertThat(ports.size(), is(2));

    port = node.getPort("port3_id123");
    assertThat(port, is(notNullValue()));
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("403"));
    assertThat(port.getId(), is("port3_id123"));
    assertThat(port.getNode(), is("node2_id123"));
    assertThat(port.getOutLink(), is(""));
    assertThat(port.getInLink(), is("link1_id123"));
    assertThat(port.getAttribute("port3_att123"), is("port3_val123"));

    port = node.getPort("port4_id123");
    assertThat(port, is(notNullValue()));
    assertThat(port.getType(), is("Port"));
    assertThat(port.getVersion(), is("404"));
    assertThat(port.getId(), is("port4_id123"));
    assertThat(port.getNode(), is("node2_id123"));
    assertThat(port.getOutLink(), is("link2_id123"));
    assertThat(port.getInLink(), is(""));
    assertThat(port.getAttribute("port4_att123"), is("port4_val123"));
  }

  private void checkLinks(Map<String, Link> links) {
    Link link;

    assertThat(links, is(notNullValue()));
    assertThat(links.size(), is(2));

    link = links.get("link1_id123");
    assertThat(link, is(notNullValue()));
    assertThat(link.getType(), is("Link"));
    assertThat(link.getVersion(), is("201"));
    assertThat(link.getId(), is("link1_id123"));
    assertThat(link.getSrcNode(), is("node1_id123"));
    assertThat(link.getSrcPort(), is("port1_id123"));
    assertThat(link.getDstNode(), is("node2_id123"));
    assertThat(link.getDstPort(), is("port3_id123"));
    assertThat(link.getAttribute("link1_att123"), is("link1_val123"));

    link = links.get("link2_id123");
    assertThat(link, is(notNullValue()));
    assertThat(link.getType(), is("Link"));
    assertThat(link.getVersion(), is("202"));
    assertThat(link.getId(), is("link2_id123"));
    assertThat(link.getSrcNode(), is("node2_id123"));
    assertThat(link.getSrcPort(), is("port4_id123"));
    assertThat(link.getDstNode(), is("node1_id123"));
    assertThat(link.getDstPort(), is("port2_id123"));
    assertThat(link.getAttribute("link2_att123"), is("link2_val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    target = new Topology("101", nodes, links);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("type");
      pk.write("Topology");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.type, is("Topology"));
    assertThat(target.getVersion(), is("0"));

    checkNodes(target.getNodeMap());
    checkLinks(target.getLinkMap());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    target = new Topology("101", nodes, links);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Topology");
      pk.write("version");
      pk.write("101");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.type, is("Topology"));
    assertThat(target.getVersion(), is("101"));

    checkNodes(target.getNodeMap());
    checkLinks(target.getLinkMap());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum2() {
    target = new Topology("101", nodes, links);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(2);
      pk.write("type");
      pk.write("Topology");
      pk.write("version");
      pk.write("101");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
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
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum5() {
    target = new Topology("101", nodes, links);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("type");
      pk.write("Topology");
      pk.write("version");
      pk.write("101");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
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
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithEmptyNodes() {
    target = new Topology("101", null, links);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Topology");
      pk.write("version");
      pk.write("101");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.type, is("Topology"));
    assertThat(target.getVersion(), is("101"));

    assertThat(target.getNodeMap().size(), is(0));
    checkLinks(target.getLinkMap());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithEmptyLinks() {
    target = new Topology("101", nodes, null);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Topology");
      pk.write("version");
      pk.write("101");
      pk.write("nodes");
      pk.write(target.getNodeMap());
      pk.write("links");
      pk.write(target.getLinkMap());
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new Topology("0", null, null));
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.type, is("Topology"));
    assertThat(target.getVersion(), is("101"));

    checkNodes(target.getNodeMap());
    assertThat(target.getLinkMap().size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteTo() {
    target = new Topology("101", nodes, links);
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
    Unpacker upk = msg.createUnpacker(in);

    target = Mockito.spy(new Topology("0", null, null));
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.type, is("Topology"));
    assertThat(target.getVersion(), is("101"));

    checkNodes(target.getNodeMap());
    checkLinks(target.getLinkMap());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getUniqueNodeId()}
   * .
   */
  @Test
  public void testGetUniqueNodeId() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    String result1 = Whitebox.invokeMethod(target, "getUniqueNodeId");
    String result2 = Whitebox.invokeMethod(target, "getUniqueNodeId");
    assertFalse(result1.equals(result2));
    assertNull(target.getNodeMap().get(result1));
    assertNull(target.getNodeMap().get(result2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#getUniqueLinkId()}
   * .
   */
  @Test
  public void testGetUniqueLinkId() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    String result1 = Whitebox.invokeMethod(target, "getUniqueLinkId");
    String result2 = Whitebox.invokeMethod(target, "getUniqueLinkId");
    assertFalse(result1.equals(result2));
    assertNull(target.getLinkMap().get(result1));
    assertNull(target.getLinkMap().get(result2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithNull() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    boolean result = Whitebox.invokeMethod(target, "deletePort",
        (Port) null);
    assertFalse(result);
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(2));
    assertThat(target.getNode("node2_id123").getPortMap().size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithPortInNotExistingNode() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    Port port = new Port("port1_id123", "node3_id123");
    boolean result = Whitebox.invokeMethod(target, "deletePort", port);
    assertFalse(result);
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(2));
    assertThat(target.getNode("node2_id123").getPortMap().size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithNotExistingPort() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    Port port = new Port("port5_id123", "node1_id123");
    boolean result = Whitebox.invokeMethod(target, "deletePort", port);
    assertFalse(result);
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(2));
    assertThat(target.getNode("node2_id123").getPortMap().size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#deletePort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   */
  @Test
  public void testDeletePortWithPort() throws Exception {
    target = Mockito.spy(new Topology("101", nodes, links));
    Port port = new Port("port1_id123", "node1_id123");
    boolean result = Whitebox.invokeMethod(target, "deletePort", port);
    assertTrue(result);
    assertThat(target.getNode("node1_id123").getPortMap().size(), is(1));
    assertNull(target.getNode("node1_id123").getPort("port1_id123"));
    assertThat(target.getNode("node2_id123").getPortMap().size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Topology#toString()}
   * .
   */
  @Test
  public void testToString() throws Exception {

    /*
     * setting
     */
    nodes = new HashMap<String, Node>();
    links = new HashMap<String, Link>();
    target = new Topology("101", nodes, links);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(
        result.endsWith("[type=Topology,version=101,nodes={},links={}]"),
        is(true));
  }

}
