
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

require 'odenos/core/util/meta_helper'

class TestObjectArray < MiniTest::Test
  include Odenos::Util
  
  def test_class_from_string
    result = MetaHelper.class_from_string("MiniTest::Unit::TestCase")
    assert_equal(MiniTest::Unit::TestCase, result)
  end
  
  def test_parent_module
    result = MetaHelper.parent_module(MiniTest::Unit::TestCase)
    assert_equal(MiniTest::Unit, result)
  end
  
  def test_simple_class
    result = MetaHelper.simple_class(MiniTest::Unit::TestCase)
    assert_equal("TestCase", result)
  end
end
