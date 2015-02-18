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

import org.o3project.odenos.core.component.network.flow.query.FlowActionOutputQuery;
import org.o3project.odenos.core.component.network.flow.query.FlowActionQuery;
import org.o3project.odenos.core.util.InstanceCreator;

import java.util.Map;

/**
 * Factory of FlowActionQuery class.
 *
 */
public class FlowActionQueryFactory {

  private static String PACKAGEPATH =
      FlowActionQuery.class.getPackage().getName() + ".";

  /**
   * Constructor.
   * @deprecated @see #create(java.lang.Map)
   */
  @Deprecated
  public FlowActionQueryFactory() {
  }

  /**
   * Create a FlowActionQuery object.
   * @param params parameters.
   * @return FlowActionQuery object.
   */
  public static FlowActionQuery create(Map<String, String> params) {
    FlowActionQuery query = null;
    if (params == null) {
      return null;
    }
    String type = params.get("type");
    if ("BasicFlowActionOutput".equals(type)) {
      query = new FlowActionOutputQuery(params);
    } else {
      String className = FlowActionQueryFactory.PACKAGEPATH + type
          + "Query";
      Class<?>[] types = { Map.class };
      Object[] args = { params };
      query = (FlowActionQuery) InstanceCreator.create(className, types,
          args);
    }
    return query;
  }
}