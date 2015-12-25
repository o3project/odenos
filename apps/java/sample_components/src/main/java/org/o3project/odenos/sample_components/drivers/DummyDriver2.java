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

package org.o3project.odenos.sample_components.drivers;

import org.o3project.odenos.core.component.Driver;
import org.o3project.odenos.core.component.NetworkInterface;
import org.o3project.odenos.core.component.network.flow.Flow;
import org.o3project.odenos.core.component.network.flow.FlowObject;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlow;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.o3project.odenos.core.component.network.flow.ofpflow.OFPFlowMatch;
import org.o3project.odenos.core.component.network.packet.Packet;
import org.o3project.odenos.core.component.network.packet.InPacket;
import org.o3project.odenos.core.component.network.packet.InPacketAdded;
import org.o3project.odenos.core.component.network.packet.OutPacketAdded;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.core.manager.system.ComponentConnectionLogicAndNetwork;
import org.o3project.odenos.core.manager.system.event.ComponentConnectionChanged;
import org.o3project.odenos.remoteobject.RequestParser;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Request.Method;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.message.LogMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;

/**
 * DummyDriver2 class.
 *
 */
public class DummyDriver2 extends Driver {
  private static final Logger log = LogManager.getLogger(DummyDriver2.class);
  private static final String description = "dummy driver 2";
  private String network = null;
  private Timer timer = null;
  private long packetIn_timer_start = 10000;	// msec
  private long packetIn_timer_interval = 5000;	// msec
  private int packetIn_timer_times = 10;
  protected final RequestParser<IActionCallback> parser;

