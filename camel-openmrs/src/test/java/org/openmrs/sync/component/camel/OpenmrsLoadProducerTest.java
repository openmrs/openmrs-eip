package org.openmrs.sync.component.camel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.model.PersonModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.springframework.context.ApplicationContext;

public class OpenmrsLoadProducerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EntityServiceFacade serviceFacade;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    private OpenmrsLoadProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        producer = new OpenmrsLoadProducer(endpoint, applicationContext, ProducerParams.builder().build());
    }

    @Test
    public void process() {
        // Given
        exchange.getIn().setHeader("OpenmrsTableSyncName", "person");
        exchange.getIn().setBody(json());
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        // When
        producer.process(exchange);

        // Then
        PersonModel model = new PersonModel();
        model.setUuid("uuid");
        verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
    }


    private String json() {
        return "{" +
                    "\"tableToSyncModelClass\": \"" + PersonModel.class.getName() + "\"," +
                    "\"model\": {" +
                        "\"uuid\":\"uuid\"," +
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
                        "\"deathdateEstimated\":false," +
                        "\"birthtime\":null" +
                    "}" +
                "}";
    }
}
