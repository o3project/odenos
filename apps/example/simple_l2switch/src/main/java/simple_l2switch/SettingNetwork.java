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

package simple_l2switch;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import simple_controller.DumpNetwork;

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

public class SettingNetwork extends StartSimpleL2Switch {
    private static Logger log = LogManager.getLogger(SettingNetwork.class);
    private static final int TXIDOFFSET = 9000000;

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
      LogMessage.initParameters(TXIDOFFSET);
      String txid = LogMessage.createTxid();
      LogMessage.setSavedTxid(txid);

      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Start initialization..."));

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
        Response rsp = null;

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (1) Set Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> ports1 = new HashMap<String, Port>();
        Map<String, Port> ports2 = new HashMap<String, Port>();
        Map<String, Port> ports3 = new HashMap<String, Port>();
        // Set NetworkIntece
        NetworkInterface networkInterface =
                new NetworkInterface(dispatcher, NETWORK_ID);
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node node1 = new Node(
                "0", "node1", ports1, new HashMap<String, String>());
        Node node2 = new Node(
                "0", "node2", ports2, new HashMap<String, String>());
        Node node3 = new Node(
                "0", "node3", ports3, new HashMap<String, String>());
        // PUT
        rsp = networkInterface.putNode(node1);
        outMsg("  -PUT Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        // PUT
        rsp = networkInterface.putNode(node2);
        outMsg("  -PUT Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        // PUT
        rsp = networkInterface.putNode(node3);
        outMsg("  -PUT Node. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port portN1P1 = new Port("0", "port1", node1.getId(),
                "", "", new HashMap<String, String>());
        Port portN1P2 = new Port("0", "port2", node1.getId(),
                "", "", new HashMap<String, String>());
        Port portN1P3 = new Port("0", "port3", node1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port portN2P1 = new Port("0", "port1", node2.getId(),
                "", "", new HashMap<String, String>());
        Port portN2P2 = new Port("0", "port2", node2.getId(),
                "", "", new HashMap<String, String>());
        // node3's ports
        Port portN3P1 = new Port("0", "port1", node3.getId(),
                "", "", new HashMap<String, String>());
        Port portN3P2 = new Port("0", "port2", node3.getId(),
                "", "", new HashMap<String, String>());
        Port portN3P3 = new Port("0", "port3", node3.getId(),
                "", "", new HashMap<String, String>());
        // PUT node1's ports
        rsp = networkInterface.putPort(portN1P1);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putPort(portN1P2);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putPort(portN1P3);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        // PUT node2's ports
        rsp = networkInterface.putPort(portN2P1);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putPort(portN2P2);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        // PUT node3's ports
        rsp = networkInterface.putPort(portN3P1);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putPort(portN3P2);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putPort(portN3P3);
        outMsg("  -PUT Port. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link link1 = new Link("0", "link1",
                portN1P1.getNode(), portN1P1.getId(), // src
                portN2P1.getNode(), portN2P1.getId(), // dst
                new HashMap<String, String>());
        Link link2 = new Link("0", "link2",
                portN2P2.getNode(), portN2P2.getId(), // src
                portN3P1.getNode(), portN3P1.getId(), // dst
                new HashMap<String, String>());
        Link link3 = new Link("0", "link3",
                portN3P2.getNode(), portN3P2.getId(), // src
                portN1P2.getNode(), portN1P2.getId(), // dst
                new HashMap<String, String>());
        rsp = networkInterface.putLink(link1);
        outMsg("  -PUT Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putLink(link2);
        outMsg("  -PUT Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        rsp = networkInterface.putLink(link3);
        outMsg("  -PUT Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (2) Dump Network's Topology.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (3) POST OFPInPacket to Network.");
        outMsg("//////////////////////////////////////////////////");
        byte[] data = {};
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OFPFlowMatch header = new OFPFlowMatch();
        header.setInNode(portN3P3.getNode());
        header.setInPort(portN3P3.getId());
        header.setEthDst("");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        InPacket inPacket = new OFPInPacket(
                "inpacket1", portN3P3.getNode(), portN3P3.getId(),
                1, header, data, new HashMap<String, String>());
        rsp = networkInterface.postInPacket(inPacket);
        outMsg("  -POST InPacket. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (4) Dump Network's In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpPackets(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (5) Get LearningSwitch's FDB.");
        outMsg("//////////////////////////////////////////////////");
        String path = String.format(
                "components/%s/fdb", LEARNING_SWITCH_ID);
        outMsg("  -GET fdb. ");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (6) POST OFPInPacket to Network.");
        outMsg("//////////////////////////////////////////////////");
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(portN1P3.getNode());
        header.setInPort(portN1P3.getId());
        header.setEthDst("ff:ff:ff:ff:ff:bb");
        header.setEthSrc("ff:ff:ff:ff:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket2", portN1P3.getNode(), portN1P3.getId(),
                1, header, data, new HashMap<String, String>());
        rsp = networkInterface.postInPacket(inPacket);
        outMsg("  -POST InPacket. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (7) Dump Network's In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpPackets(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (8) Dump Network's OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpOFPFlows(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (9) Get LearningSwitch's FDB.");
        outMsg("//////////////////////////////////////////////////");
        path = String.format(
                "components/%s/fdb", LEARNING_SWITCH_ID);
        outMsg("  -GET fdb. ");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (10) POST OFPInPacket to Network.");
        outMsg("//////////////////////////////////////////////////");
        try {
            data = "OFPInpacket Data3.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(portN3P3.getNode());
        header.setInPort(portN3P3.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket3", portN3P3.getNode(), portN3P3.getId(),
                1, header, data, new HashMap<String, String>());
        rsp = networkInterface.postInPacket(inPacket);
        outMsg("  -POST InPacket. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (11) Dump Network's In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpPackets(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (12) Dump Network's OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpOFPFlows(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (13) Delete Network's Link('link1'). ");
        outMsg("//////////////////////////////////////////////////");
        rsp = networkInterface.delLink(link1.getId());
        outMsg("  -DELETE Link. ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (14) Dump Network's Topology.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Topology
        DumpNetwork.dumpTopology(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (15) Dump Network's OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        // Dump OriginalNetwork's Packets.
        DumpNetwork.dumpOFPFlows(networkInterface);
        outMsg("");

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (16) Get LearningSwitch's FDB.");
        outMsg("//////////////////////////////////////////////////");
        path = String.format(
                "components/%s/fdb", LEARNING_SWITCH_ID);
        outMsg("  -GET fdb. ");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        dispatcher.close();
        System.exit(0);

    }

}
