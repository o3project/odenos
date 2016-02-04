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

package org.o3project.odenos.remoteobject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.ObjectProperty.PropertyNames;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ObjectPropertyTest {

  private ObjectProperty target;

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

    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#ObjectProperty()}.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testObjectProperty() {
    target = new ObjectProperty();

    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#ObjectProperty(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testObjectPropertyWithParams() {
    target = new ObjectProperty("ObjType", "ObjID", "BaseURI");
    Map<String, String> result = Whitebox.getInternalState(target,
        "property");
    assertThat(target, is(notNullValue()));
    assertThat(result.get(PropertyNames.OBJECT_TYPE), is("ObjType"));
    assertThat(result.get(PropertyNames.OBJECT_ID), is("ObjID"));
    assertThat(result.get(PropertyNames.BASE_URI), is("BaseURI"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#ObjectProperty(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testObjectPropertyWith2Params() {
    target = new ObjectProperty("ObjType", "ObjID");
    Map<String, String> result = Whitebox.getInternalState(target,
        "property");
    assertThat(target, is(notNullValue()));
    assertThat(result.get(PropertyNames.OBJECT_TYPE), is("ObjType"));
    assertThat(result.get(PropertyNames.OBJECT_ID), is("ObjID"));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.ObjectProperty#clone()}.
   */
  @Test
  public void testClone() {
    ObjectProperty objProperty = new ObjectProperty("ObjType", "ObjID");
    objProperty.setProperty(PropertyNames.BASE_URI, "BaseURI");

    ObjectProperty target = (ObjectProperty) objProperty.clone();
    Map<String, String> result = Whitebox.getInternalState(target,
        "property");

    assertThat(target, is(notNullValue()));
    assertThat(result.get(PropertyNames.OBJECT_TYPE), is("ObjType"));
    assertThat(result.get(PropertyNames.OBJECT_ID), is("ObjID"));
    assertThat(result.get(PropertyNames.BASE_URI), is("BaseURI"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getObjectType()}.
   */
  @Test
  public void testGetObjectType() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target, is(notNullValue()));
    assertThat(target.getObjectType(), is("ObjType"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getObjectId()}.
   */
  @Test
  public void testGetObjectId() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target, is(notNullValue()));
    assertThat(target.getObjectId(), is("ObjID"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getBaseUri()}.
   */
  @Test
  public void testGetBaseUri() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
    assertThat(target, is(notNullValue()));
    assertThat(target.getBaseUri(), is("BaseURI"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getObjectState()}.
   */
  @Test
  public void testGetObjectState() {
    target = new ObjectProperty("ObjType", "ObjID");
    Map<String, String> map = Whitebox.getInternalState(target, "property");
    map.put(PropertyNames.OBJECT_STATE, "ObjState");
    assertThat(target, is(notNullValue()));
    assertThat(target.getObjectState(), is("ObjState"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#setObjectState(java.lang.String)}
   * .
   */
  @Test
  public void testSetObjectState() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setObjectState("ObjState");
    assertThat(target, is(notNullValue()));
    assertThat(target.getObjectState(), is("ObjState"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#setProperty(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetProperty() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target.getObjectId(), is("ObjID"));

    target.setProperty(PropertyNames.OBJECT_ID, "NewObjectId");

    assertThat(target.getObjectId(), is("NewObjectId"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#setProperty(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetPropertyWithReadOnlyKey() {
    target = new ObjectProperty("ObjType", "ObjID");

    assertThat(
        target.setProperty(PropertyNames.OBJECT_TYPE, "NewObjctType"),
        is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#setProperty(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetPropertyWithReadOnlyKey2() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.DESCRIPTION, "description");
    assertThat(target.getProperty(PropertyNames.DESCRIPTION),
        is("description"));

    assertThat(
        target.setProperty(PropertyNames.DESCRIPTION, "NewDescription"),
        is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getProperty(java.lang.String)}
   * .
   */
  @Test
  public void testGetPropertyWithType() {
    target = new ObjectProperty("ObjType", "ObjID");
    String result = target.getProperty(PropertyNames.OBJECT_TYPE);
    assertThat(result, is(notNullValue()));
    assertThat(result, is("ObjType"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getProperty(java.lang.String)}
   * .
   */
  @Test
  public void testGetPropertyWithID() {
    target = new ObjectProperty("ObjType", "ObjID");
    String result = target.getProperty(PropertyNames.OBJECT_ID);
    assertThat(result, is(notNullValue()));
    assertThat(result, is("ObjID"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getProperty(java.lang.String)}
   * .
   */
  @Test
  public void testGetPropertyWithUri() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
    String result = target.getProperty(PropertyNames.BASE_URI);
    assertThat(result, is(notNullValue()));
    assertThat(result, is("BaseURI"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getProperty(java.lang.String)}
   * .
   */
  @Test
  public void testGetPropertyWithState() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setObjectState("ObjState");
    String result = target.getProperty(PropertyNames.OBJECT_STATE);
    assertThat(result, is(notNullValue()));
    assertThat(result, is("ObjState"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getProperty(java.lang.String)}
   * .
   */
  @Test
  public void testGetPropertyWithUnknownProperty() {
    target = new ObjectProperty("ObjType", "ObjID");
    String result = target.getProperty("Unknown");
    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithObjectID() {
    target = new ObjectProperty("ObjType", "ObjID");
    String result = target.deleteProperty(PropertyNames.OBJECT_ID);
    assertThat(result, is("ObjID"));
    assertThat(target.getObjectId(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithObjectType() {
    target = new ObjectProperty("ObjType", "ObjID");
    String result = target.deleteProperty(PropertyNames.OBJECT_TYPE);
    assertNull(result);
    assertThat(target.getObjectType(), is("ObjType"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithBaseUri() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
    String result = target.deleteProperty(PropertyNames.BASE_URI);
    assertThat(result, is("BaseURI"));
    assertThat(target.getBaseUri(), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithSuperType() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.OBJECT_SUPER_TYPE, "SuperType");
    String result = target.deleteProperty(PropertyNames.OBJECT_SUPER_TYPE);
    assertNull(result);
    assertThat(target.getProperty(PropertyNames.OBJECT_SUPER_TYPE),
        is("SuperType"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithDescription() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.DESCRIPTION, "Description");
    String result = target.deleteProperty(PropertyNames.DESCRIPTION);
    assertNull(result);
    assertThat(target.getProperty(PropertyNames.DESCRIPTION),
        is("Description"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#deleteProperty(java.lang.String)}
   * .
   */
  @Test
  public void testDeletePropertyWithObjectState() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setObjectState("ObjState");
    String result = target.deleteProperty(PropertyNames.OBJECT_STATE);
    assertThat(result, is("ObjState"));
    assertNull(target.getObjectState());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#putProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPutProperty() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
    target.setObjectState("ObjState");
    target.setProperty(PropertyNames.DESCRIPTION, "Description");
    ObjectProperty newProperty = new ObjectProperty("NewObjType",
        "NewObjID");
    newProperty.setProperty(PropertyNames.BASE_URI, "NewBaseURI");
    newProperty.setProperty(PropertyNames.OBJECT_SUPER_TYPE, "SuperType");
    target.putProperty(newProperty);
    assertThat(target.getKeys().size(), is(5));
    assertThat(target.getObjectType(), is("ObjType"));
    assertThat(target.getObjectId(), is("NewObjID"));
    assertThat(target.getBaseUri(), is("NewBaseURI"));
    assertNull(target.getObjectState());
    assertThat(target.getProperty(PropertyNames.OBJECT_SUPER_TYPE),
        is("SuperType"));
    assertThat(target.getProperty(PropertyNames.DESCRIPTION),
        is("Description"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#getKeys()}.
   */
  @Test
  public void testGetKeys() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
    Set<String> result = target.getKeys();

    assertThat(result.size(), is(3));
    assertTrue(result.contains(PropertyNames.OBJECT_TYPE));
    assertTrue(result.contains(PropertyNames.OBJECT_ID));
    assertTrue(result.contains(PropertyNames.BASE_URI));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isModify(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testIsModify() {
    target = new ObjectProperty("ObjType", "ObjID");
    ObjectProperty newProperty = new ObjectProperty("ObjType", "ObjID");
    assertFalse(target.isModify(newProperty));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isModify(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testIsModifyWithDifferentKey() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setObjectState("ObjState");
    ObjectProperty newProperty = new ObjectProperty("ObjType", "ObjID");
    assertTrue(target.isModify(newProperty));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isModify(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testIsModifyWithDifferentVal() {
    target = new ObjectProperty("ObjType", "ObjID");
    ObjectProperty newProperty = new ObjectProperty("NewObjType", "ObjID");
    assertTrue(target.isModify(newProperty));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public void testIsReadOnlyKeyWithObjectType() {
    target = new ObjectProperty("ObjType", "ObjID");

    assertThat(target.isReadOnlyKey(PropertyNames.OBJECT_TYPE), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public void testIsReadOnlyKeyWithObjectSuperType() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target.isReadOnlyKey(PropertyNames.OBJECT_SUPER_TYPE),
        is(false));

    target.setProperty(PropertyNames.OBJECT_SUPER_TYPE, "superType");

    assertThat(target.isReadOnlyKey(PropertyNames.OBJECT_SUPER_TYPE),
        is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public void testIsReadOnlyKeyWithDescription() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target.isReadOnlyKey(PropertyNames.DESCRIPTION), is(false));

    target.setProperty(PropertyNames.DESCRIPTION, "description");

    assertThat(target.isReadOnlyKey(PropertyNames.DESCRIPTION), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public void testIsReadOnlyKeyWithConnectionTypes() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target.isReadOnlyKey(PropertyNames.CONNECTION_TYPES),
        is(false));

    target.setProperty(PropertyNames.CONNECTION_TYPES, "connType");

    assertThat(target.isReadOnlyKey(PropertyNames.CONNECTION_TYPES),
        is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#isReadOnlyKey(java.lang.String)}
   * .
   */
  @Test
  public void testIsReadOnlyKeyWithBaseUri() {
    target = new ObjectProperty("ObjType", "ObjID");
    assertThat(target.isReadOnlyKey(PropertyNames.BASE_URI), is(false));

    target.setProperty(PropertyNames.BASE_URI, "baseUri");

    assertThat(target.isReadOnlyKey(PropertyNames.BASE_URI), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFrom() {
    target = new ObjectProperty("ObjType", "ObjID");
    target.setObjectState("ObjState");
    target.setProperty(PropertyNames.BASE_URI, "BaseURI");
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

    target = new ObjectProperty("ObjType2", "ObjID2");
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(9));
    assertThat(target.getObjectType(), is("ObjType"));
    assertThat(target.getObjectId(), is("ObjID"));
    assertThat(target.getBaseUri(), is("BaseURI"));
    assertThat(target.getObjectState(), is("ObjState"));
    assertThat(target.getProperty(PropertyNames.OBJECT_SUPER_TYPE),
        is("SuperType"));
    assertThat(target.getProperty(PropertyNames.DESCRIPTION),
        is("Description"));
    assertThat(target.getProperty(PropertyNames.CM_ID), is("CM_ID"));
    assertThat(target.getProperty(PropertyNames.COMPONENT_TYPES),
        is("Comp_Types"));
    assertThat(target.getProperty("OtherProp"), is("OtherPropVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectProperty#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteTo() {
    testReadFrom();
  }
}
