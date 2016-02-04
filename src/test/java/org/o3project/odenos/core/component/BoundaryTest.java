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

package org.o3project.odenos.core.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.powermock.reflect.Whitebox;

import java.util.Map;

/**
 * Test class for Boundary.
 */
public class BoundaryTest {

  private Boundary target;

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

    target = Mockito.spy(new Boundary("BoundaryId", "BoundaryType") {
      public boolean readValue(Value value) {
        return false;
      }

      @Override
      public boolean writeValueSub(Map<String, Value> values) {
        return false;
      }
    });

  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#Boundary()}.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testBoundary() {

    /*
     * test
     */
    target = new Boundary() {
      public boolean readValue(Value value) {
        return false;
      }

      public boolean writeValueSub(Map<String, Value> values) {
        return false;
      }
    };

    /*
     * check
     */
    assertThat(target, is(notNullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#Boundary(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testBoundaryStringString() {

    /*
     * test
     */
    target = Mockito.spy(new Boundary("BoundaryId", "BoundaryType") {
      public boolean readValue(Value value) {
        return false;
      }

      @Override
      public boolean writeValueSub(Map<String, Value> values) {
        return false;
      }
    });

    /*
     * check
     */
    String id = target.getId();
    assertThat(id, is("BoundaryId"));

    String type = target.getType();
    assertThat(type, is("BoundaryType"));
  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#getId()}.
   */
  @Test
  public void testGetId() {

    /*
     * test
     */
    String id = target.getId();

    /*
     * check
     */
    assertThat(id, is("BoundaryId"));

    String resultId = Whitebox.getInternalState(target, "id");
    assertThat(resultId, is("BoundaryId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {

    /*
     * test
     */
    target.setId("NewBoundaryId");

    /*
     * check
     */
    assertThat(target.getId(), is("NewBoundaryId"));

    String resultId = Whitebox.getInternalState(target, "id");
    assertThat(resultId, is("NewBoundaryId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#getType()}.
   */
  @Test
  public void testGetType() {

    /*
     * test
     */
    String type = target.getType();

    /*
     * check
     */
    assertThat(type, is("BoundaryType"));

    String resultType = Whitebox.getInternalState(target, "type");
    assertThat(resultType, is("BoundaryType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#setType(java.lang.String)}.
   */
  @Test
  public void testSetType() {

    /*
     * test
     */
    target.setType("NewBoundaryType");

    /*
     * check
     */
    assertThat(target.getType(), is("NewBoundaryType"));

    String resultType = Whitebox.getInternalState(target, "type");
    assertThat(resultType, is("NewBoundaryType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.core.component.Boundary#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[id=BoundaryId,type=BoundaryType]"), is(true));

  }

}
