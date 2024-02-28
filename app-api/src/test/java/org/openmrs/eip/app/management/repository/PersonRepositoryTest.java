package org.openmrs.eip.app.management.repository;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.component.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

public class PersonRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private PersonRepository repo;
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void getUuid_shouldGetThePersonUuid() {
		Assert.assertEquals("abfd940e-32dc-491f-8038-a8f3afe3e35b", repo.getUuid(101L));
	}
	
}
