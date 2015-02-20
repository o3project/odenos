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

import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dispose onFlow in Federator class.
 */
public class FederatorOnFlow {

  /** logger. */
  private static final Logger logger = LoggerFactory.getLogger(Federator.class);

  /** Conversion Table. */
  protected ConversionTable conversionTable;

  /** Network Interfaces. */
  protected Map<String, NetworkInterface> networkInterfaces;

  /** Boundary Table. */
  protected FederatorBoundaryTable federatorBoundaryTable;

  /** Map of paths. key: networkId, value: paths */
  protected Map<String, List<String>> nwPaths = new HashMap<>();

  /** Map of source ports. key: networkId, value: dst boundary */
  protected Map<String, BoundaryPort> nwSrcBoundaryPorts = new HashMap<>();

  /** Map of destination ports. key: networkId, value: src boundary */
  protected Map<String, BoundaryPort> nwDstBoundaryPorts = new HashMap<>();

  /** Map of flows. key: networkId, value: src boundary */
  protected Map<String, BasicFlow> orgFlowList = new HashMap<>();

  /**
   * Constructors.
   * @param conversionTable specified conversion table.
   * @param networkInterfaces specified network interface.
   * @param federatorBoundaryTable Boundary Table
   */
  public FederatorOnFlow(ConversionTable conversionTable,
      Map<String, NetworkInterface> networkInterfaces,
      FederatorBoundaryTable federatorBoundaryTable) {

    this.conversionTable = conversionTable;
    this.networkInterfaces = networkInterfaces;
    this.federatorBoundaryTable = federatorBoundaryTable;
  }

  /**
   * Add flow with path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowAddedExistPath(String networkId, BasicFlow flow) {
    logger.debug("");

    initialize();
    // set nwPaths, nwSrcBoundary, nwDstBoundary
    doPathSetter(networkId, flow);
    // set orgFlowList
    doFlowAddedSelect(flow);
    doFlowAddedSetFlowRegister();
  }

  /**
   * Add flow without path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowAddedNotExistPath(String networkId, BasicFlow flow) {
    logger.debug("");

    initialize();
    
    BasicFlowMatch flowMatch = flow.getMatches().get(0);
    BasicFlow orgFlow = createOriginalFlowNotExistPath(networkId, flow);
    if (flowMatch == null || orgFlow == null) {
      logger.warn("invalid federated flow.");
      return;
    }

    String orgNodes = getConvNodeId(networkId, flowMatch.getInNode());
    if (orgNodes == null) {
      return;
    }
    String[] orgNodeList = orgNodes.split(Federator.SEPARATOR);
    String orgNwId = orgNodeList[0];
    NetworkInterface networkInterface = networkInterfaces.get(orgNwId);

    String flowId = flow.getFlowId();
    // PUT flow
    networkInterface.putFlow(orgFlow);

    // update conversionTable.
    conversionTable.addEntryFlow(
        networkId, flowId, orgNwId, flowId);
  }

  /**
   * Update flow when status is failed.
   * @param networkId ID for networks.
   * @param flow BasicFlow
   */
  public void flowUpdatePreStatusFailed(String networkId, BasicFlow flow) {
    logger.debug("");

    String fedNwId = getNetworkIdByType(Federator.FEDERATED_NETWORK);
    NetworkInterface fedNwIf = networkInterfaces.get(fedNwId);
    Flow fedFlow = fedNwIf.getFlow(flow.getFlowId());
    // set failed.
    fedFlow.setStatus(FlowObject.FlowStatus.FAILED.toString());
    // PUT flow.
    fedNwIf.putFlow(fedFlow);

    List<String> orgNetworks =
        conversionTable.getConnectionList(Federator.ORIGINAL_NETWORK);
    for (String orgNwId : orgNetworks) {
      // update conversionTable.
      conversionTable.delEntryFlow(orgNwId, flow.getFlowId());

      NetworkInterface orgNwIf = networkInterfaces.get(orgNwId);
      // DELETE flow
      orgNwIf.delFlow(flow.getFlowId());
    }
  }

