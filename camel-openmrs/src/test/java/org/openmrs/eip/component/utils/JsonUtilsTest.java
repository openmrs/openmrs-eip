package org.openmrs.eip.component.utils;

import org.json.JSONException;
import org.junit.Test;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {
    private static final String UUID = "UUID";

    @Test
    public void marshall_should_produce_json() throws JSONException {
        // Given
        PersonModel model = new PersonModel();
        model.setUuid(UUID);
        SyncModel to = SyncModel.builder()
                .tableToSyncModelClass(PersonModel.class)
                .model(model)
                .build();

        // When
        String result = JsonUtils.marshall(to);

        // Then
        JSONAssert.assertEquals(json(), result, false);
    }

    @Test(expected = EIPException.class)
    public void marshall_should_throw_exception() {
        // Given
        WrongModel model = new WrongModel();
        model.setUuid(UUID);
        SyncModel to = SyncModel.builder()
                .tableToSyncModelClass(PersonModel.class)
                .model(model)
                .build();

        // When
        JsonUtils.marshall(to);

        // Then
    }

    @Test
    public void unmarshall() {
        // Given
        String json = json();

        // When
        SyncModel result = JsonUtils.unmarshal(json, SyncModel.class);

        // Then
        PersonModel expectedModel = new PersonModel();
        expectedModel.setUuid(UUID);
        SyncModel expected = SyncModel.builder()
                .tableToSyncModelClass(PersonModel.class)
                .model(expectedModel)
                .build();
        assertEquals(expected.getModel().getUuid(), result.getModel().getUuid());
    }

    @Test(expected = EIPException.class)
    public void unmarshall_should_throw_exception() {
        // Given
        String json = badlyFormattedJson();

        // When
        JsonUtils.unmarshal(json, SyncModel.class);

        // Then
    }

    private String badlyFormattedJson() {
        return "{" +
                "tableToSyncModelClass:" + PersonModel.class.getName() + "," +
                "model: {" +
                "uuid:" + UUID + "," +
                "creatorUuid:null," +
                "dateCreated:null," +
                "changedByUuid:null," +
                "dateChanged:null," +
                "voided:false," +
                "voidedByUuid:null," +
                "dateVoided:null," +
                "voidReason:null," +
                "gender:null," +
                "birthdate:null," +
                "birthdateEstimated:false," +
                "dead:false," +
                "deathDate:null," +
                "causeOfDeathUuid:null," +
                "deathdateEstimated:false," +
                "birthtime:null" +
                "}" +
                "}";
    }

    private String json() {
        return "{" +
                "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\"," +
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
                "\"deathdateEstimated\":false," +
                "\"birthtime\":null" +
                "}" +
                "}";
    }

    private class WrongModel extends PersonModel {

        @Override
        public String getGender() {
            throw new RuntimeException();
        }
    }
}
