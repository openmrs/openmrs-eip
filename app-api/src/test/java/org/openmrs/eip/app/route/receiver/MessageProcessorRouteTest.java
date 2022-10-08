package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_SYNC_MESSAGE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_INBOUND_DB_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_MSG_PROCESSOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_INBOUND_DB_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.time.LocalDateTime;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class MessageProcessorRouteTest extends BaseReceiverRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_INBOUND_DB_SYNC)
	private MockEndpoint mockInboundDbSyncEndpoint;
	
	@Before
	public void setup() throws Exception {
		mockInboundDbSyncEndpoint.reset();
		advise(ROUTE_ID_MSG_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_INBOUND_DB_SYNC).skipSendToOriginalEndpoint().to(mockInboundDbSyncEndpoint);
			}
			
		});
	}
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_MSG_PROCESSOR;
	}
	
	@Test
	public void shouldFailIfTheEntityHasAnItemInTheErrorQueue() throws Exception {
		SyncMessage message = new SyncMessage();
		message.setModelClassName(PersonModel.class.getName());
		message.setIdentifier("uuid-1");
		message.setEntityPayload("{}");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(message);
		mockInboundDbSyncEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockInboundDbSyncEndpoint.assertIsSatisfied();
		assertEquals("Cannot process the message because the entity has 3 message(s) in the retry queue",
		    getErrorMessage(exchange));
		assertEquals(message, exchange.getProperty(EX_PROP_SYNC_MESSAGE));
	}
	
	@Test
	public void shouldFailForASubclassEntityWhenTheParentHasAnItemInTheErrorQueue() throws Exception {
		SyncMessage message = new SyncMessage();
		message.setModelClassName(PatientModel.class.getName());
		message.setIdentifier("uuid-1");
		message.setEntityPayload("{}");
		final LocalDateTime dateSentBySender = LocalDateTime.now();
		message.setDateSentBySender(dateSentBySender);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(message);
		mockInboundDbSyncEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockInboundDbSyncEndpoint.assertIsSatisfied();
		assertEquals("Cannot process the message because the entity has 3 message(s) in the retry queue",
		    getErrorMessage(exchange));
		assertEquals(message, exchange.getProperty(EX_PROP_SYNC_MESSAGE));
	}
	
	@Test
	public void shouldProcessAMessageForAnEntityWithNoRetryItems() throws Exception {
		final String modelClass = PersonModel.class.getName();
		final String entityId = "uuid-3";
		final String payload = "{}";
		final SiteInfo site = new SiteInfo();
		SyncMessage message = new SyncMessage();
		message.setModelClassName(modelClass);
		message.setIdentifier(entityId);
		message.setEntityPayload(payload);
		message.setSite(site);
		final LocalDateTime dateSentBySender = LocalDateTime.now();
		message.setDateSentBySender(dateSentBySender);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(message);
		mockInboundDbSyncEndpoint.expectedMessageCount(1);
		mockInboundDbSyncEndpoint.expectedBodyReceived().body(SyncModel.class).isNotNull();
		mockInboundDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_MODEL_CLASS, modelClass);
		mockInboundDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_ENTITY_ID, entityId);
		mockInboundDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_PAYLOAD, payload);
		mockInboundDbSyncEndpoint.expectedPropertyReceived(EX_PROP_SYNC_MESSAGE, message);
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockInboundDbSyncEndpoint.assertIsSatisfied();
	}
	
}
