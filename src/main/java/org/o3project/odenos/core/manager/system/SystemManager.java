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

package org.o3project.odenos.core.manager.system;

import org.msgpack.type.Value;
import org.o3project.odenos.core.manager.ComponentManager2;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.event.ObjectPropertyChanged;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.manager.component.ComponentType;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This is the Class for SystemManager.
 */
public class SystemManager extends RemoteObject {

  private static final Logger log = LoggerFactory.getLogger(SystemManager.class);

  private static final int AliveIntervalTime = 5;
  private static final int WAIT_SUBSCRIPTION_TIME = 100;
  // Data for component managers. (Value:Set[compMgrId])
  private HashSet<String> componentMgrsSet = new HashSet<String>();
  private ObjectPropertiesHash compMgrsObjProps = new ObjectPropertiesHash();
  // Data for component Types. (Key: ComponentType, Value:Set[compMgrId])
  private HashMap<String, HashSet<String>> allComponentTypes =
      new HashMap<String, HashSet<String>>();
  // Data for active components. (Key: componentId, Value: State)
  private HashMap<String, String> componentStateList = new HashMap<String, String>();
  // Data for active components. (Key: componentId, Value: Componet::ObjectProperties)
  private ObjectPropertiesHash componetsObjectProperties = new ObjectPropertiesHash();

  // Data for mapping between comp_id and compmgr_id. (Key: componentId,
  // Value: compMgrId)
  private HashMap<String, String> mapCompAndCompMgr = new HashMap<String, String>();

  private ObjectProperty eventManagerProperty = null;

  private final RequestParser<IActionCallback> parser;

  // (Key: componentId, Value:BaseUri)
  private HashMap<String, String> baseUriMap = new HashMap<String, String>();

  // Note : Request and response procedures are synchronized now.
  @Deprecated
  public SystemManager(String objectId,
      String baseUri,
      MessageDispatcher dispatcher,
      ObjectProperty eventManagerPropery) {
    this(objectId, dispatcher, eventManagerPropery);
  }

  /**
   * Constructor.
   * @param objectId object ID.
   * @param dispatcher MessageDispatcher object.
   * @param eventManagerPropery properties.
   */
  public SystemManager(String objectId,
      MessageDispatcher dispatcher,
      ObjectProperty eventManagerPropery) {
    super(objectId, dispatcher);
    this.eventManagerProperty = eventManagerPropery;
    // monitorAliveOfComponents(TimeUnit.MINUTES.toMinutes(30));

    this.getProperty().setObjectState(ObjectProperty.State.RUNNING);

    this.parser = this.createParser();
  }

  // When closed, componentManager will be deleted. (Expect when
  // componentManager is finalized, the components created by it automatically
  // deleted.)
  @Override
  public void onFinalize() {
    deleteAllComponentConnection();
    deleteAllComponentManagers();
    super.onFinalize();
  }

  @Override
  protected void onEvent(Event event) {
    log.debug("onEvent: " + event.eventType);
    if (event.eventType.equals(ObjectPropertyChanged.TYPE)) {
      ObjectPropertyChanged message = event.getBody2(ObjectPropertyChanged.class);
      if (isComponentChanged(message)) {
        this.onComponentChanged(message);
        return;
      }
      this.onComponentManagerChanged(message);
    }
  }

  private void onComponentManagerChanged(ObjectPropertyChanged msg) {
    if (ObjectPropertyChanged.Action.valueOf(msg.action()) == ObjectPropertyChanged.Action.add
        || ObjectPropertyChanged.Action.valueOf(msg.action()) == ObjectPropertyChanged.Action.update) {
      log.debug("onRemoteObjectManaagerChanged: " + msg.action());
      this.updateComponentManager(msg.curr().getObjectId(), msg.curr());
    }
  }

  // //////////////////////////////////////////////////
  // ComponentChaned
  // //////////////////////////////////////////////////
  protected boolean isComponentChanged(final ObjectPropertyChanged message) {
    String id = null;
    switch (message.action()) {
      case "add":
      case "update":
        id = message.curr().getObjectId();
        break;
      case "delete":
        id = message.prev().getObjectId();
        break;
      default:
        log.debug("invalid action");
        return false;
    }
    if (this.componetsObjectProperties.containsKey(id)) {
      return true;
    }
    return false;
  }

  protected void onComponentChanged(final ObjectPropertyChanged message) {

    String compId = message.curr().getObjectId();
    ObjectProperty curr = message.curr();
    ObjectProperty prev = message.prev();

    log.debug("Recieved ComponentChangedMessag [" + message.action() + "]id:" + compId);
    log.info("Recieved ComponentChangedMessag [" + message.action() + "]id:" + compId);

    switch (message.action()) {
      case "add":
        if (this.onComponentChangedAddedPre(compId, curr)) {
          this.componetsObjectProperties.put(compId, curr);
          this.onComponentChangedAdded(compId, curr);
        }
        break;
      case "update":
        if (this.onComponentChangedUpdatePre(compId, prev, curr)) {
          this.onComponentChangedUpdate(compId, prev, curr);
        }
        break;
      case "delete":
        if (this.onComponentChangedDeletePre(compId, prev)) {
          this.onComponentChangedDelete(compId, curr);
        }
        break;
      default:
        log.debug("invalid action");
        return;
    }
  }

