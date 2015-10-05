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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * SliceConditionTable for managing the order of priority SliceCondition.
 *
 */
public class SliceConditionTable {

  private static final Logger log = LogManager.getLogger(SliceConditionTable.class);

  /**
   * Constructor.
   */
  public SliceConditionTable() {
  }

  /**
   * priority tables.
   * <pre>
   * {@literal
   * key :priority
   * value :conditionIdList ["10":["slice001","slice002"] "20":["slice003","slice004"] ..]
   * }
   * </pre>
   */
  private TreeMap<String, List<String>> priorityTables =
      new TreeMap<String, List<String>>();

  /**
   * map of condition.
   * <pre>
   * {@literal
   * key :conndition_id
   * value : Object SliceCondition.class ["Slicer1":<matchObject>, "Slice2":<match Object>...]
   * }
   * </pre>
   */
  private Map<String, SliceCondition> sliceConditionObjects =
      new HashMap<String, SliceCondition>();

  /**
   * key :connection_id value :conndition_id connections["c0":["slice001",
   * "slice002"], "c1":["slice003"] ..]
   */
  private Map<String, List<String>> connectionTables =
      new HashMap<String, List<String>>();

  // priorityTables
  /**
   * Returns a priority table.
   * @return priority table.
   */
  public TreeMap<String, List<String>>
      getPriorityTables() {
    return priorityTables;
  }

  /**
   * Returns list of string.
   * @param priority key of the priority table.
   * @return list of string.
   */
  public List<String>
      getConditionIdList(final String priority) {
    return priorityTables.get(priority);
  }

  // sliceConditionObjects
  /**
   * Returns a slice conditions.
   * @param conditionId condition ID.
   * @return slice conditions.
   */
  public SliceCondition
      getSliceConditionObject(final String conditionId) {
    return sliceConditionObjects.get(conditionId);
  }

  /**
   * Returns condition ids.
   * @param connectionId connection id.
   * @return list of condition id.
   */
  public List<String>
      getSliceConditionIds(final String connectionId) {
    List<String> conditionIds = connectionTables.get(connectionId);
    if (conditionIds == null) {
      conditionIds = new ArrayList<String>();
    }
    return conditionIds;
  }

  // connectionTables
  protected String
      getConnectionId(final String conditionId) {
    SliceCondition condition = sliceConditionObjects.get(conditionId);
    return condition.getConnection();
  }

  // Entry
  /**
   * add priority.
   * @param priority string of priority.
   * @return true if priority added.
   */
  public boolean addEntryToPriorityTable(final String priority) {

    if (priorityTables.containsKey(priority)) {
      return false;
    }
    ArrayList<String> conditionList = new ArrayList<String>();
    priorityTables.put(priority, conditionList);
    return true;
  }

  /**
   * add condition.
   * @param priority string of priority.
   * @param condition the condition to register
   */
  public void addEntryToSliceCondition(
      final String priority,
      SliceCondition condition) {

    final String conditionId = getUniqueId();
    condition.setId(conditionId);

    addEntryToSliceCondition(priority, conditionId, condition);
  }

  /**
   * add condition.
   * @param priority string of priority.
   * @param conditionId condition ID.
   * @param condition the condition to register.
   */
  public void addEntryToSliceCondition(
      final String priority,
      final String conditionId,
      final SliceCondition condition) {

    final String connectionId = condition.getConnection();
    if (!StringUtils.equals(conditionId, condition.getId())) {
      log.warn(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "set condition ID: {}", conditionId));
      condition.setId(connectionId);
    }

    // update sliceConditionObjects
    sliceConditionObjects.put(conditionId, condition);

    // update priorityTables
    List<String> conditionIds = priorityTables.get(priority);
    if (conditionIds == null) {
      conditionIds = new ArrayList<String>();
      priorityTables.put(priority, conditionIds);
    }
    conditionIds.add(conditionId);

    // update connectionTables
    List<String> connIds = connectionTables.get(connectionId);
    if (connIds == null) {
      connIds = new ArrayList<String>();
      connectionTables.put(connectionId, connIds);
    }
    connIds.add(conditionId);
  }

  // Delete
  /**
   * Delete a condition.
   * @param conditionId ID for condition.
   */
  public void
      deleteSliceCondition(final String conditionId) {
    for (List<String> conditionIds : priorityTables.values()) {
      conditionIds.remove(conditionId);
    }
    for (List<String> conditionIds : connectionTables.values()) {
      conditionIds.remove(conditionId);
    }
    sliceConditionObjects.remove(conditionId);
  }

  /**
   * Delete conditions.
   * @param priority string of priority (0 - 65535: max)
   */
  public void deleteSliceConditionTable(String priority) {

    if (priority == null) {
      throw new IllegalArgumentException("priority is null");
    }

    Map<String, List<String>> priorityTable = getPriorityTables();
    List<String> conditionIds = new ArrayList<String>(
        priorityTable.get(priority));

    for (String conditionId : conditionIds) {
      deleteSliceCondition(conditionId);
    }
    priorityTables.remove(priority);

  }

  /**
   * Method for assigned generating UUID to ComponentConnectionID.
   *
   * @return id ComponentConnectionID.
   */
  protected final String getUniqueId() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (connectionTables.containsKey(id));

    return id;
  }

  @Override
  public String toString() {

    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("priorityTables", priorityTables);
    sb.append("connectionTables", connectionTables);
    sb.append("sliceConditionObjects", sliceConditionObjects);

    return sb.toString();

  }

}
