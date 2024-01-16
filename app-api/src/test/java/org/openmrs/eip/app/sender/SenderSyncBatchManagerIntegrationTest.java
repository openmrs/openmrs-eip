package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.sender.SenderSyncMessage.SenderSyncMessageStatus.NEW;
import static org.openmrs.eip.app.management.entity.sender.SenderSyncMessage.SenderSyncMessageStatus.SENT;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@TestPropertySource(properties = SenderConstants.PROP_SENDER_ID + "=test_site")
@TestPropertySource(properties = SenderConstants.PROP_ACTIVEMQ_ENDPOINT + "=activemq:openmrs.test")
public class SenderSyncBatchManagerIntegrationTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncBatchManager manager;
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void updateItems_shouldUpdateTheMessagesToMarkThemAsSent() {
		final Long id1 = 1L;
		final Long id3 = 3L;
		final String table = "sender_sync_message";
		assertEquals(NEW.name(), TestUtils.getRowById(table, id1).get("status"));
		assertNull(TestUtils.getRowById(table, id1).get("date_sent"));
		assertEquals(NEW.name(), TestUtils.getRowById(table, id3).get("status"));
		assertNull(TestUtils.getRowById(table, id3).get("date_sent"));
		long timestamp = System.currentTimeMillis();
		
		manager.updateItems(List.of(id1, id3));
		
		assertEquals(SENT.name(), TestUtils.getRowById(table, id1).get("status"));
		Date dateSent1 = (Date) TestUtils.getRowById(table, id1).get("date_sent");
		assertTrue(dateSent1.getTime() == timestamp || dateSent1.getTime() > timestamp);
		assertEquals(SENT.name(), TestUtils.getRowById(table, id3).get("status"));
		Date dateSent3 = (Date) TestUtils.getRowById(table, id3).get("date_sent");
		assertTrue(dateSent3.getTime() == timestamp || dateSent3.getTime() > timestamp);
	}
	
}
