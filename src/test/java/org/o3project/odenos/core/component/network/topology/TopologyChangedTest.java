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

package org.o3project.odenos.core.component.network.topology;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.core.component.network.topology.TopologyChanged.Action;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test class for TopologyChanged.
 */
public class TopologyChangedTest {

  private TopologyChanged target;
  private Topology prev;
  private Topology curr;

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
    prev = Mockito.spy(new Topology("201", null, null));
    curr = Mockito.spy(new Topology("202", null, null));
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
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#TopologyChanged()}
   * .
   */
  @Test
  public void testTopologyChanged() {
    target = Mockito.spy(new TopologyChanged());

    Topology prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Topology curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String action = Whitebox.getInternalState(target, "action");
    assertNull(action);

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#TopologyChanged(org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testTopologyChangedWithNotNullParams() {
    target = Mockito.spy(new TopologyChanged(prev, curr, Action.add));

    Topology prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Topology curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#TopologyChanged(org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testTopologyChangedWithNullPrevParams() {
    target = Mockito.spy(new TopologyChanged(null, curr, Action.add));

    Topology prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Topology curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#TopologyChanged(org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testTopologyChangedWithNullCurrParams() {
    target = Mockito.spy(new TopologyChanged(prev, null, Action.add));

    Topology prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Topology curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#TopologyChanged(org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.core.component.network.topology.Topology, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testTopologyChangedWithDeleteAction() {
    target = Mockito.spy(new TopologyChanged(prev, curr, Action.delete));

    Topology prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Topology curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.delete.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    target = new TopologyChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    target = new TopologyChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("version");
      pk.write(target.version);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.version, is("202"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutPrev() {
    target = new TopologyChanged(null, curr, Action.add);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("version");
      pk.write(target.version);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.version, is("202"));
    assertNull(target.prev);
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithoutCurr() {
    target = new TopologyChanged(prev, null, Action.delete);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(3);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertNull(target.curr);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum2() {
    target = new TopologyChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(2);
      pk.write("version");
      pk.write(target.version);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
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
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#readFrom(org.msgpack.packer.Packer)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum7() {
    target = new TopologyChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("version");
      pk.write(target.version);
      pk.write("action");
      pk.write(target.action);
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

    target = Mockito.spy(new TopologyChanged());
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
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteTo() {
    target = new TopologyChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new TopologyChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.version, is("202"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteToWithoutVersion() {
    target = spy(new TopologyChanged(prev, null, Action.delete));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new TopologyChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      //e.printStackTrace();
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.TopologyChanged#writeTo(org.msgpack.unpacker.Unpacker)}
   * .
   */
  @Test
  public void testWriteToWithDeleteAction() {
    target = spy(new TopologyChanged(prev, null, Action.delete));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new TopologyChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertNull(target.curr);
  }
}
