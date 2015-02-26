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

package org.o3project.odenos.core.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.o3project.odenos.core.manager.system.ComponentConnection;
import org.o3project.odenos.remoteobject.ObjectProperty;
import org.o3project.odenos.remoteobject.ObjectSettings;
import org.o3project.odenos.remoteobject.manager.ComponentTypesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertiesHash;
import org.o3project.odenos.remoteobject.manager.ObjectPropertyList;
import org.o3project.odenos.remoteobject.message.Request;
import org.o3project.odenos.remoteobject.message.Response;
import org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for SystemManagerInterface.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemManagerInterface.class })
public class SystemManagerInterfaceTest {

  private SystemManagerInterface target;

  private MessageDispatcher dispatcher;

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

    dispatcher = Mockito.mock(MessageDispatcher.class);

    target = Mockito.spy(new SystemManagerInterface(dispatcher));

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {

    target = null;

    dispatcher = null;
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#SystemManagerInterface(org.o3project.odenos.remoteobject.messagingclient.MessageDispatcher)}
   * .
   */
  @Test
  public void testSystemManagerInterface() {

    target = new SystemManagerInterface(null);

    assertThat(target, is(notNullValue()));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getSystemManagerId()}
   * .
   */
  @Test
  public void testGetSystemManagerId() {

    /*
     * setting
     */
    dispatcher = Mockito.mock(MessageDispatcher.class);
    target = new SystemManagerInterface(dispatcher);

    doReturn("SystemManagerId").when(dispatcher).getSystemManagerId();

    /*
     * test
     */
    String result = target.getSystemManagerId();

    /*
     * check
     */
    assertThat(result, is("SystemManagerId"));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getDispatcher()}
   * .
   */
  @Test
  public void testGetDispatcher() {

    /*
     * setting
     */
    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn("SystemManagerId").when(dispatcher).getSystemManagerId();

    Whitebox.setInternalState(target, "dispatcher", dispatcher);

    /*
     * test
     */
    MessageDispatcher result = target.getDispatcher();

    /*
     * check
     */
    assertThat(result, is(dispatcher));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#setDispatcher(MessageDispatcher)}
   * .
   */
  @Test
  public void testSetDispatcher() {

    /*
     * setting
     */
    dispatcher = Mockito.mock(MessageDispatcher.class);
    doReturn("NewSystemManagerId").when(dispatcher).getSystemManagerId();

    /*
     * test
     */
    target.setDispatcher(dispatcher);

    /*
     *
     */
    assertThat(target.getDispatcher(), is(dispatcher));

    MessageDispatcher result = target.getDispatcher();
    assertThat(result, is(dispatcher));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getProperty()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetProperty() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("property");

    /*
     * test
     */
    ObjectProperty result = target.getProperty();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getProperty()}
   * .
   */
  @Test
  public void testGetProperty_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("property");

    /*
     * test
     */
    ObjectProperty result = target.getProperty();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getProperty()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetProperty_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectProperty.class);
    doReturn(response).when(target).getObjectToSystemMng("property");

    /*
     * test
     */
    ObjectProperty result = target.getProperty();

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getSettings()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetSettings() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectSettings.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("settings");

    /*
     * test
     */
    ObjectSettings result = target.getSettings();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectSettings.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getSettings()}
   * .
   */
  @Test
  public void testGetSettings_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("settings");

