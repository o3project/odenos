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

package org.o3project.odenos.core.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.PacketStatus;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for NetworkInterface.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ NetworkInterface.class })
public class NetworkInterfaceTest {

  private NetworkInterface target;
  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());
    target = PowerMockito
        .spy(new NetworkInterface(dispatcher, "NetworkId"));

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    dispatcher = null;
    target = null;

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#NetworkInterface(org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher, java.lang.String)}
   * .
   */
  @Test
  public void testNetworkInterface() {

    /*
     * setting
     */
    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn("NewSystemManagerId").when(dispatcher).getSystemManagerId();

    /*
     * test
     */
    NetworkInterface result = new NetworkInterface(dispatcher, "NetworkId");

    /*
     * check
     */
    MessageDispatcher resultDispatcher = Whitebox.getInternalState(result,
        "dispatcher");
    assertThat(resultDispatcher, is(dispatcher));
    assertThat(result.getNetworkId(), is("NetworkId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#NetworkInterface(org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher, java.lang.String)}
   * .
   */
  @Test
  public void testNetworkInterface_Null() {

    /*
     * test
     */
    NetworkInterface result = new NetworkInterface(null, null);

    /*
     * check
     */
    // TODO is null patameter effective?
    assertThat(result, is(notNullValue()));

    MessageDispatcher resultDispatcher = Whitebox.getInternalState(result,
        "dispatcher");
    assertThat(resultDispatcher, is(nullValue()));
    assertThat(result.getNetworkId(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getNetworkId()}.
   */
  @Test
  public void testGetNetworkId() {

    /*
     * test
     */
    String result = target.getNetworkId();

    /*
     * check
     */
    assertThat(result, is("NetworkId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#setNetworkId(java.lang.String)}
   * .
   */
  @Test
  public void testSetNetworkId() {

    /*
     * test
     */
    target.setNetworkId("NewNetworkID");

    /*
     * check
     */
    String networkId = target.getNetworkId();
    assertThat(networkId, is("NewNetworkID"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getTopology()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetTopology() throws Exception {

    /*
     * setting
     */
    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(), anyObject());
    doReturn(new Topology()).when(mockResponse).getBody(Topology.class);

    /*
     * test
     */
    Topology result = target.getTopology();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putTopology(org.o3project.odenos.core.component.network.topology.Topology)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutTopology() throws Exception {

    /*
     * setting
     */
    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();
    Topology topology = new Topology(nodes, links);

    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "putObjectToNetwork",
        "NetworkId",
        "topology", topology);

    /*
     * test
     */
    Response result = target.putTopology(topology);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("putObjectToNetwork",
        "NetworkId",
        "topology", topology);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("NodeId");

    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "postObjectToNetwork",
        "NetworkId",
        "topology/nodes", node);

    /*
     * test
     */
    Response result = target.postNode(node);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("postObjectToNetwork",
        "NetworkId",
        "topology/nodes", node);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getNodes()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetNodes() throws Exception {

    /*
     * setting
     */
    Node node = new Node("nodeId");
    Response initResponse = target.putNode(node);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new HashMap<String, Node>()).when(mockResponse).getBodyAsMap(
        Node.class);

    /*
     * test
     */
    Map<String, Node> result = target.getNodes();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology/nodes");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getNode(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("nodeId");
    Response initResponse = target.putNode(node);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new Node()).when(mockResponse).getBody(Node.class);

    /*
     * test
     */
    Node result = target.getNode("nodeId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putNode(org.o3project.odenos.component.network.topology.TopologyObject.NodeMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("nodeId");

    /*
     * test
     */
    Response result = target.putNode(node);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "putObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId",
        node);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delNode(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelNode() throws Exception {

    /*
     * setting
     */
    String nodeId = "nodeId";

    /*
     * test
     */
    Response result = target.delNode(nodeId);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "delObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getPhysicalNode(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetPhysicalNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("NodeId");
    PowerMockito.doReturn(new Response(Response.OK, node)).when(target,
        "getObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId");

    /*
     * test
     */
    Node result = target.getPhysicalNode("PhysicalId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId");

    assertThat(result, is(node));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putPhysicalNode(org.o3project.odenos.core.component.network.topology.Node)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutPhysicalNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("NodeId");
    node.putAttribute("physical_id", "PhysicalId");

    PowerMockito.doReturn(new Response(Response.OK, node)).when(target,
        "putObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId", node);

    /*
     * test
     */
    Response result = target.putPhysicalNode(node);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("putObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId", node);

    assertThat(result.statusCode, is(Response.OK));

    assertThat(result.getBody(Node.class), is(node));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delPhysicalNode(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelPhysicalNode() throws Exception {

    /*
     * setting
     */
    Node node = new Node("NodeId");
    node.putAttribute("physical_id", "PhysicalId");

    PowerMockito.doReturn(new Response(Response.OK, node)).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId");

    /*
     * test
     */
    Response result = target.delPhysicalNode("PhysicalId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId",
        "topology/physical_nodes/PhysicalId");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postPort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("PortId", "NodeId");

    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "postObjectToNetwork",
        "NetworkId",
        "topology/nodes/NodeId/ports", port);

    /*
     * test
     */
    Response result = target.postPort(port);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("postObjectToNetwork",
        "NetworkId",
        "topology/nodes/NodeId/ports", port);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getPorts(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetPorts() throws Exception {

    /*
     * setting
     */
    Port port = new Port("PortId", "NodeId");
    Map<String, Port> settingPorts = new HashMap<>();
    settingPorts.put("PortId", port);

    PowerMockito.doReturn(new Response(Response.OK, settingPorts)).when(
        target,
        "getObjectToNetwork", "NetworkId",
        "topology/nodes/NodeId/ports");

    /*
     * test
     */
    Map<String, Port> result = target.getPorts("NodeId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId",
        "topology/nodes/NodeId/ports");

    assertThat(result.size(), is(1));
    assertThat(result.get("PortId"), is(port));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getPort(java.lang.String, java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("portId", "nodeId");
    Response initResponse = target.putPort(port);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new Port()).when(mockResponse).getBody(Port.class);

    /*
     * test
     */
    Port result = target.getPort("nodeId", "portId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId/ports/portId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putPort(org.o3project.odenos.component.network.topology.TopologyObject.PortMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("portId", "nodeId");

    /*
     * test
     */
    Response result = target.putPort(port);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "putObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId/ports/portId", port);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delPort(java.lang.String, java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelPort() throws Exception {

    /*
     * test
     */
    String nodeId = "nodeId";
    String portId = "portId";
    Response result = target.delPort(nodeId, portId);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "delObjectToNetwork", "NetworkId",
        "topology/nodes/nodeId/ports/portId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getPhysicalPort(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetPhysicalPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("PortId", "NodeId");
    port.putAttribute("physical_id", "PhysicalId");

    PowerMockito.doReturn(new Response(Response.OK, port)).when(target,
        "getObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId");

    /*
     * test
     */
    Port result = target.getPhysicalPort("PhysicalId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId");

    assertThat(result, is(port));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putPhysicalPort(org.o3project.odenos.core.component.network.topology.Port)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutPhysicalPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("PortId", "NodeId");
    port.putAttribute("physical_id", "PhysicalId");

    PowerMockito.doReturn(new Response(Response.OK, port)).when(target,
        "putObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId", port);

    /*
     * test
     */
    Response result = target.putPhysicalPort(port);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("putObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId", port);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delPhysicalPort(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelPhysicalPort() throws Exception {

    /*
     * setting
     */
    Port port = new Port("PortId", "NodeId");
    port.putAttribute("physical_id", "PhysicalId");

    PowerMockito.doReturn(new Response(Response.OK, port)).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId");

    /*
     * test
     */
    Response result = target.delPhysicalPort("PhysicalId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId",
        "topology/physical_ports/PhysicalId");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postLink(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostLink() throws Exception {

    /*
     * setting
     */
    Link link = new Link("LinkId");

    PowerMockito.doReturn(new Response(Response.OK, link)).when(target,
        "postObjectToNetwork",
        "NetworkId",
        "topology/links", link);

    /*
     * test
     */
    Response result = target.postLink(link);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("postObjectToNetwork",
        "NetworkId",
        "topology/links", link);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getLinks()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetLinks() throws Exception {

    /*
     * setting
     */
    Link node = new Link("linkId");
    Response initResponse = target.putLink(node);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new HashMap<String, Link>()).when(mockResponse).getBodyAsMap(
        Link.class);

    /*
     * test
     */
    Map<String, Link> result = target.getLinks();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology/links");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getLink(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetLink() throws Exception {

    /*
     * setting
     */
    Link node = new Link("linkId");
    Response initResponse = target.putLink(node);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new Object()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new Link()).when(mockResponse).getBody(Link.class);

    /*
     * test
     */
    Link result = target.getLink("linkId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "topology/links/linkId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putLink(org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutLink() throws Exception {

    /*
     * setting
     */
    Link link = new Link("linkId");

    /*
     * test
     */
    Response result = target.putLink(link);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "putObjectToNetwork", "NetworkId",
        "topology/links/linkId",
        link);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delLink(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelLink() throws Exception {

    /*
     * test
     */
    String linkId = "linkId";
    Response result = target.delLink(linkId);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "delObjectToNetwork", "NetworkId",
        "topology/links/linkId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postFlow(org.o3project.odenos.core.component.network.flow.Flow)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostFlow() throws Exception {

    /*
     * setting
     */
    Flow flow = new Flow();

    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "postObjectToNetwork",
        "NetworkId", "flows",
        flow);

    /*
     * test
     */
    Response result = target.postFlow(flow);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target)
        .invoke("postObjectToNetwork", "NetworkId", "flows", flow);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getFlowSet()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetFlowSet() throws Exception {

    /*
     * setting
     */
    Flow flow = new Flow("FlowId") {

      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return null;
      }
    };
    Response initResponse = target.putFlow(flow);
    assertThat(initResponse, is(notNullValue()));

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        new FlowSet()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new FlowSet()).when(mockResponse).getBody(FlowSet.class);

    /*
     * test
     */
    FlowSet result = target.getFlowSet();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "flows");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getFlow(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetFlow() throws Exception {

    /*
     * setting
     */
    Flow flow =
        new BasicFlow("123", "FlowId", "Owner", true, "Priority",
            "none", null, null, null,
            null);
    Object responseBody = flow.writeValue();

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        responseBody));

    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(),
        eq(Request.Method.GET),
        anyString(),
        eq(null));

    /*
     * test
     */
    Flow result = target.getFlow("FlowId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "flows/FlowId");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getFlow(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetFlow_Flow() throws Exception {

    /*
     * setting
     */
    Flow flow = new Flow();
    Object responseBody = flow.writeValue();

    Response mockResponse = Mockito.spy(new Response(Response.OK,
        responseBody));

    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(),
        eq(Request.Method.GET),
        anyString(),
        eq(null));

    /*
     * test
     */
    Flow result = target.getFlow("FlowId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "flows/FlowId");

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putFlow(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutFlow() throws Exception {

    /*
     * setting
     */
    Flow flow = new Flow("FlowId") {

      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return null;
      }
    };

    Response mockResponse = new Response(Response.OK, new Object());
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(),
        eq(Request.Method.PUT),
        eq("flows/FlowId"), anyObject());

    /*
     * test
     */
    Response result = target.putFlow(flow);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "putObjectToNetwork", "NetworkId",
        "flows/FlowId", flow);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delFlow(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelFlow() throws Exception {

    /*
     * setting
     */
    Flow settingFlow = new Flow("FlowId", "Owner", true, "Priority");
    PowerMockito.doReturn(settingFlow).when(target).getFlow("FlowId");

    Response settingResponse = new Response(Response.OK,
        ValueFactory.createRawValue("Body"));
    PowerMockito.doReturn(settingResponse).when(target, "sendRequest",
        "NetworkId",
        Request.Method.DELETE,
        "flows/FlowId", settingFlow);

    /*
     * test
     */
    String flowId = "FlowId";
    Response result = target.delFlow(flowId);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.DELETE, "flows/FlowId",
        settingFlow);

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getPackets()}
   * @throws Exception
   */
  @Test
  public void testGetPackets() throws Exception {

    /*
     * setting
     */
    InPacket inPacket = new InPacket();
    Response initInResponse = target.postInPacket(inPacket);
    assertThat(initInResponse, is(notNullValue()));

    OutPacket outPacket = new OutPacket();
    Response initOutResponse = target.postOutPacket(outPacket);
    assertThat(initOutResponse, is(notNullValue()));

    Response mockResponse = Mockito.mock(Response.class);
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(),
        anyObject());
    doReturn(new PacketStatus()).when(mockResponse).getBody(
        PacketStatus.class);

    /*
     * test
     */
    PacketStatus result = target.getPackets();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

  }

  @Test
  public void testGetPackets_NoPackets() throws Exception {

    /*
     * setting
     */
    Response settingResponse = new Response(Response.OK, new PacketStatus());
    PowerMockito.doReturn(settingResponse).when(target,
        "getObjectToNetwork", "NetworkId",
        "packets");

    /*
     * test
     */
    PacketStatus result = target.getPackets();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId", "packets");
    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postInPacket(org.o3project.odenos.core.component.network.packet.PacketObject.InPacketMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostInPacket() throws Exception {

    /*
     * setting
     */
    InPacket inPacket = new InPacket();

    /*
     * test
     */
    Response result = target.postInPacket(inPacket);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "postObjectToNetwork", "NetworkId",
        "packets/in", inPacket);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getInPackets()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetInPackets() throws Exception {

    /*
     * setting
     */
    PacketStatus packetStatus = new PacketStatus();

    PowerMockito.doReturn(new Response(Response.OK, packetStatus)).when(
        target,
        "getObjectToNetwork", "NetworkId",
        "packets/in");

    /*
     * test
     */
    PacketStatus result = target.getInPackets();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId", "packets/in");

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delInPackets()}.
   *
   * @throws Exception
   */
  @Test
  public void testDelInPackets() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "packets/in");

    /*
     * test
     */
    Response result = target.delInPackets();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId", "packets/in");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getInPacketHead()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetInPacketHead() throws Exception {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Key", "Value");
    InPacket settingPacket = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(),
        attributes);
    settingPacket.type = "InPacket";
    Value packetValue = settingPacket.writeValue();

    PowerMockito.doReturn(new Response(Response.OK, packetValue)).when(
        target,
        "getObjectToNetwork", "NetworkId",
        "packets/in/head");

    /*
     * test
     */
    InPacket result = target.getInPacketHead();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId",
        "packets/in/head");

    assertThat(result.getType(), is("InPacket"));
    assertThat(result.getPacketId(), is("PacketId"));
    assertThat(result.getNodeId(), is("NodeId"));
    assertThat(result.getPortId(), is("PortId"));
    assertThat(result.getData(), is("data".getBytes()));
    assertThat(result.getAttributes().size(), is(1));
    assertThat(result.getAttributes().get("Key"), is("Value"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delInPacketHead()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelInPacketHead() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "packets/in/head");

    /*
     * test
     */
    Response result = target.delInPacketHead();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId",
        "packets/in/head");

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getInPacket(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetInPacket() throws Exception {

    /*
     * setting
     */
    InPacket inPacket = new InPacket("PacketId", "NodeId", "PortId",
        "data".getBytes(),
        new HashMap<String, String>());
    Response mockResponse = Mockito.spy(new Response(Response.OK, inPacket
        .writeValue()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(), anyObject());
    doReturn(new InPacket()).when(mockResponse).getBody(InPacket.class);

    /*
     * test
     */
    InPacket result = target.getInPacket("packetId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "packets/in/packetId");

    assertThat(result.type, is("InPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delInPacket(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelInPacket() throws Exception {

    /*
     * test
     */
    String id = "Id";
    Response result = target.delInPacket(id);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "delObjectToNetwork", "NetworkId",
        "packets/in/Id");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postOutPacket(org.o3project.odenos.core.component.network.packet.PacketObject.OutPacketMessage)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostOutPacket() throws Exception {

    /*
     * setting
     */
    OutPacket outPacket = new OutPacket();

    /*
     * test
     */
    Response result = target.postOutPacket(outPacket);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "postObjectToNetwork", "NetworkId",
        "packets/out",
        outPacket);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getOutPackets()}.
   *
   * @throws Exception
   */
  @Test
  public void testGetOutPackets() throws Exception {

    /*
     * setting
     */
    PacketStatus packetStatus = new PacketStatus();
    PowerMockito.doReturn(new Response(Response.OK, packetStatus)).when(
        target,
        "getObjectToNetwork", "NetworkId",
        "packets/out");

    /*
     * test
     */
    PacketStatus result = target.getOutPackets();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId", "packets/out");

    assertThat(result, is(packetStatus));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delOutPackets()}.
   *
   * @throws Exception
   */
  @Test
  public void testDelOutPackets() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "packets/out");

    /*
     * test
     */
    Response result = target.delOutPackets();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId", "packets/out");

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getOutPacketHead()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetOutPacketHead() throws Exception {

    /*
     * setting
     */
    List<String> portList = new ArrayList<String>(Arrays.asList("PortId"));
    List<String> portExceptList = new ArrayList<String>(
        Arrays.asList("ExceptId"));
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Key", "Value");
    OutPacket settingPacket =
        new OutPacket("PacketId", "NodeId", portList, portExceptList,
            "data".getBytes(),
            attributes);
    settingPacket.type = "OutPacket";
    Value packetValue = settingPacket.writeValue();

    PowerMockito.doReturn(new Response(Response.OK, packetValue)).when(
        target,
        "getObjectToNetwork", "NetworkId",
        "packets/out/head");

    /*
     * test
     */
    OutPacket result = target.getOutPacketHead();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("getObjectToNetwork",
        "NetworkId",
        "packets/out/head");

    assertThat(result.getType(), is("OutPacket"));
    assertThat(result.getPacketId(), is("PacketId"));
    assertThat(result.getNodeId(), is("NodeId"));
    assertThat(result.getPorts().size(), is(1));
    assertThat(result.getPorts().contains("PortId"), is(true));
    assertThat(result.getExceptPorts().size(), is(1));
    assertThat(result.getExceptPorts().contains("ExceptId"), is(true));
    assertThat(result.getData(), is("data".getBytes()));
    assertThat(result.getAttributes().size(), is(1));
    assertThat(result.getAttributes().get("Key"), is("Value"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delOutPacketHead()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelOutPacketHead() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "")).when(target,
        "delObjectToNetwork",
        "NetworkId",
        "packets/out/head");

    /*
     * test
     */
    Response result = target.delOutPacketHead();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("delObjectToNetwork",
        "NetworkId",
        "packets/out/head");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getOutPacket(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetOutPacket() throws Exception {

    /*
     * setting
     */
    OutPacket outPacket =
        new OutPacket("PacketId", "NodeId", new ArrayList<String>(),
            new ArrayList<String>(),
            "data".getBytes(), new HashMap<String, String>());
    Response mockResponse = Mockito.spy(new Response(Response.OK, outPacket
        .writeValue()));
    PowerMockito.doReturn(mockResponse).when(target, "sendRequest",
        anyString(), anyObject(),
        anyString(), anyObject());
    doReturn(new OutPacket()).when(mockResponse).getBody(OutPacket.class);

    /*
     * test
     */
    OutPacket result = target.getOutPacket("packetId");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "getObjectToNetwork", "NetworkId",
        "packets/out/packetId");

    assertThat(result.type, is("OutPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delOutPacket(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelOutPacket() throws Exception {

    /*
     * test
     */
    String id = "Id";
    Response result = target.delOutPacket(id);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke(
        "delObjectToNetwork", "NetworkId",
        "packets/out/Id");

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link
   * org.o3project.odenos.component.NetworkInterface#putAttributeOfNode(Map<
   * String, String)}.
   */
  @Test
  public void testPutAttributeOfNode() {

    /*
     * setting
     */
    Map<String, String> settingAttributes1 = new HashMap<>();
    settingAttributes1.put("Node1", "Value1");
    Map<String, String> settingAttributes2 = new HashMap<>();
    settingAttributes1.put("Node2", "Value2");

    Map<String, Node> settingNodes = new HashMap<>();
    settingNodes.put("Node1", new Node("NodeId1", "1",
        new HashMap<String, Port>(),
        settingAttributes1));
    settingNodes.put("Node2", new Node("NodeId2", "1",
        new HashMap<String, Port>(),
        settingAttributes2));

    doReturn(settingNodes).when(target).getNodes();

    doReturn(new Response(Response.OK, new Object())).when(target).putNode(
        (Node) anyObject());

    Map<String, String> attributes = new HashMap<>();
    attributes.put("Node1", "Value11");
    attributes.put("Node2", "Value22");

    /*
     * test
     */
    Response result = target.putAttributeOfNode(attributes);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

    Map<String, String> expectAttributes = new HashMap<>();
    expectAttributes.put("Node1", "Value11");
    expectAttributes.put("Node2", "Value22");

    Node expectNode1 = new Node("Node1", "1", new HashMap<String, Port>(),
        expectAttributes);
    Node expectNode2 = new Node("Node2", "1", new HashMap<String, Port>(),
        expectAttributes);
    verify(target).putNode(expectNode1);
    verify(target).putNode(expectNode2);

  }

  /**
   * Test method for {@link
   * org.o3project.odenos.component.NetworkInterface#putAttributeOfNode(Map<
   * String, String)}.
   */
  @Test
  public void testPutAttributeOfNode_NoAttributes() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<>();

    /*
     * test
     */
    Response result = target.putAttributeOfNode(attributes);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for {@link
   * org.o3project.odenos.component.NetworkInterface#putAttributeOfNode(Map<
   * String, String)}.
   */
  @Test
  public void testPutAttributeOfNode_NullAttributes() {

    /*
     * setting
     */
    Map<String, String> attributes = null;

    /*
     * test
     */
    Response result = target.putAttributeOfNode(attributes);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putStatusFaildAllFlow()}.
   */
  @Test
  public void testPutStatusFaildAllFlow() {

    /*
     * setting
     */
    FlowSet settingFlowSet = new FlowSet();

    PowerMockito.doReturn(settingFlowSet).when(target).getFlowSet();

    /*
     * test
     */
    List<Response> result = target.putStatusFaildAllFlow();

    /*
     * check
     */
    assertThat(result.size(), is(1));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#deleteAllFlow()}.
   */
  @Test
  public void testDeleteAllFlow() {

    /*
     * setting
     */
    FlowSet settingFlowSet = new FlowSet();
    settingFlowSet.createFlow("FlowId1", new BasicFlow("FlowId1"), "1");
    settingFlowSet.createFlow("FlowId2", new OFPFlow("FlowId2"), "2");
    settingFlowSet.createFlow("FlowId3", new BasicFlow("FlowId3"), "3");

    PowerMockito.doReturn(settingFlowSet).when(target).getFlowSet();

    /*
     * test
     */
    List<Response> result = target.deleteAllFlow();

    /*
     * check
     */
    assertThat(result.size(), is(3));

    assertThat(result.get(0).statusCode, is(Response.OK));
    assertThat(result.get(1).statusCode, is(Response.OK));
    assertThat(result.get(2).statusCode, is(Response.OK));

    verify(target, times(3)).delFlow(anyString());
    verify(target).delFlow("FlowId1");
    verify(target).delFlow("FlowId2");
    verify(target).delFlow("FlowId3");

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#deleteAllFlow()}.
   */
  @Test
  public void testDeleteAllFlow_NoFlow() {

    /*
     * test
     */
    List<Response> result = target.deleteAllFlow();

    /*
     * check
     */
    assertThat(result.size(), is(1));

    Response response = result.get(0);
    assertThat(response.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#deleteTopology()}.
   */
  @Test
  public void testDeleteTopology() {

    /*
     * setting
     */
    Map<String, Port> settingPorts = new HashMap<>();
    settingPorts.put("Port1", new Port("Port1"));
    Map<String, String> settingAttributes = new HashMap<>();
    settingAttributes.put("Attr1", "Value1");

    Map<String, Node> settingNodes = new HashMap<>();
    settingNodes.put("Node1", new Node("1", "Node1", settingPorts,
        settingAttributes));
    settingNodes.put("Node2", new Node("1", "Node2", settingPorts,
        settingAttributes));
    Map<String, Link> settingLinks = new HashMap<>();
    settingLinks.put("Link1", new Link("Link1"));
    settingLinks.put("Link2", new Link("Link2"));

    Topology settingTopoloy = new Topology(settingNodes, settingLinks);
    doReturn(settingTopoloy).when(target).getTopology();

    /* after verify */
    doReturn(new Response(Response.OK, new Object())).when(target).delLink(
        anyString());
    doReturn(new Response(Response.OK, new Object())).when(target).delPort(
        anyString(),
        anyString());
    doReturn(new Response(Response.OK, new Object())).when(target).delNode(
        anyString());

    /*
     * test
     */
    List<Response> result = target.deleteTopology();

    assertThat(result.size(), is(4));

    Response response = result.get(0);
    assertThat(response.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#deleteTopology()}.
   */
  @Test
  public void testDeleteTopology_NoTopology() {

    /*
     * test
     */
    List<Response> result = target.deleteTopology();

    /*
     * check
     */
    assertThat(result.size(), is(1));

    Response response = result.get(0);
    assertThat(response.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postObjectToNetwork(String, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToNetwork() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "sendRequest",
        "NetworkId",
        Request.Method.POST, "Path", "Body");

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "postObjectToNetwork",
            "NetworkId", "Path", "Body");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.POST,
        "Path", "Body");

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#postObjectToNetwork(String, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToNetwork_FailSendRequest() throws Exception {

    /*
     * setting
     */
    PowerMockito.doThrow(new RuntimeException()).when(target,
        "sendRequest", "NetworkId",
        Request.Method.POST, "Path", "Body");

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "postObjectToNetwork",
            "NetworkId", "Path", "Body");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.POST,
        "Path", "Body");

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putObjectToNetwork(String, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToNetwork() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "sendRequest",
        "NetworkId",
        Request.Method.PUT, "Path", "Body");

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "putObjectToNetwork",
            "NetworkId", "Path", "Body");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.PUT,
        "Path", "Body");

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#putObjectToNetwork(String, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToNetwork_FailSendRequest() throws Exception {

    /*
     * setting
     */
    PowerMockito.doThrow(new RuntimeException()).when(target,
        "sendRequest", "NetworkId",
        Request.Method.PUT, "Path", "Body");

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "putObjectToNetwork",
            "NetworkId", "Path", "Body");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.PUT,
        "Path", "Body");

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delObjectToNetwork(String, String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToNetwork() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "sendRequest",
        "NetworkId",
        Request.Method.DELETE, "Path", null);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "delObjectToNetwork",
        "NetworkId", "Path");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.DELETE, "Path", null);

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#delObjectToNetwork(String, String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToNetwork_FailSendRequest() throws Exception {

    /*
     * setting
     */
    PowerMockito.doThrow(new RuntimeException()).when(target,
        "sendRequest", "NetworkId",
        Request.Method.DELETE, "Path", null);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "delObjectToNetwork",
        "NetworkId", "Path");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.DELETE, "Path", null);

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getObjectToNetwork(String, String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObjectToNetwork() throws Exception {

    /*
     * setting
     */
    PowerMockito.doReturn(new Response(Response.OK, "Body")).when(target,
        "sendRequest",
        "NetworkId",
        Request.Method.GET, "Path", null);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getObjectToNetwork",
        "NetworkId", "Path");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.GET,
        "Path", null);

    assertThat(result.statusCode, is(Response.OK));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#getObjectToNetwork(String, String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObjectToNetwork_FailSendRequest() throws Exception {

    /*
     * setting
     */
    PowerMockito.doThrow(new RuntimeException()).when(target,
        "sendRequest", "NetworkId",
        Request.Method.GET, "Path", null);

    /*
     * test
     */
    Response result = Whitebox.invokeMethod(target, "getObjectToNetwork",
        "NetworkId", "Path");

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("sendRequest", "NetworkId",
        Request.Method.GET,
        "Path", null);

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.NetworkInterface#sendRequest(String, Request.Method, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSendRequest() throws Exception {

    /*
     * setting
     */

    doReturn(new Response(Response.OK, new Object())).when(dispatcher)
        .requestSync(
            (Request) anyObject(), anyString());

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "sendRequest", "NetworkId",
            Request.Method.GET,
            "Path", "Body");

    /*
     * check
     */
    verify(dispatcher).requestSync((Request) anyObject(), anyString());

    assertThat(result.statusCode, is(Response.OK));
  }

}