  protected boolean onComponentChangedAddedPre(
      final String id,
      final ObjectProperty message) {
    log.debug("");
    return true;
  }

  protected boolean onComponentChangedUpdatePre(
      final String id,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    log.debug("");
    return true;
  }

  protected boolean onComponentChangedDeletePre(
      final String id,
      final ObjectProperty message) {
    log.debug("");
    return true;
  }

  protected void onComponentChangedAdded(
      final String id,
      final ObjectProperty message) {
    log.debug("");
    this.componetsObjectProperties.put(id, message);
  }

  protected void onComponentChangedUpdate(
      final String id,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    log.debug("");
    this.componetsObjectProperties.put(id, curr);
  }

  protected void onComponentChangedDelete(
      final String id,
      final ObjectProperty message) {
    log.debug("");
    this.componetsObjectProperties.remove(id);
  }

  /**
   * Method for get destination Component ID.
   *
   * @param path
   *            Original path.
   * @return paths[0] Destination Component ID.
   */
  private String getDestinationCompId(final String path) {
    if (path == null) {
      return null;
    }

    // components/<component_id>/<path_part>
    String[] paths = path.split("^components/", 2);
    paths = paths[1].split("/");
    return paths[0];
  }

  /**
   * Method for get destination path.
   *
   * @param path
   *            Original path.
   * @return paths[1] Destination path.
   */
  private String getDestinationPath(final String path) {
    if (path == null) {
      return null;
    }

    // components/<component_id>/<path_part>
    String[] paths = path.split("^components/[^/]*/", 2);
    return paths[1];
  }

  // Not yet tested.
  /*
   * Method for monitoring the active components having timer. If
   * ActiveComponent is dead, it insert error state into the property.
   */
  @SuppressWarnings("unused")
  private void monitorAliveOfComponents(long period) {
    TimerTask task = new CheckComponentsTask(this.getProperty()
        .getBaseUri());
    Timer timer = new Timer();
    timer.schedule(task, TimeUnit.SECONDS.toSeconds(AliveIntervalTime),
        period);
  }

  // Not yet tested.
  /*
   * Method for monitoring the active components.
   */
  class CheckComponentsTask extends TimerTask {
    private String baseUri = null;

    public CheckComponentsTask(final String baseUri) {
      this.baseUri = baseUri;
    }

    @Override
    public void run() {
      for (String key : componentStateList.keySet()) {
        try {
          Response res = request(key, Method.GET, this.baseUri, null);
          if (!res.statusCode.equals(Response.OK)) {
            componentStateList.put(key, ObjectProperty.State.ERROR);
          }
        } catch (Exception e) {
          log.error("Recieved Message Exception.", e);
        }
      }
    }
  }

  /**
   * Method for deleting all componentManager at once.
   */
  private void deleteAllComponentManagers() {
    for (String compMgrId : new HashSet<String>(componentMgrsSet)) {
      deleteComponentManager(compMgrId);
    }
  }

