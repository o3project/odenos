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

package org.o3project.odenos.core.component.network.flow.basic;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.msgpack.type.Value;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for BasicFlow.
 *
 * 
 *
 */
public class BasicFlowTest {
  private BasicFlow target = null;

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
    target = new BasicFlow();
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
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#BasicFlow()}
   * .
   */
  @Test
  public final void testBasicFlow() {
    target = new BasicFlow();
    assertThat(target, is(instanceOf(BasicFlow.class)));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#BasicFlow(java.lang.String)}
   * .
   */
  @Test
  public final void testBasicFlowString() {
    target = new BasicFlow("flow_id");
    assertThat(target, is(instanceOf(BasicFlow.class)));
    assertThat((String) Whitebox.getInternalState(target, "flowId"),
        is("flow_id"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#BasicFlow(java.lang.String, java.lang.String, boolean, java.lang.String)}
   * .
   */
  @Test
  public final void testBasicFlowStringStringBooleanString() {
    target = new BasicFlow("flow_id", "owner", true, "1");
    assertThat(target, is(instanceOf(BasicFlow.class)));
    assertThat((String) Whitebox.getInternalState(target, "flowId"),
        is("flow_id"));
    assertThat((String) Whitebox.getInternalState(target, "owner"),
        is("owner"));
    assertThat((boolean) Whitebox.getInternalState(target, "enabled"),
        is(true));
    assertThat((String) Whitebox.getInternalState(target, "priority"),
        is("1"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#BasicFlow(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.Map, java.util.Map)}
   * .
   */
  @Test
  public final void testBasicFlowStringStringStringBooleanStringMore() {

    List<BasicFlowMatch> matches = null;
    List<String> path = null;
    Map<String, List<FlowAction>> edgeAction = null;
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);
    assertThat(target, is(instanceOf(BasicFlow.class)));
    assertThat((Integer) Whitebox.getInternalState(target, "version"),
        is(1));
    assertThat((String) Whitebox.getInternalState(target, "flowId"),
        is("flow_id"));
    assertThat((String) Whitebox.getInternalState(target, "owner"),
        is("owner"));
    assertThat((boolean) Whitebox.getInternalState(target, "enabled"),
        is(true));
    assertThat((String) Whitebox.getInternalState(target, "priority"),
        is("1"));
    assertThat((String) Whitebox.getInternalState(target, "status"),
        is("established"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#BasicFlow(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.Map, java.util.Map)}
   * .
   */
  @Test
  public final void testBasicFlowWithParameter() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<String, List<FlowAction>>();
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);
    assertThat(target, is(instanceOf(BasicFlow.class)));
    assertThat((Integer) Whitebox.getInternalState(target, "version"),
        is(1));
    assertThat((String) Whitebox.getInternalState(target, "flowId"),
        is("flow_id"));
    assertThat((String) Whitebox.getInternalState(target, "owner"),
        is("owner"));
    assertThat((boolean) Whitebox.getInternalState(target, "enabled"),
        is(true));
    assertThat((String) Whitebox.getInternalState(target, "priority"),
        is("1"));
    assertThat((String) Whitebox.getInternalState(target, "status"),
        is("established"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#validate()}
   * .
   */
  @Test
  public final void testValidate() {
    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#validate()}
   * .
   */
  @Test
  public final void testValidateMatchErr() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("", "port02");
    matches.add(match1);
    matches.add(match2);

    target.putMatches(matches);

    assertThat(target.validate(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#validate()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testValidateActionErr() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    target.addEdgeAction("node1", actions.get(0));

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#validate()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testValidateMatchSuccess() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);

    target.putMatches(matches);

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port01";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port02";
          }
        });
      }
    };

    target.addEdgeAction("node01", actions.get(0));
    target.addEdgeAction("node02", actions.get(1));

    assertThat(target.validate(), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is("BasicFlow"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#getMatches()}
   * .
   */
  @Test
  public final void testGetMatches() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);

    target.addMatch(match1);
    target.addMatch(match2);

    assertThat(target.getMatches(), is(matches));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#addMatch(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testAddMatch() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    matches.add(match1);

    assertThat(target.addMatch(match1), is(true));
    assertThat(target.getMatches(), is(matches));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#putMatches(java.util.List)}
   * .
   */
  @Test
  public final void testPutMatches() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);

    target.putMatches(matches);

    assertThat(target.getMatches(), is(matches));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removeMatch(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testRemoveMatch() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);
    List<BasicFlowMatch> after = new ArrayList<BasicFlowMatch>();
    after.add(match2);

    target.putMatches(matches);

    assertThat(target.removeMatch(match1), is(true));
    assertThat(target.getMatches(), is(after));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#getPath()}
   * .
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testGetPath() {

    List<String> path = new ArrayList();
    path.add("Path");

    target.putPath(path);

    assertThat(target.getPath(), is(path));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#putPath(java.util.List)}
   * .
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testPutPath() {
    List<String> path = new ArrayList();
    path.add("Path");
    path.add("path");
    target.putPath(path);
    assertThat(target.getPath(), is(path));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#addPath(java.lang.String)}
   * .
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testAddPath() {

    List<String> path = new ArrayList();
    path.add("link");

    assertThat(target.addPath("link"), is(true));
    assertThat(target.getPath(), is(path));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removePath(java.lang.String)}
   * .
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testRemovePath() {

    List<String> path = new ArrayList();
    path.add("link");
    path.add("path");
    target.putPath(path);
    List<String> after = new ArrayList();
    after.add("path");

    assertThat(target.removePath("link"), is(true));
    assertThat(target.getPath(), is(after));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#getEdgeActions()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetEdgeActions() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);

    target.addEdgeAction("node1", actions.get(0));
    target.addEdgeAction("node1", actions.get(1));

    assertThat(target.getEdgeActions(), is(edgeActions));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#getEdgeActions(java.lang.String)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testGetEdgeActionsString() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    target.addEdgeAction("node1", actions.get(0));
    target.addEdgeAction("node1", actions.get(1));
    target.addEdgeAction("node2", actions.get(0));
    target.addEdgeAction("node2", actions.get(1));

    assertThat(target.getEdgeActions("node2"), is(actions));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#putEdgeActions(java.util.Map)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testPutEdgeActions() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);

    target.putEdgeActions(edgeActions);

    assertThat(target.getEdgeActions("node1"), is(actions));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#addEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testAddEdgeAction() {
    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    assertThat(target.addEdgeAction("node1", actions.get(0)), is(true));
    assertThat(target.addEdgeAction("node1", actions.get(1)), is(true));
    assertThat(target.getEdgeActions("node1"), is(actions));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#addEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testAddEdgeActionNull() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();
    edgeActions.put("node2", actions);
    target.putEdgeActions(edgeActions);

    assertThat(target.getEdgeActions("node1"), is(nullValue()));
    assertThat(target.addEdgeAction("node1", actions.get(0)), is(true));

    List<FlowAction> after = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
      }
    };

    assertThat(target.getEdgeActions("node1"), is(after));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removeEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testRemoveEdgeAction() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);
    edgeActions.put("node2", actions);

    target.putEdgeActions(edgeActions);

    FlowActionOutput action = new FlowActionOutput("port1");

    assertThat(target.removeEdgeAction("node1", action), is(true));

    List<FlowAction> actions2 = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    assertThat(target.getEdgeActions("node1"), is(actions2));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removeEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testRemoveEdgeActionNull() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node2", actions);

    target.putEdgeActions(edgeActions);

    FlowActionOutput action = new FlowActionOutput("port1");

    assertThat(target.removeEdgeAction("node1", action), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removeEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testRemoveEdgeActionNotExist() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    List<FlowAction> actions2 = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions2);
    edgeActions.put("node2", actions);

    target.putEdgeActions(edgeActions);

    FlowActionOutput action = new FlowActionOutput("port1");

    assertThat(target.removeEdgeAction("node1", action), is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#removeEdgeAction(java.lang.String, org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testRemoveEdgeActionEmpty() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };
    List<FlowAction> actions2 = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node2", actions);
    edgeActions.put("node1", actions2);

    target.putEdgeActions(edgeActions);

    FlowActionOutput action = new FlowActionOutput("port2");

    assertThat(target.removeEdgeAction("node1", action), is(true));
    assertThat(target.getEdgeActions("node1"), is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#deleteMatch(org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch)}
   * .
   */
  @Test
  public final void testDeleteMatch() {
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);
    List<BasicFlowMatch> after = new ArrayList<BasicFlowMatch>();
    after.add(match2);

    target.putMatches(matches);

    assertThat(target.deleteMatch(match1), is(true));

    assertThat(target.getMatches(), is(after));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#deletePath(java.lang.String)}
   * .
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public final void testDeletePath() {
    List<String> path = new ArrayList();
    path.add("link");
    path.add("path");
    target.putPath(path);
    List<String> after = new ArrayList();
    after.add("path");

    assertThat(target.deletePath("link"), is(true));
    assertThat(target.getPath(), is(after));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#deleteActions(java.lang.String)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testDeleteActions() {

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);
    edgeActions.put("node2", actions);

    target.putEdgeActions(edgeActions);

    assertThat(target.deleteActions("node1"), is(true));

    Map<String, List<FlowAction>> afterEdgeActions = new HashMap<String, List<FlowAction>>();

    afterEdgeActions.put("node2", actions);

    assertThat(target.getEdgeActions(), is(afterEdgeActions));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {

    Value value = null;

    target = PowerMockito.spy(new BasicFlow() {
      @Override
      public boolean readValue(Value value) {
        return false;
      }
    });

    assertThat(target.readValue(value), is(false));
    verify(target, times(1)).readValue(value);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectNull() {
    assertThat(target.equals(null), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectThis() {
    assertThat(target.equals(target), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectWithDifferentType() {

    FlowActionOutput obj = new FlowActionOutput();
    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectSuperErr() {

    BasicFlow obj = Mockito.spy(new BasicFlow());
    when(obj.getType()).thenReturn("aaa");

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @SuppressWarnings({ "rawtypes", "serial", "unchecked" })
  @Test
  public final void testEqualsObjectPathErr() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<String, List<FlowAction>>();
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    BasicFlow obj = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    List<String> path1 = new ArrayList() {
      {
        add("path1");

      }
    };
    List<String> path2 = new ArrayList() {
      {
        add("path2");

      }
    };
    target.putPath(path1);
    obj.putPath(path2);

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectMatchesErr() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<String, List<FlowAction>>();
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    List<BasicFlowMatch> matches1 = new ArrayList<BasicFlowMatch>();
    List<BasicFlowMatch> matches2 = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node1", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node2", "port02");
    matches1.add(match1);
    matches2.add(match2);
    matches1.add(match1);
    matches2.add(match2);
    target.putMatches(matches1);

    BasicFlow obj = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    obj.putMatches(matches2);

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testEqualsObjectEdgeActionErr() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<String, List<FlowAction>>();
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions1 = new HashMap<String, List<FlowAction>>();
    Map<String, List<FlowAction>> edgeActions2 = new HashMap<String, List<FlowAction>>();

    edgeActions1.put("node1", actions);
    edgeActions2.put("node2", actions);

    target.putEdgeActions(edgeActions1);

    BasicFlow obj = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    obj.putEdgeActions(edgeActions2);

    assertThat(target.equals(obj), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#equals(java.lang.Object)}
   * .
   */
  @Test
  public final void testEqualsObjectSuccess() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    List<String> path = new ArrayList<String>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<String, List<FlowAction>>();
    Map<String, String> flowAttributes = new HashMap<String, String>();
    target = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    BasicFlow obj = new BasicFlow("1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    assertThat(target.equals(obj), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#writeValueSub(java.util.Map)}
   * .
   */
  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  @Test
  public final void testWriteValueSub() {

    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);
    target.addMatch(match1);
    target.addMatch(match2);

    List<String> path = new ArrayList();
    path.add("Path");
    path.add("path");
    target.putPath(path);

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);

    target.addEdgeAction("node1", actions.get(0));
    target.addEdgeAction("node1", actions.get(1));

    Map<String, Value> values = new HashMap<String, Value>();

    assertThat(values.get("matches"), is(nullValue()));
    assertThat(values.get("path"), is(nullValue()));
    assertThat(values.get("edge_actions"), is(nullValue()));

    assertThat(target.writeValueSub(values), is(true));
    assertThat(values.get("matches"), is(notNullValue()));
    assertThat(values.get("path"), is(notNullValue()));
    assertThat(values.get("edge_actions"), is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#clone()}
   * .
   */
  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  @Test
  public final void testClone() {
    List<BasicFlowMatch> matches = new ArrayList<BasicFlowMatch>();
    BasicFlowMatch match1 = new BasicFlowMatch("node01", "port01");
    BasicFlowMatch match2 = new BasicFlowMatch("node02", "port02");
    matches.add(match1);
    matches.add(match2);
    target.addMatch(match1);
    target.addMatch(match2);

    List<String> path = new ArrayList();
    path.add("Path");
    path.add("path");
    target.putPath(path);

    List<FlowAction> actions = new ArrayList<FlowAction>() {
      {
        add(new FlowActionOutput() {
          {
            output = "port1";
          }
        });
        add(new FlowActionOutput() {
          {
            output = "port2";
          }
        });
      }
    };

    Map<String, List<FlowAction>> edgeActions = new HashMap<String, List<FlowAction>>();

    edgeActions.put("node1", actions);

    target.addEdgeAction("node1", actions.get(0));
    target.addEdgeAction("node1", actions.get(1));

    BasicFlow result = target.clone();

    assertThat(result.matches, is(target.getMatches()));
    assertThat(result.path, is(target.getPath()));
    assertThat(result.edgeActions, is(target.getEdgeActions()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.basic.BasicFlow#clone()}
   * .
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    List<BasicFlowMatch> matches = new ArrayList<>();
    List<String> path = new ArrayList<>();
    Map<String, List<FlowAction>> edgeAction = new HashMap<>();
    Map<String, String> flowAttributes = new HashMap<>();
    target = new BasicFlow(
        "1",
        "flow_id",
        "owner",
        true,
        "1",
        "established",
        matches,
        path,
        edgeAction,
        flowAttributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[version=1",
        "flowId=flow_id",
        "owner=owner",
        "enabled=true",
        "priority=1",
        "status=established",
        "matches=[]",
        "path=[]",
        "edgeActions={}",
        "attributes={}]"
    }, ",");

    assertThat(result.endsWith(expectedString), is(true));

  }

}
