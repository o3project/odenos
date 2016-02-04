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
import org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery;
import org.o3project.odenos.core.component.network.flow.query.FlowActionQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FlowActionQueryFactory.
 */
public class FlowActionQueryFactoryTest {

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
    /* do nothing */
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    /* do nothing */
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowActionQueryFactory#FlowActionQueryFactory()}
   * .
   */
  /* not test */
  public void testFlowActionQueryFactory() {

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowActionQueryFactory#create(java.util.Map)}
   * .
   */
  @Test
  public void testCreate() {

    /*
     * setting
     */
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("type", "BasicFlowActionOutput");

    /*
     * test
     */
    FlowActionQuery result = FlowActionQueryFactory.create(paramMap);

    /*
     * check
     */
    assertThat(result, is(FlowActionOutputQuery.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowActionQueryFactory#create(java.util.Map)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testCreate_NoBasicFlowActionOutput() throws Exception {

    /*
     * setting
     */
    String[] types = {
        "OFPFlowActionCopyTtlIn",
        "OFPFlowActionCopyTtlOut",
        "OFPFlowActionDecIpTtl",
        "OFPFlowActionDecMplsTtl",
        "OFPFlowActionExperimenter",
        "OFPFlowActionGroupAction",
        "OFPFlowActionPopMpls",
        "OFPFlowActionPopPbb",
        "OFPFlowActionPopVlan",
        "OFPFlowActionPushMpls",
        "OFPFlowActionPushPbb",
        "OFPFlowActionPushVlan",
        "OFPFlowActionSetField",
        "OFPFlowActionSetIpTtl",
        "OFPFlowActionSetMplsTtl",
        "OFPFlowActionSetQueue",
    };

    for (String typeValue : types) {

      /*
       * setting
       */

      Map<String, String> paramMap = new HashMap<String, String>();
      paramMap.put("type", typeValue);

      /*
       * test
       */
      FlowActionQuery result = FlowActionQueryFactory.create(paramMap);

      /*
       * check
       */
      String className =
          "org.o3project.odenos.core.component.network.flow.query." + typeValue + "Query";
      assertThat(result, is(Class.forName(className)));

      paramMap = null;
    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowActionQueryFactory#create(java.util.Map)}
   * .
   */
  @Test
  public void testCreate_Null() {

    /*
     * test
     */
    FlowActionQuery result = FlowActionQueryFactory.create(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

}