    /*
     * test
     */
    ObjectSettings result = target.getSettings();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getSettings()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetSettings_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectSettings.class);
    doReturn(response).when(target).getObjectToSystemMng("settings");

    /*
     * test
     */
    ObjectSettings result = target.getSettings();

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectSettings.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentMngs()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentMngs() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectPropertyList.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("component_managers");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentMngs();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectPropertyList.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentMngs()}
   * .
   */
  @Test
  public void testGetComponentMngs_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("component_managers");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentMngs();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentMngs()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentMngs_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectPropertyList.class);
    doReturn(response).when(target).getObjectToSystemMng("component_managers");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentMngs();

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectPropertyList.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getEventMngs()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetEventMngs() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("event_managers");

    /*
     * test
     */
    ObjectProperty result = target.getEventMngs();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getEventMngs()}
   * .
   */
  @Test
  public void testGetEventMngs_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("event_managers");

    /*
     * test
     */
    ObjectProperty result = target.getEventMngs();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getEventMngs()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetEventMngs_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectProperty.class);
    doReturn(response).when(target).getObjectToSystemMng("event_managers");

    /*
     * test
     */
    ObjectProperty result = target.getEventMngs();

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentTypes()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentTypes() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ComponentTypesHash.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("component_types");

    /*
     * test
     */
    ComponentTypesHash result = target.getComponentTypes();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ComponentTypesHash.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentTypes()}
   * .
   */
  @Test
  public void testGetComponentTypes_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("component_types");

    /*
     * test
     */
    ComponentTypesHash result = target.getComponentTypes();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentTypes()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentTypes_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ComponentTypesHash.class);
    doReturn(response).when(target).getObjectToSystemMng("component_types");

    /*
     * test
     */
    ComponentTypesHash result = target.getComponentTypes();

    /*
     * check
     */
    verify(response, times(1)).getBody(ComponentTypesHash.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponents()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponents() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectPropertiesHash.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("components");

    /*
     * test
     */
    ObjectPropertiesHash result = target.getComponents();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectPropertiesHash.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponents()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponents_FailToGet() throws Exception {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("components");

    /*
     * test
     */
    ObjectPropertiesHash result = target.getComponents();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponents()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponents_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectPropertiesHash.class);
    doReturn(response).when(target).getObjectToSystemMng("components");

    /*
     * test
     */
    ObjectPropertiesHash result = target.getComponents();

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectPropertiesHash.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnections()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetConnections() throws Exception {

    /*
     * setting
     */
    ComponentConnection settingConnection = Mockito.mock(ComponentConnection.class);

    Response settingResponse = Mockito.spy(new Response(Response.OK, settingConnection));

    doReturn(settingResponse).when(target).getObjectToSystemMng("connections");
    doReturn(new HashMap<String, ComponentConnection>()).when(settingResponse).getBodyAsMap(
        ComponentConnection.class);

    /*
     * test
     */
    Map<String, ComponentConnection> result = target.getConnections();

    /*
     * check
     */
    verify(settingResponse, times(1)).getBodyAsMap(ComponentConnection.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnections()}
   * .
   */
  @Test
  public void testGetConnections_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("connections");

    /*
     * test
     */
    Map<String, ComponentConnection> result = target.getConnections();

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnections()}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetConnections_FailToGetAsMap() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBodyAsMap(ComponentConnection.class);
    doReturn(response).when(target).getObjectToSystemMng("connections");

    /*
     * test
     */
    Map<String, ComponentConnection> result = target.getConnections();

    /*
     * check
     */
    verify(response, times(1)).getBodyAsMap(ComponentConnection.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentManager(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetCompoentManager() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng(
        "component_managers/CompoentManagerId");

    /*
     * test
     */
    ObjectProperty result = target.getComponentManager("CompoentManagerId");

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentManager(java.lang.String)}
   * .
   */
  @Test
  public void testGetCompoentManager_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("component_managers/CompoentManagerId");

    /*
     * test
     */
    ObjectProperty result = target.getComponentManager("CompoentManagerId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentManager(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetCompoentManager_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectProperty.class);
    doReturn(response).when(target)
        .getObjectToSystemMng("component_managers/CompoentManagerId");

    /*
     * test
     */
    ObjectProperty result = target.getComponentManager("CompoentManagerId");

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentManager(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetCompoentManager_CompoentManagerIdNull() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("component_managers/null");

    /*
     * test
     */
    ObjectProperty result = target.getComponentManager(null);

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentType(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentType() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectPropertyList.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target)
        .getObjectToSystemMng("component_types/ComponentType");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentType("ComponentType");

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectPropertyList.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentType(java.lang.String)}
   * .
   */
  @Test
  public void testGetComponentType_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("component_types/ComponentType");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentType("ComponentType");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentType(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentType_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectPropertyList.class);
    doReturn(response).when(target).getObjectToSystemMng("component_types/ComponentType");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentType("ComponentType");

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectPropertyList.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentType(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentType_CompoentManagerIdNull() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectPropertyList.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("component_types/null");

    /*
     * test
     */
    ObjectPropertyList result = target.getComponentType(null);

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectPropertyList.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponent(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponent() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("components/ComponentId");

    /*
     * test
     */
    ObjectProperty result = target.getComponent("ComponentId");

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponent(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponent_FailToGet() throws Exception {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("components/ComponentId");

    /*
     * test
     */
    ObjectProperty result = target.getComponent("ComponentId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponent(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponent_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectProperty.class);
    doReturn(response).when(target).getObjectToSystemMng("components/ComponentId");

    /*
     * test
     */
    ObjectProperty result = target.getComponent("ComponentId");

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponent(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponent_ComponentIdNull() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("components/null");

    /*
     * test
     */
    ObjectProperty result = target.getComponent(null);

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnection(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetConnection() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ComponentConnection.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("connections/ConnectionId");

    /*
     * test
     */
    ComponentConnection result = target.getConnection("ConnectionId");

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ComponentConnection.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnection(java.lang.String)}
   * .
   */
  @Test
  public void testGetConnection_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("connections/ConnectionId");

    /*
     * test
     */
    ComponentConnection result = target.getConnection("ConnectionId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnection(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetConnection_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ComponentConnection.class);
    doReturn(response).when(target).getObjectToSystemMng("connections/ConnectionId");

    /*
     * test
     */
    ComponentConnection result = target.getConnection("ConnectionId");

    /*
     * check
     */
    verify(response, times(1)).getBody(ComponentConnection.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getConnection(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetConnection_ConnectionIdNull() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ComponentConnection.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("connections/null");

    /*
     * test
     */
    ComponentConnection result = target.getConnection(null);

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ComponentConnection.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getComponentManager(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetComponentManager() {

    /*
     * setting
     */
    Response settingResponse =
        new Response(Response.OK, new ObjectProperty("ObjectType", "ObjectId"));
    doReturn(settingResponse).when(target).getObjectToSystemMng(
        "component_managers/ComponentManagerId");

    /*
     * test
     */
    ObjectProperty result = target.getComponentManager("ComponentManagerId");

    /*
     * check
     */
    verify(target).getObjectToSystemMng(eq("component_managers/ComponentManagerId"));

    assertThat(result, is(ObjectProperty.class));
  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObject(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObject() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("objects/ObjectId");

    /*
     * test
     */
    ObjectProperty result = target.getObject("ObjectId");

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObject(java.lang.String)}
   * .
   */
  @Test
  public void testGetObject_FailToGet() {

    /*
     * setting
     */
    doReturn(null).when(target).getObjectToSystemMng("objects/ObjectId");

    /*
     * test
     */
    ComponentConnection result = target.getConnection("ConnectionId");

    /*
     * check
     */
    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObject(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObject_FailToGetBody() throws Exception {

    /*
     * setting
     */
    Response response = Mockito.spy(new Response(Response.INTERNAL_SERVER_ERROR, new Object()));
    doThrow(new RuntimeException()).when(response).getBody(ObjectProperty.class);
    doReturn(response).when(target).getObjectToSystemMng("objects/ObjectId");

    /*
     * test
     */
    ObjectProperty result = target.getObject("ObjectId");

    /*
     * check
     */
    verify(response, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(nullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObject(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObject_ObjectIdNull() throws Exception {

    /*
     * setting
     */
    Object settingBody = Mockito.mock(ObjectProperty.class);
    Response settingResponse = Mockito.spy(new Response(Response.OK, settingBody));
    doReturn(settingResponse).when(target).getObjectToSystemMng("objects/null");

    /*
     * test
     */
    ObjectProperty result = target.getObject(null);

    /*
     * check
     */
    verify(settingResponse, times(1)).getBody(ObjectProperty.class);

    assertThat(result, is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPutProperty() {

    /*
     * setting
     */
    ObjectProperty body = Mockito.mock(ObjectProperty.class);

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("property"),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.putProperty(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("property"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putProperty(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPutProperty_BodyNull() {

    /*
     * setting
     */
    ObjectProperty body = null;

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("property"),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.putProperty(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("property"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testPutSettings() {

    /*
     * setting
     */
    ObjectSettings body = Mockito.mock(ObjectSettings.class);

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("settings"),
        (ObjectSettings) anyObject());

    /*
     * test
     */
    Response result = target.putSettings(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("settings"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putSettings(org.o3project.odenos.remoteobject.ObjectSettings)}
   * .
   */
  @Test
  public void testPutSettings_BodyNUll() {

    /*
     * setting
     */
    ObjectSettings body = null;

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("settings"),
        (ObjectSettings) anyObject());

    /*
     * test
     */
    Response result = target.putSettings(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("settings"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putConnection(org.o3project.odenos.core.manager.system.ComponentConnection)}
   * .
   */
  @Test
  public void testPutConnection() {

    /*
     * setting
     */
    ComponentConnection body =
        new ComponentConnection("ObjectId", "ConnectionType", "ConnectionState");

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("connections/ObjectId"),
        (ComponentConnection) anyObject());

    /*
     * test
     */
    Response result = target.putConnection(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("connections/ObjectId"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putConnection(org.o3project.odenos.core.manager.system.ComponentConnection)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutConnection_BodyNull() throws Exception {

    /*
     * setting
     */
    ComponentConnection body = null;

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(eq("connections/ObjectId"),
        (ComponentConnection) anyObject());

    /*
     * test
     */
    Response result = target.putConnection(body);

    /*
     * check
     */
    assertThat(result.statusCode, is(Response.BAD_REQUEST));
    // assertThat(result.getBody(String.class), is(notNullValue()));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postComponentMng(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPostComponentMng() {

    /*
     * setting
     */
    ObjectProperty body = new ObjectProperty("DummyType", "TestId");

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(
        eq("component_managers/" + body.getObjectId()),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.putComponentMng(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("component_managers/" + body.getObjectId()),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPostComponent() {

    /*
     * setting
     */
    ObjectProperty body = Mockito.mock(ObjectProperty.class);

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).postObjectToSystemMng(eq("components"),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.postComponent(body);

    /*
     * check
     */
    verify(target, times(1)).postObjectToSystemMng(eq("components"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putComponent(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPutComponent() {

    /*
     * setting
     */
    ObjectProperty body = new ObjectProperty("DummyType", "DummyId");

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).putObjectToSystemMng(
        eq("components/" + body.getObjectId()),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.putComponent(body);

    /*
     * check
     */
    verify(target, times(1)).putObjectToSystemMng(eq("components/" + body.getObjectId()),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postComponent(org.o3project.odenos.remoteobject.ObjectProperty)}
   * .
   */
  @Test
  public void testPostComponent_BodyNull() {

    /*
     * setting
     */
    ObjectProperty body = null;

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).postObjectToSystemMng(eq("components"),
        (ObjectProperty) anyObject());

    /*
     * test
     */
    Response result = target.postComponent(body);

    /*
     * check
     */
    verify(target, times(1)).postObjectToSystemMng(eq("components"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postConnection(org.o3project.odenos.core.manager.system.ComponentConnection)}
   * .
   */
  @Test
  public void testPostConnection() {

    /*
     * setting
     */
    ComponentConnection body = Mockito.mock(ComponentConnection.class);

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).postObjectToSystemMng(eq("connections"),
        (ComponentConnection) anyObject());

    /*
     * test
     */
    Response result = target.postConnection(body);

    /*
     * check
     */
    verify(target, times(1)).postObjectToSystemMng(eq("connections"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postConnection(org.o3project.odenos.core.manager.system.ComponentConnection)}
   * .
   */
  @Test
  public void testPostConnection_BodyNull() {

    /*
     * setting
     */
    ComponentConnection body = null;

    Response settingResponse = new Response(Response.OK, body);
    doReturn(settingResponse).when(target).postObjectToSystemMng(eq("connections"),
        (ComponentConnection) anyObject());

    /*
     * test
     */
    Response result = target.postConnection(body);

    /*
     * check
     */
    verify(target, times(1)).postObjectToSystemMng(eq("connections"), anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delComponentMng(java.lang.String)}
   * .
   */
  @Test
  public void testDelComponentMng() {

    /*
     * setting
     */
    String componentManagerId = "ComponentManagerId";

    Response settingResponse = new Response(Response.OK, componentManagerId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(
        eq("component_managers/ComponentManagerId"));

    /*
     * test
     */
    Response result = target.delComponentMng(componentManagerId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("component_managers/ComponentManagerId"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delComponentMng(java.lang.String)}
   * .
   */
  @Test
  public void testDelComponentMng_IdNull() {

    /*
     * setting
     */
    String componentManagerId = null;

    Response settingResponse = new Response(Response.OK, componentManagerId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(eq("component_managers/null"));

    /*
     * test
     */
    Response result = target.delComponentMng(componentManagerId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("component_managers/null"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delComponent(java.lang.String)}
   * .
   */
  @Test
  public void testDelComponent() {

    /*
     * setting
     */
    String componentId = "ComponentId";

    Response settingResponse = new Response(Response.OK, componentId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(eq("components/ComponentId"));

    /*
     * test
     */
    Response result = target.delComponent(componentId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("components/ComponentId"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delComponent(java.lang.String)}
   * .
   */
  @Test
  public void testDelComponent_omponentIdNull() {

    /*
     * setting
     */
    String componentId = null;

    Response settingResponse = new Response(Response.OK, componentId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(eq("components/null"));

    /*
     * test
     */
    Response result = target.delComponent(componentId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("components/null"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delConnection(java.lang.String)}
   * .
   */
  @Test
  public void testDelConnection() {

    /*
     * setting
     */
    String connectionId = "ConnectionId";

    Response settingResponse = new Response(Response.OK, connectionId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(eq("connections/ConnectionId"));

    /*
     * test
     */
    Response result = target.delConnection(connectionId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("connections/ConnectionId"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delConnection(java.lang.String)}
   * .
   */
  @Test
  public void testDelConnection_ConnectionIdNull() {

    /*
     * setting
     */
    String connectionId = null;

    Response settingResponse = new Response(Response.OK, connectionId);
    doReturn(settingResponse).when(target).delObjectToSystemMng(eq("connections/null"));

    /*
     * test
     */
    Response result = target.delConnection(connectionId);

    /*
     * check
     */
    verify(target, times(1)).delObjectToSystemMng(eq("connections/null"));

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToSystemMng() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.postObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToSystemMng_BadRequest() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.BAD_REQUEST, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.postObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToSystemMng_InternalServerError() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    PowerMockito.doThrow(new RuntimeException()).when(target, "sendRequest",
        eq(Request.Method.POST), anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.postObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#postObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPostObjectToSystemMng_PathNull() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    String path = null;
    Object body = new Object();

    /*
     * test
     */
    Response result = target.postObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.POST),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToSystemMng() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.putObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToSystemMng_BadRequest() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.BAD_REQUEST, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.putObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToSystemMng_InternalServerError() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    PowerMockito.doThrow(new RuntimeException()).when(target, "sendRequest",
        eq(Request.Method.PUT), anyString(),
        anyObject());

    String path = "Path";
    Object body = new Object();

    /*
     * test
     */
    Response result = target.putObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#putObjectToSystemMng(java.lang.String, java.lang.Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testPutObjectToSystemMng_PathNull() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    String path = null;
    Object body = new Object();

    /*
     * test
     */
    Response result = target.putObjectToSystemMng(path, body);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.PUT),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToSystemMng() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.delObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToSystemMng_BadRequest() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.BAD_REQUEST, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.delObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToSystemMng_InternalServerError() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    PowerMockito.doThrow(new RuntimeException()).when(target, "sendRequest",
        eq(Request.Method.DELETE),
        anyString(), anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.delObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#delObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testDelObjectToSystemMng_PathNull() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    String path = null;

    /*
     * test
     */
    Response result = target.delObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest",
        eq(Request.Method.DELETE), anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObjectToSystemMng() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.OK, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.GET),
        anyString(),
        anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.getObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.GET),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.OK));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObjectToSystemMng_BadRequest() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    Response settingResponse = new Response(Response.BAD_REQUEST, new Object());

    PowerMockito.doReturn(settingResponse).when(target, "sendRequest", eq(Request.Method.GET),
        anyString(),
        anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.getObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.GET),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.BAD_REQUEST));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#getObjectToSystemMng(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testGetObjectToSystemMng_InternalServerError() throws Exception {

    /*
     * setting
     */
    target = PowerMockito.spy(new SystemManagerInterface(dispatcher));

    PowerMockito.doThrow(new RuntimeException()).when(target, "sendRequest",
        eq(Request.Method.GET), anyString(),
        anyObject());

    String path = "Path";

    /*
     * test
     */
    Response result = target.getObjectToSystemMng(path);

    /*
     * check
     */
    PowerMockito.verifyPrivate(target, times(1)).invoke("sendRequest", eq(Request.Method.GET),
        anyString(),
        anyObject());

    assertThat(result.statusCode, is(Response.INTERNAL_SERVER_ERROR));

  }

  /**
   * Test method for
   * {@link org.o3project.odenos.core.component.SystemManagerInterface#sendRequest(Request.Method, String, Object)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testSendRequest() throws Exception {

    /*
     * setting
     */
    doReturn("SystemManagerId").when(dispatcher).getSystemManagerId();
    doReturn(new Response(Response.OK, new Object())).when(dispatcher).requestSync(
        (Request) anyObject(), anyString());

    /*
     * test
     */
    Response result =
        Whitebox.invokeMethod(target, "sendRequest", Request.Method.GET, "Path", "Body");

    /*
     * check
     */
    verify(dispatcher).requestSync((Request) anyObject(), anyString());

    assertThat(result.statusCode, is(Response.OK));

  }

}
