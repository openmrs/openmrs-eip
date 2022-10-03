package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_INBOUND_DB_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_MSG_PROCESSOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_INBOUND_DB_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.SyncMessage.ReceiverSyncMessageStatus;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_receiver_retry_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_MSG_PROCESSOR + "=DEBUG")
public class MessageProcessorRouteTest extends BaseReceiverRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_INBOUND_DB_SYNC)
	private MockEndpoint mockDbSyncEndpoint;
	
	@Before
	public void setup() throws Exception {
		mockDbSyncEndpoint.reset();
		advise(ROUTE_ID_MSG_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_INBOUND_DB_SYNC).skipSendToOriginalEndpoint().to(mockDbSyncEndpoint);
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
		mockDbSyncEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertEquals("Cannot process the message because the entity has 3 message(s) in the retry queue",
		    getErrorMessage(exchange));
	}
	
	@Test
	public void shouldFailForASubclassEntityWhenTheParentHasAnItemInTheErrorQueue() throws Exception {
		SyncMessage message = new SyncMessage();
		message.setModelClassName(PatientModel.class.getName());
		message.setIdentifier("uuid-1");
		message.setEntityPayload("{}");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(message);
		mockDbSyncEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertEquals("Cannot process the message because the entity has 3 message(s) in the retry queue",
		    getErrorMessage(exchange));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldProcessAMessageForAnEntityWithNoRetryItems() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		SyncMessage msg = TestUtils.getEntity(SyncMessage.class, 2L);
		assertEquals(ReceiverSyncMessageStatus.NEW, msg.getStatus());
		exchange.getIn().setBody(msg);
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodyReceived().body(SyncModel.class).isNotNull();
		mockDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_MODEL_CLASS, msg.getModelClassName());
		mockDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_ENTITY_ID, msg.getIdentifier());
		mockDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_PAYLOAD, msg.getEntityPayload());
		mockDbSyncEndpoint.expectedPropertyReceived(ReceiverConstants.EX_PROP_SITE, msg.getSite());
		
		producerTemplate.send(URI_MSG_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertEquals(ReceiverSyncMessageStatus.PROCESSED, TestUtils.getEntity(SyncMessage.class, 2L).getStatus());
	}
	
}
