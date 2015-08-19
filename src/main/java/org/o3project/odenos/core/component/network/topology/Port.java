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
import java.util.Objects;

/**
 * Switch Port data class.
 *
 */
public class Port extends BaseObject implements Cloneable {
  private static final int MSG_NUM_MIN = 1;
  private static final int MSG_NUM_MAX = 7;
  private String portId;
  private String nodeId;
  private String outLink;
  private String inLink;

  /* NetworkElements */
  public static final String TYPE = "type";
  public static final String VERSION = "version";
  public static final String NODE_ID = "node_id";
  public static final String PORT_ID = "port_id";
  public static final String IN_LINK = "in_link";
  public static final String OUT_LINK = "out_link";
  public static final String ATTRIBUTES = "attributes";

  /* AttrbuteElements */
  public static final String OPER_STATUS = "oper_status";
  public static final String MAX_BANDWIDTH = "max_bandwidth";
  public static final String UNRESERVED_BANDWIDTH = "unreserved_bandwidth";
  public static final String PHYSICAL_ID = "physical_id";
  public static final String VENDOR = "vendor";
  public static final String IS_BOUNDARY = "is_boundary";

  /**
   * Constructor.
   */
  public Port() {
    initElements(this.INITIAL_VERSION, null, null, null, null, null);
  }

  /**
   * Constructor.
   * @param portId port id that is unique in the Node.
   */
  public Port(String portId) {
    initElements(this.INITIAL_VERSION, portId, null, null, null, null);
  }

  /**
   * Constructor.
   * @param portId port id that is unique in the Node.
   * @param nodeId Port belongs to this node id.
   */
  public Port(String portId, String nodeId) {
    initElements(this.INITIAL_VERSION, portId, nodeId, null, null, null);
  }

  /**
   * Constructor.
   * @param version number of version.
   * @param portId port id that is unique in the Node.
   * @param nodeId Port belongs to this node id.
   */
  public Port(String version, String portId, String nodeId) {
    initElements(version, portId, nodeId, null, null, null);
  }

  /**
   * Constructor.
   * @param version string of version.
   * @param portId port id that is unique in the Node.
   * @param nodeId Port belongs to this node id.
   * @param outLink output link id.
   * @param inLink input link id.
   * @param attributes map of attributes.
   */
  public Port(String version, String portId, String nodeId,
      String outLink, String inLink, Map<String, String> attributes) {
    initElements(version, portId, nodeId, outLink, inLink, attributes);
  }

  protected void initElements(
      String version, String portId, String nodeId,
      String outLink, String inLink, Map<String, String> attributes) {

    this.setType("Port");
    this.setVersion(version);
    this.setNode(nodeId);
    this.setId(portId);
    this.setOutLink(outLink);
    this.setInLink(inLink);
    this.putAttributes(attributes);

    if(attributes != null) {
      this.putAttributes(attributes);
    }
    if(!this.isAttribute(OPER_STATUS)) {
      this.putAttribute(OPER_STATUS, STATUS_UP);
    }
    if(!this.isAttribute(PHYSICAL_ID)) {
      this.putAttribute(PHYSICAL_ID, portId + "@" + nodeId);
    }
    if(!this.isAttribute(VENDOR)) {
      this.putAttribute(VENDOR, "unknown");
    }
    if(!this.isAttribute(IS_BOUNDARY)) {
      this.putAttribute(IS_BOUNDARY, "false");
    }
  }

  /**
   * Constructor.
   * @param msg port message.
   */
  public Port(Port msg) {
    this(msg.getVersion(), msg.getId(), msg.getNode(), msg.getOutLink(),
        msg.getInLink(), new HashMap<String, String>(
            msg.getAttributes()));
  }

  /**
   * Confirm the parameter.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    if (this.getNode() == null
        || this.getId() == null
        || this.getType() == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a port ID.
   * @return port ID.
   */
  public String getId() {
    return portId;
  }

  /**
   * Sets a port ID.
   * @param portId port ID.
   */
  public void setId(String portId) {
    this.portId = portId;
  }

  /**
   * Returns a node ID.
   * @return node ID.
   */
  public String getNode() {
    return nodeId;
  }

  /**
   * Sets a node ID.
   * @param nodeId node ID.
   */
  public void setNode(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Returns a output link ID.
   * @return output link ID.
   */
  public String getOutLink() {
    return outLink;
  }

  /**
   * Sets a output link ID.
   * @param linkId output link ID.
   */
  public void setOutLink(String linkId) {
    outLink = linkId;
  }

  /**
   * Returns a input link ID.
   * @return input link ID.
   */
  public String getInLink() {
    return inLink;
  }

  /**
   * Sets a input link ID.
   * @param linkId input link ID.
   */
  public void setInLink(String linkId) {
    inLink = linkId;
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
          this.setType(upk.readString());
          break;
        case VERSION:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            setVersion("0");
          } else {
            setVersion(upk.readString());
          }
          break;
        case PORT_ID:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            portId = null;
          } else {
            portId = upk.readString();
          }
          break;
        case NODE_ID:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            nodeId = null;
          } else {
            nodeId = upk.readString();
          }
          break;
        case OUT_LINK:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            outLink = null;
          } else {
            outLink = upk.readString();
          }
          break;
        case IN_LINK:
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            inLink = null;
          } else {
            inLink = upk.readString();
          }
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

    pk.write(PORT_ID);
    pk.write(portId);

    pk.write(NODE_ID);
    pk.write(nodeId);

    pk.write(OUT_LINK);
    if (outLink != null) {
      pk.write(outLink);
    } else {
      pk.writeNil();
    }

    pk.write(IN_LINK);
    if (inLink != null) {
      pk.write(inLink);
    } else {
      pk.writeNil();
    }

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

    if (!(obj instanceof Port)) {
      return false;
    }

    Port portMessage = (Port) obj;

    try {
      if (portMessage.getType().equals(this.getType())
          && portMessage.getVersion().equals(this.getVersion())
          && portMessage.getId().equals(this.portId)
          && portMessage.getNode().equals(this.nodeId)
          && Objects.equals(portMessage.getOutLink(), this.outLink)
          && Objects.equals(portMessage.getInLink(), this.inLink)
          && portMessage.getAttributes().equals(this.getAttributes())) {
        return true;
      }
    } catch (NullPointerException e) {
      //e.printStackTrace();
    }
    return false;
  }

  @Override
  public Port clone() {
    return new Port(this);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append(VERSION, getVersion());
    sb.append(PORT_ID, portId);
    sb.append(NODE_ID, nodeId);
    sb.append(OUT_LINK, outLink);
    sb.append(IN_LINK, inLink);
    sb.append(ATTRIBUTES, getAttributes());

    return sb.toString();
  }
}
