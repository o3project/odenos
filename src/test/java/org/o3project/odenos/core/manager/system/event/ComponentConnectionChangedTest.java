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

package org.o3project.odenos.core.manager.system.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.powermock.reflect.Whitebox;

/**
 *
 */
public class ComponentConnectionChangedTest {

  private ComponentConnectionChanged target;
  private ComponentConnection prev;
  private ComponentConnection curr;
  private String action;

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
    prev = new ComponentConnection("prevID", "prevType", "prevState");
    curr = new ComponentConnection("currID", "currType", "currState");
    action = new String("action");
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
   * {@link org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged#ComponentConnectionChanged()}
   * .
   */
  @Test
  public void testComponentConnectionChanged() {
    target = new ComponentConnectionChanged();
    ComponentConnection prev = Whitebox.getInternalState(target, "prev");
    ComponentConnection curr = Whitebox.getInternalState(target, "curr");
    String action = Whitebox.getInternalState(target, "action");

    assertNull(prev);
    assertNull(curr);
    assertNull(action);

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertThat(clazz.getName(), is("org.o3project.odenos.core.manager.system.ComponentConnection"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.manager.system.event.ComponentConnectionChanged#ComponentConnectionChanged(java.lang.String, org.o3project.odenos.core.manager.system.ComponentConnection, org.o3project.odenos.core.manager.system.ComponentConnection)}
   * .
   */
  @Test
  public void testComponentConnectionChangedWithParams() {
    target = new ComponentConnectionChanged(action, prev, curr);
    ComponentConnection objPrev = Whitebox.getInternalState(target, "prev");
    ComponentConnection objCurr = Whitebox.getInternalState(target, "curr");
    String strAction = Whitebox.getInternalState(target, "action");

    assertThat(objPrev.getObjectId(), is("prevID"));
    assertThat(objCurr.getObjectId(), is("currID"));
    assertThat(strAction, is("action"));

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertNull(clazz);
  }
}
