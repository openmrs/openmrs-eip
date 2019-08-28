package org.openmrs.sync.odoo.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.PersonModel;
import org.openmrs.sync.odoo.service.OdooModelEnum;
import org.openmrs.sync.odoo.service.OdooService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class OpenMrsOdooProducerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private OdooService odooService;

    @Captor
    private ArgumentCaptor<OdooModel> argumentCaptor;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    private OpenMrsOdooProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        producer = new OpenMrsOdooProducer(endpoint, odooService);
    }

    @Test
    public void process() {
        // Given
        exchange.getIn().setBody(json());
        doNothing().when(odooService).sendModel(argumentCaptor.capture());

        // When
        producer.process(exchange);

        // Then
        assertEquals(expectedModel(), argumentCaptor.getValue());
    }

    private String json() {
        return "{" +
                    "\"type\":\"CUSTOMER\"," +
                    "\"data\":{" +
                        "\"name\":\"Gégé\"" +
                    "}" +
                "}";
    }

    private OdooModel expectedModel() {
        OdooModel model = new OdooModel();
        model.setType(OdooModelEnum.PARTNER.getIncomingType());
        model.setData(Collections.singletonMap("name", "Gégé"));
        return model;
    }
}
