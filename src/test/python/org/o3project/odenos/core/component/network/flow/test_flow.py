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

from org.o3project.odenos.core.component.network.flow.flow import Flow
import unittest


class FlowTest(unittest.TestCase):

    Type = "BasicFlow"
    Version = "v01"
    Flow_id = "Id01"
    Owner = "Owner"
    Enabled = True
    Priority = 65535
    Status = "none"
    Attributes = {"bandwidth": 10, "req_bandwidth": 11,
                  "latency": 10, "req_latency": 11}

    def setUp(self):
        self.target = Flow(self.Type, self.Version, self.Flow_id, self.Owner,
                           self.Enabled, self.Priority, self.Status,
                           self.Attributes)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.target._body[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.target._body[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.target._body[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.target._body[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.target._body[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.target._body[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.target._body[self.target.ATTRIBUTES],
                         self.Attributes)

    def test_type_property(self):
        self.assertEqual(self.target.type, self.Type)

    def test_version_property(self):
        self.assertEqual(self.target.version, self.Version)

    def test_flow_id_property(self):
        self.assertEqual(self.target.flow_id, self.Flow_id)

    def test_owner_property(self):
        self.assertEqual(self.target.owner, self.Owner)

    def test_enabled_property(self):
        self.assertEqual(self.target.enabled, self.Enabled)

    def test_enabled_setter(self):
        self.assertEqual(self.target._body[self.target.ENABLED],
                         self.Enabled)
        self.target.enabled = False
        self.assertEqual(self.target._body[self.target.ENABLED],
                         False)

    def test_priority_property(self):
        self.assertEqual(self.target.priority, self.Priority)

    def test_priority_setter(self):
        self.assertEqual(self.target._body[self.target.PRIORITY],
                         self.Priority)
        self.target.priority = 0
        self.assertEqual(self.target._body[self.target.PRIORITY],
                         0)

    def test_status_property(self):
        self.assertEqual(self.target.status, self.Status)

    def test_status_setter(self):
        self.assertEqual(self.target._body[self.target.STATUS],
                         self.Status)
        self.target.status = "establishing"
        self.assertEqual(self.target._body[self.target.STATUS],
                         "establishing")

    def test_attributes_property(self):
        self.assertEqual(self.target.attributes, self.Attributes)

    def test_create_from_packed_Version_NotNone(self):

        Type2 = "OFPFlow"
        Version2 = "v02"
        Flow_id2 = "Id02"
        Owner2 = "Owner2"
        Enabled2 = False
        Priority2 = 1
        Status2 = "established"
        Attributes2 = {"bandwidth": 12, "req_bandwidth": 13,
                       "latency": 12, "req_latency": 13}
        self.value = {"type": Type2, "version": Version2,
                      "flow_id": Flow_id2, "owner": Owner2,
                      "enabled": Enabled2, "priority": Priority2,
                      "status": Status2, "attributes": Attributes2}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         Type2)
        self.assertEqual(self.result._body[self.target.VERSION],
                         Version2)
        self.assertEqual(self.result._body[self.target.FLOW_ID],
                         Flow_id2)
        self.assertEqual(self.result._body[self.target.OWNER],
                         Owner2)
        self.assertEqual(self.result._body[self.target.ENABLED],
                         Enabled2)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         Priority2)
        self.assertEqual(self.result._body[self.target.STATUS],
                         Status2)
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         Attributes2)

    def test_create_from_packed_Version_None(self):
        Type2 = "OFPFlow"
        Flow_id2 = "Id02"
        Owner2 = "Owner2"
        Enabled2 = False
        Priority2 = 1
        Status2 = "established"
        Attributes2 = {"bandwidth": 12, "req_bandwidth": 13,
                       "latency": 12, "req_latency": 13}
        self.value = {"type": Type2,
                      "flow_id": Flow_id2, "owner": Owner2,
                      "enabled": Enabled2, "priority": Priority2,
                      "status": Status2, "attributes": Attributes2}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         Type2)
        self.assertEqual(self.result._body[self.target.VERSION],
                         None)
        self.assertEqual(self.result._body[self.target.FLOW_ID],
                         Flow_id2)
        self.assertEqual(self.result._body[self.target.OWNER],
                         Owner2)
        self.assertEqual(self.result._body[self.target.ENABLED],
                         Enabled2)
        self.assertEqual(self.result._body[self.target.PRIORITY],
                         Priority2)
        self.assertEqual(self.result._body[self.target.STATUS],
                         Status2)
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         Attributes2)

    def test_packed_object(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result[self.target.TYPE],
                         self.Type)
        self.assertEqual(self.result[self.target.VERSION],
                         self.Version)
        self.assertEqual(self.result[self.target.FLOW_ID],
                         self.Flow_id)
        self.assertEqual(self.result[self.target.OWNER],
                         self.Owner)
        self.assertEqual(self.result[self.target.ENABLED],
                         self.Enabled)
        self.assertEqual(self.result[self.target.PRIORITY],
                         self.Priority)
        self.assertEqual(self.result[self.target.STATUS],
                         self.Status)
        self.assertEqual(self.result[self.target.ATTRIBUTES],
                         self.Attributes)

if __name__ == '__main__':
    unittest.main()
