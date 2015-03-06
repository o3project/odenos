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
import org.o3project.odenos.remoteobject.actor.Mail;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.event.ObjectPropertyChanged;
import org.o3project.odenos.remoteobject.event.ObjectSettingsChanged;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The base class by which a remote calling is possible.
 *
 */
public class RemoteObject {
  private static final Logger log = LoggerFactory.getLogger(RemoteObject.class);

  protected MessageDispatcher messageDispatcher = null;
  protected ObjectProperty objectProperty = null;
  protected ObjectSettings objectSettings = new ObjectSettings();
  protected EventSubscription eventSubscription = null;

  protected final RequestParser<IActionCallback> parser;

  // mailbox to receive messages from MessageDispatcher.
  protected Queue<Mail> mailbox;
  // true if Actor is running on this object right now.
  protected boolean running = false;

  /**
   * Constructor.
   * @param objectId object ID.
   * @param dispatcher MessageDispatcher object.
   */
  public RemoteObject(String objectId,
      MessageDispatcher dispatcher) {
    this.objectProperty = new ObjectProperty(this.getClass()
        .getSimpleName(), objectId);
    this.objectProperty.setObjectState(ObjectProperty.State.INITIALIZING);
    setMessageDispatcher(dispatcher);
    this.eventSubscription = new EventSubscription(objectId);

    this.parser = this.createParser();

    this.mailbox = new ConcurrentLinkedQueue<Mail>();
  }

  public boolean onInitialize(ObjectProperty prop) {
    return true;
  }

  /**
   * Runs finalization of the objects.
   */
  public void onFinalize() {
    ObjectProperty prev = (ObjectProperty) objectProperty.clone();
    if (objectProperty.setObjectState(ObjectProperty.State.FINALIZING)
        != ObjectProperty.State.FINALIZING) {
      onPropertyChanged(ObjectPropertyChanged.Action.delete.name(),
          prev,
          null);
    }
    messageDispatcher.removeLocalObject(this);
  }

  public String getObjectId() {
    return objectProperty.getObjectId();
  }

  public ObjectProperty getProperty() {
    return objectProperty;
  }

  public ObjectSettings getSettings() {
    return objectSettings;
  }

  public MessageDispatcher getMessageDispatcher() {
    return messageDispatcher;
  }

  /**
   * Set a state.
   * @param next string of state.
   */
  public void setState(String next) {
    if (this.objectProperty.getObjectState().equals(next)) {
      return;
    }
    ObjectProperty prev = (ObjectProperty) objectProperty.clone();
    this.objectProperty.setObjectState(next);
    ObjectProperty curr = (ObjectProperty) objectProperty.clone();

    onPropertyChanged(ObjectPropertyChanged.Action.update.name(),
        prev,
        curr);
  }

