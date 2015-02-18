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
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionDecIpTtlQueryTest {

  private OFPFlowActionDecIpTtlQuery target;
  private Map<String, String> actions;

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

    actions = new HashMap<String, String>();
    target = new OFPFlowActionDecIpTtlQuery(actions);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    actions = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecIpTtlQuery#OFPFlowActionDecIpTtlQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowActionDecIpTtlQuery() {

    actions = new HashMap<String, String>();
    target = new OFPFlowActionDecIpTtlQuery(actions);

    assertThat(target, is(instanceOf(OFPFlowActionDecIpTtlQuery.class)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecIpTtlQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {

    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("edge_node", "node_01");
      }
    };
    target = new OFPFlowActionDecIpTtlQuery(actions);
    assertThat(target.parse(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecIpTtlQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuperErr() {

    actions = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
      }
    };
    target = new OFPFlowActionDecIpTtlQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionDecIpTtlQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @Test
  public final void testMatchExactly() {
    target = new OFPFlowActionDecIpTtlQuery(actions);

    FlowAction action = new FlowActionOutput("test_port_id");

    assertThat(target.matchExactly(action), is(false));
  }

}
