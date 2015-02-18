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
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlIn;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionCopyTtlOut;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionExperimenter;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionGroupAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPopVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushMpls;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushPbb;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionPushVlan;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetIpTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetMplsTtl;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;

/**
 * Test class for FlowObject.
 */
public class FlowObjectTest {

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

    /* nothing */

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    /* nothing */

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_BasicFlow() {

    /*
     * setting
     */
    BasicFlow basicFlow = new BasicFlow();
    Value value = basicFlow.writeValue();

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(BasicFlow.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_OfpFlow() {

    /*
     * setting
     */
    OFPFlow ofpFlow = new OFPFlow();
    Value value = ofpFlow.writeValue();

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(OFPFlow.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_Flow() {

    /*
     * setting
     */
    Flow flow = new Flow();
    Value value = flow.writeValue();

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_EmptyMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createMapValue();

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_NotMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createBooleanValue(true);

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMessageFrom_NullValue() {

    /*
     * test
     */
    Flow result = FlowObject.readFlowMessageFrom(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMatchFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMatchFrom_BasicFlowMatch() {

    /*
     * setting
     */
    BasicFlowMatch basicFlowMatch = new BasicFlowMatch();
    Value value = basicFlowMatch.writeValue();

    /*
     * test
     */
    BasicFlowMatch result = FlowObject.readFlowMatchFrom(value);

    /*
     * check
     */
    assertThat(result, is(BasicFlowMatch.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMatchFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMatchFrom_OfpFlowMatch() {

    /*
     * setting
     */
    OFPFlowMatch ofpFlowMatch = new OFPFlowMatch();
    Value value = ofpFlowMatch.writeValue();

    /*
     * test
     */
    BasicFlowMatch result = FlowObject.readFlowMatchFrom(value);

    /*
     * check
     */
    assertThat(result, is(OFPFlowMatch.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMatchFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMatchFrom_EmptyMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createMapValue();

    /*
     * test
     */
    BasicFlowMatch result = FlowObject.readFlowMatchFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMatchFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMatchFrom_NotMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createBooleanValue(true);

    /*
     * test
     */
    BasicFlowMatch result = FlowObject.readFlowMatchFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readFlowMatchFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadFlowMatchFrom_NullValue() {

    /*
     * test
     */
    BasicFlowMatch result = FlowObject.readFlowMatchFrom(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_BasicFlowActionOutput() {

    /*
     * setting
     */
    FlowAction basicFlowActionOutput = new FlowActionOutput();
    Value value = basicFlowActionOutput.writeValue();

    /*
     * test
     */
    FlowAction result = FlowObject.readBasicFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(FlowActionOutput.class));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_OfpFlowActionAddrs() {

    /*
     * setting
     */
    FlowAction[] ofpFlowActions = new FlowAction[] {
        new OFPFlowActionCopyTtlIn(),
        new OFPFlowActionCopyTtlOut(),
        new OFPFlowActionDecIpTtl(),
        new OFPFlowActionDecMplsTtl(),
        new OFPFlowActionExperimenter(),
        new OFPFlowActionGroupAction(),
        new OFPFlowActionPopMpls(),
        new OFPFlowActionPopPbb(),
        new OFPFlowActionPopVlan(),
        new OFPFlowActionPushMpls(),
        new OFPFlowActionPushPbb(),
        new OFPFlowActionPushPbb(),
        new OFPFlowActionPushVlan(),
        new OFPFlowActionSetIpTtl(),
        new OFPFlowActionSetMplsTtl(),
        new OFPFlowActionSetMplsTtl(),
        new OFPFlowActionSetField(),
    };

    for (FlowAction ofpFlowAction : ofpFlowActions) {

      Value value = ofpFlowAction.writeValue();

      /*
       * test
       */
      FlowAction result = FlowObject.readBasicFlowActionFrom(value);

      /*
       * check
       */
      assertThat(result, is(nullValue()));

    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_OfpFlowActions() {

    /*
     * setting
     */
    FlowAction[] ofpFlowActions = new FlowAction[] {
        new OFPFlowActionExperimenter(),
    };

    for (FlowAction ofpFlowAction : ofpFlowActions) {

      Value value = ofpFlowAction.writeValue();

      /*
       * test
       */
      FlowAction result = FlowObject.readOFPFlowActionFrom(value);

      /*
       * check
       */
      assertThat(result, is(nullValue()));

    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_EmptyMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createMapValue();

    /*
     * test
     */
    FlowAction result = FlowObject.readBasicFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_NotMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createBooleanValue(true);

    /*
     * test
     */
    FlowAction result = FlowObject.readBasicFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readBasicFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadBasicFlowActionFrom_NullValue() {

    /*
     * test
     */
    FlowAction result = FlowObject.readBasicFlowActionFrom(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readOFPFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadOFlowActionFrom() {

    /*
     * setting
     */
    FlowAction[] ofpFlowActions = {
        new OFPFlowActionCopyTtlIn(),
        new OFPFlowActionCopyTtlOut(),
        new OFPFlowActionDecIpTtl(),
        new OFPFlowActionDecMplsTtl(),
        //new OFPFlowActionExperimenter(),
        new OFPFlowActionGroupAction(),
        new OFPFlowActionPopMpls(),
        new OFPFlowActionPopPbb(),
        new OFPFlowActionPopVlan(),
        new OFPFlowActionPushMpls(),
        new OFPFlowActionPushPbb(),
        new OFPFlowActionPushVlan(),
        new OFPFlowActionSetIpTtl(),
        new OFPFlowActionSetMplsTtl(),
        new OFPFlowActionSetMplsTtl(),
        //new OFPFlowActionSetField(),
    };

    for (FlowAction ofpFlowAction : ofpFlowActions) {

      Value value = ofpFlowAction.writeValue();

      /*
       * test
       */
      FlowAction result = FlowObject.readOFPFlowActionFrom(value);

      /*
       * check
       */
      assertThat(result, is(ofpFlowAction.getClass()));
    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readOFPFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadOFPFlowActionFrom_FlowActionOutput() {

    /*
     * setting
     */
    FlowAction basicFlowActionOutput = new FlowActionOutput();
    Value value = basicFlowActionOutput.writeValue();

    /*
     * test
     */
    FlowAction result = FlowObject.readOFPFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readOFPFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadOFPFlowActionFrom_EmptyMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createMapValue();

    /*
     * test
     */
    FlowAction result = FlowObject.readOFPFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readOFPFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadOFPFlowActionFrom_NoMap() {

    /*
     * setting
     */
    Value value = ValueFactory.createBooleanValue(true);

    /*
     * test
     */
    FlowAction result = FlowObject.readOFPFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowObject#readOFPFlowActionFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadOFPFlowActionFrom_NullValue() {

    /*
     * setting
     */
    Value value = null;

    /*
     * test
     */
    FlowAction result = FlowObject.readOFPFlowActionFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

}
