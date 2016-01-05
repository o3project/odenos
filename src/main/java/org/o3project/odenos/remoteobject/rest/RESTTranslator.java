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

package org.o3project.odenos.remoteobject.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.servlets.GzipFilter;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.message.Event;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.rest.servlet.RestServlet;
import org.o3project.odenos.remoteobject.rest.servlet.StreamServlet;
import org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * RESTTranslator transforms HTTP Transaction to transaction of ODENOS.
 *
 */
public class RESTTranslator extends RemoteObject {
  private static class DistKey extends ArrayList<String> {
    private static final long serialVersionUID = 1L;

    public DistKey(String objectId, String eventType) {
      super();
      this.add(objectId);
      this.add(eventType);
    }
  }

  private static final MessagePack messagePack = new MessagePack();
  private static final Integer DEFAULT_SERVER_PORT = 10080;

  private static final Logger log = LogManager.getLogger(RESTTranslator.class);
  private static String txid = null;

  private final Map<String, AsyncContext> asyncContextMap = new HashMap<String, AsyncContext>();
  private final Map<DistKey, Set<String>> distributionTable = new HashMap<DistKey, Set<String>>();
  private Server server;

  private final HttpSessionListener sessionListener = new HttpSessionListener() {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
      HttpSession session = se.getSession();
      String subscriptionId = session.getId();
      RESTTranslator.this.log.info("A session ({}) has been destroyed.", subscriptionId);

      AsyncContext context = RESTTranslator.this.removeAsyncContext(subscriptionId);
      context.complete(); // need it?

      // this "obj" separated for casting warning avoidance. 
      Object obj = session.getAttribute(Attributes.SUBSCRIPTION_TABLE);
      @SuppressWarnings("unchecked")
      Map<String, Set<String>> subscriptionTable = (Map<String, Set<String>>) obj;
      RESTTranslator.this.modifyDistributionSetting(subscriptionId, null, subscriptionTable);
    }
  };

  /**
   * Constructor.
   * @param objectId Object ID of RESTTranslator.
   * @param dispatcher Message Dispatcher object.
   */
  public RESTTranslator(String objectId, MessageDispatcher dispatcher) {
    this(objectId, dispatcher, null);
  }

  /**
   * Constructor.
   * @param objectId Object ID of RESTTranslator.
   * @param disp Message Dispatcher object.
   * @param propertiesFilePath a path of GUI properties file.
   */
  public RESTTranslator(String objectId, MessageDispatcher disp, String propertiesFilePath) {
    this(objectId, disp, propertiesFilePath, DEFAULT_SERVER_PORT);
  }

  /**
   * Constructor.
   * @param objectId
   *            ObjectId of RESTTranslator
   * @param dispatcher
   *            MessageDispatcher
   * @param root
   *            DocumentRoot
   * @param serverPort
   *            Odenos Server Port
   */
  public RESTTranslator(final String objectId, final MessageDispatcher dispatcher,
      final String root, final Integer serverPort) {
    super(objectId, dispatcher);
    this.startServer("0.0.0.0", serverPort, root);
  }

  private void stopServer() {
    if (this.server != null) {
      synchronized (this.server) {
        try {
          this.server.stop();
        } catch (Exception e) {
          this.log.warn("Failed to stop the existing Jetty server.", e);
        } finally {
          this.server = null;
        }
      }
    }
  }

  private Server server() {
    if (this.server != null) {
      synchronized (this.server) {
        return this.server;
      }
    } else {
      return null;
    }
  }

  private void startServer(final String host, final Integer port, final String root) {
    this.stopServer();

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        RESTTranslator.this.server = new Server(new InetSocketAddress(host, port));
        synchronized (RESTTranslator.this.server) {
          HashMap<String, String> param = new HashMap<String, String>();
          param.put(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
          param.put(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE");
          param.put(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
          FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
          filterHolder.setInitParameters(param);

          ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
          sch.setAttribute(Attributes.REST_TRANSLATOR, RESTTranslator.this);
          sch.setAttribute("resource.root", root);
          sch.getSessionHandler().addEventListener(RESTTranslator.this.sessionListener);
          sch.addFilter(GzipFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
          EnumSet<DispatcherType> dispatchers = EnumSet.of(DispatcherType.REQUEST);
          sch.addFilter(filterHolder, "/*", dispatchers);

          RESTTranslator.this.server.setHandler(sch);
          this.registerServlet(sch, RestServlet.class);
          this.registerServlet(sch, StreamServlet.class);
          this.registerServlet(sch, SubscriptionsServlet.class);

          try {
            RESTTranslator.this.server.start();
          } catch (Exception e) {
            RESTTranslator.this.log.error("Failed to start the Jetty server.", e);
            return;
          }
        }

        try {
          RESTTranslator.this.server.join();
        } catch (InterruptedException e) {
          RESTTranslator.this.log.error("Failed to join the Jetty server.", e);
          return;
        }
      }

      private void registerServlet(ServletContextHandler sch,
          Class<? extends HttpServlet> servlet) {
        ServletHolder holder = new ServletHolder(servlet);
        WebServlet annotation = servlet.getAnnotation(WebServlet.class);
        holder.setAsyncSupported(annotation.asyncSupported());
        String[] patterns = annotation.urlPatterns();
        if (patterns.length == 0) {
          patterns = annotation.value();
        }

        for (String pattern : patterns) {
          sch.addServlet(holder, pattern);
        }
      }
    });

    thread.start();
  }

  /**
   * Add subscription id to async context map.
   * @param subscriptionId subscription id.
   * @param context async context map.
   */
  public void setAsyncContext(String subscriptionId, AsyncContext context) {
    synchronized (this.asyncContextMap) {
      this.asyncContextMap.put(subscriptionId, context);
    }
  }

  /**
   * Remove subscription id from async context map.
   * @param subscriptionId subscription id.
   * @return AsyncContext
   */
  public AsyncContext removeAsyncContext(String subscriptionId) {
    synchronized (this.asyncContextMap) {
      return this.asyncContextMap.remove(subscriptionId);
    }
  }

  /**
   * Modify distribution settings.
   * @param subscriptionId subscription ID.
   * @param added added map.
   * @param removed removed map.
   */
  public void modifyDistributionSetting(String subscriptionId,
      Map<String, ? extends Collection<String>> added,
      Map<String, ? extends Collection<String>> removed) {

    synchronized (this.distributionTable) {
      if (added != null) {
        for (Entry<String, ? extends Collection<String>> entry : added.entrySet()) {
          String objectId = entry.getKey();
          Collection<String> eventTypes = entry.getValue();

          for (String evType : eventTypes) {
            Set<String> subscriptionIdSet =
                this.distributionTable.get(new DistKey(objectId, evType));
            if (subscriptionIdSet == null) {
              subscriptionIdSet = new HashSet<String>();
              this.distributionTable.put(new DistKey(objectId, evType), subscriptionIdSet);
              this.eventSubscription.addFilter(objectId, evType);
            }
            subscriptionIdSet.add(subscriptionId);
          }
        }
      }

      if (removed != null) {
        for (Entry<String, ? extends Collection<String>> entry : removed.entrySet()) {
          String objectId = entry.getKey();
          Collection<String> eventTypes = entry.getValue();

          for (String evtype : eventTypes) {
            Set<String> subscriptionIdSet =
                this.distributionTable.get(new DistKey(objectId, evtype));
            if (subscriptionIdSet == null) {
              continue;
            }

            subscriptionIdSet.remove(subscriptionId);
            if (subscriptionIdSet.isEmpty()) {
              this.eventSubscription.removeFilter(objectId, evtype);
              this.distributionTable.remove(new DistKey(objectId, evtype));
            }
          }
        }
      }
    }

    try {
      this.applyEventSubscription();
    } catch (Exception e) {
      this.log.warn("Failed to update the ODENOS Event subscription.", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.nec.odenos.remoteobject.RemoteObject#onEvent(com.nec.odenos.remoteobject.message.
   * Event)
   */
  @Override
  protected void onEvent(Event event) {
    Set<String> subscriptionIds = null;
    synchronized (this.distributionTable) {
      subscriptionIds =
          this.distributionTable.get(new DistKey(event.publisherId, event.eventType));
      if (subscriptionIds == null || subscriptionIds.isEmpty()) {
        this.log.warn("No one subscribes the {} of objectId:{}.",
            event.publisherId, event.eventType);

        this.distributionTable.remove(new DistKey(event.publisherId, event.eventType));
        this.eventSubscription.removeFilter(event.publisherId, event.eventType);
        try {
          this.applyEventSubscription();
        } catch (Exception e) {
          this.log.warn("Failed to update the ODENOS Event subscription.", e);
        }
        return;
      }
    }

    Value value;
    try {
      byte[] packed = this.messagePack.write(event);
      value = this.messagePack.read(packed);
    } catch (IOException e) {
      this.log.error("Failed to reserialize the Event object.", e);
      return;
    }

    for (String subscriptionId : subscriptionIds) {
      AsyncContext context = this.removeAsyncContext(subscriptionId);
      if (context == null) {
        continue;
      }

      try {
        context.getResponse().getWriter().write(value.toString());
      } catch (IOException e) {
        this.log.error("Failed to write the Event object as an HTTP response", e);
      }
      context.complete();
    }
  }
}
