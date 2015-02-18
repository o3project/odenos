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

# create NetworkComponent
echo "------ Create Network Component -------"
curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X PUT -d '{"type": "Network", "id": "network0", "cm_id": "romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X PUT -d '{"node_id": "node001", "type": "Node", "version": "0", "ports": {}, "attributes": {}}'
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0011", "out_link": null, "in_link": null, "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X PUT -d '{"type": "Port", "version": "0", "node_id": "node001", "port_id": "port0012", "out_link": null, "in_link": null, "attributes": {}}'
sleep 1

curl http://$ADDRESS:10080/systemmanager/components/network1/topology -X GET | python -mjson.tool

# packet
echo "------ InPacket1 -------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"InPacket", "node":"node001", "port":"port0012" , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"InPacket", "node":"node001", "port":"port0012" , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OutPacket", "node":"node001", "ports":["port0012"] , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OutPacket", "node":"node001", "ports-except":["port0012"] , "header":{"type":"BasicFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X POST -d '{"type":"OFPInPacket", "node":"node001", "port":"port0012" , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X POST -d '{"type":"OFPInPacket", "node":"node001", "ports":["port0012"] , "header":{"type":"OFPFlowMatch", "in_node":"node001", "in_port":"port0012"}, "data":"", "attributes": {}}'

echo "------ Get PacketStatus -------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets -X GET 
echo "------ Get InPacket -------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000000 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000001 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000002 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/0000000000 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/in/head -X DELETE
echo "------ Get OutPacket  -------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000000 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000001 -X GET
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000002 -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X GET 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/0000000000 -X DELETE 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X DELETE 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets/out/head -X DELETE 
echo "------ Get PacketStatus -------"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/packets -X GET 

echo ""
echo ""
echo "***********************************************"
echo "***************[ Delete ]**********************"
echo "***********************************************"
echo ""

echo "---------------- Delete Topology (network0)-------------------"
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X DELETE
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X DELETE
sleep 1
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X DELETE

sleep 1
echo "------ Check network0's Topology -------"
echo ">> GET <base_uri>/topology"
curl http://$ADDRESS:10080/systemmanager/components/network0/topology -X GET | python -mjson.tool

sleep 1
echo "------ Delete Component -------"
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X DELETE

echo "------ Check Component -------"
echo ">> GET <base_uri>/systemmanager/components"
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool

