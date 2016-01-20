
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

require 'odenos/remoteobject/remote_object'

class TestObjectProperty < MiniTest::Test
  include Odenos::Core

  def setup
    @target = ObjectProperty.new({:type => "ComponentManager", :id => "compmgr_ruby"})
  end

  def teardown
    @target = nil
  end

  def test_initialize
    assert_equal("ComponentManager", @target.get_property(:type))
    assert_equal("compmgr_ruby", @target.get_property(:id))
  end

  def test_remote_object_type
    assert_equal("ComponentManager", @target.remote_object_type)
  end

  def test_remote_object_type=
    @target.remote_object_type = "SystemManager"

    assert_equal("SystemManager", @target.remote_object_type)
  end

  def test_remote_object_id
    assert_equal("compmgr_ruby", @target.remote_object_id)
  end

  def test_remote_object_id=
    @target.remote_object_id = "compmgr_python"

    assert_equal("compmgr_python", @target.remote_object_id)
  end

  def test_set_property_key_string
    result = @target.set_property("key", "Value")

    assert_equal(nil, result)
    assert_equal("Value", @target.get_property("key"))
  end

  def test_set_property_key_symbol
    @target.set_property(:key, "Value")
    result = @target.set_property(:key, "Value2")

    assert_equal("Value", result)
    assert_equal("Value2", @target.get_property(:key))
  end

  def test_get_property_key_string
    @target.set_property("key", "Value")

    assert_equal("Value", @target.get_property("key"))
  end

  def test_get_property_key_symbol
    @target.set_property(:key, "Value")

    assert_equal("Value", @target.get_property(:key))
  end

  def test_delete_property_key_string
    @target.set_property("key", "Value")
    result = @target.delete_property("key")

    assert_equal("Value", result)
    assert_equal(nil, @target.get_property("key"))
  end

  def test_delete_property_key_symbol
    @target.set_property(:key, "Value")
    result = @target.delete_property(:key)

    assert_equal("Value", result)
    assert_equal(nil, @target.get_property(:key))
  end

  def test_delete_property_read_only_key
    result = @target.delete_property(:id)

    assert_equal(nil, result)
    assert_equal("compmgr_ruby", @target.get_property(:id))
  end

  def test_put_property
    @target.set_property(:del_key, 1)
    @target.set_property(:update_key, 2)

    new_prop = {:add_key => 3, :update_key => 4}
    @target.put_property(new_prop)

    assert_equal(nil, @target.get_property(:del_key))
    assert_equal(3, @target.get_property(:add_key))
    assert_equal(4, @target.get_property(:update_key))
  end

  def test_equal_true
    new_prop = {:type => "ComponentManager", :id => "compmgr_ruby"}

    assert_equal(true, @target == new_prop)
  end

  def test_equal_false
    new_prop = {:type => "ComponentManagers", :id => "compmgr_ruby"}
    assert_equal(false, @target == new_prop)

    new_prop = {:id => "compmgr_ruby"}    
    assert_equal(false, @target == new_prop)

    new_prop = {:type => "ComponentManagers", :id => "compmgr_ruby", :state => "error"}
    assert_equal(false, @target == new_prop)
  end

  def test_clone
    @target.super_type = "super_type"
    @target.description = "description"
    @target.connection_types = "connection_type"
    clone_obj = @target.clone

    assert_equal(true, @target == clone_obj.to_hash)
  end

  def test_read_only_true
    assert_equal(true, @target.read_only?(:type))
    assert_equal(true, @target.read_only?(:id))
    assert_equal(true, @target.read_only?(:super_type))
    assert_equal(true, @target.read_only?(:description))
    assert_equal(true, @target.read_only?(:connection_types))
  end

  def test_read_only_false
    assert_equal(false, @target.read_only?(:state))
  end

end

