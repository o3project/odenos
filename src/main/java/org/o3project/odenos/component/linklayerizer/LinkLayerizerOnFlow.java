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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.Component.AttrElements;
import org.o3project.odenos.core.component.Component.NetworkElements;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.remoteobject.message.Response;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LinkLayerizerOnFlow class.
 *
 */
public class LinkLayerizerOnFlow {
  /** logger. */
  private static final Logger log = LogManager.getLogger(LinkLayerizerOnFlow.class);

  /** Conversion Table instance. */
  protected ConversionTable conversionTable;

  /** Network Interfaces  instance. */
  protected Map<String, NetworkInterface> networkInterfaces;

  /** Boundary Table. */
  protected LinkLayerizerBoundaryTable boundaryTable;

  /**
   * Map of flows.
   * key   : linkId    (in layerized_nw)
   * value : flowId(s) (in lower_nw)
   */
  protected Map<String, List<String>> lowerFlows;

  /**
   * Map of links.
   *  key   : flowId (in lower_nw)
   *  value : linkId (in layerized_nw)
   */
  protected Map<String, String> layerizedLinks;

  /** String which indicates UP. */
  public static final String STATUS_UP = "UP";
  /** String which indicates DOWN. */
  public static final String STATUS_DOWN = "DOWN";

  /**
   * Constructors.
   * @param conversionTable conversion table instance.
   * @param networkInterfaces network interface instance.
   * @param boundaryTable boundary table instance.
   */
  public LinkLayerizerOnFlow(
      ConversionTable conversionTable,
      Map<String, NetworkInterface> networkInterfaces,
      LinkLayerizerBoundaryTable boundaryTable) {
    this.conversionTable = conversionTable;
    this.networkInterfaces = networkInterfaces;
    this.boundaryTable = boundaryTable;
    lowerFlows = new HashMap<>();
    layerizedLinks = new HashMap<>();
  }

  /**
   * Return map of links.
   * @return Map of the flow and the links.
   */
  public Map<String, String> getLayerizedLinks() {
    return layerizedLinks;
  }

  /**
   * Return map of flows.
   * @return Map of the link and the flow.
   */
  public Map<String, List<String>> getLowerFlows() {
    return lowerFlows;
  }

