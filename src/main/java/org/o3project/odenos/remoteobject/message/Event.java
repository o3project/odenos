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

package org.o3project.odenos.remoteobject.message;

import org.apache.commons.lang.StringUtils;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

/**
 * Base class of event data.
 *
 */
public class Event extends MessageBodyUnpacker {

  private static final int MSG_NUM = 3;
  public String publisherId;
  public String eventType;

  /**
   * Constructor.
   * @param publisherId publisher ID.
   * @param eventType type of events.
   * @param body Contents of events.
   */
  public Event(String publisherId, String eventType, Object body) {
    this.publisherId = publisherId;
    this.eventType = eventType;
    this.body = body;
  }

  /**
   * Constructor.
   */
  public Event() {
  }

  /**
   * Set a publisher ID.
   * @return publisherId Publisher ID.
   */
  public String getPublisherId() {
    return publisherId;
  }

  /**
   * Returns a Publisher ID.
   * @param publisherId Publisher ID.
   */
  public void setPublisherId(String publisherId) {
    this.publisherId = publisherId;
  }

  /**
   * Set a type of events.
   * @return eventType type of events.
   */
  public String getEventType() {
    return eventType;
  }

  /**
   * Returns a type of events..
   * @param eventType type of events.
   */
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  @Override
  public void readFrom(Unpacker unpacker) throws IOException {
    unpacker.readArrayBegin();
    publisherId = unpacker.readString();
    eventType = unpacker.readString();
    bodyValue = unpacker.readValue();
    unpacker.readArrayEnd();
  }

  @Override
  public void writeTo(Packer packer) throws IOException {
    packer.writeArrayBegin(MSG_NUM);
    packer.write(publisherId);
    packer.write(eventType);
    if (bodyValue != null) {
      packer.write(bodyValue);
    } else {
      packer.write(body);
    }
    packer.writeArrayEnd();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    StringBuilder sb = new StringBuilder();
    sb.append(publisherId);
    sb.append("::");
    sb.append(eventType);

    String ids = sb.toString();

    return ids.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Event)) {
      return false;
    }

    Event event = (Event) obj;

    if (StringUtils.equals(event.getPublisherId(), this.publisherId)
        && (StringUtils.equals(event.getEventType(), this.eventType))) {
      return true;
    }

    return false;
  }
}
