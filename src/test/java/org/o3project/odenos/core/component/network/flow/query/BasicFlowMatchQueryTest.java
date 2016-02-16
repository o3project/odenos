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
import org.mockito.internal.util.reflection.Whitebox;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for BasicFlowMatchQuery.
 *
 * 
 *
 */
public class BasicFlowMatchQueryTest {

  private BasicFlowMatchQuery target = null;
  private Map<String, String> match;

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
    match = new HashMap<String, String>();
    target = new BasicFlowMatchQuery(match);
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    match = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#BasicFlowMatchQuery()}
   * .
   */
  @Test
  public final void testBasicFlowMatchQuery() {
    match = new HashMap<String, String>();
    target = new BasicFlowMatchQuery(match);
    assertThat(target, is(instanceOf(BasicFlowMatchQuery.class)));
    assertThat(Whitebox.getInternalState(target, "type"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "inNode"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "inPort"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#BasicFlowMatchQuery()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testBasicFlowMatchQueryWithParameter() {
    match = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
        put("ccc", "ddd");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target, is(instanceOf(BasicFlowMatchQuery.class)));
    assertThat(Whitebox.getInternalState(target, "type"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "inNode"), is(nullValue()));
    assertThat(Whitebox.getInternalState(target, "inPort"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @Test
  public final void testParseMatchNull() {
    target = new BasicFlowMatchQuery(null);
    assertThat(target.parse(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseTypeErr() {
    match = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
        put("ccc", "ddd");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNodePortErr() {
    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("bbb", "ccc");
        put("ddd", "eee");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNodeErr() {
    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "bbb");
        put("ddd", "eee");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParsePortErr() {
    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_port", "bbb");
        put("ddd", "eee");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {
    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
      }
    };
    target = new BasicFlowMatchQuery(match);
    assertThat(target.parse(), is(true));
    assertThat((String) Whitebox.getInternalState(target, "inNode"), is("node01"));
    assertThat((String) Whitebox.getInternalState(target, "inPort"), is("port01"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {
    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
      }
    };
    target = new BasicFlowMatchQuery(match);
    target.parse();

    BasicFlowMatch param = new BasicFlowMatch("node01", "port01");
    assertThat(target.matchExactly(param), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyNodeErr() {

    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
      }
    };
    target = new BasicFlowMatchQuery(match);
    target.parse();

    BasicFlowMatch param = new BasicFlowMatch("node_02", "port01");
    assertThat(target.matchExactly(param), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyPortErr() {

    match = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
      }
    };
    target = new BasicFlowMatchQuery(match);
    target.parse();

    BasicFlowMatch param = new BasicFlowMatch("node01", "port_02");
    assertThat(target.matchExactly(param), is(false));

  }

}
