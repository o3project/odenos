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

import inspect
import traceback
import sys
from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.core.util.configurator import OdenosConfigurator
from org.o3project.odenos.core.util.rest_client import RestClient
from neo4jclient import Neo4jClient

class Neo4jsync(OdenosConfigurator):
  _components = {}

  def sync(self):
    self.client = Neo4jClient()
    self.client.init_db()
    self._sync_components()
    #self._sysc_topology() #TODO

  def _sync_components(self):
    self._components = {}
    components = self.sysmgr.get_components()
    for component in components.itervalues():
      print "component::" + component["super_type"]
      if "Driver" in component["super_type"]:
        _id = self.client.post_node(component, label_key="super_type")
      else:
        _id = self.client.post_node(component)
      self._components[component["id"]] = _id

    connections = self.sysmgr.get_connections()
    for c in connections.itervalues():
      self.client.post_relationship(c.connection_type,
                                self._components[c.logic_id],
                                self._components[c.network_id])

  def _sysc_topology(self):
    _topology = {}
    components = self.sysmgr.get_components()
    for component in components.itervalues():
      if not "Network" in component["type"]:
        continue

      print "network_id::" + component["id"]

      network = self.get_network(component["id"])
      topology = network.get_topology()
      _id = None
      for node in topology.nodes.itervalues():
        print "node_id::" + node.node_id
        obj = {"id":node.node_id, "type":node.type}
        _id = self.client.post_node(obj)
        _topology[obj["id"]] = _id
        
      if not _id :
        continue

      self.client.post_relationship("topology",
              self._components[component["id"]], _id)

      for link in topology.links.itervalues():
        self.client.post_relationship(link.type,
                                  _topology[link.dst_node],
                                  _topology[link.src_node])

      flowset = network.get_flow_set()
      flows = flowset.flows
      for flow in flowset.flows.itervalues():
        if len(flow.path) != 0:
          for path in flow.path:
            link = network.get_link(path)
            self._set_flow(flow.flow_id,
                           _topology[link.src_node],
                           _topology[link.dst_node])
        else:
          _node = _topology[flow.matches[0].in_node]
          self._set_flow(flow.flow_id, _node, _node) 

  def _set_flow(self, flow_id, in_node, out_node):
    self.client.post_relationship("Flow", in_node, out_node, flow_id)

if __name__ == "__main__":
  try :
    neo4j = Neo4jsync()
    neo4j.sync()

  except Exception, e :
    print e
    print traceback.format_exc()
    neo4j.thread.join()
    neo4j.disp.stop()
    sys.exit(1)

  neo4j.thread.join()
  neo4j.disp.stop()
  sys.exit()
