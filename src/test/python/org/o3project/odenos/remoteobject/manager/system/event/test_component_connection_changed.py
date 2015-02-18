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

from org.o3project.odenos.remoteobject.manager.system.event.component_connection_changed\
    import ComponentConnectionChanged

import unittest
import mock


class ComponentConnectionChangedTest(unittest.TestCase):
    Prev_ObjectProperty = mock.Mock()
    Curr_ObjectProperty = mock.Mock()

    def setUp(self):
        self.target = ComponentConnectionChanged("add",
                                                 self.Prev_ObjectProperty,
                                                 self.Curr_ObjectProperty)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._ComponentConnectionChanged__action,
                         "add")
        self.assertEqual(self.target._ComponentConnectionChanged__prev,
                         self.Prev_ObjectProperty)
        self.assertEqual(self.target._ComponentConnectionChanged__curr,
                         self.Curr_ObjectProperty)

    def test_action(self):
        self.assertEqual(self.target.action, "add")

    def test_prev(self):
        self.assertEqual(self.target.prev, self.Prev_ObjectProperty)

    def test_curr(self):
        self.assertEqual(self.target.curr, self.Curr_ObjectProperty)

    def test_create_from_packed_add_action_type_ComponentConnection(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}

        self.value = {"action": "add",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "add")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev, None)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr._property, self.CURR)

    def test_create_from_packed_add_action_type_LogicAndNetwork(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}

        self.value = {"action": "add",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "add")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev, None)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr._property, self.CURR)

    def test_create_from_packed_delete_action_type_ComponentConnection(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}

        self.value = {"action": "delete",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "delete")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev._property, self.PREV)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr, None)

    def test_create_from_packed_delete_action_type_LogicAndNetwork(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}

        self.value = {"action": "delete",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "delete")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev._property, self.PREV)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr, None)

    def test_create_from_packed_update_action_type_ComponentConnection(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "ComponentConnection",
                     "connection_type": "original",
                     "state": "initializing"}

        self.value = {"action": "update",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "update")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev._property, self.PREV)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr._property, self.CURR)

    def test_create_from_packed_update_action_type_LogicAndNetwork(self):
        self.PREV = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}
        self.CURR = {"id": "slicer1->network1",
                     "type": "LogicAndNetwork",
                     "connection_type": "original",
                     "state": "initializing",
                     "logic_id": "slicer1",
                     "network_id": "network1"}

        self.value = {"action": "update",
                      "prev": self.PREV,
                      "curr": self.CURR}

        self.result = ComponentConnectionChanged.create_from_packed(self.value)
        self.assertEqual(
            self.result._ComponentConnectionChanged__action, "update")
        self.assertEqual(
            self.result._ComponentConnectionChanged__prev._property, self.PREV)
        self.assertEqual(
            self.result._ComponentConnectionChanged__curr._property, self.CURR)

if __name__ == '__main__':
    unittest.main()
