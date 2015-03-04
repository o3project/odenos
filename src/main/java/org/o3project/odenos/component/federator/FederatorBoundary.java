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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.Boundary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Federator Boundary class.
 * Connection rule between heterogeneous networks.
 *
 * <pre>
 * example(JSON)
 * {
 *   "id"      : "federator1_boundary1",
 *   "type"    : "Federator",
 *   "network1": "original_network1",
 *   "network2": "original_network2",
 *   "node1"   : "original_network1_node1",
 *   "node2"   : "original_network2_node2",
 *   "port1"   : "original_network1_port1",
 *   "port2"   : "original_network2_port2",
 * }
 * </pre>
 *
 */
@Message
public class FederatorBoundary extends Boundary {

  /** logger. */
  @Ignore
  private static final Logger logger = LoggerFactory.getLogger(FederatorBoundary.class);

  /** ID that is unique in the ODENOS. */
  @NotNullable
  private String id;
  /** Boundary Type is "Federator". */
  private String type;

  /** network ID (network1). */
  private String network1;
  /** network ID (network2). */
  private String network2;
  /** node ID on network1. */
  private String node1;
  /** node ID on network2. */
  private String node2;
  /** port ID on network1. */
  private String port1;
  /** port ID on network2. */
  private String port2;

  /**
   * Constructor.
   * @deprecated @see #FederatorBoundary(String, String, String, String, String, String, String, String)
   */
  @Deprecated
  public FederatorBoundary() {
  }

  /**
   * Constructor.
   * @param id ID that is unique in the ODENOS
   * @param type Boundary Type is "Federator"
   * @param network1 network ID (network1).
   * @param node1 node ID on network1.
   * @param port1 port ID on network1
   * @param network2 network ID (network2).
   * @param node2 node ID on network2.
   * @param port2 port ID on network2.
   */
  public FederatorBoundary(String id, String type,
      String network1, String node1, String port1,
      String network2, String node2, String port2) {
    super(id, type);

    if ((StringUtils.isBlank(id)) || (StringUtils.isBlank(type))) {
      throw new IllegalArgumentException("federator parameter is null");
    }

    if ((StringUtils.isBlank(network1))
        || (StringUtils.isBlank(node1))
        || (StringUtils.isBlank(port1))) {
      throw new IllegalArgumentException("component1 parameter is null");
    }

    if ((StringUtils.isBlank(network2))
        || (StringUtils.isBlank(node2))
        || (StringUtils.isBlank(port2))) {
      throw new IllegalArgumentException("component2 parameter is null");
    }

    this.id = id;
    this.type = type;

    this.network1 = network1;
    this.node1 = node1;
    this.port1 = port1;

    this.network2 = network2;
    this.node2 = node2;
    this.port2 = port2;

  }

  /**
   * Returns true if all parameters contains.
   * @param network parameter of network
   * @param node parameter of node
   * @param port parameter of port
   * @return true if all parameters contains.
   */
  public boolean isContains(String network, String node, String port) {

    if ((network1.equals(network)) && (node1.equals(node))
        && (port1.equals(port))) {
      return true;
    }

    if ((network2.equals(network)) && (node2.equals(node))
        && (port2.equals(port))) {
      return true;
    }

    return false;
  }

  /**
   * Get a parameter of id.
   * @return ID for boundary.
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Set a parameter of id.
   * @param id parameter of id
   */
  @Override
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get a parameter of type.
   * @return Type of Object.
   */
  @Override
  public String getType() {
    return type;
  }

  /**
   * Set a parameter of type.
   * @param type parameter of type
   */
  @Override
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get a parameter of network1.
   * @return network1
   */
  public String getNetwork1() {
    return network1;
  }

  /**
   * Set a parameter of network1.
   * @param network1 parameter of network1
   */
  public void setNetwork1(String network1) {
    this.network1 = network1;
  }

  /**
   * Get a parameter of network2.
   * @return network2
   */
  public String getNetwork2() {
    return network2;
  }

  /**
   * Set a parameter of network2..
   * @param network2 parameter of network2
   */
  public void setNetwork2(String network2) {
    this.network2 = network2;
  }

  /**
   * Get a parameter of node1.
   * @return node1
   */
  public String getNode1() {
    return node1;
  }

  /**
   * Set a parameter of node1.
   * @param node1 parameter of node1
   */
  public void setNode1(String node1) {
    this.node1 = node1;
  }

