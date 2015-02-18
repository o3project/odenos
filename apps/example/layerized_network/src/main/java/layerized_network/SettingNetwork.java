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

package layerized_network;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import simple_controller.DumpNetwork;

public class SettingNetwork extends StartLayerizedNetwork {

    // LinkLayerizerBoundary ID
    protected static final String BOUNDARY_ID_01 = "boundary01";
    protected static final String BOUNDARY_ID_02 = "boundary02";

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
        NetworkInterface layerizedNwIf =
                new NetworkInterface(dispatcher, LAYERIZED_NW_ID);
        NetworkInterface upperNwIf =
                new NetworkInterface(dispatcher, UPPER_NW_ID);
        NetworkInterface lowerNwIf =
                new NetworkInterface(dispatcher, LOWER_NW_ID);

        Response rsp = null;
        int n = 0;    
                
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + UPPER_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> upperPorts1 = new HashMap<String, Port>();
        Map<String, Port> upperPorts2 = new HashMap<String, Port>();
        Map<String, Port> upperPorts3 = new HashMap<String, Port>();
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node upperNode1 = new Node(
                "0", "node001", upperPorts1, new HashMap<String, String>());
        Node upperNode2 = new Node(
                "0", "node002", upperPorts2, new HashMap<String, String>());
        Node upperNode3 = new Node(
                "0", "node003", upperPorts3, new HashMap<String, String>());
        // PUT node1
        rsp = upperNwIf.putNode(upperNode1);
        dumpResponse("  -PUT Node. ", rsp);
        // PUT node2
        rsp = upperNwIf.putNode(upperNode2);
        dumpResponse("  -PUT Node. ", rsp);
        // PUT node3
        rsp = upperNwIf.putNode(upperNode3);
        dumpResponse("  -PUT Node. ", rsp);
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port upperPort0011 = new Port("0", "port0011", upperNode1.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0012 = new Port("0", "port0012", upperNode1.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0013 = new Port("0", "port0013", upperNode1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port upperPort0021 = new Port("0", "port0021", upperNode2.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0022 = new Port("0", "port0022", upperNode2.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0023 = new Port("0", "port0023", upperNode2.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0024 = new Port("0", "port0024", upperNode2.getId(),
                "", "", new HashMap<String, String>());
        // node3's ports
        Port upperPort0031 = new Port("0", "port0031", upperNode3.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0032 = new Port("0", "port0032", upperNode3.getId(),
                "", "", new HashMap<String, String>());
        Port upperPort0033 = new Port("0", "port0033", upperNode3.getId(),
                "", "", new HashMap<String, String>());
        wait(WAIT_TIME);
        // PUT node1's ports
        rsp = upperNwIf.putPort(upperPort0011);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0012);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0013);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node2's ports
        rsp = upperNwIf.putPort(upperPort0021);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0022);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0023);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0024);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node3's ports
        rsp = upperNwIf.putPort(upperPort0031);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0032);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = upperNwIf.putPort(upperPort0033);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME * 2);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link upperLink1 = new Link("0", "link0012",
                upperPort0012.getNode(), upperPort0012.getId(), // src
                upperPort0021.getNode(), upperPort0021.getId(), // dst
                new HashMap<String, String>());
        Link upperLink2 = new Link("0", "link0021",
                upperPort0021.getNode(), upperPort0021.getId(), // src
                upperPort0012.getNode(), upperPort0012.getId(), // dst
                new HashMap<String, String>());
        rsp = upperNwIf.putLink(upperLink1);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        rsp = upperNwIf.putLink(upperLink2);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);    
       
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + LOWER_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> lowerPorts1 = new HashMap<String, Port>();
        Map<String, Port> lowerPorts2 = new HashMap<String, Port>();
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node lowerNode1 = new Node(
                "0", "node011", lowerPorts1, new HashMap<String, String>());
        Node lowerNode2 = new Node(
                "0", "node012", lowerPorts2, new HashMap<String, String>());
        // PUT node1
        rsp = lowerNwIf.putNode(lowerNode1);
        dumpResponse("  -PUT Node. ", rsp);
        // PUT node2
        rsp = lowerNwIf.putNode(lowerNode2);
        wait(WAIT_TIME);
        dumpResponse("  -PUT Node. ", rsp);
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port lowerPort0111 = new Port("0", "port0111", lowerNode1.getId(),
                "", "", new HashMap<String, String>());
        Port lowerPort0112 = new Port("0", "port0112", lowerNode1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port lowerPort0121 = new Port("0", "port0121", lowerNode2.getId(),
                "", "", new HashMap<String, String>());
        Port lowerPort0122 = new Port("0", "port0122", lowerNode2.getId(),
                "", "", new HashMap<String, String>());
        wait(WAIT_TIME);
        // PUT node1's ports
        rsp = lowerNwIf.putPort(lowerPort0111);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = lowerNwIf.putPort(lowerPort0112);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node2's ports
        rsp = lowerNwIf.putPort(lowerPort0121);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = lowerNwIf.putPort(lowerPort0122);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link lowerLink1 = new Link("0", "link0112",
                lowerPort0112.getNode(), lowerPort0112.getId(), // src
                lowerPort0121.getNode(), lowerPort0121.getId(), // dst
                new HashMap<String, String>());
        Link lowerLink2 = new Link("0", "link0121",
                lowerPort0121.getNode(), lowerPort0121.getId(), // src
                lowerPort0112.getNode(), lowerPort0112.getId(), // dst
                new HashMap<String, String>());
        rsp = lowerNwIf.putLink(lowerLink1);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        rsp = lowerNwIf.putLink(lowerLink2);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);  
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set Boundary.");
        outMsg("//////////////////////////////////////////////////");
        String path = String.format(
                "components/%s/settings/boundaries/%s",
                LINK_LAYERIZER_ID, BOUNDARY_ID_01);
        LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
                BOUNDARY_ID_01, LINK_LAYERIZER,
                LOWER_NW_ID, lowerPort0111.getNode(), lowerPort0111.getId(),
                UPPER_NW_ID, upperPort0022.getNode(), upperPort0022.getId());
        rsp = systemMngInterface.putObjectToSystemMng(path, boundary);
        dumpResponse(" -PUT Boundary", rsp);
        path = String.format(
                "components/%s/settings/boundaries/%s",
                LINK_LAYERIZER_ID, BOUNDARY_ID_02);
        boundary = new LinkLayerizerBoundary(
                BOUNDARY_ID_02, LINK_LAYERIZER,
                LOWER_NW_ID, lowerPort0122.getNode(), lowerPort0122.getId(),
                UPPER_NW_ID, upperPort0031.getNode(), upperPort0031.getId());
        rsp = systemMngInterface.putObjectToSystemMng(path, boundary);
        dumpResponse(" -PUT Boundary", rsp);
  
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Get Boundary.");
        outMsg("//////////////////////////////////////////////////");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        dumpResponse(" -GET Boundary.", rsp);
   
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set Flow to lower-nw (upperLinkSync:true).");
        outMsg("//////////////////////////////////////////////////");
        // match1
        OFPFlowMatch match1 = new OFPFlowMatch();
        match1.setInNode(lowerPort0111.getNode());
        match1.setInPort(lowerPort0111.getId());

        List<BasicFlowMatch> matches1 = new ArrayList<>();
        matches1.add(match1);

        // action1
        FlowAction action1 = new FlowActionOutput(lowerPort0122.getId());
        List<FlowAction> actionsList1 = new ArrayList<>();
        actionsList1.add(action1);
        Map<String, List<FlowAction>> actions1 = new HashMap<>();
        actions1.put(lowerPort0122.getNode(), actionsList1);

        List<String> paths1 = new ArrayList<>();
        paths1.add(lowerLink1.getId());
       
        OFPFlow ofpFlow01 = new OFPFlow(
                "0", "flow01", "logic1", true, "65535",
                FlowObject.FlowStatus.ESTABLISHING.toString(),
                matches1, new Long(60), new Long(60),
                paths1, actions1, new HashMap<String, String>());
        rsp = lowerNwIf.putFlow(ofpFlow01);
        dumpResponse("  -PUT Flow. ", rsp);
        wait(WAIT_TIME * 2);
       
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set upperLinkSync:false.");
        outMsg("//////////////////////////////////////////////////");
        path = String.format(
                "components/%s/settings/upper_link_sync", LINK_LAYERIZER_ID);
        Map<String, Boolean> syncBody = new HashMap<>();
        syncBody.put("sync", false);
        rsp = systemMngInterface.putObjectToSystemMng(path, syncBody);
        dumpResponse(" -PUT upperLinkSync", rsp);
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set Flow to lower-nw (upperLinkSync:false).");
        outMsg("//////////////////////////////////////////////////");
        // match2
        OFPFlowMatch match2 = new OFPFlowMatch();
        match2.setInNode(lowerPort0122.getNode());
        match2.setInPort(lowerPort0122.getId());

        List<BasicFlowMatch> matches2 = new ArrayList<>();
        matches2.add(match2);

        // action2
        FlowAction action2 = new FlowActionOutput(lowerPort0111.getId());
        List<FlowAction> actionsList2 = new ArrayList<>();
        actionsList2.add(action2);
        Map<String, List<FlowAction>> actions2 = new HashMap<>();
        actions2.put(lowerPort0111.getNode(), actionsList2);

        List<String> paths2 = new ArrayList<>();
        paths2.add(lowerLink2.getId());
       
        OFPFlow ofpFlow02 = new OFPFlow(
                "0", "flow02", "logic2", true, "65535",
                FlowObject.FlowStatus.ESTABLISHING.toString(),
                matches2, new Long(60), new Long(60),
                paths2, actions2, new HashMap<String, String>());
        rsp = lowerNwIf.putFlow(ofpFlow02);
        dumpResponse("  -PUT Flow. ", rsp);
        wait(WAIT_TIME * 2);
        
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + LINK_LAYERIZER_ID + "/"
                + UPPER_NW_ID + "/" + LOWER_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        // layerized
        DumpNetwork.dumpTopology(layerizedNwIf);
        wait(WAIT_TIME);
        // upper 
        DumpNetwork.dumpTopology(upperNwIf);
        wait(WAIT_TIME);
        // lower 
        DumpNetwork.dumpTopology(lowerNwIf);
        outMsg("");
       
        outMsg("");
        wait(WAIT_TIME * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") POST OFPInPacket [case1] to "
                        + UPPER_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");

        // [1] InPacket01 (upper-nw --> layerized-nw)
        byte[] data = {};
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OFPFlowMatch header = new OFPFlowMatch();
        header.setInNode(upperPort0011.getNode());
        header.setInPort(upperPort0011.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        InPacket inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = upperNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [2] InPacket02 (original01 --> federated)
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(upperPort0032.getNode());
        header.setInPort(upperPort0032.getId());
        header.setEthDst("ff:ff:ff:ff:ff:bb");
        header.setEthSrc("ff:ff:ff:ff:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = upperNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [3] InPacket03 (original01 --> federated)
        try {
            data = "OFPInpacket Data3.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(upperPort0011.getNode());
        header.setInPort(upperPort0011.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = upperNwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);
        
        outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + LAYERIZED_NW_ID + "'s In/OutPackets."
                + UPPER_NW_ID + "'s In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        DumpNetwork.dumpPackets(layerizedNwIf);
        wait(WAIT_TIME);
        DumpNetwork.dumpPackets(upperNwIf);
      
         outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(layerizedNwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(upperNwIf);
        outMsg("");
        
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + UPPER_NW_ID + "'s Topology(link).");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//////// (1)-3. Set Links.");
        Link upperLink3 = new Link("0", "link0031",
                upperPort0031.getNode(), upperPort0031.getId(), // src
                upperPort0022.getNode(), upperPort0022.getId(), // dst
                new HashMap<String, String>());
        rsp = upperNwIf.putLink(upperLink3);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + LINK_LAYERIZER_ID + "/"
                + UPPER_NW_ID + "/" + LOWER_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        // layerized
        DumpNetwork.dumpTopology(layerizedNwIf);
        wait(WAIT_TIME);
        // upper 
        DumpNetwork.dumpTopology(upperNwIf);
        wait(WAIT_TIME);
        // lower 
        DumpNetwork.dumpTopology(lowerNwIf);
        outMsg("");
      
         outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(layerizedNwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(upperNwIf);
        outMsg("");
        
        dispatcher.close();
        System.exit(0);
        
    }

}
