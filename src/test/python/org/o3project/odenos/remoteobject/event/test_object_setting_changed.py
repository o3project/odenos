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
from org.o3project.odenos.remoteobject.event.object_setting_changed\
    import ObjectSettingChanged


class ObjectSettingChangedTest(unittest.TestCase):
    value = {}
    result = {}

    def setUp(self):
        self.target = ObjectSettingChanged("update",
                                           {"Prev": "Prepre"},
                                           {"Curr": "PettiCurr"})

    def test_constructor(self):
        self.assertEqual(
            self.target._ObjectSettingChanged__action, "update")
        self.assertEqual(
            self.target._ObjectSettingChanged__prev, {"Prev": "Prepre"})
        self.assertEqual(
            self.target._ObjectSettingChanged__curr, {"Curr": "PettiCurr"})

    def test_action(self):
        self.assertEqual(self.target.action, "update")

    def test_prev(self):
        self.assertEqual(self.target.prev, {"Prev": "Prepre"})

    def test_curr(self):
        self.assertEqual(self.target.curr, {"Curr": "PettiCurr"})

    def test_create_from_packed_add_action(self):
        self.value = {"action": "add",
                      "prev": {"Prev": "Prepre"},
                      "curr": {"Curr": "PettiCurr"}}

        self.result = ObjectSettingChanged.create_from_packed(self.value)

        self.assertEqual(
            self.result._ObjectSettingChanged__action, "add")
        self.assertEqual(
            self.result._ObjectSettingChanged__prev, None)
        self.assertEqual(
            self.result._ObjectSettingChanged__curr, {"Curr": "PettiCurr"})

    def test_create_from_packed_update_action(self):
        self.value = {"action": "update",
                      "prev": {"Prev": "Prepre"},
                      "curr": {"Curr": "PettiCurr"}}

        self.result = ObjectSettingChanged.create_from_packed(self.value)

        self.assertEqual(
            self.result._ObjectSettingChanged__action, "update")
        self.assertEqual(
            self.result._ObjectSettingChanged__prev, {"Prev": "Prepre"})
        self.assertEqual(
            self.result._ObjectSettingChanged__curr, {"Curr": "PettiCurr"})

    def test_create_from_packed_delete_action(self):
        self.value = {"action": "delete",
                      "prev": {"Prev": "Prepre"},
                      "curr": {"Curr": "PettiCurr"}}

        self.result = ObjectSettingChanged.create_from_packed(self.value)

        self.assertEqual(
            self.result._ObjectSettingChanged__action, "delete")
        self.assertEqual(
            self.result._ObjectSettingChanged__prev, {"Prev": "Prepre"})
        self.assertEqual(
            self.result._ObjectSettingChanged__curr, None)

if __name__ == '__main__':
    unittest.main()
