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

from org.o3project.odenos.core.component.network.flow.flow_set import FlowSet
import unittest


class FlowSetTest(unittest.TestCase):

    Type = "FlowSet"
    Version = "v1"
    Priority = {256: ["Id01"]}
    Matches = [{"type": "BasicFlowMatch",
                        "in_node": "NODE_ID_1",
                        "in_port": "ANY"}]
    Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                    "output": "ANY"}]}
    Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                  "req_latency": 20, "latency": 21}
    Path = ["LINK_ID1"]
    Flow = {"type": "BasicFlow", "version": "V01",
            "flow_id": "Id01", "owner": "Owner",
            "enabled": True, "priority": 256,
            "status": "none", "attributes": Attributes,
            "matches": Matches, "path": Path,
            "edge_actions": Edge_actions}
    Flows = {"Id01": Flow}

    def setUp(self):
        self.target = FlowSet(self.Type, self.Version,
                              self.Priority, self.Flows)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.target._body[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.target._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.target._body[self.target.FLOWS],
                         self.Flows)

    def test_type_property(self):
        self.assertEqual(self.target.type,
                         self.Type)

    def test_version_property(self):
        self.assertEqual(self.target.version,
                         self.Version)

    def test_priority_property(self):
        self.assertEqual(self.target.priority,
                         self.Priority)

    def test_flows_property(self):
        self.assertEqual(self.target.flows,
                         self.Flows)

    def test_create_from_packed_Version_NotNone(self):
        self.value = {"type": self.Type, "version": self.Version,
                      "priority": self.Priority, "flows": self.Flows}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result._body[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result._body["flows"]["Id01"].packed_object(),
                         self.Flow)

    def test_create_from_packed_Version_None(self):
        self.value = {"type": self.Type,
                      "priority": self.Priority, "flows": self.Flows}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result._body[self.target.VERSION],
                         None)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result._body["flows"]["Id01"].packed_object(),
                         self.Flow)

    def test_packed_object(self):
        self.value = {"type": self.Type, "version": self.Version,
                      "priority": self.Priority, "flows": self.Flows}
        self.result = self.target.create_from_packed(self.value)
        self.result2 = self.result.packed_object()

        self.assertEqual(self.result2[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result2[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.result2[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result2[self.target.FLOWS],
                         self.Flows)
if __name__ == '__main__':
    unittest.main()
