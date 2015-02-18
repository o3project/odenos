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

package sliver_network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.o3project.odenos.component.slicer.BasicSliceCondition;
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

import simple_controller.DumpNetwork;

public class SettingNetwork extends StartSliverNetwork {
    
    // Slice Condition ID
    protected static final String CONDITION1_ID = "slice_condition01";
    protected static final String CONDITION2_ID = "slice_condition02";
    protected static final String CONDITION3_ID = "slice_condition03";
    protected static final String CONDITION4_ID = "slice_condition04";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

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
        // // Set NetworkIntece
        // /////////////////////////////////////
        NetworkInterface orgNwIf =
                new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
        NetworkInterface sliv1NwIf =
                new NetworkInterface(dispatcher, SLIVER_01_NW_ID);
        NetworkInterface sliv2NwIf =
                new NetworkInterface(dispatcher, SLIVER_02_NW_ID);

        Response rsp = null;
        int n = 0;

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + ORIGINAL_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> ports1 = new HashMap<String, Port>();
        Map<String, Port> ports2 = new HashMap<String, Port>();
        Map<String, Port> ports3 = new HashMap<String, Port>();
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node node1 = new Node(
                "0", "node001", ports1, new HashMap<String, String>());
        Node node2 = new Node(
                "0", "node002", ports2, new HashMap<String, String>());
        Node node3 = new Node(
                "0", "node003", ports3, new HashMap<String, String>());
        rsp = orgNwIf.putNode(node1);
        // PUT node1
        dumpResponse("  -PUT Node. ", rsp);
        rsp = orgNwIf.putNode(node2);
        wait(WAIT_TIME);
        // PUT node2
        dumpResponse("  -PUT Node. ", rsp);
        rsp = orgNwIf.putNode(node3);
        wait(WAIT_TIME);
        // PUT node3
        dumpResponse("  -PUT Node. ", rsp);
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port port0011 = new Port("0", "port0011", node1.getId(),
                "", "", new HashMap<String, String>());
        Port port0012 = new Port("0", "port0012", node1.getId(),
                "", "", new HashMap<String, String>());
        Port port0013 = new Port("0", "port0013", node1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port port0021 = new Port("0", "port0021", node2.getId(),
                "", "", new HashMap<String, String>());
        Port port0022 = new Port("0", "port0022", node2.getId(),
                "", "", new HashMap<String, String>());
        Port port0023 = new Port("0", "port0023", node2.getId(),
                "", "", new HashMap<String, String>());
        Port port0024 = new Port("0", "port0024", node2.getId(),
                "", "", new HashMap<String, String>());
        // node3's ports
        Port port0031 = new Port("0", "port0031", node3.getId(),
                "", "", new HashMap<String, String>());
        Port port0032 = new Port("0", "port0032", node3.getId(),
                "", "", new HashMap<String, String>());
        Port port0033 = new Port("0", "port0033", node3.getId(),
                "", "", new HashMap<String, String>());
        wait(WAIT_TIME);
        // PUT node1's ports
        rsp = orgNwIf.putPort(port0011);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0012);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0013);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node2's ports
        rsp = orgNwIf.putPort(port0021);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0022);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0023);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0024);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node3's ports
        rsp = orgNwIf.putPort(port0031);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0032);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = orgNwIf.putPort(port0033);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link link1 = new Link("0", "link1",
                port0012.getNode(), port0012.getId(), // src
                port0021.getNode(), port0021.getId(), // dst
                new HashMap<String, String>());
        Link link2 = new Link("0", "link2",
                port0022.getNode(), port0022.getId(), // src
                port0031.getNode(), port0031.getId(), // dst
                new HashMap<String, String>());
        rsp = orgNwIf.putLink(link1);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        rsp = orgNwIf.putLink(link2);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + ORIGINAL_NW_ID + "/"
                + SLIVER_01_NW_ID + "/" + SLIVER_02_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        // original 
        DumpNetwork.dumpTopology(orgNwIf);
        // sliver1 
        DumpNetwork.dumpTopology(sliv1NwIf);
        // sliver22 
        DumpNetwork.dumpTopology(sliv2NwIf);
        outMsg("");
     
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set SliceCondition.");
        outMsg("//////////////////////////////////////////////////");
        // condition01
        String path = String.format(
                "components/%s/settings/slice_condition_table/%s/conditions/%s",
                SLICER_ID, 40, CONDITION1_ID);
        BasicSliceCondition condition;
        try {
            condition = new BasicSliceCondition(
                    CONDITION1_ID, // object_id
                    BasicSliceCondition.class.getSimpleName(), // type
                    SLICER_CONNECT02_ID,  // connection_id
                    port0033.getNode(), // in_node
                    port0033.getId() // in_port
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } 
        rsp = systemMngInterface.putObjectToSystemMng(path, condition);
        dumpResponse(" -PUT SliceCondition ", rsp);
        path = String.format(
                "components/%s/settings/slice_condition_table/%s/conditions/%s",
                SLICER_ID, 50, CONDITION2_ID);
        try {
            condition = new BasicSliceCondition(
                    CONDITION2_ID, // object_id
                    BasicSliceCondition.class.getSimpleName(), // type
                    SLICER_CONNECT02_ID,  // connection_id
                    port0013.getNode(), // in_node
                    port0013.getId() // in_port
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } 
        rsp = systemMngInterface.putObjectToSystemMng(path, condition);
        dumpResponse(" -PUT SliceCondition ", rsp);
        
        // condition03
        path = String.format(
                "components/%s/settings/slice_condition_table/%s/conditions/%s",
                SLICER_ID, 40, CONDITION3_ID);
        try {
            condition = new BasicSliceCondition(
                    CONDITION3_ID, // object_id
                    BasicSliceCondition.class.getSimpleName(), // type
                    SLICER_CONNECT03_ID,  // connection_id
                    port0032.getNode(), // in_node
                    port0032.getId() // in_port
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } 
        rsp = systemMngInterface.putObjectToSystemMng(path, condition);
        dumpResponse(" -PUT SliceCondition ", rsp);

        // condition04
        path = String.format(
                "components/%s/settings/slice_condition_table/%s/conditions/%s",
                SLICER_ID, 50, CONDITION4_ID);
        try {
            condition = new BasicSliceCondition(
                    CONDITION4_ID, // object_id
                    BasicSliceCondition.class.getSimpleName(), // type
                    SLICER_CONNECT03_ID,  // connection_id
                    port0011.getNode(), // in_node
                    port0011.getId() // in_port
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } 
        rsp = systemMngInterface.putObjectToSystemMng(path, condition);
        dumpResponse(" -PUT SliceCondition ", rsp);

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Get SliceConditionTable.");
        outMsg("//////////////////////////////////////////////////");
        path = String.format(
                "components/%s/settings/slice_condition_table",
                SLICER_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        dumpResponse(" -GET SliceConditionTable ", rsp);

        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Get SliceCondition.");
        outMsg("//////////////////////////////////////////////////");
        path = String.format(
                "components/%s/settings/slice_condition_table/conditions/%s",
                SLICER_ID, CONDITION1_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        dumpResponse(" -GET SliceCondition ", rsp);
        path = String.format(
                "components/%s/settings/slice_condition_table/conditions/%s",
                SLICER_ID, CONDITION2_ID);
        rsp = systemMngInterface.getObjectToSystemMng(path);
        dumpResponse(" -GET SliceCondition ", rsp);
        
        // [1] InPacket01 (original --> sliver01)
        outMsg("");
        wait(WAIT_TIME * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") POST OFPInPacket [case1] to " + ORIGINAL_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        byte[] data = {};
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OFPFlowMatch header = new OFPFlowMatch();
        header.setInNode(port0033.getNode());
        header.setInPort(port0033.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        InPacket inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);

        // [2] InPacket02 (original --> sliver01)
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(port0013.getNode());
        header.setInPort(port0013.getId());
        header.setEthDst("ff:ff:ff:ff:ff:bb");
        header.setEthSrc("ff:ff:ff:ff:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);

        // [3] InPacket03 (original --> sliver01)
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(port0033.getNode());
        header.setInPort(port0033.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);

        // [4] InPacket01 (original --> sliver02)
        outMsg("");
        wait(WAIT_TIME * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") POST OFPInPacket [case2] to " + ORIGINAL_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header.setInNode(port0011.getNode());
        header.setInPort(port0011.getId());
        header.setEthDst("ff:00:ff:00:ff:aa");
        header.setEthSrc("ff:00:ff:00:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);

        // [5] InPacket02 (original --> sliver02)
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(port0032.getNode());
        header.setInPort(port0032.getId());
        header.setEthDst("ff:00:ff:00:ff:bb");
        header.setEthSrc("ff:00:ff:00:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);

        // [6] InPacket03 (original --> sliver02)
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(port0011.getNode());
        header.setInPort(port0011.getId());
        header.setEthDst("ff:00:ff:00:ff:aa");
        header.setEthSrc("ff:00:ff:00:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = orgNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        
        outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + ORIGINAL_NW_ID + "'s In/OutPackets.");
        outMsg("////          and " + SLIVER_01_NW_ID + "/"
                                + SLIVER_02_NW_ID + "'s In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        DumpNetwork.dumpPackets(orgNwIf);
        wait(WAIT_TIME);
        DumpNetwork.dumpPackets(sliv1NwIf);
        wait(WAIT_TIME);
        DumpNetwork.dumpPackets(sliv2NwIf);
 
        outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(orgNwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(sliv1NwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(sliv2NwIf);
        outMsg("");
        
        dispatcher.close();
        System.exit(0);
    }
    
}
