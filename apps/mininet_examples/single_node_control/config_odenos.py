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
from org.o3project.odenos.remoteobject.manager.system.component_connection \
    import ComponentConnection
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network \
    import ComponentConnectionLogicAndNetwork
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


if __name__ == "__main__":
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    dispatcher = MessageDispatcher()
    thread = ServerThread(dispatcher)
    thread.start()

    time.sleep(1)

    oc = OdenosConfigurator(dispatcher)

    drv1 = oc.create_ofdriver("driver1")
    net1 = oc.create_network("network1")
    lsw1 = oc.create_l2switch("l2sw1")

    oc.connect(lsw1, net1, "original")
    oc.connect(drv1, net1, "original")

    thread.join()
    dispatcher.stop()
