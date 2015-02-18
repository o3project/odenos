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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 */
public class BaseObjectChangedTest {

  private class TestClass extends BaseObjectChanged<String> {
    public TestClass(Class<String> clazz) {
      super(clazz);
    }

    public TestClass(String action, String prev, String curr) {
      super(action, prev, curr);
    }
  }

  private BaseObjectChanged<String> target;
  private String prev;
  private String curr;
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
    prev = new String("prev");
    curr = new String("curr");
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
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#BaseObjectChanged(java.lang.Class)}
   * .
   */
  @Test
  public void testBaseObjectChangedWithClass() {
    target = new TestClass(String.class);
    String prev = Whitebox.getInternalState(target, "prev");
    String curr = Whitebox.getInternalState(target, "curr");
    String action = Whitebox.getInternalState(target, "action");

    assertNull(prev);
    assertNull(curr);
    assertNull(action);

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertThat(clazz.getName(), is("java.lang.String"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#BaseObjectChanged(java.lang.Class)}
   * .
   */
  @Test
  public void testBaseObjectChangedWithParams() {
    target = new TestClass(action, prev, curr);
    String strPrev = Whitebox.getInternalState(target, "prev");
    String strCurr = Whitebox.getInternalState(target, "curr");
    String strAction = Whitebox.getInternalState(target, "action");

    assertThat(strPrev, is("prev"));
    assertThat(strCurr, is("curr"));
    assertThat(strAction, is("action"));

    Class<?> clazz = Whitebox.getInternalState(target, "msgClass");
    assertNull(clazz);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#action()}.
   */
  @Test
  public void testAction() {
    target = new TestClass(action, prev, curr);
    String strAction = target.action();

    assertThat(strAction, is("action"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#prev()}.
   */
  @Test
  public void testPrev() {
    target = new TestClass(action, prev, curr);
    String strPrev = target.prev();

    assertThat(strPrev, is("prev"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#curr()}.
   */
  @Test
  public void testCurr() {
    target = new TestClass(action, prev, curr);
    String strCurr = target.curr();

    assertThat(strCurr, is("curr"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFrom() {
    target = new TestClass(action, prev, curr);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("action");
      pk.write(target.action());
      pk.write("prev");
      pk.write(target.prev());
      pk.write("curr");
      pk.write(target.curr());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = new TestClass(String.class);
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.action(), is("action"));
    assertThat(target.prev(), is("prev"));
    assertThat(target.curr(), is("curr"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMap() {
    target = new TestClass(action, prev, curr);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(2);
      pk.write("action");
      pk.write(target.action());
      pk.write("prev");
      pk.write(target.prev());
      pk.write("curr");
      pk.write(target.curr());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = new TestClass(String.class);
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithNull() {
    target = new TestClass(action, null, null);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("action");
      pk.write(target.action());
      pk.write("prev");
      pk.write(target.prev());
      pk.write("curr");
      pk.write(target.curr());

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = new TestClass(String.class);
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.action(), is("action"));
    assertNull(target.prev());
    assertNull(target.curr());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteTo() {
    target = new TestClass(action, prev, curr);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    byte[] bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new TestClass(String.class);

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.prev(), is("prev"));
    assertThat(target.curr(), is("curr"));
    assertThat(target.action(), is("action"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.event.BaseObjectChanged#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteToWithNull() {
    target = new TestClass(action, null, null);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    byte[] bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new TestClass(String.class);

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertNull(target.prev());
    assertNull(target.curr());
    assertThat(target.action(), is("action"));
  }
}
