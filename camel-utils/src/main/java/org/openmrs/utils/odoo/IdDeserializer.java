package org.openmrs.utils.odoo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;


public class IdDeserializer extends JsonDeserializer<Integer> {

    /**
     * Deserializes an Odoo id composed of an array with the id at position 0 and the name of the object at position 1
     * If there is no id for the given field, then the value false is present and null is returned
     * @param jsonParser the parser
     * @param deserializationContext the context
     * @return the integer value of the id or null
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
