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
import org.openmrs.eip.app.management.entity.SenderSyncRequest;
import org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.openmrs.eip.app.management.entity.SyncRequestModel;
import org.openmrs.eip.app.management.entity.SyncResponseModel;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = PROP_ACTIVEMQ_IN_ENDPOINT + "=" + URI)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ACTIVEMQ_CONSUMER + "=DEBUG")
public class SenderActiveMqConsumerRouteTest extends BaseSenderRouteTest {
	
	public static final String URI = "direct:" + ROUTE_ID_ACTIVEMQ_CONSUMER;
	
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
		Exchange exchange = new DefaultExchange(camelContext);
		SyncResponseModel responseData = new SyncResponseModel();
		responseData.setMessageUuid(messageUuid);
		responseData.setDateSentByReceiver(dateSent);
		exchange.getIn().setBody(JsonUtils.marshall(responseData));
		assertEquals(0, TestUtils.getEntities(SenderSyncResponse.class).size());
		
		producerTemplate.send(URI, exchange);
		
		List<SenderSyncResponse> responses = TestUtils.getEntities(SenderSyncResponse.class);
		assertEquals(1, responses.size());
		SenderSyncResponse savedResponse = responses.get(0);
		assertEquals(messageUuid, savedResponse.getMessageUuid());
		assertEquals(dateSent, savedResponse.getDateSentByReceiver());
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
	
}
