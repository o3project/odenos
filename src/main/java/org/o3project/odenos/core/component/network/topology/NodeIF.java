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

import org.o3project.odenos.core.component.NetworkIF;
import org.o3project.odenos.remoteobject.message.Response;

import java.util.HashMap;
import java.util.Map;

public class NodeIF {
  public static final String PATH_PORTS = "topology/nodes/%s/ports/";

  private NetworkIF network;
  private Node node;

  public NodeIF(NetworkIF network, Node node) {
    this.network = network;
    this.node = node;
  }

  public String getType() {
    return this.node.getType();
  }

  public String getId() {
    return this.node.getId();
  }

  /**
   * Add port to node.
   * @param port port.
   * @return port interface.
   */
  public final PortIF addPort(Port port) {
    if (port == null) {
      return null;
    }

    String pathPort = String.format(PATH_PORTS + port.getId(), this.getId());
    Response resp = this.network.put(pathPort, port);
    if (resp.statusCode == Response.CONFLICT) {
      return null; // already exists
    }

    Port newPort = resp.getBody2(Port.class);
    return new PortIF(this.network, port);
  }

  /**
   * Get ports.
   * @return ports interface.
   */
  public final Map<String, PortIF> getPorts() {
    String pathPorts = String.format(PATH_PORTS, this.node.getId());
    Map<String, Port> ports = this.network.get(pathPorts).getBodyAsMap(Port.class);
    Map<String, PortIF> portsIf = new HashMap<String, PortIF>();
    for (Map.Entry<String, Port> e : ports.entrySet()) {
      portsIf.put(e.getKey(), new PortIF(this.network, e.getValue()));
    }
    return portsIf;
  }

  /**
   * Get port interface of port id.
   * @param portId port id.
   * @return port interface.
   */
  public final PortIF getPort(String portId) {
    String pathPort = String.format(PATH_PORTS + portId, this.node.getId());
    Port port = this.network.get(pathPort).getBody2(Port.class);
    return new PortIF(this.network, port);
  }

  //public final PortIF updatePort(Port port) {
  //  String path_port = String.format(PATH_PORTS + port.getId(), this.nodeId);
  //  PortIF port = getPort(port.getId());
  //  Port newPort = this.network.put(path_port, port).getBody2(Port.class);
  //  return new PortIF(this.network, newPort);
  //}
  //
  //public final String setAttribute(String key, String value) {
  //  Node node = this.network.get(this.path).getBody2(Node.class);
  //  node.putAttributes(key, value);
  //  Node newNode = this.network.put(this.path, node).getBody2(Node.class);
  //  return new NodeIF(this.network, newNode);
  //}

  ///**
  // * Remove all ports attached to this node.
  // */
  //public void clearPorts() {
  //  ports.clear();
  //}
  //
  ///**
  // * Returns a port.
  // * @param portId port ID.
  // * @return port associated to the port ID.
  // */
  //public Port getPort(String portId) {
  //  return ports.get(portId);
  //}
  //
  ///**
  // * Delete a port.
  // * @param portId port ID.
  // * @return removed port.
  // */
  //public Port deletePort(String portId) {
  //  return deletePort(getPort(portId));
  //}
  //
  ///**
  // * Delete a port.
  // * @param port deleted port.
  // * @return deleted the port.
  // */
  //public Port deletePort(Port port) {
  //  if (port == null) {
  //    return null;
  //  }
  //
  //  // Still has link(s)
  //  if (port.getInLink() != null || port.getOutLink() != null) {
  //    return null;
  //  }
  //
  //  Port ret = ports.remove(port.getId());
  //  return ret;
  //}
}