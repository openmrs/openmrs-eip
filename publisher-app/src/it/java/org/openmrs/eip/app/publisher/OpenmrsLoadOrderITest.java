package org.openmrs.eip.app.publisher;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.eip.component.entity.Order;
import org.openmrs.eip.component.entity.Patient;
import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.OrderTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.ProviderLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.security.PGPEncryptService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Ignore
public class OpenmrsLoadOrderITest extends OpenmrsLoadEndpointITest {

    @Autowired
    private SyncEntityRepository<Order> repo;

    @Autowired
    private PGPEncryptService pgpEncryptService;

    @Test
    public void load() {
        Patient p = new Patient();
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody("sender:openmrs-remote@icrc.org\n" + pgpEncryptService.encryptAndSign(getOrderJson()));

        template.send(exchange);

        assertEquals(1, repo.findAll().size());
    }

    // TEAR-DOWN
    @After
    public void after() {
        Order o = repo.findByUuid("918b4ee6-8d68-4849-975d-80ab98016677");
        repo.delete(o);
    }

    private String getOrderJson() {
        return "{" +
                "\"tableToSyncModelClass\":\"" + OrderModel.class.getName() + "\"," +
                "\"model\":{" +
                "\"uuid\":\"918b4ee6-8d68-4849-975d-80ab98016677\"," +
                "\"creatorUuid\":\"" + UserLight.class.getName() + "(1)\"," +
                "\"dateCreated\":[2019,5,28,13,42,31]," +
                "\"voided\":false," +
                "\"voidedByUuid\":null," +
                "\"dateVoided\":null," +
                "\"voidReason\":null," +
                "\"orderTypeUuid\":\"" + OrderTypeLight.class.getName() + "(1)\"," +
                "\"conceptUuid\":\"" + ConceptLight.class.getName() + "(1)\"," +
                "\"ordererUuid\":\"" + ProviderLight.class.getName() + "(1)\"," +
                "\"encounterUuid\":\"" + EncounterLight.class.getName() + "(1)\"," +
                "\"patientUuid\":\"" + PatientLight.class.getName() + "(dd279794-76e9-11e9-8cd9-0242ac1c000b)\"," +
                "\"careSettingUuid\":\"" + CareSettingLight.class.getName() + "(1)\"," +
                "\"orderNumber\":null," +
                "\"action\":null" +
                "}" +
                "}";
    }

}
