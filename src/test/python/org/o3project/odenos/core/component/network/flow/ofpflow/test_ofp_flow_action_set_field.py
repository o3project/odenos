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
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_action_set_field\
    import OFPFlowActionSetField

import unittest


class OFPFlowActionSetFieldTest(unittest.TestCase):

    def setUp(self):
        self.flow_match = OFPFlowMatch("OFPFlowMatch",
                                       "ofp_in_node", "ofp_in_port")
        self.target = OFPFlowActionSetField("OFPFlowActionSetField",
                                            self.flow_match)

    def tearDown(self):
        self.target = None

    def test_constractor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         "OFPFlowActionSetField")
        self.assertEqual(self.target._body[self.target.MATCH],
                         self.flow_match)

    def test_match(self):
        self.assertEqual(self.target.match, self.flow_match)

    def test_create_from_packed(self):
        flow_val = {OFPFlowMatch.TYPE: "OFPFlowMatch",
                    OFPFlowMatch.IN_NODE: "0456",
                    OFPFlowMatch.IN_PORT: "0789"}
        self.value = {self.target.TYPE: "OFPFlowActionSetField",
                      self.target.MATCH: flow_val}

        self.result = OFPFlowActionSetField.create_from_packed(self.value)

        self.assertEqual(self.result._body[self.target.TYPE],
                         "OFPFlowActionSetField")

        flow = self.result._body[self.target.MATCH]
        cmp_flow = OFPFlowMatch.create_from_packed(flow_val)
        self.assertEqual(flow.type, cmp_flow.type)
        self.assertEqual(flow.in_node, cmp_flow.in_node)
        self.assertEqual(flow.in_port, cmp_flow.in_port)

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[self.target.TYPE],
                         "OFPFlowActionSetField")
        comp_match = self.result[self.target.MATCH]
        self.assertEqual(comp_match, self.flow_match.packed_object())
        
    def test_create_from_packed_Version_NotNone_And_packed_object(self):
        self.MatchesKey = {"type": "OFPFlowMatch",
                            "in_node": "ofp_in_node",
                            "in_port": "ofp_in_port",
                            "in_phy_port": "in_phy_port",
                            "metadata": "metadata",
                            "metadata_mask": "metadata_mask",
                            "eth_src": "eth_src",
                            "eth_src_mask": "eth_src_mask",
                            "eth_dst": "eth_dst",
                            "eth_dst_mask": "eth_dst_mask",
                            "vlan_vid": "vlan_vid",
                            "vlan_vid_mask": "vlan_vid_mask",
                            "vlan_pcp": "vlan_pcp",
                            "eth_type": "eth_type",
                            "ip_dscp": "ip_dscp",
                            "ip_ecn": "ip_ecn",
                            "ip_proto": "ip_proto",
                            "ipv4_src": "ipv4_src",
                            "ipv4_src_mask": "ipv4_src_mask",
                            "ipv4_dst": "ipv4_dst",
                            "ipv4_dst_mask": "ipv4_dst_mask",
                            "tcp_src": "tcp_src",
                            "tcp_dst": "tcp_dst"
                            }
        self.Type = "OFPFlowActionSetField"
        self.value = {self.target.TYPE: self.Type, 
                      self.target.MATCH: self.MatchesKey}
        # create_from_packed_test
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.MATCH].packed_object(),
                         self.MatchesKey)

        # packed_object test
        self.result2 = self.result.packed_object()
        self.assertEqual(self.result2[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result2[self.target.MATCH],
                         self.MatchesKey)

if __name__ == '__main__':
    unittest.main()
