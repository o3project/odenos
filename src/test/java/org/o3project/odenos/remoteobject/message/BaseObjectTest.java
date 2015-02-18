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

package org.o3project.odenos.remoteobject.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for BaseObject.
 */
public class BaseObjectTest {

  private BaseObject target;

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

    target = Mockito.spy(new BaseObject() {

      @Override
      public void writeTo(Packer packer) throws IOException {
        fail();
      }

      @Override
      public void readFrom(Unpacker unpacker) throws IOException {
        fail();
      }
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
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#getVersion()}.
   */
  @Test
  public void testGetVersion() {

    /*
     * test
     */
    String result = target.getVersion();

    /*
     * check
     */
    assertThat(result, is("0"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#setVersion(int)}
   * .
   */
  @Test
  public void testSetVersionInt() {

    /*
     * test
     */
    target.setVersion(123);

    /*
     * check
     */
    String result = target.getVersion();
    assertThat(result, is("123"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#setVersion(java.lang.String)}
   * .
   */
  @Test
  public void testSetVersionString() {

    /*
     * test
     */
    target.setVersion("123");

    /*
     * check
     */
    String result = target.getVersion();
    assertThat(result, is("123"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#setVersion(java.lang.String)}
   * .
   */
  @Test
  public void testSetVersionStringWithNoInteger() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "version", 123);

    /*
     * test
     */
    target.setVersion("abc");

    /*
     * check
     */
    String result = target.getVersion();

    assertThat(result, is("123"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#updateVersion()}
   * .
   */
  @Test
  public void testUpdateVersion() {

    /*
     * test
     */
    target.updateVersion();

    /*
     * check
     */
    String result = target.getVersion();
    assertThat(result, is("1"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#getAttribute(java.lang.String)}
   * .
   */
  @Test
  public void testGetAttribute() {

    /*
     * setting
     */
    target.putAttribute("key", "value");

    /*
     * test & check
     */
    assertThat(target.getAttribute("nothing"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#getAttribute(java.lang.String)}
   * .
   */
  @Test
  public void testGetAttributeWithNoKey() {

    /*
     * test & check
     */
    assertThat(target.getAttribute("nothing"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#putAttribute(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testPutAttribute() {

    /*
     * setting
     */
    target.putAttribute("key", "value");

    /*
     * test
     */
    String result = target.getAttribute("key");

    /*
     * check
     */
    assertThat(result, is("value"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#getAttributes()}
   * .
   */
  @Test
  public void testGetAttributes() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key1", "value1");
    attributes.put("key2", "value2");
    attributes.put("key3", "value3");
    target.putAttributes(attributes);

    /*
     * test
     */
    Map<String, String> result = target.getAttributes();

    /*
     * check
     */
    assertThat(result, is(attributes));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#getAttributes()}
   * .
   */
  @Test
  public void testGetAttributesWithNoKey() {

    /*
     * test
     */
    Map<String, String> result = target.getAttributes();

    /*
     * check
     */
    Map<String, String> expected = new HashMap<String, String>();
    assertThat(result, is(expected));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#putAttributes(java.util.Map)}
   * .
   */
  @Test
  public void testPutAttributes() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key1", "value1");
    attributes.put("key2", "value2");
    attributes.put("key3", "value3");

    /*
     * test
     */
    target.putAttributes(attributes);

    /*
     * check
     */
    Map<String, String> result = target.getAttributes();
    assertThat(result, is(attributes));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#putAttributes(java.util.Map)}
   * .
   */
  @Test
  public void testPutAttributesWithNull() {

    /*
     * test
     */
    target.putAttributes(null);

    /*
     * check
     */
    Map<String, String> expect = new HashMap<String, String>();
    assertThat(target.getAttributes(), is(expect));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#putAttributes(java.util.Map)}
   * .
   */
  @Test
  public void testPutAttributesWithAppendAttribute() {

    /*
     * setting
     */
    target.putAttribute("key1", "value1");
    target.putAttribute("key2", "value2");
    target.putAttribute("key3", "value3");

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key4", "value4");
    attributes.put("key5", "value5");
    attributes.put("key6", "value6");

    /*
     * test
     */
    target.putAttributes(attributes);

    /*
     * check
     */
    Map<String, String> result = target.getAttributes();

    assertThat(result.size(), is(3));
    assertThat(result, is(attributes));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.BaseObject#deleteAttribute(java.lang.String)}
   * .
   */
  @Test
  public void testDeleteAttribute() {

    /*
     * setting
     */
    target.putAttribute("key", "value");

    /*
     * test
     */
    target.deleteAttribute("key");

    /*
     * check
     */
    assertThat(target.getAttribute("key"), is(nullValue()));
  }

}
