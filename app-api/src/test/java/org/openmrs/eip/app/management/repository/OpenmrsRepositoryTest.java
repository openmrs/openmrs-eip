package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.entity.PersonAttribute;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = { "classpath:test_data.sql", "classpath:openmrs_patient.sql" })
public class OpenmrsRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private OpenmrsRepository<PersonAttribute> repo;
	
	@Test
	public void findByUuid_shouldReturnTheEntityMatchingTheUuid() {
		Assert.assertEquals(2L, repo.findByUuid("2efd940e-32dc-491f-8038-a8f3afe3e35f").getId().longValue());
	}
	
	@Test
	public void findByUuid_shouldReturnNullIfNoTheEntityMatchesTheUuid() {
		Assert.assertNull(repo.findByUuid("some-uuid"));
	}
	
	@Test
	public void existsByUuid_shouldReturnTrueIfAnEntityMatchingTheUuid() {
		Assert.assertTrue(repo.existsByUuid("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
	@Test
	public void existsByUuid_shouldReturnTrueIfNoEntityMatchesTheUuid() {
		Assert.assertFalse(repo.existsByUuid("some-uuid"));
	}
	
	@Test
	public void countByUuidIn_shouldReturnTheCountOfEntitiesMatchingTheSpecifiedUuids() {
		final String uuid = "2efd940e-32dc-491f-8038-a8f3afe3e35f";
		List<String> uuids = List.of(uuid, "some-uuid", "4efd940e-32dc-491f-8038-a8f3afe3e35f", uuid);
		Assert.assertEquals(2, repo.countByUuidIn(uuids));
	}
	
}
