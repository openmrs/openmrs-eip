package org.openmrs.sync.component.camel.extract;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.camel.extract.fetchmodels.FetchModelsRuleEngine;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class OpenMrsExtractComponentTest {

    @Mock
    private CamelContext context;

    @Mock
    private FetchModelsRuleEngine ruleEngine;

    private OpenMrsExtractComponent component;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        component = new OpenMrsExtractComponent(context, ruleEngine);
    }

    @Test
    public void createEndPoint_should_return_endpoint() {
        // Given

        // When
        Endpoint result = component.createEndpoint("testUri", "person", new HashMap<>());

        // Then
        assertTrue(result instanceof OpenMrsExtractEndpoint);
    }
}
