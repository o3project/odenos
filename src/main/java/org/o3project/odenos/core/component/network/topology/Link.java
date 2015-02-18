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

import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.packer.Packer;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Link class.
 *
 */
public class Link extends BaseObject implements Cloneable {
  private static final int MSG_NUM_MIN = 5;
  private static final int MSG_NUM_MAX = 8;
  private String type = "Link";
  private String linkId;
  private String srcNode;
  private String srcPort;
  private String dstNode;
  private String dstPort;

  /**
   * Constructor.
   */
  public Link() {
  }

  /**
   * Constructor.
   * @param linkId link ID. ID that is unique in the Network.
   */
  public Link(String linkId) {
    this.linkId = linkId;
  }

  /**
   * Constructor.
   * @param linkId Link ID. ID that is unique in the Network.
   * @param srcNode source node ID.
   * @param srcPort source port ID.
   * @param dstNode destination node ID.
   * @param dstPort destination port ID.
   */
  public Link(String linkId, String srcNode,
      String srcPort, String dstNode, String dstPort) {
    this.linkId = linkId;
    this.srcNode = srcNode;
    this.srcPort = srcPort;
    this.dstNode = dstNode;
    this.dstPort = dstPort;
  }

  /**
   * Constructor.
   * @param version version.
   * @param linkId Link ID. ID that is unique in the Network.
   * @param srcNode source node ID.
   * @param srcPort source port ID.
   * @param dstNode destination node ID.
   * @param dstPort destination port ID.
   * @param attributes map of attributes.
   */
  public Link(String version, String linkId, String srcNode,
      String srcPort, String dstNode, String dstPort,
      Map<String, String> attributes) {
    this(linkId, srcNode, srcPort, dstNode, dstPort);
    this.setVersion(version);
    this.putAttributes(attributes);
  }

  /**
   * Constructor.
   * @param msg Link message.
   */
  public Link(Link msg) {
    this(msg.getVersion(), msg.getId(), msg.getSrcNode(), msg.getSrcPort(),
        msg.getDstNode(), msg.getDstPort(),
        new HashMap<String, String>(msg.getAttributes()));
  }

  /**
   * Confirm the parameter.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    if (this.linkId == null
        || this.srcNode == null || this.srcPort == null
        || this.dstNode == null || this.dstPort == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a type of link.
   * @return type of link.
   */
  public String getType() {
    return type;
  }

  /**
   * Returns a link ID.
   * @return link ID.
   */
  public String getId() {
    return linkId;
  }

  /**
   * Sets a link ID.
   * @param linkId link ID.
   */
  public void setId(String linkId) {
    this.linkId = linkId;
  }

  /**
   * Returns a source node ID.
   * @return source node ID.
   */
  public String getSrcNode() {
    return srcNode;
  }

  /**
   * Returns a source port ID.
   * @return source port ID.
   */
  public String getSrcPort() {
    return srcPort;
  }

  /**
   * Returns a destination node ID.
   * @return destination node ID.
   */
  public String getDstNode() {
    return dstNode;
  }

  /**
   * Returns a destination port ID.
   * @return destination port ID.
   */
  public String getDstPort() {
    return dstPort;
  }

  /**
   * Set parameter of ports.
   * @param srcNode source node id.
   * @param srcPort source port id.
   * @param dstNode destination node id.
   * @param dstPort destination port id.
   */
  public void setPorts(String srcNode, String srcPort, String dstNode,
      String dstPort) {
    this.srcNode = srcNode;
    this.srcPort = srcPort;
    this.dstNode = dstNode;
    this.dstPort = dstPort;
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
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            setVersion("0");
          } else {
            setVersion(upk.readString());
          }
          break;
        case "link_id":
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            linkId = null;
          } else {
            linkId = upk.readString();
          }
          break;
        case "src_node":
          srcNode = upk.readString();
          break;
        case "src_port":
          srcPort = upk.readString();
          break;
        case "dst_node":
          dstNode = upk.readString();
          break;
        case "dst_port":
          dstPort = upk.readString();
          break;
        case "attributes":
          putAttributes(upk.read(tMap(TString, TString)));
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

    pk.write("link_id");
    pk.write(linkId);

    pk.write("src_node");
    pk.write(srcNode);

    pk.write("src_port");
    pk.write(srcPort);

    pk.write("dst_node");
    pk.write(dstNode);

    pk.write("dst_port");
    pk.write(dstPort);

    pk.write("attributes");
    pk.write(getAttributes());

    pk.writeMapEnd();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Link)) {
      return false;
    }

    Link linkMessage = (Link) obj;

    if (linkMessage.getType().equals(this.type)
        && linkMessage.getVersion().equals(this.getVersion())
        && linkMessage.getId().equals(this.linkId)
        && linkMessage.getSrcNode().equals(this.srcNode)
        && linkMessage.getSrcPort().equals(this.srcPort)
        && linkMessage.getDstNode().equals(this.dstNode)
        && linkMessage.getDstPort().equals(this.dstPort)
        && linkMessage.getAttributes().equals(this.getAttributes())) {
      return true;
    }

    return false;
  }

  @Override
  public Link clone() {
    return new Link(this);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("version", getVersion());
    sb.append("linkId", linkId);
    sb.append("srcNode", srcNode);
    sb.append("srcPort", srcPort);
    sb.append("dstNode", dstNode);
    sb.append("dstPort", dstPort);
    sb.append("attributes", getAttributes());

    return sb.toString();
  }

}