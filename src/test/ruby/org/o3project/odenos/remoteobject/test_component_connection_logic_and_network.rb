
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

$LOAD_PATH.unshift File.expand_path(File.join(File.dirname(__FILE__), "../../../../../main/ruby/org/o3project"))

require 'minitest'
require 'minitest/unit'
require 'minitest/autorun'

require "odenos/remoteobject/component_connection_logic_and_network"
require "odenos/remoteobject/component_connection"

class TestLogicAndNetwork < MiniTest::Test
  include Odenos::Core
  
  def setup
    @target = LogicAndNetwork.new()
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    @target = LogicAndNetwork.new()
    assert_instance_of(Odenos::Core::LogicAndNetwork, @target)
  end
end
