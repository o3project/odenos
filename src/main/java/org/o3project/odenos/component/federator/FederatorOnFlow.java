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
import org.o3project.odenos.remoteobject.message.Response;
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
  private static final Logger log = LoggerFactory.getLogger(Federator.class);

  protected ConversionTable conversionTable;
  protected Map<String, NetworkInterface> networkInterfaces;

  /** Map of flows. key: federator flowId, value: counter */
  protected Map<String, Integer> orgFlowCnt = new HashMap<>();

  /**
   * Constructors.
   * @param conversionTable specified conversion table.
   * @param networkInterfaces specified network interface.
   */
  public FederatorOnFlow(ConversionTable conversionTable,
      Map<String, NetworkInterface> networkInterfaces ) {

    this.conversionTable = conversionTable;
    this.networkInterfaces = networkInterfaces;
  }

  /**
   * Add flow with path.
   * @param flow federated flow
   */
  public void createOriginalFlow(BasicFlow flow) {
    log.debug("");

    doFlowAddedSelect(flow);
  }

  /**
   * Update flow when status is failed.
   * @param networkId ID for networks.
   * @param flow BasicFlow
   */
  public void flowUpdatePreStatusFailed(String networkId, BasicFlow flow) {
    log.debug("");

    String fedNwId = getNetworkIdByType(Federator.FEDERATED_NETWORK);
    NetworkInterface fedNwIf = networkInterfaces.get(fedNwId);
    Flow fedFlow = fedNwIf.getFlow(getConvFlowId(networkId, flow.getFlowId()));
    if (fedFlow != null) {
      // set failed.
      fedFlow.setStatus(FlowObject.FlowStatus.FAILED.toString());
      // PUT flow.
      fedNwIf.putFlow(fedFlow);
    }

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
    log.debug("");

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
        log.debug("not flow's status established.");
        return false;
      }
    }
    log.debug("next federate stauts:: established.");
    return true;
  }

  /**
   * Update flow when status is none.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   * @return boolean true:status none. false:status isn't none.
   */
  public boolean flowUpdatePreStatusNone(String networkId, BasicFlow flow) {
    log.debug("");

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
        log.debug("not flow's status none.");
        return false;
      }
    }
    log.debug("next federate stauts:: none");
    return true;
  }
  
  /**
   * Update flow with path.
   * @param networkId ID for networks.
   * @param flow BasicFlow.
   */
  public void flowUpdateFromOriginal(String networkId, BasicFlow flow) {
    log.debug("");
    
    BasicFlowMatch flowMatch = flow.getMatches().get(0);
    if (flowMatch == null) {
      log.warn("invalid federated flow.");
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
    Flow fedFlow = fedNwIf.getFlow(getConvFlowId(networkId, flowId));
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

  /**
   * Check for updated other than status
   * @param prev prev flow object.
   * @param curr curr flow object.
   * @param attr update attribute list
   * @return  boolean true : updated other  false: updated status only
   */
  protected boolean checkUpdateFederator(
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attr) {
    log.debug("");

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
    log.debug("");
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
    log.debug("ignore key_list:: {}", ignorekeys);
    return ignorekeys;
  }

  protected void doFlowAddedSelect(BasicFlow fedFlow) {
    log.debug("");

    String fedNwId = getNetworkIdByType(Federator.FEDERATED_NETWORK);
    String orgNwId;
    String edgeNode = null;

    BasicFlow orgFlow = fedFlow.clone();
    orgFlow.getPath().clear();
    orgFlow.setVersion("0");

    // convert match
    try {
      orgNwId = convertMatch(fedNwId, orgFlow);
    } catch (Exception e) {
      log.warn("failed convert flow's actions.");
      return ;
    }

    if (fedFlow.getPath().size() == 0) {
      edgeNode = fedFlow.getMatches().get(0).getInNode();
    }

    for (String fedPathId : fedFlow.getPath()) {

      // convert path
      String orgPathId = convertPath(fedNwId, fedPathId);
      if (orgPathId != null) {
        orgFlow.getPath().add(orgPathId);
        continue;
      }

      // convert action
      Link fedLink = networkInterfaces.get(fedNwId).getLink(fedPathId);
      orgFlow.putEdgeActions(
        convertAction2(fedNwId, fedLink.getSrcNode(), fedLink.getSrcPort(), orgFlow.getEdgeActions()));
      doFlowAddedSetFlowRegister(orgNwId, orgFlow);

      // next network
      String dstPortId = getConvPortId(fedNwId, fedLink.getDstNode(), fedLink.getDstPort());
      String[] dstPortIds = dstPortId.split(Federator.SEPARATOR);

      orgFlow = fedFlow.clone();
      orgFlow.getPath().clear();
      orgFlow.setVersion("0");

      // set match
      orgNwId = dstPortIds[0];
      setFlowMatch(orgFlow, dstPortIds[1], dstPortIds[2]);

      edgeNode = fedLink.getDstNode();
    }

    // convert action
    try {
      orgFlow.putEdgeActions(
          convertAction(fedNwId, edgeNode, orgFlow.getEdgeActions()));
    } catch (Exception e) {
      log.warn("failed convert flow's actions.");
    }
    doFlowAddedSetFlowRegister(orgNwId, orgFlow);
  }

  protected String convertPath(String fedNwId, String fedPathId) { 
    log.debug("");

    List<String> orgPaths = conversionTable.getLink(fedNwId, fedPathId);
    if (orgPaths.size() == 0) {
      return null;
    }
    String[] orgPath = orgPaths.get(0).split(Federator.SEPARATOR);
    return orgPath[1];
  }

  /**
   *
   * @param nwId  federator network Id
   * @param nodeId  federator edge node Id
   * @param portId  federator edge port Id
   * @param edgeActions federated edgeActions
   * edge_actions : { "nodeA" : [FlowAction1, FlowAction2, ...],
   *                  "nodeB" : [FlowAction1, FlowAction2, ...] }
   * @return onvert original edgeActions
   * edge_actions : { "nodeA" : [FlowAction1, FlowAction2, ...] }
   *
   */
  protected Map<String, List<FlowAction>> convertAction2(
      String nwId,
      String nodeId,
      String portId,
      Map<String, List<FlowAction>> edgeActions) {

    log.debug("");

    Map<String, List<FlowAction>> newEdgeActions = new HashMap<String, List<FlowAction>>();
    String cnvPortId = getConvPortId(nwId, nodeId, portId);
    String[] cnvPortIds = cnvPortId.split(Federator.SEPARATOR);

    // Convert Action.
    for (String actNodeId : edgeActions.keySet()) {
      if (actNodeId.equals(nodeId)) {
        newEdgeActions.put(cnvPortIds[1], edgeActions.get(nodeId));
      }
    }

    List<FlowAction> actions = newEdgeActions.get(cnvPortIds[1]);
    if (actions == null) {
      actions = new ArrayList<FlowAction>();
    }

    actions.add(new FlowActionOutput(cnvPortIds[2]));
    newEdgeActions.put(cnvPortIds[1], actions);
    return newEdgeActions;
  }

  /**
   *
   * @param nwId  federator network Id
   * @param fedNodeId  federator edge node Id
   * @param edgeActions federated edgeActions
   * edge_actions : { "nodeA" : [FlowAction1, FlowAction2, ...],
   *                  "nodeB" : [FlowAction1, FlowAction2, ...] }
   * @return onvert original edgeActions
   * edge_actions : { "nodeA" : [FlowAction1, FlowAction2, ...] }
   *
   */
  protected Map<String, List<FlowAction>> convertAction(
      String nwId,
      String fedNodeId,
      Map<String, List<FlowAction>> edgeActions) {

    log.debug("");

    Map<String, List<FlowAction>> newEdgeActions = new HashMap<String, List<FlowAction>>();
    String cnvNodeId = getConvNodeId(nwId, fedNodeId);
    String[] cnvNodeIds = cnvNodeId.split(Federator.SEPARATOR);

    // Convert Action.
    for (String actNodeId : edgeActions.keySet()) {
      if (actNodeId.equals(fedNodeId)) {
        newEdgeActions.put(cnvNodeIds[1], edgeActions.get(fedNodeId));
      }
    }

    List<FlowAction> actions = newEdgeActions.get(cnvNodeIds[1]);
    if (actions == null) {
      actions = new ArrayList<FlowAction>();
    }

    FlowActionOutput newOutPort = null;
    int fedOutPortIndex = -1;
    for (int i = 0, n = actions.size(); i < n; i++) {
      FlowAction action = actions.get(i);
      if (!action.getType().equals(
          FlowActionOutput.class.getSimpleName())) {
        continue ;
      }

      FlowActionOutput output = (FlowActionOutput) action;
      String orgPorts = getConvPortId(nwId, fedNodeId, output.getOutput());
      if (orgPorts == null) {
        log.error("edge action out port convert Error.");
        continue ;
      }
      String[] orgPort = orgPorts.split(Federator.SEPARATOR);
      newOutPort = new FlowActionOutput(orgPort[2]);
      fedOutPortIndex = i;
      break;
    }

    if (fedOutPortIndex >= 0) {
      actions.remove(fedOutPortIndex);
    }

    if (newOutPort != null) {
      actions.add(newOutPort);
    }

    return newEdgeActions;
  }

  protected void doFlowAddedSetFlowRegister(String orgNwId, BasicFlow orgFlow) {
    log.debug("");

    String fedFlowId = orgFlow.getFlowId();
    String orgFlowId = assignOrgFlowId(orgNwId, fedFlowId);

    orgFlow.setFlowId(orgFlowId);
    networkInterfaces.get(orgNwId).putFlow(orgFlow);

    // update conversionTable
    String fedNwId = getNetworkIdByType(Federator.FEDERATED_NETWORK);
    conversionTable.addEntryFlow(orgNwId, orgFlowId, fedNwId, fedFlowId);
  }

  /**
   *
   * @param orgNwId   original network Id
   * @param fedFlowId federated flow id
   * @return oroginal flow id,  federated flow + _cnt + _multiple
   *
   */
  protected String assignOrgFlowId(String orgNwId, String fedFlowId) {
    log.debug("");

    String orgFlowId = fedFlowId;
    setOrgFlowCnt(fedFlowId);
    return orgFlowId + "_" + getOrgFlowCnt(fedFlowId).toString();
  }

  protected void setOrgFlowCnt(String flowId) {
    Integer cnt = getOrgFlowCnt(flowId);
    if (cnt == null) {
      orgFlowCnt.put(flowId, 1);
      return; 
    }
    orgFlowCnt.put(flowId, ++cnt);
  }

  protected Integer getOrgFlowCnt(String flowId) {
    return orgFlowCnt.get(flowId);
  }

  protected void delOrgFlowCnt(String flowId) {
    orgFlowCnt.remove(flowId);
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
    log.debug("");

    if (flow.getMatches() == null) {
      return false;
    }
    for (BasicFlowMatch match : flow.getMatches()) {
      match.setInNode(dstNodeId);
      match.setInPort(dstPortId);
    }
    return true;
  }

  protected String convertMatch(
      String fedNwId,
      BasicFlow flow) throws Exception {
    log.debug("");

    String networkId = null;
    for (BasicFlowMatch match : flow.getMatches()) {
      String fedNodeId = match.getInNode();
      String fedPortId = match.getInPort();
      String orgPorts = getConvPortId(fedNwId, fedNodeId, fedPortId);
      if (orgPorts == null) {
        continue;
      }
      String[] orgPList = orgPorts.split(Federator.SEPARATOR);
      networkId = orgPList[0];
      match.setInNode(orgPList[1]);
      match.setInPort(orgPList[2]);
    }
    return networkId;
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
    log.debug("");

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

  /**
   *
   * @param connType Type of the network.
   * @return ID for the network.
   */
  protected final String getNetworkIdByType(final String connType) {
    log.debug("");

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
    log.debug("");

    if (networkId == null || nodeId == null) {
      log.warn("invalid param");
      return null;
    }
    ArrayList<String> convNodeId = conversionTable.getNode(networkId, nodeId);
    if (convNodeId.size() == 0) {
      log.warn("invalid convNodeId");
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
    log.debug("");

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

  /**
   *
   * @param networkId ID for network.
   * @param flowId ID for link in the network.
   * @return ID link for in the federated network.
   */
  protected final String getConvFlowId(String networkId, String flowId) {
    if (networkId == null || flowId == null) {
      return null;
    }
    ArrayList<String> convFlowId = conversionTable.getFlow(networkId, flowId);
    if (convFlowId.size() == 0) {
      return null;
    }
    String[] fedFlowIds = convFlowId.get(0).split(Federator.SEPARATOR);
    return fedFlowIds[1];
  }

  /**
   *
   * @param networkId ID for network.
   * @param prev flow object.
   * @param curr flow object.
   * @return true : reroute, false : not reroute
   */
  protected boolean isReroute(String networkId, BasicFlow prev, BasicFlow curr) {

	boolean ret = false;
    /* update matches */
    List<BasicFlowMatch> prevMatches = prev.getMatches();
    List<BasicFlowMatch> currMatches = curr.getMatches();
    if (!prevMatches.equals(currMatches)) {
      ret = true;
    }
    /* update actions */
    Map<String, List<FlowAction>> prevEdgeActions = prev.getEdgeActions();
    Map<String, List<FlowAction>> currEdgeActions = curr.getEdgeActions();
    if (!prevEdgeActions.equals(currEdgeActions)) {
      ret = true;
    }
    /* update path */
    List<String> prevPath = prev.getPath();
    List<String> currPath = curr.getPath();
    if (!prevPath.equals(currPath)) {
      ret = true;
    }

    return ret;
  }
  
  protected void deleteOrignFlow(
      final String networkId, final Flow flow) {
    log.debug("");

    if (networkId == null || flow == null) {
      return;
    }

    ArrayList<String> orgFlows
        = conversionTable.getFlow(networkId, flow.getFlowId());

    for (String orgFlow : orgFlows) {
      String[] flowId = orgFlow.split("::");
      NetworkInterface networkIf = this.networkInterfaces.get(flowId[0]);
      if (networkIf == null) {
        continue;
      }
      networkIf.delFlow(flowId[1]);
    }

    // check delete flows.
    for (String orgFlow : orgFlows) {
      String[] flowId = orgFlow.split("::");
      NetworkInterface networkIf = this.networkInterfaces.get(flowId[0]);
      if (networkIf == null) {
        continue;
      }
      int retry  = 100;
      do {
        if (networkIf.getFlow(flowId[1]) == null) {
          break;
        }
        // sleep 1s
        try { Thread.sleep(1000); } catch(InterruptedException e){} 
        retry--;
      } while (retry > 0 );
    }
    conversionTable.delEntryFlow(networkId, flow.getFlowId());
  }
}
