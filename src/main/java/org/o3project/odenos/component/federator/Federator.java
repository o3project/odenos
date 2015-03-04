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

package org.o3project.odenos.component.federator;

import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Federater provide the ability to create a federated network to integrate original Network.
 *
 */
public class Federator extends Logic {

  /** logger. */
  private static final Logger logger = LoggerFactory.getLogger(Federator.class);

  /** ConnectionType: Original Network. */
  public static final String ORIGINAL_NETWORK = "original";
  /** ConnectionType: Federated Network. */
  public static final String FEDERATED_NETWORK = "federated";
  /** Other Network Packet's inNode, inPort value. **/
  public static final String IN_NODE_PORT_ANY = "any";

  /** name separator for ConversionTable. */
  public static final String SEPARATOR = "::";
  /** node format. */
  public static final String NODE_FORMAT = "%s" + SEPARATOR + "%s";
  /** port format. */
  public static final String PORT_FORMAT = NODE_FORMAT + SEPARATOR + "%s";
  /** link format. */
  public static final String LINK_FORMAT = "%s" + SEPARATOR + "%s";
  /** flow format. */
  public static final String FLOW_FORMAT = "%s" + SEPARATOR + "%s";

  /** RequestPaeser Instance. */
  protected final RequestParser<IActionCallback> parser;

  /** Boundary Table. */
  protected FederatorBoundaryTable federatorBoundaryTable;

  /** Map of links. key: networkId value: LinkIdList */
  protected Map<String, List<String>> originalNwAndBoundaryLinkMap = new HashMap<>();

  protected FederatorOnFlow federatorOnFlow;

  /** Description of Component. */
  private static final String DESCRIPTION = "Federator Component";

