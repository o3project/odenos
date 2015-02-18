
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

require 'odenos/core/util'

module Odenos
  module Component
    ###############################
    # Define Topology Class
    ###############################

    class Topology < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion

      hash_accessor :object, :nodes, :links

      #
      # @overload initialize(object_hash)
      #  @param [Hash] object_hash Hash created by self.to_hash
      # @overload initialize(*node_id, *link_id)
      #  @param [Array<String>] node_id Node.node_id
      #  @param [Array<String>] link_id Link.link_id
      #
      def initialize(*args)
        case args.length
        when 0
          super
          self.nodes = {}
          self.links = {}
        when 1
          if args.first.is_a? Hash
            super(args.first)
            self.nodes ||= {}
            self.links ||= {}
          end
        end
      end
    end

    class Node < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion
      include Odenos::Util::Attributes

      hash_accessor :object, :node_id
      # @return [[String]] List of Port.id on this node
      hash_accessor :object, :ports

      # Common Attribute: admin status
      ATTR_KEY_ADMIN_STATUS = 'admin_status'
      # Common Attribute: operational status
      ATTR_KEY_OPER_STATUS = 'oper_status'
      # Common Attribute: physical_id
      ATTR_KEY_PHYSICAL_ID = 'physical_id'
      # Common Attribute: vendor
      ATTR_KEY_VENDOR_ID = 'vendor'

      #
      # @overload initialize(object_hash)
      #  @param [Hash] object_hash Hash created by self.to_hash
      # @overload initialize(node_id)
      #  @param [String] node_id Node.node_id
      # @overload initialize(node_id, *port_id)
      #  @param [String] node_id Node.node_id
      #  @param [Array<String>] port_id Port.port_id
      #
      def initialize(*args)
        case args.length
        when 0
          super(node_id: nil)
          self.node_id = nil
          self.ports = {}
        when 1
          if args.first.is_a? Hash
            # Node from Hash Object
            super(args.first)
            self.node_id = node_id
            self.ports ||= {}
          end
        end
      end
    end

    class Port < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion
      include Odenos::Util::Attributes

      hash_accessor :object, :port_id, :node_id, :out_link, :in_link

      # Common Attribute: admin status
      ATTR_KEY_ADMIN_STATUS = 'admin_status'
      # Common Attribute: operational status
      ATTR_KEY_OPER_STATUS = 'oper_status'
      # Common Attribute: max bandwidth
      ATTR_KEY_MAX_BANDWIDTH = 'max_bandwidth'
      # Common Attribute: unreserved bandwidth
      ATTR_KEY_UNRESERVED_BANDWIDTH = 'unreserved_bandwidth'
      # Common Attribute: physical_id
      ATTR_KEY_PHYSICAL_ID = 'physical_id'
      # Common Attribute: vendor
      ATTR_KEY_VENDOR_ID = 'vendor'
      # Common Attribute: is_boundary
      ATTR_KEY_IS_BOUNDARY = 'is_boundary'

      #
      # @overload initialize(object_hash)
      #  @param [Hash] object_hash Hash created by self.to_hash
      # @overload initialize(node_id, port_id)
      #  @param [String] node_id Node.node_id
      #  @param [String] port_id Port.port_id
      #
      def initialize(*args)
        case args.length
        when 1
          if args.first.is_a? Hash
            # Port from Hash Object
            super(args.first)
          else
            fail ArgumentError, 'Only Hash supported.'
          end
        when 2
          # Port.new node_id, port_id
          initial_hash = {}
          initial_hash[:node_id] = args[0]
          initial_hash[:port_id] = args[1]
          super(initial_hash)
        else
          fail ArgumentError, 'Expect 1 or 2 argument.'
        end

        # mandatory keys
        fail ArgumentError, ':node_id missing' if node_id.nil?

        self.port_id = port_id
        self.out_link = out_link
        self.in_link = in_link
      end
    end

    class Link < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion
      include Odenos::Util::Attributes

      hash_accessor :object, :link_id
      hash_accessor :object, :src_node, :src_port, :dst_node, :dst_port

      # Common Attribute: operational status
      ATTR_KEY_OPER_STATUS = 'oper_status'
      # Common Attribute: cost
      ATTR_KEY_COST = 'cost'
      # Common Attribute: latency
      ATTR_KEY_LATENCY = 'latency'
      # Common Attribute: req latency
      ATTR_KEY_REQ_LATENCY = 'req_latency'
      # Common Attribute: max bandwidth
      ATTR_KEY_MAX_BANDWIDTH = 'max_bandwidth'
      # Common Attribute: unreserved bandwidth
      ATTR_KEY_UNRESERVED_BANDWIDTH = 'unreserved_bandwidth'
      # Common Attribute: req bandwidth
      ATTR_KEY_REQ_BANDWIDTH = 'req_bandwidth'
      # Common Attribute: establishment status
      ATTR_KEY_ESTABLISHMENT_STATUS = 'establishment_status'

      #
      # @overload initialize(object_hash)
      #  @param [Hash] object_hash Hash created by self.to_hash
      # @overload initialize(src_node, src_port, dst_node, dst_port)
      #  @param [String] src_node Source Node.node_id
      #  @param [String] src_port Source Port.port_id
      #  @param [String] dst_node Destination Node.node_id
      #  @param [String] dst_port Destination Port.port_id
      # @overload initialize(link_id, src_node, src_port, dst_node, dst_port)
      #  @param [String] link_id Link.link_id
      #  @param [String] src_node Source Node.node_id
      #  @param [String] src_port Source Port.port_id
      #  @param [String] dst_node Destination Node.node_id
      #  @param [String] dst_port Destination Port.port_id
      #
      def initialize(*args)
        case args.length
        when 1
          if args.first.is_a? Hash
            # Link from Hash object
            super(args.first)
          else
            fail ArgumentError, 'Only Hash supported.'
          end
        when 4
          # Link.new src_node, src_port, dst_node, dst_port
          initial_hash = {}
          initial_hash[:src_node] = args[0]
          initial_hash[:src_port] = args[1]
          initial_hash[:dst_node] = args[2]
          initial_hash[:dst_port] = args[3]
          super(initial_hash)
        when 5
          # Link.new flow_id, src_node, src_port, dst_node, dst_port
          initial_hash = {}
          initial_hash[:link_id] = args[0]
          initial_hash[:src_node] = args[1]
          initial_hash[:src_port] = args[2]
          initial_hash[:dst_node] = args[3]
          initial_hash[:dst_port] = args[4]
          super(initial_hash)
        else
          fail ArgumentError, 'Expect 1 or 5 argument.'
        end

        # mandatory keys
        fail ArgumentError, ':src_node missing' if src_node.nil?
        fail ArgumentError, ':src_port missing' if src_port.nil?
        fail ArgumentError, ':dst_node missing' if dst_node.nil?
        fail ArgumentError, ':dst_port missing' if dst_port.nil?

        self.link_id = link_id
      end
    end

    ###############################
    # Define Flow Class
    ###############################

    class FlowSet < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion

      hash_accessor :object, :priority, :flows

      def initialize(*args)
        case args.length
        when 0
          super
          self.priority = {}
          self.flows = {}
        when 1
          if args.first.is_a? Hash
            super(args.first)
            self.priority ||= {}
            self.flows ||= {}
          end
        end
      end
    end

    class Flow < Odenos::Util::TypedObjectHash
      include Odenos::Util::OdenosVersion
      include Odenos::Util::Attributes

      hash_accessor :object, :flow_id, :owner, :enabled, :priority, :status

      # Common Attribute: req bandwidth
      ATTR_KEY_REQ_BANDWIDTH = 'req_bandwidth'
      # Common Attribute: bandwidth
      ATTR_KEY_BANDWIDTH = 'bandwidth'
      # Common Attribute: req latency
      ATTR_KEY_REQ_LATENCY = 'req_latency'
      # Common Attribute: latency
      ATTR_KEY_LATENCY = 'latency'

      # Status
      NONE = 'none'
      ESTABLISHING = 'establishing'
      ESTABLISHED = 'established'
      TEARDOWN = 'teardown'
      FAILED = 'failed'
    end

    class BasicFlow < Flow
      hash_accessor :object, :matches, :path, :edge_actions

      def initialize(initial_hash = {})
        super
        @object[:matches] ||= []
        @object[:path] ||= []
        @object[:edge_actions] ||= {}

        normalize_matches
        normalize_actions
      end

      def normalize_matches
        @object[:matches].map! do |match|
          if match.is_a? Hash
            BasicFlowMatch.from_object_hash(match)
          else
            match
          end
        end
      end

      def normalize_actions
        @object[:edge_actions].each_value do |actions|
          actions.map! do |edge_action|
            if edge_action.is_a? Hash
              BasicFlowAction.from_object_hash(edge_action)
            else
              edge_action
            end
          end
        end
      end
    end

    class OFPFlow < BasicFlow
      hash_accessor :object, :idle_timeout, :hard_timeout

      def initialize(initial_hash = {})
        super
        self.idle_timeout ||= 0
        self.hard_timeout ||= 0
        self.priority     ||= 0xFFFF
      end
    end

    ###############################
    # Define FlowAction Class
    ###############################

    # @abstract
    class BasicFlowAction < Odenos::Util::TypedObjectHash
      alias_method :action_type, :odenos_type
    end

    class FlowActionOutput < BasicFlowAction
      # @return [Integer] Port.port_id
      hash_accessor :object, :output
    end

    # @abstract
    class OFPFlowAction < BasicFlowAction
    end

    class OFPFlowActionCopyTtlIn < OFPFlowAction
    end

    class OFPFlowActionCopyTtlOut < OFPFlowAction
    end

    class OFPFlowActionDecIpTtl < OFPFlowAction
    end

    class OFPFlowActionDecMplsTtl < OFPFlowAction
    end

    class OFPFlowActionExperimenter < OFPFlowAction
      hash_accessor :object, :experimenter_id

      # @return [String] experimenter-defined arbitrary additional data
      hash_accessor :object, :body
    end

    class OFPFlowActionGroupAction < OFPFlowAction
      hash_accessor :object, :group_id
    end

    class OFPFlowActionPopMpls < OFPFlowAction
      MPLS_UNICAST = 0x8847
      MPLS_MULTICAST = 0x8848
      hash_accessor :object, :eth_type
    end

    class OFPFlowActionPopPbb < OFPFlowAction
    end

    class OFPFlowActionPopVlan < OFPFlowAction
    end

    class OFPFlowActionPushMpls < OFPFlowAction
      MPLS_UNICAST = 0x8847
      MPLS_MULTICAST = 0x8848
      hash_accessor :object, :eth_type
    end

    class OFPFlowActionPushPbb < OFPFlowAction
      DEFAULT_ETHER_TYPE = 0x88e7
      hash_accessor :object, :eth_type
    end

    class OFPFlowActionPushVlan < OFPFlowAction
      # 0x8100 - Customer VLAN tag type (ctag)
      C_VTAG = 0x8100
      # 0x88a8 - Service VLAN tag identifier (stag)
      S_VTAG = 0x88a8
      hash_accessor :object, :eth_type
    end

    class OFPFlowActionSetIpTtl < OFPFlowAction
      hash_accessor :object, :ip_ttl
    end

    class OFPFlowActionSetMplsTtl < OFPFlowAction
      hash_accessor :object, :mpls_ttl
    end

    class OFPFlowActionSetField < OFPFlowAction
      hash_accessor :object, :match

      def initialize(initial_hash = {})
        super
        normalize_match
      end

      def normalize_match
        if match.is_a? Hash
          self.match = OFPFlowMatch.from_object_hash(match)
        end
      end
    end

    class OFPFlowActionSetQueue < OFPFlowAction
      hash_accessor :object, :queue_id
    end

    ###############################
    # Define FlowMatch Class
    ###############################

    class BasicFlowMatch < Odenos::Util::TypedObjectHash
      alias_method :match_type, :odenos_type

      hash_accessor :object, :in_node, :in_port
    end

    class OFPFlowMatch < BasicFlowMatch
      hash_accessor :object, :in_phy_port, :metadata, :metadata_mask

      hash_accessor :object, :eth_src, :eth_src_mask, :eth_dst, :eth_dst_mask
      hash_accessor :object, :eth_type, :vlan_vid, :vlan_vid_mask, :vlan_pcp
      hash_accessor :object, :ip_dscp, :ip_ecn, :ip_proto

      hash_accessor :object, :ipv4_src, :ipv4_src_mask, :ipv4_dst, :ipv4_dst_mask

      hash_accessor :object, :tcp_src, :tcp_dst, :udp_src, :udp_dst
      hash_accessor :object, :sctp_src, :sctp_dst, :icmpv4_type, :icmpv4_code

      hash_accessor :object, :arp_op, :arp_spa, :arp_spa_mask, :arp_tpa, :arp_tpa_mask
      hash_accessor :object, :arp_sha, :arp_sha_mask, :arp_tha, :arp_tha_mask

      hash_accessor :object, :ipv6_src, :ipv6_src_mask, :ipv6_dst, :ipv6_dst_mask
      hash_accessor :object, :ipv6_flabel, :ipv6_flabel_mask, :icmpv6_type, :icmpv6_code
      hash_accessor :object, :ipv6_nd_target, :ipv6_nd_sll, :ipv6_nd_tll

      hash_accessor :object, :mpls_label, :mpls_tc, :mpls_bos
      hash_accessor :object, :pbb_isid, :pbb_isid_mask
      hash_accessor :object, :tunnel_id, :tunnel_id_mask
      hash_accessor :object, :ipv6_exthdr, :ipv6_exthdr_mask
    end

    ###############################
    # Define Packet Class
    ###############################

    # @abstract
    class Packet < Odenos::Util::TypedObjectHash
      include Odenos::Util::Attributes

      alias_method :packet_type, :odenos_type

      def packet_id
        @object[:id]
      end

      def packet_id=(id)
        @object[:id] = id
      end
    end

    class InPacket < Packet
      hash_accessor :object, :node, :port

      # @return [String] A String that holds the entire or
      #                  portion of the received frame.
      hash_accessor :object, :data, :header
    end

    class OFPInPacket < InPacket
      hash_accessor :object, :time
    end

    class OutPacket < Packet
      hash_accessor :object, :node

      # @return [String] A String that holds the entire or
      #                  portion of the received frame.
      hash_accessor :object, :data, :header

      # @return [Array<String>] list of Port.port_id to output
      hash_reader :object, :ports
      # @return [Array<String>] list of Port.port_id not to output
      hash_reader :object, :ports_except

      def initialize(initial_hash = {})
        initial_hash['ports_except'] = initial_hash['ports-except']
        super
        @object[:ports_except] ||= []
        @object[:ports] ||= []
      end

      def ports_specified?
        (!ports.nil? && !ports.empty?)
      end

      # @param [Array<String>] portid_ary list of Port.port_id to output
      # @return [Array<String>] list of Port.port_id
      def ports=(portid_ary)
        @object[:ports_except] = []
        @object[:ports] = portid_ary
      end

      # @param [Array<String>] portid_ary list of Port.port_id not to output
      # @return [Array<String>] list of Port.port_id
      def ports_except=(portid_ary)
        @object[:ports] = []
        @object[:ports_except] = portid_ary
      end
    end

    class OFPOutPacket < OutPacket
    end

    class PacketStatus < Odenos::Util::TypedObjectHash
      hash_accessor :object, :in_packet_count, :in_packet_bytes
      hash_accessor :object, :in_packet_queue_count
      hash_accessor :object, :out_packet_count, :out_packet_bytes
      hash_accessor :object, :out_packet_queue_count

      # @return [Array<String>] list of in_packet id
      hash_accessor :object, :in_packets
      # @return [Array<String>] list of out_packet id
      hash_accessor :object, :out_packets

      def initialize(initial_hash = {})
        super
        @object[:in_packets] ||= []
        @object[:out_packets] ||= []
      end
    end

    ###############################
    # Define Event Class
    ###############################

    # @abstract
    class EntityChangeEventBase < Odenos::Core::ObjectChangeEventBase
      include Odenos::Util::OdenosVersion
    end

    class NodeChanged < EntityChangeEventBase
      TYPE = 'NodeChanged'

      def node_id
        @object[:id]
      end

      def node_id=(v)
        @object[:id] = v
      end

      def initialize(initial_hash = {})
        super
        # (ODENOS) If there can there be a variant of Link, remove below
        if curr.is_a? Hash
          self.curr = Node.new(curr)
        end
        if prev.is_a? Hash
          self.prev = Node.new(prev)
        end
      end
    end

    class PortChanged < EntityChangeEventBase
      TYPE = 'PortChanged'

      def port_id
        @object[:id]
      end

      def port_id=(v)
        @object[:id] = v
      end

      def initialize(initial_hash = {})
        super
        # (ODENOS) If there can there be a variant of Link, remove below
        if curr.is_a? Hash
          self.curr = Port.new(curr)
        end
        if prev.is_a? Hash
          self.prev = Port.new(prev)
        end
      end
    end

    class LinkChanged < EntityChangeEventBase
      TYPE = 'LinkChanged'

      def link_id
        @object[:id]
      end

      def link_id=(v)
        @object[:id] = v
      end

      def initialize(initial_hash = {})
        super
        # (ODENOS) If there can there be a variant of Link, remove below
        if curr.is_a? Hash
          self.curr = Link.new(curr)
        end
        if prev.is_a? Hash
          self.prev = Link.new(prev)
        end
      end
    end

    class FlowChanged < EntityChangeEventBase
      TYPE = 'FlowChanged'

      def flow_id
        @object[:id]
      end

      def flow_id=(v)
        @object[:id] = v
      end

      def initialize(initial_hash = {})
        super
      end
    end

    class TopologyChanged < Odenos::Core::ObjectChangeEventBase
      TYPE = 'TopologyChanged'

      def initialize(initial_hash = {})
        super
        if curr.is_a? Hash
          self.curr = Topology.new(curr)
        end
        if prev.is_a? Hash
          self.prev = Topology.new(prev)
        end
      end
    end

    class InPacketAdded < Odenos::Util::ObjectHash
      TYPE = 'InPacketAdded'

      def packet_id
        @object[:id]
      end

      def packet_id=(v)
        @object[:id] = v
      end
    end

    class OutPacketAdded < Odenos::Util::ObjectHash
      TYPE = 'OutPacketAdded'

      def packet_id
        @object[:id]
      end

      def packet_id=(v)
        @object[:id] = v
      end
    end
  end
end
