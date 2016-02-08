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
import org.o3project.odenos.core.component.network.topology.LinkChanged.Action;
import org.powermock.reflect.Whitebox;

/**
 * Test class for LinkChanged.
 */
public class LinkChangedTest {

  private LinkChanged target;
  private Link prev;
  private Link curr;

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
    prev =
        Mockito.spy(new Link("201", "link1_id", "node1_id", "port1_id", "node2_id",
            "port2_id", null));
    curr =
        Mockito.spy(new Link("202", "link2_id", "node3_id", "port3_id", "node4_id",
            "port4_id", null));
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
   * {@link org.o3project.odenos.core.component.network.topology.LinkChanged#LinkChanged()}
   * .
   */
  @Test
  public void testLinkChanged() {
    target = Mockito.spy(new LinkChanged());

    Link prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Link curr = Whitebox.getInternalState(target, "curr");
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
   * {@link org.o3project.odenos.core.component.network.topology.LinkChanged#LinkChanged(org.o3project.odenos.core.component.network.topology.Link, org.o3project.odenos.core.component.network.topology.Link, Action)}
   * .
   */
  @Test
  public void testLinkChangedWithNotNullParams() {
    target = Mockito.spy(new LinkChanged(prev, curr, Action.add));

    Link prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Link curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("link2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.LinkChanged#LinkChanged(org.o3project.odenos.core.component.network.topology.Link, org.o3project.odenos.core.component.network.topology.Link, Action)}
   * .
   */
  @Test
  public void testLinkChangedWithNullPrevParams() {
    target = Mockito.spy(new LinkChanged(null, curr, Action.add));

    Link prev = Whitebox.getInternalState(target, "prev");
    assertNull(prev);

    Link curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("link2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertThat(version, is("202"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.LinkChanged#LinkChanged(org.o3project.odenos.core.component.network.topology.Link, org.o3project.odenos.core.component.network.topology.Link, Action)}
   * .
   */
  @Test
  public void testLinkChangedWithNullCurrParams() {
    target = Mockito.spy(new LinkChanged(prev, null, Action.add));

    Link prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Link curr = Whitebox.getInternalState(target, "curr");
    assertNull(curr);

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("link1_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.add.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.LinkChanged#LinkChanged(org.o3project.odenos.core.component.network.topology.Link, org.o3project.odenos.core.component.network.topology.Link, Action)}
   * .
   */
  @Test
  public void testLinkChangedWithDeleteAction() {
    target = Mockito.spy(new LinkChanged(prev, curr, Action.delete));

    Link prev = Whitebox.getInternalState(target, "prev");
    assertThat(prev, is(notNullValue()));

    Link curr = Whitebox.getInternalState(target, "curr");
    assertThat(curr, is(notNullValue()));

    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("link2_id"));

    String action = Whitebox.getInternalState(target, "action");
    assertThat(action, is(Action.delete.toString()));

    String version = Whitebox.getInternalState(target, "version");
    assertNull(version);
  }
}
