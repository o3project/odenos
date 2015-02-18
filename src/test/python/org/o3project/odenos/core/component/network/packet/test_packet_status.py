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

from org.o3project.odenos.core.component.network.packet.packet_status\
    import PacketStatus

import unittest


class InPacketTest(unittest.TestCase):
    value = None
    result = None

    def setUp(self):
        self.target = PacketStatus("PacketStatus",
                                   3210,
                                   6540,
                                   9870,
                                   ["in_packets"],
                                   7410,
                                   8520,
                                   9630,
                                   ["out_packets"])

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         "PacketStatus")
        self.assertEqual(self.target._body[self.target.IN_PACKET_COUNT],
                         3210)
        self.assertEqual(self.target._body[self.target.IN_PACKET_BYTES],
                         6540)
        self.assertEqual(self.target._body[self.target.IN_PACKET_QUEUE_COUNT],
                         9870)
        self.assertEqual(self.target._body[self.target.IN_PACKETS],
                         ["in_packets"])
        self.assertEqual(self.target._body[self.target.OUT_PACKET_COUNT],
                         7410)
        self.assertEqual(self.target._body[self.target.OUT_PACKET_BYTES],
                         8520)
        self.assertEqual(self.target._body[self.target.OUT_PACKET_QUEUE_COUNT],
                         9630)
        self.assertEqual(self.target._body[self.target.OUT_PACKETS],
                         ["out_packets"])

    def test_type(self):
        self.assertEqual(self.target.type, "PacketStatus")

    def test_in_packet_count(self):
        self.assertEqual(self.target.in_packet_count, 3210)

    def test_in_packet_bytes(self):
        self.assertEqual(self.target.in_packet_bytes, 6540)

    def test_in_packet_queue_count(self):
        self.assertEqual(self.target.in_packet_queue_count,
                         9870)

    def test_in_packets(self):
        self.assertEqual(self.target.in_packets, ["in_packets"])

    def test_out_packet_count(self):
        self.assertEqual(self.target.out_packet_count, 7410)

    def test_out_packet_bytes(self):
        self.assertEqual(self.target.out_packet_bytes, 8520)

    def test_out_packet_queue_count(self):
        self.assertEqual(self.target.out_packet_queue_count,
                         9630)

    def test_out_packets(self):
        self.assertEqual(self.target.out_packets, ["out_packets"])

    def test_create_from_packed(self):

        self.value = {"type": "PacketStatus",
                      "in_packet_count": 456,
                      "in_packet_bytes": 789,
                      "in_packet_queue_count": 987,
                      "in_packets": ["6540"],
                      "out_packet_count": 321,
                      "out_packet_bytes": 147,
                      "out_packet_queue_count": 258,
                      "out_packets": ["0258"]}

        self.result = PacketStatus.create_from_packed(self.value)

        self.assertEqual(self.result._body[self.target.TYPE],
                         "PacketStatus")
        self.assertEqual(self.result._body[self.target.IN_PACKET_COUNT],
                         456)
        self.assertEqual(self.result._body[self.target.IN_PACKET_BYTES],
                         789)
        self.assertEqual(self.result._body[self.target.IN_PACKET_QUEUE_COUNT],
                         987)
        self.assertEqual(self.result._body[self.target.IN_PACKETS],
                         ["6540"])
        self.assertEqual(self.result._body[self.target.OUT_PACKET_COUNT],
                         321)
        self.assertEqual(self.result._body[self.target.OUT_PACKET_BYTES],
                         147)
        self.assertEqual(self.result._body[self.target.OUT_PACKET_QUEUE_COUNT],
                         258)
        self.assertEqual(self.result._body[self.target.OUT_PACKETS],
                         ["0258"])

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[self.target.TYPE],
                         "PacketStatus")
        self.assertEqual(self.result[self.target.IN_PACKET_COUNT],
                         3210)
        self.assertEqual(self.result[self.target.IN_PACKET_BYTES],
                         6540)
        self.assertEqual(self.result[self.target.IN_PACKET_QUEUE_COUNT],
                         9870)
        self.assertEqual(self.result[self.target.IN_PACKETS],
                         ["in_packets"])
        self.assertEqual(self.result[self.target.OUT_PACKET_COUNT],
                         7410)
        self.assertEqual(self.result[self.target.OUT_PACKET_BYTES],
                         8520)
        self.assertEqual(self.result[self.target.OUT_PACKET_QUEUE_COUNT],
                         9630)
        self.assertEqual(self.result[self.target.OUT_PACKETS],
                         ["out_packets"])

if __name__ == '__main__':
    unittest.main()
