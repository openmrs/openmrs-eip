package org.openmrs.eip.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.support.TypeConverterSupport;
import org.openmrs.eip.component.utils.DateUtils;

public class StringToLocalDateTimeConverter extends TypeConverterSupport {
	
	@Override
	public <T> T convertTo(final Class<T> type, final Exchange exchange, final Object value) {
		if (value == null) {
			return null;
		}
		String valueAsString = (String) value;
		if (valueAsString.isEmpty()) {
			return null;
		}
		return (T) DateUtils.stringToDate(valueAsString);
	}
	
	@Override
	public boolean allowNull() {
		return true;
	}
}
