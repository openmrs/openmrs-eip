package org.openmrs.sync.core.utils;

import org.junit.Test;
import org.openmrs.sync.core.model.PersonModel;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    @Test
    public void marshall() {
        // Given
        PersonModel model = new PersonModel();
        model.setUuid("uuid");

        // When
        String result = JsonUtils.marshall(model);

        // Then
        assertEquals(json("uuid"), result);
    }

    @Test
    public void unmarshall() {
        // Given
        String json = json("uuid");

        // When
        PersonModel result = (PersonModel) JsonUtils.unmarshal(json, PersonModel.class.getName());

        // Then
        PersonModel expected = new PersonModel();
        expected.setUuid("uuid");
        assertEquals(expected, result);
    }

    private String json(final String uuid) {
        return "{\"uuid\":\"" + uuid + "\"," +
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
                "}";
    }
}
