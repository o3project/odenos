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

import static org.msgpack.template.Templates.tList;
import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

import org.msgpack.MessagePack;
import org.msgpack.MessagePackable;
import org.msgpack.packer.Packer;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Unpack a message body.
 *
 */
public abstract class MessageBodyUnpacker implements MessagePackable {
  private static final Logger log = LoggerFactory.getLogger(MessageBodyUnpacker.class);

  @SuppressWarnings("serial")
  public static class ParseBodyException extends IOException {
    public static final int STATUS_CODE = 400;

    public ParseBodyException(Throwable ex) {
      super(ex);
    }

    public ParseBodyException(String message) {
      super(message);
    }

    public ParseBodyException(String message, Throwable ex) {
      super(message, ex);
    }
  }

  @SuppressWarnings("serial")
  public static class StringList extends LinkedList<String> implements
      MessagePackable {
    @Override
    public void writeTo(Packer pk) throws IOException {
      pk.write(this);
    }

    @Override
    public void readFrom(Unpacker unpk) throws IOException {
      this.clear();
      this.addAll(unpk.read(tList(TString)));
    }
  }

  @SuppressWarnings("serial")
  public static class StringMap extends HashMap<String, String> implements
      MessagePackable {
    @Override
    public void writeTo(Packer pk) throws IOException {
      pk.write(this);
    }

    @Override
    public void readFrom(Unpacker unpk) throws IOException {
      this.clear();
      this.putAll(unpk.read(tMap(TString, TString)));
    }

  }

  @SuppressWarnings("serial")
  public static class TemplateHashMap<T> extends HashMap<String, T> implements
      MessagePackable {

    private Class<T> valueClass;

    public TemplateHashMap(final Class<T> clazz) {
      this.valueClass = clazz;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
      pk.writeMapBegin(this.size());
      for (String key : this.keySet()) {
        pk.write(key);
        pk.write(this.get(key));
      }
      pk.writeMapEnd();
    }

    @Override
    public void readFrom(Unpacker unpk) throws IOException {
      int mapSize = unpk.readMapBegin();
      for (int i = 0; i < mapSize; i++) {
        String typeName = unpk.readString();
        this.put(typeName, unpk.read(this.valueClass));
      }
      unpk.readMapEnd();
    }
  }

  protected Object body = null;
  protected Value bodyValue = null;
  private MessagePack msgpack = new MessagePack();

  /**
   * Returns the value of specified class.
   * @param clazz a class.
   * @return the value of specified class.
   * @throws ParseBodyException if failed to parse a body.
   */
  @SuppressWarnings("unchecked")
  public <T> T getBody(Class<T> clazz) throws ParseBodyException {
    if (body == null && bodyValue != null) {
      try {
        body = msgpack.convert(bodyValue, clazz);
        // TODO avoid increase of memory use
        bodyValue = null;
      } catch (IOException e) {
        // throw new ParseBodyException(e);
        log.error("IOException", e);
        //e.printStackTrace();
      }
    }
    return (T) body;
  }

  /**
   * Returns the value of specified class.
   * @param clazz a class.
   * @return the value of specified class.
   */
  public <T> T getBody2(Class<T> clazz) {
    try {
      return this.getBody(clazz);
    } catch (ParseBodyException e) {
      log.error("ParseBodyException", e);
      //e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the value object.
   * @return the value object.
   */
  public Value getBodyValue() {
    if (bodyValue == null && body != null) {
      try {
        bodyValue = msgpack.unconvert(body);
      } catch (IOException e) {
        log.error("IOException", e);
        //e.printStackTrace();
      }
    }
    return bodyValue;
  }

  /**
   * Returns the map of specified class.
   * @param clazz a class.
   * @return the map of specified class.
   */
  @SuppressWarnings("unchecked")
  public <T> Map<String, T> getBodyAsMap(Class<T> clazz) {
    Map<String, T> map = new HashMap<String, T>();
    if (body == null && bodyValue != null) {
      try {
        MapValue mapvalue = bodyValue.asMapValue();
        for (Map.Entry<Value, Value> e : mapvalue.entrySet()) {
          map.put(e.getKey().asRawValue().getString(),
              msgpack.convert(e.getValue(), clazz));

        }
        body = map;
      } catch (IOException e) {
        log.error("IOException", e);
        //e.printStackTrace();
      }
    }
    return (Map<String, T>) body;
  }

  /**
   * Returns the list of specified class.
   * @param clazz a class.
   * @return the list of specified class.
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> getBodyAsList(Class<T> clazz) {
    List<T> list = new ArrayList<T>();
    if (body == null && bodyValue != null) {
      try {
        ArrayValue valueList = bodyValue.asArrayValue();
        for (Value e : valueList) {
          list.add(msgpack.convert(e, clazz));
        }
        body = list;
      } catch (IOException e) {
        log.error("IOException", e);
        //e.printStackTrace();
      }
    }
    return (List<T>) body;
  }

  public List<String> getBodyAsStringList() throws ParseBodyException {
    return getBody(StringList.class);
  }

  public Map<String, String> getBodyAsStringMap() throws ParseBodyException {
    return getBody(StringMap.class);
  }

  public boolean isBodyNull() {
    return (bodyValue == null) ? body == null : bodyValue.isNilValue();
  }

  @Override
  public abstract void readFrom(Unpacker unpacker) throws IOException;

  @Override
  public abstract void writeTo(Packer packer) throws IOException;
}
