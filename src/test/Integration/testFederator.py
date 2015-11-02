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


import time
import sys
import json
import traceback

from org.o3project.odenos.core.util.configurator import OdenosConfigurator
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow import OFPFlow
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_match import OFPFlowMatch
from org.o3project.odenos.core.component.network.flow.basic.flow_action_output import FlowActionOutput
from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_action_set_field import OFPFlowActionSetField


class TestFederatorConfigurator(OdenosConfigurator):

  fed = None
  nwFed  = None
  nwOrgA = None
  nwOrgB = None
  nwOrgC = None

  def create_componets(self):
    print "# Create Networks ... "
    print "##################### "
    print "#      (nwFed)        " 
    print "#         |           " 
    print "#   [  federator  ]   " 
    print "#    |     |     |    " 
    print "#  (nwA) (nwB) (nwC)  " 
    print "#    |     |    |     " 
    print "#  [D1]  [D2]  [D3]   " 
    print "##################### "

    print "# Create Components ... "
    self.fed    = self.create_federator("federator")
    dd1    = self.create_dummydriver("D1")
    dd2    = self.create_dummydriver("D2")
    dd3    = self.create_dummydriver("D3")
    self.nwFed  = self.create_network("nwFed")
    self.nwOrgA = self.create_network("nwA")
    self.nwOrgB = self.create_network("nwB")
    self.nwOrgC = self.create_network("nwC")

    print "# Component Connections ... "
    conn00 = self.connect(self.fed, self.nwFed, "federated")
    conn01 = self.connect(self.fed, self.nwOrgA, "original")
    conn02 = self.connect(self.fed, self.nwOrgB, "original")
    conn03 = self.connect(self.fed, self.nwOrgC, "original")
    conn10 = self.connect(dd1, self.nwOrgA, "original")
    conn11 = self.connect(dd2, self.nwOrgB, "original")
    conn12 = self.connect(dd3, self.nwOrgC, "original")

    components = self.sysmgr.get_components()
    print json.dumps(components, sort_keys=True, indent=2)

    connections = self.sysmgr.get_connections()
    print json.dumps(components, sort_keys=True, indent=2)
    print "# Check Component connections ... OK"


  def create_topology(self):
    print "# Create Topology ... "
    print "################################################################################### "
    print "# [h1]  [h2]                [h2]                    [h3]                      [h4]  "
    print "#   |     |                  |                       |                          |   "
    print "#   |   [p2]                [p2]                    [p2]                     [p2]   "
    print "# [p1](node1)[p3] ---- [p1](node2)[p3] ---b1--[p1](node3)[p3]  -- b3 -- [p1](node4) "
    print "#                           [p4]                   [p4]                      [p3]   "
    print "#                             |                      |                         |    "
    print "#                             +-----------b2---------+                         |    "
    print "#                                                                              |    "
    print "#                                                                            [p2]   "
    print "#              [h5] -- [p1](node7)[p2] ---b5--[p1](node6)[p2]  -- b4 -- [p1](node5) "
    print "#                                                                                   "
    print "#                                                                                   "
    print "################################################################################### "

    # Federator set boundary
    bond = {"id": "bound_01", "type": "Federator",
            "network1": self.nwOrgA.network_id, "node1": "node2", "port1": "p3",
            "network2": self.nwOrgB.network_id, "node2": "node3", "port2": "p1" }
    self.fed._put_object_to_remote_object("settings/boundaries/bound_01", bond)
    bond = {"id": "bound_02", "type": "Federator",
            "network1": self.nwOrgA.network_id, "node1": "node2", "port1": "p4",
            "network2": self.nwOrgB.network_id, "node2": "node3", "port2": "p4" }
    self.fed._put_object_to_remote_object("settings/boundaries/bound_02", bond)
    bond = {"id": "bound_03", "type": "Federator",
            "network1": self.nwOrgB.network_id, "node1": "node3", "port1": "p3",
            "network2": self.nwOrgC.network_id, "node2": "node4", "port2": "p1" }
    self.fed._put_object_to_remote_object("settings/boundaries/bound_03", bond)

    self.create_node(self.nwOrgA, "node1", {"vendor":"OrgA"})
    self.create_port(self.nwOrgA, "node1", "p1")
    self.create_port(self.nwOrgA, "node1", "p2")
    self.create_port(self.nwOrgA, "node1", "p3")
    self.create_node(self.nwOrgA, "node2", {"vendor":"OrgB"})
    self.create_port(self.nwOrgA, "node2", "p1")
    self.create_port(self.nwOrgA, "node2", "p2")
    self.create_port(self.nwOrgA, "node2", "p3")
    self.create_port(self.nwOrgA, "node2", "p4")
    self.create_node(self.nwOrgB, "node3", {"vendor":"OrgB"})
    self.create_port(self.nwOrgB, "node3", "p1")
    self.create_port(self.nwOrgB, "node3", "p2")
    self.create_port(self.nwOrgB, "node3", "p3")
    self.create_port(self.nwOrgB, "node3", "p4")
    self.create_node(self.nwOrgC, "node4", {"vendor":"OrgC"})
    self.create_port(self.nwOrgC, "node4", "p1")
    self.create_port(self.nwOrgC, "node4", "p2")
    self.create_port(self.nwOrgC, "node4", "p3")
    self.create_node(self.nwOrgC, "node5", {"vendor":"OrgC"})
    self.create_port(self.nwOrgC, "node5", "p1")
    self.create_port(self.nwOrgC, "node5", "p2")
    self.create_node(self.nwOrgB, "node6", {"vendor":"OrgB"})
    self.create_port(self.nwOrgB, "node6", "p1")
    self.create_port(self.nwOrgB, "node6", "p2")
    self.create_node(self.nwOrgA, "node7", {"vendor":"OrgA"})
    self.create_port(self.nwOrgA, "node7", "p1")
    self.create_port(self.nwOrgA, "node7", "p2")
    self.create_link(self.nwOrgA, "link1a", "node1", "p3", "node2", "p1")
    self.create_link(self.nwOrgA, "link1b", "node2", "p1", "node1", "p3")
    self.create_link(self.nwOrgC, "link3a", "node4", "p3", "node5", "p2")
    self.create_link(self.nwOrgC, "link3b", "node5", "p2", "node4", "p3")

    # Federator set boundary
    boundaries = [
      [[self.nwOrgC, "p1@node5"], [self.nwOrgB, "p2@node6"]],
      [[self.nwOrgB, "p1@node6"], [self.nwOrgA, "p2@node7"]],
    ]
    self.set_fed_boundaries(self.fed, boundaries)

    get_boud = self.get_fed_boundaries(self.fed)
    print json.dumps(get_boud, sort_keys=True, indent=2)
    print "# Check Federator Boundaries ... OK"

    fed_topology = self.nwFed.get_topology()

  def flow_multi_domain(self):
    print "test multi node ..."

    flow_id = "multi_domain"
    m1 = OFPFlowMatch("OFPFlowMatch", "nwA_node1", "p1")
    m1.eth_dst = "00:22:33:44:55:66"
    m1.ip_proto = 6
    m1.eth_type = 2048
    matches = []
    matches.append(m1)
    path = ["nwA_link1a", "bound_01_link01", "bound_03_link01",
            "nwC_link3a", "bond_0_link01", "bond_1_link01"]

    a1 =  FlowActionOutput("FlowActionOutput", "p1")
    m2 = OFPFlowMatch("OFPFlowMatch", None, None)
    m2.eth_dst = "00:22:33:44:55:88"
    a2 = OFPFlowActionSetField("OFPFlowActionSetField", m2)
    m3 = OFPFlowMatch("OFPFlowMatch", None, None)
    m3.ipv4_dst = "10.10.1.1"
    a3 = OFPFlowActionSetField("OFPFlowActionSetField", m3)
    edge_actions = {}
    edge_actions["nwA_node7"] = [a1, a3]
    edge_actions["nwA_node2"] = [a2]

    resp = self.create_ofp_flow(self.nwFed, flow_id, matches, path, edge_actions)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    time.sleep(1)

    # checks #
    check_flow = self.nwFed.get_flow("multi_domain")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception

    # checks #
    check_flow = self.nwOrgA.get_flow("multi_domain_1")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception
    if not "link1a" in check_flow.path[0]:
      print "# Check flow ... NG " + check_flow.path[0]
      raise Exception
    if not "node1" in check_flow.matches[0].in_node:
      print "# Check flow ... NG " + check_flow.matches[0].in_node
      raise Exception
    if not "p1" in check_flow.matches[0].in_port:
      print "# Check flow ... NG " + check_flow.matches[0].in_port
      raise Exception
    if not "OFPFlowActionSetField" in check_flow.edge_actions["node2"][1].type:
      print "# Check flow ... NG " + check_flow.edge_actions["node2"][1].type
      raise Exception
    if not "00:22:33:44:55:88" in check_flow.edge_actions["node2"][1].match.eth_dst:
      print "# Check flow ... NG " + check_flow.edge_actions["node2"][1].match.eth_dst
      raise Exception

    # checks #
    check_flow = self.nwOrgA.get_flow("multi_domain_5")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception
    if len(check_flow.path) != 0:
      print "# Check flow ... NG "
      raise Exception
    if not "node7" in check_flow.matches[0].in_node:
      print "# Check flow ... NG "
      raise Exception
    if not "p2" in check_flow.matches[0].in_port:
      print "# Check flow ... NG "
      raise Exception
    if not "00:22:33:44:55:66" in check_flow.matches[0].eth_dst:
      print "# Check flow ... NG "
      raise Exception
    if not "FlowActionOutput" in check_flow.edge_actions["node7"][0].type:
      print "# Check flow ... NG " + check_flow.edge_actions["node7"][0].type
      raise Exception
    if not "p1" in check_flow.edge_actions["node7"][0].output:
      print "# Check flow ... NG "
      raise Exception
    if not "OFPFlowActionSetField" in check_flow.edge_actions["node7"][1].type:
      print "# Check flow ... NG " + check_flow.edge_actions["node7"][1].type
      raise Exception
    if not "10.10.1.1" in check_flow.edge_actions["node7"][1].match.ipv4_dst:
      print "# Check flow ... NG " + check_flow.edge_actions["node7"][1].match.ipv4_dst
      raise Exception

    #key = raw_input(' flow enable=False, please any key > ') 

    new_flow = self.nwFed.get_flow("multi_domain")
    new_flow.enabled = False
    resp = self.nwFed.put_flow(new_flow)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    # checks #
    time.sleep(1)
    check_flow = self.nwOrgA.get_flow("multi_domain_5")
    if check_flow.enabled != False:
      print "# Check flow ... NG "
      raise Exception
    if not "none" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception


    print "enable true -> false , path changed"
    #key = raw_input(' path changed, please any key > ') 

    new_flow = self.nwFed.get_flow("multi_domain")
    new_flow._body[new_flow.PATH] = ["nwA_link1a", "bound_02_link01",
        "bound_03_link01", "nwC_link3a", "bond_0_link01", "bond_1_link01"] #TODO
    new_flow.enabled = True
    resp = self.nwFed.put_flow(new_flow)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    # checks #
    time.sleep(1)
    check_flow = self.nwOrgC.get_flow("multi_domain_8")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception


    #key = raw_input(' flow Delete, please any key > ') 
    resp = self.nwFed.del_flow("multi_domain")
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    # checks #
    check_flow = self.nwOrgC.get_flow("multi_domain")
    if check_flow != None:
      print "# Check flow ... NG "
      raise Exception

    #key = raw_input(' flow set, please any key > ') 

    time.sleep(1)
    flow_id = "multi_domain"
    m1 = OFPFlowMatch("OFPFlowMatch", "nwA_node1", "p1")
    m1.eth_dst = "00:22:33:44:55:66"
    m1.ip_proto = 17
    m1.eth_type = 2048
    matches = []
    matches.append(m1)
    path = ["nwA_link1a", "bound_01_link01", "bound_03_link01",
            "nwC_link3a", "bond_0_link01", "bond_1_link01"]

    a1 =  FlowActionOutput("FlowActionOutput", "p1")
    m = OFPFlowMatch("OFPFlowMatch", None, None)
    m.eth_dst = "00:22:33:44:55:88"
    a2 = OFPFlowActionSetField("OFPFlowActionSetField", m)
    edge_actions = {}
    edge_actions["nwA_node7"] = [a2,a1]

    resp = self.create_ofp_flow(self.nwFed, flow_id, matches, path, edge_actions)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    time.sleep(1)
    check_flow = self.nwFed.get_flow("multi_domain")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception

    print "test multi node ... OK"


  def flow_single_node(self):
    print "test single node ..."

    flow_id = "single_node"
    m1 = OFPFlowMatch("OFPFlowMatch", "nwA_node1", "p1")
    m1.eth_dst = "00:22:33:44:55:66"
    m1.ip_proto = 6
    m1.eth_type = 2048
    matches = []
    matches.append(m1)
    path = []

    a1 =  FlowActionOutput("FlowActionOutput", "p2")
    m = OFPFlowMatch("OFPFlowMatch", None, None)
    m.eth_dst = "00:22:33:44:55:88"
    a2 = OFPFlowActionSetField("OFPFlowActionSetField", m)
    edge_actions = {}
    edge_actions["nwA_node1"] = [a2,a1]

    resp = self.create_ofp_flow(self.nwFed, flow_id, matches, path, edge_actions)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    path = self.nwOrgA.FLOW_PATH % flow_id + "_1"
    resp = self.nwOrgA._get_object_to_remote_object(path)
    print resp.status_code
    print json.dumps(resp.body, sort_keys=True, indent=2)

    time.sleep(1)
    # checks #
    check_flow = self.nwOrgA.get_flow("single_node_1")
    if check_flow.enabled != True:
      print "# Check flow ... NG "
      raise Exception
    if not "established" in check_flow.status:
      print "# Check flow ... NG ,status :" + check_flow.status
      raise Exception
    if len(check_flow.path) != 0:
      print "# Check flow ... NG "
      raise Exception
    if not "node1" in check_flow.matches[0].in_node:
      print "# Check flow ... NG "
      raise Exception
    if not "p1" in check_flow.matches[0].in_port:
      print "# Check flow ... NG "
      raise Exception
    if not "00:22:33:44:55:66" in check_flow.matches[0].eth_dst:
      print "# Check flow ... NG "
      raise Exception
    if not "OFPFlowActionSetField" in check_flow.edge_actions["node1"][1].type:
      print "# Check flow ... NG " + check_flow.edge_actions["node1"][1].type
      raise Exception
    if not "FlowActionOutput" in check_flow.edge_actions["node1"][0].type:
      print "# Check flow ... NG " + check_flow.edge_actions["node1"][0].type
      raise Exception
    if not "p2" in check_flow.edge_actions["node1"][0].output:
      print "# Check flow ... NG "
      raise Exception

    print "test single node ... OK"
if __name__ == "__main__":
    try :
      test = TestFederatorConfigurator()
      time.sleep(1)
      test.create_componets()
      test.create_topology()
      test.flow_single_node()
      test.flow_multi_domain()
    except Exception, e :
      print e
      print traceback.format_exc()
      test.thread.join()
      test.disp.stop()
      sys.exit(1)

    test.thread.join()
    test.disp.stop()
    sys.exit()
