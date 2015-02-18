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

package org.o3project.odenos.core.component.network.packet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.Map;

/**
 * Test class for OutPacketQuery.
 *
 * 
 *
 */
public class OutPacketQueryTest {

  private OutPacketQuery target;

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
    String queriesString = "123\"456\"789";
    target = spy(new OutPacketQuery(queriesString));
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
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketQuery#OutPacketQuery(java.lang.String)}
   * .
   */
  @Test
  public final void testOutPacketQuery() {
    /*
     * test
     */
    target = new OutPacketQuery("123\"456\"789");

    /*
     * check
     */
    String result = (String) WhiteboxImpl.getInternalState(target, "queriesString");
    assertThat(result, is("123456789"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketQuery#parse()}
   * .
   */
  @Test
  public final void testParse() {
    /*
     * set
     */

    String queriesString = "attributes=\"key1=value1,key2=value2\"";
    OutPacketQuery target = new OutPacketQuery(queriesString);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertNotSame(attributes.size(), 0);
    assertSame(queries.size(), 0);
    assertThat(result, is(true));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketQuery#parse()}
   * .
   */
  @Test
  public final void testParseErr() {
    /*
     * set
     */

    OutPacketQuery target = new OutPacketQuery(null);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertSame(queries.size(), 0);
    assertNull(attributes);
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketQuery#parse()}
   * .
   */
  @Test
  public final void testParseQueriesErr() {
    /*
     * set
     */

    String queriesString = "port=\"key1=value1,key2=value2\"";
    OutPacketQuery target = new OutPacketQuery(queriesString);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertNull(attributes);
    assertNotSame(queries.size(), 0);
    assertThat(result, is(false));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OutPacketQuery#parse()}
   * .
   */
  @Test
  public final void testParseAttributesNull() {
    /*
     * set
     */

    String queriesString = "attributes";
    OutPacketQuery target = new OutPacketQuery(queriesString);

    /*
     * test
     */
    boolean result = target.parse();

    /*
     * check
     */
    Map<String, String> attributes = WhiteboxImpl.getInternalState(target, "attributes");
    Map<String, String> queries = WhiteboxImpl.getInternalState(target, "queries");
    assertSame(queries.size(), 0);
    assertThat(attributes, is(nullValue()));
    assertThat(result, is(false));
  }

}
