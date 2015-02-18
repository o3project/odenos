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

package org.o3project.odenos.core.component.network.flow.basic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.Map;

/**
 * Class representing matching condition of BasicFlow.
 *
 */
public class BasicFlowMatch extends OdenosMessage {

  public static final String IN_NODE = "in_node";
  public static final String IN_PORT = "in_port";

  public String inNode = null;
  public String inPort = null;

  protected boolean wcInPort = true;

  /**
   * Constructor.
   */
  public BasicFlowMatch() {
  }

  /**
   * Constructor.
   * @param inNode ID for node.
   * @param inPort ID for port in node.
   */
  public BasicFlowMatch(String inNode, String inPort) {
    this.inNode = inNode;
    this.inPort = inPort;
    this.wcInPort = false;
  }

  /**
   * Confirm the parameter.
   * @return true if parameter is valid.
   */
  public boolean validate() {
    if (inNode.equals("")) {
      return false;
    }
    if (!wcInPort && inPort.equals("")) {
      return false;
    }
    return true;
  }

  /**
   * Returns a type.
   * @return type.
   */
  public String getType() {
    return "BasicFlowMatch";
  }

  /**
   * Returns a port ID.
   * @return port ID.
   */
  public String getInPort() {
    return inPort;
  }

  /**
   * Sets a port ID.
   * @param inPort port ID.
   */
  public void setInPort(String inPort) {
    this.wcInPort = false;
    this.inPort = inPort;
  }

  /**
   * Returns a node ID.
   * @return node ID.
   */
  public String getInNode() {
    return inNode;
  }

  /**
   * Sets a node Id.
   * @param inNode node ID.
   */
  public void setInNode(String inNode) {
    this.inNode = inNode;
  }

  @Override
  public boolean readValue(Value value) {
    Value[] values = value.asMapValue().getKeyValueArray();
    for (int i = 0; i < values.length; i += 2) {
      Value val;
      switch (values[i].asRawValue().getString()) {
        case IN_NODE:
          val = values[i + 1];
          if (!val.isNilValue()) {
            inNode = val.asRawValue().getString();
          }
          break;
        case IN_PORT:
          val = values[i + 1];
          if (!val.isNilValue()) {
            inPort = val.asRawValue().getString();
          }
          wcInPort = false;
          break;
        default:
          break;
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put("type", ValueFactory.createRawValue(getType()));
    values.put(IN_NODE, ValueFactory.createRawValue(getInNode()));
    if (!wcInPort) {
      values.put(IN_PORT, ValueFactory.createRawValue(getInPort()));
    }

    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof BasicFlowMatch)) {
      return false;
    }

    final BasicFlowMatch obj2 = (BasicFlowMatch) obj;

    if (!StringUtils.equals(obj2.getType(), this.getType())
        || !StringUtils.equals(obj2.getInNode(), this.inNode)
        || !StringUtils.equals(obj2.getInPort(), this.inPort)) {
      return false;
    }

    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#clone()
   */
  @Override
  public BasicFlowMatch clone() {
    BasicFlowMatch match = new BasicFlowMatch();
    match.setInNode(inNode);
    if (!wcInPort) {
      match.setInPort(inPort);
    }
    return match;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("inNode", inNode);
    sb.append("inPort", inPort);
    // sb.append("wcInPort", wcInPort);

    return sb.toString();
  }

}