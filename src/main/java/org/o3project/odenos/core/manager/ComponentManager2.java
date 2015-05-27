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

package org.o3project.odenos.core.manager;

import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.RemoteObjectManager;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.manager.component.ComponentType;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentManager2 extends RemoteObjectManager {

  public static final String ATTR_COMPTYPE = "component_types";
  private static final String COMP_ID = "comp_id";

  /**
   * Constructor.
   * @param id Object id.
   * @param disp Message Dispatcher object.
   */
  public ComponentManager2(String id, MessageDispatcher disp) {
    super(id, disp);
    this.objectProperty.setProperty(ATTR_COMPTYPE, "");
    parser = this.createParser();
    
    // Registers objectId to ZooKeeper, session timeout: 5 sec 
    keepAlive("/component_manager", 5000);
  }

  public void registerComponents(Set<Class<? extends RemoteObject>> classes) {
    this.registerRemoteObjects(classes);
    this.objectProperty.setProperty(ATTR_COMPTYPE, StringUtils.join(classList.keySet(), ","));
  }

  @Override
  protected RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        addRule(Request.Method.GET,
            "component_types",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentTypes();
              }
            });
        addRule(Request.Method.GET,
            "components",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getRemoteObjects();
              }
            });
        addRule(Request.Method.GET,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getRemoteObject(parsed.getParam(COMP_ID));
              }
            });
        addRule(Request.Method.PUT,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return createRemoteObject(
                    parsed.getParam(COMP_ID),
                    parsed.getRequest().getBody(ObjectProperty.class));
              }
            });
        addRule(Request.Method.DELETE,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteRemoteObject(parsed.getParam(COMP_ID));
              }
            });
      }
    };
  }

  protected Response getComponentTypes() {
    Map<String, ComponentType> compTypes = new HashMap<>();

    for (String cmType : classList.keySet()) {
      RemoteObject component = null;
      String objectId = String.format("%s_%s", this.getObjectId(), cmType);
      try {
        Class<? extends RemoteObject> componentClass = classList.get(cmType);
        Constructor<? extends RemoteObject> ct = null;
        try {
          ct = componentClass.getConstructor(String.class,
              MessageDispatcher.class);
          component = ct.newInstance(objectId, null);
        } catch (NoSuchMethodException e) {
          ct = componentClass.getConstructor(String.class,
              String.class, MessageDispatcher.class);
          component = ct.newInstance(objectId,
              objectProperty.getBaseUri(), null);
        }
        ObjectProperty objProp = component.getProperty();
        String type = objProp.getProperty(
            ObjectProperty.PropertyNames.OBJECT_TYPE);
        String superType = objProp.getProperty(
            ObjectProperty.PropertyNames.OBJECT_SUPER_TYPE);

        Map<String, String> connectionTypes = new HashMap<>();
        String connectionTypesStr = objProp.getProperty(
            ObjectProperty.PropertyNames.CONNECTION_TYPES);
        String[] connStrList = connectionTypesStr.split(",");
        for (String connTypeElem : connStrList) {
          String[] connTypeElemList = connTypeElem.split(":");
          if (connTypeElemList.length == 2) {
            connectionTypes.put(connTypeElemList[0], connTypeElemList[1]);
          }
        }

        String description = objProp.getProperty(
            ObjectProperty.PropertyNames.DESCRIPTION);
        compTypes.put(type,
            new ComponentType(type, superType, connectionTypes, description));
      } catch (Exception e) {
        return new Response(Response.INTERNAL_SERVER_ERROR, e.getMessage());
      }
    }
    return new Response(Response.OK, compTypes);
  }

}
