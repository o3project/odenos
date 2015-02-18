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

from org.o3project.odenos.remoteobject.manager.system.event.component_manager_changed\
    import ComponentManagerChanged


import unittest


class ComponentManagerChangedTest(unittest.TestCase):
    Prev_ObjectProperty = {"id": "Prev",
                           "super_type": "Network",
                           "type": "Network",
                           "base_uri": "http://example.com:12345/objects/Prev",
                           "state": "running",
                           "description": "NetworkComponent"}
    Curr_ObjectProperty = {"id": "Curr",
                           "super_type": "Network",
                           "type": "Network",
                           "base_uri": "http://example.com:12345/objects/Curr",
                           "state": "running",
                           "description": "NetworkComponent"}

    def setUp(self):
        self.target = ComponentManagerChanged("update",
                                              self.Prev_ObjectProperty,
                                              self.Curr_ObjectProperty)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._ComponentManagerChanged__action,
                         "update")
        self.assertEqual(self.target._ComponentManagerChanged__prev,
                         self.Prev_ObjectProperty)
        self.assertEqual(self.target._ComponentManagerChanged__curr,
                         self.Curr_ObjectProperty)

    def test_action(self):
        self.assertEqual(self.target.action, "update")

    def test_prev(self):
        self.assertEqual(self.target.prev, self.Prev_ObjectProperty)

    def test_curr(self):
        self.assertEqual(self.target.curr, self.Curr_ObjectProperty)

    def test_create_from_packed_add_action(self):

        self.value = {"action": "add",
                      "prev": self.Prev_ObjectProperty,
                      "curr": self.Curr_ObjectProperty}

        self.result = ComponentManagerChanged.create_from_packed(self.value)
        self.assertEqual(self.result._ComponentManagerChanged__action,
                         "add")
        self.assertEqual(self.result._ComponentManagerChanged__prev,
                         None)
        self.assertEqual(self.result._ComponentManagerChanged__curr,
                         self.Curr_ObjectProperty)

    def test_create_from_packed_delete_action(self):

        self.value = {"action": "delete",
                      "prev": self.Prev_ObjectProperty,
                      "curr": self.Curr_ObjectProperty}

        self.result = ComponentManagerChanged.create_from_packed(self.value)
        self.assertEqual(self.result._ComponentManagerChanged__action,
                         "delete")
        self.assertEqual(self.result._ComponentManagerChanged__prev,
                         self.Prev_ObjectProperty)
        self.assertEqual(self.result._ComponentManagerChanged__curr,
                         None)

    def test_create_from_packed_update_action(self):

        self.value = {"action": "update",
                      "prev": self.Prev_ObjectProperty,
                      "curr": self.Curr_ObjectProperty}

        self.result = ComponentManagerChanged.create_from_packed(self.value)
        self.assertEqual(self.result._ComponentManagerChanged__action,
                         "update")
        self.assertEqual(self.result._ComponentManagerChanged__prev,
                         self.Prev_ObjectProperty)
        self.assertEqual(self.result._ComponentManagerChanged__curr,
                         self.Curr_ObjectProperty)

if __name__ == '__main__':
    unittest.main()
