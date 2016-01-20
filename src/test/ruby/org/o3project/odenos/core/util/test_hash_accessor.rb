
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

require 'odenos/core/util/hash_accessor'

class TestHashAccessor < MiniTest::Test

  class TestHash
    extend Odenos::Util::HashAccessor
    def initialize
      @hash_read = {}
      @hash_write = {}
      @hash_access = {}
    end
    hash_reader :hash_read, :key1
    hash_writer :hash_write, :key2
    hash_accessor :hash_access, :key3
  end

  def test_hash_reader
    hr = TestHash.new
    hr.instance_variable_get(:@hash_read)[:key1] = "aaa"
    
    assert_equal("aaa", hr.key1)
  end

  def test_hash_writer
    hw = TestHash.new
    hw.key2 = "bbb"

    assert_equal("bbb", hw.instance_variable_get(:@hash_write)[:key2])
  end

  def test_hash_accessor
    ha = TestHash.new
    ha.key3 = "ccc"

    assert_equal("ccc", ha.key3)
  end

end
