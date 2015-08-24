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

package org.o3project.odenos.component.federator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.ConversionTable;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.topology.Link;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for FederatorOnFlow.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FederatorOnFlow.class, ConversionTable.class,
    NetworkInterface.class })
public class FederatorOnFlowTest {

  private MessageDispatcher dispatcher;

  private ConversionTable conversionTable;

  private Map<String, NetworkInterface> networkInterfaces;

  private NetworkInterface networkInterface;

  private FederatorBoundaryTable federatorBoundaryTable;

  private FederatorOnFlow target;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn(new Response(Response.OK, null)).when(dispatcher).requestSync((Request) anyObject());

    conversionTable = PowerMockito.spy(new ConversionTable());

    networkInterface = new NetworkInterface(dispatcher, "NetworkId");

    networkInterfaces = new HashMap<>();
    networkInterfaces.put("NetworkId", networkInterface);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    dispatcher = null;
    conversionTable = null;
    networkInterfaces = null;
    networkInterface = null;

  }

  private FederatorOnFlow createPowerTarget() throws Exception {

    target = PowerMockito.spy(new FederatorOnFlow(conversionTable, networkInterfaces));

    return target;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#FederatorOnFlow(org.o3project.odenos.core.component.ConversionTable, java.util.Map, org.o3project.odenos.component.federator.FederatorBoundaryTable)}.
   */
  @Test
  public void testFederatorOnFlow() {

    /*
     * test
     */
    FederatorOnFlow result = new FederatorOnFlow(conversionTable, networkInterfaces);

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowAddedExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testFlowAddedExistPath() throws Exception {

    /*
     * setting
     */
    createPowerTarget();
    doNothing().when(target).doFlowAddedSelect((BasicFlow) anyObject());
    BasicFlow flow = Mockito.mock(BasicFlow.class);

    /*
     * test
     */
    target.createOriginalFlow(flow);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target).invoke("doFlowAddedSelect", flow);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowAddedNotExistPath(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testFlowAddedNotExistPath() {

    // #TODO

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowUpdatePreStatusFailed(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   */
  @Test
  public void testFlowUpdatePreStatusFailed() {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#flowUpdatePreStatusEstablished(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testFlowUpdatePreStatusEstablished() throws Exception {
    // #TODO

  }

  /*
   * ======================================================================
   * Private Methods
   * ======================================================================
   */

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#setFlowMatch(org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSetFlowMatch() throws Exception {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#convertMatch(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testConvertMatch() throws Exception {

    // #TODO

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#setFlowAction(org.o3project.odenos.core.component.network.flow.basic.BasicFlow, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testSetFlowAction() throws Exception {

    // #TODO

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#convertAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.BasicFlow)}.
   * @throws Exception
   */
  @Test
  public void testConvertAction() throws Exception {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getNetworkIdByType(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetNetworkIdByType() throws Exception {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvNodeId(java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvNodeId() throws Exception {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvPortId(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvPortId() throws Exception {

    // #TODO
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorOnFlow#getConvLinkId(java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConvLinkId() throws Exception {

    // #TODO
  }
}
