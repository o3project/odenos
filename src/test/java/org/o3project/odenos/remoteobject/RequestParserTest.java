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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RequestParserTest {

  private RequestParser<String> target;

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
    target = new RequestParser<String>();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.RequestParser.ParsedRequest#ParsedRequest(org.
   * o3project.odenos.remoteobject.message.Request, T, java.util.Map<java.lang.String,
   * java.lang.String>, java.lang.String)}.
   */
  @Test
  public void testParsedRequestTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    Request request = Whitebox.getInternalState(parsed, "original");
    assertThat(request, is(notNullValue()));

    String result = Whitebox.getInternalState(parsed, "result");
    assertThat(result, is(notNullValue()));

    Map<String, String> params = Whitebox
        .getInternalState(parsed, "params");
    assertThat(params.size(), is(1));

    String query = Whitebox.getInternalState(parsed, "queriesString");
    assertThat(query, is("testQuery"));

    assertThat(request.objectId, is("testID"));
    assertThat(request.method, is(Method.GET));
    assertThat(request.path, is("testPath"));
    assertThat(request.getBodyValue().asRawValue().getString(),
        is("testBody"));
    assertThat(result, is("testResult"));
    assertThat(params.get("testKey"), is("testVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getRequest()}
   * .
   */
  @Test
  public void testGetRequestTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertThat(parsed.getRequest().objectId, is("testID"));
    assertThat(parsed.getRequest().method, is(Method.GET));
    assertThat(parsed.getRequest().path, is("testPath"));
    assertThat(parsed.getRequest().getBodyValue().asRawValue().getString(),
        is("testBody"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#setRequest(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testSetRequestTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    parsed.setRequest(new Request("testID2", Method.PUT, "testPath2",
        "txid", "testBody2"));

    assertThat(parsed.getRequest().objectId, is("testID2"));
    assertThat(parsed.getRequest().method, is(Method.PUT));
    assertThat(parsed.getRequest().path, is("testPath2"));
    assertThat(parsed.getRequest().getBodyValue().asRawValue().getString(),
        is("testBody2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getResult()}
   * .
   */
  @Test
  public void testGetResultTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertThat(parsed.getResult(), is("testResult"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getParam(java.lang.String)}
   * .
   */
  @Test
  public void testGetParamTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertThat(parsed.getParam("testKey"), is("testVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getParam(java.lang.String)}
   * .
   */
  @Test
  public void testGetParamTestWithNotExistingKey() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertNull(parsed.getParam("noKey"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#setParam(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetParamTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");
    parsed.setParam("testKey2", "testVal2");

    assertThat(parsed.getParam("testKey2"), is("testVal2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#setParam(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testSetParamTestWithExistingKey() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");
    parsed.setParam("testKey", "testVal2");

    assertThat(parsed.getParam("testKey"), is("testVal2"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getParams()}
   * .
   */
  @Test
  public void testGetParamsTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");
    Map<String, String> params = parsed.getParams();

    assertThat(params, is(notNullValue()));
    assertThat(params.size(), is(1));
    assertThat(params.get("testKey"), is("testVal"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#getQueriesString()}
   * .
   */
  @Test
  public void testGetQueriesStringTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertThat(parsed.getQueriesString(), is("testQuery"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#hasQuery()}.
   */
  @Test
  public void testHasQueryTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "testQuery");

    assertTrue(parsed.hasQuery());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#hasQuery()}.
   */
  @Test
  public void testHasQueryEmptyQueryTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, "");

    assertTrue(parsed.hasQuery());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.ParsedRequest#hasQuery()}.
   */
  @Test
  public void testHasQueryNullQueryTest() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    RequestParser<String>.ParsedRequest parsed =
        target.new ParsedRequest(new Request("testID", Method.GET,
            "testPath", "txid", "testBody"),
            "testResult", map, null);

    assertFalse(parsed.hasQuery());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#RequestParser()}.
   */
  @Test
  public void testRequestParserTest() {
    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#addRule(org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, T)}
   * .
   */
  @Test
  public void testAddRule() {
    target.addRule(Method.GET, "path/to/<param>", "result");
    RequestParser<String>.State state = Whitebox.getInternalState(target,
        "headState");

    Map<String, RequestParser<String>.State> transit;

    transit = Whitebox.getInternalState(state, "transitionTargets");
    assertThat(transit, is(notNullValue()));

    state = transit.get("path");
    assertThat(state, is(notNullValue()));

    transit = Whitebox.getInternalState(state, "transitionTargets");
    assertThat(transit, is(notNullValue()));

    state = transit.get("to");
    assertThat(state, is(notNullValue()));

    transit = Whitebox.getInternalState(state, "transitionTargets");
    assertThat(transit, is(notNullValue()));

    state = Whitebox.getInternalState(state, "parameteredTarget");
    assertThat(state, is(notNullValue()));

    Map<Request.Method, String> result = Whitebox.getInternalState(state,
        "results");
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get(Method.GET), is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#setDefaultResult(T)}.
   */
  @Test
  public void testSetDefaultResult() {
    target.setDefaultResult("defaultResult");

    String result = Whitebox.getInternalState(target, "defaultResult");
    assertThat(result, is(notNullValue()));
    assertThat(result, is("defaultResult"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#parse(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testParse() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    target.addRule(Method.GET, "path/to/<param>", "result");
    RequestParser<String>.ParsedRequest result =
        target.parse(new Request("testID", Method.GET, "path/to/abc",
            "txid", "testBody"));

    assertThat(result, is(notNullValue()));
    assertThat(result.getParam("param"), is("abc"));
    assertFalse(result.hasQuery());
    assertNull(result.getQueriesString());
    assertThat(result.getRequest().objectId, is("testID"));
    assertThat(result.getResult(), is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#parse(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testParseWithQuery() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    target.addRule(Method.GET, "path/to/<param>", "result");
    RequestParser<String>.ParsedRequest result =
        target.parse(new Request("testID", Method.GET,
            "path/to/abc?q=v", "txid", "testBody"));

    assertThat(result, is(notNullValue()));
    assertThat(result.getParam("param"), is("abc"));
    assertTrue(result.hasQuery());
    assertThat(result.getQueriesString(), is("q=v"));
    assertThat(result.getRequest().objectId, is("testID"));
    assertThat(result.getResult(), is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#parse(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testParseWithMultiGetParams() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    target.addRule(Method.GET, "path/to/<param>", "result");
    RequestParser<String>.ParsedRequest result =
        target.parse(new Request("testID", Method.GET,
            "path/to/abc?q=v?q2=v2", "txid", "testBody"));

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser#parse(org.o3project.odenos.remoteobject.message.Request)}
   * .
   */
  @Test
  public void testParseWithRuleUndefinedMethod() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("testKey", "testVal");
    target.addRule(Method.GET, "path/to/<param>", "result");
    RequestParser<String>.ParsedRequest result =
        target.parse(new Request("testID", Method.DELETE,
            "path/to/abc", "txid", "testBody"));

    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#State()}.
   */
  @Test
  public void testState() {
    RequestParser<String>.State result = target.new State();

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#State(org.o3project.odenos.remoteobject.RequestParser.SinkState)}
   * .
   */
  @Test
  public void testStateWithSinkState() {
    RequestParser<String>.State result = target.new State(
        target.new SinkState());
    RequestParser<String>.SinkState state = Whitebox.getInternalState(
        result, "sink");

    assertThat(result, is(notNullValue()));
    assertThat(state, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#addResult(org.o3project.odenos.remoteobject.message.Request.Method, T)}
   * .
   */
  @Test
  public void testAddResult() {
    RequestParser<String>.State state = target.new State();
    state.addResult(Method.GET, "result");
    Map<Method, String> result = Whitebox
        .getInternalState(state, "results");

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get(Method.GET), is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#removeResult(org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   */
  @Test
  public void testRemoveResult() {
    RequestParser<String>.State state = target.new State();
    state.addResult(Method.GET, "result");
    state.removeResult(Method.GET);
    Map<Method, String> result = Whitebox
        .getInternalState(state, "results");

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(0));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#removeResult(org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   */
  @Test
  public void testRemoveResultWithNotExistingKey() {
    RequestParser<String>.State state = target.new State();
    state.addResult(Method.GET, "result");
    state.removeResult(Method.DELETE);
    Map<Method, String> result = Whitebox
        .getInternalState(state, "results");

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(1));
    assertThat(result.get(Method.GET), is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#getResult(org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   */
  @Test
  public void testGetResult() {
    RequestParser<String>.State state = target.new State();
    state.addResult(Method.GET, "result");
    String result = state.getResult(Method.GET);

    assertThat(state, is(notNullValue()));
    assertThat(result, is("result"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#getResult(org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   */
  @Test
  public void testGetResultWithNotExistingKey() {
    RequestParser<String>.State state = target.new State();
    state.addResult(Method.GET, "result");
    String result = state.getResult(Method.DELETE);

    assertThat(state, is(notNullValue()));
    assertNull(result);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#createTransition(java.lang.String)}
   * .
   */
  @Test
  public void testCreateTransition() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State newState = target.new State();
    Map<String, RequestParser<String>.State> transition =
        Whitebox.getInternalState(state, "transitionTargets");
    transition.put("key", newState);
    RequestParser<String>.State result = state.createTransition("key");

    assertThat(state, is(notNullValue()));
    assertTrue(result == newState);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#createTransition(java.lang.String)}
   * .
   */
  @Test
  public void testCreateTransitionWithNotExistingSegment() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State newState = target.new State();
    Map<String, RequestParser<String>.State> transition =
        Whitebox.getInternalState(state, "transitionTargets");
    transition.put("key", newState);
    RequestParser<String>.State result = state.createTransition("nokey");

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertThat(transition, is(notNullValue()));
    assertThat(transition.size(), is(2));
    assertThat(transition.get("key"), is(notNullValue()));
    assertThat(transition.get("nokey"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#createParameterTransition(java.lang.String)}
   * .
   */
  @Test
  public void testCreateParameterTransition() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State result = state
        .createParameterTransition("param");

    RequestParser<String>.State paramTarget =
        Whitebox.getInternalState(state, "parameteredTarget");
    String paramName = Whitebox.getInternalState(state, "parameterName");

    assertThat(paramName, is("param"));
    assertThat(state, is(notNullValue()));
    assertThat(result, is(paramTarget));
    assertThat(paramTarget, is(notNullValue()));
    assertThat(result, is(paramTarget));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.State#createParameterTransition(java.lang.String)}
   * .
   */
  @Test
  public void testCreateParameterTransitionWhenParameteredTargetIsNonNull() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State newState = target.new State();
    Whitebox.setInternalState(state, "parameteredTarget", newState);
    Whitebox.setInternalState(state, "parameterName", "param");
    RequestParser<String>.State result = state
        .createParameterTransition("param");

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertTrue(result == newState);
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.RequestParser.State#nextState(java.lang.String,
   * java.util.Map<java.lang.String, java.lang.String>)}.
   */
  @Test
  public void testNextState() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State nextState = target.new SinkState();
    @SuppressWarnings("unchecked")
    Map<String, String> map = mock(HashMap.class);
    Whitebox.setInternalState(state, "sink", nextState);
    RequestParser<String>.State result = state.nextState("segment", map);

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertTrue(result == nextState);
    verify(map, times(0)).put((String) anyObject(), (String) anyObject());
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.RequestParser.State#nextState(java.lang.String,
   * java.util.Map<java.lang.String, java.lang.String>)}.
   */
  @Test
  public void testNextStateWhenParameteredTargetIsNonNull() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State nextState = target.new SinkState();
    Map<String, String> map = new HashMap<String, String>();
    Whitebox.setInternalState(state, "parameteredTarget", nextState);
    Whitebox.setInternalState(state, "parameterName", "param");
    RequestParser<String>.State result = state.nextState("segment", map);

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertTrue(result == nextState);
    assertThat(map.size(), is(1));
    assertThat(map.get("param"), is("segment"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.RequestParser.State#nextState(java.lang.String,
   * java.util.Map<java.lang.String, java.lang.String>)}.
   */
  @Test
  public void testNextStateWhenTransitionTargetsIsNonNull() {
    RequestParser<String>.State state = target.new State();
    RequestParser<String>.State nextState = target.new SinkState();
    Map<String, RequestParser<String>.State> targetsMap =
        new HashMap<String, RequestParser<String>.State>();
    @SuppressWarnings("unchecked")
    Map<String, String> map = mock(HashMap.class);
    targetsMap.put("segment", nextState);
    Whitebox.setInternalState(state, "transitionTargets", targetsMap);
    RequestParser<String>.State result = state.nextState("segment", map);

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertTrue(result == nextState);
    verify(map, times(0)).put((String) anyObject(), (String) anyObject());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.RequestParser.SinkState#getResult(org.o3project.odenos.remoteobject.message.Request.Method)}
   * .
   */
  @Test
  public void testSinkStateGetResult() {
    RequestParser<String>.SinkState state = target.new SinkState();
    Whitebox.setInternalState(target, "defaultResult", "result");
    String result = state.getResult(Method.GET);

    assertThat(state, is(notNullValue()));
    assertThat(result, is("result"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.util.RequestParser.SinkState#nextState(java.lang.
   * String, java.util.Map<java.lang.String, java.lang.String>)}.
   */
  @Test
  public void testSinkStateNextState() {
    RequestParser<String>.SinkState state = target.new SinkState();
    @SuppressWarnings("unchecked")
    Map<String, String> map = mock(HashMap.class);
    RequestParser<String>.State result = state.nextState("segment", map);

    assertThat(state, is(notNullValue()));
    assertThat(result, is(notNullValue()));
    assertTrue(result == state);
    verify(map, times(0)).put((String) anyObject(), (String) anyObject());
    verify(map, times(0)).get((String) anyObject());
  }
}
