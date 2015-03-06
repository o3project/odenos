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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowChanged;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.component.network.packet.PacketObject;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.LinkChanged;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.NodeChanged;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.PortChanged;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logic class.
 *
 */
public abstract class Logic extends Component {
  private static final Logger log = LoggerFactory.getLogger(Logic.class);

  public static final String CONN_ADD = "add";
  public static final String CONN_UPDATE = "update";
  public static final String CONN_DELETE = "delete";
  public static final String NODE_CHANGED = "NODE_CHANGED";
  public static final String PORT_CHANGED = "PORT_CHANGED";
  public static final String LINK_CHANGED = "LINK_CHANGED";
  public static final String FLOW_CHANGED = "FLOW_CHANGED";
  public static final String IN_PACKET_ADDED = "IN_PACKET_ADDED";
  public static final String OUT_PACKET_ADDED = "OUT_PACKET_ADDED";

  /**
   * NetworkElements class.
   *
   */
  public class NetworkElements {
    public static final String TYPE = "type";
    public static final String VERSION = "version";
    public static final String NODE_ID = "node_id";
    public static final String PORT_ID = "port_id";
    public static final String LINK_ID = "link_id";
    public static final String FLOW_ID = "flow_id";
    public static final String IN_LINK = "in_link";
    public static final String OUT_LINK = "out_link";
    public static final String SRC_NODE = "src_node";
    public static final String SRC_PORT = "src_port";
    public static final String DST_NODE = "dst_node";
    public static final String DST_PORT = "dst_port";
    public static final String OWNER = "owner";
    public static final String ENABLED = "enabled";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";
  }

  /**
   * AttrElements class.
   *
   */
  public class AttrElements {
    public static final String ATTRIBUTES = "attributes";
    public static final String ADMIN_STATUS = "admin_status";
    public static final String OPER_STATUS = "oper_status";
    public static final String PHYSICAL_ID = "physical_id";
    public static final String VENDOR = "vendor";
    public static final String MAX_BANDWIDTH = "max_bandwidth";
    public static final String UNRESERVED_BANDWIDTH = "unreserved_bandwidth";
    public static final String IS_BOUNDARY = "is_boundary";
    public static final String COST = "cost";
    public static final String LATENCY = "latency";
    public static final String BANDWIDTH = "bandwidth";
    public static final String REQ_LATENCY = "req_latency";
    public static final String REQ_BANDWIDTH = "req_bandwidth";
    public static final String ESTABLISHMENT_STATUS = "establishment_status";
  }

  /**
   * flow's keys.
   */
  public static final ArrayList<String> keysFlow =
      new ArrayList<String>(Arrays.asList(
          NetworkElements.TYPE, NetworkElements.VERSION,
          NetworkElements.FLOW_ID, NetworkElements.OWNER,
          NetworkElements.ENABLED, NetworkElements.PRIORITY,
          NetworkElements.STATUS));
  /**
   * node's default attribute keys.
   */
  public static final ArrayList<String> attributesNode = new ArrayList<String>(
      Arrays.asList(
          AttrElements.ADMIN_STATUS, AttrElements.OPER_STATUS,
          AttrElements.PHYSICAL_ID, AttrElements.VENDOR));
  /**
   * port's default attribute keys.
   */
  public static ArrayList<String> attributesPort = new ArrayList<String>(
      Arrays.asList(
          AttrElements.ADMIN_STATUS, AttrElements.OPER_STATUS,
          AttrElements.PHYSICAL_ID, AttrElements.VENDOR,
          AttrElements.MAX_BANDWIDTH,
          AttrElements.UNRESERVED_BANDWIDTH,
          AttrElements.IS_BOUNDARY));
  /**
   * link's default attribute keys.
   */
  public static final ArrayList<String> attributesLink = new ArrayList<String>(
      Arrays.asList(
          AttrElements.OPER_STATUS, AttrElements.COST,
          AttrElements.LATENCY, AttrElements.REQ_LATENCY,
          AttrElements.MAX_BANDWIDTH,
          AttrElements.UNRESERVED_BANDWIDTH,
          AttrElements.REQ_BANDWIDTH));
  /**
   * flow's default attribute keys.
   */
  public static final ArrayList<String> attributesFlow = new ArrayList<String>(
      Arrays.asList(
          AttrElements.BANDWIDTH, AttrElements.REQ_BANDWIDTH,
          AttrElements.LATENCY, AttrElements.REQ_LATENCY));

  private static final String UPDATE_KEY = "UPDATE";

  private ConversionTable conversionTable;

  protected final ConversionTable conversionTable() {
    return this.conversionTable;
  }

  private SystemManagerInterface systemMngInterface;

  protected final SystemManagerInterface systemMngInterface() {
    return this.systemMngInterface;
  }

  private HashMap<String, NetworkInterface> networkInterfaces;

  protected final HashMap<String, NetworkInterface> networkInterfaces() {
    return this.networkInterfaces;
  }

  /**
   * Constructors.
   * @param objectId ID for Objects.
   * @param baseUri Base URI.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   * @deprecated @see #Logic(String, MessageDispatcher)
   */
  @Deprecated
  public Logic(
      final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher)
      throws Exception {
    this(objectId, dispatcher);
  }

  /**
   * Constructors.
   * @param objectId ID for Objects.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   */
  public Logic(
      final String objectId,
      final MessageDispatcher dispatcher)
      throws Exception {
    super(objectId, dispatcher);
    this.conversionTable = new ConversionTable();
    this.systemMngInterface = new SystemManagerInterface(dispatcher, objectId);
    this.networkInterfaces = new HashMap<String, NetworkInterface>();
  }

