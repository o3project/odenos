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

package org.o3project.odenos.component.slicer;

import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.core.component.Logic;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Slicer provides the ability to create multiple Slice Network by duplicating the Original Network.
 *
 */
public class Slicer extends Logic {

  /** logger. */
  private static final Logger log = LoggerFactory.getLogger(Slicer.class);

  protected static final String ORIGINAL = "original";
  protected static final String SLIVER = "sliver";

  protected RequestParser<IActionCallback> parser;

  protected final SliceConditionTable getConditionTable() {
    return this.conditionTable;
  }

  protected SliceConditionTable conditionTable;

  // {Network1:connection1, Network2:connection2, ...}
  protected Map<String, String> nwcToConnection = new HashMap<String, String>();
  // {connection1:Network1, connection2:Network2, ...}
  protected Map<String, String> connectionToNwc = new HashMap<String, String>();

  /**
   * Constructor.
   * @param objectId object id
   * @param baseUri base URI.
   * @param dispatcher Message dispatcher instance.
   * @throws Exception if parameter is wrong.
   */
  public Slicer(
      final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    this.conditionTable = new SliceConditionTable();
  }

  /**
   * Constructor.
   * @param objectId object id
   * @param dispatcher Message dispatcher instance.
   * @throws Exception if parameter is wrong.
   */
  public Slicer(
      final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    this.conditionTable = new SliceConditionTable();
  }

  /**
   * Get Super Type of Component.
   *
   * @return Super Type of Component
   */
  @Override
  protected String getSuperType() {
    return Slicer.class.getSimpleName();
  }

  /**
   * Description of Component.
   */
  private static final String DESCRIPTION = "Slicer Component";

