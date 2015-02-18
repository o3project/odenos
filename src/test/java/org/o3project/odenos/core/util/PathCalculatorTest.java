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

package org.o3project.odenos.core.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PathCalculator.class, DirectedSparseMultigraph.class,
    BasicFlow.class })
public class PathCalculatorTest {

  private PathCalculator target = null;
  private Graph<String, String> graph = null;

  private static MessageDispatcher dispatcher;
  private static final String ORIGINAL_NW_ID = "original_network";

  private Map<String, Node> nodes = null;
  private Map<String, Port> ports1 = null;
  private Map<String, Port> ports2 = null;
  private Map<String, Link> links = null;

  private NetworkInterface originalNwInterface = null;

  private Node node1 = null;
  private Node node2 = null;

  private Port portN1P1 = null;
  private Port portN1P2 = null;
  private Port portN2P1 = null;
  private Port portN2P2 = null;

  private Link link1 = null;
  private Link link2 = null;

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

    target = PowerMockito.spy(new PathCalculator());
    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    nodes = new HashMap<String, Node>();
    ports1 = new HashMap<String, Port>();
    ports2 = new HashMap<String, Port>();
    links = new HashMap<String, Link>();

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());

    originalNwInterface = new NetworkInterface(dispatcher, ORIGINAL_NW_ID);

    node1 = new Node("0", "node1", ports1, new HashMap<String, String>());
    node2 = new Node("0", "node2", ports2, new HashMap<String, String>());

    nodes.put(node1.getId(), node1);
    nodes.put(node2.getId(), node2);

    originalNwInterface.putNode(node1);
    originalNwInterface.putNode(node2);

    portN1P1 = new Port("0", "port1", "node1", "", "",
        new HashMap<String, String>());
    portN1P2 = new Port("0", "port2", "node1", "", "",
        new HashMap<String, String>());
    portN2P1 = new Port("0", "port1", "node2", "", "",
        new HashMap<String, String>());
    portN2P2 = new Port("0", "port2", "node2", "", "",
        new HashMap<String, String>());

    ports1.put(portN1P1.getId(), portN1P1);
    ports1.put(portN1P2.getId(), portN1P2);
    ports2.put(portN2P1.getId(), portN2P1);
    ports2.put(portN2P2.getId(), portN2P2);

    originalNwInterface.putPort(portN1P1);
    originalNwInterface.putPort(portN1P2);
    originalNwInterface.putPort(portN2P1);
    originalNwInterface.putPort(portN2P2);

    link1 = new Link("0", "link1",
        portN1P1.getNode(), portN1P1.getId(), // src
        portN2P2.getNode(), portN2P2.getId(), // dst
        new HashMap<String, String>());
    link2 = new Link("0", "link2",
        portN2P2.getNode(), portN2P2.getId(), // src
        portN1P1.getNode(), portN1P1.getId(), // dst
        new HashMap<String, String>());
    links.put(link1.getId(), link1);
    links.put(link2.getId(), link2);
    originalNwInterface.putLink(link1);
    originalNwInterface.putLink(link2);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    graph = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#PathCalculator()}.
   */
  @Test
  public final void testPathCalculator() {

    target = PowerMockito.spy(new PathCalculator());

    assertThat(target, is(instanceOf(PathCalculator.class)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#addLink(org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   *
   * @throws Exception
   */

  @Test
  public final void testAddLinkSuccess() throws Exception {

    target = PowerMockito.spy(new PathCalculator());

    Link link = new Link("0", "link",
        portN2P1.getNode(), portN2P1.getId(), // src
        portN1P2.getNode(), portN1P2.getId(), // dst
        new HashMap<String, String>()) {
      @Override
      public boolean validate() {
        return false;
      }
    };

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito
        .doReturn(true)
        .when(graph)
        .addEdge(
            link.getId(), link.getSrcNode(), link.getDstNode(),
            EdgeType.DIRECTED);

    WhiteboxImpl.setInternalState(target, graph);

    when(target.containsLink(link.getId())).thenReturn(false);

    assertThat(target.addLink(link), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#addLink(org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testAddLinkIdErr() {

    target = PowerMockito.spy(new PathCalculator());

    Link link = new Link() {
      @Override
      public boolean validate() {
        return false;
      }
    };

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito
        .doReturn(true)
        .when(graph)
        .addEdge(
            link.getId(), link.getSrcNode(), link.getDstNode(),
            EdgeType.DIRECTED);

    when(target.containsLink(link.getId())).thenReturn(true);

    assertThat(target.addLink(link), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#addLink(org.o3project.odenos.component.network.topology.TopologyObject.LinkMessage)}
   * .
   */
  @Test
  public final void testAddLinkValidateErr() {

    target = PowerMockito.spy(new PathCalculator());

    Link link = new Link() {
      @Override
      public boolean validate() {
        return true;
      }
    };

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito
        .doReturn(true)
        .when(graph)
        .addEdge(
            link.getId(), link.getSrcNode(), link.getDstNode(),
            EdgeType.DIRECTED);

    when(target.containsLink(link.getId())).thenReturn(false);

    assertThat(target.addLink(link), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */

  @Test
  public final void testDelLinkLinkSuccess() throws Exception {

    target = PowerMockito.spy(new PathCalculator());

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito.doReturn(true).when(graph).removeEdge(link1.getId());

    WhiteboxImpl.setInternalState(target, graph);

    when(target.containsLink(link1.getId())).thenReturn(true);

    assertThat(target.delLink(link1), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public final void testDelLinkIdErr() {
    target = PowerMockito.spy(new PathCalculator());

    Link link = new Link() {
      @Override
      public String getId() {
        return null;
      }
    };

    assertThat(target.delLink(link), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(org.o3project.odenos.component.network.topology.TopologyObject.Link)}
   * .
   */
  @Test
  public final void testDelLinkNotContainErr() {

    target = PowerMockito.spy(new PathCalculator());

    Link link = new Link();

    assertThat(target.delLink(link), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(java.lang.String)}
   * .
   */
  @Test
  public final void testDelLinkString() {

    String linkId = "100";

    when(target.containsLink(linkId)).thenReturn(true);

    assertThat(target.delLink(linkId), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(java.lang.String)}
   * .
   */
  @Test
  public final void testDelLinkStringNull() {

    String linkId = null;

    assertThat(target.delLink(linkId), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#delLink(java.lang.String)}
   * .
   */
  @Test
  public final void testDelLinkStringNoContain() {

    String linkId = "100";

    when(target.containsLink(linkId)).thenReturn(false);

    assertThat(target.delLink(linkId), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#containsLink(java.lang.String)}
   * .
   */

  @Test
  public final void testContainsLinkTrue() {

    Link link = new Link();

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito.doReturn(true).when(graph).containsEdge(link.getId());

    Whitebox.setInternalState(target, graph);

    assertThat(target.containsLink(link.getId()), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#containsLink(java.lang.String)}
   * .
   */
  @Test
  public final void testContainsLinkFalse() {

    Link link = new Link();

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    PowerMockito.doReturn(false).when(graph).containsEdge(link.getId());

    Whitebox.setInternalState(target, graph);

    assertThat(target.containsLink(link.getId()), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#containsLink(java.lang.String)}
   * .
   */
  @Test
  public final void testClear() {

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());
    Whitebox.setInternalState(target, graph);

    target.clear();

    assertThat(graph, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getInNode(org.o3project.odenos.core.component.network.flow.FlowObject.BasicFlowMessage)}
   * .
   */
  @SuppressWarnings({ "static-access" })
  @Test
  public final void testGetInNodeSuccess() {

    BasicFlow flow = Mockito.spy(new BasicFlow());

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    List<BasicFlowMatch> matches = flow.matches;

    when(flow.validate()).thenReturn(true);

    when(flow.getMatches()).thenReturn(matches);

    assertThat(target.getInNode(flow), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getInNode(org.o3project.odenos.core.component.network.flow.FlowObject.BasicFlowMessage)}
   * .
   */

  @SuppressWarnings("static-access")
  @Test
  public final void testGetInNodeNg() {

    BasicFlow flow = Mockito.spy(new BasicFlow());

    when(flow.validate()).thenReturn(false);

    assertThat(target.getInNode(flow), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getInNode(org.o3project.odenos.core.component.network.flow.FlowObject.BasicFlowMessage)}
   * .
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testGetInNodeNgFlow() {

    BasicFlow flow = Mockito.spy(new BasicFlow());

    BasicFlowMatch match1 = null;
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    List<BasicFlowMatch> matches = flow.matches;

    doReturn(true).when(flow).validate();

    when(flow.getMatches()).thenReturn(matches);

    assertThat(target.getInNode(flow), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getOutNode()}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testGetOutNodeSuccess() throws Exception {

    BasicFlow flow = PowerMockito.spy(new BasicFlow());

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    @SuppressWarnings("serial")
    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    flow.addEdgeAction("node1", actions.get(0));
    flow.addEdgeAction("node2", actions.get(1));

    when(flow.validate()).thenReturn(true);

    assertThat(target.getOutNode(flow), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getOutNode()}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testGetOutNodeNg() {

    BasicFlow flow = Mockito.mock(BasicFlow.class);

    when(flow.validate()).thenReturn(false);

    assertThat(target.getOutNode(flow), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#getInNode(org.o3project.odenos.core.component.network.flow.FlowObject.BasicFlowMessage)}
   * .
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testGetOutNodeNgFlow() {

    BasicFlow flow = PowerMockito.spy(new BasicFlow());

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    when(flow.validate()).thenReturn(true);
    PowerMockito.when(flow.getEdgeActions()).thenReturn(edgeActions);

    assertThat(target.getOutNode(flow), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#createPath(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testCreatePathSuccess() throws Exception {

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    WhiteboxImpl.setInternalState(target, graph);

    target.addLink(link1);

    assertThat(target.createPath(node1.getId(), node2.getId()).isEmpty(),
        is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#createPath(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testCreatePathWithException() {

    graph = PowerMockito
        .spy(new DirectedSparseMultigraph<String, String>());

    WhiteboxImpl.setInternalState(target, graph);

    assertThat(target.createPath("node10", "node20").isEmpty(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#checkConnectivity()}.
   */
  @Test
  public final void testCheckConnectivityTrue() {

    graph = new DirectedSparseMultigraph<String, String>();

    Link link = new Link("0", "link",
        portN2P1.getNode(), portN2P1.getId(), // src
        portN1P2.getNode(), portN1P2.getId(), // dst
        new HashMap<String, String>()) {
      @Override
      public boolean validate() {
        return false;
      }
    };

    target.addLink(link);

    assertThat(target.checkConnectivity(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.util.PathCalculator#checkConnectivity()}.
   */
  @Test
  public final void testCheckConnectivityFalse() {

    assertThat(target.checkConnectivity(), is(false));
  }

}
