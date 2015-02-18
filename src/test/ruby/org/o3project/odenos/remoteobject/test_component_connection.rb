
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

require "odenos/remoteobject/component_connection"

class TestComponentConnection < MiniTest::Test
  include Odenos::Core
  
  def setup
    @target = ComponentConnection.new(
      :id => "ofd1 -> network1"
    )
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    @target = ComponentConnection.new()
    assert_instance_of(Odenos::Core::ComponentConnection, @target)
  end
  
  def test_component_connection_id
    assert_equal("ofd1 -> network1", @target.component_connection_id)
  end
  
  def test_component_connection_id=
    @target.component_connection_id= "ofd2 -> network2"
    assert_equal("ofd2 -> network2", @target.component_connection_id)
  end
  
  class TestComponentConnectionState < MiniTest::Test
    include Odenos::Core::ComponentConnection::State

    def test_state
      assert_equal("initializing", INITIALIZING)
      assert_equal("running", RUNNING)
      assert_equal("finalizing", FINALIZING)
      assert_equal("error", ERROR)
    end 
  end  
  
end
