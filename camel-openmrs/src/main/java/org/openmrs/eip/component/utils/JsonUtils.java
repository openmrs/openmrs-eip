package org.openmrs.eip.component.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.openmrs.eip.component.exception.OpenmrsSyncException;
import org.openmrs.eip.component.model.BaseModel;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public final class JsonUtils {

    private JsonUtils() {}

    /**
     * Utility method to marshal an object to JSON
     * @param object
     * @return the object as a JSON string
     */
    public static String marshall(final Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(new LocalDateTimeSerializer());
            mapper.registerModule(module);

            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error while marshalling object", e);
            throw new OpenmrsSyncException("Error while marshalling object", e);
        }
    }

    /**
     * Utility method to unmarshal a JSON string
     * @param json
     * @param objectClass
     * @param <C>
     * @return the object
     */
    public static <C> C unmarshal(final String json,
                                  final Class<C> objectClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(BaseModel.class, new BaseModelDeserializer());
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            mapper.registerModule(module);

            return mapper.readValue(json, objectClass);
        } catch (IOException e) {
            log.error("Error while unmarshalling object", e);
            throw new OpenmrsSyncException("Error while unmarshalling object", e);
        }
    }
}
