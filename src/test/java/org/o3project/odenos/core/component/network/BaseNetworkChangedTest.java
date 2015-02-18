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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

/**
 * Test class for BaseNetworkChanged.
 */
public class BaseNetworkChangedTest {

  private BaseNetworkChanged<String> target;

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

    target = Mockito.spy(new BaseNetworkChanged<String>(String.class));

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
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#BaseNetworkChanged(java.lang.Class)}
   * .
   */
  @Test
  public void testBaseNetworkChangedClassOfT() {

    /*
     * test
     */
    BaseNetworkChanged<String> target = new BaseNetworkChanged<String>(String.class);

    /*
     * check
     */
    assertThat(target.id, is(nullValue()));
    assertThat(target.action, is(nullValue()));
    assertThat(target.prev, is(nullValue()));
    assertThat(target.curr, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#BaseNetworkChanged(java.lang.String, java.lang.Object, java.lang.Object)}
   * .
   */
  @Test
  public void testBaseNetworkChangedStringT() {

    /*
     * test
     */
    BaseNetworkChanged<String> target =
        new BaseNetworkChanged<String>("ACTION", "PREV", "CURR");

    /*
     * check
     */
    assertThat(target.action, is("ACTION"));
    assertThat(target.prev, is("PREV"));
    assertThat(target.curr, is("CURR"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#setId(java.lang.String)}
   * .
   */
  @Test
  public void testSetId() {

    /*
     * test
     */
    String id = "id";
    target.setId(id);

    /*
     * check
     */
    assertThat(target.id, is(id));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#setVersion(java.lang.String)}
   * .
   */
  @Test
  public void testSetVersion() {

    /*
     * test
     */
    String version = "version";
    target.setVersion(version);

    /*
     * check
     */
    assertThat(target.version, is(version));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReadFrom() throws Exception {

    /*
     * setting
     */
    Unpacker unpacker = Mockito.mock(Unpacker.class);

    when(unpacker.readMapBegin()).thenReturn(5);
    when(unpacker.readString()).thenReturn("id", "ID")
        .thenReturn("version", "VERSION")
        .thenReturn("action", "ACTION")
        .thenReturn("prev")
        .thenReturn("curr")
        .thenThrow(new IOException());

    when(unpacker.trySkipNil()).thenReturn(false);

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readMapBegin();
    verify(unpacker, times(1)).readMapEnd();

    assertThat(target.id, is("ID"));
    assertThat(target.version, is("VERSION"));
    assertThat(target.action, is("ACTION"));
    assertThat(target.prev, is(nullValue()));
    assertThat(target.curr, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.BaseNetworkChanged#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testWriteTo() throws Exception {

    /*
     * setting
     */
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyString());

    target.version = "VERSION";
    target.action = "ACTION";
    target.prev = "PREV";
    target.curr = "CURR";
    target.id = "ID";

    /*
     * test
     */
    target.writeTo(packer);

    /*
     * check
     */

    verify(packer, times(1)).writeMapBegin(5);
    verify(packer, times(1)).writeMapEnd();

    verify(packer, times(1)).write("id");
    verify(packer, times(1)).write("ID");
    verify(packer, times(1)).write("version");
    verify(packer, times(1)).write("VERSION");
    verify(packer, times(1)).write("action");
    verify(packer, times(1)).write("ACTION");
    verify(packer, times(1)).write("prev");
    verify(packer, times(1)).write("curr");

  }

}
