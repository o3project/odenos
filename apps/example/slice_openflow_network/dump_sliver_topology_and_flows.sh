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

echo "------ dump sliver-nw01's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/sliver-nw01/topology -X GET | python -mjson.tool
echo "------ dump sliver-nw02's topology -------" 
curl http://$ADDRESS:10080/systemmanager/components/sliver-nw02/topology -X GET | python -mjson.tool

echo "------ dump network01's flows -------" 
curl http://$ADDRESS:10080/systemmanager/components/sliver-nw01/flows -X GET | python -mjson.tool
echo "------ dump network02's flows -------" 
curl http://$ADDRESS:10080/systemmanager/components/sliver-nw02/flows -X GET | python -mjson.tool

