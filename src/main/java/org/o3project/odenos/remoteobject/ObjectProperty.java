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

package org.o3project.odenos.remoteobject;

import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * RemoteObject class and RemoteObject subclass property.
 *
 */
public class ObjectProperty implements MessagePackable, Cloneable {
  public class PropertyNames {
    public static final String OBJECT_ID = "id";
    public static final String OBJECT_SUPER_TYPE = "super_type";
    public static final String OBJECT_TYPE = "type";
    public static final String OBJECT_STATE = "state";
    public static final String BASE_URI = "base_uri";
    public static final String DESCRIPTION = "description";
    public static final String CM_ID = "cm_id";
    public static final String COMPONENT_TYPES = "component_types";
    public static final String CONNECTION_TYPES = "connection_types";
  }

  public class State {
    public static final String INITIALIZING = "initializing";
    public static final String RUNNING = "running";
    public static final String FINALIZING = "finalizing";
    public static final String ERROR = "error";
  }

  protected Map<String, String> property = new HashMap<String, String>();

  /**
   * Constructor.
   * @deprecated @see #ObjectProperty(String, String)
   */
  @Deprecated
  public ObjectProperty() {
  }

  /**
   * Constructor.
   * @param objectType type of objects.
   * @param objectId object ID.
   * @param baseUri base URI
   * @deprecated @see #ObjectProperty(String, String)
   */
  @Deprecated
  public ObjectProperty(String objectType, String objectId, String baseUri) {
    property.put(PropertyNames.OBJECT_TYPE, objectType);
    property.put(PropertyNames.OBJECT_ID, objectId);
    property.put(PropertyNames.BASE_URI, baseUri);
  }

  /**
   * Constructor.
   * @param objectType type of objects.
   * @param objectId object ID.
   */
  public ObjectProperty(String objectType, String objectId) {
    property.put(PropertyNames.OBJECT_TYPE, objectType);
    property.put(PropertyNames.OBJECT_ID, objectId);
  }

  @Override
  public Object clone() {
    ObjectProperty obj = new ObjectProperty();
    obj.property = new HashMap<String, String>(this.property);
    return obj;
  }

  public String getObjectType() {
    return property.get(PropertyNames.OBJECT_TYPE);
  }

  public String getObjectId() {
    return property.get(PropertyNames.OBJECT_ID);
  }

  public String getBaseUri() {
    return property.get(PropertyNames.BASE_URI);
  }

  public String getObjectState() {
    return property.get(PropertyNames.OBJECT_STATE);
  }

  public String setObjectState(String objectState) {
    return setProperty(PropertyNames.OBJECT_STATE, objectState);
  }

  /**
   * Set a property.
   * @param key a key name.
   * @param value a value of key.
   * @return previous value associated with key.
   */
  public String setProperty(String key, String value) {
    if (!isReadOnlyKey(key)) {
      return property.put(key, value);
    }
    return null;
  }

  public String getProperty(String key) {
    return property.get(key);
  }

  /**
   * Delete a property.
   * @param key deleted the key.
   * @return previous value associated with key.
   */
  public final String deleteProperty(final String key) {
    if (!isReadOnlyKey(key)) {
      return property.remove(key);
    }
    return null;
  }

  /**
   * Update a property.
   * @param newProperty replaced property.
   */
  public final void putProperty(final ObjectProperty newProperty) {
    Set<String> newKeySet = newProperty.getKeys();
    for (String key : new HashSet<String>(getKeys())) {
      if (!newKeySet.contains(key)) {
        deleteProperty(key);
      }
    }

    for (Entry<String, String> e : newProperty.property.entrySet()) {
      setProperty(e.getKey(), e.getValue());
    }
  }

  public Set<String> getKeys() {
    return property.keySet();
  }

  /**
   * Returns true if the settings are modified.
   * @param newProperty will replace the settings.
   * @return true if the settings are modified.
   */
  public final boolean isModify(final ObjectProperty newProperty) {
    Set<String> oldKeySet = this.getKeys();
    Set<String> newKeySet = newProperty.getKeys();

    // check add or delete
    if (!oldKeySet.equals(newKeySet)) {
      return true;
    }

    // check value modify
    for (Entry<String, String> e : newProperty.property.entrySet()) {
      String newValue = e.getValue();
      String oldValue = this.getProperty(e.getKey());
      if (!newValue.equals(oldValue)) {
        return true;
      }
    }

    return false;
  }

  protected Boolean isReadOnlyKey(String key) {
    return key.equals(PropertyNames.OBJECT_TYPE)
        || (key.equals(PropertyNames.OBJECT_SUPER_TYPE)
        && (property.containsKey(key)))
        || (key.equals(PropertyNames.DESCRIPTION)
        && (property.containsKey(key)))
        || (key.equals(PropertyNames.CONNECTION_TYPES)
        && (property.containsKey(key)));
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.write(property);
  }

  @Override
  public void readFrom(Unpacker unpk) throws IOException {
    property.clear();
    property.putAll(unpk.read(tMap(TString, TString)));
  }
}
