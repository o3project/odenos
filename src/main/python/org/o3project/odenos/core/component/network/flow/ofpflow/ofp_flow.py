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

from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match\
    import OFPFlowMatch
from org.o3project.odenos.core.component.network.flow.basic.flow_action import (
    FlowAction
)
from org.o3project.odenos.core.component.network.flow.basic.flow_action_output\
    import FlowActionOutput

from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_copy_ttl_In import OFPFlowActionCopyTtlIn
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_copy_ttl_out import OFPFlowActionCopyTtlOut
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_dec_ip_ttl import OFPFlowActionDecIpTtl
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_dec_mpls_ttl import OFPFlowActionDecMplsTtl
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_experimenter import OFPFlowActionExperimenter
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_group_action import OFPFlowActionGroupAction
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_pop_mpls import OFPFlowActionPopMpls
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_pop_pbb import OFPFlowActionPopPbb
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_pop_vlan import OFPFlowActionPopVlan
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_push_mpls import OFPFlowActionPushMpls
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_push_pbb import OFPFlowActionPushPbb
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_push_vlan import OFPFlowActionPushVlan
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_set_field import OFPFlowActionSetField
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_set_ip_ttl import OFPFlowActionSetIpTtl
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_set_mpls_ttl import OFPFlowActionSetMplsTtl
from org.o3project.odenos.core.component.network.flow.ofpflow.\
    ofp_flow_action_set_queue import OFPFlowActionSetQueue


class OFPFlow(Flow):
    # property key
    MATCHES = "matches"
    IDLE_TIMEOUT = "idle_timeout"
    HARD_TIMEOUT = "hard_timeout"
    PATH = "path"
    EDGE_ACTIONS = "edge_actions"

    def __init__(self, type_, version, flow_id, owner,
                 enabled, priority, status, attributes,
                 matches, idle_timeout, hard_timeout, path, edge_actions):
        super(OFPFlow, self).__init__(type_, version, flow_id, owner,
                                      enabled, priority, status, attributes)
        self._body[self.MATCHES] = matches
        self._body[self.IDLE_TIMEOUT] = idle_timeout
        self._body[self.HARD_TIMEOUT] = hard_timeout
        self._body[self.PATH] = path
        self._body[self.EDGE_ACTIONS] = edge_actions

    @property
    def matches(self):
        return self._body[self.MATCHES]

    @property
    def idle_timeout(self):
        return self._body[self.IDLE_TIMEOUT]

    @property
    def hard_timeout(self):
        return self._body[self.HARD_TIMEOUT]

    @property
    def path(self):
        return self._body[self.PATH]

    @property
    def edge_actions(self):
        return self._body[self.EDGE_ACTIONS]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        matches = []
        for match in packed[cls.MATCHES]:
            matches.append(OFPFlowMatch.create_from_packed(match))

        edge_actions = {}
        for node_id, basic_flow_actions in packed[cls.EDGE_ACTIONS].items():
            flow_action_list = []
            for flow_action in basic_flow_actions:
                flow_action_list.append(
                    globals()[flow_action[FlowAction.TYPE]].
                    create_from_packed(flow_action))
            edge_actions[node_id] = flow_action_list

        return cls(packed[cls.TYPE], version,
                   packed[cls.FLOW_ID], packed[cls.OWNER], packed[cls.ENABLED],
                   packed[cls.PRIORITY], packed[cls.STATUS],
                   packed[cls.ATTRIBUTES], matches,
                   packed[cls.IDLE_TIMEOUT], packed[cls.HARD_TIMEOUT],
                   packed[cls.PATH], edge_actions)

    def packed_object(self):
        object_ = deepcopy(self._body)

        matches = []
        for match in self.matches:
            matches.append(match.packed_object())
        object_[self.MATCHES] = matches

        edge_actions = {}
        for node_id, basic_flow_actions in self.edge_actions.items():
            flow_action_list = []
            for flow_action in basic_flow_actions:
                flow_action_list.append(flow_action.packed_object())
            edge_actions[node_id] = flow_action_list
        object_[self.EDGE_ACTIONS] = edge_actions

        return object_
