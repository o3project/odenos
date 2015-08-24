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


def set_of13(s):
    s.sendCmd('ovs-vsctl set bridge %s protocols=OpenFlow13' % s)


if '__main__' == __name__:
    net = Mininet(controller=RemoteController, autoStaticArp=True, switch=OVSSwitch)

    c1 = net.addController('c1', ip='127.0.0.1', port=6653)

    s1 = net.addSwitch('s1')

    h1 = net.addHost('h1')
    h2 = net.addHost('h2')

    s1.linkTo(h1)
    s1.linkTo(h2)

    net.build()

    c1.start()
    s1.start([c1])
    set_of13(s1)

    CLI(net)

    net.stop()
