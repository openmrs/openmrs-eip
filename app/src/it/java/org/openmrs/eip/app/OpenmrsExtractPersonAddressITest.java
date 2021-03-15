package org.openmrs.eip.app;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.junit.Test;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OpenmrsExtractPersonAddressITest extends OpenmrsExtractEndpointITest {

    private LocalDateTime date = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    @Test
    public void extract() throws JSONException {
        // Given
        CamelInitObect camelInitObect = CamelInitObect.builder()
                .tableToSync("PERSON_ADDRESS")
                .lastSyncDate(date)
                .build();

        // When
        template.sendBody(camelInitObect);

        // Then
        List<Exchange> result = resultEndpoint.getExchanges();
        assertEquals(1, result.size());
        pgpDecryptService.process(result.get(0));
        JSONAssert.assertEquals(getExpectedJson(), (String) result.get(0).getIn().getBody(), false);
    }

    private String getExpectedJson() {
        return "{" +
                    "\"tableToSyncModelClass\":\"" + PersonAddressModel.class.getName() + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"uuid_person_address\"," +
                        "\"creatorUuid\":\"" + UserLight.class.getName() + "(user_uuid)\"," +
                        "\"dateCreated\":\"2005-01-01T00:00:00\"," +
                        "\"changedByUuid\":null," +
                        "\"dateChanged\":null," +
                        "\"voided\":false," +
                        "\"voidedByUuid\":null," +
                        "\"dateVoided\":null," +
                        "\"voidReason\":null," +
                        "\"address\":{" +
                            "\"address1\":\"chemin perdu\"," +
                            "\"cityVillage\":\"ville\"" +
                        "}" +
                    "}" +
                "}";
    }
}
