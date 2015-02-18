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
from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.topology import Topology


class TopologyTest(unittest.TestCase):

    def setUp(self):
        port1 = Port('Port', '1', 'PortId1', 'NodeId1',
                     'LinkId1', None, {})
        port2 = Port('Port', '1', 'PortId2', 'NodeId1', None, 'LinkId2', {})
        port3 = Port('Port', '1', 'PortId3', 'NodeId2', None, 'LinkId1', {})
        port4 = Port('Port', '1', 'PortId4', 'NodeId2', 'LinkId2', None, {})
        node1 = Node('Node', '1', 'NodeId1',
                     {'PortId1': port1, 'PortId2': port2}, {})
        node2 = Node('Node', '1', 'NodeId2',
                     {'PortId3': port3, 'PortId4': port4}, {})
        link1 = Link('Link', '1', 'LinkId1', 'NodeId1',
                     'PortId1', 'NodeId2', 'PortId3', {})
        link2 = Link('Link', '1', 'LinkId2', 'NodeId2',
                     'PortId4', 'NodeId1', 'PortId2', {})
        self.nodes = {'NodeId1': node1, 'NodeId2': node2}
        self.links = {'LinkId1': link1, 'LinkId2': link2}
        self.target = Topology('Topology', '1', self.nodes, self.links)

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._body[Topology.TYPE], 'Topology')
        self.assertEqual(self.target._body[Topology.VERSION], '1')
        nodes = self.target._body[Topology.NODES]
        self.assertEqual(len(nodes), 2)
        self.assertEqual(nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(nodes['NodeId2'].node_id, 'NodeId2')
        links = self.target._body[Topology.LINKS]
        self.assertEqual(len(links), 2)
        self.assertEqual(links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(links['LinkId2'].link_id, 'LinkId2')

    def test_type(self):
        self.assertEqual(self.target.type, 'Topology')

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_nodes(self):
        nodes = self.target.nodes
        self.assertEqual(len(nodes), 2)
        self.assertEqual(nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(nodes['NodeId2'].node_id, 'NodeId2')

    def test_links(self):
        links = self.target.links
        self.assertEqual(len(links), 2)
        self.assertEqual(links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(links['LinkId2'].link_id, 'LinkId2')

    def test_create_from_packed(self):
        packed = self.target.packed_object()
        result = Topology.create_from_packed(packed)
        self.assertEqual(result.type, 'Topology')
        self.assertEqual(result.version, '1')
        nodes = result.nodes
        self.assertEqual(len(nodes), 2)
        self.assertEqual(nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(nodes['NodeId2'].node_id, 'NodeId2')
        links = result.links
        self.assertEqual(len(links), 2)
        self.assertEqual(links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(links['LinkId2'].link_id, 'LinkId2')

    def test_create_from_packed_without_version(self):
        packed = {'type': 'Topology',
                  'nodes': {
                      'NodeId1': {'type': 'Node', 'version': '1',
                                  'node_id': 'NodeId1',
                                  'ports': {
                                      'PortId1': {'type': 'Port',
                                                  'version': '1',
                                                  'port_id': 'PortId1',
                                                  'node_id': 'NodeId1',
                                                  'out_link': 'LinkId1',
                                                  'in_link': None,
                                                  'attributes': {}},
                                      'PortId2': {'type': 'Port',
                                                  'version': '1',
                                                  'port_id': 'PortId2',
                                                  'node_id': 'NodeId1',
                                                  'out_link': None,
                                                  'in_link': 'LinkId2',
                                                  'attributes': {}}
                                      },
                                  'attributes': {}},
                      'NodeId2': {'type': 'Node', 'version': '1',
                                  'node_id': 'NodeId2',
                                  'ports': {
                                      'PortId3': {'type': 'Port',
                                                  'version': '1',
                                                  'port_id': 'PortId3',
                                                  'node_id': 'NodeId2',
                                                  'out_link': None,
                                                  'in_link': 'LinkId1',
                                                  'attributes': {}},
                                      'PortId4': {'type': 'Port',
                                                  'version': '1',
                                                  'port_id': 'PortId4',
                                                  'node_id': 'NodeId2',
                                                  'out_link': 'LinkId2',
                                                  'in_link': None,
                                                  'attributes': {}}
                                      },
                                  'attributes': {}}
                      },
                  'links': {
                      'LinkId1': {'type': 'Link', 'version': '1',
                                  'link_id': 'LinkId1', 'src_node': 'NodeId1',
                                  'src_port': 'PortId1', 'dst_node': 'NodeId2',
                                  'dst_port': 'PortId3', 'attributes': {}},
                      'LinkId2': {'type': 'Link', 'version': '1',
                                  'link_id': 'LinkId2',
                                  'src_node': 'NodeId2',
                                  'src_port': 'PortId4',
                                  'dst_node': 'NodeId1',
                                  'dst_port': 'PortId2', 'attributes': {}}
                      }}
        result = Topology.create_from_packed(packed)
        self.assertEqual(result.type, 'Topology')
        self.assertEqual(result.version, None)
        nodes = result.nodes
        self.assertEqual(len(nodes), 2)
        self.assertEqual(nodes['NodeId1'].node_id, 'NodeId1')
        self.assertEqual(nodes['NodeId2'].node_id, 'NodeId2')
        links = result.links
        self.assertEqual(len(links), 2)
        self.assertEqual(links['LinkId1'].link_id, 'LinkId1')
        self.assertEqual(links['LinkId2'].link_id, 'LinkId2')

    def test_packed_object(self):
        result = self.target.packed_object()
        self.assertEqual(
            result, {'type': 'Topology', 'version': '1',
                     'nodes': {
                         'NodeId1': {'type': 'Node', 'version': '1',
                                     'node_id': 'NodeId1',
                                     'ports': {
                                         'PortId1': {'type': 'Port',
                                                     'version': '1',
                                                     'port_id': 'PortId1',
                                                     'node_id': 'NodeId1',
                                                     'out_link': 'LinkId1',
                                                     'in_link': None,
                                                     'attributes': {}},
                                         'PortId2': {'type': 'Port',
                                                     'version': '1',
                                                     'port_id': 'PortId2',
                                                     'node_id': 'NodeId1',
                                                     'out_link': None,
                                                     'in_link': 'LinkId2',
                                                     'attributes': {}}
                                         },
                                     'attributes': {}},
                         'NodeId2': {'type': 'Node',
                                     'version': '1',
                                     'node_id': 'NodeId2',
                                     'ports': {
                                         'PortId3': {'type': 'Port',
                                                     'version': '1',
                                                     'port_id': 'PortId3',
                                                     'node_id': 'NodeId2',
                                                     'out_link': None,
                                                     'in_link': 'LinkId1',
                                                     'attributes': {}},
                                         'PortId4': {'type': 'Port',
                                                     'version': '1',
                                                     'port_id': 'PortId4',
                                                     'node_id': 'NodeId2',
                                                     'out_link': 'LinkId2',
                                                     'in_link': None,
                                                     'attributes': {}}
                                         },
                                     'attributes': {}}
                     },
                     'links': {
                         'LinkId1': {'type': 'Link', 'version': '1',
                                     'link_id': 'LinkId1',
                                     'src_node': 'NodeId1',
                                     'src_port': 'PortId1',
                                     'dst_node': 'NodeId2',
                                     'dst_port': 'PortId3',
                                     'attributes': {}},
                         'LinkId2': {'type': 'Link', 'version': '1',
                                     'link_id': 'LinkId2',
                                     'src_node': 'NodeId2',
                                     'src_port': 'PortId4',
                                     'dst_node': 'NodeId1',
                                     'dst_port': 'PortId2',
                                     'attributes': {}}
                     }})

if __name__ == "__main__":
    unittest.main()
