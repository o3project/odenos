
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
require 'odenos'

module Odenos
  module Component
    module Driver
      module  OFDriver
        #
        # Class to hold flow entry setup status
        # * status description:
        #   * `:setup` : flow_mod_add sent, waiting for result
        #   * `:valid` : flow entry active on switch.
        #   * `:teardown` : flow_mod_delete sent, waiting for result
        #   * `:none` : temporal status for teardown complete.
        class FlowEntry < Odenos::Util::ObjectHash
          hash_accessor :object, :dpid, :match, :actions, :status
          # @return [Symbol] One of :establishing, :established, :teardown, :none, :failed
          #
          # FlowEntry
          # @param [Hash] options
          # @option options [Integer] :dpid datapath id
          # @option options [Trema::Match] :match match condition
          # @option options [Array<Trema::Action>] :actions actions
          #
          def initialize(options = { match: Match.new, actions: [], status: status })
            super
          end

          # Equality by dpid, match, and actions
          def ==(rhs)
            (
            (dpid == rhs.dpid) &&
            (match.to_s == rhs.match.to_s) &&
            (actions.map(&:to_s) == rhs.actions.map(&:to_s)) &&
            (status == rhs.status)
            )
          end
        end
      end
    end
  end
end
