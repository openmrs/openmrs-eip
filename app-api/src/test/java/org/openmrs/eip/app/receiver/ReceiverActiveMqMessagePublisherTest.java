package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_CAMEL_OUTPUT_ENDPOINT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.jayway.jsonpath.JsonPath;

@TestPropertySource(properties = PROP_CAMEL_OUTPUT_ENDPOINT + "="
        + ReceiverActiveMqMessagePublisherTest.URI_ACTIVEMQ_RESPONSE_PREFIX + "{0}")
public class ReceiverActiveMqMessagePublisherTest extends BaseReceiverTest {
	
	public static final String URI_ACTIVEMQ_RESPONSE_PREFIX = "mock:response.";
	
	public static final String SENDER_ID = "test-sender-id";
	
	@Autowired
	ReceiverActiveMqMessagePublisher publisher;
	
	@EndpointInject(URI_ACTIVEMQ_RESPONSE_PREFIX + SENDER_ID)
	private MockEndpoint mockActiveMqEndpoint;
	
	@Before
	public void setup() {
		mockActiveMqEndpoint.reset();
	}
	
	@Test
	public void sendSyncResponse_shouldGenerateAndSendAResponseForASyncMessage() throws Exception {
		final String msgUuid = "message-uuid";
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(SENDER_ID);
		SyncMessage message = new SyncMessage();
		message.setMessageUuid(msgUuid);
		message.setSite(siteInfo);
		message.setDateCreated(new Date());
		mockActiveMqEndpoint.expectedMessageCount(1);
		List<String> responses = new ArrayList();
		mockActiveMqEndpoint.whenAnyExchangeReceived(e -> responses.add(e.getIn().getBody(String.class)));
		
		publisher.sendSyncResponse(message);
		
		mockActiveMqEndpoint.assertIsSatisfied();
		assertEquals(1, responses.size());
		assertEquals(msgUuid, JsonPath.read(responses.get(0), "messageUuid"));
		assertNotNull(JsonPath.read(responses.get(0), "dateSentByReceiver"));
		assertNotNull(JsonPath.read(responses.get(0), "dateReceived"));
	}
	
	@Test
	public void sendSyncResponse_shouldGenerateAndSendAResponseForASyncRequest() throws Exception {
		final String msgUuid = "message-uuid";
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(SENDER_ID);
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		request.setSite(siteInfo);
		mockActiveMqEndpoint.expectedMessageCount(1);
		List<String> responses = new ArrayList();
		mockActiveMqEndpoint.whenAnyExchangeReceived(e -> responses.add(e.getIn().getBody(String.class)));
		
		publisher.sendSyncResponse(request, msgUuid);
		
		mockActiveMqEndpoint.assertIsSatisfied();
		assertEquals(1, responses.size());
		assertEquals(msgUuid, JsonPath.read(responses.get(0), "messageUuid"));
		assertNotNull(JsonPath.read(responses.get(0), "dateSentByReceiver"));
		assertNotNull(JsonPath.read(responses.get(0), "dateReceived"));
	}
	
}
