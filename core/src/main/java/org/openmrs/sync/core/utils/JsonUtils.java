package org.openmrs.sync.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.camel.BaseModelDeserializer;
import org.openmrs.sync.core.camel.TransferObject;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;

import java.io.IOException;

@Slf4j
public final class JsonUtils {

    private JsonUtils() {}

    public static String marshall(final TransferObject object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.writeValueAsString(object).replaceAll("^\"|\"$|\\\\", "");
        } catch (JsonProcessingException e) {
            log.error("Error while marshalling object", e);
            throw new OpenMrsSyncException("Error while marshalling object", e);
        }
    }

    public static TransferObject unmarshal(final String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(BaseModel.class, new BaseModelDeserializer());
            mapper.registerModule(module);
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(json, TransferObject.class);
        } catch (IOException e) {
            log.error("Error while unmarshalling object", e);
            throw new OpenMrsSyncException("Error while unmarshalling object", e);
        }
    }
}
