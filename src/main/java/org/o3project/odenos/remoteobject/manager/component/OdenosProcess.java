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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.o3project.odenos.core.manager.system.SystemManagerIF;
import org.o3project.odenos.core.manager.system.event.ComponentManagerChanged;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OdenosProcess extends RemoteObject {
  private static final String SYSMGR = "systemmanager";
  private static final String EVTMGR = "eventmanager";
  private static final String ATTR_COMPTYPE = "component_types";

  protected Map<String, Class<? extends RemoteObject>> classList =
      new HashMap<String, Class<? extends RemoteObject>>();

  public OdenosProcess(String id, MessageDispatcher disp) {
    super(id, disp);
  }

  /**
   * Setup Remote Objects.
   * @param classes remote object classes.
   * @throws Exception if failed to setup.
   */
  public void setup(Set<Class<? extends RemoteObject>> classes) throws Exception {
    this.addRemoteObject(EVTMGR);
    this.subscribeEvents();

    SystemManagerIF sysmgr = new SystemManagerIF(this.getMessageDispatcher());
    this.registerOtherComponentManagers(sysmgr.getComponentManagers());
    this.registerRemoteObjects(classes);
    this.setState(ObjectProperty.State.RUNNING);
    sysmgr.addComponentManager(this.getProperty());
  }

  private void registerRemoteObjects(Set<Class<? extends RemoteObject>> classes) {
    for (Class<? extends RemoteObject> clazz : classes) {
      String type = clazz.getSimpleName();

      if (!classList.containsKey(type)) {
        classList.put(type, clazz);
      }
    }
    this.objectProperty.setProperty(
        ATTR_COMPTYPE, StringUtils.join(classList.keySet(), ","));
  }

  private void subscribeEvents() throws Exception {
    this.eventSubscription.addFilter(SYSMGR, ComponentManagerChanged.TYPE);
    this.applyEventSubscription();
  }

  private void registerOtherComponentManagers(
      ObjectPropertyList componentManagers) throws Exception {
    for (ObjectProperty componentManager : componentManagers) {
      this.addRemoteObject(componentManager.getObjectId());
    }
  }

  /**
   * Start ODENIS process.
   * @param args arguments
   * @throws Exception if failed to start.
   */
  public static void main(String[] args) throws Exception {
    MessageDispatcher disp = null;

    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("i", "id", true, "identifier of this RemoteObject");
    options.addOption("p", "port", true, "listen port of this object");

    try {
      String id = "";
      int port = 0;
      CommandLine line = parser.parse(options, args);
      if (line.hasOption("id")) {
        id = line.getOptionValue("id");
      } else {
        throw new Exception("no id.");
      }
      if (line.hasOption("port")) {
        port = Integer.parseInt(line.getOptionValue("id"));
      }

      disp = new MessageDispatcher();
      disp.start();
      //disp.listen(new InetSocketAddress("0.0.0.0", port));

      OdenosProcess proc = new OdenosProcess(id, disp);
      Set<Class<? extends RemoteObject>> classes =
          new HashSet<Class<? extends RemoteObject>>();
      //      classes.add(Aggregator.class);
      //      classes.add(DummyDriver.class);
      //      classes.add(Federator.class);
      //      classes.add(LearningSwitch.class);
      //      classes.add(LinkLayerizer.class);
      //      classes.add(Network.class);
      //      classes.add(Slicer.class);
      proc.setup(classes);

      disp.join();
    } catch (ParseException e) {
      System.out.println("Parameter error: " + e.getMessage());
    } catch (Exception e) {
      //e.printStackTrace();
    } finally {
      if (disp != null) {
        disp.close();
      }
    }
  }
}
