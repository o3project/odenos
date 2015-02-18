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

package org.o3project.odenos.core.component.network.packet;

import org.o3project.odenos.core.component.network.BasicQuery;

import java.util.Map;

/**
 * Prepares a query for packet.
 *
 * @param <T> class of packet.
 */
public class PacketQuery<T> extends BasicQuery<Packet> {

  private static final String ATTRIBUTES = "attributes";
  protected Map<String, String> attributes = null;

  /**
   * Constructor.
   * @param queriesString query string.
   */
  public PacketQuery(String queriesString) {
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
    if (this.queries.containsKey(PacketQuery.ATTRIBUTES)) {
      this.attributes = BasicQuery.convertToMap(this.queries.remove(
          PacketQuery.ATTRIBUTES).split(","), "=");
    }
    return true;
  }

  /**
   * Returns a attribute.
   * @param key key.
   * @return attribute associated to the key.
   */
  public String getAttributeValue(String key) {
    return this.attributes.get(key);
  }

  /**
   * Compare in detail.
   * @param target packet.
   * @return true if target is same.
   */
  public boolean matchExactly(Packet target) {
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
}