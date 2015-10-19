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

package org.o3project.odenos.remoteobject.manager;

import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.event.EventSubscription;
import org.o3project.odenos.remoteobject.manager.EventSubscriptionObject.EventSubscriptionMap;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

/**
 * EventManagrer handles pub/sub request and manages streaming sessions set up
 * by client RemoteObjects.
 */
public class EventManager extends RemoteObject {
  private static final Logger log = LogManager.getLogger(EventManager.class);

  protected final RequestParser<IActionCallback> parser;

  public static final String SUBSCRIBER_ID = "subscriber_id";

  private EventSubscriptionMap subscriptionMap = new EventSubscriptionMap();

  @Deprecated
  public EventManager(String objectId, String baseUri,
      MessageDispatcher dispatcher) {
    this(objectId, dispatcher);
  }

  /**
   * Constructor.
   * @param objectId object ID.
   * @param dispatcher MessageDispatcher object.
   */
  public EventManager(final String objectId,
      final MessageDispatcher dispatcher) {
    super(objectId, dispatcher);
    parser = createParser();
    getProperty().setObjectState(ObjectProperty.State.RUNNING);
  }

  @Override
  public void onFinalize() {
    subscriptionMap.clear();
    super.onFinalize();
  }

  /**
   * Create RequestParser with rules for EventManager.
   *
   * @return RequestParser
   */
  private RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        addRule(Request.Method.PUT,
            "settings/event_subscriptions/<subscriber_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String subscriberId = parsed.getParam(SUBSCRIBER_ID);
                EventSubscription subscription = parsed
                    .getRequest().getBody(
                        EventSubscription.class);
                return putSubscription(subscriberId, subscription);
              }
            });
        addRule(Request.Method.GET,
            "settings/event_subscriptions",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                return getSubscriptions();
              }
            });
        addRule(Request.Method.GET,
            "settings/event_subscriptions/<subscriber_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>
                  .ParsedRequest parsed) throws Exception {
                String subscriberId = parsed.getParam("subscriber_id");
                return getSubscription(subscriberId);
              }
            });
      }
    };
  }

  @Override
  protected final Response onRequest(final Request request) {
    LogMessage.setSavedTxid(request.txid);
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "received {}", request.path));
    Response res;

    try {
      RequestParser<IActionCallback>.ParsedRequest parsed =
          parser.parse(request);
      if (parsed == null) {
        res = new Response(Response.BAD_REQUEST,
            "Error unknown request");
        LogMessage.delSavedTxid();
        return res;
      }

      IActionCallback callback = parsed.getResult();
      if (callback == null) {
        res = new Response(Response.BAD_REQUEST,
            "Error unknown request");
        LogMessage.delSavedTxid();
        return res;
      }

      res = callback.process(parsed);
      LogMessage.delSavedTxid();
      return res;
    } catch (Exception ex) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Error unknown request"), ex);
      res = new Response(Response.BAD_REQUEST, "Error unknown request");
      LogMessage.delSavedTxid();
      return res;
    }
  }

  /**
   * Process Request and create Response to update subscription.
   *
   * @param subscriberId subscriber ID.
   * @param subscription Request data which contains new subscription data.
   * @return Response to be sent.
   */
  private Response putSubscription(String subscriberId, EventSubscription subscription) {
    if (log.isDebugEnabled()) {
      log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "subscriberId {}", subscriberId));
    }

    if (StringUtils.isEmpty(subscriberId)) {
      return new Response(Response.BAD_REQUEST, "subscriber_id is empty.");
    }

    subscription.setSubscriberId(subscriberId);
    subscriptionMap.setSubscription(subscriberId, subscription);

    return new Response(Response.OK, subscription);
  }

  /**
   * Process Request and create Response to get all subscription data.
   *
   * @return Response to be sent.
   */
  private Response getSubscriptions() {

    log.debug("");

    return new Response(Response.OK, subscriptionMap);
  }

  /**
   * Process Request and create Response to get a subscription data.
   *
   * @param subscriberId
   *            Request data which contains target subscription ID.
   * @return Response to be sent.
   * @throws Exception if an error occurs.
   */
  private Response getSubscription(final String subscriberId)
      throws Exception {

    log.debug("");

    EventSubscription subscription = subscriptionMap.getSubscription(subscriberId);
    if (subscription == null) {
      return new Response(Response.NOT_FOUND, null);
    }

    return new Response(Response.OK, subscription);
  }

}
