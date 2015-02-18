
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

require 'odenos'

#########################
# dummy Trema module
#########################

module Trema 
  class Controller 
    OFPP_ALL = 0xffffffffc
    def initialize
    end
    def shutdown!
    end
    def start
    end
    def periodic_timer_event(func, time)
    end
    def send_packet_out(dpid, message, actions)
    end
  end
 
  class Mac < Odenos::Util::ObjectHash
    attr_accessor :mac_addr
    def initialize(mac_addr)
      @mac_addr = mac_addr
    end
    def to_s
      @mac_addr
    end
  end
   
  class Match < Odenos::Util::ObjectHash
    hash_accessor :object, :in_port, :in_phy_port
    hash_accessor :object, :metadata, :metadata_mask
    hash_accessor :object, :eth_src, :eth_src_mask
    hash_accessor :object, :eth_dst, :eth_dst_mask
    hash_accessor :object, :eth_type
    hash_accessor :object, :vlan_vid, :vlan_vid_mask
    hash_accessor :object, :vlan_pcp
    hash_accessor :object, :ip_dscp # IP DSCP ( 6 bits in ToS field )
    hash_accessor :object, :ip_ecn # IP ECN ( 2 bits in ToS field )
    hash_accessor :object, :ip_proto # ip protocol
    hash_accessor :object, :ipv4_src, :ipv4_src_mask
    hash_accessor :object, :ipv4_dst, :ipv4_dst_mask
    hash_accessor :object, :tcp_src, :tcp_dst
    hash_accessor :object, :udp_src, :udp_dst
    hash_accessor :object, :sctp_src, :sctp_dst
    hash_accessor :object, :icmpv4_type, :icmpv4_code
    hash_accessor :object, :arp_op
    hash_accessor :object, :arp_spa, :arp_spa_mask
    hash_accessor :object, :arp_tpa, :arp_tpa_mask
    hash_accessor :object, :arp_sha, :arp_sha_mask
    hash_accessor :object, :arp_tha, :arp_tha_mask
    hash_accessor :object, :ipv6_src, :ipv6_src_mask
    hash_accessor :object, :ipv6_dst, :ipv6_dst_mask
    hash_accessor :object, :ipv6_flabel, :ipv6_flabel_mask
    hash_accessor :object, :icmpv6_type, :icmpv6_code
    hash_accessor :object, :ipv6_nd_target
    hash_accessor :object, :ipv6_nd_sll, :ipv6_nd_tll
    hash_accessor :object, :mpls_label
    hash_accessor :object, :mpls_tc, :mpls_bos
    hash_accessor :object, :pbb_isid, :pbb_isid_mask
    hash_accessor :object, :tunnel_id, :tunnel_id_mask
    hash_accessor :object, :ipv6_exthdr, :ipv6_exthdr_mask
  end
  
  module Messages 

    class Port
      attr_accessor :port_no, :hw_addr, :name, :config
      attr_accessor :state, :curr, :advertised, :supported
      attr_accessor :peer, :curr_speed, :max_speed
    end

    class FlowRemoved
      attr_accessor :datapath_id, :transaction_id, :cookie
      attr_accessor :priority, :reason, :table_id
      attr_accessor :duration_sec, :duration_nsec
      attr_accessor :idle_timeout, :hard_timeout
      attr_accessor :packet_count, :byte_count
      attr_accessor :match
      def initialize(transaction_id)
        @transaction_id = transaction_id
      end
    end
    
    class BarrierReply
      attr_accessor :transaction_id
      def initialize(transaction_id)
        @transaction_id = transaction_id
      end
    end

    class Error
      attr_accessor :transaction_id
      def initialize(transaction_id)
        @transaction_id = transaction_id
      end
    end
        
    class PacketIn 
      attr_accessor :data
      attr_accessor :in_port
      attr_accessor :eth_type, :eth_src, :eth_dst 
      attr_accessor :vlan_vid, :vlan_prio
      attr_accessor :arp_op, :arp_sha, :arp_spa, :arp_tpa
      attr_accessor :ipv4_src, :ipv4_dst
      attr_accessor :ip_proto, :ip_dscp, :ip_ecn 
      attr_accessor :tcp_src, :tcp_dst, :udp_src, :udp_dst 
      attr_accessor :sctp_src, :sctp_dst, :icmpv4_type, :icmpv4_code 
      attr_accessor :icmpv6_type, :icmpv6_code 
      attr_accessor :ipv6_nd_target, :ipv6_nd_sll, :ipv6_nd_tll 
      attr_accessor :ipv6_src, :ipv6_dst, :ipv6_flabel, :ipv6_exthdr
      attr_accessor :mpls_label, :mpls_tc, :mpls_bos, :pbb_isid 
      def initialize(in_port, data)
        @in_port = in_port 
        @data = data 
      end
      def vtag?
        if !@vlan_vid.nil? and @vlan_vid > 0 
         return true
        end
        false
      end
      def arp?
        if !@eth_type.nil? and @eth_type == 0x0806 
         return true
        end
        false
      end
      def ipv4?
        if !@eth_type.nil? and @eth_type == 0x0800
         return true
        end
        false
      end
      def ipv6?
        if !@eth_type.nil? and @eth_type == 0x86dd
         return true
        end
        false
      end
      def udp?
        if !@ip_proto.nil? and @ip_proto == 17
         return true
        end
        false
      end
      def tcp?
        if !@ip_proto.nil? and @ip_proto == 6
         return true
        end
        false
      end
      def sctp?
        if !@ip_proto.nil? and @ip_proto == 132 
         return true
        end
        false
      end
      def icmpv4?
        if !@ip_proto.nil? and @ip_proto == 1 
         return true
        end
        false
      end
      def icmpv6?
        if !@ip_proto.nil? and @ip_proto == 58 
         return true
        end
        false
      end
      def pbb?
        if !@pbb_isid.nil? 
         return true
        end
        false
      end
      def mpls?
        if !@mpls_label.nil? 
         return true
        end
        false
      end
    end
    class Port
      OFPPF_10GB_FD = 10000
      OFPPF_1GB_HD = 1000
      OFPPF_1GB_FD = 1000
      OFPPF_100MB_FD = 100 
      OFPPF_100MB_HD = 100
      OFPPF_10MB_FD = 10
      OFPPF_10MB_HD = 10
      attr_accessor :port_no, :hw_addr, :name
      attr_accessor :config, :state, :curr
      attr_accessor :advertised, :supported, :peer
      attr_accessor :curr_speed, :max_speed
      def initialize()
        @port_no = 0
        @hw_addr = nil
        @name = nil
        @config = 0
        @state = 0
        @curr = 0
        @advertised = 0
        @supported = 0
        @peer = 0
        @curr_speed = 0
        @max_speed = 0
      end
    end
  end

  module Actions 

    #########################
    # BasicAction
    #########################

    class SendOutPort 
      attr_accessor :port_number
      def initialize(val) 
        if val.is_a?(Hash)
          @port_number = val[:port_number]
        else
          @port_number = val
        end
      end
    end

    class CopyTtlIn
    end

    class CopyTtlOut 
    end

    class DecMplsTtl 
    end

    class DecIpTtl 
    end

    class Experimenter 
      attr_accessor :experimenter_id
      attr_accessor :body
      def initialize(id, body)
        @experimenter_id = id
        @body = body
      end
    end

    class GroupAction 
      attr_accessor :group_id
      def initialize(val)
        @group_id = val
      end
    end

    class PopMpls 
      attr_accessor :ethertype
      def initialize(val)
        @ethertype = val
      end
    end

    class PopPbb 
    end

    class PopVlan 
    end

    class PushMpls 
      attr_accessor :ethertype
      def initialize(val)
        @ethertype = val
      end
    end

    class PushPbb 
      attr_accessor :ethertype
      def initialize(val)
        @ethertype = val
      end
    end

    class PushVlan 
      attr_accessor :ethertype
      def initialize(val)
        @ethertype = val
      end
    end

    class SetIpTtl 
      attr_accessor :ip_ttl
      def initialize(val)
        @ip_ttl = val
      end
    end
                
    class SetMplsTtl 
      attr_accessor :mpls_ttl
      def initialize(val)
        @mpls_ttl = val
      end
    end

    class SetQueue 
      attr_accessor :queue_id
      def initialize(val)
        @queue_id = val
      end
    end
        
    class SetField 
      attr_accessor :action_set
      def initialize(val)
        @vlan_vid = val
      end
    end

    #########################
    # FlexibleAction
    ########################

    class EthSrc 
      attr_accessor :eth_src
      def initialize(val)
        @eth_src = val
      end
    end

    class EthDst 
      attr_accessor :eth_dst
      def initialize(val)
        @eth_dst = val
      end
    end

    class EtherType 
      attr_accessor :eth_type
      def initialize(val)
        @eth_type = val
      end
    end
        
    class VlanVid 
      attr_accessor :vlan_vid
      def initialize(val)
        @vlan_vid = val
      end
    end

    class VlanPriority
      attr_accessor :vlan_pcp
      def initialize(val)
        @vlan_pcp = val
      end
    end

    class IpDscp 
      attr_accessor :ip_dscp
      def initialize(val)
        @ip_dscp = val
      end
    end

    class IpEcn 
      attr_accessor :ip_ecn
      def initialize(val)
        @ip_ecn = val
      end
    end

    class IpProto 
      attr_accessor :ip_proto
      def initialize(val)
        @ip_proto = val
      end
    end

    class Ipv4SrcAddr 
      attr_accessor :ipv4_src
      def initialize(val)
        @ipv4_src = val
      end
    end

    class Ipv4DstAddr 
      attr_accessor :ipv4_dst
      def initialize(val)
        @ipv4_dst = val
      end
    end

    class TcpSrcPort 
      attr_accessor :tcp_src
      def initialize(val)
        @tcp_src = val
      end
    end

    class TcpDstPort 
      attr_accessor :tcp_dst
      def initialize(val)
        @tcp_dst = val
      end
    end

    class UdpSrcPort 
      attr_accessor :udp_src
      def initialize(val)
        @udp_src = val
      end
    end

    class UdpDstPort 
      attr_accessor :udp_dst
      def initialize(val)
        @udp_dst = val
      end
    end

    class SctpSrcPort 
      attr_accessor :sctp_src
      def initialize(val)
        @sctp_src = val
      end
    end

    class SctpDstPort 
      attr_accessor :sctp_dst
      def initialize(val)
        @sctp_dst = val
      end
    end

    class Icmpv4Type 
      attr_accessor :icmpv4_type
      def initialize(val)
        @icmpv4_type = val
      end
    end

    class Icmpv4Code 
      attr_accessor :icmpv4_code
      def initialize(val)
        @icmpv4_code = val
      end
    end

    class ArpOp 
      attr_accessor :arp_op
      def initialize(val)
        @arp_op = val
      end
    end

    class ArpSpa 
      attr_accessor :arp_spa
      def initialize(val)
        @arp_spa = val
      end
    end

    class ArpTpa 
      attr_accessor :arp_tpa
      def initialize(val)
        @arp_tpa = val
      end
    end

    class ArpSha 
      attr_accessor :arp_sha
      def initialize(val)
        @arp_sha = val
      end
    end

    class ArpTha 
      attr_accessor :arp_tha
      def initialize(val)
        @arp_tha = val
      end
    end

    class Ipv6SrcAddr 
      attr_accessor :ipv6_src
      def initialize(val)
        @ipv6_src = val
      end
    end

    class Ipv6DstAddr 
      attr_accessor :ipv6_dst
      def initialize(val)
        @ipv6_dst = val
      end
    end

    class Ipv6FlowLabel 
      attr_accessor :ipv6_flabel
      def initialize(val)
        @ipv6_flabel = val
      end
    end

    class Icmpv6Type 
      attr_accessor :icmpv6_type
      def initialize(val)
        @icmpv6_type = val
      end
    end

    class Icmpv6Code 
      attr_accessor :icmpv6_code
      def initialize(val)
        @icmpv6_code = val
      end
    end

    class Ipv6NdTarget 
      attr_accessor :ipv6_nd_target
      def initialize(val)
        @ipv6_nd_target = val
      end
    end

    class Ipv6NdSll 
      attr_accessor :ipv6_nd_sll
      def initialize(val)
        @ipv6_nd_sll = val
      end
    end

    class Ipv6NdTll 
      attr_accessor :ipv6_nd_tll
      def initialize(val)
        @ipv6_nd_tll = val
      end
    end

    class MplsLabel 
      attr_accessor :mpls_label
      def initialize(val)
        @mpls_label = val
      end
    end

    class MplsTc 
      attr_accessor :mpls_tc
      def initialize(val)
        @mpls_tc = val
      end
    end

    class MplsBos 
      attr_accessor :mpls_bos
      def initialize(val)
        @mpls_bos = val
      end
    end

    class PbbIsid 
      attr_accessor :pbb_isid
      def initialize(val)
        @pbb_isid = val
      end
    end

    class TunnelId 
      attr_accessor :tunnel_id
      def initialize(val)
        @tunnel_id = val
      end
    end

    class Ipv6Exthdr 
      attr_accessor :ipv6_exthdr
      def initialize(val)
        @ipv6_exthdr = val
      end
    end
       
  end
end