  /**
   * Constructor.
   * @param objectId object id.
   * @param baseUri base URI.
   * @param dispatcher Message Dispatcher instance.
   * @throws Exception if parameter is wrong.
   * @deprecated @see #DummyDriver2(String, MessageDispatcher)
   */
  @Deprecated
  public DummyDriver2(
      final String objectId,
      final String baseUri,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, baseUri, dispatcher);
    parser = createParser();
    resetEventSubscription();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "created."));
  }

  /**
   * Constructor.
   * @param objectId object id.
   * @param dispatcher Message Dispatcher instance.
   * @throws Exception if parameter is wrong.
   */
  public DummyDriver2(
      final String objectId,
      final MessageDispatcher dispatcher) throws Exception {
    super(objectId, dispatcher);
    parser = createParser();
    resetEventSubscription();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "created."));
  }

  /**
   * Get Description of Component. Need to Implement at Inheritance Logic
   * Component.
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
  protected final boolean onConnectionChangedAddedPre(
      final ComponentConnectionChanged msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    if (!msg.curr().getObjectType()
        .equals(ComponentConnectionLogicAndNetwork.TYPE)) {
      return false;
    }
    String logicId = msg.curr().getProperty(
        ComponentConnectionLogicAndNetwork.LOGIC_ID);
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
  protected final void onConnectionChangedAdded(
      final ComponentConnectionChanged msg) {

    ComponentConnection curr = msg.curr();
    this.network = curr.getProperty(
        ComponentConnectionLogicAndNetwork.NETWORK_ID);
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called: {}", network));

    subscribeNetworkComponent();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.RUNNING);
    systemMngInterface().putConnection(curr);
  }

  // start dummy PacketIn
  private void startDummyPacketIn() {
    try {
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
      timer = new Timer("timer-dummyPacketIn");
      TimerTask task = new TimerTask() {
        private int cnt = 0;
        @Override
        public void run() {
          cnt++;
          dummyPacketIn(cnt);
          if (cnt >= packetIn_timer_times) {
            if (timer != null) {
              timer.cancel();
              timer = null;
            }
          }
        }
      };
      if ((packetIn_timer_interval <= 0) || (packetIn_timer_times <= 1)) {
        timer.schedule(task, packetIn_timer_start);
      } else {
        timer.schedule(task, packetIn_timer_start, packetIn_timer_interval);
      }
    } catch(Exception ex) {
    }
  }

  @Override
  protected final void onConnectionChangedDelete(
      final ComponentConnectionChanged message) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    ComponentConnection curr = message.curr();
    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.FINALIZING);
    systemMngInterface().putConnection(curr);

    unsubscribeNetworkComponent();
    this.network = null;

    if (timer != null) {
      timer.cancel();
      timer = null;
    }

    // Changed ConectionProperty's status.
    curr.setConnectionState(ComponentConnection.State.NONE);
    systemMngInterface().putConnection(curr);
  }

  private void subscribeNetworkComponent() {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    addEntryEventSubscription(FLOW_CHANGED, this.network);
    addEntryEventSubscription(OUT_PACKET_ADDED, this.network);
    // addEntryEventSubscription(IN_PACKET_ADDED, this.network);

    updateEntryEventSubscription(FLOW_CHANGED, this.network, null);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  private void unsubscribeNetworkComponent() {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));
    removeEntryEventSubscription(FLOW_CHANGED, this.network);
    removeEntryEventSubscription(OUT_PACKET_ADDED, this.network);
    // removeEntryEventSubscription(IN_PACKET_ADDED, this.network);

    try {
      applyEventSubscription();
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  // //////////////////////////////////////////////////
  // Event method override
  // //////////////////////////////////////////////////

  private RequestParser<IActionCallback> createParser() {
    log.debug("");

    return new RequestParser<IActionCallback>() {
      {
        addRule(Method.POST,
            "settings/packet_in",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                Map<String, String> settings = null;
                try {
                  settings = parsed.getRequest().getBodyAsStringMap();
                } catch (Exception ex) {
                  log.error("illegal data: {}", parsed.getRequest().getBodyValue());
                  return new Response(Response.BAD_REQUEST, "Error illegal data");
                }
                return postPacketInSetting(settings);
              }
            });
        addRule(Method.PUT,
            "settings/packet_in/<setting_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                Map<String, String> settings = null;
                try {
                  settings = parsed.getRequest().getBodyAsStringMap();
                } catch (Exception ex) {
                  log.error("illegal data: {}", parsed.getRequest().getBodyValue());
                  return new Response(Response.BAD_REQUEST, "Error illegal data");
                }
                return putPacketInSetting(
                    parsed.getParam("setting_id"), settings);
              }
            });
        addRule(Method.GET,
            "settings/packet_in/<setting_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return getPacketInSetting(parsed.getParam("setting_id"));
              }
            });
        addRule(Method.DELETE,
            "settings/packet_in/<setting_id>",
            new IActionCallback() {
              @Override
              public Response process(
                  final RequestParser<IActionCallback>.ParsedRequest parsed)
                  throws Exception {
                return deletePacketInSetting(parsed.getParam("setting_id"));
              }
            });
      }
    };
  }

  protected Response postPacketInSetting(Map<String, String> packetInSetting) {
    parsePacketInSetting(packetInSetting);
    if (timer == null) {
      startDummyPacketIn();
    }
    return new Response(Response.CREATED, generatePacketInSetting());
  }

  protected Response putPacketInSetting(String packetInSettingId, Map<String, String> packetInSetting) {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    parsePacketInSetting(packetInSetting);
    startDummyPacketIn();
    return new Response(Response.ACCEPTED, generatePacketInSetting());
  }

  protected Response getPacketInSetting(String packetInSettingId) {
    return new Response(Response.OK, generatePacketInSetting());
  }

  protected Response deletePacketInSetting(String packetInSettingId) {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    return new Response(Response.OK, null);
  }

  private void parsePacketInSetting(Map<String, String> packetInSetting) {
    String valueStr;
    long value;
    if (packetInSetting == null) {
      return;
    }
    if ((valueStr = packetInSetting.get("timer_start")) != null) {
      value = Long.valueOf(valueStr);
      if (value >= 0) {
        packetIn_timer_start = value;
      }
    }
    if ((valueStr = packetInSetting.get("timer_interval")) != null) {
      value = Long.valueOf(valueStr);
      if (value >= 0) {
        packetIn_timer_interval = value;
      }
    }
    if ((valueStr = packetInSetting.get("timer_times")) != null) {
      value = Long.valueOf(valueStr);
      if (value > 0) {
        packetIn_timer_times = (int)value;
      }
    }
  }

  private Map<String, String> generatePacketInSetting() {
    Map<String, String> packetInSetting = new HashMap<String, String>();
    packetInSetting.put("timer_start", String.valueOf(packetIn_timer_start));
    packetInSetting.put("timer_interval", String.valueOf(packetIn_timer_interval));
    packetInSetting.put("timer_times", String.valueOf(packetIn_timer_times));
    return packetInSetting;
  }

  @Override
  protected Response onRequest(Request request) {
    log.debug("received {}", request.path);

    Response res;
    try {
      RequestParser<IActionCallback>.ParsedRequest parsed =
          parser.parse(request);
      if (parsed == null) {
        res = new Response(Response.BAD_REQUEST, "Error unknown request");
        return res;
      }

      IActionCallback callback = parsed.getResult();
      if (callback == null) {
        res = new Response(Response.BAD_REQUEST, "Error illegal request");
        return res;
      }
      // Get response.
      res = callback.process(parsed);
      return res;
    } catch (Exception ex) {
      log.error("Error on processing request", ex);
      res = new Response(Response.BAD_REQUEST, "Error on processing request");
      return res;
    }
  }

  private void dummyPacketIn(int count) {
    String packetId;
    String nodeId = "node01";	// @@ see rest_DummyDriver2.sh
    String portId = "port010";	// @@ see rest_DummyDriver2.sh
    ByteBuffer packetData = ByteBuffer.allocate(100);
    packetData.put(("data4567890123456789012345678901"
      + "23456789012345678901234567890123").getBytes());	// 64 bytes
    packetData.putInt(0x10000001).putInt(0x10000002).putInt(0x10000003)
      .putInt(0x10000004).putInt(0x10000005).putInt(0x10000006)
      .putInt(0x10000007).putInt(0x10000008).putInt(count);	// 36 bytes

    if (network == "") {
      // not happen, perhaps, so setup at onConnectionChangedAdded()
      log.error("dummyPacketIn: internal error: count={}", count);
      return;
    }

    packetId = String.format("%010d", count - 1);
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(),
      "dummyPacketIn: packetId={}", packetId));
    BasicFlowMatch header = new BasicFlowMatch(nodeId, portId);
    NetworkInterface networkIf = networkInterfaces().get(network);

    Map<String, String> msgAttributes = new HashMap<>();
    InPacket packet = new InPacket(packetId, nodeId, portId, packetData.array(), msgAttributes, header);

    networkIf.postInPacket(packet);
  }

  @Override
  protected void onFlowAdded(
      final String networkId,
      final Flow flow) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "{} : {} ", networkId, flow));

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());
    if (targetFlow == null) {
      return;
    }

    // Status ... "None" => "Establishing" => "Established"
    if (targetFlow.getEnabled()) {
      if (targetFlow.getStatus().equals(FlowObject.FlowStatus.NONE.toString())) {
        targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHING.toString());
        networkIf.putFlow(targetFlow);
        targetFlow = getFlow(networkIf, flow.getFlowId());
      }

      if (targetFlow.getStatus().equals(FlowObject.FlowStatus.ESTABLISHING.toString())) {
        // Driver needs to set Flow to physical switch here.
        // Setting of Flow After completing the physical switch,
        // to "Established".
        log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "added Flow: network={}, flow=''{}''", networkId, targetFlow.toString()));

        targetFlow.setStatus(FlowObject.FlowStatus.ESTABLISHED.toString());
        networkIf.putFlow(targetFlow);
      }
    }
  }

  @Override
  protected final void onFlowUpdate(
      final String networkId,
      final Flow prev,
      final Flow curr,
      final ArrayList<String> attributesList) {

    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, curr.getFlowId());
    if (targetFlow == null) {
      return;
    }

    if (targetFlow.getEnabled()) {
      this.onFlowAdded(networkId, curr);
    } else {
      this.onFlowDelete(networkId, curr);
    }
  }

  @Override
  protected void onFlowDelete(
      final String networkId,
      final Flow flow) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "{} : {} ",networkId, flow));

    NetworkInterface networkIf = networkInterfaces().get(this.network);
    BasicFlow targetFlow = getFlow(networkIf, flow.getFlowId());
    if (targetFlow == null) {
      return;
    }

    if (!targetFlow.getEnabled()) {
      targetFlow.setStatus(FlowObject.FlowStatus.NONE.toString());
      networkIf.putFlow(targetFlow);
    } else {
      if (targetFlow.getStatus().equals(FlowObject.FlowStatus.ESTABLISHING.toString())
          || targetFlow.getStatus().equals(FlowObject.FlowStatus.ESTABLISHED.toString())) {
        targetFlow.setStatus(FlowObject.FlowStatus.TEARDOWN.toString());
        networkIf.putFlow(targetFlow);
        targetFlow = getFlow(networkIf, flow.getFlowId());
      }

      if (targetFlow.getStatus().equals(FlowObject.FlowStatus.TEARDOWN.toString())) {
        // Driver needs to delete Flow to physical switch here.
        // Deleting of Flow After completing the physical switch,
        // to "None".
        log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "deleted Flow: network={}", networkId));

        targetFlow.setStatus(FlowObject.FlowStatus.NONE.toString());
        networkIf.putFlow(targetFlow);
      }
    }
  }

  @Override
  protected void onInPacketAdded(
      final String networkId,
      final InPacketAdded msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    if (onInPacketAddedPre(networkId, msg)) {
      String packetId = msg.getId();
      log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "receive InPacket: packetId={}", packetId));
      msleep(100);		// @@ for DEBUG
      HashMap<String, Response> respList = conversion(networkId, msg);
      onInPacketAddedPost(networkId, msg, respList);
    }
  }

  @Override
  protected final void onOutPacketAdded(
      final String networkId,
      final OutPacketAdded msg) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    // GET Packet to Drop
    String packetId = msg.getId();
    log.info(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "receive OutPacket: {}", packetId));
    try {
      msleep(100);		// @@ for DEBUG
      NetworkInterface networkIf = networkInterfaces().get(networkId);
      Response resp = networkIf.delOutPacket(packetId);
      if (resp.isError("DELETE")) {
        log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "invalid DELETE Packet:{}", resp.statusCode));
      }
    } catch (Exception e) {
      log.error(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "Recieved Message Exception."), e);
    }
  }

  // //////////////////////////////////////////////////
  //
  // common method
  //
  // //////////////////////////////////////////////////

  @Override
  protected BasicFlow getFlow(
      final NetworkInterface nwIf,
      final String flowId) {
    log.debug(LogMessage.buildLogMessage(LogMessage.getSavedTxid(), "called"));

    if (nwIf == null || flowId == null) {
      return null;
    }
    BasicFlow flow = (BasicFlow) nwIf.getFlow(flowId);
    if (flow == null || flow.getMatches() == null
        || flow.getMatches().size() == 0) {
      return null;
    }

    return flow;
  }

  /** waiting function for DEBUG
   * msec milli seconds
   */
  private void msleep(Integer msec) {
    if(msec.longValue() <= 0) {
      return;
    }
    try {
      Thread.sleep(msec.longValue());
    } catch (Exception ex) {
      // ignored
    }
  }
}

