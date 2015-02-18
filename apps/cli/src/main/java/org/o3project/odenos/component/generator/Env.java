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

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.BaseObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class is to set environment variables for {@link Generator} via REST APIs.
 * 
 * For Federator tests, set "driverSelf" and adjacencies.
 * 
 * driver: gen1 driver: gen2 ( Network1 )[node10]--------[node10]( Network 2 ) port10 port10
 * 
 * adjacencies: [[10, 10, 2, 10, 10]] adjacencies: [[10, 10, 1, 10, 10]]
 */
public class Env extends BaseObject implements Cloneable {

  // A pair of difference node & port
  // Example: [[0, 1], [2, 3]], [[6, 9], [4, 10]] ...
  protected static final String RANDOM_NODE_PORT = "RANDOM_NODE_PORT";

  // A pair of difference node but same port
  // Example: [[1, 1], [2, 1][, [[6, 4], [8, 4]] ...
  protected static final String RANDOM_NODE = "RANDOM_NODE";

  // Same as RANDOM_NODE_PORT but same VLAN ID
  // TODO: OdenOS's Slicer does not support VLAN as a slicer condition
  // at the moment. Something like OFPSlicer and OFPSlicerConditon
  // are required for this setting.
  protected static final String RANDOM_NODE_PORT_VLAN = "RANDOM_NODE_PORT_VLAN";

  // nodeMin and nodeMax are chosen as nodes
  // Example: [[1, 5], [10, 9]], [[10, 5], [1, 7]], ...
  // Applicable to linear topology.
  protected static final String RANDOM_NODE_PORT_LINEAR = "RANDOM_NODE_PORT_LINEAR";

  // nodeMin and nodeMax are chosen as nodes
  // Example: [[1, 5], [10, 5]], [[10, 4], [1, 4]], ...
  // Applicable to linear topology
  protected static final String RANDOM_NODE_LINEAR = "RANDOM_NODE_LINEAR";

  // Fat-tree-specific node/port number generation
  // Applicable to fat-tree topology
  protected static final String RANDOM_NODE_PORT_FAT_TREE = "RANDOM_NODE_PORT_FAT_TREE";

  /*** Env model ********************************************/
  private String nodePrefix = "node"; // e.g., node1 ~ node48
  private String portPrefix = "port"; // e.g., port1 ~ port48
  private String driverPrefix = "gen";
  private int driverSelf = 1; // e.g., gen1
  private int[] nodeRange = { 1, 48 }; // 1 ~ 48 node
  private int[] portRange = { 1, 48 }; // 1 ~ 48 port
  private int[] vlanRange = { 1, 48 }; // 1 ~ 48 vlan
  private int[][] adjacencies = { {} }; // For Federator test
  private int[] sequencePattern = { 1, 1 }; // For Federator test
  private String samplePolicy = Env.RANDOM_NODE_PORT;
  private boolean dump = false;
  private boolean deleteOutPacket = true; // Delete OutPacket onOutPacketAdded()

  public Env() {
  }

  public Env(int[] nodeRange, int[] portRange) {
    this.nodeRange = nodeRange;
    this.portRange = portRange;
  }

  public Env(String nodePrefix, String portPrefix, int[] nodeRange, int[] portRange) {
    this.nodePrefix = nodePrefix;
    this.portPrefix = portPrefix;
    this.nodeRange = nodeRange;
    this.portRange = portRange;
  }

  public Env(Env msg) {
    this.setVersion(msg.getVersion());
    this.nodePrefix = msg.getNodePrefix();
    this.portPrefix = msg.getPortPrefix();
    this.driverPrefix = msg.getDriverPrefix();
    this.driverSelf = msg.getDriverSelf();
    this.nodeRange = msg.getNodeRange();
    this.portRange = msg.getPortRange();
    this.vlanRange = msg.getVlanRange();
    this.adjacencies = msg.getAdjacencies();
    this.sequencePattern = msg.getSequencePattern();
    this.samplePolicy = msg.getSamplePolicy();
    this.dump = msg.getDump();
    this.deleteOutPacket = msg.getDeleteOutPacket();
    this.putAttributes(new HashMap<String, String>(msg.getAttributes()));
  }

  protected String getNodePrefix() {
    return nodePrefix;
  }

  protected String getPortPrefix() {
    return portPrefix;
  }

  protected String getDriverPrefix() {
    return driverPrefix;
  }

  protected int getDriverSelf() {
    return driverSelf;
  }

  protected int[] getNodeRange() {
    return nodeRange;
  }

