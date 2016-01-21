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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.Map;

/**
 * Class representing matching condition of OFPFlow.
 *
 */
public class OFPFlowMatch extends BasicFlowMatch {

  public static final String IN_PHY_PORT = "in_phy_port";
  public static final String METADATA = "metadata";
  public static final String METADATA_MASK = "metadata_mask";
  public static final String ETH_SRC = "eth_src";
  public static final String ETH_SRC_MASK = "eth_src_mask";
  public static final String ETH_DST = "eth_dst";
  public static final String ETH_DST_MASK = "eth_dst_mask";
  public static final String VLAN_VID = "vlan_vid";
  public static final String VLAN_VID_MASK = "vlan_vid_mask";
  public static final String VLAN_PCP = "vlan_pcp";
  public static final String ETH_TYPE = "eth_type";

  public static final String IP_DSCP = "ip_dscp";
  public static final String IP_ECN = "ip_ecn";
  public static final String IP_PROTO = "ip_proto";
  public static final String IPV4_SRC = "ipv4_src";
  public static final String IPV4_SRC_MASK = "ipv4_src_mask";
  public static final String IPV4_DST = "ipv4_dst";
  public static final String IPV4_DST_MASK = "ipv4_dst_mask";

  public static final String TCP_SRC = "tcp_src";
  public static final String TCP_DST = "tcp_dst";
  public static final String UDP_SRC = "udp_src";
  public static final String UDP_DST = "udp_dst";
  public static final String SCTP_SRC = "sctp_src";
  public static final String SCTP_DST = "sctp_dst";
  public static final String ICMPV4_TYPE = "icmpv4_type";
  public static final String ICMPV4_CODE = "icmpv4_code";

  public static final String ARP_OP = "arp_op";
  public static final String ARP_SPA = "arp_spa";
  public static final String ARP_SPA_MASK = "arp_spa_mask";
  public static final String ARP_TPA = "arp_tpa";
  public static final String ARP_TPA_MASK = "arp_tpa_mask";
  public static final String ARP_SHA = "arp_sha";
  public static final String ARP_SHA_MASK = "arp_sha_mask";
  public static final String ARP_THA = "arp_tha";
  public static final String ARP_THA_MASK = "arp_tha_mask";
  public static final String IPV6_SRC = "ipv6_src";
  public static final String IPV6_SRC_MASK = "ipv6_src_mask";
  public static final String IPV6_DST = "ipv6_dst";
  public static final String IPV6_DST_MASK = "ipv6_dst_mask";
  public static final String IPV6_FLABEL = "ipv6_flabel";
  public static final String IPV6_FLABEL_MASK = "ipv6_flabel_mask";
  public static final String ICMPV6_TYPE = "icmpv6_type";
  public static final String ICMPV6_CODE = "icmpv6_code";
  public static final String IPV6_ND_TARGET = "ipv6_nd_target";
  public static final String IPV6_ND_SLL = "ipv6_nd_sll";
  public static final String IPV6_ND_TLL = "ipv6_nd_tll";
  public static final String MPLS_LABEL = "mpls_label";
  public static final String MPLS_TC = "mpls_tc";
  public static final String MPLS_BOS = "mpls_bos";
  public static final String PBB_ISID = "pbb_isid";
  public static final String PBB_ISID_MASK = "pbb_isid_mask";
  public static final String TUNNEL_ID = "tunnel_id";
  public static final String TUNNEL_ID_MASK = "tunnel_id_mask";
  public static final String IPV6_EXTHDR = "ipv6_exthdr";
  public static final String IPV6_EXTHDR_MASK = "ipv6_exthdr_mask";

  private Long inPhyPort = 0L;
  private String metadata = "0";
  private String metadataMask = "0";
  private String ethSrc = "";
  private String ethSrcMask = "";
  private String ethDst = "";
  private String ethDstMask = "";
  private Integer vlanVid = 0;
  private Integer vlanVidMask = 0;
  private Integer vlanPcp = 0;
  private Integer ethType = 0;

  private Integer ipDscp = 0; // 6bit in Tos field
  private Integer ipEcn = 0; // 2bit in Tos field
  private Integer ipProto = 0;
  private String ipv4Src = "";
  private String ipv4SrcMask = "";
  private String ipv4Dst = "";
  private String ipv4DstMask = "";

  private Integer tcpSrc = 0;
  private Integer tcpDst = 0;
  private Integer udpSrc = 0;
  private Integer udpDst = 0;
  private Integer sctpSrc = 0;
  private Integer sctpDst = 0;
  private Integer icmpv4Type = 0;
  private Integer icmpv4Code = 0;
  private Integer arpOp = 0;
  private String arpSpa = "";
  private String arpSpaMask = "";
  private String arpTpa = "";
  private String arpTpaMask = "";
  private String arpSha = "";
  private String arpShaMask = "";
  private String arpTha = "";
  private String arpThaMask = "";

  private String ipv6Src = "";
  private String ipv6SrcMask = "";
  private String ipv6Dst = "";
  private String ipv6DstMask = "";
  private Long ipv6Flabel = 0L;
  private Long ipv6FlabelMask = 0L;
  private Integer icmpv6Type = 0;
  private Integer icmpv6Code = 0;
  private String ipv6NdTarget = "";
  private String ipv6NdSll = "";
  private String ipv6NdTll = "";
  private Long mplsLabel = 0L;
  private Integer mplsTc = 0;
  private Integer mplsBos = 0;
  private Long pbbIsid = 0L;
  private Long pbbIsidMask = 0L;
  private String tunnelId = "0";
  private String tunnelIdMask = "0";
  private Integer ipv6Exthdr = 0;
  private Integer ipv6ExthdrMask = 0;

  private boolean wcInPhyPort = true;
  private boolean wcMetadata = true;
  private boolean wcMetadataMask = true;
  private boolean wcEthSrc = true;
  private boolean wcEthSrcMask = true;
  private boolean wcEthDst = true;
  private boolean wcEthDstMask = true;
  private boolean wcVlanVid = true;
  private boolean wcVlanVidMask = true;
  private boolean wcVlanPcp = true;
  private boolean wcEthType = true;

  private boolean wcIpDscp = true;
  private boolean wcIpEcn = true;
  private boolean wcIpProto = true;
  private boolean wcIpv4Src = true;
  private boolean wcIpv4SrcMask = true;
  private boolean wcIpv4Dst = true;
  private boolean wcIpv4DstMask = true;

  private boolean wcTcpSrc = true;
  private boolean wcTcpDst = true;
  private boolean wcUdpSrc = true;
  private boolean wcUdpDst = true;
  private boolean wcSctpSrc = true;
  private boolean wcSctpDst = true;
  private boolean wcIcmpv4Type = true;
  private boolean wcIcmpv4Code = true;
  private boolean wcArpOp = true;
  private boolean wcArpSpa = true;
  private boolean wcArpSpaMask = true;
  private boolean wcArpTpa = true;
  private boolean wcArpTpaMask = true;
  private boolean wcArpSha = true;
  private boolean wcArpShaMask = true;
  private boolean wcArpTha = true;
  private boolean wcArpThaMask = true;

  private boolean wcIpv6Src = true;
  private boolean wcIpv6SrcMask = true;
  private boolean wcIpv6Dst = true;
  private boolean wcIpv6DstMask = true;
  private boolean wcIpv6Flabel = true;
  private boolean wcIpv6FlabelMask = true;
  private boolean wcIcmpv6Type = true;
  private boolean wcIcmpv6Code = true;
  private boolean wcIpv6NdTarget = true;
  private boolean wcIpv6NdSll = true;
  private boolean wcIpv6NdTll = true;
  private boolean wcMplsLabel = true;
  private boolean wcMplsTc = true;
  private boolean wcMplsBos = true;
  private boolean wcPbbIsid = true;
  private boolean wcPbbIsidMask = true;
  private boolean wcTunnelId = true;
  private boolean wcTunnelIdMask = true;
  private boolean wcIpv6Exthdr = true;
  private boolean wcIpv6ExthdrMask = true;

  /**
   * Constructor.
   */
  public OFPFlowMatch() {
    super();
  }

  public OFPFlowMatch(String inNode, String inPort) {
    super(inNode, inPort);
  }

  public Long getInPhyPort() {
    if (isWcInPhyPort()) {
      return null;
    }
    return inPhyPort;
  }

  public void setInPhyPort(Long phyInPort) {
    this.inPhyPort = phyInPort;
    this.wcInPhyPort = false;
  }

  public void resetInPhyPort() {
    this.inPhyPort = 0L;
    this.wcInPhyPort = true;
  }

  public String getMetadata() {
    if (isWcMetadata()) {
      return null;
    }
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
    this.wcMetadata = false;
  }

  public void resetMetadata() {
    this.metadata = "0";
    this.wcMetadata = true;
  }

  public String getMetadataMask() {
    if (isWcMetadataMask()) {
      return null;
    }
    return metadataMask;
  }

  public void setMetadataMask(String metadataMask) {
    this.metadataMask = metadataMask;
    this.wcMetadataMask = false;
  }

  public void resetMetadataMask() {
    this.metadataMask = "0";
    this.wcMetadataMask = true;
  }

  public String getEthSrc() {
    if (isWcEthSrc()) {
      return null;
    }
    return ethSrc;
  }

  public void setEthSrc(String ethSrc) {
    this.ethSrc = ethSrc;
    this.wcEthSrc = false;
  }

  public void resetEthSrc() {
    this.ethSrc = "";
    this.wcEthSrc = true;
  }

  public String getEthSrcMask() {
    if (isWcEthSrcMask()) {
      return null;
    }
    return ethSrcMask;
  }

  public void setEthSrcMask(String ethSrcMask) {
    this.ethSrcMask = ethSrcMask;
    this.wcEthSrcMask = false;
  }

