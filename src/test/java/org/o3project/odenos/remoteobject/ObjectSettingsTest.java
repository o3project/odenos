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
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ObjectSettingsTest {

  private ObjectSettings target;

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
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#ObjectSettings()}.
   */
  @Test
  public void testObjectSettings() {
    target = new ObjectSettings();

    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.ObjectSettings#clone()}.
   */
  @Test
  public void testClone() {
    ObjectSettings objSettings = new ObjectSettings();
    Map<String, String> map = new HashMap<String, String>();
    map.put("key", "val");
    Whitebox.setInternalState(objSettings, "settings", map);

    ObjectSettings target = (ObjectSettings) objSettings.clone();

    assertThat(target, is(notNullValue()));
    @SuppressWarnings("unchecked")
    String val =
        ((Map<String, String>) Whitebox.getInternalState(target, "settings")).get("key");
    assertThat(val, is("val"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#getSetting(java.lang.String)}
   * .
   */
  @Test
  public void testGetSetting() {
    target = new ObjectSettings();
    Map<String, String> map = new HashMap<String, String>();
    map.put("key", "val");
    Whitebox.setInternalState(target, "settings", map);

    assertThat(target.getSetting("key"), is("val"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#getSetting(java.lang.String)}
   * .
   */
  @Test
  public void testGetSettingWithNotExistingKey() {
    target = new ObjectSettings();
    Map<String, String> map = new HashMap<String, String>();
    map.put("key", "val");
    Whitebox.setInternalState(target, "settings", map);

    assertNull(target.getSetting("nokey"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#setSetting(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetSetting() {
    target = new ObjectSettings();
    String ret = target.setSetting("key", "oldval");
    ret = target.setSetting("key", "newval");

    assertThat(target.getSetting("key"), is("newval"));
    assertThat(ret, is("oldval"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#deleteSetting(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteSetting() {
    target = new ObjectSettings();
    target.setSetting("key", "val");
    String ret = target.deleteSetting("key");

    assertNull(target.getSetting("key"));
    assertThat(ret, is("val"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#deleteSetting(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteSettingWithNotExistingKey() {
    target = new ObjectSettings();
    target.setSetting("key", "val");
    String ret = target.deleteSetting("nokey");

    assertThat(target.getSetting("key"), is("val"));
    assertNull(ret);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#putSetting(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testPutSettings() {
    target = new ObjectSettings();
    target.setSetting("oldkey", "oldval");
    ObjectSettings newSettings = new ObjectSettings();
    newSettings.setSetting("newkey", "newval");
    target.putSettings(newSettings);

    assertThat(target.getSetting("oldkey"), nullValue());
    assertThat(target.getSetting("newkey"), is("newval"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#getKeys()}.
   */
  @Test
  public void testGetKeys() {
    target = new ObjectSettings();
    target.setSetting("key1", "val1");
    target.setSetting("key2", "val2");
    Set<String> result = target.getKeys();

    assertThat(result.size(), is(2));
    assertTrue(result.contains("key1"));
    assertTrue(result.contains("key2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#isModify(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testIsModify() {
    target = new ObjectSettings();
    target.setSetting("key", "val");
    ObjectSettings newSettings = new ObjectSettings();
    newSettings.setSetting("key", "val");

    assertFalse(target.isModify(newSettings));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#isModify(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testIsModifyWithDifferentKey() {
    target = new ObjectSettings();
    target.setSetting("oldkey", "val");
    ObjectSettings newSettings = new ObjectSettings();
    newSettings.setSetting("newkey", "val");

    assertTrue(target.isModify(newSettings));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#isModify(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testIsModifyWithDifferentVal() {
    target = new ObjectSettings();
    target.setSetting("key", "oldval");
    ObjectSettings newSettings = new ObjectSettings();
    newSettings.setSetting("key", "newval");

    assertTrue(target.isModify(newSettings));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFrom() {
    target = new ObjectSettings();
    target.setSetting("key", "val");
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

    target = new ObjectSettings();
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception in readFrom()");
    }
    assertThat(target.getKeys().size(), is(1));
    assertThat(target.getSetting("key"), is("val"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.ObjectSettings#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteTo() {
    testReadFrom();
  }
}