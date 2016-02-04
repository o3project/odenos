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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

/**
 * Test class for Component.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Component.class })
@PowerMockIgnore({"javax.management.*"})
public class ComponentTest {

  private Component target;
  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);

    target = PowerMockito.spy(new Component("objectId", dispatcher) {
      @Override
      protected String getSuperType() {
        return "SuperType";
      }

      @Override
      protected String getDescription() {
        return "Description";
      }
    });
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    dispatcher = null;
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#Component(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testComponent() throws Exception {

    /*
     * test
     */
    Component result = new Component("ObjectId", dispatcher) {

      @Override
      protected String getSuperType() {
        return "SuperType";
      }

      @Override
      protected String getDescription() {
        return "Description";
      }
    };

    /*
     * check
     */
    assertThat(result.getObjectId(), is("ObjectId"));
    assertThat(result.getMessageDispatcher(), is(dispatcher));

    ObjectProperty objectProperty = result.getProperty();
    assertThat(objectProperty.getProperty("type"), is(""));
    assertThat(objectProperty.getProperty("id"), is("ObjectId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#Component(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public void testComponent_null() {

    /*
     * test
     */
    Component result = new Component(null, null) {

      @Override
      protected String getSuperType() {
        return objectProperty.getProperty(ObjectProperty.PropertyNames.OBJECT_SUPER_TYPE);
      }

      @Override
      protected String getDescription() {
        return objectProperty.getProperty(ObjectProperty.PropertyNames.DESCRIPTION);
      }

    };

    /*
     * check
     */
    assertThat(result.getObjectId(), is(nullValue()));
    assertThat(result.getMessageDispatcher(), is(nullValue()));

    ObjectProperty objectProperty = result.getProperty();
    assertThat(objectProperty.getProperty("type"), is(""));
    assertThat(objectProperty.getProperty("id"), is(nullValue()));
    assertThat(objectProperty.getProperty("base_uri"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#getSuperType()}.
   */
  @Test
  public void testGetSuperType() {

    /*
     * test & check
     */
    assertThat(target.getSuperType(), is("SuperType"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#getDescription()}.
   */
  @Test
  public void testGetDescription() {

    /*
     * test & check
     */
    assertThat(target.getDescription(), is("Description"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#getConnectionTypes()}.
   */
  @Test
  public void testGetConnectionTypes() {

    /*
     * test & check
     */
    assertThat(target.getConnectionTypes(), is(""));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#setSuperType()}.
   */
  @Test
  public void testSetSuperType() {

    /*
     * test
     */
    target.setSuperType();

    /*
     * check
     */
    verify(target, times(1)).getSuperType();
    verify(target, times(1)).setSuperType();

    assertThat(
        target.getProperty().getProperty(ObjectProperty.PropertyNames.OBJECT_SUPER_TYPE),
        is("SuperType"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#setDescription()}.
   */
  @Test
  public void testSetDescription() {

    /*
     * test
     */
    target.setDescription();

    /*
     * check
     */
    verify(target, times(1)).getDescription();
    verify(target, times(1)).setDescription();

    assertThat(target.getProperty().getProperty(ObjectProperty.PropertyNames.DESCRIPTION),
        is("Description"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#setConnectionTypes()}.
   */
  @Test
  public void testSetConnectionTypes() {

    /*
     * test
     */
    target.setConnectionTypes();

    /*
     * check
     */
    verify(target, times(3)).getConnectionTypes();
    verify(target, times(1)).setConnectionTypes();

    assertThat(target.getProperty().getProperty(ObjectProperty.PropertyNames.CONNECTION_TYPES),
        is(""));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Component#resetEventSubscription()}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testResetEventSubscription() throws Exception {

    /*
     * setting
     */
    EventSubscription eventSubscription = Mockito.spy(new EventSubscription("ObjectId"));
    Whitebox.setInternalState(target, "eventSubscription", eventSubscription);

    doReturn("SystemManagerId").when(dispatcher).getSystemManagerId();

    PowerMockito.doReturn(null).when(target, "applyEventSubscription");

    /*
     * test
     */
    target.resetEventSubscription();

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("applyEventSubscription");

    verify(eventSubscription).clearFilter();
    verify(eventSubscription).addFilter("SystemManagerId", "ComponentConnectionChanged");

  }

}
