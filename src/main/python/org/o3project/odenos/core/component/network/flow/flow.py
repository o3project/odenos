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


class Flow(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    FLOW_ID = "flow_id"
    OWNER = "owner"
    ENABLED = "enabled"
    PRIORITY = "priority"
    STATUS = "status"
    ATTRIBUTES = "attributes"

    class Status(object):
        NONE = "none"
        ESTABLISHING = "establishing"
        ESTABLISHED = "established"
        TEARDOWN = "teardown"
        FAILED = "failed"

    def __init__(self, type_, version, flow_id, owner,
                 enabled, priority, status, attributes):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.FLOW_ID: flow_id,
            self.OWNER: owner,
            self.ENABLED: enabled,
            self.PRIORITY: priority,
            self.STATUS: status,
            self.ATTRIBUTES: attributes
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def flow_id(self):
        return self._body[self.FLOW_ID]

    @property
    def owner(self):
        return self._body[self.OWNER]

    @property
    def enabled(self):
        return self._body[self.ENABLED]

    @enabled.setter
    def enabled(self, enabled):
        self._body[self.ENABLED] = enabled

    @property
    def priority(self):
        return self._body[self.PRIORITY]

    @priority.setter
    def priority(self, priority):
        self._body[self.PRIORITY] = priority

    @property
    def status(self):
        return self._body[self.STATUS]

    @status.setter
    def status(self, status):
        self._body[self.STATUS] = status

    @property
    def attributes(self):
        return self._body[self.ATTRIBUTES]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        return cls(packed[cls.TYPE], version,
                   packed[cls.FLOW_ID], packed[cls.OWNER], packed[cls.ENABLED],
                   packed[cls.PRIORITY], packed[cls.STATUS],
                   packed[cls.ATTRIBUTES])

    def packed_object(self):
        return self._body
