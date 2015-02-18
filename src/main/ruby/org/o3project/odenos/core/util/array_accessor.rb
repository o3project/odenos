
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
    # Define accessor which refer to an Array defined as an instance variable
    # @example
    #   class SomeClass
    #     extend Odenos::Util::ArrayAccessor
    #     def initialize
    #       @array = Array.new(2)
    #     end
    #     array_accessor :array, :alpha, 0, :beta, 1
    #   end
    #
    #   # Above is equivalent to
    #   class SomeClass
    #     def initialize
    #       @array = Array.new(2)
    #     end
    #     def alpha
    #       @hash[0]
    #     end
    #     def alpha= v
    #       @hash[0] = v
    #     end
    #     def beta
    #       @hash[1]
    #     end
    #     def beta= v
    #       @hash[1] = v
    #     end
    #   end
    module ArrayAccessor
      private

      # @overload array_reader(array, name, index)
      #  @param [Symbol] array instance variable name of Array
      #  @param [Symbol] name attribute name
      #  @param [Integer] index `array` index
      #  @note Multiple name, index pairs can follow.
      def array_reader(array, *args)
        args.each_slice(2) do |pair|
          if pair.length != 2
            fail ArgumentError, 'Unexpected number of arguments'
          end
          name, index = pair

          define_method(name)  do
            instance_variable_get("@#{array}")[index]
          end
        end
      end

      # @overload array_writer(array, name, index)
      #  @param [Symbol] array instance variable name of Array
      #  @param [Symbol] name attribute name
      #  @param [Integer] index `array` index
      #  @note Multiple name, index pairs can follow.
      def array_writer(array, *args)
        args.each_slice(2) do |pair|
          if pair.length != 2
            fail ArgumentError, 'Unexpected number of arguments'
          end
          name, index = pair

          define_method("#{name}=")  do |value|
            instance_variable_get("@#{array}")[index] = value
          end
        end
      end

      # @overload array_accessor(array, name, index)
      #  @param [Symbol] array instance variable name of Array
      #  @param [Symbol] name attribute name
      #  @param [Integer] index `array` index
      #  @note Multiple name, index pairs can follow.
      def array_accessor(array, *args)
        array_reader array, *args
        array_writer array, *args
      end
    end
  end
end
