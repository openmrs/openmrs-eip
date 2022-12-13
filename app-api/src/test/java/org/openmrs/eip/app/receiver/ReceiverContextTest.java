package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverContextTest extends BaseReceiverTest {
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getSiteNameAndInfoMap_shouldReturnAllSiteInfo() {
		Whitebox.setInternalState(ReceiverContext.class, "siteNameAndInfoMap", (Object) null);
		Assert.assertNull(Whitebox.getInternalState(ReceiverContext.class, "siteNameAndInfoMap"));
		Assert.assertEquals(4, ReceiverContext.getSiteNameAndInfoMap().size());
	}
	
}
