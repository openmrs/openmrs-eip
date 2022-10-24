package org.openmrs.eip.component.utils;

import org.junit.Test;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.mapper.operations.DecomposedUuid;
import org.openmrs.eip.component.model.PersonModel;

import java.util.Optional;

import static org.junit.Assert.*;

public class ModelUtilsTest {
	
	@Test
	public void decomposeUuid_should_return_a_decomposed_uuid() {
		// Given
		String uuid = PersonLight.class.getName() + "(uuid)";
		
		// When
		Optional<DecomposedUuid> result = ModelUtils.decomposeUuid(uuid);
		
		// Then
		assertTrue(result.isPresent());
		assertEquals(PersonLight.class, result.get().getEntityType());
		assertEquals("uuid", result.get().getUuid());
	}
	
	@Test
	public void decomposeUuid_should_return_a_empty_optional() {
		// Given
		String uuid = null;
		
		// When
		Optional<DecomposedUuid> result = ModelUtils.decomposeUuid(uuid);
		
		// Then
		assertFalse(result.isPresent());
	}
	
	@Test
	public void extractUuid_should_extract_uuid() {
		// Given
		String body = "{" + "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\","
		        + "\"model\": {\"personUuid\":\"" + PersonLight.class.getName() + "(uuid)\"}" + "}";
		
		// When
		String result = ModelUtils.extractUuid(body, "personUuid");
		
		// Then
		assertEquals("uuid", result);
	}
}
