# -*- coding:utf-8 -*-

# Copyright 2015 NEC Corporation.                                          #
#                                                                          #
# Licensed under the Apache License, Version 2.0 (the "License");          #
# you may not use this file except in compliance with the License.         #
# You may obtain a copy of the License at                                  #
#                                                                          #
#   http://www.apache.org/licenses/LICENSE-2.0                             #
#                                                                          #
# Unless required by applicable law or agreed to in writing, software      #
# distributed under the License is distributed on an "AS IS" BASIS,        #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
# See the License for the specific language governing permissions and      #
# limitations under the License.                                           #

import logging

from org.o3project.odenos.core.util.remote_object_interface import (
    RemoteObjectInterface
)
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response

from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.topology import Topology
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.flow.basic.basic_flow import (
    BasicFlow
)
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow import (
    OFPFlow
)
from org.o3project.odenos.core.component.network.flow.flow_set import FlowSet
from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.component.network.packet.in_packet import InPacket
from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket
from org.o3project.odenos.core.component.network.packet.ofp_in_packet import (
    OFPInPacket
)
from org.o3project.odenos.core.component.network.packet.ofp_out_packet import (
    OFPOutPacket
)
from org.o3project.odenos.core.component.network.packet.packet_status import (
    PacketStatus
)


