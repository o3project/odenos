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

import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.ObjectSettings;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * SystemManagerInterface class.
 *
 */
public class SystemManagerInterface {
  private static final Logger log = LoggerFactory.getLogger(SystemManagerInterface.class);

  public static final String PROPETY_PATH = "property";
  public static final String SETTINGS_PATH = "settings";
  public static final String COMP_MNGS_PATH = "component_managers";
  public static final String COMP_MNG_PATH = "component_managers/%s";
  public static final String EVENT_MNGS_PATH = "event_managers";
  public static final String EVENT_MNG_PATH = "event_managers/%s";
  public static final String COMP_TYPES_PATH = "component_types";
  public static final String COMP_TYPE_PATH = "component_types/%s";
  public static final String COMPS_PATH = "components";
  public static final String COMP_PATH = "components/%s";
  public static final String CONNECTIONS_PATH = "connections";
  public static final String CONNECTION_PATH = "connections/%s";
  public static final String OBJECT_PATH = "objects/%s";

  private MessageDispatcher dispatcher;
  private String sourceObjectId = null;

  /**
   * Constructor.
   * @param dispatcher Message Dispatcher object.
   */
  @Deprecated
  public SystemManagerInterface(
      final MessageDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    log.debug("Create SystemManagerInterface : Id = '"
        + this.getSystemManagerId() + "'.");
  }

  /**
   * Constructor.
   * @param dispatcher Message Dispatcher object.
   */
  public SystemManagerInterface(
      final MessageDispatcher dispatcher,
      final String sourceObjectId) {
    this.dispatcher = dispatcher;
    this.sourceObjectId = sourceObjectId;
    log.debug("Create SystemManagerInterface : Id = '"
        + this.getSystemManagerId() + "'.");
  }
  
  /**
   * Returns a system manager ID.
   * @return value of the system manager ID.
   */
  public String getSystemManagerId() {
    if (this.dispatcher == null) {
      return null;
    }
    return this.dispatcher.getSystemManagerId();
  }

