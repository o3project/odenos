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

import org.json.simple.JSONValue;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Rest servlet.
 *
 */
@WebServlet("/*")
public class RestServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Pattern PATH_PATTERN = Pattern
      .compile("^/([^/]+)/(.+)");

  private static final Logger log = LogManager.getLogger(RestServlet.class);

  private final MessagePack messagePack = new MessagePack();

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
    Request.Method method;
    try {
      method = Request.Method.valueOf(req.getMethod().toUpperCase());
    } catch (IllegalArgumentException e) {
      super.service(req, resp);
      return;
    }

    do {
      if (!method.equals(Request.Method.GET)) {
        break;
      }

      String root = (String) this.getServletContext().getAttribute("resource.root");
      if (root == null) {
        break;
      }


      Path path = Paths.get(root, req.getPathInfo());
      log.debug("Trying to read \"{}\".", path);

      if (Files.isReadable(path)) {
        if (Files.isDirectory(path)) {
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          return;
        }

        try (OutputStream out = resp.getOutputStream()) {
          Files.copy(path, out);
        } catch (IOException e) {
          // just ignore.
          log.error(LogMessage.buildLogMessage(50025, LogMessage.getSavedTxid(), "Failed serving {}", path), e);
          resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return;
      }
    } while (false);

    doRequestToComponent(req, resp, method);
  }

  protected void doRequestToComponent(
      HttpServletRequest req, HttpServletResponse resp, Request.Method method)
      throws ServletException, IOException {
    Matcher matcher = RestServlet.PATH_PATTERN.matcher(req.getRequestURI());
    if (!matcher.find()) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String objectId = matcher.group(1);
    String path = matcher.group(2);
    if (req.getQueryString() != null) {
      path = path + "?" + URLDecoder.decode(req.getQueryString(), "utf-8");
    }
    Object reqBody = JSONValue.parse(req.getReader());

    LogMessage.createTxid(LogMessage.TXID_SYSTEMMGR_OFFSET);

    RESTTranslator translator = (RESTTranslator) req.getServletContext()
        .getAttribute(Attributes.REST_TRANSLATOR);
    Response odenosResp;
    try {
      odenosResp = translator.request(objectId, method, path, reqBody);
    } catch (Exception e) {
      this.log.debug("Failed to request [{}, {}, {}, {}]",
          objectId, method, path, reqBody, e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    resp.setStatus(odenosResp.statusCode);
    if (!odenosResp.isBodyNull()) {
      Value value;
      try {
        byte[] packed = this.messagePack.write(odenosResp);
        value = this.messagePack.read(packed);
      } catch (IOException e) {
        this.log.debug("Failed to serialize a response body. /req:[{}, {}, {}, {}]",
            objectId, method, path, reqBody);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }

      resp.getWriter().write(value.asArrayValue().get(1).toString());
    }
  }
}
