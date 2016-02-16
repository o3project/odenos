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

package org.o3project.odenos.component.linklayerizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.Boundary;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.io.Serializable;
import java.util.Map;

/**
 * LinkLayerizer Boundary class.
 * Connection rule between heterogeneous networks.
 *
 * <pre>
 * example(JSON)
 * {
 *   "id"            : "linklayerizer_boundary",
 *   "type"          : "LinkLayerizer",
 *   "lower_nw"      : "lower_nw",
 *   "upper_nw"      : "upper_nw",
 *   "lower_nw_node" : "lower_nw_node",
 *   "upper_nw_node" : "upper_nw_node",
 *   "lower_nw_port" : "lower_nw_port",
 *   "upper_nw_port" : "upper_nw_port",
 * }
 * </pre>
 */
@SuppressWarnings("serial")
@Message
public class LinkLayerizerBoundary extends Boundary implements Serializable {

  /** logger. */
  @Ignore
  private static final Logger log = LogManager
      .getLogger(LinkLayerizerBoundary.class);

  /** ID that is unique in the ODENOS. */
  @NotNullable
  private String id;
  /** Boundary Type. */
  private String type;

  /** networkComponent ID (ObjectProperty.id) */
  private String lowerNw;
  /** Connection Node.id */
  private String lowerNwNode;
  /** Connection Port.id */
  private String lowerNwPort;
  /** networkComponent ID (ObjectProperty.id) */
  private String upperNw;
  /** Connection Node.id */
  private String upperNwNode;
  /** Connection Port.id */
  private String upperNwPort;

  /**
   * Constructors.
   * @deprecated uses this constructor for MessagePack
   */
  @Deprecated
  public LinkLayerizerBoundary() {
  }

  /**
   * Constructors.
   * @param id ID for boundary.
   * @param type Type for Boundary.
   * @param lowerNw ID for lower network.
   * @param lowerNwNode ID for node in lower network.
   * @param lowerNwPort ID for port in lower node.
   * @param upperNw ID for upper network.
   * @param upperNwNode ID for node in upper network.
   * @param upperNwPort ID for port in upper node.
   */
  public LinkLayerizerBoundary(String id, String type,
      String lowerNw, String lowerNwNode, String lowerNwPort,
      String upperNw, String upperNwNode, String upperNwPort) {
    super(id, type);

    this.id = id;
    this.type = type;

    this.lowerNw = lowerNw;
    this.lowerNwNode = lowerNwNode;
    this.lowerNwPort = lowerNwPort;

    this.upperNw = upperNw;
    this.upperNwNode = upperNwNode;
    this.upperNwPort = upperNwPort;

  }

  /**
   * Get a parameter of id.
   * @return parameter of id.
   */
  public String getId() {
    return id;
  }

  /**
   * Set a parameter of id.
   * @param id Parameter of id.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get a parameter of type.
   * @return type Parameter of type.
   */
  public String getType() {
    return type;
  }

  /**
   * Set a parameter of type.
   * @param type Parameter of type.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get a parameter of lowerNw (ID for lower network).
   * @return Parameter of lowerNw.
   */
  public String getLowerNw() {
    return lowerNw;
  }

  /**
   * Set a parameter of lowerNw (ID for lower network).
   * @param lowerNw Parameter of lowerNw.
   */
  public void setLowerNw(String lowerNw) {
    this.lowerNw = lowerNw;
  }

  /**
   * Get a parameter of lowerNwNode (node in lower network).
   * @return Parameter of lowerNwNode (node in lower network).
   */
  public String getLowerNwNode() {
    return lowerNwNode;
  }

  /**
   * Set a parameter of lowerNwNode (node in lower network).
   * @param lowerNwNode Parameter of lowerNwNode (node in lower network).
   */
  public void setLowerNwNode(String lowerNwNode) {
    this.lowerNwNode = lowerNwNode;
  }

  /**
   * Get a parameter of lowerNwPort (port in lower network).
   * @return Parameter of lowerNwPort (port in lower network).
   */
  public String getLowerNwPort() {
    return lowerNwPort;
  }

  /**
   * Set a parameter of lowerNwPort (port in lower network) .
   * @param lowerNwPort Parameter of lowerNwPort (port in lower network).
   */
  public void setLowerNwPort(String lowerNwPort) {
    this.lowerNwPort = lowerNwPort;
  }

  /**
   * Get a parameter of upperNw (upper network).
   * @return Parameter of upperNw upper network).
   */
  public String getUpperNw() {
    return upperNw;
  }

  /**
   * Set a parameter of upper network.
   * @param upperNw Parameter of upper network.
   */
  public void setUpperNw(String upperNw) {
    this.upperNw = upperNw;
  }

  /**
   * Get a parameter of upperNwNode (node in upper network).
   * @return Parameter of upperNwNode (node in upper network).
   */
  public String getUpperNwNode() {
    return upperNwNode;
  }

  /**
   * Set a parameter of upperNwNode (node in upper network).
   * @param upperNwNode Parameter of upperNwNode (node in upper network).
   */
  public void setUpperNwNode(String upperNwNode) {
    this.upperNwNode = upperNwNode;
  }

  /**
   * Get a parameter of port in upper network.
   * @return upper_nw_port
   */
  public String getUpperNwPort() {
    return upperNwPort;
  }

  /**
   * Set a parameter of upperNwPort (port in upper network).
   * @param upperNwPort Parameter of upperNwPort (port in upper network).
   */
  public void setUpperNwPort(String upperNwPort) {
    this.upperNwPort = upperNwPort;
  }

