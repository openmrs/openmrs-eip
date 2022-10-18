package org.openmrs.eip.app.route.sender;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.management.entity.SenderSyncMessage.SenderSyncMessageStatus.SENT;
import static org.openmrs.eip.app.route.sender.SenderActiveMqPublisherRouteTest.SENDER_ID;
import static org.openmrs.eip.app.route.sender.SenderActiveMqPublisherRouteTest.URI_ACTIVEMQ_SYNC;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_SENDER_ID;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_ACTIVEMQ_PUBLISHER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_ACTIVEMQ_PUBLISHER;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.test.context.TestPropertySource;

import com.jayway.jsonpath.JsonPath;

@TestPropertySource(properties = PROP_SENDER_ID + "=" + SENDER_ID)
@TestPropertySource(properties = "camel.output.endpoint=" + URI_ACTIVEMQ_SYNC)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ACTIVEMQ_PUBLISHER + "=DEBUG")
public class SenderActiveMqPublisherRouteTest extends BaseSenderRouteTest {
	
	public static final String URI_ACTIVEMQ_SYNC = "mock:activemq.openmrs.sync";
	
	public static final String SENDER_ID = "test-sender-id";
	
	@EndpointInject(URI_ACTIVEMQ_SYNC)
	private MockEndpoint mockActiveMqEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_ACTIVEMQ_PUBLISHER;
	}
	
	@Before
	public void setup() {
		mockActiveMqEndpoint.reset();
	}
	
	@Test
	public void shouldSubmitTheSyncMessageToActiveMq() throws Exception {
		final String table = "patient";
		final String uuid = "patient-uuid";
		final String msgUuid = "msg-uuid";
		final String op = "u";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(table);
		msg.setIdentifier(uuid);
		msg.setMessageUuid(msgUuid);
		msg.setOperation(op);
		msg.setSnapshot(true);
		msg.setDateCreated(new Date());
		msg.setEventDate(new Date());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation(op);
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(true);
		PersonModel model = new PatientModel();
		model.setUuid(uuid);
		msg.setData(JsonUtils.marshall(new SyncModel(PersonModel.class, model, metadata)));
		TestUtils.saveEntity(msg);
		assertEquals(1, TestUtils.getEntities(SenderSyncMessage.class).size());
		mockActiveMqEndpoint.expectedMessageCount(1);
		final List<String> syncMessages = new ArrayList();
		mockActiveMqEndpoint.whenAnyExchangeReceived(e -> syncMessages.add(e.getIn().getBody(String.class)));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(msg);
		
		producerTemplate.send(URI_ACTIVEMQ_PUBLISHER, exchange);
		
		mockActiveMqEndpoint.assertIsSatisfied();
		assertEquals(1, TestUtils.getEntities(SenderSyncMessage.class).size());
		assertEquals(SENT, TestUtils.getEntity(SenderSyncMessage.class, msg.getId()).getStatus());
		assertEquals(1, syncMessages.size());
		
		String syncMsg = syncMessages.get(0);
		assertEquals(PersonModel.class.getName(), JsonPath.read(syncMsg, "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(syncMsg, "model.uuid"));
		assertEquals(SENDER_ID, JsonPath.read(syncMsg, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncMsg, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncMsg, "metadata.messageUuid"));
		assertTrue(JsonPath.read(syncMsg, "metadata.snapshot"));
		LocalDateTime dateSent = LocalDateTime.ofInstant(msg.getDateSent().toInstant(), systemDefault());
		assertEquals(dateSent, ZonedDateTime.parse(JsonPath.read(syncMsg, "metadata.dateSent"), ISO_OFFSET_DATE_TIME)
		        .withZoneSameInstant(systemDefault()).toLocalDateTime());
		assertNull(JsonPath.read(syncMsg, "metadata.requestUuid"));
	}
	
}
