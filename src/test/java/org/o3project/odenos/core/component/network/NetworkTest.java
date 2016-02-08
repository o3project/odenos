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

package org.o3project.odenos.core.component.network;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowChanged;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.InPacketQueue;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacketQueue;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.packet.PacketQueue;
import org.o3project.odenos.core.component.network.packet.PacketQueueSet;
import org.o3project.odenos.core.component.network.packet.PacketStatus;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.LinkChanged;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.NodeChanged;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.PortChanged;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.core.component.network.topology.TopologyChanged;
import org.o3project.odenos.remoteobject.ObjectSettings;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for Network.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Network.class, RequestParser.class })
@PowerMockIgnore({"javax.management.*"})
public class NetworkTest {

  private Network target;

  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);

    target = Mockito.spy(new Network("ojectId", dispatcher));

  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    dispatcher = null;

  }

  private Network createPowerSpy() {

    Network network = PowerMockito.spy(new Network("ojectId", dispatcher));
    target = network;

    return network;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getSettings()}.
   */
  @Test
  public void testGetSettings() {

    ObjectSettings result = target.getSettings();

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#onRequest(org.o3project.odenos.remoteobject.message.Request)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testOnRequest() throws Exception {

    /*
     * setting
     */
    /* set listener for onRequest */
    Whitebox.invokeMethod(target, "createParser");

    Request request = new Request("ObjectId", Request.Method.GET,
        "settings/verbose_event/port", "txid", 
        ValueFactory.createRawValue("body"));

    /*
     * test
     */
    Response result = target.onRequest(request);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testOnRequestWithNullPath() {

    Request request = Mockito.spy(new Request("objectId",
        Request.Method.GET, "", "txid", null));

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#onRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testOnRequestWithBadBody() {

    Request request =
        Mockito.spy(new Request("objectId", Request.Method.GET, "/",
            "txid", new Object()));

    Response result = target.onRequest(request);

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getSuperType()}.
   */
  @Test
  public void testGetSuperType() {

    String result = target.getSuperType();

    assertThat(result, is("Network"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getDescription()}.
   */
  @Test
  public void testGetDescription() {

    String result = target.getDescription();

    assertThat(result, is("Network Component"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#Network(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public void testNetwork() {

    /*
     * test
     */
    target = new Network("ObjectId", dispatcher);

    /*
     * check
     */
    assertThat(target.getSuperType(), is("Network"));
    assertThat(target.getDescription(), is("Network Component"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getSettingVerbosePort()}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetSettingVerbosePort() throws Exception {

    /*
     * setting
     */
    target.putSettingVerbosePort("true");

    /*
     * test
     */
    Response result = target.getSettingVerbosePort();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(notNullValue()));
    assertThat(result.getBody(String.class), is("true"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putSettingVerbosePort(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSettingVerbosePort() throws Exception {

    /*
     * test
     */
    Response result = target.putSettingVerbosePort("true");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(notNullValue()));
    assertThat(result.getBody(String.class), is("true"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putSettingVerbosePort(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSettingVerbosePortWithNull() throws Exception {

    /*
     * test
     */
    Response result = target.putSettingVerbosePort(null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getSettingVerboseLink()}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetSettingVerboseLink() throws Exception {

    /*
     * setting
     */
    target.putSettingVerboseLink("true");

    /*
     * test
     */
    Response result = target.getSettingVerboseLink();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(notNullValue()));
    assertThat(result.getBody(String.class), is("true"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putSettingVerboseLink(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSettingVerboseLink() throws Exception {

    /*
     * test
     */
    Response result = target.putSettingVerboseLink("true");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(notNullValue()));
    assertThat(result.getBody(String.class), is("true"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putSettingVerboseLink(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutSettingVerboseLinkWithNull() throws Exception {

    /*
     * test
     */
    Response result = target.putSettingVerboseLink(null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBodyValue(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getTopology()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetTopology() throws Exception {

    /*
     * test
     */
    Response result = target.getTopology();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(Topology.class), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putTopology(Topology)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutTopologyWithDefaultTopology() throws Exception {

    Topology topology = new Topology();

    /*
     * test
     */
    Response result = target.putTopology(topology);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(Topology.class), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putTopology(Topology)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutTopologyWithTopologyNodeLink() throws Exception {

    Map<String, Node> nodes = new HashMap<String, Node>();
    Map<String, Link> links = new HashMap<String, Link>();
    Topology topology = new Topology(nodes, links);

    /*
     * test
     */
    Response result = target.putTopology(topology);

    /*
     * check
     */
    Topology resultToporogy = result.getBody(Topology.class);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultToporogy, is(notNullValue()));
    assertThat(resultToporogy.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putTopology(Topology)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutTopologyWithNullLink() throws Exception {

    Topology topology = Mockito.spy(new Topology("0", null, null));

    /*
     * test
     */
    Response result = target.putTopology(topology);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Topology resultTopology = result.getBody(Topology.class);
    assertThat(resultTopology.getVersion(), is("0"));
    assertThat(resultTopology.getNodeMap().size(), is(0));
    assertThat(resultTopology.getLinkMap().size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postNode(Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostNode() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    PowerMockito.doReturn(new Response(Response.OK, new Object())).when(
        target,
        "notifyNodeChanged", eq(null), anyObject(),
        eq(NodeChanged.Action.add));

    /*
     * test
     */
    Node node = new Node();
    Response result = target.postNode(node);

    /*
     * check
     */
    Node resultNode = result.getBody(Node.class);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultNode.getVersion(), is("1"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postNode(Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostNodeAfterPost() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    PowerMockito.doReturn(new Response(Response.OK, new Object())).when(
        target,
        "notifyNodeChanged", eq(null), anyObject(),
        eq(NodeChanged.Action.add));

    Node settingNode = new Node();
    Response settingResult = target.postNode(settingNode);
    assertThat(settingResult.statusCode, is(Response.OK));

    Node node = settingResult.getBody(Node.class);
    assertThat(node.getVersion(), is("1"));

    /*
     * test
     */
    Response result = target.postNode(node);

    /*
     * check
     */
    Node resultNode = result.getBody(Node.class);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultNode.getVersion(), is("1"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postNode(Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostNodeWithNull() throws Exception {

    /*
     * test
     */
    Response result = target.postNode(null);

    /*
     * check
     */
    // FIXME check status code when Node is null.
    //assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Node.class), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNodes(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNodesWithQueryTrue() throws Exception {

    /*
     * setting
     */
    Node node = new Node();
    Response settingResponse = target.putNode("NodeId", node);
    assertThat(settingResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Response result = target.getNodes(true, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Node> resultNodes = result.getBodyAsMap(Node.class);
    assertThat(resultNodes.size(), is(1));
    assertThat(resultNodes.containsKey("NodeId"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNodes(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNodesWithQueryFalse() throws Exception {

    /*
     * setting
     */
    Node node = new Node();
    Response settingResponse = target.putNode("NodeId", node);
    assertThat(settingResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Response result = target.getNodes(false, null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Node> resultNodes = result.getBodyAsMap(Node.class);
    assertThat(resultNodes.size(), is(1));
    assertThat(resultNodes.containsKey("NodeId"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNodes(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNodesWithQueryFalse_NoNode() throws Exception {

    /*
     * test
     */
    Response result = target.getNodes(false, null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Node> resultNodes = result.getBodyAsMap(Node.class);
    assertThat(resultNodes.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNode(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("NodeId");
    Response settingResponse = target.postNode(node);
    assertThat(settingResponse.statusCode, is(Response.OK));

    Node settingNode = settingResponse.getBody(Node.class);
    String nodeId = settingNode.getId();

    /*
     * test
     */
    Response result = target.getNode(nodeId);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    assertThat(result.getBody(Node.class), is(settingNode));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNode(java.lang.String)}
   * .
   */
  @Test
  public void testGetNodeNotRegisterNodeId() {

    /*
     * test
     */
    Response result = target.getNode("nodeId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodeCreate() throws Exception {

    /*
     * setting
     */
    createPowerSpy();
    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    /*
     * test
     */
    Node node = new Node();
    Response result = target.putNode("NodeId", node);

    /*
     * check
     */
    Node resultNode = result.getBody(Node.class);

    assertThat(result.statusCode, is(Response.CREATED));
    assertThat(resultNode.getId(), is("NodeId"));
    assertThat(resultNode.getVersion(), is("1"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodeUpdate() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();

    Node firstNode = new Node("1", "NodeId");
    firstNode.putAttribute("Key", "Value");
    nodes.put("NodeId", firstNode);
    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    /*
     * test
     */
    Node targetNode = new Node("1", "NodeId");
    targetNode.putAttribute("Key", "NewValue");
    Response result = target.putNode("NodeId", targetNode);

    /*
     * check
     */
    Node resultNode = result.getBody(Node.class);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultNode.getVersion(), is("2"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodeUpdate_SameNode() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();

    Node firstNode = new Node("1", "NodeId");
    nodes.put("NodeId", firstNode);
    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    /*
     * test
     */
    Node targetNode = new Node("1", "NodeId");
    Response result = target.putNode("NodeId", targetNode);

    /*
     * check
     */
    Node resultNode = result.getBody(Node.class);

    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultNode, is(targetNode));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodeWithNullId() throws Exception {

    /*
     * setting
     */
    createPowerSpy();
    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    /*
     * test
     */
    Node node = new Node();
    Response result = target.putNode(null, node);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBody(Node.class), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodeWithNullNode() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new Network("ojectId", dispatcher));
    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    /*
     * test
     */
    Response result = target.putNode("NodeId", null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.getBody(Node.class), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteNode() throws Exception {

    /*
     * setting
     */
    createPowerSpy();
    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    Node settingNode = new Node();
    Response settingResult = target.putNode("NodeId", settingNode);
    assertThat(settingResult.statusCode, is(Response.CREATED));
    assertThat(settingResult.getBody(Node.class).getVersion(), is("1"));

    /*
     * test
     */
    Node node = new Node("1", "NodeId");
    Response result = target.deleteNode("NodeId", node);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Response checkResult = target.getNode("NodeId");
    assertThat(checkResult.statusCode, is(Response.NOT_FOUND));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteNodeWithInvalidNode() throws Exception {

    /*
     * setting
     */
    createPowerSpy();
    PowerMockito.doReturn(null).when(target, "notifyNodeChanged", eq(null),
        anyObject(),
        eq(NodeChanged.Action.update));

    Node settingNode = new Node();
    Response settingResult = target.putNode("NodeId", settingNode);
    assertThat(settingResult.statusCode, is(Response.CREATED));
    assertThat(settingResult.getBody(Node.class).getVersion(), is("1"));

    /*
     * test
     */
    Node node = new Node("999", "NodeId");
    Response result = target.deleteNode("NodeId", node);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CONFLICT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteNode(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteNodeNothing() throws Exception {

    Node node = new Node();
    Response result = target.deleteNode("NodeId", node);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postPort(java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostPort() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node("NodeId");
    Response settingResponse = target.postNode(settingNode);

    Node node = settingResponse.getBody(Node.class);
    String nodeId = node.getId();

    /*
     * test
     */
    Port port = new Port();
    Response result = target.postPort(nodeId, port);

    /*
     * check
     */
    Port resultPort = result.getBody(Port.class);

    // assertThat(result.statusCode, is(Response.CREATED));
    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultPort.getNode(), is(nodeId));
    assertThat(resultPort.getVersion(), is("1"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPorts(boolean, java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPorts() throws Exception {

    /*
     * setting
     */
    Map<String, Port> ports = new HashMap<>();
    ports.put("Port1", new Port("PortId1"));
    ports.put("Port2", new Port("PortId2"));
    ports.put("Port3", new Port("PortId3"));
    Map<String, String> attributes = new HashMap<>();

    Node node = new Node("1", "NodeId", ports, attributes);
    Map<String, Node> nodes = new HashMap<>();
    nodes.put("NodeId", node);

    Map<String, Link> links = new HashMap<>();

    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    Response result = target.getPorts(false, null, "NodeId", null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Port> resultMap = result.getBodyAsMap(Port.class);
    assertThat(resultMap.size(), is(3));
    assertThat(resultMap.containsKey("Port1"), is(true));
    assertThat(resultMap.containsKey("Port2"), is(true));
    assertThat(resultMap.containsKey("Port3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPort(java.lang.String, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPort() throws Exception {

    /*
     * setting
     */
    Node node = new Node();
    Response settingNodeResponse = target.putNode("NodeId", node);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));
    ;

    Port port = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId", port);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Response result = target.getPort("NodeId", "PortId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Port resultPort = result.getBody(Port.class);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPort(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortWithNothingPort() {

    Response result = target.getPort("NodeId", "PortId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putPort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutPort() throws Exception {

    /*
     * setting
     */
    Node node = new Node();
    Response settingNodeResponse = target.putNode("NodeId", node);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port();
    Response result = target.putPort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CREATED));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putPort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutPortAfterPut() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    Port settingPort = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId",
        settingPort);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port("1", "PortId", "NodeId");
    port.setInLink("LinkIn");
    port.setOutLink("LinkOut");
    Response result = target.putPort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
    // In/Out Link is expected to be ignored on PUT Port
    Port expectedPort = new Port("1", "PortId", "NodeId");
    expectedPort.setInLink(null);
    expectedPort.setOutLink(null);
    expectedPort.updateVersion();
    assertThat(result.getBody(Port.class), is(expectedPort));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putPort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutPortAfterPortInvalid() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    Port settingPort = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId",
        settingPort);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port("999", "NodeId", "PortId");
    Response result = target.putPort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CONFLICT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePort() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    Port settingPort = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId",
        settingPort);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port("1", "PortId", "NodeId");
    Response result = target.deletePort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePortWithInvalidPort() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    Port settingPort = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId",
        settingPort);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port("999", "PortId", "NodeId");
    Response result = target.deletePort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CONFLICT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePort_NothingNode() throws Exception {

    /*
     * test
     */
    Port port = new Port();
    Response result = target.deletePort("NodeId", "PortId", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePort_NothingPort() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Port port = new Port();
    Response result = target.deletePort("NodeId", "PortId", port);

    /*
     * check
     */
    // assertThat(result.statusCode,is(Response.NOT_FOUND));
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePort(java.lang.String, java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePortWithPortNull() throws Exception {

    /*
     * setting
     */
    Node settingNode = new Node();
    Response settingNodeResponse = target.putNode("NodeId", settingNode);
    assertThat(settingNodeResponse.statusCode, is(Response.CREATED));

    Port settingPort = new Port();
    Response settingPortResponse = target.putPort("NodeId", "PortId",
        settingPort);
    assertThat(settingPortResponse.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Response result = target.deletePort("NodeId", "PortId", null);

    /*
     * check
     */
    // assertThat(result.statusCode,is(Response.NOT_FOUND));
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getNodePhysicalId(java.lang.String)}
   * .
   */
  @Test
  public void testGetNodePhysicalIdWithNullId() {

    /*
     * test
     */
    Response result = target.getNodePhysicalId(null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNodePhysicalId(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodePhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Port> ports = new HashMap<String, Port>();
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("physical_id", "PhysicalId");

    Node firstNode = new Node("1", "NodeId", ports, attributes);
    target.putNode("NodeId", firstNode);

    Node node = new Node("1", "NodeId", ports, attributes);

    /*
     * test
     */
    Response result = target.putNodePhysicalId("PhysicalId", node);

    /*
     * check
     */
    verify(target, atLeastOnce()).putNode(eq("NodeId"), (Node) anyObject());
    Node resultNode = result.getBody(Node.class);

    // same message
    assertThat(result.statusCode, is(Response.OK));
    assertThat(resultNode.getVersion(), is("1"));
    // assertThat(resultNode.getId(), is("NodeId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putNodePhysicalId(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutNodePhysicalIdWithInvalidPhysicalId() throws Exception {

    /*
     * test
     */
    Node node = new Node();
    Response result = target.putNodePhysicalId("PhysicalId", node);

    /*
     * check
     */
    verify(target, times(1)).putNode(eq((String) null), (Node) anyObject());

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteNodePhysicalId(java.lang.String, Node)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteNodePhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");
    Node node1 = new Node("1", "NodeId1", new HashMap<String, Port>(),
        attributes1);
    nodes.put("NodeId1", node1);
    Map<String, String> attributes2 = new HashMap<>();
    attributes2.put("physical_id", "PhysicalId2");
    Node node2 = new Node("1", "NodeId2", new HashMap<String, Port>(),
        attributes2);
    nodes.put("NodeId2", node2);
    Map<String, String> attributes3 = new HashMap<>();
    attributes3.put("physical_id", "PhysicalId3");
    Node node3 = new Node("1", "NodeId3", new HashMap<String, Port>(),
        attributes3);
    nodes.put("NodeId3", node3);

    Map<String, Link> links = new HashMap<>();
    Topology settingTopology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", settingTopology);

    /*
     * test
     */
    Response result = target.deleteNodePhysicalId("PhysicalId2", node2);

    /*
     * check
     */
    verify(target, atLeastOnce()).deleteNode(eq("NodeId2"),
        (Node) anyObject());

    assertThat(result.statusCode, is(Response.OK));
    // FIXME is body null?
    // Node resultNode = result.getBody(Node.class);
    // assertThat(resultNode.getId(), is("NodeId2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPortPhysicalId(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPortPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");
    Port port1 = new Port("1", "PortId1", "NodeId1", "OutLink1", "InLink1",
        attributes1);
    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("PortId1", port1);
    Node node1 = new Node("1", "NodeId1", ports1,
        new HashMap<String, String>());
    nodes.put("NodeId1", node1);

    Map<String, String> attributes2 = new HashMap<>();
    attributes2.put("physical_id", "PhysicalId2");
    Port port2 = new Port("1", "PortId2", "NodeId2", "OutLink2", "InLink2",
        attributes2);
    Map<String, Port> ports2 = new HashMap<>();
    ports2.put("PortId2", port2);
    Node node2 = new Node("1", "NodeId2", ports2,
        new HashMap<String, String>());
    nodes.put("NodeId2", node2);

    Map<String, String> attributes3 = new HashMap<>();
    attributes3.put("physical_id", "PhysicalId3");
    Port port3 = new Port("1", "PortId2", "NodeId2", "OutLink2", "InLink2",
        attributes2);
    Map<String, Port> ports3 = new HashMap<>();
    ports3.put("PortId3", port3);
    Node node3 = new Node("1", "NodeId3", ports2,
        new HashMap<String, String>());
    nodes.put("NodeId3", node3);

    Map<String, Link> links = new HashMap<>();
    Topology settingTopology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", settingTopology);

    /*
     * test
     */
    Response result = target.getPortPhysicalId("PhysicalId2");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Port resultPort = result.getBody(Port.class);
    assertThat(resultPort.getId(), is("PortId2"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPortPhysicalId(java.lang.String)}
   * .
   */
  @Test
  public void testGetPortPhysicalIdWithNotingPort() {

    /*
     * test
     */
    Response result = target.getPortPhysicalId("PhysicalId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putPortPhysicalId(java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutPortPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("PortId1", new Port("1", "PortId1", "NodeId1", "OutLink1",
        "InLink1", attributes1));

    Node node1 = new Node("1", "NodeId1", ports1,
        new HashMap<String, String>());
    nodes.put("NodeId1", node1);

    Map<String, Link> links = new HashMap<>();
    Topology settingTopology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", settingTopology);

    /*
     * test
     */
    Map<String, String> attributes = new HashMap<>();
    attributes.put("physical_id", "PhysicalId11");
    Port port = new Port("1", "PortId1", "NodeId1", "OutLink1", "InLink1",
        attributes);

    Response result = target.putPortPhysicalId("PhysicalId1", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    // FIXME is body null?
    // Port resultPort = result.getBody(Port.class);
    // assertThat(resultPort.getId(), is("PortId1"));
    // assertThat(resultPort.getAttribute("physical_id"), is("PhysicalId11"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putPortPhysicalId(java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutPortWithInvalidPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("PortId1", new Port("1", "PortId1", "NodeId1", "OutLink1",
        "InLink1", attributes1));

    Node node1 = new Node("1", "NodeId1", ports1,
        new HashMap<String, String>());
    nodes.put("NodeId1", node1);

    Map<String, Link> links = new HashMap<>();
    Topology settingTopology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", settingTopology);

    /*
     * test
     */
    Map<String, String> attributes = new HashMap<>();
    attributes.put("physical_id", "PhysicalId11");
    Port port = new Port("1", "PortId2", "NodeId1", "OutLink1", "InLink1",
        attributes);

    Response result = target.putPortPhysicalId("PhysicalId11", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CREATED));

    // FIXME is body null?
    // Port resultPort = result.getBody(Port.class);
    // assertThat(resultPort.getId(), is("PortId1"));
    // assertThat(resultPort.getAttribute("physical_id"), is("PhysicalId11"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deletePortPhysicalId(java.lang.String, Port)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePortPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("PortId1", new Port("1", "PortId1", "NodeId1", null, null,
        attributes1));

    Node node1 = new Node("1", "NodeId1", ports1,
        new HashMap<String, String>());
    nodes.put("NodeId1", node1);

    Map<String, Link> links = new HashMap<>();
    Topology settingTopology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", settingTopology);

    /*
     * test
     */
    Map<String, String> attributes = new HashMap<>();
    attributes.put("physical_id", "PhysicalId1");
    Port port = new Port("1", "PortId1", "NodeId1", "OutLink1", "InLink1",
        attributes);

    Response result = target.deletePortPhysicalId("PhysicalId1", port);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    // FIXME is body null?
    // Port resultPort = result.getBody(Port.class);
    // assertThat(resultPort.getId(), is("PortId1"));
    // assertThat(resultPort.getAttribute("physical_id"), is("PhysicalId11"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postLink(Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostLink() throws Exception {

    /*
     * setting
     */
    createNodePortForLink();

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    Response result = target.postLink(link);

    /*
     * check
     */
    // assertThat(result.statusCode, is(Response.CREATED));
    assertThat(result.statusCode, is(Response.OK));

    Link resultLink = result.getBody(Link.class);
    assertThat(resultLink.getId(), is(notNullValue()));
    assertThat(resultLink.getVersion(), is("1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getLinks(boolean, java.lang.String)}
   * .
   */
  @Test
  public void testGetLinks() {

    /*
     * setting
     */
    Map<String, Link> links = new HashMap<>();

    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("attr", "attr1");
    Link link1 = new Link("1", "LinkId1", "SrcNode1", "SrcPort1",
        "DstNode1", "DstPort1", attributes1);
    links.put("Link1", link1);

    Map<String, String> attributes2 = new HashMap<>();
    attributes2.put("attr", "attr2");
    Link link2 = new Link("1", "LinkId2", "SrcNode2", "SrcPort2",
        "DstNode2", "DstPort2", attributes2);
    links.put("Link2", link2);

    Map<String, String> attributes3 = new HashMap<>();
    attributes3.put("attr", "attr3");
    Link link3 = new Link("1", "LinkId3", "SrcNode3", "SrcPort3",
        "DstNode3", "DstPort3", attributes3);
    links.put("Link3", link3);

    Map<String, Node> nodes = new HashMap<>();
    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    Response result = target.getLinks(true, "attributes=\"attr=attr2\"");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Link> resultLinks = result.getBodyAsMap(Link.class);
    assertThat(resultLinks.size(), is(1));
    assertThat(resultLinks.containsKey("LinkId2"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getLinks(boolean, java.lang.String)}
   * .
   */
  @Test
  public void testGetLinks_NoQuery() {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();
    links.put("Link1", new Link("Link1"));
    links.put("Link2", new Link("Link2"));
    links.put("Link3", new Link("Link3"));

    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    Response result = target.getLinks(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, Link> resultLinks = result.getBodyAsMap(Link.class);
    assertThat(resultLinks.size(), is(3));
    assertThat(resultLinks.containsKey("Link1"), is(true));
    assertThat(resultLinks.containsKey("Link2"), is(true));
    assertThat(resultLinks.containsKey("Link3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getLink(java.lang.String)}
   * .
   */
  @Test
  public void testGetLink_NothingLinkId() {

    /*
     * test
     */
    Response result = target.getLink("LinkId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putLink(Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutLink() throws Exception {

    /*
     * setting
     */
    createNodePortForLink();

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    Response result = target.putLink(link.getId(), link);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.CREATED));

    Link resultLink = result.getBody(Link.class);
    assertThat(resultLink.getId(), is("LinkId"));
    assertThat(resultLink.getVersion(), is("1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putLink(Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutLinkAfterPut() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, Port> srcPorts = new HashMap<>();
    srcPorts.put("SrcPortId", new Port("SrcPortId"));
    Node settingSrcNode = new Node("1", "SrcNodeId", srcPorts,
        new HashMap<String, String>());
    nodes.put("SrcNodeId", settingSrcNode);
    Map<String, Port> dstPorts = new HashMap<>();
    dstPorts.put("DstPortId", new Port("DstPortId"));
    Node settingDstNode = new Node("1", "DstNodeId", dstPorts,
        new HashMap<String, String>());
    nodes.put("DstNodeId", settingDstNode);

    Map<String, Link> links = new HashMap<>();

    Link settingLink = new Link("LinkId", "SrcNodeId", "SrcPortId",
        "DstNodeId", "DstPortId");
    settingLink.setVersion("1");
    settingLink.putAttribute("Key", "Value");
    links.put("LinkId", settingLink);

    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    link.setVersion("1");
    link.putAttribute("Key", "NewValue");
    Response result = target.putLink(link.getId(), link);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Link resultLink = result.getBody(Link.class);
    assertThat(resultLink.getId(), is("LinkId"));
    assertThat(resultLink.getVersion(), is("2"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putLink(Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutLinkAfterPut_SameLink() throws Exception {

    /*
     * setting
     */
    createNodePortForLink();

    Link settingLink = new Link("LinkId", "SrcNodeId", "SrcPortId",
        "DstNodeId", "DstPortId");
    Response settingResult = target.putLink(settingLink.getId(),
        settingLink);
    assertThat(settingResult.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    link.setVersion("1");
    Response result = target.putLink(link.getId(), link);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteLink(java.lang.String, Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteLink() throws Exception {

    /*
     * setting
     */
    createNodePortForLink();

    Link settingLink = new Link("LinkId", "SrcNodeId", "SrcPortId",
        "DstNodeId", "DstPortId");
    Response settingResult = target.putLink(settingLink.getId(),
        settingLink);
    assertThat(settingResult.statusCode, is(Response.CREATED));

    /*
     * test
     */
    Link link = new Link("LinkId", "SrcNodeId", "SrcPortId", "DstNodeId",
        "DstPortId");
    link.setVersion("1");
    Response result = target.deleteLink("LinkId", link);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteLink(java.lang.String, Link)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteLink_NothingLink() throws Exception {

    /*
     * test
     */
    Link link = new Link();
    Response result = target.deleteLink("LinkId", link);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostFlow() throws Exception {

    /*
     * test
     */
    Flow flow = new BasicFlow("FlowId", "Owner", true, "Priority");
    flow.setStatus("none");
    Response result = target.postFlow(flow);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Flow resultFlow = result.getBody(Flow.class);
    assertThat(resultFlow.getFlowId(), is(notNullValue()));
    assertThat(resultFlow.getVersion(), is("1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostFlow_DefaultConstructorFlow() throws Exception {

    /*
     * test
     */
    Flow flow = new Flow();
    Response result = target.postFlow(flow);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getFlows(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetFlows() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    flows.put("FlowId1", new BasicFlow("FlowId1"));
    flows.put("FlowId2", new BasicFlow("FlowId2"));
    flows.put("FlowId3", new OFPFlow("FlowId3"));

    FlowSet flowSet = new FlowSet("1", priority, flows);

    Whitebox.setInternalState(target, "flowset", flowSet);

    /*
     * test
     */
    Response result = target.getFlows(true, "type=BasicFlow");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    FlowSet resultFlowSet = result.getBody(FlowSet.class);
    Map<String, Flow> resultFlows = resultFlowSet.getFlows();

    assertThat(resultFlows.size(), is(2));

    assertThat(resultFlows.containsKey("FlowId1"), is(true));
    assertThat(resultFlows.containsKey("FlowId2"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getFlows(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetFlows_NoQuery() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> priority = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    flows.put("Flow1", new Flow("FlowId1"));
    flows.put("Flow2", new Flow("FlowId2"));
    flows.put("Flow3", new Flow("FlowId3"));

    FlowSet flowSet = new FlowSet("1", priority, flows);

    Whitebox.setInternalState(target, "flowset", flowSet);

    /*
     * test
     */
    Response result = target.getFlows(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    FlowSet resultFlowSet = result.getBody(FlowSet.class);
    Map<String, Flow> resultFlows = resultFlowSet.getFlows();

    assertThat(resultFlows.size(), is(3));

    assertThat(resultFlows.containsKey("Flow1"), is(true));
    assertThat(resultFlows.containsKey("Flow2"), is(true));
    assertThat(resultFlows.containsKey("Flow3"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getFlow(java.lang.String)}
   * .
   */
  @Test
  public void testGetFlow() {

    /*
     * test
     */
    Response result = target.getFlow("FlowId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#putFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPutFlow() throws Exception {

    /*
     * test
     */
    Flow flow = new Flow();
    Response result = target.putFlow("flowId", flow);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteFlow(java.lang.String, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteFlow() throws Exception {

    /*
     * test
     */
    Flow flow = new Flow();
    Response result = target.deleteFlow("FlowId", flow);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPackets()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPackets() throws Exception {

    /*
     * setting
     */
    Packet settingInPacket = new InPacket();
    Response settingInResponse = target.postInPacket(settingInPacket);
    assertThat(settingInResponse.statusCode, is(Response.OK));
    Packet settingOutpacket = new OutPacket();
    Response settingOutResponse = target.postOutPacket(settingOutpacket);
    assertThat(settingOutResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getPackets();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus resultPacket = result.getBody(PacketStatus.class);
    assertThat(resultPacket.getInStatus().getPacketCount(), is(1L));
    assertThat(resultPacket.getOutStatus().getPacketCount(), is(1L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getPackets()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPackets_NoPacket() throws Exception {

    /*
     * test
     */
    Response result = target.getPackets();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus resultPacket = result.getBody(PacketStatus.class);
    assertThat(resultPacket.getInStatus().getPacketCount(), is(0L));
    assertThat(resultPacket.getOutStatus().getPacketCount(), is(0L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postInPacket(org.o3project.odenos.core.component.network.packet.PacketObject.PacketMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostInPacket() throws Exception {

    /*
     * test
     */
    Packet packet = new InPacket();
    Response result = target.postInPacket(packet);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postInPacket(org.o3project.odenos.core.component.network.packet.PacketObject.PacketMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostInPacket_ArgumentPacket() throws Exception {

    /*
     * test
     */
    Packet packet = new Packet() {

      @Override
      public String getType() {
        return null;
      }
    };
    Response result = target.postInPacket(packet);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postInPacket(org.o3project.odenos.core.component.network.packet.PacketObject.PacketMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostInPacket_ArgumentOutPacket() throws Exception {

    /*
     * test
     */
    Packet packet = new OutPacket();
    Response result = target.postInPacket(packet);

    /*
     * check
     */
    // FIXME check status code.
    //        assertThat(result.statusCode, is(Response.BAD_REQUEST));
    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Packet.class), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getInPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetInPacketBooleanString_NoQuery() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket();
    Response settingResponse = target.postInPacket(packet);
    assertThat(settingResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getInPacket(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus packetStatus = result.getBody(PacketStatus.class);
    assertThat(packetStatus.getInStatus().getPacketCount(), is(1L));
    assertThat(packetStatus.getOutStatus().getPacketCount(), is(0L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getInPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetInPacketBooleanString_NoQuery_NoPacket()
      throws Exception {

    /*
     * test
     */
    Response result = target.getInPacket(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus packetStatus = result.getBody(PacketStatus.class);
    assertThat(packetStatus.getInStatus().getPacketCount(), is(0L));
    assertThat(packetStatus.getOutStatus().getPacketCount(), is(0L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getInPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetInPacketBooleanString_HasQuery() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket();
    Response settingResponse = target.postInPacket(packet);
    assertThat(settingResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getInPacket(true, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    // assertThat(result.getBody(Object.class), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteInPackets()}.
   */
  @Test
  public void testDeleteInPackets() {

    InPacketQueue inPacketQueue = Mockito.spy(new InPacketQueue());
    PacketQueueSet queueSet = Mockito.spy(new PacketQueueSet());
    Whitebox.setInternalState(queueSet, "inQueue", inPacketQueue);
    Whitebox.setInternalState(target, "packetQueue", queueSet);

    /*
     * test
     */
    Response result = target.deleteInPackets();

    /*
     * check
     */
    verify(inPacketQueue, times(1)).clearPackets();

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getHeadInPacket()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadInPacket() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket();
    Response settingResult = target.postInPacket(packet);
    assertThat(settingResult.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getHeadInPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getHeadInPacket()}.
   */
  @Test
  public void testGetHeadInPacket_NoContent() {

    /*
     * test
     */
    Response result = target.getHeadInPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteHeadInPacket()}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteHeadInPacket() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket();
    Response settingResult = target.postInPacket(packet);
    assertThat(settingResult.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.deleteHeadInPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteHeadInPacket()}
   * .
   */
  @Test
  public void testDeleteHeadInPacket_NoContent() {

    /*
     * test
     */
    Response result = target.deleteHeadInPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getInPacket(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetInPacketString() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket("InPacketId", "NodeId", "PortId", null,
        null);

    PacketQueueSet packetQueueSet = Mockito.spy(new PacketQueueSet());
    InPacketQueue inPacketQueue = Mockito.mock(InPacketQueue.class);
    doReturn(inPacketQueue).when(packetQueueSet).getInQueue();
    doReturn(packet).when(inPacketQueue).getPacket("InPacketId");

    Whitebox.setInternalState(target, "packetQueue", packetQueueSet);

    /*
     * test
     */
    Response result = target.getInPacket("InPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Packet resultPacket = result.getBody(Packet.class);
    assertThat(resultPacket.getPacketId(), is("InPacketId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getInPacket(java.lang.String)}
   * .
   */
  @Test
  public void testGetInPacketString_NothingInPacketId() {

    /*
     * test
     */
    Response result = target.getInPacket("InPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteInPacket(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteInPacket() throws Exception {

    /*
     * setting
     */
    Packet packet = new InPacket();
    Response settingResult = target.postInPacket(packet);
    assertThat(settingResult.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.deleteInPacket("InPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteInPacket(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteInPacket_NoContent() {

    /*
     * test
     */
    Response result = target.deleteInPacket("InPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postOutPacket(org.o3project.odenos.core.component.network.packet.PacketObject.PacketMessage)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostOutPacket() throws Exception {

    /*
     * test
     */
    Packet packet = new OutPacket();

    /*
     * test
     */
    Response result = target.postOutPacket(packet);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getOutPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetOutPacketBooleanString_NoQuery() throws Exception {

    /*
     * setting
     */
    Packet packet = new OutPacket();
    Response settingResponse = target.postOutPacket(packet);
    assertThat(settingResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getOutPacket(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus resultPacketStatus = result.getBody(PacketStatus.class);
    assertThat(resultPacketStatus.getInStatus().getPacketCount(), is(0L));
    assertThat(resultPacketStatus.getOutStatus().getPacketCount(), is(1L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getOutPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetOutPacketBooleanString_NoQuery_NoPacket()
      throws Exception {

    /*
     * test
     */
    Response result = target.getOutPacket(false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus resultPacketStatus = result.getBody(PacketStatus.class);
    assertThat(resultPacketStatus.getInStatus().getPacketCount(), is(0L));
    assertThat(resultPacketStatus.getOutStatus().getPacketCount(), is(0L));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getOutPacket(boolean, java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetOutPacketBooleanString_HasQuery() throws Exception {

    /*
     * setting
     */
    Packet packet = new OutPacket();
    Response settingResponse = target.postOutPacket(packet);
    assertThat(settingResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getOutPacket(true, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    // assertThat(result.getBody(Object.class), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteOutPackets()}
   * .
   */
  @Test
  public void testDeleteOutPackets() {

    /*
     * test
     */
    Response result = target.deleteOutPackets();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getHeadOutPacket()}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadOutPacket() throws Exception {

    /*
     * setting
     */
    Packet settingOutPacket = new OutPacket();
    Response settingResponse = target.postOutPacket(settingOutPacket);
    assertThat(settingResponse.statusCode, is(Response.OK));

    /*
     * test
     */
    Response result = target.getHeadOutPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Packet resultPacket = result.getBody(Packet.class);
    assertThat(resultPacket.isBodyNull(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteHeadOutPacket()}
   * .
   */
  @Test
  public void testDeleteHeadOutPacket() {

    /*
     * test
     */
    Response result = target.deleteHeadOutPacket();

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getOutPacket(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetOutPacketString() throws Exception {

    /*
     * setting
     */
    Packet settingOutPacket = new OutPacket("OutPacketId", "NodeId", null,
        null, null, null);

    PacketQueueSet packetQueueSet = Mockito.spy(new PacketQueueSet());
    OutPacketQueue outPacketQueue = Mockito.mock(OutPacketQueue.class);
    doReturn(outPacketQueue).when(packetQueueSet).getOutQueue();
    doReturn(settingOutPacket).when(outPacketQueue)
        .getPacket("OutPacketId");

    Whitebox.setInternalState(target, "packetQueue", packetQueueSet);

    /*
     * test
     */
    Response result = target.getOutPacket("OutPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Packet resultPacket = result.getBody(Packet.class);
    assertThat(resultPacket.isBodyNull(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#getOutPacket(java.lang.String)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetOutPacketStringWithNothingPacket() throws Exception {

    /*
     * test
     */
    Response result = target.getOutPacket("OutPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#deleteOutPacket(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteOutPacketWithNothingPacket() {

    /*
     * test
     */
    Response result = target.deleteOutPacket("OutPacketId");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postEvent(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostEvent() throws Exception {

    /*
     * test
     */
    Response result = target.postEvent("EventType", new Object());

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postEvent(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostEventWithNullEvent() throws Exception {

    /*
     * test
     */
    Response result = target.postEvent(null, new Object());

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.Network#postEvent(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostEventWithNullObject() throws Exception {

    /*
     * test
     */
    Response result = target.postEvent("EventType", null);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#validateLinkMessage(Link)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testValidateLinkMessage() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("SrcPort", new Port("SrcPort", "SrcNode"));
    Node srcNode = new Node("1", "SrcNode", ports1,
        new HashMap<String, String>());
    nodes.put("SrcNode", srcNode);

    Map<String, Port> ports2 = new HashMap<>();
    ports2.put("DstPort", new Port("DstPort", "DstNode"));
    Node dstNode = new Node("1", "DstNode", ports2,
        new HashMap<String, String>());
    nodes.put("DstNode", dstNode);

    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    Link link = new Link("LinkId", "SrcNode", "SrcPort", "DstNode",
        "DstPort");

    /*
     * test
     */
    String result = Whitebox.invokeMethod(target, "validateLinkMessage",
        link);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#validateLinkMessage(Link)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testValidateLinkMessage_ExistsLink() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("SrcPort", new Port("SrcPort", "SrcNode"));
    Node srcNode = new Node("1", "SrcNode", ports1,
        new HashMap<String, String>());
    nodes.put("SrcNode", srcNode);

    Map<String, Port> ports2 = new HashMap<>();
    ports2.put("DstPort", new Port("DstPort", "DstNode"));
    Node dstNode = new Node("1", "DstNode", ports2,
        new HashMap<String, String>());
    nodes.put("DstNode", dstNode);

    Map<String, Link> links = new HashMap<>();
    Link settingLink = new Link("LinkId", "SrcNode", "SrcPort", "DstNode",
        "DstPort");
    links.put("LinkId", settingLink);

    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    Link link = new Link("LinkId", "SrcNode", "SrcPort", "DstNode",
        "DstPort");

    /*
     * test
     */
    String result = Whitebox.invokeMethod(target, "validateLinkMessage",
        link);

    /*
     * check
     */
    assertThat(result.contains("the link"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#validateLinkMessage(Link)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testValidateLinkMessage_InvalidNode() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("SrcPort", new Port("SrcPort", "SrcNode"));
    Node srcNode = new Node("1", "SrcNode", ports1,
        new HashMap<String, String>());
    nodes.put("SrcNode", srcNode);

    Map<String, Port> ports2 = new HashMap<>();
    ports2.put("DstPort", new Port("DstPort", "DstNode"));
    Node dstNode = new Node("1", "DstNode", ports2,
        new HashMap<String, String>());
    nodes.put("DstNode", dstNode);

    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    Link linkInvalidSrcNode = new Link("LinkId", "InvalidNode", "SrcPort",
        "DstNode", "DstPort");
    Link linkInvalidDstNode = new Link("LinkId", "SrcNode", "SrcPort",
        "InvalidNode", "DstPort");

    /*
     * test
     */
    String resultInvalidSrcNode = Whitebox.invokeMethod(target,
        "validateLinkMessage", linkInvalidSrcNode);
    String resultInvalidDstNode = Whitebox.invokeMethod(target,
        "validateLinkMessage", linkInvalidDstNode);

    /*
     * check
     */
    assertThat(resultInvalidSrcNode.contains("the src node"), is(true));
    assertThat(resultInvalidDstNode.contains("the dst node"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#validateLinkMessage(Link)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testValidateLinkMessage_InvalidPort() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("SrcPort", new Port("SrcPort", "SrcNode"));
    Node srcNode = new Node("1", "SrcNode", ports1,
        new HashMap<String, String>());
    nodes.put("SrcNode", srcNode);

    Map<String, Port> ports2 = new HashMap<>();
    ports2.put("DstPort", new Port("DstPort", "DstNode"));
    Node dstNode = new Node("1", "DstNode", ports2,
        new HashMap<String, String>());
    nodes.put("DstNode", dstNode);

    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);
    Whitebox.setInternalState(target, "topology", topology);

    Link linkInvalidSrcPort = new Link("LinkId", "SrcNode", "InvalidPort",
        "DstNode", "DstPort");
    Link linkInvalidDstPort = new Link("LinkId", "SrcNode", "SrcPort",
        "SrcNode", "InvaluePort");

    /*
     * test
     */
    String resultInvalidSrcPort = Whitebox.invokeMethod(target,
        "validateLinkMessage", linkInvalidSrcPort);
    String resultInvalidDstPort = Whitebox.invokeMethod(target,
        "validateLinkMessage", linkInvalidDstPort);

    /*
     * check
     */
    assertThat(resultInvalidSrcPort.contains("the src port"), is(true));
    assertThat(resultInvalidDstPort.contains("the dst port"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#checkFlowSequence(Flow, Flow)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCheckFlowSequence_Put() throws Exception {

     // #TODO

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#postPacket(PacketQueue, Packet)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostPacket_InQueue() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    PacketQueueSet packetQueueSet = Whitebox.getInternalState(target,
        "packetQueue");
    InPacketQueue queue = Whitebox.getInternalState(packetQueueSet,
        "inQueue");

    Packet packet = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), new HashMap<String, String>());

    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "notifyInPacketAdded",
        (Packet) anyObject());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "postPacket", queue,
        packet);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("notifyInPacketAdded",
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#postPacket(PacketQueue, Packet)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testPostPacket_OutQueue() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    PacketQueueSet packetQueueSet = Whitebox.getInternalState(target,
        "packetQueue");
    OutPacketQueue queue = Whitebox.getInternalState(packetQueueSet,
        "outQueue");

    Packet packet = new OutPacket("PacketId", "NodeId",
        new ArrayList<String>(), new ArrayList<String>(),
        "data".getBytes(), new HashMap<String, String>());

    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "notifyOutPacketAdded",
        (Packet) anyObject());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "postPacket", queue,
        packet);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("notifyOutPacketAdded",
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, boolean, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacket_InPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Key1", "Value1");
    Packet packet = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(), attributes);
    packetQueue.enqueuePacket(packet);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, true, "attributes=\"Key1=Value1\"");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    @SuppressWarnings("unchecked")
    Map<String, Packet> resultPackets = result.getBody(Map.class);
    assertThat(resultPackets.containsKey("0000000000"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, boolean, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacket_NoQuery() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();
    packetQueue.enqueuePacket(new InPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus packetStatus = result.getBody(PacketStatus.class);
    assertThat(packetStatus.getInStatus().getPacketCount(), is(1L));
    assertThat(packetStatus.getOutStatus().getPacketCount(), is(0L));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, boolean, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacket_OutPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new OutPacketQueue();
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Key1", "Value1");
    Packet packet = new OutPacket("PacketId", "NodeId",
        new ArrayList<String>(), new ArrayList<String>(),
        "data".getBytes(), attributes);
    packetQueue.enqueuePacket(packet);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, true, "attributes=\"Key1=Value1\"");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    @SuppressWarnings("unchecked")
    Map<String, Packet> resultPackets = result.getBody(Map.class);
    assertThat(resultPackets.containsKey("0000000000"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, boolean, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacket_OutPacket_NoQuery() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new OutPacketQueue();
    packetQueue.enqueuePacket(new OutPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, false, "");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus packetStatus = result.getBody(PacketStatus.class);
    assertThat(packetStatus.getInStatus().getPacketCount(), is(0L));
    assertThat(packetStatus.getOutStatus().getPacketCount(), is(1L));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePackets(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePackets_InPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new InPacketQueue();
    queue.enqueuePacket(new InPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePackets", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus status = queue.getPacketStatus();
    assertThat(status.getInStatus().packetQueueCount, is(0L));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePackets(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePackets_OutPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new OutPacketQueue();
    queue.enqueuePacket(new OutPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePackets", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    PacketStatus status = queue.getPacketStatus();
    assertThat(status.getOutStatus().packetQueueCount, is(0L));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadPacket_InPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new InPacketQueue();
    queue.enqueuePacket(new InPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getHeadPacket", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadPacket_InPacket_NoConnect() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new InPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getHeadPacket", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadPacket_OutPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new OutPacketQueue();
    queue.enqueuePacket(new OutPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getHeadPacket", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetHeadPacket_OutPacket_NoConnect() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new OutPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getHeadPacket", queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deleteHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteHeadPacket_InPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new InPacketQueue();
    queue.enqueuePacket(new InPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deleteHeadPacket",
        queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deleteHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteHeadPacket_InPacket_EmptyPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new InPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deleteHeadPacket",
        queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deleteHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteHeadPacket_OutPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new OutPacketQueue();
    queue.enqueuePacket(new OutPacket());

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deleteHeadPacket",
        queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deleteHeadPacket(PacketQueue)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeleteHeadPacket_OutPacket_EmptyPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue queue = new OutPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deleteHeadPacket",
        queue);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NO_CONTENT));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacketPacketQueueString() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();
    Packet packet = new InPacket("PacketId", "Nodeid", "PortId",
        "data".getBytes(), new HashMap<String, String>());
    packetQueue.enqueuePacket(packet);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPacketPacketQueueString_NoPacket() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getPacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.NOT_FOUND));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePacket_InQueue() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();
    Packet packet = new InPacket("PacketId", "Nodeid", "PortId",
        "data".getBytes(), new HashMap<String, String>());
    packetQueue.enqueuePacket(packet);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Packet resultPacket = result.getBody(InPacket.class);

    assertThat(resultPacket.getPacketId(), is("0000000000"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePacket_InQueueEmpty() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new InPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePacket_OutQueue() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new OutPacketQueue();
    Packet packet = new OutPacket("PacketId", "Nodeid",
        new ArrayList<String>(), new ArrayList<String>(),
        "data".getBytes(), new HashMap<String, String>());
    packetQueue.enqueuePacket(packet);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Packet resultPacket = result.getBody(InPacket.class);

    assertThat(resultPacket.getPacketId(), is("0000000000"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#deletePacket(PacketQueue, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDeletePacket_OutQueueEmpty() throws Exception {

    /*
     * setting
     */
    PacketQueue packetQueue = new OutPacketQueue();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "deletePacket",
        packetQueue, "0000000000");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyTopologyChanged(Topology, Topology, Action)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyTopologyChanged() throws Exception {

    /*
     * setting
     */
    Topology prev = new Topology();
    Topology curr = new Topology();
    TopologyChanged.Action action = TopologyChanged.Action.add;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target,
        "notifyTopologyChanged", prev, curr, action);

    /*
     * check
     */
    verify(target).postEvent(eq("TopologyChanged"),
        (TopologyChanged) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyTopologyChangedToAdd(Topology)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyTopologyChangedToAdd() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    Topology curr = new Topology();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target,
        "notifyTopologyChangedToAdd", curr);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("notifyTopologyChanged",
        (Topology) null, curr,
        TopologyChanged.Action.add);

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyTopologyChangedToUpdate(Topology, Topology)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyTopologyChangedToUpdate() throws Exception {

    /*
     * setting
     */
    createPowerSpy();

    Topology prev = new Topology();
    Topology curr = new Topology();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target,
        "notifyTopologyChangedToUpdate", prev, curr);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("notifyTopologyChanged",
        prev, curr, TopologyChanged.Action.update);

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyNodeChanged(Node, Node, Action)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyNodeChanged() throws Exception {

    /*
     * setting
     */
    Node prev = new Node("NodeIdPrev");
    Node curr = new Node("NodeIdCurr");
    NodeChanged.Action action = NodeChanged.Action.add;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyNodeChanged",
        prev, curr, action);

    /*
     * check
     */
    verify(target).postEvent(eq("NodeChanged"), (NodeChanged) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyPortChanged(Port, Port, Action)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyPortChanged() throws Exception {

    /*
     * setting
     */
    Port prev = new Port("PortIdPrev");
    Port curr = new Port("PortIdCurr");
    PortChanged.Action action = PortChanged.Action.add;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyPortChanged",
        prev, curr, action);

    /*
     * check
     */
    verify(target).postEvent(eq("PortChanged"), (PortChanged) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyLinkChanged(Link, Link, Action)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyLinkChanged() throws Exception {

    /*
     * setting
     */
    Link prev = new Link("LinkIdPrev");
    Link curr = new Link("LinkIdCurr");
    LinkChanged.Action action = LinkChanged.Action.add;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyLinkChanged",
        prev, curr, action);

    /*
     * check
     */
    verify(target).postEvent(eq("LinkChanged"), (LinkChanged) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyFlowChanged(Flow, Flow, Action)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyFlowChanged() throws Exception {

    /*
     * setting
     */
    Flow prev = new Flow("LinkIdPrev");
    Flow curr = new Flow("LinkIdCurr");
    FlowChanged.Action action = FlowChanged.Action.add;

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyFlowChanged",
        prev, curr, action);

    /*
     * check
     */
    verify(target).postEvent(eq("FlowChanged"), (FlowChanged) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyInPacketAdded(Packet)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyInPacketAdded() throws Exception {

    /*
     * setting
     */
    Packet inPacket = new InPacket();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyInPacketAdded",
        inPacket);

    /*
     * check
     */
    verify(target).postEvent(eq("InPacketAdded"),
        (InPacketAdded) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#notifyOutPacketAdded(Packet)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testNotifyOutPacketAdded() throws Exception {

    /*
     * setting
     */
    Packet outPacket = new OutPacket();

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "notifyOutPacketAdded",
        outPacket);

    /*
     * check
     */
    verify(target).postEvent(eq("OutPacketAdded"),
        (OutPacketAdded) anyObject());

    assertThat(result.statusCode, is(Response.ACCEPTED));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getNodeByPhysicalId(String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetNodeByPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");
    Node node1 = new Node("1", "NodeId1", new HashMap<String, Port>(),
        attributes1);

    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("physical_id", "PhysicalId2");
    Node node2 = new Node("1", "NodeId2", new HashMap<String, Port>(),
        attributes2);

    Map<String, String> attributes3 = new HashMap<String, String>();
    attributes3.put("physical_id", "PhysicalId3");
    Node node3 = new Node("1", "NodeId3", new HashMap<String, Port>(),
        attributes3);

    Map<String, Node> nodes = new HashMap<>();
    nodes.put("NodeId1", node1);
    nodes.put("NodeId2", node2);
    nodes.put("NodeId3", node3);
    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    String result = Whitebox.invokeMethod(target, "getNodeByPhysicalId",
        "PhysicalId2");

    /*
     * check
     */
    assertThat(result, is("NodeId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#getPortByPhysicalId(String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testGetPortByPhysicalId() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();

    Map<String, String> attributes1 = new HashMap<>();
    attributes1.put("physical_id", "PhysicalId1");
    Port port1 = new Port("1", "PortId1", "NodeId1", "OutLink1", "Inlink1",
        attributes1);
    Map<String, Port> ports1 = new HashMap<>();
    ports1.put("PortId1", port1);
    Node node1 = new Node("1", "NodeId1", ports1,
        new HashMap<String, String>());
    nodes.put("NodeId1", node1);

    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("physical_id", "PhysicalId2");
    Port port2 = new Port("1", "PortId2", "NodeId2", "OutLink2", "Inlink2",
        attributes2);
    Map<String, Port> ports2 = new HashMap<>();
    ports1.put("PortId2", port2);
    Node node2 = new Node("1", "NodeId2", ports2,
        new HashMap<String, String>());
    nodes.put("NodeId2", node2);

    Map<String, String> attributes3 = new HashMap<String, String>();
    attributes3.put("physical_id", "PhysicalId3");
    Port port3 = new Port("1", "PortId2", "NodeId2", "OutLink2", "Inlink2",
        attributes2);
    Map<String, Port> ports3 = new HashMap<>();
    ports1.put("PortId3", port3);
    Node node3 = new Node("1", "NodeId3", ports3,
        new HashMap<String, String>());
    nodes.put("NodeId3", node3);

    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);

    Whitebox.setInternalState(target, "topology", topology);

    /*
     * test
     */
    Port result = Whitebox.invokeMethod(target, "getPortByPhysicalId",
        "PhysicalId2");

    /*
     * check
     */
    assertThat(result.getId(), is("PortId2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#createParser()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCreateParser() throws Exception {

    /*
     * test
     */
    RequestParser<?> result = Whitebox.invokeMethod(target, "createParser");

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    Object state = Whitebox.getInternalState(result, "headState");
    assertThat(state, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#createErrorResponse(int, Object)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCreateErrorResponseIntObject() throws Exception {

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "createErrorResponse",
        Response.INTERNAL_SERVER_ERROR, "Body");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(result.getBody(String.class), is("Body"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#createErrorResponse(int, Object, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCreateErrorResponseIntObjectString() throws Exception {

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "createErrorResponse",
        Response.INTERNAL_SERVER_ERROR, "Body",
        "message");

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));
    assertThat(result.getBody(String.class), is("Body"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#isNeededVerboseNodeEvent()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testIsNeededVerboseNodeEvent() throws Exception {

    /*
     * test
     */
    boolean result = Whitebox.invokeMethod(target,
        "isNeededVerboseNodeEvent");

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.Network#isNeededVerbosePortEvent()}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testIsNeededVerbosePortEvent() throws Exception {

    /*
     * test
     */
    boolean result = Whitebox.invokeMethod(target,
        "isNeededVerbosePortEvent");

    /*
     * check
     */
    assertThat(result, is(true));

  }

  private void createNodePortForLink() throws Exception {
    Node srcNode = new Node();
    target.putNode("SrcNodeId", srcNode);
    Port srcPort = new Port();
    target.putPort("SrcNodeId", "SrcPortId", srcPort);

    Node dstNode = new Node();
    target.putNode("DstNodeId", dstNode);
    Port dstPort = new Port();
    target.putPort("DstNodeId", "DstPortId", dstPort);
  }

}