  /**
   * Get a parameter of node2.
   * @return node2
   */
  public String getNode2() {
    return node2;
  }

  /**
   * Set a parameter of node2.
   * @param node2 parameter of node2
   */
  public void setNode2(String node2) {
    this.node2 = node2;
  }

  /**
   * Get a parameter of port1.
   * @return port1
   */
  public String getPort1() {
    return port1;
  }

  /**
   * Set a parameter of port1.
   * @param port1 parameter of port1
   */
  public void setPort1(String port1) {
    this.port1 = port1;
  }

  /**
   * Get a parameter of port2.
   * @return port2
   */
  public String getPort2() {
    return port2;
  }

  /**
   * Set a parameter of port2.
   * @param port2 parameter of port2
   */
  public void setPort2(String port2) {
    this.port2 = port2;
  }

  /**
   * verify parameters.
   * @return boolean true if parameters are valid.
   */
  public boolean validate() {
    if (this.network1 == null || this.network2 == null
        || this.node1 == null || this.node2 == null
        || this.port1 == null || this.port2 == null) {
      return false;
    }
    if (this.network1.equals(this.network2)) {
      return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.network.OdenosMessage#)readValue(org.msgpack.type.Value)
   */
  @Override
  public boolean readValue(Value value) {

    MapValue map = value.asMapValue();

    Value idValue = map.get(ValueFactory.createRawValue("id"));
    if (idValue == null || idValue.isNilValue()) {
      logger.error("invalid value: id");
      return false;
    }
    setId(idValue.asRawValue().getString());

    Value typeValue = map.get(ValueFactory.createRawValue("type"));
    if (typeValue == null || typeValue.isNilValue()) {
      logger.error("invalid value: type");
      return false;
    }
    setType(typeValue.asRawValue().getString());

    Value network1Value = map.get(ValueFactory.createRawValue("network1"));
    if (network1Value == null || network1Value.isNilValue()) {
      logger.error("invalid value: network1");
      return false;
    }
    setNetwork1(network1Value.asRawValue().getString());

    Value node1Value = map.get(ValueFactory.createRawValue("node1"));
    if (node1Value == null || node1Value.isNilValue()) {
      logger.error("invalid value: node1");
      return false;
    }
    setNode1(node1Value.asRawValue().getString());

    Value port1Value = map.get(ValueFactory.createRawValue("port1"));
    if (port1Value == null || port1Value.isNilValue()) {
      logger.error("invalid value: port1");
      return false;
    }
    setPort1(port1Value.asRawValue().getString());

    Value network2Value = map.get(ValueFactory.createRawValue("network2"));
    if (network2Value == null || network2Value.isNilValue()) {
      logger.error("invalid value: network2");
      return false;
    }
    setNetwork2(network2Value.asRawValue().getString());

    Value node2Value = map.get(ValueFactory.createRawValue("node2"));
    if (node2Value == null || node2Value.isNilValue()) {
      logger.error("invalid value: node2");
      return false;
    }
    setNode2(node2Value.asRawValue().getString());

    Value port2Value = map.get(ValueFactory.createRawValue("port2"));
    if (port2Value == null || port2Value.isNilValue()) {
      logger.error("invalid value: port2");
      return false;
    }
    setPort2(port2Value.asRawValue().getString());

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.network.OdenosMessage#writeValueSub(java.util.Map)
   */
  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (values == null) {
      logger.error("values is null");
      throw new IllegalArgumentException("values is null");
    }

    values.put("id", ValueFactory.createRawValue(getId()));
    values.put("type", ValueFactory.createRawValue(getType()));
    values.put("network1", ValueFactory.createRawValue(getNetwork1()));
    values.put("node1", ValueFactory.createRawValue(getNode1()));
    values.put("port1", ValueFactory.createRawValue(getPort1()));
    values.put("network2", ValueFactory.createRawValue(getNetwork2()));
    values.put("node2", ValueFactory.createRawValue(getNode2()));
    values.put("port2", ValueFactory.createRawValue(getPort2()));

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Boundary#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("id", id);
    sb.append("type", type);
    sb.append("network1", network1);
    sb.append("node1", node1);
    sb.append("port1", port1);
    sb.append("network2", network2);
    sb.append("node2", node2);
    sb.append("port2", port2);

    return sb.toString();

  }

}
