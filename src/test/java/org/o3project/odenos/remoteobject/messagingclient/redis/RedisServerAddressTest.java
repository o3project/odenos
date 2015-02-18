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

import org.junit.Test;

public class RedisServerAddressTest {
  
  private static final String HOST = "main";
  private static final int PORT = 1111;
  private static final String HOST_B = "sub";
  private static final int PORT_B = 2222;
  
  RedisServerAddress redisServerAddress;
  
  @Test
  public final void testNext() {
    redisServerAddress =
        new RedisServerAddress(HOST, PORT, HOST_B, PORT_B);
    assertThat(redisServerAddress.getSessionId(), is(0));
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
    redisServerAddress.next();
    assertThat(redisServerAddress.getSessionId(), is(1));
    assertThat(redisServerAddress.getHost(), is(HOST_B));
    assertThat(redisServerAddress.getPort(), is(PORT_B));
    redisServerAddress.next();
    assertThat(redisServerAddress.getSessionId(), is(2));
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
  }

  @Test
  public final void testNextMainOnly() {
    redisServerAddress =
        new RedisServerAddress(HOST, PORT, null, 0);
    assertThat(redisServerAddress.getSessionId(), is(0));
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
    redisServerAddress.next();
    assertThat(redisServerAddress.getSessionId(), is(1));
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
    redisServerAddress.next();
    assertThat(redisServerAddress.getSessionId(), is(2));
    assertThat(redisServerAddress.getHost(), is(HOST));
    assertThat(redisServerAddress.getPort(), is(PORT));
  }
}