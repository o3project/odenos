
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

require "odenos/remoteobject/response"

class TestResponse < MiniTest::Test
  include Odenos::Core
  
  def setup
    @target = Response.new(Array[200, "body"])
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize_with_success
    @target = Odenos::Core::Response.new(200, "body")
    assert_instance_of(Odenos::Core::Response, @target)
  end
  
  def test_initialize_with_success_array
    @target = Response.new(Array[200, "body"])
    assert_instance_of(Odenos::Core::Response, @target)
  end
  
  def test_initialize_with_notarray
    assert_raises(ArgumentError){Response.new(200)}
    assert_raises(ArgumentError, "expect Array"){Response.new(200)}
  end
  
  def test_initialize_with_no_arguments
    assert_raises(ArgumentError){Response.new()}
    assert_raises(ArgumentError, "expect 1 or 2 arguments"){Response.new()}
  end
  
  def test_initialize_with_too_many_arguments
    assert_raises(ArgumentError){Response.new(Array[200, :GET, "body"])}
    assert_raises(ArgumentError, "expect 1 or 2 arguments"){Response.new(Array[200, :GET, "body"])}
  end
  
  def test_initialize_with_illegal_status_codeargument
    assert_raises(ArgumentError){Response.new(Array["200", "body"])}
    assert_raises(ArgumentError, "status_code is a Integer"){Response.new(Array["200", "body"])}
  end
    
end
