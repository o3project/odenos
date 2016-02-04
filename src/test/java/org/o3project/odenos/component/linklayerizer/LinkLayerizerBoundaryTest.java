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

package org.o3project.odenos.component.linklayerizer;

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
 * Test class for LinkLayerizerBoundary.
 */
public class LinkLayerizerBoundaryTest {

  private LinkLayerizerBoundary target;

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

    target = Mockito.spy(new LinkLayerizerBoundary("BoundaryId", "Type",
        "LowerNwId", "LowerNwNodeId", "LowerNwPortId",
        "UpperNwId", "UpperNwNodeId", "UpperNwPortId"));
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#LinkLayerizerBoundary()}.
   */
  @Test
  public void testLinkLayerizerBoundary() {

    /*
     * test
     */
    @SuppressWarnings("deprecation")
    LinkLayerizerBoundary result = new LinkLayerizerBoundary();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#LinkLayerizerBoundary(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLinkLayerizerBoundaryStringStringStringStringStringStringStringString() {

    /*
     * test
     */
    LinkLayerizerBoundary result = new LinkLayerizerBoundary(
        "NewBoundaryId", "NewType", "NewLowerNwId", "NewLowerNwNodeId",
        "NewLowerNwPortId", "NewUpperNwId", "NewUpperNwNodeId",
        "NewUpperNwPortId");

    /*
     * check
     */
    assertThat(result.getId(), is("NewBoundaryId"));
    assertThat(result.getType(), is("NewType"));
    assertThat(result.getLowerNw(), is("NewLowerNwId"));
    assertThat(result.getLowerNwNode(), is("NewLowerNwNodeId"));
    assertThat(result.getLowerNwPort(), is("NewLowerNwPortId"));
    assertThat(result.getUpperNw(), is("NewUpperNwId"));
    assertThat(result.getUpperNwNode(), is("NewUpperNwNodeId"));
    assertThat(result.getUpperNwPort(), is("NewUpperNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getId()}.
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
    assertThat(result, is("BoundaryId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {

    /*
     * test
     */
    target.setId("NewBoundaryId");

    /*
     * check
     */
    assertThat(target.getId(), is("NewBoundaryId"));

    String result = Whitebox.getInternalState(target, "id");
    assertThat(result, is("NewBoundaryId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getType()}.
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
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setType(java.lang.String)}.
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
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getLowerNw()}.
   */
  @Test
  public void testGetLowerNw() {

    /*
     * test
     */
    String result = target.getLowerNw();

    /*
     * check
     */
    assertThat(result, is("LowerNwId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setLowerNw(java.lang.String)}.
   */
  @Test
  public void testSetLowerNw() {

    /*
     * test
     */
    target.setLowerNw("NewLowerNwId");

    /*
     * check
     */
    assertThat(target.getLowerNw(), is("NewLowerNwId"));

    String result = Whitebox.getInternalState(target, "lowerNw");
    assertThat(result, is("NewLowerNwId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getLowerNwNode()}.
   */
  @Test
  public void testGetLowerNwNode() {

    /*
     * test
     */
    String result = target.getLowerNwNode();

    /*
     * check
     */
    assertThat(result, is("LowerNwNodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setLowerNwNode(java.lang.String)}.
   */
  @Test
  public void testSetLowerNwNode() {

    /*
     * test
     */
    target.setLowerNwNode("NewLowerNwNodeId");

    /*
     * check
     */
    assertThat(target.getLowerNwNode(), is("NewLowerNwNodeId"));

    String result = Whitebox.getInternalState(target, "lowerNwNode");
    assertThat(result, is("NewLowerNwNodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getLowerNwPort()}.
   */
  @Test
  public void testGetLowerNwPort() {

    /*
     * test
     */
    String result = target.getLowerNwPort();

    /*
     * check
     */
    assertThat(result, is("LowerNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setLowerNwPort(java.lang.String)}.
   */
  @Test
  public void testSetLowerNwPort() {

    /*
     * test
     */
    target.setLowerNwPort("NewLowerNwPort");

    /*
     * check
     */
    assertThat(target.getLowerNwPort(), is("NewLowerNwPort"));

    String result = Whitebox.getInternalState(target, "lowerNwPort");
    assertThat(result, is("NewLowerNwPort"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getUpperNw()}.
   */
  @Test
  public void testGetUpperNw() {

    /*
     * test
     */
    String result = target.getUpperNw();

    /*
     * check
     */
    assertThat(result, is("UpperNwId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setUpperNw(java.lang.String)}.
   */
  @Test
  public void testSetUpperNw() {

    /*
     * test
     */
    target.setUpperNw("NewUpperNwId");

    /*
     * check
     */
    assertThat(target.getUpperNw(), is("NewUpperNwId"));

    String result = Whitebox.getInternalState(target, "upperNw");
    assertThat(result, is("NewUpperNwId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getUpperNwNode()}.
   */
  @Test
  public void testGetUpperNwNode() {

    /*
     * test
     */
    String result = target.getUpperNwNode();

    /*
     * check
     */
    assertThat(result, is("UpperNwNodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setUpperNwNode(java.lang.String)}.
   */
  @Test
  public void testSetUpperNwNode() {

    /*
     * test
     */
    target.setUpperNwNode("NewUpperNwNodeId");

    /*
     * check
     */
    assertThat(target.getUpperNwNode(), is("NewUpperNwNodeId"));

    String result = Whitebox.getInternalState(target, "upperNwNode");
    assertThat(result, is("NewUpperNwNodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#getUpperNwPort()}.
   */
  @Test
  public void testGetUpperNwPort() {

    /*
     * test
     */
    String result = target.getUpperNwPort();

    /*
     * check
     */
    assertThat(result, is("UpperNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#setUpperNwPort(java.lang.String)}.
   */
  @Test
  public void testSetUpperNwPort() {

    /*
     * test
     */
    target.setUpperNwPort("NewUpperNwPortId");

    /*
     * check
     */
    assertThat(target.getUpperNwPort(), is("NewUpperNwPortId"));

    String result = Whitebox.getInternalState(target, "upperNwPort");
    assertThat(result, is("NewUpperNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#validate()}.
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
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#readValue(org.msgpack.type.Value)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testReadValue() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary = new LinkLayerizerBoundary(
        "NewBoundaryId", "NewType", "NewLowerNwId", "NewLowerNwNodeId",
        "NewLowerNwPortId", "NewUpperNwId", "NewUpperNwNodeId",
        "NewUpperNwPortId");

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

    assertThat(target.getId(), is("NewBoundaryId"));
    assertThat(target.getType(), is("NewType"));
    assertThat(target.getLowerNw(), is("NewLowerNwId"));
    assertThat(target.getLowerNwNode(), is("NewLowerNwNodeId"));
    assertThat(target.getLowerNwPort(), is("NewLowerNwPortId"));
    assertThat(target.getUpperNw(), is("NewUpperNwId"));
    assertThat(target.getUpperNwNode(), is("NewUpperNwNodeId"));
    assertThat(target.getUpperNwPort(), is("NewUpperNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#writeValueSub(java.util.Map)}.
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
    assertThat(resultMap.get("id").asRawValue().getString(),
        is("BoundaryId"));
    assertThat(resultMap.get("type").asRawValue().getString(), is("Type"));
    assertThat(resultMap.get("lower_nw").asRawValue().getString(),
        is("LowerNwId"));
    assertThat(resultMap.get("lower_nw_node").asRawValue().getString(),
        is("LowerNwNodeId"));
    assertThat(resultMap.get("lower_nw_port").asRawValue().getString(),
        is("LowerNwPortId"));
    assertThat(resultMap.get("upper_nw").asRawValue().getString(),
        is("UpperNwId"));
    assertThat(resultMap.get("upper_nw_node").asRawValue().getString(),
        is("UpperNwNodeId"));
    assertThat(resultMap.get("upper_nw_port").asRawValue().getString(),
        is("UpperNwPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#hashCode()}.
   */
  @Test
  public void testHashCode() {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary11 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    LinkLayerizerBoundary boundary12 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");

    @SuppressWarnings("deprecation")
    LinkLayerizerBoundary boundary31 = new LinkLayerizerBoundary();
    @SuppressWarnings("deprecation")
    LinkLayerizerBoundary boundary32 = new LinkLayerizerBoundary();

    /*
     * test
     */
    int result11 = boundary11.hashCode();
    int result12 = boundary12.hashCode();
    int result2 = boundary2.hashCode();
    int result31 = boundary31.hashCode();
    int result32 = boundary32.hashCode();

    /*
     * check
     */
    assertThat((result11 == result12), is(true));
    assertThat((result11 == result2), is(false));
    assertThat((result31 == result32), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#equals(java.lang.Object)}.
   */
  @Test
  public void testEquals_SameObject() {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary11 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    LinkLayerizerBoundary boundary12 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    /*
     * test
     */
    boolean result11 = boundary11.equals(boundary11);
    boolean result12 = boundary11.equals(boundary12);

    /*
     * check
     */
    assertThat(result11, is(true));
    assertThat(result12, is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#equals(java.lang.Object)}.
   */
  @Test
  public void testEquals_DifferObject() {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary11 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");

    /*
     * test
     */
    boolean result2 = boundary11.equals(boundary2);

    /*
     * check
     */
    assertThat(result2, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#equals(java.lang.Object)}.
   */
  @Test
  public void testEquals_DefaultObject() {

    /*
     * setting
     */
    @SuppressWarnings("deprecation")
    LinkLayerizerBoundary boundary31 = new LinkLayerizerBoundary();
    @SuppressWarnings("deprecation")
    LinkLayerizerBoundary boundary32 = new LinkLayerizerBoundary();

    /*
     * test
     */
    boolean result31 = boundary31.equals(boundary31);
    boolean result32 = boundary31.equals(boundary32);

    /*
     * check
     */
    assertThat(result31, is(true));
    assertThat(result32, is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary#toString()}.
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
    String expectStr = "[id=BoundaryId,type=Type,"
        + "lowerNw=LowerNwId,lowerNwNode=LowerNwNodeId,lowerNwPort=LowerNwPortId,"
        + "upperNw=UpperNwId,upperNwNode=UpperNwNodeId,upperNwPort=UpperNwPortId]";

    assertThat(result.endsWith(expectStr), is(true));
  }

}
