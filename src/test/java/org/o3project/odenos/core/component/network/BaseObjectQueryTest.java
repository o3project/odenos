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

package org.o3project.odenos.core.component.network;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.BaseObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for BaseObjectQuery.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ BaseObject.class })
@PowerMockIgnore({"javax.management.*"})
public class BaseObjectQueryTest {

  private BaseObjectQuery<String> target;

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

    target = Mockito.spy(new BaseObjectQuery<String>(
        "attributes=abc=123,def=456"));

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
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#BaseObjectQuery(java.lang.String)}
   * .
   */
  @Test
  public void testBaseObjectQuery() {

    /*
     * test
     */
    BaseObjectQuery<String> result = new BaseObjectQuery<String>(
        "attributes=abc=123,def=456");

    /*
     * check
     */
    assertThat(result.attributes.isEmpty(), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#parse()}.
   */
  @Test
  public void testParse() {

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, String> resultMap = target.attributes;
    assertThat(resultMap.size(), is(2));
    assertThat(resultMap.get("abc"), is("123"));
    assertThat(resultMap.get("def"), is("456"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#parse()}.
   */
  @Test
  public void testParse_EmptyQueriesString() {

    /*
     * setting
     */
    target = new BaseObjectQuery<>("");

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#matchExactly(org.o3project.odenos.remoteobject.message.BaseObject)}
   * .
   */
  @Test
  public void testMatchExactly() {

    /*
     * setting
     */
    Map<String, String> attributeMap = new HashMap<String, String>();
    attributeMap.put("abc", "123");
    attributeMap.put("def", "456");
    attributeMap.put("ghi", "789");

    BaseObject baseObject = PowerMockito.mock(BaseObject.class);
    PowerMockito.doReturn(attributeMap).when(baseObject).getAttributes();

    target.attributes = attributeMap;

    /*
     * test
     */
    boolean result = target.matchExactly(baseObject);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#matchExactly(org.o3project.odenos.remoteobject.message.BaseObject)}
   * .
   */
  @Test
  public void testMatchExactly_NullAttributes() {

    /*
     * setting
     */
    BaseObject baseObject = new BaseObject() {

      @Override
      public void writeTo(Packer packer) throws IOException {
        fail();
      }

      @Override
      public void readFrom(Unpacker unpacker) throws IOException {
        fail();
      }
    };

    /*
     * test
     */
    boolean result = target.matchExactly(baseObject);

    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#getAttributeValue(java.lang.String)}
   * .
   */
  @Test
  public void testGetAttributeValue() {

    /*
     * test
     */
    String result = target.getAttributeValue("abc");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseObjectQuery#getAttributeValue(java.lang.String)}
   * .
   */
  @Test
  public void testGetAttributeValue_AfterSetValue() {

    /*
     * setting
     */
    Map<String, String> settingMap = new HashMap<String, String>();
    settingMap.put("abc", "123");
    settingMap.put("def", "456");

    target.attributes = settingMap;

    /*
     * test
     */
    String result = target.getAttributeValue("abc");

    /*
     * check
     */
    assertThat(result, is("123"));

  }

}
