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

from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.transport.message_dispatcher import (
    MessageDispatcher)
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.core.util.request_parser import RequestParser
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.message.event import Event
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.logic import Logic
from mock import Mock, patch, MagicMock
from contextlib import nested
import unittest


class RemoteObjectTest(unittest.TestCase):

    def setUp(self):
        self.ObkectId = "RemoteObject_python"
        self.Base_URI = "odenos://127.0.0.1/%s" % self.ObkectId
        self.Dispatcher = Mock()
        self.ObkectType = "RemoteObject"
        self.target = RemoteObject(self.ObkectId,
                                   self.Base_URI,
                                   self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._object_property.object_id,
                         self.ObkectId)
        self.assertEqual(self.target._object_property.object_type,
                         self.ObkectType)
        self.assertEqual(self.target._object_property.get_state(),
                         ObjectProperty.State.INITIALIZING)
        self.assertEqual(self.target._object_settings, {})
        self.assertEqual(self.target.dispatcher,
                         self.Dispatcher)

    def test_on_initialize(self):
        obj_prop = ''
        self.assertEqual(self.target.on_initialize(obj_prop),
                         True)

    def test_on_finalize(self):
        self.Dispatcher.remove_local_object = Mock()
        m_remove_local_object = self.Dispatcher.remove_local_object
        self.target.on_finalize()
        self.assertEqual(m_remove_local_object.call_count, 1)

    def test_object_id(self):
        self.assertEqual(self.target.object_id,
                         self.ObkectId)

    def test_object_property(self):
        self.assertNotEqual(self.target.object_property,
                            None)
        self.assertEqual(self.target.object_property.get_state(),
                         ObjectProperty.State.INITIALIZING)

    def test_object_settings(self):

        body = {"id": "slicer1->network1",
                "type": "LogicAndNetwork",
                "connection_type": "original",
                "state": "initializing",
                "logic_id": "slicer1",
                "network_id": "network1"}

        self.target._do_put_settings(body)

        self.assertEqual(self.target.object_settings,
                         body)

    def test_state(self):
        self.assertEqual(self.target.state,
                         ObjectProperty.State.INITIALIZING)

    def test___set_description(self):
        if 'description' in self.target.object_property._object_property:
            del self.target.object_property._object_property['description']

        self.assertNotIn('description',
                         self.target.object_property._object_property)

        self.target._RemoteObject__set_description()

        self.assertIn('description',
                      self.target.object_property._object_property)
        self.assertEqual(self.target.object_property.
                         _object_property["description"],
                         '')

    def test__request_sync(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        object_id = 'NodeId1'
        method = "GET"
        path = "topology/nodes/%s" % 'NodeId1'

        self.Dispatcher.request_sync = Mock()
        m_request_sync = self.Dispatcher.request_sync

        self.result = self.target._request_sync(object_id, method,
                                                path, body)

        self.assertEqual(m_request_sync.call_count, 1)
        self.assertEqual(m_request_sync.call_args[0][0].method,
                         method)
        self.assertEqual(m_request_sync.call_args[0][0].object_id,
                         object_id)
        self.assertEqual(m_request_sync.call_args[0][0].path,
                         path)
        self.assertEqual(m_request_sync.call_args[0][0].body,
                         body)
        self.assertNotEqual(self.result, None)

    def test__request_succes(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        object_id = self.ObkectId
        method = "GET"
        path = "topology/nodes/%s" % 'NodeId1'

        self.Dispatcher.request_sync = Mock()
        m_request_sync = self.Dispatcher.request_sync

        with patch('logging.error') as m_log_error:

            self.result = self.target._request(object_id, method,
                                               path, body)

            self.assertEqual(m_request_sync.call_count, 1)
            self.assertEqual(m_request_sync.call_args[0][0].method,
                             method)
            self.assertEqual(m_request_sync.call_args[0][0].object_id,
                             object_id)
            self.assertEqual(m_request_sync.call_args[0][0].path,
                             path)
            self.assertEqual(m_request_sync.call_args[0][0].body,
                             body)
            self.assertEqual(m_log_error.call_count, 0)
            self.assertNotEqual(self.result,
                                None)

    def test__request_error(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        object_id = self.ObkectId
        method = "GET"
        path = "topology/nodes/%s" % 'NodeId1'
        debug_log = "Exception: Request to " + object_id \
                    + " Method:" + method\
                    + " Path:" + path

        self.Dispatcher.request_sync = Mock()
        m_request_sync = self.Dispatcher.request_sync
        m_request_sync.side_effect = KeyError(1)

        with patch('logging.error') as m_log_error:
            self.result = self.target._request(object_id, method,
                                               path, body)

            self.assertEqual(m_request_sync.call_count, 1)
            self.assertEqual(m_request_sync.call_args[0][0].method,
                             method)
            self.assertEqual(m_request_sync.call_args[0][0].object_id,
                             object_id)
            self.assertEqual(m_request_sync.call_args[0][0].path,
                             path)
            self.assertEqual(m_request_sync.call_args[0][0].body,
                             body)
            self.assertEqual(m_log_error.call_count, 2)
            m_log_error.assert_called_any_with(debug_log)
            self.assertNotEqual(self.result,
                                None)

    def test__publish_event_async(self):

        body = {"action": "update", "Prev": "Prev1", "Curr": "Curr1"}

        event_type = "ObjectSettingChanged"

        with patch('org.o3project.odenos.remoteobject.transport.message_dispatcher.'
                   'MessageDispatcher.publish_event_async'
                   ) as m_publish_event_async:

            self.result = self.target._publish_event_async(event_type,
                                                           body)
            m_publish_event_async.assert_called_one_with(self.ObkectId,
                                                         event_type,
                                                         body)
            self.assertNotEqual(self.result, None)

    def test__apply_event_subscription(self):

        self.Dispatcher.subscribe_event = Mock()
        m_Subscribe_event = self.Dispatcher.subscribe_event

        self.result = self.target._apply_event_subscription()

        self.assertEqual(m_Subscribe_event.call_count, 1)
        self.assertEqual(m_Subscribe_event.call_args[0][0],
                         self.target._event_subscription)
        self.assertNotEqual(self.result, None)

    def test___add_rules(self):
        test_rules = []
        test_rules.append({RequestParser.PATTERN: r"^property/?$",
                           RequestParser.METHOD: Request.Method.GET,
                           RequestParser.FUNC: self.target._do_get_property,
                           RequestParser.PARAMS: 0})
        test_rules.append({RequestParser.PATTERN: r"^property/?$",
                           RequestParser.METHOD: Request.Method.PUT,
                           RequestParser.FUNC: self.target._do_put_property,
                           RequestParser.PARAMS: 1})
        test_rules.append({RequestParser.PATTERN: r"^settings/?$",
                           RequestParser.METHOD: Request.Method.GET,
                           RequestParser.FUNC: self.target._do_get_settings,
                           RequestParser.PARAMS: 0})
        test_rules.append({RequestParser.PATTERN: r"^settings/?$",
                           RequestParser.METHOD: Request.Method.PUT,
                           RequestParser.FUNC: self.target._do_put_settings,
                           RequestParser.PARAMS: 1})

        if hasattr(self.target._RemoteObject__parser, '_RequestParser__rules'):
            del self.target._RemoteObject__parser._RequestParser__rules
            self.target._RemoteObject__parser._RequestParser__rules = []

        self.target._RemoteObject__add_rules()

        self.assertEqual(self.target._RemoteObject__parser.
                         _RequestParser__rules,
                         test_rules)

    def test_dispatch_request_success(self):

        self.request = Request("propertyId",
                               Request.Method.GET,
                               "settings",
                               None)

        self.result = self.target.dispatch_request(self.request)

        self.assertNotEqual(self.result, None)
        self.assertEqual(self.result.status_code,
                         Response.StatusCode.OK)

    def test_dispatch_request_StatusCode_NOT_FOUND(self):

        self.request = Request("propertyId",
                               Request.Method.GET,
                               "Settings",
                               None)

        self.result = self.target.dispatch_request(self.request)

        self.assertEqual(self.result.status_code,
                         Response.StatusCode.BAD_REQUEST)

    def test_dispatch_request_except(self):

        self.request = Request("propertyId",
                               Request.Method.GET,
                               "Settings",
                               None)
        debug_log = "Exception: Receive Request" \
                    + " Method:" + Request.Method.GET\
                    + " Path:" + "Settings"
        with nested(
                patch('org.o3project.odenos.core.util.request_parser.'
                      'RequestParser.action',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_action, m_log_error):

                self.result = self.target.dispatch_request(self.request)

                self.assertEqual(m_action.call_count, 1)
                m_action.assert_called_once_with(self.request)
                self.assertEqual(m_log_error.call_count, 2)
                m_log_error.assert_called_any_with(debug_log)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.status_code,
                                 Response.StatusCode.INTERNAL_SERVER_ERROR)

    def test_dispatch_event(self):
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
        Event_Type = "ComponentConnectionChanged"
        Publisher_id = 'Id1'
        self.EvntResult = Event(Publisher_id, Event_Type, self.value)

        with nested(
                patch('org.o3project.odenos.remoteobject.remote_object.'
                      'RemoteObject._do_post_event',
                      )) as m_do_post_event:

                self.target.dispatch_event(self.EvntResult)
                self.assertEqual(m_do_post_event[0].call_count, 1)
                m_do_post_event[0].assert_called_any_with(self.EvntResult)

    def test__do_get_property(self):
        self.result = self.target._do_get_property()

        self.assertEqual(self.result.status_code,
                         Response.StatusCode.OK)
        self.assertEqual(self.result.body,
                         self.target._object_property.packed_object())

    def test__do_put_property_Success1(self):
        body = {"id": "RemoteObject_python",
                "super_type": "Network",
                "type": "RemoteObject",
                "base_uri": "http://examp le.com:12345/objects/network1",
                "state": "running",
                "description": "",
                "connection_types": ""
                }

        self.assertNotEqual(self.target.object_property.packed_object(), body)
        self.result = self.target._do_put_property(body)
        self.assertEqual(self.result.status_code, Response.StatusCode.OK)
        self.assertEqual(self.result.body, body)
        self.assertEqual(self.target.object_property.packed_object(), body)

    def test__do_put_property_Success2(self):
        body = {"id": "RemoteObject_python",
                "super_type": "Network",
                "type": "RemoteObject",
                "base_uri": "http://examp le.com:12345/objects/network1",
                "state": "running",
                "description": ""
                }

        with nested(
                patch('org.o3project.odenos.remoteobject.remote_object.'
                      'RemoteObject.on_state_changed'),
                patch('org.o3project.odenos.remoteobject.object_property.'
                      'ObjectProperty.put_property'
                      )) as (m_on_state_changed,
                             m_put_property):

                self.result = self.target._do_put_property(body)
                self.assertEqual(m_on_state_changed.call_count, 1)
                m_on_state_changed.assert_called_one_with("initializing",
                                                          "running")
                self.assertEqual(m_put_property.call_count, 1)
                m_on_state_changed.assert_called_one_with(body)

    def test__do_put_property_2call(self):
        body = {"id": "RemoteObject_python",
                "super_type": "Network",
                "type": "RemoteObject",
                "base_uri": "http://examp le.com:12345/objects/network1",
                "state": "running",
                "description": "",
                "connection_types": ""
                }

        self.target._object_property.put_property(body)
        self.assertEqual(self.target.object_property.packed_object(),
                         body)

        with patch('org.o3project.odenos.remoteobject.object_property.'
                   'ObjectProperty.put_property'
                   ) as m_put_property:

                self.result = self.target._do_put_property(body)

                self.assertEqual(m_put_property.call_count, 0)
                self.assertEqual(self.result.status_code,
                                 Response.StatusCode.OK)
                self.assertEqual(self.result.body, body)
                self.assertEqual(self.target.object_property.packed_object(),
                                 body)

    def test_do_put_settings_Success(self):
        body = {"id": "slicer1->network1",
                "type": "LogicAndNetwork",
                "connection_type": "original",
                "state": "initializing",
                "logic_id": "slicer1",
                "network_id": "network1"}

        body2 = {"id": "slicer1->network1",
                 "type": "LogicAndNetwork",
                 "connection_type": "original",
                 "state": "running",
                 "logic_id": "slicer1",
                 "network_id": "network1"}

        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_settings_changed',
                   ) as m_on_settings_changed:

            self.result = self.target._do_put_settings(body)
            self.assertEqual(self.target.object_settings,
                             body)
            self.assertEqual(m_on_settings_changed.call_count, 1)
            m_on_settings_changed.assert_called_any_with("update",
                                                         {},
                                                         body)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body)

            self.result = self.target._do_put_settings(body2)
            self.assertEqual(self.target.object_settings,
                             body2)
            self.assertEqual(m_on_settings_changed.call_count, 2)
            m_on_settings_changed.assert_called_any_with("update",
                                                         body,
                                                         body2)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body2)

    def test_do_put_settings_old_new_Identity(self):
        body = {"id": "slicer1->network1",
                "type": "LogicAndNetwork",
                "connection_type": "original",
                "state": "initializing",
                "logic_id": "slicer1",
                "network_id": "network1"}

        body2 = {"id": "slicer1->network1",
                 "type": "LogicAndNetwork",
                 "connection_type": "original",
                 "state": "initializing",
                 "logic_id": "slicer1",
                 "network_id": "network1"}

        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_settings_changed',
                   ) as m_on_settings_changed:

            self.result = self.target._do_put_settings(body)
            self.assertEqual(self.target.object_settings,
                             body)
            self.assertEqual(m_on_settings_changed.call_count, 1)
            m_on_settings_changed.assert_called_any_with("update",
                                                         {},
                                                         body)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body)

            self.result = self.target._do_put_settings(body2)
            self.assertEqual(self.target.object_settings,
                             body2)
            self.assertEqual(m_on_settings_changed.call_count, 1)

            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body)

    def test_do_put_settings_element_del(self):
        body = {"id": "slicer1->network1",
                "type": "LogicAndNetwork",
                "connection_type": "original",
                "state": "initializing",
                "logic_id": "slicer1",
                "network_id": "network1"}

        body2 = {"id": "slicer1->network1",
                 "type": "LogicAndNetwork",
                 "connection_type": "original",
                 "state": "running",
                 "network_id": "network1"}

        body3 = {"id": "slicer1->network1",
                 "type": "LogicAndNetwork",
                 "connection_type": "original",
                 "state": "finalizing",
                 "logic_id": "slicer1",
                 "network_id": "network1"}

        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_settings_changed',
                   ) as m_on_settings_changed:

            self.result = self.target._do_put_settings(body)
            self.assertEqual(self.target.object_settings,
                             body)
            self.assertEqual(m_on_settings_changed.call_count, 1)
            m_on_settings_changed.assert_called_any_with("update",
                                                         {},
                                                         body)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body)

            self.result = self.target._do_put_settings(body2)
            self.assertEqual(self.target.object_settings,
                             body2)
            self.assertEqual(m_on_settings_changed.call_count, 2)
            m_on_settings_changed.assert_called_any_with("update",
                                                         body,
                                                         body2)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body2)

            self.result = self.target._do_put_settings(body3)
            self.assertEqual(self.target.object_settings,
                             body3)
            self.assertEqual(m_on_settings_changed.call_count, 3)
            m_on_settings_changed.assert_called_any_with("update",
                                                         body2,
                                                         body3)
            self.assertEqual(self.result.status_code,
                             Response.StatusCode.OK)
            self.assertEqual(self.result.body,
                             body3)

    def test__do_post_event(self):
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
        Event_Type = "componentconnectionchanged"
        Publisher_id = 'Id1'
        self.EvntResult = Event(Publisher_id, Event_Type, self.value)

        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_event',
                   ) as m_on_event:

                self.target._do_post_event(self.EvntResult)

                self.assertEqual(m_on_event.call_count, 1)

    def test_on_state_changed_OlsState_NotFINALIZING_NewState_FINALIZING(self):
        OldState = ObjectProperty.State.INITIALIZING
        NewState = ObjectProperty.State.FINALIZING
        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_finalize',
                   ) as m_on_finalize:

            self.target.on_state_changed(OldState, NewState)
            self.assertEqual(m_on_finalize.call_count, 1)

    def test_on_state_changed_OlsState_FINALIZING(self):
        OldState = ObjectProperty.State.FINALIZING
        NewState = ObjectProperty.State.ERROR
        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_finalize',
                   ) as m_on_finalize:

            self.target.on_state_changed(OldState, NewState)
            self.assertEqual(m_on_finalize.call_count, 0)

    def test_on_state_changed_OLdState_NoFINALIZING_NewState_NoFINALIZING(self):
        OldState = ObjectProperty.State.INITIALIZING
        NewState = ObjectProperty.State.ERROR
        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject.on_finalize',
                   ) as m_on_finalize:

            self.target.on_state_changed(OldState, NewState)
            self.assertEqual(m_on_finalize.call_count, 0)

    def test_on_settings_changed(self):

        body = {"action": "update", "Prev": "Prev1", "Curr": "Curr1"}

        event_type = "ObjectSettingChanged"

        with patch('org.o3project.odenos.remoteobject.remote_object.'
                   'RemoteObject._publish_event_async',
                   ) as m_publish_event_async:

            self.result = self.target.on_settings_changed("update",
                                                          "Prev1",
                                                          "Curr1")

            m_publish_event_async.assert_called_one_with(event_type,
                                                         body)

    def test__on_request(self):
        self.result = self.target._on_request("Request")
        self.assertEqual(self.result.status_code,
                         Response.StatusCode.BAD_REQUEST)

    def test_on_event_Success(self):
        Dispatcher2 = MagicMock()

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
        Event_Type = "ComponentConnectionChanged"
        Publisher_id = 'Id1'
        self.EvntResult = Event(Publisher_id, Event_Type, self.value)

        self.MessageDispatcher = Mock()
        self.LogicResult = Logic("cc_action", Dispatcher2)

        with patch('org.o3project.odenos.core.component.logic.Logic.'
                   '_do_event_componentconnectionchanged'
                   ) as m_do_event:

            self.LogicResult.on_event(self.EvntResult)
            self.assertEqual(m_do_event.call_count, 1)

    def test_on_event_callable_NG(self):
        Dispatcher2 = MagicMock()

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
        Event_Type = "ComponentConnectionChanged2"
        Publisher_id = 'Id1'
        self.EvntResult = Event(Publisher_id, Event_Type, self.value)

        self.MessageDispatcher = Mock()
        self.LogicResult = Logic("cc_action", Dispatcher2)

        with patch('org.o3project.odenos.core.component.logic.Logic.'
                   '_do_event_componentconnectionchanged'
                   ) as m_do_event:

            self.LogicResult.on_event(self.EvntResult)
            self.assertEqual(m_do_event.call_count, 0)

    def test_on_event_hasattr_NG(self):
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
        Event_Type = "componentconnectionchanged"
        Publisher_id = 'Id1'
        self.EvntResult = Event(Publisher_id, Event_Type, self.value)

        with patch('org.o3project.odenos.core.component.logic.Logic.'
                   '_do_event_componentconnectionchanged'
                   ) as m_do_event:

            self.target.on_event(self.EvntResult)
            self.assertEqual(m_do_event.call_count, 0)


if __name__ == '__main__':
    unittest.main()
