/*
 * Copyright 2015 NEC Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.o3project.odenos;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.o3project.odenos.component.aggregator.AggregatorTest;
import org.o3project.odenos.component.learningswitch.LearningSwitchTest;
//importTestCase classes.
import org.o3project.odenos.core.component.ComponentTest;
import org.o3project.odenos.core.component.ConversionTableTest;
import org.o3project.odenos.core.component.DriverTest;
import org.o3project.odenos.core.component.DummyDriverTest;
import org.o3project.odenos.core.component.LogicTest;
import org.o3project.odenos.core.component.NetworkInterfaceTest;
import org.o3project.odenos.core.component.SystemManagerInterfaceTest;
import org.o3project.odenos.core.component.network.BaseNetworkChangedTest;
import org.o3project.odenos.core.component.network.BaseObjectQueryTest;
import org.o3project.odenos.core.component.network.BasicQueryTest;
import org.o3project.odenos.core.component.network.NetworkTest;
import org.o3project.odenos.core.component.network.flow.FlowActionQueryFactoryTest;
import org.o3project.odenos.core.component.network.flow.FlowChangedTest;
import org.o3project.odenos.core.component.network.flow.FlowMatchQueryFactoryTest;
import org.o3project.odenos.core.component.network.flow.FlowObjectTest;
import org.o3project.odenos.core.component.network.flow.FlowQueryFactoryTest;
import org.o3project.odenos.core.component.network.flow.FlowSetTest;
import org.o3project.odenos.core.component.network.flow.FlowTest;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatchTest;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowTest;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutputTest;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlInTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlOutTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecIpTtlTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecMplsTtlTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenterTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionGroupActionTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopMplsTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopPbbTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopVlanTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushMplsTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushPbbTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlanTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetFieldTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetIpTtlTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtlTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueueTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchTest;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowTest;
import org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQueryTest;
import org.o3project.odenos.core.component.network.flow.query.BasicFlowQueryTest;
import org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQueryTest;
import org.o3project.odenos.core.component.network.flow.query.FlowActionQueryTest;
import org.o3project.odenos.core.component.network.flow.query.FlowQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionCopyTtlInQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionCopyTtlOutQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecIpTtlQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecMplsTtlQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionExperimenterQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionGroupActionQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPopMplsQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPopPbbQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPopVlanQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushMplsQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushPbbQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetMplsTtlQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetQueueQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQueryTest;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowQueryTest;
import org.o3project.odenos.core.component.network.packet.BasePacketAddedTest;
import org.o3project.odenos.core.component.network.packet.InPacketAddedTest;
import org.o3project.odenos.core.component.network.packet.InPacketQueryTest;
import org.o3project.odenos.core.component.network.packet.InPacketTest;
import org.o3project.odenos.core.component.network.packet.OFPInPacketTest;
import org.o3project.odenos.core.component.network.packet.OFPOutPacketTest;
import org.o3project.odenos.core.component.network.packet.OutPacketAddedTest;
import org.o3project.odenos.core.component.network.packet.OutPacketQueryTest;
import org.o3project.odenos.core.component.network.packet.OutPacketTest;
import org.o3project.odenos.core.component.network.packet.PacketObjectTest;
import org.o3project.odenos.core.component.network.packet.PacketQueryTest;
import org.o3project.odenos.core.component.network.packet.PacketQueueSetTest;
import org.o3project.odenos.core.component.network.packet.PacketQueueTest;
import org.o3project.odenos.core.component.network.packet.PacketStatusSubTest;
import org.o3project.odenos.core.component.network.packet.PacketStatusTest;
import org.o3project.odenos.core.component.network.packet.PacketTest;
import org.o3project.odenos.core.component.network.topology.LinkChangedTest;
import org.o3project.odenos.core.component.network.topology.LinkQueryTest;
import org.o3project.odenos.core.component.network.topology.LinkTest;
import org.o3project.odenos.core.component.network.topology.NodeChangedTest;
import org.o3project.odenos.core.component.network.topology.NodeQueryTest;
import org.o3project.odenos.core.component.network.topology.PortChangedTest;
import org.o3project.odenos.core.component.network.topology.PortQueryTest;
import org.o3project.odenos.core.component.network.topology.PortTest;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetworkTest;
import org.o3project.odenos.core.manager.system.ComponentConnectionTest;
import org.o3project.odenos.core.manager.system.SystemManagerTest;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChangedTest;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChangedTest;
import org.o3project.odenos.core.util.InstanceCreatorTest;
import org.o3project.odenos.core.util.PathCalculatorTest;
import org.o3project.odenos.remoteobject.ObjectPropertyTest;
import org.o3project.odenos.remoteobject.ObjectSettingsTest;
import org.o3project.odenos.remoteobject.RemoteObjectTest;
import org.o3project.odenos.remoteobject.RequestParserTest;
import org.o3project.odenos.remoteobject.event.BaseObjectChangedTest;
import org.o3project.odenos.remoteobject.event.ObjectPropertyChangedTest;
import org.o3project.odenos.remoteobject.event.ObjectSettingsChangedTest;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHashTest;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHashTest;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyListTest;
import org.o3project.odenos.remoteobject.manager.component.ComponentManagerTest;
import org.o3project.odenos.remoteobject.manager.component.event.ComponentChangedTest;
import org.o3project.odenos.remoteobject.message.BaseObjectTest;
import org.o3project.odenos.remoteobject.message.OdenosMessageTest;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcherTest;
import org.o3project.odenos.remoteobject.rest.AttributesTest;
import org.o3project.odenos.remoteobject.rest.RESTTranslatorTest;
import org.o3project.odenos.remoteobject.rest.servlet.RestServletTest;
import org.o3project.odenos.remoteobject.rest.servlet.StreamServletTest;
import org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServletTest;

@RunWith(Suite.class)
@SuiteClasses({
    AttributesTest.class,
    RestServletTest.class,
    SubscriptionsServletTest.class,
    StreamServletTest.class,
    RESTTranslatorTest.class,
    ComponentTest.class,
    RemoteObjectTest.class,
    MessageDispatcherTest.class,
    ObjectSettingsChangedTest.class,
    BaseObjectChangedTest.class,
    ObjectPropertyChangedTest.class,
    ObjectSettingsTest.class,
    ObjectPropertyTest.class,
    AggregatorTest.class,
    ComponentTest.class,
    PacketQueryTest.class,
    PacketStatusSubTest.class,
    PacketObjectTest.class,
    OutPacketTest.class,
    OFPOutPacketTest.class,
    PacketTest.class,
    InPacketQueryTest.class,
    PacketStatusTest.class,
    InPacketTest.class,
    OFPInPacketTest.class,
    InPacketAddedTest.class,
    OutPacketQueryTest.class,
    BasePacketAddedTest.class,
    PacketQueueTest.class,
    PacketQueueSetTest.class,
    OutPacketAddedTest.class,
    OdenosMessageTest.class,
    BaseObjectQueryTest.class,
    BaseNetworkChangedTest.class,
    BaseObjectTest.class,
    BasicQueryTest.class,
    NetworkTest.class,
    PortTest.class,
    LinkQueryTest.class,
    NodeQueryTest.class,
    NodeChangedTest.class,
    PortQueryTest.class,
    LinkTest.class,
    PortChangedTest.class,
    LinkChangedTest.class,
    OFPFlowMatchTest.class,
    OFPFlowTest.class,
    OFPFlowMatchQueryTest.class,
    BasicFlowMatchQueryTest.class,
    FlowActionQueryTest.class,
    OFPFlowActionCopyTtlInTest.class,
    OFPFlowActionCopyTtlOutTest.class,
    OFPFlowActionDecIpTtlTest.class,
    OFPFlowActionDecMplsTtlTest.class,
    OFPFlowActionExperimenterTest.class,
    OFPFlowActionGroupActionTest.class,
    OFPFlowActionPopMplsTest.class,
    OFPFlowActionPopPbbTest.class,
    OFPFlowActionPopVlanTest.class,
    OFPFlowActionPushMplsTest.class,
    OFPFlowActionPushPbbTest.class,
    OFPFlowActionPushVlanTest.class,
    OFPFlowActionSetFieldTest.class,
    OFPFlowActionSetIpTtlTest.class,
    OFPFlowActionSetMplsTtlTest.class,
    OFPFlowActionSetQueueTest.class,
    OFPFlowQueryTest.class,
    FlowActionOutputQueryTest.class,
    OFPFlowActionCopyTtlInQueryTest.class,
    OFPFlowActionCopyTtlOutQueryTest.class,
    OFPFlowActionDecIpTtlQueryTest.class,
    OFPFlowActionDecMplsTtlQueryTest.class,
    OFPFlowActionExperimenterQueryTest.class,
    OFPFlowActionGroupActionQueryTest.class,
    OFPFlowActionPopMplsQueryTest.class,
    OFPFlowActionPopPbbQueryTest.class,
    OFPFlowActionPopVlanQueryTest.class,
    OFPFlowActionPushMplsQueryTest.class,
    OFPFlowActionPushPbbQueryTest.class,
    OFPFlowActionPushVlanQueryTest.class,
    OFPFlowActionSetFieldQueryTest.class,
    OFPFlowActionSetIpTtlQueryTest.class,
    OFPFlowActionSetMplsTtlQueryTest.class,
    OFPFlowActionSetQueueQueryTest.class,
    FlowQueryTest.class,
    BasicFlowQueryTest.class,
    FlowQueryFactoryTest.class,
    FlowMatchQueryFactoryTest.class,
    FlowObjectTest.class,
    FlowSetTest.class,
    FlowActionQueryFactoryTest.class,
    FlowActionTest.class,
    FlowActionOutputTest.class,
    BasicFlowTest.class,
    BasicFlowMatchTest.class,
    FlowChangedTest.class,
    FlowTest.class,
    LearningSwitchTest.class,
    NetworkInterfaceTest.class,
    SystemManagerInterfaceTest.class,
    LogicTest.class,
    DriverTest.class,
    DummyDriverTest.class,
    ConversionTableTest.class,
    RequestParserTest.class,
    InstanceCreatorTest.class,
    PathCalculatorTest.class,
    ComponentChangedTest.class,
    ComponentManagerTest.class,
    ObjectPropertyListTest.class,
    ComponentConnectionTest.class,
    ComponentConnectionLogicAndNetworkTest.class,
    ComponentConnectionChangedTest.class,
    ComponentManagerChangedTest.class,
    SystemManagerTest.class,
    ObjectPropertiesHashTest.class,
    ComponentTypesHashTest.class,
    })
public class AllTests {

}

/*
 * 1. Add TestCase class to @SuiteClasses annotation. and import that class. 2.
 * Run the TestSuite. Menu Run-> Run As -> JUnit Test and select AllTests.java.
 */