  // //////////////////////////////////////////////////
  //
  // NetworkComponentConnection
  //
  // //////////////////////////////////////////////////
  private void onEventComponentConnection(
      final ComponentConnectionChanged message) {

    ComponentConnection curr = message.curr();
    String nwcId = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    if (nwcId == null) {
      return;
    }

    if (CONN_ADD.equals(message.action())) {
      log.debug("Message Action is add.");
      if (this.onConnectionChangedAddedPre(message)) {
        // Add Network Interface
        if (this.networkInterfaces.containsKey(nwcId)) {
          return;
        }
        NetworkInterface networkInterface = new NetworkInterface(
            this.messageDispatcher, nwcId, getObjectId());
        this.networkInterfaces.put(nwcId, networkInterface);
        this.onConnectionChangedAdded(message);
        return;
      }
    } else if (CONN_UPDATE.equals(message.action())) {
      log.debug("Message Action is update.");
      if (this.onConnectionChangedUpdatePre(message)) {
        this.onConnectionChangedUpdate(message);
        return;
      }
    } else if (CONN_DELETE.equals(message.action())) {
      log.debug("Message Action is delete.");
      if (this.onConnectionChangedDeletePre(message)) {
        this.onConnectionChangedDelete(message);
        // Delete Network Interface
        this.networkInterfaces.remove(nwcId);
        return;
      }
    }
  }

  protected boolean onConnectionChangedAddedPre(
      final ComponentConnectionChanged message) {
    log.debug("");
    return true;
  }

  protected boolean onConnectionChangedUpdatePre(
      final ComponentConnectionChanged message) {
    log.debug("");
    return true;
  }

  protected boolean onConnectionChangedDeletePre(
      final ComponentConnectionChanged message) {
    log.debug("");
    return true;
  }

  protected void onConnectionChangedAdded(
      final ComponentConnectionChanged message) {
    log.debug("");
  }

  protected void onConnectionChangedUpdate(
      final ComponentConnectionChanged message) {
    log.debug("");
  }

  protected void onConnectionChangedDelete(
      final ComponentConnectionChanged message) {
    log.debug("");
  }

  // //////////////////////////////////////////////////
  //
  // EventSubscription
  //
  // //////////////////////////////////////////////////

  // Event Hash Table
  // event::[action(update)]::networkComponentId => update copy attribute
  // ex) NODE_CHANGED::network1 =>
  // [admin_status, oper_status, physical_id, vendor]
  private HashMap<String, ArrayList<String>> subscriptionTable =
      new HashMap<String, ArrayList<String>>();

