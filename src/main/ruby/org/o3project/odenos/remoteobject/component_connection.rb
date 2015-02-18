
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

require 'odenos/core/util'

module Odenos
  module Core
    class ComponentConnection < Odenos::Util::TypedObjectHash
      hash_accessor :object, :connection_type, :state

      def component_connection_id
        @object[:id]
      end

      def component_connection_id=(v)
        @object[:id] = v
      end

      module State
        INITIALIZING = 'initializing'
        RUNNING = 'running'
        FINALIZING = 'finalizing'
        ERROR = 'error'
        NONE = 'none'
      end

      def initialize(initial_hash = {})
        super(initial_hash)
      end
    end
  end
end
