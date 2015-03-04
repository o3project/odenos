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

package org.o3project.odenos.remoteobject.manager.component;

import org.o3project.odenos.core.component.Component;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.manager.component.event.ComponentChanged;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ComponentManager class manages components life-cycle.
 *
 */
public class ComponentManager extends RemoteObject {

  private static final Logger log = LoggerFactory.getLogger(ComponentManager.class);

  protected SystemManagerInterface sysMngIf;
  protected Map<String, Class<? extends Component>> componentClasses;
  protected Map<String, Component> components;

  private final RequestParser<IActionCallback> parser;
  private static final String COMP_ID = "comp_id";

  /**
   * Constructor.
   * @param objectId object ID.
   * @param baseUri base URI.
   * @param dispatcher Message Dispatcher object.
   * @deprecated {@link #ComponentManager(String, MessageDispatcher)}
   */
  @Deprecated
  public ComponentManager(String objectId, String baseUri,
      MessageDispatcher dispatcher) {
    this(objectId, dispatcher);
  }

  /**
   * Constructor.
   * @param objectId object ID.
   * @param dispatcher Message Dispatcher object.
   */
  public ComponentManager(String objectId, MessageDispatcher dispatcher) {
    super(objectId, dispatcher);
    componentClasses = new HashMap<String, Class<? extends Component>>();
    components = new HashMap<String, Component>();

    this.getProperty().setObjectState(ObjectProperty.State.RUNNING);
    this.sysMngIf = new SystemManagerInterface(dispatcher, objectId);

    parser = this.createParser();
  }

  /**
   * Register to system manager.
   * @throws Exception if an error occurs.
   */
  public void registerToSystemManager() throws Exception {
    Response rsp = this.sysMngIf.putComponentMng(objectProperty);
    if (!rsp.statusCode.equals(Response.OK)) {
      throw new Exception("failed 'PUT component_managers'");
    }

    this.registerEventManager();
    this.subscribeEvents();
    this.registerComponentManagers();
    return;
  }

  private void registerEventManager() throws Exception {
    String eventManagerId = this.getEventManagerId();

    ObjectProperty eventMngObj = this.sysMngIf.getObject(eventManagerId);
    if (eventMngObj == null) {
      log.error("Internal Error to Get objects/" + eventManagerId);
      throw new Exception();
    }

    try {
      this.addRemoteObject(eventManagerId);
    } catch (IOException e) {
      log.error("Failure to addRemoteObject");
      throw new Exception();
    }
    return;
  }

  private void subscribeEvents() throws Exception {
    this.eventSubscription.addFilter(
        this.getSystemManagerId(),
        ComponentManagerChanged.TYPE);
    Response rsp = this.applyEventSubscription();
    if (!rsp.statusCode.equals(Response.OK)) {
      log.error("Can't subscribe the ComponentManagerChanged.");
      throw new Exception();
    }
  }

  private void registerComponentManagers() throws Exception {
    ObjectPropertyList componentManagers = this.sysMngIf.getComponentMngs();
    if (componentManagers == null) {
      log.error("Internal Error to Get component_managers.");
      throw new Exception();
    }
    for (ObjectProperty componentManager : componentManagers) {
      this.registerOtherComponentManager(componentManager);
    }
    return;
  }

  private void registerOtherComponentManager(ObjectProperty componentManager)
      throws Exception {
    if (this.getObjectId().equals(componentManager.getObjectId())) {
      return;
    }
    try {
      this.addRemoteObject(componentManager.getObjectId());
    } catch (IOException e) {
      log.error("Failure to addRemoteObject");
      throw new Exception();
    }
    return;
  }

  private void unregisterComponentManager(String objectId) throws Exception {
    try {
      this.removeRemoteObject(objectId);
    } catch (IOException e) {
      log.error("Failure to unregisterComponentManager");
      throw new Exception();
    }
    return;
  }

  /**
   * Register a type of component.
   * @param component class of component.
   */
  public void registerComponentType(Class<? extends Component> component) {
    String componentType = component.getSimpleName();

    if (!componentClasses.containsKey(componentType)) {
      componentClasses.put(componentType, component);
      String componentTypes = this.objectProperty.getProperty(
          ObjectProperty.PropertyNames.COMPONENT_TYPES);
      if (componentTypes == null) {
        componentTypes = componentType;
      } else {
        componentTypes += "," + componentType;
      }
      this.objectProperty.setProperty(
          ObjectProperty.PropertyNames.COMPONENT_TYPES,
          componentTypes);
    }
  }

