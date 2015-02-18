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


class ComponentConnection(object):

    TYPE = "ComponentConnection"

    # Property Key
    OBJECT_ID = "id"
    OBJECT_TYPE = "type"
    CONNECTION_TYPE = "connection_type"
    OBJECT_STATE = "state"

    class State(object):
        INITIALIZING = "initializing"
        RUNNING = "running"
        FINALIZING = "finalizing"
        ERROR = "error"
        NONE = "none"

    def __init__(self,
                 id_,
                 connection_type,
                 state,
                 type_=TYPE):
        if not state:
            state = self.State.INITIALIZING
        self._property = {
            self.OBJECT_ID: id_,
            self.OBJECT_TYPE: type_,
            self.CONNECTION_TYPE: connection_type,
            self.OBJECT_STATE: state}

    @property
    def id(self):
        return self._property[self.OBJECT_ID]

    @property
    def type(self):
        return self._property[self.OBJECT_TYPE]

    @property
    def connection_type(self):
        return self._property[self.CONNECTION_TYPE]

    @property
    def state(self):
        return self._property[self.OBJECT_STATE]

    @state.setter
    def state(self, value):
        self._property[self.OBJECT_STATE] = value

    def _is_read_only_key(self, key):
        return (key == self.OBJECT_ID
                or key == self.OBJECT_TYPE
                or key == self.CONNECTION_TYPE)

    def get_property(self, key):
        return self._property[key]

    def set_property(self, key, value):
        if self._is_read_only_key(key):
            return
        old_value = self._property[key]
        if old_value == value:
            return
        self._property[key] = value
        return

    def get_property_keys(self):
        return self._property.keys()

    @classmethod
    def create_from_packed(cls, packed):
        if cls.OBJECT_STATE not in packed:
            state_ = cls.State.INITIALIZING
        else:
            state_ = packed[cls.OBJECT_STATE]

        return cls(id_=packed[cls.OBJECT_ID],
                   type_=packed[cls.OBJECT_TYPE],
                   connection_type=packed[cls.CONNECTION_TYPE],
                   state=state_)

    def packed_object(self):
        return self._property
