package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderSyncMessageRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncMessageRepository repo;
	
	@Test
	public void deleteByMessageUuid_shouldDeleteTheMessageWithTheGivenMessageUuid() {
		final String msgUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		final Long id = 4L;
		SenderSyncMessage msg = repo.findById(id).get();
		assertEquals(msgUuid, msg.getMessageUuid());
		
		repo.deleteByMessageUuid(msgUuid);
		
		Assert.assertFalse(repo.findById(id).isPresent());
	}
	
	@Test
	public void getNewSyncMessages_shouldGetAllNewSyncMessages() {
		List<SenderSyncMessage> messages = repo.getNewSyncMessages(Pageable.ofSize(5));
		assertEquals(3, messages.size());
		assertEquals(3, messages.get(0).getId().intValue());
		assertEquals(1, messages.get(1).getId().intValue());
		assertEquals(2, messages.get(2).getId().intValue());
		
		messages = repo.getNewSyncMessages(Pageable.ofSize(2));
		
		assertEquals(2, messages.size());
		assertEquals(3, messages.get(0).getId().intValue());
		assertEquals(1, messages.get(1).getId().intValue());
	}
	
}
