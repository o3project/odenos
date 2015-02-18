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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Test class for OdenosMessage.
 */
public class OdenosMessageTest {

  private OdenosMessage target;

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

    target = Mockito.spy(new OdenosMessage() {

      @Override
      public boolean readValue(Value value) {
        return false;
      }

      @Override
      public boolean writeValueSub(Map<String, Value> values) {
        return false;
      }

    });

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.OdenosMessage#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadValue() {

    /*
     * setting
     */
    Value value = Mockito.mock(Value.class);

    /*
     * test
     */
    target.readValue(value);

    /*
     * check
     */
    verify(target, times(1)).readValue(value);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.OdenosMessage#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public void testWriteValueSub() {

    /*
     * setting
     */
    Map<String, Value> values = new HashMap<String, Value>();

    /*
     * test
     */
    target.writeValueSub(values);

    /*
     * check
     */
    verify(target, times(1)).writeValueSub(values);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.OdenosMessage#writeValue()}
   * .
   */
  @Test
  public void testWriteValue() {

    /*
     * setting
     */
    target = Mockito.spy(new OdenosMessage() {

      @Override
      public boolean readValue(Value value) {
        fail();
        return false;
      }

      @Override
      public boolean writeValueSub(Map<String, Value> values) {

        values.put("key1", Mockito.mock(Value.class));
        values.put("key2", Mockito.mock(Value.class));
        values.put("key3", Mockito.mock(Value.class));
        values.put("key4", Mockito.mock(Value.class));

        return true;
      }

    });

    /*
     * test
     */
    Value result = target.writeValue();

    /*
     * check
     */
    assertThat(result, is(notNullValue()));

    Set<Value> resultSet = result.asMapValue().keySet();
    assertThat(resultSet.size(), is(4));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.OdenosMessage#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReadFrom() throws Exception {

    /*
     * setting
     */
    Value value = Mockito.mock(Value.class);
    Unpacker unpacker = Mockito.mock(Unpacker.class);

    doReturn(value).when(unpacker).readValue();
    doReturn(true).when(target).readValue(value);

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readValue();
    verify(target, times(1)).readValue(value);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.message.OdenosMessage#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testWriteTo() throws Exception {

    /*
     * setting
     */
    Packer packer = Mockito.mock(Packer.class);
    Value value = Mockito.mock(Value.class);

    doReturn(value).when(target).writeValue();

    /*
     * test
     */
    target.writeTo(packer);

    /*
     * check
     */
    verify(target, times(1)).writeValue();
    verify(packer, times(1)).write(value);

  }

}
