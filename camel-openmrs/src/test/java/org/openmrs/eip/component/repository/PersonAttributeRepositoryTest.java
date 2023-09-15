package org.openmrs.eip.component.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = { "classpath:test_data.sql", "classpath:openmrs_patient.sql" })
public class PersonAttributeRepositoryTest extends BaseDbDrivenTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Autowired
	private PersonAttributeRepository repo;
	
	@Test
	public void getPersonAttributeUuids_shouldReturnTheUuidsOfTheSearchableAttributesOfThePersonWithTheSpecifiedUuid() {
		List<String> attributeUuids = repo.getPersonAttributeUuids(PERSON_UUID);
		
		assertEquals(2, attributeUuids.size());
		assertTrue(attributeUuids.contains("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
		assertTrue(attributeUuids.contains("4efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
}
