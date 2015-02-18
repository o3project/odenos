
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

require 'sync'
require 'thread'

module Odenos
  module Component
    module Driver
      module OFDriver
        class Transactions
          include Sync_m
          def initialize(seed = 0x00010000)
            super()
            initialize_transactions seed
          end

          def initialize_transactions(seed = 0x00010000)
            @transaction_id = seed
            @transaction_id_mutex ||= Mutex.new
            @transactions = {}
          end

          #########################
          # @!group transaction management
          #########################

          #
          # Get a new transaction ID.
          # @note Only lower 16bit is used as internal counter
          # @return [Integer] transaction id
          #
          def get_transaction_id
            @transaction_id_mutex.synchronize do
              counter_part = @transaction_id & 0xFFFF
              if counter_part == 0xFFFF
                # zero reset counter part
                @transaction_id ^= counter_part
              else
                @transaction_id += 1
              end
            end
            @transaction_id
          end

          #
          # @!method add_transaction(txid, entry)
          # @param [Integer] txid transaction id
          # @param [Hash] entry transaction info to register
          #
          def add_transaction(txid, entry)
            sync_synchronize(:EX) do
              @transactions.store(txid, entry)
            end
          end

          #
          # @!method lookup_transaction(txid)
          # @param [Integer] txid transaction id
          # return [Hash] registered transaction info
          #
          def lookup_transaction(txid)
            retval = nil
            sync_synchronize(:SH) do
              retval = @transactions[txid]
            end
            retval
          end

          #
          # @!method delete_transaction(txid)
          # @param [Integer] txid transaction id
          #
          def delete_transaction(txid)
            sync_synchronize(:EX) do
              @transactions.delete(txid)
            end
          end
        end
      end
    end
  end
end
