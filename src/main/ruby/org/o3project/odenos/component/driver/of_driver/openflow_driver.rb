
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

require 'trema'
require 'thread'
require 'optparse'
require 'msgpack'
require 'odenos/component/driver'
require 'odenos/component/driver/of_driver'

module Odenos
  module Component
    module Driver
      module  OFDriver
        #
        # ODENOS Component portion of OpenFlowDriver
        #
        class OpenFlowDriver < Odenos::Component::Driver::Driver
          include Odenos::Core
          include Odenos::Component
          include Odenos::Util

          attr_reader :network_id
          # import exception classes
          def initialize(objectId, dispatcher, controller)
            info ">> #{__method__}"

            # set OpenFLowController
            @controller = controller
            # Network ID
            @network_id = nil
            # request parser
            @local_parser = RequestParser.new
            local_add_rules

            super(objectId, dispatcher)
            @property.description = 'OpenFlowDriver for ruby'
          end

          ##############################
          ## connection event methods
          ##############################

          #
          # @param [ComponentConnectionChanged] _message
          # @return True if added connection
          #
          def on_connection_changed_added_pre(_message)
            debug ">> #{__method__}"

            connection = _message.curr
            unless _is_valid_connection(connection)
              return false
            end
            unless network_id.nil?
              return false
            end
            if connection.state != ComponentConnection::State::INITIALIZING
              return false
            end

            true
          end

          #
          # @param [ComponentConnectionChanged] _message
          # @return True if update connection
          #
          def on_connection_changed_update_pre(_message)
            debug ">> #{__method__}"

            connection = _message.curr
            unless _is_valid_connection(connection)
              return false
            end
            true
          end

          #
          # @param [ComponentConnectionChanged] _message
          # @return True if delete connection
          #
          def on_connection_changed_delete_pre(_message)
            debug ">> #{__method__}"

            connection = _message.curr
            unless _is_valid_connection(connection)
              return false
            end
            if network_id.nil?
              return false
            end

            true
          end

          #
          # @param [ComponentConnectionChanged] _message
          # @return [void]
          #
          def on_connection_changed_added(_message)
            debug ">> #{__method__}"

            connection = _message.curr
            debug 'new active_connection.'
            # connection detected
            @network_id =  connection.network_id

            # Subscribe to NetworkComponent
            begin
              add_entry_event_subscription(OutPacketAdded::TYPE, network_id)
              add_entry_event_subscription(FlowChanged::TYPE, network_id)
              update_entry_event_subscription(FlowChanged::TYPE, network_id, {})

              apply_event_subscription
            rescue => ex
              error ex.backtrace
            end

            @controller.async_call(:register_driver_component, [self, connection])
          end

          #
          # @param [ComponentConnectionChanged] _message
          # @return [void]
          #
          def on_connection_changed_update(_message)
            debug ">> #{__method__}"
            # Do Nothing.
          end

          #
          # @param [ComponentConnectionChanged] _message
          # @return [void]
          #
          def on_connection_changed_delete(_message)
            debug ">> #{__method__}"

            connection = _message.curr
            # changed connection state
            connection.state = ComponentConnection::State::FINALIZING
            @system_manager_interface.put_connection(connection)

            # Unsubscribe to NetworkComponent
            begin
              remove_entry_event_subscription(OutPacketAdded::TYPE, network_id)
              remove_entry_event_subscription(FlowChanged::TYPE, network_id)

              apply_event_subscription
            rescue => ex
              error ex.backtrace
            end

            @controller.async_call(:unregister_driver_component, [self, connection])
          end

          ##############################
          ## topology event methods
          ##############################

          def on_flow_added(_nwc_id, flow)
            debug ">> #{__method__}"

            ofp_flow = OFPFlow.new(flow)
            unless _is_valid_flow(ofp_flow)
              @controller.async_call(:on_flow_added, [ofp_flow])
            end
          end

          def on_flow_update(_nwc_id, _prev, curr, _attributes)
            debug ">> #{__method__}"

            ofp_flow = OFPFlow.new(curr)
            unless _is_valid_flow(ofp_flow)
              @controller.async_call(:on_flow_update, [ofp_flow])
            end
          end

          def on_flow_delete(_nwc_id, flow)
            debug ">> #{__method__}"

            ofp_flow = OFPFlow.new(flow)
            unless _is_valid_flow(ofp_flow)
              @controller.async_call(:on_flow_delete, [ofp_flow])
            end
          end

          def on_out_packet_added(_nwc_id, packet)
            debug ">> #{__method__}"

            packet_id = packet.packet_id
            nw_if =  network_interfaces[_nwc_id]

            resp = nw_if.del_out_packet(packet_id)
            unless resp.status_code == Response::OK
              return
            end
            out_packet = Packet.from_object_hash(resp.body)
            unless out_packet.nil?
              @controller.async_call(:on_out_packet_added, [out_packet])
            end
          end

          def _is_valid_connection(connection)
            # check logic_id
            unless connection.logic_id == remote_object_id
              debug { 'Invalid connection.logic_id' }
              return false
            end
            # check object_type
            unless connection.odenos_type == 'LogicAndNetwork'
              debug { 'Invalid connection.type' }
              return false
            end
            true
          end

          def _is_valid_flow(flow)
            # check flow, flow.flow_id
            if flow.nil? || flow.flow_id.nil?
              warn 'invalid flow. flow_id is nil'
              return true
            end
            # check flow.type
            ftype = flow.odenos_type
            if ftype != 'OFPFlow' && ftype != 'BasicFlow'
              warn "invalid flow. flow.type: #{ftype}"
              return true
            end

            false
          end

          ##############################
          ## request methods
          ##############################

          def local_add_rules
            rules = []
            rules.push(RequestParser::PATTERN => /^flow_types$/,
                       RequestParser::METHOD => :GET,
                       RequestParser::FUNC => method(:do_get_flow_types),
                       RequestParser::PARAMS => 0)
            rules.push(RequestParser::PATTERN => /^node_maps$/,
                       RequestParser::METHOD => :GET,
                       RequestParser::FUNC => method(:do_get_node_maps),
                       RequestParser::PARAMS => 0)
            rules.push(RequestParser::PATTERN => /^port_maps$/,
                       RequestParser::METHOD => :GET,
                       RequestParser::FUNC => method(:do_get_port_maps),
                       RequestParser::PARAMS => 0)
            rules.push(RequestParser::PATTERN => /^link_maps$/,
                       RequestParser::METHOD => :GET,
                       RequestParser::FUNC => method(:do_get_link_maps),
                       RequestParser::PARAMS => 0)
            rules.push(RequestParser::PATTERN => /^flow_maps$/,
                       RequestParser::METHOD => :GET,
                       RequestParser::FUNC => method(:do_get_flow_maps),
                       RequestParser::PARAMS => 0)

            @local_parser.add_rule(rules)
          end

          # @param [Request] request
          # @return [Response]
          def on_request(request)
            debug { "on_request( #{request.to_a.inspect} )" }
            @local_parser.action(request)
          end

          def do_get_flow_types
            Response.new(Response::OK, %w(OFPFlow BasicFlow))
          end

          def do_get_node_maps
            maps = @controller.sync_call(:on_get_node_maps)
            Response.new(Response::OK, maps)
          end

          def do_get_port_maps
            maps = @controller.sync_call(:on_get_port_maps)
            Response.new(Response::OK, maps)
          end

          def do_get_link_maps
            maps = @controller.sync_call(:on_get_link_maps)
            Response.new(Response::OK, maps)
          end

          def do_get_flow_maps
            maps = @controller.sync_call(:on_get_flow_maps)
            Response.new(Response::OK, maps)
          end
        end
      end
    end
  end
end
