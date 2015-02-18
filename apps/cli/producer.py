
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

import random

class Topology(object):
    """
    Topology source: ring, mesh, linear and fat_tree

    [source] -- send() --> [coroutine] -- send() --> [coroutine(sink)]

    args:
      node
      port
      eport
      link
      slice
      slice_condition <priority_policy>
      federation

    priority_policy:
      minmax    0 or 65535
      random    0 ~ 65535
      fixed     10
    
    Note: in case of fat_tree topo, this generates topo with fixed parameters:
          the number of pods: 40
          the number of ports: 40
          the number of ToR SW: 40 * pods
          the number of aggregation SW: 2 * pods
          the number of core SW: 4
    """
    eports = 3
    nodes = 10

    # fat_tree-related
    EPORT = 0
    NULL = 0
    CORE = 1 
    AGGREGATION = 2 
    TOR = 3 
    LEFT = 1
    RIGHT = 2
    

    def __init__(self, *args, **kwargs):
        """
        kwrags:
            networks    The number of networks
            topo_type   Topology type  
            nodes       The number of nodes
            eports      The number of unconnected (external) nodes
        """
        
        if 'networks' in kwargs:
            self.networks = kwargs['networks']
        else:
            self.networks = 1
        self.topo_type = kwargs['topo_type']
        if 'nodes' in kwargs:
            self.nodes = kwargs['nodes']
        else:
            self.nodes = Topology.nodes
        if 'eports' in kwargs:
            self.eports = kwargs['eports']
        else:
            self.eports = Topology.eports

        if self.topo_type == 'fat_tree':
            """
            layer: core(0), aggregation(1), tor(2)
            pod: 1 ~ 40
            """
            self.formatstr = '{layer:}{pod:02}{left_right}{number:02}'
        else:
            self.formatstr = '{:0'+str(len(str(self.nodes+self.eports))+1)+'}'
    
    # Coroutine setting    
    def __call__(self, cr_next=None):
        self.cr_next = cr_next
        return self

    def close(self):
        self.cr_next.close()

    # Coroutine send imitation    
    # TODO: this method should be coroutine's send()
    def send(self, data):
        cr_next = self.cr_next
        args = []
        kwargs = {}
        for s in data:
            if isinstance(s, dict):
                k = s.keys()[0]
                v = s.values()[0]
                kwargs[k] = v 
            else:
                args.append(s)
        gen_type = args[0]
        if gen_type == 'node':
            return self._generate_node(cr_next)
        elif gen_type == 'port':
            return self._generate_port(cr_next)
        elif gen_type == 'eport':  # External port
            return self._generate_eport(cr_next)
        elif gen_type == 'link':
            return self._generate_link(cr_next)
        elif gen_type == 'slice':
            return self._generate_slice(cr_next)
        elif gen_type == 'slice_condition':
            if len(args) == 2:
                return self._generate_slice_condition(cr_next, args[1])
            else:
                raise Exception('Requires slice_policy')
        elif gen_type == 'federation':
            if len(args) == 3:
                return self._generate_federation(cr_next, args[1], args[2])
            else:
                raise Exception('Requires boundary_node and boundary_port')
    
    def _generate_node(self, cr_next):
        formatstr = self.formatstr
        if self.topo_type == 'fat_tree':
            CORE = Topology.CORE
            AGGR = Topology.AGGREGATION
            TOR = Topology.TOR
            LEFT = Topology.LEFT
            RIGHT = Topology.RIGHT
            np = formatstr.format
            NULL = Topology.NULL
            for i in range(1, self.networks+1):
                # Core
                cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=1)])
                cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=2)])
                cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=1)])
                cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=2)])
                # Aggregation
                for pod in range(1,41):
                    cr_next.send([i, np(layer=AGGR, pod=pod, left_right=LEFT,  number=1)])
                    cr_next.send([i, np(layer=AGGR, pod=pod, left_right=RIGHT, number=1)])
                    # ToR
                    for tor in range(1,21):
                        cr_next.send([i, np(layer=TOR, pod=pod, left_right=LEFT,  number=tor)])
                        cr_next.send([i, np(layer=TOR, pod=pod, left_right=RIGHT, number=tor)])
        else:
            for i in range(1, self.networks+1):
                for j in range(1, self.nodes+1):
                    cr_next.send([i, formatstr.format(j)])

    def _generate_port(self, cr_next):
        networks = self.networks
        nodes = self.nodes
        formatstr = self.formatstr
        topo = self.topo_type
        if topo == 'ring':
            """
            ...[node]--adj_left--[node]--adj_right--[node]...
            """
            for i in range(1, networks + 1): 
                for j in range(1, nodes+1):
                    node = formatstr.format(j)
                    if j == 1:
                        adj_left = formatstr.format(nodes)
                        adj_right = formatstr.format(2) 
                    elif j == nodes:
                        adj_left = formatstr.format(nodes - 1)
                        adj_right = formatstr.format(1)
                    else:
                        adj_left = formatstr.format(j-1)
                        adj_right = formatstr.format(j+1)
                    cr_next.send([i, node, adj_left])
                    cr_next.send([i, node, adj_right])
        elif topo == 'mesh':
            """
                          | | 
            ...[node]----[node]----[node]...

            1   : range(1,1), range(2,1001)
            2   : range(1,2), range(3,1001)
            3   : range(1,3), range(4,1001)
                    :
            1000: range(1,1000), range(1001,1001)
            """
            for i in range(1, networks+1):
                for j in range(1, nodes+1):
                    node = formatstr.format(j)
                    for port in range(1,j):
                        cr_next.send([i, node, formatstr.format(port)])
                    for port in range(j+1,nodes+1):
                        cr_next.send([i, node, formatstr.format(port)])
        elif topo == 'linear':
            """
            [node]---[node]...[node]---[node]
            """
            for i in range(1, networks+1):
                for j in range(1, nodes+1):
                    node = formatstr.format(j)
                    if j == 1:
                        adj_right = formatstr.format(2) 
                        cr_next.send([i, node, adj_right])
                    elif j == nodes:
                        adj_left = formatstr.format(nodes - 1)
                        cr_next.send([i, node, adj_left])
                    else:
                        adj_left = formatstr.format(j-1)
                        adj_right = formatstr.format(j+1)
                        cr_next.send([i, node, adj_left])
                        cr_next.send([i, node, adj_right])
        elif topo == 'fat_tree':
            CORE = Topology.CORE
            AGGR = Topology.AGGREGATION
            TOR = Topology.TOR
            LEFT = Topology.LEFT
            RIGHT = Topology.RIGHT
            np = formatstr.format  # node & port
            NULL = Topology.NULL
            for i in range(1, self.networks+1):
                for pod in range(1,41):
                    # Core => Aggregation
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=1),
                              np(layer=AGGR, pod=pod,  left_right=LEFT,  number=1)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=2),
                              np(layer=AGGR, pod=pod,  left_right=LEFT,  number=1)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=1),
                              np(layer=AGGR, pod=pod,  left_right=RIGHT, number=1)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=2),
                              np(layer=AGGR, pod=pod,  left_right=RIGHT, number=1)])

                    # Aggregation => Core
                    cr_next.send([i, np(layer=AGGR, pod=pod,  left_right=LEFT,  number=1),
                              np(layer=CORE, pod=NULL, left_right=LEFT,  number=1)])
                    cr_next.send([i, np(layer=AGGR, pod=pod,  left_right=LEFT,  number=1),
                              np(layer=CORE, pod=NULL, left_right=LEFT,  number=2)])
                    cr_next.send([i, np(layer=AGGR, pod=pod,  left_right=RIGHT, number=1),
                              np(layer=CORE, pod=NULL, left_right=RIGHT, number=1)])
                    cr_next.send([i, np(layer=AGGR, pod=pod,  left_right=RIGHT, number=1),
                              np(layer=CORE, pod=NULL, left_right=RIGHT, number=2)])
                # Aggregation
                for pod in range(1,41):
                    # ToR
                    for tor in range(1,21):
                        # Aggregation => ToR
                        cr_next.send([i, np(layer=AGGR, pod=pod, left_right=LEFT,  number=1),
                                  np(layer=TOR,  pod=pod, left_right=LEFT,  number=tor)])
                        cr_next.send([i, np(layer=AGGR, pod=pod, left_right=RIGHT, number=1),
                                  np(layer=TOR,  pod=pod, left_right=LEFT,  number=tor)])
                        cr_next.send([i, np(layer=AGGR, pod=pod, left_right=LEFT,  number=1),
                                  np(layer=TOR,  pod=pod, left_right=RIGHT, number=tor)])
                        cr_next.send([i, np(layer=AGGR, pod=pod, left_right=RIGHT, number=1),
                                  np(layer=TOR,  pod=pod, left_right=RIGHT, number=tor)])
                        # ToR => Aggregation
                        cr_next.send([i, np(layer=TOR,  pod=pod, left_right=LEFT,  number=tor),
                                  np(layer=AGGR, pod=pod, left_right=LEFT,  number=1)])
                        cr_next.send([i, np(layer=TOR,  pod=pod, left_right=LEFT,  number=tor),
                                  np(layer=AGGR, pod=pod, left_right=RIGHT, number=1)])
                        cr_next.send([i, np(layer=TOR,  pod=pod, left_right=RIGHT, number=tor),
                                  np(layer=AGGR, pod=pod, left_right=LEFT,  number=1)])
                        cr_next.send([i, np(layer=TOR,  pod=pod, left_right=RIGHT, number=tor),
                                  np(layer=AGGR, pod=pod, left_right=RIGHT, number=1)])
        else:
            pass

    def _generate_eport(self, cr_next):  # External ports
        networks = self.networks
        nodes = self.nodes
        eports = self.eports
        formatstr = self.formatstr
        topo = self.topo_type
        if topo in ['ring', 'mesh']:
            for i in range(1, networks+1):
                for j in range(1, nodes+1):
                    node = formatstr.format(j)
                    for k in range(nodes+1, nodes+eports+1):
                        eport = formatstr.format(k)
                        cr_next.send([i, node, eport])
        elif topo == 'linear':
            for i in range(1, networks+1):
                for j in [1, nodes]:
                    node = formatstr.format(j)
                    for k in range(nodes+1, nodes+eports+1):
                        eport = formatstr.format(k)
                        cr_next.send([i, node, eport])
        elif topo == 'fat_tree':
            CORE = Topology.CORE
            TOR = Topology.TOR
            LEFT = Topology.LEFT
            RIGHT = Topology.RIGHT
            np = formatstr.format
            NULL = Topology.NULL
            EPORT = Topology.EPORT
            for i in range(1, networks+1):
                # Core
                for eport in range(1, 5):
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=1),
                              np(layer=EPORT,pod=NULL, left_right=NULL,  number=eport)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=LEFT,  number=2),
                              np(layer=EPORT,pod=NULL, left_right=NULL,  number=eport)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=1),
                              np(layer=EPORT,pod=NULL, left_right=NULL,  number=eport)])
                    cr_next.send([i, np(layer=CORE, pod=NULL, left_right=RIGHT, number=2),
                              np(layer=EPORT,pod=NULL, left_right=NULL,  number=eport)])
                # ToR
                #for eport in range(1, 41):
                for eport in range(1, 11):
                    for pod in range(1,41):
                        for tor in range(1,21):
                            cr_next.send([i, np(layer=TOR, pod=pod, left_right=LEFT, number=tor),
                                      np(layer=EPORT, pod=NULL, left_right=NULL, number=eport)])
                            cr_next.send([i, np(layer=TOR, pod=pod, left_right=RIGHT, number=tor),
                                      np(layer=EPORT, pod=NULL, left_right=NULL, number=eport)])

    def _generate_link(self, cr_next):
        if self.topo_type == 'ring' or 'mesh':
            return self._generate_port(cr_next)
        else:
            pass

    def _generate_slice(self, cr_next):
        nodes = self.nodes
        eports = self.eports
        formatstr = self.formatstr
        for i in range(nodes+1, nodes+eports+1):
            eport = formatstr.format(i)
            cr_next.send([eport])

    def _generate_slice_condition(self, cr_next, priority_policy):
        nodes = self.nodes
        eports = self.eports
        formatstr = self.formatstr
        topo = self.topo_type
        seqno = 0
        if topo in ['ring', 'mesh']:
            range_ = range(1, nodes+1)
        elif topo in ['linear']:
            range_ = [1, nodes]
        for i in range_:
            node = formatstr.format(i)
            for j in range(nodes+1, nodes+eports+1):
                eport = formatstr.format(j)
                slice_ = eport
                priority = 10 
                if priority_policy == 'minmax':
                    priority = random.randint(0,1) * 65535
                elif priority_policy == 'random':
                    priority = random.randint(0,65535)
                elif priority_policy == 'fixed':
                    pass 
                seqno += 1
                cr_next.send([slice_, priority, node, eport, seqno])

    def _generate_federation(self, cr_next, node, port):
        networks = self.networks
        formatstr = self.formatstr
        node = formatstr.format(node)
        port = formatstr.format(port)
        if networks < 2:
            raise Exception("Federation impossible")
        elif networks == 2:
            cr_next.send([1, node, port, 2, node, port])
        else:
            for i in range(1, networks+1):
                if i == networks:
                    cr_next.send([i, node, port, 1, node, port])
                else:
                    cr_next.send([i, node, port, i+1, node, port])


