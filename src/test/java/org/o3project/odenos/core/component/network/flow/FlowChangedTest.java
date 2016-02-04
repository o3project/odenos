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

package org.o3project.odenos.core.component.network.flow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.FlowChanged.Action;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test class for FlowChanged.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FlowChanged.class })
@PowerMockIgnore({"javax.management.*"})
public class FlowChangedTest {

  private BasicFlow paramPrev;
  private BasicFlow paramCurr;

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
    paramPrev = Mockito.spy(new BasicFlow());
    paramCurr = Mockito.spy(new BasicFlow());

    doReturn("prevId").when(paramPrev).getFlowId();
    // when(paramPrev.getFlowId()).thenReturn("");
    doReturn("currId").when(paramCurr).getFlowId();
    doReturn("currVersion").when(paramCurr).getVersion();

  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    paramPrev = null;
    paramCurr = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged()}
   * .
   */
  @Test
  public void testFlowChanged() {

    FlowChanged target = new FlowChanged();

    assertThat(target.id, is(nullValue()));
    assertThat(target.version, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction() {

    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);

    assertThat(target.id, is("currId"));
    assertThat(target.version, is("currVersion"));

    verify(paramPrev, times(0)).getFlowId();
    verify(paramPrev, times(0)).getVersion();
    verify(paramCurr, times(1)).getFlowId();
    verify(paramCurr, times(1)).getVersion();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_actionDelete() {

    FlowChanged target = new FlowChanged(paramPrev, paramCurr,
        Action.delete);

    assertThat(target.id, is("currId"));
    assertThat(target.version, is(nullValue()));

    verify(paramPrev, times(0)).getFlowId();
    verify(paramPrev, times(0)).getVersion();
    verify(paramCurr, times(1)).getFlowId();
    verify(paramCurr, times(1)).getVersion();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_currNull() {

    FlowChanged target = new FlowChanged(paramPrev, null, Action.add);

    verify(paramPrev, times(1)).getFlowId();
    verify(paramPrev, times(0)).getVersion();

    assertThat(target.id, is("prevId"));
    assertThat(target.version, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_currNull_actionDelete() {

    FlowChanged target = new FlowChanged(paramPrev, null, Action.delete);

    verify(paramPrev, times(1)).getFlowId();
    verify(paramPrev, times(0)).getVersion();

    assertThat(target.id, is("prevId"));
    assertThat(target.version, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_prevNull() {

    FlowChanged target = new FlowChanged(null, paramCurr, Action.add);

    verify(paramCurr, times(1)).getFlowId();
    verify(paramCurr, times(1)).getVersion();

    assertThat(target.id, is("currId"));
    assertThat(target.version, is("currVersion"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_prevNull_actionDelete() {

    FlowChanged target = new FlowChanged(null, paramCurr, Action.delete);

    verify(paramCurr, times(1)).getFlowId();
    verify(paramCurr, times(1)).getVersion();

    assertThat(target.id, is("currId"));
    assertThat(target.version, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_prevNull_currNull() {

    FlowChanged target = new FlowChanged(null, null, Action.add);

    assertThat(target.id, is(nullValue()));
    assertThat(target.version, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_prevNull_currNull_actionDelete() {

    FlowChanged target = new FlowChanged(null, null, Action.delete);

    assertThat(target.id, is(nullValue()));
    assertThat(target.version, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#FlowChanged(org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowObject.FlowMessage, org.o3project.odenos.core.component.network.flow.FlowChanged.Action)}
   * .
   */
  @Test
  public void testFlowChangedFlowMessageFlowMessageAction_AllNull() {

    FlowChanged target = new FlowChanged(null, null, null);

    assertThat(target.id, is(nullValue()));
    assertThat(target.version, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("id");
      pk.write("TestId");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write(target.curr);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new FlowChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.id, is("TestId"));
    assertNull(target.version);
    assertThat(target.action, is("update"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("id");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write(target.curr);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new FlowChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.id, is("TestId"));
    assertThat(target.version, is("VERSION"));
    assertThat(target.action, is("update"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutPrev() {
    FlowChanged target = new FlowChanged(null, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("id");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write((String) null);
      pk.write("curr");
      pk.write(target.curr);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }

    target = Mockito.spy(new FlowChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.id, is("TestId"));
    assertThat(target.version, is("VERSION"));
    assertThat(target.action, is("update"));
    assertNull(target.prev);
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutCurr() {
    FlowChanged target = new FlowChanged(paramPrev, null, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("id");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write((String) null);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    target = Mockito.spy(new FlowChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.id, is("TestId"));
    assertThat(target.version, is("VERSION"));
    assertThat(target.action, is("update"));
    assertThat(target.prev, is(notNullValue()));
    assertNull(target.curr);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithMessageNum3() {
    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("id");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write((String) null);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    target = Mockito.spy(new FlowChanged());
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
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithMessageNum6() {
    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(6);
      pk.write("id");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write((String) null);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    target = Mockito.spy(new FlowChanged());
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
   * {@link org.o3project.odenos.core.component.network.flow.FlowChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithCoveredParameters() {
    FlowChanged target = new FlowChanged(paramPrev, paramCurr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("idd");
      pk.write("TestId");
      pk.write("version");
      pk.write("VERSION");
      pk.write("action");
      pk.write("update");
      pk.write("prev");
      pk.write(target.prev);
      pk.write("curr");
      pk.write((String) null);

      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    target = Mockito.spy(new FlowChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }
}