  /**
   * Update flow when status is established.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   * @return boolean true:status established. false:status isn't established.
   */
  public boolean flowUpdatePreStatusEstablished(String networkId, BasicFlow flow) {
    logger.debug("");

    List<String> orgNetworks =
        conversionTable.getConnectionList(Federator.ORIGINAL_NETWORK);
    for (String orgNwId : orgNetworks) {
      if (orgNwId.equals(networkId)) {
        continue;
      }
      if (conversionTable.getFlow(orgNwId, flow.getFlowId()).size() == 0) {
        continue;
      }
      NetworkInterface orgNwIf = networkInterfaces.get(orgNwId);
      Flow orgFlow = orgNwIf.getFlow(flow.getFlowId());

      if (!FlowObject.FlowStatus.ESTABLISHED.toString().equalsIgnoreCase(
          orgFlow.getStatus())) {
        logger.debug("not flow's status established.");
        return false;
      }
    }
    logger.debug("next federate stauts:: established.");
    return true;
  }

  /**
   * Update flow when status is none.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   * @return boolean true:status none. false:status isn't none.
   */
  public boolean flowUpdatePreStatusNone(String networkId, BasicFlow flow) {
    logger.debug("");

    List<String> orgNetworks =
        conversionTable.getConnectionList(Federator.ORIGINAL_NETWORK);
    for (String orgNwId : orgNetworks) {
      if (orgNwId.equals(networkId)) {
        continue;
      }
      if (conversionTable.getFlow(orgNwId, flow.getFlowId()).size() == 0) {
        continue;
      }
      NetworkInterface orgNwIf = networkInterfaces.get(orgNwId);
      Flow orgFlow = orgNwIf.getFlow(flow.getFlowId());

      if (!FlowObject.FlowStatus.NONE.toString().equalsIgnoreCase(
          orgFlow.getStatus())) {
        logger.debug("not flow's status none.");
        return false;
      }
    }
    logger.debug("next federate stauts:: none");
    return true;
  }


  /**
   * Update flow with path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowUpdateExistPath(String networkId, BasicFlow flow) {
    logger.debug("");

    initialize();
    // set nwPaths, nwSrcBoundary, nwDstBoundary
    doPathSetter(networkId, flow);
    // set orgFlowList
    doFlowAddedSelect(flow);
    doFlowUpdateSetFlowRegister();
  }

  /**
   * Update flow without path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowUpdateNotExistPath(String networkId, BasicFlow flow) {
    logger.debug("");

    initialize();
    
    BasicFlowMatch flowMatch = flow.getMatches().get(0);
    BasicFlow orgFlow = createOriginalFlowNotExistPath(networkId, flow);
    if (flowMatch == null || orgFlow == null) {
      logger.warn("invalid federated flow.");
      return;
    }

    // get original network interface.
    String orgNodes = getConvNodeId(networkId, flowMatch.getInNode());
    if (orgNodes == null) {
      return;
    }
    String[] orgNodeList = orgNodes.split(Federator.SEPARATOR);
    String orgNwId = orgNodeList[0];
    NetworkInterface networkInterface = networkInterfaces.get(orgNwId);

    String flowId = flow.getFlowId();
      
    // Get Flow. and set version.
    Flow currOrgFlow = networkInterfaces.get(orgNwId).getFlow(flowId);
    orgFlow.setVersion(currOrgFlow.getVersion());
    // PUT flow
    networkInterface.putFlow(orgFlow);
  }
  
  /**
   * Update flow with path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowUpdateFromOriginal(String networkId, BasicFlow flow) {
    logger.debug("");
    
    BasicFlowMatch flowMatch = flow.getMatches().get(0);
    if (flowMatch == null) {
      logger.warn("invalid federated flow.");
      return;
    }

    // get federated network interface.
    String orgNodes = getConvNodeId(networkId, flowMatch.getInNode());
    if (orgNodes == null) {
      return;
    }
    String[] fedNodeList = orgNodes.split(Federator.SEPARATOR);
    String fedNwId = fedNodeList[0];
    NetworkInterface fedNwIf = networkInterfaces.get(fedNwId);

    String flowId = flow.getFlowId();
    // Get Flow. and set version.
    Flow fedFlow = fedNwIf.getFlow(flowId);
    boolean updated = false;
    // set status.
    String fedStatus = String.valueOf(fedFlow.getStatus());
    String orgStatus = String.valueOf(flow.getStatus());
    if (!(fedStatus.equals(orgStatus))) {
        updated = true;
        fedFlow.setStatus(flow.getStatus());
    }
    
    if (updated) {
      fedNwIf.putFlow(fedFlow);
    }
  }
   
  protected BasicFlow createOriginalFlowNotExistPath(String fedNwId, BasicFlow fedFlow) {
    logger.debug("");

    BasicFlow orgFlow = fedFlow.clone();

    List<BasicFlowMatch> flowMatches = fedFlow.getMatches(); /* non null */
    if (flowMatches.size() == 0) {
      logger.warn("there is no flow match");
      return null;
    }
    BasicFlowMatch flowMatch = flowMatches.get(0);

