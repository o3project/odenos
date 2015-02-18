
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

require 'odenos/remoteobject'
require 'odenos/core/util'

module Odenos
  module Manager
    class ComponentManager < Odenos::Core::RemoteObject
      include Odenos::Core
      include Odenos::Util
      def initialize(remote_object_id, dispatcher)
        @description = 'ComponentManager for ruby'
        @connection_types = ''
        super(remote_object_id, dispatcher)
        # type -> instance
        @component_classes = {}
        # id -> instance
        @components = {}

        @local_parser = RequestParser.new
        local_add_rules

        property.set_state(ObjectProperty::State::RUNNING)
      end

      def register_to_system_manager
        debug 'register_to_system_manager'
        register_component_managers
        path = 'component_managers/%s' % remote_object_id
        request(dispatcher.system_manager_id, :PUT, path, property)
        register_event_manager
        subscribe_event
      end

      def register_event_manager
        response = request(dispatcher.system_manager_id,
                           :GET,
                           "objects/#{dispatcher.event_manager_id}",
                           nil)
        if response.status_code != Response::OK
          fail 'Failed to Register Event Manager'
        end
        dispatcher.add_remote_client(dispatcher.event_manager_id)
      end

      def subscribe_event
        event_subscription.add_filter(dispatcher.system_manager_id,
                                      ComponentManagerChanged::TYPE)
        apply_event_subscription
      end

      def register_component_managers
        response = request(dispatcher.system_manager_id,
                           :GET, 'component_managers', nil)
        if response.status_code != Response::OK
          fail 'Failed to Register ComponentManagers'
        end
        response.body.each do |component_manager|
          register_component_manager(ObjectProperty.new(component_manager))
        end
      end

      def register_component_manager(component_manager)
        object_id = component_manager.remote_object_id
        if object_id == property.remote_object_id
          return
        end
        dispatcher.add_remote_client(object_id)
      end

      def unregister_component_manager(object_id)
        dispatcher.remove_remote_client(object_id)
      end

      # @param [Class] component Ruby Class of Component
      def register_component_type(component)
        component_type = MetaHelper.simple_class(component)
        unless @component_classes.include?(component_type)
          @component_classes[component_type] = component
          component_types = property.component_types
          if component_types.nil?
            component_types = component_type
          else
            component_types += ',' + component_type
          end
          property.component_types = component_types
        end
      end

      def local_add_rules
        rules = []
        rules.push(RequestParser::PATTERN => /^component_types$/,
                   RequestParser::METHOD => :GET,
                   RequestParser::FUNC => method(:do_get_component_types),
                   RequestParser::PARAMS => 0)
        rules.push(RequestParser::PATTERN => /^components$/,
                   RequestParser::METHOD => :GET,
                   RequestParser::FUNC => method(:do_get_components),
                   RequestParser::PARAMS => 0)
        rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
                   RequestParser::METHOD => :PUT,
                   RequestParser::FUNC => method(:do_put_component),
                   RequestParser::PARAMS => 2)
        rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
                   RequestParser::METHOD => :GET,
                   RequestParser::FUNC => method(:do_get_component),
                   RequestParser::PARAMS => 1)
        rules.push(RequestParser::PATTERN => /^components\/([^\/]+)$/,
                   RequestParser::METHOD => :DELETE,
                   RequestParser::FUNC => method(:do_delete_component),
                   RequestParser::PARAMS => 1)
        @local_parser.add_rule(rules)
      end

      # @param [Request] request
      # @return [Response]
      def on_request(request)
        debug { "on_request( #{request.to_a.inspect} )" }
        @local_parser.action(request)
      end

      def do_get_component_types
        comp_types = {}
        begin
          @component_classes.each do |type_name, clazz|
            comp_id = format('%s_%s', remote_object_id, type_name)
            component = clazz.new(comp_id, dispatcher)
            obj_prop = component.property

            type = obj_prop.get_property(ObjectProperty::TYPE) || ''
            super_type = obj_prop.get_property(ObjectProperty::SUPER_TYPE) || ''

            connection_types = {}
            connection_types_str = obj_prop.get_property(
              ObjectProperty::CONNECTION_TYPES) || ''
            conn_type_list = connection_types_str.split(',')
            conn_type_list.each do |type_elem|
              type_elem_list = type_elem.split(':')
              if type_elem_list.length == 2
                connection_types[type_elem_list[0]] = type_elem_list[1]
              end
            end

            description = obj_prop.get_property(ObjectProperty::DESCRIPTION) || ''

            comp_types[type_name] = {
              'type' => type, 'super_type' => super_type,
              'connection_types' => connection_types, 'description' => description }
          end
        rescue => ex
          error "Receive Exception. #{ex.backtrace}"
          return Response.new(Response::INTERNAL_SERVER_ERROR, ex.backtrace)
        end
        Response.new(Response::OK, comp_types)
      end

      def do_get_components
        component_property_map = {}
        @components.each_pair do |key, value|
          component_property_map[key] = value.property
        end
        Response.new(Response::OK, component_property_map)
      end

      def do_get_component(remote_object_id)
        if @components.include?(remote_object_id)
          return Response.new(Response::OK,
                              @components[remote_object_id].property)
        else
          return Response.new(Response::NOT_FOUND, nil)
        end
      end

      # @param [Hash] obj_property Hash representation of RemoteObject.property
      # @param [String] object_id RemoteObject.remote_object_id
      def do_put_component(obj_property, object_id)
        component_type = obj_property[ObjectProperty::TYPE]
        unless @component_classes.include?(component_type)
          return Response.new(Response::BAD_REQUEST, 'Error unknown type')
        end

        if @components.include?(object_id)
          return Response.new(Response::CONFLICT,
                              'Component is already created')
        end

        component_class = @component_classes[component_type]

        @components[object_id] = component_class.new(object_id, dispatcher)
        @components[object_id].set_state(ObjectProperty::State::RUNNING)
        do_component_changed('add', nil, @components[object_id].property)
        Response.new(Response::CREATED, @components[object_id].property)
      end

      def do_delete_component(remote_object_id)
        if @components.include? remote_object_id
          component = @components[remote_object_id]
          prev = component.property
          component.on_finalize
          @components.delete remote_object_id
          do_component_changed('delete', prev, nil)
        end
        Response.new(Response::OK, nil)
      end

      # @param [String] action String ComponentChangedEvent action kind
      # @param [Hash] prev Hash representation of Old RemoteObject.property
      # @param [Hash] curr Hash representation of New RemoteObject.property
      def do_component_changed(action, prev, curr)
        publish_event_async(ComponentChanged::TYPE,
                            'action' => action,
                            'prev' => prev,
                            'curr' => curr)
      end

      # override
      def on_event(event)
        debug "Receive Event: #{event.event_type}"
        if event.event_type == ComponentManagerChanged::TYPE
          begin
            msg = ComponentManagerChanged.new(event.body)
            on_component_manager_changed(msg)
          rescue => ex
            error 'Exception: Receive Invalid Event Message'
            error "#{ex.message} #{ex.backtrace}"
          end
        end
      end

      def on_component_manager_changed(msg)
        case msg.action
        when :add
          register_component_manager(msg.curr)
        when :delete
          unregister_component_manager(msg.prev.remote_object_id)
        end
      end
    end
  end
end
