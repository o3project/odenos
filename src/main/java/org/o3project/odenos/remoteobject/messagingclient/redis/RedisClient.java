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

package org.o3project.odenos.remoteobject.messagingclient.redis;


import static redis.clients.jedis.Protocol.Command.CLIENT;
import static redis.clients.jedis.Protocol.Command.EVALSHA;
import static redis.clients.jedis.Protocol.Command.PSUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.PUBLISH;
import static redis.clients.jedis.Protocol.Command.PUBSUB;
import static redis.clients.jedis.Protocol.Command.PUNSUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.SCRIPT;
import static redis.clients.jedis.Protocol.Command.SET;
import static redis.clients.jedis.Protocol.Command.SUBSCRIBE;
import static redis.clients.jedis.Protocol.Command.UNSUBSCRIBE;
import static redis.clients.jedis.Protocol.DEFAULT_TIMEOUT;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.Protocol.Command;
import redis.clients.jedis.Protocol;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;
import redis.clients.util.SafeEncoder;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>Redis client base class.</h1>
 *
 * <p>
 * This class supports non-blocking (asynchronous) Redis messaging:
 * <ul>
 * <li>PUBLISH
 * <li>SUBSCRIBE
 * <li>PSUBSCRIBE
 * <li>PUBSUB channels
 * <li>PUBSUB numsub channel
 * <li>EVALSHA sha1 arguments
 * <li>Asynchronous event receiving
 * </ul>
 */
public class RedisClient implements Closeable {

  private static final Logger log = LoggerFactory.getLogger(RedisClient.class);

  private static final byte[] LIST = "list".getBytes();
  private static final byte[] SETNAME = "setname".getBytes();
  private static final byte[] CHANNELS = "channels".getBytes();
  private static final byte[] NUMSUB = "numsub".getBytes();
  private static final byte[] EX = "EX".getBytes();
  private static final byte[] LOAD = "load".getBytes();
  private static final byte[] EXISTS = "exists".getBytes();

  private Socket socket; // Java socket
  private RedisOutputStream outputStream;
  private RedisInputStream inputStream;

  private int timeout = DEFAULT_TIMEOUT;
  private boolean keepAlive = true;

  /**
   * Constructor.
   *
   * <p>
   * Set keepAlive false if you want to disable TCP keep alive.
   */
  public RedisClient() {
    this.keepAlive = false;
  }

  /**
   * Constructor.
   *
   * <p>
   * Set keepAlive false if you want to disable TCP keep alive.
   *
   * @param keepAlive TCP keep alive
   */
  public RedisClient(final boolean keepAlive) {
    this.keepAlive = keepAlive;
  }

