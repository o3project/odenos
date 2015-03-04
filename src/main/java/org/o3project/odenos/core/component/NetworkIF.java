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

package org.o3project.odenos.core.component;

import org.o3project.odenos.core.component.network.topology.Node;
import org.o3project.odenos.core.component.network.topology.NodeIF;
import org.o3project.odenos.core.component.network.topology.Topology;
import org.o3project.odenos.remoteobject.RemoteObjectIF;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

public class NetworkIF extends RemoteObjectIF {
  public static final String PATH_TOPOLOGY = "topology";
  public static final String PATH_NODES = "topology/nodes/";
  public static final String PATH_PORTS = "topology/nodes/%s/ports/";
  public static final String PATH_PHYSICAL_PORTS = "topology/physical_ports/%s";
  public static final String PATH_LINKS = "topology/links/";
  public static final String PATH_FLOWS = "flows";
  public static final String PATH_INPACKETS = "packets/in/";
  public static final String PATH_INPACKETS_HEAD = "packets/in/head";
  public static final String PATH_OUTPACKETS = "packets/out/";
  public static final String PATH_OUTPACKETS_HEAD = "packets/out/head";
  public static final String PATH_PACKETS = "packets/";

  @Deprecated
  public NetworkIF(final MessageDispatcher dispatcher, final String id) {
    super(dispatcher, id);
  }

  public NetworkIF(final String sourceObjectId, final MessageDispatcher dispatcher) {
    super(sourceObjectId, dispatcher);
  }

  public final Topology getTopology() {
    return this.get(PATH_TOPOLOGY).getBody2(Topology.class);
  }

  public final NodeIF addNode(final Node node) {
    Node newNode = this.put(PATH_NODES + node.getId(), node).getBody2(Node.class);
    return new NodeIF(this, newNode);
  }
}
