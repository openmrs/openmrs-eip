package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = {
        "classpath:mgt_sender_deleted_entity.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class DeletedEntityRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private DeletedEntityRepository repo;
	
	@Test
	public void getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual_shouldGetMatchingDeletedEntities() throws Exception {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-02-27 00:00:01");
		List<DeletedEntity> deletes = repo.getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual("VISIT", date);
		assertEquals(2, deletes.size());
		assertEquals(2, deletes.get(0).getId().longValue());
		assertEquals(3, deletes.get(1).getId().longValue());
	}
	
	@Test
	public void getByTableNameIgnoreCase_shouldGetMatchingDeletedEntities() {
		List<DeletedEntity> deletes = repo.getByTableNameIgnoreCase("VISIT");
		assertEquals(3, deletes.size());
		assertEquals(1, deletes.get(0).getId().longValue());
		assertEquals(2, deletes.get(1).getId().longValue());
		assertEquals(3, deletes.get(2).getId().longValue());
	}
	
	@Test
	public void getByTableNameIgnoreCaseAndPrimaryKeyId_shouldGetMatchingDeletedEntity() {
		assertEquals(5, repo.getByTableNameIgnoreCaseAndPrimaryKeyId("PATIENT", "1").getId().longValue());
	}
	
}
