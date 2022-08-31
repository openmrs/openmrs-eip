package org.openmrs.eip.web.receiver;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverSyncMessageControllerTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverSyncMessageController controller;
	
	@Test
	public void shouldGetAllSyncMessages() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(3, result.get("count"));
		assertEquals(3, ((List) result.get("items")).size());
	}
	
	@Test
	public void shouldGetTheSyncMessageMatchingTheSpecifiedId() {
		assertEquals("27beb8bd-287c-47f2-9786-a7b98c933c05", ((SyncMessage) controller.get(2)).getMessageUuid());
	}
	
}
