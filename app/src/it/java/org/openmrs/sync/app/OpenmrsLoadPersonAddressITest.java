package org.openmrs.sync.app;

import static org.junit.Assert.assertEquals;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.After;
import org.junit.Test;
import org.openmrs.sync.component.entity.PersonAddress;
import org.openmrs.sync.component.entity.light.UserLight;
import org.openmrs.sync.component.model.PersonAddressModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.security.PGPEncryptService;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenmrsLoadPersonAddressITest extends OpenmrsLoadEndpointITest {

    @Autowired
    private SyncEntityRepository<PersonAddress> repository;

    @Autowired
    private PGPEncryptService pgpEncryptService;

    @Test
    public void load() {
        // Given
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody("sender:openmrs-remote@icrc.org\n" + pgpEncryptService.encryptAndSign(getPersonJson()));

        // When
        template.send(exchange);

        // Then
        assertEquals(2, repository.findAll().size());
    }

    // TEAR-DOWN
    @After
    public void after() {
        PersonAddress p = repository.findByUuid("818b4ee6-8d68-4849-975d-80ab98016677");
        repository.delete(p);
    }

    private String getPersonJson() {
        return "{" +
                    "\"tableToSyncModelClass\":\"" + PersonAddressModel.class.getName() + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," +
                        "\"creatorUuid\":\"" + UserLight.class.getName() + "(1)\"," +
                        "\"dateCreated\":[2019,5,28,13,42,31]," +
                        "\"changedByUuid\":null," +
                        "\"dateChanged\":null," +
                        "\"voided\":false," +
                        "\"voidedByUuid\":null," +
                        "\"dateVoided\":null," +
                        "\"voidReason\":null," +
                        "\"address\":{" +
                            "\"address1\":\"chemin perdu\"" +
                        "}" +
                    "}" +
                "}";
    }
}
