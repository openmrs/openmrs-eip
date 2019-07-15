package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Exchange;
import org.bouncycastle.openpgp.PGPException;
import org.json.JSONException;
import org.junit.Test;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.security.NoSuchProviderException;
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
        assertEquals("openmrs-remote@icrc.org", result.get(0).getIn().getHeader("pgp_key_userId"));
        String json = pgpDecryptService.verifyAndDecrypt((String) result.get(0).getIn().getBody(), "openmrs-remote@icrc.org");
        JSONAssert.assertEquals(getExpectedJson(), json, false);
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
