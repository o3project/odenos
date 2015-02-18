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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * This represent the overall topology of Network.
 *
 */
public class Topology extends BaseObject implements Cloneable {
  private static final int MSG_NUM_MIN = 3;
  private static final int MSG_NUM_MAX = 4;
  public String type = "Topology";
  public Map<String, Node> nodes;
  public Map<String, Link> links;

  /**
   * Constructor.
   */
  public Topology() {
    nodes = new HashMap<String, Node>();
    links = new HashMap<String, Link>();
  }

  /**
   * Constructor.
   * @param nodes map of nodes.
   * @param links map of links.
   */
  public Topology(Map<String, Node> nodes,
      Map<String, Link> links) {
    this.nodes = nodes;
    this.links = links;
    if (this.nodes == null) {
      this.nodes = new HashMap<String, Node>();
    }
    if (this.links == null) {
      this.links = new HashMap<String, Link>();
    }
  }

  /**
   * Constructor.
   * @param version number of version.
   * @param nodes map of nodes.
   * @param links map of links.
   */
  public Topology(String version,
      Map<String, Node> nodes,
      Map<String, Link> links) {
    this(nodes, links);
    this.setVersion(version);
  }

  /**
   * Confirm the parameter.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    if (links == null) {
      return false;
    }
    for (Map.Entry<String, Link> linkEntry : links.entrySet()) {
      Link linkMsg = linkEntry.getValue();
      if (nodes == null) {
        return false;
      }
      Node srcNode = nodes.get(linkMsg.getSrcNode());
      if (srcNode == null) {
        return false;
      }
      if (srcNode.getPort(linkMsg.getSrcPort()) == null) {
        return false;
      }
      Node dstNode = nodes.get(linkMsg.getDstNode());
      if (dstNode == null) {
        return false;
      }
      if (dstNode.getPort(linkMsg.getDstPort()) == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get nodes.
   * @return map of nodes.
   */
  public Map<String, Node> getNodeMap() {
    if (nodes == null) {
      return new HashMap<String, Node>();
    }
    return nodes;
  }

  /**
   * Get links.
   * @return map of links.
   */
  public Map<String, Link> getLinkMap() {
    if (links == null) {
      return new HashMap<String, Link>();
    }
    return links;
  }

  private String getUniqueNodeId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (nodes.get(id) != null);

