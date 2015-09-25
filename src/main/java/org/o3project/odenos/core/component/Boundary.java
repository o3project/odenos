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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.o3project.odenos.remoteobject.message.OdenosMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

/**
 * Boundary abstract class.
 * Connection rule between heterogeneous networks.
 *
 */
@Message
public abstract class Boundary extends OdenosMessage {

  /** logger. */
  @Ignore
  private static final Logger log = LogManager.getLogger(Boundary.class);

  /** ID that is unique in the ODENOS. */
  @NotNullable
  private String id;

  /** Boundary Type. */
  private String type;

  /**
   * Boundary Constructor.
   * @deprecated uses this constructor for MessagePack
   */
  @Deprecated
  public Boundary() {
  }

  /**
   * Boundary Constructor.
   * @param id ID that is unique in the ODENOS
   * @param type Boundary Type is "Federator"
   */
  public Boundary(String id, String type) {

    if (StringUtils.isBlank(id)) {
      log.error(LogMessage.buildLogMessage(50064, LogMessage.getTxid(), "id is null"));
      throw new IllegalArgumentException("id is null");
    }

    if (StringUtils.isBlank(type)) {
      log.error(LogMessage.buildLogMessage(50065, LogMessage.getTxid(), "type is null"));
      throw new IllegalArgumentException("type is null");
    }

    this.id = id;
    this.type = type;
  }

  /**
   * Returns an ID of boundary.
   * @return ID that is unique in the ODENOS
   */
  public String getId() {
    return id;
  }

  /**
   * Sets an ID of boundary.
   * @param id ID that is unique in the ODENOS
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns a type of object.
   * @return Boundary Type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets a type of object.
   * @param type Boundary Type
   */
  public void setType(String type) {
    this.type = type;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("id", id);
    sb.append("type", type);
    return sb.toString();

  }

}
