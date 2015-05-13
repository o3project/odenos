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

package federated_network;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.o3project.odenos.component.federator.FederatorBoundary;
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
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.Port;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import simple_controller.DumpNetwork;

public class SettingNetwork extends StartFederatedNetwork {

    // FederatorBoundary ID
    protected static final String BOUNDARY_ID = "federator_boundary1";
    
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
        NetworkInterface org01NwIf =
                new NetworkInterface(dispatcher, ORIGINAL_01_NW_ID);
        NetworkInterface org02NwIf =
                new NetworkInterface(dispatcher, ORIGINAL_02_NW_ID);
        NetworkInterface fedNwIf =
                new NetworkInterface(dispatcher, FEDERATED_NW_ID);

        Response rsp = null;
        int n = 0;
       
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + ORIGINAL_01_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> org01Ports1 = new HashMap<String, Port>();
        Map<String, Port> org01Ports2 = new HashMap<String, Port>();
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node org01Node1 = new Node(
                "0", "node001", org01Ports1, new HashMap<String, String>());
        Node org01Node2 = new Node(
                "0", "node002", org01Ports2, new HashMap<String, String>());
        // PUT node1
        rsp = org01NwIf.putNode(org01Node1);
        dumpResponse("  -PUT Node. ", rsp);
        // PUT node2
        rsp = org01NwIf.putNode(org01Node2);
        wait(WAIT_TIME);
        dumpResponse("  -PUT Node. ", rsp);
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port org01Port0011 = new Port("0", "port0011", org01Node1.getId(),
                "", "", new HashMap<String, String>());
        Port org01Port0012 = new Port("0", "port0012", org01Node1.getId(),
                "", "", new HashMap<String, String>());
        Port org01Port0013 = new Port("0", "port0013", org01Node1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port org01Port0021 = new Port("0", "port0021", org01Node2.getId(),
                "", "", new HashMap<String, String>());
        Port org01Port0022 = new Port("0", "port0022", org01Node2.getId(),
                "", "", new HashMap<String, String>());
        Port org01Port0023 = new Port("0", "port0023", org01Node2.getId(),
                "", "", new HashMap<String, String>());
        Port org01Port0024 = new Port("0", "port0024", org01Node2.getId(),
                "", "", new HashMap<String, String>());
        wait(WAIT_TIME);
        // PUT node1's ports
        rsp = org01NwIf.putPort(org01Port0011);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org01NwIf.putPort(org01Port0012);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org01NwIf.putPort(org01Port0013);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node2's ports
        rsp = org01NwIf.putPort(org01Port0021);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org01NwIf.putPort(org01Port0022);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org01NwIf.putPort(org01Port0023);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org01NwIf.putPort(org01Port0024);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link org01Link1 = new Link("0", "link0012",
                org01Port0012.getNode(), org01Port0012.getId(), // src
                org01Port0021.getNode(), org01Port0021.getId(), // dst
                new HashMap<String, String>());
        Link org01Link2 = new Link("0", "link0021",
                org01Port0021.getNode(), org01Port0021.getId(), // src
                org01Port0012.getNode(), org01Port0012.getId(), // dst
                new HashMap<String, String>());
        rsp = org01NwIf.putLink(org01Link1);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        rsp = org01NwIf.putLink(org01Link2);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);      
        
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set " + ORIGINAL_01_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        Map<String, Port> org02Ports1 = new HashMap<String, Port>();
        Map<String, Port> org02Ports2 = new HashMap<String, Port>();
        // //////////////////
        // Set Nodes
        // //////////////////
        outMsg("//////// (1)-1. Set Nodes.");
        Node org02Node1 = new Node(
                "0", "node001", org02Ports1, new HashMap<String, String>());
        Node org02Node2 = new Node(
                "0", "node002", org02Ports2, new HashMap<String, String>());
        // PUT node1
        rsp = org02NwIf.putNode(org02Node1);
        dumpResponse("  -PUT Node. ", rsp);
        // PUT node2
        rsp = org02NwIf.putNode(org02Node2);
        wait(WAIT_TIME);
        dumpResponse("  -PUT Node. ", rsp);
        wait(WAIT_TIME);
        // //////////////////
        // Set Ports
        // //////////////////
        outMsg("//////// (1)-2. Set Ports.");
        // node1's ports
        Port org02Port0011 = new Port("0", "port0011", org02Node1.getId(),
                "", "", new HashMap<String, String>());
        Port org02Port0012 = new Port("0", "port0012", org02Node1.getId(),
                "", "", new HashMap<String, String>());
        Port org02Port0013 = new Port("0", "port0013", org02Node1.getId(),
                "", "", new HashMap<String, String>());
        Port org02Port0014 = new Port("0", "port0014", org02Node1.getId(),
                "", "", new HashMap<String, String>());
        // node2's ports
        Port org02Port0021 = new Port("0", "port0021", org02Node2.getId(),
                "", "", new HashMap<String, String>());
        Port org02Port0022 = new Port("0", "port0022", org02Node2.getId(),
                "", "", new HashMap<String, String>());
        Port org02Port0023 = new Port("0", "port0023", org02Node2.getId(),
                "", "", new HashMap<String, String>());
        wait(WAIT_TIME);
        // PUT node1's ports
        rsp = org02NwIf.putPort(org02Port0011);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org02NwIf.putPort(org02Port0012);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org02NwIf.putPort(org02Port0013);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org02NwIf.putPort(org02Port0014);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        // PUT node2's ports
        rsp = org02NwIf.putPort(org02Port0021);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org02NwIf.putPort(org02Port0022);
        dumpResponse("  -PUT Port. ", rsp);
        rsp = org02NwIf.putPort(org02Port0023);
        dumpResponse("  -PUT Port. ", rsp);
        wait(WAIT_TIME);
        wait(WAIT_TIME);
        // //////////////////
        // Set Links
        // //////////////////
        outMsg("//////// (1)-3. Set Links.");
        Link org02Link1 = new Link("0", "link0012",
                org02Port0012.getNode(), org02Port0012.getId(), // src
                org02Port0021.getNode(), org02Port0021.getId(), // dst
                new HashMap<String, String>());
        Link org02Link2 = new Link("0", "link0021",
                org02Port0021.getNode(), org02Port0021.getId(), // src
                org02Port0012.getNode(), org02Port0012.getId(), // dst
                new HashMap<String, String>());
        rsp = org02NwIf.putLink(org02Link1);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);
        rsp = org02NwIf.putLink(org02Link2);
        dumpResponse("  -PUT Link. ", rsp);
        wait(WAIT_TIME);  
               
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set FederatorBoundary.");
        outMsg("//////////////////////////////////////////////////");
        String path = String.format(
                "components/%s/settings/boundaries", FEDERATOR_ID);
        FederatorBoundary boundary = new FederatorBoundary(
                BOUNDARY_ID, FEDERATOR,
                ORIGINAL_01_NW_ID,
                org01Port0022.getNode(),
                org01Port0022.getId(), 
                ORIGINAL_02_NW_ID,
                org02Port0011.getNode(),
                org02Port0011.getId());
        rsp = systemMngInterface.postObjectToSystemMng(path, boundary);
        dumpResponse(" -POST Boundary", rsp);
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Get FederatorBoundary.");
        outMsg("//////////////////////////////////////////////////");
        rsp = systemMngInterface.getObjectToSystemMng(path);
        dumpResponse(" -GET FederatorBoundary.", rsp);
        
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + ORIGINAL_01_NW_ID + "/"
                + ORIGINAL_02_NW_ID + "/" + FEDERATED_NW_ID + "'s Topology.");
        outMsg("//////////////////////////////////////////////////");
        // original01 
        DumpNetwork.dumpTopology(org02NwIf);
        // original02
        DumpNetwork.dumpTopology(org02NwIf);
        // federated 
        DumpNetwork.dumpTopology(fedNwIf);
        outMsg("");
        
        
        outMsg("");
        wait(WAIT_TIME * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") POST OFPInPacket [case1] to " + ORIGINAL_01_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");

        // [1] InPacket01 (original01 --> federated)
        byte[] data = {};
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OFPFlowMatch header = new OFPFlowMatch();
        header.setInNode(org01Port0011.getNode());
        header.setInPort(org01Port0011.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        InPacket inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org01NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [2] InPacket02 (original01 --> federated)
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(org01Port0024.getNode());
        header.setInPort(org01Port0024.getId());
        header.setEthDst("ff:ff:ff:ff:ff:bb");
        header.setEthSrc("ff:ff:ff:ff:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org01NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [3] InPacket03 (original01 --> federated)
        try {
            data = "OFPInpacket Data3.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(org01Port0011.getNode());
        header.setInPort(org01Port0011.getId());
        header.setEthDst("ff:ff:ff:ff:ff:aa");
        header.setEthSrc("ff:ff:ff:ff:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org01NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);
       
        // [4] InPacket01 (original02 --> federated)
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") POST OFPInPacket [case2] to " + ORIGINAL_02_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header.setInNode(org02Port0014.getNode());
        header.setInPort(org02Port0014.getId());
        header.setEthDst("ff:00:ff:00:ff:aa");
        header.setEthSrc("ff:00:ff:00:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org02NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [5] InPacket02 (original02 --> federated)
        try {
            data = "OFPInpacket Data2.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(org02Port0013.getNode());
        header.setInPort(org02Port0013.getId());
        header.setEthDst("ff:00:ff:00:ff:bb");
        header.setEthSrc("ff:00:ff:00:ff:aa");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org02NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);

        // [6] InPacket03 (original02 --> federated)
        try {
            data = "OFPInpacket Data.".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        header = new OFPFlowMatch();
        header.setInNode(org02Port0014.getNode());
        header.setInPort(org02Port0014.getId());
        header.setEthDst("ff:00:ff:00:ff:aa");
        header.setEthSrc("ff:00:ff:00:ff:bb");
        inPacket = new OFPInPacket(
                "inpacket1", header.getInNode(), header.getInPort(),
                1, header, data, new HashMap<String, String>());
        rsp = org02NwIf.postInPacket(inPacket);
        dumpResponse("  -POST InPacket. ", rsp);
        wait(WAIT_TIME);
        
        outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump " + ORIGINAL_01_NW_ID + "'s In/OutPackets.");
        outMsg("////          and " + ORIGINAL_02_NW_ID + "/"
                                + FEDERATED_NW_ID + "'s In/OutPackets.");
        outMsg("//////////////////////////////////////////////////");
        DumpNetwork.dumpPackets(org01NwIf);
        wait(WAIT_TIME);
        DumpNetwork.dumpPackets(org01NwIf);
        wait(WAIT_TIME);
        DumpNetwork.dumpPackets(fedNwIf);
       
        outMsg("");
        wait(WAIT_TIME);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Set Flow.");
        outMsg("//////////////////////////////////////////////////");
        // match1
        BasicFlowMatch match1 =
                new OFPFlowMatch();
        match1.setInNode(String.format("%s_%s", 
                ORIGINAL_01_NW_ID, org01Port0011.getNode()));
        match1.setInPort(String.format("%s_%s_%s",
                ORIGINAL_01_NW_ID, org01Port0011.getNode(), org01Port0011.getId()));

        // match2
        BasicFlowMatch match2 =
                new OFPFlowMatch();
        match2.setInNode(String.format("%s_%s", 
                ORIGINAL_02_NW_ID, org02Port0022.getNode()));
        match2.setInPort(String.format("%s_%s_%s",
                ORIGINAL_02_NW_ID, org02Port0022.getNode(), org02Port0022.getId()));

        // action1
        List<FlowAction> actionsList1 = new ArrayList<FlowAction>();
        FlowAction action1
          = new FlowActionOutput(String.format("%s_%s_%s", ORIGINAL_02_NW_ID, org02Port0022.getNode(), org02Port0022.getId()));
        actionsList1.add(action1);
        Map<String, List<FlowAction>> actions1 = new HashMap<String, List<FlowAction>>();
        String outNode1 = String.format("%s_%s", ORIGINAL_02_NW_ID, org02Port0022.getNode());
        actions1.put(outNode1, actionsList1);
        // action2
        List<FlowAction> actionsList2 = new ArrayList<FlowAction>();
        FlowAction action2
          = new FlowActionOutput(String.format("%s_%s_%s", ORIGINAL_01_NW_ID, org01Port0011.getNode(), org01Port0011.getId()));
        actionsList2.add(action2);
        Map<String, List<FlowAction>> actions2 =
                new HashMap<String, List<FlowAction>>();
        String outNode2 = String.format("%s_%s", ORIGINAL_01_NW_ID, org01Port0011.getNode());
        actions2.put(outNode2, actionsList2);

        List<BasicFlowMatch> matches1 = new ArrayList<BasicFlowMatch>();
        List<BasicFlowMatch> matches2 = new ArrayList<BasicFlowMatch>();
        matches1.add(match1);
        matches2.add(match2);
        
        List<String> paths1 = new ArrayList<>();
        paths1.add(String.format("%s_%s", ORIGINAL_01_NW_ID, org01Link1.getId()));
        paths1.add(String.format("%s_link01", BOUNDARY_ID));
        paths1.add(String.format("%s_%s", ORIGINAL_02_NW_ID, org02Link1.getId()));
        List<String> paths2 = new ArrayList<>();
        paths2.add(String.format("%s_%s", ORIGINAL_02_NW_ID, org02Link2.getId()));
        paths2.add(String.format("%s_link02", BOUNDARY_ID));
        paths2.add(String.format("%s_%s", ORIGINAL_01_NW_ID, org01Link2.getId()));
        
        Flow ofpFlow1 = new OFPFlow(
                "0", "flow01", "logic1", true, "65535",
                "none", matches1, new Long(60), new Long(60),
                paths1, actions1, new HashMap<String, String>());
        Flow ofpFlow2 = new OFPFlow(
                "0", "flow02", "logic2", true, "65535",
                "none", matches2, new Long(60), new Long(60),
                paths2, actions2, new HashMap<String, String>());
        rsp = fedNwIf.putFlow(ofpFlow1);
        dumpResponse("  -PUT Flow. ", rsp);
        wait(WAIT_TIME);
        rsp = fedNwIf.putFlow(ofpFlow2);
        dumpResponse("  -PUT Flow. ", rsp);
        
        outMsg("");
        wait(WAIT_TIME  * 2);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Dump OFPFlow.");
        outMsg("//////////////////////////////////////////////////");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(org01NwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(org02NwIf);
        outMsg("");
        wait(WAIT_TIME);
        DumpNetwork.dumpOFPFlows(fedNwIf);
        outMsg("");
        
        dispatcher.close();
        System.exit(0);
    }
}
