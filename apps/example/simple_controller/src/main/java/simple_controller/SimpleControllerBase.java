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

package simple_controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class SimpleControllerBase {

    protected static final String DISPATCHER_IP = "127.0.0.1";
    protected static final String DISPATCHER_PORT = "6379";

    protected static final String SYSTEM_MGR_ID = "systemmanager";
    protected static final String SYSTEM_MGR_IP = "127.0.0.1";
    protected static final String SYSTEM_MGR_PORT = "22222";
    protected static final String SYSTEM_MGR_BASEURI = "odenos://"
            + SYSTEM_MGR_IP + ":" + SYSTEM_MGR_PORT
            + "/" + SYSTEM_MGR_ID;

    protected static final String EVENT_MGR_ID = "eventmanager";
    protected static final String EVENT_MGR_IP = "127.0.0.1";
    protected static final String EVENT_MGR_PORT = "22222";
    protected static final String EVENT_MGR_BASEURI = "odenos://"
            + EVENT_MGR_IP + ":" + EVENT_MGR_PORT
            + "/" + EVENT_MGR_ID;

    protected static final String COMPONENT_MGR_ID = "romgr1";
    protected static final String COMPONENT_MGR_IP = "127.0.0.1";
    protected static final String COMPONENT_MGR_PORT = "33333";
    protected static final String COMPONENT_MGR_BASEURI = "odenos://"
            + COMPONENT_MGR_IP + ":" + COMPONENT_MGR_PORT
            + "/" + COMPONENT_MGR_ID;

    protected static final String REST_TRANSLATOR_ID = "resttranslator";
    protected static final String REST_TRANSLATOR_IP = "127.0.0.1";
    protected static final String REST_TRANSLATOR_PORT = "10080";
    protected static final String REST_TRANSLATOR_BASEURI = "odenos://"
            + REST_TRANSLATOR_IP + ":" + REST_TRANSLATOR_PORT
            + "/" + REST_TRANSLATOR_ID;

    public static String getUri(String ip, String port, String id) {
        return "odenos://" + ip + ":" + port + "/" + id;
    }

    public static class Parser {

        private final String[] options;

        @Deprecated
        public Parser() {
            this.options = new String[] {};
        }

        public Parser(String[] options) {
            this.options = options;
        }

        protected String getOptionValue(CommandLine cl, String key, String def) {
            return cl.hasOption(key) ? cl.getOptionValue(key) : def;
        }

        protected void showHelp(Options options) {
            new HelpFormatter().printHelp(" ", options);
        }

        protected void addOption(Options options, String key) {
            if ("sid".equals(key))
                options.addOption("sid", true, "SystemManager ID(default: " + SYSTEM_MGR_ID + ")");
            else if ("sip".equals(key))
                options.addOption("sip", true, "SystemManager IP Address(default: " + SYSTEM_MGR_IP
                        + ")");
            else if ("sp".equals(key))
                options.addOption("sp", true, "SystemManager Port Number(default: "
                        + SYSTEM_MGR_PORT + ")");
            else if ("eid".equals(key))
                options.addOption("eid", true, "EventManager ID(default: " + EVENT_MGR_ID + ")");
            else if ("eip".equals(key))
                options.addOption("eip", true, "EventManager IP Address(default: " + EVENT_MGR_IP
                        + ")");
            else if ("ep".equals(key))
                options.addOption("ep", true, "EventManager Port Number(default: " + EVENT_MGR_PORT
                        + ")");
            else if ("cid".equals(key))
                options.addOption("cid", true, "ComponentManager ID(default: " + COMPONENT_MGR_ID
                        + ")");
            else if ("cip".equals(key))
                options.addOption("cip", true, "ComponentManager IP Address(default: "
                        + COMPONENT_MGR_IP + ")");
            else if ("cp".equals(key))
                options.addOption("cp", true, "ComponentManager Port Number(default: "
                        + COMPONENT_MGR_PORT + ")");
            else if ("rid".equals(key))
                options.addOption("rid", true, "RestTranslator ID(default: " + REST_TRANSLATOR_ID
                        + ")");
            else if ("rip".equals(key))
                options.addOption("rip", true, "RestTranslator IP Address(default: "
                        + REST_TRANSLATOR_IP + ")");
            else if ("rp".equals(key))
                options.addOption("rp", true, "RestTranslator Port Number(default: "
                        + REST_TRANSLATOR_PORT + ")");
            else if ("dip".equals(key))
                options.addOption("dip", true, "Dispatcher IP Address(default: " + DISPATCHER_IP
                        + ")");
            else if ("dp".equals(key))
                options.addOption("dp", true, "Dispatcher Port Number(default: " + DISPATCHER_PORT
                        + ")");
            else if ("h".equals(key))
                options.addOption("h", false, "Show help");
            return;
        }

        protected void parseOprion(CommandLine cl, Map<String, String> opt, String key) {
            if ("sid".equals(key))
                opt.put("sid", getOptionValue(cl, "sid", SYSTEM_MGR_ID));
            else if ("sip".equals(key))
                opt.put("sip", getOptionValue(cl, "sip", SYSTEM_MGR_IP));
            else if ("sp".equals(key))
                opt.put("sp", getOptionValue(cl, "sp", SYSTEM_MGR_PORT));
            else if ("eid".equals(key))
                opt.put("eid", getOptionValue(cl, "eid", EVENT_MGR_ID));
            else if ("eip".equals(key))
                opt.put("eip", getOptionValue(cl, "eip", EVENT_MGR_IP));
            else if ("ep".equals(key))
                opt.put("ep", getOptionValue(cl, "ep", EVENT_MGR_PORT));
            else if ("cid".equals(key))
                opt.put("cid", getOptionValue(cl, "cid", COMPONENT_MGR_ID));
            else if ("cip".equals(key))
                opt.put("cip", getOptionValue(cl, "cip", COMPONENT_MGR_IP));
            else if ("cp".equals(key))
                opt.put("cp", getOptionValue(cl, "cp", COMPONENT_MGR_PORT));
            else if ("rid".equals(key))
                opt.put("rid", getOptionValue(cl, "rid", REST_TRANSLATOR_ID));
            else if ("rip".equals(key))
                opt.put("rip", getOptionValue(cl, "rip", REST_TRANSLATOR_IP));
            else if ("rp".equals(key))
                opt.put("rp", getOptionValue(cl, "rp", REST_TRANSLATOR_PORT));
            else if ("dip".equals(key))
                opt.put("dip", getOptionValue(cl, "dip", DISPATCHER_IP));
            else if ("dp".equals(key))
                opt.put("dp", getOptionValue(cl, "dp", DISPATCHER_PORT));
            return;
        }

        public Map<String, String> parse(String[] args) {

            Options options = new Options();
            addOption(options, "h");
            for (String option : this.options) {
                addOption(options, option);
            }

            BasicParser parser = new BasicParser();
            CommandLine cl = null;
            try {
                cl = parser.parse(options, args);
            } catch (ParseException e) {
                showHelp(options);
                return null;
            }
            if (cl.hasOption("h")) {
                showHelp(options);
                return null;
            }

            Map<String, String> ret = new HashMap<String, String>();
            for (String option : this.options) {
                parseOprion(cl, ret, option);
            }
            return ret;
        }
    }
}
