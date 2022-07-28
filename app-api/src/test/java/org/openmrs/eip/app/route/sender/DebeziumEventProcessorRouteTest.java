package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_IS_SUBCLASS;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBZM_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DB_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DB_EVENT_PROCESSOR;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@TestPropertySource(properties = "logging.level.debezium-event-processor=DEBUG")
public class DebeziumEventProcessorRouteTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_DB_EVENT_PROCESSOR)
	private MockEndpoint mockEventProcessorEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "debezium-event-processor-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockEventProcessorEndpoint.reset();
		advise(ROUTE_ID_DBZM_EVENT_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_DB_EVENT_PROCESSOR).skipSendToOriginalEndpoint().to(mockEventProcessorEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldProcessAnEventForAnEntityWithNoRetryItems() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long debeziumEventId = 1L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(1);
		mockEventProcessorEndpoint.expectedBodyReceived().body(Event.class).isEqualTo(event);
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_EVENT, event);
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_DBZM_EVENT, debeziumEvent);
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_IS_SUBCLASS, false);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
	@Test
	public void shouldFailForAnEventForAnEntityWithRetryItems() throws Exception {
		final String table = "person";
		final String id = "1";
		assertTrue(SenderTestUtils.hasRetryItem(table, id));
		Exchange exchange = new DefaultExchange(camelContext);
		DebeziumEvent debeziumEvent = createDebeziumEvent(table, id, null, "u");
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		Assert.assertEquals("Cannot process the event because the entity has 3 event(s) in the retry queue",
		    getErrorMessage(exchange));
	}
	
	@Test
	public void shouldProcessAnEventForASubclassEntity() throws Exception {
		final Long debeziumEventId = 3L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertTrue(Utils.isSubclassTable(event.getTableName()));
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(1);
		mockEventProcessorEndpoint.expectedBodyReceived().body(Event.class).isEqualTo(debeziumEvent.getEvent());
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_EVENT, debeziumEvent.getEvent());
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_DBZM_EVENT, debeziumEvent);
		mockEventProcessorEndpoint.expectedPropertyReceived(EX_PROP_IS_SUBCLASS, true);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
}
