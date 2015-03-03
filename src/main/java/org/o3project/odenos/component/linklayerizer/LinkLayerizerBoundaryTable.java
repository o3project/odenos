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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage boundaries class.
 *
 */
public class LinkLayerizerBoundaryTable {

  /** logger. */
  private static final Logger logger =
      LoggerFactory.getLogger(LinkLayerizerBoundaryTable.class);

  /**
   * Map of boundaries.
   * key   : Boundary ID
   * value : LinkLayerizerBoundary
   */
  private Map<String, LinkLayerizerBoundary> boundaries = new HashMap<>();

  /**
   * Constructors.
   *
   */
  public LinkLayerizerBoundaryTable() {
    // do nothing
  }

  /**
   * Get boundaries.
   * @return boundaries
   */
  public Map<String, LinkLayerizerBoundary> getBoundaries() {
    return boundaries;
  }

  /**
   * Set boundaries.
   * @param boundaries map of boundaries.
   */
  public void setBoundaries(Map<String, LinkLayerizerBoundary> boundaries) {
    this.boundaries = boundaries;
  }

  /**
   * Add a entry of boundary.
   * @param boundary Registered boundary.
   * @return added the boundary
   * @throws LinkLayerizerBoundaryException if parameters were registered or null.
   */
  public LinkLayerizerBoundary addEntry(LinkLayerizerBoundary boundary)
      throws LinkLayerizerBoundaryException {

    if (boundary == null) {
      logger.error("boundary is null");
      throw new IllegalArgumentException("boundary is null");
    }

    String boundaryId = getUniqueId();
    boundary.setId(boundaryId);

    return updateEntry(boundaryId, boundary);
  }

  /**
   * Add a entry of boundary.
   * @param boundaryId ID for boundary.
   * @param boundary Registered boundary.
   * @return added the boundary
   * @throws LinkLayerizerBoundaryException if parameters were registered or null.
   */
  public LinkLayerizerBoundary addEntry(String boundaryId, LinkLayerizerBoundary boundary)
      throws LinkLayerizerBoundaryException {

    return updateEntry(boundaryId, boundary);
  }

  /**
   * Update a entry of boundary.
   * @param boundaryId ID for boundary.
   * @param boundary Replaced boundary.
   * @return updated the boundary
   * @throws LinkLayerizerBoundaryException if parameters were registered or null.
   */
  public LinkLayerizerBoundary updateEntry(String boundaryId,
      LinkLayerizerBoundary boundary)
      throws LinkLayerizerBoundaryException {

    if (boundaryId == null) {
      logger.error("boundaryId is null");
      throw new IllegalArgumentException("boundaryId is null");
    }
    if (boundary == null) {
      logger.error("boundary is null");
      throw new IllegalArgumentException("boundary is null");
    }

    if (!boundaryId.equals(boundary.getId())) {
      logger.warn("set boundaryId: {}", boundaryId);
      boundary.setId(boundaryId);
    }

    boundaries.put(boundaryId, boundary);

    return boundary;
  }

  /**
   * Get a entry of boundary.
   * @param boundaryId ID for boundary.
   * @return got the boundary
   */
  public LinkLayerizerBoundary getEntry(String boundaryId) {
    if (logger.isDebugEnabled()) {
      logger.debug("getEntry: " + boundaryId);
    }

    return boundaries.get(boundaryId);
  }

  /**
   * Get entry of boundary.
   * @param lowerNwId ID for lower network.
   * @param lowerNodeId ID for node.
   * @param lowerPortId ID for port.
   * @return found the boundary
   */
  public LinkLayerizerBoundary getBoundary(
      String lowerNwId, String lowerNodeId, String lowerPortId) {
    logger.debug("");
    if (lowerNodeId == null
        || lowerNwId == null
        || lowerPortId == null) {
      return null;
    }
    for (LinkLayerizerBoundary boundary : boundaries.values()) {
      if (boundary.getLowerNw().equals(lowerNwId)
          && boundary.getLowerNwNode().equals(lowerNodeId)
          && boundary.getLowerNwPort().equals(lowerPortId)) {
        return boundary;
      }
    }
    return null;
  }

  /**
   * Check boundary port or not.
   * @param nwId ID for lower or upper network.
   * @param nodeId ID for node.
   * @param portId ID for port.
   * @return If exist boundary port, return true.
   */
  public boolean isBoudaryPort(
      String nwId, String nodeId, String portId) {
    logger.debug("");

    for (LinkLayerizerBoundary boundary : boundaries.values()) {
      if (boundary.getLowerNw().equals(nwId)
          && boundary.getLowerNwNode().equals(nodeId)
          && boundary.getLowerNwPort().equals(portId)) {
        return true;
      }
      if (boundary.getUpperNw().equals(nwId)
          && boundary.getUpperNwNode().equals(nodeId)
          && boundary.getUpperNwPort().equals(portId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Remove a entry of boundary.
   * @param boundaryId ID for boundary.
   * @return deleted the boundary
   */
  public LinkLayerizerBoundary deleteEntry(String boundaryId) {
    if (logger.isDebugEnabled()) {
      logger.debug("deleteEntry: " + boundaryId);
    }

    if (!boundaries.containsKey(boundaryId)) {
      return null;
    }

    LinkLayerizerBoundary resultBoundary = boundaries.remove(boundaryId);

    return resultBoundary;
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
