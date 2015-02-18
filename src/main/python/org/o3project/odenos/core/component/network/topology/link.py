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


class Link(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    LINK_ID = "link_id"
    SRC_NODE = "src_node"
    SRC_PORT = "src_port"
    DST_NODE = "dst_node"
    DST_PORT = "dst_port"
    ATTRIBUTES = "attributes"

    def __init__(self, type_, version, link_id,
                 src_node, src_port, dst_node, dst_port, attributes):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.LINK_ID: link_id,
            self.SRC_NODE: src_node,
            self.SRC_PORT: src_port,
            self.DST_NODE: dst_node,
            self.DST_PORT: dst_port,
            self.ATTRIBUTES: attributes
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def link_id(self):
        return self._body[self.LINK_ID]

    @property
    def src_node(self):
        return self._body[self.SRC_NODE]

    @property
    def src_port(self):
        return self._body[self.SRC_PORT]

    @property
    def dst_node(self):
        return self._body[self.DST_NODE]

    @property
    def dst_port(self):
        return self._body[self.DST_PORT]

    @property
    def attributes(self):
        return self._body[self.ATTRIBUTES]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        return cls(packed[cls.TYPE], version, packed[cls.LINK_ID],
                   packed[cls.SRC_NODE], packed[cls.SRC_PORT],
                   packed[cls.DST_NODE], packed[cls.DST_PORT],
                   packed[cls.ATTRIBUTES])

    def packed_object(self):
        return self._body
