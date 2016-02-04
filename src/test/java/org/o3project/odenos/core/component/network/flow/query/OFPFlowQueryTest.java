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

package org.o3project.odenos.core.component.network.flow.query;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for OFPFlowQuery.
 *
 * 
 *
 */

public class OFPFlowQueryTest {

  private String queriesString;
  private OFPFlowQuery target;

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

    queriesString = "GET <base_uri>/topology/nodes/<node_id>=2";
    target = new OFPFlowQuery(queriesString);

    nodes = new HashMap<String, Node>();
    ports1 = new HashMap<String, Port>();
    ports2 = new HashMap<String, Port>();
    links = new HashMap<String, Link>();

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
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;
    queriesString = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#OFPFlowQuery(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowQuery() {

    queriesString = "GET <base_uri>/topology/nodes/<node_id>=2";

    assertThat(target, is(instanceOf(OFPFlowQuery.class)));

    String query = queriesString.replace("\"", "");

    assertThat(
        (String) WhiteboxImpl.getInternalState(target, "queriesString"),
        is(query));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseSuccess() {

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node01,port_id=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseErr() {

    queriesString = StringUtils.join(new String[] {
        "type=node=network",
        "enabled=true",
        "status=established",
        "path=type=Network,node_id=node01,port_id=port01"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseEnabledErr() {

    queriesString = "type=node&enabled=aaa";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseStatusErr() {

    queriesString = "type=node&enabled=true&status=other";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseMatchMapNullErr() {

    queriesString = "type=node&enabled=true&status=established&match=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseNoKeyErr() {

    queriesString =
        "type=node&enabled=true&status=established&match=port_id=port01,node_id=node01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathMapErr() {

    queriesString = "type=node&enabled=true&status=established&path=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathLinkSizeErr() {

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "path=type=Network,link_id=link01,node_id=node01"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathNodePortSizeErr() {

    queriesString = StringUtils
        .join(new String[] {
            "type=node",
            "enabled=true",
            "status=established",
            "path=type=Network,node_id=node01,port_id=port01,port_id=port02"
        }, "&");

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathNodeSizeErr() {

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "path=type=Network,node_id=node01,node_id=node02"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathPortErr() {

    queriesString =
        "type=node&enabled=true&status=established&path=type=Network,port_id=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParsePathKeyErr() {

    queriesString = "type=node&enabled=true&status=established&path=type=Network";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseActionMapErr() {

    queriesString = "type=node&enabled=true&status=established&actions=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#parse()}
   * .
   */
  @Test
  public final void testParseActionKeyErr() {

    queriesString = "type=node&enabled=true&status=established&actions=node_id=Network";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.parse(), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlySuccess() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    flow.addPath(link1.getId());
    flow.addPath(link2.getId());

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node1,port_id=port1";

    Topology topology = new Topology(nodes, links);

    target = new OFPFlowQuery(queriesString);
    target.setTopology(topology);

    target.parse();

    assertThat(target.matchExactly(flow), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyClassErr() throws Exception {

    BasicFlow flow = new BasicFlow();

    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port1");
    BasicFlowMatch match2 = new BasicFlowMatch("node1", "port2");
    BasicFlowMatch match3 = new BasicFlowMatch("node2", "port1");
    BasicFlowMatch match4 = new BasicFlowMatch("node2", "port2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node01,port_id=port01";

    target = new OFPFlowQuery(queriesString);

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlySuperErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "attributes=attributes1=node01,attributes2=port01"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyEnabledErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node01,port_id=port01";

    target = new OFPFlowQuery(queriesString);

    target.parse();

    flow.setEnabled(false);

    assertThat(target.matchExactly(flow), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyStatusErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node01,port_id=port01";

    target = new OFPFlowQuery(queriesString);

    target.parse();

    flow.setEnabled(false);

    assertThat(target.matchExactly(flow), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyMatchErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    BasicFlowMatch match1 = new BasicFlowMatch("node_s1",
        "port_s1");
    BasicFlowMatch match2 = new BasicFlowMatch("node_s2",
        "port_s2");
    BasicFlowMatch match3 = new BasicFlowMatch("node_d1",
        "port_d1");
    BasicFlowMatch match4 = new BasicFlowMatch("node_d2",
        "port_d2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "match=type=BasicFlowMatch,in_port=port01,in_node=node01"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyLinkIdErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString = "type=node&enabled=true&status=established&path=link_id=link01";

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyNodePortNotFoundErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    flow.addPath(link1.getId());
    flow.addPath(link2.getId());

    queriesString =
        "type=node&enabled=true&status=established&path=node_id=node01,port_id=port01";

    Topology topology = new Topology(nodes, links);

    target = new OFPFlowQuery(queriesString);
    target.setTopology(topology);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyNodeNotFoundErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    BasicFlowMatch match1 = new BasicFlowMatch("node_s1",
        "port_01");
    BasicFlowMatch match2 = new BasicFlowMatch("node_s2",
        "port_s2");
    BasicFlowMatch match3 = new BasicFlowMatch("node_d1",
        "port_d1");
    BasicFlowMatch match4 = new BasicFlowMatch("node_d2",
        "port_d2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    flow.addPath(link1.getId());
    flow.addPath(link2.getId());

    queriesString = "type=node&enabled=true&status=established&path=node_id=node01";

    Topology topology = new Topology(nodes, links);

    target = new OFPFlowQuery(queriesString);
    target.setTopology(topology);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyActionEdgeNodeErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

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

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "actions=type=FlowActionOutput,edge_node=node01,output=port02"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyActionMatchErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

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

    flow.addEdgeAction("node01", actions.get(0));
    flow.addEdgeAction("node02", actions.get(1));

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "actions=type=FlowActionOutput,edge_node=node01,output=port02"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyActionNoEdgeNodeIdMatchErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

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

    flow.addEdgeAction("node01", actions.get(0));
    flow.addEdgeAction("node02", actions.get(1));

    queriesString = StringUtils.join(new String[] {
        "type=node",
        "enabled=true",
        "status=established",
        "actions=type=FlowActionOutput,output=port02"
    }, "&");

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyIdleTimeoutErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 100;
    long hardTimeout = 0;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    OFPFlowMatch match1 = new OFPFlowMatch();
    OFPFlowMatch match2 = new OFPFlowMatch();
    OFPFlowMatch match3 = new OFPFlowMatch();
    OFPFlowMatch match4 = new OFPFlowMatch();

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString = "type=node&enabled=true&status=established&idle_timeout=200";

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery#matchExactly(org.o3project.odenos.component.network.BaseObject)}
   * .
   */
  @Test
  public final void testMatchExactlyHardTimeoutErr() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();

    long idleTimeout = 0;
    long hardTimeout = 100;

    OFPFlow flow = new OFPFlow("1",
        "",
        "",
        true,
        "",
        "established",
        matches,
        idleTimeout,
        hardTimeout,
        path,
        edgeAction,
        flowAttributes);

    BasicFlowMatch match1 = new BasicFlowMatch("node_s1",
        "port_s1");
    BasicFlowMatch match2 = new BasicFlowMatch("node_s2",
        "port_s2");
    BasicFlowMatch match3 = new BasicFlowMatch("node_d1",
        "port_d1");
    BasicFlowMatch match4 = new BasicFlowMatch("node_d2",
        "port_d2");

    flow.addMatch(match1);
    flow.addMatch(match2);
    flow.addMatch(match3);
    flow.addMatch(match4);

    queriesString = "type=node&enabled=true&status=established&hard_timeout=200";

    target = new OFPFlowQuery(queriesString);

    target.parse();

    assertThat(target.matchExactly(flow), is(false));

  }

}
