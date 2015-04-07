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

package org.o3project.odenos.core.component.network.flow.basic;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.MapValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FlowActionOutput.
 *
 * 
 *
 */
public class FlowActionOutputTest {

  private FlowActionOutput target = null;
  private String output = "output";

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

    target = new FlowActionOutput();

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
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#FlowActionOutput()}
   * .
   */
  @Test
  public final void testFlowActionOutput() {

    target = new FlowActionOutput();
    assertThat(target, is(instanceOf(FlowActionOutput.class)));
    assertThat(target.output, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#FlowActionOutput(java.lang.String)}
   * .
   */
  @Test
  public final void testFlowActionOutputString() {
    target = new FlowActionOutput(output);
    assertThat(target, is(instanceOf(FlowActionOutput.class)));
    assertThat(target.output, is("output"));

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#validate()}
   * .
   */
  @Test
  public final void testValidateTrue() {
    target = new FlowActionOutput(output);

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#validate()}
   * .
   */
  @Test
  public final void testValidateFalse() {
    target = new FlowActionOutput();

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#validate()}
   * .
   */
  @Test
  public final void testValidateFalseNull() {
    target = new FlowActionOutput(null);

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#validate()}
   * .
   */
  @Test
  public final void testValidateFalseBlank() {
    output = "";
    target = new FlowActionOutput(output);

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#getType()}
   * .
   */
  @Test
  public final void testGetType() {

    assertThat(target.getType(), is("FlowActionOutput"));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#getOutput()}
   * .
   */
  @Test
  public final void testGetOutput() {

    target = new FlowActionOutput(output);

    assertThat(target.getOutput(), is(output));

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#setOutput(java.lang.String)}
   * .
   */
  @Test
  public final void testSetOutput() {

    target.setOutput("newOutput");
    assertThat(target.getOutput(), is("newOutput"));

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#readValue(org.msgpack.type.Value)}
   * .
   */

  @Test
  public final void testReadValue() {

    Value value = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    Value outputvalue = Mockito.mock(Value.class);
    RawValue raw = Mockito.mock(RawValue.class);

    doReturn(map).when(value).asMapValue();
    when(map.get(ValueFactory.createRawValue("output"))).thenReturn(outputvalue);
    when(outputvalue.asRawValue()).thenReturn(raw);
    when(raw.getString()).thenReturn("testOutput");

    assertThat(target.readValue(value), is(true));

    assertThat(target.output, is("testOutput"));

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#writeValueSub(java.util.Map)}
   * .
   */

  @Test
  public final void testWriteValueSubSuccess() {

    Map<String, Value> values = new HashMap<String, Value>();

    target = Mockito.spy(new FlowActionOutput("testOutput"));

    assertThat(target.writeValueSub(values), is(true));
    assertThat(values.get("type").toString(), is("\"FlowActionOutput\""));
    assertThat(values.get("output").toString(), is("\"testOutput\""));

    verify(target, times(1)).writeValueSub(values);

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsObjecttNull() {

    assertThat(target.equals(null), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsObjectThis() {

    assertThat(target.equals(target), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsOtherInstance() {

    BasicFlowMatch match = new BasicFlowMatch();
    assertThat(target.equals(match), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsSuper() {

    FlowActionOutput obj = Mockito.spy(new FlowActionOutput());
    when(obj.getType()).thenReturn("aaa");

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsDifferentOutput() {

    FlowActionOutput obj = new FlowActionOutput();
    obj.setOutput("output1");
    target.setOutput("output2");

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#equals(java.lang.Object)}
   * .
   */

  @Test
  public final void testEqualsSuccess() {

    FlowActionOutput obj = Mockito.spy(new FlowActionOutput());
    obj.setOutput("output1");
    when(obj.getType()).thenReturn("FlowActionOutput");
    target.setOutput("output1");

    assertThat(target.equals(obj), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowActionOutput#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new FlowActionOutput("Output");

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[output=Output]"), is(true));
  }

}
