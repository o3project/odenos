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

import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.SystemManagerInterface;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import java.util.List;


public class CleanUp extends RegisterAggregator {

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
    NetworkInterface originalNwInterface =
        new NetworkInterface(dispatcher, ORIGINAL_NW_ID);
    NetworkInterface aggregatedNwInterface =
        new NetworkInterface(dispatcher, AGGREGATED_NW_ID);

    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (1) Delete All Flows.");
    outMsg("//////////////////////////////////////////////////");
    List<Response> rsps = originalNwInterface.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);

    rsps = aggregatedNwInterface.deleteAllFlow(); 
    for (Response rsp : rsps) {
      outMsg("  -DELETE FLow. ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME);
    
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (2) Delete Topology.");
    outMsg("//////////////////////////////////////////////////");
    rsps = originalNwInterface.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }
    wait(WAIT_TIME * 2);
    
    rsps = aggregatedNwInterface.deleteTopology();
    for (Response rsp : rsps) {
      outMsg("  -PUT (empty)Topology (Delete Topology). ");
      outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    }   
    wait(WAIT_TIME * 2);
   
    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (3) Delete Connections.");
    outMsg("//////////////////////////////////////////////////");
    Response rsp = systemMngInterface.delConnection("conn1");
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delConnection("conn2");
    outMsg("  -DELETE Connection. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);


    outMsg("//////////////////////////////////////////////////");
    outMsg("//// (4) Delete Components.");
    outMsg("//////////////////////////////////////////////////");
    rsp = systemMngInterface.delComponent(ORIGINAL_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    rsp = systemMngInterface.delComponent(AGGREGATED_NW_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);
    
    rsp = systemMngInterface.delComponent(AGGREGATOR_ID);
    outMsg("  -DELETE Component. ");
    outMsg("  -Received: " + rsp.statusCode + " " + rsp.getBodyValue());
    wait(WAIT_TIME * 2);

    dispatcher.close();
    System.exit(0);

  }

}
