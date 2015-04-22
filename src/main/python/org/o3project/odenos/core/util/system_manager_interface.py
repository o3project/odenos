# -*- coding:utf-8 -*-

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

import logging

from org.o3project.odenos.core.util.remote_object_interface import RemoteObjectInterface
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.manager.system.component_connection import (
    ComponentConnection
)
from org.o3project.odenos.remoteobject.manager.system.\
    component_connection_logic_and_network import (
        ComponentConnectionLogicAndNetwork)
from org.o3project.odenos.remoteobject.object_property import ObjectProperty


# pylint: disable=R0923
class SystemManagerInterface(RemoteObjectInterface):
    COMP_MNGS_PATH = "component_managers"
    COMP_MNG_PATH = "component_managers/%s"
    EVENT_MNG_PATH = "event_manager"
    COMP_TYPES_PATH = "component_types"
    COMP_TYPE_PATH = "component_types/%s"
    COMPS_PATH = "components"
    COMP_PATH = "components/%s"
    CONNECTIONS_PATH = "connections"
    CONNECTION_PATH = "connections/%s"
    OBJECT_PATH = "objects/%s"

    def __init__(self, dispatcher, source_object_id=None):
        '''
        NOTE: source_object_id is required for the ODENOS monitor tool.
        '''
        logging.debug("Create SystemManagerInterface ID:"
                      + dispatcher.system_manager_id)
        super(SystemManagerInterface, self).__init__(
            dispatcher,
            dispatcher.system_manager_id,
            source_object_id)

    @property
    def system_manager_id(self):
        return self.object_id

    ###################################
    # Basic request
    ###################################
    # GET Component Managers.
    def get_component_managers(self):
        logging.debug("GET ComponentManagers")
        resp = self._get_object_to_remote_object(self.COMP_MNGS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Event Manager.
    def get_event_manager(self):
        logging.debug("GET EventManager")
        resp = self._get_object_to_remote_object(self.EVENT_MNG_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET ComponentTypes.
    def get_component_types(self):
        logging.debug("GET ComponentTypes")
        resp = self._get_object_to_remote_object(self.COMP_TYPES_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Components.
    def get_components(self):
        logging.debug("GET Components")
        resp = self._get_object_to_remote_object(self.COMPS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Connections.
    def get_connections(self):
        logging.debug("GET Connections")
        resp = self._get_object_to_remote_object(self.CONNECTIONS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        connections = {}
        try:
            for conn_id, connection in resp.body.items():
                if connection[ComponentConnection.OBJECT_TYPE] ==\
                   ComponentConnectionLogicAndNetwork.TYPE:
                    connections[conn_id] =\
                        ComponentConnectionLogicAndNetwork.create_from_packed(
                            connection)
                else:
                    connections[conn_id] =\
                        ComponentConnection.create_from_packed(connection)
        except KeyError, err:
            logging.error("GET Connections Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return connections

    # GET Component Manager.
    def get_component_manager(self, comp_mgr_id):
        logging.debug("GET ComponentManager ComponentMgrID:" + comp_mgr_id)
        path = self.COMP_MNG_PATH % comp_mgr_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    def add_component_manager(self, compmgr):
        logging.debug("object_property of ComponentManager %s is %s",
                      compmgr.object_id,
                      compmgr.object_property.packed_object)

        path = "component_managers/%s" % compmgr.object_id
        resp = self._put_object_to_remote_object(path, compmgr.object_property)
        if resp.is_error(Request.Method.PUT):
            logging.error("Failed registration to SystemManager.")
            compmgr.set_state(ObjectProperty.State.ERROR)
            return
        logging.info("Complete ComponentManager registration to SystemManager.")

    # GET ComponentType.
    def get_component_type(self, comp_type):
        logging.debug("GET ComponentType Type:" + comp_type)
        path = self.COMP_TYPE_PATH % comp_type
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Component.
    def get_component(self, comp_id):
        logging.debug("GET Component ComponentID:" + comp_id)
        path = self.COMP_PATH % comp_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Connection.
    def get_connection(self, conn_id):
        logging.debug("GET Connection ConnectionID:" + conn_id)
        path = self.CONNECTION_PATH % conn_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        connection = None
        try:
            if resp.body[ComponentConnection.OBJECT_TYPE] ==\
               ComponentConnectionLogicAndNetwork.TYPE:
                connection =\
                    ComponentConnectionLogicAndNetwork.create_from_packed(
                        resp.body)
            else:
                connection =\
                    ComponentConnection.create_from_packed(resp.body)
        except KeyError, err:
            logging.error("GET Connection Invalid Response Message"
                          + " KeyError: " + str(err))
            return None

        return connection

    # GET Object.
    def get_object(self, object_id):
        logging.debug("GET Object ObjectID:" + object_id)
        path = self.OBJECT_PATH % object_id
        resp = self._get_object_to_remote_object(path)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # PUT Connection.
    def put_connection(self, connection):
        logging.debug("PUT Connection ConnectionID:" + connection.id)
        path = self.CONNECTION_PATH % connection.id
        return self._put_object_to_remote_object(path,
                                                 connection)

    # PUT ComponentManagers.
    def put_component_managers(self, property_):
        logging.debug("PUT ComponentManagers")
        path = self.COMP_MNG_PATH % property_.object_id
        return self._put_object_to_remote_object(path,
                                                 property_)

    # PUT Components.
    def put_components(self, property_):
        logging.debug("PUT Components")
        path = self.COMP_PATH % property_.object_id
        return self._put_object_to_remote_object(path,
                                                 property_)

    # POST Components.
    def post_components(self, property_):
        logging.debug("POST Components")
        return self._post_object_to_remote_object(self.COMPS_PATH,
                                                  property_)

    # POST Connections.
    def post_connections(self, connection):
        logging.debug("POST Connections")
        return self._post_object_to_remote_object(self.CONNECTIONS_PATH,
                                                  connection)

    # DELETE ComponentManagers.
    def del_component_managers(self, comp_mgr_id):
        logging.debug("DELETE ComponentManagers ComponentMgrID:" + comp_mgr_id)
        path = self.COMP_MNG_PATH % comp_mgr_id
        return self._del_object_to_remote_object(path)

    # DELETE Components.
    def del_components(self, comp_id):
        logging.debug("DELETE Components ComponentID:" + comp_id)
        path = self.COMP_PATH % comp_id
        return self._del_object_to_remote_object(path)

    # DELETE Components.
    def del_connections(self, conn_id):
        logging.debug("DELETE Connections ConnectionID:" + conn_id)
        path = self.CONNECTION_PATH % conn_id
        return self._del_object_to_remote_object(path)
