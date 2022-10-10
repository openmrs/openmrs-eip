package org.openmrs.eip.web.sender;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderSyncMessageControllerTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncMessageController controller;
	
	@Test
	public void shouldGetAllSyncMessages() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(4, result.get("count"));
		assertEquals(4, ((List) result.get("items")).size());
	}
	
	@Test
	public void shouldGetTheSyncMessageMatchingTheSpecifiedId() {
		assertEquals("36beb8bd-287c-47f2-9786-a7b98c933c04", ((SenderSyncMessage) controller.get(3L)).getMessageUuid());
	}
	
}
