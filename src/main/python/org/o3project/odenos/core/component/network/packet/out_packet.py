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

from copy import deepcopy

from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.component.network.flow.basic.basic_flow_match\
    import BasicFlowMatch


class OutPacket(Packet):
    # property key
    NODE = "node"
    PORTS = "ports"
    PORTS_EXCEPT = "ports-except"
    HEADER = "header"
    DATA = "data"

    def __init__(self, id_, type_, attributes,
                 node, ports, ports_except, header, data):
        super(OutPacket, self).__init__(id_, type_, attributes)
        self._body[self.NODE] = node
        self._body[self.PORTS] = ports
        self._body[self.PORTS_EXCEPT] = ports_except
        self._body[self.HEADER] = header
        self._body[self.DATA] = data

    @property
    def node(self):
        return self._body[self.NODE]

    @node.setter
    def node(self, node):
        self._body[self.NODE] = node

    @property
    def ports(self):
        return self._body[self.PORTS]

    @ports.setter
    def ports(self, ports):
        self._body[self.PORTS] = ports

    @property
    def ports_except(self):
        return self._body[self.PORTS_EXCEPT]

    @ports_except.setter
    def ports_except(self, ports_except):
        self._body[self.PORTS_EXCEPT] = ports_except

    @property
    def header(self):
        return self._body[self.HEADER]

    @property
    def data(self):
        return self._body[self.DATA]

    @classmethod
    def create_from_packed(cls, packed):
        ports = None
        ports_except = None
        if packed[cls.PORTS] is not None:
            ports = packed[cls.PORTS]
            ports_except = None
        else:
            ports_except = packed[cls.PORTS_EXCEPT]
            ports = None

        return cls(packed[cls.PACKET_ID], packed[cls.TYPE],
                   packed[cls.ATTRIBUTES],
                   packed[cls.NODE], ports, ports_except,
                   BasicFlowMatch.create_from_packed(packed[cls.HEADER]),
                   packed[cls.DATA])

    def packed_object(self):
        object_ = deepcopy(self._body)
        object_[self.HEADER] = self.header.packed_object()
        return object_
