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

package org.o3project.odenos.core.manager.system;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.ObjectProperty.PropertyNames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 *
 */
public class ComponentConnectionLogicAndNetworkTest {
  private ComponentConnectionLogicAndNetwork target;

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
    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        "connectionState",
        "logicId",
        "networkId");
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
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#ComponentConnectionLogicAndNetwork(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public final void testComponentConnectionLogicAndNetwork() {

    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        null,
        "logicId",
        "networkId");

    assertThat(target, is(instanceOf(ComponentConnectionLogicAndNetwork.class)));
    assertThat(target.property.get("id"), is("objectId"));
    assertThat(target.property.get("type"), is("LogicAndNetwork"));
    assertThat(target.property.get("connection_type"), is("connectionType"));
    assertThat(target.property.get("state"), is("initializing"));
    assertThat(target.property.get("logic_id"), is("logicId"));
    assertThat(target.property.get("network_id"), is("networkId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#getLogicId()}
   * .
   */
  @Test
  public final void testGetLogicId() {

    assertThat(target.getLogicId(), is("logicId"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#getNetworkId()}
   * .
   */
  @Test
  public final void testGetNetworkId() {

    assertThat(target.getNetworkId(), is("networkId"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws IOException
   */
  @Test
  public final void testWriteTo() throws IOException {

    Packer packer = Mockito.mock(Packer.class);

    target.writeTo(packer);

    verify(packer, times(1)).write(target.property);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws IOException
   */
  @Test
  public final void testReadFrom() throws IOException {
    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        null,
        "logicId",
        "networkId");

    target.setProperty(PropertyNames.OBJECT_SUPER_TYPE, "SuperType");
    target.setProperty(PropertyNames.DESCRIPTION, "Description");
    target.setProperty(PropertyNames.CM_ID, "CM_ID");
    target.setProperty(PropertyNames.COMPONENT_TYPES, "Comp_Types");
    target.setProperty("OtherProp", "OtherPropVal");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception in writeTo()");
    }

    byte[] bytes = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);

    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        null,
        "logicId",
        "networkId");
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(11));
    assertThat(target.getObjectType(), is("LogicAndNetwork"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectState(), is("initializing"));
    assertThat(target.getProperty(PropertyNames.OBJECT_SUPER_TYPE), is("SuperType"));
    assertThat(target.getProperty(PropertyNames.DESCRIPTION), is("Description"));
    assertThat(target.getProperty(PropertyNames.CM_ID), is("CM_ID"));
    assertThat(target.getProperty(PropertyNames.COMPONENT_TYPES), is("Comp_Types"));
    assertThat(target.getProperty("OtherProp"), is("OtherPropVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws IOException
   */
  @Test
  public final void testReadFromWithNoState() throws IOException {
    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        null,
        "logicId",
        "networkId");

    target.setProperty(PropertyNames.OBJECT_SUPER_TYPE, "SuperType");
    target.setProperty(PropertyNames.DESCRIPTION, "Description");
    target.setProperty(PropertyNames.CM_ID, "CM_ID");
    target.setProperty(PropertyNames.COMPONENT_TYPES, "Comp_Types");
    target.setProperty("OtherProp", "OtherPropVal");
    target.property.remove("state");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception in writeTo()");
    }

    byte[] bytes = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);

    target = new ComponentConnectionLogicAndNetwork("objectId",
        "connectionType",
        null,
        "logicId",
        "networkId");
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(11));
    assertThat(target.getObjectType(), is("LogicAndNetwork"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectState(), is("initializing"));
    assertThat(target.getProperty(PropertyNames.OBJECT_SUPER_TYPE), is("SuperType"));
    assertThat(target.getProperty(PropertyNames.DESCRIPTION), is("Description"));
    assertThat(target.getProperty(PropertyNames.CM_ID), is("CM_ID"));
    assertThat(target.getProperty(PropertyNames.COMPONENT_TYPES), is("Comp_Types"));
    assertThat(target.getProperty("OtherProp"), is("OtherPropVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {

    Object obj = null;

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectThis() {

    assertThat(target.equals(target), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectOtherInstance() {

    ComponentConnection obj = new ComponentConnection("ObjectId", "Type", "none");

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectOtherSameProperty() {

    ComponentConnectionLogicAndNetwork obj =
        new ComponentConnectionLogicAndNetwork("objectId", "connectionType",
            "connectionState", "logicId", "networkId");

    assertThat(target.equals(obj), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectOtherDifferentProperty() {

    ComponentConnectionLogicAndNetwork obj =
        new ComponentConnectionLogicAndNetwork("objectId", "connectionType", "none",
            "logicId", "networkId");

    assertThat(target.equals(obj), is(false));
  }

}
