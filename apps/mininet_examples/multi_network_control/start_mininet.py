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

from mininet.cli import CLI
from mininet.net import Mininet
from mininet.node import RemoteController, OVSSwitch


def start_of13_switches(controller, switches):
    for s in switches:
        s.start([controller])
        s.sendCmd('ovs-vsctl set bridge %s protocols=OpenFlow13' % s)


if '__main__' == __name__:
    net = Mininet(controller=RemoteController, autoStaticArp=True, switch=OVSSwitch)

    c1 = net.addController('c1', ip='127.0.0.1', port=6653)
    c2 = net.addController('c2', ip='127.0.0.1', port=6654)
    c3 = net.addController('c3', ip='127.0.0.1', port=6655)
    c4 = net.addController('c4', ip='127.0.0.1', port=6656)
    
    switches = {}
    for i in range(1, 19):
        switches[i] = net.addSwitch('s' + str(i))

    # create three dc networks
    for i in range(1, 4):
        for j in range(2, 5):
            n = 4 * (i - 1) + j
            switches[n-1].linkTo(switches[n])
        n = 4 * (i - 1)
        switches[n + 1].linkTo(switches[n + 4])

    # create one wan network
    for i in range(14, 19):
        switches[i-1].linkTo(switches[i])
    switches[13].linkTo(switches[18])

    # add hosts
    h1 = net.addHost('h1')
    h2 = net.addHost('h2')
    h3 = net.addHost('h3')
    h4 = net.addHost('h4')
    switches[1].linkTo(h1)
    switches[3].linkTo(h2)
    switches[8].linkTo(h3)
    switches[12].linkTo(h4)

    # connect dc networks with core networks
    switches[4].linkTo(switches[13])
    switches[3].linkTo(switches[14])
    switches[6].linkTo(switches[18])
    switches[7].linkTo(switches[17])
    switches[9].linkTo(switches[16])
    switches[10].linkTo(switches[15])

    net.build()

    c1.start()
    c2.start()
    c3.start()
    c4.start()
    start_of13_switches(c1, switches.values()[0:4])
    start_of13_switches(c2, switches.values()[4:8])
    start_of13_switches(c3, switches.values()[8:12])
    start_of13_switches(c4, switches.values()[12:18])

    CLI(net)

    net.stop()