    String nodeId = flowMatch.getInNode();
    List<FlowAction> actions = fedFlow.getEdgeActions(nodeId); /* non null */
    if (actions.size() == 0) {
      logger.warn("there is no flow action");
      return null;
    }

    // convert flow's action.
    try {
      convertAction(fedNwId, orgFlow);
    } catch (Exception e) {
      logger.warn("failed convert flow's actions.");
    }

    // convert flow's match..
    try {
      convertMatch(fedNwId, orgFlow);
    } catch (Exception e) {
      logger.warn("failed convert flow's matches.");
    }

    return orgFlow;
  }

  /**
   * Check for updated other than status
   * @return  boolean true : updated other  false: updated status only
   */
  protected boolean checkUpdateFederator(
    final Flow prev,
    final Flow curr,
    final ArrayList<String> attr) {
    logger.debug("");

    BasicFlow basicFlowCurr = (BasicFlow) curr;
    BasicFlow basicFlowPrev = (BasicFlow) prev;

    if (basicFlowPrev.getEnabled() != basicFlowCurr.getEnabled()) {
      return true;
    }
    if (!basicFlowPrev.getPriority().equals(basicFlowCurr.getPriority())) {
      return true;
    }
    if (!basicFlowPrev.getOwner().equals(basicFlowCurr.getOwner())) {
      return true;
    }
    //if (!prev.getStatus().equals(curr.getStatus())) {
    //  return true;
    //}
    if (!basicFlowPrev.getMatches().equals(basicFlowCurr.matches)) {
      return true;
    }
    if (!basicFlowPrev.getPath().equals(basicFlowCurr.path)) {
      return true;
    }
    if (!basicFlowPrev.getEdgeActions().equals(basicFlowCurr.edgeActions)) {
      return true;
    }

    ArrayList<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }

    // make ignore list
    ArrayList<String> messageIgnoreAttributes = getIgnoreKeys(Logic.attributesFlow, attributesList);
    // attributes copy (curr -> body)
    Map<String, String> currAttributes = basicFlowCurr.getAttributes();
    for (String key : currAttributes.keySet()) {
      String oldAttr = basicFlowPrev.getAttribute(key);
      if (messageIgnoreAttributes.contains(key)
          || (oldAttr != null && oldAttr.equals(currAttributes.get(key)))) {
        continue;
      }
      return true;
    }
    logger.debug("");
    return false;
  }

  /**
   *
   * @param allkeys List of all keys.
   * @param updatekeys List of update keys.
   * @return List of the keys which aren't updated.
   */
  private ArrayList<String> getIgnoreKeys(
      final ArrayList<String> allkeys,
      final ArrayList<String> updatekeys) {

    ArrayList<String> ignorekeys = new ArrayList<String>();
    for (String key : allkeys) {
      ignorekeys.add(key);
    }

    String regex = "^" + Logic.AttrElements.ATTRIBUTES + "::.*";
    Pattern pattern = Pattern.compile(regex);
    for (String updatekey : updatekeys) {
      Matcher match = pattern.matcher(updatekey);
      if (match.find()) {
        String[] attributekey = updatekey.split("::");
        ignorekeys.remove(attributekey[1]);
      } else {
        ignorekeys.remove(updatekey);
      }
    }
    logger.debug("ignore key_list:: " + ignorekeys);
    return ignorekeys;
  }

  protected void doPathSetter(String networkId, BasicFlow flow) {
    logger.debug("");

    List<String> fedLinkIds = flow.getPath();

    for (String fedLinkId : fedLinkIds) {

      Link fedLink = networkInterfaces.get(networkId).getLink(fedLinkId);
      if (fedLink == null || !fedLink.validate()) {
        continue;
      }
      String orgSrcPorts = getConvPortId(
          networkId, fedLink.getSrcNode(), fedLink.getSrcPort());
      String orgDstPorts = getConvPortId(
          networkId, fedLink.getDstNode(), fedLink.getDstPort());
      if (orgSrcPorts == null || orgDstPorts == null) {
        continue;
      }
      String[] orgSrcPList = orgSrcPorts.split(Federator.SEPARATOR);
      String[] orgDstPList = orgDstPorts.split(Federator.SEPARATOR);
      // src port
      String orgSrcNw = orgSrcPList[0];
      String orgSrcNode = orgSrcPList[1];
      String orgSrcPort = orgSrcPList[2];
      // dst port
      String orgDstNw = orgDstPList[0];
      String orgDstNode = orgDstPList[1];
      String orgDstPort = orgDstPList[2];

      boolean isBoundaryLink = false;
      if (federatorBoundaryTable.isContainsLink(
          orgSrcNw, orgSrcNode, orgSrcPort)) {
        logger.debug("set boundary src port.");
        nwSrcBoundaryPorts.put(orgSrcNw,
            new BoundaryPort(orgSrcNw, orgSrcNode, orgSrcPort));
        if (!nwPaths.containsKey(orgSrcNw)) {
          nwPaths.put(orgSrcNw, new ArrayList<String>());
        }
        isBoundaryLink = true;
      }
      if (federatorBoundaryTable.isContainsLink(
          orgDstNw, orgDstNode, orgDstPort)) {
        logger.debug("set boundary dst port.");
        nwDstBoundaryPorts.put(orgDstNw,
            new BoundaryPort(orgDstNw, orgDstNode, orgDstPort));
        if (!nwPaths.containsKey(orgDstNw)) {
          nwPaths.put(orgDstNw, new ArrayList<String>());
        }
        isBoundaryLink = true;
      }

      // if src and dst networks of a flow are different, the flow should be divided.
      if (!orgSrcNw.equals(orgDstNw)) {
        nwSrcBoundaryPorts.put(orgSrcNw, new BoundaryPort(orgSrcNw, orgSrcNode, orgSrcPort));
        if (!nwPaths.containsKey(orgSrcNw)) {
          nwPaths.put(orgSrcNw, new ArrayList<String>());
        }
        nwDstBoundaryPorts.put(orgDstNw, new BoundaryPort(orgDstNw, orgDstNode, orgDstPort));
        if (!nwPaths.containsKey(orgDstNw)) {
          nwPaths.put(orgDstNw, new ArrayList<String>());
        }
        isBoundaryLink = true;
      }

      if (!isBoundaryLink) {
        logger.debug("no boundary link.");
        String orgLinks = getConvLinkId(networkId, fedLinkId);
        if (orgLinks == null) {
          continue;
        }
        String[] orgLinkList = orgLinks.split(Federator.SEPARATOR);
        String orgNwId = orgLinkList[0];
        String orgLinkId = orgLinkList[1];
        if (nwPaths.containsKey(orgNwId)) {
          nwPaths.get(orgSrcNw).add(orgLinkId);
        } else {
          List<String> newLinks = new ArrayList<>();
          newLinks.add(orgLinkId);
          nwPaths.put(orgNwId, newLinks);
        }
      }
    }
  }

  protected void doFlowAddedSelect(BasicFlow flow) {
    logger.debug("");

    for (String orgNwId : nwPaths.keySet()) {
      BasicFlow orgFlow = flow.clone();

      // set path
      orgFlow.getPath().clear();
      for (String pathLink : nwPaths.get(orgNwId)) {
        orgFlow.getPath().add(pathLink);
      }

      // set action
      BoundaryPort srcBoundaryPort = nwSrcBoundaryPorts.get(orgNwId);
      if (srcBoundaryPort != null) {
        String srcNodeId = srcBoundaryPort.getNodeId();
        String srcPortId = srcBoundaryPort.getPortId();
        // add action
        if (!setFlowAction(orgFlow, srcNodeId, srcPortId)) {
          logger.warn("failed add flow's actions.");
        }
      } else {
        // convert action
        try {
          String fedNwId =
              getNetworkIdByType(Federator.FEDERATED_NETWORK);
          convertAction(fedNwId, orgFlow);
        } catch (Exception e) {
          logger.warn("failed convert flow's actions.");
        }
      }

      // set match
      BoundaryPort dstBoundaryPort = nwDstBoundaryPorts.get(orgNwId);
      if (dstBoundaryPort != null) {
        String dstNodeId = dstBoundaryPort.getNodeId();
        String dstPortId = dstBoundaryPort.getPortId();
        // add match
        if (!setFlowMatch(orgFlow, dstNodeId, dstPortId)) {
          logger.warn("failed add flow's match.");
        }
      } else { // convert match
        try {
          String fedNwId =
              getNetworkIdByType(Federator.FEDERATED_NETWORK);
          convertMatch(fedNwId, orgFlow);
        } catch (Exception e) {
          logger.warn("failed convert flow's actions.");
        }
      }

      orgFlowList.put(orgNwId, orgFlow);
    }
  }

  protected void doFlowAddedSetFlowRegister() {
    logger.debug("");

    // Register Flow
    for (String orgNwId : orgFlowList.keySet()) {
      String fedNwId =
          getNetworkIdByType(Federator.FEDERATED_NETWORK);
      Flow orgFlow = orgFlowList.get(orgNwId);
      // flowId is common.
      String flowId = orgFlow.getFlowId();

      // update conversionTable
      conversionTable.addEntryFlow(
          orgNwId, flowId, fedNwId, flowId);
      // Put Flow
      networkInterfaces.get(orgNwId).putFlow(orgFlow);
    }
  }
  
  protected void doFlowUpdateSetFlowRegister() {
    logger.debug("");

    // Register Flow
    for (String orgNwId : orgFlowList.keySet()) {
      Flow orgFlow = orgFlowList.get(orgNwId);
      // flowId is common.
      String flowId = orgFlow.getFlowId();

      // Get Flow. and set version.
      Flow currOrgFlow = networkInterfaces.get(orgNwId).getFlow(flowId);
      orgFlow.setVersion(currOrgFlow.getVersion());
      // Put Flow
      networkInterfaces.get(orgNwId).putFlow(orgFlow);
    }
  }

  protected void initialize() {
    nwPaths.clear();
    nwSrcBoundaryPorts.clear();
    nwDstBoundaryPorts.clear();
    orgFlowList.clear();
  }

  /**
   *
   * @param flow BasicFlow.
   * @param dstNodeId Node ID.
   * @param dstPortId Port ID.
   * @return true: success. false: failed.
   */
  protected boolean setFlowMatch(
      BasicFlow flow,
      String dstNodeId,
      String dstPortId) {
    logger.debug("");

    if (flow.getMatches() == null) {
      return false;
    }
    for (BasicFlowMatch match : flow.getMatches()) {
      match.setInNode(dstNodeId);
      match.setInPort(dstPortId);
    }
    return true;
  }

  protected void convertMatch(
      String fedNwId,
      BasicFlow flow) throws Exception {
    logger.debug("");

    for (BasicFlowMatch match : flow.getMatches()) {
      String fedNodeId = match.getInNode();
      String fedPortId = match.getInPort();
      String orgPorts = getConvPortId(
          fedNwId, fedNodeId, fedPortId);
      if (orgPorts == null) {
        continue;
      }
      String[] orgPList = orgPorts.split(Federator.SEPARATOR);
      match.setInNode(orgPList[1]);
      match.setInPort(orgPList[2]);
    }
  }

  /**
   *
   * @param flow BasicFlow.
   * @param srcNodeId Node ID.
   * @param srcPortId Port ID.
   * @return true: success. false: failed.
   */
  protected boolean setFlowAction(
      BasicFlow flow,
      String srcNodeId,
      String srcPortId) {
    logger.debug("");

    try {
      List<FlowAction> actionOutputs = new ArrayList<FlowAction>();
      flow.getEdgeActions().clear();
      actionOutputs.add(new FlowActionOutput(srcPortId));
      flow.getEdgeActions().put(srcNodeId, actionOutputs);
    } catch (NullPointerException e) {
      return false;
    }
    return true;
  }

  protected void convertAction(
      String fedNwId,
      BasicFlow flow) throws Exception {
    logger.debug("");

    BasicFlow fedFlow = flow.clone();
    Map<String, List<FlowAction>> fedFlowActions = fedFlow.getEdgeActions();
    Map<String, List<FlowAction>> orgFlowActions = flow.getEdgeActions();

    Map<String, List<FlowAction>> targetActions =
        new HashMap<String, List<FlowAction>>();
    List<FlowAction> noActionOutputs = new ArrayList<FlowAction>();

    // Convert Action.
    for (String fedNodeId : fedFlowActions.keySet()) {
      for (FlowAction fact : fedFlowActions.get(fedNodeId)) {
        if (fact.getType().equals(
            FlowActionOutput.class.getSimpleName())) {
          FlowActionOutput output =
              (FlowActionOutput) fact;
          String fedPortId = output.getOutput();
          String orgPorts = getConvPortId(
              fedNwId, fedNodeId, fedPortId);
          if (orgPorts == null) {
            continue;
          }
          String[] orgPList = orgPorts.split(Federator.SEPARATOR);
          if (targetActions.containsKey(orgPList[1])) {
            targetActions.get(orgPList[1]).add(
                new FlowActionOutput(orgPList[2]));
          } else {
            List<FlowAction> target = new ArrayList<FlowAction>();
            target.add(new FlowActionOutput(orgPList[2]));
            targetActions.put(orgPList[1], target);
          }
        } else {
          // no FlowActionOutput
          noActionOutputs.add(fact);
        }
      }
    }
    // Reset dstAction.
    orgFlowActions.clear();
    for (String nodeId : targetActions.keySet()) {
      // set target action
      orgFlowActions.put(nodeId, targetActions.get(nodeId));
      // set no target action
      for (FlowAction fact : noActionOutputs) {
        orgFlowActions.get(nodeId).add(fact);
      }
    }
  }

  /**
   *
   * @param connType Type of the network.
   * @return ID for the network.
   */
  protected final String getNetworkIdByType(final String connType) {
    logger.debug("");

    if (connType == null) {
      return null;
    }
    ArrayList<String> convNetowrkId =
        conversionTable.getConnectionList(connType);
    if (convNetowrkId.size() == 0) {
      return null;
    }
    return convNetowrkId.get(0);
  }

  /**
   *
   * @param networkId ID for network.
   * @param nodeId ID for link in the network.
   * @return ID for node in the federated network.
   */
  protected final String getConvNodeId(
      final String networkId,
      final String nodeId) {
    logger.debug("");

    if (networkId == null || nodeId == null) {
      return null;
    }
    ArrayList<String> convNodeId =
        conversionTable.getNode(networkId, nodeId);
    if (convNodeId.size() == 0) {
      return null;
    }
    return convNodeId.get(0);
  }

  /**
   *
   * @param networkId ID for network.
   * @param nodeId ID for link in the network.
   * @param portId ID for port in the network.
   * @return ID for port in the federated network.
   */
  protected final String getConvPortId(
      final String networkId,
      final String nodeId,
      final String portId) {
    logger.debug("");

    if (networkId == null || nodeId == null || portId == null) {
      return null;
    }
    ArrayList<String> convPortId =
        conversionTable.getPort(networkId, nodeId, portId);
    if (convPortId.size() == 0) {
      return null;
    }
    return convPortId.get(0);
  }

  /**
   *
   * @param networkId ID for network.
   * @param linkId ID for link in the network.
   * @return ID link for in the federated network.
   */
  protected final String getConvLinkId(String networkId, String linkId) {
    if (networkId == null || linkId == null) {
      return null;
    }
    ArrayList<String> convLinkId =
        conversionTable.getLink(networkId, linkId);
    if (convLinkId.size() == 0) {
      return null;
    }
    return convLinkId.get(0);
  }

}
