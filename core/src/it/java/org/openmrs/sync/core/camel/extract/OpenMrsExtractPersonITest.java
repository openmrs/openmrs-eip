package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.junit.Test;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OpenMrsExtractPersonITest extends OpenMrsExtractEndpointITest {

    private LocalDateTime date = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    @Test
    public void extract() throws JSONException {
        // Given
        CamelInitObect camelInitObect = CamelInitObect.builder()
                .tableToSync("person")
                .lastSyncDate(date)
                .build();

        // When
        template.sendBody(camelInitObect);

        // Then
        List<Exchange> result = resultEndpoint.getExchanges();
        assertEquals(1, result.size());
        List<String> jsons = (List<String>) result.get(0).getIn().getBody();
        assertEquals(1, jsons.size());
        JSONAssert.assertEquals(getExpectedJson(), jsons.get(0), false);
    }

    private String getExpectedJson() {
        return "{" +
                    "\"tableToSync\":\"" + TableToSyncEnum.PERSON + "\"," +
                    "\"model\":{" +
                        "\"uuid\":\"dd279794-76e9-11e9-8cd9-0242ac1c000b\"," +
                        "\"creatorUuid\":null," +
                        "\"dateCreated\":[2005,1,1,0,0]," +
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
