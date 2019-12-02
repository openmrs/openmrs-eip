package org.openmrs.utils.odoo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;


public class IdDeserializer extends JsonDeserializer<Integer> {

    /**
     *
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     */
    @Override
    public Integer deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        Object value = jsonParser.readValueAs(Object.class);
        if (value instanceof ArrayList) {
            return (Integer) ((ArrayList) value).get(0);
        } else if (value instanceof Boolean) {
            return null;
        }
        return null;
    }
}
