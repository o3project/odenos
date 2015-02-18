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
from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.remoteobject.remote_object_manager import RemoteObjectManager
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.util.request_parser import RequestParser

import unittest
import logging
from mock import Mock, MagicMock, patch
from contextlib import nested


class RemoteObjectManagerTest(unittest.TestCase, RemoteObjectManager):
    MessageDispatcher = MagicMock()
    value = None
    result = None

    def setUp(self):
        self.target = RemoteObjectManager(
            "rm_action",
            self.MessageDispatcher)

    def tearDown(self):
        self.target = None
        self.dispatcher = None

    def test_constructor(self):
        self.assertEqual(
            self.target._object_property._object_property["type"],
            "RemoteObjectManager")
        self.assertEqual(
            self.target._object_property._object_property["id"],
            "rm_action")
        self.assertEqual(
            self.target._object_property._object_property["state"],
            "initializing")
        self.assertEqual(self.target.remote_object_classes, {})
        self.assertEqual(self.target.remote_objects, {})

    def test_register_remote_objects_success(self):
        self.value = RemoteObject
        self.target.register_remote_objects([self.value])
        self.assertEqual(self.target.remote_object_classes["RemoteObject"], self.value) 

    def test_add_rules(self):
        self.target._parser._RequestParser__rules = []
        self.target._add_rules()
        rules = self.target._parser._RequestParser__rules
        self.assertEqual(len(rules), 5)
        self.assertEqual(
            rules[0], {RequestParser.PATTERN: "^remote_object_types/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_remote_object_types,
                       RequestParser.PARAMS: 0})

        self.assertEqual(
            rules[1], {RequestParser.PATTERN: "^remote_objects/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_remote_objects,
                       RequestParser.PARAMS: 0})

        self.assertEqual(
            rules[2], {RequestParser.PATTERN: "^remote_objects/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.PUT,
                       RequestParser.FUNC: self.target._do_put_remote_object,
                       RequestParser.PARAMS: 2})

        self.assertEqual(
            rules[3], {RequestParser.PATTERN: "^remote_objects/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.GET,
                       RequestParser.FUNC: self.target._do_get_remote_object,
                       RequestParser.PARAMS: 1})

        self.assertEqual(
            rules[4], {RequestParser.PATTERN: "^remote_objects/"
                       + "([a-zA-Z0-9_-]+)/?$",
                       RequestParser.METHOD: Request.Method.DELETE,
                       RequestParser.FUNC: self.target._do_delete_remote_object,
                       RequestParser.PARAMS: 1})

    def test_on_request(self):
        request = Request("ObjectId",
                          Request.Method.GET,
                          "component_managers",
                          None)
        self.result = self.target._on_request(request)
        self.assertEqual(self.result.status_code, 404)
        self.assertEqual(self.result.body, None)

    def test_do_get_remote_object_id_in_remote_objects(self):
        self.value = RemoteObjectManager(
            "456",
            self.MessageDispatcher)
        self.target.remote_objects = {"object_id": self.value}
        self.result = self.target._do_get_remote_object("object_id")
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(self.result.body["type"], "RemoteObjectManager")
        self.assertEqual(self.result.body["id"], "456")

    def test_do_get_component_object_id_not_in_remote_objects(self):
        self.value = RemoteObjectManager(
            "456",
            self.MessageDispatcher)
        self.target.remote_objects = {"False_id": self.value}
        self.result = self.target._do_get_remote_object("object_id")
        self.assertEqual(self.result.status_code, 404)
        self.assertEqual(self.result.body, None)

    def test_do_put_remote_object_success(self):
        self.target.remote_object_classes = {}
        self.target.remote_object_classes["RemoteObjectManager"] = RemoteObjectManager 
        self.target.remote_objects = {}
        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 201)

    def test_do_put_remote_object_remote_object_type_not_in_remote_object_classes(self):
        self.target.remote_object_classes = {}
        self.target.remote_objects = {}
        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 400)

    def test_do_put_remote_object_object_id_in_remote_objects(self):
        self.target.remote_object_classes = {}
        self.target.remote_object_classes["RemoteObjectManager"] = RemoteObjectManager 
        self.target.remote_objects = {}
        self.target.remote_objects["456"] = None 
        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 409)

    def test_do_put_remote_object_error(self):
        self.target.remote_object_classes = {}
        self.target.remote_object_classes["RemoteObjectManager"] = RemoteObjectManager 
        self.target.remote_objects = MagicMock()
        self.target.remote_objects["456"].on_initialize.return_value = False

        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 201)

    def test_do_delete_remote_object(self):
        self.target.remote_object_classes = {}
        self.target.remote_object_classes["RemoteObjectManager"] = RemoteObjectManager 
        self.target.remote_objects = {}

        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 201)

        self.value = {"type": "RemoteObjectManager",
                      "id": "123",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "123")
        self.assertEqual(self.result.status_code, 201)

        self.result = self.target._do_delete_remote_object("456")
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(self.result.body, None)
        self.assertEqual(len(self.target.remote_objects), 1)

    def test_do_get_remote_objects(self):
        self.target.remote_object_classes = {}
        self.target.remote_object_classes["RemoteObjectManager"] = RemoteObjectManager 
        self.target.remote_objects = {}

        self.value = {"type": "RemoteObjectManager",
                      "id": "456",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "456")
        self.assertEqual(self.result.status_code, 201)

        self.value = {"type": "RemoteObjectManager",
                      "id": "123",
                      "base_uri": "789"}
        self.result = self.target._do_put_remote_object(self.value, "123")
        self.assertEqual(self.result.status_code, 201)

        self.result = self.target._do_get_remote_objects()
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(
            self.result.body["456"]["type"], "RemoteObjectManager")
        self.assertEqual(
            self.result.body["456"]["id"], "456")
        self.assertEqual(
            self.result.body["456"]["state"], "running")

        self.assertEqual(
            self.result.body["123"]["type"], "RemoteObjectManager")
        self.assertEqual(
            self.result.body["123"]["id"], "123")
        self.assertEqual(
            self.result.body["123"]["state"], "running")

    def test_do_get_do_get_remote_object_types(self):
        self.target.remote_object_classes = {"123": "Type123",
                                         "465": "Type456",
                                         "789": "Type789"}
        self.result = self.target._do_get_remote_object_types()
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(
            self.result.body, self.target.remote_object_classes.keys())

if __name__ == '__main__':
    unittest.main()
