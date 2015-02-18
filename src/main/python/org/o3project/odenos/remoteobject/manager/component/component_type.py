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


class ComponentType(object):
    # property key
    TYPE = "type"
    SUPER_TYPE = "super_type"
    CONNECTION_TYPES = "connection_types"
    DESCRIPTION = "description"

    def __init__(self, type, super_type, connection_types, description):
        self._body = {}
        if type is not None:
            self._body[self.TYPE] = type
        else:
            self._body[self.TYPE] = ""
        if super_type is not None:
            self._body[self.SUPER_TYPE] = super_type
        else:
            self._body[self.SUPER_TYPE] = ""
        if connection_types is not None:
            self._body[self.CONNECTION_TYPES] = connection_types
        else:
            self._body[self.CONNECTION_TYPES] = {}
        if description is not None:
            self._body[self.DESCRIPTION] = description
        else:
            self._body[self.DESCRIPTION] = ""

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def super_type(self):
        return self._body[self.SUPER_TYPE]

    @property
    def connection_types(self):
        return self._body[self.CONNECTION_TYPES]

    @property
    def description(self):
        return self._body[self.DESCRIPTION]

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[cls.TYPE], packed[cls.SUPER_TYPE],
                   packed[cls.CONNECTION_TYPES], packed[cls.DESCRIPTION])

    def packed_object(self):
        object_ = deepcopy(self._body)
        return object_
