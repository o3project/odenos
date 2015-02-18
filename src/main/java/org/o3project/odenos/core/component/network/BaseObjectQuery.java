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

package org.o3project.odenos.core.component.network;

import org.o3project.odenos.remoteobject.message.BaseObject;

import java.util.HashMap;
import java.util.Map;

/**
 * BasicObjectQuery class.
 *
 * @param <T> type of Queries.
 */
public class BaseObjectQuery<T> extends BasicQuery<BaseObject> {

  private static final String ATTRIBUTES = "attributes";
  protected Map<String, String> attributes = new HashMap<String, String>();

  /**
   * Constructor.
   *
   * @param queriesString Queries String.
   */
  public BaseObjectQuery(String queriesString) {
    super(queriesString);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.component.network.BasicQuery#parse()
   */
  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    if (this.queries.containsKey(BaseObjectQuery.ATTRIBUTES)) {
      this.attributes =
          BasicQuery.convertToMap(
              this.queries.remove(BaseObjectQuery.ATTRIBUTES).split(","), "=");
      if (this.attributes == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compare in detail.
   * @param target target object.
   * @return true if attributes are same.
   */
  public boolean matchExactly(BaseObject target) {
    if (this.attributes == null) {
      return true;
    }
    for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
      if (!target.getAttributes().containsKey(entry.getKey())) {
        return false;
      }
      if (!target.getAttributes().get(entry.getKey())
          .equals(this.attributes.get(entry.getKey()))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a value of attribute.
   * @param key key.
   * @return value of attribute.
   */
  public String getAttributeValue(String key) {
    return this.attributes.get(key);
  }
}