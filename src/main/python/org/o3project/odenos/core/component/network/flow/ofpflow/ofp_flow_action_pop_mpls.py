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

from org.o3project.odenos.core.component.network.flow.basic.flow_action import (
    FlowAction
)


class OFPFlowActionPopMpls(FlowAction):

    MPLS_UNICAST = 0x8847
    MPLS_MULTICAST = 0x8848

    # property key
    ETH_TYPE = "eth_type"

    def __init__(self, type_, eth_type):
        super(OFPFlowActionPopMpls, self).__init__(type_)
        self._body[self.ETH_TYPE] = eth_type

    @property
    def eth_type(self):
        return self._body[self.ETH_TYPE]

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[cls.TYPE], packed[cls.ETH_TYPE])

    def packed_object(self):
        return self._body