  /**
   * Returns a Message Dispatcher.
   * @return Message Dispatcher.
   */
  public MessageDispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * Sets a Message Dispatcher.
   * @param dispatcher Message Dispatcher.
   */
  public void setDispatcher(MessageDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  // //////////////////////////////////////
  //
  // Basic Request
  //
  // //////////////////////////////////////

  /**
   * Requests a "GET Property".
   * @return value of the property.
   */
  public final ObjectProperty getProperty() {
    String path = PROPETY_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectProperty.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Settings".
   * @return value of the settings.
   */
  public final ObjectSettings getSettings() {
    String path = SETTINGS_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectSettings.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Component Managers".
   * @return value of the properties.
   */
  public final ObjectPropertyList getComponentMngs() {
    String path = COMP_MNGS_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectPropertyList.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Event Managers".
   * @return value of properties.
   */
  public final ObjectProperty getEventMngs() {
    String path = EVENT_MNGS_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectProperty.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET ComponentTypes".
   * @return value of types.
   */
  public final ComponentTypesHash getComponentTypes() {
    String path = COMP_TYPES_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ComponentTypesHash.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Components".
   * @return value of properties.
   */
  public final ObjectPropertiesHash getComponents() {
    String path = COMPS_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectPropertiesHash.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Connections".
   * @return map of connections.
   */
  public final Map<String, ComponentConnection> getConnections() {
    String path = CONNECTIONS_PATH;
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBodyAsMap(ComponentConnection.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Component Manager".
   * @param compMngId component manager ID
   * @return value of properties.
   */
  public final ObjectProperty getComponentManager(
      final String compMngId) {
    String path = String.format(COMP_MNG_PATH, compMngId);
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectProperty.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET ComponentType".
   * @param compType component type.
   * @return value of properties.
   */
  public final ObjectPropertyList getComponentType(
      final String compType) {
    String path = String.format(COMP_TYPE_PATH, compType);
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectPropertyList.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Component".
   * @param compId component ID.
   * @return value of properties.
   */
  public final ObjectProperty getComponent(
      final String compId) {
    String path = String.format(COMP_PATH, compId);
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectProperty.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Connection".
   * @param connId connection ID.
   * @return value of connection.
   */
  public final ComponentConnection getConnection(
      final String connId) {
    String path = String.format(CONNECTION_PATH, connId);
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ComponentConnection.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "GET Object".
   * @param objId Object ID.
   * @return value of properties.
   */
  public final ObjectProperty getObject(
      final String objId) {
    String path = String.format(OBJECT_PATH, objId);
    log.debug("");
    Response resp = getObjectToSystemMng(path);
    if (resp == null) {
      return null;
    }
    try {
      return resp.getBody(ObjectProperty.class);
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return null;
    }
  }

  /**
   * Requests a "PUT Property".
   * @param body a property object.
   * @return response object.
   */
  public final Response putProperty(
      final ObjectProperty body) {
    String path = PROPETY_PATH;
    log.debug("");
    return putObjectToSystemMng(path, body);
  }

  /**
   * Requests a "PUT Settings".
   * @param body a setting object.
   * @return response object.
   */
  public final Response putSettings(
      final ObjectSettings body) {
    String path = SETTINGS_PATH;
    log.debug("");
    return putObjectToSystemMng(path, body);
  }

  /**
   * Requests a "PUT Connection".
   * @param body a connection.
   * @return response object.
   */
  public final Response putConnection(
      final ComponentConnection body) {
    if (body == null) {
      return new Response(Response.BAD_REQUEST, null);
    }
    String path = String.format(CONNECTION_PATH, body.getObjectId());
    log.debug("");
    return putObjectToSystemMng(path, body);
  }

  /**
   * Requests a "PUT Component Manager".
   * @param body a property object.
   * @return response object.
   */
  public final Response putComponentMng(
      final ObjectProperty body) {
    String path = String.format(COMP_MNG_PATH, body.getObjectId());
    log.debug("");
    return putObjectToSystemMng(path, body);
  }

  /**
   * Requests a "PUT Component".
   * @param body a property object.
   * @return response object.
   */
  public final Response putComponent(
      final ObjectProperty body) {
    String path = String.format(COMP_PATH, body.getObjectId());
    log.debug("");
    return putObjectToSystemMng(path, body);
  }

  /**
   * Requests a "POST Component".
   * @param body a property object.
   * @return response object.
   */
  public final Response postComponent(
      final ObjectProperty body) {
    String path = COMPS_PATH;
    log.debug("");
    return postObjectToSystemMng(path, body);
  }

  /**
   * Requests a "POST Connection".
   * @param body a connection.
   * @return response object.
   */
  public final Response postConnection(
      final ComponentConnection body) {
    String path = CONNECTIONS_PATH;
    log.debug("");
    return postObjectToSystemMng(path, body);
  }

  /**
   * Requests a "DELETE Component Manager".
   * @param compMngId component manager ID.
   * @return response object.
   */
  public final Response delComponentMng(
      final String compMngId) {
    String path = String.format(COMP_MNG_PATH, compMngId);
    log.debug("");
    return delObjectToSystemMng(path);
  }

  /**
   * Requests a "DELETE Component".
   * @param compId component ID.
   * @return response object.
   */
  public final Response delComponent(
      final String compId) {
    String path = String.format(COMP_PATH, compId);
    log.debug("");
    return delObjectToSystemMng(path);
  }

  /**
   * Requests a "DELETE Connection".
   * @param connId connection ID.
   * @return response object.
   */
  public final Response delConnection(
      final String connId) {
    String path = String.format(CONNECTION_PATH, connId);
    log.debug("");
    return delObjectToSystemMng(path);
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  /**
   * Requests POST to system manager.
   * @param path a path.
   * @param body a resource.
   * @return response object.
   */
  public Response postObjectToSystemMng(
      final String path, final Object body) {
    log.debug("");

    try {
      Response resp = sendRequest(Request.Method.POST, path, body);
      if (resp.isError("POST")) {
        log.warn("invalid POST:" + resp.statusCode);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  /**
   * Requests PUT to system manager.
   * @param path a path.
   * @param body a resource.
   * @return response object.
   */
  public Response putObjectToSystemMng(
      final String path, final Object body) {
    log.debug("");

    try {
      Response resp = sendRequest(Request.Method.PUT, path, body);
      if (resp.isError("PUT")) {
        log.warn("invalid PUT:" + resp.statusCode);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  /**
   * Requests DELETE to system manager.
   * @param path a path.
   * @return response object.
   */
  public Response delObjectToSystemMng(final String path) {
    log.debug("");

    try {
      Response resp = sendRequest(Request.Method.DELETE, path, null);
      if (resp.isError("DELETE")) {
        log.warn("invalid DELETE:" + resp.statusCode);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  /**
   * Requests GET to system manager.
   * @param path a path.
   * @return response object.
   */
  public Response getObjectToSystemMng(String path) {
    log.debug("");

    try {
      Response resp = sendRequest(Request.Method.GET, path, null);
      if (resp.isError("GET")) {
        log.warn("invalid GET:" + resp.statusCode);
      }
      return resp;
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
  }

  private Response sendRequest(
      final Request.Method method, final String path, final Object body) {
    log.debug("");

    Response rsp = null;
    Request req = new Request(
        this.dispatcher.getSystemManagerId(), method, path, body);
    log.debug("   " + req.getBodyValue());
    try {
      rsp = this.dispatcher.requestSync(req, sourceObjectId);

    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    return rsp;
  }

}