  protected int[] getPortRange() {
    return portRange;
  }

  protected int[] getVlanRange() {
    return vlanRange;
  }

  protected int[][] getAdjacencies() {
    return adjacencies;
  }

  protected boolean hasAdjacencies() {
    return (adjacencies[0].length > 0);
  }

  protected int[] getSequencePattern() {
    return sequencePattern;
  }

  protected String getSamplePolicy() {
    return samplePolicy;
  }

  protected boolean getDump() {
    return dump;
  }

  protected boolean getDeleteOutPacket() {
    return deleteOutPacket;
  }

  protected static final String TOR = "3";

  protected String nodeFormat() {
    String f;
    switch (this.samplePolicy) {
      case RANDOM_NODE_PORT_FAT_TREE:
        // '{layer:}{pod:02}{left_right}{number:02}'
        f = getNodePrefix() + TOR + "%05d";
        break;
      default:
        f = getNodePrefix() + "0%0" + Integer.toString(getPortRange()[1]).length() + "d";
        break;
    }
    return f;
  }

  protected String portFormat() {
    String f;
    switch (this.samplePolicy) {
      case RANDOM_NODE_PORT_FAT_TREE:
        f = getPortPrefix() + "%06d";
        break;
      default:
        f = getPortPrefix() + "0%0" + Integer.toString(getPortRange()[1]).length() + "d";
        break;
    }
    return f;
  }

  protected String driverFormat() {
    return getDriverPrefix() + "%d";
  }

  @Override
  public void readFrom(Unpacker upk) throws IOException {

    int size = upk.readMapBegin();

    while (size-- > 0) {
      switch (upk.readString()) {
        case "nodePrefix":
          nodePrefix = upk.readString();
          break;
        case "portPrefix":
          portPrefix = upk.readString();
          break;
        case "driverPrefix":
          driverPrefix = upk.readString();
          break;
        case "driverSelf":
          driverSelf = upk.readInt();
          break;
        case "nodeRange":
          nodeRange = upk.read(int[].class);
          break;
        case "portRange":
          portRange = upk.read(int[].class);
          break;
        case "vlanRange":
          vlanRange = upk.read(int[].class);
          break;
        case "adjacencies":
          adjacencies = upk.read(int[][].class);
          break;
        case "sequencePattern":
          sequencePattern = upk.read(int[].class);
          break;
        case "samplePolicy":
          samplePolicy = upk.readString();
          break;
        case "dump":
          dump = upk.readBoolean();
          break;
        case "deleteOutPacket":
          deleteOutPacket = upk.readBoolean();
          break;
        default:
          break;
      }
    }

    upk.readMapEnd();
  }

  @Override
  public void writeTo(Packer pk) throws IOException {

    pk.writeMapBegin(12);

    pk.write("nodePrefix");
    pk.write(nodePrefix);

    pk.write("portPrefix");
    pk.write(portPrefix);

    pk.write("driverPrefix");
    pk.write(driverPrefix);

    pk.write("driverSelf");
    pk.write(driverSelf);

    pk.write("nodeRange");
    pk.write(nodeRange);

    pk.write("portRange");
    pk.write(portRange);

    pk.write("vlanRange");
    pk.write(vlanRange);

    pk.write("adjacencies");
    pk.write(adjacencies);

    pk.write("sequencePattern");
    pk.write(sequencePattern);

    pk.write("samplePolicy");
    pk.write(samplePolicy);

    pk.write("dump");
    pk.write(dump);

    pk.write("deleteOutPacket");
    pk.write(deleteOutPacket);

    pk.writeMapEnd();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Env)) {
      return false;
    }

    Env env = (Env) obj;

    try {
      if (env.getVersion() == this.getVersion() && env.getNodePrefix().equals(this.nodePrefix)
          && env.getPortPrefix().equals(this.portPrefix)
          && env.getDriverPrefix().equals(this.driverPrefix)
          && env.getDriverSelf() == this.driverSelf && env.getNodeRange() == this.nodeRange
          && env.getPortRange() == this.portRange && env.getVlanRange() == this.vlanRange
          && env.getAdjacencies() == this.adjacencies
          && env.getSequencePattern() == this.sequencePattern
          && env.getSamplePolicy().equals(this.samplePolicy) && env.getDump() == this.dump
          && env.getDeleteOutPacket() == this.deleteOutPacket
          && env.getAttributes().equals(this.getAttributes())) {
        return true;
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Env clone() {
    return new Env(this);
  }
}