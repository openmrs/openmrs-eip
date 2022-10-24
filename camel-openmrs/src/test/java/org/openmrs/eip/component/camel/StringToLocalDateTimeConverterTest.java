package org.openmrs.eip.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringToLocalDateTimeConverterTest {
	
	private StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();
	
	private Exchange exchange = new DefaultExchange(new DefaultCamelContext());
	
	@Test
	public void convertTo() {
		// Given
		String dateAsString = "2019-06-05 15:52:00";
		
		// When²²²
		LocalDateTime result = converter.convertTo(LocalDateTime.class, exchange, dateAsString);
		
		// Then
		assertEquals(LocalDateTime.of(2019, 6, 5, 15, 52), result);
	}
	
	@Test
	public void convertTo_null() {
		// Given
		String dateAsString = null;
		
		// When
		LocalDateTime result = converter.convertTo(LocalDateTime.class, new DefaultExchange(new DefaultCamelContext()),
		    dateAsString);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void convertTo_empty() {
		// Given
		String dateAsString = "";
		
		// When
		LocalDateTime result = converter.convertTo(LocalDateTime.class, new DefaultExchange(new DefaultCamelContext()),
		    dateAsString);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void allowNull() {
		assertTrue(converter.allowNull());
	}
}
