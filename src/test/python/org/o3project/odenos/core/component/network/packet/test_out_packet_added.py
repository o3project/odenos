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

from org.o3project.odenos.core.component.network.packet.out_packet_added\
    import OutPacketAdded

import unittest


class OutPacketAddedTest(unittest.TestCase):
    value = None
    result = None

    def setUp(self):
        self.target = OutPacketAdded("0123")

    def tearDown(self):
        self.target = None

    def test_constractor(self):
        self.assertEqual(self.target._OutPacketAdded__id, "0123")

    def test_id(self):
        self.assertEqual(self.target.id, "0123")

    def test_create_from_packed(self):
        self.value = {"id": "0456"}
        self.result = OutPacketAdded.create_from_packed(self.value)
        self.assertEqual(self.result.id, "0456")

if __name__ == '__main__':
    unittest.main()
