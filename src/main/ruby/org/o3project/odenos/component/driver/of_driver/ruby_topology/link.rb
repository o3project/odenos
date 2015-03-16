
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
require 'pio/lldp'

module Odenos
  module Component
    module Driver
      module OFDriver
        module RubyTopology
          class Link
            attr_reader :dpid1
            attr_reader :dpid2
            attr_reader :port1
            attr_reader :port2

            alias_method :src_dpid, :dpid1
            alias_method :dst_dpid, :dpid2
            alias_method :src_port_no, :port1
            alias_method :dst_port_no, :port2
            def initialize(dpid, packet_in)
              lldp = Pio::Lldp.read(packet_in.data.pack('C*'))
              @dpid1 = lldp.dpid
              @dpid2 = dpid
              @port1 = lldp.port_id.to_i
              @port2 = packet_in.in_port
            end

            def ==(other)
              (@dpid1 == other.dpid1) &&
                (@dpid2 == other.dpid2) &&
                (@port1 == other.port1) &&
                (@port2 == other.port2)
            end

            def <=>(other)
              to_s <=> other.to_s
            end

            def to_s
              format '%#x (port %d) --> %#x (port %d)', dpid1, port1, dpid2, port2
            end

            def has?(dpid, port)
              ((@dpid1 == dpid) && (@port1 == port)) ||
                ((@dpid2 == dpid) && (@port2 == port))
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
