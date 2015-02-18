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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FederatorBoundary.
 */
public class FederatorBoundaryTest {

  private FederatorBoundary target;

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
    target = Mockito.spy(new FederatorBoundary("Id", "Type",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2"));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#FederatorBoundary()}.
   */
  @Test
  public void testFederatorBoundary() {

    /*
     * test
     */
    @SuppressWarnings("deprecation")
    FederatorBoundary result = new FederatorBoundary();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#FederatorBoundary(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testFederatorBoundaryStringStringStringStringStringStringStringString() {

    /*
     * test
     */
    FederatorBoundary result = new FederatorBoundary("NewId", "NewType",
        "NewNetwork1", "NewNode1", "NewPort1",
        "NewNetwork2", "NewNode2", "NewPort2");

    /*
     * check
     */
    assertThat(result.getId(), is("NewId"));
    assertThat(result.getType(), is("NewType"));
    assertThat(result.getNetwork1(), is("NewNetwork1"));
    assertThat(result.getNode1(), is("NewNode1"));
    assertThat(result.getPort1(), is("NewPort1"));
    assertThat(result.getNetwork2(), is("NewNetwork2"));
    assertThat(result.getNode2(), is("NewNode2"));
    assertThat(result.getPort2(), is("NewPort2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#isContains(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testIsContains() {

    /*
     * test
     */
    boolean result1 = target.isContains("Network1", "Node1", "Port1");
    boolean result2 = target.isContains("Network2", "Node2", "Port2");

    boolean resultNone = target.isContains("Network1", "Node2", "Port1");

    /*
     * check
     */
    assertThat(result1, is(true));
    assertThat(result2, is(true));

    assertThat(resultNone, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getId()}.
   */
  @Test
  public void testGetId() {

    /*
     * test
     */
    String result = target.getId();

    /*
     * check
     */
    assertThat(result, is("Id"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {

    /*
     * test
     */
    target.setId("NewId");

    /*
     * check
     */
    assertThat(target.getId(), is("NewId"));

    String result = Whitebox.getInternalState(target, "id");
    assertThat(result, is("NewId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getType()}.
   */
  @Test
  public void testGetType() {

    /*
     * test
     */
    String result = target.getType();

    /*
     * check
     */
    assertThat(result, is("Type"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setType(java.lang.String)}.
   */
  @Test
  public void testSetType() {

    /*
     * test
     */
    target.setType("NewType");

    /*
     * check
     */
    assertThat(target.getType(), is("NewType"));

    String result = Whitebox.getInternalState(target, "type");
    assertThat(result, is("NewType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getNetwork1()}.
   */
  @Test
  public void testGetNetwork1() {

    /*
     * test
     */
    String result = target.getNetwork1();

    /*
     * check
     */
    assertThat(result, is("Network1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setNetwork1(java.lang.String)}.
   */
  @Test
  public void testSetNetwork1() {

    /*
     * test
     */
    target.setNetwork1("NewNetwork1");

    /*
     * check
     */
    assertThat(target.getNetwork1(), is("NewNetwork1"));

    String result = Whitebox.getInternalState(target, "network1");
    assertThat(result, is("NewNetwork1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getNetwork2()}.
   */
  @Test
  public void testGetNetwork2() {

    /*
     * test
     */
    String result = target.getNetwork2();

    /*
     * check
     */
    assertThat(result, is("Network2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setNetwork2(java.lang.String)}.
   */
  @Test
  public void testSetNetwork2() {

    /*
     * test
     */
    target.setNetwork2("NewNetwork2");

    /*
     * check
     */
    assertThat(target.getNetwork2(), is("NewNetwork2"));

    String result = Whitebox.getInternalState(target, "network2");
    assertThat(result, is("NewNetwork2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getNode1()}.
   */
  @Test
  public void testGetNode1() {

    /*
     * test
     */
    String result = target.getNode1();

    /*
     * check
     */
    assertThat(result, is("Node1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setNode1(java.lang.String)}.
   */
  @Test
  public void testSetNode1() {

    /*
     * test
     */
    target.setNode1("NewNode1");

    /*
     * check
     */
    assertThat(target.getNode1(), is("NewNode1"));

    String result = Whitebox.getInternalState(target, "node1");
    assertThat(result, is("NewNode1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getNode2()}.
   */
  @Test
  public void testGetNode2() {

    /*
     * test
     */
    String result = target.getNode2();

    /*
     * check
     */
    assertThat(result, is("Node2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setNode2(java.lang.String)}.
   */
  @Test
  public void testSetNode2() {

    /*
     * test
     */
    target.setNode2("NewNode2");

    /*
     * check
     */
    assertThat(target.getNode2(), is("NewNode2"));

    String result = Whitebox.getInternalState(target, "node2");
    assertThat(result, is("NewNode2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getPort1()}.
   */
  @Test
  public void testGetPort1() {

    /*
     * test
     */
    String result = target.getPort1();

    /*
     * check
     */
    assertThat(result, is("Port1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setPort1(java.lang.String)}.
   */
  @Test
  public void testSetPort1() {

    /*
     * test
     */
    target.setPort1("NewPort1");

    /*
     * check
     */
    assertThat(target.getPort1(), is("NewPort1"));

    String result = Whitebox.getInternalState(target, "port1");
    assertThat(result, is("NewPort1"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#getPort2()}.
   */
  @Test
  public void testGetPort2() {

    /*
     * test
     */
    String result = target.getPort2();

    /*
     * check
     */
    assertThat(result, is("Port2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#setPort2(java.lang.String)}.
   */
  @Test
  public void testSetPort2() {

    /*
     * test
     */
    target.setPort2("NewPort2");

    /*
     * check
     */
    assertThat(target.getPort2(), is("NewPort2"));

    String result = Whitebox.getInternalState(target, "port2");
    assertThat(result, is("NewPort2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#validate()}.
   */
  @Test
  public void testValidate() {

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#readValue(org.msgpack.type.Value)}.
   * @throws Exception
   */
  @Test
  public void testReadValue() throws Exception {

    /*
     * setting
     */
    FederatorBoundary boundary = new FederatorBoundary("NewId", "NewType",
        "NewNetwork1", "NewNode1", "NewPort1",
        "NewNetwork2", "NewNode2", "NewPort2");

    MessagePack pack = new MessagePack();
    Value value = pack.unconvert(boundary);

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(target.getId(), is("NewId"));
    assertThat(target.getType(), is("NewType"));
    assertThat(target.getNetwork1(), is("NewNetwork1"));
    assertThat(target.getNode1(), is("NewNode1"));
    assertThat(target.getPort1(), is("NewPort1"));
    assertThat(target.getNetwork2(), is("NewNetwork2"));
    assertThat(target.getNode2(), is("NewNode2"));
    assertThat(target.getPort2(), is("NewPort2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#writeValueSub(java.util.Map)}.
   */
  @Test
  public void testWriteValueSub() {

    /*
     * setting
     */
    Map<String, Value> resultMap = new HashMap<>();

    /*
     * test
     */
    boolean result = target.writeValueSub(resultMap);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(resultMap.size(), is(8));
    assertThat(resultMap.get("id").asRawValue().getString(), is("Id"));
    assertThat(resultMap.get("type").asRawValue().getString(), is("Type"));
    assertThat(resultMap.get("network1").asRawValue().getString(),
        is("Network1"));
    assertThat(resultMap.get("node1").asRawValue().getString(),
        is("Node1"));
    assertThat(resultMap.get("port1").asRawValue().getString(),
        is("Port1"));
    assertThat(resultMap.get("network2").asRawValue().getString(),
        is("Network2"));
    assertThat(resultMap.get("node2").asRawValue().getString(),
        is("Node2"));
    assertThat(resultMap.get("port2").asRawValue().getString(),
        is("Port2"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundary#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectStr = "[id=Id,type=Type,"
        + "network1=Network1,node1=Node1,port1=Port1,"
        + "network2=Network2,node2=Node2,port2=Port2]";

    assertThat(result.endsWith(expectStr), is(true));

  }
}
