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

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.basic.FlowActionOutput;
import org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Flow.
 */
public class FlowTest {

  private Flow target;

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

    Map<String, String> attributes = new HashMap<String, String>();
    target =
        Mockito.spy(new Flow("1", "FlowId", "Owner", true, "Priority",
            "none", attributes));

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
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow()}.
   */
  @Test
  public void testFlow() {

    /*
     * test
     */
    target = new Flow();

    /*
     * check
     */
    assertThat(target.getFlowId(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String)}
   * .
   */
  @Test
  public void testFlowString() {

    /*
     * test
     */
    target = new Flow("FlowId");

    /*
     * check
     */
    assertThat(target.getFlowId(), is("FlowId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String)}
   * .
   */
  @Test
  public void testFlowString_Null() {

    /*
     * test
     */
    target = new Flow(null);

    /*
     * check
     */
    assertThat(target.getFlowId(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String, java.lang.String, boolean, java.lang.String)}
   * .
   */
  @Test
  public void testFlowStringStringBooleanString() {

    /*
     * test
     */
    target = new Flow("FlowId", "Owner", true, "Priority");

    /*
     * check
     */
    assertThat(target.getFlowId(), is("FlowId"));
    assertThat(target.getOwner(), is("Owner"));
    assertThat(target.getEnabled(), is(true));
    assertThat(target.getPriority(), is("Priority"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String, java.lang.String, boolean, java.lang.String)}
   * .
   */
  @Test
  public void testFlowStringStringBooleanString_Null() {

    /*
     * test
     */
    target = new Flow(null, null, false, null);

    /*
     * check
     */
    assertThat(target.getFlowId(), is(nullValue()));
    assertThat(target.getOwner(), is(nullValue()));
    assertThat(target.getEnabled(), is(false));
    assertThat(target.getPriority(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.Map)}
   * .
   */
  @Test
  public void testFlowStringStringStringBooleanStringStringMapOfStringString() {

    /*
     * test
     */
    Map<String, String> attributes = new HashMap<String, String>();
    target = new Flow("1", "FlowId", "Owner", true, "Priority", "Status",
        attributes);

    /*
     * check
     */
    assertThat(target.getVersion(), is("1"));
    assertThat(target.getFlowId(), is("FlowId"));
    assertThat(target.getOwner(), is("Owner"));
    assertThat(target.getEnabled(), is(true));
    assertThat(target.getPriority(), is("Priority"));
    assertThat(target.getStatus(), is("Status"));
    assertThat(target.getAttributes(), is(attributes));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#Flow(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.Map)}
   * .
   */
  @Test
  public void testFlowStringStringStringBooleanStringStringMapOfStringString_Null() {

    /*
     * test
     */
    target = new Flow(null, null, null, false, null, null, null);

    /*
     * check
     */
    assertThat(target.getVersion(), is("0"));
    assertThat(target.getFlowId(), is(nullValue()));
    assertThat(target.getOwner(), is(nullValue()));
    assertThat(target.getEnabled(), is(false));
    assertThat(target.getPriority(), is(nullValue()));
    assertThat(target.getStatus(), is(nullValue()));
    assertThat(target.getAttributes().size(), is(0));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getType()}.
   */
  @Test
  public void testGetType() {

    /*
     * test & check
     */
    assertThat(target.getType(), is("Flow"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#validate()}.
   */
  @Test
  public void testValidate() {

    /*
     * test & check
     */
    assertThat(target.validate(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getFlowId()}.
   */
  @Test
  public void testGetFlowId() {

    /*
     * test & check
     */
    assertThat(target.getFlowId(), is("FlowId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setFlowId(java.lang.String)}
   * .
   */
  @Test
  public void testSetFlowId() {

    /*
     * test
     */
    target.setFlowId("NewFlowId");

    /*
     * check
     */
    assertThat(target.getFlowId(), is("NewFlowId"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setFlowId(java.lang.String)}
   * .
   */
  @Test
  public void testSetFlowId_Null() {

    /*
     * test
     */
    target.setFlowId(null);

    /*
     * check
     */
    assertThat(target.getFlowId(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getOwner()}.
   */
  @Test
  public void testGetOwner() {

    /*
     * test & check
     */
    assertThat(target.getOwner(), is("Owner"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setOwner(java.lang.String)}
   * .
   */
  @Test
  public void testSetOwner() {

    /*
     * test
     */
    target.setOwner("NewOwner");

    /*
     * check
     */
    assertThat(target.getOwner(), is("NewOwner"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setOwner(java.lang.String)}
   * .
   */
  @Test
  public void testSetOwner_Null() {

    /*
     * test
     */
    target.setOwner(null);

    /*
     * check
     */
    assertThat(target.getOwner(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getEnabled()}.
   */
  @Test
  public void testGetEnabled() {

    /*
     * test & check
     */
    assertThat(target.getEnabled(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setEnabled(boolean)}
   * .
   */
  @Test
  public void testSetEnabled() {

    /*
     * test
     */
    target.setEnabled(false);

    /*
     * check
     */
    assertThat(target.getEnabled(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getPriority()}.
   */
  @Test
  public void testGetPriority() {

    /*
     * test & check
     */
    assertThat(target.getPriority(), is("Priority"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setPriority(java.lang.String)}
   * .
   */
  @Test
  public void testSetPriority() {

    /*
     * test
     */
    target.setPriority("NewPriority");

    /*
     * check
     */
    assertThat(target.getPriority(), is("NewPriority"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setPriority(java.lang.String)}
   * .
   */
  @Test
  public void testSetPriority_Null() {

    /*
     * test
     */
    target.setPriority(null);

    /*
     * check
     */
    assertThat(target.getPriority(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getStatus()}.
   */
  @Test
  public void testGetStatus() {

    /*
     * test & check
     */
    assertThat(target.getStatus(), is("none"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setStatus(java.lang.String)}
   * .
   */
  @Test
  public void testSetStatus() {

    /*
     * test
     */
    target.setStatus("NewStatus");

    /*
     * check
     */
    assertThat(target.getStatus(), is("NewStatus"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setStatus(java.lang.String)}
   * .
   */
  @Test
  public void testSetStatus_Null() {

    /*
     * test
     */
    target.setStatus(null);

    /*
     * check
     */
    assertThat(target.getStatus(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#isEnabled()}.
   */
  @Test
  public void testIsEnabled() {

    /*
     * test & check
     */
    assertThat(target.isEnabled(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#getStatusValue()}
   * .
   */
  @Test
  public void testGetStatusValue() {

    /*
     * test & check
     */
    assertThat(target.getStatusValue(), is(FlowStatus.NONE));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setStatusValue(org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus)}
   * .
   */
  @Test
  public void testSetStatusValue() {

    /*
     * test
     */
    target.setStatusValue(FlowStatus.ESTABLISHED);

    /*
     * check
     */
    assertThat(target.getStatusValue(), is(FlowStatus.ESTABLISHED));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#setStatusValue(org.o3project.odenos.core.component.network.flow.FlowObject.FlowStatus)}
   * .
   */
  @Test
  public void testSetStatusValue_Null() {

    /*
     * test
     */
    target.setStatusValue(null);

    /*
     * check
     */
    assertThat(target.getStatusValue(), is(FlowStatus.NONE));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue() {

    /*
     * setting
     */
    Value versionKey = ValueFactory.createRawValue("version");
    Value versionValue = ValueFactory.createRawValue("1");
    Value flowIdKey = ValueFactory.createRawValue("flow_Id");
    Value flowIdValue = ValueFactory.createRawValue("FlowId");
    Value ownerKey = ValueFactory.createRawValue("owner");
    Value ownerValue = ValueFactory.createRawValue("Owner");
    Value enabledKey = ValueFactory.createRawValue("enabled");
    Value enabledValue = ValueFactory.createBooleanValue(true);
    Value priorityKey = ValueFactory.createRawValue("priority");
    Value priorityValue = ValueFactory.createRawValue("Priority");
    Value statusKey = ValueFactory.createRawValue("status");
    Value statusValue = ValueFactory.createRawValue("NONE");
    Value attributesKey = ValueFactory.createRawValue("attributes");
    Value attributesValue = ValueFactory.createMapValue();

    Value values = ValueFactory.createMapValue(new Value[] {
        versionKey, versionValue,
        flowIdKey, flowIdValue,
        ownerKey, ownerValue,
        enabledKey, enabledValue,
        priorityKey, priorityValue,
        statusKey, statusValue,
        attributesKey, attributesValue
    });

    /*
     * test
     */
    boolean result = target.readValue(values);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue_FewParameter() {

    /*
     * setting
     */
    Value versionKey = ValueFactory.createRawValue("version");
    Value versionValue = ValueFactory.createRawValue("1");

    Value values = ValueFactory.createMapValue(new Value[] {
        versionKey, versionValue,
    });

    /*
     * test
     */
    boolean result = target.readValue(values);

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSub() {

    /*
     * setting
     */
    Map<String, Value> values = new HashMap<String, Value>();

    /*
     * test
     */
    boolean result = target.writeValueSub(values);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(values.get("type"),
        is((Value) ValueFactory.createRawValue("Flow")));
    assertThat(values.get("version"),
        is((Value) ValueFactory.createRawValue("1")));
    assertThat(values.get("flow_id"),
        is((Value) ValueFactory.createRawValue("FlowId")));
    assertThat(values.get("owner"),
        is((Value) ValueFactory.createRawValue("Owner")));
    assertThat(values.get("enabled"),
        is((Value) ValueFactory.createBooleanValue(true)));
    assertThat(values.get("priority"),
        is((Value) ValueFactory.createRawValue("Priority")));
    assertThat(values.get("status"),
        is((Value) ValueFactory.createRawValue("none")));
    assertThat(values.get("attributes"),
        is((Value) ValueFactory.createMapValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    target = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    Flow flow = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    /*
     * test & check
     */
    assertThat(target.equals(flow), is(true));

    // assertThat(target.hashCode(), is(flow.hashCode()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals_AsFlowDefault() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    target = new Flow("0", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    Flow flow = new Flow("FlowId");

    /*
     * test & check
     */
    assertThat(target.equals(flow), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals_FlowFlowIdOwnerEnabledPriority() {

    /*
     * setting
     */
    target = new Flow("FlowId", "Owner", true, "Priority");

    Flow flow = new Flow("FlowId", "Owner", true, "Priority");

    /*
     * test & check
     */
    assertThat(target.equals(flow), is(true));

    // assertThat(target.hashCode(), is(flow.hashCode()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals_FlowFlowId() {

    /*
     * setting
     */
    target = new Flow("FlowId");

    Flow flow = new Flow("FlowId");

    /*
     * test & check
     */
    assertThat(target.equals(flow), is(true));

    // assertThat(target.hashCode(), is(flow.hashCode()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals_FlowFlowDefault() {

    /*
     * setting
     */
    target = new Flow();

    Flow flow = new Flow();

    /*
     * test & check
     */
    assertThat(target.equals(flow), is(true));

    // assertThat(target.hashCode(), is(flow.hashCode()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject_NullObject() {

    /*
     * test & check
     */
    assertThat(target.equals(null), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject_SameObject() {

    /*
     * test & check
     */
    assertThat(target.equals(target), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject_NotFlowObject() {

    /*
     * test & check
     */
    assertThat(target.equals(new BasicFlowMatch()), is(false));
    assertThat(target.equals(new FlowActionOutput()), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject_DifferType() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    target = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    BasicFlow basicFlow =
        new BasicFlow("1", "FlowId", "Owner", true, "Priority", "none",
            null, null, null,
            attributes);

    /*
     * test
     */
    assertThat(target.equals(basicFlow), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#clone()}.
   */
  @Test
  public void testClone() {

    /*
     * setting
     */
    Flow flow =
        new Flow("1", "FlowId", "Owner", true, "Priority", "none",
            new HashMap<String, String>());

    /*
     * test
     */
    Flow result = flow.clone();

    /*
     * check
     */
    assertThat(result, is(flow));

    assertThat(result.getType(), is(flow.getType()));
    assertThat(result.getVersion(), is(flow.getVersion()));
    assertThat(result.getFlowId(), is(flow.getFlowId()));
    assertThat(result.getOwner(), is(flow.getOwner()));
    assertThat(result.getEnabled(), is(flow.getEnabled()));
    assertThat(result.getPriority(), is(flow.getPriority()));
    assertThat(result.getStatus(), is(flow.getStatus()));
    assertThat(result.getAttributes(), is(flow.getAttributes()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#clone()}.
   */
  @Test
  public void testClone_FlowFlowIdOwnerEnabledPriority() {

    /*
     * setting
     */
    Flow flow = new Flow("FlowId", "Owner", true, "Priority");

    /*
     * test
     */
    Flow result = flow.clone();

    /*
     * check
     */
    assertThat(result, is(flow));

    assertThat(result.getType(), is(flow.getType()));
    assertThat(result.getVersion(), is(flow.getVersion()));
    assertThat(result.getFlowId(), is(flow.getFlowId()));
    assertThat(result.getOwner(), is(flow.getOwner()));
    assertThat(result.getEnabled(), is(flow.getEnabled()));
    assertThat(result.getPriority(), is(flow.getPriority()));
    assertThat(result.getStatus(), is(flow.getStatus()));
    assertThat(result.getAttributes(), is(flow.getAttributes()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#clone()}.
   */
  @Test
  public void testClone_FlowFlowId() {

    /*
     * setting
     */
    Flow flow = new Flow("FlowId");

    /*
     * test
     */
    Flow result = flow.clone();

    /*
     * check
     */
    assertThat(result, is(flow));

    assertThat(result.getType(), is(flow.getType()));
    assertThat(result.getVersion(), is(flow.getVersion()));
    assertThat(result.getFlowId(), is(flow.getFlowId()));
    assertThat(result.getOwner(), is(flow.getOwner()));
    assertThat(result.getEnabled(), is(flow.getEnabled()));
    assertThat(result.getPriority(), is(flow.getPriority()));
    assertThat(result.getStatus(), is(flow.getStatus()));
    assertThat(result.getAttributes(), is(flow.getAttributes()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#clone()}.
   */
  @Test
  public void testClone_FlowDefault() {

    /*
     * setting
     */
    Flow flow = new Flow();

    /*
     * test
     */
    Flow result = flow.clone();

    /*
     * check
     */
    assertThat(result, is(flow));

    assertThat(result.getType(), is(flow.getType()));
    assertThat(result.getVersion(), is(flow.getVersion()));
    assertThat(result.getFlowId(), is(flow.getFlowId()));
    assertThat(result.getOwner(), is(flow.getOwner()));
    assertThat(result.getEnabled(), is(flow.getEnabled()));
    assertThat(result.getPriority(), is(flow.getPriority()));
    assertThat(result.getStatus(), is(flow.getStatus()));
    assertThat(result.getAttributes(), is(flow.getAttributes()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.Flow#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    target = new Flow("1", "FlowId", "Owner", true, "Priority", "none",
        attributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[version=1",
        "flowId=FlowId",
        "owner=Owner",
        "enabled=true",
        "priority=Priority",
        "status=none",
        "attributes={}]"
    }, ",");

    assertThat(result.endsWith(expectedString), is(true));

  }
}