  /**
   * Add a layerizer network with path.
   * @param networkId ID for network.
   * @param basicFlow BasicFlow.
   */
  public void flowAddedLayerizedNwExistPath(
      String networkId, BasicFlow basicFlow) {
    log.debug("");

    String upperNwId = getNetworkIdByType(LinkLayerizer.UPPER_NETWORK);
    if (!checkParam(networkId, basicFlow) || upperNwId == null) {
      return;
    }

    NetworkInterface layerizedNwIf = networkInterfaces.get(networkId);
    NetworkInterface upperNwIf = networkInterfaces.get(upperNwId);

    BasicFlow upperFlow = basicFlow.clone();

    // check flow.path 
    Map<String, Link> upperLinks = upperNwIf.getLinks();
    for (String layerizedLinkId : basicFlow.getPath()) {
      if (!upperLinks.containsKey(layerizedLinkId)) {
        Link layLink = layerizedNwIf.getLink(layerizedLinkId);

        boolean notExistPath = true;
        for (String upperLinkId : upperLinks.keySet()) {
          Link upperLink = upperLinks.get(upperLinkId);
          Link compLink = upperLink.clone();
          compLink.setPorts(
              layLink.getSrcNode(), layLink.getSrcPort(),
              layLink.getDstNode(), layLink.getDstPort());
          if (compLink.equals(upperLink)) {
            log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "replace flow's path."));
            int index = upperFlow.getPath().indexOf(layerizedLinkId);
            upperFlow.getPath().set(index, upperLinkId);
            notExistPath = false;
            break;
          }
        }
        if (notExistPath) {
          log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "not exist path's link_id to upper_nw."));
          return;
        }
      }
    }

    // register flow
    registerUpperFlows(upperFlow);
  }

  /**
   * Add flow of lower network.
   * @param networkId ID for network.
   * @param basicFlow BasicFlow.
   */
  public void flowAddedLowerNw(
      String networkId,
      BasicFlow basicFlow) {
    log.debug("");

    if (!checkParam(networkId, basicFlow)) {
      return;
    }

    String status = basicFlow.getStatus();
    boolean enable = basicFlow.getEnabled();

    if (!(FlowStatus.ESTABLISHING.toString().equals(status) && enable)) {
      log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid lower flow. [status: {}, enable: {}].", status, enable));
      return;
    }

    addLayerizedLinkbyLowerFlow(networkId, basicFlow);
  }

  /**
   * Update flow with path.
   * @param networkId ID for network.
   * @param basicFlow BasicFlow.
   * @param attr Attribute of flow.
   */
  public void flowUpdateUpperNwExistPath(
      String networkId,
      BasicFlow basicFlow,
      List<String> attr) {
    log.debug("");

    if (!checkParam(networkId, basicFlow)) {
      return;
    }

    List<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }

    List<String> layerizedFlowIds =
        conversionTable.getFlow(networkId, basicFlow.getFlowId());
    if (layerizedFlowIds.size() == 0) {
      return;
    }

    // update layerized's flow.
    String[] layerizedFlowIdList =
        layerizedFlowIds.get(0).split(LinkLayerizer.SEPARATOR);
    updateFlow(layerizedFlowIdList[0], layerizedFlowIdList[1],
        basicFlow, attributesList);

    // update other upper's flows.
    List<String> upperFlowIds = conversionTable.getFlow(
        layerizedFlowIdList[0], layerizedFlowIdList[1]);
    if (upperFlowIds.size() < 2) {
      // not exist other upper's flows.
      return;
    }
    for (String upperFlowKey : upperFlowIds) {
      String[] upperFlowIdList =
          upperFlowKey.split(LinkLayerizer.SEPARATOR);
      if (basicFlow.getFlowId().equals(upperFlowIdList[1])) {
        continue;
      }
      updateFlow(upperFlowIdList[0], upperFlowIdList[1],
          basicFlow, attributesList);
    }
  }

  /**
   * Update a flow of lower network.
   * @param networkId ID for network.
   * @param basicFlow BasicFlow.
   * @param attr Attribute of flow.
   */
  public void flowUpdateLowerNw(
      String networkId,
      BasicFlow basicFlow,
      List<String> attr) {
    log.debug("");

    String status = basicFlow.getStatus();
    boolean enable = basicFlow.getEnabled();
    if ((FlowStatus.ESTABLISHING.toString().equals(status) && enable)) {
      addLayerizedLinkbyLowerFlow(networkId, basicFlow);
      return;
    }

    String flowId = basicFlow.getFlowId();
    String linkId = layerizedLinks.get(flowId);

    if ((FlowStatus.ESTABLISHED.toString().equals(status) && enable)
        && (linkId == null)) {
      addLayerizedLinkbyLowerFlow(networkId, basicFlow);
      return;
    }

    String layerizedId =
        getNetworkIdByType(LinkLayerizer.LAYERIZED_NETWORK);
    NetworkInterface layerizedIf = networkInterfaces.get(layerizedId);

    Link link = layerizedIf.getLink(linkId);
    setLayerizedLinkStatus(link, basicFlow);
    layerizedIf.putLink(link);

    if (!FlowStatus.FAILED.toString().equals(basicFlow.getStatus())) {
      return;
    }

    if (!checkParam(networkId, basicFlow)) {
      return;
    }

    FlowSet flowSet = layerizedIf.getFlowSet();

    Map<String, Flow> flowMap = flowSet.getFlows();
    for (Flow flow : flowMap.values()) {
      if (!(flow instanceof BasicFlow)) {
        continue;
      }
      BasicFlow bflow = (BasicFlow) flow;

      List<String> paths = bflow.getPath();
      if (CollectionUtils.isEmpty(paths)) {
        continue;
      }
      String path = paths.get(0);
      String linkid = link.getId();

      if (!StringUtils.contains(path, linkid)) {
        continue;
      }

      flow.setStatus(FlowStatus.FAILED.toString());
      layerizedIf.putFlow(flow);
    }

  }

  /**
   * Remove a flow of lower lower network.
   * @param networkId ID for network.
   * @param basicFlow BasicFlow.
   */
  public void flowDeleteLowerNw(
      String networkId,
      BasicFlow basicFlow) {
    log.debug("");

    if (!checkParam(networkId, basicFlow)) {
      return;
    }

    String flowId = basicFlow.getFlowId();
    String linkId = layerizedLinks.get(flowId);

    String layerizedId =
        getNetworkIdByType(LinkLayerizer.LAYERIZED_NETWORK);
    NetworkInterface layerizedIf = networkInterfaces.get(layerizedId);

    layerizedIf.delLink(linkId);

    // update layerizedLinks
    layerizedLinks.remove(flowId);
    // update lowerFlows
    List<String> flows = lowerFlows.get(linkId);
    if (flows == null) {
      lowerFlows.remove(linkId);
    } else if (flows != null && flows.size() < 2) {
      flows.clear();
      lowerFlows.remove(linkId);
    } else {
      flows.remove(flowId);
    }

  }

  protected void updateFlow(
      String nwId,
      String dstFlowId,
      BasicFlow srcFlow,
      List<String> attributesList) {
    log.debug("");

    if (StringUtils.isEmpty(nwId) || StringUtils.isEmpty(dstFlowId)
        || srcFlow == null || attributesList == null) {
      return;
    }

    // make ignore list
    List<String> messageIgnoreKeys =
        getIgnoreKeys(Logic.keysFlow, attributesList);
    List<String> messageIgnoreAttributes =
        getIgnoreKeys(Logic.attributesFlow, attributesList);

    NetworkInterface nwIf = networkInterfaces.get(nwId);
    Flow dstFlow = nwIf.getFlow(dstFlowId);

    boolean updated = false;

    // key copy
    if (!messageIgnoreKeys.contains(NetworkElements.ENABLED)
        && (dstFlow.getEnabled() != srcFlow.getEnabled())) {
      updated = true;
      dstFlow.setEnabled(srcFlow.getEnabled());
    }

    if (!messageIgnoreKeys.contains(NetworkElements.PRIORITY)
        && (!dstFlow.getPriority().equals(srcFlow.getPriority()))) {
      updated = true;
      dstFlow.setPriority(srcFlow.getPriority());
    }

    if (!messageIgnoreKeys.contains(NetworkElements.STATUS)
        && (!dstFlow.getStatus().equals(srcFlow.getStatus()))) {
      updated = true;
      dstFlow.setStatus(srcFlow.getStatus());
    }

    // attributes copy
    Map<String, String> currAttributes = srcFlow.getAttributes();
    for (String key : currAttributes.keySet()) {
      String oldAttr = dstFlow.getAttribute(key);
      if (messageIgnoreAttributes.contains(key)
          || (oldAttr != null
          && oldAttr.equals(currAttributes.get(key)))) {
        continue;
      }
      updated = true;
      dstFlow.putAttribute(key, currAttributes.get(key));
    }
    if (updated) {
      // PUT Flow
      nwIf.putFlow(dstFlow);
    }

  }

  protected void registerUpperFlows(BasicFlow upperFlow) {
    log.debug("");

    String layerizedNwId = getNetworkIdByType(LinkLayerizer.LAYERIZED_NETWORK);
    String upperNwId = getNetworkIdByType(LinkLayerizer.UPPER_NETWORK);
    if (layerizedNwId == null || upperNwId == null) {
      return;
    }
    NetworkInterface upperNwIf = networkInterfaces.get(upperNwId);

    upperNwIf.putFlow(upperFlow);
    conversionTable.addEntryFlow(
        layerizedNwId, upperFlow.getFlowId(),
        upperNwId, upperFlow.getFlowId());
    return;
  }

  protected void addLayerizedLinkbyLowerFlow(
      String networkId,
      BasicFlow basicFlow) {
    log.debug("");
    checkParam(networkId, basicFlow);

    String status = basicFlow.getStatus();
    boolean enable = basicFlow.getEnabled();

    if ((FlowStatus.NONE.toString().equals(status))
      ||(FlowStatus.TEARDOWN.toString().equals(status))
      ||(FlowStatus.FAILED.toString().equals(status))) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid flow's status."));
        return;
    }

    if (!enable) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid flow's disable."));
      return;
    }

    String lowerNwId = getNetworkIdByType(LinkLayerizer.LOWER_NETWORK);
    if (lowerNwId == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "lower_nw not exist."));
      return;
    }

    LinkLayerizerBoundary srcBoundary =
        getBoundaryByMatches(lowerNwId, basicFlow.getMatches());
    LinkLayerizerBoundary dstBoundary =
        getBoundaryByActions(lowerNwId, basicFlow.getEdgeActions());
    if (srcBoundary == null || dstBoundary == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid lowerFlow."));
      return;
    }

    String linkId = createLinkId(
        srcBoundary.getId(), dstBoundary.getId());
    Link link = new Link(linkId,
        srcBoundary.getUpperNwNode(),
        srcBoundary.getUpperNwPort(),
        dstBoundary.getUpperNwNode(),
        dstBoundary.getUpperNwPort());

    setLayerizedLinkStatus(link, basicFlow);

    String layerizedId =
        getNetworkIdByType(LinkLayerizer.LAYERIZED_NETWORK);
    if (layerizedId == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "laerized_nw not exist."));
      return;
    }

    Response resp = networkInterfaces.get(layerizedId).putLink(link);
    if (resp.isError("PUT")) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "failed PUT Link. response: {}", resp.getBodyValue()));
      return;
    }

    // update layerizedLinks
    layerizedLinks.put(basicFlow.getFlowId(), link.getId());
    // update lowerFlows
    List<String> flows = lowerFlows.get(link.getId());
    if (flows == null) {
      flows = new ArrayList<>();
      lowerFlows.put(link.getId(), flows);
    }
    flows.add(basicFlow.getFlowId());

    return;

  }

  /**
   *
   * @param link Link of target.
   * @param flow Flow of status.
   * @return true: success. false: failed.
   */
  protected boolean setLayerizedLinkStatus(Link link, Flow flow) {
    log.debug("");

    if ((link == null) || (flow == null)) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "parameter is null"));
      return false;
    }

    link.putAttribute(AttrElements.COST, "1");
    for (String flowKey : flow.getAttributes().keySet()) {
      switch (flowKey) {
        case AttrElements.REQ_LATENCY:
          link.putAttribute(AttrElements.REQ_LATENCY, flow.getAttribute(flowKey));
          break;

        case AttrElements.LATENCY:
          link.putAttribute(AttrElements.LATENCY, flow.getAttribute(flowKey));
          break;

        case AttrElements.REQ_BANDWIDTH:
          link.putAttribute(AttrElements.REQ_BANDWIDTH, flow.getAttribute(flowKey));
          break;

        case AttrElements.BANDWIDTH:
          link.putAttribute(AttrElements.MAX_BANDWIDTH, flow.getAttribute(flowKey));
          link.putAttribute(AttrElements.UNRESERVED_BANDWIDTH, flow.getAttribute(flowKey));
          break;

        default:
          // original attributes
          link.putAttribute(flowKey, flow.getAttribute(flowKey));
          break;
      }
    }

    String status = flow.getStatus();
    FlowStatus flowStatus = FlowStatus.messageValueOf(status);
    switch (flowStatus) {
      case ESTABLISHING:
        link.putAttribute(AttrElements.ESTABLISHMENT_STATUS,
            FlowStatus.ESTABLISHING.toString());
        link.putAttribute(AttrElements.OPER_STATUS, STATUS_DOWN);
        break;

      case FAILED:
        link.putAttribute(AttrElements.ESTABLISHMENT_STATUS,
            FlowStatus.FAILED.toString());
        link.putAttribute(AttrElements.OPER_STATUS, STATUS_DOWN);
        break;

      case ESTABLISHED:
        link.putAttribute(AttrElements.ESTABLISHMENT_STATUS,
            FlowStatus.ESTABLISHED.toString());
        link.putAttribute(AttrElements.OPER_STATUS, STATUS_UP);
        break;

      default:
        log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "unknown status: {}", status));
        return false;
    }

    return true;
  }

  /**
   *
   * @param lowerNwId ID for lower network.
   * @param matches List of the match.
   * @return Got the boundary.
   */
  protected LinkLayerizerBoundary getBoundaryByMatches(
      String lowerNwId, List<BasicFlowMatch> matches) {
    log.debug("");

    if (StringUtils.isEmpty(lowerNwId)
        || matches == null || matches.size() == 0) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid parameter"));
      return null;
    }
    String inNode = matches.get(0).getInNode();
    String inPort = matches.get(0).getInPort();
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "nwId : {}, inNode : {}, inPort : {}.",
        lowerNwId, inNode, inPort));

    return boundaryTable.getBoundary(
        lowerNwId, inNode, inPort);
  }

  /**
   *
   * @param lowerNwId ID for lower network.
   * @param edgeActions Map of the action.
   * @return Got the boundary.
   */
  protected LinkLayerizerBoundary getBoundaryByActions(
      String lowerNwId, Map<String, List<FlowAction>> edgeActions) {
    log.debug("");

    if (StringUtils.isEmpty(lowerNwId) || edgeActions == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid parameter"));
      return null;
    }
    String outNode = null;
    String outPort = null;
    for (String nodeId : edgeActions.keySet()) {
      List<FlowAction> actions = edgeActions.get(nodeId);
      for (FlowAction act : actions) {
        if (act instanceof FlowActionOutput) {
          FlowActionOutput actOutput = (FlowActionOutput) act;
          outNode = nodeId;
          // output is only one.
          outPort = actOutput.getOutput();
          break;
        }
      }
    }
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "nwId : {}, outNode : {}, outPort : {}.",
        lowerNwId, outNode, outPort));
    if (outNode == null || outPort == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid lowerFlow's actions."));
      return null;
    }

    return boundaryTable.getBoundary(
        lowerNwId, outNode, outPort);
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
   * @param allkeys List of all keys.
   * @param updatekeys List of update keys.
   * @return List of the keys which aren't updated.
   */
  protected final List<String> getIgnoreKeys(
      final List<String> allkeys,
      final List<String> updatekeys) {

    ArrayList<String> ignorekeys = new ArrayList<String>();
    for (String key : allkeys) {
      ignorekeys.add(key);
    }

    String regex = "^" + AttrElements.ATTRIBUTES + "::.*";
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
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "ignore key_list:: {}", ignorekeys));
    return ignorekeys;
  }

  /**
   *
   * @param networkId ID for the network.
   * @param flow Flow
   * @return true: Right parameter. false: Wrong parameter.
   */
  protected final boolean checkParam(String networkId, BasicFlow flow) {
    log.debug("");

    if (StringUtils.isEmpty(networkId) || flow == null) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid parameter"));
      return false;
    }
    return true;
  }

  protected final String createLinkId(String srcBoundaryId, String dstBoundaryId) {
    log.debug("");

    String id = String.format("%s_%s", srcBoundaryId, dstBoundaryId);
    return id;
  }

}
