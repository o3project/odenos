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

# [LearningSwitch] --- (LayerizedNetowrk) --- [LinkLayerizer] ---- (UpperNetwork)  
#                                                              |
#                                                              |-- (LowerNetwork)

# create NetworkComponent , Federator, and other Component
curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/layerized-nw -X PUT -d '{"type": "Network", "id": "layerized-nw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/upper-nw -X PUT -d '{"type": "Network", "id": "upper-nw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lower-nw -X PUT -d '{"type": "Network", "id": "lower-nw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X PUT -d '{"type": "LearningSwitch", "id": "lsw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/linklayerizer -X PUT -d '{"type": "LinkLayerizer", "id": "linklayerizer", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_layerized-nw -X PUT -d '{"id": "linklayerizer_layerized-nw", "type": "LogicAndNetwork", "connection_type": "layerized", "logic_id": "linklayerizer", "network_id":"layerized-nw"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_upper-nw -X PUT -d '{"id": "linklayerizer_upper-nw", "type": "LogicAndNetwork", "connection_type": "upper", "logic_id": "linklayerizer", "network_id":"upper-nw"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_lower-nw -X PUT -d '{"id": "linklayerizer_lower-nw", "type": "LogicAndNetwork", "connection_type": "lower", "logic_id": "linklayerizer", "network_id":"lower-nw"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_layerized-nw -X PUT -d '{"id": "lsw_layerized-nw", "type": "LogicAndNetwork", "connection_type": "layerized", "logic_id": "lsw", "network_id":"layerized-nw"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool


sleep 1
# make Topology
#
# - "upper-nw"
#
#                                                  [port0023]
#   [port0011](node001)[port0012] <---> [port0021](node002)[port0022]      [port0031](node003)[port0032]
#             [port0013]                           [port0024]                         [port0033]
#
# - "lower-nw" 
#
#   [port0111](node011)[port0112] <---> [port0121](node012)[port0122]
#

## upper-nw's Topology
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"001", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002 -X PUT -d '{"node_id": "node002", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"002", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003 -X PUT -d '{"node_id": "node003", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"003", "vendor":"VENDOR_A"}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {"physical_id":"0011@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {"physical_id":"0012@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {"physical_id":"0013@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {"physical_id":"0021@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {"physical_id":"0022@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {"physical_id":"0023@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0024 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0024", "out_link": null, "in_link": null, "attributes": {"physical_id":"0024@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003/ports/port0031 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0031", "out_link": null, "in_link": null, "attributes": {"physical_id":"0031@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003/ports/port0032 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0032", "out_link": null, "in_link": null, "attributes": {"physical_id":"0032@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003/ports/port0033 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0033", "out_link": null, "in_link": null, "attributes": {"physical_id":"0033@003"}}'

sleep 1

curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0012", "dst_node": "node002", "dst_port": "port0021", "attributes": {"attr0021":"value0021"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0012", "attributes": {"attr0012":"value0012"}}'

sleep 1 

## lower-nw's Topology

curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node011 -X PUT -d '{"node_id": "node011", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"011", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node012 -X PUT -d '{"node_id": "node012", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"012", "vendor":"VENDOR_A"}}'

curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node011/ports/port0111 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node011", "port_id": "port0111", "out_link": null, "in_link": null, "attributes": {"physical_id":"0111@011"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node011/ports/port0112 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node011", "port_id": "port0112", "out_link": null, "in_link": null, "attributes": {"physical_id":"0112@011"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node012/ports/port0121 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node012", "port_id": "port0121", "out_link": null, "in_link": null, "attributes": {"physical_id":"0121@012"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node012/ports/port0122 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node012", "port_id": "port0122", "out_link": null, "in_link": null, "attributes": {"physical_id":"0122@012"}}'

curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/links/link0112 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0112", "src_node": "node011", "src_port": "port0112", "dst_node": "node012", "dst_port": "port0121", "attributes": {"attr0112":"value0112"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/links/link0121 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0121", "src_node": "node012", "src_port": "port0121", "dst_node": "node011", "dst_port": "port0112", "attributes": {"attr0121":"value0121"}}'

sleep 1

# make LinkLayerizerBoundary 
#
#          "upper-nw"                           "lower-nw"
#
#           [port0023]
# [port0021](node002)[port0022] <---> [port0111](node011)[port0112] 
#           [port0024] 
#
#
#          "lower-nw"                          "upper-nw"
#
# [port0121](node012)[port0122] <---> [port0031](node003)[port0032] 
#                                               [port0033] 
#

echo "------ create linklayerizer bundary -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/boundaries/boundary1 -X PUT -d '{"id":"boundary1", "type":"LinkLayerizer", "upper_nw":"upper-nw", "lower_nw":"lower-nw", "upper_nw_node":"node002", "lower_nw_node":"node011", "upper_nw_port":"port0022", "lower_nw_port":"port0111"}' 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/boundaries/boundary2 -X PUT -d '{"id":"boundary2", "type":"LinkLayerizer", "upper_nw":"upper-nw", "lower_nw":"lower-nw", "upper_nw_node":"node003", "lower_nw_node":"node012", "upper_nw_port":"port0031", "lower_nw_port":"port0122"}' 

echo "------ get linklayerizer bundary -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/boundaries -X GET 

echo "------ put upper_link_sync (false) -------" 
sleep 3 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/upper_link_sync -X PUT -d '{"sync":false}' 


echo "------ PUT Flow (to lower-nw) [case01] -------" 
sleep 1 
curl -w "$FORMAT" http://localhost:10080/lower-nw/flows/flow01 -X PUT -d '{"version":"0","flow_id":"flow01","owner":"logic","enabled":true,"status":"none","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"node011","in_port":"port0111","eth_src":"ff:aa:ff:aa:ff:aa","eth_dst":"bb:ff:bb:ff:bb:ff","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.1","ipv4_dst":"10.0.0.2","tcp_src":10000,"tcp_dst":10001}],"path":["link0112"],"edge_actions":{"node012":[{"type":"FlowActionOutput","output":"port0122"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ab:ff:ab:ff:ab:ff"}}]}}'
curl -w "$FORMAT" http://localhost:10080/lower-nw/flows/flow01 -X PUT -d '{"version":"1","flow_id":"flow01","owner":"logic","enabled":true,"status":"establishing","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"node011","in_port":"port0111","eth_src":"ff:aa:ff:aa:ff:aa","eth_dst":"bb:ff:bb:ff:bb:ff","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.1","ipv4_dst":"10.0.0.2","tcp_src":10000,"tcp_dst":10001}],"path":["link0112"],"edge_actions":{"node012":[{"type":"FlowActionOutput","output":"port0122"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ab:ff:ab:ff:ab:ff"}}]}}'
sleep 3 
curl -w "$FORMAT" http://localhost:10080/lower-nw/flows/flow01 -X PUT -d '{"version":"2","flow_id":"flow01","owner":"logic","enabled":true,"status":"established","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0", "original_key":"original_val", "original_key2":"original_val2"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"node011","in_port":"port0111","eth_src":"ff:aa:ff:aa:ff:aa","eth_dst":"bb:ff:bb:ff:bb:ff","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.1","ipv4_dst":"10.0.0.2","tcp_src":10000,"tcp_dst":10001}],"path":["link0112"],"edge_actions":{"node012":[{"type":"FlowActionOutput","output":"port0122"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ab:ff:ab:ff:ab:ff"}}]}}'

echo "------ PUT Flow (to lower-nw) [case02] -------" 
sleep 1 
curl -w "$FORMAT" http://localhost:10080/lower-nw/flows/flow02 -X PUT -d '{"version":"0","flow_id":"flow02","owner":"logic","enabled":true,"status":"establishing","attributes":{"latency":"0", "req_latency":"0", "bandwidth":"0"},"type":"OFPFlow","idle_timeout":90,"hard_timeout":90,"matches":[{"type":"OFPFlowMatch","in_node":"node012","in_port":"port0122","eth_src":"bb:ff:bb:ff:bb:ff","eth_dst":"ff:aa:ff:aa:ff:aa","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.2","ipv4_dst":"10.0.0.1","tcp_src":10000,"tcp_dst":10001}],"path":["link0121"],"edge_actions":{"node011":[{"type":"FlowActionOutput","output":"port0111"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ff:ab:ff:ab:ff:ab"}}]}}'
sleep 1 
curl -w "$FORMAT" http://localhost:10080/lower-nw/flows/flow02 -X PUT -d '{"version":"1","flow_id":"flow02","owner":"logic","enabled":true,"status":"established","attributes":{"latency":"0", "req_latency":"0", "bandwidth":"0"},"type":"OFPFlow","idle_timeout":90,"hard_timeout":90,"matches":[{"type":"OFPFlowMatch","in_node":"node012","in_port":"port0122","eth_src":"bb:ff:bb:ff:bb:ff","eth_dst":"ff:aa:ff:aa:ff:aa","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.2","ipv4_dst":"10.0.0.1","tcp_src":10000,"tcp_dst":10001}],"path":["link0121"],"edge_actions":{"node011":[{"type":"FlowActionOutput","output":"port0111"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ff:ab:ff:ab:ff:ab"}}]}}'


echo "------ get lower flows -------" 
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/lower_flows -X GET 

echo "------ get layerized links -------" 
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/layerized_links -X GET 

sleep 3

echo "------ lower network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/lower-nw/topology -X GET | python -mjson.tool
sleep 1 
echo "------ upper network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/upper-nw/topology -X GET | python -mjson.tool
sleep 1 
echo "------ layerized network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/layerized-nw/topology -X GET | python -mjson.tool

# packet
echo "------ POST InPacket -------" 
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr1":"test_attr_value1"}}'
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node003", "port":"port0032" , "header":{"type":"OFPFlowMatch", "in_node":"node003", "in_port":"port0032", "eth_src":"66:77:88:99:00:11", "eth_dst":"00:11:22:33:44:55"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr2":"test_attr_value2"}}'
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr1":"test_attr_value1"}}'


echo "------ Get Packet(upper-nw) -------" 
sleep 1 
curl http://$ADDRESS:10080/upper-nw/packets -X GET | python -mjson.tool
echo "------ Get Packet(layerized-nw) -------" 
sleep 1 
curl http://$ADDRESS:10080/layerized-nw/packets -X GET | python -mjson.tool

echo "------ Get Flows(lower-nw) -------" 
sleep 1 
curl http://$ADDRESS:10080/lower-nw/flows -X GET | python -mjson.tool
echo "------ Get Flows(upper-nw) -------" 
sleep 1 
curl http://$ADDRESS:10080/upper-nw/flows -X GET | python -mjson.tool
echo "------ Get Flows(layerized-nw) -------" 
sleep 1 
curl http://$ADDRESS:10080/layerized-nw/flows -X GET | python -mjson.tool


echo "------ put upper_link_sync (false) -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/upper_link_sync -X PUT -d '{"sync":false}' 

echo "---------------- flow delete (lower-nw)-------------------"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/flows/flow01 -X DELETE
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/flows/flow02 -X DELETE

echo "------ lower network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/lower-nw/topology -X GET | python -mjson.tool
sleep 1 
echo "------ upper network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/upper-nw/topology -X GET | python -mjson.tool
sleep 1 
echo "------ layerized network's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/layerized-nw/topology -X GET | python -mjson.tool

echo ""
echo "*************************************************"
echo "***************[ Delete ]************************"
echo "*************************************************"
echo ""
sleep 1 


#
# Delete OutPacket 
# 
echo ""
echo "////////////////////////////////////////////////////"
echo "////////// Delete Outpacket to upper-nw  ///////////"
echo "////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/out/0000000000 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/out/0000000001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/packets/out/0000000002 -X DELETE

echo "///////////////////////////////////////////////"
echo "////////// Delete boundary ////////////////////"
echo "///////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/boundaries/boundary1 -X DELETE 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/linklayerizer/settings/boundaries/boundary2 -X DELETE 

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Link to upper-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/links/link0012 -X DELETE
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/links/link0021 -X DELETE

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Link to lower-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/links/link0112 -X DELETE
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/links/link0121 -X DELETE

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Port to upper-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/physical_ports/0011@001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001/ports/port0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001/ports/port0013 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/physical_ports/0021@002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0022 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node002/ports/port0024 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/physical_ports/0031@003 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003/ports/port0032 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003/ports/port0033 -X DELETE

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Port to lower-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/physical_ports/0111@011 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node011/ports/port0112 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/physical_ports/0121@012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node012/ports/port0122 -X DELETE

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Node to upper-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/physical_nodes/002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/upper-nw/topology/nodes/node003 -X DELETE

echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Node to lower-nw    ////////////////////"
echo "//////////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/nodes/node011 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/lower-nw/topology/physical_nodes/012 -X DELETE

echo "////////////////////////////////////////////////////////"
echo "////////// Check topology           ////////////////////"
echo "////////////////////////////////////////////////////////"
sleep 3 
echo "------ Check layerized-nw's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/layerized-nw/topology -X GET | python -mjson.tool
echo "------ Check upper-nw's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/upper-nw/topology -X GET | python -mjson.tool
echo "------ Check lower-nw's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/lower-nw/topology -X GET | python -mjson.tool

echo "//////////////////////////////////////////////////"
echo "////////// Delete Connections ////////////////////"
echo "//////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_layerized-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_upper-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/linklayerizer_lower-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_layerized-nw -X DELETE

echo "//////////////////////////////////////////////////"
echo "////////// Delete Components /////////////////////"
echo "//////////////////////////////////////////////////"
sleep 3 
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/layerized-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/upper-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lower-nw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/linklayerizer -X DELETE

echo "------ Check Components -------" 
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool

echo "------ Check Connections -------" 
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool



