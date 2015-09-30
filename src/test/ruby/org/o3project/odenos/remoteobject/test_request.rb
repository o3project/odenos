
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

require "odenos/remoteobject/request"

class TestRequest < MiniTest::Test
  include Odenos::Core
  
  def setup
    array = Array["remote_object_id", :GET, "path", "*", "body"]
    @target = Request.new(array)
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize_with_success
    @target = Request.new("remote_object_id", :DELETE, "path", "*", "body")
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:DELETE, @target.request_method)
  end
  
  def test_initialize_with_success_array
    array = Array["remote_object_id", :GET, "path", "*", "body"]
    @target = Request.new(array)
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:GET, @target.request_method)
  end

  def test_initialize_with_success_action_string_get
    array = Array["remote_object_id", "GET", "path", "*", nil]
    @target = Request.new(array)
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:GET, @target.request_method)
  end

  def test_initialize_with_success_action_string_put
    array = Array["remote_object_id", "PUT", "path", "*", "body"]
    @target = Request.new(array)
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:PUT, @target.request_method)
  end

  def test_initialize_with_success_action_string_post
    array = Array["remote_object_id", "POST", "path", "*", "body"]
    @target = Request.new(array)
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:POST, @target.request_method)
  end

  def test_initialize_with_success_action_string_delete
    array = Array["remote_object_id", "DELETE", "path", "*", nil]
    @target = Request.new(array)
    assert_instance_of(Odenos::Core::Request, @target)
    assert_equal(:DELETE, @target.request_method)
  end

  def test_initialize_with_not_array
    assert_raises(ArgumentError){Request.new(200)}
    assert_raises(ArgumentError, "expect Array"){Request.new(200)}
  end
  
  def test_initialize_with_few_array
    array = Array["remote_object_id", :PUT, "path"]
    assert_raises(ArgumentError){Request.new(array)}
    assert_raises(ArgumentError, "expect Request Array(5)"){Request.new(array)}
  end
  
  def test_initialize_with_more_array
    array = Array["remote_object_id", "POST", "path", "*", "body", "test"]
    assert_raises(ArgumentError){Request.new(array)}
    assert_raises(ArgumentError, "expect Request Array(5)"){Request.new(array)}
  end
  
  def test_initialize_with_illegal_method
    array = Array["remote_object_id", "GETTER", "path", "body"]
    assert_raises(ArgumentError){Request.new(array)}
    assert_raises(ArgumentError, "Invalid method: GETTER"){Request.new(array)}
  end
  
  def test_initialize_with_few_arguments
    assert_raises(ArgumentError){Request.new("remote_object_id", :DELETE)}
    assert_raises(ArgumentError, "expect 1 or 4 arguments"){Request.new("remote_object_id", :DELETE)}
  end
  
  def test_initialize_with_illegal_objectId
    assert_raises(ArgumentError){Request.new(300, :DELETE, "path", "body")}
    assert_raises(ArgumentError, "remote_object_id is a String"){Request.new(300, :DELETE, "path", "body")}
  end
  
  def test_initialize_with_notSymbol_method
    assert_raises(ArgumentError){Request.new("remote_object_id", "POST", "path", "body")}
    assert_raises(ArgumentError, "request_method is a Symbol"){Request.new("remote_object_id", "POST", "path", "body")}
  end
  
  def test_initialize_with_illegal_path
    assert_raises(ArgumentError){Request.new("remote_object_id", :POST, 400, "body")}
    assert_raises(ArgumentError, "path is a String"){Request.new("remote_object_id", :POST, 400, "body")}
  end
  
end