    return id;
  }

  /**
   * Create a node.
   * @param msg node message.
   * @return credted the node.
   */
  public Node createNode(Node msg) {
    String nodeId;
    if (msg == null || msg.getId() == null) {
      // automatic numbering if node_id is undefined
      nodeId = getUniqueNodeId();
    } else {
      nodeId = msg.getId();
    }
    Node node = getNode(nodeId);
    if (node == null) {
      // create new Node
      node = new Node(nodeId);
      node.setVersion(INITIAL_VERSION);
      nodes.put(nodeId, node);
      updateVersion();
    }
    if (msg != null) {
      node.setPorts(msg.getPortMap());
      node.putAttributes(msg.getAttributes());
    }
    node.updateVersion();

    return node;
  }

  private String getUniqueLinkId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (links.get(id) != null);

    return id;
  }

  /**
   * Create new link between specific node and port, with given link ID.
   *
   * @param msg message of link.
   * @return new Link object.
   */
  public Link createLink(Link msg) {
    String linkId;
    if (msg == null || msg.getId() == null) {
      // automatic numbering if link_id is undefined
      linkId = getUniqueLinkId();
    } else {
      linkId = msg.getId();
    }
    Link link = getLink(linkId);
    Port port;
    if (link == null) {
      link = new Link(linkId);
      link.setVersion(INITIAL_VERSION);
      links.put(linkId, link);
      updateVersion();
    }
    if (msg != null) {
      link.setPorts(msg.getSrcNode(), msg.getSrcPort(),
          msg.getDstNode(), msg.getDstPort());
      link.putAttributes(msg.getAttributes());
      port = nodes.get(msg.getSrcNode()).getPort(msg.getSrcPort());
      port.setOutLink(linkId);
      port.updateVersion();
      port = nodes.get(msg.getDstNode()).getPort(msg.getDstPort());
      port.setInLink(linkId);
      port.updateVersion();
    }
    link.updateVersion();

    return link;
  }

  /**
   * delete a node.
   * @param node target node.
   * @return deleted the node.
   */
  public boolean deleteNode(Node node) {
    if (node == null) {
      return false;
    }

    for (Port port : node.getPortMap().values()) {
      // still has link
      if (port.getInLink() != null || port.getOutLink() != null) {
        return false;
      }
    }

    if (nodes.remove(node.getId()) == null) {
      return false;
    }
    updateVersion();
    return true;
  }

  /**
   * Delete a port.
   * @param nodeId node ID.
   * @param portId port ID.
   * @return true if the port was exist.
   */
  public boolean deletePort(String nodeId, String portId) {
    return deletePort(getPort(nodeId, portId));
  }

  private boolean deletePort(Port port) {
    if (port == null) {
      return false;
    }

    Node node = getNode(port.getNode());
    if (node == null || node.deletePort(port) == null) {
      return false;
    }
    node.updateVersion();

    return true;
  }

  /**
   * Delete a link.
   * @param linkId link ID.
   * @return true if the link was exist.
   */
  public boolean deleteLink(String linkId) {
    return deleteLink(getLink(linkId));
  }

  /**
   * Delete a link.
   * @param link target link.
   * @return deleted the link.
   */
  public boolean deleteLink(Link link) {
    if (link == null) {
      return false;
    }

    if (link.getSrcPort() != null) {
      Port port = nodes.get(link.getSrcNode()).getPort(link.getSrcPort());
      port.setOutLink(null);
      port.updateVersion();
    }

    if (link.getDstPort() != null) {
      Port port = nodes.get(link.getDstNode()).getPort(link.getDstPort());
      port.setInLink(null);
      port.updateVersion();
    }

    if (links.remove(link.getId()) == null) {
      return false;
    }

    updateVersion();
    return true;
  }

  /**
   * Get a node with specific Node ID.
   *
   * @param nodeId
   *            Node ID to find.
   * @return Node object which has specific ID. null if not found.
   */
  public Node getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  /**
   * Get a port with specific Node ID and Port ID.
   *
   * @param nodeId node id.
   * @param portId port id.
   * @return port object
   */
  public Port getPort(String nodeId, String portId) {
    Node node = getNode(nodeId);
    if (node == null) {
      return null;
    }

    return node.getPort(portId);
  }

  /**
   * Get a link with specific Link ID.
   *
   * @param linkId link id.
   * @return link object
   */
  public Link getLink(String linkId) {
    return links.get(linkId);
  }

  /**
   * Get messages of nodes.
   * @param query queries string.
   * @return map of nodes.
   */
  public Map<String, Node> getNodeMessages(NodeQuery query) {
    Map<String, Node> nodes = new HashMap<String, Node>();
    for (Node node : this.nodes.values()) {
      if (query.matchExactly(node)) {
        nodes.put(node.getId(), node);
      }
    }
    return nodes;
  }

  /**
   * Get messages of ports.
   * @param query queries string.
   * @param nodeId node id.
   * @return map of ports.
   */
  public Map<String, Port> getPortMessages(PortQuery query, String nodeId) {
    Node node = this.getNode(nodeId);
    if (node == null) {
      return new HashMap<String, Port>();
    }
    return node.getPortMessages(query);
  }

  /**
   * Get message of link.
   * @param query queries string.
   * @return map of link.
   */
  public Map<String, Link> getLinkMessages(LinkQuery query) {
    Map<String, Link> links = new HashMap<String, Link>();
    for (Link link : this.links.values()) {
      if (query.matchExactly(link)) {
        links.put(link.getId(), link);
      }
    }
    return links;
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {
    int size = upk.readMapBegin();

    if (size < MSG_NUM_MIN || MSG_NUM_MAX < size) {
      throw new IOException();
    }

    while (size-- > 0) {
      switch (upk.readString()) {
        case "type":
          type = upk.readString();
          break;
        case "version":
          setVersion(upk.readString());
          break;
        case "nodes":
          nodes.clear();
          int nodesSize = upk.readMapBegin();
          while (nodesSize-- > 0) {
            String nodeId = upk.readString();
            Node node = upk.read(Node.class);
            nodes.put(nodeId, node);
          }
          upk.readMapEnd();
          break;
        case "links":
          links.clear();
          int linksSize = upk.readMapBegin();
          while (linksSize-- > 0) {
            String linkId = upk.readString();
            Link link = upk.read(Link.class);
            links.put(linkId, link);
          }
          upk.readMapEnd();
          break;
        default:
          break;
      }
    }
    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeMapBegin(MSG_NUM_MAX);

    pk.write("type");
    pk.write(type);

    pk.write("version");
    pk.write(getVersion());

    pk.write("nodes");
    pk.write(nodes);

    pk.write("links");
    pk.write(links);

    pk.writeMapEnd();
  }

  @Override
  public Topology clone() {

    Map<String, Link> links = new HashMap<String, Link>();
    if (links != null) {
      for (Entry<String, Link> entry : this.links.entrySet()) {
        links.put(entry.getKey(), new Link(entry.getValue()));
      }
    }
    Map<String, Node> nodes = new HashMap<String, Node>();
    if (nodes != null) {
      for (Entry<String, Node> entry : this.nodes.entrySet()) {
        nodes.put(entry.getKey(), new Node(entry.getValue()));
      }
    }

    return new Topology(nodes, links);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("type", type);
    sb.append("version", getVersion());
    sb.append("nodes", nodes);
    sb.append("links", links);

    return sb.toString();
  }

}
