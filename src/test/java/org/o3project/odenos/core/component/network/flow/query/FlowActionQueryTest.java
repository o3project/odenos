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
import org.junit.runner.RunWith;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FlowActionQuery.
 *
 * 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FlowActionQuery.class })
@PowerMockIgnore({"javax.management.*"})
public class FlowActionQueryTest {

  private FlowActionQuery target = null;
  private Map<String, String> params;

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
  @SuppressWarnings("serial")
  @Before
  public void setUp() throws Exception {
    params = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
        put("ccc", "ddd");
      }
    };

    target = PowerMockito.spy(new FlowActionQuery(params) {

    });

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
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#FlowActionQuery(java.util.Map)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testFlowActionQuery() {

    params = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
        put("ccc", "ddd");
      }
    };

    target = PowerMockito.spy(new FlowActionQuery(params) {

    });

    assertThat(target, is(instanceOf(FlowActionQuery.class)));
    assertThat(Whitebox.getInternalState(target, "type"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "edgeNodeId"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#FlowActionQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testFlowActionQueryNull() {

    params = null;

    target = PowerMockito.spy(new FlowActionQuery(params) {

    });

    assertThat(target, is(instanceOf(FlowActionQuery.class)));
    assertThat(Whitebox.getInternalState(target, "type"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "edgeNodeId"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#getEdgeNodeId()}
   * .
   */
  @Test
  public final void testGetEdgeNodeIdNull() {
    Whitebox.setInternalState(target, "edgeNodeId", "");
    assertThat(target.getEdgeNodeId(), is(Whitebox.getInternalState(target, "edgeNodeId")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#getEdgeNodeId()}
   * .
   */
  @Test
  public final void testGetEdgeNodeId() {
    String edgeNodeId = "edgenode01";
    Whitebox.setInternalState(target, "edgeNodeId", edgeNodeId);
    assertThat(target.getEdgeNodeId(), is(edgeNodeId));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseActionTypeSuccess() {
    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("edge_node", "edgenode02");
      }
    };
    target = new FlowActionOutputQuery(params);
    assertThat(target.parse(), is(true));
    assertThat((String) Whitebox.getInternalState(target, "edgeNodeId"), is("edgenode02"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#parse()}
   * .
   */
  @Test
  public final void testParseActionNull() {
    target = new FlowActionOutputQuery(null);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseActionTypeErr() {
    params = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
        put("ccc", "ddd");
      }
    };
    target = new FlowActionOutputQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowActionQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowAction)}
   * .
   */
  @Test
  public final void testMatchExactly() {
    FlowAction action = new FlowActionOutput("output");
    assertThat(target.matchExactly(action), is(false));
  }

}
