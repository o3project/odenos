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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Settings data.
 *
 */
public class ObjectSettings implements MessagePackable, Cloneable {
  protected Map<String, String> settings = new HashMap<String, String>();

  /**
   * Constructor.
   */
  public ObjectSettings() {

  }

  @Override
  public Object clone() {
    ObjectSettings object = new ObjectSettings();
    object.settings = new HashMap<String, String>(this.settings);
    return object;
  }

  /**
   * Returns the value to which the key is setting.
   * @param key key.
   * @return the value to which the key is setting.
   */
  public String getSetting(String key) {
    return settings.get(key);
  }

  /**
   * Associates the value with the key in setting.
   * @param key key.
   * @param value value to be associated key.
   * @return previous value
   */
  public String setSetting(String key, String value) {
    return settings.put(key, value);
  }

  /**
   * deletes the key from setting.
   * @param key key
   * @return previous value
   */
  public final String deleteSetting(final String key) {
    return settings.remove(key);
  }

  /**
   * Copies all setting from newSettings.
   * @param newSettings new settings.
   */
  public final void putSettings(final ObjectSettings newSettings) {
    settings.clear();
    settings.putAll(newSettings.settings);
  }

  /**
   * Returns a set of keys contained in the setting.
   * @return set of keys contained in the setting.
   */
  public Set<String> getKeys() {
    return settings.keySet();
  }

  /**
   * Returns true if the settings are modified.
   * @param newSettings will replace the settings.
   * @return true if the settings are modified.
   */
  public final boolean isModify(final ObjectSettings newSettings) {
    Set<String> oldKeySet = this.getKeys();
    Set<String> newKeySet = newSettings.getKeys();

    // check add or delete
    if (!oldKeySet.equals(newKeySet)) {
      return true;
    }

    // check value modify
    for (Entry<String, String> e : newSettings.settings.entrySet()) {
      String newValue = e.getValue();
      String oldValue = this.getSetting(e.getKey());
      if (!newValue.equals(oldValue)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.write(settings);
  }

  @Override
  public void readFrom(Unpacker unpacker) throws IOException {
    unpacker.read(settings, tMap(TString, TString));
  }
}