  public void resetEthSrcMask() {
    this.ethSrcMask = "";
    this.wcEthSrcMask = true;
  }

  public String getEthDst() {
    if (isWcEthDst()) {
      return null;
    }
    return ethDst;
  }

  public void setEthDst(String ethDst) {
    this.ethDst = ethDst;
    this.wcEthDst = false;
  }

  public void resetEthDst() {
    this.ethDst = "";
    this.wcEthDst = true;
  }

  public String getEthDstMask() {
    if (isWcEthDstMask()) {
      return null;
    }
    return ethDstMask;
  }

  public void setEthDstMask(String ethDstMask) {
    this.ethDstMask = ethDstMask;
    this.wcEthDstMask = false;
  }

  public void resetEthDstMask() {
    this.ethDstMask = "";
    this.wcEthDstMask = true;
  }

  public Integer getVlanVid() {
    if (isWcVlanVid()) {
      return null;
    }
    return vlanVid;
  }

  public void setVlanVid(Integer vlanVid) {
    this.vlanVid = vlanVid;
    this.wcVlanVid = false;
  }

  public void resetVlanVid() {
    this.vlanVid = 0;
    this.wcVlanVid = true;
  }

  public Integer getVlanVidMask() {
    if (isWcVlanVidMask()) {
      return null;
    }
    return vlanVidMask;
  }

  public void setVlanVidMask(Integer vlanVidMask) {
    this.vlanVidMask = vlanVidMask;
    this.wcVlanVidMask = false;
  }

  public void resetVlanVidMask() {
    this.vlanVidMask = 0;
    this.wcVlanVidMask = true;
  }

  public Integer getVlanPcp() {
    if (isWcVlanPcp()) {
      return null;
    }
    return vlanPcp;
  }

  public void setVlanPcp(Integer vlanPcp) {
    this.vlanPcp = vlanPcp;
    this.wcVlanPcp = false;
  }

  public void resetVlanPcp() {
    this.vlanPcp = 0;
    this.wcVlanPcp = true;
  }

  public Integer getEthType() {
    if (isWcEthType()) {
      return null;
    }
    return ethType;
  }

  public void setEthType(Integer ethType) {
    this.ethType = ethType;
    this.wcEthType = false;
  }

  public void resetEthType() {
    this.ethType = 0;
    this.wcEthType = true;
  }

  public Integer getIpDscp() {
    if (isWcIpDscp()) {
      return null;
    }
    return ipDscp;
  }

  public void setIpDscp(Integer ipDscp) {
    this.ipDscp = ipDscp;
    this.wcIpDscp = false;
  }

  public void resetIpDscp() {
    this.ipDscp = 0; // 6bit in Tos field
    this.wcIpDscp = true;
  }

  public Integer getIpEcn() {
    if (isWcIpEcn()) {
      return null;
    }
    return ipEcn;
  }

  public void setIpEcn(Integer ipEcn) {
    this.ipEcn = ipEcn;
    this.wcIpEcn = false;
  }

  public void resetIpEcn() {
    this.ipEcn = 0; // 2bit in Tos field
    this.wcIpEcn = true;
  }

  public Integer getIpProto() {
    if (isWcIpProto()) {
      return null;
    }
    return ipProto;
  }

  public void setIpProto(Integer ipProto) {
    this.ipProto = ipProto;
    this.wcIpProto = false;
  }

  public void resetIpProto() {
    this.ipProto = 0;
    this.wcIpProto = true;
  }

  public String getIpv4Src() {
    if (isWcIpv4Src()) {
      return null;
    }
    return ipv4Src;
  }

  public void setIpv4Src(String ipv4Src) {
    this.ipv4Src = ipv4Src;
    this.wcIpv4Src = false;
  }

  public void resetIpv4Src() {
    this.ipv4Src = "";
    this.wcIpv4Src = true;
  }

  public String getIpv4SrcMask() {
    if (isWcIpv4SrcMask()) {
      return null;
    }
    return ipv4SrcMask;
  }

  public void setIpv4SrcMask(String ipv4SrcMask) {
    this.ipv4SrcMask = ipv4SrcMask;
    this.wcIpv4SrcMask = false;
  }

  public void resetIpv4SrcMask() {
    this.ipv4SrcMask = "";
    this.wcIpv4SrcMask = true;
  }

  public String getIpv4Dst() {
    if (isWcIpv4Dst()) {
      return null;
    }
    return ipv4Dst;
  }

  public void setIpv4Dst(String ipv4Dst) {
    this.ipv4Dst = ipv4Dst;
    this.wcIpv4Dst = false;
  }

  public void resetIpv4Dst() {
    this.ipv4Dst = "";
    this.wcIpv4Dst = true;
  }

  public String getIpv4DstMask() {
    if (isWcIpv4DstMask()) {
      return null;
    }
    return ipv4DstMask;
  }

  public void setIpv4DstMask(String ipv4DstMask) {
    this.ipv4DstMask = ipv4DstMask;
    this.wcIpv4DstMask = false;
  }

  public void resetIpv4DstMask() {
    this.ipv4DstMask = "";
    this.wcIpv4DstMask = true;
  }

  public Integer getTcpSrc() {
    if (isWcTcpSrc()) {
      return null;
    }
    return tcpSrc;
  }

  public void setTcpSrc(Integer tcpSrc) {
    this.tcpSrc = tcpSrc;
    this.wcTcpSrc = false;
  }

  public void resetTcpSrc() {
    this.tcpSrc = 0;
    this.wcTcpSrc = true;
  }

  public Integer getTcpDst() {
    if (isWcTcpDst()) {
      return null;
    }
    return tcpDst;
  }

  public void setTcpDst(Integer tcpDst) {
    this.tcpDst = tcpDst;
    this.wcTcpDst = false;
  }

  public void resetTcpDst() {
    this.tcpDst = 0;
    this.wcTcpDst = true;
  }

  public Integer getUdpSrc() {
    if (isWcUdpSrc()) {
      return null;
    }
    return udpSrc;
  }

  public void setUdpSrc(Integer udpSrc) {
    this.udpSrc = udpSrc;
    this.wcUdpSrc = false;
  }

  public void resetUdpSrc() {
    this.udpSrc = 0;
    this.wcUdpSrc = true;
  }

  public Integer getUdpDst() {
    if (isWcUdpDst()) {
      return null;
    }
    return udpDst;
  }

  public void setUdpDst(Integer udpDst) {
    this.udpDst = udpDst;
    this.wcUdpDst = false;
  }

  public void resetUdpDst() {
    this.udpDst = 0;
    this.wcUdpDst = true;
  }

  public Integer getSctpSrc() {
    if (isWcSctpSrc()) {
      return null;
    }
    return sctpSrc;
  }

  public void setSctpSrc(Integer sctpSrc) {
    this.sctpSrc = sctpSrc;
    this.wcSctpSrc = false;
  }

  public void resetSctpSrc() {
    this.sctpSrc = 0;
    this.wcSctpSrc = true;
  }

  public Integer getSctpDst() {
    if (isWcSctpDst()) {
      return null;
    }
    return sctpDst;
  }

  public void setSctpDst(Integer sctpDst) {
    this.sctpDst = sctpDst;
    this.wcSctpDst = false;
  }

  public void resetSctpDst() {
    this.sctpDst = 0;
    this.wcSctpDst = true;
  }

  public Integer getIcmpv4Type() {
    if (isWcIcmpv4Type()) {
      return null;
    }
    return icmpv4Type;
  }

  public void setIcmpv4Type(Integer icmpv4Type) {
    this.icmpv4Type = icmpv4Type;
    this.wcIcmpv4Type = false;
  }

  public void resetIcmpv4Type() {
    this.icmpv4Type = 0;
    this.wcIcmpv4Type = true;
  }

  public Integer getIcmpv4Code() {
    if (isWcIcmpv4Code()) {
      return null;
    }
    return icmpv4Code;
  }

  public void setIcmpv4Code(Integer icmpv4Code) {
    this.icmpv4Code = icmpv4Code;
    this.wcIcmpv4Code = false;
  }

  public void resetIcmpv4Code() {
    this.icmpv4Code = 0;
    this.wcIcmpv4Code = true;
  }

  public Integer getArpOp() {
    if (isWcArpOp()) {
      return null;
    }
    return arpOp;
  }

  public void setArpOp(Integer arpOp) {
    this.arpOp = arpOp;
    this.wcArpOp = false;
  }

  public void resetArpOp() {
    this.arpOp = 0;
    this.wcArpOp = true;
  }

  public String getArpSpa() {
    if (isWcArpSpa()) {
      return null;
    }
    return arpSpa;
  }

  public void setArpSpa(String arpSpa) {
    this.arpSpa = arpSpa;
    this.wcArpSpa = false;
  }

  public void resetArpSpa() {
    this.arpSpa = "";
    this.wcArpSpa = true;
  }

  public String getArpSpaMask() {
    if (isWcArpSpaMask()) {
      return null;
    }
    return arpSpaMask;
  }

  public void setArpSpaMask(String arpSpaMask) {
    this.arpSpaMask = arpSpaMask;
    this.wcArpSpaMask = false;
  }

  public void resetArpSpaMask() {
    this.arpSpaMask = "";
    this.wcArpSpaMask = true;
  }

  public String getArpTpa() {
    if (isWcArpTpa()) {
      return null;
    }
    return arpTpa;
  }

  public void setArpTpa(String arpTpa) {
    this.arpTpa = arpTpa;
    this.wcArpTpa = false;
  }

  public void resetArpTpa() {
    this.arpTpa = "";
    this.wcArpTpa = true;
  }