class TestRemoteObject < MiniTest::Test
  include Odenos::Core
  include Odenos::Util

  def setup
    @dispatcher = mock()
    @dispatcher.expects(:add_local_object).at_least_once
    @target = RemoteObject.new('object_id', @dispatcher)
  end

  def teardown
    @dispatcher = nil
    @target = nil
  end

  def test_initialize
    assert_instance_of(ObjectProperty, @target.instance_variable_get(:@property))
    assert_equal(ObjectProperty::State::INITIALIZING, @target.instance_variable_get(:@property).get_state)
    assert_equal(@dispatcher, @target.instance_variable_get(:@dispatcher))
    assert_equal({}, @target.instance_variable_get(:@settings))
    assert_instance_of(EventSubscription, @target.instance_variable_get(:@event_subscription))
    assert_instance_of(Odenos::Util::RequestParser, @target.instance_variable_get(:@parser))
  end

  def test_remote_object_id
    assert_equal('object_id', @target.remote_object_id)
  end

  def test_state
    assert_equal(ObjectProperty::State::INITIALIZING, @target.state)
  end

  def test_on_initialize
    assert_equal(true, @target.on_initialize)
  end

  def test_on_finalize_state_to_finalizing
    @target.expects(:on_property_changed).with('delete',
                                               instance_of(ObjectProperty),
                                               nil).returns().once
    @dispatcher.expects(:remove_local_object).with(@target).returns().once

    @target.on_finalize

    assert_equal(ObjectProperty::State::FINALIZING, @target.state)
  end

  def test_on_finalize_already_state_finalizing
    @target.property.state = ObjectProperty::State::FINALIZING
    @target.expects(:on_property_changed).with('delete',
                                               instance_of(ObjectProperty),
                                               nil).returns().never
    @dispatcher.expects(:remove_local_object).with(@target).returns().once

    @target.on_finalize
  end

  def test_set_state_change_state
    @target.expects(:on_property_changed).with('update',
                                               instance_of(ObjectProperty),
                                               instance_of(ObjectProperty)).returns().once

    @target.set_state(ObjectProperty::State::RUNNING)

    assert_equal(ObjectProperty::State::RUNNING, @target.state)
  end

  def test_set_state_no_change_state
    @target.expects(:on_property_changed).with('update',
                                               instance_of(ObjectProperty),
                                               instance_of(ObjectProperty)).returns().never

    @target.set_state(ObjectProperty::State::INITIALIZING)

    assert_equal(ObjectProperty::State::INITIALIZING, @target.state)
  end

  def test_request_sync
    ex_response = Response.new(Response::OK, nil)
    @dispatcher.expects(:request_sync).with(instance_of(Request)).returns(ex_response).once

    response = @target.request_sync('systemmanager', :GET, 'property', nil)

    assert_equal(ex_response, response)
  end

  def test_request
    ex_response = Response.new(Response::OK, nil)
    @target.expects(:request_sync).with('systemmanager',
                                        :GET,
                                        'property',
                                        nil).returns(ex_response).once

    response = @target.request('systemmanager', :GET, 'property', nil)

    assert_equal(ex_response, response)
  end

  def test_publish_event_async
    type = ObjectPropertyChanged::TYPE
    body = {'action' => 'add', 'prev' => nil, 'curr' => @target.property}
    @dispatcher.expects(:publish_event_async).with(instance_of(Event)).returns().once

    @target.publish_event_async(type, body)
  end

  def test_apply_event_subscription
    @dispatcher.expects(:subscribe_event).with(@target.event_subscription).returns().once

    @target.apply_event_subscription
  end

  def test_subscribe_event
    @target.expects(:apply_event_subscription).with().returns().once

    @target.subscribe_event
  end

  def test_dispatch_request_internal_request_success
    request = Request.new('object_id', :GET, 'property', "*", nil)

    response = @target.dispatch_request(request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.property)
  end

  def test_dispatch_request_outside_request_success
    request = Request.new('object_id', :GET, 'components', "*", nil)
    ex_response = Response.new(Response::OK, nil)
    @target.expects(:on_request).with(request).returns(ex_response).once

    response = @target.dispatch_request(request)

    assert_equal(ex_response, response)
  end

  def test_dispatch_request_exception
    @target.instance_variable_set(:@parser, nil)
    request = Request.new('object_id', :GET, 'property', "*", nil)

    response = @target.dispatch_request(request)

    assert_equal(Response::INTERNAL_SERVER_ERROR, response.status_code)
    assert_equal(nil, response.body)
  end

  def test_dispatch_event
    event = Event.new("systemmanager",
                      ObjectPropertyChanged::TYPE,
                      "*",
                      {"action" => "delete",
                       "prev" => @target.property,
                       "curr" => nil})
    @target.expects(:do_post_event).with(event).returns().once

    @target.dispatch_event(event)
  end

  def test_finalize_ex_finalizing
    assert_equal(@target.finalize?(ObjectProperty::State::INITIALIZING,
                                   ObjectProperty::State::FINALIZING),
                 true)
  end

  def test_finalize_ex_not_finalizing
    assert_equal(@target.finalize?(ObjectProperty::State::INITIALIZING,
                                   ObjectProperty::State::RUNNING),
                 false)
  end

  def test_finalize_ex_already_finalizing
    assert_equal(@target.finalize?(ObjectProperty::State::FINALIZING,
                                   ObjectProperty::State::FINALIZING),
                 false)
  end

  def test_on_property_changed
    action = 'add'
    prev = @target.property
    curr = nil
    @target.expects(:publish_event_async).with(ObjectPropertyChanged::TYPE,
                                               {'action' => action,
                                                'prev' => prev,
                                                'curr' => nil}).returns().once

    @target.on_property_changed(action, prev, curr)
  end

  def test_on_settings_changed
    prev = {:key => "old"}
    curr = {:key => "new"}
    @target.expects(:publish_event_async).with(ObjectSettingsChanged::TYPE,
                                               {'action' => 'update',
                                                'prev' => prev,
                                                'curr' => curr}).returns().once

    @target.on_settings_changed(prev, curr)
  end

  def test_on_request
    request = Request.new('object_id', :GET, 'property', "*", nil)

    resp = @target.on_request(request)

    assert_equal(Response::BAD_REQUEST, resp.status_code)
  end

  def test_on_event
    event = Event.new("systemmanager",
                      ObjectPropertyChanged::TYPE,
                      "*",
                      {"action" => "delete",
                       "prev" => @target.property,
                       "curr" => nil})

    # Do Nothing
    @target.on_event(event)
  end

  def test_add_rules
    rules = []
    rules.push(RequestParser::PATTERN => /^property$/,
               RequestParser::METHOD => :GET,
               RequestParser::FUNC => @target.method(:do_get_property),
               RequestParser::PARAMS => 0)
    rules.push(RequestParser::PATTERN => /^property$/,
               RequestParser::METHOD => :PUT,
               RequestParser::FUNC => @target.method(:do_put_property),
               RequestParser::PARAMS => 1)
    rules.push(RequestParser::PATTERN => /^settings$/,
               RequestParser::METHOD => :GET,
               RequestParser::FUNC => @target.method(:do_get_settings),
               RequestParser::PARAMS => 0)
    rules.push(RequestParser::PATTERN => /^settings$/,
               RequestParser::METHOD => :PUT,
               RequestParser::FUNC => @target.method(:do_put_settings),
               RequestParser::PARAMS => 1)
    @target.instance_variable_get(:@parser).expects(:add_rule).with(rules).returns().once

    @target.send(:add_rules)
  end

  def test_do_get_property
    response = @target.send(:do_get_property)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.property)
  end

  def test_do_put_property_update_success
    @target.property.set_property("del_key", "del_value")
    new_prop = {"type" => "ComponentManager",
                "id" => "compmgr_ruby",
                "state" => ObjectProperty::State::RUNNING,
                "ext" => "extension"}
    request = Request.new('object_id', :PUT, 'property', "*", new_prop)
    @target.expects(:on_property_changed).with('update',
                                               instance_of(ObjectProperty),
                                               @target.property).returns().once

    response = @target.send(:do_put_property, request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.property)
  end

  def test_do_put_property_state_change_to_finalizing
    new_prop = {"type" => "ComponentManager",
                "id" => "compmgr_ruby",
                "state" => ObjectProperty::State::FINALIZING}
    request = Request.new('object_id', :PUT, 'property', "*", new_prop)
    @target.expects(:on_finalize).with().returns().once

    response = @target.send(:do_put_property, request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.property)
  end

  def test_do_put_property_not_update
    new_prop = {"type" => "RemoteObject",
                "id" => "object_id",
                "state" => "initializing",
                "description" => nil,
                "connection_types" => nil}
    request = Request.new('object_id', :PUT, 'property', "*", new_prop)
    @target.expects(:on_finalize).with().returns().never
    @target.expects(:on_property_changed).with('update',
                                               instance_of(ObjectProperty),
                                               @target.property).returns().never

    response = @target.send(:do_put_property, request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.property)
  end

  def test_do_get_settings
    response = @target.send(:do_get_settings)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.settings)
  end

  def test_do_get_settings_update
    @target.settings = {:del => 1,
                        :update => 2}
    new_settings = {:add => 3,
                    :update => 4}
    request = Request.new('object_id', :PUT, 'settings', "*", new_settings)
    @target.expects(:on_settings_changed).with(instance_of(Hash),
                                               @target.settings).returns().once

    response = @target.send(:do_put_settings, request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.settings)
  end

  def test_do_get_settings_no_update
    @target.settings = {:del => 1,
                        :update => 2}
    new_settings = {:del => 1,
                    :update => 2}
    request = Request.new('object_id', :PUT, 'settings', "*", new_settings)
    @target.expects(:on_settings_changed).with(instance_of(Hash),
                                               @target.settings).returns().never

    response = @target.send(:do_put_settings, request)

    assert_equal(Response::OK, response.status_code)
    assert_equal(response.body, @target.settings)
  end

  def test_do_post_event
    event = Event.new("systemmanager",
                      ObjectPropertyChanged::TYPE,
                      "*", 
                      {"action" => "delete",
                       "prev" => @target.property,
                       "curr" => nil})
    @target.expects(:on_event).with(event).returns().once

    response = @target.send(:do_post_event, event)

    assert_equal(Response::ACCEPTED, response.status_code)
  end

end
