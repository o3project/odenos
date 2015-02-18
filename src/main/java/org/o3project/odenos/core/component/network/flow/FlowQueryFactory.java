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

package org.o3project.odenos.core.component.network.flow;

import org.o3project.odenos.core.component.network.flow.query.BasicFlowQuery;
import org.o3project.odenos.core.component.network.flow.query.FlowQuery;
import org.o3project.odenos.core.util.InstanceCreator;

/**
 * Factory of FlowQuery class.
 *
 */
public class FlowQueryFactory {

  private static String PACKAGEPATH =
      FlowQuery.class.getPackage().getName() + ".";

  /**
   * Constructor.
   * @deprecated @see #create(java.lang.String)
   */
  @Deprecated
  public FlowQueryFactory() {
  }

  /**
   * Create a FlowQuery object.
   * @param queriesString Queries string.
   * @return FlowQuery object.
   */
  public static FlowQuery create(String queriesString) {
    FlowQuery query = null;
    String type = FlowQueryFactory.getType(queriesString);
    if (type == null) {
      return null;
    }
    if ("BasicFlow".equals(type)) {
      query = new BasicFlowQuery(queriesString);
    } else {
      String className = FlowQueryFactory.PACKAGEPATH + type + "Query";
      Class<?>[] types = { String.class };
      Object[] args = { queriesString };
      query = (FlowQuery) InstanceCreator.create(className, types, args);
    }

    return query;
  }

  private static String getType(String queriesString) {
    if (queriesString == null) {
      return null;
    }
    String[] queries = queriesString.split("&");
    for (String s : queries) {
      String[] query = s.split("=");
      if (query.length != 2) {
        continue;
      }
      if (query[0].equals("type")) {
        return query[1];
      }
    }
    return null;
  }
}