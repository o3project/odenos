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

package org.o3project.odenos.component.slicer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

/**
 * Test class for BasicSliceCondition.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ BasicSliceCondition.class })
@PowerMockIgnore({"javax.management.*"})
public class BasicSliceConditionTest {

  private BasicSliceCondition target;

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

    target = Mockito.spy(new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort"));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;
  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#BasicSliceCondition(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   * @throws Exception
   */
  @Test
  public void testBasicSliceCondition() throws Exception {

    /*
     * test
     */
    BasicSliceCondition target = new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort");
    /*
     * check
     */
    String id = Whitebox.getInternalState(target, "id");
    assertThat(id, is("Id"));

    String type = Whitebox.getInternalState(target, "type");
    assertThat(type, is("Type"));

    String connection = Whitebox.getInternalState(target, "connection");
    assertThat(connection, is("Connection"));

    String node = Whitebox.getInternalState(target, "matchInNode");
    assertThat(node, is("InNode"));

    String port = Whitebox.getInternalState(target, "matchInPort");
    assertThat(port, is("InPort"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#getInNode()}.
   */
  @Test
  public void testGetInNode() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "matchInNode", "NewInNode");

    /*
     * test
     */
    String result = target.getInNode();

    /*
     * check
     */
    assertThat(result, is("NewInNode"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#getInPort()}.
   */
  @Test
  public void testGetInPort() {

    /*
     * setting
     */
    Whitebox.setInternalState(target, "matchInPort", "NewInPort");

    /*
     * test
     */
    String result = target.getInPort();

    /*
     * check
     */
    assertThat(result, is("NewInPort"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#setInNode(java.lang.String)}.
   */
  @Test
  public void testSetInNode() {

    /*
     * test
     */
    target.setInNode("NewInNode");

    /*
     * check
     */
    assertThat(target.getInNode(), is("NewInNode"));

    String node = Whitebox.getInternalState(target, "matchInNode");
    assertThat(node, is("NewInNode"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#setInPort(java.lang.String)}.
   */
  @Test
  public void testSetInPort() {

    /*
     * test
     */
    target.setInPort("NewInPort");

    /*
     * check
     */
    assertThat(target.getInPort(), is("NewInPort"));

    String port = Whitebox.getInternalState(target, "matchInPort");
    assertThat(port, is("NewInPort"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#readFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception
   */
  @Test
  public void testReadFrom() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort"));

    Unpacker upk = Mockito.mock(Unpacker.class);

    doNothing().when(target).doReadFrom(upk);

    doReturn(5).when(upk).readMapBegin();
    doNothing().when(upk).readMapEnd();

    /*
     * test
     */
    target.readFrom(upk);

    /*
     * check
     */
    verify(target, times(5)).doReadFrom(upk);

    InOrder upkOrder = Mockito.inOrder(upk);
    upkOrder.verify(upk).readMapBegin();
    upkOrder.verify(upk).readMapEnd();
    upkOrder.verifyNoMoreInteractions();

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#doReadFrom(org.msgpack.unpacker.Unpacker)}.
   * @throws Exception
   */
  @Test
  public void testDoReadFrom() throws Exception {
    /*
     * setting
     */
    target = PowerMockito.spy(new BasicSliceCondition("Id", "Type",
        "Connection", "InNode", "InPort"));

    Unpacker upk = Mockito.mock(Unpacker.class);

    when(upk.readString())
        .thenReturn("id", "Id")
        .thenReturn("type", "Type")
        .thenReturn("connection", "Connection")
        .thenReturn("in_node", "InNode")
        .thenReturn("in_port", "InPort");

    /*
     * test
     */
    /* id */
    target.doReadFrom(upk);
    /* type */
    target.doReadFrom(upk);
    /* connection */
    target.doReadFrom(upk);
    /* in_node */
    target.doReadFrom(upk);
    /* in_port */
    target.doReadFrom(upk);

    /*
     * check
     */
    verify(upk, times(10)).readString();

    verify(target).setId("Id");
    verify(target).setType("Type");
    verify(target).setConnection("Connection");
    verify(target).setInNode("InNode");
    verify(target).setInPort("InPort");

    assertThat(target.getId(), is("Id"));
    assertThat(target.getType(), is("Type"));
    assertThat(target.getConnection(), is("Connection"));
    assertThat(target.getInNode(), is("InNode"));
    assertThat(target.getInPort(), is("InPort"));

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#writeTo(org.msgpack.packer.Packer)}.
   * @throws Exception
   */
  @Test
  public void testWriteTo() throws Exception {

    /*
     * setting
     */
    Packer pk = Mockito.mock(Packer.class);

    doReturn(pk).when(pk).writeMapBegin(5);
    doReturn(pk).when(pk).write(anyString());
    doReturn(pk).when(pk).writeMapEnd();

    /*
     * test
     */
    target.writeTo(pk);

    /*
     * check
     */
    verify(pk).writeMapBegin(5);
    verify(pk, times(10)).write(anyString());
    verify(pk).writeMapEnd();

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#doWriteTo(org.msgpack.packer.Packer)}.
   * @throws Exception
   */
  @Test
  public void testDoWriteTo() throws Exception {

    /*
     * setting
     */
    Packer pk = Mockito.mock(Packer.class);

    doReturn(pk).when(pk).write(anyString());

    /*
     * test
     */
    target.doWriteTo(pk);

    /*
     * check
     */
    verify(pk, times(10)).write(anyString());

    InOrder inOrder = Mockito.inOrder(pk);
    inOrder.verify(pk).write("id");
    inOrder.verify(pk).write("Id");

    inOrder.verify(pk).write("type");
    inOrder.verify(pk).write("Type");

    inOrder.verify(pk).write("connection");
    inOrder.verify(pk).write("Connection");

    inOrder.verify(pk).write("in_node");
    inOrder.verify(pk).write("InNode");

    inOrder.verify(pk).write("in_port");
    inOrder.verify(pk).write("InPort");

    inOrder.verifyNoMoreInteractions();

  }

  /**
   * Test method for {@link org.o3project.odenos.component.slicer.BasicSliceCondition#toString()}.
   */
  @Test
  public void testToString() {

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[id=Id",
        "type=Type",
        "connection=Connection",
        "in_node=InNode",
        "in_port=InPort]"
    }, ",");

    assertThat(result.endsWith(expectedString), is(true));

  }

}
