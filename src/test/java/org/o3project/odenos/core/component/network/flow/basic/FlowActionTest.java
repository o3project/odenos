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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msgpack.type.Value;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FlowAction.
 *
 * 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FlowAction.class })
@PowerMockIgnore({"javax.management.*"})
public class FlowActionTest {

  private FlowAction target = null;

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

    target = PowerMockito.spy(new FlowAction() {
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

      @Override
      public FlowAction clone() {
        return target; // not clone
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
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#FlowAction()}
   * .
   */
  @Test
  public final void testFlowAction() {
    target = PowerMockito.spy(new FlowAction() {
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

      @Override
      public FlowAction clone() {
        return target; // not clone
      }
    });
    assertThat(target, is(instanceOf(FlowAction.class)));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#validate()}
   * .
   */
  @Test
  public final void testValidate() {
    assertThat(target.validate(), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("Type"));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {

    Map<String, Value> values = new HashMap<String, Value>();

    assertThat(target.writeValueSub(values), is(true));

  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectSuccess() {
    FlowAction obj = new FlowAction() {

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

      @Override
      public FlowAction clone() {
        return this; // not clone
      }

    };

    FlowAction target = new FlowAction() {

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

      @Override
      public FlowAction clone() {
        return this; // not clone
      }

    };

    assertThat(target.equals(obj), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    Object obj = null;
    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectThis() {
    assertThat(target.equals(target), is(true));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotFlowAction() {
    BasicFlowMatch obj = new BasicFlowMatch();
    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.org.o3project.odenos.component.network.flow.basic.FlowAction#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectWithDifferentType() {
    FlowAction obj = new FlowActionOutput();
    assertThat(target.equals(obj), is(false));
  }

}
