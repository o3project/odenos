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

import Queue
import threading
import logging
import msgpack

from org.o3project.odenos.remoteobject.transport.base_message_transport import (
    BaseMessageTransport
)
from org.o3project.odenos.remoteobject.message.response import Response
import message_dispatcher


class RemoteMessageTransport(BaseMessageTransport):

    class AtomicInteger:

        def __init__(self, integer=0):
            self.counter = integer
            self.lock = threading.RLock()

        def increase(self):
            self.lock.acquire()
            self.counter = self.counter + 1
            self.lock.release()
            return self.counter

    class SynchronousQueue(object):
        RESPONSE_TIMEOUT = 180

        def __init__(self):
            self.q = Queue.Queue(1)
            self.put_lock = threading.RLock()

        def get(self):
            value = self.q.get(block=True, timeout=self.RESPONSE_TIMEOUT)
            self.q.task_done()
            return value

        def put(self, item):
            with self.put_lock:
                self.q.put(item, block=True)
                self.q.join()

    def __init__(self, remote_object_id, dispatcher):
        BaseMessageTransport.__init__(self, remote_object_id)
        self.dispatcher = dispatcher
        self.seqnoGenerator = RemoteMessageTransport.AtomicInteger()
        self.responseMap = {}

    def send_request_message(self, request, source_object_id):
        logging.debug("[(B1) send request ]: " + str(request.packed_object()))
        queue = self.addRequet(request, source_object_id)
        response = queue.get()
        if response is None:
            raise IOError("fail subscribe" + request.object_id)
        return response

    def addRequet(self, request, source_object_id):
        try:
            sno = self.seqnoGenerator.increase()
            queue = RemoteMessageTransport.SynchronousQueue()
            self.responseMap[sno] = queue
            pk = msgpack.Packer()
            reqb = bytearray()
            reqb.extend(pk.pack(
                message_dispatcher.MessageDispatcher.TYPE_REQUEST))
            reqb.extend(pk.pack(sno))
            if self.dispatcher.monitor_enabled():  # Monitor
                reqb.extend(pk.pack(source_object_id))
            else:
                reqb.extend(pk.pack(self.dispatcher.get_source_dispatcher_id()))
            reqb.extend(pk.pack(request.packed_object()))
            self.dispatcher.pushPublishQueue(
                self,
                message_dispatcher.MessageDispatcher.TYPE_REQUEST,
                sno,
                request.object_id,
                reqb)

            return queue
        except Exception:
            logging.exception('[addRequet]')
            del self.responseMap[sno]
            raise

    def signalResponse(self, sno, response):
        if sno in self.responseMap:
            queue = self.responseMap[sno]
            del self.responseMap[sno]
            queue.put(response)

    def close(self):
        pass
