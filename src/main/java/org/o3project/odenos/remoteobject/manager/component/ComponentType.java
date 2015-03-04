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

package org.o3project.odenos.remoteobject.manager.component;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.o3project.odenos.remoteobject.message.OdenosMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class ComponentType extends OdenosMessage implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(ComponentType.class);

  private String type = "";
  private String superType = "";
  private Map<String, String> connectionTypes = new HashMap<>();
  private String description = "";
  private List<String> cmId = new ArrayList<>();

  public ComponentType() {
  }

  public ComponentType(String type, String superType,
      Map<String, String> connectionTypes, String description) {
    this(type, superType, connectionTypes, description, null);
  }

  /**
   * Constructor.
   * @param type Object Type.
   * @param superType super class Object Type.
   * @param connectionTypes Component Connection Types.
   * @param description description.
   * @param cmId Component ID.
   */
  public ComponentType(String type, String superType,
      Map<String, String> connectionTypes, String description, List<String> cmId) {
    if (type != null) {
      this.type = type;
    }
    if (superType != null) {
      this.superType = superType;
    }
    if (connectionTypes != null) {
      this.connectionTypes = connectionTypes;
    }
    if (description != null) {
      this.description = description;
    }
    if (cmId != null) {
      this.cmId = cmId;
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSuperType() {
    return superType;
  }

  public void setSuperType(String superType) {
    this.superType = superType;
  }

  public Map<String, String> getConnectionTypes() {
    return connectionTypes;
  }

  /**
   * Set Connection Types
   * @param connectionTypes connection types.
   */
  public void setConnectionTypes(Map<String, String> connectionTypes) {
    if (connectionTypes != null) {
      this.connectionTypes = connectionTypes;
    }
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getCmId() {
    return cmId;
  }

  public void setCmId(List<String> cmId) {
    this.cmId = cmId;
  }

  public void addCmId(String cmId) {
    this.cmId.add(cmId);
  }

  public void delCmId(String cmId) {
    this.cmId.remove(cmId);
  }

  @Override
  public int hashCode() {
    StringBuffer sb = new StringBuffer();
    sb.append(type).append("::");
    sb.append(superType).append("::");
    sb.append(connectionTypes.toString()).append("::");
    sb.append(description).append("::");
    sb.append(cmId.toString());

    return sb.toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof ComponentType)) {
      return false;
    }

    ComponentType compType = (ComponentType) obj;

    if ((StringUtils.equals(type, compType.getType()))
        && (StringUtils.equals(superType, compType.getType()))
        && (connectionTypes.equals(compType.getConnectionTypes()))
        && (cmId.equals(compType.getCmId()))
        && (StringUtils.equals(description, compType.getDescription()))) {
      return true;
    }

    return false;
  }

  @Override
  public ComponentType clone() {
    Map<String, String> cloneConnTypes = new HashMap<>();
    for (String key : connectionTypes.keySet()) {
      cloneConnTypes.put(key, connectionTypes.get(key));
    }
    List<String> cloneCmId = new ArrayList<>();
    for (String val : cmId) {
      cloneCmId.add(val);
    }
    return new ComponentType(
        type, superType, cloneConnTypes, description, cloneCmId);
  }

  @Override
  public String toString() {
    ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("type", type);
    sb.append("superType", superType);
    sb.append("connectionTypes", connectionTypes.toString());
    sb.append("description", description);
    sb.append("cmId", cmId.toString());

    return sb.toString();
  }

  @Override
  public boolean readValue(Value value) {
    try {
      MapValue map = value.asMapValue();

      Value typeValue = map.get(ValueFactory.createRawValue("type"));
      Validate.notNull(typeValue, "invalid value: type");
      setType(typeValue.asRawValue().getString());

      Value superTypeValue = map.get(ValueFactory.createRawValue("super_type"));
      Validate.notNull(superTypeValue, "invalid value: super_type");
      setSuperType(superTypeValue.asRawValue().getString());

      Value connectionTypesValue = map.get(ValueFactory.createRawValue("connection_types"));
      if (connectionTypesValue != null && !connectionTypesValue.isNilValue()) {
        MapValue connectionTypesMap = connectionTypesValue.asMapValue();
        for (Entry<Value, Value> entry : connectionTypesMap.entrySet()) {
          String key = entry.getKey().asRawValue().getString();
          String val = entry.getValue().asRawValue().getString();
          if (key != null) {
            connectionTypes.put(key, val);
          }
        }
      }

      Value descriptionValue = map.get(ValueFactory.createRawValue("description"));
      Validate.notNull(descriptionValue, "invalid value: description");
      setDescription(descriptionValue.asRawValue().getString());

      Value cmIdValue = map.get(ValueFactory.createRawValue("cm_id"));
      if (cmIdValue != null && !cmIdValue.isNilValue()) {
        ArrayValue cmIdList = cmIdValue.asArrayValue();
        for (Value entry : cmIdList) {
          String val = entry.asRawValue().getString();
          if (val != null) {
            cmId.add(val);
          }
        }
      }

      return true;

    } catch (IllegalArgumentException ex) {
      logger.error(ex.getMessage(), ex);
      return false;
    }
  }

  @Override
  public boolean writeValueSub(Map<String, Value> values) {
    if (values == null) {
      logger.error("values is null");
      throw new IllegalArgumentException("values is null");
    }

    values.put("type", ValueFactory.createRawValue(getType()));
    values.put("super_type", ValueFactory.createRawValue(getSuperType()));

    Value[] connectionTiypesMap = new Value[getConnectionTypes().size() * 2];
    int num = 0;
    for (Entry<String, String> entry : getConnectionTypes().entrySet()) {
      connectionTiypesMap[num * 2] = ValueFactory.createRawValue(entry.getKey());
      connectionTiypesMap[num * 2 + 1] = ValueFactory.createRawValue(entry.getValue());
      num++;
    }
    values.put("connection_types", ValueFactory.createMapValue(connectionTiypesMap));

    values.put("description", ValueFactory.createRawValue(getDescription()));

    Value[] idArray = new Value[getCmId().size()];
    num = 0;
    for (String id : getCmId()) {
      idArray[num] = ValueFactory.createRawValue(id);
      ++num;
    }
    if (num != 0) {
      values.put("cm_id", ValueFactory.createArrayValue(idArray));
    }

    return true;

  }

}
