package org.openmrs.sync.core.camel;

import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OpenMrsSyncComponentTest {

    @Mock
    private EntityServiceFacade facade;

    public OpenMrsSyncComponent component;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        component = new OpenMrsSyncComponent(facade);
        component.setCamelContext(new DefaultCamelContext());
    }

    @Test
    public void getEndpoint() throws Exception {
        // Given

        // When
        OpenMrsSyncEndpoint result = component.createEndpoint("openmrsSync", "person", new HashMap<>());

        // Then
        assertNotNull(result);
        assertEquals(EntityNameEnum.PERSON, component.getEntityName());
    }
}
