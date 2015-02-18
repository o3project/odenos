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


class BasicFlowMatch(object):
    # property key
    TYPE = "type"
    IN_NODE = "in_node"
    IN_PORT = "in_port"

    def __init__(self, type_, in_node, in_port):
        self._body = {
            self.TYPE: type_,
            self.IN_NODE: in_node,
        }
        if in_port is not None:
            self._body[self.IN_PORT] = in_port

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def in_node(self):
        return self._body[self.IN_NODE]

    @in_node.setter
    def in_node(self, in_node):
        self._body[self.IN_NODE] = in_node

    @property
    def in_port(self):
        if self.IN_PORT not in self._body:
            return None
        return self._body[self.IN_PORT]

    @in_port.setter
    def in_port(self, in_port):
        self._body[self.IN_PORT] = in_port

    @classmethod
    def create_from_packed(cls, packed):
        in_port = None
        if cls.IN_PORT in packed:
            in_port = packed[cls.IN_PORT]
        return cls(packed[cls.TYPE], packed[cls.IN_NODE],
                   in_port)

    def packed_object(self):
        return self._body
