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
from org.o3project.odenos.core.component.component import Component
from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.core.component.dummy_driver import DummyDriver
from org.o3project.odenos.remoteobject.remote_object_manager import RemoteObjectManager
from org.o3project.odenos.core.manager.component_manager import ComponentManager 
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.util.request_parser import RequestParser

import unittest
import logging
from mock import Mock, MagicMock, patch
from contextlib import nested


class ComponentManagerTest(unittest.TestCase, RemoteObjectManager):
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
        self.assertEqual(
            self.target._object_property._object_property["type"],
            "ComponentManager")
        self.assertEqual(
            self.target._object_property._object_property["id"],
            "cc_action")
        self.assertEqual(
            self.target._object_property._object_property["state"],
            "initializing")
        self.assertEqual(self.target.remote_object_classes, {})
        self.assertEqual(self.target.remote_objects, {})

    def test_register_components_success(self):
        self.value = Component 
        self.target.register_components([self.value])
        self.assertEqual(self.target.remote_object_classes["Component"], self.value) 

    def test_do_get_component_types(self):
        self.target.remote_object_classes = {"SampleDummyDriver": SampleDummyDriver}
        self.result = self.target._do_get_component_types()
        self.assertEqual(self.result.status_code, 200)
        self.assertEqual(
            self.result.body["SampleDummyDriver"]["type"], "SampleDummyDriver")
        self.assertEqual(
            self.result.body["SampleDummyDriver"]["super_type"], "Driver")
        self.assertEqual(
            len(self.result.body["SampleDummyDriver"]["connection_types"]), 2)

    def test_do_get_component_types_error(self):
        self.target.remote_object_classes = {"DummyDriver": self}
        self.result = self.target._do_get_component_types()
        self.assertEqual(self.result.status_code, 500)

if __name__ == '__main__':
    unittest.main()

class SampleDummyDriver(DummyDriver):
    CONNECTION_TYPES = "original:1,aggregated:1"