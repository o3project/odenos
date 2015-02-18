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

JAVA=$JAVA_HOME/bin/java
ODENOS_HOME_DIR=`pwd`/../../
RUN_DIR=`pwd`
LOG_CONF=../../etc/log_java.conf
LOG_FILE=$ODENOS_HOME_DIR/var/log/example.log


preparation() {
  cd $ODENOS_HOME_DIR
  mvn install -DskipTests=true
  ./odenos restart
  cd $RUN_DIR
  mvn clean
  mvn compile jar:jar

  CLASSPATH=.`echo \`ls $ODENOS_HOME_DIR/lib/java/*\` | tr " " :`
  CLASSPATH2=./simple_controller/target/simple_controller-1.0.jar

  sleep 3s
}

all() {
  aggregator 
  simple_l2switch 
  one_big_l2switch 
  sliver_network 
  federated_network 
  layerized_network
}

aggregator() {

  CLASSPATH3=./register_aggregator/target/register_aggregator-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * register_aggregator.RegisterAggregator\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    register_aggregator.RegisterAggregator

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * register_aggregator.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    register_aggregator.SettingNetwork

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * register_aggregator.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    register_aggregator.CleanUp 

}

simple_l2switch() {

  CLASSPATH3=./simple_l2switch/target/simple_l2switch-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * simple_l2switch.StartSimpleL2Switch\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    simple_l2switch.StartSimpleL2Switch 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * simple_l2switch.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    simple_l2switch.SettingNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * simple_l2switch.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    simple_l2switch.CleanUp 

}

one_big_l2switch() {

  CLASSPATH3=./one_big_l2switch/target/one_big_l2switch-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * one_big_l2switch.StartOneBigL2Switch\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    one_big_l2switch.StartOneBigL2Switch 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * one_big_l2switch.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    one_big_l2switch.SettingNetwork

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * one_big_l2switch.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    one_big_l2switch.CleanUp 

}

sliver_network() {

  CLASSPATH3=./sliver_network/target/sliver_network-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * sliver_network.StartSliverNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    sliver_network.StartSliverNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * sliver_network.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    sliver_network.SettingNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * sliver_network.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    sliver_network.CleanUp 

}

federated_network() {

  CLASSPATH3=./federated_network/target/federated_network-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * federated_network.StartFederatedNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    federated_network.StartFederatedNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * federated_network.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    federated_network.SettingNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * federated_network.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    federated_network.CleanUp 

}

layerized_network() {

  CLASSPATH3=./layerized_network/target/layerized_network-1.0.jar

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * layerized_network.StartLayerizedNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    layerized_network.StartLayerizedNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * layerized_network.SettingNetwork\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    layerized_network.SettingNetwork 

  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  /bin/echo -e "\033[0;32m * layerized_network.CleanUp\033[0;39m"
  /bin/echo -e "\033[0;32m---------------------------------------------\033[0;39m"
  $JAVA -classpath $CLASSPATH:$CLASSPATH2:$CLASSPATH3:. \
    -server \
    -Dlog4j.configuration=file:$LOG_CONF \
    -Dapp.log=$LOG_FILE \
    layerized_network.CleanUp 

}

case "$1" in
all)
    preparation
    all
    ;;
aggregator)
    preparation
    aggregator 
    ;;
simple_l2switch)
    preparation
    simple_l2switch 
    ;;
one_big_l2switch)
    preparation
    one_big_l2switch 
    ;;
sliver_network)
    preparation
    sliver_network 
    ;;
federated_network)
    preparation
    federated_network 
    ;;
layerized_network)
    preparation
    layerized_network 
    ;;
*)
    echo "usage: $0 { all | aggregator | simple_l2switch | one_big_l2switch | sliver_network | federated_network | layerized_network }"
esac

exit
