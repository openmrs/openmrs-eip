package org.openmrs.sync.core.camel.load;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;

@Sql("classpath:org/openmrs/sync/core/camel/load/init_person_load.sql")
public class OpenMrsLoadPersonITest extends OpenMrsLoadEndpointITest {

    @Autowired
    private SyncEntityRepository<Person> repository;

    @Test
    public void load() {
        // Given
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("OpenMrsTableSyncName", TableToSyncEnum.PERSON.name());
        exchange.getIn().setBody(getPersonJson());

        // When
        template.send(exchange);

        // Then
        assertEquals(2, repository.findAll().size());
    }

    private String getPersonJson() {
        return "{" +
                    "\"tableToSync\":\"" + TableToSyncEnum.PERSON + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," +
                        "\"creatorUuid\":1," +
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
                        "\"causeOfDeathClassUuid\":null," +
                        "\"causeOfDeathDatatypeUuid\":null," +
                        "\"deathdateEstimated\":false," +
                        "\"birthtime\":null" +
                    "}" +
                "}";
    }
}
