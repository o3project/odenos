
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
require 'mocha/mini_test'

require "odenos/component/driver/driver"
require "odenos/remoteobject/message_dispatcher"

class TestDriver < MiniTest::Test
  include Odenos::Core
  include Odenos::Util
  
  def setup
    @test_dispacher = MessageDispatcher.new
    @test_dispacher.expects(:subscribe_event)
    @base_driver = Odenos::Component::Driver::Driver.new("remote_object_id", @test_dispacher) 
  end
  
  def teardown
    @base_driver = nil
  end
  
  def test_initialize
    assert_equal("Driver", @base_driver.instance_variable_get(:@super_type))
  end
end
