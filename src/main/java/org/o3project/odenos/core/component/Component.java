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

package org.o3project.odenos.core.component;

import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

/**
 * Component.
 *
 */
public abstract class Component extends RemoteObject {

  /**
   * Component Constructor.
   * @param objectId ID for Object
   * @param dispatcher MessageDispatcher
   */
  public Component(final String objectId, final MessageDispatcher dispatcher) {
    super(objectId, dispatcher);
    this.setSuperType();
    this.setDescription();
    this.setConnectionTypes();
    resetEventSubscription();
  }

  /**
   * Get Super Type of Component. Need to Implement at Inheritance Logic Component.
   *
   * @return Super Type of Component
   */
  protected abstract String getSuperType();

  /**
   * Get Description of Component. Need to Implement at Inheritance Logic Component.
   *
   * @return Description of Component
   */
  protected abstract String getDescription();

  /**
   * Get Connection Type which can be connected to Component.
   *
   * @return Connection Types of Component.
   */
  protected String getConnectionTypes() {
    return "";
  }

  /**
   * Set SuperType to Self ObjectProperty.
   */
  protected final void setSuperType() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.OBJECT_SUPER_TYPE,
        this.getSuperType());
  }

  /**
   * Set Description to Self ObjectProperty.
   */
  protected final void setDescription() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.DESCRIPTION,
        this.getDescription());
  }

  /**
   * Set Connection Types to Self ObjectProperty.
   */
  protected final void setConnectionTypes() {
    this.objectProperty.setProperty(
        ObjectProperty.PropertyNames.CONNECTION_TYPES,
        this.getConnectionTypes());
  }

  protected void resetEventSubscription() {
    if (this.messageDispatcher == null) {
      return;
    }

    eventSubscription.clearFilter();
    eventSubscription.addFilter(
        this.messageDispatcher.getSystemManagerId(),
        ComponentConnectionChanged.TYPE);
    try {
      this.applyEventSubscription();
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }
}
