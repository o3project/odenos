
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

require 'odenos/core/util/object_hash'

class TestObjectHash < MiniTest::Test
  def setup
    @target = Odenos::Util::ObjectHash.new(
      {"ObjectHash" => "objec_hash", :Integer => "integer"})
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize_argument_exist
    assert_equal({:ObjectHash => "objec_hash", :Integer => "integer"},
      @target.instance_variable_get(:@object))
  end
  
  def test_initialize_argument_nil
    @target = Odenos::Util::ObjectHash.new()
    
    assert_equal({}, @target.instance_variable_get(:@object))
  end
  
  def test_to_hash
    assert_equal({:ObjectHash => "objec_hash", :Integer => "integer"},
      @target.to_hash)
  end
  
  def test_to_msgpack_io_exist
    io = {"io" => "value"}
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    assert_equal(msg.to_msgpack(), @target.to_msgpack(io))
  end
  
  def test_to_msgpack_io_nil
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    assert_equal(msg.to_msgpack(), @target.to_msgpack())
  end
  
  def test_from_msgpack
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    result = Odenos::Util::ObjectHash.from_msgpack(msg.to_msgpack())
    
    assert_equal(msg, result.to_hash)
  end
  
  def test_pack
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    assert_equal(msg.to_msgpack, @target.pack)
  end
  
  def test_unpack
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    result = Odenos::Util::ObjectHash.unpack(msg.to_msgpack())
    
    assert_equal(msg, result.to_hash)
  end
end

class TestOdenosVersion < MiniTest::Test
  def setup
    @target = Odenos::Util::VersionedObjectHash.new(
    {"ObjectHash" => "objec_hash", :Integer => "integer",
      "version" => "0001"})
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:ObjectHash => "objec_hash", :Integer => "integer",
      :version => "0001"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_delete_version
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    @target.delete_version

    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end

class TestOdenosType < MiniTest::Test
  def setup
    @target = Odenos::Util::TypedObjectHash.new(
    {"ObjectHash" => "objec_hash", :Integer => "integer"})
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:ObjectHash => "objec_hash", :Integer => "integer",
      :type => "TypedObjectHash"}
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
  
  def test_odenos_type
    msg = {:ObjectHash => "objec_hash", :Integer => "integer"}

    assert_equal("TypedObjectHash", @target.odenos_type)
  end
end

class TestTypedObjectHash < MiniTest::Test
  def setup
    @target = Odenos::Util::TypedObjectHash.new(
    {"ObjectHash" => "objec_hash", :Integer => "integer"})
  end
  
  def teardown
    @target = nil
  end
  
  def test_from_object_hash_scccess
    msg = {:ObjectHash => "objec_hash", :Integer => "integer",
      :type => "TypedObjectHash"}
    
    result = Odenos::Util::TypedObjectHash.from_object_hash(msg)
    assert_equal(msg,result.instance_variable_get(:@object))
  end
  
  def test_from_object_hash_type_not_much
    msg = {:ObjectHash => "objec_hash", :Integer => "integer",
      :type => "TestTypedObjectHash"}
    
    assert_raises(TypeError){
      Odenos::Util::TypedObjectHash.from_object_hash(msg)}
  end
  
  def test_from_object_hash_argument_not_is_hash
    assert_raises(TypeError){
      Odenos::Util::TypedObjectHash.from_object_hash("String")}
  end
end

class TestAttributes < MiniTest::Test
  class DummyClass < Odenos::Util::ObjectHash
    include Odenos::Util::Attributes
    def initialize(hash)
      super(hash)
    end
  end
  
  def setup
    @target = DummyClass.new({:ObjectHash => "objec_hash", :Integer => "integer"})
  end
  
  def teardown
    @target = nil
  end
  
  def test_initialize
    msg = {:ObjectHash => "objec_hash", :Integer => "integer",
      :attributes => {}}
      
    assert_equal(msg, @target.instance_variable_get(:@object))
  end
end
