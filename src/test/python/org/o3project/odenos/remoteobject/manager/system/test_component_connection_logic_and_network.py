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

from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network\
    import ComponentConnectionLogicAndNetwork

import unittest


class ComponentConnectionLogicAndNetworkTest(unittest.TestCase):

    def setUp(self):
        self.target = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                         "original",
                                                         "running",
                                                         "LogicId",
                                                         "NetworkId")

    def tearDown(self):
        self.target = None

    def test_constructor_state_running(self):
        self.assertEqual(self.target._property[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.target._property[self.target.OBJECT_TYPE],
                         "LogicAndNetwork")
        self.assertEqual(self.target._property[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.target._property[self.target.OBJECT_STATE],
                         "running")
        self.assertEqual(self.target._property[self.target.LOGIC_ID],
                         "LogicId")
        self.assertEqual(self.target._property[self.target.NETWORK_ID],
                         "NetworkId")

    def test_logic_id(self):
        self.assertEqual(self.target.logic_id, "LogicId")

    def test_network_id(self):
        self.assertEqual(self.target.network_id, "NetworkId")

    def test_create_from_packed_state_error(self):
        self.value = {"id": "slicer2->network2",
                      "connection_type": "test",
                      "state": "error",
                      "logic_id": "123456789",
                      "network_id": "987654321"}

        self.result =\
            ComponentConnectionLogicAndNetwork.create_from_packed(self.value)
        self.assertEqual(self.result._property[self.target.OBJECT_ID],
                         "slicer2->network2")
        self.assertEqual(self.result._property[self.target.OBJECT_TYPE],
                         "LogicAndNetwork")
        self.assertEqual(self.result._property[self.target.CONNECTION_TYPE],
                         "test")
        self.assertEqual(self.result._property[self.target.OBJECT_STATE],
                         "error")
        self.assertEqual(self.result._property[self.target.LOGIC_ID],
                         "123456789")
        self.assertEqual(self.result._property[self.target.NETWORK_ID],
                         "987654321")

    def test_create_from_packed_state_none(self):
        self.value = {"id": "slicer2->network2",
                      "connection_type": "test",
                      "logic_id": "123456789",
                      "network_id": "987654321"}

        self.result =\
            ComponentConnectionLogicAndNetwork.create_from_packed(self.value)
        self.assertEqual(self.result._property[self.target.OBJECT_ID],
                         "slicer2->network2")
        self.assertEqual(self.result._property[self.target.OBJECT_TYPE],
                         "LogicAndNetwork")
        self.assertEqual(self.result._property[self.target.CONNECTION_TYPE],
                         "test")
        self.assertEqual(self.result._property[self.target.OBJECT_STATE],
                         "initializing")
        self.assertEqual(self.result._property[self.target.LOGIC_ID],
                         "123456789")
        self.assertEqual(self.result._property[self.target.NETWORK_ID],
                         "987654321")

if __name__ == '__main__':
    unittest.main()
