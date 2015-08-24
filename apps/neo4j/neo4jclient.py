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

import os
import re
import logging
import json
import subprocess
from org.o3project.odenos.core.util.rest_client import RestClient
from org.o3project.odenos.remoteobject.message.response import Response

class Neo4jClient(object):
  settings = {"address":"127.0.0.1", "port":"7474", "timeout":10,
              "auth":"Basic bmVvNGo6bmVvNA=="}

  # NODE LABEL
  LABEL_COMPONENT = "component"
  LABEL_ODENOS = "ODENOS"

  rest = None # REST Client Object

  def __init__(self):
    self._read_config()
    self.rest = RestClient(self.settings["address"],
                           self.settings["port"],
                           self.settings["timeout"])
    #self.rest.set_auth(self.settings["auth"])

  def _read_config(self):
    path = None
    if os.path.exists('neo4j.conf'):
      path = 'neo4j.conf'
    if os.path.exists('apps/neo4j/neo4j.conf'):
      path = 'apps/neo4j/neo4j.conf'
    if path is None:
      return

    r = re.compile("^#.*")
    for line in open(path, 'r'):
      if r.search(line):  # remove comment 
        continue
      conf = line[:-1].split("=",1)
      if len(conf) != 2:
        continue
      self.settings[conf[0]] = conf[1]
    
    logging.info("neo4jAdapter set config :: %s", self.settings)

  #######################
  # Neo4J REST API
  #######################
  def init_db(self):
    path = "/db/data/label/" + self.LABEL_COMPONENT + "/nodes"
    resp = self.rest.get(path, None)
    logging.info("neo4j :: GET %s [%s] %s", path, resp.status_code, resp.body)
    if resp.is_error("GET"):
      return None
    body = json.loads(resp.body)
    for node in body:
      self.del_node(node["metadata"]["id"])

  def post_node(self, component, label_key="type"):
    path = "/db/data/node"
    resp = self.rest.post(path, component)
    logging.info("neo4j :: id[%s] POST %s [%s] %s",
                 component["id"], path, resp.status_code, resp.body)
    if resp.is_error("POST"):
      return None
    body = json.loads(resp.body)
    self._add_label(body["metadata"]["id"], component[label_key])
    return body["metadata"]["id"]

  def post_relationship(self, conn_type, src, dst, _id=None):
    path = "/db/data/node/" + str(src) + "/relationships"
    body = {"to":str(dst) , "type":conn_type, "data":{}}
    if _id:
      body["data"]["id"] = _id
    resp = self.rest.post(path, body)
    logging.info("neo4j :: POST %s [%s] %s", path, resp.status_code, resp.body)
    if resp.is_error("POST"):
      return None
    body = json.loads(resp.body)
    return body["metadata"]["id"]

  def del_node(self, _id):
    self._del_relationship_all(_id)
    self._del("/db/data/node/" + str(_id))

  def del_relationship(self, _id):
    self._del("/db/data/relationship/" + str(_id))

  def _del(self, path):
    resp = self.rest.delete(path, None)
    logging.info("neo4j :: DELETE %s [%s] %s", path, resp.status_code, resp.body)

  def _add_label(self, _id, component_type):
    path = "/db/data/node/" + str(_id) + "/labels"
    labels = [self.LABEL_ODENOS, self.LABEL_COMPONENT, component_type]
    resp = self.rest.post(path, labels)
    logging.info("neo4j ::POST %s [%s]", path, resp.status_code)

  def _get_relationship_all(self, _id):
    path = "/db/data/node/" + str(_id) + "/relationships/all"
    resp = self.rest.get(path, None)
    logging.info("neo4j :: GET %s [%s] %s", path, resp.status_code, resp.body)
    if resp.is_error("GET"):
      return None
    if resp.body:
      return json.loads(resp.body)
    return None

  def _del_relationship_all(self, _id):
    for relationship in self._get_relationship_all(_id):
      self.del_relationship(relationship["metadata"]["id"])
