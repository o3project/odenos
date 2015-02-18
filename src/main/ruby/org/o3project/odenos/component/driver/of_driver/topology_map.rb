
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
      module OFDriver
        module TopologyMap
          extend Forwardable
          def initialize_topology_mapping
            # dpid -> Node
            @op2od_node = {}
            # [dpid, port_no] -> Port
            @op2od_port = {}
            # [link 4-tuple] -> Link
            @op2od_link = {}

            # Node.node_id -> dpid
            @od2op_switch = {}
            # [Node.node_id, Port.port_id] -> [dpid, port_no]
            @od2op_port = {}
            # Link.link_id -> [link 4-tuple]
            @od2op_link = {}
          end

          class MappingNotFound < RuntimeError
          end

          # @return [Node]
          def lookup_node_by_nodeid(nodeid)
            @op2od_node[@od2op_switch[nodeid]]
          end

          # @return [Port]
          def lookup_port_by_odenos_id(nodeid, portid)
            @op2od_port[@od2op_port[[nodeid, portid]]]
          end

          # @return [Link]
          def lookup_link_by_linkid(linkid)
            @op2od_link[@od2op_link[linkid]]
          end

          ###########################
          # @!group Mapping table accessors ODENOS -> OpenFlow
          ###########################

          #
          # Get DPID from Node.node_id
          #
          def get_dpid(nodeid)
            dpid = @od2op_switch[nodeid]
            if dpid.nil?
              fail MappingNotFound,
                   "dpid for node #{nodeid} not found"
            end
            dpid
          end

          #
          # Get port_no from Node.node_id, Port.port_id
          #
          # @return [Integer] Corresponding OpenFlow port_no
          # @return [nil] wildcard port if Port.port_id == nil or ""
          #
          def get_of_port_no(nodeid, portid)
            if portid.nil?
              return nil
            end
            if portid == ''
              return nil
            end
            port_pair = @od2op_port[[nodeid, portid]]
            if port_pair.nil?
              fail MappingNotFound,
                   "OpenFlow port for port (#{nodeid},#{portid}) not found"
            end
            port_pair.last
          end

          #
          # Lookup link 4-tuple ([src_dpid, src_port_no, dst_dpid, dst_port_no]) from Link.link_id
          #
          def lookup_of_link_tuple(linkid)
            @od2op_link[linkid]
          end

          ###########################
          # @!group Mapping table accessors OpenFlow -> ODENOS
          ###########################

          #
          # @!method lookup_node(dpid)
          # @param [Integer] dpid datapath ID
          # @return [Node] Corresponding Node
          #
          def_delegator :@op2od_node, :[], :lookup_node

          #
          # Register Node mapping
          # @param [Integer] dpid datapath ID
          # @param [Node] node Corresponding Node
          #
          def register_node(dpid, node)
            @op2od_node[dpid] = node
            @od2op_switch[node.node_id] = dpid
          end

          #
          # Unregister Node mapping
          # @param [Integer] dpid datapath ID
          #
          # TODO Should unregister_node also unregister Ports?
          def unregister_node(dpid)
            node = @op2od_node.delete(dpid)
            unless node.nil?
              @od2op_switch.delete(node.node_id)
            end
          end

          #
          # @param [Integer] dpid datapath ID
          # @param [Integer] port_no port number
          # @return [Port] Corresponding Port
          #
          def lookup_port(dpid, port_no)
            @op2od_port[[dpid, port_no]]
          end

          #
          # register Port mapping
          # @param [Integer] dpid datapath ID
          # @param [Integer] port_no port number
          # @param [Port] port Corresponding Port
          #
          def register_port(dpid, port_no, port)
            # TODO Should we check if parent node/switch exists here?
            @op2od_port[[dpid, port_no]] = port
            @od2op_port[[port.node_id, port.port_id]] = [dpid, port_no]
          end

          #
          # unregister Port mapping
          # @param [Integer] dpid datapath ID
          # @param [Integer] port_no port number
          #
          def unregister_port(dpid, port_no)
            port = @op2od_port.delete([dpid, port_no])
            unless port.nil?
              @od2op_port.delete([port.node_id, port.port_id])
            end
          end

          #
          # @!method lookup_link(link_4tuple)
          # @param [(Integer,Integer,Integer,Integer)] link_4tuple link 4-tuple (src_dpid, src_port_no, dst_dpid, dst_port_no)
          # @return [Link] Corresponding Link
          #
          def_delegator :@op2od_link, :[], :lookup_link

          #
          # register Link mapping
          # @param [(Integer,Integer,Integer,Integer)] link_4tuple link 4-tuple (src_dpid, src_port_no, dst_dpid, dst_port_no)
          # @param [Link] link Corresponding Link
          #
          def register_link(link_4tuple, link)
            @op2od_link[link_4tuple] = link
            @od2op_link[link.link_id] = link_4tuple
          end

          #
          # unregister Link  mapping
          # @param [(Integer,Integer,Integer,Integer)] link_4tuple link 4-tuple (src_dpid, src_port_no, dst_dpid, dst_port_no)
          #
          def unregister_link(link_4tuple)
            link = @op2od_link.delete(link_4tuple)
            unless link.nil?
              @od2op_link.delete(link.link_id)
            end
          end
        end
      end
    end
  end
end
