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

import org.o3project.odenos.core.component.Component;
import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowChanged;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.FlowQueryFactory;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.query.FlowQuery;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.InPacketQuery;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacketQuery;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.packet.PacketObject;
import org.o3project.odenos.core.component.network.packet.PacketQuery;
import org.o3project.odenos.core.component.network.packet.PacketQueue;
import org.o3project.odenos.core.component.network.packet.PacketQueueSet;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.LinkChanged;
import org.o3project.odenos.core.component.network.topology.LinkQuery;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.NodeChanged;
import org.o3project.odenos.core.component.network.topology.NodeQuery;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.PortChanged;
import org.o3project.odenos.core.component.network.topology.PortQuery;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.core.component.network.topology.TopologyChanged;
import org.o3project.odenos.remoteobject.ObjectSettings;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.Map;
import java.util.HashMap;

/**
 * NetworkComponent manages network topology and flows in accordance with
 * network abstraction model. This class handles other components' requests to
 * modify topology and flows. NetworkComponent also manages packet event
 * (packet_in and packet_out) by queuing those events. Other component can
 * enqueue and dequeue packet event and process them.
 *
 */
public class Network extends Component {
  private static final Logger log = LogManager.getLogger(Network.class);

  /**
   * Request parser to parse Request object to decide which action to be
   * executed.
   */
  private RequestParser<IActionCallback> parser;

  private Topology topology;
  private FlowSet flowset;
  private Map<String,String> deletingFlow = new HashMap<>();
  private PacketQueueSet packetQueue;

  public static final String PROPERTY_KEY_FLOW_TYPE = "flow_type";

  protected NetworkObjectSettings objectSettings = new NetworkObjectSettings();

  /**
   * NetworkObjectSettings class.
   *
   */
  private class NetworkObjectSettings extends ObjectSettings {

    public static final String KEY_VERBOSE_PORT_EVENT = "verbose_event/port";
    public static final String KEY_VERBOSE_LINK_EVENT = "verbose_event/link";

    private boolean verbosePortEvent = true;
    private boolean verboseLinkEvent = true;

    @Override
    public String setSetting(String key, String value) {

      if (key.equals(KEY_VERBOSE_PORT_EVENT)) {
        if (value != null
            && (value.toLowerCase().equals("true") || value
                .toLowerCase().equals(
                    "false"))) {
          verbosePortEvent = Boolean.valueOf(value);
        } else {
          log.error("{} is wrong as a value of a key {}. expect true or false.",
              value, key);
        }
      }

      if (key.equals(KEY_VERBOSE_LINK_EVENT)) {
        if (value != null
            && (value.toLowerCase().equals("true") || value
                .toLowerCase().equals(
                    "false"))) {
          verboseLinkEvent = Boolean.valueOf(value);
        } else {
          log.error("{} is wrong as a value of a key {}. expect true or false.",
              value, key);
        }
      }

      return super.setSetting(key, value);
    }

    public boolean isVerbosePortEvent() {
      return verbosePortEvent;
    }

    public boolean isVerboseLinkEvent() {
      return verboseLinkEvent;
    }
  }

  /**
   * Constructor.
   * @param objectId ID for object.
   * @param dispatcher MessageDispatcher instance.
   */
  public Network(String objectId,
      MessageDispatcher dispatcher) {
    super(objectId, dispatcher);
    topology = new Topology();
    flowset = new FlowSet();
    packetQueue = new PacketQueueSet();
    parser = createParser();
    objectProperty.setProperty(PROPERTY_KEY_FLOW_TYPE, "BasicFlow");
  }

  /**
   * Get Super Type of Component.
   *
   * @return Super Type of Component
   */
  @Override
  protected String getSuperType() {
    return Network.class.getSimpleName();
  }

  /**
   * Description of Component.
   */
  private static final String DESCRIPTION = "Network Component";

  /**
   * Get Description of Component.
   *
   * @return Description
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public ObjectSettings getSettings() {
    return objectSettings;
  }

  @Override
  public Response onRequest(Request request) {
    log.debug("");
    Response res;

    try {
      log.debug("Received request : {}, {} {}", getObjectId(), request.method, request.path);
      log.debug("Received body    : {}, {}", getObjectId(), request.getBodyValue());
      RequestParser<IActionCallback>.ParsedRequest parsed = parser
          .parse(request);
      IActionCallback callback = parsed.getResult();
      res = callback.process(parsed);
      return res;
    } catch (Exception e) {
      log.error("Exception in onRequest() : [case:{}] [msg:{}]",
          request.path, e.getClass().getSimpleName());
      res = createErrorResponse(Response.BAD_REQUEST,
          "Error while processing : [" + request.method + "] "
              + request.path);
      return res;
    }
  }

  // ******************* Actions about settings *******************
  protected Response getSettingVerbosePort() {
    log.debug("");
    return new Response(Response.OK,
        getSettings().getSetting(
            NetworkObjectSettings.KEY_VERBOSE_PORT_EVENT));
  }

  protected Response putSettingVerbosePort(String value) {
    log.debug("");
    objectSettings.setSetting(NetworkObjectSettings.KEY_VERBOSE_PORT_EVENT,
        value);
    return new Response(Response.OK, value);
  }

  protected Response getSettingVerboseLink() {
    log.debug("");
    return new Response(Response.OK,
        getSettings().getSetting(
            NetworkObjectSettings.KEY_VERBOSE_LINK_EVENT));
  }

  protected Response putSettingVerboseLink(String value) {
    log.debug("");
    objectSettings.setSetting(NetworkObjectSettings.KEY_VERBOSE_LINK_EVENT,
        value);
    return new Response(Response.OK, value);
  }

  // ******************* Actions about topology *******************
  protected Response getTopology() {
    log.debug("");
    return new Response(Response.OK, topology);
  }

  protected Response putTopology(Topology newTopology) throws Exception {
    log.debug("");
    if (newTopology.getVersion() != null
        && !topology.getVersion().equals(newTopology.getVersion())) {
      return createErrorResponse(Response.CONFLICT, topology);
    }

    /**
     * check new topology.
     */
    if (!newTopology.validate()) {
      return createErrorResponse(Response.BAD_REQUEST, String.format(
          "Invalid Topology Format. [invalid topology: %s]", newTopology));
    }
    Map<String, Link> newLinks = newTopology.getLinkMap();
    Map<String, Node> newNodes = newTopology.getNodeMap();
    for (Link link : newLinks.values()) {
      if (link == null || !link.validate()) {
        return createErrorResponse(Response.BAD_REQUEST, String.format(
            "Invalid Topology's link Format. [invalid link: %s]", link));
      }
    }
    for (Node node : newNodes.values()) {
      if (node == null || !node.validate()) {
        return createErrorResponse(Response.BAD_REQUEST, String.format(
            "Invalid Topology's node Format. [invalid node: %s]", node));
      }
      for (Port port : node.getPortMap().values()) {
        if (port == null || !port.validate()) {
          return createErrorResponse(Response.BAD_REQUEST, String.format(
              "Invalid Topology's port Format. [invalid port: %s]", port));
        }
      }
    }

