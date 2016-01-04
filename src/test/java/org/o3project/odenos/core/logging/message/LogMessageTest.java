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

package org.o3project.odenos.core.logging.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.o3project.odenos.core.logging.message.LogMessage;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.Random;
import java.net.NetworkInterface;

import org.apache.logging.log4j.ThreadContext;

/**
 * Test class for Component.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ LogMessage.class, NetworkInterface.class, Random.class })
public class LogMessageTest {
  private int txoffset = 0;
  private LogMessage target;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    target = PowerMockito.spy(new LogMessage());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#createTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testCreateTxid() throws Exception {
    byte[] data = new byte[]{(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF};

    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(nic);
    PowerMockito.when(nic.getHardwareAddress()).thenReturn(data);

    /*
     * test
     */
    target.initParameters(txoffset);
    String result = target.createTxid();

    /*
     * check
     */
    assertThat(result, is("aabbccddeeff-0000001"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#createTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testCreateTxidNotUUIDRandomPositive() throws Exception {
    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(null);

    long l = 0x0000123456789ABCL;
    Random rnd0 = PowerMockito.mock(Random.class);
    PowerMockito.when(rnd0.nextLong()).thenReturn(l);

    Whitebox.setInternalState(LogMessage.class, "rnd", rnd0);

    /*
     * test
     */
    target.initParameters(txoffset);
    String result = target.createTxid();

    /*
     * check
     */
    assertThat(result, is("123456789abc-0000001"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#createTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testCreateTxidNotUUIDRandomNegative() throws Exception {
    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(null);

    long l = 0xFFFF123456789ABCL;
    Random rnd0 = PowerMockito.mock(Random.class);
    PowerMockito.when(rnd0.nextLong()).thenReturn(l);

    Whitebox.setInternalState(LogMessage.class, "rnd", rnd0);

    /*
     * test
     */
    target.initParameters(txoffset);
    String result = target.createTxid();

    /*
     * check
     */
    assertThat(result, is("123456789abc-0000001"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#getSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetSavedTxid() throws Exception {
    ThreadContext.put("txid", "aabbccddeeff-0000001");

    /*
     * test
     */
    String result = target.getSavedTxid();

    /*
     * check
     */
    assertThat(result, is("aabbccddeeff-0000001"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#getSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetSavedTxidNotSet() throws Exception {
    /*
     * test
     */
    String result = target.getSavedTxid();

    /*
     * check
     */
    assertThat(result, is("-"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#setSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSetSavedTxid() throws Exception {
    /*
     * test
     */
    target.setSavedTxid("123456789abc-0000001");
    String result = ThreadContext.get("txid");

    /*
     * check
     */
    assertThat(result, is("123456789abc-0000001"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#setSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSetSavedTxidNull() throws Exception {
    byte[] data = new byte[]{(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF};

    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(nic);
    PowerMockito.when(nic.getHardwareAddress()).thenReturn(data);

    /*
     * test
     */
    target.initParameters(txoffset);
    target.setSavedTxid(null);
    String result = ThreadContext.get("txid");

    /*
     * check
     */
    assertThat(result, is("aabbccddeeff-0000001"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#setSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSetSavedTxidEmpty() throws Exception {
    byte[] data = new byte[]{(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF};

    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(nic);
    PowerMockito.when(nic.getHardwareAddress()).thenReturn(data);

    /*
     * test
     */
    target.initParameters(txoffset);
    target.setSavedTxid("");
    String result = ThreadContext.get("txid");

    /*
     * check
     */
    assertThat(result, is("aabbccddeeff-0000001"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#setSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSetSavedTxidMinus() throws Exception {
    byte[] data = new byte[]{(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF};

    PowerMockito.mockStatic(NetworkInterface.class);
    NetworkInterface nic = PowerMockito.mock(NetworkInterface.class);

    PowerMockito.when(NetworkInterface.getByName(anyString())).thenReturn(nic);
    PowerMockito.when(nic.getHardwareAddress()).thenReturn(data);

    /*
     * test
     */
    target.initParameters(txoffset);
    target.setSavedTxid("-");
    String result = ThreadContext.get("txid");

    /*
     * check
     */
    assertThat(result, is("aabbccddeeff-0000001"));
    ThreadContext.clearMap();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.logging.message.LogMessage#delSavedTxid()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testdelSavedTxid() throws Exception {
    /*
     * test
     */
    target.setSavedTxid("123456789abc-0000001");
    String result1 = target.getSavedTxid();
    assertThat(result1, is("123456789abc-0000001"));
    target.delSavedTxid();
    String result2 = target.getSavedTxid();
    assertThat(result2, is("-"));
  }

}
