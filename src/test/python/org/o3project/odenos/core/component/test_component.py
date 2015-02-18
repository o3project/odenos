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

from org.o3project.odenos.core.component.component import Component
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from mock import Mock, patch
from contextlib import nested
import unittest


class ComponentTest(unittest.TestCase):
    Dispatcher = Mock()
    Dispatcher.system_manager_id = "ObjectId1"
    Object_id = "ObjectId1"

    def setUp(self):
        self.target = Component(self.Object_id, self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        with nested(
                patch('org.o3project.odenos.core.component.component.'
                      'Component._set_super_type'),
                patch('org.o3project.odenos.core.component.component.'
                      'Component._reset_event_subscription'
                      )) as (m_set_super_type, m_reset_event):

                self.result = Component(self.Object_id, self.Dispatcher)
                self.assertEqual(self.result.dispatcher, self.Dispatcher)
                self.assertEqual(self.result.object_id, self.Object_id)
                self.assertEqual(m_set_super_type.call_count, 1)
                self.assertEqual(m_reset_event.call_count, 1)

    def test__set_super_type(self):
        with nested(
                patch('org.o3project.odenos.remoteobject.object_property.'
                      'ObjectProperty.set_property'
                      )) as m_set_property:

                self.target._set_super_type()
                self.assertEqual(m_set_property[0].call_count, 1)
                m_set_property[0].assert_called_once_with(ObjectProperty.
                                                          OBJECT_SUPER_TYPE,
                                                          self.target.
                                                          _super_type)

    def test__reset_event_subscription(self):
        system_manager_id_tmp = self.Dispatcher.system_manager_id
        Type = "ComponentConnectionChanged"
        with nested(
                patch('org.o3project.odenos.remoteobject.manager.event_subscription.'
                      'EventSubscription.clear_filter'),
                patch('org.o3project.odenos.remoteobject.manager.event_subscription.'
                      'EventSubscription.add_filter'),
                patch('org.o3project.odenos.remoteobject.remote_object.'
                      'RemoteObject._apply_event_subscription'
                      )) as (m_clear_filter, m_add_filter, m_apply_event):

                self.target._reset_event_subscription()
                self.assertEqual(m_clear_filter.call_count, 1)
                self.assertEqual(m_add_filter.call_count, 1)
                self.assertEqual(m_apply_event.call_count, 1)
                m_add_filter.assert_called_once_with(system_manager_id_tmp,
                                                     Type)

if __name__ == '__main__':
    unittest.main()
