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

from org.o3project.odenos.core.component.network.topology.topology import Topology


class TopologyChanged(object):
    TYPE = "TopologyChanged"

    # property key
    VERSION = "version"
    PREV = "prev"
    CURR = "curr"

    def __init__(self, version, prev, curr):
        self.__version = version
        self.__prev = prev
        self.__curr = curr

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
        version = packed[cls.VERSION]
        prev = Topology.create_from_packed(packed[cls.PREV])
        curr = Topology.create_from_packed(packed[cls.CURR])
        return cls(version, prev, curr)
