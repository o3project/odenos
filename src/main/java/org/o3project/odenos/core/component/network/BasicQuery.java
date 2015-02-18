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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BasicQuery class.
 *
 * @param <T> type of queries.
 */
public abstract class BasicQuery<T> {
  private String queriesString = null;
  protected Map<String, String> queries = new HashMap<String, String>();

  /**
   * Constructor.
   * @deprecated {@link #BasicQuery(String)}.
   */
  @Deprecated
  public BasicQuery() {
  }

  /**
   * Constructor.
   * @param queriesString Queries String.
   */
  public BasicQuery(String queriesString) {
    if (queriesString != null) {
      this.queriesString = queriesString.replace("\"", "");
    }
  }

  /**
   * parse queries.
   * @return true if queries could be parsed.
   */
  public boolean parse() {

    if (this.queriesString == null) {
      return false;
    }

    if ("".equals(this.queriesString)) {
      return true;
    }

    String[] queries = this.queriesString.split("&");
    if (queries.length == 0) {
      return true;
    }

    for (String q : queries) {
      String[] query = q.split("=", 2);
      if (query.length != 2) {
        return false;
      }
      this.queries.put(query[0], query[1]);
    }
    return true;
  }

  /**
   * Convert queries to map.
   * @param strings queries.
   * @param regex delimiting regular expression.
   * @return map of queries.
   */
  public static Map<String, String> convertToMap(String[] strings, String regex) {
    Map<String, String> map = new HashMap<String, String>();
    for (String s : strings) {
      String[] string = s.split(regex);
      if (string.length != 2) {
        return null;
      }
      map.put(string[0], string[1]);
    }
    return map;
  }

  /**
   * Compare map in detail.
   * @param map map of queries.
   * @param keys target key name.
   * @return true if queries are same.
   */
  public static boolean checkMapExactly(Map<String, String> map, String[] keys) {

    List<String> requires = Arrays.asList(keys);

    for (String key : map.keySet()) {
      if (!requires.contains(key)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return integer of key on map.
   * @param map map of queries.
   * @param key target key name.
   * @return value of integer to which the specified key is mapped.
   */
  public static Integer cretateInteger(Map<String, String> map, String key) {
    Integer intValue = null;
    try {
      intValue = Integer.valueOf(map.get(key));
    } catch (NumberFormatException e) {
      return null;
    }

    return intValue;
  }

  /**
   * Return long of key on map.
   * @param map map of queries.
   * @param key target key name.
   * @return value of long to which the specified key is mapped.
   */
  public static Long cretateLong(Map<String, String> map, String key) {
    Long longValue = null;
    try {
      longValue = Long.valueOf(map.get(key));
    } catch (NumberFormatException e) {
      return null;
    }

    return longValue;
  }
}