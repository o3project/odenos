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
import copy
from urlparse import urlparse

from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.manager.component.event.component_changed import(
    ComponentChanged
)
from org.o3project.odenos.remoteobject.manager.component.component_type\
    import ComponentType
from org.o3project.odenos.remoteobject.manager.system.event.component_manager_changed\
    import ComponentManagerChanged
from org.o3project.odenos.core.util.request_parser import RequestParser


class ComponentManager(RemoteObject):

    # FIXME
    DESCRIPTION = "ComponentManager for python"

    def __init__(self, object_id, dispatcher):
        RemoteObject.__init__(self, object_id, dispatcher)
        self.component_classes = {}
        self.components = {}
        self.dispatcher = dispatcher
        self.__parser = RequestParser()
        self.__add_rules()
        self._object_property.set_state(ObjectProperty.State.RUNNING)

    def register_to_system_manager(self):
        logging.debug("object_property of ComponentManager %s is %s",
                      self.object_id,
                      self._object_property.packed_object())

        self.__register_component_managers()
        path = "component_managers/%s" % self.object_id
        resp = self._request(self.dispatcher.system_manager_id,
                             Request.Method.PUT,
                             path,
                             self._object_property)
        if resp.is_error(Request.Method.PUT):
            logging.error("Failed registration to SystemManager.")
            self._object_property.set_state(ObjectProperty.State.ERROR)
            return

        self.__register_event_manager()
        self.__subscribe_event()
        logging.info("Complete registration to SystemManager.")

    def __register_component_managers(self):
        resp = self._request(self.dispatcher.system_manager_id,
                             Request.Method.GET,
                             "component_managers",
                             None)
        if resp.is_error(Request.Method.GET):
            logging.error("Failed get component_managers from SystemManager.")
            self._object_property.set_state(ObjectProperty.State.ERROR)
            return

        for component_manager in resp.body:
            self.__register_other_component_manager(component_manager)

    def __register_other_component_manager(self, component_manager):
        object_id = component_manager[ObjectProperty.OBJECT_ID]
        if object_id == self.object_id:
            return

        logging.info("Register Other Component Manager ID:%s", object_id)
        self.dispatcher.add_remote_client(object_id)

    def __unregister_component_manager(self, object_id):
        self.dispatcher.remove_remote_client(object_id)

    def __register_event_manager(self):
        resp = self._request(self.dispatcher.system_manager_id,
                             Request.Method.GET,
                             "objects/%s" % self.dispatcher.event_manager_id,
                             None)
        if resp.is_error(Request.Method.GET):
            self._object_property.set_state(ObjectProperty.State.ERROR)
            return

        self.dispatcher.add_remote_client(self.dispatcher.event_manager_id)

    def __subscribe_event(self):
        self._event_subscription.add_filter(
            self.dispatcher.system_manager_id,
            ComponentManagerChanged.TYPE)
        self._apply_event_subscription()

    def register_component_type(self, component):
        component_name = component.__name__
        logging.info("Register Component Type:%s", component_name)

        self.component_classes[component_name] = component
        component_types = \
            self._object_property.get_property(ObjectProperty.COMPONENT_TYPES)
        if component_types:
            component_types += (",%s" % component_name)
        else:
            component_types = "%s" % component_name
        self._object_property.set_property(ObjectProperty.COMPONENT_TYPES,
                                           component_types)

    def __add_rules(self):
        rules = []
        rules.append({RequestParser.PATTERN: r"^component_types/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_component_types,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^components/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_components,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.PUT,
                      RequestParser.FUNC: self._do_put_component,
                      RequestParser.PARAMS: 2})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_component,
                      RequestParser.PARAMS: 1})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.DELETE,
                      RequestParser.FUNC: self._do_delete_component,
                      RequestParser.PARAMS: 1})
        self.__parser.add_rule(rules)

    def _on_request(self, request):
        return self.__parser.action(request)

    def _do_get_component(self, object_id):
        if object_id in self.components:
            return Response(Response.StatusCode.OK,
                            self.components[object_id].object_property)
        return Response(Response.StatusCode.NOT_FOUND, None)

    def _do_put_component(self, obj_prop, object_id):
        component_type = obj_prop[ObjectProperty.OBJECT_TYPE]

        if component_type not in self.component_classes:
            return Response(Response.StatusCode.BAD_REQUEST, None)
        elif object_id in self.components:
            return Response(Response.StatusCode.CONFLICT, None)

        component_class = self.component_classes[component_type]
        self.components[object_id] = component_class(object_id,
                                                     self.dispatcher)
        if self.components[object_id].on_initialize(obj_prop):
            self.components[object_id].\
                _object_property.set_state(ObjectProperty.State.RUNNING)
        else:
            self.components[object_id].\
                _object_property.set_state(ObjectProperty.State.ERROR)
        curr = self.components[object_id].object_property.packed_object()
        self._do_component_changed(ComponentChanged.Action.ADD,
                                   None,
                                   curr)

        logging.info("Created Component Type:%s ID:%s",
                     component_type, object_id)
        return Response(Response.StatusCode.CREATED,
                        self.components[object_id].object_property)

    def _do_delete_component(self, object_id):
        if object_id in self.components:
            component = self.components[object_id]
            prev = copy.deepcopy(component._object_property).packed_object()
            component.on_finalize()
            del self.components[object_id]
            self._do_component_changed(ComponentChanged.Action.DELETE,
                                       prev,
                                       None)
            logging.info("Deleted Component ID:%s", object_id)

        return Response(Response.StatusCode.OK, None)

    def _do_get_components(self):
        body = {}
        for object_id in self.components:
            body[object_id] = \
                self.components[object_id].object_property.packed_object()
        return Response(Response.StatusCode.OK, body)

    def _do_get_component_types(self):
        comp_types = {}
        try:
            for type_name, clazz in self.component_classes.items():
                comp_id = "%s_%s" % (self.object_id, type_name)
                component = clazz(comp_id, None)
                obj_prop = component.object_property

                type = obj_prop.get_property(ObjectProperty.OBJECT_TYPE)
                super_type = obj_prop.get_property(ObjectProperty.OBJECT_SUPER_TYPE)

                connection_types = {}
                connection_types_str = obj_prop.get_property(
                    ObjectProperty.CONNECTION_TYPES)
                conn_type_list = connection_types_str.split(",")
                for type_elem in conn_type_list:
                    type_elem_list = type_elem.split(":")
                    if len(type_elem_list) == 2:
                        connection_types[type_elem_list[0]] = type_elem_list[1]

                description = obj_prop.get_property(ObjectProperty.DESCRIPTION)

                target = ComponentType(type, super_type,
                                       connection_types, description)
                comp_types[type_name] = target.packed_object()

        except Exception, e:
            return Response(Response.StatusCode.INTERNAL_SERVER_ERROR,
                            str(e))

        return Response(Response.StatusCode.OK, comp_types)

    def _do_component_changed(self, action, prev, curr):
        body = {ComponentChanged.ACTION: action,
                ComponentChanged.PREV: prev,
                ComponentChanged.CURR: curr}
        self._publish_event_async(ComponentChanged.TYPE, body)

    def _do_event_componentmanagerchanged(self, event):
        msg = None
        try:
            msg = ComponentManagerChanged.create_from_packed(event.body)
        except KeyError, e:
            logging.error("Receive Invalid ComponentManagerChanged Message"
                          + "KeyError: " + str(e))
            return

        if msg.action == ComponentManagerChanged.Action.ADD:
            self.__register_other_component_manager(msg.curr)
        elif msg.action == ComponentManagerChanged.Action.DELETE:
            self.__unregister_component_manager(
                msg.prev[ObjectProperty.OBJECT_ID])
