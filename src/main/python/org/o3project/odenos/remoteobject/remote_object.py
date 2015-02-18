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
import traceback
import copy

from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.message.event import Event
from org.o3project.odenos.remoteobject.event.object_setting_changed import (
    ObjectSettingChanged
)
from org.o3project.odenos.remoteobject.manager.event_subscription import EventSubscription
from org.o3project.odenos.core.util.request_parser import RequestParser


class RemoteObject(object):
    DESCRIPTION = ""
    CONNECTION_TYPES = ""

    def __init__(self, arg1, arg2, arg3=None):
        if arg3 is None:
            object_id = arg1
            dispatcher = arg2
        else:
            object_id = arg1
            baseUrl = arg2
            dispatcher = arg3
        self._object_property = ObjectProperty(self.__class__.__name__,
                                               object_id)
        self._object_property.set_state(ObjectProperty.State.INITIALIZING)
        self.__set_description()
        self.__set_connection_types()
        self._object_settings = {}

        self.dispatcher = dispatcher
        if self.dispatcher is None:
            return
        self.dispatcher.add_local_object(self)
        self._event_subscription = EventSubscription(object_id)
        self.__parser = RequestParser()
        self.__add_rules()

    def on_initialize(self, obj_prop):
        return True

    def on_finalize(self):
        logging.debug("on_finalize ObjectID:" + self.object_id)
        self.dispatcher.remove_local_object(self)

    @property
    def object_id(self):
        return self._object_property.object_id

    @property
    def object_property(self):
        return self._object_property

    @property
    def object_settings(self):
        return self._object_settings

    @property
    def state(self):
        return self._object_property.state

    def set_state(self, state):
        prop = self.object_property
        prop.set_state(state)
        return self._do_put_property(prop.packed_object())

    def __set_description(self):
        self._object_property.set_property(ObjectProperty.DESCRIPTION,
                                           self.DESCRIPTION)

    def __set_connection_types(self):
        self._object_property.set_property(ObjectProperty.CONNECTION_TYPES,
                                           self.CONNECTION_TYPES)

    def _request_sync(self, object_id, method, path, body=None):
        return self.dispatcher.request_sync(Request(object_id,
                                                    method,
                                                    path,
                                                    body))

    def _request(self, object_id, method, path, body=None):
        resp = Response(Response.StatusCode.INTERNAL_SERVER_ERROR, None)
        try:
            resp = self._request_sync(object_id, method, path, body)
        except:
            logging.error("Exception: Request to " + object_id
                          + " Method:" + method
                          + " Path:" + path)
            logging.error(traceback.format_exc())

        return resp

    def _publish_event_async(self, event_type, body):
        return self.dispatcher.publish_event_async(Event(self.object_id,
                                                         event_type,
                                                         body))

    def _apply_event_subscription(self):
        return self.dispatcher.subscribe_event(self._event_subscription)

    def __add_rules(self):
        rules = []
        rules.append({RequestParser.PATTERN: r"^property/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_property,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^property/?$",
                      RequestParser.METHOD: Request.Method.PUT,
                      RequestParser.FUNC: self._do_put_property,
                      RequestParser.PARAMS: 1})
        rules.append({RequestParser.PATTERN: r"^settings/?$",
                      RequestParser.METHOD: Request.Method.GET,
                      RequestParser.FUNC: self._do_get_settings,
                      RequestParser.PARAMS: 0})
        rules.append({RequestParser.PATTERN: r"^settings/?$",
                      RequestParser.METHOD: Request.Method.PUT,
                      RequestParser.FUNC: self._do_put_settings,
                      RequestParser.PARAMS: 1})
        self.__parser.add_rule(rules)

    def dispatch_request(self, request):
        try:
            resp = self.__parser.action(request)
            if resp.status_code == Response.StatusCode.NOT_FOUND:
                resp = self._on_request(request)
        except:
            logging.error("Exception: Receive Request"
                          + " Method:" + request.method
                          + " Path:" + request.path)
            logging.error(traceback.format_exc())
            resp = Response(Response.StatusCode.INTERNAL_SERVER_ERROR, None)

        return resp

    def dispatch_event(self, event):
        self._do_post_event(event)

    def _do_get_property(self):
        logging.debug("Receive GET Property ObjectID:" + self.object_id)
        return Response(Response.StatusCode.OK, self._object_property)

    def _do_put_property(self, body):
        logging.debug("Receive PUT Property ObjectID:" + self.object_id)
        # check State Change to Finalizing
        for k, v in body.items():
            if k == ObjectProperty.OBJECT_STATE:
                oldValue = self._object_property.get_property(k)
                self.on_state_changed(oldValue, v)

        # update Property
        if not(self._object_property.equals(body)):
            self._object_property.put_property(body)

        return Response(Response.StatusCode.OK, self._object_property)

    def _do_get_settings(self):
        logging.debug("Receive GET Settings ObjectID:" + self.object_id)
        return Response(Response.StatusCode.OK, self._object_settings)

    def _do_put_settings(self, body):
        logging.debug("Receive PUT Settings ObjectID:" + self.object_id)
        if body != self._object_settings:
            old = copy.deepcopy(self._object_settings)
            # delete
            for k in old.keys():
                if k not in body:
                    del self._object_settings[k]

            # add or update
            for k, v in body.items():
                self._object_settings[k] = v

            self.on_settings_changed(ObjectSettingChanged.Action.UPDATE,
                                     old,
                                     self._object_settings)

        return Response(Response.StatusCode.OK, self._object_settings)

    def _do_post_event(self, event):
        self.on_event(event)

    def on_state_changed(self, old_state, new_state):
        if old_state != ObjectProperty.State.FINALIZING:
            if new_state == ObjectProperty.State.FINALIZING:
                self.on_finalize()

    def on_settings_changed(self, action, prev, curr):
        event_body = {ObjectSettingChanged.ACTION: action,
                      ObjectSettingChanged.PREV: prev,
                      ObjectSettingChanged.CURR: curr}
        self._publish_event_async(ObjectSettingChanged.TYPE,
                                  event_body)

    def _on_request(self, request):
        return Response(Response.StatusCode.BAD_REQUEST, None)

    def on_event(self, event):

        # for example: method_name = "_do_event_topologychanged"
        method_name = "_do_event_" + event.event_type.lower()
        if hasattr(self, method_name):
            method = getattr(self, method_name)
            if callable(method):
                method(event)
                return
        return
