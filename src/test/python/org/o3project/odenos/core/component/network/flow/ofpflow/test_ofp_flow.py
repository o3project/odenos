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

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow\
    import OFPFlow
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match\
    import OFPFlowMatch

import unittest


class OFPFlowTest(unittest.TestCase):

    def setUp(self):
        self.target = OFPFlow("OFPFlow", "ofp_version", "ofp_flow_id",
                              "ofp_owner", "ofp_enabled", "ofp_priority",
                              "ofp_status", "ofp_attributes", "ofp_matches",
                              "ofp_idle_timeout", "ofp_hard_timeout",
                              "ofp_path", "ofp_edge_actions")

    def tearDown(self):
        self.target = None

    def test_constractor(self):
        self.assertEqual(self.target._body[OFPFlow.TYPE],
                         "OFPFlow")
        self.assertEqual(self.target._body[OFPFlow.VERSION],
                         "ofp_version")
        self.assertEqual(self.target._body[OFPFlow.FLOW_ID], "ofp_flow_id")
        self.assertEqual(self.target._body[OFPFlow.OWNER], "ofp_owner")
        self.assertEqual(self.target._body[OFPFlow.ENABLED], "ofp_enabled")
        self.assertEqual(self.target._body[OFPFlow.PRIORITY], "ofp_priority")
        self.assertEqual(self.target._body[OFPFlow.STATUS], "ofp_status")
        self.assertEqual(self.target._body[OFPFlow.ATTRIBUTES],
                         "ofp_attributes")
        self.assertEqual(self.target._body[OFPFlow.MATCHES], "ofp_matches")
        self.assertEqual(self.target._body[OFPFlow.IDLE_TIMEOUT],
                         "ofp_idle_timeout")
        self.assertEqual(self.target._body[OFPFlow.HARD_TIMEOUT],
                         "ofp_hard_timeout")
        self.assertEqual(self.target._body[OFPFlow.PATH], "ofp_path")
        self.assertEqual(self.target._body[OFPFlow.EDGE_ACTIONS],
                         "ofp_edge_actions")

    def test_matches(self):
        self.assertEqual(self.target.matches, "ofp_matches")

    def test_idle_timeout(self):
        self.assertEqual(self.target.idle_timeout, "ofp_idle_timeout")

    def test_hard_timeout(self):
        self.assertEqual(self.target.hard_timeout, "ofp_hard_timeout")

    def test_path(self):
        self.assertEqual(self.target.path, "ofp_path")

    def test_edge_actions(self):
        self.assertEqual(self.target.edge_actions, "ofp_edge_actions")

    def test_create_from_packed(self):
        self.value = {OFPFlow.TYPE: "0123", OFPFlow.VERSION: "0456",
                      OFPFlow.FLOW_ID: "0789", OFPFlow.OWNER: "9870",
                      OFPFlow.ENABLED: "6540", OFPFlow.PRIORITY: "3210",
                      OFPFlow.STATUS: "0147", OFPFlow.ATTRIBUTES: "0258",
                      OFPFlow.MATCHES: [{"type": "0123",
                                         "in_node": "0456",
                                         "in_port": "0789"}],
                      OFPFlow.IDLE_TIMEOUT: "9630",
                      OFPFlow.HARD_TIMEOUT: "8520", OFPFlow.PATH: "7410",
                      OFPFlow.EDGE_ACTIONS: {"node_id":
                                             [{"type": "FlowActionOutput",
                                               "output": "0456"}]}
                      }
        self.result = OFPFlow.create_from_packed(self.value)
        self.maches_result = self.result._body["matches"][0].\
            packed_object()
        self.edge_action_result = self.result._body["edge_actions"]["node_id"][0].\
            packed_object()
        self.assertEqual(self.result._body[OFPFlow.TYPE],
                         "0123")
        self.assertEqual(self.result._body[OFPFlow.VERSION],
                         "0456")
        self.assertEqual(self.result._body[OFPFlow.FLOW_ID],
                         "0789")
        self.assertEqual(self.result._body[OFPFlow.OWNER],
                         "9870")
        self.assertEqual(self.result._body[OFPFlow.ENABLED],
                         "6540")
        self.assertEqual(self.result._body[OFPFlow.PRIORITY],
                         "3210")
        self.assertEqual(self.result._body[OFPFlow.STATUS],
                         "0147")
        self.assertEqual(self.result._body[OFPFlow.ATTRIBUTES],
                         "0258")
        self.assertEqual(self.maches_result,
                         {"type": "0123",
                          "in_node": "0456",
                          "in_port": "0789"})
        self.assertEqual(self.result._body[OFPFlow.IDLE_TIMEOUT],
                         "9630")
        self.assertEqual(self.result._body[OFPFlow.HARD_TIMEOUT],
                         "8520")
        self.assertEqual(self.result._body[OFPFlow.PATH],
                         "7410")
        self.assertEqual(self.edge_action_result,
                         {"type": "FlowActionOutput",
                          "output": "0456"})

    def test_packed_object(self):
        self.value = {OFPFlow.TYPE: "OFPFlow",
                      OFPFlow.VERSION: "ofp_version",
                      OFPFlow.FLOW_ID: "ofp_flow_id",
                      OFPFlow.OWNER: "ofp_owner",
                      OFPFlow.ENABLED: "ofp_enabled",
                      OFPFlow.PRIORITY: "ofp_priority",
                      OFPFlow.STATUS: "ofp_status",
                      OFPFlow.ATTRIBUTES: "ofp_attributes",
                      OFPFlow.MATCHES: [{"type": "0123",
                                         "in_node": "0456",
                                         "in_port": "0789"}],
                      OFPFlow.IDLE_TIMEOUT: "ofp_idle_timeout",
                      OFPFlow.HARD_TIMEOUT: "ofp_hard_timeout",
                      OFPFlow.PATH: "ofp_path",
                      OFPFlow.EDGE_ACTIONS: {"node_id":
                                             [{"type": "FlowActionOutput",
                                               "output": "0456"}]}
                      }

        self.result = self.target.create_from_packed(self.value)
        self.result = self.result.packed_object()
        self.assertEqual(self.result[OFPFlow.TYPE],
                         "OFPFlow")
        self.assertEqual(self.result[OFPFlow.VERSION],
                         "ofp_version")
        self.assertEqual(self.result[OFPFlow.FLOW_ID],
                         "ofp_flow_id")
        self.assertEqual(self.result[OFPFlow.OWNER],
                         "ofp_owner")
        self.assertEqual(self.result[OFPFlow.ENABLED],
                         "ofp_enabled")
        self.assertEqual(self.result[OFPFlow.PRIORITY],
                         "ofp_priority")
        self.assertEqual(self.result[OFPFlow.STATUS],
                         "ofp_status")
        self.assertEqual(self.result[OFPFlow.ATTRIBUTES],
                         "ofp_attributes")
        self.assertEqual(self.result[OFPFlow.MATCHES][0],
                         {"type": "0123",
                          "in_node": "0456",
                          "in_port": "0789"})
        self.assertEqual(self.result[OFPFlow.IDLE_TIMEOUT],
                         "ofp_idle_timeout")
        self.assertEqual(self.result[OFPFlow.HARD_TIMEOUT],
                         "ofp_hard_timeout")
        self.assertEqual(self.result[OFPFlow.PATH],
                         "ofp_path")
        self.assertEqual(self.result[OFPFlow.EDGE_ACTIONS]["node_id"][0],
                         {"type": "FlowActionOutput",
                          "output": "0456"})


if __name__ == '__main__':
    unittest.main()