  /**
   * Constructors.
   * @param objectId ID for Objects.
   * @param dispatcher MessageDispatcher.
   * @throws Exception if parameter is wrong.
   */
  public Federator(String objectId, MessageDispatcher dispatcher)
      throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    federatorBoundaryTable = new FederatorBoundaryTable();
    federatorOnFlow = new FederatorOnFlow(
        conversionTable(), networkInterfaces(), federatorBoundaryTable);
  }

  /**
   * Returns type of component.
   *
   * @return type of component.
   * @see org.o3project.odenos.core.component.Component#getSuperType()
   */
  @Override
  protected String getSuperType() {
    return Federator.class.getSimpleName();
  }

  /**
   * Returns a description of component.
   *
   * @return description of Component
   * @see org.o3project.odenos.core.component.Component#getDescription()
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  /**
   * Returns a type of component.
   *
   * @return type of component.
   */
  @Override
  protected String getConnectionTypes() {
    // <connection type>:<connection number>,...
    return String.format("%s:1,%s:*", FEDERATED_NETWORK, ORIGINAL_NETWORK);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedAddedPre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedAddedPre(ComponentConnectionChanged msg) {
    logger.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      logger.debug("curr is null");
      return false;
    }

    try {
      verifyType(curr.getObjectType());
      verifyId(curr);
    } catch (FederatorException ex) {
      return false;
    }

    String status = null;
    String connectionType = curr.getConnectionType();
    switch (connectionType) {
      case ORIGINAL_NETWORK:
        return true;
      case FEDERATED_NETWORK:
        if (conversionTable().isConnectionType(FEDERATED_NETWORK)) {
          status = ComponentConnection.State.ERROR;
        }
        break;
      default:
        /* unknown type */
        return false;
    }

    if (ComponentConnection.State.ERROR.equals(status)) {
      // Changed ConectionProperty's status.
      curr.setConnectionState(status);
      systemMngInterface().putConnection(curr);
      return false;
    }

    return true;

  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedUpdatePre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedUpdatePre(
      ComponentConnectionChanged msg) {
    logger.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      logger.debug("curr is null");
      return false;
    }

    try {
      verifyType(curr.getObjectType());
      verifyId(curr);

      return true;
    } catch (FederatorException ex) {
      return false;
    }
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedDeletePre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedDeletePre(
      ComponentConnectionChanged msg) {
    logger.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      logger.debug("curr is null");
      return false;
    }

    try {
      verifyType(curr.getObjectType());
      verifyId(curr);

      return true;

    } catch (FederatorException ex) {
      return false;

    }
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedAdded(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedAdded(ComponentConnectionChanged msg) {
    logger.debug("");

    ComponentConnection curr = msg.curr();
    String networkId =
        curr.getProperty(ComponentConnectionLogicAndNetwork.NETWORK_ID);
    String type = curr.getConnectionType();
    conversionTable().addEntryConnectionType(networkId, type);
    curr.setConnectionState(ComponentConnection.State.RUNNING);

    switch (type) {
      case ORIGINAL_NETWORK:
        subscribeOriginal(networkId);
        ArrayList<String> federateds =
            conversionTable().getConnectionList(FEDERATED_NETWORK);
        if (federateds.size() == 0) {
          // Changed ConectionProperty's status.
          systemMngInterface().putConnection(curr);
          return;
        }
        // Update conversionTable.
        conversionTable().addEntryNetwork(federateds.get(0), networkId);
        // Reflect to federated from original_network.
        reflectToFederatedFromOriginal(
            networkInterfaces().get(networkId),
            networkInterfaces().get(federateds.get(0)));
        break;
      case FEDERATED_NETWORK:
        subscribeFederated(networkId);
        ArrayList<String> originals =
            conversionTable().getConnectionList(ORIGINAL_NETWORK);
        if (originals.size() == 0) {
          // Changed ConectionProperty's status.
          systemMngInterface().putConnection(curr);
          return;
        }
        for (String pairId : originals) {
          // Update conversionTable.
          conversionTable().addEntryNetwork(networkId, pairId);
          // Reflect to federated from original_network.
          reflectToFederatedFromOriginal(
              networkInterfaces().get(pairId),
              networkInterfaces().get(networkId));
        }
        break;
      default:
        logger.error("unknown type: " + type);
    }
    // Changed ConectionProperty's status.
    systemMngInterface().putConnection(curr);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedUpdate(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedUpdate(ComponentConnectionChanged msg) {
    logger.debug("");
    /* do nothing */
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedDelete(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedDelete(ComponentConnectionChanged msg) {
    logger.debug("");

    ComponentConnection curr = msg.curr();
    String networkId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    String type = curr.getConnectionType();

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    switch (type) {
      case FEDERATED_NETWORK:
        unsubscribeFederated(networkId);
        List<String> orgNwIds =
            conversionTable().getNetwork(networkId);
        finalizingFederatedNetwork(orgNwIds, networkId);

        break;

      case ORIGINAL_NETWORK:
        unsubscribeOriginal(networkId);
        String fedNwIf = getConvNetworkId(networkId);
        finalizingOriginalNetwork(networkId, fedNwIf);

        break;

      default:
        logger.error("unknown type: " + type);
        return;
    }

    // reset conversionTable.
    conversionTable().delEntryConnectionType(networkId);
    conversionTable().delEntryNetwork(networkId);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  /* //////////////////////////////////////////////////
   *
   * Request Event
   *
   * //////////////////////////////////////////////////
   */

  private RequestParser<IActionCallback> createParser() {
    logger.debug("");

    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.POST,
            "settings/boundaries",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                FederatorBoundary boundary = parsed
                    .getRequest().getBody(
                        FederatorBoundary.class);
                return postBoundaries(boundary);
              }
            });
        addRule(Method.PUT,
            "settings/boundaries/<boundary_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String boundaryId = parsed.getParam("boundary_id");
                FederatorBoundary boundary = parsed
                    .getRequest().getBody(
                        FederatorBoundary.class);
                return putBoundaries(boundaryId, boundary);
              }
            });
        addRule(Method.GET,
            "settings/boundaries",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getBoundaries();
              }
            });
        addRule(Method.DELETE,
            "settings/boundaries/<boundary_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String boundaryId = parsed
                    .getParam("boundary_id");
                return delBoundaries(boundaryId);
              }
            });
        addRule(Method.GET,
            "federated_network_node",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwNode(FEDERATED_NETWORK);
              }
            });
        addRule(Method.GET,
            "original_network_node",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwNode(ORIGINAL_NETWORK);
              }
            });
        addRule(Method.GET,
            "federated_network_port",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwPort(FEDERATED_NETWORK);
              }
            });
        addRule(Method.GET,
            "original_network_port",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwPort(ORIGINAL_NETWORK);
              }
            });
        addRule(Method.GET,
            "federated_network_link",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwLink(FEDERATED_NETWORK);
              }
            });
        addRule(Method.GET,
            "original_network_link",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwLink(ORIGINAL_NETWORK);
              }
            });
        addRule(Method.GET,
            "federated_network_flow",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwFlow(FEDERATED_NETWORK);
              }
            });
        addRule(Method.GET,
            "original_network_flow",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getNwFlow(ORIGINAL_NETWORK);
              }
            });
      }
    };
  }

  @Override
  protected Response onRequest(Request request) {
    logger.debug("received {}", request.path);

    try {
      RequestParser<IActionCallback>.ParsedRequest parsed =
          parser.parse(request);
      if (parsed == null) {
        return new Response(Response.BAD_REQUEST,
            "Error unknown request ");
      }

      IActionCallback callback = parsed.getResult();
      if (callback == null) {
        return new Response(Response.BAD_REQUEST,
            "Error unknown request ");
      }
      // Get response.
      return callback.process(parsed);

    } catch (Exception ex) {
      logger.error("Error unknown request", ex);
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }

  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onNodeAdded(java.lang.String, org.o3project.odenos.component.network.topology.Node)
   */
  @Override
  protected void onNodeAdded(String networkId, Node node) {
    logger.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(FEDERATED_NETWORK)) {
      return;
    }
    String fedNwId = getNetworkIdByType(FEDERATED_NETWORK);
    if (fedNwId == null) {
      return;
    }
    NetworkInterface fedNwIf = networkInterfaces().get(fedNwId);

    // Create Federated's Node
    String fedNodeId = String.format("%s_%s", networkId, node.getId());
    Node fedNode = node.clone();
    fedNode.setId(fedNodeId);

    conversionTable().addEntryNode(networkId, node.getId(), fedNwId,
        fedNodeId);

    // PUT Node to Federated Network.
    fedNwIf.putNode(fedNode);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onPortAdded(java.lang.String, org.o3project.odenos.component.network.topology.Port)
   */
  @Override
  protected void onPortAdded(String networkId, Port port) {
    logger.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(FEDERATED_NETWORK)) {
      return;
    }
    String fedNwId = getNetworkIdByType(FEDERATED_NETWORK);
    if (fedNwId == null) {
      return;
    }

    // Create Federated's Port
    String fedNodeId = String.format("%s_%s", networkId, port.getNode());
    String fedPortId = String.format("%s_%s", fedNodeId, port.getId());
    Port fedPort = port.clone();
    fedPort.setNode(fedNodeId);
    fedPort.setId(fedPortId);

    conversionTable().addEntryPort(
        networkId, port.getNode(), port.getId(), fedNwId, fedNodeId,
        fedPortId);

    // PUT Port to Federated Network.
    NetworkInterface fedNwIf = networkInterfaces().get(fedNwId);
    fedNwIf.putPort(fedPort);

    // reflect boundary
    if (federatorBoundaryTable
        .isContainsLink(networkId, port.getNode(), port.getId())) {
      for (String key : federatorBoundaryTable.getBoundaries().keySet()) {
        FederatorBoundary boundary = federatorBoundaryTable.getEntry(key);
        boolean isBoundaryPort = false;
        if (String.valueOf(boundary.getNetwork1()).equals(networkId)
            && String.valueOf(boundary.getNode1()).equals(port.getNode())
            && String.valueOf(boundary.getPort1()).equals(port.getId())) {
          isBoundaryPort = true;
        } else if (String.valueOf(boundary.getNetwork2()).equals(networkId)
            && String.valueOf(boundary.getNode2()).equals(port.getNode())
            && String.valueOf(boundary.getPort2()).equals(port.getId())) {
          isBoundaryPort = true;
        }
        if (isBoundaryPort) {
          addBoundaryLink(boundary);
        }
      }
    }
  }

  @Override
  protected void onPortDelete(String networkId, Port port) {
    logger.debug("");

    List<String> delBoundaryIds = new ArrayList<String>();
    // reflect boundary
    if (federatorBoundaryTable
        .isContainsLink(networkId, port.getNode(), port.getId())) {
      for (String key : federatorBoundaryTable.getBoundaries().keySet()) {
        FederatorBoundary boundary = federatorBoundaryTable.getEntry(key);
        if (String.valueOf(boundary.getNetwork1()).equals(networkId)
            && String.valueOf(boundary.getNode1()).equals(port.getNode())
            && String.valueOf(boundary.getPort1()).equals(port.getId())) {
          delBoundaryIds.add(key);
        } else if (String.valueOf(boundary.getNetwork2()).equals(networkId)
            && String.valueOf(boundary.getNode2()).equals(port.getNode())
            && String.valueOf(boundary.getPort2()).equals(port.getId())) {
          delBoundaryIds.add(key);
        }
      }
    }
    for (String key : delBoundaryIds) {
      deleteBoundaryLink(key);
    }

    super.onPortDelete(networkId, port);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onLinkAdded(java.lang.String, org.o3project.odenos.component.network.topology.Link)
   */
  @Override
  protected void onLinkAdded(String networkId, Link link) {
    logger.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(FEDERATED_NETWORK)) {
      return;
    }
    if (!link.validate()) {
      return;
    }
    String fedNwId = getNetworkIdByType(FEDERATED_NETWORK);
    if (fedNwId == null) {
      return;
    }

    // Create Federated's Link
    String fedLinkId = String.format("%s_%s", networkId, link.getId());
    List<String> fedSrcPorts = conversionTable().getPort(networkId,
        link.getSrcNode(), link.getSrcPort());
    List<String> fedDstPorts = conversionTable().getPort(networkId,
        link.getDstNode(), link.getDstPort());
    if (fedSrcPorts.size() == 0 || fedDstPorts.size() == 0) {
      return;
    }
    String[] fedSrcList = fedSrcPorts.get(0).split(SEPARATOR);
    String[] fedDstList = fedDstPorts.get(0).split(SEPARATOR);
    Link fedLink = link.clone();
    fedLink.setId(fedLinkId);
    fedLink.setPorts(fedSrcList[1], fedSrcList[2], fedDstList[1],
        fedDstList[2]);

    conversionTable().addEntryLink(networkId, link.getId(), fedNwId,
        fedLinkId);

    // PUT Link to Federated Network.
    NetworkInterface fedNwIf = networkInterfaces().get(fedNwId);
    fedNwIf.putLink(fedLink);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onInPacketAddedPre(java.lang.String, org.o3project.odenos.component.network.packet.InPacketAdded)
   */
  @Override
  protected boolean onInPacketAddedPre(String networkId, InPacketAdded msg) {
    logger.debug("");

    NetworkInterface nwIf = networkInterfaces().get(networkId);
    if (nwIf == null) {
      return false;
    }

    InPacket inPacket = getInPacket(nwIf, msg.getId());
    if (inPacket == null) {
      return false;
    }

    String nodeId = inPacket.getNodeId();
    String portId = inPacket.getPortId();
    if (federatorBoundaryTable.isContainsLink(networkId, nodeId, portId)) {
      // drop packet.
      logger.info("Drop Packet.");
      return false;
    }

    return true;
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onOutPacketAddedPre(java.lang.String, org.o3project.odenos.component.network.packet.OutPacketAdded)
   */
  @Override
  protected void onOutPacketAdded(String networkId, OutPacketAdded msg) {
    logger.debug("");

    NetworkInterface nwIf = networkInterfaces().get(networkId);
    if (nwIf == null) {
      return;
    }

    OutPacket outPacket = delOutPacket(nwIf, msg.getId());
    if (outPacket == null) {
      logger.debug("outPacket not found.");
      return;
    }

    String nodeId = outPacket.getNodeId();
    if (nodeId == null) {
      logger.warn("invalid outPacket.");
      return;
    }

    // convert header.
    String inNode = outPacket.getHeader().getInNode();
    String inPort = outPacket.getHeader().getInPort();
    ArrayList<String> convPortId = conversionTable().getPort(
        networkId, inNode, inPort);

    String srcNetwork = null;
    if (convPortId.size() == 0) {
      logger.warn("not found conversion inPort.");
      outPacket.getHeader().setInNode(IN_NODE_PORT_ANY);
      outPacket.getHeader().setInPort(IN_NODE_PORT_ANY);
    } else {
      String[] plist = convPortId.get(0).split("::");
      logger.info(String.format("outPacket: inNode(%s), inport(%s)", plist[1], plist[2]));
      srcNetwork = plist[0];
      outPacket.getHeader().setInNode(plist[1]);
      outPacket.getHeader().setInPort(plist[2]);
    }

    // unicast or multicast.
    if (outPacket.getPorts().size() > 0) {
      postOutPacketNoBroadcast(networkId, outPacket);
      return;
    }

    // broadcast
    postOutPacketBroadcast(networkId, srcNetwork, outPacket);
  }

  /* (non-JavaDoc)
   * @see org.o3project.odenos.component.Logic#onFlowAdded(java.lang.String, org.o3project.odenos.component.network.flow.Flow)
   */
  @Override
  protected void onFlowAdded(String networkId, Flow flow) {
    logger.debug("");

    try {
      verifyFlow(flow);

      BasicFlow basicFlow = (BasicFlow) flow;
      int length = basicFlow.getPath().size();
      if (length == 0) {
        federatorOnFlow.flowAddedNotExistPath(networkId, basicFlow);
      } else {
        federatorOnFlow.flowAddedExistPath(networkId, basicFlow);
      }

    } catch (FederatorException ex) {
      logger.warn("validate fail: " + ex.getMessage(), ex);
      return;
    }
  }

  @Override
  protected boolean onFlowUpdatePre(String networkId, Flow prev, Flow curr,
      ArrayList<String> attributesList) {
    logger.debug("");

    try {
      verifyFlow(curr);
    } catch (FederatorException ex) {
      logger.warn("validate fail: " + ex.getMessage(), ex);
      return false;
    }

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(FEDERATED_NETWORK)) {
      return  federatorOnFlow.checkUpdateFederator(prev, curr, attributesList);
    }
    return true;
  }

  @Override
  protected void onFlowUpdate(String networkId, Flow prev, Flow curr,
      ArrayList<String> attributesList) {

    logger.debug("");
    if(!onFlowUpdatePre(networkId, prev, curr, attributesList)) {
      return;
    }

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(ORIGINAL_NETWORK)) {
      onFlowUpdateOriginal(networkId, prev, curr);
    }
    if (connType.equals(FEDERATED_NETWORK)) {
      onFlowUpdateFederate(networkId, prev, curr);
    }

  }

  protected void onFlowUpdateOriginal(String networkId, Flow prev, Flow curr) {
    logger.debug("");
    try {
      verifyFlow(curr);
    } catch (FederatorException ex) {
      logger.debug("validate: " + ex.getMessage(), ex);
      return;
    }

    BasicFlow basicFlow = (BasicFlow) curr;
    if (curr.getStatus().equals(
        FlowObject.FlowStatus.ESTABLISHED.toString())) {
      if(!federatorOnFlow.flowUpdatePreStatusEstablished(networkId, basicFlow)) {
        return;
      }
    } else if (curr.getStatus().equals(
        FlowObject.FlowStatus.FAILED.toString())) {
      federatorOnFlow.flowUpdatePreStatusFailed(networkId, basicFlow);
      return;
    } else if (curr.getStatus().equals(
        FlowObject.FlowStatus.NONE.toString())) {
      if(!federatorOnFlow.flowUpdatePreStatusNone(networkId, basicFlow)) {
        return;
      }
    }
    federatorOnFlow.flowUpdateFromOriginal(networkId, (BasicFlow)curr);
  }

  protected void onFlowUpdateFederate(String networkId, Flow prev, Flow curr) {
    logger.debug("");
    try {
      verifyFlow(curr);
      BasicFlow basicFlow = (BasicFlow) curr;
      int length = basicFlow.getPath().size();
      if (length == 0) {
        federatorOnFlow.flowUpdateNotExistPath(networkId, basicFlow);
      } else {
        federatorOnFlow.flowUpdateExistPath(networkId, basicFlow);
      }

    } catch (FederatorException ex) {
      logger.warn("validate fail: " + ex.getMessage(), ex);
      return;
    }
  }

  protected void verifyFlow(Flow flow) throws FederatorException {
    logger.debug("");

    if (flow == null) {
      throw new FederatorException("flow is null");
    }
    if (!(flow instanceof BasicFlow)) {
      throw new FederatorException("bad parameter flow");
    }
    BasicFlow basicFlow = (BasicFlow) flow;

    List<String> path = basicFlow.getPath();
    if (path == null) {
      throw new FederatorException("path is null");
    }
  }

  /**
   *
   * @param connType Type of the network.
   * @return ID for the network.
   */
  protected String getNetworkIdByType(final String connType) {
    logger.debug("");

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

  /**
   * Register the boundary.
   * @param boundary Registered boundary.
   * @return posted Boundary
   */
  protected Response postBoundaries(FederatorBoundary boundary) {
    logger.debug("");

    try {

      addBoundaryLink(boundary);

      FederatorBoundary resultBoundary =
          federatorBoundaryTable.addEntry(boundary);

      return new Response(Response.OK, resultBoundary);

    } catch (FederatorException ex) {
      return new Response(Response.CONFLICT, "boundary already exist");
    }
  }

  /**
   * Register the boundary.
   * @param boundary Registered boundary.
   * @return posted Boundary
   */
  protected Response putBoundaries(String boundaryId, FederatorBoundary boundary) {
    if (logger.isDebugEnabled()) {
      logger.debug("boundaryId: {}", boundaryId);
    }

    if (StringUtils.isEmpty(boundaryId)) {
      return new Response(Response.BAD_REQUEST, "boundary_id is empty.");
    }

    try {
      addBoundaryLink(boundary);

      FederatorBoundary resultBoundary =
          federatorBoundaryTable.addEntry(boundaryId, boundary);

      return new Response(Response.OK, resultBoundary);

    } catch (FederatorException ex) {
      return new Response(Response.CONFLICT, "boundary already exist");
    }
  }

  /**
   * Delete the boundary.
   * @param boundaryId Deleted boundary ID.
   * @return Response of the boundary delete.
   */
  protected Response delBoundaries(String boundaryId) {
    logger.debug("");

    deleteBoundaryLink(boundaryId);
    doDelAttributeBoundaryPort(federatorBoundaryTable.getEntry(boundaryId));

    federatorBoundaryTable.deleteEntry(boundaryId);

    /* do not set body */
    return new Response(Response.OK, null);
  }

  /**
   * Return the boundary.
   * @return Response of the boundary list.
   */
  protected Response getBoundaries() {
    logger.debug("");

    Map<String, FederatorBoundary> boundaries =
        federatorBoundaryTable.getBoundaries();

    return new Response(Response.OK, boundaries);
  }

  /**
   *
   * @param connType Type of the network.
   * @return Response of the nodes in the network.
   */
  protected Response getNwNode(String connType) {
    logger.debug("");
    switch (connType) {
      case FEDERATED_NETWORK:
        return new Response(Response.OK, getNwNodesFed());
      case ORIGINAL_NETWORK:
        return new Response(Response.OK, getNwNodesOrigin());
    }
    return new Response(Response.BAD_REQUEST, "");
  }

  /**
   *
   * @param connType Type of the network.
   * @return Response of the ports in the network.
   */
  protected Response getNwPort(String connType) {
    logger.debug("");
    switch (connType) {
      case FEDERATED_NETWORK:
        return new Response(Response.OK, getNwPortsFed());
      case ORIGINAL_NETWORK:
        return new Response(Response.OK, getNwPortsOrigin());
    }
    return new Response(Response.BAD_REQUEST, "");
  }

  /**
   *
   * @param connType Type of the network.
   * @return Response of the links in the network.
   */
  protected Response getNwLink(String connType) {
    logger.debug("");
    switch (connType) {
      case FEDERATED_NETWORK:
        return new Response(Response.OK, getNwLinksFed());
      case ORIGINAL_NETWORK:
        return new Response(Response.OK, getNwLinksOrigin());
    }
    return new Response(Response.BAD_REQUEST, "");
  }

  /**
   *
   * @param connType Type of the network.
   * @return Response of the flows in the network.
   */
  protected Response getNwFlow(String connType) {
    logger.debug("");
    switch (connType) {
      case FEDERATED_NETWORK:
        return new Response(Response.OK, getNwFlowsFed());
      case ORIGINAL_NETWORK:
        return new Response(Response.OK, getNwFlowsOrigin());
    }
    return new Response(Response.BAD_REQUEST, "");
  }

  protected void subscribeOriginal(final String nwcId) {
    logger.debug("");

    try {
      addEntryEventSubscription(IN_PACKET_ADDED, nwcId);
      addEntryEventSubscription(NODE_CHANGED, nwcId);
      addEntryEventSubscription(PORT_CHANGED, nwcId);
      addEntryEventSubscription(LINK_CHANGED, nwcId);

      String attrBase = AttrElements.ATTRIBUTES + SEPARATOR + "%s";

      ArrayList<String> nodeAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.PHYSICAL_ID),
              String.format(attrBase, AttrElements.VENDOR)));
      updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

      ArrayList<String> portAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.MAX_BANDWIDTH),
              String.format(attrBase, AttrElements.PHYSICAL_ID),
              String.format(attrBase, AttrElements.VENDOR)));
      updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

      ArrayList<String> linkAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.LATENCY),
              String.format(attrBase, AttrElements.MAX_BANDWIDTH)));
      updateEntryEventSubscription(LINK_CHANGED, nwcId, linkAttributes);

      ArrayList<String> flowAttributes = new ArrayList<String>(
          Arrays.asList(
              NetworkElements.STATUS,
              String.format(attrBase, AttrElements.REQ_BANDWIDTH),
              String.format(attrBase, AttrElements.REQ_LATENCY)));
      updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

      applyEventSubscription();

    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

  protected void unsubscribeOriginal(final String nwcId) {
    logger.debug("");

    try {
      removeEntryEventSubscription(NODE_CHANGED, nwcId);
      removeEntryEventSubscription(PORT_CHANGED, nwcId);
      removeEntryEventSubscription(LINK_CHANGED, nwcId);
      removeEntryEventSubscription(IN_PACKET_ADDED, nwcId);
      removeEntryEventSubscription(FLOW_CHANGED, nwcId);

      applyEventSubscription();

    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

  protected void subscribeFederated(final String nwcId) {
    logger.debug("");

    try {
      addEntryEventSubscription(OUT_PACKET_ADDED, nwcId);
      addEntryEventSubscription(FLOW_CHANGED, nwcId);

      String attrBase = AttrElements.ATTRIBUTES + SEPARATOR + "%s";

      ArrayList<String> nodeAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.ADMIN_STATUS)));
      updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

      ArrayList<String> portAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.ADMIN_STATUS),
              String.format(attrBase,
                  AttrElements.UNRESERVED_BANDWIDTH),
              String.format(attrBase, AttrElements.VENDOR)));
      updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

      ArrayList<String> linkAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.COST),
              String.format(attrBase, AttrElements.REQ_LATENCY),
              String.format(attrBase,
                  AttrElements.UNRESERVED_BANDWIDTH),
              String.format(attrBase, AttrElements.REQ_BANDWIDTH)));
      updateEntryEventSubscription(LINK_CHANGED, nwcId, linkAttributes);

      ArrayList<String> flowAttributes = new ArrayList<String>(
          Arrays.asList(
              NetworkElements.OWNER,
              NetworkElements.ENABLED,
              String.format(attrBase, AttrElements.BANDWIDTH),
              String.format(attrBase, AttrElements.LATENCY)));
      updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

      applyEventSubscription();

    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

  protected void unsubscribeFederated(final String nwcId) {
    logger.debug("");

    try {
      removeEntryEventSubscription(FLOW_CHANGED, nwcId);
      removeEntryEventSubscription(OUT_PACKET_ADDED, nwcId);
      removeEntryEventSubscription(NODE_CHANGED, nwcId);
      removeEntryEventSubscription(PORT_CHANGED, nwcId);
      removeEntryEventSubscription(LINK_CHANGED, nwcId);

      applyEventSubscription();

    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

  //////////////////////////////
  // common methods
  //////////////////////////////

  protected void reflectToFederatedFromOriginal(
      final NetworkInterface orgNwIf,
      final NetworkInterface fedNwIf) {
    logger.debug("");

    try {
      Topology orgTopology = orgNwIf.getTopology();
      if (orgTopology == null) {
        return;
      }
      Map<String, Node> orgNodes = orgTopology.getNodeMap();
      Map<String, Link> orgLinks = orgTopology.getLinkMap();

      for (String nodeId : orgNodes.keySet()) {
        Node node = orgNodes.get(nodeId);
        Node sendNode = node.clone();
        sendNode.clearPorts();
        // Add Node.
        this.onNodeAdded(
            orgNwIf.getNetworkId(), sendNode);
        // Add Ports.
        for (String portId : node.getPortMap().keySet()) {
          this.onPortAdded(
              orgNwIf.getNetworkId(), node.getPort(portId));
        }
      }
      for (String linkId : orgLinks.keySet()) {
        // add Link.
        this.onLinkAdded(
            orgNwIf.getNetworkId(), orgLinks.get(linkId));
      }

    } catch (Exception e) {
      logger.error("Recieved Message Exception.", e);
    }
  }

  protected void finalizingOriginalNetwork(
      final String orgNwId,
      final String fedNwId) {
    logger.debug("");

    if (orgNwId == null) {
      return;
    }
    NetworkInterface orgNwIf = networkInterfaces().get(orgNwId);
    NetworkInterface fedNwIf = null;
    if (fedNwId != null) {
      fedNwIf = networkInterfaces().get(fedNwId);
      for (String id : conversionTable().getNetwork(fedNwId)) {
        if (id.equals(orgNwId)) {
          continue;
        }
        NetworkInterface nwIf = networkInterfaces().get(id);
        FlowSet fedFlowSet = fedNwIf.getFlowSet();
        if (fedFlowSet != null) {
          for (String fid : fedFlowSet.getFlows().keySet()) {
            nwIf.delFlow(fid);
          }
        }
      }
    }

    Map<String, Node> orgNodes = null;
    Map<String, Link> orgLinks = null;
    orgNodes = orgNwIf.getNodes();
    orgLinks = orgNwIf.getLinks();
    // delete original's flows
    orgNwIf.deleteAllFlow();

    if (fedNwIf != null) {
      if (orgLinks != null) {
        for (String linkId : orgLinks.keySet()) {
          // delete federated's links.
          Link orgLink = orgNwIf.getLink(linkId);
          if (orgLink != null) {
            this.onLinkDelete(orgNwId, orgLink);
          } else {
            conversionTable().delEntryLink(orgNwId, orgNwId);
          }
        }
      }
      // delete boundary's link
      if (originalNwAndBoundaryLinkMap.containsKey(orgNwId)) {
        List<String> boundaryLinks = originalNwAndBoundaryLinkMap.get(orgNwId);
        for (String linkId : boundaryLinks) {
          fedNwIf.delLink(linkId);
        }
        boundaryLinks.clear();
      }
      if (orgNodes != null) {
        for (String nodeId : orgNodes.keySet()) {
          List<String> convNodes = conversionTable().getNode(orgNwId,
              nodeId);
          if (convNodes.size() == 0) {
            continue;
          }
          // delete federated's ports.
          for (String portId : orgNodes.get(nodeId).getPortMap()
              .keySet()) {
            Port orgPort = orgNwIf.getPort(nodeId, portId);
            if (orgPort != null) {
              this.onPortDelete(orgNwId, orgPort);
            } else {
              conversionTable().delEntryPort(orgNwId, nodeId, portId);
            }
          }
          // delete federated's nodes.
          Node orgNode = orgNwIf.getNode(nodeId);
          if (orgNode != null) {
            this.onNodeDelete(orgNwId, orgNode);
          } else {
            conversionTable().delEntryNode(orgNwId, nodeId);
          }
        }
      }
    }
  }

  protected void finalizingFederatedNetwork(
      final List<String> orgNwIds,
      final String fedNwId) {
    logger.debug("");
    if (fedNwId == null) {
      return;
    }
    List<NetworkInterface> orgNwIfs = new ArrayList<>();
    NetworkInterface fedNwIf = null;
    if (orgNwIds != null) {
      for (String id : orgNwIds) {
        orgNwIfs.add(networkInterfaces().get(id));
      }
    }
    fedNwIf = networkInterfaces().get(fedNwId);
    fedNwIf.deleteAllFlow();
    fedNwIf.deleteTopology();
    originalNwAndBoundaryLinkMap.clear();

    for (NetworkInterface nwIf : orgNwIfs) {
      nwIf.deleteAllFlow();
    }

    // reset conversionTable.
    conversionTable().getFlow().clear();
    conversionTable().getLink().clear();
    conversionTable().getPort().clear();
    conversionTable().getNode().clear();
  }

  protected void postOutPacketNoBroadcast(
      String networkId, OutPacket outPacket) {
    logger.debug("");

    String nodeId = outPacket.getNodeId();
    List<String> fedPorts = new ArrayList<>(outPacket.getPorts());
    Map<String, List<Port>> nwPortsMap = new HashMap<>();
    for (String outPortId : fedPorts) {
      // get conversion port.
      List<String> orgPorts = conversionTable().getPort(
          networkId, nodeId, outPortId);
      if (orgPorts.size() == 0) {
        continue;
      }
      String[] orgPortList = orgPorts.get(0).split(SEPARATOR);
      String orgNwId = orgPortList[0];
      Port orgPort = new Port(orgPortList[2], orgPortList[1]);
      if (nwPortsMap.containsKey(orgNwId)) {
        nwPortsMap.get(orgNwId).add(orgPort);
      } else {
        List<Port> newPorts = new ArrayList<>();
        newPorts.add(orgPort);
        nwPortsMap.put(orgNwId, newPorts);
      }
    }

    // Convert node
    if (outPacket.getNodeId() == null) {
      return;
    }
    String preNodeId = outPacket.getNodeId();
    ArrayList<String> convNodeId =
        conversionTable().getNode(networkId, preNodeId);
    if (convNodeId.size() == 0) {
      return;
    }
    String[] nlist = convNodeId.get(0).split("::");
    outPacket.setNodeId(nlist[1]);

    for (String orgNwId : nwPortsMap.keySet()) {
      // convert outPost.
      for (Port orgPort : nwPortsMap.get(orgNwId)) {
        List<String> portIds = new ArrayList<>();
        portIds.add(orgPort.getId());

        outPacket.setPorts(portIds);
        // post outpacket
        networkInterfaces().get(orgNwId).postOutPacket(outPacket);
      }
    }
    return;
  }

  protected void postOutPacketBroadcast(
      String networkId, String srcNetworkId, OutPacket outPacket) {
    logger.debug("");

    String nodeId = outPacket.getNodeId();
    List<String> exceptPorts = new ArrayList<>(outPacket.getExceptPorts());
    Map<String, List<Port>> nwExPortsMap = new HashMap<>();
    for (String exPortId : exceptPorts) {
      // get conversion port.
      List<String> orgPorts = conversionTable().getPort(
          networkId, nodeId, exPortId);
      if (orgPorts.size() == 0) {
        continue;
      }
      String[] orgPortList = orgPorts.get(0).split(SEPARATOR);
      String orgNwId = orgPortList[0];
      Port orgPort = new Port(orgPortList[2], orgPortList[1]);
      if (nwExPortsMap.containsKey(orgNwId)) {
        nwExPortsMap.get(orgNwId).add(orgPort);
      } else {
        List<Port> newPorts = new ArrayList<>();
        newPorts.add(orgPort);
        nwExPortsMap.put(orgNwId, newPorts);
      }
    }

    List<String> orgNwIds = conversionTable().getConnectionList(
        ORIGINAL_NETWORK);
    for (String orgNwId : orgNwIds) {
      List<Port> exPorts = nwExPortsMap.get(orgNwId);
      if (exPorts == null) {
        exPorts = new ArrayList<>();
      }

      List<String> exPortIds = new ArrayList<>();
      for (Port orgPort : exPorts) {
        outPacket.setNodeId(orgPort.getNode());
        exPortIds.add(orgPort.getId());
      }

      // check boundary port.
      if (srcNetworkId != null && !srcNetworkId.equals(orgNwId)) {
        Map<String, Node> nodes =
            networkInterfaces().get(orgNwId).getNodes();
        for (String orgNodeId : nodes.keySet()) {
          Map<String, Port> ports = nodes.get(orgNodeId).getPortMap();
          for (String orgPortId : ports.keySet()) {
            if (federatorBoundaryTable.isContainsLink(
                orgNwId, orgNodeId, orgPortId)) {
              exPortIds.add(orgPortId);
            }
          }
        }
      }

      outPacket.setExceptPorts(exPortIds);
      // post outpacket
      networkInterfaces().get(orgNwId).postOutPacket(outPacket);
    }
  }

  /**
   *
   * @param networkId ID for the network.
   * @return ID for the federated network.
   */
  protected String getConvNetworkId(final String networkId) {
    logger.debug("");

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

  /**
   *
   * @param boundary Registered the boundary
   * @return true: Addition succeeded. false: Addition is failed.
   */
  protected boolean addBoundaryLink(FederatorBoundary boundary) {
    logger.debug("");

    if (boundary == null || !boundary.validate()) {
      logger.warn("invalid boundary.");
      return false;
    }
    NetworkInterface nwIf1 = networkInterfaces()
        .get(boundary.getNetwork1());
    NetworkInterface nwIf2 = networkInterfaces()
        .get(boundary.getNetwork2());
    if (nwIf1 == null || nwIf2 == null) {
      logger.warn("invalid boundary.");
      return false;
    }
    Port port1 = nwIf1.getPort(boundary.getNode1(), boundary.getPort1());
    Port port2 = nwIf2.getPort(boundary.getNode2(), boundary.getPort2());
    if (port1 == null || port2 == null) {
      logger.warn("not exsist boundary's port.");
      return false;
    }

    // update original_nw's port attributes{"is_boundary":true}.
    doPutAttributeBoundaryPort(nwIf1, port1);
    doPutAttributeBoundaryPort(nwIf2, port2);

    logger.info("update boundary port attributes.");

    String fedNwId = getNetworkIdByType(FEDERATED_NETWORK);
    if (fedNwId == null) {
      logger.warn("is not connected to federated network.");
      return false;
    }
    List<String> fedPorts1 = conversionTable().getPort(
        nwIf1.getNetworkId(), port1.getNode(), port1.getId());
    List<String> fedPorts2 = conversionTable().getPort(
        nwIf2.getNetworkId(), port2.getNode(), port2.getId());
    if (fedPorts1.size() == 0 || fedPorts2.size() == 0) {
      return false;
    }
    String[] fedPortList1 = fedPorts1.get(0).split(SEPARATOR);
    String[] fedPortList2 = fedPorts2.get(0).split(SEPARATOR);

    String fedLinkId1 = String.format("%s_link01", boundary.getId());
    String fedLinkId2 = String.format("%s_link02", boundary.getId());

    Link fedLink1 = new Link(
        fedLinkId1, fedPortList1[1], fedPortList1[2], fedPortList2[1],
        fedPortList2[2]);
    Port src_port1
      = networkInterfaces().get(fedNwId).getPort(fedPortList1[1], fedPortList1[2]);
    addBoundaryLinkAttribute(fedLink1, src_port1);

    Link fedLink2 = new Link(
        fedLinkId2, fedPortList2[1], fedPortList2[2], fedPortList1[1],
        fedPortList1[2]);
    Port src_port2
      = networkInterfaces().get(fedNwId).getPort(fedPortList2[1], fedPortList2[2]);
    addBoundaryLinkAttribute(fedLink2, src_port2);

    // PUT Federated's Link
    Response resp = networkInterfaces().get(fedNwId).putLink(fedLink1);
    if (resp.isError(Request.Method.PUT.name())) {
      return false;
    }
    resp = networkInterfaces().get(fedNwId).putLink(fedLink2);
    if (resp.isError(Request.Method.PUT.name())) {
      networkInterfaces().get(fedNwId).delLink(fedLinkId1);
      return false;
    }

    if (originalNwAndBoundaryLinkMap.containsKey(nwIf1.getNetworkId())) {
      originalNwAndBoundaryLinkMap.get(nwIf1.getNetworkId()).add(
          fedLinkId1);
      originalNwAndBoundaryLinkMap.get(nwIf1.getNetworkId()).add(
          fedLinkId2);
    } else {
      List<String> newLinks = new ArrayList<>();
      newLinks.add(fedLinkId1);
      newLinks.add(fedLinkId2);
      originalNwAndBoundaryLinkMap.put(nwIf1.getNetworkId(), newLinks);
    }

    if (originalNwAndBoundaryLinkMap.containsKey(nwIf2.getNetworkId())) {
      originalNwAndBoundaryLinkMap.get(nwIf2.getNetworkId()).add(
          fedLinkId1);
      originalNwAndBoundaryLinkMap.get(nwIf2.getNetworkId()).add(
          fedLinkId2);
    } else {
      List<String> newLinks = new ArrayList<>();
      newLinks.add(fedLinkId1);
      newLinks.add(fedLinkId2);
      originalNwAndBoundaryLinkMap.put(nwIf2.getNetworkId(), newLinks);
    }

    return true;
  }


  /**
   *
   * @param link boundary Link.
   * @param srcPort boundary src Port
   */
  protected void addBoundaryLinkAttribute(Link link, Port srcPort) {
    link.putAttribute(Logic.AttrElements.OPER_STATUS,
                      srcPort.getAttribute(Logic.AttrElements.OPER_STATUS));
    link.putAttribute(Logic.AttrElements.COST, "1");
    link.putAttribute(Logic.AttrElements.MAX_BANDWIDTH,
                      srcPort.getAttribute(Logic.AttrElements.MAX_BANDWIDTH));
    link.putAttribute(Logic.AttrElements.UNRESERVED_BANDWIDTH,
                      srcPort.getAttribute(Logic.AttrElements.UNRESERVED_BANDWIDTH));

  }

  /**
   *
   * @param boundaryId Deleted the boundary.
   * @return true: Delete succeeded. false: Delete is failed .
   */
  protected boolean deleteBoundaryLink(String boundaryId) {
    logger.debug("");

    String fedNwId = getNetworkIdByType(FEDERATED_NETWORK);
    if (fedNwId == null) {
      logger.warn("is not connected to federated network.");
      return false;
    }
    FederatorBoundary boundary =
        federatorBoundaryTable.getBoundaries().get(boundaryId);
    Map<String, Link> links = networkInterfaces().get(fedNwId).getLinks();
    for (String linkId : links.keySet()) {
      Link link = links.get(linkId);
      if (!link.validate()) {
        continue;
      }
      ArrayList<String> orgPorts1 = conversionTable().getPort(
          fedNwId, link.getSrcNode(), link.getSrcPort());
      ArrayList<String> orgPorts2 = conversionTable().getPort(
          fedNwId, link.getDstNode(), link.getDstPort());
      if (orgPorts1.size() == 0 || orgPorts2.size() == 0) {
        continue;
      }
      String[] orgPortList1 = orgPorts1.get(0).split(SEPARATOR);
      String[] orgPortList2 = orgPorts2.get(0).split(SEPARATOR);
      if (boundary.isContains(orgPortList1[0], orgPortList1[1],
          orgPortList1[2])
          && boundary.isContains(orgPortList2[0], orgPortList2[1],
              orgPortList2[2])) {
        // DELETE Federated's Link
        networkInterfaces().get(fedNwId).delLink(linkId);
        // Update boundaryLink map.
        for (List<String> boundaryLinks : originalNwAndBoundaryLinkMap.values()) {
          boundaryLinks.remove(linkId);
        }
      }
    }
    return true;
  }

  /**
   *
   * @param networkId ID for network.
   * @param nodeId ID for node in the network.
   * @param portId ID for port in the node.
   * @return true: Delete succeeded. false: Delete is failed .
   */
  protected boolean deleteBoundaryLink(String networkId, String nodeId,
      String portId) {
    logger.debug("");

    if (networkId == null || nodeId == null || portId == null) {
      return false;
    }
    Map<String, FederatorBoundary> boundarys =
        federatorBoundaryTable.getBoundaries();
    for (String id : boundarys.keySet()) {
      if (boundarys.get(id).isContains(networkId, nodeId, portId)) {
        deleteBoundaryLink(id);
      }
    }
    return true;
  }

  /**
   *
   * @param connType Type of the network.
   * @return map of the nodes.
   */
  protected Map<String, String> getNodes(String connType) {
    logger.debug("");

    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(connType);

    Map<String, String> nwcNodes = new HashMap<>();
    for (String nwcId : nwcIds) {
      NetworkInterface nwif = networkInterfaces().get(nwcId);

      Map<String, Node> nodes = nwif.getNodes();
      Set<String> nodeIds = nodes.keySet();
      for (String nodeId : nodeIds) {
        ArrayList<String> pairNodes =
            conversionTable().getNode(nwcId, nodeId);
        if ((pairNodes == null) || (pairNodes.size()) == 0) {
          continue;
        }

        String key = String.format(NODE_FORMAT, nwcId, nodeId);
        nwcNodes.put(key, pairNodes.get(0));
      }
    }
    return nwcNodes;

  }

  /**
   *
   * @return Map of nodes.
   * {@code
   * dict<federated_node_id, (original_network_id, original_node_id)>
   * }
   */
  protected Map<String, ArrayList<String>> getNwNodesOrigin() {

    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(FEDERATED_NETWORK);
    HashMap<String, ArrayList<String>>
        nodeTable = conversionTable().getNode();

    Map<String, ArrayList<String>> nwcNodes = new HashMap<>();
    for(String key : nodeTable.keySet()) {
        String[] fedNode = key.split("::");  // nwcId::nodeId
        if(nwcIds.indexOf(fedNode[0]) < 0 ) {
          continue;
        }
        ArrayList<String> orgNodes = nodeTable.get(key);
        String[] orgNode = orgNodes.get(0).split("::");  // nwcId::nodeId
        nwcNodes.put(fedNode[1], new ArrayList<>(Arrays.asList(orgNode)));
    }
    return nwcNodes;
  }

  /**
   *
   * @return map of the nodes.
   * {@code
   * dict<original_network_id::original_node_id, federated_node_id>
   * }
   */
  protected Map<String, String> getNwNodesFed() {
    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(ORIGINAL_NETWORK);
    HashMap<String, ArrayList<String>>
        nodeTable = conversionTable().getNode();

    Map<String, String> nwcNodes = new HashMap<>();
    for(String orgNode : nodeTable.keySet()) {
        String[] nwcId = orgNode.split("::");  // nwcId::nodeId
        if(nwcIds.indexOf(nwcId[0]) < 0 ) {
          continue;
        }
        ArrayList<String> fedNodes = nodeTable.get(orgNode);
        String[] fedNode = fedNodes.get(0).split("::");  // nwcId::nodeId
        nwcNodes.put(orgNode, fedNode[1]);
    }
    return nwcNodes;
  }

  /**
   *
   * @param connType Type of the network.
   * @return map of the ports.
   */
  protected Map<String, String> getPorts(String connType) {
    logger.debug("");

    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(connType);

    Map<String, String> nwcPorts = new HashMap<>();
    for (String nwcId : nwcIds) {
      NetworkInterface nwif = networkInterfaces().get(nwcId);

      Map<String, Node> nodes = nwif.getNodes();
      Set<String> nodeIds = nodes.keySet();
      for (String nodeId : nodeIds) {

        ArrayList<String> pairNodes =
            conversionTable().getNode(nwcId, nodeId);
        if ((pairNodes == null) || (pairNodes.size() == 0)) {
          continue;
        }

        String pairNode = pairNodes.get(0);

        Map<String, Port> ports = nwif.getPorts(nodeId);
        Set<String> portIds = ports.keySet();
        for (String portId : portIds) {
          ArrayList<String> pairPorts =
              conversionTable().getPort(nwcId, nodeId, portId);
          if ((pairPorts == null) || (pairPorts.size() == 0)) {
            continue;
          }

          String key = String.format(PORT_FORMAT,
              nwcId, nodeId, portId);
          String value = String.format("%s::%s",
              pairNode, pairPorts.get(0));
          nwcPorts.put(key, value);
        }
      }
    }
    return nwcPorts;

  }

  /**
   *
   * @return Map of ports.
   * {@code
   * dict<federated_node_id::federated_port_id, (org_network_id, org_node_id, org_port_id)>
   * }
   */
  protected Map<String, ArrayList<String>> getNwPortsOrigin() {

    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(FEDERATED_NETWORK);
    HashMap<String, ArrayList<String>>
        portTable = conversionTable().getPort();


    Map<String, ArrayList<String>> nwcPorts = new HashMap<>();
    for(String key : portTable.keySet()) {
        String[] fedPort = key.split("::");  // nwcId::nodeId::portId
        if(nwcIds.indexOf(fedPort[0]) < 0 ) {
          continue;
        }
        ArrayList<String> orgPorts = portTable.get(key);
        String[] orgPort = orgPorts.get(0).split("::");  // nwcId::nodeId::portId
        nwcPorts.put(fedPort[1]+"::"+fedPort[2],
                     new ArrayList<>(Arrays.asList(orgPort)));
    }
    return nwcPorts;
  }

 /**
   *
   * @return map of the ports.
   * {@code
   * dict<org_nwid::org_node_id::org_port_id, list[federated_node_id, federated_port_id]>
   * }
   */
  protected Map<String, ArrayList<String>> getNwPortsFed() {
    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(ORIGINAL_NETWORK);
    HashMap<String, ArrayList<String>>
        portTable = conversionTable().getPort();

    Map<String, ArrayList<String>> nwcPorts = new HashMap<>();
    for(String orgPort : portTable.keySet()) {
        String[] nwcId = orgPort.split("::");  // nwcId::nodeId::portId
        if(nwcIds.indexOf(nwcId[0]) < 0 ) {
          continue;
        }
        ArrayList<String> fedPorts = portTable.get(orgPort);
        String[] fedPortStr = fedPorts.get(0).split("::");  // nwcId::nodeId::portId
        ArrayList<String> fedPort = new ArrayList<>(Arrays.asList(fedPortStr));
        fedPort.remove(0);
        nwcPorts.put(orgPort, fedPort);
    }
    return nwcPorts;
  }

  /**
   *
   * @param connType Type of the network.
   * @return Map of links.
   */
  protected Map<String, String> getLinks(String connType) {
    logger.debug("");

    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(connType);

    Map<String, String> nwcLinks = new HashMap<>();
    for (String nwcId : nwcIds) {
      NetworkInterface nwif = networkInterfaces().get(nwcId);

      Map<String, Link> links = nwif.getLinks();
      Set<String> linkIds = links.keySet();
      for (String linkId : linkIds) {
        ArrayList<String> pairLinks =
            conversionTable().getLink(nwcId, linkId);
        if ((pairLinks == null) || (pairLinks.size() == 0)) {
          continue;
        }
        String key = String.format(LINK_FORMAT, nwcId, linkId);
        nwcLinks.put(key, pairLinks.get(0));
      }
    }
    return nwcLinks;

  }

  /**
   *
   * @return Map of links. {@code dict<federated_link_id, list[(original_network_id, original_link_id)]>}
   */
  protected Map<String, ArrayList<String>> getNwLinksOrigin() {

    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(FEDERATED_NETWORK);
    HashMap<String, ArrayList<String>>
        linkTable = conversionTable().getLink();

    Map<String, ArrayList<String>> nwcLinks = new HashMap<>();
    for(String key : linkTable.keySet()) {
        String[] fedLink = key.split("::");  // nwcId::linkId
        if(nwcIds.indexOf(fedLink[0]) < 0 ) {
          continue;
        }
        ArrayList<String> orgLinks = linkTable.get(key);
        String[] orgLink = orgLinks.get(0).split("::");  // nwcId::linkId
        nwcLinks.put(fedLink[1], new ArrayList<>(Arrays.asList(orgLink)));
    }
    return nwcLinks;
  }

  /**
   *
   * @return Map of links. {@code dict<original_network_id::original_link_id, federated_link_id>}
   */
  protected Map<String, String> getNwLinksFed() {
    logger.debug("");

    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(ORIGINAL_NETWORK);
    HashMap<String, ArrayList<String>>
        linkTable = conversionTable().getLink();

    Map<String, String> nwcLinks = new HashMap<>();
    for(String orgLink : linkTable.keySet()) {
        String[] nwcId = orgLink.split("::");  // nwcId::linkId
        if(nwcIds.indexOf(nwcId[0]) < 0 ) {
          continue;
        }
        ArrayList<String> fedLinks = linkTable.get(orgLink);
        String[] fedLink = fedLinks.get(0).split("::");  // nwcId::linkId
        nwcLinks.put(orgLink, fedLink[1]);
    }
    return nwcLinks;
  }

  /**
   *
   * @param connType Type of the network.
   * @return Map of flows.
   */
  protected Map<String, String> getFlows(String connType) {
    logger.debug("");

    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(connType);

    Map<String, String> nwcFlows = new HashMap<>();
    for (String nwcId : nwcIds) {
      NetworkInterface nwif = networkInterfaces().get(nwcId);

      FlowSet flowset = nwif.getFlowSet();
      Set<String> flowIds = flowset.flows.keySet();
      for (String flowId : flowIds) {
        ArrayList<String> pairFlows =
            conversionTable().getFlow(nwcId, flowId);
        if ((pairFlows == null) || (pairFlows.size()) == 0) {
          continue;
        }
        String key = String.format(FLOW_FORMAT, nwcId, flowId);
        nwcFlows.put(key, pairFlows.get(0));
      }
    }
    return nwcFlows;
  }

  /**
   *
   * @return Map of Flows. {@code dict<federated_flow_id, [(original_network_id, original_flow_id)]>}
   */
  protected Map<String, ArrayList<Object>> getNwFlowsOrigin() {
    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(FEDERATED_NETWORK);
    HashMap<String, ArrayList<String>>
        flowTable = conversionTable().getFlow();

    Map<String, ArrayList<Object>> nwcFlows = new HashMap<>();
    for(String key : flowTable.keySet()) {
        String[] fedFlow = key.split("::");  // nwcId::flowId
        if(nwcIds.indexOf(fedFlow[0]) < 0 ) {
          continue;
        }
        ArrayList<String> orgFlows = flowTable.get(key);
        ArrayList<Object> orgFlowArray = new ArrayList<>();
        for(String orgFlowStr : orgFlows) {
          String[] orgFlow = orgFlowStr.split("::");  // nwcId::flowId
          orgFlowArray.add(new ArrayList<>(Arrays.asList(orgFlow)));
        }
        nwcFlows.put(fedFlow[1], orgFlowArray);
    }
    return nwcFlows;
  }


  /**
   *
   * @return Map of flows. {@code dict<original_network_id::original_flow_id, federated_flow_id>}
   */
  protected Map<String, String> getNwFlowsFed() {
    logger.debug("");
    ArrayList<String> nwcIds =
        conversionTable().getConnectionList(ORIGINAL_NETWORK);
    HashMap<String, ArrayList<String>>
        flowTable = conversionTable().getFlow();

    Map<String, String> nwcFlows = new HashMap<>();
    for(String orgFlow : flowTable.keySet()) {
        String[] nwcId = orgFlow.split("::");  // nwcId::flowId
        if(nwcIds.indexOf(nwcId[0]) < 0 ) {
          continue;
        }
        ArrayList<String> fedFlows = flowTable.get(orgFlow);
        String[] fedFlow = fedFlows.get(0).split("::");  // nwcId::flowId
        nwcFlows.put(orgFlow, fedFlow[1]);
    }
    return nwcFlows;
  }

  protected final boolean doPutAttributeBoundaryPort(
      NetworkInterface nwIf, Port port) {
    logger.debug("");

    if (port == null) {
      return false;
    }
    String isBoudary = port.getAttribute(Logic.AttrElements.IS_BOUNDARY);
    if (isBoudary != null && Boolean.valueOf(isBoudary)) {
      logger.info("already updated. port.attributes{'is_boundary':'true'}.");
      return false;
    }
    port.putAttribute(Logic.AttrElements.IS_BOUNDARY, "true");
    nwIf.putPort(port);
    return true;
  }

  protected final void doDelAttributeBoundaryPort(
      FederatorBoundary boundary) {
    logger.debug("");

    // boundary's network1, node1, port1
    NetworkInterface nwIf1 = networkInterfaces().get(boundary.getNetwork1());
    if (nwIf1 != null
        && boundary.getNode1() != null
        && boundary.getPort1() != null) {
      Port port1 = nwIf1.getPort(boundary.getNode1(), boundary.getPort1());

      if (port1 != null) {
        String isBoudary = port1.getAttribute(Logic.AttrElements.IS_BOUNDARY);
        if (isBoudary != null && Boolean.valueOf(isBoudary)) {
          port1.deleteAttribute(Logic.AttrElements.IS_BOUNDARY);
          nwIf1.putPort(port1);
        }
      }
    }

    // boundary's network2, node2, port2
    NetworkInterface nwIf2 = networkInterfaces().get(boundary.getNetwork2());
    if (nwIf2 != null
        && boundary.getNode2() != null
        && boundary.getPort2() != null) {
      Port port2 = nwIf2.getPort(boundary.getNode2(), boundary.getPort2());

      if (port2 != null) {
        String isBoudary = port2.getAttribute(Logic.AttrElements.IS_BOUNDARY);
        if (isBoudary != null && Boolean.valueOf(isBoudary)) {
          port2.deleteAttribute(Logic.AttrElements.IS_BOUNDARY);
          nwIf2.putPort(port2);
        }
      }
    }

  }

  protected final void verifyType(String type) throws FederatorException {
    logger.debug("");

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(type)) {
      throw new FederatorException();
    }
  }

  protected final void verifyId(ComponentConnection curr) throws FederatorException {
    logger.debug("");

    String logicId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    String id = getObjectId();

    if (!id.equals(logicId)) {
      throw new FederatorException();
    }

  }

}
