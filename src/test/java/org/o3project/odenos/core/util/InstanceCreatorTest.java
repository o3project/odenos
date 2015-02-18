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

package org.o3project.odenos.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Map;

/**
 *
 */
public class InstanceCreatorTest {

  private String target;

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
   * Test method for {@link
   * org.o3project.odenos.util.InstanceCreator#create(java.lang.String,
   * java.lang.Class<>[], java.lang.Object[])}.
   */
  @Test
  public void testCreateTest() {
    Object[] arg = { new String("Test") };
    Class<?>[] type = { String.class };
    target = (String) InstanceCreator.create("java.lang.String", type, arg);
    Map<String, Class<?>> map = Whitebox.getInternalState(
        InstanceCreator.class, "classCache");
    assertThat(target, is(notNullValue()));
    assertThat(target, is("Test"));
    assertThat(map.get("java.lang.String"), is(notNullValue()));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.InstanceCreator#create(java.lang.String,
   * java.lang.Class<>[], java.lang.Object[])}.
   */
  @Test
  public void testCreateTestWithInvalidClassName() {
    Object[] arg = { new String("Test") };
    Class<?>[] type = { String.class };
    target = (String) InstanceCreator.create("foo.bar.Hoge", type, arg);
    Map<String, Class<?>> map = Whitebox.getInternalState(
        InstanceCreator.class, "classCache");
    assertNull(target);
    assertNull(map.get("foo.bar.Hoge"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.InstanceCreator#create(java.lang.String,
   * java.lang.Class<>[], java.lang.Object[])}.
   */
  @Test
  public void testCreateTestWithInvalidConstructorParameterType() {
    Object[] arg = { new String("Test") };
    Class<?>[] type = { String.class, Integer.class };
    target = (String) InstanceCreator.create("java.lang.String", type, arg);
    Map<String, Class<?>> map = Whitebox.getInternalState(
        InstanceCreator.class, "classCache");
    assertNull(target);
    assertThat(map.get("java.lang.String"), is(notNullValue()));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.InstanceCreator#create(java.lang.String,
   * java.lang.Class<>[], java.lang.Object[])}.
   */
  @Test
  public void testCreateTestWithInvalidConstructorParameter() {
    Object[] arg = { new String("Test"), new Integer(1) };
    Class<?>[] type = { String.class };
    target = (String) InstanceCreator.create("java.lang.String", type, arg);
    Map<String, Class<?>> map = Whitebox.getInternalState(
        InstanceCreator.class, "classCache");
    assertNull(target);
    assertThat(map.get("java.lang.String"), is(notNullValue()));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.core.util.InstanceCreator#InstanceCreator()}.
   */
  @Test
  public void testInstanceCreator() throws Exception {
    InstanceCreator target;
    target = Whitebox.invokeConstructor(InstanceCreator.class);

    assertThat(target, is(notNullValue()));
  }
}