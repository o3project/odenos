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

package org.o3project.odenos.core.util;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.topology.Link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Path calculator class.
 *
 */
public class PathCalculator implements Cloneable {

  private Graph<String, String> graph;
  private HashMap<String, List<String>> links;

  /**
   * Constructor.
   */
  public PathCalculator() {
    graph = new DirectedSparseMultigraph<>();
    links = new HashMap<>();
  }

  @Override
  public PathCalculator clone() {
    PathCalculator clonePath = new PathCalculator();

    for (String key : this.links.keySet()) {
      clonePath.graph.addEdge(
          key , links.get(key).get(0), links.get(key).get(1), EdgeType.DIRECTED);
    }
    return clonePath;
  }

  /**
   * Appends a link.
   * @param link a link.
   * @return true if the link is appended.
   */
  public boolean addLink(
      final Link link) {
    if (link.getId() == null
        || link.getSrcNode() == null
        || link.getDstNode() == null) {
      return false;
    }
    if (containsLink(link.getId())) {
      return false;
    }
    // Directed graph
    graph.addEdge(link.getId(), link.getSrcNode(), link.getDstNode(), EdgeType.DIRECTED);

    List<String> list = Arrays.asList(link.getSrcNode(), link.getDstNode());
    links.put(link.getId(),list);
    return true;
  }

  /**
   * Delete a link.
   * @param link a link.
   * @return true if the link is deleted.
   */
  public boolean delLink(
      final Link link) {
    if (link.getId() == null || !containsLink(link.getId())) {
      return false;
    }
    graph.removeEdge(link.getId());
    links.remove(link.getId());
    return true;
  }

  /**
   * Delete a link.
   * @param linkId link ID.
   * @return true if a link related to the link ID is deleted.
   */
  public boolean delLink(
      final String linkId) {
    if (linkId == null || !containsLink(linkId)) {
      return false;
    }
    graph.removeEdge(linkId);
    links.remove(linkId);
    return true;
  }

  public boolean containsLink(
      final String linkId) {
    return graph.containsEdge(linkId);
  }

  public void clear() {
    graph = new DirectedSparseMultigraph<String, String>();
  }

  /**
   * Get destination node from flow.
   *
   * @param flow
   *            Basic flow message.
   * @return Destination node.
   */
  public static String getInNode(
      final BasicFlow flow) {

    if (!flow.validate()) {
      return null;
    }
    BasicFlowMatch flowMatchs = flow.getMatches().get(0);
    if (flowMatchs == null) {
      return null;
    }
    return flowMatchs.getInNode();
  }

  /**
   * Get source node from flow.
   *
   * @param flow
   *            Basic flow message.
   * @return Source node.
   */
  public static String getOutNode(
      final BasicFlow flow) {

    if (!flow.validate()) {
      return null;
    }
    Map<String, List<FlowAction>> edgeAction = flow.getEdgeActions();
    for (String nodeId : edgeAction.keySet()) {
      return nodeId;
    }
    return null;
  }

  /**
   * Create the shortest path using Dikstra's algorithm.
   *
   * @param srcNodeId
   *            Source Node.
   * @param dstNodeId
   *            Destination Node.
   * @return Path to the dstNode form the srcNode.
   */
  public List<String> createPath(
      final String srcNodeId,
      final String dstNodeId) {

    List<String> path = new ArrayList<String>();
    try {
      DijkstraShortestPath<String, String> alg =
          new DijkstraShortestPath<String, String>(graph);
      // Get the shortest path.
      // path = new ArrayList<String>(alg.getPath(srcNodeId, dstNodeId));
      path = alg.getPath(srcNodeId, dstNodeId);
    } catch (IllegalArgumentException e) {
      //e.printStackTrace();
    }
    return path;
  }

  /**
   * Check the connectivity of links in the topology.
   *
   * @return Result of the connectivity.
   */
  public boolean checkConnectivity() {
    WeakComponentClusterer<String, String> clusterer =
        new WeakComponentClusterer<String, String>();
    Set<Set<String>> weakComponents = clusterer.transform(graph);
    if (weakComponents.size() == 1) {
      return true;
    }
    return false;
  }

}
