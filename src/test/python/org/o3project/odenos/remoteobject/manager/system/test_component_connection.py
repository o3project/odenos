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

from org.o3project.odenos.remoteobject.manager.system.component_connection\
    import ComponentConnection

import unittest


class ComponentConnectionTest(unittest.TestCase):

    def setUp(self):
        self.target = ComponentConnection("slicer1->network1",
                                          "original",
                                          "running")

    def tearDown(self):
        self.target = None

    def test_constructor_state_running(self):
        self.assertEqual(self.target._property[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.target._property[self.target.OBJECT_TYPE],
                         "ComponentConnection")
        self.assertEqual(self.target._property[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.target._property[self.target.OBJECT_STATE],
                         "running")

    def test_constructor_Not_state(self):
        self.target = ComponentConnection("slicer1->network1",
                                          "original",
                                          None)

        self.assertEqual(self.target._property[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.target._property[self.target.OBJECT_TYPE],
                         "ComponentConnection")
        self.assertEqual(self.target._property[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.target._property[self.target.OBJECT_STATE],
                         "initializing")

    def test_id(self):
        self.assertEqual(self.target.id, "slicer1->network1")

    def test_type(self):
        self.assertEqual(self.target.type, "ComponentConnection")

    def test_connection_type(self):
        self.assertEqual(self.target.connection_type, "original")

    def test_state(self):
        self.assertEqual(self.target.state, "running")

    def test_state_setter(self):
        self.target.state = "finalizing"
        self.assertEqual(self.target._property[self.target.OBJECT_STATE],
                         "finalizing")

    def test_is_read_only_key_id(self):
        self.assertEqual(self.target._is_read_only_key("id"), True)

    def test_is_read_only_key_type(self):
        self.assertEqual(self.target._is_read_only_key("type"), True)

    def test_is_read_only_key_connection_type(self):
        self.assertEqual(
            self.target._is_read_only_key("connection_type"), True)

    def test_is_read_only_key_state(self):
        self.assertEqual(self.target._is_read_only_key("state"), False)

    def test_get_property_id(self):
        self.assertEqual(self.target.get_property("id"),
                         "slicer1->network1")

    def test_get_property_type(self):
        self.assertEqual(self.target.get_property("type"),
                         "ComponentConnection")

    def test_get_property_connection_type(self):
        self.assertEqual(self.target.get_property("connection_type"),
                         "original")

    def test_get_property_state(self):
        self.assertEqual(self.target.get_property("state"),
                         "running")

    def test_set_property_state(self):
        self.target.set_property("state", "error")
        self.assertEqual(self.target._property["state"],
                         "error")

    def test_set_property_read_only_key(self):
        self.target.set_property("id", "slicer1")
        self.assertEqual(self.target._property["id"],
                         "slicer1->network1")

    def test_set_property_Same_Old(self):
        self.target.set_property("state", "running")
        self.assertEqual(self.target._property["state"],
                         "running")

    def test_get_property_keys(self):
        self.assertEqual(self.target.get_property_keys(),
                         self.target._property.keys())

    def test_create_from_packed(self):
        self.value = {"id": "slicer1->network1",
                      "type": "ComponentConnection",
                      "connection_type": "original",
                      "state": "initializing"}

        self.result = ComponentConnection.create_from_packed(self.value)
        self.assertEqual(self.result._property[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.result._property[self.target.OBJECT_TYPE],
                         "ComponentConnection")
        self.assertEqual(self.result._property[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.result._property[self.target.OBJECT_STATE],
                         "initializing")

    def test_create_from_packed_State_None(self):
        self.value = {"id": "slicer1->network1",
                      "type": "ComponentConnection",
                      "connection_type": "original"}

        self.result = ComponentConnection.create_from_packed(self.value)
        self.assertEqual(self.result._property[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.result._property[self.target.OBJECT_TYPE],
                         "ComponentConnection")
        self.assertEqual(self.result._property[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.result._property[self.target.OBJECT_STATE],
                         "initializing")

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[self.target.OBJECT_ID],
                         "slicer1->network1")
        self.assertEqual(self.result[self.target.OBJECT_TYPE],
                         "ComponentConnection")
        self.assertEqual(self.result[self.target.CONNECTION_TYPE],
                         "original")
        self.assertEqual(self.result[self.target.OBJECT_STATE],
                         "running")

if __name__ == '__main__':
    unittest.main()
