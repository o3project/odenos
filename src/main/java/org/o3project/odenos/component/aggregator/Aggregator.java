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

package org.o3project.odenos.component.aggregator;

import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Aggregator provides the ability to create aggregated Network
 * in a single Node that consolidates the original network topology.
 *
 */
public class Aggregator extends Logic {
  private static final Logger log = LoggerFactory.getLogger(Aggregator.class);

  public static final String AGGREGATED = "aggregated";
  public static final String ORIGINAL = "original";
  private static final String STATUS_UP = "UP";
  private static final String STATUS_DOWN = "DOWN";

  protected final RequestParser<IActionCallback> parser;
  protected PathCalculator pathCalculator;

  /**
   * Constructors.
   * @param objectId ID for Objects.
   * @param baseUri Base URI.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   * @deprecated @see org.o3project.odenos.component.aggregator.Aggregator#Aggregator(java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)
   */
  @Deprecated
  public Aggregator(final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {

    super(objectId, baseUri, dispatcher);
    pathCalculator = new PathCalculator();
    parser = createParser();
  }

  /**
   * Constructors.
   * @param objectId ID for Objects.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   */
  public Aggregator(final String objectId,
      final MessageDispatcher dispatcher) throws Exception {

    super(objectId, dispatcher);
    pathCalculator = new PathCalculator();
    parser = createParser();
  }

  /**
   * Returns type of component.
   *
   * @return type of component.
   */
  @Override
  protected String getSuperType() {
    return Aggregator.class.getSimpleName();
  }

  /**
   * Description of Component.
   */
  private static final String DESCRIPTION = "Aggregator Component";

  /**
   * Returns description.
   *
   * @return description.
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  /**
   *  Returns a type of connection.
   *
   *  @return type of connection.
   */
  @Override
  protected String getConnectionTypes() {
    // <connection type>:<connection number>,...
    return String.format("%s:1,%s:1", AGGREGATED, ORIGINAL);
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

    if (!msg.curr().getObjectType()
        .equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }

    ComponentConnection compConn = msg.curr();
    String status = compConn.getObjectState();
    String connectionType = compConn.getConnectionType();
    if (connectionType.equals(ORIGINAL)) {
      if (conversionTable().isConnectionType(ORIGINAL)) {
        status = ComponentConnection.State.ERROR;
      }
    } else if (connectionType.equals(AGGREGATED)) {
      if (conversionTable().isConnectionType(AGGREGATED)) {
        status = ComponentConnection.State.ERROR;
      }
    } else {
      return false;
    }

    if (ComponentConnection.State.ERROR.equals(status)) {
      // Changed ConectionProperty's status.
      compConn.setConnectionState(status);
      systemMngInterface().putConnection(compConn);
      return false;
    }
    return true;
  }

  @Override
  protected boolean onConnectionChangedUpdatePre(
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
    return true;
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
    return true;
  }

  @Override
  protected void onConnectionChangedAdded(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr();
    String type = curr.getConnectionType();
    String networkId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    conversionTable().addEntryConnectionType(networkId, type);
    curr.setConnectionState(ComponentConnection.State.RUNNING);

    NetworkInterface orgNwIf = null;
    NetworkInterface aggNwIf = null;
    if (type.equals(ORIGINAL)) {
      log.debug("Add original_network.");
      subscribeOriginal(networkId);
      ArrayList<String> aggNetworkId =
          conversionTable().getConnectionList(AGGREGATED);
      if (aggNetworkId.size() == 0) {
        // Changed ConectionProperty's status.
        systemMngInterface().putConnection(curr);
        return;
      }
      orgNwIf = networkInterfaces().get(networkId);
      aggNwIf = networkInterfaces().get(aggNetworkId.get(0));
    } else if (type.equals(AGGREGATED)) {
      log.debug("Add aggregated_network.");
      subscribeAggregated(networkId);

      ArrayList<String> orgNetworkId = conversionTable()
          .getConnectionList(ORIGINAL);
      if (orgNetworkId.size() == 0) {
        // Changed ConectionProperty's status.
        systemMngInterface().putConnection(curr);
        return;
      }
      orgNwIf = networkInterfaces().get(orgNetworkId.get(0));
      aggNwIf = networkInterfaces().get(networkId);
    } else {
      log.error("Unexpected network type: {}", type);
      throw new IllegalArgumentException("Unexpected network type: " + type);
    }
    // Update conversionTable.
    conversionTable().addEntryNetwork(
        orgNwIf.getNetworkId(), aggNwIf.getNetworkId());
    // Reflect to aggregated_network from original_network.
    reflecteToAggregatedFromOriginal(orgNwIf, aggNwIf);
    // Changed ConectionProperty's status.
    systemMngInterface().putConnection(curr);
  }

  @Override
  protected void onConnectionChangedUpdate(
      final ComponentConnectionChanged message) {
    // Do Nothing.
  }

  @Override
  protected void onConnectionChangedDelete(
      final ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();
    String type = curr.getConnectionType();
    String networkId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    String convNetworkId = getConvNetworkId(networkId);
    NetworkInterface orgNwIf = null;
    NetworkInterface aggNwIf = null;
    if (type.equals(ORIGINAL)) {
      unsubscribeOriginal(networkId);
      orgNwIf = networkInterfaces().get(networkId);
      if (convNetworkId != null) {
        aggNwIf = networkInterfaces().get(convNetworkId);
      }
    } else if (type.equals(AGGREGATED)) {
      unsubscribeAggregated(networkId);
      aggNwIf = networkInterfaces().get(networkId);
      if (convNetworkId != null) {
        orgNwIf = networkInterfaces().get(convNetworkId);
      }
    }

    // reset original & aggregated.
    finalizingNetwork(orgNwIf, aggNwIf);

    // reset conversionTable.
    conversionTable().delEntryConnectionType(networkId);
    conversionTable().delEntryNetwork(networkId);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  protected void subscribeOriginal(final String nwcId) {
    log.debug("");

    addEntryEventSubscription(NODE_CHANGED, nwcId);
    addEntryEventSubscription(PORT_CHANGED, nwcId);
    addEntryEventSubscription(LINK_CHANGED, nwcId);
    addEntryEventSubscription(FLOW_CHANGED, nwcId);
    addEntryEventSubscription(IN_PACKET_ADDED, nwcId);

    String attrBase = AttrElements.ATTRIBUTES + "::%s";
    ArrayList<String> nodeAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.OPER_STATUS)));
    updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

