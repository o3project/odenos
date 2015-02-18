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

package org.o3project.odenos.core.component.network.flow.query;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for OFPFlowMatchQuery.
 *
 *
 *
 */
public class OFPFlowMatchQueryTest {

  private OFPFlowMatchQuery target;
  private Map<String, String> params;

  private static final String MAX_UINT64 = new BigInteger("ffffffffffffffff", 16).toString();
  private static final String STRING_MAX_UINT64 = new BigInteger("ffffffffffffffff", 16).toString();

  private static final long MAX_UINT32 = Long.decode("0xffffffff");
  private static final String STRING_MAX_UINT32 = Long.decode("0xffffffff").toString();

  private static final int MAX_UINT16 = Integer.decode("0xffff");
  private static final String STRING_MAX_UINT16 = Integer.decode("0xffff").toString();

  private static final int MAX_UINT8 = Integer.decode("0xff");
  private static final String STRING_MAX_UINT8 = Integer.decode("0xff").toString();

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
    params = new HashMap<String, String>();
    target = new OFPFlowMatchQuery(params);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    params = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#OFPFlowMatchQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowMatchQuery() {
    params = new HashMap<String, String>();
    target = new OFPFlowMatchQuery(params);

    assertThat(target, is(instanceOf(OFPFlowMatchQuery.class)));
    assertThat((String) WhiteboxImpl.getInternalState(target, "ethSrc"),
        nullValue(String.class));
    assertThat((String) WhiteboxImpl.getInternalState(target, "ethDst"),
        nullValue(String.class));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "vlanVid"),
        nullValue(Integer.class));
    assertThat(
        (Integer) WhiteboxImpl.getInternalState(target, "vlanPcp"),
        nullValue(Integer.class));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ethType"),
        nullValue(Integer.class));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "ipProto"),
        nullValue(Integer.class));
    assertThat((String) WhiteboxImpl.getInternalState(target, "ipv4Src"),
        nullValue(String.class));
    assertThat((String) WhiteboxImpl.getInternalState(target, "ipv4Dst"),
        nullValue(String.class));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "tcpSrc"),
        nullValue(Integer.class));
    assertThat((Integer) WhiteboxImpl.getInternalState(target, "tcpDst"),
        nullValue(Integer.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {
    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put(OFPFlowMatch.IN_PORT, "port01");
        put(OFPFlowMatch.VLAN_VID, "100");
        put(OFPFlowMatch.VLAN_PCP, "200");
        put(OFPFlowMatch.ETH_TYPE, "300");
        put(OFPFlowMatch.IP_DSCP, "400");
        put(OFPFlowMatch.IP_ECN, "400");
        put(OFPFlowMatch.IP_PROTO, "500");
        put(OFPFlowMatch.UDP_SRC, "600");
        put(OFPFlowMatch.UDP_DST, "700");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseDlVlanErr() {
    params = new HashMap<String, String>() {
      {
        put("dl_vlan", "dl_vlan");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseDlVlanPcpErr() {
    params = new HashMap<String, String>() {
      {
        put("dl_vlan_pcp", "dl_vlan_pcp");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseDlTypeErr() {
    params = new HashMap<String, String>() {
      {
        put(OFPFlowMatch.ETH_TYPE, "dl_type");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNwTosErr() {
    params = new HashMap<String, String>() {
      {
        put(OFPFlowMatch.IP_DSCP, "nw_tos");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNwProtoErr() {
    params = new HashMap<String, String>() {
      {
        put(OFPFlowMatch.IP_PROTO, "nw_proto");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseTpSrcErr() {
    params = new HashMap<String, String>() {
      {
        put(OFPFlowMatch.TCP_SRC, "tp_src");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseTpDstErr() {
    params = new HashMap<String, String>() {
      {
        put(OFPFlowMatch.TCP_DST, "tp_dst");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuperErr() {
    params = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSizeErr() {
    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("vendor_id", "12");
      }
    };
    target = new OFPFlowMatchQuery(params);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @Test
  public final void testParseNull() {

    /*
     * setting
     */
    target = new OFPFlowMatchQuery(null);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#parse()}
   * .
   */
  @Test
  public final void testParseAll() {

    /*
     * setting
     */
    Map<String, String> matches = new HashMap<>();

    matches.put("type", "OFPFlowMatch");
    matches.put("in_node", "NodeId");
    matches.put("in_port", "PortId");
    matches.put(OFPFlowMatch.IN_PHY_PORT, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.METADATA, STRING_MAX_UINT64);
    matches.put(OFPFlowMatch.METADATA_MASK, STRING_MAX_UINT64);
    matches.put(OFPFlowMatch.ETH_SRC, "11:22:33:44:55:66");
    matches.put(OFPFlowMatch.ETH_SRC_MASK, "ff:ff:ff:ff:ff:ff");
    matches.put(OFPFlowMatch.ETH_DST, "11:22:33:44:55:66");
    matches.put(OFPFlowMatch.ETH_DST_MASK, "ff:ff:ff:ff:ff:ff");
    matches.put(OFPFlowMatch.ETH_TYPE, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.VLAN_VID, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.VLAN_VID_MASK, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.VLAN_PCP, STRING_MAX_UINT8);

    matches.put(OFPFlowMatch.IP_DSCP, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.IP_ECN, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.IP_PROTO, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.IPV4_SRC, "127.0.0.1");
    matches.put(OFPFlowMatch.IPV4_SRC_MASK, "255.0.0.0");
    matches.put(OFPFlowMatch.IPV4_DST, "127.0.0.1");
    matches.put(OFPFlowMatch.IPV4_DST_MASK, "255.0.0.0");

    matches.put(OFPFlowMatch.TCP_SRC, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.TCP_DST, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.UDP_SRC, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.UDP_DST, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.SCTP_SRC, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.SCTP_DST, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.ICMPV4_TYPE, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.ICMPV4_CODE, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.ARP_OP, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.ARP_SPA, "127.0.0.1");
    matches.put(OFPFlowMatch.ARP_SPA_MASK, "255.0.0.0");
    matches.put(OFPFlowMatch.ARP_TPA, "127.0.0.1");
    matches.put(OFPFlowMatch.ARP_TPA_MASK, "255.0.0.0");
    matches.put(OFPFlowMatch.ARP_SHA, "127.0.0.1");
    matches.put(OFPFlowMatch.ARP_SHA_MASK, "255.0.0.0");
    matches.put(OFPFlowMatch.ARP_THA, "127.0.0.1");
    matches.put(OFPFlowMatch.ARP_THA_MASK, "255.0.0.0");

    matches.put(OFPFlowMatch.IPV6_SRC, "::1");
    matches.put(OFPFlowMatch.IPV6_SRC_MASK, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    matches.put(OFPFlowMatch.IPV6_DST, "::1");
    matches.put(OFPFlowMatch.IPV6_DST_MASK, "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    matches.put(OFPFlowMatch.IPV6_FLABEL, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.IPV6_FLABEL_MASK, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.ICMPV6_TYPE, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.ICMPV6_CODE, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.IPV6_ND_TARGET, "::1");
    matches.put(OFPFlowMatch.IPV6_ND_SLL, "11:22:33:44:55:66");
    matches.put(OFPFlowMatch.IPV6_ND_TLL, "11:22:33:44:55:66");
    matches.put(OFPFlowMatch.MPLS_LABEL, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.MPLS_TC, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.MPLS_BOS, STRING_MAX_UINT8);
    matches.put(OFPFlowMatch.PBB_ISID, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.PBB_ISID_MASK, STRING_MAX_UINT32);
    matches.put(OFPFlowMatch.TUNNEL_ID, STRING_MAX_UINT64);
    matches.put(OFPFlowMatch.TUNNEL_ID_MASK, STRING_MAX_UINT64);
    matches.put(OFPFlowMatch.IPV6_EXTHDR, STRING_MAX_UINT16);
    matches.put(OFPFlowMatch.IPV6_EXTHDR_MASK, STRING_MAX_UINT16);

    target = new OFPFlowMatchQuery(matches);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    assertThat(result, is(true));

    String inNode = Whitebox.getInternalState(target, "inNode");
    assertThat(inNode, is("NodeId"));
    String inPort = Whitebox.getInternalState(target, "inPort");
    assertThat(inPort, is("PortId"));

    Long inPhyPort = Whitebox.getInternalState(target, "inPhyPort");
    assertThat(inPhyPort, is(MAX_UINT32));
    String metadata = Whitebox.getInternalState(target, "metadata");
    assertThat(metadata, is(MAX_UINT64));
    String metadataMask = Whitebox.getInternalState(target, "metadataMask");
    assertThat(metadataMask, is(MAX_UINT64));
    String ethSrc = Whitebox.getInternalState(target, "ethSrc");
    assertThat(ethSrc, is("11:22:33:44:55:66"));
    String ethSrcMask = Whitebox.getInternalState(target, "ethSrcMask");
    assertThat(ethSrcMask, is("ff:ff:ff:ff:ff:ff"));
    String ethDst = Whitebox.getInternalState(target, "ethDst");
    assertThat(ethDst, is("11:22:33:44:55:66"));
    String ethDstMask = Whitebox.getInternalState(target, "ethDstMask");
    assertThat(ethDstMask, is("ff:ff:ff:ff:ff:ff"));
    Integer ethType = Whitebox.getInternalState(target, "ethType");
    assertThat(ethType, is(MAX_UINT16));
    Integer vlanVid = Whitebox.getInternalState(target, "vlanVid");
    assertThat(vlanVid, is(MAX_UINT16));
    Integer vlanVidMask = Whitebox.getInternalState(target, "vlanVidMask");
    assertThat(vlanVidMask, is(MAX_UINT16));
    Integer vlanPcp = Whitebox.getInternalState(target, "vlanPcp");
    assertThat(vlanPcp, is(MAX_UINT8));

    Integer ipDscp = Whitebox.getInternalState(target, "ipDscp");
    assertThat(ipDscp, is(MAX_UINT8));
    Integer ipEcn = Whitebox.getInternalState(target, "ipEcn");
    assertThat(ipEcn, is(MAX_UINT8));
    Integer ipProto = Whitebox.getInternalState(target, "ipProto");
    assertThat(ipProto, is(MAX_UINT8));
    String ipv4Src = Whitebox.getInternalState(target, "ipv4Src");
    assertThat(ipv4Src, is("127.0.0.1"));
    String ipv4SrcMask = Whitebox.getInternalState(target, "ipv4SrcMask");
    assertThat(ipv4SrcMask, is("255.0.0.0"));
    String ipv4Dst = Whitebox.getInternalState(target, "ipv4Dst");
    assertThat(ipv4Dst, is("127.0.0.1"));
    String ipv4DstMask = Whitebox.getInternalState(target, "ipv4DstMask");
    assertThat(ipv4DstMask, is("255.0.0.0"));

    Integer tcpSrc = Whitebox.getInternalState(target, "tcpSrc");
    assertThat(tcpSrc, is(MAX_UINT16));
    Integer tcpDst = Whitebox.getInternalState(target, "tcpDst");
    assertThat(tcpDst, is(MAX_UINT16));
    Integer udpSrc = Whitebox.getInternalState(target, "udpSrc");
    assertThat(udpSrc, is(MAX_UINT16));
    Integer udpDst = Whitebox.getInternalState(target, "udpDst");
    assertThat(udpDst, is(MAX_UINT16));
    Integer sctpSrc = Whitebox.getInternalState(target, "sctpSrc");
    assertThat(sctpSrc, is(MAX_UINT16));
    Integer sctpDst = Whitebox.getInternalState(target, "sctpDst");
    assertThat(sctpDst, is(MAX_UINT16));
    Integer icmpv4Type = Whitebox.getInternalState(target, "icmpv4Type");
    assertThat(icmpv4Type, is(MAX_UINT8));
    Integer icmpv4Code = Whitebox.getInternalState(target, "icmpv4Code");
    assertThat(icmpv4Code, is(MAX_UINT8));
    Integer arpOp = Whitebox.getInternalState(target, "arpOp");
    assertThat(arpOp, is(MAX_UINT16));
    String arpSpa = Whitebox.getInternalState(target, "arpSpa");
    assertThat(arpSpa, is("127.0.0.1"));
    String arpSpaMask = Whitebox.getInternalState(target, "arpSpaMask");
    assertThat(arpSpaMask, is("255.0.0.0"));
    String arpTpa = Whitebox.getInternalState(target, "arpTpa");
    assertThat(arpTpa, is("127.0.0.1"));
    String arpTpaMask = Whitebox.getInternalState(target, "arpTpaMask");
    assertThat(arpTpaMask, is("255.0.0.0"));
    String arpSha = Whitebox.getInternalState(target, "arpSha");
    assertThat(arpSha, is("127.0.0.1"));
    String arpShaMask = Whitebox.getInternalState(target, "arpShaMask");
    assertThat(arpShaMask, is("255.0.0.0"));
    String arpTha = Whitebox.getInternalState(target, "arpTha");
    assertThat(arpTha, is("127.0.0.1"));
    String arpThaMask = Whitebox.getInternalState(target, "arpThaMask");
    assertThat(arpThaMask, is("255.0.0.0"));

    String ipv6Src = Whitebox.getInternalState(target, "ipv6Src");
    assertThat(ipv6Src, is("::1"));
    String ipv6SrcMask = Whitebox.getInternalState(target, "ipv6SrcMask");
    assertThat(ipv6SrcMask, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    String ipv6Dst = Whitebox.getInternalState(target, "ipv6Dst");
    assertThat(ipv6Dst, is("::1"));
    String ipv6DstMask = Whitebox.getInternalState(target, "ipv6DstMask");
    assertThat(ipv6DstMask, is("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    Long ipv6Flabel = Whitebox.getInternalState(target, "ipv6Flabel");
    assertThat(ipv6Flabel, is(MAX_UINT32));
    Long ipv6FlabelMask = Whitebox.getInternalState(target, "ipv6FlabelMask");
    assertThat(ipv6FlabelMask, is(MAX_UINT32));
    Integer icmpv6Type = Whitebox.getInternalState(target, "icmpv6Type");
    assertThat(icmpv6Type, is(MAX_UINT8));
    Integer icmpv6Code = Whitebox.getInternalState(target, "icmpv6Code");
    assertThat(icmpv6Code, is(MAX_UINT8));
    String ipv6NdTarget = Whitebox.getInternalState(target, "ipv6NdTarget");
    assertThat(ipv6NdTarget, is("::1"));
    String ipv6NdSll = Whitebox.getInternalState(target, "ipv6NdSll");
    assertThat(ipv6NdSll, is("11:22:33:44:55:66"));
    String ipv6NdTll = Whitebox.getInternalState(target, "ipv6NdTll");
    assertThat(ipv6NdTll, is("11:22:33:44:55:66"));
    Long mplsLabel = Whitebox.getInternalState(target, "mplsLabel");
    assertThat(mplsLabel, is(MAX_UINT32));
    Integer mplsTc = Whitebox.getInternalState(target, "mplsTc");
    assertThat(mplsTc, is(MAX_UINT8));
    Integer mplsBos = Whitebox.getInternalState(target, "mplsBos");
    assertThat(mplsBos, is(MAX_UINT8));
    Long pbbIsid = Whitebox.getInternalState(target, "pbbIsid");
    assertThat(pbbIsid, is(MAX_UINT32));
    Long pbbIsidMask = Whitebox.getInternalState(target, "pbbIsidMask");
    assertThat(pbbIsidMask, is(MAX_UINT32));
    String tunnelId = Whitebox.getInternalState(target, "tunnelId");
    assertThat(tunnelId, is(MAX_UINT64));
    String tunnelIdMask = Whitebox.getInternalState(target, "tunnelIdMask");
    assertThat(tunnelIdMask, is(MAX_UINT64));
    Integer ipv6Exthdr = Whitebox.getInternalState(target, "ipv6Exthdr");
    assertThat(ipv6Exthdr, is(MAX_UINT16));
    Integer ipv6ExthdrMask = Whitebox.getInternalState(target, "ipv6ExthdrMask");
    assertThat(ipv6ExthdrMask, is(MAX_UINT16));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {
    params = new HashMap<String, String>() {
      {
        put("type", "OFPFlowMatch");
        put("in_node", "node01");
        put("in_port", "port01");
        put("eth_src", "dl_src");
        put("eth_dst", "dl_dst");
        put("vlan_vid", "100");
        put("vlan_pcp", "200");
        put("eth_type", "300");
        put("ip_proto", "500");
        put("ipv4_src", "nw_src");
        put("ipv4_dst", "nw_dst");
        put("tcp_src", "600");
        put("tcp_dst", "700");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.inNode = "node01";
    match.inPort = "port01";
    match.setEthSrc("dl_src");
    match.setEthDst("dl_dst");
    match.setVlanVid(100);
    match.setVlanPcp(200);
    match.setEthType(300);
    match.setIpProto(500);
    match.setIpv4Src("nw_src");
    match.setIpv4Dst("nw_dst");
    match.setTcpSrc(600);
    match.setTcpDst(700);

    assertThat(target.matchExactly(match), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyDlSrcErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put("eth_src", "dl_src");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthSrc("dl_dst");

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyDlDstErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put("eth_dst", "dl_dst");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthDst("dl_src");

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyDlVlanErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put("vlan_vid", "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setVlanVid(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyDlVlanPcpErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put("vlan_pcp", "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setVlanPcp(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyDlTypeErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put("eth_type", "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthType(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyNwTosErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put(OFPFlowMatch.IN_PORT, "port01");
        put(OFPFlowMatch.IP_DSCP, "100");
      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setIpDscp(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyNwProtoErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put(OFPFlowMatch.IP_PROTO, "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setIpProto(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyNwSrcErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put(OFPFlowMatch.IPV4_SRC, "nw_src");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setIpv4Src("bbb");

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyNwDstErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put(OFPFlowMatch.IPV4_DST, "nw_dst");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setIpv4Dst("bbb");

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyTpSrcErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put(OFPFlowMatch.TCP_SRC, "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setTcpSrc(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyTpDstErr() {

    params = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("in_node", "node01");
        put("in_port", "port01");
        put(OFPFlowMatch.TCP_DST, "100");

      }
    };
    target = new OFPFlowMatchQuery(params);
    target.parse();

    OFPFlowMatch match = new OFPFlowMatch();
    match.setTcpDst(0);

    assertThat(target.matchExactly(match), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testMatchExactlyAllParameter() {

    /*
     * setting
     */
    target = createAllParameters();
    OFPFlowMatch match = creteAllParameterMatch();

    /*
     * test
     */
    boolean result = target.matchExactly(match);

    /*
     * check
     */
    assertThat(result, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testMatchExactlyDifferMatchNull() {

    /*
     * setting
     */
    target = createAllParameters();
    String[] parameters = parameters();

    for (String parameter : parameters) {

      OFPFlowMatch match = creteAllParameterMatch();
      Whitebox.setInternalState(match, parameter, (Object) null);

      /*
       * test
       */
      boolean result = target.matchExactly(match);

      /*
       * check
       */
      assertThat(parameter, result, is(false));
    }

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#matchExactly(org.o3project.odenos.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testMatchExactlyDifferTargetNull() {

    /*
     * setting
     */
    OFPFlowMatch match = creteAllParameterMatch();
    String[] parameters = parameters();

    for (String parameter : parameters) {

      target = createAllParameters();
      Whitebox.setInternalState(target, parameter, (Object) null);

      /*
       * test
       */
      boolean result = target.matchExactly(match);

      /*
       * check
       */
      assertThat(parameter, result, is(true));
    }

  }

  private String[] parameters() {

    String[] parameters = new String[] {
        //      "inNode",
        //      "inPort",
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

    return parameters;
  }

  private OFPFlowMatchQuery createAllParameters() {

    Map<String, String> params = new HashMap<>();
    OFPFlowMatchQuery query = new OFPFlowMatchQuery(params);

    Whitebox.setInternalState(query, "inPhyPort", MAX_UINT32);
    Whitebox.setInternalState(query, "metadata", MAX_UINT64);
    Whitebox.setInternalState(query, "metadataMask", MAX_UINT64);
    Whitebox.setInternalState(query, "ethSrc", "11:22:33:44:55:66");
    Whitebox.setInternalState(query, "ethSrcMask", "ff:ff:ff:ff:ff:ff");
    Whitebox.setInternalState(query, "ethDst", "11:22:33:44:55:66");
    Whitebox.setInternalState(query, "ethDstMask", "ff:ff:ff:ff:ff:ff");
    Whitebox.setInternalState(query, "ethType", MAX_UINT16);
    Whitebox.setInternalState(query, "vlanVid", MAX_UINT16);
    Whitebox.setInternalState(query, "vlanVidMask", MAX_UINT16);
    Whitebox.setInternalState(query, "vlanPcp", MAX_UINT8);

    Whitebox.setInternalState(query, "ipDscp", MAX_UINT8);
    Whitebox.setInternalState(query, "ipEcn", MAX_UINT8);
    Whitebox.setInternalState(query, "ipProto", MAX_UINT8);
    Whitebox.setInternalState(query, "ipv4Src", "127.0.0.1");
    Whitebox.setInternalState(query, "ipv4SrcMask", "255.0.0.0");
    Whitebox.setInternalState(query, "ipv4Dst", "127.0.0.1");
    Whitebox.setInternalState(query, "ipv4DstMask", "255.0.0.0");

    Whitebox.setInternalState(query, "tcpSrc", MAX_UINT16);
    Whitebox.setInternalState(query, "tcpDst", MAX_UINT16);
    Whitebox.setInternalState(query, "udpSrc", MAX_UINT16);
    Whitebox.setInternalState(query, "udpDst", MAX_UINT16);
    Whitebox.setInternalState(query, "sctpSrc", MAX_UINT16);
    Whitebox.setInternalState(query, "sctpDst", MAX_UINT16);
    Whitebox.setInternalState(query, "icmpv4Type", MAX_UINT8);
    Whitebox.setInternalState(query, "icmpv4Code", MAX_UINT8);
    Whitebox.setInternalState(query, "arpOp", MAX_UINT16);
    Whitebox.setInternalState(query, "arpSpa", "127.0.0.1");
    Whitebox.setInternalState(query, "arpSpaMask", "255.0.0.0");
    Whitebox.setInternalState(query, "arpTpa", "127.0.0.1");
    Whitebox.setInternalState(query, "arpTpaMask", "255.0.0.0");
    Whitebox.setInternalState(query, "arpSha", "127.0.0.1");
    Whitebox.setInternalState(query, "arpShaMask", "255.0.0.0");
    Whitebox.setInternalState(query, "arpTha", "127.0.0.1");
    Whitebox.setInternalState(query, "arpThaMask", "255.0.0.0");

    Whitebox.setInternalState(query, "ipv6Src", "::1");
    Whitebox.setInternalState(query, "ipv6SrcMask", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    Whitebox.setInternalState(query, "ipv6Dst", "::1");
    Whitebox.setInternalState(query, "ipv6DstMask", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    Whitebox.setInternalState(query, "ipv6Flabel", MAX_UINT32);
    Whitebox.setInternalState(query, "ipv6FlabelMask", MAX_UINT32);
    Whitebox.setInternalState(query, "icmpv6Type", MAX_UINT8);
    Whitebox.setInternalState(query, "icmpv6Code", MAX_UINT8);
    Whitebox.setInternalState(query, "ipv6NdTarget", "::1");
    Whitebox.setInternalState(query, "ipv6NdSll", "11:22:33:44:55:66");
    Whitebox.setInternalState(query, "ipv6NdTll", "11:22:33:44:55:66");
    Whitebox.setInternalState(query, "mplsLabel", MAX_UINT32);
    Whitebox.setInternalState(query, "mplsTc", MAX_UINT8);
    Whitebox.setInternalState(query, "mplsBos", MAX_UINT8);
    Whitebox.setInternalState(query, "pbbIsid", MAX_UINT32);
    Whitebox.setInternalState(query, "pbbIsidMask", MAX_UINT32);
    Whitebox.setInternalState(query, "tunnelId", MAX_UINT64);
    Whitebox.setInternalState(query, "tunnelIdMask", MAX_UINT64);
    Whitebox.setInternalState(query, "ipv6Exthdr", MAX_UINT16);
    Whitebox.setInternalState(query, "ipv6ExthdrMask", MAX_UINT16);

    return query;
  }

  private OFPFlowMatch creteAllParameterMatch() {

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
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#isInvalidParameter(java.lang.Object, java.lang.Object)}
   * .
   * @throws Exception
   */
  @Test
  public final void testIsInvalidParameter() throws Exception {

    /*
     * setting
     */
    Object queryStr = "123";
    Object matchStr = "123";
    Object differStr = "999";

    Object queryLong = new Long(123);
    Object matchLong = new Long(123);
    Object differLong = new Long(999);

    Object queryInt = new Integer(123);
    Object matchInt = new Integer(123);
    Object differInt = new Integer(999);

    Object matchNull = null;

    /*
     * test
     */
    boolean resultStrStr = Whitebox.invokeMethod(target, "isInvalidParameter", queryStr, matchStr);
    boolean resultStrDiff =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryStr, differStr);
    boolean resultStrLong =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryStr, matchLong);
    boolean resultStrInt = Whitebox.invokeMethod(target, "isInvalidParameter", queryStr, matchInt);
    boolean resultStrNull =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryStr, matchNull);

    boolean resultLongLong =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryLong, matchLong);
    boolean resultLongDiff =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryLong, differLong);
    boolean resultLongStr =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryLong, matchStr);
    boolean resultLongInt =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryLong, matchInt);
    boolean resultLongNull =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryLong, matchNull);

    boolean resultIntInt = Whitebox.invokeMethod(target, "isInvalidParameter", queryInt, matchInt);
    boolean resultIntDiff =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryInt, differInt);
    boolean resultIntStr = Whitebox.invokeMethod(target, "isInvalidParameter", queryInt, matchStr);
    boolean resultIntLong =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryInt, matchLong);
    boolean resultIntNull =
        Whitebox.invokeMethod(target, "isInvalidParameter", queryInt, matchNull);

    /*
     * check
     */
    assertThat(resultStrStr, is(false));
    assertThat(resultLongLong, is(false));
    assertThat(resultIntInt, is(false));

    assertThat(resultStrDiff, is(true));
    assertThat(resultStrLong, is(true));
    assertThat(resultStrInt, is(true));
    assertThat(resultStrNull, is(true));

    assertThat(resultLongDiff, is(true));
    assertThat(resultLongStr, is(true));
    assertThat(resultLongInt, is(true));
    assertThat(resultLongNull, is(true));

    assertThat(resultIntDiff, is(true));
    assertThat(resultIntStr, is(true));
    assertThat(resultIntLong, is(true));
    assertThat(resultIntNull, is(true));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowMatchQuery#isInvalidParameter(java.lang.Object, java.lang.Object)}
   * .
   * @throws Exception
   */
  @Test
  public final void testIsInvalidParameterQueryNull() throws Exception {

    /*
     * setting
     */
    Object query = null;
    Object matchNull = null;
    Object matchString = "123";
    Object matchLong = new Long(123);
    Object matchInteger = new Integer(123);

    /*
     * test
     */
    boolean resultNull = Whitebox.invokeMethod(target, "isInvalidParameter", query, matchNull);
    boolean resultString = Whitebox.invokeMethod(target, "isInvalidParameter", query, matchString);
    boolean resultLong = Whitebox.invokeMethod(target, "isInvalidParameter", query, matchLong);
    boolean resultInteger =
        Whitebox.invokeMethod(target, "isInvalidParameter", query, matchInteger);

    /*
     * check
     */
    assertThat(resultNull, is(false));
    assertThat(resultString, is(false));
    assertThat(resultLong, is(false));
    assertThat(resultInteger, is(false));

  }

}
