#!/bin/bash

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

PROJECT_DIR=`readlink -f $0`
PROJECT_DIR=`dirname ${PROJECT_DIR}`
TEST_SRC_DIR=${PROJECT_DIR}/src/test
MAIN_SRC_DIR=${PROJECT_DIR}/src/main/python
RUBY_TEST_SCRIPT=${PROJECT_DIR}/src/test/ruby/test_helper.rb
export PYTHONPATH=${MAIN_SRC_DIR}


run_javaut()
{
    pushd $PROJECT_DIR > /dev/null
    echo -e '=========================== Java Unit Test ==========================='
    mvn test -Dapp.log=/dev/null 2>/dev/null
    popd > /dev/null
}

run_pyut()
{
    pushd $PROJECT_DIR > /dev/null
    echo -e '\n\n\n========================== Python Unit Test =========================='
    (cd ${TEST_SRC_DIR}; coverage run -m unittest discover -s python.org; coverage report --include=${MAIN_SRC_DIR}/* --omit=${MAIN_SRC_DIR}/*/__init__.py)
    popd > /dev/null
}

run_rubyut()
{
    pushd $PROJECT_DIR > /dev/null
    echo -e '\n\n\n========================== Ruby Unit Test ============================'
    ruby ${RUBY_TEST_SCRIPT}
    popd > /dev/null
}

show_help()
{
    echo >&2 "usage : $0 [-jpr]"
    echo >&2 "  -j unittest for java"
    echo >&2 "  -p unittest and coverage for python"
    echo >&2 "  -r unittest and coverage for ruby"
}

# check log output directory
if [ ! -e ${PROJECT_DIR}/var/log ]; then
    mkdir -p ${PROJECT_DIR}/var/log
fi

# check command args
if [ $# -lt 1 ]; then
    run_javaut
    run_pyut
    run_rubyut
fi

while getopts 'jprh' OPTION
do
    case $OPTION in
    "j")
        run_javaut ;;
    "p")
        run_pyut ;;
    "r")
        run_rubyut ;;
    "h")
        show_help ;;
    esac
done