    Topology oldTopology = topology;
    // clear current topology and reconstruct
    topology = newTopology.clone();

    if (oldTopology == null) {
      notifyTopologyChangedToAdd(newTopology);
    } else {
      notifyTopologyChangedToUpdate(oldTopology, newTopology);
    }

    // TODO 
    /*
    Topology backupTopology = topology.clone();
    Topology oldTopology = topology.clone();
    // old info
    Map<String, Link> oldLinks = oldTopology.getLinkMap();
    Map<String, Node> oldNodes = oldTopology.getNodeMap();
    Response resp;

    // delete old links, nodes, ports.
    for (String linkId : oldLinks.keySet()) {
      if (!newLinks.containsKey(linkId)) {
        resp = this.deleteLink(linkId, oldLinks.get(linkId));
        if (resp.isError("DELETE")) {
          this.topology = backupTopology; // rollback
          return resp;
        }
      }
    }
    for (String nodeId : oldNodes.keySet()) {
      if (!newNodes.containsKey(nodeId)) {
        resp = this.deleteNode(nodeId, oldNodes.get(nodeId));
        if (resp.isError("DELETE")) {
          this.topology = backupTopology; // rollback
          return resp;
        }
      }
    }

    // add or update nodes, ports, links.
    for (String nodeId : newNodes.keySet()) {
      Node newNode = newNodes.get(nodeId);
      Node oldNode = oldNodes.get(nodeId);
      if (oldNode != null) {
        newNode.setVersion(oldNode.getVersion());
      }
      resp = this.putNode(nodeId, newNode);
      if (resp.isError("PUT")) {
        this.topology = backupTopology; // rollback
        return resp;
      }
    }
    for (String linkId : newLinks.keySet()) {
      Link newLink = newLinks.get(linkId);
      Link oldLink = oldLinks.get(linkId);
      if (oldLink != null) {
        newLink.setVersion(oldLink.getVersion());
      }
      resp = this.putLink(linkId, newLink);
      if (resp.isError("PUT")) {
        this.topology = backupTopology; // rollback
        return resp;
      }
    }
    */

