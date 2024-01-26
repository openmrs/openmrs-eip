package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverSyncRequestRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverSyncRequestRepository repo;
	
	@Test
	public void findByRequestUuid_shouldGetTheRequestMatchingTheSpecifiedRequestUuid() {
		Assert.assertEquals(4l, repo.findByRequestUuid("46beb8bd-287c-47f2-9786-a7b98c933c04").getId().longValue());
	}
	
}
