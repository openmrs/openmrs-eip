package org.openmrs.sync.odoo.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.odoo.service.OdooService;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class OpenMrsOdooComponentTest {

    @Mock
    private CamelContext context;

    @Mock
    private OdooService odooService;

    private OpenMrsOdooComponent component;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        component = new OpenMrsOdooComponent(context, odooService);
    }

    @Test
    public void createEndPoint_should_return_endpoint() {
        // Given

        // When
        Endpoint result = component.createEndpoint("testUri", "", new HashMap<>());

        // Then
        assertTrue(result instanceof OpenMrsOdooEndpoint);
    }
}
