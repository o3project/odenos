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

import json
import sys
import time
import traceback
import copy

sys.path.append("../../../lib/python")
from org.o3project.odenos.remoteobject.transport.message_dispatcher import MessageDispatcher
from org.o3project.odenos.remoteobject.object_property import ObjectProperty 
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.core.util.logger import Logger
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.remoteobject.manager.system.component_connection import (
    ComponentConnection
)
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network import (
    ComponentConnectionLogicAndNetwork
)

if __name__ == '__main__':
    Logger.file_config()

    # Create MessageDispatcher
    dispatcher = MessageDispatcher()
    dispatcher.set_remote_system_manager()
    dispatcher.start()
    time.sleep(1)
    exit_code = 0
  
    try:
        # Create SystemManager Interface
        sysif = SystemManagerInterface(dispatcher)
        
        print "//////////////////////////////////////////////////"
        print "//// (1) Create Original Network."
        print "//////////////////////////////////////////////////"
        send_property = ObjectProperty("Network", "original_nw")
        resp = sysif.put_components(send_property)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (2) Create Aggregated Network."
        print "//////////////////////////////////////////////////"
        send_property = ObjectProperty("Network", "aggregated_nw")
        resp = sysif.put_components(send_property)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (3) Create Aggregator."
        print "//////////////////////////////////////////////////"
        send_property = ObjectProperty("Aggregator", "aggregator")
        resp = sysif.put_components(send_property)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (4) Create DummyDriver."
        print "//////////////////////////////////////////////////"
        send_property = ObjectProperty("DummyDriver", "dummy_driver")
        send_property.set_property(ObjectProperty.CM_ID, "romgr2")
        resp = sysif.put_components(send_property)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (5) Create Learning Switch."
        print "//////////////////////////////////////////////////"
        send_property = ObjectProperty("LearningSwitch", "learning_switch")
        resp = sysif.put_components(send_property)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)
        
        print "//////////////////////////////////////////////////"
        print "//// (6) Create Connection. (OriginalNW <=> Aggregator)."
        print "//////////////////////////////////////////////////"
        send_connect = ComponentConnectionLogicAndNetwork(
                                                          "conn1", "original",
                                                          ComponentConnection.State.INITIALIZING,
                                                          "aggregator",
                                                          "original_nw")
        resp = sysif.put_connection(send_connect)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        time.sleep(3)

        print "//////////////////////////////////////////////////"
        print "//// (7) Create Connection. (OriginalNW <=> DummyDriver)."
        print "//////////////////////////////////////////////////"
        send_connect = ComponentConnectionLogicAndNetwork(
                                                          "conn2", "dummy_driver_conn",
                                                          ComponentConnection.State.INITIALIZING,
                                                          "dummy_driver",
                                                          "original_nw")
        resp = sysif.put_connection(send_connect)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (8) Create Connection. (AggregatedNW <=> Aggregator)."
        print "//////////////////////////////////////////////////"
        send_connect = ComponentConnectionLogicAndNetwork(
                                                          "conn3", "aggregated",
                                                          ComponentConnection.State.INITIALIZING,
                                                          "aggregator",
                                                          "aggregated_nw")
        resp = sysif.put_connection(send_connect)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)

        print "//////////////////////////////////////////////////"
        print "//// (9) Create Connection. "
        print "////        (AggregatedNW <=> LearningSwitch)."
        print "//////////////////////////////////////////////////"
        send_connect = ComponentConnectionLogicAndNetwork(
                                                          "conn4", "learning_sw_conn",
                                                          ComponentConnection.State.INITIALIZING,
                                                          "learning_switch",
                                                          "aggregated_nw")
        resp = sysif.put_connection(send_connect)
        print resp.status_code
        print json.dumps(resp.body, sort_keys=True, indent=4)
        
        time.sleep(3)

        print "//////////////////////////////////////////////////"
        print "//// GET Components. "
        print "//////////////////////////////////////////////////"
        print json.dumps(sysif.get_components(), sort_keys=True, indent=4)
        print "//////////////////////////////////////////////////"
        print "//// GET Connections. "
        print "//////////////////////////////////////////////////"
        for id, conn in sysif.get_connections().iteritems():
            print "connection_id : " + str(id)
            print "connection_type : " + str(conn.connection_type)
            print "connection_state : " + str(conn.state)
            print "logic_id: " + str(conn.logic_id)
            print "network_id: " + str(conn.network_id)
            print "------------------------------"

    except:
        print "Unexpected error:", sys.exc_info()[0]
        print "= stack trace ============"
        traceback.print_exc()
        print "= stack trace ============"
        exit_code = 1

    dispatcher.stop()
    print "finished. %s" % exit_code
    sys.exit(exit_code)

