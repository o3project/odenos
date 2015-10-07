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

package register_aggregator;

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


public class RegisterAggregator extends SimpleControllerBase {
    private static Logger log = LogManager.getLogger(RegisterAggregator.class);
    private static final int TXIDOFFSET = 9000000;

    // Object Id
    protected static final String ORIGINAL_NW_ID = "original_network";
    protected static final String AGGREGATED_NW_ID = "aggregated_network";
    protected static final String AGGREGATOR_ID = "aggregator";

    // Type name
    protected static final String AGGREGATOR = "Aggregator";
    protected static final String NETWORK = "Network";

    // Wait time[ms]
    protected static final int WAIT_TIME = 500;

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
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
            log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
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
        // Wait
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
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
        //sendProperty.setProperty(
        //        ObjectProperty.PropertyNames.CM_ID, COMPONENT_MGR_ID);

        // Send Request. (MessageDispathcer ==> SystemManager.)
        rsp = systemMngInterface.putComponent(sendProperty);
        wait(WAIT_TIME);
        getProperty = systemMngInterface.getComponent(
                sendProperty.getObjectId());
        // Check.
        if (rsp == null || getProperty == null
                || !getProperty.getObjectId().equals(
                        sendProperty.getObjectId())) {
            log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
            return;
        }
        outMsg("  -PUT Compoent(Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (4) Create Connection. (Original Nw <=> Aggregator).");
        outMsg("//////////////////////////////////////////////////");
        ComponentConnection conn1;
        ComponentConnection conn2;
        conn1 = new ComponentConnectionLogicAndNetwork(
                "conn1", // object_id
                "original", // connection_type
                null, // connection_status
                AGGREGATOR_ID, // logic_id
                ORIGINAL_NW_ID // network_id
                );
        rsp = systemMngInterface.putConnection(conn1);
        outMsg("  -PUT Connection(OriginalNetwork <--> Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (5) Create Connection. (Aggregated Nw <=> Aggregator).");
        outMsg("//////////////////////////////////////////////////");
        conn2 = new ComponentConnectionLogicAndNetwork(
                "conn2", // object_id
                "aggregated", // connection_type
                null, // connection_status
                AGGREGATOR_ID, // logic_id
                AGGREGATED_NW_ID// network_id
                );
        rsp = systemMngInterface.putConnection(conn2);
        outMsg("  -PUT Connection(AggregatedNetwork <--> Aggregator). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // Check.
        Map<String, ComponentConnection> getConns =
                systemMngInterface.getConnections();
        if (getConns == null) {
            log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
            return;
        }
        for (String conId : getConns.keySet()) {
            ComponentConnection getConn =
                    systemMngInterface.getConnection(conId);
            if (getConn == null) {
                log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Failed."));
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
