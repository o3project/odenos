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

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.apache.commons.lang.StringUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manage boundaries class.
 *
 */
public class FederatorBoundaryTable {

  /** logger. */
  private static final Logger log =
      LogManager.getLogger(FederatorBoundaryTable.class);

  /**
   * Map of boundaries.
   * key   : Boundary ID
   * value : Boundary (FederatorBoundary)
   */
  private Map<String, FederatorBoundary> boundaries;

  /**
   * Map of ports.
   * value : BoundaryPort(networkId + nodeId + portId)
   */
  private Bag<BoundaryPort> boundaryPorts = new HashBag<>();

  /**
   * Constructors.
   */
  public FederatorBoundaryTable() {
    boundaries = new HashMap<>();
  }

  /**
   * Add entry of boundary.
   * @param boundary Registered boundary.
   * @return Added the boundary.
   * @throws FederatorException if parameters were registered or null.
   */
  public FederatorBoundary addEntry(FederatorBoundary boundary)
      throws FederatorException {

    String boundaryId = getUniqueId();
    boundary.setId(boundaryId);

    return addEntry(boundaryId, boundary);
  }

  /**
   * Add or update entry of boundary.
   * @param boundaryId boundary ID.
   * @param boundary Registered boundary.
   * @return Added the boundary.
   * @throws FederatorException if parameters were registered or null.
   */
  public FederatorBoundary addEntry(String boundaryId, FederatorBoundary boundary)
      throws FederatorException {

    if (boundary == null) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "boundary is null"));
      throw new IllegalArgumentException("boundary is null");
    }

    if (boundaries.containsKey(boundaryId)) {
      throw new FederatorException("conflict id");
    }

    if (log.isDebugEnabled()) {
      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "addEntry: {}", boundaryId));
    }

    if (StringUtils.equals(boundaryId, boundary.getId())) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "set boundaryId: {}", boundary));
      boundary.setId(boundaryId);
    }

    BoundaryPort boundaryPort1 = new BoundaryPort(
        boundary.getNetwork1(), boundary.getNode1(),
        boundary.getPort1());
    BoundaryPort boundaryPort2 = new BoundaryPort(
        boundary.getNetwork2(), boundary.getNode2(),
        boundary.getPort2());

    boundaryPorts.add(boundaryPort1);
    boundaryPorts.add(boundaryPort2);

    boundaries.put(boundaryId, boundary);

    return boundary;
  }

  /**
   * Get entry of boundary.
   * @param boundaryId ID for boundary.
   * @return found the boundary.
   */
  public FederatorBoundary getEntry(String boundaryId) {
    if (log.isDebugEnabled()) {
      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "getEntry: {}", boundaryId));
    }
    return boundaries.get(boundaryId);
  }

  /**
   *
   * @param boundaryId ID for boundary.
   * @return deleted the boundary.
   */
  public FederatorBoundary deleteEntry(String boundaryId) {
    if (log.isDebugEnabled()) {
      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "deleteEntry: {}", boundaryId));
    }

    if (!boundaries.containsKey(boundaryId)) {
      return null;
    }

    FederatorBoundary resultBoundary = boundaries.remove(boundaryId);

    BoundaryPort boundaryPort1 = new BoundaryPort(
        resultBoundary.getNetwork1(), resultBoundary.getNode1(),
        resultBoundary.getPort1());
    BoundaryPort boundaryPort2 = new BoundaryPort(
        resultBoundary.getNetwork2(), resultBoundary.getNode2(),
        resultBoundary.getPort2());
    boundaryPorts.remove(boundaryPort1, 1);
    boundaryPorts.remove(boundaryPort2, 1);

    return resultBoundary;
  }

  /**
   *
   * @param key ID for boundary.
   * @return true: found ID. false: not found ID.
   */
  public boolean isContains(String key) {
    return boundaries.containsKey(key);
  }

  /**
   *
   * @param networkId ID for network.
   * @param nodeId ID for node in the network.
   * @param portId ID for port in the node.
   * @return true: found the link. false: not found the link.
   */
  public boolean isContainsLink(String networkId, String nodeId, String portId) {

    BoundaryPort orgNode = new BoundaryPort(networkId, nodeId, portId);
    return boundaryPorts.contains(orgNode);
  }

  /**
   * Get boundaries.
   * @return Map of the boundary.
   */
  public Map<String, FederatorBoundary> getBoundaries() {
    return boundaries;
  }

  /**
   * Method for assigned generating UUID to ComponentConnectionID.
   *
   * @return id ComponentConnectionID.
   */
  protected final String getUniqueId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (boundaries.containsKey(id));

    return id;
  }

}
