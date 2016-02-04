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

package org.o3project.odenos.core.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for ConversionTable.
 */
public class ConversionTableTest {

  private ConversionTable target;

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() throws Exception {

    target = Mockito.spy(new ConversionTable());
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#ConversionTable()}.
   */
  @Test
  public void testConversionTable() {

    ConversionTable target = new ConversionTable();

    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testGetConnectionType() {

    /*
     * setting
     */
    Map<String, String> connectionTypeMap = new HashMap<String, String>();
    connectionTypeMap.put("abc", "def");

    Whitebox.setInternalState(target, "connectionTypeMap", connectionTypeMap);

    /*
     * test
     */
    String result = target.getConnectionType("abc");

    /*
     * check
     */
    assertThat(result, is("def"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testGetConnectionType_null() {

    String result = target.getConnectionType(null);

    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getConnectionList(java.lang.String)}
   * .
   */
  @Test
  public void testGetConnectionList() {

    /*
     * setting
     */
    target.addEntryConnectionType("id1", "genuine");
    target.addEntryConnectionType("id2", "fake");
    target.addEntryConnectionType("id3", "fake");
    target.addEntryConnectionType("id4", "genuine");

    /*
     * test
     */
    ArrayList<String> result = target.getConnectionList("genuine");

    /*
     * check
     */
    assertThat(result.size(), is(2));
    assertThat(result.contains("id1"), is(true));
    assertThat(result.contains("id2"), is(false));
    assertThat(result.contains("id3"), is(false));
    assertThat(result.contains("id4"), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#isConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testIsConnectionType() {

    /*
     * setting
     */
    target.addEntryConnectionType("id1", "genuine");
    target.addEntryConnectionType("id2", "fake");
    target.addEntryConnectionType("id3", "fake");
    target.addEntryConnectionType("id4", "genuine");

    /*
     * test & check
     */
    boolean result1 = target.isConnectionType("genuine");
    assertThat(result1, is(true));

    boolean result2 = target.isConnectionType("nothing");
    assertThat(result2, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#isConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testIsConnectionType_null() {

    /*
     * test
     */
    boolean result = target.isConnectionType(null);

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#isConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testIsConnectionType_nullValue() {

    /*
     * setting
     */
    target.addEntryConnectionType("id1", "genuine");
    target.addEntryConnectionType("id2", null);
    target.addEntryConnectionType("id3", null);
    target.addEntryConnectionType("id4", "genuine");

    /*
     * test
     */
    boolean result = target.isConnectionType(null);

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryConnectionType(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryConnectionType() {

    /*
     * test
     */
    target.addEntryConnectionType("id", "type");

    /*
     * check
     */
    String validationResult = target.getConnectionType("id");
    assertThat(validationResult, is("type"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryConnectionType(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryConnectionType_nullKey() {

    /*
     * test
     */
    target.addEntryConnectionType(null, "type");

    /*
     * check
     */
    String validationResult = target.getConnectionType(null);
    assertThat(validationResult, is("type"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryConnectionType(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryConnectionType_nullValue() {

    /*
     * test
     */
    target.addEntryConnectionType("id", null);

    /*
     * check
     */
    String validationResult = target.getConnectionType("id");
    assertThat(validationResult, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryConnectionType(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryConnectionType_allNull() {

    /*
     * test
     */
    target.addEntryConnectionType(null, null);

    /*
     * check
     */
    String validationResult = target.getConnectionType(null);
    assertThat(validationResult, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryConnectionType() {

    /*
     * setting
     */
    target.addEntryConnectionType("id", "value");
    String beforeValue = target.getConnectionType("id");
    assertThat(beforeValue, is("value"));

    /*
     * test
     */
    target.delEntryConnectionType("id");

    /*
     * check
     */
    String afterValue = target.getConnectionType("id");
    assertThat(afterValue, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryConnectionType(java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryConnectionType_notRegisterId() {

    /*
     * test
     */
    target.delEntryConnectionType("id");

    /*
     * check
     */
    String afterValue = target.getConnectionType("id");
    assertThat(afterValue, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getNetwork()}.
   */
  @Test
  public void testGetNetwork() {

    /*
     * test
     */
    HashMap<String, ArrayList<String>> result = target.getNetwork();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getNode()}.
   */
  @Test
  public void testGetNode() {

    /*
     * test
     */
    HashMap<String, ArrayList<String>> result = target.getNode();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getPort()}.
   */
  @Test
  public void testGetPort() {

    /*
     * test
     */
    HashMap<String, ArrayList<String>> result = target.getPort();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getLink()}.
   */
  @Test
  public void testGetLink() {

    /*
     * test
     */
    HashMap<String, ArrayList<String>> result = target.getLink();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getFlow()}.
   */
  @Test
  public void testGetFlow() {

    /*
     * test
     */
    HashMap<String, ArrayList<String>> result = target.getFlow();

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getNetwork(java.lang.String)}
   * .
   */
  @Test
  public void testGetNetworkString() {

    /*
     * test
     */
    ArrayList<String> result = target.getNetwork("nwcId");

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getNode(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetNodeStringString() {

    /*
     * test
     */
    ArrayList<String> result = target.getNode("nwcId", "nodeId");

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getPort(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPortStringStringString() {

    /*
     * test
     */
    ArrayList<String> result = target.getPort("nwcId", "nodeId", "portId");

    /*
     * chcek
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getLink(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetLinkStringString() {

    /*
     * test
     */
    ArrayList<String> result = target.getLink("nwcId", "linkId");

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#getFlow(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetFlowStringString() {

    /*
     * test
     */
    ArrayList<String> result = target.getFlow("nwcId", "flowId");

    /*
     * check
     */
    assertThat(result.size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryNetwork(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryNetwork() {

    /*
     * test
     */
    target.addEntryNetwork("nwcId1", "nwcId2");
    target.addEntryNetwork("nwcId2", "nwcId3");
    target.addEntryNetwork("nwcId3", "nwcId1");

    /*
     * check
     */
    ArrayList<String> resultNwc1 = target.getNetwork("nwcId1");
    assertThat(resultNwc1.size(), is(2));
    ArrayList<String> resultNwc2 = target.getNetwork("nwcId2");
    assertThat(resultNwc2.size(), is(2));
    ArrayList<String> resultNwc3 = target.getNetwork("nwcId3");
    assertThat(resultNwc3.size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryNode() {

    /*
     * test
     */
    target.addEntryNode("nwcId1", "nodeId", "nwcId2", "nodeId");
    target.addEntryNode("nwcId2", "nodeId", "nwcId3", "nodeId");
    target.addEntryNode("nwcId3", "nodeId", "nwcId1", "nodeId");

    /*
     * check
     */
    ArrayList<String> resultNode1 = target.getNode("nwcId1", "nodeId");
    assertThat(resultNode1.size(), is(2));
    ArrayList<String> resultNode2 = target.getNode("nwcId2", "nodeId");
    assertThat(resultNode2.size(), is(2));
    ArrayList<String> resultNode3 = target.getNode("nwcId3", "nodeId");
    assertThat(resultNode3.size(), is(2));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryPort(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryPort() {

    /*
     * test
     */
    target.addEntryPort("nwcId1", "linkId", "portId", "nwcId2", "linkId", "portId");
    target.addEntryPort("nwcId2", "linkId", "portId", "nwcId3", "linkId", "portId");
    target.addEntryPort("nwcId3", "linkId", "portId", "nwcId1", "linkId", "portId");

    /*
     * check
     */
    ArrayList<String> resultPort1 = target.getPort("nwcId1", "linkId", "portId");
    assertThat(resultPort1.size(), is(2));
    ArrayList<String> resultPort2 = target.getPort("nwcId2", "linkId", "portId");
    assertThat(resultPort2.size(), is(2));
    ArrayList<String> resultPort3 = target.getPort("nwcId3", "linkId", "portId");
    assertThat(resultPort3.size(), is(2));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryLink(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryLink() {

    /*
     * test
     */
    target.addEntryLink("nwcId1", "linkId", "nwcId2", "linkId");
    target.addEntryLink("nwcId2", "linkId", "nwcId3", "linkId");
    target.addEntryLink("nwcId3", "linkId", "nwcId1", "linkId");

    /*
     * check
     */
    ArrayList<String> resultLink1 = target.getLink("nwcId1", "linkId");
    assertThat(resultLink1.size(), is(2));
    ArrayList<String> resultLink2 = target.getLink("nwcId2", "linkId");
    assertThat(resultLink2.size(), is(2));
    ArrayList<String> resultLink3 = target.getLink("nwcId3", "linkId");
    assertThat(resultLink3.size(), is(2));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#addEntryFlow(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testAddEntryFlow() {

    /*
     * test
     */
    target.addEntryFlow("nwcId1", "flowId", "nwcId2", "flowId");
    target.addEntryFlow("nwcId2", "flowId", "nwcId3", "flowId");
    target.addEntryFlow("nwcId3", "flowId", "nwcId1", "flowId");

    /*
     * check
     */
    ArrayList<String> resultFlow1 = target.getFlow("nwcId1", "flowId");
    assertThat(resultFlow1.size(), is(2));
    ArrayList<String> resultFlow2 = target.getFlow("nwcId2", "flowId");
    assertThat(resultFlow2.size(), is(2));
    ArrayList<String> resultFlow3 = target.getFlow("nwcId3", "flowId");
    assertThat(resultFlow3.size(), is(2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryNetwork(java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryNetwork() {

    /*
     * setting
     */
    target.addEntryNetwork("nwcId1", "nwcId2");
    target.addEntryNetwork("nwcId2", "nwcId3");
    target.addEntryNetwork("nwcId3", "nwcId1");

    /*
     * test
     */
    target.delEntryNetwork("nwcId3");

    /*
     * check
     */
    ArrayList<String> resultNwc1 = target.getNetwork("nwcId1");
    assertThat(resultNwc1.size(), is(2 - 1));
    ArrayList<String> resultNwc2 = target.getNetwork("nwcId2");
    assertThat(resultNwc2.size(), is(2 - 1));
    ArrayList<String> resultNwc3 = target.getNetwork("nwcId3");
    assertThat(resultNwc3.size(), is(2 - 2));

    target.addEntryNetwork("nwcId3", "nwcId1");

    ArrayList<String> resultNwc21 = target.getNetwork("nwcId1");
    assertThat(resultNwc21.size(), is(1 + 1));
    ArrayList<String> resultNwc22 = target.getNetwork("nwcId2");
    assertThat(resultNwc22.size(), is(1));
    ArrayList<String> resultNwc23 = target.getNetwork("nwcId3");
    assertThat(resultNwc23.size(), is(0 + 1));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryNode(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryNode() {

    /*
     * setting
     */
    target.addEntryNode("nwcId1", "nodeId", "nwcId2", "nodeId");
    target.addEntryNode("nwcId2", "nodeId", "nwcId3", "nodeId");
    target.addEntryNode("nwcId3", "nodeId", "nwcId1", "nodeId");

    /*
     * test
     */
    target.delEntryNode("nwcId3", "nodeId");

    /*
     * check
     */
    ArrayList<String> resultNode1 = target.getNode("nwcId1", "nodeId");
    assertThat(resultNode1.size(), is(2 - 1));
    ArrayList<String> resultNode2 = target.getNode("nwcId2", "nodeId");
    assertThat(resultNode2.size(), is(2 - 1));
    ArrayList<String> resultNode3 = target.getNode("nwcId3", "nodeId");
    assertThat(resultNode3.size(), is(2 - 2));

    target.addEntryNode("nwcId3", "nodeId", "nwcId1", "nodeId");

    ArrayList<String> resultNode21 = target.getNode("nwcId1", "nodeId");
    assertThat(resultNode21.size(), is(1 + 1));
    ArrayList<String> resultNode22 = target.getNode("nwcId2", "nodeId");
    assertThat(resultNode22.size(), is(1));
    ArrayList<String> resultNode23 = target.getNode("nwcId3", "nodeId");
    assertThat(resultNode23.size(), is(0 + 1));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryPort(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryPort() {

    /*
     * setting
     */
    target.addEntryPort("nwcId1", "nodeId", "portId", "nwcId2", "nodeId", "portId");
    target.addEntryPort("nwcId2", "nodeId", "portId", "nwcId3", "nodeId", "portId");
    target.addEntryPort("nwcId3", "nodeId", "portId", "nwcId1", "nodeId", "portId");

    /*
     * test
     */
    target.delEntryPort("nwcId3", "nodeId", "portId");

    /*
     * check
     */
    ArrayList<String> resultPort1 = target.getPort("nwcId1", "nodeId", "portId");
    assertThat(resultPort1.size(), is(2 - 1));
    ArrayList<String> resultPort2 = target.getPort("nwcId2", "nodeId", "portId");
    assertThat(resultPort2.size(), is(2 - 1));
    ArrayList<String> resultPort3 = target.getPort("nwcId3", "nodeId", "portId");
    assertThat(resultPort3.size(), is(2 - 2));

    target.addEntryPort("nwcId3", "nodeId", "portId", "nwcId1", "nodeId", "portId");

    ArrayList<String> resultPort21 = target.getPort("nwcId1", "nodeId", "portId");
    assertThat(resultPort21.size(), is(1 + 1));
    ArrayList<String> resultPort22 = target.getPort("nwcId2", "nodeId", "portId");
    assertThat(resultPort22.size(), is(1));
    ArrayList<String> resultPort23 = target.getPort("nwcId3", "nodeId", "portId");
    assertThat(resultPort23.size(), is(0 + 1));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryLink(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryLink() {

    /*
     * setting
     */
    target.addEntryLink("nwcId1", "linkId", "nwcId2", "linkId");
    target.addEntryLink("nwcId2", "linkId", "nwcId3", "linkId");
    target.addEntryLink("nwcId3", "linkId", "nwcId1", "linkId");

    /*
     * test
     */
    target.delEntryLink("nwcId3", "linkId");

    /*
     * check
     */
    ArrayList<String> resultLink1 = target.getLink("nwcId1", "linkId");
    assertThat(resultLink1.size(), is(2 - 1));
    ArrayList<String> resultLink2 = target.getLink("nwcId2", "linkId");
    assertThat(resultLink2.size(), is(2 - 1));
    ArrayList<String> resultLink3 = target.getLink("nwcId3", "linkId");
    assertThat(resultLink3.size(), is(2 - 2));

    target.addEntryLink("nwcId3", "linkId", "nwcId1", "linkId");

    ArrayList<String> resultLink21 = target.getLink("nwcId1", "linkId");
    assertThat(resultLink21.size(), is(1 + 1));
    ArrayList<String> resultLink22 = target.getLink("nwcId2", "linkId");
    assertThat(resultLink22.size(), is(1));
    ArrayList<String> resultLink23 = target.getLink("nwcId3", "linkId");
    assertThat(resultLink23.size(), is(0 + 1));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.ConversionTable#delEntryFlow(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDelEntryFlow() {

    /*
     * setting
     */
    target.addEntryFlow("nwcId1", "flowId", "nwcId2", "flowId");
    target.addEntryFlow("nwcId2", "flowId", "nwcId3", "flowId");
    target.addEntryFlow("nwcId3", "flowId", "nwcId1", "flowId");

    /*
     * test
     */
    target.delEntryFlow("nwcId3", "flowId");

    /*
     * check
     */
    ArrayList<String> resultFlow1 = target.getFlow("nwcId1", "flowId");
    assertThat(resultFlow1.size(), is(2 - 1));
    ArrayList<String> resultFlow2 = target.getFlow("nwcId2", "flowId");
    assertThat(resultFlow2.size(), is(2 - 1));
    ArrayList<String> resultFlow3 = target.getFlow("nwcId3", "flowId");
    assertThat(resultFlow3.size(), is(2 - 2));

    target.addEntryFlow("nwcId3", "flowId", "nwcId1", "flowId");

    ArrayList<String> resultFlow21 = target.getFlow("nwcId1", "flowId");
    assertThat(resultFlow21.size(), is(1 + 1));
    ArrayList<String> resultFlow22 = target.getFlow("nwcId2", "flowId");
    assertThat(resultFlow22.size(), is(1));
    ArrayList<String> resultFlow23 = target.getFlow("nwcId3", "flowId");
    assertThat(resultFlow23.size(), is(0 + 1));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.ConversionTable#addEntryObject(HashMap, String, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testAddEntryObject() throws Exception {

    /*
     * setting
     */
    HashMap<String, ArrayList<String>> hashObject = new HashMap<>();
    ArrayList<String> initList = new ArrayList<String>(Arrays.asList("OriginalValue"));
    hashObject.put("Key", initList);

    /*
     * test
     */
    Whitebox.invokeMethod(target, "addEntryObject", hashObject, "Key", "Value");

    /*
     * check
     */
    ArrayList<String> resultValues = hashObject.get("Key");

    assertThat(resultValues.size(), is(2));
    assertThat(resultValues.contains("Value"), is(true));
    assertThat(resultValues.contains("OriginalValue"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.ConversionTable#delEntryObject(HashMap, String)}.
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDelEntryObject() throws Exception {

    /*
     * setting
     */
    HashMap<String, ArrayList<String>> hashObject = new HashMap<>();
    ArrayList<String> initList1 = new ArrayList<String>(Arrays.asList("OriginalValue1"));
    hashObject.put("Key1", initList1);
    ArrayList<String> initList2 = new ArrayList<String>(Arrays.asList("OriginalValue2"));
    hashObject.put("Key2", initList2);
    ArrayList<String> initList3 = new ArrayList<String>(Arrays.asList("OriginalValue3"));
    hashObject.put("Key3", initList3);

    /*
     * test
     */
    Whitebox.invokeMethod(target, "delEntryObject", hashObject, "Key2");

    /*
     * check
     */
    assertThat(hashObject.containsKey("Key2"), is(false));

    assertThat(hashObject.size(), is(2));
    assertThat(hashObject.containsKey("Key1"), is(true));
    assertThat(hashObject.containsKey("Key3"), is(true));

  }

}
