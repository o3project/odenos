
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

require 'odenos/remoteobject/manager/component_manager'
require 'odenos/core/component'
require 'odenos/component/driver'

class TestComponentManager < MiniTest::Test
  include Odenos::Manager
  include Odenos::Util
  include Odenos::Core
  include Odenos::Component
  include Odenos::Component::Driver

  @object_id = "compmgr_ruby"

  def setup
    @dispatcher = mock()
    @dispatcher.expects(:add_local_object).at_least_once
    @target = ComponentManager.new(@object_id, @dispatcher)
  end

  def teardown
    @dispatcher = nil
    @target = nil
  end

  def test_initialize
    assert_equal(@target.instance_variable_get(:@component_classes), {})
    assert_equal(@target.instance_variable_get(:@components), {})
    assert_instance_of(RequestParser, @target.instance_variable_get(:@parser))
    assert_equal(@target.property.state, ObjectProperty::State::RUNNING)
  end

  def test_register_to_system_manager
    @target.expects(:register_component_managers).with().returns().once
    @dispatcher.expects(:system_manager_id).at_least_once
    @target.property.expects(:==).with(anything).returns(true)
    @target.expects(:remote_object_id).returns(@object_id).once
    @target.expects(:request).with(@dispatcher.system_manager_id,
                                   :PUT,
                                   "component_managers/%s" % @object_id,
                                   @target.property).returns().once
    @target.expects(:register_event_manager).with().returns().once
    @target.expects(:subscribe_event).with().returns().once

    @target.register_to_system_manager
  end

  def test_register_event_manager_success
    @dispatcher.expects(:system_manager_id).at_least_once
    @dispatcher.expects(:event_manager_id).at_least_once
    response = Response.new(Response::OK, nil)
    @target.expects(:request).with(@dispatcher.system_manager_id,
                                   :GET,
                                   "objects/#{@dispatcher.event_manager_id}",
                                   nil).returns(response).once
   @dispatcher.expects(:add_remote_client).with(@dispatcher.event_manager_id).returns().once

   @target.register_event_manager
  end

  def test_register_event_manager_failure
    @dispatcher.expects(:system_manager_id).at_least_once
    @dispatcher.expects(:event_manager_id).at_least_once
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:request).with(@dispatcher.system_manager_id,
                                   :GET,
                                   "objects/#{@dispatcher.event_manager_id}",
                                   nil).returns(response).once
   
   @dispatcher.expects(:add_remote_client).with(@dispatcher.event_manager_id).returns().never

   assert_raises(RuntimeError) do
     @target.register_event_manager
   end
  end

  def test_subscribe_event
    @dispatcher.expects(:system_manager_id).at_least_once
    @target.event_subscription.expects(:add_filter).with(@dispatcher.system_manager_id,
                                                         ComponentManagerChanged::TYPE).returns().once
    @target.expects(:apply_event_subscription).with().returns().once

    @target.subscribe_event
  end

  def test_register_component_managers_success
    @dispatcher.expects(:system_manager_id).at_least_once
    body = []
    body.push({})
    body.push({})
    response = Response.new(Response::OK, body)
    @target.expects(:request).with(@dispatcher.system_manager_id,
                                   :GET,
                                   'component_managers',
                                   nil).returns(response).once
    @target.expects(:register_component_manager).with(anything).returns().twice

    @target.register_component_managers
  end

  def test_register_component_managers_failure
    @dispatcher.expects(:system_manager_id).at_least_once
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:request).with(@dispatcher.system_manager_id,
                                   :GET,
                                   'component_managers',
                                   nil).returns(response).once
    @target.expects(:register_component_manager).with(anything).returns().never

    assert_raises(RuntimeError) do
      @target.register_component_managers
    end
  end

  def test_register_component_manager_diff_object_id
    prop = ObjectProperty.new({:id => "compmgr_python"})
    @dispatcher.expects(:add_remote_client).with("compmgr_python").returns().once
    
    @target.register_component_manager(prop)
  end

  def test_register_component_manager_same_object_id
    prop = ObjectProperty.new({:id => @object_id})
    @dispatcher.expects(:add_remote_client).with(@object_id).returns().never

    @target.register_component_manager(prop)
  end

  def test_unregister_component_manager
    @dispatcher.expects(:remove_remote_client).with(@object_id).returns().once

    @target.unregister_component_manager(@object_id)
  end

  def test_register_component_type_not_include_component_classes
    @target.register_component_type(Driver)
    
    assert_equal(Driver, @target.instance_variable_get(:@component_classes)["Driver"])
    assert_equal(@target.property.component_types, "Driver")

    @target.register_component_type(Component)

    assert_equal(Driver, @target.instance_variable_get(:@component_classes)["Driver"])
    assert_equal(Component, @target.instance_variable_get(:@component_classes)["Component"])
    assert_equal(@target.property.component_types, "Driver,Component")
  end

  def test_register_component_type_include_component_classes
    @target.register_component_type(Driver)

    assert_equal(Driver, @target.instance_variable_get(:@component_classes)["Driver"])
    assert_equal(@target.property.component_types, "Driver")

    @target.register_component_type(Driver)

    assert_equal(Driver, @target.instance_variable_get(:@component_classes)["Driver"])
    assert_equal(@target.property.component_types, "Driver")
  end

  def test_local_add_rules
    rules = []
    rules.push(RequestParser::PATTERN => /^component_types$/,
               RequestParser::METHOD => :GET,
               RequestParser::FUNC => @target.method(:do_get_component_types),
               RequestParser::PARAMS => 0)
    rules.push(RequestParser::PATTERN => /^components$/,
               RequestParser::METHOD => :GET,
               RequestParser::FUNC => @target.method(:do_get_components),
               RequestParser::PARAMS => 0)
    rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
               RequestParser::METHOD => :PUT,
               RequestParser::FUNC => @target.method(:do_put_component),
               RequestParser::PARAMS => 2)
    rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
               RequestParser::METHOD => :GET,
               RequestParser::FUNC => @target.method(:do_get_component),
               RequestParser::PARAMS => 1)
    rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
               RequestParser::METHOD => :DELETE,
               RequestParser::FUNC => @target.method(:do_delete_component),
               RequestParser::PARAMS => 1)

    @target.local_add_rules
  end

  def test_on_request
    request = Request.new("object_id", :GET, "components", nil)

    response = @target.on_request(request)

    assert_instance_of(Response, response)
  end

  def test_do_get_component_types_success
    @dispatcher.expects(:add_local_object).once
    @dispatcher.expects(:system_manager_id).once
    @dispatcher.expects(:system_manager_id).once
    @dispatcher.expects(:subscribe_event).once
    @target.register_component_type(SampleDriver)

    response = @target.do_get_component_types

    assert_equal(response.status_code, Response::OK)
    assert_includes(response.body['SampleDriver']['type'], 'SampleDriver')
    assert_includes(response.body['SampleDriver']['super_type'], 'Driver')
    assert_includes(response.body['SampleDriver']['connection_types']['original'], '1')
    assert_includes(response.body['SampleDriver']['connection_types']['aggregated'], '1')
    assert_includes(response.body['SampleDriver']['description'], 'Description')
  end

  def test_do_get_component_types_failed_rescue
    @dispatcher.expects(:system_manager_id).raises().once
    @target.register_component_type(SampleDriver)

    response = @target.do_get_component_types

    assert_equal(response.status_code, Response::INTERNAL_SERVER_ERROR)
  end

  def test_do_get_components
    driver = mock()
    driver_prop = ObjectProperty.new({:type => "Driver", :id => "driver"})
    driver.expects(:property).with().returns(driver_prop)
    comp = mock()
    comp_prop = ObjectProperty.new({:type => "Component", :id => "component"})
    comp.expects(:property).with().returns(comp_prop)
    @target.instance_variable_get(:@components)["driver"] = driver
    @target.instance_variable_get(:@components)["component"] = comp

    response = @target.do_get_components

    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body["driver"].remote_object_id, "driver")
    assert_equal(response.body["driver"].remote_object_type, "Driver")
    assert_equal(response.body["component"].remote_object_id, "component")
    assert_equal(response.body["component"].remote_object_type, "Component")
  end

  def test_do_get_component_success
    driver = mock()
    driver_prop = ObjectProperty.new({:type => "Driver", :id => "driver"})
    driver.expects(:property).with().returns(driver_prop)
    @target.instance_variable_get(:@components)["driver"] = driver

    response = @target.do_get_component("driver")

    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body.remote_object_id, "driver")
    assert_equal(response.body.remote_object_type, "Driver")
  end

  def test_do_get_component_failure_not_found
    response = @target.do_get_component("driver")

    assert_equal(response.status_code, Response::NOT_FOUND)
  end

  def test_do_put_component_success
    @dispatcher.expects(:subscribe_event).at_least_once
    @dispatcher.expects(:publish_event_async).at_least_once
    @dispatcher.expects(:system_manager_id).at_least_once   
    @dispatcher.expects(:add_local_object).at_least_once
    @target.register_component_type(Driver)
    prop = {"type" => "Driver", "id" => "driver"}
    
    response = @target.do_put_component(prop, "driver")

    assert_equal(response.status_code, Response::CREATED)
    assert_equal(response.body.remote_object_id, "driver")
    assert_equal(response.body.remote_object_type, "Driver")
  end

  def test_do_put_component_failure_unknown_type
    prop = {"type" => "Driver", "id" => "driver"}

    response = @target.do_put_component(prop, "driver")

    assert_equal(response.status_code, Response::BAD_REQUEST)
  end

  def test_do_put_component_failure_conflict
    @dispatcher.expects(:subscribe_event).at_least_once
    @dispatcher.expects(:publish_event_async).at_least_once
    @dispatcher.expects(:system_manager_id).at_least_once
    @dispatcher.expects(:add_local_object).at_least_once
    @target.register_component_type(Driver)
    prop = {"type" => "Driver", "id" => "driver"}

    @target.do_put_component(prop, "driver")
    response = @target.do_put_component(prop, "driver")

    assert_equal(response.status_code, Response::CONFLICT)
  end

  def test_do_delete_component_created
    @dispatcher.expects(:subscribe_event).at_least_once
    @dispatcher.expects(:publish_event_async).at_least_once
    @dispatcher.expects(:system_manager_id).at_least_once
    @dispatcher.expects(:add_local_object).at_least_once
    @target.register_component_type(Driver)
    prop = {"type" => "Driver", "id" => "driver"}
    @target.do_put_component(prop, "driver")

    @target.instance_variable_get(:@components)["driver"].expects(:on_finalize).returns().once
    @target.expects(:do_component_changed).with('delete',
                                                anything,
                                                nil).returns().once

    response = @target.do_delete_component("driver")

    assert_equal(response.status_code, Response::OK)
    assert_equal(@target.instance_variable_get(:@components)["driver"], nil)
  end

  def test_do_delete_component_not_created
    response = @target.do_delete_component("driver")

    assert_equal(response.status_code, Response::OK)
  end

  def test_do_component_changed
    action = 'delete'
    prev = {"type" => "Driver", "id" => "driver1"}
    curr = {"type" => "Driver", "id" => "driver2"}

    @target.expects(:do_component_changed).with('delete',
                                                prev,
                                                curr).returns().once

    @target.do_component_changed(action, prev, curr)
  end

  def test_on_event_component_manager_changed_success
    event = Event.new("systemmanager",
                      ComponentManagerChanged::TYPE,
                      {"action" => "delete",
                       "prev" => {"type" => "ComonentManager", "id" => "compmgr_python"},
                       "curr" => nil})

    @target.expects(:on_component_manager_changed).with(anything).returns().once

    @target.on_event(event)
  end

  def test_on_event_component_manager_changed_failure_invalid_message
    event = Event.new("systemmanager",
                      ComponentManagerChanged::TYPE,
                      {"action" => "del",
                       "prev" => nil,
                       "curr" => nil})

    @target.expects(:on_component_manager_changed).with(anything).returns().never

    @target.on_event(event)
  end

  def test_on_event_not_component_manager_changed
    event = Event.new("systemmanager",
                      ComponentChanged::TYPE,
                      {"action" => "del",
                       "prev" => nil,
                       "curr" => nil})

    @target.expects(:on_component_manager_changed).with(anything).returns().never

    @target.on_event(event)
  end

  def test_on_component_manager_changed_add
    event = Event.new("systemmanager",
                      ComponentManagerChanged::TYPE,
                      {"action" => "add",
                       "prev" => nil,
                       "curr" => {"type" => "ComonentManager", "id" => "compmgr_python"}})
    msg = ComponentManagerChanged.new(event.body)

    @target.expects(:register_component_manager).with(anything).returns().once
    @target.expects(:unregister_component_manager).with(anything).returns().never

    @target.on_component_manager_changed(msg)
  end

  def test_on_component_manager_changed_delete
    event = Event.new("systemmanager",
                      ComponentManagerChanged::TYPE,
                      {"action" => "delete",
                       "prev" => {"type" => "ComonentManager", "id" => "compmgr_python"},
                       "curr" => nil})
    msg = ComponentManagerChanged.new(event.body)

    @target.expects(:register_component_manager).with(anything).returns().never
    @target.expects(:unregister_component_manager).with(anything).returns().once

    @target.on_component_manager_changed(msg)
  end

end

require 'odenos/core/component'
require 'odenos/remoteobject'
require 'odenos/core/util'
require 'odenos/core/component/logic'

module Odenos
  module Component
    module Driver
      class SampleDriver < Odenos::Component::Driver::Driver
        include Odenos::Util
        def initialize(remote_object_id, dispatcher)
          super
          @property.description = "Description"
          @property.connection_types = "original:1,aggregated:1"
        end
      end
    end
  end
end
