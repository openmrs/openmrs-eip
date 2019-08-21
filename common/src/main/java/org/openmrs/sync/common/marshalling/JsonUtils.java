package org.openmrs.sync.common.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.common.model.sync.BaseModel;

import java.io.IOException;

@Slf4j
public final class JsonUtils {

    private JsonUtils() {}

    public static String marshall(final Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.writeValueAsString(object).replaceAll("^\"|\"$|\\\\", "");
        } catch (JsonProcessingException e) {
            log.error("Error while marshalling object", e);
            throw new MarshallingException("Error while marshalling object", e);
        }
    }

    public static <C> C unmarshal(final String json,
                                      final Class<C> objectClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(BaseModel.class, new BaseModelDeserializer());
            mapper.registerModule(module);
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(json, objectClass);
        } catch (IOException e) {
            log.error("Error while unmarshalling object", e);
            throw new MarshallingException("Error while unmarshalling object", e);
        }
    }
}
