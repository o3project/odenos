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
import re
import copy
from inspect import stack

from org.o3project.odenos.core.component.component import Component
from org.o3project.odenos.core.component.conversion_table import ConversionTable
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.util.system_manager_interface import (
    SystemManagerInterface
)
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.manager.system.event.component_connection_changed\
    import ComponentConnectionChanged
from org.o3project.odenos.core.component.network.topology.node_changed import (
    NodeChanged
)
from org.o3project.odenos.core.component.network.topology.port_changed import (
    PortChanged
)
from org.o3project.odenos.core.component.network.topology.link_changed import (
    LinkChanged
)
from org.o3project.odenos.core.component.network.flow.flow_changed import (
    FlowChanged
)
from org.o3project.odenos.core.component.network.packet.in_packet_added import (
    InPacketAdded
)
from org.o3project.odenos.core.component.network.packet.out_packet_added import (
    OutPacketAdded
)

from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.packet.in_packet import InPacket
from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket


class Logic(Component):
    NETWORK_EVENT_TYPE_LIST = [NodeChanged.TYPE, PortChanged.TYPE,
                               LinkChanged.TYPE, FlowChanged.TYPE]
    PACKET_EVENT_TYPE_LIST = [InPacketAdded.TYPE, OutPacketAdded.TYPE]

    def __init__(self, object_id, dispatcher):
        super(Logic, self).__init__(object_id, dispatcher)
        # ConversionTable Object
        self._conversion_table = ConversionTable()
        # key:network_id value:NetworkInterface Object
        self._network_interfaces = {}
        # key:event_type + network_id value:attribute list
        self.__subscription_table = {}
        # SystemManager IF
        if self.dispatcher is None:
            return
        self._sys_manager_interface = SystemManagerInterface(dispatcher, object_id)

    ###################################
    # Receive ComponentConnectionChanged
    ###################################
    def _do_event_componentconnectionchanged(self, event):
        msg = None
        try:
            msg = ComponentConnectionChanged.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid ComponentConnectionChanged Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Receive ComponentConnectionChanged action:" +
                      msg.action)
        if msg.action == ComponentConnectionChanged.Action.ADD:
            if (self._connection_changed_added_pre(msg)):
                network_id = msg.curr.network_id
                if network_id in self._network_interfaces:
                    return
                self._network_interfaces[network_id] = \
                    NetworkInterface(self.dispatcher, network_id, self.object_id())
                self._connection_changed_added(msg)
        elif msg.action == ComponentConnectionChanged.Action.UPDATE:
            if (self._connection_changed_update_pre(msg)):
                self._connection_changed_update(msg)
        elif msg.action == ComponentConnectionChanged.Action.DELETE:
            if (self._connection_changed_delete_pre(msg)):
                network_id = msg.prev.network_id
                self._connection_changed_delete(msg)
                del self._network_interfaces[network_id]
        else:
            return

    def _connection_changed_added_pre(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _connection_changed_update_pre(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _connection_changed_delete_pre(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _connection_changed_added(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return

    def _connection_changed_update(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return

    def _connection_changed_delete(self, msg):
        logging.debug(">> %s", stack()[0][3])
        return

    # Add Network Event Subscription
    def _add_event_subscription(self, event_type, neteork_id):
        if event_type is None or neteork_id is None:
            return

        if event_type in self.NETWORK_EVENT_TYPE_LIST or\
           event_type in self.PACKET_EVENT_TYPE_LIST:
            logging.debug("add_event_subscription Type:" + event_type
                          + " NetworkID:" + neteork_id)
            self._event_subscription.add_filter(neteork_id, event_type)
            self.__subscription_table[event_type + "::" + neteork_id] = None

        return

    # Remove Network Event Subscription
    def _remove_event_subscription(self, event_type, neteork_id):
        if event_type is None or neteork_id is None:
            return

        if event_type in self.NETWORK_EVENT_TYPE_LIST or\
           event_type in self.PACKET_EVENT_TYPE_LIST:
            logging.debug("remove_event_subscription Type:" + event_type
                          + " NetworkID:" + neteork_id)
            self._event_subscription.remove_filter(neteork_id,
                                                   event_type)
            del self.__subscription_table[event_type + "::" + neteork_id]

        return

    # Update Network Event Subscription
    def _update_event_subscription(self, event_type,
                                   neteork_id, attributes=[]):
        if event_type is None or neteork_id is None:
            return

        if event_type in self.NETWORK_EVENT_TYPE_LIST:
            self._event_subscription.add_filter(neteork_id, event_type)
            self.__subscription_table[event_type + "::UPDATE::"
                                      + neteork_id] = attributes

        return

    ###################################
    # Receive NodeChanged
    ###################################
    def _do_event_nodechanged(self, event):
        msg = None
        try:
            msg = NodeChanged.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid NodeChanged Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received NodeChanged from Network ID:" +
                      event.publisher_id + " action:" + msg.action)
        if msg.action == NodeChanged.Action.ADD:
            key = NodeChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_node_added(event.publisher_id,
                                    msg.curr)
        elif msg.action == NodeChanged.Action.UPDATE:
            key = NodeChanged.TYPE + "::UPDATE::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_node_update(event.publisher_id,
                                     msg.prev,
                                     msg.curr,
                                     self.__subscription_table[key])
        elif msg.action == NodeChanged.Action.DELETE:
            key = NodeChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_node_delete(event.publisher_id,
                                     msg.prev)
        else:
            logging.debug("invalid action")

        return

    ###################################
    # Receive PortChanged
    ###################################
    def _do_event_portchanged(self, event):
        msg = None
        try:
            msg = PortChanged.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid PortChanged Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received PortChanged from Network ID:" +
                      event.publisher_id + " action:" + msg.action)
        if msg.action == PortChanged.Action.ADD:
            key = PortChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_port_added(event.publisher_id,
                                    msg.curr)
        elif msg.action == PortChanged.Action.UPDATE:
            key = PortChanged.TYPE + "::UPDATE::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_port_update(event.publisher_id,
                                     msg.prev,
                                     msg.curr,
                                     self.__subscription_table[key])
        elif msg.action == PortChanged.Action.DELETE:
            key = PortChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_port_delete(event.publisher_id,
                                     msg.prev)
        else:
            logging.debug("invalid action")

        return

    ###################################
    # Receive LinkChanged
    ###################################
    def _do_event_linkchanged(self, event):
        msg = None
        try:
            msg = LinkChanged.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid LinkChanged Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received LinkChanged from Network ID:" +
                      event.publisher_id + " action:" + msg.action)
        if msg.action == LinkChanged.Action.ADD:
            key = LinkChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_link_added(event.publisher_id,
                                    msg.curr)
        elif msg.action == LinkChanged.Action.UPDATE:
            key = LinkChanged.TYPE + "::UPDATE::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_link_update(event.publisher_id,
                                     msg.prev,
                                     msg.curr,
                                     self.__subscription_table[key])
        elif msg.action == LinkChanged.Action.DELETE:
            key = LinkChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_link_delete(event.publisher_id,
                                     msg.prev)
        else:
            logging.debug("invalid action")

        return

    ###################################
    # Receive FlowChanged
    ###################################
    def _do_event_flowchanged(self, event):
        msg = None
        try:
            msg = FlowChanged.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid FlowChanged Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received FlowChanged from Network ID:" +
                      event.publisher_id + " action:" + msg.action)
        if msg.action == FlowChanged.Action.ADD:
            key = FlowChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_flow_added(event.publisher_id,
                                    msg.curr)
        elif msg.action == FlowChanged.Action.UPDATE:
            key = FlowChanged.TYPE + "::UPDATE::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_flow_update(event.publisher_id,
                                     msg.prev,
                                     msg.curr,
                                     self.__subscription_table[key])
        elif msg.action == FlowChanged.Action.DELETE:
            key = FlowChanged.TYPE + "::" + event.publisher_id
            if key in self.__subscription_table:
                self._on_flow_delete(event.publisher_id,
                                     msg.prev)
        else:
            logging.debug("invalid action")

        return

    ###################################
    # Receive InPacketAdded
    ###################################
    def _do_event_inpacketadded(self, event):
        msg = None
        try:
            msg = InPacketAdded.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid InPacketAdded Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received InPacketAdded from Network ID:" +
                      event.publisher_id)
        if (self._on_in_packet_added_pre(event.publisher_id, msg)):
            resp_list = self._add_in_packet_conversion(event.publisher_id, msg)
            self._on_in_packet_added_post(event.publisher_id, msg, resp_list)

        return

    def _on_in_packet_added_pre(self, network_id, msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_in_packet_added_post(self, network_id, msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    ###################################
    # Receive OutPacketAdded
    ###################################
    def _do_event_outpacketadded(self, event):
        msg = None
        try:
            msg = OutPacketAdded.create_from_packed(event.body)
        except KeyError, err:
            logging.error("Receive Invalid OutPacketAdded Message"
                          + " KeyError: " + str(err))
            return

        logging.debug("Received OutPacketAdded from Network ID:" +
                      event.publisher_id)
        if (self._on_out_packet_added_pre(event.publisher_id, msg)):
            resp_list = self._add_out_packet_conversion(event.publisher_id,
                                                        msg)
            self._on_out_packet_added_post(event.publisher_id, msg, resp_list)

        return

    def _on_out_packet_added_pre(self, network_id, msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_out_packet_added_post(self, network_id, msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Add Node
    def _on_node_added(self, network_id, node_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_node_added_pre(network_id, node_msg)):
            resp_list = self._add_node_conversion(network_id, node_msg)
            self._on_node_added_post(network_id, node_msg, resp_list)

        return

    def _on_node_added_pre(self, network_id, node_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_node_added_post(self, network_id, node_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Add Port
    def _on_port_added(self, network_id, port_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_port_added_pre(network_id, port_msg)):
            resp_list = self._add_port_conversion(network_id, port_msg)
            self._on_port_added_post(network_id, port_msg, resp_list)

        return

    def _on_port_added_pre(self, network_id, port_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_port_added_post(self, network_id, port_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Add Link
    def _on_link_added(self, network_id, link_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_link_added_pre(network_id, link_msg)):
            resp_list = self._add_link_conversion(network_id, link_msg)
            self._on_link_added_post(network_id, link_msg, resp_list)

        return

    def _on_link_added_pre(self, network_id, link_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_link_added_post(self, network_id, link_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Add Flow
    def _on_flow_added(self, network_id, flow_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_flow_added_pre(network_id, flow_msg)):
            resp_list = self._add_flow_conversion(network_id, flow_msg)
            self._on_flow_added_post(network_id, flow_msg, resp_list)

        return

    def _on_flow_added_pre(self, network_id, flow_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_flow_added_post(self, network_id, flow_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Update Node
    def _on_node_update(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_node_update_pre(network_id, prev, curr, attributes)):
            resp_list = self._update_node_conversion(network_id,
                                                     prev,
                                                     curr,
                                                     attributes)
            self._on_node_update_post(network_id,
                                      prev,
                                      curr,
                                      attributes,
                                      resp_list)

        return

    def _on_node_update_pre(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_node_update_post(self, network_id,
                             prev, curr, attributes, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Update Port
    def _on_port_update(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_port_update_pre(network_id, prev, curr, attributes)):
            resp_list = self._update_port_conversion(network_id,
                                                     prev,
                                                     curr,
                                                     attributes)
            self._on_port_update_post(network_id,
                                      prev,
                                      curr,
                                      attributes,
                                      resp_list)

        return

    def _on_port_update_pre(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_port_update_post(self, network_id,
                             prev, curr, attributes, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Update Link
    def _on_link_update(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_link_update_pre(network_id, prev, curr, attributes)):
            resp_list = self._update_link_conversion(network_id,
                                                     prev,
                                                     curr,
                                                     attributes)
            self._on_link_update_post(network_id,
                                      prev,
                                      curr,
                                      attributes,
                                      resp_list)

        return

    def _on_link_update_pre(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_link_update_post(self, network_id,
                             prev, curr, attributes, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Update Flow
    def _on_flow_update(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_flow_update_pre(network_id, prev, curr, attributes)):
            resp_list = self._update_flow_conversion(network_id,
                                                     prev,
                                                     curr,
                                                     attributes)
            self._on_flow_update_post(network_id,
                                      prev,
                                      curr,
                                      attributes,
                                      resp_list)

        return

    def _on_flow_update_pre(self, network_id, prev, curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_flow_update_post(self, network_id,
                             prev, curr, attributes, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Delete Node
    def _on_node_delete(self, network_id, node_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_node_delete_pre(network_id, node_msg)):
            resp_list = self._delete_node_conversion(network_id, node_msg)
            self._on_node_delete_post(network_id, node_msg, resp_list)

        return

    def _on_node_delete_pre(self, network_id, node_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_node_delete_post(self, network_id, node_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Delete Port
    def _on_port_delete(self, network_id, port_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_port_delete_pre(network_id, port_msg)):
            resp_list = self._delete_port_conversion(network_id, port_msg)
            self._on_port_delete_post(network_id, port_msg, resp_list)

        return

    def _on_port_delete_pre(self, network_id, port_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_port_delete_post(self, network_id, port_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Delete Link
    def _on_link_delete(self, network_id, link_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_link_delete_pre(network_id, link_msg)):
            resp_list = self._delete_link_conversion(network_id, link_msg)
            self._on_link_delete_post(network_id, link_msg, resp_list)

        return

    def _on_link_delete_pre(self, network_id, link_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_link_delete_post(self, network_id, link_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    # Delete Flow
    def _on_flow_delete(self, network_id, flow_msg):
        logging.debug(">> %s", stack()[0][3])
        if (self._on_flow_delete_pre(network_id, flow_msg)):
            resp_list = self._delete_flow_conversion(network_id, flow_msg)
            self._on_flow_delete_post(network_id, flow_msg, resp_list)

        return

    def _on_flow_delete_pre(self, network_id, flow_msg):
        logging.debug(">> %s", stack()[0][3])
        return True

    def _on_flow_delete_post(self, network_id, flow_msg, resp_list):
        logging.debug(">> %s", stack()[0][3])
        return

    ###################################
    # Add Conversion
    ###################################

    # Add Node Conversion
    def _add_node_conversion(self, network_id, node):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        for nw_id in self._conversion_table.get_network(network_id):
            if nw_id not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[nw_id]
            resp = network_if.put_node(node)
            resp_list[nw_id] = resp
            try:
                resp_node = Node.create_from_packed(resp.body)
                self._conversion_table.add_entry_node(network_id,
                                                      node.node_id,
                                                      nw_id,
                                                      resp_node.node_id)
            except KeyError, err:
                logging.error("PUT Node Invalid Response Message"
                              + " KeyError: " + str(err))

        return resp_list

    # Add Port Conversion
    def _add_port_conversion(self, network_id, port):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}

        for nw_id in self._conversion_table.get_network(network_id):
            if nw_id not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[nw_id]
            resp = network_if.put_port(port)
            resp_list[nw_id] = resp
            try:
                resp_port = Port.create_from_packed(resp.body)
                self._conversion_table.add_entry_port(network_id,
                                                      port.node_id,
                                                      port.port_id,
                                                      nw_id,
                                                      resp_port.node_id,
                                                      resp_port.port_id)
            except KeyError, err:
                logging.error("PUT Port Invalid Response Message"
                              + " KeyError: " + str(err))

        return resp_list

    # Add Link Conversion
    def _add_link_conversion(self, network_id, link):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}

        for nw_id in self._conversion_table.get_network(network_id):
            if nw_id not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[nw_id]
            resp = network_if.put_link(link)
            resp_list[nw_id] = resp
            try:
                resp_link = Link.create_from_packed(resp.body)
                self._conversion_table.add_entry_link(network_id,
                                                      link.link_id,
                                                      nw_id,
                                                      resp_link.link_id)
            except KeyError, err:
                logging.error("PUT Link Invalid Response Message"
                              + " KeyError: " + str(err))

        return resp_list

    # Add Flow Conversion
    def _add_flow_conversion(self, network_id, flow):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        for nw_id in self._conversion_table.get_network(network_id):
            if nw_id not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[nw_id]
            resp_list[nw_id] = network_if.put_flow(flow)

        return resp_list

    # Add InPacket Conversion
    def _add_in_packet_conversion(self, network_id, in_packet):
        logging.debug(">> %s", stack()[0][3])

        resp_list = {}

        if network_id not in self._network_interfaces:
            return resp_list

        network_if = self._network_interfaces[network_id]
        del_in_packet = self._del_in_packet(network_if, in_packet.id)
        if del_in_packet is None:
            logging.error("invalid DELETE Packet.")
            return resp_list

        # convert in_node.
        if del_in_packet.node is None:
            return resp_list
        pre_node_id = del_in_packet.node
        convert_in_node_id_list = self._conversion_table.get_node(network_id,
                                                                  pre_node_id)
        if len(convert_in_node_id_list) == 0:
            return resp_list
        n_list = convert_in_node_id_list[0].split('::')
        del_in_packet.node = n_list[1]

        # convert in_port.
        if del_in_packet.port is None:
            return resp_list
        convert_in_port_id_list =\
            self._conversion_table.get_port(network_id,
                                            pre_node_id,
                                            del_in_packet.port)
        if len(convert_in_port_id_list) == 0:
            return resp_list
        p_list = convert_in_port_id_list[0].split('::')
        del_in_packet.port = p_list[2]

        # convert header.
        convert_port_id_list = \
            self._conversion_table.get_port(network_id,
                                            del_in_packet.header.in_node,
                                            del_in_packet.header.in_port)
        if len(convert_port_id_list) == 0:
            return resp_list

        attr_list = convert_port_id_list[0].split('::')
        if attr_list[0] not in self._network_interfaces:
            return resp_list

        network_if = self._network_interfaces[attr_list[0]]
        del_in_packet.header.in_node = attr_list[1]
        del_in_packet.header.in_port = attr_list[2]

        resp_list[network_if.network_id] =\
            network_if.post_in_packet(del_in_packet)

        return resp_list

    # Add OutPacket Conversion
    def _add_out_packet_conversion(self, network_id, out_packet):
        logging.debug(">> %s", stack()[0][3])

        resp_list = {}

        if network_id not in self._network_interfaces:
            return resp_list

        network_if = self._network_interfaces[network_id]
        del_out_packet = self._del_out_packet(network_if, out_packet.id)
        if del_out_packet is None:
            logging.error("invalid DELETE Packet.")
            return resp_list

        # convert header.
        convert_port_id_list = \
            self._conversion_table.get_port(network_id,
                                            del_out_packet.header.in_node,
                                            del_out_packet.header.in_port)
        if len(convert_port_id_list) == 0:
            return resp_list

        port_list = convert_port_id_list[0].split('::')
        del_out_packet.header.in_node = port_list[1]
        del_out_packet.header.in_port = port_list[2]

        # convert node.
        if del_out_packet.node is None:
            return resp_list
        pre_node_id = del_out_packet.node
        convert_node_id_list = \
            self._conversion_table.get_node(network_id,
                                            pre_node_id)
        if len(convert_node_id_list) == 0:
            return resp_list
        n_list = convert_node_id_list[0].split('::')
        del_out_packet.node = n_list[1]

        # convert ports, ports-except.
        ports = del_out_packet.ports
        convert_ports = []
        except_ports = del_out_packet.ports_except
        convert_except_ports = []
        if ports is not None and len(ports) > 0:
            for port_id in ports:
                convert_port_id = self._conversion_table.get_port(network_id,
                                                                  pre_node_id,
                                                                  port_id)
                if len(convert_port_id) == 0:
                    return resp_list

                p_list = convert_port_id[0].split('::')
                convert_ports.append(p_list[2])
        elif except_ports is not None and len(except_ports) > 0:
            for port_id in except_ports:
                convert_port_id = self._conversion_table.get_port(network_id,
                                                                  pre_node_id,
                                                                  port_id)
                if len(convert_port_id) == 0:
                    return resp_list

                p_list = convert_port_id[0].split('::')
                convert_except_ports.append(p_list[2])

        if len(convert_ports) > 0:
            del_out_packet.ports = convert_ports
        elif len(convert_except_ports) > 0:
            del_out_packet.ports_except = convert_except_ports

        network_if = self._network_interfaces[port_list[0]]
        resp_list[network_if.network_id] =\
            network_if.post_out_packet(del_out_packet)

        return resp_list

    ###################################
    # Update Conversion
    ###################################
    keys_node = ["type", "version", "node_id"]
    keys_port = ["type", "version", "node_id", "port_id",
                 "out_link", "in_link"]
    keys_link = ["type", "version", "link_id", "src_node", "src_port",
                 "dst_node", "dst_port"]
    keys_flow = ["type", "version", "flow_id", "owner",
                 "enabled", "priority", "status"]

    attributes_node = ["admin_status", "oper_status", "physical_id", "vendor"]
    attributes_port = ["admin_status", "oper_status", "physical_id", "vendor",
                       "max_bandwidth", "unreserved_bandwidth", "is_boundary"]
    attributes_link = ["oper_status", "cost", "latency", "req_latency",
                       "max_bandwidth", "unreserved_bandwidth",
                       "req_bandwidth", "establishment_status"]
    attributes_flow = ["bandwidth", "req_bandwidth", "latency", "req_latency"]

    # Update Node Conversion
    def _update_node_conversion(self, network_id,
                                node_prev, node_curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or node_curr is None:
            return resp_list

        attributes_list = []
        if attributes is None:
            attributes_list = []
        else:
            attributes_list = attributes

        # get ignore list
        node_ignore_attributes = self.__get_ignore_keys(self.attributes_node,
                                                        attributes_list)

        for dst_node in self._conversion_table.get_node(network_id,
                                                        node_curr.node_id):
            node_id = dst_node.split("::")

            if node_id[0] not in self._network_interfaces:
                continue
            network_if = self._network_interfaces[node_id[0]]

            # get node
            node = network_if.get_node(node_id[1])
            if node is None:
                continue

            # attr copy (curr -> body)
            updated = False
            curr_attr = node_curr.attributes
            for attr_key in curr_attr:
                if (attr_key in node_ignore_attributes or
                    (attr_key in node.attributes and
                     node.attributes[attr_key] == curr_attr[attr_key])):
                    continue
                updated = True
                node.attributes[attr_key] = \
                    node_curr.attributes[attr_key]

            # put node
            if updated:
                resp = network_if.put_node(node)
                try:
                    resp_node = Node.create_from_packed(resp.body)
                    resp_list[dst_node] = resp_node
                except KeyError, err:
                    logging.error("PUT Node Invalid Response Message"
                                  + " KeyError: " + str(err))

        return resp_list

    # Update Port Conversion
    def _update_port_conversion(self, network_id,
                                port_prev, port_curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or port_curr is None:
            return resp_list

        attributes_list = []
        if attributes is None:
            attributes_list = []
        else:
            attributes_list = attributes

        # get ignore list
        port_ignore_attributes = self.__get_ignore_keys(self.attributes_port,
                                                        attributes_list)

        for dst_port in self._conversion_table.get_port(network_id,
                                                        port_curr.node_id,
                                                        port_curr.port_id):
            port_id = dst_port.split("::")

            if port_id[0] not in self._network_interfaces:
                continue
            network_if = self._network_interfaces[port_id[0]]

            # get port
            port = network_if.get_port(port_id[1], port_id[2])
            if port is None:
                continue

            # attr copy (curr -> body)
            updated = False
            curr_attr = port_curr.attributes
            for attr_key in curr_attr:
                if (attr_key in port_ignore_attributes or
                    (attr_key in port.attributes and
                     port.attributes[attr_key] == curr_attr[attr_key])):
                    continue
                updated = True
                port.attributes[attr_key] = \
                    port_curr.attributes[attr_key]

            # put node
            if updated:
                resp = network_if.put_port(port)
                try:
                    resp_port = Port.create_from_packed(resp.body)
                    resp_list[dst_port] = resp_port
                except KeyError, err:
                    logging.error("PUT Port Invalid Response Message"
                                  + " KeyError: " + str(err))

        return resp_list

    # Update Link Conversion
    def _update_link_conversion(self, network_id,
                                link_prev, link_curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or link_curr is None:
            return resp_list

        attributes_list = []
        if attributes is None:
            attributes_list = []
        else:
            attributes_list = attributes

        # get ignore list
        link_ignore_attributes = self.__get_ignore_keys(self.attributes_link,
                                                        attributes_list)

        for dst_link in self._conversion_table.get_link(network_id,
                                                        link_curr.link_id):
            link_id = dst_link.split("::")

            if link_id[0] not in self._network_interfaces:
                continue
            network_if = self._network_interfaces[link_id[0]]

            # get link
            link = network_if.get_link(link_id[1])
            if link is None:
                continue

            # attr copy (curr -> body)
            updated = False
            curr_attr = link_curr.attributes
            for attr_key in curr_attr:
                if (attr_key in link_ignore_attributes or
                    (attr_key in link.attributes and
                     link.attributes[attr_key] == curr_attr[attr_key])):
                    continue
                updated = True
                link.attributes[attr_key] = \
                    link_curr.attributes[attr_key]

            # put link
            if updated:
                resp = network_if.put_link(link)
                try:
                    resp_link = Link.create_from_packed(resp.body)
                    resp_list[dst_link] = resp_link
                except KeyError, err:
                    logging.error("PUT Link Invalid Response Message"
                                  + " KeyError: " + str(err))

        return resp_list

    # Update Flow Conversion
    def _update_flow_conversion(self, network_id,
                                flow_prev, flow_curr, attributes):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or flow_curr is None:
            return resp_list

        attributes_list = []
        if attributes is None:
            attributes_list = []
        else:
            attributes_list = attributes

        # get ignore list
        flow_ignore_keys = self.__get_ignore_keys(self.keys_flow, attributes)
        flow_ignore_attributes = self.__get_ignore_keys(self.attributes_flow,
                                                        attributes_list)

        for dst_flow in self._conversion_table.get_flow(network_id,
                                                        flow_curr.flow_id):
            flow_id = dst_flow.split("::")

            if flow_id[0] not in self._network_interfaces:
                continue
            network_if = self._network_interfaces[flow_id[0]]

            # get flow
            flow = network_if.get_flow(flow_id[1])
            if flow is None:
                continue

            # key copy (curr -> body)
            updated = False
            if ("enabled" not in flow_ignore_keys
                    and flow.enabled != flow_curr.enabled):
                updated = True
                flow.enabled = flow_curr.enabled
            if ("priority" not in flow_ignore_keys
                    and flow.priority != flow_curr.priority):
                updated = True
                flow.priority = flow_curr.priority
            if ("status" not in flow_ignore_keys
                    and flow.status != flow_curr.status):
                updated = True
                flow.status = flow_curr.status

            # attr copy (curr -> body)
            curr_attr = flow_curr.attributes
            for attr_key in curr_attr:
                if (attr_key in flow_ignore_attributes or
                    (attr_key in flow.attributes and
                     flow.attributes[attr_key] == curr_attr[attr_key])):
                    continue
                updated = True
                flow.attributes[attr_key] = \
                    flow_curr.attributes[attr_key]

            # put flow
            if updated:
                resp = network_if.put_flow(flow)
                try:
                    resp_flow = Flow.create_from_packed(resp.body)
                    resp_list[dst_flow] = resp_flow
                except KeyError, err:
                    logging.error("PUT Flow Invalid Response Message"
                                  + " KeyError: " + str(err))

        return resp_list

    ###################################
    # Delete Conversion
    ###################################

    # Delete Node Conversion
    def _delete_node_conversion(self, network_id, node):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or node is None:
            return resp_list

        dst_nodes = self._conversion_table.get_node(network_id,
                                                    node.node_id)

        for dst_node in dst_nodes:
            node_id = dst_node.split("::")

            if node_id[0] not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[node_id[0]]

            resp = network_if.del_node(node_id[1])
            resp_node = None
            if resp.body is not None:
                try:
                    resp_node = Node.create_from_packed(resp.body)
                except KeyError, err:
                    logging.error("DELETE Node Invalid Response Message"
                                  + " KeyError: " + str(err))
                    return None

            resp_list[dst_node] = resp_node

        self._conversion_table.del_entry_node(network_id, node.node_id)
        return resp_list

    # Delete Port Conversion
    def _delete_port_conversion(self, network_id, port):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or port is None:
            return resp_list

        dst_ports = self._conversion_table.get_port(network_id,
                                                    port.node_id,
                                                    port.port_id)

        for dst_port in dst_ports:
            port_id = dst_port.split("::")

            if port_id[0] not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[port_id[0]]

            resp = network_if.del_port(port_id[1], port_id[2])
            resp_port = None
            if resp.body is not None:
                try:
                    resp_port = Port.create_from_packed(resp.body)
                except KeyError, err:
                    logging.error("DELETE Port Invalid Response Message"
                                  + " KeyError: " + str(err))
                    return None

            resp_list[dst_port] = resp_port

        self._conversion_table.del_entry_port(network_id,
                                              port.node_id,
                                              port.port_id)
        return resp_list

    # Delete Link Conversion
    def _delete_link_conversion(self, network_id, link):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or link is None:
            return resp_list

        dst_links = self._conversion_table.get_link(network_id,
                                                    link.link_id)

        for dst_link in dst_links:
            link_id = dst_link.split("::")

            if link_id[0] not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[link_id[0]]

            resp = network_if.del_link(link_id[1])
            resp_link = None
            if resp.body is not None:
                try:
                    resp_link = Link.create_from_packed(resp.body)
                except KeyError, err:
                    logging.error("DELETE Link Invalid Response Message"
                                  + " KeyError: " + str(err))
                    return None

            resp_list[dst_link] = resp_link

        self._conversion_table.del_entry_link(network_id, link.link_id)
        return resp_list

    # Delete Flow Conversion
    def _delete_flow_conversion(self, network_id, flow):
        logging.debug(">> %s", stack()[0][3])
        resp_list = {}
        if network_id is None or flow is None:
            return resp_list

        dst_flows = self._conversion_table.get_flow(network_id,
                                                    flow.flow_id)

        for dst_flow in dst_flows:
            flow_id = dst_flow.split("::")

            if flow_id[0] not in self._network_interfaces:
                continue

            network_if = self._network_interfaces[flow_id[0]]

            resp = network_if.del_flow(flow_id[1])
            resp_flow = None
            if resp.body is not None:
                try:
                    resp_flow = Flow.create_from_packed(resp.body)
                except KeyError, err:
                    logging.error("DELETE Flow Invalid Response Message"
                                  + " KeyError: " + str(err))
                    return None

            resp_list[dst_flow] = resp_flow

        network_if = self._network_interfaces[network_id]
        src_flow = network_if.get_flow(flow.flow_id)
        if src_flow is not None:
            src_flow.status = Flow.Status.TEARDOWN
            network_if.put_flow(src_flow)
            src_flow.status = Flow.Status.NONE
            network_if.put_flow(src_flow)

        self._conversion_table.del_entry_flow(network_id, flow.flow_id)
        return resp_list

    ###################################
    # common method
    ###################################

    def _del_in_packet(self, nw_if, packet_id):
        logging.debug(">> %s", stack()[0][3])

        resp = nw_if.del_in_packet(packet_id)
        if resp.is_error(Request.Method.DELETE):
            logging.error("invalid DELETE InPacket:" + resp.status_code)
            return None

        try:
            resp_in_packet = InPacket.create_from_packed(resp.body)

        except KeyError, err:
            logging.error("DELETE InPacket Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return resp_in_packet

    def _del_out_packet(self, nw_if, packet_id):
        logging.debug(">> %s", stack()[0][3])

        resp = nw_if.del_out_packet(packet_id)
        if resp.is_error(Request.Method.DELETE):
            logging.error("invalid DELETE OutPacket:" + resp.status_code)
            return None

        try:
            resp_out_packet = OutPacket.create_from_packed(resp.body)

        except KeyError, err:
            logging.error("DELETE OutPacket Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return resp_out_packet

    ###################################
    # private method
    ###################################

    def __get_ignore_keys(self, all_keys, update_keys):
        ignore_keys = copy.deepcopy(all_keys)

        reg_attr = re.compile("^attributes::.*")
        for update_key in update_keys:
            if reg_attr.match(update_key) is not None:
                attr_key = update_key.split("::")
                ignore_keys.remove(attr_key[1])
            else:
                ignore_keys.remove(update_key)

        logging.debug("ignore key_list:: " + str(ignore_keys))
        return ignore_keys
