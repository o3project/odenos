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

package org.o3project.odenos.component.generator;

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.remoteobject.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * This class generates InPacket following the rule set by a user via REST APIs.
 */
public class PostInPacketGenerator implements Runnable {

  private Logger log = LoggerFactory.getLogger(Generator.class);

  Generator callback;

  String objectId;

  String driverPrefix;
  String nodePrefix;
  String portPrefix;
  String samplePolicy;
  boolean hasAdjacencies;
  int[] sequencePattern;
  int driverSelf;
  int[] drivers;

  int nodeMin;
  int nodeMax;
  int portMin;
  int portMax;
  int vlanMin;
  int vlanMax;

  int frequency;
  boolean generating;
  byte[] data;

  int seqno = 0;
  int maxSeqno;
  int interval;

  Stats stats;

  int idx = 0;
  NetworkInterface networkIf;

  String nodeFormat;
  String portFormat;

  static final long mask = 0x0000000000FFFFFF;

  boolean dump = false;

  PostInPacketGenerator(Generator callback, String objectId, Env env, Control control, Stats stats,
      NetworkInterface networkIf) {

    this.objectId = objectId;

    this.nodePrefix = env.getNodePrefix();
    this.portPrefix = env.getPortPrefix();

    int[] nodeRange = env.getNodeRange();
    int[] portRange = env.getPortRange();
    int[] vlanRange = env.getVlanRange();
    this.nodeMin = nodeRange[0];
    this.nodeMax = nodeRange[1];
    this.portMin = portRange[0];
    this.portMax = portRange[1];
    this.vlanMin = vlanRange[0];
    this.vlanMax = vlanRange[1];

    this.samplePolicy = env.getSamplePolicy();

    this.hasAdjacencies = env.hasAdjacencies();
    int[][] adjacencies = env.getAdjacencies();
    if (hasAdjacencies) {
      this.drivers = new int[adjacencies.length + 1];
      this.drivers[0] = env.getDriverSelf();
      int i = 0;
      for (int[] adjacency : adjacencies) {
        this.drivers[++i] = adjacency[2];
      }
    } else {
      this.drivers = new int[1];
      this.drivers[0] = env.getDriverSelf();
    }
    this.sequencePattern = env.getSequencePattern();
    this.driverSelf = env.getDriverSelf();

    this.frequency = control.getFrequency();
    this.generating = control.getGenerating();
    this.data = control.getPayload().getBytes();

    this.maxSeqno = control.getMaxSeqno();
    if (hasAdjacencies) {
      this.seqno = sequencePattern[0];
      this.interval = sequencePattern[1];
    }

    this.stats = stats;

    this.networkIf = networkIf;

    this.nodeFormat = env.nodeFormat();
    this.portFormat = env.portFormat();

    this.dump = env.getDump();

  }

  protected static final synchronized int currentTimeMills() {
    return (int) (System.currentTimeMillis() & mask);
  }

  private int randInt(int min, int max) {
    return min + (int) (Math.random() * ((max - min) + 1));
  }

  private int[] sample() {
    int nodeSrc = 0;
    int portSrc = 0;
    int nodeDst = 0;
    int portDst = 0;
    int vlan = 0;
    switch (samplePolicy) {
      case Env.RANDOM_NODE_PORT:
        do {
          nodeSrc = randInt(nodeMin, nodeMax);
          portSrc = randInt(portMin, portMax);
          nodeDst = randInt(nodeMin, nodeMax);
          portDst = randInt(portMin, portMax);
        } while (nodeSrc == nodeDst && portSrc == portDst);
        break;
      case Env.RANDOM_NODE:
        do {
          nodeSrc = randInt(nodeMin, nodeMax);
          nodeDst = randInt(nodeMin, nodeMax);
          portSrc = portDst = randInt(portMin, portMax);
        } while (nodeSrc == nodeDst);
        break;
      case Env.RANDOM_NODE_PORT_VLAN:
        do {
          nodeSrc = randInt(nodeMin, nodeMax);
          nodeDst = randInt(nodeMin, nodeMax);
          nodeDst = randInt(nodeMin, nodeMax);
          portDst = randInt(portMin, portMax);
          vlan = randInt(vlanMin, vlanMax);
        } while (nodeSrc == nodeDst && portSrc == portDst);
        break;
      case Env.RANDOM_NODE_PORT_LINEAR:
        int rand = randInt(0, 1);
        switch (rand) {
          case 0:
            nodeSrc = nodeMin;
            nodeDst = nodeMax;
            break;
          case 1:
            nodeSrc = nodeMax;
            nodeDst = nodeMin;
            break;
          default:
            break;
        }
        do {
          portSrc = randInt(portMin, portMax);
          portDst = randInt(portMin, portMax);
        } while (portSrc == portDst);
        break;
      case Env.RANDOM_NODE_LINEAR:
        int zeroOrOne = randInt(0, 1);
        switch (zeroOrOne) {
          case 0:
            nodeSrc = nodeMin;
            nodeDst = nodeMax;
            break;
          case 1:
            nodeSrc = nodeMax;
            nodeDst = nodeMin;
            break;
          default:
            break;
        }
        portSrc = portDst = randInt(portMin, portMax);
        break;
      case Env.RANDOM_NODE_PORT_FAT_TREE:
        do {
          int pod = randInt(1, 40);
          int leftOrRight = randInt(1, 2);
          nodeSrc = randInt(1, 20) + leftOrRight * 100 + pod * 1000; // Node number
          portSrc = randInt(1, 10); // ToR eport number
          pod = randInt(1, 40);
          leftOrRight = randInt(1, 2);
          nodeDst = randInt(1, 20) + leftOrRight * 100 + pod * 1000; // Node number
          portDst = randInt(1, 10); // ToR eport number
        } while (nodeSrc == nodeDst && portSrc == portDst);
        break;
      default:
        log.error("Illegal samplePolicy");
    }

    if (drivers == null) {
      int[] pair = { nodeSrc, portSrc, nodeDst, portDst, vlan, -1 };
      return pair;
    } else {
      int[] pair = { nodeSrc, portSrc, nodeDst, portDst, vlan,
          drivers[randInt(0, drivers.length - 1)] };
      return pair;
    }
  }

