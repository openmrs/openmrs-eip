package org.openmrs.sync.odoo.service.impl;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.PersonModel;
import org.openmrs.sync.odoo.config.OdooProperties;
import org.openmrs.sync.odoo.exeption.OdooException;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OdooServiceXmlRpcImplTest {

    @Mock
    private XmlRpcClientProvider provider;

    @Mock
    private XmlRpcClient client;

    @Captor
    private ArgumentCaptor<XmlRpcClientConfigImpl> captorConfig;

    @Captor
    private ArgumentCaptor<String> captorMethod;

    @Captor
    private ArgumentCaptor<List> captorParams;

    private OdooServiceXmlRpcImpl service;

    private static final String URL = "http://localhost:8080";
    private static final String DB_NAME = "dbName";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(provider.getClient()).thenReturn(client);
    }

    @Test
    public void authenticate_should_return_uid() throws XmlRpcException {
        // Given
        OdooProperties odooProperties = new OdooProperties();
        odooProperties.setUrl(URL);
        odooProperties.setDbName(DB_NAME);
        odooProperties.setUsername(USER_NAME);
        odooProperties.setPassword(PASSWORD);
        service = new OdooServiceXmlRpcImpl(odooProperties, provider);
        when(client.execute(captorConfig.capture(), captorMethod.capture(), captorParams.capture())).thenReturn(1);

        // When
        int result = service.authenticate();

        // Then
        verify(client).execute(any(XmlRpcClientConfigImpl.class), any(), anyList());
        assertEquals(URL + "/xmlrpc/2/common", captorConfig.getValue().getServerURL().toString());
        assertEquals("authenticate", captorMethod.getValue());
        assertEquals(DB_NAME, captorParams.getValue().get(0));
        assertEquals(USER_NAME, captorParams.getValue().get(1));
        assertEquals(PASSWORD, captorParams.getValue().get(2));
        assertEquals(1, result);
    }

    @Test
    public void authenticate_should_throw_exception() {
        // Given
        OdooProperties odooProperties = new OdooProperties();
        odooProperties.setUsername("username");
        service = new OdooServiceXmlRpcImpl(odooProperties, provider);

        // When
        try {
            service.authenticate();

            fail();
        } catch (Exception e) {
            // Then
            assertTrue(e instanceof OdooException);
            assertTrue(e.getCause() instanceof MalformedURLException);
            assertEquals("Error while authenticating to Odoo server", e.getMessage());
        }
    }

    @Test
    public void sendToOdoo_should_return_new_id() throws XmlRpcException {
        // Given
        OdooProperties odooProperties = new OdooProperties();
        odooProperties.setUrl(URL);
        odooProperties.setDbName(DB_NAME);
        odooProperties.setUsername(USER_NAME);
        odooProperties.setPassword(PASSWORD);
        service = new OdooServiceXmlRpcImpl(odooProperties, provider);
        when(client.execute(captorMethod.capture(), captorParams.capture())).thenReturn(2);
        OdooModel odooModel = new OdooModel();
        odooModel.setType("CUSTOMER");
        Map<String, String> data = new HashMap<>();
        data.put("name", "name");
        odooModel.setData(data);

        // When
        int result = service.sendToOdoo(1, odooModel);

        // Then
        verify(client).execute(any(String.class), anyList());
        assertEquals("execute_kw", captorMethod.getValue());
        assertEquals(DB_NAME, captorParams.getValue().get(0));
        assertEquals(1, captorParams.getValue().get(1));
        assertEquals(PASSWORD, captorParams.getValue().get(2));
        assertEquals("res.partner", captorParams.getValue().get(3));
        assertEquals("create", captorParams.getValue().get(4));
        assertEquals(data, ((List) captorParams.getValue().get(5)).get(0));
        assertEquals(2, result);
    }

    @Test
    public void sendToOdoo_should_throw_exception() {
        // Given
        OdooProperties odooProperties = new OdooProperties();
        service = new OdooServiceXmlRpcImpl(odooProperties, provider);
        OdooModel odooModel = new OdooModel();
        odooModel.setType("CUSTOMER");
        Map<String, String> data = new HashMap<>();
        data.put("name", "name");
        odooModel.setData(data);

        // When
        try {
            service.sendToOdoo(1, odooModel);

            fail();
        } catch (Exception e) {
            // Then
            assertTrue(e instanceof OdooException);
            assertTrue(e.getCause() instanceof MalformedURLException);
            assertEquals("Error while sending value to Odoo server", e.getMessage());
        }
    }

}
