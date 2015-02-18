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

from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket

import unittest


class OutPacketTest(unittest.TestCase):
    value = None
    result = None

    def setUp(self):
        self.target = OutPacket("out_packet_id",
                                "out_packet_type",
                                {"attributes": "attributes_value"},
                                "out_packet_node",
                                "out_packet_ports",
                                "out_packet_ports-except",
                                {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                                "out_packet_data")

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.PACKET_ID],
                         "out_packet_id")
        self.assertEqual(self.target._body[self.target.TYPE],
                         "out_packet_type")
        self.assertEqual(self.target._body[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.target._body[self.target.NODE],
                         "out_packet_node")
        self.assertEqual(self.target._body[self.target.PORTS],
                         "out_packet_ports")
        self.assertEqual(self.target._body[self.target.PORTS_EXCEPT],
                         "out_packet_ports-except")
        self.assertEqual(self.target._body[self.target.HEADER],
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.target._body[self.target.DATA],
                         "out_packet_data")

    def test_node(self):
        self.assertEqual(self.target.node, "out_packet_node")

    def test_node_setter(self):
        self.target.node = "out_packet_node2"
        self.assertEqual(self.target.node, "out_packet_node2")

    def test_ports(self):
        self.assertEqual(self.target.ports, "out_packet_ports")

    def test_ports_setter(self):
        self.target.ports = ["out_packet_port1", "out_packet_port2"]
        self.assertEqual(self.target.ports,
                         ["out_packet_port1", "out_packet_port2"])

    def test_ports_except(self):
        self.assertEqual(self.target.ports_except, "out_packet_ports-except")

    def test_ports_except_setter(self):
        self.target.ports_except = ["out_packet_port3", "out_packet_port4"]
        self.assertEqual(self.target.ports_except,
                         ["out_packet_port3", "out_packet_port4"])

    def test_header(self):
        self.assertEqual(self.target.header, {"type": "BasicFlowMatch",
                                              "in_port": "123456",
                                              "in_node": "123456789"})

    def test_data(self):
        self.assertEqual(self.target.data, "out_packet_data")

    def test_create_from_packed_Not_None(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": "6540",
                      "ports-except": "6540",
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.result = OutPacket.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result._body[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result._body[self.target.NODE],
                         "9870")
        self.assertEqual(self.result._body[self.target.PORTS],
                         "6540")
        self.assertEqual(self.result._body[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result._body[self.target.HEADER]._body,
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result._body[self.target.DATA],
                         "0147")

    def test_create_from_packed_portsEx_none(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": "6540",
                      "ports-except": None,
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.result = OutPacket.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result._body[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result._body[self.target.NODE],
                         "9870")
        self.assertEqual(self.result._body[self.target.PORTS],
                         "6540")
        self.assertEqual(self.result._body[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result._body[self.target.HEADER]._body,
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result._body[self.target.DATA],
                         "0147")

    def test_create_from_packed_ports_none(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": None,
                      "ports-except": "6540",
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.result = OutPacket.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result._body[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result._body[self.target.NODE],
                         "9870")
        self.assertEqual(self.result._body[self.target.PORTS],
                         None)
        self.assertEqual(self.result._body[self.target.PORTS_EXCEPT],
                         "6540")
        self.assertEqual(self.result._body[self.target.HEADER]._body,
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result._body[self.target.DATA],
                         "0147")

    def test_create_from_packed_Port_PortEx_none(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": None,
                      "ports-except": None,
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.result = OutPacket.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result._body[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result._body[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result._body[self.target.NODE],
                         "9870")
        self.assertEqual(self.result._body[self.target.PORTS],
                         None)
        self.assertEqual(self.result._body[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result._body[self.target.HEADER]._body,
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result._body[self.target.DATA],
                         "0147")

    def test_packed_object_Not_None(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": "6540",
                      "ports-except": "6540",
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.create = OutPacket.create_from_packed(self.value)

        self.result = self.create.packed_object()

        self.assertEqual(self.result[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result[self.target.NODE],
                         "9870")
        self.assertEqual(self.result[self.target.PORTS],
                         "6540")
        self.assertEqual(self.result[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result[self.target.HEADER],
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result[self.target.DATA],
                         "0147")

    def test_packed_object_portsEx_none(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": "6540",
                      "ports-except": None,
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.create = OutPacket.create_from_packed(self.value)

        self.result = self.create.packed_object()

        self.assertEqual(self.result[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result[self.target.NODE],
                         "9870")
        self.assertEqual(self.result[self.target.PORTS],
                         "6540")
        self.assertEqual(self.result[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result[self.target.HEADER],
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result[self.target.DATA],
                         "0147")

    def test_packed_object_ports_none(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": None,
                      "ports-except": "6540",
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.create = OutPacket.create_from_packed(self.value)

        self.result = self.create.packed_object()

        self.assertEqual(self.result[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result[self.target.NODE],
                         "9870")
        self.assertEqual(self.result[self.target.PORTS],
                         None)
        self.assertEqual(self.result[self.target.PORTS_EXCEPT],
                         "6540")
        self.assertEqual(self.result[self.target.HEADER],
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result[self.target.DATA],
                         "0147")

    def test_packed_object_Ports_PortsEx_None(self):
        self.value = {"packet_id": "0123",
                      "type": "0456",
                      "attributes": {"attributes": "attributes_value"},
                      "node": "9870",
                      "ports": None,
                      "ports-except": None,
                      "header": {"type": "BasicFlowMatch",
                                 "in_port": "123456",
                                 "in_node": "123456789"},
                      "data": "0147"}

        self.create = OutPacket.create_from_packed(self.value)

        self.result = self.create.packed_object()

        self.assertEqual(self.result[self.target.PACKET_ID],
                         "0123")
        self.assertEqual(self.result[self.target.TYPE],
                         "0456")
        self.assertEqual(self.result[self.target.ATTRIBUTES],
                         {"attributes": "attributes_value"})
        self.assertEqual(self.result[self.target.NODE],
                         "9870")
        self.assertEqual(self.result[self.target.PORTS],
                         None)
        self.assertEqual(self.result[self.target.PORTS_EXCEPT],
                         None)
        self.assertEqual(self.result[self.target.HEADER],
                         {"type": "BasicFlowMatch",
                          "in_port": "123456",
                          "in_node": "123456789"})
        self.assertEqual(self.result[self.target.DATA],
                         "0147")

if __name__ == '__main__':
    unittest.main()
