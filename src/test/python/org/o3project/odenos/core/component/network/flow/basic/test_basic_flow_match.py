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
import unittest


class BasicFlowMatchTest(unittest.TestCase):
    Type = "BasicFlowMatch"
    In_node = "node_id"
    In_port = "ANY"

    def setUp(self):
        self.target = BasicFlowMatch(self.Type, self.In_node, self.In_port)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE], self.Type)
        self.assertEqual(self.target._body[self.target.IN_NODE], self.In_node)
        self.assertEqual(self.target._body[self.target.IN_PORT], self.In_port)

    def test_type_property(self):
        self.assertEqual(self.target.type, self.Type)

    def test_in_node_property(self):
        self.assertEqual(self.target.in_node, self.In_node)

    def test_in_node_setter(self):
        self.assertEqual(self.target._body[self.target.IN_NODE],
                         self.In_node)
        self.target.in_node = "node_id_second"
        self.assertEqual(self.target._body[self.target.IN_NODE],
                         "node_id_second")

    def test_in_port_port_NotNone(self):
        self.assertEqual(self.target.in_port, self.In_port)

    def test_in_port_port_None(self):
        self.port_None_target = BasicFlowMatch(self.Type, self.In_node, None)
        self.assertIsNone(self.port_None_target.in_port)

    def test_in_port_port_setter(self):
        self.assertEqual(self.target._body[self.target.IN_PORT], self.In_port)
        self.target.in_port = "port_id"
        self.assertEqual(self.target._body[self.target.IN_PORT],
                         "port_id")

    def test_create_from_packed_port_NotNone(self):
        self.value = {"type": self.Type,
                      "in_node": "In_node01",
                      "in_port": "In_port01"}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE], self.Type)
        self.assertEqual(self.result._body[self.target.IN_NODE], "In_node01")
        self.assertEqual(self.result._body[self.target.IN_PORT], "In_port01")

    def test_create_from_packed_port_None(self):
        self.value = {"type": self.Type,
                      "in_node": "In_node01"}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body, self.value)

    def test_packed_object(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result[self.target.TYPE], self.Type)
        self.assertEqual(self.result[self.target.IN_NODE], self.In_node)
        self.assertEqual(self.result[self.target.IN_PORT], self.In_port)

if __name__ == '__main__':
    unittest.main()
