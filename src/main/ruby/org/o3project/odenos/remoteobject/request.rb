
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

require 'odenos/core/util/object_array'

module Odenos
  module Core
    class Request < Odenos::Util::ObjectArray
      array_accessor :object, :remote_object_id,      0
      array_accessor :object, :request_method, 1
      array_accessor :object, :path,           2
      array_accessor :object, :request_body,   3

      alias_method :body, :request_body

      # @overload initialize(request_ary)
      #  @param [(String,Symbol or String,String,#to_msgpack)] request_ary
      # @overload initialize(objectId, method, path, body)
      #  @param [String] objectId
      #  @param [Symbol] method One of :GET, :PUT, :POST, :DELETE
      #  @param [String] path
      #  @param [#to_msgpack] body
      def initialize(*ary)
        case ary.length
        when 1
          unless ary.first.is_a?(Array)
            fail ArgumentError, 'expect Array'
          end
          if ary.first.length != 4
            fail ArgumentError, 'expect Request Array(4)'
          end

          super(ary.first)
          # convert to Symbol
          if request_method.is_a? String
            case request_method
            when 'GET'
              self.request_method = :GET
            when 'PUT'
              self.request_method = :PUT
            when 'POST'
              self.request_method = :POST
            when 'DELETE'
              self.request_method = :DELETE
            else
              fail ArgumentError, "Invalid method: #{request_method}"
            end
          end
        when 4
          super(ary)
        else
          fail ArgumentError, 'expect 1 or 4 arguments'
        end
        unless remote_object_id.is_a?(String)
          fail ArgumentError, 'remote_object_id is a String'
        end
        unless request_method.is_a?(Symbol)
          fail ArgumentError, 'request_method is a Symbol'
        end
        unless path.is_a?(String)
          fail ArgumentError, 'path is a String'
        end
      end
    end
  end
end
