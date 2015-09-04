
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

require 'thread'
require 'msgpack'
require 'timeout'
require 'odenos/remoteobject/message_dispatcher'
require 'odenos/core/util/logger'

module Odenos
  module Core
    # @abstract
    class BaseMessageTransport
      include Odenos::Util::Logger

      # @abstract
      # @param [Request] _request
      def send_request_message(_request)
        fail NotImplementedError
      end

      # @abstract
      def close
        fail NotImplementedError
      end

      # @param [String] remote_object_id
      def initialize(remote_object_id)
        @remote_object_id = remote_object_id
        logger_ident_initialize(remote_object_id)
      end
    end

    class RemoteMessageTransport < BaseMessageTransport
      FIXNUM_MAX = (2**(0.size * 8 - 2) - 1)
      FIXNUM_MIN = -(2**(0.size * 8 - 2))
      RESPONSE_TIMEOUT = 3

      def send_request_message(request)
        que = add_request request
        response = nil
        begin
          Timeout.timeout(3 * 60) do
            response = que.pop
          end
        rescue
          raise
        end
        response
      end

      def close
      end

      # @param [String] remote_object_id
      def initialize(remote_object_id, dispatcher)
        debug { "RemoteMessageTransport#initialize(#{remote_object_id})" }
        super(remote_object_id)
        @dispatcher = dispatcher
        @lockseq = Mutex.new
        @response_map = {}
        @seqno = 0
      end

      def signal_response(sno, response)
        return unless @response_map.key?(sno)
        queue = @response_map.delete(sno)
        queue.push(response)
      end

      protected

      def get_and_increment
        @lockseq.synchronize do
          ret = @seqno
          @seqno = FIXNUM_MIN if @seqno == FIXNUM_MAX
          @seqno += 1
          return ret
        end
      end

      def add_request(request)
        sno = get_and_increment
        que = Queue.new
        @response_map[sno] = que
        packer = MessagePack::Packer.new
        packer.write(MessageDispatcher::TYPE_REQUEST)
        packer.write(sno)
        packer.write(@dispatcher.get_source_dispatcher_id)
        packer.write(request)
        packer.flush
        @dispatcher.push_publish_queue(self, MessageDispatcher::TYPE_RESPONSE,
                                       sno, request.remote_object_id,
                                       packer.to_s)
        que
      end
    end

    class LocalMessageTransport < BaseMessageTransport
      def send_request_message(request)
        # deep copy
        request_copy = Request.unpack(request.pack)
        response = @dispatcher.dispatch_request request_copy
        response_copy = Response.unpack(response.pack)
        response_copy
      end

      def close
      end

      # @param [String] remote_object_id
      def initialize(remote_object_id, dispatcher)
        super(remote_object_id)
        @dispatcher = dispatcher
      end
    end
  end
end
