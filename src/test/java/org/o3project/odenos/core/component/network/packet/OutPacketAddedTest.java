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
 * Test class for OutPacketAdded.
 *
 */
public class OutPacketAddedTest {

  private OutPacketAdded target;

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
    target = Mockito.spy(new OutPacketAdded());
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketAdded#OutPacketAdded()}
   * .
   */
  @SuppressWarnings("static-access")
  @Test
  public void testOutPacketAdded() {

    /*
     * test
     */

    target = new OutPacketAdded();

    /*
     * check
     */
    assertThat(target.TYPE, is("OutPacketAdded"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketAdded#OutPacketAdded(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testOutPacketAddedPacket() {

    /*
     * set
     */
    Packet value = new OutPacket();

    /*
     * test
     */

    OutPacketAdded result = new OutPacketAdded(value);

    /*
     * check
     */
    assertThat(result.getId(), is(nullValue()));

  }

}
