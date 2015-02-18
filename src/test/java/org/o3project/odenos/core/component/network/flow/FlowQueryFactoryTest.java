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

package org.o3project.odenos.core.component.network.flow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.query.BasicFlowQuery;
import org.o3project.odenos.core.component.network.flow.query.FlowQuery;
import org.o3project.odenos.core.component.network.flow.query.OFPFlowQuery;
import org.powermock.reflect.Whitebox;

/**
 * Test class for FlowQueryFactory.
 */
public class FlowQueryFactoryTest {

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
    /* do nothing */
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    /* do nothing */
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#FlowQueryFactory()}
   * .
   */
  /* not test */
  public void testFlowQueryFactory() {

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#create(java.lang.String)}
   * .
   */
  @Test
  public void testCreate() {

    /*
     * setting
     */

    /*
     * test
     */
    FlowQuery result = FlowQueryFactory.create("type=BasicFlow");

    /*
     * check
     */
    assertThat(result, is(BasicFlowQuery.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#create(java.lang.String)}
   * .
   */
  @Test
  public void testCreate_OfpFlowQuery() {

    /*
     * setting
     */

    /*
     * test
     */
    FlowQuery result = FlowQueryFactory.create("type=OFPFlow");

    /*
     * check
     */
    assertThat(result, is(OFPFlowQuery.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#create(java.lang.String)}
   * .
   */
  @Test
  public void testCreate_Null() {

    /*
     * test
     */
    FlowQuery result = FlowQueryFactory.create(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#getType(String)}.
   * @throws Exception
   */
  @Test
  public void testGetType() throws Exception {

    /*
     * test
     */
    String result = Whitebox.invokeMethod(FlowQueryFactory.class,
        "getType", "type=BasicFlow&key1=value1");

    /*
     * check
     */
    assertThat(result, is("BasicFlow"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#getType(String)}.
   * @throws Exception
   */
  @Test
  public void testGetType_NoType() throws Exception {

    /*
     * test
     */
    String result = Whitebox.invokeMethod(FlowQueryFactory.class,
        "getType", "key1=value1&key2=value2");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.network.flow.FlowQueryFactory#getType(String)}.
   * @throws Exception
   */
  @Test
  public void testGetType_NullQuery() throws Exception {

    /*
     * test
     */
    String result = Whitebox.invokeMethod(FlowQueryFactory.class,
        "getType", (String) null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

}
