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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for BasicQuery.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ BasicQuery.class })
public class BasicQueryTest {

  private BasicQuery<String> target;

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

    target = Mockito.spy(new BasicQuery<String>("") {
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
   * {@link org.o3project.odenos.core.component.network.BasicQuery#BasicQuery()}.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testBasicQuery() {

    /*
     * test
     */
    target = new BasicQuery<String>() {
    };

    /*
     * check
     */
    assertThat(target.queries.size(), is(0));

    String resultQueriesString = Whitebox.getInternalState(target, "queriesString");
    assertThat(resultQueriesString, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#BasicQuery(java.lang.String)}
   * .
   */
  @Test
  public void testBasicQueryString() {

    /*
     * test
     */
    target = new BasicQuery<String>("abc=123&def=456") {
    };

    /*
     * check
     */
    assertThat(target.queries.size(), is(0));

    String resultQueriesString = Whitebox.getInternalState(target, "queriesString");
    assertThat(resultQueriesString, is("abc=123&def=456"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse() {

    /*
     * setting
     */
    target = new BasicQuery<String>("\"abc\"=123&\"def\"=456&\"ghi\"=789") {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, String> resultMap = target.queries;
    assertThat(resultMap.size(), is(3));
    assertThat(resultMap.get("abc"), is("123"));
    assertThat(resultMap.get("def"), is("456"));
    assertThat(resultMap.get("ghi"), is("789"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse_OneArguments() {

    /*
     * setting
     */
    target = new BasicQuery<String>("\"abc\"=123") {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, String> resultMap = target.queries;
    assertThat(resultMap.size(), is(1));
    assertThat(resultMap.get("abc"), is("123"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse_Empty() {

    /*
     * setting
     */
    target = new BasicQuery<String>("") {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, String> resultMap = target.queries;
    assertThat(resultMap.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse_MoreRegex() {

    /*
     * setting
     */
    target = new BasicQuery<String>("\"abc\"=123&\"def\"=456&\"ghi\"=\"jkl\"=789") {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    Map<String, String> resultMap = target.queries;
    assertThat(resultMap.size(), is(3));
    assertThat(resultMap.get("abc"), is("123"));
    assertThat(resultMap.get("def"), is("456"));
    assertThat(resultMap.get("ghi"), is("jkl=789"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse_NothingRegex() {

    /*
     * setting
     */
    target = new BasicQuery<String>("\"abc\"=123&\"def\"=456&\"ghi\"@789") {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#parse()}.
   */
  @Test
  public void testParse_NullQueriesString() {

    /*
     * setting
     */
    target = new BasicQuery<String>(null) {
    };

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#convertToMap(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testConvertToMap() {

    /*
     * setting
     */
    String[] src = { "abc=123", "def=456", "ghi=789" };

    /*
     * test
     */
    Map<String, String> result = BasicQuery.convertToMap(src, "=");

    /*
     * check
     */
    assertThat(result.size(), is(src.length));
    assertThat(result.get("abc"), is("123"));
    assertThat(result.get("def"), is("456"));
    assertThat(result.get("ghi"), is("789"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#convertToMap(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testConvertToMap_RegexMulticharacter() {

    /*
     * setting
     */
    String[] src = { "abc=&=123", "def=&=456", "ghi=&=789" };

    /*
     * test
     */
    Map<String, String> result = BasicQuery.convertToMap(src, "=&=");

    /*
     * check
     */
    assertThat(result.size(), is(src.length));
    assertThat(result.get("abc"), is("123"));
    assertThat(result.get("def"), is("456"));
    assertThat(result.get("ghi"), is("789"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#convertToMap(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testConvertToMap_InvalidRegex() {

    /*
     * setting
     */
    String[] src = { "abc=123", "def=456", "ghi@789" };

    /*
     * test
     */
    Map<String, String> result = BasicQuery.convertToMap(src, "=");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#convertToMap(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testConvertToMap_Invalidformat() {

    /*
     * setting
     */
    String[] src = { "abc=123", "def=456", "ghi=jkl=012" };

    /*
     * test
     */
    Map<String, String> result = BasicQuery.convertToMap(src, "=");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#convertToMap(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testConvertToMap_EmptyStrings() {

    /*
     * setting
     */
    String[] src = {};

    /*
     * test
     */
    Map<String, String> result = BasicQuery.convertToMap(src, "=");

    /*
     * check
     */
    assertThat(result.size(), is(src.length));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_SameMapAndKey() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("aaa", "aaa");
    map.put("bbb", "bbb");
    map.put("ccc", "ccc");

    String[] keys = { "aaa", "bbb", "ccc" };

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_DifferKeys() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("aaa", "aaa");
    map.put("bbb", "bbb");
    map.put("ccc", "ccc");

    String[] keys = { "aaa", "zzz", "ccc" };

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_UnderKeyCounts() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("aaa", "aaa");
    map.put("bbb", "bbb");
    map.put("ccc", "ccc");

    String[] keys = { "aaa", "bbb" };

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_OverKeyCounts() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("aaa", "aaa");
    map.put("bbb", "bbb");

    String[] keys = { "aaa", "bbb", "ccc" };

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_EmptyMap() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();

    String[] keys = { "aaa", "bbb", "ccc" };

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#checkMapExactly(java.util.Map, java.lang.String[])}
   * .
   */
  @Test
  public void testCheckMapExactly_EmptyKeys() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("aaa", "aaa");
    map.put("bbb", "bbb");
    map.put("ccc", "ccc");

    String[] keys = {};

    /*
     * test
     */
    boolean result = BasicQuery.checkMapExactly(map, keys);

    /*
     * check
     */
    assertThat(result, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateInteger(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateInteger() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("keyIntegerMax", String.valueOf(Integer.MAX_VALUE));
    map.put("keyIntegerMin", String.valueOf(Integer.MIN_VALUE));
    map.put("keyIntegerZero", String.valueOf(0));

    /*
     * test
     */
    Integer resultMax = BasicQuery.cretateInteger(map, "keyIntegerMax");
    Integer resultMin = BasicQuery.cretateInteger(map, "keyIntegerMin");
    Integer resultZero = BasicQuery.cretateInteger(map, "keyIntegerZero");

    /*
     * check
     */
    assertThat(resultMax, is(new Integer(Integer.MAX_VALUE)));
    assertThat(resultMin, is(new Integer(Integer.MIN_VALUE)));
    assertThat(resultZero, is(new Integer(0)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateInteger(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateIntegerIllegalValue() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("KeyString", "abc");
    map.put("KeyOverInteger", "9999999999");
    map.put("KeyUnderInteger", "-9999999999");

    /*
     * test
     */
    Integer resultString = BasicQuery.cretateInteger(map, "KeyString");
    Integer resultOverInteger = BasicQuery.cretateInteger(map, "KeyOverInteger");
    Integer resultUnderInteger = BasicQuery.cretateInteger(map, "KeyUnderInteger");

    /*
     * check
     */
    assertThat(resultString, is(nullValue()));
    assertThat(resultOverInteger, is(nullValue()));
    assertThat(resultUnderInteger, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateInteger(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateIntegerNothingKey() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("Key", "123");

    /*
     * test
     */
    Integer result = BasicQuery.cretateInteger(map, "Nothing");

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateLong(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateLong() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("keyLongMax", String.valueOf(Long.MAX_VALUE));
    map.put("keyLongMin", String.valueOf(Long.MIN_VALUE));
    map.put("keyLongZero", String.valueOf(0L));

    /*
     * test
     */
    Long resultMax = BasicQuery.cretateLong(map, "keyLongMax");
    Long resultMin = BasicQuery.cretateLong(map, "keyLongMin");
    Long resultZero = BasicQuery.cretateLong(map, "keyLongZero");

    /*
     * check
     */
    assertThat(resultMax, is(new Long(Long.MAX_VALUE)));
    assertThat(resultMin, is(new Long(Long.MIN_VALUE)));
    assertThat(resultZero, is(new Long(0L)));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateLong(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateLongIllegalValue() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("KeyString", "abc");
    map.put("KeyOverLong", "9999999999999999999");
    map.put("KeyUnderLong", "-9999999999999999999");

    /*
     * test
     */
    Long resultString = BasicQuery.cretateLong(map, "KeyString");
    Long resultOverLong = BasicQuery.cretateLong(map, "KeyOverLong");
    Long resultUnderLong = BasicQuery.cretateLong(map, "KeyUnderLong");

    /*
     * check
     */
    assertThat(resultString, is(nullValue()));
    assertThat(resultOverLong, is(nullValue()));
    assertThat(resultUnderLong, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BasicQuery#cretateLong(java.util.Map, java.lang.String)}
   * .
   */
  @Test
  public void testCretateLongNothingKey() {

    /*
     * setting
     */
    Map<String, String> map = new HashMap<String, String>();
    map.put("Key", "123");

    /*
     * test
     */
    Long result = BasicQuery.cretateLong(map, "Nothing");

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

}
