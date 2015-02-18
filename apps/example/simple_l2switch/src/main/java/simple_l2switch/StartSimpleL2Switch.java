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

package simple_l2switch;

import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simple_controller.SimpleControllerBase;

import java.util.Map;

public class StartSimpleL2Switch extends SimpleControllerBase {
    private static Logger log = LoggerFactory.getLogger(StartSimpleL2Switch.class);

    // Object Id
    protected static final String NETWORK_ID = "netowrk1";
    protected static final String DUMMY_DRIVER_ID = "dummy_driver";
    protected static final String LEARNING_SWITCH_ID = "leaning_switch";
    // Type name
    protected static final String NETWORK = "Network";
    protected static final String DUMMY_DRIVER = "DummyDriver";
    protected static final String LEARNING_SWITCH = "LearningSwitch";
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

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (1) Create Network.");
        outMsg("//////////////////////////////////////////////////");
        // Create Network.
        sendProperty = new ObjectProperty(NETWORK, // object_type
                NETWORK_ID // object_id
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
        outMsg("  -PUT Compoent(Netowrk). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (2) Create DummyDriver.");
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
        outMsg("//// (3) Create Learning Switch.");
        outMsg("//////////////////////////////////////////////////");
        sendProperty = new ObjectProperty(
                LEARNING_SWITCH, // object_type
                LEARNING_SWITCH_ID // object_id
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
            log.error("Failed.");
            return;
        }
        outMsg("  -PUT Compoent(LearningSwitch). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (4) Create Connection. (Network <=> DummyDriver).");
        outMsg("//////////////////////////////////////////////////");
        ComponentConnection conn1;
        ComponentConnection conn2;
        conn1 = new ComponentConnectionLogicAndNetwork(
                "conn1", // object_id
                "dummy_drv_nw", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                DUMMY_DRIVER_ID, // logic_id
                NETWORK_ID // network_id
                );
        rsp = systemMngInterface.putConnection(conn1);
        outMsg("  -PUT Connection(Network <--> DummyDriver). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());

        outMsg("");
        outMsg("//////////////////////////////////////////////////");
        outMsg("//// (5) Create Connection. (Network <=> LearningSwitch).");
        outMsg("//////////////////////////////////////////////////");
        conn2 = new ComponentConnectionLogicAndNetwork(
                "conn2", // object_id
                "learning_sw_nw", // connection_type
                ComponentConnection.State.INITIALIZING, // connection_status
                LEARNING_SWITCH_ID, // logic_id
                NETWORK_ID // network_id
                );
        rsp = systemMngInterface.putConnection(conn2);
        outMsg("  -PUT Connection(Network <--> LearningSwitch). ");
        outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
        wait(WAIT_TIME);
        // Check.
        Map<String, ComponentConnection> getConns =
                systemMngInterface.getConnections();
        if (getConns == null || getConns.size() != 2) {
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
