package org.openmrs.sync.component.camel;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.sync.component.camel.OpenmrsLoadProducer.PROP_REBUILD_SEARCH_INDEX;
import static org.openmrs.sync.component.camel.OpenmrsLoadProducer.PROP_RESOURCE;
import static org.openmrs.sync.component.camel.OpenmrsLoadProducer.PROP_SUB_RESOURCE;

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
        exchange.getIn().setBody(json());
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
        exchange.getIn().setHeader("OpenmrsTableSyncName", "person");
        exchange.getIn().setBody(OpenmrsLoadProducer.DELETE_PREFIX + "person:" + personUuid);
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        producer.process(exchange);

        verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
        assertNull(exchange.getProperty("rebuild-search-index"));
        assertNull(exchange.getProperty("resource"));
        assertNull(exchange.getProperty("sub-resource"));
    }

    @Test
    public void process_shouldDeleteAndSetTheRelevantPropertiesForAPersonName() {
        final String nameUuid = "some-uuid";
        exchange.getIn().setHeader("OpenmrsTableSyncName", "person_name");
        exchange.getIn().setBody(OpenmrsLoadProducer.DELETE_PREFIX + "person_name:" + nameUuid);
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        producer.process(exchange);

        verify(serviceFacade).delete(TableToSyncEnum.PERSON_NAME, nameUuid);
        assertEquals(true, exchange.getProperty("rebuild-search-index"));
        assertEquals("person", exchange.getProperty(PROP_RESOURCE));
        assertEquals("name", exchange.getProperty(PROP_SUB_RESOURCE));
    }

    @Test
    public void process_shouldDeleteAndSetTheRelevantPropertiesForAPersonAttribute() {
        final String attributeUuid = "some-uuid";
        exchange.getIn().setHeader("OpenmrsTableSyncName", "person_attribute");
        exchange.getIn().setBody(OpenmrsLoadProducer.DELETE_PREFIX + "person_attribute:" + attributeUuid);
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        producer.process(exchange);

        verify(serviceFacade).delete(TableToSyncEnum.PERSON_ATTRIBUTE, attributeUuid);
        assertEquals(true, exchange.getProperty(PROP_REBUILD_SEARCH_INDEX));
        assertEquals("person", exchange.getProperty(PROP_RESOURCE));
        assertEquals("attribute", exchange.getProperty(PROP_SUB_RESOURCE));
    }

    @Test
    public void process_shouldDeleteAndSetTheRelevantPropertiesForAPatienIdentifier() {
        final String identifierUuid = "some-uuid";
        exchange.getIn().setHeader("OpenmrsTableSyncName", "patient_identifier");
        exchange.getIn().setBody(OpenmrsLoadProducer.DELETE_PREFIX + "patient_identifier:" + identifierUuid);
        when(applicationContext.getBean("entityServiceFacade")).thenReturn(serviceFacade);

        producer.process(exchange);

        verify(serviceFacade).delete(TableToSyncEnum.PATIENT_IDENTIFIER, identifierUuid);
        assertEquals(true, exchange.getProperty(PROP_REBUILD_SEARCH_INDEX));
        assertEquals("patient", exchange.getProperty(PROP_RESOURCE));
        assertEquals("identifier", exchange.getProperty(PROP_SUB_RESOURCE));
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
