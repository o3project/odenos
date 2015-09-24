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

package sliver_network;

import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import simple_controller.SimpleControllerBase;

public class StartSliverNetwork extends SimpleControllerBase{

    private static Logger log = LogManager.getLogger(StartSliverNetwork.class);

    // Object Id
    protected static final String DUMMY_DRIVER_ID = "dummy_driver";
    protected static final String LEARNING_SWITCH_01_ID = "learning_switch01";
    protected static final String LEARNING_SWITCH_02_ID = "learning_switch02";
    protected static final String SLICER_ID = "slicer";
    protected static final String ORIGINAL_NW_ID = "original_network";
    protected static final String SLIVER_01_NW_ID = "slicer_network01";
    protected static final String SLIVER_02_NW_ID = "slicer_network02";

    protected static final String DRIVER_CONNECT_ID =
            String.format("%s_%s", DUMMY_DRIVER_ID, ORIGINAL_NW_ID);

    protected static final String SLICER_CONNECT01_ID =
            String.format("%s_%s", SLICER_ID, ORIGINAL_NW_ID);

    protected static final String SLICER_CONNECT02_ID =
            String.format("%s_%s", SLICER_ID, SLIVER_01_NW_ID);

    protected static final String SLICER_CONNECT03_ID =
            String.format("%s_%s", SLICER_ID, SLIVER_02_NW_ID);

    protected static final String LSW_CONNECT01_ID =
            String.format("%s_%s", LEARNING_SWITCH_01_ID, SLIVER_01_NW_ID);

    protected static final String LSW_CONNECT02_ID =
            String.format("%s_%s", LEARNING_SWITCH_02_ID, SLIVER_02_NW_ID);

    // Type name
    protected static final String DUMMY_DRIVER = "DummyDriver";
    protected static final String LEARNING_SWITCH = "LearningSwitch";
    protected static final String SLICER = "Slicer";
    protected static final String NETWORK = "Network";
    
    // Connection Type
    protected static final String ORIGINAL = "original";
    protected static final String SLIVER = "sliver";

    // Wait time[ms]
    protected static final int WAIT_TIME = 500;

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        log.debug("Start initialization...");

        // /////////////////////////////////////
        // Set MessageDispatcher.
        // /////////////////////////////////////
        MessageDispatcher dispatcher = new MessageDispatcher(
                SYSTEM_MGR_ID,
                DISPATCHER_IP,
                Integer.parseInt(DISPATCHER_PORT));
        dispatcher.start();
    
        // /////////////////////////////////////
        // // Set SystemManager Interface
        // /////////////////////////////////////
        SystemManagerInterface systemMngIf =
                new SystemManagerInterface(dispatcher);

        ObjectProperty sendProp = null;
        ObjectProperty getProp = null;
        Response rsp = null;
        String msg = "";
        int n = 0;
        
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + ORIGINAL_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                ORIGINAL_NW_ID // object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + ORIGINAL_NW_ID + "). ", rsp);

        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + SLIVER_01_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                SLIVER_01_NW_ID // object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + SLIVER_01_NW_ID + "). ", rsp);

        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + SLIVER_02_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                SLIVER_02_NW_ID // object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + SLIVER_02_NW_ID + "). ", rsp);
        
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + SLICER_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                SLICER, // object_type
                SLICER_ID// object_id
                );
        sendProp.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        // Check.
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + SLICER_ID + "). ", rsp);

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + DUMMY_DRIVER_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                DUMMY_DRIVER, // object_type
                DUMMY_DRIVER_ID// object_id
                );
        sendProp.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        // Check.
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + DUMMY_DRIVER_ID + "). ", rsp);

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + LEARNING_SWITCH_01_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                LEARNING_SWITCH, // object_type
                LEARNING_SWITCH_01_ID// object_id
                );
        sendProp.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        // Check.
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LEARNING_SWITCH_01_ID + "). ", rsp);

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + LEARNING_SWITCH_02_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                LEARNING_SWITCH, // object_type
                LEARNING_SWITCH_02_ID// object_id
                );
        sendProp.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        // Check.
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LEARNING_SWITCH_02_ID + "). ", rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).", SLICER_ID, ORIGINAL_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        ComponentConnection conn;
        conn = new ComponentConnectionLogicAndNetwork(
                SLICER_CONNECT01_ID, // object_id
                ORIGINAL, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                SLICER_ID, // logic_id
                ORIGINAL_NW_ID // network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).", SLICER_ID, SLIVER_01_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                SLICER_CONNECT02_ID, // object_id
                SLIVER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                SLICER_ID, // logic_id
                SLIVER_01_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).", SLICER_ID, SLIVER_02_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                SLICER_CONNECT03_ID, // object_id
                SLIVER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                SLICER_ID, // logic_id
                SLIVER_02_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).",
                DUMMY_DRIVER_ID, ORIGINAL_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                DRIVER_CONNECT_ID, // object_id
                DUMMY_DRIVER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                DUMMY_DRIVER_ID, // logic_id
                ORIGINAL_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);

        outMsg("");
        msg = String.format("(%s <--> %s).",
                LEARNING_SWITCH_01_ID, SLIVER_01_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                LSW_CONNECT01_ID, // object_id
                LEARNING_SWITCH, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LEARNING_SWITCH_01_ID, // logic_id
                SLIVER_01_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).",
                LEARNING_SWITCH_02_ID, SLIVER_02_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                LSW_CONNECT02_ID, // object_id
                LEARNING_SWITCH, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LEARNING_SWITCH_02_ID, // logic_id
                SLIVER_02_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        dispatcher.close();
        System.exit(0);
    }

    /**
     * 
     * @param msg
     */
    protected static void outMsg(String msg) {
        System.out.println(msg);
    }

    /**
     * 
     * @param time
     */
    protected static void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param sendProp
     * @param getProp
     * @return
     */
    protected static boolean checkProperty(
            ObjectProperty sendProp, ObjectProperty getProp) {
        if (sendProp == null || getProp == null
                || !getProp.getObjectId().equals(
                        sendProp.getObjectId())) {
            log.error("Failed.");
            return false;
        } 
        return true;
    }
    
    /**
     * 
     * @param msg
     * @param rsp
     */
    protected static void dumpResponse(String msg, Response rsp) {
        outMsg(msg);
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }

}
