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

import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.PacketObject;
import org.o3project.odenos.core.component.network.packet.PacketStatus;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NetworkInterface class.
 *
 */
public class NetworkInterface {
  private static final Logger log = LoggerFactory.getLogger(NetworkInterface.class);

  // Topology
  public static final String TOPOLOGY_PATH = "topology";
  // Node
  public static final String NODE_PATH = "topology/nodes/%s";
  public static final String NODES_PATH = "topology/nodes";
  public static final String PHYSICAL_NODES_PATH = "topology/physical_nodes/%s";
  // Port
  public static final String PORT_PATH = "topology/nodes/%s/ports/%s";
  public static final String PORTS_PATH = "topology/nodes/%s/ports";
  public static final String PHYSICAL_PORTS_PATH = "topology/physical_ports/%s";
  // Link
  public static final String LINK_PATH = "topology/links/%s";
  public static final String LINKS_PATH = "topology/links";
  // Flow
  public static final String FLOW_PATH = "flows/%s";
  public static final String FLOWS_PATH = "flows";
  // Packet
  public static final String INPACKET_PATH = "packets/in/%s";
  public static final String INPACKETS_PATH = "packets/in";
  public static final String INPACKETS_HEAD_PATH = "packets/in/head";
  public static final String OUTPACKET_PATH = "packets/out/%s";
  public static final String OUTPACKETS_PATH = "packets/out";
  public static final String OUTPACKETS_HEAD_PATH = "packets/out/head";
  public static final String PACKETS_PATH = "packets";

  private String networkId;
  private MessageDispatcher dispatcher;

  private String sourceObjectId;

  /**
   * Constructor.
   * @param dispatcher Message Dispatcher object.
   * @param nwcId network ID.
   */
  @Deprecated
  public NetworkInterface(
      final MessageDispatcher dispatcher,
      final String nwcId) {
    this.networkId = nwcId;
    this.dispatcher = dispatcher;
    log.debug("Create NetworkInterface : networkId = '" + this.networkId
        + "'.");
  }

  /**
   * Constructor.
   * @param dispatcher Message Dispatcher object.
   * @param nwcId network ID.
   * @param sourceObjectId source objectID
   */
  public NetworkInterface(
      final MessageDispatcher dispatcher,
      final String nwcId, final String sourceObjectId) {
    this.networkId = nwcId;
    this.dispatcher = dispatcher;
    this.sourceObjectId = sourceObjectId;
    log.debug("Create NetworkInterface : networkId = '" + this.networkId
        + "'.");
  }

  /**
   * Returns a network ID.
   * @return network ID.
   */
  public String getNetworkId() {
    return networkId;
  }

  /**
   * Sets a network ID.
   * @param networkId network ID.
   */
  public void setNetworkId(String networkId) {
    this.networkId = networkId;
  }

