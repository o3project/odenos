
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

require 'odenos/core/util/array_accessor'
require 'msgpack'

module Odenos
  module Util
    class ObjectArray
      extend Odenos::Util::ArrayAccessor

      def initialize(initial_array = [])
        @object = initial_array
      end

      def to_a
        @object
      end
      alias_method :to_packable, :to_a

      # @return [String] serialized object
      def to_msgpack(io = nil)
        if io
          to_a.to_msgpack(io)
        else
          to_a.to_msgpack
        end
      end

      # @param [IO,String] arg IO or String
      #                    which contains serialized ObjectArray
      # @return [ObjectArray] deserialized object
      def self.from_msgpack(arg)
        new(MessagePack.unpack(arg))
      end

      def pack
        to_msgpack
      end

      def self.unpack(packed)
        from_msgpack(packed)
      end
    end
  end
end
