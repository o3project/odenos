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

# (network1) ---- [Aggregator] ----  (network0) 

# create NetworkComponent , Aggregator 
#curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X PUT -d '{"type":"Network","id":"network0","cm_id":"romgr1"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

sleep 1
echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// PUT Node ///////////////////////"
echo "/////////////////////////////////////////////////"
# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X PUT -d '{"node_id":"node001","type":"Node","version":"0","ports":{},"attributes":{"physical_id":"001","oper_status":"UP"}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001 -X PUT -d '{"node_id":"node001","type":"Node","version":"1","ports":{},"attributes":{"physical_id":"001","oper_status":"UP"}}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002 -X PUT -d '{"node_id":null,"type":"Node","ports":{}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002 -X PUT -d '{"node_id":null,"version":"1","type":"Node","ports":{}}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003 -X PUT -d '{"type":"Node","ports":{},"attributes":{"physical_id":"003"}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003 -X PUT -d '{"type":"Node","version":"1","ports":{},"attributes":{"physical_id":"003"}}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004 -X PUT -d '{"node_id":null,"type":"Node"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004 -X PUT -d '{"node_id":null,"version":"1","type":"Node"}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005 -X PUT -d '{"type":"Node"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005 -X PUT -d '{"type":"Node","version":"1"}'

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// POST Node //////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes -X POST -d '{"node_id":"test_id","type":"Node","version":"0","ports":{}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes -X POST -d '{"node_id":null,"type":"Node","version":null,"ports":{}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes -X POST -d '{"type":"Node","version":null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes -X POST -d '{"type":"Node"}'

sleep 1

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// PUT Port ///////////////////////"
echo "/////////////////////////////////////////////////"
# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X PUT -d '{"type":"Port","version":"0","node_id":"node001","port_id":"port0011","out_link":null,"in_link":null,"attributes":{"physical_id":"0011@001","oper_status":"UP"}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0011 -X PUT -d '{"type":"Port","version":"1","node_id":"node001","port_id":"port0011","out_link":null,"in_link":null,"attributes":{"physical_id":"0011@001","oper_status":"UP"}}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X PUT -d '{"type":"Port","version":"0","node_id":"node001","port_id":"port0012","out_link":null,"in_link":null}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0012 -X PUT -d '{"type":"Port","version":"1","node_id":"node001","port_id":"port0012","out_link":null,"in_link":null}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0013 -X PUT -d '{"type":"Port","node_id":"node001","port_id":"port0013","out_link":null}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node001/ports/port0013 -X PUT -d '{"type":"Port","version":"1","node_id":"node001","port_id":"port0013","out_link":null}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0021 -X PUT -d '{"type":"Port","node_id":"node002","port_id":"port0021"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0021 -X PUT -d '{"type":"Port","version":"1","node_id":"node002","port_id":"port0021"}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0022 -X PUT -d '{"type":"Port","node_id":null,"port_id":"port0022"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0022 -X PUT -d '{"type":"Port","version":"1","node_id":null,"port_id":"port0022"}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0023 -X PUT -d '{"type":"Port","port_id":null}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0023 -X PUT -d '{"type":"Port","version":"1","port_id":null}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0024 -X PUT -d '{"type":"Port"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node002/ports/port0024 -X PUT -d '{"type":"Port","version":"1"}'

# Add
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0031 -X PUT -d '{"type":"Port","version"null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0032 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node003/ports/port0033 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports/port0041 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports/port0042 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports/port0043 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports/port0044 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005/ports/port0051 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005/ports/port0052 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005/ports/port0053 -X PUT -d '{"type":"Port"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node005/ports/port0054 -X PUT -d '{"type":"Port"}'

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// POST Port //////////////////////"
echo "/////////////////////////////////////////////////"
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port","version":"0","node_id":"node004","port_id":"port0011","out_link":null,"in_link":null,"attributes":{"physical_id":"0011@001","oper_status":"UP"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port","version":"0","node_id":"node004","port_id":"port0011","out_link":null,"in_link":null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port","port_id":"port0011","out_link":null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port","node_id":null,"port_id":"port0011"}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port","port_id":null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/nodes/node004/ports -X POST -d '{"type":"Port"}'

sleep 1

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// PUT Link  //////////////////////"
echo "/////////////////////////////////////////////////"
# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0012 -X PUT -d '{"type":"Link","version":"0","link_id":null,"src_node":"node001","src_port":"port0012","dst_node":"node002","dst_port":"port0021","attributes":{"oper_status":"UP","cost":"0"}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0012 -X PUT -d '{"type":"Link","version":"1","link_id":null,"src_node":"node001","src_port":"port0012","dst_node":"node002","dst_port":"port0021","attributes":{"oper_status":"UP","cost":"0"}}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0021 -X PUT -d '{"type": "Link","version":null,"src_node":"node002","src_port":"port0021","dst_node":"node001","dst_port":"port0012","attributes":null}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0021 -X PUT -d '{"type":"Link","version":"1","src_node":"node002","src_port":"port0021","dst_node":"node001","dst_port":"port0012","attributes": null}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0023 -X PUT -d '{"type":"Link","src_node":"node002","src_port":"port0022","dst_node":"node003","dst_port":"port0031"}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0023 -X PUT -d '{"type":"Link","version":"1","src_node":"node002","src_port":"port0022","dst_node":"node003","dst_port":"port0031"}'