    return new Response(Response.OK, newTopology);
  }

  // ******************* Actions about node *******************
  protected Response postNode(Node msg) throws Exception {
    log.debug("");

    // forced to auto-number
    if (msg != null) {
      msg.setId(null);
    }
    Node node = topology.createNode(msg);
    if (node == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "not compatible object");
    }

    if(!node.isAttribute(AttrElements.OPER_STATUS)) {
      node.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
    }

    notifyNodeChanged(null, node, NodeChanged.Action.add);

    return new Response(Response.OK, node);
  }

  protected Response getNodes(boolean hasQuery, String queriesString) {
    log.debug("");
    if (hasQuery) {
      NodeQuery query = new NodeQuery(queriesString);
      if (!query.parse()) {
        return createErrorResponse(Response.BAD_REQUEST,
            "Query is invalid.");
      }
      return new Response(Response.OK, topology.getNodeMessages(query));
    } else {
      return new Response(Response.OK, topology.getNodeMap());
    }
  }

  protected Response getNode(String nodeId) {
    log.debug("");
    Node node = topology.getNode(nodeId);

    if (node == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "node_id not found");
    }

    return new Response(Response.OK, node);
  }

  protected Response putNode(String nodeId, Node msg) throws Exception {
    log.debug("");

    if (msg == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "invalid request's body.");
    }

    msg.setId(nodeId);
    if (!msg.validate()) {
      return createErrorResponse(Response.BAD_REQUEST, "invalid nodeId.");
    }

    Node nodeOld;
    Node node = topology.getNode(nodeId);

    NodeChanged.Action action;
    Integer returnCode = Response.OK;
    msg.setId(nodeId);
    if (node == null) {
      nodeOld = null;
      node = topology.createNode(msg);
      if(!node.isAttribute(AttrElements.OPER_STATUS)) {
        node.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
      }
      action = NodeChanged.Action.add;
      returnCode = Response.CREATED;
    } else {
      // version conflict
      if (msg.getVersion() != null
          && !msg.getVersion().equals(node.getVersion())) {
        return createErrorResponse(Response.CONFLICT, node);
      }
      nodeOld = node.clone();
      if (msg.equals(nodeOld)) {
        return new Response(Response.OK, node);
      }
      node = topology.createNode(msg);
      action = NodeChanged.Action.update;
      returnCode = Response.OK;
    }

    if (node == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "not compatible object");
    }

    notifyNodeChanged(nodeOld, node.clone(), action);

    return new Response(returnCode, node);
  }

  protected Response putNodeAttributes(String nodeId, Map<String, String> addAttributes) throws Exception {
    Node node = topology.getNode(nodeId);
    Node nodeOld = node.clone();

    // attributes copy (curr -> body)
    for (String key : addAttributes.keySet()) {
        node.putAttribute(key, addAttributes.get(key));
    }

    node.updateVersion();
    notifyNodeChanged(nodeOld, node.clone(), NodeChanged.Action.update);
    return new Response(Response.OK, node);
  }

  protected Response deleteNode(String nodeId, Node msg) throws Exception {
    log.debug("");
    Node node = topology.getNode(nodeId);

    if (node == null) {
      return new Response(Response.OK, null);
    }

    if (msg != null && !node.getVersion().equals(msg.getVersion())) {
      return createErrorResponse(Response.CONFLICT, null,
          "version conflicted.");
    }

    Node nodeOld = node;

    // delete failed
    if (!topology.deleteNode(node)) {
      return createErrorResponse(Response.CONFLICT, node,
          "deletion of node_id:" + nodeId + " failed");
    }

    notifyNodeChanged(nodeOld, null, NodeChanged.Action.delete);

    return new Response(Response.OK, null);
  }

  // ******************* Actions about port *******************
  protected Response postPort(String nodeId, Port msg) throws Exception {
    log.debug("");

    Node node = topology.getNode(nodeId);
    if (node == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "deletion of node_id:" + nodeId + " failed");
    }

    Node nodeOld = node;

    if (msg != null) {
      // forced to auto-number
      msg.setId(null);
    }
    Port port = node.createPort(msg);
    if(!port.isAttribute(AttrElements.OPER_STATUS)) {
       port.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
    }

    if (port == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "not compatible object");
    }

    if (isNeededVerboseNodeEvent()) {
      notifyNodeChanged(nodeOld, node, NodeChanged.Action.update);
    }
    notifyPortChanged(null, port, PortChanged.Action.add);

    return new Response(Response.OK, port);
  }

  protected Response getPorts(boolean hasQuery, String queriesString,
      String nodeId, Port msg) {
    if (hasQuery) {
      PortQuery query = new PortQuery(queriesString);
      if (!query.parse()) {
        return createErrorResponse(Response.BAD_REQUEST,
            "Query is invalid.");
      }
      return new Response(Response.OK, topology.getPortMessages(query,
          nodeId));
    } else {
      Node node = topology.getNode(nodeId);

      if (node == null) {
        return createErrorResponse(Response.NOT_FOUND, null,
            "node_id not found");
      }
      return new Response(Response.OK, node.getPortMap());
    }
  }

  protected Response getPort(String nodeId, String portId) {
    log.debug("");
    Port port = topology.getPort(nodeId, portId);
    if (port == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "node_id not found");
    }

    return new Response(Response.OK, port);
  }

  protected Response putPort(String nodeId, String portId,
      Port msg) throws Exception {
    log.debug("");

    if (msg == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "invalid request's body");
    }

    msg.setNode(nodeId);
    msg.setId(portId);
    if (!msg.validate()) {
      return createErrorResponse(Response.BAD_REQUEST,
          "invalid content id");
    }

    Node node = topology.getNode(nodeId);
    if (node == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "node_id not found");
    }

    Port port = node.getPort(portId);

    
    Node nodeOld = null;
    if (isNeededVerboseNodeEvent()) {
      nodeOld = node.clone();
    }
    Port portOld;

    PortChanged.Action action;
    Integer returnCode = Response.OK;
    msg.setId(portId);
    if (port == null) {
      portOld = null;
      port = node.createPort(msg);
      if(!port.isAttribute(AttrElements.OPER_STATUS)) {
        port.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
      }
      action = PortChanged.Action.add;
      returnCode = Response.CREATED;
    } else {
      // version conflict
      if (msg.getVersion() != null
          && !msg.getVersion().equals(port.getVersion())) {
        return createErrorResponse(Response.CONFLICT, port,
            "version conflict");
      }
      portOld = port.clone();
      if (msg.equals(portOld)) {
        return new Response(Response.OK, port);
      }
      port = node.createPort(msg);
      action = PortChanged.Action.update;
      returnCode = Response.OK;
    }

    if (port == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "not compatible object");
    }

    if (isNeededVerboseNodeEvent()) {
      notifyNodeChanged(nodeOld, node.clone(), NodeChanged.Action.update);
    }
    notifyPortChanged(portOld, port.clone(), action);

    return new Response(returnCode, port);
  }

  protected Response putPortAttributes(String nodeId, String portId,
      Map<String, String> addAttributes) throws Exception {

    Node node = topology.getNode(nodeId);
    if (node == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "node_id not found");
    }

    Port port = node.getPort(portId);
    Port portOld = port.clone();

    // attributes copy (curr -> body)
    for (String key : addAttributes.keySet()) {
      port.putAttribute(key, addAttributes.get(key));
    }

    port.updateVersion();
    node.updateVersion();
    notifyPortChanged(portOld, port.clone(), PortChanged.Action.update);
    return new Response(Response.OK, port);
  }


  protected Response deletePort(String nodeId, String portId,
      Port msg) throws Exception {
    log.debug("");
    Node node = topology.getNode(nodeId);
    if (node == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "node_id not found");
    }
    Port port = node.getPort(portId);
    if (port == null) {
      return new Response(Response.OK, null);
    }

    if (msg != null && !port.getVersion().equals(msg.getVersion())) {
      return createErrorResponse(Response.CONFLICT, null,
          "version conflicted.");
    }

    Node nodeOld = node.clone();
    Port portOld = port;

    // delete failed
    if (!topology.deletePort(nodeId, portId)) {
      return new Response(Response.CONFLICT, port);
    }

    if (isNeededVerboseNodeEvent()) {
      notifyNodeChanged(nodeOld, node.clone(), NodeChanged.Action.update);
    }
    notifyPortChanged(portOld, null, PortChanged.Action.delete);

    return new Response(Response.OK, null);
  }

  // ******************* Actions about physical node/port *******************
  protected Response getNodePhysicalId(String physicalId) {
    log.debug("");

    String nodeId = getNodeByPhysicalId(physicalId);
    return getNode(nodeId);
  }

  protected Response putNodePhysicalId(String physicalId, Node msg)
      throws Exception {
    log.debug("");

    String nodeId = getNodeByPhysicalId(physicalId);
    return putNode(nodeId, msg);
  }

  protected Response putNodePhysicalIdAttributes(String physicalId, Map<String, String> addAttributes)
      throws Exception {
    log.debug("");

    String nodeId = getNodeByPhysicalId(physicalId);
    if (nodeId == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "physical_id not found");
    }

    return putNodeAttributes(nodeId, addAttributes);
  }

  protected Response deleteNodePhysicalId(String physicalId, Node msg)
      throws Exception {
    log.debug("");

    String nodeId = getNodeByPhysicalId(physicalId);
    return deleteNode(nodeId, msg);
  }

  protected Response getPortPhysicalId(String physicalId) {
    log.debug("");

    Port port = getPortByPhysicalId(physicalId);
    if (port == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "port_id not found");
    }
    return getPort(port.getNode(), port.getId());
  }

  protected Response putPortPhysicalId(String physicalId, Port msg)
      throws Exception {
    log.debug("");

    Port port = getPortByPhysicalId(physicalId);
    if (port == null) {
      return putPort(msg.getNode(), msg.getId(), msg);
    }
    return putPort(port.getNode(), port.getId(), msg);
  }

  protected Response putPortPhysicalIdAttributes(String physicalId,
      Map<String, String> addAttributes) throws Exception {
    log.debug("");

    Port port = getPortByPhysicalId(physicalId);
    if (port == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "physical_id not found");
    }

    Node node = topology.getNode(port.getNode());
    return putPortAttributes(node.getId(), port.getId(), addAttributes);
  }

  protected Response deletePortPhysicalId(String physicalId, Port msg)
      throws Exception {
    log.debug("");

    Port port = getPortByPhysicalId(physicalId);
    if (port == null) {
      return new Response(Response.OK, null);
    }
    return deletePort(port.getNode(), port.getId(), msg);
  }

  // ******************* Actions about link *******************
  private String validateLinkMessage(Link msg) {
    if (topology.getNode(msg.getSrcNode()) == null) {
      return "the src node does not exist";
    }
    if (topology.getPort(msg.getSrcNode(), msg.getSrcPort()) == null) {
      return "the src port does not exist";
    }
    if (topology.getNode(msg.getDstNode()) == null) {
      return "the dst node does not exist";
    }
    if (topology.getPort(msg.getDstNode(), msg.getDstPort()) == null) {
      return "the dst port does not exist";
    }

    for (Link link : topology.getLinkMap().values()) {
      if (link.getSrcNode().equals(msg.getSrcNode())
          && link.getSrcPort().equals(msg.getSrcPort())
          && link.getDstNode().equals(msg.getDstNode())
          && link.getDstPort().equals(msg.getDstPort())) {
        return "the link already exists";
      }
    }

    return null;
  }

  protected Response postLink(Link msg) throws Exception {
    log.debug("");
    String err = validateLinkMessage(msg);
    if (err != null) {
      return createErrorResponse(Response.BAD_REQUEST, null, err);
    }

    Node srcNodePrev = topology.getNode(msg.getSrcNode()).clone();
    Node dstNodePrev = topology.getNode(msg.getDstNode()).clone();
    Port srcPortPrev = topology.getPort(msg.getSrcNode(), msg.getSrcPort()).clone();
    Port dstPortPrev = topology.getPort(msg.getDstNode(), msg.getDstPort()).clone();

    // forced to auto-number
    msg.setId(null);
    Link link = topology.createLink(msg);
    if (link == null) {
      return createErrorResponse(Response.BAD_REQUEST, null,
          "not compatible object");
    }

    if(!link.isAttribute(AttrElements.OPER_STATUS)) {
      if(STATUS_DOWN.equals(srcPortPrev.getAttribute(AttrElements.OPER_STATUS))
      || STATUS_DOWN.equals(dstPortPrev.getAttribute(AttrElements.OPER_STATUS))) {
         link.putAttribute(AttrElements.OPER_STATUS, STATUS_DOWN);
       } else {
         link.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
       }
    }

    notifyLinkChanged(null, link, LinkChanged.Action.add);

    String srcNodeId = link.getSrcNode();
    String dstNodeId = link.getDstNode();
    if (isNeededVerboseNodeEvent()) {
      Node srcNodeCurr = topology.getNode(srcNodeId).clone();
      Node dstNodeCurr = topology.getNode(dstNodeId).clone();
      notifyNodeChanged(srcNodePrev, srcNodeCurr, NodeChanged.Action.update);
      notifyNodeChanged(dstNodePrev, dstNodeCurr, NodeChanged.Action.update);
    }

    if (isNeededVerbosePortEvent()) {
      String srcPortId = link.getSrcPort();
      String dstPortId = link.getDstPort();
      Port srcPortCurr = topology.getPort(srcNodeId, srcPortId).clone();
      Port dstPortCurr = topology.getPort(dstNodeId, dstPortId).clone();
      notifyPortChanged(srcPortPrev, srcPortCurr, PortChanged.Action.update);
      notifyPortChanged(dstPortPrev, dstPortCurr, PortChanged.Action.update);
    }

    return new Response(Response.OK, link);
  }

  protected Response getLinks(boolean hasQuery, String queriesString) {
    log.debug("");
    if (hasQuery) {
      LinkQuery query = new LinkQuery(queriesString);
      if (!query.parse()) {
        return createErrorResponse(Response.BAD_REQUEST,
            "Query is invalid.");
      }
      return new Response(Response.OK, topology.getLinkMessages(query));
    } else {
      return new Response(Response.OK, topology.getLinkMap());
    }
  }

  protected Response getLink(String linkId) {
    log.debug("");
    Link link = topology.getLink(linkId);
    if (link == null) {
      return new Response(Response.NOT_FOUND, null);
    }
    return new Response(Response.OK, link);
  }

  protected Response putLink(String linkId, Link msg) throws Exception {
    log.debug("");

    if (msg == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "invalid request's body");
    }

    msg.setId(linkId);
    if (!msg.validate()) {
      return createErrorResponse(Response.BAD_REQUEST, "invalid linkId");
    }

    Node srcNodePrev = topology.getNode(msg.getSrcNode()).clone();
    Node dstNodePrev = topology.getNode(msg.getDstNode()).clone();
    Port srcPortPrev = topology.getPort(msg.getSrcNode(), msg.getSrcPort()).clone();
    Port dstPortPrev = topology.getPort(msg.getDstNode(), msg.getDstPort()).clone();

    Link linkOld;
    Link link = topology.getLink(linkId);

    LinkChanged.Action action;
    Integer returnCode = Response.OK;
    msg.setId(linkId);
    if (link == null) {
      String err = validateLinkMessage(msg);
      if (err != null) {
        return createErrorResponse(Response.BAD_REQUEST, null, err);
      }

      link = topology.createLink(msg);
      linkOld = null;
      action = LinkChanged.Action.add;
      returnCode = Response.CREATED;

      if(!link.isAttribute(AttrElements.OPER_STATUS)) {
         if(STATUS_DOWN.equals(srcPortPrev.getAttribute(AttrElements.OPER_STATUS))
         || STATUS_DOWN.equals(dstPortPrev.getAttribute(AttrElements.OPER_STATUS))) {
           link.putAttribute(AttrElements.OPER_STATUS, STATUS_DOWN);
         } else {
           link.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
         }
      }
    } else {
      // version conflict
      if (msg.getVersion() != null
          && !msg.getVersion().equals(link.getVersion())) {
        return createErrorResponse(Response.CONFLICT, link,
            "version conflict");
      }
      linkOld = link.clone();

      if (msg.equals(linkOld)) {
        return new Response(Response.OK, link);
      }

      if (link.getSrcNode() != null
          && !(link.getSrcNode().equals(msg.getSrcNode()))) {
        return createErrorResponse(Response.BAD_REQUEST, null,
            "src node can't be changed.");
      }
      if (link.getDstNode() != null
          && !(link.getDstNode().equals(msg.getDstNode()))) {
        return createErrorResponse(Response.BAD_REQUEST, null,
            "dst node can't be changed.");
      }
      if (link.getSrcPort() != null
          && !(link.getSrcPort().equals(msg.getSrcPort()))) {
        return createErrorResponse(Response.BAD_REQUEST, null,
            "src port can't be changed.");
      }
      if (link.getDstPort() != null
          && !(link.getDstPort().equals(msg.getDstPort()))) {
        return createErrorResponse(Response.BAD_REQUEST, null,
            "dst port can't be changed.");
      }
      link = topology.createLink(msg);
      action = LinkChanged.Action.update;
      returnCode = Response.OK;
    }

    // failed to deploy
    if (link == null) {
      return createErrorResponse(Response.CONFLICT, link,
          "deployment of link failed");
    }

    notifyLinkChanged(linkOld, link.clone(), action);

    if (action.equals(LinkChanged.Action.add)) {
      String srcNodeId = link.getSrcNode();
      String dstNodeId = link.getDstNode();
      if (isNeededVerboseNodeEvent()) {
        Node srcNodeCurr = topology.getNode(srcNodeId).clone();
        Node dstNodeCurr = topology.getNode(dstNodeId).clone();
        notifyNodeChanged(srcNodePrev, srcNodeCurr, NodeChanged.Action.update);
        notifyNodeChanged(dstNodePrev, dstNodeCurr, NodeChanged.Action.update);
      }
      if (isNeededVerbosePortEvent()) {
        String srcPortId = link.getSrcPort();
        String dstPortId = link.getDstPort();
        Port srcPortCurr = topology.getPort(srcNodeId, srcPortId).clone();
        Port dstPortCurr = topology.getPort(dstNodeId, dstPortId).clone();
        notifyPortChanged(srcPortPrev, srcPortCurr, PortChanged.Action.update);
        notifyPortChanged(dstPortPrev, dstPortCurr, PortChanged.Action.update);
      }
    }

    return new Response(returnCode, link);
  }

  protected Response putLinkAttributes(String linkId, Map<String, String> addAttributes) throws Exception {
    Link link = topology.getLink(linkId);
    Link linkOld = link.clone();

    // attributes copy (curr -> body)
    for (String key : addAttributes.keySet()) {
        link.putAttribute(key, addAttributes.get(key));
    }

    link.updateVersion();
    notifyLinkChanged(linkOld, link.clone(), LinkChanged.Action.update);
    return new Response(Response.OK, link);
  }

  protected Response deleteLink(String linkId, Link msg) throws Exception {
    log.debug("");
    Link link = topology.getLink(linkId);

    if (link == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "link_id not found");
    }

    if (msg != null && !link.getVersion().equals(msg.getVersion())) {
      return createErrorResponse(Response.CONFLICT, null,
          "version conflicted.");
    }

    Link linkOld = link;

    // delete failed
    if (!topology.deleteLink(link)) {
      return createErrorResponse(Response.CONFLICT, link,
          "deletion of link failed");
    }

    notifyLinkChanged(linkOld, null,
        LinkChanged.Action.delete);

    String srcNodeId = link.getSrcNode();
    String srcPortId = link.getSrcPort();
    Port srcPort = topology.getNodeMap().get(srcNodeId).getPort(srcPortId);
    Port srcOld = new Port(srcPort);

    if (srcOld != null) {
      if (isNeededVerbosePortEvent()) {
        notifyPortChanged(srcOld, new Port(srcPort),
            PortChanged.Action.update);
      }
    }

    String dstNodeId = link.getSrcNode();
    String dstPortId = link.getSrcPort();
    Port dstPort = topology.getNodeMap().get(dstNodeId).getPort(dstPortId);
    Port dstOld = new Port(dstPort);

    if (dstOld != null) {
      if (isNeededVerbosePortEvent()) {
        notifyPortChanged(dstOld, new Port(dstPort),
            PortChanged.Action.update);
      }
    }

    return new Response(Response.OK, null);
  }

  // ******************* Actions about flow *******************
  private enum FlowRequestAction {
    DELETE_WITHOUT_EVENT,
    UPDATE_WITH_EVENT,
    UPDATE_WITHOUT_EVENT,
  }

  private FlowRequestAction checkFlowSequence(Flow flow) {

    if( deletingFlow.containsKey(flow.getFlowId())
        && flow.getStatusValue() == FlowStatus.TEARDOWN) {
      return FlowRequestAction.UPDATE_WITHOUT_EVENT;
    }

    if( deletingFlow.containsKey(flow.getFlowId())
         && flow.getStatusValue() == FlowStatus.NONE) {
      return FlowRequestAction.DELETE_WITHOUT_EVENT;
    }

    return  FlowRequestAction.UPDATE_WITH_EVENT;
  }

  protected Response postFlow(Flow msg) throws Exception {

    if (msg == null) {
      return createErrorResponse(
          Response.BAD_REQUEST, "Bad format: Flow is expected");
    }
    if (!msg.validate()) {
      return createErrorResponse(
          Response.BAD_REQUEST, "Bad format: Flow object was invalid");
    }

    if (msg.getStatus() == null) {
      msg.setStatus(FlowStatus.NONE.toString());
    }

    Flow flow = flowset.createFlow(msg);
    if (flow == null) {
      return createErrorResponse(Response.BAD_REQUEST, "Invalid flow type");
    }
    notifyFlowChanged(null, flow, FlowChanged.Action.add);

    return new Response(Response.OK, flow);
  }

  protected Response getFlows(boolean hasQuery, String queriesString) {
    log.debug("");
    if (hasQuery) {
      FlowQuery query = FlowQueryFactory.create(queriesString);
      if (query == null) {
        return createErrorResponse(Response.BAD_REQUEST,
            "Query is invalid.");
      }
      if (!query.parse()) {
        return createErrorResponse(Response.BAD_REQUEST,
            "Query is invalid.");
      }
      query.setTopology(topology);
      return new Response(Response.OK, flowset.getFlowMessages(query));
    } else {
      return new Response(Response.OK, flowset);
    }
  }

  protected Response getFlow(String flowId) {
    log.debug("");
    Flow flow = flowset.getFlow(flowId);
    if (flow == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "flow_id not found");
    }

    return new Response(Response.OK, flow);
  }

  protected Response putFlow(String flowId, Flow msg) throws Exception {
    log.debug("");
    Flow flowOld = null;

    if (msg == null) {
      return createErrorResponse(Response.BAD_REQUEST,
          "Flow object is expected");
    }

    if (!msg.validate()) {
      return createErrorResponse(
          Response.BAD_REQUEST, "Bad format: Flow object was invalid");
    }
    Flow flow = flowset.getFlow(flowId);

    FlowChanged.Action action = null;
    Integer returnCode = Response.CREATED;
    if (flow == null) {
      if (msg.getStatus() == null) {
        msg.setStatus(FlowObject.FlowStatus.NONE.toString());
      }

      flowOld = null;
      flow = flowset.createFlow(flowId, msg, Flow.INITIAL_VERSION);
      action = FlowChanged.Action.add;
      returnCode = Response.CREATED;
    } else {
      // version conflict
      if (msg.getVersion() != null
          && !msg.getVersion().equals(flow.getVersion())) {
        return createErrorResponse(Response.CONFLICT, flow,
            "version conflict");
      }

      flowOld = flow.clone();
      if (msg.equals(flowOld)) {
        return new Response(Response.OK, flow);
      }

      FlowRequestAction flowAction;
      flowAction = checkFlowSequence(msg);

      switch (flowAction) {
        case DELETE_WITHOUT_EVENT:
          flowset.deleteFlow(flowOld);
          deletingFlow.remove(flow.getFlowId());
          return new Response(Response.OK, flow);
        case UPDATE_WITHOUT_EVENT:
          flow = flowset.createFlow(flowId, msg, msg.getVersion());
          returnCode = Response.OK;
          break;
        case UPDATE_WITH_EVENT:
          flow = flowset.createFlow(flowId, msg, msg.getVersion());
          action = FlowChanged.Action.update;
          returnCode = Response.OK;
          break;
        default:
          return createErrorResponse(Response.BAD_REQUEST, "Bad Sequence");
      }
    }

    if (flow == null) {
      return createErrorResponse(Response.BAD_REQUEST, "Not compatible object");
    }
    if (action != null) {
      notifyFlowChanged(flowOld, flow.clone(), action);
    }
    return new Response(returnCode, flow);
  }


  protected Response putFlowAttributes(String flowId, Map<String, String> addAttributes) throws Exception {

    Flow flow = flowset.getFlow(flowId);
    Flow flowOld = flow.clone();

    // attributes copy (curr -> body)
    for (String key : addAttributes.keySet()) {
      flow.putAttribute(key, addAttributes.get(key));
    }

    flow.updateVersion();
    notifyFlowChanged(flowOld, flow.clone(), FlowChanged.Action.update);
    return new Response(Response.OK, flow);
  }

  protected Response deleteFlow(String flowId, Flow msg) throws Exception {
    log.debug("");
    Flow flow = flowset.getFlow(flowId);
    if (flow == null) {
      return createErrorResponse(Response.NOT_FOUND, null, "flow_id not found");
    }

    if (msg != null && !flow.getVersion().equals(msg.getVersion())) {
      return createErrorResponse(Response.CONFLICT, null,
          "version conflicted.");
    }

    if (flow.getStatus().equals(FlowStatus.NONE.toString())) {
      flowset.deleteFlow(flow);
      deletingFlow.remove(flow.getFlowId());
    } else {
      deletingFlow.put(flow.getFlowId(), "");
    }

    // DELETE_EVENT_ONLY
    notifyFlowChanged(flow, null, FlowChanged.Action.delete);

    return new Response(Response.OK, null);
  }

  // ******************* Actions about packet *******************
  protected Response getPackets() {
    log.debug("");
    return new Response(Response.OK, packetQueue.getPacketStatus());
  }

  private Response postPacket(PacketQueue queue, Packet msg) throws Exception {
    log.debug("");
    if (msg == null) {
      return createErrorResponse(Response.BAD_REQUEST, null,
          "packet format is not valid.");
    }
    Packet packet = PacketObject.createPacket(msg);
    if (packet == null) {
      return createErrorResponse(Response.BAD_REQUEST, null,
          "packet type is not valid.");
    }

    Packet retPacket = queue.enqueuePacket(packet);

    if (queue == packetQueue.getInQueue()) {
      notifyInPacketAdded(retPacket);
    } else {
      notifyOutPacketAdded(retPacket);
    }
    return new Response(Response.OK, retPacket);
  }

  private Response getPacket(PacketQueue queue, boolean hasQuery,
      String queriesString) {
    log.debug("");
    if (hasQuery) {
      PacketQuery<?> query;
      if (queue == packetQueue.getInQueue()) {
        query = new InPacketQuery(queriesString);
      } else {
        query = new OutPacketQuery(queriesString);
      }
      if (!query.parse()) {
        return createErrorResponse(Response.BAD_REQUEST, null,
            "Query is invalid.");
      }
      return new Response(Response.OK, queue.getPacketMessages(query));
    } else {
      return new Response(Response.OK, queue.getPacketStatus());
    }
  }

  private Response getPacket(PacketQueue queue, String packetId) {
    log.debug("");
    Packet packet = queue.getPacket(packetId);

    if (packet == null) {
      return createErrorResponse(Response.NOT_FOUND, null,
          "packet_id not found");
    }

    return new Response(Response.OK, packet);
  }

  private Response deletePackets(PacketQueue queue) {
    queue.clearPackets();

    return new Response(Response.OK, null);
  }

  private Response getHeadPacket(PacketQueue queue) {
    log.debug("");
    Packet packet = queue.peekPacket();
    Packet msg = null;
    Integer returnCode = Response.OK;
    if (packet == null) {
      returnCode = Response.NO_CONTENT;
    } else {
      msg = packet;
      returnCode = Response.OK;
    }
    return new Response(returnCode, msg);
  }

  private Response deleteHeadPacket(PacketQueue queue) {
    log.debug("");
    Packet packet = queue.dequeuePacket();
    Packet msg = null;
    Integer returnCode = Response.OK;
    if (packet == null) {
      returnCode = Response.NO_CONTENT;
    } else {
      msg = packet;
      returnCode = Response.OK;
    }

    return new Response(returnCode, msg);
  }

  private Response deletePacket(PacketQueue queue, String packetId) {
    log.debug("");
    Packet packet = queue.pickPacket(packetId);

    if (packet == null) {
      return new Response(Response.OK, null);
    }

    return new Response(Response.OK, packet);
  }

  protected Response postInPacket(Packet msg) throws Exception {
    log.debug("");
    return postPacket(packetQueue.getInQueue(), msg);
  }

  protected Response getInPacket(boolean hasQuery, String queriesString) {
    log.debug("");
    return getPacket(packetQueue.getInQueue(), hasQuery, queriesString);
  }

  protected Response getInPacket(String inPacketId) {
    log.debug("");
    return getPacket(packetQueue.getInQueue(), inPacketId);
  }

  protected Response deleteInPackets() {
    log.debug("");
    return deletePackets(packetQueue.getInQueue());
  }

  protected Response getHeadInPacket() {
    log.debug("");
    return getHeadPacket(packetQueue.getInQueue());
  }

  protected Response deleteHeadInPacket() {
    log.debug("");
    return deleteHeadPacket(packetQueue.getInQueue());
  }

  protected Response deleteInPacket(String inPacketId) {
    log.debug("");
    return deletePacket(packetQueue.getInQueue(), inPacketId);
  }

  protected Response postOutPacket(Packet msg) throws Exception {
    log.debug("");
    return postPacket(packetQueue.getOutQueue(), msg);
  }

  protected Response getOutPacket(boolean hasQuery, String queriesString) {
    log.debug("");
    return getPacket(packetQueue.getOutQueue(), hasQuery, queriesString);
  }

  protected Response getOutPacket(String outPacketId) {
    log.debug("");
    return getPacket(packetQueue.getOutQueue(), outPacketId);
  }

  protected Response deleteOutPackets() {
    log.debug("");
    return deletePackets(packetQueue.getOutQueue());
  }

  protected Response getHeadOutPacket() {
    log.debug("");
    return getHeadPacket(packetQueue.getOutQueue());
  }

  protected Response deleteHeadOutPacket() {
    log.debug("");
    return deleteHeadPacket(packetQueue.getOutQueue());
  }

  protected Response deleteOutPacket(String outPacketId) {
    log.debug("");
    return deletePacket(packetQueue.getOutQueue(), outPacketId);
  }

  // ***************** Functions to notify Events *****************

  protected Response postEvent(String eventType, Object body)
      throws Exception {
    log.debug("");
    publishEvent(eventType, body);
    return new Response(Response.ACCEPTED, null);
  }

  private Response notifyTopologyChanged(Topology prev, Topology curr,
      TopologyChanged.Action action) throws Exception {
    log.debug("");
    TopologyChanged msg = new TopologyChanged(prev, curr, action);
    return postEvent(TopologyChanged.TYPE, msg);
  }

  private Response notifyTopologyChangedToAdd(Topology curr) throws Exception {
    log.debug("");
    return notifyTopologyChanged(null, curr, TopologyChanged.Action.add);
  }

  private Response notifyTopologyChangedToUpdate(Topology prev, Topology curr)
      throws Exception {
    log.debug("");
    return notifyTopologyChanged(prev, curr, TopologyChanged.Action.update);
  }

  private Response notifyNodeChanged(Node prev, Node curr,
      NodeChanged.Action action) throws Exception {
    log.debug("");
    NodeChanged msg = new NodeChanged(prev, curr, action);
    return postEvent(NodeChanged.TYPE, msg);
  }

  private Response notifyPortChanged(Port prev, Port curr,
      PortChanged.Action action) throws Exception {
    log.debug("");
    PortChanged msg = new PortChanged(prev, curr, action);
    return postEvent(PortChanged.TYPE, msg);
  }

  private Response notifyLinkChanged(Link prev, Link curr,
      LinkChanged.Action action) throws Exception {
    log.debug("");
    LinkChanged msg = new LinkChanged(prev, curr, action);
    return postEvent(LinkChanged.TYPE, msg);
  }

  private Response notifyFlowChanged(Flow prev, Flow curr,
      FlowChanged.Action action) throws Exception {
    log.debug("");
    FlowChanged msg = new FlowChanged(prev, curr, action);
    return postEvent(FlowChanged.TYPE, msg);
  }

  private Response notifyInPacketAdded(Packet inpacket) throws Exception {
    log.debug("");
    InPacketAdded msg = new InPacketAdded(inpacket);
    return postEvent(InPacketAdded.TYPE, msg);
  }

  private Response notifyOutPacketAdded(Packet outpacket) throws Exception {
    log.debug("");
    OutPacketAdded msg = new OutPacketAdded(outpacket);
    return postEvent(OutPacketAdded.TYPE, msg);
  }

  private String getNodeByPhysicalId(String physicalId) {
    log.debug("");

    Map<String, Node> nodeMap = topology.getNodeMap();
    for (Node node : nodeMap.values()) {
      if (physicalId.equals(node
          .getAttribute(Logic.AttrElements.PHYSICAL_ID))) {
        return node.getId();
      }
    }
    return null;
  }

  private Port getPortByPhysicalId(String physicalId) {
    log.debug("");

    for (String nid : topology.getNodeMap().keySet()) {
      Node node = topology.getNode(nid);
      for (String pid : node.getPortMap().keySet()) {
        Port port = node.getPort(pid);
        if (physicalId.equals(port
            .getAttribute(Logic.AttrElements.PHYSICAL_ID))) {
          return port;
        }
      }
    }

    return null;
  }

  /**
   * Set URI parsing rules to RequestParser.
   */
  private RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        log.debug("");

        // ******************* Actions about settings
        // *******************
        addRule(Method.GET, "settings/"
            + NetworkObjectSettings.KEY_VERBOSE_PORT_EVENT,
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getSettingVerbosePort();
              }
            });

        addRule(Method.PUT, "settings/"
            + NetworkObjectSettings.KEY_VERBOSE_PORT_EVENT,
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putSettingVerbosePort(
                parsed.getRequest().getBody(String.class));
              }
            });

        addRule(Method.GET, "settings/"
            + NetworkObjectSettings.KEY_VERBOSE_LINK_EVENT,
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getSettingVerboseLink();
              }
            });

        addRule(Method.PUT, "settings/"
            + NetworkObjectSettings.KEY_VERBOSE_LINK_EVENT,
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putSettingVerboseLink(
                parsed.getRequest().getBody(String.class));
              }
            });

        // ******************* Actions about topology
        // *******************
        addRule(Method.GET, "topology", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getTopology();
          }
        });

        addRule(Method.PUT, "topology", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return putTopology(
            parsed.getRequest().getBody(Topology.class));
          }
        });

        // ******************* Actions about node *******************
        addRule(Method.POST, "topology/nodes", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postNode(
            parsed.getRequest().getBody(Node.class));
          }
        });

        addRule(Method.GET, "topology/nodes", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getNodes(parsed.hasQuery(),
                parsed.getQueriesString());
          }
        });

        addRule(Method.GET, "topology/nodes/<node_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getNode(parsed.getParam("node_id"));
              }
            });

        addRule(Method.PUT, "topology/nodes/<node_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putNode(parsed.getParam("node_id"),
                    parsed.getRequest().getBody(Node.class));
              }
            });

        addRule(Method.PUT, "topology/nodes/<node_id>/attributes",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putNodeAttributes(parsed.getParam("node_id"),
                    parsed.getRequest().getBodyAsStringMap());
              }
            });

        addRule(Method.DELETE, "topology/nodes/<node_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deleteNode(parsed.getParam("node_id"),
                    parsed.getRequest().getBody(Node.class));
              }
            });

        // ******************* Actions about port *******************
        addRule(Method.POST, "topology/nodes/<node_id>/ports",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return postPort(parsed.getParam("node_id"),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        addRule(Method.GET, "topology/nodes/<node_id>/ports",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return getPorts(parsed.hasQuery(),
                    parsed.getQueriesString(),
                    parsed.getParam("node_id"),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        addRule(Method.GET, "topology/nodes/<node_id>/ports/<port_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getPort(parsed.getParam("node_id"),
                    parsed.getParam("port_id"));
              }
            });

        addRule(Method.PUT, "topology/nodes/<node_id>/ports/<port_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putPort(parsed.getParam("node_id"),
                    parsed.getParam("port_id"),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        addRule(Method.PUT, "topology/nodes/<node_id>/ports/<port_id>/attributes",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putPortAttributes(parsed.getParam("node_id"),
                    parsed.getParam("port_id"),
                    parsed.getRequest().getBodyAsStringMap());
              }
            });

        addRule(Method.DELETE,
            "topology/nodes/<node_id>/ports/<port_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deletePort(parsed.getParam("node_id"),
                    parsed.getParam("port_id"),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        // ******************* Actions about physical node/port
        // *******************
        addRule(Method.GET, "topology/physical_nodes/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getNodePhysicalId(parsed
                    .getParam("physical_id"));
              }
            });

        addRule(Method.PUT, "topology/physical_nodes/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putNodePhysicalId(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBody(Node.class));
              }
            });

        addRule(Method.PUT, "topology/physical_nodes/<physical_id>/attributes",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putNodePhysicalIdAttributes(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBodyAsStringMap());
              }
            });

        addRule(Method.DELETE, "topology/physical_nodes/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deleteNodePhysicalId(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBody(Node.class));
              }
            });

        addRule(Method.GET, "topology/physical_ports/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getPortPhysicalId(parsed
                    .getParam(Logic.AttrElements.PHYSICAL_ID));
              }
            });

        addRule(Method.PUT, "topology/physical_ports/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putPortPhysicalId(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        addRule(Method.PUT, "topology/physical_ports/<physical_id>/attributes",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putPortPhysicalIdAttributes(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBodyAsStringMap());
              }
            });

        addRule(Method.DELETE, "topology/physical_ports/<physical_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deletePortPhysicalId(
                    parsed.getParam(Logic.AttrElements.PHYSICAL_ID),
                    parsed.getRequest().getBody(Port.class));
              }
            });

        // ******************* Actions about link *******************
        addRule(Method.POST, "topology/links", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postLink(parsed.getRequest().getBody(Link.class));
          }
        });

        addRule(Method.GET, "topology/links", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getLinks(parsed.hasQuery(),
                parsed.getQueriesString());
          }
        });

        addRule(Method.GET, "topology/links/<link_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getLink(parsed.getParam("link_id"));
              }
            });

        addRule(Method.PUT, "topology/links/<link_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putLink(parsed.getParam("link_id"),
                    parsed.getRequest().getBody(Link.class));
              }
            });

        addRule(Method.PUT, "topology/links/<link_id>/attributes",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return putLinkAttributes(parsed.getParam("link_id"),
                    parsed.getRequest().getBodyAsStringMap());
              }
            });

        addRule(Method.DELETE, "topology/links/<link_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deleteLink(parsed.getParam("link_id"),
                    parsed.getRequest().getBody(Link.class));
              }
            });

        // ******************* Actions about flow *******************
        addRule(Method.POST, "flows", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postFlow(FlowObject.readFlowMessageFrom(parsed
                .getRequest()
                .getBodyValue()));
          }
        });

        addRule(Method.GET, "flows", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getFlows(parsed.hasQuery(),
                parsed.getQueriesString());
          }
        });

        addRule(Method.GET, "flows/<flow_id>", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getFlow(parsed.getParam("flow_id"));
          }
        });

        addRule(Method.PUT, "flows/<flow_id>", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return putFlow(parsed.getParam("flow_id"),
                FlowObject.readFlowMessageFrom(parsed
                    .getRequest().getBodyValue()));
          }
        });

        addRule(Method.PUT, "flows/<flow_id>/attributes", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return putFlowAttributes(parsed.getParam("flow_id"),
              parsed.getRequest().getBodyAsStringMap());
          }
        });

        addRule(Method.DELETE, "flows/<flow_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deleteFlow(parsed.getParam("flow_id"),
                    FlowObject.readFlowMessageFrom(parsed
                        .getRequest().getBodyValue()));
              }
            });

        // ******************* Actions about packet *******************
        addRule(Method.GET, "packets", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getPackets();
          }
        });

        addRule(Method.POST, "packets/in", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postInPacket(PacketObject
                .readPacketMessageFrom(parsed.getRequest()
                    .getBodyValue()));
          }
        });

        addRule(Method.GET, "packets/in", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getInPacket(parsed.hasQuery(),
                parsed.getQueriesString());
          }
        });

        addRule(Method.DELETE, "packets/in", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return deleteInPackets();
          }
        });

        addRule(Method.GET, "packets/in/head", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getHeadInPacket();
          }
        });

        addRule(Method.DELETE, "packets/in/head",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteHeadInPacket();
              }
            });

        addRule(Method.GET, "packets/in/<packet_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getInPacket(parsed.getParam("packet_id"));
              }
            });

        addRule(Method.DELETE, "packets/in/<packet_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteInPacket(parsed
                    .getParam("packet_id"));
              }
            });

        addRule(Method.POST, "packets/out", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postOutPacket(PacketObject
                .readPacketMessageFrom(parsed.getRequest()
                    .getBodyValue()));
          }
        });

        addRule(Method.GET, "packets/out", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getOutPacket(parsed.hasQuery(),
                parsed.getQueriesString());
          }
        });

        addRule(Method.DELETE, "packets/out", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return deleteOutPackets();
          }
        });

        addRule(Method.GET, "packets/out/head", new IActionCallback() {
          @Override
          public Response process(
              RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getHeadOutPacket();
          }
        });

        addRule(Method.DELETE, "packets/out/head",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteHeadOutPacket();
              }
            });

        addRule(Method.GET, "packets/out/<packet_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getOutPacket(parsed
                    .getParam("packet_id"));
              }
            });

        addRule(Method.DELETE, "packets/out/<packet_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteOutPacket(parsed
                    .getParam("packet_id"));
              }
            });
      }
    };
  }

  private Response createErrorResponse(int code, Object body) {
    log.debug("");
    return createErrorResponse(code, body, body.toString());
  }

  private Response createErrorResponse(int code, Object body, String msg) {
    log.debug("");
    Response rsp = new Response(code, body);
    if (body == null) {
      rsp = new Response(code, msg);
    }
    return rsp;
  }

  private boolean isNeededVerboseNodeEvent() {
    log.debug("");
    return objectSettings.isVerbosePortEvent();
  }

  private boolean isNeededVerbosePortEvent() {
    log.debug("");
    return objectSettings.isVerboseLinkEvent();
  }
}
