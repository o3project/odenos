#!/bin/sh

# -*- coding:utf-8 -*-

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

RUN_DIR=`pwd`
ODENOS_HOME_DIR=../../..
#ODENOS_CONF=$RUN_DIR/odenos.conf
ODENOS_CONF=./etc/odenos.conf
TEST_RESULT=0

build_odenos() {
  cd $ODENOS_HOME_DIR
  mvn install -DskipTests=true
  cd $RUN_DIR
}

start() {
  cd $ODENOS_HOME_DIR
  ./odenos stop
  ./odenos start -c $ODENOS_CONF
  cd $RUN_DIR
  echo "start script ..."
  #read INPUT
  sleep 3
  PYTHONPATH=$ODENOS_HOME_DIR/lib/python/ ./testFederator.py
  if [ $? -ne 0 ] ; then
    TEST_RESULT=1
  fi
}

stop() {
  cd $ODENOS_HOME_DIR
  ./odenos stop
  cd $RUN_DIR
}

show_help() {
    echo >&2 "usage : $0 [-c | -s | -q ]"
    echo >&2 "            -c : build"
    echo >&2 "            -s : start"
    echo >&2 "            -q : stop"
}

case "$1" in
    -c)
        build_odenos
        ;;
    -s)
        start
        if [ $TEST_RESULT -ne 0 ] ; then
          echo "Test Failure..."
          exit 1
        fi
        ;;
    -q)
        stop
        ;;
    *)
        show_help
esac

exit
