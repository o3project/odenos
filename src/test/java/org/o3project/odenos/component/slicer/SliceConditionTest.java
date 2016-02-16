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

package org.o3project.odenos.component.slicer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * +Test class for SliceCondition.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SliceCondition.class })
@PowerMockIgnore({"javax.management.*"})
public class SliceConditionTest {

  private SliceCondition target;

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

    target = Mockito.spy(new SliceCondition("SetId", "SetType",
        "SetConnection") {
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
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#SliceCondition(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testSliceCondition() throws Exception {

    /*
     * test
     */
    SliceCondition target = new SliceCondition("NewId", "NewType",
        "NewConnection") {
    };

    /*
     * check
     */
    String id = Whitebox.getInternalState(target, "id");
    String type = Whitebox.getInternalState(target, "type");
    String connection = Whitebox.getInternalState(target, "connection");

    assertThat(id, is("NewId"));
    assertThat(type, is("NewType"));
    assertThat(connection, is("NewConnection"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#getId()}.
   */
  @Test
  public void testGetId() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "id", "NewId");

    /*
     * test
     */
    String result = target.getId();

    /*
     * check
     */
    assertThat(result, is("NewId"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#getType()}.
   */
  @Test
  public void testGetType() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "type", "NewType");

    /*
     * test
     */
    String result = target.getType();

    /*
     * check
     */
    assertThat(result, is("NewType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#getConnection()}.
   */
  @Test
  public void testGetConnection() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "connection", "NewConnection");

    /*
     * test
     */
    String result = target.getConnection();

    /*
     * check
     */
    assertThat(result, is("NewConnection"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {

    /*
     * test
     */
    target.setId("NewId");

    /*
     * check
     */
    assertThat(target.getId(), is("NewId"));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("NewId"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#setType(java.lang.String)}.
   */
  @Test
  public void testSetType() {

    /*
     * test
     */
    target.setType("NewType");

    /*
     * check
     */
    assertThat(target.getType(), is("NewType"));

    String type = Whitebox.getInternalState(target, "type");
    assertThat(type, is("NewType"));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#setConnection(java.lang.String)}.
   */
  @Test
  public void testSetConnection() {

    /*
     * test
     */
    target.setConnection("NewConnection");

    /*
     * check
     */
    assertThat(target.getConnection(), is("NewConnection"));

    String connection = Whitebox.getInternalState(target, "connection");
    assertThat(connection, is("NewConnection"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testReadFrom() throws Exception {

    /*
     * setting
     */
    Unpacker upk = Mockito.mock(Unpacker.class);

    doNothing().when(target).doReadFrom(upk);

    doReturn(3).when(upk).readMapBegin();
    doNothing().when(upk).readMapEnd();

    /*
     * test
     */
    target.readFrom(upk);

    /*
     * check
     */
    verify(target, times(3)).doReadFrom(upk);

    InOrder upkOrder = Mockito.inOrder(upk);
    upkOrder.verify(upk).readMapBegin();
    upkOrder.verify(upk).readMapEnd();
    upkOrder.verifyNoMoreInteractions();

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception throws Exception in targets
   */
  @Test(expected = IOException.class)
  public void testReadFrom_InvalidPacker() throws Exception {

    /*
     * setting
     */
    Unpacker upk = Mockito.mock(Unpacker.class);

    doNothing().when(target).doReadFrom(upk);

    doReturn(999).when(upk).readMapBegin();
    doNothing().when(upk).readMapEnd();

    /*
     * test
     */
    target.readFrom(upk);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception throws Exception in targets
   */
  @Test(expected = IllegalArgumentException.class)
  public void testReadFrom_Null() throws Exception {

    /*
     * test
     */
    target.readFrom(null);

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#doReadFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDoReadFrom() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SliceCondition("SetId", "SetType",
        "SetConnection") {
    });

    Unpacker upk = Mockito.mock(Unpacker.class);

    when(upk.readString())
        .thenReturn("id", "SetId")
        .thenReturn("type", "SetType")
        .thenReturn("connection", "SetConnection");

    /*
     * test
     */
    /* id */
    target.doReadFrom(upk);
    /* type */
    target.doReadFrom(upk);
    /* connection */
    target.doReadFrom(upk);

    /*
     * check
     */
    verify(upk, times(6)).readString();

    /*
     * PowerMockito not implement InOrder
     */
    // InOrder inOrder = PowerMockito.inOrder(target);
    // inOrder.verify(target).doReadFrom((Unpacker) anyObject());
    // inOrder.verify(target).setId("SetId");
    // inOrder.verify(target).doReadFrom((Unpacker) anyObject());
    // inOrder.verify(target).setType("SetType");
    // inOrder.verify(target).doReadFrom((Unpacker) anyObject());
    // inOrder.verify(target).setConnection("SetConnection");
    //
    // inOrder.verifyNoMoreInteractions();

    verify(target).setId("SetId");
    verify(target).setType("SetType");
    verify(target).setConnection("SetConnection");

    assertThat(target.getId(), is("SetId"));
    assertThat(target.getType(), is("SetType"));
    assertThat(target.getConnection(), is("SetConnection"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#readString(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testReadString() throws Exception {

    /*
     * setting
     */
    Unpacker upk = createUnpackerData();

    /*
     * test & check
     */
    upk.readMapBegin();

    String result1 = target.readString(upk);
    assertThat(result1, is("param1"));

    String result2 = target.readString(upk);
    assertThat(result2, is("abc"));

    String result3 = target.readString(upk);
    assertThat(result3, is("param2"));

    String result4 = target.readString(upk);
    assertThat(result4, is(nullValue()));

    String result5 = target.readString(upk);
    assertThat(result5, is("param3"));

    String result6 = target.readString(upk);
    assertThat(result6, is(nullValue()));

    upk.readMapEnd();

  }

  Unpacker createUnpackerData() throws Exception {

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    MessagePack messagePack = new MessagePack();
    Packer pk = messagePack.createPacker(bos);

    pk.writeMapBegin(3);

    pk.write("param1");
    pk.write("abc");

    pk.write("param2");
    pk.write((String) null);

    pk.write("param3");
    pk.writeNil();

    pk.writeMapEnd();

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    Unpacker upk = messagePack.createUnpacker(bis);

    return upk;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#writeTo(org.msgpack.packer.Packer)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testWriteTo() throws Exception {

    /*
     * setting
     */
    Packer pk = Mockito.mock(Packer.class);

    doReturn(pk).when(pk).writeMapBegin(3);
    doReturn(pk).when(pk).write(anyString());
    doReturn(pk).when(pk).writeMapEnd();

    /*
     * test
     */
    target.writeTo(pk);

    /*
     * check
     */
    verify(pk).writeMapBegin(3);
    verify(pk, times(6)).write(anyString());
    verify(pk).writeMapEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#writeTo(org.msgpack.packer.Packer)}.
   * @throws Exception throws Exception in targets
   */
  @Test(expected = IOException.class)
  public void testWriteTo_IoException() throws Exception {

    /*
     * setting
     */
    Packer pk = Mockito.mock(Packer.class);

    doThrow(IOException.class).when(pk).writeMapBegin(anyInt());
    doThrow(IOException.class).when(pk).write(anyString());
    doThrow(IOException.class).when(pk).writeMapEnd();

    /*
     * test
     */
    target.writeTo(pk);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#writeTo(org.msgpack.packer.Packer)}.
   * @throws Exception throws Exception in targets
   */
  @Test(expected = IllegalArgumentException.class)
  public void testWriteTo_Null() throws Exception {

    /*
     * test
     */
    target.writeTo(null);
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#doWriteTo(org.msgpack.packer.Packer)}.
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDoWriteTo() throws Exception {

    /*
     * setting
     */
    Packer pk = Mockito.mock(Packer.class);

    doReturn(pk).when(pk).write(anyString());

    /*
     * test
     */
    target.doWriteTo(pk);

    /*
     * check
     */
    verify(pk, times(6)).write(anyString());

    InOrder inOrder = Mockito.inOrder(pk);
    inOrder.verify(pk).write("id");
    inOrder.verify(pk).write("SetId");

    inOrder.verify(pk).write("type");
    inOrder.verify(pk).write("SetType");

    inOrder.verify(pk).write("connection");
    inOrder.verify(pk).write("SetConnection");

    inOrder.verifyNoMoreInteractions();
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceCondition#toString()}.
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
    assertThat(
        result.endsWith("[id=SetId,type=SetType,connection=SetConnection]"),
        is(true));
  }

}
