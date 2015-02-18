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


from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.remoteobject.transport.remote_message_transport\
    import RemoteMessageTransport
from org.o3project.odenos.remoteobject.object_property import ObjectProperty

import unittest
from mock import Mock, MagicMock, patch
from contextlib import nested


class RemoteMessageTransportTest(unittest.TestCase):
    Dispatcher = MagicMock()
    value01 = ""
    value02 = ""
    result01 = ""
    result02 = ""

    def setUp(self):
        self.target = RemoteMessageTransport(
            "RemoteMessageTransport",
            self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target.object_id, "RemoteMessageTransport")
        self.assertEqual(self.target.seqnoGenerator.counter, 0)
        self.assertEqual(self.target.responseMap, {})

    def test_AtomicInteger_constructor(self):
        with patch("threading.RLock") as Mock_RLock:
            Mock_RLock.return_value = "Mock_RLock"

            self.target = self.target.AtomicInteger(5)

            self.assertEqual(self.target.counter, 5)
            self.assertEqual(self.target.lock, "Mock_RLock")

    def test_AtomicInteger_increase(self):
        self.target = self.target.AtomicInteger(6)

        self.target.increase()

        self.assertEqual(self.target.counter, 7)

    def test_SynchronousQueue_constructor(self):
        with nested(patch("threading.RLock"),
                    patch("Queue.Queue")) as (Mock_RLock,
                                              Mock_Queue):
            Mock_Queue.return_value = "Mock_Queue"
            Mock_RLock.return_value = "Mock_RLock"

            self.target = self.target.SynchronousQueue()

            self.assertEqual(self.target.q, "Mock_Queue")
            self.assertEqual(self.target.put_lock, "Mock_RLock")

    def test_SynchronousQueue_get(self):
        self.target = self.target.SynchronousQueue()
        self.value01 = ObjectProperty("object_type", "object_id")
        with self.target.put_lock:
            self.target.q.put(self.value01, block=True)

        self.result = self.target.get()

        self.assertEqual(self.result, self.value01)

    def test_SynchronousQueue_put(self):
        self.target = self.target.SynchronousQueue()
        with patch("Queue.Queue.join") as Mock_Queue_join:
            self.value01 = ObjectProperty("object_type", "object_id")

            self.result = self.target.put(self.value01)

            self.assertEqual(self.target.get(), self.value01)

    def test_send_request_message(self):
        with patch("org.o3project.odenos.remoteobject.transport." +
                   "remote_message_transport.RemoteMessageTransport." +
                   "SynchronousQueue.get") as q_get:
            self.value01 = ObjectProperty("object_type", "object_id")

            q_get.return_value = "get_item"
            self.target.dispatcher.get_source_dispatcher_id = Mock(
                return_value="dispatcher_id")

            self.result01 = self.target.send_request_message(self.value01)

            self.assertEqual(self.result01, "get_item")

    def test_send_request_message_response_None(self):
        with patch("org.o3project.odenos.remoteobject.transport." +
                   "remote_message_transport.RemoteMessageTransport." +
                   "SynchronousQueue.get") as q_get:
            self.value01 = ObjectProperty("object_type", "object_id")

            q_get.return_value = None
            self.target.dispatcher.get_source_dispatcher_id = Mock(
                return_value="dispatcher_id")

            try:
                self.result = self.target.send_request_message(self.value01)

            except:
                pass

    def test_addRequest_success(self):
        self.value01 = ObjectProperty("object_type", "object_id")

        self.target.dispatcher.get_source_dispatcher_id = Mock(
            return_value="dispatcher_id")

        self.result01 = self.target.addRequet(self.value01)

        self.assertEqual(len(self.target.responseMap), 1)
        self.assertEqual(self.target.responseMap[1], self.result01)

    def test_addRequest_error(self):
        with nested(
            patch("org.o3project.odenos.remoteobject.transport."
                  "message_dispatcher.MessageDispatcher."
                  "pushPublishQueue"),
            patch("logging.exception")) as (Mock_pushPublishQueue,
                                            logging_exception):
            self.value01 = ObjectProperty("object_type", "object_id")

            self.target.dispatcher.get_source_dispatcher_id = Mock(
                return_value="dispatcher_id")
            self.target.dispatcher.get_source_dispatcher_id.side_effect =\
                Exception()

            try:
                self.result01 = self.target.addRequet(self.value01)

            except:
                self.assertEqual(logging_exception.call_count, 1)
                self.assertEqual(len(self.target.responseMap), 0)

    def test_signalResponse(self):
        with patch("org.o3project.odenos.remoteobject.transport." +
                   "remote_message_transport.RemoteMessageTransport." +
                   "SynchronousQueue.put") as q_put:
            self.value01 = ObjectProperty("object_type01", "object_id01")
            self.value02 = ObjectProperty("object_type02", "object_id02")

            self.target.dispatcher.get_source_dispatcher_id = Mock(
                return_value="dispatcher_id")

            self.result01 = self.target.addRequet(self.value01)
            self.result02 = self.target.addRequet(self.value02)

            self.target.signalResponse(1, self.value01)

            self.assertEqual(len(self.target.responseMap), 1)
            self.assertEqual(self.target.responseMap[2], self.result02)

    def test_close(self):
        self.target.close()

if __name__ == "__main__":
    unittest.main()
