
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

$LOAD_PATH.unshift File.expand_path(File.join(File.dirname(__FILE__), "../../../../../main/ruby/org/o3project"))

require 'minitest'
require 'minitest/unit'
require 'minitest/autorun'
require 'mocha/mini_test'

require "odenos/remoteobject/message_dispatcher"
require "odenos/remoteobject/event"
require "odenos/remoteobject/request"
require "odenos/remoteobject/response"
require "odenos/remoteobject/transport"
require "odenos/remoteobject/remote_object"

class TestMessageDispatcher < MiniTest::Test
  include Odenos::Core

  THREAD_WAIT = 0.3
 
  def setup
    @target = MessageDispatcher.new("systemManagerId")
  end
  
  def teardown
    @target = nil
  end

  def test_initialize_with_arguments
    @target = MessageDispatcher.new("sysMgrId", "local01", 12345)
    assert_instance_of(Hash, @target.instance_variable_get(:@clients))
    assert_equal("local01", @target.instance_variable_get(:@redis_server))
    assert_equal(12345, @target.instance_variable_get(:@redis_port))
    assert_equal("sysMgrId", @target.instance_variable_get(:@system_manager_id))
    assert_equal("eventmanager", @target.instance_variable_get(:@event_manager_id))
    assert_instance_of(Hash, @target.instance_variable_get(:@local_objects))
    assert_instance_of(Queue, @target.instance_variable_get(:@pubsqueue))
    assert_nil(@target.instance_variable_get(:@redis_publisher))
    assert_nil(@target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(MessageDispatcher::EventSubscriptionMap, @target.instance_variable_get(:@subscription_map)) 
  end
  
  def test_initialize_with_no_arguments
    @target = MessageDispatcher.new()
    assert_instance_of(Hash, @target.instance_variable_get(:@clients))
    assert_equal("localhost", @target.instance_variable_get(:@redis_server))
    assert_equal(6379, @target.instance_variable_get(:@redis_port))
    assert_equal("systemmanager", @target.instance_variable_get(:@system_manager_id))
    assert_equal("eventmanager", @target.instance_variable_get(:@event_manager_id))
    assert_instance_of(Hash, @target.instance_variable_get(:@local_objects))
    assert_instance_of(Queue, @target.instance_variable_get(:@pubsqueue))
    assert_nil(@target.instance_variable_get(:@redis_publisher))
    assert_nil(@target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(MessageDispatcher::EventSubscriptionMap, @target.instance_variable_get(:@subscription_map))      
  end

  def test_start    
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    mock_redis = mock()
    mock_redis.expects(:quit).returns().once
    MessageDispatcher::RedisDynSub.stubs(:new).returns(mock_redis)

    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_instance_of(Redis, @target.instance_variable_get(:@redis_publisher))
  end

  def test_start_request
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
      
    mock_redis = mock()
    on = mock()
    msg = MessagePack::Packer.new
    msg.write(MessageDispatcher::TYPE_REQUEST)
    msg.write(1)
    msg.write("remote_object_id")
    msg.write(Request.new('remote_object_id', :GET, 'property', nil))
    msg.flush
    on.stubs(:message).yields('aaa', msg)
    mock_redis.stubs(:subscribe).yields(on)
    MessageDispatcher::RedisDynSub.stubs(:new).returns(mock_redis)

    response = Response.new(Response::OK, nil)
    @target.expects(:dispatch_request).with(instance_of(Request)).returns(response).once
    @target.expects(:push_publish_queue).with(nil,
                                              MessageDispatcher::TYPE_RESPONSE,
                                              1,
                                              "remote_object_id",
                                              anything).returns().once

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_instance_of(Redis, @target.instance_variable_get(:@redis_publisher))
  end

  def test_start_response
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
      
    mock_redis = mock()
    on = mock()
    msg = MessagePack::Packer.new
    msg.write(MessageDispatcher::TYPE_RESPONSE)
    msg.write(1)
    msg.write("remote_object_id")
    msg.write(Response.new(Response::OK, nil))
    msg.flush
    on.stubs(:message).yields('aaa', msg)
    mock_redis.stubs(:subscribe).yields(on)
    MessageDispatcher::RedisDynSub.stubs(:new).returns(mock_redis)
    
    object = RemoteMessageTransport.new("remote_object_id", @target)
    object.expects(:signal_response)
    client = {"remote_object_id" => object}
    @target.instance_variable_set(:@clients, client) 

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_instance_of(Redis, @target.instance_variable_get(:@redis_publisher))
  end
  
  def test_start_event
    obj = mock()
    local_obj = {"publisher_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
      
    mock_redis = mock()
    on = mock()
    msg = MessagePack::Packer.new
    msg.write(MessageDispatcher::TYPE_EVENT)
    msg.write(1)
    msg.write("publisher_id")
    msg.write(Event.new("publisher_id", "event_type", "body"))
    msg.flush
    on.stubs(:message).yields('aaa', msg)
    mock_redis.stubs(:subscribe).yields(on)
    MessageDispatcher::RedisDynSub.stubs(:new).returns(mock_redis)
    @target.expects(:dispatch_event).with(instance_of(Event)).returns().once

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_instance_of(Redis, @target.instance_variable_get(:@redis_publisher))
  end

  def test_start_with_thread_existed
    dummy_thread = mock()
    @target.instance_variable_set(:@redis_subscriber_thread, dummy_thread)
    @target.instance_variable_set(:@redis_publisher_thread, dummy_thread)
    
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)

    @target.start
    
    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_equal(dummy_thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_nil(@target.instance_variable_get(:@redis_subscriber))
    assert_equal(dummy_thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_instance_of(Redis, @target.instance_variable_get(:@redis_publisher))
  end
  
  def test_start_with_publish 
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    @target.push_publish_queue("trans", "type", "sno", "channel", "data")
    
    publisher = mock()
    publisher.expects(:publish)
    Redis.stubs(:new).returns(publisher)

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
  end
  
  def test_start_with_base_connection_error
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
      
    mock_redis = mock()
    on = mock()
    msg = MessagePack::Packer.new
    msg.write(MessageDispatcher::TYPE_RESPONSE)
    msg.write(1)
    msg.write("remote_object_id")
    msg.write(Response.new(Response::OK, nil))
    msg.flush
    on.stubs(:message).yields('aaa', msg)
    mock_redis.stubs(:subscribe).yields(on)
    MessageDispatcher::RedisDynSub.stubs(:new).returns(mock_redis)
    
    object = RemoteMessageTransport.new("remote_object_id", @target)
    object.expects(:signal_response).raises(Redis::BaseConnectionError).at_least_once

    client = {"remote_object_id" => object}
    @target.instance_variable_set(:@clients, client) 

    Redis.stubs(:new).returns(nil)

    @target.start
    sleep THREAD_WAIT + 1

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
    assert_nil(@target.instance_variable_get(:@redis_publisher))
  end 
  
  def test_start_with_publish_exception
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    @target.push_publish_queue("trans", "type", "sno", "channel", "data")
    
    publisher = mock()
    publisher.expects(:publish).raises(RuntimeError)
    Redis.stubs(:new).returns(publisher)

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
  end
  
  def test_start_with_publish_exception_type_request
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
  
    trans = RemoteMessageTransport.new("remote_object_id", @target)
    @target.push_publish_queue(trans, MessageDispatcher::TYPE_REQUEST, "sno", "systemManagerId", "data")
    
    publisher = mock()
    publisher.expects(:publish).raises(RuntimeError)
    Redis.stubs(:new).returns(publisher)

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
  end

  def test_start_with_exception   
    map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    map.set_subscription(event_subscription)
    @target.instance_variable_set(:@subscription_map, map)
    
    obj = mock()
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    @target.push_publish_queue("trans", "type", "sno", "channel", "data")
    
    publisher = mock()
    publisher.expects(:publish).never
    Redis.stubs(:new).returns(publisher)
    
    queue = mock()
    queue.expects(:pop).raises(RuntimeError).at_least_once
    @target.instance_variable_set(:@pubsqueue, queue)

    @target.start
    sleep THREAD_WAIT

    assert_instance_of(Concurrent::CachedThreadPool, @target.instance_variable_get(:@thread_pool))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_subscriber_thread))
    assert_instance_of(Thread, @target.instance_variable_get(:@redis_publisher_thread))
  end

  def test_stop
    @target.start
    publisher = Minitest::Mock.new
    publisher.expect(:quit, nil)
    @target.instance_variable_set(:@redis_publisher, publisher)
    @target.instance_variable_get(:@thread_pool).expects(:shutdown)
    
    @target.stop
    
    assert_nil(@target.instance_variable_get(:@redis_subscriber_thread))
    assert_nil(@target.instance_variable_get(:@redis_publisher_thread))
  end
  
  def test_close   
    @target.expects(:stop).once
    @target.start
    @target.add_remote_client("remote_object_id")
    refute_equal({}, @target.instance_variable_get(:@clients))
    
    @target.close
    
    assert_equal({}, @target.instance_variable_get(:@clients))
  end
  
  def test_set_remote_system_manager
    @target.set_remote_system_manager
    map = @target.instance_variable_get(:@clients)
    assert_instance_of(RemoteMessageTransport, map["systemManagerId"])
  end
  
  def test_add_local_object
    redis_subscriber = Odenos::Core::MessageDispatcher::RedisDynSub.new
    redis_subscriber = stub(:subscribe_add=>nil)
    @target.instance_variable_set(:@redis_subscriber, redis_subscriber)
    
    remote_object = mock()
    remote_object.expects(:remote_object_id).returns("remote_object_id").times(2)
    
    assert_equal({}, @target.instance_variable_get(:@local_objects))

    @target.add_local_object(remote_object)

    local_object = @target.instance_variable_get(:@local_objects)
    assert_equal(remote_object, local_object["remote_object_id"])
  end

  def test_add_local_object_without_redis
    remote_object = mock()
    remote_object.expects(:remote_object_id).returns("remote_object_id").once
    
    assert_equal({}, @target.instance_variable_get(:@local_objects))

    @target.add_local_object(remote_object)
    
    local_object = @target.instance_variable_get(:@local_objects)
    assert_equal(remote_object, local_object["remote_object_id"])
  end
 
  def test_remove_local_object
    redis_subscriber = Odenos::Core::MessageDispatcher::RedisDynSub.new
    redis_subscriber = stub(:subscribe_add=>nil, :subscribe_del=>nil)
    @target.instance_variable_set(:@redis_subscriber, redis_subscriber)

    remote_object = mock()
    # add 2times + remove 3times
    remote_object.expects(:remote_object_id).returns("remote_object_id").times(5)
    
    @target.add_local_object(remote_object)
    
    local_object = @target.instance_variable_get(:@local_objects)
    assert_equal(remote_object, local_object["remote_object_id"])
      
    @target.remove_local_object(remote_object)

    assert_equal({}, @target.instance_variable_get(:@local_objects))
  end
  
  def test_remove_local_object_without_redis
    remote_object = mock()
    # add once + remove 2times
    remote_object.expects(:remote_object_id).returns("remote_object_id").times(3)
    
    @target.add_local_object(remote_object)
    
    local_object = @target.instance_variable_get(:@local_objects)
    assert_equal(remote_object, local_object["remote_object_id"])
      
    @target.remove_local_object(remote_object)
    
    assert_equal({}, @target.instance_variable_get(:@local_objects))
  end
  
  def test_add_remoto_client
    remote_object_id = "remote_object_id"
    
    @target.add_remote_client(remote_object_id)
    
    client = @target.instance_variable_get(:@clients)
    assert_instance_of(RemoteMessageTransport, client[remote_object_id])
  end

  def test_remove_remote_client
    @target.add_remote_client("remote_object_id")
    client = @target.instance_variable_get(:@clients)
    refute_nil(client["remote_object_id"])
      
    @target.remove_remote_client("remote_object_id")
    
    client = @target.instance_variable_get(:@clients)
    assert_nil(client["remote_object_id"])
  end  
  
  def test_request_sync
    @request = Odenos::Core::Request.new("obj_id", :GET, "test/request", "body")
    @mockclient = Minitest::Mock.new
    @mockclient.expect(:send_request_message, nil, [@request])
    @target.stub(:get_message_client, @mockclient) do
    @target.request_sync(@request)
    end
  end

  def test_publish_event_async
    @mockevent = Minitest::Mock.new
    @event = Event.new("publisher_id", "event_type", "body")
    @request = Odenos::Core::Request.new("obj_id", :POST, "event", @event)
    @request.stubs(:to_msgpack).returns(nil)
    @target.expects(:push_publish_queue)
    @target.publish_event_async(@event)     
  end
 
  def test_subscribe_event
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    @map.set_subscription(event_subscription)

    redis_subscriber = Odenos::Core::MessageDispatcher::RedisDynSub.new
    redis_subscriber = stub(:subscribe_add=>nil)
    @target.instance_variable_set(:@redis_subscriber, redis_subscriber)

    @target.instance_variable_set(:@subscription_map, @map)
    @target.expects(:subscribe_event)

    @target.subscribe_event(event_subscription)
  end
  
  def test_subscribe_event_without_redis
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    event_subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    event_subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    event_subscription.add_filter("system_manager", "ComponentManagerChanged")
    @map.set_subscription(event_subscription)

    @target.instance_variable_set(:@subscription_map, @map)
  
    @target.expects(:subscribe_event)

    @target.subscribe_event(event_subscription)
  end
 
  def test_push_publish_queue
    @target.push_publish_queue("trans", "type", "sno", "channel", "data")
    
    assert_instance_of(Odenos::Core::MessageDispatcher::PublishData,
                       @target.instance_variable_get(:@pubsqueue).pop)
  end

  def test_dispatch_request_with_local_objects
    request = Request.new("obj_id", :GET, "test/request", "body")
    response = Response.new(200, "body")

    obj = mock()
    obj.expects(:dispatch_request).returns(response)
    local_obj = {"obj_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    assert_instance_of(Response, @target.dispatch_request(request))
  end
  
  def test_dispatch_request_without_local_object
    request = Request.new("obj_id", :GET, "test/request", "body")
    
    response = @target.dispatch_request(request)
    
    assert_nil(response.request_body)
    assert_equal(404, response.status_code)
  end

  def test_dispatch_event_with_local_object
    event = Event.new("publisher_id", "event_type", "body")

    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("publisher_id", "event_type")
    @map.set_subscription(subscription)
    @target.instance_variable_set(:@subscription_map, @map)
    obj = mock()
    obj.expects(:dispatch_event)
    local_obj = {"remote_object_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    @target.dispatch_event(event)
  end
  
  def test_dispatch_event_with_local_object_nil
    event = Event.new("publisher_id", "event_type", "body")

    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("publisher_id", "event_type")
    @map.set_subscription(subscription)
    @map.expects(:remove_subscription).at_least_once
    @target.instance_variable_set(:@subscription_map, @map)
    obj = mock()
    obj.expects(:dispatch_event).never
    local_obj = {"remote_object_id" => nil}
    local_obj.expects(:delete).at_least_once
    @target.instance_variable_set(:@local_objects, local_obj)
  
    @target.dispatch_event(event)
  end
  
  def test_dispatch_event_without_local_object
    event = Event.new("publisher_id", "event_type", "body")

    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("publisher_id", "event_type")
    @map.set_subscription(subscription)
    @target.instance_variable_set(:@subscription_map, @map)
    obj = mock()
    obj.expects(:dispatch_event).never
    local_obj = {"system_manager_id" => obj}
    @target.instance_variable_set(:@local_objects, local_obj)
    
    @target.dispatch_event(event)
  end   

  def test_get_redis_publisher
    assert_equal(@target.instance_variable_get(:@redis_publisher), @target.get_redis_publisher())
    
  end
  
  def test_get_source_dispatcher_id
    assert_equal(@target.instance_variable_get(:@source_dispatcher_id), @target.get_source_dispatcher_id())
  end

  def test_get_message_client_with_clients_has_remote_object_id
    object = BaseMessageTransport.new("remote_object_id")
    client = {"remote_object_id" => object}
    @target.instance_variable_set(:@clients, client)
  
    ret = @target.send(:get_message_client, "remote_object_id")
  
    assert_equal(object, ret)
  end

  def test_get_message_client_with_local_objects_has_remote_object_id
    object = BaseMessageTransport.new("remote_object_id")  
    local_object = {"remote_object_id" => object}
    @target.instance_variable_set(:@local_objects, local_object)

    @target.expects(:add_local_object).once
    
    ret = @target.send(:get_message_client, "remote_object_id")
    # local
    assert_equal(nil, ret)
  end
  
  def test_get_message_client_without_remote_object_id
    ret = @target.send(:get_message_client, "remote_object_id")
    assert_instance_of(RemoteMessageTransport, ret)
  end

  def test_redis_dyn_sub_subscribe_add
    @redis = Odenos::Core::MessageDispatcher::RedisDynSub.new()

    mock_client = mock()
    mock_client.expects(:write)
    @redis.client.instance_variable_set(:@client, mock_client)
    
    remote_object_id = "remote_object_id"
    
    @redis.subscribe_add(remote_object_id)
  end
  
  def test_redis_dyn_sub_subscribe_del
    @redis = Odenos::Core::MessageDispatcher::RedisDynSub.new()
    mock_client = mock()
    mock_client.expects(:write)
    @redis.client.instance_variable_set(:@client, mock_client)
  
    @redis.subscribe_del("remote_object_id")
  end

  def test_eventsubscriptionmap_initialize
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new
    
    assert_equal({}, @map.instance_variable_get(:@_subscriptions))
    assert_equal({}, @map.instance_variable_get(:@_subscription_map))
  end
  
  def test_event_subscription_map_get_subscription_keys
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new
    dummyhash = {"publisher_id" => "SystemManager", "event_type" => "ComponentManagerChanged", "event_body" => 100}
    @map.instance_variable_set(:@_subscription_map, dummyhash)
    
    array = @map.get_subscriptions_keys
  
    assert_equal(["publisher_id", "event_type", "event_body"], array)
  end
  
  def test_event_subscription_map_set_subscription
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("system_manager", "ComponentManagerChanged")
    subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")

    @map.set_subscription(subscription)
    
    sub_map = @map.instance_variable_get(:@_subscription_map)
    assert_equal(true, sub_map.key?("system_manager:ComponentManagerChanged"))
    assert_equal(["remote_object_id"], sub_map["system_manager:ComponentManagerChanged"])
    sub_table = @map.instance_variable_get(:@_subscriptions)
    assert_equal(true, sub_table.key?("remote_object_id"))
    assert_equal(["system_manager:ComponentManagerChanged", "system_manager:ComponentConnectionPropertyChanged"],
                 sub_table["remote_object_id"])
  end
  
  def test_event_subscription_map_remove_subscription
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("system_manager", "ComponentManagerChanged")
    subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    @map.set_subscription(subscription)
    
    subscription = EventSubscription.new(:subscriber_id => "local_object_id")
    subscription.add_filter("original_nw", "InPacketAdded")
    subscription.add_filter("original_nw", "LinkChanged")
    subscription.add_filter("original_nw", "PortChanged")
    subscription.add_filter("original_nw", "FlowChanged")
    subscription.add_filter("original_nw", "NodeChanged")
    subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    @map.set_subscription(subscription)
    
    ret = @map.remove_subscription("remote_object_id")
    
    assert_equal(["system_manager:ComponentManagerChanged", "system_manager:ComponentConnectionPropertyChanged"], ret)
 
    refute_equal({}, @map.instance_variable_get(:@_subscription_map))
    refute_equal({}, @map.instance_variable_get(:@_subscriptions))
           
    sub_table = @map.instance_variable_get(:@_subscriptions)
    assert_nil(sub_table["remote_object_id"]) 
    refute_nil(sub_table["local_object_id"])
  end
  
  def test_event_subscription_map_get_subscribers
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new
    key = 'component_manager' + ':' + 'ComponentManagerChanged'
    @map.instance_variable_get(:@_subscription_map)[key] = Array.new
    @map.instance_variable_get(:@_subscription_map)[key].push("subscriber_id")
    
    refute_nil(@map.get_subscribers('component_manager', 'ComponentManagerChanged'))
  end
  
  def test_event_subscription_map_clear
    @map = Odenos::Core::MessageDispatcher::EventSubscriptionMap.new()
    
    subscription = EventSubscription.new(:subscriber_id => "remote_object_id")
    subscription.add_filter("system_manager", "ComponentManagerChanged")
    subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    @map.set_subscription(subscription)
    
    subscription = EventSubscription.new(:subscriber_id => "local_object_id")
    subscription.add_filter("original_nw", "InPacketAdded")
    subscription.add_filter("original_nw", "LinkChanged")
    subscription.add_filter("original_nw", "PortChanged")
    subscription.add_filter("original_nw", "FlowChanged")
    subscription.add_filter("original_nw", "NodeChanged")
    subscription.add_filter("system_manager", "ComponentConnectionPropertyChanged")
    @map.set_subscription(subscription)

    @map.clear

    assert_equal({}, @map.instance_variable_get(:@_subscription_map))
    assert_equal({}, @map.instance_variable_get(:@_subscriptions))
  end

  def test_publishdata_initialize
    @publish_data = Odenos::Core::MessageDispatcher::PublishData.new("trans",
                                                                     "type",
                                                                     "sno",
                                                                     "channel",
                                                                     "data")
    assert_equal("channel", @publish_data.instance_variable_get(:@channel))
    assert_equal("data", @publish_data.instance_variable_get(:@data))
    assert_equal("trans", @publish_data.instance_variable_get(:@trans))
    assert_equal("type", @publish_data.instance_variable_get(:@type))
    assert_equal("sno", @publish_data.instance_variable_get(:@sno))
  end

end
