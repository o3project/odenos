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
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetIpTtl;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionSetIpTtlQueryTest {

  private OFPFlowActionSetIpTtlQuery target;
  private Map<String, String> actions;

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
    actions = new HashMap<String, String>();
    target = new OFPFlowActionSetIpTtlQuery(actions);
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    actions = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#OFPFlowActionSetIpTtlQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowActionSetIpTtlQuery() {
    actions = new HashMap<String, String>();
    target = new OFPFlowActionSetIpTtlQuery(actions);

    assertThat(target, is(instanceOf(OFPFlowActionSetIpTtlQuery.class)));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ipTtl"), nullValue(Integer.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip_ttl", "12");
      }
    };
    target = new OFPFlowActionSetIpTtlQuery(actions);
    assertThat(target.parse(), is(true));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ipTtl"),
        is(Integer.valueOf(actions.get("ip_ttl"))));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#parse()}
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
    target = new OFPFlowActionSetIpTtlQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseExactlyErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip", "12");
      }
    };
    target = new OFPFlowActionSetIpTtlQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNotIntErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip_ttl", "virtual01");
      }
    };
    target = new OFPFlowActionSetIpTtlQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip_ttl", "12");
      }
    };

    target = new OFPFlowActionSetIpTtlQuery(actions);
    target.parse();
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ipTtl"),
        is(notNullValue(Integer.class)));

    OFPFlowActionSetIpTtl action = new OFPFlowActionSetIpTtl();
    action.setIpTtl(12);
    assertThat(target.matchExactly((FlowAction) action), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyFalse() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip_ttl", "12");
      }
    };

    target = new OFPFlowActionSetIpTtlQuery(actions);
    target.parse();
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ipTtl"),
        is(notNullValue(Integer.class)));

    OFPFlowActionSetIpTtl action = new OFPFlowActionSetIpTtl();
    action.setIpTtl(20);
    assertThat(target.matchExactly((FlowAction) action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetIpTtlQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyVlanNull() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("ip_ttl", "12");
      }
    };

    target = new OFPFlowActionSetIpTtlQuery(actions);

    OFPFlowActionSetIpTtl action = new OFPFlowActionSetIpTtl();
    assertThat(target.matchExactly((FlowAction) action), is(true));
  }

}
