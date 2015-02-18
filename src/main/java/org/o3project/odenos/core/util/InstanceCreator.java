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

package org.o3project.odenos.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Instance creator class.
 *
 */
public class InstanceCreator {

  private static Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

  private InstanceCreator() {
  }

  /**
   * Create a object.
   * @param className fully name of classes.
   * @param types type of class.
   * @param args objects of an argument.
   * @return created object.
   */
  public static Object create(String className, Class<?>[] types, Object[] args) {
    Class<?> clazz = null;
    if (InstanceCreator.classCache.containsKey(className)) {
      clazz = InstanceCreator.classCache.get(className);
    } else {
      try {
        clazz = Class.forName(className);
      } catch (ClassNotFoundException e) {
        return null;
      }
      InstanceCreator.classCache.put(className, clazz);
    }

    Constructor<?> constractor = null;
    try {
      constractor = clazz.getConstructor(types);
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }

    Object object = null;
    try {
      object = constractor.newInstance(args);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      return null;
    }

    return object;
  }
}
