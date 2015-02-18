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


class PortTest(unittest.TestCase):

    def setUp(self):
        self.target = Port('Port', '1', 'PortId', 'NodeId', 'OutLink',
                           'InLink', {'Key': 'Val'})

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._body[Port.TYPE], 'Port')
        self.assertEqual(self.target._body[Port.VERSION], '1')
        self.assertEqual(self.target._body[Port.PORT_ID], 'PortId')
        self.assertEqual(self.target._body[Port.NODE_ID], 'NodeId')
        self.assertEqual(self.target._body[Port.OUT_LINK], 'OutLink')
        self.assertEqual(self.target._body[Port.IN_LINK], 'InLink')
        self.assertEqual(self.target._body[Port.ATTRIBUTES]['Key'], 'Val')

    def test_type(self):
        self.assertEqual(self.target.type, 'Port')

    def test_version(self):
        self.assertEqual(self.target.version, '1')

    def test_port_id(self):
        self.assertEqual(self.target.port_id, 'PortId')

    def test_node_id(self):
        self.assertEqual(self.target.node_id, 'NodeId')

    def test_out_link(self):
        self.assertEqual(self.target.out_link, 'OutLink')

    def test_in_link(self):
        self.assertEqual(self.target.in_link, 'InLink')

    def test_attributes(self):
        result = self.target.attributes
        self.assertEqual(len(result), 1)
        self.assertEqual(result['Key'], 'Val')

    def test_create_from_packed(self):
        packed = self.target.packed_object()
        result = Port.create_from_packed(packed)
        self.assertEqual(result.type, 'Port')
        self.assertEqual(result.version, '1')
        self.assertEqual(result.port_id, 'PortId')
        self.assertEqual(result.node_id, 'NodeId')
        self.assertEqual(result.out_link, 'OutLink')
        self.assertEqual(result.in_link, 'InLink')
        self.assertEqual(len(result.attributes), 1)
        self.assertEqual(result.attributes['Key'], 'Val')

    def test_create_from_packed_without_version(self):
        packed = {'type': 'Port', 'port_id': 'PortId', 'node_id': 'NodeId',
                  'out_link': 'OutLink', 'in_link': 'InLink',
                  'attributes': {'Key': 'Val'}}
        result = Port.create_from_packed(packed)
        self.assertEqual(result.type, 'Port')
        self.assertEqual(result.version, None)
        self.assertEqual(result.port_id, 'PortId')
        self.assertEqual(result.node_id, 'NodeId')
        self.assertEqual(result.out_link, 'OutLink')
        self.assertEqual(result.in_link, 'InLink')
        self.assertEqual(len(result.attributes), 1)
        self.assertEqual(result.attributes['Key'], 'Val')

    def test_packed_object(self):
        result = self.target.packed_object()
        self.assertEqual(result, {'type': 'Port', 'version': '1',
                                  'port_id': 'PortId', 'node_id': 'NodeId',
                                  'out_link': 'OutLink', 'in_link': 'InLink',
                                  'attributes': {'Key': 'Val'}})

if __name__ == "__main__":
    unittest.main()
