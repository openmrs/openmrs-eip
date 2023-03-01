package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({ "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
public class SearchIndexUpdatingProcessorIntegrationTest extends BaseReceiverTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Autowired
	private SearchIndexUpdatingProcessor processor;
	
	@Test
	public void getPersonNameUuids_shouldReturnTheUuidsOfTheNamesOfThePersonWithTheSpecifiedUuid() {
		List<String> nameUuids = processor.getPersonNameUuids(PERSON_UUID);
		
		Assert.assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1bfd940e-32dc-491f-8038-a8f3afe3e35a"));
		assertTrue(nameUuids.contains("2bfd940e-32dc-491f-8038-a8f3afe3e35a"));
	}
	
	@Test
	public void getPatientIdentifierUuids_shouldReturnTheUuidsOfTheIdentifiersOfThePatientWithTheSpecifiedUuid() {
		List<String> nameUuids = processor.getPatientIdentifierUuids(PERSON_UUID);
		
		Assert.assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1cfd940e-32dc-491f-8038-a8f3afe3e35c"));
		assertTrue(nameUuids.contains("2cfd940e-32dc-491f-8038-a8f3afe3e35c"));
	}
	
}
