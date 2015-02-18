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

from org.o3project.odenos.core.component.network.flow.basic.basic_flow_match\
    import BasicFlowMatch


class OFPFlowMatch(BasicFlowMatch):
    # property key
    IN_PHY_PORT = "in_phy_port"
    METADATA = "metadata"
    METADATA_MASK = "metadata_mask"
    ETH_SRC = "eth_src"
    ETH_SRC_MASK = "eth_src_mask"
    ETH_DST = "eth_dst"
    ETH_DST_MASK = "eth_dst_mask"
    VLAN_VID = "vlan_vid"
    VLAN_VID_MASK = "vlan_vid_mask"
    VLAN_PCP = "vlan_pcp"
    ETH_TYPE = "eth_type"

    IP_DSCP = "ip_dscp"
    IP_ECN = "ip_ecn"
    IP_PROTO = "ip_proto"
    IPV4_SRC = "ipv4_src"
    IPV4_SRC_MASK = "ipv4_src_mask"
    IPV4_DST = "ipv4_dst"
    IPV4_DST_MASK = "ipv4_dst_mask"

    TCP_SRC = "tcp_src"
    TCP_DST = "tcp_dst"
    UDP_SRC = "udp_src"
    UDP_DST = "udp_dst"
    SCTP_SRC = "sctp_src"
    SCTP_DST = "sctp_dst"
    ICMPV4_TYPE = "icmpv4_type"
    ICMPV4_CODE = "icmpv4_code"

    ARP_OP = "arp_op"
    ARP_SPA = "arp_spa"
    ARP_SPA_MASK = "arp_spa_mask"
    ARP_TPA = "arp_tpa"
    ARP_TPA_MASK = "arp_tpa_mask"
    ARP_SHA = "arp_sha"
    ARP_SHA_MASK = "arp_sha_mask"
    ARP_THA = "arp_tha"
    ARP_THA_MASK = "arp_tha_mask"

    IPV6_SRC = "ipv6_src"
    IPV6_SRC_MASK = "ipv6_src_mask"
    IPV6_DST = "ipv6_dst"
    IPV6_DST_MASK = "ipv6_dst_mask"
    IPV6_FLABEL = "ipv6_flabel"
    IPV6_FLABEL_MASK = "ipv6_flabel_mask"
    ICMPV6_TYPE = "icmpv6_type"
    ICMPV6_CODE = "icmpv6_code"
    IPV6_ND_TARGET = "ipv6_nd_target"
    IPV6_ND_SLL = "ipv6_nd_sll"
    IPV6_ND_TLL = "ipv6_nd_tll"

    MPLS_LABEL = "mpls_label"
    MPLS_TC = "mpls_tc"
    MPLS_BOS = "mpls_bos"
    PBB_ISID = "pbb_isid"
    PBB_ISID_MASK = "pbb_isid_mask"
    TUNNEL_ID = "tunnel_id"
    TUNNEL_ID_MASK = "tunnel_id_mask"
    IPV6_EXTHDR = "ipv6_exthdr"
    IPV6_EXTHDR_MASK = "ipv6_exthdr_mask"

    def __init__(self, type_, in_node, in_port):
        super(OFPFlowMatch, self).__init__(type_, in_node, in_port)

    @property
    def in_phy_port(self):
        if self.IN_PHY_PORT not in self._body:
            return None
        return self._body[self.IN_PHY_PORT]

    @in_phy_port.setter
    def in_phy_port(self, val):
        self._body[self.IN_PHY_PORT] = val

    @property
    def metadata(self):
        if self.METADATA not in self._body:
            return None
        return self._body[self.METADATA]

    @metadata.setter
    def metadata(self, val):
        self._body[self.METADATA] = val

    @property
    def metadata_mask(self):
        if self.METADATA_MASK not in self._body:
            return None
        return self._body[self.METADATA_MASK]

    @metadata_mask.setter
    def metadata_mask(self, val):
        self._body[self.METADATA_MASK] = val

    @property
    def eth_src(self):
        if self.ETH_SRC not in self._body:
            return None
        return self._body[self.ETH_SRC]

    @eth_src.setter
    def eth_src(self, val):
        self._body[self.ETH_SRC] = val

    @property
    def eth_src_mask(self):
        if self.ETH_SRC_MASK not in self._body:
            return None
        return self._body[self.ETH_SRC_MASK]

    @eth_src_mask.setter
    def eth_src_mask(self, val):
        self._body[self.ETH_SRC_MASK] = val

    @property
    def eth_dst(self):
        if self.ETH_DST not in self._body:
            return None
        return self._body[self.ETH_DST]

    @eth_dst.setter
    def eth_dst(self, val):
        self._body[self.ETH_DST] = val

    @property
    def eth_dst_mask(self):
        if self.ETH_DST_MASK not in self._body:
            return None
        return self._body[self.ETH_DST_MASK]

    @eth_dst_mask.setter
    def eth_dst_mask(self, val):
        self._body[self.ETH_DST_MASK] = val

    @property
    def vlan_vid(self):
        if self.VLAN_VID not in self._body:
            return None
        return self._body[self.VLAN_VID]

    @vlan_vid.setter
    def vlan_vid(self, val):
        self._body[self.VLAN_VID] = val

    @property
    def vlan_vid_mask(self):
        if self.VLAN_VID_MASK not in self._body:
            return None
        return self._body[self.VLAN_VID_MASK]

    @vlan_vid_mask.setter
    def vlan_vid_mask(self, val):
        self._body[self.VLAN_VID_MASK] = val

    @property
    def vlan_pcp(self):
        if self.VLAN_PCP not in self._body:
            return None
        return self._body[self.VLAN_PCP]

    @vlan_pcp.setter
    def vlan_pcp(self, val):
        self._body[self.VLAN_PCP] = val

    @property
    def eth_type(self):
        if self.ETH_TYPE not in self._body:
            return None
        return self._body[self.ETH_TYPE]

    @eth_type.setter
    def eth_type(self, val):
        self._body[self.ETH_TYPE] = val

    @property
    def ip_dscp(self):
        if self.IP_DSCP not in self._body:
            return None
        return self._body[self.IP_DSCP]

    @ip_dscp.setter
    def ip_dscp(self, val):
        self._body[self.IP_DSCP] = val

    @property
    def ip_ecn(self):
        if self.IP_ECN not in self._body:
            return None
        return self._body[self.IP_ECN]

    @ip_ecn.setter
    def ip_ecn(self, val):
        self._body[self.IP_ECN] = val

    @property
    def ip_proto(self):
        if self.IP_PROTO not in self._body:
            return None
        return self._body[self.IP_PROTO]

    @ip_proto.setter
    def ip_proto(self, val):
        self._body[self.IP_PROTO] = val

    @property
    def ipv4_src(self):
        if self.IPV4_SRC not in self._body:
            return None
        return self._body[self.IPV4_SRC]

    @ipv4_src.setter
    def ipv4_src(self, val):
        self._body[self.IPV4_SRC] = val

    @property
    def ipv4_src_mask(self):
        if self.IPV4_SRC_MASK not in self._body:
            return None
        return self._body[self.IPV4_SRC_MASK]

    @ipv4_src_mask.setter
    def ipv4_src_mask(self, val):
        self._body[self.IPV4_SRC_MASK] = val

    @property
    def ipv4_dst(self):
        if self.IPV4_DST not in self._body:
            return None
        return self._body[self.IPV4_DST]

    @ipv4_dst.setter
    def ipv4_dst(self, val):
        self._body[self.IPV4_DST] = val

    @property
    def ipv4_dst_mask(self):
        if self.IPV4_DST_MASK not in self._body:
            return None
        return self._body[self.IPV4_DST_MASK]

    @ipv4_dst_mask.setter
    def ipv4_dst_mask(self, val):
        self._body[self.IPV4_DST_MASK] = val

    @property
    def tcp_src(self):
        if self.TCP_SRC not in self._body:
            return None
        return self._body[self.TCP_SRC]

    @tcp_src.setter
    def tcp_src(self, val):
        self._body[self.TCP_SRC] = val

    @property
    def tcp_dst(self):
        if self.TCP_DST not in self._body:
            return None
        return self._body[self.TCP_DST]

    @tcp_dst.setter
    def tcp_dst(self, val):
        self._body[self.TCP_DST] = val

    @property
    def udp_src(self):
        if self.UDP_SRC not in self._body:
            return None
        return self._body[self.UDP_SRC]

    @udp_src.setter
    def udp_src(self, val):
        self._body[self.UDP_SRC] = val

    @property
    def udp_dst(self):
        if self.UDP_DST not in self._body:
            return None
        return self._body[self.UDP_DST]

    @udp_dst.setter
    def udp_dst(self, val):
        self._body[self.UDP_DST] = val

    @property
    def sctp_src(self):
        if self.SCTP_SRC not in self._body:
            return None
        return self._body[self.SCTP_SRC]

    @sctp_src.setter
    def sctp_src(self, val):
        self._body[self.SCTP_SRC] = val

    @property
    def sctp_dst(self):
        if self.SCTP_DST not in self._body:
            return None
        return self._body[self.SCTP_DST]

    @sctp_dst.setter
    def sctp_dst(self, val):
        self._body[self.SCTP_DST] = val

    @property
    def icmpv4_type(self):
        if self.ICMPV4_TYPE not in self._body:
            return None
        return self._body[self.ICMPV4_TYPE]

    @icmpv4_type.setter
    def icmpv4_type(self, val):
        self._body[self.ICMPV4_TYPE] = val

    @property
    def icmpv4_code(self):
        if self.ICMPV4_CODE not in self._body:
            return None
        return self._body[self.ICMPV4_CODE]

    @icmpv4_code.setter
    def icmpv4_code(self, val):
        self._body[self.ICMPV4_CODE] = val

    @property
    def arp_op(self):
        if self.ARP_OP not in self._body:
            return None
        return self._body[self.ARP_OP]

    @arp_op.setter
    def arp_op(self, val):
        self._body[self.ARP_OP] = val

    @property
    def arp_spa(self):
        if self.ARP_SPA not in self._body:
            return None
        return self._body[self.ARP_SPA]

    @arp_spa.setter
    def arp_spa(self, val):
        self._body[self.ARP_SPA] = val

    @property
    def arp_spa_mask(self):
        if self.ARP_SPA_MASK not in self._body:
            return None
        return self._body[self.ARP_SPA_MASK]

    @arp_spa_mask.setter
    def arp_spa_mask(self, val):
        self._body[self.ARP_SPA_MASK] = val

    @property
    def arp_tpa(self):
        if self.ARP_TPA not in self._body:
            return None
        return self._body[self.ARP_TPA]

    @arp_tpa.setter
    def arp_tpa(self, val):
        self._body[self.ARP_TPA] = val

    @property
    def arp_tpa_mask(self):
        if self.ARP_TPA_MASK not in self._body:
            return None
        return self._body[self.ARP_TPA_MASK]

    @arp_tpa_mask.setter
    def arp_tpa_mask(self, val):
        self._body[self.ARP_TPA_MASK] = val

    @property
    def arp_sha(self):
        if self.ARP_SHA not in self._body:
            return None
        return self._body[self.ARP_SHA]

    @arp_sha.setter
    def arp_sha(self, val):
        self._body[self.ARP_SHA] = val

    @property
    def arp_sha_mask(self):
        if self.ARP_SHA_MASK not in self._body:
            return None
        return self._body[self.ARP_SHA_MASK]

    @arp_sha_mask.setter
    def arp_sha_mask(self, val):
        self._body[self.ARP_SHA_MASK] = val

    @property
    def arp_tha(self):
        if self.ARP_THA not in self._body:
            return None
        return self._body[self.ARP_THA]

    @arp_tha.setter
    def arp_tha(self, val):
        self._body[self.ARP_THA] = val

    @property
    def arp_tha_mask(self):
        if self.ARP_THA_MASK not in self._body:
            return None
        return self._body[self.ARP_THA_MASK]

    @arp_tha_mask.setter
    def arp_tha_mask(self, val):
        self._body[self.ARP_THA_MASK] = val

    @property
    def ipv6_src(self):
        if self.IPV6_SRC not in self._body:
            return None
        return self._body[self.IPV6_SRC]

    @ipv6_src.setter
    def ipv6_src(self, val):
        self._body[self.IPV6_SRC] = val

    @property
    def ipv6_src_mask(self):
        if self.IPV6_SRC_MASK not in self._body:
            return None
        return self._body[self.IPV6_SRC_MASK]

    @ipv6_src_mask.setter
    def ipv6_src_mask(self, val):
        self._body[self.IPV6_SRC_MASK] = val

    @property
    def ipv6_dst(self):
        if self.IPV6_DST not in self._body:
            return None
        return self._body[self.IPV6_DST]

    @ipv6_dst.setter
    def ipv6_dst(self, val):
        self._body[self.IPV6_DST] = val

    @property
    def ipv6_dst_mask(self):
        if self.IPV6_DST_MASK not in self._body:
            return None
        return self._body[self.IPV6_DST_MASK]

    @ipv6_dst_mask.setter
    def ipv6_dst_mask(self, val):
        self._body[self.IPV6_DST_MASK] = val

    @property
    def ipv6_flabel(self):
        if self.IPV6_FLABEL not in self._body:
            return None
        return self._body[self.IPV6_FLABEL]

    @ipv6_flabel.setter
    def ipv6_flabel(self, val):
        self._body[self.IPV6_FLABEL] = val

    @property
    def ipv6_flabel_mask(self):
        if self.IPV6_FLABEL_MASK not in self._body:
            return None
        return self._body[self.IPV6_FLABEL_MASK]

    @ipv6_flabel_mask.setter
    def ipv6_flabel_mask(self, val):
        self._body[self.IPV6_FLABEL_MASK] = val

    @property
    def icmpv6_type(self):
        if self.ICMPV6_TYPE not in self._body:
            return None
        return self._body[self.ICMPV6_TYPE]

    @icmpv6_type.setter
    def icmpv6_type(self, val):
        self._body[self.ICMPV6_TYPE] = val

    @property
    def icmpv6_code(self):
        if self.ICMPV6_CODE not in self._body:
            return None
        return self._body[self.ICMPV6_CODE]

    @icmpv6_code.setter
    def icmpv6_code(self, val):
        self._body[self.ICMPV6_CODE] = val

    @property
    def ipv6_nd_target(self):
        if self.IPV6_ND_TARGET not in self._body:
            return None
        return self._body[self.IPV6_ND_TARGET]

    @ipv6_nd_target.setter
    def ipv6_nd_target(self, val):
        self._body[self.IPV6_ND_TARGET] = val

    @property
    def ipv6_nd_sll(self):
        if self.IPV6_ND_SLL not in self._body:
            return None
        return self._body[self.IPV6_ND_SLL]

    @ipv6_nd_sll.setter
    def ipv6_nd_sll(self, val):
        self._body[self.IPV6_ND_SLL] = val

    @property
    def ipv6_nd_tll(self):
        if self.IPV6_ND_TLL not in self._body:
            return None
        return self._body[self.IPV6_ND_TLL]

    @ipv6_nd_tll.setter
    def ipv6_nd_tll(self, val):
        self._body[self.IPV6_ND_TLL] = val

    @property
    def mpls_label(self):
        if self.MPLS_LABEL not in self._body:
            return None
        return self._body[self.MPLS_LABEL]

    @mpls_label.setter
    def mpls_label(self, val):
        self._body[self.MPLS_LABEL] = val

    @property
    def mpls_tc(self):
        if self.MPLS_TC not in self._body:
            return None
        return self._body[self.MPLS_TC]

    @mpls_tc.setter
    def mpls_tc(self, val):
        self._body[self.MPLS_TC] = val

    @property
    def mpls_bos(self):
        if self.MPLS_BOS not in self._body:
            return None
        return self._body[self.MPLS_BOS]

    @mpls_bos.setter
    def mpls_bos(self, val):
        self._body[self.MPLS_BOS] = val

    @property
    def pbb_isid(self):
        if self.PBB_ISID not in self._body:
            return None
        return self._body[self.PBB_ISID]

    @pbb_isid.setter
    def pbb_isid(self, val):
        self._body[self.PBB_ISID] = val

    @property
    def pbb_isid_mask(self):
        if self.PBB_ISID_MASK not in self._body:
            return None
        return self._body[self.PBB_ISID_MASK]

    @pbb_isid_mask.setter
    def pbb_isid_mask(self, val):
        self._body[self.PBB_ISID_MASK] = val

    @property
    def tunnel_id(self):
        if self.TUNNEL_ID not in self._body:
            return None
        return self._body[self.TUNNEL_ID]

    @tunnel_id.setter
    def tunnel_id(self, val):
        self._body[self.TUNNEL_ID] = val

    @property
    def tunnel_id_mask(self):
        if self.TUNNEL_ID_MASK not in self._body:
            return None
        return self._body[self.TUNNEL_ID_MASK]

    @tunnel_id_mask.setter
    def tunnel_id_mask(self, val):
        self._body[self.TUNNEL_ID_MASK] = val

    @property
    def ipv6_exthdr(self):
        if self.IPV6_EXTHDR not in self._body:
            return None
        return self._body[self.IPV6_EXTHDR]

    @ipv6_exthdr.setter
    def ipv6_exthdr(self, val):
        self._body[self.IPV6_EXTHDR] = val

    @property
    def ipv6_exthdr_mask(self):
        if self.IPV6_EXTHDR_MASK not in self._body:
            return None
        return self._body[self.IPV6_EXTHDR_MASK]

    @ipv6_exthdr_mask.setter
    def ipv6_exthdr_mask(self, val):
        self._body[self.IPV6_EXTHDR_MASK] = val

    @classmethod
    def create_from_packed(cls, packed):
        in_port = None
        if cls.IN_PORT in packed:
            in_port = packed[cls.IN_PORT]
        flow = cls(packed[cls.TYPE], packed[cls.IN_NODE], in_port)

        if cls.IN_PHY_PORT in packed:
            flow.in_phy_port = packed[cls.IN_PHY_PORT]
        if cls.METADATA in packed:
            flow.metadata = packed[cls.METADATA]
        if cls.METADATA_MASK in packed:
            flow.metadata_mask = packed[cls.METADATA_MASK]
        if cls.ETH_SRC in packed:
            flow.eth_src = packed[cls.ETH_SRC]
        if cls.ETH_SRC_MASK in packed:
            flow.eth_src_mask = packed[cls.ETH_SRC_MASK]
        if cls.ETH_DST in packed:
            flow.eth_dst = packed[cls.ETH_DST]
        if cls.ETH_DST_MASK in packed:
            flow.eth_dst_mask = packed[cls.ETH_DST_MASK]
        if cls.VLAN_VID in packed:
            flow.vlan_vid = packed[cls.VLAN_VID]
        if cls.VLAN_VID_MASK in packed:
            flow.vlan_vid_mask = packed[cls.VLAN_VID_MASK]
        if cls.VLAN_PCP in packed:
            flow.vlan_pcp = packed[cls.VLAN_PCP]
        if cls.ETH_TYPE in packed:
            flow.eth_type = packed[cls.ETH_TYPE]

        if cls.IP_DSCP in packed:
            flow.ip_dscp = packed[cls.IP_DSCP]
        if cls.IP_ECN in packed:
            flow.ip_ecn = packed[cls.IP_ECN]
        if cls.IP_PROTO in packed:
            flow.ip_proto = packed[cls.IP_PROTO]
        if cls.IPV4_SRC in packed:
            flow.ipv4_src = packed[cls.IPV4_SRC]
        if cls.IPV4_SRC_MASK in packed:
            flow.ipv4_src_mask = packed[cls.IPV4_SRC_MASK]
        if cls.IPV4_DST in packed:
            flow.ipv4_dst = packed[cls.IPV4_DST]
        if cls.IPV4_DST_MASK in packed:
            flow.ipv4_dst_mask = packed[cls.IPV4_DST_MASK]

        if cls.TCP_SRC in packed:
            flow.tcp_src = packed[cls.TCP_SRC]
        if cls.TCP_DST in packed:
            flow.tcp_dst = packed[cls.TCP_DST]
        if cls.UDP_SRC in packed:
            flow.udp_src = packed[cls.UDP_SRC]
        if cls.UDP_DST in packed:
            flow.udp_dst = packed[cls.UDP_DST]
        if cls.SCTP_SRC in packed:
            flow.sctp_src = packed[cls.SCTP_SRC]
        if cls.SCTP_DST in packed:
            flow.sctp_dst = packed[cls.SCTP_DST]
        if cls.ICMPV4_TYPE in packed:
            flow.icmpv4_type = packed[cls.ICMPV4_TYPE]
        if cls.ICMPV4_CODE in packed:
            flow.icmpv4_code = packed[cls.ICMPV4_CODE]

        if cls.ARP_OP in packed:
            flow.arp_op = packed[cls.ARP_OP]
        if cls.ARP_SPA in packed:
            flow.arp_spa = packed[cls.ARP_SPA]
        if cls.ARP_SPA_MASK in packed:
            flow.arp_spa_mask = packed[cls.ARP_SPA_MASK]
        if cls.ARP_TPA in packed:
            flow.arp_tpa = packed[cls.ARP_TPA]
        if cls.ARP_TPA_MASK in packed:
            flow.arp_tpa_mask = packed[cls.ARP_TPA_MASK]
        if cls.ARP_SHA in packed:
            flow.arp_sha = packed[cls.ARP_SHA]
        if cls.ARP_SHA_MASK in packed:
            flow.arp_sha_mask = packed[cls.ARP_SHA_MASK]
        if cls.ARP_THA in packed:
            flow.arp_tha = packed[cls.ARP_THA]
        if cls.ARP_THA_MASK in packed:
            flow.arp_tha_mask = packed[cls.ARP_THA_MASK]

        if cls.IPV6_SRC in packed:
            flow.ipv6_src = packed[cls.IPV6_SRC]
        if cls.IPV6_SRC_MASK in packed:
            flow.ipv6_src_mask = packed[cls.IPV6_SRC_MASK]
        if cls.IPV6_DST in packed:
            flow.ipv6_dst = packed[cls.IPV6_DST]
        if cls.IPV6_DST_MASK in packed:
            flow.ipv6_dst_mask = packed[cls.IPV6_DST_MASK]
        if cls.IPV6_FLABEL in packed:
            flow.ipv6_flabel = packed[cls.IPV6_FLABEL]
        if cls.IPV6_FLABEL_MASK in packed:
            flow.ipv6_flabel_mask = packed[cls.IPV6_FLABEL_MASK]
        if cls.ICMPV6_TYPE in packed:
            flow.icmpv6_type = packed[cls.ICMPV6_TYPE]
        if cls.ICMPV6_CODE in packed:
            flow.icmpv6_code = packed[cls.ICMPV6_CODE]
        if cls.IPV6_ND_TARGET in packed:
            flow.ipv6_nd_target = packed[cls.IPV6_ND_TARGET]
        if cls.IPV6_ND_SLL in packed:
            flow.ipv6_nd_sll = packed[cls.IPV6_ND_SLL]
        if cls.IPV6_ND_TLL in packed:
            flow.ipv6_nd_tll = packed[cls.IPV6_ND_TLL]

        if cls.MPLS_LABEL in packed:
            flow.mpls_label = packed[cls.MPLS_LABEL]
        if cls.MPLS_TC in packed:
            flow.mpls_tc = packed[cls.MPLS_TC]
        if cls.MPLS_BOS in packed:
            flow.mpls_bos = packed[cls.MPLS_BOS]
        if cls.PBB_ISID in packed:
            flow.pbb_isid = packed[cls.PBB_ISID]
        if cls.PBB_ISID_MASK in packed:
            flow.pbb_isid_mask = packed[cls.PBB_ISID_MASK]
        if cls.TUNNEL_ID in packed:
            flow.tunnel_id = packed[cls.TUNNEL_ID]
        if cls.TUNNEL_ID_MASK in packed:
            flow.tunnel_id_mask = packed[cls.TUNNEL_ID_MASK]
        if cls.IPV6_EXTHDR in packed:
            flow.ipv6_exthdr = packed[cls.IPV6_EXTHDR]
        if cls.IPV6_EXTHDR_MASK in packed:
            flow.ipv6_exthdr_mask = packed[cls.IPV6_EXTHDR_MASK]

        return flow

    def packed_object(self):
        return self._body
