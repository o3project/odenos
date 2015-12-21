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

package org.o3project.odenos.component.generator;

import org.o3project.odenos.core.component.Driver;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.OFPInPacket;
import org.o3project.odenos.core.component.network.packet.OutPacket;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;

/**
 * InPacket generator.
 * 
 * This class generates InPacket for stress testing.
 */
public class Generator extends Driver {

  private final static Logger log = LogManager.getLogger(Generator.class);

  private String network;
  private final String description = "packet-in generator";

  private final RequestParser<IActionCallback> parser;

  private final String objectId;

  private Env env = new Env();
  private Control control = new Control();
  private Stats stats = new Stats();

  private boolean hasAdjacencies = false;
  private boolean dump = false;
  private boolean deleteOnOutPacketAdded = true;

  protected static final byte GO = 0;
  protected static final byte BACK = 1;

  /**
   * Generator constructor.
   */
  public Generator(final String objectId, final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    resetEventSubscription();
    this.objectId = objectId;
    log.info("created.");
  }

  private RequestParser<IActionCallback> createParser() {
    return new RequestParser<IActionCallback>() {
      {
        log.info(">> {}", new Throwable().getStackTrace()[0].getMethodName());

        addRule(Method.PUT, "env/", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed)
              throws ParseBodyException {
            return putEnv(parsed.getRequest().getBody(Env.class));
          }
        });

        addRule(Method.GET, "env/", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getEnv();
          }
        });

        addRule(Method.PUT, "control/", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed)
              throws ParseBodyException {
            return putControl(parsed.getRequest().getBody(Control.class));
          }
        });

        addRule(Method.GET, "control/", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getControl();
          }
        });

        addRule(Method.GET, "stats/", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed) {
            return getStats();
          }
        });

        addRule(Method.POST, "in_packet/<node_id>/<port_id>", new IActionCallback() {
          public Response process(RequestParser<IActionCallback>.ParsedRequest parsed)
              throws Exception {
            return postInPacketFromAdjacentNetwork(parsed.getParam("node_id"),
                parsed.getParam("port_id"), parsed.getRequest().getBody(OFPInPacket.class));
          }
        });

      }
    };
  }

  protected Response putEnv(Env env) {
    this.env = env;
    this.hasAdjacencies = env.hasAdjacencies();
    this.dump = env.getDump();
    this.deleteOnOutPacketAdded = env.getDeleteOutPacket();
    return new Response(Response.OK, null);
  }

  protected Response getEnv() {
    return new Response(Response.OK, env);
  }

  protected Response putControl(Control control) {
    generate(env, control);
    this.control = control;
    return new Response(Response.OK, null);
  }

  protected Response getControl() {
    return new Response(Response.OK, control);
  }

  protected Response getStats() {
    return new Response(Response.OK, stats);
  }

  PostInPacketGenerator postInPacketGenerator = null;
  PostInPacket postInPacket = null;

  private class GeneratorThreadFactory implements ThreadFactory {
    String name;
    int count = 0;

    GeneratorThreadFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      return new Thread(runnable, String.format("%s(%d)", name, count++));
    }
  }

  ExecutorService executorOutPacketHandling = Executors
      .newCachedThreadPool(new GeneratorThreadFactory("OutPacketHandling"));

  ScheduledExecutorService executorInPacketGenerator = Executors
      .newSingleThreadScheduledExecutor(new GeneratorThreadFactory("InPacketGenerator"));

  ScheduledFuture<?> future = null;

  private void generate(Env env, Control control) {
    if (control.getGenerating()) {
      if (future != null) {
        future.cancel(true);
      }
      NetworkInterface networkIf = networkInterfaces().get(network);
      postInPacketGenerator = new PostInPacketGenerator(this, objectId, env, control, stats,
          networkIf);
      postInPacket = new PostInPacket(objectId, stats, networkIf, dump);
      stats.clear();
      future = executorInPacketGenerator.scheduleAtFixedRate(postInPacketGenerator, 0,
          control.getFrequency(), TimeUnit.MILLISECONDS);

    } else {
      if (future != null) {
        future.cancel(true);
        future = null;
      }
    }
  }

  protected void stop() {
    if (future != null) {
      future.cancel(true);
    }
  }

  @Override
  public Response onRequest(Request request) {
    log.debug(">> {}", new Throwable().getStackTrace()[0].getMethodName());
    Response res;

    try {
      log.debug("Received request : {}, {} {}", getObjectId(), request.method, request.path);
      log.debug("Received body    : {}, {}", getObjectId(), request.getBodyValue());
      RequestParser<IActionCallback>.ParsedRequest parsed = parser.parse(request);
      IActionCallback callback = parsed.getResult();
      res = callback.process(parsed);
      return res;
    } catch (Exception e) {
      log.error("Exception in onRequest() : [case:{}] [msg:{}]", request.path, e.getMessage());
      e.printStackTrace();
      res = createErrorResponse(Response.BAD_REQUEST, "Error while processing : ["
          + request.method + "] " + request.path);
      return res;
    }
  }

  private Response createErrorResponse(int code, Object body) {
    log.debug(">> {}", new Throwable().getStackTrace()[0].getMethodName());
    return createErrorResponse(code, body, body.toString());
  }

  private Response createErrorResponse(int code, Object body, String msg) {
    log.debug(">> {}", new Throwable().getStackTrace()[0].getMethodName());
    Response rsp = new Response(code, body);
    log.debug("[{}] {}", code, msg);
    return rsp;
  }

  /**
   * Get Description of Component. Need to Implement at Inheritance Logic Component.
   *
   * @return Description of Component
   */
  @Override
  protected final String getDescription() {
    return this.description;
  }

  // //////////////////////////////////////////////////
  // NetworkConnection
  // //////////////////////////////////////////////////
  @Override
  protected final boolean onConnectionChangedAddedPre(final ComponentConnectionChanged msg) {

    if (!msg.curr().getObjectType().equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(ComponentConnectionLogicAndNetwork.LOGIC_ID);
    if (!this.getObjectId().equals(logicId)) {
      return false;
    }

    ComponentConnection compConn = msg.curr();
    if (this.network != null) {
      // Changed ConectionProperty's status.
      compConn.setConnectionState(ComponentConnection.State.ERROR);
      systemMngInterface().putConnection(compConn);
      return false;
    }

    return true;
  }

  @Override
  protected final void onConnectionChangedAdded(final ComponentConnectionChanged msg) {

    ComponentConnection curr = msg.curr();
    this.network = curr.getProperty(ComponentConnectionLogicAndNetwork.NETWORK_ID);

    subscribeNetworkComponent();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.RUNNING);
    systemMngInterface().putConnection(curr);
  }

  @Override
  protected final void onConnectionChangedDelete(final ComponentConnectionChanged message) {

    ComponentConnection curr = message.curr();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    unsubscribeNetworkComponent();
    this.network = null;

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  private void subscribeNetworkComponent() {

    addEntryEventSubscription(FLOW_CHANGED, this.network);
    addEntryEventSubscription(OUT_PACKET_ADDED, this.network);

    updateEntryEventSubscription(FLOW_CHANGED, this.network, null);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void unsubscribeNetworkComponent() {
    removeEntryEventSubscription(FLOW_CHANGED, this.network);
    removeEntryEventSubscription(OUT_PACKET_ADDED, this.network);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // //////////////////////////////////////////////////
  // Event method override
  // //////////////////////////////////////////////////

  private void setFlowSimulation(String networkId, Flow flow) {
    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());

    // Status ... "None" => "Establishing" => "Established"
    if (targetFlow.getStatus().equals(FlowObject.FlowStatus.NONE.toString())
        && targetFlow.getEnabled()) {
      targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHING.toString());

      // Driver needs to set Flow to physical switch here.
      // Setting of Flow After completing the physical switch,
      // to "Established".

      targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHED.toString());
      networkIf.putFlow(targetFlow);
    }
  }

  @Override
  protected void onFlowAdded(final String networkId, final Flow flow) {

    // Stats update for Generator
    stats.onFlowAdded();
    if (dump) {
      Util.dumpFlow("onFlowAdded", flow);
    }

    setFlowSimulation(networkId, flow);
  }

  @Override
  protected final void onFlowUpdate(final String networkId, final Flow prev, final Flow curr,
      final ArrayList<String> attributesList) {

    // Stats update for Generator
    stats.onFlowUpdate();
    if (dump) {
      Util.dumpFlow("onFlowUpdate(prev)", prev);
      Util.dumpFlow("onFlowUpdate(prev)", curr);
    }

    setFlowSimulation(networkId, curr);
  }

  @Override
  protected void onFlowDelete(final String networkId, final Flow flow) {

    // Stats update for Generator
    stats.onFlowDelete();
    if (dump) {
      Util.dumpFlow("onFlowDelete", flow);
    }

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());
    if (targetFlow == null) {
      return;
    }

    if (targetFlow.getStatus().equals(FlowObject.FlowStatus.ESTABLISHED.toString())
        && targetFlow.getEnabled()) {
      targetFlow.setStatus(FlowObject.FlowStatus.TEARDOWN.toString());

      // Driver needs to delete Flow to physical switch here.
      // Deleting of Flow After completing the physical switch,
      // to "None".

      targetFlow.setStatus(FlowObject.FlowStatus.NONE.toString());
      networkIf.putFlow(targetFlow);

    }
  }

  @Override
  protected final void onOutPacketAdded(final String networkId, final OutPacketAdded msg) {

    String packetId = msg.getId();
    // log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "receive OutPacket: {}", packetId));

    NetworkInterface networkIf = networkInterfaces().get(networkId);

    OutPacket outPacket = networkIf.getOutPacket(packetId);
    OFPFlowMatch header = (OFPFlowMatch) (outPacket.getHeader());
    stats.onOutPacketAdded_(outPacket);
    if (dump) {
      Util.dump("onOutPacketAdded", objectId, packetId, header.getInNode(), header.getInPort(),
          header.getEthSrc(), header.getEthDst());
    }

    // These methods run asynchronously in a thread pool
    postInPacketBackToSender(header, outPacket);
    postOutPacketToAdjacentNetwork(header, outPacket);
    if (deleteOnOutPacketAdded) {
      deleteOutPacket(networkIf, packetId);
    }
  }

  private void deleteOutPacket(final NetworkInterface networkIf, final String packetId) {
    executorOutPacketHandling.execute(new Runnable() {
      public void run() {
        try {
          Response resp = networkIf.delOutPacket(packetId);
          if (resp.isError("DELETE")) {
            log.error("invalid DELETE Packet:{}", resp.statusCode);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Posts InPacket back to the originating node to establish a flow.
   * 
   * This method sends back a packet with dlSrc/dlDst reversed to the originating host
   * to establish a flow.
   * 
   * To release the onEvent() thread, this function executes the procedure in another
   * ThreadPoolExecutor.
   */
  private void postInPacketBackToSender(final OFPFlowMatch header, final OutPacket outPacket) {
    if (outPacket.getData()[0] == Generator.GO) {
      executorOutPacketHandling.execute(new Runnable() {
        public void run() {
          String dlDst = header.getEthDst();
          String dlSrc = header.getEthSrc();
          String[] dlDstSplit = dlDst.split(":");
          int driver = -1;
          int node;
          int port;
          if (hasAdjacencies) {
            driver = Integer.parseInt(dlDstSplit[0], 16);
            node = Integer.parseInt(dlDstSplit[1], 16);
            port = Integer.parseInt(dlDstSplit[2], 16);
          } else {
            node = Integer.parseInt(dlDstSplit[0] + dlDstSplit[1], 16);
            port = Integer.parseInt(dlDstSplit[2], 16);
          }
          OFPInPacket inPacket = new OFPInPacket();
          String inNode = String.format(env.nodeFormat(), node);
          if (!hasAdjacencies || hasAdjacencies
              && String.format(env.driverFormat(), driver).equals(objectId)) {
            String inPort = String.format(env.portFormat(), port);
            OFPFlowMatch header = new OFPFlowMatch();
            header.setInNode(inNode);
            header.setInPort(inPort);
            header.setEthDst(dlSrc); // DlSrc => DlDst
            header.setEthSrc(dlDst); // DlDst => DlSrc
            String packetId = "*" + outPacket.getPacketId();
            inPacket.setPacketId(packetId);
            inPacket.setHeader(header);
            int timestamp = PostInPacketGenerator.currentTimeMills();
            byte[] data = outPacket.getData();
            byte[] originalData = ByteBuffer.wrap(data, 9, data.length - 9).array();
            byte[] payload = PostInPacketGenerator.createPayload(Generator.BACK, timestamp,
                timestamp, originalData);
            inPacket.setData(payload);
            inPacket.setNodeId(inNode);
            inPacket.setPortId(inPort);

            NetworkInterface networkIf = networkInterfaces().get(network);
            Response resp = networkIf.postInPacket(inPacket);

            if (dump) {
              Util.dump("postInPacketBackToSender", objectId, packetId, inNode, inPort, dlDst,
                  dlSrc, resp.statusCode); // dlSrc <=> dlDst
            }

            stats.postInPacket();
          } else {
            // No opeartion
          }
        }
      }

          );
    }
  }

  /**
   * Posts OutPacket to an adjacent federated network.
   * 
   * To release the onEvent() thread, this function executes the procedure in
   * another ThreadPoolExecutor.
   */
  private void postOutPacketToAdjacentNetwork(final OFPFlowMatch header, final OutPacket outPacket) {
    if (hasAdjacencies) {
      executorOutPacketHandling.execute(new Runnable() {
        private void postOutPacketToAdjacentNetwork(String gwNode, String gwPort, String adjDriver,
            String adjNode, String adjPort, OutPacket outPacket) {

          OFPInPacket inPacket = new OFPInPacket();
          header.setInNode(adjNode);
          header.setInPort(adjPort);
          inPacket.setPacketId(outPacket.getPacketId());
          inPacket.setHeader(header);
          inPacket.setData(outPacket.getData());
          inPacket.setNodeId(adjNode);
          inPacket.setPortId(adjPort);
          try {
            Response resp = request(adjDriver, Method.POST,
                String.format("in_packet/%s/%s", adjNode, adjPort), LogMessage.getSavedTxid(), inPacket);
            Util.dump(
                String.format("postOutPacketToAdjacentNetwork: /in_packet/%s/%s", adjNode, adjPort),
                objectId, inPacket.getPacketId(), adjNode, adjPort, header.getEthSrc(),
                header.getEthDst(), resp.statusCode);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        public void run() {
          log.debug("run() in postOutPacketToAdjacentNetwork");
          String inNode = header.getInNode();
          String inPort = header.getInPort();
          String outNode = outPacket.getNodeId();
          List<String> outPorts = outPacket.getPorts();
          List<String> exceptOutPorts = outPacket.getExceptPorts();

          boolean broadcast = (outPorts.size() == 0) ? true : false;
          int[][] adjacencies = env.getAdjacencies();
          String nodeFormat = env.nodeFormat();
          String portFormat = env.portFormat();
          String driverFormat = env.driverFormat();

          if (broadcast) { // Broadcast OutPacket to all the ports in adjacencies excluding
                           // inNode/inPort
            log.debug("broadcast");
            for (int[] adjacency : adjacencies) {
              String gwNode = String.format(nodeFormat, adjacency[0]);
              String gwPort = String.format(portFormat, adjacency[1]);
              String adjDriver = String.format(driverFormat, adjacency[2]);
              String adjNode = String.format(nodeFormat, adjacency[3]);
              String adjPort = String.format(portFormat, adjacency[4]);
              log.debug("gwNode:{}", gwNode);
              log.debug("gwPort:{}", gwPort);
              log.debug("inNode:{}", inNode);
              log.debug("inPort:{}", inPort);
              log.debug("adjDriver:{}", adjDriver);
              log.debug("adjNode:{}", adjNode);
              log.debug("adjPort:{}", adjPort);
              if (gwNode.equals(inNode) && gwPort.equals(inPort)) {
                // skip
              } else if (exceptOutPorts.contains(inPort)) {
                System.out.println("exceptOutPorts: " + inPort);
                // log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "exceptOutPorts: {}", inPort));
                // skip
              } else {
                log.debug("postOutPacket");
                postOutPacketToAdjacentNetwork(gwNode, gwPort, adjDriver, adjNode, adjPort,
                    outPacket);
              }
            }
          } else { // OutPacket to a specific port
            log.debug("specific ports");
            for (String outPort : outPorts) {
              for (int[] adjacency : adjacencies) {
                String gwNode = String.format(nodeFormat, adjacency[0]);
                String gwPort = String.format(portFormat, adjacency[1]);
                // if (!gwNode.equals(outNode) && !gwPort.equals(outPort)) {
                if (gwNode.equals(outNode) && gwPort.equals(outPort)) {
                  log.debug("postOutPacket");
                  String adjDriver = String.format(driverFormat, adjacency[2]);
                  String adjNode = String.format(nodeFormat, adjacency[3]);
                  String adjPort = String.format(portFormat, adjacency[4]);
                  postOutPacketToAdjacentNetwork(gwNode, gwPort, adjDriver, adjNode, adjPort,
                      outPacket);
                }
              }
            }
          }
        }
      });
    }
  }

  /**
   * Posts InPacket from an adjacent federated network to an network component. 
   */
  private Response postInPacketFromAdjacentNetwork(String inNode, String inPort,
      OFPInPacket inPacket) {

    NetworkInterface networkIf = networkInterfaces().get(network);
    Response resp = networkIf.postInPacket(inPacket);

    OFPFlowMatch header = (OFPFlowMatch) inPacket.getHeader();
    Util.dump("postInPacketFromAdjacentNetwork", objectId, inPacket.getPacketId(), inNode, inPort,
        header.getEthSrc(), header.getEthDst(), resp.statusCode);

    stats.postInPacket();
    return new Response(Response.OK, null);
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  @Override
  protected BasicFlow getFlow(final NetworkInterface nwIf, final String flowId) {
    log.debug(">> {}", new Throwable().getStackTrace()[0].getMethodName());

    if (nwIf == null || flowId == null) {
      return null;
    }
    BasicFlow flow = (BasicFlow) nwIf.getFlow(flowId);
    if (flow == null || flow.getMatches() == null || flow.getMatches().size() == 0) {
      return null;
    }

    return flow;
  }

}
