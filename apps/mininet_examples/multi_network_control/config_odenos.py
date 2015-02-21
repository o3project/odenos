#!/usr/bin/env python

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

import signal
import sys
import threading
import time

from org.o3project.odenos.remoteobject.transport.message_dispatcher import MessageDispatcher
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.manager.system.component_connection import ComponentConnection
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network import ComponentConnectionLogicAndNetwork

from org.o3project.odenos.core.component.network.flow.basic.flow_action_output import FlowActionOutput
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow import OFPFlow
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match import OFPFlowMatch
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.util.remote_object_interface import RemoteObjectInterface
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface


def signal_handler(num, stack):
    print 'Received signal %d' % num
    dispatcher.close()
    dispatcher.stop()
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
    CM4 = "romgr4"
    CM5 = "romgr5"
    CM6 = "romgr6"

    def __init__(self, dispatcher):
        self.disp = dispatcher
        self.sysmgr = SystemManagerInterface(self.disp)

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

    def create_l2switch(self, name, cm_id=CM1):
        return self.create_component("LearningSwitch", name, cm_id)

    def create_linklayerizer(self, name, cm_id=CM1):
        return self.create_component("LinkLayerizer", name, cm_id)

    def create_ofdriver(self, name, cm_id=CM3):
        return self.create_component("OpenFlowDriver", name, cm_id)

    def create_network(self, name, cm_id=CM1):
        self.create_component("Network", name, cm_id)
        return NetworkInterface(self.disp, name)

    def create_slicer(self, name, cm_id=CM1):
        return self.create_component("Slicer", name, cm_id)

    def connect(self, logic, network, type):
        conn_id = logic.object_id + "-" + network.object_id
        conn = ComponentConnectionLogicAndNetwork(
            conn_id, type, ComponentConnection.State.INITIALIZING,
            logic.object_id, network.object_id)
        if self.sysmgr.put_connection(conn).status_code != 201:
            print "failed to connect(ret): " + conn_id + " as " + type

    def set_flow(self, network, flow_id, inport, outport, inners, vid=0):
        inport = network.get_physical_port(inport)
        outport = network.get_physical_port(outport)

        match = OFPFlowMatch("OFPFlowMatch", inport.node_id, inport.port_id)
        if vid:
            match.vlan_vid = vid

        path = []
        for inner in inners:
            inner_inport = network.get_physical_port(inner[0])
            inner_outport = network.get_physical_port(inner[1])
            link_id = ""
            links = network.get_links()
            for id_, link in links.iteritems():
                if link.src_node == inner_inport.node_id and \
                   link.src_port == inner_inport.port_id and \
                   link.dst_node == inner_outport.node_id and \
                   link.dst_port == inner_outport.port_id:
                    print "*** found: " + id_
                    path.append(id_)
                
        actions = {outport.node_id: [FlowActionOutput("FlowActionOutput", outport.port_id)]}
        flow = OFPFlow("OFPFlow", "1", flow_id, "demo", True, "65535", "none",
                       {}, [match], 0, 0, path, actions)
        network.put_flow(flow)

    def set_lly_boundaries(self, target, boundaries):
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
                print "Please start mininet"
                port1 = net1.get_physical_port(phy_port1)
                time.sleep(2)
            while not port2:
                print "cannot get port by %s from %s" % (phy_port2, net2.network_id)
                print "Please start mininet"
                port2 = net2.get_physical_port(phy_port2)
                time.sleep(2)

            bond_id = "bond_%s" % str(n)
            bond = {"id": bond_id, "type": "LinkLayerizer",
                    "upper_nw": net1.network_id,
                    "upper_nw_node": port1.node_id, "upper_nw_port": port1.port_id,
                    "lower_nw": net2.network_id,
                    "lower_nw_node": port2.node_id, "lower_nw_port": port2.port_id
                 }
            n = n + 1
            target._put_object_to_remote_object("settings/boundaries/%s" % bond_id, bond)

    def set_fed_boundaries(self, target, boundaries):
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
                print "Please start mininet"
                port1 = net1.get_physical_port(phy_port1)
                time.sleep(2)
            while not port2:
                print "cannot get port by %s from %s" % (phy_port2, net2.network_id)
                print "Please start mininet"
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
            target._put_object_to_remote_object("settings/boundaries/%s" % bond_id, bond)


if __name__ == "__main__":
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    dispatcher = MessageDispatcher()
    thread = ServerThread(dispatcher)
    thread.start()

    time.sleep(1)

    oc = OdenosConfigurator(dispatcher)

    drv1 = oc.create_ofdriver("driver_dc1", oc.CM3)
    drv2 = oc.create_ofdriver("driver_dc2", oc.CM4)
    drv3 = oc.create_ofdriver("driver_dc3", oc.CM5)
    drv4 = oc.create_ofdriver("driver_wan", oc.CM6)
    net1 = oc.create_network("network1")
    net2 = oc.create_network("network2")
    net3 = oc.create_network("network3")
    net4 = oc.create_network("network4")
    net5 = oc.create_network("network5")
    net6 = oc.create_network("network6")
    net7 = oc.create_network("network7")
    agg1 = oc.create_aggregator("aggregator1")
    fed1 = oc.create_federator("federator1")
    lly1 = oc.create_linklayerizer("linklayerizer1")
    lsw1 = oc.create_l2switch("l2sw1")

    oc.connect(lsw1, net7, "original")
    oc.connect(agg1, net7, "aggregated")
    oc.connect(agg1, net6, "original")
    oc.connect(lly1, net6, "layerized")
    oc.connect(lly1, net5, "upper")
    oc.connect(lly1, net4, "lower")
    oc.connect(fed1, net5, "federated")
    oc.connect(fed1, net3, "original")
    oc.connect(fed1, net2, "original")
    oc.connect(fed1, net1, "original")

    oc.connect(drv1, net1, "original")
    oc.connect(drv2, net2, "original")
    oc.connect(drv3, net3, "original")
    oc.connect(drv4, net4, "original")

    time.sleep(5)

    # set boundaries
    boundaries = [
        [[net5, "4@0x3"], [net4, "3@0xe"]], [[net5, "3@0x4"], [net4, "3@0xd"]],
        [[net5, "3@0x6"], [net4, "3@0x12"]], [[net5, "3@0x7"], [net4, "3@0x11"]],
        [[net5, "3@0x9"], [net4, "3@0x10"]], [[net5, "3@0xa"], [net4, "3@0xf"]]]
    oc.set_lly_boundaries(lly1, boundaries)

    # set flows
    # net1 - net2
    oc.set_flow(net4, "flow46", "3@0xd", "3@0x12", [["2@0xd", "2@0x12"]])
    oc.set_flow(net4, "flow64", "3@0x12", "3@0xd", [["2@0x12", "2@0xd"]])

    # net2 - net3
    oc.set_flow(net4, "flow79", "3@0x11", "3@0x10", [["1@0x11", "2@0x10"]])
    oc.set_flow(net4, "flow97", "3@0x10", "3@0x11", [["2@0x10", "1@0x11"]])

    # net3 - net1
    oc.set_flow(net4, "flow103", "3@0xf", "3@0xe", [["1@0xf", "2@0xe"]])
    oc.set_flow(net4, "flow310", "3@0xe", "3@0xf", [["2@0xe", "1@0xf"]])

    thread.join()
    dispatcher.stop()
