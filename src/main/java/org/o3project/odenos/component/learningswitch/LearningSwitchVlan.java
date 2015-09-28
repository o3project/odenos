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

package org.o3project.odenos.component.learningswitch;

import org.o3project.odenos.component.learningswitch.LearningSwitch;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LearningSwitch that emulates a layer 2 switch.
 *
 */
public class LearningSwitchVlan extends LearningSwitch {
  private static final Logger log = LogManager.getLogger(LearningSwitchVlan.class);
  /**

   * Constructors.
   * @param objectId ID for Object.
   * @param dispatcher Message dispatcher.
   * @throws Exception if parameter is wrong.
   */
  public LearningSwitchVlan(
      final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    log.debug("created");
  }

  /**
   * Description of Component.
   */
  private static final String DESCRIPTION = "Learning Switch VLAN";

  /**
   * Get Description of Component.
   *
   * @return Description
   */
  @Override
  protected String getDescription() {
    return DESCRIPTION;
  }

  //////////////////////////////
  // common method
  //////////////////////////////

  @Override
  protected BasicFlow createOFPFlow(
      final InPacket inPacket,
      final String outNodeId,
      final String outPortId,
      final List<String> path) {
    log.debug("");

    OFPFlowMatch header;
    try {
      header = (OFPFlowMatch) inPacket.getHeader();
    } catch (ClassCastException e) {
      log.error(LogMessage.buildLogMessage(50043, LogMessage.getSavedTxid(), "Recieved Message ClassCastException."), e);
      return null;
    }

    // match is in_node, in_port, eth_src, eth_dst.
    OFPFlowMatch match = new OFPFlowMatch();
    match.setInNode(header.getInNode());
    match.setInPort(header.getInPort());
    match.setEthSrc(header.getEthSrc());
    match.setEthDst(header.getEthDst());
    match.setVlanVid(header.getVlanVid());

    // matches
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    matches.add(match);

    // actions
    List<FlowAction> actionOutput =
        new ArrayList<FlowAction>();
    actionOutput.add(new FlowActionOutput(outPortId));
    Map<String, List<FlowAction>> edgeActions =
        new HashMap<String, List<FlowAction>>();
    edgeActions.put(outNodeId, actionOutput);

    String flowId = String.format(
        "%s_%d", this.getObjectId(), this.flows.size());
    String status = "none";
    boolean enabled = true;

    return new OFPFlow(
        inPacket.getVersion(), flowId, this.getObjectId(), enabled,
        DEFAULT_PRIORITY, status, matches,
        new Long(this.idleTimeout), new Long(this.hardTimeout),
        path, edgeActions, new HashMap<String, String>());
  }
}
