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

package org.o3project.odenos.component.linklayerizer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test class for LinkLayerizerBoundaryTable.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UUID.class })
@PowerMockIgnore({"javax.management.*"})
public class LinkLayerizerBoundaryTableTest {

  private LinkLayerizerBoundaryTable target;

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

    target = Mockito.spy(new LinkLayerizerBoundaryTable());

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#LinkLayerizerBoundaryTable()}.
   */
  @Test
  public void testLinkLayerizerBoundaryTable() {

    /*
     * test
     */
    LinkLayerizerBoundaryTable result = new LinkLayerizerBoundaryTable();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    assertThat(result.getBoundaries().size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#getBoundaries()}.
   */
  @Test
  public void testGetBoundaries() {

    /*
     * test
     */
    Map<String, LinkLayerizerBoundary> result = target.getBoundaries();

    /*
     * check
     */
    assertThat(result.size(), is(0));

    Map<String, LinkLayerizerBoundary> boundaries = Whitebox
        .getInternalState(target, "boundaries");
    assertThat(result, is(boundaries));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#setBoundaries(java.util.Map)}.
   */
  @Test
  public void testSetBoundaries() {

    /*
     * setting
     */
    Map<String, LinkLayerizerBoundary> settingBoundaries = new HashMap<>();
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");
    settingBoundaries.put("Id1", boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");
    settingBoundaries.put("Id2", boundary2);

    /*
     * test
     */
    target.setBoundaries(settingBoundaries);

    /*
     * check
     */
    assertThat(target.getBoundaries(), is(settingBoundaries));

    Map<String, LinkLayerizerBoundary> result = Whitebox.getInternalState(
        target, "boundaries");
    assertThat(result, is(settingBoundaries));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#addEntry(org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testAddEntry() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");

    /*
     * test
     */
    LinkLayerizerBoundary result1 = target.addEntry(boundary1);
    LinkLayerizerBoundary result2 = target.addEntry(boundary2);

    /*
     * check
     */
    assertThat(result1, is(boundary1));
    assertThat(result2, is(boundary2));

    assertThat(target.getBoundaries().size(), is(2));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#updateEntry(java.lang.String, org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testUpdateEntry() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary11 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw11", "LowerNwNode11", "LowerNwPort11",
        "UpperNw11", "UpperNwNode11", "UpperNwPort11");
    target.addEntry(boundary11);

    LinkLayerizerBoundary boundary12 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw12", "LowerNwNode12", "LowerNwPort12",
        "UpperNw12", "UpperNwNode12", "UpperNwPort12");

    /*
     * test
     */
    LinkLayerizerBoundary result = target.updateEntry("Id1", boundary12);

    /*
     * check
     */
    assertThat(result, is(boundary12));

    assertThat(target.getEntry("Id1"), is(boundary12));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#updateEntry(java.lang.String, org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundary)}.
   * @throws Exception
   */
  @Test
  public void testUpdateEntry_DifferId() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary11 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw11", "LowerNwNode11", "LowerNwPort11",
        "UpperNw11", "UpperNwNode11", "UpperNwPort11");
    target.addEntry(boundary11);

    LinkLayerizerBoundary boundary12 = new LinkLayerizerBoundary(
        "Id2", "Type1",
        "LowerNw12", "LowerNwNode12", "LowerNwPort12",
        "UpperNw12", "UpperNwNode12", "UpperNwPort12");

    /*
     * test
     */
    LinkLayerizerBoundary result = target.updateEntry("Id1", boundary12);

    /*
     * check
     */
    assertThat(result, is(boundary12));

    LinkLayerizerBoundary expectedBoundary = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw12", "LowerNwNode12", "LowerNwPort12",
        "UpperNw12", "UpperNwNode12", "UpperNwPort12");

    assertThat(target.getEntry("Id1"), is(expectedBoundary));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#getEntry(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetEntry() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");
    target.updateEntry("Id1", boundary1);

    /*
     * test
     */
    LinkLayerizerBoundary result = target.getEntry("Id1");

    /*
     * check
     */
    assertThat(result, is(boundary1));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#getBoundary(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetBoundary() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");
    target.addEntry(boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");
    target.addEntry(boundary2);

    LinkLayerizerBoundary boundary3 = new LinkLayerizerBoundary(
        "Id3", "Type3",
        "LowerNw3", "LowerNwNode3", "LowerNwPort3",
        "UpperNw3", "UpperNwNode3", "UpperNwPort3");
    target.addEntry(boundary3);

    /*
     * test
     */
    LinkLayerizerBoundary result = target.getBoundary("LowerNw2",
        "LowerNwNode2", "LowerNwPort2");

    /*
     * check
     */
    assertThat(result, is(boundary2));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#deleteEntry(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDeleteEntry() throws Exception {

    /*
     * setting
     */
    LinkLayerizerBoundary boundary1 = new LinkLayerizerBoundary(
        "Id1", "Type1",
        "LowerNw1", "LowerNwNode1", "LowerNwPort1",
        "UpperNw1", "UpperNwNode1", "UpperNwPort1");
    target.addEntry("Id1", boundary1);

    LinkLayerizerBoundary boundary2 = new LinkLayerizerBoundary(
        "Id2", "Type2",
        "LowerNw2", "LowerNwNode2", "LowerNwPort2",
        "UpperNw2", "UpperNwNode2", "UpperNwPort2");
    target.addEntry("Id2", boundary2);

    LinkLayerizerBoundary boundary3 = new LinkLayerizerBoundary(
        "Id3", "Type3",
        "LowerNw3", "LowerNwNode3", "LowerNwPort3",
        "UpperNw3", "UpperNwNode3", "UpperNwPort3");
    target.addEntry("Id3", boundary3);

    /*
     * test
     */
    LinkLayerizerBoundary result = target.deleteEntry("Id2");

    /*
     * check
     */
    assertThat(result, is(boundary2));

    assertThat(target.getBoundaries().containsKey("Id2"), is(false));

  }

  /*
   * ========================================
   * private method
   * ========================================
   */

  /**
   * Test method for {@link org.o3project.odenos.component.linklayerizer.LinkLayerizerBoundaryTable#getUniqueId()}.
   * @throws Exception
   */
  @Test
  public void testGetUniqueId() throws Exception {

    /*
     * setting
     */
    PowerMockito.mockStatic(UUID.class);

    PowerMockito.when(UUID.randomUUID())
        .thenReturn(new UUID(1, 1))
        /*
         * return
         * 00000000-0000-0001-0000-000000000001
         */

        .thenReturn(new UUID(1, 1))
        /*
         * return
         * 00000000-0000-0001-0000-000000000001
         */

        .thenCallRealMethod();
    /*
     * return
     * (random UUID value)
     */

    /*
     * test
     */
    String result = Whitebox.invokeMethod(target, "getUniqueId");

    /*
     * check
     */
    PowerMockito.verifyStatic(times(3));

    assertThat(result, is(notNullValue()));
    assertThat(result, is(not("00000000-0000-0001-0000-000000000001")));

  }

}
