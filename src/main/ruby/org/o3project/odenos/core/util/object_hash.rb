
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

require 'odenos/core/util/hash_accessor'
require 'odenos/core/util/meta_helper'
require 'msgpack'

module Odenos
  module Util
    class ObjectHash
      extend Odenos::Util::HashAccessor

      # Create ObjectHash instance by given Hash.
      # @note String keys will be converted to Symbol keys
      def initialize(initial_hash = {})
        # convert String keys to Symbol
        @object = {}
        initial_hash.each_pair do | key, value |
          if key.is_a?(String)
            @object[key.to_sym] = value
          else
            @object[key] = value
          end
        end
      end

      def to_hash
        @object
      end
      alias_method :to_packable, :to_hash

      # @return [String] serialized object
      def to_msgpack(io = nil)
        if io
          to_hash.to_msgpack(io)
        else
          to_hash.to_msgpack
        end
      end

      # @param [IO,String] arg IO or String which contains serialized ObjectHash
      # @return [ObjectHash] deserialized object
      def self.from_msgpack(arg)
        hash = MessagePack.unpack(arg)
        new(hash)
      end

      def pack
        to_msgpack
      end

      def self.unpack(packed)
        from_msgpack(packed)
      end
    end

    module OdenosVersion
      extend Odenos::Util::HashAccessor

      def initialize(initial_hash = {})
        super
      end

      hash_accessor :object, :version

      # delete :version from object hash
      def delete_version
        @object.delete(:version)
      end
    end

    class VersionedObjectHash < ObjectHash
      include OdenosVersion
    end

    #
    # module to make ObjectHash element :type accessible as :odenos_type
    # Uses unqualified class name as :type if not specified
    #
    module OdenosType
      def initialize(initial_hash = {})
        super
        @object[:type] ||= self.class.to_s.split('::').last
      end

      def odenos_type
        @object[:type]
      end
    end

    class TypedObjectHash < ObjectHash
      include OdenosType

      def self.from_object_hash(hash)
        fail TypeError, 'Only Hash allowed' unless hash.is_a? Hash
        p_mod = MetaHelper.parent_module(self)
        type_str = hash['type'] || hash[:type]
        match_class = p_mod.const_get(type_str)
        fail TypeError, "Not a subclass of #{self}" unless match_class <= self
        match_class.new(hash)
      end
    end

    module Attributes
      extend Odenos::Util::HashAccessor

      hash_accessor :object, :attributes

      def initialize(initial_hash = {})
        super
        self.attributes ||= {}
      end
    end
  end
end
