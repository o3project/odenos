
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

require 'forwardable'

module Odenos
  module Component
    module Driver
      module  OFDriver
        module FlowMap
          extend Forwardable
          def initialize_flow_mapping
            # Flow.flow_id -> [ FlowEntry ]
            @od2op_flow = {}
          end

          ###########################
          # @!group Mapping table accessors ODENOS -> OpenFlow
          ###########################

          #
          # @!method register_flow_entries(flowid, flowentries)
          # @param [Integer] flowid Flow.flow_id
          # @param [Array<FlowEntry>] flowentries Sequence of FlowEntrys
          #
          def_delegator :@od2op_flow, :store, :register_flow_entries

          #
          # @!method lookup_flow_entries(flowid)
          # @param [Integer] flowid Flow.flow_id
          # @return [Array<FlowEntry>] Corresponding sequence of FlowEntrys
          #
          def_delegator :@od2op_flow, :[], :lookup_flow_entries

          #
          # @param [Integer] dpid datapath id
          # @param [Trema::Match] match match condition
          # @return [(Flow.flow_id,FlowEntry),nil] Flow information found or nil
          #
          def lookup_flowinfo_by_match(dpid, match)
            retval = nil
            @od2op_flow.each_pair do |flowid, flowentries|
              flowentries.each do |flowentry|
                next if dpid != flowentry.dpid

                # TODO
                # match
                if match.in_port == flowentry.match.in_port
                  return [flowid, flowentry]
                end
              end
            end

            retval
          end

          #
          # @param [Integer] dpid dpid
          # @return [Array<(Flow.flow_id,Array<FlowEntry>)>] Matched flow information
          #
          def get_all_flowinfo_using_switch(dpid)
            flowinfo = []
            @od2op_flow.each_pair do |flowid, flowentries|
              if flowentries.any? { |e| e.dpid == dpid }
                flowinfo << [flowid, flowentries]
              end
            end
            flowinfo
          end

          #
          # @param [Integer] dpid dpid
          # @param [Integer] of_port_no port number (OpenFlow)
          # @return [Array<(Flow.flow_id,FlowEntry)>] Matched flow information
          #
          def get_all_flowinfo_using_port(dpid, of_port_no)
            flowinfo = []
            @od2op_flow.each_pair do |flowid, flowentries|
              if flowentries.any? do|e|
                e.dpid == dpid &&
                e.actions.any? do
                  |act| act.is_a?(Trema::Actions::SendOutPort) &&
                        act.port_number == of_port_no
                end
              end
                flowinfo << [flowid, flowentries]
              end
            end
            flowinfo
          end

          #
          # @!method unregister_flow_entries(flowid)
          # @param [Integer] flowid Flow.flow_id
          #
          def_delegator :@od2op_flow, :delete, :unregister_flow_entries
        end
      end
    end
  end
end
