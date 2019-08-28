package org.openmrs.sync.odoo.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.odoo.service.OdooServiceXmlRpc;

import static org.mockito.Mockito.when;

public class OdooServiceImplTest {

    @Mock
    private OdooServiceXmlRpc odooServiceXmlRpc;

    private OdooServiceImpl odooService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(odooServiceXmlRpc.authenticate()).thenReturn(1);

        odooService = new OdooServiceImpl(odooServiceXmlRpc);
    }

    @Test
    public void sendModel_should_call_sendToOdoo() {
        // Given
        OdooModel model = new OdooModel();
        when(odooServiceXmlRpc.sendToOdoo(1, model)).thenReturn(2);

        // When
        odooService.sendModel(model);

        // Then

    }
}
