package org.openmrs.eip.deindentification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;

public class DeIdentifyClassMetadataBuilderTest extends BaseDbDrivenTest {
	
	private DeIdentifyClassMetadataBuilder builder = DeIdentifyClassMetadataBuilder.getInstance();
	
	private Field getField(String name) {
		return FieldUtils.getDeclaredField(MockEntity.class, name, true);
	}
	
	@Test
	public void build_shouldBuildTheMetadataForTheSpecifiedEntityClass() {
		Set<String> deIndentifyColumns = new HashSet();
		deIndentifyColumns.add("identifier");
		deIndentifyColumns.add("name");
		deIndentifyColumns.add("gender");
		deIndentifyColumns.add("birth_date");
		DeIdentifyClassMetadata metadata = builder.build(MockEntity.class, deIndentifyColumns);
		Field identifier = getField("identifier");
		Field name = getField("name");
		Field gender = getField("gender");
		Field birthdate = getField("birthdate");
		assertTrue(metadata.deIndentify(identifier));
		assertTrue(metadata.deIndentify(name));
		assertTrue(metadata.deIndentify(gender));
		assertTrue(metadata.deIndentify(birthdate));
		
		assertTrue(metadata.isRequired(identifier));
		assertTrue(metadata.isRequired(name));
		assertFalse(metadata.isRequired(gender));
		assertFalse(metadata.isRequired(birthdate));
		
		assertTrue(metadata.isUnique(identifier));
		assertFalse(metadata.isUnique(name));
		assertFalse(metadata.isUnique(gender));
		assertFalse(metadata.isUnique(birthdate));
		
		assertTrue(metadata.isUnique(identifier));
		assertEquals(255, metadata.getLength(identifier).intValue());
		assertEquals(255, metadata.getLength(name).intValue());
		assertEquals(1, metadata.getLength(gender).intValue());
		assertEquals(255, metadata.getLength(birthdate).intValue());
	}
	
}
