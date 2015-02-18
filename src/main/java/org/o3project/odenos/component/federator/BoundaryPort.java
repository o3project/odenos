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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * BoundaryPort is FederatorBoundary specialized in ports.
 *
 */
public class BoundaryPort {

  /** network ID. */
  private String networkId;

  /** node ID. */
  private String nodeId;

  /** port ID. */
  private String portId;

  /**
   * Constructor.
   * @param networkId ID for network.
   * @param nodeId ID for node in the network.
   * @param portId ID for port in the node.
   */
  public BoundaryPort(String networkId, String nodeId, String portId) {

    if ((networkId == null) || (nodeId == null) || (portId == null)) {
      throw new IllegalArgumentException("parameter is null");
    }

    this.networkId = networkId;
    this.nodeId = nodeId;
    this.portId = portId;

  }

  /**
   * Returns a network ID.
   * @return network ID.
   */
  public String getNetworkId() {
    return networkId;
  }

  /**
   * Returns a node ID.
   * @return node ID.
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Returns a port ID.
   * @return port ID.
   */
  public String getPortId() {
    return portId;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    StringBuilder sb = new StringBuilder();
    sb.append(networkId);
    sb.append(Federator.SEPARATOR);
    sb.append(nodeId);
    sb.append(Federator.SEPARATOR);
    sb.append(portId);
    String id = sb.toString();

    return id.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof BoundaryPort)) {
      return false;
    }

    BoundaryPort target = (BoundaryPort) obj;

    if (!(networkId.equals(target.getNetworkId()))) {
      return false;
    }

    if (!(nodeId.equals(target.getNodeId()))) {
      return false;
    }

    if (!(portId.equals(target.getPortId()))) {
      return false;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("networkId", networkId);
    sb.append("nodeId", nodeId);
    sb.append("portId", portId);

    return sb.toString();

  }

}
