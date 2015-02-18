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

public class OFPFlowActionSetQueueTest {

  private OFPFlowActionSetQueue target;

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
    target = spy(new OFPFlowActionSetQueue());
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#OFPFlowActionSetQueue()}
   * .
   */
  @Test
  public final void testOFPFlowActionSetQueue() {
    OFPFlowActionSetQueue target = new OFPFlowActionSetQueue();

    assertNull(target.getQueueId());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#OFPFlowActionSetQueue(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowActionSetQueueInteger() {
    OFPFlowActionSetQueue target = new OFPFlowActionSetQueue(1234);

    assertThat(target.getQueueId(), is(new Integer(1234)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#getQueueId()}
   * .
   */
  @Test
  public final void testGetQueueId() {

    /*
     * setting
     */
    target = new OFPFlowActionSetQueue(123);

    /*
     * test
     */
    Integer result = target.getQueueId();

    /*
     * check
     */
    assertThat(result, is(123));

    Integer resultInt = Whitebox.getInternalState(target, "queueId");
    assertThat(resultInt, is(123));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#setQueueId(java.lang.Integer)}
   * .
   */
  @Test
  public final void testSetQueueId() {

    /*
     * setting
     */
    target = new OFPFlowActionSetQueue(123);

    /*
     * test
     */
    target.setQueueId(456);

    /*
     * check
     */
    assertThat(target.getQueueId(), is(456));

    Integer resultInt = Whitebox.getInternalState(target, "queueId");
    assertThat(resultInt, is(456));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#validate()}
   * .
   */
  @Test
  public final void testValidateNull() {
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("OFPFlowActionSetQueue"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */
    Value[] valueArray = new Value[2];
    valueArray[0] = ValueFactory.createRawValue("queue_id");
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#writeValueSub(java.util.Map)}
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEquals() {
    OFPFlowActionSetQueue obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsInstanceofFalse() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsSuperFalse() {
    OFPFlowActionSetQueue obj = new OFPFlowActionSetQueue();
    doReturn("false").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNotEqualsGroupId() {
    OFPFlowActionSetQueue obj = new OFPFlowActionSetQueue();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetQueue#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new OFPFlowActionSetQueue(12345);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[queueId=12345]"), is(true));

  }

}
