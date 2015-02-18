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

from org.o3project.odenos.core.component.network.flow.basic.basic_flow\
    import BasicFlow
import unittest


class BasicFlowTest(unittest.TestCase):
    Type = "BasicFlow"
    Version = "v01"
    Flow_id = "Id01"
    Owner = "Owner"
    Enabled = True
    Priority = 65535
    Status = "none"
    Attributes = {"admin_status": "UP", "oper_status": "UP",
                  "physical_id": "DPIS", "vendor": "Vendor"}
    # BaseFlowMatch key P1:type P2:in_node P3:in_port
#    Matches = [["BasicFlowMatch", "NODE_ID_1", "ANY"]]
    Matches = [["BasicFlowMatch", "NODE_ID_1", "ANY"]]
    Path = ["LINK_ID1"]
    Edge_actions = {"NODE_ID_1": ["FlowActionOutput", "ANY"]}

    def setUp(self):

        self.target = BasicFlow(self.Type, self.Version, self.Flow_id,
                                self.Owner, self.Enabled, self.Priority,
                                self.Status, self.Attributes,
                                self.Matches, self.Path, self.Edge_actions)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.target._body[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.target._body[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.target._body[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.target._body[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.target._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.target._body[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.target._body[self.target.ATTRIBUTES],
                         self.Attributes)
        self.assertEqual(self.target._body[self.target.MATCHES],
                         self.Matches)
        self.assertEqual(self.target._body[self.target.PATH],
                         self.Path)
        self.assertEqual(self.target._body[self.target.EDGE_ACTIONS],
                         self.Edge_actions)

    def test_matches_property(self):
        self.assertEqual(self.target.matches, self.Matches)

    def test_path_property(self):
        self.assertEqual(self.target.path, self.Path)

    def test_edge_actions__property(self):
        self.assertEqual(self.target.edge_actions, self.Edge_actions)

    def test_create_from_packed_Version_NotNone_And_packed_object(self):
        self.MatchesKey = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        self.Edge_actionsKey = {"NODE_ID_1": [{"type": "FlowActionOutput",
                                               "output": "ANY"}]}
        self.value = {"type": self.Type, "version": self.Version,
                      "flow_id": self.Flow_id, "owner": self.Owner,
                      "enabled": self.Enabled, "priority": self.Priority,
                      "status": self.Status, "attributes": self.Attributes,
                      "matches": self.MatchesKey, "path": self.Path,
                      "edge_actions": self.Edge_actionsKey}
        # create_from_packed_test
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result._body[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.result._body[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.result._body[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.result._body[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result._body[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         self.Attributes)
        self.assertEqual(self.result._body["matches"][0].packed_object(),
                         self.MatchesKey[0])
        self.assertEqual(self.result._body[self.target.PATH],
                         self.Path)
        self.assertEqual(self.result._body["edge_actions"]["NODE_ID_1"][0].
                         packed_object(),
                         self.Edge_actionsKey["NODE_ID_1"][0])
        # packed_object test
        self.result2 = self.result.packed_object()
        self.assertEqual(self.result2[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result2[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.result2[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.result2[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.result2[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.result2[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result2[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.result2[self.target.ATTRIBUTES],
                         self.Attributes)
        self.assertEqual(self.result2[self.target.MATCHES],
                         self.MatchesKey)
        self.assertEqual(self.result2[self.target.PATH],
                         self.Path)
        self.assertEqual(self.result2[self.target.EDGE_ACTIONS],
                         self.Edge_actionsKey)

    def test_create_from_packed_Version_None(self):
        self.MatchesKey = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        self.Edge_actionsKey = {"NODE_ID_1": [{"type": "FlowActionOutput",
                                               "output": "ANY"}]}
        self.value = {"type": self.Type,
                      "flow_id": self.Flow_id, "owner": self.Owner,
                      "enabled": self.Enabled, "priority": self.Priority,
                      "status": self.Status, "attributes": self.Attributes,
                      "matches": self.MatchesKey, "path": self.Path,
                      "edge_actions": self.Edge_actionsKey}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result._body[self.target.VERSION],
                         None)
        self.assertEqual(self.result._body[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.result._body[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.result._body[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result._body[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         self.Attributes)
        self.assertEqual(self.result._body["matches"][0].packed_object(),
                         self.MatchesKey[0])
        self.assertEqual(self.result._body[self.target.PATH],
                         self.Path)
        self.assertEqual(self.result._body["edge_actions"]["NODE_ID_1"][0].
                         packed_object(),
                         self.Edge_actionsKey["NODE_ID_1"][0])

if __name__ == '__main__':
    unittest.main()
