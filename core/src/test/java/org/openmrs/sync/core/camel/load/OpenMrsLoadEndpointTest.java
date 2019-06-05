package org.openmrs.sync.core.camel.load;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import static org.junit.Assert.assertTrue;

public class OpenMrsLoadEndpointTest {

    @Mock
    private Component component;

    @Mock
    private EntityServiceFacade serviceFacade;

    private OpenMrsLoadEndpoint endpoint;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        endpoint = new OpenMrsLoadEndpoint("testEndPoint", component, serviceFacade);
    }

    @Test
    public void createProducer() {
        // Given

        // When
        Producer producer = endpoint.createProducer();

        // Then
        assertTrue(producer instanceof OpenMrsLoadProducer);
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
