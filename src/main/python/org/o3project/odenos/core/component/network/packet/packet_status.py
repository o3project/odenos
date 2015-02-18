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


class PacketStatus(object):
    # property key
    TYPE = "type"
    IN_PACKET_COUNT = "in_packet_count"
    IN_PACKET_BYTES = "in_packet_bytes"
    IN_PACKET_QUEUE_COUNT = "in_packet_queue_count"
    IN_PACKETS = "in_packets"
    OUT_PACKET_COUNT = "out_packet_count"
    OUT_PACKET_BYTES = "out_packet_bytes"
    OUT_PACKET_QUEUE_COUNT = "out_packet_queue_count"
    OUT_PACKETS = "out_packets"

    def __init__(self, type_, in_packet_count, in_packet_bytes,
                 in_packet_queue_count, in_packets,
                 out_packet_count, out_packet_bytes,
                 out_packet_queue_count, out_packets):
        self._body = {
            self.TYPE: type_,
            self.IN_PACKET_COUNT: in_packet_count,
            self.IN_PACKET_BYTES: in_packet_bytes,
            self.IN_PACKET_QUEUE_COUNT: in_packet_queue_count,
            self.IN_PACKETS: in_packets,
            self.OUT_PACKET_COUNT: out_packet_count,
            self.OUT_PACKET_BYTES: out_packet_bytes,
            self.OUT_PACKET_QUEUE_COUNT: out_packet_queue_count,
            self.OUT_PACKETS: out_packets
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def in_packet_count(self):
        return self._body[self.IN_PACKET_COUNT]

    @property
    def in_packet_bytes(self):
        return self._body[self.IN_PACKET_BYTES]

    @property
    def in_packet_queue_count(self):
        return self._body[self.IN_PACKET_QUEUE_COUNT]

    @property
    def in_packets(self):
        return self._body[self.IN_PACKETS]

    @property
    def out_packet_count(self):
        return self._body[self.OUT_PACKET_COUNT]

    @property
    def out_packet_bytes(self):
        return self._body[self.OUT_PACKET_BYTES]

    @property
    def out_packet_queue_count(self):
        return self._body[self.OUT_PACKET_QUEUE_COUNT]

    @property
    def out_packets(self):
        return self._body[self.OUT_PACKETS]

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[cls.TYPE],
                   packed[cls.IN_PACKET_COUNT], packed[cls.IN_PACKET_BYTES],
                   packed[cls.IN_PACKET_QUEUE_COUNT], packed[cls.IN_PACKETS],
                   packed[cls.OUT_PACKET_COUNT], packed[cls.OUT_PACKET_BYTES],
                   packed[cls.OUT_PACKET_QUEUE_COUNT], packed[cls.OUT_PACKETS])

    def packed_object(self):
        return self._body
