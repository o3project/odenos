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


import msgpack

from org.o3project.odenos.remoteobject.transport.base_message_transport import (
    BaseMessageTransport
)
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response


class LocalMessageTransport(BaseMessageTransport):

    def __init__(self, remote_object_id, dispatcher):
        BaseMessageTransport.__init__(self, remote_object_id)
        self.dispatcher = dispatcher

    def send_request_message(self, request):
        # for deep copy of Request object
        obj = msgpack.unpackb(msgpack.packb(request.packed_object()))
        rcv_request = Request.create_from_packed(obj)
        response = self.dispatcher.dispatch_request(rcv_request)
        # for deep copy of Response object
        obj = msgpack.unpackb(msgpack.packb(response.packed_object()))
        return Response.create_from_packed(obj)

    def close(self):
        pass
