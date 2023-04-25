package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_status.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SiteSyncStatusRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private SiteSyncStatusRepository repo;
	
	@Test
	public void findBySiteInfo_shouldGetTheStatusForTheSite() {
		Assert.assertEquals(1, repo.findBySiteInfo(TestUtils.getEntity(SiteInfo.class, 1L)).getId().intValue());
	}
	
	@Test
	public void findBySiteInfo_shouldReturnIfTheStatusHasExistingStatus() {
		Assert.assertNull(repo.findBySiteInfo(TestUtils.getEntity(SiteInfo.class, 3L)));
	}
	
}
