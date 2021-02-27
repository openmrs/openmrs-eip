package org.openmrs.eip.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OpenmrsLoadProducerTest {

    @Mock
    private OpenmrsEndpoint endpoint;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EntityServiceFacade serviceFacade;

    private Exchange exchange;

    private OpenmrsLoadProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        exchange = new DefaultExchange(new DefaultCamelContext());
        producer = new OpenmrsLoadProducer(endpoint, applicationContext, ProducerParams.builder().build());
    }

    @Test
    public void process() {
        // Given
        exchange.getIn().setHeader("OpenmrsTableSyncName", "person");
        exchange.getIn().setBody(syncModel());
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        // When
        producer.process(exchange);

        // Then
        PersonModel model = new PersonModel();
        model.setUuid("uuid");
        verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
    }

    @Test
    public void process_shouldDeleteAnEntity() {
        final String personUuid = "some-uuid";
        SyncModel syncModel = new SyncModel();
        syncModel.setTableToSyncModelClass(PersonModel.class);
        BaseModel model = new PersonModel();
        model.setUuid(personUuid);
        syncModel.setModel(model);
        SyncMetadata metadata = new SyncMetadata();
        metadata.setOperation("d");
        syncModel.setMetadata(metadata);
        exchange.getIn().setBody(syncModel);
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        producer.process(exchange);

        verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
    }

    private SyncModel syncModel() {
        return JsonUtils.unmarshalSyncModel("{" +
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
                "},\"metadata\":{\"operation\":\"c\"}" +
                "}");
    }
}
