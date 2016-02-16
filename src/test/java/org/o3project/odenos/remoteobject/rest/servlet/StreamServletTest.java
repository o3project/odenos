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

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 */
public class StreamServletTest {

  private StreamServlet target;

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() throws Exception {

    target = Mockito.spy(new StreamServlet());
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.StreamServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   *
   * @throws Exception throws Exception in targets
   */
  @Test
  public void testDoGetHttpServletRequestHttpServletResponse() throws Exception {

    /*
     * setting
     */
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    HttpSession httpSession = Mockito.mock(HttpSession.class);
    doReturn(httpSession).when(request).getSession();
    doReturn("SubscriptionId").when(httpSession).getId();

    AsyncContext ayncContext = Mockito.mock(AsyncContext.class);
    doReturn(ayncContext).when(request).startAsync();

    ServletContext servletContext = Mockito.mock(ServletContext.class);
    doReturn(servletContext).when(request).getServletContext();

    RESTTranslator translator = Mockito.mock(RESTTranslator.class);
    doReturn(translator).when(servletContext).getAttribute(Attributes.REST_TRANSLATOR);
    doNothing().when(translator).setAsyncContext("SubscriptionId", ayncContext);

    PrintWriter printWriter = Mockito.mock(PrintWriter.class);
    doReturn(printWriter).when(response).getWriter();
    doNothing().when(printWriter).write(anyString());

    /*
     * test
     */
    target.doGet(request, response);

    /*
     * check
     */
    verify(translator).setAsyncContext("SubscriptionId", ayncContext);

    verify(httpSession).setAttribute(eq(Attributes.SUBSCRIPTION_TABLE), anyCollection());
    verify(response).setHeader("Content-type", "application/json");
    verify(printWriter).write("{\"subscription_id\": \"SubscriptionId\"}");

  }

}
