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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.o3project.odenos.remoteobject.messagingclient.redis.RedisClient;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PowerMockIgnore("org.apache.log4j.*")
@RunWith(PowerMockRunner.class)
public class RedisClientTest {

  private static final String HOST = "localhost";
  private static final int PORT = 6379;

  RedisClient subscriber = null;
  RedisClient publisher = null;

  private static boolean skip = true;

  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }

  @Before
  public final void setUp() {
    if (!skip) {
      subscriber = new RedisClient();
      publisher = new RedisClient();
      subscriber.connect(HOST, PORT);
      publisher.connect(HOST, PORT);
    }
  }

  @After
  public void tearDown() throws Exception {
    if (!skip) {
      subscriber.close();
      publisher.close();
    }
  }

  @Test
  public final void testClientHostWithTryResources() {
    if (!skip) {
      try (RedisClient target = new RedisClient(true)) {
        target.connect(HOST, PORT);
        Socket socket = Whitebox.getInternalState(target, "socket");
        String host = socket.getInetAddress().getHostName();
        int port = socket.getPort();
        boolean keepAlive = false;
        try {
          keepAlive = socket.getKeepAlive();
        } catch (SocketException e) {
          e.printStackTrace();
        }
        assertThat(host, is(HOST));
        assertThat(port, is(PORT));
        assertThat(keepAlive, is(true));
      }
    }
  }

  @Test
  public final void testConnect() {
    if (!skip) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Socket subscriberSocket = (Socket) Whitebox.getInternalState(subscriber, "socket");
      Socket publisherSocket = (Socket) Whitebox.getInternalState(publisher, "socket");
      assertNotNull(subscriberSocket);
      assertTrue(subscriberSocket.isConnected());
      assertNotNull(publisherSocket);
      assertTrue(publisherSocket.isConnected());
    }
  }

  @Test
  public final void testClose() {
    if (!skip) {
      RedisClient subscriber = new RedisClient();
      RedisClient publisher = new RedisClient();
      subscriber.connect(HOST, PORT);
      publisher.connect(HOST, PORT);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      subscriber.close();
      publisher.close();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Socket subscriberSocket = (Socket) Whitebox.getInternalState(subscriber, "socket");
      Socket publisherSocket = (Socket) Whitebox.getInternalState(publisher, "socket");
      assertTrue(subscriberSocket.isClosed());
      assertTrue(publisherSocket.isClosed());
      subscriber.close();
      publisher.close();
    }
  }

  @Test
  public final void testPubSubUnsub() {
    if (!skip) {
      // Send a PUBLISH message
      publisher.publish("kyoto".getBytes(), "Ookini!".getBytes());
      assertThat(publisher.readIntegerFromInputStream(), is(0L));
      // Send a SUBSCRIBE message to subscribe "kyoto"
      subscriber.subscribe("kyoto".getBytes());
      // Send a PUBLISH message again
      publisher.publish("kyoto".getBytes(), "Ookini!".getBytes());
      assertThat(publisher.readIntegerFromInputStream(), is(1L));
      // Send an UNSUBSCRIBE message
      subscriber.unsubscribe("kyoto".getBytes());
      // Send a PUBLISH message again
      publisher.publish("kyoto".getBytes(), "Ookini!".getBytes());
      assertThat(publisher.readIntegerFromInputStream(), is(0L));
    }
  }

  @Test
  public final void testPubSubNumsub() {
    if (!skip) {
      RedisClient subscriber1 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      RedisClient subscriber2 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      RedisClient subscriber3 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      subscriber1.subscribe("kyoto".getBytes());
      subscriber2.subscribe("kyoto".getBytes());
      subscriber3.subscribe("kyoto".getBytes());
      publisher.pubsubNumsub("kyoto".getBytes());
      long num = 0;
      try {
        num = publisher.readPubsubNumsubReply("kyoto");
      } catch (ProtocolException e) {
        e.printStackTrace();
      }
      assertThat(num, is(3L));
      subscriber1.close();
      subscriber2.close();
      subscriber3.close();
    }
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public final void testPubSubNumsubProtocolException()
      throws ProtocolException {
    if (!skip) {
      RedisClient subscriber1 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      RedisClient subscriber2 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      RedisClient subscriber3 = new RedisClient();
      subscriber1.connect(HOST, PORT);
      subscriber1.subscribe("kyoto".getBytes());
      subscriber2.subscribe("kyoto".getBytes());
      subscriber3.subscribe("kyoto".getBytes());
      publisher.pubsubNumsub("kyoto".getBytes());
      exception.expect(ProtocolException.class);
      @SuppressWarnings("unused")
      long num = publisher.readPubsubNumsubReply("tokyo");
      subscriber1.close();
      subscriber2.close();
      subscriber3.close();
    }
  }

  @Test
  public final void testScriptLoadAndScriptExists() throws NoSuchAlgorithmException {
    if (!skip) {
      byte[] script =
          "redis.call('publish', KEYS[1]..'@bridge', ARGV[1]) ; redis.call('publish', KEYS[1], ARGV[1])"
              .getBytes();
      publisher.scriptLoad(script);
      byte[] sha1hash = calcSha1(script);
      assertThat(publisher.getStatusCodeReply(), is(new String(sha1hash)));
      publisher.scriptExists(sha1hash);
      List<Object> list = publisher.readObjectListFromInputStream();
      Long exists = (Long) list.get(0);
      assertThat(exists, is(1L));
      sha1hash = "0000000000000000000000000000000000000000".getBytes();
      publisher.scriptExists(sha1hash);
      list = publisher.readObjectListFromInputStream();
      exists = (Long) list.get(0);
      assertThat(exists, is(0L));
    }
  }
  
  private static byte[] calcSha1(byte[] script) throws NoSuchAlgorithmException {
      StringBuffer stringBuffer = new StringBuffer();
      MessageDigest messageDigest;
      messageDigest = MessageDigest.getInstance("SHA-1");
      byte[] result = messageDigest.digest(script);
      for (int i = 0; i < result.length; i++) {
        stringBuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
      }
      return stringBuffer.toString().getBytes();
  }
}
