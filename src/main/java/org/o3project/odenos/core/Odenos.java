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

package org.o3project.odenos.core;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.o3project.odenos.component.aggregator.Aggregator;
import org.o3project.odenos.component.federator.Federator;
import org.o3project.odenos.component.learningswitch.LearningSwitch;
import org.o3project.odenos.component.linklayerizer.LinkLayerizer;
import org.o3project.odenos.component.slicer.Slicer;
import org.o3project.odenos.core.component.DummyDriver;
import org.o3project.odenos.core.component.network.Network;
import org.o3project.odenos.core.manager.ComponentManager2;
import org.o3project.odenos.core.manager.system.SystemManager;
import org.o3project.odenos.core.manager.system.SystemManagerIF;
import org.o3project.odenos.core.util.ComponentLoader;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.RemoteObject;
import org.o3project.odenos.remoteobject.manager.EventManager;
import org.o3project.odenos.remoteobject.messagingclient.Config;
import org.o3project.odenos.remoteobject.messagingclient.Config.MODE;
import org.o3project.odenos.remoteobject.messagingclient.ConfigBuilder;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.o3project.odenos.remoteobject.rest.RESTTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class Odenos {

  private static final Logger log = LoggerFactory.getLogger(Odenos.class);

  public static final String MSGSV_IP = "127.0.0.1";
  public static final int MSGSV_PORT = 6379;
  public static final int REST_PORT = 10080;
  public static final String SYSTEM_MGR_ID = "systemmanager";
  public static final String EVENT_MGR_ID = "eventmanager";
  public static final String REST_TRANSLATOR_ID = "resttranslator";
  public static final String DEFAULT_REST_CONF = "etc/odenos_rest.conf";
  

  private MessageDispatcher disp;
  private String systemMgrId;
  private String msgsvIp;
  private int msgsvPort;
  private boolean systemIsEnabled;
  private String msgsvIpBackup;
  private int msgsvPortBackup;
  private String romgrId;
  private String directory;
  private CommandParser parser = new CommandParser();
  private int restport;
  private String restroot;
  private boolean monitorEnabled;

  private final class CommandParser {
    CommandLine line;
    Options options;

    public CommandParser() {
      options = new Options();
      options.addOption("d", "directory", true, "directory including Components to be loaded");
      options.addOption("i", "ip", true, "ip address or host name of MessagingServer");
      options.addOption("p", "port", true, "port number of MessagingServer");
      options.addOption("I", "ip_backup", true, "ip address or host name of backup MessagingServer");
      options.addOption("P", "port_backup", true, "port number of backup MessagingServer");
      options.addOption("r", "romgr", true, "start ComponentManager with specified id");
      options.addOption("s", "system", false, "start core system");
      options.addOption("S", "system_with_name", true, "start core system with specified id");
      options.addOption("o", "restport", true, "port number of RestPort");
      options.addOption("h", "restroot", true, "Directory of Rest root");
      options.addOption("m", "monitor", true, "Directory of Rest root");
    }

    public final void parse(String[] args) throws ParseException {
      line = new BasicParser().parse(options, args);
    }

    public final String getIp() {
      if (line.hasOption("ip")) {
        String ip = line.getOptionValue("ip");
        return (ip.equals("null")) ?  null: ip;
      } else {
        return null;
      }
    }

    public final int getPort() {
      return line.hasOption("port")
          ? Integer.parseInt(line.getOptionValue("port")) : 0;
    }

    public final String getIpBackup() {
      if (line.hasOption("ip_backup")) {
        String ip = line.getOptionValue("ip_backup");
        return (ip.equals("null")) ?  null: ip;
      } else {
        return null;
      }
    }

    public final int getPortBackup() {
      return line.hasOption("port_backup") 
          ? Integer.parseInt(line.getOptionValue("port_backup")) : 0;
    }

    public final boolean getSystem() {
      return line.hasOption("system") ? true : false;
    }

    public final String getSystemWithName() {
      if (line.hasOption("system_with_name")) {
        return line.getOptionValue("system_with_name");
      } else {
        return SYSTEM_MGR_ID;
      }
    }

    public final String getRoMgr() {
      return line.hasOption("romgr") ? line.getOptionValue("romgr") : null;
    }

    public final String getDirectory() {
      return line.hasOption("directory") ? line.getOptionValue("directory") : null;
    }

    public final int getRestPort() {
      return line.hasOption("restport") ? Integer.parseInt(line.getOptionValue("restport")) : REST_PORT;
    }

    public final String getRestRoot() {
      return line.hasOption("restroot") ? line.getOptionValue("restroot") : null;
    }
    
    public final boolean getMonitor() {
      if (line.hasOption("monitor")) {
        if (line.getOptionValue("monitor").equals("true")) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
  }

  /**
   * Parse parameters.
   * @param args arguments.
   * @throws ParseException if failed to parse.
   */
  public final void parseParameters(String[] args) throws ParseException {
    parser.parse(args);

    msgsvIp = parser.getIp();
    msgsvPort = parser.getPort();
    msgsvIpBackup = parser.getIpBackup();
    msgsvPortBackup = parser.getPortBackup();
    systemIsEnabled = parser.getSystem();
    systemMgrId = parser.getSystemWithName();
    romgrId = parser.getRoMgr();
    if (romgrId != null) {
      directory = parser.getDirectory();
      if (directory == null) {
        throw new ParseException("please specify '-d' when you want to specify '-r'");
      }
    }
    restport = parser.getRestPort();
    restroot = parser.getRestRoot();
    monitorEnabled = parser.getMonitor();
  }

  /**
   * Start ODENOS.
   */
  public final void run() {
    try {
      EnumSet<MODE> mode;
      if (monitorEnabled) {
        mode = EnumSet.of(MODE.RESEND_SUBSCRIBE_ON_RECONNECTED,
                          MODE.INCLUDE_SOURCE_OBJECT_ID,
                          MODE.REFLECT_MESSAGE_TO_MONITOR);
      } else {
        mode = EnumSet.of(MODE.RESEND_SUBSCRIBE_ON_RECONNECTED);
      }
      Config config = new ConfigBuilder()
          .setSystemManagerId(systemMgrId)
          .setEventManagerId(EVENT_MGR_ID)
          //.setSourceDispatcherId("disp-"+ ManagementFactory.getRuntimeMXBean().getName())
          .setHost(msgsvIp)
          .setPort(msgsvPort)
          .setHostB(msgsvIpBackup)
          .setPortB(msgsvPortBackup)
          .setMode(mode)
          //.setRemoteTransactionsMax(20)
          //.setRemoteTransactionsInitialTimeout(3)
          //.setRemoteTransactionsFinalTimeout(30)
          .build();
      disp = new MessageDispatcher(config);
      disp.start();

      if (systemIsEnabled) {
        this.runCoreSystem(systemMgrId);
      }

      if (romgrId != null) {
        this.runComponentManager(romgrId, directory);
      }

      disp.join();
    } catch (Exception e) {
      log.error("system start failed", e);
    } finally {
      disp.close();
    }
  }

  private final void runCoreSystem(String systemMgrId) {
    EventManager evtmgr = new EventManager(EVENT_MGR_ID, disp);
    SystemManager sysmgr = new SystemManager(systemMgrId, disp, evtmgr.getProperty());
    RESTTranslator restTranslator
      = new RESTTranslator(REST_TRANSLATOR_ID, disp, restroot, restport);
  }

  private final void runComponentManager(final String romgrId, final String dir) throws Exception {
    SystemManagerIF sysmgr = new SystemManagerIF(romgrId, disp);
    ComponentManager2 romgr = new ComponentManager2(romgrId, disp);
    romgr.registerComponents(this.findComponents(dir));
    sysmgr.addComponentManager(romgr.getProperty());
    romgr.setState(ObjectProperty.State.RUNNING);
  }

  private Set<Class<? extends RemoteObject>> findComponents(String rootOfPackages) {
    Set<Class<? extends RemoteObject>> classes = new HashSet<Class<? extends RemoteObject>>();

    try {
      classes.addAll(ComponentLoader.load(rootOfPackages));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // default
    classes.add(Aggregator.class);
    classes.add(Federator.class);
    classes.add(LearningSwitch.class);
    classes.add(LinkLayerizer.class);
    classes.add(Slicer.class);
    classes.add(Network.class);
    classes.add(DummyDriver.class);

    return classes;
  }

  /**
   * initialize and start ODENOS.
   * @param args arguments.
   * @throws Exception if failed to parse arguments, if failed to run.
   */
  public static void main(String[] args) throws Exception {
    Odenos odenos = new Odenos();

    try {
      odenos.parseParameters(args);
      odenos.run();
    } catch (ParseException e) {
      System.err.println("Invalid command line parameters: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("invalid configuration file: " + e.getMessage());
      log.error("invalid", e);
    }
  }
}
