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

package org.o3project.odenos.core.component.network.packet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
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
 * Test class for BasePacketAdded.
 *
 */
public class BasePacketAddedTest {

  private BasePacketAdded target;

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

    target = Mockito.spy(new BasePacketAdded() {
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
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#BasePacketAdded()}
   * .
   */
  @Test
  public void testBasePacketAdded() {

    /*
     * test
     */
    BasePacketAdded value = Mockito.spy(new BasePacketAdded() {
    });

    /*
     * check
     */
    assertThat(value.getId(), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#BasePacketAdded(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testBasePacketAddedPacket() {

    /*
     * test
     */
    Packet value = new InPacket();
    target.setId(value.getPacketId());

    /*
     * check
     */
    String result = target.getId();
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#getId()}
   * .
   */
  @Test
  public void testGetId() {

    /*
     * test
     */
    String result = target.getId();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#setId(java.lang.String)}
   * .
   */
  @Test
  public void testSetId() {
    /*
     * test
     */
    String value = "123456";
    target.setId(value);

    /*
     * check
     */
    String result = target.getId();
    assertThat(result, is("123456"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testReadFrom() throws Exception {
    /*
     * setting
     */
    Unpacker unpacker = Mockito.mock(Unpacker.class);

    when(unpacker.readMapBegin()).thenReturn(1);
    when(unpacker.readString()).thenReturn("id", "ID")
        .thenThrow(new IOException());

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readMapBegin();
    verify(unpacker, times(1)).readMapEnd();

    String value = target.getId();
    assertThat(value, is("ID"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.BasePacketAdded#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testWriteTo() throws Exception {

    Packer packer = Mockito.mock(Packer.class);
    target.setId("ID");

    /*
     * test
     */
    target.writeTo(packer);

    /*
     * check
     */

    verify(packer, times(1)).writeMapBegin(1);
    verify(packer, times(1)).writeMapEnd();

    verify(packer, times(1)).write("id");
    verify(packer, times(1)).write("ID");

  }
}
