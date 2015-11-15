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

import copy
import signal
import sys
import threading
import time
from functools import partial

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match import OFPFlowMatch
from org.o3project.odenos.core.component.network.flow.basic.basic_flow import BasicFlow
from org.o3project.odenos.core.component.network.flow.basic.basic_flow_match import BasicFlowMatch
from org.o3project.odenos.core.component.network.flow.basic.flow_action_output import FlowActionOutput
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.core.component.network.flow.flow_set import FlowSet
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow import OFPFlow
from org.o3project.odenos.core.component.network.packet.in_packet import InPacket
from org.o3project.odenos.core.component.network.packet.ofp_in_packet import OFPInPacket
from org.o3project.odenos.core.component.network.packet.ofp_out_packet import OFPOutPacket
from org.o3project.odenos.core.component.network.packet.out_packet import OutPacket
from org.o3project.odenos.core.component.network.packet.packet import Packet
from org.o3project.odenos.core.component.network.packet.packet_status import PacketStatus
from org.o3project.odenos.core.component.network.topology.link import Link
from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.port import Port
from org.o3project.odenos.core.component.network.topology.topology import Topology
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.util.remote_object_interface import RemoteObjectInterface
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.remoteobject.manager.system.component_connection import ComponentConnection
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network import ComponentConnectionLogicAndNetwork
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.transport.message_dispatcher import MessageDispatcher


def signal_handler(num, stack, obj):
    print 'Received signal %d' % num
    del obj
    sys.exit()


class ServerThread(threading.Thread):
    def __init__(self, dispatcher):
        threading.Thread.__init__(self)
        self.disp = dispatcher

    def run(self):
        self.disp.start()


