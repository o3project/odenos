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

import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Stream servlet.
 *
 */
@WebServlet(urlPatterns = { "/event/stream" }, asyncSupported = true)
public class StreamServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(StreamServlet.class);

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
    String subscriptionId = session.getId();

    AsyncContext asyncContext = req.startAsync();

    RESTTranslator translator = (RESTTranslator) req.getServletContext()
        .getAttribute(Attributes.REST_TRANSLATOR);
    translator.setAsyncContext(subscriptionId, asyncContext);

    if (session.getAttribute(Attributes.SUBSCRIPTION_TABLE) == null) {
      // new session
      this.logger.info("New stream: {}", subscriptionId);
      session.setAttribute(Attributes.SUBSCRIPTION_TABLE, new HashMap<String, Set<String>>());

      resp.setHeader("Content-type", "application/json");
      resp.getWriter().write(String.format("{\"subscription_id\": \"%s\"}", subscriptionId));
      asyncContext.complete();
    }
  }
}
