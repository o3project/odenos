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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
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

import java.io.IOException;

/**
 *
 *
 */
public class ResponseTest {
  private Response target = null;
  private Object body = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    body = Mockito.mock(Object.class);
    target = Mockito.spy(new Response(200, body));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    body = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#Response()}.
   */
  @SuppressWarnings({ "static-access", "deprecation" })
  @Test
  public final void testResponse() {
    target = Mockito.spy(new Response());
    assertThat(target.OK, is(200));
    assertThat(target.CREATED, is(201));
    assertThat(target.ACCEPTED, is(202));
    assertThat(target.NO_CONTENT, is(204));
    assertThat(target.BAD_REQUEST, is(400));
    assertThat(target.FORBIDDEN, is(403));
    assertThat(target.NOT_FOUND, is(404));
    assertThat(target.METHOD_NOT_ALLOWED, is(405));
    assertThat(target.CONFLICT, is(409));
    assertThat(target.INTERNAL_SERVER_ERROR, is(500));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#Response(java.lang.Integer, java.lang.Object)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testResponseIntegerObject() {
    body = Mockito.mock(Object.class);

    target = Mockito.spy(new Response(200, body));
    assertThat(target.OK, is(200));
    assertThat(target.CREATED, is(201));
    assertThat(target.ACCEPTED, is(202));
    assertThat(target.NO_CONTENT, is(204));
    assertThat(target.BAD_REQUEST, is(400));
    assertThat(target.FORBIDDEN, is(403));
    assertThat(target.NOT_FOUND, is(404));
    assertThat(target.METHOD_NOT_ALLOWED, is(405));
    assertThat(target.CONFLICT, is(409));
    assertThat(target.INTERNAL_SERVER_ERROR, is(500));

    assertThat(target.statusCode, is(200));
    assertThat(target.body, is(body));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @Test
  public final void testReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);

    target.readFrom(unpacker);

    verify(unpacker).readArrayBegin();
    verify(unpacker).readInt();
    verify(unpacker).readValue();
    verify(unpacker).readArrayEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public final void testReadFromWithIoException() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);

    doThrow(new IOException()).when(unpacker).readArrayEnd();

    target.readFrom(unpacker);

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test
  public final void testWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);

    target.writeTo(packer);

    verify(packer).writeArrayBegin(2);
    verify(packer).write(target.statusCode);
    verify(packer).write(body);
    verify(packer).writeArrayEnd();
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test
  public final void testWriteToWithBodyValue() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    target.bodyValue = Mockito.mock(Value.class);

    target.writeTo(packer);

    verify(packer).writeArrayBegin(2);
    verify(packer).write(target.statusCode);
    verify(packer).write(target.bodyValue);
    verify(packer).writeArrayEnd();
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public final void testWriteToWithIoException() throws IOException {
    Packer packer = Mockito.mock(Packer.class);

    doThrow(new IOException()).when(packer).writeArrayEnd();

    target.writeTo(packer);

  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorGetTrue() {
    target.statusCode = target.CREATED;

    assertThat(target.isError("GET"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorGetFalse() {
    target.statusCode = target.OK;

    assertThat(target.isError("GET"), is(false));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorDeleteTrue() {
    target.statusCode = target.ACCEPTED;

    assertThat(target.isError("DELETE"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorPutTrue() {
    target.statusCode = target.NO_CONTENT;

    assertThat(target.isError("PUT"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorPutFalse() {
    target.statusCode = target.CREATED;

    assertThat(target.isError("PUT"), is(false));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorPostTrue() {
    target.statusCode = target.BAD_REQUEST;

    assertThat(target.isError("POST"), is(true));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#isError(java.lang.String)}.
   */
  @SuppressWarnings("static-access")
  @Test
  public final void testIsErrorPostFalse() {
    target.statusCode = target.OK;

    assertThat(target.isError("POST"), is(false));
  }

  /**
   * Test method for {@link org.o3project.odenos.remoteobject.message.Response#toString()}.
   */
  @Test
  public final void testToString() {

    /*
     * setting
     */
    target = new Response(Response.OK, "body");

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    assertThat(result.endsWith("[statusCode=200,body=body]"), is(true));

  }

}
