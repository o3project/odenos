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

package org.o3project.odenos.core.component.network.flow.query;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.o3project.odenos.core.component.network.flow.basic.FlowAction;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowActionSetField;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;
import java.util.Map;

public class OFPFlowActionSetFieldQueryTest {

  private OFPFlowActionSetFieldQuery target;
  private Map<String, String> actions;

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
    actions = new HashMap<String, String>();
    target = new OFPFlowActionSetFieldQuery(actions);
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    actions = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#OFPFlowActionSetFieldQuery(java.util.Map)}
   * .
   */
  @Test
  public final void testOFPFlowActionSetFieldQuery() {
    actions = new HashMap<String, String>();
    target = new OFPFlowActionSetFieldQuery(actions);

    assertThat(target, is(instanceOf(OFPFlowActionSetFieldQuery.class)));

    OFPFlowMatch match = (OFPFlowMatch) WhiteboxImpl.getInternalState(target, "match");
    assertThat(match, is(OFPFlowMatch.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_src", "ethSrc");
        put("eth_dst", "ethDst");
        put("eth_type", "11");
        put("vlan_vid", "12");
        put("vlan_pcp", "13");
        put("ipv4_src", "ipv4Src");
        put("ipv4_dst", "ipv4Dst");
      }
    };
    target = new OFPFlowActionSetFieldQuery(actions);
    assertThat(target.parse(), is(true));

    OFPFlowMatch match = (OFPFlowMatch) WhiteboxImpl.getInternalState(target, "match");
    assertThat(match.getEthSrc(), is("ethSrc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseSuperErr() {
    actions = new HashMap<String, String>() {
      {
        put("aaa", "bbb");
      }
    };
    target = new OFPFlowActionSetFieldQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseExactlyErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("vlan", "12");
      }
    };
    target = new OFPFlowActionSetFieldQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#parse()}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testParseNotIntErr() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("vlan_vid", "string");
      }
    };
    target = new OFPFlowActionSetFieldQuery(actions);
    assertThat(target.parse(), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlySuccess() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_src", "ethSrc");
        put("eth_dst", "ethDst");
        put("eth_type", "11");
        put("vlan_vid", "12");
        put("vlan_pcp", "13");
        put("ipv4_src", "ipv4Src");
        put("ipv4_dst", "ipv4Dst");
      }
    };

    target = new OFPFlowActionSetFieldQuery(actions);
    target.parse();
    OFPFlowMatch targetMatch = (OFPFlowMatch) WhiteboxImpl.getInternalState(target, "match");
    assertThat(targetMatch.getEthSrc(), is("ethSrc"));
    assertThat(targetMatch.getEthDst(), is("ethDst"));
    assertThat(targetMatch.getEthType(), is(new Integer(11)));
    assertThat(targetMatch.getVlanVid(), is(new Integer(12)));
    assertThat(targetMatch.getVlanPcp(), is(new Integer(13)));
    assertThat(targetMatch.getIpv4Src(), is("ipv4Src"));
    assertThat(targetMatch.getIpv4Dst(), is("ipv4Dst"));

    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthSrc("ethSrc");
    match.setEthDst("ethDst");
    match.setEthType(11);
    match.setVlanVid(12);
    match.setVlanPcp(13);
    match.setIpv4Src("ipv4Src");
    match.setIpv4Dst("ipv4Dst");
    OFPFlowActionSetField action = new OFPFlowActionSetField();
    action.setMatch(match);
    assertThat(target.matchExactly((FlowAction) action), is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyFalse() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("eth_src", "ethSrc");
        put("eth_dst", "ethDst");
        put("eth_type", "11");
        put("vlan_vid", "12");
        put("vlan_pcp", "13");
        put("ipv4_src", "ipv4Src");
        put("ipv4_dst", "ipv4Dst");
      }
    };

    target = new OFPFlowActionSetFieldQuery(actions);
    target.parse();
    OFPFlowMatch targetMatch = (OFPFlowMatch) WhiteboxImpl.getInternalState(target, "match");
    assertThat(targetMatch.getEthSrc(), is("ethSrc"));
    assertThat(targetMatch.getEthDst(), is("ethDst"));
    assertThat(targetMatch.getEthType(), is(new Integer(11)));
    assertThat(targetMatch.getVlanVid(), is(new Integer(12)));
    assertThat(targetMatch.getVlanPcp(), is(new Integer(13)));
    assertThat(targetMatch.getIpv4Src(), is("ipv4Src"));
    assertThat(targetMatch.getIpv4Dst(), is("ipv4Dst"));

    OFPFlowMatch match = new OFPFlowMatch();
    match.setEthSrc("ethSrc");
    match.setEthDst("ethDst");
    match.setEthType(11);
    match.setVlanVid(12);
    match.setVlanPcp(20); // bad value
    match.setIpv4Src("ipv4Src");
    match.setIpv4Dst("ipv4Dst");
    OFPFlowActionSetField action = new OFPFlowActionSetField();
    action.setMatch(match);
    assertThat(target.matchExactly((FlowAction) action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#matchExactly(org.o3project.odenos.core.component.network.flow.basic.FlowAction)}
   * .
   */
  @SuppressWarnings("serial")
  @Test
  public final void testMatchExactlyVlanNull() {
    actions = new HashMap<String, String>() {
      {
        put("type", "aaa");
        put("vlan_vid", "12");
      }
    };

    target = new OFPFlowActionSetFieldQuery(actions);

    OFPFlowActionSetField action = new OFPFlowActionSetField();

    assertThat(target.matchExactly((FlowAction) action), is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#compareField(boolean, boolean, java.lang.Object, java.lang.Object)}
   * .
   * @throws Exception throws Exception in targets
   */
  @Test
  public final void testCompareField() throws Exception {

    /*
     * setting
     */
    Object queryStr = "123";
    Object matchStr = "123";
    Object differStr = "999";

    Object queryLong = new Long(123);
    Object matchLong = new Long(123);
    Object differLong = new Long(999);

    Object queryInt = new Integer(123);
    Object matchInt = new Integer(123);
    Object differInt = new Integer(999);

    Object matchNull = null;

    /*
     * test
     */
    boolean resultStrStr =
        Whitebox.invokeMethod(target, "compareField", false, false, queryStr, matchStr);
    boolean resultStrDiff =
        Whitebox.invokeMethod(target, "compareField", false, false, queryStr, differStr);
    boolean resultStrLong =
        Whitebox.invokeMethod(target, "compareField", false, false, queryStr, matchLong);
    boolean resultStrInt =
        Whitebox.invokeMethod(target, "compareField", false, false, queryStr, matchInt);
    boolean resultStrNull00 =
        Whitebox.invokeMethod(target, "compareField", false, false, queryStr, matchNull);
    boolean resultStrNull01 =
        Whitebox.invokeMethod(target, "compareField", false, true, queryStr, matchNull);

    boolean resultLongLong =
        Whitebox.invokeMethod(target, "compareField", false, false, queryLong, matchLong);
    boolean resultLongDiff =
        Whitebox.invokeMethod(target, "compareField", false, false, queryLong, differLong);
    boolean resultLongStr =
        Whitebox.invokeMethod(target, "compareField", false, false, queryLong, matchStr);
    boolean resultLongInt =
        Whitebox.invokeMethod(target, "compareField", false, false, queryLong, matchInt);
    boolean resultLongNull00 =
        Whitebox.invokeMethod(target, "compareField", false, false, queryLong, matchNull);
    boolean resultLongNull01 =
        Whitebox.invokeMethod(target, "compareField", false, true, queryLong, matchNull);

    boolean resultIntInt =
        Whitebox.invokeMethod(target, "compareField", false, false, queryInt, matchInt);
    boolean resultIntDiff =
        Whitebox.invokeMethod(target, "compareField", false, false, queryInt, differInt);
    boolean resultIntStr =
        Whitebox.invokeMethod(target, "compareField", false, false, queryInt, matchStr);
    boolean resultIntLong =
        Whitebox.invokeMethod(target, "compareField", false, false, queryInt, matchLong);
    boolean resultIntNull00 =
        Whitebox.invokeMethod(target, "compareField", false, false, queryInt, matchNull);
    boolean resultIntNull01 =
        Whitebox.invokeMethod(target, "compareField", false, true, queryInt, matchNull);

    /*
     * check
     */
    assertThat(resultStrStr, is(true));
    assertThat(resultLongLong, is(true));
    assertThat(resultIntInt, is(true));

    assertThat(resultStrDiff, is(false));
    assertThat(resultStrLong, is(false));
    assertThat(resultStrInt, is(false));
    assertThat(resultStrNull00, is(false));
    assertThat(resultStrNull01, is(false));

    assertThat(resultLongDiff, is(false));
    assertThat(resultLongStr, is(false));
    assertThat(resultLongInt, is(false));
    assertThat(resultLongNull00, is(false));
    assertThat(resultLongNull01, is(false));

    assertThat(resultIntDiff, is(false));
    assertThat(resultIntStr, is(false));
    assertThat(resultIntLong, is(false));
    assertThat(resultIntNull00, is(false));
    assertThat(resultIntNull01, is(false));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.flow.query.OFPFlowActionSetFieldQuery#compareField(boolean, boolean, java.lang.Object, java.lang.Object)}
   * .
   * @throws Exception throws Exception in targets
   */
  @Test
  public final void testCompareFieldQueryNull() throws Exception {

    /*
     * setting
     */
    Object query = null;
    Object matchNull = null;
    Object matchString = "123";
    Object matchLong = new Long(123);
    Object matchInteger = new Integer(123);

    /*
     * test
     */
    boolean resultNull11 =
        Whitebox.invokeMethod(target, "compareField", true, true, query, matchNull);
    boolean resultNull10 =
        Whitebox.invokeMethod(target, "compareField", true, false, query, matchNull);
    boolean resultNull01 =
        Whitebox.invokeMethod(target, "compareField", false, true, query, matchNull);
    boolean resultNull00 =
        Whitebox.invokeMethod(target, "compareField", false, false, query, matchNull);

    boolean resultString10 =
        Whitebox.invokeMethod(target, "compareField", true, false, query, matchString);
    boolean resultString11 =
        Whitebox.invokeMethod(target, "compareField", true, true, query, matchString); //
    boolean resultString01 =
        Whitebox.invokeMethod(target, "compareField", false, true, query, matchString);
    boolean resultString00 =
        Whitebox.invokeMethod(target, "compareField", false, false, query, matchString);

    boolean resultLong10 =
        Whitebox.invokeMethod(target, "compareField", true, false, query, matchLong);
    boolean resultLong11 =
        Whitebox.invokeMethod(target, "compareField", true, true, query, matchLong); //
    boolean resultLong01 =
        Whitebox.invokeMethod(target, "compareField", false, true, query, matchLong);
    boolean resultLong00 =
        Whitebox.invokeMethod(target, "compareField", false, false, query, matchLong);

    boolean resultInteger10 =
        Whitebox.invokeMethod(target, "compareField", true, false, query, matchInteger);
    boolean resultInteger11 =
        Whitebox.invokeMethod(target, "compareField", true, true, query, matchInteger);
    boolean resultInteger01 =
        Whitebox.invokeMethod(target, "compareField", false, true, query, matchInteger);
    boolean resultInteger00 =
        Whitebox.invokeMethod(target, "compareField", false, false, query, matchInteger);

    /*
     * check
     */
    assertThat(resultNull11, is(true));
    assertThat(resultNull10, is(true));
    assertThat(resultNull01, is(false));
    assertThat(resultNull00, is(false));

    assertThat(resultString10, is(true));
    assertThat(resultString11, is(true));
    assertThat(resultString01, is(false));
    assertThat(resultString00, is(false));

    assertThat(resultLong10, is(true));
    assertThat(resultLong11, is(true));
    assertThat(resultLong01, is(false));
    assertThat(resultLong00, is(false));

    assertThat(resultInteger10, is(true));
    assertThat(resultInteger11, is(true));
    assertThat(resultInteger01, is(false));
    assertThat(resultInteger00, is(false));

  }

}
