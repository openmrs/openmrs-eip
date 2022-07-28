package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBSYNC;

import java.util.List;

import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.entity.Event;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBSYNC + "=DEBUG")
public class DbSyncRouteTest extends BaseSenderRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return "db-sync-route";
	}
	
	@Test
	public void shouldCreateAndSaveTheSenderSyncMessageForTheEvent() {
		final int count = TestUtils.getEntities(SenderSyncMessage.class).size();
		DefaultExchange exchange = new DefaultExchange(camelContext);
		final String table = "visit";
		final String uuid = "some-uuid";
		final String op = "c";
		final String requestUuid = "some-request-uuid";
		final boolean snapshot = true;
		Event event = createEvent(table, null, uuid, op);
		event.setSnapshot(snapshot);
		event.setRequestUuid(requestUuid);
		exchange.setProperty(EX_PROP_EVENT, event);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		List<SenderSyncMessage> syncMsgs = TestUtils.getEntities(SenderSyncMessage.class);
		assertEquals(count + 1, syncMsgs.size());
		SenderSyncMessage msg = syncMsgs.get(syncMsgs.size() - 1);
		assertEquals(table, msg.getTableName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertEquals(snapshot, msg.isSnapshot());
		assertEquals(requestUuid, msg.getRequestUuid());
		assertNotNull(msg.getDateCreated());
		assertNotNull(msg.getMessageUuid());
	}
	
}
