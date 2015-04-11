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

package org.o3project.odenos.remoteobject.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Base Object class.
 *
 */
public abstract class BaseObject extends MessageBodyUnpacker {
  private Map<String, String> attributes = new HashMap<String, String>();

  private int version = 0;

  public static final String INITIAL_VERSION = "0";

  /**
   * Get version of this object.
   *
   * @return Version number in String.
   */
  public String getVersion() {
    return String.valueOf(version);
  }

  /**
   * Set version of this object.
   *
   * @param version Version of this object.
   */
  protected void setVersion(int version) {
    this.version = version;
  }

  /**
   * Set version of this object.
   *
   * @param version Version of this object.
   */
  public void setVersion(String version) {
    try {
      this.version = Integer.parseInt(version);
    } catch (IllegalArgumentException e) {
      //e.printStackTrace();
    }
  }

  /**
   * Increment version number.
   */
  public void updateVersion() {
    ++version;
  }

  /**
   * Get attribute value from given value.
   *
   * @param key Name of attribute.
   * @return attribute value
   */
  public String getAttribute(String key) {
    return attributes.get(key);
  }

  /**
   * is attribute form key
   *
   * @param key Name of attribute.
   * @return boolean
   */
  public boolean isAttribute(String key) {
    return attributes.containsKey(key);
  }

  /**
   * Set attribute value.
   *
   * @param key Name of attribute.
   * @param value Value of attribute.
   * @return attribute value
   */
  public String putAttribute(String key, String value) {
    return attributes.put(key, value);
  }

  /**
   * Get whole attribute map object.
   *
   * @return whole attribute map object
   */
  public final Map<String, String> getAttributes() {
    return attributes;
  }

  /**
   * Replace whole attributes with input.
   *
   * @param attributes Map of attributes.
   */
  public void putAttributes(Map<String, String> attributes) {
    if (attributes == null) {
      this.attributes = new HashMap<String, String>();
    } else {
      this.attributes = attributes;
    }
  }

  /**
   * Delete attribute value.
   *
   * @param key Name of attribute.
   * @return deleted attribute value
   */
  public String deleteAttribute(String key) {
    return attributes.remove(key);
  }
}