  @Override
  protected Response onRequest(Request request) {
    log.debug("onRequest: " + request.method + ", " + request.path);

    RequestParser<IActionCallback>.ParsedRequest parsed = parser
        .parse(request);
    Response response = null;

    IActionCallback callback = parsed.getResult();
    if (callback == null) {
      return new Response(Response.BAD_REQUEST, null);
    }
    try {
      response = callback.process(parsed);
    } catch (Exception e) {
      log.error("Exception Request: " + request.method + ", "
          + request.path);
      response = new Response(Response.BAD_REQUEST, null);
    }
    if (response == null) {
      response = new Response(Response.BAD_REQUEST, null);
    }
    return response;
  }

  private RequestParser<IActionCallback> createParser() {
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
                return getComponents();
              }
            });
        addRule(Request.Method.GET,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentId(parsed.getParam(COMP_ID));
              }
            });
        addRule(Request.Method.PUT,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return putComponentId(
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
                return deleteComponentId(parsed
                    .getParam(COMP_ID));
              }
            });
      }
    };
  }

  protected Response getComponentTypes() {
    Map<String, ComponentType> compTypes = new HashMap<>();

    for (String cmType : componentClasses.keySet()) {
      Component component = null;
      String objectId = String.format("%s_%s", this.getObjectId(), cmType);
      try {
        Class<? extends Component> componentClass = componentClasses.get(cmType);
        Constructor<? extends Component> ct = null;
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

  protected Response getComponents() {
    Map<String, ObjectProperty> componentsObj = new HashMap<String, ObjectProperty>();
    for (Entry<String, Component> e : components.entrySet()) {
      componentsObj.put(e.getKey(), e.getValue().getProperty());
    }
    return new Response(Response.OK, componentsObj);
  }

  protected Response getComponentId(String objectId) {
    Component component = components.get(objectId);
    if (component == null) {
      return new Response(Response.NOT_FOUND, null);
    }
    return new Response(Response.OK, component.getProperty());
  }

  protected Response putComponentId(
      String objectId, ObjectProperty prop) {
    if (prop == null) {
      return new Response(Response.BAD_REQUEST,
          "Bad format: ObjectProperty is expected");
    }

    if (!componentClasses.containsKey(prop.getObjectType())) {
      return new Response(Response.BAD_REQUEST, "Error unknown type ");
    }

    if (components.containsKey(objectId)) {
      return new Response(Response.CONFLICT,
          "Component is already created");
    }
    prop.setProperty(
        ObjectProperty.PropertyNames.OBJECT_ID, objectId);

    Component component = null;
    try {
      Class<? extends Component> componentClass = componentClasses
          .get(prop.getObjectType());
      Constructor<? extends Component> ct = null;
      try {
        ct = componentClass.getConstructor(String.class,
            MessageDispatcher.class);
        component = ct.newInstance(objectId, messageDispatcher);
      } catch (NoSuchMethodException e) {
        ct = componentClass.getConstructor(String.class,
            String.class, MessageDispatcher.class);
        component = ct.newInstance(objectId,
            objectProperty.getBaseUri(), messageDispatcher);
      }
      components.put(objectId, component);
      if (component.onInitialize(prop)) {
        component.setState(ObjectProperty.State.RUNNING);
      } else {
        component.setState(ObjectProperty.State.ERROR);
      }
    } catch (Exception e) {
      return new Response(Response.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    componentChanged(ComponentChanged.Action.add.name(), null, prop);

    return new Response(Response.CREATED, component.getProperty());
  }

  protected Response deleteComponentId(String objectId) {
    if (components.containsKey(objectId)) {
      Component component = components.get(objectId);
      ObjectProperty prev = (ObjectProperty) component
          .getProperty().clone();
      component.onFinalize();
      components.remove(objectId);

      componentChanged(ComponentChanged.Action.delete.name(), prev, null);
    }
    return new Response(Response.OK, null);
  }

  protected final void componentChanged(final String action,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    ComponentChanged msg = new ComponentChanged(action, prev, curr);
    try {
      publishEvent(ComponentChanged.TYPE, msg);
    } catch (Exception e) {
      log.error("Failed to ComponentChanged");
    }
  }

  @Override
  protected void onEvent(Event event) {
    if (event.eventType.equals(
        ComponentManagerChanged.TYPE)) {
      try {
        ComponentManagerChanged prop = event
            .getBody(ComponentManagerChanged.class);
        if (prop.action().equals(
            ComponentManagerChanged.Action.add.name())) {
          this.registerOtherComponentManager(prop.curr());
        } else if (prop.action().equals(
            ComponentManagerChanged.Action.delete.name())) {
          this.unregisterComponentManager(prop.prev().getObjectId());
        }
      } catch (Exception e) {
        log.error("Can't register ComponentManager.", e);
      }
    }
  }
}
