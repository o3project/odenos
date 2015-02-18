
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
$LOAD_PATH.unshift File.expand_path(File.join(File.dirname(__FILE__), "../../../../../main/ruby/trema-edge/ruby"))

require 'minitest'
require 'minitest/unit'
require 'minitest/autorun'
require 'mocha/mini_test'

require 'odenos/remoteobject/manager/of_component_manager'

class TestOFComponentManager < MiniTest::Test
  include Odenos::Manager
  include Odenos::Core
  include Odenos::Component
  include Odenos::Component::Driver

  def setup
    @dispatcher = mock()
    @controller = mock()
    @dispatcher.expects(:add_local_object).at_least_once
    @target = OFComponentManager.new('of_comp_mgr', @dispatcher, @controller)
  end

  def teardown
    @dispatcher = nil
    @controller = nil
    @target = nil
  end

  def register_open_flow_driver
    ofd_prop = {:id => 'open_flow_driver',
                :type => 'OpenFlowDriver'}
    @dispatcher.expects(:system_manager_id).returns().once
    @dispatcher.expects(:system_manager_id).returns().once
    @dispatcher.expects(:subscribe_event).returns().once
    ofd_comp = Driver.new('open_flow_driver', @dispatcher)
    ofd_comp.property.remote_object_type = 'OpenFlowDriver'
    ofd_comp.property.set_state('running')
    ofd_comp.property.description = 'OpenFlowDriver'
    @target.expects(:create_openflow_driver_component).with('open_flow_driver').returns(ofd_comp)
    @target.expects(:do_component_changed).with('add',
                                                nil,
                                                ofd_comp.property).returns()

    @target.do_put_component(ofd_prop, ofd_comp.remote_object_id)
  end

  def test_initialize
    assert_equal(@target.instance_variable_get(:@driver_component), nil)
    assert_equal(@target.instance_variable_get(:@controller), @controller)
  end

  def test_do_get_component_types_success
    @dispatcher.expects(:add_local_object).once
    @dispatcher.expects(:system_manager_id).once
    @dispatcher.expects(:system_manager_id).once
    @dispatcher.expects(:subscribe_event).once
    @target.register_component_type(SampleDriver)

    response = @target.do_get_component_types

    assert_equal(response.status_code, Response::OK)
    assert_includes(response.body['SampleDriver']['type'], 'SampleDriver')
    assert_includes(response.body['SampleDriver']['super_type'], 'Driver')
    assert_includes(response.body['SampleDriver']['connection_types']['original'], '1')
    assert_includes(response.body['SampleDriver']['connection_types']['aggregated'], '1')
    assert_includes(response.body['SampleDriver']['description'], 'Description')
  end

  def test_do_get_component_types_failed_rescue
    @target.expects(:create_openflow_driver_component).with(anything).raises().once
    @target.register_component_type(OFDriver::OpenFlowDriver)

    response = @target.do_get_component_types

    assert_equal(response.status_code, Response::INTERNAL_SERVER_ERROR)
  end

  def test_do_get_components_existence
    register_open_flow_driver

    response = @target.do_get_components

    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body['open_flow_driver'],
                 @target.instance_variable_get(:@driver_component).property)
  end

  def test_do_get_components_not_existence
    response = @target.do_get_components

    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, {})
  end

  def test_do_put_component_success
    ofd_prop = {:id => 'open_flow_driver',
                :type => 'OpenFlowDriver'}
    @dispatcher.expects(:system_manager_id).at_least_once
    @dispatcher.expects(:subscribe_event).at_least_once
    ofd_comp = Driver.new('open_flow_driver', @dispatcher)
    ofd_comp.property.remote_object_type = 'OpenFlowDriver'
    ofd_comp.property.set_state('running')
    ofd_comp.property.description = 'OpenFlowDriver'
    @target.expects(:create_openflow_driver_component).with('open_flow_driver').returns(ofd_comp).once
    @target.expects(:do_component_changed).with('add',
                                                nil,
                                                ofd_comp.property).returns().once
    
    response = @target.do_put_component(ofd_prop, ofd_prop[:id])

    assert_equal(response.status_code, Response::CREATED)
    assert_equal(response.body, ofd_comp.property)
    assert_equal(@target.instance_variable_get(:@driver_component), ofd_comp)
    assert_equal(@target.instance_variable_get(:@components)['open_flow_driver'], ofd_comp)
  end

  def test_do_put_component_not_support_type
    vxlan_prop = {:id => 'vxlan_driver',
                  :type => 'VXLANDriver'}

    response = @target.do_put_component(vxlan_prop, vxlan_prop[:id])

    assert_equal(response.status_code, Response::FORBIDDEN)
    assert_equal(response.body, 'Unsupported type')
  end

  def test_do_put_component_already_created
    register_open_flow_driver
    ofd_prop = {:id => 'open_flow_driver',
                :type => 'OpenFlowDriver'}

    response = @target.do_put_component(ofd_prop, ofd_prop[:id])

    assert_equal(response.status_code, Response::CONFLICT)
    assert_equal(response.body, 'Cannot overwrite existing instance.')
  end

  def test_do_delete_component_success
    register_open_flow_driver
    ofd_prop = @target.instance_variable_get(:@driver_component).property
    @target.instance_variable_get(:@driver_component).
      expects(:on_finalize).with().returns().once
    @target.expects(:do_component_changed).with('delete',
                                                ofd_prop,
                                                nil).returns().once 

    response = @target.do_delete_component('open_flow_driver')

    assert_equal(response.status_code, Response::OK)
    assert_equal(response.body, nil)
  end

  def test_do_delete_component_not_created
    response = @target.do_delete_component('open_flow_driver')

    assert_equal(response.status_code, Response::NOT_FOUND)
    assert_equal(response.body, 'open_flow_driver Not Found')
  end

  def test_do_delete_component_diff_comp_id
    register_open_flow_driver

    response = @target.do_delete_component('open_flow_driver1')

    assert_equal(response.status_code, Response::NOT_FOUND)
    assert_equal(response.body, 'open_flow_driver1 Not Found')
  end

  def test_create_openflow_driver_component
    ofd = mock()
    OFDriver::OpenFlowDriver.expects(:new).with('open_flow_driver',
                                                      @dispatcher,
                                                      @controller).returns(ofd).once

    ret = @target.create_openflow_driver_component('open_flow_driver')

    assert_equal(ret, ofd)
  end

end

