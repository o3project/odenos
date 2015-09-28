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

package org.o3project.odenos.component.slicer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.io.IOException;

/**
 * To provide rules for dividing the topology. SliceCondition is abstract class.
 *
 */
public abstract class SliceCondition implements MessagePackable {

  /** logger. */
  private static final Logger log = LogManager.getLogger(SliceCondition.class);

  private String id;
  private String type;
  private String connection;

  /**
   * Constructor.
   * @throws Exception if parameter is wrong.
   */
  public SliceCondition() throws Exception {
    this.id = null;
    this.type = null;
    this.connection = null;
  }

  /**
   * Constructor.
   * @param setId ID that is unique in the ODENOS.
   * @param setType SliceCondition Type
   * @param setConnection Sliver's ComponentConnectionProperty id.
   * @throws Exception if parameter is wrong.
   */
  public SliceCondition(final String setId,
      final String setType,
      final String setConnection)
      throws Exception {
    this.id = setId;
    this.type = setType;
    this.connection = setConnection;

  }

  // getter
  /**
   * Returns a condition ID.
   * @return condition ID.
   */
  public final String getId() {
    return id;
  }

  /**
   * Returns a type of condition.
   * @return type of condition.
   */
  public final String getType() {
    return type;
  }

  /**
   * Returns a connection ID.
   * @return connection ID.
   */
  public final String getConnection() {
    return connection;
  }

  protected final void setId(final String id) {
    this.id = id;
  }

  protected final void setType(final String type) {
    this.type = type;
  }

  protected final void setConnection(final String connection) {
    this.connection = connection;
  }

  /*
   * DAO
   */

  /*
   * (non-Javadoc)
   * @see org.msgpack.MessagePackable#readFrom(org.msgpack.unpacker.Unpacker)
   */
  @Override
  public void readFrom(final Unpacker upk) throws IOException {

    if (upk == null) {
      throw new IllegalArgumentException("unpacker is null");
    }

    int size = upk.readMapBegin();
    if (size != 3) {
      throw new IOException("invalid unpacker size");
    }

    try {
      while (size-- > 0) {
        doReadFrom(upk);
      }

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50079, LogMessage.getSavedTxid(), "fail to read packer"), ex);
      throw ex;

    } finally {
      upk.readMapEnd();
    }
  }

  protected void doReadFrom(Unpacker upk) throws IOException {

    try {
      switch (readString(upk)) {
        case "id":
          setId(readString(upk));
          break;
        case "type":
          setType(readString(upk));
          break;
        case "connection":
          setConnection(readString(upk));
          break;
        default:
          break;
      }

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50052, LogMessage.getSavedTxid(), ex.getMessage()), ex);
      throw ex;
    }
  }

  protected String readString(Unpacker upk) throws IOException {

    if (upk == null) {
      throw new IllegalArgumentException("unpacker is null");
    }

    try {
      if (upk.getNextType() == ValueType.NIL) {
        upk.readNil();
        return null;

      } else {
        String str = upk.readString();
        return str;
      }
    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50052, LogMessage.getSavedTxid(), ex.getMessage()), ex);
      throw ex;
    }
  }

  /*
   * (non-Javadoc)
   * @see org.msgpack.MessagePackable#writeTo(org.msgpack.packer.Packer)
   */
  @Override
  public void writeTo(final Packer pk) throws IOException {

    if (pk == null) {
      throw new IllegalArgumentException("packer is null");
    }

    try {
      pk.writeMapBegin(3);
      doWriteTo(pk);

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50080, LogMessage.getSavedTxid(), "fail to write packer"), ex);
      throw ex;

    } finally {
      pk.writeMapEnd();
    }
  }

  protected void doWriteTo(Packer pk) throws IOException {

    try {
      pk.write("id");
      pk.write(getId());

      pk.write("type");
      pk.write(getType());

      pk.write("connection");
      pk.write(getConnection());

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50052, LogMessage.getSavedTxid(), ex.getMessage()), ex);
      throw ex;
    }

  }

  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("id", getId());
    sb.append("type", getType());
    sb.append("connection", connection);

    return sb.toString();

  }

}
