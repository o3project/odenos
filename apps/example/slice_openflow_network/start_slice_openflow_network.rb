
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

$LOAD_PATH.unshift File.expand_path(File.join File.dirname(__FILE__), '../../../src/main/ruby/org/o3project')

require 'rubygems'
require 'odenos'
require 'tracer'
require 'pp'

Tracer.off

# MessageDispatcher Info
REDIS_IP = '127.0.0.1'
REDIS_PORT = '6379'
# Object Id
SYSTEMMANAGER_ID = 'systemmanager'
TREMA_COMP_MGR_ID = 'of_comp_mgr'
SLIVER_NW_01_ID = 'sliver-nw01'
SLIVER_NW_02_ID = 'sliver-nw02'

ORIGINAL_NW_ID = 'original-nw'
SLICER_ID = 'slicer'
LSW_01_ID = 'learning-sw01'
LSW_02_ID = 'learning-sw02'
DRIVER_ID = 'openflow-driver'
CONNECT_ID01 = "#{DRIVER_ID}_#{ORIGINAL_NW_ID}"
CONNECT_ID02 = "#{SLICER_ID}_#{ORIGINAL_NW_ID}"
CONNECT_ID03 = "#{SLICER_ID}_#{SLIVER_NW_01_ID}"
CONNECT_ID04 = "#{SLICER_ID}_#{SLIVER_NW_02_ID}"
CONNECT_ID05 = "#{LSW_01_ID}_#{SLIVER_NW_01_ID}"
CONNECT_ID06 = "#{LSW_02_ID}_#{SLIVER_NW_02_ID}"
# Object Type
NETWORK = 'Network'
LEARNING_SWITCH = 'LearningSwitch'
SLICER = 'Slicer'
OPEN_FLOW_DRIVER = 'OpenFlowDriver'
LOGIC_AND_NETWORK = 'LogicAndNetwork'

# sample_case
def sample_case(dispatcher)
  include Odenos::Core
  include Odenos::Util::Logger
  include Odenos::Component

  ##############################
  # Setting SystemManagerInterface
  ##############################
  sys_if = SystemManagerInterface.new(dispatcher)

  cnt = 1
  puts ''
  puts '///////////////////////////////////////'
  puts "// (#{cnt}) Create #{ORIGINAL_NW_ID}"
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: NETWORK,
    id: ORIGINAL_NW_ID 
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"

  cnt += 1
  puts ''
  puts '///////////////////////////////////////'
  puts "// (#{cnt}) Create #{SLIVER_NW_01_ID}"
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: NETWORK,
    id: SLIVER_NW_01_ID 
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"
 
  cnt += 1
  puts ''
  puts '///////////////////////////////////////'
  puts "// (#{cnt}) Create #{SLIVER_NW_02_ID}"
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: NETWORK,
    id: SLIVER_NW_02_ID 
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n" 

  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Create #{DRIVER_ID}" 
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: OPEN_FLOW_DRIVER,
    id: DRIVER_ID
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"
    
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Create #{SLICER_ID}" 
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: SLICER,
    id: SLICER_ID
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"

  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Create #{LSW_01_ID}" 
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: LEARNING_SWITCH,
    id: LSW_01_ID
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"
  
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Create #{LSW_02_ID}" 
  puts '////////////////////////////////////////'
  prop = ObjectProperty.new(
    type: LEARNING_SWITCH,
    id: LSW_02_ID
  )
  response = sys_if.put_components(prop)
  puts "Response: #{response.to_a.inspect}\n"

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{DRIVER_ID} <--> #{ORIGINAL_NW_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID01,
    type: LOGIC_AND_NETWORK,
    connection_type: 'original',
    logic_id: DRIVER_ID,
    network_id: ORIGINAL_NW_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{SLICER_ID} <--> #{ORIGINAL_NW_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID02,
    type: LOGIC_AND_NETWORK,
    connection_type: 'original',
    logic_id: SLICER_ID,
    network_id: ORIGINAL_NW_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"
  
  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{SLICER_ID} <--> #{SLIVER_NW_01_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID03,
    type: LOGIC_AND_NETWORK,
    connection_type: 'sliver',
    logic_id: SLICER_ID,
    network_id: SLIVER_NW_01_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{SLICER_ID} <--> #{SLIVER_NW_02_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID04,
    type: LOGIC_AND_NETWORK,
    connection_type: 'sliver',
    logic_id: SLICER_ID,
    network_id: SLIVER_NW_02_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{LSW_01_ID} <--> #{SLIVER_NW_01_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID05,
    type: LOGIC_AND_NETWORK,
    connection_type: 'sliver',
    logic_id: LSW_01_ID,
    network_id: SLIVER_NW_01_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) Connect  #{LSW_02_ID} <--> #{SLIVER_NW_02_ID}"
  puts '////////////////////////////////////////'
  connectionProperty = LogicAndNetwork.new(
    id: CONNECT_ID06,
    type: LOGIC_AND_NETWORK,
    connection_type: 'sliver',
    logic_id: LSW_02_ID,
    network_id: SLIVER_NW_02_ID
  )
  response = sys_if.put_connection(connectionProperty)
  puts "Response: #{response.to_a.inspect}\n"
  
  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) GET components"
  puts '////////////////////////////////////////'
  comps = sys_if.get_components
  if comps.nil?
    fail 'not exist conponnets.'
  else
    # dump object
    pp comps
    # check
    comps.each do |id, _prop|
      comp = sys_if.get_component(id)
      if comp.nil?
        fail "not exist comp_mgr. [id: #{id}]"
      end
    end
  end

  sleep(1)
  cnt += 1
  puts ''
  puts '////////////////////////////////////////'
  puts "// (#{cnt}) GET connections"
  puts '////////////////////////////////////////'
  puts ' * GET connections.'
  conns = sys_if.get_connections
  if conns.nil?
    fail 'not exist connection.'
  else
    # dump object
    pp conns
    # check
    conns.each do |id, _conn_prop|
      get_prop = sys_if.get_connection(id)
      if get_prop.nil?
        fail "not exist connection. [id : #{id}]"
      end
    end
  end
  
end

# main
def main
  include Odenos::Core

  # Setting MessageDispatcher
  dispatcher = MessageDispatcher.new(SYSTEMMANAGER_ID)
  dispatcher.set_remote_system_manager
  dispatcher.start

  begin
    # Start sample_case
    sample_case(dispatcher)
  rescue => ex
    error " #{ex.message}, #{ex.backtrace}"
    puts " #{ex.message}, #{ex.backtrace}"
  ensure
    # Finalizing MessageDispatcher
    dispatcher.close
    puts ''
    puts 'finished.'
    puts ''
  end
end

if __FILE__ == $PROGRAM_NAME
  main
end
