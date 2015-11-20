
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
require "odenos/component/driver/of_driver/openflow_driver"
require "odenos/remoteobject/message_dispatcher"

class TestOpenFlowDriver < MiniTest::Test
  include Odenos::Core
  include Odenos::Component
  include Odenos::Util
  
  def setup
    @logicAndNetwork_prev = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'initializing',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'}

    @logicAndNetwork_prev_obj = LogicAndNetwork.new(@logicAndNetwork_prev)

    @logicAndNetwork_curr = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'} 

    @logicAndNetwork_curr_obj = LogicAndNetwork.new(@logicAndNetwork_curr)
          
   @componentConnectionChanged_body = {
      'publisher_id'=> 'of_driver_network1',
      'event_type'=> 'ComponentConnectionChanged',
      'action'=> 'add',
      'prev'=> @logicAndNetwork_prev_obj,
      'curr'=> @logicAndNetwork_curr_obj}

    @componentConnectionChanged_obj = 
      ComponentConnectionChanged.new(@componentConnectionChanged_body)  
     
    @flow_curr_body = {
      'type'=> 'BasicFlow', 'version'=> 'V02',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> 'none', 'attributes'=> {},
      'matches'=> [], 'path'=> @port_prev_obj,
      'edge_actions'=> {}}

    @flow_curr_obj = Odenos::Component::Flow.new(@flow_curr_body)
       
    @test_dispatcher = MessageDispatcher.new
    @controller = mock()
    @test_dispatcher.expects(:subscribe_event)
    @base_driver = Driver::OFDriver::OpenFlowDriver.new("of_driver", @test_dispatcher, @controller)
  end
  
  def teardown
    @base_driver = nil
  end
  
  def test_initialize
    assert_equal("Driver", @base_driver.instance_variable_get(:@super_type))
  end

  def test_on_connection_changed_added_pre_success
    @logicAndNetwork_curr_obj.state = "initializing"
    assert_equal(true, @base_driver.on_connection_changed_added_pre(
      @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_added_pre_false
    # test case 01
    assert_equal(false, @base_driver.on_connection_changed_added_pre(
      @componentConnectionChanged_obj))

    @logicAndNetwork_curr_obj.state = "initializing"
    @logicAndNetwork_curr_obj.logic_id  = "dummy_driver"
    # test case 02
    assert_equal(false, @base_driver.on_connection_changed_added_pre(
      @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_added_pre_false_network_id_not_nil
    @base_driver.expects(:network_id).returns("network1").once
    assert_equal(false, @base_driver.on_connection_changed_added_pre(
      @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_update_pre_success
    assert_equal(true, @base_driver.on_connection_changed_update_pre(
     @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_update_pre_false
    # invalid connection type
    @logicAndNetwork_curr_obj.expects(:odenos_type).returns("").once
    assert_equal(false, @base_driver.on_connection_changed_update_pre(
      @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_delete_pre_success
    @base_driver.expects(:network_id).returns("network1").once
    assert_equal(true, @base_driver.on_connection_changed_delete_pre(
     @componentConnectionChanged_obj))
  end

  def test_on_connection_changed_delete_pre_false
    @base_driver.expects(:network_id).returns(nil).once
    assert_equal(false, @base_driver.on_connection_changed_delete_pre(
     @componentConnectionChanged_obj))

    @logicAndNetwork_curr_obj.logic_id  = "dummy_driver"
    assert_equal(false, @base_driver.on_connection_changed_delete_pre(
     @componentConnectionChanged_obj))
  end
  
  def test_on_connection_changed_added_success
    @base_driver.expects(:add_entry_event_subscription).
      with(OutPacketAdded::TYPE, @logicAndNetwork_curr_obj.network_id).once
    @base_driver.expects(:add_entry_event_subscription).
      with(FlowChanged::TYPE, @logicAndNetwork_curr_obj.network_id).once
    @base_driver.expects(:update_entry_event_subscription).
      with(FlowChanged::TYPE, @logicAndNetwork_curr_obj.network_id, {}).once
    @base_driver.expects(:apply_event_subscription).once
  
    @controller.expects(:async_call).
      with(:register_driver_component, [@base_driver, @logicAndNetwork_curr_obj]).once
    @base_driver.on_connection_changed_added(@componentConnectionChanged_obj)
  end

  def test_on_connection_changed_added_failed
    @base_driver.expects(:add_entry_event_subscription).
      with(OutPacketAdded::TYPE, @logicAndNetwork_curr_obj.network_id).once
    @base_driver.expects(:add_entry_event_subscription).
      with(FlowChanged::TYPE, @logicAndNetwork_curr_obj.network_id).once
    @base_driver.expects(:update_entry_event_subscription).
      with(FlowChanged::TYPE, @logicAndNetwork_curr_obj.network_id, {}).once
    @base_driver.expects(:apply_event_subscription).raises().once
  
    @base_driver.expects(:error).with(anything).once

    @controller.expects(:async_call).
      with(:register_driver_component, [@base_driver, @logicAndNetwork_curr_obj]).once
    @base_driver.on_connection_changed_added(@componentConnectionChanged_obj)
  end
  

  def test_on_connection_changed_update_success
    @base_driver.on_connection_changed_update(@componentConnectionChanged_obj)
  end
  
  def test_on_connection_changed_delete_success
    @test_dispatcher.expects(:request_sync).with(anything).
      returns(Response.new(Response::OK, nil)).once
    @base_driver.expects(:remove_entry_event_subscription).
      with(OutPacketAdded::TYPE, nil).once
    @base_driver.expects(:remove_entry_event_subscription).
      with(FlowChanged::TYPE, nil).once

    @base_driver.expects(:apply_event_subscription).once
  
    @controller.expects(:async_call).
      with(:unregister_driver_component, [@base_driver, @logicAndNetwork_curr_obj]).once
    @base_driver.on_connection_changed_delete(@componentConnectionChanged_obj)
  end

  def test_on_connection_changed_delete_failed
    @test_dispatcher.expects(:request_sync).with(anything).
      returns(Response.new(Response::OK, nil)).once
    @base_driver.expects(:remove_entry_event_subscription).
      with(OutPacketAdded::TYPE, nil).once
    @base_driver.expects(:remove_entry_event_subscription).
      with(FlowChanged::TYPE, nil).once

    @base_driver.expects(:apply_event_subscription).raises().once
    @base_driver.expects(:error).with(anything).once  

    @controller.expects(:async_call).
      with(:unregister_driver_component, [@base_driver, @logicAndNetwork_curr_obj]).once
    @base_driver.on_connection_changed_delete(@componentConnectionChanged_obj)
  end
    
  def test_on_flow_added_success
    @controller.expects(:async_call).with(:on_flow_added, anything).once
    @base_driver.on_flow_added("network1", @flow_curr_body)
  end

  def test_on_flow_added_failed
    @flow_curr_body['type'] = 'DummyFlow'
    @base_driver.on_flow_added("network1", @flow_curr_body)
  end
  
  def test_on_flow_update_success
    @controller.expects(:async_call).with(:on_flow_update, anything).once
    @base_driver.on_flow_update("network1", nil, @flow_curr_body, nil)
  end

  def test_on_flow_update_failed_flow_id_nil
    @flow_curr_body['flow_id'] = nil
    @base_driver.on_flow_update("network1", nil, @flow_curr_body, nil)
  end

  def test_on_flow_delete_success
    @controller.expects(:async_call).with(:on_flow_delete, anything).once
    @base_driver.on_flow_delete("network1", @flow_curr_body)
  end

  def test_on_flow_delete_failed
    @flow_curr_body['type'] = 'DummyFlow'
    @base_driver.on_flow_delete("network1", @flow_curr_body)
  end

  def test_on_out_packet_added_success
    out_packetadded_body ={
      'publisher_id' =>'publisher_id', 'event_type' => 'OutPacketAdded',
      'id'=> 'OutPacketId'}
    out_packetadded_obj = OutPacketAdded.new(out_packetadded_body)

    nw_if = mock()
    @base_driver.expects(:network_interfaces).returns({'network1' => nw_if}).once

    res_body = {"ports_except" => [], "ports-except" => ["ports-except"], 
        :type => "OutPacket", :attributes => {}, :ports => ["ports"]}
    nw_if.expects(:del_out_packet).with(out_packetadded_obj.packet_id).
      returns(Response.new(Response::OK, res_body)).once

    @controller.expects(:async_call).with(:on_out_packet_added, anything).once

    @base_driver.on_out_packet_added("network1", out_packetadded_obj)
  end
  
  def test_on_out_packet_added_failed
    out_packetadded_body ={
      'publisher_id' =>'publisher_id', 'event_type' => 'OutPacketAdded',
      'id'=> 'OutPacketId'}
    out_packetadded_obj = OutPacketAdded.new(out_packetadded_body)

    nw_if = mock()
    @base_driver.expects(:network_interfaces).returns({'network1' => nw_if}).once

    nw_if.expects(:del_out_packet).with(out_packetadded_obj.packet_id).
      returns(Response.new(Response::NOT_FOUND, nil)).once

    @base_driver.on_out_packet_added("network1", out_packetadded_obj)
  end

  def test_on_request_get_flow_types
    request = Request.new("object_id", :GET, "flow_types", "*", nil)
    response = @base_driver.on_request(request)
    assert_equal(response.status_code, Response::OK)
    assert_includes(response.body, "OFPFlow")
    assert_includes(response.body, "BasicFlow")
  end

  def test_on_request_get_node_maps
    request = Request.new("object_id", :GET, "node_maps", "*", nil)

    resp_body = mock() 
    @controller.expects(:sync_call).with(:on_get_node_maps).returns(resp_body).once
    response = @base_driver.on_request(request)
    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, resp_body)
  end
  
  def test_on_request_get_port_maps
    request = Request.new("object_id", :GET, "port_maps", "*", nil)

    resp_body = mock() 
    @controller.expects(:sync_call).with(:on_get_port_maps).returns(resp_body).once
    response = @base_driver.on_request(request)
    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, resp_body)
  end
  
  def test_on_request_get_link_maps
    request = Request.new("object_id", :GET, "link_maps", "*", nil)

    resp_body = mock() 
    @controller.expects(:sync_call).with(:on_get_link_maps).returns(resp_body).once
    response = @base_driver.on_request(request)
    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, resp_body)
  end
  
  def test_on_request_get_flow_maps
    request = Request.new("object_id", :GET, "flow_maps", "*", nil)

    resp_body = mock() 
    @controller.expects(:sync_call).with(:on_get_flow_maps).returns(resp_body).once
    response = @base_driver.on_request(request)
    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, resp_body)
  end
        
            
end
