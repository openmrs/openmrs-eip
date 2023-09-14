package org.openmrs.eip.component.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = { "classpath:test_data.sql", "classpath:openmrs_patient.sql" })
public class PersonNameRepositoryTest extends BaseDbDrivenTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Autowired
	private PersonNameRepository repo;
	
	@Test
	public void getPersonNameUuids_shouldReturnTheUuidsOfTheNamesOfThePersonWithTheSpecifiedUuid() {
		List<String> nameUuids = repo.getPersonNameUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1bfd940e-32dc-491f-8038-a8f3afe3e35a"));
		assertTrue(nameUuids.contains("2bfd940e-32dc-491f-8038-a8f3afe3e35a"));
	}
	
}
