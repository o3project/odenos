
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

require 'odenos/core/component/system_manager_interface'

class TestSystemManagerInterface < MiniTest::Test
  include Odenos::Component
  include Odenos::Core

  SYSTEM_MANAGER_ID = "systemmanager"

  def setup
    @dispatcher = mock()
    #@dispatcher.expects(:add_local_object).at_least_once
    @dispatcher.expects(:system_manager_id).returns(SYSTEM_MANAGER_ID).once
    @target = SystemManagerInterface.new(@dispatcher)
  end

  def teardown
    @dispatcher = nil
    @target = nil
  end

  def test_initialize
    assert_equal(@target.instance_variable_get(:@dispatcher), @dispatcher)
    assert_equal(@target.instance_variable_get(:@sysmgr_id), SYSTEM_MANAGER_ID)
  end

  def test_get_component_managers_success
    prop1 = {'type' => "ComponentManager",
             "id" => "compmgr_java",
             "state" => "running"}
    prop2 = {'type' => "ComponentManager",
             "id" => "compmgr_python",
             "state" => "running"}
    body = [prop1, prop2]
    response = Response.new(Response::OK, body)
    @target.expects(:get_object_to_sysmgr).with("component_managers").returns(response).once

    comp_mgrs = @target.get_component_managers

    comp_mgrs.each_pair do |id, prop|
      if id == "compmgr_java"
        assert_equal(prop, prop1)
      else
        assert_equal(prop, prop2)
      end
    end
  end

  def test_get_component_managers_failure
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:get_object_to_sysmgr).with("component_managers").returns(response).once

    comp_mgrs = @target.get_component_managers

    assert_equal(comp_mgrs, nil)
  end

  def test_get_component_managers_exception
    response = Response.new(Response::OK, [[]])
    @target.expects(:get_object_to_sysmgr).with("component_managers").returns(response).once

    comp_mgrs = @target.get_component_managers

    assert_equal(comp_mgrs, nil)
  end

  def test_get_event_manager_success
    prop = {'type' => "EventManager",
            "id" => "event_manager",
            "state" => "running"}
    response = Response.new(Response::OK, prop)
    @target.expects(:get_object_to_sysmgr).with("event_manager").returns(response).once

    evt_prop = @target.get_event_manager

    assert_equal(evt_prop, prop)
  end

  def test_get_event_manager_failure
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:get_object_to_sysmgr).with("event_manager").returns(response).once

    evt_prop = @target.get_event_manager

    assert_equal(evt_prop, nil)
  end

  def test_test_get_event_manager_exception
    response = Response.new(Response::OK, [])
    @target.expects(:get_object_to_sysmgr).with("event_manager").returns(response).once

    evt_prop = @target.get_event_manager

    assert_equal(evt_prop, nil)
  end

  def test_get_component_types_success
    prop1 = {"type" => "ComponentManager",
             "id" => "compmgr_java",
             "state" => "running",
             "component_types" => "Slicer,Aggregator,DummyDriver"}
    prop2 = {"type" => "ComponentManager",
             "id" => "compmgr_python",
             "state" => "running",
             "component_types" => "DummyDriver"}
    types_hash = {"Slicer" => [prop1],
                  "Aggregator" => [prop1],
                  "DummyDriver" => [prop1, prop2]}
    response = Response.new(Response::OK, types_hash)
    @target.expects(:get_object_to_sysmgr).with("component_types").returns(response).once

    comp_types = @target.get_component_types

    comp_types.each_pair do |type, prop_list|
      if type == "Slicer"
        assert_equal(prop_list.length, 1)
        assert_equal(prop_list[0], prop1)
      elsif type == "Aggregator"
        assert_equal(prop_list.length, 1)
        assert_equal(prop_list[0], prop1)
      else
        assert_equal(prop_list.length, 2)
        assert_equal(prop_list[0], prop1)
        assert_equal(prop_list[1], prop2)
      end
    end
  end

  def test_get_component_types_failure
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:get_object_to_sysmgr).with("component_types").returns(response).once

    comp_types = @target.get_component_types

    assert_equal(comp_types, nil)
  end

  def test_get_component_types_exception
    prop1 = {"type" => "ComponentManager",
             "id" => "compmgr_java",
             "state" => "running",
             "component_types" => "Slicer,Aggregator,DummyDriver"}
    types_hash = {"Slicer" => [prop1],
                  "Aggregator" => [prop1],
                  "DummyDriver" => [prop1, []]}
    response = Response.new(Response::OK, types_hash)
    @target.expects(:get_object_to_sysmgr).with("component_types").returns(response).once

    comp_types = @target.get_component_types

    assert_equal(comp_types, nil)
  end

  def test_get_components_success
    prop1 = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    prop2 = {'type' => "Network",
             "id" => "network2",
             "state" => "running"}
    components = {"network1" => prop1,
                  "network2" => prop2}
    response = Response.new(Response::OK, components)
    @target.expects(:get_object_to_sysmgr).with("components").returns(response).once

    comps = @target.get_components

    comps.each_pair do |id, prop|
      if id == "network1"
        assert_equal(prop, prop1)
      else
        assert_equal(prop, prop2)
      end
    end
  end

  def test_get_components_failure
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:get_object_to_sysmgr).with("components").returns(response).once

    comps = @target.get_components

    assert_equal(comps, nil)
  end

  def test_get_components_exception
    prop1 = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    components = {"network1" => prop1,
                  "network2" => []}
    response = Response.new(Response::OK, components)
    @target.expects(:get_object_to_sysmgr).with("components").returns(response).once

    comps = @target.get_components

    assert_equal(comps, nil)
  end

  def test_get_connections_success
    conn = {"id" => "1",
            "type" => "ComponentConnection",
            "connection_type" => "original",
            "state" => "running"}
    conn_log_net = {"id" => "2",
                    :type => "LogicAndNetwork",
                    "connection_type" => "sliver",
                    "state" => "running",
                    "logic_id" => "slicer",
                    "network_id" => "sliver_network"}
    connections = {"1" => conn, "2" => conn_log_net}
    response = Response.new(Response::OK, connections)
    @target.expects(:get_object_to_sysmgr).with("connections").returns(response).once

    conns = @target.get_connections

    conns.each_pair do |id, connection|
      if id == "1"
        assert_equal(connection.to_hash, Hash[conn.map{|(k,v)| [k.to_sym,v]}])
      else
        assert_equal(connection.to_hash, Hash[conn_log_net.map{|(k,v)| [k.to_sym,v]}])
      end
    end
  end

  def test_get_connections_failure
    response = Response.new(Response::INTERNAL_SERVER_ERROR, nil)
    @target.expects(:get_object_to_sysmgr).with("connections").returns(response).once

    conns = @target.get_connections

    assert_equal(conns, nil)
  end

  def test_get_connections_exception
    conn = {"id" => "1",
            "type" => "ComponentConnection",
            "connection_type" => "original",
            "state" => "running"}
    connections = {"1" => conn, "2" => []}
    response = Response.new(Response::OK, connections)
    @target.expects(:get_object_to_sysmgr).with("connections").returns(response).once

    conns = @target.get_connections

    assert_equal(conns, nil)
  end

  def test_get_component_manager_success
    object_id = "compmgr_java"
    path = "component_managers/" + object_id
    prop = {'type' => "ComponentManager",
            "id" => "compmgr_java",
            "state" => "running"}
    response = Response.new(Response::OK, prop)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    compmgr = @target.get_component_manager(object_id)

    assert_equal(compmgr, prop)
  end

  def test_get_component_manager_failure
    object_id = "compmgr_java"
    path = "component_managers/" + object_id
    response = Response.new(Response::NOT_FOUND, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    compmgr = @target.get_component_manager(object_id)

    assert_equal(compmgr, nil)
  end

  def test_get_component_manager_exception
    object_id = "compmgr_java"
    path = "component_managers/" + object_id
    response = Response.new(Response::OK, [])
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    compmgr = @target.get_component_manager(object_id)

    assert_equal(compmgr, nil)
  end

  def test_get_component_type_success
    comp_type = "DummyDriver"
    path = "component_types/" + comp_type
    prop1 = {"type" => "ComponentManager",
             "id" => "compmgr_java",
             "state" => "running",
             "component_types" => "Slicer,Aggregator,DummyDriver"}
    prop2 = {"type" => "ComponentManager",
             "id" => "compmgr_python",
             "state" => "running",
             "component_types" => "DummyDriver"}
    response = Response.new(Response::OK, [prop1, prop2])
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    types = @target.get_component_type(comp_type)

    assert_equal(types[0], prop1)
    assert_equal(types[1], prop2)
  end

  def test_get_component_type_failure
    comp_type = "DummyDriver"
    path = "component_types/" + comp_type
    response = Response.new(Response::NOT_FOUND, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    types = @target.get_component_type(comp_type)

    assert_equal(types, nil)
  end

  def test_get_component_type_exception
    comp_type = "DummyDriver"
    path = "component_types/" + comp_type
    prop1 = {"type" => "ComponentManager",
             "id" => "compmgr_java",
             "state" => "running",
             "component_types" => "Slicer,Aggregator,DummyDriver"}
    response = Response.new(Response::OK, [prop1, []])
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    types = @target.get_component_type(comp_type)

    assert_equal(types, nil)
  end

  def test_get_component_success
    object_id = "network1"
    path = "components/" + object_id
    prop = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    response = Response.new(Response::OK, prop)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_component(object_id)

    assert_equal(comp, prop)
  end

  def test_get_component_failure
    object_id = "network1"
    path = "components/" + object_id
    response = Response.new(Response::NOT_FOUND, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_component(object_id)

    assert_equal(comp, nil)
  end

  def test_get_component_exception
    object_id = "network1"
    path = "components/" + object_id
    response = Response.new(Response::OK, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_component(object_id)

    assert_equal(comp, nil)
  end

  def test_get_connection_success_base_connection
    conn_id = "1234"
    path = "connections/" + conn_id
    conn = {"id" => "1234",
            "type" => "ComponentConnection",
            "connection_type" => "original",
            "state" => "running"}
    response = Response.new(Response::OK, conn)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    connection = @target.get_connection(conn_id)

    assert_equal(connection.to_hash, Hash[conn.map{|(k,v)| [k.to_sym,v]}])
  end

  def test_get_connection_success_logic_and_network_connection
    conn_id = "1234"
    path = "connections/" + conn_id
    conn_log_net = {"id" => "1234",
                    :type => "LogicAndNetwork",
                    "connection_type" => "sliver",
                    "state" => "running",
                    "logic_id" => "slicer",
                    "network_id" => "sliver_network"}

    response = Response.new(Response::OK, conn_log_net)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    connection = @target.get_connection(conn_id)

    assert_equal(connection.to_hash, Hash[conn_log_net.map{|(k,v)| [k.to_sym,v]}])
  end

  def test_get_connection_failure
    conn_id = "1234"
    path = "connections/" + conn_id
    response = Response.new(Response::NOT_FOUND, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    connection = @target.get_connection(conn_id)

    assert_equal(connection, nil)
  end

  def test_get_connection_exception
    conn_id = "1234"
    path = "connections/" + conn_id
    response = Response.new(Response::OK, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    connection = @target.get_connection(conn_id)

    assert_equal(connection, nil)
  end

  def test_get_object_success
    object_id = "network1"
    path = "objects/" + object_id
    prop = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    response = Response.new(Response::OK, prop)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_object(object_id)

    assert_equal(comp, prop)
  end

  def test_get_object_failure
    object_id = "network1"
    path = "objects/" + object_id
    response = Response.new(Response::NOT_FOUND, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_object(object_id)

    assert_equal(comp, nil)
  end

  def test_get_object_exception
    object_id = "network1"
    path = "objects/" + object_id
    response = Response.new(Response::OK, nil)
    @target.expects(:get_object_to_sysmgr).with(path).returns(response).once

    comp = @target.get_object(object_id)

    assert_equal(comp, nil)
  end

  def test_put_connection
    conn_id = "1234"
    path = "connections/" + conn_id
    conn_log_net = {"id" => "1234",
                    :type => "LogicAndNetwork",
                    "connection_type" => "sliver",
                    "state" => "running",
                    "logic_id" => "slicer",
                    "network_id" => "sliver_network"}
    connection = LogicAndNetwork.new(conn_log_net)
    response = Response.new(Response::OK, nil)
    @target.expects(:put_object_to_sysmgr).with(path,
                                                connection).returns(response).once
    
    resp = @target.put_connection(connection)

    assert_equal(resp, response)
  end

  def test_put_component_managers
    prop = {'type' => "ComponentManager",
            "id" => "compmgr_java",
            "state" => "running"}
    property = ObjectProperty.new(prop)
    response = Response.new(Response::OK, nil)
    @target.expects(:put_object_to_sysmgr).with("component_managers/compmgr_java",
                                                 property).returns(response).once

    resp = @target.put_component_managers(property)

    assert_equal(resp, response)
  end

  def test_put_components
    prop = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    property = ObjectProperty.new(prop)
    response = Response.new(Response::CREATED, nil)
    @target.expects(:put_object_to_sysmgr).with("components/network1",
                                                 property).returns(response).once

    resp = @target.put_components(property)

    assert_equal(resp, response)
  end
  
  def test_post_components
    prop = {'type' => "Network",
            "id" => "network1",
            "state" => "running"}
    property = ObjectProperty.new(prop)
    response = Response.new(Response::OK, nil)
    @target.expects(:post_object_to_sysmgr).with("components",
                                                 property).returns(response).once

    resp = @target.post_components(property)

    assert_equal(resp, response)
  end

  def test_post_conections
    conn_log_net = {"id" => "1234",
                    :type => "LogicAndNetwork",
                    "connection_type" => "sliver",
                    "state" => "running",
                    "logic_id" => "slicer",
                    "network_id" => "sliver_network"}
    connection = LogicAndNetwork.new(conn_log_net)
    response = Response.new(Response::OK, nil)
    @target.expects(:post_object_to_sysmgr).with("connections",
                                                 connection).returns(response).once

    resp = @target.post_connections(connection)

    assert_equal(resp, response)
  end

  def test_del_component_managers
    object_id = "compmgr_java"
    path = "component_managers/" + object_id
    response = Response.new(Response::OK, nil)
    @target.expects(:del_object_to_sysmgr).with(path).returns(response).once

    resp = @target.del_component_managers(object_id)

    assert_equal(resp, response)
  end

  def test_del_components
    object_id = "slicer"
    path = "components/" + object_id
    response = Response.new(Response::OK, nil)
    @target.expects(:del_object_to_sysmgr).with(path).returns(response).once

    resp = @target.del_components(object_id)

    assert_equal(resp, response)
  end

  def test_del_connections
    conn_id = "slicer"
    path = "connections/" + conn_id
    response = Response.new(Response::OK, nil)
    @target.expects(:del_object_to_sysmgr).with(path).returns(response).once

    resp = @target.del_connections(conn_id)

    assert_equal(resp, response)
  end

  def test_post_object_to_sysmgr
    path = "components/"
    body = mock()
    response = Response.new(Response::OK, nil)
    @target.expects(:send_request).with(@target.instance_variable_get(:@sysmgr_id),
                                        :POST,
                                        path,
                                        body).returns(response).once

    resp = @target.post_object_to_sysmgr(path, body)

    assert_equal(resp, response)
  end

  def test_put_object_to_sysmgr
    path = "components/"
    body = mock()
    response = Response.new(Response::OK, nil)
    @target.expects(:send_request).with(@target.instance_variable_get(:@sysmgr_id),
                                        :PUT,
                                        path,
                                        body).returns(response).once

    resp = @target.put_object_to_sysmgr(path, body)

    assert_equal(resp, response)
  end

  def test_del_object_to_sysmgr
    path = "components/slicer"
    response = Response.new(Response::OK, nil)
    @target.expects(:send_request).with(@target.instance_variable_get(:@sysmgr_id),
                                        :DELETE,
                                        path,
                                        nil).returns(response).once

    resp = @target.del_object_to_sysmgr(path)

    assert_equal(resp, response)
  end

  def test_get_object_to_sysmgr
    path = "components/slicer"
    response = Response.new(Response::OK, nil)
    @target.expects(:send_request).with(@target.instance_variable_get(:@sysmgr_id),
                                        :GET,
                                        path,
                                        nil).returns(response).once

    resp = @target.get_object_to_sysmgr(path)

    assert_equal(resp, response)
  end

  def test_dump_error
    begin
      fail "test_dump_error"
    rescue => ex
      assert_equal(@target.send(:dump_error, ex), nil)
    end
  end

  def test_send_request_success
    obj_id = "system_manager"
    method = :POST
    path = "components"
    body = {}
    response = Response.new(Response::OK, nil)
    @dispatcher.expects(:request_sync).with(instance_of(Request)).returns(response).once

    resp = @target.send(:send_request, obj_id, method, path, body)

    assert_equal(resp, response)
  end

  def test_send_request_exception
    obj_id = "system_manager"
    method = :POST
    path = "components"
    body = {}
    @dispatcher.expects(:request_sync).with(instance_of(Request)).raises().once

    resp = @target.send(:send_request, obj_id, method, path, body)

    assert_equal(resp.status_code, Response::INTERNAL_SERVER_ERROR)
    assert_equal(resp.body, nil)
  end

end
