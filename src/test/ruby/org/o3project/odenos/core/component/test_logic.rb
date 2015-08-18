
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

require "msgpack"
require "odenos/core/component/network_interface"
require 'odenos/core/component/network_component_entity'
require 'odenos/core/component/logic'


class TestLogic < MiniTest::Test
  include Odenos::Core
  include Odenos::Util
  include Odenos::Component
  
  def setup
    @logicAndNetwork_prev = {
      'id'=> 'slicer1_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'initializing',
      'logic_id'=> 'slicer1', 'network_id'=> 'network1'}

    @logicAndNetwork_prev_obj = 
      Odenos::Core::LogicAndNetwork.new(@logicAndNetwork_prev)

    @logicAndNetwork_curr = {
      'id'=> 'slicer1_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'slicer1', 'network_id'=> 'network1'} 

    @logicAndNetwork_curr_obj = 
      Odenos::Core::LogicAndNetwork.new(@logicAndNetwork_curr)

    @port_prev_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId1', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId1', 'in_link'=> nil,
      'attributes'=> {}}

    @port_prev_obj = Odenos::Component::Port.new(@port_prev_body)

    @port_curr_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId1', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId2', 'in_link'=> nil,
      'attributes'=> {} }

    @port_curr_obj = Odenos::Component::Port.new(@port_curr_body)

    @ports_body_obj = {
      'PortId1' => @port_prev_obj,}

    @node_prev_body = {
      'type'=> 'Node', 'version'=> 'v03',
      'node_id'=> 'NodeId1', 'ports'=>  @ports_body_obj,
      'attributes'=> {}}

    @node_prev_obj = Odenos::Component::Node.new(@node_prev_body)

    @node_curr_body = {
      'type'=> 'Node', 'version'=> 'v04',
      'node_id'=> 'NodeId1', 'ports'=>  @ports_body_obj,
      'attributes'=> {}}

    @node_curr_body_obj = Odenos::Component::Node.new(@node_curr_body)

    @link_prev_body = {
      'type'=> 'Link', 'version'=> '1',
      'link_id'=> 'LinkId1', 'src_node'=> 'NodeId1',
      'src_port'=> 'PortId1', 'dst_node'=> 'NodeId2',
      'dst_port'=> 'PortId3', 'attributes'=> {}}

    @link_prev_obj = Odenos::Component::Link.new(@link_prev_body)

    @link_curr_body = {
      'type'=> 'Link', 'version'=> '1',
      'link_id'=> 'LinkId1', 'src_node'=> 'NodeId1',
      'src_port'=> 'PortId1', 'dst_node'=> 'NodeId2',
      'dst_port'=> 'PortId2', 'attributes'=> {}}

    @link_curr_obj = Odenos::Component::Link.new(@link_curr_body)

    @flow_prev_body = {
      'type'=> 'BasicFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> 'none', 'attributes'=> {},
      'matches'=> {}, 'path'=> @port_prev_obj,
      'edge_actions'=> {}}

    @flow_prev_obj = Odenos::Component::Flow.new(@flow_prev_body)

    @flow_curr_body = {
      'type'=> 'BasicFlow', 'version'=> 'V02',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> 'none', 'attributes'=> {},
      'matches'=> {}, 'path'=> @port_prev_obj,
      'edge_actions'=> {}}

    @flow_curr_obj = Odenos::Component::Flow.new(@flow_curr_body)

    @test_dispacher = MessageDispatcher.new
    @test_dispacher.expects(:subscribe_event)
    @base_logic = Odenos::Component::Logic.new("remote_object_id", @test_dispacher) 
  end
  
  def teardown
    @base_logic = nil
    @test_dispacher = nil
  end
  
  def test_initialize
    assert_equal({}, @base_logic.instance_variable_get(:@network_interfaces))
    assert_equal({}, @base_logic.instance_variable_get(:@subscription_table))
  end
  
  def test_on_component_connection_changed_add_success
    componentConnectionChanged_body ={
      'publisher_id'=> 'slicer1_network1',   
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add', 'prev'=> 'none',
      'curr'=> @logicAndNetwork_curr_obj}
        
    componentConnectionChanged_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    @base_logic.expects(:on_connection_changed_added_pre).
      with(componentConnectionChanged_obj).returns(true).once
    @base_logic.expects(:on_connection_changed_added).
      with(componentConnectionChanged_obj).returns(true).once
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
    
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
  end
  
  def test_on_component_connection_changed_add_and_changed_added_pre_false       
    componentConnectionChanged_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add','prev'=> 'none',
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    @base_logic.expects(:on_connection_changed_added_pre).
      with(componentConnectionChanged_obj).returns(false).once
    @base_logic.expects(:on_connection_changed_added).
      with(componentConnectionChanged_obj).returns(true).never
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
    
    assert_equal({}, @base_logic.instance_variable_get(:@network_interfaces)) 
  end
  
  def test_on_component_connection_changed_add_and_nwc_id_registered
    componentConnectionChanged_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add','prev'=> 'none',
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    @base_logic.expects(:on_connection_changed_added_pre).
        with(componentConnectionChanged_obj).returns(true).times(2)
    @base_logic.expects(:on_connection_changed_added).
        with(componentConnectionChanged_obj).returns(true).once
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
    
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
    
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
  end
  
  def test_on_component_connection_changed_update_success
    componentConnectionChanged_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'update', 'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    @base_logic.expects(:on_connection_changed_update_pre).
      with(componentConnectionChanged_obj).returns(true).once
    @base_logic.expects(:on_connection_changed_update).
      with(componentConnectionChanged_obj).returns(true).once
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
  end
  
  def test_on_component_connection_changed_update_and_changed_update_pre_falus      
    componentConnectionChanged_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'update',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_obj = 
        Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    @base_logic.expects(:on_connection_changed_update_pre).
        with(componentConnectionChanged_obj).returns(false).once
    @base_logic.expects(:on_connection_changed_update).
        with(componentConnectionChanged_obj).returns(true).never
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
  end
  
  def test_on_component_connection_changed_delete_success
    componentConnectionChanged_add_body ={
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_delete_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'delete',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_add_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_add_body)  
    componentConnectionChanged_delete_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_delete_body)  
    @base_logic.expects(:on_connection_changed_delete_pre).
      with(componentConnectionChanged_delete_obj).returns(true)
    @base_logic.expects(:on_connection_changed_delete).
      with(componentConnectionChanged_delete_obj).returns(true)
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_add_obj)
    
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
      
    @base_logic.on_component_connection_changed(componentConnectionChanged_delete_obj)
    assert_equal({}, @base_logic.instance_variable_get(:@network_interfaces)) 
  end
  
  def test_on_component_connection_changed_delete_and_changed_delete_pre_false
    componentConnectionChanged_add_body ={
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_delete_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'delete',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_add_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_add_body)  
    componentConnectionChanged_delete_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_delete_body)  
    @base_logic.expects(:on_connection_changed_delete_pre).
      with(componentConnectionChanged_delete_obj).returns(false)
    @base_logic.expects(:on_connection_changed_delete).
      with(componentConnectionChanged_delete_obj).returns(true).never
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_add_obj)
    
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
      
    @base_logic.on_component_connection_changed(componentConnectionChanged_delete_obj)
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
  end
  
  def test_on_component_connection_changed_invalid_action
    componentConnectionChanged_body = {
      'publisher_id'=> 'slicer1_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add',
      'prev'=> 'none',
      'curr'=> @logicAndNetwork_curr_obj}
    componentConnectionChanged_obj = 
      Odenos::Core::ComponentConnectionChanged.new(componentConnectionChanged_body)  
    componentConnectionChanged_obj.action = "addd"
    
    @base_logic.expects(:on_connection_changed_added_pre).
      with(componentConnectionChanged_obj).never
    @base_logic.expects(:on_connection_changed_update_pre).
      with(componentConnectionChanged_obj).never
    @base_logic.expects(:on_connection_changed_delete_pre).
      with(componentConnectionChanged_obj).never
        
    @base_logic.on_component_connection_changed(componentConnectionChanged_obj)
  end
  
  def test_on_connection_changed_added_pre_success
    assert_equal(true, @base_logic.on_connection_changed_added_pre("message"))
  end
  
  def test_on_connection_changed_update_pre_success
    assert_equal(true, @base_logic.on_connection_changed_update_pre("message"))
  end
  
  def test_on_connection_changed_delete_pre_success
    assert_equal(true, @base_logic.on_connection_changed_delete_pre("message"))
  end
  
  def test_on_connection_changed_added_success
    @base_logic.on_connection_changed_added("message")
  end
  
  def test_on_connection_changed_update_success
    @base_logic.on_connection_changed_update("message")
  end
  
  def test_on_connection_changed_delete_success
    @base_logic.on_connection_changed_delete("message")
  end
  
  def test_add_entry_event_subscription_and_type_NodeChanged
    @base_logic.add_entry_event_subscription('NodeChanged','network1')
    assert_equal({"NodeChanged::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"NodeChanged")
  end
  
  def test_add_entry_event_subscription_and_type_PortChanged
    @base_logic.add_entry_event_subscription('PortChanged','network1')
    assert_equal({"PortChanged::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"PortChanged")
  end
  
  def test_add_entry_event_subscription_and_type_LinkChanged
    @base_logic.add_entry_event_subscription('LinkChanged','network1')
    assert_equal({"LinkChanged::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"LinkChanged")
  end
  
  def test_add_entry_event_subscription_and_type_FlowChanged
    @base_logic.add_entry_event_subscription('FlowChanged','network1')
    assert_equal({"FlowChanged::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"FlowChanged")
  end
  
  def test_add_entry_event_subscription_and_type_InPacketAdded
    @base_logic.add_entry_event_subscription('InPacketAdded','network1')
    assert_equal({"InPacketAdded::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"InPacketAdded")
  end
  
  def test_add_entry_event_subscription_and_type_OutPacketAdded
    @base_logic.add_entry_event_subscription('OutPacketAdded','network1')
    assert_equal({"OutPacketAdded::network1"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"OutPacketAdded")
  end
  
  def test_add_entry_event_subscription_event_type_nil
    assert_nil(@base_logic.add_entry_event_subscription(nil,'network1'))
  end
  
  def test_add_entry_event_subscription_nwc_id_nil
    assert_nil(@base_logic.add_entry_event_subscription('OutPacketAdded', nil))
  end
  
  def test_add_entry_event_subscription_event_type_nil_nwc_id_nil
     assert_nil(@base_logic.add_entry_event_subscription(nil, nil))
   end
   
  def test_add_entry_event_subscription_event_type_other
    assert_nil(@base_logic.add_entry_event_subscription('OutPacketAdded2', 'network1'))
  end
  
  def test_remove_entry_event_subscription_and_type_NodeChanged
    @base_logic.add_entry_event_subscription('NodeChanged','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"NodeChanged::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"NodeChanged")
      
    @base_logic.remove_entry_event_subscription('NodeChanged','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_and_type_PortChanged
    @base_logic.add_entry_event_subscription('PortChanged','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"PortChanged::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"PortChanged")
      
    @base_logic.remove_entry_event_subscription('PortChanged','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_and_type_LinkChanged
    @base_logic.add_entry_event_subscription('LinkChanged','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"LinkChanged::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"LinkChanged")
      
    @base_logic.remove_entry_event_subscription('LinkChanged','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_and_type_FlowChanged
    @base_logic.add_entry_event_subscription('FlowChanged','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"FlowChanged::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"FlowChanged")
      
    @base_logic.remove_entry_event_subscription('FlowChanged','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_and_type_InPacketAdded
    @base_logic.add_entry_event_subscription('InPacketAdded','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"InPacketAdded::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"InPacketAdded")
      
    @base_logic.remove_entry_event_subscription('InPacketAdded','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_and_type_OutPacketAdded
    @base_logic.add_entry_event_subscription('OutPacketAdded','network1')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"OutPacketAdded::network1"=>nil}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"OutPacketAdded")
      
    @base_logic.remove_entry_event_subscription('OutPacketAdded','network1')
    assert_equal({}, subscription_table)
    assert_equal([], event_filters.event_filters['network1'])
  end
  
  def test_remove_entry_event_subscription_event_type_nil
    assert_nil(@base_logic.remove_entry_event_subscription(nil,'network1'))
  end
  
  def test_remove_entry_event_subscription_nwc_id_nil
    assert_nil(@base_logic.remove_entry_event_subscription('OutPacketAdded', nil))
  end
  
  def test_remove_entry_event_subscription_event_type_nil_nwc_id_nil
     assert_nil(@base_logic.remove_entry_event_subscription(nil, nil))
   end
   
  def test_remove_entry_event_subscription_event_type_other
    assert_nil(@base_logic.remove_entry_event_subscription('OutPacketAdded2', 'network1'))
  end
  
  def test_update_entry_event_subscription_and_type_NodeChanged
    @base_logic.update_entry_event_subscription('NodeChanged','network1', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"NodeChanged::UPDATE::network1"=>'attributes'}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"NodeChanged")
  end
  
  def test_update_entry_event_subscription_and_type_PortChanged
    @base_logic.update_entry_event_subscription('PortChanged','network1', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"PortChanged::UPDATE::network1"=>'attributes'}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"PortChanged")
  end
  
  def test_update_entry_event_subscription_and_type_LinkChanged
    @base_logic.update_entry_event_subscription('LinkChanged','network1', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"LinkChanged::UPDATE::network1"=>'attributes'}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"LinkChanged")
  end

  def test_update_entry_event_subscription_and_type_FlowChanged
    @base_logic.update_entry_event_subscription('FlowChanged','network1', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"FlowChanged::UPDATE::network1"=>'attributes'}, subscription_table)
    event_filters = @base_logic.instance_variable_get(:@event_subscription)
    assert_includes(event_filters.event_filters['network1'],"FlowChanged")
  end
  
  def test_update_entry_event_subscription_event_type_nil
    assert_nil(@base_logic.update_entry_event_subscription(nil,'network1','attributes'))
  end
  
  def test_update_entry_event_subscription_nwc_id_nil
    assert_nil(@base_logic.update_entry_event_subscription('NodeChanged', nil, 'attributes'))
  end
  
  def test_update_entry_event_subscription_attributes_nil
    assert_nil(@base_logic.update_entry_event_subscription('NodeChanged', 'network1', nil))
  end
  
  def test_update_entry_event_subscription_event_type_nil_nwc_id_nil_attributes_nil
     assert_nil(@base_logic.update_entry_event_subscription(nil, nil, nil))
   end
   
  def test_update_entry_event_subscription_event_type_other
    assert_nil(@base_logic.update_entry_event_subscription('NodeChanged2', 'network1', 'attributes'))
  end
  
  def test_on_event_and_event_type_ComponentConnectionChanged
    componentConnectionChanged_body = {
      'publisher_id'=> 'publisher_id',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add',
      'prev'=> 'none',
      'curr'=> @logicAndNetwork_curr_obj}
    event_obj = Event.new(
      'publisher_id', 'ComponentConnectionChanged',
      componentConnectionChanged_body)
    @base_logic.expects(:on_connection_changed_added_pre).
      with(anything).returns(true).once
    @base_logic.expects(:on_connection_changed_added).
      with(anything).returns(true).once
        
    @base_logic.on_event(event_obj)
    
    network_if = @base_logic.instance_variable_get(:@network_interfaces)
    assert_instance_of(Odenos::Component::NetworkInterface, network_if['network1']) 
  end
  
  def test_on_event_and_event_type_NodeChanged
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'add','version'=> '1',
      'prev' =>'none',
      'curr' => @node_curr_body_obj}
                     
    event_obj = Event.new('publisher_id', 'NodeChanged', nodechg_body)
    @base_logic.expects(:on_node_changed).with('publisher_id', anything).once
        
    @base_logic.on_event(event_obj)               
  end
  
  def test_on_event_and_event_type_PortChanged
    portchg_body = {
      'publisher_id' =>'publisher_id','event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @port_curr_obj}
    event_obj = Event.new('publisher_id', 'PortChanged', portchg_body)
    @base_logic.expects(:on_port_changed).with('publisher_id', anything).once

    @base_logic.on_event(event_obj)   
  end
  
  def test_on_event_and_event_type_LinkChanged
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @link_curr__obj}
    event_obj = Event.new('publisher_id', 'LinkChanged', linkchg_body)
    @base_logic.expects(:on_link_changed).with('publisher_id', anything).once

    @base_logic.on_event(event_obj)  
  end
  
  def test_on_event_and_event_type_FlowChanged
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @flow_curr_obj}
    event_obj = Event.new('publisher_id', 'FlowChanged',flowchg_body)
    @base_logic.expects(:on_flow_changed).with('publisher_id', anything).once

    @base_logic.on_event(event_obj)
  end
  
  def test_on_event_and_event_type_InPacketAdded
    in_packetadded_body ={
      'publisher_id' =>'publisher_id', 'event_type' => 'InPacketAdded',
      'id'=> 'InPacketId'}
    event_obj = Event.new('publisher_id', 'InPacketAdded',in_packetadded_body)
    @base_logic.expects(:on_in_packet_added).with('publisher_id', anything).once
    @base_logic.on_event(event_obj) 
  end
  
  def test_on_event_and_event_type_OutPacketAdded
    out_packetadded_body ={
      'publisher_id' =>'publisher_id', 'event_type' => 'OutPacketAdded',
      'id'=> 'OutPacketId'}
    event_obj = Event.new('publisher_id', 'OutPacketAdded', out_packetadded_body)
    @base_logic.expects(:on_out_packet_added).with('publisher_id', anything).once
    @base_logic.on_event(event_obj) 
  end
  
  def test_on_event_and_rescue
    out_packetadded_body ={
      'publisher_id' =>'publisher_id','event_type' => 'OutPacketAdded',
      'id'=> 'OutPacketId'}
    event_obj = Event.new('publisher_id', 'OutPacketAdded', out_packetadded_body)
    @base_logic.expects(:on_out_packet_added).with('publisher_id', anything).raises().once

    assert_equal(nil, @base_logic.on_event(event_obj))
  end
  
  def test_on_event_and_event_type_other
    event_obj = Event.new('publisher_id', 'OutPacketAdded1',nil)
    @base_logic.expects(:on_component_connection_changed).with('publisher_id', anything).never
    @base_logic.expects(:on_node_changed).with(anything, anything).never
    @base_logic.expects(:on_port_changed).with(anything, anything).never
    @base_logic.expects(:on_link_changed).with(anything, anything).never
    @base_logic.expects(:on_flow_changed).with(anything, anything).never
    @base_logic.expects(:on_in_packet_added).with(anything, anything).never
    @base_logic.expects(:on_out_packet_added).with(anything, anything).never

    @base_logic.on_event(event_obj) 
  end
  
  def test_on_node_changed_add_success
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'add', 'version'=> '1', 
      'prev' =>'none',
      'curr' => @node_curr__body_obj}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)
    @base_logic.expects(:on_node_added).with('publisher_id', @node_curr__body_obj).once
    @base_logic.add_entry_event_subscription('NodeChanged','publisher_id')
    assert_equal({"NodeChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_node_changed('publisher_id',nodechg_obj)
    
  end
  
  def test_on_node_changed_add_and_subscription_table_registered
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'add', 'version'=> '1', 
      'prev' =>'none',
      'curr' => @node_curr__body_obj}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)
    @base_logic.expects(:on_node_added).with(anything, anything).never
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
      
    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_changed_update_success
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'update', 'version'=> '1',
      'prev' => @node_prev_body_obj,
      'curr' => @node_curr_body_obj}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)    
    @base_logic.update_entry_event_subscription('NodeChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"NodeChanged::UPDATE::publisher_id"=>'attributes'}, subscription_table)
    @base_logic.expects(:on_node_update).with('publisher_id', @node_prev_body_obj, @node_curr_body_obj,
      subscription_table["NodeChanged::UPDATE::publisher_id"]).once

    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_changed_update_and_subscription_table_registered
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'update', 'version'=> '1',
      'prev' => @node_prev_body_obj,
      'curr' => @node_curr_body_obj}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)    
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
    @base_logic.expects(:on_node_update).with(anything, anything, anything).never

    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_changed_delete_success
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @node_prev_body_obj,
      'curr' => 'none'}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)
    @base_logic.expects(:on_node_delete).with('publisher_id', @node_prev_body_obj).once
    @base_logic.add_entry_event_subscription('NodeChanged','publisher_id')
    assert_equal({"NodeChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_changed_delete_and_subscription_table_registered
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @node_prev_body_obj,
      'curr' => 'none'}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)
    @base_logic.expects(:on_node_delete).with(anything, anything).never
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
      
    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_changed_invalid_action
    nodechg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'NodeChanged',
      'id'=>'NodeId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @node_curr_body_obj}
    nodechg_obj = Odenos::Component::NodeChanged.new(nodechg_body)
    @base_logic.add_entry_event_subscription('NodeChanged','publisher_id')
    assert_equal({"NodeChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table)) 
    nodechg_obj.action = "addd"
    
    @base_logic.expects(:on_node_added).with(anything, anything).never
    @base_logic.expects(:on_node_update).with(anything, anything, anything, anything).never
    @base_logic.expects(:on_node_delete).with(anything, anything).never
        
    @base_logic.on_node_changed('publisher_id',nodechg_obj)
  end
  
  def test_on_node_added_success
    @base_logic.expects(:on_node_added_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_node_added('publisher_id','message'))
  end
  
  def test_on_node_added_and_on_node_added_pre_false
    @base_logic.expects(:on_node_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_node_added('publisher_id','message'))
  end
  
  def test_on_node_added_pre_success
    assert_equal(true, @base_logic.on_node_added_pre('publisher_id','message'))
  end
  
  def test_on_node_update_success
    @base_logic.expects(:on_node_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(true).once
    assert_nil(@base_logic.on_node_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_node_update_and_on_node_update_pre_false
    @base_logic.expects(:on_node_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(false).once
    assert_nil(@base_logic.on_node_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_node_update_pre_success
    assert_equal(true, @base_logic.on_node_update_pre('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_node_delete_success
    @base_logic.expects(:on_node_delete_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_node_delete('publisher_id','message'))
  end
  
  def test_on_node_delete_and_on_node_added_pre_false
    @base_logic.expects(:on_node_delete_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_node_delete('publisher_id','message'))
  end
  
  def test_on_node_delete_pre_success
    assert_equal(true, @base_logic.on_node_delete_pre('publisher_id','message'))
  end
  
  def test_on_port_changed_add_success
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @port_curr_obj}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    @base_logic.expects(:on_port_added).with('publisher_id', @port_curr_obj).once
    @base_logic.add_entry_event_subscription('PortChanged','publisher_id')
    assert_equal({"PortChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_add_and_subscription_table_registered
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @port_curr_obj}

    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    @base_logic.expects(:on_port_added).with(anything, anything).never
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
      
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_update_success
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'update', 'version'=> '1',
      'prev' => @port_prev_obj,
      'curr' => @port_prev_obj}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    @base_logic.update_entry_event_subscription('PortChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"PortChanged::UPDATE::publisher_id"=>'attributes'}, subscription_table)
      
    @base_logic.expects(:on_port_update).with('publisher_id', @port_prev_obj, @port_prev_obj,
      subscription_table["PortChanged::UPDATE::publisher_id"]).once
        
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_update_and_subscription_table_registered
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'update', 'version'=> '1',
      'prev' => @port_prev_obj,
      'curr' => @port_prev_obj}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
    @base_logic.expects(:on_port_update).with(anything, anything, anything, anything).never
        
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_delete_success
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @port_prev_obj,
      'curr' => 'none'}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    @base_logic.expects(:on_port_delete).with('publisher_id', @port_prev_obj).once
    @base_logic.add_entry_event_subscription('PortChanged','publisher_id')
    assert_equal({"PortChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))

    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_delete_and_subscription_table_registered
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @port_prev_obj,
      'curr' => 'none'}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    @base_logic.expects(:on_port_delete).with(anything, anything).never
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
      
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_changed_invalid_action
    portchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'PortChanged',
      'id'=>'PortId1', 'action' =>'add', 'version'=> '1',
      'prev' => 'none',
      'curr' => @port_curr_obj}
    portchg_obj = Odenos::Component::PortChanged.new(portchg_body)
    portchg_obj.action = "addd"
    @base_logic.add_entry_event_subscription('PortChanged','publisher_id')
    assert_equal({"PortChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table)) 
    @base_logic.expects(:on_port_added).with(anything, anything).never
    @base_logic.expects(:on_port_update).with(anything, anything, anything, anything).never
    @base_logic.expects(:on_port_delete).with(anything, anything).never
        
    @base_logic.on_port_changed('publisher_id',portchg_obj)
  end
  
  def test_on_port_added_success
    @base_logic.expects(:on_port_added_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_port_added('publisher_id','message'))
  end
  
  def test_on_port_added_and_on_port_added_pre_false
    @base_logic.expects(:on_port_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_port_added('publisher_id','message'))
  end
  
  def test_on_port_added_pre_success
    assert_equal(true, @base_logic.on_port_added_pre('publisher_id','message'))
  end
  
  def test_on_port_update_success
    @base_logic.expects(:on_port_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(true).once
    assert_nil(@base_logic.on_port_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_port_update_and_on_port_update_pre_false
    @base_logic.expects(:on_port_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(false).once
    assert_nil(@base_logic.on_port_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_port_update_pre_success
    assert_equal(true, @base_logic.on_port_update_pre('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_port_delete_success
    @base_logic.expects(:on_port_delete_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_port_delete('publisher_id','message'))
  end
  
  def test_on_port_delete_and_on_port_added_pre_false
    @base_logic.expects(:on_port_delete_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_port_delete('publisher_id','message'))
  end
  
  def test_on_port_delete_pre_success
    assert_equal(true, @base_logic.on_port_delete_pre('publisher_id','message'))
  end
  
  def test_on_link_changed_add_success
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @link_curr_obj}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    @base_logic.expects(:on_link_added).with('publisher_id', @link_curr_obj).once
    @base_logic.add_entry_event_subscription('LinkChanged','publisher_id')
    assert_equal({"LinkChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_link_changed('publisher_id',linkchg_obj)
  end
  
  def test_on_link_changed_add_and_subscription_table_registered
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @link_curr_obj}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    @base_logic.expects(:on_port_added).with(anything, anything).never
    assert_equal({},@base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_port_changed('publisher_id',linkchg_obj)
  end

  def test_on_link_changed_update_success
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'update', 'version'=> '1',
      'prev' => @link_prev_obj,
      'curr' => @link_curr_obj}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    @base_logic.update_entry_event_subscription('LinkChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({"LinkChanged::UPDATE::publisher_id"=>'attributes'}, subscription_table)
    @base_logic.expects(:on_link_update).with('publisher_id', @link_prev_obj, @link_curr_obj,
      subscription_table["LinkChanged::UPDATE::publisher_id"]).once
        
    @base_logic.on_link_changed('publisher_id',linkchg_obj)
  end
  
  def test_on_link_changed_update_and_subscription_table_registered
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'update', 'version'=> '1',
      'prev' => @link_prev_obj,
      'curr' => @link_curr_obj}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, subscription_table)
    @base_logic.expects(:on_link_update).with(anything, anything, anything, anything).never

    @base_logic.on_port_changed('publisher_id',linkchg_obj)
  end
  
  def test_on_link_changed_delete_success
     linkchg_body = {
       'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
       'id'=>'LinkId1', 'action' =>'delete', 'version'=> '1',
       'prev' => @link_prev_obj,
       'curr' => 'none'}
     linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
     @base_logic.expects(:on_link_delete).with('publisher_id', @link_prev_obj).once
     @base_logic.add_entry_event_subscription('LinkChanged','publisher_id')
     assert_equal({"LinkChanged::publisher_id"=>nil},
       @base_logic.instance_variable_get(:@subscription_table))
       
     @base_logic.on_link_changed('publisher_id',linkchg_obj)
   end
   
  def test_on_link_changed_delete_and_subscription_table_registered
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @link_prev_obj,
      'curr' => 'none'}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    @base_logic.expects(:on_link_delete).with(anything, anything).never
    assert_equal({},@base_logic.instance_variable_get(:@subscription_table))
        
    @base_logic.on_link_changed('publisher_id',linkchg_obj)
  end

  def test_on_link_changed_invalid_action
    linkchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'LinkChanged',
      'id'=>'LinkId1', 'action' =>'delete', 'version'=> '1',
      'prev' => 'none',
      'curr' => @link_curr_obj}
    linkchg_obj = Odenos::Component::LinkChanged.new(linkchg_body)
    linkchg_obj.action = "addd"
    @base_logic.add_entry_event_subscription('LinkChanged','publisher_id')
    @base_logic.update_entry_event_subscription('LinkChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_includes(subscription_table,"LinkChanged::publisher_id")
    assert_includes(subscription_table,"LinkChanged::UPDATE::publisher_id")
    
    @base_logic.expects(:on_link_added).with(anything, anything).never
    @base_logic.expects(:on_link_update).with(anything, anything, anything, anything).never
    @base_logic.expects(:on_link_delete).with(anything, anything).never
        
    @base_logic.on_link_changed('publisher_id',linkchg_obj)
  end
  
  def test_on_link_added_success
    @base_logic.expects(:on_link_added_pre).
        with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_link_added('publisher_id','message'))
  end
  
  def test_on_link_added_and_on_link_added_pre_false
    @base_logic.expects(:on_link_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_link_added('publisher_id','message'))
  end
  
  def test_on_link_added_pre_success
    assert_equal(true, @base_logic.on_link_added_pre('publisher_id','message'))
  end
  
  def test_on_link_update_success
    @base_logic.expects(:on_link_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(true).once
    assert_nil(@base_logic.on_link_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_link_update_and_on_link_update_pre_false
    @base_logic.expects(:on_link_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(false).once
    assert_nil(@base_logic.on_link_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_link_update_pre_success
    assert_equal(true, @base_logic.on_link_update_pre('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_link_delete_success
    @base_logic.expects(:on_link_delete_pre).
      with('publisher_id','message').returns(true).once

  assert_nil(@base_logic.on_link_delete('publisher_id','message'))
  end
  
  def test_on_link_delete_and_on_link_added_pre_false
    @base_logic.expects(:on_link_delete_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_link_delete('publisher_id','message'))
  end
  
  def test_on_link_delete_pre_success
    assert_equal(true, @base_logic.on_link_delete_pre('publisher_id','message'))
  end
  
  def test_on_flow_changed_add_success
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @flow_curr_obj}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    @base_logic.expects(:on_flow_added).with('publisher_id', @flow_curr_obj).once
    @base_logic.add_entry_event_subscription('FlowChanged','publisher_id')
    assert_equal({"FlowChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_add_and_subscription_table_registered
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @flow_curr_obj}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    @base_logic.expects(:on_flow_added).with(anything, anything).never
    assert_equal({}, @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_update_success
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'update', 'version'=> '1',
      'prev' => @flow_prev_obj,
      'curr' => @flow_curr_obj}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    @base_logic.update_entry_event_subscription('FlowChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_includes(subscription_table,"FlowChanged::UPDATE::publisher_id")
    @base_logic.expects(:on_flow_update).with('publisher_id', @flow_prev_obj, @flow_curr_obj,
      subscription_table["FlowChanged::UPDATE::publisher_id"]).once
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_update_and_subscription_table_registered
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'update', 'version'=> '1',
      'prev' => @flow_prev_obj,
      'curr' => @flow_curr_obj}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_equal({}, @base_logic.instance_variable_get(:@subscription_table))
    @base_logic.expects(:on_flow_update).with(anything, anything, anything, anything).never
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_delete_success
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @flow_prev_obj,
      'curr' => 'none'}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    @base_logic.expects(:on_flow_delete).with('publisher_id', @flow_prev_obj).once
    @base_logic.add_entry_event_subscription('FlowChanged','publisher_id')
    assert_equal({"FlowChanged::publisher_id"=>nil},
      @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_delete_and_subscription_table_registered
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'delete', 'version'=> '1',
      'prev' => @flow_prev_obj,
      'curr' => 'none'}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    @base_logic.expects(:on_flow_delete).with(anything, anything).never
    assert_equal({}, @base_logic.instance_variable_get(:@subscription_table))
      
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_changed_invalid_action
    flowchg_body = {
      'publisher_id' =>'publisher_id', 'event_type' => 'FlowChanged',
      'id'=>'FlowId1', 'action' =>'add', 'version'=> '1',
      'prev' =>'none',
      'curr' => @flow_curr_obj}
    flowchg_obj = Odenos::Component::FlowChanged.new(flowchg_body)
    flowchg_obj.action = "addd"
    @base_logic.add_entry_event_subscription('FlowChanged','publisher_id')
    @base_logic.update_entry_event_subscription('FlowChanged','publisher_id', 'attributes')
    subscription_table = @base_logic.instance_variable_get(:@subscription_table)
    assert_includes(subscription_table,"FlowChanged::publisher_id")
    assert_includes(subscription_table,"FlowChanged::UPDATE::publisher_id")
    @base_logic.expects(:on_flow_added).with(anything, anything).never
    @base_logic.expects(:on_flow_update).with(anything, anything, anything, anything).never
    @base_logic.expects(:on_flow_delete).with(anything, anything).never
        
    @base_logic.on_flow_changed('publisher_id',flowchg_obj)
  end
  
  def test_on_flow_added_success
    @base_logic.expects(:on_flow_added_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_flow_added('publisher_id','message'))
  end
  
  def test_on_flow_added_and_on_flow_added_pre_false
    @base_logic.expects(:on_flow_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_flow_added('publisher_id','message'))
  end
  
  def test_on_flow_added_pre_success
    assert_equal(true, @base_logic.on_flow_added_pre('publisher_id','message'))
  end
  
  def test_on_flow_update_success
    @base_logic.expects(:on_flow_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(true).once
    assert_nil(@base_logic.on_flow_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_flow_update_and_on_flow_update_pre_false
    @base_logic.expects(:on_flow_update_pre).
      with('publisher_id','prev', 'curr', 'attributes').returns(false).once
    assert_nil(@base_logic.on_flow_update('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_flow_update_pre_success
    assert_equal(true, @base_logic.on_flow_update_pre('publisher_id','prev', 'curr', 'attributes'))
  end
  
  def test_on_flow_delete_success
    @base_logic.expects(:on_flow_delete_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_flow_delete('publisher_id','message'))
  end
  
  def test_on_flow_delete_and_on_flow_added_pre_false
    @base_logic.expects(:on_flow_delete_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_flow_delete('publisher_id','message'))
  end
  
  def test_on_flow_delete_pre_success
    assert_equal(true, @base_logic.on_flow_delete_pre('publisher_id','message'))
  end
  
  def test_on_in_packet_added_success
    @base_logic.expects(:on_in_packet_added_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_in_packet_added('publisher_id','message'))
  end
  
  def test_on_in_packet_added_flow_added_pre_false
    @base_logic.expects(:on_in_packet_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_in_packet_added('publisher_id','message'))
  end
  
  def test_on_in_packet_added_pre_success
    assert_equal(true, @base_logic.on_in_packet_added_pre('publisher_id','message'))
  end
  
  def test_on_out_packet_added_success
    @base_logic.expects(:on_out_packet_added_pre).
      with('publisher_id','message').returns(true).once
    assert_nil(@base_logic.on_out_packet_added('publisher_id','message'))
  end
  
  def test_on_out_packet_added_flow_added_pre_false
    @base_logic.expects(:on_out_packet_added_pre).
      with('publisher_id','message').returns(false).once
    assert_nil(@base_logic.on_out_packet_added('publisher_id','message'))
  end
  
  def test_on_out_packet_added_pre_success
    assert_equal(true, @base_logic.on_out_packet_added_pre('publisher_id','message'))
  end
  
end
