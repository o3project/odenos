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

## remove proxy
unset http_proxy

FORMAT="\n%{url_effective}, %{response_code}\n\n"
ADDRESS=localhost

# [LearningSwitch] ---- (network1) ---- [Aggregator] ----  (network0) ---- [DummyDriver] 

# create NetworkComponent , Aggregator , and other Component
curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X PUT -d '{"type": "Network", "id": "network0", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network1 -X PUT -d '{"type": "Network", "id": "network1", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X PUT -d '{"type": "LearningSwitch", "id": "lsw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/aggre -X PUT -d '{"type": "Aggregator", "id": "aggre", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/driver -X PUT -d '{"type": "DummyDriver", "id": "driver", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/aggregated -X PUT -d '{"id": "aggregated", "type": "LogicAndNetwork", "connection_type": "aggregated", "logic_id": "aggre", "network_id":"network1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/original -X PUT -d '{"id": "original", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "aggre", "network_id":"network0"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/driver -X PUT -d '{"id": "driver", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "driver", "network_id":"network0"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw -X PUT -d '{"id": "lsw", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "lsw", "network_id":"network1"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool


# make Topology
#                                                  [port0023]
#   [port0011](node001)[port0012] -----  [port0021](node002)[port0022] ----  [port0031](node003)[port0032]
#             [port0013]                           [port0024]                          [port0033]

sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {"oper_status":"UP", "physical_id":"001", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002 -X PUT -d '{"node_id": "node002", "type": "Node", "version": "0", "ports": {}, "attributes": {"oper_status":"UP", "physical_id":"002", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003 -X PUT -d '{"node_id": "node003", "type": "Node", "version": "0", "ports": {}, "attributes": {"oper_status":"UP", "physical_id":"003", "vendor":"VENDOR_A"}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {"physical_id":"0011@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {"physical_id":"0012@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {"physical_id":"0013@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {"physical_id":"0021@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {"physical_id":"0022@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {"physical_id":"0023@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0024 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0024", "out_link": null, "in_link": null, "attributes": {"physical_id":"0024@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0031 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0031", "out_link": null, "in_link": null, "attributes": {"physical_id":"0031@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0032 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0032", "out_link": null, "in_link": null, "attributes": {"physical_id":"0032@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0033 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0033", "out_link": null, "in_link": null, "attributes": {"physical_id":"0033@003"}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0012", "dst_node": "node002", "dst_port": "port0021", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0012", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0023 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0023", "src_node": "node002", "src_port": "port0022", "dst_node": "node003", "dst_port": "port0031", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0032 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0032", "src_node": "node003", "src_port": "port0031", "dst_node": "node002", "dst_port": "port0022", "attributes": {}}'


echo "------ original network -------" 
curl http://$ADDRESS:10080/systemmanager/components/network0/topology -X GET | python -mjson.tool

echo "------ aggregated network -------" 
curl http://$ADDRESS:10080/systemmanager/components/network1/topology -X GET | python -mjson.tool

# aggregated

# packet
echo "------ POST InPacket -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node003", "port":"port0032" , "header":{"type":"OFPFlowMatch", "in_node":"node003", "in_port":"port0032", "eth_src":"66:77:88:99:00:11", "eth_dst":"00:11:22:33:44:55"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'

echo "------ Get InPacket(network0) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X GET 
echo "------ Get InPacket(network1) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network1/packets/in -X GET 


sleep 5
echo "------ Get OutPacket(network0) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X GET 
echo "------ Get OutPacket(network1) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network1/packets/out -X GET 

echo "---------------- flow (network0)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network0/flows -X GET | python -mjson.tool
echo "---------------- flow (network1)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network1/flows -X GET | python -mjson.tool

echo "---------------- flow delete (network1)-------------------"
curl -w "$FORMAT" http://$ADDRESS:10080/network1/flows/lsw_0 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network1/flows/lsw_1 -X DELETE

sleep 5
echo "---------------- flow (network0)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network0/flows -X GET | python -mjson.tool
echo "---------------- flow (network1)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network1/flows -X GET | python -mjson.tool

echo ""
echo "*************************************************"
echo "***************[ Result ]************************"
echo "*************************************************"
echo ""

sleep 2
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network0/packets -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network1/packets -X GET | python -mjson.tool


# get topology
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/physical_nodes/001 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/physical_ports/0011@001 -X GET
echo "---------------- search Node -------------------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes?'attributes="physical_id=001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports?'attributes="physical_id=0021@002"' -X GET

sleep 2 
echo ""
echo ""
echo "***********************************************"
echo "***************[ Delete ]**********************"
echo "***********************************************"
echo ""

echo ""
echo "//////////////////////////////////////////////////"
echo "////////// Delete Topology to original ///////////"
echo "//////////////////////////////////////////////////"
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0032 -X DELETE
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/physical_ports/0011@001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0013 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0022 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0024 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0031 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0032 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0033 -X DELETE
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/physical_nodes/003 -X DELETE

sleep 1
echo "------ Check network0's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
echo "------ Check network1's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool

sleep 1 
echo ""
echo "/////////////////////////////////////////"
echo "////////// Delete Connections ///////////"
echo "/////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/aggregated -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/original -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/driver -X DELETE

sleep 3 
echo ""
echo "/////////////////////////////////////////"
echo "////////// Delete Components  ///////////"
echo "/////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network1 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/aggre -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/driver -X DELETE

echo ""
echo "------ Check Components -------" 
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool

echo "------ Check Connections -------" 
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool
