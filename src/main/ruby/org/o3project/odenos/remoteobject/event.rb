
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
require 'odenos/core/util/object_hash'

module Odenos
  module Core
    class Event < Odenos::Util::ObjectArray
      array_accessor :object, :publisher_id,  0
      array_accessor :object, :event_type,    1
      array_accessor :object, :body,          2

      # @overload initialize(event_ary)
      #  @param [(String,String,#to_msgpack)] event_ary
      # @overload initialize(publisher_id, event_type, body)
      #  @param [String] publisher_id
      #  @param [String] event_type
      #  @param [#to_msgpack] body
      def initialize(*ary)
        case ary.length
        when 1
          fail ArgumentError, 'expect Array' unless ary.first.is_a?(Array)
          fail ArgumentError, 'expect Event Array(3)' if ary.first.length != 3

          super(ary.first)
        when 3
          super(ary)
        else
          fail ArgumentError, 'expect 1 or 3 arguments'
        end
      end
    end

    class ObjectChangeEventBase < Odenos::Util::TypedObjectHash
      #  [Symbol] One of :add, :delete, :update
      hash_accessor :object, :action
      hash_accessor :object, :prev
      hash_accessor :object, :curr

      def initialize(initial_hash = {})
        super
        if action.is_a? String
          case action
          when 'add'
            self.action = :add
          when 'delete'
            self.action = :delete
          when 'update'
            self.action = :update
          else
            fail ArgumentError, "Invalid action specified: #{action}"
          end
        end
      end
    end

    class ObjectPropertyChanged < ObjectChangeEventBase
      TYPE = 'ObjectPropertyChanged'

      def initialize(initial_hash = {})
        super
        if prev.is_a? Hash
          self.prev = ObjectProperty.new(prev)
        end
        if curr.is_a? Hash
          self.curr = ObjectProperty.new(curr)
        end
      end
    end

    class ObjectSettingsChanged < ObjectChangeEventBase
      TYPE = 'ObjectSettingsChanged'
    end

    class ComponentConnectionChanged < ObjectChangeEventBase
      TYPE = 'ComponentConnectionChanged'

      def initialize(initial_hash = {})
        super
        if prev.is_a? Hash
          self.prev = ComponentConnection.from_object_hash(prev)
        end
        if curr.is_a? Hash
          self.curr = ComponentConnection.from_object_hash(curr)
        end
      end
    end

    class ComponentManagerChanged < ObjectChangeEventBase
      TYPE = 'ComponentManagerChanged'

      def initialize(initial_hash = {})
        super
        if prev.is_a? Hash
          self.prev = ObjectProperty.new(prev)
        end
        if curr.is_a? Hash
          self.curr = ObjectProperty.new(curr)
        end
      end
    end

    class ComponentChanged < ObjectChangeEventBase
      TYPE = 'ComponentChanged'

      def initialize(initial_hash = {})
        super
        if prev.is_a? Hash
          self.prev = ObjectProperty.new(prev)
        end
        if curr.is_a? Hash
          self.curr = ObjectProperty.new(curr)
        end
      end
    end

    class EventSubscription < Odenos::Util::ObjectHash
      hash_accessor :object, :subscriber_id
      # RemoteObject.id => [EventType.event_type]
      hash_accessor :object, :event_filters

      def initialize(initial_hash = {})
        super(initial_hash)
        self.event_filters ||= {}
      end

      def clear_filter
        self.event_filters.clear
      end

      def add_filter(publisher_id, event_id)
        self.event_filters[publisher_id] ||= []
        self.event_filters[publisher_id] << event_id
      end

      def remove_filter(publisher_id, event_id)
        if self.event_filters.include? publisher_id
          self.event_filters[publisher_id].delete(event_id)
        end
      end

      def remove_publisher_id(publisher_id)
        self.event_filters.delete(publisher_id)
      end
    end
  end
end
