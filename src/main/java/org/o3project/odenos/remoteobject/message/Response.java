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
 * Response message.
 *
 */
public class Response extends MessageBodyUnpacker {

  public static final int OK = 200;
  public static final int CREATED = 201;
  public static final int ACCEPTED = 202;
  public static final int NO_CONTENT = 204;
  public static final int BAD_REQUEST = 400;
  public static final int FORBIDDEN = 403;
  public static final int NOT_FOUND = 404;
  public static final int METHOD_NOT_ALLOWED = 405;
  public static final int CONFLICT = 409;
  public static final int INTERNAL_SERVER_ERROR = 500;

  private static final int MSG_NUM = 2;

  public Integer statusCode;

  /**
   * Return true if status isn't OK.
   * @param method a method.
   * @return true if status isn't OK.
   */
  public boolean isError(String method) {
    if (method.equals("GET") || method.equals("DELETE")) {
      if (!statusCode.equals(OK)) {
        return true;
      }
    } else if (method.equals("PUT")) {
      if ((!statusCode.equals(OK)) && (!statusCode.equals(CREATED))) {
        return true;
      }
    } else {
      if ((!statusCode.equals(OK)) && (!statusCode.equals(CREATED))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Constructor.
   * @deprecated {@link #Response(Integer, Object)}.
   */
  @Deprecated
  public Response() {
  }

  /**
   * Constructor.
   * @param statusCode status code.
   * @param body body.
   */
  public Response(Integer statusCode, Object body) {
    this.statusCode = statusCode;
    this.body = body;
  }

  @Override
  public void readFrom(Unpacker unpacker) throws IOException {
    unpacker.readArrayBegin();
    statusCode = unpacker.readInt();
    bodyValue = unpacker.readValue();
    unpacker.readArrayEnd();
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.writeArrayBegin(MSG_NUM);
    packer.write(statusCode);
    if (bodyValue != null) {
      packer.write(bodyValue);
    } else {
      packer.write(body);
    }
    packer.writeArrayEnd();
  }

  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);

    sb.append("statusCode", statusCode);
    sb.append("body", body);

    return sb.toString();
  }

}