  public String getArpTpaMask() {
    if (isWcArpTpaMask()) {
      return null;
    }
    return arpTpaMask;
  }

  public void setArpTpaMask(String arpTpaMask) {
    this.arpTpaMask = arpTpaMask;
    this.wcArpTpaMask = false;
  }

  public void resetArpTpaMask() {
    this.arpTpaMask = "";
    this.wcArpTpaMask = true;
  }

  public String getArpSha() {
    if (isWcArpSha()) {
      return null;
    }
    return arpSha;
  }

  public void setArpSha(String arpSha) {
    this.arpSha = arpSha;
    this.wcArpSha = false;
  }

  public void resetArpSha() {
    this.arpSha = "";
    this.wcArpSha = true;
  }

  public String getArpShaMask() {
    if (isWcArpShaMask()) {
      return null;
    }
    return arpShaMask;
  }

  public void setArpShaMask(String arpShaMask) {
    this.arpShaMask = arpShaMask;
    this.wcArpShaMask = false;
  }

  public void resetArpShaMask() {
    this.arpShaMask = "";
    this.wcArpShaMask = true;
  }

  public String getArpTha() {
    if (isWcArpTha()) {
      return null;
    }
    return arpTha;
  }

  public void setArpTha(String arpTha) {
    this.arpTha = arpTha;
    this.wcArpTha = false;
  }

  public void resetArpTha() {
    this.arpTha = "";
    this.wcArpTha = true;
  }

  public String getArpThaMask() {
    if (isWcArpThaMask()) {
      return null;
    }
    return arpThaMask;
  }

  public void setArpThaMask(String arpThaMask) {
    this.arpThaMask = arpThaMask;
    this.wcArpThaMask = false;
  }

  public void resetArpThaMask() {
    this.arpThaMask = "";
    this.wcArpThaMask = true;
  }

  public String getIpv6Src() {
    if (isWcIpv6Src()) {
      return null;
    }
    return ipv6Src;
  }

  public void setIpv6Src(String ipv6Src) {
    this.ipv6Src = ipv6Src;
    this.wcIpv6Src = false;
  }

  public void resetIpv6Src() {
    this.ipv6Src = "";
    this.wcIpv6Src = true;
  }

  public String getIpv6SrcMask() {
    if (isWcIpv6SrcMask()) {
      return null;
    }
    return ipv6SrcMask;
  }

  public void setIpv6SrcMask(String ipv6SrcMask) {
    this.ipv6SrcMask = ipv6SrcMask;
    this.wcIpv6SrcMask = false;
  }

  public void resetIpv6SrcMask() {
    this.ipv6SrcMask = "";
    this.wcIpv6SrcMask = true;
  }

  public String getIpv6Dst() {
    if(isWcIpv6Dst()) {
      return null;
    }
    return ipv6Dst;
  }

  public void setIpv6Dst(String ipv6Dst) {
    this.ipv6Dst = ipv6Dst;
    this.wcIpv6Dst = false;
  }

  public void resetIpv6Dst() {
    this.ipv6Dst = "";
    this.wcIpv6Dst = true;
  }

  public String getIpv6DstMask() {
    if (isWcIpv6DstMask()) {
      return null;
    }
    return ipv6DstMask;
  }

  public void setIpv6DstMask(String ipv6DstMak) {
    this.ipv6DstMask = ipv6DstMak;
    this.wcIpv6DstMask = false;
  }

  public void resetIpv6DstMask() {
    this.ipv6DstMask = "";
    this.wcIpv6DstMask = true;
  }

  public Long getIpv6Flabel() {
    if (isWcIpv6Flabel()) {
      return null;
    }
    return ipv6Flabel;
  }

  public void setIpv6Flabel(Long ipv6Flabel) {
    this.ipv6Flabel = ipv6Flabel;
    this.wcIpv6Flabel = false;
  }

  public void resetIpv6Flabel() {
    this.ipv6Flabel = 0L;
    this.wcIpv6Flabel = true;
  }

  public Long getIpv6FlabelMask() {
    if (isWcIpv6FlabelMask()) {
      return null;
    }
    return ipv6FlabelMask;
  }

  public void setIpv6FlabelMask(Long ipv6FlabelMask) {
    this.ipv6FlabelMask = ipv6FlabelMask;
    this.wcIpv6FlabelMask = false;
  }

  public void resetIpv6FlabelMask() {
    this.ipv6FlabelMask = 0L;
    this.wcIpv6FlabelMask = true;
  }

  public Integer getIcmpv6Type() {
    if (isWcIcmpv6Type()) {
      return null;
    }
    return icmpv6Type;
  }

  public void setIcmpv6Type(Integer icmpv6Type) {
    this.icmpv6Type = icmpv6Type;
    this.wcIcmpv6Type = false;
  }

  public void resetIcmpv6Type() {
    this.icmpv6Type = 0;
    this.wcIcmpv6Type = true;
  }

  public Integer getIcmpv6Code() {
    if (isWcIcmpv6Code()) {
      return null;
    }
    return icmpv6Code;
  }

  public void setIcmpv6Code(Integer icmpv6Code) {
    this.icmpv6Code = icmpv6Code;
    this.wcIcmpv6Code = false;
  }

  public void resetIcmpv6Code() {
    this.icmpv6Code = 0;
    this.wcIcmpv6Code = true;
  }

  public String getIpv6NdTarget() {
    if(isWcIpv6NdTarget()) {
      return null;
    }
    return ipv6NdTarget;
  }

  public void setIpv6NdTarget(String ipv6NdTarget) {
    this.ipv6NdTarget = ipv6NdTarget;
    this.wcIpv6NdTarget = false;
  }

  public void resetIpv6NdTarget() {
    this.ipv6NdTarget = "";
    this.wcIpv6NdTarget = true;
  }

  public String getIpv6NdSll() {
    if (isWcIpv6NdSll()) {
      return null;
    }
    return ipv6NdSll;
  }

  public void setIpv6NdSll(String ipv6NdSll) {
    this.ipv6NdSll = ipv6NdSll;
    this.wcIpv6NdSll = false;
  }

  public void resetIpv6NdSll() {
    this.ipv6NdSll = "";
    this.wcIpv6NdSll = true;
  }

  public String getIpv6NdTll() {
    if (isWcIpv6NdTll()) {
      return null;
    }
    return ipv6NdTll;
  }

  public void setIpv6NdTll(String ipv6NdTll) {
    this.ipv6NdTll = ipv6NdTll;
    this.wcIpv6NdTll = false;
  }

  public void resetIpv6NdTll() {
    this.ipv6NdTll = "";
    this.wcIpv6NdTll = true;
  }

  public Long getMplsLabel() {
    if (isWcMplsLabel()) {
      return null;
    }
    return mplsLabel;
  }

  public void setMplsLabel(Long mplsLabel) {
    this.mplsLabel = mplsLabel;
    this.wcMplsLabel = false;
  }

  public void resetMplsLabel() {
    this.mplsLabel = 0L;
    this.wcMplsLabel = true;
  }

  public Integer getMplsTc() {
    if (isWcMplsTc()) {
      return null;
    }
    return mplsTc;
  }

  public void setMplsTc(Integer mplsTc) {
    this.mplsTc = mplsTc;
    this.wcMplsTc = false;
  }

  public void resetMplsTc() {
    this.mplsTc = 0;
    this.wcMplsTc = true;
  }

  public Integer getMplsBos() {
    if (isWcMplsBos()) {
      return null;
    }
    return mplsBos;
  }

  public void setMplsBos(Integer mplsBos) {
    this.mplsBos = mplsBos;
    this.wcMplsBos = false;
  }

  public void resetMplsBos() {
    this.mplsBos = 0;
    this.wcMplsBos = true;
  }

  public Long getPbbIsid() {
    if (isWcPbbIsid()) {
      return null;
    }
    return pbbIsid;
  }

  public void setPbbIsid(Long pbbIsid) {
    this.pbbIsid = pbbIsid;
    this.wcPbbIsid = false;
  }

  public void resetPbbIsid() {
    this.pbbIsid = 0L;
    this.wcPbbIsid = true;
  }

  public Long getPbbIsidMask() {
    if (isWcPbbIsidMask()) {
      return null;
    }
    return pbbIsidMask;
  }

  public void setPbbIsidMask(Long pbbIsidMask) {
    this.pbbIsidMask = pbbIsidMask;
    this.wcPbbIsidMask = false;
  }

  public void resetPbbIsidMask() {
    this.pbbIsidMask = 0L;
    this.wcPbbIsidMask = true;
  }

  public String getTunnelId() {
    if (isWcTunnelId()) {
      return null;
    }
    return tunnelId;
  }

  public void setTunnelId(String tunnelId) {
    this.tunnelId = tunnelId;
    this.wcTunnelId = false;
  }

  public void resetTunnelId() {
    this.tunnelId = "0";
    this.wcTunnelId = true;
  }

  public String getTunnelIdMask() {
    if (isWcTunnelIdMask()) {
      return null;
    }
    return tunnelIdMask;
  }

  public void setTunnelIdMask(String tunnelIdMask) {
    this.tunnelIdMask = tunnelIdMask;
    this.wcTunnelIdMask = false;
  }

  public void resetTunnelIdMask() {
    this.tunnelIdMask = "0";
    this.wcTunnelIdMask = true;
  }

  public Integer getIpv6Exthdr() {
    if (isWcIpv6Exthdr()) {
      return null;
    }
    return ipv6Exthdr;
  }

  public void setIpv6Exthdr(Integer ipv6Exthdr) {
    this.ipv6Exthdr = ipv6Exthdr;
    this.wcIpv6Exthdr = false;
  }

  public void resetIpv6Exthdr() {
    this.ipv6Exthdr = 0;
    this.wcIpv6Exthdr = true;
  }

