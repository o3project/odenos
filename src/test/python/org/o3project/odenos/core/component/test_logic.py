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

from org.o3project.odenos.remoteobject.manager.system.event.component_connection_changed\
    import ComponentConnectionChanged
from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.core.component.logic\
    import Logic
from org.o3project.odenos.remoteobject.message.event import Event
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.packet.in_packet import InPacket
from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket
from org.o3project.odenos.core.component.network.flow.basic.basic_flow_match import\
    BasicFlowMatch
from org.o3project.odenos.core.component.network.packet.in_packet_added\
    import InPacketAdded
from org.o3project.odenos.core.component.network.packet.out_packet_added\
    import OutPacketAdded

import unittest
from contextlib import nested
from mock import Mock, MagicMock, patch


class LogicTest(unittest.TestCase):
    Message = MagicMock()
    value = {}
    result = {}

    def setUp(self):
        self.target = Logic(
            "cc_action",
            self.Message)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        conversion_table = self.target._conversion_table
        self.assertEqual(
            self.target._object_property._object_property["type"],
            "Logic")
        self.assertEqual(
            self.target._object_property._object_property["id"],
            "cc_action")
        self.assertEqual(
            conversion_table._ConversionTable__connection_type_map, {})
        self.assertEqual(
            conversion_table._ConversionTable__network_conversion_table, {})
        self.assertEqual(
            conversion_table._ConversionTable__node_conversion_table, {})
        self.assertEqual(
            conversion_table._ConversionTable__port_conversion_table, {})
        self.assertEqual(
            conversion_table._ConversionTable__link_conversion_table, {})
        self.assertEqual(
            conversion_table._ConversionTable__flow_conversion_table, {})
        self.assertEqual(self.target._network_interfaces, {})
        self.assertEqual(self.target._Logic__subscription_table, {})

    def test_do_event_componentconnectionchanged_add_action_not_Exist(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "add",
                          "prev": None,
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network1"}}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                self.target._network_interfaces["network1"].network_id,
                "network1")
            self.assertEqual(
                logging_debug.call_count, 4)

    def test_do_event_componentconnectionchanged_add_action_Exist(self):
        with patch("logging.debug") as logging_debug:
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = {"action": "add",
                          "prev": None,
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network1"}}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                self.target._network_interfaces["network1"].network_id,
                "network1")
            self.assertEqual(
                logging_debug.call_count, 3)

    def test_do_event_componentconnectionchanged_update_action(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "update",
                          "prev": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network1"},
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"}}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                logging_debug.call_count, 3)

    def test_do_event_componentconnectionchanged_delete_action(self):
        self.target._network_interfaces = {"network1": "network1_value",
                                           "network2": "network2_value"}
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "delete",
                          "prev": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"},
                          "curr": None}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                self.target._network_interfaces,
                {"network1": "network1_value"})
            self.assertEqual(
                logging_debug.call_count, 3)

    def test_do_event_componentconnectionchanged_other_action(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "other_action",
                          "prev": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"},
                          "curr": None}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_do_event_componentconnectionchanged_Error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error")) as (logging_debug,
                                                logging_error):
            self.value = {"error": "other_action"}
            self.result = Response("add", self.value)
            self.target._do_event_componentconnectionchanged(self.result)
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                logging_error.call_count, 1)

    def test_connection_changed_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "add",
                          "prev": None,
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"}}
            self.value = ComponentConnectionChanged.create_from_packed(
                self.value)
            self.result = self.target._connection_changed_added_pre(self.value)
            self.assertEqual(self.result, True)

    def test_connection_changed_update_pre(self):
        self.value = {"action": "update",
                      "prev": {"id": "slicer1->network1",
                               "type": "LogicAndNetwork",
                               "connection_type": "original",
                               "state": "initializing",
                               "logic_id": "slicer1",
                               "network_id": "network1"},
                      "curr": {"id": "slicer1->network1",
                               "type": "LogicAndNetwork",
                               "connection_type": "original",
                               "state": "initializing",
                               "logic_id": "slicer1",
                               "network_id": "network2"}}
        self.value = ComponentConnectionChanged.create_from_packed(self.value)
        self.result = self.target._connection_changed_update_pre(self.value)
        self.assertEqual(self.result, True)

    def test_connection_changed_delete_pre(self):
        self.value = {"action": "delete",
                      "prev": {"id": "slicer1->network1",
                               "type": "LogicAndNetwork",
                               "connection_type": "original",
                               "state": "initializing",
                               "logic_id": "slicer1",
                               "network_id": "network1"},
                      "curr": None}
        self.value = ComponentConnectionChanged.create_from_packed(self.value)
        self.result = self.target._connection_changed_delete_pre(self.value)
        self.assertEqual(self.result, True)

    def test_connection_changed_added(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "add",
                          "prev": None,
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"}}
            self.value =\
                ComponentConnectionChanged.create_from_packed(self.value)
            self.result =\
                self.target._connection_changed_update_pre(self.value)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_connection_changed_update(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "update",
                          "prev": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network1"},
                          "curr": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network2"}}
            self.value =\
                ComponentConnectionChanged.create_from_packed(self.value)
            self.result =\
                self.target._connection_changed_update_pre(self.value)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_connection_changed_delete(self):
        with patch("logging.debug") as logging_debug:
            self.value = {"action": "delete",
                          "prev": {"id": "slicer1->network1",
                                   "type": "LogicAndNetwork",
                                   "connection_type": "original",
                                   "state": "initializing",
                                   "logic_id": "slicer1",
                                   "network_id": "network1"},
                          "curr": None}
            self.value =\
                ComponentConnectionChanged.create_from_packed(self.value)
            self.result =\
                self.target._connection_changed_update_pre(self.value)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_add_event_subscription_network_event_type(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                "NodeChanged", "Network123")
            self.assertEqual(
                logging_debug.call_count, 1)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None})

    def test_add_event_subscription_packet_event_type(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                "InPacketAdded", "Network123")
            self.assertEqual(
                logging_debug.call_count, 1)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"InPacketAdded::Network123": None})

    def test_add_event_subscription_event_type_not_match(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                "NotType", "Network123")
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table, {})

    def test_add_event_subscription_event_type_None(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                None, "Network123")
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table, {})

    def test_add_event_subscription_network_id_None(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                "InPacketAdded", None)
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table, {})

    def test_add_event_subscription_event_type_network_id_None(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._add_event_subscription(
                None, None)
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table, {})

    def test_remove_event_subscription_network_event_type(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "NodeChanged::Network456": None}
            self.result = self.target._remove_event_subscription(
                "NodeChanged", "Network123")
            self.assertEqual(
                logging_debug.call_count, 1)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network456": None})

    def test_remove_event_subscription_packet_event_type(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None}
            self.result = self.target._remove_event_subscription(
                "OutPacketAdded", "Network123")
            self.assertEqual(
                logging_debug.call_count, 1)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None})

    def test_remove_event_subscription_event_type_not_match(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "NotType::Network123": None}
            self.result = self.target._remove_event_subscription(
                "NotType", "Network123")
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None,
                 "NotType::Network123": None})

    def test_remove_event_subscription_event_type_None(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None}
            self.result = self.target._remove_event_subscription(
                None, "Network123")
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None})

    def test_remove_event_subscription_neteork_id_None(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None}
            self.result = self.target._remove_event_subscription(
                "NodeChanged", None)
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None})

    def test_remove_event_subscription_event_type_neteork_id_None(self):
        with patch("logging.debug") as logging_debug:
            self.target._Logic__subscription_table =\
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None}
            self.result = self.target._remove_event_subscription(
                None, None)
            self.assertEqual(
                logging_debug.call_count, 0)
            self.assertEqual(
                self.target._Logic__subscription_table,
                {"NodeChanged::Network123": None,
                 "OutPacketAdded::Network123": None})

    def test_update_event_subscription_network_event_type(self):
        self.result = self.target._update_event_subscription(
            "NodeChanged", "Network123", ["attributes"])
        self.assertEqual(
            self.target._Logic__subscription_table,
            {"NodeChanged::UPDATE::Network123": ["attributes"]})

    def test_update_event_subscription_event_type_not_match(self):
        self.result = self.target._update_event_subscription(
            "NotType", "Network123", ["attributes"])
        self.assertEqual(
            self.target._Logic__subscription_table, {})

    def test_update_event_subscription_event_type_None(self):
        self.result = self.target._update_event_subscription(
            None, "Network123", ["attributes"])
        self.assertEqual(
            self.target._Logic__subscription_table, {})

    def test_update_event_subscription_neteork_id_None(self):
        self.result = self.target._update_event_subscription(
            "NodeChanged", None, ["attributes"])
        self.assertEqual(
            self.target._Logic__subscription_table, {})

    def test_update_event_subscription_event_type_neteork_id_None(self):
        self.result = self.target._update_event_subscription(
            None, None)
        self.assertEqual(
            self.target._Logic__subscription_table, {})

    def test_do_event_nodechanged_add_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_node):
            self.target._on_node_added = Mock()
            self.target._Logic__subscription_table =\
                {"NodeChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.value = {"id": "NodeId",
                          "version": "0001",
                          "action": "add",
                          "prev": None,
                          "curr": {"node": "node"}}
            mock_node.return_value = node
            self.result = Event("publisher_id", "NodeChanged", self.value)
            self.target._do_event_nodechanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_node_added.assert_called_once_with(
                "publisher_id", node)

    def test_do_event_nodechanged_update_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_node):
            self.target._on_node_update = Mock()
            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.value = {"id": "NodeId",
                          "version": "0001",
                          "action": "update",
                          "prev": {"node": "node"},
                          "curr": {"node": "node"}}
            self.result = Event("publisher_id", "NodeChanged", self.value)
            mock_node.return_value = node
            self.target._do_event_nodechanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_node_update.assert_called_once_with(
                "publisher_id", node, node, "subscription")

    def test_do_event_nodechanged_delete_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_node):
            self.target._on_node_delete = Mock()
            self.target._Logic__subscription_table =\
                {"NodeChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.value = {"id": "NodeId",
                          "version": "0001",
                          "action": "delete",
                          "prev": {"node": "node"},
                          "curr": None}
            mock_node.return_value = node
            self.result = Event("publisher_id", "NodeChanged", self.value)
            self.target._do_event_nodechanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_node_delete.assert_called_once_with(
                "publisher_id", node)

    def test_do_event_nodechanged_other_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_node):
            self.target._Logic__subscription_table =\
                {"NodeChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.value = {"id": "NodeId",
                          "version": "0001",
                          "action": "Other",
                          "prev": {"node": "node"},
                          "curr": None}
            mock_node.return_value
            self.result = Event("publisher_id", "NodeChanged", self.value)
            self.target._do_event_nodechanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 2)

    def test_do_event_nodechanged_key_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error")) as (logging_debug,
                                                logging_error):
            self.target._on_node_delete = Mock()
            self.target._Logic__subscription_table =\
                {"NodeChanged::publisher_id": "subscription"}
            self.value = {"node_id": "NodeId",
                          "version": "0001",
                          "action": "Other",
                          "prev": {"node": "node"},
                          "curr": None}
            self.result = Event("publisher_id", "NodeChanged", self.value)
            self.target._do_event_nodechanged(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_do_event_portchanged_add_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_port):
            self.target._on_port_added = Mock()
            self.target._Logic__subscription_table =\
                {"PortChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "id": "PortId",
                          "version": "0001",
                          "action": "add",
                          "prev": None,
                          "curr": {"node": "node"}}
            mock_port.return_value = port
            self.result = Event("publisher_id", "PortChanged", self.value)
            self.target._do_event_portchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_port_added.assert_called_once_with(
                "publisher_id", port)

    def test_do_event_portchanged_update_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_port):
            self.target._on_port_update = Mock()
            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "id": "PortId",
                          "version": "0001",
                          "action": "update",
                          "prev": {},
                          "curr": None}
            mock_port.return_value = port
            self.result = Event("publisher_id", "PortChanged", self.value)
            self.target._do_event_portchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_port_update.assert_called_once_with(
                "publisher_id", port, port, "subscription")

    def test_do_event_portchanged_delete_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_port):
            self.target._on_port_delete = Mock()
            self.target._Logic__subscription_table =\
                {"PortChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "id": "PortId",
                          "version": "0001",
                          "action": "delete",
                          "prev": {},
                          "curr": None}
            mock_port.return_value = port
            self.result = Event("publisher_id", "PortChanged", self.value)
            self.target._do_event_portchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_port_delete.assert_called_once_with(
                "publisher_id", port)

    def test_do_event_portchanged_other_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_port):
            self.target._on_port_delete = Mock()
            self.target._Logic__subscription_table =\
                {"PortChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "id": "PortId",
                          "version": "0001",
                          "action": "other",
                          "prev": {},
                          "curr": None}
            mock_port.return_value = port
            self.result = Event("publisher_id", "PortChanged", self.value)
            self.target._do_event_portchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 2)

    def test_do_event_portchanged_key_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_port):
            self.target._on_port_delete = Mock()
            self.target._Logic__subscription_table =\
                {"PortChanged::publisher_id": "subscription"}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "error_id": "PortId",
                          "version": "0001",
                          "action": "other",
                          "prev": {},
                          "curr": None}
            mock_port.return_value = port
            self.result = Event("publisher_id", "PortChanged", self.value)
            self.target._do_event_portchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_do_event_linkchanged_add_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_link):
            self.target._on_link_added = Mock()
            self.target._Logic__subscription_table =\
                {"LinkChanged::publisher_id": "subscription"}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "add",
                          "prev": None,
                          "curr": {"node": "node"}}
            mock_link.return_value = link
            self.result = Event("publisher_id", "LinkChanged", self.value)
            self.target._do_event_linkchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_link_added.assert_called_once_with(
                "publisher_id", link)

    def test_do_event_linkchanged_update_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_link):
            self.target._on_link_update = Mock()
            self.target._Logic__subscription_table =\
                {"LinkChanged::UPDATE::publisher_id": "subscription"}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "update",
                          "prev": {"node": "node"},
                          "curr": {"node": "node"}}
            mock_link.return_value = link
            self.result = Event("publisher_id", "LinkChanged", self.value)
            self.target._do_event_linkchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_link_update.assert_called_once_with(
                "publisher_id", link, link, "subscription")

    def test_do_event_linkchanged_delete_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_link):
            self.target._on_link_delete = Mock()
            self.target._Logic__subscription_table =\
                {"LinkChanged::publisher_id": "subscription"}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "delete",
                          "prev": {"node": "node"},
                          "curr": None}
            mock_link.return_value = link
            self.result = Event("publisher_id", "LinkChanged", self.value)
            self.target._do_event_linkchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_link_delete.assert_called_once_with(
                "publisher_id", link)

    def test_do_event_linkchanged_other_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_link):
            self.target._Logic__subscription_table =\
                {"LinkChanged::publisher_id": "subscription"}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "other",
                          "prev": {"node": "node"},
                          "curr": None}
            mock_link.return_value = link
            self.result = Event("publisher_id", "LinkChanged", self.value)
            self.target._do_event_linkchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 2)

    def test_do_event_linkchanged_key_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_link):
            self.target._Logic__subscription_table =\
                {"LinkChanged::publisher_id": "subscription"}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.value = {"node_id": "NodeId",
                          "error_id": "PortId",
                          "version": "0001",
                          "action": "other",
                          "prev": {"node": "node"},
                          "curr": None}
            mock_link.return_value = link
            self.result = Event("publisher_id", "LinkChanged", self.value)
            self.target._do_event_linkchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_do_event_flowchanged_add_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_flow):
            self.target._on_flow_added = Mock()
            self.target._Logic__subscription_table =\
                {"FlowChanged::publisher_id": "subscription"}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "add",
                          "prev": None,
                          "curr": {"type": "Flow"}}
            mock_flow.return_value = flow
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_flowchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_flow_added.assert_called_once_with(
                "publisher_id", flow)

    def test_do_event_flowchanged_update_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_flow):
            self.target._on_flow_update = Mock()
            self.target._Logic__subscription_table =\
                {"FlowChanged::UPDATE::publisher_id": "subscription"}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "update",
                          "prev": {"type": "Flow"},
                          "curr": {"type": "Flow"}}
            mock_flow.return_value = flow
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_flowchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_flow_update.assert_called_once_with(
                "publisher_id", flow, flow, "subscription")

    def test_do_event_flowchanged_delete_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_flow):
            self.target._on_flow_delete = Mock()
            self.target._Logic__subscription_table =\
                {"FlowChanged::publisher_id": "subscription"}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "delete",
                          "prev": {"type": "Flow"},
                          "curr": None}
            mock_flow.return_value = flow
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_flowchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_flow_delete.assert_called_once_with(
                "publisher_id", flow)

    def test_do_event_flowchanged_other_action(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_flow):
            self.target._Logic__subscription_table =\
                {"FlowChanged::publisher_id": "subscription"}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.value = {"id": "PortId",
                          "version": "0001",
                          "action": "other",
                          "prev": None,
                          "curr": {"type": "Flow"}}
            mock_flow.return_value = flow
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_flowchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 2)

    def test_do_event_flowchanged_key_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_flow):
            self.target._Logic__subscription_table =\
                {"FlowChanged::publisher_id": "subscription"}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.value = {"error_id": "PortId",
                          "version": "0001",
                          "action": "add",
                          "prev": None,
                          "curr": {"type": "Flow"}}
            mock_flow.return_value = flow
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_flowchanged(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_do_event_inpacketadded_success(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet_added.InPacketAdded."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_in_packet_added):
            mock_in_packet_added.return_value = "Dummy"
            self.target._on_in_packet_added_pre = Mock(
                return_value=True)
            self.target._add_in_packet_conversion = Mock(
                return_value="resp_list")
            self.target._on_in_packet_added_post = Mock()
            self.value = {"id": "InPacketAdded",
                          "version": "0001"}
            self.result = Event("publisher_id", "InPacketAdded", self.value)
            self.target._do_event_inpacketadded(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_in_packet_added_pre.assert_called_once_with(
                "publisher_id", "Dummy")
            self.target._add_in_packet_conversion.assert_called_once_with(
                "publisher_id", "Dummy")
            self.target._on_in_packet_added_post.assert_called_once_with(
                "publisher_id", "Dummy", "resp_list")

    def test_do_event_inpacketadded_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error")) as (logging_debug,
                                                logging_error):
            self.value = {"error_id": "InPacketAdded",
                          "version": "0001"}
            self.result = Event("publisher_id", "InPacketAdded", self.value)
            self.target._do_event_inpacketadded(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_on_in_packet_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_in_packet_added_pre("network_id", "msg")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_in_packet_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_in_packet_added_post("network_id", "msg",
                                                 "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_do_event_outpacketadded_success(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet_added.OutPacketAdded."
                          "create_from_packed")) as (logging_debug,
                                                     logging_error,
                                                     mock_out_packet_added):
            mock_out_packet_added.return_value = "Dummy"
            self.target._on_out_packet_added_pre = Mock(
                return_value=True)
            self.target._add_out_packet_conversion = Mock(
                return_value="resp_list")
            self.target._on_out_packet_added_post = Mock()
            self.value = {"id": "OutPacketAdded",
                          "version": "0001"}
            self.result = Event("publisher_id", "OutPacketAdded", self.value)
            self.target._do_event_outpacketadded(self.result)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                logging_debug.call_count, 1)
            self.target._on_out_packet_added_pre.assert_called_once_with(
                "publisher_id", "Dummy")
            self.target._add_out_packet_conversion.assert_called_once_with(
                "publisher_id", "Dummy")
            self.target._on_out_packet_added_post.assert_called_once_with(
                "publisher_id", "Dummy", "resp_list")

    def test_do_event_outpacketadded_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error")) as (logging_debug,
                                                logging_error):
            self.value = {"error_id": "OutPacketAdded",
                          "version": "0001"}
            self.result = Event("publisher_id", "FlowChanged", self.value)
            self.target._do_event_outpacketadded(self.result)
            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                logging_debug.call_count, 0)

    def test_on_out_packet_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_out_packet_added_pre("network_id", "msg")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_out_packet_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_out_packet_added_post("network_id", "msg",
                                                  "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_added(self):
        self.target._on_node_added_pre = Mock(
            return_value=True)
        self.target._add_node_conversion = Mock(
            return_value="resp_list")
        self.target._on_node_added_post = Mock()

        self.target._on_node_added("network_id", "node_msg")
        self.target._on_node_added_pre.assert_called_once_with(
            "network_id", "node_msg")
        self.target._add_node_conversion.assert_called_once_with(
            "network_id", "node_msg")
        self.target._on_node_added_post.assert_called_once_with(
            "network_id", "node_msg", "resp_list")

    def test_on_node_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_node_added_pre("network_id",
                                                         "node_msg")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_node_added_post("network_id", "msg", "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_added(self):
        self.target._on_port_added_pre = Mock(
            return_value=True)
        self.target._add_port_conversion = Mock(
            return_value="resp_list")
        self.target._on_port_added_post = Mock()

        self.target._on_port_added("network_id", "port_msg")
        self.target._on_port_added_pre.assert_called_once_with(
            "network_id", "port_msg")
        self.target._add_port_conversion.assert_called_once_with(
            "network_id", "port_msg")
        self.target._on_port_added_post.assert_called_once_with(
            "network_id", "port_msg", "resp_list")

    def test_on_port_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_port_added_pre("network_id",
                                                         "port_msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_port_added_post("network_id", "msg", "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_added(self):
        self.target._on_link_added_pre = Mock(
            return_value=True)
        self.target._add_link_conversion = Mock(
            return_value="resp_list")
        self.target._on_link_added_post = Mock()

        self.target._on_link_added("network_id", "link_msg")
        self.target._on_link_added_pre.assert_called_once_with(
            "network_id", "link_msg")
        self.target._add_link_conversion.assert_called_once_with(
            "network_id", "link_msg")
        self.target._on_link_added_post.assert_called_once_with(
            "network_id", "link_msg", "resp_list")

    def test_on_link_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_link_added_pre("network_id",
                                                         "link_msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_link_added_post("network_id", "msg", "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_added(self):
        self.target._on_flow_added_pre = Mock(
            return_value=True)
        self.target._add_flow_conversion = Mock(
            return_value="resp_list")
        self.target._on_flow_added_post = Mock()

        self.target._on_flow_added("network_id", "flow_msg")
        self.target._on_flow_added_pre.assert_called_once_with(
            "network_id", "flow_msg")
        self.target._add_flow_conversion.assert_called_once_with(
            "network_id", "flow_msg")
        self.target._on_flow_added_post.assert_called_once_with(
            "network_id", "flow_msg", "resp_list")

    def test_on_flow_added_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_flow_added_pre("network_id",
                                                         "flow_msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_added_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_flow_added_post("network_id", "msg", "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_update(self):
        self.target._on_node_update_pre = Mock(
            return_value=True)
        self.target._update_node_conversion = Mock(
            return_value="resp_list")
        self.target._on_node_update_post = Mock()

        self.target._on_node_update("network_id", "prev", "curr", "sttributes")
        self.target._on_node_update_pre.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._update_node_conversion.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._on_node_update_post.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes", "resp_list")

    def test_on_node_update_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_node_update_pre("network_id",
                                                          "prev",
                                                          "curr",
                                                          "sttributes")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_update_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_node_update_post("network_id",
                                             "prev",
                                             "curr",
                                             "sttributes",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_update(self):
        self.target._on_port_update_pre = Mock(
            return_value=True)
        self.target._update_port_conversion = Mock(
            return_value="resp_list")
        self.target._on_port_update_post = Mock()

        self.target._on_port_update("network_id", "prev", "curr", "sttributes")
        self.target._on_port_update_pre.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._update_port_conversion.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._on_port_update_post.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes", "resp_list")

    def test_on_port_update_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_port_update_pre("network_id",
                                                          "prev",
                                                          "curr",
                                                          "sttributes")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_update_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_port_update_post("network_id",
                                             "prev",
                                             "curr",
                                             "sttributes",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_update(self):
        self.target._on_link_update_pre = Mock(
            return_value=True)
        self.target._update_link_conversion = Mock(
            return_value="resp_list")
        self.target._on_link_update_post = Mock()

        self.target._on_link_update("network_id", "prev", "curr", "sttributes")
        self.target._on_link_update_pre.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._update_link_conversion.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._on_link_update_post.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes", "resp_list")

    def test_on_link_update_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_link_update_pre("network_id",
                                                          "prev",
                                                          "curr",
                                                          "sttributes")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_update_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_link_update_post("network_id",
                                             "prev",
                                             "curr",
                                             "sttributes",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_update(self):
        self.target._on_flow_update_pre = Mock(
            return_value=True)
        self.target._update_flow_conversion = Mock(
            return_value="resp_list")
        self.target._on_flow_update_post = Mock()

        self.target._on_flow_update("network_id", "prev", "curr", "sttributes")
        self.target._on_flow_update_pre.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._update_flow_conversion.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes")
        self.target._on_flow_update_post.assert_called_once_with(
            "network_id", "prev", "curr", "sttributes", "resp_list")

    def test_on_flow_update_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_flow_update_pre("network_id",
                                                          "prev",
                                                          "curr",
                                                          "sttributes")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_update_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_flow_update_post("network_id",
                                             "prev",
                                             "curr",
                                             "sttributes",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_delete(self):
        self.target._on_node_delete_pre = Mock(
            return_value=True)
        self.target._delete_node_conversion = Mock(
            return_value="resp_list")
        self.target._on_node_delete_post = Mock()

        self.target._on_node_delete("network_id", "msg")
        self.target._on_node_delete_pre.assert_called_once_with(
            "network_id", "msg")
        self.target._delete_node_conversion.assert_called_once_with(
            "network_id", "msg")
        self.target._on_node_delete_post.assert_called_once_with(
            "network_id", "msg", "resp_list")

    def test_on_node_delete_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_node_delete_pre("network_id",
                                                          "msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_node_delete_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_node_delete_post("network_id",
                                             "msg",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_delete(self):
        self.target._on_port_delete_pre = Mock(
            return_value=True)
        self.target._delete_port_conversion = Mock(
            return_value="resp_list")
        self.target._on_port_delete_post = Mock()

        self.target._on_port_delete("network_id", "msg")
        self.target._on_port_delete_pre.assert_called_once_with(
            "network_id", "msg")
        self.target._delete_port_conversion.assert_called_once_with(
            "network_id", "msg")
        self.target._on_port_delete_post.assert_called_once_with(
            "network_id", "msg", "resp_list")

    def test_on_port_delete_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_port_delete_pre("network_id",
                                                          "msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_port_delete_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_port_delete_post("network_id",
                                             "msg",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_delete(self):
        self.target._on_link_delete_pre = Mock(
            return_value=True)
        self.target._delete_link_conversion = Mock(
            return_value="resp_list")
        self.target._on_link_delete_post = Mock()

        self.target._on_link_delete("network_id", "msg")
        self.target._on_link_delete_pre.assert_called_once_with(
            "network_id", "msg")
        self.target._delete_link_conversion.assert_called_once_with(
            "network_id", "msg")
        self.target._on_link_delete_post.assert_called_once_with(
            "network_id", "msg", "resp_list")

    def test_on_link_delete_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_link_delete_pre("network_id",
                                                          "msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_link_delete_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_link_delete_post("network_id",
                                             "msg",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_delete(self):
        self.target._on_flow_delete_pre = Mock(
            return_value=True)
        self.target._delete_flow_conversion = Mock(
            return_value="resp_list")
        self.target._on_flow_delete_post = Mock()

        self.target._on_flow_delete("network_id", "msg")
        self.target._on_flow_delete_pre.assert_called_once_with(
            "network_id", "msg")
        self.target._delete_flow_conversion.assert_called_once_with(
            "network_id", "msg")
        self.target._on_flow_delete_post.assert_called_once_with(
            "network_id", "msg", "resp_list")

    def test_on_flow_delete_pre(self):
        with patch("logging.debug") as logging_debug:
            self.result = self.target._on_flow_delete_pre("network_id",
                                                          "msg")
            self.assertEqual(self.result, True)
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_on_flow_delete_post(self):
        with patch("logging.debug") as logging_debug:
            self.target._on_flow_delete_post("network_id",
                                             "msg",
                                             "resp_list")
            self.assertEqual(
                logging_debug.call_count, 1)

    def test_add_node_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node
            mock_put_object.return_value = self.value

            self.result = self.target._add_node_conversion("network1",
                                                           node)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "node_item")
            self.assertEqual(
                conversion_table._ConversionTable__node_conversion_table,
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]})

    def test_add_node_conversion_not_in__network_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "node_item")

            mock_node.return_value = node
            mock_put_object.return_value = self.value

            self.result = self.target._add_node_conversion("network1",
                                                           node)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})
            self.assertEqual(
                conversion_table._ConversionTable__node_conversion_table,
                {})

    def test_add_node_conversion_error(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            node = Node("Node", "0001", "Node01",
                        {"port_id": port}, {"attribute_key": "value"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"key": "error"})

            mock_put_object.return_value = self.value

            self.result = self.target._add_node_conversion("network1",
                                                           node)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result["network2"].body, {"key": "error"})
            self.assertEqual(
                conversion_table._ConversionTable__node_conversion_table,
                {})

    def test_add_port_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port
            mock_put_object.return_value = self.value

            self.result = self.target._add_port_conversion("network1",
                                                           port)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "port_item")
            self.assertEqual(
                conversion_table._ConversionTable__port_conversion_table,
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]})

    def test_add_port_conversion_not_in_network_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "port_item")

            mock_port.return_value = port
            mock_put_object.return_value = self.value

            self.result = self.target._add_port_conversion("network1",
                                                           port)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})
            self.assertEqual(
                conversion_table._ConversionTable__port_conversion_table,
                {})

    def test_add_port_conversion_error(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            port = Port("Port", "1", "PortId", "NodeId",
                        "OutLink", "InLink", {"PortKey": "PortVal"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"key": "error"})

            mock_put_object.return_value = self.value

            self.result = self.target._add_port_conversion("network1",
                                                           port)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result["network2"].body, {"key": "error"})
            self.assertEqual(
                conversion_table._ConversionTable__port_conversion_table,
                {})

    def test_add_link_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link
            mock_put_object.return_value = self.value

            self.result = self.target._add_link_conversion("network1",
                                                           link)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "link_item")
            self.assertEqual(
                conversion_table._ConversionTable__link_conversion_table,
                {"network1::LinkId": ["network2::LinkId"],
                 "network2::LinkId": ["network1::LinkId"]})

    def test_add_link_conversion_not_in_network_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "link_item")

            mock_link.return_value = link
            mock_put_object.return_value = self.value

            self.result = self.target._add_link_conversion("network1",
                                                           link)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})
            self.assertEqual(
                conversion_table._ConversionTable__link_conversion_table,
                {})

    def test_add_link_conversion_error(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            link = Link("Link", "1", "LinkId", "SrcNode",
                        "SrcPort", "DstNode", "DstPort",
                        {"PortKey": "PortVal"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"key": "error"})

            mock_put_object.return_value = self.value

            self.result = self.target._add_link_conversion("network1",
                                                           link)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result["network2"].body, {"key": "error"})
            self.assertEqual(
                conversion_table._ConversionTable__link_conversion_table,
                {})

    def test_add_flow_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "flow_item")

            mock_put_object.return_value = self.value

            self.result = self.target._add_flow_conversion("network1",
                                                           flow)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "flow_item")

    def test_add_flow_conversion_not_in_network_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            flow = Flow("BasicFlow", "1", "FlowId", "Owner",
                        True, 123456789, "establishing",
                        {"PortKey": "PortVal"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "flow_item")

            mock_put_object.return_value = self.value

            self.result = self.target._add_flow_conversion("network1",
                                                           flow)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "inpacket_item")

    def test_add_in_packet_conversionnot_in_network_interfaces(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_del_in_packet_None(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = None
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_del_in_packet_node_None(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                None, "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_convert_in_node_id_list_None(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_cdel_in_packet_port_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", None, basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_convert_in_port_id_list_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_header_in_port_id_list_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "Node01",
                                              "Port01")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_in_packet_conversion_attr_list_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object,
                          mock_post_object):
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")
            inpacket_add = InPacketAdded("inpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "inpacket_item")

            mock_del_object.return_value = self.value
            mock_in_packet.return_value = inpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_in_packet_conversion("network1",
                                                                inpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_ports_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network2::NodeId": ["network1::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", ["PortId"], None,
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "outpacket_item")

    def test_add_out_packet_conversion_portsEx_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::PortExId": ["network2::PortExId"],
                 "network1::NodeId": ["network2::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortExId": ["network2::NodeId::PortExId"],
                 "network1::NodeId::PortId": ["network2::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", None, ["PortExId"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2"].body, "outpacket_item")

    def test_add_out_packet_conversion_network_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", ["PortId"], ["Ports_ex"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_del_out_packet_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", ["PortId"], ["Ports_ex"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = None
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_convert_port_id_list_zero(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", ["PortId"], ["Ports_ex"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_del_out_packet_node_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  None, ["PortId"], ["Ports_ex"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_convert_node_id_list_zero(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", ["PortId"], ["Ports_ex"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_ports_list_zero(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::NodeId": ["network2::NodeId"],
                 "network1::Node01": ["network2::Node01"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortId": ["network2::NodeId::PortId"],
                 "network2::NodeId::PortId": ["network1::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "Node01", ["Port01"], None,
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_add_out_packet_conversion_portsEx_list_zero(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_post_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object,
                          mock_post_object):

            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::PortExId": ["network2::PortExId"],
                 "network1::NodeId": ["network2::NodeId"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::NodeId::PortEx": ["network2::NodeId::PortEx"],
                 "network1::NodeId::PortId": ["network2::NodeId::PortId"]}
            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", None, ["PortExId"],
                                  basic_flow_match, "Data")
            outpacket_add = OutPacketAdded("outpacket_id")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] =\
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "outpacket_item")

            mock_del_object.return_value = self.value
            mock_out_packet.return_value = outpacket
            mock_post_object.return_value = self.value

            self.result = self.target._add_out_packet_conversion("network1",
                                                                 outpacket_add)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Node01"].packed_object(),
                node_prev.packed_object())

    def test_update_node_conversion_network_id_None(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion(None,
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_node_curr_None(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              None,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_attributes_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Node01"].packed_object(),
                node_prev.packed_object())

    def test_update_node_conversion_node_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_success_get_node_false(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute_curr": "prev_curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = None
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_attr_key_in_ignore_attributes(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::network1": ["physical_id", "vendor"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "1", "Node01", {"port_id": port_prev},
                             {"oper_status": "DOWN"})
            node_curr = Node("Node", "2", "Node01", {"port_id": port_curr},
                             {"oper_status": "UP"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["physical_id", "vendor"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_attributes_exist(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute": "value"})
            port_curr = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_curr = Node("Node", "0001", "Node01",
                             {"port_id": port_curr},
                             {"attribute": "value"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_node_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        port_prev = Port("Port", "1", "PortId", "NodeId",
                         "OutLink", "InLink", {"PortKey": "PortVal"})
        node_prev = Node("Node", "0001", "Node02",
                         {"port_id": port_prev},
                         {"attribute_prev": "prev_value"})
        port_curr = Port("Port", "1", "PortId", "NodeId",
                         "OutLink", "InLink", {"PortKey": "PortVal"})
        node_curr = Node("Node", "0001", "Node01",
                         {"port_id": port_curr},
                         {"attribute_curr": "prev_curr"})

        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util." +
                          "network_interface.NetworkInterface.get_node")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_get_node):
            self.target._Logic__subscription_table =\
                {"NodeChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node01": ["network1::Node01"]}
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "node_item")

            mock_node.side_effect = KeyError()
            mock_get_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_node_conversion("network1",
                                                              node_prev,
                                                              node_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Node02::Port02"].packed_object(),
                port_prev.packed_object())

    def test_update_port_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion(None,
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_port_curr_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              None,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_attributes_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Node02::Port02"].packed_object(),
                port_prev.packed_object())

    def test_update_port_conversion_port_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_port_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = None
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_attr_key_ignore_attributes(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"max_bandwidth": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_attributes_exist(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node02::Port02": ["network2::Node02::Port02"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "curr"})
            port_curr = Port("Port", "1", "Port02", "Node02",
                             "OutLink", "InLink", {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_port_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        self.target._Logic__subscription_table =\
            {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
        conversion_table._ConversionTable__network_conversion_table =\
            {"network1": ["network2"]}
        conversion_table._ConversionTable__port_conversion_table =\
            {"network1::Node02::Port02": ["network2::Node02::Port02"],
             "network2::Node02::Port02": ["network1::Node02::Port02"]}
        port_prev = Port("Port", "1", "Port01", "Node01",
                         "OutLink", "InLink", {"PortKey": "prev"})
        port_curr = Port("Port", "1", "Port02", "Node02",
                         "OutLink", "InLink", {"PortKey": "curr"})

        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util." +
                          "network_interface.NetworkInterface.get_port")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_put_object,
                          mock_get_port):
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_get_port.return_value = port_prev
            mock_port.side_effect = KeyError()
            mock_put_object.return_value = self.value

            self.result = self.target._update_port_conversion("network1",
                                                              port_prev,
                                                              port_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Link02"].packed_object(),
                link_prev.packed_object())

    def test_update_link_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion(None,
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_link_curr_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              None,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_attributes(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Link02"].packed_object(),
                link_prev.packed_object())

    def test_update_link_conversion_link_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_line_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = None
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_attr_key_in_ignore_attributes(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"unreserved_bandwidth": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_attributes_exist(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link02": ["network2::Link02"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            link_curr = Link("Link", "1", "Link02", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_link_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        conversion_table._ConversionTable__network_conversion_table =\
            {"network1": ["network2"]}
        conversion_table._ConversionTable__link_conversion_table =\
            {"network1::Link02": ["network2::Link02"],
             "network2::Link02": ["network1::Link02"]}
        link_prev = Link("Link", "1", "Link01", "SrcNode",
                         "SrcPort", "DstNode", "DstPort",
                         {"PortKey": "prev"})
        link_curr = Link("Link", "1", "Link02", "SrcNode",
                         "SrcPort", "DstNode", "DstPort",
                         {"PortKey": "curr"})

        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util." +
                          "network_interface.NetworkInterface.get_link")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          Mock_get_link):
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            Mock_get_link.return_value = link_prev
            mock_link.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_link_conversion("network1",
                                                              link_prev,
                                                              link_curr,
                                                              ["oper_status"])

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 987654321, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Flow02"].packed_object(),
                flow_prev.packed_object())

    def test_update_flow_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 987654321, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                None, flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_flow_curr_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 987654321, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, None, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_attributes_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 987654321, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Flow02"].packed_object(),
                flow_prev.packed_object())

    def test_update_flow_conversion_flow_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "curr"})
            self.value = Response(200, {"type": "Flow"})

            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_flow_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_get_flow.return_value = None
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_differ_enabled(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             False, 123456789, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Flow02"].packed_object(),
                flow_prev.packed_object())

    def test_update_flow_conversion_differ_status(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 123456789, "teardown",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Flow02"].packed_object(),
                flow_prev.packed_object())

    def test_update_flow_conversion_attr_key_in_ignore_attributes(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 123456789, "establishing",
                             {"bandwidth": "curr"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["enabled", "priority",
                                   "status", "bandwidth"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_attributes_exist(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow02": ["network2::Flow02"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "curr"})
            flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "curr"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["enabled", "priority", "status"])
            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_update_flow_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        conversion_table._ConversionTable__network_conversion_table =\
            {"network1": ["network2"]}
        conversion_table._ConversionTable__flow_conversion_table =\
            {"network1::Flow02": ["network2::Flow02"],
             "network2::Flow02": ["network1::Flow02"]}
        flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                         True, 123456789, "establishing",
                         {"PortKey": "prev"})
        flow_curr = Flow("BasicFlow", "1", "Flow02", "Owner",
                         True, 987654321, "establishing",
                         {"PortKey": "curr"})

        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util.network_interface."
                          "NetworkInterface.get_flow")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow):
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_get_flow.return_value = flow_prev
            self.target._Logic__get_ignore_keys =\
                Mock(return_value=["bandwidth", "latency", "req_latency"])
            mock_flow.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value

            self.result = self.target._update_flow_conversion(
                "network1", flow_prev, flow_curr, ["priority"])

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, {})

    def test_delete_node_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node02": ["network1::Node02"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, {"item": "node_item"})

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_node_conversion("network2",
                                                              node_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                conversion_table._ConversionTable__node_conversion_table,
                {"network1::Node01": ["network2::Node01"]})
            self.assertEqual(
                self.result["network1::Node02"].packed_object(),
                node_prev.packed_object())

    def test_delete_node_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node02": ["network1::Node02"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, {"item": "node_item"})

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_node_conversion(None,
                                                              node_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_node_conversion_node_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node02": ["network1::Node02"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, {"item": "node_item"})

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_node_conversion("network2",
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_node_conversion_node_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node02": ["network1::Node02"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            self.value = Response(200, {"item": "node_item"})

            mock_node.return_value = node_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_node_conversion("network2",
                                                              node_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_node_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "node.Node."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_node,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__node_conversion_table =\
                {"network1::Node01": ["network2::Node01"],
                 "network2::Node02": ["network1::Node02"]}
            port_prev = Port("Port", "1", "PortId", "NodeId",
                             "OutLink", "InLink", {"PortKey": "PortVal"})
            node_prev = Node("Node", "0001", "Node02",
                             {"port_id": port_prev},
                             {"attribute_prev": "prev_value"})
            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, {"key": "error"})

            mock_node.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_node_conversion("network2",
                                                              node_prev)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_delete_port_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node01::Port01": ["network2::Node01::Port01"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_port_conversion("network1",
                                                              port_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Node01::Port01"].packed_object(),
                port_prev.packed_object())
            self.assertEqual(
                conversion_table._ConversionTable__port_conversion_table,
                {"network2::Node02::Port02": ["network1::Node02::Port02"]})

    def test_delete_port_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node01::Port01": ["network2::Node01::Port01"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_port_conversion(None,
                                                              port_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_port_conversion_port_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node01::Port01": ["network2::Node01::Port01"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_port_conversion("network1",
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_port_conversion_port_id_not_in_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node01::Port01": ["network2::Node01::Port01"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            self.value = Response(200, "port_item")

            mock_port.return_value = port_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_port_conversion("network1",
                                                              port_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_port_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "port.Port."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_port,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            self.target._Logic__subscription_table =\
                {"PortChanged::UPDATE::publisher_id": ["oper_status"]}
            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__port_conversion_table =\
                {"network1::Node01::Port01": ["network2::Node01::Port01"],
                 "network2::Node02::Port02": ["network1::Node02::Port02"]}

            port_prev = Port("Port", "1", "Port01", "Node01",
                             "OutLink", "InLink", {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "port_item")

            mock_port.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_port_conversion("network1",
                                                              port_prev)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_delete_link_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link01": ["network2::Link01"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_link_conversion("network1",
                                                              link_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Link01"].packed_object(),
                link_prev.packed_object())
            self.assertEqual(
                conversion_table._ConversionTable__link_conversion_table,
                {"network2::Link02": ["network1::Link02"]})

    def test_delete_link_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link01": ["network2::Link01"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_link_conversion(None,
                                                              link_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_link_conversion_link_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link01": ["network2::Link01"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_link_conversion("network1",
                                                              None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_link_conversion_link_in_not_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link01": ["network2::Link01"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            self.value = Response(200, "link_item")

            mock_link.return_value = link_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_link_conversion("network1",
                                                              link_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_link_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.topology."
                          "link.Link."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_link,
                          mock_get_object,
                          mock_put_object,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__link_conversion_table =\
                {"network1::Link01": ["network2::Link01"],
                 "network2::Link02": ["network1::Link02"]}
            link_prev = Link("Link", "1", "Link01", "SrcNode",
                             "SrcPort", "DstNode", "DstPort",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, "link_item")

            mock_link.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value

            self.result = self.target._delete_link_conversion("network1",
                                                              link_prev)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_delete_flow_conversion_success(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "get_flow"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow01": ["network2::Flow01"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value
            mock_get_flow.return_value = flow_prev

            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")

            self.result = self.target._delete_flow_conversion(
                "network1", flow_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result["network2::Flow01"].packed_object(),
                flow_prev.packed_object())
            self.assertEqual(
                conversion_table._ConversionTable__flow_conversion_table,
                {"network2::Flow02": ["network1::Flow02"]})

    def test_delete_flow_conversion_network_id_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "get_flow"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow01": ["network2::Flow01"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value
            mock_get_flow.return_value = self.value

            self.result = self.target._delete_flow_conversion(
                None, flow_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_flow_conversion_link_None(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "get_flow"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow01": ["network2::Flow01"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value
            mock_get_flow.return_value = self.value

            self.result = self.target._delete_flow_conversion(
                "network1", None)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_flow_conversion_flow_id_interfaces(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "get_flow"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow01": ["network2::Flow01"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            self.value = Response(200, {"type": "Flow"})

            mock_flow.return_value = flow_prev
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value
            mock_get_flow.return_value = flow_prev

            self.target._network_interfaces["network1"] = \
                NetworkInterface(self.target.dispatcher, "network1")

            self.result = self.target._delete_flow_conversion(
                "network1", flow_prev)

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result, {})

    def test_delete_flow_conversion_KeyError(self):
        conversion_table = self.target._conversion_table
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.flow."
                          "flow.Flow."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_get_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_put_object_to_remote_object"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "get_flow"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_flow,
                          mock_get_object,
                          mock_put_object,
                          mock_get_flow,
                          mock_del_object):

            conversion_table._ConversionTable__network_conversion_table =\
                {"network1": ["network2"]}
            conversion_table._ConversionTable__flow_conversion_table =\
                {"network1::Flow01": ["network2::Flow01"],
                 "network2::Flow02": ["network1::Flow02"]}
            flow_prev = Flow("BasicFlow", "1", "Flow01", "Owner",
                             True, 123456789, "establishing",
                             {"PortKey": "prev"})
            self.target._network_interfaces["network2"] = \
                NetworkInterface(self.target.dispatcher, "network2")
            self.value = Response(200, {"type": "Flow"})

            mock_flow.side_effect = KeyError()
            mock_get_object.return_value = self.value
            mock_put_object.return_value = self.value
            mock_del_object.return_value = self.value
            mock_get_flow.return_value = self.value

            self.result = self.target._delete_flow_conversion(
                "network1", flow_prev)

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_del_in_packet_conversion_success(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")

            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "inpacket_item")

            mock_in_packet.return_value = inpacket
            mock_del_object.return_value = self.value

            self.result = self.target._del_in_packet(
                self.target._network_interfaces["network1"], "inpacket_id")

            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result.packed_object(), inpacket.packed_object())

    def test_del_in_packet_conversion_Response_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")

            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response("400", "inpacket_item")

            mock_in_packet.return_value = inpacket
            mock_del_object.return_value = self.value

            self.result = self.target._del_in_packet(
                self.target._network_interfaces["network1"], "inpacket_id")

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_del_in_packet_conversion_KeyError(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "in_packet.InPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_in_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            inpacket = InPacket("inpacket_id", "InPcket", "attributes",
                                "NodeId", "PortId", basic_flow_match,
                                "Data")

            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "inpacket_item")

            mock_in_packet.side_effect = KeyError()
            mock_del_object.return_value = self.value

            self.result = self.target._del_in_packet(
                self.target._network_interfaces["network1"], "inpacket_id")

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_del_out_packet_conversion_success(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", "Ports", "Ports_ex",
                                  basic_flow_match, "Data")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "outpacket_item")

            mock_out_packet.return_value = outpacket
            mock_del_object.return_value = self.value

            self.result = self.target._del_out_packet(
                self.target._network_interfaces["network1"], "outpacket_id")

            self.assertEqual(
                logging_debug.call_count, 3)
            self.assertEqual(
                logging_error.call_count, 0)
            self.assertEqual(
                self.result.packed_object(), outpacket.packed_object())

    def test_del_out_packet_conversion_Response_error(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", "Ports", "Ports_ex",
                                  basic_flow_match, "Data")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response("400", "outpacket_item")

            mock_out_packet.return_value = outpacket
            mock_del_object.return_value = self.value

            self.result = self.target._del_out_packet(
                self.target._network_interfaces["network1"], "outpacket_id")

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_del_out_packet_conversion_KeyError(self):
        with nested(patch("logging.debug"),
                    patch("logging.error"),
                    patch("org.o3project.odenos.core.component.network.packet."
                          "out_packet.OutPacket."
                          "create_from_packed"),
                    patch("org.o3project.odenos.core.util."
                          "network_interface.NetworkInterface."
                          "_del_object_to_remote_object")
                    ) as (logging_debug,
                          logging_error,
                          mock_out_packet,
                          mock_del_object):

            basic_flow_match = BasicFlowMatch("BasicFlowMatch", "NodeId",
                                              "PortId")
            outpacket = OutPacket("outpacket_id", "OutPcket", "attributes",
                                  "NodeId", "Ports", "Ports_ex",
                                  basic_flow_match, "Data")
            self.target._network_interfaces["network1"] =\
                NetworkInterface(self.target.dispatcher, "network1")
            self.value = Response(200, "outpacket_item")

            mock_out_packet.side_effect = KeyError()
            mock_del_object.return_value = self.value

            self.result = self.target._del_out_packet(
                self.target._network_interfaces["network1"], "outpacket_id")

            self.assertEqual(
                logging_error.call_count, 1)
            self.assertEqual(
                self.result, None)

    def test_get_ignore_keys_match(self):
        attributes_port = ["oper_status", "physical_id",
                           "vendor", "max_bandwidth",
                           "unreserved_bandwidth", "is_boundary"]

        self.result = self.target._Logic__get_ignore_keys(
            attributes_port, ["attributes::unreserved_bandwidth"])

        self.assertEqual(
            self.result,
            ["oper_status", "physical_id",
             "vendor", "max_bandwidth",
             "is_boundary"])

    def test_get_ignore_keys_not_match(self):
        attributes_port = ["oper_status", "physical_id",
                           "vendor", "max_bandwidth",
                           "unreserved_bandwidth", "is_boundary"]

        self.result = self.target._Logic__get_ignore_keys(
            attributes_port, ["unreserved_bandwidth"])

        self.assertEqual(
            self.result,
            ["oper_status", "physical_id",
             "vendor", "max_bandwidth",
             "is_boundary"])

if __name__ == "__main__":
    unittest.main()
