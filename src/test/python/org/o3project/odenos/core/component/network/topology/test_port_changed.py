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
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.port_changed import PortChanged


class PortChangedTest(unittest.TestCase):

    def setUp(self):
        port1 = Port('Port', '1', 'PortId1', 'NodeId', 'LinkId1',
                     'LinkId2', {})
        port2 = Port('Port', '1', 'PortId2', 'NodeId', 'LinkId2',
                     'LinkId1', {})
        self.target = PortChanged('NodeId', 'ID', PortChanged.Action.UPDATE,
                                  '1', port1, port2)

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._PortChanged__id, 'ID')
        self.assertEqual(self.target._PortChanged__node_id, 'NodeId')
        self.assertEqual(self.target._PortChanged__action, 'update')
        self.assertEqual(self.target._PortChanged__version, '1')
        self.assertEqual(self.target._PortChanged__prev.port_id, 'PortId1')
        self.assertEqual(self.target._PortChanged__curr.port_id, 'PortId2')

    def test_node_id(self):
        self.assertEqual(self.target.node_id, 'NodeId')

    def test_id(self):
        self.assertEqual(self.target.node_id, 'NodeId')

    def test_action(self):
        self.assertEqual(self.target.action, 'update')

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_prev(self):
        self.assertEqual(self.target.prev.port_id, 'PortId1')

    def test_curr(self):
        self.assertEqual(self.target.curr.port_id, 'PortId2')

    def test_create_from_packed_add(self):
        packed = {'node_id': 'NodeId', 'id': 'ID', 'action': 'add',
                  'version': '1',
                  'prev': None,
                  'curr': {'type': 'port', 'version': '1',
                           'port_id': 'PortId2', 'node_id': 'NodeId',
                           'out_link': 'LinkId2', 'in_link': 'LinkId1',
                           'attributes': {}}}
        result = PortChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, PortChanged.Action.ADD)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev, None)
        self.assertEqual(result.curr.port_id, 'PortId2')

    def test_create_from_packed_delete(self):
        packed = {'node_id': 'NodeId', 'id': 'ID', 'action': 'delete',
                  'prev': {'type': 'Port', 'version': '1',
                           'port_id': 'PortId1', 'node_id': 'NodeId',
                           'out_link': 'LinkId1', 'in_link': 'LinkId2',
                           'attributes': {}},
                  'curr': None}
        result = PortChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, PortChanged.Action.DELETE)
        self.assertEqual(result.version, '')
        self.assertEqual(result.prev.port_id, 'PortId1')
        self.assertEqual(result.curr, None)

    def test_create_from_packed_update(self):
        packed = {'node_id': 'NodeId', 'id': 'ID',
                  'action': 'update', 'version': '1',
                  'prev': {'type': 'Port', 'version': '1',
                           'port_id': 'PortId1', 'node_id': 'NodeId',
                           'out_link': 'LinkId1', 'in_link': 'LinkId2',
                           'attributes': {}},
                  'curr': {'type': 'port', 'version': '1',
                           'port_id': 'PortId2', 'node_id': 'NodeId',
                           'out_link': 'LinkId2', 'in_link': 'LinkId1',
                           'attributes': {}}}
        result = PortChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, PortChanged.Action.UPDATE)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev.port_id, 'PortId1')
        self.assertEqual(result.curr.port_id, 'PortId2')

if __name__ == "__main__":
    unittest.main()