  public Integer getIpv6ExthdrMask() {
    if (isWcIpv6ExthdrMask()) {
      return null;
    }
    return ipv6ExthdrMask;
  }

  public void setIpv6ExthdrMask(Integer ipv6ExthdrMask) {
    this.ipv6ExthdrMask = ipv6ExthdrMask;
    this.wcIpv6ExthdrMask = false;
  }

  public void resetIpv6ExthdrMask() {
    this.ipv6ExthdrMask = 0;
    this.wcIpv6ExthdrMask = true;
  }

  public boolean isWcInPhyPort() {
    return wcInPhyPort;
  }

  public boolean isWcMetadata() {
    return wcMetadata;
  }

  public boolean isWcMetadataMask() {
    return wcMetadataMask;
  }

  public boolean isWcEthSrc() {
    return wcEthSrc;
  }

  public boolean isWcEthSrcMask() {
    return wcEthSrcMask;
  }

  public boolean isWcEthDst() {
    return wcEthDst;
  }

  public boolean isWcEthDstMask() {
    return wcEthDstMask;
  }

  public boolean isWcVlanVid() {
    return wcVlanVid;
  }

  public boolean isWcVlanVidMask() {
    return wcVlanVidMask;
  }

  public boolean isWcVlanPcp() {
    return wcVlanPcp;
  }

  public boolean isWcEthType() {
    return wcEthType;
  }

  public boolean isWcIpDscp() {
    return wcIpDscp;
  }

  public boolean isWcIpEcn() {
    return wcIpEcn;
  }

  public boolean isWcIpProto() {
    return wcIpProto;
  }

  public boolean isWcIpv4Src() {
    return wcIpv4Src;
  }

  public boolean isWcIpv4SrcMask() {
    return wcIpv4SrcMask;
  }

  public boolean isWcIpv4Dst() {
    return wcIpv4Dst;
  }

  public boolean isWcIpv4DstMask() {
    return wcIpv4DstMask;
  }

  public boolean isWcTcpSrc() {
    return wcTcpSrc;
  }

  public boolean isWcTcpDst() {
    return wcTcpDst;
  }

  public boolean isWcUdpSrc() {
    return wcUdpSrc;
  }

  public boolean isWcUdpDst() {
    return wcUdpDst;
  }

  public boolean isWcSctpSrc() {
    return wcSctpSrc;
  }

  public boolean isWcSctpDst() {
    return wcSctpDst;
  }

  public boolean isWcIcmpv4Type() {
    return wcIcmpv4Type;
  }

  public boolean isWcIcmpv4Code() {
    return wcIcmpv4Code;
  }

  public boolean isWcArpOp() {
    return wcArpOp;
  }

  public boolean isWcArpSpa() {
    return wcArpSpa;
  }

  public boolean isWcArpSpaMask() {
    return wcArpSpaMask;
  }

  public boolean isWcArpTpa() {
    return wcArpTpa;
  }

  public boolean isWcArpTpaMask() {
    return wcArpTpaMask;
  }

  public boolean isWcArpSha() {
    return wcArpSha;
  }

  public boolean isWcArpShaMask() {
    return wcArpShaMask;
  }

  public boolean isWcArpTha() {
    return wcArpTha;
  }

  public boolean isWcArpThaMask() {
    return wcArpThaMask;
  }

  public boolean isWcIpv6Src() {
    return wcIpv6Src;
  }

  public boolean isWcIpv6SrcMask() {
    return wcIpv6SrcMask;
  }

  public boolean isWcIpv6Dst() {
    return wcIpv6Dst;
  }

  public boolean isWcIpv6DstMask() {
    return wcIpv6DstMask;
  }

  public boolean isWcIpv6Flabel() {
    return wcIpv6Flabel;
  }

  public boolean isWcIpv6FlabelMask() {
    return wcIpv6FlabelMask;
  }

  public boolean isWcIcmpv6Type() {
    return wcIcmpv6Type;
  }

  public boolean isWcIcmpv6Code() {
    return wcIcmpv6Code;
  }

  public boolean isWcIpv6NdTarget() {
    return wcIpv6NdTarget;
  }

  public boolean isWcIpv6NdSll() {
    return wcIpv6NdSll;
  }

  public boolean isWcIpv6NdTll() {
    return wcIpv6NdTll;
  }

  public boolean isWcMplsLabel() {
    return wcMplsLabel;
  }

  public boolean isWcMplsTc() {
    return wcMplsTc;
  }

  public boolean isWcMplsBos() {
    return wcMplsBos;
  }

  public boolean isWcPbbIsid() {
    return wcPbbIsid;
  }

  public boolean isWcPbbIsidMask() {
    return wcPbbIsidMask;
  }

  public boolean isWcTunnelId() {
    return wcTunnelId;
  }

  public boolean isWcTunnelIdMask() {
    return wcTunnelIdMask;
  }

  public boolean isWcIpv6Exthdr() {
    return wcIpv6Exthdr;
  }

  public boolean isWcIpv6ExthdrMask() {
    return wcIpv6ExthdrMask;
  }

  @Override
  public boolean validate() {
    if (!super.validate()) {
      return false;
    }
    if (!wcMetadata && StringUtils.isBlank(metadata)) {
      return false;
    }
    if (!wcMetadataMask && StringUtils.isBlank(metadataMask)) {
      return false;
    }
    if (!wcEthSrc && StringUtils.isBlank(ethSrc)) {
      return false;
    }
    if (!wcEthSrcMask && StringUtils.isBlank(ethSrcMask)) {
      return false;
    }
    if (!wcEthDst && StringUtils.isBlank(ethDst)) {
      return false;
    }
    if (!wcEthDstMask && StringUtils.isBlank(ethDstMask)) {
      return false;
    }
    if (!wcIpv4Src && StringUtils.isBlank(ipv4Src)) {
      return false;
    }
    if (!wcIpv4SrcMask && StringUtils.isBlank(ipv4SrcMask)) {
      return false;
    }
    if (!wcIpv4Dst && StringUtils.isBlank(ipv4Dst)) {
      return false;
    }
    if (!wcIpv4DstMask && StringUtils.isBlank(ipv4DstMask)) {
      return false;
    }
    if (!wcArpSpa && StringUtils.isBlank(arpSpa)) {
      return false;
    }
    if (!wcArpSpaMask && StringUtils.isBlank(arpSpaMask)) {
      return false;
    }
    if (!wcArpTpa && StringUtils.isBlank(arpTpa)) {
      return false;
    }
    if (!wcArpTpaMask && StringUtils.isBlank(arpTpaMask)) {
      return false;
    }
    if (!wcArpSha && StringUtils.isBlank(arpSha)) {
      return false;
    }
    if (!wcArpShaMask && StringUtils.isBlank(arpShaMask)) {
      return false;
    }
    if (!wcArpTha && StringUtils.isBlank(arpTha)) {
      return false;
    }
    if (!wcArpThaMask && StringUtils.isBlank(arpThaMask)) {
      return false;
    }
    if (!wcIpv6Src && StringUtils.isBlank(ipv6Src)) {
      return false;
    }
    if (!wcIpv6SrcMask && StringUtils.isBlank(ipv6SrcMask)) {
      return false;
    }
    if (!wcIpv6Dst && StringUtils.isBlank(ipv6Dst)) {
      return false;
    }
    if (!wcIpv6DstMask && StringUtils.isBlank(ipv6DstMask)) {
      return false;
    }
    if (!wcIpv6NdTarget && StringUtils.isBlank(ipv6NdTarget)) {
      return false;
    }
    if (!wcIpv6NdSll && StringUtils.isBlank(ipv6NdSll)) {
      return false;
    }
    if (!wcIpv6NdTll && StringUtils.isBlank(ipv6NdTll)) {
      return false;
    }
    if (!wcTunnelId && StringUtils.isBlank(tunnelId)) {
      return false;
    }
    if (!wcTunnelIdMask && StringUtils.isBlank(tunnelIdMask)) {
      return false;
    }

    return true;
  }

  @Override
  public String getType() {
    return "OFPFlowMatch";
  }

