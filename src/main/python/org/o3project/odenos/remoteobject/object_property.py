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

import copy


class ObjectProperty(object):
    OBJECT_ID = "id"
    OBJECT_SUPER_TYPE = "super_type"
    OBJECT_TYPE = "type"
    OBJECT_STATE = "state"
    DESCRIPTION = "description"
    CM_ID = "cm_id"
    CONNECTION_TYPES = "connection_types"
    COMPONENT_TYPES = "component_types"

    class State:
        INITIALIZING = "initializing"
        RUNNING = "running"
        FINALIZING = "finalizing"
        ERROR = "error"

    def __init__(self, object_type, object_id):
        self._object_property = {}
        self._object_property[self.OBJECT_TYPE] = object_type
        self._object_property[self.OBJECT_ID] = object_id

    @property
    def object_type(self):
        return self._object_property[self.OBJECT_TYPE]

    @property
    def object_id(self):
        return self._object_property[self.OBJECT_ID]

    def get_state(self):
        return self._object_property[self.OBJECT_STATE]

    def set_state(self, new_state):
        self._object_property[self.OBJECT_STATE] = new_state

    state = property(get_state, set_state)

    def set_property(self, key, value):
        oldValue = self._object_property.get(key)
        if self.__is_read_only_key(key):
            return None
        self._object_property[key] = value
        return oldValue

    def get_property(self, key):
        return self._object_property.get(key, None)

    def delete_property(self, key):
        if not(self.__is_read_only_key(key)):
            del self._object_property[key]

    def put_property(self, new_prop):
        old = copy.deepcopy(self._object_property)
        # delete
        for key in old.keys():
            if key not in new_prop:
                self.delete_property(key)

        # add or update
        for key, value in new_prop.items():
            self.set_property(key, value)

    def equals(self, new_prop):
        return (self._object_property == new_prop)

    def __is_read_only_key(self, key):
        return (key in [self.OBJECT_TYPE, self.OBJECT_ID]) or\
               (key in self._object_property and
                key == self.OBJECT_SUPER_TYPE) or\
               (key in self._object_property and
                key == self.DESCRIPTION) or\
               (key in self._object_property and
                key == self.CONNECTION_TYPES)

    def packed_object(self):
        return self._object_property
