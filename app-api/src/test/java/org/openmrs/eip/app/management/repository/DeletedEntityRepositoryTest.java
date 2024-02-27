package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class DeletedEntityRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private DeletedEntityRepository repo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_deleted_entity.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual_shouldGetMatchingDeletedEntities() throws Exception {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-02-27 00:00:01");
		List<DeletedEntity> deletes = repo.getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual("VISIT", date);
		Assert.assertEquals(2, deletes.size());
		Assert.assertEquals(2, deletes.get(0).getId().longValue());
		Assert.assertEquals(3, deletes.get(1).getId().longValue());
	}
	
}
