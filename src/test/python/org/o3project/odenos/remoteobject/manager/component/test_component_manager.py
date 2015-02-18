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

from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.remoteobject.manager.component.component_manager\
    import ComponentManager
from org.o3project.odenos.core.component.dummy_driver import DummyDriver
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.util.request_parser import RequestParser

import unittest
import logging
from mock import Mock, MagicMock, patch
from contextlib import nested
from apt_pkg import Description


class ComponentManagerTest(unittest.TestCase, ComponentManager):
    MessageDispatcher = MagicMock()
    value = None
    result = None

    def setUp(self):
        self.target = ComponentManager(
            "cc_action",
            self.MessageDispatcher)

    def tearDown(self):
        self.target = None
        self.dispatcher = None

    def test_constructor(self):
        rules = self.target._ComponentManager__parser._RequestParser__rules
        self.assertEqual(
            self.target._object_property._object_property["type"],
            "ComponentManager")
        self.assertEqual(
            self.target._object_property._object_property["id"],
            "cc_action")
        self.assertEqual(
            self.target._object_property._object_property["state"],
            "running")
        self.assertEqual(self.target.component_classes, {})
        self.assertEqual(self.target.components, {})
        self.assertEqual(len(rules), 5)

    def test_register_to_system_manager_success(self):
        self.target._request = MagicMock()
        self.target._ComponentManager__register_component_managers =\
            MagicMock()
        register_component_managers =\
            self.target._ComponentManager__register_component_managers
        self.target._ComponentManager__register_event_manager =\
            MagicMock()
        register_event_manager =\
            self.target._ComponentManager__register_event_manager
        self.target._ComponentManager__subscribe_event =\
            MagicMock()
        subscribe_event =\
            self.target._ComponentManager__subscribe_event
        self.value = {"id": "123456",
                      "super_type": "Network",
                      "type": "Network",
                      "base_uri": "http://example.com:123456",
                      "state": "running",
                      "description": "NetworkComponent"}
        self.target._request.return_value = Response(
            Response.StatusCode.OK, self.value)

        with patch('logging.debug') as m_logging_debug:
            self.target.register_to_system_manager()
            self.assertEqual(m_logging_debug.call_count, 1)
            self.assertEqual(
                register_component_managers.call_count, 1)
            self.assertEqual(
                register_event_manager.call_count, 1)
            self.assertEqual(
                subscribe_event.call_count, 1)

            self.target._ComponentManager__register_component_managers.close()
            self.target._ComponentManager__register_event_manager.close()
            self.target._ComponentManager__subscribe_event.close()

    def test_register_to_system_manager_Error(self):
        self.target._request = MagicMock()
        self.target._ComponentManager__register_component_managers =\
            MagicMock()
        register_component_managers =\
            self.target._ComponentManager__register_component_managers
        self.target._ComponentManager__register_event_manager =\
            MagicMock()
        register_event_manager =\
            self.target._ComponentManager__register_event_manager
        self.target._ComponentManager__subscribe_event =\
            MagicMock()
        subscribe_event =\
            self.target._ComponentManager__subscribe_event

        self.value = {"id": "123456",
                      "super_type": "Network",
                      "type": "Network",
                      "base_uri": "http://example.com:123456",
                      "state": "running",
                      "description": "NetworkComponent"}
        self.target._request.return_value = Response(
            Response.StatusCode.BAD_REQUEST, self.value)

        with nested(
                patch('logging.debug'),
                patch('logging.error')) as (m_logging_debug, m_logging_error):

            self.target.register_to_system_manager()
            self.assertEqual(m_logging_debug.call_count, 1)
            self.assertEqual(m_logging_error.call_count, 1)
            self.assertEqual(
                register_component_managers.call_count, 1)
            self.assertEqual(
                register_event_manager.call_count, 0)
            self.assertEqual(
                subscribe_event.call_count, 0)

            self.target._ComponentManager__register_component_managers.close()
            self.target._ComponentManager__register_event_manager.close()
            self.target._ComponentManager__subscribe_event.close()

    def test_register_component_managers_True(self):
        self.target._request = MagicMock()
        self.target.dispatcher.add_remote_client = MagicMock()

        self.value = [{"id": "Curr",
                       "super_type": "Network",
                       "type": "Network",
                       "base_uri": "http://example.com:123456",
                       "state": "running",
                       "description": "NetworkComponent"}]
        self.target._request.return_value = Response(
            Response.StatusCode.OK, self.value)
        self.target._ComponentManager__register_component_managers()
        self.assertEqual(
            self.target.dispatcher.add_remote_client.call_count, 1)

    def test_register_component_managers_error(self):
        with patch('logging.error') as m_logging_error:

            self.target._ComponentManager__register_component_managers()
            self.assertEqual(
                self.target._object_property._object_property["state"],
                "error")
            self.assertEqual(m_logging_error.call_count, 1)

    def test_register_other_component_manager_NotEqual_object_id(self):
        self.target.dispatcher.add_remote_client = Mock()
        component_manager = {"id": "123", "base_uri": "456"}
        self.target._ComponentManager__register_other_component_manager(
            component_manager)
        self.assertEqual(
            self.target.dispatcher.add_remote_client.call_count, 1)

    def test_register_other_component_manager_Equal_object_id(self):
        self.target.dispatcher.add_remote_client = Mock()
        component_manager = {"id": "cc_action", "base_uri": "456"}
        self.target._ComponentManager__register_other_component_manager(
            component_manager)
        self.assertEqual(
            self.target.dispatcher.add_remote_client.call_count, 0)

    def test_unregister_component_manager(self):
        self.target.dispatcher.remove_remote_client = Mock()
        self.target._ComponentManager__unregister_component_manager(
            "cc_action")
        self.assertEqual(
            self.target.dispatcher.remove_remote_client.call_count, 1)

    def test_register_event_manager_error(self):
        self.target._ComponentManager__register_event_manager()
        self.assertEqual(
            self.target._object_property._object_property["state"],
            "error")

    def test_register_event_manager_not_error(self):
        self.target._request = MagicMock()
        self.target.dispatcher.add_remote_client = MagicMock()

        self.value = {"id": "123456",
                      "super_type": "Network",
                      "type": "Network",
                      "base_uri": "http://example.com:123456",
                      "state": "running",
                      "description": "NetworkComponent"}
        self.target._request.return_value = Response(
            Response.StatusCode.OK, self.value)
        self.target._ComponentManager__register_event_manager()
        self.assertEqual(
            self.target.dispatcher.add_remote_client.call_count, 1)

        self.target._request = None

    def test_subscribe_event(self):
        self.target.dispatcher.subscribe_event = Mock()
        self.target._ComponentManager__subscribe_event()
        self.assertEqual(
            self.target.dispatcher.subscribe_event.call_count, 1)

    def test_register_component_type_component_types_true(self):
        self.target._object_property._object_property["component_types"] =\
            "component_name"
        self.value = Packet("packet_id", "InPacket", {"type": "BasicFlowMatch",
                                                      "in_port": "123456",
                                                      "in_node": "123456789"})
        self.target.register_component_type(self.value.__class__)
        self.assertEqual(
            self.target.component_classes[self.value.__class__.__name__],
            self.value.__class__)
        self.assertEqual(
            self.target._object_property._object_property["component_types"],
            "component_name,Packet")

    def test_register_component_type_component_types_false(self):
        self.value = Packet("packet_id", "InPacket", {"type": "BasicFlowMatch",
                                                      "in_port": "123456",
                                                      "in_node": "123456789"})
        self.target.register_component_type(self.value.__class__)
        self.assertEqual(
            self.target._object_property._object_property["component_types"],
            "Packet")

    def test_add_rules(self):
        self.target._ComponentManager__parser._RequestParser__rules = []
        self.target._ComponentManager__add_rules()
        rules = self.target._ComponentManager__parser._RequestParser__rules
        self.assertEqual(len(rules), 5)
        self.assertEqual(
            rules[0], {RequestParser.PATTERN: "^component_types/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_component_types,
                       RequestParser.PARAMS: 0})

        self.assertEqual(
            rules[1], {RequestParser.PATTERN: "^components/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_components,
                       RequestParser.PARAMS: 0})

        self.assertEqual(
            rules[2], {RequestParser.PATTERN: "^components/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.PUT,
                       RequestParser.FUNC: self.target._do_put_component,
                       RequestParser.PARAMS: 2})

        self.assertEqual(
            rules[3], {RequestParser.PATTERN: "^components/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_component,
                       RequestParser.PARAMS: 1})

        self.assertEqual(
            rules[4], {RequestParser.PATTERN: "^components/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.DELETE,
                       RequestParser.FUNC: self.target._do_delete_component,
                       RequestParser.PARAMS: 1})

    def test_on_request(self):
        request = Request("ObjectId",
                          Request.Method.GET,
                          "component_managers",
                          None)
        self.result = self.target._on_request(request)
        self.assertEqual(self.result.status_code, 404)
        self.assertEqual(self.result.body, None)

    def test_do_get_component_object_id_in_components(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.components = {"object_id": self.value}
        self.result = self.target._do_get_component("object_id")
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(self.result.body["type"], "ComponentManager")
        self.assertEqual(self.result.body["id"], "456")

    def test_do_get_component_object_id_not_in_components(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.components = {"False_id": self.value}
        self.result = self.target._do_get_component("object_id")
        self.assertEqual(self.result.status_code, 404)
        self.assertEqual(self.result.body, None)

    def test_do_put_component_success(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")
        self.assertEqual(self.result.status_code, 201)
        self.assertEqual(self.result.body["type"], "ComponentManager")
        self.assertEqual(self.result.body["id"], "456")
        self.assertEqual(
            self.result.body["state"], "running")

    def test_do_put_component_error(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.target.components = MagicMock()
        self.target.components[" "].on_initialize.return_value = False
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")
        self.assertEqual(self.result.status_code, 201)
        self.target.components["456"].\
            _object_property.set_state.assert_called_once_with("error")

    def test_do_put_component_component_type_not_in_component_classes(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "123",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")
        self.assertEqual(self.result.status_code, 400)
        self.assertEqual(self.result.body, None)

    def test_do_put_component_object_id_in_components(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.target.components["456"] = "False"
        self.result = self.target._do_put_component(self.value, "456")
        self.assertEqual(self.result.status_code, 409)
        self.assertEqual(self.result.body, None)

    def test_do_delete_component(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")

        self.value = ComponentManager(
            "123",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "123",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "123")

        self.result = self.target._do_delete_component("456")
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(self.result.body, None)
        self.assertEqual(len(self.target.components), 1)

    def test_do_get_components(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")

        self.value = ComponentManager(
            "123",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "123",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "123")

        self.result = self.target._do_get_components()
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(
            self.result.body["456"]["type"], "ComponentManager")
        self.assertEqual(
            self.result.body["456"]["id"], "456")
        self.assertEqual(
            self.result.body["456"]["state"], "running")

        self.assertEqual(
            self.result.body["123"]["type"], "ComponentManager")
        self.assertEqual(
            self.result.body["123"]["id"], "123")
        self.assertEqual(
            self.result.body["123"]["state"], "running")

    def test_do_get_component_types(self):
        self.target.component_classes = {"SampleDummyDriver": SampleDummyDriver}
        self.result = self.target._do_get_component_types()
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(
            self.result.body["SampleDummyDriver"]["type"], "SampleDummyDriver")
        self.assertEqual(
            self.result.body["SampleDummyDriver"]["super_type"], "Driver")
        self.assertEqual(
            len(self.result.body["SampleDummyDriver"]["connection_types"]), 2)

    def test_do_get_component_types_error(self):
        self.target.component_classes = {"DummyDriver": self}
        self.result = self.target._do_get_component_types()
        self.assertEqual(self.result.status_code, 500)

    def test_do_component_changed(self):
        self.target.dispatcher.publish_event_async = Mock()
        self.target._do_component_changed("add", "prev", "curr")
        self.assertEqual(
            self.target.dispatcher.publish_event_async.call_count, 1)

    def test_do_event_componentmanagerchanged_add_action(self):
        self.value = {"action": "add",
                      "prev": None,
                      "curr": {"id": "Curr",
                               "super_type": "Network",
                               "type": "Network",
                               "base_uri": "http://example.com:12345",
                               "state": "running",
                               "description": "NetworkComponent"}}
        self.result = Response("add", self.value)
        self.target.dispatcher.add_remote_client = Mock()
        self.target._do_event_componentmanagerchanged(self.result)
        self.assertEqual(
            self.target.dispatcher.add_remote_client.call_count, 1)

    def test_do_event_componentmanagerchanged_delete_action(self):
        self.value = ComponentManager(
            "456",
            self.MessageDispatcher)
        self.target.register_component_type(self.value.__class__)
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_component(self.value, "456")
        prev = self.target.components["456"].object_property.packed_object()

        self.target.dispatcher.remove_remote_client = Mock()
        self.value = {"action": "delete",
                      "prev": prev,
                      "curr": None}
        self.result = Response("add", self.value)
        self.target._do_event_componentmanagerchanged(self.result)
        self.assertEqual(
            self.target.dispatcher.remove_remote_client.call_count, 1)

    def test_do_event_componentmanagerchanged_keyerror(self):
        self.value = {"type": "ComponentManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = Response("add", self.value)

        with patch('logging.error') as m_logging_error:
            self.target._do_event_componentmanagerchanged(self.result)
            self.assertEqual(m_logging_error.call_count, 1)


if __name__ == '__main__':
    unittest.main()
    
class SampleDummyDriver(DummyDriver):
    CONNECTION_TYPES = "original:1,aggregated:1"
