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
import org.o3project.odenos.remoteobject.message.Request.Method;

import java.io.IOException;

/**
 *
 *
 */
public class RequestTest {
  private Request target = null;
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
    target = Mockito.spy(new Request("objectId", Request.Method.GET,
        "path", "txid", body));
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
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#Request()}.
   */
  @Test
  public final void testRequestStringMethodStringObject() {
    body = Mockito.mock(Object.class);
    target = Mockito.spy(new Request("objectId", Request.Method.GET,
        "path", "txid", body));

    assertThat(target.objectId, is("objectId"));
    assertThat(target.method, is(Request.Method.GET));
    assertThat(target.path, is("path"));
    assertThat(target.txid, is("txid"));
    assertThat(target.body, is(body));

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#Request(java.lang.String, org.o3project.odenos.remoteobject.message.Request.Method, java.lang.String, java.lang.String, java.lang.Object)}.
   */
  @SuppressWarnings("deprecation")
  @Test
  public final void testRequest() {
    target = new Request();

    assertThat(target.objectId, is(nullValue()));
    assertThat(target.method, is(nullValue()));
    assertThat(target.path, is(nullValue()));
    assertThat(target.txid, is(nullValue()));
    assertThat(target.body, is(nullValue()));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    doReturn("objectId").doReturn("GET").doReturn("path").when(unpacker)
        .readString();

    target.readFrom(unpacker);

    verify(unpacker).readArrayBegin();
    verify(unpacker, times(4)).readString();
    verify(unpacker).readValue();
    verify(unpacker).readArrayEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException throws IOException in targets
   */
  @Test(expected = IOException.class)
  public final void testReadFromWithIoException() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    doReturn("objectId").doReturn("GET").doReturn("path").when(unpacker)
        .readString();

    doThrow(new IOException()).when(unpacker).readArrayEnd();

    target.readFrom(unpacker);

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyObject());

    target.writeTo(packer);

    verify(packer).writeArrayBegin(5);
    verify(packer).write("objectId");
    verify(packer).write("GET");
    verify(packer).write("path");
    verify(packer).write("txid");
    verify(packer).write(body);
    verify(packer).writeArrayEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testWriteToWithBodyValue() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    doReturn(packer).when(packer).write(anyObject());
    target.bodyValue = Mockito.mock(Value.class);

    target.writeTo(packer);

    verify(packer).writeArrayBegin(5);
    verify(packer).write("objectId");
    verify(packer).write("GET");
    verify(packer).write("path");
    verify(packer).write("txid");
    verify(packer).write(target.bodyValue);
    verify(packer).writeArrayEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#writeTo(org.msgpack.packer.Packer)}.
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
   * Test method for {@link org.o3project.odenos.remoteobject.message.Request#toString()}.
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new Request("ObjectId", Method.GET, "path", "txid", "body");

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[objectId=ObjectId,method=GET,path=path,body=body]"), is(true));

  }

}
