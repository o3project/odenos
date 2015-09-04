
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

require 'odenos/core/util/object_hash'
require 'odenos/remoteobject/event'
require 'odenos/remoteobject/request'
require 'odenos/remoteobject/response'
require 'odenos/core/util/logger'
require 'odenos/core/util/request_parser'

module Odenos
  module Core
    class ObjectProperty < Odenos::Util::ObjectHash
      ID = 'id'
      SUPER_TYPE = 'super_type'
      TYPE = 'type'
      STATE = 'state'
      DESCRIPTION = 'description'
      COMPONENT_TYPES = 'component_types'
      CONNECTION_TYPES = 'connection_types'

      hash_accessor :object, :state, :super_type
      hash_accessor :object, :description, :component_types, :connection_types

      def remote_object_type
        @object[:type]
      end

      def remote_object_type=(v)
        @object[:type] = v
      end

      def remote_object_id
        @object[:id]
      end

      def remote_object_id=(v)
        @object[:id] = v
      end

      alias_method :get_state, :state
      alias_method :set_state, :state=

      module State
        INITIALIZING = 'initializing'
        RUNNING = 'running'
        FINALIZING = 'finalizing'
        ERROR = 'error'
      end

      # @param [String,Symbol] key
      # @return old_value
      def set_property(key, value)
        key = key.to_sym if key.is_a? String

        if read_only?(key)
          return nil
        else
          old_value = @object[key]
          @object[key] = value
          return old_value
        end
      end

      # @param [String,Symbol] key
      def get_property(key)
        key = key.to_sym if key.is_a? String

        @object[key]
      end

      def delete_property(key)
        key = key.to_sym if key.is_a? String

        return if read_only?(key)
        @object.delete(key)
      end

      # @param [Hash] new_prop
      def put_property(new_prop)
        old_prop = clone
        # delete
        old_prop.to_hash.each_key do |key|
          unless new_prop.include?(key)
            delete_property(key)
          end
        end

        # add or update
        new_prop.each do |key, value|
          set_property(key, value)
        end
      end

      def initialize(initial_hash = {})
        super
      end

      # @param [Hash,ObjectProperty] other
      def ==(other)
        prop = {}
        if other.is_a? ObjectProperty
          prop = other.to_hash
        else
          other.each do |key, value|
            key = key.to_sym if key.is_a? String
            prop[key] = value
          end
        end
        @object == prop
      end

      def clone
        obj = ObjectProperty.new
        obj.remote_object_type = remote_object_type
        obj.remote_object_id = remote_object_id
        obj.super_type = super_type
        obj.description = description
        obj.connection_types = connection_types
        @object.each do |key, value|
          obj.set_property(key, value)
        end
        obj
      end

      def read_only?(key)
        if [:type, :id, :super_type, :description, :connection_types].include?(key)
          return true
        end
        false
      end
    end

    class RemoteObject
      include Odenos::Util
      include Odenos::Util::Logger

      # @return [EventSubscription]
      attr_accessor :event_subscription

      # @return [ObjectProperty]
      attr_accessor :property

      # @return [Hash]
      attr_accessor :settings

      # @return [MessageDispatcher]
      attr_reader :dispatcher

      # @!attribute [r] remote_object_id
      #   @return [String] ODENOS object id
      def remote_object_id
        property.remote_object_id
      end

      # @!attribute [r] state
      def state
        property.state
      end

      def on_initialize
        true
      end

      def on_finalize
        prev = property.clone
        if property.state != ObjectProperty::State::FINALIZING
          property.state = ObjectProperty::State::FINALIZING
          on_property_changed('delete', prev, nil)
        end
        dispatcher.remove_local_object(self)
      end

      def set_state(next_state)
        if property.state == next_state
          return
        end
        prev = property.clone
        property.state = next_state
        on_property_changed('update', prev, property)
      end

      # @param [String] remote_object_id
      # @param [Symbol] method One of :GET, :PUT, :POST, :DELETE
      # @param [String] path
      # @param [#to_msgpack] body
      # @return [Response]
      def request_sync(remote_object_id, method, path, body)
        @dispatcher.request_sync(Request.new(remote_object_id,
                                             method, path, body))
      end

      # @param [String] remote_object_id
      # @param [Symbol] method One of :GET, :PUT, :POST, :DELETE
      # @param [String] path
      # @param [#to_msgpack] body
      # @return [Response]
      def request(remote_object_id, method, path, body)
        debug { "request(#{remote_object_id},#{method},#{path},#{body.inspect})" }
        request_sync(remote_object_id, method, path, body)
      end

      # @param [String] event_type
      # @param [#to_msgpack] body
      def publish_event_async(event_type, body)
        @dispatcher.publish_event_async(Event.new(remote_object_id,
                                                  event_type, body))
      end

      #
      # Subscribe events of other RemoteObjects.
      # Caller should configure `event_subscription` member to
      # specify publisher IDs and events
      # before calling this method.
      # @return [Response]
      def apply_event_subscription
        @dispatcher.subscribe_event(event_subscription)
      end

      # @return [void]
      def subscribe_event
        apply_event_subscription
      end

      # @param [Request] request
      def dispatch_request(request)
        debug { "dispatch_request( #{request.inspect} )" }
        resp = nil
        begin
          resp = @parser.action(request)
          if resp.status_code == Response::NOT_FOUND
            resp = on_request(request)
          end
        rescue => ex
          resp = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
          error "Exception: Receive Request : #{request.inspect}"
          error "#{ex.message} #{ex.backtrace}"
        end

        return resp
      ensure
        debug { "Exit dispatch_request( #{request.inspect} )" }
      end

      def dispatch_event(event)
        debug { "dispatch_event( #{event.inspect} )" }
        do_post_event(event)
      end

      def finalize?(old_state, new_state)
        if (old_state != ObjectProperty::State::FINALIZING)
          if (new_state == ObjectProperty::State::FINALIZING)
            return true
          end
        end
        false
      end

      def on_property_changed(action, prev, curr)
        publish_event_async(ObjectPropertyChanged::TYPE,
                            'action' => action,
                            'prev' => prev,
                            'curr' => curr)
      end

      def on_settings_changed(prev, curr)
        publish_event_async(ObjectSettingsChanged::TYPE,
                            'action' => 'update',
                            'prev' => prev,
                            'curr' => curr)
      end

      # @param [Request] request
      # @return [Response]
      def on_request(request)
        debug { "on_request( #{request.inspect} )" }
        Response.new(Response::BAD_REQUEST, '')
      end

      # @param [Event] _event
      # @return [void]
      def on_event(_event)
      end

      # @param [String] remote_object_id
      # @param [MessageDispatcher] dispatcher
      def initialize(remote_object_id, dispatcher)
        logger_ident_initialize(remote_object_id)
        debug "RemoteObject#initialize #{remote_object_id}"
        @property = ObjectProperty.new(type: self.class.to_s.split('::').last,
                                       id: remote_object_id)
        @property.set_state(ObjectProperty::State::INITIALIZING)
        @property.description = @description
        @property.connection_types = @connection_types
        @settings = {}
        @dispatcher = dispatcher
        @dispatcher.add_local_object self
        @event_subscription =
            EventSubscription.new(subscriber_id: remote_object_id)
        @parser = RequestParser.new
        add_rules
      end

      protected

      def add_rules
        rules = []
        rules.push(RequestParser::PATTERN => /^property$/,
                   RequestParser::METHOD => :GET,
                   RequestParser::FUNC => method(:do_get_property),
                   RequestParser::PARAMS => 0)
        rules.push(RequestParser::PATTERN => /^property$/,
                   RequestParser::METHOD => :PUT,
                   RequestParser::FUNC => method(:do_put_property),
                   RequestParser::PARAMS => 1)
        rules.push(RequestParser::PATTERN => /^settings$/,
                   RequestParser::METHOD => :GET,
                   RequestParser::FUNC => method(:do_get_settings),
                   RequestParser::PARAMS => 0)
        rules.push(RequestParser::PATTERN => /^settings$/,
                   RequestParser::METHOD => :PUT,
                   RequestParser::FUNC => method(:do_put_settings),
                   RequestParser::PARAMS => 1)
        @parser.add_rule(rules)
      end

      # @return [Response]
      def do_get_property
        Response.new(Response::OK, property)
      end

      # @param [Request] request
      # @return [Response]
      def do_put_property(request)
        # check state change
        request.body.each do |key, value|
          next if key != ObjectProperty::STATE

          old_state = property.get_state
          if self.finalize?(old_state, value)
            on_finalize
            return Response.new(Response::OK, property)
          end
        end

        # update property
        unless property == request.body
          prev = property.clone
          property.put_property(request.body)
          # publish ObjectPropertyChanged
          on_property_changed('update', prev, property)
        end

        Response.new(Response::OK, property)
      end

      # @return [Response]
      def do_get_settings
        Response.new(Response::OK, settings)
      end

      # @param [Request] request
      # @return [Response]
      def do_put_settings(request)
        if settings != request.body
          prev = settings.clone

          # delete
          prev.each_key do |key|
            unless request.body.include?(key)
              settings.delete(key)
            end
          end

          # add or update
          request.body.each do |key, value|
            settings[key] = value
          end

          # publish ObjectSettingChanged
          on_settings_changed(prev, settings)

        end
        Response.new(Response::OK, settings)
      end

      # @param [Event] event
      # @return [Response]
      def do_post_event(event)
        on_event(event)
        Response.new(Response::ACCEPTED, nil)
      end
    end
  end
end
