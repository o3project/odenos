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
  private String linkId;
  private String srcNode;
  private String srcPort;
  private String dstNode;
  private String dstPort;

  /* NetworkElements */
  public static final String TYPE = "type";
  public static final String VERSION = "version";
  public static final String LINK_ID = "link_id";
  public static final String SRC_NODE = "src_node";
  public static final String SRC_PORT = "src_port";
  public static final String DST_NODE = "dst_node";
  public static final String DST_PORT = "dst_port";
  public static final String ATTRIBUTES = "attributes";

  /* AttrbuteElements */
  public static final String OPER_STATUS = "oper_status";
  public static final String COST = "cost";
  public static final String REQ_LATENCY = "req_latency";
  public static final String LATENCY = "latency";
  public static final String REQ_BANDWIDTH = "req_bandwidth";
  public static final String MAX_BANDWIDTH = "max_bandwidth";
  public static final String UNRESERVED_BANDWIDTH = "unreserved_bandwidth";
  public static final String ESTABLISHMENT_STATUS = "establishment_status";


  /**
   * Constructor.
   */
  public Link() {
    initElements(this.INITIAL_VERSION, 
        null, null, null, null, null, null);
  }

  /**
   * Constructor.
   * @param linkId link ID. ID that is unique in the Network.
   */
  public Link(String linkId) {
    initElements(this.INITIAL_VERSION, 
        linkId, null, null, null, null, null);
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
    initElements(this.INITIAL_VERSION, 
        linkId, srcNode, srcPort, dstNode, dstPort, null);
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
    initElements(version, linkId, srcNode, srcPort, dstNode, dstPort, attributes);
  }

  protected void initElements(
      String version, String linkId, String srcNode,
      String srcPort, String dstNode, String dstPort,
      Map<String, String> attributes) {

    this.setType("Link");
    this.setVersion(version);
    this.setId(linkId);
    this.setPorts(srcNode, srcPort, dstNode, dstPort);

    if(attributes != null) {
      this.putAttributes(attributes);
    }
    if(!this.isAttribute(COST)) {
      this.putAttribute(COST, "1");
    }
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
        case TYPE:
          setType(upk.readString());
          break;
        case VERSION:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            setVersion("0");
          } else {
            setVersion(upk.readString());
          }
          break;
        case LINK_ID:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            linkId = null;
          } else {
            linkId = upk.readString();
          }
          break;
        case SRC_NODE:
          srcNode = upk.readString();
          break;
        case SRC_PORT:
          srcPort = upk.readString();
          break;
        case DST_NODE:
          dstNode = upk.readString();
          break;
        case DST_PORT:
          dstPort = upk.readString();
          break;
        case ATTRIBUTES:
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

    pk.write(TYPE);
    pk.write(getType());

    pk.write(VERSION);
    pk.write(getVersion());

    pk.write(LINK_ID);
    pk.write(linkId);

    pk.write(SRC_NODE);
    pk.write(srcNode);

    pk.write(SRC_PORT);
    pk.write(srcPort);

    pk.write(DST_NODE);
    pk.write(dstNode);

    pk.write(DST_PORT);
    pk.write(dstPort);

    pk.write(ATTRIBUTES);
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

    if (linkMessage.getType().equals(this.getType())
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
    sb.append(VERSION, getVersion());
    sb.append(LINK_ID, linkId);
    sb.append(SRC_NODE, srcNode);
    sb.append(SRC_PORT, srcPort);
    sb.append(DST_NODE, dstNode);
    sb.append(DST_PORT, dstPort);
    sb.append(ATTRIBUTES, getAttributes());

    return sb.toString();
  }

}
