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

from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response


# pylint: disable=R0923
class RemoteObjectInterface(object):
    PROPETY_PATH = "property"
    SETTINGS_PATH = "settings"

    def __init__(self, dispatcher, object_id, source_object_id=None):
        self.__dispatcher = dispatcher
        self.__object_id = object_id
        self.__source_object_id = source_object_id

    @property
    def object_id(self):
        return self.__object_id
    
    @property
    def source_object_id(self):
        return self.__source_object_id

    ###################################
    # Basic request
    ###################################
    # GET Property.
    def get_property(self):
        logging.debug("GET Property ObjectID:" + self.__object_id)
        resp = self._get_object_to_remote_object(self.PROPETY_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # GET Settings.
    def get_settings(self):
        logging.debug("GET Settings ObjectID:" + self.__object_id)
        resp = self._get_object_to_remote_object(self.SETTINGS_PATH)
        if resp.is_error(Request.Method.GET):
            return None

        return resp.body

    # PUT Property.
    def put_property(self, property_):
        logging.debug("PUT Property ObjectID:" + self.__object_id)
        return self._put_object_to_remote_object(self.PROPETY_PATH,
                                                 property_)

    # PUT Settings.
    def put_settings(self, settings):
        logging.debug("PUT Settings ObjectID:" + self.__object_id)
        return self._put_object_to_remote_object(self.SETTINGS_PATH,
                                                 settings)

    ###################################
    # Common Method
    ###################################

    def _post_object_to_remote_object(self, path, body):
        resp = self.__send_request(Request.Method.POST, path, body)
        if resp.is_error(Request.Method.POST):
            logging.debug("Error Response POST DestID:" + self.__object_id
                          + " Path:" + path
                          + " StatusCode:" + str(resp.status_code))
        return resp

    def _put_object_to_remote_object(self, path, body):
        resp = self.__send_request(Request.Method.PUT, path, body)
        if resp.is_error(Request.Method.PUT):
            logging.debug("Error Response PUT DestID:" + self.__object_id
                          + " Path:" + path
                          + " StatusCode:" + str(resp.status_code))
        return resp

    def _del_object_to_remote_object(self, path, body=None):
        resp = self.__send_request(Request.Method.DELETE, path, body=body)
        if resp.is_error(Request.Method.DELETE):
            logging.debug("Error Response DELETE DestID:" + self.__object_id
                          + " Path:" + path
                          + " StatusCode:" + str(resp.status_code))
        return resp

    def _get_object_to_remote_object(self, path):
        resp = self.__send_request(Request.Method.GET, path)
        if resp.is_error(Request.Method.GET):
            logging.debug("Error Response GET DestID:" + self.__object_id
                          + " Path:" + path
                          + " StatusCode:" + str(resp.status_code))
        return resp

    def __send_request(self, method, path, body=None):
        resp = Response(Response.StatusCode.INTERNAL_SERVER_ERROR,
                        None)
        req = Request(self.__object_id, method, path, body=body)
        try:
            resp = self.__dispatcher.request_sync(req, self.__source_object_id)
        except:
            logging.error("Exception: Request to " + self.__object_id
                          + " Method:" + method
                          + " Path:" + path)
            logging.error(traceback.format_exc())

        return resp
