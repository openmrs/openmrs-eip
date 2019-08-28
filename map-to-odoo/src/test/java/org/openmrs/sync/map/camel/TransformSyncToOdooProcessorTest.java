package org.openmrs.sync.map.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.openmrs.sync.common.model.sync.PatientModel;
import org.openmrs.sync.common.model.sync.PersonModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TransformSyncToOdooProcessorTest {

    private TransformSyncToOdooProcessor processor = new TransformSyncToOdooProcessor();

    @Test
    public void process_should_transform_model_and_put_in_body() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(getPersonJson());

        // When
        processor.process(exchange);

        // Then
        String body = (String) exchange.getIn().getBody();
        assertEquals(json(), body);
    }

    @Test
    public void process_should_put_null_in_body() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(getPatientJson());

        // When
        processor.process(exchange);

        // Then
        String body = (String) exchange.getIn().getBody();
        assertNull(body);
    }

    private String getPersonJson() {
        return "{" +
                    "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," +
                        "\"creatorUuid\":\"org.openmrs.sync.component.entity.light.UserLight(1)\"," +
                        "\"dateCreated\":[2019,5,28,13,42,31]," +
                        "\"changedByUuid\":null," +
                        "\"dateChanged\":null," +
                        "\"voided\":false," +
                        "\"voidedByUuid\":null," +
                        "\"dateVoided\":null," +
                        "\"voidReason\":null," +
                        "\"gender\":\"F\"," +
                        "\"birthdate\":\"1982-01-06\"," +
                        "\"birthdateEstimated\":false," +
                        "\"dead\":false," +
                        "\"deathDate\":null," +
                        "\"causeOfDeathUuid\":null," +
                        "\"deathdateEstimated\":false," +
                        "\"birthtime\":null" +
                    "}" +
                "}";
    }

    private String getPatientJson() {
        return "{" +
                "\"tableToSyncModelClass\":\"" + PatientModel.class.getName() + "\"," +
                "\"model\":{" +
                "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," +
                "\"creatorUuid\":\"org.openmrs.sync.component.entity.light.UserLight(1)\"," +
                "\"dateCreated\":[2019,5,28,13,42,31]," +
                "\"changedByUuid\":null," +
                "\"dateChanged\":null," +
                "\"voided\":false," +
                "\"voidedByUuid\":null," +
                "\"dateVoided\":null," +
                "\"voidReason\":null" +
                "}" +
                "}";
    }

    private String json() {
        return "{\"type\":\"CUSTOMER\",\"data\":{\"name\":\"test\"}}";
    }
}
