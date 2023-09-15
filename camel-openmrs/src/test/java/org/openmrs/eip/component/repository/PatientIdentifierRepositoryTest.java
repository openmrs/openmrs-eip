package org.openmrs.eip.component.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = { "classpath:test_data.sql", "classpath:openmrs_patient.sql" })
public class PatientIdentifierRepositoryTest extends BaseDbDrivenTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Autowired
	private PatientIdentifierRepository repo;
	
	@Test
	public void getPatientIdentifierUuids_shouldReturnTheUuidsOfTheIdentifiersOfThePatientWithTheSpecifiedUuid() {
		List<String> nameUuids = repo.getPatientIdentifierUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1cfd940e-32dc-491f-8038-a8f3afe3e35c"));
		assertTrue(nameUuids.contains("2cfd940e-32dc-491f-8038-a8f3afe3e35c"));
	}
	
}
