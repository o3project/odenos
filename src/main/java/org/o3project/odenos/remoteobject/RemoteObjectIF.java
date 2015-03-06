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

package org.o3project.odenos.remoteobject;

import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteObjectIF {
  private static final Logger log = LoggerFactory.getLogger(RemoteObjectIF.class);

  public static final String PATH_PROPETY = "property";
  public static final String PATH_SETTINGS = "settings";

  private final MessageDispatcher dispatcher;
  private final String id;
  private final String sourceObjectId;

  @Deprecated
  public RemoteObjectIF(final MessageDispatcher dispatcher, final String id) {
    this.dispatcher = dispatcher;
    this.id = id;
    this.sourceObjectId = null;
  }

  public RemoteObjectIF(final String sourceObjectId, final MessageDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    this.id = dispatcher.getSystemManagerId();
    this.sourceObjectId = sourceObjectId;
  }

  public MessageDispatcher dispatcher() {
    return this.dispatcher;
  }

  public final String id() {
    return this.id;
  }
  
  public final String getSourceObjectId() {
    return this.sourceObjectId;
  }

  public final ObjectProperty getProperty() {
    return this.get(PATH_PROPETY).getBody2(ObjectProperty.class);
  }

  public final ObjectProperty setProperty(final ObjectProperty body) {
    return this.put(PATH_PROPETY, body).getBody2(ObjectProperty.class);
  }

  public final ObjectSettings getSettings() {
    return this.get(PATH_SETTINGS).getBody2(ObjectSettings.class);
  }

  public final ObjectSettings setSettings(final ObjectSettings body) {
    return this.put(PATH_SETTINGS, body).getBody2(ObjectSettings.class);
  }

  /**
   * Post request
   * @param path request path.
   * @param body request body.
   * @return response body.
   */
  public Response post(final String path, final Object body) {
    Response resp = this.sendRequest(Request.Method.POST, path, body);
    if (resp == null || resp.isError("POST")) {
      log.error("invalid POST:" + resp.statusCode);
    }
    return resp;
  }

  /**
   * Put request.
   * @param path request path.
   * @param body request body.
   * @return response body.
   */
  public Response put(final String path, final Object body) {
    Response resp = this.sendRequest(Request.Method.PUT, path, body);
    if (resp == null || resp.isError("PUT")) {
      log.error("PUT failed:" + resp.statusCode);
    }
    return resp;
  }

  /**
   * Get object to request.
   * @param path path to get object.
   * @return response body.
   */
  public final Response get(final String path) {
    Response resp = this.sendRequest(Request.Method.GET, path, null);
    if (resp.isError("GET")) {
      log.error("GET failed:" + resp.statusCode);
    }
    return resp;
  }

  public final Response delete(final String path) {
    return this.delete(path, null);
  }

  /**
   * Delete object.
   * @param path path to delete object.
   * @param body body to delete object.
   * @return response body.
   */
  public final Response delete(final String path, final Object body) {
    Response resp = this.sendRequest(Request.Method.DELETE, path, body);
    if (resp.isError("DELETE")) {
      log.error("DELETE failed:" + resp.statusCode);
    }
    return resp;
  }

  private Response sendRequest(final Request.Method method, final String path, final Object body) {
    Request req = new Request(this.id(), method, path, body);
    try {
      return this.dispatcher().requestSync(req, sourceObjectId);
    } catch (Exception e) {
      //e.printStackTrace();
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }
}