  protected final void addEntryEventSubscription(
      final String event,
      final String nwcId) {
    log.debug("");
    if (event == null || nwcId == null) {
      return;
    }

    if (event.equals(NODE_CHANGED)) {
      eventSubscription.addFilter(nwcId,
          NodeChanged.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", NODE_CHANGED, nwcId), null);
    }
    if (event.equals("PORT_CHANGED")) {
      eventSubscription.addFilter(nwcId,
          PortChanged.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", PORT_CHANGED, nwcId), null);
    }
    if (event.equals("LINK_CHANGED")) {
      eventSubscription.addFilter(nwcId,
          LinkChanged.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", LINK_CHANGED, nwcId), null);
    }
    if (event.equals("FLOW_CHANGED")) {
      eventSubscription.addFilter(nwcId,
          FlowChanged.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", FLOW_CHANGED, nwcId), null);
    }
    if (event.equals("IN_PACKET_ADDED")) {
      eventSubscription.addFilter(nwcId,
          InPacketAdded.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", IN_PACKET_ADDED, nwcId), null);
    }
    if (event.equals("OUT_PACKET_ADDED")) {
      eventSubscription.addFilter(nwcId,
          OutPacketAdded.TYPE);
      subscriptionTable.put(String.format(
          "%s::%s", OUT_PACKET_ADDED, nwcId), null);
    }
  }

  protected final void removeEntryEventSubscription(
      final String event,
      final String nwcId) {
    log.debug("");
    if (event == null || nwcId == null) {
      return;
    }

    if (event.equals(NODE_CHANGED)) {
      eventSubscription.removeFilter(nwcId,
          NodeChanged.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", NODE_CHANGED, nwcId));
    }
    if (event.equals(PORT_CHANGED)) {
      eventSubscription.removeFilter(nwcId,
          PortChanged.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", PORT_CHANGED, nwcId));
    }
    if (event.equals(LINK_CHANGED)) {
      eventSubscription.removeFilter(nwcId,
          LinkChanged.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", LINK_CHANGED, nwcId));
    }
    if (event.equals(FLOW_CHANGED)) {
      eventSubscription.removeFilter(nwcId,
          FlowChanged.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", FLOW_CHANGED, nwcId));
    }
    if (event.equals(IN_PACKET_ADDED)) {
      eventSubscription.removeFilter(nwcId,
          InPacketAdded.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", IN_PACKET_ADDED, nwcId));
    }
    if (event.equals(OUT_PACKET_ADDED)) {
      eventSubscription.removeFilter(nwcId,
          OutPacketAdded.TYPE);
      subscriptionTable.remove(String.format(
          "%s::%s", OUT_PACKET_ADDED, nwcId));
    }
  }

  protected final void
      updateEntryEventSubscription(
          final String event,
          final String nwcId,
          final ArrayList<String> attr) {
    log.debug("");

    ArrayList<String> attributes = attr;
    if (attr == null) {
      attributes = new ArrayList<String>();
    }
    if (event.equals(NODE_CHANGED)) {
      eventSubscription.addFilter(nwcId,
          NodeChanged.TYPE);
      subscriptionTable.put(String.format("%s::%s::%s",
          NODE_CHANGED, UPDATE_KEY, nwcId), attributes);
    }
    if (event.equals(PORT_CHANGED)) {
      eventSubscription.addFilter(nwcId,
          PortChanged.TYPE);
      subscriptionTable.put(String.format("%s::%s::%s",
          PORT_CHANGED, UPDATE_KEY, nwcId), attributes);
    }
    if (event.equals(LINK_CHANGED)) {
      eventSubscription.addFilter(nwcId,
          LinkChanged.TYPE);
      subscriptionTable.put(String.format("%s::%s::%s",
          LINK_CHANGED, UPDATE_KEY, nwcId), attributes);
    }
    if (event.equals(FLOW_CHANGED)) {
      eventSubscription.addFilter(nwcId,
          FlowChanged.TYPE);
      subscriptionTable.put(String.format("%s::%s::%s",
          FLOW_CHANGED, UPDATE_KEY, nwcId), attributes);
    }
  }

  // //////////////////////////////////////////////////
  //
  // EventDispatcher
  //
  // //////////////////////////////////////////////////
  @Override
  protected void onEvent(final Event event) {
    log.debug("onEvent : objectId = '{}'.", this.getObjectId());

    try {
      if (ComponentConnectionChanged.TYPE.equals(event.eventType)) {
        log.debug("onEvent ConnectionChanged : objectId = '{}'.",
                this.getObjectId());
        onEventComponentConnection(event
            .getBody(ComponentConnectionChanged.class));
        return;
      }

      log.debug("Recieved Message: {}", event.eventType);
      if (event.eventType == null) {
        return;
      }

      switch (event.eventType) {
        case NodeChanged.TYPE:
          onNodeChanged(event.publisherId,
              event.getBody(NodeChanged.class));
          break;
        case PortChanged.TYPE:
          onPortChanged(event.publisherId,
              event.getBody(PortChanged.class));
          break;
        case LinkChanged.TYPE:
          onLinkChanged(event.publisherId,
              event.getBody(LinkChanged.class));
          break;
        case FlowChanged.TYPE:
          onFlowChanged(event.publisherId,
              event.getBody(FlowChanged.class));
          break;
        case InPacketAdded.TYPE:
          onInPacketAdded(event.publisherId,
              event.getBody(InPacketAdded.class));
          break;
        case OutPacketAdded.TYPE:
          onOutPacketAdded(event.publisherId,
              event.getBody(OutPacketAdded.class));
          break;
        default:
          log.info("Unexpected event: {}", event.eventType);
          break;
      }
    } catch (ParseBodyException e) {
      log.error("Recieved Message which can't be parsed.", e);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
  }

  protected void onNodeChanged(
      final String networkId,
      final NodeChanged msg)
      throws Exception {
    log.debug("Recieved NodeChangedMessage [{}]networkId:{}",
                msg.action, networkId);

    String key = null;
    switch (msg.action) {
      case "add":
        key = String.format("%s::%s",
            NODE_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onNodeAdded(networkId, msg.curr);
        }
        break;
      case "update":
        key = String.format("%s::%s::%s",
            NODE_CHANGED, UPDATE_KEY, networkId);
        if (subscriptionTable.containsKey(key)) {
          onNodeUpdate(networkId, msg.prev, msg.curr,
              subscriptionTable.get(key));
        }
        break;
      case "delete":
        key = String.format("%s::%s",
            NODE_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onNodeDelete(networkId, msg.prev);
        }
        break;
      default:
        log.debug("invalid action");
        return;
    }
  }

  protected void onPortChanged(
      final String networkId,
      final PortChanged msg)
      throws Exception {
    log.debug("Recieved PortChangedMessage [{}]networkId:{}",
              msg.action, networkId);

    String key = null;
    switch (msg.action) {
      case "add":
        key = String.format("%s::%s",
            PORT_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onPortAdded(networkId, msg.curr);
        }
        break;
      case "update":
        key = String.format("%s::%s::%s",
            PORT_CHANGED, UPDATE_KEY, networkId);
        if (subscriptionTable.containsKey(key)) {
          onPortUpdate(networkId, msg.prev, msg.curr,
              subscriptionTable.get(key));
        }
        break;
      case "delete":
        key = String.format("%s::%s",
            PORT_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onPortDelete(networkId, msg.prev);
        }
        break;
      default:
        log.debug("invalid action");
        return;
    }
  }

  protected void onLinkChanged(
      final String networkId,
      final LinkChanged msg)
      throws Exception {
    log.debug("Recieved LinkChangedMessage [{}]networkId:{}",
              msg.action, networkId);

    String key = null;
    switch (msg.action) {
      case "add":
        key = String.format("%s::%s",
            LINK_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onLinkAdded(networkId, msg.curr);
        }
        break;
      case "update":
        key = String.format("%s::%s::%s",
            LINK_CHANGED, UPDATE_KEY, networkId);
        if (subscriptionTable.containsKey(key)) {
          onLinkUpdate(networkId, msg.prev, msg.curr,
              subscriptionTable.get(key));
        }
        break;
      case "delete":
        key = String.format("%s::%s",
            LINK_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onLinkDelete(networkId, msg.prev);
        }
        break;
      default:
        log.debug("invalid action");
        return;
    }
  }

  protected void onFlowChanged(
      final String networkId,
      final FlowChanged msg)
      throws Exception {
    log.debug("Recieved FlowChangedMessage [{}]networkId:{}",
              msg.action, networkId);

    String key = null;
    switch (msg.action) {
      case "add":
        key = String.format("%s::%s",
            FLOW_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onFlowAdded(networkId, msg.curr);
        }
        break;
      case "update":
        key = String.format("%s::%s::%s",
            FLOW_CHANGED, UPDATE_KEY, networkId);
        if (subscriptionTable.containsKey(key)) {
          onFlowUpdate(networkId, msg.prev, msg.curr,
              subscriptionTable.get(key));
        }
        break;
      case "delete":
        key = String.format("%s::%s",
            FLOW_CHANGED, networkId);
        if (subscriptionTable.containsKey(key)) {
          onFlowDelete(networkId, msg.prev);
        }
        break;
      default:
        log.debug("invalid action");
        return;
    }
  }

  // //////////////////////////////////////////////////
  // Event method (use Override)
  // //////////////////////////////////////////////////

  // / packet ///
  protected void onInPacketAdded(
      final String networkId,
      final InPacketAdded msg) {
    log.debug("");
    if (onInPacketAddedPre(networkId, msg)) {
      HashMap<String, Response> respList = conversion(networkId, msg);
      onInPacketAddedPost(networkId, msg, respList);
    }
  }

  protected boolean onInPacketAddedPre(
      final String networkId,
      final InPacketAdded msg) {
    log.debug("");
    return true;
  }

  protected void onInPacketAddedPost(
      final String networkId,
      final InPacketAdded msg,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  protected void onOutPacketAdded(
      final String networkId,
      final OutPacketAdded msg) {
    log.debug("");
    if (onOutPacketAddedPre(networkId, msg)) {
      HashMap<String, Response> respList = conversion(networkId, msg);
      onOutPacketAddedPost(networkId, msg, respList);
    }
  }

  protected boolean onOutPacketAddedPre(
      final String networkId,
      final OutPacketAdded msg) {
    log.debug("");
    return true;
  }

  protected void onOutPacketAddedPost(
      final String networkId,
      final OutPacketAdded msg,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / add node ///
  protected void onNodeAdded(
      final String networkId,
      final Node node) {
    log.debug("");
    if (onNodeAddedPre(networkId, node)) {
      HashMap<String, Response> respList = conversion(networkId, node);
      onNodeAddedPost(networkId, node, respList);
    }
  }

  protected boolean onNodeAddedPre(
      final String networkId, final Node node) {
    log.debug("");
    return true;
  }

  protected void onNodeAddedPost(
      final String networkId,
      final Node node,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / add port ///
  protected void onPortAdded(
      final String networkId, final Port port) {
    log.debug("");
    if (onPortAddedPre(networkId, port)) {
      HashMap<String, Response> respList = conversion(networkId, port);
      onPortAddedPost(networkId, port, respList);
    }
  }

  protected boolean onPortAddedPre(
      final String networkId, final Port port) {
    log.debug("");
    return true;
  }

  protected void onPortAddedPost(
      final String networkId,
      final Port port,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / add link ///
  protected void onLinkAdded(
      final String networkId, final Link link) {
    log.debug("");
    if (onLinkAddedPre(networkId, link)) {
      HashMap<String, Response> respList = conversion(networkId, link);
      onLinkAddedPost(networkId, link, respList);
    }
  }

  protected boolean onLinkAddedPre(
      final String networkId, final Link link) {
    log.debug("");
    return true;
  }

  protected void onLinkAddedPost(
      final String networkId,
      final Link link,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / add flow ///
  protected void onFlowAdded(final String networkId, final Flow flow) {
    log.debug("");
    if (onFlowAddedPre(networkId, flow)) {
      HashMap<String, Response> respList = conversion(networkId, flow);
      onFlowAddedPost(networkId, flow, respList);
    }
  }

  protected boolean onFlowAddedPre(
      final String networkId, final Flow flow) {
    log.debug("");
    return true;
  }

  protected void onFlowAddedPost(
      final String networkId,
      final Flow flow,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / update ///
  protected void onNodeUpdate(
      final String networkId,
      final Node prev,
      final Node curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    if (onNodeUpdatePre(networkId, prev, curr, attributesList)) {
      HashMap<String, Response> respList = conversion(networkId, prev,
          curr, attributesList);
      onNodeUpdatePost(networkId, prev, curr, attributesList, respList);
    }
  }

  protected boolean onNodeUpdatePre(
      final String networkId,
      final Node prev,
      final Node curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    return true;
  }

  protected void onNodeUpdatePost(
      final String networkId,
      final Node prev,
      final Node curr,
      final ArrayList<String> attributesList,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  protected void onPortUpdate(
      final String networkId,
      final Port prev,
      final Port curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    if (onPortUpdatePre(networkId, prev, curr, attributesList)) {
      HashMap<String, Response> respList = conversion(networkId, prev,
          curr, attributesList);
      onPortUpdatePost(networkId, prev, curr, attributesList, respList);
    }
  }

  protected boolean onPortUpdatePre(
      final String networkId,
      final Port prev,
      final Port curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    return true;
  }

  protected void onPortUpdatePost(
      final String networkId,
      final Port prev,
      final Port curr,
      final ArrayList<String> attributesList,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  protected void onLinkUpdate(
      final String networkId,
      final Link prev,
      final Link curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    if (onLinkUpdatePre(networkId, prev, curr, attributesList)) {
      HashMap<String, Response> respList = conversion(networkId, prev,
          curr, attributesList);
      onLinkUpdatePost(networkId, prev, curr, attributesList, respList);
    }
  }

  protected boolean onLinkUpdatePre(
      final String networkId,
      final Link prev,
      final Link curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    return true;
  }

  protected void onLinkUpdatePost(
      final String networkId,
      final Link prev,
      final Link curr,
      final ArrayList<String> attributesList,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  protected void onFlowUpdate(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    if (onFlowUpdatePre(networkId, prev, curr, attributesList)) {
      HashMap<String, Response> respList = conversion(networkId, prev,
          curr, attributesList);
      onFlowUpdatePost(networkId, prev, curr, attributesList, respList);
    }
  }

  protected boolean onFlowUpdatePre(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList) {
    log.debug("");
    return true;
  }

  protected void onFlowUpdatePost(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / delete node ///
  protected void onNodeDelete(
      final String networkId,
      final Node node) {
    log.debug("");
    if (onNodeDeletePre(networkId, node)) {
      HashMap<String, Response> respList = deleteConversion(networkId,
          node);
      onNodeDeletePost(networkId, node, respList);
    }
  }

  protected boolean onNodeDeletePre(
      final String networkId, final Node node) {
    return true;
  }

  protected void onNodeDeletePost(
      final String networkId,
      final Node node,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / delete port ///
  protected void onPortDelete(
      final String networkId, final Port port) {
    log.debug("");
    if (onPortDeletePre(networkId, port)) {
      HashMap<String, Response> respList = deleteConversion(networkId,
          port);
      onPortDeletePost(networkId, port, respList);
    }
  }

  protected boolean onPortDeletePre(
      final String networkId, final Port port) {
    log.debug("");
    return true;
  }

  protected void onPortDeletePost(
      final String networkId,
      final Port port,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / delete link ///
  protected void onLinkDelete(
      final String networkId, final Link link) {
    log.debug("");
    if (onLinkDeletePre(networkId, link)) {
      HashMap<String, Response> respList = deleteConversion(networkId,
          link);
      onLinkDeletePost(networkId, link, respList);
    }
  }

  protected boolean onLinkDeletePre(
      final String networkId, final Link link) {
    log.debug("");
    return true;
  }

  protected void onLinkDeletePost(
      final String networkId,
      final Link link,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // / delete flow ///
  protected void onFlowDelete(
      final String networkId, final Flow flow) {
    log.debug("");
    if (onFlowDeletePre(networkId, flow)) {
      HashMap<String, Response> respList = deleteConversion(networkId,
          flow);
      onFlowDeletePost(networkId, flow, respList);
    }
  }

  protected boolean onFlowDeletePre(
      final String networkId, final Flow flow) {
    log.debug("");
    return true;
  }

  protected void onFlowDeletePost(
      final String networkId,
      final Flow flow,
      final HashMap<String, Response> respList) {
    log.debug("");
  }

  // //////////////////////////////////////////////////
  //
  // action add conversion
  //
  // //////////////////////////////////////////////////

  protected final HashMap<String, Response>
      conversion(final String networkId, final Node node) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    for (String nwcId : conversionTable.getNetwork(networkId)) {
      NetworkInterface networkIf = this.networkInterfaces.get(nwcId);
      if (networkIf == null) {
        continue;
      }
      Response resp = networkIf.putNode(node);
      respList.put(nwcId, resp);
      try {
        Node body = resp.getBody(Node.class);
        conversionTable.addEntryNode(
            networkId, node.getId(), nwcId, body.getId());
        for (String portId : body.getPortMap().keySet()) {
          Port port = body.getPort(portId);
          conversionTable.addEntryPort(
              networkId, port.getNode(), port.getId(),
              nwcId, port.getNode(), port.getId());
        }
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }
    }
    return respList;
  }

  protected final HashMap<String, Response>
      conversion(final String networkId, final Port port) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    for (String nwcId : conversionTable.getNetwork(networkId)) {
      NetworkInterface networkIf = this.networkInterfaces.get(nwcId);
      if (networkIf == null) {
        continue;
      }
      Response resp = networkIf.putPort(port);
      respList.put(nwcId, resp);
      try {
        Port body = resp.getBody(Port.class);
        conversionTable.addEntryPort(
            networkId, port.getNode(), port.getId(),
            nwcId, body.getNode(), body.getId());
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }
    }
    return respList;
  }

  protected final HashMap<String, Response>
      conversion(final String networkId, final Link link) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    for (String nwcId : conversionTable.getNetwork(networkId)) {
      NetworkInterface networkIf = this.networkInterfaces.get(nwcId);
      if (networkIf == null) {
        continue;
      }
      Response resp = networkIf.putLink(link);
      respList.put(nwcId, resp);
      try {
        Link body = resp.getBody(Link.class);
        conversionTable.addEntryLink(
            networkId, link.getId(), nwcId, body.getId());
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }
    }
    return respList;
  }

  protected final HashMap<String, Response>
      conversion(final String networkId, final Flow flow) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    for (String nwcId : conversionTable.getNetwork(networkId)) {
      NetworkInterface networkIf = networkInterfaces.get(nwcId);
      if (networkIf == null) {
        return respList;
      }

      // conversion flow info.
      if (flow instanceof BasicFlow) {
        BasicFlow convFlow = (BasicFlow) flow.clone();
        conversionFlowInfo(networkId, convFlow);

        Response resp = networkIf.putFlow(convFlow);
        respList.put(nwcId, resp);
      } else {
        log.warn("There is no inheritance relationship with 'BasicFlow'.");
      }

    }
    return respList;
  }

  protected final HashMap<String, Response>
      conversion(final String networkId, final InPacketAdded msg) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    try {
      NetworkInterface networkIf = this.networkInterfaces.get(networkId);
      if (networkIf == null) {
        return respList;
      }

      InPacket body = delInPacket(networkIf, msg.getId());
      if (body == null) {
        log.error("invalid DELETE Packet.");
        return respList;
      }

      // Convert in_node
      if (body.getNodeId() == null) {
        return respList;
      }
      String preNodeId = body.getNodeId();
      ArrayList<String> convInNodeId =
          conversionTable.getNode(networkId, preNodeId);
      if (convInNodeId.size() == 0) {
        return respList;
      }
      String[] nlist = convInNodeId.get(0).split("::");
      body.setNodeId(nlist[1]);

      // Convert in_ports.
      if (body.getPortId() == null) {
        return respList;
      }
      ArrayList<String> convInPortId =
          conversionTable.getPort(networkId, preNodeId,
              body.getPortId());
      if (convInPortId.size() == 0) {
        return respList;
      }
      String[] plist = convInPortId.get(0).split("::");
      body.setPortId(plist[2]);

      // Convert header.
      ArrayList<String> convPortId = conversionTable.getPort(
          networkId, body.getHeader().getInNode(), body.getHeader()
              .getInPort());
      if (convPortId.size() == 0) {
        return respList;
      }
      String[] list = convPortId.get(0).split("::");
      body.getHeader().setInNode(list[1]);
      body.getHeader().setInPort(list[2]);

      NetworkInterface networkIfpost = networkInterfaces().get(list[0]);
      respList.put(networkIfpost.getNetworkId(),
          networkIfpost.postInPacket(body));
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
    return respList;
  }

  protected final HashMap<String, Response>
      conversion(final String networkId, final OutPacketAdded msg) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();

    try {
      NetworkInterface networkIf = this.networkInterfaces.get(networkId);
      if (networkIf == null) {
        return respList;
      }
      OutPacket body = delOutPacket(networkIf, msg.getId());
      if (body == null) {
        log.error("invalid DELETE Packet.");
        return respList;
      }

      // Convert header.
      ArrayList<String> convPortId = conversionTable.getPort(
          networkId, body.getHeader().getInNode(), body.getHeader()
              .getInPort());
      if (convPortId.size() != 0) {
        String[] plist = convPortId.get(0).split("::");
        body.getHeader().setInNode(plist[1]);
        body.getHeader().setInPort(plist[2]);
      }

      // Convert node
      if (body.getNodeId() == null) {
        return respList;
      }
      String preNodeId = body.getNodeId();
      ArrayList<String> convNodeId =
          conversionTable.getNode(networkId, preNodeId);
      if (convNodeId.size() == 0) {
        return respList;
      }
      String[] nlist = convNodeId.get(0).split("::");
      body.setNodeId(nlist[1]);

      // Convert ports, ports-except.
      List<String> ports = body.getPorts();
      List<String> convPorts = new ArrayList<String>();
      List<String> exceptPorts = body.getExceptPorts();
      List<String> convExceptPorts = new ArrayList<String>();
      if (ports != null && ports.size() > 0) {
        for (String p : ports) {
          ArrayList<String> convPId =
              conversionTable.getPort(
                  networkId, preNodeId, p);
          if (convPId.size() == 0) {
            return respList;
          }
          String[] pl = convPId.get(0).split("::");
          body.setNodeId(pl[1]);
          convPorts.add(pl[2]);
        }
      }
      if (exceptPorts != null && exceptPorts.size() > 0) {
        for (String p : exceptPorts) {
          ArrayList<String> convPId =
              conversionTable.getPort(
                  networkId, preNodeId, p);
          if (convPId.size() == 0) {
            return respList;
          }
          String[] pl = convPId.get(0).split("::");
          body.setNodeId(pl[1]);
          convExceptPorts.add(pl[2]);
        }
      }
      if (convPorts.size() > 0) {
        body.setPorts(convPorts);
      } else if (convExceptPorts.size() > 0) {
        body.setExceptPorts(convExceptPorts);
      }

      NetworkInterface networkIfpost = networkInterfaces().get(nlist[0]);
      respList.put(networkIfpost.getNetworkId(),
          networkIfpost.postOutPacket(body));
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }
    return respList;
  }

  // //////////////////////////////////////////////////
  //
  // action update
  //
  // //////////////////////////////////////////////////

  protected final HashMap<String, Response> conversion(
      final String networkId,
      final Node prev,
      final Node curr,
      final ArrayList<String> attr) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || curr == null) {
      return respList;
    }
    ArrayList<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }
    // make ignore list
    ArrayList<String> nodeMessageIgnoreAttributes =
        getIgnoreKeys(attributesNode, attributesList);

    for (String dstNode : conversionTable.getNode(networkId, curr.getId())) {
      String[] nodeId = dstNode.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(nodeId[0]);
      if (networkIf == null) {
        continue;
      }

      // GET node
      Node body = networkIf.getNode(nodeId[1]);
      if (body == null) {
        continue;
      }

      // attributes copy (curr -> body)
      boolean updated = false;
      Map<String, String> currAttributes = curr.getAttributes();
      for (String key : currAttributes.keySet()) {
        String oldAttr = prev.getAttribute(key);
        if (nodeMessageIgnoreAttributes.contains(key)
            || (oldAttr != null && oldAttr.equals(currAttributes
                .get(key)))) {
          continue;
        }
        updated = true;
        body.putAttribute(key, currAttributes.get(key));
      }
      if (updated) {
        // PUT Node
        respList.put(dstNode, networkIf.putNode(body));
      }
    }
    return respList;
  }

  protected final HashMap<String, Response> conversion(
      final String networkId,
      final Port prev,
      final Port curr,
      final ArrayList<String> attr) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || curr == null) {
      return respList;
    }
    ArrayList<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }
    // make ignore list
    ArrayList<String> portMessageIgnoreAttributes =
        getIgnoreKeys(attributesPort, attributesList);

    for (String dstPort : conversionTable.getPort(networkId,
        curr.getNode(), curr.getId())) {
      // GET port
      String[] portId = dstPort.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(portId[0]);
      if (networkIf == null) {
        continue;
      }

      Port body = networkIf.getPort(portId[1], portId[2]);
      if (body == null) {
        continue;
      }

      // attributes copy (curr -> body)
      boolean updated = false;
      Map<String, String> currAttributes = curr.getAttributes();
      for (String key : currAttributes.keySet()) {
        String oldAttr = prev.getAttribute(key);
        if (portMessageIgnoreAttributes.contains(key)
            || (oldAttr != null && oldAttr.equals(currAttributes
                .get(key)))) {
          continue;
        }
        updated = true;
        body.putAttribute(key, currAttributes.get(key));
      }
      if (updated) {
        // PUT Port
        respList.put(dstPort, networkIf.putPort(body));
      }
    }
    return respList;
  }

  protected final HashMap<String, Response> conversion(
      final String networkId,
      final Link prev,
      final Link curr,
      final ArrayList<String> attr) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || curr == null) {
      return respList;
    }
    ArrayList<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }
    // make ignore list
    ArrayList<String> messageIgnoreAttributes = getIgnoreKeys(
        attributesLink, attributesList);

    for (String dstLink : conversionTable.getLink(networkId, curr.getId())) {
      // GET link
      String[] linkId = dstLink.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(linkId[0]);
      if (networkIf == null) {
        continue;
      }

      Link body = networkIf.getLink(linkId[1]);
      if (body == null) {
        continue;
      }

      // attributes copy (curr -> body)
      boolean updated = false;
      Map<String, String> currAttributes = curr.getAttributes();
      for (String key : currAttributes.keySet()) {
        String oldAttr = prev.getAttribute(key);
        if (messageIgnoreAttributes.contains(key)
            || (oldAttr != null && oldAttr.equals(currAttributes
                .get(key)))) {
          continue;
        }
        updated = true;
        body.putAttribute(key, currAttributes.get(key));
      }
      if (updated) {
        // PUT link
        respList.put(dstLink, networkIf.putLink(body));
      }
    }
    return respList;
  }

  protected final HashMap<String, Response> conversion(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attr) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || curr == null) {
      return respList;
    }
    ArrayList<String> attributesList;
    if (attr == null) {
      attributesList = new ArrayList<String>();
    } else {
      attributesList = attr;
    }
    // make ignore list
    ArrayList<String> messageIgnoreKeys = getIgnoreKeys(keysFlow,
        attributesList);
    ArrayList<String> messageIgnoreAttributes = getIgnoreKeys(
        attributesFlow, attributesList);

    for (String dstFlow : conversionTable.getFlow(networkId,
        curr.getFlowId())) {
      // GET Flow
      String[] flowId = dstFlow.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(flowId[0]);
      if (networkIf == null) {
        continue;
      }

      Flow body = getFlow(networkIf, flowId[1]);
      if (body == null) {
        continue;
      }

      boolean updated = false;
      // key copy (curr -> body)
      if (!messageIgnoreKeys.contains(NetworkElements.ENABLED)
          && (body.getEnabled() != curr.getEnabled())) {
        updated = true;
        body.setEnabled(curr.getEnabled());
      }

      if (!messageIgnoreKeys.contains(NetworkElements.PRIORITY)
          && (body.getPriority().equals(curr.getPriority()))) {
        updated = true;
        body.setPriority(curr.getPriority());
      }

      if (!messageIgnoreKeys.contains(NetworkElements.STATUS)
          && (!body.getStatus().equals(curr.getStatus()))) {
        updated = true;
        body.setStatus(curr.getStatus());
      }

      // attributes copy (curr -> body)
      Map<String, String> currAttributes = curr.getAttributes();
      for (String key : currAttributes.keySet()) {
        String oldAttr = prev.getAttribute(key);
        if (messageIgnoreAttributes.contains(key)
            || (oldAttr != null && oldAttr.equals(currAttributes
                .get(key)))) {
          continue;
        }
        updated = true;
        body.putAttribute(key, currAttributes.get(key));
      }

      if (prev != null
          && prev instanceof BasicFlow
          && curr instanceof BasicFlow
          && body instanceof BasicFlow) {
        BasicFlow prevFlow = (BasicFlow) prev;
        BasicFlow currFlow = (BasicFlow) curr;
        BasicFlow bodyFlow = (BasicFlow) body;
        boolean changedFlowInfo = false;
        /**
         * update matches.
         */
        List<BasicFlowMatch> prevMatches = prevFlow.getMatches();
        List<BasicFlowMatch> currMatches = currFlow.getMatches();
        if (!prevMatches.equals(currMatches)) {
          changedFlowInfo = true;
        }
        /**
         * update actions.
         */
        Map<String, List<FlowAction>> prevEdgeActions = prevFlow.getEdgeActions();
        Map<String, List<FlowAction>> currEdgeActions = currFlow.getEdgeActions();
        if (!prevEdgeActions.equals(currEdgeActions)) {
          changedFlowInfo = true;
        }
        /**
         * update path.
         */
        List<String> prevPath = prevFlow.getPath();
        List<String> currPath = currFlow.getPath();
        if (!prevPath.equals(currPath)) {
          changedFlowInfo = true;
        }
        if (changedFlowInfo) {
          // reset matches, actions, path.
          bodyFlow.getMatches().clear();
          bodyFlow.getEdgeActions().clear();
          bodyFlow.getPath().clear();
          bodyFlow.putMatches(currMatches);
          bodyFlow.putEdgeActions(currEdgeActions);
          bodyFlow.putPath(currPath);
          // convert matches. actions, path.
          conversionFlowInfo(networkId, bodyFlow);
          updated = true;
        }
      }

      if (updated) {
        // PUT Flow
        respList.put(dstFlow, networkIf.putFlow(body));
      }

    }
    return respList;
  }

  // //////////////////////////////////////////////////
  //
  // action delete
  //
  // //////////////////////////////////////////////////

  protected final HashMap<String, Response> deleteConversion(
      final String networkId, final Node node) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (node == null) {
      return respList;
    }
    ArrayList<String> dstNodes = conversionTable.getNode(networkId,
        node.getId());

    for (String dstNode : dstNodes) {
      String[] nodeId = dstNode.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(nodeId[0]);
      if (networkIf == null) {
        continue;
      }

      respList.put(dstNode, networkIf.delNode(nodeId[1]));
    }
    conversionTable.delEntryNode(networkId, node.getId());
    return respList;
  }

  protected final HashMap<String, Response> deleteConversion(
      final String networkId, final Port port) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || port == null) {
      return respList;
    }
    ArrayList<String> dstPorts =
        conversionTable
            .getPort(networkId, port.getNode(), port.getId());

    for (String dstPort : dstPorts) {
      String[] portId = dstPort.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(portId[0]);
      if (networkIf == null) {
        continue;
      }

      respList.put(dstPort, networkIf.delPort(portId[1], portId[2]));
    }
    conversionTable.delEntryPort(networkId, port.getNode(), port.getId());
    return respList;
  }

  protected final HashMap<String, Response> deleteConversion(
      final String networkId, final Link link) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || link == null) {
      return respList;
    }
    ArrayList<String> dstLinks = conversionTable.getLink(networkId,
        link.getId());

    for (String dstLink : dstLinks) {
      String[] linkId = dstLink.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(linkId[0]);
      if (networkIf == null) {
        continue;
      }

      respList.put(dstLink, networkIf.delLink(linkId[1]));
    }
    conversionTable.delEntryLink(networkId, link.getId());
    return respList;
  }

  protected final HashMap<String, Response> deleteConversion(
      final String networkId, final Flow flow) {
    log.debug("");

    HashMap<String, Response> respList = new HashMap<String, Response>();
    if (networkId == null || flow == null) {
      return respList;
    }
    ArrayList<String> dstFlows = conversionTable.getFlow(networkId,
        flow.getFlowId());

    for (String dstFlow : dstFlows) {
      String[] flowId = dstFlow.split("::");

      NetworkInterface networkIf = this.networkInterfaces.get(flowId[0]);
      if (networkIf == null) {
        continue;
      }

      respList.put(dstFlow, networkIf.delFlow(flowId[1]));
    }

    NetworkInterface networkIf = this.networkInterfaces.get(networkId);
    Flow srcFlow = networkIf.getFlow(flow.getFlowId());
    if (srcFlow != null) {
      srcFlow.setEnabled(true);
      srcFlow.setStatus(FlowObject.FlowStatus.TEARDOWN.toString());
      networkIf.putFlow(srcFlow);

      srcFlow = networkIf.getFlow(flow.getFlowId());
      srcFlow.setEnabled(true);
      srcFlow.setStatus(FlowObject.FlowStatus.NONE.toString());
      networkIf.putFlow(srcFlow);
    }

    conversionTable.delEntryFlow(networkId, flow.getFlowId());
    return respList;
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  protected BasicFlow getFlow(
      final NetworkInterface nwIf,
      final String flowId) {
    log.debug("");

    return (BasicFlow) nwIf.getFlow(flowId);
  }

  protected InPacket getInPacket(
      final NetworkInterface nwIf,
      final String packetId) {
    log.debug("");

    return nwIf.getInPacket(packetId);
  }

  protected OutPacket getOutPacket(
      final NetworkInterface nwIf,
      final String packetId) {
    log.debug("");

    return nwIf.getOutPacket(packetId);
  }

  protected InPacket delInPacket(
      final NetworkInterface nwIf,
      final String packetId) {
    log.debug("");

    Response resp = nwIf.delInPacket(packetId);
    try {
      return PacketObject.readInPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  protected OutPacket delOutPacket(
      final NetworkInterface nwIf,
      final String packetId) {
    log.debug("");

    Response resp = nwIf.delOutPacket(packetId);
    try {
      return PacketObject.readOutPacketFrom(resp.getBodyValue());
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  protected void conversionFlowInfo(
      final String networkId, final BasicFlow convFlow) {
    log.debug("");

    try {
      /**
       * convert matches.
       */
      List<BasicFlowMatch> matches = convFlow.getMatches();
      List<BasicFlowMatch> convMatches = new ArrayList<>();
      if (matches == null) {
        matches = convMatches;
      }
      for (BasicFlowMatch match : matches) {
        BasicFlowMatch convMatch = match.clone();
        // update convMatches
        if (match.getInNode() != null && match.getInPort() != null) {
          ArrayList<String> convPortId = conversionTable.getPort(
              networkId, match.getInNode(), match.getInPort());
          if (convPortId.size() == 0) {
            log.error("not found conversion port (flow.match's in_port).");
            continue;
          }
          String[] plist = convPortId.get(0).split("::");
          convMatch.setInNode(plist[1]);
          convMatch.setInPort(plist[2]);
          convMatches.add(convMatch); // append match
        } else if (match.getInNode() != null) {
          ArrayList<String> convNodeId = conversionTable.getNode(
              networkId, match.getInNode());
          if (convNodeId.size() == 0) {
            log.error("not found conversion node (flow.match's in_node).");
            continue;
          }
          String[] nlist = convNodeId.get(0).split("::");
          convMatch.setInNode(nlist[1]);
          convMatches.add(convMatch); // append match
        }
      }
      log.info("before:" + matches.toString());
      log.info("after: " + convMatches.toString());
      /**
       * convert actions.
       */
      Map<String, List<FlowAction>> edgeActions = convFlow.getEdgeActions();
      Map<String, List<FlowAction>> convEdgeActions = new HashMap<>();
      if (edgeActions == null) {
        edgeActions = convEdgeActions;
      }
      for (String nodeId : edgeActions.keySet()) {
        // update convEdgeActions
        ArrayList<String> convNodeId = conversionTable.getNode(
            networkId, nodeId);
        if (convNodeId.size() == 0) {
          log.error("not found conversion node (flow.action's edge_node).");
          continue;
        }
        String[] nlist = convNodeId.get(0).split("::");
        String edgeNodeId = nlist[1];
        for (FlowAction action : edgeActions.get(nodeId)) {
          if (action instanceof FlowActionOutput) {
            FlowActionOutput outputAct = (FlowActionOutput) action;
            ArrayList<String> convPortId = conversionTable.getPort(
                networkId, nodeId, outputAct.getOutput());
            if (convPortId.size() == 0) {
              log.error("not found conversion port (flow.action's output).");
              continue;
            }
            String[] plist = convPortId.get(0).split("::");
            if (!convEdgeActions.containsKey(edgeNodeId)) {
              convEdgeActions.put(edgeNodeId, new ArrayList<FlowAction>());
            }
            convEdgeActions.get(edgeNodeId).add(new FlowActionOutput(plist[2]));
          } else {
            if (!convEdgeActions.containsKey(edgeNodeId)) {
              convEdgeActions.put(edgeNodeId, new ArrayList<FlowAction>());
            }
            convEdgeActions.get(edgeNodeId).add(action.clone());
          }
        }
      }
      log.info("before: " + edgeActions.toString());
      log.info("after: " + convEdgeActions.toString());
      /**
       * convert path.
       */
      List<String> path = convFlow.getPath();
      List<String> convPath = new ArrayList<>();
      if (path == null) {
        path = convPath;
      }
      for (String linkId : path) {
        // update convPath
        ArrayList<String> convLinkId =
            conversionTable.getLink(networkId, linkId);
        if (convLinkId.size() == 0) {
          log.error("not found conversion link (flow.path's linkId).");
          continue;
        }
        String[] llist = convLinkId.get(0).split("::");
        convPath.add(llist[1]);
      }
      log.info("before: " + path.toString());
      log.info("after: " + convPath.toString());

      /**
       * set conversion matches, actions, path.
       */
      convFlow.getMatches().clear();
      convFlow.putMatches(convMatches);
      convFlow.getEdgeActions().clear();
      convFlow.putEdgeActions(convEdgeActions);
      convFlow.getPath().clear();
      convFlow.putPath(convPath);
    } catch (Exception ex) {
      log.error("Received Exception, when Flow info conversion.", ex);
    }
  }

  // //////////////////////////////////////////////////
  //
  // private method
  //
  // //////////////////////////////////////////////////

  private ArrayList<String> getIgnoreKeys(
      final ArrayList<String> allkeys,
      final ArrayList<String> updatekeys) {

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
    log.debug("ignore key_list:: " + ignorekeys);
    return ignorekeys;
  }

}
