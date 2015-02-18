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


class Port(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    PORT_ID = "port_id"
    NODE_ID = "node_id"
    OUT_LINK = "out_link"
    IN_LINK = "in_link"
    ATTRIBUTES = "attributes"

    def __init__(self, type_, version, port_id,
                 node_id, out_link, in_link, attributes):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.PORT_ID: port_id,
            self.NODE_ID: node_id,
            self.OUT_LINK: out_link,
            self.IN_LINK: in_link,
            self.ATTRIBUTES: attributes,
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def port_id(self):
        return self._body[self.PORT_ID]

    @property
    def node_id(self):
        return self._body[self.NODE_ID]

    @property
    def out_link(self):
        return self._body[self.OUT_LINK]

    @property
    def in_link(self):
        return self._body[self.IN_LINK]

    @property
    def attributes(self):
        return self._body[self.ATTRIBUTES]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        return cls(packed[cls.TYPE], version,
                   packed[cls.PORT_ID], packed[cls.NODE_ID],
                   packed[cls.OUT_LINK], packed[cls.IN_LINK],
                   packed[cls.ATTRIBUTES])

    def packed_object(self):
        return self._body