# Add 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0032 -X PUT -d '{"type":"Link","src_node":"node003","src_port":"port0031","dst_node":"node002","dst_port":"port0022","attributes":{}}'
# Update 
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links/link0032 -X PUT -d '{"type":"Link","version":"1","src_node":"node003","src_port":"port0031","dst_node":"node002","dst_port":"port0022","attributes":{}}'

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// POST Link  /////////////////////"
echo "/////////////////////////////////////////////////"
# Add
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links -X POST -d '{"type":"Link","version":"0","link_id":null,"src_node":"node004","src_port":"port0041","dst_node":"node005","dst_port":"port0051","attributes":{"oper_status":"UP","cost":"0"}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links -X POST -d '{"type":"Link","version":null,"src_node":"node005","src_port":"port0051","dst_node":"node004","dst_port":"port0041","attributes":null}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links -X POST -d '{"type":"Link","src_node":"node004","src_port":"port0042","dst_node":"node005","dst_port":"port0052","attributes":{}}'
curl -w "$FORMAT" http://$ADDRESS:10080/network0/topology/links -X POST -d '{"type":"Link","src_node":"node005","src_port":"port0052","dst_node":"node004","dst_port":"port0042"}'

sleep 1

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// PUT Flow  //////////////////////"
echo "/////////////////////////////////////////////////"
# Add
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow01 -X PUT -d '{"version":"0","flow_id":"flow01","owner":"logic","enabled":true,"status":"establishing","attributes":{},"type":"OFPFlow","matches":[{"type":"OFPFlowMatch","in_node":"node005","in_port":"port0051"}],"path":[],"edge_actions":{"node005":[{"type":"FlowActionOutput","output":"port0052"}]}}'
# Update 
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow01 -X PUT -d '{"version":"1","flow_id":"flow01","owner":"logic","enabled":true,"status":"established","attributes":{},"type":"OFPFlow","matches":[{"type":"OFPFlowMatch","in_node":"node005","in_port":"port0051"}],"path":[],"edge_actions":{"node005":[{"type":"FlowActionOutput","output":"port0052"}]}}'

# Add
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow02 -X PUT -d '{"version":null,"flow_id":null,"owner":"logic","enabled":true,"status":"establishing","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node004","in_port":"port0041"}],"path":[],"edge_actions":{"node004":[{"type":"FlowActionOutput","output":"port0042"}]}}'
# Update 
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow02 -X PUT -d '{"version":"1","flow_id":null,"owner":"logic","enabled":true,"status":"established","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node004","in_port":"port0041"}],"path":[],"edge_actions":{"node004":[{"type":"FlowActionOutput","output":"port0042"}]}}'

# Add
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow03 -X PUT -d '{"owner":"logic","enabled":true,"status":"establishing","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node004","in_port":"port0042"}],"path":null,"edge_actions":{"node004":[{"type":"FlowActionOutput","output":"port0043"}]}}'
# Update 
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow03 -X PUT -d '{"version":"1","owner":"logic","enabled":true,"status":"established","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node004","in_port":"port0042"}],"edge_actions":{"node004":[{"type":"FlowActionOutput","output":"port0043"}]}}'

# Add
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow04 -X PUT -d '{"owner":"logic","enabled":true,"status":"establishing","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node005","in_port":"port0053"}],"edge_actions":{"node004":[{"type":"FlowActionOutput","output":"port0052"}]}}'
# Update 
curl -w "$FORMAT" http://localhost:10080/network0/flows/flow04 -X PUT -d '{"version":"1","owner":"logic","enabled":true,"status":"established","attributes":null,"type":"BasicFlow","matches":[{"type":"OFPFlowMatch","in_node":"node005","in_port":"port0053"}],"edge_actions":{"node005":[{"type":"FlowActionOutput","output":"port0052"}]}}'

echo ""
echo "/////////////////////////////////////////////////"
echo "//////////////// POST Flow  /////////////////////"
echo "/////////////////////////////////////////////////"
# Add
curl -w "$FORMAT" http://localhost:10080/network0/flows -X POST -d '{"version":"0","flow_id":"flow01","owner":"logic","enabled":true,"status":"established","attributes":{},"type":"OFPFlow","matches":[{"type":"OFPFlowMatch","in_node":"node001","in_port":"port0011"}],"path":[],"edge_actions":{"node001":[{"type":"FlowActionOutput","output":"port0012"}]}}'
curl -w "$FORMAT" http://localhost:10080/network0/flows -X POST -d '{"version":null,"flow_id":null,"owner":"logic","enabled":true,"status":null,"attributes":null,"type":"BasicFlow","matches":[{"type":"BasicFlowMatch","in_node":"node001","in_port":"port0012"}],"path":[],"edge_actions":{"node001":[{"type":"FlowActionOutput","output":"port0013"}]}}'
curl -w "$FORMAT" http://localhost:10080/network0/flows -X POST -d '{"owner":"logic","enabled":true,"attributes":null,"type":"BasicFlow","matches":[{"type":"BasicFlowMatch","in_node":"node002","in_port":"port0021"}],"path":null,"edge_actions":{"node002":[{"type":"FlowActionOutput","output":"port0022"}]}}'
curl -w "$FORMAT" http://localhost:10080/network0/flows -X POST -d '{"owner":"logic","enabled":true,"attributes":null,"type":"BasicFlow","matches":[{"type":"BasicFlowMatch","in_node":"node003","in_port":"port0031"}],"edge_actions":{"node003":[{"type":"FlowActionOutput","output":"port0032"}]}}'

# echo "------ network0's topology -------" 
# curl http://$ADDRESS:10080/systemmanager/components/network0/topology -X GET | python -mjson.tool
# sleep 2 
# echo "---------------- flow (network0)-------------------"
# curl http://$ADDRESS:10080/systemmanager/components/network0/flows -X GET | python -mjson.tool

