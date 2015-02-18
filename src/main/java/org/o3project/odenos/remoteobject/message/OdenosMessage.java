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

package org.o3project.odenos.remoteobject.message;

import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Odenos Message class.
 *
 */
public abstract class OdenosMessage extends BaseObject {
  public abstract boolean readValue(Value value);

  public abstract boolean writeValueSub(Map<String, Value> values);

  /**
   * Returns map of Value.
   * @return Map of Value.
   */
  public Value writeValue() {
    Map<String, Value> values = new HashMap<String, Value>();

    if (!writeValueSub(values)) {
      return null;
    }

    Value[] valueList = new Value[values.size() * 2];
    int num = 0;
    for (Entry<String, Value> entry : values.entrySet()) {
      valueList[num * 2] = ValueFactory.createRawValue(
          entry.getKey());
      valueList[num * 2 + 1] = entry.getValue();
      ++num;
    }

    return ValueFactory.createMapValue(valueList);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.remoteobject.message.MessageBodyUnpacker#readFrom(org.msgpack.unpacker.Unpacker)
   */
  @Override
  public void readFrom(Unpacker upk) throws IOException {
    Value value = upk.readValue();
    if (value == null || !readValue(value)) {
      throw new IOException();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.remoteobject.message.MessageBodyUnpacker#writeTo(org.msgpack.packer.Packer)
   */
  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.write(writeValue());
  }
}
