
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

require 'trema'

# Extensions to Trema
# * Add to_s method to Action classes used

module Trema
  #########################
  # BasicAction
  #########################

  Actions::SendOutPort.class_eval do
    def to_s
      "#{self.class}: port_number=#{@port_number}"
    end
  end

  Actions::CopyTtlIn.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::CopyTtlOut.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::DecMplsTtl.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::DecIpTtl.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::Experimenter.class_eval do
    def to_s
      "#{self.class}: experimenter_id=#{@experimenter_id}, body=#{body}"
    end
  end

  Actions::GroupAction.class_eval do
    def to_s
      "#{self.class}: group_id=#{@group_id}"
    end
  end

  Actions::PopMpls.class_eval do
    def to_s
      "#{self.class}: ethertype=#{@ethertype}"
    end
  end

  Actions::PopPbb.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::PopVlan.class_eval do
    def to_s
      "#{self.class}"
    end
  end

  Actions::PushMpls.class_eval do
    def to_s
      "#{self.class}: ethertype=#{@ethertype}"
    end
  end

  Actions::PushPbb.class_eval do
    def to_s
      "#{self.class}: ethertype=#{@ethertype}"
    end
  end

  Actions::PushVlan.class_eval do
    def to_s
      "#{self.class}: vlan_id=#{@ethertype}"
    end
  end

  Actions::SetIpTtl.class_eval do
    def to_s
      "#{self.class}: ip_ttl=#{@ip_ttl}"
    end
  end

  Actions::SetMplsTtl.class_eval do
    def to_s
      "#{self.class}: mpls_ttl=#{@mpls_ttl}"
    end
  end

  Actions::SetQueue.class_eval do
    def to_s
      "#{self.class}: queue_id=#{@queue_id}"
    end
  end

  Actions::SetField.class_eval do
    def to_s
      "#{self.class}: action_set=#{@action_set}"
    end
  end

  #########################
  # FlexibleAction
  #########################

  Actions::EthSrc.class_eval do
    def to_s
      "#{self.class}: eth_src=#{@eth_src}"
    end
  end

  Actions::EthDst.class_eval do
    def to_s
      "#{self.class}: eth_dst=#{@eth_dst}"
    end
  end

  Actions::EtherType.class_eval do
    def to_s
      "#{self.class}: eth_type=#{@eth_type}"
    end
  end

  Actions::VlanVid.class_eval do
    def to_s
      "#{self.class}: vlan_vid=#{@vlan_vid}"
    end
  end

  Actions::VlanPriority.class_eval do
    def to_s
      "#{self.class}: vlan_pcp=#{@vlan_pcp}"
    end
  end

  Actions::IpDscp.class_eval do
    def to_s
      "#{self.class}: ip_dscp=#{@ip_dscp}"
    end
  end

  Actions::IpEcn.class_eval do
    def to_s
      "#{self.class}: ip_ecn=#{@ip_ecn}"
    end
  end

  Actions::IpProto.class_eval do
    def to_s
      "#{self.class}: ip_proto=#{@ip_proto}"
    end
  end

  Actions::Ipv4SrcAddr.class_eval do
    def to_s
      "#{self.class}: ipv4_src=#{@ipv4_src}"
    end
  end

  Actions::Ipv4DstAddr.class_eval do
    def to_s
      "#{self.class}: ipv4_dst=#{@ipv4_dst}"
    end
  end

  Actions::TcpSrcPort.class_eval do
    def to_s
      "#{self.class}: tcp_src=#{@tcp_src}"
    end
  end

  Actions::TcpDstPort.class_eval do
    def to_s
      "#{self.class}: tcp_dst=#{@tcp_dst}"
    end
  end

  Actions::UdpSrcPort.class_eval do
    def to_s
      "#{self.class}: udp_src=#{@udp_src}"
    end
  end

  Actions::UdpDstPort.class_eval do
    def to_s
      "#{self.class}: udp_dst=#{@udp_dst}"
    end
  end

  Actions::SctpSrcPort.class_eval do
    def to_s
      "#{self.class}: sctp_src=#{@sctp_src}"
    end
  end

  Actions::SctpDstPort.class_eval do
    def to_s
      "#{self.class}: sctp_dst=#{@sctp_dst}"
    end
  end

  Actions::Icmpv4Type.class_eval do
    def to_s
      "#{self.class}: icmpv4_type=#{@icmpv4_type}"
    end
  end

  Actions::Icmpv4Code.class_eval do
    def to_s
      "#{self.class}: icmpv4_code=#{@icmpv4_code}"
    end
  end

  Actions::ArpOp.class_eval do
    def to_s
      "#{self.class}: arp_op=#{@arp_op}"
    end
  end

  Actions::ArpSpa.class_eval do
    def to_s
      "#{self.class}: arp_spa=#{@arp_spa}"
    end
  end

  Actions::ArpTpa.class_eval do
    def to_s
      "#{self.class}: arp_tpa=#{@arp_tpa}"
    end
  end

  Actions::ArpSha.class_eval do
    def to_s
      "#{self.class}: arp_sha=#{@arp_sha}"
    end
  end

  Actions::ArpTha.class_eval do
    def to_s
      "#{self.class}: arp_tha=#{@arp_tha}"
    end
  end

  Actions::Ipv6SrcAddr.class_eval do
    def to_s
      "#{self.class}: ipv6_src=#{@ipv6_src}"
    end
  end

  Actions::Ipv6DstAddr.class_eval do
    def to_s
      "#{self.class}: ipv6_dst=#{@ipv6_dst}"
    end
  end

  Actions::Ipv6FlowLabel.class_eval do
    def to_s
      "#{self.class}: ipv6_flabel=#{@ipv6_flabel}"
    end
  end

  Actions::Icmpv6Type.class_eval do
    def to_s
      "#{self.class}: icmpv6_type=#{@icmpv6_type}"
    end
  end

  Actions::Icmpv6Code.class_eval do
    def to_s
      "#{self.class}: icmpv6_code=#{@icmpv6_code}"
    end
  end

  Actions::Ipv6NdTarget.class_eval do
    def to_s
      "#{self.class}: ipv6_nd_target=#{@ipv6_nd_target}"
    end
  end

  Actions::Ipv6NdSll.class_eval do
    def to_s
      "#{self.class}: ipv6_nd_sll=#{@ipv6_nd_sll}"
    end
  end

  Actions::Ipv6NdTll.class_eval do
    def to_s
      "#{self.class}: ipv6_nd_tll=#{@ipv6_nd_tll}"
    end
  end

  Actions::MplsLabel.class_eval do
    def to_s
      "#{self.class}: mpls_label=#{@mpls_label}"
    end
  end

  Actions::MplsTc.class_eval do
    def to_s
      "#{self.class}: mpls_tc=#{@mpls_tc}"
    end
  end

  Actions::MplsBos.class_eval do
    def to_s
      "#{self.class}: mpls_bos=#{@mpls_bos}"
    end
  end

  Actions::PbbIsid.class_eval do
    def to_s
      "#{self.class}: pbb_isid=#{@pbb_isid}"
    end
  end

  Actions::TunnelId.class_eval do
    def to_s
      "#{self.class}: tunnel_id=#{@tunnel_id}"
    end
  end

  Actions::Ipv6Exthdr.class_eval do
    def to_s
      "#{self.class}: ipv6_exthdr=#{@ipv6_exthdr}"
    end
  end
end
