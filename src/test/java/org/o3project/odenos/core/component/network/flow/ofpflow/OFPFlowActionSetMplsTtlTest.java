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
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionSetMplsTtlTest {

  private OFPFlowActionSetMplsTtl target;

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
    target = spy(new OFPFlowActionSetMplsTtl());
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#OFPFlowActionSetMplsTtl()}
   * .
   */
  @Test
  public final void testOFPFlowActionSetMplsTtl() {
    OFPFlowActionSetMplsTtl target = new OFPFlowActionSetMplsTtl();

    assertNull(target.getMplsTtl());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#OFPFlowActionSetMplsTtl(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowActionSetMplsTtlInteger() {
    OFPFlowActionSetMplsTtl target = new OFPFlowActionSetMplsTtl(1234);

    assertThat(target.getMplsTtl(), is(new Integer(1234)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#validate()}
   * .
   */
  @Test
  public final void testValidateNull() {
    OFPFlowActionSetMplsTtl target = new OFPFlowActionSetMplsTtl();
    boolean result = target.validate();

    assertThat(result, is(false));

    target = new OFPFlowActionSetMplsTtl(1234);
    result = target.validate();

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#getMplsTtl()}
   * .
   */
  @Test
  public final void testGetMplsTtl() {

    /*
     * setting
     */
    target = new OFPFlowActionSetMplsTtl(123);

    /*
     * test
     */
    Integer result = target.getMplsTtl();

    /*
     * check
     */
    assertThat(result, is(123));

    Integer resultInt = Whitebox.getInternalState(target, "mplsTtl");
    assertThat(resultInt, is(123));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#setMplsTtl(java.lang.Integer)}
   * .
   */
  @Test
  public final void testSetMplsTtl() {

    /*
     * setting
     */
    target = new OFPFlowActionSetMplsTtl(123);

    /*
     * test
     */
    target.setMplsTtl(456);

    /*
     * check
     */
    assertThat(target.getMplsTtl(), is(456));

    Integer resultInt = Whitebox.getInternalState(target, "mplsTtl");
    assertThat(resultInt, is(456));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("OFPFlowActionSetMplsTtl"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */
    Value[] valueArray = new Value[2];
    valueArray[0] = ValueFactory.createRawValue("mpls_ttl");
    valueArray[1] = ValueFactory.createIntegerValue(1234);

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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#writeValueSub(java.util.Map)}
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEquals() {
    OFPFlowActionSetMplsTtl obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsInstanceofFalse() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsSuperFalse() {
    OFPFlowActionSetMplsTtl obj = new OFPFlowActionSetMplsTtl();
    doReturn("false").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNotEqualsGroupId() {
    OFPFlowActionSetMplsTtl obj = new OFPFlowActionSetMplsTtl();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new OFPFlowActionSetMplsTtl(12345);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[mplsTtl=12345]"), is(true));

  }

}
