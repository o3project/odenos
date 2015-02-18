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

package org.o3project.odenos.remoteobject.manager;

import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.ObjectProperty;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ArrayList of ObjcetProperty.
 *
 */
@SuppressWarnings("serial")
public class ObjectPropertyList extends ArrayList<ObjectProperty> implements
    MessagePackable {

  @Override
  public void writeTo(Packer pk) throws IOException {
    pk.writeArrayBegin(this.size());
    for (ObjectProperty prop : this) {
      pk.write(prop);
    }
    pk.writeArrayEnd();
  }

  @Override
  public void readFrom(Unpacker unpk) throws IOException {
    int arraySize = unpk.readArrayBegin();
    for (int i = 0; i < arraySize; i++) {
      ObjectProperty obj = unpk.read(ObjectProperty.class);
      add(obj);
    }
    unpk.readArrayEnd();
  }

}
