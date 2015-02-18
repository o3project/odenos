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
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.topology import Topology
from org.o3project.odenos.core.component.network.topology.topology_changed\
    import TopologyChanged


class TopologyChangedTest(unittest.TestCase):

    def setUp(self):
        link1 = Link('Link', '1', 'LinkId1', 'NodeId1',
                     'PortId1', 'NodeId2', 'PortId3', {})
        link2 = Link('Link', '1', 'LinkId2', 'NodeId2',
                     'PortId4', 'NodeId1', 'PortId2', {})
        node1 = Node('Node', '1', 'NodeId1', {}, {})
        node2 = Node('Node', '1', 'NodeId2', {}, {})
        topology1 = Topology('Topology', 'version',
                             {'NodeId1': node1}, {'LinkId1': link1})
        topology2 = Topology('Topology', 'version',
                             {'NodeId2': node2}, {'LinkId2': link2})
        self.target = TopologyChanged('1', topology1, topology2)

    def tearDown(self):
        pass

    def test_constructor(self):
        prev = self.target._TopologyChanged__prev
        curr = self.target._TopologyChanged__curr
        self.assertEqual(self.target._TopologyChanged__version, '1')
        self.assertEqual(
            prev.nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(
            curr.nodes['NodeId2'].node_id, 'NodeId2')
        self.assertEqual(
            prev.links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(
            curr.links['LinkId2'].link_id, 'LinkId2')

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_prev(self):
        self.assertEqual(self.target.prev.nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(self.target.prev.links['LinkId1'].link_id, 'LinkId1')

    def test_curr(self):
        self.assertEqual(self.target.curr.nodes['NodeId2'].node_id, 'NodeId2')
        self.assertEqual(self.target.curr.links['LinkId2'].link_id, 'LinkId2')

    def test_create_from_packed(self):
        packed = {'version': '1',
                  'prev': {'type': 'Topology', 'version': '1',
                           'nodes': {'NodeId1': {'type': 'Node',
                                                 'version': '1',
                                                 'node_id': 'NodeId1',
                                                 'ports': {},
                                                 'attributes': {}}},
                           'links': {'LinkId1': {'type': 'Link',
                                                 'version': '1',
                                                 'link_id': 'LinkId1',
                                                 'src_node': 'NodeId1',
                                                 'src_port': 'PortId1',
                                                 'dst_node': 'NodeId2',
                                                 'dst_port': 'PortId3',
                                                 'attributes': {}}}},
                  'curr': {'type': 'Topology', 'version': '1',
                           'nodes': {'NodeId2': {'type': 'Node',
                                                 'version': '1',
                                                 'node_id': 'NodeId2',
                                                 'ports': {},
                                                 'attributes': {}}},
                           'links': {'LinkId2': {'type': 'Link',
                                                 'version': '1',
                                                 'link_id': 'LinkId2',
                                                 'src_node': 'NodeId2',
                                                 'src_port': 'PortId4',
                                                 'dst_node': 'NodeId1',
                                                 'dst_port': 'PortId2',
                                                 'attributes': {}}}}
                  }
        result = TopologyChanged.create_from_packed(packed)
        self.assertEqual(result.version, '1')
        self.assertEqual(result.prev.nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(result.curr.nodes['NodeId2'].node_id, 'NodeId2')
        self.assertEqual(result.prev.links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(result.curr.links['LinkId2'].link_id, 'LinkId2')


if __name__ == "__main__":
    unittest.main()
