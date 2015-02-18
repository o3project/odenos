#!bin/sh

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
ADDRESS=0.0.0.0


# create NetworkComponent and LinkLayerizer
curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X PUT -d '{"type": "Network", "id": "network0", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network1 -X PUT -d '{"type": "Network", "id": "network1", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network2 -X PUT -d '{"type": "Network", "id": "network2", "cm_id": "romgr1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/slicer1 -X PUT -d '{"type": "Slicer", "id": "slicer1", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections -X POST -d '{"id": "conn0", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "slicer1", "network_id":"network0"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections -X POST -d '{"id": "conn1", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "slicer1", "network_id":"network1"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections -X POST -d '{"id": "conn2", "type": "LogicAndNetwork", "connection_type": "sliver", "logic_id": "slicer1", "network_id":"network2"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool

sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X PUT -d '{"id": "node001", "type": "Node", "version: "0", "ports": {}, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002 -X PUT -d '{"id": "node002", "type": "Node", "version: "0", "ports": {}, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003 -X PUT -d '{"id": "node003", "type": "Node", "version: "0", "ports": {}, "attributes": {}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0013 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0013", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0021 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0021", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0022 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0022", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0023 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0023", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0024 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node002", "port_id": "port0024", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0031 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0031", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0032 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0032", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0033 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node003", "port_id": "port0033", "out_link": null, "in_link": null, "attributes": {}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0012 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0012", "src_node": "node001", "src_port": "port0011", "dst_node": "node002", "dst_port": "port0021", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0021 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0021", "src_node": "node002", "src_port": "port0021", "dst_node": "node001", "dst_port": "port0011", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0023 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0023", "src_node": "node002", "src_port": "port0022", "dst_node": "node003", "dst_port": "port0031", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0032 -X PUT -d '{"type": "Link", "version": "0", "link_id": "link0032", "src_node": "node003", "src_port": "port0031", "dst_node": "node002", "dst_port": "port0022", "attributes": {}}'

curl http://$ADDRESS:10080/systemmanager/components/network1/topology -X GET | python -mjson.tool

curl http://$ADDRESS:10080/systemmanager/components/network2/topology -X GET | python -mjson.tool

# slice
#curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0 -X PUT -d '{"id":"slice1", "type":"BasicSliceCondition", "connection":"conn1" , "in_node":"node001", "in_port":"port0011"}'
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0/conditions/con11 -X PUT -d '{"id":"con11", "type":"BasicSliceCondition", "connection":"conn1" , "in_node":"node001", "in_port":"port0011"}'
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0/conditions/con12 -X PUT -d '{"id":"con12", "type":"BasicSliceCondition", "connection":"conn1" , "in_node":"node001", "in_port":"port0012"}'
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0/conditions/con13 -X PUT -d '{"id":"con13", "type":"BasicSliceCondition", "connection":"conn1" , "in_node":"node001", "in_port":"port0013"}'
sleep 4
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/1 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/conditions/slice1 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/connections/conn1 -X GET
#curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/0/conditions/slice1 -X PUT -d '{"id":"slice1", "type":"BasicSliceCondition", "connection":"conn1" , "in_node":"node001", "in_port":"port0013"}'
#curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/1 -X PUT -d '{"id":"slice2", "type":"BasicSliceCondition", "connection":"conn2" , "in_node":"node003", "in_port":"port0031"}'
#curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/1/conditions/slice1 -X PUT -d '{"id":"slice2", "type":"BasicSliceCondition", "connection":"conn2" , "in_node":"node003", "in_port":"port0032"}'
#curl -w "$FORMAT" http://$ADDRESS:10080/slicer1/settings/slice_condition_table/1/conditions/slice1 -X PUT -d '{"id":"slice2", "type":"BasicSliceCondition", "connection":"conn2" , "in_node":"node003", "in_port":"port0033"}'

# packet
echo "------ InPacket1 -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"InPacket", "node":"node001", "port":"port0012" , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"InPacket", "node":"node001", "port":"port0012" , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0012" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OutPacket", "node":"node001", "ports":["port0013"] , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0013"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OutPacket", "node":"node001", "ports-except":["port0012"] , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0013"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OFPInPacket", "node":"node001", "ports":["port0013"] , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"ABCDEFGHIJKLMN", "attributes": {}}'

echo "------ Get InPacket -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000000 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000001 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000002 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000000 -X DELETE 
echo "------ Get OutPacket  -------" 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000000 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000001 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000002 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000000 -X DELETE

#curl http://$ADDRESS:10080/systemmanager/components/network1/flows -X GET | python -mjson.tool

# print results
echo "---------------- Results -------------------"
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network2/topology -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network2/flows -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network1/packets -X GET | python -mjson.tool

# delete network
sleep 3
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/conn0 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/conn1 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/conn2 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network1 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network2 -X DELETE
#curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/slicer1 -X DELETE
