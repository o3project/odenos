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

public class OFPFlowActionSetFieldTest {

  private OFPFlowActionSetField target;

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
    target = spy(new OFPFlowActionSetField());
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#OFPFlowActionSetField()}
   * .
   */
  @Test
  public final void testOFPFlowActionSetField() {
    OFPFlowActionSetField target = new OFPFlowActionSetField();

    assertNull(target.getMatch());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#OFPFlowActionSetField(java.lang.String)}
   * .
   */
  @Test
  public final void testOFPFlowActionSetFieldOFPFlowMatch() {
    OFPFlowMatch match = new OFPFlowMatch();
    OFPFlowActionSetField target = new OFPFlowActionSetField(match);

    assertThat(target.getMatch(), is(match));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#validate()}
   * .
   */
  @Test
  public final void testValidateNull() {
    OFPFlowActionSetField target = new OFPFlowActionSetField();
    boolean result = target.validate();

    assertThat(result, is(false));

    OFPFlowMatch match = new OFPFlowMatch();
    target = new OFPFlowActionSetField(match);
    result = target.validate();

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#getMatch()}
   * .
   */
  @Test
  public final void testGetMatch() {

    /*
     * setting
     */
    OFPFlowMatch match = new OFPFlowMatch("NodeId", "PortId");
    target = new OFPFlowActionSetField(match);

    /*
     * test
     */
    OFPFlowMatch result = target.getMatch();

    /*
     * check
     */
    assertThat(result, is(match));

    OFPFlowMatch resultMatch = Whitebox.getInternalState(target, "match");
    assertThat(resultMatch, is(match));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#setMatch(OFPFlowMatch)}
   * .
   */
  @Test
  public final void testSetMatch() {

    /*
     * setting
     */
    OFPFlowMatch match = new OFPFlowMatch("NodeId", "PortId");

    /*
     * test
     */
    target.setMatch(match);

    /*
     * check
     */
    assertThat(target.getMatch(), is(match));

    OFPFlowMatch resultMatch = Whitebox.getInternalState(target, "match");
    assertThat(resultMatch, is(match));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("OFPFlowActionSetField"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */
    Value[] valueArray = new Value[2];
    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthDst("ethDst");
    match.setEthType(10);
    match.setArpOp(222);
    match.setArpSha("arpSha");
    match.setArpTha("arpTha");
    valueArray[0] = ValueFactory.createRawValue("match");
    valueArray[1] = match.writeValue();

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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#writeValueSub(java.util.Map)}
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEquals() {
    OFPFlowActionSetField obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsInstanceofFalse() {
    boolean result = target.equals(new OFPFlowMatch());

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsSuperFalse() {
    OFPFlowActionSetField obj = new OFPFlowActionSetField();
    doReturn("false").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsNotEqualsGroupId() {
    OFPFlowActionSetField obj = new OFPFlowActionSetField();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthDst("ethDst");
    match.setEthSrc("ethSrc");
    match.setVlanVid(1000);
    target = new OFPFlowActionSetField(match);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[inNode=<null>,ethSrc=ethSrc,ethDst=ethDst,vlanVid=1000]]"),
        is(true));

  }

}
