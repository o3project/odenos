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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.remoteobject.rest.Attributes;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SubscriptionsServlet.class, IOUtils.class })
@PowerMockIgnore({"javax.management.*"})
public class SubscriptionsServletTest {

  private SubscriptionsServlet target;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;

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

    target = Mockito.spy(new SubscriptionsServlet());

    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);

    session = Mockito.mock(HttpSession.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    request = null;
    response = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
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
    target = Mockito.spy(new SubscriptionsServlet());

    doReturn("POST").when(request).getMethod();
    doReturn("/event/subscriptions/SubscriptionId").when(request)
        .getRequestURI();

    doReturn(session).when(request).getSession(false);
    doReturn("SubscriptionId").when(session).getId();

    /*
     * XXX Mockito not supported mock to only super.method()
     */
    // doNothing().when((HttpServlet)target).service(request, response);
    doReturn("1.1").when(request).getProtocol();
    doNothing().when(response).sendError(anyInt(), anyString());
    /*
     *
     */

    /*
     * test
     */
    target.service(request, response);

    /*
     * check
     */
    assertThat(
        (String) Whitebox.getInternalState(target, "subscriptionId"),
        is("SubscriptionId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDoGetHttpServletRequestHttpServletResponse()
      throws Exception {

    /*
     * setting
     */
    doReturn(session).when(request).getSession();

    Map<String, Set<String>> subscriptionTable = new HashMap<>();
    doReturn(subscriptionTable).when(session).getAttribute(
        Attributes.SUBSCRIPTION_TABLE);

    doNothing().when(response).setStatus(anyInt());

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
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(printWriter).write(anyString());

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDoPutHttpServletRequestHttpServletResponse()
      throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SubscriptionsServlet());

    doReturn(session).when(request).getSession();

    Map<String, Set<String>> subscriptionTable = new HashMap<>();
    doReturn(subscriptionTable).when(session).getAttribute(
        Attributes.SUBSCRIPTION_TABLE);

    doReturn(Mockito.mock(BufferedReader.class)).when(request).getReader();
    PowerMockito.mockStatic(IOUtils.class);
    PowerMockito.when(IOUtils.toString((BufferedReader) anyObject()))
        .thenReturn("body");

    Map<String, Set<String>> reqTable = new HashMap<>();
    PowerMockito.doReturn(reqTable).when(target, "deserialize", "body");

    ServletContext servletContext = Mockito.mock(ServletContext.class);
    doReturn(servletContext).when(request).getServletContext();

    RESTTranslator translator = Mockito.mock(RESTTranslator.class);
    doReturn(translator).when(servletContext).getAttribute(
        Attributes.REST_TRANSLATOR);

    PrintWriter printWriter = Mockito.mock(PrintWriter.class);
    doReturn(printWriter).when(response).getWriter();
    doNothing().when(printWriter).write(anyString());

    /*
     * test
     */
    target.doPut(request, response);

    /*
     * check
     */
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(printWriter).write(anyString());
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet#deserialize(String)}.
   *
   * @throws Exception
   */
  @Test
  public void testDeserialize() throws Exception {

    /*
     * test
     */
    Map<String, Set<String>> result = Whitebox.invokeMethod(target,
        "deserialize",
        "{\"Key1\":[\"Value1\",\"Value2\"]}");

    /*
     * check
     */
    assertThat(result.containsKey("Key1"), is(true));

    Set<String> resultValues = result.get("Key1");
    assertThat(resultValues.size(), is(2));
    assertThat(resultValues.contains("Value1"), is(true));
    assertThat(resultValues.contains("Value2"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.rest.servlet.SubscriptionsServlet#toJsonStringFrom(Map<String, Set<String>>)}.
   *
   * @throws Exception
   */
  @Test
  public void testToJsonStringFrom() throws Exception {

    /*
     * setting
     */
    Map<String, Set<String>> subscriptionTable = new HashMap<>();
    Set<String> values = new HashSet<>();
    values.add("Value1");
    values.add("Value2");
    subscriptionTable.put("Key1", values);

    /*
     * test
     */
    String result = Whitebox.invokeMethod(SubscriptionsServlet.class,
        "toJsonStringFrom", subscriptionTable);

    /*
     * check
     */
    assertThat(result, is("{\"Key1\":[\"Value1\",\"Value2\"]}"));
  }

}
