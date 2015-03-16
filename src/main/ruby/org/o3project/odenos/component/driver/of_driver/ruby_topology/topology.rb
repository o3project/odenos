
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
require 'observer'
require 'odenos/component/driver/of_driver'
require 'odenos/core/util/logger'

module Odenos
  module Component
    module Driver
      module OFDriver
        module RubyTopology
          #
          # Topology Event class to pass to observer
          #
          class TopologyEvent
            attr_reader :action
            attr_reader :subject
            attr_reader :topology
            # @param [Symbol] action One of :add, :delete, :update
            # @param [Number, (Number,Trema::Port), Link] subject Switch dpid, [dpid, Port], or Link
            # @param [Topology] topology
            def initialize(action, subject, topology)
              @action = action
              @subject = subject
              @topology = topology
            end
          end

          #
          # Topology information containing the list of known switches, ports,
          # and links.
          #
          class Topology
            include Odenos::Component::Driver::OFDriver

            include Observable
            include Odenos::Util::Logger
            extend Forwardable

            def_delegator :@ports, :each_pair, :each_switch
            def_delegator :@links, :each, :each_link
            def initialize
              # dpid -> [Trema::Port]
              @ports = Hash.new { [].freeze }
              # [Link]
              @links = []
            end

            def add_switch(dpid)
              unless @ports.include?(dpid)
                @ports[dpid] = []
              end
              changed
              notify_observers TopologyEvent.new(:add, dpid, self)
            end

            def delete_switch(dpid)
              @ports[dpid].each do | each |
                delete_port(dpid, each)
              end
              @ports.delete(dpid)
              changed
              notify_observers TopologyEvent.new(:delete, dpid, self)
            end

            def update_port(dpid, port)
              existing_port = @ports[dpid].reject! { |e| e.port_no == port.port_no }
              @ports[dpid] += [port]
              if existing_port.nil?
                # port added event
                changed
                notify_observers TopologyEvent.new(:add, [dpid, port], self)
                # switch update event
                changed
                notify_observers TopologyEvent.new(:update, dpid, self)
              else
                unless is_port_up(port) && is_link_up(port)
                  delete_link_by dpid, port
                end
                # port update event
                changed
                notify_observers TopologyEvent.new(:update, [dpid, port], self)
              end
            end

            def add_port(dpid, port)
              update_port dpid, port
            end

            def delete_port(dpid, port)
              delete_link_by dpid, port
              @ports[dpid].delete_if { |e| e.port_no == port.port_no }
              # port delete event
              changed
              notify_observers TopologyEvent.new(:delete, [dpid, port], self)
            end

            def add_link_by(dpid, packet_in)
              unless packet_in.eth_type == OpenFlowController::TYPE_LLDP
                fail 'Not an LLDP packet!'
              end

              begin
                link = Link.new(dpid, packet_in)
              rescue => e
                lldp = Pio::Lldp.read(packet_in.data.pack('C*'))
                warn ">>Can not be created link dpid:#{dpid} - lldp.lpid:#{lldp.dpid}"
                return
              end

              unless @links.include?(link)
                @links << link
                @links.sort!
                changed
                # link added event
                notify_observers TopologyEvent.new(:add, link, self)
              end
            end

            ##############################################################################

            protected

            ##############################################################################

            def delete_link_by(dpid, port)
              to_delete = []
              @links.each do | each |
                if each.has?(dpid, port.port_no)
                  to_delete << each
                end
              end
              to_delete.each do |link|
                @links.delete(link)
                changed
                # link deleted event
                notify_observers TopologyEvent.new(:delete, link, self)
              end
            end

            def is_port_up(port)
              port_down = port.config & Trema::Port::OFPPC_PORT_DOWN
              if  port_down == Trema::Port::OFPPC_PORT_DOWN
                return false
              end
              true
            end

            def is_link_up(port)
              link_down = port.state & Trema::Port::OFPPC_PORT_DOWN
              if link_down == Trema::Port::OFPPC_PORT_DOWN
                return false
              end
              true
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
