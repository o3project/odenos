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
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LinkLayerizer integrates networks of different layers, to create a multi-layer network.
 *
 */
public class LinkLayerizer extends Logic {

  /** logger. */
  private static final Logger log = LoggerFactory.getLogger(LinkLayerizer.class);

  public static final String LAYERIZED_NETWORK = "layerized";

  public static final String UPPER_NETWORK = "upper";

  public static final String LOWER_NETWORK = "lower";

  /** name separator for ConversionTable. */
  public static final String SEPARATOR = "::";

  /** RequestPaeser Instance. */
  protected final RequestParser<IActionCallback> parser;

  /** Description of Component. */
  private static final String DESCRIPTION = "LinkLayerizer Component";

  /** Flag that is link synchronization in upper network. */
  protected boolean upperLinkSync = true; /* default true */
  /** Boundary Table. */
  protected LinkLayerizerBoundaryTable linkLayerizerBoundaryTable;
  /** LinkLayerizerOnFlow instance. */
  protected LinkLayerizerOnFlow linkLayerizerOnFlow;

  /**
   * Constructor.
   *
   * @param objectId ID for object.
   * @param dispatcher Message dispatcher instance.
   * @throws Exception if parameter is wrong.
   */
  public LinkLayerizer(String objectId, MessageDispatcher dispatcher)
      throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    linkLayerizerBoundaryTable = new LinkLayerizerBoundaryTable();
    linkLayerizerOnFlow = new LinkLayerizerOnFlow(
        conversionTable(),
        networkInterfaces(),
        linkLayerizerBoundaryTable);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.component.Component#getSuperType()
   */
  @Override
  protected String getSuperType() {
    return LinkLayerizer.class.getSimpleName();
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.component.Component#getDescription()
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  /**
   *  Get Connection Types of Component.
   *
   *  @return Connection Types
   */
  @Override
  protected String getConnectionTypes() {
    // <connection type>:<connection number>,...
    return String.format("%s:1,%s:1,%s:1", LAYERIZED_NETWORK, UPPER_NETWORK,
        LOWER_NETWORK);
  }

  /**
   * Returns status UpperLinkSync.
   * @return upperLinkisync
   */
  public boolean isUpperLinkisync() {
    return upperLinkSync;
  }

  /**
   * Sets flag UpperLinkSync.
   * @param upperLinkisync status UpperLinkSync.
   */
  public void setUpperLinkisync(boolean upperLinkisync) {
    this.upperLinkSync = upperLinkisync;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedAddedPre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedAddedPre(
      ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(curr
        .getObjectType())) {
      return false;
    }

    String logicId = curr
        .getProperty(ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!getObjectId().equals(logicId)) {
      return false;
    }

    boolean exist = false;

    String connectionType = curr.getConnectionType();
    switch (connectionType) {
      case LOWER_NETWORK:
        exist = isConnectionType(LOWER_NETWORK);
        break;
      case UPPER_NETWORK:
        exist = isConnectionType(UPPER_NETWORK);
        break;
      case LAYERIZED_NETWORK:
        exist = isConnectionType(LAYERIZED_NETWORK);
        break;
      default:
        /* unknown type */
        exist = true;
    }

    if (exist) {
      String status = ComponentConnection.State.ERROR;
      curr.setConnectionState(status);
      systemMngInterface().putConnection(curr);

      return false;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedUpdatePre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedUpdatePre(
      ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(curr
        .getObjectType())) {
      return false;
    }

    String logicId = curr
        .getProperty(ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!getObjectId().equals(logicId)) {
      return false;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedDeletePre(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected boolean onConnectionChangedDeletePre(
      ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(curr
        .getObjectType())) {
      return false;
    }

    String logicId = curr
        .getProperty(ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!getObjectId().equals(logicId)) {
      return false;
    }

    boolean exist = false;

    String connectionType = curr.getConnectionType();
    switch (connectionType) {
      case LOWER_NETWORK:
        exist = isConnectionType(LOWER_NETWORK);
        break;
      case UPPER_NETWORK:
        exist = isConnectionType(UPPER_NETWORK);
        break;
      case LAYERIZED_NETWORK:
        exist = isConnectionType(LAYERIZED_NETWORK);
        break;
      default:
        /* unknown type */
        exist = false;
    }

    if (exist == false) {
      String status = ComponentConnection.State.ERROR;
      curr.setConnectionState(status);
      systemMngInterface().putConnection(curr);

      return false;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedAdded(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedAdded(ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();
    String networkId = curr
        .getProperty(ComponentConnectionLogicAndNetwork.NETWORK_ID);

    String connectionType = curr.getConnectionType();
    conversionTable().addEntryConnectionType(networkId, connectionType);
    curr.setConnectionState(ComponentConnection.State.RUNNING);

    /*
     * not register lower_nw to conversionTable
     */
    switch (connectionType) {
      case LOWER_NETWORK:
        subscribeLower(networkId);
        doOnConnectionChangedAddedLower(networkId);
        break;

      case UPPER_NETWORK:
        subscribeUpper(networkId);
        ArrayList<String> layerizeds =
            conversionTable().getConnectionList(LAYERIZED_NETWORK);
        if (layerizeds.size() == 1) {
          // Update conversionTable.
          conversionTable().addEntryNetwork(layerizeds.get(0), networkId);
          doOnConnectionChangedAddedUpper(networkId);
        }
        break;

      case LAYERIZED_NETWORK:
        subscribeLayerized(networkId);
        ArrayList<String> uppers =
            conversionTable().getConnectionList(UPPER_NETWORK);
        if (uppers.size() == 1) {
          // Update conversionTable.
          conversionTable().addEntryNetwork(uppers.get(0), networkId);
          doOnConnectionChangedAddedLayerized(networkId);
        }
        break;

      default:
        String errorMessage = "unknown type: " + connectionType;
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    // Changed ConectionProperty's status.
    systemMngInterface().putConnection(curr);

  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedUpdate(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedUpdate(ComponentConnectionChanged message) {
    log.debug("");

    /*
     * do nothing
     */
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onConnectionChangedDelete(org.o3project.odenos.manager.system.event.ComponentConnectionChanged)
   */
  @Override
  protected void onConnectionChangedDelete(ComponentConnectionChanged message) {
    log.debug("");

    ComponentConnection curr = message.curr();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    String networkId = curr
        .getProperty(ComponentConnectionLogicAndNetwork.NETWORK_ID);
    String connectionType = curr.getConnectionType();
    switch (connectionType) {
      case LOWER_NETWORK:
        doOnConnectionChangedDeleteLower(networkId);
        break;
      case UPPER_NETWORK:
        doOnConnectionChangedDeleteUpper(networkId);
        break;
      case LAYERIZED_NETWORK:
        doOnConnectionChangedDeleteLayerized(networkId);
        break;
      default:
        log.error("unknown type: {}", connectionType);
        return;
    }

    // reset conversionTable.
    conversionTable().delEntryConnectionType(networkId);
    conversionTable().delEntryNetwork(networkId);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  protected void doOnConnectionChangedAddedLower(String lowerId) {
    log.debug("");

    List<String> layerizedIds = getLayerizedNetworkIds();
    if (CollectionUtils.isNotEmpty(layerizedIds)) {

      NetworkInterface layerizedIf = getNetworkIf(layerizedIds);
      Map<String, List<String>> lowerFlows =
          linkLayerizerOnFlow.getLowerFlows();
      Set<String> linkSet = lowerFlows.keySet();
      for (String linkId : linkSet) {
        layerizedIf.delLink(linkId);
        lowerFlows.remove(linkId);

      }
      setBoundaryPortAttr();
    }

    List<String> upperIds = getUpperNetworkIds();
    if (CollectionUtils.isNotEmpty(upperIds)) {

      NetworkInterface upperIf = getNetworkIf(upperIds);
      FlowSet flowSet = upperIf.getFlowSet(); // TODO null check
      for (Flow flow : flowSet.getFlows().values()) {
        if (!(flow instanceof BasicFlow)) {
          continue;
        }
        BasicFlow basicFlow = (BasicFlow) flow;
        linkLayerizerOnFlow.flowAddedLowerNw(
            lowerId, basicFlow);
      }
    }
  }

  protected void doOnConnectionChangedAddedUpper(String upperId) {
    log.debug("");

    List<String> layerizedIds = getLayerizedNetworkIds();
    if (CollectionUtils.isNotEmpty(layerizedIds)) {

      NetworkInterface layerizedNwif = getNetworkIf(layerizedIds);
      layerizedNwif.deleteTopology();

      setBoundaryPortAttr();
    }

    List<String> lowerIds = getLowerNetworkIds();
    if (CollectionUtils.isNotEmpty(lowerIds)) {

      NetworkInterface lowerNwif = getNetworkIf(lowerIds);
      FlowSet flowSet = lowerNwif.getFlowSet();

      Map<String, Flow> flowMap = flowSet.getFlows();
      Collection<Flow> flows = flowMap.values();
      for (Flow flow : flows) {
        if (!(flow instanceof BasicFlow)) {
          continue;
        }
        BasicFlow basicFlow = (BasicFlow) flow;
        linkLayerizerOnFlow.flowAddedLowerNw(
            lowerNwif.getNetworkId(), basicFlow);
      }
    }

  }

  protected void doOnConnectionChangedAddedLayerized(String layeriedId) {
    log.debug("");

    List<String> upperIds = getUpperNetworkIds();
    if (CollectionUtils.isNotEmpty(upperIds)) {

      NetworkInterface upperNwif = getNetworkIf(upperIds);

      linkLayerizerOnFlow.getLowerFlows().clear();
      linkLayerizerOnFlow.getLayerizedLinks().clear();

      Topology topology = upperNwif.getTopology();
      NetworkInterface layerizedNwif = networkInterfaces()
          .get(layeriedId);

      Map<String, Node> nodes = topology.getNodeMap();
      for (Node node : nodes.values()) {

        layerizedNwif.putNode(node);

        Map<String, Port> ports = node.getPortMap();
        for (Port port : ports.values()) {
          layerizedNwif.putPort(port);
        }
      }

      Map<String, Link> links = topology.getLinkMap();
      for (Link link : links.values()) {
        layerizedNwif.putLink(link);
      }

    }

    List<String> lowerIds = getLowerNetworkIds();
    if (CollectionUtils.isNotEmpty(lowerIds)) {

      NetworkInterface lowerNwif = getNetworkIf(lowerIds);

      FlowSet flowSet = lowerNwif.getFlowSet();

      Map<String, Flow> flowMap = flowSet.getFlows();
      Collection<Flow> flows = flowMap.values();
      for (Flow flow : flows) {
        if (!(flow instanceof BasicFlow)) {
          continue;
        }
        BasicFlow basicFlow = (BasicFlow) flow;
        linkLayerizerOnFlow.flowAddedLowerNw(
            lowerNwif.getNetworkId(), basicFlow);
      }
    }
  }

  protected void doOnConnectionChangedDeleteLower(String lowerId) {
    log.debug("");

    unsubscribeLower(lowerId);

    NetworkInterface layerizedIf = getLayerizedNetworkIf();
    if (layerizedIf == null) {
      return;
    }
    // delete layerized's  boundary link.
    for (String linkId : linkLayerizerOnFlow.getLowerFlows().keySet()) {
      layerizedIf.delLink(linkId);
    }

    // reset layerizedLinks & lowerFlows.
    linkLayerizerOnFlow.getLayerizedLinks().clear();
    linkLayerizerOnFlow.getLowerFlows().clear();
  }

  protected void doOnConnectionChangedDeleteUpper(String upperId) {
    log.debug("");

    unsubscribeUpper(upperId);

    NetworkInterface upperIf = getUpperNetworkIf();

    // delete upper's  boundary link
    for (String linkId : linkLayerizerOnFlow.getLowerFlows().keySet()) {
      upperIf.delLink(linkId);
    }

    // delete upper flows.
    upperIf.deleteAllFlow();

    NetworkInterface layerizedIf = getLayerizedNetworkIf();
    if (layerizedIf == null) {
      return;
    }

    // update layerized flow's status.
    layerizedIf.putStatusFaildAllFlow();

    // delete layerized's topology.
    Map<String, Node> nodes = layerizedIf.getNodes();
    for (Node node : nodes.values()) {

      String nodeId = node.getId();
      Map<String, Port> ports = node.getPortMap();
      for (Port port : ports.values()) {

        String portId = port.getId();
        layerizedIf.delPort(nodeId, portId);
      }

      layerizedIf.delNode(nodeId);
    }

    Map<String, Link> links = layerizedIf.getLinks();
    for (Link link : links.values()) {

      String linkId = link.getId();
      layerizedIf.delLink(linkId);
    }

    // reset conversionTable.
    conversionTable().getLink().clear();
    conversionTable().getPort().clear();
    conversionTable().getNode().clear();
    conversionTable().getFlow().clear();
    // reset layerizedLinks & lowerFlows
    linkLayerizerOnFlow.getLayerizedLinks().clear();
    linkLayerizerOnFlow.getLowerFlows().clear();
  }

  protected void doOnConnectionChangedDeleteLayerized(String layerizedId) {
    log.debug("");

    unsubscribeLayerized(layerizedId);

    // delete layerized's topology
    NetworkInterface layerizedIf = getLayerizedNetworkIf();
    Map<String, Node> nodes = layerizedIf.getNodes();
    for (Node node : nodes.values()) {

      String nodeId = node.getId();

      Map<String, Port> ports = node.getPortMap();
      for (Port port : ports.values()) {

        String portId = port.getId();
        layerizedIf.delPort(nodeId, portId);
      }

      layerizedIf.delNode(nodeId);
    }
    Map<String, Link> links = layerizedIf.getLinks();
    for (Link link : links.values()) {

      String linkId = link.getId();
      layerizedIf.delLink(linkId);
    }

    // update layerized flow's status. 
    layerizedIf.putStatusFaildAllFlow();

    NetworkInterface upperIf = getUpperNetworkIf();
    if (upperIf == null) {
      return;
    }

    // delete upper's all flows.
    upperIf.deleteAllFlow();

    // delete upper's  boundary link.
    for (String linkId : linkLayerizerOnFlow.getLowerFlows().keySet()) {
      upperIf.delLink(linkId);
    }

    // reset conversionTable.
    conversionTable().getLink().clear();
    conversionTable().getPort().clear();
    conversionTable().getNode().clear();
    conversionTable().getFlow().clear();
    // reset layerizedLinks & lowerFlows
    linkLayerizerOnFlow.getLayerizedLinks().clear();
    linkLayerizerOnFlow.getLowerFlows().clear();
  }

  protected void subscribeLower(final String lowerId) {
    log.debug("");

    try {
      addEntryEventSubscription(PORT_CHANGED, lowerId);
      addEntryEventSubscription(FLOW_CHANGED, lowerId);

      ArrayList<String> flowAttributes = new ArrayList<String>();
      updateEntryEventSubscription(FLOW_CHANGED, lowerId, flowAttributes);

      applyEventSubscription();

    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  protected void subscribeUpper(final String upperId) {
    log.debug("");

    try {
      addEntryEventSubscription(NODE_CHANGED, upperId);
      addEntryEventSubscription(PORT_CHANGED, upperId);
      addEntryEventSubscription(LINK_CHANGED, upperId);
      addEntryEventSubscription(FLOW_CHANGED, upperId);
      addEntryEventSubscription(IN_PACKET_ADDED, upperId);

      String attrBase = AttrElements.ATTRIBUTES + SEPARATOR + "%s";

      ArrayList<String> nodeAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.PHYSICAL_ID),
              String.format(attrBase, AttrElements.VENDOR)));
      updateEntryEventSubscription(NODE_CHANGED, upperId, nodeAttributes);

      ArrayList<String> portAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.MAX_BANDWIDTH),
              String.format(attrBase, AttrElements.PHYSICAL_ID),
              String.format(attrBase, AttrElements.VENDOR)));
      updateEntryEventSubscription(PORT_CHANGED, upperId, portAttributes);

      ArrayList<String> linkAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.OPER_STATUS),
              String.format(attrBase, AttrElements.LATENCY),
              String.format(attrBase, AttrElements.MAX_BANDWIDTH)));
      updateEntryEventSubscription(LINK_CHANGED, upperId, linkAttributes);

      ArrayList<String> flowAttributes = new ArrayList<String>(
          Arrays.asList(
              NetworkElements.STATUS,
              String.format(attrBase, AttrElements.REQ_BANDWIDTH),
              String.format(attrBase, AttrElements.REQ_LATENCY)));
      updateEntryEventSubscription(FLOW_CHANGED, upperId, flowAttributes);

      applyEventSubscription();

    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  protected void subscribeLayerized(final String layerizedId) {
    log.debug("");

    try {
      addEntryEventSubscription(LINK_CHANGED, layerizedId);
      addEntryEventSubscription(FLOW_CHANGED, layerizedId);
      addEntryEventSubscription(OUT_PACKET_ADDED, layerizedId);

      String attrBase = AttrElements.ATTRIBUTES + SEPARATOR + "%s";

      ArrayList<String> nodeAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.ADMIN_STATUS)));
      updateEntryEventSubscription(NODE_CHANGED, layerizedId,
          nodeAttributes);

      ArrayList<String> portAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase,
                  AttrElements.UNRESERVED_BANDWIDTH),
              String.format(attrBase, AttrElements.IS_BOUNDARY)));
      updateEntryEventSubscription(PORT_CHANGED, layerizedId,
          portAttributes);

      ArrayList<String> linkAttributes = new ArrayList<String>(
          Arrays.asList(
              String.format(attrBase, AttrElements.COST),
              String.format(attrBase, AttrElements.REQ_LATENCY),
              String.format(attrBase,
                  AttrElements.UNRESERVED_BANDWIDTH),
              String.format(attrBase, AttrElements.REQ_BANDWIDTH)));
      updateEntryEventSubscription(LINK_CHANGED, layerizedId,
          linkAttributes);

      ArrayList<String> flowAttributes = new ArrayList<String>(
          Arrays.asList(
              NetworkElements.OWNER,
              NetworkElements.ENABLED,
              NetworkElements.PRIORITY,
              String.format(attrBase, AttrElements.BANDWIDTH),
              String.format(attrBase, AttrElements.LATENCY)));
      updateEntryEventSubscription(FLOW_CHANGED, layerizedId,
          flowAttributes);

      applyEventSubscription();

    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  /**
   * Unsubscribe of lower network.
   * @param lowerid ID for lower network.
   */
  protected void unsubscribeLower(final String lowerid) {
    log.debug("");

    try {
      removeEntryEventSubscription(FLOW_CHANGED, lowerid);

      applyEventSubscription();

    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
    }
  }

  /**
   * Unsubscribe of upper network.
   * @param upperId ID for upper network.
   */
  protected void unsubscribeUpper(final String upperId) {
    log.debug("");

    try {
      removeEntryEventSubscription(NODE_CHANGED, upperId);
      removeEntryEventSubscription(PORT_CHANGED, upperId);
      removeEntryEventSubscription(LINK_CHANGED, upperId);
      removeEntryEventSubscription(FLOW_CHANGED, upperId);
      removeEntryEventSubscription(IN_PACKET_ADDED, upperId);

      applyEventSubscription();

    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
    }
  }

  /**
   * Unsubscribe of layerized network..
   * @param layerizedId ID for layerizer network.
   */
  protected void unsubscribeLayerized(final String layerizedId) {
    log.debug("");

    try {
      removeEntryEventSubscription(NODE_CHANGED, layerizedId);
      removeEntryEventSubscription(PORT_CHANGED, layerizedId);
      removeEntryEventSubscription(LINK_CHANGED, layerizedId);
      removeEntryEventSubscription(FLOW_CHANGED, layerizedId);
      removeEntryEventSubscription(OUT_PACKET_ADDED, layerizedId);

      applyEventSubscription();

    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
    }
  }

  /* //////////////////////////////////////////////////
   *
   * Request Event
   *
   * //////////////////////////////////////////////////
   */

  /**
   *
   * @return RequestParser for LinkLayerizer.
   */
  private RequestParser<IActionCallback> createParser() {
    log.debug("");

    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.PUT,
            "settings/upper_link_sync",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                HashMap<String, Boolean> syncMap =
                    (HashMap<String, Boolean>) parsed
                        .getRequest().getBodyAsMap(
                            Boolean.class);
                return putUpperLinkSync(syncMap.get("sync"));
              }
            });
        addRule(Method.POST,
            "settings/boundaries",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                LinkLayerizerBoundary boundary = parsed
                    .getRequest().getBody(
                        LinkLayerizerBoundary.class);
                return postBoundary(boundary);
              }
            });
        addRule(Method.GET,
            "settings/boundaries",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getBoundaries();
              }
            });
        addRule(Method.GET,
            "settings/boundaries/<boundary_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String boundaryId = parsed.getParam("boundary_id");
                return getBoundary(boundaryId);
              }
            });
        addRule(Method.PUT,
            "settings/boundaries/<boundary_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                LinkLayerizerBoundary boundary = parsed
                    .getRequest().getBody(
                        LinkLayerizerBoundary.class);
                String boundaryId = parsed.getParam("boundary_id");
                return putBoundary(boundaryId, boundary);
              }
            });
        addRule(Method.DELETE,
            "settings/boundaries/<boundary_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String boundaryId = parsed.getParam("boundary_id");
                return deleteBoundary(boundaryId);
              }
            });
        addRule(Method.GET,
            "lower_flows",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getLowerFlows();
              }
            });
        addRule(Method.GET,
            "lower_flows/<link_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String linkId = parsed.getParam("link_id");
                return getLowerFlows(linkId);
              }
            });
        addRule(Method.GET,
            "layerized_links",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getLayerizedlinks();
              }
            });
        addRule(Method.GET,
            "layerized_links/<flow_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String flowId = parsed.getParam("flow_id");
                return getLayerizedLink(flowId);
              }
            });
      }
    };
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.remoteobject.RemoteObject#onRequest(org.o3project.odenos.remoteobject.message.Request)
   */
  @Override
  protected Response onRequest(Request request) {
    log.debug("received {}", request.path);

    try {
      RequestParser<IActionCallback>.ParsedRequest parsed =
          parser.parse(request);
      if (parsed == null) {
        return new Response(Response.BAD_REQUEST,
            "Error unknown request ");
      }

      IActionCallback callback = parsed.getResult();
      if (callback == null) {
        return new Response(Response.BAD_REQUEST,
            "Error unknown request ");
      }
      // Get response.
      return callback.process(parsed);

    } catch (Exception ex) {
      log.error("Error unknown request", ex);
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }

  }

  /*
   * //////////////////////////////////////////////////
   *
   * Event method override
   *
   * //////////////////////////////////////////////////
   */

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onPortAddedPre(java.lang.String, org.o3project.odenos.component.network.topology.Port)
   */
  @Override
  protected boolean onPortAddedPre(String networkId, Port port) {
    log.debug("");

    if (!isLowerNetwork(networkId)) {
      return true;
    }

    if (!linkLayerizerBoundaryTable.isBoudaryPort(
        networkId, port.getNode(), port.getId())) {
      return false;
    }

    NetworkInterface nwIf = networkInterfaces().get(networkId);
    Port boundaryPort = nwIf.getPort(port.getNode(), port.getId());
    if (boundaryPort != null) {
      boundaryPort.putAttribute(AttrElements.IS_BOUNDARY, "true");
      nwIf.putPort(boundaryPort);
    }

    return false;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onPortAddedPost(java.lang.String, org.o3project.odenos.component.network.topology.Port, java.util.HashMap)
   */
  @Override
  protected void onPortAddedPost(
      String networkId, Port port, HashMap<String, Response> respList) {
    log.debug("");

    if (!isUpperNetwork(networkId)) {
      return;
    }

    if (!linkLayerizerBoundaryTable.isBoudaryPort(
        networkId, port.getNode(), port.getId())) {
      return;
    }
    NetworkInterface nwIf = networkInterfaces().get(networkId);
    Port boundaryPort = nwIf.getPort(port.getNode(), port.getId());
    if (boundaryPort == null) {
      return;
    }
    
    // update upper's port attribute.
    boundaryPort.putAttribute(AttrElements.IS_BOUNDARY, "true");
    nwIf.putPort(boundaryPort);
    
    // update boundary links.
    reflectBoundaryLinkOnUpperPortAdded(networkId, boundaryPort);
  }
  
  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onPortDeletePost(java.lang.String, org.o3project.odenos.component.network.topology.Port, java.util.HashMap)
   */
  @Override
  protected void onPortDeletePost(String networkId, Port port, HashMap<String, Response> respList) {
    log.debug("");
    
    if (!isUpperNetwork(networkId)) {
      return;
    }
    
    if (!linkLayerizerBoundaryTable.isBoudaryPort(
        networkId, port.getNode(), port.getId())) {
      return;
    }

    // update boundary links.
    reflectBoundaryLinkOnUpperPortDelete(networkId, port);
  }
  
  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onLinkAddedPre(java.lang.String, org.o3project.odenos.component.network.topology.Link)
   */
  @Override
  protected boolean onLinkAddedPre(String networkId, Link link) {
    log.debug("");

    if (StringUtils.isBlank(networkId)) {
      String message = "invalid networkID: " + networkId;
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    if (isUpperNetwork(networkId)) {
      NetworkInterface layNwIf = getLayerizedNetworkIf();
      if (layNwIf == null) {
        return false;
      }
      Map<String, Link> layerizedLinks = layNwIf.getLinks();
      if (layerizedLinks == null) {
        return true;
      }

      String convLinkId = null;
      for (String layLinkId : layerizedLinks.keySet()) {
        Link layLink = layerizedLinks.get(layLinkId);
        Link compLink = layLink.clone();
        compLink.setPorts(
            link.getSrcNode(), link.getSrcPort(),
            link.getDstNode(), link.getDstPort());
        // register conversion link.
        if (compLink.equals(layLink)) {
          conversionTable().addEntryLink(
              networkId, link.getId(), // upper's link 
              layNwIf.getNetworkId(), layLinkId); // layerized's link
          convLinkId = layLinkId;
          break;
        }
      }

      if (convLinkId == null) {
        return true; // default on_link_add.
      }
      // sync layerized's flow to upper.
      FlowSet layerizedFlows = layNwIf.getFlowSet();
      for (String layFlowId : layerizedFlows.getFlows().keySet()) {
        BasicFlow layFlow = (BasicFlow) layerizedFlows.getFlow(layFlowId);
        if (layFlow.getPath().contains(convLinkId)) {
          linkLayerizerOnFlow.flowAddedLayerizedNwExistPath(
              layNwIf.getNetworkId(), layFlow);
        }
      }
      return false;
    }

    if (isLayerizedNetwork(networkId)) {
      return upperLinkSync;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onLinkUpdatePre(java.lang.String, org.o3project.odenos.component.network.topology.Link, org.o3project.odenos.component.network.topology.Link, java.util.ArrayList)
   */
  @Override
  protected boolean onLinkUpdatePre(String networkId, Link prev, Link curr,
      ArrayList<String> attributesList) {
    log.debug("");

    if (StringUtils.isBlank(networkId)) {
      String message = "invalid networkID: " + networkId;
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    if (isLayerizedNetwork(networkId)) {
      return upperLinkSync;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onLinkDeletePre(java.lang.String, org.o3project.odenos.component.network.topology.Link)
   */
  @Override
  protected boolean onLinkDeletePre(String networkId, Link link) {
    log.debug("");

    if (StringUtils.isBlank(networkId)) {
      log.warn("invalid networkID: {}", networkId);
      return false;
    }

    if (isLayerizedNetwork(networkId)) {
      return upperLinkSync;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onFlowAddedPre(java.lang.String, org.o3project.odenos.component.network.flow.Flow)
   */
  @Override
  protected boolean onFlowAddedPre(String networkId, Flow flow) {
    log.debug("");

    if ((StringUtils.isBlank(networkId)) || (flow == null)) {
      log.error("invalid parameter");
      throw new IllegalArgumentException("invalid parameter");
    }

    BasicFlow basicFlow = getFlow(networkId, flow);
    if (basicFlow == null) {
      return false;
    }

    if (isLowerNetwork(networkId)) {
      linkLayerizerOnFlow.flowAddedLowerNw(networkId, basicFlow);
      return false;
    }

    if (isLayerizedNetwork(networkId)) {
      if (basicFlow.getPath() != null
          && basicFlow.getPath().size() > 0) {
        linkLayerizerOnFlow.flowAddedLayerizedNwExistPath(
            networkId, basicFlow);
      } else {
        return true;
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onFlowUpdatePre(java.lang.String, org.o3project.odenos.component.network.flow.Flow, org.o3project.odenos.component.network.flow.Flow, java.util.ArrayList)
   */
  @Override
  protected boolean onFlowUpdatePre(String networkId, Flow prev, Flow curr,
      ArrayList<String> attributesList) {
    log.debug("");

    BasicFlow basicFlow = getFlow(networkId, curr);
    if (basicFlow == null) {
      return false;
    }

    if (isLowerNetwork(networkId)) {
      linkLayerizerOnFlow.flowUpdateLowerNw(networkId, basicFlow, attributesList);
      return false;
    }

    if (basicFlow.getPath() != null
        && basicFlow.getPath().size() > 0) {
      if (isUpperNetwork(networkId)) {
        linkLayerizerOnFlow.flowUpdateUpperNwExistPath(
            networkId, basicFlow, attributesList);
        return false;
      }
    }

    return true;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#onFlowDeletePre(java.lang.String, org.o3project.odenos.component.network.flow.Flow)
   */
  @Override
  protected boolean onFlowDeletePre(String networkId, Flow flow) {
    log.debug("");

    if (flow == null || !(flow instanceof BasicFlow)) {
      log.warn("invalid flow.");
      return false;
    }
    BasicFlow basicFlow = (BasicFlow) flow;

    if (isLowerNetwork(networkId)) {
      linkLayerizerOnFlow.flowDeleteLowerNw(networkId, basicFlow);
      return false;
    }

    if (isLayerizedNetwork(networkId)) {
      if (!upperLinkSync) {
        
      }
      return true;
    }
    return false;
  }

  @Override
  protected boolean onInPacketAddedPre(String networkId, InPacketAdded msg) {
    log.debug("");

    NetworkInterface upperNwIf = networkInterfaces().get(networkId);
    NetworkInterface layerizedNwIf = getNetworkIf(LAYERIZED_NETWORK);
    if (upperNwIf == null || layerizedNwIf == null) {
      return false;
    }

    InPacket inPacket = getInPacket(upperNwIf, msg.getId());
    if (inPacket == null) {
      return false;
    }
    String nodeId = inPacket.getNodeId();
    String portId = inPacket.getPortId();

    Map<String, Link> layLinks = layerizedNwIf.getLinks();
    for (String linkId : layLinks.keySet()) {
      Link link = layLinks.get(linkId);
      if (link.getSrcNode().equals(nodeId)
          && link.getSrcPort().equals(portId)) {
        return false;
      }
      if (link.getDstNode().equals(nodeId)
          && link.getDstPort().equals(portId)) {
        return false;
      }
    }

    return true;
  }
  
  /**
   * reflect boundary link on upper's port added event. 
   * @param upperNwId upper's network id.
   * @param upperPort upper's port.
   */
  protected void reflectBoundaryLinkOnUpperPortAdded(String upperNwId, Port upperPort) {
    log.debug("");

    // get lower flows.
    NetworkInterface lowerNwIf = getLowerNetworkIf();
    if (lowerNwIf == null) {
      log.warn("not exist lower network interface.");
      return;
    }
    FlowSet lowerFlows = lowerNwIf.getFlowSet();
    if (lowerFlows == null) {
      log.debug("not exist lower flows.");
      return;
    }
    log.debug("lower flows : {}", lowerFlows);

    // reflect boundary links by lower flow.
    Map<String, LinkLayerizerBoundary> layerizerBoundaries =
        linkLayerizerBoundaryTable.getBoundaries();
    for (LinkLayerizerBoundary boundary : layerizerBoundaries.values()) {
      if (String.valueOf(boundary.getUpperNw()).equals(upperNwId)
          && String.valueOf(boundary.getUpperNwNode()).equals(upperPort.getNode())
          && String.valueOf(boundary.getUpperNwPort()).equals(upperPort.getId())) {
        for (Flow lowFlow : lowerFlows.getFlows().values()) {
          if (!(lowFlow instanceof BasicFlow)) {
            log.error("lower flow is not BasicFlow's instance.");
            continue;
          }
          Port lowPort = lowerNwIf.getPort(
              String.valueOf(boundary.getLowerNwNode()),
              String.valueOf(boundary.getLowerNwPort()));
          if (lowPort != null && isContainPortInFlow(lowPort, (BasicFlow)lowFlow)) {
            if (String.valueOf(lowFlow.getStatus())
                .equals(FlowObject.FlowStatus.ESTABLISHED.toString())) {
              lowFlow.setStatus(FlowObject.FlowStatus.ESTABLISHING.toString());
              // Add & Update Layerized link.
              linkLayerizerOnFlow.flowAddedLowerNw(
                  boundary.getLowerNw(), (BasicFlow)lowFlow);
              lowFlow.setStatus(FlowObject.FlowStatus.ESTABLISHED.toString());
              linkLayerizerOnFlow.flowUpdateLowerNw(
                  boundary.getLowerNw(), (BasicFlow)lowFlow, new ArrayList<String>());
            } else {
              // Add Layerized link.
              linkLayerizerOnFlow.flowAddedLowerNw(
                  boundary.getLowerNw(), (BasicFlow)lowFlow);
            }
          }
        }
      }
    }

  }
  
  
  /**
   * reflect boundary link on upper's port delete event. 
   * @param upperNwId upper's network id.
   * @param upperPort upper's port.
   */
  protected void reflectBoundaryLinkOnUpperPortDelete(String upperNwId, Port upperPort) {
    log.debug("");

     // get lower flows.
    NetworkInterface lowerNwIf = getLowerNetworkIf();
    if (lowerNwIf == null) {
      log.warn("not exist lower network interface.");
      return;
    }
    FlowSet lowerFlows = lowerNwIf.getFlowSet();
    if (lowerFlows == null) {
      log.debug("not exist lower flows.");
      return;
    }

    // reflect boundary links by lower flow.
    Map<String, LinkLayerizerBoundary> layerizerBoundaries =
        linkLayerizerBoundaryTable.getBoundaries();
    for (LinkLayerizerBoundary boundary : layerizerBoundaries.values()) {
      if (String.valueOf(boundary.getUpperNw()).equals(upperNwId)
          && String.valueOf(boundary.getUpperNwNode()).equals(upperPort.getNode())
          && String.valueOf(boundary.getUpperNwPort()).equals(upperPort.getId())) {
        for (Flow lowFlow : lowerFlows.getFlows().values()) {
          if (!(lowFlow instanceof BasicFlow)) {
            log.error("lower flow is not BasicFlow's instance.");
            continue;
          }
          Port lowPort = lowerNwIf.getPort(
              String.valueOf(boundary.getLowerNwNode()),
              String.valueOf(boundary.getLowerNwPort()));
          if (lowPort != null && isContainPortInFlow(lowPort, (BasicFlow)lowFlow)) {
            linkLayerizerOnFlow.flowDeleteLowerNw(
                boundary.getLowerNw(), (BasicFlow)lowFlow);
          }
        }
      }
    }

  }
  
  /**
   * check contain port in flow's match or action.
   * @param port Port
   * @param basicFlow BasicFlow
   * @return true: contain port in flow's match or action. false: not contain.
   */
  protected final boolean isContainPortInFlow(Port port, BasicFlow basicFlow) {
    log.debug("");
          
    String nodeId = String.valueOf(port.getNode());
    String portId = String.valueOf(port.getId());
    // check match's in_node, in_port.
    BasicFlowMatch match = ((BasicFlow)basicFlow).getMatches().get(0);
    if (match == null) {
      log.error("not exist lower flow's match.");
      return false;
    }
    if (nodeId.equals(String.valueOf(match.getInNode()))
        && portId.equals(String.valueOf(match.getInPort()))) {
      return true;
    }
    // check action's edge_node, out_port.
    List<FlowAction> actions = ((BasicFlow)basicFlow).getEdgeActions(nodeId);
    if (actions == null) {
      return false;
    }
    for (FlowAction act : actions) {
      if (act instanceof FlowActionOutput) {
        String outPort = String.valueOf(((FlowActionOutput) act).getOutput());
        if (portId.equals(outPort)) {
          return true;
        }
      }
    }
    return false;
  }
  

  /**
   *
   * @param sync true: link link is reflected in upper network. false: isn't reflected.
   * @return Response of the update.
   */
  protected Response putUpperLinkSync(Boolean sync) {
    /*
     * PUT <base_uri>/settings/upper_link_sync
     */
    log.debug("");

    if (sync == null) {
      log.error("sync is null");
      return new Response(Response.BAD_REQUEST, "sync is null");
    }

    setUpperLinkisync(sync);
    String message = String.format("sync %s", sync);

    return new Response(Response.OK, message);
  }

  /**
   *
   * @param boundary Registered boundary
   * @return Response of the boundary registration.
   */
  protected Response postBoundary(LinkLayerizerBoundary boundary) {
    /*
     * POST <base_uri>/settings/boundaries
     */
    log.debug("");

    try {
      LinkLayerizerBoundary resultBoundary =
          linkLayerizerBoundaryTable.addEntry(boundary);

      setBoundaryPortAttr();

      return new Response(Response.OK, resultBoundary);

    } catch (LinkLayerizerBoundaryException ex) {
      return new Response(Response.CONFLICT, "boundary already exist");
    }
  }

  /**
   *
   * @return Response of the boundary list.
   */
  protected Response getBoundaries() {
    /*
     * GET <base_uri>/settings/boundaries
     */
    log.debug("");

    Map<String, LinkLayerizerBoundary> boundaries =
        linkLayerizerBoundaryTable.getBoundaries();

    return new Response(Response.OK, boundaries);

  }

  /**
   *
   * @param boundaryId ID for boundary.
   * @return Response of the boundary.
   */
  protected Response getBoundary(String boundaryId) {
    /*
     * GET <base_uri>/settings/boundaries/<boundary_id>
     */
    log.debug("");

    if (StringUtils.isBlank(boundaryId)) {
      log.error("Boundary-ID is empty");
      return new Response(Response.BAD_REQUEST, "Boundary-ID is empty");
    }

    /*
     * do not confirm Boundary-ID
     */
    LinkLayerizerBoundary boundary = linkLayerizerBoundaryTable
        .getEntry(boundaryId);

    return new Response(Response.OK, boundary);
  }

  /**
   *
   * @param boundaryId ID for boundary.
   * @param boundary Replacement boundary.
   * @return Response of the boundary replacement.
   */
  protected Response putBoundary(String boundaryId,
      LinkLayerizerBoundary boundary) {
    /*
     * PUT <base_uri>/settings/boundaries/<boundary_id>
     */
    if (log.isDebugEnabled()) {
      log.debug("boundaryId: {}", boundaryId);
    }

    try {
      LinkLayerizerBoundary resultBoundary =
          linkLayerizerBoundaryTable
              .updateEntry(boundaryId, boundary);

      setBoundaryPortAttr();

      return new Response(Response.OK, resultBoundary);

    } catch (LinkLayerizerBoundaryException ex) {
      return new Response(Response.CONFLICT, "boundary already exist");
    }
  }

  /**
   *
   * @param boundaryId Deleted boundary ID.
   * @return Response of the boundary delete.
   */
  protected Response deleteBoundary(String boundaryId) {
    /*
     * DELETE <base_uri>/settings/boundaries/<boundary_id>
     */
    log.debug("");

    if (StringUtils.isBlank(boundaryId)) {
      log.error("Boundary-ID is empty");
      return new Response(Response.BAD_REQUEST, "Boundary-ID is empty");
    }

    /*
     * do not confirm Boundary-ID
     */
    unsetBoundaryPortAttr(boundaryId);
    linkLayerizerBoundaryTable.deleteEntry(boundaryId);

    return new Response(Response.OK, null);
  }

  /**
   *
   * @return Response of the flows in a lower network.
   */
  protected Response getLowerFlows() {
    /*
     * GET <base_uri>/lower_flows
     */
    log.debug("");

    return new Response(Response.OK,
        linkLayerizerOnFlow.getLowerFlows());
  }

  /**
   *
   * @param linkId ID for link.
   * @return Response of the flow in a lower network.
   */
  protected Response getLowerFlows(String linkId) {
    /*
     * GET <base_uri>/lower_flows/<link_id>
     */
    log.debug("");

    if (StringUtils.isBlank(linkId)) {
      log.error("Link-ID is empty");
      return new Response(Response.BAD_REQUEST, "Link-ID is empty");
    }

    List<String> flowIds =
        linkLayerizerOnFlow.getLowerFlows().get(linkId);

    return new Response(Response.OK, flowIds);
  }

  /**
   *
   * @return Response of the links in a layerized network.
   */
  protected Response getLayerizedlinks() {
    /*
     * GET <base_uri>/layerized_links
     */
    log.debug("");

    return new Response(Response.OK,
        linkLayerizerOnFlow.getLayerizedLinks());
  }

  /**
   *
   * @param flowId ID for flow.
   * @return Response of the link in a layerized network.
   */
  protected Response getLayerizedLink(String flowId) {
    /*
     * GET <base_uri>/layerized_links/<flow_id>
     */
    log.debug("");

    if (StringUtils.isBlank(flowId)) {
      log.error("Flow-ID is empty");
      return new Response(Response.BAD_REQUEST, "Flow-ID is empty");
    }

    String linkId =
        linkLayerizerOnFlow.getLayerizedLinks().get(flowId);

    return new Response(Response.OK, linkId);
  }

  //////////////////////////////////////////////////
  // common method
  //////////////////////////////////////////////////

  /**
   * Returns flow.
   * @param networkId ID for network.
   * @param flow Flow.
   * @return got the flow
   */
  protected BasicFlow getFlow(String networkId, Flow flow) {
    log.debug("");

    if (StringUtils.isBlank(networkId)) {
      log.error("Network ID is empty");
      throw new IllegalArgumentException("Network ID is empty");
    }

    if (flow == null) {
      log.error("flow is null");
      throw new IllegalArgumentException("flow is null");
    }

    NetworkInterface nwif = networkInterfaces().get(networkId);
    String flowId = flow.getFlowId();

    return getFlow(nwif, flowId);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.component.Logic#getFlow(org.o3project.odenos.component.NetworkInterface, java.lang.String)
   */
  @Override
  protected BasicFlow getFlow(final NetworkInterface nwIf, final String flowId) {
    log.debug("");

    if ((nwIf == null) || StringUtils.isBlank(flowId)) {
      log.error("parameter is null");
      throw new IllegalArgumentException("parameter is null");
    }

    Flow flow = nwIf.getFlow(flowId);
    if (flow == null) {
      return null;
    }

    if (!(flow instanceof BasicFlow)) {
      throw new IllegalStateException("flow is not BasicFlow");
    }

    BasicFlow basicFlow = (BasicFlow) flow;

    if (basicFlow == null
        || CollectionUtils.isEmpty(basicFlow.getMatches())) {
      throw new IllegalStateException("flow is invalid BasicFlow");
    }

    return basicFlow;
  }

  protected void setBoundaryPortAttr() {
    log.debug("");

    Map<String, LinkLayerizerBoundary> boundaryMap =
        linkLayerizerBoundaryTable.getBoundaries();
    Collection<LinkLayerizerBoundary> boundaries = boundaryMap.values();

    for (LinkLayerizerBoundary boundary : boundaries) {

      try {
        /**
         * for lower nw.
         */
        String lowerNetworkId = boundary.getLowerNw();
        NetworkInterface lowerNetif = networkInterfaces()
            .get(lowerNetworkId);

        String lowerNodeId = boundary.getLowerNwNode();
        String lowerPortId = boundary.getLowerNwPort();
        Port lowerPort = lowerNetif.getPort(lowerNodeId, lowerPortId);
        if (lowerPort != null) {
          lowerPort.putAttribute(AttrElements.IS_BOUNDARY, "true");
          lowerNetif.putPort(lowerPort);
        }

        /**
         * for upper nw.
         */
        String upperNetworkId = boundary.getUpperNw();
        NetworkInterface upperNetif = networkInterfaces()
            .get(upperNetworkId);

        String upperNodeId = boundary.getUpperNwNode();
        String upperPortId = boundary.getUpperNwPort();
        Port upperPort = upperNetif.getPort(upperNodeId, upperPortId);
        if (upperPort != null) {
          upperPort.putAttribute(AttrElements.IS_BOUNDARY, "true");
          upperNetif.putPort(upperPort);
        }

      } catch (Exception ex) {
        log.error("Receive Exception.", ex);
      }
    }
  }

  /**
   *
   * @param boundaryId ID for boundary.
   */
  protected void unsetBoundaryPortAttr(String boundaryId) {
    log.debug("");

    if (boundaryId == null) {
      return;
    }

    Map<String, LinkLayerizerBoundary> boundaryMap =
        linkLayerizerBoundaryTable.getBoundaries();
    LinkLayerizerBoundary boundary = boundaryMap.get(boundaryId);
    if (boundary == null) {
      return;
    }

    // for lower nw
    String lowerNetworkId = boundary.getLowerNw();
    NetworkInterface lowerNetif = networkInterfaces()
        .get(lowerNetworkId);

    String lowerNodeId = boundary.getLowerNwNode();
    String lowerPortId = boundary.getLowerNwPort();
    Port lowerPort = lowerNetif.getPort(lowerNodeId, lowerPortId);
    lowerPort.deleteAttribute(AttrElements.IS_BOUNDARY);
    lowerNetif.putPort(lowerPort);

    // for upper nw
    String upperNetworkId = boundary.getUpperNw();
    NetworkInterface upperNetif = networkInterfaces()
        .get(upperNetworkId);

    String upperNodeId = boundary.getUpperNwNode();
    String upperPortId = boundary.getUpperNwPort();
    Port upperPort = upperNetif.getPort(upperNodeId, upperPortId);
    upperPort.deleteAttribute(AttrElements.IS_BOUNDARY);
    upperNetif.putPort(upperPort);
  }

  /**
   *
   * @return List of ID for lower network.
   */
  protected final List<String> getLowerNetworkIds() {
    return getNetworkIds(LOWER_NETWORK);
  }

  /**
   *
   * @return List of ID for upper network.
   */
  protected final List<String> getUpperNetworkIds() {
    return getNetworkIds(UPPER_NETWORK);
  }

  /**
   *
   * @return List of ID for layerized network.
   */
  protected final List<String> getLayerizedNetworkIds() {
    return getNetworkIds(LAYERIZED_NETWORK);
  }

  /**
   *
   * @param type Type of the network.
   * @return List of ID for the network.
   */
  protected final List<String> getNetworkIds(String type) {
    log.debug("");

    ConversionTable convTable = conversionTable();

    ArrayList<String> ids =
        convTable.getConnectionList(type);

    return ids;
  }

  /**
   *
   * @return NetworkInterface of upper network.
   */
  protected final NetworkInterface getUpperNetworkIf() {
    return getNetworkIf(UPPER_NETWORK);
  }

  /**
   *
   * @return NetworkInterface of layerized network.
   */
  protected final NetworkInterface getLayerizedNetworkIf() {
    return getNetworkIf(LAYERIZED_NETWORK);
  }
  
  /**
   *
   * @return NetworkInterface of lower network.
   */
  protected final NetworkInterface getLowerNetworkIf() {
    return getNetworkIf(LOWER_NETWORK);
  }

  /**
   *
   * @param type Type of the network.
   * @return NetworkInterface for the network.
   */
  protected final NetworkInterface getNetworkIf(String type) {
    log.debug("");

    ConversionTable convTable = conversionTable();

    ArrayList<String> ids =
        convTable.getConnectionList(type);

    if (CollectionUtils.isEmpty(ids)) {
      return null;
    }

    String id = ids.get(0);
    NetworkInterface netIf = networkInterfaces().get(id);

    return netIf;
  }

  /**
   *
   * @param ids List of ID for the network.
   * @return NetworkInterface for the network.
   */
  protected final NetworkInterface getNetworkIf(List<String> ids) {
    log.debug("");

    if (CollectionUtils.isEmpty(ids)) {
      log.error("ids is empty");
      throw new IllegalArgumentException("ids is empty");
    }

    String id = ids.get(0);
    NetworkInterface netIf = networkInterfaces().get(id);

    return netIf;
  }

  /**
   *
   * @param type Type of the network.
   * @return true: connected to the network. false: not connected.
   */
  protected final boolean isConnectionType(String type) {
    log.debug("");

    ConversionTable convTable = conversionTable();
    boolean result = convTable.isConnectionType(type);

    return result;
  }

  /**
   *
   * @param networkId ID for layerized network.
   * @return true: connected to layerized network. false: not connected.
   */
  protected final boolean isLayerizedNetwork(String networkId) {
    log.debug("");

    String connType = getConnectionType(networkId);
    if (LAYERIZED_NETWORK.equals(connType)) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param networkId ID for lower network.
   * @return true: connected to lower network. false: not connected.
   */
  protected final boolean isLowerNetwork(String networkId) {
    log.debug("");

    String connType = getConnectionType(networkId);
    if (LOWER_NETWORK.equals(connType)) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param networkId ID for upper network.
   * @return true: connected to upper network. false: not connected.
   */
  protected final boolean isUpperNetwork(String networkId) {
    log.debug("");

    String connType = getConnectionType(networkId);
    if (UPPER_NETWORK.equals(connType)) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param networkId ID for the network.
   * @return Type of the network.
   */
  protected final String getConnectionType(String networkId) {
    log.debug("");

    ConversionTable convTable = conversionTable();
    String connType = convTable.getConnectionType(networkId);

    return connType;
  }

}
