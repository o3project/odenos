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

package org.o3project.odenos.core.manager.system.event;

import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.event.BaseObjectChanged;

/**
 * The event to notify of change in ObjectProperty (in ComponentManager).
 *
 */
public class ComponentManagerChanged extends
    BaseObjectChanged<ObjectProperty> {

  public static enum Action {
    add, delete, update;
  }

  public static final String TYPE = "ComponentManagerChanged";

  /**
   * Constructor.
   */
  public ComponentManagerChanged() {
    super(ObjectProperty.class);
  }

  /**
   * Constructor.
   * @param action actions. "add", "delete", or "update"
   * @param prev previous ObjectProperty.
   * @param curr current ObjectProperty.
   */
  public ComponentManagerChanged(
      final String action,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    super(action, prev, curr);
  }
}