    ArrayList<String> portAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.OPER_STATUS),
        String.format(attrBase, AttrElements.MAX_BANDWIDTH),
        String.format(attrBase, AttrElements.PHYSICAL_ID),
        String.format(attrBase, AttrElements.VENDOR)));
    updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

    ArrayList<String> linkAttributes = new ArrayList<String>();
    updateEntryEventSubscription(LINK_CHANGED, nwcId, linkAttributes);

    ArrayList<String> flowAttributes = new ArrayList<String>(Arrays.asList(
        NetworkElements.STATUS,
        String.format(attrBase, AttrElements.BANDWIDTH),
        String.format(attrBase, AttrElements.LATENCY)));
    updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void unsubscribeOriginal(final String nwcId) {
    log.debug("");

    removeEntryEventSubscription(NODE_CHANGED, nwcId);
    removeEntryEventSubscription(PORT_CHANGED, nwcId);
    removeEntryEventSubscription(LINK_CHANGED, nwcId);
    removeEntryEventSubscription(FLOW_CHANGED, nwcId);
    removeEntryEventSubscription(IN_PACKET_ADDED, nwcId);

    try {
      this.applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void subscribeAggregated(final String nwcId) {
    log.debug("");

    addEntryEventSubscription(FLOW_CHANGED, nwcId);
    addEntryEventSubscription(OUT_PACKET_ADDED, nwcId);

    String attrBase = AttrElements.ATTRIBUTES + "::%s";
    ArrayList<String> nodeAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.ADMIN_STATUS)));
    updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

    ArrayList<String> portAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.ADMIN_STATUS),
        String.format(attrBase, AttrElements.UNRESERVED_BANDWIDTH),
        String.format(attrBase, AttrElements.IS_BOUNDARY)));
    updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

    ArrayList<String> flowAttributes = new ArrayList<String>(Arrays.asList(
        NetworkElements.OWNER,
        NetworkElements.ENABLED,
        NetworkElements.PRIORITY,
        String.format(attrBase, AttrElements.BANDWIDTH),
        String.format(attrBase, AttrElements.REQ_BANDWIDTH),
        String.format(attrBase, AttrElements.LATENCY),
        String.format(attrBase, AttrElements.REQ_LATENCY)));
    updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void unsubscribeAggregated(final String nwcId) {
    log.debug("");

    removeEntryEventSubscription(FLOW_CHANGED, nwcId);
    removeEntryEventSubscription(OUT_PACKET_ADDED, nwcId);
    removeEntryEventSubscription(NODE_CHANGED, nwcId);
    removeEntryEventSubscription(PORT_CHANGED, nwcId);

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
        addRule(Method.GET, "aggregated_nw_port",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwPort(AGGREGATED);
              }
            });
        addRule(Method.GET, "original_nw_port", new IActionCallback() {
          @Override
          public Response process(
              final RequestParser<IActionCallback>
              .ParsedRequest parsed) throws Exception {
            return getNwPort(ORIGINAL);
          }
        });
        addRule(Method.GET, "aggregated_nw_flow",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwFlow(AGGREGATED);
              }
            });
        addRule(Method.GET, "original_nw_flow", new IActionCallback() {
          @Override
          public Response process(
              final RequestParser<IActionCallback>
              .ParsedRequest parsed) throws Exception {
            return getNwFlow(ORIGINAL);
          }

        });
      }
    };
  }

  @Override
  protected Response onRequest(
      final Request request) {
    log.debug("");
    log.debug("received {}", request.path);
    RequestParser<IActionCallback>.ParsedRequest parsed = parser
        .parse(request);
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

  protected Response getNwPort(
      final String connectionType) throws Exception {
    HashMap<String, String[]> respBody = new HashMap<String, String[]>();

    Map<String, Node> nodes = null;
    // Get Network Interface.
    NetworkInterface networkIf;
    if (connectionType.equals(ORIGINAL)) {
      networkIf = networkInterfaces().get(
          getNetworkIdByType(ORIGINAL));
    } else {
      networkIf = networkInterfaces().get(
          getNetworkIdByType(AGGREGATED));
    }
    if (networkIf == null) {
      return new Response(Response.OK, respBody);
    }

    // Get Nodes.
    nodes = networkIf.getNodes();
    if (nodes == null) {
      return new Response(Response.OK, respBody);
    }

    // Set Response Body.
    // [Body Format]
    // dict<node_id::port_id,
    // (conversion_node_id, conversion_port_id)>
    for (String nodeId : nodes.keySet()) {
      Node node = nodes.get(nodeId);
      Map<String, Port> ports = node.getPortMap();
      for (String portId : ports.keySet()) {
        String convPort =
            getConvPortId(networkIf.getNetworkId(), nodeId, portId);
        if (convPort == null) {
          continue;
        }
        String[] list = convPort.split("::");
        String[] respVal = { list[1], list[2] };
        respBody.put(nodeId + "::" + portId, respVal);
      }
    }
    return new Response(Response.OK, respBody);
  }

  protected Response getNwFlow(final String connectionType)
      throws Exception {
    HashMap<String, String> respBody = new HashMap<String, String>();

    FlowSet flowSet = null;

    // Get Network Interface.
    NetworkInterface networkIf;
    if (connectionType.equals(ORIGINAL)) {
      networkIf = networkInterfaces().get(
          getNetworkIdByType(ORIGINAL));
    } else {
      networkIf = networkInterfaces().get(
          getNetworkIdByType(AGGREGATED));
    }
    if (networkIf == null) {
      return new Response(Response.OK, respBody);
    }

    // Get Flows(FlowSet).
    flowSet = networkIf.getFlowSet();
    if (flowSet == null) {
      return new Response(Response.OK, respBody);
    }

    // Set Response Body.
    // [Body Format]
    // dict<flow_id, conversion_flow_id>
    for (String flowId : flowSet.flows.keySet()) {
      String convFlow = getConvFlowId(networkIf.getNetworkId(), flowId);
      if (convFlow == null) {
        continue;
      }
      String[] list = convFlow.split("::");
      respBody.put(flowId, list[1]);
    }

    return new Response(Response.OK, respBody);
  }

  // //////////////////////////////////////////////////
  //
  // Event method override
  //
  // //////////////////////////////////////////////////

  @Override
  protected void onNodeAdded(final String networkId, final Node node) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);

    if (getConvNodeId(aggNetworkId, this.getObjectId()) == null) {
      node.putAttribute(AttrElements.PHYSICAL_ID, this.getObjectId());
      Node aggNodeMsg = new Node(
          node.getVersion(), this.getObjectId(),
          new HashMap<String, Port>(), node.getAttributes());
      // PUT Node
      aggNetworkIf.putNode(aggNodeMsg);
    }

    // changed node's oper_status. ("UP" -> "DOWN")
    Map<String, String> updateAttr = new HashMap<>();
    updateAttr.put(Logic.AttrElements.OPER_STATUS, STATUS_DOWN);
    aggNetworkIf.putAttributeOfNode(updateAttr);

    // Update conversionTable(add node).
    conversionTable().addEntryNode(
        networkId, node.getId(),
        aggNetworkId, this.getObjectId());
  }

  @Override
  protected boolean onNodeUpdatePre(final String networkId,
      final Node prev, final Node curr,
      final ArrayList<String> attributesList) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(ORIGINAL)) {
      // Do Nothing.
      return false;
    }

    return true;
  }

  @Override
  protected boolean onNodeDeletePre(final String networkId,
      final Node node) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return false;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return false;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);

    // Delete port of node.
    if (node.getPortMap() != null
        && node.getPortMap().size() != 0) {
      for (String pid : node.getPortMap().keySet()) {
        Port orgPort = node.getPort(pid);
        String aggPortId = getConvPortId(
            networkId, orgPort.getNode(), orgPort.getId());
        if (aggPortId == null) {
          continue;
        }
        String[] aggPortList = aggPortId.split("::");
        aggNetworkIf.delPort(aggPortList[1], aggPortList[2]);
      }
    }

    List<String> aggNodes =
        conversionTable().getNode(aggNetworkId, this.getObjectId());
    if (aggNodes.size() == 1) {
      // Delete AggregatedNw's node.
      return true;
    }

    // Update conversionTable( delete node ).
    conversionTable().delEntryNode(networkId, node.getId());
    return false;
  }

  @Override
  protected void onPortAdded(
      final String networkId,
      final Port port) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);

    // Changed nodeId of aggregated_nw's port
    String aggPortId = String.format("%s_%s", port.getNode(), port.getId());
    Port aggPort = new Port(
        port.getVersion(), aggPortId, this.getObjectId(),
        port.getOutLink(), port.getInLink(), port.getAttributes());
    aggNetworkIf.putPort(aggPort);

    // Update conversionTable
    conversionTable().addEntryPort(
        networkId, port.getNode(), port.getId(),
        aggNetworkId, this.getObjectId(), aggPortId);
  }

  @Override
  protected boolean onPortUpdatePre(String networkId, Port prev, Port curr,
      ArrayList<String> attributesList) {

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return false;
    }
    return true;
  }

  @Override
  protected boolean onPortDeletePre(String networkId, Port port) {

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return false;
    }
    return true;
  }

  @Override
  protected void onLinkAdded(final String networkId, final Link link) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return;
    }
    if (!link.validate()) {
      log.error(String.format(">> link[ %s ] is invalid.", link.getId()));
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);

    // Update path information.
    pathCalculator.addLink(link);

    // If connected_port, Delete the port (aggregated_netwok).
    String aggSrcPortId =
        getConvPortId(networkId, link.getSrcNode(), link.getSrcPort());
    String aggDstPortId =
        getConvPortId(networkId, link.getDstNode(), link.getDstPort());
    if (aggSrcPortId != null) {
      String[] list = aggSrcPortId.split("::");
      aggNetworkIf.delPort(list[1], list[2]);
      conversionTable().delEntryPort(list[0], list[1], list[2]);
    }
    if (aggDstPortId != null) {
      String[] list = aggDstPortId.split("::");
      aggNetworkIf.delPort(list[1], list[2]);
      conversionTable().delEntryPort(list[0], list[1], list[2]);
    }

    // Update Node's "opar_status".
    updateOperStatus(aggNetworkIf);
  }

  @Override
  protected void onLinkUpdate(
      final String networkId,
      final Link prev,
      final Link curr,
      final ArrayList<String> attributesList) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return;
    }
    if (!curr.validate()) {
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);
    NetworkInterface orgNetworkIf = networkInterfaces().get(networkId);

    // Need to provide API to the Network.java
    String operStatus = curr.getAttribute(AttrElements.OPER_STATUS);
    if (operStatus == null) {
      return;
    }

    // GET original_netowrk's flows.
    FlowSet flowSet = orgNetworkIf.getFlowSet();
    for (String flowId : flowSet.flows.keySet()) {
      BasicFlow orgFlow = getFlow(orgNetworkIf, flowId);

      if (!orgFlow.path.contains(curr.getId())) {
        // When the link is not included in the path,
        // it is not nothing.
        continue;
      }

      String aggFlowId = getConvFlowId(networkId, orgFlow.getFlowId());
      if (aggFlowId == null) {
        continue;
      }
      if (operStatus.equals(STATUS_UP)) {
        if (pathCalculator.addLink(curr)) {
          // Couldn't add link.
          continue;
        }
      } else if (operStatus.equals(STATUS_DOWN)) {
        if (!pathCalculator.delLink(curr.getId())) {
          // Couldn't delete link.
          continue;
        }
      }

      if (orgFlow.getMatches() == null
          || orgFlow.getMatches().size() == 0) {
        continue;
      }

      BasicFlow aggFlow = getFlow(aggNetworkIf, aggFlowId);
      // Update Flow's path.
      updateFlow(
          orgNetworkIf, aggNetworkIf, orgFlow, aggFlow);
    }

    // Update Node's "opar_status".
    updateOperStatus(aggNetworkIf);
  }

  @Override
  protected void onLinkDelete(
      final String networkId,
      final Link link) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return;
    }
    if (!link.validate()) {
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    if (aggNetworkId == null) {
      return;
    }
    NetworkInterface aggNetworkIf = networkInterfaces().get(aggNetworkId);
    NetworkInterface orgNetworkIf = networkInterfaces().get(networkId);

    // ////////////////////////////////////////////////////////////
    // (1) If unconnected_port, Add the port to aggregated_network.
    // (2) Update the flow's path to original_netowrk.
    // (3) Check original_netowrk's topology connectivity.
    // & Update aggretated_network'node.
    // ////////////////////////////////////////////////////////////

    // (1) If unconnected_port, Add port to aggregated_network.
    addUnconnectedPortToAggregated(
        orgNetworkIf, aggNetworkIf, link);

    // (2) Update the flow's path to original_netowrk.

    FlowSet flowSet = orgNetworkIf.getFlowSet();
    for (String flowId : flowSet.flows.keySet()) {
      log.debug(String.format(">> target flow : '%s'", flowId));
      BasicFlow orgFlow = (BasicFlow) flowSet.flows.get(flowId);
      // Get aggregated_network's flowId from orgFlowId.
      String aggFlowId = getConvFlowId(
          orgNetworkIf.getNetworkId(), flowId);
      if (aggFlowId == null
          || orgFlow.getMatches() == null
          || orgFlow.getMatches().size() == 0) {
        continue;
      }
      String[] list = aggFlowId.split("::");
      log.debug(String.format(">> conversion flow : '%s'", list[1]));

      orgFlow = getFlow(orgNetworkIf, flowId);
      BasicFlow aggFlow = getFlow(aggNetworkIf, list[1]);
      updateFlow(orgNetworkIf, aggNetworkIf, orgFlow, aggFlow);
    }

    // (3) Check original_netowrk's topology connectivity.
    // & Update aggretated_network's node.
    updateOperStatus(aggNetworkIf);
  }

  @Override
  protected void onFlowAdded(final String networkId,
      final Flow flow) {
    log.debug("");

    String connType =
        conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(ORIGINAL)) {
      return;
    }

    // Get NetworkInterface.
    String aggNetworkId = getNetworkIdByType(AGGREGATED);
    String orgNetworkId = getNetworkIdByType(ORIGINAL);
    if (aggNetworkId == null || orgNetworkId == null
        || !aggNetworkId.equals(networkId)) {
      return;
    }
    NetworkInterface aggNetworkIf =
        networkInterfaces().get(aggNetworkId);
    NetworkInterface orgNetworkIf =
        networkInterfaces().get(orgNetworkId);

    BasicFlow aggFlow = getFlow(aggNetworkIf, flow.getFlowId());
    if (aggFlow == null) {
      log.error("Invalid flow.");
      return;
    }
    BasicFlow orgFlow = aggFlow.clone();

    if (!updateFlow(
        orgNetworkIf, aggNetworkIf, orgFlow, aggFlow)) {
      return;
    }

    // Update conversionTable (add flowId).
    conversionTable().addEntryFlow(
        orgNetworkIf.getNetworkId(), orgFlow.getFlowId(),
        aggNetworkIf.getNetworkId(), aggFlow.getFlowId());
  }

  @Override
  protected boolean onFlowUpdatePre(
      final String networkId, final Flow prev,
      final Flow curr, final ArrayList<String> attributesList) {
    log.debug("");

    String connType =
        conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(ORIGINAL)) {
      return true;
    }

    NetworkInterface srcNetworkIf = networkInterfaces().get(networkId);
    NetworkInterface dstNetworkIf =
        networkInterfaces().get(getConvNetworkId(networkId));
    if (dstNetworkIf == null) {
      return false;
    }

    BasicFlow srcFlow = getFlow(srcNetworkIf, curr.getFlowId());
    if (srcFlow == null) {
      return false;
    }
    String dstFlowId = getConvFlowId(
        srcNetworkIf.getNetworkId(), srcFlow.getFlowId());
    if (dstFlowId == null) {
      return false;
    }
    String[] dstFlowList = dstFlowId.split("::");
    BasicFlow dstFlow = getFlow(dstNetworkIf, dstFlowList[1]);
    if (dstFlow == null) {
      return false;
    }
    // Aggregated Network ==> Aggregator
    dstFlow.setEnabled(srcFlow.getEnabled());
    dstFlow.setPriority(srcFlow.getPriority());
    updateFlow(dstNetworkIf, srcNetworkIf, dstFlow, srcFlow);

    return false;
  }

  @Override
  protected boolean onInPacketAddedPre(
      final String networkId,
      final InPacketAdded msg) {
    log.debug("");

    log.debug(
        String.format("###Received InPacketAdded:id(%s)", msg.getId()));
    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(AGGREGATED)) {
      return false;
    }
    return true;
  }

  @Override
  protected boolean onOutPacketAddedPre(final String networkId,
      final OutPacketAdded msg) {
    log.debug("");

    log.debug(String.format("###Received OutPacketAdded:id(%s)",
        msg.getId()));
    String connType = conversionTable().getConnectionType(networkId);
    if (connType != null
        && connType.equals(ORIGINAL)) {
      return false;
    }
    return true;
  }

  ////////////////////////////////////////////////////
  // common method
  ////////////////////////////////////////////////////

  @Override
  protected BasicFlow getFlow(
      final NetworkInterface nwIf,
      final String flowId) {
    log.debug("");

    if (nwIf == null || flowId == null) {
      return null;
    }
    BasicFlow flow = (BasicFlow) nwIf.getFlow(flowId);
    if (flow == null || flow.getMatches() == null
        || flow.getMatches().size() == 0) {
      return null;
    }

    return flow;
  }

  protected void reflecteToAggregatedFromOriginal(
      final NetworkInterface orgNetworkIf,
      final NetworkInterface aggNetworkIf) {
    log.debug("");

    try {
      Topology orgTopology = orgNetworkIf.getTopology();
      if (orgTopology == null) {
        return;
      }
      Map<String, Node> orgNodes = orgTopology.nodes;
      Map<String, Link> orgLinks = orgTopology.links;

      for (String nodeId : orgNodes.keySet()) {
        // add Node & Port.
        this.onNodeAdded(
            orgNetworkIf.getNetworkId(), orgNodes.get(nodeId));
        Map<String, Port> ports = orgNodes.get(nodeId).getPortMap();
        for (String portId : ports.keySet()) {
          this.onPortAdded(orgNetworkIf.getNetworkId(), ports.get(portId));
        }
      }
      for (String linkId : orgLinks.keySet()) {
        // add Link.
        this.onLinkAdded(
            orgNetworkIf.getNetworkId(), orgLinks.get(linkId));
      }

    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void finalizingNetwork(
      final NetworkInterface orgNwIf,
      final NetworkInterface aggNwIf) {

    // delete original's flow.
    if (orgNwIf != null) {
      orgNwIf.deleteAllFlow();
    }
    if (aggNwIf != null) {
      // delete aggregated's topology.
      aggNwIf.deleteTopology();
      // update aggregated's flow status
      aggNwIf.putStatusFaildAllFlow();
    }
    // update conversionTable.
    conversionTable().getFlow().clear();
    conversionTable().getLink().clear();
    conversionTable().getNode().clear();
    conversionTable().getPort().clear();
  }

  protected boolean updateFlow(
      final NetworkInterface orgNetworkIf,
      final NetworkInterface aggNetworkIf,
      final BasicFlow orgFlow,
      final BasicFlow aggFlow) {
    log.debug("");

    if (orgNetworkIf == null || aggNetworkIf == null
        || orgFlow == null || aggFlow == null) {
      return false;
    }
    if (aggFlow.getStatus() != null
        && aggFlow.getStatus().equals(FlowStatus.FAILED.toString())) {
      log.debug(">> Invalid flow.");
      return false;
    }

    try {
      orgFlow.setOwner(this.getObjectId());
      aggFlow.setOwner(this.getObjectId());

      // Get In port by matches
      String srcPort = getConvPortIdByMatches(
          aggNetworkIf.getNetworkId(), aggFlow.getMatches());
      // Get Out port by actions
      List<String> dstPorts = getConvPortIdByActions(
          aggNetworkIf.getNetworkId(), aggFlow.getEdgeActions());
      // Create a Path & Set Match.
      List<String> path = createOriginalFlowPath(srcPort, dstPorts);
      if (path == null
          || !setMatch(orgFlow.getMatches(), srcPort)) {
        aggFlow.setStatus(FlowStatus.FAILED.toString());
        // PUT aggregated_network's flow.
        log.debug(">> PUT flow to Aggregated Network.");
        aggNetworkIf.putFlow(aggFlow);
        return false;
      }
      orgFlow.path = path;
      // Set Actions
      setActions(aggNetworkIf.getNetworkId(),
          aggFlow.getEdgeActions(), orgFlow.getEdgeActions());

      // PUT original_netowrk's flow.
      log.debug(">> PUT flow to Original Network.");
      orgNetworkIf.putFlow(orgFlow);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return false;
    }
    return true;
  }

  protected void updateOperStatus(
      final NetworkInterface networkIf) {
    log.debug("");

    HashMap<String, String> attributes = new HashMap<String, String>();
    if (this.pathCalculator.checkConnectivity()) {
      // Change oper_status to "UP".
      attributes.put(AttrElements.OPER_STATUS, STATUS_UP);
    } else {
      // Change oper_status to "DOWN".
      attributes.put(AttrElements.OPER_STATUS, STATUS_DOWN);
    }
    networkIf.putAttributeOfNode(attributes);
  }

  protected void addUnconnectedPortToAggregated(
      final NetworkInterface orgNetworkIf,
      final NetworkInterface aggNetworkIf,
      final Link link) {
    log.debug("");

    try {
      pathCalculator.delLink(link);
      Port orgSrcPort = orgNetworkIf.getPort(
          link.getSrcNode(), link.getSrcPort());
      Port orgDstPort = orgNetworkIf.getPort(
          link.getDstNode(), link.getDstPort());

      if (orgSrcPort != null
          && (orgSrcPort.getInLink() == null
          || "".equals(orgSrcPort.getInLink()))
          && (orgSrcPort.getOutLink() == null
          || "".equals(orgSrcPort.getOutLink()))) {
        String aggPortId = String.format(
            "%s_%s", orgSrcPort.getNode(), orgSrcPort.getId());
        Port aggPort = new Port(
            orgSrcPort.getVersion(), aggPortId, this.getObjectId(),
            null, null, orgSrcPort.getAttributes());
        // Add port to aggregated_network.
        aggNetworkIf.putPort(aggPort);
        conversionTable().addEntryPort(
            orgNetworkIf.getNetworkId(), orgSrcPort.getNode(),
            orgSrcPort.getId(), aggNetworkIf.getNetworkId(),
            this.getObjectId(), aggPortId);
      }
      if (orgDstPort != null
          && (orgDstPort.getInLink() == null
          || "".equals(orgDstPort.getInLink()))
          && (orgDstPort.getOutLink() == null
          || "".equals(orgDstPort.getOutLink()))) {
        String aggPortId = String.format(
            "%s_%s", orgDstPort.getNode(), orgDstPort.getId());
        Port aggPort = new Port(
            orgDstPort.getVersion(), aggPortId, this.getObjectId(),
            null, null, orgDstPort.getAttributes());
        // Add port to aggregated_network.
        aggNetworkIf.putPort(aggPort);
        conversionTable().addEntryPort(
            orgNetworkIf.getNetworkId(), orgDstPort.getNode(),
            orgDstPort.getId(), aggNetworkIf.getNetworkId(),
            this.getObjectId(), aggPortId);
      }
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected List<String> createOriginalFlowPath(
      final String srcNode,
      final List<String> dstNodes) {
    log.debug("");

    List<String> path = new ArrayList<String>();
    if (srcNode == null || dstNodes == null) {
      return null;
    }
    String[] srcNList = srcNode.split("::");

    HashSet<String> nodeSet = new HashSet<String>();
    for (String dstnode : dstNodes) {
      String[] dstNList = dstnode.split("::");
      nodeSet.add(dstNList[1]);
    }

    for (String dstNId : nodeSet) {
      if (srcNList[1].equals(dstNId)) {
        continue;
      }
      List<String> plist = pathCalculator.createPath(srcNList[1], dstNId);
      if (plist == null || plist.size() == 0) {
        return null;
      }
      path.addAll(plist);
    }
    return path;
  }

  protected final String getNetworkIdByType(final String connType) {
    log.debug("");

    if (connType == null) {
      return null;
    }
    ArrayList<String> convNetowrkId =
        conversionTable().getConnectionList(connType);
    if (convNetowrkId.size() == 0) {
      return null;
    }
    return convNetowrkId.get(0);
  }

  protected final String getConvNetworkId(final String networkId) {
    log.debug("");

    if (networkId == null) {
      return null;
    }
    ArrayList<String> convNetowrkId =
        conversionTable().getNetwork(networkId);
    if (convNetowrkId.size() == 0) {
      return null;
    }
    return convNetowrkId.get(0);
  }

  protected final String getConvNodeId(
      final String networkId,
      final String nodeId) {
    log.debug("");

    if (networkId == null || nodeId == null) {
      return null;
    }
    ArrayList<String> convNodeId =
        conversionTable().getNode(networkId, nodeId);
    if (convNodeId.size() == 0) {
      return null;
    }
    return convNodeId.get(0);
  }

  protected final String getConvPortId(
      final String networkId,
      final String nodeId,
      final String portId) {
    log.debug("");

    if (networkId == null || nodeId == null || portId == null) {
      return null;
    }
    ArrayList<String> convPortId =
        conversionTable().getPort(networkId, nodeId, portId);
    if (convPortId.size() == 0) {
      return null;
    }
    return convPortId.get(0);
  }

  protected final String getConvFlowId(
      final String networkId,
      final String flowId) {
    log.debug("");

    if (networkId == null || flowId == null) {
      return null;
    }
    ArrayList<String> convFlowId =
        conversionTable().getFlow(networkId, flowId);
    if (convFlowId.size() == 0) {
      return null;
    }
    return convFlowId.get(0);
  }

  protected final boolean setMatch(
      final List<BasicFlowMatch> matches,
      final String srcPort) {
    log.debug("");

    if (srcPort == null) {
      return false;
    }
    String[] srcPList = srcPort.split("::");
    if (srcPList[1] == null || srcPList[2] == null
        || matches == null || matches.size() == 0) {
      return false;
    }
    for (BasicFlowMatch match : matches) {
      match.setInNode(srcPList[1]);
      match.setInPort(srcPList[2]);
    }
    return true;
  }

  protected final void setActions(
      final String srcNetworkId,
      final Map<String, List<FlowAction>> srcActions,
      final Map<String, List<FlowAction>> dstActions) {
    log.debug("");

    Map<String, List<FlowAction>> targetActions =
        new HashMap<String, List<FlowAction>>();
    List<FlowAction> noActionOutputs = new ArrayList<FlowAction>();

    // Convert Action.
    for (String nodeId : srcActions.keySet()) {
      for (FlowAction fact : srcActions.get(nodeId)) {
        if (fact.getType().equals(
            FlowActionOutput.class.getSimpleName())) {
          FlowActionOutput output =
              (FlowActionOutput) fact;
          String srcport = output.getOutput();
          String dstports = getConvPortId(
              srcNetworkId, this.getObjectId(), srcport);
          if (dstports == null) {
            continue;
          }
          String[] dstPList = dstports.split("::");
          if (targetActions.containsKey(dstPList[1])) {
            targetActions.get(dstPList[1]).add(
                new FlowActionOutput(dstPList[2]));
          } else {
            List<FlowAction> target = new ArrayList<FlowAction>();
            target.add(new FlowActionOutput(dstPList[2]));
            targetActions.put(dstPList[1], target);
          }
        } else {
          noActionOutputs.add(fact);
        }
      }
    }
    // Reset dstAction.
    dstActions.clear();
    for (String nodeId : targetActions.keySet()) {
      dstActions.put(nodeId, targetActions.get(nodeId));
      for (FlowAction fact : noActionOutputs) {
        dstActions.get(nodeId).add(fact);
      }
    }
  }

  protected final String getConvPortIdByMatches(
      final String networkId,
      final List<BasicFlowMatch> matches) {
    log.debug("");

    if (matches.size() == 0) {
      return null;
    }
    return getConvPortId(
        networkId, matches.get(0).getInNode(),
        matches.get(0).getInPort());
  }

  protected final List<String> getConvPortIdByActions(
      final String networkId,
      final Map<String, List<FlowAction>> actions) {
    log.debug("");

    List<String> dstPorts = new ArrayList<String>();
    for (String nodeId : actions.keySet()) {
      List<FlowAction> action = actions.get(nodeId);
      if (action == null || action.size() == 0) {
        continue;
      }
      for (FlowAction act : action) {
        if (act.getType().equals(
            FlowActionOutput.class.getSimpleName())) {
          FlowActionOutput output = (FlowActionOutput) act;
          String dstPort = getConvPortId(networkId,
              this.getObjectId(), output.getOutput());
          dstPorts.add(dstPort);
        }
      }
    }
    return dstPorts;
  }

}
