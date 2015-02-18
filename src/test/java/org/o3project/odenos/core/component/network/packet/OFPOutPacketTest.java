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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.BooleanValue;
import org.msgpack.type.FloatValue;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.NilValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.UnpackerIterator;
import org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for OFPOutPacket.
 *
 * 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ OFPOutPacket.class })
public class OFPOutPacketTest {

  private OFPOutPacket target;
  private OFPOutPacket mockOfpOutPacket;
  private static final String TYPE = "OFPOutPacket";

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
    target = Mockito.spy(new OFPOutPacket());
    mockOfpOutPacket = PowerMockito.mock(OFPOutPacket.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    target = null;
    mockOfpOutPacket = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#OFPOutPacket()}
   * .
   */
  @Test
  public final void testOFPOutPacket() {
    target = new OFPOutPacket();
    assertThat(target, is(instanceOf(OFPOutPacket.class)));
    assertThat((byte[]) WhiteboxImpl.getInternalState(target, "data"), is(nullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#OFPOutPacket(java.lang.String, java.lang.String, java.util.List, java.util.List, org.o3project.odenos.core.component.network.flow.basic.BasicFlowMatch, byte[], java.util.Map)}
   * .
   */

  @SuppressWarnings({ "rawtypes", "serial", "unchecked" })
  @Test
  public final void testOFPOutPacketWithParameter() {

    String packetId = "packet01";
    String nodeId = "node01";
    List<String> portIds = new ArrayList() {
      {
        add("port01");
        add("port02");
      }
    };
    List<String> portExceptIds = new ArrayList() {
      {
        add("port100");
        add("port200");
      }
    };
    BasicFlowMatch header = new BasicFlowMatch();
    String dataStr = "data01";
    byte[] dataarray = dataStr.getBytes();
    Map<String, String> attributes = new HashMap<String, String>();

    OFPOutPacket param = new OFPOutPacket(packetId,
        nodeId,
        portIds,
        portExceptIds,
        header,
        dataarray,
        attributes);

    assertThat(param, is(instanceOf(OFPOutPacket.class)));
    assertThat(param.getPacketId(), is(packetId));
    assertThat(param.getNodeId(), is(nodeId));
    assertThat(param.getPorts(), is(portIds));
    assertThat(param.getExceptPorts(), is(portExceptIds));
    assertThat(param.getHeader(), is(header));
    assertThat((Map<String, String>) WhiteboxImpl.getInternalState(param, "attributes"),
        is(attributes));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#OFPOutPacket(org.o3project.odenos.core.component.network.packet.OFPOutPacket)}
   * .
   */
  @Test
  public final void testOFPOutPacketOFPOutPacket() {

    mockOfpOutPacket = PowerMockito.mock(OFPOutPacket.class);
    target = new OFPOutPacket(mockOfpOutPacket);

    assertThat(target, is(instanceOf(OFPOutPacket.class)));
    verify(mockOfpOutPacket).getPacketId();
    verify(mockOfpOutPacket).getNodeId();
    verify(mockOfpOutPacket).getPorts();
    verify(mockOfpOutPacket).getExceptPorts();
    verify(mockOfpOutPacket).getHeader();
    verify(mockOfpOutPacket).getData();
    verify(mockOfpOutPacket).getAttributes();

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#readValue(org.msgpack.type.Value)}
   * .
   */
  @Test
  public final void testReadValue() {

    Value value = Mockito.mock(Value.class);

    target = Mockito.spy(new OFPOutPacket() {
      @Override
      public boolean readValue(Value value) {
        return false;
      }

    });

    target.readValue(value);

    // return is false <-- overrided
    verify(target, times(1)).readValue(value);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#readValue(org.msgpack.type.Value)}
   * .
   *
   * @throws IOException
   */
  @Test
  public final void testReadValueTrue() throws IOException {

    Value value = new Value() {

      @Override
      public void writeTo(Packer pk) throws IOException {
        fail();
      }

      @Override
      public StringBuilder toString(StringBuilder sb) {
        fail();
        return null;
      }

      @Override
      public boolean isRawValue() {
        fail();
        return false;
      }

      @Override
      public boolean isNilValue() {
        fail();
        return false;
      }

      @Override
      public boolean isMapValue() {
        fail();
        return false;
      }

      @Override
      public boolean isIntegerValue() {
        fail();
        return false;
      }

      @Override
      public boolean isFloatValue() {
        fail();
        return false;
      }

      @Override
      public boolean isBooleanValue() {
        fail();
        return false;
      }

      @Override
      public boolean isArrayValue() {
        fail();
        return false;
      }

      @Override
      public ValueType getType() {
        fail();
        return null;
      }

      @Override
      public RawValue asRawValue() {
        fail();
        return null;
      }

      @Override
      public NilValue asNilValue() {
        fail();
        return null;
      }

      @Override
      public MapValue asMapValue() {
        fail();
        return null;
      }

      @Override
      public IntegerValue asIntegerValue() {
        fail();
        return null;
      }

      @Override
      public FloatValue asFloatValue() {
        fail();
        return null;
      }

      @Override
      public BooleanValue asBooleanValue() {
        fail();
        return null;
      }

      @Override
      public ArrayValue asArrayValue() {
        fail();
        return null;
      }
    };

    Unpacker unpacker = Mockito.spy(new Unpacker() {

      @Override
      public void close() throws IOException {
        fail();
      }

      @Override
      public boolean trySkipNil() throws IOException {
        fail();
        return false;
      }

      @Override
      public void skip() throws IOException {
        fail();
      }

      @Override
      public void setRawSizeLimit(int size) {
        fail();
      }

      @Override
      public void setMapSizeLimit(int size) {
        fail();
      }

      @Override
      public void setArraySizeLimit(int size) {
        fail();
      }

      @Override
      public void resetReadByteCount() {
        fail();
      }

      @Override
      public Value readValue() throws IOException {
        fail();
        return null;
      }

      @Override
      public String readString() throws IOException {
        fail();
        return null;
      }

      @Override
      public short readShort() throws IOException {
        fail();
        return 0;
      }

      @Override
      public void readNil() throws IOException {
        fail();
      }

      @Override
      public void readMapEnd(boolean check) throws IOException {
        fail();
      }

      @Override
      public void readMapEnd() throws IOException {
        fail();
      }

      @Override
      public int readMapBegin() throws IOException {
        fail();
        return 0;
      }

      @Override
      public long readLong() throws IOException {
        fail();
        return 0;
      }

      @Override
      public int readInt() throws IOException {
        fail();
        return 0;
      }

      @Override
      public float readFloat() throws IOException {
        fail();
        return 0;
      }

      @Override
      public double readDouble() throws IOException {
        fail();
        return 0;
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
        fail();
        return null;
      }

      @Override
      public byte[] readByteArray() throws IOException {
        fail();
        return null;
      }

      @Override
      public byte readByte() throws IOException {
        fail();
        return 0;
      }

      @Override
      public boolean readBoolean() throws IOException {
        fail();
        return false;
      }

      @Override
      public BigInteger readBigInteger() throws IOException {
        fail();
        return null;
      }

      @Override
      public void readArrayEnd(boolean check) throws IOException {
        fail();
      }

      @Override
      public void readArrayEnd() throws IOException {
        fail();
      }

      @Override
      public int readArrayBegin() throws IOException {
        fail();
        return 0;
      }

      @Override
      public <T> T read(T to, Template<T> tmpl) throws IOException {
        fail();
        return null;
      }

      @Override
      public <T> T read(Template<T> tmpl) throws IOException {
        fail();
        return null;
      }

      @Override
      public <T> T read(T to) throws IOException {
        fail();
        return null;
      }

      @Override
      public <T> T read(Class<T> klass) throws IOException {
        fail();
        return null;
      }

      @Override
      public UnpackerIterator iterator() {
        fail();
        return null;
      }

      @Override
      public int getReadByteCount() {
        fail();
        return 0;
      }

      @Override
      public ValueType getNextType() throws IOException {
        fail();
        return null;
      }
    });

    doReturn(value).when(unpacker).readValue();
    doReturn(true).when(target).readValue(value);

    assertThat(target.readValue(value), is(true));

    verify(target, times(1)).readValue(value);

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSub() {
    Map<String, Value> values = new HashMap<String, Value>();

    target = Mockito.spy(new OFPOutPacket() {

      @Override
      public boolean writeValueSub(Map<String, Value> values) {
        return false;
      }
    });

    target.writeValueSub(values);
    // return is false <-- overrided
    verify(target, times(1)).writeValueSub(values);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#writeValueSub(java.util.Map)}
   * .
   */
  @Test
  public final void testWriteValueSubTrue() {
    Map<String, Value> values = new HashMap<String, Value>();

    doReturn(true).when(target).writeValueSub(values);
    assertThat(target.writeValueSub(values), is(true));
    verify(target, times(1)).writeValueSub(values);
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.packet.OFPOutPacket#getType()}
   * .
   */
  @Test
  public final void testGetType() {
    assertThat(target.getType(), is(TYPE));
  }

}
