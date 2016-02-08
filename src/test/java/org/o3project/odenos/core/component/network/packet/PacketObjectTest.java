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

package org.o3project.odenos.core.component.network.packet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.powermock.reflect.Whitebox;

/**
 * Test class for PacketObject.
 *
 */
public class PacketObjectTest {

  private PacketObject target;

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
    target = Mockito.spy(new PacketObject());
  }

  /**
   * @throws java.lang.Exception throws Exception in targets
   */
  @After
  public void tearDown() throws Exception {
    target = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readPacketMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadPacketMessageFromInPacket() {

    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("InPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));
    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readPacketMessageFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("InPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readPacketMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadPacketMessageFromOutPacket() {

    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OutPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));
    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readPacketMessageFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OutPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readPacketMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadPacketMessageFromOFPInPacket() {

    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OFPInPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));
    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readPacketMessageFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OFPInPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readPacketMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadPacketMessageFromOFPOutPacket() {

    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OFPOutPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));
    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readPacketMessageFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OFPOutPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readPacketMessageFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public void testReadPacketMessageFromOther() {

    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("Other");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));
    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readPacketMessageFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createPacket(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testCreatePacketInPacket() {

    /*
     * set
     */
    PacketObject target = Mockito.spy(new PacketObject());
    Packet msg = new InPacket();

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.createPacket(msg);

    /*
     * check
     */
    assertThat(result.getType(), is("InPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createPacket(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testCreatePacketOutPacket() {

    /*
     * set
     */
    Packet msg = new OutPacket();

    /*
     * test
     */
    Packet result = PacketObject.createPacket(msg);

    /*
     * check
     */
    assertThat(result.getType(), is("OutPacket"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createPacket(org.o3project.odenos.core.component.network.packet.Packet)}
   * .
   */
  @Test
  public void testCreatePacket() {

    /*
     * set
     */
    Packet msg = Mockito.mock(Packet.class);

    /*
     * test
     */
    Packet result = PacketObject.createPacket(msg);

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readInPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadInPacketFromInPacket() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("InPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readInPacketFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("InPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readInPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadInPacketFromInPacketOFPInPacket() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OFPInPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readInPacketFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OFPInPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readInPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadInPacketFromInPacketNullValue() {
    /*
     * set
     */
    PacketObject target = Mockito.spy(new PacketObject());

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readInPacketFrom(null);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readInPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadInPacketFromInPacketNilValue() {
    /*
     * set
     */
    PacketObject target = Mockito.spy(new PacketObject());
    Value value = ValueFactory.createNilValue();

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readInPacketFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readInPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadInPacketFromInPacketOther() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("Other");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readInPacketFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readOutPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadOutPacketFromOutPacket() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OutPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readOutPacketFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OutPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readOutPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadOutPacketFromOFPOutPacket() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("OFPOutPacket");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readOutPacketFrom(value);

    /*
     * check
     */
    assertThat(result.getType(), is("OFPOutPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readOutPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadOutPacketFromOther() {
    /*
     * set
     */
    Value[] attributeArray = new Value[2];
    attributeArray[0] = ValueFactory.createRawValue("key");
    attributeArray[1] = ValueFactory.createRawValue("ID");
    Value[] valueArray = new Value[4];
    valueArray[0] = ValueFactory.createRawValue("type");
    valueArray[1] = ValueFactory.createRawValue("Other");
    valueArray[2] = ValueFactory.createRawValue("attributes");
    valueArray[3] = ValueFactory.createMapValue(attributeArray);

    Value value = Mockito.mock(Value.class);

    when(value.asMapValue()).thenReturn(
        ValueFactory.createMapValue(valueArray));

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readOutPacketFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readOutPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadOutPacketFromNull() {
    /*
     * set
     */
    PacketObject target = Mockito.spy(new PacketObject());
    Value value = null;

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readOutPacketFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#readOutPacketFrom(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadOutPacketFromNilValue() {
    /*
     * set
     */
    PacketObject target = Mockito.spy(new PacketObject());
    Value value = ValueFactory.createNilValue();

    /*
     * test
     */
    @SuppressWarnings("static-access")
    Packet result = target.readOutPacketFrom(value);

    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createInPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateInPacketPacketInPacket() throws Exception {
    /*
     * set
     */
    Packet msg = new InPacket();

    /*
     * test
     */
    InPacket result = Whitebox.invokeMethod(target, "createInPacket", msg);
    /*
     * check
     */
    assertThat(result.getType(), is("InPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createInPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateInPacketPacketOFPInPacket() throws Exception {
    /*
     * set
     */
    Packet msg = new OFPInPacket();

    /*
     * test
     */
    InPacket result = Whitebox.invokeMethod(target, "createInPacket", msg);
    /*
     * check
     */
    assertThat(result.getType(), is("OFPInPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createInPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateInPacketPacketOther() throws Exception {
    /*
     * set
     */
    Packet msg = new OutPacket();

    /*
     * test
     */
    InPacket result = Whitebox.invokeMethod(target, "createInPacket", msg);
    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createOutPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateOutPacketPacketOutPacket() throws Exception {
    /*
     * set
     */
    Packet msg = new OutPacket();

    /*
     * test
     */
    OutPacket result = Whitebox
        .invokeMethod(target, "createOutPacket", msg);
    /*
     * check
     */
    assertThat(result.getType(), is("OutPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createOutPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateOutPacketPacketOFPOutPacket() throws Exception {
    /*
     * set
     */
    Packet msg = new OFPOutPacket();

    /*
     * test
     */
    OutPacket result = Whitebox
        .invokeMethod(target, "createOutPacket", msg);
    /*
     * check
     */
    assertThat(result.getType(), is("OFPOutPacket"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.PacketObject#createOutPacket(Packet)}
   * .
   */
  @Test
  public final void testCreateOutPacketPacketOther() throws Exception {
    /*
     * set
     */
    Packet msg = new InPacket();

    /*
     * test
     */
    OutPacket result = Whitebox
        .invokeMethod(target, "createOutPacket", msg);
    /*
     * check
     */
    assertThat(result, is(nullValue()));
  }

}
