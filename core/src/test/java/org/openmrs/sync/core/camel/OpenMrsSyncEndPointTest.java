package org.openmrs.sync.core.camel;

import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.ScheduledPollConsumerScheduler;
import org.apache.camel.support.DefaultScheduledPollConsumerScheduler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import static org.junit.Assert.*;

public class OpenMrsSyncEndPointTest {

    @Mock
    private EntityServiceFacade entityServiceFacade;

    @Mock
    private OpenMrsSyncComponent component;

    private OpenMrsSyncEndpoint endpoint;

    private static final int DELAY = 10000;
    private static final ScheduledPollConsumerScheduler SCHEDULER = new DefaultScheduledPollConsumerScheduler();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        endpoint = new OpenMrsSyncEndpoint("openmrsSync", component, entityServiceFacade);
        endpoint.setDelay(DELAY);
        endpoint.setScheduler(SCHEDULER);
    }

    @Test
    public void createProducer() throws Exception {
        // Given

        // When
        Producer result = endpoint.createProducer();

        // Then
        assertNotNull(result);
    }

    @Test
    public void createConsumer() throws Exception {
        // Given
        Processor processor = exchange -> {};

        // When
        OpenMrsSyncConsumer result = endpoint.createConsumer(processor);

        // Then
        assertNotNull(result);
        assertEquals(DELAY, result.getDelay());
        assertEquals(SCHEDULER, result.getScheduler());
    }

    @Test
    public void isSingleton() {
        assertTrue(endpoint.isSingleton());
    }
}
