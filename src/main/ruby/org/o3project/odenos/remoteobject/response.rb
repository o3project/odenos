
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
    class Response < Odenos::Util::ObjectArray
      OK = 200
      CREATED = 201
      ACCEPTED = 202
      BAD_REQUEST = 400
      FORBIDDEN = 403
      NOT_FOUND = 404
      METHOD_NOT_ALLOWED = 405
      CONFLICT = 409
      INTERNAL_SERVER_ERROR = 500

      array_accessor :object, :status_code,  0
      array_accessor :object, :request_body, 1

      alias_method :body, :request_body

      # @overload initialize(response_ary)
      #  @param [(Integer,#to_msgpack)] response_ary
      # @overload initialize(status_code, body)
      #  @param [Integer] status_code
      #  @param [#to_msgpack] body
      def initialize(*ary)
        case ary.length
        when 1
          unless ary.first.is_a?(Array)
            fail ArgumentError, 'expect Array'
          end
          if ary.first.length != 2
            fail ArgumentError, "expect Response Array(2):#{ary.first.inspect}"
          end
          super(ary.first)
        when 2
          super(ary)
        else
          fail ArgumentError, 'expect 1 or 2 arguments'
        end
        unless status_code.is_a?(Integer)
          fail ArgumentError, 'status_code is a Integer'
        end
      end
    end
  end
end
