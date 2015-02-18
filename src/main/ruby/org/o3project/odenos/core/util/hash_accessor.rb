
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
  module Util
    # Define accessor which refer to a Hash defined as an instance variable
    # @example
    #   class SomeClass
    #     extend Odenos::Util::HashAccessor
    #     def initialize
    #       @hash = Hash.new
    #     end
    #     hash_accessor :hash, :alpha, :beta
    #   end
    #
    #   # Above is equivalent to
    #   class SomeClass
    #     def initialize
    #       @hash = Hash.new
    #     end
    #     def alpha
    #       @hash[:alpha]
    #     end
    #     def alpha= v
    #       @hash[:alpha] = v
    #     end
    #     def beta
    #       @hash[:beta]
    #     end
    #     def beta= v
    #       @hash[:beta] = v
    #     end
    #   end
    module HashAccessor
      private

      # @param [Symbol] hash instance variable name of Hash
      # @param [Symbol,...] keys list of key name
      def hash_reader(hash, *keys)
        keys.each do |key|
          define_method(key)  do
            instance_variable_get("@#{hash}")[key]
          end
        end
      end

      # @param [Symbol] hash instance variable name of Hash
      # @param [Symbol,...] keys list of key name
      def hash_writer(hash, *keys)
        keys.each do |key|
          define_method("#{key}=")  do |value|
            instance_variable_get("@#{hash}")[key] = value
          end
        end
      end

      # @param [Symbol] hash instance variable name of Hash
      # @param [Symbol,...] keys list of key name
      def hash_accessor(hash, *keys)
        hash_reader hash, *keys
        hash_writer hash, *keys
      end
    end
  end
end
