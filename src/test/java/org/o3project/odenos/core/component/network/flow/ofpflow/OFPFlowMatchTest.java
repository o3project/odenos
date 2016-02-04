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

package org.o3project.odenos.core.component.network.flow.ofpflow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for OFPFlowMatch.
 *
 */
public class OFPFlowMatchTest {

  private OFPFlowMatch target;

  private static final String MAX_UINT64 = new BigInteger("ffffffffffffffff", 16).toString();
  private static final String MIN_UINT64 = new BigInteger("0000000000000000", 16).toString();

  private static final long MAX_UINT32 = Long.decode("0xffffffff");
  private static final long MIN_UINT32 = 0L;

  private static final int MAX_UINT16 = Integer.decode("0xffff");
  private static final int MIN_UINT16 = 0;

  private static final int MAX_UINT8 = Integer.decode("0xff");
  private static final int MIN_UINT8 = 0;

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
    target = spy(new OFPFlowMatch("NodeId", "PortId"));
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
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#OFPFlowMatch()}
   * .
   */
  @Test
  public final void testOFPFlowMatch() {

    /*
     * test
     */
    target = new OFPFlowMatch();

    /*
     * check
     */
    assertThat(target.isWcInPhyPort(), is(true));
    assertThat(target.isWcMetadata(), is(true));
    assertThat(target.isWcMetadataMask(), is(true));
    assertThat(target.isWcEthSrc(), is(true));
    assertThat(target.isWcEthSrcMask(), is(true));
    assertThat(target.isWcEthDst(), is(true));
    assertThat(target.isWcEthDstMask(), is(true));
    assertThat(target.isWcVlanVid(), is(true));
    assertThat(target.isWcVlanVidMask(), is(true));
    assertThat(target.isWcVlanPcp(), is(true));
    assertThat(target.isWcEthType(), is(true));
    assertThat(target.isWcIpDscp(), is(true));
    assertThat(target.isWcIpEcn(), is(true));
    assertThat(target.isWcIpProto(), is(true));
    assertThat(target.isWcIpv4Src(), is(true));
    assertThat(target.isWcIpv4SrcMask(), is(true));
    assertThat(target.isWcIpv4Dst(), is(true));
    assertThat(target.isWcIpv4DstMask(), is(true));
    assertThat(target.isWcTcpSrc(), is(true));
    assertThat(target.isWcTcpDst(), is(true));
    assertThat(target.isWcUdpSrc(), is(true));
    assertThat(target.isWcUdpDst(), is(true));
    assertThat(target.isWcSctpSrc(), is(true));
    assertThat(target.isWcSctpDst(), is(true));
    assertThat(target.isWcIcmpv4Type(), is(true));
    assertThat(target.isWcIcmpv4Code(), is(true));
    assertThat(target.isWcArpOp(), is(true));
    assertThat(target.isWcArpSpa(), is(true));
    assertThat(target.isWcArpSpaMask(), is(true));
    assertThat(target.isWcArpTpa(), is(true));
    assertThat(target.isWcArpTpaMask(), is(true));
    assertThat(target.isWcArpSha(), is(true));
    assertThat(target.isWcArpShaMask(), is(true));
    assertThat(target.isWcArpTha(), is(true));
    assertThat(target.isWcArpThaMask(), is(true));
    assertThat(target.isWcIpv6Src(), is(true));
    assertThat(target.isWcIpv6SrcMask(), is(true));
    assertThat(target.isWcIpv6Dst(), is(true));
    assertThat(target.isWcIpv6DstMask(), is(true));
    assertThat(target.isWcIpv6Flabel(), is(true));
    assertThat(target.isWcIpv6FlabelMask(), is(true));
    assertThat(target.isWcIcmpv6Type(), is(true));
    assertThat(target.isWcIcmpv6Code(), is(true));
    assertThat(target.isWcIpv6NdTarget(), is(true));
    assertThat(target.isWcIpv6NdSll(), is(true));
    assertThat(target.isWcIpv6NdTll(), is(true));
    assertThat(target.isWcMplsLabel(), is(true));
    assertThat(target.isWcMplsTc(), is(true));
    assertThat(target.isWcMplsBos(), is(true));
    assertThat(target.isWcPbbIsid(), is(true));
    assertThat(target.isWcPbbIsidMask(), is(true));
    assertThat(target.isWcTunnelId(), is(true));
    assertThat(target.isWcTunnelIdMask(), is(true));
    assertThat(target.isWcIpv6Exthdr(), is(true));
    assertThat(target.isWcIpv6ExthdrMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#OFPFlowMatch(java.lang.String, java.lang.String)}
   */
  @Test
  public void testOFPFlowMatchStringString() {

    /*
     * test
     */
    target = new OFPFlowMatch("NodeId", "PortId");

    /*
     * check
     */
    assertThat(target.inNode, is("NodeId"));
    assertThat(target.inPort, is("PortId"));

    assertThat(target.isWcInPhyPort(), is(true));
    assertThat(target.isWcMetadata(), is(true));
    assertThat(target.isWcMetadataMask(), is(true));
    assertThat(target.isWcEthSrc(), is(true));
    assertThat(target.isWcEthSrcMask(), is(true));
    assertThat(target.isWcEthDst(), is(true));
    assertThat(target.isWcEthDstMask(), is(true));
    assertThat(target.isWcVlanVid(), is(true));
    assertThat(target.isWcVlanVidMask(), is(true));
    assertThat(target.isWcVlanPcp(), is(true));
    assertThat(target.isWcEthType(), is(true));
    assertThat(target.isWcIpDscp(), is(true));
    assertThat(target.isWcIpEcn(), is(true));
    assertThat(target.isWcIpProto(), is(true));
    assertThat(target.isWcIpv4Src(), is(true));
    assertThat(target.isWcIpv4SrcMask(), is(true));
    assertThat(target.isWcIpv4Dst(), is(true));
    assertThat(target.isWcIpv4DstMask(), is(true));
    assertThat(target.isWcTcpSrc(), is(true));
    assertThat(target.isWcTcpDst(), is(true));
    assertThat(target.isWcUdpSrc(), is(true));
    assertThat(target.isWcUdpDst(), is(true));
    assertThat(target.isWcSctpSrc(), is(true));
    assertThat(target.isWcSctpDst(), is(true));
    assertThat(target.isWcIcmpv4Type(), is(true));
    assertThat(target.isWcIcmpv4Code(), is(true));
    assertThat(target.isWcArpOp(), is(true));
    assertThat(target.isWcArpSpa(), is(true));
    assertThat(target.isWcArpSpaMask(), is(true));
    assertThat(target.isWcArpTpa(), is(true));
    assertThat(target.isWcArpTpaMask(), is(true));
    assertThat(target.isWcArpSha(), is(true));
    assertThat(target.isWcArpShaMask(), is(true));
    assertThat(target.isWcArpTha(), is(true));
    assertThat(target.isWcArpThaMask(), is(true));
    assertThat(target.isWcIpv6Src(), is(true));
    assertThat(target.isWcIpv6SrcMask(), is(true));
    assertThat(target.isWcIpv6Dst(), is(true));
    assertThat(target.isWcIpv6DstMask(), is(true));
    assertThat(target.isWcIpv6Flabel(), is(true));
    assertThat(target.isWcIpv6FlabelMask(), is(true));
    assertThat(target.isWcIcmpv6Type(), is(true));
    assertThat(target.isWcIcmpv6Code(), is(true));
    assertThat(target.isWcIpv6NdTarget(), is(true));
    assertThat(target.isWcIpv6NdSll(), is(true));
    assertThat(target.isWcIpv6NdTll(), is(true));
    assertThat(target.isWcMplsLabel(), is(true));
    assertThat(target.isWcMplsTc(), is(true));
    assertThat(target.isWcMplsBos(), is(true));
    assertThat(target.isWcPbbIsid(), is(true));
    assertThat(target.isWcPbbIsidMask(), is(true));
    assertThat(target.isWcTunnelId(), is(true));
    assertThat(target.isWcTunnelIdMask(), is(true));
    assertThat(target.isWcIpv6Exthdr(), is(true));
    assertThat(target.isWcIpv6ExthdrMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getInPhyPort()}
   */
  @Test
  public void testGetInPhyPort() {

    /*
     * test
     */
    Long result1st = target.getInPhyPort();
    target.setInPhyPort(MAX_UINT32);
    Long resultMax = target.getInPhyPort();
    target.setInPhyPort(MIN_UINT32);
    Long resultMin = target.getInPhyPort();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getInPhyPort();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setInPhyPort(java.lang.String)}
   */
  @Test
  public void testSetInPhyPort() {

    /*
     * test
     */
    target.setInPhyPort(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getInPhyPort();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "inPhyPort");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcInPhyPort();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getMetadata()}
   */
  @Test
  public void testGetMetadata() {

    /*
     * test
     */
    String result1st = target.getMetadata();
    target.setMetadata(MAX_UINT64);
    String resultMax = target.getMetadata();
    target.setMetadata(MIN_UINT64);
    String resultMin = target.getMetadata();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT64));
    assertThat(resultMin, is(MIN_UINT64));

    verify(target, times(3)).getMetadata();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setMetadata(java.lang.Long)}
   */
  @Test
  public void testSetMetadata() {

    /*
     * test
     */
    target.setMetadata(MAX_UINT64);

    /*
     * check
     */
    String resultGetter = target.getMetadata();
    assertThat(resultGetter, is(MAX_UINT64));

    String resultVariable = Whitebox.getInternalState(target, "metadata");
    assertThat(resultVariable, is(MAX_UINT64));

    boolean flag = target.isWcMetadata();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getMetadataMask()}
   */
  @Test
  public void testGetMetadataMask() {

    /*
     * test
     */
    String result1st = target.getMetadataMask();
    target.setMetadataMask(MAX_UINT64);
    String resultMax = target.getMetadataMask();
    target.setMetadataMask(MIN_UINT64);
    String resultMin = target.getMetadataMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT64));
    assertThat(resultMin, is(MIN_UINT64));

