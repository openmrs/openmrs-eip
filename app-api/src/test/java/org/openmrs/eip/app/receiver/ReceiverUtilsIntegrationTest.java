package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverUtilsIntegrationTest extends BaseReceiverTest {
	
	@Test
	public void updateColumn_shouldUpdateTheColumnValueInTheDatabase() {
		final Long id = 1L;
		final String tableName = "site_info";
		final String columnName = "sync_disabled";
		Assert.assertEquals(false, TestUtils.getRowById(tableName, id).get(columnName));
		
		ReceiverUtils.updateColumn(tableName, columnName, id, true);
		
		Assert.assertEquals(true, TestUtils.getRowById(tableName, id).get(columnName));
	}
	
}
