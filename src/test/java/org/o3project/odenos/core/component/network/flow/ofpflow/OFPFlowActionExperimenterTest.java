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
import static org.hamcrest.CoreMatchers.nullValue;
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

public class OFPFlowActionExperimenterTest {

  private OFPFlowActionExperimenter target;

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
    target = spy(new OFPFlowActionExperimenter());
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#OFPFlowActionExperimenter()}
   * .
   */
  @Test
  public final void testOFPFlowActionExperimenter() {
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter();

    assertThat(target.getExperimenterId(), is(nullValue()));
    assertThat(target.getBody(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#OFPFlowActionExperimenter(java.lang.Integer, byte[])}
   * .
   */
  @Test
  public final void testOFPFlowActionExperimenterIntegerByteArray() {
    /*
     * set
     */
    byte[] body = { 1, 10 };

    /*
     * test
     */
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter(12345679, body);

    /*
     * check
     */
    assertThat(target.getExperimenterId(), is(12345679));
    assertThat(target.getBody(), is(body));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#validate()}
   * .
   */
  @Test
  public final void testValidate() {
    boolean result = target.validate();

    assertThat(result, is(false));

    target.setExperimenterId(1);
    result = target.validate();

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#getExperimenterId()}
   * .
   */
  @Test
  public final void testGetExperimenterId() {
    /*
     * set
     */
    byte[] body = { 1, 10 };
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter(12345679, body);

    /*
     * test
     */
    Integer result = target.getExperimenterId();

    /*
     * check
     */
    assertThat(result, is(12345679));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#setExperimenterId(java.lang.Integer)}
   * .
   */
  @Test
  public final void testSetExperimenterId() {
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter();

    target.setExperimenterId(987654321);

    assertThat(target.getExperimenterId(), is(987654321));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#getBody()}
   * .
   */
  @Test
  public final void testGetBody() {
    /*
     * set
     */
    byte[] body = { 1, 10 };
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter(12345679, body);

    /*
     * test
     */
    byte[] result = target.getBody();

    /*
     * check
     */
    assertThat(result, is(body));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#setBody(byte[])}
   * .
   */
  @Test
  public final void testSetBody() {
    /*
     * set
     */
    byte[] body = { 1, 10 };
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter();

    /*
     * test
     */
    target.setBody(body);

    /*
     * check
     */
    assertThat(target.getBody(), is(body));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    String result = target.getType();

    assertThat(result, is("OFPFlowActionExperimenter"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    /*
     * set
     */
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("experimenter_id");
    valueArray[1] = ValueFactory.createIntegerValue(123456789);
    valueArray[2] = ValueFactory.createRawValue("body");
    byte[] body = { 1, 10 };
    valueArray[3] = ValueFactory.createRawValue(body);

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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {
    /*
     * set
     */
    byte[] body = { 1, 10 };
    OFPFlowActionExperimenter target = new OFPFlowActionExperimenter(123456789, body);
    Map<String, Value> values = new HashMap<String, Value>();

    /*
     * test
     */
    boolean result = target.writeValueSub(values);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObject() {
    OFPFlowActionExperimenter obj = target;

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseInstanceof() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseSuper() {
    OFPFlowActionExperimenter obj = new OFPFlowActionExperimenter();
    doReturn("Type").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotEqualsVendorId() {
    OFPFlowActionExperimenter obj = new OFPFlowActionExperimenter();
    obj.setExperimenterId(123456789);

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotEqualsBody() {
    /*
     * set
     */
    byte[] body = { 1, 10 };
    OFPFlowActionExperimenter obj = new OFPFlowActionExperimenter();
    obj.setBody(body);
    obj.setExperimenterId(123456789);

    target.setExperimenterId(123456789);

    /*
     * test
     */
    boolean result = target.equals(obj);

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    byte[] body = { 1, 10 };
    target = new OFPFlowActionExperimenter(12345, body);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[experimenterId=12345,body={1,10}]"), is(true));
  }

}
