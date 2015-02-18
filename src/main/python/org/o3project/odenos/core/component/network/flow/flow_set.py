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

from org.o3project.odenos.core.component.network.flow.flow\
    import Flow
from org.o3project.odenos.core.component.network.flow.basic.basic_flow\
    import BasicFlow
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow\
    import OFPFlow


class FlowSet(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    PRIORITY = "priority"
    FLOWS = "flows"

    def __init__(self, type_, version, priority, flows):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.PRIORITY: priority,
            self.FLOWS: flows
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def priority(self):
        return self._body[self.PRIORITY]

    @property
    def flows(self):
        return self._body[self.FLOWS]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        flows = {}
        for flow_id, flow in packed[cls.FLOWS].items():
            flows[flow_id] = globals()[flow[Flow.TYPE]].\
                create_from_packed(flow)

        return cls(packed[cls.TYPE], version,
                   packed[cls.PRIORITY], flows)

    def packed_object(self):
        object_ = deepcopy(self._body)
        flows = {}
        for flow_id in self.flows:
            flows[flow_id] = self.flows[flow_id].packed_object()
        object_[self.FLOWS] = flows
        return object_
