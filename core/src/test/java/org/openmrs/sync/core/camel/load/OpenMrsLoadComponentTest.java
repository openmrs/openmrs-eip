package org.openmrs.sync.core.camel.load;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class OpenMrsLoadComponentTest {

    @Mock
    private CamelContext context;

    @Mock
    private EntityServiceFacade serviceFacade;

    private OpenMrsLoadComponent component;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        component = new OpenMrsLoadComponent(context, serviceFacade);
    }

    @Test
    public void createEndPoint() {
        // Given

        // When
        Endpoint result = component.createEndpoint("testUri", "person", new HashMap<>());

        // Then
        assertTrue(result instanceof OpenMrsLoadEndpoint);
    }
}
