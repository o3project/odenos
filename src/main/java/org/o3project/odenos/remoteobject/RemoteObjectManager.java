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

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RemoteObjectManager extends RemoteObject {
  private static final Logger log = LogManager.getLogger(RemoteObjectManager.class);

  public static final String ATTR_OBJTYPE = "remote_object_types";
  public static final String ZK_CMPMGR_PATH = "/component_managers";
  public static final String ZK_CMP_PATH = "/components";
  private static final String OBJ_ID = "obj_id";

  protected RequestParser<IActionCallback> parser;
  protected Map<String, Class<? extends RemoteObject>> classList =
      new HashMap<String, Class<? extends RemoteObject>>();
  protected Map<String, RemoteObject> objects = new HashMap<String, RemoteObject>();

  /**
   * Constructor.
   * @param id Remote Object id.
   * @param disp Message Dispatcher id.
   */
  public RemoteObjectManager(String id, MessageDispatcher disp) {
    super(id, disp);
    this.objectProperty.setProperty(ATTR_OBJTYPE, "");
    parser = this.createParser();
  }

  /**
   * register Remote Objects.
   * @param classes Remote Object classes.
   */
  public void registerRemoteObjects(Set<Class<? extends RemoteObject>> classes) {
    for (Class<? extends RemoteObject> clazz : classes) {
      String type = clazz.getSimpleName();
      if (!classList.containsKey(type)) {
        classList.put(type, clazz);
      }
    }

    this.objectProperty.setProperty(ATTR_OBJTYPE, StringUtils.join(classList.keySet(), ","));
  }

  @Override
  protected Response onRequest(Request request) {
    LogMessage.setSavedTxid(request.txid);
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "onRequest: {}, {}", request.method, request.path));

    RequestParser<IActionCallback>.ParsedRequest parsed = parser.parse(request);
    Response response = null;

    IActionCallback callback = parsed.getResult();
    if (callback == null) {
      response = new Response(Response.BAD_REQUEST, null);
      LogMessage.delSavedTxid();
      return response;
    }
    try {
      response = callback.process(parsed);
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Exception Request: {}, {}", request.method, request.path));
      response = new Response(Response.BAD_REQUEST, null);
    }
    if (response == null) {
      response = new Response(Response.BAD_REQUEST, null);
    }
    LogMessage.delSavedTxid();
    return response;
  }

  protected RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        addRule(Request.Method.GET,
            "remote_object_types",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getRemoteObjectTypes();
              }
            });
        addRule(Request.Method.GET,
            "objects",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getRemoteObjects();
              }
            });
        addRule(Request.Method.GET,
            "objects/<obj_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getRemoteObject(parsed.getParam(OBJ_ID));
              }
            });
        addRule(Request.Method.PUT,
            "objects/<obj_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return createRemoteObject(
                    parsed.getParam(OBJ_ID),
                    parsed.getRequest().getBody(ObjectProperty.class));
              }
            });
        addRule(Request.Method.DELETE,
            "objects/<obj_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteRemoteObject(parsed.getParam(OBJ_ID));
              }
            });
      }
    };
  }

  protected Response getRemoteObjectTypes() {
    return new Response(Response.OK, new ArrayList<String>(classList.keySet()));
  }

  protected Response getRemoteObjects() {
    Map<String, ObjectProperty> map = new HashMap<String, ObjectProperty>();
    for (Entry<String, RemoteObject> e : this.objects.entrySet()) {
      map.put(e.getKey(), e.getValue().getProperty());
    }
    return new Response(Response.OK, map);
  }

  protected Response getRemoteObject(String id) {
    RemoteObject object = this.objects.get(id);
    if (object == null) {
      return new Response(Response.NOT_FOUND, null);
    }
    return new Response(Response.OK, object.getProperty());
  }

  protected Response createRemoteObject(final String id, final ObjectProperty prop) {
    if (prop == null) {
      return new Response(Response.BAD_REQUEST, "Bad format: ObjectProperty is expected");
    }

    if (!classList.containsKey(prop.getObjectType())) {
      return new Response(Response.BAD_REQUEST, "Error unknown type ");
    }

    if (objects.containsKey(id)) {
      return new Response(Response.CONFLICT, "the RemoteObject is already created");
    }
    prop.setProperty(ObjectProperty.PropertyNames.OBJECT_ID, id);

    RemoteObject object = null;
    try {
      Class<? extends RemoteObject> clazz = classList.get(prop.getObjectType());
      try {
        Constructor<? extends RemoteObject> constructor =
            clazz.getConstructor(String.class, MessageDispatcher.class);
        object = constructor.newInstance(id, this.getMessageDispatcher());
      } catch (NoSuchMethodException e) {
        Constructor<? extends RemoteObject> constructor =
            clazz.getConstructor(String.class, String.class, MessageDispatcher.class);
        object = constructor.newInstance(
            id, objectProperty.getBaseUri(), this.getMessageDispatcher());
      }
      objects.put(id, object);
      if (object.onInitialize(prop)) {
        object.setState(ObjectProperty.State.RUNNING);
      } else {
        object.setState(ObjectProperty.State.ERROR);
      }
      zkCreatePath(ZK_CMP_PATH + "/" + getObjectId() + ":" +id,
          CreateMode.EPHEMERAL);
    } catch (Exception e) {
      return new Response(Response.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    return new Response(Response.CREATED, object.getProperty());
  }

  protected Response deleteRemoteObject(final String id) {
    if (objects.containsKey(id)) {
      RemoteObject object = objects.get(id);
      ObjectProperty prev = (ObjectProperty) object.getProperty().clone();
      object.onFinalize();
      objects.remove(id);
      zkDeletePath(ZK_CMP_PATH + "/" + getObjectId() + ":" +id);
    }
    return new Response(Response.OK, null);
  }
}
