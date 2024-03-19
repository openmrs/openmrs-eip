package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictRepository repo;
	
	@Test
	public void getConflictIds_shouldGetTheIdsOfAllTheConflicts() {
		List<Long> ids = repo.getConflictIds();
		Assert.assertEquals(5, ids.size());
		Assert.assertEquals(5, ids.get(0).longValue());
		Assert.assertEquals(1, ids.get(1).longValue());
		Assert.assertEquals(2, ids.get(2).longValue());
		Assert.assertEquals(3, ids.get(3).longValue());
		Assert.assertEquals(4, ids.get(4).longValue());
	}
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldGetTheIdsOfAllTheConflicts() {
		List<String> classes = Utils.getListOfModelClassHierarchy(PersonModel.class.getName());
		Assert.assertEquals(3, repo.countByIdentifierAndModelClassNameIn("uuid-1", classes));
		Assert.assertEquals(1, repo.countByIdentifierAndModelClassNameIn("uuid-2", classes));
	}
	
}
