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

package federated_network;

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.util.List;

public class CleanUp extends StartFederatedNetwork {

  public static void main(String[] args) {
        
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
    
    // /////////////////////////////////////
    // Set NetworkIntece
    // /////////////////////////////////////
    NetworkInterface org01NwIf =
        new NetworkInterface(dispatcher, ORIGINAL_01_NW_ID);
    NetworkInterface org02NwIf =
        new NetworkInterface(dispatcher, ORIGINAL_02_NW_ID);
    NetworkInterface fedNwIf =
        new NetworkInterface(dispatcher, FEDERATED_NW_ID);

    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (1) Delete All Flows.");
    outMsg("//////////////////////////////////////////////////");
    List<Response> rsps = fedNwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);

    rsps = org01NwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);

    rsps = org02NwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);
     
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (2) Delete Topology.");
    outMsg("//////////////////////////////////////////////////");
    rsps = fedNwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME * 2);
    
    rsps = org01NwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }   
    wait(WAIT_TIME * 2);
    
    rsps = org02NwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }   
    wait(WAIT_TIME * 2);
   
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (3) Delete Connections.");
    outMsg("//////////////////////////////////////////////////");
    Response rsp = systemMngInterface.delConnection(DRIVER_CONNECT01_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(DRIVER_CONNECT02_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(FEDERATOR_CONNECT01_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(FEDERATOR_CONNECT02_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(FEDERATOR_CONNECT03_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delConnection(LSW_CONNECT_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (4) Delete Components.");
    outMsg("//////////////////////////////////////////////////");
    rsp = systemMngInterface.delComponent(DUMMY_DRIVER_01_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delComponent(DUMMY_DRIVER_02_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delComponent(LEARNING_SWITCH_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(FEDERATED_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(ORIGINAL_01_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(ORIGINAL_02_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(FEDERATOR_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    dispatcher.close();
    System.exit(0);

  }

}


