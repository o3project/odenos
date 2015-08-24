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
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Connection information between OperatorComponent and NetworkComponent.
 *
 */
public class ComponentConnection implements MessagePackable, Cloneable {

  public interface ComponentConnectionChangedListener {
    public void onComponentConnectionChanged(String key, String oldValue,
        String newValue);
  }

  protected ComponentConnectionChangedListener componentConnectionChangedListener = null;

  public void setObjectPropertyChangedListener(
      ComponentConnectionChangedListener listener) {
    componentConnectionChangedListener = listener;
  }

  public class State {
    public static final String INITIALIZING = "initializing";
    public static final String RUNNING = "running";
    public static final String FINALIZING = "finalizing";
    public static final String ERROR = "error";
    public static final String NONE = "none";
  }

  public final String defaultType = this.getClass().getSimpleName();
  public static final String OBJECT_ID = "id";
  public static final String OBJECT_TYPE = "type";
  public static final String CONNECTION_TYPE = "connection_type";
  public static final String CONNECTION_STATE = "state";

  protected HashMap<String, String> property = new HashMap<String, String>();

  /**
   * Constructor.
   * @param objectId ID that is unique in the ODENOS.
   * @param connectionType type of connection.
   * @param connectionState state. {@link State}
   */
  public ComponentConnection(String objectId, String connectionType,
      String connectionState) {
    this.onInitializing(objectId, this.defaultType, connectionType,
        connectionState);
  }

  /**
   * Constructor.
   * @param objectId ID that is unique in the ODENOS.
   * @param type type of connection.
   * @param connectionType type of connection.
   * @param connectionState state. {@link State}
   */
  public ComponentConnection(String objectId, String type,
      String connectionType,
      String connectionState) {
    this.onInitializing(objectId, type, connectionType, connectionState);
  }

  private void onInitializing(String objectId, String type,
      String connectionType,
      String connectionState) {

    property.put(OBJECT_ID, objectId);
    property.put(OBJECT_TYPE, type);
    property.put(CONNECTION_TYPE, connectionType);

    if (connectionState == null || connectionState.equals("")) {
      connectionState = State.INITIALIZING;
    }
    property.put(CONNECTION_STATE, connectionState);
  }

  @Deprecated
  public ComponentConnection() {
  }

  @Override
  public Object clone() {
    ComponentConnection obj = new ComponentConnection();
    obj.property = new HashMap<String, String>(this.property);
    return obj;
  }

  public final String getObjectId() {
    return property.get(OBJECT_ID);
  }

  public final String getObjectType() {
    return property.get(OBJECT_TYPE);
  }

  public final String getConnectionType() {
    return property.get(CONNECTION_TYPE);
  }

  public final String getObjectState() {
    return property.get(CONNECTION_STATE);
  }

  public final void setConnectionState(final String state) {
    setProperty(CONNECTION_STATE, state);
  }

  /**
   * Sets a property.
   * @param key a key of properties.
   * @param value a value of key.
   */
  public final void setProperty(final String key, final String value) {
    if (!isReadOnlyKey(key)) {
      String oldValue = property.get(key);
      if (!Objects.equals(oldValue, value)) {
        property.put(key, value);
        if (componentConnectionChangedListener != null) {
          componentConnectionChangedListener
              .onComponentConnectionChanged(key, oldValue,
                  value);
        }
      }
    }
  }

  protected Boolean isReadOnlyKey(String key) {
    return OBJECT_ID.equals(key) || OBJECT_TYPE.equals(key);
  }

  public final String getProperty(final String key) {
    return property.get(key);
  }

  public Set<String> getKeys() {
    return property.keySet();
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.write(property);
  }

  @Override
  public void readFrom(Unpacker unpk) throws IOException {
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

    if (!(obj instanceof ComponentConnection)) {
      return false;
    }

    ComponentConnection cmpConnectionProp = (ComponentConnection) obj;

    if (cmpConnectionProp.property.equals(this.property)) {
      return true;
    }

    return false;
  }
}
