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

from copy import deepcopy

from org.o3project.odenos.core.component.network.topology.node import Node
from org.o3project.odenos.core.component.network.topology.link import Link


class Topology(object):
    # property key
    TYPE = "type"
    VERSION = "version"
    NODES = "nodes"
    LINKS = "links"

    def __init__(self, type_, version, nodes, links):
        self._body = {
            self.TYPE: type_,
            self.VERSION: version,
            self.NODES: nodes,
            self.LINKS: links
        }

    @property
    def type(self):
        return self._body[self.TYPE]

    @property
    def version(self):
        return self._body[self.VERSION]

    @property
    def nodes(self):
        return self._body[self.NODES]

    @property
    def links(self):
        return self._body[self.LINKS]

    @classmethod
    def create_from_packed(cls, packed):
        version = None
        if cls.VERSION in packed:
            version = packed[cls.VERSION]

        nodes = {}
        for node_id, node in packed[cls.NODES].items():
            nodes[node_id] = Node.create_from_packed(node)

        links = {}
        for link_id, link in packed[cls.LINKS].items():
            links[link_id] = Link.create_from_packed(link)

        return cls(packed[cls.TYPE], version,
                   nodes, links)

    def packed_object(self):
        object_ = deepcopy(self._body)

        nodes = {}
        for node_id in self.nodes:
            nodes[node_id] = self.nodes[node_id].packed_object()
        object_[self.NODES] = nodes

        links = {}
        for link_id in self.links:
            links[link_id] = self.links[link_id].packed_object()
        object_[self.LINKS] = links

        return object_