  /**
   * Get Description of Component.
   *
   * @return Description of Component
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
    return String.format("%s:*,%s:1", SLIVER, ORIGINAL);
  }

  // //////////////////////////////////////////////////
  //
  // NetworkComponentConnection
  //
  // //////////////////////////////////////////////////

  @Override
  protected boolean onConnectionChangedAddedPre(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection compConn = msg.curr();
    if (compConn == null) {
      if (log.isDebugEnabled()) {
        log.debug("curr is null");
      }
      return false;
    }

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(compConn
        .getObjectType())) {
      return false;
    }
    String logicId = compConn.getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }

    String status = compConn.getObjectState();
    String connectionType = compConn.getConnectionType();
    if (connectionType != null
        && connectionType.equals(ORIGINAL)) {
      if (conversionTable().isConnectionType(ORIGINAL)) {
        status = ComponentConnection.State.ERROR;
      }
    } else if (connectionType != null
        && connectionType.equals(SLIVER)) {
      /* always true */
    } else { /* unknown connectionType */
      return false;
    }

    if (ComponentConnection.State.ERROR.equals(status)) {
      // Changed ConectionProperty's status.
      compConn.setConnectionState(status);
      systemMngInterface().putConnection(compConn);
      return false;
    }
    return true;
  }

  @Override
  protected boolean onConnectionChangedUpdatePre(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      if (log.isDebugEnabled()) {
        log.debug("curr is null");
      }
      return false;
    }

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(curr
        .getObjectType())) {
      return false;
    }
    String logicId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }
    return true;
  }

  @Override
  protected boolean onConnectionChangedDeletePre(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      if (log.isDebugEnabled()) {
        log.debug("curr is null");
      }
      return false;
    }

    if (!ComponentConnectionLogicAndNetwork.TYPE.equals(curr
        .getObjectType())) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }
    return true;
  }

  @Override
  protected void onConnectionChangedAdded(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr();
    if (curr == null) {
      if (log.isDebugEnabled()) {
        log.debug("curr is null");
      }
      return;
    }
    String type = curr.getConnectionType();
    String networkId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    conversionTable().addEntryConnectionType(networkId, type);
    curr.setConnectionState(ComponentConnection.State.RUNNING);

    String connectionId = curr.getObjectId();

    nwcToConnection.put(networkId, connectionId);
    connectionToNwc.put(connectionId, networkId);

    if (type.equals(ORIGINAL)) {
      subscribeOriginal(networkId);
      ArrayList<String> connectios =
          conversionTable().getConnectionList(SLIVER);
      if (connectios.size() == 0) {
        // Changed ConectionProperty's status.
        systemMngInterface().putConnection(curr);
        return;
      }
      for (String pairId : connectios) {
        // Update conversionTable.
        conversionTable().addEntryNetwork(networkId, pairId);
        // Reflect to sliver from original_network.
        reflecteToSliverFromOriginal(
            networkInterfaces().get(networkId),
            networkInterfaces().get(pairId));
      }
    }
    if (type.equals(SLIVER)) {
      subscribeSliver(networkId);
      ArrayList<String> original =
          conversionTable().getConnectionList(ORIGINAL);
      if (original.size() == 0) {
        // Changed ConectionProperty's status.
        systemMngInterface().putConnection(curr);
        return;
      }
      // Update conversionTable.
      conversionTable().addEntryNetwork(original.get(0), networkId);
      // Reflect to sliver from original_network.
      reflecteToSliverFromOriginal(
          networkInterfaces().get(original.get(0)),
          networkInterfaces().get(networkId));
    }
    // Changed ConectionProperty's status.
    systemMngInterface().putConnection(curr);
  }

  @Override
  protected void onConnectionChangedUpdate(
      final ComponentConnectionChanged msg) {
    log.debug("");
    // Do Nothing.
  }

  @Override
  protected void onConnectionChangedDelete(
      final ComponentConnectionChanged msg) {
    log.debug("");

    ComponentConnection curr = msg.curr(); /* non null */
    String networkId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    conversionTable().delEntryConnectionType(networkId);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    String pairConnectionId = nwcToConnection.get(networkId);
    connectionToNwc.remove(pairConnectionId);
    nwcToConnection.remove(networkId);

    String type = curr.getConnectionType();
    if (type.equals(ORIGINAL)) {
      unsubscribeOriginal(networkId);
      NetworkInterface orgNwIf = networkInterfaces().get(networkId);

      // finalizing original 
      doOnConnectionDeleteOriginal(orgNwIf);

    } else if (type.equals(SLIVER)) {
      unsubscribeSliver(networkId);
      NetworkInterface slivNwIf = networkInterfaces().get(networkId);

      // finalizing sliver
      doOnConnectionDeleteSliver(slivNwIf);

    }

    // reset conversionTable.
    conversionTable().delEntryConnectionType(networkId);
    conversionTable().delEntryNetwork(networkId);

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  protected void
      subscribeOriginal(final String nwcId) {
    log.debug("");

    addEntryEventSubscription(NODE_CHANGED, nwcId);
    addEntryEventSubscription(PORT_CHANGED, nwcId);
    addEntryEventSubscription(LINK_CHANGED, nwcId);
    addEntryEventSubscription(IN_PACKET_ADDED, nwcId);

    String attrBase = AttrElements.ATTRIBUTES + "::%s";
    ArrayList<String> nodeAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.ADMIN_STATUS)));
    updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

    ArrayList<String> portAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.ADMIN_STATUS),
        String.format(attrBase, AttrElements.UNRESERVED_BANDWIDTH),
        String.format(attrBase, AttrElements.IS_BOUNDARY)));
    updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

    ArrayList<String> linkAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.COST),
        String.format(attrBase, AttrElements.REQ_LATENCY),
        String.format(attrBase, AttrElements.UNRESERVED_BANDWIDTH)));
    updateEntryEventSubscription(LINK_CHANGED, nwcId, linkAttributes);

    ArrayList<String> flowAttributes = new ArrayList<String>(Arrays.asList(
        NetworkElements.STATUS,
        String.format(attrBase, AttrElements.BANDWIDTH),
        String.format(attrBase, AttrElements.LATENCY)));
    updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void
      unsubscribeOriginal(final String nwcId) {
    log.debug("");
    removeEntryEventSubscription(NODE_CHANGED, nwcId);
    removeEntryEventSubscription(PORT_CHANGED, nwcId);
    removeEntryEventSubscription(LINK_CHANGED, nwcId);
    removeEntryEventSubscription(IN_PACKET_ADDED, nwcId);
    removeEntryEventSubscription(FLOW_CHANGED, nwcId);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void
      subscribeSliver(final String nwcId) {
    log.debug("");

    addEntryEventSubscription(FLOW_CHANGED, nwcId);
    addEntryEventSubscription(OUT_PACKET_ADDED, nwcId);

    String attrBase = AttrElements.ATTRIBUTES + "::%s";
    ArrayList<String> nodeAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.OPER_STATUS),
        String.format(attrBase, AttrElements.PHYSICAL_ID),
        String.format(attrBase, AttrElements.VENDOR)));
    updateEntryEventSubscription(NODE_CHANGED, nwcId, nodeAttributes);

    ArrayList<String> portAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.OPER_STATUS),
        String.format(attrBase, AttrElements.MAX_BANDWIDTH),
        String.format(attrBase, AttrElements.PHYSICAL_ID),
        String.format(attrBase, AttrElements.VENDOR)));
    updateEntryEventSubscription(PORT_CHANGED, nwcId, portAttributes);

    ArrayList<String> linkAttributes = new ArrayList<String>(Arrays.asList(
        String.format(attrBase, AttrElements.OPER_STATUS),
        String.format(attrBase, AttrElements.LATENCY),
        String.format(attrBase, AttrElements.MAX_BANDWIDTH),
        String.format(attrBase, AttrElements.REQ_BANDWIDTH)));
    updateEntryEventSubscription(LINK_CHANGED, nwcId, linkAttributes);

    ArrayList<String> flowAttributes = new ArrayList<String>(Arrays.asList(
        NetworkElements.ENABLED,
        NetworkElements.PRIORITY,
        String.format(attrBase, AttrElements.REQ_BANDWIDTH),
        String.format(attrBase, AttrElements.REQ_LATENCY)));
    updateEntryEventSubscription(FLOW_CHANGED, nwcId, flowAttributes);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void
      unsubscribeSliver(final String nwcId) {
    log.debug("");

    removeEntryEventSubscription(FLOW_CHANGED, nwcId);
    removeEntryEventSubscription(OUT_PACKET_ADDED, nwcId);
    removeEntryEventSubscription(NODE_CHANGED, nwcId);
    removeEntryEventSubscription(PORT_CHANGED, nwcId);
    removeEntryEventSubscription(LINK_CHANGED, nwcId);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  // //////////////////////////////////////////////////
  //
  // Request Event
  //
  // //////////////////////////////////////////////////

  private RequestParser<IActionCallback> createParser() {
    log.debug("");

    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.PUT,
            "settings/slice_condition_table/<priority>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                List<BasicSliceCondition> basicList = parsed
                    .getRequest().getBodyAsList(
                        BasicSliceCondition.class);
                return putSliceConditionTablePriority(
                    parsed.getParam("priority"),
                    basicList);
              }
            });
        addRule(Method.POST,
            "settings/slice_condition_table/<priority>/conditions/",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return postSliceConditionTableConditionId(
                    parsed.getParam("priority"),
                    parsed.getRequest().getBody(
                        BasicSliceCondition.class));
              }
            });
        addRule(Method.PUT,
            "settings/slice_condition_table/<priority>/conditions/<condition_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return putSliceConditionTableConditionId(
                    parsed.getParam("priority"),
                    parsed.getParam("condition_id"),
                    parsed.getRequest().getBody(
                        BasicSliceCondition.class));
              }
            });
        addRule(Method.DELETE,
            "settings/slice_condition_table/<priority>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return deleteSliceConditionTable(
                parsed.getParam("priority"));
              }
            });
        addRule(Method.DELETE,
            "settings/slice_condition_table/conditions/<condition_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return deleteSliceConditionTableConditionId(
                parsed.getParam("condition_id"));
              }
            });
        addRule(Method.GET,
            "settings/slice_condition_table",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return new Response(Response.OK,
                    getConditionTable().getPriorityTables());
              }
            });
        addRule(Method.GET,
            "settings/slice_condition_table/<priority>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getSliceConditionTablePriority(
                parsed.getParam("priority"));
              }
            });
        addRule(Method.GET,
            "settings/slice_condition_table/conditions/<condition_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getSliceConditionTableConditionId(
                parsed.getParam("condition_id"));
              }
            });
        addRule(Method.GET,
            "settings/slice_condition_table/connections/<connection_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getSliceConditionTableConnectionId(
                parsed.getParam("connection_id"));
              }
            });
        addRule(Method.GET,
            "original_network_flow",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getFlowMapping(ORIGINAL);
              }
            });
        addRule(Method.GET,
            "sliver_network_flow",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getFlowMapping(SLIVER);
              }
            });
      }
    };
  }

  @Override
  protected Response onRequest(final Request request) {
    log.debug("");

    log.debug("received {}", request.path);
    RequestParser<IActionCallback>.ParsedRequest parsed = parser
        .parse(request);
    if (parsed == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }

    IActionCallback callback = parsed.getResult();
    if (callback == null) {
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
    try {
      // Get response.
      return callback.process(parsed);
    } catch (Exception e) {
      log.error("Error unknown request");
      return new Response(Response.BAD_REQUEST, "Error unknown request ");
    }
  }

  /**
   * Update a condition's priority.
   * @param priority string of priority.
   * @param body conditions.
   * @return response to the SliceCondition
   */
  protected Response putSliceConditionTablePriority(String priority,
      List<? extends BasicSliceCondition> body) {
    log.debug("");

    if (priority == null) {
      Response response = new Response(Response.BAD_REQUEST,
          "priority is null");
      return response;
    }
    if (body == null) {
      Response response = new Response(Response.BAD_REQUEST,
          "body is null");
      return response;
    }

    SliceConditionTable conditionTable = getConditionTable();
    for (SliceCondition condition : body) {
      conditionTable.addEntryToSliceCondition(priority, condition);
    }

    Response response = new Response(Response.OK, body);

    return response;
  }

  protected Response postSliceConditionTableConditionId(
      final String priority,
      final BasicSliceCondition body) {

    if (log.isDebugEnabled()) {
      log.debug("priority {}", priority);
    }

    if (StringUtils.isEmpty(priority) || (body == null)) {
      Response response = new Response(Response.BAD_REQUEST,
          "parameter is null");
      return response;
    }

    if (!(body instanceof BasicSliceCondition)) {
      return new Response(Response.BAD_REQUEST,
          "failer:: SliceCondition type no match [type is 'BasicSliceCondition' Only]");
    }

    getConditionTable().addEntryToSliceCondition(priority, body);

    return new Response(Response.OK, body);
  }

  protected Response
      putSliceConditionTableConditionId(
          final String priority,
          final String conditionId,
          final BasicSliceCondition body) {
    log.debug("");

    if ((priority == null) || (body == null)) {
      Response response = new Response(Response.BAD_REQUEST,
          "parameter is null");
      return response;
    }

    if (!(body instanceof BasicSliceCondition)) {
      return new Response(Response.BAD_REQUEST,
          "failer:: SliceCondition type no match [type is 'BasicSliceCondition' Only]");
    }
    getConditionTable().addEntryToSliceCondition(priority, conditionId, body);

    return new Response(Response.OK, body);
  }

  protected Response
      deleteSliceConditionTable(final String priority) {
    log.debug("");

    if (priority == null) {
      Response response = new Response(Response.BAD_REQUEST,
          "priority is null");
      return response;
    }

    SliceConditionTable conditionTable = getConditionTable();
    conditionTable.deleteSliceConditionTable(priority);

    return new Response(Response.OK, null);
  }

  protected Response
      deleteSliceConditionTableConditionId(
          final String conditionId) {
    log.debug("");

    conditionTable.deleteSliceCondition(conditionId);
    return new Response(Response.OK, null);
  }

  protected Response
      getSliceConditionTablePriority(final String priority) {
    log.debug("");

    return new Response(Response.OK,
        conditionTable.getConditionIdList(priority));
  }

  protected Response
      getSliceConditionTableConditionId(final String conditionId) {
    log.debug("");

    SliceCondition sliceCondition = conditionTable
        .getSliceConditionObject(conditionId);
    if (sliceCondition == null) {
      return new Response(Response.NOT_FOUND, null);
    }
    return new Response(Response.OK, sliceCondition);
  }

  protected Response
      getSliceConditionTableConnectionId(final String connectionId) {
    log.debug("");

    List<String> list = getConditionTable().getSliceConditionIds(
        connectionId);
    return new Response(Response.OK, list);
  }

  // //////////////////////////////////////////////////
  //
  // Event method override
  //
  // //////////////////////////////////////////////////

  @Override
  protected boolean onFlowAddedPre(
      final String networkId,
      final Flow flow) {
    log.debug("");

    if (flow == null) {
      return false;
    }

    String connType = conversionTable().getConnectionType(networkId);
    if (ORIGINAL.equals(connType)) {
      // error
      return false;
    }

    // check flow
    if (!(flow instanceof BasicFlow)) {
      // flow status failure
      return false;
    }

    BasicFlow basicFlow = (BasicFlow) flow;

    List<BasicFlowMatch> messageMatchs = basicFlow.getMatches();
    String connectionId = nwcToConnection.get(networkId);
    if (log.isDebugEnabled()) {
      log.debug(
          String.format("###FlowConnectionId(%s)", connectionId));
    }

    for (BasicFlowMatch match : messageMatchs) {
      if (isMatchConditonTable(connectionId, match)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onInPacketAdded(
      final String networkId,
      final InPacketAdded msg) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (!connType.equals(ORIGINAL)) {
      return;
    }

    NetworkInterface networkIf = networkInterfaces().get(networkId);
    InPacket body = delInPacket(networkIf, msg.getId());
    if (body == null) {
      log.error("invalid DELETE Packet.");
      return;
    }

    BasicFlowMatch match = body.getHeader();
    String connectionId = matchPriorityTable(match);
    if (connectionId == null) {
      // packet drop
      log.info("dorp Packet.");
      return;
    }

    String sliverNetwork = connectionToNwc.get(connectionId);
    NetworkInterface sliverNetworkIf =
        networkInterfaces().get(sliverNetwork);
    sliverNetworkIf.postInPacket(body);
  }

  @Override
  protected void onOutPacketAdded(
      final String networkId,
      final OutPacketAdded msg) {
    log.debug("");

    String connType = conversionTable().getConnectionType(networkId);
    if (connType.equals(ORIGINAL)) {
      return;
    }

    NetworkInterface networkIf = networkInterfaces().get(networkId);
    OutPacket body = delOutPacket(networkIf, msg.getId());
    if (body == null) {
      log.error("invalid DELETE Packet.");
      return;
    }

    BasicFlowMatch match = body.getHeader();
    String connectionId = matchPriorityTable(match);
    if (connectionId == null) {
      // packet drop
      log.info("dorp Packet.");
      return;
    }

    ArrayList<String> orgNwc = conversionTable()
        .getConnectionList(ORIGINAL);
    NetworkInterface orgNetworkIf = networkInterfaces().get(orgNwc.get(0));
    orgNetworkIf.postOutPacket(body);
  }

  protected void reflecteToSliverFromOriginal(
      final NetworkInterface orgNetworkIf,
      final NetworkInterface slivNetworkIf) {
    log.debug("");

    try {
      Topology orgTopology = orgNetworkIf.getTopology();
      if (orgTopology == null) {
        return;
      }
      Map<String, Node> orgNodes = orgTopology.nodes;
      Map<String, Link> orgLinks = orgTopology.links;

      for (String nodeId : orgNodes.keySet()) {
        // Add Node & Port.
        this.onNodeAdded(
            orgNetworkIf.getNetworkId(), orgNodes.get(nodeId));
      }
      for (String linkId : orgLinks.keySet()) {
        // add Link.
        this.onLinkAdded(
            orgNetworkIf.getNetworkId(), orgLinks.get(linkId));
      }
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void doOnConnectionDeleteOriginal(
      final NetworkInterface orgNwIf) {
    log.debug("");

    if (orgNwIf == null) {
      return;
    }
    List<String> slivNwIds = conversionTable().getNetwork(orgNwIf.getNetworkId());
    for (String slivNwId : slivNwIds) {
      NetworkInterface slivNwIf = networkInterfaces().get(slivNwId);
      if (slivNwIf == null) {
        continue;
      }
      // delete sliver's topology.
      slivNwIf.deleteTopology();
      // delete sliver's in_pakcet.
      slivNwIf.delInPackets();
      // update sliver's flow status
      slivNwIf.putStatusFaildAllFlow();
    }

    // delete original's flow.
    orgNwIf.deleteAllFlow();
    // delete original's out_packet.
    orgNwIf.delOutPackets();

    // reset conversionTable.
    conversionTable().getNetwork().clear();
    conversionTable().getFlow().clear();
    conversionTable().getLink().clear();
    conversionTable().getPort().clear();
    conversionTable().getNode().clear();
  }

  protected void doOnConnectionDeleteSliver(
      final NetworkInterface slivNwIf) {
    log.debug("");

    if (slivNwIf == null) {
      return;
    }
    FlowSet flowSet = new FlowSet();
    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();
    FlowSet getflowSet = slivNwIf.getFlowSet();
    if (getflowSet != null) {
      flowSet = getflowSet;
    }
    Map<String, Node> getnodos = slivNwIf.getNodes();
    if (getnodos != null) {
      nodes = getnodos;
    }
    Map<String, Link> getlinks = slivNwIf.getLinks();
    if (getlinks != null) {
      links = getlinks;
    }

    // delete sliver's topology.
    slivNwIf.deleteTopology();
    // delete sliver's in_pakcet.
    slivNwIf.delInPackets();
    // update sliver's flow status
    slivNwIf.putStatusFaildAllFlow();

    /**
     * update conversion.
     */
    String slivNwId = slivNwIf.getNetworkId();
    conversionTable().delEntryNetwork(slivNwId);
    // conversion links.
    for (String linkid : links.keySet()) {
      conversionTable().delEntryLink(slivNwId, linkid);
    }
    // conversion nodes and port.
    for (String nodeid : nodes.keySet()) {
      Map<String, Port> ports = nodes.get(nodeid).getPortMap();
      if (ports != null && ports.size() > 0) {
        for (String portid : ports.keySet()) {
          conversionTable().delEntryPort(slivNwId, nodeid, portid);
        }
      }
      conversionTable().delEntryNode(slivNwId, nodeid);
    }

    /**
     * delete original's flow.
     */
    String orgNwId = getConvNetworkId(slivNwIf.getNetworkId());
    if (orgNwId == null) {
      return;
    }
    NetworkInterface orgNwIf = networkInterfaces().get(orgNwId);
    if (orgNwIf == null) {
      return;
    }
    if (orgNwIf != null) {
      for (String flowid : flowSet.getFlows().keySet()) {
        orgNwIf.delFlow(flowid);
        // update conversionTable
        conversionTable().delEntryFlow(slivNwId, flowid);
      }
    }

  }

  protected Response getFlowMapping(final String connectionType) {
    log.debug("");

    HashMap<String, String> respBody = new HashMap<String, String>();

    ArrayList<String> original = conversionTable().getConnectionList(
        "original");
    Pattern pattern = Pattern.compile("^" + original.get(0) + "::.*");

    HashMap<String, ArrayList<String>> connectionsFlow = conversionTable()
        .getFlow();
    for (String flowId : connectionsFlow.keySet()) {
      log.debug(("####" + flowId + " = " + connectionsFlow.get(flowId)));

      Matcher match = pattern.matcher(flowId);
      if (match.find()) {
        if (connectionType != null
            && connectionType.equals(ORIGINAL)) {
          ArrayList<String> value = connectionsFlow.get(flowId);
          String[] orgId = flowId.split("::");
          respBody.put(orgId[1], value.get(0));
        }
      } else {
        if (connectionType != null
            && connectionType.equals(SLIVER)) {
          ArrayList<String> value = connectionsFlow.get(flowId);
          String[] orgId = value.get(0).split("::");
          respBody.put(flowId, orgId[1]);
        }
      }
    }
    return new Response(Response.OK, respBody);
  }

  protected boolean isMatchConditonTable(
      final String connectionId,
      final BasicFlowMatch flowmatch) {
    log.debug("");

    for (String conditionId : conditionTable
        .getSliceConditionIds(connectionId)) {
      if (isMatchSliceConditon(conditionId, flowmatch)) {
        return true;
      }
    }
    return false;
  }

  protected String matchPriorityTable(
      final BasicFlowMatch match) {
    log.debug("");

    for (String priority : conditionTable.getPriorityTables()
        .descendingKeySet()) {
      for (String conditionId : conditionTable
          .getConditionIdList(priority)) {
        if (isMatchSliceConditon(conditionId, match)) {
          return conditionTable.getConnectionId(conditionId);
        }
      }
    }
    return null;
  }

  protected boolean isMatchSliceConditon(
      final String conditionId,
      final BasicFlowMatch flowmatch) {
    log.debug("");

    BasicSliceCondition cond = (BasicSliceCondition)
        conditionTable.getSliceConditionObject(conditionId);

    if (!cond.getInNode().equals(flowmatch.getInNode())) {
      return false;
    }
    if (cond.getInPort() == null) {
      return true; // Any
    }
    if (!cond.getInPort().equals(flowmatch.getInPort())) {
      return false;
    }
    return true;
  }

  protected final String getConvNetworkId(final String networkId) {
    log.debug("");

    if (networkId == null) {
      return null;
    }
    ArrayList<String> convNetowrkId =
        conversionTable().getNetwork(networkId);
    if (convNetowrkId.size() == 0) {
      return null;
    }
    return convNetowrkId.get(0);
  }

}