  @Override
  protected Response onRequest(final Request req) {
    log.debug("onRequest: " + req.method + ", " + req.path);

    RequestParser<IActionCallback>.ParsedRequest parsed = parser.parse(req);
    if (parsed == null) {
      // Transfer Other Component
      Pattern pattern = Pattern.compile("^components/.*/.*");
      if (pattern.matcher(req.path).matches()) {
        String compId = getDestinationCompId(req.path);
        String command = getDestinationPath(req.path);
        return transferComponent(compId, command,
            req.method, req.getBodyValue());
      }

      return new Response(Response.BAD_REQUEST, null);
    }

    Response response = null;
    try {
      IActionCallback callback = parsed.getResult();
      if (callback == null) {
        return new Response(Response.BAD_REQUEST, null);
      }
      response = callback.process(parsed);
    } catch (Exception e) {
      log.error("Exception Request: " + req.method + ", " + req.path, e);
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
        addRule(Request.Method.PUT,
            "component_managers/<compmgr_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return putComponentManagers(
                    parsed.getParam("compmgr_id"),
                    parsed.getRequest().getBody(
                        ObjectProperty.class));
              }
            });
        addRule(Request.Method.GET,
            "component_managers",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentManagers();
              }
            });
        addRule(Request.Method.GET,
            "component_managers/<compmgr_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentManager(
                parsed.getParam("compmgr_id"));
              }
            });
        addRule(Request.Method.GET,
            "component_managers/<compmgr_id>/component_types",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentTypes(parsed.getParam("compmgr_id"));
              }
            });
        addRule(Request.Method.DELETE,
            "component_managers/<compmgr_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteComponentManager(parsed
                    .getParam("compmgr_id"));
              }
            });
        addRule(Request.Method.GET,
            "event_manager",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getEventManager();
              }
            });
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
            "component_types/<type>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getComponentManagersByType(parsed
                    .getParam("type"));
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
                return getComponent(parsed.getParam("comp_id"));
              }
            });
        addRule(Request.Method.POST,
            "components",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return postComponent(
                parsed.getRequest().getBody(ObjectProperty.class));
              }
            });
        addRule(Request.Method.PUT,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return putComponent(
                    parsed.getParam("comp_id"),
                    parsed.getRequest().getBody(ObjectProperty.class));
              }
            });
        addRule(Request.Method.DELETE,
            "components/<comp_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteComponent(parsed
                    .getParam("comp_id"));
              }
            });
        addRule(Request.Method.POST,
            "connections",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return postConnections(parsed.getRequest()
                    .getBody(
                        ComponentConnection.class));
              }
            });
        addRule(Request.Method.GET,
            "connections",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return getConnections();
              }
            });
        addRule(Request.Method.GET,
            "connections/<conn_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return getConnection(parsed.getParam("conn_id"));
              }
            });
        addRule(Request.Method.PUT,
            "connections/<conn_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return putConnections(
                    parsed.getParam("conn_id"),
                    parsed.getRequest().getBody(
                        ComponentConnection.class));
              }
            });
        addRule(Request.Method.DELETE,
            "connections/<conn_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return deleteConnections(parsed
                    .getParam("conn_id"));
              }
            });
        addRule(Request.Method.GET,
            "objects/<object_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return getObjectById(parsed
                    .getParam("object_id"));
              }
            });
        addRule(Request.Method.GET,
            "base_uri/<object_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                String objId = parsed.getParam("object_id");
                String baseUri = baseUriMap.get(objId);
                if (baseUri != null) {
                  return new Response(Response.OK, baseUri);
                } else {
                  return new Response(Response.NOT_FOUND,
                      null);
                }
              }
            });
      }
    };
  }

  /**
   * Method for getting the ObjectProperty.
   *
   * @param id
   *            Object ID to be acquired.
   * @return res ResponseObject. Body should be ObjectProperty.
   * @throws Exception if an error occurs.
   */
  private Response getObjectById(
      final String id) throws Exception {
    if (componentStateList.containsKey(id)
        || componentMgrsSet.contains(id)) {
      ObjectProperty op = getPropertyFromOtherComponent(id);
      if (op != null) {
        return new Response(Response.OK, op);
      } else {
        return new Response(Response.NOT_FOUND, op);
      }
    } else if (eventManagerProperty.getObjectId().equals(id)) {
      return new Response(Response.OK, eventManagerProperty);
    } else if (getObjectId().equals(id)) {
      return new Response(Response.OK, objectProperty);
    }
    return new Response(Response.NOT_FOUND, null);
  }

  /**
   * Method for registering component manager.
   *
   * @param compMgrId
   *            ComponentManager ID.
   * @param body
   *            RequestObject. Body should be ComponentManager's
   *            objectProperty.
   * @return res ResponseObject. Body should be register objectProperty.
   */
  private Response putComponentManagers(
      final String compMngId, final ObjectProperty body) {
    // Register the componentManager ID into the HashSet.
    if (!componentMgrsSet.add(compMngId)) {
      // Registered already
      log.warn("ComponentManager is already registerd, ID:{}", compMngId);
      return new Response(Response.CONFLICT,
          "ComponentManager is already registerd");
    }
    body.setProperty(
        ObjectProperty.PropertyNames.OBJECT_ID, compMngId);

    this.eventSubscription.addFilter(compMngId, ObjectPropertyChanged.TYPE);
    try {
      this.applyEventSubscription();
    } catch (Exception e) {
      log.error("Recieved Message Exception.", e);
    }

    String componentTypes = body
        .getProperty(ObjectProperty.PropertyNames.COMPONENT_TYPES);
    if (componentTypes != null) {
      List<String> typeList = Arrays.asList(componentTypes.split(","));
      log.debug("use component_types {} about {} in request body.",
          typeList, body.getObjectId());

      componentManagerChanged(ComponentManagerChanged.Action.add.name(),
          null,
          body);

      updateCreatableComponentType(body.getObjectId(), typeList);
      componentMgrsSet.add(body.getObjectId());
      compMgrsObjProps.put(body.getObjectId(), body);
    } else {
      log.warn("ComponentTypes is not set request body, Comopnent ID:{}",
          body.getObjectId());
      return new Response(Response.BAD_REQUEST, null);
    }

    log.info("Registerd ComponentManager Object ID:{}", body.getObjectId());
    return new Response(Response.OK, body);
  }

  private void updateComponentManager(final String id, final ObjectProperty body) {
    compMgrsObjProps.put(id, body);
    if (componentMgrsSet.add(id)) {
      log.warn("ComponentManager is not registerd, ID:{}", id);
      return;
    }
    String componentTypes = body.getProperty(ComponentManager2.ATTR_COMPTYPE);
    if (componentTypes == null) {
      log.warn("ComponentTypes is not set request body, Comopnent ID:{}", id);
      return;
    }
    List<String> typeList = Arrays.asList(componentTypes.split(","));
    log.debug("use component_types {} about {} in request body.", typeList, id);
    updateCreatableComponentType(id, typeList);
  }

  /**
   * Method for updating creatable component type data.
   *
   * @param compMgrId
   *            ComponentManager ID.
   * @param typeList
   *            ComponentTypes list of ComponentManager.
   */
  private void updateCreatableComponentType(final String compMgrId, final List<String> typeList) {
    for (String type : typeList) {
      if (!allComponentTypes.containsKey(type)) {
        // New type
        HashSet<String> compMgrSet = new HashSet<String>();
        compMgrSet.add(compMgrId);
        allComponentTypes.put(type, compMgrSet);
      } else {
        // Existed type
        allComponentTypes.get(type).add(compMgrId);
      }
    }
  }

  /**
   * Method for getting the component manager list.
   *
   * @return res ResponseObject. Body should be
   *         ArrayList[ComponentManager.property].
   */
  private Response getComponentManagers() {
    ObjectPropertyList al = new ObjectPropertyList();
    for (String compMgrId : componentMgrsSet) {
      ObjectProperty op = getPropertyFromOtherComponent(compMgrId);
      if (op != null) {
        al.add(op);
      }
    }
    return new Response(Response.OK, al);
  }

  /**
   * Method for getting the specific component manager.
   *
   * @param compMgrId
   *            ComponentManagerID to be acquired.
   * @return res ResponseObject. Body should be ComponentManager.property.
   */
  private Response getComponentManager(final String compMgrId) {
    if (componentMgrsSet.contains(compMgrId)) {
      ObjectProperty op = getPropertyFromOtherComponent(compMgrId);
      if (op != null) {
        return new Response(Response.OK, op);
      }
    }
    return new Response(Response.NOT_FOUND, null);
  }

  /**
   * Method for deleting the specific component manager.
   *
   * @param compMgrId
   *            RequestObject. ComponentManagerID to be deleted.
   * @return res ResponseObject. Body should be null.
   */
  private Response deleteComponentManager(
      final String compMgrId) {
    if (componentMgrsSet.contains(compMgrId)) {
      // delete Component
      for (Entry<String, String> e : new HashSet<Entry<String, String>>(
          mapCompAndCompMgr.entrySet())) {
        if (e.getValue().equals(compMgrId)) {
          deleteComponentFromComponentManager(compMgrId, e.getKey());
          componentStateList.remove(e.getKey());
          mapCompAndCompMgr.remove(e.getKey());
        }
      }

      ObjectProperty prev = getPropertyFromOtherComponent(compMgrId);
      if (prev != null) {
        componentManagerChanged(
            ComponentManagerChanged.Action.delete.name(),
            prev,
            null);
      }

      deleteFromCreatableComponentType(compMgrId);
      componentMgrsSet.remove(compMgrId);
      compMgrsObjProps.remove(compMgrId);

      log.info("Deleted ComponentManager Object ID:{}", compMgrId);
      return new Response(Response.OK, null);
    }
    return new Response(Response.OK, null);
  }

  /**
   * Method for deleting creatable component type data.
   *
   * @param compMgrId
   *            ComponentManager ID to be deleted.
   */
  private void deleteFromCreatableComponentType(final String compMgrId) {
    for (String type : new HashSet<String>(allComponentTypes.keySet())) {
      if (allComponentTypes.get(type).remove(compMgrId)) {
        if (allComponentTypes.get(type).isEmpty()) {
          allComponentTypes.remove(type);
        }
      }
    }
  }

  /**
   * Method for getting the event manager.
   *
   * @return res ResponseObject. Body should be EnvetManagerPropertyObject.
   */
  private Response getEventManager() {
    return new Response(Response.OK, eventManagerProperty);
  }

  /**
   * Method for getting the CompoentManager's componentTypes.
   *
   * @return res ResponseObject.
   *         {@literal Body should be HashMap<String, ComponentType>}.
   */
  private Response getComponentTypes(String compmgrId) {
    try {
      Response resp = request(compmgrId, Method.GET, "component_types", null);
      if (resp.isError("GET")) {
        log.warn("invalid GET:" + resp.statusCode);
        return resp;
      }
      ComponentTypesHash types = resp.getBody(ComponentTypesHash.class);
      return new Response(Response.OK, types);
    } catch (Exception ex) {
      log.error("Recieved Message Exception.", ex);
      return new Response(Response.INTERNAL_SERVER_ERROR, "Failed GET component_types.");
    }
  }

  /**
   * Method for getting the list of componentType.
   *
   * @return res ResponseObject.
   *         {@literal Body should be HashMap<ComponentType, list[ComponentManager.property]>}.
   */
  private Response getComponentTypes() {
    ComponentTypesHash componentTypesHash = new ComponentTypesHash();

    for (String compMgrId : componentMgrsSet) {
      Response resp = getComponentTypes(compMgrId);
      if (resp.isError("GET")) {
        log.warn("invalid GET:" + resp.statusCode);
        return resp;
      }
      try {
        ComponentTypesHash types = resp.getBody(ComponentTypesHash.class);
        for (String type : types.keySet()) {
          if (componentTypesHash.containsKey(type)) {
            ComponentType compType = componentTypesHash.get(type);
            compType.addCmId(compMgrId);
          } else {
            ComponentType compType = types.get(type).clone();
            compType.addCmId(compMgrId);
            componentTypesHash.put(type, compType);
          }
        }
      } catch (ParseBodyException e) {
        return new Response(Response.INTERNAL_SERVER_ERROR, "Failed GET component_types.");
      }
    }
    return new Response(Response.OK, componentTypesHash);
  }

  /**
   * Method for getting the componentManager list which can create a specified
   * componentType.
   *
   * @param type
   *            ComponentType to be acquired.
   * @return res ResponseObject. Body should be
   *         ArrayList[ComponentManager.property].
   */
  private Response getComponentManagersByType(final String type) {
    ObjectPropertyList compMgrs = new ObjectPropertyList();
    HashSet<String> compMgrIdSet = allComponentTypes.get(type);

    for (String compMgrId : compMgrIdSet) {
      ObjectProperty prop = getPropertyFromOtherComponent(compMgrId);
      if (prop != null) {
        compMgrs.add(prop);
      }
    }

    if (compMgrs.isEmpty()) {
      return new Response(Response.NOT_FOUND, null);
    }
    return new Response(Response.OK, compMgrs);
  }

  /**
   * Method for getting the component list.
   *
   * @return res ResponseObject. {@literal Body should be dict<Component.id, Component.property>}.
   */
  private Response getComponents() {
    return new Response(Response.OK, this.componetsObjectProperties);
  }

  /**
   * Method for getting a component.
   *
   * @param compId
   *            ComponentID to be acquired.
   * @return res ResponseObject. Body should be component's objectProperty.
   */
  private Response getComponent(final String compId) {
    if (componentStateList.containsKey(compId)) {
      ObjectProperty prop = getPropertyFromOtherComponent(compId);
      if (prop != null) {
        return new Response(Response.OK, prop);
      }
    }
    return new Response(Response.NOT_FOUND, null);
  }

  /**
   * Method for creating a component.
   *
   * @param body
   *            RequestObject. Body should be component's objectProperty.
   * @return res ResponseObject. Body should be component's objectProperty.
   */
  private Response postComponent(final ObjectProperty body) {
    String createdType = body.getObjectType();
    String compId = body.getObjectId();

    // forced to auto-number
    compId = getUniqueID();
    body.setProperty(
        ObjectProperty.PropertyNames.OBJECT_ID, compId);

    if (!allComponentTypes.containsKey(createdType)) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST,
          "Not Creatable Component Type");
    }
    if (allComponentTypes.get(createdType).isEmpty()) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST,
          "Not Creatable Component Type");
    }

    String compMgrId = null;
    if (body.getProperty(ObjectProperty.PropertyNames.CM_ID) != null) {
      String reqCmId = body
          .getProperty(ObjectProperty.PropertyNames.CM_ID);
      for (String cmId : allComponentTypes.get(createdType)) {
        if (reqCmId.equals(cmId)) {
          compMgrId = reqCmId;
          break;
        }
      }
    } else {
      ArrayList<String> compMgrIds =
          new ArrayList<String>(allComponentTypes.get(createdType));
      compMgrId = compMgrIds.get(0);
    }

    if (compMgrId == null) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST, null);
    }

    ObjectProperty createdObjProp = null;

    try {
      this.eventSubscription.addFilter(compId, ObjectPropertyChanged.TYPE);
      try {
        this.applyEventSubscription();
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }

      String path = String.format("components/%s", body.getObjectId());
      Response resp =
          request(compMgrId, Method.PUT, path, body);
      if (!resp.statusCode.equals(Response.CREATED)) {
        log.warn("Failed to create Component Type:{} StatusCode:{}",
            createdType, resp.statusCode);
        return resp;
      }
      createdObjProp = resp.getBody(ObjectProperty.class);
      if (createdObjProp != null) {
        componentStateList.put(compId, ObjectProperty.State.RUNNING);
        mapCompAndCompMgr.put(compId, compMgrId);
        this.componetsObjectProperties.put(compId, createdObjProp);

        log.info("Created Component Type:{} ID:{}", createdType, compId);
        // wait components's subscription
        Thread.sleep(WAIT_SUBSCRIPTION_TIME);
        return new Response(Response.OK, createdObjProp);
      }
    } catch (Exception e) {
      log.error("Exception to create Component Type:{} ID:{}",
          createdType, compId, e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }

    log.error("Unknwon Failed to create Component Type:{} ID:{}",
        createdType, compId);
    return new Response(Response.INTERNAL_SERVER_ERROR, null);
  }

  /**
   * Method for creating a component.
   *
   * @param compId
   *            ComponentID to be deleted.
   * @param body
   *            RequestObject. Body should be component's objectProperty.
   * @return res ResponseObject. Body should be component's objectProperty.
   */
  private Response putComponent(
      final String compId,
      final ObjectProperty body) {
    String createdType = body.getObjectType();

    if (!allComponentTypes.containsKey(createdType)) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST,
          "Not Creatable Component Type");
    }
    if (allComponentTypes.get(createdType).isEmpty()) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST,
          "Not Creatable Component Type");
    }

    String compMgrId = null;
    if (body.getProperty(ObjectProperty.PropertyNames.CM_ID) != null) {
      String reqCmId = body
          .getProperty(ObjectProperty.PropertyNames.CM_ID);
      for (String cmId : allComponentTypes.get(createdType)) {
        if (reqCmId.equals(cmId)) {
          compMgrId = reqCmId;
          break;
        }
      }
    } else {
      ArrayList<String> compMgrIds =
          new ArrayList<String>(allComponentTypes.get(createdType));
      compMgrId = compMgrIds.get(0);
    }

    if (compMgrId == null) {
      log.warn("Not Creatable Component Type:{}", createdType);
      return new Response(Response.BAD_REQUEST, null);
    }

    ObjectProperty createdObjProp = null;

    if (compId == null) {
      return new Response(Response.INTERNAL_SERVER_ERROR,
          "Component Id is null");
    } else if (componentStateList.containsKey(compId)) {
      return new Response(Response.CONFLICT,
          "ComponentId is already registerd");
    }
    body.setProperty(
        ObjectProperty.PropertyNames.OBJECT_ID, compId);

    try {
      this.eventSubscription.addFilter(compId, ObjectPropertyChanged.TYPE);
      try {
        this.applyEventSubscription();
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }

      String path = String.format("components/%s", body.getObjectId());
      Response resp =
          request(compMgrId, Method.PUT, path, body);
      if (!resp.statusCode.equals(Response.CREATED)) {
        log.warn("Failed to create Component Type:{} StatusCode:{}",
            createdType, resp.statusCode);
        return resp;
      }
      createdObjProp = resp.getBody(ObjectProperty.class);
      if (createdObjProp != null) {
        componentStateList.put(compId, ObjectProperty.State.RUNNING);
        mapCompAndCompMgr.put(compId, compMgrId);
        this.componetsObjectProperties.put(compId, createdObjProp);

        log.info("Created Component Type:{} ID:{}", createdType, compId);
        // wait components's subscription
        Thread.sleep(WAIT_SUBSCRIPTION_TIME);
        return resp;
      }
    } catch (Exception e) {
      log.error("Exception to create Component Type:{} ID:{}",
          createdType, compId, e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }

    log.error("Unknwon Failed to create Component Type:{} ID:{}",
        createdType, compId);
    return new Response(Response.INTERNAL_SERVER_ERROR, null);
  }

  /**
   * Method for deleting a component.
   *
   * @param compId
   *            ComponentID to be deleted.
   * @return res ResponseObject. Body should be null.
   */
  private Response deleteComponent(final String compId) {
    if (hasConnection(compId)) {
      log.warn(
          "Failed to Delete Component ID:{} Cause:Exsist Connection",
          compId);
      return new Response(Response.FORBIDDEN, null);
    }

    if (componentStateList.containsKey(compId)) {
      Response rsp = deleteComponentFromComponentManager(
          mapCompAndCompMgr.get(compId),
          compId);
      if (!rsp.statusCode.equals(Response.OK)) {
        log.warn("Failed to delete Component ID:{} StatusCode:{}",
            compId, rsp.statusCode);
        return rsp;
      }
      componentStateList.remove(compId);
      mapCompAndCompMgr.remove(compId);
      this.componetsObjectProperties.remove(compId);

      this.eventSubscription.removeFilter(compId, ObjectPropertyChanged.TYPE);
      try {
        this.applyEventSubscription();
      } catch (Exception e) {
        log.error("Recieved Message Exception.", e);
      }

      log.info("Deleted Component ID:{}", compId);
      return new Response(Response.OK, null);
    }
    return new Response(Response.NOT_FOUND, null);
  }

  /**
   * Method for transfer request message to component.
   *
   * @param id
   *            Destination ComponentID.
   * @param path
   *            Destination path.
   * @param method
   *            Request method.
   * @param bodyValue
   *            Request body.
   * @return res ResponseObject.
   */
  private Response transferComponent(
      final String id,
      final String path,
      final Request.Method method,
      final Value bodyValue) {
    if (componentStateList.containsKey(id)) {
      try {
        return this.request(id, method, path, bodyValue);
      } catch (Exception e) {
        log.error(
            "Exception to message transfer Dest ID:{} Path:{} Method:{}",
            id, path, method, e);
        return new Response(Response.INTERNAL_SERVER_ERROR, null);
      }
    }

    log.warn("Not registered Destination Component ID:{}", id);
    return new Response(Response.BAD_REQUEST, null);
  }

  /**
   * Method for getting a ObjectProperty from other component.
   *
   * @param compId
   *            ComponentID to be acquired.
   * @return res ResponseObject. Body should be component's objectProperty.
   */
  private ObjectProperty getPropertyFromOtherComponent(final String compId) {
    // GET, compId/property
    ObjectProperty respObjProp = this.componetsObjectProperties.get(compId);
    if (respObjProp == null) {
      respObjProp = this.compMgrsObjProps.get(compId);
    }
    return respObjProp;
  }

  /**
   * Method for deleting a Component from ComponentManager.
   *
   * @param compMgrId
   *            Destination ComponentManager ID.
   * @param compId
   *            ComponentID to be deleted.
   * @return res ResponseObject.
   */
  private Response deleteComponentFromComponentManager(
      final String compMgrId,
      final String compId) {
    Response resp = null;
    try {
      resp = request(compMgrId, Method.DELETE, "components/" + compId,
          null);
      if (!resp.statusCode.equals(Response.OK)) {
        log.warn("Failed to delete component from"
            + " ComponentManager:{} ComponentID:{} StatusCode:{}",
            compMgrId, compId, resp.statusCode);
        return resp;
      }
    } catch (Exception e) {
      log.error("Exception to delete component from"
          + " ComponentManager:{} ComponentID:{}",
          compMgrId, compId, e);
      return new Response(Response.INTERNAL_SERVER_ERROR, null);
    }
    return resp;
  }

  /**
   * Method for sending ComponentManagerChanged Event.
   *
   * @param action
   *            Event action
   * @param prev
   *            Message element old ObjectProperty.
   * @param curr
   *            Message element new ObjectProperty.
   */
  private void componentManagerChanged(final String action,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    ComponentManagerChanged msg = new ComponentManagerChanged(action, prev,
        curr);
    try {
      publishEvent(ComponentManagerChanged.TYPE, msg);
    } catch (Exception e) {
      log.error("Failed to send ComponentManagerChanged Action:{}",
          action, e);
    }
  }

  // ////////////////////////////////////////////////////////
  // connection
  // ////////////////////////////////////////////////////////

  // Data for active connections.
  // (Key: connectionId, Value:ComponentConnection)
  private HashMap<String, ComponentConnection> connectionTable =
      new HashMap<String, ComponentConnection>();

  /**
   * Method for creating a component connection.
   *
   * @param body
   *            RequestObject. Body should be ComponentConnection.
   * @return res ResponseObject. Body should be ComponentConnection.
   */
  private Response postConnections(
      final ComponentConnection body) {

    // forced to auto-number
    String connId = getUniqueID();
    body.setProperty(
        ObjectProperty.PropertyNames.OBJECT_ID, connId);

    ComponentConnection curr = null;
    String logicId = body.getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    String networkId = body.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);

    if (!mapCompAndCompMgr.containsKey(logicId)
        || !mapCompAndCompMgr.containsKey(networkId)) {
      log.warn("Failed to create Connection "
          + "Logic:{} Network:{} Cause:Not Exsists Component",
          logicId, networkId);
      return new Response(
          Response.BAD_REQUEST, "Not Exsists Component");
    }

    if (body.getObjectType().equals(
        ComponentConnectionLogicAndNetwork.TYPE)) {
      curr = new ComponentConnectionLogicAndNetwork(
          connId, body.getConnectionType(),
          body.getObjectState(), logicId, networkId);
    } else {
      log.warn("Failed to create Connection "
          + "Logic:{} Network:{} Cause:Unexpected ConnectionType:{}",
          body.getObjectType());
      return new Response(
          Response.BAD_REQUEST, "Unexpected ConnectionType");
    }

    curr.setConnectionState(ComponentConnection.State.INITIALIZING);
    connectionTable.put(connId, curr);

    componentConnectionChanged(
        ComponentConnectionChanged.Action.add.name(), null, curr);

    log.info("Created Component Connection ID:{} Logic:{} and Network:{}",
        connId, logicId, networkId);
    return new Response(Response.OK, curr);
  }

  /**
   * Method for update a component connection.
   *
   * @param connId
   *            ComponentID to be updated.
   * @param body
   *            RequestObject. Body should be ComponentConnection.
   * @return res ResponseObject. Body should be ComponentConnection.
   */
  private Response putConnections(
      final String connId, final ComponentConnection body) {

    ComponentConnection curr = null;
    String logicId = body.getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
    String networkId = body.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    if (body.getObjectType()
        .equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      curr = new ComponentConnectionLogicAndNetwork(
          connId, body.getConnectionType(), body.getObjectState(),
          logicId, networkId);
    } else {
      log.warn("Failed to update Connection "
          + "Logic:{} Network:{} : Unexpected ConnectionType:{}",
          body.getObjectType());
      return new Response(
          Response.BAD_REQUEST, "Unexpected ConnectionType.");
    }

    ComponentConnection prev = connectionTable.get(connId);
    String prevConnState = null;
    if (prev != null) {
      prevConnState = prev
          .getProperty(ComponentConnection.CONNECTION_STATE);
    }
    String connState = curr
        .getProperty(ComponentConnection.CONNECTION_STATE);

    // create new connection
    if (!connectionTable.containsKey(connId)) {
      if (curr == null
          || connState.equals(ComponentConnection.State.INITIALIZING)) {
        curr.setConnectionState(ComponentConnection.State.INITIALIZING);
        connectionTable.put(connId, curr);

        componentConnectionChanged(
            ComponentConnectionChanged.Action.add.name(), null, curr);
        log.info("Created Component Connection ID:{} Logic:{} and Network:{}",
            connId, logicId, networkId);
        return new Response(Response.CREATED, curr);
      } else {
        return new Response(
            Response.BAD_REQUEST, "Unexpected ComponentConnection.State.");
      }
    }

    // check ConponentConnection.
    if (prev != null && prev.equals(curr)
        && prevConnState != null && prevConnState.equals(connState)) {
      log.info(
          "No need Update Component Connection ID:{} Logic:{} and Network:{}",
          connId, logicId, networkId);
      return new Response(Response.OK, curr);
    }

    // update connectionTable
    connectionTable.put(connId, curr);
    log.info("Update Component Connection ID:{} Logic:{} and Network:{} state:{}",
        connId, logicId, networkId, connState);

    // new connection
    if (connState == null) {
      connState = ComponentConnection.State.INITIALIZING;
      curr.setConnectionState(connState);
    }
    // status:"finalizing"
    if (connState.equals(ComponentConnection.State.FINALIZING)) {
      return new Response(Response.OK, curr);
    }
    // status:"finalizing" --> status:"noe"
    if (prevConnState != null
        && prevConnState.equals(ComponentConnection.State.FINALIZING)
        && connState.equals(ComponentConnection.State.NONE)) {
      connectionTable.remove(connId);
      return new Response(Response.OK, curr);
    }

    componentConnectionChanged(
        ComponentConnectionChanged.Action.update.name(), prev, curr);

    log.info("Updated Component Connection ID:{} Logic:{} and Network:{}",
        connId, logicId, networkId);
    return new Response(Response.OK, curr);
  }

  /**
   * Method for getting the connection list.
   *
   * @return res ResponseObject.
   *         {@literal Body should be dict<ComponentConnection.id, ComponentConnection>)}.
   *
   */
  private Response getConnections() {
    TreeMap<String, ComponentConnection> sorted = new TreeMap<String, ComponentConnection>();
    sorted.putAll(connectionTable);
    return new Response(Response.OK, sorted);
  }

  /**
   * Method for getting the connection property by Id.
   *
   * @param connId
   *            ConnectionID to be acquired.
   * @return res ResponseObject. Body should be ComponentConnection.
   */
  private Response getConnection(
      final String connId) {
    if (connectionTable.containsKey(connId)) {
      return new Response(Response.OK, connectionTable.get(connId));
    }
    return new Response(Response.NOT_FOUND, null);
  }

  /**
   * Method for deleting the connection property by Id.
   *
   * @param connId
   *            ConnectionID to be deleted.
   * @return res ResponseObject. Body should be null.
   */
  private Response deleteConnections(
      final String connId) {

    ComponentConnection prev = connectionTable.get(connId);
    if (prev == null) {
      log.warn("Not Exsists Connection ID:{}", connId);
      return new Response(Response.NOT_FOUND, "Not Exsists Connection ID");
    }

    ComponentConnection curr = (ComponentConnection) prev.clone();
    componentConnectionChanged(
        ComponentConnectionChanged.Action.delete.name(), prev, curr);

    log.info("Deleted Component Connection ID:{}", connId);
    return new Response(Response.OK, null);
  }

  /**
   * Method for checking if the object has connections.
   *
   * @param id
   *            ComponentID to be checked.
   * @return true if connection is existing.
   */
  private boolean hasConnection(final String id) {
    for (ComponentConnection objProp : connectionTable.values()) {
      ComponentConnectionLogicAndNetwork val = (ComponentConnectionLogicAndNetwork) objProp;
      if (id.equals(val.getLogicId()) || id.equals(val.getNetworkId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Method for sending ComponentConnectionChanged Event.
   *
   * @param action
   *            Event action.
   * @param prev
   *            Message element old ComponentConnection.
   * @param curr
   *            Message element new ComponentConnection.
   */
  private void componentConnectionChanged(final String action,
      final ComponentConnection prev,
      final ComponentConnection curr) {
    ComponentConnectionChanged msg = new ComponentConnectionChanged(action,
        prev, curr);
    try {
      publishEvent(ComponentConnectionChanged.TYPE, msg);
      if (action.equals(ComponentConnectionChanged.Action.add.name())) {
        // wait components's subscription or unsubscription
        Thread.sleep(WAIT_SUBSCRIPTION_TIME);
      }
      if (action.equals(ComponentConnectionChanged.Action.delete.name())) {
        // wait components's subscription or unsubscription
        Thread.sleep(WAIT_SUBSCRIPTION_TIME * 2);
      }
    } catch (Exception e) {
      log.error("Failed to send ComponentConnectionChanged Action:{}",
          action, e);
    }
  }

  /**
   * Method for deleting all component connection.
   */
  private void deleteAllComponentConnection() {
    for (String compId : mapCompAndCompMgr.keySet()) {
      deleteConnectionWithCompId(compId);
    }
  }

  /**
   * Method for deleting the component connection by compId.
   *
   * @param compId
   *            ComponentID to be deleted.
   */
  private void deleteConnectionWithCompId(final String compId) {
    for (Entry<String, ComponentConnection> e : new HashSet<Entry<String, ComponentConnection>>(
        connectionTable.entrySet())) {
      ComponentConnectionLogicAndNetwork val =
          (ComponentConnectionLogicAndNetwork) e.getValue();
      if (compId.equals(val.getLogicId())
          || compId.equals(val.getNetworkId())) {
        deleteConnections(val.getObjectId());
      }
    }
  }

  /**
   * Method for assigned generating UUID to ComponentConnectionID.
   *
   * @return id ComponentConnectionID.
   */
  private String getUniqueID() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (connectionTable.containsKey(id)
        || componentStateList.containsKey(id));
    return id;
  }
}
