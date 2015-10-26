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

from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.topology import Topology
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.flow.flow_set import FlowSet
from org.o3project.odenos.core.component.network.flow.basic.basic_flow import (
    BasicFlow)
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.core.component.network.packet.in_packet import InPacket
from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket
from org.o3project.odenos.core.component.network.packet.packet_status\
    import PacketStatus
from org.o3project.odenos.core.component.network.flow.basic.basic_flow_match\
    import BasicFlowMatch
import unittest
from mock import Mock, patch
from contextlib import nested


class NetworkInterfaceTest(unittest.TestCase):
    Disppatcher = Mock()
    Network_id = "network1"

    def setUp(self):
        self.target = NetworkInterface(self.Disppatcher, self.Network_id)

    def tearDown(self):
        self.target = None

    def test_network_id(self):
        self.assertEqual(self.target.network_id, self.Network_id)

    def test_get_topology_StatusCode_OK_Create_from_packed_Ok(self):
        Type = "Topology"
        Version = "v02"
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Nodes1 = {'NodeId1': {'type': 'Node', 'version': '1',
                              'node_id': 'NodeId1', 'ports': Port1,
                              'attributes': {}}}
        links1 = {'LinkId1': {'type': 'Link', 'version': '1',
                              'link_id': 'LinkId1', 'src_node': 'NodeId1',
                              'src_port': 'PortId1', 'dst_node': 'NodeId2',
                              'dst_port': 'PortId3', 'attributes': {}}}

        body = {"type": Type,
                "version": Version,
                "nodes": Nodes1,
                "links": links1}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, None)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'topology.Topology.create_from_packed',
                      return_value=Topology.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):
                self.result = self.target.get_topology()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.TOPOLOGY_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_topology_StatusCode_OK_Create_from_packed_NG(self):
        Type = "Topology"
        Version = "v02"
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Nodes1 = {'NodeId1': {'type': 'Node', 'version': '1',
                              'node_id': 'NodeId1', 'ports': Port1,
                              'attributes': {}}}
        links1 = {'LinkId1': {'type': 'Link', 'version': '1',
                              'link_id': 'LinkId1', 'src_node': 'NodeId1',
                              'src_port': 'PortId1', 'dst_node': 'NodeId2',
                              'dst_port': 'PortId3', 'attributes': {}}}
        body = {"type": Type,
                "version": Version,
                "nodes": Nodes1,
                "links": links1}
        debug_log = "GET Topology Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'topology.Topology.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):
                self.result = self.target.get_topology()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.TOPOLOGY_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_get_topology_StatusCode_NotOK(self):
        Type = "Topology"
        Version = "v02"
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Nodes1 = {'NodeId1': {'type': 'Node', 'version': '1',
                              'node_id': 'NodeId1', 'ports': Port1,
                              'attributes': {}}}
        links1 = {'LinkId1': {'type': 'Link', 'version': '1',
                              'link_id': 'LinkId1', 'src_node': 'NodeId1',
                              'src_port': 'PortId1', 'dst_node': 'NodeId2',
                              'dst_port': 'PortId3', 'attributes': {}}}
        body = {"type": Type,
                "version": Version,
                "nodes": Nodes1,
                "links": links1}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            None)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'topology.Topology.create_from_packed',
                      return_value=Topology.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):

                self.result = self.target.get_topology()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.TOPOLOGY_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_nodes_StatusCode_OK_Create_from_packed_Ok(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        Nodes = {'Node1': [body]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Nodes)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):
                self.result = self.target.get_nodes()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODES_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result['Node1'].packed_object(), body)

    def test_get_nodes_StatusCode_OK_Create_from_packed_NG(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        Nodes = {'Node1': [body]}
        debug_log = "GET Nodes Invalid Response Message" \
                    + " KeyError: 1"
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Nodes)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):
                self.result = self.target.get_nodes()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODES_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_get_nodes_StatusCode_NotOK(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        Nodes = {'Node1': [body]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            Nodes)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object, m_create, m_log_error):
                self.result = self.target.get_nodes()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODES_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_post_link(self):
        link1 = {'type': 'Link', 'version': '1',
                 'link_id': 'LinkId1', 'src_node': 'NodeId1',
                 'src_port': 'PortId1', 'dst_node': 'NodeId2',
                 'dst_port': 'PortId3', 'attributes': {}}

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, link1)
                   ) as m_post_object:

                self.result = self.target.post_link(link1)
                self.assertEqual(m_post_object.call_count, 1)
                m_post_object.assert_called_once_with(NetworkInterface.
                                                      LINKS_PATH,
                                                      link1)
                self.assertNotEqual(self.result, None)

    def test_get_links_StatusCode_OK_Create_from_packed_Ok(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        links = {'Link1': [link]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, links)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      return_value=Link.create_from_packed(link)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):
                self.result = self.target.get_links()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINKS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result['Link1'].packed_object(), link)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_links_StatusCode_OK_Create_from_packed_NG(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        links = {'Link1': [link]}
        debug_log = "GET Links Invalid Response Message" \
                    + " KeyError: 1"
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, links)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_links()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINKS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_links_StatusCode_NotOK(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        links = {'Link1': [link]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            links)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      return_value=Link.create_from_packed(link)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):
                self.result = self.target.get_links()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINKS_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_flow_set_StatusCode_OK_Create_from_packed_Ok(self):
        Type = "FlowSet"
        Version = "v1"
        Priority = {256: ["Id01"]}
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow = {"type": "BasicFlow", "version": "V01",
                "flow_id": "Id01", "owner": "Owner",
                "enabled": True, "priority": 256,
                "status": "none", "attributes": Attributes,
                "matches": Matches, "path": Path,
                "edge_actions": Edge_actions}
        Flows = {"Id01": Flow}
        body = {"type": Type, "version": Version,
                "priority": Priority, "flows": Flows}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.flow.flow_set.'
                      'FlowSet.create_from_packed',
                      return_value=FlowSet.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow_set()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOWS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_flow_set_StatusCode_OK_Create_from_packed_NG(self):
        Type = "FlowSet"
        Version = "v1"
        Priority = {256: ["Id01"]}
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow1 = {"type": "BasicFlow", "version": "V01",
                 "flow_id": "Id01", "owner": "Owner",
                 "enabled": True, "priority": 256,
                 "status": "none", "attributes": Attributes,
                 "matches": Matches, "path": Path,
                 "edge_actions": Edge_actions}
        Flows = {"Id01": Flow1}
        body = {"type": Type, "version": Version,
                "priority": Priority, "flows": Flows}
        debug_log = "GET FlowSet Invalid Response Message" \
                    + " KeyError: 1"
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.flow.flow_set.'
                      'FlowSet.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow_set()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOWS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_flow_set_StatusCode_NotOK(self):
        Type = "FlowSet"
        Version = "v1"
        Priority = {256: ["Id01"]}
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow = {"type": "BasicFlow", "version": "V01",
                "flow_id": "Id01", "owner": "Owner",
                "enabled": True, "priority": 256,
                "status": "none", "attributes": Attributes,
                "matches": Matches, "path": Path,
                "edge_actions": Edge_actions}
        Flows = {"Id01": Flow}
        body = {"type": Type, "version": Version,
                "priority": Priority, "flows": Flows}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.flow.flow_set.'
                      'FlowSet.create_from_packed',
                      return_value=FlowSet.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow_set()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOWS_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_node_StatusCode_OK_Create_from_packed_Ok(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_node("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODE_PATH % "NodeId1")
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_node_StatusCode_OK_Create_from_packed_NG(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        debug_log = "GET Node Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_node("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODE_PATH % "NodeId1")
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_node_StatusCode_NotOK(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.topology.node.'
                      'Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):
                self.result = self.target.get_node("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.NODE_PATH % "NodeId1")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_port_StatusCode_OK_Create_from_packed_Ok(self):
        body = {'type': 'Port', 'version': '1',
                'port_id': 'PortId1', 'node_id': 'NodeId1',
                'out_link': 'LinkId1',
                'in_link': None, 'attributes': {}}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.port.'
                      'Port.create_from_packed',
                      return_value=Port.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_port('NodeId1', 'PortId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PORT_PATH % ("NodeId1",
                                                               'PortId1'))
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_port_StatusCode_OK_Create_from_packed_NG(self):
        body = {'type': 'Port', 'version': '1',
                'port_id': 'PortId1', 'node_id': 'NodeId1',
                'out_link': 'LinkId1',
                'in_link': None, 'attributes': {}}
        debug_log = "GET Port Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.port.'
                      'Port.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_port('NodeId1', 'PortId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PORT_PATH % ("NodeId1",
                                                               'PortId1'))
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_port_StatusCode_NotOK(self):
        body = {'type': 'Port', 'version': '1',
                'port_id': 'PortId1', 'node_id': 'NodeId1',
                'out_link': 'LinkId1',
                'in_link': None, 'attributes': {}}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.topology.port.'
                      'Port.create_from_packed',
                      return_value=Port.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_port('NodeId1', 'PortId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PORT_PATH % ("NodeId1",
                                                               'PortId1'))
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_link_StatusCode_OK_Create_from_packed_Ok(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, link)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      return_value=Link.create_from_packed(link)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_link('LinkId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINK_PATH % ('LinkId1'))
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), link)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_link_StatusCode_OK_Create_from_packed_NG(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        debug_log = "GET Link Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, link)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_link('LinkId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINK_PATH % ('LinkId1'))
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_link_StatusCode_NotOK(self):
        link = {'type': 'Link', 'version': '1',
                'link_id': 'LinkId1', 'src_node': 'NodeId1',
                'src_port': 'PortId1', 'dst_node': 'NodeId2',
                'dst_port': 'PortId3', 'attributes': {}}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            link)),
                patch('org.o3project.odenos.core.component.network.topology.link.'
                      'Link.create_from_packed',
                      return_value=Link.create_from_packed(link)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):
                self.result = self.target.get_link('LinkId1')
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.LINK_PATH % ('LinkId1'))
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_flow_StatusCode_OK_Create_from_packed_Ok(self):
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow = {"type": "BasicFlow", "version": "V01",
                "flow_id": "Id01", "owner": "Owner",
                "enabled": True, "priority": 256,
                "status": "none", "attributes": Attributes,
                "matches": Matches, "path": Path,
                "edge_actions": Edge_actions}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Flow)),
                patch('org.o3project.odenos.core.component.network.flow.basic.'
                      'basic_flow.BasicFlow.create_from_packed',
                      return_value=BasicFlow.create_from_packed(Flow)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow("Id01")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOW_PATH % "Id01")
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), Flow)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_flow_StatusCode_OK_Create_from_packed_NG(self):
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow = {"type": "BasicFlow", "version": "V01",
                "flow_id": "Id01", "owner": "Owner",
                "enabled": True, "priority": 256,
                "status": "none", "attributes": Attributes,
                "matches": Matches, "path": Path,
                "edge_actions": Edge_actions}
        debug_log = "GET Flow Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Flow)),
                patch('org.o3project.odenos.core.component.network.flow.basic.'
                      'basic_flow.BasicFlow.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow("Id01")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOW_PATH % "Id01")
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_flow_StatusCode_NotOK(self):
        Matches = [{"type": "BasicFlowMatch",
                            "in_node": "NODE_ID_1",
                            "in_port": "ANY"}]
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}
        Path = ["LINK_ID1"]
        Flow = {"type": "BasicFlow", "version": "V01",
                "flow_id": "Id01", "owner": "Owner",
                "enabled": True, "priority": 256,
                "status": "none", "attributes": Attributes,
                "matches": Matches, "path": Path,
                "edge_actions": Edge_actions}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            Flow)),
                patch('org.o3project.odenos.core.component.network.flow.basic.'
                      'basic_flow.BasicFlow.create_from_packed',
                      return_value=BasicFlow.create_from_packed(Flow)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_flow("Id01")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.FLOW_PATH % "Id01")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_in_packet_StatusCode_OK_Create_from_packed_Ok(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      return_value=InPacket.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_in_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.INPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_in_packet_StatusCode_OK_Create_from_packed_NG(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        debug_log = "GET InPacket Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_in_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.INPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_in_packet_StatusCode_NotOK(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      return_value=InPacket.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_in_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.INPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_out_packet_StatusCode_OK_Create_from_packed_Ok(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      return_value=OutPacket.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_out_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.OUTPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_out_packet_StatusCode_OK_Create_from_packed_NG(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        debug_log = "GET OutnPacket Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_out_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.OUTPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_out_packet_StatusCode_NotOK(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      return_value=OutPacket.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_out_packet("0123")
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.OUTPACKET_PATH % "0123")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_packets_StatusCode_OK_Create_from_packed_Ok(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_packets()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)
                self.assertEqual(m_log_error.call_count, 0)

    def test_get_packets_StatusCode_OK_Create_from_packed_NG(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}
        debug_log = "GET Packet Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_packets()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

    def test_get_packets_StatusCode_NotOK(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error'
                      )) as (m_get_object,
                             m_create,
                             m_log_error):

                self.result = self.target.get_packets()
                self.assertEqual(m_get_object.call_count, 1)
                self.assertEqual(m_get_object.call_args[0][0],
                                 NetworkInterface.PACKETS_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(self.result, None)
                self.assertEqual(m_log_error.call_count, 0)

    def test_put_node(self):
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1}, {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Node_body)
                   ) as m_put_object:

            self.result = self.target.put_node(Node_body)

            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 NODE_PATH % 'NodeId1',
                                                 Node_body)

    def test_put_node_attributes(self):
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1}, {})
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Node_body)
                   ) as m_put_object:

            self.result = self.target.put_node_attributes(Node_body, attributes)

            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 NODE_PATH % 'NodeId1' + '/attributes',
                                                 attributes)

    def test_put_port(self):
        Port_body = Port('Port', '1', 'PortId1',
                         'NodeId1', 'LinkId1', None, {})

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port_body)
                   ) as m_put_object:

            self.result = self.target.put_port(Port_body)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 PORT_PATH %
                                                 ('NodeId1',
                                                  'PortId1'),
                                                 Port_body)

    def test_put_port_attributes(self):
        Port_body = Port('Port', '1', 'PortId1',
                         'NodeId1', 'LinkId1', None, {})
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port_body)
                   ) as m_put_object:

            self.result = self.target.put_port_attributes(Port_body, attributes)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 PORT_PATH %
                                                 ('NodeId1',
                                                  'PortId1') + '/attributes',
                                                 attributes)

    def test_del_port(self):
        Port_body = Port('Port', '1', 'PortId1',
                         'NodeId1', 'LinkId1', None, {})

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port_body)
                   ) as m_del_object:

            self.result = self.target.del_port('NodeId1', 'PortId1')
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_called_once_with(NetworkInterface.
                                                 PORT_PATH %
                                                 ('NodeId1',
                                                  'PortId1'))

    def test_put_link(self):
        link1_body = Link('Link', '1', 'LinkId1',
                          'NodeId1', 'PortId1',
                          'NodeId2', 'PortId3', {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, link1_body)
                   ) as m_put_object:

            self.result = self.target.put_link(link1_body)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 LINK_PATH % 'LinkId1',
                                                 link1_body)

    def test_put_link_attributes(self):
        link1_body = Link('Link', '1', 'LinkId1',
                          'NodeId1', 'PortId1',
                          'NodeId2', 'PortId3', {})
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, link1_body)
                   ) as m_put_object:

            self.result = self.target.put_link_attributes(link1_body, attributes)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 LINK_PATH % 'LinkId1' + '/attributes',
                                                 attributes)

    def test_put_flow(self):
        flow_body = Flow("BasicFlow", "v01",
                         "FlowId1",
                         "Owner", True,
                         65535, "none", {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, flow_body)
                   ) as m_put_object:

            self.result = self.target.put_flow(flow_body)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 FLOW_PATH % 'FlowId1',
                                                 flow_body)

    def test_put_flow_attributes(self):
        flow_body = Flow("BasicFlow", "v01",
                         "FlowId1",
                         "Owner", True,
                         65535, "none", {})
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, flow_body)
                   ) as m_put_object:

            self.result = self.target.put_flow_attributes(flow_body, attributes)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_put_object.call_count, 1)
            m_put_object.assert_called_once_with(NetworkInterface.
                                                 FLOW_PATH % 'FlowId1' + '/attributes',
                                                 attributes)

    def test_post_in_packet(self):
        InPacket_head = BasicFlowMatch("BasicFlowMatch", "InNodeId1",
                                       "InPortId1")
        InPacket_body = InPacket("PacketId01", "InPacket",
                                 {}, "NodeId1", "PortId1",
                                 InPacket_head, {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         InPacket_body)
                   ) as m_post_object:

            self.result = self.target.post_in_packet(InPacket_body)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_post_object.call_count, 1)
            m_post_object.assert_called_once_with(NetworkInterface.
                                                  INPACKETS_PATH,
                                                  InPacket_body)

    def test_post_out_packet(self):
        InPacket_head = BasicFlowMatch("BasicFlowMatch", "InNodeId1",
                                       "InPortId1")
        Ports = ["PortId1"]
        PortsEx = ["PortId2"]
        OutPacket_body = OutPacket("PacketId01", "OutPacket",
                                   {}, "NodeId1", Ports, PortsEx,
                                   InPacket_head, {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK,
                                         OutPacket_body)
                   ) as m_post_object:

            self.result = self.target.post_out_packet(OutPacket_body)
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_post_object.call_count, 1)
            m_post_object.assert_called_once_with(NetworkInterface.
                                                  OUTPACKETS_PATH,
                                                  OutPacket_body)

    def test_del_node(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object'
                   ) as m_del_object:

            self.result = self.target.del_node("NodeId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_called_once_with(NetworkInterface.
                                                 NODE_PATH % "NodeId1")

    def test_del_link(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object'
                   ) as m_del_object:

            self.result = self.target.del_link("LinkId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_called_once_with(NetworkInterface.
                                                 LINK_PATH % "LinkId1")

    def test_del_flow_NotNone(self):
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Matches = [{"type": "BasicFlowMatch",
                    "in_node": "NODE_ID_1",
                    "in_port": "ANY"}]
        Path = ["LINK_ID1"]
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}

        Flow1 = BasicFlow("BasicFlow", "V01",
                          "FlowId1", "Owner",
                          True, 256, "none",
                          Attributes, Matches,
                          Path, Edge_actions)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=Flow1),
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._del_object_to_remote_object'
                      )) as (m_get_flow,
                             m_del_object):

                self.result = self.target.del_flow("FlowId1")
                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_called_once_with("FlowId1")
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     FLOW_PATH % "FlowId1",
                                                     body=Flow1)
                self.assertNotEqual(self.result, None)

    def test_del_flow_None(self):

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=None),
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._del_object_to_remote_object'
                      )) as (get_flow,
                             m_del_object):

            self.result = self.target.del_flow("FlowId1")
            self.assertEqual(get_flow.call_count, 1)
            self.assertEqual(m_del_object.call_count, 0)
            self.assertNotEqual(self.result, None)

    def test_del_in_packet(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   ) as m_del_object:

            self.result = self.target.del_in_packet("PacketId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_called_once_with(NetworkInterface.
                                                 INPACKET_PATH %
                                                 "PacketId1")

    def test_del_out_packet(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   ) as m_del_object:

            self.result = self.target.del_out_packet("PacketId1")
            self.assertNotEqual(self.result, None)
            self.assertEqual(m_del_object.call_count, 1)
            m_del_object.assert_called_once_with(NetworkInterface.
                                                 OUTPACKET_PATH %
                                                 "PacketId1")

    def test_put_attribute_of_node(self):
        NodeAttributes1 = {"oper_status": "UP", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        NodeAttributes2 = {"oper_status": "DOWN", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1},
                         NodeAttributes1)
        Node1 = {}
        Node1['NodeId1'] = Node_body

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_nodes',
                      return_value=Node1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_node')) as (m_get_nodes,
                                                        m_put_node):

                self.result = self.target.put_attribute_of_node(NodeAttributes2
                                                                )
                self.assertEqual(m_get_nodes.call_count, 1)
                self.assertEqual(m_put_node.call_count, 1)
                m_put_node.assert_called_once_with(Node_body)
                self.assertEqual(Node_body._body["attributes"],
                                 NodeAttributes2)
                self.assertNotEqual(self.result, None)

    def test_put_attribute_of_node_attributes_None(self):
        NodeAttributes1 = {"oper_status": "UP", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        NodeAttributes2 = {}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1},
                         NodeAttributes1)
        Node1 = {}
        Node1['NodeId1'] = Node_body

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_nodes',
                      return_value=Node1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_node')) as (m_get_nodes,
                                                        m_put_node):

                self.result = self.target.put_attribute_of_node(NodeAttributes2
                                                                )
                self.assertEqual(m_get_nodes.call_count, 0)
                self.assertEqual(m_put_node.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test_put_attribute_of_node_get_nodes_None(self):
        NodeAttributes1 = {"oper_status": "DOWN", "physical_id": "DPID",
                           "vendor": "Vendor1"}

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_nodes',
                      return_value=None),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_node')) as (m_get_nodes,
                                                        m_put_node):

                self.result = self.target.put_attribute_of_node(NodeAttributes1
                                                                )
                self.assertEqual(m_get_nodes.call_count, 1)
                self.assertEqual(m_put_node.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test_put_attribute_of_node_get_nodes_Len0(self):
        NodeAttributes1 = {"oper_status": "DOWN", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        Node1 = {}
        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_nodes',
                      return_value=Node1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_node')) as (m_get_nodes,
                                                        m_put_node):

                self.result = self.target.put_attribute_of_node(NodeAttributes1
                                                                )
                self.assertEqual(m_get_nodes.call_count, 1)
                self.assertEqual(m_put_node.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test_put_attribute_of_node_Update_False(self):
        NodeAttributes1 = {"oper_status": "UP", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        NodeAttributes2 = {"oper_status": "UP", "physical_id": "DPID",
                           "vendor": "Vendor1"}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1',
                         {'PortId1': Port1}, NodeAttributes1)
        Node1 = {}
        Node1['NodeId1'] = Node_body

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_nodes',
                      return_value=Node1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_node')) as (m_get_nodes,
                                                        m_put_node):

                self.result = self.target.put_attribute_of_node(NodeAttributes2
                                                                )
                self.assertEqual(m_get_nodes.call_count, 1)
                self.assertEqual(m_put_node.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test_delete_all_flow(self):
        Edge_actions = {"NODE_ID_1": [{"type": "FlowActionOutput",
                        "output": "ANY"}]}
        Matches = [{"type": "BasicFlowMatch",
                    "in_node": "NODE_ID_1",
                    "in_port": "ANY"}]
        Path = ["LINK_ID1"]
        Attributes = {"req_bandwidth": 10, "bandwidth": 11,
                      "req_latency": 20, "latency": 21}

        Flow1 = BasicFlow("BasicFlow", "V01",
                          "FlowId1", "Owner",
                          True, 256, "none",
                          Attributes, Matches,
                          Path, Edge_actions)
        Flow2 = BasicFlow("BasicFlow", "V02",
                          "FlowId2", "Owner",
                          True, 256, "none",
                          Attributes, Matches,
                          Path, Edge_actions)
        FlowsBody = {"FlowId1": Flow1, "FlowId2": Flow2}
        Flows1 = FlowSet("FlowSet", "v01", {256: ["Id01"]}, FlowsBody)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow_set',
                      return_value=Flows1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_flow')) as (m_get_flow_set,
                                                        m_del_flow):

                self.result = self.target.delete_all_flow()
                self.assertEqual(m_get_flow_set.call_count, 1)
                self.assertEqual(m_del_flow.call_count, 2)
                self.assertNotEqual(self.result[0], None)
                self.assertEqual(len(self.result), 2)

    def test_delete_all_flow_flow_set_None(self):
        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow_set',
                      return_value=None),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_flow')) as (m_get_flow_set,
                                                        m_del_flow):

                self.result = self.target.delete_all_flow()

                self.assertEqual(m_get_flow_set.call_count, 1)
                self.assertEqual(m_del_flow.call_count, 0)
                self.assertNotEqual(self.result[0], None)
                self.assertEqual(len(self.result), 1)

    def test_delete_topology(self):
        port1 = Port('Port', '1', 'PortId1',
                     'NodeId1', 'LinkId1', None, {})
        port2 = Port('Port', '1', 'PortId2',
                     'NodeId1', None, 'LinkId2', {})
        port3 = Port('Port', '1', 'PortId3',
                     'NodeId2', None, 'LinkId1', {})
        port4 = Port('Port', '1', 'PortId4',
                     'NodeId2', 'LinkId2', None, {})
        node1 = Node('Node', '1', 'NodeId1',
                     {'PortId1': port1, 'PortId2': port2}, {})
        node2 = Node('Node', '1', 'NodeId2',
                     {'PortId3': port3, 'PortId4': port4}, {})
        link1 = Link('Link', '1', 'LinkId1',
                     'NodeId1', 'PortId1', 'NodeId2', 'PortId3', {})
        link2 = Link('Link', '1', 'LinkId2',
                     'NodeId2', 'PortId4', 'NodeId1', 'PortId2', {})
        nodes = {'NodeId1': node1, 'NodeId2': node2}
        links = {'LinkId1': link1, 'LinkId2': link2}
        Topology_body = Topology('Topology', '1', nodes, links)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_topology',
                      return_value=Topology_body),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_link'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_port'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_node')) as (m_get_topology,
                                                        m_del_link,
                                                        m_del_port,
                                                        m_del_node):

                self.result = self.target.delete_topology()
                self.assertEqual(m_get_topology.call_count, 1)
                self.assertEqual(m_del_link.call_count, 2)
                self.assertEqual(m_del_port.call_count, 4)
                self.assertEqual(m_del_node.call_count, 2)
                m_del_link.assert_any_call("LinkId1")
                m_del_link.assert_any_call("LinkId2")
                m_del_node.assert_any_call("NodeId1")
                m_del_node.assert_any_call("NodeId2")
                m_del_port.assert_any_call("NodeId1", "PortId1")
                m_del_port.assert_any_call("NodeId1", "PortId2")
                m_del_port.assert_any_call("NodeId2", "PortId3")
                m_del_port.assert_any_call("NodeId2", "PortId3")
                self.assertNotEqual(self.result, None)
                self.assertEqual(len(self.result), 8)

    def test_delete_topology_Topology_None(self):
        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_topology',
                      return_value=None),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_link'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_port'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_node')) as (m_get_topology,
                                                        m_del_link,
                                                        m_del_port,
                                                        m_del_node):

                self.result = self.target.delete_topology()
                self.assertEqual(m_get_topology.call_count, 1)
                self.assertEqual(m_del_link.call_count, 0)
                self.assertEqual(m_del_port.call_count, 0)
                self.assertEqual(m_del_node.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(len(self.result), 1)

    def test_delete_topology_List_Len0(self):
        Topology_body = Topology('Topology', '1', {}, {})

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_topology',
                      return_value=Topology_body),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_link'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_port'),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_node')) as (m_get_topology,
                                                        m_del_link,
                                                        m_del_port,
                                                        m_del_node):

                self.result = self.target.delete_topology()
                self.assertEqual(m_get_topology.call_count, 1)
                self.assertEqual(m_del_link.call_count, 0)
                self.assertEqual(m_del_port.call_count, 0)
                self.assertEqual(m_del_node.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(len(self.result), 1)

    def test_put_topology(self):
        Type = "Topology"
        Version = "v02"
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Nodes1 = {'NodeId1': {'type': 'Node', 'version': '1',
                              'node_id': 'NodeId1', 'ports': Port1,
                              'attributes': {}}}
        links1 = {'LinkId1': {'type': 'Link', 'version': '1',
                              'link_id': 'LinkId1', 'src_node': 'NodeId1',
                              'src_port': 'PortId1', 'dst_node': 'NodeId2',
                              'dst_port': 'PortId3', 'attributes': {}}}

        body = {"type": Type,
                "version": Version,
                "nodes": Nodes1,
                "links": links1}

        with patch("org.o3project.odenos.core.util.remote_object_interface."
                   "RemoteObjectInterface._put_object_to_remote_object",
                   return_value=Response(Response.StatusCode.OK, body)
                   ) as m_put_object:

                self.result = self.target.put_topology(body)
                self.assertNotEqual(self.result, None)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_called_once_with(NetworkInterface.
                                                     TOPOLOGY_PATH,
                                                     body)

    def test_post_node(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, body)
                   ) as m_post_object:

                self.result = self.target.post_node(body)
                self.assertNotEqual(self.result, None)
                self.assertEqual(m_post_object.call_count, 1)
                m_post_object.assert_called_once_with(NetworkInterface.
                                                      NODES_PATH,
                                                      body)

    def test_get_physical_node_StatusCode_OK_Create_from_packed_Ok(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'node.Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_node("PhysicalId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "PhysicalId1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)

    def test_get_physical_node_StatusCode_NotOK(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'node.Node.create_from_packed',
                      return_value=Node.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_node("PhysicalId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "PhysicalId1")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_physical_node_StatusCode_OK_Create_from_packed_NG(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        body = {'type': 'Node', 'version': '1',
                'node_id': 'NodeId1', 'ports': Port1,
                'attributes': {}}

        debug_log = "GET PhysicalNode Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'node.Node.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_node("PhysicalId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "PhysicalId1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_put_physical_node_InNode(self):
        NodeAttributes1 = {"oper_status": "UP", "physical_id": "Physicalid1",
                           "vendor": "Vendor1"}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1},
                         NodeAttributes1)

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Node_body)
                   ) as m_put_object:

                self.result = self.target.put_physical_node(Node_body)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "Physicalid1",
                                                     Node_body)
                self.assertNotEqual(self.result, None)

    def test_put_physical_node_attributes_InNode(self):
        NodeAttributes1 = {"oper_status": "UP", "physical_id": "Physicalid1",
                           "vendor": "Vendor1"}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        Node_body = Node('Node', '1', 'NodeId1', {'PortId1': Port1},
                         NodeAttributes1)
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Node_body)
                   ) as m_put_object:

                self.result = self.target.put_physical_node_attributes(Node_body, attributes)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "Physicalid1" + '/attributes',
                                                     attributes)
                self.assertNotEqual(self.result, None)

    def test_del_physical_node_Inphysical_id(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_physical_node("PhysicalId1")
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_NODES_PATH %
                                                     "PhysicalId1")
                self.assertNotEqual(self.result, None)

    def test_post_port_InPort(self):
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1', 'LinkId1', None, {})
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port1)
                   ) as m_post_object:

                self.result = self.target.post_port(Port1)
                self.assertEqual(m_post_object.call_count, 1)
                m_post_object.assert_called_once_with(NetworkInterface.
                                                      PORTS_PATH %
                                                      'NodeId1',
                                                      Port1)
                self.assertNotEqual(self.result, None)

    def test_get_ports_InNode_id_StatusCode_OK_Create_from_packed_Ok(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Port1)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      return_value=Port.create_from_packed(Port2)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_ports("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PORTS_PATH %
                                                     "NodeId1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(Port2)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(len(self.result), 1)
                self.assertEqual(self.result['PortId1'].packed_object(),
                                 Port2)

    def test_get_ports_InNode_id_StatusCode_NotOK(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            Port1)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      return_value=Port.create_from_packed(Port2)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_ports("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PORTS_PATH %
                                                     "NodeId1")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_ports_InNode_id_StatusCode_OK_Create_from_packed_NG(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']
        debug_log = "GET Ports Invalid Response Message" \
                    + " KeyError: 1"
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Port1)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_ports("NodeId1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PORTS_PATH %
                                                     "NodeId1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(Port2)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_get_physical_port_INphysicalId_Status_Create_from_packedOk(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Port2)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      return_value=Port.create_from_packed(Port2)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_port("Physicalid1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "Physicalid1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(Port2)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(),
                                 Port2)

    def test_get_physical_port_INphysical_id_StatusCode_NotOK(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            Port2)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      return_value=Port.create_from_packed(Port2)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_port("Physicalid1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "Physicalid1")
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_physical_port_INphysical_StatusOK_Create_from_packed_NG(self):
        Port1 = {'PortId1': {'type': 'Port', 'version': '1',
                             'port_id': 'PortId1', 'node_id': 'NodeId1',
                             'out_link': 'LinkId1',
                             'in_link': None, 'attributes': {}}}
        Port2 = Port1['PortId1']
        debug_log = "GET PhysicalPort Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, Port2)),
                patch('org.o3project.odenos.core.component.network.topology.'
                      'port.Port.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_physical_port("Physicalid1")
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "Physicalid1")
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(Port2)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_put_physical_port_InPort(self):
        Portattributes = {"oper_status": "UP", "max_bandwidth": 128,
                          "unreserved_bandwidth": 129,
                          "physical_id": "PhysicalId1", "vendor": "Vendor1",
                          "is_boundary": True}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1',
                     'LinkId1', None, Portattributes)

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port1)
                   ) as m_put_object:

                self.result = self.target.put_physical_port(Port1)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "PhysicalId1",
                                                     Port1)
                self.assertNotEqual(self.result, None)

    def test_put_physical_port_attributes_InPort(self):
        Portattributes = {"oper_status": "UP", "max_bandwidth": 128,
                          "unreserved_bandwidth": 129,
                          "physical_id": "PhysicalId1", "vendor": "Vendor1",
                          "is_boundary": True}
        Port1 = Port('Port', '1', 'PortId1', 'NodeId1',
                     'LinkId1', None, Portattributes)
        attributes = {'attr123': 'val123'}
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._put_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Port1)
                   ) as m_put_object:

                self.result = self.target.put_physical_port_attributes(Port1, attributes)
                self.assertEqual(m_put_object.call_count, 1)
                m_put_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "PhysicalId1" + '/attributes',
                                                     attributes)
                self.assertNotEqual(self.result, None)

    def test_del_physical_port_InPhysical_id(self):

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_physical_port("PhysicalId1")
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     PHYSICAL_PORTS_PATH %
                                                     "PhysicalId1")
                self.assertNotEqual(self.result, None)

    def test_post_flow_InFlow(self):
        Flow1 = "Flow1"

        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._post_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, Flow1)
                   ) as m_post_object:

                self.result = self.target.post_flow(Flow1)
                self.assertEqual(m_post_object.call_count, 1)
                m_post_object.assert_called_once_with(NetworkInterface.
                                                      FLOWS_PATH,
                                                      Flow1)
                self.assertNotEqual(self.result, None)

    def test_get_in_packets_StatusCode_OK_Create_from_packed_OK(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_in_packets_StatusCode_NotOK(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}
        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_in_packets_StatusCode_OK_Create_from_packed_NG(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}
        debug_log = "GET InPackets Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_del_in_packets(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_in_packets()
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_PATH)
                self.assertNotEqual(self.result, None)

    def test_get_in_packet_head_StatusCode_OK_Create_from_packed_OK(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      return_value=InPacket.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_in_packet_head_StatusCode_NotOK(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      return_value=InPacket.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_in_packet_head_StatusCode_OK_Create_from_packed_NG(self):
        body = {"packet_id": "0123",
                "type": "InPacket",
                "attributes": "0789",
                "node": "9870",
                "port": "6540",
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}
        debug_log = "GET InPacketHead Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'in_packet.InPacket.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_in_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_del_in_packet_head(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_in_packet_head()
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     INPACKETS_HEAD_PATH)
                self.assertNotEqual(self.result, None)

    def test_get_out_packets_StatusCode_OK_Create_from_packed_OK(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_out_packets_StatusCode_NotOK(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      return_value=PacketStatus.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_out_packets_StatusCode_OK_Create_from_packed_NG(self):
        body = {"type": "PacketStatus",
                "in_packet_count": 456,
                "in_packet_bytes": 789,
                "in_packet_queue_count": 987,
                "in_packets": ["6540"],
                "out_packet_count": 321,
                "out_packet_bytes": 147,
                "out_packet_queue_count": 258,
                "out_packets": ["0258"]}
        debug_log = "GET OutnPackets Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'packet_status.PacketStatus.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packets()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)
                self.assertEqual(self.result, None)

    def test_del_out_packets(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_out_packets()
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_PATH)
                self.assertNotEqual(self.result, None)

    def test_get_out_packet_head_StatusCode_OK_Create_from_packed_OK(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      return_value=OutPacket.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertNotEqual(self.result, None)
                self.assertEqual(self.result.packed_object(), body)

    def test_get_out_packet_head_StatusCode_NotOK(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.NOT_FOUND,
                                            body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      return_value=OutPacket.create_from_packed(body)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 0)
                self.assertEqual(m_log_error.call_count, 0)
                self.assertEqual(self.result, None)

    def test_get_out_packet_head_StatusCode_OK_Create_from_packed_NG(self):
        body = {"packet_id": "0123",
                "type": "OutPacket",
                "attributes": {"attributes": "attributes_value"},
                "node": "9870",
                "ports": "6540",
                "ports-except": None,
                "header": {"type": "BasicFlowMatch",
                           "in_port": "123456",
                           "in_node": "123456789"},
                "data": "0147"}

        debug_log = "GET OutPacketHead Invalid Response Message" \
                    + " KeyError: 1"

        with nested(
                patch('org.o3project.odenos.core.util.remote_object_interface.'
                      'RemoteObjectInterface._get_object_to_remote_object',
                      return_value=Response(Response.StatusCode.OK, body)),
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet.OutPacket.create_from_packed',
                      side_effect=KeyError(1)),
                patch('logging.error')) as (m_get_object,
                                            m_create,
                                            m_log_error):

                self.result = self.target.get_out_packet_head()
                self.assertEqual(m_get_object.call_count, 1)
                m_get_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_HEAD_PATH)
                self.assertEqual(m_create.call_count, 1)
                m_create.assert_called_once_with(body)
                self.assertEqual(m_log_error.call_count, 1)
                m_log_error.assert_called_once_with(debug_log)

                self.assertEqual(self.result, None)

    def test_del_out_packet_head(self):
        with patch('org.o3project.odenos.core.util.remote_object_interface.'
                   'RemoteObjectInterface._del_object_to_remote_object',
                   return_value=Response(Response.StatusCode.OK, None)
                   ) as m_del_object:

                self.result = self.target.del_out_packet_head()
                self.assertEqual(m_del_object.call_count, 1)
                m_del_object.assert_called_once_with(NetworkInterface.
                                                     OUTPACKETS_HEAD_PATH)
                self.assertNotEqual(self.result, None)

if __name__ == '__main__':
    unittest.main()
