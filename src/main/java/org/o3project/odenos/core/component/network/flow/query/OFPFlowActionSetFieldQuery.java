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

import org.apache.commons.lang.ObjectUtils;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;

import java.util.Map;

/**
 * Prepares a query for OFPFlowActionSetField class.
 */
public class OFPFlowActionSetFieldQuery extends FlowActionQuery {

  private OFPFlowMatch match;

  /**
   * Constructor.
   * @param params action conditions.
   */
  public OFPFlowActionSetFieldQuery(Map<String, String> params) {
    super(params);
    match = new OFPFlowMatch();
  }

  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    try {
      if (actions.containsKey(OFPFlowMatch.IN_PORT)) {
        match.setInPort(actions.remove(OFPFlowMatch.IN_PORT));
      }
      if (actions.containsKey(OFPFlowMatch.IN_PHY_PORT)) {
        match.setInPhyPort(Long.parseLong(actions.remove(OFPFlowMatch.IN_PHY_PORT)));
      }
      if (actions.containsKey(OFPFlowMatch.METADATA)) {
        match.setMetadata(actions.remove(OFPFlowMatch.METADATA));
      }
      if (actions.containsKey(OFPFlowMatch.METADATA_MASK)) {
        match.setMetadataMask(actions.remove(OFPFlowMatch.METADATA_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.ETH_SRC)) {
        match.setEthSrc(actions.remove(OFPFlowMatch.ETH_SRC));
      }
      if (actions.containsKey(OFPFlowMatch.ETH_SRC_MASK)) {
        match.setEthSrcMask(actions.remove(OFPFlowMatch.ETH_SRC_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.ETH_DST)) {
        match.setEthDst(actions.remove(OFPFlowMatch.ETH_DST));
      }
      if (actions.containsKey(OFPFlowMatch.ETH_DST_MASK)) {
        match.setEthDstMask(actions.remove(OFPFlowMatch.ETH_DST_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.VLAN_VID)) {
        match.setVlanVid(Integer.parseInt(actions.remove(OFPFlowMatch.VLAN_VID)));
      }
      if (actions.containsKey(OFPFlowMatch.VLAN_PCP)) {
        match.setVlanPcp(Integer.parseInt(actions.remove(OFPFlowMatch.VLAN_PCP)));
      }
      if (actions.containsKey(OFPFlowMatch.ETH_TYPE)) {
        match.setEthType(Integer.parseInt(actions.remove(OFPFlowMatch.ETH_TYPE)));
      }
      if (actions.containsKey(OFPFlowMatch.IP_DSCP)) {
        match.setIpDscp(Integer.parseInt(actions.remove(OFPFlowMatch.IP_DSCP)));
      }
      if (actions.containsKey(OFPFlowMatch.IP_ECN)) {
        match.setIpEcn(Integer.parseInt(actions.remove(OFPFlowMatch.IP_ECN)));
      }
      if (actions.containsKey(OFPFlowMatch.IP_PROTO)) {
        match.setIpProto(Integer.parseInt(actions.remove(OFPFlowMatch.IP_PROTO)));
      }
      if (actions.containsKey(OFPFlowMatch.IPV4_SRC)) {
        match.setIpv4Src(actions.remove(OFPFlowMatch.IPV4_SRC));
      }
      if (actions.containsKey(OFPFlowMatch.IPV4_SRC_MASK)) {
        match.setIpv4SrcMask(actions.remove(OFPFlowMatch.IPV4_SRC_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.IPV4_DST)) {
        match.setIpv4Dst(actions.remove(OFPFlowMatch.IPV4_DST));
      }
      if (actions.containsKey(OFPFlowMatch.IPV4_DST_MASK)) {
        match.setIpv4DstMask(actions.remove(OFPFlowMatch.IPV4_DST_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.TCP_SRC)) {
        match.setTcpSrc(Integer.parseInt(actions.remove(OFPFlowMatch.TCP_SRC)));
      }
      if (actions.containsKey(OFPFlowMatch.TCP_DST)) {
        match.setTcpDst(Integer.parseInt(actions.remove(OFPFlowMatch.TCP_DST)));
      }
      if (actions.containsKey(OFPFlowMatch.UDP_SRC)) {
        match.setUdpSrc(Integer.parseInt(actions.remove(OFPFlowMatch.UDP_SRC)));
      }
      if (actions.containsKey(OFPFlowMatch.UDP_DST)) {
        match.setUdpDst(Integer.parseInt(actions.remove(OFPFlowMatch.UDP_DST)));
      }
      if (actions.containsKey(OFPFlowMatch.SCTP_SRC)) {
        match.setSctpSrc(Integer.parseInt(actions.remove(OFPFlowMatch.SCTP_SRC)));
      }
      if (actions.containsKey(OFPFlowMatch.SCTP_DST)) {
        match.setSctpDst(Integer.parseInt(actions.remove(OFPFlowMatch.SCTP_DST)));
      }
      if (actions.containsKey(OFPFlowMatch.ICMPV4_TYPE)) {
        match.setIcmpv4Type(Integer.parseInt(actions.remove(OFPFlowMatch.ICMPV4_TYPE)));
      }
      if (actions.containsKey(OFPFlowMatch.ICMPV4_CODE)) {
        match.setIcmpv4Code(Integer.parseInt(actions.remove(OFPFlowMatch.ICMPV4_CODE)));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_OP)) {
        match.setArpOp(Integer.parseInt(actions.remove(OFPFlowMatch.ARP_OP)));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_SPA)) {
        match.setArpSpa(actions.remove(OFPFlowMatch.ARP_SPA));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_SPA_MASK)) {
        match.setArpSpaMask(actions.remove(OFPFlowMatch.ARP_SPA_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_TPA)) {
        match.setArpTpa(actions.remove(OFPFlowMatch.ARP_TPA));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_TPA_MASK)) {
        match.setArpTpaMask(actions.remove(OFPFlowMatch.ARP_TPA_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_SHA)) {
        match.setArpSha(actions.remove(OFPFlowMatch.ARP_SHA));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_SHA_MASK)) {
        match.setArpShaMask(actions.remove(OFPFlowMatch.ARP_SHA_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_THA)) {
        match.setArpTha(actions.remove(OFPFlowMatch.ARP_THA));
      }
      if (actions.containsKey(OFPFlowMatch.ARP_THA_MASK)) {
        match.setArpThaMask(actions.remove(OFPFlowMatch.ARP_THA_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_SRC)) {
        match.setIpv6Src(actions.remove(OFPFlowMatch.IPV6_SRC));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_SRC_MASK)) {
        match.setIpv6SrcMask(actions.remove(OFPFlowMatch.IPV6_SRC_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_DST)) {
        match.setIpv6Dst(actions.remove(OFPFlowMatch.IPV6_DST));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_DST_MASK)) {
        match.setIpv6DstMask(actions.remove(OFPFlowMatch.IPV6_DST_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_FLABEL)) {
        match.setIpv6Flabel(Long.parseLong(actions.remove(OFPFlowMatch.IPV6_FLABEL)));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_FLABEL_MASK)) {
        match.setIpv6FlabelMask(Long.parseLong(actions.remove(OFPFlowMatch.IPV6_FLABEL_MASK)));
      }
      if (actions.containsKey(OFPFlowMatch.ICMPV6_TYPE)) {
        match.setIcmpv6Type(Integer.parseInt(actions.remove(OFPFlowMatch.ICMPV6_TYPE)));
      }
      if (actions.containsKey(OFPFlowMatch.ICMPV6_CODE)) {
        match.setIcmpv6Code(Integer.parseInt(actions.remove(OFPFlowMatch.ICMPV6_CODE)));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_ND_TARGET)) {
        match.setIpv6NdTarget(actions.remove(OFPFlowMatch.IPV6_ND_TARGET));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_ND_SLL)) {
        match.setIpv6NdSll(actions.remove(OFPFlowMatch.IPV6_ND_SLL));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_ND_TLL)) {
        match.setIpv6NdTll(actions.remove(OFPFlowMatch.IPV6_ND_TLL));
      }
      if (actions.containsKey(OFPFlowMatch.MPLS_LABEL)) {
        match.setMplsLabel(Long.parseLong(actions.remove(OFPFlowMatch.MPLS_LABEL)));
      }
      if (actions.containsKey(OFPFlowMatch.MPLS_TC)) {
        match.setMplsTc(Integer.parseInt(actions.remove(OFPFlowMatch.MPLS_TC)));
      }
      if (actions.containsKey(OFPFlowMatch.MPLS_BOS)) {
        match.setMplsBos(Integer.parseInt(actions.remove(OFPFlowMatch.MPLS_BOS)));
      }
      if (actions.containsKey(OFPFlowMatch.PBB_ISID)) {
        match.setPbbIsid(Long.parseLong(actions.remove(OFPFlowMatch.PBB_ISID)));
      }
      if (actions.containsKey(OFPFlowMatch.PBB_ISID_MASK)) {
        match.setPbbIsidMask(Long.parseLong(actions.remove(OFPFlowMatch.PBB_ISID_MASK)));
      }
      if (actions.containsKey(OFPFlowMatch.TUNNEL_ID)) {
        match.setTunnelId(actions.remove(OFPFlowMatch.TUNNEL_ID));
      }
      if (actions.containsKey(OFPFlowMatch.TUNNEL_ID_MASK)) {
        match.setTunnelIdMask(actions.remove(OFPFlowMatch.TUNNEL_ID_MASK));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_EXTHDR)) {
        match.setIpv6Exthdr(Integer.parseInt(actions.remove(OFPFlowMatch.IPV6_EXTHDR)));
      }
      if (actions.containsKey(OFPFlowMatch.IPV6_EXTHDR_MASK)) {
        match.setIpv6ExthdrMask(Integer.parseInt(actions.remove(OFPFlowMatch.IPV6_EXTHDR_MASK)));
      }
    } catch (NumberFormatException ex) {
      return false;
    }
    if (actions.size() != 0) {
      return false;
    }
    return true;
  }

  @Override
  public boolean matchExactly(FlowAction action) {
    if (action == null || !action.getType().equals(
        OFPFlowActionSetField.class.getSimpleName())) {
      return false;
    }
    OFPFlowActionSetField target = (OFPFlowActionSetField) action;
    if (this.match == null) {
      return true;
    }
    if (target.getMatch() == null) {
      return false;
    }
    OFPFlowMatch targetMatch = target.getMatch();

    // IN_PHY_PORT
    if (!compareField(match.isWcInPhyPort(), targetMatch.isWcInPhyPort(),
        match.getInPhyPort(), targetMatch.getInPhyPort())) {
      return false;
    }
    // METADATA
    if (!compareField(match.isWcMetadata(), targetMatch.isWcMetadata(),
        match.getMetadata(), targetMatch.getMetadata())) {
      return false;
    }
    // METADATA_MASK
    if (!compareField(match.isWcMetadataMask(), targetMatch.isWcMetadataMask(),
        match.getMetadataMask(), targetMatch.getMetadataMask())) {
      return false;
    }
    // ETH_SRC
    if (!compareField(match.isWcEthSrc(), targetMatch.isWcEthSrc(),
        match.getEthSrc(), targetMatch.getEthSrc())) {
      return false;
    }
    // ETH_SRC_MASK
    if (!compareField(match.isWcEthSrcMask(), targetMatch.isWcEthSrcMask(),
        match.getEthSrcMask(), targetMatch.getEthSrcMask())) {
      return false;
    }
    // ETH_DST
    if (!compareField(match.isWcEthDst(), targetMatch.isWcEthDst(),
        match.getEthDst(), targetMatch.getEthDst())) {
      return false;
    }
    // ETH_DST_MASK
    if (!compareField(match.isWcEthDstMask(), targetMatch.isWcEthDstMask(),
        match.getEthDstMask(), targetMatch.getEthDstMask())) {
      return false;
    }
    // VLAN_VID
    if (!compareField(match.isWcVlanVid(), targetMatch.isWcVlanVid(),
        match.getVlanVid(), targetMatch.getVlanVid())) {
      return false;
    }
    // VLAN_VID_MASK
    if (!compareField(match.isWcVlanVidMask(), targetMatch.isWcVlanVidMask(),
        match.getVlanVidMask(), targetMatch.getVlanVidMask())) {
      return false;
    }
    // VLAN_PCP
    if (!compareField(match.isWcVlanPcp(), targetMatch.isWcVlanPcp(),
        match.getVlanPcp(), targetMatch.getVlanPcp())) {
      return false;
    }
    // ETH_TYPE
    if (!compareField(match.isWcEthType(), targetMatch.isWcEthType(),
        match.getEthType(), targetMatch.getEthType())) {
      return false;
    }
    // IP_DSCP
    if (!compareField(match.isWcIpDscp(), targetMatch.isWcIpDscp(),
        match.getIpDscp(), targetMatch.getIpDscp())) {
      return false;
    }
    // IP_ECN
    if (!compareField(match.isWcIpEcn(), targetMatch.isWcIpEcn(),
        match.getIpEcn(), targetMatch.getIpEcn())) {
      return false;
    }
    // IP_PROTO
    if (!compareField(match.isWcIpProto(), targetMatch.isWcIpProto(),
        match.getIpProto(), targetMatch.getIpProto())) {
      return false;
    }
    // IPV4_SRC
    if (!compareField(match.isWcIpv4Src(), targetMatch.isWcIpv4Src(),
        match.getIpv4Src(), targetMatch.getIpv4Src())) {
      return false;
    }
    // IPV4_SRC_MASK
    if (!compareField(match.isWcIpv4SrcMask(), targetMatch.isWcIpv4SrcMask(),
        match.getIpv4SrcMask(), targetMatch.getIpv4SrcMask())) {
      return false;
    }
    // IPV4_DST
    if (!compareField(match.isWcIpv4Dst(), targetMatch.isWcIpv4Dst(),
        match.getIpv4Dst(), targetMatch.getIpv4Dst())) {
      return false;
    }
    // IPV4_DST_MASK
    if (!compareField(match.isWcIpv4DstMask(), targetMatch.isWcIpv4DstMask(),
        match.getIpv4DstMask(), targetMatch.getIpv4DstMask())) {
      return false;
    }
    // TCP_SRC
    if (!compareField(match.isWcTcpSrc(), targetMatch.isWcTcpSrc(),
        match.getTcpSrc(), targetMatch.getTcpSrc())) {
      return false;
    }
    // TCP_DST
    if (!compareField(match.isWcTcpDst(), targetMatch.isWcTcpDst(),
        match.getTcpDst(), targetMatch.getTcpDst())) {
      return false;
    }
    // UDP_SRC
    if (!compareField(match.isWcUdpSrc(), targetMatch.isWcUdpSrc(),
        match.getUdpSrc(), targetMatch.getUdpSrc())) {
      return false;
    }
    // UDP_DST
    if (!compareField(match.isWcUdpDst(), targetMatch.isWcUdpDst(),
        match.getUdpDst(), targetMatch.getUdpDst())) {
      return false;
    }
    // SCTP_SRC
    if (!compareField(match.isWcSctpSrc(), targetMatch.isWcSctpSrc(),
        match.getSctpSrc(), targetMatch.getSctpSrc())) {
      return false;
    }
    // SCTP_DST
    if (!compareField(match.isWcSctpDst(), targetMatch.isWcSctpDst(),
        match.getSctpDst(), targetMatch.getSctpDst())) {
      return false;
    }
    // ICMPV4_TYPE
    if (!compareField(match.isWcIcmpv4Type(), targetMatch.isWcIcmpv4Type(),
        match.getIcmpv4Type(), targetMatch.getIcmpv4Type())) {
      return false;
    }
    // ICMPV4_CODE
    if (!compareField(match.isWcIcmpv4Code(), targetMatch.isWcIcmpv4Code(),
        match.getIcmpv4Code(), targetMatch.getIcmpv4Code())) {
      return false;
    }
    // ARP_OP
    if (!compareField(match.isWcArpOp(), targetMatch.isWcArpOp(),
        match.getArpOp(), targetMatch.getArpOp())) {
      return false;
    }
    // ARP_SPA
    if (!compareField(match.isWcArpSpa(), targetMatch.isWcArpSpa(),
        match.getArpSpa(), targetMatch.getArpSpa())) {
      return false;
    }
    // ARP_SPA_MASK
    if (!compareField(match.isWcArpSpaMask(), targetMatch.isWcArpSpaMask(),
        match.getArpSpaMask(), targetMatch.getArpSpaMask())) {
      return false;
    }
    // ARP_TPA
    if (!compareField(match.isWcArpTpa(), targetMatch.isWcArpTpa(),
        match.getArpTha(), targetMatch.getArpTha())) {
      return false;
    }
    // ARP_TPA_MASK
    if (!compareField(match.isWcArpTpaMask(), targetMatch.isWcArpTpaMask(),
        match.getArpThaMask(), targetMatch.getArpThaMask())) {
      return false;
    }
    // ARP_SHA
    if (!compareField(match.isWcArpSha(), targetMatch.isWcArpSha(),
        match.getArpSha(), targetMatch.getArpSha())) {
      return false;
    }
    // ARP_SHA_MASK
    if (!compareField(match.isWcArpShaMask(), targetMatch.isWcArpShaMask(),
        match.getArpShaMask(), targetMatch.getArpShaMask())) {
      return false;
    }
    // ARP_THA
    if (!compareField(match.isWcArpTha(), targetMatch.isWcArpTha(),
        match.getArpTha(), targetMatch.getArpTha())) {
      return false;
    }
    // ARP_THA_MASK
    if (!compareField(match.isWcArpThaMask(), targetMatch.isWcArpThaMask(),
        match.getArpThaMask(), targetMatch.getArpThaMask())) {
      return false;
    }
    // IPV6_SRC
    if (!compareField(match.isWcIpv6Src(), targetMatch.isWcIpv6Src(),
        match.getIpv6Src(), targetMatch.getIpv6Src())) {
      return false;
    }
    // IPV6_SRC_MASK
    if (!compareField(match.isWcIpv6SrcMask(), targetMatch.isWcIpv6SrcMask(),
        match.getIpv6SrcMask(), targetMatch.getIpv6SrcMask())) {
      return false;
    }
    // IPV6_DST
    if (!compareField(match.isWcIpv6Dst(), targetMatch.isWcIpv6Dst(),
        match.getIpv6Dst(), targetMatch.getIpv6Dst())) {
      return false;
    }
    // IPV6_DST_MASK
    if (!compareField(match.isWcIpv6DstMask(), targetMatch.isWcIpv6DstMask(),
        match.getIpv6DstMask(), targetMatch.getIpv6DstMask())) {
      return false;
    }
    // IPV6_FLABEL
    if (!compareField(match.isWcIpv6Flabel(), targetMatch.isWcIpv6Flabel(),
        match.getIpv6Flabel(), targetMatch.getIpv6Flabel())) {
      return false;
    }
    // IPV6_FLABEL_MASK
    if (!compareField(match.isWcIpv6FlabelMask(), targetMatch.isWcIpv6FlabelMask(),
        match.getIpv6FlabelMask(), targetMatch.getIpv6FlabelMask())) {
      return false;
    }
    // ICMPV6_TYPE
    if (!compareField(match.isWcIcmpv6Type(), targetMatch.isWcIcmpv6Type(),
        match.getIcmpv6Type(), targetMatch.getIcmpv6Type())) {
      return false;
    }
    // ICMPV6_CODE
    if (!compareField(match.isWcIcmpv6Code(), targetMatch.isWcIcmpv6Code(),
        match.getIcmpv6Code(), targetMatch.getIcmpv6Code())) {
      return false;
    }
    // IPV6_ND_TARGET
    if (!compareField(match.isWcIpv6NdTarget(), targetMatch.isWcIpv6NdTarget(),
        match.getIpv6NdTarget(), targetMatch.getIpv6NdTarget())) {
      return false;
    }
    // IPV6_ND_SLL
    if (!compareField(match.isWcIpv6NdSll(), targetMatch.isWcIpv6NdSll(),
        match.getIpv6NdSll(), targetMatch.getIpv6NdSll())) {
      return false;
    }
    // IPV6_ND_TLL
    if (!compareField(match.isWcIpv6NdTll(), targetMatch.isWcIpv6NdTll(),
        match.getIpv6NdTll(), targetMatch.getIpv6NdTll())) {
      return false;
    }
    // MPLS_LABEL
    if (!compareField(match.isWcMplsLabel(), targetMatch.isWcMplsLabel(),
        match.getMplsLabel(), targetMatch.getMplsLabel())) {
      return false;
    }
    // MPLS_TC
    if (!compareField(match.isWcMplsTc(), targetMatch.isWcMplsTc(),
        match.getMplsTc(), targetMatch.getMplsTc())) {
      return false;
    }
    // MPLS_BOS
    if (!compareField(match.isWcMplsBos(), targetMatch.isWcMplsBos(),
        match.getMplsBos(), targetMatch.getMplsBos())) {
      return false;
    }
    // PBB_ISID
    if (!compareField(match.isWcPbbIsid(), targetMatch.isWcPbbIsid(),
        match.getPbbIsid(), targetMatch.getPbbIsid())) {
      return false;
    }
    // PBB_ISID_MASK
    if (!compareField(match.isWcPbbIsidMask(), targetMatch.isWcPbbIsidMask(),
        match.getPbbIsidMask(), targetMatch.getPbbIsidMask())) {
      return false;
    }
    // TUNNEL_ID
    if (!compareField(match.isWcTunnelId(), targetMatch.isWcTunnelId(),
        match.getTunnelId(), targetMatch.getTunnelId())) {
      return false;
    }
    // TUNNEL_ID_MASK
    if (!compareField(match.isWcTunnelIdMask(), targetMatch.isWcTunnelIdMask(),
        match.getTunnelIdMask(), targetMatch.getTunnelIdMask())) {
      return false;
    }
    // IPV6_EXTHDR
    if (!compareField(match.isWcIpv6Exthdr(), targetMatch.isWcIpv6Exthdr(),
        match.getIpv6Exthdr(), targetMatch.getIpv6Exthdr())) {
      return false;
    }
    // IPV6_EXTHDR_MASK
    if (!compareField(match.isWcIpv6ExthdrMask(), targetMatch.isWcIpv6ExthdrMask(),
        match.getIpv6ExthdrMask(), targetMatch.getIpv6ExthdrMask())) {
      return false;
    }

    return true;
  }

  /**
   * Compare Match Field.
   *
   * @param wc1 this match field's wildcard.
   * @param wc2 target match field's wildcard.
   * @param target1 this match field.
   * @param target2 target match field.
   * @return boolean comparison result of target1 and target2.
   */
  private boolean compareField(
      boolean wc1, boolean wc2, Object target1, Object target2) {

    if (wc1) {
      return true;
    }

    if (wc2 || (target2 == null)) {
      return false;
    }

    if (ObjectUtils.equals(target1, target2)) {
      return true;
    }

    return false;
  }

}