  /**
   * Sets a Message Dispatcher.
   * @param messageDispatcher Message DIspatcher object.
   */
  public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
    this.messageDispatcher = messageDispatcher;
    if (messageDispatcher != null) {
      this.messageDispatcher.addLocalObject(this);
    }
  }

  /**
   * Get ObjectId of SystemManager from MessageDispatcher.
   *
   * @return ObjectId of SystemManager
   */
  protected final String getSystemManagerId() {
    return this.messageDispatcher.getSystemManagerId();
  }

  /**
   * Get ObjectId of EventManager from MessageDispatcher.
   *
   * @return ObjectId of EventManager
   */
  protected final String getEventManagerId() {
    return this.messageDispatcher.getEventManagerId();
  }

  /**
   * Register information in order to message transport with RemoteObject to
   * MessageDispatcher.
   *
   * @param objectId ID of the registering RemoteObject
   * @throws IOException if an I/O error occurs.
   */
  protected final void addRemoteObject(final String objectId)
      throws IOException {
    this.messageDispatcher.addRemoteObject(objectId);
  }

  /**
   * Unregister RemoteObject from MessageDispatcher.
   *
   * @param objectId
   *            ID of the unregistering RemoteObject
   */
  protected final void removeRemoteObject(final String objectId)
      throws IOException {
    this.messageDispatcher.removeRemoteObject(objectId);
  }

  /**
   * Send a request to the specified RemoteObject asynchronously.
   *
   * @param objectId
   *            ID of the targeted RemoteObject
   * @param method
   *            method for the path
   * @param path
   *            path of the RemoteObject
   * @param body
   *            requested data
   * @return Future object to obtain the response of the request
   * @throws Exception if an error occurs.
   */
  protected Response requestSync(String objectId, Request.Method method,
      String path, Object body) throws Exception {
      return messageDispatcher.requestSync(new Request(objectId, method,
      path, body), this.getObjectId());
      }

  /**
   * Send a request to the specified RemoteObject and get the response of it.
   *
   * @param objectId
   *            ID of the targeted RemoteObject
   * @param method
   *            method for the path
   * @param path
   *            path of the RemoteObject
   * @param body
   *            requested data
   * @return a response of the request
   * @throws Exception if an error occurs.
   */
  public Response request(String objectId, Request.Method method,
      String path, Object body) throws Exception {
    return requestSync(objectId, method, path, body);
  }

  /**
   * Notify an event of this RemoteObject asynchronously.
   *
   * @param eventType
   *            type of Event to notify
   * @param body
   *            Event object
   * @throws Exception if an error occurs.
   */
  protected void publishEvent(String eventType, Object body)
      throws Exception {
    Event event = new Event(getObjectId(), eventType, body);
    messageDispatcher.publishEventAsync(event);
  }

  /**
   * Subscribe events of other RemoteObjects. Caller should configure
   * "eventSubscription" member to specify publisher IDs and events before
   * call this method.
   *
   * @return Future object
   * @throws Exception if an error occurs.
   */
  protected Response applyEventSubscription() throws Exception {
    return messageDispatcher.subscribeEvent(eventSubscription);
  }

  /**
   * Event handler to dispatch Request which is posted from another
   * RemoteObject.
   *
   * @param request
   *            the posted Request
   * @return response to the RemoteObject
   */
  public Response dispatchRequest(Request request) {
    log.debug("dispatchRequest: " + request.method + ", " + request.path);
    if (StringUtils.stripToNull(request.path) == null) {
      return new Response(Response.BAD_REQUEST, null);
    }

    RequestParser<IActionCallback>.ParsedRequest parsed = parser
        .parse(request);
    Response response = null;

    if (parsed == null) {
      response = onRequest(request);
    } else {
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
    }
    if (response == null) {
      response = new Response(Response.BAD_REQUEST, null);
    }
    return response;
  }

  /**
   * Post Event to Dispatcher.
   * @param event event.
   */
  public void dispatchEvent(Event event) {
    if (eventSubscription.contains(event.publisherId, event.eventType)) {
      doPostEvent(event);
    }
  }

  /**
   * @return true is Actor is running on this object.
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * @param running true if Actor is going to run on this object.
   */
  public void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * @return mailbox.
   */
  public Queue<Mail> getMailbox() {
    return mailbox;
  }

  protected interface IActionCallback {
    Response process(
        RequestParser<IActionCallback>.ParsedRequest parser)
        throws Exception;
  }

  private RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        addRule(Request.Method.GET,
            "property",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return doGetProperty();
              }
            });
        addRule(Request.Method.PUT,
            "property",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return doPutProperty(parsed.getRequest()
                    .getBody(
                        ObjectProperty.class));
              }
            });
        addRule(Request.Method.GET,
            "settings",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed) {
                return doGetSettings();
              }
            });
        addRule(Request.Method.PUT,
            "settings",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return doPutSettings(parsed.getRequest()
                    .getBody(
                        ObjectSettings.class));
              }
            });
        addRule(Request.Method.POST,
            "event",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws ParseBodyException {
                return doPostEvent(parsed.getRequest().getBody(
                    Event.class));
              }
            });
      }
    };
  }

  protected Response doGetProperty() {
    return new Response(Response.OK, objectProperty);
  }

  protected final Response doPutProperty(final ObjectProperty data) {
    if (isFinalize(data)) {
      onFinalize();
      return new Response(Response.OK, null);
    }

    ObjectProperty prev = (ObjectProperty) objectProperty.clone();
    if (prev.isModify(data)) {
      getProperty().putProperty(data);
      onPropertyChanged(ObjectPropertyChanged.Action.update.name(), prev,
          getProperty());
    }

    return doGetProperty();
  }

  protected final Response doGetSettings() {
    return new Response(Response.OK, getSettings());
  }

  protected final Response doPutSettings(final ObjectSettings data) {
    ObjectSettings prev = (ObjectSettings) getSettings().clone();
    if (prev.isModify(data)) {
      getSettings().putSettings(data);
      onSettingsChanged(ObjectSettingsChanged.Action.update.name(),
          prev, getSettings());
    }

    return doGetSettings();
  }

  protected Response doPostEvent(final Event event) {
    onEvent(event);
    return new Response(Response.ACCEPTED, null);
  }

  protected void onStateChanged(String oldState, String newState) {
    if (!ObjectProperty.State.FINALIZING.equals(oldState)
        && ObjectProperty.State.FINALIZING.equals(newState)) {
      onFinalize();
    }
  }

  protected final void onPropertyChanged(final String action,
      final ObjectProperty prev,
      final ObjectProperty curr) {
    ObjectPropertyChanged msg = new ObjectPropertyChanged(
        action, prev, curr);
    try {
      publishEvent(ObjectPropertyChanged.TYPE, msg);
    } catch (Exception e) {
      log.error("Failed to ObjectPropertyChanged.", e);
    }
  }

  protected void onSettingsChanged(final String action,
      final ObjectSettings prev,
      final ObjectSettings curr) {
    ObjectSettingsChanged msg =
        new ObjectSettingsChanged(action, prev, curr);
    try {
      publishEvent(ObjectSettingsChanged.TYPE, msg);
    } catch (Exception e) {
      log.error("Failed to ObjectSettingsChanged.", e);
    }
  }

  /**
   * Event handler to handle unknown Request which is posted from another
   * RemoteObject.
   *
   * @param request
   *            the posted Request
   * @return response to the RemoteObject
   */
  protected Response onRequest(Request request) {
    return new Response(Response.BAD_REQUEST, null);
  }

  /**
   * onEvent is handle event which is notified from other RemoteObject.
   *
   * @param event
   *            the notified Event
   */
  protected void onEvent(Event event) {
  }

  /**
   * ObjectProperty State Change check FINALIZING.
   *
   * @param prop
   *            new ObjectProperty
   * @return true if state is FINALIZING.
   *
   */
  private boolean isFinalize(final ObjectProperty prop) {
    if (prop.property.containsKey(
        ObjectProperty.PropertyNames.OBJECT_STATE)) {
      return prop.getObjectState().equals(
          ObjectProperty.State.FINALIZING);
    }
    return false;
  }

}
