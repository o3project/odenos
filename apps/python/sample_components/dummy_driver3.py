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

import logging

from org.o3project.odenos.core.component.driver import Driver
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.remoteobject.manager.system.component_connection import (
    ComponentConnection
)
from org.o3project.odenos.remoteobject.manager.system.\
    component_connection_logic_and_network import (
        ComponentConnectionLogicAndNetwork)
from org.o3project.odenos.core.component.network.flow.flow_changed import (
    FlowChanged
)
from org.o3project.odenos.core.component.network.packet.out_packet_added import (
    OutPacketAdded
)


class DummyDriver3(Driver):
    DESCRIPTION = "DummyDriver for python"

    def __init__(self, object_id, dispatcher):
        self.__network_id = None
        super(DummyDriver3, self).__init__(object_id, dispatcher)

    #######################
    # NetworkConnection
    #######################
    # override
    def _connection_changed_added_pre(self, msg):
        component_connection = msg.curr
        # check connection type is logic and network
        type_ = component_connection.type
        logging.debug("Receive ConnectionChanged Type: " + type_)
        if type_ != ComponentConnectionLogicAndNetwork.TYPE:
            return False

        # check logicId is self objectId
        logic_id = component_connection.logic_id
        if self.object_id != logic_id:
            return False

        # check receive ConnectionChangedAdded already
        if self.__network_id is not None:
            component_connection.state = ComponentConnection.State.ERROR
            self._sys_manager_interface.put_connection(component_connection)
            return False

        return True

    # override
    def _connection_changed_delete_pre(self, msg):
        component_connection = msg.prev
        # check connection type is logic and network
        type_ = component_connection.type
        logging.debug("Receive ConnectionChanged Type: " + type_)
        if type_ != ComponentConnectionLogicAndNetwork.TYPE:
            return False

        # check logicId is self objectId
        logic_id = component_connection.logic_id
        if self.object_id != logic_id:
            return False

        return True

    # override
    def _connection_changed_added(self, msg):
        self.__network_id = msg.curr.network_id
        self.__subscribe_network_component()

        component_connection = msg.curr
        #Changed ConectionProperty's status.
        component_connection.state = ComponentConnection.State.RUNNING
        self._sys_manager_interface.put_connection(component_connection)
        return

    # override
    def _connection_changed_delete(self, msg):
        component_connection = msg.prev
        #Changed ConectionProperty's status.
        component_connection.state = ComponentConnection.State.FINALIZING
        self._sys_manager_interface.put_connection(component_connection)

        self.__unsubscribe_network_component()
        self.__network_id = None

        #Changed ConectionProperty's status.
        component_connection.state = ComponentConnection.State.NONE
        self._sys_manager_interface.put_connection(component_connection)
        return

    def __subscribe_network_component(self):
        self._add_event_subscription(FlowChanged.TYPE, self.__network_id)
        self._add_event_subscription(OutPacketAdded.TYPE, self.__network_id)
        self._update_event_subscription(FlowChanged.TYPE,
                                        self.__network_id,
                                        attributes=[])
        self._apply_event_subscription()

    def __unsubscribe_network_component(self):
        self._remove_event_subscription(FlowChanged.TYPE, self.__network_id)
        self._remove_event_subscription(OutPacketAdded.TYPE, self.__network_id)
        self._apply_event_subscription()

    #######################
    # Event method override
    #######################
    # override
    def _on_flow_added(self, network_id, flow):
        if network_id not in self._network_interfaces:
            return

        # update flow is status changed
        network_if = self._network_interfaces[network_id]
        target_flow = network_if.get_flow(flow.flow_id)
        if target_flow is None:
            return

        # Status ... "None" => "Establishing" => "Established"
        if target_flow.status == Flow.Status.NONE and\
           target_flow.enabled:
            target_flow.status = Flow.Status.ESTABLISHING

            # Driver needs to set Flow to physical switch here.
            # Setting of Flow After completing the physical switch,
            # to "Established".
            target_flow.status = Flow.Status.ESTABLISHED
            network_if.put_flow(target_flow)

        return

    # override
    def _on_flow_update(self, network_id, prev, curr, attrs):
        self._on_flow_added(network_id, curr)

    # override
    def _on_flow_delete(self, network_id, flow):
        if network_id not in self._network_interfaces:
            return

        network_if = self._network_interfaces[network_id]
        target_flow = network_if.get_flow(flow.flow_id)
        if target_flow is None:
            return

        # Status ... "Established" => "Teardown" => "None"
        if target_flow.status == Flow.Status.ESTABLISHED and\
           target_flow.enabled:
            target_flow.status = Flow.Status.TEARDOWN

            # Driver needs to set Flow to physical switch here.
            # Setting of Flow After completing the physical switch,
            # to "None".
            target_flow.status = Flow.Status.NONE
            network_if.put_flow(target_flow)

        return

    # override
    def _do_event_outpacketadded(self, event):
        out_packet_added = None
        try:
            out_packet_added = OutPacketAdded.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid OutPacketAdded Message"
                          + " KeyError: " + str(err))
            return

        network_id = event.publisher_id

        logging.debug("Receive OutPacket: " + out_packet_added.id)

        if network_id not in self._network_interfaces:
            return

        network_if = self._network_interfaces[network_id]
        network_if.del_out_packet(out_packet_added.id)
