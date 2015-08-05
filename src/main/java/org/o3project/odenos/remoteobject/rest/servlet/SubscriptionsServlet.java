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

package org.o3project.odenos.remoteobject.rest.servlet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Subscriptions servlet.
 *
 */
@WebServlet("/event/subscriptions/*")
public class SubscriptionsServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Pattern PATH_PATTERN = Pattern.compile("^/event/subscriptions/([^/]+)/?$");

  private static final Logger log = LoggerFactory.getLogger(SubscriptionsServlet.class);

  private String subscriptionId;

  /*
   * (non-Javadoc)
   *
   * @see
   * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    switch (req.getMethod().toUpperCase()) {
      case "GET":
      case "POST":
      case "PUT":
      case "DELETE":
        break;
      default:
        super.service(req, resp);
        return;
    }

    Matcher matcher = PATH_PATTERN.matcher(req.getRequestURI());
    if (!matcher.find()) {
      this.log.debug("The indicated path is not available. /{}", req.getRequestURI());
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    HttpSession session = req.getSession(false);
    if (session == null) {
      // not start the session yet.
      this.log.debug("The session is not started yet.");
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String subscriptionId = matcher.group(1);
    if (!session.getId().equals(subscriptionId)) {
      // mismatching the subscription_id.
      this.log.debug("The Subscription ID ({}) is illegal. /Session ID: {}",
          subscriptionId, session.getId());
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    this.subscriptionId = subscriptionId;
    super.service(req, resp);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession();

    // this "obj" separated for casting warning avoidance. 
    Object obj = session.getAttribute(Attributes.SUBSCRIPTION_TABLE);
    @SuppressWarnings("unchecked")
    Map<String, Set<String>> subscriptionTable = (Map<String, Set<String>>) obj;
    if (subscriptionTable == null) {
      this.log.debug("A Subscription Table is not found. /{}", session.getId());
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().write(toJsonStringFrom(subscriptionTable));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession();

    // this "obj" separated for casting warning avoidance. 
    Object obj = session.getAttribute(Attributes.SUBSCRIPTION_TABLE);
    @SuppressWarnings("unchecked")
    Map<String, Set<String>> origTable = (Map<String, Set<String>>) obj;
    if (origTable == null) {
      this.log.debug("A Subscription Table is not found. /{}", session.getId());
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String reqBody = IOUtils.toString(req.getReader());
    Map<String, Set<String>> reqTable = this.deserialize(reqBody);
    if (reqTable == null) {
      this.log.debug("Failed to deserialize the request body. /{}", reqBody);
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Map<String, Collection<String>> addedMap = new HashMap<String, Collection<String>>();
    Map<String, Collection<String>> removedMap = new HashMap<String, Collection<String>>();

    for (Entry<String, Set<String>> reqEntry : reqTable.entrySet()) {
      String objectId = reqEntry.getKey();
      Set<String> reqEvents = reqEntry.getValue();

      Set<String> origEvents = origTable.get(objectId);
      if (origEvents == null) {
        // All events are unregistered yet.
        addedMap.put(objectId, reqEvents);
        origTable.put(objectId, reqEvents);
        continue;
      }

      // generating diff.
      @SuppressWarnings("unchecked")
      Collection<String> added = (Collection<String>) CollectionUtils
          .subtract(reqEvents, origEvents);
      addedMap.put(objectId, added);

      @SuppressWarnings("unchecked")
      Collection<String> removed = (Collection<String>) CollectionUtils
          .subtract(origEvents, reqEvents);
      removedMap.put(objectId, removed);
    }

    session.setAttribute(Attributes.SUBSCRIPTION_TABLE, reqTable);

    RESTTranslator translator = (RESTTranslator) req.getServletContext()
        .getAttribute(Attributes.REST_TRANSLATOR);
    translator.modifyDistributionSetting(
        this.subscriptionId, addedMap, removedMap);

    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().write(toJsonStringFrom(reqTable));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Set<String>> deserialize(String reqBody) {
    JSONObject reqMap;
    try {
      reqMap = (JSONObject) JSONValue.parseWithException(reqBody);
    } catch (Exception e) {
      return null;
    }

    if (reqMap == null) {
      return null;
    }

    Map<String, Set<String>> result = new HashMap<String, Set<String>>();
    for (Entry<String, ?> entry : (Set<Entry<String, ?>>) reqMap.entrySet()) {
      Object value = entry.getValue();
      if (!(value instanceof List<?>)) {
        return null;
      }

      Set<String> eventTypeSet = new HashSet<String>();
      List<?> reqList = (List<?>) value;
      for (Object eventType : reqList) {
        if (!(eventType instanceof String)) {
          return null;
        }
        eventTypeSet.add((String) eventType);
      }

      if (!eventTypeSet.isEmpty()) {
        result.put(entry.getKey(), eventTypeSet);
      }
    }

    return result;
  }

  private static String toJsonStringFrom(Map<String, Set<String>> subscriptionTable) {
    Map<String, List<String>> buf = new HashMap<String, List<String>>();

    for (Entry<String, Set<String>> entry : subscriptionTable.entrySet()) {
      buf.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
    }
    return JSONObject.toJSONString(buf);
  }
}
