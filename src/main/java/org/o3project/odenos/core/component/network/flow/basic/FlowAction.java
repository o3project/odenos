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

package org.o3project.odenos.core.component.network.flow.basic;

import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import java.util.Map;

/**
 * Action of edge node.
 *
 */
public abstract class FlowAction extends OdenosMessage {

  /**
   * Constructor.
   */
  public FlowAction() {
  }

  /**
   * Returns true if all parameter are valid.
   * @return true if all parameter are valid.
   */
  public abstract boolean validate();

  /**
   * Returns type.
   * @return type.
   */
  public abstract String getType();

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    values.put("type", ValueFactory.createRawValue(getType()));
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof FlowAction)) {
      return false;
    }

    final FlowAction obj2 = (FlowAction) obj;

    if (!obj2.getType().equals(this.getType())) {
      return false;
    }

    return true;
  }

  @Override
  public abstract FlowAction clone();

}