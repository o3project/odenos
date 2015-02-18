#!/bin/sh

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

##sudo rmmod bridge > /dev/null 2>&1
CONTROLLER_IP=127.0.0.1
CONTROLLER_PORT=6653

########################################
## custom methods
########################################

# *make Topology
#
#  "vhost1" "vhost2"  "vhost3" "vhost4"
#      |       |         |       |
#       <<ofs1>>         <<ofs3>>
#          |                |
#          ---- <<ofs2>> ---- 
#
add() {
    # diable ipv6
    sudo sysctl -w net.ipv6.conf.all.disable_ipv6=1

    # add ofs(openflow1.3 vswitch)
    addOFSwitch13 "ofs1" "0x0000000091011950"
    addOFSwitch13 "ofs2" "0x0000000092011950"
    addOFSwitch13 "ofs3" "0x0000000093011950"
    
    # connect ofs <--> ofs 
    connectVSwitchToVSwitch "ofs1" "vlinkOFS1" "ofs2"
    connectVSwitchToVSwitch "ofs2" "vlinkOFS2" "ofs3"

    # add vhost. connect vhost <--> ofs 
    addVHost "vhost1"
    connectVHostToVSwitch "vhost1" "vlink1" "ofs1" 
    vhostExec "vhost1" ifconfig "vlink1" "192.168.0.1"

    addVHost "vhost2"
    connectVHostToVSwitch "vhost2" "vlink2" "ofs1" 
    vhostExec "vhost2" ifconfig "vlink2" "192.168.0.2"

    addVHost "vhost3"
    connectVHostToVSwitch "vhost3" "vlink3" "ofs3" 
    vhostExec "vhost3" ifconfig "vlink3" "192.168.0.3"

    addVHost "vhost4"
    connectVHostToVSwitch "vhost4" "vlink4" "ofs3" 
    vhostExec "vhost4" ifconfig "vlink4" "192.168.0.4"
}

delete() {
    # able ipv6
    sudo sysctl -w net.ipv6.conf.all.disable_ipv6=0

    deleteVHost "vhost1" "vhost2" "vhost3" "vhost4"
    deleteVLink "vlink1" "vlink2" "vlink3" "vlink4"
    deleteVLink "vlinkOFS1" "vlinkOFS2"
    deleteVSwitch "ofs1" "ofs2" "ofs3"

    # restart openvswitch 
    sudo service openvswitch-switch restart 
}

show_topology() {
    sudo ovs-vsctl show
}

show_flows() {
    sudo ovs-ofctl dump-flows ofs1 --protocol=OpenFlow13
    sudo ovs-ofctl dump-flows ofs2 --protocol=OpenFlow13
    sudo ovs-ofctl dump-flows ofs3 --protocol=OpenFlow13
}

########################################
## common methods
########################################

setupVSwitchAsOpenFlowSwitch() {
    local switch="$1"
    local datapath_id="$2"
    local of_protocol="$3"

    if [ "$datapath_id" != "" ]; then
        sudo ovs-vsctl set bridge "$switch" other-config:datapath-id=`printf \"%016x\" $datapath_id`
    fi
    if [ "$of_protocol" != "" ]; then
        sudo ovs-vsctl set bridge "$switch" protocols=$of_protocol
    fi
    sudo ovs-vsctl set-fail-mode "$switch" secure
    sudo ovs-vsctl set-controller "$switch" tcp:$CONTROLLER_IP:$CONTROLLER_PORT 
}

deleteVHost() {
   local host
   for host in "$@"; do
       sudo ip netns delete "$host"
   done
}

deleteVSwitch() {
   local switch
   for switch in "$@"; do
       sudo ovs-vsctl del-br "$switch"
   done
}

deleteVLink() {
   local link
   for link in "$@"; do
       sudo ip link delete "$link"
       sudo ip link delete "${link}-0"
       sudo ip link delete "${link}-1"
   done
}

addVHost() {
    local host="$1"
    sudo ip netns add "$host"
    sudo ip netns exec "$host" ifconfig lo 127.0.0.1
}

addVSwitch() {
    local switch="$1"
    sudo ovs-vsctl add-br "$switch"
}

addOFSwitch() {
    addVSwitch "$1"
    setupVSwitchAsOpenFlowSwitch "$1" "$2"
}

addOFSwitch13() {
    addVSwitch "$1"
    setupVSwitchAsOpenFlowSwitch "$1" "$2" "OpenFlow13"
}

addStandaloneOFSwitch() {
    local switch="$1"

    addVSwitch "$switch"
    sudo ovs-vsctl set-fail-mode "$switch" secure
    sudo ovs-ofctl del-flows "$switch"
}

connectVSwitchToVSwitch() {
    local switch1="$1"
    local link="$2"
    local switch2="$3"

    sudo ip link add name "${link}-0" type veth peer name "${link}-1"

    sudo ip link set "${link}-0" up
    sudo ovs-vsctl add-port "$switch1" "${link}-0"

    sudo ip link set "${link}-1" up
    sudo ovs-vsctl add-port "$switch2" "${link}-1"
}

connectVHostToVSwitch() {
    local host="$1"
    local link="$2"
    local switch="$3"
    local opt="$4"

    sudo ip link add name "${link}" type veth peer name "${link}-1"

    sudo ip link set "${link}-1" netns "$host"
    sudo ip netns exec "$host" ip link set "${link}-1" name "${link}"
    sudo ip netns exec "$host" ifconfig "${link}" up

    sudo ip link set "${link}" up
    sudo ovs-vsctl add-port "$switch" "${link}" $opt
}

vhostExec() {
    sudo ip netns exec "$@"
}

case "$1" in
add)
    sudo sh -c exit
    set -x
    add
    ;;
delete)
    sudo sh -c exit
    delete > /dev/null 2>&1
    ;;
show_topology)
    sudo sh -c exit
    set -x
    show_topology
    ;;
show_flows)
    sudo sh -c exit
    set -x
    show_flows
    ;;
*)
    echo "usage: $0 { add | delete | show_topology | show_flows }"
esac

exit
