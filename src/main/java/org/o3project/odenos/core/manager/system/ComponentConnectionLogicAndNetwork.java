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

package org.o3project.odenos.core.manager.system;

import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

/**
 * Connection information between LogicComponent and NetworkComponent.
 *
 */
public class ComponentConnectionLogicAndNetwork
    extends ComponentConnection implements MessagePackable {

  public static final String TYPE = "LogicAndNetwork";
  public static final String LOGIC_ID = "logic_id";
  public static final String NETWORK_ID = "network_id";

  /**
   * Constructor.
   * @param objectId object ID.
   * @param connectionType type of connection.
   * @param connectionState state of connection.
   * @param logicId logic ID.
   * @param networkId network ID.
   */
  public ComponentConnectionLogicAndNetwork(
      final String objectId,
      final String connectionType,
      final String connectionState,
      final String logicId,
      final String networkId) {

    super(objectId, TYPE, connectionType, connectionState);
    property.put(LOGIC_ID, logicId);
    property.put(NETWORK_ID, networkId);
  }

  /**
   * Constructor.
   * @deprecated @see #ComponentConnectionLogicAndNetwork(String, String, String, String, String)
   */
  @Deprecated
  public ComponentConnectionLogicAndNetwork() {
  }

  /**
   * Returns a LogicComponent ID.
   * @return LogicComponent ID.
   */
  public final String getLogicId() {
    return property.get(LOGIC_ID);
  }

  /**
   * Returns a NetworkComponent ID.
   * @return NetworkComponent ID.
   */
  public final String getNetworkId() {
    return property.get(NETWORK_ID);
  }

  @Override
  public final void writeTo(final Packer packer) throws IOException {
    packer.write(property);
  }

  @Override
  public final void readFrom(final Unpacker unpk) throws IOException {
    property.clear();
    property.putAll(unpk.read(tMap(TString, TString)));
    if (!property.containsKey(CONNECTION_STATE)) {
      property.put(CONNECTION_STATE, State.INITIALIZING);
    }
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof ComponentConnectionLogicAndNetwork)) {
      return false;
    }

    ComponentConnectionLogicAndNetwork cmpConnectionProp =
        (ComponentConnectionLogicAndNetwork) obj;

    if (cmpConnectionProp.property.equals(this.property)) {
      return true;
    }

    return false;
  }
}
