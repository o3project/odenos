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

from org.o3project.odenos.core.component.driver import Driver
from mock import Mock
import unittest


class DriverTest(unittest.TestCase):
    Dispatcher = Mock()
    Dispatcher.system_manager_id = "ObjectId1"
    Object_id = "ObjectId1"

    def setUp(self):
        self.target = Driver(self.Object_id, self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._super_type, "Driver")
        self.assertEqual(self.target.dispatcher, self.Dispatcher)
        self.assertEqual(self.target.object_id, self.Object_id)

if __name__ == '__main__':
    unittest.main()
