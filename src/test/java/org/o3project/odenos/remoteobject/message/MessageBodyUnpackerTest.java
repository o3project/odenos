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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsExceptionClass;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.Unpacker;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.ParseBodyException;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.StringList;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.StringMap;
import org.o3project.odenos.remoteobject.message.MessageBodyUnpacker.TemplateHashMap;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 *
 */
public class MessageBodyUnpackerTest {

  private MessageBodyUnpacker target;

  private MessagePack mockmsgpack;

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
    target = new TestMessageBodyUnpacker();

    mockmsgpack = Mockito.mock(MessagePack.class);
    ConcurrentLinkedQueue<MessagePack> pool
        = Whitebox.getInternalState(MessageBodyUnpacker.class, "pool");
    pool.clear();
    pool.add(mockmsgpack);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  // FIXME this case is not really testing anything.
  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   * @throws ParseBodyException
   */
  @Test
  public final void testInitializeWithNull() throws ParseBodyException {
    target = new TestMessageBodyUnpacker();

    assertThat(target.body, is(nullValue()));
    assertThat(target.bodyValue, is(nullValue()));

  }

  // FIXME this case is not really testing anything.
  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   * @throws ParseBodyException
   */
  @Test
  public final void testInitializeWithBodyValue() throws ParseBodyException {
    target = new TestMessageBodyUnpacker();

    target.bodyValue = Mockito.mock(Value.class);

    assertThat(target.body, is(nullValue()));
    assertThat(target.bodyValue, is(notNullValue()));

  }

