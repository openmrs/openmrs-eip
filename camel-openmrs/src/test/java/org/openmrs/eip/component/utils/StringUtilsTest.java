package org.openmrs.eip.component.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {
	
	@Test
	public void fromCamelCaseToSnakeCase_should_return_string() {
		// Given
		String stringToTest = "TestStringToConvert";
		
		// When
		String result = StringUtils.fromCamelCaseToSnakeCase(stringToTest);
		
		// Then
		assertEquals("TEST_STRING_TO_CONVERT", result);
	}
	
	@Test
	public void fromCamelCaseToSnakeCase_should_return_null() {
		// Given
		String stringToTest = null;
		
		// When
		String result = StringUtils.fromCamelCaseToSnakeCase(stringToTest);
		
		// Then
		assertNull(result);
	}
}
