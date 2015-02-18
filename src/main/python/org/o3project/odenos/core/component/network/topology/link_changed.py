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

from org.o3project.odenos.core.component.network.topology.link import Link


class LinkChanged(object):
    TYPE = "LinkChanged"

    # property key
    ID = "id"
    ACTION = "action"
    VERSION = "version"
    PREV = "prev"
    CURR = "curr"

    class Action(object):
        ADD = "add"
        DELETE = "delete"
        UPDATE = "update"

    def __init__(self, id_, action, version, prev, curr):
        self.__id = id_
        self.__action = action
        self.__version = version
        self.__prev = prev
        self.__curr = curr

    @property
    def id(self):
        return self.__id

    @property
    def action(self):
        return self.__action

    @property
    def version(self):
        return self.__version

    @property
    def prev(self):
        return self.__prev

    @property
    def curr(self):
        return self.__curr

    @classmethod
    def create_from_packed(cls, packed):
        action = packed[cls.ACTION]
        version = ""
        prev = None
        curr = None

        if action == cls.Action.ADD:
            version = packed[cls.VERSION]
            curr = Link.create_from_packed(packed[cls.CURR])
        elif action == cls.Action.DELETE:
            prev = Link.create_from_packed(packed[cls.PREV])
        elif action == cls.Action.UPDATE:
            version = packed[cls.VERSION]
            prev = Link.create_from_packed(packed[cls.PREV])
            curr = Link.create_from_packed(packed[cls.CURR])

        return cls(packed[cls.ID], action,
                   version, prev, curr)
