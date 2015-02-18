
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

require 'odenos/core/util/array_accessor'

class TestArrayAccessor < MiniTest::Test
  include Odenos::Util::ArrayAccessor

  class TestArray
    extend Odenos::Util::ArrayAccessor
    def initialize
      @array_read = []
      @array_write = []
      @array_access = []
    end
    array_reader :array_read, :key11, 0, :key12, 1
    array_writer :array_write, :key21, 0, :key22, 1
    array_accessor :array_access, :key31, 0, :key32, 1
  end

  def test_array_reader
    ar = TestArray.new
    ar.instance_variable_get(:@array_read)[0] = "aaa"
    ar.instance_variable_get(:@array_read)[1] = "bbb"

    assert_equal(ar.key11, "aaa")
    assert_equal(ar.key12, "bbb")
  end

  def test_array_reader_aragument_error
    assert_raises(ArgumentError) do
      array_reader :obj, :body
    end
  end

  def test_array_writer
    aw = TestArray.new
    aw.key21 = "aaa"
    aw.key22 = "bbb"

    assert_equal(aw.instance_variable_get(:@array_write)[0], "aaa")
    assert_equal(aw.instance_variable_get(:@array_write)[1], "bbb")
  end

  def test_array_writer_aragument_error
    assert_raises(ArgumentError) do
      array_writer :obj, :body
    end
  end

  def test_array_accessor
    aa = TestArray.new
    aa.key31 = "aaa"
    aa.key32 = "bbb"

    assert_equal(aa.key31, "aaa")
    assert_equal(aa.key32, "bbb")
  end

end
