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

package simple_controller;

import java.util.List;
import java.util.Map;

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.FlowSet;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlIn;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlOut;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionGroupAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.PacketStatus;
import org.o3project.odenos.core.component.network.packet.PacketStatusSub;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.core.component.network.topology.Topology;

public class DumpNetwork {

  public DumpNetwork() {
  }

  public static void dumpTopology(final NetworkInterface networkIf) {
    String tab2 = "		";
    String tab4 = "				";
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("Topology Info.");
    outMsg("****************************************");
    Topology topology = networkIf.getTopology();
    if (topology == null || (topology.getLinkMap() == null || topology.getLinkMap().size() == 0)
        && (topology.getNodeMap() == null || topology.getNodeMap().size() == 0)) {
      outMsg("Topology does not exist.");
    }
    // Nodes & Ports
    for (String nodeId : topology.getNodeMap().keySet()) {
      outMsg(String.format(">> * nodeId : '%s'", nodeId));
      Node node = topology.getNodeMap().get(nodeId);
      if (node == null) {
        outMsg(String.format(">>%s * Node's data does not exist.", tab2));
        continue;
      }
      for (String portId : node.getPortMap().keySet()) {
        outMsg(String.format(">>%s * portId : '%s'", tab2, portId));
        Port port = node.getPortMap().get(portId);
        if (port == null) {
          outMsg(String.format(">>%s * Port's data does not exist.", tab4));
        }
        String outLink = port.getOutLink();
        String inLink = port.getInLink();
        outMsg(String.format(">>%s * inLink : '%s'", tab4, inLink));
        outMsg(String.format(">>%s * outLink : '%s'", tab4, outLink));
      }
      // Attributes
      if (node.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : node.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, node.getAttributes().get(attr)));
      }
    }
    // Links
    for (String linkId : topology.getLinkMap().keySet()) {
      outMsg(String.format(">> * linkId : %s", linkId));
      Link link = topology.getLinkMap().get(linkId);
      if (link == null) {
        outMsg(String.format(">>%s * Link's data does not exist.", tab2));
        continue;
      }
      outMsg(String.format(">>%s * srcNode : '%s'", tab2, link.getSrcNode()));
      outMsg(String.format(">>%s * srcPort : '%s'", tab2, link.getSrcPort()));
      outMsg(String.format(">>%s * dstNode : '%s'", tab2, link.getDstNode()));
      outMsg(String.format(">>%s * dstPort : '%s'", tab2, link.getDstPort()));
      // Attributes
      if (link.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : link.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, link.getAttributes().get(attr)));
      }
    }
    outMsg("****************************************");
  }

  public static void dumpNodes(final NetworkInterface networkIf) {
    String tab2 = "		";
    String tab4 = "				";
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("Nodes Info.");
    outMsg("****************************************");
    Map<String, Node> nodes = networkIf.getNodes();
    if (nodes == null || nodes.size() == 0) {
      outMsg("Nodes does not exist.");
    }
    // Nodes & Ports
    for (String nodeId : nodes.keySet()) {
      outMsg(String.format(">> * nodeId : '%s'", nodeId));
      Node node = nodes.get(nodeId);
      if (node == null) {
        outMsg(String.format(">>%s * Node's data does not exist.", tab2));
        continue;
      }
      for (String portId : node.getPortMap().keySet()) {
        outMsg(String.format(">>%s * portId : '%s'", tab2, portId));
        Port port = node.getPortMap().get(portId);
        if (port == null) {
          outMsg(String.format(">>%s * Port's data does not exist.", tab4));
        }
        String outLink = port.getOutLink();
        String inLink = port.getInLink();
        outMsg(String.format(">>%s * inLink : '%s'", tab4, inLink));
        outMsg(String.format(">>%s * outLink : '%s'", tab4, outLink));
      }
      // Attributes
      if (node.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : node.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, node.getAttributes().get(attr)));
      }
    }
    outMsg("****************************************");
  }

  public static void dumpLinks(final NetworkInterface networkIf) {
    String tab2 = "		";
    String tab4 = "				";
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("Links Info.");
    outMsg("****************************************");
    Map<String, Link> links = networkIf.getLinks();
    if (links == null || links.size() == 0) {
      outMsg("links does not exist.");
    }
    // Links
    for (String linkId : links.keySet()) {
      outMsg(String.format(">> * linkId : %s", linkId));
      Link link = links.get(linkId);
      if (link == null) {
        outMsg(String.format(">>%s * Link's data does not exist.", tab2));
        continue;
      }
      outMsg(String.format(">>%s * srcNode : '%s'", tab2, link.getSrcNode()));
      outMsg(String.format(">>%s * srcPort : '%s'", tab2, link.getSrcPort()));
      outMsg(String.format(">>%s * dstNode : '%s'", tab2, link.getDstNode()));
      outMsg(String.format(">>%s * dstPort : '%s'", tab2, link.getDstPort()));
      // Attributes
      if (link.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : link.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, link.getAttributes().get(attr)));
      }
    }
    outMsg("****************************************");
  }

  public static void dumpBasicFlows(final NetworkInterface networkIf) {
    String tab2 = "		";
    String tab4 = "				";
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("BasicFlow Info.");
    outMsg("****************************************");
    FlowSet flowSet = networkIf.getFlowSet();
    if (flowSet == null || flowSet.getFlows() == null || flowSet.getFlows().size() == 0) {
      outMsg("Flows does not exist.");
    }
    // Flows
    for (String flowId : flowSet.getFlows().keySet()) {
      outMsg(String.format(">> * flowId : %s", flowId));
      BasicFlow flow = (BasicFlow) flowSet.getFlow(flowId);
      if (flow == null) {
        outMsg(String.format(">>%s * BasicFlow's data does not exist.", tab2));
        continue;
      }
      outMsg(String.format(">>%s * ower : '%s'", tab2, flow.getOwner()));
      outMsg(String.format(">>%s * type : '%s'", tab2, flow.getType()));
      outMsg(String.format(">>%s * priority: '%s'", tab2, flow.getPriority()));
      outMsg(String.format(">>%s * enabled : '%s'", tab2, flow.getEnabled()));
      outMsg(String.format(">>%s * status : '%s'", tab2, flow.getStatus()));
      // Path
      if (flow.getPath() == null || flow.getPath().size() == 0) {
        outMsg(String.format(">>%s * BasicFlow's path does not exist.", tab2));
      } else {
        String paths = "";
        outMsg(String.format(">>%s * BasicFlow's path(links).", tab2));
        int cnt = 0;
        for (String linkId : flow.getPath()) {
          if (cnt++ == 0) {
            paths += String.format("%s", linkId);
          } else {
            paths += String.format(", %s", linkId);
          }
        }
        outMsg(String.format(">>%s *  path : '%s'", tab4, paths));
      }
      // Matches
      if (flow.getMatches() == null || flow.getMatches().size() == 0) {
        outMsg(String.format(">>%s * BasicFlow's matches does not exist.", tab2));
      } else {
        for (BasicFlowMatch match : flow.getMatches()) {
          outMsg(String.format(">>%s * BasicFlow's matches.", tab2));
          outMsg(String.format(">>%s * inNode : '%s'", tab4, match.getInNode()));
          outMsg(String.format(">>%s * inPort : '%s'", tab4, match.getInPort()));
        }
      }
      // Edge Action
      if (flow.getEdgeActions() == null || flow.getEdgeActions().size() == 0) {
        outMsg(String.format(">>%s * BasicFlow's edge_actions does not exist.", tab2));
      } else {
        for (String nodeId : flow.getEdgeActions().keySet()) {
          outMsg(String.format(">>%s * BasicFlow's edge_actions (nodeid : '%s').",
              tab2, nodeId));
          List<FlowAction> actions = flow.getEdgeActions(nodeId);
          if (actions == null || actions.size() == 0) {
            outMsg(String.format(">>%s * Actions does not exist.", tab4));
          }
          for (FlowAction action : actions) {
            outMsg(String.format(">>%s * type : '%s'", tab4, action.getType()));
            if (action.getType().equals(FlowActionOutput.class.getSimpleName())) {
              FlowActionOutput output = (FlowActionOutput) action;
              outMsg(String.format(">>%s     * output : '%s'", tab4, output.getOutput()));
            }
          }
        }
      }
      // Attributes
      if (flow.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : flow.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, flow.getAttributes().get(attr)));
      }
    }
    outMsg("****************************************");
  }

  public static void dumpOFPFlows(final NetworkInterface networkIf) {
    String tab2 = "		";
    String tab4 = "				";
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("OFPFlow Info.");
    outMsg("****************************************");
    FlowSet flowSet = networkIf.getFlowSet();
    if (flowSet == null || flowSet.getFlows() == null || flowSet.getFlows().size() == 0) {
      outMsg("Flows does not exist.");
    }
    // Flows
    for (String flowId : flowSet.getFlows().keySet()) {
      outMsg(String.format(">> * flowId : %s", flowId));
      OFPFlow flow = (OFPFlow) flowSet.getFlow(flowId);
      if (flow == null) {
        outMsg(String.format(">>%s * OFPFlow's data does not exist.", tab2));
        continue;
      }
      outMsg(String.format(">>%s * ower : '%s'", tab2, flow.getOwner()));
      outMsg(String.format(">>%s * type : '%s'", tab2, flow.getType()));
      outMsg(String.format(">>%s * priority: '%s'", tab2, flow.getPriority()));
      outMsg(String.format(">>%s * enabled : '%s'", tab2, flow.getEnabled()));
      outMsg(String.format(">>%s * status : '%s'", tab2, flow.getStatus()));
      outMsg(String.format(">>%s * idle_timeout : '%s'", tab2, flow.getIdleTimeout()));
      outMsg(String.format(">>%s * hard_timeout : '%s'", tab2, flow.getHardTimeout()));
      // Path
      if (flow.getPath() == null || flow.getPath().size() == 0) {
        outMsg(String.format(">>%s * OFPFlow's path does not exist.", tab2));
      } else {
        String paths = "";
        outMsg(String.format(">>%s * OFPFlow's path(links).", tab2));
        int cnt = 0;
        for (String linkId : flow.getPath()) {
          if (cnt++ == 0) {
            paths += String.format("%s", linkId);
          } else {
            paths += String.format(", %s", linkId);
          }
        }
        outMsg(String.format(">>%s *  path : '%s'", tab4, paths));
      }
      // Matches
      if (flow.getMatches() == null || flow.getMatches().size() == 0) {
        outMsg(String.format(">>%s * OFPFlow's matches does not exist.", tab2));
      } else {
        for (BasicFlowMatch match : flow.getMatches()) {
          OFPFlowMatch ofpMatch = (OFPFlowMatch) match;
          outMsg(String.format(">>%s * OFPFlow's matches.", tab2));
          outMsg(String.format(">>%s * inNode : '%s'", tab4, ofpMatch.getInNode()));
          outMsg(String.format(">>%s * inPort : '%s'", tab4, ofpMatch.getInPort()));
        }
      }
      // Edge Action
      if (flow.getEdgeActions() == null || flow.getEdgeActions().size() == 0) {
        outMsg(String.format(">>%s * OFPFlow's edge_actions does not exist.", tab2));
      } else {
        for (String nodeId : flow.getEdgeActions().keySet()) {
          outMsg(String.format(">>%s * OFPFlow's edge_actions (nodeid : '%s').", tab2, nodeId));
          List<FlowAction> actions = flow.getEdgeActions(nodeId);
          if (actions == null || actions.size() == 0) {
            outMsg(String.format(">>%s * Actions does not exist.", tab4));
            continue;
          }
          for (FlowAction action : actions) {
            outMsg(String.format(">>%s * type : '%s'", tab4, action.getType()));
            if (action.getType().equals(FlowActionOutput.class.getSimpleName())) {
              FlowActionOutput output = (FlowActionOutput) action;
              outMsg(String.format(">>%s   - '%s'", tab4, output.getOutput()));
            } else if (action.getType().equals(OFPFlowActionCopyTtlIn.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionCopyTtlOut.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionDecIpTtl.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionDecMplsTtl.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionExperimenter.class.getSimpleName())) {
              OFPFlowActionExperimenter act = (OFPFlowActionExperimenter) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getExperimenterId()));
            } else if (action.getType().equals(
                OFPFlowActionGroupAction.class.getSimpleName())) {
              OFPFlowActionGroupAction act = (OFPFlowActionGroupAction) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getGroupId()));
            } else if (action.getType().equals(
                OFPFlowActionPopMpls.class.getSimpleName())) {
              OFPFlowActionPopMpls act = (OFPFlowActionPopMpls) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getEthType()));
            } else if (action.getType().equals(OFPFlowActionPopPbb.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionPopVlan.class.getSimpleName())) {
              // do nothing.
            } else if (action.getType().equals(OFPFlowActionPushMpls.class.getSimpleName())) {
              OFPFlowActionPushMpls act = (OFPFlowActionPushMpls) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getEthType()));
            } else if (action.getType().equals(OFPFlowActionPushPbb.class.getSimpleName())) {
              OFPFlowActionPushPbb act = (OFPFlowActionPushPbb) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getEthType()));
            } else if (action.getType().equals(OFPFlowActionPushVlan.class.getSimpleName())) {
              OFPFlowActionPushVlan act = (OFPFlowActionPushVlan) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getEthType()));
            } else if (action.getType().equals(OFPFlowActionSetIpTtl.class.getSimpleName())) {
              OFPFlowActionSetIpTtl act = (OFPFlowActionSetIpTtl) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getIpTtl()));
            } else if (action.getType().equals(OFPFlowActionSetMplsTtl.class.getSimpleName())) {
              OFPFlowActionSetMplsTtl act = (OFPFlowActionSetMplsTtl) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getMplsTtl()));
            } else if (action.getType().equals(OFPFlowActionSetMplsTtl.class.getSimpleName())) {
              OFPFlowActionSetQueue act = (OFPFlowActionSetQueue) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getQueueId()));
            } else if (action.getType().equals(OFPFlowActionSetField.class.getSimpleName())) {
              OFPFlowActionSetField act = (OFPFlowActionSetField) action;
              outMsg(String.format(">>%s   - '%s'", tab4, act.getMatch()));
            }
          }
        }
      }
      // Attributes
      if (flow.getAttributes().size() == 0) {
        outMsg(String.format(">>%s * Attributes does not exit.", tab2));
      } else {
        outMsg(String.format(">>%s * Attributes.", tab2));
      }
      for (String attr : flow.getAttributes().keySet()) {
        outMsg(String.format(">>%s * '%s' : '%s'", tab4, attr, flow.getAttributes().get(attr)));
      }
    }
    outMsg("****************************************");
  }

  public static void dumpPackets(final NetworkInterface networkIf) {
    outMsg("****************************************");
    outMsg("Network ID = " + networkIf.getNetworkId());
    outMsg("Packets Info.");
    outMsg("****************************************");
    PacketStatus packetStatus = networkIf.getPackets();
    if (packetStatus == null) {
      outMsg("* PacketStatus does not exist.");
    }
    // InPackets
    String tab2 = "		";
    PacketStatusSub inStatus = packetStatus.getInStatus();
    outMsg(">> * InPacketStatus...");
    outMsg(String.format(">>%s * packetCount : '%s'", tab2, inStatus.getPacketCount()));
    outMsg(String.format(">>%s * packetBytes : '%s'", tab2, inStatus.getPacketBytes()));
    outMsg(String.format(">>%s * packetQueueCount : '%s'", tab2, inStatus.getPacketQueueCount()));
    String packetStr = "";
    if (inStatus.getPackets() != null) {
      for (String pid : inStatus.getPackets()) {
        if (packetStr.equals("")) {
          packetStr = pid;
        } else {
          packetStr += String.format(", %s", pid);
        }
      }
    }
    outMsg(String.format(">>%s * packetId : '%s'", tab2, packetStr));
    // OutPackets
    PacketStatusSub outStatus = packetStatus.getOutStatus();
    outMsg(">> * OutPacketStatus...");
    outMsg(String.format(">>%s * packetCount : '%s'", tab2, outStatus.getPacketCount()));
    outMsg(String.format(">>%s * packetBytes : '%s'", tab2, outStatus.getPacketBytes()));
    outMsg(String.format(">>%s * packetQueueCount : '%s'", tab2, outStatus.getPacketQueueCount()));
    packetStr = "";
    if (outStatus.getPackets() != null) {
      for (String pid : outStatus.getPackets()) {
        if (packetStr.equals("")) {
          packetStr = pid;
        } else {
          packetStr += String.format(", %s", pid);
        }
      }
    }
    outMsg(String.format(">>%s * packetId : '%s'", tab2, packetStr));
    outMsg("****************************************");
  }

  private static void outMsg(String msg) {
    System.out.println("  " + msg);
  }

}
