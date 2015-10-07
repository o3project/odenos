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

package org.o3project.odenos.component.slicer;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Test class for SliceConditionTable.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UUID.class })
@PowerMockIgnore({"javax.management.*"})
public class SliceConditionTableTest {

  private SliceConditionTable target;

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

    target = Mockito.spy(new SliceConditionTable());

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#SliceConditionTable()}.
   */
  @Test
  public void testSliceConditionTable() {

    /*
     * test
     */
    SliceConditionTable target = new SliceConditionTable();

    /*
     * check
     */
    assertThat(target.getPriorityTables().isEmpty(), is(true));

    TreeMap<String, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    assertThat(priorityTables.isEmpty(), is(true));

    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");
    assertThat(sliceConditionObjects.isEmpty(), is(true));

    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");
    assertThat(connectionTables.isEmpty(), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#getPriorityTables()}.
   */
  @Test
  public void testGetPriorityTables() {

    /*
     * test
     */
    TreeMap<String, List<String>> result = target.getPriorityTables();

    /*
     * check
     */
    TreeMap<String, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    assertThat(result, is(priorityTables));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#getConditionIdList(java.lang.Integer)}.
   */
  @Test
  public void testGetConditionIdList() {

    /*
     * setting
     */
    TreeMap<String, List<String>> priorityTables = target
        .getPriorityTables();

    List<String> value1 = Arrays.asList(new String[] { "abc" });
    List<String> value2 = Arrays.asList(new String[] { "abc", "def" });
    List<String> value3 = Arrays
        .asList(new String[] { "abc", "def", "ghi" });

    priorityTables.put("1", value1);
    priorityTables.put("2", value2);
    priorityTables.put("3", value3);

    /*
     * test
     */
    List<String> result = target.getConditionIdList("2");

    /*
     * check
     */
    assertThat(result, is(value2));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#getSliceConditionObject(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetSliceConditionObject() throws Exception {

    /*
     * setting
     */
    Map<String, SliceCondition> sliceConditionObjects =
        new HashMap<String, SliceCondition>();

    SliceCondition sc = new BasicSliceCondition("Id", "Type", "Connection",
        "InNode", "InPort");
    sliceConditionObjects.put("ConditionId", sc);

    Whitebox.setInternalState(target, "sliceConditionObjects",
        sliceConditionObjects);

    /*
     * test
     */
    SliceCondition result = target.getSliceConditionObject("ConditionId");

    /*
     * check
     */
    assertThat(result, is(sc));
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#getSliceConditionIds(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetSliceConditionIds() throws Exception {

    /*
     * setting
     */
    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");

    List<String> value = Arrays
        .asList(new String[] { "abc", "def", "ghi" });
    connectionTables.put("ConditionId", value);

    /*
     * test
     */
    List<String> result1 = target.getSliceConditionIds("ConditionId");
    List<String> result2 = target.getSliceConditionIds("NothingId");

    /*
     * check
     */
    assertThat(result1, is(value));
    assertThat(result2.isEmpty(), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#getConnectionId(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testGetConnectionId() throws Exception {

    /*
     * setting
     */
    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");

    SliceCondition sc = new BasicSliceCondition("Id", "Type", "Connection",
        "InNode", "InPort");
    sliceConditionObjects.put("ConditionId", sc);

    /*
     * test
     */
    String result = target.getConnectionId("ConditionId");

    /*
     * check
     */
    assertThat(result, is("Connection"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#addEntryToPriorityTable(java.lang.Integer)}.
   */
  @Test
  public void testAddEntryToPriorityTable_NonExist() {

    /*
     * test
     */
    boolean result = target.addEntryToPriorityTable("5");

    /*
     * check
     */
    assertThat(result, is(true));

    List<String> resultList = target.getConditionIdList("5");
    assertThat(resultList.isEmpty(), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#addEntryToPriorityTable(java.lang.Integer)}.
   */
  @Test
  public void testAddEntryToPriorityTable_Exist() {

    /*
     * setting
     */
    TreeMap<String, List<String>> priorityTables = target
        .getPriorityTables();
    List<String> value = Arrays
        .asList(new String[] { "abc", "def", "ghi" });
    priorityTables.put("5", value);

    /*
     * test
     */
    boolean result = target.addEntryToPriorityTable("5");

    /*
     * check
     */
    assertThat(result, is(false));

    List<String> resultList = target.getConditionIdList("5");
    assertThat(resultList, is(value));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#addEntryToSliceCondition(java.lang.String, org.o3project.odenos.component.slicer.SliceCondition)}.
   * @throws Exception
   */
  @Test
  public void testAddEntryToSliceConditionStringSliceCondition() throws Exception {

    /*
     * test
     */
    SliceCondition sc = new BasicSliceCondition("Id", "Type", "Connection",
        "InNode", "InPort");
    target.addEntryToSliceCondition("5", sc);

    /*
     * check
     */
    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");

    assertThat(sliceConditionObjects.size(), is(1));
    String conditionId = sliceConditionObjects.keySet().iterator().next();
    assertThat(conditionId, is(notNullValue()));

    assertThat(sliceConditionObjects.get(conditionId), is(sc));

    TreeMap<Integer, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    List<String> priorityTable = priorityTables.get("5");
    assertThat(priorityTable.size(), is(1));
    assertThat(priorityTable.contains(conditionId), is(true));

    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");
    List<String> connectionTable = connectionTables.get("Connection");
    assertThat(connectionTable.size(), is(1));
    assertThat(connectionTable.contains(conditionId), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#addEntryToSliceCondition(java.lang.String, java.lang.String, org.o3project.odenos.component.slicer.SliceCondition)}.
   * @throws Exception
   */
  @Test
  public void testAddEntryToSliceConditionStringStringSliceCondition() throws Exception {

    /*
     * test
     */
    SliceCondition sc = new BasicSliceCondition("Id", "Type", "Connection",
        "InNode", "InPort");
    target.addEntryToSliceCondition("5", "ConditionId", sc);

    /*
     * check
     */
    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");
    assertThat(sliceConditionObjects.size(), is(1));
    assertThat(sliceConditionObjects.get("ConditionId"), is(sc));

    TreeMap<Integer, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    List<String> priorityTable = priorityTables.get("5");
    assertThat(priorityTable.size(), is(1));
    assertThat(priorityTable.contains("ConditionId"), is(true));

    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");
    List<String> connectionTable = connectionTables.get("Connection");
    assertThat(connectionTable.size(), is(1));
    assertThat(connectionTable.contains("ConditionId"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#addEntryToSliceCondition(java.lang.Integer, org.o3project.odenos.component.slicer.SliceCondition)}.
   * @throws Exception
   */
  @Test
  public void testAddEntryToSliceCondition_2ndAdd() throws Exception {

    /*
     * setting
     */
    SliceCondition setting = new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort");
    target.addEntryToSliceCondition("5", "ConditionId", setting);

    /*
     * test
     */
    SliceCondition sc = new BasicSliceCondition("NewId", "NewType",
        "NewConnection", "NewInNode", "NewInPort");
    target.addEntryToSliceCondition("5", "NewConditionId", sc);

    /*
     * check
     */
    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");

    assertThat(sliceConditionObjects.size(), is(2));
    assertThat(sliceConditionObjects.get("ConditionId"), is(setting));
    assertThat(sliceConditionObjects.get("NewConditionId"), is(sc));

    TreeMap<String, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    List<String> priorityTable = priorityTables.get("5");
    assertThat(priorityTable.size(), is(2));
    assertThat(priorityTable.contains("ConditionId"), is(true));
    assertThat(priorityTable.contains("NewConditionId"), is(true));

    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");

    List<String> connectionTable1 = connectionTables.get("Connection");
    assertThat(connectionTable1.size(), is(1));
    assertThat(connectionTable1.contains("ConditionId"), is(true));
    List<String> connectionTable2 = connectionTables.get("NewConnection");
    assertThat(connectionTable2.size(), is(1));
    assertThat(connectionTable2.contains("NewConditionId"), is(true));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#deleteSliceCondition(java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testDeleteSliceCondition() throws Exception {

    /*
     * setting
     */
    SliceCondition setting = new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort");
    target.addEntryToSliceCondition("5", "ConditionId", setting);

    SliceCondition sc = new BasicSliceCondition("NewId", "NewType",
        "NewConnection", "NewInNode", "NewInPort");
    target.addEntryToSliceCondition("5", "NewConditionId", sc);

    /*
     * test
     */
    target.deleteSliceCondition("ConditionId");

    /*
     * check
     */
    TreeMap<String, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    List<String> priorityTable = priorityTables.get("5");
    assertThat(priorityTable.size(), is(1));
    assertThat(priorityTable.contains("NewConditionId"), is(true));

    Map<String, List<String>> connectionTables =
        Whitebox.getInternalState(target, "connectionTables");

    List<String> connectionTable1 = connectionTables.get("Connection");
    assertThat(connectionTable1.size(), is(0));

    List<String> connectionTable2 = connectionTables.get("NewConnection");
    assertThat(connectionTable2.size(), is(1));
    assertThat(connectionTable2.contains("NewConditionId"), is(true));

    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");
    assertThat(sliceConditionObjects.containsKey("ConditionId"), is(false));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#deleteSliceConditionTable(java.lang.Integer)}.
   * @throws Exception
   */
  @Test
  public void testDeleteSliceConditionTable() throws Exception {

    /*
     * setting
     */
    SliceCondition setting = new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort");
    target.addEntryToSliceCondition("5", setting);

    SliceCondition sc = new BasicSliceCondition("NewId", "NewType",
        "NewConnection", "NewInNode", "NewInPort");
    target.addEntryToSliceCondition("5", sc);

    /*
     * test
     */
    target.deleteSliceConditionTable("5");

    /*
     * check
     */
    TreeMap<String, List<String>> priorityTables =
        Whitebox.getInternalState(target, "priorityTables");
    //        List<String> priorityTable = priorityTables.get("5");
    //        assertThat(priorityTable.size(), is(0));
    assertThat(priorityTables.containsKey("5"), is(false));

    Map<String, SliceCondition> sliceConditionObjects =
        Whitebox.getInternalState(target, "sliceConditionObjects");
    assertThat(sliceConditionObjects.containsKey("Id"), is(false));
    assertThat(sliceConditionObjects.containsKey("NewId"), is(false));

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

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.SliceConditionTable#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * test
     */
    String result = target.toString();

    assertThat(
        result.endsWith("[priorityTables={},connectionTables={},sliceConditionObjects={}]"),
        is(true));

  }

}
