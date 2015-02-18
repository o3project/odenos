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

from org.o3project.odenos.core.util.request_parser import RequestParser
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.remote_object_manager import RemoteObjectManager
from org.o3project.odenos.remoteobject.manager.component.component_type\
    import ComponentType


class ComponentManager(RemoteObjectManager):

    DESCRIPTION = "python's ComponentManager"
    COMPONENT_TYPES = "component_types"

    def __init__(self, object_id, dispatcher):
        RemoteObjectManager.__init__(self, object_id, dispatcher)
        self._object_property.set_property(ComponentManager.COMPONENT_TYPES, "")

    def register_components(self, components):
        self.register_remote_objects(components)
        types = ",".join(self.remote_object_classes.keys())
        self._object_property.set_property(ComponentManager.COMPONENT_TYPES,
                                           types)

    def _add_rules(self):
        rules = []
        rules.append({RequestParser.PATTERN: r"^component_types/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_component_types,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^components/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_remote_objects,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.PUT,
                      RequestParser.FUNC: self._do_put_remote_object,
                      RequestParser.PARAMS: 2})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_remote_object,
                      RequestParser.PARAMS: 1})
        rules.append({RequestParser.PATTERN: r"^components/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.DELETE,
                      RequestParser.FUNC: self._do_delete_remote_object,
                      RequestParser.PARAMS: 1})
        self._parser.add_rule(rules)

    def _do_get_component_types(self):
        comp_types = {}
        tmp = None
        try:
            for type_name, clazz in self.remote_object_classes.items():
                comp_id = "%s_%s" % (self.object_id, type_name)
                component = clazz(comp_id, None)
                obj_prop = component.object_property
                component = None

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
