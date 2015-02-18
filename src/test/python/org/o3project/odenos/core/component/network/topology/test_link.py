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


class LinkTest(unittest.TestCase):

    def setUp(self):
        self.target = Link('Link', '1', 'LinkId', 'SrcNodeId', 'SrcPortId',
                           'DstNodeId', 'DstPortId', {'Key': 'Val'})

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._body[Link.TYPE], 'Link')
        self.assertEqual(self.target._body[Link.VERSION], '1')
        self.assertEqual(self.target._body[Link.LINK_ID], 'LinkId')
        self.assertEqual(self.target._body[Link.SRC_NODE], 'SrcNodeId')
        self.assertEqual(self.target._body[Link.SRC_PORT], 'SrcPortId')
        self.assertEqual(self.target._body[Link.DST_NODE], 'DstNodeId')
        self.assertEqual(self.target._body[Link.DST_PORT], 'DstPortId')
        self.assertEqual(self.target._body[Link.ATTRIBUTES]['Key'], 'Val')

    def test_type(self):
        self.assertEqual(self.target.type, 'Link')

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_link_id(self):
        self.assertEqual(self.target.link_id, 'LinkId')

    def test_src_node(self):
        self.assertEqual(self.target.src_node, 'SrcNodeId')

    def test_src_port(self):
        self.assertEqual(self.target.src_port, 'SrcPortId')

    def test_dst_node(self):
        self.assertEqual(self.target.dst_node, 'DstNodeId')

    def test_dst_port(self):
        self.assertEqual(self.target.dst_port, 'DstPortId')

    def test_attributes(self):
        result = self.target.attributes
        self.assertEqual(len(result), 1)
        self.assertEqual(result['Key'], 'Val')

    def test_create_from_packed(self):
        packed = self.target.packed_object()
        result = Link.create_from_packed(packed)
        self.assertEqual(result.type, 'Link')
        self.assertEqual(result.version, '1')
        self.assertEqual(result.link_id, 'LinkId')
        self.assertEqual(result.src_node, 'SrcNodeId')
        self.assertEqual(result.src_port, 'SrcPortId')
        self.assertEqual(result.dst_node, 'DstNodeId')
        self.assertEqual(result.dst_port, 'DstPortId')
        self.assertEqual(len(result.attributes), 1)
        self.assertEqual(result.attributes['Key'], 'Val')

    def test_create_from_packed_without_version(self):
        packed = {'type': 'Link', 'link_id': 'LinkId',
                  'src_node': 'SrcNodeId', 'src_port': 'SrcPortId',
                  'dst_node': 'DstNodeId', 'dst_port': 'DstPortId',
                  'attributes': {'Key': 'Val'}}
        result = Link.create_from_packed(packed)
        self.assertEqual(result.type, 'Link')
        self.assertEqual(result.version, None)
        self.assertEqual(result.link_id, 'LinkId')
        self.assertEqual(result.src_node, 'SrcNodeId')
        self.assertEqual(result.src_port, 'SrcPortId')
        self.assertEqual(result.dst_node, 'DstNodeId')
        self.assertEqual(result.dst_port, 'DstPortId')
        self.assertEqual(len(result.attributes), 1)
        self.assertEqual(result.attributes['Key'], 'Val')

    def test_packed_object(self):
        result = self.target.packed_object()
        self.assertEqual(result, {'type': 'Link', 'version': '1',
                                  'link_id': 'LinkId',
                                  'src_node': 'SrcNodeId',
                                  'src_port': 'SrcPortId',
                                  'dst_node': 'DstNodeId',
                                  'dst_port': 'DstPortId',
                                  'attributes': {'Key': 'Val'}})

if __name__ == "__main__":
    unittest.main()
