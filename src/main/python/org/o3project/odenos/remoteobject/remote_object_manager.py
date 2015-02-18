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
from org.o3project.odenos.remoteobject.manager.system.event.component_manager_changed\
    import ComponentManagerChanged
from org.o3project.odenos.core.util.request_parser import RequestParser


class RemoteObjectManager(RemoteObject):

    # FIXME
    DESCRIPTION = "python's RemoteObjectManager"
    REMOTE_OBJECT_TYPES = "remote_object_types"

    def __init__(self, object_id, dispatcher):
        RemoteObject.__init__(self, object_id, dispatcher)
        self.remote_object_classes = {}
        self.remote_objects = {}
        self.dispatcher = dispatcher
        self._parser = RequestParser()
        self._add_rules()
        self._object_property.set_property(RemoteObjectManager.REMOTE_OBJECT_TYPES, "")

    def register_remote_objects(self, remote_objects):
        for remote_object in remote_objects:
            remote_object_name = remote_object.__name__
            logging.debug("Register RemoteObject Type:%s", remote_object_name)

            self.remote_object_classes[remote_object_name] = remote_object

        types = ",".join(self.remote_object_classes.keys())
        self._object_property.set_property(RemoteObjectManager.REMOTE_OBJECT_TYPES,
                                           types)

    def _add_rules(self):
        rules = []
        rules.append({RequestParser.PATTERN: r"^remote_object_types/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_remote_object_types,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^remote_objects/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_remote_objects,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^remote_objects/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.PUT,
                      RequestParser.FUNC: self._do_put_remote_object,
                      RequestParser.PARAMS: 2})
        rules.append({RequestParser.PATTERN: r"^remote_objects/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_remote_object,
                      RequestParser.PARAMS: 1})
        rules.append({RequestParser.PATTERN: r"^remote_objects/"
                      + "([a-zA-Z0-9_-]+)/?$",
                      RequestParser.METHOD: Request.Method.DELETE,
                      RequestParser.FUNC: self._do_delete_remote_object,
                      RequestParser.PARAMS: 1})
        self._parser.add_rule(rules)

    def _on_request(self, request):
        return self._parser.action(request)

    def _do_get_remote_object_types(self):
        return Response(Response.StatusCode.OK, self.remote_object_classes.keys())

    def _do_get_remote_objects(self):
        body = {}
        for object_id in self.remote_objects:
            body[object_id] = \
                self.remote_objects[object_id].object_property.packed_object()
        return Response(Response.StatusCode.OK, body)

    def _do_get_remote_object(self, object_id):
        if object_id in self.remote_objects:
            return Response(Response.StatusCode.OK,
                            self.remote_objects[object_id].object_property)
        return Response(Response.StatusCode.NOT_FOUND, None)

    def _do_put_remote_object(self, obj_prop, object_id):
        remote_object_type = obj_prop[ObjectProperty.OBJECT_TYPE]

        if remote_object_type not in self.remote_object_classes:
            return Response(Response.StatusCode.BAD_REQUEST, None)
        elif object_id in self.remote_objects:
            return Response(Response.StatusCode.CONFLICT, None)

        remote_object_class = self.remote_object_classes[remote_object_type]
        self.remote_objects[object_id] = remote_object_class(object_id, self.dispatcher)

        if self.remote_objects[object_id].on_initialize(obj_prop):
            self.remote_objects[object_id].set_state(ObjectProperty.State.RUNNING)
        else:
            self.remote_objects[object_id].set_state(ObjectProperty.State.ERROR)

        logging.debug("Created RemoteObject Type:%s ID:%s", remote_object_type, object_id)
        return Response(Response.StatusCode.CREATED,
                        self.remote_objects[object_id].object_property)

    def _do_delete_remote_object(self, object_id):
        if object_id in self.remote_objects:
            remote_object = self.remote_objects[object_id]
            prev = copy.deepcopy(remote_object._object_property).packed_object()
            remote_object.on_finalize()
            del self.remote_objects[object_id]
            logging.debug("Deleted RemoteObject ID:%s", object_id)

        return Response(Response.StatusCode.OK, None)
