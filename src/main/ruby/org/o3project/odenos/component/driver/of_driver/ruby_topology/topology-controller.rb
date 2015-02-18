
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

require 'rubygems'
require 'trema'
require 'odenos/component/driver/of_driver'

module Odenos
  module Component
    module Driver
      module OFDriver
        module RubyTopology
          #
          # This controller collects network topology information using LLDP.
          #
          module TopologyController
            include Odenos::Component::Driver::OFDriver
            def start
              periodic_timer_event :flood_lldp_frames, 5
              # You need to add observer on your derived class
              @topology = Topology.new
            end

            def switch_ready(dpid)
              debug ">> #{__method__}"
              debug "  dpid: #{dpid}"

              # update topology
              @topology.add_switch(dpid)

              # first flow (for packet_in)
              action = SendOutPort.new(
              port_number: OFPP_CONTROLLER, max_len: OFPCML_NO_BUFFER)
              ins = ApplyAction.new(actions: [action])

              send_flow_mod_add(dpid,
                                priority: OFP_LOW_PRIORITY,
                                buffer_id: OFP_NO_BUFFER,
                                flags: OFPFF_SEND_FLOW_REM,
                                instructions: [ins])

              send_port_desc_multipart_request(dpid)
            end

            def port_desc_multipart_reply(dpid, messages)
              debug ">> #{__method__} :dpid #{dpid}"

              messages.parts.first.ports.each do|port|
                debug "port_no: #{ port.port_no }"
                @topology.add_port(dpid, port)
              end
            end

            def switch_disconnected(dpid)
              debug "[switch_disconnected] dpid: #{dpid}"
              @topology.delete_switch(dpid)
            end

            def port_status(dpid, port_status)
              # TODO
              # if port_status.local?
              #  return
              # end
              case port_status.reason
              when Trema::PortStatus::OFPPR_ADD
                @topology.add_port(dpid, port_status)
              when Trema::PortStatus::OFPPR_DELETE
                @topology.delete_port(dpid, port_status)
              when Trema::PortStatus::OFPPR_MODIFY
                @topology.update_port(dpid, port_status)
              end
            end

            def packet_in(dpid, packet_in)
              debug ">> #{__method__} dpid: #{dpid}"
              unless packet_in.eth_type == OpenFlowController::TYPE_LLDP
                return
              end
              @topology.add_link_by(dpid, packet_in)
            end

            ###############################

            protected

            ###############################

            def flood_lldp_frames
              @topology.each_switch do|dpid, ports|
                send_lldp dpid, ports
              end
            end

            def send_lldp(dpid, ports)
              debug ">> #{__method__} dpid: #{dpid}"
              ports.each do|port|
                if (port.config & Port::OFPPC_PORT_DOWN) == Port::OFPPC_PORT_DOWN
                  next
                end
                port_number = port.port_no
                send_packet_out(dpid,
                                data: lldp_binary_string(dpid, port_number).unpack('C*'),
                                buffer_id: OFP_NO_BUFFER,
                                actions: SendOutPort.new(port_number))
              end
            end

            def lldp_binary_string(dpid, port_number)
              if @destination_mac
                options = { dpid: dpid,
                            port_number: port_number,
                            destination_mac: @destination_mac.value }
              else
                options = { dpid: dpid, port_number: port_number }
              end
              Pio::Lldp.new(options).to_binary
            end
          end
        end
      end
    end
  end
end

### Local variables:
### mode: Ruby
### coding: utf-8-unix
### indent-tabs-mode: nil
### End:
