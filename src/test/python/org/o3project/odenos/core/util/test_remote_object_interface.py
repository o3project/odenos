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

from org.o3project.odenos.core.util.remote_object_interface import (
    RemoteObjectInterface)
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
import unittest
from mock import Mock, patch
from contextlib import nested


class RemoteObjectInterfaceTest(unittest.TestCase):

    def setUp(self):
        self.Disppatcher = Mock()
        self.object_id = "ObjectId"
        self.target = RemoteObjectInterface(self.Disppatcher,
                                            self.object_id)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._RemoteObjectInterface__dispatcher,
                         self.Disppatcher)
        self.assertEqual(self.target._RemoteObjectInterface__object_id,
                         self.object_id)

    def test_object_id(self):
        self.assertEqual(self.target.object_id,
                         self.object_id)

    def test_get_property_success(self):
        value = "propertyBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:

            self.result = self.target.get_property()

            self.assertEqual(m_get_object.call_count, 1)
            self.assertEqual(m_get_object.call_args[0][0],
                             RemoteObjectInterface.PROPETY_PATH)
            self.assertNotEqual(self.result, None)

    def test_get_property_StatusCode_NotOK(self):
        value = "propertyBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         value)
                   ) as m_get_object:

            self.result = self.target.get_property()

            self.assertEqual(m_get_object.call_count, 1)
            self.assertEqual(m_get_object.call_args[0][0],
                             RemoteObjectInterface.PROPETY_PATH)
            self.assertEqual(self.result, None)

    def test_get_settings_success(self):
        value = "settingsBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, value)
                   ) as m_get_object:

            self.result = self.target.get_settings()

            self.assertEqual(m_get_object.call_count, 1)
            self.assertEqual(m_get_object.call_args[0][0],
                             RemoteObjectInterface.SETTINGS_PATH)
            self.assertNotEqual(self.result, None)

    def test_get_settings_StatusCode_NotOK(self):
        value = "settingsBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._get_object_to_remote_object',
                   return_value=Response(Response.StatusCode.NOT_FOUND,
                                         value)
                   ) as m_get_object:

            self.result = self.target.get_settings()

            self.assertEqual(m_get_object.call_count, 1)
            self.assertEqual(m_get_object.call_args[0][0],
                             RemoteObjectInterface.SETTINGS_PATH)
            self.assertEqual(self.result, None)

    def test_put_property_success(self):
        value = "propertyBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object'
                   ) as m_put_object:

            self.result = self.target.put_property(value)

            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_any_call(
                RemoteObjectInterface.PROPETY_PATH, value)
            self.assertNotEqual(self.result, None)

    def test_put_setting_success(self):
        Path = RemoteObjectInterface.SETTINGS_PATH
        value = "settingsBody"
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object'
                   ) as m_put_object:

            self.result = self.target.put_settings(value)

            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_any_call(Path, value)
            self.assertNotEqual(self.result, None)

    def test__post_object_to_remote_object_success(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.POST
        _object_id = self.object_id
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(Response.StatusCode.OK, value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._post_object_to_remote_object(Path,
                                                                        value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, value)
                self.assertEqual(m_log_debug.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test__post_object_to_remote_object_StatusCode_NotOK(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.POST
        _object_id = self.object_id
        status_code = Response.StatusCode.NOT_FOUND
        debug_log = "Error Response POST DestID:" + _object_id\
                    + " Path:" + Path\
                    + " StatusCode:" + str(status_code)
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(status_code,
                                            value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._post_object_to_remote_object(Path,
                                                                        value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, value)
                self.assertEqual(m_log_debug.call_count, 1)
                m_log_debug.assert_any_call(debug_log)
                self.assertNotEqual(self.result, None)

    def test__put_object_to_remote_object_success(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.PUT
        _object_id = self.object_id
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(Response.StatusCode.OK, value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._put_object_to_remote_object(Path,
                                                                       value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, value)
                self.assertEqual(m_log_debug.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test__put_object_to_remote_object_StatusCode_NotOK(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.PUT
        _object_id = self.object_id
        status_code = Response.StatusCode.NOT_FOUND
        debug_log = "Error Response PUT DestID:" + _object_id\
                    + " Path:" + Path\
                    + " StatusCode:" + str(status_code)
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(status_code,
                                            value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._put_object_to_remote_object(Path,
                                                                       value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, value)
                self.assertEqual(m_log_debug.call_count, 1)
                m_log_debug.assert_any_call(debug_log)
                self.assertNotEqual(self.result, None)

    def test__del_object_to_remote_object_success(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.DELETE
        _object_id = self.object_id
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(Response.StatusCode.OK, value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._del_object_to_remote_object(Path,
                                                                       value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, body=value)
                self.assertEqual(m_log_debug.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test__del_object_to_remote_object_success_Nobody(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.DELETE
        _object_id = self.object_id

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(Response.StatusCode.OK, None)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._del_object_to_remote_object(Path)

                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, body=None)
                self.assertEqual(m_log_debug.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test__del_object_to_remote_object_StatusCode_NotOK(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.DELETE
        _object_id = self.object_id
        status_code = Response.StatusCode.NOT_FOUND
        debug_log = "Error Response DELETE DestID:" + _object_id\
                    + " Path:" + Path\
                    + " StatusCode:" + str(status_code)
        value = "propertyBody"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(status_code,
                                            value)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._del_object_to_remote_object(Path,
                                                                       value)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method,
                                               Path, body=value)
                self.assertEqual(m_log_debug.call_count, 1)
                m_log_debug.assert_any_call(debug_log)
                self.assertNotEqual(self.result, None)

    def test__get_object_to_remote_object_success(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.GET
        _object_id = self.object_id

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(Response.StatusCode.OK, None)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._get_object_to_remote_object(Path)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method, Path)
                self.assertEqual(m_log_debug.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test__get_object_to_remote_object_StatusCode_NotOK(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.GET
        _object_id = self.object_id
        status_code = Response.StatusCode.NOT_FOUND
        debug_log = "Error Response GET DestID:" + _object_id\
                    + " Path:" + Path\
                    + " StatusCode:" + str(status_code)

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface.'
                      '_RemoteObjectInterface__send_request',
                      return_value=Response(status_code,
                                            None)),
                patch('logging.debug')) as (m_send_request, m_log_debug):

                self.result = self.target._get_object_to_remote_object(Path)
                self.assertEqual(m_send_request.call_count, 1)
                m_send_request.assert_any_call(_object_id, method, Path)
                self.assertEqual(m_log_debug.call_count, 1)
                m_log_debug.assert_any_call(debug_log)
                self.assertNotEqual(self.result, None)

    def test___send_request_success(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.GET
        _object_id = self.object_id
        value = "propertyBody"
        dmy_request_sync = self.Disppatcher.request_sync
        dmy_request_sync.return_value = "result_request_sync"

        with patch('logging.error') as m_log_error:

            self.result = self.target._RemoteObjectInterface__send_request(
                _object_id, method, Path, value)

            self.assertEqual(dmy_request_sync.call_count, 1)
            self.assertEqual(dmy_request_sync.call_args[0][0].
                             packed_object(),
                             (_object_id, method, Path, value))
            self.assertEqual(m_log_error[0].call_count, 0)
            self.assertEqual(self.result, "result_request_sync")

    def test___send_request_request_sync_error(self):
        Path = RemoteObjectInterface.PROPETY_PATH
        method = Request.Method.GET
        _object_id = self.object_id
        value = "propertyBody"
        dmy_request_sync = self.Disppatcher.request_sync
        dmy_request_sync.side_effect = KeyError(1)
        dmy_request_sync.return_value = "result_request_sync"
        debug_log = "Exception: Request to " + _object_id \
                    + " Method:" + method\
                    + " Path:" + Path

        with nested(
                patch('logging.error')) as m_log_error:

                self.result = self.target._RemoteObjectInterface__send_request(
                    _object_id, method, Path, value)

                self.assertEqual(dmy_request_sync.call_count, 1)
                self.assertEqual(dmy_request_sync.call_args[0][0].
                                 packed_object(),
                                 (_object_id, method, Path, value))
                self.assertEqual(m_log_error[0].call_count, 2)
                m_log_error[0].assert_any_call(debug_log)
                self.assertNotEqual(self.result, "result_request_sync")
                self.assertEqual(self.result.packed_object(),
                                 (Response.StatusCode.INTERNAL_SERVER_ERROR,
                                  None))

if __name__ == '__main__':
    unittest.main()
