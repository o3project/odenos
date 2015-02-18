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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.powermock.reflect.Whitebox;

/**
 * Test class for FlowQuery.
 */
public class FlowQueryTest {

  private FlowQuery target;

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

    target = Mockito.spy(new FlowQuery("type=BasicFlow") {
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
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowQuery#FlowQuery(java.lang.String)}
   * .
   */
  @Test
  public void testFlowQuery() {

    /*
     * test
     */
    target = new FlowQuery("type=BasicFlow") {
    };

    /*
     * check
     */
    String queriesString = Whitebox.getInternalState(target, "queriesString");
    assertThat(queriesString, is("type=BasicFlow"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowQuery#FlowQuery(java.lang.String)}
   * .
   */
  @Test
  public void testFlowQuery_Null() {

    /*
     * test
     */
    target = new FlowQuery(null) {
    };

    /*
     * check
     */
    String queriesString = Whitebox.getInternalState(target, "queriesString");
    assertThat(queriesString, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.FlowQuery#setTopology(org.o3project.odenos.core.component.network.topology.Topology)}
   * .
   */
  @Test
  public void testSetTopology() {

    /*
     * setting
     */
    Topology topology = Mockito.mock(Topology.class);

    /*
     * test
     */
    target.setTopology(topology);

    /*
     * check
     */
    assertThat(target.topology, is(topology));

  }

}
