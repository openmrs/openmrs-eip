package org.openmrs.eip.deindentification;

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
		assertTrue(metadata.deIndentify(getField("identifier")));
		assertTrue(metadata.deIndentify(getField("name")));
		assertTrue(metadata.deIndentify(getField("gender")));
		assertTrue(metadata.deIndentify(getField("birthdate")));
		
		assertTrue(metadata.isRequired(getField("identifier")));
		assertTrue(metadata.isRequired(getField("name")));
		assertFalse(metadata.isRequired(getField("gender")));
		assertFalse(metadata.isRequired(getField("birthdate")));
	}
	
}
