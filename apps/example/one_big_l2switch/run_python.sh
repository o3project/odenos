#!/bin/bash
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

#
#
# Usage1: $ ./test_component.sh
#   All test_xxx.py files are executed.
# 
# Usage2: $ ./test_component.sh [file name list]
#   Specified files are executed.
#
# Examples: 
#   $ ./run_python.sh
#   $ ./run_python.sh test_aggregator.py
#   $ ./run_python.sh test_federator.py test_linklayerizer.py
#

PYTHON='python'
PYTHON_PATH='../../../lib/python'
LOG_CONF_FILE='../../../etc/log_python.conf'
APP_LOG_FILE='../../../var/log/example_py.log'
TEST_DIRECT='./'
FILE_NAME_HEADER='test_'
FILE_EXTENSION='py'


# start process
# specify test files
test_files=()
if test $# -eq 0; then
  test_files=`ls ${TEST_DIRECT} | grep ${FILE_NAME_HEADER}.*\\\.${FILE_EXTENSION}`
else
  for input_comp in $@; do
    test_files+=(${input_comp})
  done
fi

succeeded_results=()
failed_results=()

# execute test files
for file in ${test_files[@]}; do
  echo '** Execute '${file}' **'
  PYTHONPATH=${PYTHON_PATH} LOGGING_CONF=${LOG_CONF_FILE} APP_LOG=${APP_LOG_FILE} ${PYTHON} ${file}
  if test $? -eq "0"; then
  	succeeded_results+=(${file})
  else
	failed_results+=(${file})
  fi
done

echo " # succeeded #######"
for file in ${succeeded_results[@]}; do
  echo "	$file" 
done
echo " # failed #######"
for file in ${failed_results[@]}; do
  echo "	$file" 
done
