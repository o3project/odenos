
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

require "odenos/remoteobject/request"
require "odenos/remoteobject/response"
require "odenos/core/component/network_interface"
require 'odenos/core/component/network_component_entity'


class TestNetworkInterface < MiniTest::Test
  include Odenos::Core
  include Odenos::Component
  
  def setup
    @Portattributes = {
      'admin_status'=> 'UP', 'oper_status'=> 'UP',
      'max_bandwidth'=> 128, 'unreserved_bandwidth'=> 129,
      'physical_id'=> 'PhysicalId1', 'vendor'=> 'Vendor1',
      'is_boundary'=> "True"}
        
    @port_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId1', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId1', 'in_link'=> nil,
      'attributes'=> @Portattributes}

    @port1_obj = Odenos::Component::Port.new(@port_body)
    
    @port2_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId2', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId1', 'in_link'=> nil,
      'attributes'=> {}}
        
    @port2_obj = Odenos::Component::Port.new(@port2_body)

    @port3_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId1', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId1', 'in_link'=> nil,
      'attributes'=> @Portattributes}
      
    @port3_obj = Odenos::Component::Port.new(@port_body)
    
    @port4_body = {
      'type'=> 'Port', 'version'=> '1',
      'port_id'=> 'PortId4', 'node_id'=> 'NodeId1',
      'out_link'=> 'LinkId1', 'in_link'=> nil,
      'attributes'=> {}}

    @port4_obj = Odenos::Component::Port.new(@port2_body)

    @ports_body = {
      'PortId1' => @port_body,
      'PortId2' => @port2_body}

    @ports_body_obj = {
      'PortId1' => @port1_obj,
      'PortId2' => @port2_obj}

    @ports2_body_obj = {
      'PortId3' => @port3_obj,
      'PortId4' => @port4_obj}
               
    @NodeAttributes1 = {
      'admin_status'=> 'UP', 'oper_status'=> 'UP',
      'physical_id'=> 'Physicalid1', 'vendor'=> 'Vendor1'}

    @NodeAttributes2 = {
      'admin_status'=> 'UP', 'oper_status'=> 'Down',
      'physical_id'=> 'Physicalid2', 'vendor'=> 'Vendor1'}
        
    @NodeAttributes3 = {
      'admin_status'=> 'Down', 'oper_status'=> 'Down',
      'physical_id'=> 'Physicalid3', 'vendor'=> 'Vendor1'}
        
    @node_body = {
      'type'=> 'Node', 'version'=> 'v03',
      'node_id'=> 'NodeId1', 'ports'=>  @ports_body_obj,
      'attributes'=> @NodeAttributes1}

    @node_body_obj = Odenos::Component::Node.new(@node_body)
    
    @node2_body = {
      'type'=> 'Node', 'version'=> 'v03',
      'node_id'=> 'NodeId2', 'ports'=>  @ports2_body_obj,
      'attributes'=> {}}
        
    @node2_body_obj = Odenos::Component::Node.new(@node2_body)
    
    @nodes_body = {
      'NodeId1' => @node_body,
      'NodeId2' => @node2_body}

    @nodes_body_obj = {
      'NodeId1' => @node_body_obj,
      'NodeId2' => @node2_body_obj}
 
    @link_body = {
      'type'=> 'Link', 'version'=> '1',
      'link_id'=> 'LinkId1', 'src_node'=> 'NodeId1',
      'src_port'=> 'PortId1', 'dst_node'=> 'NodeId2',
      'dst_port'=> 'PortId3', 'attributes'=> {}}
    @link_body_obj = Odenos::Component::Link.new(@link_body)
    
    @link2_body = {
      'type'=> 'Link', 'version'=> '1',
      'link_id'=> 'LinkId2', 'src_node'=> 'NodeId2',
      'src_port'=> 'PortId3', 'dst_node'=> 'NodeId1',
      'dst_port'=> 'PortId1', 'attributes'=> {}}
        
    @link2_body_obj = Odenos::Component::Link.new(@link_body)
    
    @links_body = {
      'LinkId1' => @link_body,
      'LinkId2' => @link2_body}
        
    @links_body_obj = {
      'LinkId1' => @link_body_obj,
      'LinkId2' => @link2_body_obj}
        
    @topology_body = {
      'type'=> "Topology", 'version'=> "v02",
      'nodes'=> @nodes_body_obj, 'links'=> @links_body_obj}
        
    @flow_Matches = [{
      'type'=> 'BasicFlowMatch', 'in_node'=> 'NODE_ID_1',
      'in_port'=> 'ANY'}]
        
    @flow_Edge_actions ={
      'NODE_ID_1'=> [{
        'type'=> 'FlowActionOutput', 'output'=> 'ANY'}]}
        
    @flow_Attributes = {
      'req_bandwidth'=> 10, 'bandwidth'=> 11,
      'req_latency'=> 20, 'latency'=> 21}
    @flow_Priority = {
      256=> ["FlowId01"]}
        
    @flow_Path = ['LINK_ID1']
      
    @flow1_body = {
      "type"=> "BasicFlow", "version"=> "V01",
      "flow_id"=> "FlowId01", "owner"=> "Owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> @flow_Attributes,
      "matches"=> @flow_Matches, "path"=> @flow_Path,
      "edge_actions"=> @flow_Edge_actions}
        
    @flow2_body = {
      "type"=> "BasicFlow", "version"=> "V01",
      "flow_id"=> "FlowId02", "owner"=> "Owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> @flow_Attributes,
      "matches"=> @flow_Matches, "path"=> @flow_Path,
      "edge_actions"=> @flow_Edge_actions}
        
    @flows_body = {
      "FlowId01"=> @flow1_body,
      "FlowId02"=> @flow2_body}
        
    @flow_set_body = {
      "type"=> 'FlowSet', 'version'=> "V01",
      "priority"=> @flow_Priority, "flows"=> @flows_body}

    @PacketStatus_body = {
      'type'=> 'PacketStatus', 'in_packet_count'=> 456,
      'in_packet_bytes'=> 789, 'in_packet_queue_count'=> 987,
      'in_packets'=> ['InId1', 'InId2'], 'out_packet_count'=> 321,
      'out_packet_bytes'=> 147, 'out_packet_queue_count'=> 258,
      'out_packets'=> ['OutId1', 'OutId2']}
        
    @InPacket_head = {
      'type'=> "BasicFlowMatch", 'in_node'=> "InNodeId1",
      'in_port'=> "InPortId1"}

    @InPacket_body = {
      'id'=> "PacketId01", 'type'=> "InPacket",
      'node'=> "NodeId1", 'port'=> "PortId1",
      'header'=> @InPacket_head, 'data'=> {}}
        
    @OutPacket_head = {
      'type'=> "BasicFlowMatch", 'in_node'=> "OutNodeId1",
      'Out_port'=> "OutPortId1"}

    @OutPacket_body = {
      'packet_id'=> 'PacketId01', 'type'=> 'OutPacket',
      'attributes'=> {'attributes'=> 'attributes_value'},
      'node'=> "NodeId1", 'ports'=> ["PortId1", "PortId2"],
      'ports-except' => ["ExPortId1", "ExPortId2"],
      'header'=> @OutPacket_head, 'data'=> {}}

    @test_dispacher = mock()
    @test_nwc_id = "remote_object_id"
    @base_networkInterface = Odenos::Component::NetworkInterface.new(@test_dispacher, @test_nwc_id) 
  end
  
  def teardown
    @base_networkInterface = nil
  end
  
  def test_initialize
    base_dispacher = @base_networkInterface.instance_variable_get(:@dispatcher)
    base_nwc_id = @base_networkInterface.instance_variable_get(:@nwc_id)
    assert_equal(base_dispacher, @test_dispacher)
    assert_equal(base_nwc_id, @test_nwc_id)
  end

  def test_get_topology_success
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK, @topology_body)).once
    assert_instance_of(Odenos::Component::Topology,
      @base_networkInterface.get_topology())
  end

  def test_get_topology_status_code_notOK         
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @topology_body)).once
    assert_nil(@base_networkInterface.get_topology())    
  end

  def test_get_topology_Topology_NG
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    Topology.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK, @topology_body)).once
    assert_nil(@base_networkInterface.get_topology())    
  end
  
  def test_put_topology_success
    topology_obj = Odenos::Component::Topology.new(@topology_body)
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:put_object_to_network).
      with(@test_nwc_id, path, topology_obj).once
    @base_networkInterface.put_topology(topology_obj)
  end
  
  def test_post_node_success
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    @base_networkInterface.expects(:post_object_to_network).
      with(@test_nwc_id, path, @node_body).once
    @base_networkInterface.post_node(@node_body)
  end
  
  def test_get_nodes_success
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @nodes_body)).once
    get_nodes_test = @base_networkInterface.get_nodes
    assert_instance_of(Odenos::Component::Node, get_nodes_test['NodeId1']) 
    assert_equal('NodeId1', get_nodes_test['NodeId1'].node_id) 
    assert_equal(@ports_body_obj, get_nodes_test['NodeId1'].ports) 
    assert_instance_of(Odenos::Component::Node, get_nodes_test['NodeId2']) 
    assert_equal('NodeId2', get_nodes_test['NodeId2'].node_id) 
    assert_equal(@ports2_body_obj, get_nodes_test['NodeId2'].ports) 
  end

  def test_get_nodes_status_code_notOK  
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @nodes_body)).once
    assert_nil(@base_networkInterface.get_nodes) 
  end

  def test_get_nodes_Node_NG
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    Node.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @nodes_body)).once
    assert_nil(@base_networkInterface.get_nodes)

  end
  
  def test_get_node_success
    path =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @node_body)).once
    get_node_test = @base_networkInterface.get_node('NodeId1')
    assert_instance_of(Odenos::Component::Node, get_node_test) 
    assert_equal('NodeId1', get_node_test.node_id) 
    assert_equal(@ports_body_obj, get_node_test.ports) 
  end

  def test_get_node_status_code_notOK
    path =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @nodes_body)).once
    assert_nil(@base_networkInterface.get_node('NodeId1')) 
  end

  def test_get_node_Node_NG
    path =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    Node.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @node_body)).once
    assert_nil(@base_networkInterface.get_node('NodeId1'))

  end

  def test_put_node_success
    node_obj = Odenos::Component::Node.new(@node_body)
    path =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    @base_networkInterface.expects(:put_object_to_network).
      with(@test_nwc_id, path, node_obj).once
    @base_networkInterface.put_node(node_obj)
  end
  
  def test_del_node_success
    path =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    @base_networkInterface.expects(:del_object_to_network).
      with(@test_nwc_id, path).once
    @base_networkInterface.del_node('NodeId1')
  end
  
  def test_get_physical_node_success
    path =  Odenos::Component::NetworkInterface::PHYSICAL_NODES_PATH % 'Physicalid1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @node_body)).once
    get_node_test = @base_networkInterface.get_physical_node('Physicalid1')
    assert_instance_of(Odenos::Component::Node, get_node_test) 
    assert_equal('NodeId1', get_node_test.node_id) 
    assert_equal(@ports_body_obj, get_node_test.ports) 
  end
  
  def test_get_physical_node_status_code_notOK
    path =  Odenos::Component::NetworkInterface::PHYSICAL_NODES_PATH % 'Physicalid1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @nodes_body)).once
    assert_nil(@base_networkInterface.get_physical_node('Physicalid1')) 
  end
  
  def test_get_physical_node_NG
    path =  Odenos::Component::NetworkInterface::PHYSICAL_NODES_PATH % 'Physicalid1'
    Node.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @node_body)).once
    assert_nil(@base_networkInterface.get_physical_node('Physicalid1'))
  end
  
  def test_put_physical_node_success
    node_obj = Odenos::Component::Node.new(@node_body)
    path =  Odenos::Component::NetworkInterface::PHYSICAL_NODES_PATH % 'Physicalid1'
    @base_networkInterface.expects(:put_object_to_network).with(@test_nwc_id, path, node_obj).once
    @base_networkInterface.put_physical_node(node_obj)
  end
  
  def test_del_physical_node_success
    path =  Odenos::Component::NetworkInterface::PHYSICAL_NODES_PATH % 'Physicalid1'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_physical_node('Physicalid1')
  end

  def test_post_port_success
    port_obj = Odenos::Component::Port.new(@port_body)
    path =  Odenos::Component::NetworkInterface::PORTS_PATH % 'NodeId1'
    @base_networkInterface.expects(:post_object_to_network).with(@test_nwc_id, path, port_obj).once
    @base_networkInterface.post_port(port_obj)
  end

  def test_get_ports_success
    path =  Odenos::Component::NetworkInterface::PORTS_PATH % 'NodeId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @ports_body)).once
    get_port_test = @base_networkInterface.get_ports('NodeId1')
    assert_instance_of(Odenos::Component::Port, get_port_test['PortId1']) 
    assert_equal('NodeId1', get_port_test['PortId1'].node_id) 
    assert_equal('PortId1', get_port_test['PortId1'].port_id) 
    assert_equal('LinkId1', get_port_test['PortId1'].out_link) 
    assert_equal(nil, get_port_test['PortId1'].in_link) 
    assert_instance_of(Odenos::Component::Port, get_port_test['PortId2']) 
    assert_equal('NodeId1', get_port_test['PortId2'].node_id) 
    assert_equal('PortId2', get_port_test['PortId2'].port_id) 
    assert_equal('LinkId1', get_port_test['PortId2'].out_link) 
    assert_equal(nil, get_port_test['PortId2'].in_link) 
  end

  def test_get_ports_status_code_notOK
    path =  Odenos::Component::NetworkInterface::PORTS_PATH % 'NodeId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                         @ports_body)).once
    assert_nil(@base_networkInterface.get_ports('NodeId1'))
  end
  
  def test_get_ports_Port_NG
    path =  Odenos::Component::NetworkInterface::PORTS_PATH % 'NodeId1'
    Port.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                         @ports_body)).once
    assert_nil(@base_networkInterface.get_ports('NodeId1'))

  end
  
  def test_get_port_success
    path =  format(Odenos::Component::NetworkInterface::PORT_PATH ,'NodeId1', 'PortId1')
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @port_body)).once
    get_port_test = @base_networkInterface.get_port('NodeId1','PortId1')
    assert_instance_of(Odenos::Component::Port, get_port_test) 
    assert_equal('NodeId1', get_port_test.node_id) 
    assert_equal('PortId1', get_port_test.port_id) 
    assert_equal('LinkId1', get_port_test.out_link) 
    assert_equal(nil, get_port_test.in_link) 
  end
  
  def test_get_port_status_code_notOK
    path =  format(Odenos::Component::NetworkInterface::PORT_PATH ,'NodeId1', 'PortId1')
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @port_body)).once
    assert_nil(@base_networkInterface.get_port('NodeId1','PortId1'))
  end
  
  def test_get_port_Port_NG
    path =  format(Odenos::Component::NetworkInterface::PORT_PATH ,'NodeId1', 'PortId1')
    Port.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @port_body)).once
    assert_nil(@base_networkInterface.get_port('NodeId1','PortId1'))

  end
  
  def test_put_port_success
    port_obj = Odenos::Component::Port.new(@port_body)
    path =  format(Odenos::Component::NetworkInterface::PORT_PATH ,'NodeId1', 'PortId1')
    @base_networkInterface.expects(:put_object_to_network).with(@test_nwc_id, path, port_obj).once
    @base_networkInterface.put_port(port_obj)
  end

  def test_del_port_success
    path =  format(Odenos::Component::NetworkInterface::PORT_PATH ,'NodeId1', 'PortId1')
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path)
    @base_networkInterface.del_port('NodeId1', 'PortId1')
  end
  
  def test_get_physical_port_success
    path =  format(Odenos::Component::NetworkInterface::PHYSICAL_PORTS_PATH ,'PhysicalId1')
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @port_body)).once
    get_port_test = @base_networkInterface.get_physical_port('PhysicalId1')
    assert_instance_of(Odenos::Component::Port, get_port_test) 
    assert_equal('NodeId1', get_port_test.node_id) 
    assert_equal('PortId1', get_port_test.port_id) 
    assert_equal('LinkId1', get_port_test.out_link) 
    assert_equal(nil, get_port_test.in_link) 
  end
  
  def test_get_physical_port_status_code_notOK
    path =  format(Odenos::Component::NetworkInterface::PHYSICAL_PORTS_PATH ,'PhysicalId1')
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @port_body)).once
    assert_nil(@base_networkInterface.get_physical_port('PhysicalId1'))

  end
  
  def test_get_physical_portPort_NG
    path =  format(Odenos::Component::NetworkInterface::PHYSICAL_PORTS_PATH ,'PhysicalId1')
    Port.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @port_body)).once
    assert_nil(@base_networkInterface.get_physical_port('PhysicalId1'))

  end
  
  def test_put_physical_port_success
    port_obj = Odenos::Component::Port.new(@port_body)
    path =  Odenos::Component::NetworkInterface::PHYSICAL_PORTS_PATH % 'PhysicalId1'
    @base_networkInterface.expects(:put_object_to_network).with(@test_nwc_id, path, port_obj).once
    @base_networkInterface.put_physical_port(port_obj)
  end
  
  def test_del_physical_port_success
    path =  Odenos::Component::NetworkInterface::PHYSICAL_PORTS_PATH % 'PhysicalId1'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_physical_port('PhysicalId1')
  end
  
  def test_post_link_success
    link_obj = Odenos::Component::Link.new(@link_body)
    path =  Odenos::Component::NetworkInterface::LINKS_PATH
    @base_networkInterface.expects(:post_object_to_network).with(@test_nwc_id, path, link_obj).once
    @base_networkInterface.post_link(link_obj)
  end
  
  def test_get_links_success
    path =  Odenos::Component::NetworkInterface::LINKS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @links_body)).once
    get_links_test = @base_networkInterface.get_links
    assert_instance_of(Odenos::Component::Link, get_links_test['LinkId1']) 
    assert_equal('LinkId1', get_links_test['LinkId1'].link_id) 
    assert_equal('NodeId1', get_links_test['LinkId1'].src_node) 
    assert_equal('PortId1', get_links_test['LinkId1'].src_port) 
    assert_equal('NodeId2', get_links_test['LinkId1'].dst_node) 
    assert_equal('PortId3', get_links_test['LinkId1'].dst_port) 
    assert_instance_of(Odenos::Component::Link, get_links_test['LinkId2']) 
    assert_equal('LinkId2', get_links_test['LinkId2'].link_id) 
    assert_equal('NodeId2', get_links_test['LinkId2'].src_node) 
    assert_equal('PortId3', get_links_test['LinkId2'].src_port) 
    assert_equal('NodeId1', get_links_test['LinkId2'].dst_node) 
    assert_equal('PortId1', get_links_test['LinkId2'].dst_port) 
  end
  
  def test_get_links_status_code_notOK
    path =  Odenos::Component::NetworkInterface::LINKS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @links_body)).once
    assert_nil(@base_networkInterface.get_links)

  end
  
  def test_get_links_Link_NG
    path =  Odenos::Component::NetworkInterface::LINKS_PATH
    Link.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @links_body)).once
    assert_nil(@base_networkInterface.get_links)
  end
  
  def test_get_link_success
    path =  Odenos::Component::NetworkInterface::LINK_PATH % 'LinkId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @link_body)).once
    get_link_test = @base_networkInterface.get_link('LinkId1')
    assert_instance_of(Odenos::Component::Link, get_link_test) 
    assert_equal('LinkId1', get_link_test.link_id) 
    assert_equal('NodeId1', get_link_test.src_node) 
    assert_equal('PortId1', get_link_test.src_port) 
    assert_equal('NodeId2', get_link_test.dst_node) 
    assert_equal('PortId3', get_link_test.dst_port) 
  end
  
  def test_get_link_status_code_notOK
    path =  Odenos::Component::NetworkInterface::LINK_PATH % 'LinkId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @link_body)).once
    assert_nil(@base_networkInterface.get_link('LinkId1'))
  end
  
  def test_get_link_Link_NG
    path =  Odenos::Component::NetworkInterface::LINK_PATH % 'LinkId1'
    Link.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @link_body)).once
    assert_nil(@base_networkInterface.get_link('LinkId1'))
  end
  
  def test_put_link_success
    link_obj = Odenos::Component::Link.new(@link_body)
    path =  Odenos::Component::NetworkInterface::LINK_PATH % 'LinkId1'
    @base_networkInterface.expects(:put_object_to_network).with(@test_nwc_id, path, link_obj).once
    @base_networkInterface.put_link(link_obj)
  end
  
  def test_del_link_success
    path =  Odenos::Component::NetworkInterface::LINK_PATH % 'LinkId1'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_link('LinkId1')
  end
  
  def test_post_flow_success
    flow_obj = Odenos::Component::Flow.new(@flow1_body)
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:post_object_to_network).with(@test_nwc_id, path, flow_obj).once
    @base_networkInterface.post_flow(flow_obj)
  end
  
  def test_get_flow_set_success
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @flow_set_body)).once
    get_flow_set_test = @base_networkInterface.get_flow_set
    assert_instance_of(Odenos::Component::FlowSet, get_flow_set_test) 
    assert_equal(@flow_Priority, get_flow_set_test.priority) 
    assert_equal(@flows_body, get_flow_set_test.flows) 
  end
  
  def test_get_flow_set_status_code_notOK
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @flow_set_body)).once
    assert_nil(@base_networkInterface.get_flow_set)
  end
  
  def test_get_flow_set_FlowSet_NG
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    FlowSet.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @flow_set_body)).once
    assert_nil(@base_networkInterface.get_flow_set)

  end
  
  def test_get_flow_success
    path =  Odenos::Component::NetworkInterface::FLOW_PATH % 'FlowId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @flow1_body)).once
    get_flow_test = @base_networkInterface.get_flow('FlowId01')
    assert_instance_of(Odenos::Component::BasicFlow, get_flow_test) 
    assert_equal("FlowId01", get_flow_test.flow_id) 
    assert_equal("Owner", get_flow_test.owner) 
    assert_equal(true, get_flow_test.enabled) 
    assert_equal(256, get_flow_test.priority) 
    assert_equal("none", get_flow_test.status) 
    assert_equal(@flow_Matches, get_flow_test.matches) 
    assert_equal(@flow_Path, get_flow_test.path) 
    assert_equal(@flow_Edge_actions, get_flow_test.edge_actions) 
  end
  
  def test_get_flow_status_code_notOK
    path =  Odenos::Component::NetworkInterface::FLOW_PATH % 'FlowId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @flow1_body)).once
    assert_nil(@base_networkInterface.get_flow('FlowId01'))
  end
  
  def test_get_flow_Flow_NG
    path =  Odenos::Component::NetworkInterface::FLOW_PATH % 'FlowId01'
    Flow.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @flow1_body)).once
    assert_nil(@base_networkInterface.get_flow('FlowId01'))
  end
  
  def test_put_flow_success
    flow_obj = Odenos::Component::Flow.new(@flow1_body)
    path =  Odenos::Component::NetworkInterface::FLOW_PATH % 'FlowId01'
    @base_networkInterface.expects(:put_object_to_network).with(@test_nwc_id, path, flow_obj).once
    @base_networkInterface.put_flow(flow_obj)
  end
  
  def test_del_flow_success
    path =  Odenos::Component::NetworkInterface::FLOW_PATH % 'FlowId01'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_flow('FlowId01')
  end
  
  def test_get_packets_success
    path =  Odenos::Component::NetworkInterface::PACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    get_packets_test = @base_networkInterface.get_packets
    assert_instance_of(Odenos::Component::PacketStatus, get_packets_test) 
    assert_equal(456, get_packets_test.in_packet_count) 
    assert_equal(789, get_packets_test.in_packet_bytes) 
    assert_equal(987, get_packets_test.in_packet_queue_count) 
    assert_equal(321, get_packets_test.out_packet_count) 
    assert_equal(147, get_packets_test.out_packet_bytes) 
    assert_equal(258, get_packets_test.out_packet_queue_count) 
    assert_equal(['InId1', 'InId2'], get_packets_test.in_packets) 
    assert_equal(['OutId1', 'OutId2'], get_packets_test.out_packets) 
  end
  
  def test_get_packets_status_code_notOK
    path =  Odenos::Component::NetworkInterface::PACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @PacketStatus_body)).once
    assert_nil(@base_networkInterface.get_packets)
  end
  
  def test_get_packets_PacketStatus_NG
    path =  Odenos::Component::NetworkInterface::PACKETS_PATH
    PacketStatus.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    assert_nil(@base_networkInterface.get_packets)
  end
  
  def test_post_in_packet_success
    in_packet_obj = Odenos::Component::InPacket.new(@InPacket_body)
    path =  Odenos::Component::NetworkInterface::INPACKETS_PATH
    @base_networkInterface.expects(:post_object_to_network).with(@test_nwc_id, path, in_packet_obj).once
    @base_networkInterface.post_in_packet(in_packet_obj)
  end
  
  def test_get_in_packets_success
    path =  Odenos::Component::NetworkInterface::INPACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    get_in_packets_test = @base_networkInterface.get_in_packets
    assert_instance_of(Odenos::Component::PacketStatus, get_in_packets_test) 
    assert_equal(456, get_in_packets_test.in_packet_count) 
    assert_equal(789, get_in_packets_test.in_packet_bytes) 
    assert_equal(987, get_in_packets_test.in_packet_queue_count) 
    assert_equal(321, get_in_packets_test.out_packet_count) 
    assert_equal(147, get_in_packets_test.out_packet_bytes) 
    assert_equal(258, get_in_packets_test.out_packet_queue_count) 
    assert_equal(['InId1', 'InId2'], get_in_packets_test.in_packets) 
    assert_equal(['OutId1', 'OutId2'], get_in_packets_test.out_packets) 
  end
  
  def test_get_in_packets_status_code_notOK
    path =  Odenos::Component::NetworkInterface::INPACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @PacketStatus_body)).once
    assert_nil(@base_networkInterface.get_in_packets)
  end
  
  def test_get_in_packets_PacketStatus_NG
    path =  Odenos::Component::NetworkInterface::INPACKETS_PATH
    PacketStatus.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    assert_nil(@base_networkInterface.get_in_packets)
  end
  
  def test_del_in_packets_success
    path =  Odenos::Component::NetworkInterface::INPACKETS_PATH
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path)
    @base_networkInterface.del_in_packets
  end
  
  def test_get_in_packet_head_success
    path =  Odenos::Component::NetworkInterface::INPACKETS_HEAD_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @InPacket_body)).once
    get_in_packet_head_test = @base_networkInterface.get_in_packet_head
    assert_instance_of(Odenos::Component::InPacket, get_in_packet_head_test)
  end
  
  def test_get_in_packet_head_status_code_notOK
    path =  Odenos::Component::NetworkInterface::INPACKETS_HEAD_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @InPacket_body)).once
    get_in_packet_head_test = @base_networkInterface.get_in_packet_head
    assert_nil( get_in_packet_head_test)
  end
  
  def test_get_in_packet_head_Packet_NG
    path =  Odenos::Component::NetworkInterface::INPACKETS_HEAD_PATH
    Packet.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @InPacket_body)).once
    assert_nil(@base_networkInterface.get_in_packet_head)
  end
  
  def test_del_in_packet_head_success
    path =  Odenos::Component::NetworkInterface::INPACKETS_HEAD_PATH
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_in_packet_head
  end
  
  def test_get_in_packet_success
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @InPacket_body)).once
    get_in_packet_test = @base_networkInterface.get_in_packet('PacketId01')
    assert_instance_of(Odenos::Component::InPacket, get_in_packet_test)
    assert_equal('NodeId1', get_in_packet_test.node) 
    assert_equal('PortId1', get_in_packet_test.port) 
    assert_equal({}, get_in_packet_test.data) 
    assert_equal(@InPacket_head, get_in_packet_test.header) 
  end
  
  def test_get_in_packet_status_code_notOK
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @InPacket_body)).once
    assert_nil(@base_networkInterface.get_in_packet('PacketId01'))
  end
  
  def test_get_in_packet_Packet_NG
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    Packet.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @InPacket_body)).once
    assert_nil(@base_networkInterface.get_in_packet('PacketId01'))

  end
  
  def test_del_in_packet_success
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_in_packet('PacketId01')
  end
  
  def test_post_out_packet_success
    out_packet_obj = Odenos::Component::OutPacket.new(@OutPacket_body)
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_PATH
    @base_networkInterface.expects(:post_object_to_network).with(@test_nwc_id, path, out_packet_obj).once
    @base_networkInterface.post_out_packet(out_packet_obj)
  end
  
  def test_get_out_packets_success
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    get_out_packets_test = @base_networkInterface.get_out_packets
    assert_instance_of(Odenos::Component::PacketStatus, get_out_packets_test) 
    assert_equal(456, get_out_packets_test.in_packet_count) 
    assert_equal(789, get_out_packets_test.in_packet_bytes) 
    assert_equal(987, get_out_packets_test.in_packet_queue_count) 
    assert_equal(321, get_out_packets_test.out_packet_count) 
    assert_equal(147, get_out_packets_test.out_packet_bytes) 
    assert_equal(258, get_out_packets_test.out_packet_queue_count) 
    assert_equal(['InId1', 'InId2'], get_out_packets_test.in_packets) 
    assert_equal(['OutId1', 'OutId2'], get_out_packets_test.out_packets) 
  end
  
  def test_get_out_packets_status_code_notOK
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @InPacket_body)).once
    assert_nil(@base_networkInterface.get_out_packets)
  end
  
  def test_get_out_packets_PacketStatus_NG
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_PATH
    PacketStatus.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @PacketStatus_body)).once
    assert_nil(@base_networkInterface.get_out_packets)

  end
  
  def test_del_out_packets_success
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_PATH
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_out_packets
  end
  
  def test_get_out_packet_head_success
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_HEAD_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @OutPacket_body)).once
    get_out_packet_head_test = @base_networkInterface.get_out_packet_head
    assert_instance_of(Odenos::Component::OutPacket, get_out_packet_head_test) 
    assert_equal('NodeId1', get_out_packet_head_test.node) 
    assert_equal({}, get_out_packet_head_test.data) 
    assert_equal(@OutPacket_head, get_out_packet_head_test.header) 
    assert_equal(["PortId1", "PortId2"], get_out_packet_head_test.ports) 
    assert_equal(["ExPortId1", "ExPortId2"], get_out_packet_head_test.ports_except) 
  end
  
  def test_get_out_packet_head_status_code_notOK
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_HEAD_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @OutPacket_body)).once
    assert_nil(@base_networkInterface.get_out_packet_head)
  end
  
  def test_get_out_packet_head_Packet_NG
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_HEAD_PATH
    Packet.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @OutPacket_body)).once
    assert_nil(@base_networkInterface.get_out_packet_head)

  end
  
  def test_del_out_packet_head_success
    path =  Odenos::Component::NetworkInterface::OUTPACKETS_HEAD_PATH
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_out_packet_head
  end
  
  def test_get_out_packet_success
    path =  Odenos::Component::NetworkInterface::OUTPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @OutPacket_body)).once
    get_out_packet_test = @base_networkInterface.get_out_packet('PacketId01')
    assert_instance_of(Odenos::Component::OutPacket, get_out_packet_test) 
    assert_equal('NodeId1', get_out_packet_test.node) 
    assert_equal({}, get_out_packet_test.data) 
    assert_equal(@OutPacket_head, get_out_packet_test.header) 
    assert_equal(["PortId1", "PortId2"], get_out_packet_test.ports) 
    assert_equal(["ExPortId1", "ExPortId2"], get_out_packet_test.ports_except) 
  end
  
  def test_get_out_packet_status_code_notOK
    path =  Odenos::Component::NetworkInterface::OUTPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @OutPacket_body)).once
    assert_nil(@base_networkInterface.get_out_packet('PacketId01'))
  end
  
  def test_get_out_packet_Packet_NG
    path =  Odenos::Component::NetworkInterface::OUTPACKET_PATH % 'PacketId01'
    Packet.expects(:new).with(anything).raises("error").once
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @OutPacket_body)).once
    assert_nil(@base_networkInterface.get_out_packet('PacketId01'))
  end
  
  def test_del_out_packet_success
    path =  Odenos::Component::NetworkInterface::OUTPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:del_object_to_network).with(@test_nwc_id, path).once
    @base_networkInterface.del_out_packet('PacketId01')
  end
  
  def test_put_attribute_of_node_InAttribute_nil
    put_attribute_of_node_test = @base_networkInterface.put_attribute_of_node(nil)
    assert_instance_of(Odenos::Core::Response, put_attribute_of_node_test) 
    assert_equal(Response::OK, put_attribute_of_node_test.status_code)
  end
  
  def test_put_attribute_of_node_InAttribute_length_0
    put_attribute_of_node_test = @base_networkInterface.put_attribute_of_node({})
    assert_instance_of(Odenos::Core::Response, put_attribute_of_node_test) 
    assert_equal(Response::OK, put_attribute_of_node_test.status_code)
  end
  
  def test_put_attribute_of_node_nodes_nil
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK, nil)).once
    put_attribute_of_node_test = @base_networkInterface.put_attribute_of_node(@NodeAttributes3)
    assert_instance_of(Odenos::Core::Response, put_attribute_of_node_test) 
    assert_equal(Response::OK, put_attribute_of_node_test.status_code)
  end
  
  def test_put_attribute_of_node_length_0
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK, {})).once
    put_attribute_of_node_test = @base_networkInterface.put_attribute_of_node(@NodeAttributes3)
    assert_instance_of(Odenos::Core::Response, put_attribute_of_node_test) 
  end
  
  def test_put_attribute_of_node_success
    path =  Odenos::Component::NetworkInterface::NODES_PATH
    path2 =  Odenos::Component::NetworkInterface::NODE_PATH % 'NodeId1'
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @nodes_body)).once
    @base_networkInterface.expects(:put_object_to_network).
      with(@test_nwc_id, path2, anything).once
    put_attribute_of_node_test = @base_networkInterface.put_attribute_of_node(@NodeAttributes3)
    assert_equal(@NodeAttributes3, put_attribute_of_node_test['NodeId1'].attributes) 
    assert_equal({}, put_attribute_of_node_test['NodeId2'].attributes) 
  end
  
  def test_delete_all_flow_flow_set_nil
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @flow_set_body)).once
    delete_all_flow_test = @base_networkInterface.delete_all_flow
    assert_instance_of(Odenos::Core::Response, delete_all_flow_test[0]) 
  end
  
  def test_delete_all_flow_flow_set_length_0
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK, {})).once
    delete_all_flow_test = @base_networkInterface.delete_all_flow
    assert_instance_of(Odenos::Core::Response, delete_all_flow_test[0]) 
  end
  
  def test_delete_all_flow_success
    path =  Odenos::Component::NetworkInterface::FLOWS_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @flow_set_body)).once
    @base_networkInterface.expects(:del_flow).
      with("FlowId01").returns("FlowId01_del_OK").once
    @base_networkInterface.expects(:del_flow).
      with("FlowId02").returns("FlowId02_del_OK").once
    delete_all_flow_test = @base_networkInterface.delete_all_flow
    assert_equal(2, delete_all_flow_test.length) 
    assert_includes(delete_all_flow_test,'FlowId01_del_OK')
    assert_includes(delete_all_flow_test,'FlowId02_del_OK')
  end
  
  def test_delete_topology_topology_nill
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::INTERNAL_SERVER_ERROR,
                                       @topology_body)).once
    delete_topology_test = @base_networkInterface.delete_topology
    assert_instance_of(Odenos::Core::Response, delete_topology_test[0]) 
  end
  
  def test_delete_topology_length_0
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       {})).once
    delete_topology_test = @base_networkInterface.delete_topology
    assert_instance_of(Odenos::Core::Response, delete_topology_test[0]) 
  end
  
  def test_delete_topology_success
    path =  Odenos::Component::NetworkInterface::TOPOLOGY_PATH
    @base_networkInterface.expects(:get_object_to_network).
      with(@test_nwc_id, path).returns(Response.new(Response::OK,
                                       @topology_body)).once
    @base_networkInterface.expects(:del_link).
      with('LinkId1').returns('LinkId1_del_OK').once
    @base_networkInterface.expects(:del_link).
      with('LinkId2').returns('LinkId2_del_OK').once
    @base_networkInterface.expects(:del_port).
      with('NodeId1', 'PortId1').returns('PortId1_del_OK').once
    @base_networkInterface.expects(:del_port).
      with('NodeId1', 'PortId2').returns('PortId2_del_OK').once
    @base_networkInterface.expects(:del_port).
      with('NodeId2', 'PortId3').returns('PortId3_del_OK').once
    @base_networkInterface.expects(:del_port).
      with('NodeId2', 'PortId4').returns('PortId4_del_OK').once
    @base_networkInterface.expects(:del_node).
      with('NodeId1').returns('NodeId1_del_OK').once
    @base_networkInterface.expects(:del_node).
      with('NodeId2').returns('NodeId2_del_OK').once
        
    delete_topology_test = @base_networkInterface.delete_topology
    assert_equal(8, delete_topology_test.length) 
    assert_includes(delete_topology_test,'LinkId1_del_OK')
    assert_includes(delete_topology_test,'LinkId2_del_OK')
    assert_includes(delete_topology_test,'PortId1_del_OK')
    assert_includes(delete_topology_test,'PortId2_del_OK')
    assert_includes(delete_topology_test,'PortId3_del_OK')
    assert_includes(delete_topology_test,'PortId4_del_OK')
    assert_includes(delete_topology_test,'NodeId1_del_OK')
    assert_includes(delete_topology_test,'NodeId2_del_OK')
  end
  
  def test_post_object_to_network_success
    in_packet_obj = Odenos::Component::OutPacket.new(@InPacket_body)
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:send_request).
      with(@test_nwc_id, :POST, path, in_packet_obj).once
    post_object_to_network_test = @base_networkInterface.send(:post_object_to_network, @test_nwc_id, path, in_packet_obj)
  end
  
  def test_put_object_to_network_success
    in_packet_obj = Odenos::Component::OutPacket.new(@InPacket_body)
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:send_request).
      with(@test_nwc_id, :PUT, path, in_packet_obj).once
    @base_networkInterface.send(:put_object_to_network, @test_nwc_id, path, in_packet_obj)
  end
  
  def test_del_object_to_network_success
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:send_request).
      with(@test_nwc_id, :DELETE, path, nil).once
    @base_networkInterface.send(:del_object_to_network, @test_nwc_id, path)
  end

  def test_get_object_to_network_success
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    @base_networkInterface.expects(:send_request).
      with(@test_nwc_id, :GET, path, nil).once
    @base_networkInterface.send(:get_object_to_network, @test_nwc_id, path)
  end
  
  def test_send_request_success
    dispacher_test = mock()
    dispacher_test.expects(:request_sync).with(anything).returns('request_sync_OK').once
    networkInterface = Odenos::Component::NetworkInterface.new(dispacher_test, @test_nwc_id) 
    path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
    assert_equal('request_sync_OK', networkInterface.send(:send_request, @test_nwc_id, :GET, path, nil))
  end
  
 def test_send_request_raise
   request_sync_mock = mock()
   request_sync_mock.expects(:request_sync).raises().once
   path =  Odenos::Component::NetworkInterface::INPACKET_PATH % 'PacketId01'
   raises_networkInterface = Odenos::Component::NetworkInterface.new(request_sync_mock, @test_nwc_id)
   
   send_request_test = raises_networkInterface.send(:send_request, @test_nwc_id, :GET, path, nil)
   assert_instance_of(Odenos::Core::Response, send_request_test)
   assert_equal(Odenos::Core::Response::INTERNAL_SERVER_ERROR,
                send_request_test.status_code)
   
  end
end

