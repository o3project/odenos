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

package org.o3project.odenos.core.component.network.flow.query;

import org.o3project.odenos.core.component.network.BasicQuery;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;

import java.util.Map;

/**
 * Prepares a query for BasicFlowMatch class.
 *
 */
public class BasicFlowMatchQuery {

  protected Map<String, String> match = null;
  protected String type = null;
  protected String inNode = null;
  protected String inPort = null;

  /**
   * Constructor.
   * @deprecated @see #BasicFlowMatchQuery(java.util.Map)
   */
  @Deprecated
  public BasicFlowMatchQuery() {
  }

  /**
   * Constructor.
   * @param match match conditions.
   */
  public BasicFlowMatchQuery(Map<String, String> match) {
    this.match = match;
  }

  /**
   * Parse queries.
   * @return true if queries could be parsed.
   */
  public boolean parse() {
    if (this.match == null) {
      return true;
    }
    if (!this.match.containsKey("type")) {
      return false;
    }
    this.type = this.match.remove("type");
    if (!BasicQuery.checkMapExactly(this.match, new String[] { "in_node",
        "in_port" })) {
      return false;
    }
    this.inNode = this.match.remove("in_node");
    this.inPort = this.match.remove("in_port");

    return true;
  }

  /**
   * Returns true if a condition match exactly.
   *
   * @param target condition to match.
   * @return true if condition is matched exactly.
   */
  public boolean matchExactly(BasicFlowMatch target) {
    if (this.inNode != null && !this.inNode.equals(target.inNode)) {
      return false;
    }
    if (this.inPort != null && !this.inPort.equals(target.inPort)) {
      return false;
    }
    return true;
  }

}