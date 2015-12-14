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

curl -w "$FORMAT" http://localhost:10080/network2/flows/flow01 -X PUT -d '{"flow_id":"flow01","owner":"","enabled":true,"attributes":{"latency":"0", "req_latency":"0", "bandwidth":"0"},"type":"OFPFlow","idle_timeout":90,"hard_timeout":90,"matches":[{"type":"OFPFlowMatch","in_node":"aggregator1","in_port":"node0x3_port3@0x3"}],"path":[],"edge_actions":{"aggregator1":[{"type":"FlowActionOutput","output":"node0x1_port3@0x1"}]}}'

curl -w "$FORMAT" http://localhost:10080/network2/flows/flow02 -X PUT -d '{"flow_id":"flow02","owner":"","enabled":true,"attributes":{"latency":"0", "req_latency":"0", "bandwidth":"0"},"type":"OFPFlow","idle_timeout":90,"hard_timeout":90,"matches":[{"type":"OFPFlowMatch","in_node":"aggregator1","in_port":"node0x1_port3@0x1"}],"path":[],"edge_actions":{"aggregator1":[{"type":"FlowActionOutput","output":"node0x3_port3@0x3"}]}}'

