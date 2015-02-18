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

from org.o3project.odenos.core.component.network.flow.flow_changed\
    import FlowChanged
import unittest


class FlowChangedTest(unittest.TestCase):

    Id = "ID01"
    Action = "add"
    Version = "1"
    Matches = [{"type": "BasicFlowMatch",
                        "in_node": "NODE_ID_1",
                        "in_port": "ANY"}]
    Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                    "output": "ANY"}]}
    Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                  "req_latency": 20, "latency": 21}
    Path = ["LINK_ID1"]
    Prev = {"type": "BasicFlow", "version": "V01",
            "flow_id": "ID01", "owner": "Owner",
            "enabled": True, "priority": 256,
            "status": "none", "attributes": Attributes,
            "matches": Matches, "path": Path,
            "edge_actions": Edge_actions}

    Curr = {"type": "BasicFlow", "version": "V02",
            "flow_id": "ID02", "owner": "Owner",
            "enabled": True, "priority": 256,
            "status": "none", "attributes": Attributes,
            "matches": Matches, "path": Path,
            "edge_actions": Edge_actions}

    def setUp(self):
        self.target = FlowChanged(self.Id, self.Action,
                                  self.Version, self.Prev, self.Curr)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._FlowChanged__id, self.Id)
        self.assertEqual(self.target._FlowChanged__action, self.Action)
        self.assertEqual(self.target._FlowChanged__version, self.Version)
        self.assertEqual(self.target._FlowChanged__prev, self.Prev)
        self.assertEqual(self.target._FlowChanged__curr, self.Curr)

    def test_id_property(self):
        self.assertEqual(self.target.id, self.Id)

    def test_action_property(self):
        self.assertEqual(self.target.action, self.Action)

    def test_version_property(self):
        self.assertEqual(self.target.version, self.Version)

    def test_prev_property(self):
        self.assertEqual(self.target.prev, self.Prev)

    def test_curr_property(self):
        self.assertEqual(self.target.curr, self.Curr)

    def test_create_from_packed_Action_ADD(self):
        Action = "add"
        Curr = self.Curr
        Prev = None
        Version = self.Version
        self.value = {self.target.ID: self.Id, self.target.ACTION: Action,
                      self.target.VERSION: Version,  self.target.PREV: Prev,
                      self.target.CURR: Curr}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result.id, self.Id)
        self.assertEqual(self.result.action, Action)
        self.assertEqual(self.result.version, Version)
        self.assertEqual(self.result.prev, Prev)
        self.assertEqual(self.result.curr.packed_object(), Curr)

    def test_create_from_packed_Action_DELETE(self):
        Action = "delete"
        Curr = None
        Prev = self.Prev
        Version = ''
        self.value = {self.target.ID: self.Id, self.target.ACTION: Action,
                      self.target.VERSION: Version,  self.target.PREV: Prev,
                      self.target.CURR: Curr}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result.id, self.Id)
        self.assertEqual(self.result.action, Action)
        self.assertEqual(self.result.version, Version)
        self.assertEqual(self.result.prev.packed_object(), Prev)
        self.assertEqual(self.result.curr, Curr)

    def test_create_from_packed_Action_UPDATE(self):
        Action = "update"
        Curr = self.Curr
        Prev = self.Prev
        Version = self.Version
        self.value = {self.target.ID: self.Id, self.target.ACTION: Action,
                      self.target.VERSION: Version,  self.target.PREV: Prev,
                      self.target.CURR: Curr}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result.id, self.Id)
        self.assertEqual(self.result.action, Action)
        self.assertEqual(self.result.version, Version)
        self.assertEqual(self.result.prev.packed_object(), Prev)
        self.assertEqual(self.result.curr.packed_object(), Curr)

if __name__ == '__main__':
    unittest.main()

