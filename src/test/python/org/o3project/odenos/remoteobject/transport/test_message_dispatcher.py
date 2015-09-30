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


from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.event import Event
from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.transport.message_dispatcher\
    import MessageDispatcher
from org.o3project.odenos.remoteobject.manager.event_subscription\
    import EventSubscription
from org.o3project.odenos.remoteobject.transport.remote_message_transport\
    import RemoteMessageTransport
from org.o3project.odenos.remoteobject.transport.local_message_transport\
    import LocalMessageTransport

import redis
import Queue
import threading
import msgpack
import unittest
from concurrent import futures
from mock import Mock, MagicMock, patch
from contextlib import nested


class MessageDispatcherTest(unittest.TestCase):

    DISPATCHER_PATH = "org.o3project.odenos.remoteobject.transport.message_dispatcher." +\
        "MessageDispatcher"
    REQUEST_PATH = "org.o3project.odenos.remoteobject.message.request.Request"
    RESPONSE_PATH = "org.o3project.odenos.remoteobject.message.response.Response"
    EVENT_PATH = "org.o3project.odenos.remoteobject.message.event.Event"
    value = ""
    value02 = ""
    result = ""

    def setUp(self):
        self.target = MessageDispatcher()

    def tearDown(self):
        self.target = None

    def test_EventSubscriptionMap_constructor(self):
        self.target = self.target.EventSubscriptionMap()

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions, {})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map, {})

    def test_EventSubscriptionMap_get_subscriptions_keys(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscription_map = {
            "key01": "value01", "key02": "value02",
            "key03": "value03", "key04": "value04"}

        self.result = self.target.get_subscriptions_keys()

        self.assertEqual(
            self.result, ["key01", "key02", "key03", "key04"])

    def test_EventSubscriptionMap_set_subscription(self):
        self.target = self.target.EventSubscriptionMap()
        self.value = EventSubscription("EventSubscription",
                                       {"key01": ["value01",
                                                  "value02"]})

        self.target.set_subscription(self.value)

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions,
            {"EventSubscription": set(["key01:value01",
                                       "key01:value02"])})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map,
            {"key01:value01": set(["EventSubscription"]),
             "key01:value02": set(["EventSubscription"])})

    def test_EventSubscriptionMap_remove_subscription_exist(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscriptions =\
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription02": set(["key01:value01",
                                         "key01:value02"])}
        self.target._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["EventSubscription01",
                                   "EventSubscription02"]),
             "key01:value02": set(["EventSubscription01",
                                   "EventSubscription02"])}

        self.result = self.target.remove_subscription("EventSubscription02")

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions,
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"])})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map,
            {"key01:value01": set(["EventSubscription01"]),
             "key01:value02": set(["EventSubscription01"])})
        self.assertEqual(
            self.result, set(["key01:value01", "key01:value02"]))

    def test_EventSubscriptionMap_remove_subscription_subscriber_not_in(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscriptions =\
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription": set(["key01:value01",
                                       "key01:value02"])}
        self.target._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["EventSubscription01",
                                   "EventSubscription02"]),
             "key01:value02": set(["EventSubscription01",
                                   "EventSubscription02"])}

        self.result = self.target.remove_subscription("EventSubscription02")

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions,
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription": set(["key01:value01",
                                       "key01:value02"])})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map,
            {"key01:value01": set(["EventSubscription01"]),
             "key01:value02": set(["EventSubscription01"])})
        self.assertEqual(
            self.result, set())

    def test_EventSubscriptionMap_remove_subscription_subscribers_zero(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscriptions =\
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription02": set(["key01:value01",
                                         "key01:value02"])}
        self.target._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["EventSubscription02"]),
             "key01:value02": set(["EventSubscription01",
                                   "EventSubscription02"])}

        self.result = self.target.remove_subscription("EventSubscription02")

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions,
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"])})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map,
            {"key01:value02": set(["EventSubscription01"])})
        self.assertEqual(
            self.result, set(["key01:value01", "key01:value02"]))

    def test_EventSubscriptionMap_get_subscribers_return_subscribers(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["EventSubscription01",
                                   "EventSubscription02"]),
             "key01:value02": set(["EventSubscription01",
                                   "EventSubscription02"])}

        self.result = self.target.get_subscribers("key01", "value01")

        self.assertEqual(
            self.result, set(["EventSubscription01",
                              "EventSubscription02"]))

    def test_EventSubscriptionMap_get_subscribers_return_keys(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscriptions =\
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription": set(["key01:value01",
                                       "key01:value02"])}

        self.result = self.target.get_subscribers()

        self.assertEqual(
            self.result, set(["EventSubscription01", "EventSubscription"]))

    def test_EventSubscriptionMap_clear(self):
        self.target = self.target.EventSubscriptionMap()
        self.target._EventSubscriptionMap__subscriptions =\
            {"EventSubscription01": set(["key01:value01",
                                         "key01:value02"]),
             "EventSubscription": set(["key01:value01",
                                       "key01:value02"])}
        self.target._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["EventSubscription01",
                                   "EventSubscription02"]),
             "key01:value02": set(["EventSubscription01",
                                   "EventSubscription02"])}

        self.target.clear()

        self.assertEqual(
            self.target._EventSubscriptionMap__subscriptions,
            {})
        self.assertEqual(
            self.target._EventSubscriptionMap__subscription_map,
            {})

    def test_PublishData_constructor(self):
        self.target = self.target.PublishData("trans", "PublishData",
                                              1, "channel", "data")

        self.assertEqual(
            self.target.trans, "trans")
        self.assertEqual(
            self.target.type, "PublishData")
        self.assertEqual(
            self.target.sno, 1)
        self.assertEqual(
            self.target.channel, "channel")
        self.assertEqual(
            self.target.data, "data")

    def test_constructor(self):
        with nested(
            patch("uuid.uuid4"),
                patch("Queue.Queue"),
                patch("org.o3project.odenos.remoteobject." +
                      "transport.message_dispatcher." +
                      "MessageDispatcher.EventSubscriptionMap")) as (
                mock_uuid4,
                mock_Queue,
                mock_EventSubscriptionMap):

            mock_uuid4.return_value = "mock_uuid4"
            mock_Queue.return_value = "mock_Queue"
            mock_EventSubscriptionMap.return_value =\
                "mock_EventSubscriptionMap"
            self.target = MessageDispatcher()

            self.assertEqual(self.target._MessageDispatcher__clients, {})
            self.assertEqual(
                self.target._MessageDispatcher__local_objects, {})
            self.assertEqual(
                self.target._MessageDispatcher__event_manager_id, None)
            self.assertEqual(
                self.target.system_manager_id, "systemmanager")
            self.assertEqual(
                self.target._MessageDispatcher__redisServer, "localhost")
            self.assertEqual(
                self.target._MessageDispatcher__redisPort, 6379)
            self.assertEqual(
                self.target._MessageDispatcher__sourceDispatcherId,
                "mock_uuid4")
            self.assertEqual(
                self.target._MessageDispatcher__pubsqueue,
                "mock_Queue")
            self.assertEqual(
                self.target._MessageDispatcher__redisSubscriber, None)
            self.assertEqual(
                self.target._MessageDispatcher__subscription_map,
                "mock_EventSubscriptionMap")

    def test_redisPublisherRunnable_success(self):
        remote_msg_trans = RemoteMessageTransport("remote_object_id",
                                                  self.target)
        self.value = self.target.PublishData(remote_msg_trans, 0,
                                             1, "123456789", "data")
        self.target._MessageDispatcher__redisPublisher = redis.StrictRedis(
            host=self.target._MessageDispatcher__redisServer,
            port=self.target._MessageDispatcher__redisPort)

        self.target._MessageDispatcher__pubsqueue.put(self.value)
        self.target._MessageDispatcher__pubsqueue.put(None)

        with nested(patch("redis.client.StrictRedis.publish"),
                    patch("org.o3project.odenos.remoteobject.transport."
                          "remote_message_transport.RemoteMessageTransport."
                          "signalResponse")) as (mock_publish,
                                                 mock_signalResponse):
            self.target._MessageDispatcher__redisPublisherRunnable()

            mock_publish.assert_called_once_with("123456789", "data")
            self.assertEqual(mock_signalResponse.call_count, 0)

    def test_redisPublisherRunnable_Exept(self):
        remote_msg_trans = RemoteMessageTransport("remote_object_id",
                                                  self.target)
        self.value = self.target.PublishData(remote_msg_trans, 0,
                                             1, "channel", "data")

        self.target._MessageDispatcher__pubsqueue.put(self.value)
        self.target._MessageDispatcher__pubsqueue.put(None)

        with nested(patch("redis.client.StrictRedis.publish"),
                    patch("org.o3project.odenos.remoteobject.transport."
                          "remote_message_transport.RemoteMessageTransport."
                          "signalResponse")) as (mock_publish,
                                                 mock_signalResponse):
            self.target._MessageDispatcher__redisPublisherRunnable()

            self.assertEqual(mock_publish.call_count, 0)
            self.assertEqual(mock_signalResponse.call_count, 1)

    def test_redisSubscriberRunnable_TYPE_REQUEST(self):
        request = Request("object_id", "method", "path", None)
        request_packed = request.packed_object()
        response = Response(200, "body")
        response_packed = response.packed_object()

        pk = msgpack.Packer()
        resb = bytearray()
        resb.extend(pk.pack(0))
        resb.extend(pk.pack(0))
        resb.extend(pk.pack("object_id"))
        resb.extend(pk.pack(request_packed))
        self.value = {"type": "message",
                      "data": resb,
                      "channel": "object_id"}
        self.value02 = {"type": "ERROR",
                        "data": resb}

        resb_check = bytearray()
        resb_check.extend(pk.pack(1))
        resb_check.extend(pk.pack(0))
        resb_check.extend(pk.pack("object_id"))
        resb_check.extend(pk.pack(response_packed))

        self.target.thread_pool = futures.ThreadPoolExecutor(max_workers=8)
        self.target._MessageDispatcher__redisSubscriber = redis.StrictRedis(
            host=self.target._MessageDispatcher__redisServer,
            port=self.target._MessageDispatcher__redisPort)
        self.target._MessageDispatcher__redisSubscriber =\
            self.target._MessageDispatcher__redisSubscriber.pubsub()

        def dummy_request_runnable(arg, request, sno, srcid):
            arg(request, sno, srcid)

        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("redis.client.PubSub.unsubscribe"),
                patch("redis.client.PubSub.listen"),
                patch("concurrent.futures.ThreadPoolExecutor.submit"),
                patch(self.REQUEST_PATH + ".create_from_packed"),
                patch(self.DISPATCHER_PATH + ".dispatch_request")) as (
                mock_subscribe,
                mock_unsubscribe,
                mock_listen,
                mock_submit,
                mock_request,
                mock_dispatch_request):
            mock_subscribe.return_value = None
            mock_unsubscribe.return_value = None
            mock_listen.return_value = [self.value, self.value02]
            mock_request.return_value = request
            mock_dispatch_request.return_value = response
            mock_submit.side_effect = dummy_request_runnable

            self.target._MessageDispatcher__redisSubscriberRunnable()
            self.result = self.target._MessageDispatcher__pubsqueue.get()

            mock_request.assert_called_once_with(
                ["object_id", "method", "path", "*", None])
            self.assertEqual(mock_submit.call_count, 1)

            self.assertEqual(
                self.result.trans, None)
            self.assertEqual(
                self.result.type, 1)
            self.assertEqual(
                self.result.sno, 0)
            self.assertEqual(
                self.result.channel, "object_id")
            self.assertEqual(
                self.result.data, resb_check)

    def test_redisSubscriberRunnable_TYPE_RESPONSE(self):
        clients = self.target._MessageDispatcher__clients
        remote_msg_trans = RemoteMessageTransport("publisher_id:event_type",
                                                  self.target)
        clients["publisher_id:event_type"] = remote_msg_trans

        response = Response(200, "remote_msg_trans")

        pk = msgpack.Packer()
        resb = bytearray()
        resb.extend(pk.pack(1))
        resb.extend(pk.pack(0))
        resb.extend(pk.pack("publisher_id:event_type"))
        resb.extend(pk.pack(response.packed_object()))
        self.value = {"type": "message",
                      "data": resb,
                      "channel": "publisher_id:event_type"}

        self.target._MessageDispatcher__redisSubscriber = redis.StrictRedis(
            host=self.target._MessageDispatcher__redisServer,
            port=self.target._MessageDispatcher__redisPort)
        self.target._MessageDispatcher__redisSubscriber =\
            self.target._MessageDispatcher__redisSubscriber.pubsub()

        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("redis.client.PubSub.unsubscribe"),
                patch("redis.client.PubSub.listen"),
                patch(self.RESPONSE_PATH + ".create_from_packed")) as (
                mock_subscribe,
                mock_unsubscribe,
                mock_listen,
                mock_response):
            mock_subscribe.return_value = None
            mock_unsubscribe.return_value = None
            mock_listen.return_value = [self.value]

            self.target._MessageDispatcher__redisSubscriberRunnable()

            mock_response.assert_called_once_with(
                [200, "remote_msg_trans"])

    def test_redisSubscriberRunnable_TYPE_EVENT(self):
        event = Event("publisher_id", "event_type", "event_body")

        pk = msgpack.Packer()
        resb = bytearray()
        resb.extend(pk.pack(2))
        resb.extend(pk.pack(0))
        resb.extend(pk.pack("publisher_id:event_type"))
        resb.extend(pk.pack(event.packed_object()))
        self.value = {"type": "message",
                      "data": resb,
                      "channel": "publisher_id:event_type"}

        self.target.thread_pool = futures.ThreadPoolExecutor(max_workers=8)
        self.target._MessageDispatcher__redisSubscriber = redis.StrictRedis(
            host=self.target._MessageDispatcher__redisServer,
            port=self.target._MessageDispatcher__redisPort)
        self.target._MessageDispatcher__redisSubscriber =\
            self.target._MessageDispatcher__redisSubscriber.pubsub()

        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("logging.error"),
                patch("redis.client.PubSub.unsubscribe"),
                patch("redis.client.PubSub.listen"),
                patch(self.EVENT_PATH + ".create_from_packed")) as (
                mock_subscribe,
                logging_error,
                mock_unsubscribe,
                mock_listen,
                mock_event):
            mock_subscribe.return_value = None
            mock_unsubscribe.return_value = None
            mock_listen.return_value = [self.value]

            self.target._MessageDispatcher__redisSubscriberRunnable()

            mock_event.assert_called_once_with(
                ["publisher_id", "event_type", "*", "event_body"])

    def test_redisSubscriberRunnable_except(self):
        event = Event("publisher_id", "event_type", "event_body")

        pk = msgpack.Packer()
        resb = bytearray()
        resb.extend(pk.pack(2))
        resb.extend(pk.pack(0))
        resb.extend(pk.pack("publisher_id:event_type"))
        resb.extend(pk.pack(event.packed_object()))
        self.value = {"type": "message",
                      "data": resb,
                      "channel": "publisher_id:event_type"}

        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("logging.error"),
                patch("redis.client.PubSub.unsubscribe"),
                patch("redis.client.PubSub.listen"),
                patch(self.EVENT_PATH + ".create_from_packed")) as (
                mock_subscribe,
                logging_error,
                mock_unsubscribe,
                mock_listen,
                mock_event):
            mock_subscribe.return_value = None
            mock_unsubscribe.return_value = None
            mock_listen.return_value = [self.value]
            mock_event.side_effect = KeyError()

            self.target.start()
            self.target._MessageDispatcher__redisSubscriberRunnable()
            self.target.close()

            mock_event.assert_called_any_with(
                ["publisher_id", "event_type", "event_body"])

    def test_start(self):
        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("redis.client.PubSub.unsubscribe")) as (
                mock_subscribe,
                mock_unsubscribe):
            mock_subscribe.return_value = None
            mock_unsubscribe.return_value = None
            self.target.start()
            self.target.close()

    def test_stop_redisSubscriberThread_Not_None(self):
        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("threading.Thread.join"),
                patch("redis.client.PubSub.unsubscribe")) as (
                mock_subscribe,
                mock_join,
                mock_unsubscribe):
            mock_subscribe.return_value = None
            self.target.start()
            self.target.stop()

            self.assertEqual(mock_join.call_count, 2)
            self.assertEqual(mock_unsubscribe.call_count, 1)

    def test_stop_redisSubscriberThread_None(self):
        self.target.stop()

    def test_join_redisSubscriberThread_Not_None(self):
        self.target._MessageDispatcher__redisSubscriberThread =\
            threading.Thread(
                target=self.target._MessageDispatcher__redisSubscriberRunnable)

        self.target._MessageDispatcher__redisPublisherThread =\
            threading.Thread(
                target=self.target._MessageDispatcher__redisPublisherRunnable)

        with nested(
            patch("redis.client.PubSub.subscribe"),
                patch("threading.Thread.join")) as (
                mock_subscribe,
                mock_join):
            mock_subscribe.return_value = None
            self.target.join()

            self.assertEqual(mock_join.call_count, 2)

        self.target._MessageDispatcher__redisPublisherThread = None
        self.target._MessageDispatcher__redisSubscriberThread = None

    def test_join_redisSubscriberThread_None(self):
        self.target.join()

    def test_close(self):
        remote_msg_trans = RemoteMessageTransport("remote_object_id",
                                                  self.target)
        self.target._MessageDispatcher__local_objects["object_id"] =\
            remote_msg_trans
        self.target._MessageDispatcher__clients["object_id"] =\
            remote_msg_trans

        self.target.close()

        self.assertEqual(self.target._MessageDispatcher__local_objects, {})
        self.assertEqual(self.target._MessageDispatcher__clients, {})

    def test_get_message_client_create_client_exist(self):
        remote_msg_trans = RemoteMessageTransport("remote_object_id",
                                                  self.target)
        self.target._MessageDispatcher__clients["object_id"] =\
            remote_msg_trans

        self.result =\
            self.target._MessageDispatcher__get_message_client("object_id")

        self.assertNotEqual(self.target._MessageDispatcher__clients, {})
        self.assertEqual(
            self.target._MessageDispatcher__clients["object_id"].responseMap,
            {})

    def test_get_message_client_create_RemoteMessageT_client(self):
        self.result =\
            self.target._MessageDispatcher__get_message_client("object_id")

        self.assertNotEqual(self.target._MessageDispatcher__clients, {})
        self.assertEqual(
            self.target._MessageDispatcher__clients["object_id"].responseMap,
            {})

    def test_get_message_client_create_LocalMessage_client(self):
        self.target._MessageDispatcher__local_objects = {"object_id": None}

        self.result =\
            self.target._MessageDispatcher__get_message_client("object_id")

        self.assertNotEqual(self.target._MessageDispatcher__clients, {})
        self.assertEqual(
            self.target._MessageDispatcher__clients["object_id"].object_id,
            "object_id")

    def test_get_source_dispatcher_id(self):
        with patch("uuid.uuid4") as mock_uuid4:
            mock_uuid4.return_value = "mock_uuid4"
            self.target = MessageDispatcher()

        self.result = self.target.get_source_dispatcher_id()

        self.assertEqual(self.result, "mock_uuid4")

    def test_getRedisPublisher(self):
        self.assertEqual(
            self.target.getRedisPublisher(),
            self.target._MessageDispatcher__redisPublisher)

    def test_update_subscriber_redisSubscriber_Not_None(self):
        self.target._MessageDispatcher__sourceDispatcherId = "DispatcherId"
        subscription_map =\
            self.target._MessageDispatcher__subscription_map
        subscription_map._EventSubscriptionMap__subscriptions =\
            {"remote_object": set(["key01:value01",
                                   "key01:value02"]),
             "local_object": set(["key01:value01",
                                  "key01:value02"])}
        subscription_map._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["remote_object",
                                   "local_object"]),
             "key01:value02": set(["remote_object",
                                   "local_object"])}
        remote_object = RemoteObject("remote_object", self.target)
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": remote_object}
        self.target._MessageDispatcher__redisSubscriber = redis.StrictRedis(
            host=self.target._MessageDispatcher__redisServer,
            port=self.target._MessageDispatcher__redisPort)
        self.target._MessageDispatcher__redisSubscriber =\
            self.target._MessageDispatcher__redisSubscriber.pubsub()

        with patch("redis.client.PubSub.subscribe") as mock_subscribe:
                self.target._MessageDispatcher__update_subscriber()

                mock_subscribe.assert_called_once_with(
                    ["DispatcherId", "remote_object",
                     "key01:value01", "key01:value02"])

    def test_update_subscriber_redisSubscriber_None(self):
        self.target._MessageDispatcher__update_subscriber()

    def test_event_manager_id_exist(self):
        self.target._MessageDispatcher__event_manager_id = "maneager_id"

        self.assertEqual(self.target.event_manager_id, "maneager_id")

    def test_event_manager_id_None(self):
        self.target._MessageDispatcher__event_manager_id = None

        self.assertEqual(self.target.event_manager_id, "eventmanager")

    def test_add_local_object(self):
        remote_msg_trans = RemoteMessageTransport("remote_object_id",
                                                  self.target)

        self.target.add_local_object(remote_msg_trans)

        self.assertEqual(
            self.target._MessageDispatcher__local_objects["remote_object_id"],
            remote_msg_trans)

    def test_remove_local_object(self):
        remote_msg_trans = RemoteMessageTransport("remote_object",
                                                  self.target)
        local_msg_trans = LocalMessageTransport("local_object",
                                                self.target)
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": remote_msg_trans,
             "local_object": local_msg_trans}
        subscription_map =\
            self.target._MessageDispatcher__subscription_map
        subscription_map._EventSubscriptionMap__subscriptions =\
            {"remote_object": set(["key01:value01",
                                   "key01:value02"]),
             "local_object": set(["key01:value01",
                                  "key01:value02"])}
        subscription_map._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["remote_object",
                                   "local_object"]),
             "key01:value02": set(["remote_object",
                                   "local_object"])}

        self.target.remove_local_object(local_msg_trans)

        self.assertEqual(
            self.target._MessageDispatcher__local_objects,
            {"remote_object": remote_msg_trans})
        self.assertEqual(
            subscription_map._EventSubscriptionMap__subscriptions,
            {"remote_object": set(["key01:value01",
                                   "key01:value02"])})
        self.assertEqual(
            subscription_map._EventSubscriptionMap__subscription_map,
            {"key01:value01": set(["remote_object"]),
             "key01:value02": set(["remote_object"])})

    def test_add_remote_client(self):
        clients = self.target._MessageDispatcher__clients
        self.target.add_remote_client("remote_object")

        self.assertNotEqual(self.target._MessageDispatcher__clients, {})
        self.assertEqual(
            clients["remote_object"].responseMap,
            {})
        
            
    def test_remove_remote_client(self):
        clients = self.target._MessageDispatcher__clients
        clients["remote_object"] = "mock"
        self.target.remove_remote_client("remote_object")

        self.assertEqual(self.target._MessageDispatcher__clients, {})

    def test_get_local_object_ids(self):
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": None,
             "local_object": None}

        self.result = self.target.get_local_object_ids()

        self.assertEqual(
            self.result, ["remote_object", "local_object"])

    def test_set_remote_system_manager(self):
        clients = self.target._MessageDispatcher__clients
        self.result = self.target.set_remote_system_manager()

        self.assertNotEqual(self.target._MessageDispatcher__clients, {})
        self.assertEqual(
            clients["systemmanager"].responseMap,
            {})

    def test_request_sync(self):
        with patch("org.o3project.odenos.remoteobject.transport." +
                   "remote_message_transport.RemoteMessageTransport." +
                   "send_request_message") as mock_send_request_message:
            request = Request("object_id", "method", "path")
            mock_send_request_message.return_value = "send_request_message"

            self.result = self.target.request_sync(request)

            self.assertEqual(self.result, "send_request_message")

    def test_publish_event_async(self):
        event = Event("publisher_id", "event_type", "event_body")

        self.target.publish_event_async(event)
        self.result = self.target._MessageDispatcher__pubsqueue.get()

        self.assertEqual(
            self.result.trans, None)
        self.assertEqual(
            self.result.type, 2)
        self.assertEqual(
            self.result.sno, 0)
        self.assertEqual(
            self.result.channel, "publisher_id:event_type")

    def test_subscribe_event(self):
        # TODO: this requires some work...
        with patch("org.o3project.odenos.remoteobject.transport." +
                    "remote_message_transport.RemoteMessageTransport." +
                    "send_request_message") as mock_send_request_message:
             mock_send_request_message.return_value = "send_request_message"
             subscription_map =\
                 self.target._MessageDispatcher__subscription_map
             self.value = EventSubscription("EventSubscription01",
                                            {"key01": ["value01"]})
             subscription_map._EventSubscriptionMap__subscriptions =\
                 {"EventSubscription01": set(["key01:value01",
                                              "key01:value02"]),
                  "EventSubscription": set(["key01:value01",
                                            "key01:value02"])}
             self.target._EventSubscriptionMap__subscription_map =\
                 {"key01:value01": set(["EventSubscription01",
                                        "EventSubscription02"]),
                  "key01:value02": set(["EventSubscription01",
                                        "EventSubscription02"])}
 
             self.result = self.target.subscribe_event(self.value)
        self.assertEqual(self.result, "send_request_message")

    def test_pushPublishQueue(self):
        self.target.pushPublishQueue(None, 2,
                                     1, "channel", "data")
        self.result = self.target._MessageDispatcher__pubsqueue.get()

        self.assertEqual(
            self.result.trans, None)
        self.assertEqual(
            self.result.type, 2)
        self.assertEqual(
            self.result.sno, 1)
        self.assertEqual(
            self.result.channel, "channel")
        self.assertEqual(
            self.result.data, "data")

    def test_dispatch_request_object_id_in_local_objects(self):
        request = Request("remote_object", "method", "path")
        remote_object = RemoteObject("remote_object", self.target)
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": remote_object}

        with patch("org.o3project.odenos.remoteobject." +
                   "remote_object.RemoteObject." +
                   "dispatch_request") as mock_dispatch_request:
            mock_dispatch_request.return_value = "mock_dispatch_request"

            self.result = self.target.dispatch_request(request)

        self.assertEqual(
            self.result, "mock_dispatch_request")

    def test_dispatch_request_object_id_not_in_local_objects(self):
        request = Request("NoneID", "method", "path")
        remote_object = RemoteObject("remote_object", self.target)
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": remote_object}

        self.result = self.target.dispatch_request(request)

        self.assertEqual(
            self.result.status_code, 404)
        self.assertEqual(
            self.result.body, None)

    def test_dispatch_event(self):
        event = Event("key01", "value01", "event_body")
        subscription_map =\
            self.target._MessageDispatcher__subscription_map
        subscription_map._EventSubscriptionMap__subscriptions =\
            {"remote_object": set(["key01:value01",
                                   "key01:value02"]),
             "local_object": set(["key01:value01",
                                  "key01:value02"])}
        subscription_map._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["remote_object",
                                   "local_object"]),
             "key01:value02": set(["remote_object",
                                   "local_object"])}
        remote_object = RemoteObject("remote_object", self.target)
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": remote_object}

        with patch("org.o3project.odenos.remoteobject." +
                   "remote_object.RemoteObject." +
                   "dispatch_event") as mock_dispatch_event:
            self.target.dispatch_event("key01:value01", event)

        mock_dispatch_event.assert_called_once_with(event)

    def test_dispatch_event_id_equal_del_obj_ids(self):
        event = Event("key01", "value01", "event_body")
        subscription_map =\
            self.target._MessageDispatcher__subscription_map
        subscription_map._EventSubscriptionMap__subscriptions =\
            {"remote_object": set(["key01:value01",
                                   "key01:value02"]),
             "local_object": set(["key01:value01",
                                  "key01:value02"])}
        subscription_map._EventSubscriptionMap__subscription_map =\
            {"key01:value01": set(["remote_object",
                                   "local_object"]),
             "key01:value02": set(["remote_object",
                                   "local_object"])}
        self.target._MessageDispatcher__local_objects =\
            {"remote_object": None}

        with patch(self.DISPATCHER_PATH +
                   ".EventSubscriptionMap" +
                   ".remove_subscription") as mock_remove_subscription:
            self.target.dispatch_event("key01:value01", event)

        mock_remove_subscription.assert_called_once_with("remote_object")

if __name__ == "__main__":
    unittest.main()
