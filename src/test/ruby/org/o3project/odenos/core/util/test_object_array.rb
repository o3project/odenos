
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

require 'odenos/core/util/object_array'

class TestObjectArray < MiniTest::Test
  def setup
    @target = Odenos::Util::ObjectArray.new(
      ["value01", "value02", "value03"])
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    assert_equal(["value01", "value02", "value03"],
      @target.instance_variable_get(:@object))
  end
  
  def test_to_a
    assert_equal(["value01", "value02", "value03"],
      @target.to_a)
  end
  
  def test_to_msgpack_io_exist
    io = {}
    msg = ["value01", "value02", "value03"]

    assert_equal(msg.to_msgpack(), @target.to_msgpack(io))
  end
  
  def test_to_msgpack_io_nil
    msg = ["value01", "value02", "value03"]

    assert_equal(msg.to_msgpack(), @target.to_msgpack())
  end
  
  def test_from_msgpack
    msg = ["value01", "value02", "value03"]

    result = Odenos::Util::ObjectArray.from_msgpack(msg.to_msgpack())
    
    assert_equal(msg, result.to_a)
  end
  
  def test_pack
    msg = ["value01", "value02", "value03"]

    assert_equal(msg.to_msgpack, @target.pack)
  end
  
  def test_unpack
    msg = ["value01", "value02", "value03"]

    result = Odenos::Util::ObjectArray.unpack(msg.to_msgpack())
    
    assert_equal(msg, result.to_a)
  end
end
