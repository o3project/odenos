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

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match\
    import OFPFlowMatch

import unittest


class OFPFlowMatchTest(unittest.TestCase):

    def setUp(self):
        self.target = OFPFlowMatch("OFPFlowMatch", "ofp_in_node",
                                   "ofp_in_port")
        self.target.in_phy_port = "in_phy_port"
        self.target.metadata =  11
        self.target.metadata_mask =  12
        self.target.eth_src = "eth_src"
        self.target.eth_src_mask = "eth_src_mask"
        self.target.eth_dst = "eth_dst"
        self.target.eth_dst_mask = "eth_dst_mask"
        self.target.vlan_vid = 13
        self.target.vlan_vid_mask = 14
        self.target.vlan_pcp = 15
        self.target.eth_type = 16

        self.target.ip_dscp = 21
        self.target.ip_ecn = 22
        self.target.ip_proto = 23
        self.target.ipv4_src = "ipv4_src"
        self.target.ipv4_src_mask = "ipv4_src_mask"
        self.target.ipv4_dst = "ipv4_dst"
        self.target.ipv4_dst_mask = "ipv4_dst_mask"

        self.target.tcp_src = 31
        self.target.tcp_dst = 32
        self.target.udp_src = 33
        self.target.udp_dst = 34
        self.target.sctp_src = 35
        self.target.sctp_dst = 36
        self.target.icmpv4_type = 37 
        self.target.icmpv4_code = 38

        self.target.arp_op = 40
        self.target.arp_spa = "arp_spa"
        self.target.arp_spa_mask = "arp_spa_mask"
        self.target.arp_tpa = "arp_tpa" 
        self.target.arp_tpa_mask = "arp_tpa_mask" 
        self.target.arp_sha = "arp_sha" 
        self.target.arp_sha_mask = "arp_sha_mask" 
        self.target.arp_tha = "arp_tha" 
        self.target.arp_tha_mask = "arp_tha_mask" 

        self.target.ipv6_src = "ipv6_src" 
        self.target.ipv6_src_mask = "ipv6_src_mask" 
        self.target.ipv6_dst = "ipv6_dst" 
        self.target.ipv6_dst_mask = "ipv6_dst_mask" 
        self.target.ipv6_flabel = 50 
        self.target.ipv6_flabel_mask = 51 
        self.target.icmpv6_type = 52 
        self.target.icmpv6_code = 53 
        self.target.ipv6_nd_target = "ipv6_nd_target" 
        self.target.ipv6_nd_sll = "ipv6_nd_sll" 
        self.target.ipv6_nd_tll = "ipv6_nd_tll" 
        self.target.mpls_label = 54 
        self.target.mpls_tc = 55 
        self.target.mpls_bos = 56
        self.target.pbb_isid = 57 
        self.target.pbb_isid_mask = 58
        self.target.tunnel_id = 59
        self.target.tunnel_id_mask = 60
        self.target.ipv6_exthdr = 61
        self.target.ipv6_exthdr_mask = 62

    def tearDown(self):
        self.target = None

    def test_constractor_Not_None(self):
        self.assertEqual(self.target._body[OFPFlowMatch.TYPE], "OFPFlowMatch")
        self.assertEqual(self.target._body[OFPFlowMatch.IN_NODE], "ofp_in_node")
        self.assertEqual(self.target._body[OFPFlowMatch.IN_PORT], "ofp_in_port")

        self.assertEqual(self.target._body[OFPFlowMatch.IN_PHY_PORT], "in_phy_port")
        self.assertEqual(self.target._body[OFPFlowMatch.METADATA], 11)
        self.assertEqual(self.target._body[OFPFlowMatch.METADATA_MASK], 12)
        self.assertEqual(self.target._body[OFPFlowMatch.ETH_SRC], "eth_src")
        self.assertEqual(self.target._body[OFPFlowMatch.ETH_SRC_MASK], "eth_src_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.ETH_DST], "eth_dst")
        self.assertEqual(self.target._body[OFPFlowMatch.ETH_DST_MASK], "eth_dst_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.VLAN_VID], 13)
        self.assertEqual(self.target._body[OFPFlowMatch.VLAN_VID_MASK], 14)
        self.assertEqual(self.target._body[OFPFlowMatch.VLAN_PCP], 15)
        self.assertEqual(self.target._body[OFPFlowMatch.ETH_TYPE], 16)

        self.assertEqual(self.target._body[OFPFlowMatch.IP_DSCP], 21)
        self.assertEqual(self.target._body[OFPFlowMatch.IP_ECN], 22)
        self.assertEqual(self.target._body[OFPFlowMatch.IP_PROTO], 23)
        self.assertEqual(self.target._body[OFPFlowMatch.IPV4_SRC], "ipv4_src")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV4_SRC_MASK], "ipv4_src_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV4_DST], "ipv4_dst")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV4_DST_MASK], "ipv4_dst_mask")

        self.assertEqual(self.target._body[OFPFlowMatch.TCP_SRC], 31)
        self.assertEqual(self.target._body[OFPFlowMatch.TCP_DST], 32)
        self.assertEqual(self.target._body[OFPFlowMatch.UDP_SRC], 33)
        self.assertEqual(self.target._body[OFPFlowMatch.UDP_DST], 34)
        self.assertEqual(self.target._body[OFPFlowMatch.SCTP_SRC], 35)
        self.assertEqual(self.target._body[OFPFlowMatch.SCTP_DST], 36)
        self.assertEqual(self.target._body[OFPFlowMatch.ICMPV4_TYPE], 37)
        self.assertEqual(self.target._body[OFPFlowMatch.ICMPV4_CODE], 38)

        self.assertEqual(self.target._body[OFPFlowMatch.ARP_OP], 40)
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_SPA], "arp_spa")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_SPA_MASK], "arp_spa_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_TPA], "arp_tpa")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_TPA_MASK], "arp_tpa_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_SHA], "arp_sha")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_SHA_MASK], "arp_sha_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_THA], "arp_tha")
        self.assertEqual(self.target._body[OFPFlowMatch.ARP_THA_MASK], "arp_tha_mask")
        
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_SRC], "ipv6_src")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_SRC_MASK], "ipv6_src_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_DST], "ipv6_dst")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_DST_MASK], "ipv6_dst_mask")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_FLABEL], 50)
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_FLABEL_MASK], 51)
        self.assertEqual(self.target._body[OFPFlowMatch.ICMPV6_TYPE], 52)
        self.assertEqual(self.target._body[OFPFlowMatch.ICMPV6_CODE], 53)
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_ND_TARGET], "ipv6_nd_target")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_ND_SLL], "ipv6_nd_sll")
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_ND_TLL], "ipv6_nd_tll")

        self.assertEqual(self.target._body[OFPFlowMatch.MPLS_LABEL], 54)
        self.assertEqual(self.target._body[OFPFlowMatch.MPLS_TC], 55)
        self.assertEqual(self.target._body[OFPFlowMatch.MPLS_BOS], 56)
        self.assertEqual(self.target._body[OFPFlowMatch.PBB_ISID], 57)
        self.assertEqual(self.target._body[OFPFlowMatch.PBB_ISID_MASK], 58)
        self.assertEqual(self.target._body[OFPFlowMatch.TUNNEL_ID], 59)
        self.assertEqual(self.target._body[OFPFlowMatch.TUNNEL_ID_MASK], 60)
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_EXTHDR], 61)
        self.assertEqual(self.target._body[OFPFlowMatch.IPV6_EXTHDR_MASK], 62)


    def test_constractor_None(self):
        self.target = OFPFlowMatch("OFPFlowMatch", "ofp_in_node",
                                   "ofp_in_port")

        self.assertEqual(self.target._body, {"in_node": "ofp_in_node",
                                             "type": "OFPFlowMatch",
                                             "in_port": "ofp_in_port"})

    # IN_PHY_PORT
    def test_in_phy_port(self):
        self.assertEqual(self.target.in_phy_port, "in_phy_port")

    def test_in_phy_port_None(self):
        del self.target._body[OFPFlowMatch.IN_PHY_PORT]
        self.assertEqual(self.target.in_phy_port, None)
        
    # METADATA
    def test_metadata(self):
        self.assertEqual(self.target.metadata, 11)

    def test_metadata_None(self):
        del self.target._body[OFPFlowMatch.METADATA]
        self.assertEqual(self.target.metadata, None)

    # METADATA_MASK
    def test_metadata_mask(self):
        self.assertEqual(self.target.metadata_mask, 12)

    def test_metadata_mask_None(self):
        del self.target._body[OFPFlowMatch.METADATA_MASK]
        self.assertEqual(self.target.metadata_mask, None)

    # ETH_SRC
    def test_eth_src(self):
        self.assertEqual(self.target.eth_src, "eth_src")

    def test_eth_src_None(self):
        del self.target._body[OFPFlowMatch.ETH_SRC]
        self.assertEqual(self.target.eth_src, None)
        
    # ETH_SRC_MASK
    def test_eth_src_mask(self):
        self.assertEqual(self.target.eth_src_mask, "eth_src_mask")

    def test_eth_src_mask_None(self):
        del self.target._body[OFPFlowMatch.ETH_SRC_MASK]
        self.assertEqual(self.target.eth_src_mask, None)

    # ETH_DST
    def test_dl_dst(self):
        self.assertEqual(self.target.eth_dst, "eth_dst")

    def test_dl_dst_None(self):
        del self.target._body[OFPFlowMatch.ETH_DST]
        self.assertEqual(self.target.eth_dst, None)

    # ETH_DST_MASK
    def test_eth_dst_mask(self):
        self.assertEqual(self.target.eth_dst_mask, "eth_dst_mask")

    def test_eth_dst_mask_None(self):
        del self.target._body[OFPFlowMatch.ETH_DST_MASK]
        self.assertEqual(self.target.eth_dst_mask, None)

    # VLAN_VID
    def test_vlan_vid(self):
        self.assertEqual(self.target.vlan_vid, 13)

    def test_vlan_vid_None(self):
        del self.target._body[OFPFlowMatch.VLAN_VID]
        self.assertEqual(self.target.vlan_vid, None)

    # VLAN_VID_MASK
    def test_vlan_vid_mask(self):
        self.assertEqual(self.target.vlan_vid_mask, 14)

    def test_vlan_vid_mask_None(self):
        del self.target._body[OFPFlowMatch.VLAN_VID_MASK]
        self.assertEqual(self.target.vlan_vid_mask, None)

    # VLAN_PCP
    def test_vlan_pcp(self):
        self.assertEqual(self.target.vlan_pcp, 15)

    def test_vlan_pcp_None(self):
        del self.target._body[OFPFlowMatch.VLAN_PCP]
        self.assertEqual(self.target.vlan_pcp, None)

    # ETH_TYPE
    def test_eth_type(self):
        self.assertEqual(self.target.eth_type, 16)

    def test_eth_type_None(self):
        del self.target._body[OFPFlowMatch.ETH_TYPE]
        self.assertEqual(self.target.eth_type, None)

    # IP_DSCP
    def test_ip_dscp(self):
        self.assertEqual(self.target.ip_dscp, 21)

    def test_ip_dscp_None(self):
        del self.target._body[OFPFlowMatch.IP_DSCP]
        self.assertEqual(self.target.ip_dscp, None)

    # IP_ECN
    def test_ip_ecn(self):
        self.assertEqual(self.target.ip_ecn, 22)

    def test_ip_ecn_None(self):
        del self.target._body[OFPFlowMatch.IP_ECN]
        self.assertEqual(self.target.ip_ecn, None)

    # IP_PROTO
    def test_ip_proto(self):
        self.assertEqual(self.target.ip_proto, 23)

    def test_ip_proto_None(self):
        del self.target._body[OFPFlowMatch.IP_PROTO]
        self.assertEqual(self.target.ip_proto, None)

    # IPV4_SRC
    def test_ipv4_src(self):
        self.assertEqual(self.target.ipv4_src, "ipv4_src")

    def test_ipv4_src_None(self):
        del self.target._body[OFPFlowMatch.IPV4_SRC]
        self.assertEqual(self.target.ipv4_src, None)

    # IPV4_SRC_MASK
    def test_ipv4_src_mask(self):
        self.assertEqual(self.target.ipv4_src_mask, "ipv4_src_mask")

    def test_ipv4_src_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV4_SRC_MASK]
        self.assertEqual(self.target.ipv4_src_mask, None)
        
    # IPV4_DST
    def test_ipv4_dst_mask(self):
        self.assertEqual(self.target.ipv4_dst, "ipv4_dst")

    def test_ipv4_dst_None(self):
        del self.target._body[OFPFlowMatch.IPV4_DST]
        self.assertEqual(self.target.ipv4_dst, None)

    # IPV4_DST_MASK
    def test_ipv4_dst_mask_mask(self):
        self.assertEqual(self.target.ipv4_dst_mask, "ipv4_dst_mask")

    def test_ipv4_dst_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV4_DST_MASK]
        self.assertEqual(self.target.ipv4_dst_mask, None)

    # TCP_SRC
    def test_tcp_src(self):
        self.assertEqual(self.target.tcp_src, 31)

    def test_tcp_src_None(self):
        del self.target._body[OFPFlowMatch.TCP_SRC]
        self.assertEqual(self.target.tcp_src, None)

    # TCP_DST
    def test_tcp_dst(self):
        self.assertEqual(self.target.tcp_dst, 32)

    def test_tcp_dst_None(self):
        del self.target._body[OFPFlowMatch.TCP_DST]
        self.assertEqual(self.target.tcp_dst, None)

    # UDP_SRC
    def test_udp_src(self):
        self.assertEqual(self.target.udp_src, 33)

    def test_udp_src_None(self):
        del self.target._body[OFPFlowMatch.UDP_SRC]
        self.assertEqual(self.target.udp_src, None)
        
    # UDP_DST
    def test_udp_dst(self):
        self.assertEqual(self.target.udp_dst, 34)

    def test_udp_dst_None(self):
        del self.target._body[OFPFlowMatch.UDP_DST]
        self.assertEqual(self.target.udp_dst, None)

    # SCTP_SRC
    def test_sctp_src(self):
        self.assertEqual(self.target.sctp_src, 35)

    def test_sctp_src_None(self):
        del self.target._body[OFPFlowMatch.SCTP_SRC]
        self.assertEqual(self.target.sctp_src, None)

    # SCTP_DST
    def test_sctp_dst(self):
        self.assertEqual(self.target.sctp_dst, 36)

    def test_sctp_dst_None(self):
        del self.target._body[OFPFlowMatch.SCTP_DST]
        self.assertEqual(self.target.sctp_dst, None)

    # ICMPV4_TYPE
    def test_icmpv4_type(self):
        self.assertEqual(self.target.icmpv4_type, 37)

    def test_icmpv4_type_None(self):
        del self.target._body[OFPFlowMatch.ICMPV4_TYPE]
        self.assertEqual(self.target.icmpv4_type, None)

    # ICMPV4_CODE
    def test_icmpv4_code(self):
        self.assertEqual(self.target.icmpv4_code, 38)

    def test_icmpv4_code_None(self):
        del self.target._body[OFPFlowMatch.ICMPV4_CODE]
        self.assertEqual(self.target.icmpv4_code, None)

    # ARP_OP
    def test_arp_op(self):
        self.assertEqual(self.target.arp_op, 40)

    def test_arp_op_None(self):
        del self.target._body[OFPFlowMatch.ARP_OP]
        self.assertEqual(self.target.arp_op, None)

    # ARP_SPA
    def test_arp_spa(self):
        self.assertEqual(self.target.arp_spa, "arp_spa")

    def test_arp_spa_None(self):
        del self.target._body[OFPFlowMatch.ARP_SPA]
        self.assertEqual(self.target.arp_spa, None)

    # ARP_SPA_MASK
    def test_arp_spa_mask(self):
        self.assertEqual(self.target.arp_spa_mask, "arp_spa_mask")

    def test_arp_spa_mask_None(self):
        del self.target._body[OFPFlowMatch.ARP_SPA_MASK]
        self.assertEqual(self.target.arp_spa_mask, None)

    # ARP_TPA
    def test_arp_tpa(self):
        self.assertEqual(self.target.arp_tpa, "arp_tpa")

    def test_arp_tpa_None(self):
        del self.target._body[OFPFlowMatch.ARP_TPA]
        self.assertEqual(self.target.arp_tpa, None)

    # ARP_TPA_MASK
    def test_arp_tpa_mask(self):
        self.assertEqual(self.target.arp_tpa_mask, "arp_tpa_mask")

    def test_arp_tpa_mask_None(self):
        del self.target._body[OFPFlowMatch.ARP_TPA_MASK]
        self.assertEqual(self.target.arp_tpa_mask, None)

    # ARP_SHA
    def test_arp_sha(self):
        self.assertEqual(self.target.arp_sha, "arp_sha")

    def test_arp_sha_None(self):
        del self.target._body[OFPFlowMatch.ARP_SHA]
        self.assertEqual(self.target.arp_sha, None)

    # ARP_SHA_MASK
    def test_arp_sha_mask(self):
        self.assertEqual(self.target.arp_sha_mask, "arp_sha_mask")

    def test_arp_sha_mask_None(self):
        del self.target._body[OFPFlowMatch.ARP_SHA_MASK]
        self.assertEqual(self.target.arp_sha_mask, None)

    # ARP_THA
    def test_arp_tha(self):
        self.assertEqual(self.target.arp_tha, "arp_tha")

    def test_arp_tha_None(self):
        del self.target._body[OFPFlowMatch.ARP_THA]
        self.assertEqual(self.target.arp_tha, None)

    # ARP_THA_MASK
    def test_arp_tha_mask(self):
        self.assertEqual(self.target.arp_tha_mask, "arp_tha_mask")

    def test_arp_tha_mask_None(self):
        del self.target._body[OFPFlowMatch.ARP_THA_MASK]
        self.assertEqual(self.target.arp_tha_mask, None)

    # IPV6_SRC
    def test_ipv6_src(self):
        self.assertEqual(self.target.ipv6_src, "ipv6_src")

    def test_ipv6_src_None(self):
        del self.target._body[OFPFlowMatch.IPV6_SRC]
        self.assertEqual(self.target.ipv6_src, None)

    # IPV6_SRC_MASK
    def test_ipv6_src_mask(self):
        self.assertEqual(self.target.ipv6_src_mask, "ipv6_src_mask")

    def test_ipv6_src_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV6_SRC_MASK]
        self.assertEqual(self.target.ipv6_src_mask, None)

    # IPV6_DST
    def test_ipv6_dst(self):
        self.assertEqual(self.target.ipv6_dst, "ipv6_dst")

    def test_ipv6_dst_None(self):
        del self.target._body[OFPFlowMatch.IPV6_DST]
        self.assertEqual(self.target.ipv6_dst, None)

    # IPV6_DST_MASK
    def test_ipv6_dst_mask(self):
        self.assertEqual(self.target.ipv6_dst_mask, "ipv6_dst_mask")

    def test_ipv6_dst_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV6_DST_MASK]
        self.assertEqual(self.target.ipv6_dst_mask, None)

    # IPV6_FLABEL
    def test_ipv6_flabel(self):
        self.assertEqual(self.target.ipv6_flabel, 50)

    def test_ipv6_flabel_None(self):
        del self.target._body[OFPFlowMatch.IPV6_FLABEL]
        self.assertEqual(self.target.ipv6_flabel, None)

    # IPV6_FLABEL_MASK
    def test_ipv6_flabel_mask(self):
        self.assertEqual(self.target.ipv6_flabel_mask, 51)

    def test_ipv6_flabel_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV6_FLABEL_MASK]
        self.assertEqual(self.target.ipv6_flabel_mask, None)

    # ICMPV6_TYPE
    def test_icmpv6_type(self):
        self.assertEqual(self.target.icmpv6_type, 52)

    def test_icmpv6_type_None(self):
        del self.target._body[OFPFlowMatch.ICMPV6_TYPE]
        self.assertEqual(self.target.icmpv6_type, None)

    # ICMPV6_CODE
    def test_icmpv6_code(self):
        self.assertEqual(self.target.icmpv6_code, 53)

    def test_icmpv6_code_None(self):
        del self.target._body[OFPFlowMatch.ICMPV6_CODE]
        self.assertEqual(self.target.icmpv6_code, None)

    # IPV6_ND_TARGET
    def test_ipv6_nd_target(self):
        self.assertEqual(self.target.ipv6_nd_target, "ipv6_nd_target")

    def test_ipv6_nd_target_None(self):
        del self.target._body[OFPFlowMatch.IPV6_ND_TARGET]
        self.assertEqual(self.target.ipv6_nd_target, None)

    # IPV6_ND_SLL
    def test_ipv6_nd_sll(self):
        self.assertEqual(self.target.ipv6_nd_sll, "ipv6_nd_sll")

    def test_ipv6_nd_sll_None(self):
        del self.target._body[OFPFlowMatch.IPV6_ND_SLL]
        self.assertEqual(self.target.ipv6_nd_sll, None)
        
    # IPV6_ND_TLL
    def test_ipv6_nd_tll(self):
        self.assertEqual(self.target.ipv6_nd_tll, "ipv6_nd_tll")

    def test_ipv6_nd_tll_None(self):
        del self.target._body[OFPFlowMatch.IPV6_ND_TLL]
        self.assertEqual(self.target.ipv6_nd_tll, None)

    # MPLS_LABEL
    def test_mpls_label(self):
        self.assertEqual(self.target.mpls_label, 54)

    def test_mpls_label_None(self):
        del self.target._body[OFPFlowMatch.MPLS_LABEL]
        self.assertEqual(self.target.mpls_label, None)

    # MPLS_TC
    def test_mpls_tc(self):
        self.assertEqual(self.target.mpls_tc, 55)

    def test_mpls_tc_None(self):
        del self.target._body[OFPFlowMatch.MPLS_TC]
        self.assertEqual(self.target.mpls_tc, None)

    # MPLS_BOS
    def test_mpls_bos(self):
        self.assertEqual(self.target.mpls_bos, 56)

    def test_mpls_bos_None(self):
        del self.target._body[OFPFlowMatch.MPLS_BOS]
        self.assertEqual(self.target.mpls_bos, None)

    # PBB_ISID
    def test_pbb_isid(self):
        self.assertEqual(self.target.pbb_isid, 57)

    def test_pbb_isid_None(self):
        del self.target._body[OFPFlowMatch.PBB_ISID]
        self.assertEqual(self.target.pbb_isid, None)

    # PBB_ISID_MASK
    def test_pbb_isid_mask(self):
        self.assertEqual(self.target.pbb_isid_mask, 58)

    def test_pbb_isid_mask_None(self):
        del self.target._body[OFPFlowMatch.PBB_ISID_MASK]
        self.assertEqual(self.target.pbb_isid_mask, None)

    # TUNNEL_ID
    def test_tunnel_id(self):
        self.assertEqual(self.target.tunnel_id, 59)

    def test_tunnel_id_None(self):
        del self.target._body[OFPFlowMatch.TUNNEL_ID]
        self.assertEqual(self.target.tunnel_id, None)

    # TUNNEL_ID_MASK
    def test_tunnel_id_mask(self):
        self.assertEqual(self.target.tunnel_id_mask, 60)

    def test_tunnel_id_mask_None(self):
        del self.target._body[OFPFlowMatch.TUNNEL_ID_MASK]
        self.assertEqual(self.target.tunnel_id_mask, None)

    # IPV6_EXTHDR
    def test_ipv6_exthdr(self):
        self.assertEqual(self.target.ipv6_exthdr, 61)

    def test_ipv6_exthdr_None(self):
        del self.target._body[OFPFlowMatch.IPV6_EXTHDR]
        self.assertEqual(self.target.ipv6_exthdr, None)

    # IPV6_EXTHDR_MASK
    def test_ipv6_exthdr_mask(self):
        self.assertEqual(self.target.ipv6_exthdr_mask, 62)

    def test_ipv6_exthdr_mask_None(self):
        del self.target._body[OFPFlowMatch.IPV6_EXTHDR_MASK]
        self.assertEqual(self.target.ipv6_exthdr_mask, None)

    def test_create_from_packed_Not_None(self):
        self.value = { 
                    OFPFlowMatch.TYPE: "OFPFlowMatch",
                    OFPFlowMatch.IN_NODE: "ofp_in_node",
                    OFPFlowMatch.IN_PORT: "ofp_in_port",
                    OFPFlowMatch.IN_PHY_PORT: "in_phy_port",
                    OFPFlowMatch.METADATA: 11,
                    OFPFlowMatch.METADATA_MASK: 12,
                    OFPFlowMatch.ETH_SRC: "eth_src",
                    OFPFlowMatch.ETH_SRC_MASK: "eth_src_mask",
                    OFPFlowMatch.ETH_DST: "eth_dst",
                    OFPFlowMatch.ETH_DST_MASK: "eth_dst_mask",
                    OFPFlowMatch.VLAN_VID: 13,
                    OFPFlowMatch.VLAN_VID_MASK: 14,
                    OFPFlowMatch.VLAN_PCP: 15,
                    OFPFlowMatch.ETH_TYPE: 16,
                    OFPFlowMatch.IP_DSCP: 21,
                    OFPFlowMatch.IP_ECN: 22,
                    OFPFlowMatch.IP_PROTO: 23,
                    OFPFlowMatch.IPV4_SRC: "ipv4_src",
                    OFPFlowMatch.IPV4_SRC_MASK: "ipv4_src_mask",
                    OFPFlowMatch.IPV4_DST: "ipv4_dst",
                    OFPFlowMatch.IPV4_DST_MASK: "ipv4_dst_mask",
                    OFPFlowMatch.TCP_SRC: 31,
                    OFPFlowMatch.TCP_DST: 32,
                    OFPFlowMatch.UDP_SRC: 33,
                    OFPFlowMatch.UDP_DST: 34,
                    OFPFlowMatch.SCTP_SRC: 35,
                    OFPFlowMatch.SCTP_DST: 36,
                    OFPFlowMatch.ICMPV4_TYPE: 37,
                    OFPFlowMatch.ICMPV4_CODE: 38,
                    OFPFlowMatch.ARP_OP: 40,
                    OFPFlowMatch.ARP_SPA: "arp_spa",
                    OFPFlowMatch.ARP_SPA_MASK: "arp_spa_mask",
                    OFPFlowMatch.ARP_TPA: "arp_tpa",
                    OFPFlowMatch.ARP_TPA_MASK: "arp_tpa_mask",
                    OFPFlowMatch.ARP_SHA: "arp_sha",
                    OFPFlowMatch.ARP_SHA_MASK: "arp_sha_mask",
                    OFPFlowMatch.ARP_THA: "arp_tha",
                    OFPFlowMatch.ARP_THA_MASK: "arp_tha_mask",
                    OFPFlowMatch.IPV6_SRC: "ipv6_src",
                    OFPFlowMatch.IPV6_SRC_MASK: "ipv6_src_mask",
                    OFPFlowMatch.IPV6_DST: "ipv6_dst",
                    OFPFlowMatch.IPV6_DST_MASK: "ipv6_dst_mask",
                    OFPFlowMatch.IPV6_FLABEL: 50,
                    OFPFlowMatch.IPV6_FLABEL_MASK: 51,
                    OFPFlowMatch.ICMPV6_TYPE: 52,
                    OFPFlowMatch.ICMPV6_CODE: 53,
                    OFPFlowMatch.IPV6_ND_TARGET: "ipv6_nd_target",
                    OFPFlowMatch.IPV6_ND_SLL: "ipv6_nd_sll",
                    OFPFlowMatch.IPV6_ND_TLL: "ipv6_nd_tll",
                    OFPFlowMatch.MPLS_LABEL: 54,
                    OFPFlowMatch.MPLS_TC: 55,
                    OFPFlowMatch.MPLS_BOS: 56,
                    OFPFlowMatch.PBB_ISID: 57,
                    OFPFlowMatch.PBB_ISID_MASK: 58,
                    OFPFlowMatch.TUNNEL_ID: 59,
                    OFPFlowMatch.TUNNEL_ID_MASK: 60,
                    OFPFlowMatch.IPV6_EXTHDR: 61,
                    OFPFlowMatch.IPV6_EXTHDR_MASK: 62
                    }

        self.result = OFPFlowMatch.create_from_packed(self.value)

        self.assertEqual(self.result._body[OFPFlowMatch.TYPE], "OFPFlowMatch")
        self.assertEqual(self.result._body[OFPFlowMatch.IN_NODE], "ofp_in_node")
        self.assertEqual(self.result._body[OFPFlowMatch.IN_PORT], "ofp_in_port")

        self.assertEqual(self.result._body[OFPFlowMatch.IN_PHY_PORT], "in_phy_port")
        self.assertEqual(self.result._body[OFPFlowMatch.METADATA], 11)
        self.assertEqual(self.result._body[OFPFlowMatch.METADATA_MASK], 12)
        self.assertEqual(self.result._body[OFPFlowMatch.ETH_SRC], "eth_src")
        self.assertEqual(self.result._body[OFPFlowMatch.ETH_SRC_MASK], "eth_src_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.ETH_DST], "eth_dst")
        self.assertEqual(self.result._body[OFPFlowMatch.ETH_DST_MASK], "eth_dst_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.VLAN_VID], 13)
        self.assertEqual(self.result._body[OFPFlowMatch.VLAN_VID_MASK], 14)
        self.assertEqual(self.result._body[OFPFlowMatch.VLAN_PCP], 15)
        self.assertEqual(self.result._body[OFPFlowMatch.ETH_TYPE], 16)

        self.assertEqual(self.result._body[OFPFlowMatch.IP_DSCP], 21)
        self.assertEqual(self.result._body[OFPFlowMatch.IP_ECN], 22)
        self.assertEqual(self.result._body[OFPFlowMatch.IP_PROTO], 23)
        self.assertEqual(self.result._body[OFPFlowMatch.IPV4_SRC], "ipv4_src")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV4_SRC_MASK], "ipv4_src_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV4_DST], "ipv4_dst")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV4_DST_MASK], "ipv4_dst_mask")

        self.assertEqual(self.result._body[OFPFlowMatch.TCP_SRC], 31)
        self.assertEqual(self.result._body[OFPFlowMatch.TCP_DST], 32)
        self.assertEqual(self.result._body[OFPFlowMatch.UDP_SRC], 33)
        self.assertEqual(self.result._body[OFPFlowMatch.UDP_DST], 34)
        self.assertEqual(self.result._body[OFPFlowMatch.SCTP_SRC], 35)
        self.assertEqual(self.result._body[OFPFlowMatch.SCTP_DST], 36)
        self.assertEqual(self.result._body[OFPFlowMatch.ICMPV4_TYPE], 37)
        self.assertEqual(self.result._body[OFPFlowMatch.ICMPV4_CODE], 38)

        self.assertEqual(self.result._body[OFPFlowMatch.ARP_OP], 40)
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_SPA], "arp_spa")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_SPA_MASK], "arp_spa_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_TPA], "arp_tpa")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_TPA_MASK], "arp_tpa_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_SHA], "arp_sha")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_SHA_MASK], "arp_sha_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_THA], "arp_tha")
        self.assertEqual(self.result._body[OFPFlowMatch.ARP_THA_MASK], "arp_tha_mask")
        
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_SRC], "ipv6_src")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_SRC_MASK], "ipv6_src_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_DST], "ipv6_dst")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_DST_MASK], "ipv6_dst_mask")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_FLABEL], 50)
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_FLABEL_MASK], 51)
        self.assertEqual(self.result._body[OFPFlowMatch.ICMPV6_TYPE], 52)
        self.assertEqual(self.result._body[OFPFlowMatch.ICMPV6_CODE], 53)
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_ND_TARGET], "ipv6_nd_target")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_ND_SLL], "ipv6_nd_sll")
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_ND_TLL], "ipv6_nd_tll")

        self.assertEqual(self.result._body[OFPFlowMatch.MPLS_LABEL], 54)
        self.assertEqual(self.result._body[OFPFlowMatch.MPLS_TC], 55)
        self.assertEqual(self.result._body[OFPFlowMatch.MPLS_BOS], 56)
        self.assertEqual(self.result._body[OFPFlowMatch.PBB_ISID], 57)
        self.assertEqual(self.result._body[OFPFlowMatch.PBB_ISID_MASK], 58)
        self.assertEqual(self.result._body[OFPFlowMatch.TUNNEL_ID], 59)
        self.assertEqual(self.result._body[OFPFlowMatch.TUNNEL_ID_MASK], 60)
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_EXTHDR], 61)
        self.assertEqual(self.result._body[OFPFlowMatch.IPV6_EXTHDR_MASK], 62)

    def test_create_from_packed_None(self):
        self.value = {OFPFlowMatch.TYPE: "OFPFlowMatch",
                      OFPFlowMatch.IN_NODE: "0456",
                      OFPFlowMatch.IN_PORT: "0789"}

        self.result = OFPFlowMatch.create_from_packed(self.value)

        self.assertEqual(self.result._body, {OFPFlowMatch.TYPE: "OFPFlowMatch",
                                             OFPFlowMatch.IN_NODE: "0456",
                                             OFPFlowMatch.IN_PORT: "0789"})

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[OFPFlowMatch.TYPE], "OFPFlowMatch")
        self.assertEqual(self.result[OFPFlowMatch.IN_NODE], "ofp_in_node")
        self.assertEqual(self.result[OFPFlowMatch.IN_PORT], "ofp_in_port")

        self.assertEqual(self.result[OFPFlowMatch.IN_PHY_PORT], "in_phy_port")
        self.assertEqual(self.result[OFPFlowMatch.METADATA], 11)
        self.assertEqual(self.result[OFPFlowMatch.METADATA_MASK], 12)
        self.assertEqual(self.result[OFPFlowMatch.ETH_SRC], "eth_src")
        self.assertEqual(self.result[OFPFlowMatch.ETH_SRC_MASK], "eth_src_mask")
        self.assertEqual(self.result[OFPFlowMatch.ETH_DST], "eth_dst")
        self.assertEqual(self.result[OFPFlowMatch.ETH_DST_MASK], "eth_dst_mask")
        self.assertEqual(self.result[OFPFlowMatch.VLAN_VID], 13)
        self.assertEqual(self.result[OFPFlowMatch.VLAN_VID_MASK], 14)
        self.assertEqual(self.result[OFPFlowMatch.VLAN_PCP], 15)
        self.assertEqual(self.result[OFPFlowMatch.ETH_TYPE], 16)

        self.assertEqual(self.result[OFPFlowMatch.IP_DSCP], 21)
        self.assertEqual(self.result[OFPFlowMatch.IP_ECN], 22)
        self.assertEqual(self.result[OFPFlowMatch.IP_PROTO], 23)
        self.assertEqual(self.result[OFPFlowMatch.IPV4_SRC], "ipv4_src")
        self.assertEqual(self.result[OFPFlowMatch.IPV4_SRC_MASK], "ipv4_src_mask")
        self.assertEqual(self.result[OFPFlowMatch.IPV4_DST], "ipv4_dst")
        self.assertEqual(self.result[OFPFlowMatch.IPV4_DST_MASK], "ipv4_dst_mask")

        self.assertEqual(self.result[OFPFlowMatch.TCP_SRC], 31)
        self.assertEqual(self.result[OFPFlowMatch.TCP_DST], 32)
        self.assertEqual(self.result[OFPFlowMatch.UDP_SRC], 33)
        self.assertEqual(self.result[OFPFlowMatch.UDP_DST], 34)
        self.assertEqual(self.result[OFPFlowMatch.SCTP_SRC], 35)
        self.assertEqual(self.result[OFPFlowMatch.SCTP_DST], 36)
        self.assertEqual(self.result[OFPFlowMatch.ICMPV4_TYPE], 37)
        self.assertEqual(self.result[OFPFlowMatch.ICMPV4_CODE], 38)

        self.assertEqual(self.result[OFPFlowMatch.ARP_OP], 40)
        self.assertEqual(self.result[OFPFlowMatch.ARP_SPA], "arp_spa")
        self.assertEqual(self.result[OFPFlowMatch.ARP_SPA_MASK], "arp_spa_mask")
        self.assertEqual(self.result[OFPFlowMatch.ARP_TPA], "arp_tpa")
        self.assertEqual(self.result[OFPFlowMatch.ARP_TPA_MASK], "arp_tpa_mask")
        self.assertEqual(self.result[OFPFlowMatch.ARP_SHA], "arp_sha")
        self.assertEqual(self.result[OFPFlowMatch.ARP_SHA_MASK], "arp_sha_mask")
        self.assertEqual(self.result[OFPFlowMatch.ARP_THA], "arp_tha")
        self.assertEqual(self.result[OFPFlowMatch.ARP_THA_MASK], "arp_tha_mask")
        
        self.assertEqual(self.result[OFPFlowMatch.IPV6_SRC], "ipv6_src")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_SRC_MASK], "ipv6_src_mask")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_DST], "ipv6_dst")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_DST_MASK], "ipv6_dst_mask")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_FLABEL], 50)
        self.assertEqual(self.result[OFPFlowMatch.IPV6_FLABEL_MASK], 51)
        self.assertEqual(self.result[OFPFlowMatch.ICMPV6_TYPE], 52)
        self.assertEqual(self.result[OFPFlowMatch.ICMPV6_CODE], 53)
        self.assertEqual(self.result[OFPFlowMatch.IPV6_ND_TARGET], "ipv6_nd_target")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_ND_SLL], "ipv6_nd_sll")
        self.assertEqual(self.result[OFPFlowMatch.IPV6_ND_TLL], "ipv6_nd_tll")

        self.assertEqual(self.result[OFPFlowMatch.MPLS_LABEL], 54)
        self.assertEqual(self.result[OFPFlowMatch.MPLS_TC], 55)
        self.assertEqual(self.result[OFPFlowMatch.MPLS_BOS], 56)
        self.assertEqual(self.result[OFPFlowMatch.PBB_ISID], 57)
        self.assertEqual(self.result[OFPFlowMatch.PBB_ISID_MASK], 58)
        self.assertEqual(self.result[OFPFlowMatch.TUNNEL_ID], 59)
        self.assertEqual(self.result[OFPFlowMatch.TUNNEL_ID_MASK], 60)
        self.assertEqual(self.result[OFPFlowMatch.IPV6_EXTHDR], 61)
        self.assertEqual(self.result[OFPFlowMatch.IPV6_EXTHDR_MASK], 62)

if __name__ == '__main__':
    unittest.main()
