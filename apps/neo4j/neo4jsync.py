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

import traceback
import sys
from org.o3project.odenos.core.util.configurator import OdenosConfigurator
from org.o3project.odenos.core.util.rest_client import RestClient
from neo4jclient import Neo4jClient

class Neo4jsync(OdenosConfigurator):

  def sync(self):
    self.client = Neo4jClient()
    self.client.init_db()
    self._sync_components()

  def _sync_components(self):
    _components = {}
    components = self.sysmgr.get_components()
    for component in components.itervalues():
      _id = self.client.post_node(component)
      _components[component["id"]] = _id

    connections = self.sysmgr.get_connections()
    for c in connections.itervalues():
      self.client.post_relationship(c.connection_type,
                                _components[c.logic_id],
                                _components[c.network_id])

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
