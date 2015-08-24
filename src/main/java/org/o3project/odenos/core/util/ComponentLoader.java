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

import org.apache.commons.io.FilenameUtils;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class ComponentLoader {
  private static ClassLoader classLoader;

  private final static Logger log = LoggerFactory.getLogger(ComponentLoader.class);

  /**
   * create Class Loader.
   * @param dirname package path name.
   * @return class loader.
   * @throws IOException exception.
   */
  public static ClassLoader createClassLoader(final String dirname) throws IOException {
    URL[] url = new URL[1];
    File file;
    if (dirname.endsWith("/")) {
      file = new File(dirname);
    } else {
      file = new File(dirname + "/");
    }
    url[0] = file.toURI().toURL();

    ClassLoader parent = ClassLoader.getSystemClassLoader();
    URLClassLoader loader = new URLClassLoader(url, parent);

    return loader;
  }

  /**
   * Load packages.
   * @param rootOfPackages class package path name.
   * @return list of remote object.
   * @throws Exception failed to create class loader.
   */
  public static List<Class<? extends RemoteObject>> load(final String rootOfPackages)
      throws Exception {
    classLoader = createClassLoader(rootOfPackages);

    File rootDir = new File(System.getProperty("user.dir") + "/" + rootOfPackages);
    List<Class<? extends RemoteObject>> result = new ArrayList<Class<? extends RemoteObject>>();
    find("", rootDir, result);

    return result;
  }

  @SuppressWarnings("unchecked")
  private static void find(final String parentPackage, final File dir,
      List<Class<? extends RemoteObject>> result)
      throws Exception {
    String packageName = parentPackage;
    if (!packageName.equals("")) {
      packageName += ".";
    }

    File[] entries = dir.listFiles();
    if (entries == null) {
      return;
    }

    for (File entry : entries) {
      if (entry.isDirectory()) {
        find(packageName + entry.getName(), entry, result);
      } else {
        if (!isClassFile(entry)) {
          continue;
        }
        String filename = entry.getName();
        String className = packageName + FilenameUtils.removeExtension(filename);
        Class<?> clazz = classLoader.loadClass(className);
        if (RemoteObject.class.isAssignableFrom(clazz) &&
            !Modifier.isAbstract(clazz.getModifiers())) {
          log.info("Loading... {}", className); 
          result.add((Class<? extends RemoteObject>) clazz);
        }
      }
    }
  }

  private static boolean isClassFile(final File file) {
    String filename = file.getName();
    return FilenameUtils.isExtension(filename, "class");
  }
}
