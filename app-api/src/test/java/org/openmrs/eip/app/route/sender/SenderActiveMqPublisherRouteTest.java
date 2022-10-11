package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.management.entity.SenderSyncMessage.SenderSyncMessageStatus.SENT;
import static org.openmrs.eip.app.route.sender.SenderActiveMqPublisherRouteTest.SENDER_ID;
import static org.openmrs.eip.app.route.sender.SenderActiveMqPublisherRouteTest.URI_ACTIVEMQ_SYNC;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_SENDER_ID;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_ACTIVEMQ_PUBLISHER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_ACTIVEMQ_PUBLISHER;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.jayway.jsonpath.JsonPath;

import ch.qos.logback.classic.Level;

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
	
	private SenderSyncMessage createSyncMessage(String table, String identifier, String msgUuid, String op,
	                                            String requestUuid) {
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(table);
		msg.setIdentifier(identifier);
		msg.setMessageUuid(msgUuid);
		msg.setOperation(op);
		msg.setSnapshot(false);
		msg.setDateCreated(new Date());
		msg.setRequestUuid(requestUuid);
		msg.setEventDate(new Date());
		TestUtils.saveEntity(msg);
		return msg;
	}
	
	@Test
	public void shouldProcessADeleteEvent() throws Exception {
		final String table = "person";
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final String op = "d";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, msgUuid, op, null);
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
		assertEquals(false, JsonPath.read(syncMsg, "metadata.snapshot"));
		assertNotNull(JsonPath.read(syncMsg, "metadata.dateSent"));
		assertNull(JsonPath.read(syncMsg, "metadata.requestUuid"));
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void shouldProcessAnInsertEvent() throws Exception {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String msgUuid = "msg-uuid";
		final String op = "c";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, msgUuid, op, null);
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
		assertEquals(PatientModel.class.getName(), JsonPath.read(syncMsg, "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(syncMsg, "model.uuid"));
		assertEquals(SENDER_ID, JsonPath.read(syncMsg, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncMsg, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncMsg, "metadata.messageUuid"));
		assertEquals(false, JsonPath.read(syncMsg, "metadata.snapshot"));
		assertNotNull(JsonPath.read(syncMsg, "metadata.dateSent"));
		assertNull(JsonPath.read(syncMsg, "metadata.requestUuid"));
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void shouldProcessAnUpdateEvent() throws Exception {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String msgUuid = "msg-uuid";
		final String op = "u";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, msgUuid, op, null);
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
		assertEquals(PatientModel.class.getName(), JsonPath.read(syncMsg, "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(syncMsg, "model.uuid"));
		assertEquals(SENDER_ID, JsonPath.read(syncMsg, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncMsg, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncMsg, "metadata.messageUuid"));
		assertEquals(false, JsonPath.read(syncMsg, "metadata.snapshot"));
		assertNotNull(JsonPath.read(syncMsg, "metadata.dateSent"));
		assertNull(JsonPath.read(syncMsg, "metadata.requestUuid"));
	}
	
	@Test
	public void shouldProcessAnInsertOrUpdateEventAndTheEntityIsNotFound() throws Exception {
		final String table = "patient";
		final String uuid = "person-uuid";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, "msg-uuid", "u", null);
		assertEquals(1, TestUtils.getEntities(SenderSyncMessage.class).size());
		mockActiveMqEndpoint.expectedMessageCount(0);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(msg);
		
		producerTemplate.send(URI_ACTIVEMQ_PUBLISHER, exchange);
		
		mockActiveMqEndpoint.assertIsSatisfied();
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		assertMessageLogged(Level.INFO,
		    "No entity found in the database matching identifier " + uuid + " in table " + table);
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void shouldProcessASyncRequest() throws Exception {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String msgUuid = "msg-uuid";
		final String requestUuid = "request-uuid";
		final String op = "r";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, msgUuid, op, requestUuid);
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
		assertEquals(PatientModel.class.getName(), JsonPath.read(syncMsg, "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(syncMsg, "model.uuid"));
		assertEquals(SENDER_ID, JsonPath.read(syncMsg, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncMsg, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncMsg, "metadata.messageUuid"));
		assertEquals(false, JsonPath.read(syncMsg, "metadata.snapshot"));
		assertNotNull(JsonPath.read(syncMsg, "metadata.dateSent"));
		assertEquals(requestUuid, JsonPath.read(syncMsg, "metadata.requestUuid"));
	}
	
	@Test
	public void shouldProcessASyncRequestAndTheEntityIsNotFound() throws Exception {
		final String table = "patient";
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final String requestUuid = "request-uuid";
		final String op = "r";
		assertTrue(TestUtils.getEntities(SenderSyncMessage.class).isEmpty());
		SenderSyncMessage msg = createSyncMessage(table, uuid, msgUuid, op, requestUuid);
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
		assertNull(JsonPath.read(syncMsg, "tableToSyncModelClass"));
		assertNull(uuid, JsonPath.read(syncMsg, "model"));
		assertEquals(SENDER_ID, JsonPath.read(syncMsg, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncMsg, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncMsg, "metadata.messageUuid"));
		assertEquals(false, JsonPath.read(syncMsg, "metadata.snapshot"));
		assertNotNull(JsonPath.read(syncMsg, "metadata.dateSent"));
		assertEquals(requestUuid, JsonPath.read(syncMsg, "metadata.requestUuid"));
		assertMessageLogged(Level.INFO, "Entity not found for request with uuid: " + requestUuid);
	}
	
}
