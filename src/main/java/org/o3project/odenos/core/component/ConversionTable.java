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

package org.o3project.odenos.core.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ConversionTable class.
 *
 */
public class ConversionTable {

  /**
   * Constructor.
   */
  public ConversionTable() {
  }

  // ex
  // key : connectionId
  // value : connectionType
  // {connA:original, connB:sliver, connC:sliver}
  private HashMap<String, String> connectionTypeMap = new HashMap<String, String>();

  // key : connectionType
  // value : connectionIdList
  // {original:[connA], sliver:[connB,CoonC]}
  // private HashMap<String, ArrayList<String>>
  // connectionTypeTable = new HashMap<String, ArrayList<String>>();

  /**
   * Returns a type of connection.
   * @param connectionId connection ID.
   * @return type of connection.
   */
  public final String getConnectionType(final String connectionId) {
    return connectionTypeMap.get(connectionId);
  }

  /**
   * Returns a list of connection IDs.
   * @param connectionType type of connection.
   * @return list of connection IDs.
   */
  public final ArrayList<String>
      getConnectionList(final String connectionType) {
    ArrayList<String> connectionIds = new ArrayList<String>();
    for (Map.Entry<String, String> e : connectionTypeMap.entrySet()) {
      if (connectionType.equals(e.getValue())) {
        connectionIds.add(e.getKey());
      }
    }
    return connectionIds;
  }

  /**
   * Returns true if this contained type.
   * @param connectionType type of
   * @return true if this contained type.
   */
  public final boolean isConnectionType(final String connectionType) {
    if (connectionType == null
        || 0 == this.getConnectionList(connectionType).size()) {
      return false;
    }
    return true;
  }

  /**
   * Associates the type in connection map.
   * @param id key.
   * @param type value of type to be associated with the key.
   */
  public final void addEntryConnectionType(
      final String id,
      final String type) {
    connectionTypeMap.put(id, type);
  }

  /**
   * Removes the mapping for a key from connection map.
   * @param id key.
   */
  public final void delEntryConnectionType(final String id) {
    connectionTypeMap.remove(id);
  }

  // ex
  // connA --- [LogicComponent] --+-- connB
  // +-- connC
  //
  // connA : [connB,connC]
  // connB : [connA]
  // connC : [connA]

  private HashMap<String, ArrayList<String>> nwcConversionTable =
      new HashMap<String, ArrayList<String>>();
  private HashMap<String, ArrayList<String>> nodeConversionTable =
      new HashMap<String, ArrayList<String>>();
  private HashMap<String, ArrayList<String>> portConversionTable =
      new HashMap<String, ArrayList<String>>();
  private HashMap<String, ArrayList<String>> linkConversionTable =
      new HashMap<String, ArrayList<String>>();
  private HashMap<String, ArrayList<String>> flowConversionTable =
      new HashMap<String, ArrayList<String>>();

  // get object connection
  /**
   * Returns a conversion map of network.
   * @return conversion map of network.
   */
  public final HashMap<String, ArrayList<String>> getNetwork() {
    return nwcConversionTable;
  }

  /**
   * Returns a list of network IDs.
   * @param nwcId network ID.
   * @return list of network IDs.
   */
  public final ArrayList<String> getNetwork(final String nwcId) {
    ArrayList<String> ret = nwcConversionTable.get(nwcId);
    if (ret == null) {
      return new ArrayList<String>();
    }
    return ret;
  }

  /**
   * Returns a conversion map of node.
   * @return conversion map of node.
   */
  public final HashMap<String, ArrayList<String>> getNode() {
    return nodeConversionTable;
  }

  /**
   * Returns a list of node IDs.
   * @param nwcId network ID.
   * @param nodeId node ID.
   * @return list of node IDs.
   */
  public final ArrayList<String> getNode(final String nwcId, final String nodeId) {
    String key = nwcId + "::" + nodeId;
    if (nodeConversionTable.containsKey(key)) {
      return nodeConversionTable.get(key);
    }
    return new ArrayList<String>();
  }

  /**
   * Returns a conversion map of port.
   * @return conversion map of port.
   */
  public final HashMap<String, ArrayList<String>> getPort() {
    return portConversionTable;
  }

  /**
   * Returns a list of port IDs.
   * @param nwcId network ID.
   * @param nodeId node ID.
   * @param portId port ID.
   * @return list of port IDs.
   */
  public final ArrayList<String>
      getPort(final String nwcId, final String nodeId, final String portId) {
    String key = nwcId + "::" + nodeId + "::" + portId;
    if (portConversionTable.containsKey(key)) {
      return portConversionTable.get(key);
    }
    return new ArrayList<String>();
  }

  /**
   * Returns a conversion map of link.
   * @return conversion map of link.
   */
  public final HashMap<String, ArrayList<String>> getLink() {
    return linkConversionTable;
  }

  /**
   * Returns a list of link IDs.
   * @param nwcId network ID.
   * @param linkId link ID.
   * @return list of link IDs.
   */
  public final ArrayList<String>
      getLink(final String nwcId, final String linkId) {
    String key = nwcId + "::" + linkId;
    if (linkConversionTable.containsKey(key)) {
      return linkConversionTable.get(key);
    }
    return new ArrayList<String>();
  }

  /**
   * Returns a conversion map of flow.
   * @return conversion map of flow.
   */
  public final HashMap<String, ArrayList<String>> getFlow() {
    return flowConversionTable;
  }

  /**
   * Returns a list of flow IDs.
   * @param nwcId network ID.
   * @param flowId flow ID.
   * @return list of flow IDs.
   */
  public final ArrayList<String>
      getFlow(final String nwcId, final String flowId) {
    String key = nwcId + "::" + flowId;
    if (flowConversionTable.containsKey(key)) {
      return flowConversionTable.get(key);
    }
    return new ArrayList<String>();
  }

