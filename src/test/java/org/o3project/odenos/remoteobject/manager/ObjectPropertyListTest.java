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

package org.o3project.odenos.remoteobject.manager;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.UnpackerIterator;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * 
 *
 */
public class ObjectPropertyListTest {
  private ObjectPropertyList target;

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
    target = spy(new ObjectPropertyList());
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
   * {@link org.o3project.odenos.remoteobject.manager.ObjectPropertyList#writeTo(org.msgpack.packer.Packer)}
   * .
   *
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testWriteTo() throws IOException {
    /*
     * set
     */
    Packer pk = spy(new Packer() {

      @Override
      public void close() throws IOException {

      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public Packer write(boolean arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(byte arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(short arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(int arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(long arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(float arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(double arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Boolean arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Byte arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Short arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Integer arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Long arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Float arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Double arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(BigInteger arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(byte[] arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(ByteBuffer arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(String arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Value arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(Object arg0) throws IOException {

        return null;
      }

      @Override
      public Packer write(byte[] arg0, int arg1, int arg2)
          throws IOException {

        return null;
      }

      @Override
      public Packer writeArrayBegin(int arg0) throws IOException {

        return null;
      }

      @Override
      public Packer writeArrayEnd() throws IOException {

        return null;
      }

      @Override
      public Packer writeArrayEnd(boolean arg0) throws IOException {

        return null;
      }

      @Override
      public Packer writeMapBegin(int arg0) throws IOException {

        return null;
      }

      @Override
      public Packer writeMapEnd() throws IOException {

        return null;
      }

      @Override
      public Packer writeMapEnd(boolean arg0) throws IOException {

        return null;
      }

      @Override
      public Packer writeNil() throws IOException {

        return null;
      }
    });

    /*
     * test
     */
    target.writeTo(pk);

    /*
     * check
     */
    verify(pk, times(1)).writeArrayBegin(target.size());
    verify(pk, times(1)).writeArrayEnd();
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.remoteobject.manager.ObjectPropertyList#readFrom(org.msgpack.unpacker.Unpacker)}
   * .
   *
   * @throws IOException throws IOException in targets
   */
  @Test
  public final void testReadFrom() throws IOException {
    /*
     * set
     */
    ObjectPropertyList target = new ObjectPropertyList();
    Unpacker unpacker = spy(new Unpacker() {

      @Override
      public void close() throws IOException {

      }

      @Override
      public ValueType getNextType() throws IOException {

        return null;
      }

      @Override
      public int getReadByteCount() {

        return 0;
      }

      @Override
      public UnpackerIterator iterator() {

        return null;
      }

      @Override
      public <T> T read(Class<T> arg0) throws IOException {

        return null;
      }

      @Override
      public <T> T read(T arg0) throws IOException {

        return null;
      }

      @Override
      public <T> T read(Template<T> arg0) throws IOException {

        return null;
      }

      @Override
      public <T> T read(T arg0, Template<T> arg1) throws IOException {

        return null;
      }

      @Override
      public int readArrayBegin() throws IOException {

        return 0;
      }

      @Override
      public void readArrayEnd() throws IOException {

      }

      @Override
      public void readArrayEnd(boolean arg0) throws IOException {

      }

      @Override
      public BigInteger readBigInteger() throws IOException {

        return null;
      }

      @Override
      public boolean readBoolean() throws IOException {

        return false;
      }

      @Override
      public byte readByte() throws IOException {

        return 0;
      }

      @Override
      public byte[] readByteArray() throws IOException {

        return null;
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {

        return null;
      }

      @Override
      public double readDouble() throws IOException {

        return 0;
      }

      @Override
      public float readFloat() throws IOException {

        return 0;
      }

      @Override
      public int readInt() throws IOException {

        return 0;
      }

      @Override
      public long readLong() throws IOException {

        return 0;
      }

      @Override
      public int readMapBegin() throws IOException {

        return 0;
      }

      @Override
      public void readMapEnd() throws IOException {

      }

      @Override
      public void readMapEnd(boolean arg0) throws IOException {

      }

      @Override
      public void readNil() throws IOException {

      }

      @Override
      public short readShort() throws IOException {

        return 0;
      }

      @Override
      public String readString() throws IOException {

        return null;
      }

      @Override
      public Value readValue() throws IOException {

        return null;
      }

      @Override
      public void resetReadByteCount() {

      }

      @Override
      public void setArraySizeLimit(int arg0) {

      }

      @Override
      public void setMapSizeLimit(int arg0) {

      }

      @Override
      public void setRawSizeLimit(int arg0) {

      }

      @Override
      public void skip() throws IOException {

      }

      @Override
      public boolean trySkipNil() throws IOException {

        return false;
      }
    });

    /*
     * test
     */
    target.readFrom(unpacker);

    /*
     * check
     */
    verify(unpacker, times(1)).readArrayBegin();
    verify(unpacker, times(1)).readArrayEnd();
  }

}
