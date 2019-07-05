package org.openmrs.sync.core.camel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class BaseModelDeserializerTest {

    private ObjectMapper mapper;
    private BaseModelDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new BaseModelDeserializer();
    }

    @Test
    public void deserialize() {
        // Given
        String json = personJson();

        // When
        TransferObject deserialisedJson = deserializeJson(json);

        // Then
        assertNotNull(deserialisedJson);
    }

    @SneakyThrows({JsonParseException.class, IOException.class})
    private TransferObject deserializeJson(String json) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BaseModel.class, deserializer);
        mapper.registerModule(module);

        return mapper.readValue(json, TransferObject.class);
    }

    private String personJson() {
        return "{" +
                    "\"tableToSync\": \"" + TableToSyncEnum.PERSON + "\"," +
                    "\"model\": {" +
                        "\"uuid\":\"personUuid\"," +
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
}