  // add object connection
  /**
   * Associates the network in network map.
   * @param nwcId1 network ID as key.
   * @param nwcId2 associated network ID.
   */
  public final void addEntryNetwork(
      final String nwcId1,
      final String nwcId2) {
    addEntryObject(nwcConversionTable, nwcId1, nwcId2);
  }

  /**
   * Appends the node to this table.
   * @param orgNwcId original network ID.
   * @param orgNodeId original node ID.
   * @param repNwcId replaced network ID.
   * @param repNodeId replaced node ID.
   */
  public final void addEntryNode(
      final String orgNwcId,
      final String orgNodeId,
      final String repNwcId,
      final String repNodeId) {
    String key = orgNwcId + "::" + orgNodeId;
    String value = repNwcId + "::" + repNodeId;
    addEntryObject(nodeConversionTable, key, value);
  }

  /**
   * Appends the port to this table.
   * @param orgNwcId original network ID.
   * @param orgNodeId original node ID.
   * @param orgPortId original port ID.
   * @param repNwcId replaced network ID.
   * @param repNodeId replaced node ID.
   * @param repPortId replaced port ID.
   */
  public final void addEntryPort(
      final String orgNwcId,
      final String orgNodeId,
      final String orgPortId,
      final String repNwcId,
      final String repNodeId,
      final String repPortId) {
    String key = orgNwcId + "::" + orgNodeId + "::" + orgPortId;
    String value = repNwcId + "::" + repNodeId + "::" + repPortId;
    addEntryObject(portConversionTable, key, value);
  }

  /**
   * Appends the link to this table.
   * @param orgNwcId original network ID.
   * @param orgLinkId original link ID.
   * @param repNwcId replaced network ID.
   * @param repLinkId replaced link ID.
   */
  public final void addEntryLink(
      final String orgNwcId,
      final String orgLinkId,
      final String repNwcId,
      final String repLinkId) {
    String key = orgNwcId + "::" + orgLinkId;
    String value = repNwcId + "::" + repLinkId;
    addEntryObject(linkConversionTable, key, value);
  }

  /**
   * Appends the flow to this table.
   * @param orgNwcId original network ID.
   * @param orgFlowId original flow ID.
   * @param repNwcId replaced network ID.
   * @param repFlowId replaced flow ID.
   */
  public final void addEntryFlow(
      final String orgNwcId,
      final String orgFlowId,
      final String repNwcId,
      final String repFlowId) {
    String key = orgNwcId + "::" + orgFlowId;
    String value = repNwcId + "::" + repFlowId;
    addEntryObject(flowConversionTable, key, value);
  }

  private void addEntryObject(
      final HashMap<String, ArrayList<String>> hashObj,
      final String key,
      final String value) {

    // key setting
    ArrayList<String> valueList = hashObj.get(key);
    if (valueList == null) {
      valueList = new ArrayList<String>();
      hashObj.put(key, valueList);
    }
    valueList.add(value);

    // value -> key setting(reverse setting)
    ArrayList<String> valueListRev = hashObj.get(value);
    if (valueListRev == null) {
      valueListRev = new ArrayList<String>();
      hashObj.put(value, valueListRev);
    }
    valueListRev.add(key);
  }

  // delete object connection
  /**
   * Removes the mapping for a key from network map.
   * @param key network ID as key.
   */
  public final void delEntryNetwork(
      final String key) {
    delEntryObject(nwcConversionTable, key);
    return;
  }

  /**
   * Delete the node ID from in this table.
   * @param nwcId network ID.
   * @param nodeId node ID.
   */
  public final void delEntryNode(
      final String nwcId,
      final String nodeId) {
    // Delete Port => Node.
    ArrayList<String> delPortList = new ArrayList<String>();
    for (String pid : portConversionTable.keySet()) {
      String[] plist = pid.split("::");
      if (plist[0].equals(nwcId)
          && plist[1].equals(nodeId)) {
        delPortList.add(pid);
      }
    }
    for (String pid : delPortList) {
      delEntryObject(portConversionTable, pid);

    }
    String key = nwcId + "::" + nodeId;
    delEntryObject(nodeConversionTable, key);
    return;
  }

  /**
   * Delete the port ID from in this table.
   * @param nwcId network ID.
   * @param nodeId node ID.
   * @param portId port ID.
   */
  public final void delEntryPort(
      final String nwcId,
      final String nodeId,
      final String portId) {
    String key = nwcId + "::" + nodeId + "::" + portId;
    delEntryObject(portConversionTable, key);
    return;
  }

  /**
   * Delete the link ID from in this table.
   * @param nwcId network ID.
   * @param linkId link ID.
   */
  public final void delEntryLink(
      final String nwcId,
      final String linkId) {
    String key = nwcId + "::" + linkId;
    delEntryObject(linkConversionTable, key);
    return;
  }

  /**
   * Delete the flow ID from in this table.
   * @param nwcId network ID.
   * @param flowId flow ID.
   */
  public final void delEntryFlow(
      final String nwcId,
      final String flowId) {
    String key = nwcId + "::" + flowId;
    delEntryObject(flowConversionTable, key);
    return;
  }

  private void delEntryObject(
      final HashMap<String, ArrayList<String>> hashObj,
      final String key) {
    ArrayList<String> valueList = hashObj.get(key);
    if (valueList == null) {
      return;
    }

    // value -> key remove(reverse setting remove)
    for (String reversekey : valueList) {
      if (!hashObj.containsKey(reversekey)) {
        continue;
      }
      if (hashObj.get(reversekey).size() > 1) {
        hashObj.get(reversekey).remove(key);
        continue;
      }
      hashObj.remove(reversekey);
    }
    hashObj.remove(key);
  }
}