  /**
   * Connects to Redis server
   *
   * <p>
   * Since this connection is used for pubsub only, this method
   * sets the socket timeout to infinite.
   *
   * <p>
   * TCP keep alive may be disabled by setting keepAlive false
   * when instantiating this class using the constructor method.
   *
   * @param host Redis server host name or IP address
   * @param port Redis server port number
   */
  public void connect(String host, int port) {
    if (!isConnected()) {
      try {
        socket = new Socket();
        socket.setReuseAddress(true);
        socket.setKeepAlive(keepAlive);
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 0);
        socket.setSoTimeout(0); // Default: infinite
        socket.connect(new InetSocketAddress(host, port), timeout);
        outputStream = new RedisOutputStream(socket.getOutputStream());
        inputStream = new RedisInputStream(socket.getInputStream());
        if (log.isDebugEnabled()) {
          log.debug("host: {}, port: {}", host, port);
        }
      } catch (IOException e) {
        throw new JedisConnectionException(e);
      }
    }
  }

  /**
   * Sends a Redis command
   *
   * <p>
   * This method first establishes a TCP connection to Redis server.
   * The TCP connection can be kept alive by setting keepAlive true
   * when instantiating this class (its default is true).
   * @param command Redis command
   * @param args command arguments
   */
  public void sendCommand(final Command command, final byte[]... args) {
    Protocol.sendCommand(outputStream, command, args);
    try {
      outputStream.flush();
    } catch (IOException e) {
      throw new JedisConnectionException(e);
    }
  }

  /**
   * Sends a Redis command.
   *
   * <p>
   * @param cmd Redis command
   */
  public void sendCommand(final Command cmd) {
    sendCommand(cmd, new byte[0][]);
  }


  /**
   * Closes the TCP connection and the pair of Redis streams.
   */
  @Override
  public void close() {
    if (isConnected()) {
      try {
        inputStream.close();
        outputStream.close();
        if (!socket.isClosed()) {
          socket.close();
        }
      } catch (IOException ex) {
        throw new JedisConnectionException(ex);
      }
    }
  }

  /**
   * Checks if connected to Redis server.
   *
   * <p>
   * @return true if connected, false if not connected
   */
  public boolean isConnected() {
    return socket != null && socket.isBound() && !socket.isClosed()
        && socket.isConnected() && !socket.isInputShutdown()
        && !socket.isOutputShutdown();
  }

  /**
   * Subscribes channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   *
   * @param channels channels to be subscribed
   */
  public synchronized void subscribe(final byte[]... channels) {
    sendCommand(SUBSCRIBE, channels);
  }

  /**
   * Subscribes channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   *
   * @param patterns pattern to match channels to be subscribed to
   */
  public synchronized void psubscribe(final byte[]... patterns) {
    sendCommand(PSUBSCRIBE, patterns);
  }

  /**
   * "Unsubscribes all the channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   */
  public synchronized void unsubscribe() {
    sendCommand(UNSUBSCRIBE);
  }

  /**
   * "Unsubscribes all the channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   */
  public synchronized void punsubscribe() {
    sendCommand(PUNSUBSCRIBE);
  }

  /**
   * Unsubscribe specific channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   *
   * @param channels channels
   */
  public synchronized void unsubscribe(final byte[]... channels) {
    sendCommand(UNSUBSCRIBE, channels);
  }

  /**
   * Unsubscribe specific channels.
   *
   * <p>
   * Note: An application thread calls this method directly,
   *
   * @param patterns pattern to match channels to be unsubscribe from
   */
  public synchronized void punsubscribe(final byte[]... patterns) {
    sendCommand(PUNSUBSCRIBE, patterns);
  }

  /**
   * Publishes a message to a channel.
   *
   * @param channel target channel
   * @param message message to be published
   */
  public void publish(final byte[] channel, final byte[] message) {
    sendCommand(PUBLISH, channel, message);
  }

  /**
   * Issues "PUBSUB channels" command to Redis server.
   */
  public void pubsubChannels() {
    sendCommand(PUBSUB, CHANNELS);
  }

  /**
   * Issues "PUBSUB numsub channel" command to Redis server.
   *
   * @param channel channel to be checked
   */
  public void pubsubNumsub(final byte[] channel) {
    sendCommand(PUBSUB, NUMSUB, channel);
  }

  /**
   * Issues "SET key value" command to Redis server.
   *
   * @param key key
   * @param value value
   */
  public void set(final byte[] key, final byte[] value) {
    sendCommand(SET, key, value);
  }

  /**
   * Issues "SET key value EX timeout" command to Redis server.
   *
   * @param key key
   * @param value value
   * @param time timeout
   */
  public void set(final byte[] key, final byte[] value, final long time) {
    sendCommand(SET, key, value, EX, Protocol.toByteArray(time));
  }

  /**
   * Issues "CLIENT setname name" command to Redis server.
   *
   * @param name client name
   */
  public void setClientName(final byte[] name) {
    sendCommand(CLIENT, SETNAME, name);
  }

  /**
   * Issues "CLIENT list" command to Redis server.
   */
  public void getClientList() {
    sendCommand(CLIENT, LIST);
  }

  /**
   * Issues "SCRIPT exists" command to Redis server.
   */
  public void scriptExists(final byte[] sha1hash) {
    sendCommand(SCRIPT, EXISTS, sha1hash);
  }

  /**
   * Issues "SCRIPT load" command to Redis server.
   */
  public void scriptLoad(final byte[] script) {
    sendCommand(SCRIPT, LOAD, script);
  }

  /**
   * Issues "EVALSHA" command to Redis server.
   */
  public void evalsha(final byte[]... argv) {
    sendCommand(EVALSHA, argv);
  }

  /**
  * Reads a reply as status code from input stream .
  *
  * @return status code
  */
  public String getStatusCodeReply() {
    final byte[] resp = (byte[]) Protocol.read(inputStream);
    if (null == resp) {
      return null;
    } else {
      return SafeEncoder.encode(resp);
    }
  }

  /**
   * Reads a reply as integer from input stream.
   *
   * <p>
   * Redis server returns this value as ACK for PUBLISH.
   * The value means the number of subscribers receiving
   * the published data.
   *
   * @return integer value
   */
  public Long readIntegerFromInputStream() {
    return (Long) Protocol.read(inputStream);
  }

  /**
   * Low-level read command.
   *
   * @return Object
   */
  public Object read() {
    return Protocol.read(inputStream);
  }

  /**
   * Reads a reply as raw object list from input stream.
   *
   * <p>
   * Redis server returns this list as Redis messages
   *
   * @return raw object list
   * @throws JedisConnectionException read failure
   */
  @SuppressWarnings("unchecked")
  public List<Object> readObjectListFromInputStream()
      throws JedisConnectionException {
    return (List<Object>) Protocol.read(inputStream);
  }

  /**
   * Reads a reply as a response to "PUBSUB numsub channel".
   *
   * @return raw object list
   */
  public long readPubsubNumsubReply(String channel)
      throws ProtocolException {
    List<Object> reply = readObjectListFromInputStream();
    byte[] channelReturendBytes = (byte[]) reply.get(0);
    byte[] subscribersBytes = (byte[]) reply.get(1);
    String channelReturned = new String(channelReturendBytes);
    long subscribers = Long.parseLong(new String(subscribersBytes));
    if (channelReturned.equals(channel)) {
      return subscribers;
    } else {
      throw new ProtocolException("channel mismatch");
    }
  }

  /**
   * Reads a reply as a response to "CLIENT list".
   *
   * @return a list of Redis clients
   */
  public List<String> readGetClientListReply() {
    String reply = new String((byte[]) read());
    StringTokenizer tokenizer = new StringTokenizer(reply, "\n");
    List<String> list = new ArrayList<String>();
    while (tokenizer.hasMoreElements()) {
      list.add(tokenizer.nextToken());
    }
    return list;
  }
}
