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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class RestServletTest {

  private RestServlet target;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    target = Mockito.spy(new RestServlet());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.RestServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse()
      throws Exception {

    /*
     * setting
     */
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    ServletContext servletContext = Mockito.mock(ServletContext.class);

    String classFileName = "/"
        + RestServlet.class.getName().replaceAll("\\.", "/") + ".class";
    String path = RestServlet.class.getResource(classFileName).getPath();
    doReturn("GET").when(request).getMethod();
    doReturn(servletContext).when(target).getServletContext();
    doReturn("/").when(servletContext).getAttribute("resource.root");
    doReturn(path).when(request).getPathInfo();

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    ServletOutputStream out = Mockito.mock(ServletOutputStream.class);
    doReturn(out).when(response).getOutputStream();

    doNothing().when(target).doRequestToComponent(request, response,
        Request.Method.GET);

    /*
     * test
     */
    target.service(request, response);

    /*
     * check
     */
    verify(target, never()).doRequestToComponent(request, response,
        Request.Method.GET);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.RestServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse_PostPutDelete()
      throws Exception {

    /*
     * setting
     */
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    when(request.getMethod()).thenReturn("POST").thenReturn("PUT")
        .thenReturn("DELETE");
    doNothing().when(target).doRequestToComponent(eq(request),
        eq(response),
        (Request.Method) anyObject());

    /*
     * test
     */
    target.service(request, response);
    target.service(request, response);
    target.service(request, response);

    /*
     * check
     */
    InOrder methods = Mockito.inOrder(target);
    methods.verify(target).doRequestToComponent(request, response,
        Request.Method.POST);
    methods.verify(target).doRequestToComponent(request, response,
        Request.Method.PUT);
    methods.verify(target).doRequestToComponent(request, response,
        Request.Method.DELETE);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.RestServlet#doRequestToComponent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDoRequestToComponent() throws Exception {

    /*
     * setting
     */
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(anyInt());

    ServletContext servletContext = Mockito.mock(ServletContext.class);
    RESTTranslator translator = Mockito.mock(RESTTranslator.class);

    doReturn("/ObjectId/Path/c").when(request).getRequestURI();
    doReturn(servletContext).when(request).getServletContext();
    doReturn(translator).when(servletContext).getAttribute(
        Attributes.REST_TRANSLATOR);

    Response settingOdenosResponse =
        new Response(Response.OK, ValueFactory.createRawValue("Body"));
    settingOdenosResponse.statusCode = Response.OK;
    doReturn(settingOdenosResponse).when(translator).request(
        eq("ObjectId"),
        eq(Request.Method.POST), eq("Path/c"), anyObject());

    PrintWriter printWriter = Mockito.mock(PrintWriter.class);
    doReturn(printWriter).when(response).getWriter();
    doNothing().when(printWriter).write(anyString());

    /*
     * test
     */
    target.doRequestToComponent(request, response, Request.Method.POST);

    /*
     * check
     */
    verify(response).setStatus(Response.OK);

    verify(translator).request(eq("ObjectId"), eq(Request.Method.POST),
        eq("Path/c"),
        anyObject());

  }

}
