
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
require 'trema'
require 'pio/lldp'
require 'ipaddr'

require "odenos/component/driver/driver"
require "odenos/component/driver/of_driver/openflow_controller"
require "odenos/remoteobject/message_dispatcher"

class TestOpenFlowController < MiniTest::Test
  include Odenos::Core
  include Odenos::Component
  include Odenos::Util
  include Odenos::Component::Driver::OFDriver

  def setup
    @base_controller = Driver::OFDriver::OpenFlowController.new
  end
    
  def teardown
    @base_controller = nil
  end
  
  def ini
    @base_controller.initialize_topology_mapping
    @base_controller.initialize_flow_mapping
    @base_controller.initialize_queued_call
  end
  
  def test_initialize
    assert(@base_controller.is_a?(Trema::Controller), "failed") 
  end
 
  def test_start_success
    ARGV[0] = "--cmpmgr=of_comp_mgr"
    ARGV[1] = "--sysmgr=systemmanager"
    ARGV[2] = "--rip=127.0.0.1"
    ARGV[3] = "--rport=6379"
    ARGV[4] = "--suppress-lldp"
    begin
      @base_controller.start
    rescue => ex
    end
  end

  def test_start_failed_not_option
    begin
      @base_controller.start
    rescue => ex
    end
  end

  def test_start_failed_sysmgr_id_nil
    ARGV[0] = "--cmpmgr=of_comp_mgr"
    ARGV[1] = "--rip=127.0.0.1"
    ARGV[2] = "--rport=6379"
    ARGV[3] = "--no-suppress-lldp"
    begin
      @base_controller.start
    rescue => ex
    end
  end
  
  def test_update_failed_not_driver_ready
    @base_controller.expects(:_driver_ready?).returns(false).once
    event = RubyTopology::TopologyEvent.new(:add, 0x9001, Topology.new)
    @base_controller.update(event)
  end
  
  def test_update_success_switch_add
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:add, 0x9001, Topology.new)
    @base_controller.expects(:switch_added).with(event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_switch_removed
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:delete, 0x9001, Topology.new)
    @base_controller.expects(:switch_removed).with(event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_switch_updated
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:update, 0x9001, Topology.new)
    @base_controller.expects(:switch_updated).with(event.subject).once
    @base_controller.update(event)
  end

  def test_update_success_port_add
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:add, [0x01], Topology.new)
    @base_controller.expects(:port_added).with(*event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_port_removed
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:delete, [0x01], Topology.new)
    @base_controller.expects(:port_removed).with(*event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_port_updated
    @base_controller.expects(:_driver_ready?).returns(true).once
    event = RubyTopology::TopologyEvent.new(:update, [0x01], Topology.new)
    @base_controller.expects(:port_updated).with(*event.subject).once
    @base_controller.update(event)
  end
 
  def test_update_success_link_added
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    link = RubyTopology::Link.new(0x9001, packet_in)
    event = RubyTopology::TopologyEvent.new(:add, link, Topology.new)
    @base_controller.expects(:link_added).with(*event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_link_removed
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    link = RubyTopology::Link.new(0x9001, packet_in)
    event = RubyTopology::TopologyEvent.new(:delete, link, Topology.new)
    @base_controller.expects(:link_removed).with(*event.subject).once
    @base_controller.update(event)
  end
  
  def test_update_success_link_updated
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    link = RubyTopology::Link.new(0x9001, packet_in)
    event = RubyTopology::TopologyEvent.new(:update, link, Topology.new)
    @base_controller.expects(:link_updated).with(*event.subject).once
    @base_controller.update(event)
  end

  def test_update_failed_rescue_link_updated
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    link = RubyTopology::Link.new(0x9001, packet_in)
    event = RubyTopology::TopologyEvent.new(:update, link, Topology.new)
    @base_controller.expects(:link_updated).with(*event.subject).raises().once
    @base_controller.update(event)
  end
   
  ###############################
  # Test Methods for OpenFlowMessage
  ###############################
   
  def test_packet_in_failed_not_driver_ready
    @base_controller.expects(:_driver_ready?).returns(false).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    @base_controller.packet_in(0x9001, packet_in)
  end

  def test_packet_in_success_ety_type_lldp
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = OpenFlowController::TYPE_LLDP

    ARGV[0] = "--suppress-lldp"
    begin
      @base_controller.start
    rescue => ex
    end
    @base_controller.expects(:update).with(anything).once
    @base_controller.packet_in(0x9001, packet_in)
  end

  def test_packet_in_failed_lookup_node_nil
    @base_controller.expects(:_driver_ready?).returns(true).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_dst = 0x0800

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end

  def test_packet_in_failed_lookup_port_nil
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).returns(Node.new).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
  
  def test_packet_in_failed_not_expect_in_packet
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    packet_in.ip_proto = 17
    packet_in.ipv4_src = IPAddr.new('0.0.0.0/32')

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
 
  def test_packet_in_failed_drop_packet_to_src_node_port
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'src_node')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'src_node', port_id: 'src_port')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    
    link = Link.new(
      'src_node', 'src_port',
      'dst_node', 'dst_port')
    link_4tup = [
      link.src_node, link.src_port,
      link.dst_node, link.dst_port]
      
    ini
    @base_controller.register_link(link_4tup, link)
    @base_controller.packet_in(0x9001, packet_in)
  end
  
  def test_packet_in_failed_drop_packet_to_dst_node_port
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'dst_node')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'dst_node', port_id: 'dst_port')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    
    link = Link.new(
      'src_node', 'src_port',
      'dst_node', 'dst_port')
    link_4tup = [
      link.src_node, link.src_port,
      link.dst_node, link.dst_port]
      
    ini
    @base_controller.register_link(link_4tup, link)
    @base_controller.packet_in(0x9001, packet_in)
  end
  
  def test_packet_in_failed_rescue
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).raises().once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    
    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
   
  def test_packet_in_success_vtag_arp
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.vlan_vid = 2991
    packet_in.vlan_prio = 2
    packet_in.eth_type = 0x0806
    packet_in.arp_op = 0 
    packet_in.arp_sha = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.arp_spa = IPAddr.new('192.168.0.1/32') 
    packet_in.arp_tpa = IPAddr.new('192.168.0.2/32') 

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end

  def test_packet_in_success_ipv4_tcp
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    packet_in.ip_proto = 6 
    packet_in.eth_src = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.eth_dst = Trema::Mac.new('ee:ee:bb:cc:ee:ff') 
    packet_in.ipv4_src = IPAddr.new('192.168.0.1/32') 
    packet_in.ipv4_dst = IPAddr.new('192.168.0.2/32') 
    packet_in.tcp_src = 1001
    packet_in.tcp_dst = 1002 

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
  
   def test_packet_in_success_ipv4_udp
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    packet_in.ip_proto = 17 
    packet_in.eth_src = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.eth_dst = Trema::Mac.new('ee:ee:bb:cc:ee:ff') 
    packet_in.ipv4_src = IPAddr.new('192.168.0.1/32') 
    packet_in.ipv4_dst = IPAddr.new('192.168.0.2/32') 
    packet_in.udp_src = 1001
    packet_in.udp_dst = 1002 

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
  
   def test_packet_in_success_ipv4_sctp
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    packet_in.ip_proto = 132 
    packet_in.eth_src = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.eth_dst = Trema::Mac.new('ee:ee:bb:cc:ee:ff') 
    packet_in.ipv4_src = IPAddr.new('192.168.0.1/32') 
    packet_in.ipv4_dst = IPAddr.new('192.168.0.2/32') 
    packet_in.sctp_src = 1001
    packet_in.sctp_dst = 1002 

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end
  
  def test_packet_in_success_icmpv4
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x0800
    packet_in.ip_proto = 1
    packet_in.eth_src = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.eth_dst = Trema::Mac.new('ee:ee:bb:cc:ee:ff') 
    packet_in.ipv4_src = IPAddr.new('192.168.0.1/32') 
    packet_in.ipv4_dst = IPAddr.new('192.168.0.2/32') 
    packet_in.icmpv4_type = 1
    packet_in.icmpv4_code = 2 

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end     

  def test_packet_in_success_icmpv6_ipv6_mpls_pbb
    @base_controller.expects(:_driver_ready?).returns(true).once
    @base_controller.expects(:lookup_node).with(0x9001).
      returns(Node.new(node_id: 'node_id')).once
    @base_controller.expects(:lookup_port).with(0x9001, 0x01).
      returns(Port.new(node_id: 'node_id', port_id: 'port_id')).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x01, data.unpack('C*'))
    packet_in.eth_type = 0x86dd
    packet_in.ip_proto = 58
    packet_in.eth_src = Trema::Mac.new('ff:ff:bb:cc:ee:ff') 
    packet_in.eth_dst = Trema::Mac.new('ee:ee:bb:cc:ee:ff') 
    packet_in.ipv6_src = IPAddr.new('2001:DB8:0:0:8:800:200C:417A') 
    packet_in.ipv6_dst = IPAddr.new('2001:A55:0:0:1:511:1C:555B')  
    packet_in.icmpv6_type = 1
    packet_in.icmpv6_code = 2 
    packet_in.ipv6_nd_target = IPAddr.new('2001:A55:0:0:1:511:1C:555B')
    packet_in.ipv6_nd_sll = Trema::Mac.new('ff:ff:bb:cc:ee:ff')
    packet_in.ipv6_nd_tll = Trema::Mac.new('ee:ee:bb:cc:ee:ff')
    packet_in.mpls_label = 1 
    packet_in.mpls_tc = 2 
    packet_in.mpls_bos = 3
    packet_in.pbb_isid = 4

    ini
    @base_controller.packet_in(0x9001, packet_in)
  end     

  def test_barrier_reply_not_driver_ready
    @base_controller.expects(:_driver_ready?).returns(false).once
    message = Trema::Messages::BarrierReply.new(0x01)

    ini
    @base_controller.barrier_reply(0x9001, message)
  end
  
  def test_barrier_reply_faild_txinfo_nil
    @base_controller.expects(:_driver_ready?).returns(true).once
    message = Trema::Messages::BarrierReply.new(0x01)

    ini
    trans = Transactions.new(OpenFlowController::OFD_TXID)
    trans.add_transaction(0x01, nil)
    @base_controller.instance_variable_set(:@transactions, trans)
    @base_controller.barrier_reply(0x9001, message)
  end
  
  def test_barrier_reply_success
    @base_controller.expects(:_driver_ready?).returns(true).once
    message = Trema::Messages::BarrierReply.new(0x01)

    ini
    trans = Transactions.new(OpenFlowController::OFD_TXID)

    flow_body = {
      'type'=> 'BasicFlow', 'version'=> 'V02',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> 'none', 'attributes'=> {},
      'matches'=> [], 'path'=> @port_prev_obj,
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::Flow.new(flow_body)   
    flow_entry = []

    trans.add_transaction(0x01,
      {:type => :flowentry,
        :flow => flow_obj,
        :flowentry => flow_entry})

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(flow_obj.flow_id, nw_if).returns(flow_obj).once
    @base_controller.expects(:flowentry_state_trans_success).
      with(flow_obj, flow_entry).once

    @base_controller.instance_variable_set(:@transactions, trans)
    @base_controller.barrier_reply(0x9001, message)
  end
  
  def test_openflow_error_not_driver_ready
    @base_controller.expects(:_driver_ready?).returns(false).once
    message = Trema::Messages::Error.new(0x01)

    ini
    @base_controller.openflow_error(0x9001, message)
  end

  def test_openflow_error_success
    @base_controller.expects(:_driver_ready?).returns(true).once
    message = Trema::Messages::Error.new(0x01)
    ini
    trans = Transactions.new(OpenFlowController::OFD_TXID)

    flow_body = {
      'type'=> 'BasicFlow', 'version'=> 'V02',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> 'none', 'attributes'=> {},
      'matches'=> [], 'path'=> @port_prev_obj,
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::Flow.new(flow_body)   
    flow_entry = []

    trans.add_transaction(0x01,
      {:type => :flowentry,
        :flow => flow_obj,
        :flowentry => flow_entry})

    @base_controller.expects(:flowentry_state_trans_failed).
      with(flow_obj, flow_entry).once

    @base_controller.instance_variable_set(:@transactions, trans)
    @base_controller.openflow_error(0x9001, message)
  end
  
  def test_flow_removed_not_driver_ready
    @base_controller.expects(:_driver_ready?).returns(false).once
    message = Trema::Messages::FlowRemoved.new(0x01)

    ini
    @base_controller.flow_removed(0x9001, message)
  end
  
  def test_flow_removed_success
    @base_controller.expects(:_driver_ready?).returns(true).once
    message = Trema::Messages::FlowRemoved.new(0x01)

    ini
    @base_controller.expects(:propagate_of_flow_removed).
      with(0x9001, message).once
    @base_controller.flow_removed(0x9001, message)
  end

  #######################################
  # Methods for OpenFlowDriver's Event
  #######################################
  
  def test_register_driver_component_success_empty_topology
    logicAndNetwork = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'} 

    logicAndNetwork_obj = LogicAndNetwork.new(logicAndNetwork)
    component = mock() 
    sys_if = mock()

    begin
      @base_controller.start
    rescue => ex
    end
    component.expects(:remote_object_id).
      returns("component_id").once
    component.expects(:system_manager_interface).
      returns(sys_if).once
    sys_if.expects(:put_connection).
      with(logicAndNetwork_obj).once
    @base_controller.register_driver_component(
      component, logicAndNetwork_obj)
  end

  def test_register_driver_component_success_not_empty_topology
    ini
    topology = Driver::OFDriver::RubyTopology::Topology.new
    @base_controller.instance_variable_set(:@topology, topology)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09001)
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09002)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port011 = Trema::Messages::Port.new
    port011.port_no = 1
    topology.add_port(0x9001, port011)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port012 = Trema::Messages::Port.new
    port012.port_no = 2 
    topology.add_port(0x9001, port012)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port021 = Trema::Messages::Port.new
    port021.port_no = 1
    topology.add_port(0x9002, port021)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).returns(nil).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).returns(nil).once
    port022 = Trema::Messages::Port.new
    port022.port_no = 2 
    topology.add_port(0x9002, port022)
    
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: port012.port_no()).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x09002, data.unpack('C*'))
    packet_in.in_port = port021.port_no()
    packet_in.eth_type = OpenFlowController::TYPE_LLDP
    topology.add_link_by(0x9002, packet_in)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    data = Pio::Lldp.new(dpid: 0x9002, port_number: port021.port_no()).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x09001, data.unpack('C*'))
    packet_in.in_port = port012.port_no()
    packet_in.eth_type = OpenFlowController::TYPE_LLDP
    topology.add_link_by(0x9001, packet_in)
    
    logicAndNetwork = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'} 
    logicAndNetwork_obj = LogicAndNetwork.new(logicAndNetwork)
    component = mock() 
    sys_if = mock()

    component.expects(:remote_object_id).returns("component_id").once

    @base_controller.expects(:switch_added).with(anything).once
    @base_controller.expects(:port_added).with(anything, anything).once
    @base_controller.expects(:port_added).with(anything, anything).once
    @base_controller.expects(:switch_added).with(anything).once
    @base_controller.expects(:port_added).with(anything, anything).once
    @base_controller.expects(:port_added).with(anything, anything).once
    @base_controller.expects(:link_added).with(anything).once
    @base_controller.expects(:link_added).with(anything).once

    component.expects(:system_manager_interface).returns(sys_if).once
    sys_if.expects(:put_connection).
      with(logicAndNetwork_obj).once
    @base_controller.register_driver_component(
      component, logicAndNetwork_obj)
  end
  
  def test_register_driver_component_failed_rescue
    ini
    topology = Driver::OFDriver::RubyTopology::Topology.new
    @base_controller.instance_variable_set(:@topology, topology)
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09001)

    component = mock() 
    component.expects(:remote_object_id).returns("component_id").once
    @base_controller.expects(:switch_added).with(anything).raises().once
    @base_controller.register_driver_component(component, nil)
  end

  def test_unregister_driver_component_success_not_empty_topology
    ini
    topology = Driver::OFDriver::RubyTopology::Topology.new
    @base_controller.instance_variable_set(:@topology, topology)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09001)
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09002)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port011 = Trema::Messages::Port.new
    port011.port_no = 1
    topology.add_port(0x9001, port011)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port012 = Trema::Messages::Port.new
    port012.port_no = 2 
    topology.add_port(0x9001, port012)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    port021 = Trema::Messages::Port.new
    port021.port_no = 1
    topology.add_port(0x9002, port021)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).returns(nil).once
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).returns(nil).once
    port022 = Trema::Messages::Port.new
    port022.port_no = 2 
    topology.add_port(0x9002, port022)
    
    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    data = Pio::Lldp.new(dpid: 0x9001, port_number: port012.port_no()).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x09002, data.unpack('C*'))
    packet_in.in_port = port021.port_no()
    packet_in.eth_type = OpenFlowController::TYPE_LLDP
    topology.add_link_by(0x9002, packet_in)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    data = Pio::Lldp.new(dpid: 0x9002, port_number: port021.port_no()).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x09001, data.unpack('C*'))
    packet_in.in_port = port012.port_no()
    packet_in.eth_type = OpenFlowController::TYPE_LLDP
    topology.add_link_by(0x9001, packet_in)
    
    logicAndNetwork = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'} 
    logicAndNetwork_obj = LogicAndNetwork.new(logicAndNetwork)
    component = mock() 
    sys_if = mock()

    component.expects(:system_manager_interface).returns(sys_if).once
    sys_if.expects(:put_connection).with(logicAndNetwork_obj).once

    @base_controller.expects(:link_removed).with(anything).once
    @base_controller.expects(:link_removed).with(anything).once
    @base_controller.expects(:port_removed).with(anything, anything).once
    @base_controller.expects(:port_removed).with(anything, anything).once
    @base_controller.expects(:switch_removed).with(anything).once
    @base_controller.expects(:port_removed).with(anything, anything).once
    @base_controller.expects(:port_removed).with(anything, anything).once
    @base_controller.expects(:switch_removed).with(anything).once

    component.expects(:system_manager_interface).returns(sys_if).once
    sys_if.expects(:put_connection).with(logicAndNetwork_obj).once
    @base_controller.unregister_driver_component(
      component, logicAndNetwork_obj)
  end

  def test_unregister_driver_component_failed_rescue
    ini
    topology = Driver::OFDriver::RubyTopology::Topology.new
    @base_controller.instance_variable_set(:@topology, topology)

    topology.expects(:changed).once
    topology.expects(:notify_observers).with(anything).once
    topology.add_switch(0x09001)
    
    logicAndNetwork = {
      'id'=> 'of_driver_network1', 'type'=> 'LogicAndNetwork',
      'connection_type'=> 'original', 'state'=> 'running',
      'logic_id'=> 'of_driver', 'network_id'=> 'network1'} 
    logicAndNetwork_obj = LogicAndNetwork.new(logicAndNetwork)
    component = mock() 
    sys_if = mock()

    component.expects(:system_manager_interface).returns(sys_if).once
    sys_if.expects(:put_connection).with(logicAndNetwork_obj).once

    @base_controller.expects(:switch_removed).with(anything).raises().once

    @base_controller.unregister_driver_component(
      component, logicAndNetwork_obj)
  end

  def test_on_out_packet_added_success_out_packets_empty
    ini
    out_packet_head = {
      'type'=> "OFPFlowMatch", 'in_node'=> "OutNodeId1",
      'Out_port'=> "OutPortId1"}
    ports = []
    ports_except = []
    out_packet_body = {
      'packet_id'=> 'PacketId01', 'type'=> 'OFPOutPacket',
      'attributes'=> {'attributes'=> 'attributes_value'},
      'node'=> "NodeId1", 'ports'=> ports,
      'ports-except' => ports_except,
      'header'=> out_packet_head, 'data'=> {}}
    out_packet_obj = 
      Odenos::Component::OFPOutPacket.new(out_packet_body)
    data = mock()
    out_packet_obj.data = Marshal.dump(data)
    
    op2od_node = {}
    op2od_node[0x09001] = "node01"
    op2od_node[0x09002] = "node02"
    op2od_node[0x09003] = "node03"
    @base_controller.instance_variable_set(:@op2od_node, op2od_node)
   
    @base_controller.expects(:send_packet_out).
      with(0x09001, anything, anything).once
    @base_controller.expects(:send_packet_out).
      with(0x09002, anything, anything).once
    @base_controller.expects(:send_packet_out).
      with(0x09003, anything, anything).once
      
    @base_controller.on_out_packet_added(out_packet_obj)
  end

  def test_on_out_packet_added_success_out_packets_not_empty
    ini
    out_packet_head = {
      'type'=> "OFPFlowMatch", 'in_node'=> "OutNodeId1",
      'Out_port'=> "OutPortId1"}
    ports = ["port011", "port012"]
    ports_except = []
    out_packet_body = {
      'packet_id'=> 'PacketId01', 'type'=> 'OFPOutPacket',
      'attributes'=> {'attributes'=> 'attributes_value'},
      'node'=> "node01", 'ports'=> ports,
      'ports-except' => ports_except,
      'header'=> out_packet_head, 'data'=> {}}
    out_packet_obj = 
      Odenos::Component::OFPOutPacket.new(out_packet_body)
    data = mock()
    out_packet_obj.data = Marshal.dump(data)
    
    @base_controller.expects(:get_dpid).
      with("node01").returns(0x09001).once

    @base_controller.expects(:get_of_port_no).
      with("node01", "port011").returns(0x001).once
    @base_controller.expects(:get_of_port_no).
      with("node01", "port012").returns(0x002).once

    @base_controller.expects(:send_packet_out).
      with(0x09001, anything, anything).once
      
    @base_controller.on_out_packet_added(out_packet_obj)
  end

  def test_on_out_packet_added_failed_rescue_get_dpid
    ini
    out_packet_head = {
      'type'=> "OFPFlowMatch", 'in_node'=> "OutNodeId1",
      'Out_port'=> "OutPortId1"}
    ports = ["port011", "port012"]
    ports_except = []
    out_packet_body = {
      'packet_id'=> 'PacketId01', 'type'=> 'OFPOutPacket',
      'attributes'=> {'attributes'=> 'attributes_value'},
      'node'=> "node01", 'ports'=> ports,
      'ports-except' => ports_except,
      'header'=> out_packet_head, 'data'=> {}}
    out_packet_obj = 
      Odenos::Component::OFPOutPacket.new(out_packet_body)
    data = mock()
    out_packet_obj.data = Marshal.dump(data)
    
    @base_controller.expects(:get_dpid).
      with("node01").returns(0x09001).raises().once

    @base_controller.on_out_packet_added(out_packet_obj)   
  end
  
  def test_on_flow_added_failed_flow_entries_nil
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    flowentries = mock()
    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(flowentries).once

    @base_controller.on_flow_added(flow_obj)
  end

  def test_on_flow_added_failed_flow_enabled_false
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> false, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    flowentries = nil
    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(flowentries).once

    @base_controller.on_flow_added(flow_obj)
  end

  def test_on_flow_added_success_new_flow_entries_not_empty
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(nil).once

    new_flowentries = mock()
    @base_controller.expects(:flow_to_flowentries).
      with(flow_obj).returns(new_flowentries).once

    @base_controller.expects(:register_flow_entries).
      with(flow_obj.flow_id, new_flowentries).once
      
    new_flowentries.expects(:empty?).returns(false).once

    @base_controller.expects(:setup_flowentries).
      with(flow_obj, new_flowentries).once

    @base_controller.on_flow_added(flow_obj)
  end

  def test_on_flow_added_failed_rescue
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(nil).raises().once

    @base_controller.on_flow_added(flow_obj)
  end
  
  def test_on_flow_update_failed_flow_entries_nil
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(nil).once

    @base_controller.on_flow_update(flow_obj)
  end

  def test_on_flow_update_success_flow_enabled_false
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> false, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    flowentries = mock()
    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(flowentries).once

    @base_controller.expects(:on_flow_update_disabled).
      with(flow_obj, flowentries).once

    @base_controller.on_flow_update(flow_obj)
  end

  def test_on_flow_update_success_flow_enabled_true
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    flowentries = mock()
    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(flowentries).once

    @base_controller.expects(:on_flow_update_enabled).
      with(flow_obj, flowentries).once

    @base_controller.on_flow_update(flow_obj)
  end

  def test_on_flow_update_failed_rescue_lookup_flow_entries
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    flowentries = mock()
    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(flowentries).raises().once

    @base_controller.on_flow_update(flow_obj)
  end

  def test_on_flow_delete_success_to_teardown_not_empry
