package org.openmrs.eip.app;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.junit.After;
import org.junit.Test;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class OpenmrsLoadPersonITest extends OpenmrsLoadEndpointITest {

    @Autowired
    private SyncEntityRepository<Person> repository;

    @Test
    public void load() {
        // Given
        Exchange exchange = new DefaultExchange(camelContext);
        assertEquals(1, repository.findAll().size());
        exchange.getIn().setBody(getPersonModel());

        // When
        template.send(exchange);

        // Then
        assertEquals(2, repository.findAll().size());
    }

    // TEAR-DOWN
    @After
    public void after() {
        Person p = repository.findByUuid("818b4ee6-8d68-4849-975d-80ab98016677");
        repository.delete(p);
    }

    private SyncModel getPersonModel() {
        return JsonUtils.unmarshalSyncModel("{" +
                "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\"," +
                "\"model\":{" +
                "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," +
                "\"creatorUuid\":\"" + UserLight.class.getName() + "(1)\"," +
                "\"dateCreated\":\"2019-05-28T13:42:31+00:00\"," +
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
                "},\"metadata\":{\"operation\":\"c\"}" +
                "}");
    }
}
