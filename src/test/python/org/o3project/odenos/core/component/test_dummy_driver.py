# -*- coding:utf-8 -*-

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

from org.o3project.odenos.core.component.dummy_driver import DummyDriver
from org.o3project.odenos.remoteobject.manager.system.component_connection_logic_and_network\
    import ComponentConnectionLogicAndNetwork
from org.o3project.odenos.remoteobject.manager.system.component_connection\
    import ComponentConnection
from org.o3project.odenos.core.util.system_manager_interface import (
    SystemManagerInterface
    )
from org.o3project.odenos.core.component.network.packet.out_packet_added\
    import OutPacketAdded
from org.o3project.odenos.core.util.network_interface import NetworkInterface
from org.o3project.odenos.core.component.network.flow.flow import Flow
from org.o3project.odenos.remoteobject.message.event import Event
from mock import Mock, patch
import unittest
from contextlib import nested


class DummyDriverTest(unittest.TestCase):
    Dispatcher = Mock()
    Dispatcher.system_manager_id = "ObjectId1"
    Object_id = "ObjectId1"

    def setUp(self):
        self.target = DummyDriver(self.Object_id, self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._super_type, "Driver")
        self.assertEqual(self.target.dispatcher, self.Dispatcher)
        self.assertEqual(self.target.object_id, self.Object_id)
        self.assertEqual(self.target._DummyDriver__network_id, None)

    def test__connection_changed_added_pre_True(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   self.Object_id,
                                                   "NetworkId")
        msg = Mock()
        msg.curr = cclan
        self.result = self.target._connection_changed_added_pre(msg)
        self.assertEqual(self.result, True)

    def test__connection_changed_delete_pre_True(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   self.Object_id,
                                                   "NetworkId")
        msg = Mock()
        msg.prev = cclan
        self.result = self.target._connection_changed_delete_pre(msg)
        self.assertEqual(self.result, True)


    def test__connection_changed_added_pre_NotType_False(self):
        ComponentConnectioTmp = ComponentConnection("slicer1_network1",
                                                    "original",
                                                    "running")
        msg = Mock()
        msg.curr = ComponentConnectioTmp
        self.result = self.target._connection_changed_added_pre(msg)
        self.assertEqual(self.result, False)

    def test__connection_changed_delete_pre_NotType_False(self):
        ComponentConnectioTmp = ComponentConnection("slicer1_network1",
                                                    "original",
                                                    "running")
        msg = Mock()
        msg.prev = ComponentConnectioTmp
        self.result = self.target._connection_changed_delete_pre(msg)
        self.assertEqual(self.result, False)

    def test__connection_changed_added_pre_Notlogicid_False(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   "Logicid1",
                                                   "NetworkId")
        msg = Mock()
        msg.curr = cclan
        self.result = self.target._connection_changed_added_pre(msg)
        self.assertEqual(self.result, False)

    def test__connection_changed_delete_pre_Notlogicid_False(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   "Logicid1",
                                                   "NetworkId")
        msg = Mock()
        msg.prev = cclan
        self.result = self.target._connection_changed_delete_pre(msg)
        self.assertEqual(self.result, False)

    def test__connection_changed_added_pre_NotNoneNetworkid_False(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   self.Object_id,
                                                   "NetworkId")
        msg = Mock()
        msg.curr = cclan
        self.target._DummyDriver__network_id = "NetworkId"
        with nested(
                patch('org.o3project.odenos.core.util.system_manager_interface.'
                      'SystemManagerInterface.put_connection'
                      )) as m_put_connection:

                self.result = self.target._connection_changed_added_pre(msg)
                self.assertEqual(m_put_connection[0].call_count, 1)
                m_put_connection[0].assert_any_call(cclan)
                self.assertEqual(cclan.state, ComponentConnection.State.ERROR)
                self.assertEqual(self.result, False)

    def test_connection_changed_added(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   self.Object_id,
                                                   "NetworkId")
        msg = Mock()
        msg.curr = cclan
        with nested(
                patch('org.o3project.odenos.core.component.dummy_driver.'
                      'DummyDriver._DummyDriver__subscribe_network_component'
                      )) as m_subscribe_network_comp:

                self.assertEqual(self.target._DummyDriver__network_id, None)
                self.target._connection_changed_added(msg)
                self.assertEqual(self.target._DummyDriver__network_id,
                                 "NetworkId")
                self.assertEqual(m_subscribe_network_comp[0].call_count, 1)

    def test_connection_changed_delete(self):
        cclan = ComponentConnectionLogicAndNetwork("slicer1->network1",
                                                   "original",
                                                   "running",
                                                   self.Object_id,
                                                   "NetworkId")
        msg = Mock()
        msg.prev = cclan
        self.target._DummyDriver__network_id = "NetworkId"

        with nested(
                patch('org.o3project.odenos.core.util.system_manager_interface.'
                      'SystemManagerInterface.put_connection'
                      ),
                patch('org.o3project.odenos.core.component.dummy_driver.'
                      'DummyDriver._DummyDriver__unsubscribe_network_component'
                      )) as (m_put_connection, m_unsubscribe_network_comp):

                self.assertEqual(self.target._DummyDriver__network_id,
                                 "NetworkId")
                self.target._connection_changed_delete(msg)
                self.assertEqual(self.target._DummyDriver__network_id,
                                 None)
                self.assertEqual(m_unsubscribe_network_comp.call_count, 1)
                self.assertEqual(m_put_connection.call_count, 2)

    def test___subscribe_network_component(self):
        self.target._DummyDriver__network_id = "NetworkId"

        with nested(
                patch('org.o3project.odenos.core.component.logic.Logic.'
                      '_add_event_subscription'),
                patch('org.o3project.odenos.core.component.logic.Logic.'
                      '_update_event_subscription'),
                patch('org.o3project.odenos.remoteobject.remote_object.'
                      'RemoteObject._apply_event_subscription'
                      )) as (m_add_event_subscription,
                             m_update_event_subscription,
                             m_apply_event_subscription):

                self.target._DummyDriver__subscribe_network_component()

                self.assertEqual(m_add_event_subscription.call_count, 2)
                m_add_event_subscription.assert_any_call("FlowChanged",
                                                         "NetworkId")
                m_add_event_subscription.assert_any_call("OutPacketAdded",
                                                         "NetworkId")
                self.assertEqual(m_update_event_subscription.call_count, 1)
                m_update_event_subscription.assert_any_call("FlowChanged",
                                                            "NetworkId",
                                                            attributes=[])
                self.assertEqual(m_apply_event_subscription.call_count, 1)

    def test___unsubscribe_network_component(self):
        self.target._DummyDriver__network_id = "NetworkId"

        with nested(
                patch('org.o3project.odenos.core.component.logic.Logic.'
                      '_remove_event_subscription'),
                patch('org.o3project.odenos.remoteobject.remote_object.'
                      'RemoteObject._apply_event_subscription'
                      )) as (m_remove_event_subscription,
                             m_apply_event_subscription):

                self.target._DummyDriver__unsubscribe_network_component()

                self.assertEqual(m_remove_event_subscription.call_count, 2)
                m_remove_event_subscription.assert_any_call("FlowChanged",
                                                            "NetworkId")
                m_remove_event_subscription.assert_any_call("OutPacketAdded",
                                                            "NetworkId")
                self.assertEqual(m_apply_event_subscription.call_count, 1)

    def test__on_flow_added_success(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_added("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 1)
                m_put_flow.assert_any_call(flow_body1)
                self.assertEqual(flow_body1.status, "established")

    def test__on_flow_added_NotNetworkId(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_added("NetworkId_dmy", flow_body2)

                self.assertEqual(m_get_flow.call_count, 0)
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "none")

    def test__on_flow_added_NoneTargetFlow(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=None),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_added("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "none")

    def test__on_flow_added_status_NotNone(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "establishing", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_added("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "establishing")

    def test__on_flow_added_enabled_False(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", False,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_added("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "none")

    def test__on_flow_update(self):
        Curr = Mock()
        Prev = Mock()
        Attrs = {}
        with nested(
                patch('org.o3project.odenos.core.component.dummy_driver.'
                      'DummyDriver._on_flow_added'
                      )) as m_on_flow_added:

                self.target._on_flow_update("NetworkId", Prev, Curr, Attrs)
                self.assertEqual(m_on_flow_added[0].call_count, 1)
                m_on_flow_added[0].assert_any_call("NetworkId", Curr)

    def test__on_flow_delete_success(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_delete("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 2)
                m_put_flow.assert_any_call(flow_body1)
                self.assertEqual(flow_body1.status, "none")

    def test__on_flow_delete_NotNetworkId(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_delete("NetworkId_dmy", flow_body2)

                self.assertEqual(m_get_flow.call_count, 0)
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "established")

    def test__on_flow_delete_NoneTargetFlow(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=None),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_delete("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "established")

    def test__on_flow_delete_status_NotEstablished(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_delete("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "none")

    def test__on_flow_delete_enabled_False(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        flow_body1 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", False,
                          65535, "none", {})
        flow_body2 = Flow("BasicFlow", "v01",
                          "FlowId1",
                          "Owner", True,
                          65535, "established", {})

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.get_flow', return_value=flow_body1),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.put_flow'
                      )) as (m_get_flow,
                             m_put_flow):

                self.target._on_flow_delete("NetworkId", flow_body2)

                self.assertEqual(m_get_flow.call_count, 1)
                m_get_flow.assert_any_call("FlowId1")
                self.assertEqual(m_put_flow.call_count, 0)
                self.assertEqual(flow_body1.status, "none")

    def test__do_event_outpacketadded_success(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        Evbody = {"id": "OutPacketId1"}
        Event1 = Event("NetworkId", "OutPacketAdded", Evbody)
        Event_packed = OutPacketAdded.create_from_packed(Evbody)
        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet_added.OutPacketAdded.create_from_packed',
                      return_value=Event_packed),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_out_packet'),
                patch('logging.error'
                      )) as (m_create_from_packed,
                             m_del_out_packet,
                             m_logerror):

                self.target._do_event_outpacketadded(Event1)
                self.assertEqual(m_create_from_packed.call_count, 1)
                m_create_from_packed.assert_any_call(Evbody)
                self.assertEqual(m_del_out_packet.call_count, 1)
                m_del_out_packet.assert_any_call("OutPacketId1")
                self.assertEqual(m_logerror.call_count, 0)

    def test__do_event_outpacketadded_create_from_packed_error(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        Evbody = {"id": "OutPacketId1"}
        Event1 = Event("NetworkId", "OutPacketAdded", Evbody)

        self.target._network_interfaces["NetworkId"] =\
            NetworkInterface(Dispatcher, Network_id)

        debug_log = "Receive Invalid OutPacketAdded Message"\
                    + " KeyError: " + "1"

        with nested(
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet_added.OutPacketAdded.create_from_packed',
                      side_effect=KeyError(1)),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_out_packet'),
                patch('logging.error'
                      )) as (m_create_from_packed,
                             m_del_out_packet,
                             m_logerror):

                self.target._do_event_outpacketadded(Event1)
                self.assertEqual(m_create_from_packed.call_count, 1)
                m_create_from_packed.assert_any_call(Evbody)
                self.assertEqual(m_del_out_packet.call_count, 0)
                self.assertEqual(m_logerror.call_count, 1)
                m_logerror.assert_any_call(debug_log)

    def test__do_event_outpacketadded_NotNetworkId(self):
        Dispatcher = Mock()
        Dispatcher.network_id = "NetworkId"
        Network_id = "NetworkId"
        Evbody = {"id": "OutPacketId1"}
        Event1 = Event("NetworkId_dmy", "OutPacketAdded", Evbody)
        Event_packed = OutPacketAdded.create_from_packed(Evbody)
        self.target._network_interfaces[Network_id] =\
            NetworkInterface(Dispatcher, Network_id)

        with nested(
                patch('org.o3project.odenos.core.component.network.packet.'
                      'out_packet_added.OutPacketAdded.create_from_packed',
                      return_value=Event_packed),
                patch('org.o3project.odenos.core.util.network_interface.'
                      'NetworkInterface.del_out_packet'),
                patch('logging.error'
                      )) as (m_create_from_packed,
                             m_del_out_packet,
                             m_logerror):

                self.target._do_event_outpacketadded(Event1)
                self.assertEqual(m_create_from_packed.call_count, 1)
                m_create_from_packed.assert_any_call(Evbody)
                self.assertEqual(m_del_out_packet.call_count, 0)
                self.assertEqual(m_logerror.call_count, 0)

if __name__ == '__main__':
    unittest.main()
