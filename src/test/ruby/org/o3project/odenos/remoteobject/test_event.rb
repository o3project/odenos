
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

require "odenos/remoteobject/event"
require 'odenos/remoteobject/remote_object'
require 'odenos/remoteobject/component_connection'

class TestEvent < MiniTest::Test
  include Odenos::Core
  
  def setup
    array = Array["publisher_id", "event_type", "*", "body"]
    @event = Event.new(array)
    hash = {"action" => :add, "prev" => {}, "curr" => {}}
    @base = ObjectChangeEventBase.new(hash)
    @prop = ObjectPropertyChanged.new(hash)
    @subscription = EventSubscription.new({})
  end
  
  def teardown
    @event = nil
    @base = nil
    @prop = nil
    @subscription = nil
  end
  
  def test_event_initialize_with_success_array
    array = Array["publisher_id", "event_type", "*", "body"]
    @event = Event.new(array)
    assert_instance_of(Odenos::Core::Event, @event)
  end
  
  def test_event_initialize_with_success
    @event = Event.new("publisher_id", "event_type", "*", "body")
    assert_instance_of(Odenos::Core::Event, @event)
  end
  
  def test_event_initialize_with_not_array
    assert_raises(ArgumentError){Event.new(200)}
    assert_raises(ArgumentError, "expect Array"){Event.new(200)}
  end
  
  def test_event_initialize_with_few_arguments_array
    array = Array["publisher_id", "body"]
    assert_raises(ArgumentError){Event.new(array)}
    assert_raises(ArgumentError, "expect Event Array(3)"){Event.new(array)}
  end
  
  def test_event_initialize_with_few_arguments
    assert_raises(ArgumentError){Event.new("publisher_id", "body")}
    assert_raises(ArgumentError, "expect 1 or 4 arguments"){Event.new("publisher_id", "body")}
  end
  
  def test_event_initialize_with_more_arguments_array
    array = Array[200, "publisher_id", "event_type", "*", "body"]
    assert_raises(ArgumentError){Event.new(array)}
    assert_raises(ArgumentError, "expect Event Array(4)"){Event.new(array)}
  end
  
  def test_event_initialize_with_more_arguments
    array = Array[200, "publisher_id", "event_type", "*", "body"]
    assert_raises(ArgumentError){Event.new(200, "publisher_id", "event_type", "*", "body")}
    assert_raises(ArgumentError, "expect 1 or 4 arguments"){Event.new(200, "publisher_id", "event_type", "*", "body")}
  end
  
  def test_object_change_event_base_initialize_with_success_symbol
    hash = {"action" => :add, "prev" => {}, "curr" => {}}
    @base = ObjectChangeEventBase.new(hash)
    assert_instance_of(Odenos::Core::ObjectChangeEventBase, @base)
  end

  def test_object_change_event_base_initialize_with_success_string_add
    hash = {"action" => "add", "prev" => {}, "curr" => {}}
    @base = ObjectChangeEventBase.new(hash)
    assert_instance_of(Odenos::Core::ObjectChangeEventBase, @base)
  end
    
  def test_object_change_event_base_initialize_with_success_string_delete
    hash = {"action" => "delete", "prev" => {}, "curr" => {}}
    @base = ObjectChangeEventBase.new(hash)
    assert_instance_of(Odenos::Core::ObjectChangeEventBase, @base)
  end

  def test_object_change_event_base_initialize_with_success_string_update
    hash = {"action" => "update", "prev" => {}, "curr" => {}}
    @base = ObjectChangeEventBase.new(hash)
    assert_instance_of(Odenos::Core::ObjectChangeEventBase, @base)
    end
 
  def test_object_change_event_base_initialize_with_invalid_method
    hash = {"action" => "put", "prev" => {}, "curr" => {}}
    assert_raises(ArgumentError, "Invalid action specified: put"){ObjectChangeEventBase.new(hash)}
  end
  
  def test_object_property_changed_initialize_with_success
    hash = {"action" => :add, 
            "prev" => {:object => "obj_id1", :base_uri => "base_uri1"},
            "curr" => {:object => "obj_id2", :base_uri => "base_uri2"}}
    @prop = ObjectPropertyChanged.new(hash)
    assert_instance_of(Odenos::Core::ObjectPropertyChanged, @prop)
    assert_instance_of(Odenos::Core::ObjectProperty, @prop.prev)
    assert_instance_of(Odenos::Core::ObjectProperty, @prop.curr)
    assert_equal("ObjectPropertyChanged", ObjectPropertyChanged::TYPE)
  end
  
  def test_object_property_changed_initialize_with_success_string
    hash = {"action" => "delete", "prev" => {}, "curr" => {}}
    @prop = ObjectPropertyChanged.new(hash)
    assert_instance_of(Odenos::Core::ObjectPropertyChanged, @prop)
    assert_instance_of(Odenos::Core::ObjectProperty, @prop.prev)
    assert_instance_of(Odenos::Core::ObjectProperty, @prop.curr)
    end

  def test_object_property_changed_initialize_with_invalid_method
    hash = {"action" => "put", "prev" => {}, "curr" => {}}
    assert_raises(ArgumentError, "Invalid action specified: put"){ObjectPropertyChanged.new(hash)}
  end
  
  def test_object_settings_changed
    @target = ObjectSettingsChanged.new()
    assert_instance_of(Odenos::Core::ObjectSettingsChanged, @target)
    assert_equal("ObjectSettingsChanged", ObjectSettingsChanged::TYPE)
  end
  
  def test_component_connection_changed_initialize_with_success
    hash = {"action" => :add, 
           "prev" => {:object => "obj_id1", :base_uri => "base_uri1", :type => "ComponentConnection"},
            "curr" => {:object => "obj_id2", :base_uri => "base_uri2", :type => "ComponentConnection"}}
    @conn = ComponentConnectionChanged.new(hash)
    assert_equal("ComponentConnectionChanged", ComponentConnectionChanged::TYPE)
    assert_instance_of(Odenos::Core::ComponentConnectionChanged, @conn)
    assert_instance_of(Odenos::Core::ComponentConnection, @conn.prev)
    assert_instance_of(Odenos::Core::ComponentConnection, @conn.curr)
    refute_nil(@conn.prev)
    refute_nil(@conn.curr)
  end
  
  def test_component_connection_changed_initialize_with_not_hash
    hash = {"action" => :add, 
            "prev" => "obj_id1",
            "curr" => "obj_id2"}
            @conn = ComponentConnectionChanged.new(hash)
    assert_equal("ComponentConnectionChanged", ComponentConnectionChanged::TYPE)
    assert_instance_of(Odenos::Core::ComponentConnectionChanged, @conn)
    refute_instance_of(Odenos::Core::ComponentConnection, @conn.prev)
    refute_instance_of(Odenos::Core::ComponentConnection, @conn.curr)
  end
  
  def test_component_manager_changed_initialize_with_success
    hash = {"action" => :add, 
            "prev" => {:object => "obj_id1", :base_uri => "base_uri1"},
            "curr" => {:object => "obj_id2", :base_uri => "base_uri2"}}
    @mgr = ComponentManagerChanged.new(hash)
    assert_equal("ComponentManagerChanged", ComponentManagerChanged::TYPE)
    assert_instance_of(Odenos::Core::ComponentManagerChanged, @mgr)
    assert_instance_of(Odenos::Core::ObjectProperty, @mgr.prev)
    assert_instance_of(Odenos::Core::ObjectProperty, @mgr.curr)
  end
  
  def test_component_changed_initialize_with_success
    hash = {"action" => :add, 
            "prev" => {:object => "obj_id1", :base_uri => "base_uri1"},
            "curr" => {:object => "obj_id2", :base_uri => "base_uri2"}}
    @comp = ComponentChanged.new(hash)
    assert_equal("ComponentChanged", ComponentChanged::TYPE)
    assert_instance_of(Odenos::Core::ComponentChanged, @comp)
    assert_instance_of(Odenos::Core::ObjectProperty, @comp.prev)
    assert_instance_of(Odenos::Core::ObjectProperty, @comp.curr)
  end
  
  def test_event_subscription_initialize
    @subscription = EventSubscription.new({})
    assert_instance_of(Odenos::Core::EventSubscription, @subscription)
  end
  
  def test_event_subscription_clear_filter
    hash = {"event_filters" => "event_type"}
    @subscription = EventSubscription.new(hash)
    assert_includes(@subscription.event_filters, "event_type")
    @subscription.clear_filter
    assert_empty(@subscription.event_filters)
  end
  
  def test_event_subscription_add_filter
    @subscription.add_filter("original_nw", "ComponentConnectionPropertyChanged")
    
    assert_includes(@subscription.event_filters, "original_nw")
    assert_includes(@subscription.event_filters["original_nw"], "ComponentConnectionPropertyChanged")
  end
  
  def test_event_subscription_remove_filter
    @subscription.add_filter("original_nw", "ComponentConnectionPropertyChanged")
    @subscription.remove_filter("original_nw", "ComponentConnectionPropertyChanged")

    assert_includes(@subscription.event_filters, "original_nw")
    assert_equal([], @subscription.event_filters["original_nw"])
  end
  
  def test_event_subscription_remove_publisher_id
    @subscription.add_filter("original_nw", "ComponentConnectionPropertyChanged")
    @subscription.remove_publisher_id("original_nw")
    assert_empty(@subscription.event_filters)
  end
  
end
