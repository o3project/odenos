
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

require 'odenos/remoteobject/response'

module Odenos
  module Util
    class RequestParser
      PATTERN = 'pattern'
      METHOD = 'method'
      FUNC = 'func'
      PARAMS = 'params'

      def initialize
        @rules = []
      end

      # @example rule format
      #    [{"pattern": "<regex path>", "method": "<method>",
      #         "func": callable obj, "params": num of argument}, ...]
      def add_rule(rule)
        @rules += rule
      end

      def action(request)
        path_match = false

        @rules.each do |rule|
          unless rule[PATTERN] =~ request.path
            next
          end

          params = request.path.scan(rule[PATTERN])
          path_match = true
          unless rule[METHOD] == request.request_method
            next
          end

          if rule[PARAMS] == 0
            return rule[FUNC].call
          elsif rule[PARAMS] == 1
            if request.body.nil?
              return rule[FUNC].call(*params[0])
            else
              return rule[FUNC].call(request.body)
            end
          else
            if request.body.nil?
              return rule[FUNC].call(*params[0])
            else
              return rule[FUNC].call(request.body, *params[0])
            end
          end
        end

        if path_match
          return Odenos::Core::Response.new(
                   Odenos::Core::Response::METHOD_NOT_ALLOWED, nil)
        end

        Odenos::Core::Response.new(
                 Odenos::Core::Response::NOT_FOUND, nil)
      end
    end
  end
end
