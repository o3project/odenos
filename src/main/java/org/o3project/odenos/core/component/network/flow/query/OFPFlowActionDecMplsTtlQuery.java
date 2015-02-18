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
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionDecMplsTtl;

import java.util.Map;

public class OFPFlowActionDecMplsTtlQuery extends FlowActionQuery {

  /**
   * Constructor.
   * @param params action conditions.
   */
  public OFPFlowActionDecMplsTtlQuery(Map<String, String> params) {
    super(params);
  }

  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }

    if (!BasicQuery.checkMapExactly(this.actions, new String[] {})) {
      return false;
    }
    return true;
  }

  @Override
  public boolean matchExactly(FlowAction target) {
    if (target == null || !target.getType().equals(
        OFPFlowActionDecMplsTtl.class.getSimpleName())) {
      return false;
    }
    return true;
  }

}
