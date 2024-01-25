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
        "classpath:mgt_receiver_jms_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class JmsMessageRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private JmsMessageRepository repo;
	
	@Test
	public void existsByMessageId_shouldReturnTrueIfAMessageWithTheSameIdExists() {
		Assert.assertTrue(repo.existsByMessageId("1cef940e-32dc-491f-8038-a8f3afe3e37d"));
	}
	
	@Test
	public void existsByMessageId_shouldReturnFalseIfNoMessageWithTheSameIdExists() {
		Assert.assertFalse(repo.existsByMessageId("some-uuid"));
	}
	
}
