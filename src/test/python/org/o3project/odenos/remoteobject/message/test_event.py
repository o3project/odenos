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

from org.o3project.odenos.remoteobject.message.event import Event
from org.o3project.odenos.core.component.network.flow.flow import Flow
import unittest


class EventTest(unittest.TestCase):

    Type = "BasicFlow"
    Version = "v01"
    Flow_id = "Id01"
    Owner = "Owner"
    Enabled = True
    Priority = "65535"
    Status = "none"
    Attributes = {"bandwidth": "10Mbps", "req_bandwidth": "11Mbps",
                  "latency": "10msec", "req_latency": "11msec"}
    Publisher_id = 'Id1'
    Event_type = 'flowchanged'

    flow_target = Flow(Type, Version, Flow_id, Owner,
                       Enabled, Priority, Status,
                       Attributes)

    flow_target_packed_object = flow_target.packed_object()

    def setUp(self):
        self.target = Event(self.Publisher_id,
                            self.Event_type,
                            self.flow_target)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target.publisher_id, self.Publisher_id)
        self.assertEqual(self.target.event_type, self.Event_type)
        self.assertEqual(self.target.body,
                         self.flow_target)

    def test_create_from_packed(self):
        Type = "BasicFlow"
        Version = "v02"
        Flow_id = "Id02"
        Owner = "Owner"
        Enabled = True
        Priority = "65535"
        Status = "none"
        Attributes = {"bandwidth": "20Mbps", "req_bandwidth": "11Mbps",
                      "latency": "30msec", "req_latency": "11msec"}
        Publisher_id = 'Id1'
        Event_type = 'flowchanged'

        flow_target = Flow(Type, Version, Flow_id, Owner,
                           Enabled, Priority, Status,
                           Attributes)
        value = [Publisher_id, Event_type, flow_target]
        self.result = self.target.create_from_packed(value)

        self.assertEqual(self.result.publisher_id, self.Publisher_id)
        self.assertEqual(self.result.event_type, self.Event_type)
        self.assertEqual(self.result.body, flow_target)

    def test_packed_object_hasattr_True(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result[0], self.Publisher_id)
        self.assertEqual(self.result[1], self.Event_type)
        self.assertEqual(self.result[2], self.flow_target_packed_object)

    def test_packed_object_hasattr_False(self):
        self.newtarget = Event(self.Publisher_id,
                               self.Event_type,
                               self.flow_target_packed_object)
        self.result = self.newtarget.packed_object()
        self.assertEqual(self.result[0], self.Publisher_id)
        self.assertEqual(self.result[1], self.Event_type)
        self.assertEqual(self.result[2], self.flow_target_packed_object)


if __name__ == '__main__':
    unittest.main()
