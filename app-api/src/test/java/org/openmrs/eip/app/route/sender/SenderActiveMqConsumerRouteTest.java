package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.route.sender.SenderActiveMqConsumerRouteTest.URI;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_ACTIVEMQ_IN_ENDPOINT;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_ACTIVEMQ_CONSUMER;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.CustomMessageListenerContainer;
import org.openmrs.eip.app.management.entity.ReconciliationRequest;
import org.openmrs.eip.app.management.entity.SyncRequestModel;
import org.openmrs.eip.app.management.entity.SyncResponseModel;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderSyncRequest;
import org.openmrs.eip.app.management.entity.sender.SenderSyncRequest.SenderRequestStatus;
import org.openmrs.eip.app.management.entity.sender.SenderSyncResponse;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = PROP_ACTIVEMQ_IN_ENDPOINT + "=" + URI)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ACTIVEMQ_CONSUMER + "=DEBUG")
public class SenderActiveMqConsumerRouteTest extends BaseSenderRouteTest {
	
	public static final String URI = "direct:" + ROUTE_ID_ACTIVEMQ_CONSUMER;
	
	@Autowired
	private SenderReconcileRepository recRepo;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(CustomMessageListenerContainer.class, "commit", false);
	}
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_ACTIVEMQ_CONSUMER;
	}
	
	@Test
	public void shouldProcessAndSaveASyncRequestMessage() {
		final String table = "visit";
		final String uuid = "entity-uuid";
		final String requestUuid = "sync-request-uuid";
		Exchange exchange = new DefaultExchange(camelContext);
		SyncRequestModel requestData = new SyncRequestModel();
		requestData.setTableName(table);
		requestData.setIdentifier(uuid);
		requestData.setRequestUuid(requestUuid);
		exchange.getIn().setBody(JsonUtils.marshall(requestData));
		assertEquals(0, TestUtils.getEntities(SenderSyncRequest.class).size());
		
		producerTemplate.send(URI, exchange);
		
		List<SenderSyncRequest> requests = TestUtils.getEntities(SenderSyncRequest.class);
		assertEquals(1, requests.size());
		SenderSyncRequest savedRequest = requests.get(0);
		assertEquals(table, savedRequest.getTableName());
		assertEquals(uuid, savedRequest.getIdentifier());
		assertEquals(requestUuid, savedRequest.getRequestUuid());
		assertEquals(SenderRequestStatus.NEW, savedRequest.getStatus());
		assertFalse(savedRequest.getFound());
		assertNotNull(savedRequest.getDateCreated());
		assertNull(savedRequest.getDateProcessed());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldProcessAndSaveASyncResponseMessage() {
		final String messageUuid = "message-uuid";
		final LocalDateTime dateSent = LocalDateTime.now();
		final LocalDateTime dateReceived = LocalDateTime.now();
		Exchange exchange = new DefaultExchange(camelContext);
		SyncResponseModel responseData = new SyncResponseModel();
		responseData.setMessageUuid(messageUuid);
		responseData.setDateSentByReceiver(dateSent);
		responseData.setDateReceived(dateReceived);
		exchange.getIn().setBody(JsonUtils.marshall(responseData));
		assertEquals(0, TestUtils.getEntities(SenderSyncResponse.class).size());
		
		producerTemplate.send(URI, exchange);
		
		List<SenderSyncResponse> responses = TestUtils.getEntities(SenderSyncResponse.class);
		assertEquals(1, responses.size());
		SenderSyncResponse savedResponse = responses.get(0);
		assertEquals(messageUuid, savedResponse.getMessageUuid());
		assertEquals(dateSent, savedResponse.getDateSentByReceiver());
		assertEquals(dateReceived, savedResponse.getDateReceivedByReceiver());
		assertNotNull(savedResponse.getDateCreated());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldSkipAMessageThatIsNotASyncRequestOrResponse() {
		final String testMsg = "{}";
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(testMsg);
		
		producerTemplate.send(URI, exchange);
		
		assertMessageLogged(Level.WARN, "Unknown message was received: " + testMsg);
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldProcessAndSaveAReconcileRequest() {
		assertEquals(0, recRepo.count());
		final String identifier = "test";
		final int batchSize = 10;
		Exchange exchange = new DefaultExchange(camelContext);
		ReconciliationRequest request = new ReconciliationRequest();
		request.setIdentifier(identifier);
		request.setBatchSize(batchSize);
		exchange.getIn().setBody(JsonUtils.marshall(request));
		final long timestamp = System.currentTimeMillis();
		
		producerTemplate.send(URI, exchange);
		
		List<SenderReconciliation> recs = recRepo.findAll();
		assertEquals(1, recs.size());
		SenderReconciliation rec = recs.get(0);
		assertEquals(identifier, rec.getIdentifier());
		assertEquals(batchSize, rec.getBatchSize());
		assertEquals(SenderReconcileStatus.NEW, rec.getStatus());
		assertTrue(rec.getDateCreated().getTime() == timestamp || rec.getDateCreated().getTime() > timestamp);
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
}
