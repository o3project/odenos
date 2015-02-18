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

from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.core.component.network.flow.flow import Flow
import unittest


class RequestTest(unittest.TestCase):

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
    Event_type = 'flow'
    value = {"type": Type, "version": Version,
             "flow_id": Flow_id, "owner": Owner,
             "enabled": Enabled, "priority": Priority,
             "status": Status, "attributes": Attributes}
    Packed = ["FlowId2", Request.Method.GET, "flows/FlowId2", value]

    flow_target = Flow(Type, Version, Flow_id, Owner,
                       Enabled, Priority, Status,
                       Attributes)

    flow_target_packed_object = flow_target.packed_object()

    def setUp(self):
        self.target = Request("FlowId1",
                              Request.Method.GET,
                              "flows/FlowId1",
                              self.flow_target)

    def tearDown(self):
        self.target = None

    def test_constructor_hasattr_True(self):
        self.assertEqual(self.target.object_id,
                         "FlowId1")
        self.assertEqual(self.target.method,
                         Request.Method.GET)
        self.assertEqual(self.target.path,
                         "flows/FlowId1")
        self.assertEqual(self.target.body,
                         self.value)

    def test_constructor_hasattr_False(self):
        self.target2 = Request("FlowId2",
                               Request.Method.GET,
                               "flows/FlowId2",
                               self.value)
        self.assertEqual(self.target2.object_id,
                         "FlowId2")
        self.assertEqual(self.target2.method,
                         Request.Method.GET)
        self.assertEqual(self.target2.path,
                         "flows/FlowId2")
        self.assertEqual(self.target2.body,
                         self.value)

    def test_create_from_packed(self):
        self.result = self.target.create_from_packed(self.Packed)
        self.assertEqual(self.result.object_id,
                         "FlowId2")
        self.assertEqual(self.result.method,
                         Request.Method.GET)
        self.assertEqual(self.result.path,
                         "flows/FlowId2")
        self.assertEqual(self.result.body,
                         self.value)

    def test_packed_object(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result,
                         ("FlowId1",
                          Request.Method.GET,
                          "flows/FlowId1",
                          self.value))
        self.assertEqual(len(self.result), 4)

if __name__ == '__main__':
    unittest.main()