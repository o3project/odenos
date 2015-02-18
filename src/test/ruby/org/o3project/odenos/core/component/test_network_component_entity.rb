
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

require 'odenos/core/util/object_hash'
require 'odenos/remoteobject/event'
require 'odenos/core/component/network_component_entity'

class TestTopology < MiniTest::Test
  
  def setup
    @target = Odenos::Component::Topology.new(
      {:type => "Topology", :version => "0001"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize_args_first_is_hash
    msg = {:type => "Topology", :version => "0001",
      :nodes => {}, :links => {}}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_first_not_is_hash
    @target = Odenos::Component::Topology.new("String")
    
    assert_equal(nil, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_0
    msg = {:type => "Topology",
      :nodes => {}, :links => {}}

    @target = Odenos::Component::Topology.new()
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
end

class TestNode < MiniTest::Test
  
  def setup
    @target = Odenos::Component::Node.new(
      {:type => "node", :version => "0001"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize_args_first_is_hash
    msg = {:type => "node", :version => "0001", :attributes => {},
      :node_id => nil, :ports => {}}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_first_not_is_hash
    @target = Odenos::Component::Node.new("String")
    
    assert_equal(nil, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_0
    msg = {:type => "Node", :attributes => {},
      :node_id => nil, :ports => {}}

    @target = Odenos::Component::Node.new()
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
end

class TestPort < MiniTest::Test
  
  def setup
    @target = Odenos::Component::Port.new(
      {:type => "Port", :version => "0001", :attributes => {},
        :node_id => "NodeId", :port_id => nil})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize_args_first_is_hash
    msg = {:type => "Port", :version => "0001", :attributes => {},
      :node_id => "NodeId", :port_id => nil, :out_link => nil,
      :in_link => nil}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_first_not_is_hash
    begin
      Odenos::Component::Port.new("String")
    rescue => e
      assert_equal("Only Hash supported.", e.to_s)
    end
  end
  
  def test_initialize_args_length_2
    msg = {:type => "Port", :attributes => {},
      :node_id => "NodeId", :port_id => "PortId",
      :out_link => nil, :in_link => nil}

    @target = Odenos::Component::Port.new("NodeId", "PortId")
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_0
    begin
      Odenos::Component::Port.new()
    rescue => e
      assert_equal("Expect 1 or 2 argument.", e.to_s)
    end
  end
  
  def test_initialize_node_id_missing
    msg = {:type => "Port", :version => "0001", :attributes => {},
      :node_id => nil, :port_id => nil, :out_link => nil,
      :in_link => nil}
    begin
      Odenos::Component::Port.new(msg)
    rescue => e
      assert_equal(":node_id missing", e.to_s)
    end
  end
end

class TestLink < MiniTest::Test
  
  def setup
    @target = Odenos::Component::Link.new(
      {:type => "Link", :version => "0001", :attributes => {},
        :src_node => "SrcNode", :src_port => "SrcPort",
        :dst_node => "DstNonde", :dst_port => "DstPort"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize_args_first_is_hash
    msg = {:type => "Link", :version => "0001", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => nil}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_first_is_not_hash
    begin
      Odenos::Component::Link.new("String")
    rescue => e
      assert_equal("Only Hash supported.", e.to_s)
    end
  end
  
  def test_initialize_args_length_4
    msg = {:type => "Link", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => nil}
    
    @target = Odenos::Component::Link.new(
      "SrcNode", "SrcPort", "DstNonde", "DstPort")
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_5
    msg = {:type => "Link", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId"}
    
    @target = Odenos::Component::Link.new(
      "LinkId", "SrcNode", "SrcPort", "DstNonde", "DstPort")
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_0
    begin
      Odenos::Component::Link.new()
    rescue => e
      assert_equal("Expect 1 or 5 argument.", e.to_s)
    end
  end
  
  def test_initialize_src_node_missing
    msg = {:type => "Link", :version => "0001", :attributes => {},
      :src_node => nil, :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => nil}
        
    begin
      Odenos::Component::Link.new(msg)
    rescue => e
      assert_equal(":src_node missing", e.to_s)
    end
  end
  
  def test_initialize_src_port_missing
  msg = {:type => "Link", :version => "0001", :attributes => {},
    :src_node => "SrcNode", :src_port => nil,
    :dst_node => "DstNonde", :dst_port => "DstPort",
    :link_id => nil}
      
    begin
      Odenos::Component::Link.new(msg)
    rescue => e
      assert_equal(":src_port missing", e.to_s)
    end
  end
  
  def test_initialize_dst_node_missing
  msg = {:type => "Link", :version => "0001", :attributes => {},
    :src_node => "SrcNode", :src_port => "SrcPort",
    :dst_node => nil, :dst_port => "DstPort",
    :link_id => nil}
      
    begin
      Odenos::Component::Link.new(msg)
    rescue => e
      assert_equal(":dst_node missing", e.to_s)
    end
  end
  
  def test_initialize_dst_port_missing
    msg = {:type => "Link", :version => "0001", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => nil,
      :link_id => nil}
        
    begin
      Odenos::Component::Link.new(msg)
    rescue => e
      assert_equal(":dst_port missing", e.to_s)
    end
  end
end

class TestFlowSet < MiniTest::Test
  
  def setup
    @target = Odenos::Component::FlowSet.new(
      {:type => "FlowSet", :version => "0001"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "FlowSet", :version => "0001",
      :priority => {}, :flows => {}}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_first_not_is_hash
    @target = Odenos::Component::FlowSet.new("String")
    
    assert_equal(nil, @target.instance_variable_get(:@object))
  end
  
  def test_initialize_args_length_0
    msg = {:type => "FlowSet",
      :priority => {}, :flows => {}}

    @target = Odenos::Component::FlowSet.new()
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
end

class TestFlow < MiniTest::Test
  
  def setup
    @target = Odenos::Component::Flow.new(
      {:type => "Flow", :version => "0001"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "Flow", :version => "0001", :attributes => {}}
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestBasicFlow < MiniTest::Test
  
  def setup
    @target = Odenos::Component::BasicFlow.new({
      :type => "BasicFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId"})
  end 

  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "BasicFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId", :matches => [],
      :path => [], :edge_actions => {}}
         
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
    
  def test_normalize_matches
    msg = {:type => "BasicFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId", :matches => ["match",{
        :type => "BasicFlowMatch", :version => "0001", :attributes => {}}],
      :path => [], :edge_actions => {}}
    @target.instance_variable_set(:@object, msg )
    
    result = @target.normalize_matches
    
    assert_equal("match", result[0])
    assert_equal(Odenos::Component::BasicFlowMatch, result[1].class)
  end
  
  def test_normalize_actions
    msg = {:type => "BasicFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId", :matches => [],
      :path => [], :edge_actions => {:action => ["Action",{
        :type => "BasicFlowAction", :version => "0001", :attributes => {}}]}}
    @target.instance_variable_set(:@object, msg )
    
    result = @target.normalize_actions
    
    assert_equal("Action", result[:action][0])
    assert_equal(Odenos::Component::BasicFlowAction, result[:action][1].class)
  end
end

class TestOFPFlow < MiniTest::Test
  
  def setup
    @target = Odenos::Component::OFPFlow.new({
      :type => "OFPFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlow", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId", :matches => [],
      :path => [], :edge_actions => {},
      :idle_timeout => 0, :hard_timeout => 0,
      :priority => 0xFFFF}
         
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestBasicFlowAction < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::BasicFlowAction.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "BasicFlowAction"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestFlowActionOutput < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::FlowActionOutput.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "FlowActionOutput"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowAction < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowAction.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowAction"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionCopyTtlIn < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionCopyTtlIn.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionCopyTtlIn"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionCopyTtlOut < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionCopyTtlOut.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionCopyTtlOut"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionDecIpTtl < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionDecIpTtl.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionDecIpTtl"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionDecMplsTtl < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionDecMplsTtl.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionDecMplsTtl"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionExperimenter < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionExperimenter.new()
    @target2 = Odenos::Component::OFPFlowActionExperimenter.new(
      experimenter_id: 1234)
    @target3 = Odenos::Component::OFPFlowActionExperimenter.new(
      experimenter_id: 1234, body: "body")
  end 
  
  def teardown
    @target = nil
    @target2 = nil
    @target3 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionExperimenter"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionExperimenter",
    :experimenter_id => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))

    msg3 = {:type => "OFPFlowActionExperimenter",
    :experimenter_id => 1234, :body => "body"}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionGroupAction < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionGroupAction.new()
    @target2 = Odenos::Component::OFPFlowActionGroupAction.new(
      :group_id => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionGroupAction"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionGroupAction",
    :group_id => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionPopMpls < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPopMpls.new()
    @target2 = Odenos::Component::OFPFlowActionPopMpls.new(
      :eth_type => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPopMpls"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionPopMpls",
    :eth_type => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionPopPbb < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPopPbb.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPopPbb"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end


class TestOFPFlowActionPopVlan < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPopVlan.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPopVlan"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionPushMpls < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPushMpls.new()
    @target2 = Odenos::Component::OFPFlowActionPushMpls.new(
      :eth_type => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPushMpls"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionPushMpls",
    :eth_type => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionPushPbb < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPushPbb.new()
    @target2 = Odenos::Component::OFPFlowActionPushPbb.new(
      :eth_type => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPushPbb"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionPushPbb",
    :eth_type => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionPushVlan < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionPushVlan.new()
    @target2 = Odenos::Component::OFPFlowActionPushVlan.new(
      :eth_type => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionPushVlan"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionPushVlan",
    :eth_type => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionSetIpTtl < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionSetIpTtl.new()
    @target2 = Odenos::Component::OFPFlowActionSetIpTtl.new(
      :ip_ttl => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionSetIpTtl"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionSetIpTtl",
    :ip_ttl => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionSetMplsTtl < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionSetMplsTtl.new()
    @target2 = Odenos::Component::OFPFlowActionSetMplsTtl.new(
      :mpls_ttl => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionSetMplsTtl"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionSetMplsTtl",
    :mpls_ttl => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestOFPFlowActionSetField < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionSetField.new()
    @target2 = Odenos::Component::OFPFlowActionSetField.new(
    :match => {:type => "OFPFlowMatch",
      :in_node => "in_node01", :in_port => "in_port01"})
    @target3 = Odenos::Component::OFPFlowActionSetField.new(
    :match => OFPFlowMatch.new(:in_node => "in_node02", :in_port => "in_port02"))
  end 
  
  def teardown
    @target = nil
    @target2 = nil
    @target3 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionSetField"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionSetField",
    :match => OFPFlowMatch.new(:in_node => "in_node01", :in_port => "in_port01")}
    assert_equal(msg2[:type], @target2.odenos_type)
    assert_equal(msg2[:match].in_node, @target2.match.in_node)
    assert_equal(msg2[:match].in_port, @target2.match.in_port)

    msg3 = {:type => "OFPFlowActionSetField",
    :match => OFPFlowMatch.new(:in_node => "in_node02", :in_port => "in_port02")}
    assert_equal(msg3[:type], @target3.odenos_type)
    assert_equal(msg3[:match].in_node, @target3.match.in_node)
    assert_equal(msg3[:match].in_port, @target3.match.in_port)
    assert_equal(nil, @target3.match.in_phy_port)
    assert_equal(nil, @target3.match.eth_type)
    assert_equal(nil, @target3.match.ip_proto)
  end
end

class TestOFPFlowActionSetQueue < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowActionSetQueue.new()
    @target2 = Odenos::Component::OFPFlowActionSetQueue.new(
      :queue_id => 1234)
  end 
  
  def teardown
    @target = nil
    @target2 = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowActionSetQueue"}
    assert_equal(msg, @target.instance_variable_get(:@object))

    msg2 = {:type => "OFPFlowActionSetQueue",
    :queue_id => 1234}
    assert_equal(msg2, @target2.instance_variable_get(:@object))
  end
end

class TestBasicFlowMatch < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::BasicFlowMatch.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "BasicFlowMatch"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPFlowMatch < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPFlowMatch.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPFlowMatch"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestPacket < MiniTest::Test
  include Odenos::Component
  
  def setup
  @target = Odenos::Component::Packet.new({:type => "Packet", :attributes=>{}, :id => "Packet"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "Packet", :attributes=>{}, :id => "Packet"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_packet_id_get
    assert_equal("Packet", @target.packet_id)
  end
  
  def test_packet_id_set
    msg = {:type => "Packet", :attributes=>{}, :id => "new_packet"}
    
    @target.packet_id="new_packet"

    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestInPacket < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::InPacket.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "InPacket", :attributes=>{}}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPInPacket < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPInPacket.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "OFPInPacket", :attributes=>{}}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOutPacket < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OutPacket.new(
      {"ports_except" => [], "ports-except" => ["ports-except"], 
        :type => "OutPacket", :attributes => {}, :ports => ["ports"]})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:ports_except => ["ports-except"], :"ports-except" => ["ports-except"], 
      :type => "OutPacket", :attributes => {}, :ports => ["ports"]}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_ports_specified_true
    msg = {:ports_except => ["ports-except"], 
      :type => "OutPacket", :attributes => {}, :ports => ["True"]}
    @target.instance_variable_set(:@object, msg)
    
    assert_equal(true, @target.ports_specified?)
  end
  
  def test_ports_specified_ports_nil
    msg = {:ports_except => ["ports-except"],
      :type => "OutPacket", :attributes => {}, :ports => nil}
    @target.instance_variable_set(:@object, msg)
    
    assert_equal(false, @target.ports_specified?)
  end
  
  def test_ports_specified_ports_empty
    msg = {:ports_except => ["ports-except"],
      :type => "OutPacket", :attributes => {}, :ports => []}
    @target.instance_variable_set(:@object, msg)
    
    assert_equal(false, @target.ports_specified?)
  end

  def test_ports
    msg = {:ports_except => [], :"ports-except" => ["ports-except"], 
      :type => "OutPacket", :attributes => {}, :ports => ["new_ports"]}
        
    @target.ports = ["new_ports"]
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_ports_except
    msg = {:ports_except => ["new_ports_except"],
      :"ports-except" => ["ports-except"], 
      :type => "OutPacket", :attributes => {}, :ports => []}
        
    @target.ports_except = ["new_ports_except"]
        
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOFPOutPacket < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OFPOutPacket.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:ports_except => [], :type => "OFPOutPacket",
      :attributes => {}, :ports => []}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestPacketStatus < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::PacketStatus.new()
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "PacketStatus",
      :in_packets => [], :out_packets => []}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestEntityChangeEventBase < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::EntityChangeEventBase.new({:action => "add"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "EntityChangeEventBase", :action => :add}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestNodeChanged < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::NodeChanged.new(
      {:action => "add", :id => "id",
        :curr => {:version => "curr"},
        :prev => {:version => "prev"}})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "NodeChanged", :action => :add, :id => "id"}
    curr = {:version=>"curr", :type=>"Node",
      :attributes=>{}, :node_id=>nil, :ports=>{}}
    prev = {:version=>"prev", :type=>"Node",
      :attributes=>{}, :node_id=>nil, :ports=>{}}
        
    object = @target.instance_variable_get(:@object)
    assert_equal("NodeChanged", object[:type])
    assert_equal(:add, object[:action])
    assert_equal("id", object[:id])
    assert_equal(curr, object[:curr].instance_variable_get(:@object))
    assert_equal(prev, object[:prev].instance_variable_get(:@object))
  end
  
  def test_initialize_curry_prev_nil
    msg = {:type => "NodeChanged", :action => :add, :id => "id",
      :curr => nil, :prev => nil}
      
    @target = Odenos::Component::NodeChanged.new(
      {:action => "add", :id => "id", :curr => nil, :prev => nil})
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_node_id_set
    @target.node_id = "node_id"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("node_id", object[:id])
  end
  
  def test_node_id_get
    assert_equal("id", @target.node_id)
  end
end

class TestPortChanged < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::PortChanged.new(
      {:action => "add", :id => "id",
        :curr => {:version => "curr",
          :node_id => "NodeId", :port_id => nil},
        :prev => {:version => "prev",
          :node_id => "NodeId", :port_id => nil}})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "PortChanged", :action => :add, :id => "id"}
    curr = {:version=>"curr", :type => "Port", :attributes => {},
      :node_id => "NodeId", :port_id => nil, :out_link => nil,
      :in_link => nil}
    prev = {:version=>"prev", :type => "Port", :attributes => {},
      :node_id => "NodeId", :port_id => nil, :out_link => nil,
      :in_link => nil}
        
    object = @target.instance_variable_get(:@object)
    assert_equal("PortChanged", object[:type])
    assert_equal(:add, object[:action])
    assert_equal("id", object[:id])
    assert_equal(curr, object[:curr].instance_variable_get(:@object))
    assert_equal(prev, object[:prev].instance_variable_get(:@object))
  end
  
  def test_initialize_curry_prev_nil
    msg = {:type => "PortChanged", :action => :add, :id => "id",
      :curr => nil, :prev => nil}
      
    @target = Odenos::Component::PortChanged.new(
      {:action => "add", :id => "id", :curr => nil, :prev => nil})
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_port_id_set
    @target.port_id = "port"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("port", object[:id])
  end
  
  def test_port_id_get
    assert_equal("id", @target.port_id)
  end
end

class TestLinkChanged < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::LinkChanged.new(
      {:action => "add", :id => "id",
        :curr => {:version => "curr",:attributes => {},
          :src_node => "SrcNode", :src_port => "SrcPort",
          :dst_node => "DstNonde", :dst_port => "DstPort",
          :link_id => "LinkId"},
        :prev => {:version => "prev",:attributes => {},
          :src_node => "SrcNode", :src_port => "SrcPort",
          :dst_node => "DstNonde", :dst_port => "DstPort",
          :link_id => "LinkId"}})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "LinkChanged", :action => :add, :id => "id"}
    curr = {:version=>"curr", :type => "Link", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId"}
    prev = {:version=>"prev", :type => "Link", :attributes => {},
      :src_node => "SrcNode", :src_port => "SrcPort",
      :dst_node => "DstNonde", :dst_port => "DstPort",
      :link_id => "LinkId"}
        
    object = @target.instance_variable_get(:@object)
    assert_equal("LinkChanged", object[:type])
    assert_equal(:add, object[:action])
    assert_equal("id", object[:id])
    assert_equal(curr, object[:curr].instance_variable_get(:@object))
    assert_equal(prev, object[:prev].instance_variable_get(:@object))
  end
  
  def test_initialize_curry_prev_nil
    msg = {:type => "LinkChanged", :action => :add, :id => "id",
      :curr => nil, :prev => nil}
      
    @target = Odenos::Component::LinkChanged.new(
      {:action => "add", :id => "id", :curr => nil, :prev => nil})
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_port_id_set
    @target.link_id = "link"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("link", object[:id])
  end
  
  def test_port_id_get
    assert_equal("id", @target.link_id)
  end
end

class TestFlowChanged < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::FlowChanged.new(
    {:action => "add", :id => "id"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:type => "FlowChanged", :action => :add, :id => "id"}
        
    object = @target.instance_variable_get(:@object)
    assert_equal(msg, object)
  end
  
  def test_port_id_set
    @target.flow_id = "flow"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("flow", object[:id])
  end
  
  def test_port_id_get
    assert_equal("id", @target.flow_id)
  end
end

class TestTopologyChanged < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::TopologyChanged.new(
      {:action => "add", :id => "id",
        :curr => {:version => "curr",
          :nodes => {}, :links => {}},
        :prev => {:version => "prev",
        :nodes => {}, :links => {}}})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "TopologyChanged", :action => :add, :id => "id"}
    curr = {:version=>"curr", :type => "Topology",
      :nodes => {}, :links => {}}
    prev = {:version=>"prev", :type => "Topology",
      :nodes => {}, :links => {}}
        
    object = @target.instance_variable_get(:@object)
    assert_equal("TopologyChanged", object[:type])
    assert_equal(:add, object[:action])
    assert_equal("id", object[:id])
    assert_equal(curr, object[:curr].instance_variable_get(:@object))
    assert_equal(prev, object[:prev].instance_variable_get(:@object))
  end
  
  def test_initialize_curry_prev_nil
    msg = {:type => "TopologyChanged", :action => :add, :id => "id",
      :curr => nil, :prev => nil}
      
    @target = Odenos::Component::TopologyChanged.new(
      {:action => "add", :id => "id", :curr => nil, :prev => nil})
    
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
 end

class TestInPacketAdded < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::InPacketAdded.new(
    {:type => "InPacketAdded", :id => "id"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "InPacketAdded", :id => "id"}
        
    object = @target.instance_variable_get(:@object)
    assert_equal(msg, object)
  end
  
  def test_port_id_set
    @target.packet_id = "packet"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("packet", object[:id])
  end
  
  def test_port_id_get
    assert_equal("id", @target.packet_id)
  end
end

class TestOutPacketAdded < MiniTest::Test
  include Odenos::Component
  
  def setup
    @target = Odenos::Component::OutPacketAdded.new(
    {:type => "OutPacketAdded", :id => "id"})
  end 
  
  def teardown
    @target = nil
  end
  
  def test_initialize_curry_prev_exist
    msg = {:type => "OutPacketAdded", :id => "id"}
        
    object = @target.instance_variable_get(:@object)
    assert_equal(msg, object)
  end
  
  def test_port_id_set
    @target.packet_id = "packet"
    
    object = @target.instance_variable_get(:@object)
    assert_equal("packet", object[:id])
  end
  
  def test_port_id_get
    assert_equal("id", @target.packet_id)
  end
end
