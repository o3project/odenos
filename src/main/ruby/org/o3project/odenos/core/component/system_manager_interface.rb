
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
require 'odenos/core/util/logger'

module Odenos
  module Component
    class SystemManagerInterface
      include Odenos::Core
      include Odenos::Util::Logger

      COMP_MNGS_PATH = 'component_managers'
      COMP_MNG_PATH = 'component_managers/%s'
      EVENT_MNG_PATH = 'event_manager'
      COMP_TYPES_PATH = 'component_types'
      COMP_TYPE_PATH = 'component_types/%s'
      COMPS_PATH = 'components'
      COMP_PATH = 'components/%s'
      CONNECTIONS_PATH = 'connections'
      CONNECTION_PATH = 'connections/%s'
      OBJECT_PATH = 'objects/%s'

      def initialize(dispatcher)
        @dispatcher = dispatcher
        @sysmgr_id = dispatcher.system_manager_id
        logger_ident_initialize('SystemManagerInterface')
      end

      ####################
      # request method
      ####################

      # GET Component Managers.
      def get_component_managers
        debug 'GET Component Managers.'
        path = COMP_MNGS_PATH
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          comp_mgrs = {}
          resp.body.each do |prop|
            objp = ObjectProperty.new(prop)
            comp_mgrs[objp.remote_object_id] = objp
          end
          return comp_mgrs
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Event Manager.
      def get_event_manager
        debug 'GET Event Manager.'
        path = EVENT_MNG_PATH
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return ObjectProperty.new(resp.body)
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET ComponentTypes.
      def get_component_types
        debug 'GET ComponentTypes.'
        path = COMP_TYPES_PATH
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          comp_types = {}
          resp.body.each do |type, list|
            obj_list = []
            list.each do |prop|
              obj_list.push(ObjectProperty.new(prop))
            end
            comp_types[type] = obj_list
          end
          return comp_types
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Components.
      def get_components
        debug 'GET Components.'
        path = COMPS_PATH
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          comps = {}
          resp.body.each do |id, prop|
            objp = ObjectProperty.new(prop)
            comps[id] = objp
          end
          return comps
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Connections.
      def get_connections
        debug 'GET Connections.'
        path = CONNECTIONS_PATH
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          conns = {}
          resp.body.each do |id, conn|
            conn_prop = nil
            if conn[:type] == 'LogicAndNetwork'
              conn_prop = LogicAndNetwork.new(conn)
            else
              conn_prop = ComponentConnection.new(conn)
            end
            conns[id] = conn_prop
          end
          return conns
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Component Manager.
      def get_component_manager(comp_mgr_id)
        debug 'GET Component Manager.'
        path = COMP_MNG_PATH % comp_mgr_id
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return ObjectProperty.new(resp.body)
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET ComponentType.
      def get_component_type(comp_type)
        debug 'GET ComponentType.'
        path = COMP_TYPE_PATH % comp_type
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          obj_list = []
          resp.body.each do |prop|
            obj_list.push(ObjectProperty.new(prop))
          end
          return obj_list
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Component.
      def get_component(comp_id)
        debug 'GET Component.'
        path = COMP_PATH % comp_id
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return ObjectProperty.new(resp.body)
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Connection.
      def get_connection(conn_id)
        debug 'GET Connection.'
        path = CONNECTION_PATH % conn_id
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          conn_prop = nil
          if resp.body[:type] == 'LogicAndNetwork'
            conn_prop = LogicAndNetwork.new(resp.body)
          else
            conn_prop = ComponentConnection.new(resp.body)
          end
          return conn_prop
        rescue => ex
          return dump_error(ex)
        end
      end

      # GET Object.
      def get_object(object_id)
        debug 'GET Object.'
        path = OBJECT_PATH % object_id
        resp = get_object_to_sysmgr(path)
        unless resp.status_code == Response::OK
          return nil
        end
        begin
          return ObjectProperty.new(resp.body)
        rescue => ex
          return dump_error(ex)
        end
      end

      # PUT Connection.
      def put_connection(connection)
        debug 'PUT Connection.'
        path = CONNECTION_PATH % connection.component_connection_id
        put_object_to_sysmgr(path, connection)
      end

      # PUT ComponentManagers.
      def put_component_managers(property)
        debug 'PUT ComponentManagers.'
        path = COMP_MNG_PATH % property.remote_object_id
        put_object_to_sysmgr(path, property)
      end

      # PUT Components.
      def put_components(property)
        debug 'PUT Components.'
        path = COMP_PATH % property.remote_object_id
        put_object_to_sysmgr(path, property)
      end

      # POST Components.
      def post_components(property)
        debug 'POST Components.'
        path = COMPS_PATH
        post_object_to_sysmgr(path, property)
      end

      # POST Connections.
      def post_connections(connection)
        debug 'POST Components.'
        path = CONNECTIONS_PATH
        post_object_to_sysmgr(path, connection)
      end

      # DELETE ComponentManagers.
      def del_component_managers(comp_mgr_id)
        debug 'DELETE ComponentManagers.'
        path = COMP_MNG_PATH % comp_mgr_id
        del_object_to_sysmgr(path)
      end

      # DELETE Components.
      def del_components(comp_id)
        debug 'DELETE Components.'
        path = COMP_PATH % comp_id
        del_object_to_sysmgr(path)
      end

      # DELETE Connections.
      def del_connections(conn_id)
        debug 'DELETE Connections.'
        path = CONNECTION_PATH % conn_id
        del_object_to_sysmgr(path)
      end

      ####################
      # common method
      ####################

      def post_object_to_sysmgr(path, body)
        debug ">> sysmgr_id: #{@sysmgr_id} path: #{path}"
        send_request(@sysmgr_id, :POST, path, body)
      end

      def put_object_to_sysmgr(path, body)
        debug ">> sysmgr_id: #{@sysmgr_id} path: #{path}"
        send_request(@sysmgr_id, :PUT, path, body)
      end

      def del_object_to_sysmgr(path)
        debug ">> sysmgr_id: #{@sysmgr_id} path: #{path}"
        send_request(@sysmgr_id, :DELETE, path, nil)
      end

      def get_object_to_sysmgr(path)
        debug ">> sysmgr_id: #{@sysmgr_id} path: #{path}"
        send_request(@sysmgr_id, :GET, path, nil)
      end

      ####################
      # private method
      ####################

      private

      def dump_error(ex)
        error 'Exception: Get Invalid Message'
        error " #{ex.message} #{ex.backtrace}"
        nil
      end

      def send_request(obj_id, method, path, body)
        resp = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
        req = Request.new(obj_id, method, path, body)
        begin
          resp = @dispatcher.request_sync(req)
        rescue => ex
          error "Exception: Request to #{obj_id} Method:#{method} Path:#{path}"
          error " #{ex.message} #{ex.backtrace}"
        end
        resp
      end
    end
  end
end
