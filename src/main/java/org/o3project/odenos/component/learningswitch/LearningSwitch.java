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

package org.o3project.odenos.component.learningswitch;

import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OFPOutPacket;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.core.util.PathCalculator;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * LearningSwitch that emulates a layer 2 switch.
 *
 */
public class LearningSwitch extends Logic {
  private static final Logger log = LoggerFactory.getLogger(LearningSwitch.class);

  public static final String ORIGINAL = "original";
  public static final int MAC_LENGTH = 12;
  public static final String DEFAULT_PRIORITY = "0";
  public static final Integer DEFAULT_IDLE_TIMER = new Integer(60);
  public static final Integer DEFAULT_HARD_TIMER = new Integer(300);
  protected final RequestParser<IActionCallback> parser;

  protected String network;
  protected HashMap<String, String> fdb;
  protected HashMap<String, Flow> flows;
  protected PathCalculator pathCalculator;
  protected Integer idleTimeout;
  protected Integer hardTimeout;

  /**
   * Constructors.
   * @param objectId ID for Object.
   * @param baseUri Base URI
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   *
   * @deprecated @see #LearningSwitch(String, MessageDispatcher)
   */
  @Deprecated
  public LearningSwitch(
      final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {
    this(objectId, dispatcher);
  }

  /**
   * Constructors.
   * @param objectId ID for Object.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   */
  public LearningSwitch(
      final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    this.network = null;
    this.fdb = new HashMap<String, String>();
    this.flows = new HashMap<String, Flow>();
    this.pathCalculator = new PathCalculator();
    this.idleTimeout = DEFAULT_IDLE_TIMER;
    this.hardTimeout = DEFAULT_HARD_TIMER;
    log.info("created");
  }

  /**
   * Get Super Type of Component.
   *
   * @return Super Type of Component
   */
  @Override
  protected String getSuperType() {
    return LearningSwitch.class.getSimpleName();
  }

  /**
   * Description of Component.
   */
  private static final String DESCRIPTION = "Learning Switch";

  /**
   * Get Description of Component.
   *
   * @return Description
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  /**
   * Get Connection Type which can be connected to Component.
   *
   * @return Connection Types of Component.
   */
  @Override
  protected String getConnectionTypes() {
    // <connection type>:<connection number>,...
    return String.format("%s:1", ORIGINAL);

  }

  // //////////////////////////////////////////////////
  //
  // NetworkComponentConnection
  //
  // //////////////////////////////////////////////////

  @Override
  protected boolean onConnectionChangedAddedPre(
      final ComponentConnectionChanged msg) {
    log.debug("");

    if (msg == null || msg.curr() == null
        || msg.curr().getObjectType() == null) {
      return false;
    }
    if (!msg.curr().getObjectType().equals(
        ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (logicId == null || !this.getObjectId().equals(logicId)) {
      return false;
    }

    ComponentConnection compConn = msg.curr();
    if (this.network != null) {
      // Changed ConectionProperty's status.
      compConn.setConnectionState(ComponentConnection.State.ERROR);
      systemMngInterface().putConnection(compConn);
      return false;
    }
    return true;
  }

  @Override
  protected boolean onConnectionChangedUpdatePre(
      final ComponentConnectionChanged message) {
    log.debug("");

    // Do Nothing.
    log.debug("");
    return false;
  }

  @Override
  protected boolean onConnectionChangedDeletePre(
      final ComponentConnectionChanged msg) {
    log.debug("");

    if (!msg.curr().getObjectType()
        .equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }

    if (this.network == null) {
      return false;
    }
    return true;
  }

  @Override
  protected void onConnectionChangedAdded(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr();
    this.network = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    subscribeNetwork();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.RUNNING);
    systemMngInterface().putConnection(curr);
  }

  @Override
  protected void onConnectionChangedDelete(
      final ComponentConnectionChanged message) {
    log.debug("");

    if (this.network == null) {
      return;
    }
    ComponentConnection curr = message.curr();
    String delNetwork = curr
        .getProperty(ComponentConnectionLogicAndNetwork.NETWORK_ID);
    if (!this.network.equals(delNetwork)) {
      return;
    }

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    unsubscribeNetwork();
    // delete network's flow.
    networkInterfaces().get(network).deleteAllFlow();
    // Clear FDB.
    this.fdb.clear();
    // Clear Flows.
    this.flows.clear();

    this.network = null;

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);

  }

  protected void subscribeNetwork() {
    log.debug("");

    addEntryEventSubscription(NODE_CHANGED, this.network);
    addEntryEventSubscription(PORT_CHANGED, this.network);
    addEntryEventSubscription(LINK_CHANGED, this.network);
    addEntryEventSubscription(FLOW_CHANGED, this.network);
    addEntryEventSubscription(IN_PACKET_ADDED, this.network);

    updateEntryEventSubscription(NODE_CHANGED, this.network, null);
    updateEntryEventSubscription(PORT_CHANGED, this.network, null);
    updateEntryEventSubscription(LINK_CHANGED, this.network, null);
    updateEntryEventSubscription(FLOW_CHANGED, this.network, null);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void unsubscribeNetwork() {
    log.debug("");

    removeEntryEventSubscription(NODE_CHANGED, this.network);
    removeEntryEventSubscription(PORT_CHANGED, this.network);
    removeEntryEventSubscription(LINK_CHANGED, this.network);
    removeEntryEventSubscription(FLOW_CHANGED, this.network);
    removeEntryEventSubscription(IN_PACKET_ADDED, this.network);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  // //////////////////////////////////////////////////
  //
  // Request Event
  //
  // //////////////////////////////////////////////////

  private RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.GET, "fdb",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getFdb();
              }
            });
        addRule(Method.GET, "settings/default_idle_timer",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getIdleTimer();
              }
            });
        addRule(Method.GET, "settings/default_hard_timer",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getHardTimer();
              }
            });
        addRule(Method.PUT, "settings/default_idle_timer",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return putIdleTimer(
                parsed.getRequest().getBody(Integer.class));
              }
            });
        addRule(Method.PUT, "settings/default_hard_timer",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return putHardTimer(
                parsed.getRequest().getBody(Integer.class));
              }
            });
        addRule(Method.DELETE, "fdb",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return deleteFdb();
              }
            });
        addRule(Method.DELETE, "fdb/<mac>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return deleteFdb(parsed.getParam("mac"));
              }
            });
      }
    };
  }

  @Override
  protected Response onRequest(
      final Request request) {
    log.debug("");

    if (request == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
    log.debug("received {}", request.path);
    RequestParser<IActionCallback>.ParsedRequest parsed =
        parser.parse(request);
    if (parsed == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }

    IActionCallback callback = parsed.getResult();
    if (callback == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }

    try {
      // Get response.
      return callback.process(parsed);
    } catch (Exception e) {
      log.error("Error unknown request");
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
  }

  protected Response getFdb() {
    log.debug("");

    Map<String, Map<String, String>> respBody =
        new HashMap<String, Map<String, String>>();
    if (fdb == null || fdb.size() == 0) {
      return new Response(Response.OK, respBody);
    }
    try {
      for (String mac : fdb.keySet()) {
        String[] plist = fdb.get(mac).split("::");
        Map<String, String> vals = new HashMap<String, String>();
        vals.put("node_id", plist[0]);
        vals.put("port_id", plist[1]);
        respBody.put(mac, vals);
      }
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    return new Response(Response.OK, respBody);
  }

  protected Response getIdleTimer() {
    log.debug("");

    return new Response(Response.OK, this.idleTimeout);
  }

  protected Response getHardTimer() {
    log.debug("");

    return new Response(Response.OK, this.hardTimeout);
  }

  protected Response putHardTimer(
      final Integer hardTimeout) {
    log.debug("");

    this.hardTimeout = hardTimeout;
    return new Response(Response.OK, this.hardTimeout);
  }

  protected Response putIdleTimer(
      final Integer idleTimeout) {
    log.debug("");

    this.idleTimeout = idleTimeout;
    return new Response(Response.OK, this.idleTimeout);
  }

  protected Response deleteFdb() {
    log.debug("");

    this.fdb.clear();
    return new Response(Response.OK, null);
  }

  protected Response deleteFdb(String mac) {
    log.debug("");

    // ex).
    // "ff55ffaaffaa" => "ff:55:ff:aa:ff:aa"
    if (mac.length() != MAC_LENGTH) {
      return new Response(Response.BAD_REQUEST, null);
    }
    String keyMac;
    try {
      StringBuffer sb = new StringBuffer(mac);
      for (int i = MAC_LENGTH - 2; i > 0; i -= 2) {
        sb.insert(i, ":");
      }
      keyMac = sb.toString();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    this.fdb.remove(keyMac);
    return new Response(Response.OK, null);
  }

  // //////////////////////////////////////////////////
  //
  // Event method override
  //
  // //////////////////////////////////////////////////

  @Override
  protected boolean onNodeAddedPre(
      final String networkId, final Node node) {
    log.debug("");

    return false;
  }

  @Override
  protected boolean onNodeUpdatePre(
      final String networkId,
      final Node prev,
      final Node curr,
      final ArrayList<String> attributesList) {
    log.debug("");

    return false;
  }

  @Override
  protected void onNodeDelete(
      final String networkId,
      final Node node) {
    log.debug("");

    if (node == null) {
      return;
    }
    // Update FDB.
    Map<String, Port> ports = node.getPortMap();
    for (String portId : ports.keySet()) {
      String value = node.getId() + "::" + portId;
      if (!this.fdb.containsValue(value)) {
        continue;
      }
      List<String> dls = new ArrayList<String>();
      for (String dl : this.fdb.keySet()) {
        if (value.equals(this.fdb.get(dl))) {
          dls.add(dl);
        }
      }
      for (String dl : dls) {
        this.fdb.remove(dl);
      }
    }

    deleteFlowByNode(node.getId());
  }

  @Override
  protected boolean onPortAddedPre(
      final String networkId, final Port port) {
    log.debug("");

    return false;
  }

  @Override
  protected boolean onPortUpdatePre(
      final String networkId,
      final Port prev,
      final Port curr,
      final ArrayList<String> attributesList) {
    log.debug("");

    return false;
  }

  @Override
  protected void onPortDelete(
      final String networkId, final Port port) {
    log.debug("");

    // Update FDB.
    String value = port.getNode() + "::" + port.getId();
    if (!this.fdb.containsValue(value)) {
      return;
    }
    List<String> dls = new ArrayList<String>();
    for (String dl : this.fdb.keySet()) {
      if (value.equals(this.fdb.get(dl))) {
        dls.add(dl);
      }
    }
    for (String dl : dls) {
      this.fdb.remove(dl);
    }

    deleteFlowByPort(port.getId());
  }

  @Override
  protected void onLinkAdded(
      final String networkId,
      final Link link) {
    log.debug("");
    if (!link.validate()) {
      return;
    }

    pathCalculator.addLink(link);
    for (String flowId : this.flows.keySet()) {
      OFPFlow ofpFlow = (OFPFlow) this.flows.get(flowId);
      if (ofpFlow.path == null) {
        continue;
      }
      String inNode = getInNode(ofpFlow);
      String outNode = getOutNode(ofpFlow);
      List<String> newPath = pathCalculator.createPath(inNode, outNode);
      for (String linkId : newPath) {
        // If Changed flow's path. Delete current flows.
        if (!ofpFlow.path.contains(linkId)) {
          networkInterfaces().get(networkId).deleteAllFlow();
          this.pathCalculator.clear();
          this.flows.clear();
          return;
        }
      }
    }
  }

  @Override
  protected void onLinkDelete(
      final String networkId,
      final Link link) {
    log.debug("");
    if (link == null || !link.validate()) {
      return;
    }
    pathCalculator.delLink(link);

    deleteFlowByLink(link.getId());
  }

  @Override
  protected boolean onFlowAddedPre(
      final String networkId, final Flow flow) {
    log.debug("");

    return false;
  }

  @Override
  protected void onFlowUpdate(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    if (curr == null || !curr.validate()) {
      return;
    }

    NetworkInterface networkIf = networkInterfaces().get(networkId);
    BasicFlow ofpFlow = getFlow(networkIf, curr.getFlowId());
    if (ofpFlow == null) {
      return;
    }

    if (ofpFlow.getStatus().equals(
        FlowObject.FlowStatus.TEARDOWN.toString())) {
      ofpFlow.setEnabled(false);
      networkIf.putFlow(ofpFlow);
      return;
    }
    if (ofpFlow.getStatus().equals(FlowObject.FlowStatus.FAILED.toString())
        || (!ofpFlow.getEnabled()
        && ofpFlow.getStatus().equals(
            FlowObject.FlowStatus.NONE.toString()))) {
      networkIf.delFlow(ofpFlow.getFlowId());
      this.flows.remove(ofpFlow.getFlowId());
    }
  }

  @Override
  protected boolean onFlowDeletePre(
      final String networkId, final Flow flow) {
    log.debug("");

    this.flows.remove(flow.getFlowId());
    return false;
  }

  @Override
  protected void onInPacketAdded(
      final String networkId,
      final InPacketAdded msg) {
    log.debug("");

    if (msg.getId() == null) {
      log.debug(">> PacketId does not exist.");
      return;
    }
    // Create NetworkInterface
    NetworkInterface networkIf = networkInterfaces().get(networkId);
    String packetId = msg.getId();
    InPacket inPacket = getInPacket(networkIf, packetId);

    // Packet Check.
    if (inPacket == null) {
      return;
    }
    if (inPacket.getHeader() == null) {
      log.info(">> InPacket's header does not exist.");
      return;
    }
    if (!inPacket.getHeader().getType().equals(
        OFPFlowMatch.class.getSimpleName())) {
      log.info(">> InPacket is not 'OFPInPacket'.");
      return;
    }
    OFPFlowMatch header =
        (OFPFlowMatch) inPacket.getHeader();
    if (header.getInNode() == null
        || header.getInPort() == null
        || header.getEthDst() == null
        || header.getEthSrc() == null) {
      log.info(">> Invalid InPacket's header.");
      return;
    }
    // Delete Network's InPacket.
    networkIf.delInPacket(inPacket.getPacketId());

    // Learning ether address.
    String newValue = header.getInNode() + "::" + header.getInPort();
    String oldValue = this.fdb.put(header.getEthSrc(), newValue);
    if (oldValue != null && !newValue.equals(oldValue)) {
      deleteFlowByEthAddr(header.getEthSrc());
    }
    String dstPortId = this.fdb.get(header.getEthDst());
    ArrayList<String> portIds = new ArrayList<String>();
    ArrayList<String> portExceptIds = new ArrayList<String>();

    String outNodeId = inPacket.getNodeId();
    if (dstPortId == null) {
      portExceptIds.add(header.getInPort());
    } else {
      String[] dstPList = dstPortId.split("::");
      portIds.add(dstPList[1]);
      // Create shortest path.
      List<String> path;
      if (header.getInNode().equals(dstPList[0])) {
        path = new ArrayList<String>();
      } else {
        path = pathCalculator.createPath(
            header.getInNode(), dstPList[0]);
      }
      // set output node.
      outNodeId = dstPList[0];
      BasicFlow sendFlow =
          createOFPFlow(inPacket, dstPList[0], dstPList[1], path);
      // PUT flow
      log.debug(String.format("Fegisted flow info: %s", this.flows));
      if (!isRegisteredFlow(sendFlow)) {
        networkIf.putFlow(sendFlow);
        this.flows.put(sendFlow.getFlowId(), sendFlow);
      }
    }
    OFPOutPacket ofpOutPacket = new OFPOutPacket(
        inPacket.getPacketId(), outNodeId,
        portIds, portExceptIds,
        header, inPacket.getData(),
        inPacket.getAttributes());
    networkIf.postOutPacket(ofpOutPacket);
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  @Override
  protected InPacket getInPacket(
      final NetworkInterface nwIf,
      final String packetId) {
    log.debug("");

    InPacket inPacket = nwIf.getInPacket(packetId);
    if (inPacket == null || inPacket.getHeader() == null) {
      return null;
    }
    if (!inPacket.getHeader().getType().equals(
        OFPFlowMatch.class.getSimpleName())) {
      return null;
    }

    return inPacket;
  }

  //////////////////////////////
  // common method
  //////////////////////////////

  protected BasicFlow createOFPFlow(
      final InPacket inPacket,
      final String outNodeId,
      final String outPortId,
      final List<String> path) {
    log.debug("");

    OFPFlowMatch header;
    try {
      header = (OFPFlowMatch) inPacket.getHeader();
    } catch (ClassCastException e) {
      log.error("Recieved Message ClassCastException.", e);
      return null;
    }

    // match is in_node, in_port, eth_src, eth_dst.
    OFPFlowMatch match = new OFPFlowMatch();
    match.setInNode(header.getInNode());
    match.setInPort(header.getInPort());
    match.setEthSrc(header.getEthSrc());
    match.setEthDst(header.getEthDst());
    // matches
    List<BasicFlowMatch> matches =
        new ArrayList<BasicFlowMatch>();
    matches.add(match);
    // actions
    List<FlowAction> actionOutput =
        new ArrayList<FlowAction>();
    actionOutput.add(new FlowActionOutput(outPortId));
    Map<String, List<FlowAction>> edgeActions =
        new HashMap<String, List<FlowAction>>();
    edgeActions.put(outNodeId, actionOutput);

    String flowId = String.format(
        "%s_%d", this.getObjectId(), this.flows.size());
    String status = "none";
    boolean enabled = true;

    return new OFPFlow(
        inPacket.getVersion(), flowId, this.getObjectId(), enabled,
        DEFAULT_PRIORITY, status, matches,
        new Long(this.idleTimeout), new Long(this.hardTimeout),
        path, edgeActions, new HashMap<String, String>());
  }

  protected void deleteFlowByLink(
      final String linkId) {
    log.debug("");

    NetworkInterface nwIf = networkInterfaces().get(this.network);
    for (String flowId : this.flows.keySet()) {
      OFPFlow ofpFlow = (OFPFlow) this.flows.get(flowId);
      List<String> path = ofpFlow.getPath();
      if (path.contains(linkId)) {
        nwIf.delFlow(flowId);
      }
    }
  }

  protected void deleteFlowByNode(
      final String nodeId) {
    log.debug("");

    NetworkInterface nwIf = networkInterfaces().get(this.network);
    for (String flowId : this.flows.keySet()) {
      OFPFlow ofpFlow = (OFPFlow) this.flows.get(flowId);
      List<String> path = ofpFlow.getPath();
      for (String linkId : path) {
        // GET link.
        Link link = nwIf.getLink(linkId);
        if (link == null) {
          continue;
        }
        // Check node.
        if (Objects.equals(link.getSrcNode(), nodeId)
            || Objects.equals(link.getDstNode(), nodeId)) {
          nwIf.delFlow(flowId);
        }
      }
    }
  }

  protected void deleteFlowByPort(
      String portId) {
    log.debug("");

    NetworkInterface nwIf = networkInterfaces().get(this.network);
    for (String flowId : this.flows.keySet()) {
      OFPFlow ofpFlow = (OFPFlow) this.flows.get(flowId);
      List<String> path = ofpFlow.getPath();
      for (String linkId : path) {
        // GET link.
        Link link = nwIf.getLink(linkId);
        if (link == null) {
          continue;
        }
        // Check port.
        if (Objects.equals(link.getSrcPort(), portId)
            || Objects.equals(link.getDstPort(), portId)) {
          nwIf.delFlow(flowId);
        }
      }
    }
  }

  protected void deleteFlowByEthAddr(
      final String ethAddr) {
    log.debug("");

    NetworkInterface nwIf = networkInterfaces().get(this.network);
    ArrayList<String> delFlowList = new ArrayList<String>();
    for (String flowId : this.flows.keySet()) {
      OFPFlow ofpFlow = (OFPFlow) this.flows.get(flowId);
      List<BasicFlowMatch> matchs = ofpFlow.getMatches();
      for (BasicFlowMatch match : matchs) {
        OFPFlowMatch ofMatch = (OFPFlowMatch) match;
        if (Objects.equals(ofMatch.getEthDst(), ethAddr)
            || Objects.equals(ofMatch.getEthSrc(), ethAddr)) {
          delFlowList.add(flowId);
        }
      }
    }
    for (String flowId : delFlowList) {
      this.flows.remove(flowId);
      nwIf.delFlow(flowId);
    }
  }

  protected final String getInNode(
      final OFPFlow flow) {
    log.debug("");

    if (!flow.validate()) {
      return null;
    }
    BasicFlowMatch flowMatchs = flow.getMatches().get(0);
    if (flowMatchs == null) {
      return null;
    }
    return flowMatchs.getInNode();
  }

  protected final String getOutNode(
      final OFPFlow flow) {
    log.debug("");

    if (!flow.validate()) {
      return null;
    }
    Map<String, List<FlowAction>> edgeAction = flow.getEdgeActions();
    for (String nodeId : edgeAction.keySet()) {
      return nodeId;
    }
    return null;
  }

  protected final boolean isRegisteredFlow(BasicFlow flow) {
    log.debug("");

    try {
      OFPFlow compFlow = (OFPFlow) flow.clone();
      for (Flow fl : this.flows.values()) {
        OFPFlow lswFlow = (OFPFlow) fl;
        compFlow.setFlowId(lswFlow.getFlowId());
        compFlow.setVersion(lswFlow.getVersion());
        if (compFlow.equals(lswFlow)) {
          return true;
        }
      }
    } catch (Exception ex) {
      log.warn("Receive Exception.", ex);
      return false;
    }
    return false;
  }

}
