
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

require 'odenos'
require 'odenos/remoteobject'
require 'odenos/core/component'
require 'odenos/component/driver/of_driver/openflow_driver'

module Odenos
  module Manager
    class OFComponentManager < Odenos::Manager::ComponentManager
      # include Trema::DefaultLogger
      include Odenos::Core
      include Odenos::Component
      include Odenos::Component::Driver
      # @param [Trema::Controller] controller OpenFlowController
      def initialize(objectId, dispatcher, controller)
        # THE OpenFlowDriver component instance
        @driver_component = nil
        # The OpenFlowDriver Controller instance
        @controller = controller
        super(objectId, dispatcher)
      end

      ########################
      ## Override methods
      #######################

      def do_get_component_types
        comp_types = {}
        begin
          @component_classes.each do |type_name, clazz|
            comp_id = format('%s_%s', remote_object_id, type_name)
            component = nil
            if type_name == 'OpenFlowDriver'
              component = create_openflow_driver_component(comp_id)
            else
              component = clazz.new(comp_id, dispatcher)
            end
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

      # GET components
      def do_get_components
        if !@driver_component.nil?
          return Response.new(Core::Response::OK,
                              @driver_component.remote_object_id => @driver_component.property)
        else
          return Response.new(Core::Response::OK, {})
        end
      end

      # PUT component
      def do_put_component(obj_property, component_id)
        prop = ObjectProperty.new(obj_property)

        begin
          # check request body for type correctness
          if prop.remote_object_type != 'OpenFlowDriver'
            warn { "Request Error: Unsupported type #{prop.remote_object_type}" }
            return Response.new(Core::Response::FORBIDDEN, 'Unsupported type')
          end
          if @driver_component.nil?
            @driver_component = create_openflow_driver_component(component_id)
            @driver_component.set_state(ObjectProperty::State::RUNNING)
            @components[component_id] = @driver_component
            do_component_changed('add', nil, @driver_component.property)
            return Response.new(Core::Response::CREATED, @driver_component.property)
          end
        rescue => ex
          error "Receive Exception. #{ex.backtrace}"
          return Response.new(Response::INTERNAL_SERVER_ERROR, ex.backtrace)
        end

        warn { 'Request Error: Cannot overwrite existing instance.' }
        Response.new(Core::Response::CONFLICT,
                     'Cannot overwrite existing instance.')
      end

      # DELETE component
      def do_delete_component(component_id)
        resp = Response.new(
        Core::Response::NOT_FOUND, "#{component_id} Not Found")
        if @driver_component.nil?
          return resp
        end
        if component_id != @driver_component.remote_object_id
          return resp
        end
        prev = @driver_component.property
        @driver_component.on_finalize
        @driver_component = nil
        @components.delete component_id
        do_component_changed('delete', prev, nil)
        Response.new(Core::Response::OK, nil)
      end

      ########################
      ## Custom methods
      #######################
      def create_openflow_driver_component(component_id)
        debug { "create_openflow_driver_component( #{component_id} )" }
        # create instance
        OFDriver::OpenFlowDriver.new(component_id, @dispatcher, @controller)
      end
    end
  end
end
