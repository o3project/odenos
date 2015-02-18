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

# [LearningSwitch] ---- (Network01) ---- [Slicer] ----  (Network00)  
#                                     |
# [LearningSwitch] ---- (Network02) --| 

# create NetworkComponent , Aggregator , and other Component
# curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network00 -X PUT -d '{"type": "Network", "id": "network00", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network01 -X PUT -d '{"type": "Network", "id": "network01", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network02 -X PUT -d '{"type": "Network", "id": "network02", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw01 -X PUT -d '{"type": "LearningSwitch", "id": "lsw01", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw02 -X PUT -d '{"type": "LearningSwitch", "id": "lsw02", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/slicer -X PUT -d '{"type": "Slicer", "id": "slicer", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

sleep 1

# connect Components
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw00 -X PUT -d '{"id": "slicer_nw00", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "slicer", "network_id":"network00"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw01 -X PUT -d '{"id": "slicer_nw01", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "slicer", "network_id":"network01"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw02 -X PUT -d '{"id": "slicer_nw02", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "slicer", "network_id":"network02"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw01 -X PUT -d '{"id": "lsw_nw01", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "lsw01", "network_id":"network01"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw02 -X PUT -d '{"id": "lsw_nw02", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "lsw02", "network_id":"network02"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool

sleep 1

# make Topology
#                                                  [port0023]
#   [port0011](node001)[port0012] -----  [port0021](node002)[port0022] ----  [port0031](node003)[port0032]
#             [port0013]                           [port0024]                          [port0033]
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"001", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002 -X PUT -d '{"node_id": "node002", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"002", "vendor":"VENDOR_A"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003 -X PUT -d '{"node_id": "node003", "type": "Node", "version": "0", "ports": {}, "attributes": {"admin_status":"UP", "oper_status":"UP", "physical_id":"003", "vendor":"VENDOR_A"}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {"physical_id":"0011@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {"physical_id":"0012@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {"physical_id":"0013@001"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {"physical_id":"0021@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {"physical_id":"0022@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {"physical_id":"0023@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0024 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0024", "out_link": null, "in_link": null, "attributes": {"physical_id":"0024@002"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0031 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0031", "out_link": null, "in_link": null, "attributes": {"physical_id":"0031@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0032 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0032", "out_link": null, "in_link": null, "attributes": {"physical_id":"0032@003"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0033 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0033", "out_link": null, "in_link": null, "attributes": {"physical_id":"0033@003"}}'

sleep 1

curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0012", "dst_node": "node002", "dst_port": "port0021", "attributes": {"attr0021":"value0021"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0012", "attributes": {"attr0012":"value0012"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0023 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0023", "src_node": "node002", "src_port": "port0022", "dst_node": "node003", "dst_port": "port0031", "attributes": {"attr0031":"value0031"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0032 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0032", "src_node": "node003", "src_port": "port0031", "dst_node": "node002", "dst_port": "port0022", "attributes": {"attr0022":"value0022"}}'

sleep 1 

echo "------ original network -------" 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
sleep 1 
echo "------ sliver network1 -------" 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool
echo "------ sliver network2 -------" 
curl http://$ADDRESS:10080/systemmanager/components/network02/topology -X GET | python -mjson.tool

echo "------ create slicer conditions -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/40/conditions/sliceconditon1 -X PUT -d '{"id":"sliceconditon1", "type":"BasicSliceCondition", "connection":"slicer_nw01", "in_node":"node001","in_port":"port0011"}' 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/40/conditions/sliceconditon2 -X PUT -d '{"id":"sliceconditon2", "type":"BasicSliceCondition", "connection":"slicer_nw01", "in_node":"node003","in_port":"port0032"}' 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/50 -X PUT -d '[{"id":"sliceconditon3", "type":"BasicSliceCondition", "connection":"slicer_nw02", "in_node":"node001","in_port":"port0013"}, {"id":"sliceconditon4", "type":"BasicSliceCondition", "connection":"slicer_nw02", "in_node":"node003","in_port":"port0033"}]' 

sleep 2 

echo "------ get slicer conditions -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/10 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/20 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/conditions/sliceconditon1 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/conditions/sliceconditon2 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/connections/slicer_nw01 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/slicer/settings/slice_condition_table/connections/slicer_nw02 -X GET 

sleep 1

# packet
echo "------ POST InPacket -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr":"test_attr_value"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node003", "port":"port0032" , "header":{"type":"OFPFlowMatch", "in_node":"node003", "in_port":"port0032", "eth_src":"66:77:88:99:00:11", "eth_dst":"00:11:22:33:44:55"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr":"test_attr_value"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0011" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0011", "eth_src":"00:11:22:33:44:55", "eth_dst":"66:77:88:99:00:11"}, "data":"ABCDEFGHIJKLMN", "attributes": {"test_attr":"test_attr_value"}}'

curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0013" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0013", "eth_src":"ff:11:22:33:44:55", "eth_dst":"ee:cc:88:99:00:11"}, "data":"12345678abcdefg", "attributes": {"test_attr2":"test_attr_value2"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node003", "port":"port0033" , "header":{"type":"OFPFlowMatch", "in_node":"node003", "in_port":"port0033", "eth_src":"ee:cc:88:99:00:11", "eth_dst":"ff:11:22:33:44:55"}, "data":"12345678abcdefg", "attributes": {"test_attr2":"test_attr_value2"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0013" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0013", "eth_src":"ff:11:22:33:44:55", "eth_dst":"ee:cc:88:99:00:11"}, "data":"12345678abcdefg", "attributes": {"test_attr2":"test_attr_value2"}}'

sleep 1 

echo "------ Get In/OutPacket(network00) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets -X GET 
echo "------ Get In/OutPacket(network01) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network01/packets -X GET 
echo "------ Get In/OutPacket(network02) -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network02/packets -X GET 

echo "---------------- flow (network00)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network00/flows -X GET | python -mjson.tool
echo "---------------- flow (network01)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network01/flows -X GET | python -mjson.tool
echo "---------------- flow (network02)-------------------"
curl http://$ADDRESS:10080/systemmanager/components/network02/flows -X GET | python -mjson.tool

sleep 1 

echo ""
echo "*************************************************"
echo "***************[ Result ]************************"
echo "*************************************************"
echo ""

sleep 2 
echo "/////////////////////////////////////////////////"
echo "//////////////// search Node ////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes?'attributes="physical_id=001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes?'attributes="physical_id=002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes?'attributes="physical_id=003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes?'attributes="physical_id=001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes?'attributes="physical_id=002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes?'attributes="physical_id=003"' -X GET

sleep 2 
echo "/////////////////////////////////////////////////"
echo "//////////////// search Port ////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports?'attributes="physical_id=0011@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports?'attributes="physical_id=0012@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports?'attributes="physical_id=0013@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports?'attributes="physical_id=0021@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports?'attributes="physical_id=0022@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports?'attributes="physical_id=0023@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports?'attributes="physical_id=0031@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports?'attributes="physical_id=0032@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports?'attributes="physical_id=0033@003"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports?'attributes="physical_id=0011@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports?'attributes="physical_id=0012@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node001/ports?'attributes="physical_id=0013@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports?'attributes="physical_id=0021@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports?'attributes="physical_id=0022@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node002/ports?'attributes="physical_id=0023@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node003/ports?'attributes="physical_id=0031@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node003/ports?'attributes="physical_id=0032@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/nodes/node003/ports?'attributes="physical_id=0033@003"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node001/ports?'attributes="physical_id=0011@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node001/ports?'attributes="physical_id=0012@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node001/ports?'attributes="physical_id=0013@001"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node002/ports?'attributes="physical_id=0021@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node002/ports?'attributes="physical_id=0022@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node002/ports?'attributes="physical_id=0023@002"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node003/ports?'attributes="physical_id=0031@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node003/ports?'attributes="physical_id=0032@003"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/nodes/node003/ports?'attributes="physical_id=0033@003"' -X GET

sleep 2 
echo "/////////////////////////////////////////////////"
echo "//////////////// search Link ////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links?'attributes="attr0021=value0021"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links?'attributes="attr0012=value0012"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links?'attributes="attr0031=value0031"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links?'attributes="attr0022=value0022"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links?'attributes="attr0021=value0021"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links?'attributes="attr0012=value0012"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links?'attributes="attr0031=value0031"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/topology/links?'attributes="attr0022=value0022"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0021=value0021"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0012=value0012"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0031=value0031"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/topology/links?'attributes="attr0022=value0022"' -X GET

sleep 2 
echo "/////////////////////////////////////////////////"
echo "//////////////// search OutPacket ///////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out?'attributes="test_attr=test_attr_value"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out?'attributes="test_attr2=test_attr_value2"' -X GET

sleep 2 
echo "/////////////////////////////////////////////////"
echo "//////////////// search Flow ////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0011"&actions="type=FlowActionOutput,edge_node=node003,output=port0032"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0013"&actions="type=FlowActionOutput,edge_node=node003,output=port0033"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node003,in_port=port0032"&actions="type=FlowActionOutput,edge_node=node001,output=port0011"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network00/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node003,in_port=port0033"&actions="type=FlowActionOutput,edge_node=node001,output=port0013"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0011"&actions="type=FlowActionOutput,edge_node=node003,output=port0032"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network01/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node003,in_port=port0032"&actions="type=FlowActionOutput,edge_node=node001,output=port0011"' -X GET

curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node001,in_port=port0013"&actions="type=FlowActionOutput,edge_node=node003,output=port0033"' -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network02/flows?'type=OFPFlow&match="type=OFPFlowMatch,in_node=node003,in_port=port0033"&actions="type=FlowActionOutput,edge_node=node001,output=port0013"' -X GET

echo ""
echo "***********************************************"
echo "***************[ Delete ]**********************"
echo "***********************************************"
echo ""
sleep 2 

echo ""
echo "////////////////////////////////////////////////////"
echo "////////// Delete Outpacket to original  ///////////"
echo "////////////////////////////////////////////////////"
sleep 2
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000000 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000003 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000004 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/packets/out/0000000005 -X DELETE

echo ""
echo "////////////////////////////////////////////////////"
echo "////////// Delete Topology to original  ///////////"
echo "////////////////////////////////////////////////////"
sleep 2 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/links/link0032 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/physical_ports/0011@001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0012 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001/ports/port0013 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0021 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0022 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0023 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002/ports/port0024 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0031 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0032 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node003/ports/port0033 -X DELETE
sleep 1 
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node001 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/nodes/node002 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network00/topology/physical_nodes/003 -X DELETE
sleep 1 

echo "------ Check network00's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network00/topology -X GET | python -mjson.tool
echo "------ Check network01's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network01/topology -X GET | python -mjson.tool
echo "------ Check network02's Topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/network02/topology -X GET | python -mjson.tool

sleep 1 
echo ""
echo "/////////////////////////////////////////"
echo "////////// Delete Connections ///////////"
echo "/////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw00 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/slicer_nw02 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/lsw_nw02 -X DELETE

sleep 3 
echo ""
echo "/////////////////////////////////////////"
echo "////////// Delete Components  ///////////"
echo "/////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network00 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network02 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw01 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw02 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/slicer -X DELETE

echo "------ Check Components -------" 
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool

echo "------ Check Connections -------" 
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool


