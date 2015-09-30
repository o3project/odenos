
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
require 'odenos/core/util/request_parser'
require "odenos/remoteobject/request"
require "odenos/remoteobject/response"


class TestRequestParser < MiniTest::Test
  def setup
    @target = Odenos::Util::RequestParser.new
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    assert_equal([], @target.instance_variable_get(:@rules))
  end
  
  def test_add_rule
    rule = Array.new
    rule = ["rule_value"]
      
    @target.add_rule(rule)
    
    assert_equal(["rule_value"], @target.instance_variable_get(:@rules))
    end
  
  def test_action_params_0
    proc_obj = Proc.new {
      "PARAMS: 0"
    }
    rule = Array.new
    regex_None = Regexp.new("Error")
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_None, "method"=> :POST,
             "func"=> proc_obj, "params"=> 0},
            {"pattern"=> regex_path, "method"=> :GET,
             "func"=> proc_obj, "params"=> 0},
            {"pattern"=> regex_path, "method"=> :POST,
             "func"=> proc_obj, "params"=> 0}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", "body")
    result = @target.action(request)
    assert_equal("PARAMS: 0", result)
  end
  
  def test_action_params_1_body_not_nil
    proc_obj = Proc.new { |*value|
      value
    }
    rule = Array.new
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_path, "method"=> :POST,
      "func"=> proc_obj, "params"=> 1}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", "body")
    result = @target.action(request)
    assert_equal(["body"], result)
  end
  
  def test_action_params_1_body_nil
    proc_obj = Proc.new { |*value|
      value
    }
    rule = Array.new
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_path, "method"=> :POST,
      "func"=> proc_obj, "params"=> 1}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", nil)
    result = @target.action(request)
    assert_equal(["Path"], result)
  end
  
  def test_action_params_2_body_not_nil
    proc_obj = Proc.new { |status_code, body|
      Odenos::Core::Response.new(status_code, body)
    }
    rule = Array.new
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_path, "method"=> :POST,
      "func"=> proc_obj, "params"=> 2}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", 200)
    result = @target.action(request)
    assert_equal(200, result.status_code)
    assert_equal("Path", result.body)
  end
  
  def test_action_params_2_body_nil
    proc_obj = Proc.new { |body|
      Odenos::Core::Response.new(200, body)
    }
    rule = Array.new
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_path, "method"=> :POST,
      "func"=> proc_obj, "params"=> 2}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", nil)
    result = @target.action(request)
    assert_equal(200, result.status_code)
    assert_equal("Path", result.body)
  end
  
  def test_action_NOT_FOUND_METHOD_NOT_ALLOWED
    rule = Array.new
    regex_path = Regexp.new("Path")
    rule = [{"pattern"=> regex_path, "method"=> :GET,
             "func"=> "Func", "params"=> 0}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", "body")
    result = @target.action(request)
    assert_equal(405, result.status_code)
    assert_equal(nil, result.body)
  end
  
  def test_action_NOT_FOUND
    rule = Array.new
    regex_None = Regexp.new("Error")
    rule = [{"pattern"=> regex_None, "method"=> :POST,
             "func"=> "Func", "params"=> 0}]
    @target.add_rule(rule)
    request = Odenos::Core::Request.new("objectId", :POST, "Path", "*", "body")
    result = @target.action(request)
    assert_equal(404, result.status_code)
    assert_equal(nil, result.body)
  end
end
