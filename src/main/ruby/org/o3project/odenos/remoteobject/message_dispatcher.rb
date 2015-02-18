
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

require 'redis'
require 'thread'
require 'concurrent'
require 'securerandom'
require 'msgpack'
require 'odenos/core/util/logger'

module Odenos
  module Core
    class MessageDispatcher
      TYPE_REQUEST = 0
      TYPE_RESPONSE = 1
      TYPE_EVENT = 2

      include Odenos::Util::Logger
      #
      # Generic Redis class can't change the subscription channel dynamically.
      # add new methods.
      class RedisDynSub < Redis
        def subscribe_add(*channels)
          client.instance_variable_get(:@client).write([['SUBSCRIBE',
                                                         *channels.map(
                                                           &:to_s)]])
        end

        def subscribe_del(*channels)
          client.instance_variable_get(:@client).write([['UNSUBSCRIBE',
                                                         *channels.map(
                                                           &:to_s)]])
        end
      end

      class EventSubscriptionMap
        def initialize
          @_subscriptions = {}
          @_subscription_map = {}
        end

        def get_subscriptions_keys
          @_subscription_map.keys
        end

        def set_subscription(subscription)
          subscriber = subscription.subscriber_id
          subscription.event_filters.each_key do| publisherid |
            subscription.event_filters[publisherid].each do| event_type |
              key = publisherid + ':' + event_type
              unless @_subscription_map.key?(key)
                @_subscription_map[key] = Array.new
              end
              @_subscription_map[key].push(subscriber)
              @_subscription_map[key].uniq!
              unless @_subscriptions.key?(subscriber)
                @_subscriptions[subscriber] = Array.new
              end
              @_subscriptions[subscriber].push(key)
              @_subscriptions[subscriber].uniq!
            end
          end
        end

        def remove_subscription(subscriber)
          if @_subscriptions.key?(subscriber)
            keys = @_subscriptions[subscriber]
          else
            keys = []
          end
          @_subscriptions.delete(subscriber)
          @_subscription_map.each do|key, subscribers|
            subscribers.delete(subscriber)
            if subscribers.size == 0
              @_subscription_map.delete(key)
            end
          end
          keys
        end

        def get_subscribers(publisher_id, event_type)
          key = publisher_id + ':' + event_type
          @_subscription_map[key]
        end

        def clear
          @_subscriptions.clear
          @_subscription_map.clear
        end
      end

      class PublishData
        attr_accessor :trans, :type, :sno, :channel, :data
        def initialize(trans, type, sno, channel, data)
          @channel = channel
          @data = data
          @trans = trans
          @type = type
          @sno = sno
        end
      end

      attr_reader :system_manager_id
      def initialize(system_manager_id = 'systemmanager',
                     redis_server = 'localhost', redis_port = 6379)
        @clients = {}
        @redis_server = redis_server
        @redis_port = redis_port
        @source_dispatcher_id = SecureRandom.uuid
        @system_manager_id = system_manager_id
        @event_manager_id = 'eventmanager'
        @local_objects = {}
        @pubsqueue = Queue.new
        @redis_publisher = nil
        @redis_subscriber_thread = nil
        @subscription_map = EventSubscriptionMap.new
      end
      # start loop
      def start
        @thread_pool = Concurrent::CachedThreadPool.new

        @redis_subscriber_thread ||= Thread.new do
          begin
            @redis_subscriber = RedisDynSub.new(host: @redis_server,
                                                port: @redis_port)
            ch = Array.new
            ch << @source_dispatcher_id
            ch.concat(@local_objects.keys)
            @subscription_map.get_subscriptions_keys.each do |key|
              ch.concat(key)
            end
            @redis_subscriber.subscribe(ch) do |on|
              on.message do |_channel, message|
                @unpacker ||= MessagePack::Unpacker.new
                @unpacker.feed(message)
                type = @unpacker.read
                sno = @unpacker.read
                srcid = @unpacker.read
                case type
                when TYPE_REQUEST
                  request = Request.new(@unpacker.read)
                  @thread_pool << proc do
                    response = dispatch_request(request)
                    debug { "dispatch_request Response:#{response.to_a.inspect}" }
                    packer = MessagePack::Packer.new
                    packer.write(TYPE_RESPONSE)
                    packer.write(sno)
                    packer.write(request.remote_object_id)
                    packer.write(response)
                    packer.flush
                    push_publish_queue(nil, TYPE_RESPONSE,
                                       sno, srcid, packer.to_s)
                  end
                when TYPE_RESPONSE
                  trans = @clients[srcid]
                  response = Response.new(@unpacker.read)
                  trans.signal_response(sno, response)
                when TYPE_EVENT
                  event = Event.new(@unpacker.read)
                  @thread_pool << proc do
                    dispatch_event(event)
                  end
                end
              end
            end
          rescue Redis::BaseConnectionError
            sleep 1
            retry
          ensure
            @redis_subscriber.quit
            @redis_subscriber = nil
          end
        end
        @redis_publisher = Redis.new(host: @redis_server, port: @redis_port)
        @redis_publisher_thread ||= Thread.new do
          begin
            while :true
              pubs = @pubsqueue.pop
              begin
                @redis_publisher.publish(pubs.channel, pubs.data)
              rescue
                if pubs.type == TYPE_REQUEST
                  unless pubs.trans.nil?
                    pubs.trans.signal_response(pubs.sno,
                                               Response.new(Response.NOT_FOUND,
                                                            nil))
                  end
                end
              end
            end
          rescue
            sleep 1
            retry
          end
        end
      end

      def stop
        @thread_pool.shutdown
        if @redis_subscriber_thread
          @redis_subscriber_thread.kill
          @redis_subscriber_thread = nil
        end
        if @redis_publisher_thread
          @redis_publisher.quit
          @redis_publisher_thread.kill
          @redis_publisher_thread = nil
        end
      end

      def close
        stop
        @clients.each_value(&:close)
        @clients.clear
      end

      def set_remote_system_manager
        debug { 'creating RemoteMessageTransport' }
        @clients[@system_manager_id] = RemoteMessageTransport.new(
                                           @system_manager_id,
                                           self)
      end

      # @param [RemoteObject] remote_object
      def add_local_object(remote_object)
        @local_objects[remote_object.remote_object_id] = remote_object
        if @redis_subscriber
          @redis_subscriber.subscribe_add(remote_object.remote_object_id)
        end
      end

      # @param [RemoteObject] remote_object
      def remove_local_object(remote_object)
        @local_objects.delete(remote_object.remote_object_id)
        @subscription_map.remove_subscription(remote_object.remote_object_id)
        if @redis_subscriber
          @redis_subscriber.subscribe_del(remote_object.remote_object_id)
        end
      end

      # @param [String] remote_object_id
      def add_remote_client(remote_object_id)
        @clients[remote_object_id] = RemoteMessageTransport.new(
                                         remote_object_id,
                                         self)
      end

      # @param [String] remote_object_id
      def remove_remote_client(remote_object_id)
        @clients.delete(remote_object_id)
      end

      # @param [Request] request
      def request_sync(request)
        client = get_message_client(request.remote_object_id)
        client.send_request_message(request)
      end

      # @param [Event] event
      def publish_event_async(event)
        packer = MessagePack::Packer.new
        packer.write(TYPE_EVENT)
        packer.write(0)
        packer.write(@source_dispatcher_id)
        packer.write(event)
        packer.flush
        push_publish_queue(nil, TYPE_EVENT, 0,
                           event.publisher_id + ':' + event.event_type,
                           packer.to_s)
      end
      # @param [EventSubscription] event_subscription
      # @return [Future]
      def subscribe_event(event_subscription)
        debug { "subscribe_event( #{event_subscription.to_hash.inspect} )" }
        @subscription_map.set_subscription(event_subscription)
        @subscription_map.get_subscriptions_keys.each do|key|
          @redis_subscriber.subscribe_add(key) if @redis_subscriber
        end

        request = Request.new(event_manager_id, :PUT,
                              "settings/event_subscriptions/#{event_manager_id}",
                              event_subscription)
        request_sync(request)

        Response.new(Response::OK, nil)
      end

      def push_publish_queue(trans, type, sno, chanel, data)
        @pubsqueue.push(PublishData.new(trans, type, sno, chanel, data))
      end
      # @param [Request] request
      def dispatch_request(request)
        debug { "dispatch_request( #{request.to_a.inspect} )" }

        if @local_objects.key?(request.remote_object_id)
          object = @local_objects[request.remote_object_id]
          return object.dispatch_request(request)
        end
        Response.new(404, nil)
      end
      # @param [Event] event
      def dispatch_event(event)
        debug { "dispatch_event( #{event.to_a.inspect} )" }
        del_obj_ids = []
        subscribers = @subscription_map.get_subscribers(event.publisher_id,
                                                        event.event_type)
        subscribers.each do|subscriber|
          if @local_objects.key?(subscriber)
            object = @local_objects[subscriber]
            if object.nil?
              del_obj_ids.push(subscriber)
            else
              object.dispatch_event(event)
            end
          end
          for id in del_obj_ids do
            @local_objects.delete(id)
            @subscription_map.remove_subscription(id)
          end
        end
      end
      # @!attribute [r] event_manager_id
      #   @return [String] Event Manager's RemoteObject ID
      attr_reader :event_manager_id

      def get_redis_publisher
        @redis_publisher
      end

      def get_source_dispatcher_id
        @source_dispatcher_id
      end

      protected

      # @return [BaseMessageTransport]
      def get_message_client(remote_object_id)
        unless @clients.include? remote_object_id
          if @local_objects.include? remote_object_id
            add_local_object LocalMessageTransport.new(remote_object_id, self)
          else
            add_remote_client(remote_object_id)
          end
        end
        @clients[remote_object_id]
      end
    end
  end
end
