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

from org.o3project.odenos.core.component.network.topology.port import Port


class Node(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    NODE_ID = "node_id"
    PORTS = "ports"
    ATTRIBUTES = "attributes"

    def __init__(self, type_, version, node_id, ports, attributes):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.NODE_ID: node_id,
            self.PORTS: ports,
            self.ATTRIBUTES: attributes,
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def node_id(self):
        return self._body[self.NODE_ID]

    @property
    def ports(self):
        return self._body[self.PORTS]

    @property
    def attributes(self):
        return self._body[self.ATTRIBUTES]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        ports = {}
        for port_id, port in packed[cls.PORTS].items():
            ports[port_id] = Port.create_from_packed(port)

        return cls(packed[cls.TYPE], version,
                   packed[cls.NODE_ID], ports,
                   packed[cls.ATTRIBUTES])

    def packed_object(self):
        object_ = deepcopy(self._body)
        ports = {}
        for port_id in self.ports:
            ports[port_id] = self.ports[port_id].packed_object()
        object_[self.PORTS] = ports
        return object_
