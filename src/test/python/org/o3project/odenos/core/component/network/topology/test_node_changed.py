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

import unittest
from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.node_changed\
    import NodeChanged


class NodeChangedTest(unittest.TestCase):

    def setUp(self):
        node1 = Node('Node', '1', 'NodeId1', {}, {})
        node2 = Node('Node', '1', 'NodeId2', {}, {})
        self.target = NodeChanged('ID', NodeChanged.Action.ADD,
                                  '1', node1, node2)

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._NodeChanged__id, 'ID')
        self.assertEqual(self.target._NodeChanged__action,
                         NodeChanged.Action.ADD)
        self.assertEqual(self.target._NodeChanged__version, '1')
        self.assertEqual(self.target._NodeChanged__prev.node_id, 'NodeId1')
        self.assertEqual(self.target._NodeChanged__curr.node_id, 'NodeId2')

    def test_id(self):
        self.assertEqual(self.target.id, 'ID')

    def test_action(self):
        self.assertEqual(self.target.action, NodeChanged.Action.ADD)

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_prev(self):
        prev = self.target.prev
        self.assertEqual(prev.node_id, 'NodeId1')

    def test_curr(self):
        curr = self.target.curr
        self.assertEqual(curr.node_id, 'NodeId2')

    def test_create_from_packed_add(self):
        packed = {'id': 'ID', 'action': 'add', 'version': '1', 'prev': None,
                  'curr': {'type': 'Node', 'version': '1',
                           'node_id': 'NodeId2', 'ports': {},
                           'attributes': {}}}
        result = NodeChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, NodeChanged.Action.ADD)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev, None)
        self.assertEqual(result.curr.node_id, 'NodeId2')

    def test_create_from_packed_delete(self):
        packed = {'id': 'ID', 'action': 'delete',
                  'prev': {'type': 'Node', 'version': '1',
                           'node_id': 'NodeId1', 'ports': {},
                           'attributes': {}},
                  'curr': None}
        result = NodeChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, NodeChanged.Action.DELETE)
        self.assertEqual(result.version, '')
        self.assertEqual(result.prev.node_id, 'NodeId1')
        self.assertEqual(result.curr, None)

    def test_create_from_packed_update(self):
        packed = {'id': 'ID', 'action': 'update', 'version': '1',
                  'prev': {'type': 'Node', 'version': '1',
                           'node_id': 'NodeId1', 'ports': {},
                           'attributes': {}},
                  'curr': {'type': 'Node', 'version': '1',
                           'node_id': 'NodeId2', 'ports': {},
                           'attributes': {}}}
        result = NodeChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, NodeChanged.Action.UPDATE)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev.node_id, 'NodeId1')
        self.assertEqual(result.curr.node_id, 'NodeId2')

if __name__ == "__main__":
    unittest.main()
