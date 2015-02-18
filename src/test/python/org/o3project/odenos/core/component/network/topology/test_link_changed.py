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
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.link_changed\
    import LinkChanged


class LinkChangedTest(unittest.TestCase):

    def setUp(self):
        link1 = Link('Link', '1', 'LinkId1', 'NodeId1',
                     'PortId1', 'NodeId2', 'PortId3', {})
        link2 = Link('Link', '1', 'LinkId2', 'NodeId2',
                     'PortId4', 'NodeId1', 'PortId2', {})
        self.target = LinkChanged('ID', LinkChanged.Action.UPDATE,
                                  '1', link1, link2)

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._LinkChanged__id, 'ID')
        self.assertEqual(self.target._LinkChanged__action,
                         LinkChanged.Action.UPDATE)
        self.assertEqual(self.target._LinkChanged__version, '1')
        self.assertEqual(self.target._LinkChanged__prev.link_id, 'LinkId1')
        self.assertEqual(self.target._LinkChanged__curr.link_id, 'LinkId2')

    def test_id(self):
        self.assertEqual(self.target.id, 'ID')

    def test_action(self):
        self.assertEqual(self.target.action, LinkChanged.Action.UPDATE)

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_prev(self):
        prev = self.target.prev
        self.assertEqual(prev.link_id, 'LinkId1')

    def test_curr(self):
        curr = self.target.curr
        self.assertEqual(curr.link_id, 'LinkId2')

    def test_create_from_packed_add(self):
        packed = {'id': 'ID', 'action': 'add', 'version': '1',
                  'prev': None,
                  'curr': {'type': 'Link', 'version': '1',
                           'link_id': 'LinkId2',
                           'src_node': 'NodeId2',
                           'src_port': 'PortId4',
                           'dst_node': 'NodeId1',
                           'dst_port': 'PortId2',
                           'attributes': {}}}
        result = LinkChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, LinkChanged.Action.ADD)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev, None)
        self.assertEqual(result.curr.link_id, 'LinkId2')

    def test_create_from_packed_delete(self):
        packed = {'id': 'ID', 'action': 'delete',
                  'prev': {'type': 'Link', 'version': '1',
                           'link_id': 'LinkId1',
                           'src_node': 'NodeId1',
                           'src_port': 'PortId1',
                           'dst_node': 'NodeId2',
                           'dst_port': 'PortId3',
                           'attributes': {}},
                  'curr': None}
        result = LinkChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, LinkChanged.Action.DELETE)
        self.assertEqual(result.version, '')
        self.assertEqual(result.prev.link_id, 'LinkId1')
        self.assertEqual(result.curr, None)

    def test_create_from_packed_update(self):
        packed = {'id': 'ID', 'action': 'update', 'version': '1',
                  'prev': {'type': 'Link', 'version': '1',
                           'link_id': 'LinkId1', 'src_node': 'NodeId1',
                           'src_port': 'PortId1', 'dst_node': 'NodeId2',
                           'dst_port': 'PortId3', 'attributes': {}},
                  'curr': {'type': 'Link', 'version': '1',
                           'link_id': 'LinkId2',
                           'src_node': 'NodeId2', 'src_port': 'PortId4',
                           'dst_node': 'NodeId1', 'dst_port': 'PortId2',
                           'attributes': {}}}
        result = LinkChanged.create_from_packed(packed)
        self.assertEqual(result.id, 'ID')
        self.assertEqual(result.action, LinkChanged.Action.UPDATE)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev.link_id, 'LinkId1')
        self.assertEqual(result.curr.link_id, 'LinkId2')

if __name__ == "__main__":
    unittest.main()
