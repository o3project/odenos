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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for BasicFlowMatch.
 *
 * 
 *
 */
public class BasicFlowMatchTest {

  private BasicFlowMatch target = null;

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
    target = PowerMockito.spy(new BasicFlowMatch() {
      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return "Type";
      }

      @Override
      public boolean readValue(Value value) {
        return false;
      }
    });
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
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#BasicFlowMatch()}
   * .
   */
  @Test
  public final void testBasicFlowMatch() {
    target = PowerMockito.spy(new BasicFlowMatch() {
      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return "Type";
      }

      @Override
      public boolean readValue(Value value) {
        return false;
      }
    });
    assertThat(target, is(instanceOf(BasicFlowMatch.class)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#BasicFlowMatch(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testBasicFlowMatchStringString() {
    target = PowerMockito.spy(new BasicFlowMatch("node01", "port01") {
      @Override
      public boolean validate() {
        return false;
      }

      @Override
      public String getType() {
        return "Type";
      }

      @Override
      public boolean readValue(Value value) {
        return false;
      }
    });
    assertThat(target, is(instanceOf(BasicFlowMatch.class)));
    assertThat(target.inNode, is("node01"));
    assertThat(target.inPort, is("port01"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateNodeErr() {
    target = new BasicFlowMatch("", "port01");
    assertThat(target.validate(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidatePortErr() {
    target = new BasicFlowMatch("node01", "");
    assertThat(target.validate(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateSuccess() {
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.getType(), is("BasicFlowMatch"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#getInPort()}
   * .
   */
  @Test
  public final void testGetInPort() {
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.getInPort(), is("port01"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#setInPort(java.lang.String)}
   * .
   */
  @Test
  public final void testSetInPort() {
    target = new BasicFlowMatch();
    target.setInPort("port_a_01");
    assertThat(target.getInPort(), is("port_a_01"));
    assertThat((boolean) Whitebox.getInternalState(target, "wcInPort"), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#getInNode()}
   * .
   */
  @Test
  public final void testGetInNode() {
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.getInNode(), is("node01"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#setInNode(java.lang.String)}
   * .
   */
  @Test
  public final void testSetInNode() {
    target = new BasicFlowMatch();
    target.setInNode("node_b_02");
    assertThat(target.getInNode(), is("node_b_02"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {
    Value value = ValueFactory.createRawValue("in_node");
    assertThat(target.readValue(value), is(false));
    verify(target, times(1)).readValue(value);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {
    Map<String, Value> values = new HashMap<String, Value>();

    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.writeValueSub(values), is(true));
    Value type = values.get("type");
    Value inNode = values.get("in_node");
    Value inPort = values.get("in_port");

    assertThat(type.toString(), is("\"BasicFlowMatch\""));
    assertThat(inNode.toString(), is("\"node01\""));
    assertThat(inPort.toString(), is("\"port01\""));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    assertThat(target.equals(null), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectThis() {
    assertThat(target.equals(target), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectOtherInstance() {
    FlowActionOutput action = new FlowActionOutput();
    assertThat(target.equals(action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNodeErr() {
    BasicFlowMatch match = new BasicFlowMatch("node02", "port01");
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.equals(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectPortErr() {
    BasicFlowMatch match = new BasicFlowMatch("node01", "port02");
    target = new BasicFlowMatch("node01", "port01");

    assertThat(target.equals(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectSuccess() {
    BasicFlowMatch match = new BasicFlowMatch("node01", "port01");
    target = new BasicFlowMatch("node01", "port01");
    assertThat(target.equals(match), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new BasicFlowMatch("node01", "port01");

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[inNode=node01,inPort=port01]"), is(true));
  }

}
