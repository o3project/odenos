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

package org.o3project.odenos.sample_components.drivers;

import org.o3project.odenos.core.component.Driver;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;

/**
 * DummyDriver1 class.
 *
 */
public class DummyDriver1 extends Driver {
  private static final Logger log = LogManager.getLogger(DummyDriver1.class);
  private static String network;
  private static final String description = "dummy driver 1";

  /**
   * Constructor.
   * @param objectId object id.
   * @param baseUri base URI.
   * @param dispatcher Message Dispatcher instance.
   * @throws Exception if parameter is wrong.
   * @deprecated @see #DummyDriver1(String, MessageDispatcher)
   */
  @Deprecated
  public DummyDriver1(
      final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, baseUri, dispatcher);
    resetEventSubscription();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "created."));
  }

  /**
   * Constructor.
   * @param objectId object id.
   * @param dispatcher Message Dispatcher instance.
   * @throws Exception if parameter is wrong.
   */
  public DummyDriver1(
      final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    resetEventSubscription();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "created."));
  }

  /**
   * Get Description of Component. Need to Implement at Inheritance Logic
   * Component.
   *
   * @return Description of Component
   */
  @Override
  protected final String getDescription() {
    return this.description;
  }

  // //////////////////////////////////////////////////
  // NetworkConnection
  // //////////////////////////////////////////////////
  @Override
  protected final boolean onConnectionChangedAddedPre(
      final ComponentConnectionChanged msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    if (!msg.curr().getObjectType()
        .equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }

    ComponentConnection compConn = msg.curr();
    if (this.network != null) {
      // Changed ConectionProperty's status.
      compConn.setConnectionState(ComponentConnection.State.ERROR);
      systemMngInterface().putConnection(compConn);
      return false;
    }

    return true;
  }

  @Override
  protected final void onConnectionChangedAdded(
      final ComponentConnectionChanged msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    ComponentConnection curr = msg.curr();
    this.network = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    subscribeNetworkComponent();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.RUNNING);
    systemMngInterface().putConnection(curr);
  }

  @Override
  protected final void onConnectionChangedDelete(
      final ComponentConnectionChanged message) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    ComponentConnection curr = message.curr();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    unsubscribeNetworkComponent();
    this.network = null;

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  private void subscribeNetworkComponent() {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    addEntryEventSubscription(FLOW_CHANGED, this.network);
    addEntryEventSubscription(OUT_PACKET_ADDED, this.network);

    updateEntryEventSubscription(FLOW_CHANGED, this.network, null);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  private void unsubscribeNetworkComponent() {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));
    removeEntryEventSubscription(FLOW_CHANGED, this.network);
    removeEntryEventSubscription(OUT_PACKET_ADDED, this.network);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  // //////////////////////////////////////////////////
  // Event method override
  // //////////////////////////////////////////////////
  @Override
  protected void onFlowAdded(
      final String networkId,
      final Flow flow) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "{} : {} ", networkId, flow));

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());
    if (targetFlow == null) {
      return;
    }

    // Status ... "None" => "Establishing" => "Established"
    if (targetFlow.getStatus()
        .equals(FlowObject.FlowStatus.NONE.toString())
        && targetFlow.getEnabled()) {
      targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHING.toString());
      networkIf.putFlow(targetFlow);

      // Driver needs to set Flow to physical switch here.
      // Setting of Flow After completing the physical switch,
      // to "Established".
      log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "added Flow: network={}, flow=''{}''", networkId, targetFlow.toString()));

      targetFlow = getFlow(networkIf, flow.getFlowId());
      targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHED.toString());
      networkIf.putFlow(targetFlow);
    }
  }

  @Override
  protected final void onFlowUpdate(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));
    this.onFlowAdded(networkId, curr);

  }

  @Override
  protected void onFlowDelete(
      final String networkId,
      final Flow flow) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "{} : {} ",networkId, flow));

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());
    if (targetFlow == null) {
      return;
    }

    if (targetFlow.getStatus().equals(
        FlowObject.FlowStatus.ESTABLISHED.toString())) {
      targetFlow.setStatus(FlowObject.FlowStatus.TEARDOWN.toString());
      networkIf.putFlow(targetFlow);

      // Driver needs to delete Flow to physical switch here.
      // Deleting of Flow After completing the physical switch,
      // to "None".
      log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "deleted Flow: network={}", networkId));

      targetFlow = getFlow(networkIf, flow.getFlowId());
      targetFlow.setStatus(FlowObject.FlowStatus.NONE.toString());
      networkIf.putFlow(targetFlow);
    }
  }

  @Override
  protected final void onOutPacketAdded(
      final String networkId,
      final OutPacketAdded msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    // GET Packet to Drop
    String packetId = msg.getId();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "receive OutPacket: {}", packetId));
    try {
      NetworkInterface networkIf = networkInterfaces().get(networkId);
      Response resp = networkIf.delOutPacket(packetId);
      if (resp.isError("DELETE")) {
        log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid DELETE Packet:{}", resp.statusCode));
      }
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  @Override
  protected BasicFlow getFlow(
      final NetworkInterface nwIf,
      final String flowId) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    if (nwIf == null || flowId == null) {
      return null;
    }
    BasicFlow flow = (BasicFlow) nwIf.getFlow(flowId);
    if (flow == null || flow.getMatches() == null
        || flow.getMatches().size() == 0) {
      return null;
    }

    return flow;
  }

}
