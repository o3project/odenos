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

package org.o3project.odenos.core.component.network.topology;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for PortQuery.
 */
public class PortQueryTest {

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
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#PortQuery(String)}
   * .
   */
  @Test
  public void testPortQuery() {
    PortQuery result = new PortQuery("test");

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#PortQuery(String)}
   * .
   */
  @Test
  public void testPortQueryWithNull() {
    PortQuery result = new PortQuery(null);

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#parse()}
   * .
   */
  @Test
  public void testParse() {
    PortQuery result = new PortQuery("attributes=att123=val123");

    assertThat(result, is(notNullValue()));
    assertTrue(result.parse());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#parse()}
   * .
   */
  @Test
  public void testParseWithoutEqual() {
    PortQuery result = new PortQuery("attributes");

    assertThat(result, is(notNullValue()));
    assertFalse(result.parse());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#parse()}
   * .
   */
  @Test
  public void testParseWithoutAttributeValue() {
    PortQuery result = new PortQuery("attributes=att123");

    assertThat(result, is(notNullValue()));
    assertFalse(result.parse());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortQuery#parse()}
   * .
   */
  @Test
  public void testParseWithoutAttributes() {
    PortQuery result = new PortQuery("port_id=id123");

    assertThat(result, is(notNullValue()));
    assertFalse(result.parse());
  }
}
