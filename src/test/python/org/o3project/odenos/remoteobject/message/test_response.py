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

from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.component.network.flow.flow import Flow
import unittest


class ResponseTest(unittest.TestCase):

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
    Packed = [Response.StatusCode.OK, value]

    flow_target = Flow(Type, Version, Flow_id, Owner,
                       Enabled, Priority, Status,
                       Attributes)

    flow_target_packed_object = flow_target.packed_object()

    def setUp(self):
        self.target = Response(Response.StatusCode.OK, self.flow_target)

    def tearDown(self):
        self.target = None

    def test_constructor_hasattr_True(self):
        self.assertEqual(self.target.status_code,
                         Response.StatusCode.OK)
        self.assertEqual(self.target.body,
                         self.value)

    def test_constructor_hasattr_Falseself(self):
        self.target2 = Response(Response.StatusCode.CREATED,
                                self.value)
        self.assertEqual(self.target2.status_code,
                         Response.StatusCode.CREATED)
        self.assertEqual(self.target2.body,
                         self.value)

    def test_is_error_method_GET_status_code_OK(self):
        self.result = Response(Response.StatusCode.OK, self.flow_target)
        self.assertEqual(self.result.is_error("GET"),
                         False)

    def test_is_error_method_GET_status_code_NotOK(self):
        self.result = Response(Response.StatusCode.CREATED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("GET"),
                         True)

    def test_is_error_method_DELETE_status_code_OK(self):
        self.result = Response(Response.StatusCode.OK, self.flow_target)
        self.assertEqual(self.result.is_error("DELETE"),
                         False)

    def test_is_error_method_DELETE_status_code_NotOK(self):
        self.result = Response(Response.StatusCode.CREATED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("DELETE"),
                         True)

    def test_is_error_method_PUT_status_code_OK(self):
        self.result = Response(Response.StatusCode.OK, self.flow_target)
        self.assertEqual(self.result.is_error("PUT"),
                         False)

    def test_is_error_method_PUT_status_code_CREATED(self):
        self.result = Response(Response.StatusCode.CREATED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("PUT"),
                         False)

    def test_is_error_method_PUT_status_code_NotOKandCREATED(self):
        self.result = Response(Response.StatusCode.ACCEPTED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("PUT"),
                         True)

    def test_is_error_method_other_status_code_OK(self):
        self.result = Response(Response.StatusCode.OK, self.flow_target)
        self.assertEqual(self.result.is_error("POST"),
                         False)

    def test_is_error_method_other_status_code_CREATED(self):
        self.result = Response(Response.StatusCode.CREATED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("POST"),
                         False)

    def test_is_error_method_other_status_code_NotOKandCREATED(self):
        self.result = Response(Response.StatusCode.ACCEPTED,
                               self.flow_target)
        self.assertEqual(self.result.is_error("POST"),
                         True)

    def test_create_from_packed(self):
        self.result = self.target.create_from_packed(self.Packed)
        self.assertEqual(self.result.status_code,
                         Response.StatusCode.OK)
        self.assertEqual(self.result.body,
                         self.value)

    def test_packed_object(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result, (Response.StatusCode.OK,
                                       self.value))
        self.assertEqual(len(self.result), 2)


if __name__ == '__main__':
    unittest.main()
