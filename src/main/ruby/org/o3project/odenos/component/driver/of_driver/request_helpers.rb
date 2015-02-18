
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

require 'odenos'

module Odenos
  module Component
    module Driver
      module  OFDriver
        ##################################
        # ODENOS Request helpers
        ##################################
        module RequestHelpers
          include RequestErrors
          include Odenos::Core
          include Odenos::Component
          include Odenos::Util::Logger
          # POST Node
          # @return [Node] updated Node on Success
          # @raise [RequestError]
          def postNode(node, nw_if)
            check_param(node, nw_if)
            response = nw_if.post_node(node)

            status = response.status_code
            case status
            when Response::OK
              node = Node.new(response.body)
              return node
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # PUT Node
          # @return [Node] updated Node on Success
          # @raise [ConflictError, RequestError]
          def putNode(node, nw_if)
            check_param(node, nw_if)
            curr_node = nw_if.get_node(node.node_id)
            unless curr_node.nil?
              node.version = curr_node.version
            end
            response = nw_if.put_node(node)
            status = response.status_code

            node = nil
            case status
            when Response::OK
              # Success: update version, etc.
              node = Node.new(response.body)
            when Response::CREATED
              # Success: update version, etc.
              node = Node.new(response.body)
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end

            node
          end

          # DELETE Node
          # @param [Node] node
          # @return [void]
          # @raise [NotFoundError, ConflictError, RequestError]
          def deleteNode(node, nw_if)
            check_param(node, nw_if)
            response = nw_if.del_node(node.node_id)

            status = response.status_code
            case status
            when Response::OK
              # Success: Nothing to do
              return nil
            when Response::NOT_FOUND
              fail NotFoundError.new(response), "#{status} Not Found: node:#{node.inspect}"
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # POST Port and update topology mapping
          # @return [Port] updated Port on Success
          # @raise [NotFoundError, RequestError]
          def postPort(port, nw_if)
            check_param(port, nw_if)
            response = nw_if.post_port(port)

            status = response.status_code
            case status
            when Response::OK
              port = Port.new(response.body)
              return port
            when Response::NOT_FOUND
              # parent node not found
              fail NotFoundError.new(response), "#{status} Not Found: port:#{port.inspect}"
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # PUT Port
          # @return [Port] updated Port on Success
          # @raise [NotFoundError, ConflictError, RequestError]
          def putPort(port, nw_if)
            check_param(port, nw_if)
            curr_port = nw_if.get_port(port.node_id, port.port_id)
            unless curr_port.nil?
              port.version = curr_port.version
            end
            response = nw_if.put_port(port)
            status = response.status_code
            port = nil
            case status
            when Response::OK
              # Success: update version, etc.
              port = Port.new(response.body)
            when Response::CREATED
              # Success: update version, etc.
              port = Port.new(response.body)
            when Response::NOT_FOUND
              # parent node not found
              fail NotFoundError.new(response), "#{status} Not Found: port:#{port.inspect}"
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end

            port
          end

          # DELETE Port
          # @param [Port] port
          # @return [void]
          # @raise [NotFoundError, ConflictError, RequestError]
          def deletePort(port, nw_if)
            check_param(port, nw_if)
            response = nw_if.del_port(port.node_id, port.port_id)

            status = response.status_code
            case status
            when Response::OK
              # Success: Nothing to do
              return nil
            when Response::NOT_FOUND
              fail NotFoundError.new(response), "#{status} Not Found: port:#{port.inspect}"
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # POST Link
          # @return [Link] updated Link on Success
          # @raise [RequestError]
          def postLink(link, nw_if)
            check_param(link, nw_if)
            response = nw_if.post_link(link)

            status = response.status_code
            case status
            when Response::OK
              link = Link.new(response.body)
              return link
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # PUT Link
          # @return [Link] updated Node on Success
          # @raise [ConflictError, RequestError]
          def putLink(link, nw_if)
            check_param(link, nw_if)
            curr_link = nw_if.get_link(link.link_id)
            unless curr_link.nil?
              link.version = curr_link.version
            end
            response = nw_if.put_link(link)

            status = response.status_code
            case status
            when Response::OK
              # Success: update version, etc.
              link = Link.new(response.body)
              return link
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # DELETE Link
          # @param [String, Link] link String(Link.link_id) or Link
          # @return [void]
          # @raise [NotFoundError, ConflictError, RequestError]
          def deleteLink(link, nw_if)
            check_param(link, nw_if)
            link_id = nil
            link_obj = nil
            case
            when link.is_a?(String)
              link_id = link
            when link.is_a?(Link)
              link_id = link.link_id
              link_obj = link
            else
              fail ArgumentError,
                   'Expect String(Link.link_id) or Link'
            end

            response = nw_if.del_link(link_id)

            status = response.status_code
            case status
            when Response::OK
              # Success: Nothing to do
              return nil
            when Response::NOT_FOUND
              fail NotFoundError.new(response), "#{status} Not Found: Link:#{link.inspect}"
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # POST OFPInPacket < InPacket
          # @param [OFPInPacket] in_packet
          # @return [OFPInPacket] Updated `in_packet`
          # @raise [RequestError]
          def postInPacket(in_packet, nw_if)
            check_param(in_packet, nw_if)
            response = nw_if.post_in_packet(in_packet)

            status = response.status_code
            case status
            when Response::OK
              in_packet = in_packet.class.new(response.body)
              return in_packet
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # PUT Flow
          # @param [OFPFlow, BasicFlow] flow
          # @return [OFPFlow, BasicFlow] Updated `flow`
          # @raise [NotFoundError, ConflictError, RequestError]
          def putFlow(flow, nw_if)
            check_param(flow, nw_if)
            curr_flow = nw_if.get_flow(flow.flow_id)
            unless curr_flow.nil?
              flow.version = curr_flow.version
            end
            response = nw_if.put_flow(flow)

            status = response.status_code
            case status
            when Response::OK
              # Success: update version, etc.
              flow = flow.class.new(response.body)
              return flow
            when Response::NOT_FOUND
              fail NotFoundError.new(response), "#{status} Not Found: Flow:#{flow.inspect}"
            when Response::CONFLICT
              fail ConflictError.new(response), "#{status} Request Conflict."
            else
              fail ResponseError.new(response), "#{status} Request Failed."
            end
          end

          # GET Flow
          # @param [String] flow_id
          # @return [OFPFlow, BasicFlow] Updated `flow`
          # @raise [NotFoundError, RequestError]
          def getFlow(flow_id, nw_if)
            check_param(flow_id, nw_if)
            resp_flow = nw_if.get_flow(flow_id)
            if resp_flow.nil?
              response = Response.new(Response::INTERNAL_SERVER_ERROR, 'error.')
              fail ResponseError.new(response), 'flow is nil'
            end
            resp_flow
          end

          def check_param(body, nw_if)
            if body.nil?
              response = Response.new(Response::INTERNAL_SERVER_ERROR, 'error.')
              fail ResponseError.new(response), 'RequetBody not exist.'
            end
            if nw_if.nil?
              response = Response.new(Response::INTERNAL_SERVER_ERROR, 'error.')
              fail ResponseError.new(response), 'NetworkInterface not exist.'
            end
          end
        end
      end
    end
  end
end
