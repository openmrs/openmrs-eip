package org.openmrs.sync.odoo.camel;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.odoo.service.OdooService;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class OpenMrsOdooEndpointTest {

    @Mock
    private Component component;

    @Mock
    private OdooService odooService;

    private OpenMrsOdooEndpoint endpoint;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        endpoint = new OpenMrsOdooEndpoint("testEndPoint", component, odooService);
    }

    @Test
    public void createProducer() {
        // Given

        // When
        Producer producer = endpoint.createProducer();

        // Then
        assertTrue(producer instanceof OpenMrsOdooProducer);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createConsumer() {
        // Given

        // When
        Consumer consumer = endpoint.createConsumer(exchange -> {});

        // Then
        // Exception
    }

    @Test
    public void isSingleton() {
        assertTrue(endpoint.isSingleton());
    }
}
