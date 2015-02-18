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

package org.o3project.odenos.remoteobject.event;

import org.o3project.odenos.remoteobject.ObjectSettings;

/**
 * The event to notify of change in ObjectSettings.
 *
 */
public class ObjectSettingsChanged extends BaseObjectChanged<ObjectSettings> {

  public static enum Action {
    add, delete, update;
  }

  public static final String TYPE = "ObjectSettingsChanged";

  /**
   * Constructor.
   */
  public ObjectSettingsChanged() {
    super(ObjectSettings.class);
  }

  /**
   * Constructor.
   * @param action actions. {@link Action}
   * @param prev previous ObjectSettings
   * @param curr current ObjectSettings
   */
  public ObjectSettingsChanged(
      final String action,
      final ObjectSettings prev,
      final ObjectSettings curr) {
    super(action, prev, curr);
  }
}