  /**
   * Packet payload for the generator: [timestamp1][timestamp2][data] timestamp2 is referred to when
   * traversing to other drivers
   */
  protected static final synchronized byte[] createPayload(byte goback, int timestamp1,
      int timestamp2, byte[] data) {
    ByteBuffer payload = ByteBuffer.allocate(data.length + 9);
    return payload.put(goback).putInt(timestamp1).putInt(timestamp2).put(data).array();
  }

  /**
   * MAC address format: [nodeA][nodeB][portC][seqnoD][seqnoE][seqnoF] node's range: 0 ~ 65535
   * port's range: 0 ~ 255 seqno's range: 0 ~ 16581375
   */
  private String macAddress(int node, int port, int seqno) {
    int nodeA = node / 256;
    int nodeB = node % 256;
    int portC = port;
    int seqnoD = seqno >> 16;
    int seqnoE = (seqno & 0x00FF00) >> 8;
    int seqnoF = seqno & 0x0000FF;
    return String.format("%02X:%02X:%02X:%02X:%02X:%02X", nodeA, nodeB, portC, seqnoD, seqnoE,
        seqnoF);
  }

  /**
   * MAC address format: [driverA][nodeB][portC][seqnoD][seqnoE][seqnoF] driver's range: 0 ~ 255
   * node's range: 0 ~ 255 port's range: 0 ~ 255 seqno's range: 0 ~ 16581375
   */
  private String macAddressWithDriverNumber(int driver, int node, int port, int seqno) {
    int driverA = driver;
    int nodeB = node;
    int portC = port;
    int seqnoD = seqno >> 16;
    int seqnoE = (seqno & 0x00FF00) >> 8;
    int seqnoF = seqno & 0x0000FF;
    return String.format("%02X:%02X:%02X:%02X:%02X:%02X", driverA, nodeB, portC, seqnoD, seqnoE,
        seqnoF);
  }

  private void postInPacket(int inNodeNum, int inPortNum, int outNodeNum, int outPortNum, int vlan,
      int idx, byte[] data, int seqno, int driver) {

    final float time = 0;
    final HashMap<String, String> attributes = new HashMap<String, String>();

    int timestamp = currentTimeMills();
    byte[] payload = createPayload(Generator.GO, timestamp, timestamp, data);

    String inNode = String.format(nodeFormat, inNodeNum);
    String inPort = String.format(portFormat, inPortNum);
    String dlSrc;
    String dlDst;
    if (driver < 0) { // No federation
      dlSrc = macAddress(inNodeNum, inPortNum, seqno);
      dlDst = macAddress(outNodeNum, outPortNum, seqno);
    } else { // Federation
      dlSrc = macAddressWithDriverNumber(driverSelf, inNodeNum, inPortNum, seqno);
      dlDst = macAddressWithDriverNumber(driver, outNodeNum, outPortNum, seqno);
    }
    String nodeId = inNode;
    String portId = inPort;

    String packetId = String.format("packet_generator(%d)", idx++);

    OFPFlowMatch header = new OFPFlowMatch();
    header.setInNode(inNode);
    header.setInPort(inPort);
    header.setEthSrc(dlSrc);
    header.setEthDst(dlDst);
    //header.setDlVlan(vlan);  TODO:

    OFPInPacket body = new OFPInPacket(packetId, nodeId, portId, time, header, payload, attributes);
    log.debug("postInPacket() called");
    try {
      Response resp = networkIf.postInPacket(body);
      log.debug("postInPacket() retruned");
      if (dump) {
        Util.dump("postInPacket@PostInPacketGenerator", objectId, packetId, inNode, inPort, dlSrc,
            dlDst, resp.statusCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    stats.postInPacket();
  }

  @Override
  public void run() {
    log.debug("run()");
    if (seqno > maxSeqno) {
      callback.stop();
    } else {
      if (hasAdjacencies) {
        int[] pair = sample();
        this.postInPacket(pair[0], pair[1], pair[2], pair[3], pair[4], idx++, data, seqno, pair[5]);
        seqno += interval;
      } else {
        int[] pair = sample();
        // Post InPacket from one node/port
        this.postInPacket(pair[0], pair[1], pair[2], pair[3], pair[4], idx++, data, seqno, -1);
        // Post InPacket from the other node/port
        // this.postInPacket(pair[2], pair[3], pair[0], pair[1], idx++, data, seqno);
        seqno++;
      }
    }
  }
}
