package org.openmrs.sync.component.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

public class AbstractOpenMrsProducerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void constructor_should_create_producer() {
        // Given;
        ProducerParams params = ProducerParams.builder().build();

        // When
        FakeOpenMrsProducer result = new FakeOpenMrsProducer(endpoint, applicationContext, params);

        // Then
        assertNotNull(result.applicationContext);
        assertNotNull(result.getEndpoint());
        assertEquals(params, result.params);
    }
}