  /**
   * Verify parameters.
   * @return boolean true if parameters are valid.
   */
  public boolean validate() {

    try {
      Validate.notEmpty(this.lowerNw, "lower_nw is empty");
      Validate.notEmpty(this.lowerNwNode, "lower_nw_node is empty");
      Validate.notEmpty(this.lowerNwPort, "lower_nw_port is empty");

      Validate.notEmpty(this.upperNw, "upper_nw is empty");
      Validate.notEmpty(this.upperNwNode, "upper_nw_node is empty");
      Validate.notEmpty(this.upperNwPort, "upper_nw_port is empty");

      return true;

    } catch (IllegalArgumentException ex) {
      log.warn(ex.getMessage(), ex);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.OdenosMessage#readValue(org.msgpack.type.Value)
   */
  @Override
  public boolean readValue(Value value) {

    try {
      MapValue map = value.asMapValue();

      Value idValue = map.get(ValueFactory.createRawValue("id"));
      Validate.notNull(idValue, "invalid value: id");
      setId(idValue.asRawValue().getString());

      Value typeValue = map.get(ValueFactory.createRawValue("type"));
      Validate.notNull(typeValue, "invalid value: type");
      setType(typeValue.asRawValue().getString());

      Value lowerNwValue = map.get(ValueFactory
          .createRawValue("lower_nw"));
      Validate.notNull(lowerNwValue, "invalid value: lower_nw");
      setLowerNw(lowerNwValue.asRawValue().getString());

      Value lowerNwNodeValue = map.get(ValueFactory
          .createRawValue("lower_nw_node"));
      Validate.notNull(lowerNwNodeValue,
          "invalid value: lower_nw_node");
      setLowerNwNode(lowerNwNodeValue.asRawValue().getString());

      Value lowerNwPortValue = map.get(ValueFactory
          .createRawValue("lower_nw_port"));
      Validate.notNull(lowerNwPortValue,
          "invalid value: lower_nw_port");
      setLowerNwPort(lowerNwPortValue.asRawValue().getString());

      Value upperNwValue = map.get(ValueFactory
          .createRawValue("upper_nw"));
      Validate.notNull(upperNwValue, "invalid value: upper_nw");
      setUpperNw(upperNwValue.asRawValue().getString());

      Value upperNwNodeValue = map.get(ValueFactory
          .createRawValue("upper_nw_node"));
      Validate.notNull(upperNwNodeValue,
          "invalid value: upper_nw_node");
      setUpperNwNode(upperNwNodeValue.asRawValue().getString());

      Value upperNwPortValue = map.get(ValueFactory
          .createRawValue("upper_nw_port"));
      Validate.notNull(upperNwPortValue,
          "invalid value: upper_nw_port");
      setUpperNwPort(upperNwPortValue.asRawValue().getString());

      return true;

    } catch (IllegalArgumentException ex) {
      log.error(ex.getMessage(), ex);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.OdenosMessage#writeValueSub(java.util.Map)
   */
  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (values == null) {
      log.error("values is null");
      throw new IllegalArgumentException("values is null");
    }

    values.put("id", ValueFactory.createRawValue(getId()));
    values.put("type", ValueFactory.createRawValue(getType()));
    values.put("lower_nw", ValueFactory.createRawValue(getLowerNw()));
    values.put("lower_nw_node",
        ValueFactory.createRawValue(getLowerNwNode()));
    values.put("lower_nw_port",
        ValueFactory.createRawValue(getLowerNwPort()));
    values.put("upper_nw", ValueFactory.createRawValue(getUpperNw()));
    values.put("upper_nw_node",
        ValueFactory.createRawValue(getUpperNwNode()));
    values.put("upper_nw_port",
        ValueFactory.createRawValue(getUpperNwPort()));

    return true;

  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    StringBuffer sb = new StringBuffer();
    sb.append(id).append(LinkLayerizer.SEPARATOR);
    sb.append(type).append(LinkLayerizer.SEPARATOR);
    sb.append(lowerNw).append(LinkLayerizer.SEPARATOR);
    sb.append(lowerNwNode).append(LinkLayerizer.SEPARATOR);
    sb.append(lowerNwPort).append(LinkLayerizer.SEPARATOR);
    sb.append(upperNw).append(LinkLayerizer.SEPARATOR);
    sb.append(upperNwNode).append(LinkLayerizer.SEPARATOR);
    sb.append(upperNwPort);

    return sb.toString().hashCode();
  }

  /* (non-Javadoc)
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

    if (!(obj instanceof LinkLayerizerBoundary)) {
      return false;
    }

    LinkLayerizerBoundary boundary = (LinkLayerizerBoundary) obj;

    if ((StringUtils.equals(id, boundary.getId()))
        && (StringUtils.equals(type, boundary.getType()))
        && (StringUtils.equals(lowerNw, boundary.getLowerNw()))
        && (StringUtils.equals(lowerNwNode, boundary.getLowerNwNode()))
        && (StringUtils.equals(lowerNwPort, boundary.getLowerNwPort()))
        && (StringUtils.equals(upperNw, boundary.getUpperNw()))
        && (StringUtils.equals(upperNwNode, boundary.getUpperNwNode()))
        && (StringUtils.equals(upperNwPort, boundary.getUpperNwPort()))) {
      return true;
    }

    return false;
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.Boundary#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("id", id);
    sb.append("type", type);
    sb.append("lowerNw", lowerNw);
    sb.append("lowerNwNode", lowerNwNode);
    sb.append("lowerNwPort", lowerNwPort);
    sb.append("upperNw", upperNw);
    sb.append("upperNwNode", upperNwNode);
    sb.append("upperNwPort", upperNwPort);

    return sb.toString();

  }
}
