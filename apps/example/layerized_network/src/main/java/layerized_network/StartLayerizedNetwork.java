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

package layerized_network;

import org.o3project.odenos.component.learningswitch.LearningSwitch;
import org.o3project.odenos.component.linklayerizer.LinkLayerizer;
import org.o3project.odenos.core.component.DummyDriver;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.component.network.Network;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import simple_controller.SimpleControllerBase;

public class StartLayerizedNetwork extends SimpleControllerBase {
    private static Logger log = LogManager.getLogger(StartLayerizedNetwork.class);
    private static final int TXIDOFFSET = 9000000;

    // Object Id
    protected static final String DUMMY_DRIVER_ID = "dummy-driver";
    protected static final String LEARNING_SWITCH_ID = "learning-switch";
    protected static final String LINK_LAYERIZER_ID = "link-layerizer";
    protected static final String UPPER_NW_ID = "upper-nw";
    protected static final String LOWER_NW_ID = "lower-nw";
    protected static final String LAYERIZED_NW_ID = "layerized-nw";
    
    protected static final String DRIVER_CONNECT_ID =
            String.format("%s_%s", DUMMY_DRIVER_ID, UPPER_NW_ID);

    protected static final String LAYERIZER_CONNECT01_ID =
            String.format("%s_%s", LINK_LAYERIZER_ID, LAYERIZED_NW_ID);

    protected static final String LAYERIZER_CONNECT02_ID =
            String.format("%s_%s", LINK_LAYERIZER_ID, UPPER_NW_ID);

    protected static final String LAYERIZER_CONNECT03_ID =
            String.format("%s_%s", LINK_LAYERIZER_ID, LOWER_NW_ID);
    
    protected static final String LSW_CONNECT_ID =
            String.format("%s_%s", LEARNING_SWITCH_ID, LAYERIZED_NW_ID);
    
    // Type name
    protected static final String DUMMY_DRIVER =
            DummyDriver.class.getSimpleName();
    protected static final String LEARNING_SWITCH =
            LearningSwitch.class.getSimpleName();
    protected static final String LINK_LAYERIZER =
            LinkLayerizer.class.getSimpleName();
    protected static final String NETWORK =
            Network.class.getSimpleName();

    // Connection Type
    protected static final String LAYERIZED = "layerized";
    protected static final String UPPER = "upper";
    protected static final String LOWER = "lower";

    // Wait time[ms]
    protected static final int WAIT_TIME = 500;   
    
    public static void main(String[] args) {
        LogMessage.initParameters(TXIDOFFSET);
        String txid = LogMessage.createTxid();
        LogMessage.setSavedTxid(txid);

        log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Start initialization..."));

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
        outMsg("//// (" + (++n) + ") Create " + LAYERIZED_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                LAYERIZED_NW_ID// object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LAYERIZED_NW_ID + "). ", rsp);

        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + UPPER_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                UPPER_NW_ID// object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + UPPER_NW_ID + "). ", rsp);
     
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + LOWER_NW_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                NETWORK, // object_type
                LOWER_NW_ID// object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LOWER_NW_ID + "). ", rsp);

        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + LINK_LAYERIZER_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                LINK_LAYERIZER, // object_type
                LINK_LAYERIZER_ID// object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LINK_LAYERIZER_ID + "). ", rsp);
 
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create " + LEARNING_SWITCH_ID + ".");
        outMsg("//////////////////////////////////////////////////");
        sendProp = new ObjectProperty(
                LEARNING_SWITCH, // object_type
                LEARNING_SWITCH_ID// object_id
                );
        rsp = systemMngIf.putComponent(sendProp);
        wait(WAIT_TIME);
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + LEARNING_SWITCH_ID + "). ", rsp);

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
        // Check.
        getProp = systemMngIf.getComponent(sendProp.getObjectId());
        if (!checkProperty(sendProp, getProp)) { return; }
        dumpResponse(
                "  -PUT Compoent(" + DUMMY_DRIVER_ID + "). ", rsp);       
        
        outMsg("");
        msg = String.format("(%s <--> %s).",
                LINK_LAYERIZER_ID, LAYERIZED_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        ComponentConnection conn;
        conn = new ComponentConnectionLogicAndNetwork(
                LAYERIZER_CONNECT01_ID, // object_id
                LAYERIZED, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LINK_LAYERIZER_ID, // logic_id
                LAYERIZED_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);      
        
        outMsg("");
        msg = String.format("(%s <--> %s).", LINK_LAYERIZER_ID, UPPER_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                LAYERIZER_CONNECT02_ID, // object_id
                UPPER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LINK_LAYERIZER_ID, // logic_id
                UPPER_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);

        outMsg("");
        msg = String.format("(%s <--> %s).", LINK_LAYERIZER_ID, LOWER_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                LAYERIZER_CONNECT03_ID, // object_id
                LOWER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LINK_LAYERIZER_ID, // logic_id
                LOWER_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).", DUMMY_DRIVER_ID, UPPER_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                DRIVER_CONNECT_ID, // object_id
                UPPER, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                DUMMY_DRIVER_ID, // logic_id
                UPPER_NW_ID// network_id
                );
        rsp = systemMngIf.putConnection(conn);
        dumpResponse("  -PUT Connection " + msg, rsp);
        
        outMsg("");
        msg = String.format("(%s <--> %s).",
                LEARNING_SWITCH_ID, LAYERIZED_NW_ID);
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (" + (++n) + ") Create Connection. " + msg);
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                LSW_CONNECT_ID, // object_id
                LAYERIZED, // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LEARNING_SWITCH_ID, // logic_id
                LAYERIZED_NW_ID// network_id
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
            log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
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
