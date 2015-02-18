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


class ObjectSettingChanged(object):
    TYPE = "ObjectSettingChanged"

    # property key
    ACTION = "action"
    PREV = "prev"
    CURR = "curr"

    class Action(object):
        ADD = "add"
        DELETE = "delete"
        UPDATE = "update"

    def __init__(self, action, prev, curr):
        self.__action = action
        self.__prev = prev
        self.__curr = curr

    @property
    def action(self):
        return self.__action

    @property
    def prev(self):
        return self.__prev

    @property
    def curr(self):
        return self.__curr

    @classmethod
    def create_from_packed(cls, packed):
        action = packed[cls.ACTION]
        prev = None
        curr = None

        if action == cls.Action.ADD:
            curr = packed[cls.CURR]
        elif action == cls.Action.DELETE:
            prev = packed[cls.PREV]
        elif action == cls.Action.UPDATE:
            prev = packed[cls.PREV]
            curr = packed[cls.CURR]

        return cls(action, prev, curr)
