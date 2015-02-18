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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.core.component.network.topology.NodeChanged.Action;
import org.powermock.reflect.Whitebox;

/**
 * Test class for NodeChanged.
 */
public class NodeChangedTest {

  private NodeChanged target;
  private Node prev;
  private Node curr;

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
    prev = Mockito.spy(new Node("201", "node1_id"));
    curr = Mockito.spy(new Node("202", "node2_id"));
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
   * {@link org.o3project.odenos.core.component.network.topology.NodeChanged#NodeChanged()}
   * .
   */
  @Test
  public void testNodeChanged() {
    target = Mockito.spy(new NodeChanged());

    Node prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Node curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String id = Whitebox.getInternalState(target, "id");
    assertNull(id);

    String action = Whitebox.getInternalState(target, "action");
    assertNull(action);

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.NodeChanged#NodeChanged(org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testNodeChangedWithNotNullParams() {
    target = Mockito.spy(new NodeChanged(prev, curr, Action.add));

    Node prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Node curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("node2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.NodeChanged#NodeChanged(org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testNodeChangedWithNullPrevParams() {
    target = Mockito.spy(new NodeChanged(null, curr, Action.add));

    Node prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Node curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("node2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.NodeChanged#NodeChanged(org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testNodeChangedWithNullCurrParams() {
    target = Mockito.spy(new NodeChanged(prev, null, Action.add));

    Node prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Node curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("node1_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.NodeChanged#NodeChanged(org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.core.component.network.topology.Node, org.o3project.odenos.component.network.topology.Action)}
   * .
   */
  @Test
  public void testNodeChangedWithDeleteAction() {
    target = Mockito.spy(new NodeChanged(prev, curr, Action.delete));

    Node prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Node curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is(notNullValue()));
    assertThat(id, is("node2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.delete.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }
}
