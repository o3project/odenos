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

import org.o3project.odenos.remoteobject.message.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * RequestParser parses request method and request path to determine what kind
 * of request is sent. User should set decision rules and corresponding object
 * in advance. After that RequestParser can parse request and return
 * corresponding object.
 *
 * @param <T> object class.
 */
public class RequestParser<T> {

  /**
   * ParsedRequest contains result of parsing Request object and original
   * Request itself.
   *
   */
  public class ParsedRequest {
    private T result;
    private Request original;
    private Map<String, String> params;
    private String queriesString = null;

    /**
     * Constructor.
     * @param original a request.
     * @param result result.
     * @param params map of parameters.
     * @param queriesString queries string.
     */
    public ParsedRequest(Request original, T result,
        Map<String, String> params, String queriesString) {
      this.original = original;
      this.result = result;
      this.params = params;
      this.queriesString = queriesString;
    }

    /**
     * Get original Request object parsed.
     *
     * @return Original Request object.
     */
    public Request getRequest() {
      return this.original;
    }

    public void setRequest(Request req) {
      this.original = req;
    }

    /**
     * Get a result come out from parsing a request.
     *
     * @return Result value.
     */
    public T getResult() {
      return this.result;
    }

    /**
     * Get a parameter value extracted from parsing a request.
     *
     * @param key
     *            Parameter name to get value.
     * @return Parameter value if exists. null if not exists.
     */
    public String getParam(String key) {
      return params.get(key);
    }

    public void setParam(String key, String value) {
      params.put(key, value);
    }

    /**
     * Get all parameters as Map object.
     *
     * @return Map of parameters.
     */
    public Map<String, String> getParams() {
      return params;
    }

    public String getQueriesString() {
      return queriesString;
    }

    public boolean hasQuery() {
      return this.queriesString != null;
    }
  }

  /**
   * RequestParser has internal Finite-State Machine to parse request.
   * headState represents the entrance state to begin transit.
   */
  private State headState = new State(new SinkState());

  private T defaultResult = null;

  public RequestParser() {
  }

  /**
   * Add parsing rule. Set conditions of method and path and expected result
   * value.
   *
   * @param method
   *            Request method condition.
   * @param path
   *            Path condition.
   * @param result
   *            Result object to be returned.
   */
  public void addRule(Request.Method method, String path, T result) {
    String[] pathStrings = path.split("/");

    State state = headState;

    for (int i = 0; i < pathStrings.length; ++i) {
      String name = pathStrings[i];
      if (name.startsWith("<") && name.endsWith(">")) {
        // Parameter path
        String paramName = name.substring(1, name.length() - 1);
        state = state.createParameterTransition(paramName);
      } else {
        // Constant path
        state = state.createTransition(name);
      }
    }

    state.addResult(method, result);
  }

  public void setDefaultResult(T result) {
    defaultResult = result;
  }

  /**
   * Parse given request.
   *
   * @param req
   *            Request to be parsed.
   * @return Parsed request.
   */
  public ParsedRequest parse(Request req) {
    String path = null;
    String queries = null;
    if (req.path.indexOf('?') != -1) {
      String[] splited = req.path.split("\\?");
      if (splited.length != 2) {
        return null;
      } else {
        path = splited[0];
        queries = splited[1];
      }
    } else {
      path = req.path;
    }

    String[] pathStrings = path.split("/");
    Map<String, String> params = new HashMap<String, String>();

    State state = headState;
    for (int i = 0; i < pathStrings.length; ++i) {
      state = state.nextState(pathStrings[i], params);
    }

    T result = state.getResult(req.method);
    if (result == null) {
      return null;
    }

    return new ParsedRequest(req, result, params, queries);
  }

  /**
   * FSM state used to parse path.
   *
   */
  protected class State {

    /**
     * Map of transition target states. Holds path segment and which state
     * to transit.
     */
    private Map<String, State> transitionTargets = new HashMap<String, State>();

    /**
     * sinkState represents the state to which parser comes when no
     * transition rule is set.
     */
    private SinkState sink = null;

    /**
     * Transition target when path segment is a parameter. This target is
     * used only when path segment doesn't match other target.
     */
    private State parameteredTarget = null;
    private String parameterName = null;

    /**
     * Map holding request method versus result object.
     */
    private Map<Request.Method, T> results = new HashMap<Request.Method, T>();

    public State() {
    }

    public State(SinkState sink) {
      this.sink = sink;
    }

    /**
     * Add result object corresponding to request method.
     *
     * @param method
     *            Request method expected.
     * @param result
     *            Result object to be returned.
     */
    public void addResult(Request.Method method, T result) {
      results.put(method, result);
    }

    /**
     * Remove result object.
     *
     * @param method
     *            Request method expected.
     */
    public void removeResult(Request.Method method) {
      results.remove(method);
    }

    /**
     * Get result object from request method.
     *
     * @param method
     *            Request method.
     * @return Corresponding result object.
     */
    public T getResult(Request.Method method) {
      return results.get(method);
    }

    /**
     * Create State object with transition from this object. If already
     * exists, returns existing State.
     *
     * @param pathSegment
     *            Path segment to trigger transition.
     * @return Created State object.
     */
    public State createTransition(String pathSegment) {
      State existing = transitionTargets.get(pathSegment);
      if (existing != null) {
        return existing;
      }

      State next = new State(sink);
      transitionTargets.put(pathSegment, next);

      return next;
    }

    /**
     * Create State object with parameter transition from this object. If
     * already exists, returns existing State.
     *
     * @param paramName
     *            Name of parameter.
     * @return Created State object.
     */
    public State createParameterTransition(String paramName) {
      assert (parameteredTarget == null || paramName
          .equals(parameterName));

      if (parameteredTarget != null) {
        return parameteredTarget;
      }

      State next = new State(sink);
      parameteredTarget = next;
      parameterName = paramName;

      return next;
    }

    /**
     * Get state to transit when path segment is given. If path segment is
     * parameter, add key-value to params map.
     *
     * @param pathSegment
     *            Path segment to trigger transition.
     * @param params
     *            Map of parameters.
     * @return Transition target State.
     */
    public State nextState(String pathSegment, Map<String, String> params) {
      State next = transitionTargets.get(pathSegment);

      if (next == null && (!pathSegment.equals(""))) {
        if (parameteredTarget != null) {
          params.put(parameterName, pathSegment);
          next = parameteredTarget;
        } else {
          next = sink;
        }
      }

      return next;
    }
  }

  protected class SinkState extends State {
    /**
     * Always returns defautValue.
     */
    @Override
    public T getResult(Request.Method method) {
      return defaultResult;
    }

    /**
     * Always returns itself.
     */
    @Override
    public State nextState(String pathSegment, Map<String, String> params) {
      return this;
    }
  }
}
