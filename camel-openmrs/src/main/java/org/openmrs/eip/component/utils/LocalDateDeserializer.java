package org.openmrs.eip.component.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom {@link com.fasterxml.jackson.databind.JsonDeserializer} from String {@link LocalDate}
 */
public class LocalDateDeserializer extends StdDeserializer<LocalDate> {
	
	public LocalDateDeserializer() {
		this(LocalDate.class);
	}
	
	public LocalDateDeserializer(Class<LocalDate> ldt) {
		super(ldt);
	}
	
	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return LocalDate.parse(p.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
}
