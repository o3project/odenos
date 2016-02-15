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

package org.o3project.odenos.core.component.network.topology;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Link.
 */
public class LinkTest {

  private Link target;

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

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    target = Mockito.spy(new Link("123", "link_id123", "src_node123",
        "src_port123", "dst_node123", "dst_port123", attributes));

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
   * {@link org.o3project.odenos.core.component.network.topology.Link#Link()}.
   */
  @Test
  public void testLink() {

    Link result = new Link();

    assertThat(result, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#Link(java.lang.String)}
   * .
   */
  @Test
  public void testLinkWithLinkId() {

    Link result = new Link("link_id123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("link_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#Link(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testLinkWithPorts() {

    Link result =
        new Link("link_id123", "src_node123", "src_port123",
            "dst_node123", "dst_port123");

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("link_id123"));
    assertThat(result.getSrcNode(), is("src_node123"));
    assertThat(result.getSrcPort(), is("src_port123"));
    assertThat(result.getDstNode(), is("dst_node123"));
    assertThat(result.getDstPort(), is("dst_port123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#Link(org.o3project.odenos.core.component.network.topology.Link)}
   * .
   */
  @Test
  public void testLinkWithLink() {

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link param =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);
    Link result = new Link(param);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("link_id123"));
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getSrcNode(), is("src_node123"));
    assertThat(result.getSrcPort(), is("src_port123"));
    assertThat(result.getDstNode(), is("dst_node123"));
    assertThat(result.getDstPort(), is("dst_port123"));
    assertThat(result.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidate() {
    assertTrue(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidateLinkIdNull() {
    Whitebox.setInternalState(target, "linkId", (String) null);
    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidateSrcNodeNull() {
    Whitebox.setInternalState(target, "srcNode", (String) null);
    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidateSrcPortNull() {
    Whitebox.setInternalState(target, "srcPort", (String) null);
    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidateDstNodeNull() {
    Whitebox.setInternalState(target, "dstNode", (String) null);
    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#validate()}.
   */
  @Test
  public void testValidateDstPortNull() {
    Whitebox.setInternalState(target, "dstPort", (String) null);
    assertFalse(target.validate());
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getType()}.
   */
  @Test
  public void testGetType() {
    assertThat(target.getType(), is("Link"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getId()}.
   */
  @Test
  public void testGetId() {
    assertThat(target.getId(), is("link_id123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#setId(java.lang.String)}
   * .
   */
  @Test
  public void testSetId() {
    target.setId("abc");
    assertThat(target.getId(), is("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getSrcNode()}
   * .
   */
  @Test
  public void testGetSrcNode() {
    assertThat(target.getSrcNode(), is("src_node123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getSrcPort()}
   * .
   */
  @Test
  public void testGetSrcPort() {
    assertThat(target.getSrcPort(), is("src_port123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getDstNode()}
   * .
   */
  @Test
  public void testGetDstNode() {
    assertThat(target.getDstNode(), is("dst_node123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#getDstPort()}
   * .
   */
  @Test
  public void testGetDstPort() {
    assertThat(target.getDstPort(), is("dst_port123"));
  }

  /**
   * Test method for {@link
   * org.o3project.odenos.core.component.network.topology.Link#setPorts(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testSetPorts() {
    target.setPorts("abc", "def", "ghi", "jkl");
    assertThat(target.getSrcNode(), is("abc"));
    assertThat(target.getSrcPort(), is("def"));
    assertThat(target.getDstNode(), is("ghi"));
    assertThat(target.getDstPort(), is("jkl"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithoutVersion() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(7);
      pk.write("type");
      pk.write("Link");
      pk.write("link_id");
      pk.write("LINK_ID456");
      pk.write("src_node");
      pk.write("SRC_NODE456");
      pk.write("src_port");
      pk.write("SRC_PORT456");
      pk.write("dst_node");
      pk.write("DST_NODE456");
      pk.write("dst_port");
      pk.write("DST_PORT456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Link"));
    assertThat(target.getVersion(), is("123"));
    assertThat(target.getId(), is("LINK_ID456"));
    assertThat(target.getSrcNode(), is("SRC_NODE456"));
    assertThat(target.getSrcPort(), is("SRC_PORT456"));
    assertThat(target.getDstNode(), is("DST_NODE456"));
    assertThat(target.getDstPort(), is("DST_PORT456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithVersion() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(8);
      pk.write("type");
      pk.write("Link");
      pk.write("version");
      pk.write("456");
      pk.write("link_id");
      pk.write("LINK_ID456");
      pk.write("src_node");
      pk.write("SRC_NODE456");
      pk.write("src_port");
      pk.write("SRC_PORT456");
      pk.write("dst_node");
      pk.write("DST_NODE456");
      pk.write("dst_port");
      pk.write("DST_PORT456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Link"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is("LINK_ID456"));
    assertThat(target.getSrcNode(), is("SRC_NODE456"));
    assertThat(target.getSrcPort(), is("SRC_PORT456"));
    assertThat(target.getDstNode(), is("DST_NODE456"));
    assertThat(target.getDstPort(), is("DST_PORT456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum4() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(4);
      pk.write("type");
      pk.write("Link");
      pk.write("version");
      pk.write("456");
      pk.write("link_id");
      pk.write("LINK_ID456");
      pk.write("src_node");
      pk.write("SRC_NODE456");
      pk.write("src_port");
      pk.write("SRC_PORT456");
      pk.write("dst_node");
      pk.write("DST_NODE456");
      pk.write("dst_port");
      pk.write("DST_PORT456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithIllegalMessageNum9() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(9);
      pk.write("type");
      pk.write("Link");
      pk.write("version");
      pk.write("456");
      pk.write("link_id");
      pk.write("LINK_ID456");
      pk.write("src_node");
      pk.write("SRC_NODE456");
      pk.write("src_port");
      pk.write("SRC_PORT456");
      pk.write("dst_node");
      pk.write("DST_NODE456");
      pk.write("dst_port");
      pk.write("DST_PORT456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      return;
    }
    fail("could not catch an exception");
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#readFrom(Unpacker)}
   * .
   */
  @Test
  public void testReadFromWithEmptyLinkId() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("ATT456", "VAL456");
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    ByteArrayInputStream in;
    Unpacker upk = null;
    try {
      pk.writeMapBegin(8);
      pk.write("type");
      pk.write("Link");
      pk.write("version");
      pk.write("456");
      pk.write("link_id");
      pk.write("");
      pk.write("src_node");
      pk.write("SRC_NODE456");
      pk.write("src_port");
      pk.write("SRC_PORT456");
      pk.write("dst_node");
      pk.write("DST_NODE456");
      pk.write("dst_port");
      pk.write("DST_PORT456");
      pk.write("attributes");
      pk.write(attributes);
      pk.writeMapEnd();

      bytes = out.toByteArray();

      in = new ByteArrayInputStream(bytes);
      upk = msg.createUnpacker(in);
    } catch (Exception e) {
      fail("Exception in test setup");
    }
    try {
      target.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(target.getType(), is("Link"));
    assertThat(target.getVersion(), is("456"));
    assertThat(target.getId(), is(""));
    assertThat(target.getSrcNode(), is("SRC_NODE456"));
    assertThat(target.getSrcPort(), is("SRC_PORT456"));
    assertThat(target.getDstNode(), is("DST_NODE456"));
    assertThat(target.getDstPort(), is("DST_PORT456"));
    assertThat(target.getAttribute("ATT456"), is("VAL456"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteTo() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    Link link = new Link();

    try {
      link.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(link.getType(), is("Link"));
    assertThat(link.getVersion(), is("123"));
    assertThat(link.getId(), is("link_id123"));
    assertThat(link.getSrcNode(), is("src_node123"));
    assertThat(link.getSrcPort(), is("src_port123"));
    assertThat(link.getDstNode(), is("dst_node123"));
    assertThat(link.getDstPort(), is("dst_port123"));
    assertThat(link.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#writeTo(Packer)}
   * .
   */
  @Test
  public void testWriteToWithoutVersion() {
    MessagePack msg = new MessagePack();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Packer pk = msg.createPacker(out);
    byte[] bytes;
    Mockito.when(target.getVersion()).thenReturn(null);
    try {
      target.writeTo(pk);
    } catch (Exception e) {
      fail("Exception at writeTo()");
    }

    bytes = out.toByteArray();

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Unpacker upk = msg.createUnpacker(in);
    Link link = new Link();

    try {
      link.readFrom(upk);
    } catch (Exception e) {
      fail("Exception at readFrom()");
    }
    assertThat(link.getType(), is("Link"));
    assertThat(link.getVersion(), is("0"));
    assertThat(link.getId(), is("link_id123"));
    assertThat(link.getSrcNode(), is("src_node123"));
    assertThat(link.getSrcPort(), is("src_port123"));
    assertThat(link.getDstNode(), is("dst_node123"));
    assertThat(link.getDstPort(), is("dst_port123"));
    assertThat(link.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att123", "val123");
    Link link =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);
    Link link2 =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes2);

    assertTrue(link.equals(link2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithNull() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link link =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);

    assertFalse(link.equals(null));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithSameInstance() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link link =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);

    assertTrue(link.equals(link));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentClass() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link link =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);

    assertFalse(link.equals("abc"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsWithDifferentValue() {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("att456", "val456");
    Link link =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);
    Link link2 =
        new Link("456", "link_id456", "src_node456", "src_port456",
            "dst_node456",
            "dst_port456", attributes2);

    assertFalse(link.equals(link2));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#clone()}
   * .
   */
  @Test
  public void testClone() {

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link param =
        new Link("123", "link_id123", "src_node123", "src_port123",
            "dst_node123",
            "dst_port123", attributes);
    Link result = param.clone();

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("link_id123"));
    assertThat(result.getVersion(), is("123"));
    assertThat(result.getSrcNode(), is("src_node123"));
    assertThat(result.getSrcPort(), is("src_port123"));
    assertThat(result.getDstNode(), is("dst_node123"));
    assertThat(result.getDstPort(), is("dst_port123"));
    assertThat(result.getAttribute("att123"), is("val123"));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.network.topology.Link#toString()}
   * .
   */
  @Test
  public void testToString() {

    /*
     * setting
     */
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("att123", "val123");
    Link target = new Link("123", "LinkId", "SrcNode", "SrcPort",
        "DstNode", "DstPort", attributes);

    /*
     * test
     */
    String result = target.toString();

    /*
     * check
     */
    String expectedString = StringUtils.join(new String[] {
        "[version=123",
        "link_id=LinkId",
        "src_node=SrcNode",
        "src_port=SrcPort",
        "dst_node=DstNode",
        "dst_port=DstPort",
        "attributes={cost=1, att123=val123}]"
    }, ",");

    System.out.print(result);
    System.out.print(expectedString);
    assertThat(result.endsWith(expectedString), is(true));

  }
}
