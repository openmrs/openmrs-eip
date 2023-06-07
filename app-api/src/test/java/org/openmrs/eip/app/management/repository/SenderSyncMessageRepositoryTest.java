package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderSyncMessageRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncMessageRepository repo;
	
	@Test
	public void deleteByMessageUuid_shouldDeleteTheMessageWithTheGivenMessageUuid() {
		final String msgUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		SenderSyncMessage msg = repo.findById(4L).get();
		Assert.assertEquals(msgUuid, msg.getMessageUuid());
		
		repo.deleteByMessageUuid(msgUuid);
		
		Assert.assertFalse(repo.findById(1L).isPresent());
	}
	
}
