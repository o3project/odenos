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

import threading
import logging
import json
import time

from org.o3project.odenos.core.component.logic import Logic
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.manager.component.event.component_changed import ComponentChanged
from org.o3project.odenos.remoteobject.manager.system.event.component_manager_changed import ComponentManagerChanged
from org.o3project.odenos.core.util.configurator import OdenosConfigurator
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.core.util.rest_client import RestClient
from org.o3project.odenos.core.util.request_parser import RequestParser
from neo4jclient import Neo4jClient


class Neo4jAdapter(Logic):
  DESCRIPTION = "Neo4jAdapter for python"
  _super_type = "Neo4jAdapter"

  # [odenosid1:neo4jid1 , odenosid2:neo4jid2, odenosid3:neo4jid3, ... ]
  _components  = {}
  # [odenosid1:neo4jid1 , odenosid2:neo4jid2, odenosid3:neo4jid3, ... ]
  _connections = {}
  _dependency = {}

  def __init__(self, object_id, dispatcher):
    super(Neo4jAdapter, self).__init__(object_id, dispatcher)
    logging.info("neo4j :: init")
    self.client = Neo4jClient()

    self.__parser = RequestParser()
    self.__add_rules()
    self.sysmgr = SystemManagerInterface(dispatcher)


  #######################
  # Connection
  #######################
  # override
  def _connection_changed_added(self, msg):
    t = threading.current_thread()
    conn = msg.curr
    logging.info("neo4j [%s]:: ###_connection_changed_added %s", t.name, conn.id)
    logging.info("neo4j [%s]:: ###_conn lo %s", t.name, conn.logic_id)
    logging.info("neo4j [%s]:: ###_conn ne %s", t.name, conn.network_id)

    if not conn.logic_id in self._components:
      component = self.sysmgr.get_component(conn.logic_id)
      _id = self.client.post_node(component)
      self._components[component["id"]] = _id

    if not conn.network_id in self._components:
      component = self.sysmgr.get_component(conn.network_id)
      logging.info("neo4j :: ###network %s", component)
      _id = self.client.post_node(component)
      self._components[component["id"]] = _id
    
    logging.info("neo4j :: ###_components %s", self._components)
    if not conn.id in self._connections:
      _id = self.client.post_relationship(conn.connection_type, 
                                    self._components[conn.logic_id],
                                    self._components[conn.network_id])
      self._connections[conn.id] = _id
      self._append_dependency(conn.logic_id, conn.id)
      self._append_dependency(conn.network_id, conn.id)
    logging.info("neo4j :: ###_is_dependency %s", self._dependency)

  # override
  def _connection_changed_delete(self, msg):
    conn = msg.prev
    if conn.id in self._connections:
      self.client.del_relationship(self._connections[conn.id])
      del self._connections[conn.id]

    if conn.logic_id in self._components:
      self._remove_dependency(conn.logic_id, conn.id)
      logging.info("neo4j :: ###_is_dependency %s", self._dependency)
      if not self._is_dependency(conn.logic_id):
        self.client.del_node(self._components[conn.logic_id])
        del self._components[conn.logic_id]

    if conn.network_id in self._components:
      self._remove_dependency(conn.network_id, conn.id)
      if not self._is_dependency(conn.network_id):
        self.client.del_node(self._components[conn.network_id])
        del self._components[conn.network_id]

  #######################
  # Request
  #######################
  def __add_rules(self):
    rules = []
    rules.append({RequestParser.PATTERN: r"^sync",
                  RequestParser.METHOD: Request.Method.GET,
                  RequestParser.FUNC: self._sync,
                  RequestParser.PARAMS: 0})
    self.__parser.add_rule(rules)

  def _on_request(self, request):
    return self.__parser.action(request)

  def _sync(self):
    logging.info("neo4j :: Request SYNC")
    self._components.clear()
    self._connections.clear()
    self.client.init_db()
    self._sync_components()
    self._sync_connections()
    return Response(Response.StatusCode.OK, None)

  def _sync_components(self):
    components = self.sysmgr.get_components()
    print json.dumps(components, sort_keys=True, indent=2)
    for component in components.itervalues():
      _id = self.client.post_node(component)
      self._components[component["id"]] = _id
    return Response(Response.StatusCode.OK, components)

  def _sync_connections(self):
    connections = self.sysmgr.get_connections()
    #print json.dumps(connections, sort_keys=True, indent=2)
    for c in connections.itervalues():
      logging.info("neo4j :: _connection : %s", c.id)
      _id = self.client.post_relationship(c.connection_type, 
                                    self._components[c.logic_id],
                                    self._components[c.network_id])
      self._connections[c.id] = _id
    return Response(Response.StatusCode.OK, connections)

  def _is_dependency(self, node_id):
    if self._dependency[node_id] is None:
      return False
    if len(self._dependency[node_id]) == 0:
      return False
    return True

  def _get_dependency(self, node_id):
    if node_id in self._dependency:
      return self._dependency[node_id]
    _list = []
    return _list

  def _append_dependency(self, node_id, connection_id):
    logging.info("neo4j :: ###_append_dependency [%s] [%s]", node_id, connection_id)
    connections = self._get_dependency(node_id)
    connections.append(connection_id)
    self._dependency[node_id] = connections

  def _remove_dependency(self, node_id, connection_id):
    connections = self._get_dependency(node_id)
    connections.remove(connection_id)
    self._dependency[node_id] = connections
