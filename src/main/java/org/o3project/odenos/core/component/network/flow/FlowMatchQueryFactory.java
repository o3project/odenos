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

import org.o3project.odenos.core.component.network.flow.query.BasicFlowMatchQuery;
import org.o3project.odenos.core.util.InstanceCreator;

import java.util.Map;

/**
 * Factory of FlowMatchQuery class.
 *
 */
public class FlowMatchQueryFactory {

  private static String PACKAGEPATH =
      BasicFlowMatchQuery.class.getPackage().getName() + ".";

  /**
   * Constructor.
   * @deprecated @see #create(java.lang.Map)
   */
  @Deprecated
  public FlowMatchQueryFactory() {
  }

  /**
   * Create a BasicFlowMatchQuery object.
   * @param actions Map of actions.
   * @return BasicFlowMatchQuery object.
   */
  public static BasicFlowMatchQuery create(Map<String, String> actions) {
    BasicFlowMatchQuery query = null;
    if (actions == null) {
      return null;
    }
    String type = actions.get("type");
    if ("BasicFlowMatch".equals(type)) {
      query = new BasicFlowMatchQuery(actions);
    } else {
      String className = FlowMatchQueryFactory.PACKAGEPATH + type
          + "Query";
      Class<?>[] types = { Map.class };
      Object[] args = { actions };
      query = (BasicFlowMatchQuery) InstanceCreator.create(className,
          types, args);
    }
    return query;
  }
}