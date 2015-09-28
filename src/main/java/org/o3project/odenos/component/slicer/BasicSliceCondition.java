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
import org.msgpack.unpacker.Unpacker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.io.IOException;

/**
 * SliceCondition is setting Slice rules.
 * BasicSliceCondition support match "in_node" and "in_port".
 *
 */
public class BasicSliceCondition extends SliceCondition
    implements MessagePackable {

  /** logger. */
  private static final Logger log = LogManager
      .getLogger(BasicSliceCondition.class);

  protected String matchInNode;
  protected String matchInPort;

  /**
   * Constructor.
   * @throws Exception if parameter is wrong.
   */
  public BasicSliceCondition() throws Exception {
    super(null, null, null);
    this.matchInNode = null;
    this.matchInPort = null;
  }

  /**
   * Constructor.
   * @param id ID that is unique in the ODENOS.
   * @param type SliceCondition type is "BasicSliceCondition".
   * @param connection Sliver's ComponentConnectionProperty id
   * @param inNode node id.
   * @param inPort port id.
   * @throws Exception if parameter is wrong.
   */
  public BasicSliceCondition(final String id,
      final String type,
      final String connection,
      final String inNode,
      final String inPort)
      throws Exception {

    super(id, type, connection);
    this.matchInNode = inNode;
    this.matchInPort = inPort;
  }

  // getter
  /**
   * Returns a node ID.
   * @return node ID.
   */
  public final String getInNode() {
    return matchInNode;
  }

  /**
   * Returns a port ID.
   * @return port ID.
   */
  public final String getInPort() {
    return matchInPort;
  }

  // setter
  protected final void setInNode(final String inNode) {
    matchInNode = inNode;
  }

  protected final void setInPort(final String inPort) {
    matchInPort = inPort;
  }

  /*
   * DAO
   */

  @Override
  public void readFrom(final Unpacker upk) throws IOException {

    if (upk == null) {
      throw new IllegalArgumentException("unpacker is null");
    }

    int size = upk.readMapBegin();
    if (size < 4 || 5 < size) {
      throw new IOException("invalid unpaker size");
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

  @Override
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
        case "in_node":
          setInNode(readString(upk));
          break;
        case "in_port":
          setInPort(readString(upk));
          break;
        default:
          break;
      }

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50052, LogMessage.getSavedTxid(), ex.getMessage()), ex);
      throw ex;
    }
  }

  @Override
  public void writeTo(final Packer pk) throws IOException {

    if (pk == null) {
      throw new IllegalArgumentException("packer is null");
    }

    try {
      pk.writeMapBegin(5);
      doWriteTo(pk);

    } catch (IOException ex) {
      log.error(LogMessage.buildLogMessage(50080, LogMessage.getSavedTxid(), "fail to write packer"), ex);
      throw ex;

    } finally {
      pk.writeMapEnd();
    }
  }

  @Override
  protected void doWriteTo(Packer pk) throws IOException {

    try {
      super.doWriteTo(pk);

      pk.write("in_node");
      pk.write(getInNode());

      pk.write("in_port");
      pk.write(getInPort());

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
    sb.append("connection", getConnection());
    sb.append("in_node", matchInNode);
    sb.append("in_port", matchInPort);

    return sb.toString();
  }

}