#    flow_matches = [{
#      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
#      'in_port '=> 'any'}]
#    flow_edge_actions ={
#      'node01' => [{
#        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
#    flow_attributes = {
#      'req_bandwidth' => 10, 'bandwidth' => 11,
#      'req_latency' => 20, 'latency' => 21}
#    flow_priority = {256 => ["flow01"]}
#    flow_path = ['link01']
#    flow_body = {
#      "type"=> "BasicFlow", "version"=> "v01",
#      "flow_id"=> "flow01", "owner"=> "owner",
#      "enabled"=> true, "priority"=> 256,
#      "status"=> "none", "attributes"=> flow_attributes,
#      "matches"=> flow_matches, "path"=> flow_path,
#      "edge_actions"=> flow_edge_actions}
#    flow_obj = Odenos::Component::Flow.new(flow_body)
#
#    flowentries = []
#    flowentries << Driver::OFDriver::FlowEntry.new(
#      dpid: 0x09001, match: mock(),
#      actions: mock(), status: Flow::ESTABLISHED)
#    flowentries << Driver::OFDriver::FlowEntry.new(
#      dpid: 0x09001, match: mock(),
#      actions: mock(), status: Flow::ESTABLISHING)
#
#    @base_controller.expects(:lookup_flow_entries).
#      with(flow_obj.flow_id).returns(flowentries).once
#
#    @base_controller.expects(:unregister_flow_entries).
#      with(flow_obj.flow_id).once
#
#    @base_controller.expects(:teardown_flowentries).
#      with(flow_obj, anything).once
#
#    @base_controller.on_flow_delete(flow_obj)
  end

  def test_on_flow_delete_failed_rescue_lookup_flow_entries
    flow_matches = [{
      'type' => 'BasicFlowMatch', 'in_node' => 'node01',
      'in_port '=> 'any'}]
    flow_edge_actions ={
      'node01' => [{
        'type' => 'FlowActionOutput', 'output'=> 'any'}]}
    flow_attributes = {
      'req_bandwidth' => 10, 'bandwidth' => 11,
      'req_latency' => 20, 'latency' => 21}
    flow_priority = {256 => ["flow01"]}
    flow_path = ['link01']
    flow_body = {
      "type"=> "BasicFlow", "version"=> "v01",
      "flow_id"=> "flow01", "owner"=> "owner",
      "enabled"=> true, "priority"=> 256,
      "status"=> "none", "attributes"=> flow_attributes,
      "matches"=> flow_matches, "path"=> flow_path,
      "edge_actions"=> flow_edge_actions}
    flow_obj = Odenos::Component::Flow.new(flow_body)

    @base_controller.expects(:lookup_flow_entries).
      with(flow_obj.flow_id).returns(mock()).raises().once

    @base_controller.on_flow_delete(flow_obj)
  end

  #######################################
  # Test Methods for OpenFlowDriver's Request
  #######################################

  def test_on_get_node_maps
    ini
    resp_body = @base_controller.on_get_node_maps
    assert(resp_body.is_a?(Hash), "failed") 
  end

  def test_on_get_port_maps
    ini
    resp_body = @base_controller.on_get_port_maps
    assert(resp_body.is_a?(Hash), "failed") 
  end

  def test_on_get_link_maps
    ini
    resp_body = @base_controller.on_get_link_maps
    assert(resp_body.is_a?(Hash), "failed") 
  end

  def test_on_get_flow_maps
    ini
    resp_body = @base_controller.on_get_flow_maps
    assert(resp_body.is_a?(Hash), "failed") 
  end
  
  #########################################
  # Test Methods for TopologyControler's Event
  #########################################
  
  def test_switch_added_failed_rescue_deleteNode
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    node.expects(:node_id).returns("node01").once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deleteNode).
      with(node, nw_if).raises().once
    @base_controller.expects(:unregister_node).with(0x09001).once

    @base_controller.expects(:_set_node_attributes).
      with(anything, 0x09001).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(anything, nw_if).returns(node).once
    @base_controller.expects(:register_node).
      with(0x09001, node).once

    @base_controller.send(:switch_added, 0x09001)
  end

  def test_switch_added_failed_rescue_putNode
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    node.expects(:node_id).returns("node01").once
    node.expects(:version).returns("1").once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deleteNode).
      with(node, nw_if).raises().once
    @base_controller.expects(:unregister_node).with(0x09001).once

    @base_controller.expects(:_set_node_attributes).
      with(anything, 0x09001).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:get_node).with(anything).returns(node).once
    nw_if.expects(:put_node).with(anything).returns(resp).once
    
    @base_controller.send(:switch_added, 0x09001)
  end

  def test_switch_added_success_lookup_node_not_nil
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deleteNode).with(node, nw_if).once
    @base_controller.expects(:unregister_node).with(0x09001).once

    @base_controller.expects(:invalidate_flow_using_switch).
      with(0x09001).once

    @base_controller.send(:switch_removed, 0x09001)
  end

  def test_switch_removed_success_lookup_node_nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(nil).once
    
    @base_controller.expects(:unregister_node).with(0x09001).once
    @base_controller.expects(:invalidate_flow_using_switch).
      with(0x09001).once

    @base_controller.send(:switch_removed, 0x09001)
  end

  def test_switch_removed_failed_rescue_deleteNode
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    node.expects(:node_id).returns("node01").once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_node).with("node01").returns(resp).once
    node.expects(:node_id).returns("node01").once

    @base_controller.expects(:unregister_node).with(0x09001).once

    @base_controller.expects(:invalidate_flow_using_switch).
      with(0x09001).once

    @base_controller.send(:switch_removed, 0x09001)
  end
  
  def test_switch_updated_success_lookup_node_nil
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    node.expects(:nil?).returns(true).once

    @base_controller.expects(:switch_added).with(0x09001).returns(nil).once
    
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    @base_controller.expects(:_set_node_attributes).
      with(node, 0x09001).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(node, nw_if).returns(node).once

    @base_controller.expects(:register_node).with(0x09001, node).once

    @base_controller.send(:switch_updated, 0x09001)
  end

  def test_switch_updated_failed_rescue_putNode
    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    node.expects(:nil?).returns(true).once

    @base_controller.expects(:switch_added).with(0x09001).returns(nil).once

    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    @base_controller.expects(:_set_node_attributes).
      with(node, 0x09001).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    node.expects(:nil?).returns(true).once

    @base_controller.send(:switch_updated, 0x09001)
  end

  def test_port_added_failed_lookup_node_nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(nil).once

    of_port = Trema::Messages::Port.new
    @base_controller.send(:port_added, 0x09001, of_port)
  end

  def test_port_added_failed_rescue_deletePort
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    port.expects(:node_id).returns("node01").once
    port.expects(:port_id).returns("port011").once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_port).with(anything, anything).
      returns(resp).once

    node.expects(:node_id).returns("node01").once

    @base_controller.expects(:_set_port_attributes).
      with(anything, 0x09001, of_port).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putPort).
      with(anything, nw_if).returns(port).once

    @base_controller.expects(:register_port).
      with(0x09001, 1, anything).once

    ports = mock() 
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once
    ports.expects(:include?).returns(true).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(node, nw_if).returns(node).once

    @base_controller.expects(:register_node).
      with(0x09001, node).once

    @base_controller.send(:port_added, 0x09001, of_port)
  end
  
  def test_port_added_success_ports_include_true
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deletePort).
      with(port, nw_if).returns(port).once

    node.expects(:node_id).returns("node01").once

    @base_controller.expects(:_set_port_attributes).
      with(anything, 0x09001, of_port).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putPort).
      with(anything, nw_if).returns(port).once

    @base_controller.expects(:register_port).
      with(0x09001, 1, anything).once

    ports = mock() 
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once
    ports.expects(:include?).returns(true).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(node, nw_if).returns(node).once

    @base_controller.expects(:register_node).
      with(0x09001, node).once

    @base_controller.send(:port_added, 0x09001, of_port)
  end

  def test_port_added_success_ports_include_false
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deletePort).
      with(port, nw_if).returns(port).once

    node.expects(:node_id).returns("node01").once

    @base_controller.expects(:_set_port_attributes).
      with(anything, 0x09001, of_port).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putPort).
      with(anything, nw_if).returns(port).once

    @base_controller.expects(:register_port).
      with(0x09001, 1, anything).once

    ports = {}
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(node, nw_if).returns(node).once

    @base_controller.expects(:register_node).
      with(0x09001, node).once

    @base_controller.send(:port_added, 0x09001, of_port)
  end

  def test_port_added_failed_rescue_putNode
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once

    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deletePort).
      with(port, nw_if).returns(port).once

    node.expects(:node_id).returns("node01").once

    @base_controller.expects(:_set_port_attributes).
      with(anything, 0x09001, of_port).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putPort).
      with(anything, nw_if).returns(port).once

    @base_controller.expects(:register_port).
      with(0x09001, 1, anything).once

    ports = {}
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once
    port.expects(:port_id).returns("1").once
    node.expects(:ports).returns(ports).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    node.expects(:node_id).returns("node01").once
    nw_if.expects(:get_node).with("node01").returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_node).with(node).returns(resp).once
    node.expects(:node_id).returns("node01").once

    @base_controller.send(:port_added, 0x09001, of_port)
  end
  
  def test_port_removed_failed_lookup_node_nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(nil).once

    of_port = Trema::Messages::Port.new
    @base_controller.send(:port_removed, 0x09001, of_port)
  end

  def test_port_removed_success_port_not_nil
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deletePort).
      with(port, nw_if).returns(port).once

    @base_controller.expects(:unregister_port).with(0x09001, 1).once
      
    ports = {}
    port.expects(:port_id).returns("port011").once
    node.expects(:ports).returns(ports).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putNode).
      with(node, nw_if).returns(node).once

    @base_controller.expects(:register_node).with(0x09001, node).once

    @base_controller.expects(:invalidate_flow_using_port).
      with(0x09001, 1).once

    @base_controller.send(:port_removed, 0x09001, of_port)
  end

  def test_port_removed_failed_rescue_putNode
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(nil).once

    @base_controller.expects(:invalidate_flow_using_port).
      with(0x09001, 1).once

    @base_controller.send(:port_removed, 0x09001, of_port)
  end

  def test_port_removed_success_port_nil
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:deletePort).
      with(port, nw_if).returns(port).once

    @base_controller.expects(:unregister_port).with(0x09001, 1).once
      
    ports = {}
    port.expects(:port_id).returns("port011").once
    node.expects(:ports).returns(ports).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    node.expects(:node_id).returns("node01").once
    nw_if.expects(:get_node).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_node).with(anything).returns(resp).once
    port.expects(:inspect).returns("").once

    @base_controller.expects(:invalidate_flow_using_port).
      with(0x09001, 1).once

    @base_controller.send(:port_removed, 0x09001, of_port)
  end

  def test_port_updated_failed_lookup_node_nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(nil).once

    of_port = Trema::Messages::Port.new
    @base_controller.send(:port_updated, 0x09001, of_port)
  end

  def test_port_updated_success
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    port.expects(:nil?).returns(true)
    @base_controller.expects(:port_added).with(0x09001, of_port).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    @base_controller.expects(:_set_port_attributes).
      with(port, 0x09001, of_port).once
    @base_controller.expects(:_is_port_up).
      with(of_port).returns(true).once
    @base_controller.expects(:_is_link_up).
      with(of_port).returns(false).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putPort).
      with(port, nw_if).returns(port).once

    @base_controller.expects(:register_port).
      with(0x09001, 1, port).returns(port).once

    @base_controller.expects(:invalidate_flow_using_port).
      with(0x09001, 1).once

    @base_controller.send(:port_updated, 0x09001, of_port)
  end

  def test_port_updated_failed_rescue_putPort
    of_port = Trema::Messages::Port.new
    of_port.port_no = 1

    node = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(node).once
    port = mock()
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    port.expects(:nil?).returns(true)
    @base_controller.expects(:port_added).with(0x09001, of_port).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 1).returns(port).once

    @base_controller.expects(:_set_port_attributes).
      with(port, 0x09001, of_port).once
    @base_controller.expects(:_is_port_up).
      with(of_port).returns(true).once
    @base_controller.expects(:_is_link_up).
      with(of_port).returns(false).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    port.expects(:nil?).returns(true).once

    @base_controller.expects(:invalidate_flow_using_port).
      with(0x09001, 1).once

    @base_controller.send(:port_updated, 0x09001, of_port)
  end
  
  def test_link_added_success_rescue_deleteLink
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = mock()
    dst_node = mock()
    src_port = mock()
    dst_port = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 0x01).returns(src_port).once
    @base_controller.expects(:lookup_node).
      with(0x09002).returns(dst_node).once
    @base_controller.expects(:lookup_port).
      with(0x09002, 0x02).returns(dst_port).once

    src_node.expects(:node_id).returns("src_node").once
    src_port.expects(:port_id).returns("src_port").once
    dst_node.expects(:node_id).returns("dst_node").once
    dst_port.expects(:port_id).returns("dst_port").once

    @base_controller.expects(:_set_link_attributes).
      with(anything, anything).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:postLink).
      with(anything, nw_if).returns(link).once

    @base_controller.expects(:register_link).
      with(anything, link).once

    @base_controller.send(:link_added, of_link)
  end
     
  def test_link_added_failed_src_node_nil
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once

    @base_controller.send(:link_added, of_link)
  end

  def test_link_added_failed_src_port_nil
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = mock()
    src_port = nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 0x01).returns(src_port).once

    @base_controller.send(:link_added, of_link)
  end

  def test_link_added_failed_dst_node_nil
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = mock()
    src_port = mock()
    dst_node = nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 0x01).returns(src_port).once
    @base_controller.expects(:lookup_node).
      with(0x09002).returns(dst_node).once

    @base_controller.send(:link_added, of_link)
  end

  def test_link_added_failed_dst_port_nil
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = mock()
    dst_node = mock()
    src_port = mock()
    dst_port = nil
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 0x01).returns(src_port).once
    @base_controller.expects(:lookup_node).
      with(0x09002).returns(dst_node).once
    @base_controller.expects(:lookup_port).
      with(0x09002, 0x02).returns(dst_port).once

    @base_controller.send(:link_added, of_link)
  end

  def test_link_added_failed_rescue_postLink
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    src_node = mock()
    dst_node = mock()
    src_port = mock()
    dst_port = mock()
    @base_controller.expects(:lookup_node).
      with(0x09001).returns(src_node).once
    @base_controller.expects(:lookup_port).
      with(0x09001, 0x01).returns(src_port).once
    @base_controller.expects(:lookup_node).
      with(0x09002).returns(dst_node).once
    @base_controller.expects(:lookup_port).
      with(0x09002, 0x02).returns(dst_port).once

    src_node.expects(:node_id).returns("src_node").once
    src_port.expects(:port_id).returns("src_port").once
    dst_node.expects(:node_id).returns("dst_node").once
    dst_port.expects(:port_id).returns("dst_port").once

    @base_controller.expects(:_set_link_attributes).
      with(anything, anything).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:post_link).with(anything).returns(resp).once

    @base_controller.send(:link_added, of_link)
  end

  def test_link_removed_failed_rescue_deleteLink
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = Link.new("src_node", "src_port", "dst_node", "dst_port")
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:del_link).with(anything).returns(resp).once

    @base_controller.expects(:unregister_link).
      with(anything).once

    @base_controller.expects(:invalidate_flow_using_link).
      with(anything).once

    @base_controller.send(:link_removed, of_link)
  end

  def test_link_removed_failed_link_nil
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = nil
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    @base_controller.expects(:unregister_link).
      with(anything).once

    @base_controller.expects(:invalidate_flow_using_link).
      with(anything).once

    @base_controller.send(:link_removed, of_link)
  end
  
  def test_link_updated_failed_putLink
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = mock()
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    link.expects(:nil?).returns(true).once

    @base_controller.expects(:link_added).with(anything).once

    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    link.expects(:nil?).returns(true).once

    @base_controller.send(:link_updated, of_link)
  end

  def test_link_updated_success
    data = Pio::Lldp.new(dpid: 0x9001, port_number: 0x01).to_binary
    packet_in = Trema::Messages::PacketIn.new(0x02, data.unpack('C*'))
    of_link = RubyTopology::Link.new(0x9002, packet_in)

    link = mock()
    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    link.expects(:nil?).returns(true).once

    @base_controller.expects(:link_added).with(anything).once

    @base_controller.expects(:lookup_link).
      with(anything).returns(link).once
    
    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putLink).
      with(anything, nw_if).returns(link).once

    @base_controller.expects(:register_link).
      with(anything, link).once

    @base_controller.send(:link_updated, of_link)
  end

  ##########################################
  # Test Methods for Flow Setting
  ##########################################
  
  def test_invalidate_flow_using_switch_success
    od2op_flow = {}
    component = mock()
    flowentries = []
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: mock(), status: Flow::ESTABLISHED)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: mock(), status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
    @base_controller.instance_variable_set(:@component, component)

    nw_if = mock()
    flow = Flow.new 
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow).once
   
    component.expects(:_is_valid_flow).with(flow).returns(false).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).with(flow, nw_if).once

    @base_controller.send(:invalidate_flow_using_switch, 0x09001)
  end

  def test_invalidate_flow_using_switch_failed_getFlow
    od2op_flow = {}
    component = mock()
    flowentries = []
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: mock(), status: Flow::ESTABLISHED)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: mock(), status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
    @base_controller.instance_variable_set(:@component, component)

    nw_if = mock()
    flow = Flow.new 
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow).once
   
    component.expects(:_is_valid_flow).with(flow).returns(false).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(:invalidate_flow_using_switch, 0x09001)
  end

  def test_invalidate_flow_using_port_success
    od2op_flow = {}
    component = mock()
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
    @base_controller.instance_variable_set(:@component, component)

    nw_if = mock()
    flow = Flow.new 
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow).once
   
    #component.expects(:_is_valid_flow).with(flow).returns(false).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).with(flow, nw_if).once

    @base_controller.send(:invalidate_flow_using_port, 0x09001, 0x01)
  end

  def test_invalidate_flow_using_port_failed_putFlow
    od2op_flow = {}
    component = mock()
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
    @base_controller.instance_variable_set(:@component, component)

    nw_if = mock()
    flow = Flow.new 
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow).once
   
    #component.expects(:_is_valid_flow).with(flow).returns(false).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(:invalidate_flow_using_port, 0x09001, 0x01)
  end

  def test_invalidate_flow_using_link
    link_4tup = [0x09001, 0x01, 0x09002, 0x02]

    @base_controller.expects(:invalidate_flow_using_port).
      with(link_4tup[0], link_4tup[1]).once
    @base_controller.send(:invalidate_flow_using_link, link_4tup)
  end

  def test_on_flow_update_enabled_success_status_establishing
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::NONE, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHING)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)

    @base_controller.expects(:flow_to_flowentries).
      with(flow_obj).returns(flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once

    @base_controller.send(
      :on_flow_update_enabled, flow_obj, flowentries)
  end

  def test_on_flow_update_enabled_success_status_teardown
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::NONE, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::TEARDOWN)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)

    @base_controller.expects(:flow_to_flowentries).
      with(flow_obj).returns(flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once

    @base_controller.send(
      :on_flow_update_enabled, flow_obj, flowentries)
  end

  def test_on_flow_update_enabled_failed_rescue_putFlow
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::NONE, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::TEARDOWN)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)

    @base_controller.expects(:flow_to_flowentries).
      with(flow_obj).returns(flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(
      :on_flow_update_enabled, flow_obj, flowentries)
  end
  
  def test_on_flow_update_disabled_success_flowentries_empty
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []

    @base_controller.expects(:teardown_flowentries).
      with(flow_obj, flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once

    @base_controller.send(
      :on_flow_update_disabled, flow_obj, flowentries)
  end

  def test_on_flow_update_disabled_success_flowentries_not_empty
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x01)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::TEARDOWN)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: mock(),
      actions: actions, status: Flow::ESTABLISHED)

    @base_controller.expects(:teardown_flowentries).
      with(flow_obj, flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once

    @base_controller.send(
      :on_flow_update_disabled, flow_obj, flowentries)
  end

  def test_on_flow_update_disabled_failed_rescue_putFlow
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'FlowId01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentries = []

    @base_controller.expects(:teardown_flowentries).
      with(flow_obj, flowentries).once

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with(anything, nw_if).returns(flow_obj).once
    @base_controller.expects(:_nw_if).returns(nw_if).once

    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(
      :on_flow_update_disabled, flow_obj, flowentries)
  end

  def test_propagate_of_flow_removed_success_to_status_none
#    message = Trema::Messages::FlowRemoved.new(0x1234)
#    match = Trema::Match.new(in_port: 0x01)
#    message.match = match
#    flow_body = {
#      'type'=> 'OFPFlow', 'version'=> 'V01',
#      'flow_id'=> 'flow01', 'owner'=> 'Owner',
#      'enabled'=> true, 'priority'=> 256,
#      'status'=> Flow::TEARDOWN, 'attributes'=> {},
#      'matches'=> [], 'path'=> [],
#      'edge_actions'=> {}}
#    flow_obj = Odenos::Component::OFPFlow.new(flow_body)
#
#    od2op_flow = {}
#    flowentries = []
#    actions = []
#    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
#    flowentries << Driver::OFDriver::FlowEntry.new(
#      dpid: 0x09001, match: match, 
#      actions: actions, status: Flow::ESTABLISHED)
#    od2op_flow["flow01"] = flowentries   
#
#    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
#
#    nw_if = mock()
#    @base_controller.expects(:_nw_if).returns(nw_if).once
#    @base_controller.expects(:getFlow).
#      with("flow01", nw_if).returns(flow_obj).once
#
#    @base_controller.expects(:_nw_if).returns(nw_if).once
#    @base_controller.expects(:putFlow).
#      with(flow_obj, nw_if).returns(flow_obj).once
#
#    @base_controller.send(
#      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_propagate_of_flow_removed_success_to_status_teardown
#    message = Trema::Messages::FlowRemoved.new(0x1234)
#    match = Trema::Match.new(in_port: 0x01)
#    message.match = match
#    flow_body = {
#      'type'=> 'OFPFlow', 'version'=> 'V01',
#      'flow_id'=> 'flow01', 'owner'=> 'Owner',
#      'enabled'=> true, 'priority'=> 256,
#      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
#      'matches'=> [], 'path'=> [],
#      'edge_actions'=> {}}
#    flow_obj = Odenos::Component::OFPFlow.new(flow_body)
#
#    od2op_flow = {}
#    flowentries = []
#    actions = []
#    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
#    flowentries << Driver::OFDriver::FlowEntry.new(
#      dpid: 0x09001, match: match, 
#      actions: actions, status: Flow::NONE)
#    od2op_flow["flow01"] = flowentries   
#
#    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
#
#    nw_if = mock()
#    @base_controller.expects(:_nw_if).returns(nw_if).once
#    @base_controller.expects(:getFlow).
#      with("flow01", nw_if).returns(flow_obj).once
#
#    @base_controller.expects(:_nw_if).returns(nw_if).once
#    @base_controller.expects(:putFlow).
#      with(flow_obj, nw_if).returns(flow_obj).once
#
#    @base_controller.send(
#      :propagate_of_flow_removed, 0x09001, message)
  end
  
  def test_propagate_of_flow_removed_failed_flowinfo_nil
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)
    od2op_flow = {}

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    @base_controller.send(
      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_propagate_of_flow_removed_failed_flowentries_nil
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::NONE)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    @base_controller.expects(:lookup_flow_entries).returns(nil).once

    @base_controller.send(
      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_propagate_of_flow_removed_failed_flow_nil
#    message = Trema::Messages::FlowRemoved.new(0x1234)
#    match = Trema::Match.new(in_port: 0x01)
#    message.match = match
#    flow_body = {
#      'type'=> 'OFPFlow', 'version'=> 'V01',
#      'flow_id'=> 'flow01', 'owner'=> 'Owner',
#      'enabled'=> true, 'priority'=> 256,
#      'status'=> Flow::TEARDOWN, 'attributes'=> {},
#      'matches'=> [], 'path'=> [],
#      'edge_actions'=> {}}
#    flow_obj = Odenos::Component::OFPFlow.new(flow_body)
#
#    od2op_flow = {}
#    flowentries = []
#    actions = []
#    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
#    flowentries << Driver::OFDriver::FlowEntry.new(
#      dpid: 0x09001, match: match, 
#      actions: actions, status: Flow::ESTABLISHED)
#    od2op_flow["flow01"] = flowentries   
#
#    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)
#
#    nw_if = mock()
#    @base_controller.expects(:_nw_if).returns(nw_if).once
#    @base_controller.expects(:getFlow).
#      with("flow01", nw_if).returns(nil).once
#
#    @base_controller.send(
#      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_propagate_of_flow_removed_failed_rescue_putFlow_case01
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::TEARDOWN, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHED)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(
      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_propagate_of_flow_removed_failed_rescue_putFlow_case02
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::NONE)
    od2op_flow["flow01"] = flowentries   

    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once

    @base_controller.send(
      :propagate_of_flow_removed, 0x09001, message)
  end

  def test_flowentry_state_trans_success_failed_flowentries_nil
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: mock(), status: Flow::NONE)

    od2op_flow = {}
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_success_failed_flowentries_not_include_flowentry
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::NONE)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09002, match: match, 
      actions: actions, status: Flow::NONE)

    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_success_status_establishing
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHING, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHING)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once
      
    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_success_status_teardown
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::TEARDOWN, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once
      
    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end
  
  def test_flowentry_state_trans_success_failed_rescue_putFlow_case01
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHING, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHED)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHED)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once
      
    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_success_failed_rescue_putFlow_case02
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::TEARDOWN, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once
      
    @base_controller.send(
      :flowentry_state_trans_success, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_failed_failed_flowentries_nil
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: mock(), status: Flow::NONE)

    od2op_flow = {}
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    @base_controller.send(
      :flowentry_state_trans_failed, flow_obj, flowentry)
  end

  def test_flowentry_state_trans_failed_failed_flowentries_not_include_flowentry
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHED, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::NONE)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09002, match: match, 
      actions: actions, status: Flow::NONE)

    @base_controller.send(
      :flowentry_state_trans_failed, flow_obj, flowentry)
  end
  
  def test_flowentry_state_trans_failed_status_establishing
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::ESTABLISHING, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHING)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::ESTABLISHING)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once
      
    @base_controller.send(
      :flowentry_state_trans_failed, flow_obj, flowentry)

    assert(!flowentries.empty?, "flowentries empty") 
  end 

  def test_flowentry_state_trans_failed_status_teardown
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::TEARDOWN, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::TEARDOWN)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:putFlow).
      with(flow_obj, nw_if).returns(flow_obj).once
      
    @base_controller.send(
      :flowentry_state_trans_failed, flow_obj, flowentry)

    assert(flowentries.empty?, "flowentries not empty") 
  end
  
  def test_flowentry_state_trans_failed_failed_rescue_putFlow
    message = Trema::Messages::FlowRemoved.new(0x1234)
    match = Trema::Match.new(in_port: 0x01)
    message.match = match
    flow_body = {
      'type'=> 'OFPFlow', 'version'=> 'V01',
      'flow_id'=> 'flow01', 'owner'=> 'Owner',
      'enabled'=> true, 'priority'=> 256,
      'status'=> Flow::TEARDOWN, 'attributes'=> {},
      'matches'=> [], 'path'=> [],
      'edge_actions'=> {}}
    flow_obj = Odenos::Component::OFPFlow.new(flow_body)

    od2op_flow = {}
    flowentries = []
    actions = []
    actions << Trema::Actions::SendOutPort.new(port_number: 0x02)
    flowentries << Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::FAILED)
    od2op_flow["flow01"] = flowentries   
    @base_controller.instance_variable_set(:@od2op_flow, od2op_flow)

    flowentry = Driver::OFDriver::FlowEntry.new(
      dpid: 0x09001, match: match, 
      actions: actions, status: Flow::FAILED)

    nw_if = mock()
    @base_controller.expects(:_nw_if).returns(nw_if).once
    @base_controller.expects(:getFlow).
      with("flow01", nw_if).returns(flow_obj).once

    @base_controller.expects(:_nw_if).returns(nw_if).once
    nw_if.expects(:get_flow).with(anything).returns(nil).once
    resp = Response.new(Response::NOT_FOUND, nil)
    nw_if.expects(:put_flow).with(anything).returns(resp).once
      
    @base_controller.send(
      :flowentry_state_trans_failed, flow_obj, flowentry)
  end

  def test_teardown_flowentries_failed_recue_putFlow
  end

  def test_teardown_flowentries_success
  end
  
  def test_flow_to_flowentries_success_path_empty
  end

  def test_flow_to_flowentries_success_matches_empty
  end

  def test_flow_to_flowentries_success_path_and_matches_not_empty
  end

  #########################################
  # Methods for OpenFlowContoller Support
  #########################################

  def test__set_node_attributes
  end

  def test__set_port_attributes_is_port_up_10gb_fd
  end

  def test__set_port_attributes_is_port_down_1gb_fd
  end

  def test__set_port_attributes_100mb_hd
  end

  def test__set_port_attributes_10mb_fd
  end

  def test__set_link_attributes_dst_bandwidth_greater
  end

  def test__set_link_attributes_src_bandwidth_greater
  end
  
  def test__match_to_trema_match
    match_body = {
      'type'=> 'OFPFlowMatch',
      'in_node'=> 'in_node',
      'in_port'=> '1',
      'in_phy_port'=> '1',
      'metadata'=> '0x1234567812345679',
      'metadata_mask'=> '0x1234567812345679',
      'eth_src'=> 'aa:bb:cc:dd:ee:ff',
      'eth_src_mask'=> 'ff:ff:ff:ff:ff:ff',
      'eth_dst'=> 'ff:ee:dd:cc:bb:aa',
      'eth_dst_mask'=> 'ff:ff:ff:ff:ff:ff',
      'eth_type'=> '0x0800',
      'vlan_vid'=> '2991',
      'vlan_vid_mask'=> '0xFFF',
      'vlan_pcp'=> '1',
      'ip_dscp'=> '1',
      #'ip_ecn'=> '1',
      'ip_proto'=> '6',
      'ipv4_src'=> '192.168.0.1',
      'ipv4_src_mask'=> '255.255.255.255',
      'ipv4_dst'=> '192.168.0.2',
      'ipv4_dst_mask'=> '255.255.255.255',
      'tcp_src'=> '1234',
      'tcp_dst'=> '2345',
      'udp_src'=> '1234',
      'udp_dst'=> '2345',
      'sctp_src'=> '123',
      'sctp_dst'=> '234',
      'icmpv4_type'=> '1',
      'icmpv4_code'=> '2',
      'arp_op'=> '1',
      'arp_spa'=> '192.168.0.1',
      'arp_spa_mask'=> '255.255.255.255',
      'arp_tpa'=> '192.168.0.2',
      'arp_tpa_mask'=> '255.255.255.255',
      'arp_sha'=> 'aa:bb:cc:dd:ee:ff',
      'arp_sha_mask'=> 'ff:ff:ff:ff:ff:ff',
      'arp_tha'=> 'ff:ee:dd:cc:bb:aa',
      'arp_tha_mask'=> 'ff:ff:ff:ff:ff:ff',
      'ipv6_src'=> '2001:0bd8:0000:0000:0000:0000:dead:beaf',
      'ipv6_src_mask'=> 'ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_dst'=> '3002:1ce9:1111:1111:1111:1111:efbe:cfb0',
      'ipv6_dst_mask'=> 'ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_flabel'=> '0x12345',
      'ipv6_flabel_mask'=> '0xfffff',
      'icmpv6_type'=> '1',
      'icmpv6_code'=> '2',
      'ipv6_nd_target'=> '3002:1ce9:1111:1111:1111:1111:efbe:cfb0/ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_nd_sll'=> 'ff:ff:bb:cc:ee:ff',
      'ipv6_nd_tll'=> 'ee:ee:bb:cc:ee:ff',
      'mpls_label'=> '0x12345678',
      'mpls_tc'=> '0x12',
      'mpls_bos'=> '0x34',
      'pbb_isid'=> '0x23456789',
      'pbb_isid_mask'=> '0xffffffff',
      'tunnel_id'=> '0x1234567812345679',
      'tunnel_id_mask'=> '0x1234567812345679',
      'ipv6_exthdr'=> '0x1234',
      'ipv6_exthdr_mask'=> '0xffff'
    }
    
    match_obj = OFPFlowMatch.new(match_body)

    of_match = @base_controller.send(
      :_match_to_trema_match, match_obj, in_port: 0x002)
     
    assert(of_match.is_a?(Trema::Match), "failed") 
    assert(of_match.in_port == 0x002, "match.in_port failed") 
    assert(of_match.in_phy_port == match_obj.in_phy_port.to_i, "match.in_phy_port failed") 
    assert(of_match.metadata == match_obj.metadata.to_i, "match.metadata failed") 
    assert(of_match.metadata_mask == match_obj.metadata_mask.to_i, "match.metadata_mask failed") 
    assert(of_match.eth_src.to_s == Trema::Mac.new(match_obj.eth_src).to_s, "match.eth_src failed") 
    assert(of_match.eth_src_mask.to_s == Trema::Mac.new(match_obj.eth_src_mask).to_s,
      "match.eth_src_mask failed") 
    assert(of_match.eth_dst.to_s == Trema::Mac.new(match_obj.eth_dst).to_s, "match.eth_dst failed") 
    assert(of_match.eth_dst_mask.to_s == Trema::Mac.new(match_obj.eth_dst_mask).to_s,
      "match.eth_dst_mask failed") 
    assert(of_match.eth_type == match_obj.eth_type.to_i, "match.eth_type failed") 
    assert(of_match.vlan_vid == match_obj.vlan_vid.to_i, "match.vlan_vid failed") 
    assert(of_match.vlan_vid_mask == match_obj.vlan_vid_mask.to_i, "match.vlan_vid_mask failed") 
    assert(of_match.vlan_pcp == match_obj.vlan_pcp.to_i, "match.vlan_pcp failed") 
    assert(of_match.ip_dscp == match_obj.ip_dscp.to_i, "match.ip_dscp failed") 
    #assert(of_match.ip_ecn == match_obj.ip_ecn.to_i, "match.ip_ecn failed") 
    assert(of_match.ip_proto == match_obj.ip_proto.to_i, "match.ip_proto failed") 
    assert(of_match.ipv4_src.to_s == IPAddr.new(match_obj.ipv4_src).to_s, "match.ipv4_src failed") 
    assert(of_match.ipv4_src_mask.to_s == IPAddr.new(match_obj.ipv4_src_mask).to_s,
      "match.ipv4_src_mask failed") 
    assert(of_match.ipv4_dst.to_s == IPAddr.new(match_obj.ipv4_dst).to_s, "match.ipv4_dst failed") 
    assert(of_match.ipv4_dst_mask.to_s == IPAddr.new(match_obj.ipv4_dst_mask).to_s,
      "match.ipv4_dst_mask failed") 
    assert(of_match.tcp_src == match_obj.tcp_src.to_i, "match.tcp_src failed") 
    assert(of_match.tcp_dst == match_obj.tcp_dst.to_i, "match.tcp_dst failed") 
    assert(of_match.udp_src == match_obj.udp_src.to_i, "match.udp_src failed") 
    assert(of_match.sctp_src == match_obj.sctp_src.to_i, "match.sctp_src failed") 
    assert(of_match.sctp_dst == match_obj.sctp_dst.to_i, "match.sctp_dst failed") 
    assert(of_match.icmpv4_type == match_obj.icmpv4_type.to_i, "match.icmpv4_type failed") 
    assert(of_match.icmpv4_code == match_obj.icmpv4_code.to_i, "match.icmpv4_code failed") 
    assert(of_match.arp_op == match_obj.arp_op.to_i, "match.arp_op failed") 
    assert(of_match.arp_spa.to_s == IPAddr.new(match_obj.arp_spa).to_s, "match.arp_spa failed") 
    assert(of_match.arp_spa_mask.to_s == IPAddr.new(match_obj.arp_spa_mask).to_s,
      "match.arp_spa_mask failed") 
    assert(of_match.arp_tpa.to_s == IPAddr.new(match_obj.arp_tpa).to_s, "match.arp_tpa failed") 
    assert(of_match.arp_tpa_mask.to_s == IPAddr.new(match_obj.arp_tpa_mask).to_s,
      "match.arp_tpa_mask failed") 
    assert(of_match.arp_sha.to_s == Trema::Mac.new(match_obj.arp_sha).to_s, "match.arp_sha failed") 
    assert(of_match.arp_sha_mask.to_s == Trema::Mac.new(match_obj.arp_sha_mask).to_s,
      "match.arp_sha_mask failed") 
    assert(of_match.arp_tha.to_s == Trema::Mac.new(match_obj.arp_tha).to_s, "match.arp_tha failed") 
    assert(of_match.arp_tha_mask.to_s == Trema::Mac.new(match_obj.arp_tha_mask).to_s,
      "match.arp_tha_mask failed") 
    assert(of_match.ipv6_src.to_s == IPAddr.new(match_obj.ipv6_src).to_s, "match.ipv6_src failed") 
    assert(of_match.ipv6_src_mask.to_s == IPAddr.new(match_obj.ipv6_src_mask).to_s,
      "match.ipv6_src_mask failed") 
    assert(of_match.ipv6_dst.to_s == IPAddr.new(match_obj.ipv6_dst).to_s, "match.ipv6_dst failed") 
    assert(of_match.ipv6_dst_mask.to_s == IPAddr.new(match_obj.ipv6_dst_mask).to_s, "match.ipv6_dst_mask failed") 
    assert(of_match.ipv6_flabel == match_obj.ipv6_flabel.to_i, "match.ipv6_flabel failed") 
    assert(of_match.ipv6_flabel_mask == match_obj.ipv6_flabel_mask.to_i, "match.ipv6_flabel_mask failed") 
    assert(of_match.icmpv6_type == match_obj.icmpv6_type.to_i, "match.icmpv6_type failed") 
    assert(of_match.icmpv6_code == match_obj.icmpv6_code.to_i, "match.icmpv6_code failed") 
    assert(of_match.icmpv6_code == match_obj.icmpv6_code.to_i, "match.icmpv6_code failed") 
    assert(of_match.ipv6_nd_target.to_s == IPAddr.new(match_obj.ipv6_nd_target).to_s,
      "match.ipv6_nd_target failed") 
    assert(of_match.ipv6_nd_sll.to_s == Trema::Mac.new(match_obj.ipv6_nd_sll).to_s,
      "match.ipv6_nd_sll failed") 
    assert(of_match.ipv6_nd_tll.to_s == Trema::Mac.new(match_obj.ipv6_nd_tll).to_s,
      "match.ipv6_nd_tll failed") 
    assert(of_match.mpls_label == match_obj.mpls_label.to_i, "match.mpls_label failed") 
    assert(of_match.mpls_tc == match_obj.mpls_tc.to_i, "match.mpls_tc failed") 
    assert(of_match.mpls_bos == match_obj.mpls_bos.to_i, "match.mpls_bos failed") 
    assert(of_match.pbb_isid == match_obj.pbb_isid.to_i, "match.pbb_isid failed") 
    assert(of_match.pbb_isid_mask == match_obj.pbb_isid_mask.to_i, "match.pbb_isid_mask failed") 
    assert(of_match.tunnel_id == match_obj.tunnel_id.to_i, "match.tunnel_id failed") 
    assert(of_match.tunnel_id_mask == match_obj.tunnel_id_mask.to_i, "match.tunnel_id_mask failed") 
    assert(of_match.ipv6_exthdr == match_obj.ipv6_exthdr.to_i, "match.ipv6_exthdr failed") 
    assert(of_match.ipv6_exthdr_mask == match_obj.ipv6_exthdr_mask.to_i, "match.ipv6_exthdr_mask failed") 
  end

  def test__edge_actions_to_trema_actions_set_field_success
    match_body = {
      'type'=> 'OFPFlowMatch',
      'in_node'=> 'in_node',
      'in_port'=> '1',
      'in_phy_port'=> '1',
      'metadata'=> '0x1234567812345679',
      'metadata_mask'=> '0x1234567812345679',
      'eth_src'=> 'aa:bb:cc:dd:ee:ff',
      'eth_src_mask'=> 'ff:ff:ff:ff:ff:ff',
      'eth_dst'=> 'ff:ee:dd:cc:bb:aa',
      'eth_dst_mask'=> 'ff:ff:ff:ff:ff:ff',
      'eth_type'=> '0x0800',
      'vlan_vid'=> '2991',
      'vlan_vid_mask'=> '0xFFF',
      'vlan_pcp'=> '1',
      'ip_dscp'=> '1',
      'ip_ecn'=> '1',
      'ip_proto'=> '6',
      'ipv4_src'=> '192.168.0.1',
      'ipv4_src_mask'=> '255.255.255.255',
      'ipv4_dst'=> '192.168.0.2',
      'ipv4_dst_mask'=> '255.255.255.255',
      'tcp_src'=> '1234',
      'tcp_dst'=> '2345',
      'udp_src'=> '1234',
      'udp_dst'=> '2345',
      'sctp_src'=> '123',
      'sctp_dst'=> '234',
      'icmpv4_type'=> '1',
      'icmpv4_code'=> '2',
      'arp_op'=> '1',
      'arp_spa'=> '192.168.0.1',
      'arp_spa_mask'=> '255.255.255.255',
      'arp_tpa'=> '192.168.0.2',
      'arp_tpa_mask'=> '255.255.255.255',
      'arp_sha'=> 'aa:bb:cc:dd:ee:ff',
      'arp_sha_mask'=> 'ff:ff:ff:ff:ff:ff',
      'arp_tha'=> 'ff:ee:dd:cc:bb:aa',
      'arp_tha_mask'=> 'ff:ff:ff:ff:ff:ff',
      'ipv6_src'=> '2001:0bd8:0000:0000:0000:0000:dead:beaf',
      'ipv6_src_mask'=> 'ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_dst'=> '3002:1ce9:1111:1111:1111:1111:efbe:cfb0',
      'ipv6_dst_mask'=> 'ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_flabel'=> '0x12345',
      'ipv6_flabel_mask'=> '0xfffff',
      'icmpv6_type'=> '1',
      'icmpv6_code'=> '2',
      'ipv6_nd_target'=> '3002:1ce9:1111:1111:1111:1111:efbe:cfb0/ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff',
      'ipv6_nd_sll'=> 'ff:ff:bb:cc:ee:ff',
      'ipv6_nd_tll'=> 'ee:ee:bb:cc:ee:ff',
      'mpls_label'=> '0x12345678',
      'mpls_tc'=> '0x12',
      'mpls_bos'=> '0x34',
      'pbb_isid'=> '0x23456789',
      'pbb_isid_mask'=> '0xffffffff',
      'tunnel_id'=> '0x1234567812345679',
      'tunnel_id_mask'=> '0x1234567812345679',
      'ipv6_exthdr'=> '0x1234',
      'ipv6_exthdr_mask'=> '0xffff'
    }
    
    match_obj = OFPFlowMatch.new(match_body)
    
    actions = []
    actions << OFPFlowActionSetField.new(match: match_obj)

    of_actions = @base_controller.send(
      :_edge_actions_to_trema_actions, "node01", actions)

    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::SetField)},
      "Actions.SetField failed") 
  end

  def test__edge_actions_to_trema_actions_other_action
    actions = []
    actions << FlowActionOutput.new(output: "port01")
    actions << OFPFlowActionCopyTtlIn.new
    actions << OFPFlowActionCopyTtlOut.new
    actions << OFPFlowActionDecIpTtl.new
    actions << OFPFlowActionDecMplsTtl.new
    actions << OFPFlowActionExperimenter.new(experimenter_id: 0x001, body: "body")
    actions << OFPFlowActionGroupAction.new(group_id: 0x001)
    actions << OFPFlowActionPopMpls.new(eth_type: 0x8847)
    actions << OFPFlowActionPopPbb.new
    actions << OFPFlowActionPopVlan.new
    actions << OFPFlowActionPushMpls.new(eth_type: 0x8847)
    actions << OFPFlowActionPushPbb.new(eth_type: 0x88e7)
    actions << OFPFlowActionPushVlan.new(eth_type: 0x8100)
    actions << OFPFlowActionSetIpTtl.new(ip_ttl: 0x001)
    actions << OFPFlowActionSetMplsTtl.new(mpls_ttl: 0x001)
    actions << OFPFlowActionSetQueue.new(queue_id: 0x001)

    @base_controller.expects(:get_of_port_no).
      with("node01", "port01").returns(0x001).once
      
    of_actions = @base_controller.send(
      :_edge_actions_to_trema_actions, "node01", actions)

    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::SendOutPort)},
      "Actions.SendOutPort failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::CopyTtlIn)},
      "Actions.CopyTtlIn failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::CopyTtlOut)},
      "Actions.CopyTtlOut failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::DecIpTtl)},
      "Actions.DecIpTtl failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::DecMplsTtl)},
      "Actions.DecMplsTtl failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::Experimenter)},
      "Actions.Experimenter failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::GroupAction)},
      "Actions.GroupAction failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PopMpls)},
      "Actions.PopMpls failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PopPbb)},
      "Actions.PopPbb failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PopVlan)},
      "Actions.PopVlan failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PushMpls)},
      "Actions.PushMpls failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PushPbb)},
      "Actions.PushPbb failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::PushVlan)},
      "Actions.PushVlan failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::SetIpTtl)},
      "Actions.SetIpTtl failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::SetMplsTtl)},
      "Actions.SetMplsTtl failed") 
    assert(of_actions.any? {|e| e.is_a?(Trema::Actions::SetQueue)},
      "Actions.SetQueue failed") 
  end

  def test__driver_ready
  end

  def test__nw_if
  end

  def test__is_port_up
  end

  def test__is_link_up
  end

  def test_initialize_queued_call
  end

  def test_submit
  end

  def test_async_call
  end
  
  def test_sync_call
  end

end