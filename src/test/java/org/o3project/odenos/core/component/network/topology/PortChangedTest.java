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
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.o3project.odenos.core.component.network.topology.PortChanged;
import org.o3project.odenos.core.component.network.topology.PortChanged.Action;

/**
 * Test class for PortChanged.
 */
public class PortChangedTest {

  private PortChanged target;
  private Port prev;
  private Port curr;

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
    prev = Mockito.spy(new Port("201", "port1_id", "node1_id", "out_link1",
        "in_link1", null));
    curr = Mockito.spy(new Port("202", "port2_id", "node2_id", "out_link2",
        "in_link2", null));
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
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#PortChanged()}
   * .
   */
  @Test
  public void testPortChanged() {
    target = Mockito.spy(new PortChanged());

    Port prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Port curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String nodeId = Whitebox.getInternalState(target, "nodeId");
    assertNull(nodeId);

    String id = Whitebox.getInternalState(target, "id");
    assertNull(id);

    String action = Whitebox.getInternalState(target, "action");
    assertNull(action);

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#PortChanged(org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, Action)}
   * .
   */
  @Test
  public void testPortChangedWithNotNullParams() {
    target = Mockito.spy(new PortChanged(prev, curr, Action.add));

    Port prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Port curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("port1_id"));

    String nodeId = Whitebox.getInternalState(target, "nodeId");
    assertThat(nodeId, is("node1_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#PortChanged(org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, Action)}
   * .
   */
  @Test
  public void testPortChangedWithNullPrevParams() {
    target = Mockito.spy(new PortChanged(null, curr, Action.add));

    Port prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Port curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String nodeId = Whitebox.getInternalState(target, "nodeId");
    assertThat(nodeId, is("node2_id"));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("port2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#PortChanged(org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, Action)}
   * .
   */
  @Test
  public void testPortChangedWithNullCurrParams() {
    target = Mockito.spy(new PortChanged(prev, null, Action.add));

    Port prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Port curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String nodeId = Whitebox.getInternalState(target, "nodeId");
    assertThat(nodeId, is("node1_id"));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("port1_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#PortChanged(org.o3project.odenos.core.component.network.topology.Port, org.o3project.odenos.core.component.network.topology.Port, Action)}
   * .
   */
  @Test
  public void testPortChangedWithDeleteAction() {
    target = Mockito.spy(new PortChanged(prev, curr, Action.delete));

    Port prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Port curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("port1_id"));

    String nodeId = Whitebox.getInternalState(target, "nodeId");
    assertThat(nodeId, is("node1_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.delete.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    target = new PortChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(5);
      pk.write("node_id");
      pk.write(target.nodeId);
      pk.write("id");
      pk.write(target.id);
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

    target = Mockito.spy(new PortChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.nodeId, is("node1_id"));
    assertThat(target.id, is("port1_id"));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
    assertThat(target.prev.getId(), is("port1_id"));
    assertThat(target.curr.getId(), is("port2_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    target = new PortChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(6);
      pk.write("node_id");
      pk.write(target.nodeId);
      pk.write("version");
      pk.write(target.version);
      pk.write("id");
      pk.write(target.id);
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

    target = Mockito.spy(new PortChanged());
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target, is(notNullValue()));
    assertThat(target.nodeId, is("node1_id"));
    assertThat(target.id, is("port1_id"));
    assertThat(target.version, is("202"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
    assertThat(target.prev.getId(), is("port1_id"));
    assertThat(target.curr.getId(), is("port2_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum4() {
    target = new PortChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("nodeId");
      pk.write(target.nodeId);
      pk.write("version");
      pk.write(target.version);
      pk.write("id");
      pk.write(target.id);
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

    target = Mockito.spy(new PortChanged());
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
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum7() {
    target = new PortChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("nodeId");
      pk.write(target.nodeId);
      pk.write("version");
      pk.write(target.version);
      pk.write("id");
      pk.write(target.id);
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

    target = Mockito.spy(new PortChanged());
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
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteTo() {
    target = new PortChanged(prev, curr, Action.update);
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new PortChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.nodeId, is("node1_id"));
    assertThat(target.id, is("port1_id"));
    assertThat(target.version, is("202"));
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
    assertThat(target.prev.getId(), is("port1_id"));
    assertThat(target.curr.getId(), is("port2_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithoutVersion() {
    Mockito.when(prev.getVersion()).thenReturn(null);
    Mockito.when(curr.getVersion()).thenReturn(null);
    target = spy(new PortChanged(prev, curr, Action.update));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new PortChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.nodeId, is("node1_id"));
    assertThat(target.id, is("port1_id"));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertThat(target.curr, is(notNullValue()));
    assertThat(target.prev.getId(), is("port1_id"));
    assertThat(target.curr.getId(), is("port2_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.PortChanged#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithDeleteAction() {
    target = spy(new PortChanged(prev, null, Action.delete));
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    target = new PortChanged();

    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }

    assertThat(target, is(notNullValue()));
    assertThat(target.nodeId, is("node1_id"));
    assertThat(target.id, is("port1_id"));
    assertNull(target.version);
    assertThat(target.prev, is(notNullValue()));
    assertNull(target.curr);
    assertThat(target.prev.getId(), is("port1_id"));
  }
}
