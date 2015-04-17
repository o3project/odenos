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

import redis
import threading
import uuid
from io import BytesIO
import Queue
import msgpack
import logging
import traceback
from concurrent import futures

from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.event import Event

from org.o3project.odenos.remoteobject.transport.local_message_transport import (
    LocalMessageTransport
)
from org.o3project.odenos.remoteobject.transport.remote_message_transport import (
    RemoteMessageTransport
)


class MessageDispatcher:
    TYPE_REQUEST = 0
    TYPE_RESPONSE = 1
    TYPE_EVENT = 2
    REDIS_SERVER = "localhost"
    REDIS_PORT = 6379
    SYSTEM_MANAGER_ID = "systemmanager"
    TIME_RETRY_INTERVAL = 100

    class EventSubscriptionMap:

        def __init__(self):
            self.__subscriptions = {}
            self.__subscription_map = {}

        def get_subscriptions_keys(self):
            return self.__subscription_map.keys()

        def set_subscription(self, subscription):
            subscriber = subscription.subscriber_id
            for publisherid in subscription.event_filters.keys():
                for event_type in subscription.event_filters[publisherid]:
                    key = publisherid + ":" + event_type
                    if key not in self.__subscription_map:
                        self.__subscription_map[key] = set()
                    self.__subscription_map[key].add(subscriber)
                    if subscriber not in self.__subscriptions:
                        self.__subscriptions[subscriber] = set()
                    self.__subscriptions[subscriber].add(key)

        def remove_subscription(self, subscriber):
            if subscriber in self.__subscriptions:
                keys = self.__subscriptions[subscriber]
                del self.__subscriptions[subscriber]
            else:
                keys = set()
            for key in self.__subscription_map.copy():
                subscribers = self.__subscription_map[key]
                if subscriber in subscribers:
                    subscribers.remove(subscriber)
                if len(subscribers) == 0:
                    del self.__subscription_map[key]
            return keys

        def get_subscribers(self, *args):
            if len(args) == 0:
                return set(self.__subscriptions.keys())
            else:
                publisher_id, event_type = args
                key = publisher_id + ":" + event_type
                subscribers = self.__subscription_map[key]
                return subscribers

        def clear(self):
            self.__subscriptions.clear()
            self.__subscription_map.clear()

    class PublishData:

        def __init__(self, trans, type_, sno, channel, data):
            self.trans = trans
            self.type = type_
            self.sno = sno
            self.channel = channel
            self.data = data

    def __init__(self, system_manager_id=SYSTEM_MANAGER_ID,
                 redis_server=REDIS_SERVER,
                 redis_port=REDIS_PORT):
        self.__clients = {}
        self.__local_objects = {}
        self.__event_manager_id = None
        self.system_manager_id = system_manager_id
        self.__redisServer = redis_server
        self.__redisPort = redis_port
        self.__sourceDispatcherId = str(uuid.uuid4())
        self.__pubsqueue = Queue.Queue()
        self.__redisSubscriberThread = None
        self.__redisSubscriber = None
        self.__redisPublisherThread = None
        self.__redisPubliser = None
        self.__subscription_map = self.EventSubscriptionMap()

    def __redisPublisherRunnable(self):
        while (True):
            pubs = self.__pubsqueue.get()
            if pubs is None:
                break
            try:
                self.__redisPubliser.publish(pubs.channel, pubs.data)
            except:
                if pubs.type == self.TYPE_REQUEST:
                    if pubs.trans is not None:
                        pubs.trans.signalResponse(
                            pubs.sno,
                            Response(Response.StatusCode.NOT_FOUND, None))
                pass

    def __redisSubscriberRunnable(self):
        for mesg in self.__redisSubscriber.listen():
            if mesg['type'] != 'message':
                continue
            try:
                bio = BytesIO()
                bio.write(mesg['data'])
                bio.seek(0)
                upk = msgpack.Unpacker(bio)
                tp = upk.unpack()
                sno = upk.unpack()
                srcid = upk.unpack()
                if tp == self.TYPE_REQUEST:
                    request = Request.create_from_packed(upk.unpack())

                    def request_runnable(request, sno, srcid):
                        try:
                            response = self.dispatch_request(request)
                            pk = msgpack.Packer()
                            resb = bytearray()
                            resb.extend(pk.pack(self.TYPE_RESPONSE))
                            resb.extend(pk.pack(sno))
                            resb.extend(pk.pack(request.object_id))
                            resb.extend(pk.pack(response.packed_object()))
                            self.__pubsqueue.put(
                                MessageDispatcher.PublishData(None,
                                                              self.TYPE_RESPONSE,
                                                              sno,
                                                              srcid,
                                                              resb))
                        except:
                            logging.exception('Request processing error')
                    self.thread_pool.submit(request_runnable,
                                            request,
                                            sno,
                                            srcid)
                elif tp == self.TYPE_RESPONSE:
                    trans = self.__clients[srcid]
                    response = Response.create_from_packed(upk.unpack())
                    trans.signalResponse(sno, response)
                elif tp == self.TYPE_EVENT:
                    event = Event.create_from_packed(upk.unpack())

                    def event_runnable(event):
                        try:
                            self.dispatch_event(event)
                        except:
                            logging.exception('Event processing error')
                    self.thread_pool.submit(event_runnable, event)
            except:
                logging.error(traceback.format_exc())
                pass

    def start(self):
        self.thread_pool = futures.ThreadPoolExecutor(max_workers=8)
        self.__redisPubliser = redis.StrictRedis(host=self.__redisServer,
                                                 port=self.__redisPort)
        self.__redisSubscriber = redis.StrictRedis(
            host=self.__redisServer,
            port=self.__redisPort).pubsub()
        self.__update_subscriber()

        self.__redisSubscriberThread = threading.Thread(
            target=self.__redisSubscriberRunnable)
        self.__redisSubscriberThread.start()

        self.__redisPublisherThread = threading.Thread(
            target=self.__redisPublisherRunnable)
        self.__redisPublisherThread.start()

    def stop(self):
        if not (self.__redisSubscriberThread is None):
            self.__redisSubscriber.unsubscribe()
            self.__redisSubscriberThread.join()
            self.__redisSubscriberThread = None
        if not (self.__redisPublisherThread is None):
            self.__pubsqueue.put(None)
            self.__redisPublisherThread.join()
            self.__redisPublisherThread = None

    def join(self):
        if not (self.__redisSubscriberThread is None):
            self.__redisSubscriberThread.join()
            self.__redisSubscriberThread = None
        if not (self.__redisPublisherThread is None):
            self.__redisPublisherThread.join()
            self.__redisPublisherThread = None

    def close(self):
        self.stop()
        self.__local_objects.clear()
        self.__clients.clear()

    def __get_message_client(self, object_id):
        if object_id not in self.__clients:
            if object_id in self.__local_objects:
                client = LocalMessageTransport(object_id, self)
            else:
                client = RemoteMessageTransport(object_id, self)
            self.__clients[object_id] = client
        return self.__clients[object_id]

    def get_source_dispatcher_id(self):
        return self.__sourceDispatcherId

    def getRedisPublisher(self):
        return self.__redisPubliser

    def __update_subscriber(self):
        if self.__redisSubscriber is not None:
            keys = []
            keys.append(self.__sourceDispatcherId)
            for id_ in self.__local_objects.keys():
                keys.append(id_)
            for key in self.__subscription_map.get_subscriptions_keys():
                keys.append(key)
            self.__redisSubscriber.subscribe(keys)

    @property
    def event_manager_id(self):
        if not self.__event_manager_id:
            # TODO resolve id from SystemManager
            self.__event_manager_id = "eventmanager"
        return self.__event_manager_id

    def add_local_object(self, remote_object):
        self.__local_objects[remote_object.object_id] = remote_object
        self.__update_subscriber()

    def remove_local_object(self, remote_object):
        del self.__local_objects[remote_object.object_id]
        self.__subscription_map.remove_subscription(
            remote_object.object_id)
        self.__update_subscriber()

    def add_remote_client(self, object_id):
        client = RemoteMessageTransport(object_id, self)
        self.__clients[object_id] = client

    def remove_remote_client(self, object_id):
        self.__clients.pop(object_id)

    def get_local_object_ids(self):
        return self.__local_objects.keys()

    def set_remote_system_manager(self):
        self.__clients[self.system_manager_id] = \
            RemoteMessageTransport(self.system_manager_id, self)

    def request_sync(self, request):
        client = self.__get_message_client(request.object_id)
        logging.debug("[request_sync ]:send to " + client.object_id)
        return client.send_request_message(request)

    def publish_event_async(self, event):
        pk = msgpack.Packer()
        resb = bytearray()
        resb.extend(pk.pack(self.TYPE_EVENT))
        resb.extend(pk.pack(0))
        resb.extend(pk.pack(event.publisher_id + ":" + event.event_type))
        resb.extend(pk.pack(event.packed_object()))
        self.pushPublishQueue(None,
                              self.TYPE_EVENT,
                              0,
                              event.publisher_id + ":" + event.event_type,
                              resb)

    def subscribe_event(self, event_subscription):
        self.__subscription_map.remove_subscription(
            event_subscription.subscriber_id)
        self.__subscription_map.set_subscription(event_subscription)

        self.__update_subscriber()

        request = Request(self.event_manager_id,
                          Request.Method.PUT,
                          "settings/event_subscriptions/%s" % event_subscription.subscriber_id,
                          event_subscription)
        return self.request_sync(request)

    def pushPublishQueue(self, trans, type_, sno, channel, data):
        self.__pubsqueue.put(MessageDispatcher.PublishData(trans,
                                                           type_,
                                                           sno,
                                                           channel,
                                                           data))

    def dispatch_request(self, request):
        if request.object_id in self.__local_objects:
            return self.__local_objects[request.object_id].dispatch_request(
                request)
        else:
            return Response(404, None)

    def dispatch_event(self, event):
        subscribers = self.__subscription_map.get_subscribers(
            event.publisher_id,
            event.event_type)
        del_obj_ids = []
        for es in subscribers.copy():
            if es in self.__local_objects:
                if self.__local_objects[es] is None:
                    del_obj_ids.append(es)
                    continue
                self.__local_objects[es].dispatch_event(event)
        for id in del_obj_ids:
            del self.__local_objects[id]
            self.__subscription_map.remove_subscription(id)