# pylint: disable=R0923,R0904
class NetworkInterface(RemoteObjectInterface):
    # Topology
    TOPOLOGY_PATH = "topology"
    # Node
    NODE_PATH = "topology/nodes/%s"
    NODES_PATH = "topology/nodes"
    PHYSICAL_NODES_PATH = "topology/physical_nodes/%s"
    # Port
    PORT_PATH = "topology/nodes/%s/ports/%s"
    PORTS_PATH = "topology/nodes/%s/ports"
    PHYSICAL_PORTS_PATH = "topology/physical_ports/%s"
    # Link
    LINK_PATH = "topology/links/%s"
    LINKS_PATH = "topology/links"
    # Flow
    FLOW_PATH = "flows/%s"
    FLOWS_PATH = "flows"
    # Packet
    INPACKET_PATH = "packets/in/%s"
    INPACKETS_PATH = "packets/in"
    INPACKETS_HEAD_PATH = "packets/in/head"
    OUTPACKET_PATH = "packets/out/%s"
    OUTPACKETS_PATH = "packets/out"
    OUTPACKETS_HEAD_PATH = "packets/out/head"
    PACKETS_PATH = "packets"

    def __init__(self, dispatcher, network_id, source_object_id=None):
        '''
        NOTE: source_object_id is required for the ODENOS monitor tool.
        '''
        logging.debug("Create NetworkInterface : NetworkID:" + network_id)
        super(NetworkInterface, self).__init__(dispatcher, network_id, source_object_id)

    @property
    def network_id(self):
        return self.object_id

    ###################################
    # Basic request
    ###################################
    # GET Topology.
    def get_topology(self):
        logging.debug("GET Topology NetworkID:" + self.network_id)
        resp = self._get_object_to_remote_object(self.TOPOLOGY_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        topology = None
        try:
            topology = Topology.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Topology Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return topology

    # PUT Topology.
    def put_topology(self, topology):
        logging.debug("PUT Topology NetworkID:" + self.network_id)
        path = self.TOPOLOGY_PATH
        return self._put_object_to_remote_object(path, topology)

    # POST Node.
    def post_node(self, node):
        logging.debug("POST Node NetworkID:" + self.network_id)
        path = self.NODES_PATH
        return self._post_object_to_remote_object(path, node)

    # GET Nodes.
    def get_nodes(self):
        logging.debug("GET Nodes NetworkID:" + self.network_id)
        resp = self._get_object_to_remote_object(self.NODES_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        nodes = {}
        try:
            for node_id, node in resp.body.items():
                nodes[node_id] = Node.create_from_packed(node)
        except KeyError, err:
            logging.error("GET Nodes Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return nodes

    # GET Node.
    def get_node(self, node_id):
        logging.debug("GET Node NetworkID:" + self.network_id +
                      " NodeID:" + node_id)
        path = self.NODE_PATH % node_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        node = None
        try:
            node = Node.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Node Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return node

    # PUT Node.
    def put_node(self, node):
        logging.debug("PUT Node NetworkID:" + self.network_id +
                      " NodeID:" + node.node_id)
        path = self.NODE_PATH % node.node_id
        return self._put_object_to_remote_object(path, node)

    # DELETE Node.
    def del_node(self, node_id):
        logging.debug("DELETE Node NetworkID:" + self.network_id +
                      " NodeID:" + node_id)
        path = self.NODE_PATH % node_id
        return self._del_object_to_remote_object(path)

    # GET PhysicalNode.
    def get_physical_node(self, physical_id):
        logging.debug("GET PhysicalNode NetworkID:" + self.network_id +
                      " PhysicalID:" + physical_id)
        path = self.PHYSICAL_NODES_PATH % physical_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        node = None
        try:
            node = Node.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET PhysicalNode Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return node

    # PUT PhysicalNode.
    def put_physical_node(self, node):
        logging.debug("PUT PhysicalNode NetworkID:" + self.network_id +
                      " PhysicalID:" + node.attributes["physical_id"])
        path = self.PHYSICAL_NODES_PATH % node.attributes["physical_id"]
        return self._put_object_to_remote_object(path, node)

    # DELETE PhysicalNode.
    def del_physical_node(self, physical_id):
        logging.debug("DELETE PhysicalNode NetworkID:" + self.network_id +
                      " PhysicalID:" + physical_id)
        path = self.PHYSICAL_NODES_PATH % physical_id
        return self._del_object_to_remote_object(path)

    # POST Port.
    def post_port(self, port):
        logging.debug("POST Port NetworkID:" + self.network_id)
        path = self.PORTS_PATH % port.node_id
        return self._post_object_to_remote_object(path, port)

    # GET Ports.
    def get_ports(self, node_id):
        logging.debug("GET Ports NetworkID:" + self.network_id +
                      " NodeID:" + node_id)
        path = self.PORTS_PATH % node_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        ports = {}
        try:
            for port_id, port in resp.body.items():
                ports[port_id] = Port.create_from_packed(port)
        except KeyError, err:
            logging.error("GET Ports Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return ports

    # GET Port.
    def get_port(self, node_id, port_id):
        logging.debug("GET Port NetworkID:" + self.network_id +
                      " NodeID:" + node_id + " PortID:" + port_id)
        path = self.PORT_PATH % (node_id, port_id)
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        port = None
        try:
            port = Port.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Port Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return port

    # PUT Port.
    def put_port(self, port):
        logging.debug("PUT Port NetworkID:" + self.network_id +
                      " NodeID:" + port.node_id +
                      " PortID:" + port.port_id)
        path = self.PORT_PATH % (port.node_id, port.port_id)
        return self._put_object_to_remote_object(path, port)

    # DELETE Port.
    def del_port(self, node_id, port_id):
        logging.debug("DELETE Port NetworkID:" + self.network_id +
                      " NodeID:" + node_id + " PortID:" + port_id)
        path = self.PORT_PATH % (node_id, port_id)
        return self._del_object_to_remote_object(path)

    # GET PhysicalPort.
    def get_physical_port(self, physical_id):
        logging.debug("GET PhysicalPort NetworkID:" + self.network_id +
                      " PhysicalID:" + physical_id)
        path = self.PHYSICAL_PORTS_PATH % physical_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        port = None
        try:
            port = Port.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET PhysicalPort Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return port

    # PUT PhysicalPort.
    def put_physical_port(self, port):
        logging.debug("PUT PhysicalPort NetworkID:" + self.network_id +
                      " PhysicalID:" + port.attributes["physical_id"])
        path = self.PHYSICAL_PORTS_PATH % port.attributes["physical_id"]
        return self._put_object_to_remote_object(path, port)

    # DELETE PhysicalPort.
    def del_physical_port(self, physical_id):
        logging.debug("DELETE PhysicalPort NetworkID:" + self.network_id +
                      " PhysicalID:" + physical_id)
        path = self.PHYSICAL_PORTS_PATH % physical_id
        return self._del_object_to_remote_object(path)

    # POST Link.
    def post_link(self, link):
        logging.debug("POST Link NetworkID:" + self.network_id)
        path = self.LINKS_PATH
        return self._post_object_to_remote_object(path, link)

    # GET Links.
    def get_links(self):
        logging.debug("GET Links NetworkID:" + self.network_id)
        resp = self._get_object_to_remote_object(self.LINKS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        links = {}
        try:
            for link_id, link in resp.body.items():
                links[link_id] = Link.create_from_packed(link)
        except KeyError, err:
            logging.error("GET Links Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return links

    # GET Link.
    def get_link(self, link_id):
        logging.debug("GET Link NetworkID:" + self.network_id +
                      " LinkID:" + link_id)
        path = self.LINK_PATH % link_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        link = None
        try:
            link = Link.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Link Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return link

    # PUT Link.
    def put_link(self, link):
        logging.debug("PUT Link NetworkID:" + self.network_id +
                      " LinkID:" + link.link_id)
        path = self.LINK_PATH % link.link_id
        return self._put_object_to_remote_object(path, link)

    # DELETE Link.
    def del_link(self, link_id):
        logging.debug("DELETE Link NetworkID:" + self.network_id +
                      " LinkID:" + link_id)
        path = self.LINK_PATH % link_id
        return self._del_object_to_remote_object(path)

    # POST Flow.
    def post_flow(self, flow):
        logging.debug("POST Flow NetworkID:" + self.network_id)
        path = self.FLOWS_PATH
        return self._post_object_to_remote_object(path, flow)

    # GET FlowSet.
    def get_flow_set(self):
        logging.debug("GET Flows NetworkID:" + self.network_id)
        resp = self._get_object_to_remote_object(self.FLOWS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        flow_set = None
        try:
            flow_set = FlowSet.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET FlowSet Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return flow_set

    # GET Flow.
    def get_flow(self, flow_id):
        logging.debug("GET Flow NetworkID:" + self.network_id +
                      " FlowID:" + flow_id)
        path = self.FLOW_PATH % flow_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        flow = None
        try:
            flow = globals()[resp.body[Flow.TYPE]].\
                create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Flow Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return flow

    # PUT Flow.
    def put_flow(self, flow):
        logging.debug("PUT Flow NetworkID:" + self.network_id +
                      " FlowID:" + flow.flow_id)
        path = self.FLOW_PATH % flow.flow_id
        return self._put_object_to_remote_object(path, flow)

    # DELETE Flow.
    def del_flow(self, flow_id):
        logging.debug("DELETE Flow NetworkID:" + self.network_id +
                      " FlowID:" + flow_id)

        flow = self.get_flow(flow_id)
        if flow is None:
            return Response(Response.StatusCode.OK, None)

        flow.enabled = False
        path = self.FLOW_PATH % flow_id
        return self._del_object_to_remote_object(path, body=flow)

    # GET Packets.
    def get_packets(self):
        logging.debug("GET Packets NetworkID:" + self.network_id)
        resp = self._get_object_to_remote_object(self.PACKETS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        packet = None
        try:
            packet = PacketStatus.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Packet Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return packet

    # POST InPacket.
    def post_in_packet(self, in_packet):
        logging.debug("POST InPacket NetworkID:" + self.network_id)
        return self._post_object_to_remote_object(self.INPACKETS_PATH,
                                                  in_packet)

    # GET InPackets.
    def get_in_packets(self):
        logging.debug("GET InPackets NetworkID:" + self.network_id)
        path = self.INPACKETS_PATH
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        in_packet = None
        try:
            in_packet = PacketStatus.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET InPackets Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return in_packet

    # DELETE InPackets.
    def del_in_packets(self):
        logging.debug("DELETE InPackets NetworkID:" + self.network_id)
        path = self.INPACKETS_PATH
        return self._del_object_to_remote_object(path)

    # GET InPacketHead.
    def get_in_packet_head(self):
        logging.debug("GET InPacketHead NetworkID:" + self.network_id)
        path = self.INPACKETS_HEAD_PATH
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        in_packet = None
        try:
            in_packet = globals()[resp.body[Packet.TYPE]].\
                create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET InPacketHead Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return in_packet

    # DELETE InPacketHead.
    def del_in_packet_head(self):
        logging.debug("DELETE InPacketHead NetworkID:" + self.network_id)
        path = self.INPACKETS_HEAD_PATH
        return self._del_object_to_remote_object(path)

    # GET InPacket.
    def get_in_packet(self, packet_id):
        logging.debug("GET InPacket NetworkID:" + self.network_id +
                      " ID:" + packet_id)
        path = self.INPACKET_PATH % packet_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        in_packet = None
        try:
            in_packet = globals()[resp.body[Packet.TYPE]].\
                create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET InPacket Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return in_packet

    # DELETE InPacket.
    def del_in_packet(self, packet_id):
        logging.debug("DELETE InPacket NetworkID:" + self.network_id +
                      " ID:" + packet_id)
        path = self.INPACKET_PATH % packet_id
        return self._del_object_to_remote_object(path)

    # POST OutPacket.
    def post_out_packet(self, out_packet):
        logging.debug("POST OutPacket NetworkID:" + self.network_id)
        return self._post_object_to_remote_object(self.OUTPACKETS_PATH,
                                                  out_packet)

    # GET OutPackets.
    def get_out_packets(self):
        logging.debug("GET OutPackets NetworkID:" + self.network_id)
        path = self.OUTPACKETS_PATH
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        out_packet = None
        try:
            out_packet = PacketStatus.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET OutnPackets Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return out_packet

    # DELETE OutPackets.
    def del_out_packets(self):
        logging.debug("DELETE OutPackets NetworkID:" + self.network_id)
        path = self.OUTPACKETS_PATH
        return self._del_object_to_remote_object(path)

    # GET OutPacketHead.
    def get_out_packet_head(self):
        logging.debug("GET OutPacketHead NetworkID:" + self.network_id)
        path = self.OUTPACKETS_HEAD_PATH
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        in_packet = None
        try:
            in_packet = globals()[resp.body[Packet.TYPE]].\
                create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET OutPacketHead Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return in_packet

    # DELETE OutPacketHead.
    def del_out_packet_head(self):
        logging.debug("DELETE OutPacketHead NetworkID:" + self.network_id)
        path = self.OUTPACKETS_HEAD_PATH
        return self._del_object_to_remote_object(path)

    # GET OutPacket.
    def get_out_packet(self, packet_id):
        logging.debug("GET OutPacket NetworkID:" + self.network_id +
                      " ID:" + packet_id)
        path = self.OUTPACKET_PATH % packet_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        out_packet = None
        try:
            out_packet = globals()[resp.body[Packet.TYPE]].\
                create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET OutnPacket Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return out_packet

    # DELETE OutPacket.
    def del_out_packet(self, packet_id):
        logging.debug("DELETE OutPacket NetworkID:" + self.network_id +
                      " ID:" + packet_id)
        path = self.OUTPACKET_PATH % packet_id
        return self._del_object_to_remote_object(path)

    ###################################
    # Custom request
    ###################################

    def put_attribute_of_node(self, attributes):
        logging.debug("PUT AttributeOfNode NetworkID:" + self.network_id)
        resp = Response(Response.StatusCode.OK, None)
        if attributes is None or len(attributes) == 0:
            return resp
        nodes = self.get_nodes()
        if nodes is None or len(nodes) == 0:
            return resp

        for node in nodes.values():
            update = False
            # check update node attributes
            for attr_key in attributes:
                node_attr = node.attributes
                if attr_key not in node_attr or\
                   node_attr[attr_key] == attributes[attr_key]:
                    continue
                update = True
                node_attr[attr_key] = attributes[attr_key]
            if update:
                # update
                self.put_node(node)
        return resp

    def delete_all_flow(self):
        logging.debug("DELETE AllFlow NetworkID:" + self.network_id)
        resp_list = []

        flow_set = self.get_flow_set()
        if flow_set is None:
            resp_list.append(Response(Response.StatusCode.OK, None))
            return resp_list

        for flow_id in flow_set.flows:
            resp_list.append(self.del_flow(flow_id))

        return resp_list

    def delete_topology(self):
        logging.debug("DELETE Topology NetworkID:" + self.network_id)
        resp_list = []

        topology = self.get_topology()
        if topology is None:
            resp_list.append(Response(Response.StatusCode.OK, None))
            return resp_list

        for link_id in topology.links:
            # delete Link
            resp_list.append(self.del_link(link_id))

        for node_id, node in topology.nodes.items():
            for port_id in node.ports:
                # delete port
                resp_list.append(self.del_port(node_id, port_id))
            # delete node
            resp_list.append(self.del_node(node_id))

        if len(resp_list) == 0:
            resp_list.append(Response(Response.StatusCode.OK, None))

        return resp_list