  @Override
  public boolean readValue(Value value) {
    super.readValue(value);

    Value[] values = value.asMapValue().getKeyValueArray();
    for (int i = 0; i < values.length; i += 2) {
      switch (values[i].asRawValue().getString()) {

        case IN_PHY_PORT:
          setInPhyPort(values[i + 1].asIntegerValue().getLong());
          break;
        case METADATA:
          setMetadata(values[i + 1].asRawValue().getString());
          break;
        case METADATA_MASK:
          setMetadataMask(values[i + 1].asRawValue().getString());
          break;
        case ETH_SRC:
          setEthSrc(values[i + 1].asRawValue().getString());
          break;
        case ETH_SRC_MASK:
          setEthSrcMask(values[i + 1].asRawValue().getString());
          break;
        case ETH_DST:
          setEthDst(values[i + 1].asRawValue().getString());
          break;
        case ETH_DST_MASK:
          setEthDstMask(values[i + 1].asRawValue().getString());
          break;
        case VLAN_VID:
          setVlanVid(values[i + 1].asIntegerValue().getInt());
          break;
        case VLAN_VID_MASK:
          setVlanVidMask(values[i + 1].asIntegerValue().getInt());
          break;
        case VLAN_PCP:
          setVlanPcp(values[i + 1].asIntegerValue().getInt());
          break;
        case ETH_TYPE:
          setEthType(values[i + 1].asIntegerValue().getInt());
          break;

        case IP_DSCP:
          setIpDscp(values[i + 1].asIntegerValue().getInt());
          break;
        case IP_ECN:
          setIpEcn(values[i + 1].asIntegerValue().getInt());
          break;
        case IP_PROTO:
          setIpProto(values[i + 1].asIntegerValue().getInt());
          break;
        case IPV4_SRC:
          setIpv4Src(values[i + 1].asRawValue().getString());
          break;
        case IPV4_SRC_MASK:
          setIpv4SrcMask(values[i + 1].asRawValue().getString());
          break;
        case IPV4_DST:
          setIpv4Dst(values[i + 1].asRawValue().getString());
          break;
        case IPV4_DST_MASK:
          setIpv4DstMask(values[i + 1].asRawValue().getString());
          break;

        case TCP_SRC:
          setTcpSrc(values[i + 1].asIntegerValue().getInt());
          break;
        case TCP_DST:
          setTcpDst(values[i + 1].asIntegerValue().getInt());
          break;
        case UDP_SRC:
          setUdpSrc(values[i + 1].asIntegerValue().getInt());
          break;
        case UDP_DST:
          setUdpDst(values[i + 1].asIntegerValue().getInt());
          break;
        case SCTP_SRC:
          setSctpSrc(values[i + 1].asIntegerValue().getInt());
          break;
        case SCTP_DST:
          setSctpDst(values[i + 1].asIntegerValue().getInt());
          break;
        case ICMPV4_TYPE:
          setIcmpv4Type(values[i + 1].asIntegerValue().getInt());
          break;
        case ICMPV4_CODE:
          setIcmpv4Code(values[i + 1].asIntegerValue().getInt());
          break;

        case ARP_OP:
          setArpOp(values[i + 1].asIntegerValue().getInt());
          break;
        case ARP_SPA:
          setArpSpa(values[i + 1].asRawValue().getString());
          break;
        case ARP_SPA_MASK:
          setArpSpaMask(values[i + 1].asRawValue().getString());
          break;
        case ARP_TPA:
          setArpTpa(values[i + 1].asRawValue().getString());
          break;
        case ARP_TPA_MASK:
          setArpTpaMask(values[i + 1].asRawValue().getString());
          break;
        case ARP_SHA:
          setArpSha(values[i + 1].asRawValue().getString());
          break;
        case ARP_SHA_MASK:
          setArpShaMask(values[i + 1].asRawValue().getString());
          break;
        case ARP_THA:
          setArpTha(values[i + 1].asRawValue().getString());
          break;
        case ARP_THA_MASK:
          setArpThaMask(values[i + 1].asRawValue().getString());
          break;

        case IPV6_SRC:
          setIpv6Src(values[i + 1].asRawValue().getString());
          break;
        case IPV6_SRC_MASK:
          setIpv6SrcMask(values[i + 1].asRawValue().getString());
          break;
        case IPV6_DST:
          setIpv6Dst(values[i + 1].asRawValue().getString());
          break;
        case IPV6_DST_MASK:
          setIpv6DstMask(values[i + 1].asRawValue().getString());
          break;
        case IPV6_FLABEL:
          setIpv6Flabel(values[i + 1].asIntegerValue().getLong());
          break;
        case IPV6_FLABEL_MASK:
          setIpv6FlabelMask(values[i + 1].asIntegerValue().getLong());
          break;
        case ICMPV6_TYPE:
          setIcmpv6Type(values[i + 1].asIntegerValue().getInt());
          break;
        case ICMPV6_CODE:
          setIcmpv6Code(values[i + 1].asIntegerValue().getInt());
          break;
        case IPV6_ND_TARGET:
          setIpv6NdTarget(values[i + 1].asRawValue().getString());
          break;
        case IPV6_ND_SLL:
          setIpv6NdSll(values[i + 1].asRawValue().getString());
          break;
        case IPV6_ND_TLL:
          setIpv6NdTll(values[i + 1].asRawValue().getString());
          break;

        case MPLS_LABEL:
          setMplsLabel(values[i + 1].asIntegerValue().getLong());
          break;
        case MPLS_TC:
          setMplsTc(values[i + 1].asIntegerValue().getInt());
          break;
        case MPLS_BOS:
          setMplsBos(values[i + 1].asIntegerValue().getInt());
          break;
        case PBB_ISID:
          setPbbIsid(values[i + 1].asIntegerValue().getLong());
          break;
        case PBB_ISID_MASK:
          setPbbIsidMask(values[i + 1].asIntegerValue().getLong());
          break;
        case TUNNEL_ID:
          setTunnelId(values[i + 1].asRawValue().getString());
          break;
        case TUNNEL_ID_MASK:
          setTunnelIdMask(values[i + 1].asRawValue().getString());
          break;
        case IPV6_EXTHDR:
          setIpv6Exthdr(values[i + 1].asIntegerValue().getInt());
          break;
        case IPV6_EXTHDR_MASK:
          setIpv6ExthdrMask(values[i + 1].asIntegerValue().getInt());
          break;
        default:
          break;
      }
    }

    return true;
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (!super.writeValueSub(values)) {
      return false;
    }

    if (!wcInPhyPort) {
      values.put(IN_PHY_PORT, ValueFactory.createIntegerValue(getInPhyPort()));
    }
    if (!wcMetadata) {
      values.put(METADATA, ValueFactory.createRawValue(getMetadata()));
    }
    if (!wcMetadataMask) {
      values.put(METADATA_MASK, ValueFactory.createRawValue(metadataMask));
    }
    if (!wcEthSrc) {
      values.put(ETH_SRC, ValueFactory.createRawValue(ethSrc));
    }
    if (!wcEthSrcMask) {
      values.put(ETH_SRC_MASK, ValueFactory.createRawValue(ethSrcMask));
    }
    if (!wcEthDst) {
      values.put(ETH_DST, ValueFactory.createRawValue(ethDst));
    }
    if (!wcEthDstMask) {
      values.put(ETH_DST_MASK, ValueFactory.createRawValue(ethDstMask));
    }
    if (!wcVlanVid) {
      values.put(VLAN_VID, ValueFactory.createIntegerValue(vlanVid));
    }
    if (!wcVlanVidMask) {
      values.put(VLAN_VID_MASK, ValueFactory.createIntegerValue(vlanVidMask));
    }
    if (!wcVlanPcp) {
      values.put(VLAN_PCP,
          ValueFactory.createIntegerValue(vlanPcp));
    }
    if (!wcEthType) {
      values.put(ETH_TYPE, ValueFactory.createIntegerValue(ethType));
    }

    if (!wcIpDscp) {
      values.put(IP_DSCP, ValueFactory.createIntegerValue(ipDscp));
    }
    if (!wcIpEcn) {
      values.put(IP_ECN, ValueFactory.createIntegerValue(ipEcn));
    }
    if (!wcIpProto) {
      values.put(IP_PROTO, ValueFactory.createIntegerValue(ipProto));
    }
    if (!wcIpv4Src) {
      values.put(IPV4_SRC, ValueFactory.createRawValue(ipv4Src));
    }
    if (!wcIpv4SrcMask) {
      values.put(IPV4_SRC_MASK, ValueFactory.createRawValue(ipv4SrcMask));
    }
    if (!wcIpv4Dst) {
      values.put(IPV4_DST, ValueFactory.createRawValue(ipv4Dst));
    }
    if (!wcIpv4DstMask) {
      values.put(IPV4_DST_MASK, ValueFactory.createRawValue(ipv4DstMask));
    }

    if (!wcTcpSrc) {
      values.put(TCP_SRC, ValueFactory.createIntegerValue(tcpSrc));
    }
    if (!wcTcpDst) {
      values.put(TCP_DST, ValueFactory.createIntegerValue(tcpDst));
    }
    if (!wcUdpSrc) {
      values.put(UDP_SRC, ValueFactory.createIntegerValue(udpSrc));
    }
    if (!wcUdpDst) {
      values.put(UDP_DST, ValueFactory.createIntegerValue(udpDst));
    }
    if (!wcSctpSrc) {
      values.put(SCTP_SRC, ValueFactory.createIntegerValue(sctpSrc));
    }
    if (!wcSctpDst) {
      values.put(SCTP_DST, ValueFactory.createIntegerValue(sctpDst));
    }
    if (!wcIcmpv4Type) {
      values.put(ICMPV4_TYPE, ValueFactory.createIntegerValue(icmpv4Type));
    }
    if (!wcIcmpv4Code) {
      values.put(ICMPV4_CODE, ValueFactory.createIntegerValue(icmpv4Code));
    }

    if (!wcArpOp) {
      values.put(ARP_OP, ValueFactory.createIntegerValue(arpOp));
    }
    if (!wcArpSpa) {
      values.put(ARP_SPA, ValueFactory.createRawValue(arpSpa));
    }
    if (!wcArpSpaMask) {
      values.put(ARP_SPA_MASK, ValueFactory.createRawValue(arpSpaMask));
    }
    if (!wcArpTpa) {
      values.put(ARP_TPA, ValueFactory.createRawValue(arpTpa));
    }
    if (!wcArpTpaMask) {
      values.put(ARP_TPA_MASK, ValueFactory.createRawValue(arpTpaMask));
    }
    if (!wcArpSha) {
      values.put(ARP_SHA, ValueFactory.createRawValue(arpSha));
    }
    if (!wcArpShaMask) {
      values.put(ARP_SHA_MASK, ValueFactory.createRawValue(arpShaMask));
    }
    if (!wcArpTha) {
      values.put(ARP_THA, ValueFactory.createRawValue(arpTha));
    }
    if (!wcArpThaMask) {
      values.put(ARP_THA_MASK, ValueFactory.createRawValue(arpThaMask));
    }

    if (!wcIpv6Src) {
      values.put(IPV6_SRC, ValueFactory.createRawValue(ipv6Src));
    }
    if (!wcIpv6SrcMask) {
      values.put(IPV6_SRC_MASK, ValueFactory.createRawValue(ipv6SrcMask));
    }
    if (!wcIpv6Dst) {
      values.put(IPV6_DST, ValueFactory.createRawValue(ipv6Dst));
    }
    if (!wcIpv6DstMask) {
      values.put(IPV6_DST_MASK, ValueFactory.createRawValue(ipv6DstMask));
    }
    if (!wcIpv6Flabel) {
      values.put(IPV6_FLABEL, ValueFactory.createIntegerValue(ipv6Flabel));
    }
    if (!wcIpv6FlabelMask) {
      values.put(IPV6_FLABEL_MASK, ValueFactory.createIntegerValue(ipv6FlabelMask));
    }
    if (!wcIcmpv6Type) {
      values.put(ICMPV6_TYPE, ValueFactory.createIntegerValue(icmpv6Type));
    }
    if (!wcIcmpv6Code) {
      values.put(ICMPV6_CODE, ValueFactory.createIntegerValue(icmpv6Code));
    }
    if (!wcIpv6NdTarget) {
      values.put(IPV6_ND_TARGET, ValueFactory.createRawValue(ipv6NdTarget));
    }
    if (!wcIpv6NdSll) {
      values.put(IPV6_ND_SLL, ValueFactory.createRawValue(ipv6NdSll));
    }
    if (!wcIpv6NdTll) {
      values.put(IPV6_ND_TLL, ValueFactory.createRawValue(ipv6NdTll));
    }
    if (!wcMplsLabel) {
      values.put(MPLS_LABEL, ValueFactory.createIntegerValue(mplsLabel));
    }
    if (!wcMplsTc) {
      values.put(MPLS_TC, ValueFactory.createIntegerValue(mplsTc));
    }
    if (!wcMplsBos) {
      values.put(MPLS_BOS, ValueFactory.createIntegerValue(mplsBos));
    }
    if (!wcPbbIsid) {
      values.put(PBB_ISID, ValueFactory.createIntegerValue(pbbIsid));
    }
    if (!wcPbbIsidMask) {
      values.put(PBB_ISID_MASK, ValueFactory.createIntegerValue(pbbIsidMask));
    }
    if (!wcTunnelId) {
      values.put(TUNNEL_ID, ValueFactory.createRawValue(tunnelId));
    }
    if (!wcTunnelIdMask) {
      values.put(TUNNEL_ID_MASK, ValueFactory.createRawValue(tunnelIdMask));
    }
    if (!wcIpv6Exthdr) {
      values.put(IPV6_EXTHDR, ValueFactory.createIntegerValue(ipv6Exthdr));
    }
    if (!wcIpv6ExthdrMask) {
      values.put(IPV6_EXTHDR_MASK, ValueFactory.createIntegerValue(ipv6ExthdrMask));
    }

    return true;
  }