  // FIXME this case is not really testing anything.
  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   * @throws ParseBodyException
   */
  @Test
  public final void testInitializeWithNotNull() throws ParseBodyException {
    target = new TestMessageBodyUnpacker();

    target.body = Mockito.mock(Object.class);

    assertThat(target.body, is(notNullValue()));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyWithBodyNotNull() throws IOException {
    target = new TestMessageBodyUnpacker();

    final String string = "Already unpacked";
    target.body = string;

    assertThat(target.getBody(String.class), is(string));
    verify(mockmsgpack, never()).unconvert(target.bodyValue);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyReturnNull() throws IOException {

    assertThat(target.getBody(String.class), is(nullValue()));
    verify(mockmsgpack, never()).unconvert(target.bodyValue);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyWithBodyValueNotNull() throws IOException {

    target.bodyValue = Mockito.mock(Value.class);
    final String ret = "Unpacked value";
    when(mockmsgpack.convert(target.bodyValue, String.class)).thenReturn(
        ret);

    assertThat(target.getBody(String.class), is(ret));
    verify(mockmsgpack, never()).unconvert(target.bodyValue);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBody(java.lang.Class)}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyWithIoException() throws IOException {

    target.bodyValue = Mockito.mock(Value.class);

    when(mockmsgpack.convert(target.bodyValue, String.class)).thenThrow(
        new IOException());

    assertThat(target.getBody(String.class), is(nullValue()));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyValue()}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyValueWithBodyValueNotNull() throws IOException {

    target.bodyValue = Mockito.mock(Value.class);

    Value ret = target.getBodyValue();


    assertThat(ret, is(notNullValue()));
    assertThat(ret, is(target.bodyValue));

    verify(mockmsgpack, never()).unconvert(target.bodyValue);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyValue()}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyValueReturnNull() throws IOException {

    assertThat(target.getBodyValue(), is(nullValue()));

    verify(mockmsgpack, never()).unconvert(target.bodyValue);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyValue()}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyValueWithBodyNotNull() throws IOException {

    target.body = Mockito.mock(Object.class);
    Value mockvalue = Mockito.mock(Value.class);
    when(mockmsgpack.unconvert(target.body)).thenReturn(mockvalue);

    Value ret = target.getBodyValue();

    assertThat(ret, is(notNullValue()));
    assertThat(ret, is(mockvalue));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyValue()}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyValueWithIoException() throws IOException {

    target.body = Mockito.mock(Object.class);

    when(mockmsgpack.unconvert(target.body)).thenThrow(new IOException());

    assertThat(target.getBodyValue(), is(nullValue()));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsMap(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsMapWithBodyNotNull() throws IOException {

    Map<String, String> map = new HashMap<>();
    target.body = map;

    assertThat(target.getBodyAsMap(String.class), is(map));
    verify(mockmsgpack, never()).unconvert(any());
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsMap(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsMapReturnNull() throws IOException {

    assertThat(target.getBodyAsMap(String.class), is(nullValue()));
    verify(mockmsgpack, never()).unconvert(any());
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsMap(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsMapWithBodyValueNotNull() throws IOException {

    final Map<String, String> expected = new HashMap<>();
    expected.put("statusCode", "initialize");
    expected.put("returnCode", "OK");

    Value[] valuemap = new Value[4];
    valuemap[0] = ValueFactory.createRawValue("statusCode");
    valuemap[1] = ValueFactory.createRawValue("initialize");
    valuemap[2] = ValueFactory.createRawValue("returnCode");
    valuemap[3] = ValueFactory.createRawValue("OK");


    target.bodyValue = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    map = ValueFactory.createMapValue(valuemap);
    doReturn(map).when(target.bodyValue).asMapValue();

    doReturn("initialize").when(mockmsgpack).convert(valuemap[1], String.class);
    doReturn("OK").when(mockmsgpack).convert(valuemap[3], String.class);

    assertThat(target.getBodyAsMap(String.class), is(expected));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsMap(java.lang.Class)}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyAsMapWithIoException() throws IOException {
    doThrow(new IOException()).when(mockmsgpack).convert(
        (Value) anyObject(), eq(String.class));

    Value[] valuemap = new Value[4];
    valuemap[0] = ValueFactory.createRawValue("statusCode");
    valuemap[1] = ValueFactory.createRawValue("initialize");
    valuemap[2] = ValueFactory.createRawValue("returnCode");
    valuemap[3] = ValueFactory.createRawValue("OK");

    target.bodyValue = Mockito.mock(Value.class);
    MapValue map = Mockito.mock(MapValue.class);
    map = ValueFactory.createMapValue(valuemap);

    doReturn(map).when(target.bodyValue).asMapValue();

    assertThat(target.getBodyAsMap(String.class), is(nullValue()));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsList(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsListWithBodyNotNull() throws IOException {

    List<String> list = new ArrayList<>();
    target.body = list;

    assertThat(target.getBodyAsList(String.class), is(sameInstance(list)));

    verify(mockmsgpack, never()).convert(target.bodyValue, String.class);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsList(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsListReturnNull() throws IOException {

    assertThat(target.getBodyAsList(String.class), is(nullValue()));
    verify(mockmsgpack, never()).convert(target.bodyValue, String.class);
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsList(java.lang.Class)}.
   */
  @Test
  public final void testGetBodyAsListWithBodyValueNotNullTest() throws IOException {

    List<String> expected = new ArrayList<>();
    expected.add("body01");
    expected.add("body02");

    Value[] valuearray = new Value[2];
    valuearray[0] = ValueFactory.createRawValue("body01");
    valuearray[1] = ValueFactory.createRawValue("body02");

    target.bodyValue = Mockito.mock(Value.class);
    ArrayValue array = Mockito.mock(ArrayValue.class);
    array = ValueFactory.createArrayValue(valuearray);
    doReturn(array).when(target.bodyValue).asArrayValue();

    doReturn("body01").when(mockmsgpack).convert(valuearray[0], String.class);
    doReturn("body02").when(mockmsgpack).convert(valuearray[1], String.class);

    assertThat(target.getBodyAsList(String.class), is(expected));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsList(java.lang.Class)}.
   * @throws IOException
   */
  @Test
  public final void testGetBodyAsListWithIoExceptionTest() throws IOException {

    doThrow(new IOException()).when(mockmsgpack).convert(
        (Value) anyObject(), eq(String.class));

    Value[] valuearray = new Value[2];
    valuearray[0] = ValueFactory.createRawValue("body01");
    valuearray[1] = ValueFactory.createRawValue("body02");

    target.bodyValue = Mockito.mock(Value.class);
    ArrayValue array = Mockito.mock(ArrayValue.class);
    array = ValueFactory.createArrayValue(valuearray);
    doReturn(array).when(target.bodyValue).asArrayValue();

    assertThat(target.getBodyAsList(String.class), is(nullValue()));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsStringList()}.
   * @throws ParseBodyException
   */
  @Test
  public final void testGetBodyAsStringList() throws ParseBodyException {
    StringList ret = new StringList();
    MessageBodyUnpacker mock = Mockito.mock(MessageBodyUnpacker.class, Mockito.CALLS_REAL_METHODS);
    when(mock.getBody(StringList.class)).thenReturn(ret);

    assertThat((StringList) mock.getBodyAsStringList(), is(sameInstance(ret)));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#getBodyAsStringMap()}.
   * @throws ParseBodyException
   */
  @Test
  public final void testGetBodyAsStringMap() throws ParseBodyException {
    StringMap ret = new StringMap();
    MessageBodyUnpacker mock = Mockito.mock(MessageBodyUnpacker.class, Mockito.CALLS_REAL_METHODS);
    when(mock.getBody(StringMap.class)).thenReturn(ret);

    assertThat((StringMap) mock.getBodyAsStringMap(), is(sameInstance(ret)));
  }

  /**
   * Test method for {@link MessageBodyUnpacker#isBodyNull()}.
   */
  @Test
  public final void testIsBodyNullTrueWithBodyNull() {
    assertThat(target.isBodyNull(), is(true));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#isBodyNull()}.
   */
  @Test
  public final void testIsBodyNullFalseWithBodyNotNull() {
    target.body = Mockito.mock(Object.class);

    assertThat(target.isBodyNull(), is(false));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#isBodyNull()}.
   */
  @Test
  public final void testIsBodyNullTrue() {
    target.bodyValue = Mockito.mock(Value.class);
    when(target.bodyValue.isNilValue()).thenReturn(true);

    assertThat(target.isBodyNull(), is(true));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#isBodyNull()}.
   */
  @Test
  public final void testIsBodyNullFalse() {
    target.bodyValue = Mockito.mock(Value.class);
    when(target.bodyValue.isNilValue()).thenReturn(false);

    assertThat(target.isBodyNull(), is(false));

  }

  /**
   * Test method for {@link MessageBodyUnpacker#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @Test
  public final void testReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    try {
      target.readFrom(unpacker);
    } catch (IOException e) {
      fail();
    }

  }

  /**
   * Test method for {@link MessageBodyUnpacker#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException      */
  @Test(expected = IOException.class)
  public final void testReadFromWithIoException() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class, new ThrowsExceptionClass(IOException.class));

    target = new TestMessageBodyUnpacker() {
        @Override
        public void readFrom(Unpacker unpacker) throws IOException {
            // call mocked unpacker to throw IOException;
            unpacker.skip();
        };
    };

    target.readFrom(unpacker);

  }

  /**
   * Test method for {@link MessageBodyUnpacker#writeTo(org.msgpack.packer.Packer)}.
   */
  @Test
  public final void testWriteTo() {
    Packer packer = Mockito.mock(Packer.class);
    try {
      target.writeTo(packer);
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Test method for {@link MessageBodyUnpacker#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public final void testWriteToWithIoException() throws IOException {
    Packer packer = Mockito.mock(Packer.class, new ThrowsExceptionClass(IOException.class));

    target = new TestMessageBodyUnpacker() {
        @Override
        public void writeTo(Packer packer) throws IOException {
            // call mocked packer to throw IOException;
            packer.writeNil();
        }
    };

    target.writeTo(packer);

  }

  /**
   * Test method for {@link MessageBodyUnpacker.StringList#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test
  public final void testStringListWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    StringList list = Mockito.spy(new StringList());

    doReturn(null).when(packer).write(list);

    list.writeTo(packer);

    verify(packer).write(list);

  }

  /**
   * Test method for {@link MessageBodyUnpacker.StringList#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testStringListReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    StringList list = Mockito.spy(new StringList());

    doNothing().when(list).clear();
    doReturn(false).when(list).addAll(
        (Collection<? extends String>) anyObject());

    list.readFrom(unpacker);

    verify(list).clear();
    verify(list).addAll((Collection<? extends String>) anyObject());

  }

  /**
   * Test method for {@link MessageBodyUnpacker.StringMap#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @Test
  public final void testStringMapWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    StringMap map = Mockito.spy(new StringMap());
    doReturn(null).when(packer).write(map);

    map.writeTo(packer);

    verify(packer).write(map);

  }

  /**
   * Test method for {@link MessageBodyUnpacker.StringMap#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testStringMapReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    StringMap map = Mockito.spy(new StringMap());
    doNothing().when(map).clear();
    doNothing().when(map).putAll(
        (Map<? extends String, ? extends String>) anyObject());

    map.readFrom(unpacker);

    verify(map).clear();
    verify(map).putAll(
        (Map<? extends String, ? extends String>) anyObject());

  }

  /**
   * Test method for {@link MessageBodyUnpacker.TemplateHashMap#TemplateHashMap(java.lang.Class<T>)}.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testTemplateHashMap() {
    TemplateHashMap<String> hashmap = new TemplateHashMap(String.class);

    assertThat((Class) Whitebox.getInternalState(hashmap, "valueClass"),
        equalTo((Class) java.lang.String.class));

  }

  /**
   * Test method for {@link MessageBodyUnpacker.TemplateHashMap#writeTo(org.msgpack.packer.Packer)}.
   * @throws IOException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public final void testTemplateHashMapWriteTo() throws IOException {
    Packer packer = Mockito.mock(Packer.class);
    TemplateHashMap<String> hashmap = Mockito.spy(new TemplateHashMap(
        String.class));
    hashmap.put("key1", "value1");
    hashmap.put("key2", "value2");
    hashmap.put("key3", "value3");

    hashmap.writeTo(packer);

    verify(hashmap).keySet();
    verify(packer, atLeastOnce()).write(anyObject());
    verify(packer).writeMapEnd();

  }

  /**
   * Test method for {@link MessageBodyUnpacker.TemplateHashMap#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws IOException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testTemplateHashMapReadFrom() throws IOException {
    Unpacker unpacker = Mockito.mock(Unpacker.class);
    TemplateHashMap<String> hashmap = Mockito.spy(new TemplateHashMap(
        String.class));

    when(unpacker.readMapBegin()).thenReturn(1);
    when(unpacker.readString()).thenReturn("type", "TYPE");

    hashmap.readFrom(unpacker);

    verify(unpacker).readMapBegin();
    verify(unpacker).readMapEnd();

  }

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  /**
   * Test method for {@link MessageBodyUnpacker.ParseBodyException#ParseBodyException(java.lang.Throwable)}.
   *
   */
  @Test
  public final void testParseBodyExceptionWithString() throws Exception {
    expectedException.expect(ParseBodyException.class);
    expectedException.expectMessage("errorMessage");
    throw new ParseBodyException("errorMessage");

  }

  /**
   * Test method for {@link MessageBodyUnpacker.ParseBodyException#ParseBodyException(java.lang.Throwable)}.
   *
   */
  @Test
  public final void testParseBodyExceptionWithThrowable() throws Exception {
    expectedException.expect(ParseBodyException.class);
    throw new ParseBodyException(new IOException());

  }

  /**
   * Test method for {@link MessageBodyUnpacker.ParseBodyException#ParseBodyException(java.lang.Throwable)}.
   *
   */
  @Test
  public final void testParseBodyExceptionWithStringThrowable()
      throws Exception {
    expectedException.expect(ParseBodyException.class);
    expectedException.expectMessage("errorMessage");
    throw new ParseBodyException("errorMessage", new IOException());

  }

  private static class TestMessageBodyUnpacker extends MessageBodyUnpacker {
    @Override
    public void readFrom(Unpacker unpacker) throws IOException {}

    @Override
    public void writeTo(Packer packer) throws IOException {}
  }

}
