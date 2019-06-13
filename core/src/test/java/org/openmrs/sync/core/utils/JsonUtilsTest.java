package org.openmrs.sync.core.utils;

import org.junit.Test;
import org.openmrs.sync.core.model.MockedModel;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    private static final String UUID = "UUID";

    @Test
    public void marshall() {
        // Given
        MockedModel model = new MockedModel(UUID);
        model.setUuid(UUID);

        // When
        String result = JsonUtils.marshall(model);

        // Then
        assertEquals(json(), result);
    }

    @Test
    public void unmarshall() {
        // Given
        String json = json();

        // When
        MockedModel result = (MockedModel) JsonUtils.unmarshal(json, MockedModel.class.getName());

        // Then
        MockedModel expected = new MockedModel(UUID);
        expected.setUuid(UUID);
        assertEquals(expected, result);
    }

    private String json() {
        return "{\"uuid\":\"UUID\"," +
                "\"creatorUuid\":null," +
                "\"dateCreated\":null," +
                "\"changedByUuid\":null," +
                "\"dateChanged\":null," +
                "\"voided\":false," +
                "\"voidedByUuid\":null," +
                "\"dateVoided\":null," +
                "\"voidReason\":null}";
    }
}
