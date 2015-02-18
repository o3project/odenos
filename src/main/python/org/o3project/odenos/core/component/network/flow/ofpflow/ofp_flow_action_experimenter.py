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


class OFPFlowActionExperimenter(FlowAction):
    # property key
    EXPERIMENTER_ID = "experimenter_id"
    BODY = "body"

    def __init__(self, type_, experimenter_id, body):
        super(OFPFlowActionExperimenter, self).__init__(type_)
        self._body[self.EXPERIMENTER_ID] = experimenter_id
        self._body[self.BODY] = body

    @property
    def experimenter_id(self):
        return self._body[self.EXPERIMENTER_ID]

    @property
    def body(self):
        return self._body[self.BODY]

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[cls.TYPE],
                   packed[cls.EXPERIMENTER_ID], packed[cls.BODY])

    def packed_object(self):
        return self._body
