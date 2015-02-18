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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.core.manager.system.ComponentConnection.ComponentConnectionChangedListener;
import org.o3project.odenos.remoteobject.manager.component.event.ComponentChanged;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 *
 */

public class ComponentConnectionTest {

  // private ComponentConnection target = PowerMockito.spy(new
  // ComponentConnection());
  private ComponentConnection target = null;
  private ComponentConnectionChangedListener listener = null;
  private ComponentConnection mocktarget = null;

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
    target = new ComponentConnection("objectId", "connectionType",
        "connectionState");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    listener = null;
    mocktarget = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#ComponentConnection(java.lang.String, java.lang.String, java.lang.String)}
   * .
   *
   * @throws IOException
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testComponentConnection() {
    target = new ComponentConnection("objectId", "connectionType",
        "connectionState");

    assertThat(target, is(instanceOf(ComponentConnection.class)));
    assertThat(target.componentConnectionChangedListener, is(nullValue()));
    assertThat(target.defaultType, is("ComponentConnection"));
    assertThat(target.OBJECT_ID, is("id"));
    assertThat(target.OBJECT_TYPE, is("type"));
    assertThat(target.CONNECTION_TYPE, is("connection_type"));
    assertThat(target.CONNECTION_STATE, is("state"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectType(), is("ComponentConnection"));
    assertThat(target.getConnectionType(), is("connectionType"));
    assertThat(target.getObjectState(), is("connectionState"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#ComponentConnection(java.lang.String, java.lang.String, java.lang.String)}
   * .
   *
   * @throws IOException
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testComponentConnectionStateblank() {
    target = new ComponentConnection("objectId", "connectionType", "");

    assertThat(target, is(instanceOf(ComponentConnection.class)));
    assertThat(target.componentConnectionChangedListener, is(nullValue()));
    assertThat(target.defaultType, is("ComponentConnection"));
    assertThat(target.OBJECT_ID, is("id"));
    assertThat(target.OBJECT_TYPE, is("type"));
    assertThat(target.CONNECTION_TYPE, is("connection_type"));
    assertThat(target.CONNECTION_STATE, is("state"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectType(), is("ComponentConnection"));
    assertThat(target.getConnectionType(), is("connectionType"));
    assertThat(target.getObjectState(), is("initializing"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#ComponentConnection(java.lang.String, java.lang.String, java.lang.String)}
   * .
   *
   * @throws IOException
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testComponentConnectionStateNull() {
    target = new ComponentConnection("objectId", "connectionType", null);

    assertThat(target, is(instanceOf(ComponentConnection.class)));
    assertThat(target.componentConnectionChangedListener, is(nullValue()));
    assertThat(target.defaultType, is("ComponentConnection"));
    assertThat(target.OBJECT_ID, is("id"));
    assertThat(target.OBJECT_TYPE, is("type"));
    assertThat(target.CONNECTION_TYPE, is("connection_type"));
    assertThat(target.CONNECTION_STATE, is("state"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectType(), is("ComponentConnection"));
    assertThat(target.getConnectionType(), is("connectionType"));
    assertThat(target.getObjectState(), is("initializing"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#ComponentConnection(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testComponentConnectionWithType() {
    target = new ComponentConnection("objectId", "type", "connectionType",
        "connectionState");

    assertThat(target, is(instanceOf(ComponentConnection.class)));
    assertThat(target.componentConnectionChangedListener, is(nullValue()));
    assertThat(target.defaultType, is("ComponentConnection"));
    assertThat(target.OBJECT_ID, is("id"));
    assertThat(target.OBJECT_TYPE, is("type"));
    assertThat(target.CONNECTION_TYPE, is("connection_type"));
    assertThat(target.CONNECTION_STATE, is("state"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectType(), is("type"));
    assertThat(target.getConnectionType(), is("connectionType"));
    assertThat(target.getObjectState(), is("connectionState"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#setObjectPropertyChangedListener(ComponentConnectionChangedListener)}
   * .
   */
  @Test
  public final void testSetObjectPropertyChangedListener() {
    listener = Mockito.mock(ComponentConnectionChangedListener.class);

    target.setObjectPropertyChangedListener(listener);

    assertThat(target.componentConnectionChangedListener, is(listener));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#onInitializing(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnInitializing() throws Exception {

    String objectId = "objId";
    String type = "type";
    String connectionType = "connectionType";
    String connectionState = "connectionState";

    Whitebox.invokeMethod(target, "onInitializing", objectId, type,
        connectionType, connectionState);

    assertThat(target.getObjectId(), is("objId"));
    assertThat(target.getObjectState(), is("connectionState"));
    assertThat(target.getObjectType(), is("type"));
    assertThat(target.getConnectionType(), is("connectionType"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#onInitializing(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnInitializingStateNull() throws Exception {

    String objectId = "objId";
    String type = "type";
    String connectionType = "connectionType";

    Whitebox.invokeMethod(target, "onInitializing", objectId, type,
        connectionType, null);

    assertThat(target.getObjectId(), is("objId"));
    assertThat(target.getObjectState(), is("initializing"));
    assertThat(target.getObjectType(), is("type"));
    assertThat(target.getConnectionType(), is("connectionType"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#onInitializing(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   * .
   *
   * @throws Exception
   */
  @Test
  public final void testOnInitializingStateBlank() throws Exception {

    String objectId = "objId";
    String type = "type";
    String connectionType = "connectionType";
    String connectionState = "";

    Whitebox.invokeMethod(target, "onInitializing", objectId, type,
        connectionType, connectionState);

    assertThat(target.getObjectId(), is("objId"));
    assertThat(target.getObjectState(), is("initializing"));
    assertThat(target.getObjectType(), is("type"));
    assertThat(target.getConnectionType(), is("connectionType"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#clone()}.
   */
  @Test
  public final void testClone() {
    ComponentConnection obj = (ComponentConnection) target.clone();

    assertThat(obj.getObjectId(), is(target.getObjectId()));
    assertThat(obj.getObjectState(), is(target.getObjectState()));
    assertThat(obj.getObjectType(), is(target.getObjectType()));
    assertThat(obj.getConnectionType(), is(target.getConnectionType()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getObjectId()}
   * .
   */
  @Test
  public final void testGetObjectId() {
    assertThat(target.getObjectId(), is(target.property.get("id")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getObjectType()}
   * .
   */
  @Test
  public final void testGetObjectType() {
    assertThat(target.getObjectType(), is(target.property.get("type")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getConnectionType()}
   * .
   */
  @Test
  public final void testGetConnectionType() {
    assertThat(target.getConnectionType(),
        is(target.property.get("connection_type")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getObjectState()}
   * .
   */
  @Test
  public final void testGetObjectState() {
    assertThat(target.getObjectState(), is(target.property.get("state")));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#setConnectionState(java.lang.String)}
   * .
   */
  @Test
  public final void testSetConnectionState() {
    target.setConnectionState("running");
    assertThat(target.property.get("state"), is("running"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#setProperty(java.lang.String,java.lang.String)}
   * .
   */
  @Test
  public final void testSetPropertyOk() {
    mocktarget =
        Mockito.spy(new ComponentConnection("objectId",
            "connectionType", "initializing"));
    String oldValue = mocktarget.getObjectType();
    assertThat(oldValue, is(notNullValue()));

    listener = Mockito.mock(ComponentConnectionChangedListener.class);
    mocktarget.setObjectPropertyChangedListener(listener);

    mocktarget.setProperty("state", "finalizing");

    verify(mocktarget.componentConnectionChangedListener, times(1))
        .onComponentConnectionChanged("state", "initializing",
            "finalizing");

    assertThat(mocktarget.property.get("state"), is("finalizing"));
    assertThat(mocktarget.property.get("state"), is(not(oldValue)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#setProperty(java.lang.String,java.lang.String)}
   * .
   */
  @Test
  public final void testSetPropertyReadOnly() {
    String oldValue = target.getObjectType();
    target.setProperty("type", "ComponentManager");
    assertThat(target.property.get("type"), is(not("ComponentManager")));
    assertThat(target.property.get("type"), is(oldValue));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#setProperty(java.lang.String,java.lang.String)}
   * .
   */
  @Test
  public final void testSetPropertySameValue() {
    mocktarget =
        Mockito.spy(new ComponentConnection("objectId", "type",
            "connectionType",
            "initialize"));
    Map<String, String> mockprop = Mockito
        .spy(new HashMap<String, String>());
    Whitebox.setInternalState(target, "property", mockprop);
    String oldValue = mocktarget.getObjectState();

    mocktarget.setProperty("state", "initialize");

    assertThat(mocktarget.property.get("state"), is("initialize"));
    assertThat(mocktarget.property.get("state"), is(oldValue));

    Mockito.verify(mockprop, never()).put("state", "initialize");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public final void testIsReadOnlyKeysTrue() {
    assertThat(target.isReadOnlyKey("id"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public final void testIsReadOnlyKeysfalse() {
    assertThat(target.isReadOnlyKey("status"), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getProperty(java.lang.String)}
   * .
   */
  @Test
  public final void testGetProperty() {
    assertThat(target.getProperty("id"), is("objectId"));
    assertThat(target.getProperty("type"), is("ComponentConnection"));
    assertThat(target.getProperty("connection_type"), is("connectionType"));
    assertThat(target.getProperty("state"), is("connectionState"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#getProperty(java.lang.String)}
   * .
   */
  @Test
  public final void testGetKeys() {
    Set<String> keys = target.getKeys();

    assertThat(keys.contains("id"), is(true));
    assertThat(keys.contains("type"), is(true));
    assertThat(keys.contains("connection_type"), is(true));
    assertThat(keys.contains("state"), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws IOException
   */
  @Test
  public void testWriteTo() throws IOException {

    Packer mockPacker = PowerMockito.mock(Packer.class);

    when(mockPacker.write(anyObject())).thenReturn(mockPacker);
    when(mockPacker.write(anyObject())).thenReturn(mockPacker);

    target.writeTo(mockPacker);

    verify(mockPacker, times(1)).write(target.property);
    verify(mockPacker).write(anyObject());
    verify(mockPacker).write(anyObject());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public final void testReadFrom() {
    target = new ComponentConnection("objectId",
        "ComponentConnection",
        "connectionType",
        "running");

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

    target = new ComponentConnection("objectId",
        "ComponentConnection",
        "connectionType",
        "running");
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(4));
    assertThat(target.getObjectType(), is("ComponentConnection"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectState(), is("running"));
    assertThat(target.getProperty("id"), is("objectId"));
    assertThat(target.getProperty("type"), is("ComponentConnection"));
    assertThat(target.getProperty("connection_type"), is("connectionType"));
    assertThat(target.getProperty("state"), is("running"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public final void testReadFromWithNoState() {
    target = new ComponentConnection("objectId",
        "ComponentConnection",
        "connectionType",
        "");

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

    target = new ComponentConnection("objectId",
        "ComponentConnection",
        "connectionType",
        "");
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(4));
    assertThat(target.getObjectType(), is("ComponentConnection"));
    assertThat(target.getObjectId(), is("objectId"));
    assertThat(target.getObjectState(), is("initializing"));
    assertThat(target.getProperty("id"), is("objectId"));
    assertThat(target.getProperty("type"), is("ComponentConnection"));
    assertThat(target.getProperty("connection_type"), is("connectionType"));
    assertThat(target.getProperty("state"), is("initializing"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjNull() {
    Object obj = null;

    assertThat(target.equals(obj), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjThis() {
    assertThat(target.equals(target), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectOtherInstance() {
    ComponentChanged obj = new ComponentChanged();

    assertThat(target.equals(obj), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsOtherProperty() {
    ComponentConnectionLogicAndNetwork obj =
        new ComponentConnectionLogicAndNetwork("ObjectId", "Type",
            "none",
            "logicandNewwrok", "network");

    assertThat(target.equals(obj), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.manager.system.ComponentConnection#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsSameProperty() {
    ComponentConnection obj =
        new ComponentConnection("objectId", "connectionType",
            "connectionState");

    assertThat(target.equals(obj), is(true));

  }

}
