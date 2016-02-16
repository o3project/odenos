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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for InPacketAdded.
 *
 */
public class InPacketAddedTest {

  private InPacketAdded target;

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
    target = Mockito.spy(new InPacketAdded());
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
   * {@link org.o3project.odenos.core.component.network.packet.InPacketAdded#InPacketAdded()}
   * .
   */
  @SuppressWarnings("static-access")
  @Test
  public void testInPacketAdded() {

    /*
     * test
     */
    target = new InPacketAdded();

    /*
     * check
     */
    assertThat(target.TYPE, is("InPacketAdded"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.InPacketAdded#InPacketAdded(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testInPacketAddedPacket() {

    /*
     * test
     */
    Packet result = new InPacket();

    /*
     * check
     */
    assertThat(result.type, is(nullValue()));
    assertThat(result.packetId, is(nullValue()));
  }

}