class OdenosConfigurator(object):
    CM1 = "romgr1"
    CM2 = "romgr2"
    CM3 = "romgr3"
    DEF_ATTR = {"oper_status": "UP"}
    DEF_VENDOR = "VENDOR1"

    def __init__(self, dispatcher=None):
      if dispatcher is None:
        bound_func = partial(signal_handler, obj=self)
        signal.signal(signal.SIGINT, bound_func)
        signal.signal(signal.SIGTERM, bound_func)

      self.disp = dispatcher
      if dispatcher is None:
        self.disp = MessageDispatcher()
        self.thread = ServerThread(self.disp)
        self.thread.start()
        
      self.sysmgr = SystemManagerInterface(self.disp)
      self.stations = {}
      self.packet_id = 0;

    def __del__(self):
        self.thread.join()
        self.disp.stop()

    def create_component(self, type, name, cm_id):
        obj = ObjectProperty(type, name)
        obj.set_property("version", "1")
        obj.set_property(ObjectProperty.CM_ID, cm_id)
        ret = self.sysmgr.put_components(obj).status_code
        if ret != 201:
            print "failed to create(ret): " + type + " " + name + " @ " + cm_id
        return RemoteObjectInterface(self.disp, name)

    def create_aggregator(self, name, cm_id=CM1):
        return self.create_component("Aggregator", name, cm_id)

    def create_federator(self, name, cm_id=CM1):
        return self.create_component("Federator", name, cm_id)

    def get_fed_boundaries(self, federator):
        resp = federator._get_object_to_remote_object("settings/boundaries")
        if resp.is_error(Request.Method.GET):
            return None
        return resp.body

    def get_ll_boundaries(self, linklayerizer):
        return self.get_fed_boundaries(linklayerizer)

    def set_fed_boundaries(self, federator, boundaries):
        n = 0
        for ports in boundaries:
            net1 = ports[0][0]
            net2 = ports[1][0]
            phy_port1 = ports[0][1]
            phy_port2 = ports[1][1]
            port1 = net1.get_physical_port(phy_port1)
            port2 = net2.get_physical_port(phy_port2)
            while not port1:
                print "cannot get port by %s from %s" % (phy_port1, net1.network_id)
                port1 = net1.get_physical_port(phy_port1)
                time.sleep(2)
            while not port2:
                print "cannot get port by %s from %s" % (phy_port2, net2.network_id)
                port2 = net2.get_physical_port(phy_port2)
                time.sleep(2)

            bond_id = "bond_%s" % str(n)
            bond = {"id": bond_id, "type": "Federator",
                     "network1": net1.network_id,
                     "node1": port1.node_id, "port1": port1.port_id,
                     "network2": net2.network_id,
                     "node2": port2.node_id, "port2": port2.port_id
                 }
            n = n + 1
            federator._put_object_to_remote_object("settings/boundaries/%s" % bond_id, bond)

    def set_ll_boundaries(self, linklayerizer, boundaries):
        n = 0
        for ports in boundaries:
            net1 = ports[0][0]
            net2 = ports[1][0]
            phy_port1 = ports[0][1]
            phy_port2 = ports[1][1]
            port1 = net1.get_physical_port(phy_port1)
            port2 = net2.get_physical_port(phy_port2)
            while not port1:
                print "cannot get port by %s from %s" % (phy_port1, net1.network_id)
                port1 = net1.get_physical_port(phy_port1)
                time.sleep(2)
            while not port2:
                print "cannot get port by %s from %s" % (phy_port2, net2.network_id)
                port2 = net2.get_physical_port(phy_port2)
                time.sleep(2)

            bond_id = "bond_" + str(n) + "_low_" + port1.port_id + "_up_" +  port2.port_id
            bond = {"id": bond_id, "type": "LinkLayerizer",
                     "lower_nw": net1.network_id,
                     "lower_nw_node": port1.node_id, "lower_nw_port": port1.port_id,
                     "upper_nw": net2.network_id,
                     "upper_nw_node": port2.node_id, "upper_nw_port": port2.port_id
                 }
            n = n + 1
            linklayerizer._put_object_to_remote_object("settings/boundaries/%s" % bond_id, bond)

    def create_l2switch(self, name, cm_id=CM1):
        return self.create_component("LearningSwitch", name, cm_id)

    def create_linklayerizer(self, name, cm_id=CM1):
        return self.create_component("LinkLayerizer", name, cm_id)

    def create_dummydriver(self, name, cm_id=CM1):
        return self.create_component("DummyDriver", name, cm_id)

    def create_ofdriver(self, name, cm_id=CM3):
        return self.create_component("OpenFlowDriver", name, cm_id)

    def create_network(self, name, cm_id=CM1):
        self.create_component("Network", name, cm_id)
        return NetworkInterface(self.disp, name)

    def get_network(self, name):
        return NetworkInterface(self.disp, name)

    def create_node(self, network, node_id, attr=DEF_ATTR):
        attr = copy.deepcopy(attr)
        attr.update({"physical_id": node_id})
        if not attr.has_key("vendor"):
          attr.update({"vendor": self.DEF_VENDOR})
        return network.put_node(Node("Node", "0", node_id, {}, attr))

    def create_port(self, network, node_id, port_id, attr=DEF_ATTR):
        attr = copy.deepcopy(attr)
        attr.update({"physical_id": "%s@%s" % (port_id, node_id)})
        attr.update({"max_bandwidth": "10000000"})
        attr.update({"unreserved_bandwidth": "10000000"})
        if not attr.has_key("vendor"):
          attr.update({"vendor": self.DEF_VENDOR})
        return network.put_port(Port("Port", "0", port_id, node_id, "", "", attr))

    def create_link(self, network, link_id, snode, sport, dnode, dport, attr=DEF_ATTR):
        attr = copy.deepcopy(attr)
        attr.update({"max_bandwidth": "10000000"})
        attr.update({"unreserved_bandwidth": "10000000"})
        return network.put_link(Link("Link", "0", link_id, snode, sport, dnode, dport, attr))

    def create_simple_basicFlow(self, network, flow_id,
                                in_node, in_port, path, out_node, out_port, attr={}):
        matches = []
        matches.append(BasicFlowMatch("BasicFlowMatch", in_node, in_port))
        edge_actions = {}
        edge_actions[out_node] = [FlowActionOutput("FlowActionOutput", out_port)]
        attributes = attr
        flow = BasicFlow("BasicFlow", "0", flow_id, "simple_basicFlow",
                True, None, None, attributes, matches, path, edge_actions)
        return network.put_flow(flow)

    def create_ofp_flow(self, network, flow_id, matches, path, edge_actions):
        attributes = {}
        flow = OFPFlow("OFPFlow", "0", flow_id, "flowSetter",
                      True, None, None, attributes, matches,
                      None, None, path, edge_actions)
        return network.put_flow(flow)

    def add_ofp_inpacket(self, network, node_id, port_id, header, data, attr={}):
        self.packet_id = self.packet_id + 1
        attr = copy.deepcopy(attr)
        network.post_in_packet(
            OFPInPacket(str(self.packet_id),
                        "OFPInPacket", attr, node_id, port_id, header, data))

    def add_station(self, network, sid, node_id, port_id, mac):
        self.stations.update(
            {sid:{"network": network, "node_id": node_id, "port_id": port_id, "mac": mac}})

    def ping(self, src_sid, dst_sid):
        src = self.stations[src_sid]
        dst = self.stations[dst_sid]
        data = "deadbeef"
        header = OFPFlowMatch("OFPFlowMatch", src["node_id"], src["port_id"])
        header.eth_src = src["mac"]
        header.eth_dst = dst["mac"]
        self.add_ofp_inpacket(src["network"], src["node_id"], src["port_id"], header, data)

    def create_slicer(self, name, cm_id=CM1):
        return self.create_component("Slicer", name, cm_id)

    def set_slice_condition(self, slicer, priority, cond_id, conn_id, match):
        path = "settings/slice_condition_table/%s/conditions/%s" % (priority, cond_id)
        body = {"id": cond_id, "type":"BasicSliceCondition", "connection": conn_id}
        body.update(match)
        slicer._put_object_to_remote_object(path, body)

    def create_ofpslicer(self, name, cm_id=CM1):
        return self.create_component("OpenFlowSlicer", name, cm_id)

    def set_ofpslice_condition(self, slicer, priority, cond_id, conn_id, match):
        path = "settings/slice_condition_table/%s/conditions/%s" % (priority, cond_id)
        body = {"id": cond_id, "type":"OpenFlowSliceCondition", "connection": conn_id}
        body.update(match)
        slicer._put_object_to_remote_object(path, body)

    def connect(self, logic, network, type):
        conn_id = logic.object_id + "-" + network.object_id
        conn = ComponentConnectionLogicAndNetwork(
            conn_id, type, ComponentConnection.State.INITIALIZING,
            logic.object_id, network.object_id)
        if self.sysmgr.put_connection(conn).status_code != 201:
            print "failed to connect(ret): " + conn_id + " as " + type
        return conn_id
