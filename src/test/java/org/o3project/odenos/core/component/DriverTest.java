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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

/**
 * test class for Driver.
 */
public class DriverTest {

  @SuppressWarnings("unused")
  private Driver target;
  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);

    target = Mockito.spy(new Driver("objectId", dispatcher) {

      @Override
      protected String getDescription() {
        return "Description";
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
   * {@link org.o3project.odenos.core.component.Driver#Driver(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDriver() throws Exception {

    /*
     * test
     */
    Driver result = new Driver("ObjectId", dispatcher) {

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
    assertThat(objectProperty.getProperty("id"), is("ObjectId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.Driver#Driver(java.lang.String, java.lang.String, org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDriver_allNull() throws Exception {

    /*
     * test
     */
    Driver result = new Driver(null, null) {

      @Override
      protected String getDescription() {
        return "Description";
      }
    };

    /*
     * check
     */
    assertThat(result.getObjectId(), is(nullValue()));
    assertThat(result.getMessageDispatcher(), is(nullValue()));

    ObjectProperty objectProperty = result.getProperty();
    assertThat(objectProperty.getProperty("id"), is(nullValue()));
    assertThat(objectProperty.getProperty("base_uri"), is(nullValue()));

  }

}
