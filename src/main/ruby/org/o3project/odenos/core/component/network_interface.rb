
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

require 'odenos/remoteobject'
require 'odenos/core/util/logger'

module Odenos
  module Component
    class NetworkInterface
      include Odenos::Core
      include Odenos::Util::Logger

      # Topology
      TOPOLOGY_PATH = 'topology'
      # Node
      NODE_PATH = 'topology/nodes/%s'
      NODES_PATH = 'topology/nodes'
      PHYSICAL_NODES_PATH = 'topology/physical_nodes/%s'
      # Port
      PORT_PATH = 'topology/nodes/%s/ports/%s'
      PORTS_PATH = 'topology/nodes/%s/ports'
      PHYSICAL_PORTS_PATH = 'topology/physical_ports/%s'
      # Link
      LINK_PATH = 'topology/links/%s'
      LINKS_PATH = 'topology/links'
      # Flow
      FLOW_PATH = 'flows/%s'
      FLOWS_PATH = 'flows'
      # Packet
      INPACKET_PATH = 'packets/in/%s'
      INPACKETS_PATH = 'packets/in'
      INPACKETS_HEAD_PATH = 'packets/in/head'
      OUTPACKET_PATH = 'packets/out/%s'
      OUTPACKETS_PATH = 'packets/out'
      OUTPACKETS_HEAD_PATH = 'packets/out/head'
      PACKETS_PATH = 'packets'

      def initialize(dispatcher, nwc_id)
        logger_ident_initialize("NetworkInterface")
        debug "NetworkInterface#initialize ID: #{nwc_id}"
        @dispatcher = dispatcher
        @nwc_id = nwc_id
      end

      # Topology's Request
      # GET Topology.
      # ( GET <base_uri>/topology )
      def get_topology
        debug ">> #{__method__}"
        path = TOPOLOGY_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Topology.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT Topology.
      # ( PUT <base_uri>/topology )
      def put_topology(topology)
        debug ">> #{__method__}"
        path = TOPOLOGY_PATH
        put_object_to_network(@nwc_id, path, topology)
      end

      # Node's Request
      # POST Node.
      # ( POST <base_uri>/topology/nodes )
      def post_node(node)
        debug ">> #{__method__}"
        path = NODES_PATH
        post_object_to_network(@nwc_id, path, node)
      end

      # GET Nodes.
      # ( GET <base_uri>/topology/nodes )
      def get_nodes
        debug ">> #{__method__}"
        path = NODES_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          nodes = {}
          resp.body.each do |node_id, node|
            nodes[node_id] = Node.new(node)
          end
          return nodes
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # GET Node.
      # ( GET <base_uri>/topology/nodes/<node_id> )
      def get_node(node_id)
        debug ">> #{__method__}"
        path = NODE_PATH % node_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Node.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT Node.
      # ( PUT <base_uri>/topology/nodes/<node_id> )
      def put_node(node)
        debug ">> #{__method__}"
        path = NODE_PATH % node.node_id
        put_object_to_network(@nwc_id, path, node)
      end

      # DELETE Node.
      # ( DELETE <base_uri>/topology/nodes/<node_id> )
      def del_node(node_id)
        debug ">> #{__method__}"
        path = NODE_PATH % node_id
        del_object_to_network(@nwc_id, path)
      end

      # GET PhysicalNode.
      # ( GET <base_uri>/topology/physical_nodes/<physical_id> )
      def get_physical_node(physical_id)
        debug ">> #{__method__}"
        path = PHYSICAL_NODES_PATH % physical_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Node.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT PhysicalNode.
      # ( PUT <base_uri>/topology/physical_nodes/<physical_id> )
      def put_physical_node(node)
        debug ">> #{__method__}"
        path = PHYSICAL_NODES_PATH % node.attributes[Node::ATTR_KEY_PHYSICAL_ID]
        put_object_to_network(@nwc_id, path, node)
      end

      # DELETE PhysicalNode.
      # ( DELETE <base_uri>/topology/physical_nodes/<physical_id> )
      def del_physical_node(physical_id)
        debug ">> #{__method__}"
        path = PHYSICAL_NODES_PATH % physical_id
        del_object_to_network(@nwc_id, path)
      end

      # Packet's Request
      # POST Port.
      # ( POST <base_uri>/topology/nodes/<node_id>/ports )
      def post_port(port)
        debug ">> #{__method__}"
        path = PORTS_PATH % port.node_id
        post_object_to_network(@nwc_id, path, port)
      end

      # GET Ports.
      # ( GET <base_uri>/topology/nodes/<node_id>/ports )
      def get_ports(node_id)
        debug ">> #{__method__}"
        path = PORTS_PATH % node_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          ports = {}
          resp.body.each do |port_id, port|
            ports[port_id] = Port.new(port)
          end
          return ports
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # GET Port.
      # ( GET <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
      def get_port(node_id, port_id)
        debug ">> #{__method__}"
        path = format(PORT_PATH, node_id, port_id)
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Port.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT Port.
      # ( PUT <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
      def put_port(port)
        debug ">> #{__method__}"
        path = format(PORT_PATH, port.node_id, port.port_id)
        put_object_to_network(@nwc_id, path, port)
      end

      # DELETE Port.
      # ( DELETE <base_uri>/topology/nodes/<node_id>/ports/<port_id> )
      def del_port(node_id, port_id)
        debug ">> #{__method__}"
        path = format(PORT_PATH, node_id, port_id)
        del_object_to_network(@nwc_id, path)
      end

      # GET PhysicalPort.
      # ( GET <base_uri>/topology/physical_ports/<physical_id> )
      def get_physical_port(physical_id)
        debug ">> #{__method__}"
        path = PHYSICAL_PORTS_PATH % physical_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Port.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT PhysicalPort.
      # ( PUT <base_uri>/topology/physical_ports/<physical_id> )
      def put_physical_port(port)
        debug ">> #{__method__}"
        path = PHYSICAL_PORTS_PATH % port.attributes[Port::ATTR_KEY_PHYSICAL_ID]
        put_object_to_network(@nwc_id, path, port)
      end

      # DELETE PhysicalPort.
      # ( DELETE <base_uri>/topology/physical_ports/<physical_id> )
      def del_physical_port(physical_id)
        debug ">> #{__method__}"
        path = PHYSICAL_PORTS_PATH % physical_id
        del_object_to_network(@nwc_id, path)
      end

      # Link's Request
      # POST Link.
      # ( POST <base_uri>/topology/links )
      def post_link(link)
        debug ">> #{__method__}"
        path = LINKS_PATH
        post_object_to_network(@nwc_id, path, link)
      end

      # GET Links.
      # ( GET <base_uri>/topology/links )
      def get_links
        debug ">> #{__method__}"
        path = LINKS_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          links = {}
          resp.body.each do |link_id, link|
            links[link_id] = Link.new(link)
          end
          return links
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # GET Link.
      # ( GET <base_uri>/topology/links/<link_id> )
      def get_link(link_id)
        debug ">> #{__method__}"
        path = LINK_PATH % link_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Link.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT Link.
      # ( PUT <base_uri>/topology/links/<link_id> )
      def put_link(link)
        debug ">> #{__method__}"
        path = LINK_PATH % link.link_id
        put_object_to_network(@nwc_id, path, link)
      end

      # DELETE Link.
      # ( DELETE <base_uri>/topology/links/<link_id> )
      def del_link(link_id)
        debug ">> #{__method__}"
        path = LINK_PATH % link_id
        del_object_to_network(@nwc_id, path)
      end

      # Flow's Request
      # POST Flow.
      # ( POST <base_uri>/flows )
      def post_flow(flow)
        debug ">> #{__method__}"
        path = FLOWS_PATH
        post_object_to_network(@nwc_id, path, flow)
      end

      # GET FlowSet.
      # ( GET <base_uri>/flows )
      def get_flow_set
        debug ">> #{__method__}"
        path = FLOWS_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return FlowSet.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # GET Flow.
      # ( GET <base_uri>/flows/<flow_id> )
      def get_flow(flow_id)
        debug ">> #{__method__}"
        path = FLOW_PATH % flow_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Flow.from_object_hash(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # PUT Flow.
      # ( PUT <base_uri>/flows/<flow_id> )
      def put_flow(flow)
        debug ">> #{__method__}"
        path = FLOW_PATH % flow.flow_id
        put_object_to_network(@nwc_id, path, flow)
      end

      # DELETE Flow.
      # ( DELETE <base_uri>/flows/<flow_id> )
      def del_flow(flow_id)
        debug ">> #{__method__}"
        path = FLOW_PATH % flow_id
        del_object_to_network(@nwc_id, path)
      end

      # Packet's Request
      # GET Packets.
      # ( GET <base_uri>/packets )
      def get_packets
        debug ">> #{__method__}"
        path = PACKETS_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return PacketStatus.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # POST InPacket.
      # ( POST <base_uri>/packets/in )
      def post_in_packet(in_packet)
        debug ">> #{__method__}"
        path = INPACKETS_PATH
        post_object_to_network(@nwc_id, path, in_packet)
      end

      # GET InPackets.
      # ( GET <base_uri>/packets/in )
      def get_in_packets
        debug ">> #{__method__}"
        path = INPACKETS_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return PacketStatus.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # DELETE InPackets.
      # ( DELETE <base_uri>/packets/in )
      def del_in_packets
        debug ">> #{__method__}"
        path = INPACKETS_PATH
        del_object_to_network(@nwc_id, path)
      end

      # Get InPacket first pending currently.
      # ( GET <base_uri>/packets/in/head )
      def get_in_packet_head
        debug ">> #{__method__}"
        path = INPACKETS_HEAD_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Packet.from_object_hash(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # Delete InPacket first pending currently.
      # ( DELETE <base_uri>/packets/in/head )
      def del_in_packet_head
        debug ">> #{__method__}"
        path = INPACKETS_HEAD_PATH
        del_object_to_network(@nwc_id, path)
      end

      # GET InPacket.
      # ( GET <base_uri>/packets/in/<packet_id> )
      def get_in_packet(packet_id)
        debug ">> #{__method__}"
        path = INPACKET_PATH % packet_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Packet.from_object_hash(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # DELETE InPacket.
      # ( DELETE <base_uri>/packets/in/<packet_id> )
      def del_in_packet(packet_id)
        debug ">> #{__method__}"
        path = INPACKET_PATH % packet_id
        del_object_to_network(@nwc_id, path)
      end

      # POST OutPacket.
      # (  POST <base_uri>/packets/out )
      def post_out_packet(out_packet)
        debug ">> #{__method__}"
        path = OUTPACKETS_PATH
        post_object_to_network(@nwc_id, path, out_packet)
      end

      # GET OutPackets.
      # ( GET <base_uri>/packets/out )
      def get_out_packets
        debug ">> #{__method__}"
        path = OUTPACKETS_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return PacketStatus.new(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # DELETE OutPackets.
      # ( DELETE <base_uri>/packets/out )
      def del_out_packets
        debug ">> #{__method__}"
        path = OUTPACKETS_PATH
        del_object_to_network(@nwc_id, path)
      end

      # GET OutPacket first pending currently.
      # ( GET <base_uri>/packets/out/head )
      def get_out_packet_head
        debug ">> #{__method__}"
        path = OUTPACKETS_HEAD_PATH
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Packet.from_object_hash(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # DELETE OutPacket first pending currently.
      # ( DELETE <base_uri>/packets/out/head )
      def del_out_packet_head
        debug ">> #{__method__}"
        path = OUTPACKETS_HEAD_PATH
        del_object_to_network(@nwc_id, path)
      end

      # GET OutPacket.
      # ( GET <base_uri>/packets/out/<packet_id> )
      def get_out_packet(packet_id)
        debug ">> #{__method__}"
        path = OUTPACKET_PATH % packet_id
        resp = get_object_to_network(@nwc_id, path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return Packet.from_object_hash(resp.body)
        rescue => ex
          error 'Exception: Get Invalid Message'
          error " #{ex.message} #{ex.backtrace}"
          return nil
        end
      end

      # DELETE OutPacket.
      # ( DELETE <base_uri>/packets/out/<packet_id> )
      def del_out_packet(packet_id)
        debug ">> #{__method__}"
        path = OUTPACKET_PATH % packet_id
        del_object_to_network(@nwc_id, path)
      end

      # Custom Request
      def put_attribute_of_node(attributes)
        debug ">> #{__method__}"

        resp = Response.new(Response::OK, nil)
        if attributes.nil? ||
           attributes.length == 0
          return resp
        end

        nodes = get_nodes
        if nodes.nil? ||
           nodes.length == 0
          return resp
        end
        nodes.each_value do |node|
          update = false
          attributes.each do |key, attr|
            if !node.attributes.include?(key) ||
               node.attributes[key] == attr
              next
            end
            update = true
            node.attributes[key] = attr
          end
          if update
            put_node(node)
          end
        end
      end

      def delete_all_flow
        debug ">> #{__method__}"

        resp_list = []
        flow_set = get_flow_set
        if flow_set.nil? ||
           flow_set.flows.length == 0
          resp_list.push(Response.new(Response::OK, nil))
          return resp_list
        end

        flow_set.flows.each_key do |flow_id|
          resp_list.push(del_flow(flow_id))
        end
        resp_list
      end

      def delete_topology
        debug ">> #{__method__}"

        resp_list = []
        topology = get_topology
        if topology.nil?
          resp_list.push(Response.new(Response::OK, nil))
          return resp_list
        end

        # delete link
        topology.links.each_key do |link_id|
          resp_list.push(del_link(link_id))
        end

        # delete node
        topology.nodes.each do |node_id, node|
          # delete port
          node.ports.each_key do |port_id|
            resp_list.push(del_port(node_id, port_id))
          end
          resp_list.push(del_node(node_id))
        end

        if resp_list.length == 0
          resp_list.push(Response.new(Response::OK, nil))
        end
        resp_list
      end

      # common method

      protected

      def post_object_to_network(nwc_id, path, body)
        debug ">> #{__method__} NWC ID: #{nwc_id} PATH: #{path}"
        send_request(nwc_id, :POST, path, body)
      end

      def put_object_to_network(nwc_id, path, body)
        debug ">> #{__method__} NWC ID: #{nwc_id} PATH: #{path}"
        send_request(nwc_id, :PUT, path, body)
      end

      def del_object_to_network(nwc_id, path)
        debug ">> #{__method__} NWC ID: #{nwc_id} PATH: #{path}"
        send_request(nwc_id, :DELETE, path, nil)
      end

      def get_object_to_network(nwc_id, path)
        debug ">> #{__method__} NWC ID: #{nwc_id} PATH: #{path}"
        send_request(nwc_id, :GET, path, nil)
      end

      def send_request(obj_id, method, path, body)
        resp = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
        req = Request.new(obj_id, method, path, body)
        begin
          resp = @dispatcher.request_sync(req)
          debug ">> RESPONSE: #{resp.to_a.inspect}"
        rescue => ex
          error "Exception: Request to #{obj_id} Method:#{method} Path:#{path}"
          error " #{ex.message} #{ex.backtrace}"
        end
        resp
      end
    end
  end
end
