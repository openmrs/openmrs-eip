package org.openmrs.sync.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

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
            throw new RuntimeException(e);
        }
    }

    public static Object unmarshal(final String json,
                                   final String className) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(json, Class.forName(className));
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while unmarshalling object", e);
            throw new RuntimeException(e);
        }
    }
}
