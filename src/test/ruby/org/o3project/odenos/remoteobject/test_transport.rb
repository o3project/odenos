
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

require "msgpack"
require "odenos/core/util/object_array"
require "odenos/remoteobject/transport"
require "odenos/remoteobject/request"
require "odenos/remoteobject/response"

class TestBaseMessageTransport < MiniTest::Test
  include Odenos::Core
  
  def setup
    @base_transport = Odenos::Core::BaseMessageTransport.new("remote_object_id")
  end
  
  def teardown
    @base_transport = nil
  end
  
  def test_base_initialize
    assert_equal("remote_object_id", @base_transport.instance_variable_get(:@remote_object_id))
  end
  
  def test_base_send_request_message
    @request = Minitest::Mock.new
    assert_raises(NotImplementedError){@base_transport.send_request_message(@request)}
  end
  
  def test_base_close
    assert_raises(NotImplementedError){@base_transport.close}
  end
end

class TestRemoteMessageTransport < MiniTest::Test
  include Odenos::Core
  
  def setup
    @dispatcher = mock()
    @remote_transport = Odenos::Core::RemoteMessageTransport.new("remote_object_id", @dispatcher)
  end
  
  def teardown
    @remote_transport = nil
  end
  
  def test_remote_initialize
    assert_equal("remote_object_id", @remote_transport.instance_variable_get(:@remote_object_id))
    assert(@remote_transport.instance_variable_get(:@dispatcher))
    assert_instance_of(Mutex, @remote_transport.instance_variable_get(:@lockseq))
    assert_instance_of(Hash, @remote_transport.instance_variable_get(:@response_map))
    assert_equal(0, @remote_transport.instance_variable_get(:@seqno))
  end
  
  def test_remote_send_request_message
    body = Object.new
    @request = Request.new("object_id", :PUT, "path", "*", body)
    @response = Response.new(200, body)
    @que = stub(:pop=>@response)
    @remote_transport.stubs(:add_request).returns(@que)

    assert_equal(@response, @remote_transport.send_request_message(@request))
  end

  def test_remote_send_request_message_exception
    body = Object.new
    @request = Request.new("object_id", :PUT, "path", "*", body)
    @que = Queue.new
    @que.expects(:pop).with().raises()
    @remote_transport.stubs(:add_request).returns(@que)

    assert_raises(RuntimeError) do
      @remote_transport.send_request_message(@request)
    end
  end

  def test_remote_send_request_message_with_notpush
    body = Object.new
    @request = Request.new("object_id", :PUT, "path", "*", body)
    @que = mock(:pop)
    @remote_transport.stubs(:add_request).returns(@que)
     
    assert_equal(nil, @remote_transport.send_request_message(@request))
  end
  
  def test_remote_close
    # do nothing
  end
  
  def test_remote_signalResponse_with_key
    sno = @remote_transport.send(:get_and_increment)
    response_map = Hash.new()
    que = stub(:push)
    response_map[sno] = que
    
    @remote_transport.instance_variable_set(:@response_map, response_map)    
    
    body = Object.new
    @response = Response.new(200, body)
    
    @remote_transport.signal_response(sno, @response)
    
    assert_equal(false, @remote_transport.instance_variable_get(:@response_map).value?(que))
  end
  
  def test_remote_signalResponse_with_nokey
    sno = @remote_transport.send(:get_and_increment)

    que = mock()
    que.expects(:push).never
  
    body = Object.new
    @response = Response.new(200, body)
  
    @remote_transport.signal_response(sno, @response)
  end
  
  def test_remote_get_and_increment
    ret = @remote_transport.send(:get_and_increment)
    assert_equal(@remote_transport.instance_variable_get(:@seqno)-1, ret)
  end
  
  def test_remote_get_and_increment_maxno
    @remote_transport.instance_variable_set(:@seqno, RemoteMessageTransport::FIXNUM_MAX)
    
    assert_equal(RemoteMessageTransport::FIXNUM_MAX, @remote_transport.send(:get_and_increment))
    assert_equal(RemoteMessageTransport::FIXNUM_MIN+1, @remote_transport.instance_variable_get(:@seqno))
  end
 
  def test_remote_add_request
    @dispatcher.expects(:get_source_dispatcher_id).once
    @dispatcher.expects(:push_publish_queue).once

    ret = @remote_transport.send(:get_and_increment)
    
    body = Object.new
    
    @request = Request.new("object_id", :GET, "path", "*", body)    
    @request.expects(:remote_object_id).once
    @request.stubs(:to_msgpack).returns(nil)
    
    @remote_transport.send(:add_request, @request)
    
    map = @remote_transport.instance_variable_get(:@response_map)
    
    assert_instance_of(Queue, map[ret+1])
  end

end

class TestLocalMessageTransport < MiniTest::Test
  include Odenos::Core
  
  def setup
    @dispatcher = Minitest::Mock.new
    @local_transport = Odenos::Core::LocalMessageTransport.new("remote_object_id", @dispatcher)
  end
  
  def teardown
    @local_transport = nil
    @dispatcher = nil
  end
  
  def test_local_initialize
    @local_transport = Odenos::Core::LocalMessageTransport.new("remote_object_id", @dispatcher)
    assert(@local_transport.instance_variable_get(:@dispatcher))
    assert_equal("remote_object_id", @local_transport.instance_variable_get(:@remote_object_id))
  end
  
  def test_local_close
    # do nothing
  end

  def test_local_send_request_message
    @packedrequest = Minitest::Mock.new
    @mockrequest = Minitest::Mock.new
    @mockresponse = Minitest::Mock.new
    @packedresponse = Minitest::Mock.new
    
    body = Object.new
    
    @request = Request.new("object_id", :GET, "path", "*", body)    
    @request.stubs(:pack).returns(@packedrequest)    
    Request.stubs(:unpack).returns(@mockrequest)
    
    @response = Response.new(200, body)
    @response.stubs(:pack).returns(@packedresponse) 
    Response.stubs(:unpack).returns(@response)
    
    @dispatcher.expect(:dispatch_request, @response, [Object])
    
    ret = @local_transport.send_request_message(@request)

    assert_equal(@response, ret)  
  end
end
