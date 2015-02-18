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

from org.o3project.odenos.core.util.system_manager_interface import (
    SystemManagerInterface
    )
from org.o3project.odenos.core.manager.component_manager import ComponentManager
from org.o3project.odenos.remoteobject.manager.system.component_connection\
    import ComponentConnection
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network\
    import ComponentConnectionLogicAndNetwork
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from mock import Mock, patch
from contextlib import nested
import unittest


class SystemManagerInterfaceTest(unittest.TestCase,):
    Dispatcher = Mock()
    Dispatcher.system_manager_id = "SystemManagerId"

    def setUp(self):
        self.target = SystemManagerInterface(self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_system_manager_id(self):
        self.assertEqual(self.target.system_manager_id,
                         "SystemManagerId")

    def test_get_component_managers_success(self):
        value = "ComponentManagersBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:

            self.result = self.target.get_component_managers()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNGS_PATH)
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_managers_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:
            self.result = self.target.get_component_managers()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNGS_PATH)
            self.assertEqual(self.result, None)

    def test_get_event_manager_success(self):
        value = "EventManager_Body"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:
            self.result = self.target.get_event_manager()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         EVENT_MNG_PATH)
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_event_manager_Error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:

            self.result = self.target.get_event_manager()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         EVENT_MNG_PATH)
            self.assertEqual(self.result, None)

    def test_get_component_type_success(self):
        value = "ComponentTypeBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         value)
                   ) as m_get_object:

            self.result = self.target.get_component_type("ComponentManager")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPE_PATH %
                                         "ComponentManager")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_type_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:
            self.result = self.target.get_component_type("ComponentManager")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPE_PATH %
                                         "ComponentManager")
            self.assertEqual(self.result, None)

    def test_get_component_types_success(self):
        value = "ComponentTypesBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:

            self.result = self.target.get_component_types()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPES_PATH)
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_types_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND, None)
                   ) as m_get_object:

            self.result = self.target.get_component_types()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPES_PATH)
            self.assertEqual(self.result, None)

    def test_get_component_success(self):
        value = "ComponentBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:

            self.result = self.target.get_component("CompId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_PATH % "CompId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_components_success(self):
        value = "ComponentsBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:
            self.result = self.target.get_components()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMPS_PATH)
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_components_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:
            self.result = self.target.get_components()
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMPS_PATH)
            self.assertEqual(self.result, None)

    def test_get_component_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:
            self.result = self.target.get_component("CompId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_PATH % "CompId1")
            self.assertEqual(self.result, None)

    def test_get_connections_success(self):
        ComponentConnectioTmp = ComponentConnection("slicer1_network1",
                                                    "original",
                                                    "running")
        LogicAndNetworkTmp =\
            ComponentConnectionLogicAndNetwork("slicer1_network2",
                                               "original",
                                               "running",
                                               "LogicId",
                                               "NetworkId")
        ComponentConnectioTmp_packed_object =\
            ComponentConnectioTmp.packed_object()
        LogicAndNetworkTmp_packed_object =\
            LogicAndNetworkTmp.packed_object()
        body = {"slicer1network": ComponentConnectioTmp_packed_object,
                "slicer2network": LogicAndNetworkTmp_packed_object}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed',
                      return_value=ComponentConnection.create_from_packed(
                          ComponentConnectioTmp_packed_object)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed',
                      return_value=ComponentConnectionLogicAndNetwork.
                      create_from_packed(LogicAndNetworkTmp_packed_object)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):

                self.result = self.target.get_connections()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTIONS_PATH)
                self.assertEqual(m_Connection.call_count, 1)
                m_Connection.assert_any_call(
                    ComponentConnectioTmp_packed_object)
                self.assertEqual(m_LogicAndNetwork.call_count, 1)
                m_LogicAndNetwork.assert_any_call(
                    LogicAndNetworkTmp_packed_object)
                self.assertNotEqual(self.result, None)
                self.assertEqual(len(self.result), 2)
                self.assertEqual(self.result["slicer1network"].packed_object(),
                                 ComponentConnectioTmp_packed_object)
                self.assertEqual(self.result["slicer2network"].packed_object(),
                                 LogicAndNetworkTmp_packed_object)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_connections_is_error_NotGet(self):
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            None)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed'),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed'),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):

                self.result = self.target.get_connections()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_Connection.call_count, 0)
                self.assertEqual(m_LogicAndNetwork.call_count, 0)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTIONS_PATH)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_connections_create_from_packed_error(self):
        ComponentConnectioTmp = ComponentConnection("slicer1_network1",
                                                    "original",
                                                    "running")
        LogicAndNetworkTmp =\
            ComponentConnectionLogicAndNetwork("slicer1_network2",
                                               "original",
                                               "running",
                                               "LogicId",
                                               "NetworkId")
        ComponentConnectioTmp_packed_object =\
            ComponentConnectioTmp.packed_object()
        LogicAndNetworkTmp_packed_object =\
            LogicAndNetworkTmp.packed_object()
        body = {"slicer1network": ComponentConnectioTmp_packed_object,
                "slicer2network": LogicAndNetworkTmp_packed_object}
        debug_log = "GET Connections Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed',
                      side_effect=KeyError(1)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed',
                      return_value=ComponentConnectionLogicAndNetwork.
                      create_from_packed(LogicAndNetworkTmp_packed_object)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):
                self.result = self.target.get_connections()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTIONS_PATH)
                self.assertEqual(m_Connection.call_count, 1)
                m_Connection.assert_any_call(
                    ComponentConnectioTmp_packed_object)
                self.assertEqual(m_LogicAndNetwork.call_count, 1)
                m_LogicAndNetwork.assert_any_call(
                    LogicAndNetworkTmp_packed_object)
                self.assertEqual(self.result, None)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(m_log_error.call_count, 1)

    def test_get_component_manager_arg_comp_mgr_id_success(self):
        value = "ComponentManagerBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_component_manager("CompMngId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNG_PATH % "CompMngId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_manager_arg_comp_mgr_id_error(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         None)
                   ) as m_get_object:
                self.result = self.target.get_component_manager("CompMngId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             COMP_MNG_PATH % "CompMngId1")
                self.assertEqual(self.result, None)

    def test_get_component_type_arg_comp_type_success(self):
        value = "ComponentTypeBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_component_type("CompTypeId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPE_PATH % "CompTypeId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_type_arg_comp_type_error(self):
        value = "ComponentTypeBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_component_type("CompTypeId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_TYPE_PATH % "CompTypeId1")
            self.assertEqual(self.result, None)

    def test_get_component_arg_comp_id_success(self):
        value = "ComponentBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_component("CompId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_PATH % "CompId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_component_arg_comp_id_error(self):
        value = "ComponentBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_component("CompId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         COMP_PATH % "CompId1")
            self.assertEqual(self.result, None)

    def test_get_connection_arg_LogicAndNetwork_success(self):
        LogicAndNetworkTmp =\
            ComponentConnectionLogicAndNetwork("slicer1network",
                                               "original",
                                               "running",
                                               "LogicId",
                                               "NetworkId")
        body = LogicAndNetworkTmp.packed_object()
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed'),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed',
                      return_value=ComponentConnectionLogicAndNetwork.
                      create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):

                self.result = self.target.get_connection("slicer1network")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTION_PATH %
                                             "slicer1network")
                self.assertEqual(m_Connection.call_count, 0)
                self.assertEqual(m_LogicAndNetwork.call_count, 1)
                m_LogicAndNetwork.assert_any_call(body)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_connection_arg_ComponentConnectio_success(self):
        ComponentConnectioTmp = ComponentConnection("slicer1network",
                                                    "original",
                                                    "running")
        body = ComponentConnectioTmp.packed_object()
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK,
                                            body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed',
                      return_value=ComponentConnection.create_from_packed(
                          body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed'),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):

                self.result = self.target.get_connection("slicer1network")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTION_PATH %
                                             "slicer1network")
                self.assertEqual(m_Connection.call_count, 1)
                m_Connection.assert_any_call(body)
                self.assertEqual(m_LogicAndNetwork.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_connection_arg_is_error_NG(self):
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            None)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed'),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed'),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):
                self.result = self.target.get_connection("slicer1network")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTION_PATH %
                                             "slicer1network")
                self.assertEqual(m_Connection.call_count, 0)
                self.assertEqual(m_LogicAndNetwork.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_connection_arg_create_from_packed_NG(self):
        ComponentConnectioTmp = ComponentConnection("slicer1network",
                                                    "original",
                                                    "running")
        body = ComponentConnectioTmp.packed_object()
        debug_log = "GET Connection Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK,
                                            body)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection.ComponentConnection.'
                      'create_from_packed',
                      side_effect=KeyError(1)),
                patch('org.o3project.odenos.remoteobject.manager.system.'
                      'component_connection_logic_and_network.'
                      'ComponentConnectionLogicAndNetwork.create_from_packed'),
                patch('logging.error'
                      )) as (m_get_object,
                             m_Connection,
                             m_LogicAndNetwork,
                             m_log_error):

                self.result = self.target.get_connection("slicer1network")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_any_call(SystemManagerInterface.
                                             CONNECTION_PATH %
                                             "slicer1network")
                self.assertEqual(m_Connection.call_count, 1)
                m_Connection.assert_any_call(body)
                self.assertEqual(m_LogicAndNetwork.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_object_arg_object_id_success(self):
        value = "ObjectBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         value)
                   ) as m_get_object:
            self.result = self.target.get_object("ObjectId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         OBJECT_PATH % "ObjectId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(self.result, value)

    def test_get_object_arg_object_id_error(self):
        value = "ObjectBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         value)
                   ) as m_get_object:

            self.result = self.target.get_object("ObjectId1")
            self.assertEqual(m_get_object.call_count, 1)
            m_get_object.assert_any_call(SystemManagerInterface.
                                         OBJECT_PATH % "ObjectId1")
            self.assertEqual(self.result, None)

    def test_put_connection_success(self):
        value = ComponentConnection("Component_ConnectionId1", "ObjectType",
                                    "original", "running")
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._put_object_to_remote_object'
                      )) as m_put_object:
                self.result = self.target.put_connection(value)
                self.assertEqual(m_put_object[0].call_count, 1)
                m_put_object[0].assert_any_call(SystemManagerInterface.
                                                CONNECTION_PATH %
                                                "Component_ConnectionId1",
                                                value)
                self.assertNotEqual(self.result, None)

    def test_put_component_managers_success(self):
        value = ObjectProperty("DummyType", "CompMngId1")
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object'
                   ) as m_put_object:
            self.result = self.target.put_component_managers(value)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNG_PATH % value.object_id,
                                          value)
            self.assertNotEqual(self.result, None)

    def test_add_component_managers_failed(self):
        value = ComponentManager("cmp_id", self.Dispatcher)

        with nested(patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object'
                   ), patch("logging.error")) as (m_put_object, logging_error):
            self.result = self.target.add_component_manager(value)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNG_PATH % value.object_id,
                                          value.object_property)
            self.assertEqual(logging_error.call_count, 1)
            #self.assertNotEqual(self.result, None)

    def test_add_component_managers_success(self):
        value = ComponentManager("cmp_id", self.Dispatcher)

        with nested(patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object', \
                   return_value=Response(Response.StatusCode.OK, value.object_property)
                   ), patch("logging.error")) as (m_put_object, logging_error):
            self.result = self.target.add_component_manager(value)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNG_PATH % value.object_id,
                                          value.object_property)
            #self.assertNotEqual(self.result, None)

    def test_post_component_success(self):
        value = "PostComponentBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object'
                   ) as m_post_object:
                self.result = self.target.post_components(value)
                self.assertEqual(m_post_object.call_count, 1)
                m_post_object.assert_any_call(SystemManagerInterface.
                                              COMPS_PATH,
                                              value)
                self.assertNotEqual(self.result, None)

    def test_put_component_success(self):
        value = ObjectProperty("DummyType", "CompId1")
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object'
                   ) as m_put_object:
                self.result = self.target.put_components(value)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_any_call(SystemManagerInterface.
                                              COMP_PATH % value.object_id,
                                              value)
                self.assertNotEqual(self.result, None)

    def test_post_connections_success(self):
        value = "PostConnectionsBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object'
                   ) as m_post_object:
            self.result = self.target.post_connections(value)
            self.assertEqual(m_post_object.call_count, 1)
            m_post_object.assert_any_call(SystemManagerInterface.
                                          CONNECTIONS_PATH,
                                          value)
            self.assertNotEqual(self.result, None)

    def test_del_component_managers_success(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object'
                   ) as m_del_object:
            self.result = self.target.del_component_managers("CompMgrId1")
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_any_call(SystemManagerInterface.
                                         COMP_MNG_PATH % "CompMgrId1")
            self.assertNotEqual(self.result, None)

    def test_del_components_success(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object'
                   ) as m_del_object:
            self.result = self.target.del_components("CompId1")
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_any_call(SystemManagerInterface.
                                         COMP_PATH % "CompId1")
            self.assertNotEqual(self.result, None)

    def test_del_connections_success(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object'
                   ) as m_del_object:
            self.result = self.target.del_connections("ConnId1")
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_any_call(SystemManagerInterface.
                                         CONNECTION_PATH % "ConnId1")
            self.assertNotEqual(self.result, None)

if __name__ == '__main__':
    unittest.main()