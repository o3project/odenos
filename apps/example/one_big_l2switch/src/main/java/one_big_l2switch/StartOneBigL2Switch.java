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

package one_big_l2switch;

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

import java.util.Map;

public class StartOneBigL2Switch extends SimpleControllerBase {

    private static Logger log = LogManager.getLogger(StartOneBigL2Switch.class);

    // Object Id
    protected static final String DUMMY_DRIVER_ID = "dummy_driver2";
    protected static final String LEARNING_SWITCH_ID = "learning_switch2";
    protected static final String AGGREGATOR_ID = "aggregator2";
    protected static final String ORIGINAL_NW_ID = "original_network2";
    protected static final String AGGREGATED_NW_ID = "aggregated_network2";

    // Type name
    protected static final String DUMMY_DRIVER = "DummyDriver";
    protected static final String LEARNING_SWITCH = "LearningSwitch";
    protected static final String AGGREGATOR = "Aggregator";
    protected static final String NETWORK = "Network";

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
        SystemManagerInterface systemMngInterface =
                new SystemManagerInterface(dispatcher);

        ObjectProperty sendProperty = null;
        ObjectProperty getProperty = null;
        Response rsp = null;

        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (1) Create Original Network.");
        outMsg("//////////////////////////////////////////////////");
        // Create Original Network.
        sendProperty = new ObjectProperty(NETWORK, // object_type
                ORIGINAL_NW_ID // object_id
                );
        // Send Request. (MsgDispathcer => SystemMng => ComponentMng.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(Original Netowrk). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (2) Create Aggregated Network.");
        outMsg("//////////////////////////////////////////////////");
        sendProperty = new ObjectProperty(
                NETWORK, // object_type
                AGGREGATED_NW_ID // object_id
                );
        // Send Request. (MessageDispathcer ==> SystemManager.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(Aggregated Netowrk). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (3) Create Aggregator.");
        outMsg("//////////////////////////////////////////////////");
        sendProperty = new ObjectProperty(
                AGGREGATOR, // object_type
                AGGREGATOR_ID // object_id
                );
        sendProperty.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        // Send Request. (MessageDispathcer ==> SystemManager.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (4) Create DummyDriver.");
        outMsg("//////////////////////////////////////////////////");
        sendProperty = new ObjectProperty(
                DUMMY_DRIVER, // object_type
                DUMMY_DRIVER_ID // object_id
                );
        sendProperty.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        // Send Request. (MessageDispathcer ==> SystemManager.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(DummyDriver). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (5) Create Learning Switch.");
        outMsg("//////////////////////////////////////////////////");
        sendProperty = new ObjectProperty(
                LEARNING_SWITCH, // object_type
                LEARNING_SWITCH_ID // object_id
                );
        sendProperty.setProperty(
                ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);
        // Send Request. (MessageDispathcer ==> SystemManager.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(LearningSwitch). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (6) Create Connection. (OriginalNW <=> Aggregator).");
        outMsg("//////////////////////////////////////////////////");
        ComponentConnection conn;
        conn = new ComponentConnectionLogicAndNetwork(
                "conn1", // object_id
                "original", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                AGGREGATOR_ID, // logic_id
                ORIGINAL_NW_ID // network_id
                );
        rsp = systemMngInterface.putConnection(conn);
        outMsg("  -PUT Connection(OriginalNetwork <--> Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (7) Create Connection. (OriginalNW <=> DummyDriver).");
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                "conn2", // object_id
                "dummy_driver_conn", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                DUMMY_DRIVER_ID, // logic_id
                ORIGINAL_NW_ID // network_id
                );
        rsp = systemMngInterface.putConnection(conn);
        outMsg("  -PUT Connection(OriginalNetwork <--> DummyDriver). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (8) Create Connection. (AggregatedNW <=> Aggregator).");
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                "conn3", // object_id
                "aggregated", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                AGGREGATOR_ID, // logic_id
                AGGREGATED_NW_ID// network_id
                );
        rsp = systemMngInterface.putConnection(conn);
        outMsg("  -PUT Connection(AggregatedNetwork <--> Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (9) Create Connection. ");
        outMsg("////        (AggregatedNW <=> LearningSwitch).");
        outMsg("//////////////////////////////////////////////////");
        conn = new ComponentConnectionLogicAndNetwork(
                "conn4", // object_id
                "learning_sw_conn", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LEARNING_SWITCH_ID, // logic_id
                AGGREGATED_NW_ID// network_id
                );
        rsp = systemMngInterface.putConnection(conn);
        outMsg("  -PUT Connection(AggregatedNetwork <--> LearningSwitch). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        wait(WAIT_TIME);
        // Check.
        Map<String, ComponentConnection> getConns =
                systemMngInterface.getConnections();
        if (getConns == null || getConns.size() == 0) {
            log.error("Failed.");
            return;
        }
        for (String conId : getConns.keySet()) {
            ComponentConnection getConn =
                    systemMngInterface.getConnection(conId);
            if (getConn == null) {
                log.error("Failed.");
                return;
            }
        }
        dispatcher.close();
        System.exit(0);
    }

    protected static void outMsg(String msg) {
        System.out.println(msg);
    }

    protected static void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
