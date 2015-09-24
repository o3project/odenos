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

package org.o3project.odenos.component.federator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;

import org.apache.commons.collections15.Bag;
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

import java.util.Map;
import java.util.UUID;

/**
 * Test class for FederatorBoundaryTable.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UUID.class })
@PowerMockIgnore({"javax.management.*"})
public class FederatorBoundaryTableTest {

  private FederatorBoundaryTable target;

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

    target = Mockito.spy(new FederatorBoundaryTable());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#FederatorBoundaryTable()}.
   */
  @Test
  public void testFederatorBoundaryTable() {

    /*
     * test
     */
    FederatorBoundaryTable result = new FederatorBoundaryTable();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    Map<String, FederatorBoundary> boundaries = Whitebox.getInternalState(
        target, "boundaries");
    assertThat(boundaries.size(), is(0));

    Bag<BoundaryPort> boundaryPorts = Whitebox.getInternalState(target,
        "boundaryPorts");
    assertThat(boundaryPorts.size(), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#addEntry(org.o3project.odenos.component.federator.FederatorBoundary)}.
   * @throws FederatorException
   */
  @Test
  public void testAddEntryFederatorBoundary() throws FederatorException {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network11", "Node11", "Port11", "Network21", "Node21",
        "Port21");
    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network12", "Node12", "Port12", "Network22", "Node22",
        "Port22");
    FederatorBoundary boundary3 = new FederatorBoundary("Id3", "Type3",
        "Network13", "Node13", "Port13", "Network23", "Node23",
        "Port23");

    /*
     * test
     */
    FederatorBoundary result1 = target.addEntry(boundary1);
    FederatorBoundary result2 = target.addEntry(boundary2);
    FederatorBoundary result3 = target.addEntry(boundary3);

    /*
     * check
     */
    assertThat(result1, is(boundary1));
    assertThat(result2, is(boundary2));
    assertThat(result3, is(boundary3));

    Map<String, FederatorBoundary> maps = Whitebox.getInternalState(target,
        "boundaries");
    assertThat(maps.size(), is(3));
    assertThat(result1.getId(), is(not("Id1")));
    assertThat(maps.get(result1.getId()), is(boundary1));
    assertThat(result2.getId(), is(not("Id2")));
    assertThat(maps.get(result2.getId()), is(boundary2));
    assertThat(result3.getId(), is(not("Id3")));
    assertThat(maps.get(result3.getId()), is(boundary3));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#addEntry(java.lang.String, org.o3project.odenos.component.federator.FederatorBoundary)}.
   * @throws FederatorException
   */
  @Test
  public void testAddEntryStringFederatorBoundary() throws FederatorException {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network11", "Node11", "Port11", "Network21", "Node21",
        "Port21");
    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network12", "Node12", "Port12", "Network22", "Node22",
        "Port22");
    FederatorBoundary boundary3 = new FederatorBoundary("Id3", "Type3",
        "Network13", "Node13", "Port13", "Network23", "Node23",
        "Port23");

    /*
     * test
     */
    FederatorBoundary result1 = target.addEntry("Id1", boundary1);
    FederatorBoundary result2 = target.addEntry("Id2", boundary2);
    FederatorBoundary result3 = target.addEntry("Id3", boundary3);

    /*
     * check
     */
    assertThat(result1, is(boundary1));
    assertThat(result2, is(boundary2));
    assertThat(result3, is(boundary3));

    Map<String, FederatorBoundary> maps = Whitebox.getInternalState(target,
        "boundaries");
    assertThat(maps.size(), is(3));
    assertThat(maps.get("Id1"), is(boundary1));
    assertThat(maps.get("Id2"), is(boundary2));
    assertThat(maps.get("Id3"), is(boundary3));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#addEntry(java.lang.String, org.o3project.odenos.component.federator.FederatorBoundary)}.
   * @throws FederatorException
   */
  @Test
  public void testAddEntry_Branch() throws FederatorException {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");

    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network1", "Node1", "Port1", "Network3", "Node3", "Port3");

    /*
     * test
     */
    FederatorBoundary result1 = target.addEntry("Id1", boundary1);
    FederatorBoundary result2 = target.addEntry("Id2", boundary2);

    /*
     * check
     */
    assertThat(result1, is(boundary1));
    assertThat(result2, is(boundary2));

    Map<String, FederatorBoundary> maps = Whitebox.getInternalState(target,
        "boundaries");
    assertThat(maps.size(), is(2));
    assertThat(maps.get("Id1"), is(boundary1));
    assertThat(maps.get("Id2"), is(boundary2));

    Bag<BoundaryPort> boundaryPorts = Whitebox.getInternalState(target,
        "boundaryPorts");
    BoundaryPort port1 = new BoundaryPort("Network1", "Node1", "Port1");
    BoundaryPort port2 = new BoundaryPort("Network2", "Node2", "Port2");
    BoundaryPort port3 = new BoundaryPort("Network3", "Node3", "Port3");

    assertThat(boundaryPorts.getCount(port1), is(2));
    assertThat(boundaryPorts.getCount(port2), is(1));
    assertThat(boundaryPorts.getCount(port3), is(1));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#getEntry(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetEntry() throws Exception {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network11", "Node11", "Port11", "Network21", "Node21",
        "Port21");
    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network12", "Node12", "Port12", "Network22", "Node22",
        "Port22");
    FederatorBoundary boundary3 = new FederatorBoundary("Id3", "Type3",
        "Network13", "Node13", "Port13", "Network23", "Node23",
        "Port23");
    target.addEntry("Id1", boundary1);
    target.addEntry("Id2", boundary2);
    target.addEntry("Id3", boundary3);

    /*
     * test
     */
    FederatorBoundary result = target.getEntry("Id2");

    /*
     * check
     */
    assertThat(result, is(boundary2));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#deleteEntry(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDeleteEntry() throws Exception {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network11", "Node11", "Port11", "Network21", "Node21",
        "Port21");
    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network12", "Node12", "Port12", "Network22", "Node22",
        "Port22");
    FederatorBoundary boundary3 = new FederatorBoundary("Id3", "Type3",
        "Network13", "Node13", "Port13", "Network23", "Node23",
        "Port23");
    target.addEntry("Id1", boundary1);
    target.addEntry("Id2", boundary2);
    target.addEntry("Id3", boundary3);

    /*
     * test
     */
    FederatorBoundary result = target.deleteEntry("Id2");

    /*
     * check
     */
    assertThat(result, is(boundary2));

    assertThat(target.isContains("Id2"), is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#deleteEntry(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDeleteEntry_Branch() throws Exception {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");

    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network1", "Node1", "Port1", "Network3", "Node3", "Port3");
    target.addEntry("Id1", boundary1);
    target.addEntry("Id2", boundary2);

    /*
     * test
     */
    FederatorBoundary result = target.deleteEntry("Id2");

    /*
     * check
     */
    assertThat(result, is(boundary2));

    assertThat(target.isContains("Id2"), is(false));

    Bag<BoundaryPort> boundaryPorts = Whitebox.getInternalState(target,
        "boundaryPorts");
    BoundaryPort port1 = new BoundaryPort("Network1", "Node1", "Port1");
    BoundaryPort port2 = new BoundaryPort("Network2", "Node2", "Port2");
    BoundaryPort port3 = new BoundaryPort("Network3", "Node3", "Port3");

    assertThat(boundaryPorts.getCount(port1), is(1));
    assertThat(boundaryPorts.getCount(port2), is(1));
    assertThat(boundaryPorts.getCount(port3), is(0));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#isContains(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsContains() throws Exception {

    /*
     * setting
     */
    FederatorBoundary boundary1 = new FederatorBoundary("Id1", "Type1",
        "Network11", "Node11", "Port11", "Network21", "Node21",
        "Port21");
    FederatorBoundary boundary2 = new FederatorBoundary("Id2", "Type2",
        "Network12", "Node12", "Port12", "Network22", "Node22",
        "Port22");
    FederatorBoundary boundary3 = new FederatorBoundary("Id3", "Type3",
        "Network13", "Node13", "Port13", "Network23", "Node23",
        "Port23");
    target.addEntry("Id1", boundary1);
    target.addEntry("Id2", boundary2);
    target.addEntry("Id3", boundary3);

    /*
     * test & check
     */

    /* 1st */
    boolean result1 = target.isContains("Id1");
    assertThat(result1, is(true));

    /* 2nd */
    boolean result2 = target.isContains("Id2");
    assertThat(result2, is(true));

    /* 3rd */
    boolean result3 = target.isContains("Id3");
    assertThat(result3, is(true));

    /* Nothing */
    boolean result0 = target.isContains("Id0");
    assertThat(result0, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#isContainsLink(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsContainsLink() throws Exception {

    /*
     * setting
     */
    FederatorBoundary setBoundary = new FederatorBoundary("Id", "Type",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");
    target.addEntry("Id", setBoundary);

    /*
     * test
     */
    boolean result1 = target.isContainsLink("Network1", "Node1", "Port1");
    boolean result2 = target.isContainsLink("Network2", "Node2", "Port2");

    boolean result0 = target.isContainsLink("Network", "Node", "Port");

    /*
     * check
     */
    assertThat(result1, is(true));
    assertThat(result2, is(true));

    assertThat(result0, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#isContainsLink(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsContainsLink_Branch() throws Exception {

    /*
     * setting
     */
    FederatorBoundary setBoundary1 = new FederatorBoundary("Id1", "Type1",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");
    FederatorBoundary setBoundary2 = new FederatorBoundary("Id2", "Type2",
        "Network1", "Node1", "Port1", "Network3", "Node3", "Port3");
    FederatorBoundary setBoundary3 = new FederatorBoundary("Id3", "Type3",
        "Network1", "Node1", "Port1", "Network4", "Node4", "Port4");
    target.addEntry("Id1", setBoundary1);
    target.addEntry("Id2", setBoundary2);
    target.addEntry("Id3", setBoundary3);

    target.deleteEntry("Id2");

    /*
     * test & check
     */
    boolean result1 = target.isContainsLink("Network1", "Node1", "Port1");
    assertThat(result1, is(true));

    boolean result2 = target.isContainsLink("Network2", "Node2", "Port2");
    assertThat(result2, is(true));

    boolean result3 = target.isContainsLink("Network3", "Node3", "Port3");
    assertThat(result3, is(false));

    boolean result4 = target.isContainsLink("Network4", "Node4", "Port4");
    assertThat(result4, is(true));

    boolean result0 = target.isContainsLink("Network", "Node", "Port");
    assertThat(result0, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#isContainsLink(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testIsContainsLink2() throws Exception {

    /*
     * setting
     */
    FederatorBoundary setBoundary1 = new FederatorBoundary("Id1", "Type1",
        "Network1", "Node1", "Port1", "Network2", "Node2", "Port2");
    FederatorBoundary setBoundary2 = new FederatorBoundary("Id2", "Type2",
        "Network1", "Node1", "Port1", "Network3", "Node3", "Port3");
    target.addEntry("Id1", setBoundary1);
    target.addEntry("Id2", setBoundary2);

    /*
     * test & check
     */
    boolean result1 = target.isContainsLink("Network1", "Node1", "Port1");
    assertThat(result1, is(true));

    boolean result2 = target.isContainsLink("Network2", "Node2", "Port2");
    assertThat(result2, is(true));

    boolean result3 = target.isContainsLink("Network3", "Node3", "Port3");
    assertThat(result3, is(true));

    boolean result0 = target.isContainsLink("Network", "Node", "Port");
    assertThat(result0, is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#getBoundaries()}.
   */
  @Test
  public void testGetBoundaries() {

    /*
     * test
     */
    Map<String, FederatorBoundary> result = target.getBoundaries();

    /*
     * check
     */
    Map<String, FederatorBoundary> expect = Whitebox.getInternalState(
        target, "boundaries");

    assertThat(result, is(expect));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.federator.FederatorBoundaryTable#getUniqueId()}.
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
