package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OpenMrsExtractProducerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private EntityServiceFacade serviceFacade;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    private LocalDateTime lastSyncDate = LocalDateTime.now();

    private OpenMrsExtractProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        producer = new OpenMrsExtractProducer(endpoint, serviceFacade, EntityNameEnum.PERSON, lastSyncDate);
    }

    @Test
    public void process() {
        // Given
        PersonModel model1 = new PersonModel();
        model1.setUuid("uuid1");
        PersonModel model2 = new PersonModel();
        model2.setUuid("uuid2");
        when(serviceFacade.getModels(EntityNameEnum.PERSON, lastSyncDate)).thenReturn(Arrays.asList(model1, model2));

        // When
        producer.process(exchange);

        // Then
        List<String> json = (List<String>) exchange.getIn().getBody();
        assertEquals(2, json.size());
        assertEquals(expectedJson("uuid1"), json.get(0));
        assertEquals(expectedJson("uuid2"), json.get(1));
    }

    private String expectedJson(final String uuid) {
        return "{\"uuid\":\"" + uuid + "\"," +
                "\"creatorUuid\":null," +
                "\"dateCreated\":null," +
                "\"changedByUuid\":null," +
                "\"dateChanged\":null," +
                "\"voided\":false," +
                "\"voidedByUuid\":null," +
                "\"dateVoided\":null," +
                "\"voidReason\":null," +
                "\"gender\":null," +
                "\"birthdate\":null," +
                "\"birthdateEstimated\":false," +
                "\"dead\":false," +
                "\"deathDate\":null," +
                "\"causeOfDeathUuid\":null," +
                "\"causeOfDeathClassUuid\":null," +
                "\"causeOfDeathDatatypeUuid\":null," +
                "\"deathdateEstimated\":false," +
                "\"birthtime\":null" +
                "}";
    }
}
