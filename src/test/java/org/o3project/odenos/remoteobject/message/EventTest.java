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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;
import org.powermock.reflect.Whitebox;

import java.io.IOException;

/**
 * 
 *
 */
public class EventTest {
  private Event target = null;
  private Object body = null;

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @Before
  public void setUp() throws Exception {
    body = Mockito.mock(Object.class);
    target = Mockito.spy(new Event("publisherId", "eventType", "txid", body));
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    body = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#Event(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
   */
  @Test
  public final void testEventStringStringObject() {
    target = Mockito.spy(new Event("publisherId", "eventType", "txid", body));

    assertThat(target.publisherId, is("publisherId"));
    assertThat(target.eventType, is("eventType"));
    assertThat(target.body, is(body));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#Event()}.
   */
  @Test
  public final void testEvent() {
    target = Mockito.spy(new Event());

    assertThat(target.publisherId, is(nullValue()));
    assertThat(target.eventType, is(nullValue()));
    assertThat(target.body, is(nullValue()));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#getPublisherId()}.
   */
  @Test
  public final void testGetPublisherId() {

    /*
     * test
     */
    String result = target.getPublisherId();

    /*
     * check
     */
    assertThat(result, is("publisherId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#setPublisherId(java.lang.String)}.
   */
  @Test
  public final void testSetPublisherId() {

    /*
     * test
     */
    target.setPublisherId("NewPublisherId");

    /*
     * check
     */
    String resultMember = Whitebox.getInternalState(target, "publisherId");
    assertThat(resultMember, is("NewPublisherId"));

    String resultGet = target.getPublisherId();
    assertThat(resultGet, is("NewPublisherId"));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#getEventType()}.
   */
  @Test
  public final void testGetEventType() {

    /*
     * test
     */
    String result = target.getEventType();

    /*
     * check
     */
    assertThat(result, is("eventType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#setEventType(String)}.
   */
  @Test
  public final void testSetEventType() {

    /*
     * test
     */
    target.setEventType("NewEventType");

    /*
     * check
     */
    String resultMember = Whitebox.getInternalState(target, "eventType");
    assertThat(resultMember, is("NewEventType"));

    String resultGet = target.getEventType();
    assertThat(resultGet, is("NewEventType"));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    doReturn("publisherId").doReturn("eventType").when(unpacker)
        .readString();

    target.readFrom(unpacker);

    verify(unpacker).readArrayBegin();
    verify(unpacker, times(3)).readString();
    verify(unpacker).readValue();
    verify(unpacker).readArrayEnd();
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException throws IOException in targets
   */
  @Test(expected = IOException.class)
  public final void testReadFromWithIoException() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    doReturn("publisherId").doReturn("eventType").when(unpacker)
        .readString();

    doThrow(new IOException()).when(unpacker).readArrayEnd();

    target.readFrom(unpacker);

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyObject());

    target.writeTo(packer);

    verify(packer).writeArrayBegin(4);
    verify(packer).write("publisherId");
    verify(packer).write("eventType");
    verify(packer).write("txid");
    verify(packer).write(body);
    verify(packer).writeArrayEnd();
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testWriteToWithBodyValue() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyObject());
    target.bodyValue = Mockito.mock(Value.class);

    target.writeTo(packer);

    verify(packer).writeArrayBegin(4);
    verify(packer).write("publisherId");
    verify(packer).write("eventType");
    verify(packer).write("txid");
    verify(packer).write(target.bodyValue);
    verify(packer).writeArrayEnd();
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException throws IOException in targets
   */
  @Test(expected = IOException.class)
  public final void testWriteToWithIoException() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyObject());

    doThrow(new IOException()).when(packer).writeArrayEnd();

    target.writeTo(packer);

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#hashCode()}.
   */
  @Test
  public final void testHashCodeTrue() {

    /*
     * setting
     */
    Object body = new Object();
    Event target1 = new Event("publisherId", "eventType", "txid", body);
    Event target2 = new Event("publisherId", "eventType", "txid", body);

    Object eventBody = new Object(); // not body
    Event target3 = new Event("publisherId", "eventType", "txid", eventBody);

    /*
     * test
     */
    int result1 = target1.hashCode();
    int result2 = target2.hashCode();
    int result3 = target3.hashCode();

    /*
     * check
     */
    assertThat(result2, is(result1));
    assertThat(result3, is(result1));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#equals(Object)}.
   */
  @Test
  public final void testHashCodeFalse() {

    /*
     * setting
     */
    Object body = new Object();
    Event target11 = new Event("publisherId", "eventType", "txid", body);
    Event target12 = new Event("publisherId", "eventType2", "txid", body);
    Event target21 = new Event("publisherId2", "eventType", "txid", body);
    Event target22 = new Event("publisherId2", "eventType2", "txid", body);

    /*
     * test
     */
    int result11 = target11.hashCode();
    int result12 = target12.hashCode();
    int result21 = target21.hashCode();
    int result22 = target22.hashCode();

    /*
     * check
     */
    assertThat(result12, is(not(result11)));
    assertThat(result21, is(not(result11)));
    assertThat(result22, is(not(result11)));

    assertThat(result21, is(not(result12)));
    assertThat(result22, is(not(result12)));

    assertThat(result22, is(not(result21)));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#equals(Object)}.
   */
  @Test
  public final void testEqualsEventTrue() {

    /*
     * setting
     */
    Object body = new Object();
    Event target = new Event("publisherId", "eventType", "txid", body);
    Event event2 = new Event("publisherId", "eventType", "txid", body);

    Object eventBody = new Object(); // not body
    Event event3 = new Event("publisherId", "eventType", "txid", eventBody);

    /*
     * test
     */
    boolean result1 = target.equals(target);
    boolean result2 = target.equals(event2);
    boolean result3 = target.equals(event3);

    /*
     *
     */
    assertThat(result1, is(true));
    assertThat(result2, is(true));
    assertThat(result3, is(true));

    assertThat(target.hashCode(), is(target.hashCode()));
    assertThat(event2.hashCode(), is(target.hashCode()));
    assertThat(event3.hashCode(), is(target.hashCode()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Event#equals(java.lang.Object)}.
   */
  @Test
  public final void testEqualsEventFalse() {

    /*
     * setting
     */
    Object body = new Object();
    Event target11 = new Event("publisherId", "eventType", "txid", body);
    Event event12 = new Event("publisherId", "eventType2", "txid", body);
    Event event21 = new Event("publisherId2", "eventType", "txid", body);
    Event event22 = new Event("publisherId2", "eventType2", "txid", body);

    /*
     * test
     */
    boolean result12 = target11.equals(event12);
    boolean result21 = target11.equals(event21);
    boolean result22 = target11.equals(event22);

    /*
     *
     */
    assertThat(result12, is(false));
    assertThat(result21, is(false));
    assertThat(result22, is(false));

  }

}
