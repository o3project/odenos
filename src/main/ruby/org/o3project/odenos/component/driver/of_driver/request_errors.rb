
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

module Odenos
  module Component
    module Driver
      module  OFDriver
        module RequestErrors
          class RequestError < RuntimeError
          end

          # Connection to NetworkComponent not present
          class NoConnectionError < RequestError
          end

          class ResponseError < RequestError
            attr_reader :response
            def initialize(response)
              @response = response
            end
          end

          # 404
          class NotFoundError < ResponseError
          end

          # 409
          class ConflictError < ResponseError
          end
        end
      end
    end
  end
end
