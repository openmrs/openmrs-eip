package org.openmrs.eip.component.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;
import org.openmrs.eip.component.entity.PersonAttribute;
import org.openmrs.eip.component.entity.PersonName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = { "classpath:test_data.sql", "classpath:openmrs_patient.sql" })
public class OpenmrsRepositoryTest extends BaseDbDrivenTest {
	
	@Autowired
	private OpenmrsRepository<PersonAttribute> attribRepo;
	
	@Autowired
	private OpenmrsRepository<PersonName> nameRepo;
	
	@Test
	public void findByUuid_shouldReturnTheEntityMatchingTheUuid() {
		assertEquals(2L, attribRepo.findByUuid("2efd940e-32dc-491f-8038-a8f3afe3e35f").getId().longValue());
	}
	
	@Test
	public void findByUuid_shouldReturnNullIfNoTheEntityMatchesTheUuid() {
		Assert.assertNull(attribRepo.findByUuid("some-uuid"));
	}
	
	@Test
	public void existsByUuid_shouldReturnTrueIfAnEntityMatchingTheUuid() {
		Assert.assertTrue(attribRepo.existsByUuid("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
	@Test
	public void existsByUuid_shouldReturnTrueIfNoEntityMatchesTheUuid() {
		Assert.assertFalse(attribRepo.existsByUuid("some-uuid"));
	}
	
	@Test
	public void countByUuidIn_shouldReturnTheCountOfEntitiesMatchingTheSpecifiedUuids() {
		final String uuid = "2efd940e-32dc-491f-8038-a8f3afe3e35f";
		List<String> uuids = List.of(uuid, "some-uuid", "4efd940e-32dc-491f-8038-a8f3afe3e35f", uuid);
		assertEquals(2, attribRepo.countByUuidIn(uuids));
	}
	
	@Test
	public void getMaxId_shouldReturnMaximumRowId() {
		assertEquals(4l, attribRepo.getMaxId().longValue());
		assertEquals(2l, nameRepo.getMaxId().longValue());
	}
	
	@Test
	public void getMaxId_shouldGetTheNextRowId() {
		assertEquals(3l, attribRepo.getNextId(2L).longValue());
	}
	
	@Test
	public void getCountWithIdGreaterThan_shouldGetTheCountOfRowsWithIdsGreaterThanSpecifiedId() {
		assertEquals(3, attribRepo.getCountWithIdGreaterThan(1L).longValue());
		assertEquals(2, attribRepo.getCountWithIdGreaterThan(2L).longValue());
	}
	
	@Test
	public void getUuidBatchToReconcile_shouldGetTheBatchOfUuidsMatchingTheSpecifiedRange() {
		List<String> uuids = attribRepo.getUuidBatchToReconcile(1L, 3L, Pageable.ofSize(5));
		assertEquals(2, uuids.size());
		assertTrue(uuids.contains("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
		assertTrue(uuids.contains("3efd940e-32dc-491f-8038-a8f3afe3e35f"));
		
		uuids = attribRepo.getUuidBatchToReconcile(1L, 3L, Pageable.ofSize(1));
		assertEquals(1, uuids.size());
		assertTrue(uuids.contains("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
}
