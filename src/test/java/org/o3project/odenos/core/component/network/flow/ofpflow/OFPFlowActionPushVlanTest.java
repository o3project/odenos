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

package org.o3project.odenos.core.component.network.flow.ofpflow;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionPushVlanTest {

  private OFPFlowActionPushVlan target;

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
    target = spy(new OFPFlowActionPushVlan());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#OFPFlowActionPushVlan()}
   * .
   */
  @Test
  public final void testOFPFlowActionPushVlan() {
    OFPFlowActionPushVlan target = new OFPFlowActionPushVlan();

    assertNull(target.getEthType());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#OFPFlowActionPushVlan(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowActionPushVlanInteger() {
    OFPFlowActionPushVlan target = new OFPFlowActionPushVlan(OFPFlowActionPushVlan.C_VTAG);

    assertThat(target.getEthType(), is(new Integer(OFPFlowActionPushVlan.C_VTAG)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#validate()}
   * .
   */
  @Test
  public final void testValidateNull() {
    OFPFlowActionPushVlan target = new OFPFlowActionPushVlan();
    boolean result = target.validate();

    assertThat(result, is(false));

    target = new OFPFlowActionPushVlan(OFPFlowActionPushVlan.C_VTAG);
    result = target.validate();

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("OFPFlowActionPushVlan"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */
    Value[] valueArray = new Value[2];
    valueArray[0] = ValueFactory.createRawValue("eth_type");
    valueArray[1] = ValueFactory.createIntegerValue(OFPFlowActionPushVlan.C_VTAG);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {
    Map<String, Value> values = new HashMap<String, Value>();

    boolean result = target.writeValueSub(values);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEquals() {
    OFPFlowActionPushVlan obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsInstanceofFalse() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsSuperFalse() {
    OFPFlowActionPushVlan obj = new OFPFlowActionPushVlan();
    doReturn("false").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNotEqualsGroupId() {
    OFPFlowActionPushVlan obj = new OFPFlowActionPushVlan();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new OFPFlowActionPushVlan(OFPFlowActionPushVlan.C_VTAG);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expect = String.format("[ethType=%s]", OFPFlowActionPushVlan.C_VTAG);
    assertThat(result.endsWith(expect), is(true));

  }

}
