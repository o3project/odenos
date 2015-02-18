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

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.util.List;

public class CleanUp extends StartLayerizedNetwork {

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
    NetworkInterface layerizedNwIf =
        new NetworkInterface(dispatcher, LAYERIZED_NW_ID);
    NetworkInterface upperNwIf =
        new NetworkInterface(dispatcher, UPPER_NW_ID);
    NetworkInterface lowerNwIf =
        new NetworkInterface(dispatcher, LOWER_NW_ID);

    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (1) Delete All Flows.");
    outMsg("//////////////////////////////////////////////////");
    List<Response> rsps = layerizedNwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);

    rsps = lowerNwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);
    
    rsps = upperNwIf.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);
    
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (2) Delete Topology.");
    outMsg("//////////////////////////////////////////////////");
    rsps = layerizedNwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME * 2);
    
    rsps = upperNwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }   
    wait(WAIT_TIME * 2);
    
    rsps = lowerNwIf.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }   
    wait(WAIT_TIME * 2);
   
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (3) Delete Connections.");
    outMsg("//////////////////////////////////////////////////");
    Response rsp = systemMngInterface.delConnection(DRIVER_CONNECT_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(LAYERIZER_CONNECT01_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(LAYERIZER_CONNECT02_ID);
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection(LAYERIZER_CONNECT03_ID);
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
    rsp = systemMngInterface.delComponent(DUMMY_DRIVER_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delComponent(LEARNING_SWITCH_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delComponent(LOWER_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(LAYERIZED_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(UPPER_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(LINK_LAYERIZER_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    dispatcher.close();
    System.exit(0);

  }

}

