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

import org.o3project.odenos.core.component.network.flow.Flow;

/**
 * Utility for the package. 
 */
public class Util {

  protected static final synchronized void dump(String title, String objectId, String packetId,
      String inNode, String inPort, String dlSrc, String dlDst, int statusCode) {
    System.out.println("--- " + title + " ---");
    System.out.println("objectId: " + objectId);
    System.out.println("packetId: " + packetId);
    System.out.println("inNode: " + inNode);
    System.out.println("inPort: " + inPort);
    System.out.println("dlSrc: " + dlSrc);
    System.out.println("dlDst: " + dlDst);
    System.out.println("statusCode: " + statusCode);
  }

  protected static final synchronized void dump(String title, String objectId, String packetId,
      String inNode, String inPort, String dlSrc, String dlDst) {
    System.out.println("--- " + title + " ---");
    System.out.println("objectId: " + objectId);
    System.out.println("packetId: " + packetId);
    System.out.println("inNode: " + inNode);
    System.out.println("inPort: " + inPort);
    System.out.println("dlSrc: " + dlSrc);
    System.out.println("dlDst: " + dlDst);
  }

  protected static final synchronized void dumpFlow(String title, Flow flow) {
    System.out.println("--- " + title + " ---");
    System.out.println(flow.toString());
  }
}
