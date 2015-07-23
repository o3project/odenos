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
ADDRESS=0.0.0.0

curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
# create NetworkComponent and Logic
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/lsw -X PUT -d '{"type": "LearningSwitch", "id": "lsw"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network0 -X PUT -d '{"type": "Network", "id": "network0"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/ofd -X PUT -d '{"type": "OpenFlowDriver", "id": "ofd"}'
curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections -X POST -d '{"id": "conn0", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "lsw", "network_id":"network0"}'
curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections -X POST -d '{"id": "conn1", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "ofd", "network_id":"network0"}'
curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool

# print results
sleep 1
echo "---------------- Results -------------------"
curl http://$ADDRESS:10080/systemmanager/components -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/connections -X GET | python -mjson.tool
curl http://$ADDRESS:10080/systemmanager/components/network0/topology -X GET | python -mjson.tool
