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

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match\
    import OFPFlowMatch
from org.o3project.odenos.core.component.network.flow.basic.flow_action import (
    FlowAction
)


class OFPFlowActionSetField(FlowAction):
    # property key
    MATCH = "match"

    def __init__(self, type_, match):
        super(OFPFlowActionSetField, self).__init__(type_)
        # match is OFPFlowMatch class
        self._body[self.MATCH] = match

    @property
    def match(self):
        return self._body[self.MATCH]

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[cls.TYPE],
                   OFPFlowMatch.create_from_packed(packed[cls.MATCH]))

    def packed_object(self):
        object_ = deepcopy(self._body)
        object_[self.MATCH] = self._body[self.MATCH].packed_object()
        return object_
