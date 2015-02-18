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
ADDRESS=127.0.0.1

# [LearningSwitch] ---- (Network02) ---- [Federator] ---- (Network00)  
#                                                     |
#                                                     |-- (Network01)

# create NetworkComponent , Federator, and other Component
curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network00 -X PUT -d '{"type": "Network", "id": "network00", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network01 -X PUT -d '{"type": "Network", "id": "network01", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network02 -X PUT -d '{"type": "Network", "id": "network02", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X PUT -d '{"type": "LearningSwitch", "id": "lsw", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/federator -X PUT -d '{"type": "Federator", "id": "federator", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw00 -X PUT -d '{"id": "federator_nw00", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "federator", "network_id":"network00"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw01 -X PUT -d '{"id": "federator_nw01", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "federator", "network_id":"network01"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw02 -X PUT -d '{"id": "federator_nw02", "type": "LogicAndNetwork", "connection_type": "federated", "logic_id": "federator", "network_id":"network02"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw01 -X PUT -d '{"id": "lsw_nw01", "type": "LogicAndNetwork", "connection_type": "lsw_conn", "logic_id": "lsw", "network_id":"network02"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool

# make Topology
#
# - "network00"
#                                                    [port0023]
#     [port0011](node001)[port0012] -----  [port0021](node002)[port0022]
#               [port0013]                           [port0024]        
#
# - "network01" 
#               [port0013]
#     [port0011](node001)[port0012] -----  [port0021](node002)[port0022]
#               [port0014]                           [port0023]        

## network00's Topology
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"001", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002 -X PUT -d '{"node_id": "node002", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"002", "vendor":"VENDOR_A"}}'

sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {"physical_id":"0011@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {"physical_id":"0012@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {"physical_id":"0013@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {"physical_id":"0021@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {"physical_id":"0022@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {"physical_id":"0023@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0024 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0024", "out_link": null, "in_link": null, "attributes": {"physical_id":"0024@002"}}'

## network01's Topology
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"001", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002 -X PUT -d '{"node_id": "node002", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"002", "vendor":"VENDOR_A"}}'

sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {"physical_id":"0011@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {"physical_id":"0012@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {"physical_id":"0013@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0014 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0014", "out_link": null, "in_link": null, "attributes": {"physical_id":"0014@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {"physical_id":"0021@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {"physical_id":"0022@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {"physical_id":"0023@002"}}'

sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0012", "dst_node": "node002", "dst_port": "port0021", "attributes": {"attr0021":"value0021"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0012", "attributes": {"attr0012":"value0012"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0012", "dst_node": "node002", "dst_port": "port0021", "attributes": {"attr0021":"value0021"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0012", "attributes": {"attr0012":"value0012"}}'

echo "------ original network1 -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
echo "------ original network2 -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool
echo "------ federated network -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network02/topology -X GET | python -mjson.tool

# make FederatorBoundary 
#
#          "network00"                         "network01"
#
#           [port0023]                          [port0013]
# [port0021](node002)[port0022] ----- [port0011](node001)[port0012]
#           [port0024]                          [port0023]
#               
echo "------ create federator bundary -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/federator/settings/boundaries/federator_boundary1 -X PUT -d '{"id":"federator_boundary1", "type":"Federator", "network1":"network00", "network2":"network01", "node1":"node002", "node2":"node001", "port1":"port0022", "port2":"port0011"}' 

echo "------ get federator bundary -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/federator/settings/boundaries -X GET 

echo "------ original network1 -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
echo "------ original network2 -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool
echo "------ federated network -------" 
sleep 1 
curl http://$ADDRESS:10080/systemmanager/components/network02/topology -X GET | python -mjson.tool

# packet
echo "------ POST InPacket [case1] -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0013" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0013", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr1":"test_attr_value1"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node002", "port":"port0022" , "header":{"type":"OFPFlowMatch", "in_node":"node002", "in_port":"port0022", "eth_src":"66:77:88:99:00:11", "eth_dst":"00:11:22:33:44:55"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr2":"test_attr_value2"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0013" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0013", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr3":"test_attr_value3"}}'

echo "------ POST InPacket [case2] -------" 
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0014" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0014", "eth_src":"55:66:77:88:99:aa", "eth_dst":"99:aa:bb:cc:dd:ee"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr4":"test_attr_value4"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0013" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0013", "eth_src":"99:aa:bb:cc:dd:ee", "eth_dst":"55:66:77:88:99:aa"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr5":"test_attr_value5"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0014" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0014", "eth_src":"55:66:77:88:99:aa", "eth_dst":"99:aa:bb:cc:dd:ee"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr6":"test_attr_value6"}}'

echo "------ POST InPacket (in_port is boundary_port) [case3] -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node002", "port":"port0022" , "header":{"type":"OFPFlowMatch", "in_node":"node002", "in_port":"port0022", "eth_src":"ff:aa:ff:aa:ff:aa", "eth_dst":"bb:ff:bb:ff:bb:ff"}, "data":"ABCABCABCABC"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"cc:aa:cc:aa:cc:aa", "eth_dst":"bb:cc:bb:cc:bb:cck"}, "data":"CBACBACBA"}'

echo "------ Get Packet(network00) -------" 
sleep 1 
curl http://$ADDRESS:10080/network00/packets -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network00/packets/out/0000000000 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network00/packets/out/0000000001 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network00/packets/out/0000000002 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network00/packets/in/0000000002 -X GET | python -mjson.tool
echo "------ Get Packet(network01) -------" 
sleep 1 
curl http://$ADDRESS:10080/network01/packets -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network01/packets/out/0000000000 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network01/packets/out/0000000001 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network01/packets/out/0000000002 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network01/packets/out/0000000003 -X GET | python -mjson.tool
curl http://$ADDRESS:10080/network01/packets/in/0000000004 -X GET | python -mjson.tool
echo "------ Get Packet(network02) -------" 
sleep 1 
curl http://$ADDRESS:10080/network02/packets -X GET | python -mjson.tool

echo "------ Get Flows(network00) -------" 
sleep 1 
curl http://$ADDRESS:10080/network00/flows -X GET | python -mjson.tool
echo "------ Get Flows(network01) -------" 
sleep 1 
curl http://$ADDRESS:10080/network01/flows -X GET | python -mjson.tool
echo "------ Get Flows(network02) -------" 
sleep 1 
curl http://$ADDRESS:10080/network02/flows -X GET | python -mjson.tool

echo "------ PUT Flow (to federated_network) -------" 
sleep 1 
curl -w "$FORMAT" http://localhost:10080/network02/flows/flow01 -X PUT -d '{"version":"0","flow_id":"flow01","owner":"logic","enabled":true,"status":"establishing","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"network00_node001","in_port":"network00_node001_port0013","eth_src":"ff:aa:ff:aa:ff:aa","eth_dst":"bb:ff:bb:ff:bb:ff","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.1","ipv4_dst":"10.0.0.2","tcp_src":10000,"tcp_dst":10001}],"path":["network00_link0012","federator_boundary1_link01","network01_link0012"],"edge_actions":{"network01_node002":[{"type":"FlowActionOutput","output":"network01_node002_port0022"},{"type":"OFPFlowActionSetField","match": {"type":"OFPFlowMatch","eth_src":"ab:ff:ab:ff:ab:ff"}}]}}'
curl -w "$FORMAT" http://localhost:10080/network02/flows/flow02 -X PUT -d '{"version":"0","flow_id":"flow02","owner":"logic","enabled":true,"status":"established","attributes":{"latency":"0", "req_latency":"0", "bandwidth":"0"},"type":"OFPFlow","idle_timeout":90,"hard_timeout":90,"matches":[{"type":"OFPFlowMatch","in_node":"network01_node002","in_port":"network01_node002_port0022","eth_src":"bb:ff:bb:ff:bb:ff","eth_dst":"ff:aa:ff:aa:ff:aa","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.0.2","ipv4_dst":"10.0.0.1","tcp_src":10000,"tcp_dst":10001}],"path":["network01_link0021","federator_boundary1_link02","network00_link0021"],"edge_actions":{"network00_node001":[{"type":"FlowActionOutput","output":"network00_node001_port0013"}]}}'
curl -w "$FORMAT" http://localhost:10080/network02/flows/flow03 -X PUT -d '{"version":"0","flow_id":"flow03","owner":"logic","enabled":true,"status":"establishing","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"network00_node001","in_port":"network00_node001_port0013","eth_src":"11:aa:11:aa:11:aa","eth_dst":"bb:11:bb:11:bb:11","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.10.1","ipv4_dst":"10.0.10.2","tcp_src":10000,"tcp_dst":10001}],"path":[],"edge_actions":{"network00_node001":[{"type":"FlowActionOutput","output":"network00_node001_port0011"}]}}'
# update flow
curl -w "$FORMAT" http://localhost:10080/network00/flows/flow03 -X PUT -d '{"version":"1","flow_id":"flow03","owner":"logic","enabled":true,"status":"established","attributes":{"bandwidth":"0", "req_bandwidth":"0", "latency":"0"},"type":"OFPFlow","idle_timeout":60,"hard_timeout":60,"matches":[{"type":"OFPFlowMatch","in_node":"node001","in_port":"port0013","eth_src":"11:aa:11:aa:11:aa","eth_dst":"bb:11:bb:11:bb:11","vlan_vid":0,"vlan_pcp":0,"eth_type":2048,"ip_proto":6,"ipv4_src":"10.0.10.1","ipv4_dst":"10.0.10.2","tcp_src":10000,"tcp_dst":10001}],"path":[],"edge_actions":{"node001":[{"type":"FlowActionOutput","output":"port0011"}]}}'

echo "------ Get Flows(network00) -------" 
sleep 1 
curl http://$ADDRESS:10080/network00/flows -X GET | python -mjson.tool
echo "------ Get Flows(network01) -------" 
sleep 1 
curl http://$ADDRESS:10080/network01/flows -X GET | python -mjson.tool
echo "------ Get Flows(network02) -------" 
sleep 1 
curl http://$ADDRESS:10080/network02/flows -X GET | python -mjson.tool

echo ""
echo "*************************************************"
echo "***************[ Result ]************************"
echo "*************************************************"
echo ""
sleep 2

echo "///////////////////////////////////////////////////////"
echo "////////// search Node to federated_network ///////////"
echo "///////////////////////////////////////////////////////"
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes?'attributes="physical_id=001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes?'attributes="physical_id=002"' -X GET

echo "///////////////////////////////////////////////////////"
echo "////////// search Port to federated_network ///////////"
echo "///////////////////////////////////////////////////////"
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node001/ports?'attributes="physical_id=0011@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node001/ports?'attributes="physical_id=0012@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node001/ports?'attributes="physical_id=0013@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node001/ports?'attributes="physical_id=0011@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node001/ports?'attributes="physical_id=0012@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node001/ports?'attributes="physical_id=0013@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node001/ports?'attributes="physical_id=0014@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node002/ports?'attributes="physical_id=0021@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node002/ports?'attributes="physical_id=0022@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node002/ports?'attributes="physical_id=0023@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network00_node002/ports?'attributes="physical_id=0024@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node002/ports?'attributes="physical_id=0021@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node002/ports?'attributes="physical_id=0022@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/network01_node002/ports?'attributes="physical_id=0023@002"' -X GET

echo "///////////////////////////////////////////////////////"
echo "////////// search Link to federated_network ///////////"
echo "///////////////////////////////////////////////////////"
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0021=value0021"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0012=value0012"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links/federator_boundary1_link01 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links/federator_boundary1_link02 -X GET

echo "//////////////////////////////////////////////////////"
echo "////////// search Flow to original_network ///////////"
echo "//////////////////////////////////////////////////////"
sleep 2 
# LeanningSwitch created flow.
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0011"&actions="type=FlowActionOutput,edge_node=node002,output=port0024"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node002,in_port=port0024"&actions="type=FlowActionOutput,edge_node=node001,output=port0011"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0014"&actions="type=FlowActionOutput,edge_node=node001,output=port0013"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0013"&actions="type=FlowActionOutput,edge_node=node001,output=port0014"' -X GET
# GET flow to federated_network.
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0011"&actions="type=FlowActionOutput,edge_node=node002,output=port0022"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0011"&actions="type=FlowActionOutput,edge_node=node002,output=port0022"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node002,in_port=port0022"&actions="type=FlowActionOutput,edge_node=node001,output=port0011"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node002,in_port=port0022"&actions="type=FlowActionOutput,edge_node=node001,output=port0011"' -X GET

echo "///////////////////////////////////////////////////////////"
echo "////////// search OutPacket to original_network ///////////"
echo "///////////////////////////////////////////////////////////"
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out?'attributes="test_attr1=test_attr_value1"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out?'attributes="test_attr2=test_attr_value2"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out?'attributes="test_attr3=test_attr_value3"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out?'attributes="test_attr4=test_attr_value4"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out?'attributes="test_attr5=test_attr_value5"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out?'attributes="test_attr6=test_attr_value6"' -X GET

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
echo "////////// Delete Outpacket to original  ///////////"
echo "////////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000000 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in/0000000002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out/0000000000 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out/0000000001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out/0000000002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/out/0000000003 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets/in/0000000004 -X DELETE

#
# Delete Flow 
# 
echo ""
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[flow info]  ///////////"
echo "////////// (before Delete Flow)            ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_flow -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_flow -X GET | python -mjson.tool
echo "////////////////////////////////////////////////"
echo "////////// Delete Flow to federated  ///////////"
echo "////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows/lsw_0 -X DELETE 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows/lsw_1 -X DELETE 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows/flow01 -X DELETE 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows/flow02 -X DELETE 
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[flow info]  ///////////"
echo "////////// (after Delete Flow)             ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_flow -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_flow -X GET | python -mjson.tool
echo ""

#
# Delete FederatorBoundary. 
# 
echo ""
echo "///////////////////////////////////////////////"
echo "////////// Check federator bundary  ///////////"
echo "////////// (before Delete boundary)  ///////////"
echo "///////////////////////////////////////////////"
curl http://$ADDRESS:10080/federator/settings/boundaries -X GET | python -mjson.tool
sleep 1 
echo "///////////////////////////////////////////////"
echo "////////// Delete boundary ////////////////////"
echo "///////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/federator/settings/boundaries/federator_boundary1 -X DELETE 
echo "------  (after Delete boundary) -------" 
echo "///////////////////////////////////////////////"
echo "////////// Check federator bundary  ///////////"
echo "////////// (after Delete boundary)  ///////////"
echo "///////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/settings/boundaries -X GET | python -mjson.tool
echo ""

#
# Delete Toplogy.
# 

## delete link
echo ""
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[link info]  ///////////"
echo "////////// (before Delete Link.)           ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_link -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_link -X GET | python -mjson.tool
sleep 1 
echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Link to original_nw ////////////////////"
echo "//////////////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links/link0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links/link0021 -X DELETE
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[link info]  ///////////"
echo "////////// (after Delete Link.)            ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_link -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_link -X GET | python -mjson.tool
echo ""

## delete port 
echo ""
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[port info]  ///////////"
echo "////////// (before Delete Port.)           ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1
curl http://$ADDRESS:10080/federator/federated_network_port -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_port -X GET | python -mjson.tool
sleep 1 
echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Port to original_nw ////////////////////"
echo "//////////////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/physical_ports/0011@001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0013 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0022 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0024 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/physical_ports/0011@001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0013 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports/port0014 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0022 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports/port0023 -X DELETE
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[port info]  ///////////"
echo "////////// (after Delete Port.)            ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1
curl http://$ADDRESS:10080/federator/federated_network_port -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_port -X GET | python -mjson.tool
echo ""

## delete node 
echo ""
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[node info]  ///////////"
echo "////////// (before Delete Node.)           ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_node -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_node -X GET | python -mjson.tool
sleep 1 
echo "//////////////////////////////////////////////////////////"
echo "////////// Delete Node to original_nw ////////////////////"
echo "//////////////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/physical_nodes/002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/physical_nodes/002 -X DELETE
sleep 1 
echo "//////////////////////////////////////////////////////"
echo "////////// Check federator API[node info]  ///////////"
echo "////////// (after Delete Node.)            ///////////"
echo "//////////////////////////////////////////////////////"
sleep 1 
curl http://$ADDRESS:10080/federator/federated_network_node -X GET | python -mjson.tool
curl http://$ADDRESS:10080/federator/original_network_node -X GET | python -mjson.tool
echo ""

echo "////////////////////////////////////////////////////////"
echo "////////// Check topology           ////////////////////"
echo "////////// (original and federated) ////////////////////"
echo "////////////////////////////////////////////////////////"
sleep 3 
echo "------ Check network00's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
echo "------ Check network01's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool
echo "------ Check network02's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network02/topology -X GET | python -mjson.tool

echo "//////////////////////////////////////////////////"
echo "////////// Delete Connections ////////////////////"
echo "//////////////////////////////////////////////////"
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw00 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/federator_nw02 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw01 -X DELETE

echo "//////////////////////////////////////////////////"
echo "////////// Delete Components /////////////////////"
echo "//////////////////////////////////////////////////"
sleep 3 
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network00 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network02 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/federator -X DELETE

echo "------ Check Components -------" 
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool

echo "------ Check Connections -------" 
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool


