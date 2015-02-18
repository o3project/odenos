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

from org.o3project.odenos.core.component.network.packet.packet import Packet

import unittest


class PacketTest(unittest.TestCase):
    value = None
    result = None

    def setUp(self):
        self.target = Packet("packet_id", "packet_type",
                             {"attributes", "packet_attributes"})

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.PACKET_ID],
                         "packet_id")
        self.assertEqual(self.target._body[self.target.TYPE],
                         "packet_type")
        self.assertEqual(self.target._body[self.target.ATTRIBUTES],
                         {"attributes", "packet_attributes"})

    def test_packet_id(self):
        self.assertEqual(self.target.packet_id, "packet_id")

    def test_type(self):
        self.assertEqual(self.target.type, "packet_type")

    def test_attributes(self):
        self.assertEqual(self.target.attributes,
                         {"attributes", "packet_attributes"})

if __name__ == '__main__':
    unittest.main()
