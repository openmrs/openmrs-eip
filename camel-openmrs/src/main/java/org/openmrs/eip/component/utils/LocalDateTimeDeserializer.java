package org.openmrs.eip.component.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom {@link com.fasterxml.jackson.databind.JsonDeserializer} from String  {@link LocalDateTime}
 */
public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public LocalDateTimeDeserializer() {
        this(LocalDateTime.class);
    }

    public LocalDateTimeDeserializer(Class<LocalDateTime> ldt) {
        super(ldt);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ZonedDateTime.parse(p.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }

}
