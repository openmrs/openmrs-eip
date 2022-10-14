package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBSYNC;

import java.util.Date;
import java.util.List;

import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.management.entity.SenderSyncMessage.SenderSyncMessageStatus;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.PatientModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.jayway.jsonpath.JsonPath;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBSYNC + "=DEBUG")
@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
public class DbSyncRouteTest extends BaseSenderRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return "db-sync-route";
	}
	
	@Test
	public void shouldProcessAnInsertEvent() {
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		DefaultExchange exchange = new DefaultExchange(camelContext);
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String op = "c";
		final boolean snapshot = true;
		DebeziumEvent debeziumEvent = createDebeziumEvent(table, null, uuid, op);
		debeziumEvent.setDateCreated(new Date());
		debeziumEvent.getEvent().setSnapshot(snapshot);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		List<SenderSyncMessage> syncMsgs = TestUtils.getEntities(SenderSyncMessage.class);
		assertEquals(1, syncMsgs.size());
		SenderSyncMessage msg = syncMsgs.get(0);
		assertEquals(table, msg.getTableName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertEquals(snapshot, msg.getSnapshot());
		assertEquals(SenderSyncMessageStatus.NEW, msg.getStatus());
		assertNotNull(msg.getMessageUuid());
		assertNotNull(msg.getDateCreated());
		assertEquals(msg.getEventDate().getTime(), debeziumEvent.getDateCreated().getTime());
		assertNull(msg.getRequestUuid());
		assertNull(msg.getDateSent());
		assertEquals(PatientModel.class.getName(), JsonPath.read(msg.getData(), "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(msg.getData(), "model.uuid"));
		assertEquals(op, JsonPath.read(msg.getData(), "metadata.operation"));
		assertEquals(msg.getMessageUuid(), JsonPath.read(msg.getData(), "metadata.messageUuid"));
		assertTrue(JsonPath.read(msg.getData(), "metadata.snapshot"));
		assertNull(JsonPath.read(msg.getData(), "metadata.sourceIdentifier"));
		assertNull(JsonPath.read(msg.getData(), "metadata.dateSent"));
		assertNull(JsonPath.read(msg.getData(), "metadata.requestUuid"));
	}
	
	@Test
	public void shouldProcessAnUpdateEvent() {
		Assert.fail();
	}
	
	@Test
	public void shouldProcessADeleteEvent() {
		Assert.fail();
	}
	
	@Test
	public void shouldProcessARetryItem() {
		Assert.fail();
	}
	
	@Test
	public void shouldProcessAnInsertOrUpdateEventAndTheEntityIsNotFound() {
		Assert.fail();
	}
	
	@Test
	public void shouldProcessASyncRequest() {
		Assert.fail();
	}
	
	@Test
	public void shouldProcessASyncRequestAndTheEntityIsNotFound() {
		Assert.fail();
	}
	
	@Test
	public void shouldFailIfNoDebeziumEventOrRetryItemIsFoundOnTheExchange() {
		Assert.fail();
	}
	
}
