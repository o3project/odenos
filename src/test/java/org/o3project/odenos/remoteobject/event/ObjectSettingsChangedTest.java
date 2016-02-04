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

package org.o3project.odenos.remoteobject.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.remoteobject.ObjectSettings;
import org.powermock.reflect.Whitebox;

/**
 *
 */
public class ObjectSettingsChangedTest {

  private ObjectSettingsChanged target;
  private ObjectSettings prev;
  private ObjectSettings curr;
  private String action;

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
    prev = new ObjectSettings();
    curr = new ObjectSettings();
    prev.setSetting("keyPrev", "valPrev");
    curr.setSetting("keyCurr", "valCurr");
    action = new String("action");
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
   * {@link org.o3project.odenos.remoteobject.event.ObjectSettingsChanged#ObjectSettingsChanged()}
   * .
   */
  @Test
  public void testObjectSettingsChanged() {
    target = new ObjectSettingsChanged();
    ObjectSettings prev = Whitebox.getInternalState(target, "prev");
    ObjectSettings curr = Whitebox.getInternalState(target, "curr");
    String action = Whitebox.getInternalState(target, "action");

    assertNull(prev);
    assertNull(curr);
    assertNull(action);

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertThat(clazz.getName(), is("org.o3project.odenos.remoteobject.ObjectSettings"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.ObjectSettingsChanged#ObjectSettingsChanged(java.lang.String, org.o3project.odenos.remoteobject.event.ObjectSetting, org.o3project.odenos.remoteobject.event.ObjectSetting)}
   * .
   */
  @Test
  public void testObjectSettingsChangedWithParams() {
    target = new ObjectSettingsChanged(action, prev, curr);
    ObjectSettings objPrev = Whitebox.getInternalState(target, "prev");
    ObjectSettings objCurr = Whitebox.getInternalState(target, "curr");
    String strAction = Whitebox.getInternalState(target, "action");

    assertThat(objPrev.getSetting("keyPrev"), is("valPrev"));
    assertThat(objCurr.getSetting("keyCurr"), is("valCurr"));
    assertThat(strAction, is("action"));

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertNull(clazz);
  }
}
