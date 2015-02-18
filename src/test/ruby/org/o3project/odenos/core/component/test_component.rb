
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

require "odenos/core/component/component"
require "odenos/remoteobject/message_dispatcher"
require "odenos/component/driver/driver"

class TestComponent < MiniTest::Test
  include Odenos::Core
  
  def setup
    @test_dispacher = MessageDispatcher.new
    @test_dispacher.expects(:subscribe_event).once
    @base_component = Odenos::Component::Component.new("remote_object_id", @test_dispacher)
  end
  
  def teardown
    @base_component = nil
    @test_dispacher = nil
  end
  
  def test_initialize
    driver_dispacher = MessageDispatcher.new
    driver_dispacher.expects(:subscribe_event).once
    base_driver = Odenos::Component::Driver::Driver.new("remote_object_id", driver_dispacher)
    assert_equal("Driver", base_driver.instance_variable_get(:@super_type))
    assert_equal("Driver", base_driver.instance_variable_get(:@property).super_type)
  end
  
  def test_reset_event_subscription_success
    event = @base_component.instance_variable_get(:@event_subscription)
    if event.event_filters.size != 0
      event.event_filters.clear
    end
    @test_dispacher.expects(:subscribe_event).once
    @base_component.reset_event_subscription
    event_filters = @base_component.instance_variable_get(:@event_subscription)
    type = Odenos::Core::ComponentConnectionChanged::TYPE
    assert(event_filters.event_filters[@test_dispacher.system_manager_id].index(type))
  end
end
