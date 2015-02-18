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
 * Switch Port data class.
 *
 */
public class Port extends BaseObject implements Cloneable {
  private static final int MSG_NUM_MIN = 1;
  private static final int MSG_NUM_MAX = 7;
  private String type = "Port";
  private String portId;
  private String nodeId;
  private String outLink;
  private String inLink;

  /**
   * Constructor.
   */
  public Port() {
  }

  /**
   * Constructor.
   * @param portId port id that is unique in the Node.
   */
  public Port(String portId) {
    this.portId = portId;
  }

  /**
   * Constructor.
   * @param portId port id that is unique in the Node.
   * @param nodeId Port belongs to this node id.
   */
  public Port(String portId, String nodeId) {
    this(portId);
    this.nodeId = nodeId;
  }

  /**
   * Constructor.
   * @param version number of version.
   * @param portId port id that is unique in the Node.
   * @param nodeId Port belongs to this node id.
   */
  public Port(String version, String portId, String nodeId) {
    this(portId, nodeId);
    this.setVersion(version);
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
    this(version, portId, nodeId);
    this.outLink = outLink;
    this.inLink = inLink;
    this.putAttributes(attributes);
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
    if (this.nodeId == null
        || this.portId == null
        || this.type == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns a type of port.
   * @return type of port.
   */
  public String getType() {
    return type;
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
        case "port_id":
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            portId = null;
          } else {
            portId = upk.readString();
          }
          break;
        case "node_id":
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            nodeId = null;
          } else {
            nodeId = upk.readString();
          }
          break;
        case "out_link":
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            outLink = null;
          } else {
            outLink = upk.readString();
          }
          break;
        case "in_link":
          if (upk.getNextType() == ValueType.NIL) {
            upk.readNil();
            inLink = null;
          } else {
            inLink = upk.readString();
          }
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

    pk.write("port_id");
    pk.write(portId);

    pk.write("node_id");
    pk.write(nodeId);

    pk.write("out_link");
    if (outLink != null) {
      pk.write(outLink);
    } else {
      pk.writeNil();
    }

    pk.write("in_link");
    if (inLink != null) {
      pk.write(inLink);
    } else {
      pk.writeNil();
    }

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

    if (!(obj instanceof Port)) {
      return false;
    }

    Port portMessage = (Port) obj;

    try {
      if (portMessage.getType().equals(this.type)
          && portMessage.getVersion().equals(this.getVersion())
          && portMessage.getId().equals(this.portId)
          && portMessage.getNode().equals(this.nodeId)
          && String.format("%s", portMessage.getOutLink()).equals(
              String.format("%s", this.outLink))
          && String.format("%s", portMessage.getInLink()).equals(
              String.format("%s", this.inLink))
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
    sb.append("version", getVersion());
    sb.append("portId", portId);
    sb.append("nodeId", nodeId);
    sb.append("outLink", outLink);
    sb.append("inLink", inLink);
    sb.append("attributes", getAttributes());

    return sb.toString();

  }
}
