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

from org.o3project.odenos.remoteobject.manager.system.component_connection import (
    ComponentConnection
)


class ComponentConnectionLogicAndNetwork(ComponentConnection):

    TYPE = "LogicAndNetwork"

    # property key
    LOGIC_ID = "logic_id"
    NETWORK_ID = "network_id"

    def __init__(self, id_, connection_type, state, logic_id, network_id):
        super(ComponentConnectionLogicAndNetwork, self).\
            __init__(id_,
                     connection_type,
                     state,
                     self.TYPE)
        self._property[self.LOGIC_ID] = logic_id
        self._property[self.NETWORK_ID] = network_id

    @property
    def logic_id(self):
        return self._property[self.LOGIC_ID]

    @property
    def network_id(self):
        return self._property[self.NETWORK_ID]

    @classmethod
    def create_from_packed(cls, packed):
        if cls.OBJECT_STATE not in packed:
            state_ = cls.State.INITIALIZING
        else:
            state_ = packed[cls.OBJECT_STATE]

        return cls(id_=packed[cls.OBJECT_ID],
                   connection_type=packed[cls.CONNECTION_TYPE],
                   state=state_,
                   logic_id=packed[cls.LOGIC_ID],
                   network_id=packed[cls.NETWORK_ID])
