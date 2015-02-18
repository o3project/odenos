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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionPushVlanQueryTest {

  private OFPFlowActionPushVlanQuery target;
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
    target = new OFPFlowActionPushVlanQuery(actions);
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
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#OFPFlowActionPushVlanQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowActionPushVlanQuery() {
    actions = new HashMap<String, String>();
    target = new OFPFlowActionPushVlanQuery(actions);

    assertThat(target, is(instanceOf(OFPFlowActionPushVlanQuery.class)));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ethType"), nullValue(Integer.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_type", "12");
      }
    };
    target = new OFPFlowActionPushVlanQuery(actions);
    assertThat(target.parse(), is(true));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ethType"),
        is(Integer.valueOf(actions.get("eth_type"))));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#parse()}
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
    target = new OFPFlowActionPushVlanQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseExactlyErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth", "12");
      }
    };
    target = new OFPFlowActionPushVlanQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNotIntErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_type", "virtual01");
      }
    };
    target = new OFPFlowActionPushVlanQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_type", "12");
      }
    };

    target = new OFPFlowActionPushVlanQuery(actions);
    target.parse();
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ethType"),
        is(notNullValue(Integer.class)));

    OFPFlowActionPushVlan action = new OFPFlowActionPushVlan();
    action.setEthType(12);
    assertThat(target.matchExactly((FlowAction) action), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyFalse() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_type", "12");
      }
    };

    target = new OFPFlowActionPushVlanQuery(actions);
    target.parse();
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ethType"),
        is(notNullValue(Integer.class)));

    OFPFlowActionPushVlan action = new OFPFlowActionPushVlan();
    action.setEthType(20);
    assertThat(target.matchExactly((FlowAction) action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionPushVlanQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyVlanNull() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_type", "12");
      }
    };

    target = new OFPFlowActionPushVlanQuery(actions);

    OFPFlowActionPushVlan action = new OFPFlowActionPushVlan();
    assertThat(target.matchExactly((FlowAction) action), is(true));
  }

}
