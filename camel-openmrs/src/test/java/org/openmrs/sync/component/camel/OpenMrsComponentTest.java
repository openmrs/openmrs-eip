package org.openmrs.sync.component.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpenMrsComponentTest {

    @Mock
    private CamelContext context;

    @Mock
    private ApplicationContext applicationContext;

    private OpenMrsComponent component;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        component = new OpenMrsComponent(context, applicationContext);
    }

    @Test
    public void createEndPoint_should_return_endpoint() {
        // Given

        // When
        Endpoint result = component.createEndpoint("testUri", "extract", new HashMap<>());

        // Then
        assertTrue(result instanceof OpenMrsEndpoint);
    }
}
