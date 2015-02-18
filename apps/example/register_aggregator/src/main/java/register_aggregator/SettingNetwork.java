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

package register_aggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.core.component.network.packet.OFPOutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import simple_controller.DumpNetwork;


public class SettingNetwork extends RegisterAggregator {

    public static void main(String[] args) throws Exception {

        // /////////////////////////////////////
        // Set MessageDispatcher.
        // /////////////////////////////////////
        MessageDispatcher dispatcher = new MessageDispatcher(
                SYSTEM_MGR_ID,
                DISPATCHER_IP,
                Integer.parseInt(DISPATCHER_PORT));
        dispatcher.start();

        // /////////////////////////////////////
        // // Set SystemManager Interface
        // /////////////////////////////////////
        SystemManagerInterface systemMngInterface =
                new SystemManagerInterface(dispatcher);

        // /////////////////////////////////////
        // Set NetworkIntece
        // /////////////////////////////////////
        NetworkInterface originalNwInterface =
                new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
        NetworkInterface aggregatedNwInterface =
                new NetworkInterface(dispatcher, AGGREGATED_NW_ID);

        Response rsp = null;

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (1) Set OriginalNetwork's Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Node> nodes = new HashMap<String, Node>();
        Map<String, Port> ports1 = new HashMap<String, Port>();
        Map<String, Port> ports2 = new HashMap<String, Port>();
        Map<String, Link> links = new HashMap<String, Link>();

        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set OriginalNetwork's Nodes.");
        Node node1 = new Node(
                "0", "node1", ports1, new HashMap<String, String>());
        Node node2 = new Node(
                "0", "node2", ports2, new HashMap<String, String>());
        nodes.put(node1.getId(), node1);
        nodes.put(node2.getId(), node2);
        // PUT
        rsp = originalNwInterface.putNode(node1);
        outMsg("  -PUT Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        // PUT
        rsp = originalNwInterface.putNode(node2);
        outMsg("  -PUT Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set OriginalNetwork's Ports.");
        Port portN1P1 = new Port("0", "port1", "node1",
                "", "", new HashMap<String, String>());
        Port portN1P2 = new Port("0", "port2", "node1",
                "", "", new HashMap<String, String>());
        Port portN2P1 = new Port("0", "port1", "node2",
                "", "", new HashMap<String, String>());
        Port portN2P2 = new Port("0", "port2", "node2",
                "", "", new HashMap<String, String>());
        ports1.put(portN1P1.getId(), portN1P1);
        ports1.put(portN1P2.getId(), portN1P2);
        ports2.put(portN2P1.getId(), portN2P1);
        ports2.put(portN2P2.getId(), portN2P2);
        rsp = originalNwInterface.putPort(portN1P1);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.putPort(portN1P2);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.putPort(portN2P1);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.putPort(portN2P2);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set OriginalNetwork's Links.");
        Link link1 = new Link("0", "link1",
                portN1P1.getNode(), portN1P1.getId(), // src
                portN2P2.getNode(), portN2P2.getId(), // dst
                new HashMap<String, String>());
        Link link2 = new Link("0", "link2",
                portN2P2.getNode(), portN2P2.getId(), // src
                portN1P1.getNode(), portN1P1.getId(), // dst
                new HashMap<String, String>());
        links.put(link1.getId(), link1);
        links.put(link2.getId(), link2);
        rsp = originalNwInterface.putLink(link1);
        outMsg("  -PUT Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.putLink(link2);
        outMsg("  -PUT Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (2) Set Flow .");
        outMsg("//////////////////////////////////////////////////");
        // match1
        BasicFlowMatch match1 =
                new OFPFlowMatch();
        match1.setInNode("aggregator");
        match1.setInPort(String.format("%s_%s",
                portN1P2.getNode(), portN1P2.getId()));

        // match2
        BasicFlowMatch match2 =
                new OFPFlowMatch();
        match2.setInNode("aggregator");
        match2.setInPort(String.format("%s_%s",
                portN2P1.getNode(), portN2P1.getId()));

        // action1
        FlowAction action1 =
                new FlowActionOutput(String.format(
                        "%s_%s", portN2P1.getNode(), portN2P1.getId()));
        List<FlowAction> actionsList1 = new ArrayList<FlowAction>();
        actionsList1.add(action1);
        Map<String, List<FlowAction>> actions1 =
                new HashMap<String, List<FlowAction>>();
        actions1.put(AGGREGATOR_ID, actionsList1);
        // action2
        FlowAction action2 =
                new FlowActionOutput(
                        String.format(
                                "%s_%s", portN1P2.getNode(), portN1P2.getId()));
        List<FlowAction> actionsList2 = new ArrayList<FlowAction>();
        actionsList2.add(action2);
        Map<String, List<FlowAction>> actions2 =
                new HashMap<String, List<FlowAction>>();
        actions2.put(AGGREGATOR_ID, actionsList2);

        List<BasicFlowMatch> matches1 = new ArrayList<BasicFlowMatch>();
        List<BasicFlowMatch> matches2 = new ArrayList<BasicFlowMatch>();
        matches1.add(match1);
        matches2.add(match2);
        Flow basicFlow1 = new OFPFlow(
                "0", "flowN1P2", "logic1", true, "65535",
                "none", matches1, new Long(60), new Long(60),
                new ArrayList<String>(),
                actions1,
                new HashMap<String, String>());
        Flow basicFlow2 = new OFPFlow(
                "0", "flowN2P1", "logic2", true, "65535",
                "none", matches2, new Long(60), new Long(60),
                new ArrayList<String>(),
                actions2,
                new HashMap<String, String>());
        rsp = aggregatedNwInterface.putFlow(basicFlow1);
        outMsg("  -PUT Flow. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = aggregatedNwInterface.putFlow(basicFlow2);
        outMsg("  -PUT Flow. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        aggregatedNwInterface.getFlow(basicFlow2.getFlowId());

        outMsg("");
        wait(WAIT_TIME * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (3) Dump Topology(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Topology
        DumpNetwork.dumpTopology(aggregatedNwInterface);
        outMsg("");

        String path = String.format(
                "components/%s/aggregated_nw_port", AGGREGATOR_ID);
        outMsg("  -GET AggregatedNetwork's ConversionTable('Ports'). ");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        path = String.format(
                "components/%s/original_nw_port", AGGREGATOR_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -GET OriginalNetwork's ConversionTable('Ports'). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (4) Dump OFPFlow(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Flow.
        DumpNetwork.dumpOFPFlows(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Flow.
        DumpNetwork.dumpOFPFlows(aggregatedNwInterface);
        outMsg("");

        path = String.format(
                "components/%s/aggregated_nw_flow", AGGREGATOR_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -GET AggregatedNetwork's ConversionTable('Flows'). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        path = String.format(
                "components/%s/original_nw_flow", AGGREGATOR_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -GET OriginalNetwork's ConversionTable('Flows'). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (5) POST OFPInPacket To OriginalNetwork.");
        outMsg("////     (OriginalNw ==> AggregatedNw)");
        outMsg("//////////////////////////////////////////////////");
        String strData = "InPakcet Data.";
        byte[] data = null;
        data = strData.getBytes("UTF-8");
        OFPFlowMatch header = new OFPFlowMatch();
        header.setInNode(portN1P2.getNode());
        header.setInPort(portN1P2.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        InPacket inPacket = new OFPInPacket(
                "inpacket1", portN1P2.getNode(), portN1P2.getId(),
                1, header, data, new HashMap<String, String>());
        rsp = originalNwInterface.postInPacket(inPacket);
        outMsg("  -POST InPacket. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (6) POST OutPacket To AggregatedNetwork.");
        outMsg("////     (AggregatedNw ==> OriginalNw)");
        outMsg("//////////////////////////////////////////////////");
        strData = "OutPakcet Data.";
        data = null;
        data = strData.getBytes("UTF-8");
        header = new OFPFlowMatch();
        header.setInNode(AGGREGATOR_ID);
        header.setInPort(String.format(
                "%s_%s", portN2P1.getNode(), portN2P1.getId()));
        header.setEthDst("ff:ff:ff:ff:ff:cc");
        header.setEthSrc("ff:ff:ff:ff:ff:dd");
        List<String> portIds = new ArrayList<String>();
        portIds.add(String.format(
                "%s_%s", portN2P1.getNode(), portN2P1.getId()));
        List<String> portExceptIds = new ArrayList<String>();
        OutPacket outPacket = new OFPOutPacket(
                "outpacket1", AGGREGATOR_ID, portIds,
                portExceptIds, header, data,
                new HashMap<String, String>());
        rsp = aggregatedNwInterface.postOutPacket(outPacket);
        outMsg("  -POST OutPacket. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (7) Dump In/OutPacket(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpPackets(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Packets.
        DumpNetwork.dumpPackets(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (8) Delete OriginalNetwork's Link. ");
        outMsg("////      ('link1', 'link2'). ");
        outMsg("//////////////////////////////////////////////////");
        rsp = originalNwInterface.delLink(link1.getId());
        outMsg("  -DELETE Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.delLink(link2.getId());
        outMsg("  -DELETE Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (9) Dump Topology(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Topology
        DumpNetwork.dumpTopology(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (10) Dump OFPFlow(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Flow.
        DumpNetwork.dumpOFPFlows(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Flow.
        DumpNetwork.dumpOFPFlows(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (11) Delete OriginalNetwork's Port. ");
        outMsg("////      ('node1::port1', 'node2::port1'). ");
        outMsg("//////////////////////////////////////////////////");
        rsp = originalNwInterface.delPort(portN1P1.getNode(), portN1P1.getId());
        outMsg("  -DELETE Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = originalNwInterface.delPort(portN2P1.getNode(), portN2P1.getId());
        outMsg("  -DELETE Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (12) Dump Topology(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Topology
        DumpNetwork.dumpTopology(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (13) Delete OriginalNetwork's Node('node1').");
        outMsg("//////////////////////////////////////////////////");
        rsp = originalNwInterface.delNode(node1.getId());
        outMsg("  -DELETE Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (14) Dump Topology(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Topology
        DumpNetwork.dumpTopology(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (15) Delete OriginalNetwork's Node('node2').");
        outMsg("//////////////////////////////////////////////////");
        rsp = originalNwInterface.delNode(node2.getId());
        outMsg("  -DELETE Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (16) Dump Topology(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Topology
        DumpNetwork.dumpTopology(aggregatedNwInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (17) Delete AllFlow(Original/Aggregated Network).");
        outMsg("////        (Del Original's Flows => Del Aggregated's Flows).");
        outMsg("//////////////////////////////////////////////////");
        List<Response> rsps = originalNwInterface.deleteAllFlow();
        outMsg("  -DELETE OriginalNetwork's All Flow. ");
        for (Response rs : rsps) {
            outMsg("  -Received: " + rs.statusCode + " " + rs.getBodyValue());
        }

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (18) Dump OFPFlow(Original/Aggregated Network).");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Flow.
        DumpNetwork.dumpOFPFlows(originalNwInterface);
        outMsg("");
        // Dump AggregatedNetwork's Flow.
        DumpNetwork.dumpOFPFlows(aggregatedNwInterface);
        outMsg("");

        dispatcher.close();
        System.exit(0);

    }

}