  // //////////////////////////////////////
  //
  // Topology's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "GET Topology".
   * <pre>
   * {@literal
   * GET Topology.
   * ( GET <base_uri>/topology )
   * }
   * </pre>
   * @return value of the topology.
   */
  public final Topology getTopology() {
    String path = TOPOLOGY_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Topology.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Topology".
   * <pre>
   * {@literal
   * PUT Topology.
   * ( PUT <base_uri>/topology )
   * }
   * </pre>
   * @param body topology.
   * @return response object.
   */
  public final Response putTopology(final Topology body) {
    String path = TOPOLOGY_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  // //////////////////////////////////////
  //
  // Node's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "POST Node".
   * <pre>
   * {@literal
   * POST Node.
   * ( POST <base_uri>/topology/nodes )
   * }
   * </pre>
   * @param body node.
   * @return response object.
   */
  public final Response postNode(final Node body) {
    String path = NODES_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET Nodes".
   * <pre>
   * {@literal
   * GET Nodes.
   * ( GET <base_uri>/topology/nodes )
   * }
   * </pre>
   * @return map of the nodes.
   */
  public final Map<String, Node> getNodes() {
    String path = NODES_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBodyAsMap(Node.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Node".
   * <pre>
   * {@literal
   * GET Node.
   * ( GET <base_uri>/topology/nodes/<node_id> )
   * }
   * </pre>
   * @param nodeId node ID.
   * @return value of the node.
   */
  public final Node getNode(final String nodeId) {
    String path = String.format(NODE_PATH, nodeId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Node.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Node".
   * <pre>
   * {@literal
   * PUT Node.
   * ( PUT <base_uri>/topology/nodes/<node_id> )
   * }
   * </pre>
   * @param body node.
   * @return response object.
   */
  public final Response putNode(final Node body) {
    String path = String.format(NODE_PATH, body.getId());
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE Node".
   * <pre>
   * {@literal
   * DELETE Node.
   * ( DELETE <base_uri>/topology/nodes/<node_id> )
   * }
   * </pre>
   * @param nodeId node id
   * @return response object.
   */
  public final Response delNode(final String nodeId) {
    String path = String.format(NODE_PATH, nodeId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * Requests a "GET PhysicalNode".
   * <pre>
   * {@literal
   * GET PhysicalNode.
   * ( GET <base_uri>/topology/physical_nodes/<physical_id> )
   * }
   * </pre>
   * @param physicalId physical ID.
   * @return value of the node.
   */
  public final Node getPhysicalNode(final String physicalId) {
    String path = String.format(PHYSICAL_NODES_PATH, physicalId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Node.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT PhysicalNode".
   * <pre>
   * {@literal
   * PUT PhysicalNode.
   * ( PUT <base_uri>/topology/physical_nodes/<physical_id> )
   * }
   * </pre>
   * @param body a node.
   * @return response object.
   */
  public final Response putPhysicalNode(final Node body) {
    String path = String.format(PHYSICAL_NODES_PATH,
        body.getAttribute(Logic.AttrElements.PHYSICAL_ID));
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE PhysicalNode".
   * <pre>
   * {@literal
   * DELETE PhysicalNode.
   * ( DELETE <base_uri>/topology/physical_nodes/<physical_id> )
   * }
   * </pre>
   * @param physicalId physical ID.
   * @return response node.
   */
  public final Response delPhysicalNode(final String physicalId) {
    String path = String.format(PHYSICAL_NODES_PATH, physicalId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  // //////////////////////////////////////
  //
  // Packet's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "POST Port".
   * <pre>
   * {@literal
   * POST Port.
   * ( POST <base_uri>/topology/nodes/<node_id>/ports )
   * }
   * </pre>
   * @param body a port.
   * @return response object.
   */
  public final Response postPort(final Port body) {
    String path = String.format(PORTS_PATH, body.getNode());
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET Ports".
   * <pre>
   * {@literal
   * GET Ports.
   * ( GET <base_uri>/topology/nodes/<node_id>/ports )
   * }
   * </pre>
   * @param nodeId node ID.
   * @return map of the ports.
   */
  public final Map<String, Port> getPorts(String nodeId) {
    String path = String.format(PORTS_PATH, nodeId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBodyAsMap(Port.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Port".
   * <pre>
   * {@literal
   * GET Port.
   * ( GET <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
   * }
   * </pre>
   * @param nodeId node ID.
   * @param portId port ID.
   * @return value of the port.
   */
  public final Port getPort(
      final String nodeId, final String portId) {
    String path = String.format(PORT_PATH, nodeId, portId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Port.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Port".
   * <pre>
   * PUT Port.
   * {@literal
   * ( PUT <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
   * }
   * </pre>
   * @param body a port.
   * @return response object.
   */
  public final Response putPort(final Port body) {
    String path =
        String.format(PORT_PATH, body.getNode(), body.getId());
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE Port".
   * <pre>
   * {@literal
   * DELETE Port.
   * ( DELETE <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
   * }
   * </pre>
   * @param nodeId node ID.
   * @param portId port ID.
   * @return response object.
   */
  public final Response delPort(
      final String nodeId, final String portId) {
    String path = String.format(PORT_PATH, nodeId, portId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * Requests a "GET PhysicalPort".
   * <pre>
   * {@literal
   * GET PhysicalPort.
   * ( GET <base_uri>/topology/physical_ports/<physical_id> )
   * }
   * </pre>
   * @param physicalId physical ID.
   * @return value of the port.
   */
  public final Port getPhysicalPort(final String physicalId) {
    String path = String.format(PHYSICAL_PORTS_PATH, physicalId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Port.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT PhysicalPort".
   * <pre>
   * {@literal
   * PUT PhysicalPort.
   * ( PUT <base_uri>/topology/physical_ports/<physical_id> )
   * }
   * </pre>
   * @param body a port.
   * @return response object.
   */
  public final Response putPhysicalPort(final Port body) {
    String path = String.format(PHYSICAL_PORTS_PATH,
        body.getAttribute(Logic.AttrElements.PHYSICAL_ID));
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE PhysicalPort".
   * <pre>
   * {@literal
   * DELETE PhysicalPort.
   * ( DELETE <base_uri>/topology/physical_ports/<physical_id> )
   * }
   * </pre>
   * @param physicalId physical ID.
   * @return response object.
   */
  public final Response delPhysicalPort(final String physicalId) {
    String path = String.format(PHYSICAL_PORTS_PATH, physicalId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  // //////////////////////////////////////
  //
  // Link's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "POST Link".
   * <pre>
   * {@literal
   * POST Link.
   * ( POST <base_uri>/topology/links )
   * }
   * </pre>
   * @param body a link.
   * @return response object.
   */
  public final Response postLink(final Link body) {
    String path = LINKS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET Links".
   * <pre>
   * {@literal
   * GET Links.
   * ( GET <base_uri>/topology/links )
   * }
   * </pre>
   * @return map of the links.
   */
  public final Map<String, Link> getLinks() {
    String path = LINKS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBodyAsMap(Link.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Link".
   * <pre>
   * {@literal
   * GET Link.
   * ( GET <base_uri>/topology/links/<link_id> )
   * }
   * </pre>
   * @param linkId link ID.
   * @return value of the link.
   */
  public final Link getLink(final String linkId) {
    String path = String.format(LINK_PATH, linkId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(Link.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Link".
   * <pre>
   * {@literal
   * PUT Link.
   * ( PUT <base_uri>/topology/links/<link_id> )
   * }
   * </pre>
   * @param body a link.
   * @return response object.
   */
  public final Response putLink(final Link body) {
    String path = String.format(LINK_PATH, body.getId());
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE Link".
   * <pre>
   * {@literal
   * DELETE Link.
   * ( DELETE <base_uri>/topology/links/<link_id> )
   * }
   * </pre>
   * @param linkId a link.
   * @return response object.
   */
  public final Response delLink(final String linkId) {
    String path = String.format(LINK_PATH, linkId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  // //////////////////////////////////////
  //
  // Flow's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "POST Flow".
   * <pre>
   * {@literal
   * POST Flow.
   * ( POST <base_uri>/flows )
   * }
   * </pre>
   * @param body a flow.
   * @return response object.
   */
  public final Response postFlow(final Flow body) {
    String path = FLOWS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET FlowSet".
   * <pre>
   * {@literal
   * GET FlowSet.
   * ( GET <base_uri>/flows )
   * }
   * </pre>
   * @return value of the flow set.
   */
  public final FlowSet getFlowSet() {
    String path = FLOWS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(FlowSet.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Flow".
   * <pre>
   * {@literal
   * GET Flow.
   * ( GET <base_uri>/flows/<flow_id> )
   * }
   * </pre>
   * @param flowId flow ID.
   * @return value of the flow.
   */
  public final Flow getFlow(final String flowId) {
    String path = String.format(FLOW_PATH, flowId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return FlowObject.readFlowMessageFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Flow".
   * <pre>
   * {@literal
   * PUT Flow.
   * ( PUT <base_uri>/flows/<flow_id> )
   * }
   * </pre>
   * @param body a flow.
   * @return response object.
   */
  public final Response putFlow(final Flow body) {
    String path = String.format(FLOW_PATH, body.getFlowId());
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return putObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "DELETE Flow".
   * <pre>
   * {@literal
   * DELETE Flow.
   * ( DELETE <base_uri>/flows/<flow_id> )
   * }
   * </pre>
   * @param flowId flow ID.
   * @return response object.
   */
  public final Response delFlow(final String flowId) {
    String path = String.format(FLOW_PATH, flowId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    Flow flow = getFlow(flowId);
    if (flow == null) {
      return new Response(Response.OK, null);
    }
    try {
      flow.setEnabled(false);
      Response resp = sendRequest(this.networkId,
          Request.Method.DELETE, path, flow);
      if (resp.isError("DELETE")) {
        log.warn("invalid DELETE:" + resp.statusCode);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  // //////////////////////////////////////
  //
  // Packet's Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "GET Packets".
   * <pre>
   * {@literal
   * GET Packets.
   * ( GET <base_uri>/packets )
   * }
   * </pre>
   * @return value of the packet status.
   */
  public final PacketStatus getPackets() {
    String path = PACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(PacketStatus.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "POST InPacket".
   * <pre>
   * {@literal
   * POST InPacket.
   * ( POST <base_uri>/packets/in )
   * }
   * </pre>
   * @param body a Packet IN.
   * @return response object.
   */
  public final Response postInPacket(final InPacket body) {
    String path = INPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET InPackets".
   * <pre>
   * {@literal
   * GET InPackets.
   * ( GET <base_uri>/packets/in )
   * }
   * </pre>
   * @return value of the packet status.
   */
  public final PacketStatus getInPackets() {
    String path = INPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(PacketStatus.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "DELETE InPackets".
   * <pre>
   * {@literal
   * DELETE InPackets.
   * ( DELETE <base_uri>/packets/in )
   * }
   * </pre>
   * @return response object.
   */
  public final Response delInPackets() {
    String path = INPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * Get InPacket first pending currently.
   * <pre>
   * {@literal
   * Get InPacket first pending currently.
   * ( GET <base_uri>/packets/in/head )
   * }
   * </pre>
   * @return value of the Pakcet IN.
   */
  public final InPacket getInPacketHead() {
    String path = INPACKETS_HEAD_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return PacketObject.readInPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Delete InPacket first pending currently.
   * <pre>
   * {@literal
   * Delete InPacket first pending currently.
   * ( DELETE <base_uri>/packets/in/head )
   * }
   * </pre>
   * @return response object.
   */
  public final Response delInPacketHead() {
    String path = INPACKETS_HEAD_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * Requests a "GET InPacket".
   * <pre>
   * {@literal
   * GET InPacket.
   * ( GET <base_uri>/packets/in/<packet_id> )
   * }
   * </pre>
   * @param packetId packet ID.
   * @return value of the Packet IN.
   */
  public final InPacket getInPacket(final String packetId) {
    String path = String.format(INPACKET_PATH, packetId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return PacketObject.readInPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "DELETE InPacket".
   * <pre>
   * {@literal
   * DELETE InPacket.
   * ( DELETE <base_uri>/packets/in/<packet_id> )
   * }
   * </pre>
   * @param id packet ID.
   * @return response object.
   */
  public final Response delInPacket(final String id) {
    String path = String.format(INPACKET_PATH, id);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * Requests a "POST OutPacket".
   * <pre>
   * {@literal
   * POST OutPacket.
   * ( POST <base_uri>/packets/out )
   * }
   * </pre>
   * @param body a Packet OUT.
   * @return response object.
   */
  public final Response postOutPacket(final OutPacket body) {
    String path = OUTPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return postObjectToNetwork(this.networkId, path, body);
  }

  /**
   * Requests a "GET OutPackets".
   * <pre>
   * {@literal
   * GET OutPackets.
   * ( GET <base_uri>/packets/out )
   * }
   * </pre>
   * @return value of the packet status.
   */
  public final PacketStatus getOutPackets() {
    String path = OUTPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(PacketStatus.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "DELETE OutPackets".
   * <pre>
   * {@literal
   * DELETE OutPackets.
   * ( DELETE <base_uri>/packets/out )
   * }
   * </pre>
   * @return response object.
   */
  public final Response delOutPackets() {
    String path = OUTPACKETS_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * GET OutPacket first pending currently.
   * <pre>
   * {@literal
   * ( GET <base_uri>/packets/out/head )
   * }
   * </pre>
   * @return value of the Packet OUT.
   */
  public final OutPacket getOutPacketHead() {
    String path = OUTPACKETS_HEAD_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return PacketObject.readOutPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * DELETE OutPacket first pending currently.
   * <pre>
   * {@literal
   * ( DELETE <base_uri>/packets/out/head )
   * }
   * </pre>
   * @return response object.
   */
  public final Response delOutPacketHead() {
    String path = OUTPACKETS_HEAD_PATH;
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  /**
   * GET OutPacket.
   * <pre>
   * {@literal
   * ( GET <base_uri>/packets/out/<packet_id> )
   * }
   * </pre>
   * @param packetId packet ID.
   * @return value of the packet OUT.
   */
  public final OutPacket getOutPacket(final String packetId) {
    String path = String.format(OUTPACKET_PATH, packetId);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    Response resp = getObjectToNetwork(this.networkId, path);
    if (resp == null) {
      return null;
    }
    try {
      return PacketObject.readOutPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "DELETE OutPacket".
   * <pre>
   * {@literal
   * DELETE OutPacket.
   * ( DELETE <base_uri>/packets/out/<packet_id> )
   * }
   * </pre>
   * @param id packet ID.
   * @return response object.
   */
  public final Response delOutPacket(
      final String id) {
    String path = String.format(OUTPACKET_PATH, id);
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));
    return delObjectToNetwork(this.networkId, path);
  }

  // //////////////////////////////////////
  //
  // Custom Request
  //
  // //////////////////////////////////////

  /**
   * Update attributes of node.
   * @param attributes map of attributes.
   * @return response object.
   */
  public final Response putAttributeOfNode(
      final Map<String, String> attributes) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    Response resp = new Response(Response.OK, null);
    if (attributes == null || attributes.size() == 0) {
      return resp;
    }
    try {
      Map<String, Node> nodes = this.getNodes();
      if (nodes.size() == 0) {
        return resp;
      }
      for (String nodeId : nodes.keySet()) {
        Node node = nodes.get(nodeId);
        boolean update = false;
        for (String attrKey : attributes.keySet()) {
          if (!node.getAttributes().containsKey(attrKey)
              || node.getAttribute(attrKey).equals(
                  attributes.get(attrKey))) {
            continue;
          }
          update = true;
          node.putAttribute(attrKey, attributes.get(attrKey));
        }
        if (update) {
          this.putNode(node);
        }
      }
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    return resp;
  }

  /**
   * Update status to "Failed" of all flows.
   * @return list of responses.
   */
  public final List<Response> putStatusFaildAllFlow() {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    List<Response> resps = new ArrayList<Response>();
    FlowSet flowSet = this.getFlowSet();
    if (flowSet == null
        || flowSet.getFlows().size() == 0) {
      resps.add(new Response(Response.OK, null));
      return resps;
    }
    for (String flowId : flowSet.getFlows().keySet()) {
      Flow flow = flowSet.getFlow(flowId);
      flow.setStatus(FlowObject.FlowStatus.FAILED.toString());
      resps.add(this.putFlow(flow));
    }
    return resps;
  }

  /**
   * Delete all flows.
   * @return list of response.
   */
  public final List<Response> deleteAllFlow() {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    List<Response> resps = new ArrayList<Response>();
    FlowSet flowSet = this.getFlowSet();
    if (flowSet == null
        || flowSet.getFlows().size() == 0) {
      resps.add(new Response(Response.OK, null));
      return resps;
    }
    for (String flowId : flowSet.getFlows().keySet()) {
      resps.add(this.delFlow(flowId));
    }
    return resps;
  }

  /**
   * Delete a topology.
   * @return list of response.
   */
  public final List<Response> deleteTopology() {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    List<Response> resps = new ArrayList<Response>();
    Topology topology = this.getTopology();
    if (topology == null) {
      resps.add(new Response(Response.OK, null));
      return resps;
    }

    /* TODO
    Topology emptyTopology = new Topology();
    emptyTopology.setVersion(topology.getVersion());
    this.putTopology(emptyTopology);
    */

    Map<String, Link> links = topology.getLinkMap();
    for (String linkId : links.keySet()) {
      resps.add(this.delLink(linkId));
    }
    Map<String, Node> nodes = topology.getNodeMap();
    for (String nodeId : nodes.keySet()) {
      resps.add(this.delNode(nodeId));
    }

    if (resps.size() == 0) {
      resps.add(new Response(Response.OK, null));
    }
    return resps;
  }

  // //////////////////////////////////////////////////
  //
  // common method ( private )
  //
  // //////////////////////////////////////////////////

  private Response postObjectToNetwork(
      final String nwcId, final String path, final Object body) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    try {
      Response resp = sendRequest(nwcId, Request.Method.POST, path, body);
      if (resp.isError("POST")) {
        String msg = String.format("invalid POST(%s) to %s: '%s' %s",
            resp.statusCode, nwcId, path, resp.getBodyValue());
        log.warn(msg);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  private Response putObjectToNetwork(
      final String nwcId, final String path, final Object body) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    try {
      Response resp = sendRequest(nwcId, Request.Method.PUT, path, body);
      if (resp.isError("PUT")) {
        String msg = String.format("invalid PUT(%s) to %s: '%s' %s",
            resp.statusCode, nwcId, path, resp.getBodyValue());
        log.warn(msg);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  private Response delObjectToNetwork(
      final String nwcId,
      final String path) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    try {
      Response resp = sendRequest(nwcId, Request.Method.DELETE, path,
          null);
      if (resp.isError("DELETE")) {
        String msg = String.format("invalid DELETE(%s) to %s: '%s' %s",
            resp.statusCode, nwcId, path, resp.getBodyValue());
        log.warn(msg);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  private Response getObjectToNetwork(
      String nwcId, String path) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    try {
      Response resp = sendRequest(nwcId, Request.Method.GET, path, null);
      if (resp.isError("GET")) {
        String msg = String.format("invalid GET(%s) to %s: '%s' %s",
            resp.statusCode, nwcId, path, resp.getBodyValue());
        log.warn(msg);
        return null;
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  private Response sendRequest(final String objId,
      final Request.Method method, final String path, final Object body) {
    log.debug(">> "
        + String.format(" [networkId : '%s']", this.networkId));

    Response rsp = null;
    Request req = new Request(objId, method, path, body);
    try {
      rsp = this.dispatcher.requestSync(req, sourceObjectId);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    return rsp;
  }

}