  @Override
  public int hashCode() {

    HashCodeBuilder hsb = new HashCodeBuilder();
    hsb.append(inNode);
    hsb.append(inPort);

    hsb.append(inPhyPort);
    hsb.append(metadata);
    hsb.append(metadataMask);
    hsb.append(ethSrc);
    hsb.append(ethSrcMask);
    hsb.append(ethDst);
    hsb.append(ethDstMask);
    hsb.append(vlanVid);
    hsb.append(vlanVidMask);
    hsb.append(vlanPcp);
    hsb.append(ethType);

    hsb.append(ipDscp);
    hsb.append(ipEcn);
    hsb.append(ipProto);
    hsb.append(ipv4Src);
    hsb.append(ipv4SrcMask);
    hsb.append(ipv4Dst);
    hsb.append(ipv4DstMask);

    hsb.append(tcpSrc);
    hsb.append(tcpDst);
    hsb.append(udpSrc);
    hsb.append(udpDst);
    hsb.append(sctpSrc);
    hsb.append(sctpDst);
    hsb.append(icmpv4Type);
    hsb.append(icmpv4Code);
    hsb.append(arpOp);
    hsb.append(arpSpa);
    hsb.append(arpSpaMask);
    hsb.append(arpTpa);
    hsb.append(arpTpaMask);
    hsb.append(arpSha);
    hsb.append(arpShaMask);
    hsb.append(arpTha);
    hsb.append(arpThaMask);

    hsb.append(ipv6Src);
    hsb.append(ipv6SrcMask);
    hsb.append(ipv6Dst);
    hsb.append(ipv6DstMask);
    hsb.append(ipv6Flabel);
    hsb.append(ipv6FlabelMask);
    hsb.append(icmpv6Type);
    hsb.append(icmpv6Code);
    hsb.append(ipv6NdTarget);
    hsb.append(ipv6NdSll);
    hsb.append(ipv6NdTll);
    hsb.append(mplsLabel);
    hsb.append(mplsTc);
    hsb.append(mplsBos);
    hsb.append(pbbIsid);
    hsb.append(pbbIsidMask);
    hsb.append(tunnelId);
    hsb.append(tunnelIdMask);
    hsb.append(ipv6Exthdr);
    hsb.append(ipv6ExthdrMask);

    return hsb.toHashCode();

  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof OFPFlowMatch)) {
      return false;
    }

    if (!(super.equals(obj))) {
      return false;
    }

    final OFPFlowMatch obj2 = (OFPFlowMatch) obj;

    if (!StringUtils.equals(inNode, obj2.inNode)) {
      return false;
    }

    if (!StringUtils.equals(inPort, obj2.inPort)) {
      return false;
    }

    if (wcInPhyPort != obj2.wcInPhyPort) {
      return false;
    } else if (!wcInPhyPort) {
      if (!this.getInPhyPort().equals(obj2.getInPhyPort())) {
        return false;
      }
    }
    if (wcMetadata != obj2.wcMetadata) {
      return false;
    } else if (!wcMetadata) {
      if (!this.getMetadata().equals(obj2.getMetadata())) {
        return false;
      }
    }
    if (wcMetadataMask != obj2.wcMetadataMask) {
      return false;
    } else if (!wcMetadataMask) {
      if (!this.metadataMask.equals(obj2.getMetadataMask())) {
        return false;
      }
    }
    if (wcEthSrc != obj2.wcEthSrc) {
      return false;
    } else if (!wcEthSrc) {
      if (!this.ethSrc.equals(obj2.getEthSrc())) {
        return false;
      }
    }
    if (wcEthSrcMask != obj2.wcEthSrcMask) {
      return false;
    } else if (!wcEthSrcMask) {
      if (!this.ethSrcMask.equals(obj2.getEthSrcMask())) {
        return false;
      }
    }
    if (wcEthDst != obj2.wcEthDst) {
      return false;
    } else if (!wcEthDst) {
      if (!this.ethDst.equals(obj2.getEthDst())) {
        return false;
      }
    }
    if (wcEthDstMask != obj2.wcEthDstMask) {
      return false;
    } else if (!wcEthDstMask) {
      if (!this.ethDstMask.equals(obj2.getEthDstMask())) {
        return false;
      }
    }
    if (wcVlanVid != obj2.wcVlanVid) {
      return false;
    } else if (!wcVlanVid) {
      if (!this.vlanVid.equals(obj2.getVlanVid())) {
        return false;
      }
    }
    if (wcVlanVidMask != obj2.wcVlanVidMask) {
      return false;
    } else if (!wcVlanVidMask) {
      if (!this.vlanVidMask.equals(obj2.getVlanVidMask())) {
        return false;
      }
    }
    if (wcVlanPcp != obj2.wcVlanPcp) {
      return false;
    } else if (!wcVlanPcp) {
      if (!this.vlanPcp.equals(obj2.getVlanPcp())) {
        return false;
      }
    }
    if (wcEthType != obj2.wcEthType) {
      return false;
    } else if (!wcEthType) {
      if (!this.ethType.equals(obj2.getEthType())) {
        return false;
      }
    }

    if (wcIpDscp != obj2.wcIpDscp) {
      return false;
    } else if (!wcIpDscp) {
      if (!this.ipDscp.equals(obj2.getIpDscp())) {
        return false;
      }
    }
    if (wcIpEcn != obj2.wcIpEcn) {
      return false;
    } else if (!wcIpEcn) {
      if (!this.ipEcn.equals(obj2.getIpEcn())) {
        return false;
      }
    }
    if (wcIpProto != obj2.wcIpProto) {
      return false;
    } else if (!wcIpProto) {
      if (!this.ipProto.equals(obj2.getIpProto())) {
        return false;
      }
    }
    if (wcIpv4Src != obj2.wcIpv4Src) {
      return false;
    } else if (!wcIpv4Src) {
      if (!this.ipv4Src.equals(obj2.getIpv4Src())) {
        return false;
      }
    }
    if (wcIpv4SrcMask != obj2.wcIpv4SrcMask) {
      return false;
    } else if (!wcIpv4SrcMask) {
      if (!this.ipv4SrcMask.equals(obj2.getIpv4SrcMask())) {
        return false;
      }
    }
    if (wcIpv4Dst != obj2.wcIpv4Dst) {
      return false;
    } else if (!wcIpv4Dst) {
      if (!this.ipv4Dst.equals(obj2.getIpv4Dst())) {
        return false;
      }
    }
    if (wcIpv4DstMask != obj2.wcIpv4DstMask) {
      return false;
    } else if (!wcIpv4DstMask) {
      if (!this.ipv4DstMask.equals(obj2.getIpv4DstMask())) {
        return false;
      }
    }

    if (wcTcpSrc != obj2.wcTcpSrc) {
      return false;
    } else if (!wcTcpSrc) {
      if (!this.tcpSrc.equals(obj2.getTcpSrc())) {
        return false;
      }
    }
    if (wcTcpDst != obj2.wcTcpDst) {
      return false;
    } else if (!wcTcpDst) {
      if (!this.tcpDst.equals(obj2.getTcpDst())) {
        return false;
      }
    }
    if (wcUdpSrc != obj2.wcUdpSrc) {
      return false;
    } else if (!wcUdpSrc) {
      if (!this.udpSrc.equals(obj2.getUdpSrc())) {
        return false;
      }
    }
    if (wcUdpDst != obj2.wcUdpDst) {
      return false;
    } else if (!wcUdpDst) {
      if (!this.udpDst.equals(obj2.getUdpDst())) {
        return false;
      }
    }
    if (wcSctpSrc != obj2.wcSctpSrc) {
      return false;
    } else if (!wcSctpSrc) {
      if (!this.sctpSrc.equals(obj2.getSctpSrc())) {
        return false;
      }
    }
    if (wcSctpDst != obj2.wcSctpDst) {
      return false;
    } else if (!wcSctpDst) {
      if (!this.sctpDst.equals(obj2.getSctpDst())) {
        return false;
      }
    }
    if (wcIcmpv4Type != obj2.wcIcmpv4Type) {
      return false;
    } else if (!wcIcmpv4Type) {
      if (!this.icmpv4Type.equals(obj2.getIcmpv4Type())) {
        return false;
      }
    }
    if (wcIcmpv4Code != obj2.wcIcmpv4Code) {
      return false;
    } else if (!wcIcmpv4Code) {
      if (!this.icmpv4Code.equals(obj2.getIcmpv4Code())) {
        return false;
      }
    }

    if (wcArpOp != obj2.wcArpOp) {
      return false;
    } else if (!wcArpOp) {
      if (!this.arpOp.equals(obj2.getArpOp())) {
        return false;
      }
    }
    if (wcArpSpa != obj2.wcArpSpa) {
      return false;
    } else if (!wcArpSpa) {
      if (!this.arpSpa.equals(obj2.getArpSpa())) {
        return false;
      }
    }
    if (wcArpSpaMask != obj2.wcArpSpaMask) {
      return false;
    } else if (!wcArpSpaMask) {
      if (!this.arpSpaMask.equals(obj2.getArpSpaMask())) {
        return false;
      }
    }
    if (wcArpTpa != obj2.wcArpTpa) {
      return false;
    } else if (!wcArpTpa) {
      if (!this.arpTpa.equals(obj2.getArpTpa())) {
        return false;
      }
    }
    if (wcArpTpaMask != obj2.wcArpTpaMask) {
      return false;
    } else if (!wcArpTpaMask) {
      if (!this.arpTpaMask.equals(obj2.getArpTpaMask())) {
        return false;
      }
    }
    if (wcArpSha != obj2.wcArpSha) {
      return false;
    } else if (!wcArpSha) {
      if (!this.arpSha.equals(obj2.getArpSha())) {
        return false;
      }
    }
    if (wcArpShaMask != obj2.wcArpShaMask) {
      return false;
    } else if (!wcArpShaMask) {
      if (!this.arpShaMask.equals(obj2.getArpShaMask())) {
        return false;
      }
    }
    if (wcArpTha != obj2.wcArpTha) {
      return false;
    } else if (!wcArpTha) {
      if (!this.arpTha.equals(obj2.getArpTha())) {
        return false;
      }
    }
    if (wcArpThaMask != obj2.wcArpThaMask) {
      return false;
    } else if (!wcArpThaMask) {
      if (!this.arpThaMask.equals(obj2.getArpThaMask())) {
        return false;
      }
    }
    if (wcIpv6Src != obj2.wcIpv6Src) {
      return false;
    } else if (!wcIpv6Src) {
      if (!this.ipv6Src.equals(obj2.getIpv6Src())) {
        return false;
      }
    }
    if (wcIpv6SrcMask != obj2.wcIpv6SrcMask) {
      return false;
    } else if (!wcIpv6SrcMask) {
      if (!this.ipv6SrcMask.equals(obj2.getIpv6SrcMask())) {
        return false;
      }
    }

    if (wcIpv6Dst != obj2.wcIpv6Dst) {
      return false;
    } else if (!wcIpv6Dst) {
      if (!this.ipv6Dst.equals(obj2.getIpv6Dst())) {
        return false;
      }
    }
    if (wcIpv6DstMask != obj2.wcIpv6DstMask) {
      return false;
    } else if (!wcIpv6DstMask) {
      if (!this.ipv6DstMask.equals(obj2.getIpv6DstMask())) {
        return false;
      }
    }
    if (wcIpv6Flabel != obj2.wcIpv6Flabel) {
      return false;
    } else if (!wcIpv6Flabel) {
      if (!this.ipv6Flabel.equals(obj2.getIpv6Flabel())) {
        return false;
      }
    }
    if (wcIpv6FlabelMask != obj2.wcIpv6FlabelMask) {
      return false;
    } else if (!wcIpv6FlabelMask) {
      if (!this.ipv6FlabelMask.equals(obj2.getIpv6FlabelMask())) {
        return false;
      }
    }
    if (wcIcmpv6Type != obj2.wcIcmpv6Type) {
      return false;
    } else if (!wcIcmpv6Type) {
      if (!this.icmpv6Type.equals(obj2.getIcmpv6Type())) {
        return false;
      }
    }
    if (wcIcmpv6Code != obj2.wcIcmpv6Code) {
      return false;
    } else if (!wcIcmpv6Code) {
      if (!this.icmpv6Code.equals(obj2.getIcmpv6Code())) {
        return false;
      }
    }
    if (wcIpv6NdTarget != obj2.wcIpv6NdTarget) {
      return false;
    } else if (!wcIpv6NdTarget) {
      if (!this.ipv6NdTarget.equals(obj2.getIpv6NdTarget())) {
        return false;
      }
    }
    if (wcIpv6NdSll != obj2.wcIpv6NdSll) {
      return false;
    } else if (!wcIpv6NdSll) {
      if (!this.ipv6NdSll.equals(obj2.getIpv6NdSll())) {
        return false;
      }
    }
    if (wcIpv6NdTll != obj2.wcIpv6NdTll) {
      return false;
    } else if (!wcIpv6NdTll) {
      if (!this.ipv6NdTll.equals(obj2.getIpv6NdTll())) {
        return false;
      }
    }
    if (wcMplsLabel != obj2.wcMplsLabel) {
      return false;
    } else if (!wcMplsLabel) {
      if (!this.mplsLabel.equals(obj2.getMplsLabel())) {
        return false;
      }
    }
    if (wcMplsTc != obj2.wcMplsTc) {
      return false;
    } else if (!wcMplsTc) {
      if (!this.mplsTc.equals(obj2.getMplsTc())) {
        return false;
      }
    }
    if (wcMplsBos != obj2.wcMplsBos) {
      return false;
    } else if (!wcMplsBos) {
      if (!this.mplsBos.equals(obj2.getMplsBos())) {
        return false;
      }
    }
    if (wcPbbIsid != obj2.wcPbbIsid) {
      return false;
    } else if (!wcPbbIsid) {
      if (!this.pbbIsid.equals(obj2.getPbbIsid())) {
        return false;
      }
    }
    if (wcPbbIsidMask != obj2.wcPbbIsidMask) {
      return false;
    } else if (!wcPbbIsidMask) {
      if (!this.pbbIsidMask.equals(obj2.getPbbIsidMask())) {
        return false;
      }
    }
    if (wcTunnelId != obj2.wcTunnelId) {
      return false;
    } else if (!wcTunnelId) {
      if (!this.tunnelId.equals(obj2.getTunnelId())) {
        return false;
      }
    }
    if (wcTunnelIdMask != obj2.wcTunnelIdMask) {
      return false;
    } else if (!wcTunnelIdMask) {
      if (!this.tunnelIdMask.equals(obj2.getTunnelIdMask())) {
        return false;
      }
    }
    if (wcIpv6Exthdr != obj2.wcIpv6Exthdr) {
      return false;
    } else if (!wcIpv6Exthdr) {
      if (!this.ipv6Exthdr.equals(obj2.getIpv6Exthdr())) {
        return false;
      }
    }
    if (wcIpv6ExthdrMask != obj2.wcIpv6ExthdrMask) {
      return false;
    } else if (!wcIpv6ExthdrMask) {
      if (!this.ipv6ExthdrMask.equals(obj2.getIpv6ExthdrMask())) {
        return false;
      }
    }

    return true;
  }

  @Override
  public OFPFlowMatch clone() {
    OFPFlowMatch match = new OFPFlowMatch();
    match.setInNode(getInNode());

    if (!wcInPort) {
      match.setInPort(inPort);
    }
    if (!wcInPhyPort) {
      match.setInPhyPort(getInPhyPort());
    }
    if (!wcMetadata) {
      match.setMetadata(getMetadata());
    }
    if (!wcMetadataMask) {
      match.setMetadataMask(metadataMask);
    }
    if (!wcEthSrc) {
      match.setEthSrc(ethSrc);
    }
    if (!wcEthSrcMask) {
      match.setEthSrcMask(ethSrcMask);
    }
    if (!wcEthDst) {
      match.setEthDst(ethDst);
    }
    if (!wcEthDstMask) {
      match.setEthDstMask(ethDstMask);
    }
    if (!wcVlanVid) {
      match.setVlanVid(vlanVid);
    }
    if (!wcVlanVidMask) {
      match.setVlanVidMask(vlanVidMask);
    }
    if (!wcVlanPcp) {
      match.setVlanPcp(vlanPcp);
    }
    if (!wcEthType) {
      match.setEthType(ethType);
    }
    if (!wcIpProto) {
      match.setIpProto(ipProto);
    }
    if (!wcIpv4Src) {
      match.setIpv4Src(ipv4Src);
    }
    if (!wcIpv4SrcMask) {
      match.setIpv4SrcMask(ipv4SrcMask);
    }
    if (!wcIpv4Dst) {
      match.setIpv4Dst(ipv4Dst);
    }
    if (!wcIpv4DstMask) {
      match.setIpv4DstMask(ipv4DstMask);
    }
    if (!wcIpDscp) {
      match.setIpDscp(ipDscp);
    }
    if (!wcIpEcn) {
      match.setIpEcn(ipEcn);
    }
    if (!wcTcpSrc) {
      match.setTcpSrc(tcpSrc);
    }
    if (!wcTcpDst) {
      match.setTcpDst(tcpDst);
    }
    if (!wcUdpSrc) {
      match.setUdpSrc(udpSrc);
    }
    if (!wcUdpDst) {
      match.setUdpDst(udpDst);
    }
    if (!wcSctpSrc) {
      match.setSctpSrc(sctpSrc);
    }
    if (!wcSctpDst) {
      match.setSctpDst(sctpDst);
    }
    if (!wcIcmpv4Type) {
      match.setIcmpv4Type(icmpv4Type);
    }
    if (!wcIcmpv4Code) {
      match.setIcmpv4Code(icmpv4Code);
    }
    if (!wcArpOp) {
      match.setArpOp(arpOp);
    }
    if (!wcArpSpa) {
      match.setArpSpa(arpSpa);
    }
    if (!wcArpSpaMask) {
      match.setArpSpaMask(arpSpaMask);
    }
    if (!wcArpTpa) {
      match.setArpTpa(arpTpa);
    }
    if (!wcArpTpaMask) {
      match.setArpTpaMask(arpTpaMask);
    }
    if (!wcArpSha) {
      match.setArpSha(arpSha);
    }
    if (!wcArpShaMask) {
      match.setArpShaMask(arpShaMask);
    }
    if (!wcArpTha) {
      match.setArpTha(arpTha);
    }
    if (!wcArpThaMask) {
      match.setArpThaMask(arpThaMask);
    }
    if (!wcIpv6Src) {
      match.setIpv6Src(ipv6Src);
    }
    if (!wcIpv6SrcMask) {
      match.setIpv6SrcMask(ipv6SrcMask);
    }
    if (!wcIpv6Dst) {
      match.setIpv6Dst(ipv6Dst);
    }
    if (!wcIpv6DstMask) {
      match.setIpv6DstMask(ipv6DstMask);
    }
    if (!wcIpv6Flabel) {
      match.setIpv6Flabel(ipv6Flabel);
    }
    if (!wcIpv6FlabelMask) {
      match.setIpv6FlabelMask(ipv6FlabelMask);
    }
    if (!wcIcmpv6Type) {
      match.setIcmpv6Type(icmpv6Type);
    }
    if (!wcIcmpv6Code) {
      match.setIcmpv6Code(icmpv6Code);
    }
    if (!wcIpv6NdTarget) {
      match.setIpv6NdTarget(ipv6NdTarget);
    }
    if (!wcIpv6NdSll) {
      match.setIpv6NdSll(ipv6NdSll);
    }
    if (!wcIpv6NdTll) {
      match.setIpv6NdTll(ipv6NdTll);
    }
    if (!wcMplsLabel) {
      match.setMplsLabel(mplsLabel);
    }
    if (!wcMplsTc) {
      match.setMplsTc(mplsTc);
    }
    if (!wcMplsBos) {
      match.setMplsBos(mplsBos);
    }
    if (!wcPbbIsid) {
      match.setPbbIsid(pbbIsid);
    }
    if (!wcPbbIsidMask) {
      match.setPbbIsidMask(pbbIsidMask);
    }
    if (!wcTunnelId) {
      match.setTunnelId(tunnelId);
    }
    if (!wcTunnelIdMask) {
      match.setTunnelIdMask(tunnelIdMask);
    }
    if (!wcIpv6Exthdr) {
      match.setIpv6Exthdr(ipv6Exthdr);
    }
    if (!wcIpv6ExthdrMask) {
      match.setIpv6ExthdrMask(ipv6ExthdrMask);
    }
    return match;
  }

  /* (non-Javadoc)
   * @see org.o3project.odenos.component.network.flow.basic.BasicFlowMatch#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("inNode", inNode);

    if (!wcInPort) {
      sb.append("inPort", inPort);
    }
    if (!wcInPhyPort) {
      sb.append("inPhyPort", getInPhyPort());
    }
    if (!wcMetadata) {
      sb.append("metadata", getMetadata());
    }
    if (!wcMetadataMask) {
      sb.append("metadataMask", metadataMask);
    }
    if (!wcEthSrc) {
      sb.append("ethSrc", ethSrc);
    }
    if (!wcEthSrcMask) {
      sb.append("ethSrcMask", ethSrcMask);
    }
    if (!wcEthDst) {
      sb.append("ethDst", ethDst);
    }
    if (!wcEthDstMask) {
      sb.append("ethDstMask", ethDstMask);
    }
    if (!wcEthType) {
      sb.append("ethType", ethType);
    }
    if (!wcVlanVid) {
      sb.append("vlanVid", vlanVid);
    }
    if (!wcVlanVidMask) {
      sb.append("vlanVidMask", vlanVidMask);
    }
    if (!wcVlanPcp) {
      sb.append("vlanPcp", vlanPcp);
    }
    if (!wcIpDscp) {
      sb.append("ipDscp", ipDscp);
    }
    if (!wcIpEcn) {
      sb.append("ipEcn", ipEcn);
    }
    if (!wcIpProto) {
      sb.append("ipProto", ipProto);
    }
    if (!wcIpv4Src) {
      sb.append("ipv4Src", ipv4Src);
    }
    if (!wcIpv4SrcMask) {
      sb.append("ipv4SrcMask", ipv4SrcMask);
    }
    if (!wcIpv4Dst) {
      sb.append("ipv4Dst", ipv4Dst);
    }
    if (!wcIpv4DstMask) {
      sb.append("ipv4DstMask", ipv4DstMask);
    }

    if (!wcTcpSrc) {
      sb.append("tcpSrc", tcpSrc);
    }
    if (!wcTcpDst) {
      sb.append("tcpDst", tcpDst);
    }
    if (!wcUdpSrc) {
      sb.append("udpSrc", udpSrc);
    }
    if (!wcUdpDst) {
      sb.append("udpDst", udpDst);
    }
    if (!wcSctpSrc) {
      sb.append("sctpSrc", sctpSrc);
    }
    if (!wcSctpDst) {
      sb.append("sctpDst", sctpDst);
    }
    if (!wcIcmpv4Type) {
      sb.append("icmpv4Type", icmpv4Type);
    }
    if (!wcIcmpv4Code) {
      sb.append("icmpv4Code", icmpv4Code);
    }

    if (!wcArpOp) {
      sb.append("arpOp", arpOp);
    }
    if (!wcArpSpa) {
      sb.append("arpSpa", arpSpa);
    }
    if (!wcArpSpaMask) {
      sb.append("arpSpaMask", arpSpaMask);
    }
    if (!wcArpTpa) {
      sb.append("arpTpa", arpTpa);
    }
    if (!wcArpTpaMask) {
      sb.append("arpTpaMask", arpTpaMask);
    }
    if (!wcArpSha) {
      sb.append("arpSha", arpSha);
    }
    if (!wcArpShaMask) {
      sb.append("arpShaMask", arpShaMask);
    }
    if (!wcArpTha) {
      sb.append("arpTha", arpTha);
    }
    if (!wcArpThaMask) {
      sb.append("arpThaMask", arpThaMask);
    }

    if (!wcIpv6Src) {
      sb.append("ipv6Src", ipv6Src);
    }
    if (!wcIpv6SrcMask) {
      sb.append("ipv6SrcMask", ipv6SrcMask);
    }
    if (!wcIpv6Dst) {
      sb.append("ipv6Dst", ipv6Dst);
    }
    if (!wcIpv6DstMask) {
      sb.append("ipv6DstMask", ipv6DstMask);
    }
    if (!wcIpv6Flabel) {
      sb.append("ipv6Flabel", ipv6Flabel);
    }
    if (!wcIpv6FlabelMask) {
      sb.append("ipv6FlabelMask", ipv6FlabelMask);
    }
    if (!wcIcmpv6Type) {
      sb.append("icmpv6Type", icmpv6Type);
    }
    if (!wcIcmpv6Code) {
      sb.append("icmpv6Code", icmpv6Code);
    }
    if (!wcIpv6NdTarget) {
      sb.append("ipv6NdTarget", ipv6NdTarget);
    }
    if (!wcIpv6NdSll) {
      sb.append("ipv6NdSll", ipv6NdSll);
    }
    if (!wcIpv6NdTll) {
      sb.append("ipv6NdTll", ipv6NdTll);
    }
    if (!wcMplsLabel) {
      sb.append("mplsLabel", mplsLabel);
    }
    if (!wcMplsTc) {
      sb.append("mplsTc", mplsTc);
    }
    if (!wcMplsBos) {
      sb.append("mplsBos", mplsBos);
    }
    if (!wcPbbIsid) {
      sb.append("pbbIsid", pbbIsid);
    }
    if (!wcPbbIsidMask) {
      sb.append("pbbIsidMask", pbbIsidMask);
    }
    if (!wcTunnelId) {
      sb.append("tunnelId", tunnelId);
    }
    if (!wcTunnelIdMask) {
      sb.append("tunnelIdMask", tunnelIdMask);
    }
    if (!wcIpv6Exthdr) {
      sb.append("ipv6Exthdr", ipv6Exthdr);
    }
    if (!wcIpv6ExthdrMask) {
      sb.append("ipv6ExthdrMask", ipv6ExthdrMask);
    }

    return sb.toString();
  }

}
