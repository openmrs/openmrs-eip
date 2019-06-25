package org.openmrs.sync.core.utils;

import org.json.JSONException;
import org.junit.Test;
import org.openmrs.sync.core.camel.TransferObject;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    private static final String UUID = "UUID";

    @Test
    public void marshall() throws JSONException {
        // Given
        PersonModel model = new PersonModel();
        model.setUuid(UUID);
        TransferObject to = TransferObject.builder()
                .tableToSync(TableToSyncEnum.PERSON)
                .model(model)
                .build();

        // When
        String result = JsonUtils.marshall(to);

        // Then
        JSONAssert.assertEquals(json(), result, false);
    }

    @Test
    public void unmarshall() {
        // Given
        String json = json();

        // When
        TransferObject result = (TransferObject) JsonUtils.unmarshal(json);

        // Then
        PersonModel expectedModel = new PersonModel();
        expectedModel.setUuid(UUID);
        TransferObject expected = TransferObject.builder()
                .tableToSync(TableToSyncEnum.PERSON)
                .model(expectedModel)
                .build();
        assertEquals(expected, result);
    }

    private String json() {
        return "{" +
                    "\"tableToSync\":\"" + TableToSyncEnum.PERSON + "\"," +
                    "\"model\": {" +
                        "\"uuid\":\"" + UUID + "\"," +
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
                        "\"causeOfDeathClassUuid\":null," +
                        "\"causeOfDeathDatatypeUuid\":null," +
                        "\"deathdateEstimated\":false," +
                        "\"birthtime\":null" +
                    "}" +
                "}";
    }
}
