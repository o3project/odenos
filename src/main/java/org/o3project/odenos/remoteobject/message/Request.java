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

package org.o3project.odenos.remoteobject.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

/**
 * Request message.
 *
 */
public class Request extends MessageBodyUnpacker {

  private static final int MSG_NUM = 5;

  public String objectId;
  public Method method;
  public String path;
  public String txid;

  public enum Method {
    GET, PUT, POST, DELETE
  }

  /**
   * Constructor.
   * @deprecated {@link #Request(String, Method, String, Object)}.
   */
  @Deprecated
  public Request() {
  }

  /**
   * Constructor.
   * @param objectId object ID.
   * @param method a method.
   * @param path a path.
   * @param txid transaction ID.
   * @param body contents.
   */
  public Request(String objectId, Method method, String path, String txid, Object body) {
    this.objectId = objectId;
    this.method = method;
    this.path = path;
    this.txid = txid;
    this.body = body;
  }

  /**
   * Set txid in request object.
   * @param txid transaction ID.
   */
  public void setRequestTxid(String txid) {
    this.txid = txid;
  }


  @Override
  public void readFrom(Unpacker unpacker) throws IOException {
    unpacker.readArrayBegin();
    objectId = unpacker.readString();
    method = Method.valueOf(unpacker.readString());
    path = unpacker.readString();
    txid = unpacker.readString();
    bodyValue = unpacker.readValue();
    unpacker.readArrayEnd();
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.writeArrayBegin(MSG_NUM);
    packer.write(objectId);
    packer.write(method.name());
    packer.write(path);
    packer.write(txid);
    if (bodyValue != null) {
      packer.write(bodyValue);
    } else {
      packer.write(body);
    }
    packer.writeArrayEnd();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);

    sb.append("objectId", objectId);
    sb.append("method", method);
    sb.append("path", path);
    sb.append("body", body);

    return sb.toString();
  }

}
