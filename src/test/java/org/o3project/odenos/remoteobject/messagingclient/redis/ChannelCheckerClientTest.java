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

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.o3project.odenos.remoteobject.messagingclient.redis.ChannelCheckerClient;
import org.o3project.odenos.remoteobject.messagingclient.redis.RedisClient;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore("org.apache.log4j.*")
@RunWith(PowerMockRunner.class)
public class ChannelCheckerClientTest {

  private static final String HOST = "localhost";
  private static int PORT = 6379;

  private static boolean skip = true;
  
  private RedisServerAddress redisServerAddress = new RedisServerAddress(HOST, PORT, null, 0);
  
  @BeforeClass
  public static void setUpBeforeClass() {
    skip = (System.getProperty("testWithRedis") == null) ? true : false;
    System.out.println("skip: " + skip);
  }
  
  @Test
  public final void testChannelExist() {
    if (!skip) {
    ChannelCheckerClient target = new ChannelCheckerClient(redisServerAddress);
    RedisClient redisClient = new RedisClient();
    redisClient.connect(HOST, PORT);
    redisClient.subscribe("tokyo".getBytes());
    assertTrue(target.channelExist("tokyo"));
    target.close();
    redisClient.close();
    }
  }

  @Test
  public final void testChannelExistForNonExistentChannel() {
    if (!skip) {
    ChannelCheckerClient target = new ChannelCheckerClient(redisServerAddress);
    RedisClient redisClient = new RedisClient();
    redisClient.connect(HOST, PORT);
    redisClient.subscribe("kyoto".getBytes());
    assertFalse(target.channelExist("tokyo"));
    target.close();
    redisClient.close();
    }
  }

}
