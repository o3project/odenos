
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
require 'ipaddr'
require 'json'
require 'odenos/core/component/network_component_entity'
require 'odenos/component/driver/of_driver/topology_map'
require 'odenos/component/driver/of_driver/flow_map'
require 'odenos/component/driver/of_driver/request_errors'
require 'odenos/component/driver/of_driver/request_helpers'
require 'odenos/component/driver/of_driver/ruby_topology'
require 'odenos/remoteobject/manager/of_component_manager'

module Odenos
  module Component
    module  Driver
      module  OFDriver
        #
        # OpenFlow Controller portion of OpenFlowDriver
        #
        class OpenFlowController < Trema::Controller
          include RubyTopology::TopologyController
          include TopologyMap
          include FlowMap
          include RequestErrors
          include RequestHelpers
          include Odenos::Core
          include Odenos::Component
          include Odenos::Manager

          # OpenFlowDriver Component
          attr_accessor :component

          OFD_TXID = 0x0FD0000
          ATTR_OFD_VENDORID = 'OpenFlow'
          TYPE_LLDP = 0x88cc
          IPPROTO_TCP = 6
          IPPROTO_UDP = 17

          PORT_ATTR_KEY_LINK_STATUS = 'link_status'
          PORT_ATTR_KEY_HW_ADDR = 'hw_addr'

          public

          #
          # Initialize
          # OFComponentManager and OpenFlowController
          #
          def start
            @transactions = Transactions.new(OFD_TXID)
            initialize_topology_mapping
            initialize_flow_mapping
            initialize_queued_call

            super
            # analyze command line options
            # objectId = nil
            component_mgr_id = nil
            system_manager_id = 'systemmanager'
            redis_server = 'localhost'
            redis_port = '6379'
            @vendor_id = ATTR_OFD_VENDORID
            @suppress_lldp = true

            opt = OptionParser.new

            opt.on('--cmpmgr=id',
                   "OFComponentManager's object ID") { |v| component_mgr_id = v }
            # options required for dispatcher
            opt.on('--rip=redis_server_id',
                   "Redis Server's ip address") { |v| redis_server = v }
            opt.on('--rport=redis_server_port',
                   "Redis Server's port") { |v| redis_port = v }
            opt.on('--suppress-lldp', 'Ignore and suppress LLDP packet-in (default)') do|_v|
              @suppress_lldp = true
            end
            opt.on('--no-suppress-lldp', 'Generate packet-in events for LLDP') do|_v|
              @suppress_lldp = false
            end
            opt.on('--vendor=vendor_id',
                   "VendorID (default OpenFlow)") { |v| @vendor_id = v }

            begin
              opt.parse!(ARGV)
              if component_mgr_id.nil?
                fail OptionParser::ParseError,
                     "OFComponentManager's object ID not specified"
              end
              info format(
              "OFComponentManager's Id: %s", component_mgr_id)
              info format("System Manager's object ID: %s", system_manager_id)

            rescue OptionParser::ParseError => e
              error "#{e.message}\n#{opt.help}"
              shutdown!
            end
            # initialization for TopologyController
            @topology.add_observer self

            # THE OpenFlowDriver component instance
            @component = nil

            # create dispatcher for OpenFlowDriver Component
            dispatcher = MessageDispatcher.new(system_manager_id, redis_server, redis_port.to_i)
            dispatcher.set_remote_system_manager
            dispatcher.start

            # create dispatcher (as ODENOS bus client) for OpenFlowController
            @dispatcher = MessageDispatcher.new(system_manager_id, redis_server, redis_port.to_i)
            @dispatcher.set_remote_system_manager
            @dispatcher.start

            # Create OFComponentManager
            @component_manager = OFComponentManager.new(component_mgr_id, dispatcher, self)

            info 'Register OFComponentManager to System Manager'

            info "trema main thread info:#{Thread.current.inspect}"
            # start dispatcher message loop
            @component_thread = Thread.new do
              info "Dispatcher thread info: #{Thread.current.inspect}"

              @component_manager.register_component_type(OpenFlowDriver)
              @component_manager.register_to_system_manager

            end
          end

          ############################################
          # TopologyController's Evnet notification
          ############################################

          #
          # Topology event observer handler
          # @param [RubyTopology::TopologyEvent] event
          #
          def update(event)
            debug ">> #{__method__}"

            unless _driver_ready?
              return
            end

            #  [Symbol] action One of :add, :delete, :update
            # event.subject
            #  [Number, (Number,Trema::Messages::Port), Link] subject Switch dpid, [dpid, Port], or Link
            # event.topology
            #  [Topology] topology

            begin
              case
              when event.subject.is_a?(Integer)
                # switch
                case event.action
                when :add
                  switch_added(event.subject)
                when :delete
                  switch_removed(event.subject)
                when :update
                  switch_updated(event.subject)
                end
              when event.subject.is_a?(Array)
                # port
                case event.action
                when :add
                  port_added(*event.subject)
                when :delete
                  port_removed(*event.subject)
                when :update
                  port_updated(*event.subject)
                end
              when event.subject.is_a?(RubyTopology::Link)
                # link
                case event.action
                when :add
                  link_added(event.subject)
                when :delete
                  link_removed(event.subject)
                when :update
                  link_updated(event.subject)
                end
              end

            rescue => ex
              error ex.backtrace
            end
          end

          ###############################
          # Methods for OpenFlowMessage
          ###############################

          #
          # @param [Integer] dpid
          # @param [Trema::Messages::PacketIn] message
          #
          def packet_in(dpid, message)
            debug ">> #{__method__}"

            super
            unless _driver_ready?
              return
            end

            # no ether flame.
            if message.eth_type.to_i < 0x0600
              return
            end

            if @suppress_lldp && message.eth_type == TYPE_LLDP
              return
            end

            begin
              node = lookup_node(dpid)
              if node.nil?
                error "Ignoring PacketIn from switch(#{dpid}) Node does not exist."
                return
              end
              port = lookup_port(dpid, message.in_port)
              if port.nil?
                error "Ignoring PacketIn from port(#{dpid}:#{message.in_port}) Port does not exist."
                return
              end

              @op2od_link.each do|_link4tuple, link|
                if link.src_node == node.node_id  &&
                   link.src_port == port.port_id
                  debug 'drop in_pakcet form connected port'
                  return
                end
                if link.dst_node == node.node_id  &&
                   link.dst_port == port.port_id
                  debug 'drop in_packet form connected port'
                  return
                end
              end

              info format(' packet_in [%s]', message.inspect)

              # convert match
              # OFPFlowMacth for OpenFlow1.3
              match = OFPFlowMatch.new
              match.in_node = node.node_id
              match.in_port = port.port_id
              match.eth_src = message.eth_src.to_s
              match.eth_dst = message.eth_dst.to_s
              match.eth_type = message.eth_type
              if message.vtag?
                match.vlan_vid = message.vlan_vid
                match.vlan_pcp = message.vlan_prio
              end
              if message.arp?
                match.arp_op = message.arp_op
                match.arp_sha = message.arp_sha.to_s
                match.arp_spa = message.arp_spa.to_s
                match.arp_tpa = message.arp_tpa.to_s
              end
              if message.ipv4?
                match.ipv4_src = message.ipv4_src.to_s
                match.ipv4_dst = message.ipv4_dst.to_s
                match.ip_proto = message.ip_proto
                match.ip_dscp = message.ip_dscp
              end
              if message.tcp?
                match.tcp_src = message.tcp_src
                match.tcp_dst = message.tcp_dst
                match.ip_ecn = message.ip_ecn
              end
              if message.udp?
                match.udp_src = message.udp_src
                match.udp_dst = message.udp_dst
              end
              if message.sctp?
                match.sctp_src = message.sctp_src
                match.sctp_dst = message.sctp_dst
              end
              if message.icmpv4?
                match.icmpv4_type = message.icmpv4_type
                match.icmpv4_code = message.icmpv4_code
              end
              if message.icmpv6?
                match.icmpv6_type = message.icmpv6_type
                match.icmpv6_code = message.icmpv6_code
                match.ipv6_nd_target = message.ipv6_nd_target.to_s
                match.ipv6_nd_sll = message.ipv6_nd_sll.to_s
                match.ipv6_nd_tll = message.ipv6_nd_tll.to_s
              end
              if message.ipv6?
                match.ipv6_src = message.ipv6_src.to_s
                match.ipv6_dst = message.ipv6_dst.to_s
                match.ipv6_flabel = message.ipv6_flabel
                match.ipv6_exthdr = message.ipv6_exthdr
                match.ip_proto = message.ip_proto
                match.ip_dscp = message.ip_dscp
              end
              if message.mpls?
                match.mpls_label = message.mpls_label
                match.mpls_tc = message.mpls_tc
                match.mpls_bos = message.mpls_bos
              end
              if message.pbb?
                match.pbb_isid = message.pbb_isid
              end

              # using UNIX time(float)
              time = Time.now.to_f

              data = Marshal.dump(message)

              inpkt = OFPInPacket.new
              inpkt.node = node.node_id
              inpkt.port = port.port_id
              inpkt.time = time
              inpkt.header = match
              inpkt.data = data

            rescue => ex
              error ex.backtrace
              return
            end

            # TODO
            # currently, block ipv6 packet
            if message.ipv6?
              info 'drop ipv6 packet'
              return
            end
            if message.udp?
              # do not expect packet_in
              cmp_addr = IPAddr.new('0.0.0.0/32')
              src_addr = message.ipv4_src
              if cmp_addr.to_s == src_addr.to_s
                return
              end
            end

            begin
              # POST InPacket
              inpkt = postInPacket(inpkt, _nw_if)
            rescue RequestError => e
              error "POSTing InPacket failed.@call_queue #{e.message}"
            end
          end

          #
          # @param [Integer] dpid
          # @param [Trema::Messages::BarrierReply] message
          #
          def barrier_reply(dpid, message)
            debug ">> #{__method__}"

            unless _driver_ready?
              return
            end

            txid = message.transaction_id
            info "barrier_reply(#{'%#x' % dpid}, transaction_id:#{txid})"
            txinfo = @transactions.lookup_transaction(txid)
            @transactions.delete_transaction txid

            return nil if txinfo.nil?

            case txinfo[:type]
            when :flowentry
              flow = txinfo[:flow]

              begin
                flow = getFlow(flow.flow_id, _nw_if)
              rescue RequestError => e
                warn 'not exist flow.'
                return
              end

              flowentry = txinfo[:flowentry]
              flowentry_state_trans_success(flow, flowentry)
            end
          end

          #
          # @param [Integer] dpid
          # @param [Trema::Messages::Error] message
          #
          def openflow_error(dpid, message)
            warn ">> #{__method__}"

            unless _driver_ready?
              return
            end
            info "openflow_error( #{'%#x' % dpid}, " \
              "[transaction_id:#{message.transaction_id}] )"

            txid = message.transaction_id
            txinfo = @transactions.lookup_transaction(txid)
            @transactions.delete_transaction txid

            return if txinfo.nil?

            case txinfo[:type]
            when :flowentry
              flow = txinfo[:flow]
              flowentry = txinfo[:flowentry]
              flowentry_state_trans_failed(flow, flowentry)
            end
          end

          #
          # @param [Integer] dpid
          #  @param [Trema::Messages::FlowRemoved] message
          #
          def flow_removed(dpid, message)
            debug ">> #{__method__}"

            unless _driver_ready?
              return
            end
            info "flow_removed( #{'%#x' % dpid}, ... )"

            propagate_of_flow_removed(dpid, message)
          end

          #######################################
          # Methods for OpenFlowDriver's Event
          #######################################

          #
          # Called to bind OpenFlowController with OpenFlowDriver(Component).
          # @param [OpenFlowDriver] component OpenFlowDriver component
          #
          # @note This should be called after `component` is connected to a Network
          #
          def register_driver_component(component, connection)
            info ">> #{__method__} ( #{component.remote_object_id} )"

            begin
              @component = component

              # register all current topology
              @topology.each_switch do|dpid, ports|
                switch_added(dpid)
                ports.each do|port|
                  port_added(dpid, port)
                end
              end

              @topology.each_link do|link|
                link_added(link)
              end
              @initial_topology_set = true

              # changed connection state
              connection.state = ComponentConnection::State::RUNNING
              @component.system_manager_interface.put_connection(connection)

            rescue => ex
              error ex.backtrace
            end
          end

          #
          # Called to bind OpenFlowController with OpenFlowDriver(Component).
          # @note This should be called after `component` is disconnected to a Network
          #
          def unregister_driver_component(_component, connection)
            info ">> #{__method__}"

            begin
              # changed connection state
              connection.state = ComponentConnection::State::FINALIZING
              _component.system_manager_interface.put_connection(connection)

              # clear topology.
              # changed flow.status to "failed".
              @topology.each_link do|link|
                link_removed(link)
              end
              @topology.each_switch do|dpid, ports|
                ports.each do|port|
                  port_removed(dpid, port)
                end
                switch_removed(dpid)
              end

              # changed connection state
              connection.state = ComponentConnection::State::NONE
              _component.system_manager_interface.put_connection(connection)

              @component = nil
              @initial_topology_set = false
            rescue => ex
              error ex.backtrace
            end
          end

          #
          # Handler for OutPacketAdded Event
          #  @param [OFPOutPacket, OutPacket] out_packet
          #
          def on_out_packet_added(out_packet)
            debug ">> #{__method__}"

            begin
              info "on_out_packet_added( #{out_packet.inspect} )"
              match = OFPFlowMatch.new(out_packet.header)
              message = Marshal.load(out_packet.data)

              # if out_packet.ports_except.length > 0
              #  node = lookup_node(dpid)
              #  # add all ports except those found in ports_except
              #  out_packet.ports = (node.ports.keys - out_packet.ports_except)
              # end

              actions = []
              if out_packet.ports.length == 0
                # send with OFPP_ALL
                actions = Trema::Actions::SendOutPort.new(
                  port_number: Trema::Controller::OFPP_ALL)

                @op2od_node.each do|out_dpid, _out_node|
                  debug format('send_packet_out (dpid: %#x, %s)',
                               out_dpid, actions.inspect)

                  send_packet_out(out_dpid,
                                  packet_in: message,
                                  actions: actions)
                end
                return
              end

              dpid = get_dpid out_packet.node
              # Use ports
              info ">> out_packet info: #{out_packet.inspect}"
              out_packet.ports.each do|portid|
                actions << Trema::Actions::SendOutPort.new(
                port_number: get_of_port_no(out_packet.node, portid))
              end

              debug format('send_packet_out (dpid: %#x, %s)',
                           dpid, actions.inspect)

              send_packet_out(dpid,
                              packet_in: message,
                              actions: actions)

            rescue => ex
              error ex.backtrace
            end
          end

          #
          # Handler for FlowChanged(add) Event
          #  @param [OFPFlow, BasicFlow] flow
          #
          def on_flow_added(flow)
            debug ">> #{__method__}"

            begin
              flowentries = lookup_flow_entries(flow.flow_id)
              unless flowentries.nil?
                error "invalid flow. #{flow.flow_id}"
                return
              end
              unless flow.enabled
                error "invalid flow. flow.enabled err:#{flow.enabled}"
                return
              end

              # registered flowentries with new list
              new_flowentries = flow_to_flowentries(flow)

              debug '>> register_flow_entries'
              register_flow_entries(flow.flow_id, new_flowentries)

              unless new_flowentries.empty?
                setup_flowentries(flow, new_flowentries)
              end

              begin
                flow = getFlow(flow.flow_id, _nw_if)
                flow.status = Flow::ESTABLISHING
                # PUT flow (update)
                updated_flow = putFlow(flow, _nw_if)
                unless updated_flow.nil?
                  flow.version = updated_flow.version
                end
              rescue RequestError => e
                error "PUT Flow failed: #{e.backtrace}"
              end

            rescue => ex
              error ex.backtrace
            end
          end

          #
          # Handler for FlowChanged(update) Event
          #  @param [OFPFlow, BasicFlow] flow
          #
          def on_flow_update(flow)
            debug ">> #{__method__}"

            begin
              flowentries = lookup_flow_entries(flow.flow_id)
              if flowentries.nil?
                error "invalid flow. #{flow.flow_id}"
                return
              end

              if flow.enabled
                on_flow_update_enabled(flow, flowentries)
              else
                on_flow_update_disabled(flow, flowentries)
              end
            rescue => ex
              error ex.backtrace
            end
          end

          #
          # Handler for FlowChanged(delete) Event
          #  @param [OFPFlow, BasicFlow] flow
          #
          def on_flow_delete(flow)
            debug ">> #{__method__}"

            begin
              # Silently teardown flow entries
              flowentries = lookup_flow_entries(flow.flow_id) || []

              # unregister Flow to avoid further Flow.status update
              unregister_flow_entries flow.flow_id

              to_teardown = flowentries.select do
                |e| (e.status == Flow::ESTABLISHED ||
                e.status == Flow::ESTABLISHING)
              end

              unless flow.nil?
                begin
                  flow = getFlow(flow.flow_id, _nw_if)
                  # PUT flow to "teardown"
                  flow.status = Flow::TEARDOWN
                  flow.enabled = true
                  updated_flow = putFlow(flow, _nw_if)
                  flow.version = updated_flow.version if updated_flow
                rescue RequestError => e
                  warn 'PUT Flow failed.'
                end
              end

              unless to_teardown.empty?
                teardown_flowentries(flow, to_teardown)
              end

              unless flow.nil?
                # PUT flow to "none"
                begin
                  flow = getFlow(flow.flow_id, _nw_if)
                  flow.status = Flow::NONE
                  flow.enabled = true
                  updated_flow = putFlow(flow, _nw_if)
                  flow.version = updated_flow.version if updated_flow
                rescue RequestError => e
                  warn 'PUT Flow failed.'
                end
              end

            rescue => ex
              error ex.backtrace
            end
          end

          #######################################
          # Methods for OpenFlowDriver's Request
          #######################################

          #
          # Handler for GET "node_maps" Request
          #
          def on_get_node_maps
            # return deep copied map
            dpid_key = 'dpid -> Node'
            node_key = 'Node.node_id -> dpid'
            {
              dpid_key => MessagePack.unpack(@op2od_node.to_msgpack),
              node_key => MessagePack.unpack(@od2op_switch.to_msgpack)
            }
          end

          #
          # Handler for GET "port_maps" Request
          #
          def on_get_port_maps
            # return deep copied map
            phy_port_key = '[dpid, port_no] -> Port'
            port_key = '[Node.node_id, Port.port_id] -> [dpid, port_no]'
            {
              phy_port_key => MessagePack.unpack(@op2od_port.to_msgpack),
              port_key => MessagePack.unpack(@od2op_port.to_msgpack)
            }
          end

          #
          # Handler for GET "link_maps" Request
          #
          def on_get_link_maps
            # return deep copied map
            link4tuple_key = '[src_dpid, src_port_no, dst_dpid, dst_port_no] -> Link'
            link_key = 'Link.link_id -> [src_dpid, src_port_no, dst_dpid, dst_port_no]'
            {
              link4tuple_key => MessagePack.unpack(@op2od_link.to_msgpack),
              link_key => MessagePack.unpack(@od2op_link.to_msgpack)
            }
          end

          #
          # Handler for GET "flow_maps" Request
          #
          def on_get_flow_maps
            # return deep copied map
            flow_key = 'Flow.flow_id -> [FlowEntry]'
            {
              flow_key => MessagePack.unpack(@od2op_flow.to_msgpack)
            }
          end

          protected

          #########################################
          # Methods for TopologyControler's Event
          #########################################

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          #
          def switch_added(dpid)
            info format(">> #{__method__} [dpid=%#x]", dpid)

            # lookup Node
            node = lookup_node(dpid)
            unless node.nil?
              # Invalid case: duplicate dpid.
              begin
                deleteNode(node, _nw_if)
              rescue => ex
                # Don't care about request error here
                msg = format('Node(%s) bound to %s. : %s', node.node_id, dpid, ex.backtrace)
                warn "Failed to delete unexpected #{msg}"
              end
              unregister_node(dpid)
            end

            # Create Node base
            node_id = format('node%#x', dpid)
            node = Node.new(node_id: node_id)
            _set_node_attributes(node, dpid)
            # Create Ports here if it is to be supplied here

            begin
              # PUT Node
              node = putNode(node, _nw_if)
              # register updated Node
              register_node(dpid, node)
            rescue RequestError => e
              error "POSTing new Node failed: #{e.backtrace}"
            end
          ensure
            debug format('Exit Switch added %#x', dpid)
          end

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          #
          def switch_removed(dpid)
            info format(">> #{__method__} [dpid=%#x]", dpid)

            # lookup Node
            node = lookup_node(dpid)
            if node.nil?
              warn "Switch/Node to be removed was already gone. dpid: #{dpid}"
            end

            begin
              # DELETE Node
              unless node.nil?
                deleteNode(node, _nw_if)
              end
            rescue ResponseError => e
              error "DELETEing Node(#{node.node_id}) failed: #{e.message}"
            ensure
              unregister_node(dpid)
            end

            invalidate_flow_using_switch dpid
          ensure
            debug format('Exit Switch removed %#x', dpid)
          end

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          #
          def switch_updated(dpid)
            info format(">> #{__method__} [dpid=%#x]", dpid)

            # lookup Node
            node = lookup_node(dpid)
            if node.nil?
              warn format('Switch/Node to be updated was not there. Adding one. dpid:%#x', dpid)
              switch_added(dpid)
              node = lookup_node(dpid)
            end

            # Update Node attribute
            _set_node_attributes(node, dpid)

            # TODO
            # uncomment below if switch can be administratively down
            # invalidate_flow_using_switch dpid

            begin
              # PUT Node
              node = putNode(node, _nw_if)
              # re-register updated Node
              register_node(dpid, node)
            rescue RequestError => e
              error "PUT Node failed: #{e.backtrace}"
            end
          ensure
            debug format('Exit Switch updated %#x', dpid)
          end

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          # @param [Trema::Messages::Port] of_port
          #
          def port_added(dpid, of_port)
            info format('>> %s [dpid=%#x, port=%d]', __method__, dpid, of_port.port_no)

            # lookup Node
            node = lookup_node(dpid)
            if node.nil?
              error "Ignoring Port add where parent switch #{dpid} was not found."
              return
            end

            if of_port.port_no > 0xFFFF
              warn "Invalid Port: #{of_port.port_no}"
              return
            end

            #  lookup port
            port_no = of_port.port_no
            port = lookup_port(dpid, port_no)
            unless port.nil?
              msg = format('dpid:%s, port_no:%d', dpid, port_no)
              warn "Duplicate port found while adding port. #{msg}"
              begin
                # DELETE old Port.port_id
                deletePort(port, _nw_if)
              rescue RequestError => e
                # Don't care about request error here
                msg = format('Port(%s) bound to %s:%d. : %s', port.inspect, dpid, port_no, e.message)
                warn "Failed to delete unexpected #{msg}"
              end
            end

            # Create Port base
            port_id = format('port%s@%#x', port_no, dpid)
            port = Port.new(node_id: node.node_id, port_id: port_id)

            #  Update Port attributes
            _set_port_attributes(port, dpid, of_port)

            begin
              # PUT Port
              port = putPort(port, _nw_if)
              # register Port
              register_port(dpid, port_no, port)

              # Update Node
              if node.ports.include?(port.port_id)
                msg = format('[dpid: %#x, port_no: %s]', dpid, port_no)
                warn "Duplicate port found on switch while adding port. #{msg}"
              else
                node.ports[port.port_id] = port
              end

              # PUT Node
              node = putNode(node, _nw_if)
              # re-register updated Node
              register_node(dpid, node)
            rescue RequestError => e
              msg = format('POST Port(%#x,%d), PUT Node(%s)', dpid, port_no, node.node_id)
              error "Failed to #{msg}: #{e.backtrace}"
            end
          ensure
            debug format('Exit Port added %#x:%d', dpid, of_port.port_no)
          end

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          # @param [Trema::Messages::Port] of_port
          #
          def port_removed(dpid, of_port)
            info format('>> %s [dpid=%#x, port=%d]', __method__, dpid, of_port.port_no)

            # lookup Node
            node = lookup_node(dpid)
            if node.nil?
              error format('Ignoring Port removed. switch(%#x) was not found.', dpid)
              return
            end

            #  lookup Port
            port_no = of_port.port_no
            port = lookup_port(dpid, port_no)
            if port.nil?
              warn format('Port to be removed was already gone. (%#x, %s)', dpid, port_no)
            end

            begin
              unless port.nil?
                # DELETE Port
                deletePort(port, _nw_if)

                # unregister Port
                unregister_port(dpid, port_no)

                # update Node
                node.ports.delete(port.port_id)

                node = putNode(node, _nw_if)
                register_node(dpid, node)
              end
            rescue RequestError => e
              error "DELETEing Port(#{port.inspect}) failed: #{e.backtrace}"
            end

            invalidate_flow_using_port dpid, port_no

          ensure
            debug format('Exit Port removed %#x:%s', dpid, port_no)
          end

          #
          # Topology event observer sub-handler
          # @param [Integer] dpid
          # @param [Trema::Messages::Port] of_port
          #
          def port_updated(dpid, of_port)
            info format('>> %s [dpid=%#x, port=%d]', __method__, dpid, of_port.port_no)

            # lookup Node
            node = lookup_node(dpid)
            if node.nil?
              error format('Ignoring Port update. switch(%#x) was not found.', dpid)
              return
            end

            #  lookup Port
            port_no = of_port.port_no
            port = lookup_port(dpid, port_no)
            if port.nil?
              warn format('Port to be updated was not there. (%#x, %s)', dpid, port_no)
              port_added(dpid, of_port)
              port = lookup_port(dpid, port_no)
            end

            #  Update Port attributes
            _set_port_attributes(port, dpid, of_port)

            unless _is_port_up(of_port) && _is_link_up(of_port)
              invalidate_flow_using_port(dpid, port_no)
            end

            begin
              # PUT Port
              port = putPort(port, _nw_if)
              register_port(dpid, port_no, port)
            rescue RequestError => e
              error "PUT Port to update failed. #{e.backtrace}"
            end
          ensure
            debug format('Exit Port updated [dpid:%#x, of_port:%s]',
                         dpid, of_port.inspect)
          end

          #
          # Topology event observer sub-handler
          # @param [RubyTopology::Link] of_link
          #
          def link_added(of_link)
            info format(">> #{__method__} [link=%s]", of_link.to_s)

            # lookup Link
            link_4tup = [
              of_link.src_dpid, of_link.src_port_no,
              of_link.dst_dpid, of_link.dst_port_no]
            link = lookup_link(link_4tup)
            unless link.nil?
              warn "Link to be added already exist. Removing.: #{link_4tup.inspect}"
              # DELETE old Link.link_id
              begin
                deleteLink(link, _nw_if)
              rescue RequestError => e
                msg = "Link(#{link_4tup.inspect}). : #{e.message}"
                warn "Failed to delete unexpected #{msg}"
              end
            end

            src_node = lookup_node(of_link.src_dpid)
            if src_node.nil?
              error "Src switch #{of_link.src_dpid} not found when adding link"
              return
            end
            src_port = lookup_port(of_link.src_dpid, of_link.src_port_no)
            if src_port.nil?
              msg = "Src port #{of_link.src_dpid}:#{of_link.src_port_no}"
              error "#{msg} not found when adding link"
              return
            end

            dst_node = lookup_node(of_link.dst_dpid)
            if dst_node.nil?
              error "Dst switch #{of_link.dst_dpid} not found when adding link"
              return
            end
            dst_port = lookup_port(of_link.dst_dpid, of_link.dst_port_no)
            if dst_port.nil?
              msg = "Dst port #{of_link.dst_dpid}:#{of_link.dst_port_no} "
              error "#{msg} not found when adding link"
              return
            end

            # Create Link base if new
            link = Link.new(
            src_node.node_id, src_port.port_id,
            dst_node.node_id, dst_port.port_id)

            #  Update Link attributes
            _set_link_attributes(link, of_link)

            begin
              # POST Link
              link = postLink(link, _nw_if)
              # register Link
              register_link(link_4tup, link)
            rescue RequestError => e
              error "POSTing new Link failed: #{e.backtrace}"
            end
          ensure
            debug format('Exit Link added %s', of_link.to_s)
          end

          #
          # Topology event observer sub-handler
          # @param [RubyTopology::Link] of_link
          #
          def link_removed(of_link)
            info format(">> #{__method__} [link=%s]", of_link.to_s)

            # lookup Link
            link_4tup = [
              of_link.src_dpid, of_link.src_port_no,
              of_link.dst_dpid, of_link.dst_port_no]
            link = lookup_link(link_4tup)
            if link.nil?
              warn "Link to be removed was already gone. : #{link_4tup.inspect}"
            end

            begin
              # DELETE Link
              unless link.nil?
                deleteLink(link, _nw_if)
              end
            rescue ResponseError => e
              error "DELETEing Link(#{link.link_id}) failed: #{e.message}"
              # fall through
            ensure
              # unregister Link
              unregister_link(link_4tup)
            end

            invalidate_flow_using_link link_4tup
          ensure
            debug format('Exit Link removed %s', of_link.to_s)
          end

          #
          # Topology event observer sub-handler
          # @param [RubyTopology::Link] of_link
          #
          def link_updated(of_link)
            info format(">> #{__method__} [link=%s]", of_link.to_s)

            # lookup Link
            link_4tup = [
              of_link.src_dpid, of_link.src_port_no,
              of_link.dst_dpid, of_link.dst_port_no]
            link = lookup_link(link_4tup)
            if link.nil?
              warn format('Link to be updated was not there. [%s]', link_4tup.inspect)
              link_added(of_link)
              link = lookup_link(link_4tup)
            end

            # TODO
            # uncomment below is link can be administratively down
            # invalidate_flow_using_link link_4tup

            begin
              # PUT Link
              link = putLink(link, _nw_if)
              register_link(link_4tup, link)
            rescue RequestError => e
              error "PUT Link failed: #{e.backtrace}"
            end
          ensure
            debug format('Exit Link updated %s', of_link.to_s)
          end

          ##########################################
          # Methods for Flow Setting
          ##########################################

          #
          # Invalidate Flows related to specified OpenFlow switch
          # @param [Integer] dpid
          #
          def invalidate_flow_using_switch(dpid)
            debug ">> #{__method__}"

            # lookup impacted flows
            flowinfo = get_all_flowinfo_using_switch(dpid)

            affected_flowid_set = {}

            flowinfo.each do| flowinfo |
              flowid = flowinfo[0]
              flowentries = flowinfo[1]

              affected_flowid_set[flowid] = flowid

              # change flowentry status to none
              # for entries on disappeared Switch(dpid)
              flowentries.each do|flowentry|
                if flowentry.dpid == dpid
                  flowentry.status = Flow::NONE
                end
              end
            end

            affected_flowid_set.each_key do | flowid |
              begin
                # GET Flow
                flow = getFlow(flowid, _nw_if)
                unless @component._is_valid_flow(flow)
                  # trigger FlowChanged event to start recalculation
                  flow.status = Flow::FAILED

                  # PUT Flow
                  putFlow(flow, _nw_if)
                end
              rescue RequestError => e
                error "Invalidating Flow(flowid) state failed. #{e.message}"
              end
            end
          end

          #
          # Invalidate Flows related to specified OpenFlow port
          # @param [Integer] dpid
          # @param [Integer] port_no
          #
          def invalidate_flow_using_port(dpid, port_no)
            debug ">> #{__method__}"

            # lookup impacted flows
            flowinfo = get_all_flowinfo_using_port(dpid, port_no)

            affected_flowid_set = {}

            flowinfo.each do| flowinfo |
              flowid = flowinfo[0]
              flowentries = flowinfo[1]

              affected_flowid_set[flowid] = flowid

              # No change flowentry status
              # should be removed after recalculation triggered by FlowChanged
            end

            affected_flowid_set.each_key do| flowid |
              begin
                flow = getFlow(flowid, _nw_if)
                # trigger FlowChanged event to start recalculation
                flow.status = Flow::FAILED
                # PUT Flow
                putFlow(flow, _nw_if)
              rescue RequestError => e
                error "PUT Flow failed: #{e.backtrace}"
              end
            end
          end

          #
          # Invalidate Flows related to specified link
          # @param [(Integer, Integer, Integer, Integer)] link_4tuple ([src_dpid, src_port_no, dst_dpid, dst_port_no])
          #
          def invalidate_flow_using_link(link_4tuple)
            debug ">> #{__method__}"

            # assuming only 1 link per port
            invalidate_flow_using_port(link_4tuple[0], link_4tuple[1])
          end

          #
          # Sub-Handler for case FlowChanged(enabled=true)
          #  @param [OFPFlow, BasicFlow] flow
          #  @param [Array<FlowEntry>] flowentries
          #
          def on_flow_update_enabled(flow, flowentries)
            debug ">> #{__method__}"

            #  Case FlowChanged(enabled=true)
            new_flowentries = flow_to_flowentries(flow)
            old_flowentries = flowentries

            # Flow state transition
            next_flow_status = flow.status
            if new_flowentries.any? { |e| e.status == :establishing } ||
               new_flowentries.any? { |e| e.status == Flow::ESTABLISHING }
              next_flow_status = Flow::ESTABLISHING
            else
              # no setup in progress
              if old_flowentries.any? { |e| e.status == :teardown } ||
                 old_flowentries.any? { |e| e.status == Flow::TEARDOWN }
                next_flow_status = Flow::TEARDOWN
              end
            end

            unless next_flow_status == flow.status
              begin
                flow = getFlow(flow.flow_id, _nw_if)
                flow.status = next_flow_status
                # PUT flow (update)
                updated_flow = putFlow(flow, _nw_if)
                unless updated_flow.nil?
                  flow.version = updated_flow.version
                end
              rescue RequestError => e
                error "PUTing new Flow failed: #{e.backtrace}"
              end
            end
          end

          #
          # Sub-Handler for case FlowChanged(enabled=false)
          #  @param [OFPFlow, BasicFlow] flow
          #  @param [Array<FlowEntry>] flowentries
          #
          def on_flow_update_disabled(flow, flowentries)
            debug ">> #{__method__}"

            # FlowChanged(enabled=false)
            teardown_flowentries(flow, flowentries)

            next_flow_status = flow.status
            if flowentries.empty?
              next_flow_status = Flow::NONE
            else
              if flowentries.any? { |e| e.status == :teardown } ||
                 flowentries.any? { |e| e.status == Flow::TEARDOWN }
                next_flow_status = Flow::TEARDOWN
              end
            end

            unless next_flow_status == flow.status
              begin
                # PUT flow (update)
                flow = getFlow(flow.flow_id, _nw_if)
                flow.status = next_flow_status
                updated_flow = putFlow(flow, _nw_if)
                unless updated_flow.nil?
                  flow.version = updated_flow.version
                end
              rescue RequestError => e
                error "PUT Flow failed: #{e.backtrace}"
              end
            end
          end

          #
          #  @param [Integer] dpid
          #  @param [Trema::Messages::FlowRemoved] message
          #
          def propagate_of_flow_removed(dpid, message)
            debug ">> #{__method__}"

            match = message.match
            flowinfo = lookup_flowinfo_by_match(dpid, match)
            if flowinfo.nil?
              return
            end

            flowid = flowinfo.first
            flowentry = flowinfo.last

            flowentries = lookup_flow_entries(flowid)
            if flowentries.nil? || flowentries.empty?
              return
            end

            # remove FlowEntry from flowentries
            flowentries.delete(flowentry)

            begin
              flow = getFlow(flowid, _nw_if)
              # PUT flow to "failed"
              flow.status = Flow::FAILED
              flow.enabled = true
              updated_flow = putFlow(flow, _nw_if)
            rescue RequestError => e
              warn 'PUT Flow failed.'
              return
            end
          end

          #
          # @param [OFPFlow] flow
          # @param [FlowEntry] flowentry
          #
          def flowentry_state_trans_success(flow, flowentry)
            debug ">> #{__method__}"

            flowentries = lookup_flow_entries(flow.flow_id)
            if flowentries.nil?
              return
            end
            # ignore if the flowentry is not active
            unless flowentries.include?(flowentry)
              return
            end

            target_entry = flowentries[flowentries.index(flowentry)]
            # FlowEntry status transition
            case flowentry.status
            when Flow::ESTABLISHING
              flowentry.status = Flow::ESTABLISHED
              target_entry.status = Flow::ESTABLISHED
            when Flow::TEARDOWN
              flowentry.status = Flow::NONE
              target_entry.status = Flow::NONE
              flowentries.delete(target_entry)
            else
              warn "Unexpected FlowEntry status : #{flowentry.status}"
            end

            # Flow status transition
            case
            when flowentries.empty?
              # Whole Flow teardown complete
              case flow.status
              when Flow::TEARDOWN
                begin
                  flow = getFlow(flow.flow_id, _nw_if)
                  flow.status = Flow::NONE
                  # PUT flow, silently ignore version mismatch
                  updated_flow = putFlow(flow, _nw_if)
                  unless updated_flow.nil?
                    flow.version = updated_flow.version
                  end
                rescue RequestError => e
                  warn 'PUT Flow failed.'
                end
              end
            when flowentries.all? do |e|
              e.status == Flow::ESTABLISHED
            end
              # Whole Flow setup complete
              case flow.status
              when Flow::ESTABLISHING, Flow::NONE
                begin
                  flow = getFlow(flow.flow_id, _nw_if)
                  flow.status = Flow::ESTABLISHED
                  # PUT flow, silently ignore version mismatch
                  updated_flow = putFlow(flow, _nw_if)
                  unless updated_flow.nil?
                    flow.version = updated_flow.version
                  end
                rescue RequestError => e
                  warn 'PUT Flow failed.'
                end
              end
            end
          end

          #
          # @param [OFPFlow] flow Flow
          # @param [FlowEntry] flowentry
          #
          def flowentry_state_trans_failed(flow, flowentry)
            debug ">> #{__method__}"

            flowentries = lookup_flow_entries(flow.flow_id)
            if flowentries.nil?
              return
            end
            # ignore if the flowentry is not active
            unless flowentries.include?(flowentry)
              return
            end

            target_entry = flowentries[flowentries.index(flowentry)]
            case flowentry.status
            when Flow::ESTABLISHING
              flowentry.status = Flow::FAILED
            when Flow::TEARDOWN
              flowentry.status = Flow::FAILED
              target_entry.status = Flow::FAILED
              # forget about flowentry on teardown error
              flowentries.delete(target_entry)
            else
              warn format(
              'Unexpected FlowEntry status : %s on trans failed',
              flowentry.status)
            end

            unless flow.status == Flow::FAILED
              begin
                flow = getFlow(flow.flow_id, _nw_if)
                # 1st error: notify
                flow.status = Flow::FAILED
                # PUT flow
                updated_flow = putFlow(flow, _nw_if)
                if updated_flow
                  flow.version = updated_flow.version
                end
              rescue RequestError => e
                warn "PUT Flow failed: #{e.backtrace}"
              end
            end
          end

          #
          # Install FlowEntrys to switches
          # @param [OFPFlow] flow
          # @param [Array<FlowEntry>] flowentries
          #
          def setup_flowentries(flow, flowentries)
            debug ">> #{__method__} : #{flow.inspect}, #{flowentries.inspect}"

            idle_timeout = 0
            hard_timeout = 0
            priority     = 0xFFFF
            if flow.respond_to?(:idle_timeout)
              idle_timeout = flow.idle_timeout
            end
            if flow.respond_to?(:hard_timeout)
              hard_timeout = flow.hard_timeout
            end
            if flow.respond_to?(:priority)
              priority     = flow.priority
            end
            if priority != 0xFFFF || priority.is_a?(String)
              priority = priority.to_i
            end
            if priority == OFP_LOW_PRIORITY
              #  OFP_LOW_PRIORITY can not be used
              # for spent in first flow
              priority += 1
            end

            flowentries.each do|flowentry|
              if flowentry.status == Flow::ESTABLISHED
                debug "flowentry.status is #{Flow::ESTABLISHED}"
                next
              end
              if flowentry.status == Flow::ESTABLISHING
                debug "flowentry.status is #{Flow::ESTABLISHING}"
                next
              end
              debug "Change flow_entry state to #{Flow::ESTABLISHING}"
              flowentry.status = Flow::ESTABLISHING

              txid = @transactions.get_transaction_id
              txinfo = {
                type: :flowentry,
                flow: flow,
                flowentry: flowentry }
              @transactions.add_transaction txid, txinfo

              info ">> send_flow_mod_add. #{flowentry.inspect}"
              inst = Instructions::ApplyAction.new(actions: flowentry.actions)

              send_flow_mod_add(flowentry.dpid,
                                match: flowentry.match,
                                idle_timeout: idle_timeout,
                                hard_timeout: hard_timeout,
                                priority: priority,
                                transaction_id: txid,
                                buffer_id: OFP_NO_BUFFER,
                                flags: OFPFF_SEND_FLOW_REM,
                                instructions: [inst])

              # send barrier request with txid to be notified for success
              send_message(flowentry.dpid, BarrierRequest.new(txid))
            end
          end

          #
          # Delete FlowEntrys from switches.
          # FlowEntry is ignored if in :teardown state.
          # FlowEntry is removed from `flowentries` if in :none state.
          # @param [OFPFlow] flow
          # @param [Array<FlowEntry>] flowentries
          #
          def teardown_flowentries(flow, flowentries)
            # idle_timeout = flow.idle_timeout
            # hard_timeout = flow.hard_timeout
            idle_timeout = 0
            hard_timeout = 0
            priority     = flow.priority
            priority     = priority.to_i if priority.is_a?(String)
            if priority == OFP_LOW_PRIORITY
              priority += 1
            end
            flowentries.delete_if { |entry| entry.status == Flow::NONE }
            flowentries.each do|flowentry|
              if flowentry.status == Flow::TEARDOWN # flow_mod already sent
                next
              end

              # Change flow "entry" state to "teardown"
              flowentry.status = Flow::TEARDOWN

              inst = Instructions::ApplyAction.new(actions: flowentry.actions)
              info ">> send_flow_mod. #{flowentry.inspect}"
              send_flow_mod(flowentry.dpid,
                            match: flowentry.match,
                            idle_timeout: idle_timeout,
                            hard_timeout: hard_timeout,
                            out_port: OFPP_ANY,
                            out_group: OFPG_ANY,
                            priority: priority,
                            buffer_id: OFP_NO_BUFFER,
                            flags: OFPFF_SEND_FLOW_REM,
                            command: OFPFC_DELETE_STRICT,
                            strict: true,
                            instructions: nil)
            end
          end

          #
          # Convert OFPFlow to OpenFlow(trema) FlowEntries(dpid, Match, Actions)
          # @param [OFPFlow, BasicFlow] flow
          # @return [Array<FlowEntry>]
          #
          # @note Provided `flow`.path must form a tree(connected acyclic graph)
          #
          def flow_to_flowentries(flow)
            debug ">> #{__method__}"

            # [ FlowEntry ]
            # (src.NodeID) -> [Link]
            out_links = {}
            in_links = {}

            # Node.node_id
            node_list = []

            # build in/out degree map, node_list
            #   Walking from output to ingress.
            #   (Assuming flow.path is ordered from ingress toward output)
            flow.path.reverse_each do|linkId|
              link4tuple = lookup_of_link_tuple(linkId)
              link = lookup_link(link4tuple)

              out_links[link.src_node] ||= []
              out_links[link.src_node] << link

              in_links[link.dst_node] ||= []
              in_links[link.dst_node] << link

              unless node_list.include?(link.dst_node)
                node_list << link.dst_node
              end
              unless node_list.include?(link.src_node)
                node_list << link.src_node
              end
            end

            if flow.path.empty?
              match = Marshal.load(Marshal.dump(flow.matches[0]))
              node_list[0] = match.in_node
            end

            flowentries = []
            node_list.each do|nodeID|
              ##############################
              # set output_port_ids (for actions)
              output_port_ids = []
              unless flow.edge_actions[nodeID].nil?
                flow.edge_actions[nodeID].each do|action|
                  case action.action_type
                  when 'FlowActionOutput'
                    output_port_ids << action.output
                  end
                end
              end
              unless out_links[nodeID].nil?
                out_links[nodeID].each do|link_obj|
                  output_port_ids << link_obj.src_port
                end
              end

              ##############################
              # set match
              matches = []
              flow.matches.each do|match|
                # convert and add matches explicitly specified by Flow
                any_port_match_exist = false
                case match.in_node
                when nodeID, nil
                  if match.in_port.nil?
                    any_port_match_exist = true
                  end

                  if match.in_port.nil? && !output_port_ids.empty?
                    # ANY port match must be converted to every ports but output port
                    node = lookup_node_by_nodeid(nodeID)
                    node.ports.each do|port_id|
                      if output_port_ids.include?(port_id)
                        next
                      end

                      of_in_port_no = get_of_port_no(nodeID, port_id)
                      matches << _match_to_trema_match(
                        match, in_port: of_in_port_no)
                    end

                  else
                    # Add match as ingress node (use match as is)
                    of_in_port_no = get_of_port_no(nodeID, match.in_port)
                    matches << _match_to_trema_match(
                      match, in_port: of_in_port_no)
                  end
                end

                # add matches required for forwarding
                if any_port_match_exist
                  # match any port exist: match as ingress also covers match for forwarding
                  # No more match needs to be added
                else
                  # Add match as forwarding node (rewrite in_port match condition)
                  in_link = in_links[nodeID]
                  unless in_link.nil?
                    in_link.each do|fwd_link|
                      of_in_port_no = get_of_port_no(nodeID, fwd_link.dst_port)
                      matches << _match_to_trema_match(
                        match, in_port: of_in_port_no)
                    end
                  end
                end
              end
              if matches.empty?
                warn "Matches for Flow(#{flow.flow_id}) is emptry."
              end

              ##############################
              # set actions
              actions = []
              # set forwarding actions first
              unless out_links[nodeID].nil?
                out_link = out_links[nodeID]
                out_link.each do|fwd_link|
                  port_no = get_of_port_no(nodeID, fwd_link.src_port)
                  actions << Trema::Actions::SendOutPort.new(port_number: port_no)
                end
              end
              # then edge actions, which may have side effects.
              unless flow.edge_actions[nodeID].nil?
                actions += _edge_actions_to_trema_actions(
                nodeID, flow.edge_actions[nodeID])
                actions.compact!
                if actions.empty?
                  warn "Actions for Flow(#{flow.flow_id}) is emptry."
                end
              end

              # walk nodes related to this flow
              dpid = get_dpid(nodeID)
              matches.each do|match|
                flowentries << FlowEntry.new(
                  dpid: dpid,
                  match: match,
                  actions: actions,
                  status: flow.status)
              end
            end
            flowentries
          end

          #########################################
          # Methods for OpenFlowContoller Support
          #########################################

          #
          # Set attributes to `node` using `dpid`
          # @param [Odenos::Component::Node] node
          # @param [Integer] dpid
          #
          def _set_node_attributes(node, dpid)
            info "_set_node_attributes( #{node.inspect}, #{'%#x' % dpid})"
            node.attributes[Node::ATTR_KEY_ADMIN_STATUS] = 'UP'
            node.attributes[Node::ATTR_KEY_OPER_STATUS] = 'UP'
            node.attributes[Node::ATTR_KEY_PHYSICAL_ID] = '%#x' % dpid
            #node.attributes[Node::ATTR_KEY_VENDOR_ID] = ATTR_OFD_VENDORID
            node.attributes[Node::ATTR_KEY_VENDOR_ID] = @vendor_id
          end

          #
          # Set attributes to `port` using `of_port`
          # @param [Port] port
          # @param [Integer] dpid
          # @param [Trema::Messages::Port] of_port
          #
          def _set_port_attributes(port, dpid, of_port)
            msg = format('[port:%s, dpid:%#x, of_port]', port.inspect, dpid, of_port.inspect)
            info "_set_port_attributes #{msg}"

            if _is_port_up(of_port)
              port.attributes[Port::ATTR_KEY_ADMIN_STATUS] = 'UP'
            else
              port.attributes[Port::ATTR_KEY_ADMIN_STATUS] = 'DOWN'
            end
            if _is_link_up(of_port)
              port.attributes[PORT_ATTR_KEY_LINK_STATUS] = 'UP'
            else
              port.attributes[PORT_ATTR_KEY_LINK_STATUS] = 'DOWN'
            end
            if _is_port_up(of_port) && _is_link_up(of_port)
              port.attributes[Port::ATTR_KEY_OPER_STATUS] = 'UP'
            else
              port.attributes[Port::ATTR_KEY_OPER_STATUS] = 'DOWN'
            end

            port.attributes[Port::ATTR_KEY_PHYSICAL_ID] = '%d@%#x' % [of_port.port_no, dpid]
            #port.attributes[Port::ATTR_KEY_VENDOR_ID] = ATTR_OFD_VENDORID
            port.attributes[Port::ATTR_KEY_VENDOR_ID] = @vendor_id

            port.attributes[PORT_ATTR_KEY_HW_ADDR] = of_port.hw_addr.to_s

            case
            when of_port.curr & Trema::Messages::Port::OFPPF_10GB_FD
              port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH] = '10000'
            when of_port.curr & Trema::Messages::Port::OFPPF_1GB_FD,
            of_port.curr & Trema::Messages::Port::OFPPF_1GB_HD
              port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH] = '1000'
            when of_port.curr & Trema::Messages::Port::OFPPF_100MB_FD,
            of_port.curr & Trema::Messages::Port::OFPPF_100MB_HD
              port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH] = '100'
            when of_port.curr & Trema::Messages::Port::OFPPF_10MB_FD,
            of_port.curr & Trema::Messages::Port::OFPPF_10MB_HD
              port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH] = '10'
            end

            # Copy MAX_BANDWIDTH if UNRESERVED_BANDWIDTH not set
            unless port.attributes.include?(Port::ATTR_KEY_UNRESERVED_BANDWIDTH)
              port_attr = port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH]
              port.attributes[Port::ATTR_KEY_UNRESERVED_BANDWIDTH] = port_attr
            end
            info "Exit _set_port_attributes #{msg}"
          end

          #
          # Set attributes to `link` using `of_link`
          # @param [Odenos::Component::Link] link
          # @param [RubyTopology::Link] of_link
          #
          def _set_link_attributes(link, of_link)
            msg = format('[link:%s, of_link:%s]', link.inspect, of_link.inspect)
            info "_set_link_attributes #{msg}"

            # get port info
            src_port = lookup_port_by_odenos_id(link.src_node, link.src_port)
            dst_port = lookup_port_by_odenos_id(link.dst_node, link.dst_port)
            src_port_attr = src_port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH]
            dst_port_attr = dst_port.attributes[Port::ATTR_KEY_MAX_BANDWIDTH]
            src_bandwidth = _bandwidth_str_to_int(src_port_attr)
            dst_bandwidth = _bandwidth_str_to_int(dst_port_attr)

            if src_bandwidth < dst_bandwidth
              _bandwidth = src_bandwidth
            else
              _bandwidth = dst_bandwidth
            end

            str_bandwidth = _bandwidth_int_to_str(_bandwidth)
            link.attributes[Link::ATTR_KEY_MAX_BANDWIDTH] = str_bandwidth
            # Copy MAX_BANDWIDTH if UNRESERVED_BANDWIDTH not set
            unless link.attributes.include?(Link::ATTR_KEY_UNRESERVED_BANDWIDTH)
              link.attributes[Link::ATTR_KEY_UNRESERVED_BANDWIDTH] = str_bandwidth
            end
            info "Exit _set_link_attributes(#{link.inspect})"
          end

          def _bandwidth_str_to_int(bandwidth)
            match = (bandwidth =~ Regexp.compile('^([0-9]\d*|0)(\.\d+)?'))
            unless match
              error format('error:format error:%s', bandwidth)
              return nil
            end
            _bandwidth = match[0].to_f
            (_bandwidth * 1000).to_i         # Mbps -> Kbps
          end

          def _bandwidth_int_to_str(bandwidth)
            _bandwidth = bandwidth.to_f / 1000      # Kbps -> MBps
            match = (_bandwidth.to_s =~ Regexp.compile('\.[0]'))
            unless match
              return _bandwidth.to_s
            end
            (_bandwidth.to_i).to_s
          end

          #
          # Convert OFPFlow.match to Trema Match
          # @param [OFPFlowMatch,BasicFlowMatch] match
          # @param [Hash] modifier Conditions to modify from match. Same Hash format as Trema.initialize
          # @return [Trema::Match]
          #
          def _match_to_trema_match(match, modifier = {})
            debug ">> #{__method__}"

            arg = match.to_hash.dup
            # remove non-OpenFlow keys
            arg.delete(:type)
            arg.delete(:in_node)

            arg.merge!(modifier)

            # convert match
            # OFPFlowMacth for OpenFlow1.3
            conv_arg = {}
            conv_arg[:in_port] = arg[:in_port].to_i if arg.key?(:in_port)
            conv_arg[:in_phy_port] = arg[:in_phy_port].to_i if arg.key?(:in_phy_port)
            conv_arg[:metadata] = arg[:metadata].to_i if arg.key?(:metadata)
            conv_arg[:metadata_mask] = arg[:metadata_mask].to_i if arg.key?(:metadata_mask)
            conv_arg[:eth_src] = Trema::Mac.new(arg[:eth_src]) if arg.key?(:eth_src)
            conv_arg[:eth_src_mask] = Trema::Mac.new(arg[:eth_src_mask]) if arg.key?(:eth_src_mask)
            conv_arg[:eth_dst] = Trema::Mac.new(arg[:eth_dst]) if arg.key?(:eth_dst)
            conv_arg[:eth_dst_mask] = Trema::Mac.new(arg[:eth_dst_mask]) if arg.key?(:eth_dst_mask)
            conv_arg[:eth_type] = arg[:eth_type].to_i if arg.key?(:eth_type)
            conv_arg[:vlan_vid] = arg[:vlan_vid].to_i if arg.key?(:vlan_vid)
            conv_arg[:vlan_vid_mask] = arg[:vlan_vid_mask].to_i if arg.key?(:vlan_vid_mask)
            conv_arg[:vlan_pcp] = arg[:vlan_pcp].to_i if arg.key?(:vlan_pcp)
            conv_arg[:ip_dscp] = arg[:ip_dscp].to_i if arg.key?(:ip_dscp)
            # TODO: PFS not support.
            # conv_arg[:ip_ecn] = arg[:ip_ecn].to_i if arg.key?(:ip_ecn)
            conv_arg[:ip_proto] = arg[:ip_proto].to_i if arg.key?(:ip_proto)

            conv_arg[:ipv4_src] = IPAddr.new(arg[:ipv4_src]) if arg.key?(:ipv4_src)
            conv_arg[:ipv4_src_mask] = IPAddr.new(arg[:ipv4_src_mask]) if arg.key?(:ipv4_src_mask)
            conv_arg[:ipv4_dst] = IPAddr.new(arg[:ipv4_dst]) if arg.key?(:ipv4_dst)
            conv_arg[:ipv4_dst_mask] = IPAddr.new(arg[:ipv4_dst_mask]) if arg.key?(:ipv4_dst_mask)

            conv_arg[:tcp_src] = arg[:tcp_src].to_i if arg.key?(:tcp_src)
            conv_arg[:tcp_dst] = arg[:tcp_dst].to_i if arg.key?(:tcp_dst)
            conv_arg[:udp_src] = arg[:udp_src].to_i if arg.key?(:udp_src)
            conv_arg[:udp_dst] = arg[:udp_dst].to_i if arg.key?(:udp_dst)
            conv_arg[:sctp_src] = arg[:sctp_src].to_i if arg.key?(:sctp_src)
            conv_arg[:sctp_dst] = arg[:sctp_dst].to_i if arg.key?(:sctp_dst)
            conv_arg[:icmpv4_type] = arg[:icmpv4_type].to_i if arg.key?(:icmpv4_type)
            conv_arg[:icmpv4_code] = arg[:icmpv4_code].to_i if arg.key?(:icmpv4_code)

            conv_arg[:arp_op] = arg[:arp_op].to_i if arg.key?(:arp_op)
            conv_arg[:arp_spa] = IPAddr.new(arg[:arp_spa]) if arg.key?(:arp_spa)
            conv_arg[:arp_spa_mask] = IPAddr.new(arg[:arp_spa_mask]) if arg.key?(:arp_spa_mask)
            conv_arg[:arp_tpa] = IPAddr.new(arg[:arp_tpa]) if arg.key?(:arp_tpa)
            conv_arg[:arp_tpa_mask] = IPAddr.new(arg[:arp_tpa_mask]) if arg.key?(:arp_tpa_mask)
            conv_arg[:arp_sha] = Trema::Mac.new(arg[:arp_sha]) if arg.key?(:arp_sha)
            conv_arg[:arp_sha_mask] = Trema::Mac.new(arg[:arp_sha_mask]) if arg.key?(:arp_sha_mask)
            conv_arg[:arp_tha] = Trema::Mac.new(arg[:arp_tha]) if arg.key?(:arp_tha)
            conv_arg[:arp_tha_mask] = Trema::Mac.new(arg[:arp_tha_mask]) if arg.key?(:arp_tha_mask)

            conv_arg[:ipv6_src] = IPAddr.new(arg[:ipv6_src]) if arg.key?(:ipv6_src)
            conv_arg[:ipv6_src_mask] = IPAddr.new(arg[:ipv6_src_mask]) if arg.key?(:ipv6_src_mask)
            conv_arg[:ipv6_dst] = IPAddr.new(arg[:ipv6_dst]) if arg.key?(:ipv6_dst)
            conv_arg[:ipv6_dst_mask] = IPAddr.new(arg[:ipv6_dst_mask]) if arg.key?(:ipv6_dst_mask)
            conv_arg[:ipv6_flabel] = arg[:ipv6_flabel].to_i if arg.key?(:ipv6_flabel)
            conv_arg[:ipv6_flabel_mask] = arg[:ipv6_flabel_mask].to_i if arg.key?(:ipv6_flabel_mask)
            conv_arg[:icmpv6_type] = arg[:icmpv6_type].to_i if arg.key?(:icmpv6_type)
            conv_arg[:icmpv6_code] = arg[:icmpv6_code].to_i if arg.key?(:icmpv6_code)
            conv_arg[:ipv6_nd_target] = IPAddr.new(arg[:ipv6_nd_target]) if arg.key?(:ipv6_nd_target)
            conv_arg[:ipv6_nd_sll] = Trema::Mac.new(arg[:ipv6_nd_sll]) if arg.key?(:ipv6_nd_sll)
            conv_arg[:ipv6_nd_tll] = Trema::Mac.new(arg[:ipv6_nd_tll]) if arg.key?(:ipv6_nd_tll)

            conv_arg[:mpls_label] = arg[:mpls_label].to_i if arg.key?(:mpls_label)
            conv_arg[:mpls_tc] = arg[:mpls_tc].to_i if arg.key?(:mpls_tc)
            conv_arg[:mpls_bos] = arg[:mpls_bos].to_i if arg.key?(:mpls_bos)
            conv_arg[:pbb_isid] = arg[:pbb_isid].to_i if arg.key?(:pbb_isid)
            conv_arg[:pbb_isid_mask] = arg[:pbb_isid_mask].to_i if arg.key?(:pbb_isid_mask)
            conv_arg[:tunnel_id] = arg[:tunnel_id].to_i if arg.key?(:tunnel_id)
            conv_arg[:tunnel_id_mask] = arg[:tunnel_id_mask].to_i if arg.key?(:tunnel_id_mask)
            conv_arg[:ipv6_exthdr] = arg[:ipv6_exthdr].to_i if arg.key?(:ipv6_exthdr)
            conv_arg[:ipv6_exthdr_mask] = arg[:ipv6_exthdr_mask].to_i if arg.key?(:ipv6_exthdr_mask)

            # Translate match to Trema::Match
            Trema::Match.new(conv_arg)
          end

          #
          # Convert OFPFlow.edge_actions to Trema Actions
          # @param [Integer] nodeid ODENOS Node ID
          # @param [Array<OFPFlowAction>, Array<BasicFlowAction>] edge_actions
          # @return [Array<Trema::Actions::*>]
          #
          def _edge_actions_to_trema_actions(nodeid, edge_actions)
            debug ">> #{__method__}"

            actions = []
            edge_actions.each do |od_action|
              # FlowAction for OpenFlow1.3
              case od_action.action_type
              when 'FlowActionOutput'
                of_port_no = get_of_port_no(nodeid, od_action.output)
                actions << Trema::Actions::SendOutPort.new(port_number: of_port_no)
              when 'OFPFlowActionCopyTtlIn'
                actions.push(Trema::Actions::CopyTtlIn.new)
              when 'OFPFlowActionCopyTtlOut'
                actions.push(Trema::Actions::CopyTtlOut.new)
              when 'OFPFlowActionDecIpTtl'
                actions.push(Trema::Actions::DecIpTtl.new)
              when 'OFPFlowActionDecMplsTtl'
                actions.push(Trema::Actions::DecMplsTtl.new)
              when 'OFPFlowActionExperimenter'
                actions.push(Trema::Actions::Experimenter.new(
                  od_action.experimenter_id, od_action.body.unpack('C*')))
              when 'OFPFlowActionGroupAction'
                actions.push(Trema::Actions::GroupAction.new(od_action.group_id))
              when 'OFPFlowActionPopMpls'
                actions.push(Trema::Actions::PopMpls.new(od_action.eth_type))
              when 'OFPFlowActionPopPbb'
                actions.push(Trema::Actions::PopPbb.new)
              when 'OFPFlowActionPopVlan'
                actions.push(Trema::Actions::PopVlan.new)
              when 'OFPFlowActionPushMpls'
                actions.push(Trema::Actions::PushMpls.new(od_action.eth_type))
              when 'OFPFlowActionPushPbb'
                actions.push(Trema::Actions::PushPbb.new(od_action.eth_type))
              when 'OFPFlowActionPushVlan'
                actions.push(Trema::Actions::PushVlan.new(od_action.eth_type))
              when 'OFPFlowActionSetIpTtl'
                actions.push(Trema::Actions::SetIpTtl.new(od_action.ip_ttl))
              when 'OFPFlowActionSetMplsTtl'
                actions.push(Trema::Actions::SetMplsTtl.new(od_action.mpls_ttl))
              when 'OFPFlowActionSetQueue'
                actions.push(Trema::Actions::SetQueue.new(od_action.queue_id))
              when 'OFPFlowActionSetField'
                match = od_action.match
                fields = []
                # Set Action flelds
                # invalid OXM_IN_PORT, OXM_IN_PHY_PORT, OXM_METADATA
                if match.is_a? OFPFlowMatch
                  unless match.eth_src.nil?
                    val = match.eth_src
                    unless match.eth_src_mask.nil?
                      val = format('%s/%s', match.eth_src, match.eth_src_mask)
                    end
                    fields.push(Trema::Actions::EthSrc.new(mac_address: Trema::Mac.new(val)))
                  end
                  unless match.eth_dst.nil?
                    val = match.eth_dst
                    unless match.eth_dst_mask.nil?
                      val = format('%s/%s', match.eth_dst, match.eth_dst_mask)
                    end
                    fields.push(Trema::Actions::EthDst.new(mac_address: Trema::Mac.new(val)))
                  end
                  unless match.eth_type.nil?
                    val = match.eth_type.to_i
                    fields.push(Trema::Actions::EtherType.new(val))
                  end
                  unless match.vlan_vid.nil?
                    val = match.vlan_vid.to_i
                    unless match.vlan_vid_mask.nil?
                      val = match.vlan_vid.to_i & match.vlan_vid_mask.to_i
                    end
                    fields.push(Trema::Actions::VlanVid.new(val))
                  end
                  unless match.vlan_pcp.nil?
                    val = match.vlan_pcp.to_i
                    fields.push(Trema::Actions::VlanPriority.new(val))
                  end
                  unless match.ip_dscp.nil?
                    val = match.ip_dscp.to_i
                    fields.push(Trema::Actions::IpDscp.new(val))
                  end
                  unless match.ip_ecn.nil?
                    val = match.ip_ecn.to_i
                    fields.push(Trema::Actions::IpEcn.new(val))
                  end
                  unless match.ip_proto.nil?
                    val = match.ip_proto.to_i
                    fields.push(Trema::Actions::IpProto.new(val))
                  end
                  unless match.ipv4_src.nil?
                    val = match.ipv4_src
                    unless match.ipv4_src_mask.nil?
                      val = format('%s/%s', match.ipv4_src, match.ipv4_src_mask)
                    end
                    fields.push(Trema::Actions::Ipv4SrcAddr.new(IPAddr.new(val)))
                  end
                  unless match.ipv4_dst.nil?
                    val = match.ipv4_dst
                    unless match.ipv4_dst_mask.nil?
                      val = format('%s/%s', match.ipv4_dst, match.ipv4_dst_mask)
                    end
                    fields.push(Trema::Actions::Ipv4DstAddr.new(IPAddr.new(val)))
                  end
                  unless match.tcp_src.nil?
                    val = match.tcp_src.to_i
                    fields.push(Trema::Actions::TcpSrcPort.new(val))
                  end
                  unless match.tcp_dst.nil?
                    val = match.tcp_dst.to_i
                    fields.push(Trema::Actions::TcpDstPort.new(val))
                  end
                  unless match.udp_src.nil?
                    val = match.udp_src.to_i
                    fields.push(Trema::Actions::UdpSrcPort.new(val))
                  end
                  unless match.udp_dst.nil?
                    val = match.udp_dst.to_i
                    fields.push(Trema::Actions::UdpDstPort.new(val))
                  end
                  unless match.sctp_src.nil?
                    val = match.sctp_src.to_i
                    fields.push(Trema::Actions::SctpSrcPort.new(val))
                  end
                  unless match.sctp_dst.nil?
                    val = match.sctp_dst.to_i
                    fields.push(Trema::Actions::SctpDstPort.new(val))
                  end
                  unless match.icmpv4_type.nil?
                    val = match.icmpv4_type.to_i
                    fields.push(Trema::Actions::Icmpv4Type.new(val))
                  end
                  unless match.icmpv4_code.nil?
                    val = match.icmpv4_code.to_i
                    fields.push(Trema::Actions::Icmpv4Code.new(val))
                  end
                  unless match.arp_op.nil?
                    val = match.arp_op.to_i
                    fields.push(Trema::Actions::ArpOp.new(val))
                  end
                  unless match.arp_spa.nil?
                    val = match.arp_spa
                    unless match.arp_spa_mask.nil?
                      val = format('%s/%s', match.arp_spa, match.arp_spa_mask)
                    end
                    fields.push(Trema::Actions::ArpSpa.new(IPAddr.new(val)))
                  end
                  unless match.arp_tpa.nil?
                    val = match.arp_tpa
                    unless match.arp_tpa_mask.nil?
                      val = format('%s/%s', match.arp_tpa, match.arp_tpa_mask)
                    end
                    fields.push(Trema::Actions::ArpTpa.new(IPAddr.new(val)))
                  end
                  unless match.arp_sha.nil?
                    val = match.arp_sha
                    unless match.arp_sha_mask.nil?
                      val = format('%s/%s', match.arp_sha, match.arp_sha_mask)
                    end
                    fields.push(Trema::Actions::ArpSha.new(Trema::Mac.new(val)))
                  end
                  unless match.arp_tha.nil?
                    val = match.arp_tha
                    unless match.arp_tha_mask.nil?
                      val = format('%s/%s', match.arp_tha, match.arp_tha_mask)
                    end
                    fields.push(Trema::Actions::ArpTha.new(Trema::Mac.new(val)))
                  end
                  unless match.ipv6_src.nil?
                    val = match.ipv6_src
                    unless match.ipv6_src_mask.nil?
                      val = format('%s/%s', match.ipv6_src, match.ipv6_src_mask)
                    end
                    fields.push(Trema::Actions::Ipv6SrcAddr.new(IPAddr.new(val)))
                  end
                  unless match.ipv6_dst.nil?
                    val = match.ipv6_dst
                    unless match.ipv6_dst_mask.nil?
                      val = format('%s/%s', match.ipv6_dst, match.ipv6_dst_mask)
                    end
                    fields.push(Trema::Actions::Ipv6DstAddr.new(IPAddr.new(val)))
                  end
                  unless match.ipv6_flabel.nil?
                    val = match.ipv6_flabel.to_i
                    unless match.ipv6_flabel_mask.nil?
                      val = match.ipv6_flabel.to_i & match.ipv6_flabel_mask.to_i
                    end
                    fields.push(Trema::Actions::Ipv6FlowLabel.new(val))
                  end
                  unless match.icmpv6_type.nil?
                    val = match.icmpv6_type.to_i
                    fields.push(Trema::Actions::Icmpv6Type.new(val))
                  end
                  unless match.icmpv6_code.nil?
                    val = match.icmpv6_code.to_i
                    fields.push(Trema::Actions::Icmpv6Code.new(val))
                  end
                  unless match.ipv6_nd_target.nil?
                    val = match.ipv6_nd_target
                    fields.push(Trema::Actions::Ipv6NdTarget.new(IPAddr.new(val)))
                  end
                  unless match.ipv6_nd_sll.nil?
                    val = match.ipv6_nd_sll
                    fields.push(Trema::Actions::Ipv6NdSll.new(Trema::Mac.new(val)))
                  end
                  unless match.ipv6_nd_tll.nil?
                    val = match.ipv6_nd_tll
                    fields.push(Trema::Actions::Ipv6NdTll.new(Trema::Mac.new(val)))
                  end
                  unless match.mpls_label.nil?
                    val = match.mpls_label.to_i
                    fields.push(Trema::Actions::MplsLabel.new(val))
                  end
                  unless match.mpls_tc.nil?
                    val = match.mpls_tc.to_i
                    fields.push(Trema::Actions::MplsTc.new(val))
                  end
                  unless match.mpls_bos.nil?
                    val = match.mpls_bos.to_i
                    fields.push(Trema::Actions::MplsBos.new(val))
                  end
                  unless match.pbb_isid.nil?
                    val = match.pbb_isid.to_i
                    unless match.pbb_isid_mask.nil?
                      val = match.pbb_isid.to_i & match.pbb_isid_mask.to_i
                    end
                    fields.push(Trema::Actions::PbbIsid.new(val))
                  end
                  unless match.tunnel_id.nil?
                    # TODO
                    # convert String"0X..." -> Integer
                    val = match.tunnel_id.to_i
                    unless match.tunnel_id_mask.nil?
                      val = match.tunnel_id.to_i & match.tunnel_id_mask.to_i
                    end
                    fields.push(Trema::Actions::TunnelId.new(val))
                  end
                  unless match.ipv6_exthdr.nil?
                    val = match.ipv6_exthdr.to_i
                    unless match.ipv6_exthdr_mask.nil?
                      val = match.ipv6_exthdr.to_i & match.ipv6_exthdr_mask.to_i
                    end
                    fields.push(Trema::Actions::Ipv6Exthdr.new(val))
                  end
                end
                unless fields.empty?
                  actions << Trema::Actions::SetField.new(action_set: fields)
                end
              end
            end

            actions
          end

          #
          # Check OpenFlowDriver exists
          # @return [Boolean]
          #
          def _driver_ready?
            (!@component.nil? && @initial_topology_set)
          end

          #
          # Get NetworkInterface from OpenFlowDriver
          # @return [NetworkInterface]
          #
          def _nw_if
            if @component.nil?
              return nil
            end

            nw_id = @component.network_id
            if nw_id.nil?
              return nil
            end

            nw_if = @component.network_interfaces[nw_id]
            nw_if
          end

          #
          # Check Port state is "UP"
          # @param [Trema::Messages::Port] port
          # @return [Boolean]
          #
          def _is_port_up(port)
            port_down = port.config & Trema::Messages::Port::OFPPC_PORT_DOWN
            if  port_down == Trema::Messages::Port::OFPPC_PORT_DOWN
              return false
            end
            true
          end

          #
          # Check Link state is "UP"
          # @param [Trema::Messages::Port] port
          # @return [Boolean]
          #
          def _is_link_up(port)
            link_down = port.state & Trema::Messages::Port::OFPPC_PORT_DOWN
            if link_down == Trema::Messages::Port::OFPPC_PORT_DOWN
              return false
            end
            true
          end

          ####################

          public

          ####################

          #
          # Initialize objects required for queued call
          #
          def initialize_queued_call
            @queue ||= Queue.new
            @queued_call_thread ||= Thread.new do
              loop { @queue.pop.call }
            end
          end

          #
          # task push to queue
          #
          def submit(task = nil, &block)
            task ||= block
            @queue ||= Queue.new
            @queue.push(task)
          end

          #
          # @param [Symbol] method method to call on Trema thread
          # @param [Array] args method arguments
          # @param [Hash] _options call options(custom txid, enable need reply flag, etc.)
          # @return [Resonse]
          # @note Call from ODENOS thread
          #
          def async_call(method, args = [], _options = {})
            debug "async_call( #{method}, ... )"
            retval = nil
            submit do
              retval = send(method, *args)
            end
            retval
          end

          #
          # @param [Symbol] method method to call on Trema thread
          # @param [Array] args method arguments
          # @return [Object] return value the method called
          # @note Call only from ODENOS thread
          #
          def sync_call(method, args = [], options = {})
            info "sync_call( #{method}, ... )"
            async_call(method, args, options)
          end
        end
      end
    end
  end
end
