package org.openmrs.eip.app.publisher;

import static java.time.LocalDateTime.of;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.junit.Test;
import org.openmrs.eip.component.model.PersonModel;
import org.skyscreamer.jsonassert.JSONAssert;

public class OpenmrsExtractPersonITest extends OpenmrsExtractEndpointITest {

    private LocalDateTime date = of(1970, 1, 1, 0, 0, 0);

    @Test
    public void extract() throws JSONException {
        // Given
        CamelInitObect camelInitObect = CamelInitObect.builder()
                .tableToSync("PERSON")
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
                    "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"dd279794-76e9-11e9-8cd9-0242ac1c000b\"," +
                        "\"creatorUuid\":null," +
                        "\"dateCreated\":\"" + of(2005, 1, 1, 0, 0, 0).atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME) + "\"," +
                        "\"changedByUuid\":null," +
                        "\"dateChanged\":null," +
                        "\"voided\":false," +
                        "\"voidedByUuid\":null," +
                        "\"dateVoided\":null," +
                        "\"voidReason\":null," +
                        "\"gender\":\"M\"," +
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
