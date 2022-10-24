package org.openmrs.eip.component.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom {@link com.fasterxml.jackson.databind.JsonSerializer} from {@link LocalDate} to String
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> {
	
	public LocalDateSerializer() {
		this(LocalDate.class);
	}
	
	public LocalDateSerializer(Class<LocalDate> ldt) {
		super(ldt);
	}
	
	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
	}
	
}
