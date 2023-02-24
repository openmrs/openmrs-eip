package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_synced_msg.sql",
        "classpath:mgt_receiver_post_sync_action.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class PostSyncActionRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private PostSyncActionRepository repo;
	
	@Test
	public void getBatchOfPendingResponseActions_shouldReturnABatchOfUnProcessedResponseActions() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		
		List<PostSyncAction> actions = repo.getBatchOfPendingResponseActions(site, page);
		
		assertEquals(2, actions.size());
		assertEquals(1l, actions.get(0).getId().longValue());
		assertEquals(7l, actions.get(1).getId().longValue());
		
	}
	
}
