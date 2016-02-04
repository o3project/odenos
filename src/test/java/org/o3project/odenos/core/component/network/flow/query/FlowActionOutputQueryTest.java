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

package org.o3project.odenos.core.component.network.flow.query;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FlowActionOutputQuery.
 *
 * 
 *
 */
public class FlowActionOutputQueryTest {

  private FlowActionOutputQuery target;
  private Map<String, String> params;
  private static final String OUTPUT = "outputString";

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
    params = new HashMap<String, String>();
    target = new FlowActionOutputQuery(params);
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    params = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#FlowActionOutputQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testFlowActionOutputQuery() {
    params = new HashMap<String, String>();
    target = new FlowActionOutputQuery(params);

    assertThat(target, is(instanceOf(FlowActionOutputQuery.class)));
    assertThat((String) WhiteboxImpl.getInternalState(target, "output"),
        nullValue(String.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParse() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("output", OUTPUT);
      }
    };
    target = new FlowActionOutputQuery(params);
    assertThat(target.parse(), is(true));
    assertThat((String) WhiteboxImpl.getInternalState(target, "output"), is(OUTPUT));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#parse()}
   * .
   */
  @Test
  public final void testParseSuperErr() {

    target = new FlowActionOutputQuery(null);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseExactlyErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("bbb", "ccc");
      }
    };
    target = new FlowActionOutputQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("output", OUTPUT);
      }
    };
    FlowActionOutput action = new FlowActionOutput();
    action.output = "action";

    target = new FlowActionOutputQuery(params);
    target.parse();

    assertThat(target.matchExactly(action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("output", OUTPUT);
      }
    };
    FlowActionOutput action = new FlowActionOutput();
    action.output = OUTPUT;

    target = new FlowActionOutputQuery(params);
    target.parse();

    assertThat(target.matchExactly(action), is(true));
  }

}
