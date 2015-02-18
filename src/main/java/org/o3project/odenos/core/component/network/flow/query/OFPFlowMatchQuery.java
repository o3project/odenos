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

import org.o3project.odenos.core.component.network.BasicQuery;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;

import java.util.Map;
import java.util.Objects;

/**
 * Prepares a query for OFPFlowMatch class.
 *
 */
public class OFPFlowMatchQuery
    extends BasicFlowMatchQuery {

  public Long inPhyPort = null;
  public String metadata = null;
  public String metadataMask = null;
  public String ethSrc = null;
  public String ethSrcMask = null;
  public String ethDst = null;
  public String ethDstMask = null;
  public Integer vlanVid = null;
  public Integer vlanVidMask = null;
  public Integer vlanPcp = null;
  public Integer ethType = null;

  public Integer ipDscp = null; // 6bit in Tos field
  public Integer ipEcn = null; // 2bit in Tos field
  public Integer ipProto = null;
  public String ipv4Src = null;
  public String ipv4SrcMask = null;
  public String ipv4Dst = null;
  public String ipv4DstMask = null;

  public Integer tcpSrc = null;
  public Integer tcpDst = null;
  public Integer udpSrc = null;
  public Integer udpDst = null;
  public Integer sctpSrc = null;
  public Integer sctpDst = null;
  public Integer icmpv4Type = null;
  public Integer icmpv4Code = null;
  public Integer arpOp = null;
  public String arpSpa = null;
  public String arpSpaMask = null;
  public String arpTpa = null;
  public String arpTpaMask = null;
  public String arpSha = null;
  public String arpShaMask = null;
  public String arpTha = null;
  public String arpThaMask = null;

  public String ipv6Src = null;
  public String ipv6SrcMask = null;
  public String ipv6Dst = null;
  public String ipv6DstMask = null;
  public Long ipv6Flabel = null;
  public Long ipv6FlabelMask = null;
  public Integer icmpv6Type = null;
  public Integer icmpv6Code = null;
  public String ipv6NdTarget = null;
  public String ipv6NdSll = null;
  public String ipv6NdTll = null;
  public Long mplsLabel = null;
  public Integer mplsTc = null;
  public Integer mplsBos = null;
  public Long pbbIsid = null;
  public Long pbbIsidMask = null;
  public String tunnelId = null;
  public String tunnelIdMask = null;
  public Integer ipv6Exthdr = null;
  public Integer ipv6ExthdrMask = null;

  /**
   * Constructor.
   * @param params match conditions.
   */
  public OFPFlowMatchQuery(Map<String, String> params) {
    super(params);
  }

  @Override
  public boolean parse() {

    if (this.match == null) {
      return true;
    }

    if (this.match.containsKey(OFPFlowMatch.IN_PHY_PORT)) {
      if ((this.inPhyPort = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.IN_PHY_PORT)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IN_PHY_PORT);
    }
    this.metadata = this.match.remove(OFPFlowMatch.METADATA);
    this.metadataMask = this.match.remove(OFPFlowMatch.METADATA_MASK);
    this.ethSrc = this.match.remove(OFPFlowMatch.ETH_SRC);
    this.ethSrcMask = this.match.remove(OFPFlowMatch.ETH_SRC_MASK);
    this.ethDst = this.match.remove(OFPFlowMatch.ETH_DST);
    this.ethDstMask = this.match.remove(OFPFlowMatch.ETH_DST_MASK);
    if (this.match.containsKey(OFPFlowMatch.VLAN_VID)) {
      if ((this.vlanVid = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.VLAN_VID)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.VLAN_VID);
    }
    if (this.match.containsKey(OFPFlowMatch.VLAN_VID_MASK)) {
      if ((this.vlanVidMask = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.VLAN_VID_MASK)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.VLAN_VID_MASK);
    }
    if (this.match.containsKey(OFPFlowMatch.VLAN_PCP)) {
      if ((this.vlanPcp = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.VLAN_PCP)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.VLAN_PCP);
    }
    if (this.match.containsKey(OFPFlowMatch.ETH_TYPE)) {
      if ((this.ethType = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ETH_TYPE)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ETH_TYPE);
    }

    if (this.match.containsKey(OFPFlowMatch.IP_DSCP)) {
      if ((this.ipDscp = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.IP_DSCP)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IP_DSCP);
    }
    if (this.match.containsKey(OFPFlowMatch.IP_ECN)) {
      if ((this.ipEcn = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.IP_ECN)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IP_ECN);
    }
    if (this.match.containsKey(OFPFlowMatch.IP_PROTO)) {
      if ((this.ipProto = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.IP_PROTO)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IP_PROTO);
    }
    this.ipv4Src = this.match.remove(OFPFlowMatch.IPV4_SRC);
    this.ipv4SrcMask = this.match.remove(OFPFlowMatch.IPV4_SRC_MASK);
    this.ipv4Dst = this.match.remove(OFPFlowMatch.IPV4_DST);
    this.ipv4DstMask = this.match.remove(OFPFlowMatch.IPV4_DST_MASK);
    if (this.match.containsKey(OFPFlowMatch.TCP_SRC)) {
      if ((this.tcpSrc = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.TCP_SRC)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.TCP_SRC);
    }
    if (this.match.containsKey(OFPFlowMatch.TCP_DST)) {
      if ((this.tcpDst = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.TCP_DST)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.TCP_DST);
    }
    if (this.match.containsKey(OFPFlowMatch.UDP_SRC)) {
      if ((this.udpSrc = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.UDP_SRC)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.UDP_SRC);
    }
    if (this.match.containsKey(OFPFlowMatch.UDP_DST)) {
      if ((this.udpDst = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.UDP_DST)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.UDP_DST);
    }
    if (this.match.containsKey(OFPFlowMatch.SCTP_SRC)) {
      if ((this.sctpSrc = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.SCTP_SRC)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.SCTP_SRC);
    }
    if (this.match.containsKey(OFPFlowMatch.SCTP_DST)) {
      if ((this.sctpDst = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.SCTP_DST)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.SCTP_DST);
    }
    if (this.match.containsKey(OFPFlowMatch.ICMPV4_TYPE)) {
      if ((this.icmpv4Type = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ICMPV4_TYPE)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ICMPV4_TYPE);
    }
    if (this.match.containsKey(OFPFlowMatch.ICMPV4_CODE)) {
      if ((this.icmpv4Code = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ICMPV4_CODE)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ICMPV4_CODE);
    }

    if (this.match.containsKey(OFPFlowMatch.ARP_OP)) {
      if ((this.arpOp = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ARP_OP)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ARP_OP);
    }
    this.arpSpa = this.match.remove(OFPFlowMatch.ARP_SPA);
    this.arpSpaMask = this.match.remove(OFPFlowMatch.ARP_SPA_MASK);
    this.arpTpa = this.match.remove(OFPFlowMatch.ARP_TPA);
    this.arpTpaMask = this.match.remove(OFPFlowMatch.ARP_TPA_MASK);
    this.arpSha = this.match.remove(OFPFlowMatch.ARP_SHA);
    this.arpShaMask = this.match.remove(OFPFlowMatch.ARP_SHA_MASK);
    this.arpTha = this.match.remove(OFPFlowMatch.ARP_THA);
    this.arpThaMask = this.match.remove(OFPFlowMatch.ARP_THA_MASK);
    this.ipv6Src = this.match.remove(OFPFlowMatch.IPV6_SRC);
    this.ipv6SrcMask = this.match.remove(OFPFlowMatch.IPV6_SRC_MASK);
    this.ipv6Dst = this.match.remove(OFPFlowMatch.IPV6_DST);
    this.ipv6DstMask = this.match.remove(OFPFlowMatch.IPV6_DST_MASK);
    if (this.match.containsKey(OFPFlowMatch.IPV6_FLABEL)) {
      if ((this.ipv6Flabel = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.IPV6_FLABEL)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IPV6_FLABEL);
    }
    if (this.match.containsKey(OFPFlowMatch.IPV6_FLABEL_MASK)) {
      if ((this.ipv6FlabelMask = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.IPV6_FLABEL_MASK)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IPV6_FLABEL_MASK);
    }
    if (this.match.containsKey(OFPFlowMatch.ICMPV6_TYPE)) {
      if ((this.icmpv6Type = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ICMPV6_TYPE)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ICMPV6_TYPE);
    }
    if (this.match.containsKey(OFPFlowMatch.ICMPV6_CODE)) {
      if ((this.icmpv6Code = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.ICMPV6_CODE)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.ICMPV6_CODE);
    }
    this.ipv6NdTarget = this.match.remove(OFPFlowMatch.IPV6_ND_TARGET);
    this.ipv6NdSll = this.match.remove(OFPFlowMatch.IPV6_ND_SLL);
    this.ipv6NdTll = this.match.remove(OFPFlowMatch.IPV6_ND_TLL);
    if (this.match.containsKey(OFPFlowMatch.MPLS_LABEL)) {
      if ((this.mplsLabel = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.MPLS_LABEL)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.MPLS_LABEL);
    }
    if (this.match.containsKey(OFPFlowMatch.MPLS_TC)) {
      if ((this.mplsTc = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.MPLS_TC)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.MPLS_TC);
    }
    if (this.match.containsKey(OFPFlowMatch.MPLS_BOS)) {
      if ((this.mplsBos = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.MPLS_BOS)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.MPLS_BOS);
    }
    if (this.match.containsKey(OFPFlowMatch.PBB_ISID)) {
      if ((this.pbbIsid = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.PBB_ISID)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.PBB_ISID);
    }
    if (this.match.containsKey(OFPFlowMatch.PBB_ISID_MASK)) {
      if ((this.pbbIsidMask = BasicQuery.cretateLong(
          this.match, OFPFlowMatch.PBB_ISID_MASK)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.PBB_ISID_MASK);
    }
    this.tunnelId = this.match.remove(OFPFlowMatch.TUNNEL_ID);
    this.tunnelIdMask = this.match.remove(OFPFlowMatch.TUNNEL_ID_MASK);
    if (this.match.containsKey(OFPFlowMatch.IPV6_EXTHDR)) {
      if ((this.ipv6Exthdr = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.IPV6_EXTHDR)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IPV6_EXTHDR);
    }
    if (this.match.containsKey(OFPFlowMatch.IPV6_EXTHDR_MASK)) {
      if ((this.ipv6ExthdrMask = BasicQuery.cretateInteger(
          this.match, OFPFlowMatch.IPV6_EXTHDR_MASK)) == null) {
        return false;
      }
      this.match.remove(OFPFlowMatch.IPV6_EXTHDR_MASK);
    }

    if (!super.parse()) {
      return false;
    }

    return this.match.size() == 0;
  }

  @Override
  public boolean matchExactly(BasicFlowMatch match) {
    if (match == null || !match.getType().equals(
        OFPFlowMatch.class.getSimpleName())) {
      return false;
    }
    OFPFlowMatch target = (OFPFlowMatch) match;
    if (!super.matchExactly(target)) {
      return false;
    }

    if (isInvalidParameter(this.inPhyPort, target.getInPhyPort())) {
      return false;
    }
    if (isInvalidParameter(this.metadata, target.getMetadata())) {
      return false;
    }
    if (isInvalidParameter(this.metadataMask, target.getMetadataMask())) {
      return false;
    }
    if (isInvalidParameter(this.ethSrc, target.getEthSrc())) {
      return false;
    }
    if (isInvalidParameter(this.ethSrcMask, target.getEthSrcMask())) {
      return false;
    }
    if (isInvalidParameter(this.ethDst, target.getEthDst())) {
      return false;
    }
    if (isInvalidParameter(this.ethDstMask, target.getEthDstMask())) {
      return false;
    }
    if (isInvalidParameter(this.vlanVid, target.getVlanVid())) {
      return false;
    }
    if (isInvalidParameter(this.vlanVidMask, target.getVlanVidMask())) {
      return false;
    }
    if (isInvalidParameter(this.vlanPcp, target.getVlanPcp())) {
      return false;
    }
    if (isInvalidParameter(this.ethType, target.getEthType())) {
      return false;
    }
    if (isInvalidParameter(this.ipDscp, target.getIpDscp())) {
      return false;
    }
    if (isInvalidParameter(this.ipEcn, target.getIpEcn())) {
      return false;
    }
    if (isInvalidParameter(this.ipProto, target.getIpProto())) {
      return false;
    }
    if (isInvalidParameter(this.ipv4Src, target.getIpv4Src())) {
      return false;
    }
    if (isInvalidParameter(this.ipv4SrcMask, target.getIpv4SrcMask())) {
      return false;
    }
    if (isInvalidParameter(this.ipv4Dst, target.getIpv4Dst())) {
      return false;
    }
    if (isInvalidParameter(this.ipv4DstMask, target.getIpv4DstMask())) {
      return false;
    }
    if (isInvalidParameter(this.tcpSrc, target.getTcpSrc())) {
      return false;
    }
    if (isInvalidParameter(this.tcpDst, target.getTcpDst())) {
      return false;
    }
    if (isInvalidParameter(this.udpSrc, target.getUdpSrc())) {
      return false;
    }
    if (isInvalidParameter(this.udpDst, target.getUdpDst())) {
      return false;
    }
    if (isInvalidParameter(this.sctpSrc, target.getSctpSrc())) {
      return false;
    }
    if (isInvalidParameter(this.sctpDst, target.getSctpDst())) {
      return false;
    }
    if (isInvalidParameter(this.icmpv4Type, target.getIcmpv4Type())) {
      return false;
    }
    if (isInvalidParameter(this.icmpv4Code, target.getIcmpv4Code())) {
      return false;
    }
    if (isInvalidParameter(this.arpOp, target.getArpOp())) {
      return false;
    }
    if (isInvalidParameter(this.arpSpa, target.getArpSpa())) {
      return false;
    }
    if (isInvalidParameter(this.arpSpaMask, target.getArpSpaMask())) {
      return false;
    }
    if (isInvalidParameter(this.arpTpa, target.getArpTpa())) {
      return false;
    }
    if (isInvalidParameter(this.arpTpaMask, target.getArpTpaMask())) {
      return false;
    }
    if (isInvalidParameter(this.arpSha, target.getArpSha())) {
      return false;
    }
    if (isInvalidParameter(this.arpShaMask, target.getArpShaMask())) {
      return false;
    }
    if (isInvalidParameter(this.arpTha, target.getArpTha())) {
      return false;
    }
    if (isInvalidParameter(this.arpThaMask, target.getArpThaMask())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6Src, target.getIpv6Src())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6SrcMask, target.getIpv6SrcMask())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6Dst, target.getIpv6Dst())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6DstMask, target.getIpv6DstMask())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6Flabel, target.getIpv6Flabel())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6FlabelMask, target.getIpv6FlabelMask())) {
      return false;
    }
    if (isInvalidParameter(this.icmpv6Type, target.getIcmpv6Type())) {
      return false;
    }
    if (isInvalidParameter(this.icmpv6Code, target.getIcmpv6Code())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6NdTarget, target.getIpv6NdTarget())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6NdSll, target.getIpv6NdSll())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6NdTll, target.getIpv6NdTll())) {
      return false;
    }
    if (isInvalidParameter(this.mplsLabel, target.getMplsLabel())) {
      return false;
    }
    if (isInvalidParameter(this.mplsTc, target.getMplsTc())) {
      return false;
    }
    if (isInvalidParameter(this.mplsBos, target.getMplsBos())) {
      return false;
    }
    if (isInvalidParameter(this.pbbIsid, target.getPbbIsid())) {
      return false;
    }
    if (isInvalidParameter(this.pbbIsidMask, target.getPbbIsidMask())) {
      return false;
    }
    if (isInvalidParameter(this.tunnelId, target.getTunnelId())) {
      return false;
    }
    if (isInvalidParameter(this.tunnelIdMask, target.getTunnelIdMask())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6Exthdr, target.getIpv6Exthdr())) {
      return false;
    }
    if (isInvalidParameter(this.ipv6ExthdrMask, target.getIpv6ExthdrMask())) {
      return false;
    }
    return true;
  }

  /**
   * check invalid parameters.
   * @param target1 value of query.
   * @param target2 value of match.
   * @return boolean
   */
  private boolean isInvalidParameter(Object target1, Object target2) {
    if (target1 == null) {
      return false;
    }
    return !Objects.equals(target1, target2);
  }

}