    verify(target, times(3)).getMetadataMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setMetadataMask(java.lang.Long)}
   */
  @Test
  public void testSetMetadataMask() {

    /*
     * test
     */
    target.setMetadataMask(MAX_UINT64);

    /*
     * check
     */
    String resultGetter = target.getMetadataMask();
    assertThat(resultGetter, is(MAX_UINT64));

    String resultVariable = Whitebox.getInternalState(target, "metadataMask");
    assertThat(resultVariable, is(MAX_UINT64));

    boolean flag = target.isWcMetadataMask();
    assertThat(flag, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getEthSrc()}
   */
  @Test
  public void testGetEthSrc() {

    /*
     * test
     */
    String result1st = target.getEthSrc();
    target.setEthSrc("11:22:33:44:55:66");
    String result2nd = target.getEthSrc();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("11:22:33:44:55:66"));

    verify(target, times(2)).getEthSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setEthSrc(java.lang.String)}
   */
  @Test
  public void testSetEthSrc() {

    /*
     * test
     */
    target.setEthSrc("11:22:33:44:55:66");

    /*
     * check
     */
    String resultGetter = target.getEthSrc();
    assertThat(resultGetter, is("11:22:33:44:55:66"));

    String resultVariable = Whitebox.getInternalState(target, "ethSrc");
    assertThat(resultVariable, is("11:22:33:44:55:66"));

    boolean flag = target.isWcEthSrc();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getEthSrcMask()}
   */
  @Test
  public void testGetEthSrcMask() {

    /*
     * test
     */
    String result1st = target.getEthSrcMask();
    target.setEthSrcMask("ff:ff:ff:ff:ff:ff");
    String result2nd = target.getEthSrcMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("ff:ff:ff:ff:ff:ff"));

    verify(target, times(2)).getEthSrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setEthSrcMask(java.lang.String)}
   */
  @Test
  public void testSetEthSrcMask() {

    /*
     * test
     */
    target.setEthSrcMask("ff:ff:ff:ff:ff:ff");

    /*
     * check
     */
    String resultGetter = target.getEthSrcMask();
    assertThat(resultGetter, is("ff:ff:ff:ff:ff:ff"));

    String resultVariable = Whitebox.getInternalState(target, "ethSrcMask");
    assertThat(resultVariable, is("ff:ff:ff:ff:ff:ff"));

    boolean flag = target.isWcEthSrcMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getEthDst()}
   */
  @Test
  public void testGetEthDst() {

    /*
     * test
     */
    String result1st = target.getEthDst();
    target.setEthDst("11:22:33:44:55:66");
    String result2nd = target.getEthDst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("11:22:33:44:55:66"));

    verify(target, times(2)).getEthDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setEthDst(java.lang.String)}
   */
  @Test
  public void testSetEthDst() {

    /*
     * test
     */
    target.setEthDst("11:22:33:44:55:66");

    /*
     * check
     */
    String resultGetter = target.getEthDst();
    assertThat(resultGetter, is("11:22:33:44:55:66"));

    String resultVariable = Whitebox.getInternalState(target, "ethDst");
    assertThat(resultVariable, is("11:22:33:44:55:66"));

    boolean flag = target.isWcEthDst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getEthDstMask()}
   */
  @Test
  public void testGetEthDstMask() {

    /*
     * test
     */
    String result1st = target.getEthDstMask();
    target.setEthDstMask("11:22:33:44:55:66");
    String result2nd = target.getEthDstMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("11:22:33:44:55:66"));

    verify(target, times(2)).getEthDstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setEthDstMask(java.lang.String)}
   */
  @Test
  public void testSetEthDstMask() {

    /*
     * test
     */
    target.setEthDstMask("11:22:33:44:55:66");

    /*
     * check
     */
    String resultGetter = target.getEthDstMask();
    assertThat(resultGetter, is("11:22:33:44:55:66"));

    String resultVariable = Whitebox.getInternalState(target, "ethDstMask");
    assertThat(resultVariable, is("11:22:33:44:55:66"));

    boolean flag = target.isWcEthDstMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getVlanVid()}
   */
  @Test
  public void testGetVlanVid() {

    /*
     * test
     */
    Integer result1st = target.getVlanVid();
    target.setVlanVid(MAX_UINT16);
    Integer resultMax = target.getVlanVid();
    target.setVlanVid(MIN_UINT16);
    Integer resultMin = target.getVlanVid();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getVlanVid();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setVlanVid(java.lang.Integer)}
   */
  @Test
  public void testSetVlanVid() {

    /*
     * test
     */
    target.setVlanVid(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getVlanVid();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "vlanVid");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcVlanVid();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getVlanVidMask()}
   */
  @Test
  public void testGetVlanVidMask() {

    /*
     * test
     */
    Integer result1st = target.getVlanVidMask();
    target.setVlanVidMask(MAX_UINT16);
    Integer resultMax = target.getVlanVidMask();
    target.setVlanVidMask(MIN_UINT16);
    Integer resultMin = target.getVlanVidMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getVlanVidMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setVlanVidMask(java.lang.Integer)}
   */
  @Test
  public void testSetVlanVidMask() {

    /*
     * test
     */
    target.setVlanVidMask(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getVlanVidMask();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "vlanVidMask");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcVlanVidMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getVlanPcp()}
   */
  @Test
  public void testGetVlanPcp() {

    /*
     * test
     */
    Integer result1st = target.getVlanPcp();
    target.setVlanPcp(MAX_UINT8);
    Integer resultMax = target.getVlanPcp();
    target.setVlanPcp(MIN_UINT8);
    Integer resultMin = target.getVlanPcp();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getVlanPcp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setVlanPcp(java.lang.Integer)}
   */
  @Test
  public void testSetVlanPcp() {

    /*
     * test
     */
    target.setVlanPcp(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getVlanPcp();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "vlanPcp");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcVlanPcp();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getEthType()}
   */
  @Test
  public void testGetEthType() {

    /*
     * test
     */
    Integer result1st = target.getEthType();
    target.setEthType(MAX_UINT16);
    Integer resultMax = target.getEthType();
    target.setEthType(MIN_UINT16);
    Integer resultMin = target.getEthType();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getEthType();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setEthType(java.lang.Integer)}
   */
  @Test
  public void testSetEthType() {

    /*
     * test
     */
    target.setEthType(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getEthType();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "ethType");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcEthType();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpDscp()}
   */
  @Test
  public void testGetIpDscp() {

    /*
     * test
     */
    Integer result1st = target.getIpDscp();
    target.setIpDscp(MAX_UINT16);
    Integer resultMax = target.getIpDscp();
    target.setIpDscp(MIN_UINT16);
    Integer resultMin = target.getIpDscp();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getIpDscp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpDscp(java.lang.Integer)}
   */
  @Test
  public void testSetIpDscp() {

    /*
     * test
     */
    target.setIpDscp(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getIpDscp();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "ipDscp");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcIpDscp();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpEcn()}
   */
  @Test
  public void testGetIpEcn() {

    /*
     * test
     */
    Integer result1st = target.getIpEcn();
    target.setIpEcn(MAX_UINT8);
    Integer resultMax = target.getIpEcn();
    target.setIpEcn(MIN_UINT8);
    Integer resultMin = target.getIpEcn();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIpEcn();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpEcn(java.lang.Integer)}
   */
  @Test
  public void testSetIpEcn() {

    /*
     * test
     */
    target.setIpEcn(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIpEcn();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "ipEcn");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIpEcn();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpProto()}
   */
  @Test
  public void testGetIpProto() {

    /*
     * test
     */
    Integer result1st = target.getIpProto();
    target.setIpProto(MAX_UINT8);
    Integer resultMax = target.getIpProto();
    target.setIpProto(MIN_UINT8);
    Integer resultMin = target.getIpProto();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIpProto();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpProto(java.lang.Integer)}
   */
  @Test
  public void testSetIpProto() {

    /*
     * test
     */
    target.setIpProto(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIpProto();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "ipProto");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIpProto();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv4Src()}
   */
  @Test
  public void testGetIpv4Src() {

    /*
     * test
     */
    String result1st = target.getIpv4Src();
    target.setIpv4Src("127.0.0.1");
    String result2nd = target.getIpv4Src();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getIpv4Src();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv4Src(java.lang.String)}
   */
  @Test
  public void testSetIpv4Src() {

    /*
     * test
     */
    target.setIpv4Src("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getIpv4Src();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "ipv4Src");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcIpv4Src();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv4SrcMask()}
   */
  @Test
  public void testGetIpv4SrcMask() {

    /*
     * test
     */
    String result1st = target.getIpv4SrcMask();
    target.setIpv4SrcMask("255.0.0.0");
    String result2nd = target.getIpv4SrcMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getIpv4SrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv4SrcMask(java.lang.String)}
   */
  @Test
  public void testSetIpv4SrcMask() {

    /*
     * test
     */
    target.setIpv4SrcMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getIpv4SrcMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "ipv4SrcMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcIpv4SrcMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv4Dst()}
   */
  @Test
  public void testGetIpv4Dst() {

    /*
     * test
     */
    String result1st = target.getIpv4Dst();
    target.setIpv4Dst("127.0.0.1");
    String result2nd = target.getIpv4Dst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getIpv4Dst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv4Dst(java.lang.String)}
   */
  @Test
  public void testSetIpv4Dst() {

    /*
     * test
     */
    target.setIpv4Dst("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getIpv4Dst();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "ipv4Dst");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcIpv4Dst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv4DstMask()}
   */
  @Test
  public void testGetIpv4DstMask() {

    /*
     * test
     */
    String result1st = target.getIpv4DstMask();
    target.setIpv4DstMask("255.0.0.0");
    String result2nd = target.getIpv4DstMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getIpv4DstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv4DstMask(java.lang.String)}
   */
  @Test
  public void testSetIpv4DstMask() {

    /*
     * test
     */
    target.setIpv4DstMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getIpv4DstMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "ipv4DstMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcIpv4DstMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getTcpSrc()}
   */
  @Test
  public void testGetTcpSrc() {

    /*
     * test
     */
    Integer result1st = target.getTcpSrc();
    target.setTcpSrc(MAX_UINT16);
    Integer resultMax = target.getTcpSrc();
    target.setTcpSrc(MIN_UINT16);
    Integer resultMin = target.getTcpSrc();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getTcpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setTcpSrc(java.lang.Integer)}
   */
  @Test
  public void testSetTcpSrc() {

    /*
     * test
     */
    target.setTcpSrc(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getTcpSrc();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "tcpSrc");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcTcpSrc();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getTcpDst()}
   */
  @Test
  public void testGetTcpDst() {

    /*
     * test
     */
    Integer result1st = target.getTcpDst();
    target.setTcpDst(MAX_UINT16);
    Integer resultMax = target.getTcpDst();
    target.setTcpDst(MIN_UINT16);
    Integer resultMin = target.getTcpDst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getTcpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setTcpDst(java.lang.Integer)}
   */
  @Test
  public void testSetTcpDst() {

    /*
     * test
     */
    target.setTcpDst(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getTcpDst();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "tcpDst");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcTcpDst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getUdpSrc()}
   */
  @Test
  public void testGetUdpSrc() {

    /*
     * test
     */
    Integer result1st = target.getUdpSrc();
    target.setUdpSrc(MAX_UINT16);
    Integer resultMax = target.getUdpSrc();
    target.setUdpSrc(MIN_UINT16);
    Integer resultMin = target.getUdpSrc();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getUdpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setUdpSrc(java.lang.Integer)}
   */
  @Test
  public void testSetUdpSrc() {

    /*
     * test
     */
    target.setUdpSrc(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getUdpSrc();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "udpSrc");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcUdpSrc();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getUdpDst()}
   */
  @Test
  public void testGetUdpDst() {

    /*
     * test
     */
    Integer result1st = target.getUdpDst();
    target.setUdpDst(MAX_UINT16);
    Integer resultMax = target.getUdpDst();
    target.setUdpDst(MIN_UINT16);
    Integer resultMin = target.getUdpDst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getUdpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setUdpDst(java.lang.Integer)}
   */
  @Test
  public void testSetUdpDst() {

    /*
     * test
     */
    target.setUdpDst(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getUdpDst();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "udpDst");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcUdpDst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getSctpSrc()}
   */
  @Test
  public void testGetSctpSrc() {

    /*
     * test
     */
    Integer result1st = target.getSctpSrc();
    target.setSctpSrc(MAX_UINT16);
    Integer resultMax = target.getSctpSrc();
    target.setSctpSrc(MIN_UINT16);
    Integer resultMin = target.getSctpSrc();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getSctpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setSctpSrc(java.lang.Integer)}
   */
  @Test
  public void testSetSctpSrc() {

    /*
     * test
     */
    target.setSctpSrc(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getSctpSrc();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "sctpSrc");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcSctpSrc();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getSctpDst()}
   */
  @Test
  public void testGetSctpDst() {

    /*
     * test
     */
    Integer result1st = target.getSctpDst();
    target.setSctpDst(MAX_UINT16);
    Integer resultMax = target.getSctpDst();
    target.setSctpDst(MIN_UINT16);
    Integer resultMin = target.getSctpDst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getSctpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setSctpDst(java.lang.Integer)}
   */
  @Test
  public void testSetSctpDst() {

    /*
     * test
     */
    target.setSctpDst(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getSctpDst();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "sctpDst");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcSctpDst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIcmpv4Type()}
   */
  @Test
  public void testGetIcmpv4Type() {

    /*
     * test
     */
    Integer result1st = target.getIcmpv4Type();
    target.setIcmpv4Type(MAX_UINT8);
    Integer resultMax = target.getIcmpv4Type();
    target.setIcmpv4Type(MIN_UINT8);
    Integer resultMin = target.getIcmpv4Type();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIcmpv4Type();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIcmpv4Type(java.lang.Integer)}
   */
  @Test
  public void testSetIcmpv4Type() {

    /*
     * test
     */
    target.setIcmpv4Type(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIcmpv4Type();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "icmpv4Type");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIcmpv4Type();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIcmpv4Code()}
   */
  @Test
  public void testGetIcmpv4Code() {

    /*
     * test
     */
    Integer result1st = target.getIcmpv4Code();
    target.setIcmpv4Code(MAX_UINT8);
    Integer resultMax = target.getIcmpv4Code();
    target.setIcmpv4Code(MIN_UINT8);
    Integer resultMin = target.getIcmpv4Code();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIcmpv4Code();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIcmpv4Code(java.lang.Integer)}
   */
  @Test
  public void testSetIcmpv4Code() {

    /*
     * test
     */
    target.setIcmpv4Code(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIcmpv4Code();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "icmpv4Code");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIcmpv4Code();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpOp()}
   */
  @Test
  public void testGetArpOp() {

    /*
     * test
     */
    target.setArpOp(MAX_UINT16);
    Integer resultMax = target.getArpOp();
    target.setArpOp(MIN_UINT16);
    Integer resultMin = target.getArpOp();

    /*
     * check
     */
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));
    verify(target, times(2)).getArpOp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpOp(java.lang.Integer)}
   */
  @Test
  public void testSetArpOp() {

    /*
     * test
     */
    target.setArpOp(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getArpOp();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "arpOp");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcArpOp();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpSpa()}
   */
  @Test
  public void testGetArpSpa() {

    /*
     * test
     */
    String result1st = target.getArpSpa();
    target.setArpSpa("127.0.0.1");
    String result2nd = target.getArpSpa();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getArpSpa();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpSpa(java.lang.String)}
   */
  @Test
  public void testSetArpSpa() {

    /*
     * test
     */
    target.setArpSpa("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getArpSpa();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "arpSpa");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcArpSpa();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpSpaMask()}
   */
  @Test
  public void testGetArpSpaMask() {

    /*
     * test
     */
    String result1st = target.getArpSpaMask();
    target.setArpSpaMask("255.0.0.0");
    String result2nd = target.getArpSpaMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getArpSpaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpSpaMask(java.lang.String)}
   */
  @Test
  public void testSetArpSpaMask() {

    /*
     * test
     */
    target.setArpSpaMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getArpSpaMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "arpSpaMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcArpSpaMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpTpa()}
   */
  @Test
  public void testGetArpTpa() {

    /*
     * test
     */
    String result1st = target.getArpTpa();
    target.setArpTpa("127.0.0.1");
    String result2nd = target.getArpTpa();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getArpTpa();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpTpa(java.lang.String)}
   */
  @Test
  public void testSetArpTpa() {

    /*
     * test
     */
    target.setArpTpa("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getArpTpa();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "arpTpa");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcArpTpa();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpTpaMask()}
   */
  @Test
  public void testGetArpTpaMask() {

    /*
     * test
     */
    String result1st = target.getArpTpaMask();
    target.setArpTpaMask("255.0.0.0");
    String result2nd = target.getArpTpaMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getArpTpaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpTpaMask(java.lang.String)}
   */
  @Test
  public void testSetArpTpaMask() {

    /*
     * test
     */
    target.setArpTpaMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getArpTpaMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "arpTpaMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcArpTpaMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpSha()}
   */
  @Test
  public void testGetArpSha() {

    /*
     * test
     */
    String result1st = target.getArpSha();
    target.setArpSha("127.0.0.1");
    String result2nd = target.getArpSha();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getArpSha();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpSha(java.lang.String)}
   */
  @Test
  public void testSetArpSha() {

    /*
     * test
     */
    target.setArpSha("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getArpSha();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "arpSha");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcArpSha();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpShaMask()}
   */
  @Test
  public void testGetArpShaMask() {

    /*
     * test
     */
    String result1st = target.getArpShaMask();
    target.setArpShaMask("255.0.0.0");
    String result2nd = target.getArpShaMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getArpShaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpShaMask(java.lang.String)}
   */
  @Test
  public void testSetArpShaMask() {

    /*
     * test
     */
    target.setArpShaMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getArpShaMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "arpShaMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcArpShaMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpTha()}
   */
  @Test
  public void testGetArpTha() {

    /*
     * test
     */
    String result1st = target.getArpTha();
    target.setArpTha("127.0.0.1");
    String result2nd = target.getArpTha();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("127.0.0.1"));

    verify(target, times(2)).getArpTha();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpTha(java.lang.String)}
   */
  @Test
  public void testSetArpTha() {

    /*
     * test
     */
    target.setArpTha("127.0.0.1");

    /*
     * check
     */
    String resultGetter = target.getArpTha();
    assertThat(resultGetter, is("127.0.0.1"));

    String resultVariable = Whitebox.getInternalState(target, "arpTha");
    assertThat(resultVariable, is("127.0.0.1"));

    boolean flag = target.isWcArpTha();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getArpThaMask()}
   */
  @Test
  public void testGetArpThaMask() {

    /*
     * test
     */
    String result1st = target.getArpThaMask();
    target.setArpThaMask("255.0.0.0");
    String result2nd = target.getArpThaMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("255.0.0.0"));

    verify(target, times(2)).getArpThaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setArpThaMask(java.lang.String)}
   */
  @Test
  public void testSetArpThaMask() {

    /*
     * test
     */
    target.setArpThaMask("255.0.0.0");

    /*
     * check
     */
    String resultGetter = target.getArpThaMask();
    assertThat(resultGetter, is("255.0.0.0"));

    String resultVariable = Whitebox.getInternalState(target, "arpThaMask");
    assertThat(resultVariable, is("255.0.0.0"));

    boolean flag = target.isWcArpThaMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6Src()}
   */
  @Test
  public void testGetIpv6Src() {

    /*
     * test
     */
    String result1st = target.getIpv6Src();
    target.setIpv6Src("::1");
    String result2nd = target.getIpv6Src();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("::1"));

    verify(target, times(2)).getIpv6Src();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6Src(java.lang.String)}
   */
  @Test
  public void testSetIpv6Src() {

    /*
     * test
     */
    target.setIpv6Src("::1");

    /*
     * check
     */
    String resultGetter = target.getIpv6Src();
    assertThat(resultGetter, is("::1"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6Src");
    assertThat(resultVariable, is("::1"));

    boolean flag = target.isWcIpv6Src();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6SrcMask()}
   */
  @Test
  public void testGetIpv6SrcMask() {

    /*
     * test
     */
    String result1st = target.getIpv6SrcMask();
    target.setIpv6SrcMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    String result2nd = target.getIpv6SrcMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    verify(target, times(2)).getIpv6SrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6SrcMask(java.lang.String)}
   */
  @Test
  public void testSetIpv6SrcMask() {

    /*
     * test
     */
    target.setIpv6SrcMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");

    /*
     * check
     */
    String resultGetter = target.getIpv6SrcMask();
    assertThat(resultGetter, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6SrcMask");
    assertThat(resultVariable, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    boolean flag = target.isWcIpv6SrcMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6Dst()}
   */
  @Test
  public void testGetIpv6Dst() {

    /*
     * test
     */
    String result1st = target.getIpv6Dst();
    target.setIpv6Dst("::1");
    String result2nd = target.getIpv6Dst();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("::1"));

    verify(target, times(2)).getIpv6Dst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6Dst(java.lang.String)}
   */
  @Test
  public void testSetIpv6Dst() {

    /*
     * test
     */
    target.setIpv6Dst("::1");

    /*
     * check
     */
    String resultGetter = target.getIpv6Dst();
    assertThat(resultGetter, is("::1"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6Dst");
    assertThat(resultVariable, is("::1"));

    boolean flag = target.isWcIpv6Dst();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6DstMask()}
   */
  @Test
  public void testGetIpv6DstMask() {

    /*
     * test
     */
    String result1st = target.getIpv6DstMask();
    target.setIpv6DstMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    String result2nd = target.getIpv6DstMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    verify(target, times(2)).getIpv6DstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6DstMask(java.lang.String)}
   */
  @Test
  public void testSetIpv6DstMask() {

    /*
     * test
     */
    target.setIpv6DstMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");

    /*
     * check
     */
    String resultGetter = target.getIpv6DstMask();
    assertThat(resultGetter, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6DstMask");
    assertThat(resultVariable, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));

    boolean flag = target.isWcIpv6DstMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6Flabel()}
   */
  @Test
  public void testGetIpv6Flabel() {

    /*
     * test
     */
    Long result1st = target.getIpv6Flabel();
    target.setIpv6Flabel(MAX_UINT32);
    Long resultMax = target.getIpv6Flabel();
    target.setIpv6Flabel(MIN_UINT32);
    Long resultMin = target.getIpv6Flabel();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getIpv6Flabel();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6Flabel(java.lang.Integer)}
   */
  @Test
  public void testSetIpv6Flabel() {

    /*
     * test
     */
    target.setIpv6Flabel(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getIpv6Flabel();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "ipv6Flabel");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcIpv6Flabel();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6FlabelMask()}
   */
  @Test
  public void testGetIpv6FlabelMask() {

    /*
     * test
     */
    Long result1st = target.getIpv6FlabelMask();
    target.setIpv6FlabelMask(MAX_UINT32);
    Long resultMax = target.getIpv6FlabelMask();
    target.setIpv6FlabelMask(MIN_UINT32);
    Long resultMin = target.getIpv6FlabelMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getIpv6FlabelMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6FlabelMask(java.lang.Integer)}
   */
  @Test
  public void testSetIpv6FlabelMask() {

    /*
     * test
     */
    target.setIpv6FlabelMask(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getIpv6FlabelMask();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "ipv6FlabelMask");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcIpv6FlabelMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIcmpv6Type()}
   */
  @Test
  public void testGetIcmpv6Type() {

    /*
     * test
     */
    Integer result1st = target.getIcmpv6Type();
    target.setIcmpv6Type(MAX_UINT8);
    Integer resultMax = target.getIcmpv6Type();
    target.setIcmpv6Type(MIN_UINT8);
    Integer resultMin = target.getIcmpv6Type();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIcmpv6Type();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIcmpv6Type(java.lang.Integer)}
   */
  @Test
  public void testSetIcmpv6Type() {

    /*
     * test
     */
    target.setIcmpv6Type(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIcmpv6Type();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "icmpv6Type");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIcmpv6Type();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIcmpv6Code()}
   */
  @Test
  public void testGetIcmpv6Code() {

    /*
     * test
     */
    Integer result1st = target.getIcmpv6Code();
    target.setIcmpv6Code(MAX_UINT8);
    Integer resultMax = target.getIcmpv6Code();
    target.setIcmpv6Code(MIN_UINT8);
    Integer resultMin = target.getIcmpv6Code();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getIcmpv6Code();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIcmpv6Code(java.lang.Integer)}
   */
  @Test
  public void testSetIcmpv6Code() {

    /*
     * test
     */
    target.setIcmpv6Code(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getIcmpv6Code();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "icmpv6Code");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcIcmpv6Code();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6NdTarget()}
   */
  @Test
  public void testGetIpv6NdTarget() {

    /*
     * test
     */
    String result1st = target.getIpv6NdTarget();
    target.setIpv6NdTarget("::1");
    String result2nd = target.getIpv6NdTarget();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("::1"));

    verify(target, times(2)).getIpv6NdTarget();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6NdTarget(java.lang.String)}
   */
  @Test
  public void testSetIpv6NdTarget() {

    /*
     * test
     */
    target.setIpv6NdTarget("::1");

    /*
     * check
     */
    String resultGetter = target.getIpv6NdTarget();
    assertThat(resultGetter, is("::1"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6NdTarget");
    assertThat(resultVariable, is("::1"));

    boolean flag = target.isWcIpv6NdTarget();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6NdSll()}
   */
  @Test
  public void testGetIpv6NdSll() {

    /*
     * test
     */
    String result1st = target.getIpv6NdSll();
    target.setIpv6NdSll("11:22:33:44:55:66");
    String result2nd = target.getIpv6NdSll();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("11:22:33:44:55:66"));

    verify(target, times(2)).getIpv6NdSll();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6NdSll(java.lang.String)}
   */
  @Test
  public void testSetIpv6NdSll() {

    /*
     * test
     */
    target.setIpv6NdSll("11:22:33:44:55:66");

    /*
     * check
     */
    String resultGetter = target.getIpv6NdSll();
    assertThat(resultGetter, is("11:22:33:44:55:66"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6NdSll");
    assertThat(resultVariable, is("11:22:33:44:55:66"));

    boolean flag = target.isWcIpv6NdSll();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6NdTll()}
   */
  @Test
  public void testGetIpv6NdTll() {

    /*
     * test
     */
    String result1st = target.getIpv6NdTll();
    target.setIpv6NdTll("11:22:33:44:55:66");
    String result2nd = target.getIpv6NdTll();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(result2nd, is("11:22:33:44:55:66"));

    verify(target, times(2)).getIpv6NdTll();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6NdTll(java.lang.String)}
   */
  @Test
  public void testSetIpv6NdTll() {

    /*
     * test
     */
    target.setIpv6NdTll("11:22:33:44:55:66");

    /*
     * check
     */
    String resultGetter = target.getIpv6NdTll();
    assertThat(resultGetter, is("11:22:33:44:55:66"));

    String resultVariable = Whitebox.getInternalState(target, "ipv6NdTll");
    assertThat(resultVariable, is("11:22:33:44:55:66"));

    boolean flag = target.isWcIpv6NdTll();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getMplsLabel()}
   */
  @Test
  public void testGetMplsLabel() {

    /*
     * test
     */
    Long result1st = target.getMplsLabel();
    target.setMplsLabel(MAX_UINT32);
    Long resultMax = target.getMplsLabel();
    target.setMplsLabel(MIN_UINT32);
    Long resultMin = target.getMplsLabel();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getMplsLabel();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setMplsLabel(java.lang.Integer)}
   */
  @Test
  public void testSetMplsLabel() {

    /*
     * test
     */
    target.setMplsLabel(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getMplsLabel();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "mplsLabel");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcMplsLabel();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getMplsTc()}
   */
  @Test
  public void testGetMplsTc() {

    /*
     * test
     */
    Integer result1st = target.getMplsTc();
    target.setMplsTc(MAX_UINT8);
    Integer resultMax = target.getMplsTc();
    target.setMplsTc(MIN_UINT8);
    Integer resultMin = target.getMplsTc();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getMplsTc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setMplsTc(java.lang.Integer)}
   */
  @Test
  public void testSetMplsTc() {

    /*
     * test
     */
    target.setMplsTc(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getMplsTc();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "mplsTc");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcMplsTc();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getMplsBos()}
   */
  @Test
  public void testGetMplsBos() {

    /*
     * test
     */
    Integer result1st = target.getMplsBos();
    target.setMplsBos(MAX_UINT8);
    Integer resultMax = target.getMplsBos();
    target.setMplsBos(MIN_UINT8);
    Integer resultMin = target.getMplsBos();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT8));
    assertThat(resultMin, is(MIN_UINT8));

    verify(target, times(3)).getMplsBos();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setMplsBos(java.lang.Integer)}
   */
  @Test
  public void testSetMplsBos() {

    /*
     * test
     */
    target.setMplsBos(MAX_UINT8);

    /*
     * check
     */
    Integer resultGetter = target.getMplsBos();
    assertThat(resultGetter, is(MAX_UINT8));

    Integer resultVariable = Whitebox.getInternalState(target, "mplsBos");
    assertThat(resultVariable, is(MAX_UINT8));

    boolean flag = target.isWcMplsBos();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getPbbIsid()}
   */
  @Test
  public void testGetPbbIsid() {

    /*
     * test
     */
    Long result1st = target.getPbbIsid();
    target.setPbbIsid(MAX_UINT32);
    Long resultMax = target.getPbbIsid();
    target.setPbbIsid(MIN_UINT32);
    Long resultMin = target.getPbbIsid();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getPbbIsid();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setPbbIsid(java.lang.Integer)}
   */
  @Test
  public void testSetPbbIsid() {

    /*
     * test
     */
    target.setPbbIsid(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getPbbIsid();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "pbbIsid");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcPbbIsid();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getPbbIsidMask()}
   */
  @Test
  public void testGetPbbIsidMask() {

    /*
     * test
     */
    Long result1st = target.getPbbIsidMask();
    target.setPbbIsidMask(MAX_UINT32);
    Long resultMax = target.getPbbIsidMask();
    target.setPbbIsidMask(MIN_UINT32);
    Long resultMin = target.getPbbIsidMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT32));
    assertThat(resultMin, is(MIN_UINT32));

    verify(target, times(3)).getPbbIsidMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setPbbIsidMask(java.lang.Integer)}
   */
  @Test
  public void testSetPbbIsidMask() {

    /*
     * test
     */
    target.setPbbIsidMask(MAX_UINT32);

    /*
     * check
     */
    Long resultGetter = target.getPbbIsidMask();
    assertThat(resultGetter, is(MAX_UINT32));

    Long resultVariable = Whitebox.getInternalState(target, "pbbIsidMask");
    assertThat(resultVariable, is(MAX_UINT32));

    boolean flag = target.isWcPbbIsidMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getTunnelId()}
   */
  @Test
  public void testGetTunnelId() {

    /*
     * test
     */
    String result1st = target.getTunnelId();
    target.setTunnelId(MAX_UINT64);
    String resultMax = target.getTunnelId();
    target.setTunnelId(MIN_UINT64);
    String resultMin = target.getTunnelId();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT64));
    assertThat(resultMin, is(MIN_UINT64));

    verify(target, times(3)).getTunnelId();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setTunnelId(java.lang.Long)}
   */
  @Test
  public void testSetTunnelId() {

    /*
     * test
     */
    target.setTunnelId(MAX_UINT64);

    /*
     * check
     */
    String resultGetter = target.getTunnelId();
    assertThat(resultGetter, is(MAX_UINT64));

    String resultVariable = Whitebox.getInternalState(target, "tunnelId");
    assertThat(resultVariable, is(MAX_UINT64));

    boolean flag = target.isWcTunnelId();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getTunnelIdMask()}
   */
  @Test
  public void testGetTunnelIdMask() {

    /*
     * test
     */
    String result1st = target.getTunnelIdMask();
    target.setTunnelIdMask(MAX_UINT64);
    String resultMax = target.getTunnelIdMask();
    target.setTunnelIdMask(MIN_UINT64);
    String resultMin = target.getTunnelIdMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT64));
    assertThat(resultMin, is(MIN_UINT64));

    verify(target, times(3)).getTunnelIdMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setTunnelIdMask(java.lang.Long)}
   */
  @Test
  public void testSetTunnelIdMask() {

    /*
     * test
     */
    target.setTunnelIdMask(MAX_UINT64);

    /*
     * check
     */
    String resultGetter = target.getTunnelIdMask();
    assertThat(resultGetter, is(MAX_UINT64));

    String resultVariable = Whitebox.getInternalState(target, "tunnelIdMask");
    assertThat(resultVariable, is(MAX_UINT64));

    boolean flag = target.isWcTunnelIdMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6Exthdr()}
   */
  @Test
  public void testGetIpv6Exthdr() {

    /*
     * test
     */
    Integer result1st = target.getIpv6Exthdr();
    target.setIpv6Exthdr(MAX_UINT16);
    Integer resultMax = target.getIpv6Exthdr();
    target.setIpv6Exthdr(MIN_UINT16);
    Integer resultMin = target.getIpv6Exthdr();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getIpv6Exthdr();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6Exthdr(java.lang.Integer)}
   */
  @Test
  public void testSetIpv6Exthdr() {

    /*
     * test
     */
    target.setIpv6Exthdr(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getIpv6Exthdr();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "ipv6Exthdr");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcIpv6Exthdr();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getIpv6ExthdrMask()}
   */
  @Test
  public void testGetIpv6ExthdrMask() {

    /*
     * test
     */
    Integer result1st = target.getIpv6ExthdrMask();
    target.setIpv6ExthdrMask(MAX_UINT16);
    Integer resultMax = target.getIpv6ExthdrMask();
    target.setIpv6ExthdrMask(MIN_UINT16);
    Integer resultMin = target.getIpv6ExthdrMask();

    /*
     * check
     */
    assertThat(result1st, is(nullValue()));
    assertThat(resultMax, is(MAX_UINT16));
    assertThat(resultMin, is(MIN_UINT16));

    verify(target, times(3)).getIpv6ExthdrMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#setIpv6ExthdrMask(java.lang.Integer)}
   */
  @Test
  public void testSetIpv6ExthdrMask() {

    /*
     * test
     */
    target.setIpv6ExthdrMask(MAX_UINT16);

    /*
     * check
     */
    Integer resultGetter = target.getIpv6ExthdrMask();
    assertThat(resultGetter, is(MAX_UINT16));

    Integer resultVariable = Whitebox.getInternalState(target, "ipv6ExthdrMask");
    assertThat(resultVariable, is(MAX_UINT16));

    boolean flag = target.isWcIpv6ExthdrMask();
    assertThat(flag, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    String result = target.getType();

    assertThat(result, is("OFPFlowMatch"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcInPhyPort()}
   */
  @Test
  public void testIsWcInPhyPort() {

    /*
     * test
     */
    boolean result1st = target.isWcInPhyPort();
    Whitebox.setInternalState(target, "wcInPhyPort", false);
    boolean result2nd = target.isWcInPhyPort();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcInPhyPort();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcMetadata()}
   */
  @Test
  public void testIsWcMetadata() {

    /*
     * test
     */
    boolean result1st = target.isWcMetadata();
    Whitebox.setInternalState(target, "wcMetadata", false);
    boolean result2nd = target.isWcMetadata();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcMetadata();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcMetadataMask()}
   */
  @Test
  public void testIsWcMetadataMask() {

    /*
     * test
     */
    boolean result1st = target.isWcMetadataMask();
    Whitebox.setInternalState(target, "wcMetadataMask", false);
    boolean result2nd = target.isWcMetadataMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcMetadataMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcEthSrc()}
   */
  @Test
  public void testIsWcEthSrc() {

    /*
     * test
     */
    boolean result1st = target.isWcEthSrc();
    Whitebox.setInternalState(target, "wcEthSrc", false);
    boolean result2nd = target.isWcEthSrc();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcEthSrc();

  }

  /**
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcEthSrcMask()}
   */
  @Test
  public void testIsWcEthSrcMask() {

    /*
     * test
     */
    boolean result1st = target.isWcEthSrcMask();
    Whitebox.setInternalState(target, "wcEthSrcMask", false);
    boolean result2nd = target.isWcEthSrcMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcEthSrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcEthDst()}
   */
  @Test
  public void testIsWcEthDst() {

    /*
     * test
     */
    boolean result1st = target.isWcEthDst();
    Whitebox.setInternalState(target, "wcEthDst", false);
    boolean result2nd = target.isWcEthDst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcEthDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcEthDstMask()}
   */
  @Test
  public void testIsWcEthDstMask() {

    /*
     * test
     */
    boolean result1st = target.isWcEthDstMask();
    Whitebox.setInternalState(target, "wcEthDstMask", false);
    boolean result2nd = target.isWcEthDstMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcEthDstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcVlanVid()}
   */
  @Test
  public void testIsWcVlanVid() {

    /*
     * test
     */
    boolean result1st = target.isWcVlanVid();
    Whitebox.setInternalState(target, "wcVlanVid", false);
    boolean result2nd = target.isWcVlanVid();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcVlanVid();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcVlanVidMask()}
   */
  @Test
  public void testIsWcVlanVidMask() {

    /*
     * test
     */
    boolean result1st = target.isWcVlanVidMask();
    Whitebox.setInternalState(target, "wcVlanVidMask", false);
    boolean result2nd = target.isWcVlanVidMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcVlanVidMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcVlanPcp()}
   */
  @Test
  public void testIsWcVlanPcp() {

    /*
     * test
     */
    boolean result1st = target.isWcVlanPcp();
    Whitebox.setInternalState(target, "wcVlanPcp", false);
    boolean result2nd = target.isWcVlanPcp();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcVlanPcp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcEthType()}
   */
  @Test
  public void testIsWcEthType() {

    /*
     * test
     */
    boolean result1st = target.isWcEthType();
    Whitebox.setInternalState(target, "wcEthType", false);
    boolean result2nd = target.isWcEthType();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcEthType();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpDscp()}
   */
  @Test
  public void testIsWcIpDscp() {

    /*
     * test
     */
    boolean result1st = target.isWcIpDscp();
    Whitebox.setInternalState(target, "wcIpDscp", false);
    boolean result2nd = target.isWcIpDscp();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpDscp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpEcn()}
   */
  @Test
  public void testIsWcIpEcn() {

    /*
     * test
     */
    boolean result1st = target.isWcIpEcn();
    Whitebox.setInternalState(target, "wcIpEcn", false);
    boolean result2nd = target.isWcIpEcn();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpEcn();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpProto()}
   */
  @Test
  public void testIsWcIpProto() {

    /*
     * test
     */
    boolean result1st = target.isWcIpProto();
    Whitebox.setInternalState(target, "wcIpProto", false);
    boolean result2nd = target.isWcIpProto();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpProto();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv4Src()}
   */
  @Test
  public void testIsWcIpv4Src() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv4Src();
    Whitebox.setInternalState(target, "wcIpv4Src", false);
    boolean result2nd = target.isWcIpv4Src();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv4Src();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv4SrcMask()}
   */
  @Test
  public void testIsWcIpv4SrcMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv4SrcMask();
    Whitebox.setInternalState(target, "wcIpv4SrcMask", false);
    boolean result2nd = target.isWcIpv4SrcMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv4SrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv4Dst()}
   */
  @Test
  public void testIsWcIpv4Dst() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv4Dst();
    Whitebox.setInternalState(target, "wcIpv4Dst", false);
    boolean result2nd = target.isWcIpv4Dst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv4Dst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv4DstMask()}
   */
  @Test
  public void testIsWcIpv4DstMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv4DstMask();
    Whitebox.setInternalState(target, "wcIpv4DstMask", false);
    boolean result2nd = target.isWcIpv4DstMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv4DstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcTcpSrc()}
   */
  @Test
  public void testIsWcTcpSrc() {

    /*
     * test
     */
    boolean result1st = target.isWcTcpSrc();
    Whitebox.setInternalState(target, "wcTcpSrc", false);
    boolean result2nd = target.isWcTcpSrc();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcTcpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcTcpDst()}
   */
  @Test
  public void testIsWcTcpDst() {

    /*
     * test
     */
    boolean result1st = target.isWcTcpDst();
    Whitebox.setInternalState(target, "wcTcpDst", false);
    boolean result2nd = target.isWcTcpDst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcTcpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcUdpSrc()}
   */
  @Test
  public void testIsWcUdpSrc() {

    /*
     * test
     */
    boolean result1st = target.isWcUdpSrc();
    Whitebox.setInternalState(target, "wcUdpSrc", false);
    boolean result2nd = target.isWcUdpSrc();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcUdpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcUdpDst()}
   */
  @Test
  public void testIsWcUdpDst() {

    /*
     * test
     */
    boolean result1st = target.isWcUdpDst();
    Whitebox.setInternalState(target, "wcUdpDst", false);
    boolean result2nd = target.isWcUdpDst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcUdpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcSctpSrc()}
   */
  @Test
  public void testIsWcSctpSrc() {

    /*
     * test
     */
    boolean result1st = target.isWcSctpSrc();
    Whitebox.setInternalState(target, "wcSctpSrc", false);
    boolean result2nd = target.isWcSctpSrc();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcSctpSrc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcSctpDst()}
   */
  @Test
  public void testIsWcSctpDst() {

    /*
     * test
     */
    boolean result1st = target.isWcSctpDst();
    Whitebox.setInternalState(target, "wcSctpDst", false);
    boolean result2nd = target.isWcSctpDst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcSctpDst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIcmpv4Type()}
   */
  @Test
  public void testIsWcIcmpv4Type() {

    /*
     * test
     */
    boolean result1st = target.isWcIcmpv4Type();
    Whitebox.setInternalState(target, "wcIcmpv4Type", false);
    boolean result2nd = target.isWcIcmpv4Type();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIcmpv4Type();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIcmpv4Code()}
   */
  @Test
  public void testIsWcIcmpv4Code() {

    /*
     * test
     */
    boolean result1st = target.isWcIcmpv4Code();
    Whitebox.setInternalState(target, "wcIcmpv4Code", false);
    boolean result2nd = target.isWcIcmpv4Code();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIcmpv4Code();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpOp()}
   */
  @Test
  public void testIsWcArpOp() {

    /*
     * test
     */
    boolean result1st = target.isWcArpOp();
    Whitebox.setInternalState(target, "wcArpOp", false);
    boolean result2nd = target.isWcArpOp();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpOp();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpSpa()}
   */
  @Test
  public void testIsWcArpSpa() {

    /*
     * test
     */
    boolean result1st = target.isWcArpSpa();
    Whitebox.setInternalState(target, "wcArpSpa", false);
    boolean result2nd = target.isWcArpSpa();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpSpa();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpSpaMask()}
   */
  @Test
  public void testIsWcArpSpaMask() {

    /*
     * test
     */
    boolean result1st = target.isWcArpSpaMask();
    Whitebox.setInternalState(target, "wcArpSpaMask", false);
    boolean result2nd = target.isWcArpSpaMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpSpaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpTpa()}
   */
  @Test
  public void testIsWcArpTpa() {

    /*
     * test
     */
    boolean result1st = target.isWcArpTpa();
    Whitebox.setInternalState(target, "wcArpTpa", false);
    boolean result2nd = target.isWcArpTpa();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpTpa();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpTpaMask()}
   */
  @Test
  public void testIsWcArpTpaMask() {

    /*
     * test
     */
    boolean result1st = target.isWcArpTpaMask();
    Whitebox.setInternalState(target, "wcArpTpaMask", false);
    boolean result2nd = target.isWcArpTpaMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpTpaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpSha()}
   */
  @Test
  public void testIsWcArpSha() {

    /*
     * test
     */
    boolean result1st = target.isWcArpSha();
    Whitebox.setInternalState(target, "wcArpSha", false);
    boolean result2nd = target.isWcArpSha();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpSha();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpShaMask()}
   */
  @Test
  public void testIsWcArpShaMask() {

    /*
     * test
     */
    boolean result1st = target.isWcArpShaMask();
    Whitebox.setInternalState(target, "wcArpShaMask", false);
    boolean result2nd = target.isWcArpShaMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpShaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpTha()}
   */
  @Test
  public void testIsWcArpTha() {

    /*
     * test
     */
    boolean result1st = target.isWcArpTha();
    Whitebox.setInternalState(target, "wcArpTha", false);
    boolean result2nd = target.isWcArpTha();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpTha();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcArpThaMask()}
   */
  @Test
  public void testIsWcArpThaMask() {

    /*
     * test
     */
    boolean result1st = target.isWcArpThaMask();
    Whitebox.setInternalState(target, "wcArpThaMask", false);
    boolean result2nd = target.isWcArpThaMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcArpThaMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6Src()}
   */
  @Test
  public void testIsWcIpv6Src() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6Src();
    Whitebox.setInternalState(target, "wcIpv6Src", false);
    boolean result2nd = target.isWcIpv6Src();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6Src();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6SrcMask()}
   */
  @Test
  public void testIsWcIpv6SrcMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6SrcMask();
    Whitebox.setInternalState(target, "wcIpv6SrcMask", false);
    boolean result2nd = target.isWcIpv6SrcMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6SrcMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6Dst()}
   */
  @Test
  public void testIsWcIpv6Dst() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6Dst();
    Whitebox.setInternalState(target, "wcIpv6Dst", false);
    boolean result2nd = target.isWcIpv6Dst();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6Dst();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6DstMask()}
   */
  @Test
  public void testIsWcIpv6DstMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6DstMask();
    Whitebox.setInternalState(target, "wcIpv6DstMask", false);
    boolean result2nd = target.isWcIpv6DstMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6DstMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6Flabel()}
   */
  @Test
  public void testIsWcIpv6Flabel() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6Flabel();
    Whitebox.setInternalState(target, "wcIpv6Flabel", false);
    boolean result2nd = target.isWcIpv6Flabel();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6Flabel();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6FlabelMask()}
   */
  @Test
  public void testIsWcIpv6FlabelMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6FlabelMask();
    Whitebox.setInternalState(target, "wcIpv6FlabelMask", false);
    boolean result2nd = target.isWcIpv6FlabelMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6FlabelMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIcmpv6Type()}
   */
  @Test
  public void testIsWcIcmpv6Type() {

    /*
     * test
     */
    boolean result1st = target.isWcIcmpv6Type();
    Whitebox.setInternalState(target, "wcIcmpv6Type", false);
    boolean result2nd = target.isWcIcmpv6Type();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIcmpv6Type();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIcmpv6Code()}
   */
  @Test
  public void testIsWcIcmpv6Code() {

    /*
     * test
     */
    boolean result1st = target.isWcIcmpv6Code();
    Whitebox.setInternalState(target, "wcIcmpv6Code", false);
    boolean result2nd = target.isWcIcmpv6Code();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIcmpv6Code();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6NdTarget()}
   */
  @Test
  public void testIsWcIpv6NdTarget() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6NdTarget();
    Whitebox.setInternalState(target, "wcIpv6NdTarget", false);
    boolean result2nd = target.isWcIpv6NdTarget();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6NdTarget();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6NdSll()}
   */
  @Test
  public void testIsWcIpv6NdSll() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6NdSll();
    Whitebox.setInternalState(target, "wcIpv6NdSll", false);
    boolean result2nd = target.isWcIpv6NdSll();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6NdSll();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6NdTll()}
   */
  @Test
  public void testIsWcIpv6NdTll() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6NdTll();
    Whitebox.setInternalState(target, "wcIpv6NdTll", false);
    boolean result2nd = target.isWcIpv6NdTll();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6NdTll();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcMplsLabel()}
   */
  @Test
  public void testIsWcMplsLabel() {

    /*
     * test
     */
    boolean result1st = target.isWcMplsLabel();
    Whitebox.setInternalState(target, "wcMplsLabel", false);
    boolean result2nd = target.isWcMplsLabel();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcMplsLabel();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcMplsTc()}
   */
  @Test
  public void testIsWcMplsTc() {

    /*
     * test
     */
    boolean result1st = target.isWcMplsTc();
    Whitebox.setInternalState(target, "wcMplsTc", false);
    boolean result2nd = target.isWcMplsTc();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcMplsTc();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcMplsBos()}
   */
  @Test
  public void testIsWcMplsBos() {

    /*
     * test
     */
    boolean result1st = target.isWcMplsBos();
    Whitebox.setInternalState(target, "wcMplsBos", false);
    boolean result2nd = target.isWcMplsBos();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcMplsBos();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcPbbIsid()}
   */
  @Test
  public void testIsWcPbbIsid() {

    /*
     * test
     */
    boolean result1st = target.isWcPbbIsid();
    Whitebox.setInternalState(target, "wcPbbIsid", false);
    boolean result2nd = target.isWcPbbIsid();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcPbbIsid();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcPbbIsidMask()}
   */
  @Test
  public void testIsWcPbbIsidMask() {

    /*
     * test
     */
    boolean result1st = target.isWcPbbIsidMask();
    Whitebox.setInternalState(target, "wcPbbIsidMask", false);
    boolean result2nd = target.isWcPbbIsidMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcPbbIsidMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcTunnelId()}
   */
  @Test
  public void testIsWcTunnelId() {

    /*
     * test
     */
    boolean result1st = target.isWcTunnelId();
    Whitebox.setInternalState(target, "wcTunnelId", false);
    boolean result2nd = target.isWcTunnelId();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcTunnelId();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcTunnelIdMask()}
   */
  @Test
  public void testIsWcTunnelIdMask() {

    /*
     * test
     */
    boolean result1st = target.isWcTunnelIdMask();
    Whitebox.setInternalState(target, "wcTunnelIdMask", false);
    boolean result2nd = target.isWcTunnelIdMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcTunnelIdMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6Exthdr()}
   */
  @Test
  public void testIsWcIpv6Exthdr() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6Exthdr();
    Whitebox.setInternalState(target, "wcIpv6Exthdr", false);
    boolean result2nd = target.isWcIpv6Exthdr();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6Exthdr();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#isWcIpv6ExthdrMask()}
   */
  @Test
  public void testIsWcIpv6ExthdrMask() {

    /*
     * test
     */
    boolean result1st = target.isWcIpv6ExthdrMask();
    Whitebox.setInternalState(target, "wcIpv6ExthdrMask", false);
    boolean result2nd = target.isWcIpv6ExthdrMask();

    /*
     * check
     */
    assertThat(result1st, is(true));
    assertThat(result2nd, is(false));

    verify(target, times(2)).isWcIpv6ExthdrMask();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetInPhyPort()}
   */
  @Test
  public void testResetInPhyPort() {
    /*
     * test
     */
    target.setInPhyPort(MAX_UINT32);
    target.resetInPhyPort();

    /*
     * check
     */
    assertThat(target.getInPhyPort(), is(nullValue()));
    assertThat(target.isWcInPhyPort(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetMetadata()}
   */
  @Test
  public void testResetMetadata() {
    /*
     * test
     */
    target.setMetadata(MAX_UINT64);
    target.resetMetadata();

    /*
     * check
     */
    assertThat(target.getMetadata(), is(nullValue()));
    assertThat(target.isWcMetadata(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetMetadataMask()}
   */
  @Test
  public void testResetMetadataMask() {
    /*
     * test
     */
    target.setMetadataMask(MAX_UINT64);
    target.resetMetadataMask();

    /*
     * check
     */
    assertThat(target.getMetadataMask(), is(nullValue()));
    assertThat(target.isWcMetadataMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetEthSrc()}
   */
  @Test
  public void testResetEthSrc() {
    /*
     * test
     */
    target.setEthSrc("11:22:33:44:55:66");
    target.resetEthSrc();

    /*
     * check
     */
    assertThat(target.getEthSrc(), is(nullValue()));
    assertThat(target.isWcEthSrc(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetEthSrcMask()}
   */
  @Test
  public void testResetEthSrcMask() {
    /*
     * test
     */
    target.setEthSrcMask("ff:ff:ff:ff:ff:ff");
    target.resetEthSrcMask();

    /*
     * check
     */
    assertThat(target.getEthSrcMask(), is(nullValue()));
    assertThat(target.isWcEthSrcMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetEthDst()}
   */
  @Test
  public void testResetEthDst() {
    /*
     * test
     */
    target.setEthDst("11:22:33:44:55:66");
    target.resetEthDst();

    /*
     * check
     */
    assertThat(target.getEthDst(), is(nullValue()));
    assertThat(target.isWcEthDst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetEthDstMask()}
   */
  @Test
  public void testResetEthDstMask() {
    /*
     * test
     */
    target.setEthDstMask("11:22:33:44:55:66");
    target.resetEthDstMask();

    /*
     * check
     */
    assertThat(target.getEthDstMask(), is(nullValue()));
    assertThat(target.isWcEthDstMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetVlanVid()}
   */
  @Test
  public void testResetVlanVid() {
    /*
     * test
     */
    target.setVlanVid(MAX_UINT16);
    target.resetVlanVid();

    /*
     * check
     */
    assertThat(target.getVlanVid(), is(nullValue()));
    assertThat(target.isWcVlanVid(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetVlanVidMask()}
   */
  @Test
  public void testResetVlanVidMask() {
    /*
     * test
     */
    target.setVlanVidMask(MAX_UINT16);
    target.resetVlanVidMask();

    /*
     * check
     */
    assertThat(target.getVlanVidMask(), is(nullValue()));
    assertThat(target.isWcVlanVidMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetVlanPcp()}
   */
  @Test
  public void testResetVlanPcp() {
    /*
     * test
     */
    target.setVlanPcp(MAX_UINT8);
    target.resetVlanPcp();

    /*
     * check
     */
    assertThat(target.getVlanPcp(), is(nullValue()));
    assertThat(target.isWcVlanPcp(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetEthType()}
   */
  @Test
  public void testResetEthType() {
    /*
     * test
     */
    target.setEthType(MAX_UINT16);
    target.resetEthType();

    /*
     * check
     */
    assertThat(target.getEthType(), is(nullValue()));
    assertThat(target.isWcEthType(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpDscp()}
   */
  @Test
  public void testResetIpDscp() {
    /*
     * test
     */
    target.setIpDscp(MAX_UINT16);
    target.resetIpDscp();

    /*
     * check
     */
    assertThat(target.getIpDscp(), is(nullValue()));
    assertThat(target.isWcIpDscp(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpEcn()}
   */
  @Test
  public void testResetIpEcn() {
    /*
     * test
     */
    target.setIpEcn(MAX_UINT8);
    target.resetIpEcn();

    /*
     * check
     */
    assertThat(target.getIpEcn(), is(nullValue()));
    assertThat(target.isWcIpEcn(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpProto()}
   */
  @Test
  public void testResetIpProto() {
    /*
     * test
     */
    target.setIpProto(MAX_UINT8);
    target.resetIpProto();

    /*
     * check
     */
    assertThat(target.getIpProto(), is(nullValue()));
    assertThat(target.isWcIpProto(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv4Src()}
   */
  @Test
  public void testResetIpv4Src() {
    /*
     * test
     */
    target.setIpv4Src("127.0.0.1");
    target.resetIpv4Src();

    /*
     * check
     */
    assertThat(target.getIpv4Src(), is(nullValue()));
    assertThat(target.isWcIpv4Src(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv4SrcMask()}
   */
  @Test
  public void testResetIpv4SrcMask() {
    /*
     * test
     */
    target.setIpv4SrcMask("255.0.0.0");
    target.resetIpv4SrcMask();

    /*
     * check
     */
    assertThat(target.getIpv4SrcMask(), is(nullValue()));
    assertThat(target.isWcIpv4SrcMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv4Dst()}
   */
  @Test
  public void testResetIpv4Dst() {
    /*
     * test
     */
    target.setIpv4Dst("127.0.0.1");
    target.resetIpv4Dst();

    /*
     * check
     */
    assertThat(target.getIpv4Dst(), is(nullValue()));
    assertThat(target.isWcIpv4Dst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv4DstMask()}
   */
  @Test
  public void testResetIpv4DstMask() {
    /*
     * test
     */
    target.setIpv4DstMask("255.0.0.0");
    target.resetIpv4DstMask();

    /*
     * check
     */
    assertThat(target.getIpv4DstMask(), is(nullValue()));
    assertThat(target.isWcIpv4DstMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetTcpSrc()}
   */
  @Test
  public void testResetTcpSrc() {
    /*
     * test
     */
    target.setTcpSrc(MAX_UINT16);
    target.resetTcpSrc();

    /*
     * check
     */
    assertThat(target.getTcpSrc(), is(nullValue()));
    assertThat(target.isWcTcpSrc(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetTcpDst()}
   */
  @Test
  public void testResetTcpDst() {
    /*
     * test
     */
    target.setTcpDst(MAX_UINT16);
    target.resetTcpDst();

    /*
     * check
     */
    assertThat(target.getTcpDst(), is(nullValue()));
    assertThat(target.isWcTcpDst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetUdpSrc()}
   */
  @Test
  public void testResetUdpSrc() {
    /*
     * test
     */
    target.setUdpSrc(MAX_UINT16);
    target.resetUdpSrc();

    /*
     * check
     */
    assertThat(target.getUdpSrc(), is(nullValue()));
    assertThat(target.isWcUdpSrc(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetUdpDst()}
   */
  @Test
  public void testResetUdpDst() {
    /*
     * test
     */
    target.setUdpDst(MAX_UINT16);
    target.resetUdpDst();

    /*
     * check
     */
    assertThat(target.getUdpDst(), is(nullValue()));
    assertThat(target.isWcUdpDst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetSctpSrc()}
   */
  @Test
  public void testResetSctpSrc() {
    /*
     * test
     */
    target.setSctpSrc(MAX_UINT16);
    target.resetSctpSrc();

    /*
     * check
     */
    assertThat(target.getSctpSrc(), is(nullValue()));
    assertThat(target.isWcSctpSrc(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetSctpDst()}
   */
  @Test
  public void testResetSctpDst() {
    /*
     * test
     */
    target.setSctpDst(MAX_UINT16);
    target.resetSctpDst();

    /*
     * check
     */
    assertThat(target.getSctpDst(), is(nullValue()));
    assertThat(target.isWcSctpDst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIcmpv4Type()}
   */
  @Test
  public void testResetIcmpv4Type() {
    /*
     * test
     */
    target.setIcmpv4Type(MAX_UINT8);
    target.resetIcmpv4Type();

    /*
     * check
     */
    assertThat(target.getIcmpv4Type(), is(nullValue()));
    assertThat(target.isWcIcmpv4Type(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIcmpv4Code()}
   */
  @Test
  public void testResetIcmpv4Code() {
    /*
     * test
     */
    target.setIcmpv4Code(MAX_UINT8);
    target.resetIcmpv4Code();

    /*
     * check
     */
    assertThat(target.getIcmpv4Code(), is(nullValue()));
    assertThat(target.isWcIcmpv4Code(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpOp()}
   */
  @Test
  public void testResetArpOp() {
    /*
     * test
     */
    target.setArpOp(MAX_UINT16);
    target.resetArpOp();

    /*
     * check
     */
    assertThat(target.getArpOp(), is(nullValue()));
    assertThat(target.isWcArpOp(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpSpa()}
   */
  @Test
  public void testResetArpSpa() {
    /*
     * test
     */
    target.setArpSpa("127.0.0.1");
    target.resetArpSpa();

    /*
     * check
     */
    assertThat(target.getArpSpa(), is(nullValue()));
    assertThat(target.isWcArpSpa(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpSpaMask()}
   */
  @Test
  public void testResetArpSpaMask() {
    /*
     * test
     */
    target.setArpSpaMask("255.0.0.0");
    target.resetArpSpaMask();

    /*
     * check
     */
    assertThat(target.getArpSpaMask(), is(nullValue()));
    assertThat(target.isWcArpSpaMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpTpa()}
   */
  @Test
  public void testResetArpTpa() {
    /*
     * test
     */
    target.setArpTpa("127.0.0.1");
    target.resetArpTpa();

    /*
     * check
     */
    assertThat(target.getArpTpa(), is(nullValue()));
    assertThat(target.isWcArpTpa(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpTpaMask()}
   */
  @Test
  public void testResetArpTpaMask() {
    /*
     * test
     */
    target.setArpTpaMask("255.0.0.0");
    target.resetArpTpaMask();

    /*
     * check
     */
    assertThat(target.getArpTpaMask(), is(nullValue()));
    assertThat(target.isWcArpTpaMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpSha()}
   */
  @Test
  public void testResetArpSha() {
    /*
     * test
     */
    target.setArpSha("127.0.0.1");
    target.resetArpSha();

    /*
     * check
     */
    assertThat(target.getArpSha(), is(nullValue()));
    assertThat(target.isWcArpSha(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpShaMask()}
   */
  @Test
  public void testResetArpShaMask() {
    /*
     * test
     */
    target.setArpShaMask("255.0.0.0");
    target.resetArpShaMask();

    /*
     * check
     */
    assertThat(target.getArpShaMask(), is(nullValue()));
    assertThat(target.isWcArpShaMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpTha()}
   */
  @Test
  public void testResetArpTha() {
    /*
     * test
     */
    target.setArpTha("127.0.0.1");
    target.resetArpTha();

    /*
     * check
     */
    assertThat(target.getArpTha(), is(nullValue()));
    assertThat(target.isWcArpTha(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetArpThaMask()}
   */
  @Test
  public void testResetArpThaMask() {
    /*
     * test
     */
    target.setArpThaMask("255.0.0.0");
    target.resetArpThaMask();

    /*
     * check
     */
    assertThat(target.getArpThaMask(), is(nullValue()));
    assertThat(target.isWcArpThaMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6Src()}
   */
  @Test
  public void testResetIpv6Src() {
    /*
     * test
     */
    target.setIpv6Src("::1");
    target.resetIpv6Src();

    /*
     * check
     */
    assertThat(target.getIpv6Src(), is(nullValue()));
    assertThat(target.isWcIpv6Src(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6SrcMask()}
   */
  @Test
  public void testResetIpv6SrcMask() {
    /*
     * test
     */
    target.setIpv6SrcMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.resetIpv6SrcMask();

    /*
     * check
     */
    assertThat(target.getIpv6SrcMask(), is(nullValue()));
    assertThat(target.isWcIpv6SrcMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6Dst()}
   */
  @Test
  public void testResetIpv6Dst() {
    /*
     * test
     */
    target.setIpv6Dst("::1");
    target.resetIpv6Dst();

    /*
     * check
     */
    assertThat(target.getIpv6Dst(), is(nullValue()));
    assertThat(target.isWcIpv6Dst(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6DstMask()}
   */
  @Test
  public void testResetIpv6DstMask() {
    /*
     * test
     */
    target.setIpv6DstMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.resetIpv6DstMask();

    /*
     * check
     */
    assertThat(target.getIpv6DstMask(), is(nullValue()));
    assertThat(target.isWcIpv6DstMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6Flabel()}
   */
  @Test
  public void testResetIpv6Flabel() {
    /*
     * test
     */
    target.setIpv6Flabel(MAX_UINT32);
    target.resetIpv6Flabel();

    /*
     * check
     */
    assertThat(target.getIpv6Flabel(), is(nullValue()));
    assertThat(target.isWcIpv6Flabel(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6FlabelMask()}
   */
  @Test
  public void testResetIpv6FlabelMask() {
    /*
     * test
     */
    target.setIpv6FlabelMask(MAX_UINT32);
    target.resetIpv6FlabelMask();

    /*
     * check
     */
    assertThat(target.getIpv6FlabelMask(), is(nullValue()));
    assertThat(target.isWcIpv6FlabelMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIcmpv6Type()}
   */
  @Test
  public void testResetIcmpv6Type() {
    /*
     * test
     */
    target.setIcmpv6Type(MAX_UINT8);
    target.resetIcmpv6Type();

    /*
     * check
     */
    assertThat(target.getIcmpv6Type(), is(nullValue()));
    assertThat(target.isWcIcmpv6Type(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIcmpv6Code()}
   */
  @Test
  public void testResetIcmpv6Code() {
    /*
     * test
     */
    target.setIcmpv6Code(MAX_UINT8);
    target.resetIcmpv6Code();

    /*
     * check
     */
    assertThat(target.getIcmpv6Code(), is(nullValue()));
    assertThat(target.isWcIcmpv6Code(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6NdTarget()}
   */
  @Test
  public void testResetIpv6NdTarget() {
    /*
     * test
     */
    target.setIpv6NdTarget("::1");
    target.resetIpv6NdTarget();

    /*
     * check
     */
    assertThat(target.getIpv6NdTarget(), is(nullValue()));
    assertThat(target.isWcIpv6NdTarget(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6NdSll()}
   */
  @Test
  public void testResetIpv6NdSll() {
    /*
     * test
     */
    target.setIpv6NdSll("11:22:33:44:55:66");
    target.resetIpv6NdSll();

    /*
     * check
     */
    assertThat(target.getIpv6NdSll(), is(nullValue()));
    assertThat(target.isWcIpv6NdSll(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6NdTll()}
   */
  @Test
  public void testResetIpv6NdTll() {
    /*
     * test
     */
    target.setIpv6NdTll("11:22:33:44:55:66");
    target.resetIpv6NdTll();

    /*
     * check
     */
    assertThat(target.getIpv6NdTll(), is(nullValue()));
    assertThat(target.isWcIpv6NdTll(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetMplsLabel()}
   */
  @Test
  public void testResetMplsLabel() {
    /*
     * test
     */
    target.setMplsLabel(MAX_UINT32);
    target.resetMplsLabel();

    /*
     * check
     */
    assertThat(target.getMplsLabel(), is(nullValue()));
    assertThat(target.isWcMplsLabel(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetMplsTc()}
   */
  @Test
  public void testResetMplsTc() {
    /*
     * test
     */
    target.setMplsTc(MAX_UINT8);
    target.resetMplsTc();

    /*
     * check
     */
    assertThat(target.getMplsTc(), is(nullValue()));
    assertThat(target.isWcMplsTc(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetMplsBos()}
   */
  @Test
  public void testResetMplsBos() {
    /*
     * test
     */
    target.setMplsBos(MAX_UINT8);
    target.resetMplsBos();

    /*
     * check
     */
    assertThat(target.getMplsBos(), is(nullValue()));
    assertThat(target.isWcMplsBos(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetPbbIsid()}
   */
  @Test
  public void testResetPbbIsid() {
    /*
     * test
     */
    target.setPbbIsid(MAX_UINT32);
    target.resetPbbIsid();

    /*
     * check
     */
    assertThat(target.getPbbIsid(), is(nullValue()));
    assertThat(target.isWcPbbIsid(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetPbbIsidMask()}
   */
  @Test
  public void testResetPbbIsidMask() {
    /*
     * test
     */
    target.setPbbIsidMask(MAX_UINT32);
    target.resetPbbIsidMask();

    /*
     * check
     */
    assertThat(target.getPbbIsidMask(), is(nullValue()));
    assertThat(target.isWcPbbIsidMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetTunnelId()}
   */
  @Test
  public void testResetTunnelId() {
    /*
     * test
     */
    target.setTunnelId(MAX_UINT64);
    target.resetTunnelId();

    /*
     * check
     */
    assertThat(target.getTunnelId(), is(nullValue()));
    assertThat(target.isWcTunnelId(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetTunnelIdMask()}
   */
  @Test
  public void testResetTunnelIdMask() {
    /*
     * test
     */
    target.setTunnelIdMask(MAX_UINT64);
    target.resetTunnelIdMask();

    /*
     * check
     */
    assertThat(target.getTunnelIdMask(), is(nullValue()));
    assertThat(target.isWcTunnelIdMask(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6Exthdr()}
   */
  @Test
  public void testResetIpv6Exthdr() {
    /*
     * test
     */
    target.setIpv6Exthdr(MAX_UINT16);
    target.resetIpv6Exthdr();

    /*
     * check
     */
    assertThat(target.getIpv6Exthdr(), is(nullValue()));
    assertThat(target.isWcIpv6Exthdr(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatchresetIpv6ExthdrMask()}
   */
  @Test
  public void testResetIpv6ExthdrMask() {
    /*
     * test
     */
    target.setIpv6ExthdrMask(MAX_UINT16);
    target.resetIpv6ExthdrMask();

    /*
     * check
     */
    assertThat(target.getIpv6ExthdrMask(), is(nullValue()));
    assertThat(target.isWcIpv6ExthdrMask(), is(true));
  }


  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidate() {

    /*
     * setting
     */
    target = new OFPFlowMatch("NodeId", "PortId");
    target.setMetadata(MAX_UINT64);
    target.setMetadataMask(MAX_UINT64);
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthSrcMask("ff:ff:ff:ff:ff:ff");
    target.setEthDst("11:22:33:44:55:66");
    target.setEthDstMask("ff:ff:ff:ff:ff:ff");
    target.setEthType(MAX_UINT16);
    target.setVlanVid(MAX_UINT16);
    target.setVlanPcp(MAX_UINT8);
    target.setIpDscp(MAX_UINT8);
    target.setIpEcn(MAX_UINT8);
    target.setIpProto(MAX_UINT8);
    target.setIpv4Src("127.0.0.1");
    target.setIpv4SrcMask("255.0.0.0");
    target.setIpv4Dst("127.0.0.1");
    target.setIpv4DstMask("255.0.0.0");
    target.setTcpSrc(MAX_UINT16);
    target.setTcpDst(MAX_UINT16);
    target.setUdpSrc(MAX_UINT16);
    target.setUdpDst(MAX_UINT16);
    target.setSctpSrc(MAX_UINT16);
    target.setSctpDst(MAX_UINT16);
    target.setIcmpv4Type(MAX_UINT8);
    target.setIcmpv4Code(MAX_UINT8);
    target.setArpOp(MAX_UINT16);
    target.setArpSpa("127.0.0.1");
    target.setArpSpaMask("255.0.0.0");
    target.setArpTpa("127.0.0.1");
    target.setArpTpaMask("255.0.0.0");
    target.setArpSha("127.0.0.1");
    target.setArpShaMask("255.0.0.0");
    target.setArpTha("127.0.0.1");
    target.setArpThaMask("255.0.0.0");
    target.setIpv6Src("::1");
    target.setIpv6SrcMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.setIpv6Dst("::1");
    target.setIpv6DstMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.setIpv6Flabel(MAX_UINT32);
    target.setIpv6FlabelMask(MAX_UINT32);
    target.setIcmpv6Type(MAX_UINT8);
    target.setIcmpv6Code(MAX_UINT8);
    target.setIpv6NdTarget("::1");
    target.setIpv6NdSll("11:22:33:44:55:66");
    target.setIpv6NdTll("11:22:33:44:55:66");
    target.setMplsLabel(MAX_UINT32);
    target.setMplsTc(MAX_UINT8);
    target.setMplsBos(MAX_UINT8);
    target.setPbbIsid(MAX_UINT32);
    target.setPbbIsidMask(MAX_UINT32);
    target.setTunnelId(MAX_UINT64);
    target.setTunnelIdMask(MAX_UINT64);
    target.setIpv6Exthdr(MAX_UINT16);
    target.setIpv6ExthdrMask(MAX_UINT16);

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateFalseSuper() {
    /*
     * set
     */
    target = new OFPFlowMatch("", "in_port");
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthDst("dl_dst");
    target.setIpv4Src("11:22:33:44:55:66");
    target.setIpv4Dst("nw_dst");

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateFalseDlSrc() {

    /*
     * setting
     */
    target = new OFPFlowMatch("", "in_port");
    target.setEthSrc("");
    target.setEthDst("dl_dst");
    target.setIpv4Src("11:22:33:44:55:66");
    target.setIpv4Dst("nw_dst");

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateFalseDlDst() {

    /*
     * setting
     */
    target = new OFPFlowMatch("in_node", "in_port");
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthDst("");
    target.setIpv4Src("11:22:33:44:55:66");
    target.setIpv4Dst("nw_dst");

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateFalseNwSrc() {

    /*
     * setting
     */
    target = new OFPFlowMatch("in_node", "in_port");
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthDst("dl_dst");
    target.setIpv4Src("");
    target.setIpv4Dst("nw_dst");

    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateFalseNw_dst() {

    /*
     * setting
     */
    target = new OFPFlowMatch("in_node", "in_port");
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthDst("dl_dst");
    target.setIpv4Src("11:22:33:44:55:66");
    target.setIpv4Dst("");
    /*
     * test
     */
    boolean result = target.validate();

    /*
     * check
     */
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#validate()}
   * .
   */
  @Test
  public final void testValidateStringEmpty() {

    /**
     * setting
     */
    String[] parameters = new String[] {
        "metadata",
        "metadataMask",
        "ethSrc",
        "ethSrcMask",
        "ethDst",
        "ethDstMask",
        "ipv4Src",
        "ipv4SrcMask",
        "ipv4Dst",
        "ipv4DstMask",
        "arpSpa",
        "arpSpaMask",
        "arpTpa",
        "arpTpaMask",
        "arpSha",
        "arpShaMask",
        "arpTha",
        "arpThaMask",
        "ipv6Src",
        "ipv6SrcMask",
        "ipv6Dst",
        "ipv6DstMask",
        "ipv6NdTarget",
        "ipv6NdSll",
        "ipv6NdTll",
        "tunnelId",
        "tunnelIdMask",
    };

    for (String parameter : parameters) {
      target = creteAllParameterTarget();
      Whitebox.setInternalState(target, parameter, "");

      /*
       * test
       */
      boolean result = target.validate();

      /*
       * check
       */
      assertThat(parameter, result, is(false));
    }

  }

  private OFPFlowMatch creteAllParameterTarget() {

    OFPFlowMatch target = new OFPFlowMatch("NodeId", "PortId");
    target.setInPhyPort(MAX_UINT32);
    target.setMetadata(MAX_UINT64);
    target.setMetadataMask(MAX_UINT64);
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthSrcMask("ff:ff:ff:ff:ff:ff");
    target.setEthDst("11:22:33:44:55:66");
    target.setEthDstMask("ff:ff:ff:ff:ff:ff");
    target.setEthType(MAX_UINT16);
    target.setVlanVid(MAX_UINT16);
    target.setVlanVidMask(MAX_UINT16);
    target.setVlanPcp(MAX_UINT8);
    target.setIpDscp(MAX_UINT8);
    target.setIpEcn(MAX_UINT8);
    target.setIpProto(MAX_UINT8);
    target.setIpv4Src("127.0.0.1");
    target.setIpv4SrcMask("255.0.0.0");
    target.setIpv4Dst("127.0.0.1");
    target.setIpv4DstMask("255.0.0.0");
    target.setTcpSrc(MAX_UINT16);
    target.setTcpDst(MAX_UINT16);
    target.setUdpSrc(MAX_UINT16);
    target.setUdpDst(MAX_UINT16);
    target.setSctpSrc(MAX_UINT16);
    target.setSctpDst(MAX_UINT16);
    target.setIcmpv4Type(MAX_UINT8);
    target.setIcmpv4Code(MAX_UINT8);
    target.setArpOp(MAX_UINT16);
    target.setArpSpa("127.0.0.1");
    target.setArpSpaMask("255.0.0.0");
    target.setArpTpa("127.0.0.1");
    target.setArpTpaMask("255.0.0.0");
    target.setArpSha("127.0.0.1");
    target.setArpShaMask("255.0.0.0");
    target.setArpTha("127.0.0.1");
    target.setArpThaMask("255.0.0.0");
    target.setIpv6Src("::1");
    target.setIpv6SrcMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.setIpv6Dst("::1");
    target.setIpv6DstMask("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    target.setIpv6Flabel(MAX_UINT32);
    target.setIpv6FlabelMask(MAX_UINT32);
    target.setIcmpv6Type(MAX_UINT8);
    target.setIcmpv6Code(MAX_UINT8);
    target.setIpv6NdTarget("::1");
    target.setIpv6NdSll("11:22:33:44:55:66");
    target.setIpv6NdTll("11:22:33:44:55:66");
    target.setMplsLabel(MAX_UINT32);
    target.setMplsTc(MAX_UINT8);
    target.setMplsBos(MAX_UINT8);
    target.setPbbIsid(MAX_UINT32);
    target.setPbbIsidMask(MAX_UINT32);
    target.setTunnelId(MAX_UINT64);
    target.setTunnelIdMask(MAX_UINT64);
    target.setIpv6Exthdr(MAX_UINT16);
    target.setIpv6ExthdrMask(MAX_UINT16);

    return target;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {

    /*
     * setting
     */
    Value[] valueArray = createAllParameterValue();

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    target = new OFPFlowMatch();

    /*
     * test
     */
    boolean result = target.readValue(value);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(target.inNode, is("NodeId"));
    assertThat(target.inPort, is("PortId"));

    assertThat(target.getInPhyPort(), is(MAX_UINT32));
    assertThat(target.getMetadata(), is(MAX_UINT64));
    assertThat(target.getMetadataMask(), is(MAX_UINT64));
    assertThat(target.getEthSrc(), is("11:22:33:44:55:66"));
    assertThat(target.getEthSrcMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(target.getEthDst(), is("11:22:33:44:55:66"));
    assertThat(target.getEthDstMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(target.getEthType(), is(MAX_UINT16));
    assertThat(target.getVlanVid(), is(MAX_UINT16));
    assertThat(target.getVlanVidMask(), is(MAX_UINT16));
    assertThat(target.getVlanPcp(), is(MAX_UINT8));
    assertThat(target.getIpDscp(), is(MAX_UINT8));
    assertThat(target.getIpEcn(), is(MAX_UINT8));
    assertThat(target.getIpProto(), is(MAX_UINT8));
    assertThat(target.getIpv4Src(), is("127.0.0.1"));
    assertThat(target.getIpv4SrcMask(), is("255.0.0.0"));
    assertThat(target.getIpv4Dst(), is("127.0.0.1"));
    assertThat(target.getIpv4DstMask(), is("255.0.0.0"));
    assertThat(target.getTcpSrc(), is(MAX_UINT16));
    assertThat(target.getTcpDst(), is(MAX_UINT16));
    assertThat(target.getUdpSrc(), is(MAX_UINT16));
    assertThat(target.getUdpDst(), is(MAX_UINT16));
    assertThat(target.getSctpSrc(), is(MAX_UINT16));
    assertThat(target.getSctpDst(), is(MAX_UINT16));
    assertThat(target.getIcmpv4Type(), is(MAX_UINT8));
    assertThat(target.getIcmpv4Code(), is(MAX_UINT8));
    assertThat(target.getArpOp(), is(MAX_UINT16));
    assertThat(target.getArpSpa(), is("127.0.0.1"));
    assertThat(target.getArpSpaMask(), is("255.0.0.0"));
    assertThat(target.getArpTpa(), is("127.0.0.1"));
    assertThat(target.getArpTpaMask(), is("255.0.0.0"));
    assertThat(target.getArpSha(), is("127.0.0.1"));
    assertThat(target.getArpShaMask(), is("255.0.0.0"));
    assertThat(target.getArpTha(), is("127.0.0.1"));
    assertThat(target.getArpThaMask(), is("255.0.0.0"));
    assertThat(target.getIpv6Src(), is("::1"));
    assertThat(target.getIpv6SrcMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(target.getIpv6Dst(), is("::1"));
    assertThat(target.getIpv6DstMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(target.getIpv6Flabel(), is(MAX_UINT32));
    assertThat(target.getIpv6FlabelMask(), is(MAX_UINT32));
    assertThat(target.getIcmpv6Type(), is(MAX_UINT8));
    assertThat(target.getIcmpv6Code(), is(MAX_UINT8));
    assertThat(target.getIpv6NdTarget(), is("::1"));
    assertThat(target.getIpv6NdSll(), is("11:22:33:44:55:66"));
    assertThat(target.getIpv6NdTll(), is("11:22:33:44:55:66"));
    assertThat(target.getMplsLabel(), is(MAX_UINT32));
    assertThat(target.getMplsTc(), is(MAX_UINT8));
    assertThat(target.getMplsBos(), is(MAX_UINT8));
    assertThat(target.getPbbIsid(), is(MAX_UINT32));
    assertThat(target.getPbbIsidMask(), is(MAX_UINT32));
    assertThat(target.getTunnelId(), is(MAX_UINT64));
    assertThat(target.getTunnelIdMask(), is(MAX_UINT64));
    assertThat(target.getIpv6Exthdr(), is(MAX_UINT16));
    assertThat(target.getIpv6ExthdrMask(), is(MAX_UINT16));

    boolean wcInPort = WhiteboxImpl.getInternalState(target, "wcInPort");
    assertThat(wcInPort, is(false));

    assertThat(target.isWcInPhyPort(), is(false));
    assertThat(target.isWcMetadata(), is(false));
    assertThat(target.isWcMetadataMask(), is(false));
    assertThat(target.isWcEthSrc(), is(false));
    assertThat(target.isWcEthSrcMask(), is(false));
    assertThat(target.isWcEthDst(), is(false));
    assertThat(target.isWcEthDstMask(), is(false));
    assertThat(target.isWcVlanVid(), is(false));
    assertThat(target.isWcVlanVidMask(), is(false));
    assertThat(target.isWcVlanPcp(), is(false));
    assertThat(target.isWcEthType(), is(false));
    assertThat(target.isWcIpDscp(), is(false));
    assertThat(target.isWcIpEcn(), is(false));
    assertThat(target.isWcIpProto(), is(false));
    assertThat(target.isWcIpv4Src(), is(false));
    assertThat(target.isWcIpv4SrcMask(), is(false));
    assertThat(target.isWcIpv4Dst(), is(false));
    assertThat(target.isWcIpv4DstMask(), is(false));
    assertThat(target.isWcTcpSrc(), is(false));
    assertThat(target.isWcTcpDst(), is(false));
    assertThat(target.isWcUdpSrc(), is(false));
    assertThat(target.isWcUdpDst(), is(false));
    assertThat(target.isWcSctpSrc(), is(false));
    assertThat(target.isWcSctpDst(), is(false));
    assertThat(target.isWcIcmpv4Type(), is(false));
    assertThat(target.isWcIcmpv4Code(), is(false));
    assertThat(target.isWcArpOp(), is(false));
    assertThat(target.isWcArpSpa(), is(false));
    assertThat(target.isWcArpSpaMask(), is(false));
    assertThat(target.isWcArpTpa(), is(false));
    assertThat(target.isWcArpTpaMask(), is(false));
    assertThat(target.isWcArpSha(), is(false));
    assertThat(target.isWcArpShaMask(), is(false));
    assertThat(target.isWcArpTha(), is(false));
    assertThat(target.isWcArpThaMask(), is(false));
    assertThat(target.isWcIpv6Src(), is(false));
    assertThat(target.isWcIpv6SrcMask(), is(false));
    assertThat(target.isWcIpv6Dst(), is(false));
    assertThat(target.isWcIpv6DstMask(), is(false));
    assertThat(target.isWcIpv6Flabel(), is(false));
    assertThat(target.isWcIpv6FlabelMask(), is(false));
    assertThat(target.isWcIcmpv6Type(), is(false));
    assertThat(target.isWcIcmpv6Code(), is(false));
    assertThat(target.isWcIpv6NdTarget(), is(false));
    assertThat(target.isWcIpv6NdSll(), is(false));
    assertThat(target.isWcIpv6NdTll(), is(false));
    assertThat(target.isWcMplsLabel(), is(false));
    assertThat(target.isWcMplsTc(), is(false));
    assertThat(target.isWcMplsBos(), is(false));
    assertThat(target.isWcPbbIsid(), is(false));
    assertThat(target.isWcPbbIsidMask(), is(false));
    assertThat(target.isWcTunnelId(), is(false));
    assertThat(target.isWcTunnelIdMask(), is(false));
    assertThat(target.isWcIpv6Exthdr(), is(false));
    assertThat(target.isWcIpv6ExthdrMask(), is(false));
  }

  private Value[] createAllParameterValue() {

    Value[] valueArray = new Value[] {

        ValueFactory.createRawValue(OFPFlowMatch.IN_NODE),
        ValueFactory.createRawValue("NodeId"),
        ValueFactory.createRawValue(OFPFlowMatch.IN_PORT),
        ValueFactory.createRawValue("PortId"),

        ValueFactory.createRawValue(OFPFlowMatch.IN_PHY_PORT),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.METADATA),
        ValueFactory.createRawValue(MAX_UINT64),
        ValueFactory.createRawValue(OFPFlowMatch.METADATA_MASK),
        ValueFactory.createRawValue(MAX_UINT64),
        ValueFactory.createRawValue(OFPFlowMatch.ETH_SRC),
        ValueFactory.createRawValue("11:22:33:44:55:66"),
        ValueFactory.createRawValue(OFPFlowMatch.ETH_SRC_MASK),
        ValueFactory.createRawValue("ff:ff:ff:ff:ff:ff"),
        ValueFactory.createRawValue(OFPFlowMatch.ETH_DST),
        ValueFactory.createRawValue("11:22:33:44:55:66"),
        ValueFactory.createRawValue(OFPFlowMatch.ETH_DST_MASK),
        ValueFactory.createRawValue("ff:ff:ff:ff:ff:ff"),
        ValueFactory.createRawValue(OFPFlowMatch.ETH_TYPE),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.VLAN_VID),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.VLAN_VID_MASK),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.VLAN_PCP),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.IP_DSCP),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.IP_ECN),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.IP_PROTO),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.IPV4_SRC),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV4_SRC_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV4_DST),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV4_DST_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.TCP_SRC),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.TCP_DST),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.UDP_SRC),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.UDP_DST),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.SCTP_SRC),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.SCTP_DST),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.ICMPV4_TYPE),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.ICMPV4_CODE),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_OP),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_SPA),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_SPA_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_TPA),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_TPA_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_SHA),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_SHA_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_THA),
        ValueFactory.createRawValue("127.0.0.1"),
        ValueFactory.createRawValue(OFPFlowMatch.ARP_THA_MASK),
        ValueFactory.createRawValue("255.0.0.0"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_SRC),
        ValueFactory.createRawValue("::1"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_SRC_MASK),
        ValueFactory.createRawValue("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_DST),
        ValueFactory.createRawValue("::1"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_DST_MASK),
        ValueFactory.createRawValue("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_FLABEL),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_FLABEL_MASK),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.ICMPV6_TYPE),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.ICMPV6_CODE),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_ND_TARGET),
        ValueFactory.createRawValue("::1"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_ND_SLL),
        ValueFactory.createRawValue("11:22:33:44:55:66"),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_ND_TLL),
        ValueFactory.createRawValue("11:22:33:44:55:66"),
        ValueFactory.createRawValue(OFPFlowMatch.MPLS_LABEL),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.MPLS_TC),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.MPLS_BOS),
        ValueFactory.createIntegerValue(MAX_UINT8),
        ValueFactory.createRawValue(OFPFlowMatch.PBB_ISID),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.PBB_ISID_MASK),
        ValueFactory.createIntegerValue(MAX_UINT32),
        ValueFactory.createRawValue(OFPFlowMatch.TUNNEL_ID),
        ValueFactory.createRawValue(MAX_UINT64),
        ValueFactory.createRawValue(OFPFlowMatch.TUNNEL_ID_MASK),
        ValueFactory.createRawValue(MAX_UINT64),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_EXTHDR),
        ValueFactory.createIntegerValue(MAX_UINT16),
        ValueFactory.createRawValue(OFPFlowMatch.IPV6_EXTHDR_MASK),
        ValueFactory.createIntegerValue(MAX_UINT16)

    };

    return valueArray;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {

    /*
     * setting
     */
    target = creteAllParameterTarget();

    Map<String, Value> values = spy(new HashMap<String, Value>());

    /*
     * test
     */
    boolean result = target.writeValueSub(values);

    /*
     * check
     */
    assertThat(result, is(true));

    assertThat(target.inNode, is("NodeId"));
    assertThat(target.inPort, is("PortId"));

    assertThat(target.getInPhyPort(), is(MAX_UINT32));
    assertThat(target.getMetadata(), is(MAX_UINT64));
    assertThat(target.getMetadataMask(), is(MAX_UINT64));
    assertThat(target.getEthSrc(), is("11:22:33:44:55:66"));
    assertThat(target.getEthSrcMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(target.getEthDst(), is("11:22:33:44:55:66"));
    assertThat(target.getEthDstMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(target.getEthType(), is(MAX_UINT16));
    assertThat(target.getVlanVid(), is(MAX_UINT16));
    assertThat(target.getVlanVidMask(), is(MAX_UINT16));
    assertThat(target.getVlanPcp(), is(MAX_UINT8));
    assertThat(target.getIpDscp(), is(MAX_UINT8));
    assertThat(target.getIpEcn(), is(MAX_UINT8));
    assertThat(target.getIpProto(), is(MAX_UINT8));
    assertThat(target.getIpv4Src(), is("127.0.0.1"));
    assertThat(target.getIpv4SrcMask(), is("255.0.0.0"));
    assertThat(target.getIpv4Dst(), is("127.0.0.1"));
    assertThat(target.getIpv4DstMask(), is("255.0.0.0"));
    assertThat(target.getTcpSrc(), is(MAX_UINT16));
    assertThat(target.getTcpDst(), is(MAX_UINT16));
    assertThat(target.getUdpSrc(), is(MAX_UINT16));
    assertThat(target.getUdpDst(), is(MAX_UINT16));
    assertThat(target.getSctpSrc(), is(MAX_UINT16));
    assertThat(target.getSctpDst(), is(MAX_UINT16));
    assertThat(target.getIcmpv4Type(), is(MAX_UINT8));
    assertThat(target.getIcmpv4Code(), is(MAX_UINT8));
    assertThat(target.getArpOp(), is(MAX_UINT16));
    assertThat(target.getArpSpa(), is("127.0.0.1"));
    assertThat(target.getArpSpaMask(), is("255.0.0.0"));
    assertThat(target.getArpTpa(), is("127.0.0.1"));
    assertThat(target.getArpTpaMask(), is("255.0.0.0"));
    assertThat(target.getArpSha(), is("127.0.0.1"));
    assertThat(target.getArpShaMask(), is("255.0.0.0"));
    assertThat(target.getArpTha(), is("127.0.0.1"));
    assertThat(target.getArpThaMask(), is("255.0.0.0"));
    assertThat(target.getIpv6Src(), is("::1"));
    assertThat(target.getIpv6SrcMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(target.getIpv6Dst(), is("::1"));
    assertThat(target.getIpv6DstMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(target.getIpv6Flabel(), is(MAX_UINT32));
    assertThat(target.getIpv6FlabelMask(), is(MAX_UINT32));
    assertThat(target.getIcmpv6Type(), is(MAX_UINT8));
    assertThat(target.getIcmpv6Code(), is(MAX_UINT8));
    assertThat(target.getIpv6NdTarget(), is("::1"));
    assertThat(target.getIpv6NdSll(), is("11:22:33:44:55:66"));
    assertThat(target.getIpv6NdTll(), is("11:22:33:44:55:66"));
    assertThat(target.getMplsLabel(), is(MAX_UINT32));
    assertThat(target.getMplsTc(), is(MAX_UINT8));
    assertThat(target.getMplsBos(), is(MAX_UINT8));
    assertThat(target.getPbbIsid(), is(MAX_UINT32));
    assertThat(target.getPbbIsidMask(), is(MAX_UINT32));
    assertThat(target.getTunnelId(), is(MAX_UINT64));
    assertThat(target.getTunnelIdMask(), is(MAX_UINT64));
    assertThat(target.getIpv6Exthdr(), is(MAX_UINT16));
    assertThat(target.getIpv6ExthdrMask(), is(MAX_UINT16));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#hashCode()}
   * .
   */
  @Test
  public final void testHashCode() {

    /*
     * setting
     */
    OFPFlowMatch target1 = creteAllParameterTarget();
    OFPFlowMatch target1Clone = target1.clone();
    OFPFlowMatch target1New = creteAllParameterTarget();

    OFPFlowMatch target2 = new OFPFlowMatch();
    OFPFlowMatch target2Clone = target2.clone();
    OFPFlowMatch target2New = new OFPFlowMatch();

    OFPFlowMatch target3 = new OFPFlowMatch("NodeId", "PortId");
    OFPFlowMatch target3Clone = target3.clone();
    OFPFlowMatch target3New = new OFPFlowMatch("NodeId", "PortId");

    /*
     * test
     */
    int result1 = target1.hashCode();
    int result1Clone = target1Clone.hashCode();
    int result1New = target1New.hashCode();

    int result2 = target2.hashCode();
    int result2Clone = target2Clone.hashCode();
    int result2New = target2New.hashCode();

    int result3 = target3.hashCode();
    int result3Clone = target3Clone.hashCode();
    int result3New = target3New.hashCode();

    /*
     * check
     */
    assertThat(result1Clone, is(result1));
    assertThat(result1New, is(result1));

    assertThat(result2Clone, is(result2));
    assertThat(result2New, is(result2));

    assertThat(result3Clone, is(result3));
    assertThat(result3New, is(result3));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#hashCode()}
   * .
   */
  @Test
  public final void testHashCodeDiffer() {

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObject() {

    target = creteAllParameterTarget();
    OFPFlowMatch obj = creteAllParameterTarget();

    boolean result = target.equals(obj);

    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    boolean result = target.equals(null);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseInstanceof() {
    boolean result = target.equals("String");

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectFalseSuper() {
    OFPFlowMatch obj = new OFPFlowMatch();
    doReturn("Type").when(target).getType();

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNotEquals() {
    OFPFlowMatch obj = new OFPFlowMatch("in_node", "in_port");

    boolean result = target.equals(obj);

    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsDiffOneParameter() {

    /*
     * setting
     */
    target = creteAllParameterTarget();

    String[] parameters = new String[] {
        "inNode",
        "inPort",
        "inPhyPort",
        "metadata",
        "metadataMask",
        "ethSrc",
        "ethSrcMask",
        "ethDst",
        "ethDstMask",
        "vlanVid",
        "vlanVidMask",
        "vlanPcp",
        "ethType",

        "ipDscp",
        "ipEcn",
        "ipProto",
        "ipv4Src",
        "ipv4SrcMask",
        "ipv4Dst",
        "ipv4DstMask",

        "tcpSrc",
        "tcpDst",
        "udpSrc",
        "udpDst",
        "sctpSrc",
        "sctpDst",
        "icmpv4Type",
        "icmpv4Code",
        "arpOp",
        "arpSpa",
        "arpSpaMask",
        "arpTpa",
        "arpTpaMask",
        "arpSha",
        "arpShaMask",
        "arpTha",
        "arpThaMask",

        "ipv6Src",
        "ipv6SrcMask",
        "ipv6Dst",
        "ipv6DstMask",
        "ipv6Flabel",
        "ipv6FlabelMask",
        "icmpv6Type",
        "icmpv6Code",
        "ipv6NdTarget",
        "ipv6NdSll",
        "ipv6NdTll",
        "mplsLabel",
        "mplsTc",
        "mplsBos",
        "pbbIsid",
        "pbbIsidMask",
        "tunnelId",
        "tunnelIdMask",
        "ipv6Exthdr",
        "ipv6ExthdrMask"
    };

    for (String parameter : parameters) {

      OFPFlowMatch obj = creteAllParameterTarget();
      Whitebox.setInternalState(obj, parameter, (Object) null);

      /*
       * test
       */
      boolean result = target.equals(obj);

      /*
       * check
       */
      assertThat(parameter, result, is(false));
    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#clone()}
   * .
   */
  @Test
  public final void testClone() {

    /*
     * setting
     */
    target = creteAllParameterTarget();

    /*
     * test
     */
    OFPFlowMatch result = target.clone();

    /*
     * check
     */
    assertThat(result, is(not(sameInstance(target))));

    assertThat(result.inNode, is("NodeId"));
    assertThat(result.inPort, is("PortId"));

    assertThat(result.getMetadata(), is(MAX_UINT64));
    assertThat(result.getMetadataMask(), is(MAX_UINT64));
    assertThat(result.getEthSrc(), is("11:22:33:44:55:66"));
    assertThat(result.getEthSrcMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(result.getEthDst(), is("11:22:33:44:55:66"));
    assertThat(result.getEthDstMask(), is("ff:ff:ff:ff:ff:ff"));
    assertThat(result.getEthType(), is(MAX_UINT16));
    assertThat(result.getVlanVid(), is(MAX_UINT16));
    assertThat(result.getVlanVidMask(), is(MAX_UINT16));
    assertThat(result.getVlanPcp(), is(MAX_UINT8));
    assertThat(result.getIpDscp(), is(MAX_UINT8));
    assertThat(result.getIpEcn(), is(MAX_UINT8));
    assertThat(result.getIpProto(), is(MAX_UINT8));
    assertThat(result.getIpv4Src(), is("127.0.0.1"));
    assertThat(result.getIpv4SrcMask(), is("255.0.0.0"));
    assertThat(result.getIpv4Dst(), is("127.0.0.1"));
    assertThat(result.getIpv4DstMask(), is("255.0.0.0"));
    assertThat(result.getTcpSrc(), is(MAX_UINT16));
    assertThat(result.getTcpDst(), is(MAX_UINT16));
    assertThat(result.getUdpSrc(), is(MAX_UINT16));
    assertThat(result.getUdpDst(), is(MAX_UINT16));
    assertThat(result.getSctpSrc(), is(MAX_UINT16));
    assertThat(result.getSctpDst(), is(MAX_UINT16));
    assertThat(result.getIcmpv4Type(), is(MAX_UINT8));
    assertThat(result.getIcmpv4Code(), is(MAX_UINT8));
    assertThat(result.getArpOp(), is(MAX_UINT16));
    assertThat(result.getArpSpa(), is("127.0.0.1"));
    assertThat(result.getArpSpaMask(), is("255.0.0.0"));
    assertThat(result.getArpTpa(), is("127.0.0.1"));
    assertThat(result.getArpTpaMask(), is("255.0.0.0"));
    assertThat(result.getArpSha(), is("127.0.0.1"));
    assertThat(result.getArpShaMask(), is("255.0.0.0"));
    assertThat(result.getArpTha(), is("127.0.0.1"));
    assertThat(result.getArpThaMask(), is("255.0.0.0"));
    assertThat(result.getIpv6Src(), is("::1"));
    assertThat(result.getIpv6SrcMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(result.getIpv6Dst(), is("::1"));
    assertThat(result.getIpv6DstMask(), is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    assertThat(result.getIpv6Flabel(), is(MAX_UINT32));
    assertThat(result.getIpv6FlabelMask(), is(MAX_UINT32));
    assertThat(result.getIcmpv6Type(), is(MAX_UINT8));
    assertThat(result.getIcmpv6Code(), is(MAX_UINT8));
    assertThat(result.getIpv6NdTarget(), is("::1"));
    assertThat(result.getIpv6NdSll(), is("11:22:33:44:55:66"));
    assertThat(result.getIpv6NdTll(), is("11:22:33:44:55:66"));
    assertThat(result.getMplsLabel(), is(MAX_UINT32));
    assertThat(result.getMplsTc(), is(MAX_UINT8));
    assertThat(result.getMplsBos(), is(MAX_UINT8));
    assertThat(result.getPbbIsid(), is(MAX_UINT32));
    assertThat(result.getPbbIsidMask(), is(MAX_UINT32));
    assertThat(result.getTunnelId(), is(MAX_UINT64));
    assertThat(result.getTunnelIdMask(), is(MAX_UINT64));
    assertThat(result.getIpv6Exthdr(), is(MAX_UINT16));
    assertThat(result.getIpv6ExthdrMask(), is(MAX_UINT16));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#toString()}
   * .
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new OFPFlowMatch("NodeId", "PortId");
    target.setEthSrc("11:22:33:44:55:66");
    target.setEthDst("192.168.0.2");
    target.setIpv4Src("11:22:33:44:55:66");
    target.setIpv4Dst("192.168.1.2");

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedStrnig = StringUtils.join(new String[] {
        "[inNode=NodeId",
        "inPort=PortId",
        "ethSrc=11:22:33:44:55:66",
        "ethDst=192.168.0.2",
        "ipv4Src=11:22:33:44:55:66",
        "ipv4Dst=192.168.1.2]"
    }, ",");
    assertThat(result.endsWith(expectedStrnig), is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch#toString()}
   * .
   */
  @Test
  public final void testToStringAllParameter() {

    /*
     * setting
     */
    target = creteAllParameterTarget();

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append("inNode=NodeId,");
    sb.append("inPort=PortId,");
    sb.append("inPhyPort=4294967295,");
    sb.append("metadata=18446744073709551615,");
    sb.append("metadataMask=18446744073709551615,");
    sb.append("ethSrc=11:22:33:44:55:66,");
    sb.append("ethSrcMask=ff:ff:ff:ff:ff:ff,");
    sb.append("ethDst=11:22:33:44:55:66,");
    sb.append("ethDstMask=ff:ff:ff:ff:ff:ff,");
    sb.append("ethType=65535,");
    sb.append("vlanVid=65535,");
    sb.append("vlanVidMask=65535,");
    sb.append("vlanPcp=255,");
    sb.append("ipDscp=255,");
    sb.append("ipEcn=255,");
    sb.append("ipProto=255,");
    sb.append("ipv4Src=127.0.0.1,");
    sb.append("ipv4SrcMask=255.0.0.0,");
    sb.append("ipv4Dst=127.0.0.1,");
    sb.append("ipv4DstMask=255.0.0.0,");
    sb.append("tcpSrc=65535,");
    sb.append("tcpDst=65535,");
    sb.append("udpSrc=65535,");
    sb.append("udpDst=65535,");
    sb.append("sctpSrc=65535,");
    sb.append("sctpDst=65535,");
    sb.append("icmpv4Type=255,");
    sb.append("icmpv4Code=255,");
    sb.append("arpOp=65535,");
    sb.append("arpSpa=127.0.0.1,");
    sb.append("arpSpaMask=255.0.0.0,");
    sb.append("arpTpa=127.0.0.1,");
    sb.append("arpTpaMask=255.0.0.0,");
    sb.append("arpSha=127.0.0.1,");
    sb.append("arpShaMask=255.0.0.0,");
    sb.append("arpTha=127.0.0.1,");
    sb.append("arpThaMask=255.0.0.0,");
    sb.append("ipv6Src=::1,");
    sb.append("ipv6SrcMask=ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff,");
    sb.append("ipv6Dst=::1,");
    sb.append("ipv6DstMask=ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff,");
    sb.append("ipv6Flabel=4294967295,");
    sb.append("ipv6FlabelMask=4294967295,");
    sb.append("icmpv6Type=255,");
    sb.append("icmpv6Code=255,");
    sb.append("ipv6NdTarget=::1,");
    sb.append("ipv6NdSll=11:22:33:44:55:66,");
    sb.append("ipv6NdTll=11:22:33:44:55:66,");
    sb.append("mplsLabel=4294967295,");
    sb.append("mplsTc=255,");
    sb.append("mplsBos=255,");
    sb.append("pbbIsid=4294967295,");
    sb.append("pbbIsidMask=4294967295,");
    sb.append("tunnelId=18446744073709551615,");
    sb.append("tunnelIdMask=18446744073709551615,");
    sb.append("ipv6Exthdr=65535,");
    sb.append("ipv6ExthdrMask=65535");
    sb.append("]");
    String expectStr = sb.toString();

    assertThat(result.endsWith(expectStr), is(true));

  }
}
