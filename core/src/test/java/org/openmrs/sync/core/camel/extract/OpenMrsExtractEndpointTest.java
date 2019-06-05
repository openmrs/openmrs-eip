package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpenMrsExtractEndpointTest {

    @Mock
    private Component component;

    @Mock
    private EntityServiceFacade serviceFacade;

    private OpenMrsExtractEndpoint endpoint;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        endpoint = new OpenMrsExtractEndpoint("testEndPoint", component, serviceFacade, EntityNameEnum.PERSON);
    }

    @Test
    public void createProducer() {
        // Given

        // When
        Producer producer = endpoint.createProducer();

        // Then
        assertTrue(producer instanceof OpenMrsExtractProducer);
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

    @Test
    public void getLastSyncDate() {
        // Given
        LocalDateTime date = LocalDateTime.now();

        // When
        endpoint.setLastSyncDate(date);

        // Then
        assertEquals(date, endpoint.getLastSyncDate());
    }
}
