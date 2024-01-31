package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = {
        "classpath:mgt_site_info.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SiteRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private SiteRepository repo;
	
	@Test
	public void findBySiteInfo_shouldGetTheStatusForTheSite() {
		Assert.assertEquals(2, repo.getByIdentifier("remote2").getId().intValue());
	}
	
}
