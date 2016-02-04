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

package org.o3project.odenos.component.federator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for BoundaryPort.
 */
public class BoundaryPortTest {

  private BoundaryPort target;

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
    target = Mockito.spy(new BoundaryPort("NetworkId", "NodeId", "PortId"));
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#BoundaryPort(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testBoundaryPort() {

    /*
     * test
     */
    BoundaryPort result = new BoundaryPort("NewNetworkId", "NewNodeId",
        "NewPortId");

    /*
     * check
     */
    assertThat(result.getNetworkId(), is("NewNetworkId"));
    assertThat(result.getNodeId(), is("NewNodeId"));
    assertThat(result.getPortId(), is("NewPortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#getNetworkId()}.
   */
  @Test
  public void testGetNetworkId() {

    /*
     * test
     */
    String result = target.getNetworkId();

    /*
     * check
     */
    assertThat(result, is("NetworkId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#getNodeId()}.
   */
  @Test
  public void testGetNodeId() {

    /*
     * test
     */
    String result = target.getNodeId();

    /*
     * check
     */
    assertThat(result, is("NodeId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#getPortId()}.
   */
  @Test
  public void testGetPortId() {

    /*
     * test
     */
    String result = target.getPortId();

    /*
     * check
     */
    assertThat(result, is("PortId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#hashCode()}.
   */
  @Test
  public void testHashCode() {

    /*
     * setting
     */
    BoundaryPort target11 = new BoundaryPort("NetworkId1", "NodeId1",
        "PortId1");
    BoundaryPort target12 = new BoundaryPort("NetworkId1", "NodeId1",
        "PortId1");

    BoundaryPort target21 = new BoundaryPort("NetworkId2", "NodeId2",
        "PortId2");
    BoundaryPort target22 = new BoundaryPort(target21.getNetworkId(),
        target21.getNodeId(), target21.getPortId());

    /*
     * test
     */
    int result11 = target11.hashCode();
    int result12 = target12.hashCode();

    int result21 = target21.hashCode();
    int result22 = target22.hashCode();

    /*
     * check
     */
    assertThat(result12, is(result11));
    assertThat(result22, is(result21));

    assertThat(result12, not(result21));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {

    /*
     * setting
     */
    BoundaryPort target11 = new BoundaryPort("NetworkId1", "NodeId1",
        "PortId1");
    BoundaryPort target12 = new BoundaryPort("NetworkId1", "NodeId1",
        "PortId1");

    BoundaryPort target21 = new BoundaryPort("NetworkId2", "NodeId2",
        "PortId2");
    BoundaryPort target22 = new BoundaryPort(target21.getNetworkId(),
        target21.getNodeId(), target21.getPortId());

    /*
     * test
     */
    boolean result1 = target11.equals(target12);
    boolean result2 = target21.equals(target22);

    boolean result12 = target11.equals(target21);

    /*
     * check
     */
    assertThat(result1, is(true));
    assertThat(result2, is(true));

    assertThat(result12, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.BoundaryPort#toString()}.
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
        result.endsWith("[networkId=NetworkId,nodeId=NodeId,portId=PortId]"),
        is(true));

  }

}
